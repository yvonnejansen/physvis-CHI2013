package fr.inria.aviz.physVizEval.experimentlauncher;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import fr.inria.aviz.physVizEval.barchart2d.BarChart2D;
import fr.inria.aviz.physVizEval.barchart3d.BarChart3D;
import fr.inria.aviz.physVizEval.stereotest.BlackScreen;
import fr.inria.aviz.physVizEval.stereotest.GrayScreen;
import fr.inria.aviz.physVizEval.stereotest.StereoTest;
import fr.inria.aviz.physVizEval.stereotest.StereoVisionTest;
import fr.inria.aviz.physVizEval.stereotest.JStereoComponent.StereoRenderingMethod;
import fr.inria.aviz.physVizEval.util.GUIUtils;
import fr.inria.aviz.physVizEval.util.UserInfo;
import TUIO.TuioClient;
import TUIO.TuioCursor;
import TUIO.TuioListener;
import TUIO.TuioObject;
import TUIO.TuioTime;


public class ExperimentLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			new ExperimentV2();
	}

}


class Experiment implements TuioListener
{
	TuioClient tuio;
	BarChart2D bc2d;
	BarChart3D bc3d;
	StereoTest stereoTest;
	String fileDirectory = "data/datasets/";
	// TODO exchange this by automatically filling the array with all csv contained in a specified directory
	String[] datasets = { // listing our datasets
			fileDirectory + "army.csv", 
			fileDirectory + "externaldebt.csv", 
			fileDirectory + "health.csv", 
			fileDirectory + "electricity.csv", 
			fileDirectory + "military.csv", 
			fileDirectory + "grosscapital.csv",
			fileDirectory + "education.csv", 
			fileDirectory + "carmortality.csv", 
			fileDirectory + "hiv.csv", 
			fileDirectory + "agriculturalland.csv", 
			fileDirectory + "co2.csv", 
			fileDirectory + "suicide.csv", 
			fileDirectory + "homicide.csv",
			fileDirectory + "food.csv",
			fileDirectory + "births.csv",
			fileDirectory + "tax.csv"
	};
	JFrame blackScreen;
	
	public Experiment()
	{	
		// Init other windows
		BarChart2D.init();
		StereoTest.init();
		GrayScreen.init();
		BlackScreen.init();
		try {
			Thread.sleep(2000);
		} catch (Exception e) {}
		
		// Show black screen. Will normally always remain in the background
		GrayScreen.display();

		
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
		tuio = new TuioClient(3333);
		tuio.addTuioListener(this);
//		StereoTest.main(null);
		tuio.connect();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		BarChart2D.display(datasets[0]);
//		for (int i = 0; i < 10; i++)
//			addTuioObject(new TuioObject(0, i, 0, 0, 0));
//		System.err.println("\n");
	}

	@Override
	public void addTuioCursor(TuioCursor arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addTuioObject(TuioObject arg0) {
		System.err.println("add tuio object -> forwarding to updateTuioObject");
		updateTuioObject(arg0);
		//		int condition = arg0.getSymbolID();
//		System.out.println("add tuio object " + condition);
//		int id = (int) Math.ceil(arg0.getX() * (datasets.length - 1));
//		float rem = arg0.getY();
//		int screen = (int)(rem * 10);
//		switch (condition) {
////		case 0:
////			StereoTest.main(null);
////			break;
//		case 2:
//			System.out.println("display 2d with id " + id);
//			BarChart2D.display(datasets[id]);
//			break;
//		case 3:
//			System.out.println("display mono with id " + id);
//			BarChart3D.display(datasets[id], false);
//			break;
//		case 4:
//			System.out.println("display stereo with id " + id);
//			BarChart3D.display(datasets[id], true);
//			break;
//		case 7:
//			if (screen == 9)
//				StereoTest.hide();
//			else
//			{
//				System.out.println("display stereo with id " + screen);
//				StereoTest.display(screen);
//			}
//			break;
//		case 9:
//			System.exit(0);
//			break;
//		default:
//			break;
//		}
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
	public void removeTuioObject(TuioObject arg0) {
		// TODO Auto-generated method stub
		int condition = arg0.getSymbolID();
		System.out.println("removing tuio object " + condition);

		switch (condition) {
		case 2:
			BarChart2D.hide();
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

	@Override
	public void updateTuioCursor(TuioCursor arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTuioObject(TuioObject arg0) {
		// TODO Auto-generated method stub
		int condition = arg0.getSymbolID();
		System.out.println("updating tuio object " + condition);
		float rem = arg0.getY();
		int id = (int) Math.ceil(arg0.getX() * (datasets.length - 1));
		if (rem == 1)
		{
			switch (condition) {
			case 1:
				BlackScreen.hide();
				break;
			case 2:
				BarChart2D.hide();
				System.out.println("hiding 2d");
				break;
			case 3:
				BarChart3D.hide();
				System.out.println("hiding mono");
				break;
			case 4:
				BarChart3D.hide();
				System.out.println("hiding stereo");
				break;
			default:
				break;
			}
		}
		else
		{
			int screen = (int) (rem * 10);
			switch (condition) {
			case 1:
				BlackScreen.display();
				break;
			case 2:
				System.out.println("display 2d with id " + id);
				BarChart2D.display(datasets[id]);
				
				break;
			case 3:
				System.out.println("display mono id with " + id);
				BarChart3D.display(datasets[id], false, true);
					try {
						Thread.sleep(500);
						BarChart3D.hide();
						Thread.sleep(500);
						BarChart3D.display(datasets[id], false, true);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			case 4:
				System.out.println("display stereo with id " + id);
				BarChart3D.display(datasets[id], true);
				try {
						Thread.sleep(500);
						BarChart3D.hide();
						Thread.sleep(500);
						BarChart3D.display(datasets[id], true);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				break;
			case 7:
				if (screen == 9)
					StereoTest.hide();
				else
					StereoTest.display(screen);
				System.out.println("show stereo test screen " + screen);
				break;
			case 9:
				System.exit(0);
				break;
			default:
				break;
			}			
		}
		
	}


}