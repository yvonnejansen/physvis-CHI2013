package fr.inria.aviz.physVisEval.logs;

import java.util.ArrayList;

import fr.inria.aviz.physVisEval.logs.questions.QuestionData;
import fr.inria.aviz.physVisEval.logs.ConditionData;

public class CSVAggLogWriter extends CSVLogWriter {

	public CSVAggLogWriter(ParsingOptions options) {
		
		super(options);
		
		// TODO Auto-generated constructor stub
		trialDataWriter.addColumn(
				"subject",
				"Subject's number, encoded in the plist file.");
			
			trialDataWriter.addColumn(
				"subjectname",
				"Subject's name, extracted from the filename.");
			
			trialDataWriter.addColumn(
				"group",
				"The group number, computed by taking subject number (above) modulo 4.");

			trialDataWriter.addColumn(
					"infovisbackground", 
					"Indicates whether this person is working in the infovis field.");
			trialDataWriter.addColumn(
					"timephysical", 
					"aggregated time for all trials from physical modality based on the trialScore (sum of times for the 3 tasks");
			trialDataWriter.addColumn(
					"time2D", 
					"aggregated time for all trials from 2D modality based on the trialScore (sum of times for the 3 tasks");
			trialDataWriter.addColumn(
					"timemono", 
					"aggregated time for all trials from mono modality based on the trialScore (sum of times for the 3 tasks");
			trialDataWriter.addColumn(
					"timestereo", 
					"aggregated time for all trials from stereo modality based on the trialScore (sum of times for the 3 tasks");
			trialDataWriter.addColumn(
					"time1stmod", 
					"aggregated time for all trials from 1st modality based on the trialScore (sum of times for the 3 tasks");
			trialDataWriter.addColumn(
					"time2ndmod", 
					"aggregated time for all trials from 2nd modality based on the trialScore (sum of times for the 3 tasks");
			trialDataWriter.addColumn(
					"time3rdmod", 
					"aggregated time for all trials from 3rd modality based on the trialScore (sum of times for the 3 tasks");
			trialDataWriter.addColumn(
					"time4thmod", 
					"aggregated time for all trials from 4th modality based on the trialScore (sum of times for the 3 tasks");
			trialDataWriter.addColumn("matrixToP", "effect 2d to physical");
			trialDataWriter.addColumn("matrixToM", "effect 2d to mono");
			trialDataWriter.addColumn("matrixToS", "effect 2d to stereo");
			trialDataWriter.addColumn("pToM", "effect physical to mono");
			trialDataWriter.addColumn("pToS", "effect physcial to stereo");
			trialDataWriter.addColumn("sToM", "effect mono to stereo");
			
			trialDataWriter.addColumn("errorphysical", "averaged error for physical");
			trialDataWriter.addColumn("error2d", "averaged error for 2d");
			trialDataWriter.addColumn("errormono", "averaged error for mono");
			trialDataWriter.addColumn("errorstereo", "averaged error for stereo");
	}

	public CSVAggLogWriter(ArrayList<ParticipantData> data, String taskNameFilter, ParsingOptions options) {
		this(options);
		setData(data, taskNameFilter, options);
		// TODO Auto-generated constructor stub
	}

	
	
public void setData(ArrayList<ParticipantData> data, String taskNameFilter, ParsingOptions options) {
		
		trialDataWriter.clearRows();
		
		int trialDataRow = 0;
		
		for (int subject = 0; subject < data.size(); subject++) {

			ParticipantData subjectData = data.get(subject);

			// DATA PER TRIAL
			
			trialDataWriter.setValue(trialDataRow, "subject", subjectData.getUserID());
			trialDataWriter.setValue(trialDataRow, "subjectname", subjectData.getUserName());
			trialDataWriter.setValue(trialDataRow, "group", subjectData.getUserGroupID());
			trialDataWriter.setValue(trialDataRow, "infovisbackground", subjectData.getInfovisPerson() ? "yes" : "no");
							
			trialDataWriter.setValue(trialDataRow, "timephysical", subjectData.getTimePhysical());
			trialDataWriter.setValue(trialDataRow, "time2D", subjectData.getTime2D());
			trialDataWriter.setValue(trialDataRow, "timemono", subjectData.getTimeMono());
			trialDataWriter.setValue(trialDataRow, "timestereo", subjectData.getTimeStereo());

			trialDataWriter.setValue(trialDataRow, "time1stmod", subjectData.getTime1stmod());
			trialDataWriter.setValue(trialDataRow, "time2ndmod", subjectData.getTime2ndmod());
			trialDataWriter.setValue(trialDataRow, "time3rdmod", subjectData.getTime3rdmod());

			trialDataWriter.setValue(trialDataRow, "time4thmod", subjectData.getTime4thmod());

//			ConditionData c2d = subjectData.getConditionData("2D");
//			ConditionData cp = subjectData.getConditionData("physical");
//			ConditionData cm = subjectData.getConditionData("mono");
//			ConditionData cs = subjectData.getConditionData("stereo");
//			
			double c2dToP, c2dToM, c2dToS, pToM, pToS, sToM;
//			c2dToP = c2dToM = c2dToS = pToM = pToS = mToS = 0;
			double t1, t2, t3, t4;
//			for (int i = 1; i <= c2d.getTrialData().size(); i++)
//			{
//				// get values for this repetition
				t1 = subjectData.getTimePhysical();
				t2 = subjectData.getTime2D();
				t3 = subjectData.getTimeMono(); 
				t4 = subjectData.getTimeStereo();
				
				// compute the effects for this repetition
				c2dToP = t2 - t1;
				c2dToM = t2 - t3;
				c2dToS = t2 - t4;
				
				pToM = t1 - t3;
				pToS = t1 - t4;
				
				sToM = t4 - t3;
//			}
//			int s = c2d.getTrialData().size();
//			c2dToP/= s;
//			c2dToM /= s;
//			c2dToS /= s;
//			pToM /= s;
//			pToS /= s;
//			mToS /= s;
			
			trialDataWriter.setValue(trialDataRow, "matrixToP", c2dToP);
			trialDataWriter.setValue(trialDataRow, "matrixToM", c2dToM);
			trialDataWriter.setValue(trialDataRow, "matrixToS", c2dToS);
			trialDataWriter.setValue(trialDataRow, "pToM", pToM);
			trialDataWriter.setValue(trialDataRow, "pToS", pToS);
			trialDataWriter.setValue(trialDataRow, "sToM", sToM);

			trialDataWriter.setValue(trialDataRow, "errorphysical", subjectData.getErrorPhysical());			
			trialDataWriter.setValue(trialDataRow, "error2d", subjectData.getError2D());			
			trialDataWriter.setValue(trialDataRow, "errormono", subjectData.getErrorMono());			
			trialDataWriter.setValue(trialDataRow, "errorstereo", subjectData.getErrorStereo());			
			
			
			trialDataRow++;
						
					
				
			
		}
	}

}
