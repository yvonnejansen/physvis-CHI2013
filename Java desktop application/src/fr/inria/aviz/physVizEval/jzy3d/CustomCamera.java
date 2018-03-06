package fr.inria.aviz.physVizEval.jzy3d;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;

public class CustomCamera extends Camera{

	private float aspectRatio = 1.2f;
	private float orthogonalZoom = 0.85f;
	private float perspectiveZoom = 10f;//3.2f;
	private float orthogonalYOffset = -5;
	private float perspectiveYOffset = -1.5f;
	private Coord3d rotationVector;
	private Coord3d newUp = new Coord3d(0,0,1);
	
	float myNear = -200;
	public CustomCamera(Coord3d target) {
		super(target);
		System.out.println("using custom camera");
//		setRenderingSphereRadius((float) 1.2);
		far = 1500f;
	}
	
	public void setRotationVector(Coord3d v)
	{
		rotationVector = v.clone();
	}

	public void doShoot(GL2 gl, GLU glu, CameraMode projection)
	{
		
//		eye.normalizeTo(280f);
//		eye.z = -100;
//		System.out.println("distance eye to target: " + eye.distance(target) + " " + eye + " near " + myNear + " far " + far + " cameramode " + projection);
//		setTarget(new Coord3d(-3.5, 3.5, 20.8625));
//		setTarget(Coord3d.ORIGIN);
//		setEye(new Coord3d(200,0,0));
		far = 1200f;
		near = -90f;
		myNear +=1f;
		far+=1f;
		perspectiveZoom = 10;
		applyViewPort(gl, glu);
		
//		gl.glMatrixMode(GL2.GL_MODELVIEW);
//		gl.glLoadIdentity();
//		gl.glPushMatrix();
//
//		gl.glRotatef(rotationVector.y, 0, 1, 0);
//		gl.glRotatef(rotationVector.x, 1, 0, 0);
//		gl.glRotatef(rotationVector.z, 0, 0, 1);
//
//		gl.glPopMatrix();
		
		// Set perspective
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		if(projection==CameraMode.PERSPECTIVE){
//			gl.glFrustum(-20*aspectRatio, 20*aspectRatio, -20, 20, 30, far);
			gl.glFrustum(-radius/perspectiveZoom, +radius/perspectiveZoom, -radius/perspectiveZoom + perspectiveYOffset, +radius/perspectiveZoom + perspectiveYOffset, 30, far);
		}
		else if(projection==CameraMode.ORTHOGONAL){
//			gl.glOrtho(-radius*aspectRatio*1.3, +radius*aspectRatio*1.3, -radius*1.3, +radius*1.3, near, far);			
			gl.glOrtho(-radius/orthogonalZoom, +radius/orthogonalZoom, -radius/orthogonalZoom + orthogonalYOffset, +radius/orthogonalZoom + orthogonalYOffset, near, far);			
		}
		else
			throw new RuntimeException("Camera.shoot(): unknown projection mode '" + projection + "'");
		gl.glPushMatrix();

//    	System.out.println("setting rotation vector to : " + rotationVector);


		// Set camera position
		glu.gluLookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);
//		System.out.println(this);
	}

	public void setAspectRatio(float ar)
	{
		aspectRatio = ar;
	}

	public Coord3d getNewUp() {
		return newUp;
	}

	public void setNewUp(Coord3d newUp) {
		this.newUp = newUp.clone();
	}
}
