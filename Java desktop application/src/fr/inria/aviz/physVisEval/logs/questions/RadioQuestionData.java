package fr.inria.aviz.physVisEval.logs.questions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import fr.inria.aviz.physVisEval.logs.ParsingOptions;

public class RadioQuestionData extends QuestionData {

	String bestAnswer;
	String userAnswer;
	
	public RadioQuestionData(int questionNumber, Map<String, Object> questionMap, ParsingOptions options) {
		super(questionNumber, questionMap, options);
		
		questionType = "radio";
		
		// Parse correct answer
		
		String correctAnswer = (String)correctAnswerInfo.get(0);
		this.bestAnswer = correctAnswer;
		
		// Parse answer
		
		Map<String, Object> answerMap = (Map<String, Object>)answerInfo.get(answerInfo.size() - 2);
		String answer = (String)answerMap.get("answer");
		this.userAnswer = answer;
		Map<String, Object> lastAnswer = (Map<String, Object>)answerInfo.get(answerInfo.size() - 1);
		double lastSelectTime = (Double)lastAnswer.get("time");
		
		// Update performance info
		
		if (answer == null) {
			System.err.println("Error: second to last answer item in Radio question does not have an Answer field.");
			System.err.println(answerInfo);
			System.exit(0);
		}
		
		if (answer.equals(correctAnswer)) {
			error = 0;
		} else {
			error = 1;
			errorString = "\n answer:  " + answer.toString() + "\n correct: " + correctAnswer.toString();
		}
		completionTime = lastSelectTime;

		if (completionTime == 0) {
			System.err.println("Error: could not find completion time");
			System.err.println(answerInfo);
			System.exit(0);
		}
		
		// Update human-readable answer strings for log file
		
		correctAnswerAsString = correctAnswer;
		userAnswerAsString = answer;
	}
	
	/**
	 * Recompute error given a list of best answers (rather than a single one)
	 * @param bestAnswers
	 */
	public void setBestAnswers(ArrayList<String> bestAnswers) {
		
		if (!bestAnswers.get(0).equals(bestAnswer)) {
			System.err.println("WARNING: best answer for the average task does not match the actual lowest average");
//			System.exit(0);
		}
		
		// Update error
		
		int count = bestAnswers.size();
		int rank = bestAnswers.indexOf(userAnswer);
		error = rank / (double)(count - 1);
	}
		
}
