package fr.inria.aviz.physVizEval.jzy3d;

import org.jzy3d.maths.Coord3d;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.util.MatrixUtils;
import org.openmali.vecmath2.util.MatrixUtils.EulerOrder;

import fr.inria.aviz.physVizEval.util.OneEuroFilter;
import fr.inria.aviz.physVizEval.util.Watcher;

/**
 * This class manages four different representations for an object rotation in 3D:
 * 
 * - Quaternions
 * - Intrinsic XYZ Tait-Bryan angles
 * - Extrinsic XYZ Tait-Bryan angles
 * - OpenGL rotation matrices
 * 
 * Quaternions are used as the internal pivotal representation.
 * 
 * Tait-Bryan angles are commonly called Euler angles, but proper Euler angles use a duplicate axis (for
 * example ZYZ rotates about Z, then the transformed Y, then the transformed Z again). So anything like
 * XYZ where there is not duplicate axis are actually Tait-Bryan angles. But the two are very similar.
 * 
 * Intrinsic or "moving axes" XYZ angles means we rotate around X, then the transformed Y, then the
 * transformed Z. Example: an airplane, where we mostly care about what happens with respect to the pilot.
 * Extrinsic or "fixed axes" XYZ angles means we rotate around X, then around the world (untransformed)
 * Y, then around the world (untransformed) Z. Used for mouse manipulations, where we only care about
 * what happens with respect to a fixed, external frame of reference (the screen or eye).
 * There is a relationship between applying rotations in the reverse order (ZYX) and switching between
 * intrinsic and extrinsic but I'm not sure I fully understand that. Works in OpenGL. 
 * 
 * Meaning of coordinates passed to coord3d:
 * X = yaw   = psi   = rotation about z axis
 * Y = pitch = theta = rotation about x axis
 * Z = roll  = phi   = rotation about y axis
 * 
 * All angles are in degrees.
 * 
 * For now this class supports:
 * - Writing intrinsic and extrinsic XYZ Tait-Bryan angles
 * - Setting an offset for extrinsic angles
 * - Reading intrinsic and extrinsic XYZ Tait-Bryan angles
 * - Reading quaternion
 * - Reading OpenGL matrix and inverse matrix, to be used with glMultMatrixd
 * 
 * FIXME:
 * - Find algorithm for conversion to extrinsic coordinates, to be used for label placement.
 * - Figure out what's the deal with angle signs and left-handed versus right-handed coordinate systems.
 * 
 * @author dragice
 *
 */
public class ModelRotation {

	public static boolean DEBUG_ANGLES = false; 
	
	// This class uses a quaternion as internal representation, and keeps it always up-to-date.
    private Quaternion4f quaternion = new Quaternion4f();

    // Intrinsic XYZ angles
    private Coord3d intrinsicAngles = new Coord3d(0,0,0);
    private boolean intrinsicAnglesUptodate = false;
    
    // Extrinsic XYZ angles
    Coord3d extrinsicAngles = new Coord3d(0,0,0);
    private boolean extrinsicAnglesUptodate = false;
    
    // GL Matrix
    double[] glMatrix = null;
    private boolean glMatrixUptodate = false;

    // GL Inverse matrix
    double[] glInverseMatrix = null;
    private boolean glInverseMatrixUptodate = false;

    // Extrinsic angle offset
    Coord3d extrinsicAnglesOffset = null;
        
    // Global internal variables to speed up conversions
    Matrix4f tmpMatrix = new Matrix4f();
    Tuple3f tmpTuple = new Tuple3f();
    Coord3d tmpCoord = new Coord3d();
    Quaternion4f tmpQuaternion = new Quaternion4f();
    
    // Filtering
    // Turn the filtering OFF here, since it's already done by Serial.
    boolean filterEnabled = false; // keep to false!
    static class MyFilter extends OneEuroFilter {
    	public MyFilter() {
    		super(
    			60, // default input frequency (not used)
    			0.1, // min freq cutoff (Hz) -> the lower the more filtering at low speeds
    			0, // beta -> the higer the less lag at high speeds
    			1 // cutoff for computing speed (they say 1 Hz = good default)
    		);
    	}
    }
    OneEuroFilter filter_a = new MyFilter();
    OneEuroFilter filter_b = new MyFilter();
    OneEuroFilter filter_c = new MyFilter();
    OneEuroFilter filter_d = new MyFilter();
	double timestamp0 = System.currentTimeMillis() / 1000.0;
    
    public ModelRotation() {
    	if (DEBUG_ANGLES) {
    		double d = 1.5;
    		double[] rightAngles = new double[] {-d, d, 90-d, 90+d, 180-d, 180-d, 270-d, 270+d, 360-d, 360+d, -90-d, -90+d, -180-d, -180+d, -270-d, -270+d, -360-d, -360+d};
//    		Watcher.watch("intrinsic", "x", "y", "z");
    		Watcher.watch("extrinsic", "x", "y", "z");
    		Watcher.setSpecialValues("extrinsic", rightAngles);
    		Watcher.watch("offset", "x", "y", "z");
    		Watcher.watch("relative", "rotx", "roty", "tilt");
    		Watcher.watch("quaternion", "a", "b", "c", "d");
    		
    		d = 0.01;
    		double[] rightQuat = new double[] {-d, d};
    		Watcher.setSpecialValues("quaternion", rightQuat);
    	}
    }
    
    /**
     * Sets model orientation using intrinsic (moving axes) XYZ Tait-Bryan angles.
     */
	public synchronized void setIntrinsicAngles(Coord3d angles) {
		this.intrinsicAngles.set(angles);
		convertIntrinsicAnglesToQuaternion();
		glMatrixUptodate = false;
		glInverseMatrixUptodate = false;
		if (extrinsicAnglesOffset == null) {
			intrinsicAnglesUptodate = true;
			extrinsicAnglesUptodate = false;
		} else {
			convertQuaternionToExtrinsicAngles();
			extrinsicAngles.addSelf(extrinsicAnglesOffset);
			convertExtrinsicAnglesToQuaternion();
			extrinsicAnglesUptodate = true;
			intrinsicAnglesUptodate = false;
		}
	}

    /**
     * Increments model orientation using intrinsic (moving axes) XYZ Tait-Bryan angles.
     */
//	public void addToIntrinsicAngles(Coord3d angles) {
//		if (!intrinsicAnglesUptodate)
//			convertQuaternionToIntrinsicAngles();
//		setIntrinsicAngles(intrinsicAngles.add(angles));
//	}

    /**
     * Sets model orientation using extrinsic (fixed axes) XYZ Tait-Bryan angles.
     */
	public void setExtrinsicAngles(Coord3d angles) {
		this.extrinsicAngles.set(angles);
		convertExtrinsicAnglesToQuaternion();
		glMatrixUptodate = false;
		glInverseMatrixUptodate = false;
		if (extrinsicAnglesOffset == null) {
			extrinsicAnglesUptodate = true;
			intrinsicAnglesUptodate = false;
		} else {
			extrinsicAngles.addSelf(extrinsicAnglesOffset);
			convertExtrinsicAnglesToQuaternion();
			extrinsicAnglesUptodate = true;
			intrinsicAnglesUptodate = false;
		}
	}

    /**
     * Increments model orientation using extrinsic (fixed axes) XYZ Tait-Bryan angles.
     */
	public void addToExtrinsicAngles(Coord3d angles) {
		if (!extrinsicAnglesUptodate)
			convertQuaternionToExtrinsicAngles();
		this.extrinsicAngles.addSelf(angles);
		convertExtrinsicAnglesToQuaternion();
		extrinsicAnglesUptodate = true;
		intrinsicAnglesUptodate = false;
		glMatrixUptodate = false;
		glInverseMatrixUptodate = false;
	}
	
	/**
	 * FIXME: This is somehow buggy. For best results, first set the offset to zero, then position the
	 * model, then call this method again with the offset value.
	 */
	public void setExtrinsicAnglesOffset(Coord3d offset) {
		if (!extrinsicAnglesUptodate)
			convertQuaternionToExtrinsicAngles();
		if (extrinsicAnglesOffset != null)
			extrinsicAngles.addSelf(offset.sub(extrinsicAnglesOffset));
		else
			extrinsicAngles.addSelf(offset);
		extrinsicAnglesOffset = offset.clone();
		convertExtrinsicAnglesToQuaternion();
		extrinsicAnglesUptodate = true;
		intrinsicAnglesUptodate = false;
		glMatrixUptodate = false;
		glInverseMatrixUptodate = false;
	}
	
    /**
     * Returns model orientation using intrinsic (moving axes) XYZ Tait-Bryan angles.
     */
	public Coord3d getIntrinsicAngles() {
		if (!intrinsicAnglesUptodate)
			convertQuaternionToIntrinsicAngles();
		intrinsicAnglesUptodate = true;
		return intrinsicAngles.clone();
	}

    /**
     * Returns model orientation using extrinsinc (fixed axes) XYZ Tait-Bryan angles.
     */
	public Coord3d getExtrinsicAngles() {
		if (!extrinsicAnglesUptodate)
			convertQuaternionToExtrinsicAngles();
		extrinsicAnglesUptodate = true;
		return extrinsicAngles.clone();
	}

	/**
     * Returns the quaternion describing the model orientation.
     * 
     * Defined by three values a, b, c, d.
     * 
     * For a rotation about an axis whose normalized vector is vn, we have:
     * a = vn.x * sin(angle);
	 * b = vn.y * sin(angle);
	 * c = vn.z * sin(angle);
     * d = cos(angle);
     */
	public Quaternion4f getQuaternion() {
	    // FIXME: we post-multiply a and c by -1, because this quaternion gives correct angles for 
	    //        doing OpenGL rotations in sequence, whereas the original values give correct OpenGL
	    //        rotation matrices. Not sure which is correct, but the matrix conversion code seems
		//        less reliable.
		tmpQuaternion.set(-quaternion.a(), quaternion.b(), -quaternion.c(), quaternion.d());
		return new Quaternion4f(tmpQuaternion);
	}
	
	public synchronized void setQuaternion(Quaternion4f q) {
		// see comments in getQuaternion()
		quaternion.set(-q.a(), q.b(), -q.c(), q.d());
		quaternion.normalize();
		intrinsicAnglesUptodate = false;
		extrinsicAnglesUptodate = false;
		glMatrixUptodate = false;
		glInverseMatrixUptodate = false;
		debugAngles();
	}
	
    /**
     * Returns the matrix for performing rotation in OpenGL using glMultMatrixd.
     */
	public synchronized double[] getGLMatrix() {
		
		if (glMatrixUptodate)
			return glMatrix;

		tmpMatrix.set(quaternion);
    	// Reorder axes:
    	// 0 -> 1 (Y)
    	// 1 -> 2 (Z)
    	// 2 -> 0 (X)
    	glMatrix = new double[] {
    		tmpMatrix.m11(), tmpMatrix.m12(), tmpMatrix.m10(), tmpMatrix.m13(),
    		tmpMatrix.m21(), tmpMatrix.m22(), tmpMatrix.m20(), tmpMatrix.m23(),
    		tmpMatrix.m01(), tmpMatrix.m02(), tmpMatrix.m00(), tmpMatrix.m03(),
    		tmpMatrix.m31(), tmpMatrix.m32(), tmpMatrix.m30(), tmpMatrix.m33()            		
    	};
    	glMatrixUptodate = true;
    	return glMatrix;
	}
	
	// FIXME: Bugged
	public synchronized double[] getGLInverseMatrix() {
		
		if (glInverseMatrixUptodate)
			return glInverseMatrix;
		
		tmpQuaternion.invert(quaternion);
		tmpMatrix.set(tmpQuaternion);
    	glInverseMatrix = new double[] {
    		tmpMatrix.m11(), tmpMatrix.m12(), tmpMatrix.m10(), tmpMatrix.m13(),
    		tmpMatrix.m21(), tmpMatrix.m22(), tmpMatrix.m20(), tmpMatrix.m23(),
    		tmpMatrix.m01(), tmpMatrix.m02(), tmpMatrix.m00(), tmpMatrix.m03(),
    		tmpMatrix.m31(), tmpMatrix.m32(), tmpMatrix.m30(), tmpMatrix.m33()            		
        };
    	glInverseMatrixUptodate = true;
      	return glInverseMatrix;
	}
	
	/**
	 * Utility method that returns the orientation of the model in extrinsic angles
	 * relative to the camera. Used for rendering labels.
	 * 
	 * FIXME: uses the extrinsic angles representation, so does not work after setting intrinsic angles.
	 * 
	 * @param viewpoint
	 * @return
	 */
	public Coord3d getRelativeRotation(Coord3d viewpoint) {
		Coord3d relativeRotation = new Coord3d();
    	relativeRotation.x = viewpoint.x + getExtrinsicAngles().x * (float)Math.PI / 180;
    	relativeRotation.y = viewpoint.y - getExtrinsicAngles().z * (float)Math.PI / 180;
    	relativeRotation.z = getExtrinsicAngles().y * (float)Math.PI / 180;
    	if (DEBUG_ANGLES)
    		Watcher.update("relative", relativeRotation.x, relativeRotation.y, relativeRotation.z);
    	return relativeRotation;
	}
	
	//// Conversions
	
	private void convertIntrinsicAnglesToQuaternion() {
	    convertAnglesToQuaternion(intrinsicAngles, EulerOrder.XYZ);
	}
	
	private void convertExtrinsicAnglesToQuaternion() {
		// extrinsic angles are equivalent to applying the transformations in reverse order,
		// or something like that.
   		convertAnglesToQuaternion(extrinsicAngles, EulerOrder.ZYX);
	}

	private void convertAnglesToQuaternion(Coord3d angles, EulerOrder order) {
	    tmpTuple.set(angles.x, angles.y, angles.z);
	    tmpTuple.mul((float)(Math.PI / 180));
    	MatrixUtils.eulerToMatrix4f(tmpTuple, tmpMatrix, order);
    	
    	if (!filterEnabled) {
	    	quaternion.set(tmpMatrix);
	    	quaternion.normalize();
    	} else {
    		tmpQuaternion.set(tmpMatrix);
    		tmpQuaternion.normalize();
    		double timestamp = System.currentTimeMillis() / 1000.0 - timestamp0;
    		tmpQuaternion.a((float)filter_a.filter(tmpQuaternion.a(), timestamp));
    		tmpQuaternion.b((float)filter_b.filter(tmpQuaternion.b(), timestamp));
    		tmpQuaternion.c((float)filter_c.filter(tmpQuaternion.c(), timestamp));
    		tmpQuaternion.d((float)filter_d.filter(tmpQuaternion.d(), timestamp));
    		quaternion.set(tmpQuaternion);
    		quaternion.normalize();
    	}
    	
    	debugAngles();
	}
	
	private void debugAngles() {
    	if (DEBUG_ANGLES) {
    		Coord3d a;
    		a = getIntrinsicAngles();
    		Watcher.update("intrinsic", a.x, a.y, a.z);
    		a = getExtrinsicAngles();
    		Watcher.update("extrinsic", a.x, a.y, a.z);
    		a = extrinsicAnglesOffset;
    		if (a != null)
    			Watcher.update("offset", a.x, a.y, a.z);
    		else
    			Watcher.update("offset", "null", "null", "null");
    		Watcher.update("quaternion", quaternion.a(), quaternion.b(), quaternion.c(), quaternion.d());
    	}
	}

	private void convertQuaternionToIntrinsicAngles() {
		convertQuaternionToAngles(intrinsicAngles, EulerOrder.XYZ);
	}
	
	private void convertQuaternionToExtrinsicAngles() {
		convertQuaternionToAngles(extrinsicAngles, EulerOrder.ZYX);
	}
	
	private void convertQuaternionToAngles(Coord3d angles, EulerOrder order) {
		// We have to transform the quaternion because it seems that for applying rotations
		// in sequence in OpenGL, we need to interpret Tait-Bryan angles as being negative, whereas for
		// computing the OpenGL transformation matrices, we need to take the positive Tait-Bryan angles
		// as input. Not sure why this is the case, but it might have to do with left-handed and
		// right-handed coordinate systems.
		// The quaternion tranformation below is equivalent to passing negative Tait-Bryan angles to
		// convertAnglesToQuaternion.
		tmpQuaternion.set(-quaternion.a(), quaternion.b(), -quaternion.c(), quaternion.d());
		tmpMatrix.set(tmpQuaternion);
		MatrixUtils.matrixToEuler(tmpMatrix, tmpTuple, order); // Only XYZ ordering supported
    	tmpTuple.mul((float)(180 / Math.PI));
    	tmpTuple.mul(-1); // now take negative angles again
    	angles.set(tmpTuple.x(), tmpTuple.y(), tmpTuple.z());
	}
	
}
