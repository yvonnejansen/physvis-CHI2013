package fr.inria.aviz.physVisEval.logs.kinematics;

import java.io.File;
import java.util.ArrayList;

import org.openmali.vecmath2.AxisAngle3f;
import org.openmali.vecmath2.Quaternion4f;

import fr.inria.aviz.physVizEval.util.CSV;

public class Kinematics {

	public static final double MAX_EVENT_INTERVAL = 50.0 / 1000.0; // 50 Hz
	public static final int SMOOTHING_ITERATIONS = 0;
	
	public static class RotationEvent {
		public Quaternion4f quaternion;
		public double time;
		// derived values
		public double speed;
		public double qacceleration;
		public double acceleration;
		public double deltaAngle;

		public RotationEvent(double t, float a, float b, float c, float d) {
			this.time = t;
			this.quaternion = new Quaternion4f(a, b, c, d);
		}
		public RotationEvent(double t, RotationEvent ev) {
			this.time = t;
			this.quaternion = new Quaternion4f(ev.quaternion);
		}
	}
	
	// Necessary and sufficient information for identifying the trial
	String username;
	int trial;
	int question;
	
	// Additional data for display/debug
	String filename;
	String condition;
	String dataset;
	double completionTime = -1;
	double distanceTraveled = 0;
	double averageSpeed = 0;
	
	// The rotation events
	ArrayList<RotationEvent> events = new ArrayList<RotationEvent>();
	ArrayList<RotationEvent> events_smoothed = new ArrayList<RotationEvent>();
	
	Kinematics() {
		// use KinematicsLoader.load()
	}
	
	public void processData() {

		RotationEvent prev_ev = null;

		// First, add an event at zero
		RotationEvent ev2 = new RotationEvent(0, events.get(0));
		events.add(0, ev2);
		
		// Maybe add the last event (for mouse)
		if (completionTime != -1 && completionTime > events.get(events.size() - 1).time) {
			RotationEvent ev3 = new RotationEvent(completionTime, events.get(events.size() - 1));
			events.add(ev3);
		}
		
		// Fill missing events
		ArrayList<RotationEvent> events_filled = new ArrayList<RotationEvent>();
		prev_ev = null;
		for (RotationEvent ev : events) {
			if (prev_ev != null) {
				double dt = ev.time - prev_ev.time;
				if (dt == 0)
					continue;
				int copies = 1;
				if (dt > MAX_EVENT_INTERVAL) {
					// duplicate the event (mostly happens for mouse data)
					copies = (int)Math.ceil(dt / MAX_EVENT_INTERVAL);
				}
				events_filled.add(prev_ev);
				double fill_dt = dt / copies; 
				for (int i = 1; i < copies; i++) {
					double t = prev_ev.time + i * fill_dt;
					events_filled.add(new RotationEvent(t, prev_ev));
				}
			}
			prev_ev = ev;
		}
		events_filled.add(events.get(events.size() - 1)); // add last event
		events = events_filled;

		// Compute instantaneous speed
		prev_ev = null;
		for (RotationEvent ev : events) {
			if (prev_ev == null) {
				ev.speed = 0;
				ev.deltaAngle = 0;
			} else {
				ev.deltaAngle = getAngle(prev_ev.quaternion, ev.quaternion);
				ev.speed = ev.deltaAngle / (ev.time / prev_ev.time);
				
			}
			prev_ev = ev;
		}
		
		// Smooth and recenter speed data (moving average of 2 samples)
		prev_ev = null;
		for (RotationEvent ev : events) {
			if (prev_ev != null)
				prev_ev.speed = (prev_ev.speed + ev.speed) / 2.0;
			prev_ev = ev;
		}
		
		// Smooth data
		events_smoothed = events;
		for (int it = 0; it < SMOOTHING_ITERATIONS; it++) {
			ArrayList<RotationEvent> tmp_events_smoothed = new ArrayList<RotationEvent>();
			prev_ev = null;
			Quaternion4f avgq = new Quaternion4f();
			for (RotationEvent ev : events_smoothed) {
				if (prev_ev == null) {
					// first event
					prev_ev = ev;
					tmp_events_smoothed.add(ev);
				} else {
					if (prev_ev.quaternion.equals(ev.quaternion)) {
						avgq.set(prev_ev.quaternion);
					} else {
						avgq.interpolateSLERP(prev_ev.quaternion, ev.quaternion, 0.5f);
					}
					tmp_events_smoothed.add(new RotationEvent((prev_ev.time + ev.time)/2.0, avgq.a(), avgq.b(), avgq.c(), avgq.d()));
				}
				prev_ev = ev;
			}
			events_smoothed = tmp_events_smoothed;
		}
		
		// Compute instantaneous speed
		prev_ev = null;
		for (RotationEvent ev : events_smoothed) {
			if (prev_ev == null) {
				ev.speed = 0;
			} else {
				ev.speed = getAngle(prev_ev.quaternion, ev.quaternion) / (ev.time / prev_ev.time);
			}
			prev_ev = ev;
		}
		
		// Smooth and recenter speed data (moving average of 2 samples)
		prev_ev = null;
		for (RotationEvent ev : events_smoothed) {
			if (prev_ev != null)
				prev_ev.speed = (prev_ev.speed + ev.speed) / 2.0;
			prev_ev = ev;
		}
		
		// Compute instantaneous quaternion acceleration
		prev_ev = null;
		RotationEvent prev_prev_ev = null;
		for (RotationEvent ev : events_smoothed) {
			if (prev_ev == null || prev_prev_ev == null) {
				ev.qacceleration = 0;
			} else {
				ev.qacceleration = getAccelerationAngle(prev_prev_ev.quaternion, prev_ev.quaternion, ev.quaternion) / (ev.time / prev_prev_ev.time);
			}
			prev_prev_ev = prev_ev;
			prev_ev = ev;
		}
		
		// Smooth and recenter quaternion acceleration data (moving average of 3 samples)
		prev_ev = null;
		prev_prev_ev = null;
		for (RotationEvent ev : events_smoothed) {
			if (prev_ev != null && prev_prev_ev != null)
				prev_ev.qacceleration = (prev_prev_ev.qacceleration + prev_ev.qacceleration + ev.qacceleration) / 3.0;
			prev_ev = ev;
		}
		
		// Compute instantaneous acceleration
		prev_ev = null;
		for (RotationEvent ev : events_smoothed) {
			if (prev_ev == null) {
				ev.acceleration = 0;
			} else {
				ev.acceleration = (ev.speed - prev_ev.speed) / (ev.time / prev_ev.time);
			}
			prev_ev = ev;
		}
		
		// Smooth and recenter acceleration data (moving average of 2 samples)
		prev_ev = null;
		for (RotationEvent ev : events_smoothed) {
			if (prev_ev != null)
				prev_ev.acceleration = (prev_ev.acceleration + ev.acceleration) / 2.0;
			prev_ev = ev;
		}
		
		
		// compute total distance traveled
		for (RotationEvent ev : events) {
			distanceTraveled += ev.deltaAngle;
		}
		
		averageSpeed = distanceTraveled / completionTime;
	}
	
	

	private static Quaternion4f tmpq = new Quaternion4f();
	private static Quaternion4f tmpq2 = new Quaternion4f();
	private static Quaternion4f tmpq3 = new Quaternion4f();
	private static Quaternion4f tmpq4 = new Quaternion4f();
	private static AxisAngle3f axisangle = new AxisAngle3f();
	protected static double getAngle(Quaternion4f q1, Quaternion4f q2) {
		tmpq.invert(q2);
		tmpq2.mul(q1, tmpq);
		axisangle.set(tmpq2);
		return Math.min(Math.abs(axisangle.getAngle()), Math.abs(axisangle.getAngle() - Math.PI * 2));
	}

	/**
	 * FIXME: this seems wrong somehow.
	 * 
	 * @param q1
	 * @param q2
	 * @param q3
	 * @return
	 */
	protected static double getAccelerationAngle(Quaternion4f q1, Quaternion4f q2, Quaternion4f q3) {
		// tmpq2 = q1 / q2  (q2-q1)
		tmpq.invert(q2);
		tmpq2.mul(q1, tmpq);
		// tmpq4 = q2 / q3  (q3-q2)
		tmpq3.invert(q3);
		tmpq4.mul(q2, tmpq3);
		// tmpq3 = tmpq2 / tmpq4 (tmpq4-tmpq2)
		tmpq.invert(tmpq4);
		tmpq3.mul(tmpq2, tmpq);
		axisangle.set(tmpq3);
		return Math.min(Math.abs(axisangle.getAngle()), Math.abs(axisangle.getAngle() - Math.PI * 2));
	}

	public RotationEvent getRotationEventRightAfter(double t) {
		for (RotationEvent e: events) {
			if (e.time > t)
				return e;
		}
		return events.get(events.size() - 1);
	}
	
	public double getDuration() {
		return events.size() > 0 ? events.get(events.size() - 1).time : 0;
	}
	
	public String toString() {
		return "kinematics for " + username + " trial " + trial + " question " + question + ": " + events.size() + " rotation events, " + " duration " + (int)getDuration() + " sec.";
	}

	public double getCompletionTime() {
		return completionTime;
	}
}
