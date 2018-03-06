package fr.inria.aviz.physVizEval.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * For debugging.
 * 
 * @author dragice
 *
 */

public class Watcher extends JFrame {
	
	private static Hashtable<String, Watcher> watchers = new Hashtable<String, Watcher>();
	private static int nextYPosition = 0; 
	
	public static void watch(String title, String... labels) {
		if (!watchers.containsKey(title)) {
			Watcher watcher = new Watcher(title, labels);
			watchers.put(title, watcher);
		}
	}

	public static void update(String title, double... values) {
		Watcher watcher = watchers.get(title);
		if (watcher != null)
			watcher._update(values);
	}

	public static void update(String title, String... values) {
		Watcher watcher = watchers.get(title);
		if (watcher != null)
			watcher._update(values);
	}
	
	public static void setSpecialValues(String title, double... specialValues) {
		Watcher watcher = watchers.get(title);
		watcher._setSpecialValues(specialValues);
	}

//	public static void main(String[] args) {
//		watch("Intrinsic", "X", "Y", "Z");
//		setSpecialValues("Intrinsic", 0, 1);
//		update("Intrinsic", 0, 223.3344, 1000);
//		watch("Extrinsic", "X", "Y", "Z2");
//		update("Extrinsic", "a", "b", "c");
//	}

	JLabel[] fields;
	JPanel[] bgs;
	double[] specialValues = null;
	
	private Watcher(String title, String... labels) {
		super(title);
		int dimensions = labels.length;
		fields = new JLabel[dimensions];
		bgs = new JPanel[dimensions];
		Container c = getContentPane();
		c.setLayout(new GridLayout(dimensions, 2));
		c.setBackground(Color.white);
		for (int i=0; i<dimensions; i++) {
			JPanel bg = new JPanel();
			bg.setBackground(Color.white);
			bg.setLayout(new BorderLayout());
			JLabel value = new JLabel("n/a");
			value.setForeground(Color.black);
			bgs[i] = bg;
			fields[i] = value;
			JLabel text = new JLabel(labels[i] + "  ");
			text.setForeground(Color.gray);
			text.setHorizontalAlignment(JLabel.RIGHT);
			c.add(text);
			bg.add(value);
			c.add(bg);
		}
		pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screen.width - getWidth() - 4, nextYPosition + 13);
		nextYPosition = getY() + getHeight();
		setVisible(true);
		setAlwaysOnTop(true);
	}
	
	void _update(double... values) {
		for (int i=0; i<values.length; i++) {
			fields[i].setText(""+ Math.round(values[i]*100)/100.0);
			if (specialValues != null) {
				boolean special = false;
				for (int j=0; j<specialValues.length; j+=2)
					special |= (values[i] >= specialValues[j] && values[i] <= specialValues[j+1]);
				bgs[i].setBackground(special ? new Color(1f, .6f, .6f) : Color.white);
			}
		}
	}
	
	void _update(String... values) {
		for (int i=0; i<values.length; i++) {
			fields[i].setText(values[i]);
		}
	}
	
	public void _setSpecialValues(double... specialValues) {
		this.specialValues = specialValues;
	}
}

