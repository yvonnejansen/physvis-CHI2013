/**
 * Copyright (c) 2007-2009, OpenMaLi Project Group all rights reserved.
 * 
 * Portions based on the Sun's javax.vecmath interface, Copyright by Sun
 * Microsystems or Kenji Hiranabe's alternative GC-cheap implementation.
 * Many thanks to the developers.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the 'OpenMaLi Project Group' nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 */
package org.openmali.vecmath2.util;

import org.openmali.FastMath;
import org.openmali.vecmath2.AxisAngle3f;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Tuple3f;

/**
 * Util class for Maths.
 *
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 * @author Pierre Dragicevic
 */
public class MatrixUtils
{
	
	public enum EulerOrder {
	    XYZ, ZXY, ZYX, YZX
	}

    /**
     * Converts a Matrix4f to a Tuple3f with Euler angles.
     * 
     * @param matrix the Matrix4f to be converted
     */
    public static void matrixToEuler( Matrix4f matrix, Tuple3f euler, EulerOrder order )
    {
    	
    	// FIXME: implement the whole method from https://truesculpt.googlecode.com/hg-history/38000e9dfece971460473d5788c235fbbe82f31b/Doc/rotation_matrix_to_euler.pdf
    	// including special cases.
    	
        if ( matrix.m10() == 1 )
        {
            euler.setX( 0.0f );
            euler.setY( FastMath.atan2( matrix.m02(), matrix.m22() ) );
            euler.setZ( FastMath.asin( -matrix.m10() ) );
        }
        else if ( matrix.m10() == -1 )
        {
            euler.setX( 0.0f );
            euler.setY( FastMath.atan2( matrix.m02(), matrix.m22() ) );
            euler.setZ( FastMath.asin( -matrix.m10() ) );
        }
        else
        {
        	// This was the original conversion and seems wrong.
        	
//            euler.setX( FastMath.atan2( -matrix.m12(), matrix.m11() ) );
//            euler.setY( FastMath.atan2( -matrix.m20(), matrix.m00() ) );
//            euler.setZ( FastMath.asin( matrix.m10() ) );
            
        	// This is the new conversion taken from:
            // https://truesculpt.googlecode.com/hg-history/38000e9dfece971460473d5788c235fbbe82f31b/Doc/rotation_matrix_to_euler.pdf
            
        	if (order == EulerOrder.XYZ) {
        		
	            // theta - pitch - y
	            euler.setY( FastMath.asin(matrix.m20()));
	            float cosTheta = (float)Math.cos(euler.getY());
	            
	            // psi - yaw - x
	            euler.setX( FastMath.atan2( matrix.m21() / cosTheta, matrix.m22() / cosTheta) );
	
	            // phi - roll - z
	            euler.setZ( FastMath.atan2( matrix.m10() / cosTheta, matrix.m00() / cosTheta) );
	            
        	} else if (order == EulerOrder.ZYX) {

	            // theta - pitch - y
	            euler.setY( FastMath.asin(- matrix.m02()));
	            float cosTheta = (float)Math.cos(euler.getY());

	            // psi - yaw - x
	            euler.setX( FastMath.atan2( - matrix.m12() / cosTheta, matrix.m22() / cosTheta) );
	
	            // phi - roll - z
	            euler.setZ( FastMath.atan2( - matrix.m01() / cosTheta, matrix.m00() / cosTheta) );

        	}
        }
    }
    
    /**
     * Converts Euler angles to a Matrix4f.
     * 
     * @param eulerX the x-Euler-angle
     * @param eulerY the y-Euler-angle
     * @param eulerZ the z-Euler-angle
     * @param matrix the Matrix4f instance to write rotational values to
     */
    public static void eulerToMatrix4f( float eulerX, float eulerY, float eulerZ, Matrix4f matrix, EulerOrder eulerOrder)
    {
        final float sx = FastMath.sin( eulerX );
        final float sy = FastMath.sin( eulerY );
        final float sz = FastMath.sin( eulerZ );
        final float cx = FastMath.cos( eulerX );
        final float cy = FastMath.cos( eulerY );
        final float cz = FastMath.cos( eulerZ );

        // The code below is inspired from:
        // http://stackoverflow.com/questions/1568568/how-to-convert-euler-angles-to-directional-vector
        // Not all 6 orders are implemented, to implement more see the website. 
        // The order of axes here is inverted compared to the website, e.g., XYZ -> ZYX

        if (eulerOrder == EulerOrder.XYZ) {  
        	
        	// This case was present in the original implementation and can be found
        	// on the website under the order ZYX (see comment above).
        	
//            Mx.M[0][0]=Cy*Cz;
//            Mx.M[0][1]=Cz*Sx*Sy-Cx*Sz;
//            Mx.M[0][2]=Cx*Cz*Sy+Sx*Sz;
//            Mx.M[1][0]=Cy*Sz;
//            Mx.M[1][1]=Cx*Cz+Sx*Sy*Sz;
//            Mx.M[1][2]=-Cz*Sx+Cx*Sy*Sz;
//            Mx.M[2][0]=-Sy;
//            Mx.M[2][1]=Cy*Sx;
//            Mx.M[2][2]=Cx*Cy;
            
	        matrix.set( 0, 0, cy * cz );
	        matrix.set( 0, 1, -( cx * sz ) + ( sx * sy * cz ) );
	        matrix.set( 0, 2, ( sx * sz) + (cx * sy * cz ) );
	        matrix.set( 1, 0, cy * sz );
	        matrix.set( 1, 1, ( cx * cz ) + ( sx * sy * sz ) );
	        matrix.set( 1, 2, -( sx * cz ) + ( cx * sy * sz ) );
	        matrix.set( 2, 0, -sy );
	        matrix.set( 2, 1, sx * cy );
	        matrix.set( 2, 2, cx * cy );
	        matrix.set( 3, 3, 1 );
	        
        } else if (eulerOrder == EulerOrder.ZYX) {
//          Mx.M[0][0]=Cy*Cz;
//          Mx.M[0][1]=-Cy*Sz;
//          Mx.M[0][2]=Sy;
//          Mx.M[1][0]=Cz*Sx*Sy+Cx*Sz;
//          Mx.M[1][1]=Cx*Cz-Sx*Sy*Sz;
//          Mx.M[1][2]=-Cy*Sx;
//          Mx.M[2][0]=-Cx*Cz*Sy+Sx*Sz;
//          Mx.M[2][1]=Cz*Sx+Cx*Sy*Sz;
//          Mx.M[2][2]=Cx*Cy;
      	matrix.set(0, 0, cy*cz);
      	matrix.set(0, 1, -cy*sz);
      	matrix.set(0, 2, sy);
      	matrix.set(1, 0, cz*sx*sy + cx*sz);
      	matrix.set(1, 1, cx*cz - sx*sy*sz);
      	matrix.set(1, 2, -cy*sx);
      	matrix.set(2, 0, -cx*cz*sy + sx*sz);
      	matrix.set(2, 1, cz*sx + cx*sy*sz);
      	matrix.set(2, 2, cx*cy);
      	matrix.set( 3, 3, 1 );
      } else if (eulerOrder == EulerOrder.ZXY) {
        	        	
//            Mx.M[0][0]=Cy*Cz+Sx*Sy*Sz;
//            Mx.M[0][1]=Cz*Sx*Sy-Cy*Sz;
//            Mx.M[0][2]=Cx*Sy;
//            Mx.M[1][0]=Cx*Sz;
//            Mx.M[1][1]=Cx*Cz;
//            Mx.M[1][2]=-Sx;
//            Mx.M[2][0]=-Cz*Sy+Cy*Sx*Sz;
//            Mx.M[2][1]=Cy*Cz*Sx+Sy*Sz;
//            Mx.M[2][2]=Cx*Cy;
        	matrix.set(0, 0, cy*cz + sx*sy*sz);
        	matrix.set(0, 1, cz*sx*sy - cy*sz);
        	matrix.set(0, 2, cx*sy);
        	matrix.set(1, 0, cx*sz);
        	matrix.set(1, 1, cx*cz);
        	matrix.set(1, 2, -sx);
        	matrix.set(2, 0, -cz*sy + cy*sx*sz);
        	matrix.set(2, 1, cy*cz*sx + sy*sz);
        	matrix.set(2, 2, cx*cy);
        	matrix.set( 3, 3, 1 );
        } else if (eulerOrder == EulerOrder.YZX) {
//            Mx.M[0][0]=Cy*Cz;
//            Mx.M[0][1]=-Sz;
//            Mx.M[0][2]=Cz*Sy;
//            Mx.M[1][0]=Sx*Sy+Cx*Cy*Sz;
//            Mx.M[1][1]=Cx*Cz;
//            Mx.M[1][2]=-Cy*Sx+Cx*Sy*Sz;
//            Mx.M[2][0]=-Cx*Sy+Cy*Sx*Sz;
//            Mx.M[2][1]=Cz*Sx;
//            Mx.M[2][2]=Cx*Cy+Sx*Sy*Sz;
        	matrix.set(0, 0, cy*cz);
        	matrix.set(0, 1, -sz);
        	matrix.set(0, 2, cz*sy);
        	matrix.set(1, 0, sx*sy + cx*cy*sz);
        	matrix.set(1, 1, cx*cz);
        	matrix.set(1, 2, -cy*sx + cx*sy*sz);
        	matrix.set(2, 0, -cx*sy + cy*sx*sz);
        	matrix.set(2, 1, cz*sx);
        	matrix.set(2, 2, cx*cy+sx*sy*sz);
        	matrix.set( 3, 3, 1 );
        }
    }
    
    /**
     * Converts Euler angles to a Matrix4f.
     * 
     * @param euler the Tuple3f containing all three Euler angles
     * @param matrix the Matrix4f instance to write rotational values to
     */
    public static void eulerToMatrix4f( Tuple3f euler, Matrix4f matrix, EulerOrder eulerOrder )
    {
        eulerToMatrix4f( euler.getX(), euler.getY(), euler.getZ(), matrix, eulerOrder );
    }
    
    /**
     * Converts Euler angles to a Matrix4f.
     * 
     * @param euler the Tuple3f containing all three Euler angles
     * 
     * @return the new Matrix4f instance reflecting the rotation
     */
    public static Matrix4f eulerToMatrix4f( Tuple3f euler, EulerOrder eulerOrder )
    {
        Matrix4f matrix = new Matrix4f();
        
        eulerToMatrix4f( euler.getX(), euler.getY(), euler.getZ(), matrix, eulerOrder );
        
        return ( matrix );
    }
    
    /**
     * Creates a 3x3 rotation matrix by a specified vector.
     * 
     * @param axisX the x-component of the vector (axis) to rotate about
     * @param axisY the y-component of the vector (axis) to rotate about
     * @param axisZ the z-component of the vector (axis) to rotate about
     * @param angle the angle to rotate by
     * @param out the Matrix3f to write the result to
     */
    public static final void getRotationMatrix(float axisX, float axisY, float axisZ, float angle, Matrix3f out)
    {
        final float length = FloatUtils.vectorLength( axisX, axisY, axisZ );
        final float v1 = (axisX / length);
        final float v2 = (axisY / length);
        final float v3 = (axisZ / length);
        
        final float v1q = v1 * v1;
        final float v2q = v2 * v2;
        final float v3q = v3 * v3;
        
        final float a = angle;
        final float sin_a = FastMath.sin( a );
        final float cos_a = FastMath.cos( a );
        
        final float m11 = ( cos_a + (v1q * (1 - cos_a)) );
        final float m12 = ( (v1 * v2 * (1 - cos_a)) - (v3 * sin_a) );
        final float m13 = ( (v1 * v3 * (1 - cos_a)) + (v2 * sin_a) );
        final float m21 = ( (v2 * v1 * (1 - cos_a)) + (v3 * sin_a) );
        final float m22 = ( cos_a + (v2q * (1 - cos_a)) );
        final float m23 = ( (v2 * v3 * (1 - cos_a)) - (v1 * sin_a) );
        final float m31 = ( (v3 * v1 * (1 - cos_a)) - (v2 * sin_a) );
        final float m32 = ( (v3 * v2 * (1 - cos_a)) + (v1 * sin_a) );
        final float m33 = ( cos_a + (v3q * (1 - cos_a)) );
        
        /*
        out.set( m11, m12, m13,
                 m21, m22, m23,
                 m31, m32, m33
               );
        */
        out.m00( m11 );
        out.m01( m12 );
        out.m02( m13 );
        out.m10( m21 );
        out.m11( m22 );
        out.m12( m23 );
        out.m20( m31 );
        out.m21( m32 );
        out.m22( m33 );
    }
    
    /**
     * Creates a 3x3 rotation matrix by a specified vector.
     * 
     * @param axisX the vector (axis) to rotate around
     * @param axisY the vector (axis) to rotate around
     * @param axisZ the vector (axis) to rotate around
     * @param angle the angle to rotate by
     * @return the created 3x3 rotation matrix
     */
    public static final Matrix3f getRotationMatrix( float axisX, float axisY, float axisZ, float angle )
    {
        final Matrix3f result = new Matrix3f();
        
        getRotationMatrix( axisX, axisY, axisZ, angle, result );
        
        return ( result );
    }
    
    /**
     * Creates a 3x3 rotation matrix by a specified vector.
     * 
     * @param axis Rotation axis
     * @param angle the angle to rotate by
     * @param out the Matrix3f to write the result to
     */
    public static void getRotationMatrix( Tuple3f axis, float angle, Matrix3f out )
    {
        getRotationMatrix( axis.getX(), axis.getY(), axis.getZ(), angle, out );
    }
    
    /**
     * Creates a 3x3 rotation matrix by a specified vector.
     * 
     * @param axis Rotation axis
     * @param angle the angle to rotate by
     * @return the created 3x3 rotation matrix
     */
    public static Matrix3f getRotationMatrix( Tuple3f axis, float angle )
    {
        return ( getRotationMatrix( axis.getX(), axis.getY(), axis.getZ(), angle ) );
    }
    
    /**
     * Computes the rotation between the vectors v1 ans v2.
     * 
     * @param v1x the first vector
     * @param v1y the first vector
     * @param v1z the first vector
     * @param v2x the second vector
     * @param v2y the second vector
     * @param v2z the second vector
     * @param normalize normalize input vectors (bitmask)
     * @param result the result object
     * 
     * @return the result object back again
     */
    public static final Matrix3f computeRotation( float v1x, float v1y, float v1z, float v2x, float v2y, float v2z, int normalize, Matrix3f result )
    {
        AxisAngle3f rotation = AxisAngle3f.fromPool();
        
        FloatUtils.computeRotation( v1x, v1y, v1z, v2x, v2y, v2z, normalize, rotation );
        
        result.set( rotation );
        
        AxisAngle3f.toPool( rotation );
        
        return ( result );
    }
    
    /**
     * Computes the rotation between the vectors v1 ans v2.
     * 
     * @param v1 the first vector
     * @param v2 the second vector
     * @param normalize normalize input vectors (bitmask)
     * @param result the result object
     * 
     * @return the result object back again
     */
    public static final Matrix3f computeRotation( Tuple3f v1, Tuple3f v2, int normalize, Matrix3f result )
    {
        AxisAngle3f rotation = AxisAngle3f.fromPool();
        
        TupleUtils.computeRotation( v1, v2, normalize, rotation );
        
        result.set( rotation );
        
        AxisAngle3f.toPool( rotation );
        
        return ( result );
    }
}
