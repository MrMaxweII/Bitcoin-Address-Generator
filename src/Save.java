import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;



	/************************************************
	* 						*
	*			save			*
	*						*
	*************************************************/



public class Save
{



	public static void saveGUI(Component c, String dateiName) throws IOException
	{
		saveBufferedImage(paintComponent(c),dateiName);
	}



	public static BufferedImage paintComponent(Component c)
	{
		BufferedImage img = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		c.paintAll(g);
		g.dispose();
		return img;
	}	  



	public static void saveBufferedImage(BufferedImage img, String name) throws IOException
	{
		JFileChooser chooser = new JFileChooser();
	    chooser.setSelectedFile(new File(name));
	    chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
	    int button = chooser.showSaveDialog(null);
	    File file = chooser.getSelectedFile();
		if(button==0)
		{
	       FileOutputStream outputStream = new FileOutputStream(file);
	       ImageIO.write(img, "png", outputStream);
		}
	}
}
