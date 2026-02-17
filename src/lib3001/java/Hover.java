package lib3001.java;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;




/********************************************************************************************************************
*	V1.1									Mr. Maxwell											26.12.2025			*	
* 	LIB3001 Bibliotheks Klasse																						*
*	Diese Klasse animiert Java-Swing Komponenten wenn mit der Maus darüber gehovert werde.							*
*	Nur wenn die Komponente "Enable" ist.																			*
*	Statische Klasse, kein Konstruktor.																				*
*	Diese Hover Klasse kann die meisten Swing-Klasse animieren.														*
*********************************************************************************************************************/





public class Hover
{
	
	private static int offset = 20;			// Offset der auf die Grundfarbe beim hovern addiert wird
	private static Color lineColor;			// Die Animationsfarbe des LineBorders, der hinzugefügt wird, wenn kein Border dem Feld hinterlegt wurde.
	private static HashSet<Component> object = new HashSet<Component>();	// Ist nur dafür da, um zu verhinders das die Listener in Objekten mehrmals registriert werden.
	
	
	
/**	Das Object wird der Hover-Animation hinzugefügt. 
	Es wird der Hintergrund animiert
	@param o Das Objekt, welches der Hover-Animation hinzugt wird.  **/
	public static void addBackground(Component o)
	{	
		if(object.contains(o)) return;
		else object.add(o);	
		o.addMouseListener(new MouseAdapter() 
		{
			Color defaultBeckground;
			boolean defaultOpague;		
			@Override
			public void mouseEntered(MouseEvent e) 
			{
				if(o.isEnabled()==false) return;
				defaultOpague = o.isOpaque();		
				if(o.isOpaque())	defaultBeckground = o.getBackground();	
				else				defaultBeckground = (o.getParent() != null) ? o.getParent().getBackground() : null;
				o.setBackground(offsetColor(defaultBeckground));
				((JComponent) o).setOpaque(true);
			}	
			@Override
			public void mouseExited(MouseEvent e) 
			{
				if(o.isEnabled()==false) return;
				o.setBackground(defaultBeckground);
				((JComponent) o).setOpaque(defaultOpague);
			}
		});
	}
	
	
	
	/**	Das Object wird der Hover-Animation hinzugefügt. 
		Hier wird der Border animiert.
		Falls das Feld keinen Border hat (Border==null, oder EmtyBorder) dann wird ein Line-Border der Farbe setBorderColor() und der Linienstärke 1 für die Hover-Animation benutzt.
		@param o Das Objekt, welches der Hover-Animation hinzugt wird.  **/
	public static void addBorder(Component o)
	{	
		if(object.contains(o)) return;
		else object.add(o);
		o.addMouseListener(new MouseAdapter() 
		{
			Border defaultBorder;
			@Override
			public void mouseEntered(MouseEvent e) 
			{	
				if(o.isEnabled()==false) return;
				defaultBorder = ((JComponent) o).getBorder();
				((JComponent) o).setBorder(animateBorder(defaultBorder));		
			}	
			@Override
			public void mouseExited(MouseEvent e) 
			{
				if(o.isEnabled()==false) return;
				((JComponent) o).setBorder(defaultBorder);
			}
		});
	}
	
	
	
	/**	Verändert den Offset der beim Hovern auf alle Farben addiert wird.
	 	delaut = 50   **/
	public static void setOffset(int offs)
	{
		offset = offs;
	}
	

	
	// Legt die Border-Hintergrundfarbe der Hoveranimation fest, wenn addBorder() ausgewählt wurde aber im Feld kein Border enthalten ist.
	public static void setBorderColor(Color c)
	{
		lineColor=c;
	}
	
	
	
	
	
// ---------------------------------------------- Hilfsmethoden -------------------------------------------------
	
	// Gibt eine Farbe zurück die von der Eingabefarbe einen offset addiert hat. (Hover-Farbe)
	// Dieser Offset ist die Farbänderung der Hoverfarbe.
	private static Color offsetColor(Color c)
	{
		int r,g,b;	
		if(c.getRed()  -offset < 0)		r=c.getRed()  +offset;
		else							r=c.getRed()  -offset;	
		if(c.getGreen()-offset < 0)		g=c.getGreen()+offset;
		else							g=c.getGreen()-offset;	
		if(c.getBlue() -offset < 0)		b=c.getBlue() +offset;
		else							b=c.getBlue() -offset;	
		return new Color(r,g,b);		
	}
	
	
	
	// Bekommt einen unbekannten Border übergeben,
	// analysiert ihn und Animiert dort die Linie ensprechend, je nach Border-Typ, wird etwas anderes Animiert.
	// Der animierte Border wird dann zurückgegeben.
	private static Border animateBorder(Border cb)
	{   
		if(cb==null)	// Wenn keien Border enthalten ist, wird mit einem LineBorder animiert.
		{
			return new LineBorder(lineColor,1);
		}	
		if (cb instanceof MatteBorder) 
	    {	    		
	         MatteBorder matteBorder = (MatteBorder) cb;
	         return new MatteBorder(matteBorder.getBorderInsets(null).top + 1,  matteBorder.getBorderInsets(null).left + 1,  matteBorder.getBorderInsets(null).bottom + 1,  matteBorder.getBorderInsets(null).right + 1,lineColor);	
	    } 	
		if (cb instanceof LineBorder) 
	    {
	        LineBorder lineBorder = (LineBorder) cb; 
	        return  new LineBorder(lineBorder.getLineColor(), lineBorder.getThickness()+1);
	    } 
	    else if (cb instanceof CompoundBorder) 
	    {
	    	 CompoundBorder compoundBorder = (CompoundBorder) cb;
	         Border outside = compoundBorder.getOutsideBorder();
	         Border inside = compoundBorder.getInsideBorder();
	         if (outside instanceof LineBorder) 
	         {
	             LineBorder outsideLine = (LineBorder) outside;
	             outside = new LineBorder(outsideLine.getLineColor(), outsideLine.getThickness() + 1);
	         }
	         if (inside instanceof LineBorder) 
	         {
	             LineBorder insideLine = (LineBorder) inside;
	             inside = new LineBorder(insideLine.getLineColor(), insideLine.getThickness() + 1);
	         }
	         return new CompoundBorder(outside, inside);
	    } 
	    else if (cb instanceof EmptyBorder) // Wenn keien Border enthalten ist, wird mit einem LineBorder animiert.
	    {
	    	return new LineBorder(lineColor,1);
	    } 
	    else if (cb instanceof TitledBorder) 
	    {
	    	 TitledBorder titledBorder = (TitledBorder) cb;
	         Border border = titledBorder.getBorder();
	         if (border instanceof LineBorder) 
	         {
	             LineBorder lineBorder = (LineBorder) border;
	             return new TitledBorder(new LineBorder(lineBorder.getLineColor(), lineBorder.getThickness() + 1), titledBorder.getTitle());
	         }
	         return cb;
	    } 
	    else if (cb instanceof EtchedBorder) 
	    {
	        EtchedBorder etchedBorder = (EtchedBorder) cb;
	        if (etchedBorder.getEtchType() == EtchedBorder.LOWERED) return new EtchedBorder(EtchedBorder.RAISED, Color.LIGHT_GRAY, Color.DARK_GRAY);    
	        else  													return new EtchedBorder(EtchedBorder.LOWERED, Color.LIGHT_GRAY, Color.DARK_GRAY);      
	    } 
	    else if (cb instanceof BevelBorder) 
	    {

	    	BevelBorder bevelBorder = (BevelBorder) cb;
	        if (bevelBorder.getBevelType() == BevelBorder.LOWERED) return new BevelBorder(BevelBorder.RAISED, Color.LIGHT_GRAY, Color.DARK_GRAY);    
	        else  												   return new BevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.DARK_GRAY); 
	    } 
	    else if (cb instanceof SoftBevelBorder) 
	    {   	
	        return cb;
	    }  
	    return cb;
	}	
}