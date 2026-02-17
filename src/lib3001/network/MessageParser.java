package lib3001.network;
import java.net.ConnectException;
import java.util.Arrays;
import org.json.JSONException;

import lib3001.crypt.Calc;
import lib3001.crypt.Convert;





/************************************************************************************************************************************
*	Version 0.1																									vom 15.01.2021		*
* 	LIB3001 Bibliotheks Klasse																										*
*	Diese statische Klasse parst empfangene Nachrichten vom rohen Byte-Code in verschiedene Formate.								*
*	Übergeben wird jeweils der Paylod Datensatz (Nutzdaten) aus der Peer-Klasse im Byte-Array Format und ggf. der MAGIC Wert.		*
*	Zurück gegeben werden die Daten in sinnvollen Formaten, so wie sie dann weiterverarbeitet werden können.						*
*	Es sind mehrere Methoden für den gleichen Befehl möglich, um verschiedene Rückgabe Formate zu beinhalten.						*
*	Hier sind nur Parse-Methoden enthalten die nicht schon in der Klasse Peer enthalten sind. z.B. Versionsheader					*
************************************************************************************************************************************/






public class MessageParser 
{

	
	
	
	
	
	
	
	
	
/**	Diese Methode parst nur INV-Nachrichten die Tx-Hash´s enthalten, andere Hash-Typen werden Ignoriert!
 	"inv" Nachricht wird in ein 2Dim. Byte[][] Array geparst.
	2Dim Array weil mehrere "inv" Nachrichten in einem Block vom Peer gesendet werden.
	Diese Methode ist so performant wie möglich geschrieben.
	@param data Übergeben werden die rohen Nutzdaten die als Payload von der Peer-Klasse kommen.
	@return Zurückgegeben wird ein 2Dim Array mit den geparsten Tx Has´s. (in DER-Codierung, also geswapt)
	Die erste Dimension ist eine Liste mit variabler Länge der Tx-Hash´s.
	Die zweite Dimension ist der einzelne Tx-Hash selbst, als Byte-Array im rohen Format mit der festen Länge von 32 Bytes.	**/
public static byte[][] inv_tx(byte[] data)
{
	 // Beispiel inf-Nachricht in: 01 01000000 8755813f152b676ab768f6ddae046d0911e326617a43cc8d98eafd9fb78b6e0a		*/
	int[] sizeData = Calc.decodeCompactSize(data, 0);												// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0];																	// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];																		// Die Anzahl der Zeilen (36Bytes lang)
	if((data[0]==(byte)0xFF) || (data[0]==(byte)0xFE) || len>50000) {System.out.println("\n Maximale Länge der inv-Daten von 50000 überschritten!"); return null;}
	byte[][] out = new byte[len][];
	int k = 0;
	for(int i = start; i<(len*36); i=i+36)														// 36Byte Zeilen werden durchlaufen
	{
		if(data[i]==0x01)	
		{
			byte[] b = new byte[32];
			System.arraycopy(data,i+4, b, 0, 32);
			out[k] = b;
			k++;
		}		
	}
	return Arrays.copyOfRange(out, 0, k);
}	
	
	
	


/**	Diese Methode parst Alle INV-Nachrichten, das Rückgabe-Array ist aber kein standard 32Byte-Array!
	"inv" Nachricht wird in ein 2Dim. Byte[][] Array geparst.
	2Dim Array weil mehrere "inv" Nachrichten in einem Block vom Peer gesendet werden.
	@param data Übergeben werden die rohen Nutzdaten die als Payload von der Peer-Klasse kommen.
	@return Die erste Dimension ist eine Liste mit variabler Länge.
	Die zweite Dimension enthält ein Array welches NICHT die fertigen Hashes enthält, sondern das originale Format aufweist:
	- 36 Byte-Array, die ersten 4 Bytes sind Code-Bytes die den INV-Hash-Typ identifizieren. Die Restlichen 32Byte sind der Tx Hash. (in DER-Codierung, also geswapt)
	Die ersten 4 Bytes, müssen also im übergeordneten Code ausgewertet und später entfernt werden.  
	Beispiel Rückgabe-Zeile: 01000000 8755813f152b676ab768f6ddae046d0911e326617a43cc8d98eafd9fb78b6e0a		
	Codierung des ersten Bytes:
	x01 = tx-Hash
	x02 = block Hash;
	x03 = filtered_block-hash
	x04 = CMPCT_block-hash	  **/
public static byte[][] inv(byte[] data)
{
	int[] sizeData = Calc.decodeCompactSize(data, 0);											// Die Werte für "start" und "len" werden hier gesetzt
	int len = sizeData[1];																		// Die Anzahl der Zeilen (36Bytes lang)
	if((data[0]==(byte)0xFF) || (data[0]==(byte)0xFE) || len>50000) {System.out.println("\n Maximale Länge der inv-Daten von 50000 überschritten!"); return null;}
	byte[][] out = new byte[len][];
	for(int i=0; i<len; i++)														// 36Byte Zeilen werden durchlaufen
	{
		out[i]  = new byte[36];
		System.arraycopy(data,i*36+1, out[i], 0, 36);		
	}
	return out;
}

	





/**	Dieser getdata_Tx Parser ist nur für Transaktionen gedacht. Es wird also nur geparst, wenn die getdata-Nachricht eine Transaktion anfordert.
	Andernfalls wird NULL zurückgegeben.
	@param data Übergeben werden die rohen Nutzdaten die als Payload von der Peer-Klasse kommen.
	@return Es wird ein 2Dim Byte-Array zurück gegeben, welches eine Liste mit Tx-Hashes enthält die angefordert werden.
	getdata wird als Antwort auf inv verwendet, um den Inhalt eines bestimmten Objekts abzurufen, und wird normalerweise nach dem Empfang eines inv- Pakets gesendet, nachdem bekannte Elemente gefiltert wurden. 
	Sie kann verwendet werden, um Transaktionen abzufragen, aber nur wenn sie sich im Speicherpool oder Relay-Set befinden
	Nachricht mit variabler länge: die Länge enthält die Anzahl von 36Byte langen Zeilen. Maximal 50000 Zeilen erlaubt!
	Jede Zeile (36Byte) besteht aus dem "Hash-Code-Typ" (4Byte) gefolgt von einem 32Byte langem Hash
	Beispiel getdata-Nachricht: 01 01000000 8755813f152b676ab768f6ddae046d0911e326617a43cc8d98eafd9fb78b6e0a
	Achtung! Änderung in Hashcode-Typ! Siehe bip-0144, dies steht nicht in der Protokolldokumentation! 
	Es wurde dort ein zusätzliches Witness-Flag hingelegt. Hash-Code-Typ sieht dann z.B. so aus: 01000040  **/
public static byte[][] getdata_Tx(byte[] data) throws JSONException, ConnectException
{
	int[] sizeData = Calc.decodeCompactSize(data, 0);											// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0];																	// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];																		// Die Anzahl der Zeilen (36Bytes lang)
	if((data[0]==(byte)0xFF) || (data[0]==(byte)0xFE) || len>50000) {throw new ConnectException("\nMaximale Länge der inv-Daten von 50000 überschritten!");}				
	byte[][] out = new byte[len][];	
	int j = 0;
	for(int i = start; i<(len*36); i=i+36)														// 36Byte Zeilen werden durchlaufen
	{
		byte[] b = new byte[4];																	// Hash-Code (4Byte)															
		System.arraycopy(data,i, b, 0, 4);														// Hash-Code (4Byte) wird in "b" kopiert
		byte[] h = new byte[32];																// Der eigentliche SHA-256 Hash (32Byte)
		System.arraycopy(data,i+4, h, 0, 32);													// Der eigentliche SHA-256 Hash (32Byte) wird kopiert
		Convert.swapBytes(h);																	// "DER" Codierung		
		if(b[0] == 1) out[j] = h;																// Wenn diese Byte = 1 ist, ist der Hash-Type ein Transaktions-Hash	
		j++;
	}
	return out;
}




	
	
	
	
/**	(8 Byte Nutzdaten) der festgelegten minimalen Überweisungsgebühr in Satoshi
*	Beispiel "1000" = 0.00001 BTC Gebühr.      */
public static long feefilter(byte[] data)
{
	byte[] b = new byte[8];
	System.arraycopy(data,0, b, 0, 8);
	Convert.swapBytes(b);
	return Convert.byteArray_to_long(b);	
}	
	
	
	
	
	
	
	
	
	
/**	Liste mit IP-Adressen von Peers
	@param data Übergeben wird der rohe Payload Datensatz
	@return Rückgabe ist ein String-Feld: = "IP-Adresse" **/
public static String addr(byte[] data)
{
	String out = "";
	int[] sizeData = Calc.decodeCompactSize(data, 0);											// Die Werte für "start" und "len" werden hier gesetzt
	int start = sizeData[0];																	// Position des Data-Byte-Array´s bei der die Nutzdaten beginnen
	int len = sizeData[1];																		// die Anzahl an Zeilen	(30Bytes lang) 
	if((data[0]==(byte)0xFF) || (data[0]==(byte)0xFE) || len>1000) {return "\nMessageParser.parse_addr, maximale Länge der IP-Adress-Liste von 1000 überschritten!  Länge ist: "+len;}	
	for(int i =start; i<(len*30); i=i+30)														// 30Byte Zeilen werden durchlaufen
	{
	//	byte[] b = new byte[4];																	// Timestamp ( Achtung hier nur 4 Byte ) !!!															
	//	System.arraycopy(data,i, b, 0, 4);
	//	Convert.swapBytes(b);
	//	out = out + new Timestamp(Convert.byteArray_to_int(b)*1000L) + "    ";					// Timestamp wird auf dem String ausgegeben
		byte[] b = new byte[18];																		// 18 Byte der IP Adresse																
		System.arraycopy(data,i+12, b, 0, 18);
		out = out + ConvertIP.ByteArrayToString(b) + "\n";
	}	
	return out ;
}	
}