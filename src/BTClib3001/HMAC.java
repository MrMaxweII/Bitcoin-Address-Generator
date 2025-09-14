package BTClib3001;
import java.util.Arrays;



/****************************************************************************************************************
*	Version 1.0    						Autor: Mr. Nickolas-Antoine B.   						vom 22.02.2023				*
*	HMAC SHA256																									*
*	Calculates an HMAC using SHA256: "https://de.wikipedia.org/wiki/HMAC"									*	
*	A static class that has only one static method.	 													*
*	There is only one dependency on the SHA256 hash class														*
*	Tested against the reference implementation of "javax.crypto.Mac;" with 10000000 random. vectors, random. lengths.	*
****************************************************************************************************************/




public class HMAC 
{



	/**	HMAC function that uses SHA256 **/
	public static byte[] getHMAC_SHA256(byte[] data, byte[] key) throws Exception
	{	
		byte[] k;
		if(key.length>64) k = Arrays.copyOf(SHA256.getHash(key),64);
		else k = Arrays.copyOf(key, 64);	
		byte[] opad = new byte[64];
		byte[] ipad = new byte[64];
		for(int i=0; i<64;i++) opad[i]=0x5c;
		for(int i=0; i<64;i++) ipad[i]=0x36;
		byte[] b = SHA256.getHash(add(xor(k,ipad), data));
		byte[] out = SHA256.getHash(add(xor(k,opad), b));	
		return out;	
	}
	

	
// ------------------------------------------------- private Methoden -------------------------------------------
	
	// connects the two arrays behind each other
	private static byte[] add(byte[] a, byte[] b) 
	{
		byte[] out = new byte[a.length + b.length];
		System.arraycopy(a, 0, out, 0, a.length);
		System.arraycopy(b, 0, out, a.length, b.length);
		return out;	
	}
	
	
	// XOR of a and b. a unb b must be the same length!
	private static byte[] xor(byte[] a, byte[] b) throws Exception
	{
		if(a.length != b.length) throw new Exception("a and b are not the same length");
		byte[] out = new byte[a.length];
		for(int i=0;i<a.length;i++)
		{
			out[i] =  (byte) (a[i] ^ b[i]);
		}
		return out;
	}
}