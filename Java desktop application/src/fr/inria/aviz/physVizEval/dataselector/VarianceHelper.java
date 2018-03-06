package fr.inria.aviz.physVizEval.dataselector;

public class VarianceHelper implements Comparable<VarianceHelper>{
	int index = -1;
	double variance = 0;
	String[] row;
	
	public VarianceHelper (int ind, double var, String [] r)
	{
		this.index = ind;
		this.variance = var;
		this.row = r;
	}

	@Override
	public int compareTo(VarianceHelper arg0) {
		// TODO Auto-generated method stub
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;

	    if (this.variance < arg0.variance) return BEFORE;
	    if (this.variance > arg0.variance) return AFTER;
	    else return EQUAL;
	}
	
}
