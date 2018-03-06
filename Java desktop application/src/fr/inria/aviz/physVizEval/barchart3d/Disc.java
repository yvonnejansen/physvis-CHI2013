package fr.inria.aviz.physVizEval.barchart3d;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Cylinder;
import org.jzy3d.plot3d.rendering.view.Camera;

class Disc {
	double x, y, z, h;
	double w;
	  Country country;
	  Cylinder disc;
	  
	  public Disc (Country country, double x, double y, double z, double w, double h) {
	    this.country = country;
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.w = Math.sqrt(w/(4*Math.PI));;
	    this.h = h;
	    disc = new Cylinder();
	    disc.setData(new Coord3d(x,y,z), (float)h, (float)w/2, 30, 10, country.col);
	  }
	  
	  void draw(GL2 gl, GLU glu, Camera cam) {
//	    pushMatrix();
//	    translate(x, y, z);
//	    fill(country.col);
//	    noStroke();
//	    disc.draw();
//	    popMatrix();
		  disc.draw(gl, glu, cam);
	  }
	}
