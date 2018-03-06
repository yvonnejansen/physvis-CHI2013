package fr.inria.aviz.physVizEval.barchart2d;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.MatrixData;

/**
 * Contains all bar chart views.
 * 
 * @author dragice
 *
 */
public class JMatrixVisualization extends JPanel implements MouseListener, MouseMotionListener, MatrixDataVisualization {

	JBarChart2D matrix, detail;
	JMatrixVisualization linked_vis;

	AxisLabeling axis;

	public JMatrixVisualization() {

		super();
		
		matrix = new JBarChart2D();
		matrix.hmargin_left = 0.20;
		matrix.hmargin_right = 0.08;
		matrix.vmargin = 0.17;
		matrix.hspacing = 0.015;
		matrix.vspacing = 0.015;
		matrix.verticalBars = true;
		matrix.horizontalBars = true;
		matrix.centeredBars = true;
		
		detail = new JBarChart2D();		
		detail.hmargin_left = 0.20;
		detail.hmargin_right = 0.10;
		detail.vmargin = 0.18;
		detail.hspacing = 0.03;
		detail.vspacing = 0.03;
		detail.noLabelBorder();
		
		setLayout(new GridLayout(1, 2));
		add(matrix);
		add(detail);

		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void setData(MatrixData data) {
		// Update charts
		matrix.setData(data);
		if (data != null)
			matrix.setData(data);
		else
			matrix.setData(null);
		detail.setData(null);
	}
	
	@Override
	public void mousePressed(MouseEvent evt) {
		int[] bar = pickBar(matrix, evt.getPoint());
		if (bar == null) {
			bar = pickBar(detail, evt.getPoint());
			if (bar != null) {
				if (matrix.selectedRow != -1)
					bar[0] = matrix.selectedRow;
				if (matrix.selectedCol != -1)
					bar[1] = matrix.selectedCol;
			}
		}	
		if (bar == null) {
			updateDetailChart(evt.getPoint(), true);
		}
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
	public void mouseClicked(MouseEvent evt) {
		int[] bar = pickBar(matrix, evt.getPoint());
		if (bar == null) {
			bar = pickBar(detail, evt.getPoint());
			if (bar != null) {
				if (matrix.selectedRow != -1)
					bar[0] = matrix.selectedRow;
				if (matrix.selectedCol != -1)
					bar[1] = matrix.selectedCol;
			}
		}	
		if (bar == null) {
		} else {
			boolean selected = matrix.data.isSelected(bar[0], bar[1]);
			matrix.data.setSelected(bar[0], bar[1], !selected);
			if (detail.data != null) {
				if (matrix.selectedRow == bar[0])
					detail.data.setSelected(0, bar[1], !selected);
				if (matrix.selectedCol == bar[1])
					detail.data.setSelected(bar[0], 0, !selected);
			}
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}

	@Override
	public void mouseDragged(MouseEvent evt) {
		updateDetailChart(evt.getPoint(), false);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {		
	}
	
	public void updateDetailChart(Point p, boolean enableUnselect) {
		
		if (matrix.getBounds().contains(p)) {
			int row = -1, col = -1;
			if (matrix.selectedRow != -1 && !enableUnselect) // crossing
				row = pickRow(matrix, p, true, false);
			else if (matrix.selectedCol != -1 && !enableUnselect) // crossing
				col = pickColumn(matrix, p, true, false);
			else {
				row = pickRow(matrix, p, true, false);
				col = pickColumn(matrix, p, true, false);
			}
			updateDetailChart(row, col, enableUnselect);
			if (linked_vis != null)
				linked_vis.updateDetailChart(row, col, enableUnselect);
		}
		else if (detail.getBounds().contains(p)) {
			updateDetailChart(-1, -1, enableUnselect);
			if (linked_vis != null)
				linked_vis.updateDetailChart(-1, -1, enableUnselect);
		}
	}
	
	public void updateDetailChart(int row, int col, boolean enableUnselect) {
		if (row != -1) {
			matrix.setSelectedRow(row);
			matrix.setSelectedColumn(-1);
			detail.setData(matrix.data.extractRow(row, true));
			detail.verticalBars = true;
			detail.horizontalBars = false;
			detail.showBottomLabels = true;
			detail.showTopLabels = false;
		} else if (col != -1) {
			matrix.setSelectedRow(-1);
			matrix.setSelectedColumn(col);
			detail.setData(matrix.data.extractColumn(col, true));
			detail.verticalBars = false;
			detail.horizontalBars = true;
			detail.showBottomLabels = false;
			detail.showTopLabels = true;
		} else if (enableUnselect) {
			matrix.setSelectedRow(-1);
			matrix.setSelectedColumn(-1);
			detail.setData(null);
		}
	}
	
	private int pickRow(JBarChart2D c, Point p, boolean labels, boolean content) {
		return c.pickRow(p.x - c.getX(), p.y - c.getY(), labels, content);
	}
	
	private int pickColumn(JBarChart2D c, Point p, boolean labels, boolean content) {
		return c.pickColumn(p.x - c.getX(), p.y - c.getY(), labels, content);
	}
	
	private int[] pickBar(JBarChart2D c, Point p) {
		return c.pickBar(p.x - c.getX(), p.y - c.getY());
	}
}
