package fr.inria.aviz.physVisEval.logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ParticipantData {

	private String userName = null;
	private int userID = -1;
	private int userGroupID = -1;
	private boolean infovisPerson = false;
	private ArrayList<ConditionData> conditionData = new ArrayList<ConditionData>();
	
	private Map<String, Object> lastMap; // original raw data, used for debugging
	
	private double timePhysical, time2D, timeMono, timeStereo,
		timePhysical_task1, time2D_task1, timeMono_task1, timeStereo_task1,
		timePhysical_task2, time2D_task2, timeMono_task2, timeStereo_task2,
		timePhysical_task3, time2D_task3, timeMono_task3, timeStereo_task3,
		errorPhysical_task1, error2D_task1, errorMono_task1, errorStereo_task1,
		errorPhysical_task2, error2D_task2, errorMono_task2, errorStereo_task2,
		errorPhysical_task3, error2D_task3, errorMono_task3, errorStereo_task3,		
		errorPhysical, error2D, errorMono, errorStereo, time_touch, time_notouch, time_prop, time_mouse, error_touch, error_notouch, error_prop, error_mouse;
	private double time1stmod, time2ndmod, time3rdmod, time4thmod, timePhysicalPostLog, time2DPostLog, timeMonoPostLog, timeStereoPostLog;
	private boolean computed = false;
	


	
	
	public double getTimePhysical_task1()
	{
		if (!computed) compute();
		return timePhysical_task1;
	}
	
	public double getTimePhysical_task2()
	{
		if (!computed) compute();
		return timePhysical_task2;
	}
	
	public double getTimePhysical_task3()
	{
		if (!computed) compute();
		return timePhysical_task3;
	}
	
	public double getTime2D_task1()
	{
		if (!computed) compute();
		return time2D_task1;
	}

	public double getTime2D_task2()
	{
		if (!computed) compute();
		return time2D_task2;
	}

	public double getTime2D_task3()
	{
		if (!computed) compute();
		return time2D_task3;
	}

	public double getTimeMono_task1()
	{
		if (!computed) compute();
		return timeMono_task1;
	}
	
	public double getTimeMono_task2()
	{
		if (!computed) compute();
		return timeMono_task2;
	}
	
	public double getTimeMono_task3()
	{
		if (!computed) compute();
		return timeMono_task3;
	}
	
	public double getTimeStereo_task1()
	{
		if (!computed) compute();
		return timeStereo_task1;
	}

	public double getTimeStereo_task2()
	{
		if (!computed) compute();
		return timeStereo_task2;
	}

	public double getTimeStereo_task3()
	{
		if (!computed) compute();
		return timeStereo_task3;
	}

	public double getTime1stmod() {
		if (!computed) compute();
		return time1stmod;
	}

	public void setTime1stmod(double time1stmod) {
		this.time1stmod = time1stmod;
	}

	public double getTime2ndmod() {
		if (!computed) compute();
		return time2ndmod;
	}

	public void setTime2ndmod(double time2ndmod) {
		this.time2ndmod = time2ndmod;
	}

	public double getTime3rdmod() {
		if (!computed) compute();
		return time3rdmod;
	}

	public void setTime3rdmod(double time3rdmod) {
		this.time3rdmod = time3rdmod;
	}

	public double getTime4thmod() {
		if (!computed) compute();
		return time4thmod;
	}

	public void setTime4thmod(double time4thmod) {
		this.time4thmod = time4thmod;
	}

	private void compute()
	{
		if (!computed)
		{
			ArrayList<ConditionData> cond = this.getConditionData();
//			time1stmod = cond.get(0).getConditionScoreLog();
//			time2ndmod = cond.get(1).getConditionScoreLog();
//			time3rdmod = cond.get(2).getConditionScoreLog();
//			time4thmod = cond.get(3).getConditionScoreLog();
			
			for (int i = 0; i < cond.size(); i ++)
			{
				int modId = cond.get(i).getModalityID();
				switch (modId) {
				case 1:
					time_touch = cond.get(i).getConditionScoreGeoMean();
					error_touch = cond.get(i).getErrorConditionScore();
					
					errorPhysical_task1 = cond.get(i).getErrorTask1();
					errorPhysical_task2 = cond.get(i).getErrorTask2();
					errorPhysical_task3 = cond.get(i).getErrorTask3();
					timePhysical_task1 = cond.get(i).getLogTimeTask1();
					timePhysical_task2 = cond.get(i).getLogTimeTask2();
					timePhysical_task3 = cond.get(i).getLogTimeTask3();
					timePhysical = (timePhysical_task1 + timePhysical_task2 + timePhysical_task3)/3.0;
					errorPhysical = cond.get(i).getErrorConditionScore();
					break;
				case 2:
					time_notouch = cond.get(i).getConditionScoreGeoMean();
					error_notouch = cond.get(i).getErrorConditionScore();
			
					error2D_task1 = cond.get(i).getErrorTask1();
					error2D_task2 = cond.get(i).getErrorTask2();
					error2D_task3 = cond.get(i).getErrorTask3();
					time2D_task1 = cond.get(i).getLogTimeTask1();
					time2D_task2 = cond.get(i).getLogTimeTask2();
					time2D_task3 = cond.get(i).getLogTimeTask3();
					time2D = (time2D_task1 + time2D_task2 + time2D_task3) / 3.0;
					error2D = cond.get(i).getErrorConditionScore();
					break;
				case 3:
					time_prop = cond.get(i).getConditionScoreGeoMean();
					error_prop = cond.get(i).getErrorConditionScore();

					errorMono_task1 = cond.get(i).getErrorTask1();
					errorMono_task2 = cond.get(i).getErrorTask2();
					errorMono_task3 = cond.get(i).getErrorTask3();
					timeMono_task1 = cond.get(i).getLogTimeTask1();
					timeMono_task2 = cond.get(i).getLogTimeTask2();
					timeMono_task3 = cond.get(i).getLogTimeTask3();
					timeMono = (timeMono_task1 + timeMono_task2 + timeMono_task3)/3.0;
					errorMono = cond.get(i).getErrorConditionScore();
					break;
				case 4:
					time_mouse = cond.get(i).getConditionScoreGeoMean();
					error_mouse = cond.get(i).getErrorConditionScore();

					errorStereo_task1 = cond.get(i).getErrorTask1();
					errorStereo_task2 = cond.get(i).getErrorTask2();
					errorStereo_task3 = cond.get(i).getErrorTask3();
					timeStereo_task1 = cond.get(i).getLogTimeTask1();
					timeStereo_task2 = cond.get(i).getLogTimeTask2();
					timeStereo_task3 = cond.get(i).getLogTimeTask3();
					timeStereo = (timeStereo_task1 + timeStereo_task2 + timeStereo_task3)/3.0;
					errorStereo = cond.get(i).getErrorConditionScore();
					break;
				default:
					break;
				}
			}
			computed = true;
		}
	}

	
	
	/**
	 * Builds an empty participant data
	 */
	public ParticipantData(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Fills participant data from a xml plist file.
	 */
	public void addData(String filename, ParsingOptions options) {
		addData(Utils.PListLoad(filename), options);
	}
	
	/**
	 * Fills the participant data from a hash map.
	 */
	public void addData(Map<String, Object> map, ParsingOptions options) {
		try {
			Map<String, Object> rootmap = map;//(Map<String, Object>)map.get("Root");
			int newUserID = -1;
			if (options.getFormatVersion() == 1) {
				newUserID = Integer.parseInt((String)rootmap.get("userID"));
			} else if (options.getFormatVersion() >= 2) {
				Map<String, Object> userinfo = (Map<String, Object>)map.get("userInfo");
				newUserID = Integer.parseInt((String)userinfo.get("userID"));
				if (options.getFormatVersion() == 2)
					this.infovisPerson = ((String)userinfo.get("groupID")).equals("B") ? true : false;
			}
			if (this.userID == -1) {
				this.userID = newUserID;
				this.userGroupID = ((userID-1) % options.getNumberOfConditions()) + 1; 
			} else if (this.userID != newUserID)
				System.err.println("  Warning: user IDs are not consistent across log files.");
			for (int block = 1; block <= options.getNumberOfConditions(); block++) {
				String blockKey = "block " + block;
				Map<String, Object> blockMap = (Map<String, Object>)rootmap.get(blockKey);
				updateConditionData(block, blockMap, options);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ConditionData getConditionData(int block) {
		int blockIndex = block - 1;
		if (conditionData.size() <= blockIndex) {
			int firstBlockToAdd = conditionData.size();
			for (int b = firstBlockToAdd; b <= blockIndex; b++)
				conditionData.add(new ConditionData(b + 1));
		}
		return conditionData.get(blockIndex);
	}
	
	public ConditionData getConditionData(String modality) {
		for (ConditionData c : conditionData) {
			if (c.getModality().equals(modality))
				return c;
		}
		ConditionData c = new ConditionData(-1);
		conditionData.add(c);
		return c;
	}
	
	protected void updateConditionData(int block, Map<String, Object> map, ParsingOptions options) {
		ConditionData cd = getConditionData(block);
		cd.addData(map, options);
	}
	
//	public void computeTrialNumbers() {
//		int trialNumber = 1;
//		for (ConditionData cd : getConditionData()) {
//			for (TrialData td : cd.getTrialData()) {
//				td.setTrialNumber(trialNumber);
//				trialNumber++;
//			}
//		}
//	}
	
	/**
	 * Participant name.
	 * 
	 * @return
	 */
	public int getUserID() {
		return userID;
	}
	
	public int getUserGroupID() {
		return userGroupID;
	}
	
	public boolean getInfovisPerson() {
		return infovisPerson;
	}
	
	/**
	 * The last raw hash map used to update this object. Use for debugging.
	 * @return
	 */
	Map<String, Object> getLastMap() {
		return lastMap;
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<ConditionData> getConditionData() {
		return conditionData;
	}

	public String getUserName() {
		return userName;
	}
	
//	public int countTrials() {
//		int count = 0;
//		for (ConditionData cd : getConditionData()) {
//			for (TrialData td : cd.getTrialData()) {
//				if (td != null) // skip null trials
//					count++;
//			}
//		}
//		return count;
//	}
	
	public String toString() {
		String s = "Participant #" + userID + " (" + userName + ", group #" + getUserGroupID() + ")\n";
		for (ConditionData c : conditionData)
			s += "  " + c.toString() + "\n";
		s += "\n";
		return s;
	}

	public double getErrorPhysical() {
		return errorPhysical;
	}

	public void setErrorPhysical(double errorPhysical) {
		this.errorPhysical = errorPhysical;
	}

	public double getError2D() {
		return error2D;
	}

	public void setError2D(double error2D) {
		this.error2D = error2D;
	}

	public double getErrorMono() {
		return errorMono;
	}

	public void setErrorMono(double errorMono) {
		this.errorMono = errorMono;
	}

	public double getErrorStereo() {
		return errorStereo;
	}

	public void setErrorStereo(double errorStereo) {
		this.errorStereo = errorStereo;
	}

	public double getTimePhysicalPostLog() {
		return timePhysicalPostLog;
	}

	public void setTimePhysicalPostLog(double timePhysicalPostLog) {
		this.timePhysicalPostLog = timePhysicalPostLog;
	}

	public double getTime2DPostLog() {
		return time2DPostLog;
	}

	public void setTime2DPostLog(double time2DPostLog) {
		this.time2DPostLog = time2DPostLog;
	}

	public double getTimeMonoPostLog() {
		return timeMonoPostLog;
	}

	public void setTimeMonoPostLog(double timeMonoPostLog) {
		this.timeMonoPostLog = timeMonoPostLog;
	}

	public double getTimeStereoPostLog() {
		return timeStereoPostLog;
	}

	public void setTimeStereoPostLog(double timeStereoPostLog) {
		this.timeStereoPostLog = timeStereoPostLog;
	}

	public double getTime_touch() {
		if (!computed) compute();
		return time_touch;
	}


	public double getTime_notouch() {
		if (!computed) compute();
		return time_notouch;
	}


	public double getTime_prop() {
		if (!computed) compute();
		return time_prop;
	}


	public double getTime_mouse() {
		if (!computed) compute();
		return time_mouse;
	}


	public double getError_touch() {
		if (!computed) compute();
		return error_touch;
	}


	public double getError_notouch() {
		if (!computed) compute();
		return error_notouch;
	}


	public double getError_prop() {
		if (!computed) compute();
		return error_prop;
	}

	public double getError_mouse() {
		if (!computed) compute();
		return error_mouse;
	}

	public double getTimePhysical() {
		if (!computed) compute();
		return timePhysical;
	}

	public void setTimePhysical(double timePhysical) {
		this.timePhysical = timePhysical;
	}

	public double getTime2D() {
		if (!computed) compute();
		return time2D;
	}

	public void setTime2D(double time2D) {
		this.time2D = time2D;
	}

	public double getTimeMono() {
		if (!computed) compute();
		return timeMono;
	}

	public void setTimeMono(double timeMono) {
		this.timeMono = timeMono;
	}

	public double getTimeStereo() {
		if (!computed) compute();
		return timeStereo;
	}

	public void setTimeStereo(double timeStereo) {
		this.timeStereo = timeStereo;
	}

	public double getErrorPhysical_task1() {
		return errorPhysical_task1;
	}

	public void setErrorPhysical_task1(double errorPhysical_task1) {
		this.errorPhysical_task1 = errorPhysical_task1;
	}

	public double getError2D_task1() {
		return error2D_task1;
	}

	public void setError2D_task1(double error2d_task1) {
		error2D_task1 = error2d_task1;
	}

	public double getErrorMono_task1() {
		return errorMono_task1;
	}

	public void setErrorMono_task1(double errorMono_task1) {
		this.errorMono_task1 = errorMono_task1;
	}

	public double getErrorStereo_task1() {
		return errorStereo_task1;
	}

	public void setErrorStereo_task1(double errorStereo_task1) {
		this.errorStereo_task1 = errorStereo_task1;
	}

	public double getErrorPhysical_task2() {
		return errorPhysical_task2;
	}

	public void setErrorPhysical_task2(double errorPhysical_task2) {
		this.errorPhysical_task2 = errorPhysical_task2;
	}

	public double getError2D_task2() {
		return error2D_task2;
	}

	public void setError2D_task2(double error2d_task2) {
		error2D_task2 = error2d_task2;
	}

	public double getErrorMono_task2() {
		return errorMono_task2;
	}

	public void setErrorMono_task2(double errorMono_task2) {
		this.errorMono_task2 = errorMono_task2;
	}

	public double getErrorStereo_task2() {
		return errorStereo_task2;
	}

	public void setErrorStereo_task2(double errorStereo_task2) {
		this.errorStereo_task2 = errorStereo_task2;
	}

	public double getErrorPhysical_task3() {
		return errorPhysical_task3;
	}

	public void setErrorPhysical_task3(double errorPhysical_task3) {
		this.errorPhysical_task3 = errorPhysical_task3;
	}

	public double getError2D_task3() {
		return error2D_task3;
	}

	public void setError2D_task3(double error2d_task3) {
		error2D_task3 = error2d_task3;
	}

	public double getErrorMono_task3() {
		return errorMono_task3;
	}

	public void setErrorMono_task3(double errorMono_task3) {
		this.errorMono_task3 = errorMono_task3;
	}

	public double getErrorStereo_task3() {
		return errorStereo_task3;
	}

	public void setErrorStereo_task3(double errorStereo_task3) {
		this.errorStereo_task3 = errorStereo_task3;
	}

}
