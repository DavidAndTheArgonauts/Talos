package strategy.gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import comms.robot.*;
import comms.vision.*;

import strategy.controller.*;
import strategy.world.*;

public class Gui implements ActionListener, ListSelectionListener{
	
	JButton stopButton;
	JButton startButton;
	JButton changeButton; 
	JButton exitButton;
	
	JList pList;

	PlannerEnum current;
	PlannerEnum next;

	boolean weAreBlue = true;
	boolean started = false;
	
	public Gui(PlannerEnum defaultPlanner){
		current = defaultPlanner;
	}

	public void createGui(){
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e){}
		JFrame frame = new JFrame("Argonauts Strategy Manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new BoxLayout(mainpanel, BoxLayout.PAGE_AXIS));
		
		JPanel toppanel = new JPanel();
		
		stopButton = new JButton("Stop");
		stopButton.setActionCommand("halt");
		startButton = new JButton("Start");
		startButton.setActionCommand("start");
		changeButton = new JButton("Change Planner");
		changeButton.setActionCommand("change");

		stopButton.addActionListener(this);
		startButton.addActionListener(this);
		changeButton.addActionListener(this);
		
		toppanel.add(stopButton);
		toppanel.add(startButton);
		toppanel.add(changeButton);

		JPanel centerpanel = new JPanel();
		

		pList = new JList(PlannerEnum.values());
		pList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//pList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		pList.setVisibleRowCount(-1);
		pList.setSelectedValue(PlannerEnum.ZPLANNER, true);
		pList.addListSelectionListener(this);
		
		JScrollPane listScroller = new JScrollPane(pList);
		listScroller.setPreferredSize(new Dimension(150,100));
		centerpanel.add(listScroller);

		JRadioButton blue = new JRadioButton("We Are Blue");
		blue.setActionCommand("blue");
		blue.setSelected(true);
		
		JRadioButton yellow = new JRadioButton("We Are Yellow");
		yellow.setActionCommand("yellow");

		ButtonGroup group = new ButtonGroup();
		group.add(blue);
		group.add(yellow);
		
		blue.addActionListener(this);
		yellow.addActionListener(this);

		JPanel radioPanel = new JPanel(new GridLayout(0,1));
		radioPanel.add(blue);
		radioPanel.add(yellow);
		centerpanel.add(radioPanel);
		
		JPanel bottompanel = new JPanel();

		exitButton = new JButton("Exit");
		exitButton.setActionCommand("exit");
		
		exitButton.addActionListener(this);		
		
		bottompanel.add(exitButton);

		mainpanel.add(toppanel);
		mainpanel.add(centerpanel);
		mainpanel.add(bottompanel);

		frame.getContentPane().add(mainpanel, BorderLayout.CENTER);
		
		frame.pack();
		//frame.setSize(300,300);
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if ("halt".equals(e.getActionCommand())){
			StrategyManager.haltStrategy(current);
			started = false;
			//stopButton.setText("Clicked");
		} else if ("start".equals(e.getActionCommand())) {
			StrategyManager.startStrategy(current);
			started = true;
			//startButton.setText("Clicked");
		} else if ("change".equals(e.getActionCommand())){
			//Halt currently running planner, set current = next, execute current planner
			PlannerEnum temp = current;
			current = next;
			StrategyManager.haltStrategy(temp);
			StrategyManager.startStrategy(current);
			
			//changeButton.setText("Clicked");
		} else if ("exit".equals(e.getActionCommand())){
			//Stop all Planners, stop vision, stop robot conenection exit	
			StrategyManager.haltStrategy(current);
			StrategyManager.getCommand().unsubscribe(StrategyManager.getCommand());
			StrategyManager.getVision().close();
			//exitButton.setText("Clicked");
			System.exit(0);
		} else if ("blue".equals(e.getActionCommand())){
			
			weAreBlue = true;
			StrategyManager.getWorld().setBlue(weAreBlue);
		} else if ("yellow".equals(e.getActionCommand())){
			//change to world set yellow
			weAreBlue = false;
			StrategyManager.getWorld().setBlue(weAreBlue);
		}
		
	}

	public void valueChanged(ListSelectionEvent e) {
		if(e.getValueIsAdjusting() == false){
			if(pList.getSelectedValue() == null){
				changeButton.setEnabled(false);
			} else {
				changeButton.setEnabled(true);
				next = (PlannerEnum) pList.getSelectedValue();
			}
		}
	}

}
