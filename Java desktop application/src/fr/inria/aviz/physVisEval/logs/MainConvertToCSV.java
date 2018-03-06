package fr.inria.aviz.physVisEval.logs;
import java.io.File;
import java.util.ArrayList;

import fr.inria.aviz.physVisEval.data.MatrixData;

public class MainConvertToCSV {

	static final String inputDir = "./logs/pilot3/";
	static final String outputFilename = "pilot3";
	static final int formatVersion = 2;

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
		parsingOptions.formatVersion = formatVersion;
		parsingOptions.setNumberOfConditions(4);
		parsingOptions.setNumberOfItemsForMultiQuestions(10);
		
		// Load the data
		ArrayList<ParticipantData> data = loader.loadFiles(logFiles);

		// Process the data
		DataProcessor.computeNormalizedErrors(data);
		
		// Write the CSV file
		CSVLogWriter csv = new CSVLogWriter(data, null);
		csv.write(inputDir + "/" + outputFilename + ".alltasks", true); // don't put the extension
		
		// Write the CSV file for each task
		ArrayList<String> taskNames = DataProcessor.getAllTaskNames(data);
		for(String taskName : taskNames) {
			csv = new CSVLogWriter(data, taskName);
			csv.write(inputDir + "/" + outputFilename + "." + taskName, false); // don't put the extension
		}
		
		csv = new CSVAggLogWriter_XP1(data, null);
		csv.write(inputDir + "/" + outputFilename + "." + "agg", true);

		
		csv = new CSVRatioLogWriter(data, null);
		csv.write(inputDir + "/" + outputFilename + "." + "ratio", true);

		// read the csv back in to aggregate
//		MatrixData allP = new MatrixData(inputDir + "/" + outputFilename + ".csv", null);
		
//		ArrayList<>
	}

}
