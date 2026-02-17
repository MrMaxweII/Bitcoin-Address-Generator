package lib3001.btc;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.SystemColor;
import java.text.DecimalFormat;
import java.util.Base64;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import javax.swing.border.TitledBorder;
import org.json.JSONException;
import lib3001.crypt.Convert;
import lib3001.qrCode.QRCodeZXING;
import javax.swing.JSplitPane;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import java.awt.Checkbox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayInputStream;
import java.awt.event.ItemEvent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;



/*******************************************************************************************************************************************
*		V1.15	   												 Autor: Mr. Maxwell  									vom 01.02.2026		*
* 		LIB3001 Bibliotheks Klasse																											*
*		Letzte Änderungen: 																													*
*			- Icons für Buttons hinzugefügt. (Alle fest in dieser Klasse)																															*
*			- QR-Code hinzugefügt																											*
*			- TX kann in JSON angezeigt werden, btn_JSON ScrolPane eingefügt.																*
*			- v-size Hinzugefügt (wird nur bei signierten Tx angezeigt),  isSigned wird angezeigt 											*
*			- bug behoben, nun wird auch bei misch-Transaktionen r,s und PubKey angezeigt.													*
*																																			*
*		Dieser Transaktions-Printer zeigt Transaktionen ausführlich in einer GUI an.														*
*		Es öffnen sich ein oder mehrere JFrames (oder JDialog) in denen die TX angezeigt wird.												*
*		Diese Klasse kann von JFrame oder JDialog nach Wunsch abgeleitet werden! 															*
*		Anwendung: 																															*
*		Dies Klasse ist eine Erweiterung von JFrame (oder JDialog) und muss so behandlet werden.											*
*		1. Konstruktor Aufruf und JFrame Object erzeugen, am besten in einem eigenem Thread!												*
*		 (JFrame frame = new TxPrinter(MAINNET,tx,500,200);)																				*
*		2. frame.setVisible(true);																											*
*		Die Konstruktor-Parameter sind beim Konstruktor beschrieben.																		*
*		Es gibt mehrere Konstruktoren die verschiedene Input-Formate akzeptieren.															*
*		Achtung: Font für Textfelder "Ubuntu Mono" muss möglw. installiert werden!															*
********************************************************************************************************************************************/



public class TxPrinter extends JDialog
{

	public final static byte[] MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
	public final static byte[] TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};
	
	
	
	
/**	Konstruktor der das JFrame Fenster öffnet. (Besser in einem neuem Thread öffnen)
	@param magic Magic für MainNet: f9beb4d9, TestNet: 0b110907
	@param tx	 Transaktion aus der Transaktion-Klasse
	@param x	 X-Position des JFrames
	@param y	 Y-Position des JFrames   
 * @wbp.parser.constructor**/
	public TxPrinter(byte[] magic, Transaktion tx, int x, int y) 
	{
		setTitle("TxPrinter");
		setBounds(x, y, 1300, 425);
		if(tx.getTxInCount()>4 || tx.getTxOutCount() >4) setBounds(x, y, 1300, 580);
		if(tx.getTxInCount()>8 || tx.getTxOutCount() >8) setBounds(x, y, 1300, 800);
		
		JPanel contentPane = new JPanel();
		setContentPane(contentPane);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);  // Die Klasse als Einzelne Anwendung gestartet wird, dann hier aktivieren!
		
		JPanel 			pnl_oben 		= new JPanel();
		JPanel 			pnl_hide 		= new JPanel();				// Das Panel zum Aus- Einblenden der Einzelnen Spalten
		JTextArea 		txt_meldungen 	= new JTextArea();			// Für Fehlermeldungen etc.		
		JTextField 		txt_txHash		= new JTextField();
		JTextField 		txt_version		= new JTextField();
		JTextField		txt_isWitness	= new JTextField();
		JTextField 		txt_size		= new JTextField();
		JTextField 		txt_vSize		= new JTextField();			// Virtual-Size 
		JTextField 		txt_lockTime	= new JTextField();
		JTextField 		txt_totalValue	= new JTextField();  		// Die Gebühr ist generell nicht in Transaktionen enthalten und muss daher übergeben werden!
		JTextField		txt_signed		= new JTextField();			// Gibt an, ob es sich um eine Unsignierte Tx oder eine Signierte Tx handelt.
		JButton 		btn_rawTx 		= new JButton("raw");		// Zeigt die Tx in Hex an
		JButton 		btn_JSON 		= new JButton("JSON"); 	// Zeigt die Tx in JSON an
		JButton 		btn_qr 			= new JButton("QR Code"); 	// Zeigt die Tx im QR-Code an
		Checkbox 		hide_prevHash 	= new Checkbox("prev Hash");
		Checkbox 		hide_prevIndex 	= new Checkbox("prev Index");
		Checkbox		hide_sig		= new Checkbox("signature");
		Checkbox		hide_sigR		= new Checkbox("sig.R");
		Checkbox		hide_sigS		= new Checkbox("sig.S");
		Checkbox		hide_pubKey		= new Checkbox("public Key");
		Checkbox 		hide_sequence 	= new Checkbox("sequence");
		Checkbox		hide_witness	= new Checkbox("witness");
		Checkbox 		hide_pkScript 	= new Checkbox("PK-Script");
		Checkbox 		hide_address 	= new Checkbox("address");
		Checkbox		hide_h160		= new Checkbox("Ripemd-160");
		Checkbox 		hide_value 		= new Checkbox("value");
		JSplitPane 		splitPane 		= new JSplitPane();
		JSeparator 		separator 		= new JSeparator();
		Font			font1			= new Font("SansSerif", Font.PLAIN, 9); 		// Font für Rahmenbeschriftung klein
		Font 			font2 			= new Font("SansSerif", Font.PLAIN, 11); 		// Font für Rahmenbeschriftung normal
		Font 			font3 			= new Font("SansSerif", Font.PLAIN, 14); 		// Font für Rahmenbeschriftung groß
		Font			font4			= new Font("Ubuntu Mono", Font.PLAIN, 14); 		// Font für Rahmenbeschrifteun
				
		btn_rawTx	.setIcon(Base64StringToImageIcon("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAHFJREFUOE/Fk1EOwCAIQ+f9Dz2DSZcKKENN9E8tj4JSns1VKP4NWKz9pDiUYFegEhhNBiAskygLMJAVQAfxAGwTjdXNln3TaQAHaBCCxMEQwPY8gLmflXAfALuoGX8KztwmZibjLIDt/nXRHiAaoBBWARTkMBE8IKqOAAAAAElFTkSuQmCC"));
		btn_JSON	.setIcon(Base64StringToImageIcon("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAFxJREFUOE9jZKAQMKLp/4/EJ0oOWRFMM7pGdDeiqBtGBhDrf1h4wNWDwoBUzSiGUMUAkImkugLFCxj+IpA46ZcOQDbBwgidBjkSnAipmhKRAxPdcJxyhDIOwcwOALtIHw96+UzDAAAAAElFTkSuQmCC"));
		btn_qr		.setIcon(Base64StringToImageIcon("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAJ1JREFUOE+lU0EOwCAIKxcP+/9bd9jFBScLw4om84KBQkoBwc8nPb+SOhozv/97qPgC9leAJsaY972YGSgrYLFmVwxiawPLHQ1SjK8YdWQCDvjmOIB6AlKAen3LMGGpBqaqWkbZxhhjj4jGIPZg/m5RAHSGL7NdEadjXY2xkQoMq2eSLZJ1xNaYtjAo7qYSRaSrzO7SJ9K9yBZp69BvFchDEZ8pbZ8AAAAASUVORK5CYIIA"));
		
		txt_txHash		.setBounds(10,  10, 540, 36);
		txt_version		.setBounds(10,  60, 100, 36);
		txt_isWitness	.setBounds(120, 60, 100, 36);
		txt_size		.setBounds(230, 60, 100, 36);
		txt_vSize		.setBounds(340, 60, 100, 36);
		txt_lockTime	.setBounds(450, 60, 100, 36);
		txt_signed		.setBounds(560, 60, 100, 36);
		txt_totalValue	.setBounds(670, 60, 100, 36);
		btn_rawTx		.setBounds(562, 21, 94,  22);
		btn_JSON		.setBounds(662, 21, 94,  22);
		btn_qr			.setBounds(762, 21, 94,  22);
		pnl_hide		.setBounds(10,  107,1264,50);
		separator		.setBounds(700, 8,  3,  39);
		hide_prevHash	.setBounds(10,  20, 80, 23);
		hide_prevIndex 	.setBounds(96,  20, 80, 23);
		hide_sig	 	.setBounds(182, 20, 80, 23);
		hide_witness 	.setBounds(268, 20, 70, 23);
		hide_sigR	 	.setBounds(344, 20, 60, 23);
		hide_sigS	 	.setBounds(410, 20, 60, 23);
		hide_pubKey	 	.setBounds(476, 20, 80, 23);
		hide_sequence 	.setBounds(562, 20, 70, 23);
		hide_address 	.setBounds(730, 20, 90, 23);
		hide_pkScript 	.setBounds(830, 20, 90, 23);
        hide_h160 		.setBounds(930, 20, 90, 23);	
        hide_value 		.setBounds(1030, 20, 90, 23);	
					
		txt_meldungen	.setBorder(new EmptyBorder(0, 4, 2, 2));
		txt_txHash		.setBorder(new TitledBorder(null, "Tx-ID", 			TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
		txt_version		.setBorder(new TitledBorder(null, "Version", 		TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
		txt_isWitness	.setBorder(new TitledBorder(null, "witness", 		TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
		txt_size		.setBorder(new TitledBorder(null, "size", 			TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
		txt_vSize		.setBorder(new TitledBorder(null, "v-size", 		TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
		txt_lockTime	.setBorder(new TitledBorder(null, "Lock Time", 		TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
		txt_signed		.setBorder(new TitledBorder(null, "signed", 		TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
		txt_totalValue	.setBorder(new TitledBorder(null, "total out Value",TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
		pnl_hide		.setBorder(new TitledBorder(null, "Show or hide columns", TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));

		txt_meldungen   .setFont(font4);
		txt_txHash		.setFont(font4);
		txt_version		.setFont(font4);
		txt_isWitness	.setFont(font4);
		txt_size		.setFont(font4);
		txt_vSize		.setFont(font4);
		txt_lockTime	.setFont(font4);
		txt_signed		.setFont(font4);
		txt_totalValue  .setFont(font4);
		hide_prevHash	.setFont(font2);		
		hide_prevIndex 	.setFont(font2);
		hide_sequence 	.setFont(font2);
		hide_witness	.setFont(font2);
		hide_sig		.setFont(font2);
		hide_sigR		.setFont(font2);
		hide_sigS		.setFont(font2);
		hide_pubKey		.setFont(font2);
		hide_pkScript 	.setFont(font2);
		hide_address 	.setFont(font2);
		hide_h160 		.setFont(font2);
		hide_value 		.setFont(font2);
		
		txt_meldungen	.setForeground(Color.RED);
		separator		.setForeground(SystemColor.inactiveCaption);
		txt_meldungen	.setBackground(SystemColor.menu);
	
		btn_rawTx		.setMargin(new Insets(0,0,0,0));
		btn_JSON		.setMargin(new Insets(0,0,0,0));
		btn_qr			.setMargin(new Insets(0,0,0,0));
		
		txt_txHash		.setText(Convert.byteArrayToHexString(Convert.swapBytesCopy(tx.getTxHash())));		
		txt_version		.setText(String.valueOf(tx.getVersion()));	
		txt_isWitness	.setText(String.valueOf(tx.getWitness().length!=0));	
		txt_size		.setText(String.valueOf(tx.size()));	
		txt_vSize		.setText(String.valueOf(tx.getVsize()));
		txt_lockTime	.setText(Convert.byteArrayToHexString(tx.getLockTime()));
		txt_signed		.setText(String.valueOf(tx.isSigned()));													
		
		txt_meldungen	.setEditable(false);
		txt_txHash		.setEditable(false);
		txt_version		.setEditable(false);
		txt_isWitness	.setEditable(false);
		txt_size		.setEditable(false);
		txt_vSize		.setEditable(false);
		txt_lockTime	.setEditable(false);
		txt_totalValue	.setEditable(false);
		txt_signed		.setEditable(false);
		
		txt_vSize		.setVisible(tx.isSigned()); // wird nur bei Signierten Tx angezeigt.
	
		txt_txHash		.setColumns(10);
		txt_version		.setColumns(10);
		txt_isWitness	.setColumns(10);
		txt_size		.setColumns(10);
		txt_vSize		.setColumns(10);
		txt_lockTime	.setColumns(10);
		txt_totalValue	.setColumns(10);
		txt_signed		.setColumns(10);
		
		contentPane		.setLayout(new BorderLayout(0, 0));
		pnl_oben		.setLayout(null);
		pnl_hide		.setLayout(null);

		hide_prevHash	.setState(true);
		hide_prevIndex 	.setState(true);	
		hide_sig	 	.setState(false);
		hide_sigR	 	.setState(false);
		hide_sigS	 	.setState(false);
		hide_pubKey	 	.setState(false);
		hide_sequence 	.setState(true);
		hide_witness	.setState(false);
		hide_pkScript 	.setState(false);
		hide_address 	.setState(true);
		hide_h160 		.setState(false);
		hide_value 		.setState(true);
				
		pnl_oben		.setPreferredSize(new Dimension(10, 160));
		splitPane		.setDividerSize(20);
		splitPane		.setDividerLocation(700);
		separator		.setOrientation(SwingConstants.VERTICAL);
		
		contentPane	.add(pnl_oben, BorderLayout.NORTH);
		pnl_oben	.add(txt_txHash);
		pnl_oben	.add(txt_version);
		pnl_oben	.add(txt_isWitness);
		pnl_oben	.add(txt_size);
		pnl_oben	.add(txt_vSize);
		pnl_oben	.add(txt_lockTime);
		pnl_oben	.add(txt_totalValue);
		pnl_oben	.add(txt_signed);
		pnl_oben	.add(btn_rawTx);
		pnl_oben	.add(btn_JSON);
		pnl_oben	.add(btn_qr);
		pnl_oben	.add(pnl_hide);
		pnl_hide	.add(hide_prevHash);
		pnl_hide	.add(hide_prevIndex);
		pnl_hide	.add(hide_sig);
		pnl_hide	.add(hide_sigR);
		pnl_hide	.add(hide_sigS);
		pnl_hide	.add(hide_pubKey);
		pnl_hide	.add(hide_sequence);
		pnl_hide	.add(hide_witness);
		pnl_hide	.add(hide_address);
		pnl_hide	.add(hide_pkScript);
		pnl_hide	.add(hide_h160);
		pnl_hide	.add(hide_value);
		pnl_hide	.add(separator);
		contentPane	.add(splitPane,BorderLayout.CENTER);
		contentPane	.add(txt_meldungen, BorderLayout.SOUTH);

		

		
				
// ------------------------------------------- Generiert alle Panels für die Tx-Eingänge	---------------------------------------------------------------
		int txInCount = tx.getTxInCount();
		Witness ws = null;
		if(tx.getWitness().length!=0) ws = new Witness(tx.getWitness(),0,txInCount); // Witness-Object wird nur erstellt, wenn Witness-Daten vorhanden sind.
		JPanel pnl_txIn = new JPanel();
		JScrollPane 	scrollPaneL 		= new JScrollPane(); 							
		scrollPaneL		.setViewportView(pnl_txIn);
		pnl_txIn.setBorder(new TitledBorder(null, "Input "+txInCount, 	TitledBorder.CENTER, TitledBorder.TOP, font3, Color.gray));
		splitPane.setLeftComponent(scrollPaneL);
		pnl_txIn.setLayout(new BoxLayout(pnl_txIn, BoxLayout.Y_AXIS));

		JPanel[] pnlA_txIn 			= new JPanel[txInCount];
		JTextField[] txt_prevHash 	= new JTextField[txInCount];
		JTextField[] txt_prevIndex 	= new JTextField[txInCount];
		JTextField[] txt_sigName	= new JTextField[txInCount];	
		JTextField[] txt_sig 		= new JTextField[txInCount];
		JTextField[] txt_witness 	= new JTextField[txInCount];	
		JTextField[] txt_sigR		= new JTextField[txInCount];		
		JTextField[] txt_sigS 		= new JTextField[txInCount];		
		JTextField[] txt_pubKey		= new JTextField[txInCount];		
		JTextField[] txt_sequence 	= new JTextField[txInCount];		
		for(int i=0;i<txInCount;i++)
		{
			SigScript ssw 		= null;
			SigScript ss 		= new SigScript(tx.getSigScript()[i]);				// SigScript aus normaler Tx   wird erzeugt
			SigScript ssFinal = ss;													// Finales Signarur Scipt, entweder aus Witness, oder aus Legancy	

			if(ws != null) 
			{
				ssw = new SigScript(ws.getWitnessSignature()[i]);
				if(ssw.getSigR().length==32 || ssw.getSigR().length==33) ssFinal = ssw;	// SigScript kommt aus Witness-Date
			}		

			if(ss .getSigR().length==32 || ss .getSigR().length==33) ssFinal = ss;	// SigScript kommt aus Legancy SigFeld
	
			pnlA_txIn[i] = new JPanel();
			pnl_txIn.add(pnlA_txIn[i]);
			pnlA_txIn[i].setLayout(new BoxLayout(pnlA_txIn[i], BoxLayout.X_AXIS));	
			pnlA_txIn[i].setMaximumSize(new Dimension(20000,39));
					
			txt_prevHash[i] = new JTextField();
			txt_prevHash[i].setEditable(false);
			txt_prevHash[i].setFont(font4);
			txt_prevHash[i].setMaximumSize(new Dimension(465,40));
			txt_prevHash[i].setBorder(new TitledBorder(null, "prev Hash", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
			txt_prevHash[i].setText(Convert.byteArrayToHexString(tx.getTxPrevHash()[i]));
			pnlA_txIn[i].add(txt_prevHash[i]);
				
			txt_prevIndex[i] = new JTextField();
			txt_prevIndex[i].setEditable(false);
			txt_prevIndex[i].setFont(font4);
			txt_prevIndex[i].setPreferredSize(new Dimension(65,40));
			txt_prevIndex[i].setMaximumSize(new Dimension(65,40));
			txt_prevIndex[i].setBorder(new TitledBorder(null, "prev Index",	TitledBorder.LEADING, TitledBorder.TOP, font1, Color.gray));
			txt_prevIndex[i].setText(String.valueOf(tx.getTxPrevIndex()[i]));
			pnlA_txIn[i].add(txt_prevIndex[i]);

			txt_sigName[i] = new JTextField();
			txt_sigName[i].setEditable(false);
			txt_sigName[i].setFont(font4);
			txt_sigName[i].setVisible(false);
			txt_sigName[i].setBorder(new TitledBorder(null, "name",	TitledBorder.LEADING, TitledBorder.TOP, font1, Color.gray));
			//txt_sigName[i].setText(ss.getName());
			txt_sigName[i].setMaximumSize(txt_sigName[i].getPreferredSize());
			pnlA_txIn[i].add(txt_sigName[i]);
					
			txt_sig[i] = new JTextField();
			txt_sig[i].setEditable(false);
			txt_sig[i].setFont(font4);
			txt_sig[i].setVisible(false);
			txt_sig[i].setBorder(new TitledBorder(null, "signature",	TitledBorder.LEADING, TitledBorder.TOP, font1, Color.gray));
			txt_sig[i].setText(Convert.byteArrayToHexString(tx.getSigScript()[i]));
			txt_sig[i].setMaximumSize(txt_sig[i].getPreferredSize());
			pnlA_txIn[i].add(txt_sig[i]);
				
			txt_witness[i] = new JTextField();
			txt_witness[i].setEditable(false);
			txt_witness[i].setFont(font4);
			txt_witness[i].setVisible(false);
			txt_witness[i].setBorder(new TitledBorder(null, "witness signature",	TitledBorder.LEADING, TitledBorder.TOP, font1, Color.gray));
			if(ws != null) txt_witness[i].setText(Convert.byteArrayToHexString(ws.getWitnessSignature()[i]));
			txt_witness[i].setMaximumSize(txt_witness[i].getPreferredSize());
			pnlA_txIn[i].add(txt_witness[i]);
			
			txt_sigR[i] = new JTextField();
			txt_sigR[i].setEditable(false);
			txt_sigR[i].setFont(font4);
			txt_sigR[i].setVisible(false);
			txt_sigR[i].setBorder(new TitledBorder(null, "signature R",	TitledBorder.LEADING, TitledBorder.TOP, font1, Color.gray));
			txt_sigR[i].setText(Convert.byteArrayToHexString(ssFinal.getSigR()));
			txt_sigR[i].setMaximumSize(txt_sigR[i].getPreferredSize());
			pnlA_txIn[i].add(txt_sigR[i]);
			
			txt_sigS[i] = new JTextField();
			txt_sigS[i].setEditable(false);
			txt_sigS[i].setFont(font4);
			txt_sigS[i].setVisible(false);
			txt_sigS[i].setBorder(new TitledBorder(null, "signature S",	TitledBorder.LEADING, TitledBorder.TOP, font1, Color.gray));
			txt_sigS[i].setText(Convert.byteArrayToHexString(ssFinal.getSigS()));
			txt_sigS[i].setMaximumSize(txt_sigS[i].getPreferredSize());
			pnlA_txIn[i].add(txt_sigS[i]);
			
			txt_pubKey[i] = new JTextField();
			txt_pubKey[i].setEditable(false);
			txt_pubKey[i].setFont(font4);
			txt_pubKey[i].setVisible(false);
			txt_pubKey[i].setBorder(new TitledBorder(null, "public Key",	TitledBorder.LEADING, TitledBorder.TOP, font1, Color.gray));
			txt_pubKey[i].setText(Convert.byteArrayToHexString(ssFinal.getPubKey()));
			txt_pubKey[i].setMaximumSize(txt_pubKey[i].getPreferredSize());
			pnlA_txIn[i].add(txt_pubKey[i]);
						
			txt_sequence[i] = new JTextField();
			txt_sequence[i].setEditable(false);
			txt_sequence[i].setFont(font4);
			txt_sequence[i].setMaximumSize(new Dimension(90,40));
			txt_sequence[i].setBorder(new TitledBorder(null, "sequence",	TitledBorder.LEADING, TitledBorder.TOP, font1, Color.gray));
			txt_sequence[i].setText(Convert.byteArrayToHexString(tx.getSequence()[i]));
			pnlA_txIn[i].add(txt_sequence[i]);
		}
		
	
		
		
// ------------------------------------------- Generiert alle Panels für die Tx-Ausgänge	---------------------------------------------------------------
		long totalValue = 0;
		int txOutCount = tx.getTxOutCount();
		JPanel pnl_txOut = new JPanel();
		JScrollPane 	scrollPaneR 		= new JScrollPane(); 							
		scrollPaneR		.setViewportView(pnl_txOut);
		pnl_txOut.setBorder(new TitledBorder(null, "Output "+txOutCount, 	TitledBorder.CENTER, TitledBorder.TOP, font3, Color.gray));
		splitPane.setRightComponent(scrollPaneR);
		pnl_txOut.setLayout(new BoxLayout(pnl_txOut, BoxLayout.PAGE_AXIS));
		JPanel[] pnlA_txOut 		= new JPanel[txOutCount];
		JTextField[] txt_addr 		= new JTextField[txOutCount];
		JTextField[] txt_scriptName = new JTextField[txOutCount];
		JTextField[] txt_pkScript	= new JTextField[txOutCount];
		JTextField[] txt_h160 		= new JTextField[txOutCount];
		JTextField[] txt_value 		= new JTextField[txOutCount];
		for(int i=0;i<txOutCount;i++)
		{
			PkScript pk = new PkScript(tx.getPkScript()[i]);				
			pnlA_txOut[i] = new JPanel();
			pnl_txOut.add(pnlA_txOut[i]);
			pnlA_txOut[i].setLayout(new BoxLayout(pnlA_txOut[i], BoxLayout.X_AXIS));		
			pnlA_txOut[i].setMaximumSize(new Dimension(20000,39));
			
			txt_addr[i] = new JTextField();
			txt_addr[i].setEditable(false);
			txt_addr[i].setFont(font4);
			txt_addr[i].setBorder(new TitledBorder(null, "address", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
			txt_addr[i].setText(tx.getBitcoinAddr(magic)[i]);
			txt_addr[i].setMaximumSize(new Dimension(400,40));
			pnlA_txOut[i].add(txt_addr[i]);
			
			txt_scriptName[i] = new JTextField();
			txt_scriptName[i].setEditable(false);
			txt_scriptName[i].setFont(font4);
			txt_scriptName[i].setMaximumSize(new Dimension(70,40));
			txt_scriptName[i].setBorder(new TitledBorder(null, "name", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
			txt_scriptName[i].setText(pk.getName());
			txt_scriptName[i].setVisible(false);
			pnlA_txOut[i].add(txt_scriptName[i]);
			
			txt_pkScript[i] = new JTextField();
			txt_pkScript[i].setEditable(false);
			txt_pkScript[i].setFont(font4);
			txt_pkScript[i].setBorder(new TitledBorder(null, "Pk-Script", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
			txt_pkScript[i].setText(Convert.byteArrayToHexString(tx.getPkScript()[i]));
			txt_pkScript[i].setMaximumSize(txt_pkScript[i].getPreferredSize()); 
			txt_pkScript[i].setVisible(false);
			pnlA_txOut[i].add(txt_pkScript[i]);
				
			txt_h160[i] = new JTextField();
			txt_h160[i].setEditable(false);
			txt_h160[i].setFont(font4);
			txt_h160[i] .setMinimumSize(new Dimension(40,40));
			txt_h160[i].setMaximumSize(new Dimension(320,40));
			txt_h160[i].setBorder(new TitledBorder(null, "Ripemd-160", 	TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));
			try {txt_h160[i].setText(Convert.byteArrayToHexString(pk.getHash160()));} 
			catch (Exception e1){txt_h160[i].setText(e1.getMessage());}
			txt_h160[i].setVisible(false);
			pnlA_txOut[i].add(txt_h160[i]);
					
			txt_value[i] = new JTextField();
			txt_value[i].setEditable(false);
			txt_value[i].setFont(font4);
			txt_value[i].setMaximumSize(new Dimension(100,40));
			txt_value[i].setBorder(new TitledBorder(null, "value", 		TitledBorder.LEADING, TitledBorder.TOP, font2, Color.gray));			
			txt_value[i].setText(new DecimalFormat("0.00000000").format((double)tx.getValue()[i]/100000000)); 
			totalValue = totalValue + tx.getValue()[i];
			pnlA_txOut[i].add(txt_value[i]);
		}
		txt_totalValue.setText(new DecimalFormat("0.00000000").format((double)totalValue/100000000));		
		
		
		
	
	
	
// ------------------------------------------------------------------ Action Listeners ---------------------------------------------------------------------------------	
	
		// Zeigt die Raw-Tx in einem eigenem Dialog an.
		btn_rawTx.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JDialog d = new JDialog();
				d.setTitle("RAW Transaction");
				JTextArea txt = new JTextArea(Convert.byteArrayToHexString(tx.getRawTx()));
				txt.setLineWrap(true);
				txt.setEditable(false);
				d.setBounds(getX()+20, getY()+20, 1200, (tx.size()/5)+50);
				d.getContentPane().add(txt);
				d.setModal(true);
				d.setVisible(true);
			}
		});
		
		
		// Zeigt die Tx als JSON in einem eigenem Dialog an.
		btn_JSON.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				JDialog d = new JDialog();
				d.setTitle("JSON Transaction");
				JScrollPane sp = new JScrollPane();
				JTextArea txt = new JTextArea();
				try {txt.setText(tx.toString(magic));} 
				catch (JSONException e1) {e1.printStackTrace();}			
				txt.setLineWrap(true);
				txt.setEditable(false);		
				sp.setViewportView(txt);;
				d.setBounds(getX()+20, getY()+20, 1200,600);
				d.getContentPane().add(sp);
				d.setModal(true);
				d.setVisible(true);
			}
		});
		
		
		
		
		
		
		// Gibt die TX als QR-Code in einem eigenem Dialog aus.
		btn_qr.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					QRCodeZXING.printQR_on_JDialog(Convert.byteArrayToHexString(tx.getRawTx()), "Tx: "+Convert.byteArrayToHexString(tx.getTxID()), Color.white, Color.black, x+755, y+70);
				} 
				catch (Exception e1) {e1.printStackTrace();}
			}
		});
		
		
		
		
	
	
		// Blendet gewählte Spalten Ein-Aus
		ItemListener ml = new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				if(hide_prevHash.getState()){for(int i=0;i<tx.getTxInCount();i++) txt_prevHash[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxInCount();i++) txt_prevHash[i].setVisible(false);}
				
				if(hide_prevIndex.getState()){for(int i=0;i<tx.getTxInCount();i++) txt_prevIndex[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxInCount();i++) txt_prevIndex[i].setVisible(false);}
				
				if(hide_sig.getState()) 	{for(int i=0;i<tx.getTxInCount();i++) {txt_sig[i].setVisible(true); txt_sigName[i].setVisible(true);}}
				else						{for(int i=0;i<tx.getTxInCount();i++) {txt_sig[i].setVisible(false);txt_sigName[i].setVisible(false);}}
				
				if(hide_witness.getState()) {for(int i=0;i<tx.getTxInCount();i++) txt_witness[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxInCount();i++) txt_witness[i].setVisible(false);}
		
				if(hide_sigR.getState()) 	{for(int i=0;i<tx.getTxInCount();i++) txt_sigR[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxInCount();i++) txt_sigR[i].setVisible(false);}
				
				if(hide_sigS.getState()) 	{for(int i=0;i<tx.getTxInCount();i++) txt_sigS[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxInCount();i++) txt_sigS[i].setVisible(false);}
				
				if(hide_pubKey.getState()) 	{for(int i=0;i<tx.getTxInCount();i++) txt_pubKey[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxInCount();i++) txt_pubKey[i].setVisible(false);}
				
				if(hide_sequence.getState()){for(int i=0;i<tx.getTxInCount();i++) txt_sequence[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxInCount();i++) txt_sequence[i].setVisible(false);}
							
				if(hide_pkScript.getState()){for(int i=0;i<tx.getTxOutCount();i++) {txt_scriptName[i].setVisible(true); txt_pkScript[i].setVisible(true);}}
				else						{for(int i=0;i<tx.getTxOutCount();i++) {txt_scriptName[i].setVisible(false);txt_pkScript[i].setVisible(false);}}
				
				if(hide_address.getState()) {for(int i=0;i<tx.getTxOutCount();i++) txt_addr[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxOutCount();i++) txt_addr[i].setVisible(false);}
				
				if(hide_h160.getState()) 	{for(int i=0;i<tx.getTxOutCount();i++) txt_h160[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxOutCount();i++) txt_h160[i].setVisible(false);}
				
				if(hide_value.getState())   {for(int i=0;i<tx.getTxOutCount();i++) txt_value[i].setVisible(true); }
				else						{for(int i=0;i<tx.getTxOutCount();i++) txt_value[i].setVisible(false);}
				
				splitPane.validate();		
			}
		};
		hide_prevHash	.addItemListener(ml);
		hide_prevIndex	.addItemListener(ml);
		hide_sig		.addItemListener(ml);
		hide_witness	.addItemListener(ml);
		hide_sigR		.addItemListener(ml);
		hide_sigS		.addItemListener(ml);
		hide_pubKey		.addItemListener(ml);
		hide_sequence	.addItemListener(ml);
		hide_pkScript	.addItemListener(ml);
		hide_address	.addItemListener(ml);
		hide_h160		.addItemListener(ml);
		hide_value		.addItemListener(ml);
	}
	
	
	
	
	//---------------------------------------------------------- Hilfs Methoden ---------------------------------------------------------------
	
	
	// Konvertiert ein Base64 String in ein ImageIcon
	private static ImageIcon Base64StringToImageIcon(String str) 
	{
		try
		{
			byte[] imageBytes = Base64.getDecoder().decode(str);
			Image image = ImageIO.read(new ByteArrayInputStream(imageBytes));
			return new ImageIcon(image);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		return null;
	}		
}