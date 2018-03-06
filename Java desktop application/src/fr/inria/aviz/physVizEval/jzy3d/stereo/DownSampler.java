package fr.inria.aviz.physVizEval.jzy3d.stereo;

import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.ViewPort;

/**
 * 
 * @author dragice
 *
 */
public class DownSampler {

	FrameBufferObject downsamplingFBO = null;
	
	double scalex, scaley;

	public DownSampler(double scalex, double scaley) {
		this.scalex = scalex;
		this.scaley = scaley;
	}
	
	public ViewPort getScaledViewport(ViewPort viewport) {
    	return new ViewPort((int)(viewport.getWidth() * scalex), (int)(viewport.getHeight() * scaley), (int)(viewport.getX() * scaley), (int)(viewport.getY() * scaley));
	}
	
	public void resize(GL2 gl, GLU glu, ViewPort viewport, float newWidth, float newHeight) {
		
       	if (viewport.getHeight() > 0) {
    		if (downsamplingFBO != null)
    			downsamplingFBO.delete(gl);
    		ViewPort scaledViewport = getScaledViewport(viewport);
    		downsamplingFBO = FrameBufferObject.create(gl, scaledViewport.getWidth(), scaledViewport.getHeight(), true);
    	}
	}
	
    public void preRender(GL2 gl, GLU glu, ViewPort viewport) {
    	// 1 - Draw the scene into the framebuffer using a downscaled viewport
		downsamplingFBO.bind(gl);
    	gl.glClearColor(1f, 1f, 1f, 0f);
    	downsamplingFBO.clear(gl);
    }
    
    public void postRender(GL2 gl, GLU glu, ViewPort viewport, int buffer) {   	
    	// 2 - Draw the framebuffer back to the screen by scaling it up
    	gl.glBindFramebuffer(GL2.GL_DRAW_FRAMEBUFFER, 0);
    	if (buffer > 0)
    		gl.glDrawBuffer(buffer);
    	gl.glBlitFramebuffer(0, 0, downsamplingFBO.width, downsamplingFBO.height, 0, 0, viewport.getWidth(), viewport.getHeight(), GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT, GL2.GL_NEAREST);
    }

    
}
