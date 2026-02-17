package GUI;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import lib3001.network.CoinParameter;



/************************************************************************************************************************
*	Diese Klasse ist Teil der GUI des CoinAddressGenerators																*
*	Erzeugt das List-Fenster, in dem Coins hinzugefügt oder entfernt werden können.										*
*	Das Aufrufen dieser Klasse ist im Code dekativiert, weil Multicoin nicht mehr alle Funktionen implemntieren kann.	*
************************************************************************************************************************/



class GUI_CoinList extends JFrame
{
	CoinParameter[] cp = null;
	GUI_CoinList(int x, int y, boolean remove) 
	{
		setTitle("Coin List");
		setBounds(x, y, 300, 400);
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		String[] strList;
		if(remove)
		{
			int size = GUI.comboBox_coin.getItemCount();
			strList = new String[size];				
			for (int i = 0; i < size; i++)  strList[i] = (String) GUI.comboBox_coin.getItemAt(i);				
		}
		else
		{
			cp = CoinParameter.getList();		
			strList = new String[cp.length];
			for(int i=0;i<cp.length;i++) {strList[i] = (cp[i].symbol+"        ").substring(0,10)+cp[i].name;}
		}
		JList list = new JList(strList);
		list.setFont(new Font("Consolas", Font.PLAIN, 11));
		scrollPane.setViewportView(list);
		list.addKeyListener(new KeyAdapter() 
		{
			@Override
			public void keyPressed(KeyEvent e) 
			{
				if(e.getKeyCode()==10) 
				{
					int i = list.getSelectedIndex();
			    	if(remove) 	GUI.comboBox_coin.removeItemAt(i);
			    	else 		GUI.comboBox_coin.addItem(cp[i].symbol); 		    	
			    	GUI.frame.setEnabled(true);
			    	dispose();
				}
			}
		});
		
		list.addMouseListener(new MouseAdapter() 
		{
		    public void mouseClicked(MouseEvent e) 
		    {
		        JList list = (JList)e.getSource();
		        if (e.getClickCount() == 2) 
		        {
		    		int i = list.getSelectedIndex();
			    	if(remove) 	GUI.comboBox_coin.removeItemAt(i);
			    	else 		GUI.comboBox_coin.addItem(cp[i].symbol); 
			    	GUI.frame.setEnabled(true);
			    	dispose();
		        }
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
}