package fr.inria.aviz.physVizEval.barmodels;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class Spacer extends Area {
	protected double width;
	protected double height;
	
	public Spacer (double startX, double startY, double width, double height, double thickness, double barSpacing, double baseCoverThickness)
	{
		add(new Area(new Rectangle2D.Double(startX+0.5, startY, width-0.5, height - baseCoverThickness)));
		add(new Area(new Rectangle2D.Double(startX + width/5 - thickness, startY+height - baseCoverThickness, 2*thickness, thickness)));
		add(new Area(new Rectangle2D.Double(startX + width/5*4 - thickness, startY+height - baseCoverThickness, 2*thickness, thickness)));
	}
}
