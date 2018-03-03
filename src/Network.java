import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;



public class Network 
{

	

public static String getBitcoinBalance(String BitcoinAddress)
{
	try 
	{
		String[] str = getURLString("https://blockchain.info/de/balance?active="+BitcoinAddress);
		int posA = str[1].indexOf("final_balance");
		int posB = str[1].indexOf(",");
		double b = Double.parseDouble(str[1].substring(posA+16,posB));
		double betrag = b/100000000.0;
		return String.format("%13.8f", betrag)+" BTC"; 	
	} 
	catch (Exception e) {return "";}
}
	
	
	
static String[] getURLString(String urlName) 
{
	try 
	{
		URL url = new URL(urlName);
		URLConnection con= url.openConnection();
		con.setConnectTimeout(2000);							
		con.setReadTimeout(2000);							
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
