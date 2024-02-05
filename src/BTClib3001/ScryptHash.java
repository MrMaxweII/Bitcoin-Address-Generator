package BTClib3001;



/********************************************************************************************************************************************************************
*	Version 1.0    														Autor: Mr. Maxwell   										vom 21.02.2023					*
*																																									*
*	ScryptHash ist ein Hash zur Laufzeitverlängerung der erhöhte CPU und Speicher Resourcen verbraucht.																*
*	ScryptHash benötigt nur die HMAC Kasse, sonnst keine Abhängigkeiten, der ScryptHash wird vollständig in dieser Klasse berechnet.								*
*	Es gibt nur eine statische Methode die den ScryptHash berechnet.																								*
*	Es werden mehrere Parameter benötigt mit denen die Laufzeit und die benötigten Resourcen angepasst werden.														*
*	Jede Änderung an den Parametern hat natürlich IMMER zur Folge, dass der ScryptHash sich ändert und damit inkompatibel zu vorherigen Berechnungen wird. 			*
*	Diese Klasse wurde mit einer Referenzimplementierung von bouncycastle aureichendt getestet, mehrere Tausend Random Testvektoren, mit verschiedenen Parametern.	*
*																																									*
*	Beschreibung der Parameter:																																		*
*	- data:	Die Zeichenkette, die gehasht werden soll																												*
*	- salt:	Eine Zeichenfolge, die den Hash zum Schutz vor Rainbow Table-Angriffen ändert																			*
*	- n:	CPU-/Speicherkostenparameter. Muss größer als 1 sein und eine Potenz von 2 und kleiner als 2^(128*r/8)  Standart = 2^14									*
*	- r:	Der Blockgröße-Parameter, mit dem die Größe und die Leistung des sequenziellen Speichers genau eingestellt werden. Muss größer 1 sein.					*
*	- p:	Parallelisierungsparameter; eine positive ganze Zahl p ≤ (232− 1).																						*
*	- outLen - Die Länge der Ausgabe in Bytes.																														*
*********************************************************************************************************************************************************************/



public class ScryptHash 
{
 
	
/**	@param data    	Input Data
   	@param salt     Salt
   	@param n        CPU cost parameter
   	@param r        Memory cost parameter
   	@param p        Parallelization parameter
   	@param outLen   Length out **/
    public static byte[] getHash(byte[] data, byte[] salt, int n, int r, int p, int outLen) throws Exception 
    {
        if (n < 2 || (n & (n - 1)) != 0) 	 throw new IllegalArgumentException("N is not a correct input parameter");
        if (n > Integer.MAX_VALUE / 128 / r) throw new IllegalArgumentException("N is not a correct input parameter");
        if (r > Integer.MAX_VALUE / 128 / p) throw new IllegalArgumentException("r is not a correct input parameter");
        byte[] out = new byte[outLen];     
        byte[] b = new byte[128*r*p];
        byte[] a = new byte[256*r];
        byte[] c = new byte[128*r*n];
        b = pbkdf2(data, salt, b, p*r*128);
        for (int i=0; i<p; i++) smix(b, i*128*r, r, n, c, a);      
        out = pbkdf2(data, b, out, outLen);
        return out;
    }
 
    
    
    
// --------------------------------------------- Private Methoden ------------------------------------------------------------
    
    
    private static void smix(byte[] b, int ir, int r, int n, byte[] c, byte[] a) 
    {
        System.arraycopy(b, ir, a, 0, 128*r);
        for (int i=0; i<n; i++) 
        {
        	System.arraycopy(a, 0, c, i*128*r, 128*r); bmix(a,r);
        }
        for (int i=0; i<n; i++) 
        {
            int j = toInt(a,r) & (n-1);
            xor(c, j*128*r, a, 128*r); bmix(a,r);
        }
        System.arraycopy(a, 0, b, ir, 128*r);
    }

    
    private static void bmix(byte[] a, int r) 
    {	
    	byte[] x = new byte[64];
        System.arraycopy(a, 128*r-64, x, 0, 64);
        for(int i=0; i<2*r; i++) 
        {
            xor(a, 64*i, x, 64);
            mixByte(x);
            System.arraycopy(x, 0, a, 128*r+i*64, 64);
        }
        for(int i=0; i<r; i++) System.arraycopy(a, 128*r+128*i,    a, 64*i,      64);     
        for(int i=0; i<r; i++) System.arraycopy(a, 128*r+128*i+64, a, 64*i+64*r, 64);      
    }

      
    private static void mixByte(byte[] b) 
    {
        int[] a = new int[16];
        int[] x = new int[16];
        for (int i=0; i<16; i++) 
        {
            a[i]  = (b[i*4]   & 0xff) << 0;
            a[i] |= (b[i*4+1] & 0xff) << 8;
            a[i] |= (b[i*4+2] & 0xff) << 16;
            a[i] |= (b[i*4+3] & 0xff) << 24;
        }
        System.arraycopy(a, 0, x, 0, 16);
        for (int i=8; i>0; i-=2) 
        {
            x[ 4] ^= rot(x[ 0]+x[12], 7);  x[ 8] ^= rot(x[ 4]+x[ 0], 9);
            x[12] ^= rot(x[ 8]+x[ 4],13);  x[ 0] ^= rot(x[12]+x[ 8],18);
            x[ 9] ^= rot(x[ 5]+x[ 1], 7);  x[13] ^= rot(x[ 9]+x[ 5], 9);
            x[ 1] ^= rot(x[13]+x[ 9],13);  x[ 5] ^= rot(x[ 1]+x[13],18);
            x[14] ^= rot(x[10]+x[ 6], 7);  x[ 2] ^= rot(x[14]+x[10], 9);
            x[ 6] ^= rot(x[ 2]+x[14],13);  x[10] ^= rot(x[ 6]+x[ 2],18);
            x[ 3] ^= rot(x[15]+x[11], 7);  x[ 7] ^= rot(x[ 3]+x[15], 9);
            x[11] ^= rot(x[ 7]+x[ 3],13);  x[15] ^= rot(x[11]+x[ 7],18);
            x[ 1] ^= rot(x[ 0]+x[ 3], 7);  x[ 2] ^= rot(x[ 1]+x[ 0], 9);
            x[ 3] ^= rot(x[ 2]+x[ 1],13);  x[ 0] ^= rot(x[ 3]+x[ 2],18);
            x[ 6] ^= rot(x[ 5]+x[ 4], 7);  x[ 7] ^= rot(x[ 6]+x[ 5], 9);
            x[ 4] ^= rot(x[ 7]+x[ 6],13);  x[ 5] ^= rot(x[ 4]+x[ 7],18);
            x[11] ^= rot(x[10]+x[ 9], 7);  x[ 8] ^= rot(x[11]+x[10], 9);
            x[ 9] ^= rot(x[ 8]+x[11],13);  x[10] ^= rot(x[ 9]+x[ 8],18);
            x[12] ^= rot(x[15]+x[14], 7);  x[13] ^= rot(x[12]+x[15], 9);
            x[14] ^= rot(x[13]+x[12],13);  x[15] ^= rot(x[14]+x[13],18);
        }
        for (int i=0; i<16; i++) a[i]=x[i]+a[i];
        for (int i=0; i<16; i++) 
        {
            b[i*4]   = (byte) (a[i] >> 0  & 0xff);
            b[i*4+1] = (byte) (a[i] >> 8  & 0xff);
            b[i*4+2] = (byte) (a[i] >> 16 & 0xff);
            b[i*4+3] = (byte) (a[i] >> 24 & 0xff);
        }
    }


    // PBKDF2 Schlüsselableitungsfunktion: "https://en.wikipedia.org/wiki/PBKDF2"
    private static byte[] pbkdf2(byte[] data, byte[] salt, byte[] c, int dkLen) throws Exception 
    {
    	int hLen = 32;  
        byte[] hash = new byte[hLen];
        byte[] blk  = new byte[salt.length+4];
        int l = (int) Math.ceil((double)dkLen / hLen);
        int r = dkLen-(l-1)*hLen;
        System.arraycopy(salt, 0, blk, 0, salt.length);
        for (int i=1; i<=l; i++) 
        {
            blk[salt.length]   = (byte) (i >> 24 & 0xff);
            blk[salt.length+1] = (byte) (i >> 16 & 0xff);
            blk[salt.length+2] = (byte) (i >> 8  & 0xff);
            blk[salt.length+3] = (byte) (i >> 0  & 0xff);          
            byte[] hmac = HMAC.getHMAC_SHA256(blk ,data);   
            System.arraycopy(hmac, 0, hash, 0, hLen); 
            System.arraycopy(hash, 0, c, (i-1)*hLen, i==l ? r:hLen);
        }
        return c;
    }  
      
    private static void xor(byte[] b1, int a, byte[] b2, int len) 
    {
        for (int i=0; i<len; i++) b2[i] ^= b1[a+i];      
    }
    
    private static int rot(int a, int b) 
    {
        return (a << b) | (a >>> (32 - b));
    }
       
    private static int toInt(byte[] b, int pos) 
    {
        int out;
        int i = (2*pos-1)*64;
        out  = (b[i]   & 0xff) << 0;
        out |= (b[i+1] & 0xff) << 8;
        out |= (b[i+2] & 0xff) << 16;
        out |= (b[i+3] & 0xff) << 24;
        return out;
    } 
}