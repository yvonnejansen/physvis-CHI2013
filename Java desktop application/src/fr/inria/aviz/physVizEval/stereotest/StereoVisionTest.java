package fr.inria.aviz.physVizEval.stereotest;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVizEval.barchart2d.JMatrixVisualization;
import fr.inria.aviz.physVizEval.barchart2d.JVisualizationContainer;
import fr.inria.aviz.physVizEval.util.GUIUtils;
import fr.inria.aviz.physVizEval.util.GUIUtils.AdvancedKeyListener;

/**
 * A component for stereo calibration and vision test.
 * 
 * The vision test is a small subset of http://3d.mcgill.ca/
 * 
 * @author dragice
 *
 */
public class StereoVisionTest extends JStereoComponent {

	int screen = 1;
	boolean rightAnswer1 = false, rightAnswer2 = false;
	Font f = new Font("Times", 0, 24);
	//Font f = new Font("Helvetica", 0, 18);
	static Image fdTestImageLeft, fdTestImageRight, bdTestImageLeft, bdTestImageRight;
	static {
		try {
			fdTestImageLeft = ImageIO.read(new File("images/stereovision-test/fd05s0-left.png"));
			fdTestImageRight = ImageIO.read(new File("images/stereovision-test/fd05s0-right.png"));
			bdTestImageLeft = ImageIO.read(new File("images/stereovision-test/bd05s0-left.png"));
			bdTestImageRight = ImageIO.read(new File("images/stereovision-test/bd05s0-right.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	boolean displayMessages = true;
	
	/**
	 * 
	 * Starts the stereo calibration and vision test.
	 * 
	 * Set the last parameter to false in case you're calling this from another application
     *
	 * @param width
	 * @param height
	 * @param stereoRendering
	 * @param exitJavaWhenFinished
	 */
	public static void startTest(int width, int height, StereoRenderingMethod stereoRendering, boolean exitJavaWhenFinished) {
		JFrame win = new JFrame();
		win.getContentPane().add(new StereoVisionTest(stereoRendering, true, true));
		if (exitJavaWhenFinished)
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GUIUtils.centerOnPrimaryScreen(win, width, height);
		win.setVisible(true);
	}

	
	public StereoVisionTest(StereoRenderingMethod renderingMethod, boolean displayMessages, boolean keyboardEnabled) {
		super();
		this.displayMessages = displayMessages;
		setStereoRenderingMethod(renderingMethod);
		
		if (keyboardEnabled) {
			GUIUtils.addAdvancedKeyListener(null, new AdvancedKeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}
				
				@Override
				public void keyReleased(KeyEvent e) {
				}
				
				@Override
				public void keyPressed(KeyEvent e) {
				}
				
				@Override
				public void keyRepeated(KeyEvent e) {
				}
				
				@Override
				public void keyPressedOnce(KeyEvent e) {
					int k = e.getKeyCode();
					if (screen == 1 && k == KeyEvent.VK_SPACE) {
						flicker();
						screen = 2;
						repaint();
					} else if (screen == 2 && (k == KeyEvent.VK_1 || k == KeyEvent.VK_2 || k == KeyEvent.VK_3 || k == KeyEvent.VK_NUMPAD1 || k == KeyEvent.VK_NUMPAD2 || k == KeyEvent.VK_NUMPAD3)) {
						if (k == KeyEvent.VK_2 || k == KeyEvent.VK_NUMPAD2)
							rightAnswer1 = true;
						flicker();
						screen = 3;
						repaint();
					} else if (screen == 3 && (k == KeyEvent.VK_1 || k == KeyEvent.VK_2 || k == KeyEvent.VK_3 || k == KeyEvent.VK_NUMPAD1 || k == KeyEvent.VK_NUMPAD2 || k == KeyEvent.VK_NUMPAD3)) {
						if (k == KeyEvent.VK_1 || k == KeyEvent.VK_NUMPAD1)
							rightAnswer2 = true;
						flicker();
						screen = 4;
						repaint();
					} else if (screen == 4 && k == KeyEvent.VK_ESCAPE) {
						 WindowEvent wev = new WindowEvent(SwingUtilities.windowForComponent(StereoVisionTest.this), WindowEvent.WINDOW_CLOSING);
			             Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
					}
				}
				
			}, false);		
		}
	}
	
	protected void flicker() {
		int oldScreen = screen;
		screen = -1;
		paintImmediately(getBounds());
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {}
		screen = oldScreen;
		
	}
	
	public void paint(Graphics g_, Eye eye) {
		Graphics2D g = (Graphics2D)g_;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int width = getWidth();
		int height = getHeight();
		
		g.setColor(Color.white);
		g.fill(getBounds());
		
		if (screen == 1) { 
			
			// == Screen 1
			
			// -- Draw the four circles
			int margin = 220;
			int additionalXmargin = 300;
			int size = 40;
			int xoffset = (eye == Eye.LEFT) ?  10 : -10;
			drawCircle(g, margin + additionalXmargin + size / 2 + xoffset, margin + size / 2, size);
			drawCircle(g, width - margin - additionalXmargin - size / 2 - xoffset, margin + size / 2, size);
			drawCircle(g, margin + additionalXmargin + size / 2 + xoffset, height - margin - size / 2, size);
			drawCircle(g, width - margin - additionalXmargin - size / 2 - xoffset, height - margin - size / 2, size);
	
			// -- Draw left / right eye
			g.setColor(Color.black);
			g.setFont(f);
			if (eye == Eye.LEFT) {
				String s = "Left eye";
				drawText(g, width/2, height * 0.48, s);
			} else {
				String s = "Right eye";
				drawText(g, width/2, height * 0.48, s);
			}
			
			// -- Draw the instructions
			if (displayMessages) {
				String s;
				s = "1. Move your chair so you face the middle of the screen at an arm's length distance.";
				drawText(g, width/2, height*0.2, s);
				s = "Then put the 3D glasses on. If you already wear glasses, put the 3D glasses on top.";
				drawText(g, width/2, height*0.2 + 25, s);
				
				s = "2. Make sure you read 'Left eye' below when you close your right eye and vice-versa.";
				drawText(g, width/2, height*0.4, s);
				s = "If not, adjust the height of your chair.";
				drawText(g, width/2, height*0.4 + 25, s);
		
				s = "3. Make sure you see only one circle on each corner with an eye closed (check with both eyes).";
				drawText(g, width/2, height*0.6, s);
				s = "If not, adjust the height of your chair.";
				drawText(g, width/2, height*0.6 + 25, s);
				s = "You will have to keep the same posture, so make sure you sit comfortably.";
				drawText(g, width/2, height*0.6 + 50, s);
		
				s = "Press Space when you're done.";
				drawText(g, width/2, height*0.8, s);
			}
			
		} else if (screen == 2 || screen == 3) {
			
			// == Screens 2 and 3
			
			// -- Draw the test image
			if (screen == 2)
				drawImage(g, width / 2, height / 2, eye == Eye.LEFT ? bdTestImageLeft : bdTestImageRight);
			else if (screen == 3)
				drawImage(g, width / 2, height / 2, eye == Eye.LEFT ? fdTestImageLeft : fdTestImageRight);
			
			// -- Draw the instructions
			if (displayMessages) {
				g.setColor(Color.black);
				g.setFont(f);
				String s;
				s = "Please read the 3 options below and type the number corresponding to what you see.";
				drawText(g, width/2, 100, s);
				s = "1) The square is in front of the screen";
				drawText(g, width/2, height - 100, s);
				s = "2) The square is behind the screen";
				drawText(g, width/2, height - 70, s);
				s = "3) Not sure";
				drawText(g, width/2, height - 40, s);
			}

		} else if (screen == 4) {
			
			// -- Draw the test results
			g.setFont(f);
			
			if (rightAnswer1 && rightAnswer2) {
				g.setColor(new Color(0, 0, 0));
				String s;
				s = "Thank you.";
				drawText(g, width/2, height/2 - 15, s);
				if (displayMessages) {
					s = "Press Escape to continue.";
					drawText(g, width/2, height/2 + 15, s);
				}
			} else {
				g.setColor(new Color(255, 0, 0));
				String s;
				s = "Stereo vision test failed.";
				drawText(g, width/2, height/2, s);
			}
		}
	}
	
	protected void drawCircle(Graphics2D g, int xcenter, int ycenter, int diameter) {
		g.setStroke(new BasicStroke(4));
		g.setColor(Color.gray);
		g.fillOval(xcenter - diameter / 2, ycenter - diameter / 2, diameter, diameter);
		g.setColor(Color.black);
		g.drawOval(xcenter - diameter / 2, ycenter - diameter / 2, diameter, diameter);
	}
	
	protected void drawText(Graphics g, double x, double y, String s) {
	    FontMetrics fm = g.getFontMetrics();
	    int x2 = (int)(x - fm.stringWidth(s) / 2);
	    int y2 = (int)(y - ((fm.getAscent() - (fm.getAscent() + fm.getDescent())) / 2));
	    g.drawString(s, x2, y2);
	}
	
	protected void drawImage(Graphics g, double x, double y, Image im) {
		int x2 = (int)(x - im.getWidth(null) / 2);
	    int y2 = (int)(y - im.getHeight(null) / 2);
	    g.drawImage(im, x2, y2, null);
	}

}
