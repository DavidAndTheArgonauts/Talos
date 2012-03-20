package gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import comms.robot.*;
import comms.vision.*;

import java.lang.reflect.Constructor;

import strategy.controller.*;
import strategy.mode.*;

public class ControlGUI implements ActionListener, ListSelectionListener
{

	JButton stopButton;
	JButton startButton;
	JButton changeButton;
	JButton exitButton;

	JList pList;

	ModeEnum current = ModeEnum.GotoBallMode;
	ModeEnum next;

	boolean weAreBlue = true;
	boolean started = false;

	Commander commander;

	Class cls;

	public 	ControlGUI(Commander commander)
	{
		this.commander = commander;
	}

	public void createGui()
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e)
		{}
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


		pList = new JList(ModeEnum.values());
		pList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		//pList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		pList.setVisibleRowCount(-1);
		pList.setSelectedValue(current, true);
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

	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand().equals(startButton.getActionCommand()))
		{
			String mode = pList.getSelectedValue().toString();
			System.out.println(mode);
			AbstractMode reflectMode = null;
			Class cls;

			try 
			{
				cls = Class.forName("strategy.mode." + mode);
				Constructor[] ctors = cls.getDeclaredConstructors();
				Constructor ctor = null;
				for (int i = 0; i < ctors.length; i++) 
				{
					ctor = ctors[i];
					if (ctor.getGenericParameterTypes().length == 1)
						break;
				}

				ctor = cls.getDeclaredConstructor(Commander.class);

				ctor.setAccessible(true);
				//reflectMode = new OffensiveMode(c);
				reflectMode = (AbstractMode)ctor.newInstance(commander);

			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
				System.out.println("Unable to load class");
			} 
		}

		if (e.getActionCommand().equals(exitButton.getActionCommand()))
		{
			System.exit(0);
		}
	}

	public void valueChanged(ListSelectionEvent e) 
	{

	}

}
