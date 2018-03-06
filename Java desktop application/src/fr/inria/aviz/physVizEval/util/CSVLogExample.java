package fr.inria.aviz.physVizEval.util;

import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

public class CSVLogExample {
	
	static long launchingTime;
	String logfile = "./data/logExample"; // The CSV class will add the .csv extension
	int logfileNumber = 1;
	CSV csv;

	public static void main(String[] args) {
		new CSVLogExample();
	}
	
	public CSVLogExample() {
		
		// Create CSV structure
		csv = new CSV();
		csv.addColumn("time", "timestamp in milliseconds since the launching of the application");
		csv.addColumn("x", "x mouse coordinate");
		csv.addColumn("y", "y mouse coordinate");
		csv.addColumn("comment", "some string");
		
		// Create window
		JFrame win = new JFrame("Move the mouse in the window then click when finished");
		GUIUtils.centerOnPrimaryScreen(win, 800, 600);
		win.setVisible(true);

		// Register mouse listeners
		MouseInputListener listener = new MouseInputAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				log(e.getX(), e.getY(), "This is an example of a string");
			}
			@Override
			public void mousePressed(MouseEvent e) {
				saveLog();
			}
		};
		win.getContentPane().addMouseListener(listener);
		win.getContentPane().addMouseMotionListener(listener);
	}
	
	public void log(int x, int y, String comment) {
		System.out.print(".");
		double t = System.nanoTime()/1000.0; // more precise than System.currentTimeMillis()
		int lastrow = csv.getRowCount();
		String[] rowArray = csv.getRowArray(lastrow); // faster than multiple calls to setValue()
		rowArray[0] = "" + t;
		rowArray[1] = "" + x;
		rowArray[2] = "" + y;
		rowArray[3] = comment;		
	}
	
	protected void saveLog() {
		System.out.println();
		System.out.print("Saving " + csv.getRowCount() + " rows to the log file...");
		boolean writeReadmeFile = logfileNumber == 1;
		csv.write(logfile + logfileNumber, writeReadmeFile);
		System.out.println("Done.");
		
		// Switch to a new file
		csv.clearRows();
		logfileNumber++;
	}

}
