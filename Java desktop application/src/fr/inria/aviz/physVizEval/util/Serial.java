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

public class Serial implements SerialPortEventListener {
	
	// Constants
	private static Serial _instance;
	
	static final String NEWVAL_DELIMITER = ",";
	static final String NEWROW_DELIMITER = "\n";
	static final double MIN_VALUE = -255;
	static final double MAX_VALUE = 255;
	static final String PREFIX = "#YPR=";
	
	private float yawOffset = 0;
	private float pitchOffset = 0;
	private float rollOffset = 0;
	private boolean calibrate = false;
	private boolean connected = false;
	
	boolean binary = false;
	
	private Logger logger;

	private Coord3d rotation = new Coord3d();
	
	ModelRotation modelRotation;
	
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
			//"/dev/tty.Free2moveWU-SerialPort", // bluetooth Mac OS X
			"/dev/tty.usbserial-A8003LIa", // Mac OS X
			"/dev/ttyUSB0", // Linux
			"COM9", // Windows
			};
	/** Buffered input stream from the port */
	private InputStream input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 115200;
	private CustomView view;
	ArrayList<Float> values;
	
	String currentMatrixChunk = "";

//	public Serial(CustomView _view)
//	{
//		super();
//		this.view = _view;
//	}
	
	public static synchronized Serial getInstance()
	{
		if (_instance == null)
			_instance = new Serial();
		return _instance;
	}
	
	public void setModelRotation(ModelRotation m)
	{
		modelRotation = m;
	}
	
	
	public void calibrate()
	{
		calibrate = true;
	}
	
	public void setView(CustomView v) throws IOException
	{
		this.view = v;
//		if (connected)
//			if (v == null)
//				output.write("#o0".getBytes()); // turn off continuous output
//			else
//				output.write("#o1".getBytes()); // turn on continuous output


	}
	
//	public void setLogger(Logger l)
//	{
//		logger = l;
//	}
	
	@Override
	public void finalize()
	{
		close();
	}
	
	public boolean initialize() {
		
		System.out.print("Initializing serial... ");
        Runtime.getRuntime().addShutdownHook(new Thread(){
        	public void run() {
//	        	if (serialConnected)
	        		close();
        	}
        });
		
        values = new ArrayList<Float>();
        
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		System.out.println("available port names: ");
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			for (String portName : PORT_NAMES) {
				System.out.println(portName);
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
			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		System.out.println("Done. ");
		
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		if (binary)
		{
			try {
		
				System.out.println("sending Razor configuration now...");
				input.read(new byte[input.available()], 0, input.available()); // empty buffer
				output.write("#ob".getBytes()); // turn on binary output
				output.write("#o1".getBytes()); // turn on continuous output
				output.write("#oe0".getBytes()); // disable error message output
				output.write("#s00".getBytes());
				// sync with razor
				boolean synced = false;
				long time0 = System.currentTimeMillis();
				long time = time0;
				for (int i = 0; i < 3; i++) // try sync 3 times
				{
					while (!synced && time - time0 < TIME_OUT)
					{
						time = System.currentTimeMillis();
						int available = 0;
						String syncToken = "#SYNCH00\r\n";
						while (available < syncToken.length())
						{
							available = input.available();
						}
						byte chunk[] = new byte[syncToken.length()];
						input.read(chunk, 0, syncToken.length());
						String chunkstring = new String(chunk);
						if (chunkstring.equals(syncToken))
						{
							synced = true;
							currentMatrixChunk = chunkstring.substring(chunkstring.indexOf("#SYNCH00\r\n") + "#SYNCH00\r\n".length());
						
						}
					}
					if (synced)
					{
						i = 3;
						// add event listeners
						serialPort.addEventListener(this);
						serialPort.notifyOnDataAvailable(true);
						System.out.println("Sync with Razor successful. Left over string: " + currentMatrixChunk);
					}
					else
					{
						System.err.println(i + ". sync attempt failed");
						time0 = System.currentTimeMillis();
						time = time0;
						input.read(new byte[input.available()], 0, input.available()); // empty buffer
						output.write("#s00".getBytes());

					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (TooManyListenersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return connected;
	}

	public boolean isConnected() {
		return connected;
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			int available;
			try {
				available = input.available();
				input.read(new byte[available], 0, available);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
		values.clear();
//		if (handlingEvent)
//			System.err.println("******* COLLISION");
		handlingEvent = true;
		
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			int available = -1;
			try {
				available = input.available();
//				System.out.println("bytes available: " + available);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (binary && available >= 12)
			{
				byte chunk[] = new byte[12];
				int noOfFramesToDump = available/12 - 1;
				
				try {
					input.skip(noOfFramesToDump * 12);
					input.read(chunk, 0, 12);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int i = 0; i < 3; i++)
				{ 
					values.add(Float.intBitsToFloat(chunk[i*4] + chunk[i*4+1] << 8) + 
							(chunk[i*4+2] << 16) + (chunk[i*4+3] << 24));
				}
				System.out.println("reading " + values);
			}
			else if (!binary)
			{
				try {
	//				if (available + currentMatrixChunk.length() >= 12)
					
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
							currentMatrixChunk = nextChunk;

						}
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}

			}			
//			if (values.size() == 3)
			{
			////////// filtering
				
				if (filterEnabled) {
		    		double timestamp = System.currentTimeMillis() / 1000.0 - timestamp0;
		    		values.set(0, (float)filter_x.filter(values.get(0), timestamp));
		    		values.set(1, (float)filter_y.filter(values.get(1), timestamp));
		    		values.set(2, (float)filter_z.filter(values.get(2), timestamp));
				}
											
				////////////////////
			//

				if (calibrate)
				{
					yawOffset = -values.get(0);
					pitchOffset = 0;//-values.get(1);
					rollOffset = 0;//-values.get(2);// - 45;
					
//								rotation.set(values.get(0), values.get(1), values.get(2));
//								view.setExtrinsincAnglesOffset(new Coord3d(0, 0, 0));
//								view.setRotationVector(rotation);
					
//								Coord3d extrotation = view.modelRotation.getExtrinsicAngles();
//								view.setExtrinsincAnglesOffset(extrotation.mul(-1));								
					
					rotation.set(values.get(0) + yawOffset, values.get(1) + pitchOffset, values.get(2) + rollOffset);
					modelRotation.setExtrinsicAnglesOffset(new Coord3d(0, 0, 0));								
					modelRotation.setIntrinsicAngles(rotation);
					Coord3d extrotation = modelRotation.getExtrinsicAngles();
					modelRotation.setExtrinsicAnglesOffset(new Coord3d(0, 0, -extrotation.z));								
					
//								view.setRotationVector(rotation);
					
					calibrate = false;
					System.out.println("calibrated");
					
				} 
				else 
				{

					rotation.set(values.get(0) + yawOffset, values.get(1) + pitchOffset, values.get(2) + rollOffset);
			    	if (modelRotation != null)
			    		modelRotation.setIntrinsicAngles(rotation);
//								if (view != null)
//									view.setRotationVector(rotation);
					// NEEDED FOR LOG
					if (logger != null && modelRotation != null)
						logger.rotationEvent(modelRotation.getQuaternion());
				}
			}
//			else
//				System.err.println("parse error for " + (binary ? "binary" : "ascii") + " parsing. Only read " + values.size() + " values.");
						
				
				
				
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
		
		handlingEvent = false;

	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

//	public void addMatrixListener(MatrixListener l) {
//		listeners.add(l);
//	}
//	
//	protected void fireMatrixEvent(Matrix values) {
//		for (MatrixListener l : listeners)
//			l.matrixEvent(values);
//	}	
}
