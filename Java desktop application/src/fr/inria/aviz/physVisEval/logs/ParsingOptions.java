package fr.inria.aviz.physVisEval.logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ParsingOptions {

	private int numberOfConditions = 4;
	private int numberOfItemsForMultiQuestions = 10;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private String plistfilename = null; // for retrieving missing data
	
	// 1: pilot1 plist format (before experiment 1)
	// 2: pilot2 plist format (right before experiment 1) and probably experiment 1 itself
	// 3: pilot3 plist format (before experiment 2)
	private int formatVersion = 3;
	
	private String warningContext = "";
	
	// used for parsing timestamps in plists
	public DateFormat getDateFormat() {
		return dateFormat;
	}
	
	// used for computing user group numbers and for parsing the right number of condition keys
	public int getNumberOfConditions() {
		return numberOfConditions;
	}
	
	// for warning messages
	String getWarningContext() {
		return warningContext;
	}

	// for warning messages
	void setWarningContext(String warningContext) {
		this.warningContext = warningContext;
	}

	// used for computing Hamming errors for multi questions
	public int getNumberOfItemsForMultiQuestions() {
		return numberOfItemsForMultiQuestions;
	}

	public void setNumberOfConditions(int numberOfConditions) {
		this.numberOfConditions = numberOfConditions;
	}

	public void setNumberOfItemsForMultiQuestions(int numberOfItemsForMultiQuestions) {
		this.numberOfItemsForMultiQuestions = numberOfItemsForMultiQuestions;
	}
	
	
	public String getPlistfilename() {
		return plistfilename;
	}

	public void setPlistfilename(String plistfilename) {
		this.plistfilename = plistfilename;
	}

	public int getFormatVersion() {
		return formatVersion;
	}
	
	public void setFormatVersion(int version) {
		this.formatVersion = version;
	}
}
