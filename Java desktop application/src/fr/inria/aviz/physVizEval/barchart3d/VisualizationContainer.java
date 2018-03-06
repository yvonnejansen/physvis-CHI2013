package fr.inria.aviz.physVizEval.barchart3d;

import javax.swing.*;

import org.jzy3d.colors.Color;

import fr.inria.aviz.physVisEval.data.MatrixData;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Adds a title to a visualization and manages scale.
 * @author dragice
 *
 */
public class VisualizationContainer extends Panel implements ComponentListener {

	double scale = 0.95;
	
	Label title = new Label();
	Component vis;
	String titlestring;
	
	public VisualizationContainer(Component vis, String titlestring) {
		this.vis = vis;
		title.setAlignment(Label.CENTER);
		title.setFont(new Font("Helvetica", 0, 16));
		title.setText(titlestring);
		setLayout(null);
		addComponentListener(this);
		add(title);
		add(vis);
		setBackground(new java.awt.Color(0.95f, 0.95f, 0.95f));
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		int w = getWidth();
		int h = getHeight();
		int vh = (int)(h * scale);
		int vy = h - vh;//(h - vh) / 2;
		int vw = (int)(w * scale);
		int vx = (w - vw) / 2;
		vis.setBounds(vx, vy, vw, vh);
		int th = 20;
		title.setBounds(0, (vy - th)/2, w, th);
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
