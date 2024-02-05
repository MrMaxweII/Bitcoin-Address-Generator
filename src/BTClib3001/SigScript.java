package BTClib3001;



/****************************************************************************************************************************
*		V2.0    					Autor: Mr. Maxwell   							vom 04.10.2023							*
*		Letzte Änderung: getName(); hinzugefügt und Klasse komplett neu, da sie fehlerhaft uns schlecht programmiert war. 	*
*		Gehört zur BTClib3001																								*
*		Nicht statische Klasse die eine Signature aus dem SigScript parst.													*
*		Vorgehensweise:																										*
*		Es wird mit dem Konstruktor ein "new SigScript(sig)" Object erstellt.												*
*		Nun können die Signature-Teile aus dem SigScript über die Methoden abgerufen werden.								*
*		Das raw SigScript in ByteArray darf durch die Klasse nicht verändert werden!										*
*		Derzeit sind 2 Sig.Scripte implementiert: P2PK und P2PKH.															*
*		Möglicherweise fehlen noch weitere Sig.Scripte!																		*
*		Achtung, dieser Parser wird im Blockexplorer verwendet und durchläuft jede Transakton. Laufzeit Konstruktor bachten!*
*****************************************************************************************************************************/



public class SigScript
{
	private byte[] sig;			// Original raw Signature
	private int lenSig;			// Die Gesamtlänge dieser Signatur (erstes Byte)
	private int posSig;			// Die Startposition dieser Signature	
	private int lenSigRS;		// Die Länge der Signature R+S
	private int posSigRS;		// Die Startposition der Signature r+s
	private int posSigR;		// Die Startposition der Signature r
	private int posSigS;		// Die Startposition der Signature s
	private int lenSigR;		// Die Länge der Signature r
	private int lenSigS;		// Die Länge der Signature s
	private int lenPub;			// Die Länge des Public Keys
	private int posPub;			// Die Startposition des Public Keys
	private String 	name;		// Der Name es Sig.Scriptes
	private int		nr =-1;		// Die Nummer des identifizierten Sig.Scriptes. P2PK=1, P2PKH=2 ...  Wenn -1 wurde ein unbekanntes Sig.Script geladen.

	
	
	
// ------------------------------------- Konstruktor --------------------------------------------------

	
	
/**	Der Konstruktor erstellt ein SigScript Objekt
	@param data Das raw Sig.Script als ByteArray   
	Es soll hier keine Excepteion ausgelöst werden, da dies in Anwendungen (z.B. Blockexplorer) Störungen auslöst die so nicht gewollt sind.
	Bei einem unbekanntem Sig.Script soll stattdessen nur der Name "unknown..." in das Namensfeld übergeben werden und die Anwendung soll weiter laufen.  **/
	public SigScript(byte[] data)
	{
		this.sig = data;	
		try 
		{			
			if(calc_P2PK())  	return;						// P2PK   Pay-to-Public-Key
			if(calc_P2PKH()) 	return;						// P2PKH  Pay-to-Public-Key-Hash 		
			// Weitere Scripte hier einfügen!
			name = "unknown sig.script";
		} 
		catch (Exception e) {name = "unknown sig.script";}	
	}	
	
	
	


// ---------------------------------------------------------------------- Public Methoden ----------------------------------------------------------------


/**	Gibt den Namen des Signature-Scriptes zurück.
	Derzeit sind implementiert: P2PK und P2PKH **/
public String getName()
{
	return name;
}


/** Gibt den r Teil der Signature zurück. 
 	Im Fehlerfall oder wenn das Script nicht erkannt wird, wird ein Byte-Array der Länge NULL zurück gegeben. Dies führt zu keinem Fehler in Programmen.**/	
public byte[] getSigR()
{
	if(nr==1 || nr==2)
	{
		byte[] out = new byte[lenSigR];
		System.arraycopy(sig, posSigR, out, 0, lenSigR);
		return out;
	}
	return new byte[0];
}


/** Gibt den s Teil der Signature zurück. 
 	Im Fehlerfall oder wenn das Script nicht erkannt wird, wird ein Byte-Array der Länge NULL zurück gegeben. Dies führt zu keinem Fehler in Programmen.**/		
public byte[] getSigS()
{
	if(nr==1 || nr==2) // Bei P2PK oder P2PKH 
	{
		byte[] out = new byte[lenSigS];
		System.arraycopy(sig, posSigS, out, 0, lenSigS);
		return out;
	}
	return new byte[0];
}
	

/** Gibt den Public Key (einschließlich der 0x02, oder 0x03 oder 0x04 am Anfang) zurück.
 	Im Fehlerfall oder wenn das Script nicht erkannt wird, wird ein Byte-Array der Länge NULL zurück gegeben. Dies führt zu keinem Fehler in Programmen.	
	In einem P2PK Sig.Script ist kein Public-Key vorhanden! In dem Fall wird auch ein Null-Längen-ByteArray zurück gegeben. **/
public byte[] getPubKey()
{
	if(nr==2) // Nur in P2PKH ist ein Public-Key in der Signatur vorhanden.
	{
		byte[] out = new byte[lenPub];
		System.arraycopy(sig, posPub, out, 0, lenPub);
		return out;
	}
	return new byte[0];
}
	
	
/** Gibt die Signaturteile r, s, und Pub.Key in getrennter, beschrifteter Form, als String zurück. **/
public String toString()
{
	String r 	= "Sig. r   = " + Convert.byteArrayToHexString(getSigR());
	String s 	= "Sig. s   = " + Convert.byteArrayToHexString(getSigS());
	String pub 	= "Pub. Key = " + Convert.byteArrayToHexString(getPubKey());
	return r+"\n"+s+"\n"+pub+"\n";	
}
	


	
// ---------------------------------------------------------------------- private Methoden -------------------------------------------------------------



// parst ein P2PK Sig.Script.
// wenn es geparst werden kann wird true zurück gegeben.
// Ein P2PK (Pay to Public-Key) Script enthält nur R und S, abe der Public Key ist hier nicht enthalten!
private boolean calc_P2PK() throws Exception
{
	try
	{			
		int[] cs = Calc.decodeCompactSize(sig,0);
		posSig = cs[0];
		lenSig = cs[1];
			
		cs = Calc.decodeCompactSize(sig,posSig+1);
		posSigRS = cs[0];
		lenSigRS = cs[1];
		
		cs = Calc.decodeCompactSize(sig,posSigRS+1);
		posSigR = cs[0];
		lenSigR = cs[1];
		
		cs = Calc.decodeCompactSize(sig,posSigR+lenSigR+1);
		posSigS = cs[0];
		lenSigS = cs[1];
			
		if(posSigRS+lenSigRS+1 == sig.length) // Wenn keine weiteren Daten folgen ist es wahscheinlich ein P2PK Script
		{
			name = "P2PK"; 
			nr = 1;
			return true;
		}
		else return false;
	}
	catch (Exception e){return false;}
}
	

// parst ein P2PKH Sig.Script.
// wenn es geparst werden kann wird true zurück gegeben.
// Ein P2PKH (Pay to Public-Key Hash) Script enthält R,S und den Public Key. Es ist das häufigste und allgemeine SigScript aller Legancy BTC Transaktionen.
private boolean calc_P2PKH() throws Exception
{
	try
	{
		int[] cs = Calc.decodeCompactSize(sig,0);
		posSig = cs[0];
		lenSig = cs[1];
			
		cs = Calc.decodeCompactSize(sig,posSig+1);
		posSigRS = cs[0];
		lenSigRS = cs[1];
		
		cs = Calc.decodeCompactSize(sig,posSigRS+1);
		posSigR = cs[0];
		lenSigR = cs[1];
		
		cs = Calc.decodeCompactSize(sig,posSigR+lenSigR+1);
		posSigS = cs[0];
		lenSigS = cs[1];
					
		cs = Calc.decodeCompactSize(sig,posSigRS+lenSigRS+1);
		posPub = cs[0];
		lenPub = cs[1];	
			
		if(lenPub + lenSig + 2 == sig.length) // Wenn keine weiteren Daten folgen ist es wahscheinlich ein P2PKH Script
		{
			name = "P2PKH"; 
			nr = 2;
			return true;
		}
		else return false;	
	}
	catch (Exception e){return false;}
}	
}