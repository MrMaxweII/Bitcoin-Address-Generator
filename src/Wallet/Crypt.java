package Wallet;
import java.security.SecureRandom;
import java.util.Arrays;
import lib3001.crypt.AES256;
import lib3001.crypt.Calc;
import lib3001.crypt.ScryptHash;
import lib3001.crypt.Twofish;



/***************************************************************************************************************
*	Version 1.2    						Autor: Mr. Maxwell   						vom 19.09.2023				*
*																												*
*	Letzte Änderung: (v1.2)																						*
*	Abhängikeit zu Bouncycastle entfernt!																		*
*	ParanoidCrypt wurde (ab KeyPass-Verion: 1.2.x) ohne Bouncycastle-Lib programmiert. 							*
*	Die verwendeten Klassen für AES, Twofish, Scrypt und SHA256 wurden in BTClib3001 hinzugefügt.				*
*																												*
*	Statische Klasse die verschiedene Methoden zum Verschlüsseln von Daten zur verfügung stellt.				*
*	Hauptmethoden sind paranoidEncrypt() und paranoidDecrypt() die zur verschlüsselung verwendet werden.		*
*	Siehe Blockdiagramm "ParanoidCrypt.png"																		*
*	Wird verwendet um die CAGWallet.dat	vom CoinAddressGernerator mit maximaler Sichereit zu verschlüsseln		*
*	Achtung! Diese Klasse ist explizip für den CoinAddressGen. konzipirt. die Parameter in getScryptHash unterscheiden sich von anderne Versionen!
****************************************************************************************************************/



public class Crypt 
{
	


/**	Verschlüsselt Daten mit zwei vierschiedenen Verschlüsselungsmethoden hintereinander, AES & Twofish.
	Siehe Blockdiagramm "ParanoidCrypt.png
	- padding16() hängt Leerzeichen an den Klartext an, damit die Länge durch 16 Teilbar ist.
	- addSHA256Checksum() fügt 32Byte SHA256 Hash vorne ein, um später prüfen zu können
	- KeyDuplication erstellt zwei isolierte Keys für die Verschlüsselung
	- mit Scrypt-Hash wird eine Verzögerung von ca.3 Sec. eingebaut und ein GB-RAM verwendet.
	- Zufalls-Salz, wird in AES & Twofish intern angewendet
	@param text Klartext der verschlüsselt werden soll als ByteArray.
	@param masterKey Der Schlüssel der zur Ver/End-schlüsselung verwendet wird als ByteArray
	@return Die verschlüsselte Chiffre als ByteArray.  **/
public static byte[] paranoidEncrypt(byte[] text, byte[] masterKey) throws Exception
{
	text = padding16(text);
	text = addSHA256Checksum(text);
	byte[][] keys = keyDuplication(masterKey,2);
	byte[] ch = encrypt(text, keys[0] , "AES");
	return encrypt(ch, getScryptHash(keys[1]), "Twofish");		
}
	


/**	Entschlüsselt Chiffre mit zwei vierschiedenen Endschlüsselungsmethoden hintereinander, Twofish & AES
	Siehe Blockdiagramm "ParanoidCrypt.png
	- KeyDuplication erstellt zwei isolierte Keys für die Ver/End-schlüsselung
	- mit Scrypt-Hash wird eine Verzögerung von ca.3 Sec. eingebaut und 1GB-RAM verwendet
	- Zufalls-Salz, wird in AES & Twofish intern angewendet
	@param chiffre Daten die entschlüsselt werden sollen als ByteArray.
	@param masterKey Der Schlüssel der zur Ver/End-schlüsselung verwendet wird als ByteArray
	@return Entschlüsselter Klar Text als ByteArray.  **/
public static byte[] paranoidDecrypt(byte[] chiffre, byte[] masterKey) throws Exception
{
	byte[][] keys = keyDuplication(masterKey,2);
	byte[] ch =  decrypt(chiffre, getScryptHash(keys[1]), "Twofish");		
	return decrypt(ch, keys[0] , "AES");
}



	
/**	Generiert eine beliebige Menge isolierter (SHA256) Schlüsseln aus einem Master-Key.
	Die Ausgegebenen Schlüssel werden deterministisch vom "masterKey" erzeugt mit der Eigenschaft,
	dass alle erzeugten Keys frei von Informationen der anderen Keys oder dem "masterKey" sind.	
	@param masterKey als Byte-Array 
	@param count die Anzahl der SHA256 Schlüssel die erzeugt werden sollen. 
	@return Eine Menge von Schlüsseln mit einer Länge von 32Byte. Die Anzahl der Schlüssel hängt von "count" ab. **/
public static byte[][] keyDuplication(byte[] masterKey, int count)
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
	
	

/**	@param text Der Text der verschlüsselt werden soll als ByteArray
@param key Der Schlüssel zur Verschlüsselung als ByteArray (32Byte)
@param name Der Name des Verschlüsselungsalgorythmus. Getestet ist: "aes", "twofish"
@return Gibt die Verschlüsselte Chiffre als ByteArray zurück. **/	
public static byte[] encrypt(byte[] text, byte[] key, String name) throws Exception
{	
	byte[] iv = new byte[16];
	SecureRandom.getInstanceStrong().nextBytes(iv);
	byte[] ch=null;
	if(name.equals("AES")) 		ch = AES256.encryptCBC(text, key, iv);	
	if(name.equals("Twofish"))	ch = Twofish.encrypt(text, key, iv);
	if(ch==null) throw new Exception("Crypt Name unknown");
	byte[] out = Arrays.copyOf(iv, ch.length+16);
	System.arraycopy(ch, 0, out, 16, ch.length);
	return out;
}



/**	@param chiffre Chiffre die entschlüsselt werden soll als ByteArray
@param key Der Schlüssel zur ver/entschlüsselung als ByteArray (32Byte)
@param name Der Name des Verschlüsselungsalgorithmus. Getestet ist: "aes", "twofish"
@return Gibt den entschlüsselten Klartext als ByteArray zurück. **/	
public static byte[] decrypt(byte[] chiffre, byte[] key, String name) throws Exception
{ 
	byte[] iv = Arrays.copyOf(chiffre, 16);
	chiffre = Arrays.copyOfRange(chiffre, 16, chiffre.length);
	if(name.equals("AES"))		return AES256.decryptCBC(chiffre, key, iv);
	if(name.equals("Twofish"))	return Twofish.decrypt(chiffre, key, iv);
	throw new Exception("Crypt Name unknown");
}




/**	Entfernt den vorranstehenden SHA256 Hash von "data" und gibt die Daten getrennt zurück.
@param data Daten mit vorranstehenden SHA256 Hash
@return Es werden 3 ByteArrays zurück gegeben:
Array[0]: der Vorrangestellen 32Byte SHA256 Hash
Array[1]: Die eigentlichen Daten ohne den SHA256 Hash
Array[2]: ByteArray mit einem einzigem Byte! Dieses Byte kann folgende zustände haben:
0x00 = Fehler, der SHA256Hash stimmt nicht mit dem errechneten SHA256 Hash der Nutzdaten überein.
0x01 = Korrekt, der SHA256Hash stimmt mit den errechneten SHA256 Hash überein.  **/ 
public static byte[][] removeAndCheckSHA256Checksum(byte[] data)
{
	byte[][] out = new byte[3][];
	out[0] = new byte[32];
	out[1] = new byte[data.length-32];
	out[2] = new byte[1];	
	System.arraycopy(data, 0, out[0], 0, 32);
	System.arraycopy(data, 32, out[1], 0, data.length-32);
	byte[] hash = Calc.getHashSHA256(out[1]);
	if(Arrays.equals(out[0], hash)) out[2][0] = 1;
	else 							out[2][0] = 0;	
	return out;
}





//----------------------------------------------------- Private Methoden -------------------------------------------------------------------



/**	Generiert einen Scrypt-Hash, der zur Laufzeitverlängerung geeignet ist.
	Achtung hierfür wird eine eigener Thread gestartet!
	Eingestellt sind Parameter die 1GB Ram verbrauchen und ca 5 Sec. Rechenzeit benötigen.
	@param data Die Daten die gehasht werden sollen als Byte-Array.
	@return Gibt den Scrypt-Hash als Byte-Array zurück.  **/
private static byte[] getScryptHash(byte[] data) throws Exception
{
	final int p			= 1;		// Parallelisierungsparameter. ist immer 1 
	final byte[] salt 	= {0x00};   // Salz wird hier nicht benötigt.
	final int memory 	= 524288;	// (N) CPU/Speicherkostenparameter in Byte. Muss größer als 1 sein und eine Potenz von 2 und kleiner als 2^(128*r/8)  Standart = 2^14, Hier verwendet: 2^19
	final int r 		= 16;		// Die Blockgröße muss >= 1 sein. Hier wird 16 verwendet;
	final int outLen 	= 32;		// Die Länge des Ausgabe Hash.
	return ScryptHash.getHash(data, salt, memory, r, p, outLen);
}



/**	Hängt einen SHA256 Hash von "data" vorne an "data" an. 
	"Vorne" weil die hinten angehängten Leerzeichen des Paddings das hintere Parsen erschwerden würden. 
	Wird in der Verschlüsselung verwendet. Dieser Hash, wird dem Klartext vorne angehängt,
	um später bei der entschlüsselung festzustellen ob korrekt entschlüsselt wurde. **/
private static byte[] addSHA256Checksum(byte[] data)
{
	byte[] hash = Calc.getHashSHA256(data);
	byte[] out  = Arrays.copyOf(hash, data.length+32);	
	System.arraycopy(data, 0, out, 32, data.length);
	return out;
}



// Das übergebene ByteArray wird hinten mit Nullen aufgefüllt wenn die Länge nicht durch 16 ohne Rest Teilbar ist.
private static byte[] padding16(byte[] input)
{
	int m = 16 - input.length%16;
	if(m==16) m=0;
	return Arrays.copyOf(input, input.length+m);
}
}