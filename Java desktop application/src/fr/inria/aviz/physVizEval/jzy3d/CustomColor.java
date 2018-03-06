package fr.inria.aviz.physVizEval.jzy3d;

import org.jzy3d.colors.Color;

public class CustomColor extends Color {

	public CustomColor(java.awt.Color c) {
		super(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		// TODO Auto-generated constructor stub
	}

	public CustomColor(float r, float g, float b) {
		super(r, g, b);
		// TODO Auto-generated constructor stub
	}

	public CustomColor(int r, int g, int b) {
		super(r, g, b);
		// TODO Auto-generated constructor stub
	}

	public CustomColor(float r, float g, float b, float a) {
		super(r, g, b, a);
		// TODO Auto-generated constructor stub
	}

	public CustomColor(int r, int g, int b, int a) {
		super(r, g, b, a);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Color))
			return false;
		Color c = (Color)o;
		return c.r == r && c.g == g && c.b == b && c.a == a;
	}

}
