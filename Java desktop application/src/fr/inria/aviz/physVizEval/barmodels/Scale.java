package fr.inria.aviz.physVizEval.barmodels;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.jzy3d.plot3d.primitives.Shape;

import fr.inria.aviz.physVisEval.data.Tick;

public class Scale extends Area {
    protected double sliceWidth = 72;
    protected double scaleFactor = 1;
    protected double maxValue;
    protected double midValue;
    protected boolean isRight;
    protected double height;
    protected double thickness;
    protected double startX;
    protected double startY;   
    protected double lineStart;
    protected double lineEnd;
    protected Line2D[] lines;
    
    public Scale (ArrayList<Tick> ticks, double startX, double startY, double sliceWidth, double height, double baseHeight, double thickness, double maxTick, double maxTickHeight, boolean isRight)
    {
    	this.startX = startX;
    	this.startY = startY;
    	this.sliceWidth = sliceWidth;
    	this.height = height;
    	this.thickness = thickness;
    	this.scaleFactor = maxTickHeight/maxTick;
    	this.maxValue = maxValue;
    	this.midValue = midValue;
    	this.isRight = isRight;
    	this.lineStart = isRight ? startX + 5 : startX + thickness;
    	this.lineEnd = isRight ? startX + sliceWidth + thickness : startX + sliceWidth + 2*thickness - 5;
    	
    	add(new Area(new Rectangle2D.Double(isRight ? startX : startX + thickness, startY, isRight ? sliceWidth + 2*thickness : sliceWidth + thickness, height)));
    	add(new Area(new Rectangle2D.Double(isRight ? startX + thickness  : startX + thickness, startY+height, isRight ? sliceWidth + thickness : sliceWidth, baseHeight)));
    	if (isRight) {
    		subtract(new Area(new Rectangle2D.Double(startX + sliceWidth + thickness, startY + height/4 - thickness, thickness, 2*thickness)));
    		subtract(new Area(new Rectangle2D.Double(startX + sliceWidth + thickness, startY + height*3/4 - thickness, thickness, 2*thickness)));
    		subtract(new Area(new Rectangle2D.Double(startX + sliceWidth/4, startY + height, 2 * thickness, thickness)));
    		subtract(new Area(new Rectangle2D.Double(startX + sliceWidth/4*3, startY + height, 2 * thickness, thickness)));
    	}
    	else {
    		add(new Area(new Rectangle2D.Double(startX, startY + height/4 - thickness, thickness, 2*thickness)));
    		add(new Area(new Rectangle2D.Double(startX, startY + height*3/4 - thickness, thickness, 2*thickness)));
    		subtract(new Area(new Rectangle2D.Double(startX +  sliceWidth/4, startY + height, 2 * thickness, thickness)));
    		subtract(new Area(new Rectangle2D.Double(startX + sliceWidth/4*3, startY + height, 2 * thickness, thickness)));
    	}
//		add(new Area(new Rectangle2D.Double(isRight ? startX + sliceWidth/4 + 3* thickness : startX + sliceWidth/4 + 2* thickness , startY+height+baseHeight, 2*thickness, thickness)));
//		add(new Area(new Rectangle2D.Double(isRight ? startX + sliceWidth/4*3 -  thickness  :  startX + sliceWidth/4*3 - 2 * thickness , startY+height+baseHeight, 2*thickness, thickness)));

    	lines = new Line2D[ticks.size() - 1];
    	for (int i = 0; i < ticks.size() - 1; i++){
    		lines[i] = new Line2D.Double(lineStart, startY + (height - maxTickHeight) + i * (maxTickHeight/(ticks.size()-1)), lineEnd, startY + (height - maxTickHeight) + i*maxTickHeight/(ticks.size()-1));
    		
    	}
    }
    

}
