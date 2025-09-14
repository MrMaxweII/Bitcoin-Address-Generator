package BTClib3001;



/***************************************************************************************************************************
*	V1.0								Mr.Maxwell 												vom 22.01.2021				*
*	BTClib3001 Klasse																										*
*	Diese Implementierung der ByteArrayList Klasse ist eine allgemeine Daten-Typ-Klasse 									*
*	die verschiedene wichtige Methode für Byte-Arrays implementiert.														*	
*	- Besitzt keine Abhängigkeiten aus anderen Klassen der BTClib3001														*
****************************************************************************************************************************/




public class ByteArrayList 
{

	private byte[] data;		// The byte array as data type of this class
	
	
	

// ----------------------------- Konstruktor ----------------------------//
	
	
/**	The constructor receives the byte array. **/	
public ByteArrayList(byte[] data)
{
	this.data = data;
}
	



// ----------------------------- Public Methoden -----------------------//




/**	@param b Appends the given byte to the end of the list.	**/
public void add(byte b)
{
	byte[] out = new byte[data.length+1];
	System.arraycopy(data, 0, out, 0, data.length);
	out[data.length] = b;
	data = out;
}



/**	@param b Appends the given byte array to the end of the list.	**/
public void add(byte[] b)
{
	int len = b.length;
	byte[] out = new byte[data.length + len];
	System.arraycopy(data, 0, out, 0, data.length);
	System.arraycopy(b, 0, out, data.length, len);
	data = out;
}



/**	Removes a selected range, between from and to.
	Subsequent bytes fill the gap by being moved forward.
	@param from Start position from which bytes should be removed.
	@param to End position up to which bytes should be removed.	**/
public void remove(int from, int to)
{
	int len = to-from;
	byte[] out = new byte[data.length - len];
	System.arraycopy(data, 0, out, 0, from);
	System.arraycopy(data, to, out, from, data.length -to);
	data = out;
}



/**	Inserts a new byte array at the specified position and shifts the remaining elements backwards.
	@param b The byte array to be inserted.
	@param pos Position where the new byte array should be inserted.  **/
public void insert(byte[] b, int pos)
{
	int len = b.length;											// Length of the array to be inserted.
	byte[] out = new byte[data.length + len];					// new array
	System.arraycopy(data, 0, out, 0, pos);						// Front part is copied
	System.arraycopy(b, 0, out, pos, len);						// Middle part is copied
	System.arraycopy(data, pos, out, pos+len, data.length-pos);	// End part is copied
	data = out;
}



/**	Inserts a single byte at the specified position and shifts the remaining elements backwards.
@param b The byte to be inserted.
@param pos Position where the new byte should be inserted.  **/
public void insert(byte b, int pos)
{
	byte[] out = new byte[data.length + 1];						// new array
	System.arraycopy(data, 0, out, 0, pos);						// Front part is copied
	out[pos] = b;												// The byte is copied
	System.arraycopy(data, pos, out, pos+1, data.length-pos);	// End part is copied
	data = out;
}



/** @return Returns the element at the specified position in this list. **/
public byte get(int index)
{
	return data[index];
}



/**	Returns a byte array in the specified range. **/
public byte[] getArray(int from, int to)
{
	byte[] out = new byte[to-from];
	System.arraycopy(data, from, out, 0, to-from);
	return out;
}



/** @return returns the complete byte array.	**/
public byte[] getArrayAll()
{
	return data;
}



/**	@return Returns the number of elements in this list. **/
public int size()
{
	return data.length;
}
}