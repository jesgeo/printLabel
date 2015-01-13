package applet;

import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.print.*;
import javax.swing.JOptionPane;




public class WebApp extends Applet {
	
	private static final long serialVersionUID = 1839485453686620321L;

	private PrinterJob printJob = null;
	private PrintService ps = null;
	private Address address = null;
	
	protected Preferences prefs;
	
	protected String systemPrinterName = null;
	
	public void init() {
		setLayout(new FlowLayout());
		
		prefs = Preferences.userRoot().node("Printlabel");
		printJob = PrinterJob.getPrinterJob();
				       
        setAddress();
        systemPrinterName = getParameter("autoprinter") == null ? "idntkno" : getParameter("autoprinter");
        
                
        // GUI paint
        setBackground (Color.black);
        setForeground (Color.white);
        Button pButton = new Button("Print"); 
        
        add(pButton);
        
        pButton.addActionListener(new ActionListener (){
			@Override
			public void actionPerformed(ActionEvent a) {
				printDocument();
			}
        });
        
	}
	
	private Address setAddress(){
		
		address = new Address();
        address.name = getParameter("name");
        address.address1 = getParameter("address1");
        address.address2 = getParameter("address2");
        address.town = getParameter("town");
        address.county = getParameter("county");
        address.postcode = getParameter("postcode");
        address.country = getParameter("country");
        
        return address;
        
		
	}
	
	/**
	 * 
	 * @returns true if successfully sets a printer to the printJob
	 */
	private boolean setPrinter() {
		
		PrintService[] ps = PrinterJob.lookupPrintServices();

		try {
	        // try automatic selection
			for (int i = 0; i < ps.length; i++){
	        		        	
				String thisPrinter  = ps[i].getName().toLowerCase();
				// get preferred printer for this particular user
				if (prefs.get("printer", "") != "" ){
					if (thisPrinter.contains(prefs.get("printer", ""))){
						this.ps = ps[i];
						break;
					}
				}
				// select printer by system specification
	        	if (thisPrinter.contains(systemPrinterName))
						this.ps = ps[i];
	        }
				        
	        if (this.ps != null) // preferred printer found
	        	printJob.setPrintService(this.ps);
	        else {
	        	// otherwise recursive ask user to select a printer
	        	while (this.ps == null) {
	        		if (printJob.printDialog()){
		        		this.ps = printJob.getPrintService();
		        		prefs.put("printer", this.ps.getName().toLowerCase()); // user preference		        		
		        	} else { // confirm box to exit the loop
		        		if ( JOptionPane.showConfirmDialog(this, 
		        				"Please select a printer !", 
		        				"Warning", 
		        				JOptionPane.OK_CANCEL_OPTION, 
		        				JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION )
		        			break;
		        	}
	        	}
	        }
	        	        
	        if (this.ps != null) {	        	
	        	return true;
	        } else 
	        	return false;
	        
		} catch (PrinterException e) {
			JOptionPane.showMessageDialog(this, e.getMessage() , "Error Occured", JOptionPane.WARNING_MESSAGE);
			e.printStackTrace();
			return false;
		}
		
	}
	
	private PageFormat getLabelFormat(){
		
		PageFormat pf = printJob.defaultPage();
        Paper paper = pf.getPaper();    
        double width = fromCMToPPI(3.5);
        double height = fromCMToPPI(8.8);    
        paper.setSize(width, height);
        paper.setImageableArea(
                        fromCMToPPI(0.25),
                        fromCMToPPI(0.5), 
                        width - fromCMToPPI(0.35), 
                        height - fromCMToPPI(1));
        pf.setOrientation(PageFormat.LANDSCAPE);
        pf.setPaper(paper);
		return pf;
	}
	
	public void printDocument() {
		
		if (!address.validate()) {
    		JOptionPane.showMessageDialog(this, "Error in parsing address" , "Warning", JOptionPane.ERROR_MESSAGE);
    		return;
		}	
		
		if (!setPrinter())
			return;
			
		printJob.setPrintable(new AddressLabelPrintable(address), 
				getLabelFormat());
		
		try {
			printJob.print();
		} catch (PrinterException e) {
			JOptionPane.showMessageDialog(this, e.getMessage() , "Error Occured", JOptionPane.WARNING_MESSAGE); 
			e.printStackTrace();
		}
	}
	
	public void clearPrefs() throws BackingStoreException{
		prefs.clear();
	}
	
	public void paint(Graphics g){
		
		String strParameter = address.name;
		if (strParameter == null)
			strParameter = "-ZigZag Education-";
		g.drawString(strParameter, 50, 25);
				 
	}
	
	protected static double fromCMToPPI(double cm) {            
        return toPPI(cm * 0.393700787);            
    }

    protected static double toPPI(double inch) {            
        return inch * 72d;            
    }

}
