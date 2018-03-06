package fr.inria.aviz.physVizEval.barchart2d;

import javax.swing.*;

import fr.inria.aviz.physVisEval.data.MatrixData;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * Adds a title to a visualization and manages scale.
 * @author dragice
 *
 */
public class JVisualizationContainer extends JPanel implements ComponentListener {

	double scale = 0.82;
	
	JLabel title = new JLabel();
	MatrixDataVisualization vis;
	
	public JVisualizationContainer(MatrixDataVisualization vis) {
		this.vis = vis;
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font("Helvetica", 0, 16));
		setLayout(null);
		addComponentListener(this);
		add(title);
		add((JComponent)vis);
		setBackground(JBarChart2D.bgColor);
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
		((JComponent)vis).setBounds(vx, vy, vw, vh);
		int th = 20;
		title.setBounds(0, (vy - th)/2, w, th);
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void setData(MatrixData data) {
		if (data != null)
			title.setText(data.getDataInfo().getTitle());
		else
			title.setText("");
		vis.setData(data);
	}
	
}
