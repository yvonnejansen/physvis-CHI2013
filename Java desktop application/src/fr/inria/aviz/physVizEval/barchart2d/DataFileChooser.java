package fr.inria.aviz.physVizEval.barchart2d;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;

public class DataFileChooser extends JDialog implements ListSelectionListener {

	private static final long serialVersionUID = 1L;

	JList list;
	JVisualizationContainer[] chartPanels;

	public DataFileChooser(JVisualizationContainer[] win, String csvDirectory) {
		this(win, getCsvFiles(csvDirectory));
	}
	
	public DataFileChooser(JVisualizationContainer[] win, String[] csvFiles) {
		setTitle("CSV files");
		this.chartPanels = win;
		list = new JList();
		DefaultListModel content = new DefaultListModel();
		for (int i=0; i<csvFiles.length; i++)
			content.addElement(csvFiles[i]);
		list.setModel(content);
		JScrollPane sp = new JScrollPane(list);
		getContentPane().add(sp, BorderLayout.CENTER);
		list.addListSelectionListener(this);
		list.setSelectedIndex(0);
		setPreferredSize(new Dimension(200, 400));
		pack();
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;
		final String csvFile = (String)list.getSelectedValue();
		if (csvFile == null) return;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DataFileChooser.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				MatrixData data = new MatrixData(csvFile, null);
				if (data.getDataInfo() == null) {
					DataInfo defaultInfo = new DataInfo("No metadata file", new AxisLabeling(data.computeMin(), data.computeMax(), 0, 1, ""), null);
					data.setDataInfo(defaultInfo);
				}
				for (JVisualizationContainer chartPanel : chartPanels)
					chartPanel.setData(data);	
				DataFileChooser.this.setCursor(Cursor.getDefaultCursor());
			}
		});
	}
	
	public void next() {
		int index = list.getSelectedIndex();
		if (index + 1 < list.getModel().getSize())
			list.setSelectedIndex(index + 1);
	}
	
	public void previous() {
		int index = list.getSelectedIndex();
		if (index - 1 >= 0)
			list.setSelectedIndex(index - 1);
	}
	
	public String getCurrentCsvFile() {
		return (String) list.getSelectedValue();
	}

	/**
	 * 
	 */
	public static String[] getCsvFiles(String directory) {
		File file = new File(directory);
		File[] res = file.listFiles(new FileFilter() {
			public boolean accept(File f) {
				if (f.isFile()) {
					if (f.getName().endsWith(".csv")) {
						return true;
					}
				}
				return false;
			}
		});
		if (res == null || res.length == 0) {
			System.out.println("No csv file in " + directory);
			System.exit(0);
		}
		String[] res2 = new String[res.length];
		for (int i = 0; i < res.length; i++) {
			res2[i] = directory + res[i].getName();
		}
		return res2;
	}
}
