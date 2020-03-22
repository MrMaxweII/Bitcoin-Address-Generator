package CoinGen;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;



/********************************************************************************
*	Erzeugt einen QR-Code oder liest ein CR-Code von einem Image ein.	*
*	Benötigt zxing.rar Bibliothek						*
*********************************************************************************/



public class QRCodeReaderZXING
{

    
/**	Erzeugt einen QR-Code aus einem String.
	@param data String aus dem der QR-Code erzeugt werden soll.
	@param color1 Hintergrundfarbe
	@param color2 Schriftfarbe
	@param x Breite
	@param y Höhe
	@return Das Bild mit dem QR-Code  **/
public static BufferedImage writeQRCode(String data, Color color1, Color color2, int x, int y) 
{																
	QRCodeWriter writer = new QRCodeWriter();														
	BufferedImage image = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB); 			
	int white = color1.getRed() << 16 | color1.getGreen() << 8 | color1.getBlue();															
	int black = color2.getRed() << 16 | color2.getGreen() << 8 | color2.getBlue();																						
	try 
	{
	    BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, x+20, y+20);			
	    for (int i = 0; i < x; i++) 
	    {
	       for (int j = 0; j < y; j++) image.setRGB(i, j, bitMatrix.get(i+10, j+10) ? black : white); 
	    }
	} 
	catch (WriterException e) {e.printStackTrace();}
	return image;
}
	


/**	Scannt einen QR-Code von einem Image Bild.
	@param image Übergeben wird ein Image mit dem QR-Code
	@param rotate muss "false" sein, wird von der Methode selbst verwendet.
	@return Der String, der die Daten aus dem QR-Code enthällt **/
public static String readQRCode(BufferedImage image, boolean rotate)
{
	Image tmp = image.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
	BufferedImage dimg = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2d = dimg.createGraphics();
	g2d.drawImage(tmp, 0, 0, null);
	g2d.dispose();
	image = dimg;
	int width = image.getWidth();
	int heigth = image.getHeight();
	int[] 		   	   pixels = image.getRGB(0,0, width, heigth, null, 0, width);	
	RGBLuminanceSource source = new RGBLuminanceSource(width, heigth, pixels);
	BinaryBitmap 	   bitmap = new BinaryBitmap(new HybridBinarizer(source));
	QRCodeReader       reader = new QRCodeReader(); 		
	if(rotate)																		
	{	
		try {bitmap.getBlackMatrix().rotate180();} 
		catch (NotFoundException e1) {e1.printStackTrace();}
	}   
	try 
	{
		Result result = reader.decode(bitmap);
	    return result.getText();
	} 
	catch (NotFoundException e) 
	{
		if(rotate==false) return readQRCode(image,true);							
		else e.printStackTrace(); return "QR code could not be read";
	} 
	catch (ChecksumException e) 
	{
		if(rotate==false) return readQRCode(image,true);							
		else e.printStackTrace(); return "Checksum Error";
	} 
	catch (FormatException e)   
	{
		if(rotate==false) return readQRCode(image,true);							
		else e.printStackTrace(); return "Format Error";
	}    
}
}
