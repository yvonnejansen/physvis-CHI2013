package fr.inria.aviz.physVisEval.logs;

import java.util.ArrayList;
import java.util.Map;

public class ConditionData {

	private int block;
	private String modality = null;
	private int modalityID = -1;
	private double conditionScoreGeoMean, errorConditionScore, conditionScoreLog;
	private ArrayList<Integer> datasetIDs = null;
	private ArrayList<TrialData> trialData = new ArrayList<TrialData>(); 
	
	ConditionData(int block) {
		this.block = block;
	}
	
	/**
	 * Fills the condition data from a hash map.
	 */
	public void addData(Map<String, Object> map, ParsingOptions options) {
		Map<String, Object> conditionInfoMap = (Map<String, Object>)map.get("condition");
		conditionScoreGeoMean = errorConditionScore = conditionScoreLog = 0;
		String newConditionModality = (String)conditionInfoMap.get("name");
		if (this.modality == null) {
			this.modality = newConditionModality;
		} else if (!this.modality.equals(newConditionModality))
			System.err.println("  Warning: modality names are not consistent across log files.");
		
		int newConditionModalityID = (Integer)conditionInfoMap.get("id");
		if (this.modalityID == -1) {
			this.modalityID = newConditionModalityID;
		} else if (this.modalityID != newConditionModalityID)
			System.err.println("  Warning: modality IDs are not consistent across log files.");

		ArrayList<String> newDatasetsIDs = (ArrayList<String>)conditionInfoMap.get("datasets");
		this.datasetIDs = new ArrayList<Integer>();
		for (String d : newDatasetsIDs)
			datasetIDs.add(Integer.parseInt(d));
		
		ArrayList<Map<String, Object>> trials = (ArrayList<Map<String, Object>>)map.get("repetitions");
		options.setWarningContext("condition " + modality);
		for (int r = 0; r < trials.size(); r++) {
			int repetition = r + 1;
			int datasetID = datasetIDs.get(r);
			Map<String, Object> trialMap = trials.get(r);
			TrialData td = new TrialData(repetition, datasetID, trialMap, options);
			setTrialData(repetition, td);
			conditionScoreGeoMean += td.getTrialTimeGeoMean();
			errorConditionScore += td.getErrorScore();
			conditionScoreLog += td.getTrialScore();
		}
		conditionScoreGeoMean /= trials.size();
		errorConditionScore /= trials.size();
		conditionScoreLog /= trials.size();
		options.setWarningContext("");
//		System.err.println("Condition log: " + conditionScoreLog);
	}
	
	private void setTrialData(int repetition, TrialData data) {
		int ind = repetition - 1;
		int add = ind - trialData.size() + 1;
		for (int i = 0; i < add; i++)
			trialData.add(null);
		trialData.set(ind, data);
	}
	
	public TrialData getTrialData(int repetition) {
		int ind = repetition - 1;
		if (trialData.size() <= ind)
			return null;
		return trialData.get(ind);
	}

	/**
	 * 
	 * @return
	 */
	public int getBlock() {
		return block;
	}

	/**
	 * 
	 */
	public String getTechnique() {
		return modality.substring(0, modality.indexOf("/"));
	}

	/**
	 * 
	 */
	public String getTask() {
		return modality.substring(modality.indexOf("/") + 1);
	}

	/**
	 * 
	 */
	//public String get 

	/**
	 * 
	 * @return
	 */
	public ArrayList<TrialData> getTrialData() {
		return trialData;
	}
	
	public String toString() {
		String s = "Condition #" + block + " (" + modality + ", datasets " + getDatasetIDsAsString() + "\n";
		for (TrialData t : trialData)
			s += "     " + t.toString() + "\n";
		//s += "\n";
		return s;
	}

	public String getModality() {
		return modality;
	}

	public int getModalityID() {
		return modalityID;
	}
	
	public ArrayList<Integer> getDatasetIDs() {
		return datasetIDs;
	}
	
	public String getDatasetIDsAsString() {
		String s = "";
		for (int i=0; i<datasetIDs.size(); i++) {
			int d = datasetIDs.get(i);
			s += d;
			if (i < datasetIDs.size() - 1)
				s += ", ";
		}
		return s;
	}

	
	public double getErrorTask1()
	{
		double avg = 0;
		for (TrialData t: trialData)
		{
			avg += t.getQuestionData().get(0).getError();
		}
		return avg / trialData.size();
	}

	
	
	public double getErrorTask2()
	{
		double avg = 0;
		for (TrialData t: trialData)
		{
			avg += t.getQuestionData().get(1).getError();
		}
		return avg / trialData.size();
	}

	
	public double getErrorTask3()
	{
		double avg = 0;
		for (TrialData t: trialData)
		{
			avg += t.getQuestionData().get(2).getError();
		}
		return avg / trialData.size();
	}

	

	public double getLogTimeTask1()
	{
		double avg = 0;
		for (TrialData t: trialData)
		{
			avg += t.getQuestionData().get(0).getCompletionTimeLog();
		}
		return avg / trialData.size();
	}
	
	public double getLogTimeTask2()
	{
		double avg = 0;
		for (TrialData t: trialData)
		{
			avg += t.getQuestionData().get(1).getCompletionTimeLog();
		}
		return avg / trialData.size();
	}

	public double getLogTimeTask3()
	{
		double avg = 0;
		for (TrialData t: trialData)
		{
			avg += t.getQuestionData().get(2).getCompletionTimeLog();
		}
		return avg / trialData.size();
	}

	
	public double getConditionScoreGeoMean()
	{
		return conditionScoreGeoMean;
	}

	public double getErrorConditionScore() {
		return errorConditionScore;
	}

	public void setErrorConditionScore(double errorConditionScore) {
		this.errorConditionScore = errorConditionScore;
	}

	public double getConditionScoreLog() {
		return conditionScoreLog;
	}

	public void setConditionScoreLog(double conditionScoreLog) {
		this.conditionScoreLog = conditionScoreLog;
	}

}
