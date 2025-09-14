package BTClib3001;
import java.io.IOException;
import java.util.Arrays;



/***********************************************************************************************************************************************
 * Version 1.0   						 Autor: Mr. Nickolas-Antoine B. 				  	vom 16.02.2023													*
 * 																																				*
 * AES256-CBC Mode																																*
 * Encryption and Decryption with AES 256Bit, Rijndael Algorithmus 																				*
 * - AES-256,  only the 256Bit version is implemented here																				*
 * - CBC Mode; only the CBC mode is implemented here																							*
 * - No Padding! The application must implement padding (filling with bytes to block size (128Bit) by itself!						*
 * - This class does not require external dependencies and runs completely alone in this class.										*
 * - This implementation is variable and generally applicable.																				*
 * - This AES256 class is NIST-compliant. The generated test vectors of the cipher correspond to those published by NIST.			*
 * - This Rijndael implementation is designed to be simple, short, and understandable. (Textbook code) No complex detours.					*
 * - Side-channel attack vectors, etc. are not taken into account in this implementation.													*
 * - The code has been extensively tested against BouncyCastle. All ciphers are always equal.											*
 * 																																				*
 * Application: There are two main methods for encryption and decryption:																			*
 * 		public static byte[] encrypt(byte[] key, byte[] iv, byte[] text);																		*
 *  	public static byte[] decrypt(byte[] key, byte[] iv, byte[] chiffre);																	*
 * All inputs and outputs of these methods are to be done in byte arrays. 																		*
 * Attention the iv vector must be a random vector! The application must create the 128Bit random vector "iv" by itself.						*
 * Since no padding is implemented, the text or the cipher must be divisible by 16Bytes without a remainder. So 16, 32, 48 ... Bytes long!		*
 ***********************************************************************************************************************************************/



public class AES256 
{

	/**	AES-256 CBC-Mode Encryption without Padding.
	@param text Corresponds to the plain text as a byte array which is to be encrypted. Must be divisible by 16 bytes without a remainder! So 16, or 32, or 48 ... Bytes long!
	@param key The main key as a byte array must be exactly 32Bytes long! Since this is a 256Bit encryption.
	@param iv The random vector iv as a byte array. Must be exactly 16Bytes long (128Bit block size) Attention never use a fixed number!
	@return The encrypted cipher is returned as a byte array! Since there is no padding here, it is always divisible by 16Bytes without a remainder.	**/
	public static byte[] encryptCBC(byte[] text, byte[] key, byte[] iv) throws IOException
	{
		if(text.length % 16 !=0) throw new IOException("text not block size (16Byte) aligned");
		if(key.length  % 32 !=0) throw new IOException("key not 32Byte long!");
		if(iv.length   % 16 !=0) throw new IOException("iv not 16Byte long!");
		int round = text.length/16; 	
		int[] ivi = split(iv); 
		int[] subKeys = keyExpand(key);			
		byte[] out = new byte[text.length];
		for(int r=0; r<round; r++)
		{		
			int[] data = split(Arrays.copyOfRange(text, r*16, r*16+16));
			data = keyAdd(ivi,data);
			data = keyAdd(Arrays.copyOfRange(subKeys, 0, 4),data);		 
			for(int i=0; i<13; i++)
			{
				data = subBytesAndShiftRows(data,false);
				data = mixColumns(data,false);
				data = keyAdd(Arrays.copyOfRange(subKeys, (i*4)+4, (i*4)+8),data); 
			}
			data = subBytesAndShiftRows(data,false);
			data = keyAdd(Arrays.copyOfRange(subKeys, 56, 60),data); 			
			ivi = data; // for the next round, iv is the previous data set (CBC-Mode)				
			byte[] b1 = int_To_4_ByteArray(data[0]);
			byte[] b2 = int_To_4_ByteArray(data[1]);
			byte[] b3 = int_To_4_ByteArray(data[2]);
			byte[] b4 = int_To_4_ByteArray(data[3]);			
			System.arraycopy(b1, 0, out, (r*16)+0, 4);
			System.arraycopy(b2, 0, out, (r*16)+4, 4);
			System.arraycopy(b3, 0, out, (r*16)+8, 4);
			System.arraycopy(b4, 0, out, (r*16)+12, 4);
		}
		return out;
	}


	
	
	/**	AES-256 CBC-Mode Decryption without Padding.
	@param chiffre The cipher as a byte array which is to be decrypted. Must be divisible by 16 bytes without a remainder! So 16, or 32, or 48 ... Bytes long!
	@param key The main key as a byte array must be exactly 32Bytes long! Since this is a 256Bit encryption.
	@param iv The random vector iv as a byte array. Must be exactly 16Bytes long (128Bit block size) Attention never use a fixed number!
	@return The decrypted plain text is returned as a byte array! Since there is no padding here, it is always divisible by 16Bytes without a remainder.	**/
	public static byte[] decryptCBC(byte[] chiffre, byte[] key, byte[] iv) throws IOException
	{
		if(chiffre.length % 16 !=0) throw new IOException("cipher not block size (16Byte) aligned");
		if(key.length  	  % 32 !=0) throw new IOException("key not 32Byte long!");
		if(iv.length      % 16 !=0) throw new IOException("iv not 16Byte long!");
		int round = chiffre.length/16; 	
		int[] ivi = split(iv); 
		int[] subKeys = keyExpand(key);	
		byte[] out = new byte[chiffre.length];
		for(int r=0; r<round; r++)
		{	
			int[] data = split(Arrays.copyOfRange(chiffre, r*16, r*16+16));
			if(r>0) ivi = split(Arrays.copyOfRange(chiffre, (r-1)*16, (r-1)*16+16));
			data = keyAdd(Arrays.copyOfRange(subKeys, 56, 60),data); 	
			data = subBytesAndShiftRows(data,true);
			for(int i=12; i>=0; i--)
			{
				data = keyAdd(Arrays.copyOfRange(subKeys, (i*4)+4, (i*4)+8),data); 			
				data = mixColumns(data,true);
				data = subBytesAndShiftRows(data,true);
			}	
			data = keyAdd(Arrays.copyOfRange(subKeys, 0, 4),data); 	
			data = keyAdd(ivi,data);
			byte[] b1 = int_To_4_ByteArray(data[0]);
			byte[] b2 = int_To_4_ByteArray(data[1]);
			byte[] b3 = int_To_4_ByteArray(data[2]);
			byte[] b4 = int_To_4_ByteArray(data[3]);			
			System.arraycopy(b1, 0, out, (r*16)+0, 4);
			System.arraycopy(b2, 0, out, (r*16)+4, 4);
			System.arraycopy(b3, 0, out, (r*16)+8, 4);
			System.arraycopy(b4, 0, out, (r*16)+12, 4);
		}
		return out;
	}
	
	
	
	
//------------------------------------------ SubBytes / ShiftRows / MixColumns / KeyAddition -------------------------------------------//
	

	// 16Byte Array wird in 4 Integer-Zahlen aufgeteilt
	public static int[] split(byte[] key)
	{
		int[] out = new int[4];
		for(int i=0; i<4; i++)
		{
    		byte[] b = Arrays.copyOfRange(key, i*4, (i*4)+4);   		
    		out[i] 	 = byteArray_to_int(b);
		}
		return out;
	}
	
	
	// Key-Additions Methode, der SubKey wird mit data addiert (XOR)
	// Alle In/Out daten sind 4 Integer Zahlen in einem Array
	private static int[] keyAdd(int[] subKey, int[] data)
	{
		int[] out = new int[4];
		for(int i=0;i<4;i++)
		{
			out[i] = subKey[i] ^ data[i];
		}
		return out;
	}

	
	// SubBytes und ShiftRows in einer Mehtode
	// Wenn "inv" == true dann wird die Inverse-Funktion berechnet, sonnst die normale.
	private static int[] subBytesAndShiftRows(int[] data, boolean inv)
	{
		byte[] a0 = int_To_4_ByteArray(data[0]);
		byte[] a1 = int_To_4_ByteArray(data[1]);
		byte[] a2 = int_To_4_ByteArray(data[2]);
		byte[] a3 = int_To_4_ByteArray(data[3]);		
		int[] ii = new int[16];	
		if(inv==false) // normale
		{
			ii[0]  = getSBox(a0[0],false);
			ii[13] = getSBox(a0[1],false);
			ii[10] = getSBox(a0[2],false);
			ii[7]  = getSBox(a0[3],false);   	
			ii[4]  = getSBox(a1[0],false);
			ii[1]  = getSBox(a1[1],false);
			ii[14] = getSBox(a1[2],false);
			ii[11] = getSBox(a1[3],false);		
			ii[8]  = getSBox(a2[0],false);
			ii[5]  = getSBox(a2[1],false);
			ii[2]  = getSBox(a2[2],false);
			ii[15] = getSBox(a2[3],false);		
			ii[12] = getSBox(a3[0],false);
			ii[9]  = getSBox(a3[1],false);
			ii[6]  = getSBox(a3[2],false);
			ii[3]  = getSBox(a3[3],false);	
		}
		else // inverse
		{
			ii[0]  = getSBox(a0[0],true);
			ii[5]  = getSBox(a0[1],true);
			ii[10] = getSBox(a0[2],true);
			ii[15] = getSBox(a0[3],true);  		
			ii[4]  = getSBox(a1[0],true);
			ii[9]  = getSBox(a1[1],true);
			ii[14] = getSBox(a1[2],true);
			ii[3]  = getSBox(a1[3],true);	
			ii[8]  = getSBox(a2[0],true);
			ii[13] = getSBox(a2[1],true);
			ii[2]  = getSBox(a2[2],true);
			ii[7]  = getSBox(a2[3],true);
			ii[12] = getSBox(a3[0],true);
			ii[1]  = getSBox(a3[1],true);
			ii[6]  = getSBox(a3[2],true);
			ii[11] = getSBox(a3[3],true);	
		}
		
		int[] out = new int[4];
		out[0] = byteArray_to_int(Arrays.copyOfRange(ii, 0, 4));
		out[1] = byteArray_to_int(Arrays.copyOfRange(ii, 4, 8));		
		out[2] = byteArray_to_int(Arrays.copyOfRange(ii, 8, 12));		
		out[3] = byteArray_to_int(Arrays.copyOfRange(ii, 12, 16));		
		return out;
	}
	
	
	
	// Führt die Berechnung von MixColumns für 4 Integer (16-Byte) durch und gibt das Ergebnis als Integer-Array (4-Integer) zurück.
	// Wenn "inv" == true dann wird die Inverse-Funktion berechnet, sonnst die normale.
	private static int[] mixColumns(int[] in, boolean inv)
	{
		int[] out = new int[4];
		for(int i=0; i<4; i++)
		{
			out[i] = mixColumns(in[i], inv);
		}
		return out;
	}
	
	
	
	// führt die Berechnung von MixColumns für ein vollständigen Integer (4-Byte) durch und gibt das Ergebnis als vollständigen Integer zurück.
	// Wenn "inv" == true dann wird die Inverse-Funktion berechnet, sonnst die normale.
	private static int mixColumns(int in, boolean inv) 
	{ 
	    int a = (in & 0xff000000)>>>24;
	    int b = (in & 0x00ff0000)>>>16;
	    int c = (in & 0x0000ff00)>>>8;
	    int d = (in & 0x000000ff);
	    int[] out = new int[4];
	    if(inv==false) // normale
	    {
		    out[0] = gMul(2,a) ^ gMul(3,b) ^ gMul(1,c) ^ gMul(1,d);
		    out[1] = gMul(1,a) ^ gMul(2,b) ^ gMul(3,c) ^ gMul(1,d);
		    out[2] = gMul(1,a) ^ gMul(1,b) ^ gMul(2,c) ^ gMul(3,d);
		    out[3] = gMul(3,a) ^ gMul(1,b) ^ gMul(1,c) ^ gMul(2,d);
	    }
	    else // inverse
	    {
	        out[0] = gMul(0x0e,a) ^ gMul(0x0b,b) ^ gMul(0x0d,c) ^ gMul(0x09,d);
		    out[1] = gMul(0x09,a) ^ gMul(0x0e,b) ^ gMul(0x0b,c) ^ gMul(0x0d,d);
		    out[2] = gMul(0x0d,a) ^ gMul(0x09,b) ^ gMul(0x0e,c) ^ gMul(0x0b,d);
		    out[3] = gMul(0x0b,a) ^ gMul(0x0d,b) ^ gMul(0x09,c) ^ gMul(0x0e,d);
	    }
	    return byteArray_to_int(out);
	}
	
	
	
	//	Multiplikation auf dem Galois-Feld. Es wird a mit b multipliziert.
	//	Alle Zahlen (input und Output) dürfen/können nur zwichen 0 und 255 liegen!	
	//	Es wird also nur das letzte Byte aus dem Integer verwendet. 
	private static int gMul(int a, int b) 
	{
		int  p = 0;
		int  hi;	
		for(int i = 0; i < 8; i++) 
		{
			if((b & 1) == 1) p ^= a;
			hi = (a & 0x80);
			a <<= 1;
			if(hi == 0x80) a ^= 0x1b;		
			b >>= 1;
		}	
		return p & 0x000000FF;
	}	
	
	
    /**	Transformiert das Eingangs-Byte mit der sBox.
	In Java kann der Datentyp Byte nicht verwendet werden!
	Dieses Integer für Input und Output wird als Byte verwendet!
	Es können nur Zahlen zwischen 0x00000000 und 0x000000ff verwendet werden!!! 
	Da ein Byte in Java intern auch als Integer verarbeitet wird, löscht diese Methode alle bits die größer als 0x000000ff sind!
	@param inv Wenn "inv" == true dann wird die Inverse-Funktion berechnet, sonnst die normale.
	@return gibt ein Integer, welches als Byte zu interpretieren ist zurück. (0x00000000 bis 0x000000ff) **/
private static int getSBox(int in, boolean inv)
{
	in = in & 0x000000ff;  // Falls "in" größer als erlaubt ist, werden alle falschen bit´s gelöscht
	int[] sBox = new int[] {
	0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
	0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
	0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
	0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
	0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
	0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
	0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
	0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
	0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
	0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
	0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
	0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
	0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
	0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
	0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
	0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 };
	
	int[] inv_sBox = new int[] {
	0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
	0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
	0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
	0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
	0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
	0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
	0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
	0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
	0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
	0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
	0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
	0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
	0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
	0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
	0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
	0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d };	
		
	if(inv == false) 	return sBox[in];
	else	 			return inv_sBox[in];
}
	
	
    
// --------------------------------------------------------------- Schlüssel-Expansion  ------------------------------------------------------------------------------------- // 
		
	/**	Berechnung aller Rundenschlüssel (Sub-Keys)
		@param masterKey Der Hauptschlüssel (32Byte = 256Bit) wird als Byte-Array eingegeben
		@return Alle Rundenschlüssel werden in einem einzigen langen Integer-Array zurück gegeben.
		Es werden insgesamt 15 Rundenschlüssel in AES-256 benötigt und hier erstellt.
		Dabei besteht jeder Rundenschlüssel aus genau 4 Integer-Werten. D.H. 4x15 = 60 Integer Zahlen werden hier zurück gegeben.	**/
	private static int[] keyExpand(byte[] masterKey)
	{
		int[] out = new int[64];					// Das Ausgabe-intArray. (Wird am Ende auf die Länge 60 gekürzt)
		int[] key = splitKey(masterKey);			// Der Master-Key wird in 8 Integer´s aufgeteilt
		System.arraycopy(key, 0, out, 0, 8); 		// Der Master-Key wird so wie er ist, in die ersten 8 Integer kopiert, er bilden die ersten beiden Sub-Keys
		int pos = 8;								// Positionszähler, an dieser Position wird der nächste Schlüssel eingefügt.
		for(int i=1;i<128;i=i<<1)					// Der Rundenzähler "i" zählt etwas seltsam: 1,2,4,8,16 usw.
		{
			key = getOneRoundKey(key,i);			// Gibt einen einzigen Rundenschlüssel zurück, er besteht bei AES-256 aber aus zwei SChlüsseln, also 8-Integer´s
			System.arraycopy(key, 0, out, pos, 8);	// Kopieren nach "out"
			pos = pos+8;							// Position für den nächsten Schlüssel, 8 nach hinten
		}		
		out = Arrays.copyOf(out, 60); 				// Länge wird auf 60 gekürzt, weil durch den Algorithmus 64 Integer = 16 Schlüssel erzeugt werden.
		return out; 
	}
	
		
	/**	Berechnung der Rundenschlüssel für 256Bit, wie im Video: "https://youtu.be/tvYHcS57Lq4?t=2774" min: 46:00	
		Es werden immer genau 2 Rundenschlüssel berechnet, ausgenommen der ersten Beiden, die ja der Master-Key selbst sind.
		@param key Der Master Key (32Byte lang)
		@param round die Nummer der Runde, zwichen 1 und 15
		@return Es wird ein Integer Array mit genau 8 Integer´s zurück gegeben. Diese 8 Zahlen sind 2 Rundenschlüssel	**/
	private static int[] getOneRoundKey(int[] k, int round)
	{
		int[] out = new int[8];		
		out[0] = k[0] ^ g(k[7],round);  
		out[1] = k[1] ^ out[0];
		out[2] = k[2] ^ out[1];
		out[3] = k[3] ^ out[2];	
		out[4] = k[4] ^ h(out[3]);  
		out[5] = k[5] ^ out[4];
		out[6] = k[6] ^ out[5];
		out[7] = k[7] ^ out[6];
		return out;
	}
    
	
	// Der Master-Key als Byte-Array wird in 8 Integer Zahlen aufgeteilt und in einen Integer-Array zurückgegeben
	private static int[] splitKey(byte[] key)
	{
		int[] out = new int[8];
		for(int i=0; i<8; i++)
		{
    		byte[] b = Arrays.copyOfRange(key, i*4, (i*4)+4);   		
    		out[i] 	 = byteArray_to_int(b);
		}
		return out;
	}
    

	/**	g-Funktion wie aus dem Video "https://youtu.be/tvYHcS57Lq4?t=1654" Bei 27:50min wird diese g-Funktion erklärt. 	
	- Die g-Funktion transformiert zu erst jedes einzelne Byte mit der sBox. 
	- Danach findet eine Linksrotation der Bytes statt.
	- Danach wird das erste Byte mit dem Rundenzähler (round) ver-XOR´rt.
	@param in Eingangswert in die g-Funktion. Alle 4-Bytes des Integers werden verwendet.
	@param round Der Rundenzähler für die g-Funktiorn. Erste-Runde=1, zweite Runde=2, ...,  letzte Runde=15
	@return Ausgang der g-Funktion, alle 4-Bytes des Integers wurden berechnet.	**/
	private static int g(int in, int round)
	{
		byte[] b = int_To_4_ByteArray(in);
		int[] o = new int[4];		
		o[0] = getSBox(b[1],false);
		o[1] = getSBox(b[2],false);
		o[2] = getSBox(b[3],false);
		o[3] = getSBox(b[0],false);	
		o[0] = (o[0]^round);	
		return byteArray_to_int(o);		
	}

	
	/**	h-Funktion wie aus dem Video "https://youtu.be/tvYHcS57Lq4?t=2757" Bei 46:00min wird diese h-Funktion erklärt. 	
	- Die h-Funktion transformiert jedes einzelne Byte mit der sBox. 
	@param in Eingangswert in die h-Funktion. Alle 4-Bytes des Integers werden verwendet.
	@return Ausgang der h-Funktion, alle 4-Bytes des Integers wurden berechnet.	**/
	private static int h(int in)
	{
		byte[] b = int_To_4_ByteArray(in);
		int[] o = new int[4];		
		o[0] = getSBox(b[0],false);
		o[1] = getSBox(b[1],false);
		o[2] = getSBox(b[2],false);
		o[3] = getSBox(b[3],false);	
		return byteArray_to_int(o);		
	}
	
	
// -------------------------------------------------------------------------- Convert Hilfsmethoden ----------------------------------------------------------------
	
	private static int byteArray_to_int(byte[] b) 
	{
		if(b.length==1) return b[0]&0xff;
		if(b.length==2) return ((0xFF & b[0]) <<  8) |  (0xFF & b[1]);
		if(b.length==3) return ((0xFF & b[0]) << 16) | ((0xFF & b[1]) <<  8) |  (0xFF & b[2]);	
		if(b.length>=4) return ((0xFF & b[0]) << 24) | ((0xFF & b[1]) << 16) | ((0xFF & b[2]) << 8) | (0xFF & b[3]);
		return -1;
	}
	
	private static int byteArray_to_int(int[] b) 
	{
		if(b.length==1) return b[0]&0xff;
		if(b.length==2) return ((0xFF & b[0]) <<  8) |  (0xFF & b[1]);
		if(b.length==3) return ((0xFF & b[0]) << 16) | ((0xFF & b[1]) <<  8) |  (0xFF & b[2]);	
		if(b.length>=4) return ((0xFF & b[0]) << 24) | ((0xFF & b[1]) << 16) | ((0xFF & b[2]) << 8) | (0xFF & b[3]);
		return -1;
	}
	
	private static byte[] int_To_4_ByteArray(int data)
	{
		byte[] out = new byte[4];
		out[0] = (byte) ((data & 0xFF000000) >> 24);
		out[1] = (byte) ((data & 0x00FF0000) >> 16);
		out[2] = (byte) ((data & 0x0000FF00) >> 8);
		out[3] = (byte) ((data & 0x000000FF) >> 0);
		return out;
	}	
}
// Please note: All comments and documentation in this file have been translated from German to English.