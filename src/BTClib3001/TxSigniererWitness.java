package BTClib3001;
import java.math.BigInteger;
import java.util.Arrays;
import ECDSA.Secp256k1;



/*******************************************************************************************************************************************
* V1.1                                            Author: Mr. Nickolas-Antoine B.                                             20.01.2024              *
* BTClib3001 class                                                                                                                          *
* Signs unsigned Witness transactions (only Witness here, no Legacy).                                                                        *
* - Computes sighash                                                                                                                         *
* - Can verify signatures for Witness transactions                                                                                           *
* Static class without constructor                                                                                                           *
* Supports multiple inputs/outputs                                                                                                           *
* Implemented Witness types:                                                                                                                 *
* - P2SH multisig addresses (start with 3; 2 in testnet). Single signature only.                                                             *
* - P2WPKH Bech32 addresses                                                                                                                  *
* - Legacy inputs embedded in Witness transactions (requires at least one SegWit input)                                                      *
********************************************************************************************************************************************/
/***********************************************************************************************
* Witness-Unsigned-Tx Protocol: Version 1.0                                                                                               *
* Unsigned Witness transactions are not part of the Bitcoin protocol and thus not uniformly specified.                                   *
* They exist only within wallets as custom code; therefore a defined format is required.                                                  *
* This documents the format of unsigned Witness transactions for this software.                                                           *
* Refer to BIP-0143: https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki                                                       *
************************************************************************************************/




public class TxSigniererWitness 
{
	final static byte[] 	MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};			// magic
	final static byte[] 	TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};			// magic
	final static byte[] 	PrevPrivMAINNET = {(byte) 0x80};			
	final static byte[] 	PrevPrivTESTNET3 = {(byte) 0xef};			
	
	

	
	
	/** Signs an unsigned Witness transaction with multiple inputs/outputs. No Legacy transactions here!
 Arrays per input for private keys, random k, and compressed flag where applicable. **/
public static byte[] sigTx(byte[] usigTx, byte[][] privKey, byte[][] k, boolean[] compressed) throws Exception 
{
	ByteArrayList wData	= new ByteArrayList(new byte[0]);											// Witness-Daten werden hier zusammengehängt
	Transaktion uTx 	= new Transaktion(usigTx,0);												// Unsignierte Tx
	Witness wit			= new Witness(uTx.getWitness(),0,uTx.getTxInCount());						// Die Witness-Daten in der UsigTx, enthalten die Input-Beträge.
	byte[][] ww			= wit.getWitnessSignature();												// Die Witness-Daten in der UsigTx, enthalten die Input-Beträge. Als Byte-Array´s
	byte[][] prevPk 	= uTx.getSigScript();														// vorherige Pk-Scripte sind hier enthalten.	
	byte[][] sigScript	= new byte[uTx.getTxInCount()][];											// Signature-Scripte für das Witness-Feld
	for(int i=0;i<uTx.getTxInCount();i++)  															// Witness-Felder werden durchlaufen
	{
		if(prevPk[i][0]==(byte)0xa9)																// P2SH Input
		{
			byte[] preValue = Arrays.copyOfRange(ww[i], 1, 9);
			sigScript[i] = sigScript_P2SH(usigTx, privKey[i], preValue, k[i], i);					// Erstellt die Witness-Signaturen
		}
		else if(prevPk[i][0]==(byte)0x00)															// P2WPK Bech32 Input
		{
			byte[] preValue = Arrays.copyOfRange(ww[i], 1, 9);
			sigScript[i] = sigScript_P2WPKH(usigTx, privKey[i], preValue, k[i], i);					// Erstellt die Witness-Signaturen
			usigTx = trimTx(usigTx);																// Tx muss ggf. auf die richtige Länge gekürzt werden, weil die Methode sigScript_P2WPKH() dies erfordert. (geht nicht anders)
		}
		else if(prevPk[i][0]==(byte)0x76)															// Legancy-Adressen
		{
			sigScript[i] = new byte[1];																// Erzeugt ein leeres Witness-SigScript
			byte[] sigLegancy = sigScript_Legancy(usigTx, privKey[i], k[i], compressed[i], i);		// Berechnet das Legancy-SigScript
			usigTx =  switchSigScript(usigTx, sigLegancy, i);										// Schiebt das Legancy-SigScript in das Legancy-SigScript Feld.
		}
		else throw new Exception("Input "+i+" cannot be signed.\nUnknown script type.");
		wData.add(sigScript[i]);																	// Fügt die Witness-Daten zusammen
	}		
	Transaktion txnew 		= new Transaktion(usigTx,0);											// Angepasste Unsignierte Tx
	int posW = txnew.witness_pos;																	// Position der Witness-Daten in der angepassten Tx
	int lenW = txnew.witness_len;																	// Länge der Witness-Daten
	ByteArrayList outTx = new ByteArrayList(usigTx);												// Signierte Tx, als Byte-Array-List, die zur Ausgabe führt											
	outTx.remove(posW, posW+lenW);																	// alte default Witness-Daten werden entfernt
	outTx.insert(wData.getArrayAll(), posW);														// neue Witness-Daten mit den Signaturen werden final eingefügt.
	return outTx.getArrayAll();																		// Signierte Tx, wird nach Byte-Array Konvertiert und ausgegeben
}
	





// ------------------------ Compute individual sig-scripts ------------------------------





// Methode fertig und mehrfach mit verschiedenen Inputs getestet. Auch Compressed oder Uncompressed funktioniert! 
// Computes signature script for Legacy inputs embedded in a Witness transaction using Legacy sighash.
// Für diesen Fall muss die ganze Transakion temporär in eine Legancy-Tx konviertiert werden um dort den Signature-Hash im Legancy-Format zu berechnen.
// Dafür werden alle Witness-Felder und das Witness-Flag entfernt. Alle anderen Signature-Felder bleiben auch leer, bis auf das eigene, in das das alte Pk-Script eingefügt wird.
// Das Signature-Script wird anschließend in der Klasse TxSigniererLegancy berechnet.
private static byte[] sigScript_Legancy(byte[] wUsigTx, byte[] privKey, byte[] k, boolean compressed, int index)
{
	byte[] legancyTx = witnessToLegancy(wUsigTx);										// Witness-Tx wird in Legancy-Tx convertiert			
	byte[] uTx = Arrays.copyOfRange(legancyTx, 0, legancyTx.length+4);					// Hash-Code wird angehängt
	uTx[uTx.length-4] = 0x01;															// Hash-Code wird angehängt	
	return TxSigniererLegancy.sigScript(uTx, privKey, k, compressed, index);					// SigScript wird als Legancy-Tx berechnet.
}







	// Methode fertig, wurde mehrmals mit min.4 Inputs getestet.
	// Computes signature script for P2WPKH (Bech32) input.
	/*	Der Input der hier signiert werden soll, muss ??? noch offen
	@param index der zu signierenden Tx-Eingang. 0 = die erste Eingabe usw.
	@param usigTx Unsignierte Tx. Muss als Witness-Tx vorliegen. Achtung! Der Wert dieser Unsignierten Tx, wird durch die Methode verändert! Danach ist diese Tx aber zu lang und muss mit trimTx() gekürzt werden!
	@param privKey der Private-Key mit dem diese Eingabe signiert werden soll
	@param k Zufalls-Zahl
	@return Das Witness-SigScript für diesen Tx-Eingang. Wird später in den Witness-Bereich gelegt.*/
private static byte[] sigScript_P2WPKH (byte[] usigTx, byte[] privKey, byte[] valueRaw, byte[] k, int index) throws Exception 
{
	Transaktion txU = new Transaktion(usigTx,0);
	byte[] pkScriptPrev = txU.getSigScript()[index];												// Das vorherige Pk-Script wird aus dem SigScript codiert und bereitgestellt
	//System.out.println("PkScriptPrev: "+Convert.byteArrayToHexString(pkScriptPrev));				// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen hier einfügen!
	byte[] sigScript = {0x00};																		// Leeres SigScript wird vorbereitet
	byte[] txNew = switchSigScript(usigTx, sigScript, index);										// Tauscht das SigScript mit einem leerem SigScript aus. (vorher war dort das prev-PK-Script enthalten. 
	//System.out.println(Convert.byteArrayToHexString(txNew));										// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen hier einfügen!
	System.arraycopy(txNew, 0, usigTx, 0, txNew.length);											// in "usigTx" wird nun die neue Tx mit dem "leerem SigScript" hard-Kopiert. (Damit wird die übergebene Variable verändert!)
	//System.out.println(Convert.byteArrayToHexString(usigTx));										// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen hier einfügen!
	Transaktion tx = new Transaktion(usigTx,0);
	byte[] sigHash = getSigHash_P2WPKH(tx, pkScriptPrev, valueRaw, index);
	//System.out.println("SigHash "+index+":     "+Convert.byteArrayToHexString(sigHash));  		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen hier einfügen!
	Secp256k1 secp = new Secp256k1();																
	BigInteger[] sig  = secp.sig(sigHash, privKey, k);  												
	if(sig[1].compareTo(new BigInteger("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0",16)) > 0) // Y-Koordinate auf der Elliptischen Kurve muss immer auf den positiven Wert gesetzt werden. (Bip0062)
	{  sig[1] =        (new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16)).subtract(sig[1]);}
	byte[] sigR = sig[0].toByteArray();																// Die fertige Signatur R
	byte[] sigS = sig[1].toByteArray();																// Die fertige Signatur S	
	int lenPub	= 33;																				// Länge des Compressed-Pub-Key ist immer 65 Byte
	int lenR	= sigR.length;
	int lenS 	= sigS.length;																		// Die Länge Sig-S
	//--------- Ende Signatur-Berechnung, Begin SigScript-Erstellung
	byte[] ws 	= new byte[lenPub + lenR + lenS + 10];  											// Die Witness-Daten
	ws[0]	= 0x02;																					// Anzahl der Witness-Blöcke (Immer 2)
	ws[1]	= (byte) (lenR + lenS + 7);																// Byte-Länge Block 1
	ws[2]	= 0x30;																					// Fest codiert
	ws[3]	= (byte) (lenR + lenS + 4);																// Länge R + S
	ws[4]	= 0x02;																					// Fest Codiert
	ws[5]	= (byte) lenR;																			// Länge R
	System.arraycopy(sigR, 0, ws, 6, lenR);															// Sig-R wird in das Array kopiert
	ws[lenR+6] = 0x02; 																				// 0x02 fest codiert
	ws[lenR+7] = (byte) lenS;																		// Länge S
	System.arraycopy(sigS, 0, ws, lenR+8, lenS);													// Sig-S wird in das Array kopiert
	ws[lenR+lenS+8] = 0x01;																			// Hash-Code fest codiert
	ws[lenR+lenS+9] = 0x21;																			// Länge Block 2 ist immer 65 Byte, da es immer der compressed Pub-Key ist.	
	byte[] pub	= Calc.getPublicKey(privKey, true);													// PublicKey wird aus Private-Key berechnet
	System.arraycopy(pub, 0, ws, lenR+lenS+10, lenPub);												// Pub-Key wird kopiert.
	return ws;	
}




	
	
	

	
	// Methode fertig, wurde für 4 P2SH Inputs getestet!
	// Computes signature script for P2SH-P2WPKH input.
	/*	Der Input der hier signiert werden soll, muss im SigScript ein spezielles Pk-Script enthalten: (Beispiel: 160014a53c44c87003ec71ca414448492e198bb3aaa3c3)
	@param index der zu signierenden Tx-Eingang. 0 = die erste Eingabe usw.
	@param usigTx Unsignierte Tx. Muss als Witness-Tx vorliegen. Achtung! Der Wert dieser Unsignierten Tx, wird durch die Methode verändert!
	@param privKey der Private-Key mit dem diese Eingabe signiert werden soll
	@param k Zufalls-Zahl
	@return Das Witness-SigScript für diesen Tx-Eingang. Wird später in den Witness-Bereich gelegt.*/
private static byte[] sigScript_P2SH(byte[] usigTx, byte[] privKey, byte[] valueRaw, byte[] k, int index) throws Exception 
{
	byte[] pub	= Calc.getPublicKey(privKey, true);													// PublicKey wird aus Private-Key berechnet
	byte[] h160 = Calc.getHashRIPEMD160(Calc.getHashSHA256(pub));
	byte[] scriptInSig = {0x17,0x16,0x00,0x14,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	System.arraycopy(h160, 0, scriptInSig, 4, 20);													// scriptInSig = Hier ist nun das Script, welches bei P2SH sich im Legancy-Script-Feld befindet. (Beispie: 160014404c836c0ecbc42d1b20b999814319b17327100f)	
	byte[] txNew = switchSigScript(usigTx, scriptInSig, index);										// Tauscht das SigScript mit der nr."index" gegen ein neues Script: "newScript" aus. 
	System.arraycopy(txNew, 0, usigTx, 0, txNew.length);											// in "usigTx" wird nun die neue Tx mit dem "newScript" hard-Kopiert. (Damit wird die übergebene Variable verändert!)
//	System.out.println(Convert.byteArrayToHexString(usigTx));										// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen hier einfügen!
	Transaktion tx = new Transaktion(usigTx,0);
	byte[] sigHash = getSigHash_P2SH_P2WPKH(tx, valueRaw, index);
//	System.out.println("SigHash "+index+":     "+Convert.byteArrayToHexString(sigHash));  			// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Zum Debuggen hier einfügen!
	Secp256k1 secp = new Secp256k1();																
	BigInteger[] sig  = secp.sig(sigHash, privKey, k);  												
	if(sig[1].compareTo(new BigInteger("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0",16)) > 0) // Y-Koordinate auf der Elliptischen Kurve muss immer auf den positiven Wert gesetzt werden. (Bip0062)
	{  sig[1] =        (new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16)).subtract(sig[1]);}
	byte[] sigR = sig[0].toByteArray();																// Die fertige Signatur R
	byte[] sigS = sig[1].toByteArray();																// Die fertige Signatur S	
	int lenPub	= 33;																				// Länge des Compressed-Pub-Key ist immer 65 Byte
	int lenR	= sigR.length;
	int lenS 	= sigS.length;																		// Die Länge Sig-S
	//--------- Ende Signatur-Berechnung, Begin SigScript-Erstellung
	byte[] ws 	= new byte[lenPub + lenR + lenS + 10];  											// Die Witness-Daten
	ws[0]	= 0x02;																					// Anzahl der Witness-Blöcke (Immer 2)
	ws[1]	= (byte) (lenR + lenS + 7);																// Byte-Länge Block 1
	ws[2]	= 0x30;																					// Fest codiert
	ws[3]	= (byte) (lenR + lenS + 4);																// Länge R + S
	ws[4]	= 0x02;																					// Fest Codiert
	ws[5]	= (byte) lenR;																			// Länge R
	System.arraycopy(sigR, 0, ws, 6, lenR);															// Sig-R wird in das Array kopiert
	ws[lenR+6] = 0x02; 																				// 0x02 fest codiert
	ws[lenR+7] = (byte) lenS;																		// Länge S
	System.arraycopy(sigS, 0, ws, lenR+8, lenS);													// Sig-S wird in das Array kopiert
	ws[lenR+lenS+8] = 0x01;																			// Hash-Code fest codiert
	ws[lenR+lenS+9] = 0x21;																			// Länge Block 2 ist immer 65 Byte, da es immer der compressed Pub-Key ist.	
	System.arraycopy(pub, 0, ws, lenR+lenS+10, lenPub);												// Pub-Key wird kopiert.
	return ws;	
}
	
	
	
	

// ---------------------------- Compute sighash -----------------------------------------


//Ist erfolgreich getestet. Für Compressed und Uncompresset Legancy-Transaktion!
/** Returns sighash for Legacy inputs embedded in SegWit transactions. */
public static byte[] getSigHash_Legancy(Transaktion tx, byte[] pkScript, int txIndex) throws Exception 
{
	byte[] b = witnessToLegancy(tx.getRawTx());
	Transaktion txL = new Transaktion(b,0); 
	return TxSigniererLegancy.getSigHash(txL, pkScript, txIndex);
}





// Ist erfolgreich getestet für Witness-Transaktionen!
/** Returns sighash for P2WPKH (Bech32) input. */
public static byte[] getSigHash_P2WPKH(Transaktion tx, byte[] pkScript, byte[] valueRaw, int txIndex) throws Exception 
{
	if(tx.isWitness==false)	throw new Exception("Legancy transaction cannot be signed in Witness Class!");																			
	byte[][] txPrev = tx.getTxPrevHashNoSwap();
	byte[][] prevIndex = tx.getTxPrevIndexByte();
	ByteArrayList list = new ByteArrayList(new byte[0]);
	for(int i=0; i<tx.getTxInCount(); i++)
	{
		list.add(txPrev[i]);
		list.add(prevIndex[i]);
	}		
	byte[] nVersion = tx.getVersion_byte();													// nVersion 	aus der github-Doku: bip-0143
	byte[] hashPrevouts = Calc.getHashSHA256(Calc.getHashSHA256(list.getArrayAll()));		// hashPrevouts aus der github-Doku: bip-0143
	list = new ByteArrayList(new byte[0]);
	for(int i=0; i<tx.getTxInCount(); i++) list.add(tx.getSequence()[i]);
	byte[] hashSequence = Calc.getHashSHA256(Calc.getHashSHA256(list.getArrayAll()));		// hashSequence aus der github-Doku: bip-0143
	list = new ByteArrayList(new byte[0]);
	list.add(txPrev[txIndex]); list.add(prevIndex[txIndex]);
	byte[] outpoint 	= list.getArrayAll();												// outpoint aus der github-Doku: bip-0143			
	PkScript pk 		= new PkScript(pkScript);
	byte[] b 			= {0x19,0x76,(byte)0xa9,0x14};
	list = new ByteArrayList(b);
	list.add(pk.getHash160());
	list.add((byte) 0x88); list.add((byte) 0xac);
	byte[] scriptCode 	= list.getArrayAll();												// scriptCode aus der github-Doku: bip-0143
	byte[] amount 		= valueRaw;															// amount scriptCode aus der github-Doku: bip-0143
	byte[] nSequence 	= tx.getSequence()[txIndex];										// nSequence scriptCode aus der github-Doku: bip-0143
	byte[] hashOutputs 	= new byte[tx.witness_pos - tx.value_pos[0]];
	System.arraycopy(tx.getRawTx(), tx.value_pos[0], hashOutputs, 0, hashOutputs.length);
	hashOutputs 		= Calc.getHashSHA256(Calc.getHashSHA256(hashOutputs));				// hashOutputs aus der github-Doku: bip-0143
	byte[] nLockTime 	= tx.getLockTime();													// nLockTime aus der github-Doku: bip-0143
	byte[] nHashType 	= {1,0,0,0};														// nHashType aus der github-Doku: bip-0143	
	list = new ByteArrayList(nVersion);
	list.add(hashPrevouts);
	list.add(hashSequence);
	list.add(outpoint);
	list.add(scriptCode);
	list.add(amount);
	list.add(nSequence);
	list.add(hashOutputs);
	list.add(nLockTime);
	list.add(nHashType);	
	// System.out.println("before SigHash: "+Convert.byteArrayToHexString(list.getArrayAll()));
	return Calc.getHashSHA256(Calc.getHashSHA256(list.getArrayAll()));
}





// Methode ist fertig, wurde einmal mit 2 Inputs erfolgreich getestet.!
// Nach Doku: https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki
/** Returns sighash for P2SH_P2WPKH input. */
public static byte[] getSigHash_P2SH_P2WPKH(Transaktion tx, byte[] valueRaw, int txIndex) throws Exception
{
	if(tx.isWitness==false)	throw new Exception("Legancy transaction cannot be signed in Witness Class!");																			
	byte[][] txPrev = tx.getTxPrevHashNoSwap();
	byte[][] prevIndex = tx.getTxPrevIndexByte();
	ByteArrayList list = new ByteArrayList(new byte[0]);
	for(int i=0; i<tx.getTxInCount(); i++)
	{
		list.add(txPrev[i]);
		list.add(prevIndex[i]);
	}		
	byte[] nVersion = tx.getVersion_byte();												// nVersion 	aus der github-Doku: bip-0143
	byte[] hashPrevouts = Calc.getDoubleHashSHA256(list.getArrayAll());					// hashPrevouts aus der github-Doku: bip-0143				
	list = new ByteArrayList(new byte[0]);
	for(int i=0; i<tx.getTxInCount(); i++) list.add(tx.getSequence()[i]);
	byte[] hashSequence = Calc.getDoubleHashSHA256(list.getArrayAll());					// hashSequence aus der github-Doku: bip-0143   
	list = new ByteArrayList(new byte[0]);
	list.add(txPrev[txIndex]); list.add(prevIndex[txIndex]);
	byte[] outpoint 	= list.getArrayAll();											// outpoint aus der github-Doku: bip-0143	
	byte[] scriptCode = new byte[26];
	scriptCode[0] = (byte) 0x19;
	scriptCode[1] = (byte) 0x76;
	scriptCode[2] = (byte) 0xa9;
	scriptCode[3] = (byte) 0x14;
	scriptCode[24] = (byte) 0x88;
	scriptCode[25] = (byte) 0xac;
	System.arraycopy(tx.getSigScript()[txIndex], 3, scriptCode, 4, 20);    				// scriptCode aus der github-Doku: bip-0143
	byte[] amount 		= valueRaw;														// amount scriptCode aus der github-Doku: bip-0143
	byte[] nSequence 	= tx.getSequence()[txIndex];									// nSequence scriptCode aus der github-Doku: bip-0143
	byte[] hashOutputs 	= new byte[tx.witness_pos - tx.value_pos[0]];
	System.arraycopy(tx.getRawTx(),tx.value_pos[0],hashOutputs,0,hashOutputs.length);
	hashOutputs = Calc.getDoubleHashSHA256(hashOutputs);								// hashOutputs aus der github-Doku: bip-0143
	byte[] nLockTime 	= tx.getLockTime();												// nLockTime aus der github-Doku: bip-0143
	byte[] nHashType 	= {1,0,0,0};													// nHashType aus der github-Doku: bip-0143	
	list = new ByteArrayList(nVersion);
	list.add(hashPrevouts);
	list.add(hashSequence);
	list.add(outpoint);
	list.add(scriptCode);
	list.add(amount);
	list.add(nSequence);
	list.add(hashOutputs);
	list.add(nLockTime);
	list.add(nHashType);				
	//Zum debuggen. Nicht löschen!
	//			System.out.println("nVersion:     "+Convert.byteArrayToHexString(nVersion));		
	//			System.out.println("hashPrevouts: "+Convert.byteArrayToHexString(hashPrevouts));		
	//			System.out.println("hashSequence: "+Convert.byteArrayToHexString(hashSequence));		
	//			System.out.println("outpoint:     "+Convert.byteArrayToHexString(outpoint));		
	//			System.out.println("scriptCode:   "+Convert.byteArrayToHexString(scriptCode));		
	//			System.out.println("amount:       "+Convert.byteArrayToHexString(amount));		
	//			System.out.println("nSequence:    "+Convert.byteArrayToHexString(nSequence));
	//			System.out.println("hashOutputs:  "+Convert.byteArrayToHexString(hashOutputs));
	//			System.out.println("nLockTime:    "+Convert.byteArrayToHexString(nLockTime));
	//			System.out.println("nHashType:    "+Convert.byteArrayToHexString(nHashType));
	//			System.out.println("SigHash:      "+Convert.byteArrayToHexString(Calc.getHashSHA256(Calc.getHashSHA256(list.getArrayAll()))));
	return Calc.getHashSHA256(Calc.getHashSHA256(list.getArrayAll()));
}








// ---------------------------- Other helpers -------------------------------------------


// Swap sigScript at given index with newScript
private static byte[] switchSigScript(byte[] tx, byte[] newScript, int index)
{
	Transaktion uTx = new Transaktion(tx,0);
	byte[] tx2 = tx.clone();
	ByteArrayList b = new ByteArrayList(tx2);	
	b.remove(uTx.getSigScript_pos()[index]-1, uTx.getSigScript_pos()[index]+uTx.getSigScript_len()[index]);
	b.insert(newScript, uTx.getSigScript_pos()[index]-1);
	return b.getArrayAll();
}	



/** Converts a Legacy transaction to a Witness transaction by inserting witness flag and minimal witness data. **/
public static byte[] legancyToWitness(byte[] txb)
{
	Transaktion tx = new Transaktion(txb,0);
	byte[] tx2 = txb.clone();
	ByteArrayList b = new ByteArrayList(tx2);
	for(int i=0; i<tx.getTxInCount(); i++)
	{
		b.insert(new byte[]{01,0x00}, tx.lockTime_pos); // Für jeden Tx-Input muss ein leerer Witness-Block eingefügt werden!
	}
	b.insert(new byte[]{00,01}, 4);
	return b.getArrayAll();
}



/** Converts a Witness transaction to a Legacy transaction by removing witness flag and witness data. **/
public static byte[] witnessToLegancy(byte[] txb)
{
	Transaktion tx = new Transaktion(txb,0);
	byte[] tx2 = txb.clone();
	ByteArrayList b = new ByteArrayList(tx2);
	b.remove(tx.witness_pos, tx.lockTime_pos);
	b.remove(4, 6);
	return b.getArrayAll();
}



// Trims a transaction to correct length if it became too long.
// Die Methode "sigScript_P2WPKH()" verändert eine tx die als Parameter übergeben wird. Die Tx ist dann aber zu lang (geht nicht anders).  Hier wird sie dann gekürzt.
private static byte[] trimTx(byte[] in)
{
	Transaktion tx = new Transaktion(in, 0);
	return Arrays.copyOf(in, tx.end());
}
}