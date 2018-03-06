/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.inria.aviz.physVizEval.jzy3d;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.ChartMouseController;
import org.jzy3d.chart.controllers.mouse.MouseUtilities;
import org.jzy3d.chart.controllers.thread.ChartThreadController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.maths.Scale;
import org.jzy3d.picking.PickingSupport;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;

import fr.inria.aviz.physVizEval.barchart3d.BarChart3D;
import fr.inria.aviz.physVizEval.util.GUIUtils;

/**
 *
 * @author ao
 */
public class CustomMouseControl extends ChartMouseController {

    private final Chart chart;
    //private PickingSupport pickingSupport;
    
    public static boolean ROTATE_ON_MOUSE_MOVE = true;
    private Cursor noCursor = GUIUtils.NO_CURSOR;
    boolean enabled = true;

    public CustomMouseControl(Chart chart) {
        this.chart = chart;
       // this.pickingSupport = ((CustomView)chart.getView()).getPickingSupport();
        if (ROTATE_ON_MOUSE_MOVE) {
        	((Component)chart.getCanvas()).setCursor(noCursor);
        }
    }

    int wheelAngle = 0;
    
	public void mouseMoved(MouseEvent e) {
   	
		//((CustomView)chart.getView()).setMousePosition(e.getX(), e.getY());
		if (!ROTATE_ON_MOUSE_MOVE)
			UglyPickingSupport.setMousePosition(e.getX(), chart.getCanvas().getRendererHeight() - e.getY());
		else {
	        Coord2d mouse = new Coord2d(e.getX(), e.getY());
	        // Rotate
	        
	        if (prevMouse != null) {
		        Coord2d move = mouse.sub(prevMouse).div(100);
		    	if (enabled)
		    		rotate(move);
			    prevMouse = mouse;
				((CustomView)chart.getView()).setMousePosition(e.getX(), e.getY());
	        } else {
	        	prevMouse = new Coord2d(mouse.x, mouse.y);
	        }
		}
	}
	
	public void mouseClicked(MouseEvent e) {
    	if (!enabled) return;
    	
		if (!ROTATE_ON_MOUSE_MOVE)
			UglyPickingSupport.PICKING_REQUESTED = true;
	}
    
	/** Handles toggle between mouse rotation/auto rotation: double-click starts the animated
	 * rotation, while simple click stops it.*/
    @Override
	public void mousePressed(MouseEvent e) {
    	if (!enabled) return;
    	
    	prevMouse.x  = e.getX();
		prevMouse.y  = e.getY();
		
	}
	
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    	if (!enabled) return;
    	
        if (threadController != null) {
            threadController.stop();
        }
    
        wheelAngle += e.getWheelRotation();
        boolean persp = (wheelAngle % 2) == 0;
//        if (persp)
//        	chart.getView().setCameraMode(CameraMode.PERSPECTIVE);
//        else
//        	chart.getView().setCameraMode(CameraMode.ORTHOGONAL);
//        float factor = 1 + (e.getWheelRotation() / 20.0f);
//        zoomAll(factor);
    }

    public void mouseDragged(MouseEvent e) {
    	if (!enabled) return;
    	
    	if (!ROTATE_ON_MOUSE_MOVE) {
	        Coord2d mouse = new Coord2d(e.getX(), e.getY());
	        // Rotate
	        if (MouseUtilities.isLeftDown(e)) {
	            Coord2d move = mouse.sub(prevMouse).div(150);
	            rotate(move);
	        }
	        prevMouse = mouse;
			((CustomView)chart.getView()).setMousePosition(e.getX(), e.getY());
    	}
    }

    protected void zoomAll(final float factor) {
        for (Chart c : targets) {
//                    c.getView().zoom(factor);
            BoundingBox3d bb = c.getView().getBounds();
            c.getView().setBoundManual(new BoundingBox3d(bb.getXmin(), bb.getXmax() * factor,
                    bb.getYmin(), bb.getYmax() * factor,
                    bb.getZmin(), bb.getZmax() * factor));
            c.getView().updateBounds();
//                    c.getView().shoot();
        }
//		fireControllerEvent(ControllerType.ZOOM, factor);
    }

    public void install() {
        ChartThreadController threadCamera = new ChartThreadController(chart);
        this.addSlaveThreadController(threadCamera);
        chart.addController(this);
    }
    
    public void recenterMouse() {
    	//System.err.println("--- Inside recenterMouse");
    	if (ROTATE_ON_MOUSE_MOVE) {
	    	Robot robot;
	    	try {
	    		robot = new Robot();
	    	} catch (AWTException e) {
	    		e.printStackTrace();
	    		return;
	    	}
//			Component c = ((Component)chart.getCanvas());
//			if (c.getWidth() == 0)
//				return;
			Point pos = new Point(0, 0);
//			SwingUtilities.convertPointToScreen(new Point(0, 0), c);
			enabled = false;
	    	Dimension c = Toolkit.getDefaultToolkit().getScreenSize();
//			System.err.println("    RECENTER -> " + (pos.x + c.getWidth() / 2) + " " + (pos.y + c.getHeight() / 2));
			robot.mouseMove(pos.x + (int)c.getWidth() / 2, pos.y + (int)c.getHeight() / 2);
			prevMouse = null;
			enabled = true;
    	}
    }
}
