package lib3001.ecdsa;
import java.math.BigInteger;



/***************************************************************************************
*   Math_Modulo Class V1.1   Autor: Mr. Maxwell          	05.11.2019					*
* 	LIB3001 Bibliotheks Klasse															*
*   Hier werden mathematische Berechnungen definiert über den Zahlenraum Modulo "p"  	*
****************************************************************************************/



public class Math_Modulo
{

final static BigInteger p   		= new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F",16);  	// Bitcoin Modulo: FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFE FFFFFC2F
final static BigInteger GENERATOR  	= new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798",16);  	// Bitcoin Generator Punkt  G =  02 79BE667E F9DCBBAC 55A06295 CE870B07 029BFCDB 2DCE28D9 59F2815B 16F81798
final static BigInteger GENERATORY 	= new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8",16);	// Generator Y-Koordinate
final static BigInteger ORDNUNG    	= new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16);  	// Ordnung n von G: (Modulo)
//final static BigInteger HALB       	= new BigInteger("7fffffffffffffffffffffffffffffff5d576e7357a4501ddfe92f46681b20a1",16);  	// Zahl 1/2, mit dem durch 2 dividiert werden kann.

static BigInteger ZERO = new BigInteger("0");
static BigInteger ONE  = new BigInteger("1");
static BigInteger TWO  = new BigInteger("2");
static BigInteger THREE= new BigInteger("3");
static BigInteger FOUR = new BigInteger("4");
static BigInteger FIVE = new BigInteger("5");
static BigInteger SIX  = new BigInteger("6");
static BigInteger SEVEN= new BigInteger("7");




/** Addiert a + b    2,3µs   */
public static BigInteger add(BigInteger a, BigInteger b)
{
	return a.add(b).mod(p);
}
public static BigInteger add(BigInteger a, BigInteger b, BigInteger c)
{
	return (a.add(b).add(c)).mod(p);
}
public static BigInteger add(BigInteger a, BigInteger b, BigInteger c, BigInteger d)
{
	return add(add(a,b),add(c,d));
}



/** negiert -a   0,9µs  */
public static BigInteger neg(BigInteger a)
{
	return p.subtract(a);
}

/** Subtrahiert a-b  */
public static BigInteger sub(BigInteger a, BigInteger b)
{
	return  add(a,neg(b));
}



/** Multipliziert a * b    7µs  */
public static BigInteger mul(BigInteger a, BigInteger b)
{
	return a.multiply(b).mod(p);
}
public static BigInteger mul(BigInteger a, BigInteger b, BigInteger c)
{
	return a.multiply(b).multiply(c).mod(p);
}
public static BigInteger mul(BigInteger a, BigInteger b, BigInteger c, BigInteger d)
{
	return mul(mul(a,b),mul(c,d).mod(p));
}



/** dividiert a/b   */
public static BigInteger div(BigInteger a, BigInteger b)
{
	return mul(a,inv(b));
}



/** Liefert  1/a   45µs  */
static BigInteger inv(BigInteger a)
{
	return a.modInverse(p);
}



/** Diese Funktion berechnet die Zahl 1/2 mit der auf der elliptischen Kurve durch zwei geteilt werden kann.  */
static BigInteger calcHalb(BigInteger a)
{
	return a.modInverse(ORDNUNG);
}



/** Potenz x^n    (sehr langsam!)   */
static BigInteger pow(BigInteger x, BigInteger n)
{
	return x.modPow(n,p);
}



/** Wurzel sqrt(a)     Tonelli–Shanks Algorithmus */
public static BigInteger sqrt(BigInteger n)
{
	BigInteger s = ZERO;
	BigInteger q = p.subtract(ONE);
	while (q.and(ONE).equals(ZERO)) { q=q.divide(TWO); s=s.add(ONE); }
	if (s.equals(ONE))
	{
		BigInteger r = pow(n, (p.add(ONE)).divide(FOUR));
		if (r.multiply(r).mod(p).equals(n)) return abs(r);
		{System.out.println("\nFehler!  sqrt("+n+") existiert nicht! (Math_Modulo.sqrt) \n");    return ZERO;}
	}

	// Find the first quadratic non-residue z by brute-force search
	BigInteger z = ZERO;
	while (!pow(z=z.add(ONE), (p.subtract(ONE)).divide(TWO)).equals(p.subtract(ONE)));
	BigInteger c = pow(z, q);
	BigInteger r = pow(n, q.add(ONE).divide(TWO));
	BigInteger t = pow(n, q);
	BigInteger m = s;
	while (!t.equals(ONE))
	{
		BigInteger tt = t;
		BigInteger i = ZERO;
		while (!tt.equals(ONE))
		{
			tt = (tt.multiply(tt).mod(p));
			i=i.add(ONE);
			if (i.equals(m))
			{
				if(!n.equals(ZERO)) System.out.println("\nFehler!  sqrt("+n+") existiert nicht! (Math_Modulo.sqrt) \n");
				return ZERO;
			}
		}
		BigInteger b = pow(c, pow(TWO, m.subtract(i).subtract(ONE)));
		BigInteger b2 = b.multiply(b).mod(p);
		r = r.multiply(b).mod(p);
		t = t.multiply(b2).mod(p);
		c = b2;
		m = i;
	}
	if (r.multiply(r).mod(p).equals(n)) return abs(r);
	return ZERO;
}


// liefer den Betrag |a|
private static BigInteger abs(BigInteger a)
{
	if(a.compareTo(p.subtract(ONE).divide(TWO))   == 1) return neg(a);
	return a;
}
}