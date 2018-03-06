package fr.inria.aviz.physVizEval.barchart2d;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVizEval.barchart2d.JBarChart2D.Direction;

/**
 * Contains all bar chart views.
 * 
 * @author dragice
 *
 */
public class JMultipleBarChartsVisualization extends JPanel implements MouseListener, MouseMotionListener, MatrixDataVisualization {

	double scale = 0.2;
	
	JBarChart2D smallMultiples1, smallMultiples2, detail;
	AxisLabeling axis;

	public JMultipleBarChartsVisualization() {

		super();
		
		smallMultiples1 = new JBarChart2D();
		smallMultiples1.hmargin_left = 0.30;
		smallMultiples1.hmargin_right = 0.10;
		smallMultiples1.vmargin = 0.08;
		smallMultiples1.hspacing = 0.04;
		smallMultiples1.vspacing = 0.015;
		smallMultiples1.noLabelBorder();
		
		smallMultiples2 = new JBarChart2D();
		smallMultiples2.hmargin_left = 0.30;
		smallMultiples2.hmargin_right = 0.10;
		smallMultiples2.vmargin = 0.08;
		smallMultiples2.hspacing = 0.04;
		smallMultiples2.vspacing = 0.015;
		smallMultiples2.noLabelBorder();
		
		detail = new JBarChart2D();		
		detail.hmargin_left = detail.hmargin_right = 0.20;
		detail.vmargin = 0.21;
		detail.hspacing = 0.03;
		detail.showTopLabels = false;
		detail.selectedRow = 0;
		detail.horizontalBars = false;
		detail.verticalBars = true;
		detail.noLabelBorder();
		
		JPanel smallMultiplesPanel = new JPanel();
		smallMultiplesPanel.setLayout(new GridLayout(1, 2));
		smallMultiplesPanel.add(smallMultiples1);
		smallMultiplesPanel.add(smallMultiples2);
		
		setLayout(new GridLayout(1, 2));
		add(smallMultiplesPanel);
		add(detail);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
		
	public void setData(MatrixData data) {
		// Compute axis range and labelling for all charts
		axis = null;

		// Update charts
		smallMultiples1.setData(data);
		if (data != null) {
			// Now the reordering is done in dataselection.Main
//		      data.reorder();
			smallMultiples2.setData(data.getInverse(true));
		}
		else
			smallMultiples2.setData(null);
		detail.setData(null);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		updateDetailChart(evt.getPoint(), true);	
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent evt) {
		updateDetailChart(evt.getPoint(), false);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void updateDetailChart(Point p, boolean enableUnselect) {
		
		if (smallMultiples1.getBounds().contains(p)) {
			int row = pickRow(smallMultiples1, p);
			if (row == -1) {
				if (enableUnselect) {
					unselectAll();
					detail.setData(null);
				}
				return;
			}
			MatrixData detailData = smallMultiples1.data.extractRow(row, true);
			unselectAll();
			smallMultiples1.setSelectedRow(row);
			smallMultiples2.setSelectedColumn(row);
			detail.setData(detailData);
		}
		else if (smallMultiples2.getBounds().contains(p)) {
			int row = pickRow(smallMultiples2, p);
			if (row == -1) {
				if (enableUnselect) {
					unselectAll();
					detail.setData(null);
				}
				return;
			}
			MatrixData detailData = smallMultiples2.data.extractRow(row, true);
			unselectAll();
			smallMultiples2.setSelectedRow(row);
			smallMultiples1.setSelectedColumn(row);
			detail.setData(detailData);
		} else if (detail.getBounds().contains(p)) {
			int row = pickRow(detail, p);
			if (row == -1 && enableUnselect) {
				unselectAll();
				detail.setData(null);
			}
		}
	}
	
	private int pickRow(JBarChart2D c, Point p) {
		return c.pickRowContent(p.x - c.getX(), p.y - c.getY());
	}
	
	private void unselectAll() { 
		smallMultiples1.setSelectedRow(-1);
		smallMultiples2.setSelectedRow(-1);
		smallMultiples1.setSelectedColumn(-1);
		smallMultiples2.setSelectedColumn(-1);
	}
	
	public void showOcclusions(boolean show) {
		if (show) {
			smallMultiples1.showOcclusions = Direction.TOP;
			smallMultiples2.showOcclusions = Direction.BOTTOM;
		} else {
			smallMultiples1.showOcclusions = Direction.NONE;
			smallMultiples2.showOcclusions = Direction.NONE;
		}
		repaint();
	}
	
	public double getOcclusionRatio() {
		double occl1 = smallMultiples1.computeOcclusionRatio(Direction.TOP);
		double occl2 = smallMultiples2.computeOcclusionRatio(Direction.BOTTOM);
		return (occl1 + occl2) / 2;
	}
	
}
