package fr.inria.aviz.physVizEval.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserInfo extends JFrame {

		//Class Declarations
		JTextField userName, group;
		JButton okButton;
		String disp = "";
//		TextHandler handler = null;
		//Constructor
		public UserInfo(ActionListener l) {
			super("User Info");
			Container container = getContentPane();
			container.setLayout(new FlowLayout());
			userName = new JTextField(10);
			group = new JTextField(1);
			okButton = new JButton("OK");
			
			container.add(new JLabel("User name: "));
			container.add(userName);
//			handler = new TextHandler();
			okButton.addActionListener(l);
//			okButton.addActionListener(handler);
			container.add(new JLabel("Group: "));
			container.add(group);
			container.add(okButton);
			
			setSize(325, 100);
			setVisible(true);
		}
		
		protected void setUserName(String userName)
		{
			
		}
		
		public String getUserName()
		{
			return this.userName.getText();
		}
		
		public int getGroupID()
		{
			return Integer.parseInt(this.group.getText());
		}
		
		//Inner Class TextHandler
//		private class TextHandler implements ActionListener {
//
//			public void actionPerformed(ActionEvent e) {
//				if (e.getSource() == okButton) {
//					disp = "text1 : " + e.getActionCommand();
//				} else if (e.getSource() == userName) {
//					disp = "text3 : " + e.getActionCommand();
//				}
//				JOptionPane.showMessageDialog(null, disp);
//			}
//		}
//		//Main Program that starts Execution
//		public static void main(String args[]) {
//			UserInfo test = new UserInfo(args[]);
//			test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		}
}

