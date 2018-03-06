package fr.inria.aviz.physVizEval.barmodels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.util.SVGConstants;
import org.sittingbull.gt.util.NiceStepSizeGenerator;
import org.sittingbull.gt.util.XWilkinson;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVizEval.util.CSV;
import fr.inria.aviz.physVizEval.util.GUIUtils;
/**
 * Displays a JFrame and draws a ractangle on it using the Java 2D Graphics API
 *
 * @author www.javadb.com
 */
public class BarmodelSVGFileCreator extends javax.swing.JFrame implements MouseListener, MouseMotionListener, MouseWheelListener {
	protected final int scaling = 100;

	protected MatrixData data;
    protected double barWidth = 5;// * scaling; //3; //5;
    protected double sliceWidth = 72;// * scaling; //102; //72;
    protected double barSpacing = 2;// * scaling;
    protected double baseCoverThickness = 1.5;
//    protected double maxBarHeight = 60;
    protected double baseHeight = 22;// * scaling;
    protected Slice[] slices;
    protected Spacer[] spacers;
    protected BottomPlate bottom;
    protected Scale leftScale, rightScale;
    protected BaseLabel[] base;
    protected int numberOfTicks = 9;
    protected String[] ticks;
    protected double maxTick;
    protected double scaleHeight = 72;// * scaling;
    protected double maxTickHeight = (scaleHeight - 3);// * scaling;
	protected double scaleFactor = maxTickHeight/maxTick;//7.2 / 2.54;
	protected double thickness = 3;// * scaling;
	protected static String filename;
	protected BaseCover baseCover;
	double panx, pany, scale = 1;
	Point lastpos;
	
	protected static boolean writeFile = true;
	protected static boolean displayOnScreen = false;
	
    /**
     * Creates a new instance of Java2DFrame
     */
    public BarmodelSVGFileCreator(String _filename) {
        initComponents(_filename);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    /**
     * This is the method where the rectangle is drawn.
     *
     * @param g The graphics object
     */
    public void paint(Graphics g) {
        Graphics2D graphics2 = (Graphics2D) g;
        
        graphics2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        if (displayOnScreen) {
	        graphics2.setColor(Color.white);
	        graphics2.fill(getBounds());
	        
	        
	        graphics2.translate(panx, pany);
	        graphics2.scale(scale, scale);
        }       
//        graphics2.translate(50, 50);
//        graphics2.scale(3.5,3.5);
        
//        Rectangle2D rectangle = new Rectangle2D.Float(100, 100, 240, 160);
        graphics2.setStroke(new BasicStroke((float) 0.05));
        graphics2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 3));
        graphics2.setPaint(Color.BLACK);
        for (int i = 0; i < slices.length; i++) {
        	graphics2.draw(slices[i]);
            graphics2.drawString(slices[i].name, (int)Math.floor(slices[i].startX + sliceWidth/2 - 5), (int)Math.floor(slices[i].startY + baseHeight/2 + 3));
            graphics2.setStroke(new BasicStroke((float) 0.2));
            for (int j = 0; j < slices[i].lines.length; j ++)
            {
            	if (slices[i].lines[j] != null)
            		graphics2.draw(slices[i].lines[j]);
            }
            graphics2.setStroke(new BasicStroke((float) 0.05));

        }
//        graphics2.drawRect(100, 400, 240, 160); 
        for (int i = 0; i < spacers.length; i++) {
        	graphics2.draw(spacers[i]);
        }
        graphics2.draw(bottom);
        for (int i = 0; i < base.length; i++){
            graphics2.draw(base[i]);
//            graphics2.rotate(Math.PI/2);
            int numLabels = base[i].labels.length;
            for (int j =  0; j < numLabels; j++) {
        				GUIUtils.drawText(graphics2, base[0].labels[j], base[0].getBounds().getCenterX() + thickness/2, base[0].getBounds().y+ j*(barWidth + barSpacing) + 2* thickness + barSpacing, baseHeight - 2, Double.MAX_VALUE, GUIUtils.HALIGN.Center, GUIUtils.VALIGN.Top, 0);
        				GUIUtils.drawText(graphics2, base[2].labels[j], base[2].getBounds().getCenterX() + thickness/2, base[2].getBounds().y+ j*(barWidth + barSpacing) + thickness + barSpacing, baseHeight - 2, Double.MAX_VALUE, GUIUtils.HALIGN.Center, GUIUtils.VALIGN.Top, 0);        				
        		
        				GUIUtils.drawText(graphics2, base[1].labels[numLabels - 1 -j], base[1].getBounds().getCenterX() + thickness/2, base[1].getBounds().y+ j*(barWidth + barSpacing) + thickness + barSpacing, baseHeight - 2, Double.MAX_VALUE, GUIUtils.HALIGN.Center, GUIUtils.VALIGN.Top, 0);        			
        			
        				GUIUtils.drawText(graphics2, base[3].labels[numLabels - 1 -j], base[3].getBounds().getCenterX() + thickness/2, base[3].getBounds().y+ j*(barWidth + barSpacing) + 2 * thickness + barSpacing, baseHeight-2, Double.MAX_VALUE, GUIUtils.HALIGN.Center, GUIUtils.VALIGN.Top, 0);        			
        		
            }
//        	graphics2.rotate(0);
        }
        
        GUIUtils.drawText(graphics2, data.getDataInfo().getTitle(), base[3].getBounds().getCenterX() - 5, base[3].getBounds().getMaxY() - thickness - barSpacing, Double.MAX_VALUE, Double.MAX_VALUE, GUIUtils.HALIGN.Right, GUIUtils.VALIGN.Top, Math.PI/2);
        
        graphics2.draw(leftScale);
        graphics2.draw(rightScale);
        
        graphics2.draw(baseCover);
        
        graphics2.setStroke(new BasicStroke((float) 0.2));
//        double tickFactor = ticks[0] >= 1000 ? 0.001 : 1;

        double textMargin = 0.5;
        
        for (int i = 0; i < data.getDataInfo().getAxisLabeling().getTicks().size() - 1; i++){
        	String currentTick = data.getDataInfo().getAxisLabeling().getTicks().get(data.getDataInfo().getAxisLabeling().getTicks().size() - 1 - i).label;

        	GUIUtils.drawText(graphics2, currentTick, leftScale.getBounds().getMinX() + textMargin,  leftScale.lines[i].getY1(), Double.MAX_VALUE, Double.MAX_VALUE, GUIUtils.HALIGN.Left, GUIUtils.VALIGN.Middle, 0, null, null, 1, true);
        	GUIUtils.drawText(graphics2, currentTick, rightScale.getBounds().getMaxX() - textMargin,  rightScale.lines[i].getY2(), Double.MAX_VALUE, Double.MAX_VALUE, GUIUtils.HALIGN.Right, GUIUtils.VALIGN.Middle, 0, null, null, 1, true);
        	double textWidth = GUIUtils.getCachedMetrics(graphics2, currentTick).bounds.getWidth();
        	((Line2D.Double)leftScale.lines[i]).x1 = textWidth + textMargin * 2;
        	((Line2D.Double)rightScale.lines[i]).x2 = rightScale.getBounds().getMaxX() - textWidth - textMargin*2;
        	graphics2.draw(leftScale.lines[i]);
        	graphics2.draw(rightScale.lines[i]);

        	//graphics2.setColor(new Color(1f, 0f, 0f, 0.3f));
        	//graphics2.draw(new Line2D.Double(0, leftScale.lines[i].getY1(), getWidth(), leftScale.lines[i].getY1()));
        	//graphics2.setColor(Color.black);
        	
        	//        	AffineTransform at = graphics2.getTransform();
//
//        	graphics2.translate(leftScale.lines[i].getX1()-1, leftScale.lines[i].getY1()+1);
//        	graphics2.scale(-1, 1);
////        	graphics2.drawString(Integer.toString((int)currentTick), (float)leftScale.lines[i].getX1()-4, (float)leftScale.lines[i].getY1()+1f);
//        	graphics2.drawString(currentTick, 0, 0);
//            graphics2.setTransform(at);
//
//        	graphics2.translate(rightScale.lines[i].getX2()+4, leftScale.lines[i].getY2()+1);
//        	graphics2.scale(-1, 1);
//        	graphics2.drawString(currentTick, 0, 0);          
////        	graphics2.drawString(Integer.toString((int)currentTick), (float)rightScale.lines[i].getX2()+1, (float)leftScale.lines[i].getY2()+1f);
//            graphics2.setTransform(at);
        }
        
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code "> 
    private void initComponents(String _filename) {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridLayout(5,2));
 
        this.setBounds(new Rectangle(0,0,1000,1000));
		// Let the user chose log files to display and process
        filename = _filename;//Utils.chooseFiles("./data/datasets", ".csv")[0].getAbsolutePath();
//        filename = "./data/datasets/agriculturalland.csv";
        data = new MatrixData(filename, null);
//        double maxValue = originaldata.computeMax();
//        data = originaldata.getNormalizedCopy(maxValue * scaling);
//		data.read(filename);
//		data.read("C:\\Users\\yvonne\\Documents\\code\\tangibleVis\\java\\physVizEval\\data\\inflation.csv");

//		String[] rows = data.getColumnArray(0);
//		JList dataList = new JList(rows);
//		JScrollPane scrollPane = new JScrollPane(dataList);
//		scrollPane.setVisible(true);
//		scrollPane.setSize(300, 400);
//		getContentPane().add(scrollPane);
//		searchTickMarks();
        maxTick = data.getAxisMax();
		this.scaleFactor = maxTickHeight / maxTick;
//		System.out.println("Tick marks: " + ticks.toString() + " max val " + data.getMaxValue());
		slices = new Slice[data.rows];
		System.out.println("maxTick = " + maxTick + data + "\n" + data.getDataInfo());
		double maxY = 0;
		for (int i = 0 ; i < slices.length; i++) {
			slices[i] = new Slice(data.getDataInfo().getAxisLabeling().getTicks(), data.getRowLabel(i), data.getRow(i), this.scaleFactor, (i % 5) * (sliceWidth + barWidth), Math.floor(i/5) * (scaleHeight + baseHeight + 2* barWidth), barWidth, sliceWidth, baseHeight, barSpacing, thickness, maxTickHeight, scaleHeight, baseCoverThickness);
			maxY = Math.max(maxY, slices[i].getBounds().getMaxY());
		}
		leftScale = new Scale(data.getDataInfo().getAxisLabeling().getTicks(), 0, maxY + barWidth, sliceWidth, scaleHeight, baseHeight, 3, maxTick, maxTickHeight, true);
		rightScale = new Scale(data.getDataInfo().getAxisLabeling().getTicks(), sliceWidth + 2* barWidth, maxY + barWidth, sliceWidth, scaleHeight, baseHeight, 3, maxTick, maxTickHeight, false);
		
		spacers = new Spacer[slices.length + 1];
		for (int i = 0; i < slices.length + 1; i++) {
			spacers[i] = new Spacer(5 * (sliceWidth + barWidth), i * (baseHeight + thickness), sliceWidth, baseHeight, thickness, barSpacing, baseCoverThickness);
		}
		bottom = new BottomPlate(spacers[0].getBounds().getMaxX() + barWidth, 0, sliceWidth + 2*thickness, sliceWidth + 2*thickness, thickness, barWidth, barSpacing, slices.length);
		base = new BaseLabel[4];
		base[0] = new BaseLabel(data.getRowLabels(), rightScale.getBounds().getMaxX() + barWidth, rightScale.getBounds().getMinY(), sliceWidth + thickness, baseHeight, thickness, true);
		base[1] = new BaseLabel(data.getRowLabels(), base[0].getBounds().getMaxX() + barWidth, rightScale.getBounds().getMinY(), sliceWidth + thickness, baseHeight, thickness, false);
		base[2] = new BaseLabel(data.getColumnLabels(), base[1].getBounds().getMaxX() + barWidth, rightScale.getBounds().getMinY(), sliceWidth + thickness, baseHeight, thickness, true);
		base[3] = new BaseLabel(data.getColumnLabels(), base[2].getBounds().getMaxX() + barWidth, rightScale.getBounds().getMinY(), sliceWidth + thickness, baseHeight, thickness, false);
		
		baseCover = new BaseCover(bottom.getBounds().getMinX(), bottom.getBounds().getMaxY() + barWidth, sliceWidth, sliceWidth, thickness, 1.75, 1.75, 1.75, 1.75, data.rows, data.cols, barWidth + barSpacing - baseCoverThickness, baseCoverThickness);

		//        pack();
    }// </editor-fold> 
    
    
//    private void searchTickMarks()
//    {
//    	XWilkinson x = new XWilkinson(new NiceStepSizeGenerator());
//    	
//    	x.setLooseFlag(false);
//    	XWilkinson.Label label = x.search(0, data.getMaxValue(), numberOfTicks);
//    	ticks = new int[numberOfTicks];
//    	for (int i = 0; i < numberOfTicks; i++) {
//    		ticks[i] = (int) (label.min + i * label.step);
//    	}
//    	maxTick = label.max;
//    }
    /**
     * Starts the program
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) 
		{
        if (displayOnScreen)
    	java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BarmodelSVGFileCreator(Utils.chooseFiles("./data/datasets", ".csv")[0].getAbsolutePath()).setVisible(true);
            }
        });
		    
        if (writeFile)
        {
        // Get a DOMImplementation.
		    DOMImplementation domImpl =
		        GenericDOMImplementation.getDOMImplementation();

		    // Create an instance of org.w3c.dom.Document.
		    String svgNS = "http://www.w3.org/2000/svg";
		    
		    
		    File dir = new File("./data/datasets");
		    File[] files = dir.listFiles(new FilenameFilter() { 
                public boolean accept(File dir, String filename)
                { return filename.endsWith(".csv"); }
  } );
		    for (int i = 0; i < files.length; i++) {
		    Document document = domImpl.createDocument(svgNS, "svg", null);

	        document.getDocumentElement().setAttribute( 
	                 "width", "600mm"); 
	        document.getDocumentElement().setAttribute( 
	                 "height", "300mm"); 
	        document.getDocumentElement().setAttribute( 
	                SVGConstants.SVG_VIEW_BOX_ATTRIBUTE, "0 0 600 300"); 

		    // Create an instance of the SVG Generator.
		    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

		    // Ask the test to render into the SVG Graphics2D implementation.
		    BarmodelSVGFileCreator test = new BarmodelSVGFileCreator(files[i].getAbsolutePath());
		    test.paint(svgGenerator);
	        document.getDocumentElement().setAttribute( 
	                 "width", "600mm"); 
	        document.getDocumentElement().setAttribute( 
	                 "height", "300mm"); 
	        document.getDocumentElement().setAttribute( 
	                SVGConstants.SVG_VIEW_BOX_ATTRIBUTE, "0 0 600 300"); 

//		    System.out.println(svgGenerator.getDeviceConfiguration());

		    // Finally, stream out SVG to the standard output using
		    // UTF-8 encoding.
		    boolean useCSS = true; // we want to use CSS style attributes
		    Writer out;
		    
			try {
				String svgFileName = files[i].getAbsolutePath().substring(0, files[i].getAbsolutePath().lastIndexOf('.'));
//				String lastName = svgFileName.substring(svgFileName.lastIndexOf("\\"), svgFileName.length()-1);
				
				out = new FileWriter(new File(svgFileName + ".svg"));
				svgGenerator.stream(out, useCSS);
			} catch (SVGGraphics2DIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
   
		catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			System.out.println(" Done writing file.");
		    }
	}
		}
    
    
    

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (lastpos != null) {
			panx += arg0.getX() - lastpos.x; 
			pany += arg0.getY() - lastpos.y;
		}
		lastpos = arg0.getPoint();
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		lastpos = null;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		int rotation = arg0.getWheelRotation();
		if (rotation > 0) {
			scale *= 1.05;
		} else if (rotation < 0) {
			scale /= 1.05;	
		}
//System.err.println(scale);
		repaint();
	}
    
}





