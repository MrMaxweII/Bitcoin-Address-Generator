package CoinGen;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONObject;



/***********************************************************************************
*	Version 0.2    			Autor: Mr. Maxwell					vom 29.11.2022 		*
*	Kommunikation mit Webseiten														*
*	Statische Klasse mit verschiedenen Methoden  									*
************************************************************************************/



public class Web 
{


	
/**	@param coinAddress  Die Coin-Adresse als String
	@param coinParameterSymbol Es wird das Symbol als String aus der CoinParameter-Klasse übergeben, um den jeweiligen Coin auszuwählen
	@return Gibt den Link als String zu einer Adresse in einem Blockexprorer des gewünschten Coins zurück.    **/
public static String getLink(String coinAddress, String coinParameterSymbol)
{
      switch(coinParameterSymbol) 
      { 
          case "BTC"   	: return "https://www.blockchain.com/btc/address/" 			+ coinAddress;  
          case "BTC-T" 	: return "https://live.blockcypher.com/btc-testnet/address/"+ coinAddress;
          case "BCH" 	: return "https://blockchair.com/bitcoin-cash/address/" 	+ coinAddress;
          case "BCH-T" 	: return "https://explorer.bitcoin.com/tbch/address/" 		+ coinAddress;
          case "LTC" 	: return "https://blockchair.com/litecoin/address/" 		+ coinAddress;
          case "LTC-T" 	: return "https://blockexplorer.one/ltc/testnet/address/" 	+ coinAddress;
          case "BSV" 	: return "https://blockchair.com/bitcoin-sv/address/" 		+ coinAddress;
          case "BSV-T" 	: return "https://testnet.bitcoincloud.net/address/" 		+ coinAddress;   
          case "DOGE" 	: return "https://live.blockcypher.com/doge/address/" 		+ coinAddress; 
          case "DASH" 	: return "https://live.blockcypher.com/dash/address/" 		+ coinAddress; 

      } 
	return "";
}
	




/**Gibt das Guthaben einer Coin-Adresse online aus der blockcypher.com API zurück
//@param coinAddress  Die Coin-Adresse als String
//@param coinParameterSymbol Es wird das Symbol aus der CoinParameter-Klasse übergeben um den jeweiligen Coin auszuwählen (z.B. "BTC" für Bitcoin)
//@return Gibt den aktuellen Betrag des entsprechenden Coin als double zurück.  **/
public static double getValue(String coinAddress, String coinParameterSymbol) 
{
	try
	{
		switch(coinParameterSymbol) 
	      { 
	      	case "BTC"  : {return getJSON("https://api.blockcypher.com/v1/btc/main/addrs/" +coinAddress+"/balance").getDouble("balance");} 
	      	case "BTC-T": {return getJSON("https://api.blockcypher.com/v1/btc/test3/addrs/"+coinAddress+"/balance").getDouble("balance");} 
	      	case "LTC"  : {return getJSON("https://api.blockcypher.com/v1/ltc/main/addrs/" +coinAddress+"/balance").getDouble("balance");} 
	      	case "DOGE" : {return getJSON("https://api.blockcypher.com/v1/doge/main/addrs/"+coinAddress+"/balance").getDouble("balance");}      	
	      	case "DASH" : {return getJSON("https://api.blockcypher.com/v1/dash/main/addrs/"+coinAddress+"/balance").getDouble("balance");}
	      } 
		}
	catch(Exception e) {}
	return -1;
}




///**Gibt das Guthaben einer Coin-Adresse online aus der Blockchain.info API zurück
//@param coinAddress  Die Coin-Adresse als String
//@param coinParameterSymbol Es wird das Symbol aus der CoinParameter-Klasse übergeben um den jeweiligen Coin auszuwählen (z.B. "BTC" für Bitcoin)
//@return Gibt den aktuellen Betrag des entsprechenden Coin als double zurück.  **/
//public static double getValue(String coinAddress, String coinParameterSymbol) 
//{
//	try
//	{
//		switch(coinParameterSymbol) 
//	      { 
//	          case "BTC"  : {return getJSON("https://blockchain.info/balance?active="+coinAddress).getJSONObject(coinAddress).getDouble("final_balance");} 
//	      } 
//		}
//	catch(Exception e) {}
//	return -1;
//}






//** Blockchair geht nicht mehr!
//Gibt das Guthaben einer Coin-Adresse aus dem Web zurück
//@param coinAddress  Die Coin-Adresse als String
//@param coinParameterSymbol Es wird das Symbol aus der CoinParameter-Klasse übergeben um den jeweiligen Coin auszuwählen (z.B. "BTC" für Bitcoin)
//@return Gibt den aktuellen Betrag des entsprechenden Coin als double zurück.  **/
//public static double getValue(String coinAddress, String coinParameterSymbol) 
//{
//	try
//	{
//		switch(coinParameterSymbol) 
//	      { 
//	          case "BTC"  : {return getJSON("https://api.blockchair.com/bitcoin/dashboards/address/"+ coinAddress)		.getJSONObject("data").getJSONObject(coinAddress).getJSONObject("address").getDouble("balance");} 
//	          case "BTC-T": {return getJSON("https://api.blockchair.com/bitcoin/testnet/dashboards/address/"+coinAddress).getJSONObject("data").getJSONObject(coinAddress).getJSONObject("address").getDouble("balance");}
//	          case "BCH"  : {return getJSON("https://api.blockchair.com/bitcoin-cash/dashboards/address/"+coinAddress)	.getJSONObject("data").getJSONObject(coinAddress).getJSONObject("address").getDouble("balance");}
//	          case "LTC"  : {return getJSON("https://api.blockchair.com/litecoin/dashboards/address/"+coinAddress)		.getJSONObject("data").getJSONObject(coinAddress).getJSONObject("address").getDouble("balance");}
//	          case "BSV"  : {return getJSON("https://api.blockchair.com/bitcoin-sv/dashboards/address/"+coinAddress)	.getJSONObject("data").getJSONObject(coinAddress).getJSONObject("address").getDouble("balance");} 
//	      } 
//		}
//	catch(Exception e) {}
//	return -1;
//}







/**	@param coinAddress  Die Coin-Adresse als String
	@param coinParameterSymbol Es wird das Symbol als String aus der CoinParameter-Klasse übergeben, um den jeweiligen Coin auszuwählen
	@return Gibt den Link in (konvertiert in HTML-Form) als String zu einer Adresse in einem Blockexprorer des gewünschten Coins zurück.    **/
public static String getLinkHTML(String coinAddress, String coinParameterSymbol)
{	
	String str = getLink(coinAddress,coinParameterSymbol);
	if(str.equals("")) return ""; 
	else return  "<a href="+str+">"+coinAddress+"</a>"; 	
}


	
/**	Gibt eine JSON Webseite als JSONObject zurück. 
	@param url Die URL der Webseite als String
	@return JSON-Object des gesamten Inhaltes **/
public static JSONObject getJSON(String url) throws Exception
{
	InputStream is = new URL(url).openStream();	   
	BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	StringBuilder sb = new StringBuilder();
	while(rd.ready()) sb.append(rd.readLine());  
	rd.close();	 
	JSONObject json = new JSONObject(sb.toString());
	return json;	
}	
}