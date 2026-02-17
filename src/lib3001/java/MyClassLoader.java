package lib3001.java;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lib3001.crypt.Convert;



/***************************************************************************************************************************************************************************
*	Version 1.2      									Autor: Mr. Maxwell																		vom 27.01.2023 				*
*																																											*
* 	LIB3001 Bibliotheks Klasse																																				*
*	Dies ist ein Class-Loader der Java-Klassen (xxx.class) und .jar Archive zur Laufzeit laden und Ausführen kann.															*
*	Die Klassen müssen NICHT vorher existieren oder bekannt sein oder sich im bin-Ordner befinden.																			*
*	Wird verwendet um Klassen und Bibliotheken z.B. als PlugIns im Nachhinein hinzufügen zu können ohne das Hauptprogramm neu kompilieren oder ändern zu müssen.			*
*	Forgehen:																																								*
*	Die zu ladenden Klassen oder jar-Archive werden immer zu erst mit der loadClass() oder loadJar() Methode in das myClassLoader-Object geladen.							*
*	Typischerweise wird zu erst eine große Menge an Klassen nacheinander in das myClassLoader-Object geladen. Z.B. Bibliotheken aus jar-Dateien etc.						*
*	Die Klassen befinden sich dann alle in diesem myClassLoader-Object, also im RAM und sind zur Laufzeit registriert, bzw. bekannt und können ausgeführt werden.			*
*	Wenn alle benötigten Klasse geladen sind, kann die Start-Methode: "runStartMethod()" aufgerufen werden, die dann diese Methode ausführt.								*
*																																											*
*	Es können nur Java-Klassen bzw. jar Archive die nur Java-Klassen enthalten geladen werden.																				*
*	Enthalten .jar-Archive dll´s oder weitere Dateien, werden sie hier vom ClassLoader ignoriert und übersprungen.															*
*  	Diese benötigten Dateien werden von der jeweiligen Bibliothek normalerweise selbst verwaltet und aufgerufen.															*
*	Die entsprechenden Dateien müssen sich dann aber physikalisch in dem jeweiligem Verzeichnis befinden!																	*
*****************************************************************************************************************************************************************************/



public class MyClassLoader extends ClassLoader 
{
    
	
	
    	
		private static MyClassLoader myClassLoader = new MyClassLoader();	// Das ClassLoader-Object in das, das Paket mit allen Klassen geladen wird.
	
	

		
		
/**	Lädt den Inhalt eines ganzen .jar-Archives in das myClassLoader-Object.
 	Jar-Datein sind grundsätzlich Zip-Datein mit Java-Klassen als Inhalt.
 	Also wird hier eine Zip-Datei geöffnet und die Java-Klassen geladen.
 	Diese Methode erkennt Java-Klassen selbstständig anhand der magic-Bytes. (cafebabe)
 	Es werden alle Java-Class Dateien in dem Archive geladen, in allen Unterordnern.
 	Die Ordnernamen in denen sich Class-Dateien befinden, werden als Paket-Name für die jeweilige Class verwendet.
 	Beispiel: HalloWelt.class, befindet sich ich Ordner "grün" im Unterordner "schwarz" also "grün/schwarz/HalloWelt.class"
 	Diese Klasse bekommt nun den Namen: "grün.schwarz.HalloWelt" zugeordnet.  "grün.schwarz" wäre der Packet-Name und "HalloWelt" der Klassen-Name.
 	Mit dieser Methode können Java-Bibliotheken zur Laufzeit hinzugefügt werden, die vorher nicht bekannt waren.
 	Es müssen immer erst alle Bibliotheken hinzugefügt werden, bevor die "Main-Startmehode" aufgerufen wird.
	@param jarFile Der FileName des jar Archives **/
	public static void loadJar(String jarFile) throws Throwable
	{
		ZipFile file = new ZipFile(jarFile);
		Enumeration<? extends ZipEntry> entries = file.entries();
		if (entries != null) 
		{
			while (entries.hasMoreElements()) 
			{
				ZipEntry entry = entries.nextElement();				
				InputStream stream = file.getInputStream(entry);
				byte[] b = stream.readAllBytes();
				stream.close();				
				if (entry.isDirectory()==false && isClass(b)) 
				{
					//System.out.println(entry.getName()); // Zum Testen
					loadClass(b);
				}
			}
		}		
		file.close();	
	}
			
	
	
	
	
	/**	Gleiche Methode wie loadJar() nur das hier das Jar-Archive schon entpackt in einem Ordner liegt
		@param FolderName Der Ordner-Name in dem das entpackte Archive liegt, alle Klassen. **/
	public static void loadFolder(File FolderName) throws Throwable
	{
		for (File file : FolderName.listFiles()) 
		{
			if (file.isDirectory()==false) 
			{
				InputStream stream = new FileInputStream(file);
				byte[] b = stream.readAllBytes();
				stream.close();	
				if(isClass(b))
				{
					//System.out.println(file.toString());  //<<<<<<<<<<<<<<<<<< Zum Testen			
					loadClass(b);				
				}
				else System.out.println("Diese Datei ist keine Class: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+file.toString());  //<<<<<<<< Zum Testen
			} 
			else {loadFolder(file);}
		}
	}
	
	
	
	
	
		
	

	/**	Lädt eine anonyme Klasse aus einem Byte-Array in das statische myClassLoader-Object dieser Klasse
	 	Wird verwendet wenn der Name der Klasse unbekannt ist.
		@param data die Klasse muss als Byte-Array übergeben werden
		@return	Die Klasse wird als anonyme Klasse zurück gegeben und kann ausgeführt werden.**/
	public static Class<?> loadClass(byte[] data)
    {	
		return myClassLoader.defineClass(null, data, 0, data.length); 
    }
	
	
	
	/**	Lädt eine Klasse aus einem Byte-Array
		@param data die Klasse muss als Byte-Array übergeben werden
		@param packageName Der Paket Name der in der Klasse oben als erstes steht. Wenn also oben steht "package PlugIns;" muss hier "PlugIns" übergeben werden! 
		@param className der Name der Klasse
		@return	Die Klasse wird als anonyme Klasse zurück gegeben und kann ausgeführt werden.**/
	public static Class<?> loadClass(byte[] data, String packageName, String className)
	{	
		String newClassName = packageName + "." + className;
		return myClassLoader.defineClass(newClassName, data, 0, data.length); 
	}
	
	
	
	/**	Führt die Startmethode einer Klasse aus im MyClassLoader-Object aus
		@param packageName Der Paket-Name in der die Klasse ist
		@param className Der Name der Klasse
		@param startMethod der Name der Startmethode ohne Klammern die ausgeführt werden soll. (Meisten "start")	**/
	@SuppressWarnings("deprecation")
	public static void runStartMethod(String packageName, String className, String startMethod) throws Exception
	{
		Class<?> clas = getClass(packageName, className);
		clas.getMethod(startMethod).invoke(clas.newInstance());
	}
	
	
	
	/**	Gibt eine Klasse zurück die vorher in dieses ClassLoader-Object geladen wurde. 
	 	@param packageName Der Paket-Name in der die Klasse ist
	 	@param name Der Name der Klasse	**/
	public static Class<?> getClass(String packageName, String className) throws ClassNotFoundException
	{
		String newClassName = packageName + "." + className;
		return myClassLoader.loadClass(newClassName);
	}
	
	
	
	
	

	
	
// ------------------------------------------------------------ Private Hilfs-Methoden ---------------------------------------------------------------------------------------------// 
    
	
	// Gibt true zurück wenn es sich um eine Java-Klasse handelt (Magic = cafebabe)
	private static boolean isClass(byte[] b)
	{
		b = Arrays.copyOf(b, 4);
		byte[] b1 = Convert.hexStringToByteArray("cafebabe");
		return Arrays.equals(b, b1);
	}

}