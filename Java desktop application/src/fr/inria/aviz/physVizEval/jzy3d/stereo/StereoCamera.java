package fr.inria.aviz.physVizEval.jzy3d.stereo;

import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;


public class StereoCamera extends Camera{
	 
	protected double fov = 45; //83;//136;                                          //field of view in y-axis
	protected double frustFar = 1200;
	protected float frustNear = -39;//350;
	protected double focalLength = 168;//70;
	protected double IOD = 10;                                          //intraocular distance
	private Coord3d centerEye;
	private float camDistanceFactor = 1;
	private float modelDistance = 175;//477;
	private float parallaxPlane = -164;//-100;
	private float aspectRatio = 1.2f;
	private float orthogonalZoom = 0.85f;
	private float perspectiveZoom = 75f;
	private float orthogonalYOffset = -1f;
	private float perspectiveYOffset = -5f;
	
	float counter = 0;
	
    public StereoCamera(Coord3d target) {
        super(target);
//        eye = new Coord3d(-39,-53,-22);
//        target = new Coord3d(-eye.x, -eye.y, -eye.z);
        near = (float) (focalLength / 50);
        far = 70;
        radius = 200;
        centerEye= eye.clone();
        
 

    }
    
//    private Coord3d crossproduct (Coord3d v1, Coord3d v2)
//    {
//    	return new Coord3d(v1.y * v2.z - v1.z * v2.y, 
//    						v1.z * v2.x - v1.x * v2.z,
//    						v1.x * v2.y - v1.y * v2.x);
//    }
    
    private void stereoProjection(GL2 gl, float xmin, float xmax, float ymin, float ymax, float znear, float zfar, float zzps, float dist, float iod, boolean ortho) {
    	float xmid, ymid, clip_near, clip_far, t, b, l, r, dx, dy, n_over_d;
    	
    	dx = xmax - xmin;
    	dy = ymax - ymin;
    	
    	xmid = (xmax + xmin) / 2f;
    	ymid = (ymax + ymin) / 2f;
    	
    	clip_near = dist + zzps - znear;
    	clip_far = dist + zzps - zfar;
    	
    	n_over_d = clip_near / dist;
    	
    	t = n_over_d * dy / 2f;
    	b = -t;
    	r = n_over_d * (dx / 2f - iod);
    	l = n_over_d * (-dx / 2f - iod);
    	
    	gl.glMatrixMode(GL2.GL_PROJECTION);
    	gl.glLoadIdentity();
    	
		gl.glFrustum(l, r, b, t, clip_near, clip_far);
		
		if (ortho)
//			gl.glOrtho(-radius*aspectRatio, +radius*aspectRatio, -radius, +radius, near, far);
			gl.glOrtho(-radius/orthogonalZoom, +radius/orthogonalZoom, -radius/orthogonalZoom + orthogonalYOffset, +radius/orthogonalZoom + orthogonalYOffset, near, far);			
    	gl.glTranslatef(-xmid - iod, -ymid, -zzps - dist);
    	
    }
    
    public void doShoot(GL2 gl, GLU glu, CameraMode projection){
    	
    	if (projection == CameraMode.ORTHOGONAL) {
			setNear(500);
			setFocalLength(55);
			setFOV(300);
			setModelDistance(250);
			setParallaxPlane(-187);
			IOD = 0;
    	}
    	else {
			setNear(-200); //-39);
			setFocalLength(68); //168);
			setFOV(83);
			setModelDistance(150); //175);
			setParallaxPlane(-300); //-110);
			IOD = (float) (focalLength / 30);
    	}
    	
//    	eye.normalizeTo((float) focalLength);//(camDistanceFactor);
//    	setTarget(Coord3d.ORIGIN);
//		setTarget(new Coord3d(31.5, 31.5, 13.414));
    	applyViewPort(gl, glu);
        

    	IntBuffer ib = IntBuffer.allocate(1);
//    	gl.glGetIntegerv(GL2.GL_DRAW_BUFFER, ib);
    	// We use this global variable since we cannot pass the information through the GL context any more.
    	ib.put(StereoView.currentEye); // FIXME
    	
    							// xmin						xmax					ymin				ymax		znear			zfar		z of 0-parallax plane	dist 0-par to model			inter-ocular distance
//    	stereoProjection(gl, (float)(-6f*aspectRatio*fov/5), (float)(6f*aspectRatio*fov/5), (float)(-5f*fov/5), (float)(5f*fov/5), frustNear, -(float)frustFar, (float) parallaxPlane,  modelDistance, ib.get(0) == GL2.GL_BACK_LEFT ? -(float)(IOD/2) : (float)(IOD/2), projection == CameraMode.ORTHOGONAL);
    	    	
    	stereoProjection(gl, (float)(-radius/perspectiveZoom*fov), (float)(radius/perspectiveZoom*fov), (float)(-radius/perspectiveZoom*fov) + perspectiveYOffset, (float)(radius/perspectiveZoom*fov) + perspectiveYOffset, frustNear, -(float)frustFar, (float) parallaxPlane,  modelDistance, ib.get(0) == GL2.GL_BACK_LEFT ? -(float)(IOD/2) : (float)(IOD/2), projection == CameraMode.ORTHOGONAL);

        gl.glPushMatrix();
//        glu.gluLookAt(1, 1, 1, 0, 0, 0, 0, 0, 1);
            	glu.gluLookAt(eye.x, eye.y, eye.z, target.x, target.y, target.z, up.x, up.y, up.z);
//            	System.out.println("eye: " + eye + " target: " + target);

    }
    
	public void setFOV (double _fov)
	{
		fov = _fov;
	}
	
	
	public double getFOV ()
	{
		return fov;
	}
	
	public void setFocalLength (double _focalLength)
	{
		focalLength = _focalLength;
	}
	
	public double getFocalLength ()
	{
		return focalLength;
	}
	
	public void setIOD (double _iod)
	{
		IOD = _iod;
	}
	
	public double getIOD ()
	{
		return IOD;
	}

    public void setFar(float _far)
    {
    	this.frustFar = _far;
    }

    public void setNear(float _near)
    {
    	this.frustNear = _near;
    }

    public void setCameraDistance(float _dist)
    {
    	camDistanceFactor = _dist;
    }
    
    public void setModelDistance(float _dist)
    {
    	modelDistance = _dist;
    }
    
    
    public void setParallaxPlane(float _par) 
    {
    	parallaxPlane = _par;
    }
    
    public void setAspectRatio(float r) 
    {
    	aspectRatio = r;
    }
}
