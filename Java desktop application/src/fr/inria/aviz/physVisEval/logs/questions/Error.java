package fr.inria.aviz.physVisEval.logs.questions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class Error {
	
	
	// --- Comparing unordered lists
	
	/**
	/* Normalized hamming distance between strings:
	/* The number of characters that differ in the two strings divided by the total string length.
	 * http://en.wikipedia.org/wiki/Hamming_distance
	 */
	public static double getNormalizedHammingDistance(String s1, String s2) {
		if (s1.length() != s2.length()) {
			throw(new IllegalArgumentException("The two strings are not of equal length"));
		}
		int n = s1.length();
		int sameCharacters = 0;
		for (int i=0; i<n; i++) {
			if (s1.substring(i, i+1).equals(s2.substring(i, i+1)))
				sameCharacters++;
		}
		return (n - sameCharacters) / (double)n;
	}
	
	/**
	/* Normalized hamming distance between two sets, computed by transforming lists into series
	 * of zeros and ones depending on whether the list items are present or not.
	 */
	public static double getNormalizedHammingDistance(Collection l1, Collection l2, int totalSize) {
		ArrayList allElements = new ArrayList();
		for (Object o : l1)
			if (!allElements.contains(o))
				allElements.add(o);
		for (Object o : l2)
			if (!allElements.contains(o))
				allElements.add(o);
		int curLength = allElements.size();
		for (int i=0; i<totalSize - curLength; i++) {
			allElements.add(new Object()); // add dummy objects to complete the list
		}
		
		String s1 = "";
		String s2 = "";
		for (int i = 0; i < allElements.size(); i++) {
			Object o = allElements.get(i);
			s1 += l1.contains(o) ? "1" : "0";
			s2 += l2.contains(o) ? "1" : "0";
		}
//System.out.println(s1 + "\n" + s2 + "\n");
		return getNormalizedHammingDistance(s1, s2);
	}
	
	/**
	 * Jaccard distance between two sets:
	 * http://en.wikipedia.org/wiki/Jaccard_index
	 * 
	 * The difference between hamming distance is that the total number of selectable items is not taken into account.
	 * I.e., correctly  non-selected items are not counted as right.
	 */
	public static double getJaccardDistance(Set set1, Set set2) {
		return 0; // TODO
	}

	/** 
	 * Normalized Kendall tau distance (or bubble sort distance) between two ordered lists:
	 * http://en.wikipedia.org/wiki/Kendall_tau_distance
	 */
	public static double getNormalizedKendallTauDistance(ArrayList order1, ArrayList order2) {
		boolean warningIssued = false;
		if (order1.size() != order2.size()) {
			//throw(new IllegalArgumentException("The two lists are not of the same size"));
			System.err.println("Warning in getNormalizedKendallTauDistance(): the two lists are not of the same size.");
			warningIssued = true;
			// fix it
			if (order1.size() < order2.size()) {
				ArrayList tmp = order1;
				order1 = order2;
				order2 = tmp;
			}
			int add = order1.size() - order2.size();
			for (int i=0; i<add; i++)
				order2.add("x");
		}
		int n = order1.size();
		int distance = 0;
		for (int i=0; i<n; i++) {
			int ranki1 = i;
			int ranki2 = order2.indexOf(order1.get(i));
			if (ranki2 == -1) {
				//throw(new IllegalArgumentException("The two lists do not contain the same elements"));
				if (!warningIssued)
					System.err.println("Warning in getNormalizedKendallTauDistance(): the two lists do not contain the same elements.");
				warningIssued = true;
			}
			for (int j=i+1; j<n; j++) {
				int rankj1 = j;
				int rankj2 = order2.indexOf(order1.get(j));
				if (rankj2 == -1) {
//					throw(new IllegalArgumentException("The two lists do not contain the same elements"));
					if (!warningIssued)
						System.err.println("Warning in getNormalizedKendallTauDistance(): the two lists do not contain the same elements.");
					warningIssued = true;
				}
				if (ranki2 == -1 || rankj2 == -1) {
					distance++;
				} else if (ranki1 < rankj1 && ranki2 < rankj2) {
					// OK
				} else if (ranki1 > rankj1 && ranki2 > rankj2) {
					// OK
				} else {
					distance++;
				}
			}
		}
		// return normalized distance
		return distance / (n * (n - 1) / 2.0);
	}
}
