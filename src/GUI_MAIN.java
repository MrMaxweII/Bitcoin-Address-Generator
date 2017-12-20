import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;



	/************************************************
	* 						*
	*		MAIN and GUI			*
	*						*
	*************************************************/



public class GUI_MAIN extends JFrame
{



	public static final String VersionsNummer = "2.1";
	public static final String Autor	  = "Mr.Maxwell";
	private static final long serialVersionUID = 2L;
	private JFormattedTextField txtPrivateKey;
	private JTextField txtOutPrivateKey;
	private JTextField txtPublicKeyX;
	private JTextField txtPublicKeyY;
	private JTextField txtBitcoinAdress;
	private JTextField VariablePrivateKey;
	private JPanel 	   panelStyle;
	private JComboBox  comboBoxSprache;
	private JTextArea  txtInfo;
	private JPanel 	   panelInfo;
	private JTextPane  txtInfotext;
	private JTextPane  txtLogo;
	private JTextPane  txtLogo2;
	private JTextPane  txtEingabePassphrase;
	private JFormattedTextField txtWuerfel;
	private JLabel lblPassphrase;
	private JLabel lblGebenSieEinen;
	private JLabel lblWuerfelEingabe;
	private JLabel lblWuerfelzeichenEingeben;
	private JLabel lblPrivateKeyIn;
	private JLabel lblPrivateKeyOut;
	private JLabel lblPublicKeyX;
	private JLabel lblPublicKeyY;
	private JLabel lblBitcoinAdressOut;
	private JLabel lblBase58;
	private JTextPane txtFehler;
	private JTextPane txtQRBildFehler;
	private JLabel lblBitcoinAdress;
	private JLabel lblBitcoinAdresseQRCode;
	private JComboBox comboBoxPrivateKey;
	private JSlider[] slider = new JSlider[30];
	private JPanel panelQRCode;
	private JLabel lblFarben1;
	private JTextPane lblFarben2;
	private int I=0;
	private JCheckBox btnPrivateKeyAusblenden;
	private JLabel lblLink;
	private JTextPane txtÜberblendung;
	private JPanel panelÜberblendung;
	private JButton btnÜberblendungSchließen;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					GUI_MAIN frame = new GUI_MAIN();
					frame.setVisible(true);
				} 
				catch (Exception e) {e.printStackTrace();}
			}	
		});
		Config.loadConfig(Config.name);
		Language.setLanguage(Integer.parseInt(Config.settings[10]));
	
	}

	/**
	 * Create the frame.
	 * @throws ParseException 
	 */
	public GUI_MAIN() throws ParseException
	{
		setTitle("Bitcoin Adress Generator "+VersionsNummer);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 830, 1009);
		getContentPane().setLayout(null);
		
		comboBoxSprache = new JComboBox();
		comboBoxSprache.setModel(new DefaultComboBoxModel(new String[] {" English", " German", " French", " Russian", " Chinese    中国"}));
		comboBoxSprache.setSelectedIndex(Integer.parseInt(Config.settings[10]));
		comboBoxSprache.setBounds(683, 0, 121, 23);
		getContentPane().add(comboBoxSprache);
		
		txtInfo = new JTextArea();
		txtInfo.setTabSize(4);
		txtInfo.setBounds(10, 20, 511, 257);
		txtInfo.setWrapStyleWord(true);
		txtInfo.setLineWrap(true);
		txtInfo.setEditable(false);
		txtInfo.setText(Language.InfoText);
		
		panelInfo = new JPanel();
		panelInfo.setBorder(new TitledBorder(null, Language.btnInfo, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelInfo.setBounds(267, 21, 531, 288);
		panelInfo.setVisible(false);
		panelInfo.add(txtInfo);
		panelInfo.setLayout(null);
		
		panelStyle = new JPanel();
		panelStyle.setBorder(new TitledBorder(null, Language.farbenAnpassen, TitledBorder.LEADING, TitledBorder.TOP, null,null));
		panelStyle.setBounds(233, 21, 335, 450);
		panelStyle.setLayout(null);
		panelStyle.setVisible(false);
		getContentPane().add(panelStyle);
		
		JButton btnFarbe1 = new JButton(Language.btnSpeichern);
		btnFarbe1.setFont(new Font("Dialog", Font.PLAIN, 10));
		btnFarbe1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
			    Config.saveConfig(Config.name);
			    panelStyle.setVisible(false);
			    getContentPane().repaint();
			}
		});
		btnFarbe1.setBounds(236, 419, 89, 20);
		panelStyle.add(btnFarbe1);
		
		JButton btnX2 =new JButton("X");
		btnX2.setBounds(285, 0, 50, 20);
		panelStyle.add(btnX2);
		btnX2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				panelStyle.setVisible(false);
				Config.loadConfig(Config.name);
				setAllSlider();
			}
		});
		btnX2.setFont(new Font("Arial Black", Font.PLAIN, 14));
		
		JButton btnInfo = new JButton(Language.btnInfo);
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				panelStyle.setVisible(false);
				if(panelInfo.isVisible())  panelInfo.setVisible(false);
				else                       panelInfo.setVisible(true); 
				Config.loadConfig(Config.name);
				setAllSlider();
			}
		});
		btnInfo.setBounds(562, 0, 121, 23);
		getContentPane().add(btnInfo);
		
		JButton btnX = new JButton("X");
		btnX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				panelInfo.setVisible(false);
			}
		});
		btnX.setFont(new Font("Arial Black", Font.PLAIN, 14));
		btnX.setBounds(472, 0, 49, 20);
		panelInfo.add(btnX);
		
		JButton btnStyle = new JButton(Language.btnStyle);	
		btnStyle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				panelInfo.setVisible(false);
				if(panelStyle.isVisible())  panelStyle.setVisible(false);
				else                        panelStyle.setVisible(true); 
				Config.loadConfig(Config.name);
				setAllSlider();
			}
		});
		btnStyle.setBounds(445, 0, 121, 23);
		getContentPane().add(btnStyle);
		getContentPane().add(panelInfo);
		
		txtInfotext = new JTextPane();
		txtInfotext.setEditable(false);
		txtInfotext.setFont(new Font("Arial", Font.ITALIC, 13));
		txtInfotext.setText(Language.Einleitung);
		txtInfotext.setBounds(10, 158, 566, 68);
		getContentPane().add(txtInfotext);
		
		
		panelÜberblendung = new JPanel();
		panelÜberblendung.setBorder(new TitledBorder(null, "private", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelÜberblendung.setBounds(4, 238, 806, 391);
		getContentPane().add(panelÜberblendung);
		panelÜberblendung.setLayout(null);
		panelÜberblendung.setVisible(false);
		
		btnÜberblendungSchließen = new JButton("X");
		btnÜberblendungSchließen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				btnPrivateKeyAusblenden.setSelected(false);
				panelÜberblendung.setVisible(false);
				
			}
		});
		btnÜberblendungSchließen.setBounds(757, 0, 49, 21);
		panelÜberblendung.add(btnÜberblendungSchließen);
		btnÜberblendungSchließen.setFont(new Font("Arial Black", Font.PLAIN, 14));
		
		txtÜberblendung = new JTextPane();
		txtÜberblendung.setBounds(10, 15, 786, 365);
		panelÜberblendung.add(txtÜberblendung);
		txtÜberblendung.setFont(new Font("SimSun-ExtB", Font.BOLD, 4));
		txtÜberblendung.setEditable(false);
		txtÜberblendung.setText(AsciiLogos.Private);
		
		txtLogo = new JTextPane();
		txtLogo.setText(AsciiLogos.BitcoinLogo);
		txtLogo.setFont(new Font("SimSun-ExtB", Font.PLAIN, 5));
		txtLogo.setEditable(false);
		txtLogo.setBounds(566, 23, 250, 203);
		getContentPane().add(txtLogo);
		
		txtLogo2 = new JTextPane();
		txtLogo2.setText(AsciiLogos.BitcoinText);
		txtLogo2.setFont(new Font("MS Gothic", Font.BOLD, 4));
		txtLogo2.setEditable(false);
		txtLogo2.setBounds(0, 21, 566, 205);
		getContentPane().add(txtLogo2);
		
		MaskFormatter formatter = new MaskFormatter("****************************************************************");
		formatter.setValidCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= ");
		txtPrivateKey = new JFormattedTextField(formatter);
		txtPrivateKey.setHorizontalAlignment(SwingConstants.LEFT);
		txtPrivateKey.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				panelInfo.setVisible(false);
				txtPrivateKey.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
			}
		});	
		txtPrivateKey.setFont(new Font("Courier New", Font.PLAIN, 14));
		txtPrivateKey.setBounds(26, 468, 518, 23);
		getContentPane().add(txtPrivateKey);
		
		txtEingabePassphrase = new JTextPane();
		txtEingabePassphrase.setBorder(BorderFactory.createEtchedBorder());
		txtEingabePassphrase.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				panelInfo.setVisible(false);
				txtEingabePassphrase.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30]))); 
			}
		});
		txtEingabePassphrase.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtEingabePassphrase.setBounds(25, 279, 773, 42);
		getContentPane().add(txtEingabePassphrase);
		
		MaskFormatter formatWuerfel = new MaskFormatter("##########-##########-##########-##########-##########-##########-##########-##########-##########-##########");
		formatWuerfel.setValidCharacters("123456");
		txtWuerfel = new JFormattedTextField(formatWuerfel);
		txtWuerfel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				panelInfo.setVisible(false);
				txtWuerfel.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
			}
		});
		txtWuerfel.setFont(new Font("Courier New", Font.PLAIN, 11));
		txtWuerfel.setBounds(26, 390, 772, 23);
		getContentPane().add(txtWuerfel);
		
		lblPassphrase = new JLabel(Language.lblPassphrase);
		lblPassphrase.setFont(new Font("Dialog", Font.PLAIN, 14));
		lblPassphrase.setBounds(25, 254, 519, 23);
		getContentPane().add(lblPassphrase);
		
		lblGebenSieEinen = new JLabel(Language.lblGebenSieEinen);
		lblGebenSieEinen.setFont(new Font("Dialog", Font.PLAIN, 11));
		lblGebenSieEinen.setBounds(25, 320, 518, 14);
		getContentPane().add(lblGebenSieEinen);
		
		lblWuerfelEingabe = new JLabel(Language.lblWuerfelEingabe);
		lblWuerfelEingabe.setFont(new Font("Dialog", Font.PLAIN, 14));
		lblWuerfelEingabe.setBounds(26, 367, 518, 23);
		getContentPane().add(lblWuerfelEingabe);
		
		lblWuerfelzeichenEingeben = new JLabel(Language.lblWuerfelzeichenEingeben);
		lblWuerfelzeichenEingeben.setFont(new Font("Dialog", Font.PLAIN, 11));
		lblWuerfelzeichenEingeben.setBounds(25, 413, 519, 14);
		getContentPane().add(lblWuerfelzeichenEingeben);
		
		lblPrivateKeyIn = new JLabel(Language.lblPrivateKeyIn);
		lblPrivateKeyIn.setFont(new Font("Dialog", Font.PLAIN, 14));
		lblPrivateKeyIn.setBounds(26, 447, 518, 17);
		getContentPane().add(lblPrivateKeyIn);
		
		lblPrivateKeyOut = new JLabel(Language.lblPrivateKeyIn_1);
		lblPrivateKeyOut.setFont(new Font("Dialog", Font.BOLD, 16));
		lblPrivateKeyOut.setBounds(277, 579, 267, 23);
		getContentPane().add(lblPrivateKeyOut);
		
		lblPublicKeyX = new JLabel(Language.lblPublicKeyX);
		lblPublicKeyX.setFont(new Font("Dialog", Font.PLAIN, 13));
		lblPublicKeyX.setBounds(233, 667, 95, 23);
		getContentPane().add(lblPublicKeyX);
		
		lblPublicKeyY = new JLabel(Language.lblPublicKeyY);
		lblPublicKeyY.setFont(new Font("Dialog", Font.PLAIN, 13));
		lblPublicKeyY.setBounds(233, 697, 95, 23);
		getContentPane().add(lblPublicKeyY);
		
		txtBitcoinAdress = new JTextField();
		txtBitcoinAdress.setHorizontalAlignment(SwingConstants.CENTER);
		txtBitcoinAdress.setFont(new Font("Courier New", Font.PLAIN, 18));
		txtBitcoinAdress.setEditable(false);
		txtBitcoinAdress.setColumns(10);
		txtBitcoinAdress.setBounds(327, 784, 471, 33);
		getContentPane().add(txtBitcoinAdress);
		
		txtOutPrivateKey = new JTextField();
		txtOutPrivateKey.setHorizontalAlignment(SwingConstants.CENTER);
		txtOutPrivateKey.setFont(new Font("Courier New", Font.PLAIN, 14));
		txtOutPrivateKey.setEditable(false);
		txtOutPrivateKey.setBounds(277, 605, 521, 23);
		getContentPane().add(txtOutPrivateKey);
		txtOutPrivateKey.setColumns(10);
		
		txtPublicKeyX = new JTextField();
		txtPublicKeyX.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPublicKeyX.setEditable(false);
		txtPublicKeyX.setFont(new Font("Courier New", Font.PLAIN, 12));
		txtPublicKeyX.setBounds(327, 669, 471, 23);
		getContentPane().add(txtPublicKeyX);
		txtPublicKeyX.setColumns(10);
		
		lblBitcoinAdressOut = new JLabel(Language.lblBitcoinAdress);
		lblBitcoinAdressOut.setHorizontalAlignment(SwingConstants.LEFT);
		lblBitcoinAdressOut.setFont(new Font("Dialog", Font.BOLD, 16));
		lblBitcoinAdressOut.setBounds(327, 756, 471, 26);
		getContentPane().add(lblBitcoinAdressOut);
		
		lblBase58 = new JLabel(Language.lblBase58);
		lblBase58.setFont(new Font("Dialog", Font.PLAIN, 11));
		lblBase58.setBounds(327, 817, 193, 14);
		getContentPane().add(lblBase58);
				
		txtFehler = new JTextPane();
		txtFehler.setEditable(false);
		txtFehler.setForeground(new Color(255, 0, 0));
		txtFehler.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 20));
		txtFehler.setBounds(176, 502, 622, 55);
		getContentPane().add(txtFehler);
		
		txtQRBildFehler = new JTextPane();
		txtQRBildFehler.setForeground(Color.RED);
		txtQRBildFehler.setFont(new Font("Tahoma", Font.ITALIC, 28));
		txtQRBildFehler.setEditable(false);
		txtQRBildFehler.setBounds(26, 857, 302, 42);
		txtQRBildFehler.setVisible(false);
		getContentPane().add(txtQRBildFehler);
		
		lblBitcoinAdress = new JLabel("");
		lblBitcoinAdress.setFont(new Font("Dialog", Font.PLAIN, 11));
		lblBitcoinAdress.setHorizontalAlignment(SwingConstants.CENTER);
		lblBitcoinAdress.setBounds(10, 730, 221, 20);
		getContentPane().add(lblBitcoinAdress);
		
		lblBitcoinAdresseQRCode = new JLabel("");
		lblBitcoinAdresseQRCode.setHorizontalAlignment(SwingConstants.CENTER);
		lblBitcoinAdresseQRCode.setFont(new Font("Courier New", Font.PLAIN, 10));
		lblBitcoinAdresseQRCode.setBounds(10, 933, 220, 17);
		getContentPane().add(lblBitcoinAdresseQRCode);
		
		lblFarben2 = new JTextPane();
		lblFarben2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFarben2.setEditable(false);
		lblFarben2.setBounds(10, 51, 89, 344);
		panelStyle.add(lblFarben2);
		lblFarben2.setText(Language.lblFarben2);
		
		btnPrivateKeyAusblenden = new JCheckBox(Language.btnPrivateKeyAusblenden);
		btnPrivateKeyAusblenden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				if(btnPrivateKeyAusblenden.isSelected())panelÜberblendung.setVisible(true);
				else panelÜberblendung.setVisible(false);
			}
		});
		btnPrivateKeyAusblenden.setBounds(544, 865, 254, 23);
		btnPrivateKeyAusblenden.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		getContentPane().add(btnPrivateKeyAusblenden);
		
		panelQRCode = new JPanel();
		panelQRCode.setBounds(10, 730, 220, 220);
		getContentPane().add(panelQRCode);
		
		txtPublicKeyY = new JTextField();
		comboBoxPrivateKey = new JComboBox();
		
		

		
		
		

		
		
// ---------------------------------------------------- Colors are changed ----------------------------------------------------------------------------------------------

		while(I<30)
		{
			slider[I] = new JSlider();	
			slider[I].setMaximum(255);	
			slider[I].setMajorTickSpacing(I);
			panelStyle.add(slider[I]);
			slider[I].addChangeListener(new ChangeListener() 
			{
				public void stateChanged(ChangeEvent e) 
				{
					int n = getEventNum(e);	
					Config.settings[n+13] = Integer.toString(slider[n].getValue());	
					paintAllSettings();										
				}
			});
			I++;
		}
		
		slider[ 0].setBounds(100,  51, 100, 20);
		slider[ 1].setBounds(100,  71, 100, 20);
		slider[ 2].setBounds(100,  91, 100, 20);
		slider[ 3].setBounds(220, 51, 100, 20);
		slider[ 4].setBounds(220, 71, 100, 20);
		slider[ 5].setBounds(220, 91, 100, 20);
		slider[ 6].setBounds(100, 122, 100, 20);
		slider[ 7].setBounds(100, 142, 100, 20);
		slider[ 8].setBounds(100, 162, 100, 20);
		slider[ 9].setBounds(220,122, 100, 20);
		slider[10].setBounds(220,142, 100, 20);
		slider[11].setBounds(220,162, 100, 20);
		slider[12].setBounds(100, 193, 100, 20);
		slider[13].setBounds(100, 213, 100, 20);
		slider[14].setBounds(100, 233, 100, 20);
		slider[15].setBounds(220,193, 100, 20);
		slider[16].setBounds(220,213, 100, 20);
		slider[17].setBounds(220,233, 100, 20);
		slider[18].setBounds(100, 264, 100, 20);
		slider[19].setBounds(100, 284, 100, 20);
		slider[20].setBounds(100, 304, 100, 20);
		slider[21].setBounds(220,264, 100, 20);
		slider[22].setBounds(220,284, 100, 20);
		slider[23].setBounds(220,304, 100, 20);
		slider[24].setBounds(100, 335, 100, 20);
		slider[25].setBounds(100, 355, 100, 20);
		slider[26].setBounds(100, 375, 100, 20);
		slider[27].setBounds(220,335, 100, 20);
		slider[28].setBounds(220,355, 100, 20);
		slider[29].setBounds(220,375, 100, 20);
		setAllSlider();	
		

	
		
		
// -------------------------------------------------------- Action ----------------------------------------------------- //
			
		JButton btnLoadDefaultSetting = new JButton(Language.loadDefaultSettings);
		btnLoadDefaultSetting.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				Config.loadDefaultConfig();	
			    	Config.saveConfig(Config.name);
				panelStyle.setVisible(false); 
				setAllSlider();
			}
		});
		btnLoadDefaultSetting.setFont(new Font("Dialog", Font.PLAIN, 10));
		btnLoadDefaultSetting.setBounds(10, 419, 214, 20);
		panelStyle.add(btnLoadDefaultSetting);
		
		lblFarben1 = new JLabel();
		lblFarben1.setBounds(111, 26, 203, 24);
		lblFarben1.setText(Language.lblFarben1);
		panelStyle.add(lblFarben1);
		

		
		JButton btnDrucken = new JButton(Language.btnDrucken);
		btnDrucken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{                         
				panelInfo.setVisible(false);
				Print.PrintImage(getContentPane());
			}
		});
		btnDrucken.setBounds(544, 917, 121, 33);
		getContentPane().add(btnDrucken);
		
		JButton btnSpeichern = new JButton(Language.btnSpeichern);
		btnSpeichern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				panelInfo.setVisible(false);
				try   {Save.saveGUI(getContentPane(),txtBitcoinAdress.getText()+".png");} 
				catch (IOException e1){e1.printStackTrace();}	
			}
		});
		btnSpeichern.setBounds(675, 917, 123, 33);
		getContentPane().add(btnSpeichern);
		
		
		txtPublicKeyY.addCaretListener(new CaretListener() 
		{
		   public void caretUpdate(CaretEvent arg0) 
		   {
			  panelInfo.setVisible(false);
			  panelQRCode.removeAll();					
			  panelQRCode.repaint();			
			  if(txtPublicKeyX.getText().equals("")==false  &&  txtPublicKeyY.getText().equals("")==false)		
			  {	
				try {
					txtBitcoinAdress.setText(Calc.ConvertPublicKeyToBitcoinAdress(txtPublicKeyX.getText() + txtPublicKeyY.getText()));
					BufferedImage qrCode = QRCodeReaderZXING.writeQRCode(txtBitcoinAdress.getText());
					panelQRCode.add(new JLabel(new ImageIcon(qrCode)));			
					lblBitcoinAdresseQRCode.setText(QRCodeReaderZXING.readQRCode(qrCode,false));
					lblBitcoinAdress.setText(Language.lblBitcoinAdress);		
					if(lblBitcoinAdresseQRCode.getText().equals(txtBitcoinAdress.getText())==false)
					{ 
					  txtQRBildFehler.setText(Language.F2);
					  txtQRBildFehler.setVisible(true);	
					}
					else 		
					{
					   txtQRBildFehler.setText("");		
					   txtQRBildFehler.setVisible(false);
					}
					getContentPane().repaint();		
				} 
				catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {e.printStackTrace();}	
			  }	
		   }
		});
		txtPublicKeyY.setHorizontalAlignment(SwingConstants.RIGHT);
		txtPublicKeyY.setFont(new Font("Courier New", Font.PLAIN, 12));
		txtPublicKeyY.setEditable(false);
		txtPublicKeyY.setColumns(10);
		txtPublicKeyY.setBounds(327, 697, 471, 23);
		getContentPane().add(txtPublicKeyY);	
		
																	
		comboBoxPrivateKey.setFont(new Font("Dialog", Font.PLAIN, 11));
		comboBoxPrivateKey.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0)                   
			{
				panelInfo.setVisible(false);
				switch(comboBoxPrivateKey.getSelectedIndex())
				{
				case 0:  txtOutPrivateKey.setText(VariablePrivateKey.getText());        								break;  
				case 1: try {txtOutPrivateKey.setText(Calc.ConvertPrivKeyToBitcoinConformBase58(VariablePrivateKey.getText()));} 								
				        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {e.printStackTrace();}	                		break;
				case 2: try {txtOutPrivateKey.setText(Calc.ConvertPrivKeyToBitcoinConformBase58Compressed(VariablePrivateKey.getText()));} 					
		                catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {e.printStackTrace();}	                			break;
				case 3: txtOutPrivateKey.setText(new String(Base64.getEncoder().encode(Convert.hexStringToByteArray(VariablePrivateKey.getText()))));  	break;  
				}
				Config.settings[11] = Integer.toString(comboBoxPrivateKey.getSelectedIndex());
				Config.saveConfig(Config.name);
			}
		});
		comboBoxPrivateKey.setModel(new DefaultComboBoxModel(Language.comboBoxPrivateKey));	
		comboBoxPrivateKey.setMaximumRowCount(4);
		comboBoxPrivateKey.setBounds(176, 605, 95, 23);
		getContentPane().add(comboBoxPrivateKey);
		
		
		
		
		JButton btnEnterPassphrase = new JButton(Language.btnEnter);					
		btnEnterPassphrase.addActionListener(new ActionListener() 			
		{
			public void actionPerformed(ActionEvent arg0) 
			{																								
			  panelInfo.setVisible(false);
			  panelQRCode.removeAll();						
			  panelQRCode.repaint();																										
			  try {VariablePrivateKey.setText(Calc.getHashSHA256(txtEingabePassphrase.getText()));}                   		 					
			  catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {e.printStackTrace();}	
			  txtWuerfel.setForeground(new Color(Integer.parseInt(Config.settings[25]),Integer.parseInt(Config.settings[26]),Integer.parseInt(Config.settings[27])));	
			  txtPrivateKey.setForeground(new Color(Integer.parseInt(Config.settings[25]), Integer.parseInt(Config.settings[26]), Integer.parseInt(Config.settings[27])));	
			  txtEingabePassphrase.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
			  if(txtEingabePassphrase.getText().length() < 8) { txtFehler.setText(Language.W4); txtFehler.setForeground(Color.ORANGE);}				
			  else {txtFehler.setText(""); txtFehler.setForeground(Color.RED);}								
			}
		});
		btnEnterPassphrase.setBounds(709, 327, 89, 23);
		getContentPane().add(btnEnterPassphrase);
		

		JButton btnEnterWuerfel = new JButton(Language.btnEnter);					
		btnEnterWuerfel.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{                                                                                               
				panelInfo.setVisible(false);
				panelQRCode.removeAll();														
				panelQRCode.repaint();								
				if(txtWuerfel.getText().equals("          -          -          -          -          -          -          -          -          -          ")==false  && txtWuerfel.getText().equals("1111111111-1111111111-1111111111-1111111111-1111111111-1111111111-1111111111-1111111111-1111111111-1111111111")==false)
				  {	 
				     VariablePrivateKey.setText(Calc.wuerfelToHexString(txtWuerfel.getText()));                                														
				     txtEingabePassphrase.setForeground(new Color(Integer.parseInt(Config.settings[25]),Integer.parseInt(Config.settings[26]),Integer.parseInt(Config.settings[27])));	
				     txtPrivateKey.setForeground(new Color(Integer.parseInt(Config.settings[25]),Integer.parseInt(Config.settings[26]),Integer.parseInt(Config.settings[27])));		
				     txtWuerfel.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));			
				  }
				 else { txtFehler.setText(Language.F0);  txtFehler.setForeground(Color.RED);}																							
			}
		});
		btnEnterWuerfel.setBounds(709, 419, 89, 23);
		getContentPane().add(btnEnterWuerfel);
		

		
	
		JButton btnEnterPrivateKey = new JButton(Language.btnEnter);															
		btnEnterPrivateKey.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{																																						
				panelInfo.setVisible(false);  
				  panelQRCode.removeAll();																															
				  panelQRCode.repaint();																																			
				  if(txtPrivateKey.getText().equals("                                                                ")==false && txtPrivateKey.getText().equals("0000000000000000000000000000000000000000000000000000000000000000")==false )
				  {	                                               																														
				    try
					{VariablePrivateKey.setText(Calc.txtToHexPrivKey(txtPrivateKey.getText()));} 																						
				    catch (KeyException e1) {txtFehler.setText(e1.getMessage());  txtFehler.setForeground(Color.RED);}																	
				    txtEingabePassphrase.setForeground(new Color(Integer.parseInt(Config.settings[25]),Integer.parseInt(Config.settings[26]),Integer.parseInt(Config.settings[27])));	
				    txtWuerfel.setForeground(new Color(Integer.parseInt(Config.settings[25]),Integer.parseInt(Config.settings[26]),Integer.parseInt(Config.settings[27])));				
				    txtPrivateKey.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));		
				  }
				  else { txtFehler.setText(Language.F0);  txtFehler.setForeground(Color.RED);}	  				
			}
		});
		btnEnterPrivateKey.setBounds(554, 468, 89, 23);
		getContentPane().add(btnEnterPrivateKey);
		
		
		
		txtPrivateKey.addActionListener(new ActionListener() 																													
		{
			public void actionPerformed(ActionEvent e) 
			{																																						
				  panelInfo.setVisible(false);
				  panelQRCode.removeAll();																																			
				  panelQRCode.repaint();																																			
				  if(txtPrivateKey.getText().equals("                                                                ")==false && txtPrivateKey.getText().equals("0000000000000000000000000000000000000000000000000000000000000000")==false )
				  {	                                               																														
					try
					{VariablePrivateKey.setText(Calc.txtToHexPrivKey(txtPrivateKey.getText()));} 																						
					catch (KeyException e1) {txtFehler.setText(e1.getMessage());  txtFehler.setForeground(Color.RED);}																
				    	txtEingabePassphrase.setForeground(new Color(Integer.parseInt(Config.settings[25]),Integer.parseInt(Config.settings[26]),Integer.parseInt(Config.settings[27])));	
				    	txtWuerfel.setForeground(new Color(Integer.parseInt(Config.settings[25]),Integer.parseInt(Config.settings[26]),Integer.parseInt(Config.settings[27])));				
				    	txtPrivateKey.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));		
				  }
				  else { txtFehler.setText(Language.F0);  txtFehler.setForeground(Color.RED);} 		
			}
		});
		
		
		
		

		
		VariablePrivateKey = new JTextField();			
		VariablePrivateKey.addCaretListener(new CaretListener() 
		{
			public void caretUpdate(CaretEvent arg0) 										
			{	
			  panelQRCode.removeAll();										
			  panelQRCode.repaint();												
			  if(VariablePrivateKey.getText().equals("")==false)						
			  {	  
				switch(comboBoxPrivateKey.getSelectedIndex())							
				{
				case 0:  txtOutPrivateKey.setText(VariablePrivateKey.getText());        								break;
				case 1: try {txtOutPrivateKey.setText(Calc.ConvertPrivKeyToBitcoinConformBase58(VariablePrivateKey.getText()));} 
				        catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {e.printStackTrace();}	                		break;
				case 2: try {txtOutPrivateKey.setText(Calc.ConvertPrivKeyToBitcoinConformBase58Compressed(VariablePrivateKey.getText()));} 
		                catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {e.printStackTrace();}	                			break;
				case 3: txtOutPrivateKey.setText(new String(Base64.getEncoder().encode(Convert.hexStringToByteArray(VariablePrivateKey.getText()))));  	break;
				}
				txtPublicKeyX.setText(Calc.getPublicKeyX(VariablePrivateKey.getText()));  	
				txtPublicKeyY.setText(Calc.getPublicKeyY(VariablePrivateKey.getText()));	
		      }	
			  if(Calc.is_PrivKey_Valid(VariablePrivateKey.getText()) ==-1) {txtFehler.setText(Language.F0); txtFehler.setForeground(Color.RED);	}
			  if(Calc.is_PrivKey_Valid(VariablePrivateKey.getText()) == 1) {txtFehler.setText(Language.W2); txtFehler.setForeground(Color.ORANGE);	}
			  if(Calc.is_PrivKey_Valid(VariablePrivateKey.getText()) == 2) {txtFehler.setText(Language.W3); txtFehler.setForeground(Color.ORANGE);	}
			  if(Calc.is_PrivKey_Valid(VariablePrivateKey.getText()) == 0) txtFehler.setText("");						
			  lblLink.setText("<HTML><U>BLOCKCHAIN_"+txtBitcoinAdress.getText()+"</U><HTML>");
			}
		});
		VariablePrivateKey.setFont(new Font("Courier New", Font.PLAIN, 14));
		VariablePrivateKey.setEditable(false);					
		VariablePrivateKey.setBounds(0, 225, 816, 1);
		getContentPane().add(VariablePrivateKey);
		VariablePrivateKey.setColumns(10);
		
		comboBoxPrivateKey.setSelectedIndex(Integer.parseInt(Config.settings[11]));
		
		
		
		
	lblLink = new JLabel();				
	lblLink.setFont(new Font("Tahoma", Font.ITALIC, 11));
	lblLink.setForeground(new Color(0, 0, 255));
	lblLink.addMouseListener(new MouseAdapter() 
	{
		public void mouseClicked(MouseEvent arg0) 
		{
			try {Desktop.getDesktop().browse(new URI("https://blockchain.info/address/"+txtBitcoinAdress.getText()));} 	
			catch (URISyntaxException | IOException ex) {}
		}
	});
	lblLink.setBounds(237, 933, 293, 18);
	getContentPane().add(lblLink);
	
	
		
	comboBoxSprache.addMouseListener(new MouseAdapter() 
	{
		public void mouseEntered(MouseEvent arg0) 
		{
			panelStyle.setVisible(false);
			panelInfo.setVisible(false);
			Config.loadConfig(Config.name);
			setAllSlider();
		}
	});
	comboBoxSprache.addItemListener(new ItemListener() 
	{   public void itemStateChanged(ItemEvent arg0) 
		{
		  switch(comboBoxSprache.getSelectedIndex())
		  {
		  case 0: Language.setLanguage(0); lblPublicKeyX.setFont(new Font("Dialog", Font.PLAIN, 13)); lblPublicKeyY.setFont(new Font("Dialog", Font.PLAIN, 13)); break;
		  case 1: Language.setLanguage(1); lblPublicKeyX.setFont(new Font("Dialog", Font.PLAIN, 12)); lblPublicKeyY.setFont(new Font("Dialog", Font.PLAIN, 12)); break;
		  case 2: Language.setLanguage(2); lblPublicKeyX.setFont(new Font("Dialog", Font.PLAIN, 12)); lblPublicKeyY.setFont(new Font("Dialog", Font.PLAIN, 12)); break;
		  case 3: Language.setLanguage(3); lblPublicKeyX.setFont(new Font("Dialog", Font.PLAIN, 11)); lblPublicKeyY.setFont(new Font("Dialog", Font.PLAIN, 11)); break;
		  case 4: Language.setLanguage(4); lblPublicKeyX.setFont(new Font("Dialog", Font.PLAIN, 14)); lblPublicKeyY.setFont(new Font("Dialog", Font.PLAIN, 14)); break;
		  }	
		  	
		    panelInfo.setVisible(false);
		    panelInfo.setBorder(new TitledBorder(null, Language.btnInfo, TitledBorder.LEADING, TitledBorder.TOP, null, null));
		    txtInfo.setText(				Language.InfoText);
		    txtInfotext.setText(			Language.Einleitung);				
		    lblPassphrase.setText(			Language.lblPassphrase);
		  	lblGebenSieEinen.setText(		Language.lblGebenSieEinen);
		  	lblWuerfelEingabe.setText(		Language.lblWuerfelEingabe);
		  	lblWuerfelzeichenEingeben.setText(	Language.lblWuerfelzeichenEingeben);
		  	lblPrivateKeyIn.setText(  		Language.lblPrivateKeyIn);
		  	lblPrivateKeyOut.setText(  		Language.lblPrivateKeyIn_1);
		  	lblPublicKeyX.setText(  		Language.lblPublicKeyX);
		  	lblPublicKeyY.setText(  		Language.lblPublicKeyY); 
		  	lblBitcoinAdressOut.setText(  		Language.lblBitcoinAdress);
		  	lblBase58.setText(  			Language.lblBase58);
		  	lblFarben1.setText(			Language.lblFarben1);
		  	lblFarben2.setText(			Language.lblFarben2);
		  	btnStyle.setText(               	Language.btnStyle);
		  	btnInfo.setText(			Language.btnInfo);
		  	btnDrucken.setText(  			Language.btnDrucken);
		  	btnSpeichern.setText(  			Language.btnSpeichern);
		  	btnEnterPassphrase.setText(  		Language.btnEnter);
		  	btnEnterWuerfel.setText(  		Language.btnEnter);
		  	btnEnterPrivateKey.setText(  		Language.btnEnter);
		  	btnFarbe1.setText(              	Language.btnSpeichern);
		  	btnLoadDefaultSetting.setText(		Language.loadDefaultSettings);
		  	btnPrivateKeyAusblenden.setText(	Language.btnPrivateKeyAusblenden);
		  	comboBoxPrivateKey.setModel(new DefaultComboBoxModel(Language.comboBoxPrivateKey));
		  	comboBoxPrivateKey.setSelectedIndex(Integer.parseInt(Config.settings[11]));	
		  	comboBoxSprache.repaint(); 	
		  	panelStyle.setBorder(new TitledBorder(null, Language.farbenAnpassen, TitledBorder.LEADING, TitledBorder.TOP, null,null));
		  	Config.saveConfig(Config.name);	
		}
	});	
  }
	
	
	
	
// Redraw color settings
	public void paintAllSettings()
	{
	    	getContentPane().setBackground(new Color(Integer.parseInt(Config.settings[19]), Integer.parseInt(Config.settings[20]), Integer.parseInt(Config.settings[21])));
	    	panelStyle.setBackground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
	    	txtFehler.setBackground(new Color(Integer.parseInt(Config.settings[19]), Integer.parseInt(Config.settings[20]), Integer.parseInt(Config.settings[21])));
	    	txtLogo.setForeground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
	    	txtLogo.setBackground(new Color(Integer.parseInt(Config.settings[13]), Integer.parseInt(Config.settings[14]), Integer.parseInt(Config.settings[15])));
	    	txtLogo2.setForeground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
	    	txtLogo2.setBackground(new Color(Integer.parseInt(Config.settings[13]), Integer.parseInt(Config.settings[14]), Integer.parseInt(Config.settings[15])));
	    	txtInfotext.setForeground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
	    	txtInfotext.setBackground(new Color(Integer.parseInt(Config.settings[13]), Integer.parseInt(Config.settings[14]), Integer.parseInt(Config.settings[15])));
	    	panelInfo.setBackground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
	    
		lblPassphrase.setForeground(new Color(Integer.parseInt(Config.settings[22]), Integer.parseInt(Config.settings[23]), Integer.parseInt(Config.settings[24])));
		lblGebenSieEinen.setForeground(new Color(Integer.parseInt(Config.settings[22]), Integer.parseInt(Config.settings[23]), Integer.parseInt(Config.settings[24])));
		
		txtEingabePassphrase.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
		txtEingabePassphrase.setBackground(new Color(Integer.parseInt(Config.settings[25]), Integer.parseInt(Config.settings[26]), Integer.parseInt(Config.settings[27])));
		
		lblWuerfelEingabe.setForeground(new Color(Integer.parseInt(Config.settings[22]), Integer.parseInt(Config.settings[23]), Integer.parseInt(Config.settings[24])));
		lblWuerfelzeichenEingeben.setForeground(new Color(Integer.parseInt(Config.settings[22]), Integer.parseInt(Config.settings[23]), Integer.parseInt(Config.settings[24])));
		txtWuerfel.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
		txtWuerfel.setBackground(new Color(Integer.parseInt(Config.settings[25]), Integer.parseInt(Config.settings[26]), Integer.parseInt(Config.settings[27])));
		
		lblPrivateKeyOut.setForeground(new Color(Integer.parseInt(Config.settings[34]), Integer.parseInt(Config.settings[35]), Integer.parseInt(Config.settings[36])));
		lblPrivateKeyIn.setForeground(new Color(Integer.parseInt(Config.settings[22]), Integer.parseInt(Config.settings[23]), Integer.parseInt(Config.settings[24])));
		txtPrivateKey.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
		txtPrivateKey.setBackground(new Color(Integer.parseInt(Config.settings[25]), Integer.parseInt(Config.settings[26]), Integer.parseInt(Config.settings[27])));
		
		comboBoxPrivateKey.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
		comboBoxPrivateKey.setBackground(new Color(Integer.parseInt(Config.settings[31]), Integer.parseInt(Config.settings[32]), Integer.parseInt(Config.settings[33])));
		txtOutPrivateKey.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
		txtOutPrivateKey.setBackground(new Color(Integer.parseInt(Config.settings[31]), Integer.parseInt(Config.settings[32]), Integer.parseInt(Config.settings[33])));
		
	
		lblPublicKeyX.setForeground(new Color(Integer.parseInt(Config.settings[40]), Integer.parseInt(Config.settings[41]), Integer.parseInt(Config.settings[42])));
		lblPublicKeyY.setForeground(new Color(Integer.parseInt(Config.settings[40]), Integer.parseInt(Config.settings[41]), Integer.parseInt(Config.settings[42])));
		txtPublicKeyX.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
		txtPublicKeyX.setBackground(new Color(Integer.parseInt(Config.settings[37]), Integer.parseInt(Config.settings[38]), Integer.parseInt(Config.settings[39])));
		txtPublicKeyY.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
		txtPublicKeyY.setBackground(new Color(Integer.parseInt(Config.settings[37]), Integer.parseInt(Config.settings[38]), Integer.parseInt(Config.settings[39])));
		
	    	panelQRCode.setBackground(new Color(Integer.parseInt(Config.settings[19]), Integer.parseInt(Config.settings[20]), Integer.parseInt(Config.settings[21])));
		txtQRBildFehler.setBackground(new Color(Integer.parseInt(Config.settings[19]), Integer.parseInt(Config.settings[20]), Integer.parseInt(Config.settings[21])));
		
		lblBitcoinAdressOut.setForeground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
		lblBase58.setForeground(new Color(Integer.parseInt(Config.settings[22]), Integer.parseInt(Config.settings[23]), Integer.parseInt(Config.settings[24])));
		txtBitcoinAdress.setBackground(new Color(Integer.parseInt(Config.settings[13]), Integer.parseInt(Config.settings[14]), Integer.parseInt(Config.settings[15])));
		txtBitcoinAdress.setForeground(new Color(Integer.parseInt(Config.settings[28]), Integer.parseInt(Config.settings[29]), Integer.parseInt(Config.settings[30])));
		
		lblFarben2.setBackground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
		btnPrivateKeyAusblenden.setBackground(new Color(Integer.parseInt(Config.settings[19]), Integer.parseInt(Config.settings[20]), Integer.parseInt(Config.settings[21])));
		btnPrivateKeyAusblenden.setForeground(new Color(Integer.parseInt(Config.settings[22]), Integer.parseInt(Config.settings[23]), Integer.parseInt(Config.settings[24])));
		
		panelÜberblendung.setBackground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
		txtÜberblendung.setForeground(new Color(Integer.parseInt(Config.settings[16]), Integer.parseInt(Config.settings[17]), Integer.parseInt(Config.settings[18])));
		txtÜberblendung.setBackground(new Color(Integer.parseInt(Config.settings[13]), Integer.parseInt(Config.settings[14]), Integer.parseInt(Config.settings[15])));
		getContentPane().repaint();
	}
	
	
	
	
	
// -----------------------------------------------------  helper methods ----------------------------------------------------------------------------------------------------	
	
	public void setAllSlider()
	{
		int f=13;
		for(int i=0; i<30; i++)
		{
		  slider[i].setValue(Integer.parseInt(Config.settings[f]));
		  f++;
		}
	}
	
	

   private int getEventNum(ChangeEvent e)
   {
	   int a 	  = e.toString().indexOf("=",190)+1;
	   int b	  = e.toString().indexOf(",",a);
	   String str = e.toString().substring(a,b);
	   return  Integer.parseInt(str);
   }
}
