package fr.inria.aviz.physVizEval.jzy3d;

import javax.media.opengl.GL2;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.rendering.lights.Light;

public class CustomLight extends Light {
	
	float brightness = 0.8f; // not sure why but this produces correct rgb colors
	
	public CustomLight(int id, boolean enabled, boolean represented) {
		super(id, enabled, represented);
	}

	public void apply(GL2 gl, Coord3d scale){
		if (!UglyPickingSupport.PICKING) {
			// == Default
			super.apply(gl, scale);
		} else {
			// == Picking mode
			Color a0 = getAmbiantColor();
			Color d0 = getDiffuseColor();
			Color s0 = getSpecularColor();
	        setAmbiantColor(new Color(brightness, brightness, brightness));
	        setDiffuseColor(new Color(0f, 0f, 0f));
	        setSpecularColor(new Color(0f, 0f, 0f));
			super.apply(gl, scale);
			setAmbiantColor(a0);
			setDiffuseColor(d0);
			setSpecularColor(s0);
		}
	}
}
