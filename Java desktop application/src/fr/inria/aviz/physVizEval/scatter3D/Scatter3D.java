package fr.inria.aviz.physVizEval.scatter3D;

import java.awt.Dimension;

import java.awt.Frame;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.SwingUtilities;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartView;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
//import org.jzy3d.demos.histogram.barchart.CustomKeyboardControl;
//import org.jzy3d.demos.histogram.barchart.CustomLegendRenderer;
//import org.jzy3d.demos.histogram.barchart.SVGKeyboardSaver;
import org.jzy3d.factories.AxeFactory;
import org.jzy3d.factories.CameraFactory;
import org.jzy3d.factories.JzyFactories;
import org.jzy3d.factories.OrderingStrategyFactory;
import org.jzy3d.factories.ViewFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IntegerCoord2d;
import org.jzy3d.maths.Range;
import org.jzy3d.picking.IObjectPickedListener;
import org.jzy3d.picking.PickingSupport;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.HistogramBar;
import org.jzy3d.plot3d.primitives.Parallelepiped;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.lights.LightSet;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.DefaultOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.PointOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.controllers.ViewMouseController;
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.ui.ChartLauncher;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.ChartMouseController;
import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.layout.AxeBoxLayout;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.ITickRenderer;
import org.jzy3d.plot3d.primitives.axes.layout.renderers.TickLabelMap;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer2d;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;
import org.jzy3d.plot3d.text.ITextRenderer;
import org.jzy3d.plot3d.text.overlay.TextOverlay;
import org.jzy3d.plot3d.text.renderers.TextBillboardRenderer;
import org.openmali.vecmath2.Quaternion4f;

import com.jogamp.graph.math.Quaternion;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVisEval.logs.Utils;
import fr.inria.aviz.physVisEval.logs.kinematics.Kinematics.RotationEvent;
import fr.inria.aviz.physVizEval.jzy3d.BarChartBar;
import fr.inria.aviz.physVizEval.jzy3d.CustomAxeBox;
import fr.inria.aviz.physVizEval.jzy3d.CustomCamera;
import fr.inria.aviz.physVizEval.jzy3d.CustomColor;
import fr.inria.aviz.physVizEval.jzy3d.CustomFrameAWT;
import fr.inria.aviz.physVizEval.jzy3d.CustomKeyboardControl;
import fr.inria.aviz.physVizEval.jzy3d.CustomLight;
import fr.inria.aviz.physVizEval.jzy3d.CustomMouseControl;
import fr.inria.aviz.physVizEval.jzy3d.CustomTextBitMapRenderer;
import fr.inria.aviz.physVizEval.jzy3d.CustomView;
import fr.inria.aviz.physVizEval.jzy3d.ModelRotation;
import fr.inria.aviz.physVizEval.jzy3d.MonoView;
import fr.inria.aviz.physVizEval.jzy3d.UglyPickingSupport;
import fr.inria.aviz.physVizEval.jzy3d.stereo.StereoCamera;
import fr.inria.aviz.physVizEval.jzy3d.stereo.StereoView;
import fr.inria.aviz.physVizEval.stereotest.JStereoComponent.StereoRenderingMethod;
import fr.inria.aviz.physVizEval.stereotest.StereoCursor;
import fr.inria.aviz.physVizEval.util.CSV;
import fr.inria.aviz.physVizEval.util.GUIUtils;
import fr.inria.aviz.physVizEval.util.GUIUtils.AdvancedKeyListener;




public class Scatter3D {

	
    private static CustomFrameAWT fr = null;
    private static Chart chart;
//    private static StereoCursor stereoCursor;

    private static MatrixData data;
    private Scatter scatter;
         
    
    public static ModelRotation modelRotation = new ModelRotation();

    
    public static void main(String[] args) throws Exception
    {
//    	init ();
    	String inputDir = "./logs/exp2";
		File[] logFiles = Utils.choseFiles(inputDir, ".csv");
        chart = new Chart(Quality.Advanced, "swing");
//        ViewMouseController mouse = new ViewMouseController();
//        chart.getCanvas().addMouseListener(mouse);
//        ((CustomView) chart.getView()).setModelRotation(modelRotation);

		for (File f : logFiles)
    	{
    		Color col = Color.random();
			addData(new MatrixData(f.getAbsolutePath(), null), col);
    	}
        ChartLauncher.openChart(chart);

    }
    
    
    public static void init ()
    {
//    	JzyFactories.view = new ViewFactory() {
//    		public ChartView getInstance(Scene scene, ICanvas canvas, Quality quality){
//    			return new MonoView(scene, canvas, quality);
//
//    		}
//    	};
//        JzyFactories.camera = new CameraFactory() {
//        		public Camera getInstance(Coord3d center) {
//        			return new CustomCamera(center);
//        		}
//    	};
//    	JzyFactories.ordering = new OrderingStrategyFactory() {
//    		public AbstractOrderingStrategy getInstance() {
//    			return new DefaultOrderingStrategy();
//    		}
//    	};
    	chart = new Chart(Quality.Nicest, "swing");
//        chart.getView().setCameraMode(CameraMode.PERSPECTIVE);
		chart.getView().setBackgroundColor(new Color(0.95f, 0.95f, 0.95f));

    }
    
    public static void hide ()
    {
    	if (chart != null) chart.dispose();
    	chart = null;
    	if (fr != null) fr.dispose();

    	System.err.println("hiding scatter");
    }
    
    
    public static void addData(MatrixData dat, Color col)
    {
    	System.err.println("adding another dataset");
    	
    	data = dat;
    	
        Coord3d[] points = new Coord3d[data.rows+6];
        Color[]   colors = new Color[data.rows+6];
        float x;
        float y;
        float z;
        float a;
    	Quaternion4f q = new Quaternion4f();
    	if (dat.cols >= 9)
    	{
	        for(int i=0; i < dat.rows; i++){
	        	float[] vals = {(float) dat.getValue(i, 6), (float) dat.getValue(i, 7), (float) dat.getValue(i, 8), (float) dat.getValue(i, 9)};
	        	q.set(vals);
	        	points[i] = new Coord3d();
	        	modelRotation.setQuaternion(q);
	        	points[i].set(modelRotation.getExtrinsicAngles());
	        	points[i].normalizeTo(180);
	
	//            x = (float)Math.random() - 0.5f;
	//            y = (float)Math.random() - 0.5f;
	//            z = (float)Math.random() - 0.5f;
	//            points[i] = new Coord3d(x, y, z);
	            a = 0.25f;
	            colors[i] = col; //new Color(x, y, z, a);
	        }
    	

	    	points[data.rows] = new Coord3d(180,0,0);
	    	points[data.rows+1] = new Coord3d(-180,0,0);
	    	points[data.rows+2] = new Coord3d(0,180,0);
	    	points[data.rows+3] = new Coord3d(0,-180,0);
	    	points[data.rows+4] = new Coord3d(0,0,180);
	    	points[data.rows+5] = new Coord3d(0,0,-180);
	    	colors[data.rows] = new Color(0,0,0,1);
	    	colors[data.rows+1] = new Color(0,0,0,1);
	    	colors[data.rows+2] = new Color(0,0,0,1);
	    	colors[data.rows+3] = new Color(0,0,0,1);
	    	colors[data.rows+4] = new Color(0,0,0,1);
	    	colors[data.rows+5] = new Color(0,0,0,1);

    	
	    	System.err.println("added " + points.length + " data points");
        
	    	Scatter scatter = new Scatter(points, colors);
	    	chart.getScene().add(scatter);
    	}
    	else
    		System.err.println("no data to add");

    }

 
    // call this if only one data set is displayed -> creates new chart
    public static void display(ArrayList<RotationEvent> events)
    {
        chart = new Chart(Quality.Fastest, "swing");

        display(events, new Color(0,0,0,50));
        
        ((CustomView) chart.getView()).setModelRotation(modelRotation);
        ViewMouseController mouse = new ViewMouseController();
        chart.getCanvas().addMouseListener(mouse);

        ChartLauncher.openChart(chart);

    }
    
    // call this to add another data set with a different color (default for the first one is black with alpha 0.5)
    public static void display(ArrayList<RotationEvent> events, Color col)
    {
    	System.err.println("adding more data");
    	
    	Coord3d[] points = new Coord3d[events.size()+6];
        Color[]   colors = new Color[events.size()+6];
    	int i = 0;
    	for(RotationEvent ev : events)
    	{
    		points[i] = new Coord3d();
        	modelRotation.setQuaternion(ev.quaternion);
        	points[i].set(modelRotation.getExtrinsicAngles());
        	points[i].normalizeTo(18);
            colors[i] = col; //new Color(0,0,0,50);//Color.BLACK; //new Color(x, y, z, a);
            i++;
    	}

    	points[events.size()] = new Coord3d(18,0,0);
    	points[events.size()+1] = new Coord3d(-18,0,0);
    	points[events.size()+2] = new Coord3d(0,18,0);
    	points[events.size()+3] = new Coord3d(0,-18,0);
    	points[events.size()+4] = new Coord3d(0,0,18);
    	points[events.size()+5] = new Coord3d(0,0,-18);
    	colors[events.size()] = new Color(0,0,0,50);
    	colors[events.size()+1] = new Color(0,0,0,50);
    	colors[events.size()+2] = new Color(0,0,0,50);
    	colors[events.size()+3] = new Color(0,0,0,50);
    	colors[events.size()+4] = new Color(0,0,0,50);
    	colors[events.size()+5] = new Color(0,0,0,50);
    	
        Scatter scatter = new Scatter(points, colors);
        scatter.setWidth(3);
        
        chart.getScene().add(scatter);
//        chart.setAxeDisplayed(false);
//        ((CustomView) chart.getView()).setCameraMode(CameraMode.PERSPECTIVE);
//        
//        fr = new CustomFrameAWT(chart, new Rectangle(0, 0, 800, 600), "scatter", true);
//        fr.setVisible(true);
//        fr.toFront();

    }
    

}
