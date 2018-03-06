package fr.inria.aviz.physVizEval.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.TooManyListenersException;

import javax.swing.SwingUtilities;

import org.jzy3d.maths.Coord3d;
import org.openmali.vecmath2.util.MatrixUtils.EulerOrder;

import fr.inria.aviz.physVizEval.jzy3d.CustomCamera;
import fr.inria.aviz.physVizEval.jzy3d.CustomView;
import fr.inria.aviz.physVizEval.jzy3d.ModelRotation;

public class FilterTest implements SerialPortEventListener {
	
	// Constants
	private static FilterTest _instance;
	
	static final String NEWVAL_DELIMITER = ",";
	static final String NEWROW_DELIMITER = "\n";
	static final double MIN_VALUE = -255;
	static final double MAX_VALUE = 255;
	static final String PREFIX = "#YPR=";
	
	private boolean connected = false;
		
    // Filtering
    boolean filterEnabled = true;
    static class MyFilter extends OneEuroFilter {
    	public MyFilter() {
    		super(
    			60, // default input frequency (not used)
    			-1.2, // min freq cutoff (Hz) -> the lower the more filtering at low speeds
    			0.75, // beta -> the higer the less lag at high speeds
    			0.75 // cutoff for computing speed (1 Hz = good default)
    		);
    	}
    }
    OneEuroFilter filter_x = new MyFilter();
    OneEuroFilter filter_y = new MyFilter();
    OneEuroFilter filter_z = new MyFilter();
	double timestamp0 = System.currentTimeMillis() / 1000.0;
	
	//////////////////////////////////////////////////////
	
	SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.Free2moveWU-SerialPort", // bluetooth Mac OS X
			//"/dev/tty.usbserial-A8003LIa", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM3", // Windows
			};
	/** Buffered input stream from the port */
	private InputStream input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 38400;
	
	String currentMatrixChunk = "";

	public FilterTest()
	{
		initialize();
		if (connected)
			try {
				output.write("#o1".getBytes()); // turn on continuous output
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void finalize()
	{
		close();
	}
	
	public boolean initialize() {
		
		System.out.print("Initializing serial... ");
        Runtime.getRuntime().addShutdownHook(new Thread(){
        	public void run() {
        		close();
        	}
        });
		
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			System.out.println("Could not find COM port.");
			return false;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			connected = true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		System.out.print("Done. ");
		
		try {
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		}
		catch (TooManyListenersException e) {
			e.printStackTrace();
		}
		return connected;
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			System.out.println("closed serial port.");
		}
	}

	boolean handlingEvent = false;
	
	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		
//		if (handlingEvent)
//			System.err.println("******* COLLISION");
		handlingEvent = true;
		
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int available = input.available();
//				if (available + currentMatrixChunk.length() >= 12)
				{
					byte chunk[] = new byte[available];
					int n = input.read(chunk, 0, available);
//					System.out.println("reading " + n + " bytes");
					// Displayed results are codepage dependent
//					System.out.print(new String(chunk));
	
					String chunkstring = new String(chunk);
					
					currentMatrixChunk += chunkstring;
					
					
					if (currentMatrixChunk.contains(PREFIX)) {
						currentMatrixChunk = currentMatrixChunk.substring(currentMatrixChunk.lastIndexOf(PREFIX));
						if (currentMatrixChunk.contains(NEWROW_DELIMITER))
						{
//						while (searchString.contains(PREFIX) && searchString.contains(NEWROW_DELIMITER))
//						{
//							int index = searchString.indexOf(PREFIX);
//							prefixString = prefixString.concat(searchString.substring(0, index));
//							searchString = searchString.substring(index);
//							System.out.println("in while loop with index = " + index + " search string length = " + searchString.length());
//						}
						 
							ArrayList<Float> values = new ArrayList<Float>();
							int i = currentMatrixChunk.indexOf(NEWROW_DELIMITER);
							String nextChunk = currentMatrixChunk.substring(i + 1);
							currentMatrixChunk = currentMatrixChunk.substring(0, i);
							StringTokenizer t_row = new StringTokenizer(currentMatrixChunk, NEWVAL_DELIMITER);
							while (t_row.hasMoreTokens()) {
								String val = t_row.nextToken().trim();
								if (val.startsWith(PREFIX))
									val = val.substring(5);
								float value = Float.parseFloat(val);
								values.add(value);
								
							}
							
							////////// filtering
							
							if (filterEnabled) {
					    		double timestamp = System.currentTimeMillis() / 1000.0 - timestamp0;
					    		values.set(0, (float)filter_x.filter(values.get(0), timestamp));
					    		values.set(1, (float)filter_y.filter(values.get(1), timestamp));
					    		values.set(2, (float)filter_z.filter(values.get(2), timestamp));
							}
														
							////////////////////
						//
							currentMatrixChunk = nextChunk;

						}
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
		
		handlingEvent = false;

	}
}
