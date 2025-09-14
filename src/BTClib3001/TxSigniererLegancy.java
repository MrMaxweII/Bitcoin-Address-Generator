package BTClib3001;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import ECDSA.Secp256k1;




/*******************************************************************************************************************************************
* V1.3.0                                        Author: Mr. Nickolas-Antoine B.                                              08.01.2024               *
*                                                                                                                                           *
* BTClib3001 class                                                                                                                          *
* This transaction signer signs unsigned Legacy transactions (no Witness here).                                                              *
* Static class without constructor.                                                                                                          *
* Tested to work for all Legacy transactions (multiple inputs/outputs) since V1.2.0.                                                         *
********************************************************************************************************************************************/





public class TxSigniererLegancy 
{
	final static byte[] 	MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};			// magic
	final static byte[] 	TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};			// magic
	final static byte[] 	PrevPrivMAINNET = {(byte) 0x80};			
	final static byte[] 	PrevPrivTESTNET3 = {(byte) 0xef};			
	
	
	
	
	
	
/** Signs an unsigned Legacy transaction with multiple inputs/outputs. Do not use for Witness transactions!
 Appends hash type 01000000 at the end internally. Attributes are arrays per input.
 @param usigTx unsigned transaction (without hash type at end)
 @param privKey array of private keys per input
 @param k array of random numbers per input
 @param compressed boolean array per input indicating compressed pubkey
 @return signed raw transaction as protocol byte array **/
public static byte[] sigTx(byte[] usigTx, byte[][] privKey, byte[][] k, boolean[] compressed) 
{
	byte[] uTx = Arrays.copyOfRange(usigTx, 0, usigTx.length+4);									// Hash-Code wird angehängt
	uTx[uTx.length-4] = 0x01;																		// Hash-Code wird angehängt	
	byte[] b1 = removeSigScript(uTx, -1);															// Löschte alle Daten im SigScript Feld
	ByteArrayList out = new ByteArrayList(b1);
	Transaktion tx = new Transaktion(b1,0);	
	for(int i = tx.getTxInCount()-1; i>=0; i--)
	{
		byte[] sicScript = sigScript(uTx,privKey[i],k[i],compressed[i], i);							// Erstellt die Signatur
		int posS = tx.getSigScript_pos()[i];														// Ermittelt die Position des SigScriptes in der Tx
		out.remove(posS-1, posS);																	// Löscht das alte Längen-Byte des SigScript Felds
		out.insert(sicScript, posS-1);																// Schiebt die Signatur im SigScript Feld ein
	}
	out.remove(out.size()-4, out.size());															// Der Hashcode wird wieder entfernt
	return out.getArrayAll();
}
	
	

	
	
/** Here a signature script (sigScript) for a single input is created/signed.
 Removes all other signature scripts and only the one to be signed is left, then the Sig-Hash is generated.
 @param index the input index to sign. 0 = first input, etc.
 @param usigTx unsigned transaction from which a SigScript is to be signed
 @param privKey the private key with which this input is to be signed
 @param k random number
 @param compressed If true, the pubkey will be compressed. 
 @return The SigScript for this Tx input **/
public static byte[] sigScript(byte[] usigTx, byte[] privKey, byte[] k, boolean compressed, int index) 
{
	byte[] b1 = removeSigScript(usigTx, index);														// Löscht alle SigScripts bis auf eine (index) aus der Tx	
	byte[] sigHash = Calc.getHashSHA256(Calc.getHashSHA256(b1));									// Signatur-Hash wird gebildet
	// System.out.println("TxToSig "+index+":     "+Convert.byteArrayToHexString(b1));				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen
	// System.out.println("SigHash "+index+":     "+Convert.byteArrayToHexString(sigHash));			// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen
	Secp256k1 secp = new Secp256k1();																
	BigInteger[] sig  = secp.sig(sigHash, privKey, k);  												
	if(sig[1].compareTo(new BigInteger("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0",16)) > 0) // Y-Koordinate auf der Elliptischen Kurve muss immer auf den positiven Wert gesetzt werden. (Bip0062)
	{  sig[1] =        (new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16)).subtract(sig[1]);}
	byte[] sigR = sig[0].toByteArray();																// Die fertige Signatur R
	byte[] sigS = sig[1].toByteArray();																// Die fertige Signatur S	
	byte[] pub	= Calc.getPublicKey(privKey, compressed);											// wird der PublicKey aus dem PrivKey berechnet.	
	// Das SigScript wird nun von hinten in umgekehrter Reihenfolge erstellt
	ByteArrayList out = new ByteArrayList(pub);														// Public-Key wird eingefügt
	out.insert((byte)pub.length, 0);																// Public-Key Länge wird eingefügt
	out.insert((byte)0x01, 0);																		// Hash-Code 0x01 wird eingefügt
	out.insert(sigS, 0);																			// Sig-S wird eingefügt
	out.insert((byte)sigS.length, 0);																// Sig-S Länge wird eingefügt
	out.insert((byte)0x02, 0);																		// 0x02 wird eingefügt
	out.insert(sigR, 0);																			// Sig-R wird eingefügt
	out.insert((byte)sigR.length, 0);																// Sig-R Länge wird eingefügt
	out.insert((byte)0x02, 0);																		// 0x02 wird eingefügt
	out.insert((byte)(sigR.length + sigS.length + 4), 0);											// Sig-R Länge wird eingefügt
	out.insert((byte)0x30, 0);																		// 0x30 wird eingefügt
	out.insert((byte)(sigR.length + sigS.length + 7), 0);											// Länge der Signatur ohne Pub-Key
	out.insert((byte)out.size(), 0);																// Länge des gesamten Scriptes wird eingefügt.
	return out.getArrayAll();
}
	



// Test: Mit 3 Inputs erfolgreich getestet! Methode funktioniert gut!
/** Returns the Legacy sighash for a transaction (signed or unsigned) for a specific input.
 For each transaction input, a different sighash is generated!
 The previous transaction's PK-Script is needed for sighash computation.
 @param tx unsigned or signed transaction.
 @param pkScript The Pk-Script of the previous Tx to which this Signature-Hash refers.
 @param txIndex The transaction index of the Tx-In whose Signature-Hash is to be calculated.
 @return Signature-Hash of this transaction for a specific Tx-In.    
 Successfully tested for standard transactions!
 1. All transaction signatures are removed and replaced with (Compact-Size) 0x00.
 2. The provided PK-Script of the previous transaction is inserted at the desired Signature position.
 3. Hash-Code 0x01000000 is appended
 4. This corresponds to the original unsigned transaction and is then hashed with SHA256². 
 Do not use for Witness-Tx! For Witness-Tx, use the "TxSigniererWitness" class. 
 Does NOT work for Legacy inputs embedded in Witness transactions!  **/
public static byte[] getSigHash(Transaktion tx, byte[] pkScript, int txIndex) throws Exception 
{
	if(tx.isWitness)	throw new Exception("Witness transaction cannot be signed in Legancy Class!");																			
	else																						
	{	
		ByteArrayList list = new ByteArrayList(tx.getRawTx());
		for(int i=tx.getTxInCount()-1; i>=0;i--)
		{
			int pos = tx.getSigScript_pos()[i]-1;
			list.remove(pos, pos + tx.getSigScript_len()[i]+1);
			if(i==txIndex)
			{
				list.insert(pkScript, pos);
				list.insert((byte)pkScript.length,pos);
			}
			else list.insert((byte)0x00, pos);	
		}	
		byte[] b = {0x01, 0x00, 0x00, 0x00};
		list.add(b);
		byte[] uSigTx =  list.getArrayAll();
		// System.out.println("TxToSig "+txIndex+":     "+Convert.byteArrayToHexString(uSigTx));			// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen
		return  Calc.getHashSHA256(Calc.getHashSHA256(uSigTx));
	}	
}





// --------------------------------------------------------------------------------------------- Additional methods --------------------------------------------------------------



//Löscht alle SigScripts aus der Transaktion, bis auf eine Einzige, die in "notRemove" markiert wird.
//Sollen alle gelöscht werden, dann im "notRemove" Feld -1 eintragen.
private static byte[] removeSigScript(byte[] usigTx, int notRemove)
{
	Transaktion uTx = new Transaktion(usigTx,0);
	byte[] tx2 = usigTx.clone();
	ByteArrayList b = new ByteArrayList(tx2);
	for(int i = uTx.getTxInCount()-1; i>=0; i--)
	{
		if(i!=notRemove)
		{
			b.remove(uTx.getSigScript_pos()[i]-1, uTx.getSigScript_pos()[i]+uTx.getSigScript_len()[i]);
			b.insert((byte)0x00, uTx.getSigScript_pos()[i]-1);
		}
	}
	return b.getArrayAll();
}




/** Selects the correct private keys from the provided list by matching against PK scripts.
 Returns them in the order needed for signing. Throws if any needed key is not found. **/
public static byte[][]	calcPrivKeyList(byte[] usigTx, byte[][] privTestKeys, byte[] pref_PrivKey, byte[] magic) throws Exception 
{
	Transaktion tx = new Transaktion(usigTx,0);
	byte[][] b_pk = tx.getSigScript(); 									// Die PK-Scripte, an der Stelle der späteren Sig-Scripte 
	byte[][] out = new byte[b_pk.length][];								// Rückgabe-Array wird initialisiert
	for(int i=0; i<b_pk.length; i++)									// Die PK-Scripte werden durchlaufen
	{		
		PkScript pk = new PkScript(b_pk[i]);
		byte[] h160 = pk.getHash160();
		for(int j=0; j<privTestKeys.length; j++)						// Die Liste der Test-Keys wird durchlaufen
		{
			PrvKey priv = new PrvKey(privTestKeys[j], pref_PrivKey);
			if(Arrays.equals(h160, priv.getHash160BitcoinAddress(false))) {out[i] = privTestKeys[j];	break;};
			if(Arrays.equals(h160, priv.getHash160BitcoinAddress(true)))  {out[i] = privTestKeys[j];	break;};
			if(Arrays.equals(h160, priv.getHash160_RedeemScript_P2SH()))  {out[i] = privTestKeys[j];	break;};
			if(j==privTestKeys.length-1) throw new IOException("Private key to sign the address: "+ pk.getBitcoinAddress(magic) +" could not be found!");
		}
	}
	return out;
}
}