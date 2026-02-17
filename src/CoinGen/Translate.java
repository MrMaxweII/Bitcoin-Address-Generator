package CoinGen;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import GUI.GUI;


	/************************************************************************************************************************************
	 *	Translate V1.0                                            Mr.Maxwell                                             28.01.2026		*
	 *	Hier wird die Sprachumschaltung für verschiedene Sprachen implementiert.														*
	 *																																	*
	 *	Bedienung:																														*
	 *	1.: public static Translate t = new Translate(); 																				*
	 *	2.: System.out.println(t.t("Hello World"));																						*
	 *																																	*
	 *	Achtung. Die Klasse ist NICHT static, es muss der Konsruktor verwendet werden!													*
	 *	Es werden keine Texte übersetzt die an einen Logger geschickt werden. 															*										
	 ***********************************************************************************************************************************/



public class Translate
{

	public static String[] 	languages = {"English", "German"};						// Die Verfügbaren Sprachen
	private JSONObject 		englishTextJSON = new JSONObject(); 					// Enthält die Englischen Texte als JSON-Object
	private JSONObject 		germanTextJSON = new JSONObject(); 						// Enthält die Deutschen  Texte als JSON-Object

	



	// Konstruktor. Muss beim Start des Programms aufgerufen werden, Legt die Sprache fest
 	public Translate()					
 	{ 		
 		try
 		{
 			setEnglishJSON();
 	 		setGermanJSON(); 	 		
 		}
 		catch(JSONException e){e.printStackTrace();}
 	}
 	
 	
 	
 	
 	// Gibt ein Dialog-Feld am Bildschirm aus, in dem die Sprache augsgewählt werden soll.
 	// Wird von der Klasse "Config" aufgerufen, wenn das Programm das erste mal starten. (Wenn die TxBuilder.json nicht vorhanen ist)
 	public static void showStartDialogg()
 	{
		JDialog dialog = new JDialog();   
		dialog.setLocationRelativeTo(null);  // Dialog im Mittelpunkt des Bildschirms anzeigen
        dialog.setLayout(null);
        dialog.setSize(500, 320); 
		dialog.setModal(true);
		dialog.setTitle(GUI.progName+"     "+GUI.version);
		dialog.setIconImage(MyIcons.bitcoinLogoMain.getImage());	
		JComboBox cb = new JComboBox();
		JButton btn = new JButton();
		btn.setBounds(150, 240, 200, 30);
		btn.setText("OK");
		cb.setBounds(150, 80, 200, 55);
		cb	.setBorder(new TitledBorder(new EmptyBorder(0, 0, 0, 0),"Choose language", TitledBorder.LEADING, TitledBorder.TOP, new Font("DejaVu Sans", Font.PLAIN, 18), GUI.color4));
		cb	.setModel(new DefaultComboBoxModel(languages));
		//cb	.setForeground(GUI.color3);
		cb	.setBackground(GUI.color1);
		dialog.add(cb);
		dialog.add(btn);	
		btn.addActionListener(new ActionListener() 
		{
		    public void actionPerformed(ActionEvent e) 
		    {
				GUI.cBox_language.setSelectedIndex(cb.getSelectedIndex());	
		    	dialog.dispose();  
		    }
		});
		// Close Button wird abgefangen und hier selbst verarbeitet.
		dialog.addWindowListener(new java.awt.event.WindowAdapter() 
		{
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {System.exit(0);}
		});
		dialog.setVisible(true);
 	}
 	
 
 	
 	
 
	/**	Gibt den gewünschten Text in der eingestellen Sprache zurück. **/
 	public String t(String str)
 	{
 		try
 		{
 	 		if(GUI.cBox_language.getSelectedIndex()==0) return englishTextJSON.getString(str);
 	 		if(GUI.cBox_language.getSelectedIndex()==1) return germanTextJSON.getString(str);
 	 		throw new Exception("Sprache mit dem Index = "+GUI.cBox_language.getSelectedIndex()+" wurde nicht gefunden!");
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 			GUI.txt_ausgabe.setForeground(Color.red);
 			GUI.txt_ausgabe.setText(e.getMessage());
 			return "<Translate-Error!>";	
 		}
 	}
 	
 	
	/**	Gibt ein Text-Array in der eingestellten Sprache zurück, welcher dem Schlüssel "key" entspricht.. 
	 Diese Variante wird z.B. für ComboBoxen verwendet. 
 	 @param arraySize Die Array-länge des zu erwartenden Arrays, muss zwingend hier angegeben werden!	**/
 	public String[] getTextArray(String key, int arraySize)
 	{
 		try
 		{
 			if(GUI.cBox_language.getSelectedIndex()==0) 
 			{
 	 			JSONArray jaEN = englishTextJSON.getJSONArray(key);
 	 			String[] strEN=new String[jaEN.length()];
 				for(int i=0; i<strEN.length; i++) {strEN[i]=jaEN.getString(i);}
 				return strEN;
 			}
 	 		if(GUI.cBox_language.getSelectedIndex()==1) 
 	 		{
 	 			JSONArray jaDE =  germanTextJSON.getJSONArray(key);
 	 			String[] strDE=new String[jaDE.length()];
 				for(int i=0; i<strDE.length; i++) {strDE[i]=jaDE.getString(i);}
 				return strDE;
 	 		}
 	 		throw new Exception("Sprache mit dem Index = "+GUI.cBox_language.getSelectedIndex()+" wurde nicht gefunden!");
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 			GUI.txt_ausgabe.setForeground(Color.red);
 			GUI.txt_ausgabe.setText(e.getMessage());
 			String[] strError = new String[arraySize];
 			for(int i=0; i<arraySize; i++) {strError[i] = "<Translate-Error!>";}	
 			return strError;	
 		}
 	}
 	
 	
 
 	
 	// -------------------------------------------------------- Ein Java Programm zum sortieren von diesen Texten ist im Projekt "Test" "als Klasse: SortJsonPuts.java"
 	//----------------------------------------------------------------------------------- sotiert: --------------------------------------------------------
 	
 	
 	private void setEnglishJSON() throws JSONException
 	{		 		
 		englishTextJSON.put("File"							,"File");
 		englishTextJSON.put("Info"							,"Info");
 		englishTextJSON.put("open"							,"open");
 		englishTextJSON.put("save priv"						,"save priv.Key");
 		englishTextJSON.put("edit"							,"edit");
 		englishTextJSON.put("Date"							,"Date");
 		englishTextJSON.put("Save"							,"Save");
 		englishTextJSON.put("print"							,"print");
 		englishTextJSON.put("Delete"						,"Delete");
 		englishTextJSON.put("New CVC"						,"New check digit");
 		englishTextJSON.put("load Tx"						,"load Tx");
 		englishTextJSON.put("save Tx"						,"save all");
 		englishTextJSON.put("QR code"						,"QR code");
 		englishTextJSON.put("show Tx"						,"show Tx");		
 		englishTextJSON.put("signing"						,"signing");	
 		englishTextJSON.put("Address"						,"Address");
 		englishTextJSON.put("Passwort"						,"Password");
 		englishTextJSON.put("Count = "						,"Count = ");
 		englishTextJSON.put("Settings"						,"Settings");
 		englishTextJSON.put("save Seed"						,"save Seed");
 		englishTextJSON.put("Open Seed"						,"Open Seed");
 		englishTextJSON.put("Prüfziffer"					,"Check digit");
 		englishTextJSON.put("Sig method"					,"Sig method");
 		englishTextJSON.put("Was saved: "					,"Was saved: ");
 		englishTextJSON.put("remove Seed"					,"Remove Seed");
 		englishTextJSON.put("remove Coin"					,"remove Coin"); 		
 		englishTextJSON.put("Description"					,"Description");
 		englishTextJSON.put("Seed loaded"					,"Seed loaded");
 		englishTextJSON.put("Open PrivKey"					,"Open Private Key");
 		englishTextJSON.put("scan QR code"					,"scan QR");
 		englishTextJSON.put("Seed decipher"					,"Seed decipher");
 		englishTextJSON.put("Save completed"				,"Save completed");
 		englishTextJSON.put("ToolTips enable"				,"Enable Tool Tips");
 		englishTextJSON.put("Address Format"				,"Address Format");	
 		englishTextJSON.put("load from Hexa"				,"hex input");
 		englishTextJSON.put("Seed-Extractor"				," Seed-Extractor");
 		englishTextJSON.put("import new Coin"				,"import new Coin");
 		englishTextJSON.put("Input Text-Hash"				," Input Text Hash  ");
 		englishTextJSON.put("Sign Transaktion"				,"Sign Transaktion");	
 		englishTextJSON.put("Input private key"				," Input private key");
 		englishTextJSON.put("show private keys"				,"show private keys");	
 		englishTextJSON.put("automatic signing"				,"automatic signing");
 		englishTextJSON.put("Create check digit"			,"Create check digit");
 		englishTextJSON.put("Input rolling dice"			," Input rolling dice");
 		englishTextJSON.put("export BTC address"			,"export BTC address");
 		englishTextJSON.put("Check digit wrong!"			,"Check digit wrong!");
 		englishTextJSON.put("Check digit missing!"			,"Check digit missing!");
 		englishTextJSON.put("Storage location is: "			,"Storage location is: ");
 		englishTextJSON.put("SHA256 hash of a text"			,"Generate a private key from the SHA256 hash of a text.");
 		englishTextJSON.put("Only .txn files allowed!"		,"Only .txn files allowed!");
 		englishTextJSON.put("Verschlüsselter Seed (Hexa)"	,"Encrypted Seed (Hexa)");
 		englishTextJSON.put("No Seed or Private-Keys loaded!","No Seed or Private-Keys loaded!");
 		englishTextJSON.put("Imput unsign Transaktion in Hexa"					,"Enter the unsigned transaction in Hexa.");
 		englishTextJSON.put("Unable to save, no seed decrypted."				,"Unable to save, no seed decrypted.");
 		englishTextJSON.put("Signed transactions were saved successfully."		,"Signed transactions were saved successfully.");
 		englishTextJSON.put("Seed hat falsches Format oder ist nicht vorhanden"	,"Seed is in incorrect format or does not exist");
 		englishTextJSON.put("Drag unsigned transactions (.txn) into this field!","Drag unsigned transactions (.txn) into this field!");
 		englishTextJSON.put("Saving not possible, no valid address in the output field."				,"Saving not possible, no valid address in the output field.");
 		englishTextJSON.put("Note: The location from which Transactions were imported is used."			,"Note: \nThe location from which Transactions were imported is used.");
 		englishTextJSON.put("Error! No transaction was found that could be successfully sign."			,"Error!\nNo transaction was found that could be successfully sign.");
 		englishTextJSON.put("Error input seed! \nEncrypted seed can only contain hexa characters!"		,"Error input seed! \nEncrypted seed can only contain hexa characters!"); 		
 		englishTextJSON.put("Error input seed! \nEncrypted seed must be exactly 64 characters long!"	,"Error input seed! \nEncrypted seed must be exactly 64 characters long!");
 		englishTextJSON.put("Niemals Seed eingeben auf Hardware mit möglicher Internetverbindung !!!"	,"Never enter seeds on hardware with a possible internet connection!!!");
 		englishTextJSON.put("Zum Erstellen der Prüfziffer muss ein 64Hexa-Zeichen Seed eingegeben sein!","To create the check digit, a 64 hexa character seed must be entered!");
 		englishTextJSON.put("Enter a private key directly,\nthe format will be recognized automatically."						,"Enter a private key directly,\nthe format will be recognized automatically.");
 		englishTextJSON.put("Generate private-key by rolling dice.\n100 characters, only digits between 1 and 6 are allowed!"	,"Generate private-key by rolling dice.\n100 characters, only digits between 1 and 6 are allowed!");
 		englishTextJSON.put("No seed found!\nA seed must be stored in the “Seed Extractor”!\nOr the “Sig method” must be set to “Private Keys”!","No seed found!\nA seed must be stored in the Seed-Extractor!\nOr the “Sig method” must be set to “Private Keys”!");
 		englishTextJSON.put("Signure with a list of private keys that you can enter here.\r\nPrivate keys must be formatted in Hexa Decimal format!\r\nThe order of the private keys doesn't matter. All private keys required for signing must only be present!\r\nThe private keys must separated with a comma:\r\nExample:"				,"Signure with a list of private keys that you can enter here.\r\nPrivate keys must be formatted in Hexa Decimal format!\r\nThe order of the private keys doesn't matter. All private keys required for signing must only be present!\r\nThe private keys must separated with a comma:\r\nExample:");
 		englishTextJSON.put("Die 3-stellige Prüfziffer sollte sich bei dem Seed befinden.\nSie ist nützlich um Eingabefehler zu erkennen.\nDie Prüfziffer wird normalerweise nur beim erstmaligen Erstellen des Seeds genieriert und dann notiert.\nWird die Prüfziffer neu erstellt, kann nicht erkannt werden, ob der Seed Fehler enthällt.\nSoll die Prüfziffer aus dem Seed neu berechnet werden?"				,"The 3-digit check digit should be located with the seed.\nIt is useful for detecting input errors.\nThe check digit is normally only generated when the seed is first created and then noted down.\nIf the check digit is recreated, it cannot be recognized whether the seed contains errors.\nShould the check digit be recalculated from the seed?");

 	 	// -------------------------------------------------------------------------- ToolTip Texte ------------------------------------------------------------------------------------------------------------------------------

 		
 		englishTextJSON.put("Datei Menü"												,"File menu");
 		englishTextJSON.put("Sprache ändern"											,"change language");
 		englishTextJSON.put("Crypto Währung ändern"										,"<html>Change Crypto Currency<br>BTC-MainNet or BTC-TestNet</html>");
 		englishTextJSON.put("Bitcoin Adress-Format ändern"								,"Change Bitcoin Address Format");
 		englishTextJSON.put("Die 3 Stellige Prüfziffer Eingeben!"						,"Enter the 3-digit check digit!");
 		englishTextJSON.put("Die Maximale Anzahl der Private-Keys die erzeugt werden."	,"The maximum number of private keys that will be generated.");
 		
 		englishTextJSON.put("Schlüsseldatei öffnen"										,"Opens the Key file of your seed or private key.");
 		englishTextJSON.put("Schlüsseldatei ändern"										,"Edit the Key file of your seed or private key.");
 		englishTextJSON.put("Private Key speichern"										,"Save the currently created private key to the key file.");
 		englishTextJSON.put("Seed Speichern"											,"Save the currently created seed to the key file.");
 		englishTextJSON.put("Ansicht Drucken"											,"Print the current view of the program as a screenshot");
 		
 		englishTextJSON.put("Adress Format unCompressed"								,"<html>Bitcoin WIF address format Legacy P2PKH <br> Base58 encoded, with uncompressed public key hash</html>");
 		englishTextJSON.put("Adress Format compressed"									,"<html>Bitcoin WIF address format Legacy P2PKH <br> Base58 encoded, with   compressed public key hash</html>");
 		englishTextJSON.put("Adress Format p2sh"										,"<html>Bitcoin     address format SegWit P2SH  <br> Base58 encoded, address begins with 3</html>");
 		englishTextJSON.put("Adress Format bech32"										,"<html>Bitcoin     address format SegWit Bech32<br> Base32 encoded, address begins with bc1</html>");
 		
 		englishTextJSON.put("Seed erstellen oder laden"									,"Create or load seed.");
 		englishTextJSON.put("Transaktion signieren"										,"Sign transaction");
 		
 		englishTextJSON.put("Private Key der markierten Zeile öffnen"					,"Open private key of the selected line");
 		englishTextJSON.put("Private Key der markierten Zeile als Seed öffnen"			,"Open the private key of the selected line as a seed");
 		englishTextJSON.put("Markierte Zeile löschen"									,"Delete selected line");
 		englishTextJSON.put("Alle Änderungen endgültig speichern"						,"Save all changes permanently");
 		
 		englishTextJSON.put("Hinterlegten Seed aus dem Speicher löschen"				,"Delete deposited seed from memory");
 		englishTextJSON.put("Seed hier eingeben, 64 Hex Zeichen."						,"Enter seed here, 64 hex characters");
 		englishTextJSON.put("Passwort mit dem der Seed entschlüsselt wird"				,"Password with which the seed is decrypted");
 		englishTextJSON.put("Neue Prüfziffer berechnen"									,"Calculate new check digit");
 		englishTextJSON.put("Entschlüsselt den Seed und erstellt die Liste"				,"<html>Decrypts the seed and creates the list of Bitcoin addresses<br>and the associated private keys.</html>");
 		englishTextJSON.put("Die Nummer des aktuell angezeigten Private-Keys mit Bitcoin Adresse.","The number of the currently displayed private key with Bitcoin address.");
 		englishTextJSON.put("CSV-Datei mit Bitcoin-Adressen exportieren"				,"Export CSV file with Bitcoin addresses.");
 		englishTextJSON.put("Seed wurde geladen"										,"Seed has been loaded");
 		
 		englishTextJSON.put("Unsignierte Transaktion per QR-Code scannen"				,"Scan unsigned transaction via QR code");
 		englishTextJSON.put("Unsignierte Transaktion aus dem Dateisystem öffnen"		,"<html>Open unsigned transactions from the file system.<br>Several can be opened at the same time.</html>");
 		englishTextJSON.put("Aktuell verwendeter Dateipfad"								,"Currently used file path");
 		englishTextJSON.put("Alle Transaktionen entfernen"								,"Remove all transactions");
 		englishTextJSON.put("Signaturmethode auswählen"									,"<html>Select signature method.<br>Signing can be done with a stored seed or with a list of private keys.</html>");
 		englishTextJSON.put("Automatisches Signieren"									,"<html>\"Automatic signing\" signs all transactions that are in a directory<br> and then saves all successfully signed transactions in the same directory.</html>");
 		englishTextJSON.put("Alle Signieren"											,"Signs all unsigned transactions that are in the input field");
 		englishTextJSON.put("Alle signierten Transaktionen Speichern"					,"<html>Saves all successfully signed transactions in the same directory<br>from which they were imported.</html>");
 		
 		englishTextJSON.put("Unsignierte Transaktion anzeigen"							,"Show unsigned transaction");
 		englishTextJSON.put("Signierte Transaktion als QR-Code anzeigen"				,"Show signed transaction as QR code");
 		englishTextJSON.put("Signierte Transaktion Speichern"							,"Save signed transaction");
 		englishTextJSON.put("Signierte Transaktion anzeigen"							,"Show signed transaction");
 		englishTextJSON.put("Fehlermeldung anzeigen"									,"Show error message");
 

 	} 	

	

 	
 	
 	
 	
 


 	private void setGermanJSON() throws JSONException
 	{ 		
 		germanTextJSON.put("File"							,"Datei");
 		germanTextJSON.put("Info"							,"Info");
 		germanTextJSON.put("open"							,"Schlüssel Datei Öffnen");
 		germanTextJSON.put("edit"							,"Schlüssel Datei Ändern");	
 		germanTextJSON.put("Date"							,"Datum");
 		germanTextJSON.put("print"							,"Ansicht Drucken");
		germanTextJSON.put("Save"							,"Speichern");
 		germanTextJSON.put("Delete"							,"Löschen");	
 		germanTextJSON.put("load Tx"						,"Tx Laden");
 		germanTextJSON.put("save Tx"						,"Alle Speichern");
 		germanTextJSON.put("QR code"						,"QR-Code");
 		germanTextJSON.put("show Tx"						,"Tx Anzeigen");
 		germanTextJSON.put("signing"						,"Signieren"); 				
 		germanTextJSON.put("New CVC"						,"Neue Prüfziffer");
 		germanTextJSON.put("Address"						,"Adresse");		
 		germanTextJSON.put("Passwort"						,"Passwort");
 		germanTextJSON.put("Count = "						,"Anzahl = ");
 		germanTextJSON.put("Settings"						,"Einstellungen");		
 		germanTextJSON.put("save priv"						,"Priv.Key Speichern");
 		germanTextJSON.put("save Seed"						,"Seed Speichern");
 		germanTextJSON.put("Open Seed"						,"Seed Öffnen");	
 		germanTextJSON.put("Prüfziffer"						,"Prüfziffer");
 		germanTextJSON.put("Sig method"						,"Sig. Methode");	
 		germanTextJSON.put("Was saved: "					,"Gespeichert wurde: ");
 		germanTextJSON.put("remove Seed"					,"Seed entfernen");
 		germanTextJSON.put("remove Coin"					,"Coin Entfernen");
 		germanTextJSON.put("Description"					,"Beschreibung");
 		germanTextJSON.put("Seed loaded"					,"Seed geladen");
 		germanTextJSON.put("Open PrivKey"					,"Private-Key Öffnen");	
 		germanTextJSON.put("scan QR code"					,"Scan QR");
 		germanTextJSON.put("load from Hexa"					,"Hex Eingabe");
 		germanTextJSON.put("Seed decipher"					,"Seed entschlüsseln"); 
 		germanTextJSON.put("Save completed"					,"Speichern abgeschlossen");
 		germanTextJSON.put("ToolTips enable"				,"Tool Tips aktivieren");
 		germanTextJSON.put("Address Format"					,"Adress-Format");
 		germanTextJSON.put("Seed-Extractor"					,"Seed-Extractor");
 		germanTextJSON.put("import new Coin"				,"Coin Importieren");
 		germanTextJSON.put("Input Text-Hash"				," Text Hash         ");
 		germanTextJSON.put("Sign Transaktion"				," Signieren         ");
 		germanTextJSON.put("show private keys"				,"Privat Schlüssel anzeigen/verdecken");
 		germanTextJSON.put("Input private key"				,"Privat Schlüssel");
 		germanTextJSON.put("automatic signing"				,"Automatisches Signieren");
 		germanTextJSON.put("Input rolling dice"				," Würfeln            ");
 		germanTextJSON.put("Create check digit"				,"Prüfziffer erstellen");
 		germanTextJSON.put("export BTC address"				,"BTC-Adressen Exportieren");
 		germanTextJSON.put("Check digit wrong!"				,"Prüfziffer falsch!");
 		germanTextJSON.put("Check digit missing!"			,"Prüfziffer fehlt!");
 		germanTextJSON.put("Storage location is: "			,"Speicherort ist: ");
 		germanTextJSON.put("SHA256 hash of a text"			,"Generiere einen privaten Schlüssel aus dem SHA256-Hash eines Textes.");
 		germanTextJSON.put("Only .txn files allowed!"		,"Nur .txn-Dateien erlaubt!");
 		germanTextJSON.put("Verschlüsselter Seed (Hexa)"	,"Verschlüsselter Seed (Hexa)");
 		germanTextJSON.put("No Seed or Private-Keys loaded!","Kein Seed oder Private-Keys geladen!");
 		germanTextJSON.put("Imput unsign Transaktion in Hexa"					,"Unsignierte Transaktion in Hexa eingeben.");		
 		germanTextJSON.put("Unable to save, no seed decrypted."					,"Speichern nicht möglich, kein Seed entschlüsselt.");
 		germanTextJSON.put("Signed transactions were saved successfully."		,"Signierte Transaktionen wurden erfolgreich gespeichert.");
 		germanTextJSON.put("Seed hat falsches Format oder ist nicht vorhanden"	,"Seed hat falsches Format oder ist nicht vorhanden");
 		germanTextJSON.put("Drag unsigned transactions (.txn) into this field!"	,"Unsignierte Transaktionen (.txn) per Drag and Drop in dieses Feld ziehen!");
 		germanTextJSON.put("Saving not possible, no valid address in the output field."					,"Speichern nicht möglich, keine gültige Adresse im Ausgabefeld.");
 		germanTextJSON.put("Note: The location from which Transactions were imported is used."			,"Hinweis:\nEs wird der Speicherort verwendet, aus dem Transaktionen importiert wurden.");
 		germanTextJSON.put("Error! No transaction was found that could be successfully sign."			,"Fehler!\nEs wurde keine Transaktion gefunden, die erfolgreich signiert werden konnte.");
 		germanTextJSON.put("Error input seed! \nEncrypted seed can only contain hexa characters!"		,"Fehler Eingabe Seed! \nVerschlüsselter Seed darf nur Hexa-Zeichen enthalten!");
 		germanTextJSON.put("Error input seed! \nEncrypted seed must be exactly 64 characters long!"		,"Fehler Eingabe Seed! \nDer verschlüsselte Seed muss genau 64 Zeichen lang sein!");
 		germanTextJSON.put("Niemals Seed eingeben auf Hardware mit möglicher Internetverbindung !!!"	,"Niemals Seed eingeben auf Hardware mit möglicher Internetverbindung !!!");
 		germanTextJSON.put("Zum Erstellen der Prüfziffer muss ein 64Hexa-Zeichen Seed eingegeben sein!"	,"Zum Erstellen der Prüfziffer muss ein 64Hexa-Zeichen Seed eingegeben sein!");
 		germanTextJSON.put("Enter a private key directly,\nthe format will be recognized automatically.","Eingabe eines privaten Schlüssels,\ndas Format wird automatisch erkannt.");
 		germanTextJSON.put("Generate private-key by rolling dice.\n100 characters, only digits between 1 and 6 are allowed!"					,"Generiere einen privaten Schlüssel durch Würfeln.\n100 Zeichen, nur Ziffern zwischen 1 und 6 sind erlaubt!");
 		germanTextJSON.put("No seed found!\nA seed must be stored in the “Seed Extractor”!\nOr the “Sig method” must be set to “Private Keys”!"	,"Kein Seed gefunden!\nEs muss ein Seed im \"Seed-Extractor\" hinterlegt sein!\nOder die \"Sig Methode\" muss auf \"Private Keys\" gestellt werden!");
 		germanTextJSON.put("Signure with a list of private keys that you can enter here.\r\nPrivate keys must be formatted in Hexa Decimal format!\r\nThe order of the private keys doesn't matter. All private keys required for signing must only be present!\r\nThe private keys must separated with a comma:\r\nExample:"		,"Signieren Sie mit einer Liste privater Schlüssel, die Sie hier eingeben können.\r\nPrivate Schlüssel müssen im Hexadezimalformat formatiert sein!\r\nDie Reihenfolge der privaten Schlüssel spielt keine Rolle. Alle zum Signieren erforderlichen privaten Schlüssel müssen nur vorhanden sein!\r\nDie privaten Schlüssel müssen durch ein Komma getrennt sein:\r\nBeispiel:");
 		germanTextJSON.put("Die 3-stellige Prüfziffer sollte sich bei dem Seed befinden.\nSie ist nützlich um Eingabefehler zu erkennen.\nDie Prüfziffer wird normalerweise nur beim erstmaligen Erstellen des Seeds genieriert und dann notiert.\nWird die Prüfziffer neu erstellt, kann nicht erkannt werden, ob der Seed Fehler enthällt.\nSoll die Prüfziffer aus dem Seed neu berechnet werden?"		,"Die 3-stellige Prüfziffer sollte sich bei dem Seed befinden.\nSie ist nützlich um Eingabefehler zu erkennen.\nDie Prüfziffer wird normalerweise nur beim erstmaligen Erstellen des Seeds genieriert und dann notiert.\nWird die Prüfziffer neu erstellt, kann nicht erkannt werden, ob der Seed Fehler enthällt.\nSoll die Prüfziffer aus dem Seed neu berechnet werden?");
 
 	 	// -------------------------------------------------------------------------- ToolTip Texte ------------------------------------------------------------------------------------------------------------------------------
 	 	
 		germanTextJSON.put("Datei Menü"													,"Datei Menü");
 		germanTextJSON.put("Sprache ändern"												,"Sprache ändern");
 		germanTextJSON.put("Crypto Währung ändern"										,"<html>Crypto Währung ändern<br>BTC-MainNet odre BTC-TestNet</html>");
 		germanTextJSON.put("Bitcoin Adress-Format ändern"								,"Bitcoin Adress-Format ändern");
 		germanTextJSON.put("Die 3 Stellige Prüfziffer Eingeben!"						,"Die 3 Stellige Prüfziffer Eingeben!");
 		germanTextJSON.put("Die Maximale Anzahl der Private-Keys die erzeugt werden."	,"Die Maximale Anzahl der Private-Keys die erzeugt werden.");
 		
 		germanTextJSON.put("Schlüsseldatei öffnen"										,"Öffnet die Schlüsseldatei deines Seed´s oder Private-Schlüssel´s.");
 		germanTextJSON.put("Schlüsseldatei ändern"										,"Schlüsseldatei deines Seed´s oder Private-Schlüssel´s bearbeiten.");
 		germanTextJSON.put("Private Key speichern"										,"Aktuell erstellten Private Schlüssel in der Schlüsseldatei speichern.");
 		germanTextJSON.put("Seed Speichern"												,"Aktuell erstellten Seed in der Schlüsseldatei speichern.");
 		germanTextJSON.put("Ansicht Drucken"											,"Aktuelle Ansicht des Programms als Screenshot drucken");
 		
 		germanTextJSON.put("Adress Format unCompressed"									,"<html>Bitcoin WIF Adress Format Legacy P2PKH<br>Base58 codiert, mit unkomprimierten Public-Key Hash</html>");
 		germanTextJSON.put("Adress Format compressed"									,"<html>Bitcoin WIF Adress Format Legacy P2PKH<br>Base58 codiert, mit   komprimierten Public-Key Hash</html>");
 		germanTextJSON.put("Adress Format p2sh"											,"<html>Bitcoin Adress Format SegWit P2SH     <br>Base58 codiert, Adresse begint mit 3</html>");
 		germanTextJSON.put("Adress Format bech32"										,"<html>Bitcoin Adress Format SegWit Bech32   <br>Base32 codiert, Adresse begint mit bc1</html>");
 		
 		germanTextJSON.put("Seed erstellen oder laden"									,"Seed erstellen oder laden");
 		germanTextJSON.put("Transaktion signieren"										,"Transaktion signieren");
 		
 		germanTextJSON.put("Private Key der markierten Zeile öffnen"					,"Private Key der markierten Zeile öffnen");
 		germanTextJSON.put("Private Key der markierten Zeile als Seed öffnen"			,"Private Key der markierten Zeile als Seed öffnen");
 		germanTextJSON.put("Markierte Zeile löschen"									,"Markierte Zeile löschen");
 		germanTextJSON.put("Alle Änderungen endgültig speichern"						,"Alle Änderungen endgültig speichern");
 		
 		germanTextJSON.put("Hinterlegten Seed aus dem Speicher löschen"					,"Hinterlegten Seed aus dem Speicher löschen");
 		germanTextJSON.put("Seed hier eingeben, 64 Hex Zeichen."						,"Seed hier eingeben, 64 Hex Zeichen.");
 		germanTextJSON.put("Passwort mit dem der Seed entschlüsselt wird"				,"Passwort mit dem der Seed entschlüsselt wird");
 		germanTextJSON.put("Neue Prüfziffer berechnen"									,"Neue Prüfziffer berechnen");
 		germanTextJSON.put("Entschlüsselt den Seed und erstellt die Liste"				,"<html>Entschlüsselt den Seed und erstellt die Liste der Bitcoin-Adressen<br>und der zugehörigen privaten Schlüssel.</html>");
 		germanTextJSON.put("Die Nummer des aktuell angezeigten Private-Keys mit Bitcoin Adresse.","Die Nummer des aktuell angezeigten Private-Keys mit Bitcoin Adresse.");
 		germanTextJSON.put("CSV-Datei mit Bitcoin-Adressen exportieren"					,"CSV-Datei mit Bitcoin-Adressen exportieren");
 		germanTextJSON.put("Seed wurde geladen"											,"Seed wurde geladen");
 		
 		germanTextJSON.put("Unsignierte Transaktion per QR-Code scannen"				,"Unsignierte Transaktion per QR-Code scannen");
 		germanTextJSON.put("Unsignierte Transaktion aus dem Dateisystem öffnen"			,"<html>Unsignierte Transaktionen aus dem Dateisystem öffnen.<br>Es können mehrere gleichzeitig geöffnet werden.</html>");
 		germanTextJSON.put("Aktuell verwendeter Dateipfad"								,"Aktuell verwendeter Dateipfad");
 		germanTextJSON.put("Alle Transaktionen entfernen"								,"Alle Transaktionen entfernen");
 		germanTextJSON.put("Signaturmethode auswählen"									,"<html>Signaturmethode auswählen.<br>Das Signieren kann mit einem gespeicherten Seed oder mit einer Liste privater Schlüssel erfolgen.</html>");
 		germanTextJSON.put("Automatisches Signieren"									,"<html>„Automatisches Signieren“ signiert alle Transaktionen, die sich in einem Verzeichnis befinden<br> und speichert anschließend alle erfolgreich signierten Transaktionen im selben Verzeichnis.</html>");
 		germanTextJSON.put("Alle Signieren"												,"Signiert alle unsignierten Transaktionen, die sich im Eingabefeld befinden.");
 		germanTextJSON.put("Alle signierten Transaktionen Speichern"					,"<html>Speichert alle erfolgreich signierten Transaktionen im selben Verzeichnis,<br>aus dem sie importiert wurden.</html>");
 		
 		germanTextJSON.put("Unsignierte Transaktion anzeigen"							,"Unsignierte Transaktion anzeigen");
 		germanTextJSON.put("Signierte Transaktion als QR-Code anzeigen"					,"Signierte Transaktion als QR-Code anzeigen");
 		germanTextJSON.put("Signierte Transaktion Speichern"							,"Signierte Transaktion Speichern");
 		germanTextJSON.put("Signierte Transaktion anzeigen"								,"Signierte Transaktion anzeigen");
 		germanTextJSON.put("Fehlermeldung anzeigen"										,"Fehlermeldung anzeigen");


 	}	
 	

 	
 	
 	
 	
 	
 	



 	// Stellt alle OptionPane,FileChooser etc. auf Deutsch.
    public void setGermanUI() 
    {
        UIManager.put("OptionPane.okButtonText"			, "OK");
        UIManager.put("OptionPane.cancelButtonText"		, "Abbrechen");
        UIManager.put("OptionPane.yesButtonText"		, "Ja");
        UIManager.put("OptionPane.noButtonText"			, "Nein");
        UIManager.put("OptionPane.messageDialogTitle"	, "Hinweis");
        UIManager.put("OptionPane.inputDialogTitle"		, "Eingabe");
        UIManager.put("FileChooser.openDialogTitleText"	, "Öffnen");
        UIManager.put("FileChooser.saveDialogTitleText"	, "Speichern");
        UIManager.put("FileChooser.openButtonText"		, "Öffnen");
        UIManager.put("FileChooser.saveButtonText"		, "Speichern");
        UIManager.put("FileChooser.cancelButtonText"	, "Abbrechen");
        UIManager.put("ProgressMonitor.progressText"	, "Fortschritt");    
    }
    
    
    
 	// Stellt alle OptionPane,FileChooser etc. auf English.
    public void setEnglishUI() 
    {
        UIManager.put("OptionPane.okButtonText"			, "OK");
        UIManager.put("OptionPane.cancelButtonText"		, "Cancel");
        UIManager.put("OptionPane.yesButtonText"		, "Yes");
        UIManager.put("OptionPane.noButtonText"			, "No");
        UIManager.put("OptionPane.messageDialogTitle"	, "Message");
        UIManager.put("OptionPane.inputDialogTitle"		, "Input"); 
        UIManager.put("FileChooser.openDialogTitleText"	, "Open");
        UIManager.put("FileChooser.saveButtonText"		, "Save");
        UIManager.put("FileChooser.openButtonText"		, "Open");
        UIManager.put("FileChooser.saveDialogTitleText"	, "Save");
        UIManager.put("FileChooser.cancelButtonText"	, "Cancel");
        UIManager.put("ProgressMonitor.progressText"	, "Progress");          
    }
}