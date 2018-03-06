package fr.inria.aviz.physVizEval.jzy3d;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.text.align.Halign;
import org.jzy3d.plot3d.text.align.Valign;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

import com.jogamp.opengl.util.gl2.GLUT;

public class CustomTextBitMapRenderer extends org.jzy3d.plot3d.text.renderers.TextBitmapRenderer {

	public CustomTextBitMapRenderer() {
		super();
		font = GLUT.BITMAP_HELVETICA_18;
		fontHeight = 18;
	}
	
	public BoundingBox3d drawText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, float halign, float valign, Color color) {
		return drawText(gl, glu, cam, s, position, halign, valign, color, defScreenOffset, defSceneOffset);	
	}

	/** Draw a string at the specified position and compute the 3d volume occupied by the string 
	 * according to the current Camera configuration.
	 * 
	 * align = -1: left, 0: center, 1: right.
	 * 
	 * */
	public BoundingBox3d drawText(GL2 gl, GLU glu, Camera cam, String s, Coord3d position, float halign, float valign, Color color, Coord2d screenOffset, Coord3d sceneOffset){
		gl.glColor3f(color.r, color.g, color.b);
		
		Coord3d posScreen = cam.modelToScreen(gl, glu, position);
		
		//System.out.println(posScreen);
		
		// compute a corrected position according to layout
        float  strlen = glut.glutBitmapLength(font, s);
		float  x      = 0.0f;
        float  y      = 0.0f;
        
//        if(halign==Halign.RIGHT)
//        	x = posScreen.x;
//        else if(halign==Halign.CENTER)
//			x = posScreen.x - strlen/2;
//        else if(halign==Halign.LEFT)
//			x = posScreen.x - strlen;

        x = posScreen.x + (strlen / 2) * (halign - 1);
        
//        if(valign==Valign.TOP)
//			y = posScreen.y;
//        else if(valign==Valign.GROUND)
//        	y = posScreen.y;
//        else if(valign==Valign.CENTER)
//			y = posScreen.y - fontHeight/2;
//        else if(valign==Valign.BOTTOM)
//			y = posScreen.y - fontHeight;
//        
        y = posScreen.y + fontHeight / 2 * (valign - 1);

        
        Coord3d posScreenShifted = new Coord3d(x+screenOffset.x, y+screenOffset.y, posScreen.z);
        Coord3d posReal;
        
        try{
        	posReal = cam.screenToModel(gl, glu, posScreenShifted);
        }
        catch(RuntimeException e){ // TODO: really solve this bug due to a Camera.PERSPECTIVE mode.
        	System.err.println("TextBitmap.drawText(): could not process text position: " + posScreen + " " + posScreenShifted);
        	return new BoundingBox3d();
        }
        
        // Draws actual string
        gl.glRasterPos3f(posReal.x+sceneOffset.x, posReal.y+sceneOffset.y, posReal.z+sceneOffset.z);
        glut.glutBitmapString(font, s);

        // Compute bounds of text
		Coord3d botLeft   = new Coord3d();
		Coord3d topRight  = new Coord3d();		
		botLeft.x  = posScreenShifted.x;
		botLeft.y  = posScreenShifted.y;
		botLeft.z  = posScreenShifted.z;		
		topRight.x = botLeft.x + strlen;
		topRight.y = botLeft.y + fontHeight;
		topRight.z = botLeft.z;		
		
		BoundingBox3d txtBounds = new BoundingBox3d();
		txtBounds.add( cam.screenToModel(gl, glu, botLeft) );
		txtBounds.add( cam.screenToModel(gl, glu, topRight) );		
		return txtBounds;
	}
	
	public static float halignToFloat(Halign align) {
		switch (align) {
		case LEFT:
			return -1;
		case CENTER:
			return 0;
		case RIGHT:
			return 1;
		}
		return 0;
	}
	
	public static float valignToFloat(Valign align) {
		switch (align) {
		case TOP:
			return -1;
		case CENTER:
			return 0;
		case BOTTOM:
			return 1;
		}
		return 0;
	}
	
}
