package lib3001.java;



/***************************************************************************************************************************
*	V1.0								Mr.Maxwell 												vom 22.01.2021				*
*	BTClib3001 Klasse																										*
*	Diese Implementierung der ByteArrayList Klasse ist eine allgemeine Daten-Typ-Klasse 									*
*	die verschiedene wichtige Methode für Byte-Arrays implementiert.														*
*	- Besitzt keine Abhängigkeiten aus anderen Klassen der BTClib3001														*
****************************************************************************************************************************/




public class ByteArrayList
{

	private byte[] data;		// Das Byte-Array als Datentyp dieser Klasse




// ----------------------------- Konstruktor ----------------------------//


/**	Dem Konstruktor wird das Byte-Array übergeben. **/
public ByteArrayList(byte[] data)
{
	this.data = data;
}




// ----------------------------- Public Methoden -----------------------//




/**	@param b Hängt das angegebene Byte hinten an die Liste an.	**/
public void add(byte b)
{
	byte[] out = new byte[data.length+1];
	System.arraycopy(data, 0, out, 0, data.length);
	out[data.length] = b;
	data = out;
}



/**	@param b Hängt das angegebene Byte-Array hinten an die Liste an.	**/
public void add(byte[] b)
{
	int len = b.length;
	byte[] out = new byte[data.length + len];
	System.arraycopy(data, 0, out, 0, data.length);
	System.arraycopy(b, 0, out, data.length, len);
	data = out;
}



/**	Entfernt einen ausgewählten Bereich, zwischen from und to.
	Nachfolgende Bytes füllen die Lücke auf, in dem sie nach vorne geschoben werden.
	@param from Start-Position ab der Bytes entfernt werden sollen.
	@param to End-Position bis an diese Stelle werden Bytes entfernt.	**/
public void remove(int from, int to)
{
	int len = to-from;
	byte[] out = new byte[data.length - len];
	System.arraycopy(data, 0, out, 0, from);
	System.arraycopy(data, to, out, from, data.length -to);
	data = out;
}



/**	Fügt ein neues Byte-Array in die angegebene Position ein und verschiebt die restlichen Elemente nach hinten.
	@param b Das Byte-Array welches eingefügt werden soll.
	@param pos Position der Stelle an dem das neue Byte-Array eingefügt werden soll.  **/
public void insert(byte[] b, int pos)
{
	int len = b.length;											// Länge des einzufügenden Arrays.
	byte[] out = new byte[data.length + len];					// neues Array
	System.arraycopy(data, 0, out, 0, pos);						// Vorderteil wird kopiert
	System.arraycopy(b, 0, out, pos, len);						// Mittelteil wird kopiert
	System.arraycopy(data, pos, out, pos+len, data.length-pos);	// End-Teil wird kopiert
	data = out;
}



/**	Fügt ein einzelnes Byte in die angegebene Position ein und verschiebt die restlichen Elemente nach hinten.
@param b Das Byte welches eingefügt werden soll.
@param pos Position der Stelle an dem das neue Byte eingefügt werden soll.  **/
public void insert(byte b, int pos)
{
	byte[] out = new byte[data.length + 1];						// neues Array
	System.arraycopy(data, 0, out, 0, pos);						// Vorderteil wird kopiert
	out[pos] = b;												// Das Byte wird kopiert
	System.arraycopy(data, pos, out, pos+1, data.length-pos);	// End-Teil wird kopiert
	data = out;
}



/** @return Gibt das Element an der angegebenen Position in dieser Liste zurück. **/
public byte get(int index)
{
	return data[index];
}



/**	Gibt ein Byte-Array in dem angegebenem Bereich zurück. **/
public byte[] getArray(int from, int to)
{
	byte[] out = new byte[to-from];
	System.arraycopy(data, from, out, 0, to-from);
	return out;
}



/** @return gibt das komplette Byte-Array zurück.	**/
public byte[] getArrayAll()
{
	return data;
}



/**	@return Gibt die Anzahl der Elemente in dieser Liste zurück. **/
public int size()
{
	return data.length;
}
}