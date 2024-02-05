package BTClib3001;
import java.util.Arrays;



/****************************************************************************************************************************************************
 * Version 1.0   						 Autor: Mr. Maxwell 				  	vom 20.02.2023														*
 * 																																					*
 * Ver- und Endschlüsselung mit Twofish 																											*
 * - CBC Mode; nur der CBC Modus ist hier implementiert																								*
 * - Kein Padding! Die Anwendung muss das Padding (das Auffüllen mit Bytes auf Blockgröße (128Bit) selbst implementieren!							*
 * - Diese Klasse benötigt keine externen Abhängigkeiten und läuft vollständig alleine in dieser Klasse.											*
 * - Diese Implementierung ist variabel und allgemein einsetzbar.																					*
 * - Diese Twofish Klasse verschlüsselt komform nach dem Twofish Protokoll von Bcruce Schneier (dem Erfinder von Twofish)							*
 * - Die Schiffren entsprechen denen, die von Bruce Schneier veröffentlicht sind: https://www.schneier.com/wp-content/uploads/2015/12/ecb_ival.txt	*
 * - Der Code wurde ausgiebig im Vergleich mit BouncyCastle getestet. Alle Schiffren sind stets gleich.												*
 * 																																					*
 * Anwendung: Es gibt zwei Hauptmethoden zur Ver- und Enschlüsselung:																				*
 * 		public static byte[] encrypt(byte[] txt, byte[] key, byte[] iv);																			*
 *  	public static byte[] decrypt(byte[] chiper, byte[] key, byte[] iv);																			*
 * Alle Ein- und Ausgaben dieser Methoden sind in Byte-Array zu erfolgen. 																			*
 * Achtung der iv-Vektor muss ein Zufallsvektor sein! Die Anwendung muss den 128Bit Zufallsvektor "iv" selbst erstellen.							*
 * Da kein Padding implementiert ist, muss der text oder die chiffre durch 16Byte ohne Rest teilbar sein. Also 16, 32, 48 ... Bytes lang!			*
 ****************************************************************************************************************************************************/



public class Twofish 
{

	
    
    /** Verschlüsselt im CBC-Mode no Padding
		@param txt Klartex als Byte-Array. Muss durch 16Byte ohne Rest teilbar sein!
		@param key Schlüssel, muss 16 oder 32 Byte lang sein
		@param iv Zufallsvektor, muss 16Byte lang sein
		@return Verschlüsselte Schiffre als byte-Array **/
    public static byte[] encrypt(byte[] txt, byte[] key, byte[] iv) throws Exception 
    {
        if (txt.length % 16 != 0) throw new Exception("InputError! txt size not correct");
        if (key.length % 16 != 0) throw new Exception("InputError! key size not correct");      
        if (iv.length % 16 != 0)  throw new Exception("InputError! iv size not correct");      
        int[] ivi = split(iv);
        byte[] out = new byte[txt.length];
        for (int i = 0; i < txt.length; i=i+16) 
        {
    		byte[] txtBlock = Arrays.copyOfRange(txt, i, i+16); 
        	int[]  data  = add(ivi, split(txtBlock));
        	byte[] b = blockEncrypt(data,  key);
            System.arraycopy(b, 0, out ,i , 16);
            ivi = split(b);
        }
        return out;
    }

    
    
    
    
    /** Endschlüsselt im CBC-Mode no Padding
		@param chiper Schiffre als Byte-Array. Muss durch 16Byte ohne Rest teilbar sein!
		@param key Schlüssel, muss 16 oder 32 Byte lang sein
		@param iv Zufallsvektor, muss 16Byte lang sein
		@return Entschlüsselter Kartext als byte-Array **/
    public static byte[] decrypt(byte[] chiper, byte[] key, byte[] iv) throws Exception 
    {
    	if (chiper.length % 16 != 0) 	throw new Exception("InputError! cipher size not correct");
        if (key.length % 16 != 0) 		throw new Exception("InputError! key size not correct"); 
        if (iv.length % 16 != 0)  		throw new Exception("InputError! iv size not correct");      
        int[] ivi = split(iv);
        byte[] out = new byte[chiper.length];
    	byte[] chBlock = null;
        for (int i = 0; i < chiper.length; i=i+16) 
        {
        	if(i>=16) ivi = split(chBlock); 
        	chBlock = Arrays.copyOfRange(chiper, i, i+16); 
        	byte[] b = blockDecrypt(split(chBlock),  key);
        	int[]  data  = add(ivi, split(b));
        	b = fourIntToByteArray(data);
            System.arraycopy(b, 0, out ,i , 16);     	
        }
        return out;
    }


    
    
    // -------------------------------------------------------- private Methoden ---------------------------------------------------------- 
    
  

    
    // Verschlüsselt einen Block
    private static byte[] blockEncrypt(int[] txt, byte[] key) throws Exception 
    { 	
    	int[] data = swapBytes(txt);
    	int[] sKey = keyExpand(key);
    	data[3] ^= sKey[0];
    	data[2] ^= sKey[1];
    	data[1] ^= sKey[2];
    	data[0] ^= sKey[3];
        int t0, t1;
        int k = 8;
        for (int i = 0; i < 8; i++) 
        {
            t0 = sBoxMul(sBox, data[3], 0);
            t1 = sBoxMul(sBox, data[2], 3);
            data[1] ^= t0 + t1 + sKey[k++];
            data[1] = data[1] >>> 1 | data[1] << 31;
        	data[0] = data[0] << 1 | data[0] >>> 31;
        	data[0] ^= t0 + 2 * t1 + sKey[k++];
            t0 = sBoxMul(sBox, data[1], 0);
            t1 = sBoxMul(sBox, data[0], 3);
            data[3] ^= t0 + t1 + sKey[k++];
            data[3] = data[3] >>> 1 | data[3] << 31;
        	data[2] = data[2] << 1 | data[2] >>> 31;
        	data[2] ^= t0 + 2 * t1 + sKey[k++];
        }
        data[1] ^= sKey[4];
        data[0] ^= sKey[5];
        data[3] ^= sKey[6];
        data[2] ^= sKey[7];
        byte[] out = new byte[]
        {
	        (byte) data[1], (byte) (data[1] >>> 8), (byte) (data[1] >>> 16), (byte) (data[1] >>> 24),
	        (byte) data[0], (byte) (data[0] >>> 8), (byte) (data[0] >>> 16), (byte) (data[0] >>> 24),
	        (byte) data[3], (byte) (data[3] >>> 8), (byte) (data[3] >>> 16), (byte) (data[3] >>> 24),
	        (byte) data[2], (byte) (data[2] >>> 8), (byte) (data[2] >>> 16), (byte) (data[2] >>> 24),
        };  
        return out;
    }
    
    
    
 
	// Entschlüsselt einen Block
    private static byte[] blockDecrypt(int[] chiffre, byte[] key) throws Exception 
    {   
    	int[] data = swapBytes(chiffre);
    	int[] sKey = keyExpand(key);
    	data[3] ^= sKey[4];
        data[2] ^= sKey[5];
        data[1] ^= sKey[6];
        data[0] ^= sKey[7];
        int k = 39;
        int t0, t1;
        for (int i = 0; i < 8; i++) 
        {
            t0 = sBoxMul(sBox, data[3], 0);
            t1 = sBoxMul(sBox, data[2], 3);
            data[0] ^= t0 + 2 * t1 + sKey[k--];
            data[0] = data[0] >>> 1 | data[0] << 31;
            data[1] = data[1] << 1 | data[1] >>> 31;
            data[1] ^= t0 + t1 + sKey[k--];
            t0 = sBoxMul(sBox, data[1], 0);
            t1 = sBoxMul(sBox, data[0], 3);
            data[2] ^= t0 + 2 * t1 + sKey[k--];
            data[2] = data[2] >>> 1 | data[2] << 31;
            data[3] = data[3] << 1 | data[3] >>> 31;
            data[3] ^= t0 + t1 + sKey[k--];
        }
        data[1] ^= sKey[0];
        data[0] ^= sKey[1];
        data[3] ^= sKey[2];
        data[2] ^= sKey[3];
        byte[] out = new byte[]
        {
	        (byte) data[1], (byte) (data[1] >>> 8), (byte) (data[1] >>> 16), (byte) (data[1] >>> 24),
	        (byte) data[0], (byte) (data[0] >>> 8), (byte) (data[0] >>> 16), (byte) (data[0] >>> 24),
	        (byte) data[3], (byte) (data[3] >>> 8), (byte) (data[3] >>> 16), (byte) (data[3] >>> 24),
	        (byte) data[2], (byte) (data[2] >>> 8), (byte) (data[2] >>> 16), (byte) (data[2] >>> 24),
        };
        return out;
    }
    
  
    
    
    
    
    
 // -------------------------------------------------------------- Key Expand ---------------------------------------------------------------    

    
    private static int[] sBox = new int[1024];   // Die S-Box

    
    // Erstellt 30 Teilschlüssel und die sBox	
    private static int[] keyExpand(byte[] key)throws Exception 
    {
        int len = key.length;
        int cn = len / 8;
        int subkeyCnt = 40;
        int[] k32e = new int[4]; 
        int[] k32o = new int[4]; 
        int[] sKey = new int[4];
        int i, j, pos = 0;
        for (i = 0, j = cn - 1; i < 4 && pos < len; i++, j--) 
        {
            k32e[i] = (key[pos++] & 0xFF) | (key[pos++] & 0xFF) << 8 |(key[pos++] & 0xFF) << 16 | (key[pos++] & 0xFF) << 24;
            k32o[i] = (key[pos++] & 0xFF) | (key[pos++] & 0xFF) << 8 | (key[pos++] & 0xFF) << 16 |(key[pos++] & 0xFF) << 24;
            sKey[j] = reedSolomonEncode(k32e[i], k32o[i]); 
        }
        int q, A, B;
        int[] out = new int[subkeyCnt];
        for (i = q = 0; i < subkeyCnt / 2; i++, q += 0x02020202) 
        {
            A = h(cn, q, k32e); 
            B = h(cn, q + 0x01010101, k32o); 
            B = B << 8 | B >>> 24;
            A += B;
            out[2 * i] = A;              
            A += B;
            out[2 * i + 1] = A << 9 | A >>> 23;
        }
        int k0 = sKey[0];
        int k1 = sKey[1];
        int k2 = sKey[2];
        int k3 = sKey[3];
        int b0, b1, b2, b3;
        for (i = 0; i < 256; i++) 
        {
            b0 = b1 = b2 = b3 = i;
            switch (cn & 3) 
            {
                case 1:
                    sBox[2 * i] 			= MDS[0][(S[0][b0] & 0xFF) ^ (k0 & 0xFF)];
                    sBox[2 * i + 1] 		= MDS[1][(S[0][b1] & 0xFF) ^ ((k0 >>> 8) & 0xFF)];
                    sBox[0x200 + 2 * i] 	= MDS[2][(S[1][b2] & 0xFF) ^ ((k0 >>> 16) & 0xFF)];
                    sBox[0x200 + 2 * i + 1] = MDS[3][(S[1][b3] & 0xFF) ^ ((k0 >>> 24) & 0xFF)];
                    break;
                case 0: 
                    b0 = (S[1][b0] & 0xFF) ^ (k3 & 0xFF);
                    b1 = (S[0][b1] & 0xFF) ^ ((k3 >>> 8) & 0xFF);
                    b2 = (S[0][b2] & 0xFF) ^ ((k3 >>> 16) & 0xFF);
                    b3 = (S[1][b3] & 0xFF) ^ ((k3 >>> 24) & 0xFF);
                case 3:
                    b0 = (S[1][b0] & 0xFF) ^ (k2 & 0xFF);
                    b1 = (S[1][b1] & 0xFF) ^ ((k2 >>> 8) & 0xFF);
                    b2 = (S[0][b2] & 0xFF) ^ ((k2 >>> 16) & 0xFF);
                    b3 = (S[0][b3] & 0xFF) ^ ((k2 >>> 24) & 0xFF);
                case 2: 
                    sBox[2 * i] 			= MDS[0][(S[0][(S[0][b0] & 0xFF) ^ (k1 & 0xFF)] & 0xFF) ^ (k0 & 0xFF)];
                    sBox[2 * i + 1] 		= MDS[1][(S[0][(S[1][b1] & 0xFF) ^ ((k1 >>> 8) & 0xFF)] & 0xFF) ^ ((k0 >>> 8) & 0xFF)];
                    sBox[0x200 + 2 * i] 	= MDS[2][(S[1][(S[0][b2] & 0xFF) ^ ((k1 >>> 16) & 0xFF)] & 0xFF) ^ ((k0 >>> 16) & 0xFF)];
                    sBox[0x200 + 2 * i + 1] = MDS[3][(S[1][(S[1][b3] & 0xFF) ^ ((k1 >>> 24) & 0xFF)] & 0xFF) ^ ((k0 >>> 24) & 0xFF)];
            }
        }
        return out;
    }
    
    
    
    
    
    
 // ---------------------------------------------------------  Hilfs Methoden ------------------------------------------------------------------- 
    
    
    
	// 16Byte Array wird in 4 Integer-Zahlen aufgeteilt
	private static int[] split(byte[] key)
	{
		int[] out = new int[4];
		for(int i=0; i<4; i++)
		{
    		byte[] b = Arrays.copyOfRange(key, i*4, (i*4)+4);   		
    		out[i] 	 = byteArray_to_int(b);
		}
		return out;
	}
    
	
	// Wandelt 4 Integer (Int-Array mit genau 4 Integer) nach Byte-Array.
	private static byte[] fourIntToByteArray(int[] in)
	{
		byte[] out = new byte[16];
		byte[] b1 = int_To_4_ByteArray(in[0]);
		byte[] b2 = int_To_4_ByteArray(in[1]);
		byte[] b3 = int_To_4_ByteArray(in[2]);
		byte[] b4 = int_To_4_ByteArray(in[3]);			
		System.arraycopy(b1, 0, out, 0, 4);
		System.arraycopy(b2, 0, out, 4, 4);
		System.arraycopy(b3, 0, out, 8, 4);
		System.arraycopy(b4, 0, out, 12, 4);
		return out;
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
	
	
	private static int byteArray_to_int(byte[] b) 
	{
		if(b.length==1) return b[0]&0xff;
		if(b.length==2) return ((0xFF & b[0]) <<  8) |  (0xFF & b[1]);
		if(b.length==3) return ((0xFF & b[0]) << 16) | ((0xFF & b[1]) <<  8) |  (0xFF & b[2]);	
		if(b.length>=4) return ((0xFF & b[0]) << 24) | ((0xFF & b[1]) << 16) | ((0xFF & b[2]) << 8) | (0xFF & b[3]);
		return -1;
	}
		
	
	// Additions Methode, z.B: iv wird mit data addiert (XOR)
	// Alle In/Out daten sind 4 Integer Zahlen in einem Array
	private static int[] add(int[] in, int[] data)
	{
		int[] out = new int[4];
		for(int i=0;i<4;i++)
		{
			out[i] = in[i] ^ data[i];
		}
		return out;
	}
	
	
	// Dreht die Byte-Reihenfolge eines Integer-Arrays um. 
    private static int[] swapBytes(int[] in) 
    {
    	int[] out = in.clone();
    	if (out == null) return null;
    	int i = 0;
    	int j = out.length - 1;
    	int tmp;
    	while (j > i) 
    	{
    		tmp = Integer.reverseBytes(out[j]);
    		out[j] = Integer.reverseBytes(out[i]);
    		out[i] = tmp;
    		
    		j--;
    		i++;
    	}
    	return out;
    }
	
    
    // h-Funktion 
    private static int h(int keyLen, int in, int[] ii) 
    {
        int b0 = in & 0xFF;
        int b1 = ((in >>> 8) & 0xFF);
        int b2 = ((in >>> 16) & 0xFF);
        int b3 = ((in >>> 24) & 0xFF);
        int k0 = ii[0];
        int k1 = ii[1];
        int k2 = ii[2];
        int k3 = ii[3];
        int out = 0;
        switch (keyLen & 3) 
        {
            case 1:
                out = MDS[0][(S[0][b0] & 0xFF) ^ (k0 & 0xFF)] ^ MDS[1][(S[0][b1] & 0xFF) ^ ((k0 >>> 8) & 0xFF)] ^ MDS[2][(S[1][b2] & 0xFF) ^ ((k0 >>> 16) & 0xFF)] ^ MDS[3][(S[1][b3] & 0xFF) ^ ((k0 >>> 24) & 0xFF)];
                break;
            case 0:  
                b0 = (S[1][b0] & 0xFF) ^ (k3 & 0xFF);
                b1 = (S[0][b1] & 0xFF) ^ ((k3 >>> 8) & 0xFF);
                b2 = (S[0][b2] & 0xFF) ^ ((k3 >>> 16) & 0xFF);
                b3 = (S[1][b3] & 0xFF) ^ ((k3 >>> 24) & 0xFF);
            case 3:
                b0 = (S[1][b0] & 0xFF) ^ (k2 & 0xFF);
                b1 = (S[1][b1] & 0xFF) ^ ((k2 >>> 8) & 0xFF);
                b2 = (S[0][b2] & 0xFF) ^ ((k2 >>> 16) & 0xFF);
                b3 = (S[0][b3] & 0xFF) ^ ((k2 >>> 24) & 0xFF);
            case 2:                             // 128-bit 
                out =  MDS[0][(S[0][(S[0][b0] & 0xFF) ^ (k1 & 0xFF)] & 0xFF) ^ (k0 & 0xFF)] ^ MDS[1][(S[0][(S[1][b1] & 0xFF) ^ ((k1 >>> 8) & 0xFF)] & 0xFF) ^ ((k0 >>> 8) & 0xFF)] ^ MDS[2][(S[1][(S[0][b2] & 0xFF) ^ ((k1 >>> 16) & 0xFF)] & 0xFF) ^ ((k0 >>> 16) & 0xFF)] ^ MDS[3][(S[1][(S[1][b3] & 0xFF) ^ ((k1 >>> 24) & 0xFF)] & 0xFF) ^ ((k0 >>> 24) & 0xFF)];
                break;
        }
        return out;
    }
 
    
    // Reed-Solomon Encode
    private static int reedSolomonEncode(int n, int r) 
    {
        for (int i = 0; i < 4; i++) r = reedSol(r);
        r = r ^ n;
        for (int i = 0; i < 4; i++) r = reedSol(r);
        return r;
    }

   
    // Reed-Solomon code 
    private static int reedSol(int x) 
    {
        int a = (x >>> 24) & 0xFF;
        int b = ((a << 1) ^ ((a & 0x80) != 0 ? 0x14D : 0)) & 0xFF;
        int c = (a >>> 1) ^ ((a & 0x01) != 0 ? (0x14D >>> 1) : 0) ^ b;
        int out = (x << 8) ^ (c << 24) ^ (b << 16) ^ (c << 8) ^ a;
        return out;
    }


    // Bits n from x
    private static int bits(int x, int n) 
    {
        int out = 0;
        switch (n % 4) 
        {
            case 0: out = (x & 0xFF);		   break;
            case 1: out = ((x >>> 8) & 0xFF);  break;
            case 2: out = ((x >>> 16) & 0xFF); break;
            case 3: out = ((x >>> 24) & 0xFF); break;
        }
        return out;
    }

    
    // Multiplikation mit S-Box
    private static int sBoxMul(int[] sBox, int x, int R) 
    {
        return sBox[2 * bits(x, R)] ^ sBox[2 * bits(x, R + 1) + 1] ^ sBox[0x200 + 2 * bits(x, R + 2)] ^ sBox[0x200 + 2 * bits(x, R + 3) + 1];
    }

  
    private static final byte[][] S = new byte[][]
    {
            {  
                    (byte) 0xA9, (byte) 0x67, (byte) 0xB3, (byte) 0xE8,
                    (byte) 0x04, (byte) 0xFD, (byte) 0xA3, (byte) 0x76,
                    (byte) 0x9A, (byte) 0x92, (byte) 0x80, (byte) 0x78,
                    (byte) 0xE4, (byte) 0xDD, (byte) 0xD1, (byte) 0x38,
                    (byte) 0x0D, (byte) 0xC6, (byte) 0x35, (byte) 0x98,
                    (byte) 0x18, (byte) 0xF7, (byte) 0xEC, (byte) 0x6C,
                    (byte) 0x43, (byte) 0x75, (byte) 0x37, (byte) 0x26,
                    (byte) 0xFA, (byte) 0x13, (byte) 0x94, (byte) 0x48,
                    (byte) 0xF2, (byte) 0xD0, (byte) 0x8B, (byte) 0x30,
                    (byte) 0x84, (byte) 0x54, (byte) 0xDF, (byte) 0x23,
                    (byte) 0x19, (byte) 0x5B, (byte) 0x3D, (byte) 0x59,
                    (byte) 0xF3, (byte) 0xAE, (byte) 0xA2, (byte) 0x82,
                    (byte) 0x63, (byte) 0x01, (byte) 0x83, (byte) 0x2E,
                    (byte) 0xD9, (byte) 0x51, (byte) 0x9B, (byte) 0x7C,
                    (byte) 0xA6, (byte) 0xEB, (byte) 0xA5, (byte) 0xBE,
                    (byte) 0x16, (byte) 0x0C, (byte) 0xE3, (byte) 0x61,
                    (byte) 0xC0, (byte) 0x8C, (byte) 0x3A, (byte) 0xF5,
                    (byte) 0x73, (byte) 0x2C, (byte) 0x25, (byte) 0x0B,
                    (byte) 0xBB, (byte) 0x4E, (byte) 0x89, (byte) 0x6B,
                    (byte) 0x53, (byte) 0x6A, (byte) 0xB4, (byte) 0xF1,
                    (byte) 0xE1, (byte) 0xE6, (byte) 0xBD, (byte) 0x45,
                    (byte) 0xE2, (byte) 0xF4, (byte) 0xB6, (byte) 0x66,
                    (byte) 0xCC, (byte) 0x95, (byte) 0x03, (byte) 0x56,
                    (byte) 0xD4, (byte) 0x1C, (byte) 0x1E, (byte) 0xD7,
                    (byte) 0xFB, (byte) 0xC3, (byte) 0x8E, (byte) 0xB5,
                    (byte) 0xE9, (byte) 0xCF, (byte) 0xBF, (byte) 0xBA,
                    (byte) 0xEA, (byte) 0x77, (byte) 0x39, (byte) 0xAF,
                    (byte) 0x33, (byte) 0xC9, (byte) 0x62, (byte) 0x71,
                    (byte) 0x81, (byte) 0x79, (byte) 0x09, (byte) 0xAD,
                    (byte) 0x24, (byte) 0xCD, (byte) 0xF9, (byte) 0xD8,
                    (byte) 0xE5, (byte) 0xC5, (byte) 0xB9, (byte) 0x4D,
                    (byte) 0x44, (byte) 0x08, (byte) 0x86, (byte) 0xE7,
                    (byte) 0xA1, (byte) 0x1D, (byte) 0xAA, (byte) 0xED,
                    (byte) 0x06, (byte) 0x70, (byte) 0xB2, (byte) 0xD2,
                    (byte) 0x41, (byte) 0x7B, (byte) 0xA0, (byte) 0x11,
                    (byte) 0x31, (byte) 0xC2, (byte) 0x27, (byte) 0x90,
                    (byte) 0x20, (byte) 0xF6, (byte) 0x60, (byte) 0xFF,
                    (byte) 0x96, (byte) 0x5C, (byte) 0xB1, (byte) 0xAB,
                    (byte) 0x9E, (byte) 0x9C, (byte) 0x52, (byte) 0x1B,
                    (byte) 0x5F, (byte) 0x93, (byte) 0x0A, (byte) 0xEF,
                    (byte) 0x91, (byte) 0x85, (byte) 0x49, (byte) 0xEE,
                    (byte) 0x2D, (byte) 0x4F, (byte) 0x8F, (byte) 0x3B,
                    (byte) 0x47, (byte) 0x87, (byte) 0x6D, (byte) 0x46,
                    (byte) 0xD6, (byte) 0x3E, (byte) 0x69, (byte) 0x64,
                    (byte) 0x2A, (byte) 0xCE, (byte) 0xCB, (byte) 0x2F,
                    (byte) 0xFC, (byte) 0x97, (byte) 0x05, (byte) 0x7A,
                    (byte) 0xAC, (byte) 0x7F, (byte) 0xD5, (byte) 0x1A,
                    (byte) 0x4B, (byte) 0x0E, (byte) 0xA7, (byte) 0x5A,
                    (byte) 0x28, (byte) 0x14, (byte) 0x3F, (byte) 0x29,
                    (byte) 0x88, (byte) 0x3C, (byte) 0x4C, (byte) 0x02,
                    (byte) 0xB8, (byte) 0xDA, (byte) 0xB0, (byte) 0x17,
                    (byte) 0x55, (byte) 0x1F, (byte) 0x8A, (byte) 0x7D,
                    (byte) 0x57, (byte) 0xC7, (byte) 0x8D, (byte) 0x74,
                    (byte) 0xB7, (byte) 0xC4, (byte) 0x9F, (byte) 0x72,
                    (byte) 0x7E, (byte) 0x15, (byte) 0x22, (byte) 0x12,
                    (byte) 0x58, (byte) 0x07, (byte) 0x99, (byte) 0x34,
                    (byte) 0x6E, (byte) 0x50, (byte) 0xDE, (byte) 0x68,
                    (byte) 0x65, (byte) 0xBC, (byte) 0xDB, (byte) 0xF8,
                    (byte) 0xC8, (byte) 0xA8, (byte) 0x2B, (byte) 0x40,
                    (byte) 0xDC, (byte) 0xFE, (byte) 0x32, (byte) 0xA4,
                    (byte) 0xCA, (byte) 0x10, (byte) 0x21, (byte) 0xF0,
                    (byte) 0xD3, (byte) 0x5D, (byte) 0x0F, (byte) 0x00,
                    (byte) 0x6F, (byte) 0x9D, (byte) 0x36, (byte) 0x42,
                    (byte) 0x4A, (byte) 0x5E, (byte) 0xC1, (byte) 0xE0
            },
            {  
                    (byte) 0x75, (byte) 0xF3, (byte) 0xC6, (byte) 0xF4,
                    (byte) 0xDB, (byte) 0x7B, (byte) 0xFB, (byte) 0xC8,
                    (byte) 0x4A, (byte) 0xD3, (byte) 0xE6, (byte) 0x6B,
                    (byte) 0x45, (byte) 0x7D, (byte) 0xE8, (byte) 0x4B,
                    (byte) 0xD6, (byte) 0x32, (byte) 0xD8, (byte) 0xFD,
                    (byte) 0x37, (byte) 0x71, (byte) 0xF1, (byte) 0xE1,
                    (byte) 0x30, (byte) 0x0F, (byte) 0xF8, (byte) 0x1B,
                    (byte) 0x87, (byte) 0xFA, (byte) 0x06, (byte) 0x3F,
                    (byte) 0x5E, (byte) 0xBA, (byte) 0xAE, (byte) 0x5B,
                    (byte) 0x8A, (byte) 0x00, (byte) 0xBC, (byte) 0x9D,
                    (byte) 0x6D, (byte) 0xC1, (byte) 0xB1, (byte) 0x0E,
                    (byte) 0x80, (byte) 0x5D, (byte) 0xD2, (byte) 0xD5,
                    (byte) 0xA0, (byte) 0x84, (byte) 0x07, (byte) 0x14,
                    (byte) 0xB5, (byte) 0x90, (byte) 0x2C, (byte) 0xA3,
                    (byte) 0xB2, (byte) 0x73, (byte) 0x4C, (byte) 0x54,
                    (byte) 0x92, (byte) 0x74, (byte) 0x36, (byte) 0x51,
                    (byte) 0x38, (byte) 0xB0, (byte) 0xBD, (byte) 0x5A,
                    (byte) 0xFC, (byte) 0x60, (byte) 0x62, (byte) 0x96,
                    (byte) 0x6C, (byte) 0x42, (byte) 0xF7, (byte) 0x10,
                    (byte) 0x7C, (byte) 0x28, (byte) 0x27, (byte) 0x8C,
                    (byte) 0x13, (byte) 0x95, (byte) 0x9C, (byte) 0xC7,
                    (byte) 0x24, (byte) 0x46, (byte) 0x3B, (byte) 0x70,
                    (byte) 0xCA, (byte) 0xE3, (byte) 0x85, (byte) 0xCB,
                    (byte) 0x11, (byte) 0xD0, (byte) 0x93, (byte) 0xB8,
                    (byte) 0xA6, (byte) 0x83, (byte) 0x20, (byte) 0xFF,
                    (byte) 0x9F, (byte) 0x77, (byte) 0xC3, (byte) 0xCC,
                    (byte) 0x03, (byte) 0x6F, (byte) 0x08, (byte) 0xBF,
                    (byte) 0x40, (byte) 0xE7, (byte) 0x2B, (byte) 0xE2,
                    (byte) 0x79, (byte) 0x0C, (byte) 0xAA, (byte) 0x82,
                    (byte) 0x41, (byte) 0x3A, (byte) 0xEA, (byte) 0xB9,
                    (byte) 0xE4, (byte) 0x9A, (byte) 0xA4, (byte) 0x97,
                    (byte) 0x7E, (byte) 0xDA, (byte) 0x7A, (byte) 0x17,
                    (byte) 0x66, (byte) 0x94, (byte) 0xA1, (byte) 0x1D,
                    (byte) 0x3D, (byte) 0xF0, (byte) 0xDE, (byte) 0xB3,
                    (byte) 0x0B, (byte) 0x72, (byte) 0xA7, (byte) 0x1C,
                    (byte) 0xEF, (byte) 0xD1, (byte) 0x53, (byte) 0x3E,
                    (byte) 0x8F, (byte) 0x33, (byte) 0x26, (byte) 0x5F,
                    (byte) 0xEC, (byte) 0x76, (byte) 0x2A, (byte) 0x49,
                    (byte) 0x81, (byte) 0x88, (byte) 0xEE, (byte) 0x21,
                    (byte) 0xC4, (byte) 0x1A, (byte) 0xEB, (byte) 0xD9,
                    (byte) 0xC5, (byte) 0x39, (byte) 0x99, (byte) 0xCD,
                    (byte) 0xAD, (byte) 0x31, (byte) 0x8B, (byte) 0x01,
                    (byte) 0x18, (byte) 0x23, (byte) 0xDD, (byte) 0x1F,
                    (byte) 0x4E, (byte) 0x2D, (byte) 0xF9, (byte) 0x48,
                    (byte) 0x4F, (byte) 0xF2, (byte) 0x65, (byte) 0x8E,
                    (byte) 0x78, (byte) 0x5C, (byte) 0x58, (byte) 0x19,
                    (byte) 0x8D, (byte) 0xE5, (byte) 0x98, (byte) 0x57,
                    (byte) 0x67, (byte) 0x7F, (byte) 0x05, (byte) 0x64,
                    (byte) 0xAF, (byte) 0x63, (byte) 0xB6, (byte) 0xFE,
                    (byte) 0xF5, (byte) 0xB7, (byte) 0x3C, (byte) 0xA5,
                    (byte) 0xCE, (byte) 0xE9, (byte) 0x68, (byte) 0x44,
                    (byte) 0xE0, (byte) 0x4D, (byte) 0x43, (byte) 0x69,
                    (byte) 0x29, (byte) 0x2E, (byte) 0xAC, (byte) 0x15,
                    (byte) 0x59, (byte) 0xA8, (byte) 0x0A, (byte) 0x9E,
                    (byte) 0x6E, (byte) 0x47, (byte) 0xDF, (byte) 0x34,
                    (byte) 0x35, (byte) 0x6A, (byte) 0xCF, (byte) 0xDC,
                    (byte) 0x22, (byte) 0xC9, (byte) 0xC0, (byte) 0x9B,
                    (byte) 0x89, (byte) 0xD4, (byte) 0xED, (byte) 0xAB,
                    (byte) 0x12, (byte) 0xA2, (byte) 0x0D, (byte) 0x52,
                    (byte) 0xBB, (byte) 0x02, (byte) 0x2F, (byte) 0xA9,
                    (byte) 0xD7, (byte) 0x61, (byte) 0x1E, (byte) 0xB4,
                    (byte) 0x50, (byte) 0x04, (byte) 0xF6, (byte) 0xC2,
                    (byte) 0x16, (byte) 0x25, (byte) 0x86, (byte) 0x56,
                    (byte) 0x55, (byte) 0x09, (byte) 0xBE, (byte) 0x91
            }
    };
    
    
    private static final int[][] MDS = new int[4][256]; 
    static 
    {
        int[] m1 = new int[2];
        int[] mX = new int[2];
        int[] mY = new int[2];
        int i, j;
        for (i = 0; i < 256; i++) 
        {
            j = S[0][i] & 0xFF; 
            m1[0] = j;
            mX[0] = (j ^ ((j >> 2) ^ ((j & 0x02) != 0 ? 180 : 0) ^ ((j & 0x01) != 0 ? 90 : 0))) & 0xFF;
            mY[0] = (j ^ ((j >> 1) ^ ((j & 0x01) != 0 ? 180 : 0)) ^ ((j >> 2) ^ ((j & 0x02) != 0 ? 180 : 0) ^ ((j & 0x01) != 0 ? 90 : 0))) & 0xFF;
            j = S[1][i] & 0xFF;
            m1[1] = j;
            mX[1] = (j ^ ((j >> 2) ^ ((j & 0x02) != 0 ? 180 : 0) ^ ((j & 0x01) != 0 ? 90 : 0))) & 0xFF;
            mY[1] = (j ^ ((j >> 1) ^ ((j & 0x01) != 0 ? 180 : 0)) ^ ((j >> 2) ^ ((j & 0x02) != 0 ? 180 : 0) ^ ((j & 0x01) != 0 ? 90 : 0))) & 0xFF;
            MDS[0][i] = m1[1] | mX[1] << 8 | mY[1] << 16 | mY[1] << 24;
            MDS[1][i] = mY[0] | mY[0] << 8 | mX[0] << 16 | m1[0] << 24;
            MDS[2][i] = mX[1] | mY[1] << 8 | m1[1] << 16 | mY[1] << 24;
            MDS[3][i] = mX[0] | m1[0] << 8 | mY[0] << 16 | mX[0] << 24;
        }
    }  
}