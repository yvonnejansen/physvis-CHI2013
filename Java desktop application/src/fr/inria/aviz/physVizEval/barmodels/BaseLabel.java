package fr.inria.aviz.physVizEval.barmodels;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class BaseLabel extends Area {
	String[] labels;
	boolean isFront;
	public BaseLabel(String[] labels, double startX, double startY, double width, double height, double thickness, boolean isFront) 
	{
		this.labels = labels;
		this.isFront = isFront;
		width += thickness;
		add(new Area(new Rectangle2D.Double(startX + thickness, startY, height, width)));
		subtract(new Area(new Rectangle2D.Double(startX + height/2, startY, 2*thickness, thickness)));
		add(new Area(new Rectangle2D.Double(startX, startY + width/2 - thickness/2, thickness, 2*thickness)));
		add(new Area(new Rectangle2D.Double(startX + height/2, startY + width, thickness * 2, thickness)));
	
	}
}
