package BTClib3001;
import java.util.Arrays;




/*******************************************************************************************************************************************
*		V1.7.1    Autor: Mr. Maxwell   		Signieren von Tx wurde ausgelagert											vom 18.01.2024		*
*																																			*
*		Letzte Änderung: 																													*
*		Die Methode getSigHash(), wird von hier aus zwar aufgerufen, leitet aber die Berechnung an die entsprechende Signatur-Klasse weiter.*
*		Alle Methoden zur Signierung, oder Transaktions-Erstellung werden entfernt / Ausgelagert!											*
*		Diese Klasse ist nur zum Parsen von Transaktionen zu verwenden!!!																	*
*		BTClib3001	Klasse																													*
*		Nicht statische Klasse die einen Transaktionscode parst und viele Methoden zur Verfügung stellt.									*
*		Vorgehensweise:																														*
*		Es wird mit dem Konstruktor ein "new Transaktion(tx,pos0)" Object erstellt.															*
*		Dem Konstruktor wird ein Byte-Array (beliebiger Länge) mit mindestens einer enthaltenen Transaktion und einem "pos0" Wert übergeben.*
*		Im Byte-Array "data" dürfen mehrere Transaktionen enthalten sein. Es wird aber nur eine hier geparst.								*
*		Der Pos0-Wert stellt den Startpunkt dieser Transaktion dar, die dann in dieser Klasse behandelt wird.								*		
*		Die übergebene tx-Byte-Array darf durch die Klasse nicht verändert werden!															*
********************************************************************************************************************************************/



public class Transaktion 
{
														
	private byte[] 	data;				// Der Daten Stream mit zusammenhängenden Transaktionen Darf nicht verändert werden! 
	private int		pos0;				// Die Startposition der Transaktion deren Länge ermittelt werden soll.
	private int		version_pos;		// Start der Versions Bytes. (immer 4 Byte)
	public  boolean	isWitness;			// Ist true, wenn es sich um eine Witness-Transaktion handelt.	
	private int 	txIn_count;			// Die Anzahl der Eingangs-Transaktionen
	private int[] 	prev_Hash_pos;		// Die Startpunkte der Tx-In vorherigen Hashes. (immer 32Byte)
	private int[]	txIndex_pos;		// Die Nummer der Tx der vorherigen Tx. (immer 4 Byte)
	private int[] 	sigScript_len;		// int-Array mit der Script-Länge der Eingangs-Transaktionen. (Mehrere Tx.In daher auch mehrerer Längen)
	private int[]	sigScript_pos;		// Die Startposition des Signatur Scripts.
	private int[]	sequence_pos;		// Startposition der Sequence (immer 4Bytes)	
	private int 	txOut_count;		// Die Anzahl der Ausgangs-Transaktionen
	public  int[]	value_pos;			// Der Betrag, (Immer 8Byte)
	private int[] 	pkScript_len;		// int-Array mit der Script-Länge der Ausgangs-Transaktionen. (Mehrere Tx.Out daher auch mehrerer Längen)
	private int[] 	pkScript_pos;		// Startpositionen aller PK.Scripte;
	public int		witness_len;		// Wenn Witness vorhanden ist, ist die Länge nicht 0
	public  int		witness_pos;		// Startposition der Witness-Daten, falls vorhanden.
	public  int 	lockTime_pos;		// Die Start Position des der LockTime bzw. Witness ganz am Ende. (immer 4Byte)	
	private int		tx_size;			// Die endgültige Länge der gesamten Transaktion.
	private int		end_pos;			// Zeigt auf das nächste Byte nach der Transaktion. Wird benötigt, Falls mehrere Tx geparst werden sollen.
	
	
	
	
	
	
// ---------------------------------------------------------------- Konstruktor ---------------------------------------------------------------//

// Der Konstruktor parst die gesamte Transaktion einmal und legt dabei die obigen Positions und Längen Zeiger an.	
/**	Dem Konstruktor wird ein Byte-Array (beliebiger Länge) mit mindestens einer enthaltenen Raw-Transaktion und einem "pos0" Wert übergeben.
	Im Byte-Array "data" dürfen mehrere Transaktionen enthalten sein. Es wird aber nur eine hier geparst.	
	Der Pos0-Wert stellt den Startpunkt dieser Transaktion dar, die dann in dieser Klasse behandelt wird.
	@param data ByteArray beliebiger Länge mit mindestens einer Transaktion 
 	@param pos0 Startposition der Transaktion die hier verwendet werden soll.  **/
public Transaktion(byte[] data, int pos0)
{	
	int pos = pos0;									// Position an der sich der Parser gerade befindet.	
	this.data = data;
	this.pos0 = pos0;	
	version_pos = pos;	pos=pos+4;					// Die ersten 4 Bytes "version";
	if(isWitness()) 	pos=pos+2;					// Die Verschiebung nach hinten durch witness wird gesetzt.	
	int[] cs = Calc.decodeCompactSize(data, pos);	// Parst die Tx-In-Count
	pos =	 cs[0];  txIn_count = cs[1];
	prev_Hash_pos 	= new int[txIn_count];
	txIndex_pos		= new int[txIn_count];
	sigScript_len	= new int[txIn_count];
	sigScript_pos	= new int[txIn_count];
	sequence_pos	= new int[txIn_count];	
	for(int i=0;i<txIn_count;i++)					// Parst alle Tx-In
	{
		prev_Hash_pos[i] = pos;  pos=pos+32;
		txIndex_pos[i]   = pos;  pos=pos+4;
		cs = Calc.decodeCompactSize(data, pos);		// Parst die Sig.Script Länge
		sigScript_pos[i] = cs[0];  sigScript_len[i] = cs[1];	
		pos = sigScript_pos[i] + sigScript_len[i];
		sequence_pos[i] = pos;  pos=pos+4;
	}
	cs = Calc.decodeCompactSize(data, pos);			// Parst die Tx-Out-Count
	pos =	 cs[0];  txOut_count = cs[1];
			
	value_pos 	 = new int[txOut_count];		
	pkScript_len = new int[txOut_count];	
	pkScript_pos = new int[txOut_count];		
	for(int i=0;i<txOut_count;i++)					// Parst alle Tx-Out
	{
		value_pos[i] = pos;  pos=pos+8;
		cs = Calc.decodeCompactSize(data, pos);		// Parst die Pk.Script Länge
		pkScript_pos[i] = cs[0];  pkScript_len[i] = cs[1];
		pos = pkScript_pos[i] + pkScript_len[i];
	}
	if(isWitness) 									// Parst Witness, falls vorhanden 
	{  
		witness_pos = pos;;
		
		for(int i=0;i<txIn_count;i++)					
		{
			cs = Calc.decodeCompactSize(data, pos);	
			pos = cs[0]; int c = cs[1];
			for(int j=0;j<c;j++)
			{
				cs = Calc.decodeCompactSize(data, pos);	
				pos = cs[0]; int len = cs[1];
				pos = pos + len;
			}
		}	
		witness_len = pos - witness_pos;		
	}
	lockTime_pos = pos;  pos=pos+4;					// Parst die Lock-Time
	end_pos = pos;
	tx_size = pos - pos0;
}
	



/**	Gibt die Länge (Anzahl Byte) der Transaktion zurück die im Konstruktor mit pos0 markiert wurde. 
 	Achtung, es handelt sich nicht um die Byte-Länge des übergebenen Byte-Arrays im Konstruktor!**/
public int size()
{
	return tx_size;
}




//----------------------------------------------------- Public Getter Methoden ---------------------------------------------------------//



/** Gibt die erste Raw-Transaktion als Byte-Array zurück, die im Data-Stream enthalten ist. **/
public byte[] getRawTx()
{
	byte[] out = new byte[tx_size];
	System.arraycopy(data, pos0, out, 0, tx_size);
	return out;
}


/**	Zeigt auf das nächste Byte, nach dieser eingelesenen Transaktion. Wird benötigt, Falls mehrere Tx geparst werden sollen. 
	Achtung: Wurde dem Konstruktor nur eine Tx übergeben, zeigt diese "end-pos" auf ein Element, welches im Array nicht enthalten ist. **/
public int end() 
{
	return end_pos;
}


/**	Gibt die Versions Nr. Der Transaktion als Integer zurück (ersten 4 Byte in gedrehter Reihenfolge)	
	ByteSwab wird durchgeführt!
	Beispiel: 01000000  ->   1*/
public int getVersion()
{
	byte[] b = Convert.swapBytesCopy(getVersion_byte());					
	return Convert.byteArray_to_int(b);
}		
	

/**	Gibt die Versions Nr. Der Transaktion als ByteArray in Originalform zurück **/
public byte[] getVersion_byte()
{
	byte[] out = new byte[4];
	System.arraycopy(data, version_pos, out, 0, 4);
	return out;					
}	


/** Gibt die Anzahl der Transaktions Eingänge zurück. **/
public int getTxInCount()
{
	return txIn_count;
}


/**	Gibt ein 2Dim Array mit den Transaktions-Hash´s zurück die von den vorherigen Transaktionen stammen.
	2Dim Array, weil es mehrere Tx-Hashes sein können! 
	Rückgabe ist ein Array mit 32Bytes langen Bytes-Arrays (ByteArray[?][32])
	Die Tx-Hash´s werden in der allgemeinen Form zurück gegeben. (ByteSwap wird hier durchgeführt)	**/
public byte[][] getTxPrevHash()
{
	byte[][] out = new byte[txIn_count][32];
	for(int i=0;i<txIn_count;i++)
	{
		System.arraycopy(data, prev_Hash_pos[i], out[i], 0, 32);
		Convert.swapBytes(out[i]);
	}
	return out;
}



/**	Gibt ein 2Dim Array mit den Transaktions-Hash´s zurück die von den vorherigen Transaktionen stammen.
	2Dim Array, weil es mehrere Tx-Hashes sein können! 
	Rückgabe ist ein Array mit 32Bytes langen Bytes-Arrays (ByteArray[?][32])
	Die Tx-Hash´s werden in Ursprungs-Form zurück gegeben. Nicht geswapt	**/
public byte[][] getTxPrevHashNoSwap()
{
	byte[][] out = new byte[txIn_count][32];
	for(int i=0;i<txIn_count;i++)
	{
		System.arraycopy(data, prev_Hash_pos[i], out[i], 0, 32);
	}
	return out;
}



/**	Gibt ein Int-Array mit dem Transaktions-Index zurück die von den vorherigen Transaktionen stammen (4Bytes)
	Transaktions-Index ist die Nummer der Transaktion der Vorherigen Transaktion. 
	(Wenn die Vorherige Transaktion die auf diese Transaktion verweist, mehrere Ausgangs-Transaktionen hatte, 
	dann ist diese Nummer die Position (Index) der Transaktion die auf diese verweist. 0 ist die erste, 1 die zweite, 2 die dritte usw. )	**/
public int[] getTxPrevIndex()
{
	byte[] b = new byte[4];
	int[] out = new int[txIn_count];
	for(int i=0;i<txIn_count;i++)
	{
		System.arraycopy(data, txIndex_pos[i], b, 0, 4);
		Convert.swapBytes(b);
		out[i] = Convert.byteArray_to_int(b);
	}
	return out;
}



/**	Gibt ein 2d Byte-Array mit dem Transaktions-Index zurück die von den vorherigen Transaktionen stammen (4Bytes)
Transaktions-Index ist die Nummer der Transaktion der Vorherigen Transaktion. 
(Wenn die Vorherige Transaktion die auf diese Transaktion verweist, mehrere Ausgangs-Transaktionen hatte, 
dann ist diese Nummer die Position (Index) der Transaktion die auf diese verweist. 0 ist die erste, 1 die zweite, 2 die dritte usw. )	**/
public byte[][] getTxPrevIndexByte()
{
	byte[][] out = new byte[txIn_count][4];
	for(int i=0;i<txIn_count;i++)
	{
		System.arraycopy(data, txIndex_pos[i], out[i], 0, 4);
	}
	return out;
}



/**	2Dim Byte-Array mit dem SigScrips aller Tx-Eingänge.
	Kein ByteSwap, das Script wird so zurück gegeben wie es im Raw Format vorliegt  */
public byte[][] getSigScript()
{
	byte[][] out = new byte[txIn_count][];
	for(int i=0;i<txIn_count;i++)
	{
		out[i] = new byte[sigScript_len[i]];		
		System.arraycopy(data, sigScript_pos[i], out[i], 0, sigScript_len[i]);
	}
	return out;
}


public int[] getSigScript_len()
{
	return sigScript_len;
}

public int[] getSigScript_pos()
{
	return sigScript_pos;
}



/**	2Dim Byte-Array mit der Sequence aller Tx-Eingänge.
	Kein ByteSwap, die Sequence wird so zurück gegeben wie sie im Raw Format vorliegt  */
public byte[][] getSequence()
{
	byte[][] out = new byte[txIn_count][4];
	for(int i=0;i<txIn_count;i++)
	{
		System.arraycopy(data, sequence_pos[i], out[i], 0, 4);
	}
	return out;
}


/** Gibt die Anzahl der Transaktions Ausgänge zurück. **/
public int getTxOutCount()
{
	return txOut_count;
}


/**	Gibt ein long-Array mit den Beträgen der Transaktion zurück (8Bytes).
	Der Betrag ist mit dem Faktor 100.000.000 codiert!
	Zur korrekten Ausgabe z.B. in Double konvertieren:  (double)value()/100000000)	*/
public long[] getValue()
{
	long[] out = new long[txOut_count];
	byte[] b = new byte[8]; 
	for(int i=0;i<txOut_count;i++)
	{
		System.arraycopy(data, value_pos[i], b, 0, 8);
		Convert.swapBytes(b);
		out[i] = Convert.byteArray_to_long(b);
	}
	return out;
}



/**	Gibt ein 2D Byte-Array mit den Raw-Beträgen der Transaktion zurück (8Bytes).
	Die Beträge werden im raw-Hexa Format zurück gegeben, so wie sie in der Tx stehen.	*/
public byte[][] getValueRaw()
{
	byte[][] out = new byte[txOut_count][8];
	for(int i=0;i<txOut_count;i++)
	{
		System.arraycopy(data, value_pos[i], out[i], 0, 8);
	}
	return out;
}



/**	2Dim Byte-Array mit dem PK-Scripts aller Tx-Ausgänge.
	Kein ByteSwap, das Script wird so zurück gegeben wie es im Raw Format vorliegt.  */
public byte[][] getPkScript()
{
	byte[][] out = new byte[txOut_count][];
	for(int i=0;i<txOut_count;i++)
	{
		out[i] = new byte[pkScript_len[i]];		
		System.arraycopy(data, pkScript_pos[i], out[i], 0, pkScript_len[i]);
	}
	return out;
}


/**	Gibt ein Array mit Hash160 "Adressen" zurück (je 20bytes).
	No ByteSwap! Die Hash´s-160 sind in der Raw-TX nicht verdreht! So dass sie direkt so in der Datenbank verwendet werden. 
	Das PK-Script welches den Hash160 normalerweise enthält, beinhaltet nicht immer Hash160 Adressen, sondern können auch nur Daten enthalten die der Absender erstellt hat.
	In diesem Fall kann dann natürlich kein Hash160 Adresse decodiert werden. (Blockchain.info zeigt in diesem Fall die Meldung der nicht decodierbaren Adresse an.)  
	@throws Exception Wenn das Script nicht decodiert werden kann, wird "Unbekanntes PK.Script Exception!" ausgelöst.  **/ 
public byte[][] getHash160()
{	
	byte[][] pk_b = getPkScript();
	byte[][] out = new byte[pk_b.length][];
	for(int i=0;i<pk_b.length;i++)
	{
		PkScript pk = new PkScript(pk_b[i]);
		try{out[i] = pk.getHash160();} 
		catch (Exception e){out[i] = null;}				
	}
	return out;
}


/**	Gibt die Witness Raw-Daten zurück wenn sie vorhanden sind. Kein ByteSwap. 
	Achtung, ist Witness nicht enthalten wird ein Array der Länge null zurückgegeben! */ 
public byte[] getWitness()															
{
	byte[] out = new byte[0];	
	if(isWitness)
	{		
		out = new byte[witness_len];
		System.arraycopy(data, witness_pos, out, 0, witness_len);
	}
	return out;
}


/**	Gibt die LockTime zurück (4Bytes)  (ist die Zeit ab der eine Transaktion eingetragen wird. 0 = sofort)  
	Kein ByteSwap, LockTime wird im Raw Format zurück gegeben.*/
public byte[] getLockTime()
{
	byte[] out = new byte[4];
	System.arraycopy(data, lockTime_pos, out, 0, 4);
	return out;
}


/** Gibt alle Bitcoin-Adressen in Base58 zurück, die in der Tx vorkommen.
@param magic Der Magic Wert als HexString, MainNet oder TestNet  **/
public String[] getBitcoinAddr(byte[] magic)
{
	byte[][] pk_b = getPkScript();
	String[] str = new String[pk_b.length];
	for(int i=0;i<pk_b.length;i++)
	{
		PkScript pk = new PkScript(pk_b[i]);
		str[i] = pk.getBitcoinAddress(magic);				
	}
	return str;
}


/**	Gibt den Transaktions-Hash (2xSH256) der kompletten Transaktion zurück.	
	Der Tx-Hash wird im "geswapten" also verdrehtem Format ausgegeben. 32Byte	*/
public byte[] getTxHash() 
{	
	if(isWitness) 
	{
		byte[] mitte = Arrays.copyOfRange(data, pos0+6, witness_pos);						
		byte[] out = new byte[8 + mitte.length];											
		System.arraycopy(data, pos0, out, 0, 4);		
		System.arraycopy(mitte, 0, out, 4, mitte.length);		
		System.arraycopy(getLockTime(), 0, out, out.length-4, 4);
		return (Calc.getHashSHA256(Calc.getHashSHA256(out)));
	}
	else return (Calc.getHashSHA256(Calc.getHashSHA256(data)));
}

	




// Test: 
//	-	Legancy 1 und 2 und 3 Input = true (Mit 3 Inputs erfolgreich getestet!
//	-	Witness P2SH_P2WPKH getestet mit einem Input
//	-	Witenss P2WPKH (bech32 Adress) funktioniert auch
/**	Gibt einen Signature-Hash zurück, der zum Signieren einer einzigen Signatur in dieser Tx verwendet werden kann.
Implementiert sind:
- SegWit  Bech32 Adressen                                     
- SegWit  P2SH MultiSig Adresse die mit 3 beginnen            
- SegWit  Legancy Adressen die in SegWit-Tx eingebettet sind  
- Legancy P2PK und P2PKH Transaktionen (Möglicherweise Alle Legancy-Tx, habe dies nicht getestet)
Der SigHash kann mit dieser Methode nicht für alle Tranaktionen in der Blockchain berechnet werden. Diverse oder neue Tx-Formate sind evtl. nicht implementiert. In dem Fall wird eine Exception ausgelöst!
Jeder Transaktions-Eingang muss einzeln signiert werden und es gibt deswegen auch für jede Tx-Input einen anderen Signature-Hash!
Um den Signature-Hash erstellen zu können ist das PK-Script der vorherigen Transaktion notwendig und bei Witness-Tx auch der Transaktionsbetrag der Vorherigen Transaktion.
Der Signature-Hash wird für jeden Tx-Typ oder Input-Typ anders berechnet. Für Witness oder Legancy Tx sind die Berechenungen auch sehr unterschiedlich und m.u. sehr komplex.
Die Methode identifiziert zu erst um was für einen Tx-Typ/Input-Typ es sich handelt und ruft dann die jeweils zuständige Klasse auf, in der die Berechnung durchgeführt wird. 
Nicht alle Parameter werden für alle Tx-Typen benötigt. Nicht benötigte Parameter werden mit "null" übergeben.
Diese Methode kann sowol für Unsignierte als auch für fertig Signierte Transaktionen verwerndet werden. Dabei müssen die unsignierten richtig zusammengestellt sein, so das lediglich die Signatur fehlt.
@param pkScript Das Pk-Script der vorherigen Tx auf die sich dieser Signature-Hash bezieht.
@param valueRaw Der Transaktionsbetrag der Tx-In dessen Signature-Hash berechnet werden soll, wird nur bei Witness-Tx benötigt.
@param txIndex Der Transaktions-Index der Tx-In dessen Signature-Hash berechnet werden soll.
@return Signature-Hash für den mit "txIndex" markierten Input.     **/
public byte[] getSigHash(byte[] pkScript, byte[] valueRaw, int txIndex) throws Exception 
{
	if(isWitness) 
	{
		byte[] b = getSigScript()[txIndex];	
		if(b.length==0) 						return TxSigniererWitness.getSigHash_P2WPKH(this, pkScript, valueRaw, txIndex);			// Bech32 Adressen
		if(b.length==23 && b[0]==(byte)0x16)	return TxSigniererWitness.getSigHash_P2SH_P2WPKH(this, valueRaw, txIndex);				// P2SH MultiSig Adresse die mit 3 beginnen
		if(b.length>=106 && b[1]==(byte)0x30)   return TxSigniererWitness.getSigHash_Legancy(this, pkScript, txIndex);					// Legancy Adressen die in SegWit-Tx eingebettet sind
		throw new Exception("SigHash is not implemented for this type of tx input: "+txIndex);
	}
	else
	{
		return TxSigniererLegancy.getSigHash(this, pkScript, txIndex);
	}
}







/**	Gibt die geparste Transaktion als Info-String zurück. 
Dabei werden alle relevanten Teile in Zeilen aufgeteilt und Beschreibungen hinzugefügt.
Kann direkt auf der Konsole angezeigt werden.	
@param magig Der MagigWert als Byte-Array signalisiert das Netzwerk
MainNet:  F9BEB4D9    TestNet:  0B110907      **/
public String toString(byte[] magig) 
{
	String out = 							"TxHash:             "+Convert.byteArrayToHexString(Convert.swapBytesCopy(getTxHash()))+
											"\nVersion:            "+getVersion() +
											"\nWitness:            "+isWitness +
											"\nTx-In count:        "+getTxInCount() ;
	byte[][] txOutH =	getTxPrevHash();
	for(int i=0;i<txOutH.length;i++)out=out+"\nTx prev Hash "+i+":     "+Convert.byteArrayToHexString(txOutH[i]);
	int[] txOuti = getTxPrevIndex();
	for(int i=0;i<txOuti.length;i++)out=out+"\nTx Out Indx "+i+":      "+txOuti[i];
	byte[][] sicS = getSigScript();
	for(int i=0;i<sicS.length;i++) 	out=out+"\nSig.Script "+i+":       "+Convert.byteArrayToHexString(sicS[i]);
	byte[][] seq = getSequence();	  
	for(int i=0;i<seq.length;i++) 	out=out+"\nSequence  "+i+":        "+Convert.byteArrayToHexString(seq[i]);
									out=out+"\nTxOut count:        " + getTxOutCount();	
	long[] val = getValue();   		 
	for(int i=0;i<val.length;i++) 	out=out+"\nValue:    "+i+":        "+(double)val[i]/100000000;
	byte[][] pk = getPkScript();
	for(int i=0;i<pk.length;i++)   	out=out+ "\nPK.Script "+i+":        "+Convert.byteArrayToHexString(pk[i]);
	byte[][] h160;	
	h160 = getHash160();
	for(int i=0;i<h160.length;i++)
	{	
		if(h160[i]!=null) out=out+"\nHash160   "+i+":        "+Convert.byteArrayToHexString(h160[i]);
		else out=out+"\nHash160   "+i+":        "+"Unknown, cannot be decoded!";
	}
	String[] addr = getBitcoinAddr(magig);
	for(int i=0;i<addr.length;i++)  out=out+"\nBit.Addr: "+i+":        "+addr[i];
	out=out+"\nWitness:            "+Convert.byteArrayToHexString(getWitness());
									out=out+"\nLockTime:           "+Convert.byteArrayToHexString(getLockTime());								
	return out;
}



// ----------------------------------------------------------------------- Private Methoden --------------------------------------------------------------------//



// gibt an ob Witness-Daten enthalten sind oder nicht
private boolean isWitness()
{
	if(data[pos0+4]==0 && data[pos0+5]==1) 	{isWitness = true; return true;}
	else 									{isWitness = false; return false;}
}	
}