package fr.inria.aviz.physVizEval.barchart2d;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import fr.inria.aviz.physVizEval.util.GUIUtils;

public class Main2D extends JFrame {

	public final JMultipleBarChartsVisualization vis1 = new JMultipleBarChartsVisualization();
	public final JMatrixVisualization vis2 = new JMatrixVisualization();
	public final JLineChartVisualization vis3 = new JLineChartVisualization();
	
	public final JVisualizationContainer[] containers;
	public final MatrixDataVisualization[] visualizations;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main2D win = new Main2D();

		DataFileChooser chooser = new DataFileChooser(win.containers, "data/datasets/");
		chooser.setBounds(20, 20, 300, 350);
		chooser.setAlwaysOnTop(true);
		chooser.setVisible(true);
		
		win.setVisible(true);

		chooser.toFront();
	}
	
	public Main2D() {
						
		Container c = getContentPane();
		
		visualizations = new MatrixDataVisualization[]{vis1, vis2, vis3};
		
		containers = new JVisualizationContainer[visualizations.length];
		for (int i=0; i<visualizations.length; i++)
			containers[i] = new JVisualizationContainer(visualizations[i]);
		
		// Synchronize the selection in the matrix and the line charts
		vis3.linked_vis = vis2;
		vis2.linked_vis = vis3;
			
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Multiple bar charts", null, containers[0]);
		tabbedPane.addTab("Matrix", null, containers[1]);
		tabbedPane.addTab("Line charts", null, containers[2]);
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_B);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_M);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_L);
		c.add(tabbedPane);
		
		//Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
		GUIUtils.centerOnPrimaryScreen(this, 1920, 1080); // the resolution of the HP screen used in the experiment
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}


}
