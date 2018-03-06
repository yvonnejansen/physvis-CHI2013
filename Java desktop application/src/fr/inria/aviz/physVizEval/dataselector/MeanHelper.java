package fr.inria.aviz.physVizEval.dataselector;

public class MeanHelper implements Comparable<MeanHelper> {

	int index;
	double mean;
	
	public MeanHelper(int ind, double m)
	{
		this.mean = m;
		this.index = ind;
	}
	@Override
	public int compareTo(MeanHelper o) {
		// TODO Auto-generated method stub
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;

	    if (this.mean < o.mean) return BEFORE;
	    if (this.mean > o.mean) return AFTER;
	    else return EQUAL;
	}

}
