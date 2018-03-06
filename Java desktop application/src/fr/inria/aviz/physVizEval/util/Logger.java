package fr.inria.aviz.physVizEval.util;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import org.jzy3d.maths.Coord3d;
import org.openmali.vecmath2.Quaternion4f;

public class Logger implements RotationListener{
	CSV log = new CSV();
	String user, currentCondition, dataset;
	int question, trial, repetition, block, group;
	double time, error;
	double startTime;
	boolean initialized = false;
	ArrayList<Double> sensorData;

	
	
	
	
	static long launchingTime;
	String logfile = "./logs/exp2/"; // The CSV class will add the .csv extension
	int logfileNumber = 1;
	CSV csv;

	
//	public Logger(String username, int group) {
//		
//		user = username;
//		this.group = group;
//		question = trial = repetition = block = 0;
//		time = error = 0;
//		
//		// Create CSV structure
//		csv = new CSV();
//		csv.addColumn("user", "the user name");
//		csv.addColumn("group", "the group the user was assigned to");
//		csv.addColumn("condition", "name of the current condition");
//		csv.addColumn("block", "the current condition");
//		csv.addColumn("repetition", "the repetition within the current condition");
//		csv.addColumn("trial", "the trial number (increasing across conditions");
//		csv.addColumn("question", "the id of the current question");
//		csv.addColumn("dataset", "the name of the current dataset");
//		csv.addColumn("time", "time to answer the question");
//		csv.addColumn("error", "error for the question");
//		
//	}
	
	public Logger(String username, int group)
	{
		this.user = username;
		
		
		csv = new CSV();
		csv.addColumn("user", "the user name");
		csv.addColumn("group", "the group the user was assigned to");
		csv.addColumn("condition", "name of the current condition");
		csv.addColumn("dataset", "name of the dataset used for this trial");
		csv.addColumn("trial", "the trial number (increasing across conditions");
		csv.addColumn("question", "number of the current question within this trial");
		csv.addColumn("timestamp(s)", "time in s since question started");
		csv.addColumn("a", "quaternion a value for the rotation");
		csv.addColumn("b", "quaternion b value for the rotation");
		csv.addColumn("c", "quaternion c value for the rotation");
		csv.addColumn("d", "quaternion d value for the rotation");
	}
	
	public void newLog(String condition, String dataset, int trial, int question)
	{
		this.currentCondition  = condition;
		this.dataset = dataset;
		this.trial = trial;
		this.question = question;
		
		startTime = System.nanoTime()/1000000000.0;
		initialized = true;
		System.out.println("new log started: " + user + "_" + currentCondition + "_" + trial + "_" + question);
	}
	
	public void setStartTime()
	{
		startTime = System.nanoTime()/1000000000.0;

	}
	
	public void log(Quaternion4f v) {
		if (initialized)
		{
			double t = System.nanoTime()/1000000000.0 - startTime; // more precise than System.currentTimeMillis()
			int lastrow = csv.getRowCount();
			String[] rowArray = csv.getRowArray(lastrow); // faster than multiple calls to setValue()
			rowArray[0] = user;
			rowArray[1] = "" + group;
			rowArray[2] = currentCondition;
			rowArray[3] = dataset;
			rowArray[4] = "" + trial;
			rowArray[5] = "" + question;
			rowArray[6] = "" + t;
			rowArray[7] = "" + v.a();
			rowArray[8] = "" + v.b();
			rowArray[9] = "" + v.c();
			rowArray[10] = "" + v.d();
		}
		
	}
	
	public void saveLog() {
		System.out.println();
		System.out.print("Saving " + csv.getRowCount() + " rows to the log file...");
		boolean writeReadmeFile = logfileNumber == 1;
		csv.write(logfile + group + "_" + user + "_" + currentCondition +  "_dataset_" + dataset + "_question_" + question + "_log_" + logfileNumber, writeReadmeFile);
		
		// Switch to a new file
		csv.clearRows();
//		initialized = false;
		logfileNumber++;
	}

		

	@Override
	public void rotationEvent(Quaternion4f q) {
		log(q);
	}

}
