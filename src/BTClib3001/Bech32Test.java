package BTClib3001;

import java.math.BigInteger;


/*******************************************************************************************************
* 	Entwicklung eigener Implementierung von Bech32 Adressen  											*
* 	Zum Testen:		https://slowli.github.io/bech32-buffer/												*
*	Anleitung: https://bitcointalk.org/index.php?topic=4992632.0										*
*******************************************************************************************************/


public class Bech32Test 
{

	
	//	private static final String	ALPHABET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l";
	//	private static final int[] 	GENERATOR = {0x3B6A57B2, 0x26508E6D, 0x1EA119FA, 0x3D4233DD, 0x2A1462B3};	
	
	
	
	public static void main(String[] args) throws Exception
	{
//		byte[] pub  = Convert.hexStringToByteArray("0279be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798");
//		byte[] b0 	= Calc.getHashRIPEMD160(Calc.getHashSHA256(pub));
//		print(b0);
//		byte[] b1 = new byte[22];
//		b1[0]  = (byte)0x21;
//		b1[21] = (byte)0xac;
//		System.arraycopy(b0, 0, b1, 1, 20);		
//		print(b1);
//		b1 = Calc.getHashSHA256(b1);
//		print(b1);
		
		
	

		
		BigInteger bi = new BigInteger("751e76e8199196d454941c45d1b3a323f1433bd6",16);
		String str = bi.toString(2);	
		if(str.length()%2==1)  str = "0"+str;			
		String out ="";
		for(int i=0;i<str.length();i=i+5)
		{
			String str1 = str.substring(i, i+5);
			BigInteger bi2 = new BigInteger(str1,2);
			String str2 = bi2.toString(16);
			if(str2.length()%2==1)  str2 = "0"+str2;	
			out=out+str2;		
		}
		out = "00"+out;		
		System.out.println(out);
		
		
		
		
		out = Calc.getHashSHA256_from_HexString(Calc.getHashSHA256_from_HexString(out));
		
		System.out.println(out);
	
		
		
		//print(b);
		// 01110 10100 01111 00111 01101 11010 00000 11001 10010 00110 01011 01101 01000 10101 00100 10100 00011 10001 00010 11101 00011 01100 11101 00011 00100 01111 11000 10100 00110 01110 11110 10110
		// 01110 10100 01111 00111 01101 11010 00000 11001 10010 00110 01011 01101 01000 10101 00100 10100 00011 10001 00010 11101 00011 01100 11101 00011 00100 01111 11000 10100 00110 01110 11110 10110
	}
	
	
	
	

	
}
