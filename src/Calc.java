import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;





public class Calc
{
     
	
	public static String AddresVersionsNr 	= "00";				// Die Bitcoin Adress VersionsNr. "00" = MainNet und "6f" = TestNet
	public static String PrivKeyVersionsNr 	= "80";				// Die Private Key    VersionsNr. "80" = MainNet und "ef" = TestNet
	
	
	
	
	
	
	
	
	
	
// ---------------------------------------------- Private Key convert --------------------------------------------------------//	
	
	
	
	
	// erkennt den String als private-Key in den 4 möglichen Formaten, und gibt ihn als Hexa-PrivateKey zurück
	// Je nach Auswahl Main-Net / Test-Net, unterschiedlich
	public static String txtToHexPrivKey(String str) throws KeyException
	{
		str = str.trim();
		int s;

		if(GUI_MAIN.btnTestNet.isSelected()) 	s = getFormat_TestNet(str);
		else 									s = getFormat_MainNet(str);
		
		switch(s)
		{
			case-1: 	throw new KeyException(Language.F3);																				//-1 = Fehler kein richtiges Format erkannt
			case 0:     throw new KeyException(Language.F0);																				// 0 = Null String
			case 1:  	return str;  																										// 1 = Hexa       
			case 2:  	return Convert.base58_PrivateKey_to_HexPrivateKey(str);																// 2 = Base58
			case 3:     return Convert.base58compressed_PrivateKey_to_HexPrivateKey(str);													// 3 = Base58 compressed              			
			case 4:     return Convert.base64_PrivateKey_to_HexPrivateKey(str);  															// 4 = Base64            				
		}
		return null;
	}
	
	
	
	

	
// Der Hex PrivateKey wird zum Bitcoin conforman Private Key in Base58 convertiert
// mit VersionsNr. 80  und SHS256 Checksumme
	public static String ConvertPrivKeyToBitcoinConformBase58(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
	   if(str.equals("")) return "";
	   //String msb = "80";                         																										// Die VersionsNr.80  MainNet
	   //String msb = "ef";                         																										// Die VersionsNr.80  TestNet3
	   String h = getHashSHA256_from_HexString(PrivKeyVersionsNr+str);
	          h = getHashSHA256_from_HexString(h);              																							// 2 mal SHA256
	          h = h.substring(0,8);																															// String abschneiden bis auf die letzen 8 Zeichen
	   return Convert.hexStringToBase58(PrivKeyVersionsNr + str + h);																						// Strings zusammenf�gen und in Base58 umwandeln
	}
	
	
// Der Hex PrivateKey wird zum Bitcoin conforman Private Key in Base58 compressed convertiert
// mit VersionsNr. 80 , compressions Flag und SHS256 Checksumme
   	public static String ConvertPrivKeyToBitcoinConformBase58Compressed(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
       if(str.equals("")) return "";
   	  // String msb = "80";                         																										// Die VersionsNr. 80  MainNet
   	  // String msb = "ef";                         																										// Die VersionsNr.80  TestNet3
	   String com = "01";                         																											// Compressed Bit  01
	   String h = getHashSHA256_from_HexString(PrivKeyVersionsNr+str+com);
	          h = getHashSHA256_from_HexString(h);               																							// 2 mal SHA256
	          h = h.substring(0,8);																															// String abschneiden bis auf die letzen 8 Zeichen
	  return Convert.hexStringToBase58(PrivKeyVersionsNr + str + com + h);        																			// Strings zusammenf�gen und in Base58 umwandeln
	}

	
	
// ---------------------------------------------- Bitcoin Adresse wird aus Public Key erstellt --------------------------------------------------------//		
	
// Der Public Key wird zur Bitcoin Adresse convertiert
// 1X SHA256Hash,  1X RIPEMD160 Hash,  VersionsBit 01, 2xSHA256 letzten 8 Zeichen angeh�ngt
    public static String ConvertPublicKeyToBitcoinAdress(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
      String h   = getHashSHA256_from_HexString(str);																										// 1 mal SHA256					
	         h   = getHashRIPEMD160_from_HexString(h);																										// 1 mal RIPEMD160      
	  //String msb = "00";  																																	// Die VersionsNr. 00  Main Net
	  //String msb = "6f";  																																	// Die VersionsNr. 6f  TestNet3
	  String adr = AddresVersionsNr + h;                                             																					// Strings werden zusammengef�rgt
	         h   = getHashSHA256_from_HexString(adr);					
	         h   = getHashSHA256_from_HexString(h);																											// 2 mal SHA256
	         adr = adr +  h.substring(0,8);																													// die rohe Adresse wird mit dem gek�rztem 2fach Hash zusammengef�gt					
	  return Convert.hexStringToBase58(adr);																												// nach Base58
   }
	
	
	
// ------------------------------------------------- Würfel --------------------------------------------------------------//	

// Hier werden die Würfelzeichen in HEX konviertiert
// 1=1, 2=2, 3=3 4=4, 5=5, 6=0
// Mamimaler Würfelwerd (Mod) = 1621416542-2615626236-3462631235-4363525141-6636261141-4266313436-1546433233-1342224233-3313325535-4344331641
	public static String wuerfelToHexString(String str)                  
	{
	  BigInteger mod = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141",16);											    // Modulo des Privatkey / �berlauf                                                                                                                                                      
	  str = str.replaceAll("-","");								 																							// Platzhalter Zeichen "-" wird aus dem String entfernt
	  str = str.replaceAll("6","0");																														// Die Ziffer 6 wird mit 0 ersetzt																														// nach String     zur Basis 16
      BigInteger dec = new BigInteger(str,6);																													// nach BigInteger zur Bases  6
      dec = dec.mod(mod);																																	// Private Key Modulo FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141 
      String erg = dec.toString(16);  																														// nach String     zur Bases 16 
      while(erg.length() < 64) erg="0"+erg;																													// String wird vorne mit nullen aufgef�llt
      return erg;
	}
	
	

	

// ------------------------------------------------- Hash SHA256 ------------------------------------------------------//
	
	public static String getHashSHA256_from_HexString(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
	  byte[] b = getHashSHA256(Convert.hexStringToByteArray(str));
	  return Convert.byteArrayToHexString(b);
	}
	
	
	public static String getHashSHA256(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
	  byte[] b = getHashSHA256((str).getBytes("UTF-8"));
	  return Convert.byteArrayToHexString(b);
	}
	
	
	public static byte[] getHashSHA256(byte[] b) throws NoSuchAlgorithmException
	{
	  MessageDigest sha = MessageDigest.getInstance("SHA-256");
      return sha.digest(b);
	}
	
	
	
// ------------------------------------------------- Hash RIPEMD-160 ----------------------------------------------------//
		
	public static String getHashRIPEMD160_from_HexString(String str)
	{
	  byte[] b = getHashRIPEMD160(Convert.hexStringToByteArray(str));
	  return Convert.byteArrayToHexString(b);
	}
	
	
	public static byte[] getHashRIPEMD160(byte[] b)
	{
	  RIPEMD160Digest ripemd = new RIPEMD160Digest();
      ripemd.update (b, 0, b.length);
      byte[] hash160 = new byte[ripemd.getDigestSize()];
      ripemd.doFinal (hash160, 0);
	  return hash160;	
	}
	
	
	
// ------------------------------------------------- Public Key ---------------------------------------------------------//	
	
	public static String getPublicKeyX(String str)
	{
	  return getPublicKey(str).substring(0,66);	
	}
	
	
	public static String getPublicKeyY(String str)
	{
	  return getPublicKey(str).substring(66,130);	
	}
	
	
	public static String getPublicKey(String str) 
	{ 
	  byte[] b = getPublicKey(Convert.hexStringToByteArray(str));
	  return Convert.byteArrayToHexString(b);
	}
	
	
	public static byte[] getPublicKey(byte[] privateKey) 
	{
	  ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(Long.toString(Long.parseLong(GUI_MAIN.hash.substring(0,9),36)^105187519678246L,36)); 
	  ECPoint pointQ = spec.getG().multiply(new BigInteger(1, privateKey)); 
	  return pointQ.getEncoded(false); 
	} 
	
	
	
// ------------------------------------------------ Prüfen  ------------------------------------------------------------//
	
	
	
	// gibt das Format des Strings zurück: (Nur für Main-Net)
	//-1 = Fehler kein richtiges Format erkannt
	// 0 = Null String
	// 1 = Hexa
	// 2 = Base58
	// 3 = Base58 compressed
	// 4 = Base64
	public static int getFormat_MainNet(String str)
	{
		if(str.equals(""))   								return 0;																									// prüfen ob leer String
		if(str.length()==64 && str.matches("[0-9a-fA-F]+")) return 1;																									// prüfen auf Hexa
		if(str.length()==51 && str.matches("[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+") && str.charAt(0)=='5') 							return 2;	// prüfen auf Base58
		if(str.length()==52 && str.matches("[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+") && (str.charAt(0)=='L' || str.charAt(0)=='K'))	return 3;	// prüfen auf Base58 compressed
		if(str.length()==44 && str.matches("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=]+") &&  str.charAt(43)=='=') 				return 4;	// prüfen auf Base64
		return -1;
	}
	
	
	
	
	
	
	
	
	
	
	// gibt das Format des Strings zurück (Nur für Test-Net)
	//-1 = Fehler kein richtiges Format erkannt
	// 0 = Null String
	// 1 = Hexa
	// 2 = Base58
	// 3 = Base58 compressed
	// 4 = Base64
	public static int getFormat_TestNet(String str)
	{
		if(str.equals(""))   								return 0;																									// prüfen ob leer String
		if(str.length()==64 && str.matches("[0-9a-fA-F]+")) return 1;																									// prüfen auf Hexa
		if(str.length()==51 && str.matches("[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+") && str.charAt(0)=='9') 							return 2;	// prüfen auf Base58 Testnet
		if(str.length()==52 && str.matches("[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+") && (str.charAt(0)=='c' || str.charAt(0)=='C'))	return 3;	// prüfen auf Base58 compressed Testnet
		if(str.length()==44 && str.matches("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=]+") &&  str.charAt(43)=='=') 				return 4;	// prüfen auf Base64
		return -1;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// hier wird gepr�ft ob der Private-Key(Hex) inerhalb des erlaubten Bereiches liegt
	//  0 wird zur�ckgegeben wenn ok
	// -1 wird zur�ckgegeben wenn key zu klein
	//  1 wird zur�ckgegeben wenn key zu gro�
	//  2 wird zur�ckgegeben wenn key sehr klein und daher als unsicher gilt
	public static int is_PrivKey_Valid(String str)
	{
		if(str.equals("")) str="0";																										// Der Key darf nich leer sein
		BigInteger min = new BigInteger("0",16);																						// Null Key
		BigInteger uns = new BigInteger("00000F0000000000000000000000000000000000000000000000000000000000",16);							// bis zu diesem Wert gelten die Keys als unsicher (wilk�rliche Festlegung von mir)
		BigInteger max = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140",16);							// Maximalwert den ein Schl�ssel haben kann, ab dort: �berlauf (mod)
		BigInteger key = new BigInteger(str,16);
		if(key.compareTo(min) <= 0) return -1; 
		if(key.compareTo(max) >  0) return  1;
		if(key.compareTo(uns) <  0) return  2;
		return 0;
	}
	
	
	
	
	
	// pr�ft ob der in Base58 eingegebene Private Key den richtigen Hash (SHA256) am Ende hat
	public static boolean ist_PrivateKey_Base58_Valid(String str)
	{
		if(str.equals("") || str.length() != 51) 		return false;                                                                   // Format pr�fung
		String roh = Convert.Base58ToHexString(str,74);																					// wird nach Hex konvertiert  (74 Zeichen lang)
		String a = roh.substring(0,66);																									// die ersten 66 Zeichen
		String b = roh.substring(66,74).toUpperCase();																					// die letzten 8 Zeichen die als Hash(SHA256) angeh�ngt sind werden in gro�buchstaben konvertiert
		String h = null;																												// der neue Hash, zum vergleich
		try
		{
			h = getHashSHA256_from_HexString(a);																						// 2x SHA256 vom ersten Teil
			h = getHashSHA256_from_HexString(h);
		} 
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e)	{e.printStackTrace();}										
		h=h.substring(0,8); 																											// die ersten 8 Zeichen des neuen Hash
		if(b.equals(h)) return true;																									// die Hashs werden verglichen
		return false;
	}
	
	
	
	
	
	// pr�ft ob der in Base58-compressed eingegebene Private Key den richtigen Hash (SHA256) am Ende hat
	public static boolean ist_PrivateKey_Base58compressed_Valid(String str)
	{
		if(str.equals("") || str.length() != 52) 		return false;                                                                   // Format pr�fung
		String roh = Convert.Base58ToHexString(str,76);																					// wird nach Hex konvertiert  (76 Zeichen lang)
		String a = roh.substring(0,68);																									// die ersten 68 Zeichen
		String b = roh.substring(68,76).toUpperCase();																					// die letzten 8 Zeichen die als Hash(SHA256) angeh�ngt sind werden in gro�buchstaben konvertiert
		String h = null;																												// der neue Hash, zum vergleich
		try
		{
			h = getHashSHA256_from_HexString(a);																						// 2x SHA256 vom ersten Teil
			h = getHashSHA256_from_HexString(h);
		} 
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e)	{e.printStackTrace();}										
		h=h.substring(0,8); 																											// die ersten 8 Zeichen des neuen Hash
		if(b.equals(h)) return true;																									// die Hashs werden verglichen
		return false;
	}
	
	
	// pr�ft ob der in Base64 eingegebene Private-Key das richtige Format hat
	public static boolean ist_PrivateKey_Base64_Valid(String str)
	{
		if(str.length()==44 && str.matches("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=]+") &&  str.charAt(43)=='=') 		return true;          
		return false;																													// ist immer "true" da bei Base64 keine Hash-Pr�fung vorgesehen ist
	}
	
	
	
	
	
	
	
	
	
}
