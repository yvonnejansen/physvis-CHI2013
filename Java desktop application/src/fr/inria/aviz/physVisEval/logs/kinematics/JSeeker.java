package fr.inria.aviz.physVisEval.logs.kinematics;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.*;

import fr.inria.aviz.physVisEval.logs.kinematics.Kinematics.RotationEvent;

public class JSeeker extends JComponent {

	public static interface Listener {
		public void setTime(double t);
	}
	
    Kinematics kinematics = null;
    double time = 0;
    double datalength = 0;
    double maxlength = 2 * 60; // two minutes
    double minspeed = 0, maxspeed = 0.15;
//    double minacceleration = 0, maxacceleration = 0.01;
    double minacceleration = -0.01, maxacceleration = 0.01;
    ArrayList<Listener> listeners = new ArrayList<Listener>();
    Rectangle2D.Double tmprect = new Rectangle2D.Double();
    Line2D.Double tmpline = new Line2D.Double();
    
	public JSeeker() {
		addMouseListener(new MouseAdapter () {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				play();
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				pause();
				seek(e.getX());
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter () {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				seek(e.getX());
			}
		});
	}
	
	protected void pause() {
		MainTest.pause(); // FIXME
	}
	
	protected void play() {
		MainTest.play(); // FIXME
	}
	
	protected void seek(int mousex) {
		int w = getWidth();
		MainTest.seek(mousex/(double)w*maxlength); // FIXME
	}
	
	public void paint(Graphics g_) {
		Graphics2D g = (Graphics2D)g_;
		int w = getWidth();
		int h = getHeight();
		double x;
		
		// background
		g.setColor(Color.white);
		g.fill(getBounds());
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// area with data
		g.setColor(new Color(0.9f, 0.9f, 1.0f));
		x = datalength / maxlength * w;
		tmprect.setRect(0, 0, x, h);
		g.fill(tmprect);
		
		if (kinematics != null) {

			// speed
			g.setColor(new Color(0, 0, 0f, 0.4f));
			double y = -1, old_y = -1, old_x = -1;
			x = 0;
			for (RotationEvent ev : kinematics.events) {
				x = ev.time / maxlength * w;
				y = h - ((ev.speed - minspeed) / (maxspeed - minspeed)) * h - 2;
				if (old_x != -1 && old_y != -1) {
					tmpline.setLine(old_x, old_y, x, y);
					g.draw(tmpline);
				}
				old_x = x;
				old_y = y;
			}
			
			// smoothed speed
			g.setColor(new Color(0, 0, 1f, 0.6f));
			y = -1; old_y = -1; old_x = -1;
			x = 0;
			for (RotationEvent ev : kinematics.events_smoothed) {
				x = ev.time / maxlength * w;
				y = h - ((ev.speed - minspeed) / (maxspeed - minspeed)) * h - 2;
				if (old_x != -1 && old_y != -1) {
					tmpline.setLine(old_x, old_y, x, y);
					g.draw(tmpline);
				}
				old_x = x;
				old_y = y;
			}
			
			// acceleration
			g.setColor(new Color(1, 0, 0, 0.2f));
			y = -1;
			old_y = -1;
			old_x = -1;
			x = 0;
			for (RotationEvent ev : kinematics.events_smoothed) {
				x = ev.time / maxlength * w;
				y = h - ((ev.acceleration - minacceleration) / (maxacceleration - minacceleration)) * h - 2;
				if (old_x != -1 && old_y != -1) {
					tmpline.setLine(old_x, old_y, x, y);
					g.draw(tmpline);
				}
				old_x = x;
				old_y = y;
			}
			y = h - ((0 - minacceleration) / (maxacceleration - minacceleration)) * h - 2;
			tmpline.setLine(0, y, w, y);
			g.draw(tmpline);
		}
		
		// cursor
		g.setColor(new Color(0.2f, 0.2f, 0.5f));
		x = time / maxlength * w;
		tmprect.setRect(x - 1, 2, 2, h-4);
		g.fill(tmprect);
	}
	
	public void setKinematics(Kinematics k) {
		this.kinematics = k;
		datalength = k == null ? 0 : k.getCompletionTime(); //getDuration();
		repaint();
	}
	
	public void setTime(double t) {
		this.time = t;
		repaint();
	}
	
    public void addListener(Listener l) {
    	listeners.add(l);
    }
}
