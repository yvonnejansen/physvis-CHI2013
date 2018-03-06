package fr.inria.aviz.physVizEval.jzy3d;

import java.nio.ByteBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.ViewPort;

public class MonoView extends CustomView {

	public MonoView(Scene scene, ICanvas canvas, Quality quality) {
		super(scene, canvas, quality);
	}

    protected void renderScene(GL2 gl, GLU glu, ViewPort viewport) {
    	
    	if (render)
    	{
	    	if (dimensionDirty) {
	        	float newWidth = canvas.getRendererWidth();
	        	float newHeight = canvas.getRendererHeight();
	        	if (newWidth != 0 && newHeight != 0)
	        		((CustomCamera)cam).setAspectRatio(newWidth/newHeight);
	        	else
	        		System.err.println("Cannot get valid dimensions from canvas.");
	
	    	}
	    	
	    	// -- picking code
	    	if (UglyPickingSupport.PICKING_REQUESTED) {
	    		UglyPickingSupport.beginPick(gl);
		    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		    	super.renderScene(gl, glu, viewport);
		    	gl.glPopMatrix();
	    		UglyPickingSupport.endPick(gl);
	    	}
	    	
	    	gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	    	
	    	try {
	    		super.renderScene(gl, glu, viewport);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    	gl.glPopMatrix();
	    	
	    	//doPick(gl, glu);
    	}    	
    }

}
