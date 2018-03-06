package fr.inria.aviz.physVisEval.logs;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class LogFileLoader {

	static class LogFile implements Comparable<LogFile> {
		Date date;
		String fullFilename;
		@Override
		public int compareTo(LogFile o) {
			return date.compareTo(o.date);
		}
	}
	
	static class ParticipantLogFiles implements Comparable<ParticipantLogFiles> {
		String participantName;
		ArrayList<LogFile> logFiles = new ArrayList<LogFile>();
		
		@Override
		public int compareTo(ParticipantLogFiles o) {
			// make sure the logFiles are sorted beforehand
			return logFiles.get(0).compareTo(o.logFiles.get(0));
		}
	}
	
	static class AllParticipantLogFiles {
		ArrayList<ParticipantLogFiles> participantLogFiles = new ArrayList<ParticipantLogFiles>();
		
		public ParticipantLogFiles getLogFiles(String participantName) {
			for (ParticipantLogFiles plf : participantLogFiles) {
				if (plf.participantName.equals(participantName)) {
					return plf;
				}
			}
			ParticipantLogFiles plf = new ParticipantLogFiles();
			plf.participantName = participantName;
			participantLogFiles.add(plf);
			return plf;
		}
		
		public void sort() {
			for (ParticipantLogFiles plf : participantLogFiles)
				Collections.sort(plf.logFiles);
			Collections.sort(participantLogFiles);
		}
	}
	
	///////////////////////////////////////////////////////////////////////
	
	String fileExtension = ".plist";
	DateFormat filenameDateFormat = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss Z");
	ParsingOptions parsingOptions = new ParsingOptions();
	
	public ArrayList<ParticipantData> loadDirectory(String dirName) {
		
		System.out.println("Looking in directory '" + dirName + "'...");
		File dir = new File(dirName);
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(fileExtension);
		    }
		};
		
		File[] children = dir.listFiles(filter);
		if (children == null)
			return null;
		
		return loadFiles(children);

	}
	
	/**
	 * @param args
	 */
	public ArrayList<ParticipantData> loadFiles(File[] files) {

		// 1 - Build the list of participant names and associated files

		AllParticipantLogFiles participantLogFiles = new AllParticipantLogFiles();
		
	    for (int i=0; i<files.length; i++) {
	    	File file = files[i];
	        String filename = file.getName();
	        String fullFilename = file.getAbsolutePath();
	        String shortFilename = filename.substring(0, filename.indexOf(fileExtension));
	        String participant = shortFilename.substring(0, shortFilename.indexOf(" "));
	        String date_s = shortFilename.substring(shortFilename.indexOf(" ") + 1);
	        Date date = null;
	        try {
	        	date = filenameDateFormat.parse(date_s);
	        } catch (Exception e) {
	        	System.err.println("  Warning: Invalid date format for file '" + filename + "'. Skipped file.");
	        	continue;
	        }
	        // add
	        ParticipantLogFiles plogFiles = participantLogFiles.getLogFiles(participant);
	        LogFile lf = new LogFile();
	        lf.fullFilename = fullFilename;
	        lf.date = date;
	        plogFiles.logFiles.add(lf);
	    }
		System.out.println("Found " + participantLogFiles.participantLogFiles.size() + " participants.\n");
		participantLogFiles.sort();
		 
		
		// 2 - Load participant data

		ArrayList<ParticipantData> participantsData = new ArrayList<ParticipantData>();

		for (ParticipantLogFiles plf : participantLogFiles.participantLogFiles) {
			int count = plf.logFiles.size();
			System.out.println("Loading participant '" + plf.participantName + "' (" + count + " file" + (count > 1 ? "s" : "") + ")...");
			ParticipantData pd = new ParticipantData(plf.participantName);
			try {
				for (LogFile lf : plf.logFiles)
					pd.addData(lf.fullFilename, parsingOptions);
				//pd.computeTrialNumbers();
				//System.out.println("  Found " + pd.countTrials() + " trials.");
				participantsData.add(pd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//System.out.println(pd + "\n");
		}		
		
		return participantsData;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public DateFormat getFilenameDateFormat() {
		return filenameDateFormat;
	}

	public void setFilenameDateFormat(DateFormat filenameDateFormat) {
		this.filenameDateFormat = filenameDateFormat;
	}

	public ParsingOptions getParsingOptions() {
		return parsingOptions;
	}

	public void setParsingOptions(ParsingOptions parsingOptions) {
		this.parsingOptions = parsingOptions;
	}

}
