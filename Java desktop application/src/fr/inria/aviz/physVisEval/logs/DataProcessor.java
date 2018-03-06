package fr.inria.aviz.physVisEval.logs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import fr.inria.aviz.physVisEval.logs.questions.QuestionData;

public class DataProcessor {

	public static void computeNormalizedErrors(ArrayList<ParticipantData> participantData) {
		
		// Compute grand means of question errors and times per task type
		
		Hashtable<String, Double> totalErrors = new Hashtable<String, Double>(); 
		Hashtable<String, Double> totalTimes = new Hashtable<String, Double>(); 
		Hashtable<String, Double> totalReadingTimes = new Hashtable<String, Double>(); 
		Hashtable<String, Integer> counts = new Hashtable<String, Integer>();
		for (ParticipantData pd : participantData) {
			ArrayList<ConditionData> cds = pd.getConditionData();
			for (ConditionData cd: cds) {
				ArrayList<TrialData> tds = cd.getTrialData();
				for (TrialData td : tds) {
					ArrayList<QuestionData> qds = td.getQuestionData();
					for (QuestionData qd : qds) {
						String taskType = qd.getTaskName();
						if (!totalErrors.containsKey(taskType)) {
							totalErrors.put(taskType, 0.0);
							totalTimes.put(taskType, 0.0);
							totalReadingTimes.put(taskType, 0.0);
							counts.put(taskType, 0);
						}
						totalErrors.put(taskType, totalErrors.get(taskType) + qd.getError());
						totalTimes.put(taskType, totalTimes.get(taskType) + qd.getCompletionTime());
						totalReadingTimes.put(taskType, totalReadingTimes.get(taskType) + qd.getTimeToReadQuestion());
						counts.put(taskType, counts.get(taskType) + 1);
					}
				}
			}
		}
		Hashtable<String, Double> errorMeans = new Hashtable<String, Double>();
		Hashtable<String, Double> timeMeans = new Hashtable<String, Double>();
		Hashtable<String, Double> readingTimeMeans = new Hashtable<String, Double>();
		for (String key : totalErrors.keySet()) {
			errorMeans.put(key, totalErrors.get(key) / counts.get(key));
			timeMeans.put(key, totalTimes.get(key) / counts.get(key));
			readingTimeMeans.put(key, totalReadingTimes.get(key) / counts.get(key));
		}
		
		System.out.println("\n-- Performance means --");
		ArrayList<String> keys = new ArrayList<String>();
		keys.addAll(errorMeans.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			System.out.println(key + ": error = " + Utils.round(errorMeans.get(key), 2) + ", time = " + Utils.round(timeMeans.get(key), 1) + " sec" + " (time to read questions = " + Utils.round(readingTimeMeans.get(key), 1) + " sec)");
		}
		System.out.println();
		
		// Compute normalized errors and times
		for (ParticipantData pd : participantData) {
			ArrayList<ConditionData> cds = pd.getConditionData();
			for (ConditionData cd: cds) {
				ArrayList<TrialData> tds = cd.getTrialData();
				for (TrialData td : tds) {
					ArrayList<QuestionData> qds = td.getQuestionData();
					for (QuestionData qd : qds) {
						String taskType = qd.getTaskName();
						if (errorMeans.get(taskType) == 0)
							qd.setNormalizedError(0);
						else
							qd.setNormalizedError(qd.getError() / errorMeans.get(taskType));
						if (timeMeans.get(taskType) == 0)
							qd.setNormalizedTime(0);
						else
							{
							qd.setNormalizedTimeLog(Math.log(qd.getCompletionTime()) / Math.log(timeMeans.get(taskType)));
							qd.setNormalizedTime(qd.getCompletionTime() / timeMeans.get(taskType));
							}
						qd.computeNormalizedPerformance();
					}
				}
			}
		}
	}
	
	public static ArrayList<String> getAllTaskNames(ArrayList<ParticipantData> participantData) {
		
		ArrayList<String> taskNames = new ArrayList<String>(); 
		for (ParticipantData pd : participantData) {
			ArrayList<ConditionData> cds = pd.getConditionData();
			for (ConditionData cd: cds) {
				ArrayList<TrialData> tds = cd.getTrialData();
				for (TrialData td : tds) {
					ArrayList<QuestionData> qds = td.getQuestionData();
					for (QuestionData qd : qds) {
						String taskName = qd.getTaskName();
						if (!taskNames.contains(taskName)) {
							taskNames.add(taskName);
						}
					}
				}
			}
		}
		return taskNames;
	}
}
