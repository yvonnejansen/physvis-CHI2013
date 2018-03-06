package fr.inria.aviz.physVisEval.logs.questions;

import java.util.HashSet;
import java.util.Map;

import fr.inria.aviz.physVisEval.data.AxisLabeling;
import fr.inria.aviz.physVisEval.data.DataInfo;
import fr.inria.aviz.physVisEval.data.MatrixData;
import fr.inria.aviz.physVisEval.logs.ParsingOptions;
import fr.inria.aviz.physVisEval.logs.Utils;

public class TwoBarQuestionData extends QuestionData {

	public TwoBarQuestionData(int questionNumber, Map<String, Object> questionMap, ParsingOptions options, double barRange) {
		super(questionNumber, questionMap, options);
		
		questionType = "two-bar";
		
		// Parse correct answer
		
		double correctMin = Double.parseDouble((String)correctAnswerInfo.get(0)); 
		double correctMax = Double.parseDouble((String)correctAnswerInfo.get(1)); 
		
		// Parse answer
		
		//@SuppressWarnings("unchecked")
		double answerMin, answerMax, lastSelectTime;
//		try{
		Map<String, Object> answerMap = (Map<String, Object>)answerInfo.get(answerInfo.size()-2);
		if (answerMap.get("min value") != null) 
		{
			answerMin = (Double)answerMap.get("min value");
			answerMax = (Double)answerMap.get("max value");
			lastSelectTime = (Double)answerMap.get("time");
			if (answerMin > answerMax)
			{
				double temp = answerMax;
				answerMax = answerMin;
				answerMin = temp;
				System.err.println("Subject switched min and max sliders!");
			}
		}
		else // work-around for a log with missing values for slider positions
		{
			answerMap = (Map<String, Object>)answerInfo.get(answerInfo.size()-1);
			lastSelectTime = (Double)answerMap.get("time");
			answerMin = correctMin + 0.04 * correctMin;
			answerMax = correctMax + 0.04 * correctMax;
		}
//		finally
//		{
//			 answerMin = correctMin;
//			 answerMax = correctMax;
//			 lastSelectTime = (Double)((Map<String, Object>)answerInfo.get(answerInfo.size()-1)).get("time");
//		}
		
		// Update performance info
		
		// FIXME: use the axis max
		//double barRange = Math.max(answerMax, correctMax); 
		double error1 = Math.abs(answerMin - correctMin) / barRange; 
		double error2 = Math.abs(answerMax - correctMax) / barRange;
		error = (error1 + error2) / 2;
		
		if (error > 0.05)
			errorString = "\n answered: [" + answerMin + ", " + answerMax + "]" + "\n correct:  [" + correctMin + ", " + correctMax + "]";
		completionTime = lastSelectTime;
		
		if (completionTime == 0) {
			System.err.println("Error: could not find completion time");
			System.err.println(answerInfo);
			System.exit(0);
		}
		// Update human-readable answer strings for log file
		
		correctAnswerAsString = "[" + Utils.round(correctMin, 4) + ", " + Utils.round(correctMax, 4) + "]";
		userAnswerAsString =  "[" + Utils.round(answerMin, 4) + ", " + Utils.round(answerMax, 4) + "]";
		
	}
	
}
