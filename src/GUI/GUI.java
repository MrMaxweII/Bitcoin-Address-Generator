package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.MaskFormatter;
import BTClib3001.Bech32Address;
import BTClib3001.BitcoinAddr;
import BTClib3001.Calc;
import BTClib3001.CoinParameter;
import BTClib3001.Convert;
import BTClib3001.PkScript;
import BTClib3001.PrvKey;
import BTClib3001.Transaktion;
import BTClib3001.TxPrinter;
import BTClib3001.TxSigniererLegancy;
import BTClib3001.TxSigniererWitness;
import CoinGen.Action;
import CoinGen.Config;
import CoinGen.Print;
import CoinGen.QRCodeZXING;
import CoinGen.QrCapture;
import seedExtractor.SeedExtractor;
import javax.swing.border.MatteBorder;



public class GUI extends JFrame 
{

	
	public static final String			progName		= "Coin Address Generator";
	public static final String			version 		= "V3.4.3";
	public static final String			autor 			= "Mr. Maxwell";
	public static final String			eMail			= "Maxwell-KSP@gmx.de";
	public static final String			myBitcoinAddr 	= "12zeCvN7zbAi3JDQhC8tU3DBm35kDEUNiB";	
	public static GUI 					frame;												// Frame des Hauptprogrammes
	public static GUI_Wallet 			guiW;												// Frame der Wallet
	public static JFormattedTextField 	txt_wuerfel;										// Eingabefeld der Würfelzahlen
	public static JFormattedTextField	txt_privKey;										// Eingabefeld des PrivKey (Hexa oder WIF)
	public static JTextArea 			txt_inText 		= new JTextArea();					// Eingabefeld des für den Priv-Key in TextForm. (Wird gehasht)
	public static JTextArea 			txt_usigTx 		= new JTextArea();					// Eingabefeld Signatur: Unsignierte Tx
	public static JTextArea 			txt_sigTx 		= new JTextArea();					// Ausgabefeld der Signierten Transaktion
	public static JComboBox 			comboBox_coin 	= new JComboBox();					// Auswahlfeld der Coins
	public static JEditorPane 			txt_ausgabe 	= new JEditorPane("text/html","");	// Das gesamte Ausgabefeld, für Meldungen, etc. oder der Coin-Daten + QR-Codes
	public static JTabbedPane 			tabbedPane 		= new JTabbedPane(JTabbedPane.LEFT);// Linkes Tap-Feld mit der Auswahl der Registerkarten
	public static JLabel 				lbl_file_uTx 	= new JLabel();						// Label des Speicherorts der Unsign.TX
	public static JPanel 				panel_QRCode 	= new JPanel();						// Das Panel für die QR-Codes
	public static JRadioButtonMenuItem 	compressed 		= new JRadioButtonMenuItem("WIF compressed");
	public static JRadioButtonMenuItem 	unCompressed	= new JRadioButtonMenuItem("WIF uncompressed");
	public static JRadioButtonMenuItem 	p2sh 			= new JRadioButtonMenuItem("P2SH");
	public static JRadioButtonMenuItem 	bech32 			= new JRadioButtonMenuItem("Bech32");
	public static JRadioButtonMenuItem	showPrivKeys	= new JRadioButtonMenuItem("show private keys");  // Zeigt die Private-Keys im Programm an oder verdeckt sie
	public static JMenuItem 			mItem_newCoin 	= new JMenuItem("import new Coin");	// Importiert einen neuen Coin. 				(Ist Default disabled)
	public static JMenuItem 			mItem_removeCoin= new JMenuItem("remove Coin");		// Deaktiviert wieder einen Coin aus der Liste. (Ist Default disabled)
	public static int 					posX			= 50;								// X-Position des Programmes
	public static int					posY			= 50;								// Y-Position des Programmes
	public static JTextField 			txt_seed		= new JTextField(); 				// SeedExtractor Eingabe verschlüsselter Seed
	public static JTextField			txt_cvc 		= new JTextField();					// SeedExtractor Eingabe Prüfziffer
	public static JTextField 			txt_pw			= new JTextField();					// SeedExtractor Eingabe Passwort
	public static JTextField 			txt_max 		= new JTextField();					// SeedExtractor Maximale Anzahl der Private-Keys bei der Ausgabe
	public static JSpinner 				txt_nr			= new JSpinner();					// SeedExtractor Die aktuelle Nummer des Priv-Keys	
	public static JButton 				btn_go 			= new JButton("Seed entschlüsseln");// SeedExtractor Go-button der die die entschlüsselung startet
	public static JProgressBar 			progressBar 	= new JProgressBar();				// SeedExtractor Warte-Balken
	public static Color 				color1 			= new Color(255,244,230);			// Hintergrundfarbe des Programmes
	public static Color 				color4 			= new Color(247,147,26); 			// Farbe Linien (Offizielle BTC-Farbe)

	
	
	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{				
					frame = new GUI();
					comboBox_coin.setSelectedIndex(0);
					Config.load();
					frame.setVisible(true);
				}
				catch (Exception e) {e.printStackTrace();}
			}
		});
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
	JPanel 		panel_input5 	= new JPanel();
	JPanel 		panel_4_1 		= new JPanel();
	JPanel 		panel_4_2 		= new JPanel();
	JPanel 		panel_4_3 		= new JPanel();
	JPanel 		panel_4_4 		= new JPanel();
	JScrollPane scrollPane1 	= new JScrollPane();
	JScrollPane scrollPane2 	= new JScrollPane();
	JMenuBar 	menuBar 		= new JMenuBar();
	JMenu 		mn_file 		= new JMenu("File");
	JMenu 		mn_addrForm		= new JMenu("Address Format");
	JMenu 		mn_settings 	= new JMenu("Settings");
	JMenu 		mn_Info 		= new JMenu("Info");
	JMenuItem 	mntm_open 		= new JMenuItem("open");
	JMenuItem 	mntm_save 		= new JMenuItem("save");
	JMenuItem 	mntm_edit 		= new JMenuItem("edit");
	JMenuItem 	mntm_print 		= new JMenuItem("print");	
	JTextPane 	txt_info 		= new JTextPane();
	JTextPane 	lbl1 			= new JTextPane();
	JTextArea 	lbl2 			= new JTextArea();
	JTextPane 	lbl3 			= new JTextPane();
	JTextPane 	lbl_coinName 	= new JTextPane();
	JTextPane 	lbl_UsignTx 	= new JTextPane();
	JTextPane 	lbl_signTx 		= new JTextPane();
	MaskFormatter formatWürfel	= new MaskFormatter("##########-##########-##########-##########-##########-##########-##########-##########-##########-##########");  
	MaskFormatter formatter 	= new MaskFormatter("****************************************************************");	// Format Eingabe in *EX 
	txt_privKey 				= new JFormattedTextField(formatter);	
	txt_wuerfel 				= new JFormattedTextField(formatWürfel);	
	ButtonGroup group 			= new ButtonGroup();
	JButton 	btn_Ok 			= new JButton("OK");
	JButton 	btn_scanQR 		= new JButton("scan QR code");
	JButton 	btn_open_uTx 	= new JButton("open Tx");
	JButton 	btn_save_Tx 	= new JButton("save Tx");
	JButton 	btn_qrOut 		= new JButton("QR code");
	JButton 	btn_showUsigTx 	= new JButton("show Tx");
	JButton 	btn_showSigTx 	= new JButton("show Tx");
	JButton 	btn_sig_seed 	= new JButton("sig with Seed");
	JButton 	btn_sig_privK 	= new JButton("sig with Priv-Keys");
	JButton 	btn_exportAdr 	= new JButton("Adressen exportieren");	
	JLabel 		lbl_Achtung 	= new JLabel("Niemals Seed eingeben auf Hardware mit möglicher Internetverbindung !!!");
	Font 		font1 			= new Font("SansSerif", Font.PLAIN, 12); 			// Font für Label-Beschriftungen
	Font 		font2 			= new Font("SansSerif", Font.PLAIN, 11); 			// Font für Rahmenbeschriftung
	Font 		font3 			= new Font("SansSerif", Font.PLAIN, 10); 			// Font für Rahmenbeschriftung der Prüfziffer
	

	
	setTitle(progName+"     "+version);	
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	setMinimumSize(new Dimension(600, 300));
	setContentPane(contentPane);	
	setBounds(posX, posY, 1000, 550);
	UIManager.put("TitledBorder.border", new LineBorder(color4, 1));

	
	txt_ausgabe		.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
	
	lbl_coinName	.setForeground(Color.GRAY);		
	lbl_Achtung		.setForeground(Color.RED);
	progressBar		.setForeground(Color.LIGHT_GRAY);
	txt_sigTx		.setBackground(new Color(245,245,245));
	contentPane		.setBackground(color1);
	menuBar			.setBackground(color1);	
	mntm_open		.setBackground(color1);
	mntm_save		.setBackground(color1);
	mntm_edit		.setBackground(color1);
	mntm_print		.setBackground(color1);
	comboBox_coin	.setBackground(color1);
	compressed		.setBackground(color1);
	unCompressed	.setBackground(color1);
	p2sh			.setBackground(color1);
	bech32			.setBackground(color1);
	mItem_newCoin	.setBackground(color1);
	mItem_removeCoin.setBackground(color1);
	showPrivKeys	.setBackground(color1);
	txt_info		.setBackground(color1);
	txt_ausgabe		.setBackground(color1);
	panel_eingabe	.setBackground(color1);
	tabbedPane		.setBackground(color1);
	panel_input1	.setBackground(color1);
	panel_input2	.setBackground(color1);
	panel_input3	.setBackground(color1);
	panel_input5	.setBackground(color1);
	panel_input4	.setBackground(color1);
	panel_4_1		.setBackground(color1);
	panel_4_2		.setBackground(color1);
	panel_4_3		.setBackground(color1);
	panel_4_4		.setBackground(color1);
	lbl1			.setBackground(color1);
	lbl2			.setBackground(color1);
	lbl3			.setBackground(color1);
	lbl_coinName	.setBackground(color1);
	panel_QRCode	.setBackground(color1);
	panel_ausgabe	.setBackground(color1);
	lbl_file_uTx	.setBackground(color1);	
	lbl_UsignTx		.setBackground(color1);
	lbl_signTx		.setBackground(color1);
	progressBar		.setBackground(color1);	

	contentPane		.setBorder(new EmptyBorder(5, 5, 5, 5));
	menuBar			.setBorder(new MatteBorder(0, 0, 1, 0, color4));
	lbl1			.setBorder(new LineBorder(color1, 7));
	lbl2			.setBorder(new LineBorder(color1, 10));
	lbl3			.setBorder(new LineBorder(color1, 10));	
	btn_Ok			.setBorder(new LineBorder(color1, 6));
	lbl_coinName	.setBorder(new LineBorder(color1, 7));
	txt_inText		.setBorder(new LineBorder(color1, 7));
	txt_usigTx		.setBorder(new LineBorder(color1, 5));
	txt_sigTx		.setBorder(new LineBorder(color1, 5));
	txt_privKey		.setBorder(new LineBorder(color4));
	txt_wuerfel		.setBorder(new LineBorder(color4));
	scrollPane1		.setBorder(BorderFactory.createEmptyBorder()); 
	scrollPane2		.setBorder(BorderFactory.createEmptyBorder());
	tabbedPane		.setBorder(null);		
	txt_pw			.setBorder(new TitledBorder(null, "Passwort (Text)", 				TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
	txt_seed		.setBorder(new TitledBorder(null, "Verschlüsselter Seed (Hexa)", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.GRAY));
	txt_cvc			.setBorder(new TitledBorder(null, "Pr\u00FCfziffer", 				TitledBorder.LEADING, TitledBorder.TOP, font3, Color.GRAY));
	txt_max			.setBorder(new TitledBorder(null, "Max. Key\u00B4s", 				TitledBorder.LEADING, TitledBorder.TOP, font3, Color.GRAY));
	txt_nr			.setBorder(new TitledBorder(null, "Key. Nr.", 						TitledBorder.LEADING, TitledBorder.TOP, font3, Color.GRAY));
	progressBar		.setBorder(null);


	tabbedPane.setUI(new BasicTabbedPaneUI() // legt die Umriss Lineenfarbe (Border) des TabbedPane fest. Windows-Builder geht damit nicht!
	{
		   @Override
		   protected void installDefaults() 
		   {
		       super.installDefaults();
		       highlight 		= color1; 	// nichts
		       lightHighlight 	= color4;  	// Oberer TabRahmen
		       shadow 			= color4;	// Unterer Tab Rahmen + Hauptrahmen Unten
		       darkShadow 		= color1;   // Unterer Tab Rahmen + Hauptrahmen Unten
		       focus 			= color1;	// nichts
		   }
	});
	
	
	lbl2			.setBounds(0, 0, 646, 54);
	lbl3			.setBounds(0, 0, 646, 54);
	txt_wuerfel		.setBounds(10, 56, 660, 20);
	txt_privKey		.setBounds(10, 54, 523, 20);
	txt_seed		.setBounds(10, 24, 614, 36);
	txt_pw			.setBounds(10, 71, 614, 39);
	btn_go			.setBounds(10, 118, 161, 36);
	lbl_Achtung		.setBounds(10, 3, 656, 20);
	txt_nr			.setBounds(181, 119, 77, 39);
	txt_cvc			.setBounds(632, 24, 64, 36);
	progressBar		.setBounds(0, -1, 702, 5);
	txt_max			.setBounds(268, 121, 69, 36);
	btn_exportAdr	.setBounds(10, 165, 161, 36);

		
	txt_nr			.setVisible(false);
	btn_exportAdr	.setVisible(false);
	progressBar		.setVisible(false);
	txt_info		.setEditable(false);
	txt_info		.setEditable(false);
	txt_ausgabe		.setEditable(false);
	lbl1			.setEditable(false);
	lbl2			.setEditable(false);
	lbl3			.setEditable(false);
	lbl_coinName	.setEditable(false);
	lbl_UsignTx		.setEditable(false);	
	lbl_signTx		.setEditable(false);	
	txt_sigTx		.setEditable(false);
	mItem_newCoin	.setEnabled(false);
	mItem_removeCoin.setEnabled(false);
	
	txt_info		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	tabbedPane		.setFont(new Font("Century Gothic", Font.PLAIN, 14));
	lbl1			.setFont(new Font("Century Gothic", Font.PLAIN, 15));
	lbl3			.setFont(new Font("Century Gothic", Font.PLAIN, 14));
	lbl_coinName	.setFont(new Font("Century Gothic", Font.PLAIN, 18));
	txt_inText		.setFont(new Font("Century Gothic", Font.PLAIN, 13));
	lbl2			.setFont(new Font("Century Gothic", Font.PLAIN, 14));
	lbl_file_uTx	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	lbl_UsignTx		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	lbl_signTx		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	btn_scanQR		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	btn_open_uTx	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	btn_save_Tx		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	btn_qrOut		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	btn_showUsigTx	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	btn_sig_seed	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	btn_sig_privK	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	btn_showSigTx	.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	txt_privKey		.setFont(new Font("Consolas", Font.PLAIN, 14));
	txt_wuerfel		.setFont(new Font("Consolas", Font.PLAIN, 11));
	txt_ausgabe		.setFont(new Font("Consolas", Font.PLAIN, 12));	
	txt_sigTx		.setFont(new Font("Consolas", Font.PLAIN, 11));
	txt_usigTx		.setFont(new Font("Consolas", Font.PLAIN, 11));
	lbl_Achtung		.setFont(font1);
	btn_go			.setFont(font1);
	btn_exportAdr	.setFont(font1);
	txt_nr			.setFont(font1);

	contentPane		.setLayout(new BorderLayout(0, 0));
	panel_ausgabe	.setLayout(new BorderLayout(0, 0));
	panel_eingabe	.setLayout(new BorderLayout(0, 0));
	panel_input1	.setLayout(new BorderLayout(0, 0));
	panel_input2	.setLayout(null);
	panel_input3	.setLayout(null);
	panel_input4	.setLayout(null);
	panel_input5	.setLayout(new BorderLayout(0, 0));
	panel_4_2		.setLayout(new BoxLayout(panel_4_2, BoxLayout.Y_AXIS));
	panel_4_1		.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
	panel_4_3		.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
	panel_4_4		.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
	panel_QRCode	.setLayout(new BoxLayout(panel_QRCode, BoxLayout.X_AXIS));		

	txt_inText		.setLineWrap(true);
	lbl2			.setLineWrap(true);
	txt_usigTx		.setLineWrap(true);
	txt_sigTx		.setLineWrap(true);
	
	btn_Ok			.setMargin(new Insets(0, 0, 0, 0));
	btn_scanQR		.setMargin(new Insets(0, 0, 0, 0));
	btn_open_uTx	.setMargin(new Insets(0, 0, 0, 0));	
	lbl_UsignTx		.setMargin(new Insets(8, 8, 0, 3));
	lbl_signTx		.setMargin(new Insets(8, 8, 0, 3));
	btn_save_Tx		.setMargin(new Insets(0, 0, 0, 0));
	btn_qrOut		.setMargin(new Insets(0, 0, 0, 0));
	btn_go			.setMargin(new Insets(0, 0, 0, 0));
	btn_exportAdr	.setMargin(new Insets(0, 0, 0, 0));
	btn_showUsigTx	.setMargin(new Insets(0, 0, 0, 0));
	btn_sig_seed	.setMargin(new Insets(0, 0, 0, 0));	
	btn_sig_privK	.setMargin(new Insets(0, 0, 0, 0));
	btn_showSigTx	.setMargin(new Insets(0, 0, 0, 0));

	comboBox_coin	.setMaximumSize(new Dimension(150, 32767));
	lbl_UsignTx		.setMaximumSize(new Dimension(2147483647, 20));
	lbl_signTx		.setMaximumSize(new Dimension(2147483647, 20));
	panel_4_4		.setMaximumSize(new Dimension(32767, 30));
	btn_Ok			.setPreferredSize(new Dimension(40, 10));
	btn_scanQR		.setPreferredSize(new Dimension(110, 19));
	btn_open_uTx	.setPreferredSize(new Dimension(85, 19));	
	btn_save_Tx		.setPreferredSize(new Dimension(85, 19));
	btn_qrOut		.setPreferredSize(new Dimension(85, 19));
	panel_4_1		.setPreferredSize(new Dimension(1000, 26));
	btn_showUsigTx	.setPreferredSize(new Dimension(85, 19));
	btn_sig_seed	.setPreferredSize(new Dimension(120, 19));
	btn_sig_privK	.setPreferredSize(new Dimension(120, 19));
	btn_showSigTx	.setPreferredSize(new Dimension(85, 19));
	
	txt_info		.setText(progName+"\n"+"Version:                "+version+"\n"+"Author:                  "+autor+"\n"+"E-Mail:                   "+eMail+"\n"+"please donate: "+myBitcoinAddr+"\n\nThis program creates a private-key in Wallet Import Format (WIF)\nas well as the associated address and the public-key.\nYou must enter 64 random hexadecimal characters or have them generated.");
	lbl1			.setText("SHA256 hash of a text");
	lbl2			.setText("Generate private-key by rolling dice.  \r\n100 characters, only digits between 1 and 6 are allowed!");
	lbl3			.setText("Enter a private key directly, \r\nthe format will be recognized automatically.");
	lbl_file_uTx	.setText("user.dir");	
	lbl_UsignTx		.setText("unsign Transaktion");
	txt_info		.setText("Der SeedExtractor entschlüsselt einen Seed mit einem Password und erstellt beliebig viele PrivateKeys mit BTC-Adressen aus diesem Seed.\r\nEs muss der verschlüsselte Seed und das dazugehörige Passwort eingegeben werden.\r\n\r\nDie 3 Stellige Prüfziffer sollte auf auf dem Seed-Medium enthalten sein.\r\nEs handelt sich um die letzen 3 Stellen des SHA-256 Hash, des verschlüsselten Seed.\r\nDie Prüfziffer soll Fehler beim eintippen des Seed´s verhindern.\r\n\r\nDer Entschlüsselte Seed wird zur Sicherheit nicht angezeigt, was normalerweise auch nicht nötig ist.\r\nDieses Programm speichert keine Daten ab.\r\n\r\nAchung! \r\nSei kein Dummkopf und halte dich immer an die folgenden Regeln!\r\nWenn du deinen echten Seed hier eingibtst und mit dem Passwort entschlüsselst, darf die Hardware auf dem diese Programm ausgeführt wird, \r\nniemals in Verbindung mit dem Internet stehen oder in zukunft kommen können!!!\r\nAchte auch auf mögliche W-Lan Verbindungen.\r\nSorge dafür das alle beteiligten Datenägräger zerstört oder ordendlich Gelöscht werden! \r\nAlle Treiber für eine Internet oder W-Lan verbindung müssen deinstalliert sein.\r\nSchreibe den benötigten PrivKey am besten ab oder nutze USB-Sticks, die später zu löschen sind!");
	txt_max			.setText("100");
	lbl_signTx		.setText("sign Transaktion");
	txt_cvc			.setToolTipText("Die 3 Stellige Prüfziffer Eingeben! (Rückseite)\r\n");
	txt_max			.setToolTipText("Die Maximale Anzahl der Private-Keys die erzeugt werden.");
	txt_usigTx		.setToolTipText("Paste the Unsigned Transaction here!");

	compressed		.setSelected(true);
	progressBar		.setIndeterminate(true);
	btn_Ok			.setContentAreaFilled(false);	
	formatWürfel	.setValidCharacters("123456");		
	formatter		.setValidCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= ");
	btn_Ok			.setVerticalAlignment(SwingConstants.BOTTOM);		
	lbl_Achtung		.setHorizontalAlignment(SwingConstants.CENTER);
	scrollPane1		.setViewportBorder(null);		
	scrollPane1		.setViewportView(txt_usigTx);	
	scrollPane2		.setViewportBorder(null);	
	scrollPane2		.setViewportView(txt_sigTx);
	txt_sigTx		.setRows(4);

	txt_seed		.setColumns(10);
	txt_pw			.setColumns(10);
	txt_cvc			.setColumns(10);
	txt_max			.setColumns(10);
	
	comboBox_coin	.setModel(new DefaultComboBoxModel(new String[] {"BTC","BTC-T"}));	
	txt_nr			.setModel(new SpinnerNumberModel(1, 1, 100, -1));
	
	group			.add(p2sh);
	group			.add(compressed);
	group			.add(bech32);
	group			.add(unCompressed);	
	tabbedPane		.addTab("Input Text-Hash",    null, panel_input1, null);
	tabbedPane		.addTab("Input rolling dice", null, panel_input2, null);
	tabbedPane		.addTab("Input private key",  null, panel_input3, null);
	tabbedPane		.addTab("Seed-Extractor", 	  null, panel_input4, null);
	tabbedPane		.addTab("Sign Transaktion",   null, panel_input5, null);
	contentPane		.add(menuBar, BorderLayout.NORTH);
	panel_ausgabe	.add(txt_ausgabe, BorderLayout.CENTER);
	contentPane		.add(panel_eingabe, BorderLayout.CENTER);
	panel_eingabe	.add(tabbedPane, BorderLayout.CENTER);
	menuBar			.add(mn_file);
	mn_file			.add(mntm_open);
	mn_file			.add(mntm_save);	
	mn_file			.add(mntm_edit);
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
	menuBar			.add(Box.createHorizontalStrut(10));
	menuBar			.add(mn_Info);
	mn_Info			.add(txt_info);
	contentPane		.add(panel_ausgabe, BorderLayout.SOUTH);
	panel_ausgabe	.add(panel_QRCode, BorderLayout.WEST);
	panel_input1	.add(txt_inText);
	panel_input1	.add(lbl1, BorderLayout.NORTH);
	panel_input1	.add(btn_Ok, BorderLayout.EAST);
	panel_input2	.add(lbl2);
	panel_input2	.add(txt_wuerfel);
	panel_input3	.add(lbl3);
	panel_input3	.add(txt_privKey);
	panel_eingabe	.add(lbl_coinName, BorderLayout.NORTH);
	panel_ausgabe	.add(Box.createHorizontalStrut(15), BorderLayout.NORTH);
	panel_input5	.add(panel_4_1, BorderLayout.NORTH);
	panel_4_1		.add(btn_scanQR);
	panel_4_1		.add(btn_open_uTx);
	panel_4_1		.add(lbl_file_uTx);	
	panel_input5	.add(panel_4_2, BorderLayout.CENTER);	
	panel_4_2		.add(lbl_UsignTx);
	panel_4_2		.add(scrollPane1);
	panel_4_2		.add(panel_4_4);
	panel_4_4		.add(btn_showUsigTx);
	panel_4_4		.add(btn_sig_privK);
	panel_4_4		.add(btn_sig_seed);	
	panel_4_2		.add(lbl_signTx);
	panel_4_2		.add(scrollPane2);
	panel_input5	.add(panel_4_3, BorderLayout.SOUTH);
	panel_4_3		.add(btn_showSigTx);
	panel_4_3		.add(btn_save_Tx);
	panel_4_3		.add(btn_qrOut);
	panel_input4	.add(lbl_Achtung);		
	panel_input4	.add(txt_seed);
	panel_input4	.add(txt_pw);	
	panel_input4	.add(btn_go);
	panel_input4	.add(txt_nr);
	panel_input4	.add(txt_cvc);
	panel_input4	.add(progressBar);
	panel_input4	.add(txt_max);
	panel_input4	.add(btn_exportAdr);
	
	
	
	
	
	
// ----------------------------------------------------------- ActionListeners -------------------//
	
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
	
	
	mntm_save.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				if(Action.addr.equals("")) throw new IllegalArgumentException("Saving not possible, no valid address in the output field.");
				GUI_Wallet guiW = new GUI_Wallet(frame.getX()+10, frame.getY()+10, "save");
				guiW.setVisible(true);
				GUI.frame.setEnabled(false);
				guiW.dtm.addRow(new Object[]{"", comboBox_coin.getSelectedItem() , Action.addr , Action.prvWIF , getSelectedAddressFormat(), LocalDate.now()}); 
			}
			catch(Exception ex) 
			{
				txt_ausgabe.setForeground(Color.RED);
				txt_ausgabe.setText(ex.toString());
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
				if(lbl_coinName.getText().equals("Bitcoin-Mainnet"))	color1 = new Color(255,244,230); 
				else 													color1 = new Color(245,255,233);
				UIManager.put("Panel.background", 		color1);
				UIManager.put("OptionPane.background", 	color1);
				UIManager.put("TextField.background", 	color1);
				contentPane		.setBackground(color1);
				menuBar			.setBackground(color1);	
				mntm_open		.setBackground(color1);
				mntm_save		.setBackground(color1);
				mntm_edit		.setBackground(color1);
				mntm_print		.setBackground(color1);
				comboBox_coin	.setBackground(color1);
				compressed		.setBackground(color1);
				unCompressed	.setBackground(color1);
				p2sh			.setBackground(color1);
				bech32			.setBackground(color1);
				mItem_newCoin	.setBackground(color1);
				mItem_removeCoin.setBackground(color1);
				showPrivKeys	.setBackground(color1);
				txt_info		.setBackground(color1);
				txt_ausgabe		.setBackground(color1);
				panel_eingabe	.setBackground(color1);
				tabbedPane		.setBackground(color1);
				panel_input1	.setBackground(color1);
				panel_input2	.setBackground(color1);
				panel_input3	.setBackground(color1);
				panel_input5	.setBackground(color1);
				panel_input4	.setBackground(color1);
				panel_4_1		.setBackground(color1);
				panel_4_2		.setBackground(color1);
				panel_4_3		.setBackground(color1);
				panel_4_4		.setBackground(color1);
				lbl1			.setBackground(color1);
				lbl2			.setBackground(color1);
				lbl3			.setBackground(color1);
				lbl_coinName	.setBackground(color1);
				panel_QRCode	.setBackground(color1);
				panel_ausgabe	.setBackground(color1);
				lbl_file_uTx	.setBackground(color1);	
				lbl_UsignTx		.setBackground(color1);
				lbl_signTx		.setBackground(color1);
				progressBar		.setBackground(color1);	
				lbl1			.setBorder(new LineBorder(color1, 7));
				lbl2			.setBorder(new LineBorder(color1, 10));
				lbl3			.setBorder(new LineBorder(color1, 10));	
				btn_Ok			.setBorder(new LineBorder(color1, 6));
				lbl_coinName	.setBorder(new LineBorder(color1, 7));
				txt_inText		.setBorder(new LineBorder(color1, 7));	
				txt_usigTx		.setBorder(new LineBorder(color1, 5));
				txt_sigTx		.setBorder(new LineBorder(color1, 5));
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
	
	
	
	
	
	
	
	
// ------------------------------------------------------------- Sign Transaktion -----------------------------------------------------------
	
	
	
	// Scannt eine unsignierte Tx per QR-Code mit der Kamera
		btn_scanQR.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{			
				Thread t = new Thread(new Runnable() 
				{
					@Override
					public void run() 
					{														
						try
						{
							txt_ausgabe.setText("");
							frame.setEnabled(false);
							QrCapture qr = new QrCapture(null, getX()+50, getY()+80);	
							String p2 = qr.getResult();
							qr.close();								
							if(p2.equals("")) throw new IOException("User abort");								
							txt_usigTx.setText(p2);
						}
						catch(Exception ex) 
						{
							GUI.txt_ausgabe.setForeground(Color.RED);
							txt_ausgabe.setText(ex.getMessage());
						};	
						frame.setEnabled(true);
					}
				});
				t.start();			
			}
		});
	
	
	
	
	// Öffnet mit dem JFileChooser die Unsig. Transatkon.
	btn_open_uTx.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			txt_ausgabe.setText("");
			String userDir = System.getProperty("user.home");			
			JFileChooser chooser = new JFileChooser(userDir +"/Desktop")
			{	// Diese Änderung sorgt für die Positionierung des FileChoosers
				protected JDialog createDialog(Component parent) throws HeadlessException 
				{
					JDialog dlg = super.createDialog(parent);
					dlg.setLocation(frame.getX()+150,frame.getY()+80); 
					return dlg;
				}
			};	
			chooser.setFileFilter(new FileNameExtensionFilter("unsigned.txn", "txn"));			
			chooser.setCurrentDirectory(new File(GUI.lbl_file_uTx.getText()));							
			int button = chooser.showOpenDialog(null);		
			if(button==0)																					
			{
				GUI.lbl_file_uTx.setText(chooser.getSelectedFile().getAbsolutePath());	
				try 
				{
					BufferedReader br = new BufferedReader(new FileReader(GUI.lbl_file_uTx.getText()));
					String str = "";
					while(br.ready()) str = str +br.readLine();	
					br.close();				
					txt_usigTx.setText(str);
					GUI.txt_ausgabe.setText("");
				} 
				catch (Exception e1) 
				{
					GUI.txt_ausgabe.setForeground(Color.RED);
					GUI.txt_ausgabe.setText("File Error: No correct transaction!");
				}			
			}			
		}
	});
	
	

	
	// Unsignierte Transaktion wird mit dem TxPrinter angezeigt.
	btn_showUsigTx.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_ausgabe.setText("");
				Transaktion utx = new Transaktion(Convert.hexStringToByteArray(GUI.txt_usigTx.getText()),0); 
				CoinParameter cp = CoinParameter.getFromSymbol((String) GUI.comboBox_coin.getSelectedItem());
				TxPrinter tx = new TxPrinter(cp.magic, utx, frame.getX()+5, frame.getY()+30);
				tx.setModal(true);
				tx.setVisible(true);
			}
			catch(Exception ex) 
			{	
				GUI.txt_ausgabe.setForeground(Color.RED);
				GUI.txt_ausgabe.setText("Tx Error: No correct transaction!");
				ex.printStackTrace();
			}
		}
	});
	
	
	
	
	// Signierte Transaktion wird mit dem TxPrinter angezeigt.
	btn_showSigTx.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_ausgabe.setText("");
				Transaktion sigTx = new Transaktion(Convert.hexStringToByteArray(GUI.txt_sigTx.getText()),0); 
				CoinParameter cp = CoinParameter.getFromSymbol((String) GUI.comboBox_coin.getSelectedItem());
				TxPrinter tx = new TxPrinter(cp.magic, sigTx, frame.getX()+5, frame.getY()+30);
				tx.setModal(true);
				tx.setVisible(true);
			}
			catch(Exception ex) 
			{	
				GUI.txt_ausgabe.setForeground(Color.RED);
				GUI.txt_ausgabe.setText("Tx Error: No correct transaction!");
				ex.printStackTrace();
			}
		}
	});
	
	
	
	
	
	// Transaktion wird mit dem "Seed-Extractor" signiert.  Dabei werden alle Seed-Erzeugten Priv.Keys zum Signieren getestet.
	btn_sig_seed.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				GUI.txt_ausgabe.setText("");
				if(SeedExtractor.privKeys==null) throw new IOException("No Seed loaded!");	
				byte[] sigTx = sigTx(Convert.hexStringToByteArray(GUI.txt_usigTx.getText()),   SeedExtractor.privKeys);				
				txt_sigTx.setText(Convert.byteArrayToHexString(sigTx));
			}
			catch (Exception e1) 
			{
				GUI.txt_ausgabe.setForeground(Color.RED);
				GUI.txt_ausgabe.setText(e1.getMessage());
			}
		}
	});
	
	
	
	
	
	
	
	// Transaktion wird mit einer Liste von Private-Keys Signiert
	JTextPane txt_privKeys = new JTextPane();						// Liste der Private-Keys im "Sign Transaktion" Dialogfeld. Muss hier außerhalb sein, da die Werte beim schließen des Dialogfeldes gespeichert bleiben sollen.
	String promtText = "ecc58dff38448c0f56232d226a83cbc83d802f4c60b5d1145476cff0ee531fa0,\n1c210145de5c6912c473821bbda4de2c651be1011be8594a944f55809c953af1,\n50da38ee4b4a1f8390180512de113c913bfdb62061d02d5099a30951985e8b4d";
	txt_privKeys.setText(promtText);
	txt_privKeys.setForeground(Color.gray);
	btn_sig_privK.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			GUI.txt_ausgabe.setText("");
			JDialog dialog = new JDialog();		// Kleines Dialog-Fenster zur Eingabe der Priv-Keys öffnet sich
			dialog.setBounds(frame.getX()+160, frame.getY()+65, 480, 350);
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.setTitle("Sign Transaction");
			JTextPane lbl = new JTextPane();
			JScrollPane scrollPane = new JScrollPane();
			JButton okButton = new JButton("OK");
			lbl.setBackground(color1);
			lbl.setEditable(false);
			lbl.setText("Signure with a list of private keys that you can enter here.\r\nPrivate keys must be formatted in Hexa Decimal format!\r\nThe order of the private keys doesn't matter. All private keys required for signing must only be present!\r\nThe private keys must separated with a comma:\r\nExample:");	
			txt_privKeys.setFont(new Font("Consolas", Font.PLAIN, 11));
			scrollPane.setViewportView(txt_privKeys);
			okButton.setActionCommand("OK");
			dialog.getContentPane().add(lbl, BorderLayout.NORTH);
			dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
			dialog.getContentPane().add(okButton, BorderLayout.SOUTH);
			okButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e) // Startet die Signierung und schließt das Fenster
				{
					try
					{
						String str = txt_privKeys.getText();
						str = str.replaceAll(" ", "");
						str = str.replaceAll("\n", "");
						str = str.replaceAll("\r", "");
						ArrayList<String> list = new ArrayList<>(Arrays.asList(str.split(",")));	
						byte[][] priv_b = new byte[list.size()][];  // Array der Private-Keys
						for(int i=0;i<list.size();i++)
						{
							priv_b[i] = Convert.hexStringToByteArray(list.get(i));
						}				
						byte[] sigTx = sigTx(Convert.hexStringToByteArray(GUI.txt_usigTx.getText()),priv_b);				
						txt_sigTx.setText(Convert.byteArrayToHexString(sigTx));
						dialog.dispose();
					}
					catch (Exception e1) 
					{
						GUI.txt_ausgabe.setForeground(Color.RED);
						GUI.txt_ausgabe.setText(e1.getMessage());
						e1.printStackTrace();
					}
				}
			});	
			
			txt_privKeys.addFocusListener(new FocusListener() 
			{
				@Override
				public void focusGained(FocusEvent e) 
				{
					if(txt_privKeys.getText().equals(promtText)) 
					{
						txt_privKeys.setForeground(Color.black);
						txt_privKeys.setText("");
	                }
				}

				@Override
				public void focusLost(FocusEvent e) 
				{
					 if(txt_privKeys.getText().isEmpty()) 
					 {
						 txt_privKeys.setForeground(Color.gray);
						 txt_privKeys.setText(promtText);
		             }
				}
			});
			dialog.setModal(true);
			dialog.setVisible(true);	
		}
	});
	
	
	
	
	// Signierte Transaktion wird gespeichert
	btn_save_Tx.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				txt_ausgabe.setText("");
				String str = txt_sigTx.getText();
				String userDir = System.getProperty("user.home");
				JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
				FileFilter filter = new FileNameExtensionFilter(".txn", "txn");
				chooser.setFileFilter(filter);	
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setSelectedFile(new File("signed.txn"));
				int button = chooser.showSaveDialog(btn_save_Tx);
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
				GUI.txt_ausgabe.setText(ex.getMessage());
				ex.printStackTrace();
			}
		}
	});
	
	
	
	// Signierte Transaktion wird als QR-Code ausgegeben
		btn_qrOut.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try
				{	
					txt_ausgabe.setText("");
					QRCodeZXING.printQR(txt_sigTx.getText(), "Sig. Transaction", GUI.color1, Color.black, GUI.frame.getX()+150, GUI.frame.getY()+5);
				}
				catch(Exception ex) 
				{	
					GUI.txt_ausgabe.setForeground(Color.RED);
					GUI.txt_ausgabe.setText(ex.getMessage());
					ex.printStackTrace();
				}
			}
		});

	
	
	
	
// ----------------------------------------------------------------------------- Seed-Extractor ----------------------------------------------------------------------------------//
	

	
	// Seed wird entschlüsselt
	// Muss in einem eigenem Thread gestartet werden, da die Zeitverzögerung (Scrypt) den Thread blockiert.
	btn_go.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{			
			GUI.txt_ausgabe.setForeground(Color.BLACK);
			GUI.txt_ausgabe.setText("");
			GUI.panel_QRCode.removeAll();		
				Thread t = new Thread(new Runnable() 
				{
					public void run() 
					{
						progressBar.setVisible(true);
						btn_go.setEnabled(false);
						txt_nr.setEnabled(false);
						txt_max.setEnabled(false);
						btn_exportAdr.setEnabled(false);
						try 
						{
							checkCVC(txt_seed.getText(), txt_cvc.getText());
							SeedExtractor.encodeSeed(txt_seed.getText(), txt_pw.getText());
							txt_nr		.setVisible(true);
							btn_exportAdr.setVisible(true);
						} 
						catch (Exception e1) 
						{
							GUI.txt_ausgabe.setForeground(Color.RED);
							GUI.txt_ausgabe.setText(e1.getMessage());
							e1.printStackTrace();
						}			
						progressBar.setVisible(false);
						btn_go.setEnabled(true);
						txt_nr.setEnabled(true);
						txt_max.setEnabled(true);
						btn_exportAdr.setEnabled(true);
					}
				});	
				t.start();		
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
			txt_nr		.setModel(new SpinnerNumberModel(1, 1, Integer.parseInt(txt_max.getText()), 1));
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
// Diese Metode wirft nur eine IOException wenn der cvc falsch ist, oder eben nicht.
private static void checkCVC(String seed, String cvc) throws IOException
{
	if(cvc.equals("")) 																throw new IOException("Prüfziffer fehlt!");
	if(Calc.getHashSHA256_from_HexString(seed).substring(61).equals(cvc) == false) 	throw new IOException("Prüfziffer ist falsch!");
}



// Signiert Legancy und SegWit Transaktion
// Achtung, derzeit sind compressed und uncompresset Tx implementiert.
// Übergeben werden muss die unsigniert Tx als Byte-Array und eine unsortierte Liste mit möglichen Private-Keys. Die Anzahl der Priv-Keys ist egel
// Das Formate der Signatur soll diese Methode selbstständig erkennen.
private static byte[] sigTx(byte[] unSigTx, byte[][] privKeyList) 
{
	try
	{
		if(unSigTx.length==0) 		throw new IOException("No unsigned transaction loaded.");
		if(privKeyList.length==0) 	throw new IOException("No private keys found.");
		Transaktion tx = new Transaktion(unSigTx,0);
		byte[][] randArray = new byte[tx.getTxInCount()][];
		SecureRandom random = new SecureRandom();
		for(int i=0; i<tx.getTxInCount();i++)										// Berechnet die Zufallszahlen "k" aus dem Hash von {SecureRandom und dem prevHash aus der Tx}
		{																			// Sollte SecureRandom versagen, ist "k" immer noch einzigartig, da prevHash einzigartig für jeden Tx-Input ist.
			String prevHash = Convert.byteArrayToHexString(tx.getTxPrevHash()[i]);
			String randStr= Convert.byteArrayToHexString(random.generateSeed(32));
			String randOut = prevHash + randStr;
			randOut = Calc.getHashSHA256_from_HexString(randOut);
			randArray[i] = Convert.hexStringToByteArray(randOut);
		}
		CoinParameter cp = CoinParameter.getFromSymbol((String) GUI.comboBox_coin.getSelectedItem());
		byte[][] privList = TxSigniererLegancy.calcPrivKeyList(unSigTx, privKeyList, cp.pref_PrivKey, cp.magic);
		boolean[] compressed = new boolean[tx.getTxInCount()];
		byte[][] b_pk = tx.getSigScript(); 											// Die PK-Scripte, an der Stelle der späteren Sig-Scripte 
		for(int i=0; i<tx.getTxInCount();i++)										
		{
			PkScript pk = new PkScript(b_pk[i]);
			byte[] h160 = pk.getHash160();
			PrvKey priv = new PrvKey(privList[i], cp.pref_PrivKey);
			if(Arrays.equals(h160, priv.getHash160BitcoinAddress(false))) {compressed[i] = false;};
			if(Arrays.equals(h160, priv.getHash160BitcoinAddress(true)))  {compressed[i] = true;};		
		}
		if(tx.isWitness) 	return TxSigniererWitness.sigTx(unSigTx, privList, randArray, compressed);
		else				return TxSigniererLegancy.sigTx(unSigTx, privList, randArray, compressed);   	
	}
	catch(Exception e)
	{
		GUI.txt_ausgabe.setForeground(Color.RED);
		GUI.txt_ausgabe.setText(e.getMessage());
		e.printStackTrace();
		return Convert.hexStringToByteArray("");
	}	
}
}