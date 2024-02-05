package seedExtractor;
import BTClib3001.Calc;
import BTClib3001.Convert;
import BTClib3001.ScryptHash;
import CoinGen.Action;
import GUI.GUI;



/********************************************************************
 * V1.1				 Angepasst für den CoinAddressGen.				*
 * Führt die Berechnungen des SeedExtractors durch					*
 *********************************************************************/



public class SeedExtractor 
{
	private static byte[] 	seed;
	public static byte[][] 	privKeys;
	
	
	
	
	
	// Entschlüsselt den Seed aus dem verschlüsseltem Seed und dem Passwort
	public static void encodeSeed(String vSeed, String passWort) throws Exception
	{
		if(vSeed.length() != 64) 					throw new Exception("Error Eingabe Seed! \nVerschlüsselter Seed muss genau 64 Zeichen lang sein!");
		if(vSeed.matches("^[0-9a-fA-F]+$")==false) 	throw new Exception("Error Eingabe Seed! \nVerschlüsselter Seed darf nur Hexa-Zeichen enthalten!");	
		seed = getScryptHash(Convert.hexStringToByteArray(Calc.getHashSHA256_from_HexString(vSeed + Calc.getHashSHA256(passWort))));
		GUI.txt_nr.setValue(1);			
		privKeys = keyDuplication(seed, Integer.parseInt(GUI.txt_max.getText()));
		Action.go();
	}
	
	
	/**	Generiert eine beliebige Menge isolierter (SHA256) Schlüsseln aus einem Master-Key.
	Die Ausgegebenen Schlüssel werden deterministisch vom "masterKey" erzeugt mit der Eigenschaft,
	dass alle erzeugten Keys frei von Informationen der anderen Keys oder dem "masterKey" sind.	
	@param masterKey als Byte-Array 
	@param count die Anzahl der SHA256 Schlüssel die erzeugt werden sollen. 
	@return Eine Menge von Schlüsseln mit einer Länge von 32Byte. Die Anzahl der Schlüssel hängt von "count" ab. **/
	private static byte[][] keyDuplication(byte[] masterKey, int count)
	{
		byte[] mKey = Calc.getHashSHA256(masterKey);
		byte[] kn = mKey;
		byte[][] out = new byte[count][32];
		for(int i=0;i<count;i++)
		{
			kn = Calc.getHashSHA256(kn);
			byte[] k = new byte[64];
			System.arraycopy(kn, 0, k, 0, 32);
			System.arraycopy(mKey, 0, k, 32, 32);
			out[i] = Calc.getHashSHA256(k);	
		}
		return out;
	}
	
	
	//	Generiert einen Scrypt-Hash, der zur Laufzeitverlängerung geeignet ist.
	//	Achtung hierfür sollte ein eigener Thread gestartet werden!
	//	Eingestellt sind Parameter die 1GB Ram verbrauchen und ca 8 Sec. Rechenzeit benötigen.
	private static byte[] getScryptHash(byte[] data) throws Exception
	{
		final int p			= 1;		// Parallelisierungsparameter. ist immer 1 
		final byte[] salt 	= {0x00};   // Salz wird hier nicht benötigt.
		final int memory 	= 524288;	// (N) CPU/Speicherkostenparameter in Byte. Muss größer als 1 sein und eine Potenz von 2 und kleiner als 2^(128*r/8)  Standart = 2^14, Hier verwendet 2^19
		final int r 		= 16;		// Die Blockgröße muss >= 1 sein. Hier wird 16 verwendet;
		final int outLen 	= 32;		// Die Länge des Ausgabe Hash.
		return ScryptHash.getHash(data, salt, memory, r, p, outLen);
	}
	
	
// Alt: Für ein separate Programm SeedExtractor vorgesehen.	Nicht löschen!
// Zeigt die Key´s mit dem Index "index" der AusgebeKonsole an.
//	public static void printKey(int index) 
//	{
//		try
//		{
//			String addr;
//			byte[] priv = privKeys[index-1];
//			PrvKey p 		= new PrvKey(priv,Convert.hexStringToByteArray("80"));
//			String prvHex 	= Convert.byteArrayToHexString(p.getHexPrivKey());
//			String prvWIF 	= p.getBase58PrivKey(GUI.unCompressed.isSelected()==false);
//			String pub 		= Calc.getPublicKey(prvHex, GUI.unCompressed.isSelected()==false);
//			String h160 	= Calc.getHashRIPEMD160_from_HexString(Calc.getHashSHA256_from_HexString(pub));
//			if(GUI.bech32.isSelected())
//			{
//				addr = Bech32Address.segwitToBech32("bc" , 0, Convert.hexStringToByteArray(h160));
//			}
//			else
//			{
//				BitcoinAddr address = new BitcoinAddr(Convert.hexStringToByteArray(h160) , Convert.hexStringToByteArray("00"));		
//				if(GUI.p2sh.isSelected()) 	addr = address.getP2SHAddress(Convert.hexStringToByteArray("05"));
//				else 						addr = address.getBase58Address();		
//			}	
//			GUI.txt_out.setText("Priv Hex: "+prvHex+"\nPriv WIF: "+prvWIF+"\nPub Key:  "+pub+"\nHash160:  "+h160+"\nAddress:  "+addr);	
//		}
//		catch(Exception e)
//		{
//			GUI.txt_out.setText(e.getMessage());
//		}
//	}
	

}