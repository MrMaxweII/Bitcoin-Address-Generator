package lib3001.qrCode;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;



/***************************************************************************************************************
*	V1.3						optimiert		Mr. Maxwell									24.01.2024			*
* 	LIB3001 Bibliotheks Klasse																					*
*	Angepasste Verion die von JDialog aufgerufen werden kann. JDialog muss dem Konstruktor übergeben werden.	*
*	Diese Klasse ist von https://github.com/sarxos/webcam-capture												*
*	Mit dieser Klasse kann ein QR-Code mit der Kamera gescannt und ausgegeben werden.							*
*	Es wird automatisch die Kamera gestartet und ein Fenster geöffnet, welches das live Kamera-Bild anzeigt.	*
*	Nun muss mit der Kamera ein QR-Code gescannt werden.														*
*	Nach erfolgreichem Scan, schleißt die Kamera und der QR-Code wird als String zurück gegeben.				*
*	Achtung: die Methode getResult() blockiert den Thread bis ein Ergebnis kommt!								*
*	Diese Klasse kann auch als JDialog implementiert werden. (Mit Einschränkungen)								*
*	Einschränkung: Die Methode setModal(true); Darf hier nicht aufgerufen werden! Führt zum Chrash der Kamera!	*
*	Die Blockierung der Übergeordenten Fenster muss daher manuell erfolgen!										*
*	Beispiel zur Anwendung:																						*
*		QrCapture qr = new QrCapture(); 																		*
*		String str = qr.getResult();																			*
*		qr.close();																								*
*	in "str" ist dann der String des QR-Codes enthalten.														*
*																												*
*	Benötiget Bibliotheken:																						*
*	- webcam-capture-smal-0.3.12.jar	Kamera-API´s (wurde von mir angepasst: weitere libs, daraus entfernt)	*
*	- bridj-0.7.0.jar					Wird von webcam-Capture selbst benötigt									*
*	- zxing.jar							QR-Code API  															*
****************************************************************************************************************/



public class QrCapture extends JDialog implements Closeable 
{

	private Webcam 			webcam 	= null;
	private BufferedImage 	image 	= null;
	private Result 			result 	= null;
	private Exchanger<String> exchanger = new Exchanger<String>();

	
	/** Konstruktor starte die Kamera und scannt den QR-Code
	@param x Position des Frames 
	@param y Position des Frames 
	@param title Der Titel des Scann-Fensters*/
	public QrCapture(JDialog owner, String title, int x, int y) throws IOException 
	{
		super(owner);
		setBounds(new Rectangle(x, y, 250, 250));
		getContentPane().setLayout(new FlowLayout());
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		webcam = Webcam.getDefault();
		if(webcam==null) throw new IOException("no camera found");		
		webcam.setViewSize(WebcamResolution.QVGA.getSize());
		try {webcam.open();}
		catch(Exception e){throw new IOException("Camera Error! Check permission");}	
		getContentPane().add(new WebcamPanel(webcam));
		pack();
		setVisible(true);	
			
		// Wenn das Kamera Fenster durch Abbruch geschlossen wird
		addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosed(WindowEvent e) 
			{
				try {exchanger.exchange("",1,TimeUnit.MICROSECONDS);} 		// Hinzugefügt, da es sonnst zu einem Thread-Blocking kommt
				catch (InterruptedException e1) {e1.printStackTrace();} 
				catch (TimeoutException e1) {}
				close();
			}
		});
			
		// Aufnahme Endlos-Schleife
		final Thread daemon = new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				while (isVisible()) read();
			}
		});
		daemon.setDaemon(true);
		daemon.start();
	}

	
	// Wartet hier auf ein Ergebnis
	// Achtung, blockiert den Thread, bis ein Ergebnis kommt.  !!! 
	public String getResult() throws InterruptedException 
	{
		return exchanger.exchange(null);	
	}

	
	@Override
	public void close() 
	{
		webcam.close();
	}
	
	
	
	
//------------------------------------------ Private Methoden --------------------------------------------
	
	// Diese Methode wird in einer Endlosschleife solange wiederholt bis ein Scan erfolgreich ist oder abgebrochen wird.
	private void read() 
	{
		if (!webcam.isOpen()) 						return;	
		if ((image = webcam.getImage()) == null) 	return;
		try 
		{			
			BinaryBitmap bb = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));		
			result = new MultiFormatReader().decode(bb);
		} 
		catch (NotFoundException e) {return; }		
		if (result != null) 
		{
			try {exchanger.exchange(result.getText());} 
			catch (InterruptedException e) {return;} 
			finally {dispose();}
		}
	}	
}