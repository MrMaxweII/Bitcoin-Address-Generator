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



	/********************************************
	* 											*
	*			QRCodeReader ZXING				*
	*											*
	********************************************/



public class QRCodeReaderZXING
{



	public static BufferedImage writeQRCode(String data) 
	{
		int width  = 220, height = 220;
		QRCodeWriter writer = new QRCodeWriter();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int white = 255 << 16 | 255 << 8 | 255;
		int black = 0;
		try 
		{
		    BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height);
		    for (int i = 0; i < width; i++) 
		    {
		       for (int j = 0; j < height; j++) image.setRGB(i, j, bitMatrix.get(i, j) ? black : white);
		    }
		} 
		catch (WriterException e) {e.printStackTrace();}
		return image;
	}



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
	    	System.out.println("QR-Code: Bild wurde um 180� gedreht");
	    	if(rotate==false) return readQRCode(image,true);			
	    	else e.printStackTrace(); return "Not Found Fehler";
	    } 
	    catch (ChecksumException e) 
	    {
	    	System.out.println("QR-Code: Bild wurde um 180� gedreht");
	    	if(rotate==false) return readQRCode(image,true);			
	    	else e.printStackTrace(); return "Checksum Fehler";
	    } 
	    catch (FormatException e)   
	    {
	    	System.out.println("QR-Code: Bild wurde um 180� gedreht");
	    	if(rotate==false) return readQRCode(image,true);			
	    	else e.printStackTrace(); return "Format Fehler";
	    }    
    }
}
