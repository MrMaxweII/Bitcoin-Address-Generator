package lib3001.crypt;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import lib3001.ecdsa.Secp256k1;




/********************************************************************************************************************
*	Version 1.8    	 					Autor: Mr. Maxwell											vom 03.01.2024 	*
* 	LIB3001 Bibliotheks Klasse																						*
*	Letzte Änderung: getDoubleHashSHA256() hinzugefügt. (Doppelter SHA256)											*
*	Hier werden gundlegende Crypt-Berechnungen durchgefürt.															*
*********************************************************************************************************************/



public class Calc
{



// ------------------------------------------------- Secp256k1 ------------------------------------------------------//

/** Berechnet den PublicKey aus einem Private-Key.
	@param str Übergeben wird der Private-Key als Hex-String (32Byte)
	@param compressed wenn "true" dann wird der Pub-Key komprimiert (nur X-Koordinate)
	@return Gibt den Public Key als Hex-String zurück.
	Das Erste Byte ist ein Status-Byte mit den folgenden Informationen.
	02 : komprimierter Pub-Key, enthält nur die X-Koordinate, die Y-Koordinate ist positiv (33Byte)
	03 : komprimierter Pub-Key, enthält nur die X-Koordinate, die Y-Koordinate ist negativ (33Byte)
	04 : unkomprimierter Pub-Key mit X und Y Koordinaten (65Byte) **/
public static String getPublicKey(String str, boolean compressed)
{
    byte[] b = getPublicKey(Convert.hexStringToByteArray(str),compressed);
    return Convert.byteArrayToHexString(b);
}




/** Berechnet den PublicKey aus einem Private-Key.
@param privateKey Übergeben wird der Private-Key als Byte-Array (32Byte)
@param compressed wenn "true" dann wird der Pub-Key komprimiert (nur X-Koordinate)
@return Gibt den Public Key als Byte-Array zurück.
Das Erste Byte ist ein Status-Byte mit den folgenden Informationen.
02 : komprimierter Pub-Key, enthält nur die X-Koordinate, die Y-Koordinate ist positiv (33Byte)
03 : komprimierter Pub-Key, enthält nur die X-Koordinate, die Y-Koordinate ist negativ (33Byte)
04 : unkomprimierter Pub-Key mit X und Y Koordinaten (65Byte) **/
public static byte[] getPublicKey(byte[] privateKey, boolean compressed)
{
	Secp256k1 spec = new Secp256k1();
	BigInteger[] bi = spec.multiply_G(new BigInteger(1,privateKey));
	return Secp256k1.comp(bi, compressed);
}








// ------------------------------------------------- Hash SHA256 ------------------------------------------------------//

/** Berechnet den Hash(SHA256) aus einem Hex-String und gibt ihn als Hex-String zurück.   */
public static String getHashSHA256_from_HexString(String str)
{
	  byte[] b = getHashSHA256(Convert.hexStringToByteArray(str));
	  return Convert.byteArrayToHexString(b);
}


/** Berechnet den doppelten Hash(SHA256) aus einem Hex-String und gibt ihn als Hex-String zurück.   */
public static String getDoubleHashSHA256_from_HexString(String str)
{
	  byte[] b = getDoubleHashSHA256(Convert.hexStringToByteArray(str));
	  return Convert.byteArrayToHexString(b);
}


/** Berechnet den Hash(SHA256) aus einem normalen Text und gibt ihn als Hex-String zurück.  */
public static String getHashSHA256(String str)
{
	try
	{
		byte[] b = getHashSHA256((str).getBytes("UTF-8"));
		return Convert.byteArrayToHexString(b);
	}
		catch (UnsupportedEncodingException e) {
			System.out.println("Error getHashSHA256()");
			System.out.println(e.getMessage());
			return "-1";
	}

}


/** Berechnet SHA-256 aus Byte-Array. Rückgabe ist Byte-Array. **/
public static byte[] getHashSHA256(byte[] b)
{
	MessageDigest sha;
	try
	{
		sha = MessageDigest.getInstance("SHA-256");
		return sha.digest(b);
	}
	catch (NoSuchAlgorithmException e)
	{
		System.out.println("Error getHashSHA256()");
		System.out.println(e.getMessage());
		return null;
	}
}


/** Berechnet den doppelten SHA-256 aus Byte-Array. Rückgabe ist Byte-Array. **/
public static byte[] getDoubleHashSHA256(byte[] b)
{
	return getHashSHA256(getHashSHA256(b));
}


//------------------------------------------------- Hash RIPEMD-160 neu ----------------------------------------------------//


public static String getHashRIPEMD160_from_HexString(String str) throws NoSuchAlgorithmException
{
	  byte[] b = getHashRIPEMD160(Convert.hexStringToByteArray(str));
	  return Convert.byteArrayToHexString(b);
}


public static byte[] getHashRIPEMD160(byte[] b) 
{
	return Ripemd160.getHash(b);
}






// ------------------------------------------------- SHA1 ----------------------------------------------------------------//

/**	Gibt den SHA1 Hash in Base64 zurück  **/
public static String getHashSHA1(String in)
{
	try
	{
		MessageDigest crypt = MessageDigest.getInstance("SHA-1");
		crypt.update(in.getBytes("UTF-8"));
		return Base64.getEncoder().encodeToString(crypt.digest());
	}
	catch (NoSuchAlgorithmException | UnsupportedEncodingException e)
	{
		System.out.println("Error getHashSHA1()");
		System.out.println(e.getMessage());
		return null;
	}
}





// ------------------------------------------------------ Compact-Size ---------------------------------------------------//

/** parst einen Datensatz mit Variabler Länge  Compact-Size: https://en.bitcoin.it/wiki/Protocol_documentation
 	"pos" zeigt auf den Startwert des ersten Compact-Size Bytes.
	Rückgabe ist ein Byte-Array mit den Nutzdaten.         **/
public static byte[] parseCompactSize(byte[] data, int pos) throws ConnectException
{
	int[] sizeData = decodeCompactSize(data,pos);									// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0];														// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];															// Länge der Nutzdaten
	if(len<0) {throw new ConnectException("Error in Calc.parseCompactSize, max size 2147483647 Byte!"); }
	byte[] out = new byte[len];														// Das Ausgabe Array wird erstellt
	System.arraycopy(data,start,out,0,len);											// Die Nutzdaten werden in das Ausgabe-Array kopiert.
	return out;
}



/** Decodiert die Längeninformationen aus einem Compact-Size Format:  https://en.bitcoin.it/wiki/Protocol_documentation
 	Übergeben wird ein Byte-Array welches Compact-Size Daten beinhaltet.
 	Das Übergebene Array wird hier nicht verändert sondern nur Analysiert.
 	"pos" zeigt auf den Startwert des Compact-Size Bytes welches Analysiert werden soll.
 	Das Rückgabe-Array ist 2 Elemente lang: int[0]="start" und int[1]="len"
 	int[0]="start" ist der Startwert ab welchem Byte die Nutzdaten beginnen.
 	int[1]="len" ist die Länge der Nutzdaten.       **/
public static int[] decodeCompactSize(byte[] data, int pos)
{
	int[] out = new int[2];
	if((data[pos]&0xff) < 253)											// Auswahl <0xFD
	{
		byte[] b = {data[pos]};											// Das erste Feld welches die Länge enthält wird zwischengespeichert
		out[1] = Convert.byteArray_to_int(b);							// Die Länge wird in Integer konvertiert
		out[0] = pos+1;
	}

	if(data[pos]==(byte)0xFD)											// Auswahl  == 0xFD
	{
		byte[] b = {data[pos+1],data[pos+2]};							// Das zweite Feld welches die Länge enthält wird zwischengespeichert
		Convert.swapBytes(b);											// Byte-Reihenfolge wird vertauscht
		out[1] = Convert.byteArray_to_int(b);							// Die Länge wird in Integer konvertiert
		out[0] = pos+3;
	}

	if(data[pos]==(byte)0xFE)											// Auswahl == 0xFE
	{
		byte[] b = {data[pos+1],data[pos+2],data[pos+3],data[pos+4]};	// Das dritte Feld welches die Länge enthält wird zwischengespeichert
		Convert.swapBytes(b);											// Byte-Reihenfolge wird vertauscht
		out[1] = Convert.byteArray_to_int(b);							// Die Länge wird in Integer konvertiert
		out[0] = pos+5;
	}
	return out;
}



/** Codiert CompactSize Daten, in dem die entsprechende Bytes voran gestellt werden.
	@param dataIn Nutzdaten die Kodiert werden sollen.
	@return Nutzdaten mit den Vorangestellten Bytes   **/
public static byte[] encodeCompactSize(byte[] dataIn)
{
	int size = dataIn.length;
	if(size < 253)
	{
		byte[] out = new byte[size+1];
		out[0] = (byte) (size&0xff);
		System.arraycopy(dataIn, 0, out, 1, size);
		return out;
	}
	if(size >= 253 && size < 65536 )
	{
		byte[] out = new byte[size+3];
		out[0] = (byte) 0xfd;
		byte[] s = Convert.swapBytesCopy(Convert.int_To_2_ByteArray(size));
		System.arraycopy(s, 0, out, 1, 2);
		System.arraycopy(dataIn, 0, out, 3, size);
		return out;
	}
	if(size >= 65536)
	{
		byte[] out = new byte[size+5];
		out[0] = (byte) 0xfe;
		byte[] s = Convert.swapBytesCopy(Convert.int_To_4_ByteArray(size));
		System.arraycopy(s, 0, out, 1, 4);
		System.arraycopy(dataIn, 0, out, 5, size);
		return out;
	}
	return null;
}
}