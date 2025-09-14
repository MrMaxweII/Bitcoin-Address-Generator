package BTClib3001;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;



/************************************************************************************************************
*	Version 1.11   					 Autor: Mr. Nickolas-Antoine B.  				 	vom 02.02.2024					*
*	letzte Änderung:	valueToHex();  Fehler behoben und getestet.											*
*	Hier werden verschiedene Konvertierungen vorgenommen.													*
************************************************************************************************************/



public class Convert
{


	
/**	Decodes known magic values from 4-byte array to string.  
	@param MAGIC 4Byte MAGIC value
	@return String that describes the network of the magic value.
	Unknown MAGIC values are returned as "Unknown network". **/
public static String decodeMAGIC(byte[] MAGIC)
{
	if(MAGIC==null) return "No MAGIC value was entered";
	if(MAGIC.length!=4) return "No 4 bytes were passed";
	final byte[] MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
	final byte[] TESTNET = {(byte) 0xfa,(byte) 0xbf,(byte) 0xb5,(byte) 0xda};
	final byte[] TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};
	if(Arrays.equals(MAGIC,MAINNET)) return "Bitcoin-MainNet";
	if(Arrays.equals(MAGIC,TESTNET)) return "Bitcoin-TestNet";
	if(Arrays.equals(MAGIC,TESTNET3))return "Bitcoin-TestNet3";
	return "Unknown network";
}



/**	Wandelt ein Byte in Boolean um.
	@param b Wenn das Byte nicht 0x00 ist wird true zurück gegeben. **/	
public static boolean byteToBool(byte b) 
{
	return (b != 0x00) ;
}



/**	Wandelt ein Boolean in Byte um.
	@param bool Wenn bool = false wird, 0x00 zurück gegeben.
	Wenn bool = true, wird 0xff zurück gegeben. **/
public static byte boolToByte(boolean bool) 
{	
	if(bool) return (byte)0xff;
	else return 0x00;
}


	
/**	Hex-String wird in ein Byte Array konvertiert. 
	@param hex Es dürfen nur Zeichen zwichen [0-f] oder [0-F] mit einer geraden Anzahl übergeben werden.
	@return Byte-Array welches dem Hex-String entspricht.
	@exception IllegalArgumentException bei ungerader Zeichenfolge!**/
public static byte[] hexStringToByteArray(String hex)
{                                                                     
	if((hex.length()%2)==1) throw new IllegalArgumentException("Ungerade String-Zeichenfolge!");
	int l = hex.length();
	byte[] data = new byte[l/2];
	for (int i = 0; i < l; i += 2) 
	{
		data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
	}
	return data;
}



/**	Hex-String wird in ein Byte Array konvertiert. Dabei werden hier auch ungerade Zeichenfolgen akzepiert!
	Ist die Länge ungerade, wird eine 0 voranngestellt: Beispeiel 123 ---> 0123
	@param hex Zeichen zwichen [0-f] oder [0-F] mit einer geraden oder ungeraden Anzahl.
	@return Byte-Array welches dem Hex-String entspricht.	**/
public static byte[] hexStringToByteArray_oddLength(String hex) 
{                                                                     
	if((hex.length()%2)==1) hex = "0"+hex;	
	int l = hex.length();
	byte[] data = new byte[l/2];
	for (int i = 0; i < l; i += 2) 
	{
		data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
	}
	return data;
}


  
/** Byte Array wird in einen Hexa String konvertiert. */
public static String byteArrayToHexString(byte[] a) 
{
	StringBuilder sb = new StringBuilder(a.length * 2);
	for(byte b: a)
	sb.append(String.format("%02x", b));
	return sb.toString();
}	
	


/** Konvertiert ein Double in 8 Byte-Array **/
public static byte[] double_to_8Bytes(double value) 
{
    byte[] bytes = new byte[8];
    ByteBuffer.wrap(bytes).putDouble(value);
    return bytes;
}



/**	Wandelt ein 8 Byte-Array in Double um
	@param b Es werden maximal nur die ersten 8 Bytes verwendet.	
	Wenn weniger als 8Bytes übergeben wurde, wird wird vorne mit Nullen aufgefüllt. **/
public static double byteArray_to_double(byte[] b)
{
	if(b.length < 8)
	{
		byte[] out = new byte[8];
		System.arraycopy(b, 0, out, 8-b.length, b.length);		
		return ByteBuffer.wrap(out).getDouble();
	}	
	return ByteBuffer.wrap(b).getDouble();
}



/**	Konvertiert das normalte Integer Format in ein korrekt aufsteigendes Integer Format
	0x00000000 = -2.147.483.648;
	0xFFFFFFFF =  2.147.483.647;         */
public static int int_convert_int(int in)
{
	return in ^ -2147483648;
}



/** Wandelt ein Base58 Text-String in Hex-String um. 
	In "laenge" wird die Anzahl der Ausgabe-Zeichen übergeben. */
public static String Base58ToHexString(String str, int laenge)
{
	char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
	char[] a 	= str.toCharArray();
	BigInteger erg 	= new BigInteger("0");
	BigInteger b58 	= new BigInteger("58");
	int e = a.length-1;
	for(int j=0;j<a.length;j++)
	{
		for(int i=0;i<ALPHABET.length;i++)
		{
		 		if(a[j]==ALPHABET[i]) 
		 		{
		   			BigInteger I = new BigInteger(String.valueOf(i));
		   			erg = erg.add(I.multiply(b58.pow(e)));
		 		}    
		}
		e--;
	}
	char[] c = erg.toString().toCharArray();
	int nullLaenge = 0;
	for(int i=0; a[i]=='1' && i<a.length;i++) nullLaenge++;
	char[] EINS   = {'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0'};
	char[] KeyOut = new char[nullLaenge + c.length];
	System.arraycopy(c, 0, KeyOut, nullLaenge, c.length);
	System.arraycopy(EINS, 0, KeyOut, 0, nullLaenge);
	String out = new String(KeyOut);
	BigInteger big = new BigInteger(out,10);
	out =  big.toString(16); 
	String nullen = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
	out = nullen+out;
	return out.substring(out.length()-laenge);
}



/** Hexa String wird in einen Base58 String konvertiert. */
public static String hexStringToBase58(String str)
{
   byte[] b = hexStringToByteArray(str);
   char[] c = toBase58(b);
   return String.valueOf(c);
}
 

 
/** Byte Array wird in ein Base58 char Array konvertiert. */
public static char[] toBase58(byte[] k)   
{ 
     char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
     BigInteger z = new BigInteger(1,k);
     BigInteger z1;
     BigInteger rest = new BigInteger("0");
     BigInteger base = new BigInteger("58");
     int laenge=0;
     z1=z;           
     for(double i=1; i>0;) 
     {
        z1 = z1.divide(base);
        i  = z1.doubleValue();
        laenge++;
     }           
     char[] Key = new char[laenge];             
     for(int i=laenge; i>0; i--) 
     {
          rest = z.mod(base);
          Key[i-1] = ALPHABET[rest.intValue()];
          z =  z.divide(base);
     }
     int nullLänge = 0;
     for(int i=0; k[i]==0 && i<k.length;i++) nullLänge++;
     char[] EINS   = {'1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1'};
     char[] KeyOut = new char[nullLänge + Key.length];
     System.arraycopy(Key, 0, KeyOut, nullLänge, Key.length);
     System.arraycopy(EINS, 0, KeyOut, 0, nullLänge);
     return KeyOut; 
} 



/** Dreht die Reihenfolge eines ByteArray´s um.  */
public static void swapBytes(byte[] array) 
{
	if (array == null) return;
	int i = 0;
	int j = array.length - 1;
	byte tmp;
	while (j > i) 
	{
		tmp = array[j];
		array[j] = array[i];
		array[i] = tmp;
		j--;
		i++;
	}
}



/** Dreht die Reihenfolge eines ByteArray´s und gibt das Kopierte Array zurück. */
public static byte[] swapBytesCopy(byte[] in) 
{
	byte[] array = in.clone();
	if (array == null) return null;
	int i = 0;
	int j = array.length - 1;
	byte tmp;
	while (j > i) 
	{
		tmp = array[j];
		array[j] = array[i];
		array[i] = tmp;
		j--;
		i++;
	}
	return array;
}



/** Wandelt Byte-Array in Integer um, nur positive Zahlen.
	Es werden maximal nur die ersten 4 Bytes verwendet.
	Im Fehlerfall wird -1 zurückgegeben
	Maximaler Byte Wert für positive Werte: 7f ff ff ff  = 2147483647  = Maximaler positiver Integer Wert		*/
public static int byteArray_to_int(byte[] b) 
{
	if(b.length==1) return b[0]&0xff;
	if(b.length==2) return ((0xFF & b[0]) <<  8) |  (0xFF & b[1]);
	if(b.length==3) return ((0xFF & b[0]) << 16) | ((0xFF & b[1]) <<  8) |  (0xFF & b[2]);	
	if(b.length>=4) return ((0xFF & b[0]) << 24) | ((0xFF & b[1]) << 16) | ((0xFF & b[2]) << 8) | (0xFF & b[3]);
	return -1;
}



/** Wandelt Byte-Array in Integer um, nur positive Zahlen.
	Hier kann auch ein Integer-Array verwendet weren
	Es werden maximal nur die ersten 4 Bytes verwendet.
	Im Fehlerfall wird -1 zurückgegeben
	Maximaler Byte Wert für positive Werte: 7f ff ff ff  = 2147483647  = Maximaler positiver Integer Wert		*/
public static int byteArray_to_int(int[] b) 
{
	if(b.length==1) return b[0]&0xff;
	if(b.length==2) return ((0xFF & b[0]) <<  8) |  (0xFF & b[1]);
	if(b.length==3) return ((0xFF & b[0]) << 16) | ((0xFF & b[1]) <<  8) |  (0xFF & b[2]);	
	if(b.length>=4) return ((0xFF & b[0]) << 24) | ((0xFF & b[1]) << 16) | ((0xFF & b[2]) << 8) | (0xFF & b[3]);
	return -1;
}



/** Wandelt Byte-Array in Long um, nur positive Zahlen.
 	Wenn mehr als 8 Byte übergeben werden, werden nur die vordersten 8 Byte zur Berechnung benutzt.
	Maximaler Byte Wert für positive Werte: 7f ff ff ff ff ff ff ff  = 2^63-1 Maximaler positiver Long wert	*/
public static long byteArray_to_long(byte[] b) 
{
	if(b.length==1) return b[0]&0xff;
	if(b.length==2) return ((long)(0xFF & b[0]) <<  8) | ((long)(0xFF & b[1]));
	if(b.length==3) return ((long)(0xFF & b[0]) << 16) | ((long)(0xFF & b[1]) <<  8) | ((long)(0xFF & b[2]));	
	if(b.length==4) return ((long)(0xFF & b[0]) << 24) | ((long)(0xFF & b[1]) << 16) | ((long)(0xFF & b[2]) <<  8) | ((long)(0xFF & b[3]));
	if(b.length==5) return ((long)(0xFF & b[0]) << 32) | ((long)(0xFF & b[1]) << 24) | ((long)(0xFF & b[2]) << 16) | ((long)(0xFF & b[3]) <<  8) | ((long)(0xFF & b[4]));
	if(b.length==6) return ((long)(0xFF & b[0]) << 40) | ((long)(0xFF & b[1]) << 32) | ((long)(0xFF & b[2]) << 24) | ((long)(0xFF & b[3]) << 16) | ((long)(0xFF & b[4]) <<  8)  | ((long)(0xFF & b[5]));
	if(b.length==7) return ((long)(0xFF & b[0]) << 48) | ((long)(0xFF & b[1]) << 40) | ((long)(0xFF & b[2]) << 32) | ((long)(0xFF & b[3]) << 24) | ((long)(0xFF & b[4]) << 16)  | ((long)(0xFF & b[5]) <<  8) | ((long)(0xFF & b[6]));
	if(b.length>=8) return ((long)(0xFF & b[0]) << 56) | ((long)(0xFF & b[1]) << 48) | ((long)(0xFF & b[2]) << 40) | ((long)(0xFF & b[3]) << 32) | ((long)(0xFF & b[4]) << 24)  | ((long)(0xFF & b[5]) << 16) | ((long)(0xFF & b[6]) << 8) | ((long)(0xFF & b[7]));
	return -1;
}



/** Wandelt ein Integer in 2 Byte um.  */
public static byte[] int_To_2_ByteArray(int data)
{
	byte[] out = new byte[2];
	out[0] = (byte) ((data & 0x0000FF00) >> 8);
	out[1] = (byte) ((data & 0x000000FF) >> 0);
	return out;
}



/** Wandelt ein Integer in 4 Byte um.  */
public static byte[] int_To_4_ByteArray(int data)
{
	byte[] out = new byte[4];
	out[0] = (byte) ((data & 0xFF000000) >> 24);
	out[1] = (byte) ((data & 0x00FF0000) >> 16);
	out[2] = (byte) ((data & 0x0000FF00) >> 8);
	out[3] = (byte) ((data & 0x000000FF) >> 0);
	return out;
}



/** Wandelt ein Integer in 4 Byte um und swapt die Byte-Reihenvolge.  */
public static byte[] int_To_4_ByteArray_swap(int data)
{
	byte[] out = new byte[4];
	out[3] = (byte) ((data & 0xFF000000) >> 24);
	out[2] = (byte) ((data & 0x00FF0000) >> 16);
	out[1] = (byte) ((data & 0x0000FF00) >> 8);
	out[0] = (byte) ((data & 0x000000FF) >> 0);
	return out;
}



/**	Wandelt ein Long in 8 Byte um.
	Es werden immer genau 8Byte erzeugt und mit Nullen vorne aufgefüllt   */
public static byte[] long_To_8_ByteArray(long data)
{
	ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.putLong(data);
    return buffer.array();
}



/**	Beschneidet ein ByteArray belibiger länge auf eine fest definierte Länge "len".
	- Wenn "data" kleiner als "len" ist wird es vorne mit Nullen aufgefüllt.
	- Wenn "data" länger als "len" ist, wird es hinten abgeschnitten.   */
public static byte[] to_fixLength(byte[] data, int len)
{
	if(data.length < len)
	{
		byte[] out = new byte[len];
		System.arraycopy(data, 0, out, len-data.length, data.length);
		return out;
	}	
	if(data.length > len) return Arrays.copyOf(data, len);
	return data;
}



/**	Entfernt alle führenden Nullen eines Byte-Arrays
@return Null wenn das Byte-Array nur Nullen enthällt.  **/
public static byte[] removeLeadingZeros(byte[] data)
{
	int i=0;
	for(;i<data.length;i++) {if(data[i] != 0x00) break;}
	return Arrays.copyOfRange(data, i, data.length);
}



/**	Prüft ob der als Zahl interpretierter String zwischen a und b liegt.  */
public static boolean isZwischen(String str, int a, int b)
{
	int z = Integer.parseInt(str);	
	if(z>=a && z<= b)return true;
	else return false;	
}



/** Konvertiert einen BitcoinBetrag von ByteArray nach Text-String. z.B. (0,03198327)  
 	Es wird Komma als Trennzeichen angezeigt.
 	Es werden immer 8 Nachkommazeichen vollständig angezeigt.
	Methode getestet mit Referenz (BitcoinCore über RPC). Zahlenraum vollständig getestet von 0.00000000  bis 0.15000000   **/
public static String byteArrayToValue(byte[] hexValue)
{
	byte[] b = Convert.swapBytesCopy(hexValue);
	return new DecimalFormat("0.00000000").format((double)Convert.byteArray_to_long(b)/100000000);		
}



/** Konvertiert ein Bitcoin Betrag von einem Zahlen-Wert (z.B. 0,03198327) in ein ByteArray so wie der Betrag in der Transaktion gepeichert wird.  
	Es kann Komma oder Punkt als Trennzeichen verwendet werden.
	Bis zu 8 Nachkommastellen erlaubt. Nachkommastellen als "0" können weggelassen werden.
	Methode getestet mit Referenz (BitcoinCore über RPC). Zahlenraum vollständig getestet von 0.00000000  bis 0.15000000   **/
public static byte[] valueToHex(String inValue)
{
	double d = Double.parseDouble(inValue.replace(",", "."));
	String str = String.format("%.8f", d);
	str = str.replaceFirst(",", "");
	BigInteger bi = new BigInteger(str);
	bi.longValue();
	Long l = bi.longValue();
	return Convert.swapBytesCopy(Convert.long_To_8_ByteArray(l)); 
}
}