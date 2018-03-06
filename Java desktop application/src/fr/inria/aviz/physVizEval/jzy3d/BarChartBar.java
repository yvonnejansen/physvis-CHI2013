/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.inria.aviz.physVizEval.jzy3d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.picking.IPickable;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.HistogramBar;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Quad;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.view.Camera;

import fr.inria.aviz.physVizEval.barchart3d.BarChart3D;
import fr.inria.aviz.physVizEval.util.GUIUtils;

/**
 *
 * @author ao
 */
public class BarChartBar extends HistogramBar implements IPickable {

    protected float BAR_RADIUS = 2.5f;
    protected float BAR_FEAT_BUFFER_RADIUS = 2;
    protected boolean ENABLE_BAR_GRIDLINES = false;
    protected double gridSpacing;
//    private ToggleTextTooltipRenderer tr;
    private final Chart chart;
    private Shape shape;
    
    private CustomColor defaultWireFrameColor; // = (CustomColor) Color.BLACK;
    private CustomColor selectedWireFrameColor = new CustomColor(0f, 0f, 0f);
    private float defaultWireFrameWidth = 0.3f;
    private float selectedWireFrameWidth = 1.5f;
    
    // HACK! -> the view class in API does not expose GLU object!
    public GLU gluObj;
    private final String info;
    
    Color pickingColor;
    ArrayList<Quad> allQuads = new ArrayList<Quad>();
    boolean selected = false;

    public String getInfo() {
        return info;
    }

    public void toggleSelect() {
    	setSelected(!selected);
    }
    
    public void setSelected(boolean select) {
    	selected = select;
    	if (selected) {
	    	for (Quad q: allQuads) {
	    		if (q.getWireframeColor().equals(defaultWireFrameColor)) {
			        q.setWireframeColor(selectedWireFrameColor);
			        q.setWireframeWidth(selectedWireFrameWidth);
	    		}
	    	}
    	} else {
	    	for (Quad q: allQuads) {
	    		if (q.getWireframeColor().equals(selectedWireFrameColor)) {
			        q.setWireframeColor(defaultWireFrameColor);
			        q.setWireframeWidth(defaultWireFrameWidth);
	    		}
	    	}
    	}
    }
    
    public IntegerCoord2d getCenterToScreenProj() {
        Coord3d co = chart.getView().getCamera().modelToScreen(
                chart.getView().getCurrentGL(),
                gluObj,
                getBounds().getCenter());

        IntegerCoord2d c2d = new IntegerCoord2d((int) co.x, (int) chart.flip(co.y));
        return c2d;
    }

    public List<Coord2d> getBoundsToScreenProj() {
        Coord3d[] co = chart.getView().getCamera().modelToScreen(
                chart.getView().getCurrentGL(),
                gluObj,
                getShape().getBounds().getVertices().toArray(new Coord3d[]{}));
        List<Coord2d> l = new ArrayList<Coord2d>();
        for (Coord3d c3 : co) {
            l.add(new Coord2d((int) c3.x, (int) chart.flip(c3.y)));
        }
        return l;
    }

    public BarChartBar(Chart c, String info) {
        super();
        this.chart = c;
        this.info = info;
    }

    public BarChartBar(Chart chart2, String string, float width, float barSpacing, boolean gridlines, double spacing) {
    	this(chart2, string);
    	this.ENABLE_BAR_GRIDLINES = gridlines;
    	this.gridSpacing = spacing;
    	this.BAR_RADIUS = width/2f;
    	this.BAR_FEAT_BUFFER_RADIUS = barSpacing;
    }

	public Shape getShape() {
        return shape;
    }

    @Override
    public BoundingBox3d getBounds() {
        return getShape().getBounds();
    }

//    @Override
    public void draw(GL2 gl, GLU glu, Camera camera) {
   		super.draw(gl, glu, camera);
    }

    public void setData(Coord3d c, float height, Color color, Color pickingColor) {
    	this.pickingColor = pickingColor;
    	defaultWireFrameColor = new CustomColor(GUIUtils.mix(color.awt(), Color.BLACK.awt(), 0.5f));
//    	wireFrameColor = Color.BLACK.alphaSelf((float) 0);
    	shape = getBar(
                new Coord3d((c.x) * (BAR_RADIUS * 2 + BAR_FEAT_BUFFER_RADIUS),
                c.y * (BAR_RADIUS * 2 + BAR_FEAT_BUFFER_RADIUS) , c.z),
                BAR_RADIUS, height, color);
        add(shape);
    }

    private Quad getZQuad(Coord3d position, float radius, Color color) {
    	CustomQuad q = new CustomQuad();
    	q.setPickingColor(pickingColor);
    	allQuads.add(q);
        q.add(new Point(new Coord3d(position.x - radius, position.y + radius, position.z)));
        q.add(new Point(new Coord3d(position.x - radius, position.y - radius, position.z)));
        q.add(new Point(new Coord3d(position.x + radius, position.y - radius, position.z)));
        q.add(new Point(new Coord3d(position.x + radius, position.y + radius, position.z)));
        q.setColor(color);
        q.setWireframeColor(defaultWireFrameColor);
        q.setWireframeWidth(defaultWireFrameWidth);
        q.setWireframeDisplayed(true);
        return q;
    }

    private Quad getYQuad(Coord3d position, float radius, float height, Color color) {
    	CustomQuad q = new CustomQuad();
    	q.setPickingColor(pickingColor);
    	allQuads.add(q);
        q.add(new Point(new Coord3d(position.x - radius, position.y, position.z + height)));
        q.add(new Point(new Coord3d(position.x - radius, position.y, position.z)));
        q.add(new Point(new Coord3d(position.x + radius, position.y, position.z)));
        q.add(new Point(new Coord3d(position.x + radius, position.y, position.z + height)));
        q.setColor(color);
        q.setWireframeColor(defaultWireFrameColor);
//        q.setWireframeColor(Color.BLACK.clone().alphaSelf((float) 0));
        q.setWireframeWidth(defaultWireFrameWidth);
        q.setWireframeDisplayed(true);
        return q;
    }

    private Quad getXQuad(Coord3d position, float radius, float height, Color color) {
    	CustomQuad q = new CustomQuad() {

//            @Override
            public void draw(GL2 gl, GLU glu, Camera cam) {
                super.draw(gl, glu, cam);
                gluObj = glu;
            }
        };
    	q.setPickingColor(pickingColor);
    	allQuads.add(q);
        q.add(new Point(new Coord3d(position.x, position.y + radius, position.z + height)));
        q.add(new Point(new Coord3d(position.x, position.y - radius, position.z + height)));
        q.add(new Point(new Coord3d(position.x, position.y - radius, position.z)));
        q.add(new Point(new Coord3d(position.x, position.y + radius, position.z)));
        q.setColor(color);
        q.setWireframeColor(defaultWireFrameColor);
        q.setWireframeWidth(defaultWireFrameWidth);
        q.setWireframeDisplayed(true);
        return q;
    }

    private Shape getBar(Coord3d position, float radius, float height, Color color) {
        Coord3d p1 = position.clone();
        Coord3d p2 = position.clone();
        p2.z += height;
        Coord3d p3 = position.clone();
        p3.y -= radius;
        Coord3d p4 = position.clone();
        p4.y += radius;
        Coord3d p5 = position.clone();
        p5.x -= radius;
        Coord3d p6 = position.clone();
        p6.x += radius;

        List<Polygon> ps = new LinkedList<Polygon>();

        ps.add(getZQuad(p1, radius, color));
        Quad topQuad = getZQuad(p2, radius, color);
        topQuad.setWireframeDisplayed(true);
        ps.add(topQuad);

        ps.add(getYQuad(p3, radius, height, color));
        ps.add(getYQuad(p4, radius, height, color));

        ps.add(getXQuad(p5, radius, height, color));
        ps.add(getXQuad(p6, radius, height, color));
        
        if (ENABLE_BAR_GRIDLINES) {
	        double i = gridSpacing;
	        while (i < height)
	        {
	        	Coord3d grid = p1.clone();
	        	grid.z+=i;
	        	//Quad q = getZQuad(grid, radius, Color.WHITE);
	        	Color col = new CustomColor(GUIUtils.mix(color.awt(), Color.WHITE.awt(), 0.5f));
	        	Quad q = getZQuad(grid, radius, color);	        	
	        	q.setWireframeColor(col);
	        	q.setWireframeWidth(0.6f);
	        	ps.add(q);
	        	i += gridSpacing;
	        }
        }

        return new Shape(ps) {

            @Override
            public boolean isDisplayed() {
                BoundingBox3d ba = chart.getView().getAxe().getBoxBounds();
                BoundingBox3d bs = getBounds();
                return (ba.getXmax() >= bs.getXmax()) && (ba.getYmax() >= bs.getYmax())
                        && bs.getXmin() >= ba.getXmin() && bs.getYmin() >= ba.getYmin();
            }
        };
    }

    int pickingId = 0;
    
	@Override
	public void setPickingId(int id) {
		this.pickingId = id;
	}

	@Override
	public int getPickingId() {
		return pickingId;
	}

//    void setSelected(boolean selected) {
//        if (tr == null) {
//            tr = new ToggleTextTooltipRenderer(info, this);
//            chart.getView().addTooltip(tr);
//        }
//        if (selected) {
//            setWireframeWidth(3);
//            tr.setVisible(true);
//        } else {
//            setWireframeWidth(1);
//            tr.setVisible(false);
//        }
//    }
}
