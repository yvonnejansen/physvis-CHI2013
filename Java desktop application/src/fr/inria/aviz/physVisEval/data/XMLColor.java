package fr.inria.aviz.physVisEval.data;

import java.awt.Color;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * We have to create this class because the AWT Color class does not have support for simpleframework.xml serialization.
 * @author dragice
 */
@Root(name = "color")
class XMLColor {

	@Attribute
	private int r;

	@Attribute
	private int g;

	@Attribute
	private int b;

	@Attribute
	private int a;
	
	XMLColor() {
		
	}
	
	XMLColor(Color c) {
		this.r = c.getRed();
		this.g = c.getGreen();
		this.b = c.getBlue();
		this.a = c.getAlpha();
	}
	
	Color toColor() {
		return new Color(r, g, b, a);
	}
	

}
