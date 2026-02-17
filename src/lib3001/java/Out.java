package lib3001.java;
import java.math.BigInteger;

import lib3001.crypt.Convert;



/****************************************************************************************************************
*	Version 1.0    						Autor: Mr. Maxwell   						vom 08.02.2023				*
* 	LIB3001 Bibliotheks Klasse																					*
*	Statische Printer-Klasse																					*
*	Es werden diverse "Out.println()..." Methoden bereitgestellt für verschiedenste spezielle Formate			*	
*	Die Klasse dient nur zum Anzeigen aus diversen Formaten. 													*
****************************************************************************************************************/



public class Out 
{

	
// ------------------------------------------------- Allgemein alle Formate ---------------------------------------------------	
	
	/** Gleiche wie "System.out.print()"	**/
	public static void print(Object data)
	{
		System.out.print(data);
	}
	
	/** Gleiche wie "System.out.println()"	**/
	public static void println(Object data)
	{
		System.out.println(data);
	}
	
	
	
// ------------------------------------------------- Byte-Array ---------------------------------------------------	
	
	/** Das Byte-Array wird als Hex-String ausgegeben.	**/
	public static void print(byte[] b)
	{
		System.out.print(Convert.byteArrayToHexString(b));
	}
	
	/** Das Byte-Array wird als Hex-String ausgegeben.	**/
	public static void println(byte[] b)
	{
		System.out.println(Convert.byteArrayToHexString(b));
	}
	
	
	
// ------------------------------------------------- hex Print Ausgaben in Hex-String --------------------------------	
	
	
	/** Das Byte-Array wird als Hex-String ausgegeben.	**/
	public static void hexPrint(byte[] b)
	{
		System.out.print(Convert.byteArrayToHexString(b));
	}
	
	/** Das Byte-Array wird als Hex-String ausgegeben.	**/
	public static void hexPrintln(byte[] b)
	{
		System.out.println(Convert.byteArrayToHexString(b));
	}
	
	
	public static void hexPrint(int data)
	{
		System.out.print(Convert.byteArrayToHexString(Convert.int_To_4_ByteArray(data)));		
	}
	
	public static void hexPrintln(int data)
	{
		System.out.println(Convert.byteArrayToHexString(Convert.int_To_4_ByteArray(data)));		
	}
	
	/**	Integer-Array wird als Hexa in Gruppen angezeigt **/
	public static void hexPrint(int[] data)
	{
		 for(int i=0; i<data.length;i++) 
		 {
			 Out.hexPrint(data[i]); Out.print(" ");
		 }
	}
	
	/**	Integer-Array wird als Hexa in Gruppen angezeigt **/
	public static void hexPrintln(int[] data)
	{
		 for(int i=0; i<data.length;i++) 
		 {
			 Out.hexPrint(data[i]); Out.print(" ");
		 }
		 Out.println("\n"); 
	}
	
	
	
	
// ------------------------------------------------- Binary Print Ausgaben in Binär  --------------------------------	

	/** Ausgabe von Integer oder Byte wie folgt als Beispiel:
	 	01111111 11111111 11111111 11111111   	**/
	public static void binaryPrint(int data) 
	{
	    int gruppe = 8;  // 8 Zahlen in einer Gruppe	
		StringBuilder out = new StringBuilder();
	    for(int i = 31; i >= 0 ; i--) 
	    {
	        int mask = 1 << i;
	        out.append((data & mask) != 0 ? "1" : "0");
	        if (i % gruppe == 0) out.append(" ");
	    }
	    out.replace(out.length() - 1, out.length(), "");
	    System.out.print(out.toString());
	}
	
	
	/** Ausgabe von Integer oder Byte wie folgt als Beispiel:
 	01111111 11111111 11111111 11111111   	**/
	public static void binaryPrintln(int data) 
	{
	    int gruppe = 8;  // 8 Zahlen in einer Gruppe	
		StringBuilder out = new StringBuilder();
	    for(int i = 31; i >= 0 ; i--) 
	    {
	        int mask = 1 << i;
	        out.append((data & mask) != 0 ? "1" : "0");
	        if (i % gruppe == 0) out.append(" ");
	    }
	    out.replace(out.length() - 1, out.length(), "");
	    System.out.println(out.toString());
	}


	
// ------------------------------------------------- BigInteger  --------------------------------	
	
public static void hexPrintln(BigInteger[] vektor)
{
	for(int i=0; i<vektor.length; i++) System.out.println(vektor[i].toString(16));	
}
	
	
	
	
	
	
	
	
	
	
}