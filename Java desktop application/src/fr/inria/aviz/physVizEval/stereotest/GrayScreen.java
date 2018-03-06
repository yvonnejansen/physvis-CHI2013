package fr.inria.aviz.physVizEval.stereotest;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;

public class GrayScreen {

	public static JFrame win = null;
	
	public static void init() {
		// Create and show black screen
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
	    Rectangle screenBounds = gs[0].getDefaultConfiguration().getBounds();
		win = new JFrame();
		win.getContentPane().setBackground(new Color(0.88f, 0.88f, 0.88f));
		win.setVisible(false);
		win.setUndecorated(true);
		//win.setAlwaysOnTop(true);
		win.setBounds(screenBounds);		
	}
	
	public static void display() {
		
		// If window and components not created, do it.
		if (win == null)
			init();
		
		// Show window
		win.setVisible(true);
		win.toFront();	
	}
	
	public static void hide() {
		if (win == null || !win.isVisible())
			return;		
		win.setVisible(false);
	}
}
