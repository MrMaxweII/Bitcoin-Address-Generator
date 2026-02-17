package GUI;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import CoinGen.MyIcons;



/***************************************************************
 * Hier ist der Info-Text für dieses Programm enthalten.		*
 * Der Info-Text ist in HTML formatiert.						*
 ***************************************************************/



public class GUI_InfoText extends JDialog
{



	
	// Konstruktor
	// Lässt den JDialog mit dem InfoText erscheinen
	public GUI_InfoText()
	{
		this.setBounds(GUI.frame.getX()+40, GUI.frame.getY()+40, 1300, 800);
		this.setTitle(GUI.progName +"        Info");	
		if(((String)GUI.comboBox_coin.getSelectedItem()).equals("BTC"))	this.setIconImage(MyIcons.bitcoinLogoMain.getImage());
		else 																		this.setIconImage(MyIcons.bitcoinLogoTest.getImage());
		JScrollPane pnl = new JScrollPane();
		pnl.getVerticalScrollBar().setUnitIncrement(7);
		pnl.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JTextPane txt_info = new JTextPane();		// Programm Info-Text
		txt_info.setBorder(new EmptyBorder(10,10,10,10));
		txt_info.setBackground(GUI.color1);
		txt_info.setBackground(GUI.color1);
		txt_info.setEditable(false);
		txt_info.setContentType("text/html");	
	 	if(GUI.cBox_language.getSelectedIndex()==0) txt_info.setText(infoTextEN);
	 	if(GUI.cBox_language.getSelectedIndex()==1) txt_info.setText(infoTextDE);	
		txt_info.setCaretPosition(0); 	// Setzt den ScrollBalken nach oben
		pnl.setViewportView(txt_info);
		this.add(pnl);
	}
	
	
	
	
	
	
	
	
	// Info Text mit der Beschreibung des Programmes in Deutsch
	public String infoTextDE =   GUI.progName+"   Version: "+GUI.version+"\n"
	+ "<br>"
	+ "<br>"
	+ ""+
	"<h2>Sicherheitshinweis</h2>" +
	
	"Die Verwendung dieses Programms muss auf einem Offline-PC erfolgen!<br>" +
	"Wenn du Private-Keys oder einen Seed hier eingibst, darf die Hardware niemals in Verbindung mit dem Internet stehen oder in Zukunft kommen können!<br>" +
	"Achte auch auf mögliche W-Lan Verbindungen.<br>" +
	"Sorge dafür, dass alle beteiligten Datenträger zerstört oder ordentlich gelöscht werden!<br>" +
	"Alle Treiber für eine Internet- oder W-Lan Verbindung müssen deinstalliert sein!<br>" +
	"Schreibe den benötigten PrivKey am besten ab oder nutze USB-Sticks, die später zu löschen sind!<br><br>" +
	
	
	"<h2>Erstellung einer Bitcoin Adresse mit Private-Key</h2>" +
	
	"Es gibt drei verschiedene Varianten:<br>" +
	"1. Die Erstellung durch einen Text-Hash, im ersten Tab: \"Text Hash\".<br>" +
	"&nbsp &nbsp Dort kannst du einen beliebigen Text eingeben. Durch den Hash SHA256 des gesamten Textes, wird im Anschluss ein Bitcoin Private-Key mit Bitcoin Adresse gebildet.<br>" +
	"2. Die Erstellung durch Würfeln im 2. Tab.<br>" +
	"&nbsp &nbsp Du musst dort 100 mal würfeln und die Zahlen von 1-6 dort eingeben. Daraus wird dann ein Bitcoin Private-Key mit Bitcoin Adresse gebildet.<br>" +
	"3. Den Bitcoin Private-Key kannst du im 3. Tab direkt eingeben.<br>" +
	"&nbsp &nbsp Unterstützte Formate sind: Base58 WIF, Base64 und Hexa.<br><br>" +
	
	
	"<h2>Erstellung eines Seed´s</h2>" +
	
	"Die Erstellung des Seeds funktioniert prinzipiell gleich wie die Erstellung eines Private-Key´s. Denn jeder erstellte Private-Key kann im Hexa Format prinzipiell auch immer als Seed verwendet werden. <br>"
	+ "Im weiteren Verlauf dieser Anleitung wird daher nun der Begriff „Schlüssel“ definiert, der entweder ein Private-Key oder ein Seed sein kann.<br>" +
	"Zur Erstellung eines Seed wird also ein Private-Key, am besten durch Würfeln erzeugt. Zum Anzeigen des Private-Keys muss vorher im Menü „Einstellungen“, „Private-Schlüssel anzeigen/verdecken“ aktiviert werden.<br>"
	+ "Der angezeigte Private-Key wird zur Verwendung als Seed nun im Hex-Format in den Tab: „Seed-Extractor“ kopiert.<br>"
	+ "Anschließend wird mit dem Button „Neue Prüfziffer“ eine 3-Stellige Prüfziffer erstellt.<br>"
	+ "Nun muss im Passwort-Feld ein Passwort eingegeben werden, mit dem der Seed entschlüsselt wird. Beachte, dass jedes Passwort eine Liste von gültigen Private-Keys mit Bitcoin Adressen erstellt.<br>" 
	+ "Es gibt hier keinen Passwort-Schutz oder Passwort-Falsch Funktion. Wird bei der späteren Verwendung ein anderes Passwort eingegeben, als bei der Erstellung des Seed´s,<br>"
	+ "wird eine Liste mit vollständig anderen Private-Key´s erzeugt, wodurch der Zugriff auf die vorherigen nicht gegeben ist.<br>" 
	+ "Nach Eingabe des Passwortes muss nun der Seed mit dem Button „Seed entschlüsseln“ entschlüsselt werden, wobei eine Liste mit Private-Keys mit Bitcoin Adressen entsteht.<br>" 
	+ "In diesem Programm wird der Seed immer als 64 Hexa Dezimal-Zeichen codiert. Der hier verwendete Seed hat eine eigene Codierung, die nicht kompatibel mit Seed´s aus anderen Anwendungen ist!<br>"
	+ "Der Seed ist standardmäßig immer Verschlüsselt, die unverschlüsselte Version des Seedes kann nicht angezeigt oder exportiert werden.<br>"
	+ "Selbst wenn kein Passwort für den Seed eingegeben wird, ist der Seed dann mit einem „leerem“ Passwort verschlüsselt.<br>"
	+ "Es gibt in dieser Software keine Passwort Wiederherstellung Funktionen oder ähnliches.<br><br>" +
	
	
	"<h2>Seed-Extractor</h2>" +
	
	"Der Seed-Extractor entschlüsselt einen Seed mit einem Passwort und erstellt beliebig viele Private Keys mit Bitcoin Adressen aus diesem Seed.<br>"
	+ "Es müssen der verschlüsselte Seed und das dazugehörige Passwort eingegeben werden. Die 3 Stellige Prüfziffer sollte auf dem Seed-Medium enthalten sein.<br>"
	+ "Es handelt sich um die letzten 3 Stellen des SHA-256 Hash, des verschlüsselten Seed. Die Prüfziffer soll Fehler beim Eintippen des Seed´s verhindern.<br>" 
	+ "Nun wird der Seed mit dem Button „Seed entschlüsseln“ entschlüsselt, wobei eine Liste mit Private-Keys und Bitcoin Adressen entsteht.<br>" 
	+ "In dem Feld „Key.Nr.“ kann die Nummer des jeweiligen Private-Keys aus der Liste direkt abgerufen werden. Oder es kann mit den zwei kleinen Pfeilen an dem Feld die Liste durchlaufen werden.<br>"
	+ "Die Größe der Liste wird mit dem Feld „Max Key´s“ bestimmt. Es würden also 100 Bitcoin Adressen erstellt werden, wenn dort 100 eingetragen wird. Die Größe der Liste kann später beliebig oft angepasst werden.<br>"
	+ "Umso größer die Liste, umso länger dauert der Signaturvorgang später.<br>" 
	+ "Mit dem Button „BTC-Adressen Exportieren“ werden ausschließlich Bitcoin Adressen ohne die Private-Keys exportiert. Diese Liste wird unverschlüsselt als CSV Format erstellt.<br>"
	+ "Diese Liste kann anschließend im TxBuilder importiert und dort auch verschlüsselt werden. Da diese Liste keine Private-Key´s enthält, kann sie nur zur Erstellung von unsignierten Transaktionen verwendet werden.<br>"
	+ "Keinesfalls kann ein Zugriff auf die Coins in der Liste ohne die zugehörigen Private-Keys erfolgen. Es stellt daher kein Sicherheitsrisiko dar, diese Liste auf einen online-PC zu übertragen.<br>"
	+ "Es besteht lediglich das Risiko des Bekanntwerdens der verwendeten Bitcoin Adressen.<br><br>" +
	
	
	"<h2>Speichern oder Öffnen eines Schlüssels in einer gesicherten Datei</h2>"
	
	+ "Im Menü „Datei“ wird die Schlüsseldatenbank verwaltet. Dort können deine Schlüssel geöffnet, geändert oder neu gespeichert werden.<br>"
	+ "Deine Schlüssel werden mit einem Passwort verschlüsselt, welches du selbst wählst.<br>" 
	+ "Beachte, dass du einen Schlüssel entweder als Seed oder als Private-Key speichern oder öffnen kannst.<br>"
	+ "Das Menü „Priv.Key Speichern“, speichert immer den Private-Key mit der zugehörigen Bitcoin Adresse, so wie er im unteren Ausgabefeld neben dem QR-Code steht.<br>" 
	+ "Das Menü „Seed Speichern“, speichert hingegen den Verschlüsselten Seed, so wie er im Seed-Extraktor im dortigem Eingabefeld steht.<br><br>" +
	
	
	"<h2>Bitcoin Adress-Formate</h2>" 
	
	+ "Es gibt 4 verschiedene Bitcoin Adress-Formate die implementiert sind:<br>"
	+ "- WIF uncompressed<br>"
	+ "- WIF compressed<br>"
	+ "- P2SH<br>"
	+ "- Bech32<br>"
	+ "Die Beschreibung dieser Bitcoin Adress-Formate ist Teil der Bitcoin Protokoll Dokumentation und wird hier nicht näher erläutert.<br>"
	+ "Im Menü „Adress-Format“ kann das gewünschte Bitcoin Adress-Format eingestellt und jederzeit geändert werden.<br><br>" +
	
	
	"<h2>Signieren</h2>"
	
	+ "Im 5. Tab „Signieren“ können unsignierte Transaktionen signiert werden. Folgende Bedingungen müssen dafür erfüllt sein:<br>" 
	+ "1. Unsignierte Transaktionen müssen korrekt vom TxBuilder erstellt worden sein. Unsignierte Transaktionen die von einer anderen Software erstellt wurden, werden wahrscheinlich nicht erkannt.<br>" 
	+ "2. Jede unsignierte Transaktion muss prinzipiell gültig sein, was die Protokoll-spezifischen Anforderungen betrifft.<br>" 
	+ "3. Alle benötigten Private-Keys oder der Seed müssen bekannt und im Programm geladen sein.<br><br>"
	+ "" 
	+ "Es sind 4 Varianten implementiert, die unsignierte Transaktion in das Programm zu übertragen:<br>" 
	+ "1. Per Drag and Drop, durch Ziehen einer oder mehrerer Transaktionen mit der Maus in das Eingabefenster.<br>" 
	+ "2. Mit dem Button „Scan QR“ öffnet sich ein Video-Fenster, mit dem ein QR-Code der Transaktion mit einer Kamera gescannt werden kann. (sicherste Methode)<br>" 
	+ "3. Die Transaktion kann in Hexa in ein Eingabefeld kopiert werden.<br>" 
	+ "4. Eine oder mehrere Transaktionen können mit dem Button „Tx Laden“ durch einen Explorer als (.txn) Datei importiert werden.<br>" 
	+ "Es können beliebig viele Transaktionen auf einmal oder nacheinander importiert und später signiert werden.<br>" 
	+ "Sobald die unsignierten Transaktionen importiert sind, können sie mit einem Mausklick noch einmal angesehen werden.<br><br>"
	+ ""
	+ "Es gibt 2 Signier Methoden die unten links ausgewählt werden können.<br>"
	+ "1. Seed: Hier muss der gültige Seed vorher im Seed-Extractor korrekt entschlüsselt worden sein.<br>"
	+ "2. Private-Keys: Beim Starten des signier Vorganges öffnet sich ein Eingabefenster in das die benötigen Private-Keys eingetragen werden müssen.<br><br>"
	+ ""
	+ "Das „Automatische Signieren“ signiert alle Transaktionen sofort hintereinander und speichert sie direkt im selben Verzeichnis ab, aus dem sie importiert wurden.<br>"
	+ "Wurden sie als QR-Code gescannt oder im Hex-Format eingegeben, dann wird der zuletzt verwendet Dateipfad verwendet. Der verwendete Dateipfad wird oben neben dem Button „Tx Laden“ angezeigt.<br>"
	+ "Durch dem Button „Signieren“ werden alle Transaktionen im Feld hintereinander signiert, aber noch nicht gespeichert.<br>"
	+ "Falls es zu Fehlern kommt, kann mit einem Mausklick auf die fehlerhafte Transaktion der Fehler gelesen werden.<br>"
	+ "Korrekt signierte Transaktionen werden mit der grünem Transaktions-ID bestätigt.<br>"
	+ "Die signierten Transaktionen können zunächst durch einen Mausklick nochmal angesehen und geprüft werden.<br>"
	+ "Die signierten Transaktion können nun einzeln oder zusammen abgespeichert werden. Oder als QR-Code angezeigt werden.<br>"
	+ "Der Dateiname jeder signierten Transaktion ist immer die 64 Zeichen lange Tx-ID.txn<br><br>" +

	
	
	"<h2>Signierte Transaktionen übertragen und Senden</h2>"
	
	+ "Die signierten Transaktionen können per USB-Stick oder durch einen QR-Code Scann auf den Online-PC übertragen werden und in den TxBuilder importiert werden,<br>"
	+ "wo sie schließlich gesendet werden können.<br>"
	+ "";

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Info Text mit der Beschreibung des Programmes in English
	public String infoTextEN = GUI.progName+"   Version: "+GUI.version+"\n"
	+ "<br>"
	+ "<br>"+
	
	
	"<h2>Safety notice</h2>" + 

	"Use of this program must be done on an offline PC!<br>" + 
	"If you enter private keys or a seed here, the hardware must never be connected to the Internet or be able to do so in the future!<br>" + 
	"Also pay attention to possible WiFi connections.<br>" + 
	"Ensure that all data storage media involved are destroyed or properly deleted!<br>" + 
	"All drivers for an Internet or WiFi connection must be uninstalled!<br>" + 
	"It's best to write down the PrivKey you need or use USB sticks that can be deleted later!<br><br>" + 


	"<h2>Creation of a Bitcoin address with private key</h2>" + 

	"There are three different variants:<br>" + 
	"1. The creation by a text hash, in the first tab: \"Text Hash\".<br>" + 
	"&nbsp &nbsp You can enter any text there. The SHA256 hash of the entire text will then create a Bitcoin private key with a Bitcoin address.<br>" + 
	"2. The creation by rolling dice in the 2nd tab.<br>" + 
	"&nbsp &nbsp You have to roll the dice 100 times and enter the numbers from 1-6 there. This will then be used to create a Bitcoin private key with a Bitcoin address.<br>" + 
	"3. You can enter the Bitcoin private key directly in the 3rd tab.<br>" + 
	"&nbsp &nbsp Supported formats are: Base58 WIF, Base64 and Hexa.<br><br>" + 


	"<h2>Creation of a seed</h2>" + 

	"The creation of the seed works in principle in the same way as the creation of a private key. Because every private key created can in principle always be used as a seed in hexa format. <br>" 
	+ "In the further course of these instructions, the term key will now be defined, which can be either a private key or a seed.<br>"
	+ "To create a seed, a private key is generated, ideally by rolling the dice. To display the private key, “Show/hide private key” must first be activated in the “Settings” menu.<br>" 
	+ "The displayed private key is now copied in hex format into the tab: “Seed Extractor” for use as a seed.<br>" 
	+ "Then a 3-digit check digit is created using the \"New check digit\" button.<br>" 
	+ "Now a password must be entered in the password field to decrypt the seed. Note that each password creates a list of valid private keys with Bitcoin addresses.<br>" 
	+ "There is no password protection or incorrect password function here. If a different password is entered for later use than when the seed was created,<br>" 
	+ "a list with completely different private keys is created, which means that access to the previous ones is not possible.<br>" 
	+ "After entering the password, the seed must now be decrypted using the “Seed decipher” button, which creates a list of private keys with Bitcoin addresses.<br>" 
	+ "In this program the seed is always encoded as 64 hexa decimal characters. The seed used here has its own coding that is not compatible with seeds from other applications!<br>" 
	+ "The seed is always encrypted by default, the unencrypted version of the seed cannot be viewed or exported.<br>"
	+ "Even if no password is entered for the seed, the seed is then encrypted with an “empty” password.<br>"
	+ "There are no password recovery functions or anything similar in this software.<br><br>" + 


	"<h2>Seed Extractor</h2>" + 

	"The seed extractor decrypts a seed with a password and creates any number of private keys with Bitcoin addresses from this seed.<br>" 
	+ "The encrypted seed and the associated password must be entered. The 3-digit check digit should be included on the seed medium.<br>" 
	+ "These are the last 3 digits of the SHA-256 hash, the encrypted seed. The check digit is intended to prevent errors when typing the seed.<br>" 
	+ "Now the seed is decrypted using the “Seed decipher” button, creating a list of private keys and Bitcoin addresses.<br>" 
	+ "In the field \"Key.No.\" The number of the respective private key can be retrieved directly from the list. Or you can scroll through the list using the two small arrows on the field.<br>" 
	+ "The size of the list is determined using the “Max Keys” field. So 100 Bitcoin addresses would be created if 100 was entered there. The size of the list can be adjusted later as often as you like.<br>" 
	+ "The larger the list, the longer the signature process will take later.<br>" 
	+ "With the \"Export BTC addresses\" button, only Bitcoin addresses without the private keys are exported. This list is created unencrypted in CSV format.<br>" 
	+ "This list can then be imported into TxBuilder and encrypted there. Since this list does not contain any private keys, it can only be used to create unsigned transactions.<br>" 
	+ "Under no circumstances can the coins in the list be accessed without the associated private keys. It therefore poses no security risk to transfer this list to an online PC.<br>" 
	+ "There is only the risk of the Bitcoin addresses used becoming known.<br><br>" + 
	
	
	"<h2>Saving or opening a key in a secured file</h2>" 
	
	+ "The key database is managed in the “File” menu. There your keys can be opened, changed or saved again.<br>" 
	+ "Your keys will be encrypted with a password that you choose yourself.<br>" 
	+ "Note that you can save or open a key as either a seed or a private key.<br>" 
	+ "The \"Save Priv.Key\" menu always saves the private key with the associated Bitcoin address, as it appears in the lower output field next to the QR code.<br>" 
	+ "The \"Save Seed\" menu, however, saves the encrypted seed as it appears in the input field in the seed extractor.<br><br>" + 
	
	
	"<h2>Bitcoin address formats</h2>" 
	
	+ "There are 4 different Bitcoin address formats that are implemented:<br>" 
	+ "- WIF uncompressed<br>" 
	+ "- WIF compressed<br>" 
	+ "- P2SH<br>" 
	+ "-Bech32<br>" 
	+ "The description of these Bitcoin address formats is part of the Bitcoin protocol documentation and is not explained in more detail here.<br>" 
	+ "In the “Address Format” menu, the desired Bitcoin address format can be set and changed at any time.<br><br>" + 
	
	
	"<h2>Sign</h2>"
	
	+ "In the 5th tab \"Sign\" unsigned transactions can be signed. The following conditions must be met:<br>" 
	+ "1. Unsigned transactions must have been correctly created by TxBuilder. Unsigned transactions created by other software are unlikely to be recognized.<br>" 
	+ "2. Every unsigned transaction must in principle be valid as far as the protocol-specific requirements are concerned.<br>" 
	+ "3. All required private keys or the seed must be known and loaded in the program.<br><br>" 
	+ "" 
	+ "There are 4 variants implemented to transfer the unsigned transaction into the program:<br>" 
	+ "1. By drag and drop, by dragging one or more transactions with the mouse into the input window.<br>" 
	+ "2. The “Scan QR” button opens a video window with which a QR code from the transaction can be scanned with a camera. (safest method)<br>" 
	+ "3. The transaction can be copied into an input field in hexa.<br>" 
	+ "4. One or more transactions can be imported as a (.txn) file through an explorer using the “Load Tx” button.<br>" 
	+ "Any number of transactions can be imported at once or one after the other and signed later.<br>" 
	+ "Once the unsigned transactions are imported, they can be viewed again with a mouse click.<br><br>" 
	+ "" 
	+ "There are 2 signing methods that can be selected at the bottom left.<br>" 
	+ "1. Seed: Here the valid seed must have been correctly decrypted in the seed extractor beforehand.<br>" 
	+ "2. Private keys: When you start the signing process, an input window opens in which the required private keys must be entered.<br><br>" 
	+ "" 
	+ "\"Automatic signing\" signs all transactions immediately one after the other and saves them directly in the same directory from which they were imported.<br>" 
	+ "If they were scanned as a QR code or entered in hex format, then the last used file path will be used. The file path used is displayed at the top next to the “Load Tx” button.<br>" 
	+ "Using the “Sign” button, all transactions in the field are signed one after the other, but not yet saved.<br>" 
	+ "If errors occur, the error can be read by clicking on the incorrect transaction.<br>" 
	+ "Correctly signed transactions are confirmed with the green transaction ID.<br>" 
	+ "The signed transactions can first be viewed and checked again with a mouse click.<br>" 
	+ "The signed transactions can now be saved individually or together. Or displayed as a QR code.<br>" 
	+ "The file name of each signed transaction is always the 64-character Tx-ID.txn<br><br>" + 


	"<h2>Transfer and send signed transactions</h2>" 

	+ "The signed transactions can be transferred to the online PC via USB stick or by scanning a QR code and imported into the TxBuilder,<br>" 
	+ "where they can finally be sent.<br>" 
	+ "";
			

	
	
	
	
	
	
	
	
	
	
	// Zeigt einen Begrüßungs Infotext mit Online-Warungen an!
	// Wird nur beim ersten start des Programmes angezeigt.
	public static void showFirstInfo()
	{
		// English
		if(GUI.cBox_language.getSelectedIndex()==0)
		{
			String str = "Attention!\n"
					+ "The use of this program must be done on an offline PC!\n"
					+ "If you enter private keys or your seed here,\n"
					+ "the hardware must never be connected to the Internet or be able to come in the future!\n"
					+ "Also pay attention to possible Wi-Fi connections.\n"
					+ "Make sure that all data carriers involved are destroyed or properly deleted!\n"
					+ "All drivers for an Internet or Wi-Fi connection must be uninstalled!\n"
					+ "It is best to write off the required PrivKey or use USB sticks that have to be deleted later!";
			
			JOptionPane.showMessageDialog(GUI.frame, str, "Attention", JOptionPane.WARNING_MESSAGE);
		}
		else // Deutsch
		{
			String str = "Achtung!\n"
					+ "Die Verwendung dieses Programmes muss auf einem Offline-PC erfolgen!\n"
					+ "Wenn du Private-Keys oder deinen Seed hier eingibst, \n"
					+ "darf die Hardware niemals in Verbindung mit dem Internet stehen oder in Zukunft kommen können!\n"
					+ "Achte auch auf mögliche W-Lan-Verbindungen.\n"
					+ "Sorge dafür, dass alle beteiligten Datenträger zerstört oder ordentlich gelöscht werden!\n"
					+ "Alle Treiber für eine Internet- oder W-Lan-Verbindung müssen deinstalliert sein! \n"
					+ "Schreibe den benötigten PrivKey am besten ab oder nutze USB-Sticks, die später zu löschen sind!\n";		
			JOptionPane.showMessageDialog(GUI.frame, str, "Warnung", JOptionPane.WARNING_MESSAGE);

		}
	}
	
}