package fr.inria.aviz.physVisEval.logs.questions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import fr.inria.aviz.physVisEval.logs.ParsingOptions;
import fr.inria.aviz.physVisEval.logs.TrialData;

public abstract class QuestionData {

	private int questionNumber;
	private String taskName;
	private String questionText;
	private Date displayTime;
	private Date startTime;
	private double timeToReadQuestion;
	private int perceivedDifficulty = -1; // xp2 only
	private double perceivedTime = -1; // xp2 only 

	// read by subclasses
	protected ArrayList answerInfo;
	protected ArrayList correctAnswerInfo;
	
	// computed by subclasses
	String questionType = "unknown";
	String correctAnswerAsString = "unknown";
	String userAnswerAsString = "unknown";
	protected double completionTime = -1;
	protected double error = -1;
	protected String errorString = "";
	
	// computed later by DataProcessor
	private double normalizedError = -1; 
	private double normalizedTime = -1; 
	private double normalizedTimeLog = -1;
	
	private double normalizedPerformance = -1;
	
	public static String getQuestionType(Map<String, Object> questionMap, ParsingOptions options) {
		Map<String, Object> questionInfo = (Map<String, Object>)questionMap.get("question");
		return (String)questionInfo.get("type");
	}
	
	public QuestionData(int questionNumber, Map<String, Object> questionMap, ParsingOptions options) {
		
		this.questionNumber = questionNumber;
		this.taskName = "task" + questionNumber;
		Map<String, Object> questionInfo = (Map<String, Object>)questionMap.get("question");
		this.questionText = (String)questionInfo.get("question");
		try {
			this.displayTime = (Date)questionInfo.get("time");//options.getDateFormat().parse((String)questionInfo.get("time"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.startTime = (Date)((Map<String, Object>)questionMap.get("startTime")).get("time");//options.getDateFormat().parse((String)questionInfo.get("time"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.timeToReadQuestion = (this.startTime.getTime() - this.displayTime.getTime()) / 1000.0;
		
		this.correctAnswerInfo = (ArrayList)questionInfo.get("correct answer");
		this.answerInfo = (ArrayList)questionMap.get("answer");
		
		// extra info from xp2
		if (options.getFormatVersion() >= 3) {
//			System.err.println("file format version: " + options.getFormatVersion());
			Map<String, Object> additionalInfo = (Map<String, Object>)questionMap.get("additionalInfo");
			perceivedDifficulty = Integer.parseInt((String)additionalInfo.get("difficulty"));
			String sperceivedTime = (String)additionalInfo.get("perceivedTime");
			if (sperceivedTime.endsWith(" s")) {
				perceivedTime = Integer.parseInt(sperceivedTime.substring(0, sperceivedTime.length() - 2));
			} else {
				throw(new IllegalArgumentException("perceivedTime format invalid"));
			}
		}
	}
	
	public boolean isEmpty() {
		return answerInfo.size() == 0;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public String getQuestionText() {
		return questionText;
	}

	public double getCompletionTime() {
		return completionTime;
	}

	public double getError() {
		return error;
	}

	public String getTaskName() {
		return taskName;
	}

	public String getQuestionType() {
		return questionType;
	}
	
	public String getCorrectAnswerAsString() {
		return correctAnswerAsString;
	}
	
	public String getUserAnswerAsString() {
		return userAnswerAsString;
	}

	public double getNormalizedError() {
		return normalizedError;
	}
	
	public double getNormalizedTime() {
		return normalizedTime;
	}
	
	public double getNormalizedTimeLog() {
		return normalizedTimeLog;
	}

	// Called by dataprocessor
	public void setNormalizedError(double normalizedError) {
		this.normalizedError = normalizedError;
	}

	// Called by dataprocessor
	public void setNormalizedTime(double normalizedTime) {
		this.normalizedTime = normalizedTime;
	}
	
	public void setNormalizedTimeLog(double normalizedTime) {
		this.normalizedTimeLog = normalizedTime;
	}
	
	
	public void computeNormalizedPerformance() {
		if (normalizedTime != -1)
		{
			if (normalizedError != -1)
			{
				this.normalizedPerformance = this.normalizedError + this.normalizedTime;
			}
			else
				System.err.println("No normalized time set yet.");
		}
		else
			System.err.println("No normalized error set yet.");

	}
	
	public double getNormalizedPerformance()
	{
		if (this.normalizedPerformance != -1)
			return this.normalizedPerformance;
		else
		{
			computeNormalizedPerformance();
			if (this.normalizedPerformance != -1)
				return this.normalizedPerformance;			
		}
		return -1;
		
	}
	
	public double getTimeToReadQuestion() {
		return timeToReadQuestion;
	}
	
	public String toString() {
		String s = "Question #" + getQuestionNumber() + " (" + getTaskName() + ", " + getQuestionType() + ")  ->  time " + (int)getCompletionTime() + " sec, error " + getError() + errorString;
		return s;
	}

	public double getCompletionTimeLog() {
		return Math.log(completionTime);
	}

	public int getPerceivedDifficulty() {
		return perceivedDifficulty;
	}

	public double getPerceivedTime() {
		return perceivedTime;
	}
}
