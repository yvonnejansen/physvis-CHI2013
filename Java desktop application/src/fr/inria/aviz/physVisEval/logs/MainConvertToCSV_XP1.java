package fr.inria.aviz.physVisEval.logs;
import java.io.File;
import java.util.ArrayList;

import fr.inria.aviz.physVisEval.data.MatrixData;

public class MainConvertToCSV_XP1 {

	static final String inputDir = "./logs/exp1";
	static final String outputFilename = "exp1";
	
	// 1: pilot1 plist format (before experiment 1)
	// 2: pilot2 plist format (right before experiment 1) and probably experiment 1 itself
	// 3: pilot3 plist format (right before experiment 2) and probably experiment 2 itself
	static final int formatVersion = 2;
	
	static final int numberOfConditions = 4;
	static final int NumberOfItemsForMultiQuestions = 10;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Let the user chose log files to display and process
		File[] logFiles = Utils.choseFiles(inputDir, ".plist");
		
		// Create the data loader
		LogFileLoader loader = new LogFileLoader();
		ParsingOptions parsingOptions = loader.getParsingOptions();
		
		// Configure the data loader
		parsingOptions.setFormatVersion(formatVersion);
		parsingOptions.setNumberOfConditions(numberOfConditions);
		parsingOptions.setNumberOfItemsForMultiQuestions(NumberOfItemsForMultiQuestions);
		
		// Load the data
		ArrayList<ParticipantData> data = loader.loadFiles(logFiles);

		// Process the data
		DataProcessor.computeNormalizedErrors(data);
		
		// Write the CSV file
		CSVLogWriter csv = new CSVLogWriter(data, null, parsingOptions);
//		csv.write(inputDir + "/" + outputFilename + ".alltasks", true); // don't put the extension
//		
//		// Write the CSV file for each task
//		ArrayList<String> taskNames = DataProcessor.getAllTaskNames(data);
//		for(String taskName : taskNames) {
//			csv = new CSVLogWriter(data, taskName, parsingOptions);
//			csv.write(inputDir + "/" + outputFilename + "." + taskName, false); // don't put the extension
//		}
		
		csv = new CSVAggLogWriter_XP1(data, null, parsingOptions);
		csv.write(inputDir + "/" + outputFilename + "." + "agg", true);

		
//		csv = new CSVRatioLogWriter(data, null, parsingOptions);
//		csv.write(inputDir + "/" + outputFilename + "." + "ratio", true);

		// read the csv back in to aggregate
//		MatrixData allP = new MatrixData(inputDir + "/" + outputFilename + ".csv", null);
		
//		ArrayList<>
	}

}
