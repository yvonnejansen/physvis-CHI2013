package fr.inria.aviz.physVizEval.stereotest;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.swing.*;

/**
 * 
 * A Java component with a modified paint method that allows to draw different images for the left and the right eye:
 * public void paint(Graphics g, Eye eye);
 *
 * - The RED_BLUE_ANAGLYPH rendering method is very slow and is only provided for testing.
 * - Switching between ODD_EVEN_INTERLACED and EVEN_ODD_INTERLACED rendering methods will swap left and right eyes on passive 3D screens.
 *
 * @author dragice
 *
 */
public abstract class JStereoComponent extends JComponent {

	public enum StereoRenderingMethod {LEFT_ONLY, RIGHT_ONLY, EVEN_ODD_INTERLACED, EVEN_ODD_INTERLACED_SUBSAMPLED, ODD_EVEN_INTERLACED, ODD_EVEN_INTERLACED_SUBSAMPLED, RED_BLUE_ANAGLYPH};
	private StereoRenderingMethod stereoRenderingMethod = StereoRenderingMethod.ODD_EVEN_INTERLACED_SUBSAMPLED;

	public enum Eye {LEFT, RIGHT};

	BufferedImage left_buffer = null;
	BufferedImage right_buffer = null;
	BufferedImage combined_buffer = null;
	
	public JStereoComponent() {
		super();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// Force the recomputation of left and right buffers
				left_buffer = null;
				right_buffer = null;
				combined_buffer = null;
			}
		});
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		Window toplevel = SwingUtilities.windowForComponent(this);
		toplevel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) {
				// Repaint (for the interlaced mode)
				if (isInterlaced())
					JStereoComponent.this.repaint();
			}
		});
	}
	
	private boolean isInterlaced() {
		return (stereoRenderingMethod == StereoRenderingMethod.ODD_EVEN_INTERLACED || stereoRenderingMethod == StereoRenderingMethod.EVEN_ODD_INTERLACED || stereoRenderingMethod == StereoRenderingMethod.ODD_EVEN_INTERLACED || stereoRenderingMethod == StereoRenderingMethod.EVEN_ODD_INTERLACED_SUBSAMPLED);
	}
	
	private boolean isSubsampled() {
		return (stereoRenderingMethod == StereoRenderingMethod.ODD_EVEN_INTERLACED_SUBSAMPLED || stereoRenderingMethod == StereoRenderingMethod.EVEN_ODD_INTERLACED_SUBSAMPLED);
	}
	
	public abstract void paint(Graphics g, Eye eye);
	
	public void paint(Graphics g) {
		if (stereoRenderingMethod == StereoRenderingMethod.LEFT_ONLY) {
			paint(g, Eye.LEFT);
		} else if (stereoRenderingMethod == StereoRenderingMethod.RIGHT_ONLY) {
			paint(g, Eye.RIGHT);
		} else {
			if (left_buffer == null)
				left_buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			if (right_buffer == null)
				right_buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			if (combined_buffer == null)
				combined_buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			//
			paintComponentInBuffer(left_buffer, Eye.LEFT, isSubsampled());
			paintComponentInBuffer(right_buffer, Eye.RIGHT, isSubsampled());
			
			//
			clearImage(combined_buffer);
			Point pt = new Point();
			SwingUtilities.convertPointToScreen(pt, this);
			boolean reverseInterlacing = (pt.y % 2) == 1;
			if (stereoRenderingMethod == StereoRenderingMethod.RED_BLUE_ANAGLYPH)
				paintAnaglyph(left_buffer, right_buffer, combined_buffer);
			else if (stereoRenderingMethod == StereoRenderingMethod.ODD_EVEN_INTERLACED || stereoRenderingMethod == StereoRenderingMethod.ODD_EVEN_INTERLACED_SUBSAMPLED)
				paintInterlaced(left_buffer, right_buffer, combined_buffer, reverseInterlacing);
			else if (stereoRenderingMethod == StereoRenderingMethod.EVEN_ODD_INTERLACED || stereoRenderingMethod == StereoRenderingMethod.EVEN_ODD_INTERLACED_SUBSAMPLED)
				paintInterlaced(left_buffer, right_buffer, combined_buffer, !reverseInterlacing);
			
			g.drawImage(combined_buffer, 0, 0, null);
		}
	}
	
	private void paintComponentInBuffer(BufferedImage buffer, Eye eye, boolean verticalSubsample) {
		Graphics2D g = (Graphics2D)buffer.createGraphics();
		clearImage(buffer);
		
		if (!verticalSubsample)
			paint(g, eye);
		else {
			// FIXME: maybe don't use combined_buffer as a temporary buffer
			Graphics2D gtmp = (Graphics2D)combined_buffer.createGraphics();
			gtmp.setTransform(AffineTransform.getScaleInstance(1, 0.5));
			paint(gtmp, eye);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setTransform(AffineTransform.getScaleInstance(1, 2.0));
			g.drawImage(combined_buffer, 0, 0, null);
		}

	}
	
	public StereoRenderingMethod getStereoRenderingMethod() {
		return stereoRenderingMethod;
	}

	public void setStereoRenderingMethod(StereoRenderingMethod stereoRenderingMethod) {
		this.stereoRenderingMethod = stereoRenderingMethod;
	}

	/**
	 * 
	 */
	static BufferedImage paintAnaglyph(BufferedImage left, BufferedImage right, BufferedImage output) {
		int width = left.getWidth();
		int height = left.getHeight();
		if (output == null)
			output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int left_r, left_g, left_b, left_a;
		int right_r, right_g, right_b, right_a;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// Read pixel color in the left image
				int left_rgb = left.getRGB(x, y);
				left_a = (left_rgb >> 24) & 0xff ;
				left_r = (left_rgb >> 16) & 0xff ;
				left_g = (left_rgb >> 8) & 0xff ;
				left_b = left_rgb & 0xff ;
				// Read pixel color in the right image
				int right_rgb = right.getRGB(x, y);
				right_a = (right_rgb >> 24) & 0xff ;
				right_r = (right_rgb >> 16) & 0xff ;
				right_g = (right_rgb >> 8) & 0xff ;
				right_b = right_rgb & 0xff ;
				// Merge these colors.
				// Uses the optimized Anaglyph method from http://3dtv.at/Knowhow/AnaglyphComparison_en.aspx
				int r = (int)Math.round(0.7 * left_g + 0.3 * left_b);
				int g = (int)Math.round(right_g);
				int b = (int)Math.round(right_b);
				int a = (int)((left_a + right_a)/2);
				int rgb = (a << 24) | (r << 16) | (g << 8) | b;
				output.setRGB(x, y, rgb);
			}
		}
		return output;
	}
	
	/**
	 * 
	 */
	static BufferedImage paintInterlaced(BufferedImage left, BufferedImage right, BufferedImage output, boolean mapEvenLinesToLeftEye) {
		int width = left.getWidth();
		int height = left.getHeight();
		if (output == null)
			output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		// Erase half of the lines of the left image
		// FIXME: maybe not a good idea to change the buffered image
		Graphics2D g_left = left.createGraphics();
		g_left.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		int firstLineToErase = mapEvenLinesToLeftEye ? 1 : 0;
		for (int y = firstLineToErase; y < height; y += 2)
			g_left.drawLine(0, y, width, y);

		// Paint left image
		Graphics2D g = output.createGraphics();
		g.drawImage(left, 0, 0, null);
		
		// Erase half of the lines on the right image
		// FIXME: maybe not a good idea to change the buffered image
		Graphics2D g_right = right.createGraphics();
		g_right.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
		firstLineToErase = mapEvenLinesToLeftEye ? 0 : 1;
		for (int y = firstLineToErase; y < height; y += 2)
			g_right.drawLine(0, y, width, y);
		
		// Paint right image
		g.drawImage(right, 0, 0, null);
		
		return output;
	}
	
	/**
	 * Clears an image 
	 */
	private void clearImage(BufferedImage im) {
		Graphics2D g2D = im.createGraphics();
//		if (im.getType() == BufferedImage.TYPE_INT_ARGB) {
//			// Makes the image totally transparent
//			g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
//			Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, im.getWidth(), im.getHeight());
//			g2D.fill(rect);
//		} else {
			// Fill with the default bg color
			g2D.setColor(getBackground());
			g2D.fillRect(0, 0, im.getWidth(), im.getHeight());
//		}
	}
}
