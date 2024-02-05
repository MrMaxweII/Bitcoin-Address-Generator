package BTClib3001;
import java.util.Arrays;



/********************************************************************************************************************
*		V1.2   							 Autor: Mr. Maxwell   								vom 15.01.2021			*
*		BTClib3001 Klasse																							*
*		Nicht statische Klasse die verschiedene Teile aus dem Pk.Script parst.	(evtl. fehlen noch Pk.Scripte)		*
*		Vorgehensweise:																								*
*		Es wird mit dem Konstruktor ein "new PkScript(pk)" Object erstellt.											*
*		Nun können die verschiedenen Teile über die Methoden abgerufen werden.										*
*		Das raw Pk.Script in ByteArray darf durch die Klasse nicht verändert werden!								*
*		Info: https://bitcoinblog.de/2019/07/19/p2pk-p2pkh-p2sh-p2wpk-was-diese-wichtigen-abkuerzungen-bedeuten/	*
********************************************************************************************************************/



public class PkScript
{

	private byte[] 	pk;			// Raw. Pk.Script wie es original in der Tx steht.
	private String 	name;		// Der Name   des identifizierten Pk.Scriptes. Ist im Fehlerfall "unknown PK.script"; 
	private int		nr =-1;		// Die Nummer des identifizierten Pk.Scriptes. P2PK=1, P2PKH=2, P2SH=3, P2WPKH=4, OP_RETURN=5
	private byte[] 	h160;		// Hash160
	private boolean io;			// io = true, wenn das PK.Script fehlerfrei erkannt wurde. 
	
	final byte[] 	MAINNET = {(byte) 0xf9,(byte) 0xbe,(byte) 0xb4,(byte) 0xD9};
	final byte[] 	TESTNET3 = {(byte) 0x0b,(byte) 0x11,(byte) 0x09,(byte) 0x07};
	
	
	
//------------------------------------------ Konstruktor -----------------------------------------------/

/**	Der Konstruktor erstellt ein PkScript Objekt
@param pk Das raw Pk.Script als ByteArray   **/
public PkScript(byte[] pk)
{
	this.pk = pk;	
	try 
	{			
		if(calc_P2PK())  	{io=true; return;}						// P2PK   Pay-to-Public-Key
		if(calc_P2PKH()) 	{io=true; return;}						// P2PKH  Pay-to-Public-Key-Hash 
		if(calc_P2SH()) 	{io=true; return;}						// P2SH   Pay-to-Script-Hash 
		if(calc_P2WPKH()) 	{io=true; return;}						// P2WPKH Pay To Witness Public Key Hash (bech32 Adressen)	
		if(calc_RETURN()) 	{io=true; return;}						// OP_RETURN NULL-Daten die nicht ausgegeben werden können	
		name = "unknown Pk.script";  io = false;
	} 
	catch (Exception e) {name = "unknown Pk.script";}	
}



//------------------------------------------- Public Methoden ------------------------------------------//

/**	Gibt den Namen des PK.Scriptes zurück (z.B: P2PK)
	Wird das Script nicht erkannt, wird "unknown Pk.script" zurück gegeben. **/
public String getName()
{
	return name;
}



/**	Gibt den Namen des PK.Scriptes als Nummer zurück:
P2PK   Pay-to-Public-Key		= 1                         
P2PKH  Pay-to-Public-Key-Hash	= 2                       
P2SH   Pay-to-Script-Hash 		= 3                 
P2WPKH (bech32 Adressen) 		= 4
OP_RETURN 						= 5
Wird das Script nicht erkannt 	= -1 
Dies ist im Grunde das gleiche wie getName() nur das die Nummer als Int besser und schneller zu verarbeiten ist. **/
public int getNr()
{
	return nr;
}



/**	Gibt den Hash160 als 20 ByteArray aus dem PK.Script zurück, wenn es decodiert werden konnte.
	@throws Exception Wenn das Script nicht decodiert werden kann, wird "Unbekanntes PK.Script Exception!" ausgelöst.  **/ 
public byte[] getHash160() throws Exception
{
	if(io==true && h160.length==20) return h160;
	else throw new Exception("PK.Script could not be decoded. Unknown PK.Script!");
}



/**	Gibt die Bitcoin Adresse aus dem PK.Script zurück, wenn sie decodiert werden konnte.
	Wenn das Pk.Script nicht decodiert werden konnte, wird "unknown Pk.script" zurück gegeben.  
	@param magic Der Magic Wert des Netzwerkes muss übergeben werden.**/
public String getBitcoinAddress(byte[] magic) 
{
	try
	{		
		if(name.equals("RETURN")) 																	// OP_RETURN
		{ 
			String str =  "OP_RETURN Data:   " + new String(Calc.parseCompactSize(pk, 1),"UTF-8"); 
			return str;
		}				
		if(name.equals("P2WPKH"))																	// bech32 Adresse
		{
			
			String na;
			if(Arrays.equals(magic,MAINNET)) na = "bc";
			else na = "tb";			
			return Bech32Address.segwitToBech32(na, 0, getHash160());
		}
		else																						// alte Adressen
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





//------------------------------------------- Private Methoden ------------------------------------------//


// Wählt das richtige Adressprefix aus, für TestNet oder MainNet und die verschiedenen Scrpit-Typen
private byte calcPreFix(byte[] magic)
{
	if(Arrays.equals(magic,MAINNET))
	{
		if(name.equals("P2SH")) return  0x05;
		else					return  0x00;
	}		
	if(Arrays.equals(magic,TESTNET3))
	{
		if(name.equals("P2SH")) return  (byte) 0xc4;
		else					return  0x6f;
	}	
	return 0x00;
}



// Berechnet den Hash160 aus typ P2PK (Pay-to-Public-Key)
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
			nr	 = 1;
			return true;
		}
		else return false;	
	}
	catch (Exception e){return false;}
}



// Parst den Hash160 aus dem P2PKH (Pay-to-Public-Key-Hash)
private boolean calc_P2PKH()
{
	if(pk[0]==(byte)0x76 && pk[1]==(byte)0xa9 && pk[2]==(byte)0x14 && pk[23]==(byte)0x88 && pk[24]==(byte)0xac && pk.length==25)
	{	
		h160 = new byte[20];
		System.arraycopy(pk, 3, h160, 0, 20);
		name = "P2PKH";
		nr	 = 2;
		return true;
	}
	else return false;	
}



// Parst den Hash160 aus dem P2SH (Pay-to-Script-Hash)
private boolean calc_P2SH()
{
	if(pk[0]==(byte)0xa9 && pk[1]==(byte)0x14 && pk[22]==(byte)0x87 && pk.length==23)
	{	
		h160 = new byte[20];
		System.arraycopy(pk, 2, h160, 0, 20);
		name = "P2SH";
		nr	 = 3;
		return true;
	}
	else return false;	
}



// Parst den Hash160 aus dem P2WPKH (Pay-To-Witness-Public-Key-Hash)
private boolean calc_P2WPKH()
{
	if(pk[0]==(byte)0x00 && pk[1]==(byte)0x14 && pk.length==22)
	{	
		h160 = new byte[20];
		System.arraycopy(pk, 2, h160, 0, 20);
		name = "P2WPKH";
		nr	 = 4;
		return true;
	}
	else return false;	
}



// Parst den OP_RETURN Daten
private boolean calc_RETURN()
{
	if(pk[0]==(byte)0x6a)
	{	
		name = "RETURN";
		nr	 = 5;
		return true;
	}
	else return false;	
}
}