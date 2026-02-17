package lib3001.network;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import lib3001.crypt.Calc;
import lib3001.crypt.Convert;





	/****************************************************************************************************************************
	 *		Version 1.0																				vom 01.01.2021 				*
	 * 		LIB3001 Bibliotheks Klasse																							*
	 *		Dies ist eine "nicht statische Klasse" die eine Verbindung mit einem Bitcoin-Peer aufnimmt und mit ihm kommuniziert.*
	 *		Es dürfen keine static Variablen (außer die Befehls-Sätze am Anfang) verwendet werden!								*
	 *																															*
	 *		Mit dem Konstruktor wird zuerst ein "new Peer(ip,port,magic)" Object erstellt.										*
	 *		Der Handshake mit "Version" und "Verack" wird schon beim Konstruktoraufruf durchgeführt.							*
	 *		Zum Senden kann dann send() und zum empfangen receive() aufgerufen werden.											*
	 *		Achtung: die Methode send() ist "synchronized! (Andere Threads warten)												*
	 *		Es wird immer nur eine Nachricht / recive()-Aufruf empfangen.														*
	 *		Nun können die empfangenen Daten mit den verschiedenen get-Methoden aufgerufen werden.								*
	 *																															*
	 *		Bitcoin Peer Kommunikation Doku: 																					*
	 *		https://coinlogic.wordpress.com/2014/03/09/the-bitcoin-protocol-4-network-messages-1-version/						*
	 *		https://en.bitcoin.it/wiki/Protocol_documentation																	*
	 *		Bitcoin Port: 	 8333																								*
	 *		Bitcoin Testnet: 18333																								*
	 *																															*
	 *		ip address:           72.65.246.84   :18333 		TestNet-Peer													*
	 *		dns address:          testnet2.libbitcoin.net																		*
	 *																															*
	 *		"magic" Bitcoin Hauptnetz: 		f9beb4d9																			*
	 *		"magic" Bitcoin TestNet: 		0b110907																			*
	 *		Eigener Bitcoin MainNet Client: 192.168.178.20";																	*
	 *		Eigener Bitcoin TestNet Client: 192.168.178.30";																	*
	 *																															*
	 *																															*
	 ****************************************************************************************************************************/









public class Peer
{






// static Variablen der Befehlssätze in Long.
final public static long VERSION		= 	8531350909138988544L;
final public static long VERACK			=	8531350831728623616L;
final public static long GETADDR		=	7450489120199307776L;
final public static long ADDR			=	7017844560139124736L;
final public static long INV			=	7597139363792683008L;
final public static long GETDATA		=	7450489133034922240L;
final public static long SENDHEADERS	=	8315173664536748388L;
final public static long GETHEADERS		=	7450489150280655973L;
final public static long HEADERS		=	7522525836418904832L;
final public static long TX				=	8392457905604919296L;
final static public long BLOCK			=	7092165985989296128L;
final static public long GETBLOCKS		=	7450489124629209963L;
final static public long MEMPOOL		=	7882827052455259136L;
final static public long FEEFILTER		=	7378415055046210661L;
final static public long NOTFOUND		=	7957707049892408932L;
final static public long REJECT			=	8243111676664348672L;
final static public long PING			=	8100126793463234560L;
final static public long PONG			= 	8101815643323498496L;
final static public long CHECKORDER		=	7163086685011407460L; // Veraltet
final static public long SUBMITORDER	=	8319664108697055090L; // Veraltet
final static public long REPLY			=	8243118304160382976L; // Veraltet
final static public long ALERT			=	7020097461410594816L; // Veraltet
final static public long FILTERLOAD		=	7379548711597272175L; // Bip 037
final static public long FILTERADD		=	7379548711597269348L; // Bip 037
final static public long FILTERCLEAR	=	7379548711597269868L; // Bip 037
final static public long MERKLEBLOCK	=	7882832528487572076L; // Bip 037
final static public long SENDCMPCT		=	8315173664453390435L; // Bip 152
final static public long cmpctblock		=	7164506154673007727L; // Bip 152
final static public long getblocktxn	=	7450489124629209963L; // Bip 152
final static public long blocktxn		=	7092165985996929134L; // Bip 152








// Private Variablen
private Socket 			soc;									// Der Socket
private String			ip;										// Die IP-Adresse mit Port zum verbundenem Peer
private JSONObject		my_versionsHeader;						// Mein Versionsheader der an den Peer geschickt wird.
private DataInputStream in;										// Der  InputStream der empfangenen Nachricht
private	OutputStream 	out;									// Output Stream zum Senden der Nachrichten
private	JSONObject 		jo;										// Hier wird der Versions-Header des verbundenen Peer als JSON gespeichert.
private int				timeOut = 3000;							// Time-Out in milli-Sekunden beim Verbindungsaufbau
private	byte[]			magic_my;								// Mein eigener magic-Wert für das Netzwerk in dem ich mich befinde. (Zum vergleich mit den empfangenen)
private	byte[] 			magic		= new byte[4];				// Der magic-Wert der letzten Nachricht
private	byte[] 			befehl_b	= new byte[12];   			// [12Byte] Der Befehlssatz als ByteArray. Der Befehlssatz signalisiert um was für eine Nachricht es sich handelt.
private	long			befehl_l;								// Der Befehls-Satz der letzten Nachricht, der den Nachrichtentyp Interpretiert als long
private int				len;									//  Länge der Nutzdaten in Integer. Damit sind die Anzahl an Bytes der Nutzdaten gemeint.
private byte[]			len_b	= new byte[4];					// [ 4 Byte]__Länge der Nutzdaten. (Achtung: ByteSwap ist an der Stelle schon durchgeführt wurden!)
private byte[] 			hash 	= new byte[4];					// [ 4 Byte]__Der übermittelte doppelte SHA-256 Hash der Nutzdaten. (Nicht der Hash der selbst gebildet werden kann!) */
private int				hashInt;								// Der übermittelte doppelte SHA-256 Hash als Integer der Nutzdaten. (Nicht der Hash der selbst gebildet werden kann!) */
private byte[] 			payloadData;							// Die rohen Nutzdaten in variabler Länge. Die Nutzdaten sind hier unverändert so wie sie original vom Peer kommen. */








// -------------------------------------------------------------------------------------------- Konstruktor ---------------------------------------------------------------------------------------------------//




/** Mit dem Konstruktor wird die Verbindung zu einem Peer aufgenommen. Dies beinhaltet den Austausch von Versions- und Verack-Nachrichten.
	Der Verbundene Peer wird sofort damit beginnen inv-Nachrichten zu senden.
	@param ip IP-Adresse als String z.B. 194.123.123.123:18333
	@param magic_my "f9beb4d9" = Main-Net, "0b110907" = TestNet
	@param versionsHeader Ein Versios-Header als JSON-Object der in der Klasse "MessageList" beschrieben wird. Es kann NULL übergeben werden, wenn ein default Versions-Header verwendet werden soll.**/
public Peer(String ip, byte[] magic_my, JSONObject versionsHeader) throws Exception
{
	this.ip = ip;
	this.magic_my = magic_my;
	this.my_versionsHeader = versionsHeader;
	connect();
}



/** Mit dem Konstruktor wird die Verbindung zu einem Peer aufgenommen. Dies beinhaltet den Austausch von Versions- und Verack-Nachrichten.
	Der Verbundene Peer wird sofort damit beginnen inv-Nachrichten zu senden.
	@param ip IP-Adresse als String z.B. 194.123.123.123:18333
	@param magic_my "f9beb4d9" = Main-Net, "0b110907" = TestNet
	@param versionsHeader Ein Versios-Header als JSON-Object der in der Klasse "MessageList" beschrieben wird. Es kann NULL übergeben werden, wenn ein default Versions-Header verwendet werden soll.
	@param timeOut setzt ein TimeOut beim ersten Verbindungsversuch in milli-Sekunden. Standart ist: 3ms*   **/
public Peer(String ip, byte[] magic_my, JSONObject versionsHeader, int timeOut) throws Exception
{
	this.ip = ip;
	this.magic_my = magic_my;
	this.my_versionsHeader = versionsHeader;
	this.timeOut = timeOut;
	connect();
}






//--------------------------------------------------------------------------------------------- Kontruktor Hilfsmethoden ------------------------------------------------------------------------------------//



// Diese Methode wird nur vom Konstruktor und von reconnect() aufgerufen. Muss Private bleiben!
// Öffnet Socket zum Peer und schickt die erste Versionsnachricht.
private void connect() throws Exception
{
	String[] ipAddr = ConvertIP.splitIP(ip);																												// Splittet die IP-Adresse in IP und Port auf.
	soc = new Socket();																																		// Socket wird geöffnet
	soc.connect(new InetSocketAddress(ipAddr[0], Integer.valueOf(ipAddr[1])), timeOut);																		// Socket wird verbunden
	out = soc.getOutputStream();																															// OutputStream zum Senden wird angelegt
	send(MessageList.version(magic_my, my_versionsHeader));																									// Version wird gesendet (Siehe Versions-Daten in der MessageList)
	in = new DataInputStream(soc.getInputStream());																											// Der InputStream auf dem Nachrichten empfangen werden
	receive(); 																																				// Empfängt erste Nachricht vom Peer. Sollte die Versioins-Nachricht sein.
	if(!java.util.Arrays.equals(magic_my,magic))	{soc.close(); 	out.close();	throw new ConnectException("Falscher magic Wert in Versionsnachricht.");}// Wenn kein gültiger magic Wert, dann wird der Socket geschlossen.
	if(hashInt != hashCode()) 							{soc.close();	out.close();	throw new ConnectException("Falscher hashCode in Versionsnachricht.");}	// Wenn der Hash nicht richtig ist, wird der Socket geschlossen
	if(befehl_l==VERSION)																																	// Erste Nachricht MUSS Versionsnachricht sein!
	{
		jo = new JSONObject();
		jo.put("Befehls-Code", "version"); jo.put("magic", Convert.byteArrayToHexString(magic));															// magic und Befehlscode wird dem JSON-Object hinzugefügt.
		MessageParserJSON.parse_version(payloadData, jo);																								// Der Versions-Header wird dem JSON-Object hinzugefügt.
		send(MessageList.verack(magic_my));																													// Verack wird gesendet	(Damit ist die Verbindung hergestellt)
	}
	else 																																					// Wenn erste Nachricht nicht Versions-Nachricht ist.
	{
		disconnect();
		throw new ConnectException("Keine Versions-Nachricht als erste Nachricht!\nNachricht vom Peer: "+MessageParserJSON.parse(getRawData()));			// Fehlermeldung vom Peer
	}
}













//----------------------------------------------------------------------------------------------------- Haupt public Methoden ---------------------------------------------------------------------//





/** Empfängt eine neue Nachricht. Blockierende Methode!
 * 	Diese Methode empfängt genau eine Nachricht. Um weitere Nachrichten zu empfangen, muss sie erneut aufgerufen werden.
 *	Diese Methode wartet so lange bis eine Nachricht empfangen wird.
 *	Wird keine Nachricht mehr empfangen wird diese Methode niemals beendete! Sie ist dann Blockiert!
 *	ConnectException wird erzeugt (aber die Verbindung nicht getrennt) wenn: der magic-Wert falsch ist, oder wenn der hashCode falsch ist. **/
public void receive() throws IOException
{
	try
	{
		in.readFully(magic);																									// Die ersten 4Byte "magic" werden empfange
		in.readFully(befehl_b);																									// 12 Byte Befehls-Satz werden gelesen
		befehl_l = Convert.byteArray_to_long(befehl_b);																			// Befehls-Satz wird in Long konvertiert
		in.readFully(len_b);																									// 4 Byte Länge Nutzdaten werden empfangen
		Convert.swapBytes(len_b);																								// Byte-Swap wird durchgeführt da die Bytes hier in der falschen Reihenfolge vorliegen
		len = Convert.byteArray_to_int(len_b);																					// Die Länge (Nutzdaten) wird in Integer umgerechnet
		in.readFully(hash);																										// 4 Byte Hash werden empfangen
		hashInt = Convert.byteArray_to_int(hash);																				// Hash als Integer der empfangen wurde
		payloadData = new byte[len];																							// Dass Array mit der richtigen Länge der Nutzdaten wird erstellt
		in.readFully(payloadData);																								// Die restlichen Nutzdaten werden empfangen
	}
	catch (IOException e) {disconnect();	throw new EOFException("Socket geschlossen, receive() wurde abgebrochen.");}		// Wenn die Verbindung unterbrochen wird
	if(!checkmagic(magic))			throw new ConnectException("Falscher magic Wert in Nachricht. ");					// Prüfung ob magic richtig ist
	if(hashInt != hashCode()) 				throw new ConnectException("Falscher hashCode in Nachricht. ");						// Prüfung ob hashCode richtig ist
}



/** Sendet eine Nachricht zum Peer. Achtung diese Methode ist synchronized! Andere Threads warten.
 *	@param in raw-Daten die direkt so in das Netzwerk geschickt werden.  **/
public synchronized void send(byte[] in) throws IOException
{
	out.write(in);
}



/**	Trennt die Verbindung in dem der Socket geschlossen wird.
	Durch das Schließen des Sockets werden auch InputStream und OutputStream des Sockets geschlossen. (Laut Socket-Doku)
	@exception IOException Das Schließen einer Verbindung darf keinen Fehler verursachen. Wenn dieser Fehler doch auftritt, strürzt das Programm ab!  **/
public void disconnect()
{
	try {soc.close();}
	catch (IOException e) {e.printStackTrace();}
}



/**	@deprecated
 	Trennt die Verbindung und verbindet sich neu. Funktioniert nur für den einzigen Thread der den Socket hat. Und das ist in der Regel die Empfangsschleife.
 	Der ConnectionHandler oder ein anderer Thread, darf diese Methode nicht verwenden, da ein neu Verbinden dann nicht mehr funktioniert!
 	Für ein korrektes Reconnect müssen alle verbundenen Threads mit dem Socket komplett beendet werden und eine neue Verbindung zur selben IP aufgebaut werden.  **/
@Deprecated
public void reconnect() throws Exception
{
	disconnect();
	connect();
}










// ------------------------------------------------------------------------------ Get-Methoden  --------------------------------------------------------------------- //




/** Gibt den magic-Wert der letzten Nachricht als 4-Byte Array zurück. **/
public byte[] getmagic()
{
	return magic;
}


/** Gibt den eigenen magic-Wert der dem Konstruktor übergeben wurde zurück**/
public byte[] getmagic_my()
{
	return magic_my;
}


/** Gibt den Nachrichten-Befehl als long zurück.
 * 	Dies ist der Befehls-Code der den Nachrichtentyp interpretiert.  **/
public long getBefehl()
{
	return befehl_l;
}


/** Gibt den Befehls-Satz als String zurück. Achtung: der Befehls-String ist gefolgt von Nullen und kann daher nicht mit der String.equals() Methode direkt verglichen werden.
 *  Nur für Testzwecke Benutzen! Sonnst getBefehl() als Long verwenden! **/
public String getBefehlString()
{
	return new String(befehl_b);
}


/** Gibt den Nachrichten-Befehl als 12ByteArray zurück, so wie er original empfangen wird.
 * 	Dies ist der Befehls-Code der den Nachrichtentyp interpretiert.  **/
public byte[] getBefehlByte()
{
	return befehl_b;
}


/** Gibt den rohen Datensatz der vollständigen Nachricht als Byte-Array zurück.
 	Falls keine Daten vorhanden sind wird ein ByteArray mit null Element übergeben.   **/
public byte[] getRawData()
{
	try {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(magic);
		out.write(befehl_b);
		out.write(Convert.swapBytesCopy(len_b));
		out.write(hash);
		out.write(payloadData);
		return out.toByteArray();
	}
	catch (IOException e){return new byte[0];}
}


/** Gibt den rohen Datensatz der Nutzdaten als Byte-Array in Hex zurück.  **/
public byte[] getPayload()
{
	return payloadData;
}


/** Gibt den empfangenden HashCode in der Nachricht zurück  **/
public int getHash()
{
	return hashInt;
}


/** Berechnet den doppelten SHA-256 Hash der Nutzdaten und gibt die ersten 4Bytes als Integer zurück.
 *	Dieser HashCode kann direkt mit dem "getHash" verglichen werden. Sind die beiden Hash´s gleich, ist die Nachricht richtig empfangen wurden. */
@Override
public int hashCode()
{
	byte[] HASH = Calc.getHashSHA256(Calc.getHashSHA256(payloadData));					// 2x SHA-256 der Nutzdaten wird berechnet
	return Convert.byteArray_to_int(Arrays.copyOfRange(HASH, 0, 4));			// Hash wird auf 4 Bytes gekürzt
}


/** Gibt einen Hex-String der rohen Nutzdaten zurück. Achtung: Sehr Resourcenfressend, nicht verwenden in kritischen Bereichen!*/
@Override
public String toString()
{
	return Convert.byteArrayToHexString(payloadData);
}








// --------------------------------------- Get-Methoden des Versions-Headers --------------------------------------------------



/** @return Gibt den vollständigen Versions-Header vom verbundenen Peer als  JSON-Object zurück. **/
public JSONObject getVersionHeaderJSON()
{
	return jo;
}


/** @return Gibt den vollständigen Versions-Header vom verbundenen Peer als  JSON-String zurück. **/
public String getVersionHeaderString() throws JSONException
{
	return jo.toString(4);
}


/**  	Gibt die vom Knoten verwendete Protokollversion als Integer zurück.
 * 		Beispiel: 70013 **/
public int getProtokollversion() throws JSONException
{
	return jo.getInt("BitcoinCore-Version");
}


/**  	Gibt ein Hex-String welches ein Bitfeld der für diese Verbindung zu aktivierenden Funktionen enthält.
 * 		Beispiel: 0000000000000009 **/
public String getServices() throws JSONException
{
	return jo.getString("services");
}


/**  	Gibt den Zeitstempel der ersten Verbindung des Peers als Timestamp zurück.
 * 		Zum umwandeln in String: die toString() Methode verwenden!
 * 		Beispiel: 2019-03-19 20:26:47.0 **/
public Timestamp getTimestamp() throws JSONException
{
	return (Timestamp) jo.get("timestamp");
}


/**  	Gibt die eigene IP-Adresse als String zurück die der Peer im Versions-Header sendet.
 * 		Beispiel: 123.123.123.123:55555 **/
public String getIP_my() throws JSONException
{
	return jo.getString("IP-Address-Gegenstelle");
}


/**  	Gibt die IP-Adresse des Verbundenen Peer als String zurück.
 * 		Achtung, dies ist die IP-Adresse die der Peer in der Versionsnachricht selbst sendet und kann daher auch falsch sein.
 * 		Beispiel: 72.65.246.84:18333 **/
public String getIP_Peer() throws JSONException
{
	return jo.getString("IP-Address Sender");
}


/**  	Gibt die Nonce als Hex-String zurück.
 * 		Zufällig generierte Nonce wird verwendet, um verbindungen zu sich selbst zu erkennen.
 * 		Beispiel: 1b1ab09a847a7461 **/
public String getNonce() throws JSONException
{
	return jo.getString("nonce");
}


/**  	Gibt den User-Agent als lesbaren String zurück.
 * 		Der User-Agent ist der selbst gewählte Name des verbundenen Peer.
 * 		Beispiel: /libbitcoin:3.4.0/ **/
public String getUserAgent() throws JSONException
{
	return jo.getString("User-Agent");
}


/**  	Gibt die Blockhöhe des letzten Block, der vom verbundenen Peer empfangen wurde als Integer zurück.
 * 		Beispiel: 1485274 **/
public int getLastBlock() throws JSONException
{
	return jo.getInt("last-Block");
}


/**  	Gibt einen boolean Wert "Relay" zurück.
 * 		Ob der Remote-Peer weitergeleitete Transaktionen ankündigen soll oder nicht, finden Sie unter BIP 0037  **/
public boolean getRelay() throws JSONException
{
	return jo.getBoolean("relay");
}









//-------------------------------------------------- Private Methoden ------------------------------------------------------------------------------------------------------//



// Vergleicht den übergebenen magic-Wert mit dem festgelegten "magic_my" Wert.
// Ist der magic-Wert richtig, wirt true zurück gegeben.
private boolean checkmagic(byte[] in)
{
	if ((in[0] == magic_my[0]) && (in[1] == magic_my[1]) && (in[2] == magic_my[2]) && (in[3] == magic_my[3])) return true;
	else return false;
}

}