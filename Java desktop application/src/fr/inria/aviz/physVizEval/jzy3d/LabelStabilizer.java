package fr.inria.aviz.physVizEval.jzy3d;

import java.util.Hashtable;

import org.jzy3d.maths.Coord3d;

public class LabelStabilizer {

	static final int windowSize = 10;
	
	static class DoubleFilter {
		double[] values = new double[windowSize];
		int index = 0;
		
		public void setNewValue(double value) {
			values[index] = value;
			index = (index + 1) % windowSize;
		}
		
		public double getFilteredValue() {
			double sum = 0;
			for (int i=0; i < windowSize; i++)
				sum += values[i];
			return sum / windowSize;
		}
	}
	
	static class Coord3dFilter {
		DoubleFilter xfilter = new DoubleFilter();
		DoubleFilter yfilter = new DoubleFilter();
		DoubleFilter zfilter = new DoubleFilter();
		
		public void setNewValue(Coord3d c) {
			xfilter.setNewValue(c.x);
			yfilter.setNewValue(c.y);
			zfilter.setNewValue(c.z);
		}
		
		public Coord3d getFilteredValue() {
			return new Coord3d (xfilter.getFilteredValue(), yfilter.getFilteredValue(), zfilter.getFilteredValue());
		}
	}
	
	Hashtable<String, Coord3dFilter> labelpos = new Hashtable<String, Coord3dFilter>();
	
	public Coord3d setNewLabelPosition(String label, Coord3d position) {
		Coord3dFilter filter;
		if (!labelpos.containsKey(label)) {
			filter = new Coord3dFilter();
			labelpos.put(label, filter);
		} else {
			filter = labelpos.get(label);
		}
		filter.setNewValue(position);
		return filter.getFilteredValue();
	}

}
