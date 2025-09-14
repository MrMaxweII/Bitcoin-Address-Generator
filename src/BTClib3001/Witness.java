package BTClib3001;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




/*******************************************************************************************************************************
* Version 1.2                                 Mr. Nickolas-Antoine B.                                           20.01.2024                *
* Last change: Fixed constructor bug. Empty witness fields can now be parsed.                                                  *
* BTClib3001 class                                                                                                            *
* Decodes witness data from a transaction. Docs: https://bitcoincore.org/en/segwit_wallet_dev/                                *
* First create a Witness object with the constructor.                                                                          *
* Non-static class; do not add static methods!                                                                                 *
* Guide: https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki                                                        *
*******************************************************************************************************************************/



public class Witness 
{
	private byte[] 	data;								// Main data array. Warning: must not be modified!
	private	int 	posStart;							// The position where the witness data starts
	private	int 	txInCount;							// The number of transaction inputs
	private int[]	posField;							// Position of the witness field
	private int[]	lenField;							// Length of the witness field
	private	int[][]	posBlock;							// Start position of all blocks
	private	int[][]	lenBlock;							// Length of all blocks
	private	int 	finalLen;							// Total length of the witness data.

	
	
	
	
// ------------------------------------------------------- 	Konstruktor -----------------------------------------------------------------//
	
	
/** Constructor: pass a byte array containing witness data.
 @param data byte array containing witness data
 @param pos start position of witness data
 @param txInCount number of transaction inputs (not contained in witness data) **/
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


/** @return Returns byte length of the entire witness data. **/
public int getLength()
{
	return finalLen;
}



/** @return Returns witness data as byte array. **/
public byte[] getByteArray()
{
	byte[] d = new byte[finalLen];
	System.arraycopy(data, posStart, d, 0, finalLen);	
	return d;
}



/** @return Returns witness data split by inputs, like a signature script per input. **/
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



/** @return Returns witness data as hex string. **/
public String toString()
{
	byte[] d = new byte[finalLen];
	System.arraycopy(data, posStart, d, 0, finalLen);	
	return Convert.byteArrayToHexString(d);
}
}