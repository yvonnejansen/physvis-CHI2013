package fr.inria.aviz.physVizEval.stereotest;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVizEval.barchart2d.BarChart2D;
import fr.inria.aviz.physVizEval.stereotest.JStereoComponent.StereoRenderingMethod;

public class StereoTest {

	static JFrame win = null;
	static StereoVisionTest test = null;
	
	/**
	 * 
	 */
	public static void init() {
		
		// Initialize device
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
	    Rectangle screenBounds = gs[0].getDefaultConfiguration().getBounds();

		// Create window and components
		win = new JFrame();
		test = new StereoVisionTest(StereoRenderingMethod.ODD_EVEN_INTERLACED_SUBSAMPLED, false, false);
		Container c = win.getContentPane();
		c.add(test, BorderLayout.CENTER);
		win.setVisible(false);
		win.setUndecorated(true);
		//win.setAlwaysOnTop(true);
		win.setBounds(screenBounds);
		
		// Attempt at solving the HP screen bug
		test.screen = -1;
		win.setVisible(true);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				win.setVisible(false);
			}
		});
	}
	
	/**
	 * 
	 * @param page
	 */
	public static void display(int page) {
		
		// If window and components not created, do it.
		if (win == null)
			init();
		
		// Do a flicker to make it clear the content is changing
		win.setVisible(false);
		try {
			Thread.sleep(200);
		} catch (Exception e) {}

		// Update page
		test.screen = page;
		test.repaint();
		
		// Show window
		win.setVisible(true);
		win.toFront();	
	}
	
	public static void hide() {
		if (win == null || !win.isVisible())
			return;		
		win.setVisible(false);
	}
	
//	public static void main(String[] args) {
//		
//		// Starts the stereo calibration and vision test.
//		//
//		// Use StereoRenderingMethod.ODD_EVEN_INTERLACED in case the left and right eyes are inverted.
//		// Set the last parameter to false in case you're calling this test from another application.
//		StereoVisionTest.startTest(1920, 1080, StereoRenderingMethod.ODD_EVEN_INTERLACED_SUBSAMPLED, false);
//	}
	
	// Test
	public static void main(String[] args) {
		
		// Show black screen. Will normally always remain in the background
		GrayScreen.display();
		
		StereoTest.init();
		try {
			Thread.sleep(1000);
		} catch (Exception e) {}
		
		StereoTest.display(1);
		try {
			Thread.sleep(2000);
		} catch (Exception e) {}
		StereoTest.display(2);
		try {
			Thread.sleep(2000);
		} catch (Exception e) {}
		StereoTest.display(3);
		try {
			Thread.sleep(2000);
		} catch (Exception e) {}
		StereoTest.hide();
		GrayScreen.hide();
	}
	
}
