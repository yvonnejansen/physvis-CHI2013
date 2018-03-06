package fr.inria.aviz.physVizEval.jzy3d;

import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.Hashtable;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.jzy3d.colors.Color;

import fr.inria.aviz.physVizEval.barchart3d.BarChart3D;

public class UglyPickingSupport {

    public static boolean PICKING = false;
    public static boolean PICKING_REQUESTED = false;
    
    static int maxObjects = 10;
    private static Hashtable<Object, CustomColor> objectColors = new Hashtable<Object, CustomColor>();    
    static int mousex, mousey;
    
    public static CustomColor objectToColor(Object o) {
    	if (!objectColors.containsKey(o))
        	objectColors.put(o, newColor());
    	return objectColors.get(o);
    }
    
    public static Object colorToObject(CustomColor c) {
    	for (Object o : objectColors.keySet())
    		if (objectColors.get(o).equals(c))
    			return o;
    	return null;
    }

    private static CustomColor newColor() {
    	int count = objectColors.size();
    	int i1 = count / maxObjects;
    	int i2 = count % maxObjects;//count - count / maxObjects;
    	CustomColor c = new CustomColor(111, 11 + i1 * maxObjects, 11 + i2 * maxObjects, 255);
  		if (objectColors.containsKey(c))
  			System.err.println("*** Picking color already exists");
    	return c; 
    }
    
    public static void setMousePosition(int x, int y) {
    	mousex = x;
    	mousey = y;
    }
    
    private static ByteBuffer pixelsRGB = null;
    private static int pixelsRGBSize = 0;
    
    public static void beginPick(GL2 gl) {
		PICKING = true;
    }
    
    public static void endPick(GL2 gl) {
    	// Read Frame back into our ByteBuffer.
    	// Create a ByteBuffer to hold the frame data.
    	int bufferSize = 1 * 1 * 3;
    	if (pixelsRGB == null || pixelsRGBSize != bufferSize)
    		pixelsRGB = ByteBuffer.allocateDirect(bufferSize); 
        gl.glReadBuffer(GL.GL_BACK);
    	gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
    	gl.glReadPixels(mousex, mousey, 1, 1, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixelsRGB);
    	
    	int r = (int)pixelsRGB.get(0) & 0xff;
    	int g = (int)pixelsRGB.get(1) & 0xff;
    	int b = (int)pixelsRGB.get(2) & 0xff;
    	CustomColor c = new CustomColor(r, g, b, 255);
    	
    	
    	Point o = (Point)colorToObject(c);
//    	if (o == null)
//    		System.err.println(r + ", " + g + "," + b + " -> " + o);
//    	else
//    		System.err.println(r + ", " + g + "," + b + " -> " + o.x + "," + o.y);
    	
    	if (o != null) {
    		BarChartBar bar = BarChart3D.getBar(o.x, o.y);
    		bar.toggleSelect();
    	}
    	
		PICKING = false;
		PICKING_REQUESTED = false;
    }
    
}
