package fr.inria.aviz.physVizEval.barchart3d;

import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.AbstractComposite;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.view.Camera;

public class Box extends AbstractComposite {

	private double width, length, height;
	private Coord3d position;
	private Color color;
	private Shape shape;
	private BoundingBox3d boundingBox;
	
	public Box(Coord3d _position, double xRange, double yRange, double zRange, Color _color) {
		super();
		this.boundingBox = new BoundingBox3d(_position, (float) xRange).scale(new Coord3d(1, yRange/xRange, zRange/xRange));
		this.position = _position;
		this.color = _color;
		shape = getBox();
		add(shape);
	}
	
	public Box(BoundingBox3d bb, Color col) {
		this.position = bb.getCenter();
		this.boundingBox = bb;
		this.color = col;
		shape = getBox();
		add(shape);
		
	}
	
	private Shape getBox() {
		List<Coord3d> v = boundingBox.getVertices();
		List<Polygon> ps = new LinkedList<Polygon>();
		ps.add(getQuad(v.get(3), v.get(2), v.get(1), v.get(0)));
		ps.add(getQuad(v.get(0), v.get(4), v.get(5), v.get(1)));
		ps.add(getQuad(v.get(0), v.get(3), v.get(7), v.get(4)));
		ps.add(getQuad(v.get(2), v.get(3), v.get(7), v.get(6)));
		ps.add(getQuad(v.get(5), v.get(4), v.get(7), v.get(6)));
		ps.add(getQuad(v.get(5), v.get(1), v.get(2), v.get(6)));
		
		return new Shape(ps);
	}

	private Quad getQuad(Coord3d p1, Coord3d p2, Coord3d p3, Coord3d p4) {
		Quad q = new Quad();
		q.add(new Point(p1));
		q.add(new Point(p2));
		q.add(new Point(p3));
		q.add(new Point(p4));
		q.setColor(color);
		q.setWireframeColor(Color.GRAY);
		q.setWireframeWidth(0.5f);
		q.setWireframeDisplayed(true);
		return q;
	}
	
	public void draw(GL2 gl, GLU glu, Camera camera){
		super.draw(gl,  glu, camera);
	}
}
