package CoinGen;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;



/***********************************************************************************
*	Hauptprogramm des CoinAddressGenerators	V3      	Autor: Mr. Maxwell   	*
*	Hier wird die Berechnung aller Ausgaben durchgeführt,				* 
*	sobalt es eine Änderung der Eingabe gegeben hat,				*
************************************************************************************/


public class Action 
{
/**	Startet die Ausführung. Alle Eingaben werden von hier aus gescannt.
	Und alle Ausgabefelder werden von hier aus eigenständig beschrieben.
	Diese Methode muss getartet werden, nach jeder relevanten Änderung in der GUI. **/
public static void go()
{
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
	}
	catch(Exception e) 
	{
		GUI.txt_ausgabe.setForeground(Color.RED);
		GUI.txt_ausgabe.setText(e.toString());
	}	
}
	
	
	
	
// Bekommt den Private-Key als Hex-String übergeben und führt die weitere Berechnung durch.	
private static void calcFromPrivKey(String priv) throws Exception
{
	CoinParameter cp = CoinParameter.getFromSymbol((String) GUI.comboBox_coin.getSelectedItem());
	PrvKey p 		= new PrvKey(priv,cp.pref_PrivKey);
	String prvHex 	= Convert.byteArrayToHexString(p.getHexPrivKey());
	String prvWIF 	= p.getBase58PrivKey(GUI.unCompressed.isSelected()==false);
	String pub 		= Calc.getPublicKey(prvHex, GUI.unCompressed.isSelected()==false);
	String h160 	= Calc.getHashRIPEMD160_from_HexString(Calc.getHashSHA256_from_HexString(pub));
	String addr;
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
	String link = Web.getLinkHTML(addr,cp.symbol);
	Double	final_balance 	=  Web.getValue(addr, cp.symbol);			
	double betrag = final_balance/100000000.0;
	String value;
	if(final_balance != -1) value = "\nBalance: "+String.format("%11.8f", betrag)+"  "+cp.symbol; 
	else value = "";
	GUI.txt_ausgabe.setText("<pre>\nPriv Hex: "+prvHex+"\nPriv WIF: "+prvWIF+"<br>Pub Key:  "+pub+"\nHash160:  "+h160+"\nAddress:  "+addr+"\nExplorer: "+link+value+"</pre>");
	setQRCodes(prvWIF, addr);
}
	




static JLabel privKey;   	// QR-Code Label
static JLabel address;		// QR-Code Label

private static void setQRCodes(String prvWIF, String addr) throws Exception
{
	BufferedImage qrCode1 = QRCodeReaderZXING.writeQRCode(prvWIF, GUI.color1, new Color(120,0,0), 120, 120);
	BufferedImage qrCode2 = QRCodeReaderZXING.writeQRCode(addr, GUI.color1, new Color(0,80,0), 120, 120); 	
	verifyQRCode(qrCode1, prvWIF);
	verifyQRCode(qrCode2, addr);	
	privKey = new JLabel(new ImageIcon(qrCode1));   
	address = new JLabel(new ImageIcon(qrCode2)); 
	privKey	.setBackground(GUI.color1);
	address	.setBackground(GUI.color1);
	privKey	.setText("Private Key");
	address	.setText("Address");
	privKey	.setOpaque(true);
	address	.setOpaque(true);
	privKey.setVerticalTextPosition(SwingConstants.TOP);
	address.setVerticalTextPosition(SwingConstants.TOP);
	privKey.setHorizontalTextPosition(SwingConstants.CENTER);
	address.setHorizontalTextPosition(SwingConstants.CENTER);
	Component strut5 = Box.createHorizontalStrut(10);
	Component strut6 = Box.createHorizontalStrut(20);
	strut5.setEnabled(false);
	strut6.setEnabled(false);
	GUI.panel_QRCode.add(privKey);
	GUI.panel_QRCode.add(strut5);
	GUI.panel_QRCode.add(address);
	GUI.panel_QRCode.add(strut6);
}



// prüft den erstellten QR Code, indem das Bild gescannt wird und mit dem übergebenem String verglichen wird.
// Sind beide nicht gleich, wird ein Fehler ausgegeben und der QR-Code darf nicht angezeigt werden!
private static void verifyQRCode(BufferedImage image, String str) throws Exception
{
	String qr = QRCodeReaderZXING.readQRCode(image, false);
	if(qr.equals(str)==false) throw new Exception("QR-Code Error! QR code was not recognized correctly!");
}
}
