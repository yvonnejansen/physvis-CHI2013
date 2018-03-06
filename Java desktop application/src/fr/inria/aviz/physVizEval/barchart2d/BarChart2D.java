package fr.inria.aviz.physVizEval.barchart2d;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVizEval.barmodels.Utils;
import fr.inria.aviz.physVizEval.stereotest.BlackScreen;
import fr.inria.aviz.physVizEval.stereotest.GrayScreen;
import fr.inria.aviz.physVizEval.util.GUIUtils;

public class BarChart2D {

	private static JVisualizationContainer vis = null;
	private static JFrame win = null;
	private static GraphicsDevice device = null;
	
	public static void init() {
		// Initialize device
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
	    device = gs[0];
	    Rectangle screenBounds = device.getDefaultConfiguration().getBounds();

		// Create window and components
		win = new JFrame();
		win.setUndecorated(true);
		Container c = win.getContentPane();
		vis = new JVisualizationContainer(new JMatrixVisualization());
		c.add(vis, BorderLayout.CENTER);
		win.setVisible(false);
		//win.setAlwaysOnTop(true);
		win.setBounds(0, 0, 1920, 1080);
		
		// Attempt at solving the HP screen bug
		win.setVisible(true);
		vis.setData(null);
		try {
			Thread.sleep(2000);
		} catch (Exception e) {}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				win.setVisible(false);
			}
		});
	}
	
	public static void display(final String filename) {
		
		// If window and components not created, do it.
		if (win == null)
			init();
		
		// Do a flicker to make it clear the content is changing
		win.setVisible(false);
		try {
			Thread.sleep(300);
		} catch (Exception e) {}

		// Update data
		MatrixData data = new MatrixData(filename, null);
		if (data.getDataInfo() == null) {
			DataInfo defaultInfo = new DataInfo("No metadata file", new AxisLabeling(data.computeMin(), data.computeMax(), 0, 1, ""), null);
			data.setDataInfo(defaultInfo);
		}
		win.setVisible(true); // needed, otherwise won't refresh properly.
		vis.setData(data);
		vis.repaint();
		vis.paintImmediately(vis.getBounds());
		
		// Show window
		win.setVisible(true);
		win.toFront();
				
	}
	
	public static void hide() {
		if (win == null || !win.isVisible())
			return;		
		vis.setData(null);
		win.setVisible(false);
	}
	
	// Test
	public static void main(String[] args) {
				
		// Show black screen. Will normally always remain in the background.
		//GrayScreen.display();
		
		BarChart2D.init();
//		try {
//			Thread.sleep(500);
//		} catch (Exception e) {}
		
//		BarChart2D.display("data/datasets/grosscapital.csv");
		BarChart2D.display(Utils.chooseFiles("data/datasets/", "csv")[0].getAbsolutePath());
//		try {
//			Thread.sleep(4000);
//		} catch (Exception e) {}
//		BarChart2D.display("data/datasets/carmortality-10x10.csv");
//		try {
//			Thread.sleep(4000);
//		} catch (Exception e) {}
//		BarChart2D.display("data/datasets/carmortality-10x10.csv");
//		try {
//			Thread.sleep(4000);
//		} catch (Exception e) {}
//		BarChart2D.hide();
//		GrayScreen.hide();
		
		
	}
	
}
