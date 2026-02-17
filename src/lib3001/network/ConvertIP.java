package lib3001.network;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lib3001.crypt.Calc;
import lib3001.crypt.Convert;



/********************************************************************************************************************************
 *	Version 1.0 				Mr.Maxwell																	vom 09.04.2019		*
 * 	LIB3001 Bibliotheks Klasse																									*
 *	Hier werden verschiedene Konvertierungen für IP-Adressen implementiert.														*
 *	Alle IP-Adressen werden hier als 18Byte-Array wie folgt definiert:															*
 *	IPv6 row Adresse:      200100009d3878cf14722656a4bec71ac669    --->  [2001:0:9d38:78cf:1472:2656:a4be:c71a]:50793			*
 *	IPv4 row Adresse:      00000000000000000000ffff5b4138e5c322	  --->   91.65.56.229:49954										*
 *																																*
 *	IPv6 Adressen dürfen NICHT in reduzierter Form vorliegen! Es müssen IMMER 8 Blöcke mit min. einer Ziffer vorliegen.!		*
 *	- 	[2001:0db8:0000:08d3:0000:8a2e:0070:7344]:12345 	gültig																*
 *	-	[2001:db8:0:8d3:0:8a2e:70:7344]:123456				gültig																*
 *	-	[2001:db8::1428:57ab]:12345							ungültig!															*
 ********************************************************************************************************************************/





public class ConvertIP
{



/**	Konvertiert die IP-Adresse mit Port von String nach 18Byte-Array.   Funktioniert für IPv4 und IPv6 Adressen.
 	IPv6 row Adresse:    [2001:0:9d38:78cf:1472:2656:a4be:c71a]:50793	--->	200100009d3878cf14722656a4bec71ac669
	IPv4 row Adresse:     91.65.56.229:49954							--->	00000000000000000000ffff5b4138e5c322
	@param ip Text-String der IP-Adresse mit Port z.B: 123.123.123.123:13888	oder	[2001:0:9d38:78cf:1472:2656:a4be:c71a]:50793
	@return IP-Adresse mit Port als 18Byte-Array
	@exception IllegalArgumentException wenn der übergebene String keine IP-Adresse ist.**/
public static byte[] stringToByteArray(String ip)
{
	byte[] out = new byte[18];
	if(ip.indexOf("[")!=0)																// Wenn es sich um eine IPv4 Adresse handelt
	{
		if ( !ip.matches("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\:[0-9]{1,5}" ) )
		{
		    throw new IllegalArgumentException( "IPv4 Address is an invalid format!  Must be: [0-255].[0-255].[0-255].[0-255]:[0-65535]" );
		}
		String[] ipArray = splitIP(ip);
		String ipAddr 	= ipArray[0];
		int port = Integer.valueOf(ipArray[1]);
		int pos1 = ipAddr.indexOf(".", 0);
		int pos2 = ipAddr.indexOf(".", pos1+1);
		int pos3 = ipAddr.indexOf(".", pos2+1);
		out[10] = (byte)0xff;
		out[11] = (byte)0xff;
		out[12] = (byte) ((Integer.valueOf(ipAddr.substring(0,      pos1)))&0xff);
		out[13] = (byte) ((Integer.valueOf(ipAddr.substring(pos1+1, pos2)))&0xff);
		out[14] = (byte) ((Integer.valueOf(ipAddr.substring(pos2+1, pos3)))&0xff);
		out[15] = (byte) ((Integer.valueOf(ipAddr.substring(pos3+1)))&0xff);
		out[16] = (byte) ((port & 0x0000FF00) >> 8);
		out[17] = (byte) ((port & 0x000000FF) >> 0);
		return out;
	}
	else																				// Wenn es sich um eine IPv6 Adresse handelt
	{
		String[] ipArray = splitIP(ip);
		String ipAddr 	= ipArray[0];
		ipAddr = ipAddr.replace("]", "");
		ipAddr = ipAddr.replace("[", "");
		if (!ipAddr.matches("\\A(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\z") )
		{
		    throw new IllegalArgumentException("IPv6 Address is an invalid format!  Must be: [xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx:xxxx]:xxxxx");
		}
		int port 	= Integer.valueOf(ipArray[1]);
		int[] pos 	= new int[8];
		byte[][] b 	= new byte[8][2];
		pos[0] = -1;
		for(int i=0;i<7;i++) pos[i+1] = ipAddr.indexOf(":", pos[i]+1);
		for(int i=0;i<7;i++) b[i] = Convert.to_fixLength(Convert.hexStringToByteArray_oddLength(ipAddr.substring(pos[i]+1, pos[i+1])), 2);
		 					 b[7] = Convert.to_fixLength(Convert.hexStringToByteArray_oddLength(ipAddr.substring(pos[7]+1          )), 2);
		for(int i=0;i<16;i++) out[i] = b[i/2][i%2];
		out[16] = (byte) ((port & 0x0000FF00) >> 8);
		out[17] = (byte) ((port & 0x000000FF) >> 0);
	}
	return out;
}



/**	Konvertiert die 18Byte-Array IP-Adresse mit Port nach String.    Funktioniert für IPv4 und IPv6 Adressen.
	IPv6 row Adresse:      200100009d3878cf14722656a4bec71ac669    --->  [2001:0:9d38:78cf:1472:2656:a4be:c71a]:50793
	IPv4 row Adresse:      00000000000000000000ffff5b4138e5c322	  --->   91.65.56.229:49954
@param ip 18Byte-Array der IP-Adresse mit Port
@return IP-Adresse mit Port als String z.B.: 123.123.123.123:18333		**/
public static String ByteArrayToString(byte[] ip) throws IllegalArgumentException
{
	if(ip.length!=18) {throw new IllegalArgumentException("Es wurden nicht genau 18Bytes für die IP-Adresse übergeben!");} 											// Fehler wenn nicht genau 18Bytes übergeben wurden
	if(ip[0]==0x00 && ip[1]==0x00 && ip[2]==0x00 && ip[3]==0x00 && ip[4]==0x00 && ip[5]==0x00 && ip[6]==0x00 && ip[7]==0x00 && ip[8]==0x00 && ip[9]==0x00 && ip[10]==(byte)0xff && ip[11]==(byte)0xff)
	{																																								// Wenn es sich um eine IPv4 Adresse handelt
		return (ip[12]&0xff) +"."+(ip[13]&0xff)+"."+(ip[14]&0xff)+"."+(ip[15]&0xff)+":"+ (((ip[16] & 0xff) << 8) | (ip[17] & 0xff));
	}
	else																																							// Wenn es sich um eine IPv6 Adresse handelt
	{
		return "[" 	+(String.format("%02x",ip[0]))+(String.format("%02x",ip[1]))
					+":"+(String.format("%02x",ip[2]))+(String.format("%02x",ip[3]))
					+":"+(String.format("%02x",ip[4]))+(String.format("%02x",ip[5]))
					+":"+(String.format("%02x",ip[6]))+(String.format("%02x",ip[7]))
					+":"+(String.format("%02x",ip[8]))+(String.format("%02x",ip[9]))
					+":"+(String.format("%02x",ip[10]))+(String.format("%02x",ip[11]))
					+":"+(String.format("%02x",ip[12]))+(String.format("%02x",ip[13]))
					+":"+(String.format("%02x",ip[14]))+(String.format("%02x",ip[15]))
					+"]"+":"+(((ip[16] & 0xff) << 8) | (ip[17] & 0xff));
	}
}



/**	Splittet die IP-Adresse in IP-Adresse und Port auf. Funktioniert für IPv4 und IPv6 Adressen.
	@param ip IP-Adresse mit Port als String z.B.:  123.123.123.123:18333   oder 	[2001:0:9d38:78cf:1472:2656:a4be:c71a]:50793
	@return String-Array mit genau 2 Elementen: 1 = IP-Adresse,  2 = Port   **/
public static String[] splitIP(String ip)
{
	String[] out = new String[2];
	Pattern p = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
	Matcher m = p.matcher(ip);
	if (m.matches())
	{
	  out[0] = m.group(1);
	  out[1] = m.group(2);
	}
	return out;
}



// -------------------------------------------------------------- Test Methoden die diese Konvertierungen testen ---------------------------------------------------------



/**	Testet die obigen Konvertierungs-Methoden in dem "count" Zufalls-Adressen erzeugt, konvertiert und geprüft werden.
	Es werden IPv4 und IPV6 Adressen getestet.
	@param count Anzahl der Zufalls-Adressen die geprüft werden sollen.	(1 Million ca. 12sec.)	**/
public static void testConvertIP(int count)
{
	int i=0;
	for(; i<count ; i++)
	{
		byte[] b1;
		if(i%2==0) 	b1 = getRandomIPv4();
		else 		b1 = getRandomIPv6();

		String str = ConvertIP.ByteArrayToString(b1);
		byte[] b2 = ConvertIP.stringToByteArray(str);
		if(!java.util.Arrays.equals(b1,b2))
		{
			System.out.println(Convert.byteArrayToHexString(b1));
			System.out.println(Convert.byteArrayToHexString(b2));
			throw new IllegalArgumentException("Fehler, Werte sind nicht gleich.");
		}
//		System.out.println(str);
//		System.out.println(Convert.byteArrayToHexString(b1));
//		System.out.println(Convert.byteArrayToHexString(b2)+"\n");
		if(i%100000==0)System.out.println(i);
	}
	System.out.println("Es wurden "+i+" Random-Adressen erfolgreich getestet.");
}



/** @return Erzeugt eine zufällige IPv4-Adresse als 18ByteArray  **/
public static byte[] getRandomIPv4()
{
	byte[] out = Convert.hexStringToByteArray("00000000000000000000ffff000000000000");
	byte[] nonce = Convert.double_to_8Bytes(Math.random());
	System.arraycopy(nonce, 2, out, 12, 6);
	return out;
}



/** @return Erzeugt eine zufällige IPv6-Adresse als 18ByteArray  **/
public static byte[] getRandomIPv6()
{
	byte[] out = Convert.hexStringToByteArray("000000000000000000000000000000000000");
	byte[] nonce = Calc.getHashSHA256(Convert.double_to_8Bytes(Math.random()));
	System.arraycopy(nonce, 0, out, 0, 18);
	return out;
}
}