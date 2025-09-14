package BTClib3001;
import java.util.Arrays;



/********************************************************************************************************************
*       V1.2                                 Author: Mr. Nickolas-Antoine B.                                 from 15.01.2021         *
*       BTClib3001 class                                                                                              *
*       Non-static class that parses various parts of the Pk.Script.   (some Pk.Scripts may still be missing)         *
*       Procedure:                                                                                                    *
*       An object is created with the constructor: "new PkScript(pk)".                                                *
*       The various parts can now be accessed via the methods.                                                        *
*       The raw Pk.Script in ByteArray must not be changed by the class!                                              *
*       Info: https://bitcoinblog.de/2019/07/19/p2pk-p2pkh-p2sh-p2wpk-was-diese-wichtigen-abkuerzungen-bedeuten/      *
********************************************************************************************************************/



public class PkScript
{

    private byte[]     pk;         // Raw. Pk.Script as it originally appears in the Tx.
    private String     name;       // The name of the identified Pk.Script. In case of error: "unknown PK.script";
    private int        nr =-1;     // The number of the identified Pk.Script. P2PK=1, P2PKH=2, P2SH=3, P2WPKH=4, OP_RETURN=5
    private byte[]     h160;       // Hash160
    private boolean io;            // io = true, if the PK.Script was recognized without error.
    
    final byte[]   MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
    final byte[]   TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};
    
    
	
//------------------------------------------ Constructor -----------------------------------------------/

/** The constructor creates a PkScript object
@param pk The raw Pk.Script as ByteArray   **/
public PkScript(byte[] pk)
{
    this.pk = pk;    
    try 
    {           
        if(calc_P2PK())     {io=true; return;}                        // P2PK   Pay-to-Public-Key
        if(calc_P2PKH())    {io=true; return;}                        // P2PKH  Pay-to-Public-Key-Hash 
        if(calc_P2SH())     {io=true; return;}                        // P2SH   Pay-to-Script-Hash 
        if(calc_P2WPKH())   {io=true; return;}                        // P2WPKH Pay To Witness Public Key Hash (bech32 addresses)    
        if(calc_RETURN())   {io=true; return;}                        // OP_RETURN NULL-data that cannot be spent    
        name = "unknown Pk.script";  io = false;
    } 
    catch (Exception e) {name = "unknown Pk.script";}    
}



//------------------------------------------- Public Methods ------------------------------------------//

/** Returns the name of the PK.Script (e.g.: P2PK)
    If the script is not recognized, returns "unknown Pk.script". **/
public String getName()
{
    return name;
}



/** Returns the name of the PK.Script as a number:
P2PK   Pay-to-Public-Key        = 1                         
P2PKH  Pay-to-Public-Key-Hash   = 2                       
P2SH   Pay-to-Script-Hash       = 3                 
P2WPKH (bech32 addresses)       = 4
OP_RETURN                       = 5
If the script is not recognized = -1 
This is basically the same as getName() but the number as int is easier and faster to process. **/
public int getNr()
{
    return nr;
}



/** Returns the Hash160 as a 20 ByteArray from the PK.Script, if it could be decoded.
    @throws Exception If the script cannot be decoded, "Unknown PK.Script Exception!" is thrown.  **/ 
public byte[] getHash160() throws Exception
{
    if(io==true && h160.length==20) return h160;
    else throw new Exception("PK.Script could not be decoded. Unknown PK.Script!");
}



/** Returns the Bitcoin address from the PK.Script, if it could be decoded.
    If the Pk.Script could not be decoded, returns "unknown Pk.script".  
    @param magic The magic value of the network must be passed.**/
public String getBitcoinAddress(byte[] magic) 
{
    try
    {       
        if(name.equals("RETURN"))                                                                 // OP_RETURN
        { 
            String str =  "OP_RETURN Data:   " + new String(Calc.parseCompactSize(pk, 1),"UTF-8"); 
            return str;
        }               
        if(name.equals("P2WPKH"))                                                                 // bech32 address
        {
            
            String na;
            if(Arrays.equals(magic,MAINNET)) na = "bc";
            else na = "tb";           
            return Bech32Address.segwitToBech32(na, 0, getHash160());
        }
        else                                                                                       // old addresses
        {           
            byte[] erg = new byte[21];
            erg[0] = calcPreFix(magic);
            System.arraycopy(getHash160(), 0, erg, 1, 20);
            byte[] h = Calc.getHashSHA256(Calc.getHashSHA256(erg));
            erg = Arrays.copyOf(erg, 25);
            System.arraycopy(h, 0, erg, 21, 4);
            return String.valueOf(Convert.toBase58(erg));
        }
    }
    catch (Exception e) {return "PK.Script could not be decoded. Unknown PK.Script!";}
}





//------------------------------------------- Private Methods ------------------------------------------//


// Selects the correct address prefix for TestNet or MainNet and the different script types
private byte calcPreFix(byte[] magic)
{
    if(Arrays.equals(magic,MAINNET))
    {
        if(name.equals("P2SH")) return  0x05;
        else                    return  0x00;
    }       
    if(Arrays.equals(magic,TESTNET3))
    {
        if(name.equals("P2SH")) return  (byte) 0xc4;
        else                    return  0x6f;
    }   
    return 0x00;
}



// Calculates the Hash160 from type P2PK (Pay-to-Public-Key)
private boolean calc_P2PK() throws Exception
{   
    try
    {
        byte[] pk = Calc.parseCompactSize(this.pk, 0);   
        if(pk[0]==(byte)0x04 && pk.length==65)
        {
            byte[] h = Calc.getHashSHA256(pk);
            h160 = Calc.getHashRIPEMD160(h);
            name = "P2PK";
            nr   = 1;
            return true;
        }
        else return false;   
    }
    catch (Exception e){return false;}
}



// Parses the Hash160 from the P2PKH (Pay-to-Public-Key-Hash)
private boolean calc_P2PKH()
{
    if(pk[0]==(byte)0x76 && pk[1]==(byte)0xa9 && pk[2]==(byte)0x14 && pk[23]==(byte)0x88 && pk[24]==(byte)0xac && pk.length==25)
    {   
        h160 = new byte[20];
        System.arraycopy(pk, 3, h160, 0, 20);
        name = "P2PKH";
        nr   = 2;
        return true;
    }
    else return false;   
}



// Parses the Hash160 from the P2SH (Pay-to-Script-Hash)
private boolean calc_P2SH()
{
    if(pk[0]==(byte)0xa9 && pk[1]==(byte)0x14 && pk[22]==(byte)0x87 && pk.length==23)
    {   
        h160 = new byte[20];
        System.arraycopy(pk, 2, h160, 0, 20);
        name = "P2SH";
        nr   = 3;
        return true;
    }
    else return false;   
}



// Parses the Hash160 from the P2WPKH (Pay-To-Witness-Public-Key-Hash)
private boolean calc_P2WPKH()
{
    if(pk[0]==(byte)0x00 && pk[1]==(byte)0x14 && pk.length==22)
    {   
        h160 = new byte[20];
        System.arraycopy(pk, 2, h160, 0, 20);
        name = "P2WPKH";
        nr   = 4;
        return true;
    }
    else return false;   
}



// Parses the OP_RETURN data
private boolean calc_RETURN()
{
    if(pk[0]==(byte)0x6a)
    {   
        name = "RETURN";
        nr   = 5;
        return true;
    }
    else return false;   
}
}