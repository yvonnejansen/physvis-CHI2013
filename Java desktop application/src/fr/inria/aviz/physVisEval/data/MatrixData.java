package fr.inria.aviz.physVisEval.data;

import java.awt.Color;

import cern.colt.GenericPermuting;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.ObjectFactory2D;
import cern.colt.matrix.ObjectMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import cern.jet.stat.Descriptive;
import fr.inria.aviz.physVizEval.barchart2d.Order;
import fr.inria.aviz.physVizEval.util.CSV;

/**
 * This is a matrix that contains data for a two-dimensional (or 3-D with the value axis) bar chart.
 * Each cell is a bar with a value and a color.
 * 
 * @author dragice
 *
 */
public class MatrixData {
	
    public final int rows, cols;
	private DoubleMatrix2D values;
	private DoubleMatrix2D selection; // contains only 0 and 1 values
	private String[] colLabels;
	private String[] rowLabels;
	private DataInfo dataInfo;
	private boolean ordered = false;
	
	public MatrixData(MatrixData copy) {
		this.rows = copy.rows;
		this.cols = copy.cols;
		this.values = copy.values.copy();
		this.selection = copy.selection.copy();
		colLabels = new String[copy.colLabels.length];
		for (int i=0; i<colLabels.length; i++)
			colLabels[i] = copy.colLabels[i];
		rowLabels = new String[copy.rowLabels.length];
		for (int i=0; i<rowLabels.length; i++)
			rowLabels[i] = copy.rowLabels[i];
		dataInfo = new DataInfo(copy.dataInfo);
	}
	
	public MatrixData(String csvFile, DataInfo defaultDataInfo) {
		this(new CSV(csvFile), DataInfo.load(getMetadataFilename(csvFile), defaultDataInfo));
	}
	
	public MatrixData(CSV csv, DataInfo info) {
		this(csv.getRowCount(), csv.getColumnCount() - 1, info);
		extractDataFromCSV(csv);
	}
	
	private MatrixData(int rows, int cols, DataInfo info) {
		this(DoubleFactory2D.dense.make(rows,cols), null, null, info);
	}
	
	private MatrixData(DoubleMatrix2D mat, String[] clabels, String[] rlabels, DataInfo info) {
        this.rows = mat.rows();
        this.cols = mat.columns();
        this.dataInfo = info;
        values = mat;
        selection = DoubleFactory2D.dense.make(mat.rows(), mat.columns());
        if (clabels == null)
            clabels = new String[this.cols];
        colLabels = clabels;
        if (rlabels == null)
            rlabels = new String[this.rows];
        rowLabels = rlabels;
    }
	
	private void extractDataFromCSV(CSV csv) {
		
		int nbYears = csv.getColumnCount() - 1;
		int nbCountries = csv.getRowCount();
		
        for (int y = 0; y < nbYears; y++) {
        	setColumnLabel(y, csv.getColumnName(y + 1));
        }
        
        for (int c = 0; c < nbCountries; c++) {
        	int cmatrix = c;
        	setRowLabel(cmatrix, csv.getValue(c, 0));
            for (int y = 0; y < nbYears; y++) {
            	try {
            		String v = csv.getValue(c, y + 1);
                	setValue(cmatrix, y, Double.valueOf(v));
            	} catch (NumberFormatException e) {
            		//e.printStackTrace();
            	}
            }
        }            
        
	}
	
	private static String getMetadataFilename(String csvFilename) {
		if (csvFilename.endsWith(".csv"))
			csvFilename = csvFilename.substring(0, csvFilename.length() - 4);
		return csvFilename + ".metadata";
	}
		
	public void setValue(int row, int col, double value) {
	    values.set(row, col, value);
	}
	
	public double getValue(int row, int col) {
	    return values.get(row, col);
	}
	
	public double[] getRow(int row) {
		return values.viewRow(row).toArray();
	}
	
	public Color getColor(int row, int col) {
		if (dataInfo.getColorCoding() == null)
			return Color.lightGray;
	    return (Color) dataInfo.getColorCoding().getColor(this, row, col);
	}
	
	public void setRowLabel(int row, String label) {
		rowLabels[row] = label;
	}
	
	public String getRowLabel(int row) {
	    return rowLabels[row];
	}

	public void setColumnLabel(int col, String label) {
		colLabels[col] = label;
	}
	
	public String getColumnLabel(int col) {
	    return colLabels[col];
	}
	
	public String[] getRowLabels() {
		return rowLabels;
	}
	
	public String[] getColumnLabels() {
		return colLabels;
	}
	
	public MatrixData getInverse(boolean preserveColors) {
		DataInfo dataInfo2 = new DataInfo(dataInfo);
		if (preserveColors && dataInfo2.getColorCoding() != null)
			dataInfo2.setColorCoding(dataInfo2.getColorCoding().getInverse());
	    MatrixData data2 = new MatrixData(values.viewDice(), rowLabels, colLabels, dataInfo2);
	    data2.selection = selection.viewDice();
		return data2;
	}
	
	public MatrixData extractRow(int row, boolean preserveColors) {
		DataInfo dataInfo2 = new DataInfo(dataInfo);
		if (preserveColors && dataInfo2.getColorCoding() != null)
			dataInfo2.setColorCoding(dataInfo2.getColorCoding().extractRow(row));
		MatrixData data2 = new MatrixData(values.viewPart(row, 0, 1, cols), colLabels, null, dataInfo2);
		data2.rowLabels[0] = rowLabels[row];
		for (int c = 0; c < cols; c++)
			data2.setSelected(0, c, isSelected(row, c));
		return data2;
	}
	
	public MatrixData extractColumn(int col, boolean preserveColors) {
		DataInfo dataInfo2 = new DataInfo(dataInfo);
		if (preserveColors && dataInfo2.getColorCoding() != null)
			dataInfo2.setColorCoding(dataInfo2.getColorCoding().extractColumn(col));
		MatrixData data2 = new MatrixData(values.viewPart(0, col, rows, 1), null, rowLabels, dataInfo2);
		data2.colLabels[0] = colLabels[col];
		for (int r = 0; r < rows; r++)
			data2.setSelected(r, 0, isSelected(r, col));
		return data2;
	}
	
    public double computeSum() {
        return values.aggregate(Functions.plus, Functions.identity);
    }
    
    public double computeSumOfSquares() {
        return values.aggregate(Functions.plus, Functions.square);
    }


    public double computeMean() {
	    return computeSum() / (rows * cols);
	}
	
    public double computeMax() {
        return values.aggregate(Functions.max, Functions.identity);
    }
    
    public double computeMin() {
        return values.aggregate(Functions.min, Functions.identity);
    }
    
    public double computeVariance() {
        return Descriptive.variance(rows*cols, computeSum(), computeSumOfSquares());
    }
    
    public double computeStandardDeviation() {
        return Descriptive.standardDeviation(computeVariance());
    }
    
    public boolean areAllRowColorsEqual(int row) {
    	Object c0 = getColor(row, 0);
    	for (int i=1; i<rows; i++)
    		if (!getColor(row, i).equals(c0))
    			return false;
    	return true;
    }
    
    public void reorder() {
        if (ordered) return;
        Order order = new Order();
        DoubleMatrix2D dist = Order.computeDistance(values);
        int[] rowsPerm = order.computeOrdering(dist);
        reorder(rowsPerm);
        ordered = true;
    }
    
    private boolean isPermutation(int[] perm) {
        long mask = (1L << perm.length) - 1; // contain length successive bits to 1
        for (int b : perm) {
            long bit = 1L << b; // bit to test
            if ((mask & bit) == 0)
                return false;
            mask &= ~bit; // set the bit to 0
        }
        return mask == 0;
    }
    
    private boolean isIdentity(int[] perm) {
        for (int i = 0; i < perm.length; i++) 
            if (perm[i] != i) return false;
        return true;
    }
    
    private int[] invertMaybe(int[] perm) {
        int sum = 0;
        for (int i = 1; i < perm.length; i++) {
            sum += perm[i] - perm[i-1];
        }
        if (sum < 0) {
            System.out.println("Inverting permutation to remain consistent");
            for (int i = 0, j = perm.length-1; i < j; i++, j--) {
                int tmp = perm[j];
                perm[j] = perm[i];
                perm[i] = tmp;
            }
        }
        return perm;
    }
    
    private void printLabels() {
        for (int i = 0; i < rowLabels.length; i++) {
            System.out.print(" '");
            System.out.print(rowLabels[i]);
            System.out.print("'");
        }
        System.out.println();
    }
    
    public void reorder(int[] rowsPerm) {
        assert(rowsPerm.length == rows);
        
        if (isIdentity(rowsPerm)) {
            System.out.println("Permutation is identity, ignoring");
            return;
        }
        rowsPerm = invertMaybe(rowsPerm);
        assert(isPermutation(rowsPerm));

        if (rowsPerm != null) {
            System.out.print("Permutation is");
            for (int p : rowsPerm) {
                System.out.print(" "+p);
            }
            System.out.println();
            int[] temp = new int[rowsPerm.length];
            values = Algebra.DEFAULT.permuteRows(values, rowsPerm, temp);
            System.out.print("Labels before:");
            printLabels();
            GenericPermuting.permute(rowLabels, rowsPerm);
            System.out.print("Labels after :");
            printLabels();
        }
    }

	public DataInfo getDataInfo() {
		return dataInfo;
	}

	public void setDataInfo(DataInfo metadata) {
		this.dataInfo = metadata;
	}
	
	public void write(String csvfile) {
		
		// Write values to the csv file 
		CSV csv = new CSV();
		csv.addColumn("Matrix", "");
		for (int c = 0; c < cols; c++)
			csv.addColumn(getColumnLabel(c), "");
		for (int r = 0; r < rows; r++) {
			csv.setValue(r, 0, getRowLabel(r));
			for (int c = 0; c < cols; c++) {
				csv.setValue(r, c + 1, getValue(r, c) + "");
			}
		}
		csv.write(csvfile, false);
		
		// Write metadata
		dataInfo.write(getMetadataFilename(csvfile));

	}
	
	public double getAxisMax() {
		if (dataInfo == null)
			return computeMax();
		return dataInfo.getAxisLabeling().getMaxAxisValue();
	}
	
	// Useful for 3D
	public MatrixData getNormalizedCopy(double maxValue) {
		MatrixData copy = new MatrixData(this);
		double scale = maxValue / getAxisMax();
		for (int r = 0; r < rows; r++)
			for (int c = 0; c < cols; c++)
				copy.setValue(r, c, getValue(r, c) * scale);
		if (dataInfo != null) { 
			AxisLabeling axis = copy.dataInfo.getAxisLabeling();
			axis.scaleValues(scale);
		}
		return copy;
	}
	
	public void setSelected(int row, int col, boolean selected) {
	    selection.set(row, col, selected ? 1 : 0);
	}
	
	public boolean isSelected(int row, int col) {
	    return selection.get(row, col) != 0;
	}
	
	public void unselectAll() {
		for (int r=0; r<selection.rows(); r++)
			for (int c=0; c<selection.columns(); c++)
				selection.set(r, c, 0);
	}
}
