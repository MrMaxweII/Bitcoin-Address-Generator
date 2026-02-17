package CoinGen;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.DefaultComboBoxModel;
import org.json.JSONArray;
import org.json.JSONObject;
import GUI.GUI;
import GUI.GUI_InfoText;
import GUI.GUI_SignTx;




/***************************************************************************************************  
 *   Läd und speichert die ConfigAddressGen.json Datei.												*
 *   Wenn die Datei nicht vorhanden ist, wird sie beim Programmende automatisch gespeichert.		*
 *   Diese Datei beinhaltet diverse Einstellungen und Zustände im Programm, außer die Eingabe Felder!*
 ***************************************************************************************************/



public class Config 
{
	
final static String fileName = "ConfigAddressGen.json";												// Name der Configurations Datei für diese. Programm
final static String dateiID  = "ecab3c3cd7470bd5c43566a59b793fdeb820e73aa426b3e86cebe56020488349";  // Die dateiID dient zur Identifizierung dieser json Datei.
	


/**	Läd die Programm Status Einstellungen aus der Datei: "ConfigAddressGen.json" und schreibt sie in die Variablen.  **/
public static void load()
{
	File f = new File(fileName);
	if(f.exists())
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			String str = "";
			while(br.ready()) str = str +br.readLine();		
			br.close();
			JSONObject jo = new JSONObject(str);				
			String dateiID = jo.getString("dateiID");
			String version = jo.getString("version");
			if(dateiID.equals(Config.dateiID)==false) 	throw new FileNotFoundException("The file "+fileName+" is an incorrect file!");
			if(version.equals(GUI.version)==false) 		throw new FileNotFoundException("The file "+fileName+" has the wrong version!");
			JSONArray array = jo.getJSONArray("coinList");
			String[] list = new String[array.length()];
			for(int i=0;i<array.length();i++) list[i] = array.getString(i);		
			GUI.setSelectedAddressFormat(jo.getString("addressFormat"));
			GUI.comboBox_coin	.setModel(new DefaultComboBoxModel(list));			
			GUI.comboBox_coin	.setSelectedIndex(jo.getInt("comboBox_coin"));
			GUI.cBox_language	.setSelectedIndex(jo.getInt("languageIndex"));
			GUI.tabbedPane		.setSelectedIndex(jo.getInt("tabbedPaneInput"));
			GUI_SignTx.lbl_file_uTx	.setText(jo.getString("fileTx"));
			GUI_SignTx.cBox_sigMethode.setSelectedIndex(jo.getInt("sigMethodeIndex"));
			GUI.txt_max			.setText(jo.getString("maxKeys"));
			GUI.mItem_newCoin	.setEnabled(jo.getBoolean("enabledImportCoin"));
			GUI.mItem_removeCoin.setEnabled(jo.getBoolean("enabledImportCoin"));
			GUI.posX = jo.getInt("posX");
			GUI.posY = jo.getInt("posY");	
			GUI.toolTipEnabled.setSelected(jo.getBoolean("toolTipEnabled"));
			GUI.toolTipSetDismissDelay = jo.getInt("toolTipSetDismissDelay");
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if(GUI.posX > screenSize.width-855 || GUI.posX < 0)  GUI.posX=0;
			if(GUI.posY > screenSize.height-340|| GUI.posY < 0) GUI.posY=0;			
			GUI.frame.setLocation(GUI.posX, GUI.posY);			
		} 
		catch (Exception e) {e.printStackTrace();}
	}	
	else 
	{
		Translate.showStartDialogg(); 
		GUI_InfoText.showFirstInfo();
	}
}
	


public static void save()
{
	try 
	{
		JSONObject jo = new JSONObject();
		jo.put("dateiID", dateiID);   
		jo.put("progName", GUI.progName);
		jo.put("version", GUI.version);
		jo.put("autor", GUI.autor);
		jo.put("posX", GUI.frame.getX());
		jo.put("posY", GUI.frame.getY());
		int size = GUI.comboBox_coin.getItemCount();
		String[] list = new String[size];				
		for (int i=0;i<size;i++)  list[i] = (String) GUI.comboBox_coin.getItemAt(i);		
		JSONArray array = new JSONArray(list);
		jo.put("coinList", array);
		jo.put("comboBox_coin", 		GUI.comboBox_coin.getSelectedIndex());
		jo.put("languageIndex", 		GUI.cBox_language.getSelectedIndex());
		jo.put("addressFormat", 		GUI.getSelectedAddressFormat());
		jo.put("tabbedPaneInput", 		GUI.tabbedPane.getSelectedIndex());
		jo.put("fileTx", 				GUI_SignTx.lbl_file_uTx.getText());
		jo.put("sigMethodeIndex", 		GUI_SignTx.cBox_sigMethode.getSelectedIndex());	
		jo.put("maxKeys", 				GUI.txt_max.getText());
		jo.put("enabledImportCoin", 	GUI.mItem_newCoin.isEnabled());
		jo.put("enabledImportCoin", 	GUI.mItem_removeCoin.isEnabled());
		jo.put("toolTipEnabled", 		GUI.toolTipEnabled.isSelected());
		jo.put("toolTipSetDismissDelay",GUI.toolTipSetDismissDelay);	
		BufferedWriter br = new BufferedWriter(new FileWriter(fileName));
		br.write(jo.toString(1));
		br.close();
	} 
	catch (Exception e) {e.printStackTrace();}
}
}