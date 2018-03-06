package fr.inria.aviz.physVisEval.logs.questions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import fr.inria.aviz.physVisEval.logs.ParsingOptions;
import fr.inria.aviz.physVisEval.logs.Utils;

public class RankQuestionData extends QuestionData {

	public RankQuestionData(int questionNumber, Map<String, Object> questionMap, ParsingOptions options) {
		super(questionNumber, questionMap, options);
		
		questionType = "rank";

		// Parse correct answer
		
		Hashtable<Integer, String> correctAnswer = new Hashtable<Integer, String>();
		for (int r=0; r<correctAnswerInfo.size(); r++) {
			String answerItem = (String)correctAnswerInfo.get(r);
			correctAnswer.put(r + 1, answerItem);
		}
		
		// Parse answer
		
		Hashtable<Integer, String> answer = new Hashtable<Integer, String>();
		double lastSelectTime = 0;
		for (Object a : answerInfo) {
			Map<String, Object> answerMap = (Map<String, Object>)a;
			Object rank_o = answerMap.get("rank");
			if (rank_o == null) {
				if (answerMap.get("buttonPressed") != null) {
					// last item
					lastSelectTime = (Double)answerMap.get("time");
					continue;
				} else {
					System.err.println("Error: rank question item without a rank and without a buttonPressed");
					System.err.println(answerInfo);
					System.exit(0);
				}
			}
			int rank = (Integer)rank_o;
			String answerItem = (String)answerMap.get("answer");
			if (answerItem == null) {
				// delete
				String correctItem = (String)answerMap.get("correction");
				if (correctItem != null) {
					//String itemToRemove = correctItem.substring(correctItem.indexOf(" ") + 1);
					int keyToRemove = -1;
					for (Integer k : answer.keySet()) {
						String item = answer.get(k);
						String itemWithNumber = k + " - " + item;
						if (itemWithNumber.equals(correctItem)) {
							keyToRemove = k;
						}
					}
					answer.remove(keyToRemove);
				} else {
					System.err.println("Error: question item without answer, correction or buttonPressed");
					System.err.println(answerInfo);
					System.exit(0);
				}
			} else {
				// add
				answer.put(rank, answerItem);
			}
		}
		
		// Turn hashtables into lists (more convenient for debugging output and computing distances)
		ArrayList<String> answerlist = Utils.hashtableToSortedArray(answer);
		ArrayList<String> correctAnswerList = Utils.hashtableToSortedArray(correctAnswer);
		
		// Update performance info
		
		if (answerlist.equals(correctAnswerList)) {
//			error = 0;
			error = Error.getNormalizedKendallTauDistance(answerlist, correctAnswerList);

		} else {
//			error = 1;
			error = Error.getNormalizedKendallTauDistance(answerlist, correctAnswerList);
			errorString = "\n answer:  " + answerlist.toString() + "\n correct: " + correctAnswerList.toString();
		}
		completionTime = lastSelectTime;
		
		if (completionTime == 0) {
			System.err.println("Error: could not find completion time");
			System.err.println(answerInfo);
			System.exit(0);
		}
		
		// Update human-readable answer strings for log file
		
		correctAnswerAsString = correctAnswerList.toString();
		userAnswerAsString = answerlist.toString();
	}
		
}
