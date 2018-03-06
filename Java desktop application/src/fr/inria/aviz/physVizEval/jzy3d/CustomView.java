package fr.inria.aviz.physVizEval.jzy3d;

import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import org.jzy3d.chart.ChartView;
import org.jzy3d.colors.Color;
import org.jzy3d.events.ViewPointChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.picking.PickingSupport;
import org.jzy3d.plot3d.primitives.Parallelepiped;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.ViewPort;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Scale;
import org.jzy3d.plot3d.transform.Transform;
import org.jzy3d.plot3d.transform.Translate;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.util.MatrixUtils;

import com.jogamp.newt.event.KeyEvent;



import fr.inria.aviz.physVizEval.barchart3d.BarChart3D;
import fr.inria.aviz.physVizEval.jzy3d.stereo.StereoCamera;
import fr.inria.aviz.physVizEval.util.GUIUtils;
import fr.inria.aviz.physVizEval.util.Logger;
import fr.inria.aviz.physVizEval.util.RotationListener;
import fr.inria.aviz.physVizEval.util.Serial;
import fr.inria.aviz.physVizEval.util.Watcher;

public class CustomView extends ChartView {
	
	public float scaleBaseline = 0;
	public boolean render = true;
    PickingSupport pickingSupport = new CustomPickingSupport(0);
    int mousex, mousey;
    boolean mousePositionChanged = true;
//    Serial serial;
    boolean serialConnected = false;
    private static Coord3d X_AXIS = new Coord3d(1,0,0);
    private static Coord3d Y_AXIS = new Coord3d(0,1,0);
    private static Coord3d Z_AXIS = new Coord3d(0,0,1);
    double roll;
    public Coord3d tmp_up;
    protected Coord3d viewpointRect;
    boolean useViewpointRect = false;
    int safeCounter = 0;
    
    public ModelRotation modelRotation; // = new ModelRotation();
	private RotationListener l;

    public Coord3d getRelativeRotation() {
		return modelRotation.getRelativeRotation(viewpoint);
	}
    
    public double[] getGLRotationMatrix() {
    	return modelRotation.getGLMatrix();
    }
    
    public void setExtrinsincAnglesOffset(Coord3d offset) {
    	modelRotation.setExtrinsicAnglesOffset(offset);
    }

    public void setRotationListener(RotationListener l) {
 		this.l = l;
 	}
    
    public void setModelRotation(ModelRotation m)
    {
    	this.modelRotation = m;
    }

    
    public Scene getScene()
    {
    	return this.scene;
    }
    
    /**
     * Set model rotation according to the ** prop **.
     * @param v
     */
    public void setRotationVector(Coord3d v)
    {
    	modelRotation.setIntrinsicAngles(v);
    	if (l != null)
    		l.rotationEvent(modelRotation.getQuaternion());
//    	if (safeCounter == 500)
//    	{	
//    		((Logger)l).saveLog();
//    		safeCounter = 0;
//    	}
//    	else
//    		safeCounter++;
    	
//    	updateRelativeRotation();
    	    }

    public void dispose()
    {
//    	serial.close();
    	super.dispose();
    }
    
	public PickingSupport getPickingSupport() {
		return pickingSupport;
	}
	
	public void setMousePosition(int x, int y) {
		mousex = x;
		mousey = y;
		mousePositionChanged = true;
	}

	public CustomView(Scene scene, ICanvas canvas, Quality quality) {
		super(scene, canvas, quality);
//		this.viewpoint = new Coord3d(-Math.PI / 4, Math.PI / 4, 2000);
//		this.setViewPoint(new Coord3d(0,Math.PI/4.5,10000));
		this.setViewPoint(new Coord3d(0,0,10000));
		// initial viewpoint
		this.viewmode = ViewPositionMode.FREE;
//		this.serial = Serial.getInstance();
//		this.serial.setView(this);
//		serialConnected = this.serial.initialize();
//		l = new Logger("test", "prop", 1);
	}
	
	public void setSerialConnected(boolean c)
	{
		serialConnected = c;
	}
	
	public boolean getSerialConnected()
	{
		return serialConnected;
	}

    public void setViewPoint(Coord3d polar, double roll) {
    	setViewPoint(polar, roll, false);
    }

		
	public void setViewPoint(Coord3d polar, double roll, boolean updateView) {
		// Snap
//		if (getCameraMode() == CameraMode.ORTHOGONAL) {
//			polar.x = (float)snap(polar.x, 0.03);
//			polar.y = (float)snap(polar.y, 0.04);
//		}
		
        viewpoint = polar;

        viewpoint.y = viewpoint.y < -PI_div2 ? -PI_div2 : viewpoint.y;
        viewpoint.y = viewpoint.y > PI_div2 ? PI_div2 : viewpoint.y;
        
        this.roll = roll;
        
        if(updateView)
        	shoot();
        
        fireViewPointChangedEvent(new ViewPointChangedEvent(this, polar));
    }
	
	public void setViewPointRect(Coord3d rect, double roll) {
        viewpointRect = rect;  
        useViewpointRect = true;
        fireViewPointChangedEvent(new ViewPointChangedEvent(this, rect));
    }

	
	protected static double snap(double angle, double tolerance) {
		double n = angle / (Math.PI / 2);
		int n_i = (int)Math.round(n);
		if (Math.abs(n - n_i) < tolerance)
			return n_i * (Math.PI / 2);
		else
			return angle;
	}
		
	protected void init (GL2 gl)
	{
	       gl.glEnable(GL2.GL_DEPTH_TEST); 
	        gl.glDepthFunc(GL2.GL_LESS);
	        gl.glEnable(GL2.GL_LINE_SMOOTH);
	        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
	        gl.glEnable(GL2.GL_POINT_SMOOTH);
	        gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
	        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
	        gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
	        gl.glShadeModel(GL2.GL_SMOOTH);
	        gl.glEnable(GL2.GL_ALPHA);
	        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	        
			// Turn on alpha blending
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

			this.quality = Quality.Nicest;
			


	}
	
    public void setScale(org.jzy3d.maths.Scale scale, boolean notify)
    {
    	System.out.println("set scale");
    	BoundingBox3d bounds = getBounds();
    	bounds.setZmin(0);
    	bounds.setZmax((float)scale.getMax());
    	setBoundManual(bounds);
    	if (notify)
    		shoot();
    }
        
    Coord3d simulatedIntrinsic = new Coord3d(); // for debug
    
    /**
     * Set model rotation according to the ** mouse **
     */
	public void rotate(final Coord2d move){
		Coord3d eye = getViewPoint();
		
		boolean cameraMode = BarChart3D.ALLOW_SPECIAL_ROTATIONS && GUIUtils.isKeyPressed(KeyEvent.VK_SHIFT);
		boolean tiltMode = BarChart3D.ALLOW_SPECIAL_ROTATIONS && GUIUtils.isKeyPressed(KeyEvent.VK_CONTROL);
		boolean offsetMode = BarChart3D.ALLOW_SPECIAL_ROTATIONS && GUIUtils.isKeyPressed(KeyEvent.VK_ALT);
		boolean rotationMode = !serialConnected && !cameraMode && !tiltMode && !offsetMode;		
		
		if (rotationMode) {
			// NORMAL MODEL ROTATION
			modelRotation.addToExtrinsicAngles(new Coord3d(-move.mul(50).x, 0, -move.mul(50).y));
//			simulatedIntrinsic.addSelf(new Coord3d(-move.mul(50).x, 0, -move.mul(50).y));
//			modelRotation.setIntrinsicAngles(simulatedIntrinsic);
		} else if (tiltMode) {
			// TILT MODE -- FOR DEBUG
			modelRotation.addToExtrinsicAngles(new Coord3d(0, move.mul(50).x, -move.mul(50).y));
		} else if (offsetMode) {
			// OFFSET MODE -- FOR DEBUG
			Coord3d offset = modelRotation.extrinsicAnglesOffset;
			if (offset == null)
				offset = new Coord3d(0, 0, 0);
			modelRotation.setExtrinsicAnglesOffset(new Coord3d(offset.x - move.mul(50).x, 0, offset.z -move.mul(50).y));
		} else if (cameraMode) {
			// CAMERA MODE -- FOR DEBUG
			eye.x -= move.x;
			eye.y += move.y;
			setViewPoint(eye, true);		
//			fireControllerEvent(ControllerType.ROTATE, eye);
		}
		
		if (l != null)
			l.rotationEvent(modelRotation.getQuaternion());
	}


    
    protected void renderScene(GL2 gl, GLU glu, ViewPort viewport) {
    	if(quality.isAlphaActivated())
            gl.glEnable(GL2.GL_BLEND);
    	else
            gl.glDisable(GL2.GL_BLEND);

    	// -- Scale the scene's view -------------------
        if (squared) // force square scale
            scaling = squarify();
        else
            scaling = Coord3d.IDENTITY.clone();  
        
        // -- Compute the bounds for computing cam distance, clipping planes, etc
        if(targetBox==null)
        	targetBox = new BoundingBox3d(0,1,0,1,0,1);
        BoundingBox3d boundsScaled = new BoundingBox3d();
        boundsScaled.add(targetBox.scale(scaling));
        if (MAINTAIN_ALL_OBJECTS_IN_VIEW)
            boundsScaled.add(scene.getGraph().getBounds().scale(scaling));
        float sceneRadiusScaled = (float) boundsScaled.getRadius();

        // -- Camera settings --------------------------
        Coord3d target = center.mul(scaling);

        Coord3d eye;
        viewpoint.z = sceneRadiusScaled * 4; // maintain a reasonnable distance to the scene for viewing it.

        if (viewmode == ViewPositionMode.FREE) {
        	if (!useViewpointRect)
        		eye = viewpoint.cartesian().add(target);
        	else
        		eye = new Coord3d(viewpointRect.x * viewpoint.z, viewpointRect.y * viewpoint.z, viewpointRect.z * viewpoint.z).add(target);
        } else if (viewmode == ViewPositionMode.TOP) {
            eye = viewpoint;
            eye.x = -(float) Math.PI / 2; // on x
            eye.y = (float) Math.PI / 2; // on top
            eye = eye.cartesian().add(target);
        } else if (viewmode == ViewPositionMode.PROFILE) {
            eye = viewpoint;
            eye.y = 0;
            eye = eye.cartesian().add(target);
        } else
            throw new RuntimeException("Unsupported ViewMode: " + viewmode);

//        Coord3d up;
//        if (Math.abs(viewpoint.y) == (float) Math.PI / 2) {
//        	// handle up vector
//            Coord2d direction = new Coord2d(viewpoint.x, viewpoint.z).cartesian(); 
//            if (viewpoint.y > 0) // on top
//                up = new Coord3d(-direction.x, -direction.y, 0);
//            else
//                up = new Coord3d(direction.x, direction.y, 0);
//
//            // handle "on-top" events
//            if (!wasOnTopAtLastRendering) {
//                wasOnTopAtLastRendering = true;
//                fireViewOnTopEvent(true);
//            }
//        } else {
            // handle up vector
//      Coord3d direction = new Coord2d(viewpoint.y, viewpoint.z).cartesian(); 

//        Coord3d up = viewpoint.cartesian();//
//        up.x = 0
//        up = new Coord3d(Math.PI,viewpoint.z,Math.PI);

            // handle "on-top" events
            if (wasOnTopAtLastRendering) {
                wasOnTopAtLastRendering = false;
                fireViewOnTopEvent(false);
//            }
        }

            
        // Compute up vector
//        Coord3d lookat = new Coord3d(target.x - eye.x, target.y - eye.y, target.z - eye.z);
        Coord3d up = new Coord3d(0,0,1);
                        
        // -- Apply camera settings ------------------------
        cam.setTarget(target);
        //cam.setUp(new Coord3d(0, 0, 1));
        cam.setUp(up);
        cam.setEye(eye);

        // Set rendering volume
        if (viewmode == ViewPositionMode.TOP) {
            cam.setRenderingSphereRadius(Math.max(boundsScaled.getXmax() - boundsScaled.getXmin(), boundsScaled.getYmax() - boundsScaled.getYmin()) / 2);
            correctCameraPositionForIncludingTextLabels(gl, glu, viewport); // quite experimental!
        } else{
            cam.setRenderingSphereRadius(sceneRadiusScaled);
        }

        // Setup camera (i.e. projection matrix)
        //cam.setViewPort(canvas.getRendererWidth(), canvas.getRendererHeight(), left, right);
        cam.setViewPort(viewport);
        		
        cam.shoot(gl, glu, cameraMode);
        
        // -- Render elements -----------------
        if (axeBoxDisplayed) {
        	        
            gl.glMatrixMode(GL2.GL_PROJECTION_MATRIX);            
      	  	gl.glPushMatrix();
  	//    	gl.glLoadIdentity();
      	  	
  	    	gl.glTranslatef(target.x, target.y, target.z);
 			
  	    	// -- Method 1: apply the three rotations in sequence
  	    	//    This has been tested with the mouse and works.
//      	    Coord3d rot = modelRotation.getIntrinsicAngles();      	    	
//   			gl.glRotatef(-rot.x, 0, 0, 1); // Add minus signs because OpenGL performs counterclockwise rotations
//   			gl.glRotatef(-rot.y, 1, 0, 0);
//   			gl.glRotatef(-rot.z, 0, 1, 0);

   			// -- The following is for testing conversion to extrinsic angles
//      	    modelRotation.setIntrinsicAngles(modelRotation.getIntrinsicAngles());
//   			rot = modelRotation.getExtrinsicAngles();
//   			gl.glRotatef(-rot.z, 0, 1, 0);
//   			gl.glRotatef(-rot.y, 1, 0, 0);
//   			gl.glRotatef(-rot.x, 0, 0, 1); // Add minus signs because OpenGL performs counterclockwise rotations		
  	    	
			// -- Method 2: apply the whole rotation matrix
  	    	//    This has been tested with the mouse and works.
  	    	double[] mat = modelRotation.getGLMatrix();
        	gl.glMultMatrixd(mat, 0);
			            	
  	    	gl.glTranslatef(-target.x, -target.y, -target.z);

  	    	
            gl.glMatrixMode(GL2.GL_MODELVIEW);            
            scene.getLightSet().disable(gl);
            
        	axe.setScale(scaling);
            axe.draw(gl, glu, cam);
            if (DISPLAY_AXE_WHOLE_BOUNDS) { // for debug
            	AxeBox abox = (AxeBox)axe;
                BoundingBox3d box = abox.getWholeBounds();
                Parallelepiped p = new Parallelepiped(box);
                p.setFaceDisplayed(false);
                p.setWireframeColor(Color.MAGENTA);
                p.setWireframeDisplayed(true);
                p.draw(gl, glu, cam);
            }
            
            scene.getLightSet().enableLightIfThereAreLights(gl);
        }
        


        Transform transform = new Transform(new Scale(scaling));

        scene.getLightSet().apply(gl, scaling);
        //gl.glEnable(GL2.GL_LIGHTING);
        //gl.glEnable(GL2.GL_LIGHT0);
        //gl.glDisable(GL2.GL_LIGHTING);
        scene.getGraph().setTransform(transform);
//      transform.add(new Translate(target));
//      transform.add(new Rotate(-rotationVector.x, Z_AXIS));
//  	transform.add(new Rotate(-rotationVector.y, X_AXIS));
//  	transform.add(new Rotate(-rotationVector.z, Y_AXIS));
//  	transform.add(new Translate(target.negative()));
////  	scene.getGraph().getStrategy().setTransform(transform);
        scene.getGraph().draw(gl, glu, cam);
    }

}
