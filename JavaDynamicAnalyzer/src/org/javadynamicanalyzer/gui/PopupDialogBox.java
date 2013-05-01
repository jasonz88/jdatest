package org.javadynamicanalyzer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class PopupDialogBox{
	  JFrame frame;
	  public static void main(String[] args){
	      PopupDialogBox dialogBox = new PopupDialogBox();
	  }

	  public PopupDialogBox(){
	  frame = new JFrame("Show popup Dialog Box");
	  JButton button = new JButton("Click to viw dialog box");
	  button.addActionListener((ActionListener) new MyAction());
	  frame.add(button);
	  frame.setSize(400, 400);
	  frame.setVisible(true);
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  }

	  public class MyAction implements ActionListener{
	  public void actionPerformed(ActionEvent e){
	  JOptionPane.showMessageDialog(frame,"Welcome in Roseindia");
	  }
	  }                                       
	}
