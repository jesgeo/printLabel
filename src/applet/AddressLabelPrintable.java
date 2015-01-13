package applet;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Arrays;

public class AddressLabelPrintable implements Printable {

	private Address address = null;
	
	public AddressLabelPrintable(Address a){
		address = a;
	}
	
	@Override
	public int print(Graphics g, PageFormat pf, int pi)
			throws PrinterException {
		if (pi > 0) return Printable.NO_SUCH_PAGE;
		
		Graphics2D g2d = (Graphics2D) g;
		
		// resize 
        g2d.translate((int) pf.getImageableX(), 
            (int) pf.getImageableY());
		
        //double width = pf.getImageableWidth();
        //double height = pf.getImageableHeight();
        
        Font f = new Font("TimesRoman", Font.PLAIN, 12);
        g2d.setFont(f);
        
        // name and address line 1
		String faddr = address.name+"\n"+address.address1+"\n";
		// if address line 2
		if (address.address2 != null && address.address2.length() > 2)
			 faddr += address.address2 + "\n";
		// town / address line 3 and if post-code (tab)
		faddr += address.town+"\t\t";
		if (address.postcode != null && address.postcode.length() > 2)
			faddr += address.postcode+"\n";
		else
			faddr += "\n";
		// add if county when address line 2 is missing
		if (address.address2.length() < 2)
			if (address.county != null && address.county.length() > 2)
				faddr += address.county+"\n";
		// add country if no post-code is given		
		if (address.postcode.length() < 2 || address.postcode == "0")
			faddr += address.country+"\n";
		
		faddr = faddr.replace("\n\n", "\n"); // remove multiple breaks
		
		drawString(g2d, faddr, 15f, 10f);
        
		
	    return Printable.PAGE_EXISTS;   
	}
	
	/**
	 * Draw/Paint formatted text onto a referenced graphics
	 * 
	 * 		Line breaks are defined by '\n' and tabs are set by '\t'
	 * 
	 * @param g - Graphics Instance 
	 * @param str - Formatted Text
	 * @param x - Starting X value
	 * @param y - Starting Y value
	 */
	private void drawString(Graphics2D g, String str, float x, float  y){
		float m = g.getFontMetrics().getHeight()-2;
		
		float strtX = x;
		
		for (String line : str.split("\n")){
			if (line.contains("\t")){
				y += m;
				ArrayList<String> tabbed = new ArrayList<String>(Arrays.asList(line.split("\t")));
				float sWidth = g.getFontMetrics().stringWidth(tabbed.get(0));
				g.drawString(tabbed.get(0), x, y);
				tabbed.remove(0);
				for (String st : tabbed) {
					x += sWidth+m;
					g.drawString(st, x, y);
					sWidth += g.getFontMetrics().stringWidth(st);
				}
	            x = strtX;
			} else
				g.drawString(line, x, y += m);
		}
	}

}
