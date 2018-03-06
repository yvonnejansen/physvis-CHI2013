package fr.inria.aviz.physVizEval.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.ArrayUtils;

public class CSV {

	private String columnSeparator = ",";
	private String rowSeparator = "\r";
	private String noValueString = "n/a";
	private ArrayList<String> columnNames = new ArrayList<String>();
	private ArrayList<String> columnExplanations = new ArrayList<String>();
	private ArrayList<String[]> rows = new ArrayList<String[]>();
	private boolean verbose = false;
	
	public CSV() {
		
	}
	
	public CSV(String filename) {
		this();
		readSeparatorSafe(filename);
	}
		
	public CSV(CSV copy) {
		this();
		this.columnSeparator = copy.columnSeparator;
		this.rowSeparator = copy.rowSeparator;
		this.noValueString = copy.noValueString;
		for (String s : copy.columnNames)
			columnNames.add(s);
		for (String s : copy.columnExplanations)
			columnExplanations.add(s);
		for (String[] sa : copy.rows) {
			String[] sa2 = new String[sa.length];
			for (int i=0; i < sa.length; i++)
				sa2[i] = sa[i];
			rows.add(sa2);
		}
	}
	
	public void clear() {
		rows.clear();
		columnNames.clear();
		columnExplanations.clear();
	}
	
	public void clearRows() {
		rows.clear();
	}
	
	public void clearRow(int row) {
		rows.remove(row);
	}
	
	public void clearRow(String[] row) {
		rows.remove(row);
	}

	public void sortRows(int[] order) {
		ArrayList<String[]> sortedList = new ArrayList<String[]>();
		for (int i = 0; i < order.length; i++) {
			sortedList.add(rows.get(order[i]));
		}
		rows = sortedList;
	}
	
	public void addColumn(String name, String explanation) {
		columnNames.add(name);
		columnExplanations.add(explanation);
	}
	
	public void removeColumn(String name) {
		int i = getColumn(name);
		ArrayList<String[]> newRows = new ArrayList<String[]>();
		for (String[] row: rows) {
			newRows.add(ArrayUtils.remove(row, i));
		}
		columnNames.remove(name);
		rows = newRows;
	}
	
	public void removeColumns(String[] list)
	{
		for (String name:list) {
			removeColumn(name);
		}
	}
	
	public void removeAllColumnsExcept(String[] list)
	{
		ArrayList<String> names = (ArrayList<String>) columnNames.clone();
		for (String name:columnNames) {
			if (ArrayUtils.contains(list, name) || getColumn(name) == 0) {
				names.remove(name);
			}
		}
		String[] remaining = new String[names.size()];
		if (verbose)
			System.out.println("now removing columns for " + names);
		removeColumns(names.toArray(remaining));
	}
	
	public void removeRowsWithMissingValues()
	{
		ArrayList<Integer> indeces = new ArrayList<Integer>();
		for (int i =0; i < getRowCount(); i++) {
			String[] myRow = getRowArray(i);
			int j = 0;
			while(j < myRow.length) {
				if (myRow[j] == null)
				{
					if (verbose)
						System.out.println("removing: " + myRow[0] + " due to col " + j);
					indeces.add(i);
					j = myRow.length;
				}
				j++;
			}
		}
		for (int i = indeces.size() - 1; i >= 0 ; i--)
		{
			clearRow(indeces.get(i));
		}
	}
	
	public void removeRowsWithNonNumericalValues(int firstColumnToTest)
	{
		ArrayList<Integer> indeces = new ArrayList<Integer>();
		for (int i =0; i < getRowCount(); i++) {
			String[] myRow = getRowArray(i);
			int j = firstColumnToTest;
			while(j < myRow.length) {
				boolean hasNumericalValue = false;
				if (myRow[j] != null) {
					try {
						Double.valueOf(myRow[j]);
						hasNumericalValue = true;
					} catch (Exception e) {
					}
				}
				if (!hasNumericalValue)
				{
					if (verbose)
						System.out.println("removing: " + myRow[0] + " due to col " + j);
					indeces.add(i);
					j = myRow.length;
				}
				j++;
			}
		}
		for (int i = indeces.size() - 1; i >= 0 ; i--)
		{
			clearRow(indeces.get(i));
		}
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
		getRowArray(row)[getColumn(columnName)] = value;
	}
	
	public void setValue(int row, int column, String value) {
		getRowArray(row)[column] = value;
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
	
	public int columnIndex(String column) {
		return columnNames.indexOf(column);
	}
	
	public void write(String filename, boolean includeColumnExplanations) {
		
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
		if (includeColumnExplanations) {
			StringBuilder sb2 = new StringBuilder();
			
			sb2.append("CSV file generated " + (new Date()).toString() + "\n\n");
			for (int c = 0; c < columnNames.size(); c++)
				sb2.append((c+1) + " " + columnNames.get(c) + ": " + columnExplanations.get(c) + "\n");
	
			writeStringToFile(filename + ".readme", sb2.toString());
		}
		
		System.out.println("Done.");
	}
	
	public String[] getRowArray(int row) {
		int lastRow = rows.size() - 1;
		for (int i = lastRow+1; i <= row; i++)
			rows.add(new String[columnNames.size()]);		
		return rows.get(row);		
	}
	
	public double[] getDoubleRowArray(int row) {
		ArrayList<Double> doubleList = new ArrayList<Double>();
		String [] myRow = rows.get(row);
		for (int i = 1; i < myRow.length; i++) {
			//if (myRow[i] != null && !myRow[i].equals(noValueString) && !"-".equals(myRow[i]) && "0".equals(myRow[i]) && !Double.valueOf(myRow[i]).isNaN()) 
			//{
				doubleList.add(Double.valueOf(myRow[i]));
			//}
			//else return null;
		}
		// make ArrayList into double[]
		double[] ar = new double[doubleList.size()];
		for (int i = 0; i < ar.length; i++) {
			ar[i] = doubleList.get(i);
		}
		return ar;
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
	
	public void readSeparatorSafe(String filename) {
		// FIXME
		clear();
		setRowSeparator("\r");
		read(filename);
		if (getRowCount() == 0) {
			clear();
			setRowSeparator("\n");
			read(filename);
		}
	}
	
	public void read(String filename) {
		
		//System.out.print("\nLoading " + filename + "... ");
		
		clearRows();
		String s = readStringFromFile(filename);
		StringTokenizer linest = new StringTokenizer(s, rowSeparator);
		int row = 0;
		while (linest.hasMoreElements()) {
			String line = linest.nextToken();
			int col = 0;
			String[] colst = splitTotokens(line, columnSeparator);
			for (int i=0; i<colst.length; i++) {
				String cell = colst[i];
				if (cell.trim().startsWith("\"")) {
					while (!cell.trim().endsWith("\"")) {
						i++;
						cell += colst[i];
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
		
		//System.out.println("Done. Loaded " + getColumnCount() + " columns and " + getRowCount() + " rows.");

	}
	
	/**
	 * Fixes StringTokenizer.
	 * 
	 * @param line
	 * @param delim
	 * @return
	 */
	public static String[] splitTotokens(String line, String delim){
		  String s = line;
		  int i = 0;

		  while (s.contains(delim)) {
		      s = s.substring(s.indexOf(delim) + delim.length());
		      i++;
		  }
		  String token = null;
		  String remainder = null;
		  int count;
		  if (line.endsWith(delim)) {
			  count = i;
		  } else {
			  count = i+1;
		  }
		  String[] tokens = new String[count];

		  for (int j = 0; j < i; j++) {
		        token = line.substring(0, line.indexOf(delim));
		        //System.out.print("#" + token + "#");
		        tokens[j] = token;
		        remainder = line.substring(line.indexOf(delim) + delim.length());
		        //System.out.println("#" + remainder + "#");

		        line = remainder;
		    }
		  if (count == i+1)
			  tokens[i] = line;

		  return tokens;
	}
	
	static StringBuffer fileData = new StringBuffer(1000);
	
	protected static String readStringFromFile(String filename) {
		
		BufferedInputStream bin = null;
		fileData.delete(0, fileData.length());
		
		try {
			InputStream fin = new FileInputStream(filename);
			if (filename.endsWith(".gz"))
				fin = new GZIPInputStream(fin);
			bin = new BufferedInputStream(fin);
           
            byte[] contents = new byte[1024];
            int bytesRead=0;
            while( (bytesRead = bin.read(contents)) != -1){
                String s = new String(contents, 0, bytesRead);
                fileData.append(s);
            }
		} catch (Exception e) {
			e.printStackTrace();
			try {
				if(bin != null)
					bin.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			return null;
		}
        
		try {
			if(bin != null)
				bin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileData.toString();
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
	
	public String[] getColumnArray(int column)
	{
		String[] ar = new String[rows.size()];
		for (int i = 0; i < rows.size(); i++) {
			ar[i] = rows.get(i)[column];
		}
		return ar;
	}
	
	public String[] getColumnNames()
	{
		ArrayList<String> nameList = (ArrayList<String>) columnNames.clone();
		nameList.remove(0);
		String[] names = new String[nameList.size()]; 
		nameList.toArray(names);
		return names;
	}
	
	public double getMaxValue(int[] lines) {
		double max = 0, val = 0;
		for (int i = 0; i < lines.length; i++) {
			String[] row = getRowArray(lines[i]);
			for (int j = 0; j < row.length; j++) {
			val = Double.valueOf(row[j]);
				if (val != Double.NaN)
				{
					max = Math.max(max, val);
				}
			}
		}
		return max;
	}
	
	public double getMaxValue() {
		double max = 0, val = 0;
		for (int i = 0; i < getRowCount(); i++) {
			String[] row = getRowArray(i);
			for (int j = 1; j < row.length; j++) {
			val = Double.valueOf(row[j]);
				if (val != Double.NaN)
				{
					max = Math.max(max, val);
				}
			}
		}
		return max;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
}
