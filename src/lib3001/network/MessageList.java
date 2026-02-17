package lib3001.network;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import lib3001.crypt.Calc;
import lib3001.crypt.Convert;





	/************************************************************************************************************
	 *		V2.3   vom 07.01.2021         																		*
	 * 		LIB3001 Bibliotheks Klasse																			*
	 *		Nachrichten Liste die ins Bitcoin P2P Netzwerk gesendet werden können.								*
	 *		Die Nachrichten werden in die passende Formt konvertiert, so das sie direkt gesendet werden können.	*
	 *		Hier nur zu SENDENDE Nachrichten!																	*
	 *		Der "magic" String ist die Info in welchem Netzwerk (TestNet etc) man sich befindet					*
	 *																											*
	 *		Die Klasse ist noch nicht fertig, es fehlen immer noch Nachrichten.									*
	 ************************************************************************************************************/






public class MessageList
{





//	Erstellt ein default Versions-Header im JSON-Format der z.B. zum Testen verwendet werden kann.
//	Dieser Versions-Header wird automatisch verwendet, wenn kein anderer Versions-Header übergeben wird.
private static JSONObject getDefaultVersionsHeader() throws JSONException
{
	JSONObject jo = new JSONObject();
	jo.put("version",	"70013");									// Version      (4Byte)	Die BitcoinCore-Version die die Gegenstelle benutzt
	jo.put("services", 	"0000000000000009");						// Services 	(8Byte) Bitfeld der Features, die für diese Verbindung aktiviert werden sollen
	jo.put("ip_peer", 	"0.0.0.0:8333");							// IP-Adresse der Gegenstelle
	jo.put("ip_my", 	"0.0.0.0:8333");							// Eigene IP-Adresse
	jo.put("u-agent",	"/Satoshi:0.15.1/");						// Der User-Agent ist die selbst definierte Beschreibung des Clienten
	jo.put("last-block","514151");									// Der letzte vom Knoten empfangene Block
	jo.put("relay",		"true");									// Ob die Gegenstelle vermittelte Transaktionen ankündigen soll oder nicht, siehe BIP 0037 , seit Version> = 70001
	return jo;
}








/** Erstellt Versions-Nachricht zum Handshake
	@param magic TestNet oder MainNet als Byte-Array
	@param versionsHeader Ein Versions-Header im JSON-Format der in der Klasse MessageList beschrieben ist. Es kann "NULL" übergeben werden, wenn ein default-Header verwendet werden soll.
	@return Endgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] version(byte[] magic, JSONObject versionsHeader) throws Exception
{
	if(versionsHeader==null) versionsHeader = getDefaultVersionsHeader();		// Wenn kein VersionsHeader übergeben wurde, wird der default VersionsHeader ausgewählt.
// ------------- Versions-Header -----------------------//
	String befehl 	= "version";														// Befehls-Code (CHAR) 	Eingabe ohne Leerzeichen, diese werden später aufgefüllt)
	int version 	= versionsHeader.getInt("version");									// Version      (4Byte)	Die BitcoinCore-Version die die Gegenstelle benutzt
	String services	= versionsHeader.getString("services");								// Services 	(8Byte) Bitfeld der Features, die für diese Verbindung aktiviert werden sollen
	long time		= System.currentTimeMillis() / 1000L;								// timestamp 	(8Byte) Standard-UNIX-Zeitstempel in Sekunden
	byte[] ipPeer	= ConvertIP.stringToByteArray(versionsHeader.getString("ip_peer"));	// IP-Adresse der Gegenstelle
	byte[] ipMy		= ConvertIP.stringToByteArray(versionsHeader.getString("ip_my"));	// Eigene IP-Adresse
	String U_Agent	= versionsHeader.getString("u-agent");								// Der User-Agent ist die selbst definierte Beschreibung des Clienten
	int l_Block		= versionsHeader.getInt("last-block");								// last Block Der letzte vom Knoten empfangene Block
	boolean relay	= versionsHeader.getBoolean("relay");								// Ob die Gegenstelle vermittelte Transaktionen ankündigen soll oder nicht, siehe BIP 0037 , seit Version> = 70001
// ------------- Ende Versions-Header --------------------------------------------------------------//

	ByteArrayOutputStream stream_1 = new ByteArrayOutputStream();	// Erster  Teil: magic + Befehls-Satz
	ByteArrayOutputStream stream_2 = new ByteArrayOutputStream();	// Zweiter Teil: Nutzdaten

	stream_1.write(magic);											// "Magic"  wird in den ersten Teil des Streams gespeichert
	stream_1.write(befehl.getBytes());								// "Befehl" wird in den ersten Teil des Streams gespeichert
	int l = (12 - befehl.length());									// fehlende Leerzeichen (Nullen) müssen aufgefüllt werden
	for(int i=0;i<l;i++) stream_1.write(0);							// fehlende Leerzeichen (Nullen) des Befehls-Codes werden aufgefüllt

	byte[] b_version = Convert.int_To_4_ByteArray(version);			// "Version"  wird zu Byte-Array Konvertiert
	Convert.swapBytes(b_version);									// "Version"  Byte-Swap
	stream_2.write(b_version);										// "Version"  wird in den zweiten Teil des Streams gespeichert

	byte[] b_service = Convert.hexStringToByteArray(services);
	Convert.swapBytes(b_service);
	stream_2.write(b_service);										// "Services"  wird in den zweiten Teil des Streams gespeichert

	byte[] b_time = Convert.long_To_8_ByteArray(time);
	Convert.swapBytes(b_time);
	stream_2.write(b_time);											// "TimeStamp" wird in den zweiten Teil des Streams gespeichert

	for(int i=0;i<8;i++) stream_2.write(0);							// 8 Nullen Leer-Feld
	stream_2.write(ipPeer);											// IP-Adresse des Absenders wird gespeichert
	for(int i=0;i<8;i++) stream_2.write(0);							// 8 Nullen Leer-Feld
	stream_2.write(ipMy);											// IP-Adresse des Absenders wird gespeichert

	SecureRandom sr = new SecureRandom();							// Zufallszahl wird vorbereitet
	byte[] rand = new byte[8];										// 8Byte für Zufallszahl
	sr.nextBytes(rand);												// Zufallszahl wird generiert
	stream_2.write(rand);											// Zufallszahl wird gespeichert

	l = U_Agent.length();											// Länge des "User-Agents" wird berechnet
	stream_2.write((byte)(l&0xff));									// Länge des "User-Agents" wird gespeichert
	stream_2.write(U_Agent.getBytes());								// Daten des "User-Agents" wird gespeichert

	byte[] b_last_Block = Convert.int_To_4_ByteArray(l_Block);		// Last-Block wird in Int Konvertier
	Convert.swapBytes(b_last_Block);								// Byte Swap Last_Block
	stream_2.write(b_last_Block);									// Last-Block wird gespeichert

	if(relay) 	stream_2.write(0x01);								// Relay wird gespeichert
	else		stream_2.write(0x00);

	byte[] s2 = stream_2.toByteArray();								// Stream_2 wird in Byte-Array konvertiert
	byte[] b_len = Convert.int_To_4_ByteArray(s2.length);			// Länge der Nutzdaten wird in Byte-Array konvertiert
	Convert.swapBytes(b_len);										// Swap Länge Nutzdaten
	stream_1.write(b_len);											// Die Länge der Nutzdaten wird an Stream_1 angehängt
	byte[] hash = Calc.getHashSHA256(Calc.getHashSHA256(s2));		// 2x SHA-256 wird von den Nutzdaten gebildet
	hash = Arrays.copyOfRange(hash, 0, 4);							// Hash wird auf 4 Bytes gekürzt
	stream_1.write(hash);											// Hash der Nutzdaten wird an Stream_1 angegängt
	stream_1.write(s2);												// Stream_1 und Stream_2 wird aneinander gehängt
	return stream_1.toByteArray();									// Stream wird in Byste-Array konvertiert und ausgegeben
}




/**	Feste Verack Nachricht	24Byte **/
public static byte[] verack(byte[] magic) throws Exception
{
	byte[] out = new byte[24];
	byte[] data = Convert.hexStringToByteArray("76657261636b000000000000000000005df6e0e2");
	System.arraycopy(magic, 0, out, 0, 4);
	System.arraycopy(data, 0, out, 4, 20);
	return out;
}





/**	Erstellt eine inv-Nachricht mit einem einzelnem Tx-Hash Inhalt.
	@param magic TestNet oder MainNet als Hex-String
	@param data 32Byte-Array mit dem Tx-Hash in DER-Codierung also in gedrehter Form.
	@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] inv(byte[] magic, byte[] data)
{
	byte[] out = new byte[61];
	System.arraycopy(magic, 0, out, 0, 4);												// Magic wird gespeichert
	out[4]  = 0x69;																		// Message OP-Code
	out[5]  = 0x6e;																		// Message OP-Code
	out[6]  = 0x76;																		// Message OP-Code
	out[16] = 0x25;																		// Länge Nutzdaten
	out[24] = 0x01;																		// inv-code
	out[25] = 0x01;																		// inv-code
	System.arraycopy(data, 0, out, 29, 32);												// Der Tx-Hash in das Out-Array kopiert.
	byte[] hash = Arrays.copyOfRange(out, 24, 61);										// Eine Zwichenvariable um den Hash zu bestimmen.
	System.arraycopy( Calc.getHashSHA256(Calc.getHashSHA256(hash)) ,0, out,20,4);
	return out;
}







/**	Erstellt "getaddr" Nachricht  (fordert eine Liste mit IP-Adressen an) **/
public static byte[] getaddr(byte[] magic) throws Exception
{
	byte[] out = new byte[24];
	System.arraycopy(magic, 0, out, 0, 4);
	byte[] b = {0x67, 0x65, 0x74, 0x61, 0x64, 0x64, 0x72, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x5d, (byte) 0xf6, (byte) 0xe0, (byte) 0xe2};
	System.arraycopy(b, 0, out, 4, 20);
	return out;
}










/**	Senden Sie ein Header- Paket zurück, das die Header der Blöcke enthält, die direkt nach dem letzten bekannten Hash im Block Locator-Objekt beginnen,
	bis zu hash_stop oder 2000 Blöcken, je nachdem, was zuerst eintritt. Um die nächsten Block-Header zu erhalten, müssen Sie erneut Getheaders mit einem neuen Block-Locator-Objekt ausgeben.
	Beachten Sie, dass einige Clients Header von Blöcken bereitstellen können, die ungültig sind, wenn das Blocklokatorobjekt einen Hash für den ungültigen Zweig enthält.
	@param magic TestNet oder MainNet als Hex-String
	@param data 2D-Array welches ein Array der 32byte-Blockhashes enthalt der Blockheader die angefordert werden sollen.
	Jede übergebene 32Byte-Zeile im data-Array muss GENAU 32-Bytes lang sein.
	Der Datenstz MUSS schon geswapt über geben werden (DER-codiert) so wie er im Netzwerk übertragen wird.
	Es können maximal 255 Zeilen übergeben werden!
	Die letzte Zeile wird hier selbst eingefügt, da sie immer 0x000... ist.
	@param version Protollverion  z.B. 70013
	@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] getheaders(String magic, int version, byte[][] data)
{
	byte[] ver = Convert.int_To_4_ByteArray_swap(version);
	if(data.length > 255)   throw new IllegalArgumentException("Maximale Anzahl von 255 Header überschritten!");        					// Maximal 255 Header-Zeilen erlaubt
	byte[] serial = new byte[data.length*32+37];																							// Hier wird das serialisierte Data-Array hineingelegt.
	System.arraycopy(ver, 0, serial, 0, 4);																									// Die Version wird kopiert
	serial[4] = (byte) (data.length&0xff);																									// Die Anzahl der Zeilen der Nutzdaten wird gespeichert.
	for(int i=0;i<data.length;i++)																											// Hier wird "data" in ein eindimensionales Array serialisiert.
	{
		if(data[i].length != 32) throw new IllegalArgumentException("Es wurde ein Hash übergeben der nicht genau 32Byte lang ist!");        // Nur genau 32Byte lange Eingaben erlaubt.
		System.arraycopy(data[i], 0, serial, i*32+5, 32);																					// Serialisierung
	}
	byte[] len = Convert.int_To_4_ByteArray(serial.length);																					// in "len" ist nun die Länge in Bytes der Nutzdaten.
	Convert.swapBytes(len);
	byte[] hash = Calc.getHashSHA256(Calc.getHashSHA256(serial));																			// Nutzdaten werden gehasht (SHA-256²)
	byte[] m_b = Convert.hexStringToByteArray(magic + "676574686561646572730000");															// magic und Befehls-Code der Nachricht
	byte[] out = new byte[24+serial.length];
	System.arraycopy(m_b, 0, out, 0, 16);																									// magic und Befehlscode wird in das Ausgangs-Array kopiert
	System.arraycopy(len, 0, out, 16, 4);																									// Länge wird in das Ausgangs-Array kopiert
	System.arraycopy(hash, 0, out, 20, 4);																									// Hash wird in das Ausgangs-Array kopiert
	System.arraycopy(serial, 0, out, 24, serial.length);																					// Hash wird in das Ausgangs-Array kopiert
	return out;
}








/** getdata wird als Antwort auf inv verwendet, um den Inhalt eines bestimmten Objekts abzurufen, und wird normalerweise nach dem Empfang eines inv- Pakets gesendet, nachdem bekannte Elemente gefiltert wurden.
	Sie kann verwendet werden, um Transaktionen abzufragen, aber nur wenn sie sich im Speicherpool oder Relay-Set befinden.
	Blöcke können im Gegensatz zu Transaktionen ohne voherige inv-Nachricht direkt abgegrufen werden.
	@param magic TestNet oder MainNet als Hex-String
	@param data  Jede Zeile in "data" (genau 36Byte") besteht aus dem "Hash-Code" (4Byte) gefolgt von einem 32Byte langem Hash. Der "Hash-Code" sagt aus um was für einen Hash es sich handelt.
	Der Datenstz MUSS schon geswapt über geben werden (DER-Codiert) so wie er im Netzwerk übertragen wird.
	Beispiel für eine Tx-Anforderung    (data): 01000000 8755813f152b676ab768f6ddae046d0911e326617a43cc8d98eafd9fb78b6e0a
	Beispiel für eine Block-Anforderung (data): 02000000 8e296732768dc985586da9dbde7c3118f5ff6ba4c4e98a25ba00000000000000
	Es können gleich mehrere (magx.255) Nachrichten in einem Block gesendet werden daher ein 2D-Array
	@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann.**/
public static byte[] getdata(String magic, byte[][] data)
{
	if(data.length > 255)   throw new IllegalArgumentException("Maximale Anzahl von 255 getdata-Zeilen überschritten!");        			// Maximal 255 getdata-Zeilen erlaubt
	byte[] payload = new byte[data.length*36+1];																							// Hier wird das serialisierte Data-Array hineingelegt.
	payload[0] = (byte) (data.length&0xff);																									// Die Anzahl der Zeilen der Nutzdaten wird in einem Byte gespeichert gespeichert. (Daher auch max. 255)
	for(int i=0;i<data.length;i++)																											// Hier wird "data" in ein eindimensionales Array serialisiert.
	{
		if(data[i].length != 36) throw new IllegalArgumentException("Es wurde ein data-Array übergeben das nicht genau 36Byte lang ist!");  // Nur genau 36Byte lange Eingaben erlaubt.
		System.arraycopy(data[i], 0, payload, i*36+1, 36);																					// Serialisierung
	}
	byte[] len = Convert.int_To_4_ByteArray(payload.length);																				// in "len" ist nun die Länge in Bytes der Nutzdaten.
	Convert.swapBytes(len);
	byte[] hash = Calc.getHashSHA256(Calc.getHashSHA256(payload));																			// Nutzdaten werden gehasht (SHA-256²)
	byte[] m_b = Convert.hexStringToByteArray(magic + "676574646174610000000000");															// magic und Befehls-Code der Nachricht
	byte[] out = new byte[24+payload.length];
	System.arraycopy(m_b, 0, out, 0, 16);																									// magic und Befehlscode wird in das Ausgangs-Array kopiert
	System.arraycopy(len, 0, out, 16, 4);																									// Länge wird in das Ausgangs-Array kopiert
	System.arraycopy(hash, 0, out, 20, 4);																									// Hash wird in das Ausgangs-Array kopiert
	System.arraycopy(payload, 0, out, 24, payload.length);																					// Hash wird in das Ausgangs-Array kopiert
	return out;
}




//Dies ist der vorbereitete leer Datensatz der "getdate" Nachricht, hier werden die "echten" Daten dann reinkopiert.
//final static byte[] GETDATA_LEER = Convert.hexStringToByteArray(Config.MAGIC + "676574646174610000000000" + "25000000" + "00000000" + "01010000000000000000000000000000000000000000000000000000000000000000000000");

/**	Spezielle GetData Methode die auf sehr schnelle Verarbeitung-Zeit optimiert ist.
	Diese Nachricht ist für das Anfordern von genau EINER TX-Transaktion gedacht.
	der alleinige Hasch der Transaktion wird übergeben und die fertige Nachricht wird zurück gegeben.
	Gemessene Zeit: 1.6 µs / Berechnung**/
//public static byte[] getdata(byte[] in) throws Exception
//{
//	byte[] out = GETDATA_LEER.clone();																// Das vorgefertigte Daten Array wird nach out kopiert.
//	byte[] data = new byte[37];																		// 37 Byte der Nutzdaten werdn angelegt.
//	System.arraycopy(GETDATA_LEER, 24, data, 0, 5);													// Die ersten festen 5Byte der Nutzdaten "0101000000" werden kopiert.
//	System.arraycopy(in, 0, data, 5, 32);															// 37 Byte der Nutzdaten werden zusammengefasst damit der Hash gebildet werden kann.
//	byte[] hash = Calc.getHashSHA256(Calc.getHashSHA256(data)); 									// der doppelte Hash SHA256 wird gebildet
//	System.arraycopy(hash, 0, out, 20, 4);															// Die ersten 4Byte vom Hash werden nach out kopiert
//	System.arraycopy(data, 0, out, 24, 37);															// Die Nutzdaten werden nach Out kopiert.
//	return out;
//}





/** Selbe wie getdata nur das hier direkt ein einziger Block nur mit einem Block-Hash angefordert werden kann.
	@param magic TestNet oder MainNet als Hex-String
	@param blockHash 32-Byte-Array des Block-Hash´s der angefordert werden soll. Direkt-Codiert ohne Swap. z.B: 00000000000000ba258ae9c4a46bfff518317cdedba96d5885c98d763267298e
	@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann.**/
public static byte[] getdata_getBlockTest(String magic, byte[] blockHash)
{
	byte[] data = Convert.swapBytesCopy(blockHash);
	byte[][] out = new byte[1][36];
	out[0][0] = 0x02; out[0][1] = 0x00; out[0][2] = 0x00; out[0][3] = 0x00;
	System.arraycopy(data, 0, out[0], 4, 32);
	return getdata(magic, out);
}




/**	Diese getdata Nachricht sendet ALLE inv-Nachritena als getdata zurück.
	Dafür wird einfach nur der Befehlscode von inv auf getdate ausgetauscht.
	@param data die vollständige raw inv-Nachricht so wie sie vom Netzwerk kommmt.
	@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann.**/
public static byte[] getdata_fromInv(byte[] data)
{
	byte[] befehl = Convert.hexStringToByteArray("676574646174610000000000");	// Befehlscode "getdata"
	System.arraycopy(befehl, 0, data, 4, 12);
	return data;
}





/**	Damit werden inf-Nachrichten über neue Blöcke angefordert. Wird benutzt um die Block-Hash´s der Blockchain in vorwärtsrichtung anzufordern.
	Damit im zweiten Schritt die Blöcke in richtiger Reihenfolge runtergeladen werden können.
	@param data 2D-Array welches ein Array der 32byte-Blockhashes enthalt.
	Jede übergebene 32Byte-Zeile im data-Array muss GENAU 32-Bytes lang sein. Im Prinzip reicht es aus nur einen Blockhash zu übergeben.
	Nämlich der, ab dem folgende Blockhash´s angefordert werden sollen. Wenn mit dem Genisis-Block begonnen werden soll, dann: "0000000000000000000000000000000000000000000000000000000000000000" übergeben.
	Der Datenstz MUSS schon geswapt über geben werden (DER-codiert) so wie er im Netzwerk übertragen wird.
	Es können maximal 255 Zeilen übergeben werden!
	Die letzte Zeile wird hier selbst eingefügt, da sie immer 0x000... ist.
	@param version Blockverion  (nicht ganz klar was die Versionen bedeuten "1" hatte funktioniert.)
	@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] getblocks(String magic, int version, byte[][] data)
{
byte[] ver = Convert.int_To_4_ByteArray_swap(version);
if(data.length > 255)   throw new IllegalArgumentException("Maximale Anzahl von 255 Header überschritten!");        					// Maximal 255 Header-Zeilen erlaubt
byte[] serial = new byte[data.length*32+37];																							// Hier wird das serialisierte Data-Array hineingelegt.
System.arraycopy(ver, 0, serial, 0, 4);																									// Die Version wird kopiert
serial[4] = (byte) (data.length&0xff);																									// Die Anzahl der Zeilen der Nutzdaten wird gespeichert.
for(int i=0;i<data.length;i++)																											// Hier wird "data" in ein eindimensionales Array serialisiert.
{
	if(data[i].length != 32) throw new IllegalArgumentException("Es wurde ein Hash übergeben der nicht genau 32Byte lang ist!");        // Nur genau 32Byte lange Eingaben erlaubt.
	System.arraycopy(data[i], 0, serial, i*32+5, 32);																					// Serialisierung
}
byte[] len = Convert.int_To_4_ByteArray(serial.length);																					// in "len" ist nun die Länge in Bytes der Nutzdaten.
Convert.swapBytes(len);
byte[] hash = Calc.getHashSHA256(Calc.getHashSHA256(serial));																			// Nutzdaten werden gehasht (SHA-256²)
byte[] m_b = Convert.hexStringToByteArray(magic + "676574626c6f636b73000000");															// magic und Befehls-Code der Nachricht
byte[] out = new byte[24+serial.length];
System.arraycopy(m_b, 0, out, 0, 16);																									// magic und Befehlscode wird in das Ausgangs-Array kopiert
System.arraycopy(len, 0, out, 16, 4);																									// Länge wird in das Ausgangs-Array kopiert
System.arraycopy(hash, 0, out, 20, 4);																									// Hash wird in das Ausgangs-Array kopiert
System.arraycopy(serial, 0, out, 24, serial.length);																					// Hash wird in das Ausgangs-Array kopiert
return out;
}








/**	TX Nachricht die eine Transaktion sendet.
	@param magic TestNet oder MainNet als Hex-String
	@param data raw-Transaktion
	@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] tx(byte[] magic, byte[] data)
{
	int len = data.length;
	byte[] out = new byte[24+len];
	System.arraycopy(magic, 0, out, 0, 4);				// magic wird gespeichert
	out[4]  = 0x74;																		// Message OP-Code
	out[5]  = 0x78;																		// Message OP-Code
	out[16] = (byte)len;																// Länge Nutzdaten
	System.arraycopy(data, 0, out, 24, len);											// Die Nutzdaten werden in das Out-Array kopiert.
	System.arraycopy( Calc.getHashSHA256(Calc.getHashSHA256(data)) ,0, out,20,4);		// Hash wird gebildet
	return out;
}








/**	Ping Nachricht
@param nonce Nonce muss hier als long übergeben werden. Beispiel: nonce = new Random().nextLong();
@param magic TestNet oder MainNet als Hex-String
@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] ping(byte[] magic, long nonce)
{
	byte[] out = new byte[32];
	System.arraycopy(magic, 0, out, 0, 4);
	out[4]  = 0x70;
	out[5]  = 0x69;
	out[6]  = 0x6e;
	out[7]  = 0x67;
	out[16] = 0x08;																	// Länge Nutzdaten (Nonce) Genau 8Byte
	byte[] n = Convert.long_To_8_ByteArray(nonce);
	System.arraycopy(n, 0, out, 24, 8);												// Die Nonce wird in das Out-Array kopiert.
	System.arraycopy( Calc.getHashSHA256(Calc.getHashSHA256(n)) ,0, out,20,4);		// Hash wird gebildet und in OUT kopiert.
	return out;
}





/**	Ping Nachricht mit fixierter Noce
@param magic TestNet oder MainNet als Hex-String
@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] ping_fix(String magic)
{
	return Convert.hexStringToByteArray(magic+"70696e67000000000000000008000000fa45cc9850494e4750494e47");
}





/**	Pong Nachricht die auf einen Ping antwortet
@param magic TestNet oder MainNet als Hex-String
@param nonce Die Nonce die vom Ping gesendet wurde, muss hier zurück gegeben werden.
@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] pong(byte[] magic, byte[] nonce)
{
	byte[] out = new byte[32];
	System.arraycopy(magic, 0, out, 0, 4);				// magic wird gespeichert
	out[4]  = 0x70;
	out[5]  = 0x6f;
	out[6]  = 0x6e;
	out[7]  = 0x67;
	out[16] = 0x08;																		// Länge Nutzdaten (Nonce) Genau 8Byte
	System.arraycopy(nonce, 0, out, 24, 8);												// Die Nonce wird in das Out-Array kopiert.
	System.arraycopy(Calc.getHashSHA256(Calc.getHashSHA256(nonce)) ,0, out,20,4);		// Hash wird gebildet und in OUT kopiert.
	return out;
}





/**	Die sendcmpct-Nachricht wird als Nachricht definiert, die eine 1-Byte-Ganzzahl gefolgt von einer 8-Byte-Ganzzahl enthält, wobei pchCommand == "sendcmpct" ist. Siehe: Bip 0152
@param magic TestNet oder MainNet als Hex-String
@param data 9Byte raw Nutzdaten. z.B. 000100000000000000
@return Entgültiger row-Datensatz der direkt so ins Netzwerk gesendet werden kann. **/
public static byte[] sendcmpct(String magic, byte[] data)
{
	byte[] out = new byte[33];
	System.arraycopy(Convert.hexStringToByteArray(magic), 0, out, 0, 4);				// magic wird gespeichert
	byte[] bCode = Convert.hexStringToByteArray("73656e64636d706374000000");			// Befehlscode
	System.arraycopy(bCode, 0, out, 4, 12);												// Befehlscode wird kopiert
	out[16] = 0x09;																		// Länge Nutzdaten Genau 9Byte
	System.arraycopy(data, 0, out, 24, 9);												// Die Nutzdaten werden in das Out-Array kopiert.
	System.arraycopy(Calc.getHashSHA256(Calc.getHashSHA256(data)) ,0, out,20,4);		// Hash wird gebildet und in OUT kopiert.
	return out;
}











/** Die Mempool-Nachricht sendet eine Anfrage an einen Knoten, die nach Informationen über Transaktionen fragt, die verifiziert, aber noch nicht bestätigt wurden....
	enthält keine Nutzdaten		**/
public static byte[] mempool(byte[] magic) throws Exception
{
	byte[] out = new byte[24];
	byte[] bCode = Convert.hexStringToByteArray("6d656d706f6f6c0000000000000000005df6e0e2");			// Befehlscode
	System.arraycopy(magic, 0, out, 0, 4);
	System.arraycopy(bCode, 0, out, 4, 20);
	return out;
}



//------------------------------------------------------------- Test Methoden zum Testen und erweitern. Nicht löschen! ---------------------------------






/** Gibt den (12Byte) Befehlssataz in Hex-String zurück.
	Ist dafür gedacht neue Befehlsätze zu codieren
	@param befehl Den Befehl direkt in Textform wie in der Dokumentation. Z.B.: "getblocks"
	@return Hex-String des Befehls-Codes, genau 12Bytes mit angehängten Nullen. **/
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




}
