package lib3001.java;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;



/************************************************************************************************
*	V1.0							Mr.Maxwell								22.12.2025			*
* 	LIB3001 Bibliotheks Klasse																	*
*	Eigene ComboBox Klasse erweitert JComboBox													*
*	Hier gibt es nun eine setText(String[]) Methode die der ursprünglichen Combobox fehlt.		*
*	Dadurch können Texte der Combobox-Felder dynamisch geändert werden. 						*
*	Wird verwendet für die Sprachumschaltung													*
 ************************************************************************************************/





public class MyComboBox extends JComboBox
{
	
	public MyComboBox() 
	{
		 super();
	}
	
	
/**	Updates the ComboBox items with a new array of strings and restores the selected index.
	@param newText The new array of strings to be used as the ComboBox items.
	@throws IllegalArgumentException if the new array length does not match the current model's size. **/
	public void setText(String[] newText) 
	{
		if (newText.length != this.getModel().getSize()) throw new IllegalArgumentException("Incorrect array length. The length must correspond to the combo box model!");
		int selectedIndex = this.getSelectedIndex();
		if (selectedIndex < 0 || selectedIndex >= newText.length) selectedIndex = 0;			
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(newText);
		this.setModel(model);
		this.setSelectedIndex(selectedIndex);
	}
	
	
	
	/**	Sets the selected index for the ComboBox.
	@param index The index to be selected.
	@throws IndexOutOfBoundsException if the index is out of bounds of the current model size.  **/
	@Override
	public void setSelectedIndex(int index) 
	{
		if (index < 0 || index >= getModel().getSize()) 
		{
		    throw new IndexOutOfBoundsException("The index is out of range.");
		}
		super.setSelectedIndex(index); // Aufruf der ursprünglichen setSelectedIndex-Methode
	}
}