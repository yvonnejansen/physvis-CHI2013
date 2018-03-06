package fr.inria.aviz.physVizEval.barmodels;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class BaseCover extends Area {

	public BaseCover (double startX, double startY, double width, double height, double thickness, double lMargin, double rMargin, double tMargin, double bMargin, int rows, int cols, double barWidth, double barSpacing)
	{
		add(new Area(new Rectangle2D.Double(startX, startY + height/4, thickness, 2*thickness)));
		add(new Area(new Rectangle2D.Double(startX, startY + height/4*3, thickness, 2*thickness)));
		add(new Area(new Rectangle2D.Double(startX + width/4, startY, 2 * thickness, thickness)));
		add(new Area(new Rectangle2D.Double(startX + width/4*3, startY, 2 * thickness, thickness)));
		startX += thickness;
		startY += thickness;
		add(new Area(new Rectangle2D.Double(startX, startY, width, height)));
		System.out.println("margins: " + lMargin + ", " + tMargin);
		System.out.println("draw cover at: " + startX + ", " + startY + ", " + (startX+width) + ", " + (startY+height));
		
		double newStartX = startX + lMargin;
		double newStartY = startY + tMargin;
		for (int i = 0; i < rows; i++) {
			double offsetX = newStartX + (i* (barWidth + barSpacing));
			System.out.println("x offset at: " + offsetX);
			for (int j = 0; j < cols; j++) {
				double offsetY = newStartY + j * (barWidth + barSpacing);
				subtract(new Area(new Rectangle2D.Double(offsetX, offsetY, barWidth, barWidth)));
			}
		}
	}

}
