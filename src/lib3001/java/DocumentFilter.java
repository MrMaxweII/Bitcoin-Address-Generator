package lib3001.java;
import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;



/***************************************************************************************************************************************************
*	V1.0 													Mr. Maxwell														vom 18.12.2023			*
* 																																					*
* 	LIB3001 Bibliotheks Klasse																														*
* 	Nicht statische Klasse die von PlainDocument abgeleitet ist!									  												*
* 	Filtert die Eingabe in TextFelder während der Eingabe.																							*
* 																																					*
* 	Achtung:  Der Inhalt der JTextFelder wird gelöscht, beim aufruf der Methode "JTextField.setDocument(...);										*
*	Diese Klasse funktioniert gut, bis auf diese eine Besonderheit. 																				*	
*																																					*
* 	Anwendung:																																		*
* 	Dem Textfeld wird mit JTextField.setDocument(DocumentFilter) dieser Filter übergeben.															*
* 	Vorher kann der DocumentFilder mit diversen Methoden angepasst werden.																			*
* 	Dem Konstruktor wird ein String mit dem FilterNamen von hier implementierten FilterMethoden übergeben!											*
* 	Mögliche FilterNamen, sind im Konstruktor beschrieben.																							*
* 	Wird kein hier implementierter Filter verwendet, wird dem Konstruktor nichts übergeben.															*
* 	Oft genutzte Filtermethoden sollten hier stets erweitert werden, so das diese einfach angewendet werden können.									*
* 																																					*
* 	Spezielle Filter können selbst implementiert werden in dem die Methode "insertString(...)" in der Anwendungsklasse überschrieben wird. 			*
* 	Dabei ist zu entscheiden ob es ein einmaliger spezieller Filterfall ist, (dann wird die Methode überschrieben)									*
* 	oder ob dieser Filter gängig ist, dann sollte dieser Filter hier eine Methode bekommen die spätter wiederverwendet werden kann.					*	
* 																																					*
*	Syntax:																																			*
*	Die Methode "insertString(...)" (geerbt von Document) ist die entscheidende Methode, in der beliebig viele IF-Anweisungen den Inhalt prüfen.	*
*	Die Methode getText() gibt den String zurück der mit der Tastatur in das Feld eingegeben wird, ABER noch nicht ins Feld übertragen wird.		*
*	Es handelt sich hierbei um den Text der durch drücken einer Taste in das Feld geschrieben werden soll. 											*
*	Kopy and Paste funktionieren hierbei auch. 																										*
*	Bestehend aus dem Text der vorher schon drinn war und dem Text der hinzugefügt werden soll. 													*
*	Also der finale Text so wie er währe, wenn die Eingabe erfolgen würde. (Sie wird ja vorher hier noch geprüft)									*
*	An genau der Stelle kann nun dieser Text mit IF-anweisungn nach Belieben geprüft werden.														*
*	Ist der Text "falsch" , wird mit "return" abgebrochen (Hier kann noch ein Ton ausgegeben werden".												*
*	Wenn Abgebrochen wird, wird der Text nicht ins Feld übertragen, der Tastendruck wird also dann ignoriert. Die vorherige Eingabe bleibt bestehen.*
*	Ist der Text "richtig", wird "super.insertString(offs, str, a);" am Ende ausgeführt und der neue Text wird damit in das Feld übertragen.		*
*****************************************************************************************************************************************************/


	


public class DocumentFilter extends PlainDocument
{
	
	
	private String 	filterName 	= "";							// FilterName, der beschreibt, welcher Filter anzufenden ist.
	private int 	intMin 		= Integer.MIN_VALUE;			// Auswahl kleinster Integer-Wert (Default: Integer.Min)
	private int 	intMax 		= Integer.MAX_VALUE;			// Auswahl größter   Integer-Wert (Default: Integer.Max)
	private long 	longMin		= Long.MIN_VALUE;				// Auswahl kleinster Integer-Wert (Default: Integer.Min)
	private long 	longMax		= Long.MAX_VALUE;				// Auswahl größter   Integer-Wert (Default: Integer.Max)
	
	
	
	
	
	
// ----------------------------------------------------------- Konstruktoren ----------------------------------------------------------------------
	
	public DocumentFilter() // Leerer Konstruktor, wird benötigt. 
	{
	}
	
	
	/** Dem Konstruktor wird ein FilterName als String übergeben.
		Mögliche FilterNamen: intFilter, longFilter,...
		FilterNamen hier erweitern! 
	 * @throws BadLocationException **/
	public DocumentFilter(String filterName)
	{		
		super();
//		try 				// Nur zum Debuggen
//		{
//			int end = this.getDefaultRootElement().getElement(0).getEndOffset();
//			String textOld = this.getText(0,end-1);
//			System.out.println("Text = "+textOld);
//			super.insertString(0, textOld, null);
//		} 
//		catch (BadLocationException e) {e.printStackTrace();}
		
		this.filterName = filterName;
	}
	
	
	
// ----------------------------------------------------------- Hauptmethoden -----------------------------------------------------------------------
	
	// Diese Methode bei bedarf in der Anwedungsklasse überschreiben und wie gewünscht filtern.
	// In Dieser methode wird ausgewählt, ob die Tastatureingabe übernommen wird und in das Feld geschrieben, oder ob sie ignoriert wird.
	// Wenn Ignoriert, dann mit return; zausspringen.
	// Wenn Ausführen, dann "super.insertString(offs, str, a);" am Ende ausführen.
	// Die zu prüfende TastaturEingabe hier mit "getText(int offs, String str);" aufrufen, oder eine unten implementierte FilterMethode aufrufen.
	// offs: ist die Position an der was eingefügt wird.
	// str: Nur aktuelle Tastatur-Eingabe. (nicht zusammengefügt mit der vorherigen Zeichenvolge)
	// a: Unbekanntes Attribut, wurde noch nicht benötigt.
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException 
	{				
		if(filterName.equals("intFilter"))  		{if(intFilter(offs,str)==false)  		{Toolkit.getDefaultToolkit().beep(); return;}}
		if(filterName.equals("longFilter")) 		{if(longFilter(offs,str)==false) 		{Toolkit.getDefaultToolkit().beep(); return;}}
		if(filterName.equals("longFilterPositiv")) 	{if(longFilterPositiv(offs,str)==false) {Toolkit.getDefaultToolkit().beep(); return;}}


		// <---------------------------------------------------- Hier Weitere Filter hinzufügen	
		super.insertString(offs, str, a);
	}
	
	
	
	
	
	
// -------------------------------------------------------------- set/get-Methoden --------------------------------------------------------------------
	
	/** Setzt die Grenzen beim Integer Filter.
	@param intMin Minimaler Integer Wert
	@param intMax Maximaler Integer Wert        **/
	public void setIntMaxMinValue(int intMin, int intMax)
	{
		this.intMin=intMin;
		this.intMax=intMax;
	}
	
	
	/** Setzt die Grenzen beim Long-Filter.
	@param longMin Minimaler Long Wert
	@param longMax Maximaler Long Wert        **/
	public void setLongMaxMinValue(long longMin, long longMax)
	{
		this.longMin=longMin;
		this.longMax=longMax;
	}
	
	
// ----------------------------------------------------- Erweiterte FilterMethoden (Hier Filter-Methoden hinzufügen ------------------------------------
	
	
	// Läst nur Integer-Zahlen zu die in dem angegebenem Bereich sind.
	private boolean intFilter(int offs, String str) throws BadLocationException
	{
		if(!str.matches("^-?[0-9]*$"))  return false;
		String txt = getText(offs,str);
		if(txt.equals("-")) return true; // Einzelnes "-" Zeichen wird erlaubt!
		int z;
		try {z = Integer.parseInt(txt);} catch(Exception e) {return false;};
		if(z < intMin)  return false;
		if(z > intMax)  return false;
		return true;
	}
	
	
	
	// Läst nur Long-Zahlen zu die in dem angegebenem Bereich sind.
	private boolean longFilter(int offs, String str) throws BadLocationException
	{
		if(!str.matches("^-?[0-9]*$"))  return false;
		String txt = getText(offs,str);
		if(txt.equals("-")) return true; // Einzelnes "-" Zeichen wird erlaubt!
		long z;
		try {z = Long.parseLong(txt);} catch(Exception e) {return false;};
		if(z < longMin)  return false;
		if(z > longMax)  return false;
		return true;
	}
	
	
	// Läst nur positive Long-Zahlen zu die in dem angegebenem Bereich sind. (Keine negativen Zahlen)
	private boolean longFilterPositiv(int offs, String str) throws BadLocationException
	{
		if(!str.matches("^[0-9]*$"))  return false;
		String txt = getText(offs,str);
		if(txt.equals("-")) return true; // Einzelnes "-" Zeichen wird erlaubt!
		long z;
		try {z = Long.parseLong(txt);} catch(Exception e) {return false;};
		if(z < longMin)  return false;
		if(z > longMax)  return false;
		return true;
	}	
	
	
	
	
	
// ------------------------------------------------------------- private Hilfsmethoden --------------------------------------------------------------	
	
	// Gibt den EingabeText zurück der geprüft werden soll, befor er ins Feld geschrieben wird.
	// Diese Methode nicht anpassen oder überschreiben/verändern!
	private String getText(int offs, String str) throws BadLocationException
	{
		int end = this.getDefaultRootElement().getElement(0).getEndOffset();
		String textOld = this.getText(0,end-1);
		StringBuilder sb = new StringBuilder(textOld);
		return sb.insert(offs, str).toString();
	}		
}
