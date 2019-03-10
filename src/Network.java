import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONObject;



  /**********************************************************************
   *                                    				*
   *    Schnittstelle mit Webseiten                  			*
   *    Hier können Webseiten geslesen werden             		*
   *    oder mit ihnen kommuniziert werden                		*
   *                                    				*
   **********************************************************************/
public class Network 
{

	public static String httpString = "https://blockchain.info/de/balance?active=";	// Der http-String der die Webseite aufruft. Hier Global weil er beim Testnet geändert wird.
	

		
// Gibt den Bitcoin Betrag von Blockchain.info zurück
// und parst den json Balance-String von Blockchain.info und gibt die "Balance" als String zurück.
public static String getBitcoinBalance(String BitcoinAddress)
{
	try {		
		String[] url = getURLString(httpString+BitcoinAddress);			// Die Webseite wird als String-Array in "url" gespeichert		
		String jon = "";	
		for(int i=0;i<url.length;i++){jon = jon + url[i];} 			// Verbindet das String-Array zu einem String, zur JSON-Verarbeitung
		JSONObject op  = new JSONObject(jon); 					// op enthält nun das JSON Object
		JSONObject x = op.getJSONObject(op.getNames(op)[0]);			// Verschachteltes "x" JSON Object im OP Object (Alle weiteren Inhalte sind im "x" Object)	
		Double	final_balance 	= x.getDouble("final_balance");			// Die Balance wird aus dem JSON Objekt geparst
		//int	n_tx			= x.getInt("n_tx");
		//int	total_received	= x.getInt("total_received");
		double betrag = final_balance/100000000.0;				// Die Balance im Bitoin-Format wird umgewandelt
		return String.format("%13.8f", betrag)+" BTC"; 	
		}
		catch (Exception e) {return "";}					// Im Fehlerfall (Wenn keine Verbindung zustande kommt) wird nichts ausgegeben. Fehler wird ignoriert.
}
	
	
	
	
	
	
// gibt den Seitenquelltext einer Webseite als String Array zurück
static String[] getURLString(String urlName) 
{
	try 
	{
		URL url = new URL(urlName);
		URLConnection con= url.openConnection();
		con.setConnectTimeout(2000);							// Time Out der Verbindung
		con.setReadTimeout(2000);							// Time Out für das Lesen
		BufferedReader bf = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String[] feld = new String[2000000];
		int i=0;
		while((feld[i] = bf.readLine()) != null) i++;
		bf.close();
		String[] out = new String[i];
		System.arraycopy(feld,0,out, 0,i);
		return out;
	} 
	catch (java.net.SocketTimeoutException e) {  return null;} 
	catch (java.io.IOException e) { return null;}	 
}
}
