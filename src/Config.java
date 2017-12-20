import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;



	/************************************************************************
	* 									*
	*	Creates a BAGen.cfg in which all settings are saved		*
	*									*
	*************************************************************************/



public class Config
{


	public static final String  name    	= "BAGen.cfg";
	public static final int anzahlSprachen  = 5;
	public static final int laengeArray     = 43;			
	public static String[]  settings 	= new String[laengeArray];
	public static boolean   fileError 	= false;



	public static void loadDefaultConfig()
	{
		settings[0] = "Bitcoin Adress Generator settings";
		settings[1] = GUI_MAIN.VersionsNummer;
		settings[2] = GUI_MAIN.Autor;
		settings[3] = Language.meineBitcoinAdresse;
		settings[4] = "";
		settings[5] = "";
		settings[6] = "";
		settings[7] = "";
		settings[8] = "";
		settings[9] = "";
		settings[10] = "0";
		settings[11] = "0";
		settings[12] = "";
		
		settings[13] = "255";
		settings[14] = "248";
		settings[15] = "219";

		settings[16] = "128";
		settings[17] = "128";
		settings[18] = "0";	
		
		settings[19] = "255";
		settings[20] = "250";
		settings[21] = "240";
		
		settings[22] = "50";
		settings[23] = "50";
		settings[24] = "50";
		
		settings[25] = "250";
		settings[26] = "250";
		settings[27] = "250";
		
		settings[28] = "50";
		settings[29] = "50";
		settings[30] = "50";
		
		settings[31] = "255";
		settings[32] = "228";
		settings[33] = "225";
		
		settings[34] = "193";
		settings[35] = "36";
		settings[36] = "36";
		
		settings[37] = "224";
		settings[38] = "255";
		settings[39] = "255";
		
		settings[40] = "36";
		settings[41] = "36";
		settings[42] = "193";
	}
		



	//  check settings
	private static boolean isValid(String[] str)
	{
		for(int i=0; i<16; i++) if(str[i]==null) 	{fileError=true;	return false;}
		if(str[ 0].equals(settings[ 0])==false)  				return false; 
		if(str[ 1].matches("[0-9](.[0-9]*)?")==false)  				return false;
		if(str[ 2].equals(settings[ 2])==false)  				return false;
		if(str[ 3].equals(settings[ 3])==false) 	{fileError=true;	return false;}
		if(str[ 4].equals(settings[ 4])==false)  				return false;
		if(str[ 5].equals(settings[ 5])==false)  				return false;
		if(str[ 6].equals(settings[ 6])==false)  				return false;
		if(str[ 7].equals(settings[ 7])==false)  				return false;
		if(str[ 8].equals(settings[ 8])==false)  				return false;
		if(str[ 9].equals(settings[ 9])==false)  				return false;
		if(str[10].matches("[0-9]+")==false || isZwischen(str[10],0,anzahlSprachen-1)==false)	return false;
		if(str[11].matches("[0-9]+")==false || isZwischen(str[11],0,3)  ==false)return false;
		if(str[12].equals(settings[12])==false)  				return false;
		if(str[13].matches("[0-9]+")==false || isZwischen(str[13],0,255)==false)return false;
		if(str[14].matches("[0-9]+")==false || isZwischen(str[14],0,255)==false)return false;
		if(str[15].matches("[0-9]+")==false || isZwischen(str[15],0,255)==false)return false;
		if(str[16].matches("[0-9]+")==false || isZwischen(str[16],0,255)==false)return false;
		if(str[17].matches("[0-9]+")==false || isZwischen(str[17],0,255)==false)return false;
		if(str[18].matches("[0-9]+")==false || isZwischen(str[18],0,255)==false)return false;
		if(str[19].matches("[0-9]+")==false || isZwischen(str[19],0,255)==false)return false;
		if(str[20].matches("[0-9]+")==false || isZwischen(str[20],0,255)==false)return false;
		if(str[21].matches("[0-9]+")==false || isZwischen(str[21],0,255)==false)return false;
		if(str[22].matches("[0-9]+")==false || isZwischen(str[22],0,255)==false)return false;
		if(str[23].matches("[0-9]+")==false || isZwischen(str[23],0,255)==false)return false;
		if(str[24].matches("[0-9]+")==false || isZwischen(str[24],0,255)==false)return false;
		if(str[25].matches("[0-9]+")==false || isZwischen(str[25],0,255)==false)return false;
		if(str[26].matches("[0-9]+")==false || isZwischen(str[26],0,255)==false)return false;
		if(str[27].matches("[0-9]+")==false || isZwischen(str[27],0,255)==false)return false;
		if(str[28].matches("[0-9]+")==false || isZwischen(str[28],0,255)==false)return false;
		if(str[29].matches("[0-9]+")==false || isZwischen(str[29],0,255)==false)return false;
		if(str[30].matches("[0-9]+")==false || isZwischen(str[30],0,255)==false)return false;
		if(str[31].matches("[0-9]+")==false || isZwischen(str[31],0,255)==false)return false;
		if(str[32].matches("[0-9]+")==false || isZwischen(str[32],0,255)==false)return false;
		if(str[33].matches("[0-9]+")==false || isZwischen(str[33],0,255)==false)return false;
		if(str[34].matches("[0-9]+")==false || isZwischen(str[34],0,255)==false)return false;
		if(str[35].matches("[0-9]+")==false || isZwischen(str[35],0,255)==false)return false;
		if(str[36].matches("[0-9]+")==false || isZwischen(str[36],0,255)==false)return false;
		if(str[37].matches("[0-9]+")==false || isZwischen(str[37],0,255)==false)return false;
		if(str[38].matches("[0-9]+")==false || isZwischen(str[38],0,255)==false)return false;
		if(str[39].matches("[0-9]+")==false || isZwischen(str[39],0,255)==false)return false;
		if(str[40].matches("[0-9]+")==false || isZwischen(str[40],0,255)==false)return false;
		if(str[41].matches("[0-9]+")==false || isZwischen(str[41],0,255)==false)return false;
		if(str[42].matches("[0-9]+")==false || isZwischen(str[42],0,255)==false)return false;
		return true;	
	}
	
	
	
	
	public static void loadConfig(String name) 
	{
		loadDefaultConfig();
		int l = laengeArray;
		int i = 0;
		String[] str = new String[l];
		str[3]       = "0";
		boolean fehler = false;
		BufferedReader br= null;
		try   
		{
		    br = new BufferedReader(new FileReader(name));
		    String zeile = null;
		    while(i<l)				
		    {
		    	zeile = br.readLine(); 	
		    	str[i]=zeile;		
		    	i++;      			
		    }
		    br.close();			
		} 							
		catch (FileNotFoundException e) {fehler = true;}
		catch (IOException e){e.printStackTrace();fehler = true;}
	    if(fehler==false && isValid(str)==true)  settings = str;
	    else loadDefaultConfig();
	}
	
	


	public static void saveConfig(String name)   
	{
	    String[] str=settings;
		if(fileError==false)
	    {	
			BufferedWriter f=null;
			try{f = new BufferedWriter(new FileWriter(name));} 
			catch (IOException e){e.printStackTrace();}
		    for(int i=0;i<str.length;i++)
		    {	
		    	try {f.write(str[i]+"\n");} 
		    	catch (IOException e) {e.printStackTrace();}
		    }
		    try{f.close();} 
		    catch (IOException e){e.printStackTrace();} 
	    }    
	} 
	
	
	

	private static boolean isZwischen(String str, int a, int b)
	{
		int z = Integer.parseInt(str);	
		if(z>=a && z<= b)return true;
		else return false;	
	}	
}
