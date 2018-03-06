package fr.inria.aviz.physVizEval.barmodels;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;

import fr.inria.aviz.physVisEval.data.Tick;

public class Slice extends Area{
    protected double barWidth = 5;
    protected double sliceWidth = 72;
    protected double baseHeight = 17;
    protected double barSpacing = 2;
    protected double scaleFactor = 1;
    protected double[] vals;
    protected double startX = 0, startY = 0;
    protected String name;
    protected Line2D[] lines;
    protected double maxVal;


	public Slice(ArrayList<Tick>ticks, String name, double[] vals, double scaleFactor, double _startX, double _startY, double barWidth, double sliceWidth, double baseHeight, double barSpacing, double thickness, double maxTickHeight, double height, double baseCoverThickness)
	{
		this.barWidth = barWidth;
		this.sliceWidth = sliceWidth;
		this.baseHeight = baseHeight;
		this.barSpacing = barSpacing;
		this.vals = vals;
		this.scaleFactor = scaleFactor;
		this.name = name;
		this.startX = _startX;
		this.startY = _startY + thickness;
		this.lines = new Line2D[ticks.size()];
		
		double[] sortedVals = Arrays.copyOf(vals, vals.length);
		Arrays.sort(sortedVals);
		maxVal = sortedVals[vals.length-1];
		
		double check = vals.length * (barWidth + barSpacing) + barSpacing;
		boolean valid = check == sliceWidth;
		System.out.println("valid data " + valid + " check val is " + check + " slicewidth is " + sliceWidth);
		
		if (valid) 
		{
			add(new Area(new Rectangle2D.Double(startX, startY, sliceWidth, baseHeight - baseCoverThickness)));
			add(new Area(new Rectangle2D.Double(startX + sliceWidth/3 - thickness, _startY, 2* thickness, thickness)));
			add(new Area(new Rectangle2D.Double(startX + sliceWidth/3*2 - thickness, _startY, 2* thickness, thickness)));
			double xOffset = barSpacing + startX;
			for (int i =0; i < vals.length; i++) {
				add(new Area(new Rectangle2D.Double(xOffset, startY + baseHeight - baseCoverThickness, barWidth, vals[i] * scaleFactor + baseCoverThickness)));
				xOffset += barSpacing + barWidth;
			}
		}
		
    	for (int i = 0; i < ticks.size(); i++){
    		if (i * (maxTickHeight/(ticks.size()-1)) <= maxVal * scaleFactor)
    			lines[i] = new Line2D.Double(startX + barSpacing, startY + baseHeight + i * (maxTickHeight/(ticks.size()-1)), startX + sliceWidth - barSpacing , startY + baseHeight + i*maxTickHeight/(ticks.size()-1));
    		
    	}

	}
	
//	public void paint (Graphics g) 
//	{
//		Graphics2D g2 = (Graphics2D)g;
//		int check = vals.length * (barWidth + barSpacing) + barSpacing;
//		boolean valid = check == sliceWidth;
//		System.out.println("valid data " + valid);
//		if (valid) {
//			g2.drawRect(getHeight() - baseHeight, 0, sliceWidth, baseHeight);
//		}
//	}
}
