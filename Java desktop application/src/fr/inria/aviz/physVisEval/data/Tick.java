package fr.inria.aviz.physVisEval.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

public class Tick {
	@Attribute
	public double value;
	
	@Attribute
	public String label;
}
