package GUI;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;
import CoinGen.Action;
import CoinGen.MyIcons;
import Wallet.Wallet;
import lib3001.btc.PrvKey;
import lib3001.crypt.Calc;
import lib3001.crypt.Convert;
import lib3001.network.CoinParameter;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;



/************************************************************************************************
*	V1.2									Mr.Maxwell							27.01.2026		*
*	Diese Klasse ist Teil der GUI des CoinAddressGenerators										*
*	Hier wird das Wallet-Fenster erstellt, welches beim Öffnen oder Speichern angezeigt wird.	*
************************************************************************************************/



public class GUI_Wallet extends JFrame 
{

	
	
	public DefaultTableModel 	dtm;						// Die Tabelle der Wallet
	private JLabel 				lbl_error = new JLabel();	// Meldungsfenster
	private JSONObject 			wallet;						// Die entschlüsselte Wallet-JSON-Datei
	
	
	
/**	Konstruktor, wird von der GUI aufgerufen
	Oberste Ebene der Wallet. 
	Löst alle Aktionen aus die zum Öffnen, editieren oder Speichern der Wallet führen.
	@param x x-pos des Fensters
	@param y y-pos des Fensters
	@param profil legt fest wie das Fenster gestaltet wird. Auswahlmöglichkeiten: "open", "save", "edit".  **/
public GUI_Wallet(int x, int y, String profil) throws Exception
{
	JPanel 		contentPane = new JPanel();
	JScrollPane scrollPane 	= new JScrollPane();
	JTable 		table 		= new JTable();
	JPanel 		panel 		= new JPanel();
	JButton 	btn_openPK 	= new JButton(GUI.t.t("Open PrivKey"));
	JButton		btn_openSeed= new JButton(GUI.t.t("Open Seed"));
	JButton 	btn_delete 	= new JButton(GUI.t.t("Delete"));
	JButton 	btn_save 	= new JButton(GUI.t.t("Save"));
	
	setBounds(x, y, 1200, 370);
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPane.setLayout(new BorderLayout(0, 0));
	setTitle("Wallet "+profil);
	setIconImage(MyIcons.key.getImage());
	setContentPane(contentPane);
	
	btn_openPK	.setIcon(MyIcons.keySmal);
	btn_openSeed.setIcon(MyIcons.key);
	btn_delete	.setIcon(MyIcons.delete);
	btn_save	.setIcon(MyIcons.save);	
	btn_openPK	.setPreferredSize( new Dimension(180,35));
	btn_openPK	.setMargin(new Insets(0,0,0,0));
	
	btn_openPK	.setToolTipText(GUI.t.t("Private Key der markierten Zeile öffnen"));
	btn_openSeed.setToolTipText(GUI.t.t("Private Key der markierten Zeile als Seed öffnen"));
	btn_delete	.setToolTipText(GUI.t.t("Markierte Zeile löschen"));
	btn_save	.setToolTipText(GUI.t.t("Alle Änderungen endgültig speichern"));	
	
	
	dtm = new DefaultTableModel(null , new String[] {GUI.t.t("Description"), "Coin", GUI.t.t("Address"), "Private Key", "Format", GUI.t.t("Date")}) 
	{
		boolean[] columnEditables = new boolean[] {true, false, false, false, false, false};
		public boolean isCellEditable(int row, int column) {return columnEditables[column];}
	};
	
	table.setRowHeight(20);
	table.setGridColor(new Color(240, 240, 240));
	table.setFont(new Font("Ubuntu Mono", Font.PLAIN, 13));
	table.setCellSelectionEnabled(true);
	table.putClientProperty ("terminateEditOnFocusLost", Boolean.TRUE);
	table.setModel(dtm);	
	table.getColumnModel().getColumn(0).setPreferredWidth(220);
	table.getColumnModel().getColumn(1).setPreferredWidth(15);
	table.getColumnModel().getColumn(2).setPreferredWidth(180);
	table.getColumnModel().getColumn(3).setPreferredWidth(310);
	table.getColumnModel().getColumn(4).setPreferredWidth(20);
	table.getColumnModel().getColumn(5).setPreferredWidth(20);
		
	scrollPane.setViewportView(table);	
	FlowLayout flowLayout = (FlowLayout) panel.getLayout();
	flowLayout.setAlignment(FlowLayout.RIGHT);
	lbl_error.setForeground(Color.RED);

	contentPane	.add(scrollPane, BorderLayout.CENTER);
	contentPane	.add(panel, BorderLayout.SOUTH);	
	panel		.add(lbl_error);
	panel		.add(btn_openPK);
	panel		.add(btn_openSeed);
	panel		.add(btn_delete);
	panel		.add(btn_save);
	
	loadWallet();
	
	if(profil.equals("open"))
	{
		btn_save.setVisible(false);	
		btn_delete.setVisible(false);
		table.addMouseListener(new MouseAdapter() 	// bei doppelklick wird sofort geöffnet	
		{
			@Override
			public void mousePressed(MouseEvent e) 
			{
				if(e.getClickCount() >= 2) btn_openPK.doClick(); 
			}
		});
	}
	if(profil.equals("save"))
	{
		btn_openPK.setVisible(false);
		btn_openSeed.setVisible(false);
		btn_delete.setVisible(false);
		setBounds(x, y, 875, 210);
		for(int i=0;i<dtm.getRowCount();i++)
		{
			table.setRowHeight(i, 1); // Alle Zeilenhöhen werden auf 1 gesetzt, damit sie nicht zu sehen sind.
		}	
	}

	
	
	
	
	
// ---------------------------------------------------------- Action Listeners ---------------------------------------------------	

	
// Öffnet den Datensatz als Private-Key	
btn_openPK.addActionListener(new ActionListener() 
{
	public void actionPerformed(ActionEvent e) 
	{
		try
		{
			int row = table.getSelectedRow();
			if(row>=0) 
			{				
				if(checkComboBoxItem(GUI.comboBox_coin,(String) dtm.getValueAt(row, 1))==false) throw new Exception("Unknown Coin, first import this coin!");
				GUI.comboBox_coin.setSelectedItem(dtm.getValueAt(row, 1));
				GUI.setSelectedAddressFormat((String) dtm.getValueAt(row, 4));				
				GUI.tabbedPane.setSelectedIndex(2);
				GUI.txt_privKey.setText((String) dtm.getValueAt(row, 3));
				Action.go();
				GUI.frame.setEnabled(true);
				dispose();	
		    	
			}
		}
		catch(Exception ex) {lbl_error.setText(ex.getMessage());}
	}
});



// Öffnet und Importiert den Datensatz als Seed in den Seed-Extractor
btn_openSeed.addActionListener(new ActionListener() 
{
	public void actionPerformed(ActionEvent e) 
	{
		try
		{
			int row = table.getSelectedRow();
			if(row>=0) 
			{				
				if(checkComboBoxItem(GUI.comboBox_coin,(String) dtm.getValueAt(row, 1))==false) throw new Exception("Unknown Coin, first import this coin!");			
				GUI.btn_removeSeed.doClick();			
				GUI.comboBox_coin.setSelectedItem(dtm.getValueAt(row, 1));
				GUI.tabbedPane.setSelectedIndex(3);
				String str = (String) dtm.getValueAt(row, 3);
				CoinParameter c = CoinParameter.getFromSymbol((String)GUI.comboBox_coin.getSelectedItem());
				PrvKey pk = new PrvKey(str, c.pref_PrivKey);
				GUI.txt_seed.setText(Convert.byteArrayToHexString(pk.getHexPrivKey()));
				String cvc = Calc.getHashSHA256_from_HexString(GUI.txt_seed.getText()).substring(61);
				GUI.txt_cvc.setText(cvc);
				GUI.frame.setEnabled(true);
				dispose();		    	
			}
		}
		catch(Exception ex) {lbl_error.setText(ex.getMessage());}
	}
});



btn_save.addActionListener(new ActionListener() 
{
	public void actionPerformed(ActionEvent e) 
	{
		if(isDuplicateAddress()) {lbl_error.setText("Address already exists!");}
		else
		{			
			int count = dtm.getRowCount();		
			JSONArray ja = new JSONArray();
			try
			{
				for(int i=0;i<count;i++)
				{				
					JSONObject zeile = new JSONObject();
					zeile.put("Description",dtm.getValueAt(i, 0));
					zeile.put("Coin", 		dtm.getValueAt(i, 1));
					zeile.put("Address", 	dtm.getValueAt(i, 2));
					zeile.put("Priv.Key",	dtm.getValueAt(i, 3));
					zeile.put("Format", 	dtm.getValueAt(i, 4));
					zeile.put("Date", 		dtm.getValueAt(i, 5));
					ja.put(zeile);	
				}								
				JSONObject jo = new JSONObject();
				jo.put("list", ja);					
				jo.put("pwHash", wallet.getString("pwHash"));				
				Wallet.save(jo);
				GUI.frame.setEnabled(true);
			    dispose();
				
			}
			catch(Exception ex) {lbl_error.setText(ex.getMessage());}			
		}
	}
});



btn_delete.addActionListener(new ActionListener()
{
	public void actionPerformed(ActionEvent e) 
	{
		int pos = table.getSelectedRow();
		if(pos>=0) dtm.removeRow(pos);
	}
});





// Close Button wird abgefangen und hier selbst verarbeitet.
addWindowListener(new java.awt.event.WindowAdapter() 
{
    @Override
    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
    {
    	GUI.frame.setEnabled(true);
    	dispose();
    }
});	
}



// Läd die Wallet in die Tabelle der GUI
private void loadWallet() throws Exception
{
	dtm.setRowCount(0); 				
	wallet = Wallet.open();
	JSONArray ja = wallet.getJSONArray("list");
	for(int i=0;i<ja.length();i++)
	{
		dtm.addRow(new Object[]{ ja.getJSONObject(i).getString("Description") , ja.getJSONObject(i).getString("Coin") , ja.getJSONObject(i).getString("Address") , ja.getJSONObject(i).getString("Priv.Key") , ja.getJSONObject(i).getString("Format") , ja.getJSONObject(i).getString("Date")}); 
	}
}



// Prüft ob diese Adresse schon in der Wallet-Datei enthalten ist.
private boolean isDuplicateAddress()
{			
	int count = dtm.getRowCount();
	if(count==0) return false;
	String addr = (String) dtm.getValueAt(count-1, 2);	
	for(int i=0;i<count-1;i++) 
	{
		if(addr.equals(dtm.getValueAt(i, 2))) return true;
	}
	return false;
}



// Es wird geprüft ob der String "item" als ComboBox-Item enthalten ist. Wenn nicht, wird eine IllegalArgumentException geworfen.
// Diese Methode ist notwendig, weil Java ein falsches Argument in der Methode 	ComboBox.setSelectedItem("") nicht erkennt.
private boolean checkComboBoxItem(JComboBox comboBox, String item)
{
	if(((DefaultComboBoxModel) comboBox.getModel()).getIndexOf(item) == -1) return false;
	else return true;
}
}