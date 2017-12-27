



	/************************************************
	* 						*
	*		language here			*
	*						*
	*************************************************/



public class Language
{


	public static final String meinPublicKey       = "049A442B458F5CCA534718DEF760CCEA8F7D944D157A8D653713957C6A57B6AD8A";
	public static final String meineBitcoinAdresse = "12zeCvN7zbAi3JDQhC8tU3DBm35kDEUNiB";
	public static String InfoText;					
	
	public static String Einleitung;				
	public static String max; 						
	public static String F0;  						
	public static String F2;  						
	public static String F3;						
	public static String W1; 						
	public static String W2;  						
	public static String W3;  						
	public static String W4;  						
	
	public static String lblPassphrase;				
	public static String lblGebenSieEinen;			
	public static String lblWuerfelEingabe;			
	public static String lblWuerfelzeichenEingeben;	
	public static String lblPrivateKeyIn;			
	public static String lblPrivateKeyIn_1;			
	public static String lblPublicKeyX;				
	public static String lblPublicKeyY;				
	public static String lblBase58;					
	public static String lblBitcoinAdress;			
	
	public static String btnStyle;					
	public static String btnInfo;					
	public static String btnDrucken;				
	public static String btnSpeichern;				
	public static String btnEnter;					
	public static String btnPrivateKeyAusblenden;	
	public static String farbe;						
	public static String farbenAnpassen;			
	public static String loadDefaultSettings;		
	public static String lblFarben1;				
	public static String lblFarben2;				
	public static String[] comboBoxPrivateKey = new String[4];



	//  			0: Englisch
	//			1: Deutsch
	//			2: Französisch
	//			3: Russisch
	//			4: Chinesisch
	public static void setLanguage(int sprache)
	{
	  	Config.settings[10] = Integer.toString(sprache);
	  	switch(sprache)
	{



// -----------------------------------------  Englisch -------------------------------------------------------------------//	  
	case 0:
	{
		InfoText = "\n\n"
		+"      Bitcoin Adress Generator                              \n"
		+"      Version: "+GUI_MAIN.VersionsNummer+"                  \n\n"
		+"      Author:  "+GUI_MAIN.Autor+"                                   \n"
		+"      E-Mail:  "+GUI_MAIN.E_Mail+"                           \n\n"
		+"      Copyright © 2017   Mr.Maxwell    All rights reserved. \n\n"
		+"      Public Key:                                           \n "
		+"     "+meinPublicKey+"                                      \n\n"
		+"      donate to:      "+meineBitcoinAdresse+"				  \n";
			
			
		Einleitung = "" 
		+"   This program creates a private-key in Bitcoin Wallet Import Format WIF \n"
	    	+"   as well as the associated bitcoin address and the public-key. \n"
	   	+"   You must enter 64 random hexadecimal characters or have them generated. \n";		  
			
		max = "Key max:  FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140";
		F0  = "Error!   Key can not be 0!";
		F2  = "QR-Code  Error!";
		F3  = "No correct private key format was detected!";
			
		W2  = "warning!   Maximum size of the key exceeded!";
		W3  = "warning!   Key too short, unsafe!";
		W4  = "warning!   Passphrase too short!";		

		lblPassphrase 			= "pass private-key through passphrase";
		lblGebenSieEinen 		= "type a passphrase";	
		lblWuerfelEingabe		= "generate private-key by rolling dice";
		lblWuerfelzeichenEingeben 	= "100 characters, only digits between 1 and 6 are allowed !";
		lblPrivateKeyIn			= "Enter your private-key";
		lblPrivateKeyIn_1		= "Private key";
		lblPublicKeyX			= "    Public key X";
		lblPublicKeyY			= "    Public key Y";
		lblBase58			= "Base58";
		lblBitcoinAdress		= "Bitcoin adress";
		btnStyle			= "Style";
		btnInfo				= "Info";
		btnDrucken			= "Print";
		btnSpeichern			= "Save";
		btnEnter			= "Enter";
		btnPrivateKeyAusblenden	 	= "Hide private key";
		comboBoxPrivateKey[0]    	= "HEXA";
		comboBoxPrivateKey[1]    	= "Base58";
		comboBoxPrivateKey[2]    	= "Base58 compressed";
		comboBoxPrivateKey[3]    	= "Base64";
		farbe			 	= "Color";
		farbenAnpassen		 	= "Customize colors";
		loadDefaultSettings 	 	= "Load default settings";
		lblFarben1		 	= "Background                 Foreground";
		lblFarben2 = "Logo\nand\nBitcoinadress\n\n\nMain window\nand\nlabel color\n\n\nInput box\n\n\n\n\nPrivate key\n\n\n\n\nPublic Key";
		break;			
	}	



// ---------------------------------------  Deutsch -----------------------------------------------------------------//		
	case 1:
	{
		InfoText = "\n\n"
		+"      Bitcoin Adress Generator                              \n"
		+"      Version: "+GUI_MAIN.VersionsNummer+"                  \n\n"
		+"      Author:  "+GUI_MAIN.Autor+"                                   \n"
		+"      E-Mail:  "+GUI_MAIN.E_Mail+"                            \n\n"
		+"      Copyright © 2017   Mr.Maxwell    Alle Rechte vorbehalten. \n\n"
		+"      Public Key:                                           \n "
		+"     "+meinPublicKey+"                                      \n\n"
		+"      Spenden an:     "+meineBitcoinAdresse+"    \n";
			
			
		Einleitung = "" 
		+"   Dieses Programm erstellt einen privat Schlüssel im Bitcoin Wallet Import Format WIF\n"
		+"   Sowie die zugehörige Bitcoin-Adresse und den öffentlichen Schlüssel.\n"
		+"   Dazu müssen sie 64 zufällige Hexadezimalzeichen eingegeben oder sie generieren lassen.\n";	
			
		max = "Key max ist:  FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140";
		F0  = "Fehler!   Key darf nicht 0 sein!";
		F2  = "QR-Bild Fehler!";
		F3  = "Das Format des privaten Schlüssels ist falsch!";
			
		W2  = "Warnung!   Maximale Größe des Schlüssels überschritten!";
		W3  = "Warnung!   Schlüssel zu klein, unsicher!";
		W4  = "Warnung!   Passphrase zu kurz!";
					  
		lblPassphrase 			= "Privat-Schlüssel durch Passphrase generieren lassen";
		lblGebenSieEinen 		= "Geben Sie einen Text als Passphrase ein";	
		lblWuerfelEingabe		= "Privat-Schlüssel durch Würfeln generieren";
		lblWuerfelzeichenEingeben 	= "100 Würfelzeichen eingeben, es sind nur Ziffern zwischen 1 und 6 erlaubt !";
		lblPrivateKeyIn			= "Privat-Schlüssel eingeben";
		lblPrivateKeyIn_1		= "Privat Schlüssel";
		lblPublicKeyX			= "Öfftl. Schlüssel X";
		lblPublicKeyY			= "Öfftl. Schlüssel Y";
		lblBase58			= "Base58";
		lblBitcoinAdress		= "Bitcoin Adresse";
		btnStyle			= "Stil";
		btnInfo				= "Info";
		btnDrucken			= "Drucken";
		btnSpeichern			= "Speichern";
		btnEnter			= "OK";
		btnPrivateKeyAusblenden	 	= "Privat Schlüssel ausblenden";
		comboBoxPrivateKey[0]    	= "HEXA";
		comboBoxPrivateKey[1]    	= "Base58";
		comboBoxPrivateKey[2]    	= "Base58 compressed";
		comboBoxPrivateKey[3]    	= "Base64";
		farbe			 	= "Farbe";
		farbenAnpassen		 	= "Farben anpassen";
		loadDefaultSettings 	 	= "standard Konfig. laden";
		lblFarben1		 	= "Hintergrund                 Vordergrund";
		lblFarben2 = "Logo\nund\nBitcoinadresse\n\n\nHauptfenster\nund\nLabel Farbe\n\n\nEingabefeld\n\n\n\n\nPrivat Schlüssel\n\n\n\nÖffentlicher Schlüssel";
		break;	
	}



// ---------------------------------------  Französisch -----------------------------------------------------------//	
	case 2:
	{
		InfoText = "\n\n"
		+"      Bitcoin Adress Generator                              \n"
		+"      Version: "+GUI_MAIN.VersionsNummer+"                  \n\n"
		+"      Author:  "+GUI_MAIN.Autor+"                                   \n"
		+"      E-Mail:  "+GUI_MAIN.E_Mail+"                            \n\n"
		+"      Copyright © 2017   Mr.Maxwell   Tous les droits sont réservés.\n\n"
		+"      Clé publique:                                         \n "
		+"     "+meinPublicKey+"                                      \n\n"
		+"      faire un don à:  "+meineBitcoinAdresse+"   \n";
			
				
		Einleitung = "" 
		+ "   Ce programme crée une clé privée dans Bitcoin Wallet Import Format WIF \n"
		+ "   Ainsi que l'adresse bitcoin associée et la clé publique. \n"
		+ "   Vous devez entrer 64 caractères hexadécimaux aléatoires ou les générer. \n";
		
		max = "Clé max:  FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140";
		F0  = "Erreur!   La clé ne peut pas être 0!";
		F2  = "Erreur d'image QR!";
		F3  = "Aucun format de clé privée correct n'a été détecté";
		
		W2 = "Avertissement! La taille maximale de la clé est dépassée!";
		W3 = "Attention! Clé trop petite, dangereuse!";
		W4 = "Avertissement! Phrase de passe trop courte!";
				  
		lblPassphrase 			= "Clé privée générée par la phrase de passe";
		lblGebenSieEinen 		= "Tapez une phrase secrète";	
		lblWuerfelEingabe		= "Créer une clé privée en lançant des dés";
		lblWuerfelzeichenEingeben 	= "100 caractères cubes, seuls les chiffres compris entre 1 et 6 sont autorisés !";
		lblPrivateKeyIn			= "Entrez une clé privée";
		lblPrivateKeyIn_1		= "Clé privée";
		lblPublicKeyX			= "  Clé publique X";
		lblPublicKeyY			= "  Clé publique Y";
		lblBase58			= "Base58";
		lblBitcoinAdress		= "Adresse Bitcoin";
		btnStyle			= "Style";
		btnInfo				= "Info";
		btnDrucken			= "imprimer";
		btnSpeichern			= "Enregistrer";
		btnEnter			= "OK";
		btnPrivateKeyAusblenden	 	= "Masquer la clé privée";
		comboBoxPrivateKey[0]    	= "HEXA";
		comboBoxPrivateKey[1]    	= "Base58";
		comboBoxPrivateKey[2]    	= "Base58 compressée";
		comboBoxPrivateKey[3]    	= "Base64";
		farbe			 	= "couleur";
		farbenAnpassen		 	= "Personnaliser les couleurs";
		loadDefaultSettings 	 	= "Charger les paramètres par défaut";
		lblFarben1		 	= "Contexte                     premier plan";
		lblFarben2 = "Logo et \nAdresse Bitcoin\n\n\nFenêtre principale\net couleur de\nl'étiquette\n\n\nChamp de\nsaisie\n\n\n\nClé privée\n\n\n\nClé publique";
		break;	
	}



// ---------------------------------------  Russisch -----------------------------------------------------------------//		
	case 3:
	{
		InfoText = "\n\n"
		+"      Bitcoin Adress Generator                              \n"
		+"      Версия: "+GUI_MAIN.VersionsNummer+"                   \n\n"
		+"      Автор:          "+GUI_MAIN.Autor+"                            \n"
		+"      Эл. адрес:  "+GUI_MAIN.E_Mail+"                        \n\n"
		+"      Copyright © 2017   Mr.Maxwell    Все права защищены.  \n\n"
		+"      открытый ключ:                                        \n "
		+"     "+meinPublicKey+"                                      \n\n"
		+"      пожертвовать:   "+meineBitcoinAdresse+"    \n";
			
			
		Einleitung = "" 
		+"   Эта программа создает частный биткоин-ключ\n"
		+"   А также связанный с ним биткоин-адрес и открытый ключ.\n"
		+"   Вы должны ввести 64 случайных шестнадцатеричных символа или сгенерировать их.\n";	
					
		max = "Максимальное значение ключа:  FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140";
		F0  = "Ошибка! Ключ не может быть 0!";											
		F2  = "QR изображение ошибка!";													
		F3  = "Неверный формат закрытого ключа";
				
		W2  = "Внимание! Максимальный размер ключа превышен!";						
		W3  = "Внимание! Ключ слишком маленький, небезопасный!";                                                   
		W4  = "Внимание! Парольная фраза слишком короткая!";                      									
						  
		lblPassphrase 			= "ключевая фраза";								
		lblGebenSieEinen 		= "Введите ключевую фразу";							
		lblWuerfelEingabe		= "Создание секретных ключей путем игра в кости";		
		lblWuerfelzeichenEingeben 	= "100 символов, допускаются только номера от 1 до 6!";                            
		lblPrivateKeyIn			= "Введите закрытый ключ";										
		lblPrivateKeyIn_1		= "Биткоин закрытый ключ";										
		lblPublicKeyX			= "открытый ключ X";											
		lblPublicKeyY			= "открытый ключ Y";											
		lblBase58			= "База58";												
		lblBitcoinAdress		= "Адрес биткоина ";											
		btnStyle			= "стиль";
		btnInfo				= "Информация";												
		btnDrucken			= "печать";												
		btnSpeichern			= "сохранить";												
		btnEnter			= "ОК";												
		btnPrivateKeyAusblenden	 	= "Скрыть закрытый ключ";										
		comboBoxPrivateKey[0]    	= "гекса";														
		comboBoxPrivateKey[1]    	= "База58";												
		comboBoxPrivateKey[2]    	= "База58 сжата";											
		comboBoxPrivateKey[3]    	= "База64";												
		farbe			 	= "цвет";													
		farbenAnpassen		 	= "Настройка цветов";										
		loadDefaultSettings 	 	= "Загрузить настройки по умолчанию";								
		lblFarben1		 	= "задний план            передний план";
		lblFarben2 = "Логотип и\nбиткоин-адрес\n\n\nГлавное\nокно и\nцвет ярлыка\n\n\nполе ввода\n\n\n\nБиткоин\nзакрытый ключ\n\n\nоткрытый ключ";
		break;	
	}



// ---------------------------------------  Chinesisch -----------------------------------------------------------------//		
	case 4:
	{
		InfoText = "\n\n"
		+"      Bitcoin Adress Generator                          \n"
		+"      版：     "+GUI_MAIN.VersionsNummer+"                  \n\n"
		+"      作者：           "+GUI_MAIN.Autor+"                                \n"
		+"      电子邮件：   "+GUI_MAIN.E_Mail+"                       \n\n"
		+"      Copyright © 2017   Mr.Maxwell  版权所有。                          \n\n"
		+"      公钥:                                             \n "
		+"     "+meinPublicKey+"                                 \n\n"
		+"      捐赠给：           "+meineBitcoinAdresse+"    \n";
		
	
		Einleitung = "" 
		+"   该程序创建一个Bitcoin私钥\n"
		+"   以及相关的比特币地址和公钥。\n"
		+"   您必须输入64个随机的十六进制字符或生成它们。\n";	
				
		max = "最大密钥长度：  FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364140";		
		F0  = "错了，关键不能零";							      	// Fehler, der Schlüssel darf nicht 0 sein!
		F2  = "二维码 错误";									  // QR-Bild Fehler
		F3  = "私钥格式无效";
			
		W2  = "警告！ 键的最大大小超过！";							     // Warnung! Die maximale Größe des Schlüssels ist überschritten!
		W3  = "警告！ 钥匙太小，不安全！";                                                    			
		W4  = "请注意！ 密码太短了!";                      				       // Warnung!   Passphrase zu kurz!
							  
		lblPassphrase 			= "密码";						    // Passphrase
		lblGebenSieEinen 		= "输入密码";					   // Geben Sie einen Text als Passphrase ein
		lblWuerfelEingabe		= "通过滚动骰子创建私钥";				       // Privat-Schlüssel durch würfeln generieren
		lblWuerfelzeichenEingeben 	= "100个字符，只允许1到6之间的数字！";              
		lblPrivateKeyIn			= "输入私钥";					   // Private Key eingeben
		lblPrivateKeyIn_1		= "比特币私钥";					   // Bitcoin Private Key
		lblPublicKeyX			= "            公钥X";				     // Public Key X
		lblPublicKeyY			= "            公钥Y";				     // Public Key Y
		lblBase58			= "基地58";					     // Base58
		lblBitcoinAdress		= "比特币地址";					   // Bitcoin Adresse
		btnStyle			= "风格";
		btnInfo				= "И信息";					     // Info
		btnDrucken			= "打印";						     // Drucken
		btnSpeichern			= "保存";						     // Speichern
		btnEnter			= "输入";						     // Enter
		btnPrivateKeyAusblenden	 	= "隐藏私钥";					    // Private Key ausblenden
		comboBoxPrivateKey[0]    	= "十六进制";					    // Hexa
		comboBoxPrivateKey[1]    	= "基地58";					     // Base58
		comboBoxPrivateKey[2]    	= "基座58压缩";					    // Base58 Compressed
		comboBoxPrivateKey[3]    	= "基地64";					     // Base64
		farbe			 	= "颜色";						     // Farbe
		farbenAnpassen		 	= "自定义颜色";					   // Farben anpassen
		loadDefaultSettings 	 	= "加载默认设置";
		lblFarben1		 	= "     背景                                 前景";
		lblFarben2               	= "徽标和比特币地址\n\n\n\n主窗口和标签颜色\n\n\n\n输入字段\n\n\n\n\n比特币私钥\n\n\n\n公钥";
		break;	
	}				



// ---------------------------------------  insert next language here  -----------------------------------------------------------------//		




	}
    }	
}
