package fr.inria.aviz.physVizEval.barmodels;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class BottomPlate extends Area {
	protected double width;
	protected double height;
	protected double sliceThickness;
	protected double spacerThickness;
	protected double numSlices;
	protected double sliceWidth;
	
	public BottomPlate(double startX, double startY, double width, double height, double thickness, double sliceThickness, double spacerThickness, double numSlices)
	{
		sliceWidth = width - 2*thickness;
		width += thickness;
		height += thickness;
		Rectangle2D outerR = new Rectangle2D.Double(startX, startY, width, height);
		// the outer bottom plate
		add(new Area(outerR));
		startX += thickness;
		startY += thickness;
		// remove 4 rectangles to hold the label plates
		subtract(new Area(new Rectangle2D.Double(outerR.getCenterX()-thickness, outerR.getMinY(), 2* thickness, thickness)));
		subtract(new Area(new Rectangle2D.Double(outerR.getCenterX()-thickness, outerR.getMaxY() - thickness, 2* thickness, thickness)));
		subtract(new Area(new Rectangle2D.Double(outerR.getMinX(), outerR.getCenterY() - thickness, thickness, 2*thickness)));
		subtract(new Area(new Rectangle2D.Double(outerR.getMaxX() - thickness, outerR.getCenterY() - thickness, thickness, 2*thickness)));

		// these are the holes for the spacers and the country slices
		for (int i = 0; i < numSlices; i++) {
			// the country slices
			subtract(new Area(new Rectangle2D.Double(startX + (sliceWidth/3) , startY + thickness + i * sliceThickness + (i+1) * spacerThickness, thickness * 2, sliceThickness)));
			subtract(new Area(new Rectangle2D.Double(startX + (sliceWidth/3*2) , startY + thickness + i * sliceThickness + (i+1) * spacerThickness, thickness * 2, sliceThickness)));

			// the spacers
			subtract(new Area(new Rectangle2D.Double(startX + (sliceWidth/5) , startY + thickness + i * sliceThickness + i * spacerThickness, thickness * 2, spacerThickness)));
			subtract(new Area(new Rectangle2D.Double(startX + (sliceWidth/5*4) , startY + thickness + i * sliceThickness + i * spacerThickness, thickness * 2, spacerThickness)));
		}
		// one additional spacer
		subtract(new Area(new Rectangle2D.Double(startX + (sliceWidth/5) , startY + thickness + numSlices * sliceThickness + numSlices * spacerThickness, thickness * 2, spacerThickness)));
		subtract(new Area(new Rectangle2D.Double(startX + (sliceWidth/5*4) , startY + thickness + numSlices * sliceThickness + numSlices * spacerThickness, thickness * 2, spacerThickness)));

//		// the holes for the scales
//			// left
//		subtract(new Area(new Rectangle2D.Double(startX + (sliceWidth/3) , startY + thickness , thickness * 2, thickness)));
//		subtract(new Area(new Rectangle2D.Double(startX + (sliceWidth/3*2) , startY + thickness, thickness * 2, thickness)));
//		
//			// right
//		subtract(new Area(new Rectangle2D.Double(startX + thickness , startY + thickness + i * sliceThickness + (i+1) * spacerThickness, thickness * 2, sliceThickness)));
//		subtract(new Area(new Rectangle2D.Double(startX + thickness , startY + thickness + i * sliceThickness + (i+1) * spacerThickness, thickness * 2, sliceThickness)));
//
	}
}
