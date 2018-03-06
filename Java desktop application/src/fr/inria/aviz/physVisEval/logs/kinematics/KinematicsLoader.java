package fr.inria.aviz.physVisEval.logs.kinematics;

import java.io.File;
import java.util.ArrayList;

import fr.inria.aviz.physVisEval.logs.ConditionData;
import fr.inria.aviz.physVisEval.logs.ParticipantData;
import fr.inria.aviz.physVisEval.logs.TrialData;
import fr.inria.aviz.physVisEval.logs.kinematics.Kinematics.RotationEvent;
import fr.inria.aviz.physVisEval.logs.questions.QuestionData;
import fr.inria.aviz.physVizEval.util.CSV;

public class KinematicsLoader {

	public static Kinematics load(ArrayList<ParticipantData> trialData, File file) {
		
		Kinematics kinematics = new Kinematics();
		
//		System.err.println();
//		System.err.println(file.getName());
		
		CSV csv = new CSV(file.getAbsolutePath());
		csv.setVerbose(false);
				
		// Columns
		// user,group,condition,dataset,trial,question,timestamp(s),a,b,c,d

		int nrows = csv.getRowCount();
		
		if (nrows == 0) {
			System.err.println("  ERROR: no data in " + file.getName());
			//System.exit(0); // this problem needs to be fixed in the log file
			return null;
		}
		
		int usercol = csv.columnIndex("user");
		int trialcol = csv.columnIndex("trial");
		int questioncol = csv.columnIndex("question");
		int tcol = csv.columnIndex("timestamp(s)");
		int acol = csv.columnIndex("a");
		int bcol = csv.columnIndex("b");
		int ccol = csv.columnIndex("c");
		int dcol = csv.columnIndex("d");
		
		// Read user and trial from the last row to test for junk data
//		username = csv.getValue(nrows - 1, usercol);
//		trial = Integer.parseInt(csv.getValue(nrows - 1, trialcol));
//		question = Integer.parseInt(csv.getValue(nrows - 1, questioncol));
		
		// Read rotation events and skip junk (wrong trials)
		
		double t = 0, prev_t = 0;
		int nConditionChanges = 0, nTimestampResets = 0;
		String rusername = null, prev_rusername = null;
		int rtrial = -1, rquestion = -1, prev_rtrial = -1, prev_rquestion = -1;
		for (int r = 0; r < nrows; r++) {
			
			boolean conditionChange = false;
			boolean timestampReset = false;
						
			// User name -- safety check
			rusername = csv.getValue(r, usercol);
			if (r > 0 && !rusername.equals(prev_rusername)) {
				System.err.println("  ERROR: user name changes in " + file.getName() + " row " + (r+2));
				//System.exit(0); // this problem needs to be fixed in the log file
				return null;
			}
			prev_rusername = rusername;
					
			// Trial number -- safety check
			rtrial = Integer.parseInt(csv.getValue(r, trialcol));
			if (r > 0 && rtrial != prev_rtrial) {
				//System.err.println("  ERROR: trial number changes in " + file.getName() + " row " + (r+2));
				//System.exit(0); // this problem needs to be fixed in the log file
				if (nTimestampResets > 0) {
					System.err.println("  ERROR: trial number change after a timestamp reset in " + file.getName() + " row " + (r+2));
					return null;
				}
				conditionChange = true;
				nConditionChanges++;
			}
			prev_rtrial = rtrial;
			
			// Question number -- safety check
			rquestion = Integer.parseInt(csv.getValue(r, questioncol));
			if (r > 0 && rquestion != prev_rquestion) {
				//System.err.println("  ERROR: question number changes in " + file.getName() + " row " + (r+2));
				//System.exit(0); // this problem needs to be fixed in the log file
				//return;
				if (nTimestampResets > 0) {
					System.err.println("  ERROR: question number change after a timestamp reset in " + file.getName() + " row " + (r+2));
					return null;
				}
				conditionChange = true;
				nConditionChanges++;
			}
			prev_rquestion = rquestion;

//			if (Integer.parseInt(csv.getValue(r, trialcol)) != trial || Integer.parseInt(csv.getValue(r, questioncol)) != question) {
//				// ignore data for wrong trial/question
//				continue;
//			}
			t = Double.parseDouble(csv.getValue(r, tcol));
			if (!conditionChange && r > 0 && t < prev_t) {
				//System.err.println("  ERROR: timestamp decreases in " + file.getName() + " row " + (r+2));
				//System.exit(0); // this problem needs to be fixed in the log file
				//return;
				if (nTimestampResets > 0) {
					System.err.println("  ERROR: More than one timestamp reset in " + file.getName() + " row " + (r+2));
					return null;
				}
				timestampReset = true;
				nTimestampResets++;
			}
			prev_t = t;
			
			if (conditionChange || timestampReset) {
				kinematics.events.clear();
			}
			
			kinematics.events.add(new RotationEvent(
				t,
				Float.parseFloat(csv.getValue(r, acol)),
				Float.parseFloat(csv.getValue(r, bcol)),
				Float.parseFloat(csv.getValue(r, ccol)),
				Float.parseFloat(csv.getValue(r, dcol))
			));
		}
		
		// Necessary and sufficient information for identifying the trial
		kinematics.username = rusername;
		kinematics.trial = rtrial;
		kinematics.question = rquestion;
		
		// Additional data for display/debug (read them on the last row -> TODO: check it's correct)
		kinematics.filename = file.getName();
		kinematics.condition = csv.getValue(nrows-1, "condition");
		kinematics.dataset = csv.getValue(nrows-1, "dataset");
		
		// Try to find the trial completion time
		if (trialData != null) {
			for (ParticipantData pd: trialData) {
				if (pd.getUserName().equals(kinematics.username)) {
					ConditionData cd = pd.getConditionData(kinematics.condition);
					//System.err.println(cd);
					if (cd != null) {
						ArrayList<TrialData> tds = cd.getTrialData();
						for (TrialData td : tds) {
						//	System.err.println(td);
							if (td.getDataset().equals(kinematics.dataset)) {
								QuestionData qd = td.getQuestionData().get(kinematics.question - 1);
								kinematics.completionTime = qd.getCompletionTime();
							}
						}
					}
				}
			}
			//kinematics.completionTime
		}

		// Do some math on quaternions
		kinematics.processData();
		
		System.out.println("Loaded " + kinematics);

		return kinematics;
	}
	
}
