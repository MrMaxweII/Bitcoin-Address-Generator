package BTClib3001;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




/*******************************************************************************************************************************
*	Version 1.2     								Mr. Maxwell												20.01.2024			*
*	Letzte Änderung: Bug im Konstruktor behoben. Leeres WitnessFeld kann nun geparst werden.									*
*	BTClib3001 Klasse																											*
*	Decodiert die Witness Daten aus einer Transaktion. Doku: https://bitcoincore.org/en/segwit_wallet_dev/						*
*	Zuerst muss mit dem Konstruktor ein Witness-Object erzeugt werden.															*
*	Nicht statische Klasse, es dürfen keine static-Methoden angelegt werden!													*
*	Anleitung: https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki													*
*******************************************************************************************************************************/



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
				c1[0] = c1[0] +c2[1]+1;
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



/** @return Gibt die Witness Daten im JSON Format als String zurück.  Alt, muss noch auf neue Serialisierung angepasst werden.**/
public String getJSON() throws JSONException
{
	JSONObject jo = new JSONObject();	
	int pos = this.posStart;
	int len; 
	for(int j=0;j<txInCount;j++)
	{	
		JSONArray ja = new JSONArray();		
		int c = data[pos]&0xff;
		pos++;
		for(int i=0;i<c;i++)
		{
			len = data[pos]&0xff;
			pos++;
			byte[] witBlock = new byte[len];
			System.arraycopy(data, pos, witBlock, 0, len);	
			ja.put(Convert.byteArrayToHexString(witBlock));
			pos = pos + len;
		}
		String key = "0"+(j+1);
		jo.put(key, ja);
	}	
	return jo.toString(4);
}



/** @return Gibt die Witness-Daten als Hex-String zurück. **/
public String toString()
{
	byte[] d = new byte[finalLen];
	System.arraycopy(data, posStart, d, 0, finalLen);	
	return Convert.byteArrayToHexString(d);
}
}