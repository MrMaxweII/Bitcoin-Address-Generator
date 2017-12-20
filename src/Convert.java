import java.math.BigInteger;
import java.util.Base64;



	/********************************************************
	* 							*
	*	Conversion of different formats			*
	*							*
	*********************************************************/



public class Convert
{


	  static byte[] hexStringToByteArray(String hex) 
	  {                           
	    	if((hex.length()%2)==1)
	    	{
	      		char c = '1';
	      		hex += c;
	    	}
	    	int l = hex.length();
	    	byte[] data = new byte[l/2];
	    	for (int i = 0; i < l; i += 2) 
	    	{
	      		data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
	    	}
	    	return data;
	  }



	  public static String byteArrayToHexString(byte[] a) 
	  {
	    	StringBuilder sb = new StringBuilder(a.length * 2);
	    	for(byte b: a) sb.append(String.format("%02X", b));
	    	return sb.toString();
	  }



// ------------------------------------------------------ Base58 -----------------------------------------------------------------//

	  public static String hexStringToBase58(String str)
	  {
		  byte[] b = hexStringToByteArray(str);
		  char[] c = toBase58(b);
		  return String.valueOf(c);
	  }



	  private static char[] toBase58(byte[] k)   
	  { 
	      	char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();
	      	BigInteger z 	= new BigInteger(1,k);
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
	      	int nullLaenge = 0;
	      	for(int i=0; k[i]==0 && i<k.length;i++) nullLaenge++;
	      	char[] EINS   = {'1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1'};
	      	char[] KeyOut = new char[nullLaenge + Key.length];
	      	System.arraycopy(Key, 0, KeyOut, nullLaenge, Key.length);
	      	System.arraycopy(EINS, 0, KeyOut, 0, nullLaenge);
	      	return KeyOut; 
	  }	



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



	public static String base58_PrivateKey_to_HexPrivateKey(String str) throws KeyException
	{
		if(Calc.ist_PrivateKey_Base58_Valid(str) == false) throw new KeyException("Private-Key Base58 ist nicht richtig");
		String erg = Base58ToHexString(str,74);	
		return erg.substring(2,66);	
	}	  



	public static String base58compressed_PrivateKey_to_HexPrivateKey(String str) throws KeyException
	{
		if(Calc.ist_PrivateKey_Base58compressed_Valid(str) == false) throw new KeyException("Private-Key Base58compressed ist nicht richtig");
		String erg = Base58ToHexString(str,76);
		return erg.substring(2,66);	
	}



// ------------------------------------------------------ Base64 -----------------------------------------------------------------//		

	public static String base64_PrivateKey_to_HexPrivateKey(String str) throws KeyException
	{
		if(Calc.ist_PrivateKey_Base64_Valid(str) == false)  throw new KeyException("Private-Key Base64 ist nicht richtig");
		byte[] erg = Base64.getDecoder().decode(str);
		return Convert.byteArrayToHexString(erg);
	}


}
