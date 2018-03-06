package fr.inria.aviz.physVizEval.jzy3d;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;

import fr.inria.aviz.physVizEval.barchart3d.VisualizationContainer;

public class CustomFrameAWT extends java.awt.Frame {
	public CustomFrameAWT(Chart chart, Rectangle bounds, String title, boolean decoration){
		this.setUndecorated(!decoration);
		this.chart = chart;
		this.setTitle(title + "[AWT]");
		//this.add((java.awt.Component)chart.getCanvas());
		this.add(new VisualizationContainer((java.awt.Component)chart.getCanvas(), title));
		this.pack();
		this.setBounds(bounds);
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() { 
			public void windowClosing(WindowEvent e) {
				CustomFrameAWT.this.remove((java.awt.Component)CustomFrameAWT.this.chart.getCanvas());
				CustomFrameAWT.this.chart.dispose();
				CustomFrameAWT.this.chart = null;
				CustomFrameAWT.this.dispose();
			}
		});
	}
	private Chart chart;
	private static final long serialVersionUID = 1L;

}
