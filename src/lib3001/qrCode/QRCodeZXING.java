package lib3001.qrCode;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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



/***************************************************************************
*	V1.4				Mr. Maxwell						23.12.2025			*
* 	LIB3001 Bibliotheks Klasse												*
* 	Letzte Änderung:														*
* 	- printQR_on_JDialog() Methode hinzugefügt								* 
*	Erzeugt einen QR-Code oder liest ein CR-Code von einem Image ein.		*
*	Benötigt zxing.rar Bibliothek											*
****************************************************************************/



public class QRCodeZXING
{



	
	/**	Öffnet ein JDialog (Ein neues Fenster) und zeigt den QR-Code an.
	Der JDialog ist hier als Modal gesetzt. (Wenn nicht Modal verwendet werden soll, dann die Methode:printQR() verwenden!)
 	Automatische Größenänderung des QR-Codes beim Verändern der Größe des Fensters
	@param data String aus dem der QR-Code erzeugt werden soll.
	@param title Tittel des JFrame Fensters (Kann null sein)
	@param color1 Hintergrundfarbe
	@param color2 Schriftfarbe
	@param x postion X
	@param y position Y	  
	Achtung Änderungen an setModal() sind extrem buggy und müssen ausgiebig getestet werden!**/
public static void printQR_on_JDialog(String data, String title, Color color1, Color color2, int x, int y) throws Exception
{
	BufferedImage bi = QRCodeZXING.writeQRCode(data, color1, color2, 1024, 1024);
	JLabel lbl = new JLabel(new ImageIcon(bi));
	lbl.setBounds(0, 0, 1200, 1200);
	JDialog d = new JDialog();
	d.setTitle(title);
	d.addComponentListener(new ComponentAdapter() 
	{
		public void componentResized(ComponentEvent e) 
		{
			int minSize = Integer.min(d.getWidth(), d.getHeight());
			lbl.setIcon(new ImageIcon(new ImageIcon(bi).getImage().getScaledInstance(minSize, minSize, 2)));				
		}
	});
	d.setBounds(x, y, 500, 520);
	d.add(lbl);
	d.setModal(true);
	d.setVisible(true);	
}	
	
		


	
/**	Öffnet ein JFrame (Ein neues Fenster) und zeigt den QR-Code an.
 	Automatische Größenänderung des QR-Codes beim Verändern der Größe des Fensters
	@param data String aus dem der QR-Code erzeugt werden soll.
	@param title Tittel des JFrame Fensters (Kann null sein)
	@param color1 Hintergrundfarbe
	@param color2 Schriftfarbe
	@param x postion X
	@param y position Y	**/
public static void printQR(String data, String title, Color color1, Color color2, int x, int y) throws Exception
{
	BufferedImage bi = QRCodeZXING.writeQRCode(data, color1, color2, 1024, 1024);
	JLabel lbl = new JLabel(new ImageIcon(bi));
	lbl.setBounds(0, 0, 1200, 1200);
	JFrame d = new JFrame();
	d.setTitle(title);
	d.setBounds(x, y, 500, 520);
	d.add(lbl);
	d.setVisible(true);
	d.addComponentListener(new ComponentAdapter() 
	{
		public void componentResized(ComponentEvent e) 
		{
			int minSize = Integer.min(d.getWidth(), d.getHeight());
			lbl.setIcon(new ImageIcon(new ImageIcon(bi).getImage().getScaledInstance(minSize, minSize, 2)));				
		}
	});
}	
	
	
	
	
/**	Erzeugt einen QR-Code aus einem Text-String.
	@param data String aus dem der QR-Code erzeugt werden soll.
	@param color1 Hintergrundfarbe
	@param color2 Schriftfarbe
	@param x Breite
	@param y Höhe
	@return base64-Code des PNG-QR Codes  **/
public static String writeQRCodetoBase64(String data, Color color1, Color color2, int x, int y) throws IOException 
{	
  	BufferedImage bi = QRCodeZXING.writeQRCode(data, color1, color2, x, y);	   	
	ByteArrayOutputStream  bo = new ByteArrayOutputStream ();
	ImageIO.write(bi, "png", bo);
	byte[] b = bo.toByteArray();   	
	bo.close();
	return new String(Base64.getEncoder().encode(b));
}
	
	

/**	Erzeugt einen QR-Code aus einem Text-String.
@param data String aus dem der QR-Code erzeugt werden soll.
@param color1 Hintergrundfarbe
@param color2 Schriftfarbe
@param x Breite
@param y Höhe
@return Byte-Array des PNG-QR Codes  **/
public static byte[] writeQRCodetoByteArray(String data, Color color1, Color color2, int x, int y) throws IOException 
{	
	BufferedImage bi = QRCodeZXING.writeQRCode(data, color1, color2, x, y);	   	
	ByteArrayOutputStream  bo = new ByteArrayOutputStream ();
	ImageIO.write(bi, "png", bo);
	byte[] b = bo.toByteArray();   	
	bo.close();
	return b;
}


    
/**	Erzeugt einen QR-Code aus einem Text-String.
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