package fr.inria.aviz.physVisEval.logs;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVisEval.logs.questions.MultiQuestionData;
import fr.inria.aviz.physVisEval.logs.questions.QuestionData;
import fr.inria.aviz.physVisEval.logs.questions.RadioQuestionData;
import fr.inria.aviz.physVisEval.logs.questions.RankQuestionData;
import fr.inria.aviz.physVisEval.logs.questions.TwoBarQuestionData;

public class TrialData {

	//private int trialNumber;
	private int repetition;
	//private double startTime, endTime;
	private int datasetID;
	private String dataset;
	private double trialScore, errorScore, trialscorelog, trialscoregeomean;
	private ArrayList<QuestionData> questionData = new ArrayList<QuestionData>();
	
	TrialData() {
		
	}
	
	/**
	 * Fills the trial data from a hash map.
	 */
	TrialData(int repetition, int datasetID, Map<String, Object> map, ParsingOptions options) {
		this.repetition = repetition;
		this.datasetID = datasetID;
		this.trialScore = 0;
		this.errorScore = 0;
		this.trialscorelog = 0;
		this.trialscoregeomean = 0;
		this.dataset = (String)((Map<String, Object>)map.get("dataset")).get("dataset");
		ArrayList<Map<String, Object>> questions = (ArrayList<Map<String, Object>>)map.get("questions");
		int taskRank = 1;
		for (int i=0; i<questions.size(); i++) {
			Map<String, Object> questionMap = questions.get(i);
			QuestionData qd;
			String questionType = QuestionData.getQuestionType(questionMap, options);
			if (questionType.equals("multi")) {
				qd = new MultiQuestionData(taskRank, questionMap, options);
			} else if (questionType.equals("radio")) {
				qd = new RadioQuestionData(taskRank, questionMap, options);
				if (qd.getTaskName().equals("average")) {
					((RadioQuestionData)qd).setBestAnswers(retrieveSortedYearAverages(dataset));
				}
			} else if (questionType.equals("rank")) {
				qd = new RankQuestionData(taskRank, questionMap, options);
			} else if (questionType.equals("two-bar")) {
				qd = new TwoBarQuestionData(taskRank, questionMap, options, retrieveAxisRange(dataset));
			} else {
				qd = null;
				System.err.println(questionType + " is not a known question type");
			}
			if (qd.isEmpty()) {
				System.err.println("Question answer is empty.");
			}
			if (qd != null && !qd.isEmpty()) {
				questionData.add(qd);
				taskRank++;
			}
			trialScore += qd.getCompletionTime();
			errorScore += qd.getError();
			trialscorelog += qd.getCompletionTimeLog();
		}
		trialscoregeomean = trialscorelog / questions.size();
		errorScore /= questions.size();
		trialScore = Math.log(trialScore);
	}
	
	public ArrayList<QuestionData> getQuestionData() {
		return questionData;
	}
	
	public int getRepetition() {
		return repetition;
	}
	
	public String getDataset() {
		return dataset;
	}
	
	public double getTrialScore() {
		return trialScore;
	}

	public String toString() {
		String s = "Repetition #" + repetition + " (dataset #" + datasetID + ", " + dataset + ")\n";
		for (QuestionData q : questionData) {
			s += "      " + q.toString() + "\n";
		}
		return s;
	}

	// FIXME: encode this in the plist
	public static double retrieveAxisRange(String dataset) {
		String csvFile = "./data/datasets/" + dataset + ".csv";
		MatrixData data = new MatrixData(csvFile, null);
		DataInfo info = data.getDataInfo();
		return info.getAxisLabeling().getAxisRange();
	}
	
	static class YearAverage implements Comparable<YearAverage> {
		String year;
		double average;
		public YearAverage(String year, double average) {
			this.year = year;
			this.average = average;
		}
		@Override
		public int compareTo(YearAverage o) {
			return Double.compare(this.average, ((YearAverage)o).average);
		}
		@Override
		public String toString() {
			return year + " " + average;
		}
	}
	
	// FIXME: encode this in the plist
	public static ArrayList<String> retrieveSortedYearAverages(String dataset) {
		String csvFile = "./data/datasets/" + dataset + ".csv";
		MatrixData data = new MatrixData(csvFile, null);
		String[] cols = data.getColumnLabels();
		int ncols = data.cols;
		ArrayList<YearAverage> list = new ArrayList<YearAverage>();
		for (int c=0; c<ncols; c++) {
			String colName = data.getColumnLabel(c);
			double colAverage = data.extractColumn(c, false).computeMean();
			list.add(new YearAverage(colName, colAverage));
		}
		Collections.sort(list);
		ArrayList<String> sortedYears = new ArrayList<String>();
		for (YearAverage ya : list)
			sortedYears.add(ya.year);
		return sortedYears;
	}
	
	public static void main(String[] args) {
		retrieveSortedYearAverages("suicide");
	}

	public double getErrorScore() {
		return errorScore;
	}

	public void setErrorScore(double errorScore) {
		this.errorScore = errorScore;
	}

	public double getTrialTimeLog() {
		return trialscorelog;
	}
	
	public double getTrialTimeGeoMean()
	{
		return trialscoregeomean;
	}


}
