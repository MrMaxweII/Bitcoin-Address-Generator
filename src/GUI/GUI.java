package GUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import java.awt.Component;
import java.awt.Desktop;
import javax.swing.Box;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.text.ParseException;
import java.time.LocalDate;
import javax.swing.border.LineBorder;
import javax.swing.text.MaskFormatter;
import BTClib3001.CoinParameter;
import CoinGen.Action;
import CoinGen.Config;
import CoinGen.Print;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;


public class GUI extends JFrame 
{
	public static final String			progName		= "Coin Address Generator";
	public static final String			version 		= "V3.1.0";
	public static final String			autor 			= "Mr. Maxwell";
	public static final String			eMail			= "Maxwell-KSP@gmx.de";
	public static final String			myBitcoinAddr 	= "12zeCvN7zbAi3JDQhC8tU3DBm35kDEUNiB";	
	public static GUI 					frame;			// Frame des Hauptprogrammes
	public static GUI_Wallet 			guiW;			// Frame der Wallet
	public static Color 				color1 			= new Color(255, 247, 230);		// Hintergrundfarbe des Programmes
	public static JFormattedTextField 	txt_wuerfel;
	public static JFormattedTextField	txt_privKey;
	public static JComboBox 			comboBox_coin 	= new JComboBox();
	public static JEditorPane 			txt_ausgabe 	= new JEditorPane("text/html","");
	public static JTabbedPane 			tabbedPane 		= new JTabbedPane(JTabbedPane.LEFT);
	public static JTextArea 			txt_inText 		= new JTextArea();
	public static JPanel 				panel_QRCode 	= new JPanel();
	public static JRadioButtonMenuItem 	compressed 		= new JRadioButtonMenuItem("WIF compressed");
	public static JRadioButtonMenuItem 	unCompressed	= new JRadioButtonMenuItem("WIF uncompressed");
	public static JRadioButtonMenuItem 	p2sh 			= new JRadioButtonMenuItem("P2SH");
	public static JRadioButtonMenuItem 	bech32 			= new JRadioButtonMenuItem("Bech32");

	
	
	
	
	public static void main(String[] args) 
	{
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					frame = new GUI();
					frame.setVisible(true);
					comboBox_coin.setSelectedIndex(0);
					Config.load();
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
	JMenuBar 	menuBar 		= new JMenuBar();
	JMenu 		mn_file 		= new JMenu("File");
	JMenu 		mn_addrForm		= new JMenu("Address Format");
	JMenu 		mn_settings 	= new JMenu("Settings");
	JMenu 		mn_Info 		= new JMenu("Info");
	JMenuItem 	mItem_newCoin 	= new JMenuItem("import new Coin");
	JMenuItem 	mItem_removeCoin= new JMenuItem("remove Coin");
	JMenuItem 	mntm_open 		= new JMenuItem("open");
	JMenuItem 	mntm_save 		= new JMenuItem("save");
	JMenuItem 	mntm_edit 		= new JMenuItem("edit");
	JMenuItem 	mntm_print 		= new JMenuItem("print");
	JTextPane 	txt_info 		= new JTextPane();
	JTextPane 	lbl1 			= new JTextPane();
	JTextPane 	lbl3 			= new JTextPane();
	JTextPane 	lbl_coinName 	= new JTextPane();
	JTextArea 	lbl2 			= new JTextArea();
	MaskFormatter formatWürfel	= new MaskFormatter("##########-##########-##########-##########-##########-##########-##########-##########-##########-##########");  
	MaskFormatter formatter 	= new MaskFormatter("****************************************************************");	// Format Eingabe in *EX 
	txt_privKey 				= new JFormattedTextField(formatter);	
	txt_wuerfel 				= new JFormattedTextField(formatWürfel);	
	ButtonGroup group 			= new ButtonGroup();
	JButton 	btn_Ok 			= new JButton("OK");
	
	setTitle(progName+"     "+version);	
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	setMinimumSize(new Dimension(600, 300));
	setBounds(100, 100, 875, 370);
	setContentPane(contentPane);	
	
	contentPane		.setLayout(new BorderLayout(0, 0));
	panel_ausgabe	.setLayout(new BorderLayout(0, 0));
	panel_eingabe	.setLayout(new BorderLayout(0, 0));
	panel_input1	.setLayout(new BorderLayout(0, 0));
	panel_QRCode	.setLayout(new BoxLayout(panel_QRCode, BoxLayout.X_AXIS));
	panel_input2	.setLayout(null);
	panel_input3	.setLayout(null);
	
	contentPane		.setBackground(color1);
	menuBar			.setBackground(color1);	
	mntm_open		.setBackground(color1);
	mntm_save		.setBackground(color1);
	mntm_edit		.setBackground(color1);
	mntm_print		.setBackground(color1);
	comboBox_coin	.setBackground(color1);
	compressed		.setBackground(color1);
	p2sh			.setBackground(color1);
	bech32			.setBackground(color1);
	mItem_newCoin	.setBackground(color1);
	mItem_removeCoin.setBackground(color1);
	txt_info		.setBackground(color1);
	txt_ausgabe		.setBackground(color1);
	panel_eingabe	.setBackground(color1);
	panel_eingabe	.setBackground(color1);
	tabbedPane		.setBackground(color1);
	panel_input1	.setBackground(color1);
	panel_input2	.setBackground(color1);
	panel_input3	.setBackground(color1);
	lbl1			.setBackground(color1);
	lbl1			.setBackground(color1);
	lbl_coinName	.setBackground(color1);
	lbl2			.setBackground(color1);
	lbl3			.setBackground(color1);
	panel_QRCode	.setBackground(color1);
	panel_ausgabe	.setBackground(color1);

	contentPane		.setBorder(new EmptyBorder(5, 5, 5, 5));
	lbl1			.setBorder(new LineBorder(color1, 7));
	lbl2			.setBorder(new LineBorder(color1, 10));
	lbl3			.setBorder(new LineBorder(color1, 10));	
	btn_Ok			.setBorder(new LineBorder(color1, 6));
	lbl_coinName	.setBorder(new LineBorder(color1, 7));
	txt_inText		.setBorder(new LineBorder(color1, 7));
	tabbedPane		.setBorder(null);
	
	txt_info		.setFont(new Font("Century Gothic", Font.PLAIN, 12));
	tabbedPane		.setFont(new Font("Century Gothic", Font.PLAIN, 14));
	lbl1			.setFont(new Font("Century Gothic", Font.PLAIN, 15));
	lbl3			.setFont(new Font("Century Gothic", Font.PLAIN, 14));
	lbl_coinName	.setFont(new Font("Century Gothic", Font.PLAIN, 18));
	txt_inText		.setFont(new Font("Century Gothic", Font.PLAIN, 13));
	lbl2			.setFont(new Font("Century Gothic", Font.PLAIN, 14));
	txt_privKey		.setFont(new Font("Consolas", Font.PLAIN, 14));
	txt_wuerfel		.setFont(new Font("Consolas", Font.PLAIN, 11));
	txt_ausgabe		.setFont(new Font("Consolas", Font.PLAIN, 12));
	lbl_coinName	.setForeground(Color.GRAY);
	comboBox_coin	.setModel(new DefaultComboBoxModel(new String[] {"BTC"}));
	
	txt_ausgabe		.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
	txt_info		.setText(progName+"\n"+"Version:                "+version+"\n"+"Author:                  "+autor+"\n"+"E-Mail:                   "+eMail+"\n"+"please donate: "+myBitcoinAddr+"\n\nThis program creates a private-key in Wallet Import Format (WIF)\nas well as the associated address and the public-key.\nYou must enter 64 random hexadecimal characters or have them generated.");
	lbl1			.setText("SHA256 hash of a text");
	lbl2			.setText("Generate private-key by rolling dice.  \r\n100 characters, only digits between 1 and 6 are allowed!");
	lbl3			.setText("Enter a private key directly, \r\nthe format will be recognized automatically.");
	
	Component strut1 = Box.createHorizontalStrut(10);
	Component strut2 = Box.createHorizontalStrut(10);
	Component strut3 = Box.createHorizontalStrut(10);
	Component strut4 = Box.createHorizontalStrut(10);
	Component strut7 = Box.createVerticalStrut(15);
	
	strut1		.setEnabled(false);
	strut2		.setEnabled(false);
	strut3		.setEnabled(false);
	strut4		.setEnabled(false);
	strut7		.setEnabled(false);
	txt_info	.setEditable(false);
	txt_ausgabe	.setEditable(false);
	lbl1		.setEditable(false);
	lbl2		.setEditable(false);
	lbl3		.setEditable(false);
	lbl_coinName.setEditable(false);
	txt_inText	.setLineWrap(true);
	lbl2		.setLineWrap(true);
	compressed	.setSelected(true);

	lbl2		.setBounds(0, 0, 646, 54);
	lbl3		.setBounds(0, 0, 646, 54);
	txt_wuerfel	.setBounds(10, 56, 660, 20);
	txt_privKey	.setBounds(10, 54, 523, 20);

	btn_Ok		.setVerticalAlignment(SwingConstants.BOTTOM);	
	btn_Ok		.setMargin(new Insets(0, 0, 0, 0));
	btn_Ok		.setContentAreaFilled(false);
	btn_Ok		.setPreferredSize(new Dimension(40, 10));
	comboBox_coin.setMaximumSize(new Dimension(150, 32767));
	formatWürfel.setValidCharacters("123456");		
	formatter	.setValidCharacters("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/= ");
		
	group			.add(p2sh);
	group			.add(compressed);
	group			.add(bech32);
	group			.add(unCompressed);	
	tabbedPane		.addTab("Input Text-Hash", null, panel_input1, null);
	tabbedPane		.addTab("Input rolling dice", null, panel_input2, null);
	tabbedPane		.addTab("Input private key", null, panel_input3, null);
	contentPane		.add(menuBar, BorderLayout.NORTH);
	panel_ausgabe	.add(txt_ausgabe, BorderLayout.CENTER);
	contentPane		.add(panel_eingabe, BorderLayout.CENTER);
	panel_eingabe	.add(tabbedPane, BorderLayout.CENTER);
	menuBar			.add(mn_file);
	mn_file			.add(mntm_open);
	mn_file			.add(mntm_save);	
	mn_file			.add(mntm_edit);
	mn_file			.add(mntm_print);
	menuBar			.add(strut1);
	menuBar			.add(comboBox_coin);
	menuBar			.add(strut2);
	unCompressed	.setBackground(color1);
	mn_addrForm		.add(unCompressed);
	mn_addrForm		.add(compressed);
	menuBar			.add(mn_addrForm);	
	mn_addrForm		.add(p2sh);	
	mn_addrForm		.add(bech32);
	menuBar			.add(strut3);
	menuBar			.add(mn_settings);
	mn_settings		.add(mItem_newCoin);
	mn_settings		.add(mItem_removeCoin);
	menuBar			.add(strut4);
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
	panel_ausgabe	.add(strut7, BorderLayout.NORTH);

		
	
	
	
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
			try {lbl_coinName.setText(CoinParameter.getFromSymbol((String) comboBox_coin.getSelectedItem()).name);}
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
}




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
}