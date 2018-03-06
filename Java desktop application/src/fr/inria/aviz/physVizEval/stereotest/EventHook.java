/*
 * Created on 28 nov. 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package fr.inria.aviz.physVizEval.stereotest;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author Pierre
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class EventHook extends EventQueue {
	  private static final EventQueue sysQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
	  private static final EventHook sharedInstance = new EventHook(); // singleton
	  private static boolean keyDisablingActive = false;
	  private static boolean mouseDisablingActive = false;
	  private static boolean pushed = false;
	  private static Vector<AWTEventListener> keymonitors = new Vector<AWTEventListener>();
	  private static Vector<AWTEventListener> mousemonitors = new Vector<AWTEventListener>();
	  private static Vector<AWTEvent> authorizedEvents = new Vector<AWTEvent>();

	/**
	 *  We activate/deactivate instead of pushing/popping because this feature seems to be bugged
	 */


	  protected void dispatchEvent(AWTEvent e) {
	  		if (authorizedEvents.contains(e)) {
	  			super.dispatchEvent(e);
	  			authorizedEvents.remove(e);
	  			return;
	  		}

			if (e instanceof KeyEvent) {
				  // Dispatch to AWTKeyboard Devices
				  for (Iterator<AWTEventListener> i = keymonitors.iterator(); i.hasNext();)
						i.next().eventDispatched(e);
				  if (keyDisablingActive) {
						KeyEvent ke = (KeyEvent)e;
						// dispatch Ctrl + C to editor
						if (ke.getID() == KeyEvent.KEY_PRESSED && ke.getKeyCode() == KeyEvent.VK_C && (ke.getModifiers() & InputEvent.ALT_MASK) > 0) {
							  super.dispatchEvent(e);
						}
						return; // other key events are not dispatched
				  }
			}

			if (e instanceof MouseEvent) {
				// Dispatch to AWTPointer Devices
				for (Iterator<AWTEventListener> i = mousemonitors.iterator(); i.hasNext();)
					  i.next().eventDispatched(e);
				  if (mouseDisablingActive)
				  	return; // mouse events are not dispatched
			}

			// Dispatch to Swing
			super.dispatchEvent(e);
	  }
	  /**
	   * Causes all key events to be disabled
	   */
	  public static void activateKeyDisabling() {
	  		checkInstall();
			keyDisablingActive = true;
	  }
	  /**
	   * Enables key events
	   */
	  public static void deactivateKeyDisabling() {
			keyDisablingActive = false;
	  }
	/**
	 * Causes all mouse events to be disabled
	 */
	public static void activateMouseDisabling() {
		  checkInstall();
		  mouseDisablingActive = true;
	}
	/**
	 * Enables mouse events
	 */
	public static void deactivateMouseDisabling() {
		  mouseDisablingActive = false;
	}

	protected static void checkInstall() {
		if (!pushed) {
			  EventQueue.invokeLater(new Runnable() {
					public void run() {sysQueue.push(sharedInstance);}
			  });
			  pushed = true;
		}
	  }
	  public static void addKeyMonitor(AWTEventListener l) {
	  		checkInstall();
			if (!keymonitors.contains(l))
				  keymonitors.add(l);
	  }
	  public static void removeKeyMonitor(AWTEventListener l) {
			if (keymonitors.contains(l))
				  keymonitors.remove(l);
	  }
		public static void addMouseMonitor(AWTEventListener l) {
			checkInstall();
			  if (!mousemonitors.contains(l))
					mousemonitors.add(l);
		}
		public static void removeMouseMonitor(AWTEventListener l) {
			  if (mousemonitors.contains(l))
					mousemonitors.remove(l);
		}
	 /**
	  * Queues an event and process it despite disablings.
	  */
	 public static void postEventAndForceProcess(AWTEvent e) {
	 	if (!pushed)
	 		sysQueue.postEvent(e);
	 	else {
	 		authorizedEvents.add(e);
	 		sharedInstance.postEvent(e);
	 	}
	 }
}
