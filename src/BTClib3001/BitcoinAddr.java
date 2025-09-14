package BTClib3001;



/***************************************************************************************************************
*	Version 1.3    						Autor: Mr. Nickolas-Antoine B.   						vom 21.02.2020				*
*	Non-static class that creates a coin address.														*
*	This class can be used for multiple coins. The respective coin is specified with the prefix argument.	*
****************************************************************************************************************/




public class BitcoinAddr 
{
			
	private byte[] pref_Pub; 		// The prefix that represents the respective coin
	private byte[] hash160;			// The coin address as 20Byte Hash160									
	
	

// ---------------------------------------------------- Constructors --------------------------------------------
	
/**	@param hash160 The Bitcoin address is passed to the constructor as Hash160 20 byte array.	
	@param pref_pubKey The prefix from the coin parameters for the Bitcoin address  **/
public BitcoinAddr(byte[] hash160, byte[] pref_pubKey) 
{	
	this.pref_Pub = pref_pubKey;	
	if(hash160.length != 20)  throw new IllegalArgumentException("Error in \"Hash160\": length ist not 20Byte!");
	else this.hash160 = hash160;	
}



/**	The Bitcoin address is passed to the constructor as a Base58 string.	
	@param addr Base58 string of the Bitcoin address		
	@param pref_pubKey The prefix from the coin parameters for the Bitcoin address  **/
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




// ----------------------------------------------------------  Bitcoin-Address Methods --------------------------------

/** Returns the Hash160 of the Bitcoin address as a 20Byte array. */
public byte[] getHash160()
{
	return hash160;
}	



/**	@return Returns the Bitcoin address as a Base58 string in WIF format (compressed is detected automatically)**/
public String getBase58Address()
{																												
	String prefix = Convert.byteArrayToHexString(pref_Pub);
	String h160   = Convert.byteArrayToHexString(hash160);
	String addr   = prefix+h160;
	String hash   = Calc.getHashSHA256_from_HexString(Calc.getHashSHA256_from_HexString(addr));
	return Convert.hexStringToBase58(addr+hash.substring(0,8));	
}



/**	@param prefix_P2SH The P2SH prefix must be passed as a numeric string (as it comes from the coin parameters).
	@return Returns the Bitcoin address as a Base58 string in P2SH witness format (starting with 3).  **/
public String getP2SHAddress(byte[] prefix_P2SH)
{
	String prefix = Convert.byteArrayToHexString(prefix_P2SH);	
	String h160   = Convert.byteArrayToHexString(getRedeemScript());
	String addr   = prefix+h160;
	String hash   = Calc.getHashSHA256_from_HexString(Calc.getHashSHA256_from_HexString(addr));
	return Convert.hexStringToBase58(addr+hash.substring(0,8));	
}



// Creates the redeem script for a SegWit P2SH address starting with 3. 
// The Hash160 is passed as usually calculated. However, it must come from a compressed pubkey!
// The "redeemScript" is returned, corresponding to the new Hash160, which can then be encoded into a Base58 address.
private byte[] getRedeemScript()
{
	final byte[] scriptPrefix = {0x00,0x14};  // The script prefix 	
	byte[] script = new byte[22];
	script[0] = scriptPrefix[0];
	script[1] = scriptPrefix[1];	
	System.arraycopy(hash160, 0, script, 2, 20);
	return Calc.getHashRIPEMD160(Calc.getHashSHA256(script));
}
}