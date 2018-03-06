package fr.inria.aviz.physVisEval.logs;

import java.util.ArrayList;

public class CSVRatioLogWriter extends CSVLogWriter {

	String[] ratios = {"p2m", "p2s", "ma2p", "ma2m", "ma2s", "s2m"};
	
	public CSVRatioLogWriter(ParsingOptions options) {
		
		super(options);
		
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

			trialDataWriter.addColumn("ratio", "which modalities are compared");
			
			trialDataWriter.addColumn("log", "log value for the ratio");
			
			trialDataWriter.addColumn("antilog", "anti log for the plotting");
	}

	public CSVRatioLogWriter(ArrayList<ParticipantData> data, String taskNameFilter, ParsingOptions options) {
		this(options);
		setData(data, taskNameFilter, options);
	}
	
public void setData(ArrayList<ParticipantData> data, String taskNameFilter, ParsingOptions options) {
		
		trialDataWriter.clearRows();
		
		int trialDataRow = 0;
		
		for (int subject = 0; subject < data.size(); subject++) {

			ParticipantData subjectData = data.get(subject);

			// DATA PER TRIAL
			for (String r : ratios) 
			{
				trialDataWriter.setValue(trialDataRow, "subject", subjectData.getUserID());
				trialDataWriter.setValue(trialDataRow, "subjectname", subjectData.getUserName());
				trialDataWriter.setValue(trialDataRow, "group", subjectData.getUserGroupID());
				trialDataWriter.setValue(trialDataRow, "infovisbackground", subjectData.getInfovisPerson() ? "yes" : "no");
		
				trialDataWriter.setValue(trialDataRow, "ratio", r);
				double ratio = 0;
				if (r.equals("p2m"))
				{
					ratio = subjectData.getTimePhysical() - subjectData.getTimeMono();
				}
				else if (r.equals("p2s"))
				{
					ratio = subjectData.getTimePhysical() - subjectData.getTimeStereo();
				}
				else if (r.equals("ma2p"))
				{
					ratio = subjectData.getTime2D() - subjectData.getTimePhysical();
				}
				else if (r.equals("ma2m"))
				{
					ratio = subjectData.getTime2D() - subjectData.getTimeMono();
				}
				else if (r.equals("ma2s"))
				{
					ratio = subjectData.getTime2D() - subjectData.getTimeStereo();
				}
				else if (r.equals("s2m"))
				{
					ratio = subjectData.getTimeStereo() - subjectData.getTimeMono();
				}



				trialDataWriter.setValue(trialDataRow, "log", ratio);
				trialDataWriter.setValue(trialDataRow, "antilog", Math.exp(ratio));
				
				trialDataRow++;
			}
		}
	}
}
