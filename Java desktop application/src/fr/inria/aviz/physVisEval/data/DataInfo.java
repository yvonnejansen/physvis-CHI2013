package fr.inria.aviz.physVisEval.data;

import java.io.File;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Metadata about a csv file. Contains title, axis labeling (w/t units) and color coding scheme.
 * 
 * @author dragice
 *
 */
@Root
public class DataInfo {

	@Element
	String title;
	
	@Element
	AxisLabeling axisLabeling = null;
	
	@Element
	ColorCoding colorCoding = null;
	
	public DataInfo(String title, AxisLabeling axisLabeling, ColorCoding colorCoding) {
		this.title = title;
		this.axisLabeling = axisLabeling;
		this.colorCoding = colorCoding;
	}
	
	public DataInfo(DataInfo info) {
		this(info.title, new AxisLabeling(info.axisLabeling), info.colorCoding);
	}

	// Constructor needed for XML deserialization
	private DataInfo() {
		
	}

	
	/**
	 * The data description that will be used for the visualization's title.
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * The axis title, that will be used for the visualization's title.
	 * @return
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	public AxisLabeling getAxisLabeling() {
		return axisLabeling;
	}

	public void setAxisLabeling(AxisLabeling axisInfo) {
		this.axisLabeling = axisInfo;
	}
	
	public ColorCoding getColorCoding() {
		return colorCoding;
	}

	public void setColorCoding(ColorCoding colorCoding) {
		this.colorCoding = colorCoding;
	}
	
	public static DataInfo load(String filename, DataInfo defaultDataInfo) {
		Serializer serializer = new Persister();
		File f = new File(filename);
		try {
			return serializer.read(DataInfo.class, f);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		if (defaultDataInfo == null) {
			return null;
		}
		return defaultDataInfo;
	}
	
	public void write(String filename) {
		Serializer serializer = new Persister();
		File f = new File(filename);
		try {
			serializer.write(this, f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// test
	public static void main(String[] args) {

		AxisLabeling axis = new AxisLabeling(0, 15.6, 10, 1, "Kilometers");
		//axis.setUnit("Kilometers");
		DataInfo info = new DataInfo("Title of the vis", axis, new RowColorCoding(ColorCoding.PALETTE_COLORBREWER_10));

		// save
		info.write("example.xml");

		// load
		try {
			DataInfo example2 = DataInfo.load("example.xml", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
