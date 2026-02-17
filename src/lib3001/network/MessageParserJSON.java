package lib3001.network;
import java.net.ConnectException;
import java.sql.Timestamp;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lib3001.btc.Witness;
import lib3001.crypt.Calc;
import lib3001.crypt.Convert;




	/************************************************************************************************************************************
	 *	Version 1.1	vom 01.01.2021																										*
	 * 	LIB3001 Bibliotheks Klasse																										*
	 *	Diese statische Klasse parst empfangene Nachrichten aus dem BTC P2P-Netzwerk vom rohen Byte-Code in das lesbare JSON Format.	*
	 *	Dafür wird der rohe Datensatz der vollständigen Nachricht als Byte-Array übergeben.												*
	 *	Dieses Konvertieren in das JSON-Format kostet Ressourcen und wird daher nur für Diagnosezwecke oder Logs etc. empfohlen.		*
	 ************************************************************************************************************************************/




public class MessageParserJSON
{

	// Die Befehls-Sätze als Long interpretiert.
	final static long VERSION		= 	8531350909138988544L;
	final static long VERACK		=	8531350831728623616L;
	final static long GETADDR		=	7450489120199307776L;
	final static long ADDR			=	7017844560139124736L;
	final static long INV			=	7597139363792683008L;
	final static long GETDATA		=	7450489133034922240L;
	final static long SENDHEADERS	=	8315173664536748388L;
	final static long GETHEADERS	=	7450489150280655973L;
	final static long HEADERS		=	7522525836418904832L;
	final static long TX			=	8392457905604919296L;
	final static long BLOCK			=	7092165985989296128L;
	final static long GETBLOCKS		=	7450489124629209963L;
	final static long MEMPOOL		=	7882827052455259136L;
	final static long FEEFILTER		=	7378415055046210661L;
	final static long NOTFOUND		=	7957707049892408932L;
	final static long REJECT		=	8243111676664348672L;
	final static long PING			=	8100126793463234560L;
	final static long PONG			= 	8101815643323498496L;
	final static long CHECKORDER	=	7163086685011407460L; // Veraltet
	final static long SUBMITORDER	=	8319664108697055090L; // Veraltet
	final static long REPLY			=	8243118304160382976L; // Veraltet
	final static long ALERT			=	7020097461410594816L; // Veraltet
	final static long FILTERLOAD	=	7379548711597272175L; // Bip 037
	final static long FILTERADD		=	7379548711597269348L; // Bip 037
	final static long FILTERCLEAR	=	7379548711597269868L; // Bip 037
	final static long MERKLEBLOCK	=	7882832528487572076L; // Bip 037
	final static long SENDCMPCT		=	8315173664453390435L; // Bip 152
	final static long cmpctblock	=	7164506154673007727L; // Bip 152
	final static long getblocktxn	=	7450489124629209963L; // Bip 152
	final static long blocktxn		=	7092165985996929134L; // Bip 152






/** parst empfangene Nachrichten aus dem BTC P2P-Netzwerk vom rohen ByteArray in das lesbare JSON Format
	@param data Übergeben wird das rohe Byte-Array der vollständigen Nachricht
	@return String im JSON Format  **/
public static String parse(byte[] data) throws JSONException, ConnectException
{
	JSONObject j = new JSONObject(); 												// ein leeres JSON-Object wird erzeugt.
	byte[] magic = new byte[4];														// magic Byte-Array wird vorbereitet
	System.arraycopy(data,0, magic, 0, 4);											// magic wird geparst
	j.put("magic", Convert.byteArrayToHexString(magic));							// magic wird in JSON geschrieben
	byte[] code = new byte[12];														// Befehlscode wird als Byte-Array vorbereitet
	System.arraycopy(data,4, code, 0, 12);											// Der Befehlscode wird geparst
	long befehl = Convert.byteArray_to_long(code);									// Befehlscode wird nach long konvertiert
	for(int i=11;i>0;i--) {if(code[i]==0)  code[i]=95; else break;}					// Angehängte Nullen im Befehlscode werden durch _ ersetzt.
	String s = new String(code);													// Der Befehls-Code wird von Byte-Array nach String in Textform konvertiert.
	s = s.replaceAll("_","");														// Das Eingefügte _ wird nun entfernt.
	byte[] payload = new byte[data.length - 24];									// In "payload" befinden sich nun die Nutzdaten die an die einzelnen -Parse Methoden weitergeleitet werden
	System.arraycopy(data,24, payload, 0, data.length - 24);						// Nutzdaten werden geparst
		// Keine Nutzdaten
		// Keine Nutzdaten
		// Keine Nutzdaten
	if((befehl == VERACK) || (befehl == GETADDR) || (befehl == SENDHEADERS) || (befehl == MEMPOOL))		{j.put("Befehls-Code", s); 	j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload));	return j.toString();}	// Keine Nutzdaten
	if(befehl == PONG)			{j.put("Befehls-Code", s); 	j.put("zufalls-Nonce", Convert.byteArrayToHexString(payload));		return j.toString();}	// 8Byte Nonce
	if(befehl == CHECKORDER)	{j.put("Befehls-Code-Old", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); return j.toString();}	// Veraltet
	if(befehl == SUBMITORDER)	{j.put("Befehls-Code-Old", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); return j.toString();}	// Veraltet
	if(befehl == REPLY)			{j.put("Befehls-Code-Old", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); return j.toString();}	// Veraltet
	if(befehl == ALERT)			{j.put("Befehls-Code-Old", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); return j.toString();}	// Veraltet
	if(befehl == FILTERLOAD)	{j.put("Befehls-Code", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); 	return j.toString();}	// Bip 037
	if(befehl == FILTERADD)		{j.put("Befehls-Code", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); 	return j.toString();}	// Bip 037
	if(befehl == FILTERCLEAR)	{j.put("Befehls-Code", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); 	return j.toString();}	// Bip 037
	if(befehl == MERKLEBLOCK)	{j.put("Befehls-Code", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); 	return j.toString();}	// Bip 037
	if(befehl == cmpctblock)	{j.put("Befehls-Code", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); 	return j.toString();}	// Bip 152
	if(befehl == getblocktxn)	{j.put("Befehls-Code", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); 	return j.toString();}	// Bip 152
	if(befehl == blocktxn)		{j.put("Befehls-Code", s);j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload)); 	return j.toString();}	// Bip 152
	if(befehl == SENDCMPCT)		{j.put("Befehls-Code", s);	parse_sendcmpct(payload, j); 										return j.toString();}	// Bip 152
	if(befehl == VERSION)		{j.put("Befehls-Code", s); 	parse_version(payload, j); 											return j.toString(4);}
	if(befehl == ADDR)			{j.put("Befehls-Code", s);	parse_addr(payload, j);												return j.toString(4);}
	if(befehl == INV)			{j.put("Befehls-Code", s);	parse_inv(payload, j);												return j.toString();}
	if(befehl == GETDATA)		{j.put("Befehls-Code", s);	parse_getdata(payload, j);											return j.toString();}
	if(befehl == GETHEADERS)	{j.put("Befehls-Code", s);	parse_getheaders(payload, j);										return j.toString(4);}
	if(befehl == HEADERS)		{j.put("Befehls-Code", s);	parse_headers(payload, j);											return j.toString(4);}
	if(befehl == TX)			{j.put("Befehls-Code", s);	parse_tx(payload, j);												return j.toString();}
	if(befehl == BLOCK)			{j.put("Befehls-Code", s);	parse_block(payload, j); 											return j.toString(4);}
	if(befehl == GETBLOCKS)		{j.put("Befehls-Code", s); 	parse_getblocks(payload, j); 										return j.toString(4);}
	if(befehl == FEEFILTER)		{j.put("Befehls-Code", s); 	parse_feefilter(payload, j); 										return j.toString();}
	if(befehl == NOTFOUND)		{j.put("Befehls-Code", s);	parse_notfound(payload, j); 										return j.toString();}
	if(befehl == PING)			{j.put("Befehls-Code", s);	parse_ping(payload, j); 											return j.toString();}
	if(befehl == REJECT)		{j.put("Befehls-Code", s);	parse_reject(payload, j); 											return j.toString();}
	{j.put("Unbekannter Befehls-Code", s);					j.put("Nutzdaten in Hex", Convert.byteArrayToHexString(payload));	return j.toString();}}	// Wenn der Befehls-Code unbekannt ist







// Nachricht "version": Handshake, erste Nachricht die zum Start der Verbindung ausgetauscht wird
// Übergeben werden die Nutzdaten als Byte-Array
public static void parse_version(byte[] data, JSONObject j) throws JSONException, ConnectException
{
// Version (4Byte) wird geparst:  Die BitcoinCore-Version die die Gegenstelle benutzt
	byte[] b = new byte[4];
	System.arraycopy(data,0, b, 0, 4);
	Convert.swapBytes(b);
	j.put("BitcoinCore-Version" , Integer.toUnsignedString(Convert.byteArray_to_int(b)));
// Services (8Byte) wird geparst: Bitfeld der Features, die für diese Verbindung aktiviert werden sollen
	b = new byte[8];
	System.arraycopy(data,4, b, 0, 8);
	Convert.swapBytes(b);
	JSONArray ja = parse_Services(b);
	j.put("services",ja);
	j.put("services_Hex" , Convert.byteArrayToHexString(b));
// timestamp (8Byte) wird geparst: Standard-UNIX-Zeitstempel in Sekunden
	b = new byte[8];
	System.arraycopy(data,12, b, 0, 8);
	Convert.swapBytes(b);
	j.put("timestamp" , new Timestamp(Convert.byteArray_to_long(b)*1000L));
// IP-Address (26Byte) wird geparst: Die Netzwerkadresse des Knotens der Gegenstelle
	b = new byte[18];
	System.arraycopy(data,28, b, 0, 18);
	j.put("IP-Address-Gegenstelle" , ConvertIP.ByteArrayToString(b));
// IP-Address (26Byte) wird geparst: Die Netzwerkadresse des Knotens der diese Nachricht sendet
	b = new byte[18];
	System.arraycopy(data,54, b, 0, 18);
	j.put("IP-Address Sender" , ConvertIP.ByteArrayToString(b));
// Zufallszahl (8Byte) wird geparst: "Random Nonce", zufällig generiert jedes Mal, wenn ein Versionspaket gesendet wird. Diese Nonce wird verwendet, um Verbindungen zu sich selbst zu erkennen.
	b = new byte[8];
	System.arraycopy(data,72, b, 0, 8);
	j.put("nonce" , Convert.byteArrayToHexString(b));
// User-Agent (Variable Länge) wird geparst: belibige Zeichenfolge die als Beschreibung des Nodes zu verstehen ist. (Zeichenfolge kann leer sein
	b = new byte[data.length];												// Da die Länge der Zeichenfolge vorher nicht bekannt ist, wird das gesammte Array geparst.
	System.arraycopy(data,80, b, 0, data.length-80);
	b = Calc.parseCompactSize(b, 0);
	j.put("User-Agent" , new String(b));
// last Block (4Byte) Der letzte vom Knoten empfangene Block
	b = new byte[4];
	System.arraycopy(data,data.length-5, b, 0, 4);
	Convert.swapBytes(b);
	j.put("last-Block" , Convert.byteArray_to_int(b));
// relay (1Byte)  (Boolischer Wert) Ob die Gegenstelle vermittelte Transaktionen ankündigen soll oder nicht, siehe BIP 0037 , seit Version> = 70001
	boolean y = data[data.length-1] != 0;
	j.put("relay" ,y);
}






// Nachricht "addr":  Liste mit IP-Adressen von Peers
// Nachricht mit variabler länge (die Länge wird hier aber nicht in Byte dargestellt sondern in Zeilen! Jede Zeile enthält genau 30Byte! Die maximale Länge sind 1000 Zeilen!
// Jede Zeile (30Byte) besteht aus (dem Zeitstempel(4-Byte  gefolgt von einer 8Byte(unbekannt) gefolgt von der IP-Adresse (18Byte)
// Der Zeitstempel wird entfernt, da er im Programm nicht gebraucht wird und Resourcen verbraucht !!!
// Beispiel: 	fd e803  // Indifikator Variabler Länge (hier sind es "03e8" = 1000  Nachfolgende "Zeilen" die 30Byte Lang sind (nicht Bytes !!!)
// 4c72885a 0d0000000000000000000000000000000000ffff579d3154 479d // ersten 4Bytes sind der Zeitstempel, gefolgt von 26Bytes IP-Adresse
private static void parse_addr(byte[] data, JSONObject j)  throws JSONException, ConnectException
{
	int[] sizeData = Calc.decodeCompactSize(data, 0);											// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0];																	// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];																		// die Anzahl an Zeilen	(30Bytes lang)
	if((data[0]==(byte)0xFF) || (data[0]==(byte)0xFE) || len>1000) {throw new ConnectException( "\nFehler in BTC_Network_MessageList.parse_addr, maximale Länge der IP-Adress-Liste von 1000 überschritten!  Länge ist: "+len);}
	JSONArray ja = new JSONArray();																// Ein JSON-Array wird erzeugt
	for(int i =start; i<(len*30); i=i+30)														// 30Byte Zeilen werden durchlaufen
	{
		byte[] b = new byte[18];																// 18 Byte der IP Adresse
		System.arraycopy(data,i+12, b, 0, 18);
		ja.put(ConvertIP.ByteArrayToString(b));
	}
	j.put("IP-Adressen", ja);
}







// Nachricht "inv":  Liste mit Hash´s über die der Peer informieren möchte
// Nachricht mit variabler länge: die Länge enthält die Anzahl von 36Byte langen Zeilen
// Jede Zeile (36Byte) besteht aus dem "Hash-Code" (4Byte) gefolgt von einem 32Byte langem Hash
// Rückgabe ist ein JSON-Object: = "hash-Code":"HASH"
// Beispiel inf-Nachricht: 01 01000000 8755813f152b676ab768f6ddae046d0911e326617a43cc8d98eafd9fb78b6e0a
private static void parse_inv(byte[] data, JSONObject j) throws JSONException, ConnectException
{
	int[] sizeData = Calc.decodeCompactSize(data, 0);												// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0];																		// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];																			// Die Anzahl der Zeilen (36Bytes lang)
	if((data[0]==(byte)0xFF) || (data[0]==(byte)0xFE) || len>50000) {throw new ConnectException("\nFehler in BTCMessageParserJSON.parse_inv, maximale Länge der inv-Daten von 50000 überschritten!");}
	JSONArray tx 		= new JSONArray();															// Ein JSON-Array mit den Tx-hash-codes
	JSONArray block 	= new JSONArray();															// Ein JSON-Array mit den block-hash-codes
	JSONArray filter 	= new JSONArray();															// Ein JSON-Array mit den filterred Block-hash-codes
	JSONArray cmpctBlock= new JSONArray();															// Ein JSON-Array mit den CMPCT Block-hash-codes
	for(int i = start; i<(len*36); i=i+36)															// 36Byte Zeilen werden durchlaufen
	{
		byte[] b = new byte[4];																		// Hash-Code (4Byte)
		System.arraycopy(data,i, b, 0, 4);															// Hash-Code (4Byte) wird in "b" kopiert
		byte[] h = new byte[32];																	// Der eigentliche SHA-256 Hash (32Byte)
		System.arraycopy(data,i+4, h, 0, 32);														// Der eigentliche SHA-256 Hash (32Byte) wird kopiert
		Convert.swapBytes(h);																		// "DER" codierung
		switch (b[0])																				// in b[0] befindet sich nun die Zahl, die den hash-Code Name enthält.
		{
			case 0:  throw new ConnectException("Es wurde in einer inv-Nachricht ein hash-code Typ 0x00000000 empfangen, denn es nicht gibt!");		// Einen Hash-Typ "0" gibt es nicht.
			case 1:  tx.put(Convert.byteArrayToHexString(h));			break;						// Wenn es sich um einen Transaktionshash-Code handelt.
			case 2:  block.put(Convert.byteArrayToHexString(h));		break;						// Wenn es sich um einen Blockhash-Code haldelt.
			case 3:  filter.put(Convert.byteArrayToHexString(h));		break;						// Hash eines Blockheaders; identisch mit MSG_BLOCK. Nur zur Verwendung in der getdata-Nachricht. Bip 152
			case 4:  cmpctBlock.put(Convert.byteArrayToHexString(h));	break;						// Hash eines Blockheaders; identisch mit MSG_BLOCK. Nur zur Verwendung in der getdata-Nachricht. Bip 152.
			default: throw new ConnectException("Unbekannter hash-code-Typ "+Convert.byteArrayToHexString(b)+" in inf-Nachricht.");
		}																							// Weitere hash-Codes sind unbekannt, und können hier erweitert werden.
	}
	// Es werden nur JSON-Arrays in das JSON-Object eingefügt, die auch min. ein Element enthalten.
	if(tx.length() >0) 			j.put("tx-hash", tx);
	if(block.length() >0) 		j.put("block-hash", block);
	if(filter.length() >0) 		j.put("filtered_block-hash", filter);
	if(cmpctBlock.length() >0) 	j.put("CMPCT_block-hash", cmpctBlock);
}






// getdata wird als Antwort auf inv verwendet, um den Inhalt eines bestimmten Objekts abzurufen, und wird normalerweise nach dem Empfang eines inv- Pakets gesendet, nachdem bekannte Elemente gefiltert wurden.
// Sie kann verwendet werden, um Transaktionen abzufragen, aber nur wenn sie sich im Speicherpool oder Relay-Set befinden
// Nachricht mit variabler länge: die Länge enthält die Anzahl von 36Byte langen Zeilen. Maximal 50000 Zeilen erlaubt!
// Jede Zeile (36Byte) besteht aus dem "Hash-Code-Typ" (4Byte) gefolgt von einem 32Byte langem Hash
// Beispiel getdata-Nachricht: 01 01000000 8755813f152b676ab768f6ddae046d0911e326617a43cc8d98eafd9fb78b6e0a
// Achtung! Änderung in Hashcode-Typ! Siehe bip-0144, dies steht nicht in der Protocolldukumentation! Es wurde dort ein zusätzliches Witness-Flag hingelegt. Hash-Code-Typ sieht dann z.B. so aus: 01000040
private static void parse_getdata(byte[] data, JSONObject j) throws JSONException, ConnectException
{
	int[] sizeData = Calc.decodeCompactSize(data, 0);											// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0];																	// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];																		// Die Anzahl der Zeilen (36Bytes lang)
	if((data[0]==(byte)0xFF) || (data[0]==(byte)0xFE) || len>50000) {throw new ConnectException("\nFehler in BTCMessageParserJSON.parse_getdata, maximale Länge der inv-Daten von 50000 überschritten!");}
	JSONArray haupt 	= new JSONArray();														// Das Haupt-Array was alle Einträge enthalt
	String hashTyp;																				// Es gibt derzeit 4 verschiedene Hash-Typen in der Dokumentation: (tx-hash, block-hash, Filtered_Block-hash, MSG_CMPCT_Block-hash)
	boolean witness;																			// Das zuätzliche Witness-Flag nach: Bip-0144
	for(int i = start; i<(len*36); i=i+36)														// 36Byte Zeilen werden durchlaufen
	{
		byte[] b = new byte[4];																	// Hash-Code (4Byte)
		System.arraycopy(data,i, b, 0, 4);														// Hash-Code (4Byte) wird in "b" kopiert
		byte[] h = new byte[32];																// Der eigentliche SHA-256 Hash (32Byte)
		System.arraycopy(data,i+4, h, 0, 32);													// Der eigentliche SHA-256 Hash (32Byte) wird kopiert
		Convert.swapBytes(h);																	// "DER" codierung
		JSONArray zeile 	= new JSONArray();													// Das Zeilen-Array welches folgene Einträge hat: hashCode-Typ, WitnessFlag, hashCode
		switch(b[0])																			// Der Hash-Type wird aus diesem Byte geparst.
		{
			case 0:	hashTyp = "Fehler! Hash-Type 00 gibt es nicht!";		throw new ConnectException("Es wurde der Hash-Typ 00 in der GETDATA-Nachricht empfangen, den es aber nicht gibt!");
			case 1:	hashTyp = "tx-Hash";										break;
			case 2:	hashTyp = "Block-Hash";										break;
			case 3:	hashTyp = "Filtered_Block-Hash";							break;
			case 4:	hashTyp = "CMPCT_Block-Hash";								break;
			default:hashTyp = "Unbekannter Hash-Typ in GETDATE-Nachricxht!";  	break;
		}
		if(b[3]==64) witness = true; else witness = false;										// Das Witness Flag (Bip-0144) wird hier geparst
		zeile.put(hashTyp); zeile.put(witness); zeile.put(Convert.byteArrayToHexString(h));		// Diese 3 Einträge werden in das Zeilen-Array geschrieben.
		haupt.put(zeile);
	}
	j.put("Hash-Typ, Witness, Hash", haupt);
}





// Liefert ein Header- Paket, das die Header von Blöcken enthält, die direkt nach dem letzten bekannten Hash im Block-Locator-Objekt beginnen, bis zu hash_stop oder 2000 Blöcken, je nachdem, was zuerst eintritt.
// Um die nächsten Block-Header zu erhalten, muss man die getheaders erneut mit einem neuen Block-Locator-Objekt ausgeben.
// Der Befehl getheaders wird von Thin Clients verwendet, um schnell die Blockkette herunterzuladen, bei der die Inhalte der Transaktionen irrelevant sind (weil sie nicht unsere sind).
private static void parse_getheaders(byte[] data, JSONObject j) throws JSONException, ConnectException
{
	byte[] b = new byte[4];																	// Protokoll-Version (4Byte)
	System.arraycopy(data,0, b, 0, 4);														// Protokoll-Version (4Byte)  wird kopiert
	Convert.swapBytes(b);
	j.put("Block-Version", Integer.toUnsignedString(Convert.byteArray_to_int(b)));			// die Protokoll-Version wird nach int konvertiert und in das JSON gelegt
	b = new byte[5];																		// (5Byte)  zur Längenberechnung der Folgedaten (compact size) wird bereitgestellt
	System.arraycopy(data,4, b, 0, 5);														// (5Byte)  zur Längenberechnung der Folgedaten (compact size) wird kopiert
	int[] sizeData = Calc.decodeCompactSize(b,0);											// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0] + 4;															// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen (um 4Bytes nach hinten verschoben, da voher ja noch die Protokoll-Version ist.
	int len = sizeData[1]+1;																// Die Anzahl der Zeilen (32Bytes lang) + Eine mehr für: "Hash des letzten gewünschten Blockheaders; setze auf Null, um so viele Blöcke wie möglich zu bekommen (2000)"
	if(len>2000) throw new ConnectException("Fehler in BTCMessageParserJSON.parse_getheaders, maximale Länge der GETHEADER-Zeilen von 2000 überschritten!");
	JSONArray ja = new JSONArray();															// Neues JSON Array wird angelegt
	for(int i = start; i<(len*32); i=i+32)													// 32Byte Zeilen werden durchlaufen
	{
		b = new byte[32];																	// SHA-256 (32Byte)
		System.arraycopy(data,i, b, 0, 32);
		Convert.swapBytes(b);
		ja.put(Convert.byteArrayToHexString(b));											// Die Header-Hashes werden in das Array gelegt
	}
	j.put("Block Locator Hash", ja);
}








// Das Header- Paket gibt Block-Header als Antwort auf ein Getheaders- Paket zurück.
// Jede Zeile enthält 81 Bytes mit den Einzelheiten des Headers
// - Version (4Byte)ist eine Block-Version die bisher immer 1 war.
// - Block-Hash (32Byte) ist der Hash des gesamten Blockes
// - merkle_root (32Byte) ist der Transaktionshash der ersten Coinbase Transaktion in dem Block, die auch die Belohnung auszahlt.
// - timestamp (4Byte) ist ein kurzer Zeitstämpel des Blockes. Der nicht immer richtig sein muss. (Regionalzeit usw.)
// - bits ist (4byte) ist der Schwierigskeitsgrad
// - noce (4byte), txCoint (1byte) ist hier immer null.
private static void parse_headers(byte[] data, JSONObject j) throws JSONException, ConnectException
{
		int[] sizeData = Calc.decodeCompactSize(data, 0);										// Die Werte für "start" und "len" werden hier gesetzt
		int start = sizeData[0];																// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
		int len = sizeData[1];																	// Die Anzahl der Zeilen (81Bytes lang)
		if((data[0]==(byte)0xFF) || (data[0]==(byte)0xFE) || len>2000) throw new ConnectException("Maximale Länge der HEADER-Zeilen von 2000 überschritten!");
		JSONArray ja = new JSONArray();															// Neues JSON Array wird angelegt
		for(int i = start; i<(len*81); i=i+81)													// 81Byte Zeilen werden durchlaufen
		{
			byte[] b = new byte[81];
			System.arraycopy(data,i, b, 0, 81);
			JSONObject jo = new JSONObject();													// ein neues JSON-Object für jede einzelne Zeile

			byte[] version = new byte[4];														// 4Byte Version
			System.arraycopy(b, 0, version, 0, 4);
			Convert.swapBytes(version);
			jo.put("version", Convert.byteArray_to_int(version));								// Header-Version wird geparst

			byte[] blockHash = new byte[32];
			System.arraycopy(b, 4, blockHash, 0, 32);
			Convert.swapBytes(blockHash);
			jo.put("Block Hash", Convert.byteArrayToHexString(blockHash));						// Block-Hash wird geparst

			byte[] merkleRoot = new byte[32];
			System.arraycopy(b, 36, merkleRoot, 0, 32);
			Convert.swapBytes(merkleRoot);
			jo.put("merkle_root", Convert.byteArrayToHexString(merkleRoot));					// Merkle_Root wird geparst

			byte[] timeStamp = new byte[4];
			System.arraycopy(b, 68, timeStamp, 0, 4);
			Convert.swapBytes(timeStamp);
			jo.put("timestamp", new Timestamp(Convert.byteArray_to_long(timeStamp)*1000L));		// Time-Stamp wird geparst

			byte[] bits = new byte[4];
			System.arraycopy(b, 72, bits, 0, 4);
			Convert.swapBytes(bits);
			jo.put("difficulty", Convert.byteArrayToHexString(bits));							// Der Schwierigskeitsgrad wird geparst

			byte[] nonce = new byte[4];
			System.arraycopy(b, 76, nonce, 0, 4);
			Convert.swapBytes(nonce);
			jo.put("nonce", Convert.byteArrayToHexString(nonce));								// Die Nonce

			byte[] txnCount = new byte[1];
			System.arraycopy(b, 80, txnCount, 0, 1);
			jo.put("txn_count", Convert.byteArrayToHexString(txnCount));						// Anzahl der Transaktionseinträge, dieser Wert ist immer 0

			ja.put(jo);																			// Das Zeilen JSON-Object wird dem Haupt-Array übergeben.
		}
		j.put("Block headers", ja);
		j.put("Anzahl", len);
}






// Tranasaktions Nachricht mit der raw-Transaktion
private static void parse_tx(byte[] data, JSONObject j) throws JSONException
{
	j.put("tx-raw", Convert.byteArrayToHexString(data));
}






// Die Blocknachricht wird als Antwort auf eine getdata-Nachricht gesendet, die Transaktionsinformationen von einem Blockhash anfordert.
// Einzelheiten https://en.bitcoin.it/wiki/Protocol_documentation#block
private static void parse_block(byte[] data, JSONObject j) throws JSONException
{
		byte[] version = new byte[4];
		System.arraycopy(data, 0, version, 0, 4);
		//Convert.swapBytes(version);															// Vermutlich hier nicht getreht (Konnte es nicht rausfinden)
		j.put("version", Convert.byteArray_to_int(version));									// Header-Version wird geparst

		byte[] prevBlock = new byte[32];
		System.arraycopy(data, 4, prevBlock, 0, 32);
		Convert.swapBytes(prevBlock);
		j.put("prev_block hash", Convert.byteArrayToHexString(prevBlock));						// Block-Hash des Vorherigen Blockes

		byte[] merkleRoot = new byte[32];
		System.arraycopy(data, 36, merkleRoot, 0, 32);
		Convert.swapBytes(merkleRoot);
		j.put("merkle_root", Convert.byteArrayToHexString(merkleRoot));							// Merkle_Root wird geparst

		byte[] timeStamp = new byte[4];
		System.arraycopy(data, 68, timeStamp, 0, 4);
		Convert.swapBytes(timeStamp);
		j.put("timestamp", new Timestamp(Convert.byteArray_to_long(timeStamp)*1000L));			// Time-Stamp wird geparst

		byte[] bits = new byte[4];
		System.arraycopy(data, 72, bits, 0, 4);
		Convert.swapBytes(bits);
		j.put("difficulty", Convert.byteArrayToHexString(bits));								// Der Schwierigskeitsgrad wird geparst

		byte[] nonce = new byte[4];
		System.arraycopy(data, 76, nonce, 0, 4);
		Convert.swapBytes(nonce);
		j.put("nonce", Convert.byteArrayToHexString(nonce));									// Die Nonce

		int[] sizeData = Calc.decodeCompactSize(data, 80);										// Decodiert die Transaktions Anzahl
		int txStart = sizeData[0];																// Position des Data-Byte-Array´s der Tx-Daten
		int txCount = sizeData[1];																// Anzahl der Transaktionen in diesem Block
		j.put("txn_count", txCount);															// Anzahl der Transaktion in diesem Block

		byte[][] tx = txDeserialize(data, txStart, txCount);									// Alle Transaktionen werden aus dem Block geparst.
		JSONArray ja = new JSONArray();															// Das JSONArray welches die Transaktionen im Block enthält
		for (byte[] element : tx)
			ja.put(Convert.byteArrayToHexString(element));				// Die Transaktionen werdne den JSON-Array zugefügt.
		j.put("tx", ja);
}







//	Damit werden inf-Nachrichten über neue Blöcke angefordert. Wird benutzt um die Block-Hash´s der Blockchain in vorwärtsrichtung anzufordern.
//	Damit im zweiten Schritt die Blöcke in richtiger Reihenfolge runtergeladen werden können.
private static void parse_getblocks(byte[] data, JSONObject j) throws JSONException, ConnectException
{
		byte[] b = new byte[4];																	// Block-Version (4Byte)
		System.arraycopy(data,0, b, 0, 4);														// Block-Version (4Byte)  wird kopiert
		Convert.swapBytes(b);
		j.put("Block-Version", Integer.toUnsignedString(Convert.byteArray_to_int(b)));			// die Block-Version wird nach int konvertiert und in das JSON gelegt
		b = new byte[5];																		// (5Byte)  zur Längenberechnung der Folgedaten (compact size) wird bereitgestellt
		System.arraycopy(data,4, b, 0, 5);														// (5Byte)  zur Längenberechnung der Folgedaten (compact size) wird kopiert
		int[] sizeData = Calc.decodeCompactSize(b,0);											// Die Werte für "start" und "len" werden hier gesetzt
		int start = sizeData[0] + 4;															// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen (um 4Bytes nach hinten verschoben, da voher ja noch die Protokoll-Version ist.
		int len = sizeData[1]+1;																// Die Anzahl der Zeilen 32Bytes + 1 mehr für: "Hash des letzten gewünschten Blockheaders; setze auf Null, um so viele Blöcke wie möglich zu bekommen (500)"
		if(len>500) throw new ConnectException("Fehler in BTCMessageParserJSON.parse_getblocks, maximale Länge der GETBLOCKS-Zeilen von 500 überschritten!");
		JSONArray ja = new JSONArray();															// Neues JSON Array wird angelegt
		for(int i = start; i<(len*32); i=i+32)													// 32Byte Zeilen werden durchlaufen
		{
			b = new byte[32];																	// SHA-256 (32Byte)
			System.arraycopy(data,i, b, 0, 32);
			Convert.swapBytes(b);
			ja.put(Convert.byteArrayToHexString(b));											// Die Header-Hashes werden in das Array gelegt
		}
		j.put("Block-Hash", ja);
}






//	(8 Byte Nutzdaten) der festgelegten minimalen Überweisungsgebür in Satoshi
//	Beispiel "1000" = 0.00001 BTC Gebühr.
private static void parse_feefilter(byte[] data, JSONObject j) throws JSONException
{
	byte[] b = new byte[8];
	System.arraycopy(data,0, b, 0, 8);
	Convert.swapBytes(b);
	Long value = Convert.byteArray_to_long(b);
	double d = (((double)value)/100000000);
	DecimalFormat df = new DecimalFormat("0");
	df.setMaximumFractionDigits(340);
	j.put("Min-Feerate", df.format(d));
}






// notfound ist eine Antwort auf eine getdata, die gesendet wird, wenn angeforderte Datenelemente nicht weitergeleitet werden konnten,
// z. B. weil sich die angeforderte Transaktion nicht im Speicherpool oder Relay-Set befand.
// Es wird hier nicht unterschieden ob es sich um einen TX-Hash oder Block-Hash handelt.  (Vereinfachung, da man das eh am Hash erkennen kann.)
private static void parse_notfound(byte[] data, JSONObject j) throws JSONException, ConnectException
{
	int[] sizeData = Calc.decodeCompactSize(data,0);										// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0];																// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];																	// Die Anzahl der Zeilen (36Bytes lang)
	if(len>500) throw new ConnectException("Fehler in BTCMessageParserJSON.parse_notfound, maximale Anzahl der Zeilen von 500 überschritten!");
	JSONArray ja = new JSONArray();															// Neues JSON Array wird angelegt
	for(int i = start; i<(len*36); i=i+36)													// 36 Byte Zeilen werden durchlaufen
	{
		byte[] out = new byte[32];															// Block-Hash, oder Tx-Hash
		System.arraycopy(data,i+4, out, 0, 32);
		Convert.swapBytes(out);
		ja.put(Convert.byteArrayToHexString(out));											// Die Header-Hashes werden in das Array gelegt
	}
	j.put("hash", ja);
}






// Die Ablehnungsnachricht wird gesendet, wenn Nachrichten abgewiesen werden.
private static void parse_reject(byte[] data, JSONObject j) throws JSONException
{
	int dataSize = data.length;																// Gesamtlänge der Nutzdaten wird zwischengespeichert
	int[] sizeData = Calc.decodeCompactSize(data,0);										// Die Werte für "start" und "len" werden hier gesetzt
	int pos = sizeData[0];																	// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];																	// Byte-Länge von "Typ of Message"
	byte[] typ = new byte[len];																// Typ of Message
	System.arraycopy(data,pos, typ, 0, len);
	j.put("Typ",new String(typ));															// Typ of Message wird in JSON gelegt
	pos = pos + len;																		// "pos" wird zum nächsten Feld geschoben (ccode)
	byte ccode = data[pos];																	// "ccode" Code, der sich auf die abgelehnte Nachricht bezieht
	switch (ccode)
	{
		case 0x01: 	j.put("cCode", "MALFORMED");		break;
		case 0x10: 	j.put("cCode", "INVALID");			break;
		case 0x11: 	j.put("cCode", "OBSOLETE");			break;
		case 0x12: 	j.put("cCode", "DUPLICATE");		break;
		case 0x40: 	j.put("cCode", "NONSTANDARD");		break;
		case 0x41: 	j.put("cCode", "DUST");				break;
		case 0x42: 	j.put("cCode", "INSUFFICIENTFEE");	break;
		case 0x43: 	j.put("cCode", "CHECKPOINT");		break;
		default:	j.put("cCode", "Unbekannt");		break;
	}
	pos = pos +1;																			// "pos" wird zum nächsten Felöd geschoben (Textversion des Ablehnungsgrundes )
	sizeData = Calc.decodeCompactSize(data,pos);											// Die Werte für "start" und "len" werden hier gesetzt
	pos = sizeData[0];																		// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	len = sizeData[1];																		// Byte-Länge von "Text-Version"
	byte[] text = new byte[len];															// Text of Message
	System.arraycopy(data,pos, text, 0, len);
	j.put("Text",new String(text));															// Text of Message wird in JSON gelegt
	pos = pos + len;																		// "pos" wird zum nächsten Feld geschoben (Optional-Data)
	if(dataSize > pos)																		// Optional, wenn noch zusätzliche Felder gesendet werden.
	{
		len = dataSize - pos;																// Die Optionalen Daten werden ohne Längenangabe bis zum Ende des Datensatzes gesendet.
		byte[] optional = new byte[len];
		System.arraycopy(data,pos, optional, 0, len);
		j.put("optional-Data",Convert.byteArrayToHexString(optional));
	}
	j.put("Hex", Convert.byteArrayToHexString(data));										// zur Sicherheit wird hier noch mal der Hex-String übergeben.
}





// Die sendcmpct-Nachricht wird als Nachricht definiert, die eine 1-Byte-Ganzzahl gefolgt von einer 8-Byte-Ganzzahl enthält... Siehe Bip 152
private static void parse_sendcmpct(byte[] data, JSONObject j) throws JSONException
{
	j.put("first", data[0]);									// Muss 1 oder Null sein
	byte[] version = new byte[8];
	System.arraycopy(data, 1, version, 0, 8);
	Convert.swapBytes(version);
	j.put("version", Convert.byteArray_to_long(version));
	j.put("hex-raw", Convert.byteArrayToHexString(data));
}





// Die Ping- Nachricht wird hauptsächlich gesendet, um zu bestätigen, dass die TCP / IP-Verbindung noch gültig ist.
private static void parse_ping(byte[] data, JSONObject j) throws JSONException
{
	byte[] nonce = new byte[8];
	System.arraycopy(data, 0, nonce, 0, 8);
	j.put("nonce", Convert.byteArrayToHexString(nonce));
}















// ----------------------------------------------------------------------------- Hilfsmethoden ------------------------------------------------------------------


/** Parst das Services Bitfeld aus der Versions-Nachricht.
	@param services 8Bytes-Array aus der Versions-Nachricht, wenn mehr als 8Bytes übergeben werden, werden nur die ersten 8Bytes benutzt.
	@return JSON-Array welches die Verwendeten Dienste in Textform enthält.  **/
public static JSONArray parse_Services(byte[] services)
{
	JSONArray ja = new JSONArray();
	long ser = Convert.byteArray_to_long(services);
	long mask = 1;
	if((ser&mask<<0) != 0)	ja.put("NETWORK");
	if((ser&mask<<1) != 0)	ja.put("GETUTXO");
	if((ser&mask<<2) != 0)	ja.put("BLOOM");
	if((ser&mask<<3) != 0)	ja.put("WITNESS");
	if((ser&mask<<4) != 0)	ja.put("unknown");
	if((ser&mask<<5) != 0)	ja.put("unknown");
	if((ser&mask<<6) != 0)	ja.put("unknown");
	if((ser&mask<<7) != 0)	ja.put("unknown");
	if((ser&mask<<8) != 0)	ja.put("unknown");
	if((ser&mask<<9) != 0)	ja.put("unknown");
	if((ser&mask<<10)!= 0)	ja.put("NETWORK_LIMITED");
	mask = mask<<10;
	for(int i=11; i<64;i++)
	{
		mask = mask<<1;
		if((ser&mask) != 0)	ja.put("unknown");
	}
	return ja;
}




// Deserialisiert aus einem zusammenhängendem Byte-Array mehrere raw-Transaktionen, so wie sie in dem Blöcken hintereinander gespeichert sind.
// Übergeben wird das Byte-Array mit den Transaktionen und ein Integer "pos" welcher ein Zeiger ist, der auf den Startpunkt der ersten Tx zeigt.
// Zurück gegeben wird ein 2dim Byte-Array mit den einzelnen Raw-Transaktionen.
// txCount ist die Anzahl der Transaktionen in dem Block
private static byte[][] txDeserialize(byte[] data, int pos, int txCount) throws JSONException
{
	byte[][] out = new byte[txCount][];
	for(int itx=0; itx<txCount; itx++ )
	{
		int posTxStart = pos;																				// Startposition der Tx wird gespeichert
		boolean witness;																					// Wenn true, dann ist Witness enthalten.
		if(data[pos+4]==0 && data[pos+5]==1) witness = true; else witness = false;							// Abfrage ob Tx Witness enthält
		if(witness) pos = pos+6; else pos=pos+4;															// pos wird auf TxIn Count geschoben.
		int[] comSize = Calc.decodeCompactSize(data, pos);													// Compact-Size der TxIn wird decodiert.
		pos = comSize[0];																					// pos wird verschoben
		int txInCount = comSize[1];																			// Die Anzahl der Tx-Eingänge
		for(int i=0; i<txInCount; i++) 																		// Schleife mit den Transaktionseingängen
		{
			pos = pos+36; 																					// pos wird bis zur Script-Länge geschoben
			int[] sigSize = Calc.decodeCompactSize(data, pos);												// Compact-Size der SigScript wird decodiert.
			pos = sigSize[0];																				// position SigScript
			int siglen = sigSize[1];																		// Länge SigScript
			pos = pos+siglen+4;																				// pos wird bis zum Ende der TxIn geschoben
		}
		int[] comSizeOUT = Calc.decodeCompactSize(data, pos);												// Compact-Size der TxOUT wird decodiert.
		pos = comSizeOUT[0];																				// pos wird verschoben
		int txOutCount = comSizeOUT[1];																		// Die Anzahl der Tx-Ausgänge
		for(int i=0; i<txOutCount; i++) 																	// Schleife mit den Transaktionsausgängen
		{
			pos = pos+8; 																					// pos wird bis zur Script OUT geschoben
			int[] scriptOUT = Calc.decodeCompactSize(data, pos);											// Compact-Size des ScriptOUT wird decodiert.
			pos = scriptOUT[0];																				// position ScriptOUT
			int outlen = scriptOUT[1];																		// Länge ScriptOUT
			pos = pos+outlen;																				// pos wird bis zum Ende des ScriptOUT geschoben
		}
		if(witness)
		{
			Witness w =  new Witness(data, pos,txInCount );													// Witness wird geparst
			int witLen = w.getLength();																		// Die Byte-Länge der Witness-Daten
			pos = pos + witLen;																				// pos wird bis zum Ende der Witness-Daten geschoben
		}
		pos = pos + 4;																						// pos wird an´s Ende der Transaktion geschoben.
		int txLen = pos - posTxStart;																		// Die Länge der Tx kann nun berechnet werden.
		out[itx] = new byte[txLen];																			// Byte-Array der einzelnen Tx wird initialisiert
		System.arraycopy(data, posTxStart, out[itx], 0, txLen);												// Ausgangs-Array wird erstellt
	}
	return out;
}










// ------------------------------------------------------------- Test Methoden zum Testen und erweitern.  ---------------------------------



/** Condiert neue Befehlssätze
	@param befehl Der Befehl in Textform, kleingeschrieben.
	@return 12Byte Befehlscode als Hax-String  **/
public static String encodeBefehsSatzToHex(String befehl)
{
	char[] ch = befehl.toCharArray();
	StringBuilder builder = new StringBuilder();
	for (char c : ch)
	{
		int i = c;
		builder.append(Integer.toHexString(i).toLowerCase());
	}
	String out = builder.toString() + "000000000000000000000000";
	return out.substring(0,24);
}



/** Condiert neue Befehlssätze
@param befehl Der Befehl in Textform, kleingeschrieben.
@return Befehlscode in Long **/
public static long encodeBefehsSatzToLong(String befehl)
{
	byte[] b = Convert.hexStringToByteArray(encodeBefehsSatzToHex(befehl));
	return Convert.byteArray_to_long(b);
}
}