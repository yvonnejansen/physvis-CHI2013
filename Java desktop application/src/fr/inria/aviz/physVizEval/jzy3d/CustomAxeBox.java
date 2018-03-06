package fr.inria.aviz.physVizEval.jzy3d;

import java.util.Hashtable;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.apache.batik.dom.util.HashTable;
import org.jzy3d.colors.Color;
import org.jzy3d.io.GLImage;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Vector3d;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.layout.AxeBoxLayout;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.ViewPort;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.overlay.TextOverlay;

import fr.inria.aviz.physVizEval.barchart3d.Box;
import fr.inria.aviz.physVizEval.util.Watcher;


public class CustomAxeBox extends AxeBox {
	
	Color zLabelColor = new Color(0.5f, 0.5f, 0.5f);
	
	double[] glRotationMatrix = null;
	double[] glRotationInverseMatrix = null;
	LabelStabilizer stab = new LabelStabilizer();
	
	public CustomAxeBox(BoundingBox3d bbox){
		super(bbox);
	}
	
	public CustomAxeBox(BoundingBox3d bbox, IAxeLayout layout){
		super(bbox, layout);
	}
	
	protected BoundingBox3d drawTicks(GL2 gl, GLU glu, Camera cam, int axis, int direction, Color color, Halign hal, Valign val){
		
		glRotationMatrix = ((CustomView)view).getGLRotationMatrix();
//		glRotationInverseMatrix = ((CustomView)view).modelRotation.getGLInverseMatrix();
		
		int quad_0; 
		int quad_1;
		Halign hAlign;
		Valign vAlign;
		float tickLength = 30.0f; // with respect to range
		float axeLabelDist = 2.5f;
		float tickLengthLab = 20.0f;
		BoundingBox3d ticksTxtBounds = new BoundingBox3d();
		
		// Retrieve the quads that intersect and create the selected axe
		if(direction==AXE_X){
			quad_0 = axeXquads[axis][0];
			quad_1 = axeXquads[axis][1];
		}
		else if(direction==AXE_Y){
			quad_0 = axeYquads[axis][0];
			quad_1 = axeYquads[axis][1];
		}
		else{ //(axis==AXE_Z)
			quad_0 = axeZquads[axis][0];
			quad_1 = axeZquads[axis][1];
		}
		
		// Computes POSition of ticks lying on the selected axe 
		// (i.e. 1st point of the tick line)
		float xpos = normx[quad_0] + normx[quad_1];
		float ypos = normy[quad_0] + normy[quad_1];
		float zpos = normz[quad_0] + normz[quad_1];
		
		// Variables for storing the position of the LABel position
		// (2nd point on the tick line)
		float xlab;
		float ylab;
		float zlab;

		// Computes the DIRection of the ticks
		// assuming initial vector point is the center 
		float xdir = ( normx[quad_0] + normx[quad_1] ) - center.x;
		float ydir = ( normy[quad_0] + normy[quad_1] ) - center.y;
		float zdir = ( normz[quad_0] + normz[quad_1] ) - center.z; 
		xdir = xdir==0?0:xdir/Math.abs(xdir); // so that direction as length 1
		ydir = ydir==0?0:ydir/Math.abs(ydir);
		zdir = zdir==0?0:zdir/Math.abs(zdir);
		
		// Draw the label for axis
		String axeLabel;
		int dist = 1;
		if(direction==AXE_X){ 
			xlab  = center.x;
			ylab  = axeLabelDist*(yrange/tickLength)*dist*ydir + ypos;
			zlab  = axeLabelDist*(zrange/tickLength)*dist*zdir + zpos;
			axeLabel = layout.getXAxeLabel();
		}else if(direction==AXE_Y){
			xlab  = axeLabelDist*(xrange/tickLength)*dist*xdir + xpos;
			ylab  = center.y;
			zlab  = axeLabelDist*(zrange/tickLength)*dist*zdir + zpos;
			axeLabel = layout.getYAxeLabel();
		}else{ 
			xlab  = axeLabelDist*(xrange/tickLength)*dist*xdir + xpos;
			ylab  = axeLabelDist*(yrange/tickLength)*dist*ydir + ypos;
			zlab  = center.z;
			axeLabel = layout.getZAxeLabel();
		}
		
		if( (direction==AXE_X && layout.isXAxeLabelDisplayed())
		 || (direction==AXE_Y && layout.isYAxeLabelDisplayed())
		 || (direction==AXE_Z && layout.isZAxeLabelDisplayed()) ){
			Coord3d labelPosition = new Coord3d(xlab, ylab, zlab);
			if(txtRenderer!=null)
				txtRenderer.appendText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, Color.BLACK);
			else{
				BoundingBox3d labelBounds = txt.drawText(gl, glu, cam, axeLabel, labelPosition, Halign.CENTER, Valign.CENTER, direction==AXE_Z ? zLabelColor : Color.BLACK);		
				if(labelBounds!=null)
				    ticksTxtBounds.add( labelBounds );
			}
		}
		
		// Retrieve the selected tick positions
		float ticks[];
		if(direction==AXE_X) 
			ticks = layout.getXTicks();
		else if(direction==AXE_Y) 
			ticks = layout.getYTicks();
		else //(axis==AXE_Z) 
			ticks = layout.getZTicks();
		
		
		// Draw the ticks, labels, and dotted lines iteratively
		String tickLabel = "";		
		gl.glLineWidth(1);
		
		double rotx = ((CustomView)view).getRelativeRotation().x;
		double roty = ((CustomView)view).getRelativeRotation().y;
		double tilt = ((CustomView)view).getRelativeRotation().z;
		while (rotx < -Math.PI)
			rotx += 2*Math.PI;
		while (rotx > Math.PI)
			rotx -= 2*Math.PI;
		boolean dirY = Math.abs(rotx - Math.PI / 2) > Math.abs(rotx + Math.PI / 2);
		boolean dirX = Math.min(Math.abs(rotx - Math.PI), Math.abs(rotx + Math.PI)) < Math.abs(rotx);

		
		for(int t=0; t<ticks.length; t++){
			
			// -----------------------------------------------------
			double upDownAmount = 0;
			if(direction==AXE_X) {
				xpos  = ticks[t];
				xlab  = xpos;
				ylab  = (yrange/tickLengthLab)*ydir + ypos;
				zlab  = (zrange/tickLengthLab)*zdir + zpos;
				tickLabel = layout.getXTickRenderer().format(xpos);
				upDownAmount = 1 - 4 * Math.abs(Math.abs(rotx) - Math.PI / 2);
				upDownAmount -= 4 * (Math.PI / 2 - Math.abs(Math.abs(tilt) - Math.PI / 2));
				if (upDownAmount < 0)
					upDownAmount = 0;
//				if (upDownAmount > 0)
//					upDownAmount = 1;
			}
			// --------------------------------------------------------
			double slopeAmount = 0;
			if (direction == AXE_X) {
				double posSlopeAmount = 1 - Math.min(Math.abs(rotx - Math.PI), Math.abs(rotx + Math.PI));
				if (posSlopeAmount < 0)
					posSlopeAmount = 0;
				double negSlopeAmount = 1 - Math.abs(rotx);
				if (negSlopeAmount < 0)
					negSlopeAmount = 0;
				slopeAmount = (negSlopeAmount > posSlopeAmount) ? -negSlopeAmount : posSlopeAmount;
			}
			if (direction==AXE_Y) {
				double posSlopeAmount = 1 - Math.abs(rotx + Math.PI/2);
				if (posSlopeAmount < 0)
					posSlopeAmount = 0;
				double negSlopeAmount = 1 - Math.abs(rotx - Math.PI/2);
				if (negSlopeAmount < 0)
					negSlopeAmount = 0;
				slopeAmount = (negSlopeAmount > posSlopeAmount) ? -negSlopeAmount : posSlopeAmount;
			}
			
			slopeAmount *= Math.max(0, 1 - 4 * roty);
			// singularity
			if (Math.abs(Math.abs(tilt) - Math.PI / 2) < 0.1)
				slopeAmount = 0;
			if (slopeAmount > 1.5 || slopeAmount < -1.5)
				slopeAmount = 0;
			
//			Watcher.watch(axeLabel, "updown", "slope");
//			Watcher.update(axeLabel, upDownAmount, slopeAmount);

			//slopeAmount = 0;
			
			// ---------------------------------------------------------
			double lengthenFactor = 1 / 30.0;
			if (getView().getCameraMode() == CameraMode.ORTHOGONAL)
				lengthenFactor = 1.3 / 30.0;
			double lengthen = (t % 2 == 0) ? upDownAmount / 3 * lengthenFactor : -upDownAmount * lengthenFactor;
			double zlengthen = lengthen * zrange;
			double xlengthen = lengthen * xrange;
			double ylengthen = lengthen * yrange;
			// --------------------
			lengthenFactor = 1 / 60.0;
			if (getView().getCameraMode() == CameraMode.ORTHOGONAL)
				lengthenFactor = 2.4 / 60.0;
			if (slopeAmount < 0) {
				lengthen =  slopeAmount * (t) * lengthenFactor;
			} else {
				lengthen = slopeAmount * (t - ticks.length + 1) * lengthenFactor;
			}
			zlengthen += lengthen * zrange;
			xlengthen += lengthen * xrange / 4;
			ylengthen += lengthen * yrange / 4;
			
			// Shift the tick vector along the selected axis
			// and set the tick length
			if(direction==AXE_X){
				xpos  = ticks[t];
				xlab  = xpos;
				ylab  = (yrange/tickLength)*ydir + ypos;
				zlab  = (zrange/tickLength)*zdir + zpos;
				tickLabel = layout.getXTickRenderer().format(xpos);
				// --------------------- Up / down labels and slope
				zlab += zlengthen;
				ylab += (dirY ? 1 : -1) * ylengthen;
			}
			else if(direction==AXE_Y){
				ypos  = ticks[t];
				xlab  = (xrange/tickLength)*xdir + xpos;
				ylab  = ypos;
				zlab  = (zrange/tickLength)*zdir + zpos;
				tickLabel = layout.getYTickRenderer().format(ypos);
				// --------------------- Up / down labels and slope
				zlab += zlengthen;
				xlab += (dirX ? 1 : -1) * xlengthen;
			}
			else{ //(axis==AXE_Z)
				zpos  = ticks[t];
				xlab  = (xrange/tickLength)*xdir + xpos;
				ylab  = (yrange/tickLength)*ydir + ypos;
				zlab  = zpos;
				tickLabel = layout.getZTickRenderer().format(zpos);
			}
			Coord3d tickPosition = new Coord3d(xlab, ylab, zlab);
					
			// Draw the tick line
			gl.glColor3f(color.r, color.g, color.b);
			gl.glBegin(GL2.GL_LINES);
				gl.glVertex3f( xpos, ypos, zpos ); 
				gl.glVertex3f( xlab, ylab, zlab ); 			
			gl.glEnd();
			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////////////////
			
			// Shift the tick vector along the selected axis
			// and set the tick length
			if(direction==AXE_X){
				xpos  = ticks[t];
				xlab  = xpos;
				ylab  = (yrange/tickLengthLab)*ydir + ypos;
				zlab  = (zrange/tickLengthLab)*zdir + zpos;
				tickLabel = layout.getXTickRenderer().format(xpos);
			}
			else if(direction==AXE_Y){
				ypos  = ticks[t];
				xlab  = (xrange/tickLengthLab)*xdir + xpos;
				ylab  = ypos;
				zlab  = (zrange/tickLengthLab)*zdir + zpos;
				tickLabel = layout.getYTickRenderer().format(ypos);
			}
			else{ //(axis==AXE_Z)
				zpos  = ticks[t];
				xlab  = (xrange/tickLengthLab)*xdir + xpos;
				ylab  = (yrange/tickLengthLab)*ydir + ypos;
				zlab  = zpos;
				tickLabel = layout.getZTickRenderer().format(zpos);
			}
			tickPosition = new Coord3d(xlab, ylab, zlab);
						
			// Select the alignement of the tick label
			float vAlign_float = 0, hAlign_float;
			if(hal==null) {
				double side = camside(cam, tickPosition);
				if (side < -1)
					side = -1;
				if (side > 1)
					side = 1;
//				float align = 0;
//				if (side > 0.5)
//					align = -1;
//				else if (side < -0.5)
//					align = 1;
//				hAlign_float = align;
				//if (side > -0.5)
				//	hAlign_float = - (float)((side + 0.5f) * 2); // normally *2
				//else 
				hAlign_float = - (float)(side * 4); // normally *2
				
				hAlign_float *= (1 - upDownAmount);

				if (hAlign_float > 1)
					hAlign_float = 1f;
				if (hAlign_float < -1)
					hAlign_float = -1f;
				
			} else
				hAlign_float = CustomTextBitMapRenderer.halignToFloat(hal);
			
			if(val==null){
				if(direction==AXE_Z)
					vAlign_float = 0;
				else{
					if(zdir>0)
						vAlign_float = -1;
					else
						vAlign_float = 1;
				}
			}
			else
				vAlign = val;

			// --------------------- Up / down labels and slope
			tickPosition.z += zlengthen;
			if(direction==AXE_X)
				tickPosition.y += (dirY ? 1 : -1) * ylengthen;
			if(direction==AXE_Y)
				tickPosition.x += (dirX ? 1 : -1) * xlengthen;
						
//System.err.println(hAlign_float);
			
			// Draw the text label of the current tick
//			if(txtRenderer!=null)
//				txtRenderer.appendText(gl, glu, cam, tickLabel, tickPosition, hAlign, vAlign, color);
//			else{
				gl.glPushMatrix();
				if(direction==AXE_Z)
					vAlign_float = 0;
				else{
					if(zdir>0) {
						vAlign_float = -1;
					//	tickPosition.z += 0.4f;
					} else {
						vAlign_float = 1;
					//	tickPosition.z -= 0.4f;
					}
				}
				
//				tickPosition = transformPoint(tickPosition, glRotationMatrix);
//				tickPosition = stab.setNewLabelPosition(tickLabel, tickPosition);
//				tickPosition = transformPoint(tickPosition, glRotationInverseMatrix);
				
				BoundingBox3d tickBounds = ((CustomTextBitMapRenderer)txt).drawText(gl, glu, cam, tickLabel, tickPosition, hAlign_float, vAlign_float, direction==AXE_Z ? zLabelColor : Color.BLACK);
				
				if(tickBounds!=null)
				    ticksTxtBounds.add( tickBounds );
			//}
		}
				
		return ticksTxtBounds;
	}
			
	// takes a gl matrix
	private static Coord3d transformPoint(Coord3d point, double[] matrix) {
		
		Coord3d point2 = new Coord3d();
		
		point2.x = (float)(point.x * matrix[0] + point.y * matrix[4] + point.z * matrix[8] + 1 * matrix[12]);
		point2.y = (float)(point.x * matrix[1] + point.y * matrix[5] + point.z * matrix[9] + 1 * matrix[13]);
		point2.z = (float)(point.x * matrix[2] + point.y * matrix[6] + point.z * matrix[10] + 1 * matrix[14]);
		float r = (float)(point.x * matrix[3] + point.y * matrix[7] + point.z * matrix[11] + 1 * matrix[15]);		
		point2.x /= r;
		point2.y /= r;
		point2.z /= r;
		
		return point2;
		
//    	return new double[] {
//              0                1                2                3
//        		tmpMatrix.m11(), tmpMatrix.m12(), tmpMatrix.m10(), tmpMatrix.m13(),
//              4                5                6                7				
//        		tmpMatrix.m21(), tmpMatrix.m22(), tmpMatrix.m20(), tmpMatrix.m23(),
//              8                9                10               11
//        		tmpMatrix.m01(), tmpMatrix.m02(), tmpMatrix.m00(), tmpMatrix.m03(),
//              12               13               14               15				
//        		tmpMatrix.m31(), tmpMatrix.m32(), tmpMatrix.m30(), tmpMatrix.m33()            		
//        	};
		
	}
	
	/** Return true if the given point is on the left of the vector eye->target.*/
	public double camside(Camera cam, Coord3d point){
		
		// Rotate point
		point = transformPoint(point, glRotationMatrix);
		
		Coord3d target = boxBounds.getCenter();//cam.getTarget();
		target = transformPoint(target, glRotationMatrix);
		
		Coord3d eye = cam.getEye();
		
		double target_eye_dist = target.distance(eye);
		double target_point_dist = target.distance(point);
		return ((point.x - target.x) * (eye.y - target.y) - (point.y - target.y) * (eye.x - target.x)) / (target_eye_dist * target_point_dist);
	}
	
	/**
	 * Draws the AxeBox. The camera is used to determine which axis is closest
	 * to the ur point ov view, in order to decide for an axis on which 
	 * to diplay the tick values.
	 */
	@Override
	public void draw(GL2 gl, GLU glu, Camera camera){
		// Set scaling
		gl.glLoadIdentity();
		gl.glScalef(scale.x, scale.y, scale.z);
		
		// Set culling
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glFrontFace(GL2.GL_CCW);
		gl.glCullFace(GL2.GL_FRONT);
		
		// Draw cube in feedback buffer for computing hidden quads
		quadIsHidden = getHiddenQuads(gl);	
		
//		for (int i=0; i<quadIsHidden.length; i++)
//			quadIsHidden[i] = true;
		
//		double roty = view.getViewPoint().y / Math.PI * 2;
//		System.err.println(roty);
		
		// Plain part of quad making the surrounding box
		//if( layout.isFaceDisplayed() ){
			Color quadcolor = layout.getQuadColor();
			gl.glPolygonMode(GL2.GL_BACK, GL2.GL_FILL);
			gl.glColor4f(1f, 1f, 1f, 1f);//quadcolor.r, quadcolor.g, quadcolor.b, quadcolor.a);
			gl.glLineWidth(1.0f);
			gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
			gl.glPolygonOffset(1.0f, 1.0f); // handle stippling
			drawCube(gl, GL2.GL_RENDER);
			gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
		//}
		
		// Edge part of quads making the surrounding box
		Color gridcolor = layout.getGridColor();
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
		gl.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
		gl.glLineWidth(1);			
		drawCube(gl, GL2.GL_RENDER);	
				
		// Draw grids on non hidden quads
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
		gl.glColor4f(gridcolor.r, gridcolor.g, gridcolor.b, gridcolor.a);
		gl.glLineWidth(0.5f);
		//gl.glLineStipple(1, (short)0xAAAA);
		//gl.glEnable(GL2.GL_LINE_STIPPLE);		
		for(int quad=0; quad<6; quad++)
			if(!quadIsHidden[quad] && quad != 4 && quad != 5)		
				drawGridOnQuad(gl, quad, camera);
		//gl.glDisable(GL2.GL_LINE_STIPPLE);
		
		// Draw ticks on the closest axes
		wholeBounds.reset();
		wholeBounds.add(boxBounds);
		
		//gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		
		// Display x axis ticks
		if(xrange>0 && layout.isXTickLabelDisplayed()){
			
			// If we are on top, we make direct axe placement
			if(view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				BoundingBox3d bbox = drawTicks(gl, glu, camera, 1, AXE_X, layout.getXTickColor(), Halign.LEFT, Valign.TOP); // setup tick labels for X on the bottom 
				wholeBounds.add(bbox);
			}
			// otherwise computed placement
			else{
				int xselect = findClosestXaxe(camera);
				if(xselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, xselect, AXE_X, layout.getXTickColor());
					wholeBounds.add(bbox);
				}
				else{
					//System.err.println("no x axe selected: " + Arrays.toString(quadIsHidden));
					// HACK: handles "on top" view, when all face of cube are drawn, which forbid to select an axe automatically
//					BoundingBox3d bbox = drawTicks(gl, glu, camera, 2, AXE_X, layout.getXTickColor(), Halign.CENTER, Valign.TOP); 
//					wholeBounds.add(bbox);
				}
			}
		}
		
		// Display y axis ticks
		if(yrange>0 && layout.isYTickLabelDisplayed()){
			if( view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				BoundingBox3d bbox = drawTicks(gl, glu, camera, 2, AXE_Y, layout.getYTickColor(), Halign.LEFT, Valign.GROUND); // setup tick labels for Y on the left 
				wholeBounds.add(bbox);
			}
			else{
				int yselect = findClosestYaxe(camera);
				if(yselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, yselect, AXE_Y, layout.getYTickColor());
					wholeBounds.add(bbox);
				}
				else{
					//System.err.println("no y axe selected: " + Arrays.toString(quadIsHidden));
//					// HACK: handles "on top" view, when all face of cube are drawn, which forbid to select an axe automatically
//					BoundingBox3d bbox = drawTicks(gl, glu, camera, 1, AXE_Y, layout.getYTickColor(), Halign.RIGHT, Valign.GROUND);
//					wholeBounds.add(bbox);
				}
			}
		}
		
		// Display z axis ticks
		if(zrange>0 && layout.isZTickLabelDisplayed()){
			if( view != null && view.getViewMode().equals(ViewPositionMode.TOP) ){
				
			}
			else{
				int zselect = findClosestZaxe(camera, true);
				if(zselect>=0){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, zselect, AXE_Z, layout.getZTickColor());
					wholeBounds.add(bbox);
				}	
				int zselect2 = findClosestZaxe(camera, false);
				if(zselect2>=0 && zselect2 != zselect){
					BoundingBox3d bbox = drawTicks(gl, glu, camera, zselect2, AXE_Z, layout.getZTickColor());
					wholeBounds.add(bbox);
				}	
			}
		}
		
		// Unset culling
		gl.glDisable(GL2.GL_CULL_FACE);
	}
	
	protected void drawGridOnQuad(GL2 gl, int quad, Camera camera){
		
		int closestx = findClosestXaxe(camera);
		int closesty = findClosestYaxe(camera);

//		if (closestx == quad && center.z < (axeXz[closestx][0]+axeXz[closestx][1])/2) {
//				System.err.println("ABOVE " + closestx);
//		}
		
		//		// Draw X grid along X axis
		if((quad!=0)&&(quad!=1)){ 
			if (closestx != -1 && center.z < (axeXz[closestx][0]+axeXz[closestx][1])/2) {
				float[] xticks = layout.getXTicks();
				for(int t=0; t<xticks.length; t++){
					gl.glBegin(GL2.GL_LINES);
						gl.glVertex3f( xticks[t], quady[quad][0], quadz[quad][0]);
						gl.glVertex3f( xticks[t], quady[quad][2], quadz[quad][2]);
					gl.glEnd();
				}
			}
		}
//		// Draw Y grid along Y axis
		if((quad!=2)&&(quad!=3)){
			if (closesty != -1 && center.z < (axeXz[closesty][0]+axeXz[closesty][1])/2) {
				float[] yticks = layout.getYTicks();
				for(int t=0; t<yticks.length; t++){
					gl.glBegin(GL2.GL_LINES);
						gl.glVertex3f( quadx[quad][0], yticks[t], quadz[quad][0]);
						gl.glVertex3f( quadx[quad][2], yticks[t], quadz[quad][2]);
					gl.glEnd();
				}
			}
		}
		// Draw Z grid along Z axis
		if((quad!=4)&&(quad!=5)){
			float[] zticks = layout.getZTicks();
			for(int t=0; t<zticks.length; t++){
				gl.glBegin(GL2.GL_LINES);
					gl.glVertex3f( quadx[quad][0], quady[quad][0], zticks[t]);
					gl.glVertex3f( quadx[quad][2], quady[quad][2], zticks[t]);
				gl.glEnd();
			}
		}
	}
	
	/******************************************************************/
	/**                    AXIS SELECTION                            **/
	
    /**
     * Selects the closest displayable X axe from camera
     */
    protected int findClosestXaxe(Camera cam){
    	int na = 4;
    	double [] distAxeX = new double[na];
		
    	double roty = ((CustomView)view).getRelativeRotation().y / Math.PI * 2;
    	
    	// keeps axes that are not at intersection of 2 quads
		for(int a=0; a<na; a++){
			if(quadIsHidden[axeXquads[a][0]] ^ quadIsHidden[axeXquads[a][1]])
				distAxeX[a] = new Vector3d(axeXx[a][0], axeXy[a][0], axeXz[a][0], 
                						   axeXx[a][1], axeXy[a][1],  axeXz[a][1]
                					).distance(cam.getEye());
			else
				distAxeX[a] = Double.MAX_VALUE;
		}
		
		// prefers the lower one
		for(int a=0; a<na; a++){
			if(distAxeX[a] < Double.MAX_VALUE){
				if(center.z > (axeXz[a][0]+axeXz[a][1])/2)
					distAxeX[a] *= -1;
			} else if (roty > 0.4) {
				distAxeX[a] = new Vector3d(axeXx[a][0], axeXy[a][0], axeXz[a][0], 
						   axeXx[a][1], axeXy[a][1],  axeXz[a][1]
				).distance(cam.getEye()) * 100;
			}
		}
		
		return min(distAxeX);
    }
    
    /**
     * Selects the closest displayable Y axe from camera
     */
    protected int findClosestYaxe(Camera cam){
    	int na = 4;
    	double [] distAxeY = new double[na];
		
    	double roty = ((CustomView)view).getRelativeRotation().y / Math.PI * 2;

    	// keeps axes that are not at intersection of 2 quads
		for(int a=0; a<na; a++){
			if(quadIsHidden[axeYquads[a][0]] ^ quadIsHidden[axeYquads[a][1]])
				distAxeY[a] = new Vector3d(axeYx[a][0], axeYy[a][0], axeYz[a][0], 
                						   axeYx[a][1], axeYy[a][1], axeYz[a][1]
				                          ).distance(cam.getEye());
			else
				distAxeY[a] = Double.MAX_VALUE;
		}
		
		// prefers the lower one
		for(int a=0; a<na; a++){
			if(distAxeY[a] < Double.MAX_VALUE){
				if(center.z > (axeYz[a][0]+axeYz[a][1])/2)
					distAxeY[a] *= -1;
			} else if (roty > 0.4) {
				distAxeY[a] = new Vector3d(axeYx[a][0], axeYy[a][0], axeYz[a][0], 
						   axeYx[a][1], axeYy[a][1], axeYz[a][1]
                       ).distance(cam.getEye()) * 100;
			}
		}
		
		return min(distAxeY);
    }
    
    /**
     * Selects the closest displayable Z axe from camera
     */
    protected int findClosestZaxe(Camera cam, boolean rightSide){
    	int na = 4;
    	double [] distAxeZ = new double[na];
		
 //   	double roty = ((CustomView)view).getRelativeRotation().y / Math.PI * 2;
    	
    	// keeps axes that are not at intersection of 2 quads
		for(int a=0; a<na; a++){
			if(quadIsHidden[axeZquads[a][0]] ^ quadIsHidden[axeZquads[a][1]])
				distAxeZ[a] = new Vector3d(axeZx[a][0], axeZy[a][0], axeZz[a][0], 
						                   axeZx[a][1], axeZy[a][1], axeZz[a][1]
                                          ).distance(cam.getEye());
			else
				distAxeZ[a] = Double.MAX_VALUE;
		}
		
		for(int a=0; a<na; a++){
			if(distAxeZ[a] < Double.MAX_VALUE){
				Coord3d axeCenter = new Coord3d((axeZx[a][0]+axeZx[a][1])/2, 
						                        (axeZy[a][0]+axeZy[a][1])/2, 
						                        (axeZz[a][0]+axeZz[a][1])/2 );
				if(rightSide) {
					if (!cam.side(axeCenter))
						distAxeZ[a] *= -1;
				} else {
					if (cam.side(axeCenter))
						distAxeZ[a] *= -1;
				}
			}
		}
		
		return min(distAxeZ);
    }
	
	/******************************************************************/
	/**                    DRAW AXEBOX ELEMENTS                      **/
	
	/**
	 * Make all GL2 calls allowing to build a cube with 6 separate quads.
	 * Each quad is indexed from 0.0f to 5.0f using glPassThrough,
	 * and may be traced in feedback mode when mode=GL2.GL_FEEDBACK 
	 */
	protected void drawCube(GL2 gl, int mode){
		for(int q=0; q<6; q++){
			if (q == 4 || q == 5)
				continue;
			if(mode==GL2.GL_FEEDBACK)
				gl.glPassThrough((float)q);
			gl.glBegin(GL2.GL_QUADS);
				for(int v=0; v<4; v++){
					gl.glVertex3f( quadx[q][v], quady[q][v], quadz[q][v]);
				}
			gl.glEnd();
		}
	}
	
	@Override
	public void setAxe(BoundingBox3d bbox){
		this.boxBounds = bbox;
		setAxeBox(bbox.getXmin(), bbox.getXmax(), 
				  bbox.getYmin(), bbox.getYmax(), 
				  bbox.getZmin(), bbox.getZmax());
	}	

}
