package fr.inria.aviz.physVisEval.logs.questions;

import java.util.HashSet;
import java.util.Map;

import fr.inria.aviz.physVisEval.logs.ParsingOptions;

public class MultiQuestionData extends QuestionData {

	public MultiQuestionData(int questionNumber, Map<String, Object> questionMap, ParsingOptions options) {
		super(questionNumber, questionMap, options);
		
		questionType = "multi";
		
		// Parse correct answer
		
		HashSet<String> correctAnswer = new HashSet<String>();
		for (Object a : correctAnswerInfo) {
			String answerItem = (String)a;
			correctAnswer.add(answerItem);
		}
		
		// Parse answer
		
		HashSet<String> answer = new HashSet<String>();
		double lastSelectTime = 0;
		for (Object a : answerInfo) {
			Map<String, Object> answerMap = (Map<String, Object>)a;
			String answerItem = (String)answerMap.get("answer");
			
			if (answerItem == null) {
				// delete
				String correctItem = (String)answerMap.get("correction");
				if (correctItem != null) {
					answer.remove(correctItem);
				} else {
					if (answerMap.get("buttonPressed") != null) {
						// last item
						lastSelectTime = (Double)answerMap.get("time");
					} else {
						System.err.println("Error: question item without answer, correction or buttonPressed");
						System.err.println(answerInfo);
						System.exit(0);
					}
				}
				
			} else {
				// add
				answer.add(answerItem);
			}
		}
		
		// Update performance info
		
		if (answer.equals(correctAnswer)) {
			error = 0;
		} else {
			//error = 1;
			error = Error.getNormalizedHammingDistance(answer, correctAnswer, options.getNumberOfItemsForMultiQuestions());
			errorString = "\n answer:  " + answer.toString() + "\n correct: " + correctAnswer.toString();
		}
		completionTime = lastSelectTime;
		
		if (completionTime == 0) {
			System.err.println("Error: could not find completion time");
			System.err.println(answerInfo);
			System.exit(0);
		}
		
		// Update human-readable answer strings for log file
		
		correctAnswerAsString = correctAnswer.toString();
		userAnswerAsString = answer.toString();

	}
	
}
