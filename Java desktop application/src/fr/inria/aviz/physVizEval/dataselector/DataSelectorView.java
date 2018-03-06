package fr.inria.aviz.physVizEval.dataselector;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.ColorCoding;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVisEval.data.RowColorCoding;
import fr.inria.aviz.physVizEval.barchart2d.JMultipleBarChartsVisualization;
import fr.inria.aviz.physVizEval.barchart2d.JVisualizationContainer;
import fr.inria.aviz.physVizEval.barchart2d.Main2D;
import fr.inria.aviz.physVizEval.barchart2d.MatrixDataVisualization;
import fr.inria.aviz.physVizEval.barchart3d.BarChart3D;
import fr.inria.aviz.physVizEval.barchart3d.Utils;
import fr.inria.aviz.physVizEval.util.CSV;
import fr.inria.aviz.physVizEval.util.GUIUtils;

public class DataSelectorView extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int maximumCountryNameLength = 15;
	
	static boolean mac = System.getProperty("os.name").contains("Mac");
	
	CSV originalData;//, selectedData;
//	JButton ok = new JButton("OK");
	JButton changeData = new JButton("Load data...");
	JButton exportData = new JButton("Save data");
	JTextField noOfCountries = new JTextField();
	int countryNumber = 10;
	
	JTextField yearInter = new JTextField();
	JSlider yearInterSlider = new JSlider();
	int yearInterval;
	int minimumYearInterval, maximumYearInterval;
	
	JTextField lastYearField = new JTextField();
	JSlider lastYearSlider = new JSlider();
	int lastYear, firstYear;
	int datasetFirstYear, datasetLastYear;
	boolean valueNotificationEnabled = false;
	boolean verbose = false;
	
	JTextField excludeField = new JTextField();
	String lastExcludeFieldValue = "";
	ArrayList<String> excludeList = new ArrayList<String>();

	JLabel distanceCheck = new JLabel("Min distance: ");
	JLabel stats = new JLabel("");
	JCheckBox showOcclusions = new JCheckBox("Show occlusions");
	Main2D visualizations;
	BarChart3D viz3d;
	
	JButton reorder = new JButton("Reorder countries" + (mac ? " (not on Mac)" : ""));
	JTextField title = new JTextField("");
	JTextField divider = new JTextField("1");
	JTextField unit = new JTextField("");
	
	String filename;
	MatrixData data;

	public DataSelectorView()
	{
		super();
		
		// Create and display the barchart viewer
		BarChart3D.init(false);
		visualizations = new Main2D();
		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
		int margin = res.width / 8;
		visualizations.setBounds(margin, margin*3/2, res.width - margin*2, res.height - margin*2);
				
		// Configure this component
		
		exportData.setActionCommand("export");
		exportData.addActionListener(this);
		exportData.setMnemonic(KeyEvent.VK_ENTER);
		changeData.setActionCommand("change");
		changeData.addActionListener(this);
		
		noOfCountries.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valuesChanged(false);
				update3DChart(false);
			}
		});
		yearInterSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				valuesChanged(false);
				if (!yearInterSlider.getValueIsAdjusting())
					update3DChart(false);
				else
					hide3DChart();
			}
		});
		yearInter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valuesChanged(false);
				update3DChart(false);
			}
		});
		lastYearSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				valuesChanged(false);
				if (!lastYearSlider.getValueIsAdjusting())
					update3DChart(false);
				else
					hide3DChart();
			}
		});
		lastYearField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valuesChanged(false);
				update3DChart(false);
			}
		});
		excludeField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				valuesChanged(false);
				update3DChart(false);
			}
		});
		showOcclusions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				visualizations.vis1.showOcclusions(showOcclusions.isSelected());
			}
		});
		reorder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!mac)
				data.reorder();
				exportData.setEnabled(true);
				visualizations.repaint();
				update3DChart(false);
			}
		});
		if (mac)
			reorder.setEnabled(false);
		title.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				metadataChanged();
				update3DChart(false);
			}
		});
		divider.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				metadataChanged();
				update3DChart(false);
			}
		});
		unit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				metadataChanged();
				update3DChart(false);
			}
		});		
		///
				
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 2, 2, 2);
		
        JComponent content = new JPanel();
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.setLayout(gridbag);
        getContentPane().add(content);
        
		
        /////////
        
		JLabel lbl;

        // ++++++++++++++++++++++++++++++++++
        
		c.gridwidth = 1;
		c.weightx = 0;
		c.insets = new Insets(2, 2, 30, 2);
		gridbag.setConstraints(changeData, c);
		content.add(changeData);
		
		c.gridwidth = 1; 
		c.weightx = 0.2;
		lbl = new JLabel("");
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.7;
		lbl = new JLabel("");
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
        // ++++++++++++++++++++++++++++++++++
        
		lbl = new JLabel("Countries/years to be extracted");
		lbl.setHorizontalAlignment(JLabel.RIGHT);
		c.gridwidth = 1;
		c.weightx = 0;
		c.insets = new Insets(2, 2, 2, 2);
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
		c.gridwidth = 1; 
		c.weightx = 0.2;
		gridbag.setConstraints(noOfCountries, c);
		content.add(noOfCountries);
		noOfCountries.setText(countryNumber + "");
		
		c.gridwidth = GridBagConstraints.RELATIVE; //end row
		c.weightx = 0.7;
		stats.setFont(new Font("Fixed", 0, 10));
		gridbag.setConstraints(stats, c);
		content.add(stats);

		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		c.weightx = 0.7;
		distanceCheck.setFont(new Font("Fixed", 0, 10));
		gridbag.setConstraints(distanceCheck, c);
		content.add(distanceCheck);

        // ++++++++++++++++++++++++++++++++++
		
		lbl = new JLabel("Countries to exclude");
		lbl.setHorizontalAlignment(JLabel.RIGHT);
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
		c.gridwidth = 1; 
		c.weightx = 0.2;
		gridbag.setConstraints(excludeField, c);
		content.add(excludeField);
		excludeField.setText("");
		
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		c.weightx = 0.7;
		gridbag.setConstraints(showOcclusions, c);
		content.add(showOcclusions);
		
        // ++++++++++++++++++++++++++++++++++
		
		lbl = new JLabel("Interval between years");
		lbl.setHorizontalAlignment(JLabel.RIGHT);
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
		c.gridwidth = 1;
		c.weightx = 0.2;
		gridbag.setConstraints(yearInter, c);
		content.add(yearInter);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		c.weightx = 0.7;
		gridbag.setConstraints(yearInterSlider, c);
		content.add(yearInterSlider);
		
        // ++++++++++++++++++++++++++++++++++
		
		lbl = new JLabel("Last year");
		lbl.setHorizontalAlignment(JLabel.RIGHT);
		c.gridwidth = 1;
		c.weightx = 0;
		c.insets = new Insets(2, 2, 30, 2);

		gridbag.setConstraints(lbl, c);
		content.add(lbl);

		gridbag.setConstraints(lastYearField, c);
		c.gridwidth = 1;
		c.weightx = 0.2;
		gridbag.setConstraints(lastYearField, c);
		content.add(lastYearField);
		
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		c.weightx = 0.7;
		gridbag.setConstraints(lastYearSlider, c);
		content.add(lastYearSlider);
		
        // ++++++++++++++++++++++++++++++++++
		
		c.gridwidth = 1;
		c.weightx = 0;
		c.insets = new Insets(2, 2, 2, 2);
		gridbag.setConstraints(reorder, c);
		content.add(reorder);
		
		c.gridwidth = 1;
		c.weightx = 0.2;
		lbl = new JLabel("");
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.7;
		lbl = new JLabel("");
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
        // ++++++++++++++++++++++++++++++++++
		
		lbl = new JLabel("Title:");
		lbl.setHorizontalAlignment(JLabel.RIGHT);
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		gridbag.setConstraints(title, c);
		content.add(title);
		
        // ++++++++++++++++++++++++++++++++++
		
		lbl = new JLabel("Unit divider:");
		lbl.setHorizontalAlignment(JLabel.RIGHT);
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
		c.gridwidth = 1;
		c.weightx = 0.2;
		gridbag.setConstraints(divider, c);
		content.add(divider);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.7;
		lbl = new JLabel("");
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
        // ++++++++++++++++++++++++++++++++++
		
		lbl = new JLabel("Unit:");
		lbl.setHorizontalAlignment(JLabel.RIGHT);
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
		c.gridwidth = 1;
		c.weightx = 0.2;
		gridbag.setConstraints(unit, c);
		content.add(unit);
		
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 0.7;
		lbl = new JLabel("");
		gridbag.setConstraints(lbl, c);
		content.add(lbl);
		
        // ++++++++++++++++++++++++++++++++++
		
		c.gridwidth = 1;
		c.weightx = 0;
		gridbag.setConstraints(exportData, c);
		content.add(exportData);
		
		c.gridwidth = 1;
		c.weightx = 0.2;
		lbl = new JLabel("");
		gridbag.setConstraints(lbl, c);
		content.add(lbl);

		//
		
		originalData = new CSV();
		
		setFile("data/datasets/army.csv");
		
		pack();
		setBounds(30, 00, 1000, getHeight());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		visualizations.setVisible(true);
		this.setAlwaysOnTop(true);
//		this.toFront();
	}
	
	public void setFile(String filename) {
		
		valueNotificationEnabled = false;
		
		// Title
		setTitle(filename);
		
		// Load the data
		// FIXME
		this.filename = filename;
		originalData.clear();
		originalData.readSeparatorSafe(filename);
		
		// Compute stuff
		String lastColName = originalData.getColumnName(originalData.getColumnCount()-1).trim();
		datasetLastYear = Integer.valueOf(lastColName);
		String secondTolastColName = originalData.getColumnName(originalData.getColumnCount()-2).trim();
		String firstColName = originalData.getColumnName(1).trim();
		datasetFirstYear = Integer.valueOf(firstColName);
		minimumYearInterval = datasetLastYear - Integer.valueOf(secondTolastColName);
		maximumYearInterval = (datasetLastYear - datasetFirstYear + 1) / countryNumber;
		
		// Set default values
		lastYear = datasetLastYear;
		lastYearField.setText(lastYear + "");
		yearInterval = minimumYearInterval;
		yearInter.setText(minimumYearInterval + "");
		firstYear = lastYear - yearInterval * (countryNumber - 1);
		lastExcludeFieldValue = "";
		String defaultTitle = extractFilename(filename);
		title.setText(defaultTitle);
		divider.setText("1");
		unit.setText("");
		
		// Configure sliders
		configureSliders();
		yearInterSlider.setValue(yearInterval);
		lastYearSlider.setValue(lastYear);
		excludeField.setText(lastExcludeFieldValue);
		
		valuesChanged(true);	
		update3DChart(true);
		valueNotificationEnabled = true;
	}
	
	protected void lookAtAllCombinations()
	{
		
	}
	
	protected void configureSliders() {
		
		try {
			maximumYearInterval = (datasetLastYear - datasetFirstYear + 1) / countryNumber;
	
			yearInterSlider.setMinimum(minimumYearInterval);
			yearInterSlider.setMaximum(maximumYearInterval);
			yearInterSlider.setSnapToTicks(true);
			yearInterSlider.setPaintTicks(true);
			yearInterSlider.setPaintLabels(true);
			yearInterSlider.setMinorTickSpacing(minimumYearInterval);
			yearInterSlider.setMajorTickSpacing(minimumYearInterval);
			yearInterSlider.setInverted(true);
			yearInterSlider.setEnabled(minimumYearInterval < maximumYearInterval);
			
			lastYearSlider.setMinimum(datasetFirstYear + minimumYearInterval + countryNumber);
			lastYearSlider.setMaximum(datasetLastYear);
			lastYearSlider.setSnapToTicks(true);
			lastYearSlider.setPaintTicks(true);
			lastYearSlider.setPaintLabels(true);
			lastYearSlider.setMinorTickSpacing(minimumYearInterval);
			lastYearSlider.setMajorTickSpacing((datasetLastYear - datasetFirstYear) / minimumYearInterval <= 15 ? minimumYearInterval : minimumYearInterval * 10);
			lastYearSlider.setInverted(false);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	public void valuesChanged(boolean force) {
		if (!valueNotificationEnabled && !force)
			return;

		int new_countryNumber = 10;
		try {
			new_countryNumber = Integer.valueOf(noOfCountries.getText());
		} catch (Exception e) {
		}
		noOfCountries.setText(new_countryNumber + "");		
		
		int new_YearInterval;
		try {
			new_YearInterval = Integer.valueOf(yearInter.getText());
			if (!yearInterSlider.getValueIsAdjusting())
				yearInterSlider.setValue(new_YearInterval);
		} catch (Exception e) {
		}
		new_YearInterval = yearInterSlider.getValue();
		yearInter.setText(new_YearInterval + "");
		
		int new_lastYear;
		try {
			new_lastYear = Integer.valueOf(lastYearField.getText());
			if (!lastYearSlider.getValueIsAdjusting())
				lastYearSlider.setValue(new_lastYear);
		} catch (Exception e) {
		}
		new_lastYear = lastYearSlider.getValue();
		// prevent selecting nonexistent values
		if (((datasetLastYear - new_lastYear) % minimumYearInterval) != 0) {
			new_lastYear = datasetLastYear - minimumYearInterval * (int)((datasetLastYear - new_lastYear) / minimumYearInterval);
		}
		lastYearField.setText(new_lastYear + "");
		
		String newExcludeFieldValue = excludeField.getText();
		
		if (!force && (yearInterval == new_YearInterval && new_countryNumber == countryNumber && new_lastYear == lastYear && lastExcludeFieldValue == newExcludeFieldValue)) {
			BarChart3D.display(data, false);
			return;
		}
		
		if (new_countryNumber != countryNumber) {
			countryNumber = new_countryNumber;
			configureSliders();
		}
		
		StringTokenizer st = new StringTokenizer(newExcludeFieldValue, ",");
		excludeList.clear();
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			excludeList.add(s.trim().toLowerCase());
		}

		lastExcludeFieldValue = newExcludeFieldValue;
		yearInterval = new_YearInterval;
		lastYear = new_lastYear;
		firstYear = lastYear - yearInterval * (countryNumber - 1);

		// Compute data subset
		compute();

		// Compute stats
		String info = "";
		info += "Occlusion: " + String.format("%3.1f", visualizations.vis1.getOcclusionRatio() * 100) + " %";
		stats.setText(info);
	}
	
	public void update3DChart(boolean force) {
		if (!valueNotificationEnabled && !force)
			return;
		BarChart3D.hide();
		BarChart3D.display(data, false);
	}
	
	public void hide3DChart() {
		BarChart3D.hide();
	}
	
	public void metadataChanged() {
		if (data != null) {
			//compute();
			data.setDataInfo(createMetaDataForCountryIndicators());
			for (JVisualizationContainer v : visualizations.containers) {
				v.setData(data);
			}
		}
	}
	
	/**
	 * A method with a long name which contains everything specific to the country indicator dataset.
	 * @param maxValue
	 * @return
	 */
	public DataInfo createMetaDataForCountryIndicators() {
		final int desiredNumberOfTicks = 10;
		double minDataValue = 0;
		double maxDataValue = data.computeMax();
		double unitDivider = 1;
		try {
			unitDivider = Double.valueOf(divider.getText());
		} catch (NumberFormatException e) {}
		AxisLabeling axisLabeling = new AxisLabeling(minDataValue, maxDataValue, desiredNumberOfTicks, unitDivider, unit.getText());
		DataInfo info = new DataInfo(title.getText() + (mac ? " (unordered)" : ""), axisLabeling, new RowColorCoding(ColorCoding.PALETTE_COLORBREWER_10));
		return info;
	}
	
	public void compute()
	{
		CSV selectedData = new CSV(originalData);
				
		// Compute first year
		int firstYear = lastYear - yearInterval * (countryNumber - 1);
		if (verbose)
			System.out.println("The last year in our dataset is " + lastYear + " and the first year is " + firstYear);
		String[] yearSelection = new String[countryNumber];

//System.out.println("Selected interval " + firstYear + " " + lastYear);

		for (int i = 0; i < countryNumber; i++) {
			yearSelection[i] = String.valueOf(firstYear + i * yearInterval);
		}

		// remove everything else
		selectedData.removeAllColumnsExcept(yearSelection);
		if (verbose)
			System.out.println("We're down to " + selectedData.getColumnCount() + " columns. (double check: " + selectedData.getRowArray(1).length + ")");
		
//System.out.print("Kept columns ");
//for (int i=0; i<selectedData.getColumnCount(); i++)
//	System.out.print(selectedData.getColumnName(i) + ", ");
//System.out.println();
		
		// remove long country names and countries from explicit exclude list
		for (int i = 0; i < selectedData.getRowCount(); i++) {
			String country = selectedData.getValue(i, 0);
			if (country.length() > maximumCountryNameLength) {
				selectedData.setValue(i, 0, null); // will be removed below
			} else if (excludeList.contains(country.trim().toLowerCase())) {
				selectedData.setValue(i, 0, null); // will be removed below
			}
		}
		
//		System.out.println("current data: \n" + data);
		selectedData.removeRowsWithMissingValues();
		selectedData.removeRowsWithNonNumericalValues(1);
		
//System.out.println("We have " + selectedData.getRowCount() + " rows left");
		
//		System.out.println("\n\n\n\n\n\n\n\n\n\n complete data: \n" + data);		
		
		// now we compute the standard deviation for each row to pick those with highest variance
		VarianceHelper[] sds = new VarianceHelper[selectedData.getRowCount()];
		for (int i = 0; i < selectedData.getRowCount(); i++) {
			double[] ar = selectedData.getDoubleRowArray(i);
			
			if (ar != null) {
				if (ar.length == selectedData.getColumnCount() - 1) {
				DescriptiveStatistics stat = new DescriptiveStatistics(ar);
				sds[i] = new VarianceHelper(i, stat.getStandardDeviation(), selectedData.getRowArray(i));
				if (verbose)
					System.out.println("Variance for country " + selectedData.getRowArray(i)[0] + " is " + sds[i].variance);
				}
				else {
					sds[i] = new VarianceHelper(i, 0, selectedData.getRowArray(i));
				}
			}
			else {
				sds[i] = new VarianceHelper(i, 0, selectedData.getRowArray(i));
			}
		}
		
		// sort the array according to the variance
		Arrays.sort(sds);
		
		int[] sortOrder = new int[sds.length];
		for(int i =0; i < sds.length; i++)
			sortOrder[i] = sds[i].index;
		selectedData.sortRows(sortOrder);
				
		// now check if the data for the last noOfCountryies is complete
		
		if (selectedData.getRowCount() < countryNumber) {
			//if (verbose)
				System.err.println("Not enough suitable rows left (we have " + selectedData.getRowCount() + " but need " + countryNumber + "). Choose different # of countries or different year interval for this dataset. ");
			for (JVisualizationContainer v : visualizations.containers)
				v.setData(null);
			exportData.setEnabled(false);
			return;
		}
		else {
			exportData.setEnabled(true);

			//  remove rows from the dataset
			int rowsToDelete = selectedData.getRowCount() -1 - countryNumber;
			if (rowsToDelete > 0) {
				for (int i = rowsToDelete; i >= 0; i--)
				{
					if (verbose)
						System.out.println("removing " + selectedData.getValue(i, 0) + " with variance: " + sds[i].variance);
					selectedData.clearRow(sds[i].row);
				}
			}
//			// now remove deleted indices from sds -> not needed anymore, now we use means
//			for (int i = rowsToDelete - 1; i >= 0; i--)
//			{
//				ArrayUtils.remove(sds, i);
//			}
			// compute mean for each row and sort dataset accordingly
			if (verbose)
				System.out.println("Now computing means.");
			MeanHelper[] ms = new MeanHelper[selectedData.getRowCount()];
			for (int i = 0; i < selectedData.getRowCount(); i++) {
				DescriptiveStatistics stat = new DescriptiveStatistics(selectedData.getDoubleRowArray(i));
				ms[i] = new MeanHelper(i, stat.getMean());
			}
			Arrays.sort(ms, Collections.reverseOrder());
			sortOrder = new int[ms.length];
			for(int i =0; i < ms.length; i++)
				sortOrder[i] = ms[i].index;
			selectedData.sortRows(sortOrder);
		}
		
		if (selectedData.getRowCount() < countryNumber) {
			System.err.println("not enough countries left");
			for (JVisualizationContainer v : visualizations.containers)
				v.setData(null);
			exportData.setEnabled(false);
			return;
		} else {
			exportData.setEnabled(true);

			// Clean up country names
			for (int i = 0; i < selectedData.getRowCount(); i++) {
				String country = selectedData.getValue(i, 0);
				if (country.startsWith("\"") && country.endsWith("\""))
					selectedData.setValue(i, 0, country.substring(1, country.length() - 1));
			}
			
			

		data = new MatrixData(selectedData, null);
		data.setDataInfo(createMetaDataForCountryIndicators());

		double[] distances = checkDistances(data);
		boolean suitable = false;
		for (int i = 0; i < distances.length; i++) {
			if (distances[i] >= 2.2) suitable = true;
		}
		if (suitable) distanceCheck.setForeground(Color.blue);
		else distanceCheck.setForeground(Color.red);
		distanceCheck.setText("Distances (per column): " + 
				String.format("%.1f | ", distances[0]) +
				String.format("%.1f | ", distances[1]) +
				String.format("%.1f | ", distances[2]) +
				String.format("%.1f | ", distances[3]) +
				String.format("%.1f | ", distances[4]) +
				String.format("%.1f | ", distances[5]) +
				String.format("%.1f | ", distances[6]) +
				String.format("%.1f | ", distances[7]) +
				String.format("%.1f | ", distances[8]) +
				String.format("%.1f | ", distances[9]));
//		ArrayList<String> suitableYears = new ArrayList<String>;
		for (JVisualizationContainer v : visualizations.containers)
			v.setData(data);
			if (!mac)
				exportData.setEnabled(false); // have to reorder
		}
	}
	
	private double[] checkDistances(MatrixData d)
	{
		double[] dist = new double[d.rows];
		double maxScale = d.getAxisMax();
		for (int i = 0; i < d.cols; i++) {
//			double max = d.extractColumn(i, false).computeMax();
			double min = 100;
			double cur;
			for (int j = 0; j < d.rows; j++) {
				for (int k = j+1; k < d.rows; k++) {
					double a = d.getValue(j, i);
					double b = d.getValue(k, i);
					cur = Math.abs(a - b)/maxScale*100;
					min = Math.min(min, cur);
//					System.out.println("col "+ i + " row " + j + " with " + k + " is " + cur);
				}
			}
			dist[i] = min;
		}
		
		
		return dist;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
//		if ("ok".equals(arg0.getActionCommand())) {
//			countryNumber = Integer.valueOf(noOfCountries.getText());
//			yearInterval = Integer.valueOf(yearInter.getText());
//			System.out.println("reducing data set now to " + countryNumber + " countries with " + yearInterval + " year distance.");
//			compute();
//		}
		if("change".equals((arg0.getActionCommand()))) {
			filename = Utils.chooseFiles("./data/csv/backup data sets/", ".csv")[0].getAbsolutePath();
			setFile(filename);
		}
		if ("export".equals(arg0.getActionCommand())) {
			String f = extractFilename(filename); 
			String fn = "./data/datasets/" + f + "-" + countryNumber +"x" + countryNumber;
			if ((new File(fn + ".csv")).exists()) {
				int n = JOptionPane.showConfirmDialog(
					    this,
					    "Overwrite the CSV and metadata files?",
					    "",
					    JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.NO_OPTION)
					fn = "./data/datasets/" + f + "-" + countryNumber +"x" + countryNumber + " new";
					//return;
			}
			MatrixData selectedDataMatrix = data;//new MatrixData(selectedData, data.getDataInfo());
			selectedDataMatrix.write(fn);			
		}
	}
	
	private static String extractFilename(String fn) {
		if (fn.indexOf("/") != -1)
			fn = fn.substring(fn.lastIndexOf("/") + 1); 
		if (fn.indexOf("\\") != -1)
			fn = fn.substring(fn.lastIndexOf("\\") + 1);
		if (fn.indexOf(".csv") != -1)
			fn = fn.substring(0, fn.lastIndexOf(".csv"));
		return fn;
	}
}
