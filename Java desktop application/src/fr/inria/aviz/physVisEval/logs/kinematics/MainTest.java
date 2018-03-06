package fr.inria.aviz.physVisEval.logs.kinematics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.Timer;

import org.jzy3d.colors.Color;

import fr.inria.aviz.physVisEval.logs.LogFileLoader;
import fr.inria.aviz.physVisEval.logs.ParsingOptions;
import fr.inria.aviz.physVisEval.logs.ParticipantData;
import fr.inria.aviz.physVisEval.logs.Utils;
import fr.inria.aviz.physVizEval.barchart3d.BarChart3D;
import fr.inria.aviz.physVizEval.jzy3d.CustomMouseControl;
import fr.inria.aviz.physVizEval.jzy3d.ModelRotation;
import fr.inria.aviz.physVizEval.scatter3D.Scatter3D;
import fr.inria.aviz.physVizEval.util.CSV;
import fr.inria.aviz.physVizEval.util.GUIUtils;

public class MainTest {

	static final String inputDir = "./logs/exp2";
	static final boolean LOAD_TRIAL_DATA = true;

	static JSeeker seeker;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ArrayList<ParticipantData> data = null;
		
		if (LOAD_TRIAL_DATA) {
			// Let the user chose log files to display and process
			//File[] logFiles = Utils.choseFiles(inputDir, ".plist");
			
			File dir = new File(inputDir);
			File[] logFiles = dir.listFiles(new FileFilter() {
				public boolean accept(File f) {
					if (f.isFile()) {
						if (f.getName().endsWith(".plist")) {
							return true;
						}
					}
					return false;
				}
			});
			
			// Create the data loader
			LogFileLoader loader = new LogFileLoader();
			ParsingOptions parsingOptions = loader.getParsingOptions();
			
			// Configure the data loader
			parsingOptions.setFormatVersion(3);
			parsingOptions.setNumberOfConditions(4);
			parsingOptions.setNumberOfItemsForMultiQuestions(10);
			
			// Load the data
			data = loader.loadFiles(logFiles);
		}

    	// Choose files
		File[] logFiles = Utils.choseFiles(inputDir, ".csv");
//		File[] logFiles = new File[] {new File("logs/exp2/compressed sensor logs/0_s10_virtual-prop_dataset_military_question_3_log_17.csv.gz")};
//		File[] logFiles = new File[] {new File("logs/exp2/compressed sensor logs/0_s10_virtual-mouse_dataset_hiv_question_3_log_16.csv.gz")};	
//		File[] logFiles = new File[] {new File("logs/exp2/compressed sensor logs/0_s11_physical-notouch_dataset_grosscapital_question_1_log_20.csv.gz")};
		// reversals
//		File[] logFiles = new File[] {new File("logs/exp2/compressed sensor logs/0_s3_physical-notouch_dataset_grosscapital_question_4_log_23.csv.gz")};
		Kinematics[] kinematics = new Kinematics[logFiles.length];
		
		// Initialize the 3D chart
		System.err.print("Initializing jz3d...");
    	BarChart3D.initForDebugMode();
    	Scatter3D.init();
    	ModelRotation.DEBUG_ANGLES = true;
    	System.err.println(" Done.\n");

		// Sort files
		Arrays.sort(logFiles, new Comparator<File>(){
		    public int compare(File f1, File f2)
		    {
//		    	int c = getSubjectName(f1.getName()).compareTo(getSubjectName(f2.getName()));
//		    	if (c != 0)
//		    		return c;
//		    	return new Integer(getLogNumber(f1.getName())).compareTo(getLogNumber(f2.getName()));
		    	
		        //return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());		    	
		    	return f1.getName().compareTo(f2.getName());
		    } });

		// Load files
		for (int i=0; i<logFiles.length; i++) {
			kinematics[i] = KinematicsLoader.load(data, logFiles[i]);
			//System.out.println("Loaded " + kin);
		}
		
		// Show selector
		KinematicsSelector.show(logFiles, kinematics);
		
		// Seeker bar
		seeker = new JSeeker();
		JFrame swin = new JFrame();
		swin.getContentPane().add(seeker, BorderLayout.CENTER);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		swin.setBounds(40, screen.height - 100 - 40, screen.width - 40, 100);
		swin.setVisible(true);
		
		// Selector listener that updates the 3D model
		KinematicsSelector.addListener(new KinematicsSelector.Listener() {
			@Override
			public void kinematicsSelected(Kinematics k) {
				stopAnimation();
				showModel(k);
				startAnimation(k);
				
				if (k != null)
				{
		    		Scatter3D.hide();
		    		Scatter3D.display(k.events_smoothed);
				}
			}
			
			public void multipleKinematicsSelected(ArrayList<Kinematics> k)
			{
				Color[] col = new Color[] {new Color(255,0,0,50), new Color(0,255,0,50), new Color(0,0,255,50)};
				if (k!= null)
				{
//					Scatter3D.hide();
					Scatter3D.display(k.get(0).events_smoothed);
					for (int i = 1; i < k.size(); i++)
					{
						Scatter3D.display(k.get(i).events_smoothed, col[i]);
					}
					
				}
			}
		});
	}
	
	public static int getLogNumber(String filename) {
		int n1 = filename.lastIndexOf("log_");
		int n2 = filename.lastIndexOf(".csv");
		String n = filename.substring(n1 + 4, n2);
		return Integer.parseInt(n);
	}
	
	public static String getSubjectName(String filename) {
		int n1 = filename.indexOf("_");
		int n2 = filename.indexOf("_", n1+1);
		String n = filename.substring(n1 + 1, n2);
		return n;
	}
	
	static String lastDataset = "";
	
	public static void showModel(Kinematics k) {
		if (k == null) {
			lastDataset = "";
			BarChart3D.hide();
			Scatter3D.hide();
			seeker.setTime(0);
	    	seeker.setKinematics(null);
			return;
		}
    	//display("data/generated/co2percapita-10x10.csv", ENABLE_STEREO);
    	String datasetfile = "data/datasets/" + k.dataset + ".csv";
    	if (!lastDataset.equals(datasetfile)) {
    		BarChart3D.hide();
    		BarChart3D.display(datasetfile, true, false);
    		BarChart3D.centerOnScreen();
    		
    		System.err.println("getting a new scatter plot...");
    		lastDataset = datasetfile;
    	}
		seeker.setTime(0);
    	seeker.setKinematics(k);
	}
	
	//////////// Animation
	
	static Timer animationTimer = new Timer(20, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			animate();
		}
	});
	static Kinematics animation_k = null;
	static double animation_t0 = 0;
	static double dest_animation_t0 = 0;
	static boolean seeking = false;
	static double seek_t = 0;
	
	public static void startAnimation(Kinematics k) {
		animation_k = k;
		animation_t0 = System.nanoTime() / 1000000000.0;
		if (!animationTimer.isRunning())
			animationTimer.start();
	}
	
	public static void stopAnimation() {
		animationTimer.stop();
	}
	
	private static void animate() {
		if (animation_k != null) {
			double now = System.nanoTime() / 1000000000.0;
			if (seeking) {
				double t_anim = now - animation_t0 - 0.1;
				dest_animation_t0 = animation_t0 + (t_anim - seek_t);
				double smoothing = 0.4;
				animation_t0 = smoothing * animation_t0 + (1 - smoothing) * dest_animation_t0;
			}
			double t = now - animation_t0 - 0.1;
			BarChart3D.modelRotation.setQuaternion(animation_k.getRotationEventRightAfter(t).quaternion);
			seeker.setTime(t);
		}
	}
	
	static void pause() {
		seeking = true;
		//animationTimer.stop();
	}
	
	static void play() {
		seeking = false;
		//if (!animationTimer.isRunning())
		//	animationTimer.start();
	}
	
	static void seek(double t) {
		seek_t = t;
	}
}
