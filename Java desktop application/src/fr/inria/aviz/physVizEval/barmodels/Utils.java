package fr.inria.aviz.physVizEval.barmodels;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class Utils {

    /**
     * For debugging.
     * @param map
     * @return
     */
    public static String mapToString(Map<String, Object> map) {
    	return decomposeToString(map, "");
    }
    
    private static String decomposeToString(Object o, String tabs) {
    	if (o instanceof Map) {
    		return mapToString((Map<String, Object>)o, tabs);
    	}
    	if (o instanceof ArrayList) {
    		return arrayToString((ArrayList)o, tabs);
    	}
    	return "";
    }
    
    private static String mapToString(Map<String, Object> map, String tabs) {
    	String s = "";
    	
    	for (String key : map.keySet()) {
    		Object value = map.get(key);
    		s += tabs + key + " -> " + toString(value) + "\n";
    		s += decomposeToString(value, tabs + "   ");
    	}
    	return s;
    }
    
    private static String arrayToString(ArrayList<Object> array, String tabs) {
    	String s = "";
    	int count = Math.min(2, array.size());
    	for (int i=0; i<count; i++) {
    		Object value = array.get(i);
    		s += tabs + "[" + i + "] -> " + toString(value) + "\n";
    		s += decomposeToString(value, tabs + "   ");
    	}
    	if (count < array.size()) {
    		s += tabs + "[" + count + "] -> (" + array.size() + " values...)\n";
    	}
    	return s;
    }
    
    private static String toString(Object o) {
    	if (o instanceof Integer || o instanceof Double || o instanceof String)
    		return o.toString();
    	return o.getClass().getName();
    }
    
    /**
     * Fail-safe version of Plist.load for use in a constructor.
     * 
     * @param filename
     * @return
     */
//	public static Map<String, Object> PListLoad(String filename) {
//		try {
//			return Plist.load(filename);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public static File[] chooseFiles(String directory, final String extension) {
		JFileChooser fc = new JFileChooser(directory);
		fc.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				return "*" + extension;
			}
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(extension);
			}
		});
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            return fc.getSelectedFiles();
        } else {
            return new File[] {};
        }
	}
	
	
	static int map(int x, int min, int max, int newMin, int newMax)
	{
		if (x <= min) return min;
		if (x >= max) return max;
		if (max != 0) return x*min/max;
		return 0;
	}

	static double map(double x, double min, double max, double newMin, double newMax)
	{
		if (x <= min) return min;
		if (x >= max) return max;
		if (max != 0) return x*min/max;
		return 0;
	}

	public static String[] removeFirstElement(String[] input) {
		String[] result = new String[input.length - 1];
		for (int i = 1; i < input.length; i++) {
			result[i-1] = input[i];
		}
		return result;
	}

}
