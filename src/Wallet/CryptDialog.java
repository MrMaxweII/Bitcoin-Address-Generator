package Wallet;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Font;
import javax.swing.border.LineBorder;

import org.json.JSONException;
import org.json.JSONObject;

import BTClib3001.Calc;
import BTClib3001.Convert;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Dimension;
import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import java.awt.event.ItemListener;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;



/***************************************************************************************************************************
*	Ver/End-Schlüsselung mit Dialogfeld (GUI) zu Eingabe von Passwörtern.													*
*	Die beiden Hauptmethoden paranoidEncrypt() und paranoidDecrypt() 														*
*	integrieren den kompletten Verschlüsselungsprozess Siehe Blockdiagramm "ParanoidCrypt.png".								*
*	Beim Aufruf öffnet sich dieses Dialogfeld welches zur Eingabe des Verschlüsselungs-Passwortes auffordert.				*
*	Dieser Konstruktor CryptDialog() erstellt das Dialogfeld zur eingabe des Passwortes.									*
*	Diese Klasse ist zwar für den CoinAddressGenerator programmiert, ist aber allgemein kompatibel mit folgener Ausnahme:	*
*	Die Klartext-Daten müssen ein JSONObject sein welches den Datensetzt "pwHash" enthällt!									*
*	Dieser PwHash ist ein 32Byte SHA256² Hex-String der den doppelten PasswortHash, des voherigen Paswortes enthällt. 		*
*	(Doppel, weil dadurch der einfache SHA256 Hash der zum verschlüsseln benutz wird nicht aufgedeckt werden kann.)			*
*	Dies ist notwendig um zu verhindern, das versehentlich mit einem falschen Passort neu verschlüsselt wird.				*
*	Bei der Verschlüsselung kann so, das alte Passwort geprüft werden bevor neu verschlüsselt wird.							*
*	Bei der erstmaligen Verschlüsselung ist pwHash = "" (Leerstring);														*
*	Die Methode paranoidEncrypt() prüft so das alte Passort!																*
****************************************************************************************************************************/



public class CryptDialog extends JDialog 
{



	private static CryptDialog 	dialog; 							// Das Dialogfeld von Typ: JDialog
	private byte[] 				dataOut;							// Rückgabe Daten, Chiffre oder Klartext.
	private JPasswordField 		txt_pw1 = new JPasswordField();		
	private JPasswordField 		txt_pw2 = new JPasswordField();
	private JPasswordField 		txt_pw3 = new JPasswordField();	
	private boolean 			threadRun = false;					
	private JProgressBar 		spinner= new JProgressBar();
	
	
	
	
/**	Hauptmethode zur Verschlüsselung. 
	- Öffnet das Dialogfeld zur Eingabe des Passwortes zur Verschlüsselung
	- Es kann entweder das alte Passwort verwendet werden oder es kann ein neues Passwort erstellt werden.
	- Das Passwort wird geprüft mit einem im Klartext enthaltenem JSON-Datensatz "pwHash" der den doppelten SHA256² Hash des voherigen Passwortes enthällt.
	  Bei der erstmaligen Verschlüsselung muss dieser Datensatz auch vorhanden und ein Leerstring sein!
	- Verschlüsselt die Daten mit dem eingegebenem Passwort
	- Vor der Verschlüsselung wird an die Daten ein checksum SAH256 Hash vorrangestellt
	- Gibt Meldungen oder Fehler im Dialogfeld aus
	@param x & y  Position des Dialogfensters
	@param klarText Die Daten die mit ParanoidCrypt verschlüsselt werden sollen als JSONObject.
	@return Nach erfolgreicher verschlüsselung wird die Chiffre als ByteArray zurück gegeben und das Dialogfeld geschlossen.
	Bei Fehlern oder durch Abbruch des Benutzers wird "null" zurück gegeben. **/
public static byte[] paranoidEncrypt(int x, int y, JSONObject klarText) throws Exception
{	
	dialog = new CryptDialog(x,y,"encrypt", klarText,null); 
	dialog.setVisible(true);
	dialog.dispose();
	return dialog.dataOut;
}
	

	
/**	Hauptmethode zur Endschlüsselung. 
	- Öffnet das Dialogfeld zur Eingabe des Passwortes zur Endschlüsselung
	- Entschlüsselt die Daten mit dem eingegebenem Passwort
	- Mit dem an den Klartext vorrangestellten SHA256 wird geprüft, ob die entschlüsselung erfolgreich war.
	- Gibt Meldungen oder Fehler im Dialogfeld aus
	@param x & y  Position des Dialogfensters
	@param chiffre Die Daten die mit ParanoidCrypt entschlüsselt werden sollen
	@return Nach erfolgreicher entschlüsselung werden die Daten im Klartext zurück gegeben und das Dialogfeld geschlossen.
	Bei Fehlern oder durch Abbruch des Benutzers wird "null" zurück gegeben. **/
public static String paranoidDecrypt(int x, int y, byte[] chiffre) throws Exception
{	
	dialog = new CryptDialog(x,y,"decrypt",null,chiffre); 
	dialog.setVisible(true);
	dialog.dispose();
	if(dialog.dataOut==null) return null;
	return new String(dialog.dataOut, StandardCharsets.UTF_8);
}
	

	
/**	Konstruktor der GUI des Passowrt-Dialog-Feldes
	@param profil Das Dialog-Profil. Es gibt: "encrypt" oder "decrypt" 
	@param pwHash SHA256² das alte Password wird nur zur Überprüfung bei der Verschlüsselung genutzt.
 * @throws JSONException **/	
private CryptDialog(int x, int y, String profil, JSONObject klarText, byte[] chiffre) throws JSONException 
{
	setModal(true);
	setBounds(x, y, 500, 340);
	setTitle("Password");
	
	JPanel contentPanel = new JPanel();
	JPanel panel_haupt 	= new JPanel();
	JPanel panel_unten 	= new JPanel();	
	JTextArea info 		= new JTextArea("    Attention! Password cannot be recovered! \n    If the password is forgotten, coins will be lost forever!");
	JLabel lbl_pw1 		= new JLabel("Enter Password");
	JLabel lbl_pw2 		= new JLabel("New Password");
	JLabel lbl_pw3 		= new JLabel("Confirm Password");
	JCheckBox checkbox_1= new JCheckBox("Show Password");
	JCheckBox checkbox_2= new JCheckBox("Change Password");
	JTextArea txt_error = new JTextArea();
	JButton btn_ok 		= new JButton("OK");
	JButton btn_abbruch = new JButton("Cancel");
	
	Component strut1 = Box.createHorizontalStrut(20);
	Component strut2 = Box.createHorizontalStrut(20);
	Component strut3 = Box.createHorizontalStrut(2000);
	Component strut4 = Box.createHorizontalStrut(2000);
	Component strut5 = Box.createHorizontalStrut(2000);
	Component strut6 = Box.createHorizontalStrut(2000);
	
	strut3.		setPreferredSize(new Dimension(2000, 10));
	strut4.		setPreferredSize(new Dimension(2000, 10));
	strut5.		setPreferredSize(new Dimension(2000, 10));
	strut6.		setPreferredSize(new Dimension(2000, 6));
	txt_pw1.	setPreferredSize(new Dimension(2000, 21));	
	txt_pw2.	setPreferredSize(new Dimension(2000, 21));	
	txt_pw3.	setPreferredSize(new Dimension(2000, 21));
	checkbox_1.	setPreferredSize(new Dimension(2000, 18));	
	checkbox_2.	setPreferredSize(new Dimension(2000, 18));
	btn_ok.		setPreferredSize(new Dimension(70, 23));
	btn_abbruch.setPreferredSize(new Dimension(70, 23));
	spinner.	setPreferredSize(new Dimension(440, 5));

	contentPanel.setLayout(new BorderLayout());
	panel_haupt	.setLayout(new FlowLayout( 0, 1, 1));
	panel_unten	.setLayout(new FlowLayout(FlowLayout.RIGHT));
	info.		setFont(new Font("Arial", Font.PLAIN, 13));
	txt_error.	setFont(new Font("Tahoma", Font.PLAIN, 13));
	checkbox_2.	setFont(new Font("Tahoma", Font.PLAIN, 10));
	checkbox_1.	setFont(new Font("Tahoma", Font.PLAIN, 10));
	info.		setBorder(new LineBorder(new Color(255, 180, 180), 4));
	info		.setEditable(false);
	info.setVisible(false);
	txt_error.	setSize(new Dimension(440, 30));
	txt_error.	setWrapStyleWord(true);
	txt_error.	setLineWrap(true);
	info		.setBackground(new Color(255, 180, 180));
	txt_error.	setBackground(new Color(240, 240, 240));
	txt_error.	setForeground(new Color(165, 42, 42));	
	txt_error.	setEditable(false);
	btn_abbruch.setMargin(new Insets(0, 0, 0, 0));
	spinner.	setIndeterminate(true);
	spinner.	setBorderPainted(false);
	spinner.	setVisible(false);
	
	setContentPane(contentPanel);
	contentPanel.add(info, BorderLayout.NORTH);
	contentPanel.add(panel_haupt, BorderLayout.CENTER);
	
	Component strut0 = Box.createHorizontalStrut(2000);
	strut0.setPreferredSize(new Dimension(2000, 10));
	panel_haupt.add(strut0);
	panel_haupt.add(lbl_pw1);
	panel_haupt.add(txt_pw1);
	panel_haupt.add(strut3);
	panel_haupt.add(lbl_pw2);
	panel_haupt.add(txt_pw2);
	panel_haupt.add(strut4);
	panel_haupt.add(lbl_pw3);
	panel_haupt.add(txt_pw3);
	panel_haupt.add(strut5);
	panel_haupt.add(checkbox_1);
	panel_haupt.add(checkbox_2);	
	panel_haupt.add(strut6);
	panel_haupt.add(spinner);
	panel_haupt.add(txt_error);
	contentPanel.add(panel_unten, BorderLayout.SOUTH);	
	panel_unten.add(btn_ok);
	panel_unten.add(btn_abbruch);
	contentPanel.add(strut1, BorderLayout.WEST);
	contentPanel.add(strut2, BorderLayout.EAST);
		
	if(profil.equals("decrypt"))
	{
		lbl_pw2.setVisible(false);
		lbl_pw3.setVisible(false);
		txt_pw2.setVisible(false);	
		txt_pw3.setVisible(false);
		checkbox_2.setVisible(false);
	}
	if(profil.equals("encrypt"))
	{
		if(klarText.getString("pwHash").equals("")) 
		{
			checkbox_2.setSelected(true);
			checkbox_2.setVisible(false);
			lbl_pw1.setVisible(false);
			txt_pw1.setVisible(false);
			lbl_pw2.setVisible(true);
			lbl_pw3.setVisible(true);
			txt_pw2.setVisible(true);
			txt_pw3.setVisible(true);
			info.setVisible(true);
		}
		else
		{
			lbl_pw2.setVisible(false);
			lbl_pw3.setVisible(false);
			txt_pw2.setVisible(false);	
			txt_pw3.setVisible(false);
		}
	}

	
	
	
// ---------------------------------------- Actoin Listeners ------------------------------------//


btn_ok.addActionListener(new ActionListener() 
{
	public void actionPerformed(ActionEvent e) 
	{			
		if(profil.equals("encrypt"))
		{
			Thread tr = new Thread(new Runnable() 
			{
				public void run() 
				{
					threadRun = true;
					dialog.setEnabled(false);
					spinner.setVisible(true);
					try 
					{
						txt_error.setText("");
						String pw = new String(txt_pw1.getPassword());						
						String pwh = Calc.getHashSHA256_from_HexString(Calc.getHashSHA256(pw));						
						if(pwh.equals(klarText.getString("pwHash")))				// Wenn das eingegebene PW dem voherigem PW entspricht.
						{
							if(checkbox_2.isSelected())								// Wenn das PW geändert wird.
							{
								char[] pw2 = txt_pw2.getPassword();
								char[] pw3 = txt_pw3.getPassword();	
								String newPW = new String(pw3);
								if("".equals(new String(pw3))) throw new Exception("New Password cannot be empty!");
								if(Arrays.equals(pw2, pw3)) 
								{
									klarText.put("pwHash", Calc.getHashSHA256_from_HexString(Calc.getHashSHA256(newPW)));
									dataOut = Crypt.paranoidEncrypt(klarText.toString().getBytes(), Convert.hexStringToByteArray(Calc.getHashSHA256(newPW)));
									dispose();
								}
								else throw new Exception("new Passwords are not the same!");
							}
							else 													// Verschlüsselung mit altem Passort
							{
								dataOut = Crypt.paranoidEncrypt(klarText.toString().getBytes(), Convert.hexStringToByteArray(Calc.getHashSHA256(pw)));
								dispose();
							}
						}
						else
						{
							if("".equals(klarText.getString("pwHash")))   			// Wenn PasswortHash leer ist, wird erstmalig ein neues Passwort angelegt
							{
								char[] pw2 = txt_pw2.getPassword();
								char[] pw3 = txt_pw3.getPassword();	
								String newPW = new String(pw3);
								if("".equals(new String(pw3))) throw new Exception("New Password cannot be empty!");
								if(Arrays.equals(pw2, pw3)) 
								{
									klarText.put("pwHash", Calc.getHashSHA256_from_HexString(Calc.getHashSHA256(newPW)));
									dataOut = Crypt.paranoidEncrypt(klarText.toString().getBytes(), Convert.hexStringToByteArray(Calc.getHashSHA256(newPW)));
									dispose();
								}
								else throw new Exception("new Passwords are not the same!");							
							}							
							else throw new Exception("Password incorrect!");						
						}
					} 
					catch(Exception ex) {txt_error.setText(ex.getMessage());}
					spinner.setVisible(false);
					threadRun = false;
					dialog.setEnabled(true);
				}	
			});
			if(threadRun == false) tr.start();
		}
		if(profil.equals("decrypt"))
		{				
			Thread tr = new Thread(new Runnable() 
			{
				public void run() 
				{
					threadRun = true;
					dialog.setEnabled(false);
					spinner.setVisible(true);
					try 
					{
						txt_error.setText("");
						String pw = new String(txt_pw1.getPassword());
						byte[] b = Crypt.paranoidDecrypt(chiffre, Convert.hexStringToByteArray(Calc.getHashSHA256(pw)));
						byte[][] dec = Crypt.removeAndCheckSHA256Checksum(b);
						if(dec[2][0]==1) {dataOut = dec[1]; dispose();}
						else throw new Exception("Error decrypt, Password incorrect!");																							
					} 
					catch(Exception ex) {txt_error.setText(ex.getMessage());}
					spinner.setVisible(false);
					threadRun = false;
					dialog.setEnabled(true);
				}	
			});
			if(threadRun == false) tr.start();
		}		
	}
});



txt_pw1.addKeyListener(new KeyAdapter() 
{
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode()==10) btn_ok.doClick();
	}
});

txt_pw2.addKeyListener(new KeyAdapter() 
{
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode()==10) btn_ok.doClick();
	}
});

txt_pw3.addKeyListener(new KeyAdapter() 
{
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(e.getKeyCode()==10) btn_ok.doClick();
	}
});



checkbox_1.addItemListener(new ItemListener() 
{
	public void itemStateChanged(ItemEvent e) 
	{
		if (checkbox_1.isSelected()) 
		{
        	txt_pw1.setEchoChar((char) 0);
        	txt_pw2.setEchoChar((char) 0);
        	txt_pw3.setEchoChar((char) 0);
        } 
		else 
        {
			txt_pw1.setEchoChar('•');
			txt_pw2.setEchoChar('•');
			txt_pw3.setEchoChar('•');
        }
	}
});



checkbox_2.addItemListener(new ItemListener() 
{
	public void itemStateChanged(ItemEvent e) 
	{
		if (checkbox_2.isSelected()) 
		{
			lbl_pw2.setVisible(true);
			lbl_pw3.setVisible(true);
			txt_pw2.setVisible(true);
			txt_pw3.setVisible(true);
			info.setVisible(true);
        } 
		else 
        {
			lbl_pw2.setVisible(false);
			lbl_pw3.setVisible(false);
			txt_pw2.setVisible(false);	
			txt_pw3.setVisible(false);
			lbl_pw1.setText("Enter Password");
			info.setVisible(false);

        }
	}
});



btn_abbruch.addActionListener(new ActionListener() 
{
	public void actionPerformed(ActionEvent e) 
	{
	 	dataOut = null;
    	dispose();
	}
});



// Close Button wird abgefangen und hier selbst verarbeitet.
addWindowListener(new java.awt.event.WindowAdapter() 
{
    @Override
    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
    {
     	dataOut = null;
    	dispose();
    }
});	
	
}
}