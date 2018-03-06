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
public class JLineChartVisualization extends JMatrixVisualization {

	public JLineChartVisualization() {

		super();
		
		matrix.verticalBars = false;
		matrix.horizontalBars = false;
		matrix.horizontalLines = true;
		matrix.verticalSuperimpose = true;
	//	matrix.selectionColor = new Color(0, 0, 0, 0);
	}
		
}
