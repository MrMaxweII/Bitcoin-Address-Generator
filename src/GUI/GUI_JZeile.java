package GUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import CoinGen.MyIcons;
import lib3001.btc.Transaktion;
import lib3001.btc.TxPrinter;
import lib3001.crypt.Convert;
import lib3001.java.Hover;
import lib3001.network.CoinParameter;
import lib3001.qrCode.QRCodeZXING;



/********************************************************************************************************
*	V1.0							Mr.Maxwell							17.01.2026						*
*																										*
*	Es handelt sich um ein JPanel mit einer einzigen Zeile mit einer Transaktion.						*
*	Diese Zeile wird dem pnl_zentrum dynamisch mehrfach hinzugefügt/angehängt.							*
*	Diese Zeile behinhaltet die Unsignierte Tx und später die sig.tx.									*
*	Alle Icons und Listener jeder Zeile werden hier vorab registiert.									*
*	Im Programm werden dynamisch die Elemente mit setVisible() aktiviert oder deaktiviert				*
*	Diese Klasse ist nicht static! 																		*
*	Es werden mit dem Konstruktor dynamisch mehrere Zeilen-Instanzen erstellt. 							*
*	Es gibt Methoden mit denen Transaktions-Daten z.b: SigTx später übergeben werden.					*
*********************************************************************************************************/




class GUI_JZeile extends JPanel
{
		JLabel		lbl_uTx		= new JLabel(MyIcons.hex);				// Label der Unsignierten Tx
		JLabel		lbl_sigTx	= new JLabel(MyIcons.hex_green);		// Label der signierten Tx
		JLabel		lbl_qr		= new JLabel(MyIcons.qrCode_green);		// Das Label mit nur einem grünem QR-Code, zeigt den QR-Code der sig.Tx an.
		JLabel		lbl_save	= new JLabel(MyIcons.save_green);		// Das Label als grünen Save-Butten
		JLabel		lbl_load_gif= new JLabel(MyIcons.load_gif);			// Das Animations-Lade gif. drehender Bitcoin, während die Tx signiert wird.
		JLabel		lbl_error	= new JLabel(MyIcons.error);			// Das Error-Label
		String		err			= new String(""); 						// Die vollständige Fehlermeldung im Fehlerfall
		Transaktion	uTx;												// Unsignierte Tx wird hier inizialisiert
		Transaktion	sigTx;												// Signierte Tx wird hier inizialisiert
	
		
		
	/** Dem Konstruktor muss die Unsignierte Transaktion übergeben werden **/
	GUI_JZeile(Transaktion uTx)
	{
		super();	
		this.uTx =uTx;
		
		lbl_sigTx	.setVisible(false);
		lbl_qr		.setVisible(false);
		lbl_save	.setVisible(false);
		lbl_load_gif.setVisible(false);
		lbl_error	.setVisible(false);
	
		Hover.addBackground(lbl_uTx);
		Hover.addBackground(lbl_sigTx);
		Hover.addBackground(lbl_qr);
		Hover.addBackground(lbl_save);
		Hover.addBackground(lbl_error);
		
		lbl_uTx  .setFont(new Font("Ubuntu Mono",Font.PLAIN,14));
		lbl_sigTx.setFont(new Font("Ubuntu Mono",Font.PLAIN,11));
		lbl_error.setFont(new Font("Ubuntu Mono",Font.BOLD,14));
		lbl_sigTx.setForeground(new Color(11,130,2));
		lbl_error.setForeground(Color.red);
		lbl_error.setText("   Error                                                            ");

		lbl_uTx		.setToolTipText(GUI.t.t("Unsignierte Transaktion anzeigen"));
		lbl_qr		.setToolTipText(GUI.t.t("Signierte Transaktion als QR-Code anzeigen"));
		lbl_save	.setToolTipText(GUI.t.t("Signierte Transaktion Speichern"));
		lbl_sigTx	.setToolTipText(GUI.t.t("Signierte Transaktion anzeigen"));
		lbl_error	.setToolTipText(GUI.t.t("Fehlermeldung anzeigen"));
		
		this.setOpaque(true);
		this.setBackground(new Color(GUI.color1.getRed()-10, GUI.color1.getGreen()-20, GUI.color1.getBlue()-30));
		this.setMaximumSize(new Dimension(32768,50));
		this.setBorder(new LineBorder(new Color(247, 147, 26)));
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		this.add(lbl_uTx);	
		this.add(lbl_load_gif);
		this.add(Box.createHorizontalGlue());
		this.add(lbl_save);						
		this.add(Box.createHorizontalStrut(5));	
		this.add(lbl_qr);							
		this.add(Box.createHorizontalStrut(2));	
		this.add(lbl_sigTx);						
		this.add(Box.createHorizontalStrut(10));	
		this.add(lbl_error);						
		
		// UsigTx wird mit Tx-Builder angeschaut.
		lbl_uTx.addMouseListener(new MouseAdapter() 
		{
			public void mouseReleased(MouseEvent e) 
			{				
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					try
					{
						GUI.txt_ausgabe.setText("");
						CoinParameter cp = CoinParameter.getFromSymbol((String) GUI.comboBox_coin.getSelectedItem());
						TxPrinter txPrinter = new TxPrinter(cp.magic, uTx, GUI.frame.getX()+5, GUI.frame.getY()+30);	
						if(GUI.comboBox_coin.getSelectedIndex()==0) txPrinter.setIconImage(MyIcons.bitcoinLogoMain.getImage());
						else 										txPrinter.setIconImage(MyIcons.bitcoinLogoTest.getImage());
						txPrinter.setVisible(true);
					}
					catch(Exception ex) 
					{	
						GUI.txt_ausgabe.setForeground(Color.RED);
						GUI.txt_ausgabe.setText("Tx Error: No correct transaction!");
						ex.printStackTrace();
					}
				}
			}
		});	
		// sigTx wird mit Tx-Builder angeschaut.
		lbl_sigTx.addMouseListener(new MouseAdapter() 
		{
			public void mouseReleased(MouseEvent e) 
			{				
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					try
					{							
						GUI.txt_ausgabe.setText("");
						CoinParameter cp = CoinParameter.getFromSymbol((String) GUI.comboBox_coin.getSelectedItem());
						TxPrinter txPrinter = new TxPrinter(cp.magic, sigTx, GUI.frame.getX()+5, GUI.frame.getY()+30);
						if(GUI.comboBox_coin.getSelectedIndex()==0) txPrinter.setIconImage(MyIcons.bitcoinLogoMain.getImage());
						else 										txPrinter.setIconImage(MyIcons.bitcoinLogoTest.getImage());
						txPrinter.setVisible(true);
					}
					catch(Exception ex) 
					{	
						GUI.txt_ausgabe.setForeground(Color.RED);
						GUI.txt_ausgabe.setText("Tx Error: No correct transaction!");
						ex.printStackTrace();
					}
				}
			}
		});		
		// Signierte Transaktion wird als QR-Code ausgegeben 
		lbl_qr.addMouseListener(new MouseAdapter() 
		{
			public void mouseReleased(MouseEvent e) 
			{				
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					try
					{	
						GUI.txt_ausgabe.setText("");
						QRCodeZXING.printQR_on_JDialog(Convert.byteArrayToHexString(sigTx.getRawTx()), "Sig.TxID: "+Convert.byteArrayToHexString(sigTx.getTxID()), GUI.color1, Color.black, GUI.frame.getX()+150, GUI.frame.getY()+5);
					}
					catch(Exception ex) 
					{	
						GUI.txt_ausgabe.setForeground(Color.RED);
						GUI.txt_ausgabe.setText(ex.getMessage());
						ex.printStackTrace();
					}
				}
			}
		});	
		// Signierte Transaktion wird gespeichert
		lbl_save.addMouseListener(new MouseAdapter() 
		{
			public void mouseReleased(MouseEvent e) 
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					try
					{
						GUI.txt_ausgabe.setText("");
						String str = Convert.byteArrayToHexString(sigTx.getRawTx());
						JFileChooser chooser = new JFileChooser(GUI_SignTx.lbl_file_uTx.getText());
						FileFilter filter = new FileNameExtensionFilter(".txn", "txn");
						chooser.setFileFilter(filter);	
						chooser.setAcceptAllFileFilterUsed(false);
						chooser.setSelectedFile(new File(Convert.byteArrayToHexString(sigTx.getTxID())+".txn"));
						int button = chooser.showSaveDialog(lbl_save);
						if(button==0)																					
						{
							String file = chooser.getSelectedFile().getAbsolutePath();	
							GUI_SignTx.lbl_file_uTx.setText(chooser.getSelectedFile().getParent());	
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
			}
		});	
		// Bei einem Fehler wird dieser Listener im Error-Feld ein JOptionPane mit der Fehlermeldung anzeigen.
		lbl_error.addMouseListener(new MouseAdapter() 
		{
			public void mouseReleased(MouseEvent e2) 
			{				
				if (e2.getButton() == MouseEvent.BUTTON1)
				{											 				
					JOptionPane.showMessageDialog(lbl_error,err, "Error", JOptionPane.ERROR_MESSAGE);  
				}
			}
		});
	}
	
	
	// Dateiname der Unsignierten Tx muss hier festgelegt werden.
	void setUtxFileName(String fileName)
	{
		lbl_uTx.setText(fileName);			
	}
	
	// Signierte Transaktion wird hier später übergeben
	void setSigTx(Transaktion sigTx)
	{
		this.sigTx = sigTx;
	}	
}
