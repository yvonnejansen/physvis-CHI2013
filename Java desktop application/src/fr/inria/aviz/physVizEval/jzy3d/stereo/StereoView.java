package fr.inria.aviz.physVizEval.jzy3d.stereo;

import java.nio.IntBuffer;

import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;


import org.jzy3d.chart.ChartView;
import org.jzy3d.colors.Color;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Disk;
import org.jzy3d.plot3d.primitives.Parallelepiped;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.Legend;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.ViewPort;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transform;

import fr.inria.aviz.physVizEval.jzy3d.CustomView;
import fr.inria.aviz.physVizEval.jzy3d.UglyPickingSupport;


/**
 * Picked ideas from http://paulbourke.net/texture_colour/anaglyph/
 * 
 * @see GL method docs: http://glprogramming.com/blue/ch05.html
 * @author Martin
 */
public class StereoView extends CustomView {
	
	boolean downSamplingEnabled = false;
	DownSampler downSampler;
	// We use this global variable since we cannot pass the information through the GL context any more.
	public static int currentEye = 0; 

    public StereoView(Scene scene, ICanvas canvas, Quality quality) {
        super(scene, canvas, quality);
    }

    protected void renderScene(GL2 gl, GLU glu, ViewPort viewport) {
     	if (render)
     	{
	    	if (dimensionDirty) {
	        	float newWidth = canvas.getRendererWidth();
	        	float newHeight = canvas.getRendererHeight();
	        	if (newWidth != 0 && newHeight != 0)
	        		((StereoCamera)cam).setAspectRatio(newWidth/newHeight);
	        	else
	        		System.err.println("Cannot get valid dimensions from canvas.");
	        	
	        	if (downSamplingEnabled) {
	        		if (downSampler == null)
	        			downSampler = new DownSampler(1, 0.5);
	        		downSampler.resize(gl, glu, viewport, newWidth, newHeight);
	        	}
	    	}
	     	
	     	if (downSamplingEnabled) {
	     		
		    	downSampler.preRender(gl, glu, viewport);
		    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	currentEye = GL2.GL_BACK_LEFT; 	// We use this global variable since we cannot pass the information through the GL context any more.
		    	super.renderScene(gl, glu, downSampler.getScaledViewport(viewport));
		       	downSampler.postRender(gl, glu, viewport, currentEye);
		    	gl.glPopMatrix();
		
		    	downSampler.preRender(gl, glu, viewport);
		    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	currentEye = GL2.GL_BACK_RIGHT;	// We use this global variable since we cannot pass the information through the GL context any more.
		    	super.renderScene(gl, glu, downSampler.getScaledViewport(viewport));
		       	downSampler.postRender(gl, glu, viewport, currentEye);
		    	gl.glPopMatrix();
		    	
	     	} else {
	     		
		    	// -- picking code
		    	// The majority of people are right eye dominant, so use the right image for ray casting.
		    	if (UglyPickingSupport.PICKING_REQUESTED) {
		    		UglyPickingSupport.beginPick(gl);
			    	currentEye = GL2.GL_BACK;
			    	gl.glDrawBuffer(currentEye);
			    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
			    	super.renderScene(gl, glu, viewport);
			    	gl.glPopMatrix();
		    		UglyPickingSupport.endPick(gl);
		    	}
		    	
		    	currentEye = GL2.GL_BACK_LEFT;
		    	gl.glDrawBuffer(currentEye);
		    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	
		    	try {
		    		super.renderScene(gl, glu, viewport);
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    	gl.glPopMatrix();
		    	
		    	currentEye = GL2.GL_BACK_RIGHT;
		    	gl.glDrawBuffer(currentEye);
		    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	super.renderScene(gl, glu, viewport);
		    	gl.glPopMatrix();		    	
	    	}
	     	
	    	//doPick(gl, glu);
     	}

    }

    
    /**
     * Print current drawing buffer in console.
     */
    protected void printCurrentDrawingBuffer(GL2 gl){
        int b = getCurrentDrawingBuffer(gl);
        
        switch(b){
            case GL2.GL_FRONT_AND_BACK:System.out.println("GL_FRONT_AND_BACK");break;
            
            case GL2.GL_FRONT:System.out.println("GL_FRONT (default for single buffering)");break;
            case GL2.GL_FRONT_LEFT:System.out.println("GL_FRONT_LEFT");break;
            case GL2.GL_FRONT_RIGHT:System.out.println("GL_FRONT_RIGHT");break;

            case GL2.GL_BACK:System.out.println("GL_BACK (default for double buffering)");break;
            case GL2.GL_BACK_LEFT:System.out.println("GL_BACK_LEFT");break;
            case GL2.GL_BACK_RIGHT:System.out.println("GL_BACK_RIGHT");break;
            
            case GL2.GL_LEFT:System.out.println("GL_LEFT");break;
            case GL2.GL_RIGHT:System.out.println("GL_RIGHT");break;
            
            case GL2.GL_NONE:System.out.println("GL_NONE");break;
            
            default:System.out.println("Another Buffer");break;
        }
    }
    
     
    /**
     * Return current drawing buffer.
     * @see http://glprogramming.com/blue/ch05.html#id18132
     */
    protected int getCurrentDrawingBuffer(GL2 gl){
        IntBuffer ib = IntBuffer.allocate(1);
        gl.glGetIntegerv(GL2.GL_DRAW_BUFFER, ib);
        return ib.get(0);
    }
    
    @Override
    public void setCameraMode(CameraMode mode) {
    	// ignore
    	super.setCameraMode(CameraMode.PERSPECTIVE);
    }
    
    
}
