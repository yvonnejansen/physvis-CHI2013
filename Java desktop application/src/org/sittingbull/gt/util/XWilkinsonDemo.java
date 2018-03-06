package org.sittingbull.gt.util;

import org.sittingbull.gt.util.NiceStepSizeGenerator;
import org.sittingbull.gt.util.XWilkinson;

public class XWilkinsonDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		XWilkinson x = new XWilkinson(new NiceStepSizeGenerator());

	
		// Examples taken from the paper pg 6, Fig 4
		x.setLooseFlag(true);
		System.out.println(x.search(-98.0, 18.0, 3).toString());
		x.setLooseFlag(false);
		System.out.println(x.search(-98.0, 18.0, 3).toString());

		System.out.println();
		
		x.setLooseFlag(true);
		System.out.println(x.search(-1.0, 200.0, 3).toString());
		x.setLooseFlag(false);
		System.out.println(x.search(-1.0, 200.0, 3).toString());

		System.out.println();
		
		x.setLooseFlag(true);
		System.out.println(x.search(119.0, 178.0, 3).toString());
		x.setLooseFlag(false);
		System.out.println(x.search(119.0, 178.0, 3).toString());

		System.out.println();
		
		x.setLooseFlag(true);
		System.out.println(x.search(-31.0, 27.0, 4).toString());
		x.setLooseFlag(false);
		System.out.println(x.search(-31.0, 27.0, 3).toString());

		System.out.println();
		
		x.setLooseFlag(true);
		System.out.println(x.search(-55.45, -49.99, 2).toString());
		x.setLooseFlag(false);
		System.out.println(x.search(-55.45, -49.99, 3).toString());
		
		System.out.println();
		x.setLooseFlag(false);
		System.out.println(x.search(0, 100, 2).toString());
		System.out.println(x.search(0, 100, 3).toString());
		System.out.println(x.search(0, 100, 4).toString());
		System.out.println(x.search(0, 100, 5).toString());
		System.out.println(x.search(0, 100, 6).toString());
		System.out.println(x.search(0, 100, 7).toString());
		System.out.println(x.search(0, 100, 8).toString());
		System.out.println(x.search(0, 100, 9).toString());
		System.out.println(x.search(0, 100, 10).toString());
	}
}