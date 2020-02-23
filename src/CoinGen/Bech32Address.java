package CoinGen;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;



/*******************************************************************************************************
* 	Diese statische Klasse Codiert eine Hash160-Adresse zu einer Bech32-Adresse mit "bc" beginnent  	*
* 	Achtung: Der Hash160 muss aus einem comprimierten Public-Key stammen! (2 oder 3 am Anfang)			*
* 	Zum Testen:		https://slowli.github.io/bech32-buffer/												*
*******************************************************************************************************/




public class Bech32Address
{
	private static final String	ALPHABET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
	private static final int[] 	GENERATOR = {0x3B6A57B2, 0x26508E6D, 0x1EA119FA, 0x3D4233DD, 0x2A1462B3};	
	private static final int 	CHECKSUM_LEN = 6;
	

/**	Codiert eine Hash160-Adresse zu einer Bech32-Adresse mit "bc" beginnent
	@param bech32Prefix Das Präfix für die resultierende Zeichenfolge bei BTC = "bc"
	@param version Witness Versionsnummer. Bei Bitcoin = 0;
	@param hash160 Die rohen Zeugendaten, oder die Hash160 Adresse.
	@return Gibt die fertige Bech32-Adresse als Text-String zurück. **/
public static String segwitToBech32(String bech32Prefix, int version, byte[] hash160) 
{
	Objects.requireNonNull(bech32Prefix);
	Objects.requireNonNull(hash160);
	if(version < 0 || version > 16) 				throw new IllegalArgumentException("Invalid witness version");
	if(hash160.length < 2 || hash160.length > 40)	throw new IllegalArgumentException("Invalid witness data length");
	ByteArrayOutputStream data = new ByteArrayOutputStream();  
	assert(version >>> 5) == 0;
	data.write(version);  
	int inputIndex = 0;
	int bitBuffer = 0; 
	int bitBufferLen = 0;  
	while(inputIndex < hash160.length || bitBufferLen > 0) 
	{
		assert 0 <= bitBufferLen && bitBufferLen <= 8 + 5 - 1;
		assert(bitBuffer << bitBufferLen) == 0;	
		if(bitBufferLen < 5) 
		{
			if(inputIndex < hash160.length) 
			{  
				bitBuffer |= (hash160[inputIndex] & 0xFF) << (32 - 8 - bitBufferLen);
				inputIndex++;
				bitBufferLen += 8;
			} 
			else bitBufferLen = 5;
		}
		assert bitBufferLen >= 5;
		data.write(bitBuffer >>> (32 - 5));
		bitBuffer <<= 5;
		bitBufferLen -= 5;
	}
	return bitGroupsToBech32(bech32Prefix, data.toByteArray());
}




// ------------------------------------------------------------- Private Methoden ----------------------------------
	

private static String bitGroupsToBech32(String bech32Prefix, byte[] data) 
{
	Objects.requireNonNull(bech32Prefix);
	Objects.requireNonNull(data);
	char[] human = bech32Prefix.toCharArray();
	checkHumanReadablePart(human);
	for(byte b : data) {if ((b >>> 5) != 0)		throw new IllegalArgumentException("Expected 5-bit groups");}
	if(human.length + 1 + data.length + 6 > 90) throw new IllegalArgumentException("Output too long");	
	int checksum;
	try 
	{
		ByteArrayOutputStream temp = expandHumanReadablePart(human); 
		temp.write(data);
		temp.write(new byte[CHECKSUM_LEN]);
		checksum = polymod(temp.toByteArray()) ^ 1;
	} 
	catch(IOException e) {throw new AssertionError(e);}	
	StringBuilder sb = new StringBuilder(bech32Prefix).append('1');
	for(byte b : data) sb.append(ALPHABET.charAt(b));
	for(int i = 0; i < CHECKSUM_LEN; i++) 
	{
		int b = (checksum >>> ((CHECKSUM_LEN - 1 - i) * 5)) & 0x1F; 
		sb.append(ALPHABET.charAt(b));
	}
	return sb.toString();
}
	

	
private static void checkHumanReadablePart(char[] s) 
{
	int n = s.length;
	if(n<1 || n>83) 			throw new IllegalArgumentException("Invalid length of human-readable part string");
	for(char c : s) 
	{ 
		if(c<33 || c>126) 		throw new IllegalArgumentException("Invalid character in human-readable part string");
		if('A'<=c && c<='Z')	throw new IllegalArgumentException("Human-readable part string must be lowercase");
	}
}
	

	
private static ByteArrayOutputStream expandHumanReadablePart(char[] s) 
{
	ByteArrayOutputStream result = new ByteArrayOutputStream();
	for(char c : s) result.write(c >>> 5);
	result.write(0);
	for(char c : s) result.write(c & 0x1F);
	return result;
}
	

	
private static int polymod(byte[] data) 
{
	int result = 1;
	for(byte b : data) 
	{
		assert 0<=b && b<32;
		int x = result >>> 25;
		result = ((result & ((1 << 25) - 1)) << 5) | b;
		for(int i = 0; i < GENERATOR.length; i++)	result ^= ((x >>> i) & 1) * GENERATOR[i];
		assert(result >>> 30) == 0;
	}
	return result;
}
}