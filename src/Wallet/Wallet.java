package Wallet;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.json.JSONArray;
import org.json.JSONObject;
import GUI.GUI;



/***********************************************************************************
*	Autor: Mr. Maxwell								*
*	Speichert und öffnet die CAGWallet.dat für den Coin-Address-Generator		*	
*											*
*	Aufbau der CAGWallet.dat: entschlüsselt im JSON-Format
* {
* "dateiID"  : "c6fcf3b9bc7c855f46a43e70450d2dca444b527ec5fb30ada91d9bbd90fcef7a",
*  "progName": "Coin Address Generator",
*  "version" : "V3.0.0",
*  "pwHash"  : "92b165232fbd011da355eca0b033db22b934ba9af0145a437a832d27310b89f9",
*  "list": 
*  [{
*      "Coin": "BTC",
*      "Description": "",
*      "Format"     : "P2SH",
*      "Address"    : "bc1qdjljwt79a7wsf0zmmfzcqk5xnzcms6mt6zcrvd",
*      "Priv.Key"   : "L4pvfk8nAkYeoRSwCvTG2PRwVfY8kHA6nffBHxqPpjHSEUfYgKcE",
*      "Date"       : "2020-02-26"
*    }]
* }
************************************************************************************/



public class Wallet 
{
	

	
	final static String dateiID  = "c6fcf3b9bc7c855f46a43e70450d2dca444b527ec5fb30ada91d9bbd90fcef7a";
	final static String fileName = "CAGWallet.dat";	// Name der Wallet Datei	
	 
	

/**	Wird von der GUI_Wallet aufgerufen.
	- Die CAGWallet.dat wird gelesen, oder ggf. neu erstellt
	- ParanoidDecrypt wird gestartet und öffnet den Passwort-Dialog
	@return CAGWallet.dat wird entschlüsselt und als JSONObject zurück gegeben. **/
public static JSONObject open() throws Exception
{	
	File file = new File(fileName);
	if(file.exists()==false)
	{
		JSONObject jo = new JSONObject();
		jo.put("pwHash", "");
		jo.put("list", new JSONArray());		
		save(jo);
	}
	BufferedInputStream bi = new BufferedInputStream(new FileInputStream(file));
	byte[] in = new byte[(int) file.length()];
	bi.read(in);
	bi.close();	
	String str = CryptDialog.paranoidDecrypt(GUI.frame.getX()+20, GUI.frame.getY()+20, in);	
	if(str==null) 		throw new Exception("Error in Wallet.open(). null Password.");
	if(str.equals("")) 	throw new Exception("Error in Wallet.open(). empty Password.");	
	return new JSONObject(str);
}
	


/**	Wird von der GUI_Wallet aufgerufen.
	@param jo JSONObject der Wallet wird im Klartext übergeben
	- Kopiert die CAGWallet.dat	
	- ParanoidEcrypt wird gestartet und öffnet den Passwort-Dialog
	- Die übergebene Wallet "jo" wird mit ParanoidEncrypt verschlüsset
	- CAGWallet.dat wird nun mit der neuen verschlüsselten Wallet überschrieben **/
public static void save(JSONObject jo) throws Exception
{	
	kopieBackup();
	jo.put("dateiID", dateiID);
	jo.put("progName", GUI.progName);
	jo.put("version",  GUI.version);
	byte[] ch;	
	File file = new File(fileName);
	if(file.exists()==true) ch = CryptDialog.paranoidEncrypt(GUI.frame.getX()+20, GUI.frame.getY()+20, jo );
	else ch = CryptDialog.paranoidEncrypt(GUI.frame.getX()+20, GUI.frame.getY()+20, jo);	
	if(ch == null) throw new Exception("Error Encrypt, NULL!");	
	BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(file));
	bo.write(ch);
	bo.close();	
}


// Kopiert die vorhandene (alte) Wallet-Datei nach Backup_CAGWallet.dat
private static void kopieBackup() throws IOException
{
	File src = new File(fileName);
	File des = new File("old_"+fileName);
	if(src.exists()) Files.copy(src.toPath(), des.toPath(), StandardCopyOption.REPLACE_EXISTING);
}
}
