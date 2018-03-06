package fr.inria.aviz.physVisEval.logs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

public class CSV {

	private String columnSeparator = ",";
	private String rowSeparator = "\n";
	private String noValueString = "";
	private ArrayList<String> columnNames = new ArrayList<String>();
	private ArrayList<String> columnExplanations = new ArrayList<String>();
	private ArrayList<String[]> rows = new ArrayList<String[]>();
	
	public void clear() {
		rows.clear();
		columnNames.clear();
		columnExplanations.clear();
	}
	
	public void clearRows() {
		rows.clear();
	}

	public void addColumn(String name, String explanation) {
		columnNames.add(name);
		columnExplanations.add(explanation);
	}
	
	public boolean columnExists(String name) {
		return columnNames.contains(name);
	}
	
	public String getColumnName(int index) {
		return columnNames.get(index);
	}
	
	public String getColumnExplanation(int index) {
		return columnExplanations.get(index);
	}

	public int getColumnCount() {
		return columnNames.size();
	}
	
	public int getRowCount() {
		return rows.size();
	}
	
	public void setValue(int row, String columnName, int value) {
		setValue(row, columnName, "" + value);
	}
	
	public void setValue(int row, String columnName, double value) {
		setValue(row, columnName, "" + value);
	}

	public void setValue(int row, String columnName, String value) {
		if (value.contains(","))
			value = "\"" + value + "\"";
		getRowArray(row)[getColumn(columnName)] = value;
	}
	
	public String getValue(int row, int column) {
		String value = getRowArray(row)[column];
		if (value == null)
			return noValueString;
		return value;
	}
	
	public String getValue(int row, String column) {
		int i = columnNames.indexOf(column);
		if (i == -1) {
			System.err.println("  Warning: column " + column + " not found.");
			return null;
		}
		return getValue(row, i);
	}
	
	public void write(String filename, boolean writeExplanations) {
		
		System.out.print("\nWriting " + filename + "... ");
		
		// Write column names
		StringBuilder sb = new StringBuilder();
		for (int c = 0; c < columnNames.size(); c++) {
			sb.append(columnNames.get(c));
			if (c < columnNames.size() - 1)
				sb.append(columnSeparator);
		}
		sb.append(rowSeparator);

		// Write rows
		for (int r = 0; r < rows.size(); r++) {
			for (int c = 0; c < columnNames.size(); c++) {
				sb.append(getValue(r, c));
				if (c < columnNames.size() - 1)
					sb.append(columnSeparator);
			}
			sb.append(rowSeparator);
		}

		writeStringToFile(filename + ".csv", sb.toString());
		
		// Write file with column explanations
		if (writeExplanations) {
			StringBuilder sb2 = new StringBuilder();
			
			sb2.append("CSV file generated " + (new Date()).toString() + "\n\n");
			for (int c = 0; c < columnNames.size(); c++)
				sb2.append((c+1) + " " + columnNames.get(c) + ": " + columnExplanations.get(c) + "\n");
	
			writeStringToFile(filename + ".readme", sb2.toString());	
		}
		
		System.out.println("Done.");
	}
	
	protected String[] getRowArray(int row) {
		int lastRow = rows.size() - 1;
		for (int i = lastRow+1; i <= row; i++)
			rows.add(new String[columnNames.size()]);		
		return rows.get(row);		
	}

	protected int getColumn(String name) {
		for (int i = 0; i < columnNames.size(); i++)
			if (columnNames.get(i).equals(name))
				return i;
		return -1;
	}
	
	protected static void writeStringToFile(String filename, String s) {
		try {
			Writer output = null;
			File file = new File(filename);
			output = new BufferedWriter(new FileWriter(file));
			output.write(s);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public String getColumnSeparator() {
		return columnSeparator;
	}

	public void setColumnSeparator(String columnSeparator) {
		this.columnSeparator = columnSeparator;
	}

	public String getRowSeparator() {
		return rowSeparator;
	}

	public void setRowSeparator(String rowSeparator) {
		this.rowSeparator = rowSeparator;
	}

	public String getNoValueString() {
		return noValueString;
	}

	public void setNoValueString(String noValueString) {
		this.noValueString = noValueString;
	}
	
	public boolean isEmpty() {
		return columnNames.isEmpty();
	}
	
	public void read(String filename) {
		
		System.out.print("\nLoading " + filename + "... ");
		
		clearRows();
		String s = readStringFromFile(filename);
		StringTokenizer linest = new StringTokenizer(s, rowSeparator);
		int row = 0;
		while (linest.hasMoreElements()) {
			String line = linest.nextToken();
			int col = 0;
			StringTokenizer colst = new StringTokenizer(line, columnSeparator);
			while (colst.hasMoreElements()) {
				String cell = colst.nextToken();
				if (cell.trim().startsWith("\"")) {
					while (!cell.trim().endsWith("\"")) {
						cell += colst.nextToken();
					}
				}
				if (row == 0) {
					addColumn(cell, "");
				} else {
					if (col == columnNames.size())
						addColumn("noname", "");
					setValue(row-1, columnNames.get(col), cell);
				}
				col++;
			}
			row++;
		}
		
		System.out.println("Done.");

	}
	
	protected static String readStringFromFile(String filename) {
		try {
			StringBuffer fileData = new StringBuffer(1000);
	        BufferedReader reader = new BufferedReader(
	                new FileReader(filename));
	        char[] buf = new char[1024];
	        int numRead=0;
	        while((numRead=reader.read(buf)) != -1){
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	            buf = new char[1024];
	        }
	        reader.close();
	        return fileData.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		// Write column names
		StringBuilder sb = new StringBuilder();
		for (int c = 0; c < columnNames.size(); c++) {
			sb.append(columnNames.get(c));
			if (c < columnNames.size() - 1)
				sb.append(columnSeparator);
		}
		sb.append(rowSeparator);

		// Write rows
		for (int r = 0; r < rows.size(); r++) {
			for (int c = 0; c < columnNames.size(); c++) {
				sb.append(getValue(r, c));
				if (c < columnNames.size() - 1)
					sb.append(columnSeparator);
			}
			sb.append(rowSeparator);
		}
		
		return sb.toString();
	}
}
