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
* Author: Mr. Nickolas-Antoine B.                                                               *
* Saves and opens CAGWallet.dat for the Coin Address Generator                      * 
*                                                                                   *
* Structure of CAGWallet.dat: decrypted JSON format                                  *
* {                                                                                 *
*  "dateiID": "c6fcf3...",                                                        *
*  "progName": "Coin Address Generator",                                          *
*  "version" : "V3.0.0",                                                          *
*  "pwHash"  : "92b1...",                                                         *
*  "list": [ { ... } ]                                                              *
* }                                                                                 *
************************************************************************************/



public class Wallet 
{
	

	
	final static String dateiID  = "c6fcf3b9bc7c855f46a43e70450d2dca444b527ec5fb30ada91d9bbd90fcef7a";
	final static String fileName = "CAGWallet.dat";	// Name der Wallet Datei	
	 
	

/** Called from GUI_Wallet.open().
 - Reads or creates CAGWallet.dat
 - Starts ParanoidDecrypt and opens the password dialog
 @return Decrypted wallet as JSONObject **/
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
	


/** Called from GUI_Wallet.save().
 @param jo Wallet JSONObject in plaintext
 - Creates backup copy
 - Starts ParanoidEncrypt and opens password dialog
 - Encrypts jo and overwrites CAGWallet.dat **/
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


// Copies the existing wallet file to old_CAGWallet.dat
private static void kopieBackup() throws IOException
{
	File src = new File(fileName);
	File des = new File("old_"+fileName);
	if(src.exists()) Files.copy(src.toPath(), des.toPath(), StandardCopyOption.REPLACE_EXISTING);
}
}