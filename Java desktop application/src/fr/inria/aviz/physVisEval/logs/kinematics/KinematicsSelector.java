package fr.inria.aviz.physVisEval.logs.kinematics;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import table.model.EnvelopeTableModel;

import fr.inria.aviz.physVizEval.util.GUIUtils;

public class KinematicsSelector extends JFrame {

	public static interface Listener {
		public void kinematicsSelected(Kinematics k);
		public void multipleKinematicsSelected(ArrayList<Kinematics> k);
	}

    static KinematicsSelector instance = null;

	Vector<Object> columnNames = new Vector<Object>();
 

    CustomJTable table;
    ArrayList<Listener> listeners = new ArrayList<KinematicsSelector.Listener>();
    int selectedRow = -1;

    KinematicsSelector(File[] files, final Kinematics[] data) {
    	
    	int nrows = data.length;

    	Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
    	columnNames.add("File");
    	columnNames.add("User");
    	columnNames.add("Condition");
    	columnNames.add("Dataset");
    	columnNames.add("Question");
    	columnNames.add("Total Distance");
    	columnNames.add("Avg Speed");

    	for (int r=0; r<nrows; r++) {
//    		cells[r][0] = files[r].getName();
//    		if (data[r] != null) {
//	    		cells[r][1] = data[r].username; 
//	    		cells[r][2] = data[r].condition;
//	    		cells[r][3] = data[r].dataset;
//	    		cells[r][4] = "" + data[r].question;
//	    		cells[r][5] = "" + data[r].distanceTraveled;
//	    		cells[r][6] = "" + data[r].averageSpeed;
//    		} else {
//    			cells[r][1] = "invalid file"; 
//    		}
//    	}
    	
//		for (ParticipantData pd : data) {
//			for (ConditionData cd : pd.getConditionData()) {
//				for (TrialData td : cd.getTrialData()) {
					Vector<Object> row = new Vector<Object>();
					row.add(files[r].getName());
					if (data[r] != null) {
						row.add(data[r].username);
						row.add(data[r].condition);
						row.add(data[r].dataset);
						row.add(data[r].question);
						row.add(data[r].distanceTraveled);
						row.add(data[r].averageSpeed);
					} else {
		    			row.add("invalid file"); 
		    		}
					tableData.add(row);

			
		}
    	
    	
    	TableModel model = new DefaultTableModel(tableData, columnNames);
    	final EnvelopeTableModel model2 = new EnvelopeTableModel(model);
    	
        table = new CustomJTable(model2);
        
        
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
//				int[] rows = table.getSelectedRows();
//				if (rows == null || rows.length == -1)
//					displayTrialData(null);
//				else {
//					ArrayList<TrialData> data = new ArrayList<TrialData>();
//					for (int i=0; i<rows.length; i++) {
//						Integer key = (Integer)table.getModel().getValueAt(rows[i], keyIndex);
//						data.add(allTrials.get(key));
//					}
//					displayTrialData(data);
				System.err.println("row selected: " + table.getSelectedRowCount());
				if (table.getSelectedRowCount() == 1)
				{
					int row = table.getSelectedRow();
					if (row != selectedRow) {
						for (Listener l : listeners) {
							l.kinematicsSelected(data[row]);
						}
						selectedRow = row;
					}
				}
				else
				{
					if (table.getSelectedRowCount() > 1)
					{
						for (Listener l : listeners)
						{
							ArrayList<Kinematics> k = new ArrayList<Kinematics>();
							for (int i : table.getSelectedRows())
							{
								k.add(data[i]);
							}
							l.multipleKinematicsSelected(k);
						}
					}
				}

				
			}			
		});
		table.setShowGrid(true);
		
		final JButton sort = new JButton("sort");
		sort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int c0 = table.getColumnModel().getColumn(0).getModelIndex();
				int c1 = table.getColumnModel().getColumn(1).getModelIndex();
				int c2 = table.getColumnModel().getColumn(2).getModelIndex();
				int c3 = table.getColumnModel().getColumn(3).getModelIndex();
				int c4 = table.getColumnModel().getColumn(4).getModelIndex();
				int c5 = table.getColumnModel().getColumn(5).getModelIndex();
				int c6 = table.getColumnModel().getColumn(6).getModelIndex();
				int[] sortColumnIndexes=new int[] {c0, c1, c2, c3, c4, c5, c6};
				boolean[] sortOrders=new boolean[] {true, true, true, true, true, true, true};
				try {
					model2.setSort(sortColumnIndexes, sortOrders);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				sort.setEnabled(false);
			}
		});

		//System.out.println(table.getColumnModel().getColumn(1).getCellRenderer());
		//table.getColumnModel().getColumn(3).setCellRenderer(new RedCellRenderer());
		table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			public void columnAdded(TableColumnModelEvent e) {
			}
			public void columnMarginChanged(ChangeEvent e) {
			}
			public void columnMoved(TableColumnModelEvent e) {
				sort.setEnabled(true);
			}
			public void columnRemoved(TableColumnModelEvent e) {
			}
			public void columnSelectionChanged(ListSelectionEvent e) {
			}			
		});
		
		

        
        
        
        
        
        
        
        
        
        
        
        
        
//        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				int row = table.getSelectedRow();
//				if (row != selectedRow) {
//					for (Listener l : listeners) {
//						l.kinematicsSelected(data[row]);
//					}
//					selectedRow = row;
//				}
//			}
//		});
        
        JScrollPane scrollPane = new JScrollPane(table);
        Container c = getContentPane();
        c.add(scrollPane, BorderLayout.CENTER);
        c.add(sort, BorderLayout.SOUTH);
    }
        
    public static void show(File[] files, Kinematics[] data) {
    	instance = new KinematicsSelector(files, data);
    	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    	int w = 400;
    	int h = (int)(screen.height * 0.75);
    	instance.setBounds(screen.width - w - 30, (screen.height - h) / 2, w, h);
    	instance.setVisible(true);
    	instance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void addListener(Listener l) {
    	instance.listeners.add(l);
    }
    
    public static void focus() {
    	instance.toFront();
    	instance.table.requestFocus();
    }
    
}
