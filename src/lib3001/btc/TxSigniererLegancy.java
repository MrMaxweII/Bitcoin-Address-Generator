package lib3001.btc;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import lib3001.crypt.Calc;
import lib3001.ecdsa.Secp256k1;
import lib3001.java.ByteArrayList;




/*******************************************************************************************************************************************
*		V1.3.0   									Autor: Mr. Maxwell   												vom 08.01.2024		*
*																																			*
* 		LIB3001 Bibliotheks Klasse																											*
*		Dieser Transaktions-Singnierer signiert unsignierte Legancy Transaktionen.  (Keine Witness Tx Verwenden)							*
*		static Klasse ohne Konstruktor																										*
*		Klasse getestet! Funktioniert für alle Legancy-Transaktionen (mit mehreren Inputs und Outputs) fehlerfrei ab Version V1.2.0!		*
********************************************************************************************************************************************/





public class TxSigniererLegancy 
{
	final static byte[] 	MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};			// magic
	final static byte[] 	TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};			// magic
	final static byte[] 	PrevPrivMAINNET = {(byte) 0x80};			
	final static byte[] 	PrevPrivTESTNET3 = {(byte) 0xef};			
	
	
	
	
	
	
/**	Signiert eine unsignierte Legancy Transaktion mit mehreren Tx-In und Ausgängen.  Keine Witness Transaktionen verwenden!
 	Der Hash-Code am ende der Tx "01000000" wird hier eingefügt!  (Tx wird ohne Hash-Code übergeben)
	Da jeder Tx-Eingang separat signiert werden muss, werden die Attribute als Array angefordert. Die Länge der Arrays ergibt sich aus der Tx-In Anzahl.
	Beispiel: Wenn die TX 27 Eingänge enthält, müssen natürlich auch 27 Private-Keys, 27 Zufallszahlen, und 27 Compressed-Flags übergeben werden.
	@param usigTx Unsignierte Transaktion, die signiert werden soll. (Ohne Hash-Code am Ende)  
	@param privKey Ein Array mit der Anzahl an Private-Keys von Tx-In Eingängen.
	@param k Ein Array mit der Anzahl an Zufallszahlen von Tx-In Eingängen
	@param compressed Boolean-Array, Für jede Tx-In muss die Angabe über das Compressed Flag erfolgen.
	@return Signierte Raw-Transaktion als Byte-Array in Protokoll-Format die so gesendet werde kann. **/
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
	
	

	
	
/**	Hier wird ein Signatur-Skript (sigScript) für eine einzelne Eingabe erzeugt/signiert.
	Dazu werden alle anderen Signatur-Scripte entfernt und nur das zu signierende gelassen und dann der Sig-Hash generiert.
	@param index der zu signierenden Tx-Eingabe. 0 = die erste Eingabe usw.
	@param usigTx Unsignierte Transaktion, aus der ein SigScript signiert werden soll
	@param privKey der Private-Key mit dem diese Eingabe signiert werden soll
	@param k Zufalls-Zahl
	@param compressed Wenn true, wird der Pub-Key komprimiert. 
	@return Das SigScript für diesen Tx-Eingang **/
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
/**	Gibt den Signature-Hash der Legancy Transaktion zurück. Es können Signiert oder Unsignierte Transaktionen verwedet werden.
Jeder Transaktions-Eingang muss einzeln signiert werden und es gibt deswegen auch für jede Tx-In einen anderen Signature-Hash!
Um den Signature-Hash erstellen zu können ist das PK-Script der vorherigen Transaktion notwendig.
@param tx unsignierte oder signierte Transakion.
@param pkScript Das Pk-Script der vorherigen Tx auf die sich dieser Signature-Hash bezieht.
@param txIndex Der Transaktions-Index der Tx-In dessen Signature-Hash berechnet werden soll.
@return Signature-Hash dieser Transaktion für eine bestimmte Tx-In.    
Ist erfolgreich getestet für Standard-Transaktionen!
1. Alle Signaturen der Transaktion werden entfernt und durch (Compact-Size) 0x00 ersetzt.
2. Das übergebene PK-Script der vorherigen Transaktion wird an die (txIndex) gewünschte Stelle der Signature eingefügt.
3. Hash-Code 0x01000000 wird hinten angehängt
4. Dies entspricht dann der ursprünglichen unsignierten Transaktion und wird dann mit SHA256² gehascht. 
Keine Witness-Tx hier verwenden! Für Witness-Tx gibt es eine eigene Klasse: "TxSigniererWitness". 
Funktioniert auch NICHT für Legancy-Inputs die in Witness-Transaktionen eingebettet sind!  **/
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





// --------------------------------------------------------------------------------------------- Zusätzliche Methoden --------------------------------------------------------------



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




/**	Sucht die richtigen Private-Keys aus der übergebenen Liste heraus und und gibt sie in der Reihenvolge zurück, wie sie zum signieren benötigt werden.
Dabei werden alle übergebenen Private-Keys in allen möglichen Formaten getestet, ob sie für sie für einen Eingang dieser Tx gültig sind. (Brute-Forcing-Methode)
Es können beliebig viele Priv-Test-Keys übergeben werden. Unter Beachtung der Laufzeit dieser Methode.
Funktionsbeschreibung: Aus jedem Test-Priv-Key wird eine BTC-Adresse (Hash160) erstellt und mit dem PK-Script in der Unsignierten Tx verglichen. Bei Übereinstimmung, ist der Priv.Key richtig.
Vorraussetztung ist, dass in der unsignierten Tx die PK-Scripte der vorherigen Tx vorhanden sind. (Temporär im Signatur-Feld)
Da die übergebene PrivKey-Liste groß sein kann, sollte hier etwas auf die Laufzeit geachtet werden!
@param usigTx Unsignierte Tx, aus der die Private-Keys gesucht werden sollen.
@param privTestKeys Eine Liste von Private-Keys, beliebiger Länge, aus der die richtigen Priv.Keys gesucht werden.
@param pref_PrivKey Die CoinParameter müssen übergeben werden, zur Identifizierung des jeweiligen Coin. Siehe CoinParameter Class. (Bitcoin-MainNet=0x80, Bitcoin-TestNet=0xEF)
@param magic, TestNet oder MainNet (MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9}; TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};)
@return Werden alle benötigten Priv-Keys gefunden, wird ein Array mit den richtigen Priv.keys, in der richtigen Reihenvolge und Länge, zum signieren zurückgegeben. 
@throws Exception Wird nur ein benötiger Priv.Key nicht gefunden, wird eine Exceptin geworfen.		**/
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