package fr.inria.aviz.physVisEval.logs.kinematics;
import java.awt.*;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.table.*;

/**
 * A better-looking table than JTable. In particular, on Mac OS this looks
 * more like a Cocoa table than the default Aqua LAF manages.
 *
 * @author Elliott Hughes
 */
public class CustomJTable extends JTable {
    public CustomJTable(TableModel m) {
    	super(m);
    }

    /**
     * Paints empty rows too, after letting the UI delegate do
     * its painting.
     */
    public void paint(Graphics g) {
        super.paint(g);
    }

    /**
     * Changes the behavior of a table in a JScrollPane to be more like
     * the behavior of JList, which expands to fill the available space.
     * JTable normally restricts its size to just what's needed by its
     * model.
     */
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            JViewport parent = (JViewport) getParent();
            return (parent.getHeight() > getPreferredSize().height);
        }
        return false;
    }

    static final Color bg0 = UIManager.getColor("Table.background");
    static final Color[] bg = new Color[] {
    	bg0,
    	new Color(bg0.getRed()-30, bg0.getGreen()-30, bg0.getBlue()-20),
    	new Color(bg0.getRed()-20, bg0.getGreen()-5, bg0.getBlue()-20),
    	new Color(bg0.getRed(), bg0.getGreen()-50, bg0.getBlue()-50)
    };
    static final Color sbg0 = UIManager.getColor("Table.selectionBackground");
    static final Color[] sbg = new Color[] {
    	sbg0,
    	new Color(sbg0.getRed()-30, sbg0.getGreen()-30, sbg0.getBlue()-20),
    	new Color(sbg0.getRed()-20, sbg0.getGreen()-15, sbg0.getBlue()-20),
    	new Color(sbg0.getRed(), sbg0.getGreen()-50, sbg0.getBlue()-50)
    };
    
    int colorIndex(int row, int column) {
    	
    	// This is ugly, I know.
    	
    	int c = getColumnModel().getColumn(column).getModelIndex();
    	String name = getModel().getColumnName(c);
    	if (getModel().getValueAt(row, c) instanceof Boolean) {
    		return ((Boolean)getModel().getValueAt(row, c)).booleanValue()
    			? 3 : 0;
    	}
    	    	
    	if (getModel().getValueAt(row, c) instanceof String) {
			String s = (String)getModel().getValueAt(row, c);
			
			if (s.equals("MISSING"))
				return 3;
			
	    	if (name.equals("Subject#")) {
//	    		if (s.equals("Control"))
//	    			return 2;
	    		int n = Integer.parseInt(s);
	    		return n%2 == 1
				? 1 : 0;
	    	}
	    	if (name.equals("Technique")) {
	    		return (s.equals("tangible"))
				? 1 : 0;
	    	}
	    	if (name.equals("Task")) {
	    		return (s.equals("distractor"))    			
	    		? 1 : 0;
	    	}
	    	if (name.equals("Trial")) {
	    		try {
		    		int n = Integer.parseInt(s);
		    		return n%2 == 1
					? 1 : 0;
	    		} catch (Exception e) {
	    			return 3;
	    		}
	    	}
    	}
    	
    	if (getModel().getValueAt(row, c) instanceof Double) {
    		double n = (Double)getModel().getValueAt(row, c);
    		return n%2 == 1
			? 1 : 0;
    	}
    	
    	if (getModel().getValueAt(row, c) instanceof Integer) {
    		int n = (Integer)getModel().getValueAt(row, c);
    		return n%2 == 1
			? 1 : 0;
    	}
    	
    	return 0;
    }
    
    /**
     * Shades alternate rows in different colors.
     */
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (isCellSelected(row, column) == false) {
       		System.err.println("setting background color for table");
       		c.setBackground(bg[colorIndex(row, column)]);
            c.setForeground(UIManager.getColor("Table.foreground"));
        } else {
       		System.err.println("setting background color for table");
       		c.setBackground(sbg[colorIndex(row, column)]);
        	c.setForeground(UIManager.getColor("Table.selectionForeground"));
        }
        return c;
    }
    
	protected void processKeyEvent(KeyEvent e) {
		if (e.getID() == e.KEY_PRESSED && e.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_UP) {
			if (getSelectedRows().length > 1) {
				moveSelection(-1);
				return;
			}
		} else if (e.getID() == e.KEY_PRESSED && e.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (getSelectedRows().length > 1) {
				moveSelection(1);
				return;
			}			
		}
		super.processKeyEvent(e);
	}
	
	protected void moveSelection(int direction) {
		int[] r = getSelectedRows();
		int minr = r[0];
		int maxr = r[r.length - 1];
		int span = maxr - minr + 1;
		int newminr = minr + direction * span;
		if (newminr < 0 || newminr + span - 1 > getModel().getRowCount() - 1)
			return;
		clearSelection();
		for (int i=0; i<r.length; i++) {
			addRowSelectionInterval(r[i] + newminr - minr, r[i] + newminr - minr);
		}
		changeSelection(newminr, newminr + span - 1, true, true);
	}
}
