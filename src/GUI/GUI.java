package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.MaskFormatter;
import CoinGen.Action;
import CoinGen.Config;
import CoinGen.MyIcons;
import CoinGen.Print;
import CoinGen.Translate;
import lib3001.btc.Bech32Address;
import lib3001.btc.BitcoinAddr;
import lib3001.crypt.Calc;
import lib3001.crypt.Convert;
import lib3001.java.Hover;
import lib3001.java.MyFont;
import lib3001.network.CoinParameter;
import seedExtractor.SeedExtractor;



public class GUI extends JFrame 
{

	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{	
				//	UIManager.setLookAndFeel(new FlatLightLaf());										// FlatLaf Design, wenn gewünscht. (Lib flatlaf.jar benötigt)
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();			// Font Ubuntu Mono wird intalliert
					ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, MyFont.get_UbuntuMono()));		// Font Ubuntu Mono wird geladen
					ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, MyFont.get_DejaVuSans()));		// Font DejaVu Sans wird geladen
					frame = new GUI();
					comboBox_coin.setSelectedIndex(0);
					Config.load();
					setAllTextInGUI();
					frame.setVisible(true);
				}
				catch (Exception e) {e.printStackTrace();}
			}
		});
	}
	
	
	

	
	public static final String			progName		= "Coin Address Generator";
	public static final String			version 		= "V3.6.0";
	public static final String			autor 			= "Mr. Maxwell";
	public static final String			eMail			= "Maxwell-KSP@gmx.de";
	public static final String			myBitcoinAddr 	= "12zeCvN7zbAi3JDQhC8tU3DBm35kDEUNiB";	
	public static GUI 					frame;												// Frame des Hauptprogrammes
	public static GUI_Wallet 			guiW;												// Frame der Wallet
	public static GUI_SignTx			guiSigTx		= new GUI_SignTx();					// Das JPanel SigTx, hier als GUI bezeichnet weil es so implementiert ist.
	public static JTabbedPane 			tabbedPane 		= new JTabbedPane(JTabbedPane.LEFT);// Linkes Tap-Feld mit der Auswahl der Registerkarten
	public static JPanel 				panel_QRCode 	= new JPanel();						// Das Panel für die QR-Codes
	public static JMenu 				mn_file 		= new JMenu("File");				// File Menü
	public static JMenu 				mn_addrForm		= new JMenu("Address Format");		// Adress Format wählen Butten
	public static JMenu 				mn_settings 	= new JMenu("Settings");			// Settings Menü
	public static JMenu 				mn_Info 		= new JMenu("Info");				// Info Menü
	public static JMenuItem 			mntm_Info 		= new JMenuItem("Info");			// Info Menü Icon
	public static JMenuItem 			mntm_open 		= new JMenuItem("open");			// Menü Open
	public static JMenuItem 			mntm_savePK 	= new JMenuItem("save priv");		// Menü Save Priv.Key
	public static JMenuItem 			mntm_saveSeed	= new JMenuItem("save Seed");		// Menü Seed speichern
	public static JMenuItem 			mntm_edit 		= new JMenuItem("edit");			// Menü Edit
	public static JMenuItem 			mntm_print 		= new JMenuItem("print");			// Menü Print (Auf Papier Drucken)
	public static JMenuItem 			mItem_newCoin 	= new JMenuItem("import new Coin");	// Importiert einen neuen Coin. 				(Ist Default disabled)
	public static JMenuItem 			mItem_removeCoin= new JMenuItem("remove Coin");		// Deaktiviert wieder einen Coin aus der Liste. (Ist Default disabled)
	public static JRadioButtonMenuItem	toolTipEnabled	= new JRadioButtonMenuItem("ToolTip Enable");// Deaktiviert die ToolTips
	public static JRadioButtonMenuItem 	compressed 		= new JRadioButtonMenuItem("WIF compressed");
	public static JRadioButtonMenuItem 	unCompressed	= new JRadioButtonMenuItem("WIF uncompressed");
	public static JRadioButtonMenuItem 	p2sh 			= new JRadioButtonMenuItem("P2SH");
	public static JRadioButtonMenuItem 	bech32 			= new JRadioButtonMenuItem("Bech32");
	public static JRadioButtonMenuItem	showPrivKeys	= new JRadioButtonMenuItem("show private keys");  // Zeigt die Private-Keys im Programm an oder verdeckt sie
	public static JComboBox 			comboBox_coin 	= new JComboBox();					// Auswahlfeld der Coins
	public static JComboBox 			cBox_language	= new JComboBox();					// Auswahlmenü der verwendeten Sprache
	public static JLabel 				lbl_Achtung 	= new JLabel("Niemals Seed eingeben auf Hardware mit möglicher Internetverbindung !!!");
	public static JLabel				lbl_seed		= new JLabel(MyIcons.seedBigRed);	// Das große rote Bild des Seed´s
	public static JLabel				lbl_seedSmal	= new JLabel(MyIcons.seed);			// Das kleine rote Bild des Seed´s
	public static JFormattedTextField 	txt_wuerfel;										// Eingabefeld der Würfelzahlen
	public static JFormattedTextField	txt_privKey;										// Eingabefeld des PrivKey (Hexa oder WIF)
	public static JTextArea 			txt_inText 		= new JTextArea();					// Eingabefeld des für den Priv-Key in TextForm. (Wird gehasht)
	public static JTextPane 			lbl_1textHash 	= new JTextPane();					// Beschreibung in Tap 1 Text-Hash
	public static JTextArea 			lbl_2wuerfel 	= new JTextArea();					// Beschreibung in Tap 2 Würfel
	public static JTextPane 			lbl_3privKey 	= new JTextPane();					// Beschreibung in Tap 3 Private-Key
	public static JEditorPane 			txt_ausgabe 	= new JEditorPane("text/html","");	// Das gesamte Ausgabefeld, für Meldungen, etc. oder der Coin-Daten + QR-Codes
	public static JTextField 			txt_seed		= new JTextField(); 				// SeedExtractor Eingabe verschlüsselter Seed
	public static JTextField			txt_cvc 		= new JTextField();					// SeedExtractor Eingabe Prüfziffer
	public static JTextField 			txt_max 		= new JTextField();					// SeedExtractor Maximale Anzahl der Private-Keys bei der Ausgabe
	public static JPasswordField 		txt_pw			= new JPasswordField();				// SeedExtractor Eingabe Passwort
	public static JSpinner 				txt_nr			= new JSpinner();					// SeedExtractor Die aktuelle Nummer des Priv-Keys	
	public static JButton 				btn_seedUnlock 	= new JButton(MyIcons.unlock_red);	// SeedExtractor Go-button der die die entschlüsselung startet
	public static JButton				btn_newCVC		= new JButton();					// Generiert eine neue Prüfziffer.
	public static JButton 				btn_Ok 			= new JButton("OK");
	public static JButton 				btn_exportAdr 	= new JButton("export BTC address");		
	public static JButton				btn_removeSeed	= new JButton(MyIcons.delete);		// Damit kann der eingegebene Seed wieder gelöscht werden, da das eingabe Feld disabled ist.
	public static JProgressBar 			progressBar 	= new JProgressBar();				// SeedExtractor Warte-Balken
	public static Color 				color1 			= new Color(255,244,230);			// Hintergrundfarbe des Programmes
	public static Color					color2			= new Color(255,244,230);			// Hinergrundfarbe des nicht aktiven Tabs. (Etwas dunkler als der Hintergrund des Programmes)
	public static Color 				color4 			= new Color(247,147,26); 			// Farbe Linien (Offizielle BTC-Farbe)
	public static Translate 			t				= new Translate(); 					// Übersetzungstexte werden geladen
	public static int 					posX			= 50;								// X-Position des Programmes
	public static int					posY			= 50;								// Y-Position des Programmes
	public static int	  				toolTipSetDismissDelay = 10000;						// Alle ToolTips werden nach dieser Zeit ausgeblendet. Kann in der Config angepasst werden.
	public static Font 					font1 			= new Font("DejaVu Sans", Font.PLAIN, 12); 			// Font für Label-Beschriftungen
	public static Font 					font2 			= new Font("DejaVu Sans", Font.PLAIN, 11); 			// Font für Rahmenbeschriftung
	public static Font 					font3 			= new Font("DejaVu Sans", Font.PLAIN, 10); 			// Font für Rahmenbeschriftung der Prüfziffer
	public static Font					font4			= new Font("Ubuntu Mono",Font.PLAIN,14); 			// Font für Textfelder (Ubuntu Font, selbst geladen)
	
	
	
	
	// Alle Texte der GUI müssen hier plaztiert werden. Damit die Sprachumschalung funktioeniert.
	// Diese Methode wird aufgerufen nachdem die GUI und die Config geladen wurde, oder wenn die Sprache geändert wird.
	private static void setAllTextInGUI()
	{	
		ToolTipManager.sharedInstance().setDismissDelay(toolTipSetDismissDelay);  	// Tooltip bleibt 20s sichtbar
		ToolTipManager.sharedInstance().setEnabled(toolTipEnabled.isSelected());	// Alle Tooltips können AUS oder Eingeschaltet werden
		txt_nr.setModel(new SpinnerNumberModel(1, 1, Integer.parseInt(txt_max.getText()), 1)); // Muss hier ausgeführt werden, damit das Feld richtig aktualisiert!
		if(cBox_language		.getSelectedIndex()==1) t.setGermanUI(); 
	 	if(cBox_language		.getSelectedIndex()==0) t.setEnglishUI();  
		
	 	btn_seedUnlock			.setText(t.t("Seed decipher"));
	 	btn_newCVC				.setText(t.t("New CVC"));	
	 	mn_file					.setText(t.t("File"));
	 	mn_addrForm				.setText(t.t("Address Format"));
	 	mn_settings				.setText(t.t("Settings"));
	 	mn_Info					.setText(t.t("Info"));
	 	mntm_open				.setText(t.t("open"));
	 	mntm_savePK				.setText(t.t("save priv"));
	 	mntm_saveSeed			.setText(t.t("save Seed"));
	 	mntm_edit				.setText(t.t("edit"));
	 	mntm_print				.setText(t.t("print"));
	 	mItem_newCoin			.setText(t.t("import new Coin"));
	 	mItem_removeCoin		.setText(t.t("remove Coin"));
	 	showPrivKeys			.setText(t.t("show private keys"));
	 	toolTipEnabled			.setText(t.t("ToolTips enable"));
	 	lbl_Achtung				.setText(t.t("Niemals Seed eingeben auf Hardware mit möglicher Internetverbindung !!!"));
	 	btn_exportAdr			.setText(t.t("export BTC address")); 	
		lbl_1textHash			.setText(t.t("SHA256 hash of a text"));
		lbl_2wuerfel			.setText(t.t("Generate private-key by rolling dice.\n100 characters, only digits between 1 and 6 are allowed!"));
		lbl_3privKey			.setText(t.t("Enter a private key directly,\nthe format will be recognized automatically."));
	 	lbl_seed				.setText(t.t("Seed loaded"));
		
	 	GUI_SignTx.dragText 	= t.t("Drag unsigned transactions (.txn) into this field!");
	 	GUI_SignTx.btn_scanQR	.setText(t.t("scan QR code"));
	 	GUI_SignTx.btn_fromHex	.setText(t.t("load from Hexa"));
	 	GUI_SignTx.btn_open_uTx	.setText(t.t("load Tx"));
	 	GUI_SignTx.btn_saveAllSigTx	.setText(t.t("save Tx"));
	 	GUI_SignTx.btn_sig		.setText(t.t("signing"));
	 	GUI_SignTx.btn_autoSig	.setText(t.t("automatic signing"));

	 	GUI_SignTx.cBox_sigMethode	.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1),t.t("Sig method"),	TitledBorder.LEADING, TitledBorder.TOP, GUI.font2, Color.GRAY));
		txt_pw						.setBorder(new TitledBorder(null, t.t("Passwort"), 						TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
		txt_seed					.setBorder(new TitledBorder(null, t.t("Verschlüsselter Seed (Hexa)"),	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
		txt_cvc						.setBorder(new TitledBorder(null, t.t("Prüfziffer"), 					TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
	 	
		tabbedPane.setTitleAt(0, t.t("Input Text-Hash"));
		tabbedPane.setTitleAt(1, t.t("Input rolling dice"));
		tabbedPane.setTitleAt(2, t.t("Input private key"));
		tabbedPane.setTitleAt(3, t.t("Seed-Extractor"));
		tabbedPane.setTitleAt(4, t.t("Sign Transaktion"));

		
		
	// -------------------------------------------- ToolTips -----------------------------------------------------------------------
		
		mn_file			.setToolTipText(t.t("Datei Menü"));
		comboBox_coin	.setToolTipText(t.t("Crypto Währung ändern"));
	 	mn_addrForm		.setToolTipText(t.t("Bitcoin Adress-Format ändern"));
	 	mn_settings		.setToolTipText(t.t("Settings"));
		cBox_language	.setToolTipText(t.t("Sprache ändern"));
	 	mn_Info			.setToolTipText(t.t("Info"));

	 	mntm_open		.setToolTipText(t.t("Schlüsseldatei öffnen"));
	 	mntm_edit		.setToolTipText(t.t("Schlüsseldatei ändern"));
	 	mntm_savePK		.setToolTipText(t.t("Private Key speichern"));
	 	mntm_saveSeed	.setToolTipText(t.t("Seed Speichern"));
	 	mntm_print		.setToolTipText(t.t("Ansicht Drucken"));
	 	showPrivKeys	.setToolTipText(t.t("show private keys"));
	 	toolTipEnabled	.setToolTipText(t.t("ToolTips enable"));
	 		
	 	unCompressed	.setToolTipText(t.t("Adress Format unCompressed"));
	 	compressed		.setToolTipText(t.t("Adress Format compressed"));
	 	p2sh			.setToolTipText(t.t("Adress Format p2sh"));
	 	bech32			.setToolTipText(t.t("Adress Format bech32"));

	 	tabbedPane		.setToolTipTextAt(0, t.t("SHA256 hash of a text"));
	 	tabbedPane		.setToolTipTextAt(1, t.t("Generate private-key by rolling dice.\n100 characters, only digits between 1 and 6 are allowed!"));
	 	tabbedPane		.setToolTipTextAt(2, t.t("Enter a private key directly,\nthe format will be recognized automatically."));
	 	tabbedPane		.setToolTipTextAt(3, t.t("Seed erstellen oder laden"));
	 	tabbedPane		.setToolTipTextAt(4, t.t("Transaktion signieren"));
	
	 	btn_removeSeed	.setToolTipText(t.t("Hinterlegten Seed aus dem Speicher löschen"));
	 	txt_seed		.setToolTipText(t.t("Seed hier eingeben, 64 Hex Zeichen."));
	 	txt_pw			.setToolTipText(t.t("Passwort mit dem der Seed entschlüsselt wird"));
		txt_cvc			.setToolTipText(t.t("Die 3 Stellige Prüfziffer Eingeben!"));
	 	btn_newCVC		.setToolTipText(t.t("Neue Prüfziffer berechnen"));
	 	btn_seedUnlock	.setToolTipText(t.t("Entschlüsselt den Seed und erstellt die Liste"));
		txt_nr			.setToolTipText(t.t("Die Nummer des aktuell angezeigten Private-Keys mit Bitcoin Adresse."));
		txt_max			.setToolTipText(t.t("Die Maximale Anzahl der Private-Keys die erzeugt werden."));
	 	btn_exportAdr	.setToolTipText(t.t("CSV-Datei mit Bitcoin-Adressen exportieren"));
	 	lbl_seed		.setToolTipText(t.t("Seed wurde geladen"));
	 	lbl_seedSmal	.setToolTipText(t.t("Seed wurde geladen"));
			
	 	GUI_SignTx.btn_scanQR		.setToolTipText(t.t("Unsignierte Transaktion per QR-Code scannen"));
	 	GUI_SignTx.btn_fromHex		.setToolTipText(t.t("Imput unsign Transaktion in Hexa"));
	 	GUI_SignTx.btn_open_uTx		.setToolTipText(t.t("Unsignierte Transaktion aus dem Dateisystem öffnen"));
	 	GUI_SignTx.lbl_file_uTx		.setToolTipText(t.t("Aktuell verwendeter Dateipfad"));
	 	GUI_SignTx.btn_delete		.setToolTipText(t.t("Alle Transaktionen entfernen"));
	 	GUI_SignTx.cBox_sigMethode	.setToolTipText(t.t("Signaturmethode auswählen"));
	 	GUI_SignTx.btn_autoSig		.setToolTipText(t.t("Automatisches Signieren"));
	 	GUI_SignTx.btn_sig			.setToolTipText(t.t("Alle Signieren"));
	 	GUI_SignTx.btn_saveAllSigTx	.setToolTipText(t.t("Alle signierten Transaktionen Speichern"));
		


	}
	
	
	
	

public GUI() throws ParseException
{
	JPanel 		contentPane 	= new JPanel();
	JPanel 		panel_ausgabe 	= new JPanel();
	JPanel 		panel_eingabe 	= new JPanel();
	JPanel 		panel_input1 	= new JPanel();
	JPanel 		panel_input2 	= new JPanel();
	JPanel 		panel_input3 	= new JPanel();
	JPanel 		panel_input4 	= new JPanel();	// SeedExtractor
	JMenuBar 	menuBar 		= new JMenuBar();	
	JLabel 		lbl_coinName 	= new JLabel();
	MaskFormatter formatWürfel	= new MaskFormatter("##########-##########-##########-##########-##########-##########-##########-##########-##########-##########");  
	MaskFormatter formatter 	= new MaskFormatter("****************************************************************");	// Format Eingabe in *EX 
	txt_privKey 				= new JFormattedTextField(formatter);	
	txt_wuerfel 				= new JFormattedTextField(formatWürfel);	
	ButtonGroup group 			= new ButtonGroup();
	
	setTitle(progName+"     "+version);	
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	setMinimumSize(new Dimension(600, 380));
	setContentPane(contentPane);	
	setSize(1000, 510);
	setLocationRelativeTo(null);
	UIManager.put("TitledBorder.border"	, new LineBorder(color4, 1));
    UIManager.put("ToolTip.background"	, new Color(255,177,56));
    UIManager.put("ToolTip.border"		, BorderFactory.createLineBorder(color4));
	txt_ausgabe	.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
	
	mntm_open		.setIcon(MyIcons.open);
	mntm_edit		.setIcon(MyIcons.edit);
	mntm_savePK		.setIcon(MyIcons.saveKey);
	mntm_saveSeed	.setIcon(MyIcons.saveSeed);
	mntm_print		.setIcon(MyIcons.print);
	mn_file			.setIcon(MyIcons.file);
	mn_settings		.setIcon(MyIcons.settings);
	showPrivKeys	.setIcon(MyIcons.noSeed);
	
	lbl_coinName	.setForeground(Color.GRAY);	
	btn_seedUnlock	.setForeground(Color.red);
	lbl_Achtung		.setForeground(Color.RED);
	progressBar		.setForeground(Color.LIGHT_GRAY);
	contentPane		.setBackground(color1);
	menuBar			.setBackground(color1);	
	mntm_open		.setBackground(color1);
	mntm_savePK		.setBackground(color1);
	mntm_saveSeed	.setBackground(color1);
	mntm_edit		.setBackground(color1);
	mntm_print		.setBackground(color1);
	comboBox_coin	.setBackground(color1);
	cBox_language	.setBackground(color1);
	compressed		.setBackground(color1);
	unCompressed	.setBackground(color1);
	p2sh			.setBackground(color1);
	bech32			.setBackground(color1);
	mItem_newCoin	.setBackground(color1);
	mItem_removeCoin.setBackground(color1);
	showPrivKeys	.setBackground(color1);
	toolTipEnabled	.setBackground(color1);
	txt_ausgabe		.setBackground(color1);
	panel_eingabe	.setBackground(color2);
	tabbedPane		.setBackground(color1);
	panel_input1	.setBackground(color1);
	panel_input2	.setBackground(color1);
	panel_input3	.setBackground(color1);
	panel_input4	.setBackground(color1);
	lbl_1textHash	.setBackground(color1);
	lbl_2wuerfel	.setBackground(color1);
	lbl_3privKey	.setBackground(color1);
	lbl_coinName	.setBackground(color1);
	panel_QRCode	.setBackground(color1);
	panel_ausgabe	.setBackground(color1);
	progressBar		.setBackground(color1);
	txt_seed		.setBackground(Color.white);
	txt_cvc			.setBackground(Color.white);
	txt_max			.setBackground(Color.white);	

	contentPane		.setBorder(new EmptyBorder(5, 5, 5, 5));
	menuBar			.setBorder(new MatteBorder(0, 0, 1, 0, color4));
	lbl_1textHash	.setBorder(new LineBorder(color1, 7));
	lbl_2wuerfel	.setBorder(new LineBorder(color1, 10));
	lbl_3privKey	.setBorder(new LineBorder(color1, 10));	
	btn_Ok			.setBorder(new LineBorder(color1, 6));
	lbl_coinName	.setBorder(new LineBorder(color1, 7));
	txt_inText		.setBorder(new LineBorder(color1, 7));
	txt_privKey		.setBorder(new LineBorder(color4));
	txt_wuerfel		.setBorder(new LineBorder(color4));
	tabbedPane		.setBorder(null);		
	txt_pw			.setBorder(new TitledBorder(null, "Passwort", 					TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
	txt_seed		.setBorder(new TitledBorder(null, "Verschlüsselter Seed (Hexa)",TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
	txt_cvc			.setBorder(new TitledBorder(null, "Prüfziffer", 				TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
	txt_max			.setBorder(new TitledBorder(null, "Max. Key´s", 				TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
	txt_nr			.setBorder(new TitledBorder(null, "Key. Nr.", 					TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
	progressBar		.setBorder(new EmptyBorder(0, 0, 0, 0));

	lbl_2wuerfel	.setBounds(0,   0,646, 60);
	lbl_3privKey	.setBounds(0,   0,646, 60);
	txt_wuerfel		.setBounds(10, 65,780, 20);
	txt_privKey		.setBounds(10, 65,523, 20);
	txt_seed		.setBounds(10, 24,614, 36);
	txt_pw			.setBounds(10, 71,614, 39);
	btn_seedUnlock	.setBounds(10,118,200, 36);
	lbl_Achtung		.setBounds(10,  3,656, 20);
	txt_nr			.setBounds(220,118,77, 38);
	txt_cvc			.setBounds(629, 24,98, 36);
	progressBar		.setBounds(0,   -1,702, 5);
	txt_max			.setBounds(308,119,80, 37);
	btn_exportAdr	.setBounds(10,165,200, 36);
	btn_newCVC		.setBounds(631,78, 94, 29);
	btn_removeSeed	.setBounds(592,118, 30, 36);
	lbl_seed		.setBounds(620,120, 100, 120);
	
	txt_nr			.setVisible(false);
	btn_exportAdr	.setVisible(false);
	lbl_seed		.setVisible(false);
	lbl_seedSmal	.setVisible(false);
	progressBar		.setVisible(false);
	txt_ausgabe		.setEditable(false);
	lbl_1textHash	.setEditable(false);
	lbl_2wuerfel	.setEditable(false);
	lbl_3privKey	.setEditable(false);
	mItem_newCoin	.setEnabled(false);
	mItem_removeCoin.setEnabled(false);
	
	tabbedPane		.setFont(font1);
	lbl_1textHash	.setFont(new Font("Century Gothic", Font.PLAIN, 15));
	lbl_2wuerfel	.setFont(new Font("Century Gothic", Font.PLAIN, 15));
	lbl_3privKey	.setFont(new Font("Century Gothic", Font.PLAIN, 15));
	lbl_coinName	.setFont(new Font("Century Gothic", Font.PLAIN, 18));
	txt_privKey		.setFont(new Font("Ubuntu Mono",Font.PLAIN,15));	
	txt_inText		.setFont(font4);
	txt_wuerfel		.setFont(font4);
	txt_ausgabe		.setFont(font4);	
	lbl_Achtung		.setFont(font1);
	btn_seedUnlock	.setFont(font1);
	btn_newCVC		.setFont(font3);
	btn_exportAdr	.setFont(font1);
	btn_removeSeed	.setFont(font1);
	txt_nr			.setFont(font1);
	txt_seed		.setFont(new Font("Ubuntu Mono",Font.PLAIN,15));
	txt_cvc			.setFont(font4);
	txt_max			.setFont(font4);
	lbl_seed		.setFont(font3);
		
	contentPane		.setLayout(new BorderLayout(0, 0));
	panel_ausgabe	.setLayout(new BorderLayout(0, 0));
	panel_eingabe	.setLayout(new BorderLayout(0, 0));
	panel_input1	.setLayout(new BorderLayout(0, 0));
	panel_input2	.setLayout(null);
	panel_input3	.setLayout(null);
	panel_input4	.setLayout(null);
	panel_QRCode	.setLayout(new BoxLayout(panel_QRCode, BoxLayout.X_AXIS));		

	txt_inText		.setLineWrap(true);
	lbl_2wuerfel	.setLineWrap(true);
	
	btn_Ok			.setMargin(new Insets(0, 0, 0, 0));
	btn_seedUnlock	.setMargin(new Insets(0, 0, 0, 0));
	btn_newCVC		.setMargin(new Insets(0, 0, 0, 0));
	btn_exportAdr	.setMargin(new Insets(0, 0, 0, 0));
	btn_removeSeed	.setMargin(new Insets(0, 0, 0, 0));

	comboBox_coin	.setMaximumSize(new Dimension(3000, 32767));
	cBox_language	.setMaximumSize(new Dimension(4000, 32767));
	btn_Ok			.setPreferredSize(new Dimension(40, 10));
	
	txt_max			.setText("100");			// nur für Default-Einstellungen
	toolTipEnabled	.setSelected(true);			// nur für Default-Einstellungen

	compressed		.setSelected(true);
	progressBar		.setIndeterminate(true);
	btn_Ok			.setContentAreaFilled(false);	
	formatWürfel	.setValidCharacters("123456");		
	formatter		.setValidCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= ");
	btn_Ok			.setVerticalAlignment(SwingConstants.BOTTOM);		
	lbl_Achtung		.setHorizontalAlignment(SwingConstants.CENTER);
	lbl_seedSmal	.setVerticalAlignment(SwingConstants.BOTTOM);
	btn_seedUnlock	.setIconTextGap(10);
	btn_removeSeed	.setIconTextGap(10);
	lbl_seedSmal.setOpaque(true);
	
    lbl_seed		.setVerticalTextPosition(SwingConstants.BOTTOM);
    lbl_seed		.setHorizontalTextPosition(SwingConstants.CENTER);
	
	txt_seed		.setColumns(10);
	txt_pw			.setColumns(10);
	txt_cvc			.setColumns(10);
	txt_max			.setColumns(10);
	
	comboBox_coin	.setModel(new DefaultComboBoxModel(new String[] {"BTC","BTC-T"}));	
	cBox_language	.setModel(new DefaultComboBoxModel(Translate.languages));
	txt_nr			.setModel(new SpinnerNumberModel(1, 1, 100, -1));
	
	// Hover des TappedPane funktioneirt nicht, daher hier weggelassen.
	Hover.addBackground(mn_file);
	Hover.addBackground(comboBox_coin);
	Hover.addBackground(cBox_language);
	Hover.addBackground(mn_addrForm);
	Hover.addBackground(mn_settings);
	Hover.addBackground(mn_Info);
	Hover.addBackground(btn_Ok);
	Hover.addBorder(txt_wuerfel);
	Hover.addBorder(txt_privKey);
	Hover.addBorder(txt_seed);
	Hover.addBorder(txt_cvc);
	Hover.addBorder(txt_pw);
	Hover.addBorder(txt_nr);
	//Hover.addBorder(txt_max);	Deaktiviert, da der Text beim Hovern zu groß wird
	
	group			.add(p2sh);
	group			.add(compressed);
	group			.add(bech32);
	group			.add(unCompressed);	
	
	tabbedPane		.addTab("Input Text-Hash",    MyIcons.txt, 			panel_input1, null);
	tabbedPane		.addTab("Input rolling dice", MyIcons.wuerfel, 		panel_input2, null);
	tabbedPane		.addTab("Input private key",  MyIcons.keyBlack, 	panel_input3, null);
	tabbedPane		.addTab("Seed-Extractor", 	  MyIcons.seedExtractor,panel_input4, null);
	tabbedPane		.addTab("Sign Transaktion",   MyIcons.sig, 			guiSigTx,	  null);
	contentPane		.add(menuBar, BorderLayout.NORTH);
	panel_ausgabe	.add(txt_ausgabe, BorderLayout.CENTER);
	contentPane		.add(panel_eingabe, BorderLayout.CENTER);
	panel_eingabe	.add(tabbedPane, BorderLayout.CENTER);
	menuBar			.add(mn_file);
	mn_file			.add(mntm_open);
	mn_file			.add(mntm_edit);
	mn_file			.add(mntm_savePK);
	mn_file			.add(mntm_saveSeed);	
	mn_file			.add(mntm_print);
	menuBar			.add(Box.createHorizontalStrut(10));
	menuBar			.add(comboBox_coin);
	menuBar			.add(Box.createHorizontalStrut(10));
	mn_addrForm		.add(unCompressed);
	mn_addrForm		.add(compressed);
	menuBar			.add(mn_addrForm);	
	mn_addrForm		.add(p2sh);	
	mn_addrForm		.add(bech32);
	menuBar			.add(Box.createHorizontalStrut(10));
	menuBar			.add(mn_settings);
	mn_settings		.add(mItem_newCoin);
	mn_settings		.add(mItem_removeCoin);
	mn_settings		.add(showPrivKeys);
	mn_settings     .add(toolTipEnabled);
	menuBar			.add(Box.createHorizontalGlue());
	menuBar			.add(cBox_language);
	menuBar			.add(Box.createHorizontalStrut(10));
	menuBar			.add(mn_Info);
	mn_Info			.add(mntm_Info);
	contentPane		.add(panel_ausgabe, BorderLayout.SOUTH);
	panel_ausgabe	.add(panel_QRCode, BorderLayout.WEST);
	panel_ausgabe	.add(lbl_seedSmal, BorderLayout.EAST);
	panel_input1	.add(txt_inText);
	panel_input1	.add(lbl_1textHash, BorderLayout.NORTH);
	panel_input1	.add(btn_Ok, BorderLayout.EAST);
	panel_input2	.add(lbl_2wuerfel);
	panel_input2	.add(txt_wuerfel);
	panel_input3	.add(lbl_3privKey);
	panel_input3	.add(txt_privKey);
	panel_eingabe	.add(lbl_coinName, BorderLayout.NORTH);
	panel_ausgabe	.add(Box.createHorizontalStrut(15), BorderLayout.NORTH);
	panel_input4	.add(lbl_Achtung);		
	panel_input4	.add(txt_seed);
	panel_input4	.add(txt_pw);	
	panel_input4	.add(btn_seedUnlock);
	panel_input4	.add(btn_newCVC);
	panel_input4	.add(txt_nr);
	panel_input4	.add(txt_cvc);
	panel_input4	.add(progressBar);
	panel_input4	.add(txt_max);
	panel_input4	.add(btn_exportAdr);
	panel_input4	.add(btn_removeSeed);
	panel_input4	.add(lbl_seed);

	

// ----------------------------------------------------------- ActionListeners -------------------//
	
	
	// Öffnet das Info-Bescheibungs-Fenster
	mntm_Info.addActionListener(new ActionListener() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			GUI_InfoText dialog = new GUI_InfoText();
			dialog.setVisible(true);

		}
	});
	

	
	
	// Close Button wird abgefangen und hier selbst verarbeitet.
	addWindowListener(new java.awt.event.WindowAdapter() 
	{
	    @Override
	    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
	    {
	    	Config.save();
	    	System.exit(0);
	    }
	});
	
	
	mntm_open.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				guiW = new GUI_Wallet(frame.getX()+10, frame.getY()+10, "open");
				guiW.setVisible(true);
				GUI.frame.setEnabled(false);
			}
			catch(Exception ex) 
			{
				txt_ausgabe.setForeground(Color.RED);
				txt_ausgabe.setText(ex.toString());
			} 
		}
	});
	
	
	mntm_savePK.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				if(Action.addr.equals("")) throw new IllegalArgumentException(t.t("Saving not possible, no valid address in the output field."));
				GUI_Wallet guiW = new GUI_Wallet(frame.getX()+10, frame.getY()+10, "save");
				guiW.setVisible(true);
				GUI.frame.setEnabled(false);
				guiW.dtm.addRow(new Object[]{"", comboBox_coin.getSelectedItem() , Action.addr , Action.prvWIF , getSelectedAddressFormat(), LocalDate.now()}); 
			}
			catch(Exception ex) 
			{
				txt_ausgabe.setForeground(Color.RED);
				txt_ausgabe.setText(ex.getMessage());
			}  
		}
	});
	
	
	
	// Speichert den Seed in der Schlüsseldatenbank
	mntm_saveSeed.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				if(txt_seed.getText().equals("") || SeedExtractor.privKeys==null) throw new IllegalArgumentException(t.t("Unable to save, no seed decrypted."));
				GUI_Wallet guiW = new GUI_Wallet(frame.getX()+10, frame.getY()+10, "save");
				guiW.setVisible(true);
				GUI.frame.setEnabled(false);
				guiW.dtm.addRow(new Object[]{"", comboBox_coin.getSelectedItem() , "", txt_seed.getText() , getSelectedAddressFormat(), LocalDate.now()}); 
			}
			catch(Exception ex) 
			{
				txt_ausgabe.setForeground(Color.RED);
				txt_ausgabe.setText(ex.getMessage());
			}  
		}
	});
	
	
	
	
	mntm_edit.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				GUI_Wallet guiW = new GUI_Wallet(frame.getX()+10, frame.getY()+10, "edit");
				guiW.setVisible(true);
				GUI.frame.setEnabled(false);
			}
			catch(Exception ex) 
			{
				txt_ausgabe.setForeground(Color.RED);
				txt_ausgabe.setText(ex.toString());
			} 			
		}
	});
	
	
	
	btn_Ok.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent arg0) {Action.go();}		
	});
	
	txt_inText.addKeyListener(new KeyAdapter() 
	{
		@Override
		public void keyReleased(KeyEvent arg0) {if(arg0.getKeyCode()==10) Action.go();}
	});
	
	txt_wuerfel.addKeyListener(new KeyAdapter() 
	{
		@Override
		public void keyPressed(KeyEvent arg0) {if(arg0.getKeyCode()==10) Action.go();}
	});
	
	txt_privKey.addKeyListener(new KeyAdapter() 
	{
		@Override
		public void keyPressed(KeyEvent arg0) {if(arg0.getKeyCode()==10) Action.go();}
	});
	
	comboBox_coin.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			try 
			{
				lbl_coinName.setText(CoinParameter.getFromSymbol((String) comboBox_coin.getSelectedItem()).name);
				if(lbl_coinName.getText().equals("Bitcoin-Mainnet"))	{color1 = new Color(255,244,230); color2 = new Color(235,224,210);  lbl_coinName.setIcon(MyIcons.bitcoinLogoMain); frame.setIconImage(MyIcons.bitcoinLogoMain.getImage());}
				else 													{color1 = new Color(245,255,233); color2 = new Color(225,235,213);  lbl_coinName.setIcon(MyIcons.bitcoinLogoTest); frame.setIconImage(MyIcons.bitcoinLogoTest.getImage());}
				UIManager.put("Panel.background"		,color1);
				UIManager.put("OptionPane.background"	,color1);
				UIManager.put("TextField.background"	,color1);		// Ist nötig für z.B. den TxPrinter, damit dor alle Farben richtig sind.
				UIManager.put("TabbedPane.selected"		,color1);		// Selektierter Hintergrund
				UIManager.put("TabbedPane.contentAreaColor", color1); 	// Selektierter Hintergrund
				contentPane		.setBackground(color1);
				menuBar			.setBackground(color1);	
				mntm_open		.setBackground(color1);
				mntm_savePK		.setBackground(color1);
				mntm_saveSeed	.setBackground(color1);
				mntm_edit		.setBackground(color1);
				mntm_print		.setBackground(color1);
				mntm_Info		.setBackground(color1);
				comboBox_coin	.setBackground(color1);
				cBox_language	.setBackground(color1);
				compressed		.setBackground(color1);
				unCompressed	.setBackground(color1);
				p2sh			.setBackground(color1);
				bech32			.setBackground(color1);
				mItem_newCoin	.setBackground(color1);
				mItem_removeCoin.setBackground(color1);
				showPrivKeys	.setBackground(color1);
				toolTipEnabled	.setBackground(color1);
				txt_ausgabe		.setBackground(color1);
				panel_eingabe	.setBackground(color2);
				tabbedPane		.setBackground(color2);
				panel_input1	.setBackground(color1);
				panel_input2	.setBackground(color1);
				panel_input3	.setBackground(color1);
				guiSigTx		.setBackground(color1);
				panel_input4	.setBackground(color1);
				lbl_1textHash	.setBackground(color1);
				lbl_2wuerfel	.setBackground(color1);
				lbl_3privKey	.setBackground(color1);
				lbl_seedSmal	.setBackground(color1);
				lbl_coinName	.setBackground(color1);
				panel_QRCode	.setBackground(color1);
				panel_ausgabe	.setBackground(color2);		
				GUI_SignTx.lbl_file_uTx.setBackground(color1);
				GUI_SignTx.cBox_sigMethode.setBackground(color1);
				progressBar		.setBackground(color1);	
				lbl_1textHash	.setBorder(new LineBorder(color1, 7));
				lbl_2wuerfel	.setBorder(new LineBorder(color1, 10));
				lbl_3privKey	.setBorder(new LineBorder(color1, 10));	
				btn_Ok			.setBorder(new LineBorder(color1, 6));
				lbl_coinName	.setBorder(new LineBorder(color2, 7));
				txt_inText		.setBorder(new LineBorder(color1, 7));	
				
				SwingUtilities.updateComponentTreeUI(frame);// Muss hier rein um die UI zu aktualisieren
				tabbedPane.setUI(new BasicTabbedPaneUI() 	// legt die Umriss Lineenfarbe (Border) des TabbedPane fest. Windows-Builder geht damit nicht!
				{
				   @Override
				   protected void installDefaults() 
				   {
				       super.installDefaults();
				       highlight 		= color2; 	// nichts
				       lightHighlight 	= color4;  	// Oberer TabRahmen
				       shadow 			= color4;	// Unterer Tab Rahmen + Hauptrahmen Unten
				       darkShadow 		= color2;   // Unterer Tab Rahmen + Hauptrahmen Unten
				       focus 			= color2;	// nichts  
				   }
				});
			}
			catch(Exception e){lbl_coinName.setText("");};
			Action.go();
		}
	});
	
	mItem_newCoin.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{	
			setEnabled(false);
			GUI_CoinList frame= new GUI_CoinList(getX()+mn_settings.getX()-170,getY(), false);
			frame.setVisible(true);
		}
	});

	mItem_removeCoin.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			setEnabled(false);
			GUI_CoinList frame= new GUI_CoinList(getX()+mn_settings.getX()-170,getY(), true);
			frame.setVisible(true);
		}
	});
	
	
	showPrivKeys.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			Action.go();
		}
	});
	
	toolTipEnabled.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			ToolTipManager.sharedInstance().setEnabled(toolTipEnabled.isSelected());	
		}
	});
	
	tabbedPane.addChangeListener(new ChangeListener() 
	{
		public void stateChanged(ChangeEvent e) {Action.go();}
	});
	
	unCompressed.addItemListener(new ItemListener() 
	{
		public void itemStateChanged(ItemEvent e) {Action.go();}
	});
	
	p2sh.addItemListener(new ItemListener() 
	{
		public void itemStateChanged(ItemEvent e) {Action.go();}
	});
	
	bech32.addItemListener(new ItemListener() 
	{
		public void itemStateChanged(ItemEvent e) {Action.go();}
	});
	
	mntm_print.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			Print.PrintImage(contentPane,50,50,contentPane.getWidth()/2,contentPane.getHeight()/2);
		}
	});
	
	txt_ausgabe.addHyperlinkListener(new HyperlinkListener() 
	{
		public void hyperlinkUpdate(HyperlinkEvent e) 
		{	
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) 
			{
				try {Desktop.getDesktop().browse(e.getURL().toURI());} 												
				catch (Exception ex) {}
			}
		}
	});
	
	cBox_language.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			setAllTextInGUI();
		}
	});
	
	
	
	


	
	
	
	
	
	
		
	
// ----------------------------------------------------------------------------- Seed-Extractor ----------------------------------------------------------------------------------//
	
	
	// Der Hinterlegte Seed wird wieder gelöscht
	// und betreffende Felder Freigegeben und angepasst.
	btn_removeSeed.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent e) 
		{
			SeedExtractor.removeSeed();
			txt_seed 			.setEnabled(true);
			txt_pw				.setEnabled(true);
			GUI.txt_nr			.setVisible(false);
			GUI.btn_exportAdr	.setVisible(false);
			lbl_seed			.setVisible(false);
			lbl_seedSmal		.setVisible(false);
			Action.go();			
		}
	});
	
	
	
	
	
	
	
	// Erstellt eine neue 3 Stellige Prüfziffer
	// Die Prüfziffer sind die letzten 3 Ziffern des einfachen SHA256 Hash des Seed´s.
	btn_newCVC.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent e) 
		{
			int i = JOptionPane.showConfirmDialog(frame, t.t("Die 3-stellige Prüfziffer sollte sich bei dem Seed befinden.\nSie ist nützlich um Eingabefehler zu erkennen.\nDie Prüfziffer wird normalerweise nur beim erstmaligen Erstellen des Seeds genieriert und dann notiert.\nWird die Prüfziffer neu erstellt, kann nicht erkannt werden, ob der Seed Fehler enthällt.\nSoll die Prüfziffer aus dem Seed neu berechnet werden?"), t.t("Create check digit"), 2);
			if(i==0)
			{
				if(txt_seed.getText().length()!=64) {Toolkit.getDefaultToolkit().beep();  JOptionPane.showMessageDialog(frame, t.t("Zum Erstellen der Prüfziffer muss ein 64Hexa-Zeichen Seed eingegeben sein!"), t.t("Seed hat falsches Format oder ist nicht vorhanden"), JOptionPane.ERROR_MESSAGE);}
				else
				{
					String cvc = Calc.getHashSHA256_from_HexString(txt_seed.getText()).substring(61);
					txt_cvc.setText(cvc);
				}
			}		
		}	
	});	
		
			
	
	// Seed wird entschlüsselt
	// Muss in einem eigenem Thread gestartet werden, da die Zeitverzögerung (Scrypt) den Thread blockiert.
	// Achtung, alle GUI-Anpassungen müssen in der EDT (SwingUtilities.invokeLater) ausgeführt werden!
	btn_seedUnlock.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent e) 
		{			
			GUI.txt_ausgabe.setForeground(Color.BLACK);
			GUI.txt_ausgabe.setText("");
			GUI.panel_QRCode.removeAll();
			if(checkCVC(txt_seed.getText(), txt_cvc.getText())==false) return;
			progressBar	.setVisible(true);
			txt_seed	.setEnabled(false);
			txt_pw		.setEnabled(false);		
			txt_cvc		.setEnabled(false);
			btn_newCVC	.setEnabled(false);
			txt_nr		.setEnabled(false);
			txt_max		.setEnabled(false);
			btn_seedUnlock		.setEnabled(false);
			btn_removeSeed.setEnabled(false);
			btn_exportAdr.setEnabled(false);
			new Thread(new Runnable() 
			{
				public void run() 
				{
					try 
					{
						SeedExtractor.encodeSeed(txt_seed.getText(), new String(txt_pw.getPassword()));					
						SwingUtilities.invokeLater(new Runnable() 
						{
							public void run()
							{
								txt_nr		.setVisible(true);
								btn_exportAdr.setVisible(true);
								lbl_seed	.setVisible(true);
								lbl_seedSmal	.setVisible(true);
							}
						});
					} 
					catch (Exception e1) 
					{
						SwingUtilities.invokeLater(new Runnable() 
						{
							public void run()
							{							
								txt_seed	.setEnabled(true);
								txt_pw		.setEnabled(true);
								GUI.txt_ausgabe.setForeground(Color.RED);
								GUI.txt_ausgabe.setText(e1.getMessage());
								e1.printStackTrace();
							}
						});
					}			
					SwingUtilities.invokeLater(new Runnable() 
					{
						public void run()
						{
							progressBar	.setVisible(false);
							txt_cvc		.setEnabled(true);
							btn_newCVC	.setEnabled(true);
							txt_nr		.setEnabled(true);
							txt_max		.setEnabled(true);
							btn_seedUnlock	.setEnabled(true);
							btn_exportAdr.setEnabled(true); 
							btn_removeSeed.setEnabled(true);
						}
					});
				}
			}).start();			
		}
	});
	
	
	
	
	// Bei Eingabe einer Nummer, oder beim Anklicken des JSpinners
	txt_nr.addChangeListener(new ChangeListener() 
	{
		public void stateChanged(ChangeEvent e) 
		{
			try
			{
				int nr = (Integer) txt_nr.getValue();
				// Diese Bedingung wird vom SpinnerNumberModel schon abgefangen und kann daher eh nicht auftreten!
				if(nr < 1 || nr > Integer.parseInt(txt_max.getText())) throw new Exception("Error: Priv.Key Nr. muss zwichen 1 und "+ Integer.parseInt(txt_max.getText()) +" sein!"); 
				Action.go();	
			}
			catch(Exception e1)
			{
				GUI.txt_ausgabe.setForeground(Color.RED);
				GUI.txt_ausgabe.setText(e1.getMessage());
			}
		}
	});
	
	
	
	// Bei Eingabe in das "Max Key´s" Feld
	txt_max.addKeyListener(new KeyAdapter() 
	{
		@Override
		public void keyReleased(KeyEvent e) 
		{
			txt_nr	.setModel(new SpinnerNumberModel(1, 1, Integer.parseInt(txt_max.getText()), 1));
		}
	});
	
	
	
	// Bitcoin Adressen werden auf csv Datei exportiert
	btn_exportAdr.addActionListener(new ActionListener()
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				String str = "";
				CoinParameter cp = CoinParameter.getFromSymbol((String) GUI.comboBox_coin.getSelectedItem());
				for(int i=0; i<SeedExtractor.privKeys.length;i++)
				{				
					byte[] pub_u 	= Calc.getPublicKey(SeedExtractor.privKeys[i], false);  	// Uncompresses PubKey
					byte[] pub_c 	= Calc.getPublicKey(SeedExtractor.privKeys[i], true);		// Compressed PubKey
					byte[] h160_u 	= Calc.getHashRIPEMD160(Calc.getHashSHA256(pub_u));   		// Uncompressed Hash160  					
					byte[] h160_c 	= Calc.getHashRIPEMD160(Calc.getHashSHA256(pub_c));  		// Compressed Hash160   					
					BitcoinAddr address_u = new BitcoinAddr(h160_u, cp.pref_PubKey);
					BitcoinAddr address_c = new BitcoinAddr(h160_c, cp.pref_PubKey);
					String addrUncomp = address_u.getBase58Address();
					String addrCompre = address_c.getBase58Address();
					String addrP2SH   = address_c.getP2SHAddress(cp.pref_P2SH);
					String addrBech32 = Bech32Address.segwitToBech32(cp.bech32 , Convert.byteArray_to_int(cp.bip44), h160_c);
					str = str+addrUncomp+","+addrCompre+","+addrP2SH+","+addrBech32+",\n";	
				}
				String userDir = System.getProperty("user.home");
				JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
				FileFilter filter = new FileNameExtensionFilter(".csv", "csv");
				chooser.setFileFilter(filter);	
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setSelectedFile(new File("BTCAddressList.csv"));
				int button = chooser.showSaveDialog(btn_exportAdr);
				if(button==0)																					
				{
					String file = chooser.getSelectedFile().getAbsolutePath();	
					BufferedWriter br = new BufferedWriter(new FileWriter(file));
					br.write(str);
					br.close();				
				}		
			}
			catch(Exception ex) 
			{	
				GUI.txt_ausgabe.setForeground(Color.RED);
				GUI.txt_ausgabe.setText("Error: "+ex.getMessage());
				ex.printStackTrace();
			}
		}
	});	
}







// ----------------------------------------------------------------------------------- Hilfs-Methoden -----------------------------------------------------------------------------------




	// Gibt den Namen des selektierten Address Format (JRadioButtonMenuItem) zurück. 
	public static String getSelectedAddressFormat()
	{
		if(unCompressed.isSelected())	return 	"unCompressed";		
		if(compressed.isSelected()) 	return 	"compressed";	
		if(p2sh.isSelected())			return 	"P2SH";				
		if(bech32.isSelected())			return 	"bech32";				
		return null;
	}
	
	
	
	// Setzt den JRadioButtonMenuItem des Address Formates auf den Wert, dessen Name übergeben wurde. 
	public static void setSelectedAddressFormat(String name)
	{
		if(name.equals("unCompressed")) {unCompressed.doClick(); return;}
		if(name.equals("compressed")) 	{compressed.doClick();	 return;}
		if(name.equals("P2SH")) 		{p2sh.doClick();		 return;}
		if(name.equals("bech32")) 		{bech32.doClick();		 return;}
		throw new IllegalArgumentException("Unknown address format!");
	}
	
	
	
	// Seed-Exractor: prüft den CVC-Code (Prüfziffer)
	// Der CVC Code sind die letzten 3 Stellen des SHA256 des Seed´s 
	// Gibt true zurück, wenn CVP richtig ist.
	// Exception wird ausgelöst, bei einem echtem Fehler, z.B: "ungerade String-Zeichenfolge" etc.
	private static boolean checkCVC(String seed, String cvc)
	{
		try
		{
			if(cvc.equals("")) 																
			{
				JOptionPane.showMessageDialog(frame, t.t("Check digit missing!"), t.t("Check digit missing!"), JOptionPane.ERROR_MESSAGE);		
				return false;
			}
			if(Calc.getHashSHA256_from_HexString(seed).substring(61).equals(cvc) == false) 	
			{
				JOptionPane.showMessageDialog(frame, t.t("Check digit wrong!"), t.t("Check digit wrong!"), JOptionPane.ERROR_MESSAGE);		
				return false;
			}
			return true;
		}
		catch(Exception e)
		{
			GUI.txt_ausgabe.setForeground(Color.RED);
			GUI.txt_ausgabe.setText("Error: "+e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}