package fr.inria.aviz.physVisEval.data;

import java.awt.Color;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


/**
 * A color coding scheme where each row is assigned a color.
 * 
 * @author dragice
 *
 */
@Root
public class RowColorCoding implements ColorCoding {

	private ArrayList<Color> palette = new ArrayList<Color>();
	
	@ElementList(name = "palette")
	private ArrayList<XMLColor> xmlPalette = new ArrayList<XMLColor>(); // for xml serialization
	
	/**
	 * Creates a row color coding with the specified palette.
	 */
	public RowColorCoding(Color[] palette) {
		for (Color c : palette) {
			this.palette.add(c);
			this.xmlPalette.add(new XMLColor(c)); // update this list as well for future xml serialization
		}
	}
	
	/**
	 * Constructor for XML deserialization
	 */
	private RowColorCoding(@ElementList(name = "palette") ArrayList<XMLColor> xmlPalette) {
		for (XMLColor c : xmlPalette)
			this.palette.add(c.toColor());
	}

	@Override
	public Color getColor(MatrixData data, int row, int column) {
		return palette.get(row % palette.size());
	}
	
	public ColorCoding getInverse() {
		Color[] newpalette = new Color[palette.size()];
		for (int i=0; i<palette.size(); i++)
			newpalette[i] = palette.get(i);
		return new ColumnColorCoding(newpalette);
	}
	
	public ColorCoding extractRow(int row) {
		return new RowColorCoding(new Color[]{palette.get(row % palette.size())});
	}

	public ColorCoding extractColumn(int column) {
		return this;
	}

}
