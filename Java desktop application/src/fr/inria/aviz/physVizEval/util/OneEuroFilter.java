package fr.inria.aviz.physVizEval.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author s. conversy from n. roussel c++ version
 */

public class OneEuroFilter {

	static class LowPassFilter {

	    double y, a, s;
	    boolean initialized;

	    void setAlpha(double alpha)  {
	    	if (alpha <= 0)
	    		alpha = 0;
	    	if (alpha > 1)
	    		alpha = 1;
//	        if (alpha <= 0.0 || alpha > 1.0) {
//	            throw new IllegalArgumentException("alpha should be in (0.0., 1.0]");
//	        }
	        a = alpha;
	    }

	    public LowPassFilter(double alpha)  {
	        init(alpha, 0);
	    }

	    public LowPassFilter(double alpha, double initval)  {
	        init(alpha, initval);
	    }

	    private void init(double alpha, double initval)  {
	        y = s = initval;
	        setAlpha(alpha);
	        initialized = false;
	    }

	    public double filter(double value) {
	        double result;
	        if (initialized) {
	            result = a * value + (1.0 - a) * s;
	        } else {
	            result = value;
	            initialized = true;
	        }
	        y = value;
	        s = result;
	        return result;
	    }

	    public double filterWithAlpha(double value, double alpha)  {
	        setAlpha(alpha);
	        return filter(value);
	    }

	    public boolean hasLastRawValue() {
	        return initialized;
	    }

	    public double lastRawValue() {
	        return y;
	    }
	};
	
    double freq;
    double mincutoff;
    double beta_;
    double dcutoff;
    LowPassFilter x;
    LowPassFilter dx;
    double lasttime;
    static double UndefinedTime = -1;

    double alpha(double cutoff) {
    	if (cutoff == 0)
    		return 0;
        double te = 1.0 / freq;
        double tau = 1.0 / (2 * Math.PI * cutoff);
        return 1.0 / (1.0 + tau / te);
    }

    void setFrequency(double f)  {
        if (f <= 0) {
            throw new IllegalArgumentException("freq should be >0");
        }
        freq = f;
    }

    void setMinCutoff(double mc)  {
//        if (mc <= 0) {
//            throw new IllegalArgumentException("mincutoff should be >0");
//        }
        mincutoff = mc;
    }

    void setBeta(double b) {
        beta_ = b;
    }

    void setDerivateCutoff(double dc)  {
        if (dc <= 0) {
            throw new IllegalArgumentException("dcutoff should be >0");
        }
        dcutoff = dc;
    }

    public OneEuroFilter(double freq) {
        init(freq, 1.0, 0.0, 1.0);
    }

    public OneEuroFilter(double freq, double mincutoff)  {
        init(freq, mincutoff, 0.0, 1.0);
    }

    public OneEuroFilter(double freq, double mincutoff, double beta_)  {
        init(freq, mincutoff, beta_, 1.0);
    }

    public OneEuroFilter(double freq, double mincutoff, double beta_, double dcutoff)  {
        init(freq, mincutoff, beta_, dcutoff);
    }

    private void init(double freq,
            double mincutoff, double beta_, double dcutoff) {
        setFrequency(freq);
        setMinCutoff(mincutoff);
        setBeta(beta_);
        setDerivateCutoff(dcutoff);
        x = new LowPassFilter(alpha(mincutoff));
        dx = new LowPassFilter(alpha(dcutoff));
        lasttime = UndefinedTime;
    }

    public double filter(double value)  {
        return filter(value, UndefinedTime);
    }

    public double filter(double value, double timestamp)  {
        // update the sampling frequency based on timestamps
        if (lasttime != UndefinedTime && timestamp != UndefinedTime) {
            freq = 1.0 / (timestamp - lasttime);
        }
        
        lasttime = timestamp;
        // estimate the current variation per second
        double dvalue = x.hasLastRawValue() ? (value - x.lastRawValue()) * freq : 0.0; // FIXME: 0.0 or value?
        double edvalue = dx.filterWithAlpha(dvalue, alpha(dcutoff));
        // use it to update the cutoff frequency
        double cutoff = mincutoff + beta_ * Math.abs(edvalue);
        
//Watcher.watch("filter", "alpha");
//Watcher.update("filter", alpha(cutoff));

        
        // filter the given value
        return x.filterWithAlpha(value, alpha(cutoff));
    }

    public static void main(String[] args)  {
        //randSeed();
        double duration = 10.0; // seconds
        double frequency = 120; // Hz
        double mincutoff = 1.0; // FIXME
        double beta = 1.0;      // FIXME
        double dcutoff = 1.0;   // this one should be ok

        System.out.print(
                "#SRC OneEuroFilter.java" + "\n"
                + "#CFG {'beta': " + beta + ", 'freq': " + frequency + ", 'dcutoff': " + dcutoff + ", 'mincutoff': " + mincutoff + "}" + "\n"
                + "#LOG timestamp, signal, noisy, filtered" + "\n");

        OneEuroFilter f = new OneEuroFilter(frequency,
                mincutoff,
                beta,
                dcutoff);
        for (double timestamp = 0.0; timestamp < duration; timestamp += 1.0 / frequency) {
            double signal = Math.sin(timestamp);
            double noisy = signal + (Math.random() - 0.5) / 5.0;
            double filtered = f.filter(noisy, timestamp);
            System.out.println("" + timestamp + ", "
                    + signal + ", "
                    + noisy + ", "
                    + filtered);
        }
    }
}