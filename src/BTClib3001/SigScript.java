package BTClib3001;

/****************************************************************************************************************************
*       V2.0                        Author: Mr. Nickolas-Antoine B.                              from 04.10.2023                          *
*       Last change: getName(); added and class completely new, as it was faulty and poorly programmed.                      *
*       Part of BTClib3001                                                                                                   *
*       Non-static class that parses a signature from the SigScript.                                                         *
*       Procedure:                                                                                                           *
*       An object is created with the constructor: "new SigScript(sig)".                                                    *
*       The signature parts from the SigScript can now be accessed via the methods.                                          *
*       The raw SigScript in ByteArray must not be changed by the class!                                                     *
*       Currently, 2 Sig.Scripts are implemented: P2PK and P2PKH.                                                           *
*       More Sig.Scripts may be missing!                                                                                     *
*       Attention, this parser is used in the block explorer and processes every transaction. Note the constructor runtime!  *
*****************************************************************************************************************************/

public class SigScript
{
	private byte[] sig;			// Original raw Signature
	private int lenSig;			// The total length of this signature (first byte)
	private int posSig;			// The start position of this signature	
	private int lenSigRS;		// The length of the signature R+S
	private int posSigRS;		// The start position of the signature r+s
	private int posSigR;		// The start position of the signature r
	private int posSigS;		// The start position of the signature s
	private int lenSigR;		// The length of the signature r
	private int lenSigS;		// The length of the signature s
	private int lenPub;			// The length of the public key
	private int posPub;			// The start position of the public key
	private String 	name;		// The name of the sig.script
	private int		nr =-1;		// The number of the identified sig.script. P2PK=1, P2PKH=2 ...  If -1 an unknown sig.script was loaded.


// ------------------------------------- Constructor --------------------------------------------------

/** The constructor creates a SigScript object
	@param data The raw Sig.Script as ByteArray
	No exception should be thrown here, as this causes unwanted disruptions in applications (e.g. block explorer).
	For an unknown Sig.Script, only the name "unknown..." should be passed to the name field and the application should continue running. **/
	public SigScript(byte[] data)
	{
		this.sig = data;	
		try 
		{			
			if(calc_P2PK())  	return;						// P2PK   Pay-to-Public-Key
			if(calc_P2PKH()) 	return;						// P2PKH  Pay-to-Public-Key-Hash 		
			// Add more scripts here!
			name = "unknown sig.script";
		} 
		catch (Exception e) {name = "unknown sig.script";}	
	}	

// ---------------------------------------------------------------------- Public Methods ----------------------------------------------------------------

/** Returns the name of the signature script.
	Currently implemented: P2PK and P2PKH **/
public String getName()
{
	return name;
}

/** Returns the r part of the signature.
	In case of error or if the script is not recognized, a byte array of length NULL is returned. This does not cause an error in programs. **/	
public byte[] getSigR()
{
	if(nr==1 || nr==2)
	{
		byte[] out = new byte[lenSigR];
		System.arraycopy(sig, posSigR, out, 0, lenSigR);
		return out;
	}
	return new byte[0];
}

/** Returns the s part of the signature.
	In case of error or if the script is not recognized, a byte array of length NULL is returned. This does not cause an error in programs. **/		
public byte[] getSigS()
{
	if(nr==1 || nr==2) // For P2PK or P2PKH 
	{
		byte[] out = new byte[lenSigS];
		System.arraycopy(sig, posSigS, out, 0, lenSigS);
		return out;
	}
	return new byte[0];
}
	
/** Returns the public key (including the 0x02, 0x03 or 0x04 at the beginning).
	In case of error or if the script is not recognized, a byte array of length NULL is returned. This does not cause an error in programs.
	In a P2PK Sig.Script, no public key is present! In that case, a null-length byte array is also returned. **/
public byte[] getPubKey()
{
	if(nr==2) // Only in P2PKH is a public key present in the signature.
	{
		byte[] out = new byte[lenPub];
		System.arraycopy(sig, posPub, out, 0, lenPub);
		return out;
	}
	return new byte[0];
}
	
/** Returns the signature parts r, s, and pub.key in separate, labeled form as a string. **/
public String toString()
{
	String r 	= "Sig. r   = " + Convert.byteArrayToHexString(getSigR());
	String s 	= "Sig. s   = " + Convert.byteArrayToHexString(getSigS());
	String pub 	= "Pub. Key = " + Convert.byteArrayToHexString(getPubKey());
	return r+"\n"+s+"\n"+pub+"\n";	
}

// ---------------------------------------------------------------------- private Methods -------------------------------------------------------------

// Parses a P2PK Sig.Script.
// If it can be parsed, true is returned.
// A P2PK (Pay to Public-Key) script contains only R and S, but the public key is not included here!
private boolean calc_P2PK() throws Exception
{
	try
	{			
		int[] cs = Calc.decodeCompactSize(sig,0);
		posSig = cs[0];
		lenSig = cs[1];
			
		cs = Calc.decodeCompactSize(sig,posSig+1);
		posSigRS = cs[0];
		lenSigRS = cs[1];
		
		cs = Calc.decodeCompactSize(sig,posSigRS+1);
		posSigR = cs[0];
		lenSigR = cs[1];
		
		cs = Calc.decodeCompactSize(sig,posSigR+lenSigR+1);
		posSigS = cs[0];
		lenSigS = cs[1];
			
		if(posSigRS+lenSigRS+1 == sig.length) // If no further data follows, it is probably a P2PK Script
		{
			name = "P2PK"; 
			nr = 1;
			return true;
		}
		else return false;
	}
	catch (Exception e){return false;}
}
	
// Parses a P2PKH Sig.Script.
// If it can be parsed, true is returned.
// A P2PKH (Pay to Public-Key Hash) script contains R, S, and the public key. It is the most common and general SigScript of all legacy BTC transactions.
private boolean calc_P2PKH() throws Exception
{
	try
	{
		int[] cs = Calc.decodeCompactSize(sig,0);
		posSig = cs[0];
		lenSig = cs[1];
			
		cs = Calc.decodeCompactSize(sig,posSig+1);
		posSigRS = cs[0];
		lenSigRS = cs[1];
		
		cs = Calc.decodeCompactSize(sig,posSigRS+1);
		posSigR = cs[0];
		lenSigR = cs[1];
		
		cs = Calc.decodeCompactSize(sig,posSigR+lenSigR+1);
		posSigS = cs[0];
		lenSigS = cs[1];
					
		cs = Calc.decodeCompactSize(sig,posSigRS+lenSigRS+1);
		posPub = cs[0];
		lenPub = cs[1];	
			
		if(lenPub + lenSig + 2 == sig.length) // If no further data follows, it is probably a P2PKH Script
		{
			name = "P2PKH"; 
			nr = 2;
			return true;
		}
		else return false;	
	}
	catch (Exception e){return false;}
}	
}
