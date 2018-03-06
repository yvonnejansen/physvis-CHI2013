package fr.inria.aviz.physVizEval.experimentlauncher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;

import fr.inria.aviz.physVizEval.barchart2d.BarChart2D;
import fr.inria.aviz.physVizEval.barchart3d.BarChart3D;
import fr.inria.aviz.physVizEval.stereotest.BlackScreen;
import fr.inria.aviz.physVizEval.stereotest.GrayScreen;
import fr.inria.aviz.physVizEval.stereotest.StereoTest;
import fr.inria.aviz.physVizEval.util.GUIUtils;
import fr.inria.aviz.physVizEval.util.Logger;
import fr.inria.aviz.physVizEval.util.Serial;
import fr.inria.aviz.physVizEval.util.UserInfo;

public class ExperimentV2 implements TuioListener {

	TuioClient tuio;
	BarChart2D bc2d;
	BarChart3D bc3d;
	StereoTest stereoTest;
	String fileDirectory = "data/datasets/";
	// TODO exchange this by automatically filling the array with all csv contained in a specified directory
	String[] datasets = { // listing our datasets
			"army", 
			"externaldebt", 
			"health", 
			"military", 
			"grosscapital",
			"education", 
			"carmortality", 
			"hiv", 
			"suicide", 
			"homicide", // temporarily switched homicide and suicide
			"births",
			"exports",
	};
	JFrame blackScreen;

	
	
	private class UserInfoHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			userId = uInfo.getUserName();
			group = uInfo.getGroupID();
			log = new Logger(userId, group);
			uInfo.dispose();
			System.out.println("user: " + userId + " group: " + group);
		}
	}

	
	
	String userId, condition;
	int group, trial, question = -1;
	Logger log;
	
	private static int NUMBER_OF_QUESTIONS = 4;
	
	String[] conditionNames = {"physical-touch", "physical-notouch", "virtual-prop", "virtual-mouse"};
	
	UserInfo uInfo;
	

	public ExperimentV2()
	{
		GrayScreen.init();
		BlackScreen.init();
		GrayScreen.display();
		
		BarChart3D.init(false);

		uInfo = new UserInfo(new UserInfoHandler());

		// Add shortcut for quitting the application
	    GUIUtils.addAdvancedKeyListener(null, new GUIUtils.AdvancedKeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
			@Override
			public void keyRepeated(KeyEvent e) {
			}
			@Override
			public void keyPressedOnce(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.isShiftDown()) {
					System.exit(0);
				}
			}
		}, false);
		
		// Setup TUIO
		tuio = new TuioClient(3334);
		tuio.addTuioListener(this);
//		StereoTest.main(null);
		tuio.connect();
		System.out.println("TUIO connected: " + tuio.isConnected());

		for (int i = 0; i < 12; i++)
		{
			tuio.getTuioObjects().add(new TuioObject(0, i, 0, 0, 0));
		}
	}
	
	
	public void removeTuioObject(TuioObject arg0) {
		// TODO Auto-generated method stub
		int condition = arg0.getSymbolID();
		System.out.println("removing tuio object " + condition);

		switch (condition) {
		case 1:
			// trial finished -> save log;
			break;
		case 2:
//			BarChart2D.hide();
			break;
		case 3:
			BarChart3D.hide();
			break;
		case 4:
			BarChart3D.hide();
			break;
		default:
			break;
		}
		
	}

	
	
	public void updateTuioObject(TuioObject arg0) {
		// TODO Auto-generated method stub
		int condition = arg0.getSymbolID();
		System.out.println("received tuio with id " + arg0.getSymbolID() + " question " + arg0.getAngle() +  " trial " + arg0.getX());
		if (condition > 10)
		{
			
		}
		else if (condition == 7)
		{
			log.setStartTime();
			System.out.println("start message received");
		}
		else if (condition == 8)
			Serial.getInstance().calibrate();
		
		else
		{
			float rem = arg0.getY();
			question = (int)arg0.getAngle() + 1;
			trial = (int) Math.ceil(arg0.getX() * (datasets.length - 1));
			if (log != null && rem == 1)
				log.saveLog();
	
			if (question > 1 && trial < datasets.length)
			{
				log.saveLog();
				System.out.println("creating new log for condition " + condition + " trial (datasetnr) " + trial);
				log.newLog(conditionNames[condition-1], datasets[trial], trial, question);
				
			}
			else
			{
				System.out.println("updating tuio object with id" + condition + " x " + arg0.getX() + " y " + arg0.getY() + " angle " + arg0.getAngle());
				if (rem == 1) // close model
				{
					switch (condition) {
					case 1:
						BlackScreen.hide();
						break;
					case 2:
						BlackScreen.hide();
						System.out.println("hiding 2d");
						break;
					case 3:
						BarChart3D.hide();
						System.out.println("hiding mono with prop");
						break;
					case 4:
						BarChart3D.hide();
						System.out.println("hiding mono with mouse");
						break;
					default:
						break;
					}
				}
				else
				{
					int screen = (int) (rem * 10);
					System.out.println("creating new log for " + conditionNames[condition-1] + datasets[trial] + trial + question);
					log.newLog(conditionNames[condition-1], datasets[trial], trial, question);
					String filename = fileDirectory + datasets[trial] + ".csv";
					switch (condition) {
					case 1:
						BlackScreen.display();
						BarChart3D.display(filename, false, true, log);
						break;
					case 2:
						BlackScreen.display();	
						BarChart3D.display(filename, false, true, log);
						break;
					case 3:
						System.out.println("display mono id with " + trial);
						BarChart3D.display(filename, true, true, log);
		//					try {
		//						Thread.sleep(500);
		//						BarChart3D.hide();
		//						Thread.sleep(500);
		//						BarChart3D.display(datasets[trial], true, true);
		//					} catch (InterruptedException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
						break;
					case 4:
						System.out.println("display stereo with id " + trial);
						BarChart3D.display(filename, true, false, log);
		//				try {
		//						Thread.sleep(500);
		//						BarChart3D.hide();
		//						Thread.sleep(500);
		//						BarChart3D.display(datasets[trial], true, false);
		//					} catch (InterruptedException e) {
		//						// TODO Auto-generated catch block
		//						e.printStackTrace();
		//					}
						break;
					default:
						break;
					}			
				}
			}
			if (condition == 6)
				System.exit(0);
		}
		
	}


	@Override
	public void addTuioCursor(TuioCursor arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addTuioObject(TuioObject arg0) {
		// TODO Auto-generated method stub
		System.out.println("TUIO object added referring to update...");
		updateTuioObject(arg0);
	}


	@Override
	public void refresh(TuioTime arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void removeTuioCursor(TuioCursor arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateTuioCursor(TuioCursor arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
