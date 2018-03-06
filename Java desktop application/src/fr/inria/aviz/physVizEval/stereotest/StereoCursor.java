package fr.inria.aviz.physVizEval.stereotest;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import fr.inria.aviz.physVizEval.stereotest.JStereoComponent.StereoRenderingMethod;
import fr.inria.aviz.physVizEval.util.GUIUtils;

/**
 * See constructor for instructions. 
 * 
 * @author dragice
 *
 */
public class StereoCursor implements AWTEventListener {

	private static Image cursorImage;
	private static Point cursorHotPoint = new Point(0, 0);
	static {
		try {
			cursorImage = ImageIO.read(new File("images/cursors/cursor-std.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private StereoRenderingMethod stereoRenderingMethod = StereoRenderingMethod.ODD_EVEN_INTERLACED_SUBSAMPLED;
	int disparity;
	private Cursor normalCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, cursorHotPoint, "normal cursor");
	private Cursor evenLinesCursor, oddLinesCursor;
	
	Window window;
	private boolean enabled = true;
	Point lastMousePos = new Point(0, 0);

	/**
	 * Creates a new stereo cursor.
	 * 
	 * @param stereoRenderingMethod Note: the anaglyph method only works on black backgrounds for now.
	 * @param disparity: the horizontal offset between the left eye and the right eye, in pixels. Positive values
	 * yield a cursor in front of the screen plane, negative values move it behind. The best value is the one
	 * that is equal or slightly above the maximum disparity of the 3D scene.
	 */
	public StereoCursor(Window window, StereoRenderingMethod stereoRenderingMethod, int disparity) {
		this.window = window;
		this.stereoRenderingMethod = stereoRenderingMethod;
		generateCursorImages(disparity);
		updateCursor(null);
		EventHook.addMouseMonitor(this);
	}
	
	protected void generateCursorImages(int disparity) {
		
		// 1 - Paint cursor for left and right eye
		int w = cursorImage.getWidth(null);
		int h = cursorImage.getHeight(null);
		w += Math.abs(disparity) + 1;
		Dimension s = Toolkit.getDefaultToolkit().getBestCursorSize(w, h); // windows expects 32x32 cursors
		w = s.width;
		h = s.height;
		
		int x = Math.abs(disparity) / 2;
		BufferedImage left = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		BufferedImage right = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = left.createGraphics();
		g.drawImage(cursorImage, x + disparity/2, 0, null); 
		g = right.createGraphics();
		g.drawImage(cursorImage, x - disparity/2, 0, null); 
		
		// 2 - Paint the combined cursor
		Image evenCursorImage, oddCursorImage;
		boolean evenInterlacing = stereoRenderingMethod == StereoRenderingMethod.EVEN_ODD_INTERLACED || stereoRenderingMethod == StereoRenderingMethod.EVEN_ODD_INTERLACED_SUBSAMPLED;
		boolean oddInterlacing = stereoRenderingMethod == StereoRenderingMethod.ODD_EVEN_INTERLACED || stereoRenderingMethod == StereoRenderingMethod.ODD_EVEN_INTERLACED_SUBSAMPLED;
		if (evenInterlacing || oddInterlacing) {
			evenCursorImage = JStereoComponent.paintInterlaced(left, right, null, evenInterlacing);
			// Restore left and right images which have been modified by the interlaced paint method -> FIXME
			g = left.createGraphics();
			g.drawImage(cursorImage, x + disparity/2, 0, null); 
			g = right.createGraphics();
			g.drawImage(cursorImage, x - disparity/2, 0, null); 
			oddCursorImage = JStereoComponent.paintInterlaced(left, right, null, oddInterlacing);
		} else if (stereoRenderingMethod == StereoRenderingMethod.RED_BLUE_ANAGLYPH) {
			evenCursorImage = JStereoComponent.paintAnaglyph(left, right, null);
			oddCursorImage = evenCursorImage;
		} else {
			evenCursorImage = cursorImage;
			oddCursorImage = cursorImage;
		}
		
		// 3 - Create the Cursor objects
		// Note: the cursor's hot point is between the two eye images but its best position should actually match
		//       the user's director eye.
		int x_hotspot = cursorHotPoint.x + Math.abs(disparity / 2);
		evenLinesCursor = Toolkit.getDefaultToolkit().createCustomCursor(evenCursorImage, new Point(x_hotspot, cursorHotPoint.y), "Even line 3D cursor");
		if (oddLinesCursor != evenLinesCursor)
			oddLinesCursor = Toolkit.getDefaultToolkit().createCustomCursor(oddCursorImage, new Point(x_hotspot, cursorHotPoint.y), "Odd line 3D cursor");
		else
			oddLinesCursor = evenLinesCursor;
	}
	
	public StereoRenderingMethod getStereoRenderingMethod() {
		return stereoRenderingMethod;
	}

	public void setStereoRenderingMethod(StereoRenderingMethod stereoRenderingMethod) {
		this.stereoRenderingMethod = stereoRenderingMethod;
		generateCursorImages(disparity);
		updateCursor(null);
	}

	public int getDisparity() {
		return disparity;
	}

	/**
	 * Warning: this method is rather slow, so calling it too often might generate artifacts.
	 * @param disparity
	 */
	public void setDisparity(int disparity) {
		this.disparity = disparity;
		generateCursorImages(disparity);
		updateCursor(null);
	}

	protected void updateCursor(Point p) {
		Cursor c = window.getCursor();
		Cursor c2;
		if (enabled) {
			if (p == null)
				p = lastMousePos;
			if (p.y % 2 == 0)
				c2 = evenLinesCursor;
			else
				c2 = oddLinesCursor;
		} else {
			c2 = normalCursor;
		}
		if (c2 != c)
			window.setCursor(c2);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		updateCursor(null);
	}
	
	@Override
	public void eventDispatched(AWTEvent e) {
		if (e instanceof MouseEvent && (e.getSource() instanceof Component)) {
			MouseEvent ev = (MouseEvent)e;
			// movement
			if (e.getID() == MouseEvent.MOUSE_MOVED || e.getID() == MouseEvent.MOUSE_DRAGGED) {
				Point p = ev.getLocationOnScreen();
				updateCursor(p);
			}
		}
	}
	
	/////////// Test
	public static void main(String[] args) {
		JFrame win = new JFrame("Stereo cursor");
		GUIUtils.centerOnPrimaryScreen(win, 800, 600);
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.setVisible(true);
		StereoCursor cursor = new StereoCursor(win, StereoRenderingMethod.ODD_EVEN_INTERLACED, -10);
		cursor.setEnabled(true);
	}

}
