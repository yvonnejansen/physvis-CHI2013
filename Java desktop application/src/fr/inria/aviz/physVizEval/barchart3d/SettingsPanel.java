package fr.inria.aviz.physVizEval.barchart3d;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import org.jzy3d.bridge.awt.DoubleBufferedPanelAWT;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;

import fr.inria.aviz.physVizEval.jzy3d.stereo.StereoCamera;

public class SettingsPanel extends java.awt.Frame implements ChangeListener{

	JSlider far = new JSlider(JSlider.HORIZONTAL, 10,1200,1200);
	JLabel farLabel = new JLabel();
	JSlider near = new JSlider(JSlider.HORIZONTAL, -500, 500,225);
	JLabel nearLabel = new JLabel();
//	JSlider dist = new JSlider(JSlider.HORIZONTAL, -100, 500, 1);
//	JLabel distLengthLabel = new JLabel();
	JSlider focalLength = new JSlider(JSlider.HORIZONTAL, 1, 200, 200);
	JLabel focalLengthLabel = new JLabel();
	JSlider fov = new JSlider(JSlider.HORIZONTAL, 5, 200, 55);
	JLabel fovLabel = new JLabel();
	JSlider modelDistance = new JSlider(JSlider.HORIZONTAL, -1000, 1500, 418);
	JLabel modelDistanceLabel = new JLabel();
	JSlider parallaxPlane = new JSlider(JSlider.HORIZONTAL, -1500, 1000, -189);
	JLabel parallaxPlaneLabel = new JLabel();
	StereoCamera cam;
	
	public SettingsPanel (Chart chart, Rectangle maximizedBounds, String title) {
		super();

		far.addChangeListener(this);
//		dist.addChangeListener(this);
		near.addChangeListener(this);
		focalLength.addChangeListener(this);
		fov.addChangeListener(this);
		modelDistance.addChangeListener(this);
		parallaxPlane.addChangeListener(this);
		setLayout(new GridLayout(6,3));
		this.cam = (StereoCamera)chart.getView().getCamera();
		this.add(new JLabel("Far distance:"));
		this.add(far);
		this.add(farLabel);
		this.add(new JLabel("Near distance:"));
		this.add(near);
		this.add(nearLabel);
//		this.add(new JLabel("Camera distance:"));
//		this.add(dist);
//		this.add(distLengthLabel);
		this.add(new JLabel("Focal length:"));
		this.add(focalLength);
		this.add(focalLengthLabel);
		this.add(new JLabel("Field of view:"));
		this.add(fov);
		this.add(fovLabel);
		this.add(new JLabel("Distance model to zero parallax:"));
		this.add(modelDistance);
		this.add(modelDistanceLabel);
		this.add(new JLabel("Z of parallax plane:"));
		this.add(parallaxPlane);
		this.add(parallaxPlaneLabel);
		
		this.setBounds(maximizedBounds);
		this.setVisible(true);
		System.out.println("created panel");
		cam.setFar(far.getValue());
		cam.setNear(near.getValue());
		cam.setFocalLength(focalLength.getValue());
		cam.setFOV(fov.getValue());
		cam.setModelDistance(modelDistance.getValue());
		cam.setParallaxPlane(parallaxPlane.getValue());

	}
	
//	@Override
//	public void draw(Graphics g) {
//		// TODO Auto-generated method stub
//
//	}
//	
	public void stateChanged(ChangeEvent e) {
				
		JSlider source = (JSlider)e.getSource();
		String value = String.valueOf(source.getValue());
		if (source.equals(far)) {
			cam.setFar(source.getValue());
			farLabel.setText(value);
		}
//		if (source.equals(dist)) {
//			cam.setCameraDistance(source.getValue());
//			distLengthLabel.setText(value);
//		}
		if (source.equals(focalLength)) {
			cam.setFocalLength(source.getValue());
			focalLengthLabel.setText(value);
		}
		if (source.equals(fov)) {
			cam.setFOV(source.getValue());
			fovLabel.setText(value);
		}
		if (source.equals(modelDistance)) {
			cam.setModelDistance(source.getValue());
			modelDistanceLabel.setText(value);
		}
		if (source.equals(parallaxPlane)) {
			cam.setParallaxPlane(source.getValue());
			parallaxPlaneLabel.setText(value);
		}
		if (source.equals(near)) {
			cam.setNear(source.getValue());
			nearLabel.setText(value);
		}
		
 		
	}

}
