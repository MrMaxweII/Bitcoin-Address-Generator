package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import CoinGen.MyIcons;
import lib3001.btc.PkScript;
import lib3001.btc.PrvKey;
import lib3001.btc.Transaktion;
import lib3001.btc.TxSigniererLegancy;
import lib3001.btc.TxSigniererWitness;
import lib3001.crypt.Calc;
import lib3001.crypt.Convert;
import lib3001.java.Hover;
import lib3001.network.CoinParameter;
import lib3001.qrCode.QrCapture;
import seedExtractor.SeedExtractor;



/************************************************************************************
*	V1.1							Mr.Maxwell							29.01.2026	*
*	Die GUI für den Tab Sign Transaktion											*
************************************************************************************/



public class GUI_SignTx extends JPanel
{

	
	public static JPanel 		pnl_oben 		= new JPanel();						// Oben
	public static JPanel 		pnl_unten 		= new JPanel();						// Unten
	public static MyDropPanel 	pnl_zentrum 	= new MyDropPanel();				// Zentrums-Panel, mit allen Tx. Dies ist ein JPanel, mit der Erweiterung des Drop-Info-Text im Hintergrund.
	public static JLabel 		lbl_file_uTx 	= new JLabel();						// Label des Speicherorts der Unsign.TX
	public static JScrollPane 	scrollPanel 	= new JScrollPane();				// ScrollPanel für das Zentrum.
	public static JButton 		btn_scanQR 		= new JButton(MyIcons.qrScan);		// Tx mit der Kamera als QR-Code Scanen
	public static JButton		btn_fromHex		= new JButton(MyIcons.hex);			// Tx als Hex String in ein Eingabefeld kopieren.
	public static JButton 		btn_open_uTx 	= new JButton(MyIcons.loadTx);		// Tx Datei vom File-System laden
	public static JButton 		btn_delete 		= new JButton(MyIcons.delete);		// Löscht alle Zeilen, also das gesamte pnl_zentrum
	public static JButton 		btn_sig 		= new JButton(MyIcons.key);			// Alle Signieren
	public static JButton 		btn_autoSig 	= new JButton(MyIcons.key);			// Automatisches Signieren eines ganzen Verzeichnisses
	public static JButton 		btn_saveAllSigTx= new JButton(MyIcons.save);		// Alle signierten Tx speichern
	public static JComboBox		cBox_sigMethode	= new JComboBox();					// Auswahl der Signatur-Methode mit Seed oder mit Private-Keys
	private static byte[][] 	privateKeys;  										// Array der Private-Keys 
    public static String 		dragText 		= "Elemente hier hineinziehen";		// Der große Drag and Drop Informations-Text in der Mitte 
	
	
	
	
	
	// Konstruktor
	public GUI_SignTx()
	{
		lbl_file_uTx	.setText("user.dir");	
		
		scrollPanel		.setBorder(new EmptyBorder(8,8,8,8));
		pnl_zentrum		.setBorder(BorderFactory.createDashedBorder(Color.GRAY,1.5f, 5f, 5f, false)); // Gestrichelter Rand
		pnl_oben		.setBorder(new EmptyBorder(6,8,2,8));
		pnl_unten		.setBorder(new EmptyBorder(0,2,4,8));
		cBox_sigMethode	.setBorder(new TitledBorder(new EmptyBorder(1,1,1,1), "Sig Methode",TitledBorder.LEADING, TitledBorder.TOP, GUI.font2, Color.GRAY)); // Wird nach config-Load noch einmal überschrieben!
		cBox_sigMethode	.setModel(new DefaultComboBoxModel(new String[] {"Seed","Private Keys"}));

		btn_sig			.setForeground(Color.red);
		btn_autoSig		.setForeground(Color.red);
		this			.setBackground(GUI.color1);
		pnl_oben		.setBackground(GUI.color1);
		pnl_unten		.setBackground(GUI.color1);
		lbl_file_uTx	.setBackground(GUI.color1);	
		cBox_sigMethode	.setBackground(GUI.color1);
		scrollPanel		.setBackground(GUI.color1);
		pnl_zentrum		.setBackground(new Color(240,240,240));
	
		lbl_file_uTx	.setFont(new Font(null, Font.PLAIN, 10));
		btn_scanQR		.setFont(GUI.font2);
		btn_fromHex		.setFont(GUI.font2);
		btn_open_uTx	.setFont(GUI.font2);
		btn_delete		.setFont(GUI.font2);
		btn_saveAllSigTx.setFont(GUI.font2);
		btn_sig			.setFont(new Font("DejaVu Sans", Font.PLAIN, 13));
		btn_autoSig		.setFont(new Font("DejaVu Sans", Font.PLAIN, 13));
		
		this			.setLayout(new BorderLayout(0, 0));
		pnl_oben		.setLayout(new BoxLayout(pnl_oben,    BoxLayout.X_AXIS));
		pnl_unten		.setLayout(new BoxLayout(pnl_unten,   BoxLayout.X_AXIS));
		pnl_zentrum		.setLayout(new BoxLayout(pnl_zentrum, BoxLayout.Y_AXIS));

		btn_scanQR		.setMargin(new Insets(0, 0, 0, 0));
		btn_fromHex		.setMargin(new Insets(0, 0, 0, 0));
		btn_open_uTx	.setMargin(new Insets(0, 0, 0, 0));	
		btn_delete		.setMargin(new Insets(0, 0, 0, 0));	
		btn_saveAllSigTx.setMargin(new Insets(0, 10, 0, 10));
		btn_sig			.setMargin(new Insets(0, 30, 0, 30));	
		btn_autoSig		.setMargin(new Insets(0, 10, 0, 10));	
		
		btn_scanQR		.setPreferredSize(new Dimension(125, 30));
		btn_fromHex		.setPreferredSize(new Dimension(125, 30));
		btn_open_uTx	.setPreferredSize(new Dimension(125, 30));
		btn_delete		.setPreferredSize(new Dimension(29,  30));
		
		cBox_sigMethode	.setMaximumSize(new Dimension(300, 32767));
		lbl_file_uTx	.setMinimumSize(new Dimension(100, 20));

		btn_sig			.setVisible(false);
		btn_autoSig		.setVisible(true);
		btn_saveAllSigTx.setVisible(false);
		
		btn_sig			.setIconTextGap(10);
		btn_autoSig		.setIconTextGap(10);
		btn_saveAllSigTx.setIconTextGap(10);
				
		Hover.addBackground(cBox_sigMethode);
		
		scrollPanel		.setViewportView(pnl_zentrum);

		this			.add(pnl_oben,    BorderLayout.NORTH);
		this			.add(scrollPanel, BorderLayout.CENTER);	
		this			.add(pnl_unten,   BorderLayout.SOUTH);
		pnl_oben		.add(btn_scanQR);
		pnl_oben		.add(Box.createHorizontalStrut(5));
		pnl_oben		.add(btn_fromHex);
		pnl_oben		.add(Box.createHorizontalStrut(5));
		pnl_oben		.add(btn_open_uTx);
		pnl_oben		.add(Box.createHorizontalStrut(5));
		pnl_oben		.add(lbl_file_uTx);
		pnl_oben		.add(Box.createHorizontalGlue());
		pnl_oben		.add(btn_delete);
		pnl_unten		.add(cBox_sigMethode);
		pnl_unten		.add(Box.createHorizontalStrut(5));
		pnl_unten		.add(btn_sig);
		pnl_unten		.add(btn_autoSig);
		pnl_unten		.add(Box.createHorizontalGlue());
		pnl_unten		.add(btn_saveAllSigTx);


	
	
	
			
		
		
// ------------------------------------------------------------------ Action Listeners Unsignierte Transaktinen importieren ---------------------------------------------------------------------------	
		
		
		// Unsignierte Tx per Drag and Drop in das Textfeld ziehen
		new DropTarget(pnl_zentrum, new DropTargetAdapter() 
		{
			public void drop(DropTargetDropEvent event) 
			{
				try 
				{
					event.acceptDrop(DnDConstants.ACTION_COPY);					
					Transferable transferable = event.getTransferable();
					List<File> list = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);					
					for(int i=0; i<list.size();i++)
					{		
						File file = list.get(i);			
						lbl_file_uTx.setText(file.getParent());	
						if (file.getName().endsWith(".txn")) 								// Lässt nur .txn Datein zu.
						{
							BufferedReader br = new BufferedReader(new FileReader(file));
							String str = "";
							while(br.ready()) str = str +br.readLine();	
							br.close();								
							addTx(file.getName(),str);
						}	
						else
						{
							GUI.txt_ausgabe.setForeground(Color.RED);
							GUI.txt_ausgabe.setText(GUI.t.t("Only .txn files allowed!"));
						}
					}	
				} 
				catch (Exception e) {e.printStackTrace();}
			}
		});
			

		
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
								GUI.txt_ausgabe.setText("");
								GUI.frame.setEnabled(false);
								QrCapture qr = new QrCapture(null,"Scan unsign. Transaction", GUI.frame.getX()+50, GUI.frame.getY()+80);	
								String p2 = qr.getResult();
								qr.close();								
								if(p2.equals("")) throw new IOException("User abort");								
								addTx("unsigTxQR.txn",p2);
							}
							catch(Exception ex) 
							{
								GUI.txt_ausgabe.setForeground(Color.RED);
								GUI.txt_ausgabe.setText(ex.getMessage());
							};	
							GUI.frame.setEnabled(true);
						}
					});
					t.start();			
				}
			});
			
			
			
		// Öffnet ein JOptionPane und die unsig-Tx kann dort als Hex-String eingegeben werden.	
		btn_fromHex.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try
				{			
					GUI.txt_ausgabe.setText("");
					String str = JOptionPane.showInputDialog(pnl_zentrum,GUI.t.t("Imput unsign Transaktion in Hexa"),GUI.t.t("Imput unsign Transaktion in Hexa"),-1);	
					if(str==null || str.length()<=0) return;
					addTx("unsigTx.txn",str);
				}
				catch(Exception ex)
				{
					GUI.txt_ausgabe.setForeground(Color.RED);
					GUI.txt_ausgabe.setText(ex.getMessage());
				}
			}
		});	
			

		
		// Öffnet mit dem JFileChooser eine oder mehrerer Unsig. Transatkon.
		// Multi-Selection ist aktiv, es können daher mehrere Tx gleichzeitig geladen werden
		btn_open_uTx.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{			
				GUI.txt_ausgabe.setText("");
				String userDir = System.getProperty("user.home");			
				JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
				chooser.setMultiSelectionEnabled(true);
				chooser.setFileFilter(new FileNameExtensionFilter("unsigned.txn", "txn"));			
				chooser.setCurrentDirectory(new File(lbl_file_uTx.getText()));							
				int button = chooser.showOpenDialog(pnl_zentrum);		
				if(button==0)																					
				{
					File[] file = chooser.getSelectedFiles();
					lbl_file_uTx.setText(file[0].getParent());	
					for(int i=0; i<file.length;i++)
					{
						try 
						{
							BufferedReader br = new BufferedReader(new FileReader(file[i].getAbsolutePath()));
							String str = "";
							while(br.ready()) str = str +br.readLine();	
							br.close();		
							addTx(file[i].getName(),str);
							GUI.txt_ausgabe.setText("");
						} 
						catch (Exception e1) 
						{
							GUI.txt_ausgabe.setForeground(Color.RED);
							GUI.txt_ausgabe.setText("File Error: No correct transaction!");
						}
					}		
				}			
			}
		});
		
			
	
		// Löscht alle Zeilen, also das ganze pnl_zentrum
		btn_delete.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				pnl_zentrum.removeAll();
				btn_sig	.setVisible(false);
				btn_autoSig	.setVisible(true);
				btn_saveAllSigTx.setVisible(false);
				GUI.frame.repaint();
				
			}
		});
		
		
		
		
		
	
		
		
		
// ------------------------------------------------------------------ Action Listeners Tx Signieren ---------------------------------------------------------------------------	
	
		
 // Auslösendes Ereignis zum Automatischen Signieren
 // Das Automatische Signieren signiert alle Transaktionen die sich in einem Verzeichnis befinden
 // 1. Zunächst werden Bedingungen geprüft (Seed importiert, oder SigMethode auf Private-Keys)
 // 2. Der FileChooser wird geöffnet, zur Auswahl des Verzeichnisses mit unsignieren Transaktionen
 // 3. Alle Dateien mit der Dateiendung: .tnx in diesem Verzeichnis werden als unsig.Tx automatisch importiert (Fehlerhafte Tx werden ignoriert und übersprungen)
 // 4. Der Signaturvorgang startet automatisch.
 // 5. Nach Abschluss werden alle erfolgreich signierten Tx automatisch im selben Verzeichnis gespeichert.
 // 6. Abschluss-Meldung wird angezeigt.
	btn_autoSig.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(SeedExtractor.privKeys == null && cBox_sigMethode.getSelectedIndex()==0)
			{
				JOptionPane.showMessageDialog(pnl_zentrum,GUI.t.t("No seed found!\nA seed must be stored in the “Seed Extractor”!\nOr the “Sig method” must be set to “Private Keys”!"),"No seed found",JOptionPane.ERROR_MESSAGE,MyIcons.noSeed);
				return;
			}		
			GUI.txt_ausgabe.setText("");
			String userDir = System.getProperty("user.home");				
			JFileChooser chooser = new JFileChooser(userDir +"/Desktop");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setCurrentDirectory(new File(lbl_file_uTx.getText()));								
			int button = chooser.showOpenDialog(pnl_zentrum);	
			if(button==0)
			{
				File file = chooser.getSelectedFile();
				lbl_file_uTx.setText(file.getAbsolutePath());				
				File[] files= file.listFiles(new FilenameFilter() // Filtert nur .txn Datein
				{
					public boolean accept(File dir, String name) {return name.endsWith(".txn");}	
				});
				for(int i=0; i<files.length; i++)
				{
					try 
					{						
						BufferedReader br = new BufferedReader(new FileReader(files[i].getAbsolutePath()));
						String str = "";
						while(br.ready()) str = str +br.readLine();	
						br.close();		
						addTx(files[i].getName(),str);
						GUI.txt_ausgabe.setText("");
					} 
					catch (Exception e1) 
					{
						GUI.txt_ausgabe.setForeground(Color.RED);
						GUI.txt_ausgabe.setText("File Error: No correct transaction!");
					}			
				}

				SwingUtilities.invokeLater(new Runnable() // Zur syncronisierung wichtig!
				{
					public void run() 
					{
						sigAllTx();		
					}
				});
	
				new Thread(new Runnable() // Zur syncronisierung wichtig!
				{
					public void run() 
					{
						while(true)
						{
							try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
							if(btn_autoSig.isEnabled())  break;			
						}
						SwingUtilities.invokeLater(new Runnable() 
						{
							public void run() 
							{
								saveAllSigTx();
							}
						});
					}
				}).start();
			}	
		}
	});
		


		
	// Auslösendes Ereignis zum Signieren aller importierten Usig-Transaktionen
	btn_sig.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{	
			sigAllTx();		
		}
	});
		
	
	
		
	// Speichert alle SigTx direkt.
	btn_saveAllSigTx.addActionListener(new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
		{
			saveAllSigTx();
		}
	});
			
}
	
	
	

	
	
// --------------------------------------------------------------------------------- Private Methoden -------------------------------------------------------------------------------------------------------------	
	
	
	// Alle Zeilen werden signiert.
	private static void sigAllTx()
	{
		if(SeedExtractor.privKeys == null && cBox_sigMethode.getSelectedIndex()==0)
		{
			JOptionPane.showMessageDialog(pnl_zentrum,GUI.t.t("No seed found!\nA seed must be stored in the “Seed Extractor”!\nOr the “Sig method” must be set to “Private Keys”!"),"No seed found",JOptionPane.ERROR_MESSAGE,MyIcons.noSeed);
			return;
		}
		GUI.txt_ausgabe.setText("");
		setAllComponentsEnabled(false);       	
		getPrivateKeyList();
		new Thread(new Runnable()
		{
			public void run()
			{
				for(int i=0; i<pnl_zentrum.getComponentCount()  ;i++)
				{
					final int index = i;		
					sigZeile(index);			
				}	
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						setAllComponentsEnabled(true);    // muss in die EDT!
					}
				});	
			}
		}).start();		
	}
	
	
	
	
    // Ausgewählte Komponenten werden während des Signier-Vorgangs deaktiviert oder aktiviert.
	// Die betreffenden Komponenten werden in dieser Methode selbst bestimmt und können hier angepasst werden.
	private static void setAllComponentsEnabled(boolean enabled) 
	{
		btn_scanQR			.setEnabled(enabled);
		btn_fromHex			.setEnabled(enabled);
		btn_open_uTx		.setEnabled(enabled);
		btn_delete			.setEnabled(enabled);
		btn_sig				.setEnabled(enabled);
		btn_autoSig			.setEnabled(enabled);
		cBox_sigMethode		.setEnabled(enabled);
		GUI.btn_removeSeed	.setEnabled(enabled);
		GUI.txt_cvc			.setEnabled(enabled);
		GUI.btn_newCVC		.setEnabled(enabled);
		GUI.btn_seedUnlock	.setEnabled(enabled);
		GUI.btn_exportAdr	.setEnabled(enabled);
		GUI.txt_max			.setEnabled(enabled);
		GUI.txt_nr			.setEnabled(enabled);
		GUI.mn_file			.setEnabled(enabled);
		GUI.comboBox_coin	.setEnabled(enabled);
	}
	

	
	
	
	// Alle signierten Transaktion werden ohne FileChooser direkt hintereinander auf die Festplatte gespeichert.
	// Der verwendete Dateipfad ist dabei der letzte Dateipfad der auch bei dem Import verwendet wurde.
	// Am ende wird eine Meldung angezeigt wo und was gespeichert wurde.
	private static void saveAllSigTx()
	{
		try
		{
			GUI.txt_ausgabe.setText("");		
			Component[] c = pnl_zentrum.getComponents();																											
			int index =0;
			String messageTx = "";											// Ein Message Element, das nach Abschluss im OptionPane angezeigt wird.
			for(int i=0; i<c.length;i++)
			{
				GUI_JZeile zeile = (GUI_JZeile) c[i];
				if(zeile.sigTx == null) continue;
				else
				{
					File file = new File(lbl_file_uTx.getText() + "/"+zeile.lbl_sigTx.getText());  				
					BufferedWriter br = new BufferedWriter(new FileWriter(file));
					br.write(Convert.byteArrayToHexString(zeile.sigTx.getRawTx()));
					br.close();
					messageTx = messageTx + zeile.lbl_sigTx.getText() + "\n";	
					index++;
				}
			}								
			String me2 = GUI.t.t("Count = ") + index +"\n" ;
			String me3 = GUI.t.t("Storage location is: ") + lbl_file_uTx.getText() +"\n\n";
			String me4 = GUI.t.t("Was saved: ") + "\n" + messageTx +"\n\n";
			String me5 = GUI.t.t("Note: The location from which Transactions were imported is used.");				
			if(index>0) // Bei mindestens einer erfolgreich signierten Tx.
			{
				String me1 = GUI.t.t("Signed transactions were saved successfully.")+"\n\n";
				JOptionPane.showMessageDialog(pnl_zentrum, me1+me2+me3+me4+me5, GUI.t.t("Save completed"), JOptionPane.INFORMATION_MESSAGE, MyIcons.ok);
			}
			else
			{
				String me1 = GUI.t.t("Error! No transaction was found that could be successfully sign.")+"\n\n";
				JOptionPane.showMessageDialog(pnl_zentrum, me1+me2, "Error", JOptionPane.ERROR_MESSAGE, MyIcons.error);
			}	
		}
		catch(Exception ex) 
		{	
			GUI.txt_ausgabe.setForeground(Color.RED);
			GUI.txt_ausgabe.setText(ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	
	
	// fügt eine neue Transaktion dem Panel pnl_zentrum hinzu.
	private static void addTx(String fileName, String txHexString)
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
		    public void run() 
		    {
				GUI.txt_ausgabe.setText("");
				Transaktion tx;		 
		    	try
		    	{	
		    		tx = new Transaktion(Convert.hexStringToByteArray(txHexString),0); 	
		    	}
		    	catch(Exception ex)
		    	{
		    		GUI.txt_ausgabe.setForeground(Color.RED);
					GUI.txt_ausgabe.setText("Transaction cannot be decoded.<br>"+ex.getMessage());
					return;
		    	}
		    	btn_sig.setVisible(true);
		    	btn_autoSig.setVisible(false);
		    	GUI_JZeile zeile = new GUI_JZeile(tx);
		    	zeile.setUtxFileName(fileName);
				pnl_zentrum.add(zeile);		
				GUI.frame.repaint();
		    }
		});
	}
	

	
	
	//  Signiert eine einzelne Zeile und nimmt die GUI anpassungen vor. (Es wird nur eine einzige Zeile im pnl_zentrum Signiert)
	//	Dies ist eine reine GUI-Methode die hintereinander ausgeführt wird, wenn mehrere Zeilen die Signiert werden.
	//	Es werden alle GUI-Anderungen für das Signieren einer Zeile hier vorgenommen. Die eigentliche Berechnung des Signierens wird in sigTx vorgenommen.
	//	In index kommt die Zeilen Nummer der aktuell zu signierenden Zeile
	//	Achtung, diese Methode wird von einem eigenem Thread aufgefufen. Alle GUI Änderungen müssen daher zwingend in die EDT mit SwingUtilities.invokeLater(new Runnable()...) gesetzt werden !!!
	private static void sigZeile(int index)
	{
		GUI_JZeile zeile = (GUI_JZeile) pnl_zentrum.getComponent(index);
		if(zeile.lbl_sigTx.isVisible()) return;				// Wenn die Tx schon signiert ist, wird der Schleifendurchlauf hier abgebrochen!		
		try
		{			
			if(privateKeys==null) throw new IOException(GUI.t.t("No Seed or Private-Keys loaded!"));	
			SwingUtilities.invokeLater(new Runnable() 			// GUI änderungen müssen hier in der EDT sein!
			{
				public void run() 
				{
					zeile.lbl_load_gif.setEnabled(true);
					zeile.lbl_load_gif.setVisible(true);
				}
			});		

			byte[] sigTx_byte = sigTx(zeile.uTx.getRawTx(), privateKeys);	
			Transaktion sigTx = new Transaktion(sigTx_byte,0);
			SwingUtilities.invokeLater(new Runnable() 			// GUI änderungen müssen hier in der EDT sein!
			{
				public void run() 								// Wenn die Tx erfolgreich signiert wurde.
				{
					zeile.setSigTx(sigTx);
					zeile.lbl_sigTx .setText(Convert.byteArrayToHexString(sigTx.getTxID())+".txn");
					zeile.lbl_sigTx	.setVisible(true);
					zeile.lbl_save	.setVisible(true);
					zeile.lbl_qr	.setVisible(true);
					zeile.lbl_error.setVisible(false);
					zeile.lbl_load_gif.setVisible(false);
					btn_saveAllSigTx.setVisible(true);
				}
			});
		}
		catch (Exception e1) 
		{				
			SwingUtilities.invokeLater(new Runnable()    // GUI änderungen müssen hier in der EDT sein!
			{
				public void run() 
				{
					zeile.lbl_load_gif.setEnabled(false);
					zeile.lbl_load_gif.setVisible(false);
					zeile.lbl_error.setVisible(true);
					GUI.txt_ausgabe.setForeground(Color.RED);
					GUI.txt_ausgabe.setText(e1.getMessage());				
					StringWriter sw = new StringWriter();
			    	e1.printStackTrace(new PrintWriter(sw)); 
					zeile.err = e1.getMessage()+"\n\n"+sw.toString(); // Die Fehlermeldung wird mit einem Mausklick auf "Error" in einem OptionPane angezeigt.
				}
			});
		}			
	}
	
	
	

	// Es wird die Liste von Private-Keys in dem Byte-Array: "privateKeys" gespeichert,
	// Wenn Sig-Methode Seed ausgewählt ist, ist die Liste aus dem Seed-Extraktor bezogen
	// Wenn Sig-Methode Private-Keys ausgewählt ist wird ein JDialog geöffnet und der Anwender muss die Private-Key Liste in das Fenster schreiben.
	private static void getPrivateKeyList()
	{
		// Private-Key aus Seed	
		if(cBox_sigMethode.getSelectedIndex()==0) 
		{	
			privateKeys = SeedExtractor.privKeys;
		}	
		else // Prvate-Keys durch Eingabe in den JDialog
		{ 										
			GUI.txt_ausgabe.setText("");	
			JTextPane txt_privKeys = new JTextPane();						
			String promtText = "ecc58dff38448c0f56232d226a83cbc83d802f4c60b5d1145476cff0ee531fa0,\n1c210145de5c6912c473821bbda4de2c651be1011be8594a944f55809c953af1,\n50da38ee4b4a1f8390180512de113c913bfdb62061d02d5099a30951985e8b4d";
			txt_privKeys.setText(promtText);
			txt_privKeys.setForeground(Color.gray);
			
			JDialog dialog = new JDialog();		// Kleines Dialog-Fenster zur Eingabe der Priv-Keys öffnet sich
			dialog.setBounds(GUI.frame.getX()+160, GUI.frame.getY()+65, 580, 350);
			dialog.getContentPane().setLayout(new BorderLayout());
			dialog.setTitle("Sign Transaction");
			dialog.setIconImage(MyIcons.key.getImage());
			JTextPane lbl = new JTextPane();
			JScrollPane scrollPane = new JScrollPane();
			JButton okButton = new JButton("OK");
			lbl.setBackground(GUI.color1);
			lbl.setEditable(false);
			lbl.setText(GUI.t.t("Signure with a list of private keys that you can enter here.\r\nPrivate keys must be formatted in Hexa Decimal format!\r\nThe order of the private keys doesn't matter. All private keys required for signing must only be present!\r\nThe private keys must separated with a comma:\r\nExample:"));	
			txt_privKeys.setFont(GUI.font4);
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
						privateKeys = priv_b;				
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
	}
	
	
	
	
	
	
	// Signiert Legancy und SegWit Transaktion
	// Achtung, derzeit sind compressed und uncompresset Tx implementiert.
	// Übergeben werden muss die unsigniert Tx als Byte-Array und eine unsortierte Liste mit möglichen Private-Keys. Die Anzahl der Priv-Keys ist egel
	// Das Format der Signatur soll diese Methode selbstständig erkennen.
	private static byte[] sigTx(byte[] unSigTx, byte[][] privKeyList) throws Exception
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
}







// ------------------------------------------------------------------- Hilfs Klassen ------------------------------------------------------------------------



// Diese Klasse erweitert JPanel mit dem Drop-Text alse Hintergrund-Bild ohne das JPanel zu beeinflussen.
// Wird als pnl_zentrum verwendet.
class MyDropPanel extends JPanel
{
    @Override															// Die Erweiterung bewirkt den DropText als Hitergrund-Bild ohne das JPanel zu beeinflussen.
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(255,187,106));
        g2.setFont(new Font("DejaVu Sans", Font.PLAIN, 18));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(GUI_SignTx.dragText)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(GUI_SignTx.dragText, x, y);
        g2.dispose();
    }
}