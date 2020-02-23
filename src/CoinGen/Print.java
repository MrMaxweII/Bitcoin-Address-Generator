package CoinGen;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;



public class Print 
{
	
	static Component print;										
	static int x;
	static int y;
	static int width;
	static int height;



/**	Druckt ein Object an einem Drucker aus. Es wird das übliche Drucker-Dialogfeld geöffnet.
	@param print Das zu druckende Object, kann ein Panel sein, oder der ganze Frame.
	@param x X-Pos des Druckes
	@param y Y-Pos des Druckes
	@param width Breite des Druckes
	@param height Höhe des Druckes  **/
public static void PrintImage(Component print, int x, int y, int width, int height) 
{  
	Print.print = print;
	Print.x 	= x;
	Print.y 	= y;
	Print.width = width;
	Print.height= height;
	try 
	{
		PrinterJob pjob = PrinterJob.getPrinterJob();
		if (pjob.printDialog() == false) return;		
		pjob.setPrintable(new PrintObject());			
		pjob.print();
	} 
	catch (Exception e) {e.printStackTrace();}
	}
}



class PrintObject implements Printable
{
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int seiten) throws PrinterException 
	{
		if (seiten > 0) return NO_SUCH_PAGE;
		try 
		{
			BufferedImage img = paintComponent(Print.print);
			Graphics2D g2 = (Graphics2D) graphics;	
			g2.drawImage(img, Print.x, Print.y,Print.width,Print.height, null);
		}
		catch (Exception e) {return NO_SUCH_PAGE;}
		return PAGE_EXISTS;
	}
	
	private static BufferedImage paintComponent(Component c)
	{
		BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB); 
		Graphics2D g = img.createGraphics();
		c.paintAll(g);
		g.dispose();
		return img;
	}	
}
