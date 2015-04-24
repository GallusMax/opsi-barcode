package org.opsi;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;


public class BarCodePane extends JOptionPane{

	private static final long serialVersionUID = -2553429599369927823L;
	public static final String title = "OPSI Hostname ";
	public static final String Version = "0.4";
	protected static String mycode;
	public static String domain = "ub.hsu-hh.de";

	public BarCodePane() {
		// TODO Auto-generated constructor stub
	}

	public BarCodePane(String string) {
		mycode=string;
	}

	private static BufferedImage generate(String msg){
		//Create the barcode bean
        DataMatrixBean bean = new DataMatrixBean();
        
        final int dpi = 150;
        
        //Configure the barcode generator
        bean.setModuleWidth(UnitConv.in2mm(4.0f / dpi)); //makes the narrow bar 
                                                         //width exactly one pixel
        bean.doQuietZone(false);
//        bean.setShape(SymbolShapeHint.FORCE_SQUARE);
        
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
        
            //Generate the barcode
        bean.generateBarcode(canvas, msg);
            //Signal end of generation
        try {
			canvas.finish();
			return canvas.getBufferedImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		
		if(0<args.length){
			System.err.println("replacing domain with "+args[0]);
			domain=args[0];
		}
		
		BarCodePane me=new BarCodePane();

		// TODO find the hostname as known to OPSI (hn=..)
		final String hostname=me.envName();
		final String nicname=me.nicName();

		String barcodename;
		if(null != nicname) // windows again..
			barcodename=nicname;
		else
			barcodename=hostname;
			
		mycode="{'dns':'"+barcodename+"."+domain+"'}";
		mycode="{\"dns\":\""+barcodename+"."+domain+"\"}";
	
		final BufferedImage img = generate(mycode);

		Icon codeIcon = new Icon() {
			
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				// TODO generate and paint the QRcode 
				String ts = hostname;
				g.setColor(c.getForeground());
//				g.draw3DRect(x-20, y, 100, 100, true);
				g.drawBytes(ts.getBytes(), 0, ts.length(), x, y+20+img.getHeight()); // und als text
				g.drawImage(img, x, y, null);
			}
			
			@Override
			public int getIconWidth() {
				return img.getWidth();
			}
			
			@Override
			public int getIconHeight() {
				return img.getHeight()+20;
			}
		};
		
		JOptionPane.showMessageDialog(getRootFrame(), codeIcon,title+Version,JOptionPane.PLAIN_MESSAGE);

//		System.err.println("clicked");

		
	}
	
	/**
	 * 
	 * @return a String containing either COMPUTERNAME or HOSTNAME environment
	 */
	public String envName(){
		// try environment properties.
		String hostName = System.getenv("COMPUTERNAME");
		if (hostName == null)
			hostName = System.getenv("HOSTNAME");
		if (hostName == null) // still no result?
			hostName="noHostNameFound";
		return  hostName.toLowerCase();
	}
	
	
	/**
	 * 
	 * @return a String containing the hardware address of the first nic w/o colons
	 */
	public String nicName(){
	String hostName = "";
	byte[] hwAddress = {0,1,2,3,4,5,6};

	Enumeration<NetworkInterface> interfaces;
	try {
		interfaces = NetworkInterface.getNetworkInterfaces();
	
	    while (interfaces.hasMoreElements()) { 
	        NetworkInterface nic = interfaces.nextElement();
	        hwAddress=nic.getHardwareAddress();
	        if(null!=hwAddress && 0<hwAddress.length)
	        	break; // only the first address (non-null length) found
/*	        
	        Enumeration<InetAddress> addresses = nic.getInetAddresses();
	        while (hostName == null && addresses.hasMoreElements()) {
	            InetAddress address = addresses.nextElement();
	            if (!address.isLoopbackAddress()) {
	                hostName = address.getHostName();
	            }
	        }
*/	        
	    
	    }
	    for(int i=0;i<6;i++){
	    	hostName+=String.format("%02x", hwAddress[i]);
	    }
	    
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
	return hostName;

	}

}
