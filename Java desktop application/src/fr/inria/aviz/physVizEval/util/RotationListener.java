package fr.inria.aviz.physVizEval.util;

import org.jzy3d.maths.Coord3d;
import org.openmali.vecmath2.Quaternion4f;

public interface RotationListener {
	public void rotationEvent(Quaternion4f q);
}
