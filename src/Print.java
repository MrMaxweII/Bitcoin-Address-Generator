import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;



	/********************************************
	* 											*
	*			Print							*
	*											*
	********************************************/



public class Print 
{



static Component gui;



	public static void PrintImage(Component c) 
	{ 
		gui = c;
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
			BufferedImage img = Save.paintComponent(Print.gui);
			Graphics2D g2 = (Graphics2D) graphics;
			g2.drawImage(img, 120, 160,Print.gui.getWidth()-400,Print.gui.getHeight()-470, null);
		}
		catch (Exception e) {return NO_SUCH_PAGE;}
		return PAGE_EXISTS;
	}	
}