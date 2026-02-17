package CoinGen;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import GUI.GUI;
import lib3001.btc.Bech32Address;
import lib3001.btc.BitcoinAddr;
import lib3001.btc.PrvKey;
import lib3001.crypt.Calc;
import lib3001.crypt.Convert;
import lib3001.network.CoinParameter;
import lib3001.qrCode.QRCodeZXING;
import seedExtractor.SeedExtractor;



/***********************************************************************************
*	Hauptprogramm des CoinAddressGenerators	V3.1      		Autor: Mr. Maxwell   	*
*	Hier wird die Berechnung aller Ausgaben durchgeführt,							* 
*	sobald es eine Änderung der Eingabe gegeben hat,								*
************************************************************************************/


public class Action 
{
	public static String 	prvWIF;
	public static String	addr;
	static 	JLabel 			privKey;   						// QR-Code Label
	static 	JLabel 			address;						// QR-Code Label
	static 	JSpinner 		spinner = new JSpinner(new SpinnerNumberModel(120, 80, 500, 5));	// Verändert die größe der QR-Codes
	static 	BufferedImage 	qrCode1;
	static 	BufferedImage 	qrCode2;
	
	
	
	
	
/**	Startet die Ausführung. Alle Eingaben werden von hier aus gescannt.
	Und alle Ausgabefelder werden von hier aus eigenständig beschrieben.
	Diese Methode muss gestartet werden, nach jeder relevanten Änderung in der GUI. **/
public static void go()
{
	SwingUtilities.invokeLater(new Runnable() 
	{
		public void run() 
		{
			prvWIF = "";
			addr = "";
			GUI.txt_ausgabe.setForeground(Color.BLACK);
			GUI.txt_ausgabe.setText("");
			GUI.panel_QRCode.removeAll();
			try
			{
				if(GUI.tabbedPane.getSelectedIndex()==0)
				{
					String str = GUI.txt_inText.getText();
					if(str.equals("")==false) calcFromPrivKey(Calc.getHashSHA256(str));			
				}
				if(GUI.tabbedPane.getSelectedIndex()==1)
				{
					if(GUI.txt_wuerfel.getText().equals("          -          -          -          -          -          -          -          -          -          "));
					else 
					{
						String str = GUI.txt_wuerfel.getText();
						calcFromPrivKey(str);
					}
				}
				if(GUI.tabbedPane.getSelectedIndex()==2)
				{
					String str = GUI.txt_privKey.getText();		
					str = str.replaceAll(" ","");
					if(str.equals("")==false) calcFromPrivKey(str);
				}
				if(GUI.tabbedPane.getSelectedIndex()==3)
				{
					if(SeedExtractor.privKeys!=null)
					{
						int index = (Integer)GUI.txt_nr.getValue();
						byte[] priv = SeedExtractor.privKeys[index-1];
						calcFromPrivKey(Convert.byteArrayToHexString(priv));
					}
				}
			}
			catch(Exception e) 
			{
				GUI.txt_ausgabe.setForeground(Color.RED);
				GUI.txt_ausgabe.setText(e.toString());
				e.printStackTrace();
			}
		}
	});				
}
	
	
	
	
// Bekommt den Private-Key als Hex-String übergeben und führt die weitere Berechnung durch.	
private static void calcFromPrivKey(String priv) throws Exception
{
	CoinParameter cp = CoinParameter.getFromSymbol((String) GUI.comboBox_coin.getSelectedItem());
	PrvKey p 		= new PrvKey(priv,cp.pref_PrivKey);
	String prvHex 	= Convert.byteArrayToHexString(p.getHexPrivKey());
		   prvWIF 	= p.getBase58PrivKey(GUI.unCompressed.isSelected()==false);
	String pub 		= Calc.getPublicKey(prvHex, GUI.unCompressed.isSelected()==false);
	String h160 	= Calc.getHashRIPEMD160_from_HexString(Calc.getHashSHA256_from_HexString(pub));
	if(GUI.bech32.isSelected())
	{
		addr = Bech32Address.segwitToBech32(cp.bech32 , Convert.byteArray_to_int(cp.bip44), Convert.hexStringToByteArray(h160));
	}
	else
	{
		BitcoinAddr address = new BitcoinAddr(Convert.hexStringToByteArray(h160) , cp.pref_PubKey);		
		if(GUI.p2sh.isSelected()) 	addr = address.getP2SHAddress(cp.pref_P2SH);
		else 						addr = address.getBase58Address();		
	}	
//	String link = Web.getLinkHTML(addr,cp.symbol);
//	Double	final_balance 	=  Web.getValue(addr, cp.symbol);		// Wurde aus Datenschutzgrüngen entfernt!	
//	double betrag = final_balance/100000000.0;
//	String value = "";;
//	if(final_balance != -1) value = "\nBalance: "+String.format("%11.8f", betrag)+"  "+cp.symbol; 
//	else value = "";	
	if(GUI.showPrivKeys.isSelected()) 	GUI.txt_ausgabe.setText("<pre>\nPriv Hex: "+prvHex+"\nPriv WIF: "+prvWIF+"<br>Pub Key:  "+pub+"\nHash160:  "+h160+"\nAddress:  "+addr +"</pre>");
	else								GUI.txt_ausgabe.setText("<pre>\nPriv Hex: ***\nPriv WIF: ***<br>Pub Key:  "              +pub+"\nHash160:  "+h160+"\nAddress:  "+addr +"</pre>");
	setQRCodes(prvWIF, addr);
}
	





static int v;

private static void setQRCodes(String prvWIF, String addr) throws Exception
{
	spinner.setEditor(new JSpinner.DefaultEditor(spinner));
	spinner.setPreferredSize(new Dimension(16,16));
	v =  Integer.valueOf(spinner.getModel().getValue().toString());
	GUI.panel_QRCode .add(spinner);

	qrCode1 = QRCodeZXING.writeQRCode(prvWIF, GUI.color1, 	new Color(120,0,0), 75, 75);
	qrCode2 = QRCodeZXING.writeQRCode(addr, GUI.color1, 	new Color(0,80,0),  75, 75); 	

	spinner.addChangeListener(new ChangeListener() 
	{
		public void stateChanged(ChangeEvent e) 
		{		
			v =  Integer.valueOf(spinner.getModel().getValue().toString());	
			privKey.setIcon(new ImageIcon(new ImageIcon(qrCode1).getImage().getScaledInstance(v, v, 2)));
			address.setIcon(new ImageIcon(new ImageIcon(qrCode2).getImage().getScaledInstance(v, v, 2)));			
		}
	});

	try
	{ 
		verifyQRCode(qrCode1, prvWIF);
		privKey = new JLabel(new ImageIcon(qrCode1));   
		privKey	.setBackground(GUI.color1);
		privKey	.setText("Private Key");
		privKey	.setOpaque(true);
		privKey.setVerticalTextPosition(SwingConstants.TOP);
		privKey.setHorizontalTextPosition(SwingConstants.CENTER);
		privKey.setIcon(new ImageIcon(new ImageIcon(qrCode1).getImage().getScaledInstance(v, v, 2)));

	}
	catch(Exception e)
	{
		privKey = new JLabel(e.getMessage());
		privKey.setForeground(Color.red);
	}	
	try
	{
		verifyQRCode(qrCode2, addr);
		address = new JLabel(new ImageIcon(qrCode2)); 
		address	.setBackground(GUI.color1);
		address	.setText("Address");
		address	.setOpaque(true);
		address.setVerticalTextPosition(SwingConstants.TOP);
		address.setHorizontalTextPosition(SwingConstants.CENTER);
		address.setIcon(new ImageIcon(new ImageIcon(qrCode2).getImage().getScaledInstance(v, v, 2)));

	}
	catch(Exception e)
	{
		address = new JLabel(e.getMessage());
		address.setForeground(Color.red);
	}
	Component strut5 = Box.createHorizontalStrut(10);
	Component strut6 = Box.createHorizontalStrut(20);
	strut5.setEnabled(false);
	strut6.setEnabled(false);
	privKey.setVisible(GUI.showPrivKeys.isSelected());
	GUI.panel_QRCode.add(privKey);
	GUI.panel_QRCode.add(strut5);
	GUI.panel_QRCode.add(address);
	GUI.panel_QRCode.add(strut6);
}



// prüft den erstellten QR Code, indem das Bild gescannt wird und mit dem übergebenem String verglichen wird.
// Sind beide nicht gleich, wird ein Fehler ausgegeben und der QR-Code darf nicht angezeigt werden!
public static void verifyQRCode(BufferedImage image, String str) throws Exception
{
	String qr = QRCodeZXING.readQRCode(image, false);
	if(qr.equals(str)==false) throw new Exception("QR-Code Error!");
}
}