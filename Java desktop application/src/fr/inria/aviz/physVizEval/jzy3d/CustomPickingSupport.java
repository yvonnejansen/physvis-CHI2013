package fr.inria.aviz.physVizEval.jzy3d;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.picking.IObjectPickedListener;
import org.jzy3d.picking.IPickable;
import org.jzy3d.picking.PickingSupport;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Transform;

import com.jogamp.common.nio.Buffers;

/**
 * @see: http://www.opengl.org/resources/faq/technical/selection.htm
 * 
 * @author Martin Pernollet
 *
 */
public class CustomPickingSupport extends PickingSupport {
	
	int selectBuf[];
	
	public CustomPickingSupport(){
		this(10);
	}
	
	public CustomPickingSupport(int brushSize){
		this(brushSize, 2048);
	}
	
	public CustomPickingSupport(int brushSize, int bufferSize){
		this.brushSize = brushSize;
		this.bufferSize = bufferSize;
        selectBuf = new int[bufferSize]; // TODO: move @ construction
	}

	// FIXME: Optimize memory usage
	public void pickObjects(GL2 gl, GLU glu, View view, Graph graph, IntegerCoord2d pickPoint) {
        int viewport[] = new int[4];
        IntBuffer selectBuffer = Buffers.newDirectIntBuffer(bufferSize);
        
        // Prepare selection data
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);        
        gl.glSelectBuffer(bufferSize, selectBuffer);        
        gl.glRenderMode(GL2.GL_SELECT);         
        gl.glInitNames();
        gl.glPushName(0); 

        // Retrieve view settings
        Camera camera = view.getCamera();
        CameraMode cMode = view.getCameraMode();
        Coord3d viewScaling = view.getLastViewScaling();
        Transform viewTransform = new Transform(new Scale(viewScaling));
        double xpick = (double) pickPoint.x;
        double ypick = (double) pickPoint.y;
        
        // Setup projection matrix
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        {
	        gl.glLoadIdentity();
	        // Setup picking matrix, and update view frustrum
	        glu.gluPickMatrix(xpick, ypick, brushSize, brushSize, viewport, 0);
	        camera.doShoot(gl, glu, cMode);
	        
	        // Draw each pickable element in select buffer
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        for(IPickable pickable: pickables.values()){
	        	setCurrentName(gl, pickable);
	        	pickable.setTransform(viewTransform);
	        	pickable.draw(gl, glu, camera);
	        	releaseCurrentName(gl);
	        }
	        // Back to projection matrix
	        gl.glMatrixMode(GL2.GL_PROJECTION);
        }
        gl.glPopMatrix();
        gl.glFlush();
        
        // Process hits
        int hits = gl.glRenderMode(GL2.GL_RENDER);
        selectBuffer.get(selectBuf);
        List<IPickable> picked = processHits(hits, selectBuf);
        
        // Trigger an event
        List<Object> clickedObjects = new ArrayList<Object>(hits);
        for(IPickable pickable: picked){
        	Object vertex = pickableTargets.get(pickable);
        	clickedObjects.add(vertex);
        }
        fireObjectPicked(clickedObjects);
    }
 }
