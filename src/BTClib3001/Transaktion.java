package BTClib3001;
import java.util.Arrays;




/*******************************************************************************************************************************************
*		V1.7.1    Author: Mr. Nickolas-Antoine B.   		Signing of Tx has been outsourced											as of 18.01.2024		*
*																																			*
*		Last change: 																													*
*		The method getSigHash(), is called from here, but delegates the calculation to the corresponding signature class.*
*		All methods for signing or transaction creation are removed / outsourced!											*
*		This class is only to be used for parsing transactions!!!																	*
*		BTClib3001	Klasse																													*
*		Non-static class that parses a transaction code and provides many methods.									*
*		Procedure:																														*
*		A "new Transaktion(tx,pos0)" object is created with the constructor.															*
*		A byte array (of arbitrary length) containing at least one transaction and a "pos0" value is passed to the constructor.*
*		The byte array "data" may contain multiple transactions. However, only one is parsed here.								*
*		The pos0 value represents the starting point of the transaction, which is then processed in this class.								*		
*		The passed tx byte array must not be modified by the class!															*
********************************************************************************************************************************************/



public class Transaktion 
{
														
	private byte[] 	data;				// The data stream with contiguous transactions must not be modified! 
	private int		pos0;				// The starting position of the transaction whose length is to be determined.
	private int		version_pos;		// Start of the version bytes. (always 4 bytes)
	public  boolean	isWitness;			// Is true if it is a witness transaction.	
	private int 	txIn_count;			// The number of input transactions
	private int[] 	prev_Hash_pos;		// The starting points of the Tx-In previous hashes. (always 32Byte)
	private int[]	txIndex_pos;		// The number of the Tx of the previous Tx. (always 4 bytes)
	private int[] 	sigScript_len;		// int-array with the script length of the input transactions. (multiple Tx.In therefore also multiple lengths)
	private int[]	sigScript_pos;		// The starting position of the signature script.
	private int[]	sequence_pos;		// Starting position of the sequence (always 4Bytes)	
	private int 	txOut_count;		// The number of output transactions
	public  int[]	value_pos;			// The amount, (always 8Byte)
	private int[] 	pkScript_len;		// int-array with the script length of the output transactions. (multiple Tx.Out therefore also multiple lengths)
	private int[] 	pkScript_pos;		// Starting positions of all PK scripts;
	public int		witness_len;		// If witness is present, the length is not 0
	public  int		witness_pos;		// Starting position of the witness data, if present.
	public  int 	lockTime_pos;		// The starting position of the locktime or witness at the very end. (always 4Byte)	
	private int		tx_size;			// The final length of the entire transaction.
	private int		end_pos;			// Points to the next byte after the transaction. Needed if multiple Tx are to be parsed.
	
	
	
	
	
	
// ---------------------------------------------------------------- Constructor ---------------------------------------------------------------//

// The constructor parses the entire transaction once and thereby sets up the above position and length pointers.	
/**	The constructor is passed a byte array (of arbitrary length) containing at least one raw transaction and a "pos0" value.
	The byte array "data" may contain multiple transactions. However, only one is parsed here.	
	The pos0 value represents the starting point of this transaction, which is then processed in this class.
	@param data ByteArray of arbitrary length with at least one transaction 
 	@param pos0 Starting position of the transaction to be used here.  **/
public Transaktion(byte[] data, int pos0)
{	
	int pos = pos0;									// Position at which the parser is currently located.	
	this.data = data;
	this.pos0 = pos0;	
	version_pos = pos;	pos=pos+4;					// The first 4 bytes "version";
	if(isWitness()) 	pos=pos+2;					// The shift backwards due to witness is set.	
	int[] cs = Calc.decodeCompactSize(data, pos);	// Parses the Tx-In-Count
	pos =	 cs[0];  txIn_count = cs[1];
	prev_Hash_pos 	= new int[txIn_count];
	txIndex_pos		= new int[txIn_count];
	sigScript_len	= new int[txIn_count];
	sigScript_pos	= new int[txIn_count];
	sequence_pos	= new int[txIn_count];	
	for(int i=0;i<txIn_count;i++)					// Parses all Tx-In
	{
		prev_Hash_pos[i] = pos;  pos=pos+32;
		txIndex_pos[i]   = pos;  pos=pos+4;
		cs = Calc.decodeCompactSize(data, pos);		// Parses the sig.script length
		sigScript_pos[i] = cs[0];  sigScript_len[i] = cs[1];	
		pos = sigScript_pos[i] + sigScript_len[i];
		sequence_pos[i] = pos;  pos=pos+4;
	}
	cs = Calc.decodeCompactSize(data, pos);			// Parses the Tx-Out-Count
	pos =	 cs[0];  txOut_count = cs[1];
			
	value_pos 	 = new int[txOut_count];		
	pkScript_len = new int[txOut_count];	
	pkScript_pos = new int[txOut_count];		
	for(int i=0;i<txOut_count;i++)					// Parses all Tx-Out
	{
		value_pos[i] = pos;  pos=pos+8;
		cs = Calc.decodeCompactSize(data, pos);		// Parses the Pk.Script length
		pkScript_pos[i] = cs[0];  pkScript_len[i] = cs[1];
		pos = pkScript_pos[i] + pkScript_len[i];
	}
	if(isWitness) 									// Parses witness, if present 
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
	lockTime_pos = pos;  pos=pos+4;					// Parses the lock-time
	end_pos = pos;
	tx_size = pos - pos0;
}
	



/**	Gives the length (number of bytes) of the transaction back that was marked with pos0 in the constructor. 
 	Attention, this is not the byte length of the passed byte array in the constructor!**/
public int size()
{
	return tx_size;
}




//----------------------------------------------------- Public Getter Methods ---------------------------------------------------------//



/** Returns the first raw transaction as a byte array contained in the data stream. **/
public byte[] getRawTx()
{
	byte[] out = new byte[tx_size];
	System.arraycopy(data, pos0, out, 0, tx_size);
	return out;
}


/**	Points to the next byte after this read transaction. Needed if multiple Tx are to be parsed. 
	Attention: If only one Tx was passed to the constructor, this "end-pos" points to an element that is not contained in the array. **/
public int end() 
{
	return end_pos;
}


/**	Gives the version number of the transaction as an integer (first 4 bytes in reversed order)	
	ByteSwab is performed!
	Example: 01000000  ->   1*/
public int getVersion()
{
	byte[] b = Convert.swapBytesCopy(getVersion_byte());					
	return Convert.byteArray_to_int(b);
}		
	

/**	Gives the version number of the transaction back as a byte array in original form **/
public byte[] getVersion_byte()
{
	byte[] out = new byte[4];
	System.arraycopy(data, version_pos, out, 0, 4);
	return out;					
}	


/** Returns the number of transaction inputs. **/
public int getTxInCount()
{
	return txIn_count;
}


/**	Gives a 2D array with the transaction hashes back that come from the previous transactions.
	2D array, because there can be multiple Tx-hashes! 
	Return is an array with 32Bytes long byte-arrays (ByteArray[?][32])
	The Tx-hashes are returned in the general form. (ByteSwap is performed here)	**/
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



/**	Gives a 2D array with the transaction hashes back that come from the previous transactions.
	2D array, because there can be multiple Tx-hashes! 
	Return is an array with 32Bytes long byte-arrays (ByteArray[?][32])
	The Tx-hashes are returned in their original form. Not swapped	**/
public byte[][] getTxPrevHashNoSwap()
{
	byte[][] out = new byte[txIn_count][32];
	for(int i=0;i<txIn_count;i++)
	{
		System.arraycopy(data, prev_Hash_pos[i], out[i], 0, 32);
	}
	return out;
}



/** Returns an int array with the transaction indices from previous transactions (4 bytes).
Transaction index is the output index of the previous transaction referenced.
(If the previous transaction had multiple outputs, this index is the position of the output being spent. 0 = first, 1 = second, etc.) **/
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



/** Returns a 2D byte array with the transaction indices from previous transactions (4 bytes each).
Same meaning as getTxPrevIndex(). **/
public byte[][] getTxPrevIndexByte()
{
	byte[][] out = new byte[txIn_count][4];
	for(int i=0;i<txIn_count;i++)
	{
		System.arraycopy(data, txIndex_pos[i], out[i], 0, 4);
	}
	return out;
}



/** 2D byte array with the signature scripts of all inputs.
 No byte swap; scripts are returned as in raw format. */
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



/** 2D byte array with the sequences of all inputs.
 No byte swap; sequences are returned as in raw format. */
public byte[][] getSequence()
{
	byte[][] out = new byte[txIn_count][4];
	for(int i=0;i<txIn_count;i++)
	{
		System.arraycopy(data, sequence_pos[i], out[i], 0, 4);
	}
	return out;
}


/** Gives the number of transaction outputs. **/
public int getTxOutCount()
{
	return txOut_count;
}


/**	Gives a long array with the amounts of the transaction outputs (8Bytes).
	The amount is encoded with the factor 100,000,000!
	For correct output, e.g. convert to Double:  (double)value()/100000000)	*/
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



/** Returns a 2D byte array with raw amounts of the outputs (8 bytes each).
 Values are returned in raw hex as in the transaction. */
public byte[][] getValueRaw()
{
	byte[][] out = new byte[txOut_count][8];
	for(int i=0;i<txOut_count;i++)
	{
		System.arraycopy(data, value_pos[i], out[i], 0, 8);
	}
	return out;
}



/** 2D byte array with the PK scripts of all outputs.
 No byte swap; scripts are returned as in raw format. */
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


/** Returns an array with Hash160 addresses (20 bytes each) where possible.
 No byte swap. If the script cannot be decoded, null is returned for that entry. */
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


/** Returns witness raw data if present. No byte swap.
 If no witness, returns zero-length array. */ 
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