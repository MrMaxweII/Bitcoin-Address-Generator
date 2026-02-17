package lib3001.btc;
import org.json.JSONArray;
import org.json.JSONException;

import lib3001.crypt.Calc;
import lib3001.crypt.Convert;




/***************************************************************************************************************************************************
*	Version 1.4     								Mr. Maxwell												06.12.2025								*
* 	LIB3001 Bibliotheks Klasse																														*
*	Letzte Änderung: 																																*
*	GetWitnessBlocks() hinzugefügt, getJSON() neu geschrieben.																						*
*	Schwerer Bug im Konstruktor behoben. Taproot tx kann nun geparst werden.																		*
*																																					*
*	Decodiert die Witness Daten aus einer Transaktion. Doku: https://bitcoincore.org/en/segwit_wallet_dev/											*
*	Zuerst muss mit dem Konstruktor ein Witness-Object erzeugt werden.																				*
*	Nicht statische Klasse, es dürfen keine static-Methoden angelegt werden!																		*
*	Anleitung: https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki																		*
*	Achtung, diese Klasse wurde fehlerfrei getestet, nach Änderungen muss wieder getestet werden mit: Test_Witness.VergleichWitnessDataWithCore(). 	*
*****************************************************************************************************************************************************/



public class Witness 
{
	private byte[] 	data;								// Haupt Daten-Array. Achtung darf nicht verändert werden!
	private	int 	posStart;							// Die Position an der die Witness-Daten beginnen
	private	int 	txInCount;							// Die Anzahl der Transaktions-Eingänge
	private int[]	posField;							// Position des Witness-Feld
	private int[]	lenField;							// Länge des Witness-Feld
	private	int[][]	posBlock;							// Startposition aller Blöcke
	private	int[][]	lenBlock;							// Länge aller Blöcke
	private	int 	finalLen;							// Gesamtlänge der Witness Daten.

	
	
	
	
// ------------------------------------------------------- 	Konstruktor -----------------------------------------------------------------//
	

// Schweren Bug korrigiert. 
// Test: über 100000 tx aus der Blockchain fehlerfrei geparst und mit Core verglichen
// Vergleichstest fehlerfrei durchgeführt mit: Test_Witness.VergleichWitnessDataWithCore().
/**	Dem Konstruktor wird ein byte-Array übergeben "data" welches Witness Daten enthält.
	@param data beliebiges Byte-Array welches Witness Daten enthält.
	@param pos Position an der die Witness Daten beginnen.   
	@param txInCount Die Anzahl der Transaktions-Eingänge muss hier übergeben werden weil sie nicht in den Witnessdaten enthalten sind. **/
public Witness(byte[] data, int pos, int txInCount)
{
	this.data = data;
	this.posStart = pos;
	this.txInCount = txInCount;	
	posField = new int[txInCount];
	lenField = new int[txInCount];
	posBlock = new int[txInCount][];
	lenBlock = new int[txInCount][];
	for(int j=0;j<txInCount;j++)
	{	
		posField[j] = pos;
		int[] c1 = Calc.decodeCompactSize(data, pos);
		if(c1[1]==0) pos++; 
		else
		{
			posBlock[j] = new int[c1[1]];
			lenBlock[j] = new int[c1[1]];		
			for(int i=0;i<c1[1];i++)
			{
				int[] c2 = Calc.decodeCompactSize(data, c1[0]);				
				c1[0] = c2[0] +c2[1]+0;			// <<<<<<<<<<<<< neu ab version 1.3
				//c1[0] = c1[0] +c2[1]+1; 		// <<<<<<<<<<<<< alter Code (hat zufällig für Witness-Daten gleicher Länge funktioneirt)
				posBlock[j][i] = c2[0];
				lenBlock[j][i] = c2[1];
				pos = c2[0] + c2[1];
				lenField[j] = pos - posField[j];
				finalLen = pos;
			}
		}
	}		
}	

	




//---------------------------------------------------------- Get Methoden -----------------------------------------------------//


/** @return Gibt die Byte-Länge der gesamten Witness Daten zurück.  **/
public int getLength()
{
	return finalLen;
}



/** @return Gibt die Witness-Daten als Byte-Array zurück. **/
public byte[] getByteArray()
{
	byte[] d = new byte[finalLen];
	System.arraycopy(data, posStart, d, 0, finalLen);	
	return d;
}



/**	@return Gibt die Witness-Daten getrennt in den einzelnen Feldern zurück.
 	Entspricht dann dem gewöhnlichem Signature-Script und kann so der Klasse SigScript übergeben werden.
	Die Anzahl der Felder entspricht der Anzahl der Tx-Eingänge.**/
public byte[][] getWitnessSignature()
{
	byte[][] out = new byte[txInCount][];
	for(int i=0; i<txInCount;i++)
	{	
		if(lenField[i]==0)out[i] = new byte[0];
		else
		{
			out[i] = new byte[lenField[i]-1];
			System.arraycopy(data, posField[i]+1, out[i], 0, lenField[i]-1);
		}
	}
	return out;
}


//Vergleichstest fehlerfrei durchgeführt mit: Test_Witness.VergleichWitnessDataWithCore().
/**	Gibt für jeden Tx-Input, Witness-Daten aufgeteilt in Blöcke zurück.
	Die Anzahl der Blöcke ist unterschiedlich, je nach Transaktion, können es beliebig viele Blöcke sein.
	Im Prizip werden hier die Daten aus getWitnessSignature() noch mal in Blöcke aufgeteilt.
	Dies entspricht auch der Ausgabe des BitcoinCores mit: "decoderawtransaction"
	Achtung, wird ein inputIndex übergeben, deren WitnessFeld keine Daten enthällt, wird ein byte[0][0] Byte-Array mit den Inhalten null zurückgeliefert.
	@param inputIndex Hier muss der Index des Inputes übergeben werden, dessen Witness-Blöcke zurückgegeben werden sollen. **/
public byte[][] getWitnessBlocks(int inputIndex)
{	
	if(lenField[inputIndex]==0) return new byte[0][0];
	byte[][] b = new byte[lenBlock[inputIndex].length][];
	for(int i=0; i<lenBlock[inputIndex].length;i++)
	{
		b[i] = new byte[lenBlock[inputIndex][i]];
		System.arraycopy(data, posBlock[inputIndex][i], b[i] ,0, lenBlock[inputIndex][i]);		
	}	
	return b;
}


//Vergleichstest fehlerfrei durchgeführt mit: Test_Witness.VergleichWitnessDataWithCore().
/**	Gibt die Witness Blöcke als JSONArray zurück.
	Dies entspricht auch der Ausgabe des BitcoinCores mit: "decoderawtransaction"
	@param inputIndex Hier muss der Index des Inputes übergeben werden, dessen Witness-Blöcke zurückgegeben werden sollen. 
	Achtung, wird ein inputIndex übergeben, deren WitnessFeld keine Daten enthällt, wird ein Leeres Array zurück geliefert.**/
public JSONArray getJSON(int inputIndex) throws JSONException
{
	byte[][] b = getWitnessBlocks(inputIndex);
	JSONArray ja = new JSONArray();	
	for(int i=0; i<b.length;i++) ja.put(Convert.byteArrayToHexString(b[i]));
	return ja;
}



/** @return Gibt die Witness-Daten als Hex-String zurück. **/
public String toString()
{
	byte[] d = new byte[finalLen];
	System.arraycopy(data, posStart, d, 0, finalLen);	
	return Convert.byteArrayToHexString(d);
}
}