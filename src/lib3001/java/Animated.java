package lib3001.java;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import javax.swing.Timer;





/****************************************************************************************************************************************
*	V2.0												Mr. Maxwell												04.01.2026				*		
* 	- V2.0 gesamte Klasse auf Swing.Timer umgestellt.																					*
* 	LIB3001 Bibliotheks Klasse																											*
*	Diese Animated Klasse kann alle Klassen-Objekte animieren die eine Methode "setForeground(Color)" hat.								*
*	Diese Animated Klasse ist static weil es nur einen Animations-Thread für das ganze Progremm geben darf!								*
*	1.	Es muss zuerst einmal initAnimation() aufgerufen werden, damit der Animationssthread (Swing.Timer) gestartet wird.				*
*	- Optional können mit denn Setter-Methoden verschiedene Animationseinstellungen vorgenommen werden.	(Gelten immer für alle)			*
*	2. 	Der Methode start(<T>) können verschiedene Objecte übergeben werden die eine "setForeground()" methode enthällt.				*
*		Dadurch wird die Animation sofort gestartet. Es blinkt die Fordergurndfarbe														*
*		Doppelaufrufe von start(<T>) des selben Objektes sind störungsfrei möglich und bewirken nichts.									*
*		Die Methode start(<T>) kann mit verschiedenen Objekten, merfach aufgerufen werden.												*
*		D.H. es können beliebig viele Objekte in die Animation aufgenommen werden.														*
*	-	Mit der Methode stop(<T>) wird ein Obkect wieder aus der Animation entfernt. Dies beendet die Animation für dieses Objekt.		*
*	3.	Die Methode: close() muss spätestens bei Programmende ausgeführt werden, um den Swing.Timer zu beenden!							*
*	Alle Objekte werden stets syncron und identisch mit einheitlicher Farbe animiert. Unterschiedliche Animationen sind nicht möglich.	*
*	Mit der Methode setColors(Color, Color) werden die zwei animierten Farben eingestellt.												*
*	Durch die Verwendung des Swing.Timers, ist die Animation Threadsicher implementiert.												*
****************************************************************************************************************************************/





public class Animated
{
	

	private static int 		i			= 0;						// Schleifenindex des Threads.
	private static int 		sleepTime 	= 70;						// Default Thread.SleepTime();	
	private static double 	scal = Math.PI;							// Scallierungsfaktor 
	private static Color	color_l		= Color.black;				// Grundfarbe, die Farbe wenn die Animation auf dem Tiefpungt ist.
	private static Color	color_h		= Color.white;				// Zielfarbe, die Farbe wenn die Animation auf dem Höhepunkt ist.
	private static HashSet<Element>list = new HashSet<Element>();	// Das HashSet mit allen Objekten die der Animation hinzugefügt werden können.
	private static Timer	timer;									// Der Swing.Timer der den sicheren Animations-Thread verwaltet. Achung: import javax.swing.Timer; verwenden!
	private static int 		dr;										// Änderungsvariable der Farbe rot
	private static int 		dg;										// Änderungsvariable der Farbe grün
	private static int 		db;										// Änderungsvariable der Farbe blau
	private static boolean  init = false;							// Wenn true ist die Animation initialisiert, wird nur einmal bei Programmstart auf true gesetzt. Verhindert doppeltes Starten des Swing.timers.
	
	
	
	
	
	
	
	/**	Initialisiert die Animation und startet den Swing.Timer.
		Muss bei Programmstart einmal ausgeführt werden
		Kann nur einmal gestartet werden.**/
	public static void initAnimation()
	{
		if(init) return;
		else startThread();		
	}
	
	
	
	/**	Das Object wird der Animation hinzugefügt. 
		Startet die Animation für das übergebene Object
		@param o Das Objekt, welches animeirt werden soll
		@param foreground Wenn true, wird die Fordergrundfarbe animiert, sonnst die Hintergrundfarbe. **/
	public static <T> void start(T o, boolean foreground)
	{
		Iterator<Element> iterator = list.iterator(); 
		while (iterator.hasNext()) 
		{
			Element e = iterator.next();
			if(e.t == o) {return;}			// Wenn das Element schon existiert, wird abgebrochen.
		}	
		Element e;
		if(foreground) 	 e = new Element(o,   ((Component) o).getForeground() ,foreground);
		else			 e = new Element(o,   ((Component) o).getBackground() ,foreground);
		list.add(e);
	}
	
	
	
	/**	Das Object wird aus der Animation entfernt. Dies stoppt die Animation für diese Element	
		@param o Das Object in welchem die Animation gestoppt werden soll.**/
	public static <T> void stop(T o)
	{
		Iterator<Element> iterator = list.iterator(); 
		while (iterator.hasNext()) 
		{
			Element e = iterator.next();
			if(e.t == o)
			{	
				if(e.foreground) ((Component) o).setForeground(e.defaultColor);
				else			 ((Component) o).setBackground(e.defaultColor);	
				iterator.remove();		 	
			}
		}
	}
	
	
	
	/**	Die Animation wechselt flüssig zwischen diesen beiden Farben.
		Default sind schwartz, und weiß eingestellt. */
	public static void setColors(Color c1, Color c2)
	{
		color_l = c1; color_h = c2;	
		dr = color_h.getRed()  - color_l.getRed();		
		dg = color_h.getGreen()- color_l.getGreen();	
		db = color_h.getBlue() - color_l.getBlue();		
	}
	
	
	
	/** Setzt die Animations SleepTime, sie bestimmt die Geschwindigkeit der Animation 
	 	Default = 70	**/
	public static void setSleepTime(int s)
	{
		sleepTime = s;
		timer.setDelay(sleepTime);
	}
	
	
	
	/**	setFrames bestimt die Anzahl der Frames (Farbänderungen) in einer Runde.	
	 	Der kleinste Wert von  2 bedeutet, das es  2   Farbänderungen gibt, die sich zyklich wiederholen.
	 	Der größte Wert von 500 bedeutet, das es 500 Farbänderungen gibt, die sich zyklich wiederholen. 
	 	Der frames-Wert kann zwischen 2 und 500 eingestellt werden.
		Fehlerhafte Eingaben setzen immer den Default-Wert von 20 
		**/
	public static void setFrames(int frames)
	{
		if(frames < 2 || frames > 500) return;
		scal = frames / (Math.PI *2.0);
	}

	
	
	/**	Beendet den Animations-Thread apprupt, löscht den Speicher und setzt damit alle einstellungen zurück.  
	 	Achtung: dadurch bleibt die Animation einfach stehen, die Objekte bekommen nicht ihre ursprüngliche Farbe zurück!
	 	Daher sollen vorher alle Elemente mit stop() angehalten werden!
	 	Diese Methode muss spätestens bei Programm-Ende aufgerufen werden, um den Swing.Timer zu beenden.**/
	public static void close()
	{
		timer.stop();
		System.out.println("Swing.Timer stoppt");
		i			= 0;		
		sleepTime 	= 70;			
		scal = Math.PI;					
		color_l		= Color.black;			
		color_h		= Color.white;		
		list.clear();
	}
	
	
	
	
	
	
// ----------------------------------------------------------------- Private Methoden -----------------------------------------------------------------------	
	
	
	
	

	//  Startet den Swing.Timer der die Animation implementiert.
	private static void startThread()
	{
		init = true;
		System.out.println("init Animation");			
		dr = color_h.getRed()  - color_l.getRed();		// Änderungsvariable der Farbe rot
		dg = color_h.getGreen()- color_l.getGreen();	// Änderungsvariable der Farbe grün
		db = color_h.getBlue() - color_l.getBlue();		// Änderungsvariable der Farbe blau	
		ActionListener al = new ActionListener()
		{
			public void actionPerformed(ActionEvent event) 
			{
				try
				{		
					//System.out.println("Swing.Timer running.");														
					int r = (int)((Math.cos(i/scal)+1.0)*dr/2.0);
					int g = (int)((Math.cos(i/scal)+1.0)*dg/2.0);
					int b = (int)((Math.cos(i/scal)+1.0)*db/2.0);
					r = color_l.getRed() + r;
					g = color_l.getGreen() + g;
					b = color_l.getBlue() + b;
					r = Math.max(0, Math.min(255, r)); 
					g = Math.max(0, Math.min(255, g)); 
					b = Math.max(0, Math.min(255, b)); 	
					Iterator<Element> iterator = list.iterator(); 
					while (iterator.hasNext()) 
					{
						Element e = iterator.next();
						if(e.foreground) ((Component) e.t).setForeground(new Color(r,g,b));
						else			 ((Component) e.t).setBackground(new Color(r,g,b));	
					}
					i++;						
				}
				catch(Exception ex) 
				{
					timer.stop();
					System.out.println("Swing.timer stopped due to an error.");
					ex.printStackTrace();				
				}
			}	
		};		
		timer = new Timer(sleepTime,al);
		if(timer.isRunning()==false)
		{
			timer.start();
			System.out.println("Swing.Timer start");	
		}			
	}
}	
	
	



// --------------------------------------------------------------------------- Hilfs Klasse----------------------------------------------------------------
// Diese Hilfsklasse wird nur innerhalb der Animated-Klasse benutzt.
// Es handelt sich um ein Listenelement welches mehrere Objekte in sich trägt.
class Element<T>
{
	T t;							// Der generische Datentyp z.B. Jlabel, der der Animation hinzugefügt wird.
	Color 		defaultColor;		// Die urprüngliche Farbe die nach der Animation wieder angenommen werden soll.
	boolean 	foreground;			// Wenn true, wird die Fordergrundfarbe animiert, wenn false die Hintergrundfarbe
	
	
	// Konstruktor
	Element(T t, Color c, boolean f)
	{
		this.t = t;
		this.defaultColor = c;
		this.foreground = f;
	}
}