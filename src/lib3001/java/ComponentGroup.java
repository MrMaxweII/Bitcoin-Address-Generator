package lib3001.java;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JComponent;



/****************************************************************************************************************************
*	V1.0												Mr.Maxwell 										vom 24.01.2021		*
*	BTClib3001 Klasse																										*
* 																															*
*	Die ComponentGroup fasst mehrere JComponenten zu einer logischen Gruppe zusammen.										*
*	Sie dient dazu, wiederkehrende Operationen wie setEnabled(), oder andere Zustandsänderungen zentral auf					*
*	mehrere Swing-Komponenten gleichzeitig anzuwenden.																		*
*	Die Klasse folgt dem Prinzip der Komposition (nicht Vererbung):															*
*	Eine ComponentGroup ist selbst keine Swing-Komponente, sondern verwaltet eine Menge von Komponenten.					*
*	Über die Methode apply(...) können beliebige Aktionen auf alle enthaltenen Komponenten angewendet werden,				*			 
*	ohne dass jede mögliche Methode explizit weitergereicht werden muss.													*
*																															*
*	Anwendungs Beispiel:																									*
*	ComponentGroup group = new ComponentGroup(btn1, btn2, btn3, lbl);   // Setzt 3 Buttons und ein Label in die Gruppe.		*
*	group.apply(c -> c.setForeground(Color.red));						// Alle Fordergrundfarben werden auf rot gesetzt.	*
****************************************************************************************************************************/











public class ComponentGroup 
{

    private final List<JComponent> components = new ArrayList<>();

    
    /** Konstruktor dem belibig viele Swing-Komponenten übergeben werden können **/
    public ComponentGroup(JComponent... comps) 
    {
        components.addAll(Arrays.asList(comps));
    }

    
    
    /**	Fügt der Gruppe eine weitere Komponente hinzu. **/
    public void add(JComponent c) 
    {
        components.add(c);
    }

    
    
    /** Wendet eine Aktion auf alle Komponenten der Gruppe an.  **/
    public void apply(Consumer<JComponent> action) 
    {
        components.forEach(action);
    }
}