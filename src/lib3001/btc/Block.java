package lib3001.btc;
import java.util.Arrays;

import lib3001.crypt.Calc;
import lib3001.crypt.Convert;



/*******************************************************************************************************
*	V1.3 	Autor: Mr. Maxwell												vom 01.01.2020				*
* 	LIB3001 Bibliotheks Klasse																			*
*	Nicht statische Klasse die einen Block parst.														*
*	Vorgehensweise:																						*
*	Es wird mit dem Konstruktor ein "new Block(blk)" Object erstellt.									*
*	Nun können alle möglichen Teile des Blocks über die Methoden abgerufen werden.						*
*	Der raw Block in ByteArray darf durch die Klasse nicht verändert werden!							*
********************************************************************************************************/



public class Block
{

	private byte[] 	blk;					// Der originale Raw Block. als ByteArray so wie er in der Blockchain steht.
	private int 	txCount;				// Die Anzahl der Transaktionen
	private int 	txPos0;					// Die Position in der die Transaktionen beginnen.




// ------------------------------------- Konstruktor --------------------------------------------------

/**	Dem Konstruktor wird der originale  Raw Block als Byte Array übergeben. */
public Block(byte[] blk)
{
	this.blk=blk;
	setTxCount();
}





//-------------------------------------- public Methoden ----------------------------------------------

/** Gibt den rohen Block unverändert als ByteArray zurück	**/
public byte[] getRawBlock()
{
	return blk;
}


/** Gibt den rohen Block unverändert als String zurück	**/
public String getRawBlockString()
{
	return Convert.byteArrayToHexString(blk);
}


/** Gibt den ganzen Block-Header zurück.	**/
public byte[] getBlockHeader()
{
	byte[] bH = new byte[80];
	System.arraycopy(blk, 0, bH, 0, 80);
	return bH;
}


/**	Gibt die Version des Blockes zurück.	**/
public byte[] getVersion()
{
	byte[] b = new byte[4];
	System.arraycopy(blk, 0, b, 0, 4);
	return b;
}


/**	Gibt den BlockHash des vorherigen Blockes zurück.	**/
public byte[] getPrevBlockHash()
{
	byte[] b = new byte[32];
	System.arraycopy(blk, 4, b, 0, 32);
	return b;
}


/**	Gibt die Merkle-Root aus dem Block-Header zurück.	**/
public byte[] getMerkleRoot()
{
	byte[] b = new byte[32];
	System.arraycopy(blk, 36, b, 0, 32);
	return b;
}


/**	Gibt den TimeStamp zurück.	**/
public byte[] getTimeStamp()
{
	byte[] b = new byte[4];
	System.arraycopy(blk, 68, b, 0, 4);
	return b;
}


/**	Gibt den Schwierigkeitsgrad zurück.	**/
public byte[] getDifficulty()
{
	byte[] b = new byte[4];
	System.arraycopy(blk, 72, b, 0, 4);
	return b;
}


/**	Gibt die Nonce zurück.	**/
public byte[] getNonce()
{
	byte[] b = new byte[4];
	System.arraycopy(blk, 76, b, 0, 4);
	return b;
}


/**	Gibt die Anzahl der enthaltenen Transaktionen zurück.	**/
public int getTxCount()
{
	return txCount;
}


/** Gibt ein ByteArray mit allen Raw-Transaktionen des Blockes zurück. **/
public byte[][] getTxArrays()
{
	byte[][] tx = new byte[txCount][];
	int pos = txPos0;
	for(int i=0;i<txCount;i++)
	{
		Transaktion s = new Transaktion(blk, pos);
		int len = s.size();
		tx[i] = new byte[len];
		System.arraycopy(blk, pos, tx[i], 0, len);
		pos = pos + len;
	}
	return tx;
}


/**	Berechnet den Blockhash (ersten 80 Byte) und gibt ihn zurück. **/
public byte[] getBlockHash()
{
	byte[] h = new byte[80];
	System.arraycopy(blk, 0, h, 0, 80);
	h = Calc.getHashSHA256(Calc.getHashSHA256(h));
	return Convert.swapBytesCopy(h);
}



/**	Berechnet die Merkle-Root aus allen Transaktionen und gibt sie zurück.
	Die Merkle-Root wird in verdrehter Form (DER-Codierung) zurück gegeben!	**/
public byte[] getCalcMerkleRootfromTx()
{
	byte[][] tx_b = getTxArrays();
	byte[][] tx_h = new byte[tx_b.length][];
	for(int i=0;i<tx_b.length;i++)
	{
		Transaktion tx = new Transaktion(tx_b[i],0);
		tx_h[i] = tx.getTxHash();
	}
	return calcMerkleRoot(tx_h)[0];
}



/**	Berechnet die Merkle-Root aus allen Transaktionen und vergleicht sie, mit der Merkle-Root aus dem Block-Header.
	Sind die beiden gleich, wird true zurück gegeben, sonnst false.		**/
public boolean verifyMerkleRoot()
{
	byte[] mR1 = getCalcMerkleRootfromTx();
	byte[] mR2 = getMerkleRoot();
	return Arrays.equals(mR1, mR2);
}



/**	Gibt den Block als Hex-String zurück. Unterteilt in die verschiedenen Bereiche.**/
@Override
public String toString()
{
	String str = "";
	str = str + Convert.byteArrayToHexString(Convert.swapBytesCopy(getVersion())) + "\n";
	str = str + Convert.byteArrayToHexString(Convert.swapBytesCopy(getPrevBlockHash())) + "\n";
	str = str + Convert.byteArrayToHexString(Convert.swapBytesCopy(getMerkleRoot())) + "\n";
	str = str + Convert.byteArrayToHexString(Convert.swapBytesCopy(getTimeStamp())) + "\n";
	str = str + Convert.byteArrayToHexString(Convert.swapBytesCopy(getDifficulty())) + "\n";
	str = str + Convert.byteArrayToHexString(Convert.swapBytesCopy(getNonce())) + "\n";
	for(int i=0; i<txCount;i++) str = str + Convert.byteArrayToHexString(getTxArrays()[i]) + "\n";
	str = str + "\n\n";
	return str;
}



/**	Gibt den BlockHeader als String zurück. Unterteilt in die verschiedenen Bereiche.**/
public String getBlockHeaderString()
{
	String str = "";
	str = str + "Block Hash:      " + Convert.byteArrayToHexString(getBlockHash()) + "\n";
	str = str + "Block Version:   " + Convert.byteArrayToHexString(Convert.swapBytesCopy(getVersion())) + "\n";
	str = str + "Prev Block Hash: "+ Convert.byteArrayToHexString(Convert.swapBytesCopy(getPrevBlockHash())) + "\n";
	str = str + "Merkle Root:     "+ Convert.byteArrayToHexString(Convert.swapBytesCopy(getMerkleRoot())) + "\n";
	str = str + "Time Stamp:      "+ Convert.byteArrayToHexString(Convert.swapBytesCopy(getTimeStamp())) + "\n";
	str = str + "Difficulity:     "+ Convert.byteArrayToHexString(Convert.swapBytesCopy(getDifficulty())) + "\n";
	str = str + "Nonce:           "+ Convert.byteArrayToHexString(Convert.swapBytesCopy(getNonce())) + "\n";
	return str;
}




// --------------------------------------------------- Private Methoden ----------------------------------//


// Rekursive private Methode die die Merkle-Root berechnet
private byte[][] calcMerkleRoot(byte[][] mr)
{
	if(mr.length == 1) return mr;
	if(mr.length%2 !=0)  // Wenn die Elemente ungerade sind, wird das letzte dupliziert.
	{
		mr = Arrays.copyOf(mr, mr.length+1);
		mr[mr.length-1] = mr[mr.length-2];
	}
	byte[][] sum = new byte[mr.length/2][];
	for(int i=0;i<sum.length;i++)
	{
		sum[i] = new byte[64];
		System.arraycopy(mr[i*2], 0, sum[i], 0, 32);
		System.arraycopy(mr[i*2+1], 0, sum[i], 32, 32);
		sum[i] = Calc.getHashSHA256(Calc.getHashSHA256(sum[i]));
	}
	if(sum.length>1) return calcMerkleRoot(sum);
	return sum;
}



// Setzt die Startpositionen der Transaktion TxCount und TxPos0
private void setTxCount()
{
	int[] c = Calc.decodeCompactSize(blk, 80);
	txPos0  = c[0];
	txCount = c[1];
}
}