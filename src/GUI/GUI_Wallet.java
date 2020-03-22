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
import Wallet.Wallet;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;



/********************************************************************************************************
*	Diese Klasse ist Teil der GUI des CoinAddressGenerators						*
*	Hier wird das Wallet-Fenster erstellt, welches beim Öffnen oder Speichern angezeigt wird.	*
*********************************************************************************************************/



public class GUI_Wallet extends JFrame 
{

	
	
	public DefaultTableModel dtm;					// Die Tabelle der Wallet
	private JLabel 			lbl_error = new JLabel();	// Meldungsfenster
	private JSONObject 		wallet;				// Die entschlüsselte Wallet-JSON-Datei
	
	
	
/**	Konstruktor, wird von der GUI aufgerufen
	Oberste Ebene der Wallet. 
	Löst alle Aktionen aus die zum Öffnen, editieren oder Speichern der Wallet führen.
	@param x x-pos des Fensters
	@param y y-pos des Fensters
	@param profil legt fest wie das Fenster gestaltet wird. Auswahlmöglichkeiten: "open", "save", "edit".  **/
public GUI_Wallet(int x, int y, String profil) throws Exception
{
	JPanel 		contentPane 	= new JPanel();
	JScrollPane 	scrollPane 	= new JScrollPane();
	JTable 		table 		= new JTable();
	JPanel 		panel 		= new JPanel();
	JButton 	btn_delete 	= new JButton("Delete");
	JButton 	btn_save 	= new JButton("Save");
	JButton 	btn_open 	= new JButton("Open");
	JButton 	btn_close 	= new JButton("Close");
		
	setBounds(x, y, 875, 370);
	contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	contentPane.setLayout(new BorderLayout(0, 0));
	setTitle("Wallet "+profil);
	setContentPane(contentPane);
	
	dtm = new DefaultTableModel(null , new String[] {"Description", "Coin", "Address", "Priv.Key", "Format", "Date"}) 
	{
		boolean[] columnEditables = new boolean[] {true, false, false, false, false, false};
		public boolean isCellEditable(int row, int column) {return columnEditables[column];}
	};
	
	table.setRowHeight(20);
	table.setGridColor(new Color(240, 240, 240));
	table.setFont(new Font("Tahoma", Font.PLAIN, 9));
	table.setCellSelectionEnabled(true);
	table.putClientProperty ("terminateEditOnFocusLost", Boolean.TRUE);
	table.setModel(dtm);	
	table.getColumnModel().getColumn(0).setPreferredWidth(80);
	table.getColumnModel().getColumn(1).setPreferredWidth(20);
	table.getColumnModel().getColumn(2).setPreferredWidth(170);
	table.getColumnModel().getColumn(3).setPreferredWidth(260);
	table.getColumnModel().getColumn(4).setPreferredWidth(20);
	table.getColumnModel().getColumn(5).setPreferredWidth(20);
		
	scrollPane.setViewportView(table);	
	FlowLayout flowLayout = (FlowLayout) panel.getLayout();
	flowLayout.setAlignment(FlowLayout.RIGHT);
	lbl_error.setForeground(Color.RED);

	contentPane	.add(scrollPane, BorderLayout.CENTER);
	contentPane	.add(panel, BorderLayout.SOUTH);	
	panel		.add(lbl_error);
	panel		.add(btn_open);
	panel		.add(btn_delete);
	panel		.add(btn_save);
	panel		.add(btn_close);
	
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
				if(e.getClickCount() >= 2) btn_open.doClick(); 
			}
		});
	}
	if(profil.equals("save"))
	{
		btn_open.setVisible(false);
		btn_delete.setVisible(false);
		setBounds(x, y, 875, 210);
		for(int i=0;i<dtm.getRowCount();i++)
		{
			table.setRowHeight(i, 1); // Alle Zeilenhöhen werden auf 1 gesetzt, damit sie nicht zu sehen sind.
		}	
	}

	
	
	
	
	
// ---------------------------------------------------------- Action Listeners ---------------------------------------------------	

	
	
btn_open.addActionListener(new ActionListener() 
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



btn_close.addActionListener(new ActionListener() 
{
	public void actionPerformed(ActionEvent e) 
	{
		GUI.frame.setEnabled(true);
    	dispose();
	}
});



// Close Button wird abgefangen und hier selbst verarbeitet.
addWindowListener(new java.awt.event.WindowAdapter() 
{
    @Override
    public void windowClosing(java.awt.event.WindowEvent windowEvent) 
    {
    	GUI.frame.setEnabled(true);
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
