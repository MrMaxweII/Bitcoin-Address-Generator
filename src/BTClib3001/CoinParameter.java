package BTClib3001;
import java.util.Arrays;



/***********************************************************************************************************
*																											*
*	V1.4 	Autor: Mr. Maxwell		Bip44 und Bech32 hinzugefügt					vom 15.02.2020			*
*	Klasse die die Coin-Prameter mit dem  MAGIC Wert verwaltet,	der das Netzwerk repräsentiert.				*
*	Alle MAGIC-Werte gibt es hier: 	https://github.com/dan-da/CoinParameter/blob/master/coinnetworking.md	*
*	Diese Classe Gehört zur BTClib3001 und muss algemein und kompatibel gehalten werden!					*
*	Achtung, alle Methodenaufrufe sind sehr resourcenfressend, 												*
*	weil die ganze Liste aller Coins durchsucht wird. nicht in Schleifen verwenden!							*
* 	Beispiel: 		CoinParameter c = CoinParameter.getFromSymbol("BTC"); 									*
*					String str = c.name;																	*
************************************************************************************************************/



public class CoinParameter 
{
	private static CoinParameter[] coinParameter = new CoinParameter[691];
	public String symbol;
	public String name;
	public byte[] p2pPort;
	public byte[] rpcPort;
	public byte[] dnsSeed;
	public byte[] magic = new byte[4];
	public byte[] genesisBlockHash;
	public byte[] pref_PrivKey;
	public byte[] pref_PubKey;
	public byte[] pref_P2SH;
	public byte[] bip44;
	public String bech32;
	
	
// Konstruktor
private CoinParameter(String symbol, String name, String p2pPort, String rpcPort, String dnsSeed, String magic, String genesisBlockHash, String pref_PrivKey, String pref_PubKey, String pref_P2SH, String bip44, String bech32)
{
											this.symbol           =	 symbol;
											this.name             =	 name;
	if(p2pPort.equals("")==false)			this.p2pPort          =	 Convert.hexStringToByteArray_oddLength(Integer.toHexString(Integer.valueOf(p2pPort)));
	if(rpcPort.equals("")==false)			this.rpcPort          =	 Convert.hexStringToByteArray_oddLength(Integer.toHexString(Integer.valueOf(rpcPort)));
	if(dnsSeed.equals("")==false)			this.dnsSeed          =	 Convert.hexStringToByteArray_oddLength(Integer.toHexString(Integer.valueOf(dnsSeed)));
	if(magic.equals("")==false)				this.magic            =	 Convert.hexStringToByteArray(magic);
	if(genesisBlockHash.equals("")==false)	this.genesisBlockHash =	 Convert.hexStringToByteArray(genesisBlockHash);
	if(pref_PrivKey.equals("")==false)		this.pref_PrivKey     =	 Convert.hexStringToByteArray_oddLength(Integer.toHexString(Integer.valueOf(pref_PrivKey)));
	if(pref_PubKey.equals("")==false)		this.pref_PubKey      =	 Convert.hexStringToByteArray_oddLength(Integer.toHexString(Integer.valueOf(pref_PubKey)));
	if(pref_P2SH.equals("")==false)			this.pref_P2SH        =	 Convert.hexStringToByteArray_oddLength(Integer.toHexString(Integer.valueOf(pref_P2SH)));	
	if(bip44.equals("")==false)				this.bip44        	  =	 Convert.hexStringToByteArray_oddLength(Integer.toHexString(Integer.valueOf(bip44)));
											this.bech32        	  =	 bech32;	
}
	
	
	
/**	@param symbol Übergeben wird der Symbol-String z.B. "BTC" 
	@return Gibt die Coin-Parameter zurück. Achtung: Das symbol ist nicht eindeutig, 
	es kann mehrere Coins mit dem selben Symbol geben. Der erste in der Liste wird zurück gegeben.**/
public static CoinParameter getFromSymbol(String symbol) 
{
	set();
	if(symbol==null || symbol.equals("")) 	{throw new IllegalArgumentException("Error! NULL was entered!");}
	for(int i=0; i<coinParameter.length; i++)
	{
		if(symbol.equals(coinParameter[i].symbol))return coinParameter[i];
	}
	throw new IllegalArgumentException("Error! No coin found from: "+symbol);
}	
	
	

/**	@param name Übergeben wird der Name-String z.B. "Bitcoin-Mainnet" 
	@return Gibt die Coin-Parameter zurück. **/
public static CoinParameter getFromName(String name) 
{
	set();
	if(name==null || name.equals("")) 	{throw new IllegalArgumentException("Error! NULL was entered!");}
	for(int i=0; i<coinParameter.length; i++)
	{
		if(name.equals(coinParameter[i].name))    return coinParameter[i];
	}
	throw new IllegalArgumentException("Error! No coin found from: "+name);
}	



/**	@param magic Übergeben wird "MAGIC" als byte-Array 
	@return Gibt die Coin-Parameter zurück. Achtung: Es gibt viele Coins mit dem selben MAGIC Wert, der erste in der Liste wird zurück gegeben. **/
public static CoinParameter getFromMagic(byte[] magic) 
{
	set();
	if(magic==null) 	{throw new IllegalArgumentException("MAGIC Bytes Error! NULL was entered!");}
	if(magic.length!=4) {throw new IllegalArgumentException("MAGIC Bytes Error! MAGIC value does not contain exactly 4 bytes!");}	
	for(int i=0; i<coinParameter.length; i++)
	{
		if(Arrays.equals(coinParameter[i].magic, magic))    return coinParameter[i];
	}
   return null;
}



/**	@param blockHash Übergeben wird blockHash des Genesis Blockes als byte-Array. 
	@return Gibt die Coin-Parameter zurück. Achtung: Viele Coins haben den selben Genesis BlockHash von Bitcoin **/
public static CoinParameter getFromGenesisBlock(byte[] genesisBlockHash) 
{
	set();
	if(genesisBlockHash==null) 		{throw new IllegalArgumentException("NULL was entered!");}
	for(int i=0; i<coinParameter.length; i++)
	{
		if(Arrays.equals(coinParameter[i].genesisBlockHash, genesisBlockHash))    return coinParameter[i];
	}
	return null;
}



/**	@return Gibt die vollständige Liste aller CoinParameter als CoinParameter-Array zurück;	**/
public static CoinParameter[] getList()
{
	set();
	return coinParameter;
}



// Setzt die CoinParameter Liste. Muss in jeder getMethode zuerst aufgerufen werden!
//          https://github.com/dan-da/coinparams/blob/master/coinnetworking.md
private static void set()
{	
	//Achtung ArrayIndex nicht komplett sortiert!
	coinParameter[0]=new  CoinParameter("$PAC",		"PACcoin-Mainnet",				"7112",		"7111",		"4",	"c8e5612c",	"00000354655ff039a51273fe61d3b493bd2897fe6c16f732dbc4ae19f04b789e",	"204",	"55",	"10"	,"",	"");
	coinParameter[1]=new  CoinParameter("1337",		"Elite-Mainnet",				"13373",	"13372",	"4",	"5ac382d3",	"000004611c87517dfd29fe7f34bd6da2e1ad3d305ac12afe80a3229069390f68",	"176",	"48",	"28"	,"",	"");
	coinParameter[2]=new  CoinParameter("42",		"42-coin-Mainnet",				"4242",		"2121",		"0",	"1d05140b",	"000004cf6cc5eec2d2d564fa45c26278ed72014822a601c1ff02cd84d0ef63be",	"136",	"8",	"20"	,"",	"");
	coinParameter[3]=new  CoinParameter("ABJC",		"AbjcoinCommerce-Mainnet",		"69082",	"69082",	"0",	"325e6f86",	"000003082798cb529e90c7f9944dbf263007c349f6fd7d548f8eddcbfde3489d",	"151",	"23",	"85"	,"",	"");
	coinParameter[4]=new  CoinParameter("ACC",		"AdCoin-Mainnet",				"19499",	"19500",	"2",	"3d053577",	"6da2f2ef5b0f62c0717dbbd687826b0e9e6fe08cf0c61c8f40173efadd49a9f9",	"176",	"23",	"5"		,"161",	"");
	coinParameter[5]=new  CoinParameter("ACES",		"Aces-Mainnet",					"21274",	"21273",	"0",	"70352205",	"000005653beb1e6540e83b43c21127ec6ff15e73e57ffa1f0c87c023bb134c33",	"151",	"23",	"85"	,"",	"");
	coinParameter[6]=new  CoinParameter("ACP",		"AnarchistsPrime-Mainnet",		"11050",	"21050",	"1",	"f9beb4d9",	"000000008afd70438709390e1a1b4e64c81437ffb244d785b7d6029d7b1ac95e",	"128",	"0",	"5"		,"",	"");
	coinParameter[7]=new  CoinParameter("ADC",		"AudioCoin-Mainnet",			"25159",	"26159",	"1",	"faf4fbff",	"000003d9347a2ed183e9167c5e3d6932530886401bd7769b991c40e6962f85d6",	"151",	"23",	"125"	,"",	"");
	coinParameter[8]=new  CoinParameter("ADZ",		"Adzcoin-Mainnet",				"43029",	"43028",	"2",	"fdc3b9de",	"00000f27bd42306ee2f582232a7fce3815c32a1f950d6d7f52ad2bbc560c2e66",	"148",	"20",	"5"		,"",	"");
	coinParameter[9]=new  CoinParameter("AIB",		"AdvancedInternetBlocks-Mainnet","5223",	"0",		"3",	"0f68c6cb",	"" ,																"0"	 ,	"0",	"0"		,"55",  "");
	coinParameter[10]=new CoinParameter("ALL",		"Allion-Mainnet",				"12916",	"11916",	"14",	"a4a6fd05",	"0000007712af1202c5d79cc2db4389dfafee039f8ecd5b099f4c4443c2da61e6",	"154",	"26",	"8"	    ,"",    "");
	coinParameter[11]=new CoinParameter("ALQO",		"ALQO-Mainnet",					"55500",	"55000",	"2",	"94041514",	"000006680a85db563f3a849c77e02a8c860068474ab2c5495cc24a2204727b62",	"193",	"23",	"16"    ,"",    "");
	coinParameter[12]=new CoinParameter("AMMO",		"AmmoReloaded-Mainnet",			"21582",	"21583",	"1",	"c3c5a2a3",	"0000022b2e357af0d19792b48873442952ac8bf314a992bec0cf897b01e7da79",	"142",	"14",	"30"    ,"",    "");
	coinParameter[13]=new CoinParameter("AMS",		"AmsterdamCoin-Mainnet",		"50020",	"51020",	"6",	"002200aa",	"0000016c5074e3a3d7df0c9b9cdc38ef6a72be36c4a3a0233a564533120957f5",	"212",	"23",	"13"    ,"",    "");
	coinParameter[14]=new CoinParameter("ANI",		"Animecoin-Mainnet",			"1212",		"2121",		"0",	"414e494d",	"0000099acc274b7b403a828238bad69414e03a1a51b297a250c0a0da8a337840",	"151",	"23",	"9"	    ,"",    "");
	coinParameter[15]=new CoinParameter("ANTX",		"Antimatter-Mainnet",			"18595",	"18596",	"0",	"ad61f3ce",	"000009964453428b6f9ca4f20daa0691c3435f7572d53124579039dd33b1c299",	"203",	"75",	"85"    ,"",    "");
	coinParameter[16]=new CoinParameter("APR",		"APRCoin-Mainnet",				"3133",		"3132",		"0",	"a4ec922e",	"000003ae9a3767e1a412e8ac769a4e99a7a1ca3cf2848c9e5abe1ea2fef8a2fd",	"46",	"12",	"6"	    ,"",    "");
	coinParameter[17]=new CoinParameter("ARB",		"ARbit-Mainnet",				"31930",	"31931",	"0",	"e3a77c0e",	"00000d63e8cf1f6894a67eb143655fba6c6fda0cfb6c066e38acc908ca38162d",	"151",	"23",	"28"    ,"",    "");
	coinParameter[18]=new CoinParameter("ARC",		"AdvancedTechnologyCoin-Mainnet","7209",	"7208",		"0",	"3dd22861",	"00000f39e7fdff2d6025511f525bf1dce2f705c15d098d7f31c824a1785a254a",	"176",	"23",	"8"	    ,"",    "");
	coinParameter[19]=new CoinParameter("ARCO",		"AquariusCoin-Mainnet",			"6205",		"6206",		"10",	"933064c7",	"00000393a7de08ce23b3882ae7b5c1567e83bda4849ed24b52610a9b2541c6c9",	"151",	"23",	"5"	    ,"",    "");
	coinParameter[20]=new CoinParameter("ARG",		"Argentum-Mainnet",				"13580",	"13581",	"0",	"fbc1b8dc",	"88c667bc63167685e4e4da058fffdfe8e007e5abffd6855de52ad59df7bb0bb2",	"151",	"23",	"5"	    ,"45",  "");
	coinParameter[21]=new CoinParameter("ASAFE2",	"AllSafe-Mainnet",				"30234",	"30244",	"0",	"325e6f86",	"000002fb73d5d11d2ca0f931baecd9bdd912d1aa38c6f8e41376dd41ace32713",	"151",	"23",	"85"    ,"",    "");
	coinParameter[22]=new CoinParameter("ATC",		"Arbitracoin-Mainnet",			"32540",	"32640",	"0",	"ec221025",	"000002ebbd06d189fff6768e89abf3ea66ab837c706f061b26d97ff3d1a9e179",	"155",	"23",	"5"	    ,"",    "");
	coinParameter[23]=new CoinParameter("AU",		"AurumCoin-Mainnet",			"11080",	"21080",	"0",	"f9beb4d9",	"0000000040a867146dcc50bee85f69ac20addc33080c2a769c01200920251955",	"128",	"0",	"5"	    ,"",    "");
	coinParameter[24]=new CoinParameter("AUR",		"Auroracoin-Mainnet",			"12340",	"12341",	"8",	"fda4dc6c",	"2a8e100939494904af825b488596ddd536b3a96226ad02e0f7ab7ae472b27a8e",	"176",	"23",	"5"	    ,"85",  "");
	coinParameter[25]=new CoinParameter("AV",		"AvatarCoin-Mainnet",			"9712",		"9711",		"2",	"b23bf8a6",	"000002cd57a4b76ce5426f3af4f7ddce7d158d89eee3ce470b3a368daa30386f",	"151",	"23",	"41"    ,"",    "");
	coinParameter[26]=new CoinParameter("AXIOM",	"Axiom-Mainnet",				"15760",	"15770",	"1",	"033f1a0c",	"75687e926dd7611f320a99144869f1e281e275b306c634e285e780f1440a0064",	"153",	"23",	"85"    ,"",    "");
	coinParameter[27]=new CoinParameter("BAY",		"BitBay-Mainnet",				"19914",	"19915",	"0",	"70352205",	"0000075685d3be1f253ce777174b1594354e79954d2a32a6f77fe9cba00e6467",	"153",	"25",	"85"    ,"",    "");
	coinParameter[28]=new CoinParameter("BCA",		"BitcoinAtom-Mainnet",			"7333",		"7332",		"3",	"4fc11de8",	"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",	"128",	"23",	"10"    ,"185", "bca");
	coinParameter[29]=new CoinParameter("BCC",		"BitConnect-Mainnet",			"9239",		"9240",		"0",	"325e6f86",	"00000d3bd95c47fa17c47e1e2732d7072a6c4014a2fa93873124418a8fd9a300",	"146",	"18",	"85"    ,"",    "");
	coinParameter[30]=new CoinParameter("BCD",		"BitcoinDiamond-Mainnet",		"7117",		"7116",		"6",	"bddeb4d9",	"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",	"128",	"0",	"5"	    ,"999", "");
	coinParameter[31]=new CoinParameter("BCF",		"BitcoinFast-Mainnet",			"25671",	"25672",	"1",	"cefbfadb",	"0000056ae1a4b604b1f71b5436f3f22775429c03f29a9786e5e33fc104041b46",	"153",	"25",	"8"	    ,"",    "");
	coinParameter[32]=new CoinParameter("BCH",		"BitcoinCash-Mainnet",			"8333",		"8332",		"6",	"e3e1f3e8",	"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",	"128",	"0",	"5"	    ,"145", "");
	coinParameter[33]=new CoinParameter("BCI",		"BitcoinInterest-Mainnet",		"8331",		"8332",		"3",	"ede4fe26",	"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",	"128",	"102",	"23"    ,"",    "");
	coinParameter[34]=new CoinParameter("BCO",		"BridgeCoin-Mainnet",			"6333",		"6332",		"3",	"fbc0b6db",	"6515ba86f08c652e0eb53e1b789102d3499b59c08b8421582a822d55ac17ab6e",	"176",	"27",	"5"	    ,"162", "");
	coinParameter[35]=new CoinParameter("BENJI",	"BenjiRolls-Mainnet",			"1777",		"1776",		"0",	"fbc0b6db",	"4deb54e42ec54fc20c975c3ab122cdd9655bc90d3c64c99d851300ae7dd629ae",	"383",	"255",	"253"	,"",    "");
	coinParameter[36]=new CoinParameter("BERN",		"BERNcash-Mainnet",				"32020",	"32016",	"27",	"91116200",	"3a787ca8cd2114c31b9094d729e4d8485b23ce04e57b5528c18e98e31d17dead",	"153",	"25",	"85"	,"",    "");
	coinParameter[37]=new CoinParameter("BIO",		"BioCoin-Mainnet",				"24885",	"24889",	"5",	"b4f9e1a5",	"000009038d39a5ecfecf6a15cc7c49ff3c7bc0b23f8115902a9c0f3c9f72e9b9",	"153",	"25",	"20"	,"154", "");
	coinParameter[38]=new CoinParameter("BIOB",		"BioBar-Mainnet",				"37257",	"37258",	"0",	"a4d2f8a6",	"0000007dda5d2d1547f9247ff7863529cf12825cc866517534ac680b3a00b850",	"153",	"25",	"50"	,"",    "");
	coinParameter[39]=new CoinParameter("BIRDS",	"Birds-Mainnet",				"20013",	"20014",	"0",	"17f7fedf",	"000007620092fb7eeeb238e94604713d1de6f39c1feb5c84214a0acbbd943bc1",	"153",	"25",	"85"	,"",    "");
	coinParameter[40]=new CoinParameter("BITG",		"BitcoinGreen-Mainnet",			"9333",		"9332",		"3",	"54dc12ae",	"000008467c3a9c587533dea06ad9380cded3ed32f9742a6c0c1aebc21bf2bc9b",	"46",	"38",	"6"		,"222", "");
	coinParameter[41]=new CoinParameter("BITOK",	"Bitok-Mainnet",				"11122",	"11100",	"0",	"bca3fb5c",	"00000a8f7dc0e4df283e6192ceeec9dc5d10f8dc73f1a97c4a1618b4b0fa31d0",	"125",	"25",	"191"	,"",    "");
	coinParameter[42]=new CoinParameter("BITS",		"Bitstar-Mainnet",				"62123",	"62124",	"4",	"cef1dbfa",	"3bac216bbdf5b02ef8f8d43e9ee2fb28eef38e1e47bcb271ee81780445e2ca30",	"153",	"25",	"8"		,"",    "");
	coinParameter[43]=new CoinParameter("BLAZR",	"BlazerCoin-Mainnet",			"8213",		"8212",		"0",	"fbc0b6db",	"1040a19e0803e77f56662a6e6feb76c1edc690bbab90caf5fa36189e7ab6ba2c",	"153",	"25",	"125"	,"",    "");
	coinParameter[44]=new CoinParameter("BLK",		"BlackCoin-Mainnet",			"15714",	"15715",	"1",	"70352205",	"000001faef25dec4fbcf906e6242621df2c183bf232f263d0ba5b101911e4563",	"153",	"25",	"85"	,"10",  "");
	coinParameter[45]=new CoinParameter("BLOCK",	"Blocknet-Mainnet",				"41412",	"41414",	"0",	"a1a0a2a3",	"00000eb7919102da5a07dc90905651664e6ebf0811c28f06573b9a0fd84ab7b8",	"154",	"26",	"28"	,"328", "");
	coinParameter[46]=new CoinParameter("BLU",		"BlueCoin-Mainnet",				"27104",	"27105",	"0",	"f3f2aead",	"dff2c9d2f60f1b9cd955b64639000159b444f2df71a66bf455cee071653f8de2",	"176",	"26",	"28"	,"",    "");
	coinParameter[47]=new CoinParameter("BOAT",		"BOAT-Mainnet",					"33827",	"33828",	"4",	"1c3b1aa3",	"e428661f3bf5",														"153",	"25",	"50"	,"",    "");
	coinParameter[48]=new CoinParameter("BOLI",		"Bolivarcoin-Mainnet",			"3893",		"3563",		"0",	"fdc3b9de",	"00000e4fc293a1912b9d73cbb8d8f7270007a7d84382f1370661e65d5d57b1f6",	"213",	"85",	"5"		,"",    "");
	coinParameter[49]=new CoinParameter("BRO",		"Bitradio-Mainnet",				"32454",	"32455",	"3",	"d31a3de4",	"00000e54a9606e655b0093dff041d471fb46d913f979f0e79d4fafc944a214a9",	"128",	"26",	"102"	,"",    "");
	coinParameter[50]=new CoinParameter("BSC",		"BowsCoin-Mainnet",				"8155",		"8145",		"0",	"ffc4badf",	"00000a97cc51c61ef0212cd2113603c96c72ea9e1a30e72d15da1297890c64d3",	"153",	"25",	"5"		,"",    "");
	coinParameter[51]=new CoinParameter("BSD",		"BitSend-Mainnet",				"8886",		"8800",		"1",	"a3d5c2f9",	"0000012e1b8843ac9ce8c18603658eaf8895f99d3f5e7e1b7b1686f35e3c087a",	"204",	"102",	"5"		,"91",  "");
	coinParameter[52]=new CoinParameter("BSR",		"BitSoar-Mainnet",				"40119",	"50119",	"0",	"53746942",	"00000e7fd05283b85e11077f45e7b515df83a4af03b9ae2c5ad87427c1d49281",	"154",	"26",	"5"		,"",    "");
	coinParameter[53]=new CoinParameter("BSTY",		"GlobalBoost-Y-Mainnet",		"8226",		"8225",		"2",	"a2b2e2f2",	"2e28050194ad73f2405394d2f081361a23c2df8904ec7f026a018bbe148d5adf",	"205",	"77",	"139"	,"",    "");
	coinParameter[54]=new CoinParameter("BTA",		"Bata-Mainnet",					"5784",		"5493",		"2",	"34c3afeb",	"b4bee36fd54a6176fd832f462641415c142d50e4b378f71c041870c2b1186bc8",	"188",	"25",	"5"		,"89",  "");
	coinParameter[55]=new CoinParameter("BTB",		"BitBar-Mainnet",				"8777",		"9344",		"1",	"e4e8e9e5",	"00000b03a8fb08c5fc7c876f73c224a964ed79f71276694c3c0a6d288ae57fc3",	"153",	"25",	"30"	,"",    "");
	coinParameter[56]=new CoinParameter("BTC",		"Bitcoin-Mainnet",				"8333",		"8332",		"7",	"f9beb4d9",	"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",	"128",	"0",	"5"		,"0",   "bc");
	coinParameter[57]=new CoinParameter("BTCD",		"BitcoinDark-Mainnet",			"14631",	"14632",	"0",	"a4a2d8e7",	"0000044966f40703b516c5af180582d53f783bfd319bb045e2dc3e05ea695d46",	"188",	"60",	"85"	,"52",  "");
	coinParameter[58]=new CoinParameter("BTCP",		"BitcoinPrivate-Mainnet",		"7933",		"7932",		"2",	"a8eaa2cd",	"0007104ccda289427919efc39dc9e4d499804b7bebc22df55f8b834301260602",	"128",	"4901",	"5039"	,"183", "");
	coinParameter[59]=new CoinParameter("BTCR",		"Bitcurrency-Mainnet",			"15814",	"15815",	"3",	"70421309",	"000008764d5c635c287151cabaa066526f6aac5ac885b46f4183f8c1b7c357d4",	"153",	"25",	"8"		,"",    "");
	coinParameter[60]=new CoinParameter("BTCZ",		"BitcoinZ-Mainnet",				"1989",		"1979",		"6",	"24e92764",	"f499ee3d498b4298ac6a64205b8addb7c43197e2a660229be65db8a4534d75c1",	"128",	"7352",	"7357"	,"177", "");
	coinParameter[61]=new CoinParameter("BTDX",		"Bitcloud-Mainnet",				"8329",		"8330",		"1",	"e4e8bdfd",	"000002d56463941c20eae5cb474cc805b646515d18bc7dc222a0885b206eadb0",	"153",	"25",	"5"		,"218", "");
	coinParameter[62]=new CoinParameter("BTG",		"BitcoinGold-Mainnet",			"8338",		"8332",		"3",	"e1476d44",	"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",	"128",	"38",	"23"	,"156", "");
	coinParameter[63]=new CoinParameter("BTGEM",	"Bitgem-Mainnet",				"7692",		"8348",		"1",	"e4e8e9e5",	"00000b91c4ef8fd7e86329e11cff7f03c96208dac5c057b3fa6877b9fbd97c74",	"226",	"98",	"8"		,"",    "");
	coinParameter[64]=new CoinParameter("BTX",		"Bitcore-Mainnet",				"8555",		"8556",		"1",	"f9beb4d9",	"604148281e5c4b7f2487e5d03cd60d8e6f69411d613f6448034508cea52e9574",	"128",	"3",	"125"	,"160", "btx");
	coinParameter[65]=new CoinParameter("BUB",		"Bubble-Mainnet",				"15716",	"15719",	"0",	"cd2119dd",	"000002adb9be617747c9473829522ff84bd1ce5ed6ee943553a42ca64e9c2928",	"128",	"53",	"5"		,"",    "");
	coinParameter[66]=new CoinParameter("BUMBA",	"BumbaCoin-Mainnet",			"20222",	"10111",	"0",	"b1c0d2e3",	"00000e2781c9a29cbd75ee5a69394a950fc72276236d28e04f36d24034a293ae",	"153",	"25",	"28"	,"",    "");
	coinParameter[67]=new CoinParameter("BUZZ",		"BuzzCoin-Mainnet",				"25128",	"25129",	"0",	"07358205",	"00000ed9b0a3e9ac0424573f83b2c36c89db5d03d69bc95b15fe4df990ae478b",	"153",	"25",	"85"	,"169", "");
	coinParameter[68]=new CoinParameter("BWK",		"Bulwark-Mainnet",				"52543",	"52541",	"11",	"08020117",	"0000068e7ab8e264f6759d2d81b29e8b917c10b04db47a9a0bb3cba3fba5d574",	"212",	"85",	"18"	,"",    "");
	coinParameter[69]=new CoinParameter("CANN",		"CannabisCoin-Mainnet",			"39348",	"39347",	"2",	"fec3b9de",	"00000a10f7ce671e773330376ce892a6c0b93fbc05553ebbf659b11e3bf9188d",	"156",	"28",	"5"		,"",    "");
	coinParameter[70]=new CoinParameter("CARBON",	"Carboncoin-Mainnet",			"9350",		"9351",		"2",	"abccbbdf",	"a94f1aae8c409a0bd1e53cbca92d7e506b61c51d955cf56f76da501718d48d6c",	"175",	"47",	"5"	    ,"",    "");
	coinParameter[71]=new CoinParameter("CAT",		"Catcoin-Mainnet",				"9933",		"9932",		"2",	"fcc1b7dc",	"bc3b4ec43c4ebb2fef49e6240812549e61ffa623d9418608aa90eaad26c96296",	"149",	"21",	"88"    ,"",    "");
	coinParameter[72]=new CoinParameter("CAZ",		"Cazcoin-Mainnet",				"17350",	"17358",	"10",	"3da232b4",	"000008fef3a083a36208f4bcf73811d225bf70c0c25fced32b0e457ca75eab59",	"144",	"28",	"43"    ,"",    "");
	coinParameter[73]=new CoinParameter("CBX",		"Bullion-Mainnet",				"7695",		"8395",		"1",	"e4e8e9e5",	"000002655a721555160ea9bcb1072faf63ff0c8dffed173d60b1d3556a134dc1",	"0",	"11",	"8"	    ,"",    "");
	coinParameter[74]=new CoinParameter("CHAN",		"ChanCoin-Mainnet",				"19117",	"19118",	"5",	"0f9154f8",	"72c18e80787d961e92bc4bd508dbe7d7c5189794d449a6a58853f4e032b4831c",	"156",	"28",	"5"	    ,"",    "");
	coinParameter[75]=new CoinParameter("CHEAP",	"Cheapcoin-Mainnet",			"36648",	"36645",	"0",	"f32da571",	"062456fe2c6e55ccfe2bf0f84d91cf3bbafbcc5d101d8763129cd278f784951b",	"173",	"45",	"142"	,"",    "");
	coinParameter[76]=new CoinParameter("CHESS",	"ChessCoin-Mainnet",			"7323",		"7324",		"1",	"de3aae3c",	"0000048f94311e912681a9a25eb553e4a4d1703689c5f9a264c7b07245c7ff1f",	"156",	"28",	"85"    ,"",    "");
	coinParameter[77]=new CoinParameter("CHIPS",	"CHIPS-Mainnet",				"57777",	"57776",	"6",	"ffeeddcc",	"0000006e75f6aa0efdbf7db03132aa4e4d0c84951537a6f5a7c39a0a9d30e1e7",	"188",	"60",	"85"    ,"",    "");
	coinParameter[78]=new CoinParameter("CJ",		"Cryptojacks-Mainnet",			"33433",	"33533",	"7",	"5ac382d3",	"000004611c87517dfd29fe7f34bd6da2e1ad3d305ac12afe80a3229069390f68",	"156",	"28",	"85"    ,"",    "");
	coinParameter[79]=new CoinParameter("CLAM",		"Clams-Mainnet",				"31174",	"30174",	"1",	"03223515",	"00000c3ce6b3d823a35224a39798eca9ad889966aeb5a9da7b960ffb9869db35",	"133",	"137",	"13"    ,"23",  "");
	coinParameter[80]=new CoinParameter("CLUB",		"ClubCoin-Mainnet",				"18114",	"19114",	"5",	"70354205",	"000001768b08da66b92dede0ea8e7dcb97424f93d7ac2ac59e7a6cf98f20615a",	"153",	"28",	"85"    ,"79",  "");
	coinParameter[81]=new CoinParameter("CMPCO",	"CampusCoin-Mainnet",			"28195",	"28199",	"0",	"d7cfa4e6",	"2cadd7467aa4ca55a8be084aa8779da4f250981cbd7a7e377a4502bb58e5578b",	"156",	"28",	"5"		,"",    "");
	coinParameter[82]=new CoinParameter("CNNC",		"Cannation-Mainnet",			"12367",	"12368",	"0",	"52568b9e",	"00000fc248c48d26e8777d9531d501d5ee1ec4d51da539fc4c2ecfff706418be",	"156",	"28",	"85"	,"",    "");
	coinParameter[83]=new CoinParameter("CNX",		"Cryptonex-Mainnet",			"20863",	"20864",	"7",	"18e482a0",	"00000e20f302fba484374f33aedf9cd4a20073de7e7ac4bdfe473e060c2d11b3",	"166",	"38",	"85"	,"",    "");
	coinParameter[84]=new CoinParameter("COLX",		"ColossusXT-Mainnet",			"51572",	"51473",	"3",	"91c5feea",	"a0ce8206c908357008c1b9a8ba2813aff0989ca7f72d62b14e652c55f02b4f5c",	"212",	"30",	"13"	,"",    "");
	coinParameter[85]=new CoinParameter("CON",		"PayCon-Mainnet",				"9455",		"9456",		"2",	"4b3c3b2d",	"00000174b4ac95cc6334f5fe62e951dda9fcf930601df174f2940d990aa1a563",	"183",	"55",	"8"		,"",    "");
	coinParameter[86]=new CoinParameter("CPC",		"Capricoin-Mainnet",			"22714",	"22713",	"12",	"a1a0a2a3",	"00000d23fa0fc52c90893adb1181c9ddffb6c797a3e41864b9a23aa2f2981fe3",	"156",	"28",	"35"	,"",    "");
	coinParameter[87]=new CoinParameter("CRAVE",	"Crave-Mainnet",				"48882",	"48883",	"10",	"564f4944",	"7084c9af2c34a1179522d71ceaef27d87855365793ed9c9dd47ff4f8721462c1",	"153",	"70",	"85"	,"186", "");
	coinParameter[88]=new CoinParameter("CRC",		"CrowdCoin-Mainnet",			"12875",	"9998",		"7",	"1a4a5aaa",	"000007db550074c6535ce41c2a6043d0afbc86f17f1762b06e2cd65d100f7b5f",	"0",	"28",	"88"	,"",    "");
	coinParameter[89]=new CoinParameter("CRM",		"Cream-Mainnet",				"45066",	"45077",	"0",	"22e4c314",	"00000064f9440ba747396d1686dc56a2be9b215245c4ba7974913d65aa8f6dc1",	"155",	"28",	"5"		,"",    "");
	coinParameter[90]=new CoinParameter("CROP",		"Cropcoin-Mainnet",				"17720",	"17721",	"10",	"11c3b1de",	"0000074d7678e79efb00bbdef7a4724403823110f28ef8263b879f39b8d372b7",	"153",	"87",	"85"	,"",    "");
	coinParameter[91]=new CoinParameter("CRW",		"Crown-Mainnet",				"9340",		"9341",		"8",	"b8ebb3df",	"0000000085370d5e122f64f4ab19c68614ff3df78c8d13cb814fd7e69a1dc6da",	"128",	"0",	"28"	,"72",  "");
	coinParameter[92]=new CoinParameter("CTO",		"Crypto-Mainnet",				"8899",		"8898",		"0",	"a2c4b4da",	"1471f84b77a71ff798a39012823edd7d91b11ad7f2b90263a6bbbbe12c4d03fb",	"156",	"28",	"5"		,"",    "");
	coinParameter[93]=new CoinParameter("CURE",		"Curecoin-Mainnet",				"9911",		"19911",	"2",	"e4e8e9e5",	"00000ce427729d5393dbf9f464e7a1d2c039e393e881f93448516b1530b688fc",	"153",	"25",	"30"	,"",    "");
	coinParameter[94]=new CoinParameter("CYDER",	"Cyder-Mainnet",				"48848",	"48845",	"0",	"f32da571",	"09cf0e681409d41bbc337834790ea4846c1872fb8ae05855b32c2ffb95ccbc2b",	"148",	"20",	"142"	,"",    "");
	coinParameter[95]=new CoinParameter("DASH",		"Dash-Mainnet",					"9999",		"9998",		"4",	"bf0c6bbd",	"00000ffd590b1485b3caadc19b22e6379c733355108f107a430458cdf3407ab6",	"204",	"76",	"16"	,"5",   "");
	coinParameter[96]=new CoinParameter("DCR",		"Decred-Mainnet",				"9108",		"9109",		"4",	"f900b4d9",	"298e5cc3d985bfe7f81dc135f360abe089edd4396b86d2de66b0cef42b21d980",	"8926",	"1855",	"1818"	,"42",  "");
	coinParameter[97]=new CoinParameter("DEM",		"DeutscheeMark-Mainnet",		"5556",		"6666",		"1",	"e4e8e9e5",	"00000ac7a13fffb72c10f6fd9a773dcf3e8388b8ffc359cee2483b1236ebcda1",	"181",	"53",	"30"	,"",    "");
	coinParameter[98]=new CoinParameter("DEV",		"DeviantCoin-Mainnet",			"22618",	"22617",	"5",	"28b4adb8",	"00000e697a1e963f6ae8288419ffb0d59de9d232bac8efda8c6491d1c31e3139",	"137",	"90",	"10"	,"",    "");
	coinParameter[99]=new CoinParameter("DGB",		"DigiByte-Mainnet",				"12024",	"8332",		"7",	"fac3b6da",	"7497ea1b465eb39f1c8f507bc877078fe016d6fcb6dfad3a64c98dcc6e1e8496",	"128",	"30",	"5"		,"20",  "dgb");
	coinParameter[100]=new CoinParameter("DGC",		"Digitalcoin-Mainnet",			"7999",		"7998",		"2",	"fbc0b6db",	"5e039e1ca1dbf128973bf6cff98169e40a1b194c3b91463ab74956f413b2f9c8",	"128",	"30",	"5"		,"18",  "");
	coinParameter[101]=new CoinParameter("DIME",	"Dimecoin-Mainnet",				"11931",	"8372",		"0",	"fea503dd",	"00000d5a9113f87575c77eb5442845ff8a0014f6e79e2dd2317d88946ef910da",	"143",	"15",	"9"		,"",    "");
	coinParameter[102]=new CoinParameter("DLC",		"Dollarcoin-Mainnet",			"8145",		"8146",		"0",	"fc9b3d41",	"0000000013f91db600d56b852497767c013c2b5a4e62a182bd4a5ad6c93c732e",	"158",	"30",	"5"		,"102", "");
	coinParameter[103]=new CoinParameter("DMB",		"DigitalMoneyBits-Mainnet",		"35097",	"35098",	"0",	"f12ffeef",	"3f4ce71618777f85a62fcba786e80273b1bc0224a851d59f3ff086200c61f145",	"153",	"137",	"85"	,"",    "");
	coinParameter[104]=new CoinParameter("DMD",		"Diamond-Mainnet",				"17771",	"17770",	"1",	"e4e8bdfd",	"0000029b550c0095513d9bb9dd14f88442573baca94d70e49018a510979c0f9b",	"218",	"90",	"8"		,"152", "");
	coinParameter[105]=new CoinParameter("DNR",		"Denarius-Mainnet",				"33339",	"32339",	"9",	"faf2efb4",	"00000d5dbbda01621cfc16bbc1f9bf3264d641a5dbf0de89fd0182c2c4828fcd",	"158",	"30",	"90"    ,"116", "");
	coinParameter[106]=new CoinParameter("DOGE",	"Dogecoin-Mainnet",				"22556",	"22555",	"4",	"c0c0c0c0",	"1a91e3dace36e2be3bf030a65679fe821aa1d6ef92e7c9902eb318182c355691",	"158",	"30",	"22"    ,"3",   "");
	coinParameter[107]=new CoinParameter("DOLLAR",	"DollarOnline-Mainnet",			"22888",	"22882",	"10",	"a1a0a2a3",	"00000d69d6d9bb99b8d58438e238f58660c920b70b5e7f7ff9f68ea3835698fb",	"158",	"30",	"90"    ,"",    "");
	coinParameter[108]=new CoinParameter("DRXNE",	"DROXNE-Mainnet",				"41241",	"41242",	"0",	"b4fee4e5",	"00000c26d69561dab27af37a791a4e16e5f27012b8049f45d347ef090f52a2b8",	"159",	"31",	"20"    ,"",    "");
	coinParameter[109]=new CoinParameter("ECN",		"E-coin-Mainnet",				"18741",	"18742",	"1",	"b4f8e2e5",	"0000013ee9723e303e53c23690950cf8f35c43b69d75cac32c382ef47dbc6089",	"220",	"92",	"20"    ,"115", "");
	coinParameter[110]=new CoinParameter("EGC",		"EverGreenCoin-Mainnet",		"5757",		"5758",		"27",	"21246247",	"00000a02cefbc063ba09034a6fbc123f7062b7ee0e4eed9128a1cadc7533e388",	"161",	"33",	"85"    ,"",    "");
	coinParameter[111]=new CoinParameter("EGG",		"EggCoin-Mainnet",				"31104",	"31105",	"0",	"1a320320",	"00003d1bc115df47810ee300c9bf0b3943ee139d2863c384a3ac0d6dd07e3772",	"153",	"33",	"85"    ,"",    "");
	coinParameter[112]=new CoinParameter("EMB",		"EmberCoin-Mainnet",			"10024",	"10022",	"3",	"efa9657c",	"00000ff60784c4c8ceed0866d00de5742529ef3f1911e245f4126c5c293c88cd",	"50",	"92",	"110"	,"",    "");
	coinParameter[113]=new CoinParameter("EMC",		"Emercoin-Mainnet",				"6661",		"6662",		"4",	"e6e8e9e5",	"00000000bcccd459d036a588d1008fce8da3754b205736f32ddfd35350e84c2d",	"128",	"33",	"92"	,"",    "");
	coinParameter[114]=new CoinParameter("EMC2",	"Einsteinium-Mainnet",			"41878",	"41879",	"9",	"e8f1c4ac",	"4e56204bb7b8ac06f860ff1c845f03f984303b5b97eb7b42868f714611aed94b",	"176",	"33",	"5"		,"41",  "");
	coinParameter[115]=new CoinParameter("EMD",		"EmeraldCrypto-Mainnet",		"12127",	"12128",	"5",	"fbc0b6db",	"b297ecb2a87e6a306d0c2bbdbd0402cbb53368c74ac9ed2296c4a7815e599f1c",	"162",	"34",	"5"		,"",    "");
	coinParameter[116]=new CoinParameter("ENRG",	"Energycoin-Mainnet",			"22706",	"22705",	"0",	"fcd9b7dd",	"00000a5ac2dc57cfb0b92bc8be7731fe6a94a8c1c49a0d2f32e9e2da4f7d2308",	"220",	"92",	"8"		,"",    "");
	coinParameter[117]=new CoinParameter("ENT",		"Eternity-Mainnet",				"4855",		"4854",		"1",	"8ff74d2e",	"00000b602d00ef2d0466aa9156a905f59c6e5944a088782614a90e58089070c4",	"101",	"33",	"8"		,"",    "");
	coinParameter[118]=new CoinParameter("EQT",		"EquiTrader-Mainnet",			"43103",	"43102",	"6",	"b4f8e7a5",	"00000dddf1b7a90d3b3239094f8812699bdb348f873f0c29e68535717d5215b7",	"161",	"33",	"20"	,"",    "");
	coinParameter[119]=new CoinParameter("ERA",		"ERA-Mainnet",					"14442",	"14443",	"0",	"ea117acc",	"00000040cc36d5f0dc9054d8b002f1bf1a1fbd1ca48065770d7db3d512422bd4",	"154",	"26",	"5"		,"",    "");
	coinParameter[120]=new CoinParameter("ERC",		"EuropeCoin-Mainnet",			"8881",		"11989",	"0",	"4555524f",	"",																	"41",	"33",	"5"     ,"151", "");
	coinParameter[121]=new CoinParameter("ETH",		"Ethereum-Mainnet",				"30303",	"8545",		"0",	"",			"d4e56740f876aef8c010b86a40d5f56745a118d0906a34e69aec8c0db1cb8fa3",	"",		"",		""	    ,"60",  "");
	coinParameter[122]=new CoinParameter("EVIL",	"EvilCoin-Mainnet",				"20001",	"20002",	"0",	"a1a0a2a3",	"000008056bcc711afe7cdb1ff527cddfa981c7ef10883a9da0d5b1eb68a7a949",	"203",	"75",	"5"	    ,"",    "");
	coinParameter[123]=new CoinParameter("EXCL",	"ExclusiveCoin-Mainnet",		"23230",	"23231",	"1",	"a23d2ff3",	"000007156509515339b8d7ba056ca7c448a3c2be2eade1bc80440b3b4350bab5",	"161",	"33",	"137"   ,"190", "");
	coinParameter[124]=new CoinParameter("FAIR",	"FairCoin-Mainnet",				"46392",	"46393",	"2",	"e4e8e9e5",	"f1ae188b0c08e296e45980f9913f6ad2304ff02d5293538275bacdbcb05ef275",	"223",	"95",	"36"    ,"",    "");
	coinParameter[125]=new CoinParameter("FGC",		"FantasyGold-Mainnet",			"57810",	"57814",	"24",	"54494D42",	"000006b85859195cd62b57d137bba5871588d8f05cecc5fa21673e4c894e8997",	"212",	"35",	"18"    ,"",    "");
	coinParameter[126]=new CoinParameter("FJC",		"FujiCoin-Mainnet",				"3777",		"3776",		"2",	"66756a69",	"adb6d9cfd74075e7f91608add4bd2a2ea636f70856183086842667a1597714a0",	"164",	"36",	"16"    ,"75",  "fc");
	coinParameter[127]=new CoinParameter("FLO",		"FlorinCoin-Mainnet",			"7312",		"7313",		"5",	"fdc0a5f1",	"09c7781c9df90708e278c35d38ea5c9041d7ecfcdd1c56ba67274b7cff3e1cea",	"163",	"35",	"8"	    ,"",    "");
	coinParameter[128]=new CoinParameter("FLT",		"FlutterCoin-Mainnet",			"7408",		"7474",		"1",	"cfd1e8ea",	"00000e95eda26ced69bcda5d4794e153c54b2a7edb7752439a7c5d376238c7e0",	"163",	"35",	"20"    ,"",    "");
	coinParameter[129]=new CoinParameter("FRC",		"Freicoin-Mainnet",				"8639",		"8638",		"4",	"2cfe7e6d",	"000000005b1e3d23ecfd2dd4a6e1a35238aa0392c0a8528c40df52376d7efe2c",	"128",	"0",	"5"	    ,"",    "");
	coinParameter[130]=new CoinParameter("FREE",	"Freecoin-Mainnet",				"25880",	"21880",	"2",	"b8bca8a8",	"9ba33b76a1c80661bcc3a3ed1571d4fe54e448b42af5f2aa60c9db6b61c6e377",	"128",	"35",	"57"    ,"252", "");
	coinParameter[131]=new CoinParameter("FST",		"Fastcoin-Mainnet",				"9526",		"9527",		"5",	"fbc0b6db",	"ecba185817b726ef62e53afb14241a8095bd9613d2d3df679911029b83c98e5b",	"224",	"96",	"20"    ,"",    "");
	coinParameter[132]=new CoinParameter("FTC",		"Feathercoin-Mainnet",			"9336",		"9337",		"4",	"41151a21",	"12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2",	"142",	"14",	"5"	    ,"8",   "fc");
	coinParameter[133]=new CoinParameter("FTO",		"FuturoCoin-Mainnet",			"9009",		"9008",		"2",	"CFD2D4C6",	"00000bb3fa6e7040e32c0f8e8218e928c37e177139b62ae1daba803e0543c175",	"164",	"36",	"13"    ,"",    "");
	coinParameter[134]=new CoinParameter("FUNK",	"TheCypherfunks-Mainnet",		"33666",	"34666",	"1",	"fbc0b6db",	"cc22a327027dda63764cb25db7ab2349ad67ecb5a6bae0e1b629c9defab3bb14",	"176",	"28",	"5"	    ,"",    "");
	coinParameter[135]=new CoinParameter("GAIN",	"UGAIN-Mainnet",				"7891",		"7892",		"0",	"06bbe2e5",	"0000000031bc418b96e5bd9a47b9b1f786a02234475cbb472d50855a93a421d1",	"166",	"38",	"5"	    ,"",    "");
	coinParameter[136]=new CoinParameter("GAM",		"Gambit-Mainnet",				"47077",	"47177",	"3",	"f2f4f6f8",	"00000bcebb12f0ec7bd562d5257215afb6641ce8b9712e3cc4bb91f0cb7aa0f5",	"166",	"38",	"97"    ,"",    "");
	coinParameter[137]=new CoinParameter("GAME",	"GameCredits-Mainnet",			"40002",	"40001",	"1",	"fbc0b6db",	"91ec5f25ee9a0ffa1af7d4da4db9a552228dd2dc77cdb15b738be4e1f55f30ee",	"166",	"38",	"5"	    ,"101", "");
	coinParameter[138]=new CoinParameter("GB",		"GoldBlocks-Mainnet",			"27920",	"26920",	"7",	"60b6cdf4",	"000005fc99547a646f2273caa7a1f407ebd885ad89e0b019aa0a70c353598f38",	"155",	"38",	"85"    ,"94",  "");
	coinParameter[139]=new CoinParameter("GBX",		"GoByte-Mainnet",				"12455",	"12454",	"10",	"1ab2c3d4",	"0000033b01055cf8df90b01a14734cae92f7039b9b0e48887b4e33a469d7bc07",	"198",	"38",	"10"    ,"176", "");
	coinParameter[140]=new CoinParameter("GEERT",	"GeertCoin-Mainnet",			"64333",	"64332",	"1",	"f8d2e3bc",	"01e0ab8f22808260eae3e54c645d1dc137f3e06530b8232d4eee4eb6e4b02065",	"166",	"38",	"5"	    ,"",    "");
	coinParameter[141]=new CoinParameter("GIN",		"GINcoin-Mainnet",				"10111",	"10211",	"5",	"bf0c6bbd",	"00000cd6bde619b2c3b23ad2e384328a450a37fa28731debf748c3b17f91f97d",	"198",	"38",	"10"    ,"",    "");
	coinParameter[142]=new CoinParameter("GLD",		"GoldCoin-Mainnet",				"9333",		"9332",		"5",	"fbc0b6db",	"12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2",	"176",	"48",	"5"	    ,"",    "");
	coinParameter[143]=new CoinParameter("GLT",		"GlobalToken-Mainnet",			"9319",		"9320",		"1",	"c708d32d",	"00000000fe3e3e93344a6b73888137397413eb11f601b4231b5196390d24d3b6",	"166",	"38",	"5"	    ,"",    "gt");
	coinParameter[144]=new CoinParameter("GPL",		"GoldPressedLatinum-Mainnet",	"23635",	"23645",	"1",	"cdf2c0ef",	"00000164dc3880ea186076b3f54652254df73f30aa94cab098dd50b1831766ef",	"163",	"35",	"140"   ,"",    "");
	coinParameter[145]=new CoinParameter("GRC",		"GridCoin-Mainnet",				"32749",	"15715",	"6",	"70352205",	"000005a247b397eadfefa58e872bc967c2614797bdc8d4d0e6b09fea5c191599",	"190",	"62",	"85"    ,"84",  "");
	coinParameter[146]=new CoinParameter("GRIM",	"Grimcoin-Mainnet",				"24861",	"24862",	"2",	"e7420652",	"00000733223215fa8ba50ae21c4b0142b773ceb8c9c912375156efd51dd09823",	"166",	"38",	"85"    ,"",    "");
	coinParameter[147]=new CoinParameter("GRLC",	"Garlicoin-Mainnet",			"42069",	"42068",	"3",	"d2c6b6db",	"2ada80bf415a89358d697569c96eb98cdbf4c3b8878ac5722c01284492e27228",	"176",	"38",	"50"    ,"",    "grlc");
	coinParameter[148]=new CoinParameter("GRN",		"Granite-Mainnet",				"21777",	"21776",	"25",	"fec3b9de",	"000009950383ab1f400a783688346f9fc6d21962944e0e40a17b35dc904bd82a",	"166",	"38",	"5"	    ,"",    "");
	coinParameter[149]=new CoinParameter("GRS",		"Groestlcoin-Mainnet",			"8333",		"1441",		"6",	"f9beb4d9",	"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",	"128",	"0",	"5"	    ,"17",  "bc");
	coinParameter[150]=new CoinParameter("GSR",		"GeyserCoin-Mainnet",			"10556",	"10555",	"3",	"60341208",	"00000f8c28f5ed6b3097af0be5d00f8cc240e5715322ee332d05722b539c3019",	"171",	"38",	"63"    ,"",    "");
	coinParameter[151]=new CoinParameter("GTC",		"GlobalTourCoin-Mainnet",		"28111",	"28110",	"0",	"d4caafeb",	"7db09e449c2fcb04991f9fdcd13e59bbcdcfa56a84fc9353b1b555c1ff1688d3",	"139",	"11",	"5"	    ,"",    "");
	coinParameter[152]=new CoinParameter("HAL",		"Halcyon-Mainnet",				"21108",	"21109",	"2",	"a1a0a2a3",	"0000044449d51b7f537583006cf141e2d5a55a28d0149d75bebaaad724a2b405",	"168",	"40",	"28"    ,"",    "");
	coinParameter[153]=new CoinParameter("HALLO",	"HalloweenCoin-Mainnet",		"35727",	"35718",	"0",	"2cc34aa3",	"00000bef71c5ffeb052afd75f793fa4eb6648db14c124db90390b8c36c20be28",	"168",	"40",	"85"    ,"",    "");
	coinParameter[154]=new CoinParameter("HBN",		"HoboNickels-Mainnet",			"7372",		"7373",		"5",	"e4e8e9e5",	"000009ea5ef5019446b315e7e581fc2ea184315ed46c9ddeadc8aa9442deedc9",	"162",	"34",	"8"	    ,"",    "");
	coinParameter[155]=new CoinParameter("HC",		"HarvestMasternodeCoin-Mainnet","12116",	"12117",	"0",	"1d7ea62d",	"0000ebc8051bff80f7946f4420efb219e66f66b89fdc1df0ed8a30b428bf0033",	"153",	"40",	"85"    ,"",    "");
	coinParameter[156]=new CoinParameter("HOLD",	"InterstellarHoldings-Mainnet",	"10130",	"10131",	"2",	"902f3215",	"000001c7205cc2fb5b0fc12de43b7e6d2526eb5a61b1731650cbb5a570b87243",	"153",	"40",	"100"   ,"",    "");
	coinParameter[157]=new CoinParameter("HPC",		"Happycoin-Mainnet",			"12846",	"12847",	"5",	"b4f3efcc",	"00000d32d307ff91c27e15562e11fa914f62de3f6aa7b8f5d3f63ba0f3340437",	"169",	"41",	"20"    ,"",    "");
	coinParameter[158]=new CoinParameter("HTML",	"HTMLCOIN-Mainnet",				"4888",		"4889",		"4",	"1f2e3d4c",	"0000bf23c6424c270a24a17a3db723361c349e0f966d7b55a6bca4bfb2d951b0",	"169",	"41",	"100"   ,"172", "");
	coinParameter[159]=new CoinParameter("HVCO",	"HighVoltage-Mainnet",			"47824",	"47823",	"2",	"b1b6f8a6",	"000000e80102c0ce063a81d81683ae9ee15315e3237add9742dc14f84dcbd53a",	"168",	"40",	"85"    ,"",    "");
	coinParameter[160]=new CoinParameter("HWC",		"HollyWoodCoin-Mainnet",		"10267",	"11030",	"1",	"3b409d4f",	"00000418c55096b5ffaad25266162c24c8c51241cbdc16b32d53311f65dbbc38",	"168",	"40",	"85"    ,"",    "");
	coinParameter[161]=new CoinParameter("HXX",		"Hexx-Mainnet",					"29100",	"29200",	"5",	"68657878",	"322bad477efb4b33fa4b1f0b2861eaf543c61068da9898a95062fdb02ada486f",	"210",	"40",	"10"    ,"",    "");
	coinParameter[162]=new CoinParameter("HYP",		"HyperStake-Mainnet",			"18775",	"18777",	"2",	"dbadbdda",	"000005fe04e512585c3611369c7ce23f130958038c18a462577d002680dab4fc",	"245",	"117",	"8"	    ,"",    "");
	coinParameter[163]=new CoinParameter("HYPER",	"Hyper-Mainnet",				"11194",	"11195",	"0",	"cefbfadb",	"0000067c1bf612492b2200b253d3a523efd323ee627bddcfcacbfd7d11ef8775",	"104",	"100",	"101"   ,"",    "");
	coinParameter[164]=new CoinParameter("I0C",		"I0Coin-Mainnet",				"7333",		"7332",		"1",	"f1b2b3d4",	"00000000de13b7f748fb214e3f9c284fe6a57e1559fee545bfe473f72599c0d1",	"128",	"105",	"5"	    ,"",    "");
	coinParameter[165]=new CoinParameter("IBANK",	"iBank-Mainnet",				"7619",		"7620",		"0",	"f45490dc",	"00000966ef8bbe4f8b00bd3267ab8a42c51f5ec45e04f892bf52b7a39b67ea90",	"138",	"10",	"85"    ,"",    "");
	coinParameter[166]=new CoinParameter("ICON",	"Iconic-Mainnet",				"47426",	"47425",	"2",	"a3a5f8a6",	"0000009e50ab7f225ea50a98da6969717e714a6ef6089fea3fae3c77b366365a",	"230",	"102",	"85"    ,"",    "");
	coinParameter[167]=new CoinParameter("IFC",		"Infinitecoin-Mainnet",			"9321",		"9322",		"3",	"fbc0b6db",	"b10d5e83a5b2e62d9d872096bc20cae1a276ae6aacc02a71a5832b1fc9aeff85",	"230",	"102",	"5"	    ,"",    "");
	coinParameter[168]=new CoinParameter("IFLT",	"InflationCoin-Mainnet",		"11370",	"11371",	"0",	"dbc4f1fa",	"000001d598aef26e66fe34d3027ed1a88d49a01a5955d5b5c24088130cc8a993",	"230",	"102",	"7"	    ,"",    "");
	coinParameter[169]=new CoinParameter("IMS",		"IndependentMoneySystem-Mainnet","6177",	"6178",		"0",	"caa57766",	"0000048564bdab999e626d575bcb071a18f578fc6f3b640ee813f5735c7796c0",	"131",	"3",	"85"    ,"",    "");
	coinParameter[170]=new CoinParameter("IMX",		"Impact-Mainnet",				"22629",	"21529",	"0",	"b1f5d3a9",	"0073cf5568e3ce7070f0094a9a02593296b7b54e70bdf5204871672a809a6759",	"204",	"76",	"141"   ,"",    "");
	coinParameter[171]=new CoinParameter("INFX",	"Influxcoin-Mainnet",			"9238",		"9239",		"70",	"f1e0a2d3",	"00000cbd79dc030b9a4397f20a6607fcbd41a05a6ffdd6f8167c73c7123f18bb",	"230",	"102",	"28"    ,"",    "");
	coinParameter[172]=new CoinParameter("INN",		"Innova-Mainnet",				"14520",	"8818",		"2",	"3c2a3ab9",	"000003841e9ad6096539e5a6a0c3a0d3a3f71b96da0c1ecf744052e6ee3cc2cd",	"195",	"102",	"20"    ,"",    "");
	coinParameter[173]=new CoinParameter("IOC",		"I/OCoin-Mainnet",				"33764",	"33765",	"0",	"fec3bade",	"00000afad2d5833b50b59a9784fdc59869b688680e1670a52c52e3c2c04c1fe8",	"231",	"103",	"85"    ,"",    "");
	coinParameter[174]=new CoinParameter("ION",		"ION-Mainnet",					"12700",	"12705",	"2",	"c4e1d8ec",	"0000004cf5ffbf2e31a9aa07c86298efb01a30b8911b80af7473d1114715084b",	"153",	"103",	"88"    ,"",    "");
	coinParameter[175]=new CoinParameter("IRL",		"IrishCoin-Mainnet",			"12375",	"11372",	"2",	"f7c1b6db",	"31afc6c26b37f0e474ee3456e15797c58035cf6519adc705b56ae89a406793b8",	"161",	"33",	"5"	    ,"",    "");
	coinParameter[176]=new CoinParameter("ISL",		"IslaCoin-Mainnet",				"9731",		"9831",		"3",	"a1a0a2a3",	"0000055bac210396d3152abfed2d8acf5d24da974d0349575330b8331a4b710f",	"230",	"102",	"28"    ,"",    "");
	coinParameter[177]=new CoinParameter("ITI",		"iTicoin-Mainnet",				"42177",	"42144",	"3",	"e4e8e9e5",	"00000a622d889580a343416e3bd9464ea0c38b6fad3ec7b71f144a1de6c5f22c",	"193",	"65",	"20"    ,"",    "");
	coinParameter[178]=new CoinParameter("IXC",		"Ixcoin-Mainnet",				"8337",		"8338",		"3",	"f1bab6db",	"0000000001534ef8893b025b9c1da67250285e35c9f76cae36a4904fdf72c591",	"128",	"138",	"5"	    ,"86",  "");
	coinParameter[179]=new CoinParameter("JIYO",	"Jiyo-Mainnet",					"6080",		"6070",		"6",	"63434956",	"0000066e30a469e6a4a3fc73ffa500ba19d852e57e36e70dbb03d47fb865538d",	"212",	"43",	"12"    ,"",    "");
	coinParameter[180]=new CoinParameter("KEK",		"KekCoin-Mainnet",				"13337",	"13377",	"0",	"11223344",	"0000091bc0f9d1578c7979142b2ff70e6bf8ff7c388cf3dcb486cf19a7518949",	"133",	"45",	"88"    ,"",    "");
	coinParameter[181]=new CoinParameter("KMD",		"Komodo-Mainnet",				"7770",		"7771",		"3",	"f9eee48d",	"027e3758c3a65b12aa1046462b486d0a63bfa1beae327897f56c5cfb7daaae71",	"188",	"60",	"85"    ,"141", "");
	coinParameter[182]=new CoinParameter("KNC",		"KingNCoin-Mainnet",			"18373",	"18374",	"1",	"fc4c8736",	"000004abf1a9742cec3abdb8e189a75b61c4b6ebd93c248876b838bd5d6d7eb4",	"173",	"45",	"85"    ,"",    "");
	coinParameter[183]=new CoinParameter("KOBO",	"Kobocoin-Mainnet",				"9011",		"3341",		"4",	"a1a0a2a3",	"00000c98be708f9ff86319738d4ce8778f1db7ce140d2d665cca9281eef433bd",	"163",	"35",	"28"    ,"",    "");
	coinParameter[184]=new CoinParameter("KUSH",	"KushCoin-Mainnet",				"31544",	"31543",	"2",	"b4e9c2ee",	"00000da47fa70170680a44fc227f643ff91cd9647c6b4c92a6150518b32bb17f",	"173",	"45",	"20"    ,"",    "");
	coinParameter[185]=new CoinParameter("LANA",	"LanaCoin-Mainnet",				"7506",		"5706",		"9",	"a5f790fd",	"0000095667f3c1fdbf0b9b4937be57c6401162fcfe72be373df27393f0c69d93",	"176",	"48",	"5"	    ,"",    "");
	coinParameter[186]=new CoinParameter("LBC",		"LBRYCredits-Mainnet",			"9246",		"9245",		"3",	"fae4aaf1",	"9c89283ba0f3227f6c03b70216b9f665f0118d5e0fa729cedf4fb34d6a34f463",	"28",	"85",	"122"   ,"140", "");
	coinParameter[187]=new CoinParameter("LBTC",	"LiteBitcoin-Mainnet",			"19037",	"19038",	"1",	"5b6d2f54",	"46f1eae198eddd22156b5cebf7023e3998981f518d558e42830edd2795b89b9f",	"131",	"3",	"5"	    ,"",    "");
	coinParameter[188]=new CoinParameter("LCC",		"LitecoinCash-Mainnet",			"62458",	"62457",	"1",	"c7e4baf8",	"12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2",	"176",	"28",	"5"	    ,"192", "lcc");
	coinParameter[189]=new CoinParameter("LCP",		"LitecoinPlus-Mainnet",			"44351",	"44350",	"2",	"cefbfadb",	"000007ca97a92891d88e204cfe475193c9e68fa80d94b97796ba9bcd15e3301c",	"203",	"75",	"8"	    ,"",    "");
	coinParameter[190]=new CoinParameter("LEO",		"LEOcoin-Mainnet",				"5840",		"5211",		"0",	"646e7882",	"00000b486d79051b82d3d843550fb893bf605005f098877c4a911f1f58dfd5ca",	"144",	"18",	"88"    ,"",    "");
	coinParameter[191]=new CoinParameter("LINDA",	"Linda-Mainnet",				"33820",	"33821",	"0",	"9cd31701",	"000000fa793257f502f94a3531aebdaf91b070324a8f5041324b80f4b0f74c51",	"153",	"48",	"85"    ,"",    "");
	coinParameter[192]=new CoinParameter("LIR",		"LetItRide-Mainnet",			"2717",		"2718",		"0",	"2fc3e527",	"36e41150b60b2dd024c2649455870d9d955177d4ccaf5434efcc1404f1c8c3d0",	"153",	"48",	"5"	    ,"",    "");
	coinParameter[193]=new CoinParameter("LOG",		"Woodcoin-Mainnet",				"8338",		"9332",		"6",	"fcd9b7dd",	"30758383eae55ae5c7752b73388c1c85bdfbe930ad25ad877252841ed1e734a4",	"74",	"73",	"5"	    ,"",    "");
	coinParameter[194]=new CoinParameter("LTC",		"Litecoin-Mainnet",				"9333",		"9332",		"5",	"fbc0b6db",	"12a765e31ffd4059bada1e25190f6e98c99d9714d334efa41a195a7e7e04bfe2",	"176",	"48",	"5"	    ,"2",   "ltc");
	coinParameter[195]=new CoinParameter("LUX",		"LUXCoin-Mainnet",				"26969",	"9888",		"0",	"6ab3c8a9",	"00000759bb3da130d7c9aedae170da8335f5a0d01a9007e4c8d3ccd08ace6a42",	"155",	"48",	"63"    ,"",    "bc");
	coinParameter[196]=new CoinParameter("MAC",		"Machinecoin-Mainnet",			"40333",	"40332",	"3",	"fbc0b6db",	"6a1f879bcea5471cbfdee1fd0cb2ddcc4fed569a500e352d41de967703e83172",	"178",	"50",	"5"	    ,"",    "mc");
	coinParameter[197]=new CoinParameter("MAGN",	"Magnetcoin-Mainnet",			"22458",	"22459",	"0",	"a4d2f8a6",	"000000f61edb1c1c06cb2177fa86ffa098051e625cdf7eb5203fae4e773559c5",	"179",	"51",	"50"    ,"",    "");
	coinParameter[198]=new CoinParameter("MAO",		"MaoZedong-Mainnet",			"9670",		"9669",		"14",	"f4d335f1",	"00000f83c8a7fb7ba26343cc23a0ae8a1f0cfb54e6cdf55050bd6a947e7aa456",	"178",	"50",	"28"    ,"",    "");
	coinParameter[199]=new CoinParameter("MARX",	"MarxCoin-Mainnet",				"41103",	"41102",	"4",	"fec3b9de",	"00000d2cc6b7f42962160af9436ff7852e9e4fdb7659624ef1df5d1ccacad43c",	"179",	"51",	"5"	    ,"",    "");
	coinParameter[200]=new CoinParameter("MAX",		"MaxCoin-Mainnet",				"8668",		"8669",		"2",	"f9bebbd2",	"0000002d0f86558a6e737a3a351043ee73906fe077692dfaa3c9328aaca21964",	"128",	"110",	"112"   ,"",    "");
	coinParameter[201]=new CoinParameter("MAY",		"TheresaMayCoin-Mainnet",		"35010",	"35011",	"0",	"a4d2f8a6",	"00000056b82939ea6dbaa22632c4ff5324f2b2c574286af11a380bdb40eda62d",	"178",	"50",	"85"    ,"",    "");
	coinParameter[202]=new CoinParameter("MAZA",	"MAZA-Mainnet",					"12835",	"12832",	"1",	"f8b503df",	"00000c7c73d8ce604178dae13f0fc6ec0be3275614366d44b1b4b5c6e238c60c",	"224",	"50",	"9"	    ,"",    "");
	coinParameter[203]=new CoinParameter("MCRN",	"MACRON-Mainnet",				"55553",	"55555",	"0",	"a4d2f8a6",	"0000008a544bd2fd7451b3eb9a88e7fbd1d765dcbcbe1cd1122fd0628ea35ed4",	"140",	"12",	"85"    ,"",    "");
	coinParameter[204]=new CoinParameter("MEC",		"Megacoin-Mainnet",				"7951",		"7952",		"0",	"ede0e4ee",	"7520788e2d99eec7cf6cf7315577e1268e177fff94cb0a7caf6a458ceeea9ac2",	"178",	"50",	"5"	    ,"217", "");
	coinParameter[205]=new CoinParameter("MEDIC",	"MedicCoin-Mainnet",			"2118",		"2117",		"0",	"1bd76fc9",	"0000ac3f71507372db48ac2626bed6c1321cade7292431b274647673ab60a7ee",	"153",	"50",	"85"    ,"",    "");
	coinParameter[206]=new CoinParameter("MEME",	"Memetic/PepeCoin-Mainnet",		"29377",	"29376",	"2",	"3ac42c2f",	"00008cae6a01358d774087e2daf3b2108252b0b5a440195ffec4fd38f9892272",	"153",	"55",	"85"    ,"",    "");
	coinParameter[207]=new CoinParameter("METAL",	"MetalCoin-Mainnet",			"22332",	"22333",	"0",	"a1a0a2a3",	"0000069a10d81a433e391e3f6696eaaf892c1711388dac257209cca300577e65",	"178",	"50",	"28"    ,"",    "");
	coinParameter[208]=new CoinParameter("MINT",	"Mintcoin-Mainnet",				"12788",	"12789",	"3",	"ced5dbfa",	"af4ac34e7ef10a08fe2ba692eb9a9c08cf7e89fcf352f9ea6f0fd73ba3e5d03c",	"179",	"51",	"8"	    ,"",    "");
	coinParameter[209]=new CoinParameter("MLM",		"MktCoin-Mainnet",				"9275",		"9276",		"1",	"f9beb4d9",	"dcbcae705b0af93fe0aaad1cc588ba243d02ed543d09bf5b167127c05cfcd5ce",	"238",	"110",	"115"   ,"",    "");
	coinParameter[210]=new CoinParameter("MNM",		"Mineum-Mainnet",				"31316",	"31315",	"1",	"5ac382d3",	"000003572d81eb2b1bbae0720300714f9791f440988d3c04e46fe255de8e281d",	"179",	"51",	"85"    ,"",    "");
	coinParameter[211]=new CoinParameter("MNX",		"MinexCoin-Mainnet",			"8335",		"17786",	"5",	"4b4a4c5d",	"490a36d9451a55ed197e34aca7414b35d775baa4a8e896f1c577f65ce2d214cb",	"128",	"75",	"5"	    ,"182", "");
	coinParameter[212]=new CoinParameter("MOJO",	"MojoCoin-Mainnet",				"9495",		"9496",		"9",	"71312106",	"00000b38581f2f7297c2be4689e26dca446cb8c86d23e034791e0833dcea0b8c",	"153",	"50",	"28"    ,"",    "");
	coinParameter[213]=new CoinParameter("MONA",	"MonaCoin-Mainnet",				"9401",		"9402",		"1",	"fbc0b6db",	"ff9f1c0116d19de7c9963845e129f9ed1bfc0b376eb54fd7afa42e0d418c8bb6",	"176",	"50",	"5"	    ,"22",  "");
	coinParameter[214]=new CoinParameter("MONK",	"MonkeyProject-Mainnet",		"8710",		"8101",		"6",	"42a4c52b",	"000009e335cea03f1432c49c44893095f18e261c4d78de38882961f92f4b22c7",	"55",	"51",	"28"    ,"",    "");
	coinParameter[215]=new CoinParameter("MOON",	"Mooncoin-Mainnet",				"44664",	"44663",	"3",	"f9f7c0e8",	"bd687cbbac4842724d4b1dfd2e1a6ce35d765db05635b3ebd3810ba66ac5aee9",	"131",	"3",	"22"    ,"",    "");
	coinParameter[216]=new CoinParameter("MRQ",		"MIRQ-Mainnet",					"55611",	"55622",	"0",	"e1ab3cc1",	"000000c3bb8288f6627ffcb2cd8444bb37ab2cb63146ce4abef121b842514bd6",	"138",	"110",	"40"    ,"",    "");
	coinParameter[217]=new CoinParameter("MST",		"MustangCoin-Mainnet",			"19667",	"19668",	"0",	"a1a0a2a3",	"000007a1a66bbb8c2147bd2d06bca3463d588655c326051b63f12d6130844f5f",	"176",	"48",	"28"    ,"",    "");
	coinParameter[218]=new CoinParameter("MTNC",	"Masternodecoin-Mainnet",		"10086",	"10085",	"0",	"d5ce8c5e",	"00000016046213f1d00cf060b74be707ee78cf9fadb6e5717935295c1ca3d9a4",	"178",	"50",	"5"	    ,"",    "");
	coinParameter[219]=new CoinParameter("MUE",		"MonetaryUnit-Mainnet",			"19683",	"29683",	"4",	"b5cccda7",	"",																	"126",	"16",	"76"    ,"31",  "");
	coinParameter[220]=new CoinParameter("MXT",		"MarteXcoin-Mainnet",			"51315",	"51314",	"5",	"2d3fa2f5",	"617fc383b07fbab3505213b41fe34f9705b92d854a9f72593cb616e4726e155c",	"0",	"50",	"5"	    ,"180", "");
	coinParameter[221]=new CoinParameter("NAV",		"NavCoin-Mainnet",				"44440",	"44444",	"2",	"80503420",	"00006a4e3e18c71c6d48ad6c261e2254fa764cf29607a4357c99b712dfbb8e6a",	"150",	"53",	"85"    ,"130", "");
	coinParameter[222]=new CoinParameter("NEVA",	"NevaCoin-Mainnet",				"7391",		"3791",		"12",	"e483e632",	"0000067865dd821b9f8b2dcdfb367c9d1344d3335fa6fc6d7940b026b88169a6",	"177",	"53",	"5"	    ,"",    "");
	coinParameter[223]=new CoinParameter("NLC2",	"NoLimitCoin-Mainnet",			"6521",		"6520",		"3",	"b1b6f8a6",	"000000c97e5317f440753bb4b0155badbce95fa893cb65d677385df2f07f7a2e",	"181",	"53",	"85"    ,"149", "");
	coinParameter[224]=new CoinParameter("NLG",		"Gulden-Mainnet",				"9231",		"9232",		"5",	"fcfef7e0",	"6c5d71a461b5bff6742bb62e5be53978b8dec5103ce52d1aaab8c6a251582f92",	"39",	"38",	"98"    ,"87",  "");
	coinParameter[225]=new CoinParameter("NLX",		"Nullex-Mainnet",				"6897",		"6898",		"0",	"9f180a16",	"00000e1a057a1f4ede09d189c448ec56b260eb2a6c4744d46352f97ad8be838e",	"166",	"38",	"85"    ,"",    "");
	coinParameter[226]=new CoinParameter("NMC",		"Namecoin-Mainnet",				"8334",		"8336",		"2",	"f9beb4fe",	"000000000062b72c5e2ceb45fbc8587e807c155b0da735e6483dfba2f0a9c770",	"180",	"52",	"13"    ,"7",   "nc");
	coinParameter[227]=new CoinParameter("NMS",		"Numus-Mainnet",				"28121",	"28122",	"0",	"f1eca1c7",	"0711d3aa8846a5a90ab56f0653e066351080f095661c573a45cb94737a82c37c",	"25",	"21",	"20"    ,"",    "");
	coinParameter[228]=new CoinParameter("NUMUS",	"NumusCash-Mainnet",			"21139",	"21140",	"1",	"654ac5d5",	"00000e2bbd11364371c9c5b22d84fa7f6e6807dd86c52356daba0c1fcde76567",	"181",	"53",	"85"    ,"",    "");
	coinParameter[229]=new CoinParameter("NVC",		"Novacoin-Mainnet",				"7777",		"8344",		"4",	"e4e8e9e5",	"00000a060336cbb72fe969666d337b87198b1add2abaa59cca226820b32933a4",	"136",	"8",	"20"    ,"50",  "");
	coinParameter[230]=new CoinParameter("NYC",		"new YorkCoin-Mainnet",			"17020",	"18823",	"0",	"c0c0c0c0",	"5597f25c062a3038c7fd815fe46c67dedfcb3c839fbc8e01ed4044540d08fe48",	"188",	"60",	"22"    ,"179", "");
	coinParameter[231]=new CoinParameter("OCC",		"OctoinCoin-Mainnet",			"16840",	"8332",		"3",	"64736261",	"000059ccb73d95cf54b1ab4f23a1d1821f7ebf3246b9f125562bb78ddc254323",	"128",	"115",	"83"    ,"",    "");
	coinParameter[232]=new CoinParameter("ODN",		"Obsidian-Mainnet",				"56660",	"56661",	"5",	"4f646e31",	"0000006dd8a92f58e952fa61c9402b74a381a69d1930fb5cc12c73273fab5f0a",	"0",	"75",	"125"   ,"173", "");
	coinParameter[233]=new CoinParameter("OK",		"OKCash-Mainnet",				"6970",		"6969",		"6",	"69f00f69",	"0000046309984501e5e724498cddb4aff41a126927355f64b44f1b8bba4f447e",	"183",	"55",	"28"    ,"69",  "");
	coinParameter[234]=new CoinParameter("OMC",		"Omicron-Mainnet",				"8519",		"8520",		"0",	"f240ebb3",	"000003f746e0a666c19ab4eecd597b492ade4d0c3e4d550014372f2f2e2eade2",	"143",	"15",	"85"    ,"",    "");
	coinParameter[235]=new CoinParameter("ONION",	"DeepOnion-Mainnet",			"17570",	"18580",	"0",	"d1f1dbf2",	"000004e29458ef4f2e0abab544737b07344e6ff13718f7c2d12926166db07b5e",	"159",	"31",	"78"    ,"",    "");
	coinParameter[236]=new CoinParameter("OPC",		"OPCoin-Mainnet",				"13355",	"13357",	"3",	"9be0d8e9",	"000093fe59ae8a91008a91b1c10f2ca313bcc9656776c79d84bb4c9ccc26cf80",	"156",	"28",	"85"    ,"",    "");
	coinParameter[237]=new CoinParameter("ORE",		"Galactrum-Mainnet",			"6270",		"6269",		"5",	"b1ded0af",	"0000082da923a04678394f873852c7f08b777af30224b6e23296f586370e80ae",	"204",	"38",	"16"    ,"",    "");
	coinParameter[238]=new CoinParameter("PAK",		"Pakcoin-Mainnet",				"7867",		"7866",		"1",	"70616b63",	"3091f440b6dfcf5d00be71b75bceded11f8606decaa339fe2c9f59e8efcfded2",	"183",	"55",	"5"	    ,"",    "");
	coinParameter[239]=new CoinParameter("PART",	"Particl-Mainnet",				"51738",	"51735",	"3",	"fbf2efb4",	"0000ee0784c195317ac95623e22fddb8c7b8825dc3998e0bb924d66866eccf4c",	"108",	"0",	"0"	    ,"44",  "bc");
	coinParameter[240]=new CoinParameter("PHR",		"Phore-Mainnet",				"11771",	"11772",	"2",	"91c4fde9",	"2b1a0f66712aad59ad283662d5b919415a25921ce89511d73019107e380485bf",	"212",	"55",	"13"    ,"444", "ph");
	coinParameter[241]=new CoinParameter("PHS",		"PhilosopherStones-Mainnet",	"16281",	"16282",	"0",	"e4efdbfd",	"3c2ba73eae76c802be5e7e2da8316268b63b0335365eaa8dff76cc1d76662533",	"149",	"21",	"8"	    ,"",    "");
	coinParameter[242]=new CoinParameter("PIGGY",	"Piggycoin-Mainnet",			"54481",	"54480",	"4",	"a1a0a2a3",	"00000561d6f5f76b0c101ba6bac27ad99a18fc8927c6af844adfd913097e9271",	"246",	"118",	"28"    ,"118", "");
	coinParameter[243]=new CoinParameter("PINK",	"PinkCoin-Mainnet",				"9134",		"9135",		"6",	"f2f4f9fb",	"00000f79b700e6444665c4d090c9b8833664c4e2597c7087a6ba6391b956cc89",	"131",	"3",	"28"    ,"117", "");
	coinParameter[244]=new CoinParameter("PIVX",	"PIVX-Mainnet",					"51472",	"51473",	"4",	"90c4fde9",	"0000041e482b9b9691d98eefb48473405c0b8ec31b76df3797c74a78680ef818",	"212",	"30",	"13"    ,"119", "");
	coinParameter[245]=new CoinParameter("PLACO",	"PlayerCoin-Mainnet",			"16666",	"17321",	"1",	"eb3d1cc4",	"00000f2c9cbab0cf50346d1dd4d55e44afe6430fcaea9eef6f26b1868acb2f9f",	"183",	"55",	"85"    ,"",    "");
	coinParameter[246]=new CoinParameter("PND",		"Pandacoin-Mainnet",			"22445",	"22444",	"1",	"c0c0c0c0",	"68fad98bd07315eef904fa3bf4344a38cb4f05549f659272bad7b4e88961d4c5",	"183",	"55",	"22"    ,"37",  "");
	coinParameter[247]=new CoinParameter("PNX",		"Phantomx-Mainnet",				"31978",	"21978",	"0",	"d627d6e2",	"6e63025da5bd0721303f35e62ec8452a3e684cfd99598ecea14ca198e97dc2c8",	"155",	"55",	"5"	    ,"",    "");
	coinParameter[248]=new CoinParameter("POLIS",	"Polis-Mainnet",				"24126",	"24127",	"5",	"bf0c6bbd",	"000009701eb781a8113b1af1d814e2f060f6408a2c990db291bc5108a1345c1e",	"60",	"55",	"56"    ,"",    "");
	coinParameter[249]=new CoinParameter("POP",		"PopularCoin-Mainnet",			"18181",	"27172",	"0",	"cefbfadb",	"00000e1438374d15f55c670fcae8d7cce5456a0b4cfa7429503eaeb46f6d55d4",	"184",	"56",	"5"	    ,"",    "");
	coinParameter[250]=new CoinParameter("POST",	"PostCoin-Mainnet",				"9130",		"9131",		"0",	"35c3d6a2",	"000005c17d0f7e6ff81c28d4ef2dd07be2e9363582efbdc7481bad70636be4ec",	"183",	"55",	"28"    ,"",    "");
	coinParameter[251]=new CoinParameter("POT",		"PotCoin-Mainnet",				"4200",		"42000",	"1",	"fbc0b6db",	"de36b0cb2a9c7d1d7ac0174d0a89918f874fabcf5f9741dd52cd6d04ee1335ec",	"183",	"55",	"5"	    ,"81",  "");
	coinParameter[252]=new CoinParameter("PPC",		"Peercoin-Mainnet",				"9901",		"9902",		"2",	"e5e9e8e6",	"0000000032fe677166d54963b62a4677d8957e87c508eaa4fd7eb1c880cd27e3",	"183",	"55",	"117"   ,"6",   "");
	coinParameter[253]=new CoinParameter("PROUD",	"PROUDMoney-Mainnet",			"24241",	"24242",	"9",	"f1e0a2d3",	"00000dd205a31c99554c350b8f2d469369e873e04949dff83094f79e35aa9e3e",	"225",	"97",	"38"    ,"",    "");
	coinParameter[254]=new CoinParameter("PURA",	"Pura-Mainnet",					"44444",	"55555",	"0",	"b897c543",	"",																	"150",	"55",	"16"    ,"",    "");
	coinParameter[255]=new CoinParameter("PURE",	"Pure-Mainnet",					"32745",	"32746",	"10",	"11c3b1de",	"0000555c3b7b3f248dc11f0611cb0bbfb0377f4c4406101c10f416dd4f4eaa76",	"153",	"55",	"85"    ,"",    "");
	coinParameter[256]=new CoinParameter("PUT",		"PutinCoin-Mainnet",			"20095",	"20094",	"0",	"b7f0e2e5",	"000002533dfcd597bfb748a6e87f2e6a0b3e50b662e2301e9d338f5d24520829",	"183",	"55",	"20"    ,"122", "");
	coinParameter[257]=new CoinParameter("Q2C",		"QubitCoin-Mainnet",			"7788",		"7799",		"4",	"fea503dd",	"0000049ce6324e2f3f17eec90ce7e1f0bc9bcb44f85d769621d83cbb223ddc03",	"224",	"38",	"9"	    ,"",    "");
	coinParameter[258]=new CoinParameter("QBC",		"Quebecoin-Mainnet",			"56790",	"56789",	"2",	"d3edc9f1",	"00000948015ca05a2197f8f676476c9dbc11de07c87e1a46f2331ea10f33087d",	"186",	"58",	"5"	    ,"",    "");
	coinParameter[259]=new CoinParameter("QTL",		"Quatloo-Mainnet",				"17012",	"17011",	"3",	"facebeda",	"bcb2bb00da6bdf5f80e2afce42307b7fe664f3ec789b6c61936e03ea0e9cf5cc",	"186",	"58",	"5"	    ,"",    "");
	coinParameter[260]=new CoinParameter("QTUM",	"Qtum-Mainnet",					"3888",		"3889",		"4",	"f1cfa6d3",	"000075aef83cf2853580f8ae8ce6f8c3096cfa21d98334d6e3f95e5582ed986c",	"128",	"58",	"50"    ,"2301","");
	coinParameter[261]=new CoinParameter("RADS",	"Radium-Mainnet",				"27913",	"27914",	"0",	"2a7ccbab",	"000000770c6aea829bb1ace7b06497f71799a6358e0e292740c4f9443a17bfb6",	"121",	"76",	"58"    ,"",    "");
	coinParameter[262]=new CoinParameter("RBY",		"Rubycoin-Mainnet",				"5937",		"5938",		"4",	"13121611",	"00000760e24f1ad47f7a6e912bc9ed2b9ce013fc85ba217da8b079762f6b0058",	"188",	"60",	"85"    ,"16",  "");
	coinParameter[263]=new CoinParameter("RC",		"RussiaCoin-Mainnet",			"19992",	"19991",	"0",	"c0c0c0c0",	"fec5da8a697f919cf2c6583adbf41887ea95f7fc1bcb6bb0016b035e0a355dce",	"188",	"60",	"75"    ,"",    "");
	coinParameter[264]=new CoinParameter("RDD",		"ReddCoin-Mainnet",				"45444",	"45443",	"4",	"fbc0b6db",	"b868e0d95a3c3c0e0dadc67ee587aaf9dc8acbf99e3b4b3110fad4eb74c1decc",	"189",	"61",	"5"	    ,"4",   "");
	coinParameter[265]=new CoinParameter("REGA",	"Regacoin-Mainnet",				"28192",	"28190",	"0",	"d4ca3fec",	"52dc4c1688b419e13c68d355a02088b254b5b8e0d7446867f2c0c652137709e6",	"189",	"61",	"5"	    ,"",    "");
	coinParameter[266]=new CoinParameter("RIC",		"Riecoin-Mainnet",				"28333",	"28332",	"1",	"fcbcb2db",	"e1ea18d0676ef9899fbc78ef428d1d26a2416d0f0441d46668d33bcb41275740",	"128",	"60",	"5"	    ,"143", "");
	coinParameter[267]=new CoinParameter("RNS",		"Renos-Mainnet",				"57155",	"57154",	"2",	"aaa3b2c4",	"00000e590857f6bf83e3ae07b56528e47565115465f9d4e8ead2c5bffb1a9edc",	"150",	"60",	"28"    ,"",    "");
	coinParameter[268]=new CoinParameter("ROOFS",	"Roofs-Mainnet",				"20019",	"20020",	"0",	"904a9240",	"000002026c1880506adfbaf9c9aa468764eb7bd4757446b5a8bc409af0f9339a",	"188",	"60",	"85"    ,"",    "");
	coinParameter[269]=new CoinParameter("RPC",		"RonPaulCoin-Mainnet",			"9027",		"9026",		"1",	"fbc0b6db",	"12e05e8d4a8258aed79e113ee429c5194ee1c8999f50df176ecea97380d474ab",	"188",	"60",	"3"	    ,"",    "");
	coinParameter[270]=new CoinParameter("RUP",		"Rupee-Mainnet",				"10459",	"10460",	"0",	"3b69ded4",	"fb6a19a31293de12035e5cdaadce2a8552d3feeec6fa5f910101d9d8c579b566",	"188",	"60",	"5"	    ,"",    "");
	coinParameter[271]=new CoinParameter("RUPX",	"Rupaya-Mainnet",				"9020",		"7020",		"6",	"63434956",	"0000059f8ba2b9ec3f6690af8d118ff1ffd7d791a420636d147846393d7be6b2",	"212",	"15",	"8"	    ,"",    "");
	coinParameter[272]=new CoinParameter("RVN",		"Ravencoin-Mainnet",			"8767",		"8766",		"2",	"5241564e",	"0000006b444bc2f2ffe627be9d9e7e7a0730000870ef6eb6da46c8eae389df90",	"128",	"60",	"122"   ,"175", "rc");
	coinParameter[273]=new CoinParameter("SAGA",	"SagaCoin-Mainnet",				"48744",	"48844",	"2",	"aaa3b2c4",	"e523a92cbe17c3e13fbc1bf191e79138c9dfbe61199ede7870b4d5abd89c0e93",	"142",	"125",	"44"    ,"",    "");
	coinParameter[274]=new CoinParameter("SAK",		"Sharkcoin-Mainnet",			"4011",		"4009",		"2",	"fea503db",	"000000042864103f8cd5cb2c644e71bdc109e80249fae4ec5d0e384c884ff2dd",	"191",	"63",	"9"	    ,"",    "");
	coinParameter[275]=new CoinParameter("SBC",		"StrikeBitClub-Mainnet",		"21575",	"21576",	"2",	"556a3299",	"00000a082edd9c65494b1f206f81dc9f4781556d5e4013aa5566ce0f0cb9a579",	"141",	"13",	"85"    ,"",    "");
	coinParameter[276]=new CoinParameter("SEQ",		"Sequence-Mainnet",				"16662",	"16663",	"4",	"01100220",	"000000251356c62e0aa14c63e2b0db2a05ac0d3316ea5000c797a281be8c9fd7",	"170",	"63",	"64"    ,"",    "");
	coinParameter[277]=new CoinParameter("SIB",		"SIBCoin-Mainnet",				"1945",		"1944",		"5",	"bf0c6bbd",	"00000c492bf73490420868bc577680bfc4c60116e7e85343bc624787c21efa4c",	"128",	"63",	"40"    ,"",    "");
	coinParameter[278]=new CoinParameter("SKC",		"Skeincoin-Mainnet",			"11230",	"21230",	"11",	"f726a1bf",	"0000046cebed69de151ada93a60cb8a5f9490a196399abe714bb83ad5b20f985",	"226",	"63",	"12"    ,"",    "");
	coinParameter[279]=new CoinParameter("SLR",		"SolarCoin-Mainnet",			"18188",	"18181",	"2",	"04f104fd",	"edcf32dbfd327fe7f546d3a175d91b05e955ec1224e087961acc9a2aa8f592ee",	"146",	"18",	"5"	    ,"58",  "");
	coinParameter[280]=new CoinParameter("SLS",		"SaluS-Mainnet",				"22534",	"22530",	"0",	"d3c5a721",	"000003f07d368792f736f3098ad7201bb821883fbb66b27a8ee65be273847e02",	"191",	"63",	"23"    ,"",    "");
	coinParameter[281]=new CoinParameter("SMART",	"SmartCash-Mainnet",			"9678",		"9679",		"9",	"5ca1ab1e",	"000007acc6970b812948d14ea5a0a13db0fdd07d5047c7e69101fa8b361e05a4",	"191",	"63",	"18"    ,"224", "");
	coinParameter[282]=new CoinParameter("SOCC",	"SocialCoin-Mainnet",			"18645",	"18646",	"0",	"ee64e31d",	"716b50a3d5c5e9c5cd4fdbdf68c618712e3595cc324053ffd617082103cf5e6b",	"191",	"63",	"5"	    ,"",    "");
	coinParameter[283]=new CoinParameter("SONG",	"SongCoin-Mainnet",				"8335",		"8334",		"1",	"534f4e47",	"c2e1053b8b32c0aba8039421206c6760558d4fcd264394834fcf00a3245eca91",	"191",	"63",	"5"	    ,"",    "");
	coinParameter[284]=new CoinParameter("SPACE",	"SpaceCoin-Mainnet",			"9172",		"9173",		"3",	"f4f2f9fb",	"00000beb628cb003ee6fa0145731dff34960e01cd9ce877b23ce1395dfb93088",	"191",	"63",	"52"    ,"",    "");
	coinParameter[285]=new CoinParameter("SPD",		"Stipend-Mainnet",				"46978",	"46979",	"0",	"a3fbdbdb",	"7a5541643f97ca4bff6cd7b01e3fbea04002760713320a5750276d9b9c71d845",	"23",	"63",	"21"    ,"",    "");
	coinParameter[286]=new CoinParameter("SPHR",	"Sphere-Mainnet",				"37544",	"37545",	"1",	"a8b81130",	"00001f60dc7ff24d6eefb369ce6878e886815907cf44ceb45aa12a1d30b28acd",	"153",	"25",	"85"    ,"",    "");
	coinParameter[287]=new CoinParameter("SPK",		"Sparks-Mainnet",				"8890",		"8892",		"4",	"1ab2c3d4",	"00000a5c6ddfaac5097218560d5b92d416931cfeba1abf10c81d1d6a232fc8ea",	"198",	"38",	"10"    ,"",    "");
	coinParameter[288]=new CoinParameter("SRC",		"SecureCoin-Mainnet",			"12567",	"12568",	"1",	"fcb4d9ab",	"0000047462b7a97ff0e85630d00e57352d98ab4c17156273f22415fe8d78a104",	"128",	"125",	"9"	    ,"",    "");
	coinParameter[289]=new CoinParameter("STAK",	"STRAKS-Mainnet",				"7575",		"7574",		"5",	"b0d5f02c",	"00000df14d859c4b3219d93978bcf02afc123d2344a2ed39033e1208948aa7c0",	"204",	"63",	"5"	    ,"187", "");
	coinParameter[290]=new CoinParameter("STN",		"SteneumCoin-Mainnet",			"26965",	"26966",	"0",	"e626170d",	"6786546873c422dfc3780ea05ebce6b6ef8056ec77541c66f6336d7d64cf69bf",	"191",	"63",	"5"	    ,"",    "");
	coinParameter[291]=new CoinParameter("STRAT",	"Stratis-Mainnet",				"26965",	"26966",	"0",	"e626170d",	"",																	"191",	"63",	"125"   ,"158", "");
	coinParameter[292]=new CoinParameter("STV",		"Sativacoin-Mainnet",			"60990",	"62990",	"3",	"a1a0a2a3",	"000006f40075bafa504d64ac179d47dc09faae31f35209431a5d2398f88de3e2",	"191",	"63",	"28"    ,"",    "");
	coinParameter[293]=new CoinParameter("SUPER",	"SuperCoin-Mainnet",			"19390",	"19391",	"2",	"dbfafcfd",	"00000130028bd75067e05b06fe8126e7f8ce8027a67898a6d649d0e239232a75",	"191",	"63",	"28"    ,"",    "");
	coinParameter[294]=new CoinParameter("SWING",	"Swing-Mainnet",				"16061",	"16080",	"1",	"dd1ee2af",	"000000c856fd696b02fe03df844aa7b1b92480a959d5595a5c942f129fae2220",	"191",	"63",	"85"    ,"",    "");
	coinParameter[295]=new CoinParameter("SXC",		"Sexcoin-Mainnet",				"9560",		"9561",		"3",	"face6969",	"f42b9553085a1af63d659d3907a42c3a0052bbfa2693d3acf990af85755f2279",	"190",	"62",	"5"	    ,"",    "");
	coinParameter[296]=new CoinParameter("SYS",		"Syscoin-Mainnet",				"8369",		"8370",		"4",	"f9beb4d9",	"000006e5c08d6d2414435b294210266753b05a75f90e926dd5e6082306812622",	"128",	"63",	"5"	    ,"57",  "");
	coinParameter[297]=new CoinParameter("TAJ",		"TajCoin-Mainnet",				"10712",	"12107",	"9",	"7d4f8b4d",	"00000b414afcc2b242531999ae4cc5e7279eaa1508aa72cb289cc175ebb1db2b",	"111",	"65",	"5"	    ,"",    "");
	coinParameter[298]=new CoinParameter("TELL",	"Tellurion-Mainnet",			"9999",		"33716",	"5",	"1a1c3a1b",	"000003034531d80c294d555923c0be7577aafbae370e2a3ca787fa0e509000c7",	"193",	"65",	"50"    ,"",    "");
	coinParameter[299]=new CoinParameter("THC",		"HempCoin-Mainnet",				"21054",	"12055",	"0",	"a5a5fd01",	"00000dd93ea1774f94a1ac8d26fa5bc165c1bc3d415b9c37ecb8a6ebe5e05fb1",	"168",	"40",	"8"	    ,"",    "");
	coinParameter[300]=new CoinParameter("TOA",		"ToaCoin-Mainnet",				"9642",		"3564",		"0",	"eaafe3c7",	"000006cb2d081254e84a103ab457c64f8d8bf01c52fa00e5a3cefc5986bb167b",	"193",	"65",	"23"    ,"159", "");
	coinParameter[301]=new CoinParameter("TOKC",	"TOKYO-Mainnet",				"23517",	"23518",	"1",	"005aab1e",	"00000d49a58b1928c5c66347a4d869b97ec8288a54ea0b846e8bfd1a4d5369c7",	"193",	"65",	"85"    ,"",    "");
	coinParameter[302]=new CoinParameter("TOP",		"TopCoin-Mainnet",				"22561",	"22562",	"0",	"cefbfadb",	"00000d439165d4f937bd617eb8ee539ce9e801d8874ecbe90b2a54978a88d340",	"255",	"127",	"8"	    ,"",    "");
	coinParameter[303]=new CoinParameter("TPAY",	"TokenPay-Mainnet",				"8801",		"8800",		"0",	"fbf1e2b1",	"000008b71ab32e585a23f0de642dc113740144e94c0ece047751e9781f953ae9",	"179",	"65",	"126"   ,"",    "");
	coinParameter[304]=new CoinParameter("TRC",		"Terracoin-Mainnet",			"13333",	"13332",	"2",	"42babe56",	"00000000804bbc6a621a9dbb564ce469f492e1ccf2d70f8a6b241e26a277afa2",	"128",	"0",	"5"	    ,"83",  "");
	coinParameter[305]=new CoinParameter("TRK",		"Truckcoin-Mainnet",			"18775",	"18776",	"5",	"dbadbdda",	"000005fe04e512585c3611369c7ce23f130958038c18a462577d002680dab4fc",	"245",	"117",	"8"	    ,"",    "");
	coinParameter[306]=new CoinParameter("TX",		"TransferCoin-Mainnet",			"17170",	"17171",	"3",	"d12e1ee6",	"9672529bc958a440a8acd061d914120d44c914a06454b82d3e1cd68fe4f1f916",	"153",	"66",	"85"    ,"",    "");
	coinParameter[307]=new CoinParameter("UIS",		"Unitus-Mainnet",				"50603",	"50604",	"4",	"c5abc69d",	"d8a2b2439d013a59f3bfc626a33487a3d7d27e42a3c9e0b81af814cd8e592f31",	"132",	"68",	"10"    ,"",    "");
	coinParameter[308]=new CoinParameter("ULA",		"Ulatech-Mainnet",				"21659",	"21660",	"2",	"8ba3369a",	"1b12b0f061a5e8313157563b86bef7d73b45dc6668a084b9b4c1615c9c9d9a34",	"196",	"68",	"5"	    ,"",    "");
	coinParameter[309]=new CoinParameter("UNIFY",	"Unify-Mainnet",				"18649",	"18650",	"6",	"c447f9ee",	"943dc625657143761f0f01dbf08c45a02260fc4089a27b20de3e42ae8a49154f",	"196",	"68",	"5"	    ,"124", "");
	coinParameter[310]=new CoinParameter("UNIT",	"UniversalCurrency-Mainnet",	"14158",	"14157",	"0",	"a4d2f8a6",	"00000007f1fac8c118dcfd23e8a75303112ba9a05add3b092daee24137a3aede",	"183",	"55",	"85"    ,"",    "");
	coinParameter[311]=new CoinParameter("UNO",		"Unobtanium-Mainnet",			"65534",	"65535",	"3",	"03d5b503",	"000004c2fc5fffb810dccc197d603690099a68305232e552d96ccbe8e2c52b75",	"224",	"130",	"30"    ,"92",  "");
	coinParameter[312]=new CoinParameter("USC",		"UltimateSecureCash-Mainnet",	"51737",	"51736",	"0",	"faf2efb4",	"00000438d60fb1a01a92a141f86d367589fd6190727d246ad24ac4119d3e6691",	"191",	"68",	"125"   ,"112", "");
	coinParameter[313]=new CoinParameter("VEC2",	"VectorAI-Mainnet",				"1715",		"1716",		"0",	"e79260e8",	"00000d5fdbc741683f8ca202c7827a3904a417d8fa68b3f1db5d3d4f3f6df3d6",	"186",	"70",	"45"    ,"",    "");
	coinParameter[314]=new CoinParameter("VIA",		"Viacoin-Mainnet",				"5223",		"5222",		"3",	"0f68c6cb",	"4e9b54001f9976049830128ec0331515eaabe35a70970d79971da1539a400ba1",	"199",	"71",	"33"    ,"14",  "");
	coinParameter[315]=new CoinParameter("VIDZ",	"PureVidz-Mainnet",				"3818",		"4818",		"0",	"e4155311",	"d4593568eaa0db15bf551bbe322d9782cc2aaf709f3d7b5696c172b9c89fa68a",	"153",	"71",	"75"    ,"",    "");
	coinParameter[316]=new CoinParameter("VIPS",	"VipstarCoin-Mainnet",			"31915",	"31916",	"1",	"012CE7B5",	"0000d068e1d30f79fb64446137106be9c6ee69a6a722295c131506b1ee09b77c",	"128",	"70",	"50"    ,"",    "");
	coinParameter[317]=new CoinParameter("VISIO",	"Visio-Mainnet",				"16778",	"16774",	"4",	"70352205",	"100944391b1edc3090600c5748a9373665500e80dc2a2e4b74e2168c3252ee47",	"0",	"71",	"125"   ,"",    "");
	coinParameter[318]=new CoinParameter("VIVO",	"VIVO-Mainnet",					"12845",	"9998",		"2",	"1d425ba7",	"00000f6be3e151f9082a2b82c2916192a791090015b80979934a45d625460d62",	"198",	"70",	"10"    ,"166", "");
	coinParameter[319]=new CoinParameter("VOLT",	"Bitvolt-Mainnet",				"11516",	"15615",	"0",	"12695122",	"0000038be9d28a7189fdfbd10e2c05fa51e555430da4967b03dcd374ee1ba9ec",	"166",	"70",	"85"    ,"",    "");
	coinParameter[320]=new CoinParameter("VOT",		"VoteCoin-Mainnet",				"8144",		"8242",		"3",	"24e92764",	"",                                                                 "128",	"7352","7357"   ,"",    "");
	coinParameter[321]=new CoinParameter("VRC",		"VeriCoin-Mainnet",				"58684",	"58683",	"3",	"70352205",	"",                                                                 "198",	"70",	"132"   ,"",    "");
	coinParameter[322]=new CoinParameter("VSX",		"Vsync-Mainnet",				"65010",	"65015",	"10",	"21550a5a",	"",                                                                 "212",	"70",	"13"    ,"",    "");
	coinParameter[323]=new CoinParameter("VTA",		"Virtacoin-Mainnet",			"22816",	"22815",	"2",	"bed0c8d1",	"",                                                                 "128",	"0",	"5"     ,"",    "");
	coinParameter[324]=new CoinParameter("VTC",		"Vertcoin-Mainnet",				"5889",		"5888",		"7",	"fabfb5da",	"",                                                                 "128",	"71",	"5"     ,"28",  "vtc");
	coinParameter[325]=new CoinParameter("VULC",	"Vulcano-Mainnet",				"21041",	"21042",	"4",	"e5777746",	"",                                                                 "198",	"70",	"85"    ,"",    "");
	coinParameter[326]=new CoinParameter("WC",		"WINCOIN-Mainnet",				"11610",	"11611",	"0",	"f9beb4d9",	"",                                                                 "201",	"73",	"83"    ,"181", "");
	coinParameter[327]=new CoinParameter("WINK",	"Wink-Mainnet",					"37748",	"37745",	"0",	"f32da571",	"",                                                                 "176",	"48",	"142"   ,"",    "");
	coinParameter[328]=new CoinParameter("WOMEN",	"WomenCoin-Mainnet",			"19207",	"19208",	"0",	"f11394ee",	"",                                                                 "201",	"73",	"85"    ,"",    "");
	coinParameter[329]=new CoinParameter("XBTC21",	"Bitcoin21-Mainnet",			"21008",	"21007",	"6",	"b4f8e2e5",	"",                                                                 "131",	"3",	"20"    ,"",    "");
	coinParameter[330]=new CoinParameter("XBTS",	"Beatcoin-Mainnet",				"26152",	"26151",	"0",	"a1a0a2a3",	"",                                                                 "153",	"25",	"28"    ,"",    "");
	coinParameter[331]=new CoinParameter("XCO",		"X-Coin-Mainnet",				"14641",	"14642",	"0",	"a5d2d7a6",	"",                                                                 "203",	"75",	"85"    ,"",    "");
	coinParameter[332]=new CoinParameter("XCT",		"C-Bit-Mainnet",				"8289",		"8288",		"0",	"deadfed5",	"",                                                                 "128",	"0",	"5"     ,"",    "");
	coinParameter[333]=new CoinParameter("XGOX",	"XGOX-Mainnet",					"23185",	"23186",	"2",	"71ae7664",	"",                                                                 "166",	"38",	"85"    ,"",    "");
	coinParameter[334]=new CoinParameter("XJO",		"Joulecoin-Mainnet",			"26789",	"8844",		"6",	"a5c07955",	"",                                                                 "143",	"43",	"11"    ,"",    "");
	coinParameter[335]=new CoinParameter("XLR",		"Solaris-Mainnet",				"60020",	"61020",	"13",	"022101a1",	"",                                                                 "212",	"63",	"13"    ,"",    "");
	coinParameter[336]=new CoinParameter("XMR",		"Monero-Mainnet",				"18080",	"18081",	"4",	"",			"",                                                                 ""   ,	"18",	""      ,"60",  "");
	coinParameter[337]=new CoinParameter("XMY",		"Myriad-Mainnet",				"10888",	"10889",	"9",	"af4576ee",	"",                                                                 "178",	"50",	"9"     ,"90",  "");
	coinParameter[338]=new CoinParameter("XP",		"ExperiencePoints-Mainnet",		"28192",	"28191",	"4",	"b4f8e2e5",	"",                                                                 "203",	"75",	"20"    ,"",    "");
	coinParameter[339]=new CoinParameter("XPM",		"Primecoin-Mainnet",			"9911",		"9912",		"4",	"e4e7e5e7",	"",                                                                 "151",	"23",	"83"    ,"24",  "");
	coinParameter[340]=new CoinParameter("XPTX",	"PlatinumBAR-Mainnet",			"18993",	"18994",	"5",	"03030606",	"",                                                                 "214",	"55",	"117"   ,"",    "");
	coinParameter[341]=new CoinParameter("XQN",		"Quotient-Mainnet",				"30994",	"30997",	"1",	"efb2faf2",	"",                                                                 "186",	"58",	"125"   ,"",    "");
	coinParameter[342]=new CoinParameter("XRA",		"Ratecoin-Mainnet",				"35851",	"35850",	"3",	"a1a0a2a3",	"",                                                                 "188",	"60",	"28"    ,"",    "");
	coinParameter[343]=new CoinParameter("XRE",		"RevolverCoin-Mainnet",			"8777",		"8775",		"0",	"f9beb4d9",	"",                                                                 "128",	"0",	"5"     ,"",    "");
	coinParameter[344]=new CoinParameter("XSN",		"Stakenet-Mainnet",				"62583",	"51473",	"6",	"bf0c6cbd",	"",                                                                 "204",	"76",	"16"    ,"",    "xc");
	coinParameter[345]=new CoinParameter("XSPEC",	"Spectrecoin-Mainnet",			"37347",	"36657",	"0",	"b5225cd3",	"",                                                                 "179",	"63",	"136"   ,"",    "");
	coinParameter[346]=new CoinParameter("XTO",		"Tao-Mainnet",					"15150",	"15151",	"7",	"1dd11ee1",	"",                                                                 "76",	"66",	"3"     ,"",    "");
	coinParameter[347]=new CoinParameter("XVG",		"Verge-Mainnet",				"21102",	"20102",	"0",	"f7a77eff",	"",                                                                 "158",	"30",	"33"    ,"77",  "");
	coinParameter[348]=new CoinParameter("XWC",		"WhiteCoin-Mainnet",			"15814",	"15815",	"4",	"182d43f3",	"",                                                                 "74",	"73",	"87"    ,"155", "");
	coinParameter[349]=new CoinParameter("XZC",		"ZCoin-Mainnet",				"8168",		"8888",		"5",	"e3d9fef1",	"",                                                                 "210",	"82",	"7"     ,"136", "");
	coinParameter[350]=new CoinParameter("YTN",		"YENTEN-Mainnet",				"9981",		"9252",		"1",	"ad5aeb9f",	"",                                                                 "123",	"78",	"10"    ,"",    "");
	coinParameter[351]=new CoinParameter("ZCL",		"ZClassic-Mainnet",				"8033",		"8023",		"13",	"24e92764",	"",                                                                 "128",	"7352","7357"   ,"147", "");
	coinParameter[352]=new CoinParameter("ZEC",		"Zcash-Mainnet",				"8233",		"8232",		"3",	"24e92764",	"",                                                                 "128",	"7352","7357"   ,"133", "");
	coinParameter[353]=new CoinParameter("ZEIT",	"Zeitcoin-Mainnet",				"44845",	"44843",	"3",	"ced5dbfa",	"",                                                                 "179",	"51",	"8"     ,"",    "");
	coinParameter[354]=new CoinParameter("ZEN",		"ZenCash-Mainnet",				"9033",		"8231",		"6",	"63617368",	"",                                                                 "128",	"8329","8342"   ,"121", "");
	coinParameter[355]=new CoinParameter("ZENI",	"Zennies-Mainnet",				"11011",	"11012",	"2",	"2a7ccbab",	"",                                                                 "142",	"80",	"72"    ,"",    "");
	coinParameter[356]=new CoinParameter("ZET",		"Zetacoin-Mainnet",				"17333",	"8332",		"12",	"fab503df",	"",                                                                 "224",	"80",	"9"     ,"",    "");
	coinParameter[357]=new CoinParameter("ZOI",		"Zoin-Mainnet",					"8255",		"8255",		"0",	"f503a951",	"",                                                                 "208",	"80",	"7"     ,"",    "");
	coinParameter[358]=new CoinParameter("ZYD"	,	"Zayedcoin-Mainnet",			"8371",		"8372",		"0",	"d0cecf9c",	"",                                                                 "209",	"81",	"5"     ,"103", "");
	coinParameter[359]=new CoinParameter("ZZC",		"ZoZoCoin-Mainnet",				"19995",	"3882",		"9",	"bf0c6bbd",	"",                                                                 "204",	"76",	"16"    ,"",    "");
	coinParameter[689]=new CoinParameter("BSV",		"Bitcoin SV-Mainnet",			"8333",		"8332",		"6",	"e3e1f3e8",	"000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f",	"128",	"0",	"5"		,"145", "");

	
	
	// --------------------------------- TestNet -----------------------------------------------------------------------------------------------------------------//
	
	coinParameter[360]=new CoinParameter("$PAC-T",	"PACcoin-Testnet",				"17112",	"17111",	"1",	"9b2ffae3",	"00000da63bd9478b655ef6bf1bf76cd9af05202ab68643f9091e049b2b5280ed",	"239",	"120",	"14"    ,"1",    "");
	coinParameter[361]=new CoinParameter("1337-T",	"Elite-Testnet",				"26714",	"26715",	"0",	"cdf2c0ef",	"000004611c87517dfd29fe7f34bd6da2e1ad3d305ac12afe80a3229069390f68",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[362]=new CoinParameter("42-T",	"42-coin-Testnet",				"42420",	"21210",	"0",	"cdf2c0ef",	"00000bc79a2049b1430c77d81fad3373070e65668b21792d298ae5dde3e7abb8",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[363]=new CoinParameter("ABJC-T",	"AbjcoinCommerce-Testnet",		"69081",	"69081",	"0",	"1bba63c5",	"446de92737e111de39af38837ba50dc1adb60c208bf09d4c26ac9549eb0f6021",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[364]=new CoinParameter("ACC-T",	"AdCoin-Testnet",				"19335",	"29500",	"3",	"fdd2c8f1",	"93c2ed969aedded98026e9ee8f2c0c6213329d1f4e3b0df19a53d01378e40cd4",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[365]=new CoinParameter("ACES-T",	"Aces-Testnet",					"21275",	"21275",	"0",	"70352205",	"000005653beb1e6540e83b43c21127ec6ff15e73e57ffa1f0c87c023bb134c33",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[366]=new CoinParameter("ACP-T",	"AnarchistsPrime-Testnet",		"5744",		"5745",		"0",	"0b110907",	"000000008afd70438709390e1a1b4e64c81437ffb244d785b7d6029d7b1ac95e",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[367]=new CoinParameter("ADZ-T",	"Adzcoin-Testnet",				"143029",	"143028",	"0",	"fec4bade",	"000008da0e16960d6c2548da4831323b956d61370e2a3fdc5150188c5c478c49",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[368]=new CoinParameter("ALL-T",	"Allion-Testnet",				"0",		"0",		"0",	"f1f3c0ef",	"0000007712af1202c5d79cc2db4389dfafee039f8ecd5b099f4c4443c2da61e6",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[369]=new CoinParameter("ALQO-T",	"ALQO-Testnet",					"55600",	"55000",	"0",	"64446554",	"0000083eda581357fce6db177da9049ee583b3d32236d4ff52e1f86fee2d5aad",	"193",	"83",	"18"    ,"1",    "");
	coinParameter[370]=new CoinParameter("AMMO-T",	"AmmoReloaded-Testnet",			"28582",	"28583",	"0",	"",			"",																	"149",	"21",	"196"   ,"1",    "");
	coinParameter[371]=new CoinParameter("ANI-T",	"Animecoin-Testnet",			"2424",		"4242",		"0",	"4d494e41",	"0000042d48638031294f0d84a027e895c1a321612dc326e6adc7a6c07deb352c",	"247",	"119",	"199"   ,"1",    "");
	coinParameter[372]=new CoinParameter("ANTX-T",	"Antimatter-Testnet",			"28595",	"28596",	"0",	"66791ae3",	"000009964453428b6f9ca4f20daa0691c3435f7572d53124579039dd33b1c299",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[373]=new CoinParameter("APR-T",	"APRCoin-Testnet",				"13133",	"13132",	"0",	"aa2d92bc",	"00000a8cc5b18e27b3b0068d3a554d4f9b5661b092f4d467a8df2ca9407fef27",	"108",	"98",	"12"    ,"1",    "");
	coinParameter[374]=new CoinParameter("ARB-T",	"ARbit-Testnet",				"31914",	"31915",	"0",	"adf1c2af",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[375]=new CoinParameter("ARC-T",	"AdvancedTechnologyCoin-Testnet","17209",	"17208",	"0",	"2a2c2c2d",	"000007ddc82c93642da51870d1d549c6b91c44d290aad2b9d95e90f17ec2fc2b",	"239",	"83",	"9"     ,"1",    "");
	coinParameter[376]=new CoinParameter("ARCO-T",	"AquariusCoin-Testnet",			"16205",	"16206",	"0",	"6a1cd5e6",	"00005b3bb1da9b87ce69834b3b1bd98018eccf663c3a4e3c89827a4c0c3b2933",	"239",	"0",	"0"     ,"1",    "");
	coinParameter[377]=new CoinParameter("ARG-T",	"Argentum-Testnet",				"13555",	"13556",	"0",	"fcc1b7dc",	"6bee778b0f99ee7a02635bc7de7d2c28f8a844f8c3aa01cb19b02adb9a169461",	"239",	"88",	"188"   ,"1",    "");
	coinParameter[378]=new CoinParameter("ASAFE2-T","AllSafe-Testnet",				"40234",	"40244",	"0",	"1bba63c5",	"000002fb73d5d11d2ca0f931baecd9bdd912d1aa38c6f8e41376dd41ace32713",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[379]=new CoinParameter("ATC-T",	"Arbitracoin-Testnet",			"32541",	"32641",	"0",	"edc6feeb",	"000002ebbd06d189fff6768e89abf3ea66ab837c706f061b26d97ff3d1a9e179",	"239",	"83",	"196"   ,"1",    "");
	coinParameter[380]=new CoinParameter("AU-T",	"AurumCoin-Testnet",			"5744",		"5745",		"0",	"0b110907",	"0000000040a867146dcc50bee85f69ac20addc33080c2a769c01200920251955",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[381]=new CoinParameter("AV-T",	"AvatarCoin-Testnet",			"19712",	"19711",	"0",	"b23b2adf",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[382]=new CoinParameter("AXIOM-T",	"Axiom-Testnet",				"25760",	"25770",	"0",	"3f1a1c05",	"75687e926dd7611f320a99144869f1e281e275b306c634e285e780f1440a0064",	"239",	"83",	"196"   ,"1",    "");
	coinParameter[383]=new CoinParameter("BCA-T",	"BitcoinAtom-Testnet",			"17333",	"17332",	"3",	"a68e3fd6",	"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",	"239",	"111",	"196"   ,"1",    "tbca");
	coinParameter[384]=new CoinParameter("BCC-T",	"BitConnect-Testnet",			"19239",	"19240",	"0",	"1bba63c5",	"00000d3bd95c47fa17c47e1e2732d7072a6c4014a2fa93873124418a8fd9a300",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[385]=new CoinParameter("BCD-T",	"BitcoinDiamond-Testnet",		"17117",	"17116",	"1",	"0bcd2018",	"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[386]=new CoinParameter("BCF-T",	"BitcoinFast-Testnet",			"35671",	"35672",	"0",	"cdf1c0ef",	"0000056ae1a4b604b1f71b5436f3f22775429c03f29a9786e5e33fc104041b46",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[387]=new CoinParameter("BCH-T",	"BitcoinCash-Testnet",			"18333",	"18332",	"4",	"f4e5f3f4",	"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[388]=new CoinParameter("BCI-T",	"BitcoinInterest-Testnet",		"18331",	"18332",	"4",	"dd74e77b",	"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[389]=new CoinParameter("BCO-T",	"BridgeCoin-Testnet",			"16333",	"16332",	"3",	"fdd2c8f1",	"6515ba86f08c652e0eb53e1b789102d3499b59c08b8421582a822d55ac17ab6e",	"239",	"10",	"196"   ,"1",    "");
	coinParameter[390]=new CoinParameter("BENJI-T",	"BenjiRolls-Testnet",			"17763",	"17762",	"0",	"fcc1b7dc",	"30be3f60eda0d41cae6cb172b584ec9d524156890bf5909e394679b651593ae2",	"382",	"254",	"252"   ,"1",    "");
	coinParameter[391]=new CoinParameter("BERN-T",	"BERNcash-Testnet",				"42020",	"42016",	"0",	"70352205",	"787cd5c72fe58e6eb86845ca45eee05c932310ed684a6a712382a4a1de031f89",	"214",	"86",	"196"   ,"1",    "");
	coinParameter[392]=new CoinParameter("BIO-T",	"BioCoin-Testnet",				"17778",	"18345",	"0",	"cdf2c0ef",	"000009038d39a5ecfecf6a15cc7c49ff3c7bc0b23f8115902a9c0f3c9f72e9b9",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[393]=new CoinParameter("BIOB-T",	"BioBar-Testnet",				"21650",	"21655",	"0",	"b8a64dc2",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[394]=new CoinParameter("BIRDS-T",	"Birds-Testnet",				"30013",	"30014",	"0",	"131f9ae3",	"000007620092fb7eeeb238e94604713d1de6f39c1feb5c84214a0acbbd943bc1",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[395]=new CoinParameter("BITG-T",	"BitcoinGreen-Testnet",			"19333",	"19332",	"0",	"4a2d32bc",	"000008467c3a9c587533dea06ad9380cded3ed32f9742a6c0c1aebc21bf2bc9b",	"108",	"98",	"12"    ,"1",    "");
	coinParameter[396]=new CoinParameter("BITOK-T",	"Bitok-Testnet",				"21997",	"21996",	"0",	"a7b1a5ab",	"0000df5f98453dbb5161d724fd4eb8a00b54d4a0e6ecad965abf0b2818ba8032",	"255",	"127",	"196"   ,"1",    "");
	coinParameter[397]=new CoinParameter("BITS-T",	"Bitstar-Testnet",				"63123",	"63124",	"0",	"cdf1c0ef",	"3bac216bbdf5b02ef8f8d43e9ee2fb28eef38e1e47bcb271ee81780445e2ca30",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[398]=new CoinParameter("BLAZR-T",	"BlazerCoin-Testnet",			"18213",	"18212",	"0",	"fcc1b7dc",	"7b026df6f36ca2f3516c1026ae4615907767d780ad4277074543a74e3fdf921e",	"254",	"126",	"124"   ,"1",    "");
	coinParameter[399]=new CoinParameter("BLK-T",	"BlackCoin-Testnet",			"25714",	"25715",	"0",	"cdf2c0ef",	"0000724595fb3b9609d441cbfb9577615c292abf07d996d3edabc48de843642d",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[400]=new CoinParameter("BLOCK-T",	"Blocknet-Testnet",				"41474",	"41419",	"0",	"457665ba",	"00000f90ac260859e4515356719d94c9fb8cadb1a3dda186a64ac41ce4c3c7a7",	"239",	"139",	"19"    ,"1",    "");
	coinParameter[401]=new CoinParameter("BLU-T",	"BlueCoin-Testnet",				"17104",	"17105",	"0",	"fef5abaa",	"b8d9c75508ad7d021f83a08852d0537110ad233ec4088e2856fdb6e6a7a00549",	"239",	"36",	"38"    ,"1",    "");
	coinParameter[402]=new CoinParameter("BOAT-T",	"BOAT-Testnet",					"33728",	"33882",	"0",	"2d3c0f1e",	"",																	"183",	"55",	"109"   ,"1",    "");
	coinParameter[403]=new CoinParameter("BOLI-T",	"Bolivarcoin-Testnet",			"13893",	"13563",	"0",	"fec4bade",	"000008da0e16960d6c2548da4831323b956d61370e2a3fdc5150188c5c478c49",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[404]=new CoinParameter("BSC-T",	"BowsCoin-Testnet",				"18155",	"18145",	"0",	"ffc4b9de",	"00000344d8f4c75001f0fda455567ec2dd0b7ee1d19cf45d6e3fa15472055b1c",	"255",	"127",	"196"   ,"1",    "");
	coinParameter[405]=new CoinParameter("BSD-T",	"BitSend-Testnet",				"18333",	"18800",	"0",	"0b110907",	"43f9bd8f69fabb104a127606edc72c9a144da46d27ddb0817fb3462021d9e367",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[406]=new CoinParameter("BSR-T",	"BitSoar-Testnet",				"50119",	"40119",	"0",	"a1a0a2a3",	"00000b5911130805e916ccfdbd3d8975381ca7561424d3a453d7f38aa64adae5",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[407]=new CoinParameter("BSTY-T",	"GlobalBoost-Y-Testnet",		"18226",	"18225",	"1",	"",			"2e28050194ad73f2405394d2f081361a23c2df8904ec7f026a018bbe148d5adf",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[408]=new CoinParameter("BTA-T",	"Bata-Testnet",					"33813",	"33812",	"3",	"baadafc5",	"00000a6800e5e2b43515cd12da56861eb482ebcdfa6ee92c1a8d7836ff654500",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[409]=new CoinParameter("BTB-T",	"BitBar-Testnet",				"18777",	"19344",	"0",	"cdf2c0ef",	"00000b03a8fb08c5fc7c876f73c224a964ed79f71276694c3c0a6d288ae57fc3",	"243",	"115",	"130"   ,"1",    "");
	coinParameter[410]=new CoinParameter("BTC-T",	"Bitcoin-Testnet",				"18333",	"18332",	"4",	"0b110907",	"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",	"239",	"111",	"196"   ,"0",    "tb");
	coinParameter[411]=new CoinParameter("BTCD-T",	"BitcoinDark-Testnet",			"25714",	"25715",	"0",	"cdf2c0ef",	"0000a3af4d553378169e82ac59a767b343ad5a4b8acb9c84ca4d3fbd0ae54fee",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[412]=new CoinParameter("BTCP-T",	"BitcoinPrivate-Testnet",		"17933",	"17932",	"2",	"f61bf6d6",	"03e1c4bb705c871bf9bfda3e74b7f8f86bff267993c215a89d5795e3708e5e1f",	"239",	"6487","6624"   ,"1",    "");
	coinParameter[413]=new CoinParameter("BTCR-T",	"Bitcurrency-Testnet",			"25814",	"25815",	"0",	"bca4b0da",	"0000bc53a50f95a98dd38be1ea73577e73f6232ad6483c213575a589ae32f8a0",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[414]=new CoinParameter("BTCZ-T",	"BitcoinZ-Testnet",				"11989",	"11979",	"2",	"fa1af9bf",	"",																	"239",	"7461","7354"   ,"1",    "");
	coinParameter[415]=new CoinParameter("BTDX-T",	"Bitcloud-Testnet",				"51474",	"8329",		"0",	"457665ba",	"000002d56463941c20eae5cb474cc805b646515d18bc7dc222a0885b206eadb0",	"239",	"139",	"19"    ,"1",    "");
	coinParameter[416]=new CoinParameter("BTG-T",	"BitcoinGold-Testnet",			"18338",	"18332",	"3",	"e2486e45",	"00000000e0781ebe24b91eedc293adfea2f557b53ec379e78959de3853e6f9f6",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[417]=new CoinParameter("BTGEM-T",	"Bitgem-Testnet",				"17692",	"18348",	"0",	"cdf2c0ef",	"000003d7b81b08a6fd709d76e9d235605a811b71bef07950a307370a191d419b",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[418]=new CoinParameter("BTX-T",	"Bitcore-Testnet",				"8666",		"50332",	"2",	"fdd2c8f1",	"02c5d66e8edb49984eb743c798bca069466ce457b7febfa3c3a01b33353b7bc6",	"239",	"111",	"196"   ,"1",    "tbtx");
	coinParameter[419]=new CoinParameter("BUB-T",	"Bubble-Testnet",				"38881",	"38882",	"0",	"08121619",	"0000772c897ca2f18846c85ccba7be651cdc5c30c7dfe437a5e1c76dda262e88",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[420]=new CoinParameter("BUMBA-T",	"BumbaCoin-Testnet",			"20202",	"10101",	"0",	"",			"00000e2781c9a29cbd75ee5a69394a950fc72276236d28e04f36d24034a293ae",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[421]=new CoinParameter("BUZZ-T",	"BuzzCoin-Testnet",				"20114",	"20115",	"0",	"1f220530",	"00000a336bf3e2be21c2ce9a3f9bc9849c697475d0de85e201bdc3452f3c343b",	"239",	"127",	"196"   ,"1",    "");
	coinParameter[422]=new CoinParameter("BWK-T",	"Bulwark-Testnet",				"42133",	"42132",	"3",	"b5d9f4a0",	"000001a2f1a9a313468d66b81dd2cb199f6f8f5d426198a7c4daa9c3f9498285",	"239",	"65",	"12"    ,"1",    "");
	coinParameter[423]=new CoinParameter("CANN-T",	"CannabisCoin-Testnet",			"29347",	"29347",	"1",	"fec4bade",	"00000a10f7ce671e773330376ce892a6c0b93fbc05553ebbf659b11e3bf9188d",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[424]=new CoinParameter("CARBON-T","Carboncoin-Testnet",			"19350",	"19351",	"2",	"fcc1b7dc",	"027c03874be8e8c1ead6518fd37f5c416e0eb02d5982919a21a2a7f8b86c80c8",	"241",	"113",	"196"   ,"1",    "");
	coinParameter[425]=new CoinParameter("CAT-T",	"Catcoin-Testnet",				"19933",	"19932",	"1",	"fdcbb8dd",	"ec7987a2ab5225246c5cf9b8d93b4b75bcef383a4a65d5a265bc09ed54006188",	"151",	"23",	"83"    ,"1",    "");
	coinParameter[426]=new CoinParameter("CBX-T",	"Bullion-Testnet",				"51474",	"18395",	"0",	"457665ba",	"0000041e482b9b9691d98eefb48473405c0b8ec31b76df3797c74a78680ef818",	"0",	"111",	"196"   ,"1",    "");
	coinParameter[427]=new CoinParameter("CHAN-T",	"ChanCoin-Testnet",				"29117",	"19332",	"0",	"93973717",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[428]=new CoinParameter("CHEAP-T",	"Cheapcoin-Testnet",			"36647",	"36646",	"0",	"",			"",																	"181",	"53",	"196"   ,"1",    "");
	coinParameter[429]=new CoinParameter("CHESS-T",	"ChessCoin-Testnet",			"17323",	"17324",	"0",	"bf46c174",	"0000048f94311e912681a9a25eb553e4a4d1703689c5f9a264c7b07245c7ff1f",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[430]=new CoinParameter("CHIPS-T",	"CHIPS-Testnet",				"18333",	"18332",	"4",	"0b110907",	"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[431]=new CoinParameter("CJ-T",	"Cryptojacks-Testnet",			"26714",	"26715",	"0",	"cdf2c0ef",	"000004611c87517dfd29fe7f34bd6da2e1ad3d305ac12afe80a3229069390f68",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[432]=new CoinParameter("CLAM-T",	"Clams-Testnet",				"35714",	"35715",	"0",	"c4f1c0df",	"00001924120e93f445dd4adb9d90e0020350b8c6c2b08e1a4950372a37f8bcc8",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[433]=new CoinParameter("CLUB-T",	"ClubCoin-Testnet",				"28114",	"29114",	"0",	"cdf242ef",	"0000fb7faf0608bb189df6bfde6c17b7d9bb337056bb8c97f7973f929b493a4e",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[434]=new CoinParameter("CMPCO-T",	"CampusCoin-Testnet",			"33813",	"33812",	"0",	"bcadafc4",	"b78197f0e175697646db1f738edc1ffdcb30588ebe70e7e16026489076577061",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[435]=new CoinParameter("CNNC-T",	"Cannation-Testnet",			"22367",	"22368",	"0",	"d8889442",	"00000fc248c48d26e8777d9531d501d5ee1ec4d51da539fc4c2ecfff706418be",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[436]=new CoinParameter("CNX-T",	"Cryptonex-Testnet",			"30863",	"30864",	"0",	"4c7951f0",	"00000e20f302fba484374f33aedf9cd4a20073de7e7ac4bdfe473e060c2d11b3",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[437]=new CoinParameter("COLX-T",	"ColossusXT-Testnet",			"51374",	"51475",	"0",	"467766bb",	"6cd37a546cfaafeee652fd0f3a85ba64c0f539f771a27fca9610cdc2f3278932",	"239",	"139",	"19"    ,"1",    "");
	coinParameter[438]=new CoinParameter("CON-T",	"PayCon-Testnet",				"25072",	"25073",	"0",	"70352205",	"00000174b4ac95cc6334f5fe62e951dda9fcf930601df174f2940d990aa1a563",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[439]=new CoinParameter("CPC-T",	"Capricoin-Testnet",			"22715",	"22712",	"0",	"a1a0a2a3",	"00000d23fa0fc52c90893adb1181c9ddffb6c797a3e41864b9a23aa2f2981fe3",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[440]=new CoinParameter("CRC-T",	"CrowdCoin-Testnet",			"13845",	"19998",	"1",	"da24b57a",	"00000a8d0db898c786060f839e63529700bd00e4708b028206a8a60f391566d8",	"0",	"28",	"88"    ,"1",    "");
	coinParameter[441]=new CoinParameter("CRM-T",	"Cream-Testnet",				"16066",	"17077",	"0",	"edc6fe20",	"00000064f9440ba747396d1686dc56a2be9b215245c4ba7974913d65aa8f6dc1",	"239",	"88",	"196"   ,"1",    "");
	coinParameter[442]=new CoinParameter("CRW-T",	"Crown-Testnet",				"19340",	"19341",	"8",	"0f180e06",	"0000000085370d5e122f64f4ab19c68614ff3df78c8d13cb814fd7e69a1dc6da",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[443]=new CoinParameter("CTO-T",	"Crypto-Testnet",				"18899",	"18898",	"1",	"f7c1d4dc",	"1471f84b77a71ff798a39012823edd7d91b11ad7f2b90263a6bbbbe12c4d03fb",	"202",	"74",	"196"   ,"1",    "");
	coinParameter[444]=new CoinParameter("CURE-T",	"Curecoin-Testnet",				"8600",		"18600",	"0",	"cdf2c0ef",	"0000a25934ca63ddc77adf8fe033cfb5f847e5687943ad717bb95ff041a74eb7",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[445]=new CoinParameter("CYDER-T",	"Cyder-Testnet",				"48847",	"48846",	"0",	"",			"",																	"154",	"26",	"196"   ,"1",    "");
	coinParameter[446]=new CoinParameter("DASH-T",	"Dash-Testnet",					"19999",	"19998",	"2",	"cee2caff",	"00000bafbc94add76cb75e2ec92894837288a481e5c005f6563d91623bf8bc2c",	"239",	"140",	"19"    ,"1",    "");
	coinParameter[447]=new CoinParameter("DCR-T",	"DecredTestnet",				"19108",	"19109",	"4",	"48e7a065",	"5b7466edf6739adc9b32aaedc54e24bdc59a05f0ced855088835fe3cbe58375f",	"8974",	"3873","3836"   ,"1",    "");
	coinParameter[448]=new CoinParameter("DEM-T",	"DeutscheeMark-Testnet",		"15556",	"16666",	"0",	"fec3b9de",	"0000fa2783bec51b4039b29d61bd2bff61be74cdd4404f582b5b3f8fd3d15921",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[449]=new CoinParameter("DGB-T",	"DigiByte-Testnet",				"12025",	"18332",	"4",	"fdc8bddd",	"308ea0711d5763be2995670dd9ca9872753561285a84da1d58be58acaa822252",	"254",	"126",	"140"   ,"1",    "dgbt");
	coinParameter[450]=new CoinParameter("DGC-T",	"Digitalcoin-Testnet",			"17999",	"17998","2","cee2cafffbc0b6db",	"9dc0f1255caf75497a73dd0183d06b45dbe23bdfc90f67a2fb124a19be1a4cde",	"128",	"30",	"5"     ,"1",    "");
	coinParameter[451]=new CoinParameter("DIME-T",	"Dimecoin-Testnet",				"21931",	"11930",	"0",	"011A39F7",	"e5524c7f4b08e6a04689a551fa060d9e39934991d5a4111105d9359447733285",	"247",	"119",	"199"   ,"1",    "");
	coinParameter[452]=new CoinParameter("DLC-T",	"Dollarcoin-Testnet",			"18145",	"18146",	"0",	"d5acae18",	"0000000013f91db600d56b852497767c013c2b5a4e62a182bd4a5ad6c93c732e",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[453]=new CoinParameter("DMB-T",	"DigitalMoneyBits-Testnet",		"32097",	"32098",	"0",	"2eea4eee",	"3f4ce71618777f85a62fcba786e80273b1bc0224a851d59f3ff086200c61f145",	"239",	"75",	"196"   ,"1",    "");
	coinParameter[454]=new CoinParameter("DMD-T",	"Diamond-Testnet",				"51474",	"17772",	"1",	"457665ba",	"0000029b550c0095513d9bb9dd14f88442573baca94d70e49018a510979c0f9b",	"239",	"139",	"19"    ,"1",    "");
	coinParameter[455]=new CoinParameter("DNR-T",	"Denarius-Testnet",				"33338",	"32338",	"0",	"0711050b",	"000086bfe8264d241f7f8e5393f747784b8ca2aa98bdd066278d590462a4fdb4",	"146",	"18",	"116"   ,"1",    "");
	coinParameter[456]=new CoinParameter("DOGE-T",	"Dogecoin-Testnet",				"44556",	"44555",	"1",	"fcc1b7dc",	"bb0a78264637406b6360aad926284d544d7049f45189db5664f3c4d07350559e",	"241",	"113",	"196"   ,"1",    "");
	coinParameter[457]=new CoinParameter("DOLLAR-T","DollarOnline-Testnet",			"19666",	"19666",	"0",	"a1a0a2a3",	"00000d69d6d9bb99b8d58438e238f58660c920b70b5e7f7ff9f68ea3835698fb",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[458]=new CoinParameter("DRXNE-T",	"DROXNE-Testnet",				"17778",	"18345",	"0",	"cdf2c0ef",	"0000c763e402f2436da9ed36c7286f62c3f6e5dbafce9ff289bd43d7459327eb",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[459]=new CoinParameter("ECN-T",	"E-coin-Testnet",				"17778",	"18345",	"0",	"cdf2c0ef",	"0000c763e402f2436da9ed36c7286f62c3f6e5dbafce9ff289bd43d7459327eb",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[460]=new CoinParameter("EGC-T",	"EverGreenCoin-Testnet",		"15757",	"15758",	"0",	"21246247",	"00002423cec5e6f756042ad0d08c416075de6aa5e92c883ee12504c24e83bb7d",	"221",	"93",	"196"   ,"1",    "");
	coinParameter[461]=new CoinParameter("EGG-T",	"EggCoin-Testnet",				"20134",	"20135",	"0",	"2a230640",	"00003d1bc115df47810ee300c9bf0b3943ee139d2863c384a3ac0d6dd07e3772",	"239",	"127",	"196"   ,"1",    "");
	coinParameter[462]=new CoinParameter("EMB-T",	"EmberCoin-Testnet",			"10012",	"10018",	"0",	"121a52b4",	"000067596509a7006e9d4d0978441e23ff5e18903b127a4a975833fff92b7b99",	"51",	"93",	"111"   ,"1",    "");
	coinParameter[463]=new CoinParameter("EMC-T",	"Emercoin-Testnet",				"6663",		"6662",		"1",	"cbf2c0ef",	"0000000810da236a5c9239aa1c49ab971de289dbd41d08c4120fc9c8920d2212",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[464]=new CoinParameter("EMC2-T",	"Einsteinium-Testnet",			"31878",	"31879",	"1",	"faa2f0c1",	"a4271888b5e60092c3e7183a76d454741e9a7a55f2b4afbe574615829e406bee",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[465]=new CoinParameter("EMD-T",	"EmeraldCrypto-Testnet",		"22127",	"112128",	"5",	"fcc1b7dc",	"893f3509f074447e18e387f173a9d1773df4089a6121023a669c499b588869c9",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[466]=new CoinParameter("ENRG-T",	"Energycoin-Testnet",			"22709",	"22705",	"0",	"ccf0c1ed",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[467]=new CoinParameter("ENT-T",	"Eternity-Testnet",				"14855",	"19998",	"0",	"c3b3ea5b",	"000004be80c8a589024cb741114475d7d83ab0473c85d6131443f67f8d6f3f30",	"239",	"93",	"10"    ,"1",    "");
	coinParameter[468]=new CoinParameter("EQT-T",	"EquiTrader-Testnet",			"17778",	"18345",	"0",	"cdf2c0ef",	"0000022908b074b5f1d1164772d32b9385e285ad9ad5c84009973b442038fa82",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[469]=new CoinParameter("ERC-T",	"EuropeCoin-Testnet",			"8989",		"18332",	"0",	"4555524f",	"0000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f494385",	"41",	"33",	"5"     ,"1",    "");
	coinParameter[470]=new CoinParameter("ETH-T",	"Ethereum-Testnet",				"30303",	"8080",		"0",	"",			"41941023680923e0fe4d74a34bdac8141f2540e3ae90623718e47d66d1ca4a2d",	"",		"",		""      ,"1",    "");
	coinParameter[471]=new CoinParameter("EVIL-T",	"EvilCoin-Testnet",				"12347",	"12347",	"0",	"a1a0a2a3",	"000008056bcc711afe7cdb1ff527cddfa981c7ef10883a9da0d5b1eb68a7a949",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[472]=new CoinParameter("FAIR-T",	"FairCoin-Testnet",				"18392",	"18393",	"0",	"cdf2c0ef",	"5df7281a262d9a6251e65d12bdb78c6a69041c7d28e209df2a7719059de3088c",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[473]=new CoinParameter("FGC-T",	"FantasyGold-Testnet",			"58806",	"58804",	"3",	"41023564",	"000005b218ee50a90d18144376a07d8fa5e2477b234c1a7df54fa29229ecf96c",	"239",	"65",	"12"    ,"1",    "");
	coinParameter[474]=new CoinParameter("FJC-T",	"FujiCoin-Testnet",				"13777",	"13776",	"1",	"696a7566",	"96bd214d68bcbfe9c786c4da26cf71fb6bbb6f24032065bdf2f4cd2b003d9c72",	"202",	"74",	"196"   ,"1",    "tf");
	coinParameter[475]=new CoinParameter("FLO-T",	"FlorinCoin-Testnet",			"17312",	"17313",	"1",	"fdc05af2",	"9b7bc86236c34b5e3a39367c036b7fe8807a966c22a7a1f0da2a198a27e03731",	"239",	"115",	"198"   ,"1",    "");
	coinParameter[476]=new CoinParameter("FLT-T",	"FlutterCoin-Testnet",			"17408",	"17474",	"0",	"cdf2c0ef",	"000074e6ba00a73a1ab0a802b1665107690a23d2b1b2363a83f102aaa78a8708",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[477]=new CoinParameter("FRC-T",	"Freicoin-Testnet",				"18639",	"18638",	"1",	"5ed67cf3",	"00000000a52504ffe3420a43bd385ef24f81838921a903460b235d95f37cd65e",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[478]=new CoinParameter("FST-T",	"Fastcoin-Testnet",				"19526",	"19527",	"3",	"fcc1b7dc",	"f5ae71e26c74beacc88382716aced69cddf3dffff24f384e1808905e0188f68f",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[479]=new CoinParameter("FTC-T",	"Feathercoin-Testnet",			"19336",	"0",		"2",	"91656a71",	"7734b3734ab1f0d0758e6c274622a377092549df05f6a4fe6939cbc754939169",	"239",	"111",	"196"   ,"1",    "tf");
	coinParameter[480]=new CoinParameter("FTO-T",	"FuturoCoin-Testnet",			"19009",	"19008",	"2",	"D4D2D4C6",	"00000bb3fa6e7040e32c0f8e8218e928c37e177139b62ae1daba803e0543c175",	"223",	"95",	"17"    ,"1",    "");
	coinParameter[481]=new CoinParameter("FUNK-T",	"TheCypherfunks-Testnet",		"34666",	"33666",	"1",	"fdc4bbe3",	"8da4b127dd8a5317d2165690c97ecb5947839e7cf106c1e762728e5b24cfcde4",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[482]=new CoinParameter("GAIN-T",	"UGAIN-Testnet",				"17891",	"17892",	"0",	"8bcfc39a",	"0000000031bc418b96e5bd9a47b9b1f786a02234475cbb472d50855a93a421d1",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[483]=new CoinParameter("GAM-T",	"Gambit-Testnet",				"47078",	"47178",	"0",	"0a02080f",	"00000a6b3167cd44779db506b85e84144b6ac0a5c2b1f2ce3e5ebb0ba9306fec",	"228",	"100",	"128"   ,"1",    "");
	coinParameter[484]=new CoinParameter("GAME-T",	"GameCredits-Testnet",			"50001",	"50000",	"2",	"0b110907",	"153b15fb136894fc92041175a59038d4b155b22d40d2885a7996f170d95c1d20",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[485]=new CoinParameter("GB-T",	"GoldBlocks-Testnet",			"27921",	"26921",	"0",	"d0ac3412",	"000005fc99547a646f2273caa7a1f407ebd885ad89e0b019aa0a70c353598f38",	"239",	"97",	"196"   ,"1",    "");
	coinParameter[486]=new CoinParameter("GBX-T",	"GoByte-Testnet",				"13455",	"13454",	"2",	"d12bb37a",	"00000c958ba1a0fe2174effe57a7f39c4c32b8341f1efa20be78b48b6b6bb353",	"240",	"112",	"20"    ,"1",    "");
	coinParameter[487]=new CoinParameter("GEERT-T",	"GeertCoin-Testnet",			"65333",	"65332",	"1",	"f9d3e4ce",	"01e0ab8f22808260eae3e54c645d1dc137f3e06530b8232d4eee4eb6e4b02065",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[488]=new CoinParameter("GIN-T",	"GINcoin-Testnet",				"12111",	"12211",	"2",	"cee2caff",	"00000ac0100fe889e7130e47d13819cc91eba5646dc1e8fc050c7a67532565ed",	"239",	"140",	"19"    ,"1",    "");
	coinParameter[489]=new CoinParameter("GLD-T",	"GoldCoin-Testnet",				"19335",	"19332",	"3",	"fdd2c8f1",	"4966625a4b2851d9fdee139e56211a0d88575f59ed816ff5e6a63deb4e3e29a0",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[490]=new CoinParameter("GLT-T",	"GlobalToken-Testnet",			"19319",	"19320",	"0",	"3a6f375b",	"00000000fe3e3e93344a6b73888137397413eb11f601b4231b5196390d24d3b6",	"239",	"111",	"196"   ,"1",    "tg");
	coinParameter[491]=new CoinParameter("GPL-T",	"GoldPressedLatinum-Testnet",	"33635",	"33645",	"0",	"cdf2c0ef",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[492]=new CoinParameter("GRC-T",	"GridCoin-Testnet",				"32748",	"25715",	"0",	"cdf2c0ef",	"00006e037d7b84104208ecf2a8638d23149d712ea810da604ee2f2cb39bae713",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[493]=new CoinParameter("GRIM-T",	"Grimcoin-Testnet",				"34861",	"34862",	"0",	"2b3ad4dc",	"00000733223215fa8ba50ae21c4b0142b773ceb8c9c912375156efd51dd09823",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[494]=new CoinParameter("GRLC-T",	"Garlicoin-Testnet",			"42075",	"42070",	"2",	"fdd2c8f2",	"b25004ec2dc27689999f9ee14856ee0464d7f73f3fae3cf43c1d40f89c141aea",	"239",	"111",	"58"    ,"1",    "tgrlc");
	coinParameter[495]=new CoinParameter("GRN-T",	"Granite-Testnet",				"22777",	"22776",	"1",	"fec4bade",	"0000048163d0f3a7c442828a87228277fb102e059e14ea22b3968e145ead7480",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[496]=new CoinParameter("GRS-T",	"Groestlcoin-Testnet",			"18333",	"17766",	"4",	"0b110907",	"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",	"239",	"111",	"196"   ,"1",    "tb");
	coinParameter[497]=new CoinParameter("GSR-T",	"GeyserCoin-Testnet",			"20556",	"20555",	"0",	"adf4d0ac",	"00002a22a9993977bc613c110e298131d808ae2923e42714547841e5b8bd436b",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[498]=new CoinParameter("GTC-T",	"GlobalTourCoin-Testnet",		"33813",	"33812",	"0",	"bcadafc4",	"b78197f0e175697646db1f738edc1ffdcb30588ebe70e7e16026489076577061",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[499]=new CoinParameter("HAL-T",	"Halcyon-Testnet",				"11108",	"35615",	"0",	"fdf2f0df",	"9a9e20c1cc8dc1297fc4842e1b358a576db6c985f6dc4a6b8cf16f36f69aa54d",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[500]=new CoinParameter("HALLO-T",	"HalloweenCoin-Testnet",		"65889",	"65889",	"0",	"a51c23b5",	"00000bef71c5ffeb052afd75f793fa4eb6648db14c124db90390b8c36c20be28",	"166",	"38",	"196"   ,"1",    "");
	coinParameter[501]=new CoinParameter("HBN-T",	"HoboNickels-Testnet",			"17372",	"17373",	"0",	"cdf2c0ef",	"0000f9e0292f278190e4d58cd1e1e9a32b7466c8092bd2371ffc80b06f8eca4a",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[502]=new CoinParameter("HC-T",	"HarvestMasternodeCoin-Testnet","20114",	"20115",	"0",	"1d7ea62c",	"0000ebc8051bff80f7946f4420efb219e66f66b89fdc1df0ed8a30b428bf0033",	"239",	"127",	"196"   ,"1",    "");
	coinParameter[503]=new CoinParameter("HOLD-T",	"InterstellarHoldings-Testnet",	"15130",	"15131",	"0",	"bc45ec12",	"00008783573601e55e38841fb2bcaf3bf52c323bc36190d19ce2ccb42b2c106c",	"153",	"40",	"100"   ,"1",    "");
	coinParameter[504]=new CoinParameter("HPC-T",	"Happycoin-Testnet",			"17778",	"18345",	"0",	"cdf2c0ef",	"0000c763e402f2436da9ed36c7286f62c3f6e5dbafce9ff289bd43d7459327eb",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[505]=new CoinParameter("HTML-T",	"HTMLCOIN-Testnet",				"14888",	"14889",	"1",	"2f3e4d5c",	"000013694772f8aeb88efeb2829fe5d71fbca3e23d5043baa770726f204f528c",	"228",	"100",	"110"   ,"1",    "");
	coinParameter[506]=new CoinParameter("HVCO-T",	"HighVoltage-Testnet",			"47822",	"47821",	"0",	"b1b6b1b6",	"0000d78f6443e99c5cb56a0ef110f8cf0b28da89b3cc3ee0cbacfb10d6196872",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[507]=new CoinParameter("HWC-T",	"HollyWoodCoin-Testnet",		"20267",	"21030",	"0",	"6c4e7880",	"00000418c55096b5ffaad25266162c24c8c51241cbdc16b32d53311f65dbbc38",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[508]=new CoinParameter("HYP-T",	"HyperStake-Testnet",			"28775",	"28776",	"0",	"dd4ddd4d",	"534d8009c099b04d05d7475f48eea977ca2fedaf409e233c884eff34d2efdb8e",	"237",	"109",	"196"   ,"1",    "");
	coinParameter[509]=new CoinParameter("HYPER-T",	"Hyper-Testnet",				"11199",	"11200",	"0",	"cdf1c0ef",	"0000024551a7ceb01f17bcfee40f05e18b8f83c0fd49f2fbafc9bf95b77614f2",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[510]=new CoinParameter("I0C-T",	"I0Coin-Testnet",				"17333",	"17332",	"0",	"0b110907",	"",																	"239",	"112",	"196"   ,"1",    "");
	coinParameter[511]=new CoinParameter("IBANK-T",	"iBank-Testnet",				"17619",	"17620",	"0",	"c11a5884",	"00000966ef8bbe4f8b00bd3267ab8a42c51f5ec45e04f892bf52b7a39b67ea90",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[512]=new CoinParameter("ICON-T",	"Iconic-Testnet",				"47424",	"47423",	"0",	"a3a5b3b5",	"00009ae2655bef17fbf0fcfe87dc8702c5ac139bfbd83d24ef749efaa07a4e09",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[513]=new CoinParameter("IFC-T",	"Infinitecoin-Testnet",			"19321",	"9322",		"0",	"fcc1b7dc",	"47cfc1ea5d27a873dcc90fa8befc9378dbc793ca68dd608d4f6aa123437701ba",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[514]=new CoinParameter("IFLT-T",	"InflationCoin-Testnet",		"21370",	"21371",	"0",	"a1a0a2a3",	"000001d598aef26e66fe34d3027ed1a88d49a01a5955d5b5c24088130cc8a993",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[515]=new CoinParameter("IMS-T",	"IndependentMoneySystem-Testnet","16177",	"16178",	"0",	"da5e9d5e",	"0000724595fb3b9609d441cbfb9577615c292abf07d996d3edabc48de843642d",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[516]=new CoinParameter("IMX-T",	"Impact-Testnet",				"22628",	"21528",	"0",	"",			"",																	"220",	"92",	"146"   ,"1",    "");
	coinParameter[517]=new CoinParameter("INFX-T",	"Influxcoin-Testnet",			"17778",	"18345",	"0",	"ccf2c18f",	"0000a3702fcced20983244e54290842fc199faf457863913ef7e631b6732a8d3",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[518]=new CoinParameter("INN-T",	"Innova-Testnet",				"15520",	"18818",	"0",	"b1a4d57c",	"0000042893def7fbac13873db544dc5924fb083f17701255fcf85ec03936f388",	"240",	"112",	"10"    ,"1",    "");
	coinParameter[519]=new CoinParameter("IOC-T",	"I/OCoin-Testnet",				"43764",	"43765",	"0",	"ffc4bbdf",	"5f97300cd3dc3d2215dd38ce6d99bf7d5984bb62b2777060d3b5564298bd5484",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[520]=new CoinParameter("ION-T",	"ION-Testnet",					"27170",	"27171",	"2",	"db86fc69",	"0000002bed128b6b2a62bd8edd4e6f8a414eac38e256aa0194adb8c93fe18132",	"239",	"97",	"196"   ,"1",    "");
	coinParameter[521]=new CoinParameter("IRL-T",	"IrishCoin-Testnet",			"11375",	"11375",	"2",	"fcc1b7dc",	"f5ae71e26c74beacc88382716aced69cddf3dffff24f384e1808905e0188f68f",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[522]=new CoinParameter("ISL-T",	"IslaCoin-Testnet",				"9733",		"9833",		"0",	"a1a0a2a3",	"0000055bac210396d3152abfed2d8acf5d24da974d0349575330b8331a4b710f",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[523]=new CoinParameter("ITI-T",	"iTicoin-Testnet",				"52177",	"52144",	"0",	"cdf2c0ef",	"00006d231ae9d3a12200a0cee284140dcf2151e5c171600f04c6762730bd963e",	"255",	"127",	"196"   ,"1",    "");
	coinParameter[524]=new CoinParameter("IXC-T",	"Ixcoin-Testnet",				"18333",	"18338",	"4",	"0b110907",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[525]=new CoinParameter("KEK-T",	"KekCoin-Testnet",				"13777",	"11337",	"0",	"55667788",	"0000c7b67a057053c5043fad3ae7896f3d3172361ba4a850abb24f6dd80df5dc",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[526]=new CoinParameter("KMD-T",	"Komodo-Testnet",				"17770",	"17771",	"1",	"5A1F7E62",	"05a60a92d99d85997cce3b87616c089f6124d7342af37106edc76126334a2c38",	"128",	"0",	"5"     ,"1",    "");
	coinParameter[527]=new CoinParameter("KNC-T",	"KingNCoin-Testnet",			"28373",	"28374",	"0",	"5c0ff6b8",	"000004abf1a9742cec3abdb8e189a75b61c4b6ebd93c248876b838bd5d6d7eb4",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[528]=new CoinParameter("KOBO-T",	"Kobocoin-Testnet",				"19011",	"13341",	"0",	"a1a0a2a3",	"0000d20b7549d1457039ebc7d0c8ea5f6d6c8c2c811f898d5530bfeb70fcb06f",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[529]=new CoinParameter("KUSH-T",	"KushCoin-Testnet",				"17778",	"18345",	"0",	"cdf2c0ef",	"0000c763e402f2436da9ed36c7286f62c3f6e5dbafce9ff289bd43d7459327eb",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[530]=new CoinParameter("LANA-T",	"LanaCoin-Testnet",				"17506",	"15706",	"2",	"cccbd27f",	"00005f78276904fdf0cbd61b10b5330e915362c2457173d443ad46e08fa621ef",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[531]=new CoinParameter("LBC-T",	"LBRYCredits-Testnet",			"19246",	"19245",	"2",	"fae4aae1",	"9c89283ba0f3227f6c03b70216b9f665f0118d5e0fa729cedf4fb34d6a34f463",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[532]=new CoinParameter("LBTC-T",	"LiteBitcoin-Testnet",			"19335",	"19332",	"0",	"aec2b1f5",	"c3a67cf4b8f9782ac27e82610b4131fe0fe4ddfef07c7f7532643e841ef17f28",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[533]=new CoinParameter("LCC-T",	"LitecoinCash-Testnet",			"19335",	"62455",	"0",	"b6f5d3cf",	"4966625a4b2851d9fdee139e56211a0d88575f59ed816ff5e6a63deb4e3e29a0",	"239",	"111",	"196"   ,"1",    "tlcc");
	coinParameter[534]=new CoinParameter("LCP-T",	"LitecoinPlus-Testnet",			"44352",	"44352",	"0",	"cdf1c0ef",	"000007ca97a92891d88e204cfe475193c9e68fa80d94b97796ba9bcd15e3301c",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[535]=new CoinParameter("LEO-T",	"LEOcoin-Testnet",				"15840",	"15211",	"0",	"8c96a0aa",	"0000fd740a38215055ebf8cff390775524f03b7450673736a733330ebbd424d9",	"194",	"66",	"76"    ,"1",    "");
	coinParameter[536]=new CoinParameter("LINDA-T",	"Linda-Testnet",				"28888",	"28889",	"0",	"cef2c0ef",	"00005862693a9c2692d3abb4d071f261c1c64db9824d89ae28d533e435f6d87c",	"239",	"23",	"187"   ,"1",    "");
	coinParameter[537]=new CoinParameter("LIR-T",	"LetItRide-Testnet",			"3717",		"3718",		"0",	"efc31e07",	"79ddad49620b44108df81c845b676802214cf9bee54c9a6b69c0f071092ae405",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[538]=new CoinParameter("LOG-T",	"Woodcoin-Testnet",				"18338",	"19332",	"1",	"fcd9b7ddfcc1b7dc",															"",	"136",	"135",	"8"     ,"1",    "");
	coinParameter[539]=new CoinParameter("LTC-T",	"Litecoin-Testnet",				"19335",	"19332",	"3",	"fdd2c8f1",	"4966625a4b2851d9fdee139e56211a0d88575f59ed816ff5e6a63deb4e3e29a0",	"239",	"111",	"196"   ,"1",    "tltc");
	coinParameter[540]=new CoinParameter("LUX-T",	"LUXCoin-Testnet",				"28333",	"9777",		"0",	"546751ab",	"00000b773a72afd051c6fe34c6d9c8e1ba78b1556263c807a1ca7d7a200cda82",	"155",	"48",	"63"    ,"1",    "tb");
	coinParameter[541]=new CoinParameter("MAC-T",	"Machinecoin-Testnet",			"50333",	"50332",	"3",	"fbc0b6db",	"72059c481cc49a2941cc36bd0f070abfe1ccc6e329534602dbdef555547e895f",	"178",	"53",	"5"     ,"1",    "tmc");
	coinParameter[542]=new CoinParameter("MAGN-T",	"Magnetcoin-Testnet",			"11650",	"11655",	"0",	"1a6f2a4e",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[543]=new CoinParameter("MAO-T",	"MaoZedong-Testnet",			"19670",	"19669",	"0",	"d73dac32",	"00000f83c8a7fb7ba26343cc23a0ae8a1f0cfb54e6cdf55050bd6a947e7aa456",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[544]=new CoinParameter("MARX-T",	"MarxCoin-Testnet",				"141103",	"141102",	"0",	"fec4bade",	"000008da0e16960d6c2548da4831323b956d61370e2a3fdc5150188c5c478c49",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[545]=new CoinParameter("MAX-T",	"MaxCoin-Testnet",				"18668",	"18669",	"0",	"0b11bb07",	"0000000a23e3eb42dc87b61d4015c80ffd85471d34e2c7210c7ca63b78a58612",	"239",	"127",	"130"   ,"1",    "");
	coinParameter[546]=new CoinParameter("MAY-T",	"TheresaMayCoin-Testnet",		"25714",	"25715",	"0",	"adf1c2af",	"00000056b82939ea6dbaa22632c4ff5324f2b2c574286af11a380bdb40eda62d",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[547]=new CoinParameter("MAZA-T",	"MAZA-Testnet",					"11835",	"11832",	"1",	"05fea901",	"000003ae7f631de18a457fa4fa078e6fa8aff38e258458f8189810de5d62cede",	"239",	"88",	"188"   ,"1",    "");
	coinParameter[548]=new CoinParameter("MCRN-T",	"MACRON-Testnet",				"25714",	"25715",	"0",	"adf1c2af",	"0000008a544bd2fd7451b3eb9a88e7fbd1d765dcbcbe1cd1122fd0628ea35ed4",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[549]=new CoinParameter("MEC-T",	"Megacoin-Testnet",				"19444",	"50732",	"2",	"fdf0f4fe",	"7520788e2d99eec7cf6cf7315577e1268e177fff94cb0a7caf6a458ceeea9ac2",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[550]=new CoinParameter("MEDIC-T",	"MedicCoin-Testnet",			"12118",	"12117",	"0",	"1bd76fc9",	"0000ac3f71507372db48ac2626bed6c1321cade7292431b274647673ab60a7ee",	"239",	"127",	"196"   ,"1",    "");
	coinParameter[551]=new CoinParameter("MEME-T",	"Memetic/PepeCoin-Testnet",		"39377",	"39376",	"0",	"2bca3c3f",	"00008cae6a01358d774087e2daf3b2108252b0b5a440195ffec4fd38f9892272",	"239",	"55",	"196"   ,"1",    "");
	coinParameter[552]=new CoinParameter("METAL-T",	"MetalCoin-Testnet",			"22330",	"22331",	"0",	"a1a0a2a3",	"00004058932bc1698ce2d0cb1446d5bbf59414fa2d9e0d51f44007a229292510",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[553]=new CoinParameter("MINT-T",	"Mintcoin-Testnet",				"22788",	"22789",	"0",	"cdf2c0ef",	"af4ac34e7ef10a08fe2ba692eb9a9c08cf7e89fcf352f9ea6f0fd73ba3e5d03c",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[554]=new CoinParameter("MLM-T",	"MktCoin-Testnet",				"19275",	"19276",	"1",	"0b110907",	"247b208ebeb1ecd60df2c2884fa39c4e13ac17da56efdf0348e036363697ee75",	"238",	"110",	"115"   ,"1",    "");
	coinParameter[555]=new CoinParameter("MNM-T",	"Mineum-Testnet",				"31317",	"31317",	"0",	"70352205",	"00",																"239",	"111",	"196"   ,"1",    "");
	coinParameter[556]=new CoinParameter("MNX-T",	"MinexCoin-Testnet",			"8336",		"17788",	"0",	"544a4c54",	"6ef2e897f7acb347086f9860b2ad401f133fe1b103f77de771aac7b5e88cfe70",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[557]=new CoinParameter("MOJO-T",	"MojoCoin-Testnet",				"19495",	"19496",	"0",	"cef1c6e3",	"00000647de02bbb1b56c568085024fbb79e2f27dba79ac20477fb2caae1a610d",	"239",	"97",	"196"   ,"1",    "");
	coinParameter[558]=new CoinParameter("MONA-T",	"MonaCoin-Testnet",				"19403",	"19402",	"1",	"fdd2c8f1",	"a2b106ceba3be0c6d097b2a6a6aacf9d638ba8258ae478158f449c321061e0b2",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[559]=new CoinParameter("MONK-T",	"MonkeyProject-Testnet",		"8711",		"8102",		"0",	"2cab21c3",	"00000faeefd0e5c19a761b91689dc00e46a43974faf3c82d75bf4f57a771d9bc",	"63",	"127",	"196"   ,"1",    "");
	coinParameter[560]=new CoinParameter("MOON-T",	"Mooncoin-Testnet",				"19335",	"144663",	"0",	"fdd2c8f1",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[561]=new CoinParameter("MST-T",	"MustangCoin-Testnet",			"19666",	"19666",	"0",	"a1a0a2a3",	"000007a1a66bbb8c2147bd2d06bca3463d588655c326051b63f12d6130844f5f",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[562]=new CoinParameter("MTNC-T",	"Masternodecoin-Testnet",		"11111",	"11110",	"0",	"f2aee7e4",	"00000016046213f1d00cf060b74be707ee78cf9fadb6e5717935295c1ca3d9a4",	"239",	"97",	"196"   ,"1",    "");
	coinParameter[563]=new CoinParameter("MUE-T",	"MonetaryUnit-Testnet",			"18683",	"28683",	"4",	"bda3c8b1",	"",																	"64",	"38",	"19"    ,"1",    "");
	coinParameter[564]=new CoinParameter("MXT-T",	"MarteXcoin-Testnet",			"41315",	"41314",	"0",	"70352205",	"be10a5eb2ff7c7f2c958826bc773b05748cf3c8c851744425e1af311ed36e502",	"324",	"111",	"196"   ,"1",    "");
	coinParameter[565]=new CoinParameter("NAV-T",	"NavCoin-Testnet",				"15556",	"44445",	"2",	"3fa25220",	"0000a8003f8dd50820bd7885af42dd63d7be0c39c0f8433b3e9397a6ce7d7a5c",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[566]=new CoinParameter("NEVA-T",	"NevaCoin-Testnet",				"17391",	"13791",	"1",	"ac43fe8c",	"005a03c21a8fd3cec753cd5d8bcb0b8b18071ee65f8c77af13e671c1bce771f4",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[567]=new CoinParameter("NLC2-T",	"NoLimitCoin-Testnet",			"16521",	"16520",	"0",	"b1b6b1b6",	"0000834807c7b0f6e8772e7bee9b7cd3524e1211c196386f41b3fc20243b0dfa",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[568]=new CoinParameter("NLX-T",	"Nullex-Testnet",				"16897",	"16898",	"0",	"4671f75e",	"00000e1a057a1f4ede09d189c448ec56b260eb2a6c4744d46352f97ad8be838e",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[569]=new CoinParameter("NMC-T",	"Namecoin-Testnet",				"18334",	"18336",	"1",	"fabfb5fe",	"00000007199508e34a9ff81e6ec0c477a4cccff2a4767a8eee39c11db367b008",	"239",	"111",	"196"   ,"1",    "tn");
	coinParameter[570]=new CoinParameter("NMS-T",	"Numus-Testnet",				"27121",	"27122",	"0",	"1f220530",	"0711d3aa8846a5a90ab56f0653e066351080f095661c573a45cb94737a82c37c",	"50",	"45",	"44"    ,"1",    "");
	coinParameter[571]=new CoinParameter("NUMUS-T",	"NumusCash-Testnet",			"31139",	"31140",	"0",	"2b02a9fa",	"00000e2bbd11364371c9c5b22d84fa7f6e6807dd86c52356daba0c1fcde76567",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[572]=new CoinParameter("NVC-T",	"Novacoin-Testnet",				"17777",	"18344",	"0",	"cdf2c0ef",	"0000c763e402f2436da9ed36c7286f62c3f6e5dbafce9ff289bd43d7459327eb",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[573]=new CoinParameter("NYC-T",	"new YorkCoin-Testnet",			"27020",	"118823",	"0",	"acb1c5dc",	"24463e4d3c625b0a9059f309044c2cf0d7e196cf2a6ecce901f24f681be33c8f",	"241",	"113",	"196"   ,"1",    "");
	coinParameter[574]=new CoinParameter("OCC-T",	"OctoinCoin-Testnet",			"25443",	"18332",	"3",	"61746263",	"000059ccb73d95cf54b1ab4f23a1d1821f7ebf3246b9f125562bb78ddc254323",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[575]=new CoinParameter("ODN-T",	"Obsidian-Testnet",				"26178",	"26174",	"0",	"71312111",	"00000022fa47e5dcb5f9279a8e8631bf42da1f0b7076b9e8e6206549c82d5a44",	"0",	"115",	"196"   ,"1",    "");
	coinParameter[576]=new CoinParameter("OK-T",	"OKCash-Testnet",				"7980",		"7979",		"0",	"00097a0f",	"0000e3283629707a14a6c5f3297995095ac0e337b2af9bca1358d4788ed86169",	"196",	"68",	"73"    ,"1",    "");
	coinParameter[577]=new CoinParameter("OMC-T",	"Omicron-Testnet",				"18519",	"18520",	"0",	"151c9d41",	"000003f746e0a666c19ab4eecd597b492ade4d0c3e4d550014372f2f2e2eade2",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[578]=new CoinParameter("ONION-T",	"DeepOnion-Testnet",			"26550",	"28580",	"0",	"a1a0a2f3",	"0000024e03c36fa1727092fd3093fc8526a126bd4607e3619fb04d3ab782fa0e",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[579]=new CoinParameter("OPC-T",	"OPCoin-Testnet",				"15555",	"15557",	"0",	"75ea1361",	"000093fe59ae8a91008a91b1c10f2ca313bcc9656776c79d84bb4c9ccc26cf80",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[580]=new CoinParameter("ORE-T",	"Galactrum-Testnet",			"16270",	"16269",	"1",	"b1ded0ab",	"00000c0db74e7ed874ef2ad35c2401352326c1b4c58f5b7a5eaa2c22cac5c353",	"239",	"140",	"19"    ,"1",    "");
	coinParameter[581]=new CoinParameter("PAK-T",	"Pakcoin-Testnet",				"17867",	"17866",	"0",	"fcc1b7dc",	"48ce59ea60dec70e478a07e2362f1bade9d7c025072a301121fcf3fd9ff84933",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[582]=new CoinParameter("PART-T",	"Particl-Testnet",				"51938",	"0",		"2",	"0811050b",	"0000594ada5310b367443ee0afd4fa3d0bbd5850ea4e33cdc7d6a904a7ec7c90",	"46",	"0",	"0"     ,"1",    "tb");
	coinParameter[583]=new CoinParameter("PHR-T",	"Phore-Testnet",				"11773",	"11774",	"0",	"477665ba",	"fab709a0c107fe7cf6b0d552c514ef3228f9e0f107cd3c9b2fcea96512342cd8",	"239",	"139",	"19"    ,"1",    "tp");
	coinParameter[584]=new CoinParameter("PHS-T",	"PhilosopherStones-Testnet",	"26281",	"26282",	"0",	"cdf2c0ef",	"3c2ba73eae76c802be5e7e2da8316268b63b0335365eaa8dff76cc1d76662533",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[585]=new CoinParameter("PIGGY-T",	"Piggycoin-Testnet",			"34732",	"34731",	"0",	"a1a0a2a3",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[586]=new CoinParameter("PINK-T",	"PinkCoin-Testnet",				"19134",	"19135",	"0",	"0204050d",	"000076a007b949e5f8cdee6c18817d26bc224bfde575ce3f2ecb0dd000f7ec19",	"183",	"55",	"196"   ,"1",    "");
	coinParameter[587]=new CoinParameter("PIVX-T",	"PIVX-Testnet",					"51474",	"51475",	"3",	"457665ba",	"0000041e482b9b9691d98eefb48473405c0b8ec31b76df3797c74a78680ef818",	"239",	"139",	"19"    ,"1",    "");
	coinParameter[588]=new CoinParameter("PLACO-T",	"PlayerCoin-Testnet",			"15666",	"20786",	"0",	"70e49e63",	"00000f2c9cbab0cf50346d1dd4d55e44afe6430fcaea9eef6f26b1868acb2f9f",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[589]=new CoinParameter("PND-T",	"Pandacoin-Testnet",			"44656",	"25715",	"0",	"fcc1b7dc",	"68fad98bd07315eef904fa3bf4344a38cb4f05549f659272bad7b4e88961d4c5",	"241",	"113",	"196"   ,"1",    "");
	coinParameter[590]=new CoinParameter("PNX-T",	"Phantomx-Testnet",				"31979",	"21979",	"0",	"275cd6d9",	"6e63025da5bd0721303f35e62ec8452a3e684cfd99598ecea14ca198e97dc2c8",	"239",	"55",	"192"   ,"1",    "");
	coinParameter[591]=new CoinParameter("POLIS-T",	"Polis-Testnet",				"21430",	"24131",	"0",	"cee2caff",	"0000009038aeaea86784e959b0b4002793adad39fc9d6f8789ed2edf99ad5c8b",	"239",	"140",	"19"    ,"1",    "");
	coinParameter[592]=new CoinParameter("POP-T",	"PopularCoin-Testnet",			"38181",	"37172",	"0",	"cdf1c0ef",	"00000e1438374d15f55c670fcae8d7cce5456a0b4cfa7429503eaeb46f6d55d4",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[593]=new CoinParameter("POST-T",	"PostCoin-Testnet",				"25500",	"25500",	"0",	"35c3d6a2",	"000005c17d0f7e6ff81c28d4ef2dd07be2e9363582efbdc7481bad70636be4ec",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[594]=new CoinParameter("PPC-T",	"Peercoin-Testnet",				"9903",		"9904",		"1",	"efc0f2cb",	"00008d0d88095d31f6dbdbcf80f6e51f71adf2be15740301f5e05cc0f3b2d2c0",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[595]=new CoinParameter("PROUD-T",	"PROUDMoney-Testnet",			"33542",	"36523",	"0",	"aed0a2a3",	"00000d4d0549912423730a89e05b8f096591d32795b1612a0abd5c3541904ddf",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[596]=new CoinParameter("PURA-T",	"Pura-Testnet",					"44443",	"55554",	"0",	"b796c542",	"00000622dfb4d5a2d83c43d60495419c82a0ee6659f210f5e8a0ed4ae7b40c59",	"239",	"140",	"19"    ,"1",    "");
	coinParameter[597]=new CoinParameter("PURE-T",	"Pure-Testnet",					"42745",	"42746",	"0",	"11c3b1df",	"0000555c3b7b3f248dc11f0611cb0bbfb0377f4c4406101c10f416dd4f4eaa76",	"239",	"204",	"196"   ,"1",    "");
	coinParameter[598]=new CoinParameter("PUT-T",	"PutinCoin-Testnet",			"17778",	"18345",	"0",	"cdf2c0ef",	"0000c763e402f2436da9ed36c7286f62c3f6e5dbafce9ff289bd43d7459327eb",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[599]=new CoinParameter("Q2C-T",	"QubitCoin-Testnet",			"11788",	"17799",	"0",	"011A39F7",	"2055f388b8bd5c6134272477eab7672af188fc70c55dbf3f6eedddf02d902aed",	"239",	"119",	"199"   ,"1",    "");
	coinParameter[600]=new CoinParameter("QBC-T",	"Quebecoin-Testnet",			"46790",	"46789",	"1",	"d3edc9f1",	"00000480b7d253fa603971195bae6b4fa10c65126c26c44c648ac2b5fc3fecbb",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[601]=new CoinParameter("QTL-T",	"Quatloo-Testnet",				"17912",	"17911",	"1",	"fbcdbfdb",	"94a28d0a7ecf0f184eef21f8639ade99866f059e72bf9bfe0fe5491c424a9f1b",	"248",	"120",	"196"   ,"1",    "");
	coinParameter[602]=new CoinParameter("QTUM-T",	"Qtum-Testnet",					"13888",	"13889",	"1",	"0d221506",	"0000e803ee215c0684ca0d2f9220594d3f828617972aad66feb2ba51f5e14222",	"239",	"120",	"110"   ,"1",    "");
	coinParameter[603]=new CoinParameter("RADS-T",	"Radium-Testnet",				"47963",	"47993",	"0",	"c377cc77",	"0000dedd0118550f474631b30cfc8b9d260706d59ea89118d81d1a292a5681d9",	"239",	"110",	"129"   ,"1",    "");
	coinParameter[604]=new CoinParameter("RC-T",	"RussiaCoin-Testnet",			"20992",	"20991",	"0",	"c0c0c0c0",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[605]=new CoinParameter("RDD-T",	"ReddCoin-Testnet",				"55444",	"55443",	"2",	"fec3b9de",	"a12ac9bd4cd26262c53a6277aafc61fe9dfe1e2b05eaa1ca148a5be8b394e35a",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[606]=new CoinParameter("REGA-T",	"Regacoin-Testnet",				"33813",	"33812",	"0",	"bcadafc4",	"b78197f0e175697646db1f738edc1ffdcb30588ebe70e7e16026489076577061",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[607]=new CoinParameter("RNS-T",	"Renos-Testnet",				"57255",	"57254",	"0",	"a179a4a2",	"00000e590857f6bf83e3ae07b56528e47565115465f9d4e8ead2c5bffb1a9edc",	"229",	"111",	"196"   ,"1",    "");
	coinParameter[608]=new CoinParameter("ROOFS-T",	"Roofs-Testnet",				"30019",	"30020",	"0",	"aaaa2bf9",	"000002026c1880506adfbaf9c9aa468764eb7bd4757446b5a8bc409af0f9339a",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[609]=new CoinParameter("RPC-T",	"RonPaulCoin-Testnet",			"19027",	"19026",	"1",	"fcc1b7dc",	"0ab57c2e763aef69d99638ae0059ed157ed6d0181b9e35cf71e04401ef240fee",	"250",	"122",	"196"   ,"1",    "");
	coinParameter[610]=new CoinParameter("RUP-T",	"Rupee-Testnet",				"20459",	"20460",	"0",	"06b9e2cd",	"83b9153a393cb8b9c4857932f37b2c3fb8ce65077029b8f7e3b2db11a7fae0d1",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[611]=new CoinParameter("RVN-T",	"Ravencoin-Testnet",			"18767",	"18766",	"2",	"",			"000000055c6b201ac99ed634953f92bd52239f5b26e090ce3caab6ec81bec921",	"239",	"111",	"196"   ,"1",    "tr");
	coinParameter[612]=new CoinParameter("SAK-T",	"Sharkcoin-Testnet",			"14011",	"14009",	"0",	"011A39F7",	"0000014a2f2596b0dd200958581579705e718d9fbd74674a76688c4a16ee345b",	"255",	"127",	"199"   ,"1",    "");
	coinParameter[613]=new CoinParameter("SBC-T",	"StrikeBitClub-Testnet",		"31575",	"31576",	"0",	"ddd7d477",	"00000a082edd9c65494b1f206f81dc9f4781556d5e4013aa5566ce0f0cb9a579",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[614]=new CoinParameter("SEQ-T",	"Sequence-Testnet",				"16664",	"16665",	"0",	"02200330",	"00000009cfa952b4f748c8cf7cdf975f89bce0a26a6a7e7c8cb33968bc20848e",	"204",	"53",	"50"    ,"1",    "");
	coinParameter[615]=new CoinParameter("SIB-T",	"SIBCoin-Testnet",				"11945",	"11944",	"2",	"cee2caff",	"00000617791d0e19f524387f67e558b2a928b670b9a3b387ae003ad7f9093017",	"239",	"125",	"100"   ,"1",    "");
	coinParameter[616]=new CoinParameter("SKC-T",	"Skeincoin-Testnet",			"27711",	"37711",	"0",	"07a05503",	"00000015f9fb4c1c9cc55ad08b6ec47fcce2b00bc482a2c48914ab6506daf439",	"237",	"56",	"88"    ,"1",    "");
	coinParameter[617]=new CoinParameter("SLR-T",	"SolarCoin-Testnet",			"19335",	"18182",	"2",	"fdd2c8f1",	"edcf32dbfd327fe7f546d3a175d91b05e955ec1224e087961acc9a2aa8f592ee",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[618]=new CoinParameter("SMART-T",	"SmartCash-Testnet",			"19678",	"19679",	"2",	"cffcbeea",	"0000027235b5679bcd28c90d03d4bf1a9ba4c07c4efcc1c87d6c68cce25e6e5d",	"193",	"65",	"21"    ,"1",    "");
	coinParameter[619]=new CoinParameter("SOCC-T",	"SocialCoin-Testnet",			"28645",	"28646",	"0",	"29362093",	"",																	"239",	"111",	"196"   ,"1",    "");
	coinParameter[620]=new CoinParameter("SONG-T",	"SongCoin-Testnet",				"18335",	"18334",	"1",	"fcc1b7dc",	"c2e1053b8b32c0aba8039421206c6760558d4fcd264394834fcf00a3245eca91",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[621]=new CoinParameter("SPACE-T",	"SpaceCoin-Testnet",			"19172",	"19173",	"0",	"0504030d",	"",																	"226",	"98",	"145"   ,"1",    "");
	coinParameter[622]=new CoinParameter("SPD-T",	"Stipend-Testnet",				"59432",	"59433",	"0",	"c4d5a6b8",	"e133d1d3f584f1c3f561e7d0606fa87dabb869325a979c2a82e2f3943c0e3370",	"25",	"85",	"23"    ,"1",    "");
	coinParameter[623]=new CoinParameter("SPHR-T",	"Sphere-Testnet",				"27544",	"27545",	"0",	"8de74912",	"00001f60dc7ff24d6eefb369ce6878e886815907cf44ceb45aa12a1d30b28acd",	"239",	"127",	"196"   ,"1",    "");
	coinParameter[624]=new CoinParameter("SPK-T",	"Sparks-Testnet",				"8891",		"8893",		"1",	"d12bb37a",	"000005f15ec2b9e4495efb539fb5b113338df946291cccd8dfd192bb68cd6dcf",	"240",	"112",	"20"    ,"1",    "");
	coinParameter[625]=new CoinParameter("SRC-T",	"SecureCoin-Testnet",			"22567",	"22568",	"0",	"021BC4F5",	"00000ddb5211c09372fef81d256f5afd98b4ba4e807dd664ee2829b30d84269d",	"239",	"127",	"199"   ,"1",    "");
	coinParameter[626]=new CoinParameter("STAK-T",	"STRAKS-Testnet",				"7565",		"17564",	"2",	"2a1ed5d1",	"000000cd747bd0b653e1fe417b60c1d9e990600cf2ff193404ea12c3ecb348b4",	"239",	"127",	"19"    ,"1",    "");
	coinParameter[627]=new CoinParameter("STN-T",	"SteneumCoin-Testnet",			"36965",	"36966",	"0",	"37fa833f",																	"",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[628]=new CoinParameter("STRAT-T",	"Stratis-Testnet",				"36965",	"36966",	"0",	"37fa833f",																	"",	"191",	"65",	"125"   ,"105",  "");
	coinParameter[629]=new CoinParameter("STV-T",	"Sativacoin-Testnet",			"50991",	"51991",	"0",	"a1a0a2a3",																	"",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[630]=new CoinParameter("SUPER-T",	"SuperCoin-Testnet",			"29390",	"29391",	"0",	"a1a0a2a3",	"00000130028bd75067e05b06fe8126e7f8ce8027a67898a6d649d0e239232a75",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[631]=new CoinParameter("SWING-T",	"Swing-Testnet",				"25764",	"25785",	"0",	"7ea68dbc",																	"",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[632]=new CoinParameter("SXC-T",	"Sexcoin-Testnet",				"19560",	"19561",	"3",	"face9669",	"73dc70a1698579360b62e724ecfeacfd938f45283162f3cf18f1b9eb3fc9fcd7",	"239",	"124",	"196"   ,"1",    "");
	coinParameter[633]=new CoinParameter("TAJ-T",	"TajCoin-Testnet",				"71210",	"7121",		"1",	"426fe813",	"00f8aa29160c44e912a3c45d05dfba13031c9b6e667b7368f788e23afd88db78",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[634]=new CoinParameter("TELL-T",	"Tellurion-Testnet",			"19999",	"33717",	"0",	"3c2d1e0f",	"000003034531d80c294d555923c0be7577aafbae370e2a3ca787fa0e509000c7",	"183",	"55",	"109"   ,"1",    "");
	coinParameter[635]=new CoinParameter("THC-T",	"HempCoin-Testnet",				"31054",	"22055",	"0",	"f2f2c0ef",	"000c20eb9b869a603d2251b26de94cadcf7a26677c5797cdb70f895cc2b9b19e",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[636]=new CoinParameter("TOA-T",	"ToaCoin-Testnet",				"19642",	"13564",	"0",	"198ed1d1",	"00000dcbbeda19169ce4ba73ca69babdffd47ccd5c6aafdce82974514c363447",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[637]=new CoinParameter("TOKC-T",	"TOKYO-Testnet",				"33517",	"33518",	"0",	"6a5b6dba",	"00000d49a58b1928c5c66347a4d869b97ec8288a54ea0b846e8bfd1a4d5369c7",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[638]=new CoinParameter("TOP-T",	"TopCoin-Testnet",				"32561",	"32562",	"0",	"cdf1c0ef",	"00000d439165d4f937bd617eb8ee539ce9e801d8874ecbe90b2a54978a88d340",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[639]=new CoinParameter("TPAY-T",	"TokenPay-Testnet",				"16601",	"16600",	"0",	"a32c44b4",	"00002f40aaa035cf667855579231429fa53519286fef744f6cc346bf4327579c",	"255",	"127",	"196"   ,"1",    "");
	coinParameter[640]=new CoinParameter("TRC-T",	"Terracoin-Testnet",			"18321",	"18332",	"1",	"0b110907",	"00000000a48f093611895d7452e456b646d213d238e86dc2c0db7d15fe6c555d",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[641]=new CoinParameter("TRK-T",	"Truckcoin-Testnet",			"28775",	"28776",	"0",	"dd4ddd4d",	"0000076130e1a816bab8f26310839ab601305b2315dc3b8b1a250faa0cb1f9a8",	"237",	"109",	"196"   ,"1",    "");
	coinParameter[642]=new CoinParameter("TX-T",	"TransferCoin-Testnet",			"27170",	"27171",	"0",	"2fca4d3e",	"9672529bc958a440a8acd061d914120d44c914a06454b82d3e1cd68fe4f1f916",	"239",	"97",	"196"   ,"1",    "");
	coinParameter[643]=new CoinParameter("UIS-T",	"Unitus-Testnet",				"60603",	"60604",	"2",	"c6abc79d",	"79b11e9472e5876fa6b6fac3efd46d63ee19e6f700d9048364e0b4ddeab0b58b",	"239",	"130",	"192"   ,"1",    "");
	coinParameter[644]=new CoinParameter("ULA-T",	"Ulatech-Testnet",				"31659",	"31660",	"0",	"e7f80328",																	"",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[645]=new CoinParameter("UNIT-T",	"UniversalCurrency-Testnet",	"24158",	"24157",	"0",	"adf1c2af",																	"",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[646]=new CoinParameter("UNO-T",	"Unobtanium-Testnet",			"65525",	"65531",	"2",	"01020304",	"000004aa8e535bedb2186a3c1c2f3b119e70c2f61286b15ec98a81021c3a4a0c",	"239",	"68",	"30"    ,"1",    "");
	coinParameter[647]=new CoinParameter("USC-T",	"UltimateSecureCash-Testnet",	"51997",	"51996",	"0",	"faf2efb4",	"00000438d60fb1a01a92a141f86d367589fd6190727d246ad24ac4119d3e6691",	"255",	"127",	"196"   ,"1",    "");
	coinParameter[648]=new CoinParameter("VIA-T",	"Viacoin-Testnet",				"25223",	"25222",	"2",	"a9c5ef92",	"770aa712aa08fdcbdecc1c8df1b3e2d4e17a7cf6e63a28b785b32e74c96cb27d",	"255",	"127",	"196"   ,"1",    "");
	coinParameter[649]=new CoinParameter("VIDZ-T",	"PureVidz-Testnet",				"3918",		"4918",		"0",	"eca1b201",	"1587a4a368eaaf2da693e727d3dfe44bfdb24d2e5a83529dabeae884349af09e",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[650]=new CoinParameter("VIPS-T",	"VipstarCoin-Testnet",			"14888",	"14889",	"0",	"2f3e4d5c",	"0000d068e1d30f79fb64446137106be9c6ee69a6a722295c131506b1ee09b77c",	"228",	"100",	"110"   ,"1",    "");
	coinParameter[651]=new CoinParameter("VISIO-T",	"Visio-Testnet",				"25714",	"25715",	"0",	"cdf2c0ef",	"2f647dd5a1a1eccec407aab62ba52cf07415502191db29c66d26d50042cbecc5",	"239",	"111",	"196"   ,"1",    "");
	coinParameter[652]=new CoinParameter("VIVO-T",	"VIVO-Testnet",					"13845",	"19998",	"1",	"d124b57a",	"00000f6be3e151f9082a2b82c2916192a791090015b80979934a45d625460d62",	"240",	"112",	"20"    ,"1",    "");
	coinParameter[653]=new CoinParameter("VOT-T",	"VoteCoin-Testnet",				"18233",	"18232",	"1",	"fa1af9bf",	"",                                                            	    "239",	"7461","7354"   ,"1",    "");
	coinParameter[654]=new CoinParameter("VRC-T",	"VeriCoin-Testnet",				"48684",	"48683",	"0",	"cdf2c0ef",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[655]=new CoinParameter("VTA-T",	"Virtacoin-Testnet",			"12025",	"14023",	"0",	"fcc1b7dc",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[656]=new CoinParameter("VTC-T",	"Vertcoin-Testnet",				"15889",	"15888",	"4",	"76657274",	"",                                                            	    "239",	"74",	"196"   ,"1",    "tvtc");
	coinParameter[657]=new CoinParameter("VULC-T",	"Vulcano-Testnet",				"31041",	"31042",	"0",	"82547825",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[658]=new CoinParameter("WC-T",	"WINCOIN-Testnet",				"11610",	"11611",	"0",	"ffe1d0ef",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[659]=new CoinParameter("WINK-T",	"Wink-Testnet",					"37747",	"37746",	"0",	"",			"",                                                            	    "177",	"49",	"196"   ,"1",    "");
	coinParameter[660]=new CoinParameter("WOMEN-T",	"WomenCoin-Testnet",			"29207",	"29208",	"0",	"00247f1e",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[661]=new CoinParameter("XBTC21-T","Bitcoin21-Testnet",			"17778",	"18345",	"0",	"cdf2c0ef",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[662]=new CoinParameter("XBTS-T",	"Beatcoin-Testnet",				"26153",	"26153",	"0",	"a1a0a2a3",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[663]=new CoinParameter("XCO-T",	"X-Coin-Testnet",				"25714",	"25825",	"0",	"afd1eaa5",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[664]=new CoinParameter("XCT-T",	"C-Bit-Testnet",				"18289",	"18288",	"0",	"0b10d907",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[665]=new CoinParameter("XGOX-T",	"XGOX-Testnet",					"33185",	"33186",	"0",	"85fa0371",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[666]=new CoinParameter("XJO-T",	"Joulecoin-Testnet",			"26783",	"8844",		"1",	"0ac07312",	"",                                                            	    "212",	"83",	"13"    ,"1",    "");
	coinParameter[667]=new CoinParameter("XM-R-T",	"Monero-Testnet",				"28080",	"28081",	"0",	"",			"",                                                            	    "",		"53",	""      ,"1",    "");
	coinParameter[668]=new CoinParameter("XMY-T",	"Myriad-Testnet",				"20888",	"20889",	"2",	"01f555a4",	"",                                                            	    "239",	"88",	"188"   ,"1",    "");
	coinParameter[669]=new CoinParameter("XP-T",	"ExperiencePoints-Testnet",		"17778",	"18345",	"0",	"cdf2c0ef",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[670]=new CoinParameter("XPM-T",	"Primecoin-Testnet",			"9913",		"9914",		"2",	"fbfecbc3",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[671]=new CoinParameter("XPTX-T",	"PlatinumBAR-Testnet",			"15001",	"15002",	"0",	"01020304",	"",                                                            	    "246",	"117",	"55"    ,"1",    "");
	coinParameter[672]=new CoinParameter("XQN-T",	"Quotient-Testnet",				"30993",	"30996",	"0",	"0b051107",	"",                                                            	    "248",	"120",	"196"   ,"1",    "");
	coinParameter[673]=new CoinParameter("XRA-T",	"Ratecoin-Testnet",				"35852",	"35852",	"0",	"a1a0a2a3",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[674]=new CoinParameter("XRE-T",	"RevolverCoin-Testnet",			"18777",	"18775",	"0",	"0b110907",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[675]=new CoinParameter("XSN-T",	"Stakenet-Testnet",				"29999",	"19998",	"4",	"cee2caff",	"",                                                            	    "239",	"140",	"19"    ,"1",    "tb");
	coinParameter[676]=new CoinParameter("XSPEC-T",	"Spectrecoin-Testnet",			"37111",	"36757",	"0",	"a32c44b4",	"",                                                            	    "255",	"127",	"196"   ,"1",    "");
	coinParameter[677]=new CoinParameter("XVG-T",	"Verge-Testnet",				"29102",	"21102",	"0",	"cdf2c0ef",	"",                                                            	    "243",	"115",	"198"   ,"1",    "");
	coinParameter[678]=new CoinParameter("XZC-T",	"ZCoin-Testnet",				"18168",	"18888",	"6",	"cffcbeea",	"",                                                            	    "185",	"65",	"178"   ,"1",    "");
	coinParameter[679]=new CoinParameter("YTN-T",	"YENTEN-Testnet",				"19981",	"19252",	"2",	"9554e495",	"",                                                            	    "240",	"112",	"197"   ,"1",    "");
	coinParameter[680]=new CoinParameter("ZCL-T",	"ZClassic-Testnet",				"18233",	"18023",	"1",	"fa1af9bf",	"",                                                            	    "239",	"7461","7354"   ,"1",    "");
	coinParameter[681]=new CoinParameter("ZEC-T",	"Zcash-Testnet",				"18233",	"18232",	"1",	"fa1af9bf",	"",                                                            	    "239",	"7461","7354"   ,"1",    "");
	coinParameter[682]=new CoinParameter("ZEIT-T",	"Zeitcoin-Testnet",				"22788",	"22789",	"0",	"cdf2c0ef",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[683]=new CoinParameter("ZEN-T",	"ZenCash-Testnet",				"19033",	"18231",	"4",	"bff2cde6",	"",                                                            	    "239",	"8344","8338"   ,"1",    "");
	coinParameter[684]=new CoinParameter("ZENI-T",	"Zennies-Testnet",				"11021",	"11022",	"0",	"aabbccee",	"",                                                            	    "239",	"120",	"196"   ,"1",    "");
	coinParameter[685]=new CoinParameter("ZET-T",	"Zetacoin-Testnet",				"27333",	"18332",	"3",	"05fea901",	"",                                                            	    "239",	"88",	"188"   ,"1",    "");
	coinParameter[686]=new CoinParameter("ZOI-T",	"Zoin-Testnet",					"28168",	"28168",	"3",	"ae5dbf09",	"",                                                            	    "193",	"65",	"178"   ,"1",    "");
	coinParameter[687]=new CoinParameter("ZYD-T",	"Zayedcoin-Testnet",			"33813",	"33812",	"0",	"bcadafc4",	"",                                                            	    "239",	"111",	"196"   ,"1",    "");
	coinParameter[688]=new CoinParameter("ZZC-T",	"ZoZoCoin-Testnet",				"29977",	"12883",	"2",	"cee2caff",	"",                                                            	    "239",	"140",	"19"    ,"1",    "");
	coinParameter[690]=new CoinParameter("BSV-T",	"Bitcoin SV-Testnet",			"18333",	"18332",	"4",	"f4e5f3f4",	"000000000933ea01ad0ee984209779baaec3ced90fa3f408719526f8d77f4943",	"239",	"111",	"196"   ,"1",    "");
}
}