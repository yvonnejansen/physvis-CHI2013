package fr.inria.aviz.physVizEval.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import org.apache.batik.util.gui.CSSMediaPanel.AddMediumDialog;

import fr.inria.aviz.physVizEval.util.FilterTest.MyFilter;

public class Grapher extends JComponent {
	
	static class Value {
		double minValue;
		double maxValue;
		
		// x = time, y = value
		ArrayList<Point2D.Double> samples = new ArrayList<Point2D.Double>();
		
		Value(double minValue, double maxValue) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		
		void newValue(double value, double time) {
			samples.add(new Point2D.Double(time, value));
		}
	}
	
	private static Grapher instance = null;
	private double timerange = 4;// seconds
	private double lensRelativeSize = 0.1;
	private ArrayList<String> valueNames = new ArrayList<String>();
	private Hashtable<String, Value> values = new Hashtable<String, Value>();
	private double maxTime = 0;
	Rectangle lensBounds = new Rectangle();
	boolean lensVisible = false;
	boolean lensActive = false;
	boolean paused = false;
	
	private Grapher(double timerange) {
		super();
		this.timerange = timerange;
		
		MouseInputListener l = new MouseInputAdapter() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				setLensVisible(true);
				moveLens(arg0.getX(), arg0.getY());	
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				setLensVisible(true);
				moveLens(arg0.getX(), arg0.getY());	
			}
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				setLensActive(false);
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				setLensActive(true);
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				setLensVisible(false);
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				setLensVisible(true);
			}			
		};
		addMouseListener(l);
		addMouseMotionListener(l);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				setLensSize((int)(getWidth() * lensRelativeSize), (int)(getHeight() * lensRelativeSize));
			}
		});
		
		GUIUtils.addAdvancedKeyListener(null, new GUIUtils.AdvancedKeyListener() {
			
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					paused = !paused;
				}
			}
			
			public void keyRepeated(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void keyPressedOnce(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		}, false);
	}
	
	private void _addValue(String name, double minValue, double maxValue) {
		Value val = new Value(minValue, maxValue);
		valueNames.add(name);
		values.put(name, val);
		repaint();
	}
	
	private void _setValue(String name, double value, double timestamp) {
		
		Value val = values.get(name);
		val.newValue(value, timestamp);
		if (timestamp > maxTime && !paused)
			maxTime = timestamp;
		repaint();
	}
	
	private void moveLens(int x, int y) {
		int lw = lensBounds.width;
		int lh = lensBounds.height;
		lensBounds.setLocation(x - lw/2, y - lh/2);
		repaint();
	}
	
	private void setLensVisible(boolean visible) {
		lensVisible = visible;
		repaint();
	}
	
	private void setLensActive(boolean active) {
		lensActive = active;
		repaint();
	}

	private void setLensSize(int w, int h) {
		int lw = lensBounds.width;
		int lh = lensBounds.height;
		int x0 = lensBounds.x - lw/2;
		int y0 = lensBounds.y + lh/2;
		lensBounds.setSize(w, h);
		lensBounds.setLocation(x0 - w/2, y0 - h/2);
		repaint();
	}
	
	// Graphics constants
	final double dotsize = 0.5;
	final double linethickness = 0.05;
	final Color[] colors = new Color[] {Color.blue, Color.red, Color.green, Color.gray, Color.magenta, Color.cyan, Color.yellow};
	final Color lensColor = new Color(0.9f, 0.9f, 0.5f, 0.2f);
	Font font = new Font("Helvetica", 0, 10);
	// Graphics tmp variables
	private Rectangle2D.Double tmprect = new Rectangle2D.Double();
	private Line2D.Double tmpline = new Line2D.Double();
	Point2D.Double mouseDebug = new Point2D.Double();
	///////
	
	public void paint(Graphics g_) {
		
		Graphics2D g = (Graphics2D)g_;
		double winw, winh, winx, winy;
		double w = getWidth();
		double h = getHeight();
		if (!lensActive) {
			winx = 0;
			winy = 0;
			winw = w;
			winh = h;
		} else {
			winw = w * (w / lensBounds.getWidth()); 
			winh = h * (h / lensBounds.getHeight());
			winx = - winw * (lensBounds.getX() / w);
			winy = - winh * (lensBounds.getY() / h);
		}
		
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		double thickness = lensActive ? 3 : 1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(font);
		g.setStroke(new BasicStroke((float)(linethickness * thickness)));

		double time0 = maxTime - timerange;
		double time1 = maxTime;
		double dotsize2 = dotsize * thickness;

		int i = 0;
		for (String name: valueNames) {
			
			Color c = colors[i % colors.length];
			g.setColor(c);
			
			Value val = values.get(name);
			double value0 = val.maxValue;
			double value1 = val.minValue;
			
			Point2D.Double sample;
			int n = val.samples.size();
			double x0 = 0, y0 = 0;
			for (int s = 0; s < n; s++) {
				sample = val.samples.get(s);
				double x = winx + (sample.x - time0) / (time1 - time0) * winw;
				double y = winy + (sample.y - value0) / (value1 - value0) * winh;
				if (s > 0) {
					tmpline.setLine(x0, y0, x, y);
					g.draw(tmpline);
				}
				tmprect.setRect(x - dotsize2, y - dotsize2, dotsize2*2+1, dotsize2*2+1);
				g.fill(tmprect);
				x0 = x;
				y0 = y;
			}
			
			g.drawString(name, 10, 10 + i * font.getSize());
			
			i++;
		}
		
		if (lensVisible) {
			g.setColor(lensColor);
			g.fill(lensBounds);
		}
		
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(1));
		tmprect.setRect(mouseDebug.x - 6, mouseDebug.y - 6, 12, 12);
		g.draw(tmprect);
	}
	
	private static void makeInstance() {
		JFrame win = new JFrame("Grapher");
		instance = new Grapher(20);
		win.getContentPane().add(instance);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		//win.setSize(400, 200);
		//win.setLocation(screen.width - win.getWidth() - 5, screen.height - win.getHeight() - 5);
		GUIUtils.centerOnPrimaryScreen(win, screen.width, screen.height * 2 / 3);
		win.setVisible(true);
	}
	
	public static void addValue(String name, double minValue, double maxValue) {
		if (instance == null)
			makeInstance();
		instance._addValue(name, minValue, maxValue);
	}
	
	public static void setValue(String name, double value, double timestamp) {
		if (instance == null)
			makeInstance();
		instance._setValue(name, value, timestamp);
	}
	
	// test
	static int mousex = 0, mousey = 0;
	public static void main(String[] args) {
		
		final double noise = 1.5;
		Grapher.addValue("x", 0, 360);
		Grapher.addValue("y", 0, 360);
		Grapher.addValue("x filtered", 0, 360);
		Grapher.addValue("y filtered", 0, 360);
	    final OneEuroFilter filter_x = new OneEuroFilter(
	    		60, // default input frequency (not used)
    			-2.0, // min freq cutoff (Hz) -> the lower the more filtering at low speeds
    			0.3, // beta -> the higer the less lag at high speeds
    			2 // cutoff for computing speed (1 Hz = good default)
    	);
	    final OneEuroFilter filter_y = new OneEuroFilter(
	    		60, // default input frequency (not used)
    			-2.0, // min freq cutoff (Hz) -> the lower the more filtering at low speeds
    			0.3, // beta -> the higer the less lag at high speeds
    			2 // cutoff for computing speed (1 Hz = good default)
    	);
		
		instance.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				mousex = e.getX();
				mousey = e.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Timer timer = new Timer(1000/60, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int w = instance.getWidth();
				int h = instance.getHeight();
				double x = mousex * 360.0 / w + noise * (Math.random() - 0.5);
				double y = mousey * 360.0 / h + noise * (Math.random() - 0.5);
				double t = System.currentTimeMillis() / 1000.0;
				Grapher.setValue("x", x, t);
				Grapher.setValue("y", y, t);
				double xf = filter_x.filter(x, t);
				double yf = filter_y.filter(y, t);
				Grapher.setValue("x filtered", xf, t);
				Grapher.setValue("y filtered", yf, t);
				
				instance.mouseDebug.x = xf * w / 360;
				instance.mouseDebug.y = yf * h / 360;
			}
		});
		timer.setRepeats(true);
		timer.start();
	}
}
