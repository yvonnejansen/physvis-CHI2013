package fr.inria.aviz.physVizEval.dataselector;

import java.util.ArrayList;

import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVisEval.data.Tick;
import fr.inria.aviz.physVizEval.barmodels.Utils;
import fr.inria.aviz.physVizEval.util.CSV;
import org.apache.commons.math.*;
import org.apache.commons.math.util.MathUtils;
public class CheckDataDistances {

	
	CSV data;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new CheckDataDistances();
	}

	public CheckDataDistances()
	{
		data= new CSV();
		String filename = Utils.chooseFiles("./data/datasets", ".csv")[0].getAbsolutePath();
//		data.readSeparatorSafe(filename);
		MatrixData d = new MatrixData(filename, null);
		double[] distances = checkDistances(d);
		System.out.println("minimum distances for each column:");
		for (int i = 0; i < distances.length; i++)
			System.out.println(i + ": " + distances[i]);
		
		System.out.println("Countries that have at least one year below the third tick:");
		System.out.println(checkMinVals(d));
		
		System.out.println(getMinAverage(d));
		int countryIndex = 3;
		double [] minMax = getMinMaxForIndex(d, countryIndex);
		System.out.println("min and max for " + countryIndex + "th country: " + d.getRowLabel(countryIndex) + " " + minMax[0] + ", " + minMax[1]);
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
	
	private ArrayList<String> checkMinVals(MatrixData d)
	{
		int tickUsed = 2;
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Tick> ticks = d.getDataInfo().getAxisLabeling().ticks;
		double realTickValue;// = Double.parseDouble(ticks.get(tickUsed).label);
		double axeVal = d.getDataInfo().getAxisLabeling().getMinAxisValue() + tickUsed * d.getDataInfo().getAxisLabeling().getFirstTickStep();
		realTickValue = axeVal;
		double factor = realTickValue / axeVal;
		names.add(tickUsed + "rd tick at " + realTickValue + "\n");
		for (int i = 0; i < d.rows; i++)
		{
			for (int j = 0; j < d.cols; j++) {
				if (d.getValue(i, j) < axeVal) {
					names.add(d.getRowLabel(i) + " " + (axeVal - d.getValue(i, j)) * factor);
					j = d.cols;
				}
			}
		}
		return names;
	}

	private String getMinAverage(MatrixData d)
	{
		double minAvg = 10000000;
		String minYear = "";
		for (int i = 0; i < d.cols; i++) {
			double avg = 0;
			for (int j =0; j < d.rows; j++) {
				avg += d.getValue(j, i);
			}
			avg /= d.rows;
			minAvg = Math.min(minAvg, avg);
			if (avg == minAvg)
			{
				minYear = d.getColumnLabel(i);
			}
		}
		return ("year with min average: " + minYear + " " + minAvg);
	}
	
	private String[] getSortedAverages(MatrixData d)
	{
		String[] avgs = new String[d.cols];
		for (int i = 0; i < d.cols; i++) {
//			double[] year = d.getC
		}
		return avgs;
	}
	
	private double[] getMinMaxForIndex(MatrixData d, int index)
	{
		double[] minMax = new double[2];
		double[] country = d.getRow(index);
		ArrayList<Tick> ticks = d.getDataInfo().getAxisLabeling().ticks;
		double realTickValue; // = Double.parseDouble(ticks.get(1).label);
		double axeVal = d.getDataInfo().getAxisLabeling().getMinAxisValue() +  d.getDataInfo().getAxisLabeling().getFirstTickStep();
		realTickValue = axeVal;
		double factor = realTickValue / axeVal;
		double min = 100000000;
		double max = 0;
		for (int i = 0; i < country.length; i++)
		{
			min = Math.min(min, country[i] * factor);
			max = Math.max(max, country[i] * factor);
		}
		minMax[0] = min;
		minMax[1] = max;

		return minMax;
	}
		
}
