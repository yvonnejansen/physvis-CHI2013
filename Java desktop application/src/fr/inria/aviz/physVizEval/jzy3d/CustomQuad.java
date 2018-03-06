package fr.inria.aviz.physVizEval.jzy3d;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.rendering.view.Camera;

public class CustomQuad extends Quad {
	
	Color pickingColor;
	
	public void setPickingColor(Color c) {
		this.pickingColor = c;
	}
	
	@Override
	public void draw(GL2 gl, GLU glu, Camera cam){
		// Execute transformation
		if(transform!=null)
			transform.execute(gl);
//		wfstatus = false;		
		// Draw content of polygon
		// Draw edge of polygon
		if(facestatus){
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
//			gl.glColorMaterial(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
//			gl.glEnable(GL2.GL_COLOR_MATERIAL);
			if(wfstatus){
				gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
				gl.glPolygonOffset(1f, 1f);
			}
			gl.glBegin(GL2.GL_QUADS); // <<<
			for(Point p: points){
				if (!UglyPickingSupport.PICKING) {
					if(mapper!=null){
						Color c = mapper.getColor(p.xyz); // TODO: should store result in the point color
						gl.glColor4f(c.r, c.g, c.b, c.a);
						//System.out.println(c);
					}
					else
						gl.glColor4f(p.rgb.r, p.rgb.g, p.rgb.b, p.rgb.a);
				} else {
					gl.glColor4f(pickingColor.r, pickingColor.g, pickingColor.b, pickingColor.a);
				}
				gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
			}
			gl.glEnd();
			if(wfstatus)
				gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
			
//			gl.glDisable(GL2.GL_COLOR_MATERIAL);
		}
		
		if(wfstatus && !UglyPickingSupport.PICKING){
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
			gl.glDepthMask(false);
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
			gl.glEnable(GL2.GL_LINE_SMOOTH);
			gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
//			gl.glEnable(GL2.GL_POLYGON_OFFSET_LINE);
			gl.glPolygonOffset(1f, 1f);
			
			if (!UglyPickingSupport.PICKING)
				gl.glColor4f(wfcolor.r, wfcolor.g, wfcolor.b, wfcolor.a);//wfcolor.a);
			else
				gl.glColor4f(pickingColor.r, pickingColor.g, pickingColor.b, pickingColor.a);

			gl.glLineWidth(wfwidth);

			gl.glBegin(GL2.GL_QUADS);
			for(Point p: points){
				gl.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
			}
			gl.glEnd();
			
			gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);
			gl.glDepthMask(true);
//			gl.glDisable(GL2.GL_BLEND);
//			gl.glDisable(GL2.GL_LINE_SMOOTH);
		}

		/*Point b = new Point(getBarycentre(), Color.BLUE);
		b.setWidth(5);
		b.draw(gl,glu,cam);*/
	}

}
