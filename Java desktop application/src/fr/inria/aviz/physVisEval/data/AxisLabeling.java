package fr.inria.aviz.physVisEval.data;

import java.awt.List;
import java.io.File;
import java.util.ArrayList;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.sittingbull.gt.util.NiceStepSizeGenerator;
import org.sittingbull.gt.util.XWilkinson;

/**
 * An axis with ticks and labels.
 * 
 * Uses Talbot and al for placing tick marks and tries to format text labels
 * nicely.
 * 
 * @author dragice
 * 
 */
@Root
public class AxisLabeling {

	@Element
	private double minAxisValue;
	
	@Element
	private double maxAxisValue;

	@ElementList
	public final ArrayList<Tick> ticks;

	/**
	 * Creates a labeled axis with the specified number of desired ticks and unit.
	 * 
	 * @param minDataValue
	 * @param maxDataValue
	 * @param desiredNumberOfTicks
	 */
	public AxisLabeling(double minDataValue, double maxDataValue, int desiredNumberOfTicks, double unitDivider, String unit) {

		if (desiredNumberOfTicks == 0) {
			minAxisValue = minDataValue;
			maxAxisValue = maxDataValue;
			ticks = new ArrayList<Tick>();
			return;
		}
		
		XWilkinson x = new XWilkinson(new NiceStepSizeGenerator());
		x.setLooseFlag(false);
		XWilkinson.Label wilkinsonLabeling = x.search(minDataValue / unitDivider, maxDataValue / unitDivider, desiredNumberOfTicks);

		minAxisValue = wilkinsonLabeling.min * unitDivider;
		maxAxisValue = wilkinsonLabeling.max * unitDivider;
		double tickStep = wilkinsonLabeling.step * unitDivider;
		int numberOfTicks = (int) Math.round((maxAxisValue - minAxisValue) / tickStep) + 1;

		int decimalPlaces = getDecimalPlaces(tickStep / unitDivider);
		ticks = new ArrayList<Tick>();
		for (int i = 0; i < numberOfTicks; i++) {
			double value = minAxisValue + i * tickStep;
			Tick t = new Tick();
			t.value = value;
			t.label = String.format("%,." + decimalPlaces + "f", value / unitDivider) + (unit != null && (unit.length() > 0) ? (" " + unit) : "");
			ticks.add(t);
		}
	}
	
	/**
	 * Constructor for XML deserialization
	 */
	private AxisLabeling(@Element(name = "minAxisValue") double minAxisValue, @Element(name = "maxAxisValue") double maxAxisValue, @ElementList(name = "ticks") ArrayList<Tick> ticks) {
		this.minAxisValue = minAxisValue;
		this.maxAxisValue = maxAxisValue;
		this.ticks = ticks;
	}
	
	public AxisLabeling(AxisLabeling copy) {
		this.minAxisValue = copy.minAxisValue;
		this.maxAxisValue = copy.maxAxisValue;
		ticks = new ArrayList<Tick>();
		for (Tick t : copy.ticks) {
			Tick t2 = new Tick();
			t2.value = t.value;
			t2.label = t.label;
			ticks.add(t2);
		}
	}

	/**
	 * Returns the relative position of the value on the axis, between 0 and 1.
	 */
	public double getPositionOnAxis(double value) {
		return (value - minAxisValue) / (maxAxisValue - minAxisValue);
	}

	/**
	 * Returns the minimum value on the axis.
	 */
	public double getMinAxisValue() {
		return minAxisValue;
	}

	/**
	 * Returns the maximum value of the axis.
	 */
	public double getMaxAxisValue() {
		return maxAxisValue;
	}

	/**
	 * Returns the ticks created on the axis, with the corresponding labels.
	 */
	public ArrayList<Tick> getTicks() {
		return ticks;
	}

	/**
	 * Returns the number of ticks on this axis.
	 * 
	 * @return
	 */
	public int getNumberOfTicks() {
		return ticks.size();
	}

	/**
	 * Returns the distance between the first two ticks in data units.
	 * 
	 * In this implementation of LabeledAxis ticks are evenly spaced, but you
	 * should not assume so. Use getTicks() instead.
	 * 
	 * @return
	 */
	public double getFirstTickStep() {
		if (ticks.size() >= 2)
			return ticks.get(1).value - ticks.get(0).value;
		return 1;
	}

	/**
	 * Returns the difference between the max value and the min value on this
	 * axis.
	 * 
	 * @return
	 */
	public double getAxisRange() {
		return maxAxisValue - minAxisValue;
	}

	//

	private static int getDecimalPlaces(double value) {
		int places = 0;
		while (true) {
			int divider = (int) Math.pow(10, places);
			if (value * divider == (int) (value * divider))
				break;
			places++;
		}
		return places;
	}
	
	// scale the values but not the labels
	public void scaleValues(double scale) {
		for (Tick tick : ticks)
			tick.value *= scale;
		maxAxisValue *= scale;
		minAxisValue *= scale;
	}
	
	public void setNewMaxAxisValue(double maxAxisValue) {
		this.maxAxisValue = maxAxisValue;
	}

	public String toString() {
		String s = "";
		s += "\n\nAxis " + super.toString() + ": \n";
		s += "min: " + minAxisValue + " max: " + maxAxisValue + " step: "
				+ getFirstTickStep() + " ticks: " + getNumberOfTicks() + "\n";
		for (Tick t : ticks) {
			s += t.value + " (" + t.label + ") ";
		}
		return s;
	}

}
