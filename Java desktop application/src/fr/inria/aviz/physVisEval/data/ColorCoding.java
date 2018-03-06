package fr.inria.aviz.physVisEval.data;

import java.awt.Color;


/**
 * Color coding info
 * 
 * @author dragice
 *
 */
public interface ColorCoding {

	// Color brewer palette for 10 categorical items
	public static Color[] PALETTE_COLORBREWER_10 = {
		new Color(51, 160, 44), // dark green
		new Color(178, 223, 138), // light green
		new Color(31, 120, 180), // dark blue
		new Color(166, 206, 227), // light blue
		new Color(106, 61, 154), // dark purple
		new Color(202, 178, 214), // light purple
		new Color(227, 26, 28), // red
		new Color(251, 154, 153), // rose
		new Color(255, 127, 0), // orange
		new Color(253, 191, 111) }; // yellow
	
	public Color getColor(MatrixData data, int row, int column);
	
	public ColorCoding getInverse();
	
	public ColorCoding extractRow(int row);

	public ColorCoding extractColumn(int column);

}
