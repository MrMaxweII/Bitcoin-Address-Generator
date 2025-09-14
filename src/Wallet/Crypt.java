package Wallet;
import java.security.SecureRandom;
import java.util.Arrays;
import BTClib3001.AES256;
import BTClib3001.Calc;
import BTClib3001.ScryptHash;
import BTClib3001.Twofish;



/***************************************************************************************************************
* Version 1.2                          Author: Mr. Nickolas-Antoine B.                          19.09.2023                *
*                                                                                                              *
* Last change (v1.2):                                                                                          *
* Removed dependency on Bouncycastle!                                                                          *
* ParanoidCrypt (from KeyPass 1.2.x) implemented without Bouncycastle.                                         *
* AES, Twofish, Scrypt, SHA256 classes were added in BTClib3001.                                               *
*                                                                                                              *
* Static class providing various encryption helper methods.                                                    *
* Main methods paranoidEncrypt() and paranoidDecrypt() perform the encryption/decryption.                      *
* See block diagram "ParanoidCrypt.png"                                                                        *
* Used to encrypt CAGWallet.dat of the Coin Address Generator with maximum security.                           *
* Note: This class is tailored for CoinAddressGen; scrypt parameters can differ from other versions!           *
****************************************************************************************************************/



public class Crypt 
{
	


/** Encrypts data with two algorithms in sequence, AES then Twofish.
 Padding to 16, add SHA256 checksum, key duplication, scrypt delay, random salt per cipher.
 @param text plaintext as byte[]
 @param masterKey encryption key as byte[]
 @return ciphertext **/
public static byte[] paranoidEncrypt(byte[] text, byte[] masterKey) throws Exception
{
	text = padding16(text);
	text = addSHA256Checksum(text);
	byte[][] keys = keyDuplication(masterKey,2);
	byte[] ch = encrypt(text, keys[0] , "AES");
	return encrypt(ch, getScryptHash(keys[1]), "Twofish");		
}
	


/** Decrypts data with Twofish then AES in sequence.
 Key duplication, scrypt delay, random salt per cipher.
 @param chiffre ciphertext
 @param masterKey key
 @return plaintext **/
public static byte[] paranoidDecrypt(byte[] chiffre, byte[] masterKey) throws Exception
{
	byte[][] keys = keyDuplication(masterKey,2);
	byte[] ch =  decrypt(chiffre, getScryptHash(keys[1]), "Twofish");		
	return decrypt(ch, keys[0] , "AES");
}



	
/** Generates multiple isolated (SHA256) keys from a master key. See keyDuplication. **/
public static byte[][] keyDuplication(byte[] masterKey, int count)
{
	byte[] mKey = Calc.getHashSHA256(masterKey);
	byte[] kn = mKey;
	byte[][] out = new byte[count][32];
	for(int i=0;i<count;i++)
	{
		kn = Calc.getHashSHA256(kn);
		byte[] k = new byte[64];
		System.arraycopy(kn, 0, k, 0, 32);
		System.arraycopy(mKey, 0, k, 32, 32);
		out[i] = Calc.getHashSHA256(k);	
	}
	return out;
}
	
	

/** Encrypt using the named algorithm ("AES" or "Twofish"). Returns IV||ciphertext. **/	
public static byte[] encrypt(byte[] text, byte[] key, String name) throws Exception
{	
	byte[] iv = new byte[16];
	SecureRandom.getInstanceStrong().nextBytes(iv);
	byte[] ch=null;
	if(name.equals("AES")) 		ch = AES256.encryptCBC(text, key, iv);	
	if(name.equals("Twofish"))	ch = Twofish.encrypt(text, key, iv);
	if(ch==null) throw new Exception("Crypt Name unknown");
	byte[] out = Arrays.copyOf(iv, ch.length+16);
	System.arraycopy(ch, 0, out, 16, ch.length);
	return out;
}



/** Decrypt using the named algorithm ("AES" or "Twofish"). Expects IV||ciphertext input. **/	
public static byte[] decrypt(byte[] chiffre, byte[] key, String name) throws Exception
{ 
	byte[] iv = Arrays.copyOf(chiffre, 16);
	chiffre = Arrays.copyOfRange(chiffre, 16, chiffre.length);
	if(name.equals("AES"))		return AES256.decryptCBC(chiffre, key, iv);
	if(name.equals("Twofish"))	return Twofish.decrypt(chiffre, key, iv);
	throw new Exception("Crypt Name unknown");
}




/** Removes the leading SHA256 checksum and returns [hash,data,okFlag]. okFlag=1 if checksum matches. **/
public static byte[][] removeAndCheckSHA256Checksum(byte[] data)
{
	byte[][] out = new byte[3][];
	out[0] = new byte[32];
	out[1] = new byte[data.length-32];
	out[2] = new byte[1];	
	System.arraycopy(data, 0, out[0], 0, 32);
	System.arraycopy(data, 32, out[1], 0, data.length-32);
	byte[] hash = Calc.getHashSHA256(out[1]);
	if(Arrays.equals(out[0], hash)) out[2][0] = 1;
	else 							out[2][0] = 0;	
	return out;
}





//----------------------------------------------------- Private Methoden -------------------------------------------------------------------



/** Generates an scrypt hash used for runtime delay (~1GB RAM, ~5s). **/
private static byte[] getScryptHash(byte[] data) throws Exception
{
	final int p			= 1;		// Parallelisierungsparameter. ist immer 1 
	final byte[] salt 	= {0x00};   // Salz wird hier nicht benötigt.
	final int memory 	= 524288;	// (N) CPU/Speicherkostenparameter in Byte. Muss größer als 1 sein und eine Potenz von 2 und kleiner als 2^(128*r/8)  Standart = 2^14, Hier verwendet: 2^19
	final int r 		= 16;		// Die Blockgröße muss >= 1 sein. Hier wird 16 verwendet;
	final int outLen 	= 32;		// Die Länge des Ausgabe Hash.
	return ScryptHash.getHash(data, salt, memory, r, p, outLen);
}



/** Prepends SHA256(data) to data to allow integrity check after decrypt. **/
private static byte[] addSHA256Checksum(byte[] data)
{
	byte[] hash = Calc.getHashSHA256(data);
	byte[] out  = Arrays.copyOf(hash, data.length+32);	
	System.arraycopy(data, 0, out, 32, data.length);
	return out;
}



// Pads the input to a multiple of 16 bytes with zeros at the end.
private static byte[] padding16(byte[] input)
{
	int m = 16 - input.length%16;
	if(m==16) m=0;
	return Arrays.copyOf(input, input.length+m);
}
}