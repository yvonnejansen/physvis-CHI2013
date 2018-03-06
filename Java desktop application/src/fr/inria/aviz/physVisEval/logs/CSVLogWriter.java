package fr.inria.aviz.physVisEval.logs;

import java.util.ArrayList;

import fr.inria.aviz.physVisEval.logs.questions.QuestionData;

public class CSVLogWriter {
	
	CSV trialDataWriter = new CSV(); 

	public CSVLogWriter()
	{
		
	}
	
	public CSVLogWriter(ParsingOptions options) {
			
		// Subject
		
//		trialDataWriter.addColumn(
//				"[subject info]",
//				" ");
		
		trialDataWriter.addColumn(
			"subject",
			"Subject's number, encoded in the plist file.");
		
		trialDataWriter.addColumn(
			"subjectname",
			"Subject's name, extracted from the filename.");
		
		trialDataWriter.addColumn(
			"group",
			"The group number, computed by taking subject number (above) modulo 4.");

		if (options.getFormatVersion() >= 3) {
			trialDataWriter.addColumn(
					"formerSubject", 
					"Indicates whether this subject has participated in the first experiment.");

		}
		else
			trialDataWriter.addColumn(
				"infovisbackground", 
				"Indicates whether this person is working in the infovis field.");
		// Condition

//		trialDataWriter.addColumn(
//				" ",
//				" ");

		trialDataWriter.addColumn(
			"conditionrank",
			"The rank of the condition (block?) according to the order of the experiment, starting from 1 and ending with 4.");
	
		trialDataWriter.addColumn(
			"modality",
			"The modality involved in the condition, defined by a number.");
			
		trialDataWriter.addColumn(
			"modalityname",
			"The name of the modality involved in the condition.");

//		trialDataWriter.addColumn(
//			"datasets",
//			"A list of all datasets involved in the condition (identified with numbers).");
		trialDataWriter.addColumn(
				"conditionScore",	
				"average trialScore for this condition");
		
		// Repetition
		
//		trialDataWriter.addColumn(
//				" ",
//				" ");

		trialDataWriter.addColumn(
			"repetition",
			"The repetition number, initialized to 1 when a new condition starts, and incremented with each new dataset presented.");
		trialDataWriter.addColumn(
			"datasetname",
			"The name of the dataset used in this repetition.");
		trialDataWriter.addColumn(
				"trialscore", 
				"The sum of all completion times for this trial.");
		trialDataWriter.addColumn(
				"trialscorelog", 
				"The log of the sum of all completion times for this trial.");
		trialDataWriter.addColumn(
				"trialscoregeomean", 
				"The geometric mean of all completion times for this trial.");

		// Task
		
//		trialDataWriter.addColumn(
//				" ",
//				" ");

		trialDataWriter.addColumn("trial", "The global trial number within the experiment (1-32).");
		
		trialDataWriter.addColumn(
			"question",
			"The question number in order of appearance, initialized to 1 when a new repetition starts.");
	
		trialDataWriter.addColumn(
			"tasktype",
			"The type of task captured by the question.");

		trialDataWriter.addColumn(
			"inputtype",
			"The type of input required by the question.");
		
//		trialDataWriter.addColumn(
//			"questiontext",
//			"The text displayed for the question.");
//
//		trialDataWriter.addColumn(
//			"correctanswer",
//			"The correct answer to the question.");
		
		// Performance
		
//		trialDataWriter.addColumn(
//				" ",
//				" ");

		trialDataWriter.addColumn(
			"readingTime",
			"The users' time to read the question.");

//		trialDataWriter.addColumn(
//			"answer",
//			"The users' answer to the question.");
		
		trialDataWriter.addColumn(
			"error",
			"The error in the question's answer, from 0 (exact answer) to 1 (worst possible answer).");
		
		trialDataWriter.addColumn(
			"time",
			"Task completion time in seconds, from the press on Start to the last selection or deselection in the list (FIXME).");

		if (options.getFormatVersion() >= 3) {
			// Extra measures from experiment 2
			trialDataWriter.addColumn(
				"perceivedDifficulty",
				"Difficulty rated by the subject, from 1 to 5");

			trialDataWriter.addColumn(
				"perceivedTime",
				"Task completion time estimated by the subject, in seconds (integer from 0 to 120 in xp2)");
		}
		
		// Derived measures of performance
		
//		trialDataWriter.addColumn(
//				" ",
//				" ");

		trialDataWriter.addColumn(
				"logtime",
				"Task completion time in log of seconds, from the press on Start to the last selection or deselection in the list (FIXME).");
		
		trialDataWriter.addColumn(
			"normalizederror",
			"The error value above, divided by the grand error mean for this type of task, computed across all participants.");
			
		trialDataWriter.addColumn(
			"normalizedtime",
			"The task completion time above, divided by the grand time mean for this type of task, computed across all participants.");
		trialDataWriter.addColumn(
				"normalizedperformance",
				"The sum of normalized time and error.");
		trialDataWriter.addColumn(
				"normalizedtime(log)", 
				"Task completion time in log of seconds, from the press on Start to the last selection or deselection in the list (FIXME).");
//		trialDataWriter.addColumn(
//				"effectp2D",	
//				"effect size 2d to physical");
//		trialDataWriter.addColumn(
//				"effectp2D",	
//				"effect size 2d to physical");

	}
	
	public CSVLogWriter(ArrayList<ParticipantData> data, String taskNameFilter, ParsingOptions options) {
		this(options);
		setData(data, taskNameFilter, options);
	}
	
	public void setData(ArrayList<ParticipantData> data, String taskNameFilter, ParsingOptions options) {
		
		trialDataWriter.clearRows();
		
		int trialDataRow = 0;
		int subjectDataRow = 0;
		
		for (int subject = 0; subject < data.size(); subject++) {

			ParticipantData subjectData = data.get(subject);

			// DATA PER TRIAL
			
			for (ConditionData condData : subjectData.getConditionData()) {
				for (TrialData trialData : condData.getTrialData()) {
					for (QuestionData qData : trialData.getQuestionData()) {
						
						if (taskNameFilter == null || qData.getTaskName().equals(taskNameFilter)) {
						
							trialDataWriter.setValue(trialDataRow, "subject", subjectData.getUserID());
							trialDataWriter.setValue(trialDataRow, "subjectname", subjectData.getUserName());
							trialDataWriter.setValue(trialDataRow, "group", subjectData.getUserGroupID());
							if (options.getFormatVersion() >= 3) {
								trialDataWriter.setValue(trialDataRow, "formerSubject", subjectData.getUserID() > 8 ? "yes" : "no");

							}
							else
								trialDataWriter.setValue(trialDataRow, "infovisbackground", subjectData.getInfovisPerson() ? "yes" : "no");
							
							trialDataWriter.setValue(trialDataRow, "conditionrank", condData.getBlock());
							trialDataWriter.setValue(trialDataRow, "modality", condData.getModalityID());
							trialDataWriter.setValue(trialDataRow, "modalityname", condData.getModality());
//							trialDataWriter.setValue(trialDataRow, "datasets", condData.getDatasetIDsAsString());
							trialDataWriter.setValue(trialDataRow, "conditionScore", condData.getConditionScoreGeoMean());
							
							trialDataWriter.setValue(trialDataRow, "repetition", trialData.getRepetition());
							trialDataWriter.setValue(trialDataRow, "datasetname", trialData.getDataset());
							trialDataWriter.setValue(trialDataRow, "trialscore", trialData.getTrialScore());
							trialDataWriter.setValue(trialDataRow, "trialscorelog", trialData.getTrialTimeLog());
							trialDataWriter.setValue(trialDataRow, "trialscoregeomean", trialData.getTrialTimeGeoMean());
							
							trialDataWriter.setValue(trialDataRow, "trial", trialDataRow %32 + 1);
							trialDataWriter.setValue(trialDataRow, "question", qData.getQuestionNumber());
							trialDataWriter.setValue(trialDataRow, "tasktype", qData.getTaskName());
							trialDataWriter.setValue(trialDataRow, "inputtype", qData.getQuestionType());
//							trialDataWriter.setValue(trialDataRow, "questiontext", qData.getQuestionText());
//							trialDataWriter.setValue(trialDataRow, "correctanswer", qData.getCorrectAnswerAsString());
							
							trialDataWriter.setValue(trialDataRow, "readingTime", Utils.round(qData.getTimeToReadQuestion(), 5));
//							trialDataWriter.setValue(trialDataRow, "answer", qData.getUserAnswerAsString());
							trialDataWriter.setValue(trialDataRow, "error", Utils.round(qData.getError(), 5));
							trialDataWriter.setValue(trialDataRow, "time", Utils.round(qData.getCompletionTime(), 4));
							if (options.getFormatVersion() >= 3) {
								// extra info from xp2
								trialDataWriter.setValue(trialDataRow, "perceivedDifficulty", qData.getPerceivedDifficulty());
								trialDataWriter.setValue(trialDataRow, "perceivedTime", Utils.round(qData.getPerceivedTime(), 4));
							}			
							
							trialDataWriter.setValue(trialDataRow, "logtime", Utils.round(qData.getCompletionTimeLog(), 4));
							trialDataWriter.setValue(trialDataRow, "normalizederror", Utils.round(qData.getNormalizedError(), 5));
							trialDataWriter.setValue(trialDataRow, "normalizedtime", Utils.round(qData.getNormalizedTime(), 4));
							trialDataWriter.setValue(trialDataRow, "normalizedperformance", Utils.round(qData.getNormalizedPerformance(), 4));
							trialDataWriter.setValue(trialDataRow, "normalizedtime(log)", Utils.round(qData.getNormalizedTimeLog(), 4));
							trialDataRow++;
						}
					}
				}
			}
		}
	}
	
	public void write(String filename, boolean writeExplanations) {
		trialDataWriter.write(filename, writeExplanations);
	}
	
}
