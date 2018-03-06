package fr.inria.aviz.physVizEval.barchart3d;

import java.awt.Dimension;

import java.awt.Frame;
import java.awt.Rectangle;

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
import org.jzy3d.plot3d.rendering.view.modes.CameraMode;
import org.jzy3d.ui.ChartLauncher;

import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

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

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
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
import fr.inria.aviz.physVizEval.util.Logger;
import fr.inria.aviz.physVizEval.util.Serial;

public class BarChart3D {

	// HP Fullscreen
	static Dimension WINDOW_SIZE = new Dimension(1920, 1080);
	
	// Mac Fullscreen
//	static Dimension WINDOW_SIZE = new Dimension(1440, 900);
	
	// Small window (for debugging) 
//	static Dimension WINDOW_SIZE = new Dimension(800, 600);
	
	// even smaller window (for debugging) 
//	static Dimension WINDOW_SIZE = new Dimension(600, 600);

	
	// For debugging. Turn this off before an experiment
	public final static boolean ALLOW_SPECIAL_ROTATIONS = true;
	
    protected static boolean ENABLE_STEREO = false;
    protected static boolean ENABLE_SERIAL = true;
    protected static boolean ENABLE_VIRTUAL = true;
    protected static boolean ENABLE_PERSPECTIVE = true;
    protected static boolean ENABLE_COLORBAR = false;
    protected static boolean ENABLE_BAR_GRIDLINES = true;
    protected static boolean ROTATE_ON_MOUSE_MOVE = true;
    protected static boolean WINDOW_DECORATIONS = false;
    
    static final float BASE_THICKNESS = 0.05f;//2.125f;
    static final float BASE_EDGE_WIDTH = 0.5f;
    
//    private LabeledMouseSelector mouseSelection;
//    private CustomMouseControl mouseCamera;
    private static CustomFrameAWT fr = null;
    private static Chart monoChart = null;
    private static Chart stereoChart = null;
    private static CustomMouseControl mouseCamera;
    private static Chart chart;
//    private static StereoCursor stereoCursor;

    private static MatrixData data;
    private static BarChartBar[][] bars;
    
    private int chartHeight = 72;
    private int baseHeight = 25;
    private static int barWidth = 5;
    private static int barSpacing = 2;
    
    private static Serial serial;
    private static boolean serialConnected;
     
    
    public static ModelRotation modelRotation = new ModelRotation();

    /**
	 * @param args
	 */
    public static void main(String[] args) throws Exception {


    	init(ENABLE_STEREO);
    	//display("data/generated/co2percapita-10x10.csv", ENABLE_STEREO);
    	display("data/datasets/hiv.csv", ENABLE_VIRTUAL, ENABLE_SERIAL);
    	
    	
//        display("data/datasets/army.csv", ENABLE_STEREO);
//    	display("data/datasets/army.csv", ENABLE_STEREO);
//    	display("data/datasets/tax.csv", ENABLE_STEREO);
    	//display("data/generated/agriculturalland-10x10.csv", ENABLE_STEREO);
    }
	
    public static void init(boolean stereo)
    {
        if (!serialConnected && ENABLE_SERIAL)
    	{
        	serial = Serial.getInstance();
        	serialConnected = serial.initialize();
    	}
        
        CustomMouseControl.ROTATE_ON_MOUSE_MOVE = ROTATE_ON_MOUSE_MOVE;

    	if (ALLOW_SPECIAL_ROTATIONS)
    		GUIUtils.activateKeyStates();
    	GUIUtils.addAdvancedKeyListener(null, new AdvancedKeyListener() {
			
			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_C)
					Serial.getInstance().calibrate();
			}
			
			@Override
			public void keyRepeated(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressedOnce(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
		}, false);

    	
        
    	if (!stereo)
    	{
        	JzyFactories.view = new ViewFactory() {
        		public ChartView getInstance(Scene scene, ICanvas canvas, Quality quality){
        			return new MonoView(scene, canvas, quality);
 
        		}
        	};
            JzyFactories.camera = new CameraFactory() {
            		public Camera getInstance(Coord3d center) {
            			return new CustomCamera(center);
            		}
        	};
//        	JzyFactories.ordering = new OrderingStrategyFactory() {
//        		public AbstractOrderingStrategy getInstance() {
//        			return new DefaultOrderingStrategy();
//        		}
//        	};
        	monoChart = new Chart(Quality.Nicest, "awt");
            monoChart.getView().setCameraMode(CameraMode.PERSPECTIVE);
    		monoChart.getView().setBackgroundColor(new Color(0.95f, 0.95f, 0.95f));

        }
        else
        {
        	JzyFactories.view = new ViewFactory() {
        		public ChartView getInstance(Scene scene, ICanvas canvas, Quality quality){
//                    GLCapabilities cap = new GLCapabilities(GLProfile.getDefault());
//                    cap.setStereo(true);
//                    cap.setDoubleBuffered(true);
//                    CustomCanvasAWT c = new CustomCanvasAWT(scene, quality, cap, false, false);
        			return new StereoView(scene, canvas, quality);
        		}
        	};
        	JzyFactories.camera = new CameraFactory() {
        		public Camera getInstance(Coord3d center) {
        			return new StereoCamera(center);
        		}
        	};
            GLCapabilities cap = new GLCapabilities(GLProfile.getDefault());
            cap.setStereo(true);
            cap.setDoubleBuffered(true);
            stereoChart = new Chart(Quality.Nicest, "awt", cap);
    		stereoChart.getView().setBackgroundColor(new Color(0.95f, 0.95f, 0.95f));
    		
    		

        }

    	
        {
        }
    	
//      if (ENABLE_STEREO) {
//            SettingsPanel panel = new SettingsPanel(stereoChart, new Rectangle(1200, 0, 300, 300), "Stereo Camera Settings");
//            }

    }
    /**
     * creates a barchart with a sample data set for debugging purposes
     * 
     */
    public BarChart3D()
    {
    	init(true);
//    	display("data/generated/army_percent.csv-10x10.csv", ENABLE_STEREO);
		
 
    }
//    public BarChart3D (String file, boolean stereo) {
//    
//    {
//    	new BarChart3D(file, stereo);
//    }
    
    @Override
    protected void finalize () throws Throwable
    {
    	if (serialConnected)
    		serial.close();
    }
    
    public static void hide ()
    {
//    	((CustomView)monoChart.getView()).render = false;
    	try {
    		if (serial != null)
    			serial.setView(null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if (monoChart != null) monoChart.dispose();
//    	((CustomView)stereoChart.getView()).render = false;
    	if (stereoChart != null) stereoChart.dispose();
    	monoChart = null;
    	stereoChart = null;
    	if (fr != null) fr.dispose();
    	
    }
    
    public static void display(MatrixData dat, boolean stereo)
    {
    	data = dat;
		if (data.getDataInfo() == null) {
			DataInfo defaultInfo = new DataInfo("No metadata file", new AxisLabeling(data.computeMin(), data.computeMax(), 0, 1, ""), null);
			data.setDataInfo(defaultInfo);
		}
		display(stereo);
    }
    
    
    public static void display(String file, boolean virtual, boolean prop, Logger l)
    {
    	if (virtual)
    		display(file, false);
    	
    	if (ENABLE_SERIAL && serial.isConnected())
        	serial.setModelRotation(modelRotation);

    	if (virtual && !prop)
    	{	
    		((CustomView)monoChart.getView()).setRotationListener(l);
    		serial.setModelRotation(null);
    	}
    	else
    	{	
    		if (!serial.isConnected())
    			serial.initialize();
    		serial.setLogger(l);
        	serial.setModelRotation(modelRotation);
    	}
    }
    
    
    public static void display(String file, boolean virtual, boolean prop)
    {
    	if (virtual)
    		display(file, false);
    	if (ENABLE_SERIAL && serial.isConnected())
        	serial.setModelRotation(modelRotation);

//    	if (prop && serialConnected)
//    		{
//    			try {
//					serial.setView((CustomView) monoChart.getView());
//		        	serial.setModelRotation(modelRotation);
//	    			((CustomView) monoChart.getView()).setSerialConnected(serialConnected);
//	    			System.out.println("using serial");
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//    		}
//    	if (!virtual && ! serialConnected)
//    		System.err.println("mode error: no virtual model selected and no serial connection found!");
    }
    
    public static void display (String file, boolean stereo)
    {
    	data = new MatrixData(file, null);
    	display(stereo);
    }
    
    private static void display (boolean stereo)
    {    
    	
//    	data = new MatrixData(file, null);
		if (data.getDataInfo() == null) {
			DataInfo defaultInfo = new DataInfo("No metadata file", new AxisLabeling(data.computeMin(), data.computeMax(), 0, 1, ""), null);
			data.setDataInfo(defaultInfo);
		}
		data = data.getNormalizedCopy(10); // This will ensure a consistent scene
		
        if (! stereo && monoChart == null)
        	init(stereo);
        	
        if (stereo && stereoChart == null)
        	init(stereo);

        Scene scene;
        if (stereo) {
        	scene = stereoChart.getScene();
        	chart = stereoChart;
        }
        else {
        	scene = monoChart.getScene();
        	chart = monoChart;
        }
    	CustomView view = (CustomView)chart.getView();
        view.render = false;
        int numRows = data.rows;
        int numCols = data.cols;
        bars = new BarChartBar[numRows][numCols];
               
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
        		double height = data.getValue(row, col);
        		BarChartBar bar = addBar(row, col, height);
//        		org.jzy3d.plot3d.transform.Transform t = new org.jzy3d.plot3d.transform.Transform();
//        		t.add(new org.jzy3d.plot3d.transform.Rotate(Math.PI/2, new Coord3d(1, 0, 1)));
//        		bar.setTransform(t);
        		scene.add(bar);
        		bars[row][col] = bar;
        		//view.getPickingSupport().registerPickableObject(bar, new int[] {row, col});
            }
        }
        
        // force the bounding box to extend to the max tick value by adding an invisible point.
        double maxValue = data.getAxisMax();
        scene.add(new Point(new Coord3d(0, 0, maxValue), new Color(new java.awt.Color(1f, 1f, 1f, 0f))));

		
		// -- Add bottom base
		//Box base = new Box(new BoundingBox3d(0, numRows, 0, numCols, 0, 100));
        BoundingBox3d bbox = view.getBounds();
		Box base = new Box(new BoundingBox3d(bbox.getXmin() - BASE_EDGE_WIDTH, bbox.getXmax() + BASE_EDGE_WIDTH, bbox.getYmin() - BASE_EDGE_WIDTH, bbox.getYmax() + BASE_EDGE_WIDTH, bbox.getZmin() - BASE_THICKNESS, bbox.getZmin() - 0.01f), Color.WHITE);
		scene.add(base);
		
		// -- Add axes
		BoundingBox3d axisbox = bbox;//new BoundingBox3d(bbox.getXmin(), bbox.getXmax(), bbox.getYmin(), bbox.getYmax(), bbox.getZmin() + BASE_THICKNESS/2, bbox.getZmax());
		AxeBox axe = new CustomAxeBox(axisbox, new BarChartAxeBoxLayout(data.getRowLabels(), data.getColumnLabels(), barWidth + barSpacing, barWidth + barSpacing, data.getDataInfo().getAxisLabeling().getTicks()));
		axe.setView(view);
//		axe.setExperimentalTextOverlayRenderer(null);
		axe.setTextRenderer(new CustomTextBitMapRenderer());
		view.setAxe(axe);
		       
		float dist = 100f;
        Light topLight = new CustomLight(0, true, false);
        topLight.setEnabled(true);
        topLight.setAmbiantColor(new Color(0.6f, 0.6f, 0.6f));
        topLight.setDiffuseColor(new Color(0.9f, 0.9f, 0.9f));
        topLight.setSpecularColor(new Color(0f, 0f, 0f));
        topLight.setPosition(new Coord3d(-20 * dist, 50 * dist, 6 * maxValue * dist));
        scene.getLightSet().add(topLight);

        view.setModelRotation(modelRotation);
        
        setupMouseNavigation();
        setupKeyboardNavigation();
        
//        view.getPickingSupport().addObjectPickedListener(new IObjectPickedListener() {
//			@Override
//			public void objectPicked(List<? extends Object> vertex) {
//				System.err.println(vertex.size() + " objects picked.");
//			}
//		});
        
        Coord3d viewCenter = view.getBounds().getCenter();
        view.getCamera().setTarget(viewCenter);
        view.getCamera().setEye(new Coord3d(Math.PI/3,Math.PI/5,500).cartesian().add(viewCenter));
        view.setCameraMode(CameraMode.PERSPECTIVE);

        String title = data.getDataInfo().getTitle();
        fr = stereo ? new CustomFrameAWT(stereoChart, new Rectangle(0, 0, WINDOW_SIZE.width, WINDOW_SIZE.height), title, WINDOW_DECORATIONS)
        			: new CustomFrameAWT(monoChart, new Rectangle(0, 0, WINDOW_SIZE.width, WINDOW_SIZE.height), title, WINDOW_DECORATIONS);
        fr.setVisible(true);
        fr.toFront();
        view.render = true;

        if (ROTATE_ON_MOUSE_MOVE)
        	mouseCamera.recenterMouse();

        SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				modelRotation.setExtrinsicAngles(new Coord3d(45, 0, -45));
			}
		});
        
        System.out.println(chart.getScene().getGraph().getStrategy());

//        // now tell serial about the view
//        if (serialConnected)
//        {        
//        	serial.setView(view);
//        	view.setSerialConnected(serialConnected);
//        }
		// The last constructor argument is the stereo disparity, i.e. the horizontal offset between the left and right images in pixels.
    	// Negative values are behind the screen, positive values are in front of the screen.
    	// The maximum stereo disparity on Windows is 18 pixels because Windows cannot display a mouse cursor larger than 32x32 pixels.
//		stereoCursor = new StereoCursor(fr, StereoRenderingMethod.ODD_EVEN_INTERLACED_SUBSAMPLED, 18);
//		stereoCursor.setEnabled(ENABLE_STEREO);
    }

    public static BarChartBar getBar(int row, int col) {
    	return bars[row][col];
    }
    
    private static void setupMouseNavigation() {
        mouseCamera = new CustomMouseControl(chart);
        mouseCamera.install();
    }
		
		
    private static void setupKeyboardNavigation() {
        chart.getCanvas().addKeyListener(new CustomKeyboardControl(chart));
    }
		
    public static BarChartBar addBar(int row, int col, double height) {
        CustomColor color = new CustomColor(data.getColor(row, col));
        Color pickingColor = UglyPickingSupport.objectToColor(new java.awt.Point(row, col));
        BarChartBar bar = new BarChartBar(chart, "f" + col + ",c" + row, barWidth, barSpacing, ENABLE_BAR_GRIDLINES, data.getDataInfo().getAxisLabeling().getFirstTickStep());

        bar.setData(new Coord3d(row, col, 0), (float) height, color, pickingColor);
                
//        if (!a) {
//            bar.setColorMapper(new ColorMapper(new AffinityColorGen(), 0f, 2.0f));
//            bar.setLegend(new ColorbarLegend(bar, chart.getAxeLayout()));
//            bar.setLegendDisplayed(true);
//            a = true;
//        }
//        if (colorIndex == 0) 
//        {
//        	xLabels.register(bar.getBounds().getCenter().x, data.getRowLabel(row));
//        	yLabels.register(bar.getBounds().getCenter().y, data.getColumnLabel(col));
//        }
        return bar;
    }
    
//    public AbstractDrawable addBase() {
//    	chart.
//    }

    public Chart getChart() {
        return chart;
    }
    
    public static void initForDebugMode() {
		ENABLE_VIRTUAL = true;
		ENABLE_STEREO = false;
		ENABLE_SERIAL = false;
		ROTATE_ON_MOUSE_MOVE = false;
		WINDOW_DECORATIONS = true;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		WINDOW_SIZE = new Dimension(screen.width/2, screen.height/2);
    	init(false);
    }
    
    public static void centerOnScreen() {
    	if (fr != null) {
    		GUIUtils.centerOnPrimaryScreen(fr);
    	}
    }

//
//    private void setupLegend() {
//        chart.addRenderer(new CustomLegendRenderer(chart.getCanvas()));
//    }
//
//    private void setupKeyboardNavigation() {
//        chart.getCanvas().addKeyListener(new CustomKeyboardControl(chart));
//    }
//
//    private void setupKeyboardSave() {
//        chart.getCanvas().addKeyListener(new SVGKeyboardSaver(chart));
//    }


}
