package CoinGen;



/***************************************************************************************************************
*	Version 1.3    						Autor: Mr. Maxwell   						vom 21.02.2020				*
*	Nicht statische Klasse die eine Coin-Adresse erstellt.														*
*	Diese Klasse ist für mehrere Coins verwendbar. Mit dem Präfix Argument wird der jeweilige Coin angegeben.	*
****************************************************************************************************************/




public class BitcoinAddr 
{
			
	private byte[] pref_Pub; 		// Das Prefix welches den jeweiligen Coin repräsentiert
	private byte[] hash160;			// Die Coin-Adresse als 20Byte Hash160									
	
	

// ---------------------------------------------------- Konstruktoren --------------------------------------------
	
/**	@param hash160 Dem Konstruktor wird die BitcoinAdresse als Hash160 20 Byte-Array übergeben.	
	@param pref_pubKey Das Präfix aus den CoinParametern für die Bitcoin Adresse  **/
public BitcoinAddr(byte[] hash160, byte[] pref_pubKey) 
{	
	this.pref_Pub = pref_pubKey;	
	if(hash160.length != 20)  throw new IllegalArgumentException("Error in \"Hash160\": length ist not 20Byte!");
	else this.hash160 = hash160;	
}



/**	Dem Konstruktor wird die BitcoinAdresse als Base58 String übergeben.	
	@param addr Base58 String der Bitcoin-Adresse		
	@param pref_pubKey Das Präfix aus den CoinParametern für die Bitcoin Adresse  **/
public BitcoinAddr(String addr, byte[] pref_pubKey) throws IllegalArgumentException
{
	this.pref_Pub = pref_pubKey;
	String address = Convert.Base58ToHexString(addr, 50);	
	if(address.substring(0,2).equals(Convert.byteArrayToHexString(pref_pubKey)))
	{
		String m = address.substring(0,42);
		String h   = Calc.getHashSHA256_from_HexString(Calc.getHashSHA256_from_HexString(m));	// 2 x SHA256				
		h=h.substring(0, 8);
		if(h.equals(address.substring(42, 50))) hash160 =  Convert.hexStringToByteArray(address.substring(2,42));
		else throw new IllegalArgumentException("Error in \"BitcoinAddr\" : False Address-Hash!"); 
		return;
	}
	else throw new IllegalArgumentException("Error in \"BitcoinAddr\" : False Network-Version!");
}




// ----------------------------------------------------------  Bitcoin-Adress Methoden --------------------------------

/**	Gibt den Hash160 der Bitcoin-Adresse  als 20Byte-Array zurück. */
public byte[] getHash160()
{
	return hash160;
}	



/**	@return Gibt die Bitcoin-Adresse als Base58 String im WIF-Format zurück  (compressed wird selbst erkannt)**/
public String getBase58Address()
{																												
	String prefix = Convert.byteArrayToHexString(pref_Pub);
	String h160   = Convert.byteArrayToHexString(hash160);
	String addr   = prefix+h160;
	String hash   = Calc.getHashSHA256_from_HexString(Calc.getHashSHA256_from_HexString(addr));
	return Convert.hexStringToBase58(addr+hash.substring(0,8));	
}



/**	@param prefix_P2SH Es muss das P2SH-Prefix als Zahlen-String (so wie es aus den CoinParametern kommt) übergeben werden.
	@return Gibt die Bitcoin-Adresse als Base58 String im P2SH-Witness Format (mit 3 beginnend) zurück.  **/
public String getP2SHAddress(byte[] prefix_P2SH)
{
	String prefix = Convert.byteArrayToHexString(prefix_P2SH);	
	String h160   = Convert.byteArrayToHexString(getRedeemScript());
	String addr   = prefix+h160;
	String hash   = Calc.getHashSHA256_from_HexString(Calc.getHashSHA256_from_HexString(addr));
	return Convert.hexStringToBase58(addr+hash.substring(0,8));	
}



// Erstellt das RedeemScript für eine SegWit S2SH Adresse die mit 3 beginnt. 
// Es wird der Hash160 übergeben so wie er üblicherweise berechnet wird. Allerdings muss er von einem komprimierten PubKey stammen!
// Zurück gegeben wird das "RedeemScript" welches dem neuem Hash160 entspricht, der dann so in eine Base58 Adresse codert werden kann.
private byte[] getRedeemScript()
{
	final byte[] scriptPrefix = {0x00,0x14};  // Das Script Prefix 	
	byte[] script = new byte[22];
	script[0] = scriptPrefix[0];
	script[1] = scriptPrefix[1];	
	System.arraycopy(hash160, 0, script, 2, 20);
	return Calc.getHashRIPEMD160(Calc.getHashSHA256(script));
}
}