package lib3001.ecdsa;
import java.math.BigInteger;
import java.util.Arrays;

import lib3001.crypt.Convert;



/********************************************************************************************
* 	Secp256k1  V2.5        Autor: Mr. Maxwell     						31.01.2023			*
* 	LIB3001 Bibliotheks Klasse																*
*	- Multipliziert einen Faktor mit einem Punkt auf der elliptischen Kurve.				*
*	- Generiert den Pub.Key durch die Multiplikation von "G" mit dem Priv.Key.				*
*	- Erzeugt ECDSA Signatur																*
*	- Verifiziert ECDSA Signatur															*
********************************************************************************************/



public class Secp256k1
{
	public final static BigInteger ModuloHalb  = new BigInteger("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF7FFFFE17",16);
	public final static BigInteger GENERATOR   = new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798",16);
	public final static BigInteger GENERATORY  = new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8",16);
	public final static BigInteger ORDNUNG     = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16);
	public final static BigInteger HALB        = new BigInteger("7fffffffffffffffffffffffffffffff5d576e7357a4501ddfe92f46681b20a1",16);

	public final static BigInteger ZERO = new BigInteger("0");
	public final static BigInteger ONE  = new BigInteger("1");
	public final static BigInteger TWO  = new BigInteger("2");
	public final static BigInteger THREE= new BigInteger("3");
	public final static BigInteger FOUR = new BigInteger("4");
	public final static BigInteger FIVE = new BigInteger("5");
	public final static BigInteger SIX  = new BigInteger("6");
	public final static BigInteger SEVEN= new BigInteger("7");




/**	Mit dem Konstruktor wird die EXP-List initialisiert.
 	Dies ist für die Multiplikation mit "G" notwendig!  **/
public Secp256k1()
{
	EXPList.set_EXP_List();
}



/**	Es wird eine Signatur erstellt bestehend aus den Teilen "r" und "s".
	Übergeben wird der 32byte lange Hash, der signiert werden soll,
	- der Priv.Key 32Byte,
	- die "rand" Zufallszahl "k" als ByteArray.
	Rückgabe ist ein BigInteger-Array bestehend aus 2 Elementen: [0] = r   und    [1] = s.
	Achtung: Die "rand" Zufallszahl "k" muss aus einer kryptographisch starken Entropie stammen!
	Falls "k" vorhersehbar ist, kann der Priv.Key leicht aufgedeckt werden!!! */
public BigInteger[] sig(byte[] hash, byte[] privKey, byte[] k)
{
	byte[] ran = to_fixLength(k,32);
	if(ran[0]<0)
	{
		ran = Arrays.copyOf(ran, 31);
		ran = to_fixLength(ran,32);
	}
	BigInteger rand = new BigInteger(1,ran);
	BigInteger[] out= new BigInteger[2];
	BigInteger r  = multiply_G(rand)[0];
	BigInteger r_x_priv	=	r.multiply(new BigInteger(1,privKey)).mod(ORDNUNG);
	BigInteger zähler	=	(new BigInteger(1,hash).add(r_x_priv)).mod(ORDNUNG);
	BigInteger k_inverse= 	rand.modInverse(ORDNUNG);
	out[0] = r;
	out[1] = k_inverse.multiply(zähler).mod(ORDNUNG);
	return out;
}



/**	Die Signatur "r" und "s" wird geprüft.
	- Übergeben wird der 32byte lange Hash, dessen Signatur geprüft werden soll,
	- die Signatur selbst "sig" als BigInteger-Array bestehend aus 2 Elementen: [0] = r   und    [1] = s.
	- und der Pub.Key als BigInteger Array mit 2 Elementen.*/
public boolean verify(byte[] hash, BigInteger[] sig, BigInteger[] pub)
{
	BigInteger h =  new BigInteger(1,hash).mod(ORDNUNG);
	BigInteger s_invers = sig[1].modInverse(ORDNUNG);
	BigInteger[] arg1 = multiply_G(h.multiply(s_invers).mod(ORDNUNG));
	BigInteger[] arg2 = multiply_Point(pub,sig[0].multiply(s_invers).mod(ORDNUNG));
	BigInteger[] arg3 = addition(arg1,arg2);
	if(arg3[0].equals(sig[0])) return true;
	else return false;
}



/**	Multipliziert den Generator mit dem "factor" auf der elliptischen Kurve.
	Schnelle Berechnung mit Hilfe der EXP_List.   ca. 3ms  */
public BigInteger[] multiply_G(BigInteger factor)
{
	BigInteger[] voher = EXPList.nullVektor;
	BigInteger[] erg   = new BigInteger[2];
	for(int i=0;i<=255;i++)
	{
		if(factor.testBit(i))
		{
		  erg = addition(voher,EXPList.list[i]);
		  voher = erg;
		}
	}
	return erg;
}



/**	Multipliziert einen eigenen Punkt "point" mit "factor" auf der elliptischen Kurve.
	Rekursive Funktion, sehr rechenintensiv und daher sehr langsam!	*/
public static BigInteger[] multiply_Point(BigInteger[] point, BigInteger factor)
{
	BigInteger[] erg = point;
	BigInteger[] NULL= new BigInteger[2];
	NULL[0] = ZERO;
	NULL[1] = ZERO;
	if(factor.equals(ZERO)) return NULL;
	if(factor.equals(ONE)) return erg;
	if(factor.equals(TWO)) return multiply_2(erg);
	if(factor.equals(THREE)) return addition(multiply_2(erg),erg);
	if(factor.equals(FOUR)) return multiply_2(multiply_2(erg));
	if(factor.compareTo(FOUR)==1);
	{
		int exp = factor.bitLength()-1;
		for(;exp >0;exp--)erg = multiply_2(erg);
		factor = factor.clearBit(factor.bitLength()-1);
		erg = addition(multiply_Point(point,factor),erg);
	}
	return erg;
}





//	Multiplikation auf der elliptischen Kurve mit 2  (Nur zur Vorberechnung, nicht zur Laufzeit anwenden!)	m = (3*P[0]²)/(2*sqrt(P[0]²+7))
//	n = P[1] - m*P[0];
//	erg[0] = m² - 2*P[0]
//	erg[1] = -(m*erg[0] + n)
private static BigInteger[] multiply_2(BigInteger[] P)
{
	BigInteger[] erg = new BigInteger[2];
	BigInteger m = Math_Modulo.div(Math_Modulo.mul(THREE,Math_Modulo.pow(P[0],TWO)) , Math_Modulo.mul(TWO , Math_Modulo.sqrt(Math_Modulo.add(Math_Modulo.pow(P[0],THREE),SEVEN))));
	if(P[1].compareTo(ModuloHalb)==1)  m = Math_Modulo.neg(m);
	BigInteger n = Math_Modulo.sub(P[1] , Math_Modulo.mul(m,P[0]));
	erg[0] = Math_Modulo.sub(Math_Modulo.pow(m,TWO) , Math_Modulo.mul(TWO,P[0]));
	erg[1] = Math_Modulo.neg(Math_Modulo.add(Math_Modulo.mul(m,erg[0]) , n));
	return erg;
}



/**	Addiert ein Punkt P mit dem Punkt Q auf der elliptischen Kurve.
	m = (Q[1]-P[1])/(Q[0]-P[0])
	n = P[1] - m*P[0];
	x = m² - x1 -x2
	y = -(m*x + n)   */
public static BigInteger[] addition(BigInteger[] po1, BigInteger[] po2)
{
	BigInteger[] nullVektor = new BigInteger[2];
	nullVektor[0] = new BigInteger("0",16); nullVektor[1]  = new BigInteger("0",16);
	if(po1[0].equals(ZERO) && po1[1].equals(ZERO)) return po2;
	if(po2[0].equals(ZERO) && po2[1].equals(ZERO)) return po1;
	if(po2[1].equals(po1[1])) return multiply_2(po1);
	else if(po2[0].equals(po1[0])) return nullVektor;
	BigInteger[] erg = new BigInteger[2];
	BigInteger m = Math_Modulo.div(Math_Modulo.sub(po2[1],po1[1]) , Math_Modulo.sub(po2[0],po1[0]));
	BigInteger n = Math_Modulo.sub(po1[1] , Math_Modulo.mul(m,po1[0]));
	erg[0] = Math_Modulo.sub(Math_Modulo.sub(Math_Modulo.mul(m,m) ,(po1[0])) , (po2[0]));
	erg[1] = Math_Modulo.neg(Math_Modulo.add(Math_Modulo.mul(m,erg[0]) , n));
	return erg;
}



/**	Subtrahiert ein Punkt P mit dem Punkt Q auf der elliptischen Kurve.
	Wird nur zu Testzwecken benötigt.  */
public static BigInteger[]subtraktion(BigInteger[] p1, BigInteger[] p2)
{
	BigInteger[] y = new BigInteger[2];
	y[0] = p2[0];
	y[1] = Math_Modulo.neg(p2[1]);
	return addition(p1,y);
}



/**	Dividiert P/Q auf der elliptischen Kurve
	Wird nur zu Testzwecken benötigt.  */
public static BigInteger[] div(BigInteger[] P, BigInteger Q)
{
	BigInteger teiler = Math_Modulo.calcHalb(Q);
	return multiply_Point(P,teiler);
}



/**	Beschneidet ein ByteArray beliebiger Länge auf eine fest definierte Länge "len".
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



// -------------------------------------------- Sonnstiges ---------------------------------- //


/**	Gibt den Punkt Y von X zurück.
	Da es immer zwei gültige Y-Werte zu einem X-Wert gibt ist die Berechnung nur zu 50% richtig.
	Die Information um welchen Punkt es sich handelt kann die Methode nicht "erraten". Daher werden beide möglichen Y-Werte in einem Array zurück gegeben.
	Y1 = sqrt(x³+7) ,  Y2 = -sqrt(x³+7)  */
public static BigInteger[] y_von_x(BigInteger x)
{
	BigInteger[] erg = new BigInteger[2];
	try
	{
		erg[0] = Math_Modulo.sqrt(Math_Modulo.add(Math_Modulo.pow(x,THREE) , SEVEN));
		erg[1] = Math_Modulo.neg(Math_Modulo.sqrt(Math_Modulo.add(Math_Modulo.pow(x,THREE) , SEVEN)));
		return erg;
	}
	catch(Exception e)
	{
		System.out.println("X-Koordinate trifft nicht auf die elliptische Kurve!");
		return erg;
	}
}



/**	Decompremiert einen Punkt auf der elliptischen Kurve secp256k1.
    Es Wird ein kompriemierter Punkt (PubKey) mit 02 oder 03 vorne als Hex-String übergeben.
    Der Übergebene Hex-String muss genau 33Byte lang sein und darf nur Hexadezimale Zeichen enthalten.
    Das erste Byte muss 02 oder 03 Sein! Dies gibt an ob der Y-Wert gerade oder ungerade ist. "02"= Gerade, "03"= unerade
    Zurück gegeben wird der unkomprimierte Punkt als BigInteger-Array. **/
public static BigInteger[] deComp(String pub) throws Exception
{
	if(pub.length()!=66)                                 throw new Exception("Format Error: String not 33Byte long!");
	String s = pub.substring(0,2);
	if(!s.equals("02") && !s.equals("03"))   throw new Exception("Format Error: First byte is not 02 or 03!");
	String str = pub.substring(2,66);
	BigInteger[] erg =  new BigInteger[2];
	erg[0] = new BigInteger(str,16);
	BigInteger[] y = y_von_x(erg[0]);
	BigInteger yGerade;
	BigInteger yUngerade;
	if(y[0].mod(TWO).equals(BigInteger.ZERO))
	{
		yGerade   = y[0];
		yUngerade = y[1];
	}
	else
	{
		yGerade   = y[1];
		yUngerade = y[0];
	}
	if(s.equals("02")) erg[1] = yGerade;
	if(s.equals("03")) erg[1] = yUngerade;
	return erg;
}



/**	Komprimiert einen Punkt auf der elliptischen Kurve secp256k1.
	@param pub Es wird ein umkomprimierter EC-Punkt als BigInteger[] übergeben. X-Koordinate ist [0] und X-Koordinate ist[1]
	@compressed wenn true 
	@return Der Komprimierte Public-Key wird als Byte-Array zurückgegeben und ist genau 33 Byte oder genau 65 Byte lann (wenn uncompressed).
	Das erste Byte ist dabei immer "02", "03", oder "04". Dies gibt an ob der Y-Wert gerade oder ungerade ist. "02"= Gerade, "03"= ungerade 
	oder "04" Uncompressed X und Y werden hintereinander ausgegeben.**/
public static byte[] comp(BigInteger[] pub, boolean compressed)
{
	byte[] x = pub[0].toByteArray();
	byte[] y = pub[1].toByteArray();
	x = Convert.removeLeadingZeros(x);
	y = Convert.removeLeadingZeros(y);
	x = Convert.to_fixLength(x, 32);
	y = Convert.to_fixLength(y, 32);
	if(compressed)
	{
		byte[] out = new byte[33];
		System.arraycopy(x, 0, out, 1, 32);
		if(pub[1].mod(BigInteger.TWO).equals(BigInteger.ZERO))	
		{
			out[0] = 0x02;
			return out;
		}
		else 													
		{
			out[0] = 0x03;
			return out;
		}
	}
	else
	{
		byte[] out = new byte[65];
		System.arraycopy(x, 0, out, 1, 32);
		System.arraycopy(y, 0, out, 33, 32);
		out[0] = 0x04;
		return out;
	}
}



}