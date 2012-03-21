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
import strategy.world.*;

public class ControlGUI implements ActionListener, ListSelectionListener
{

	JButton stopButton;
	JButton startButton;
	JButton changeButton;
	JButton exitButton;
	JButton pauseButton;

	JList pList;

	ModeEnum current = ModeEnum.GotoBallMode;
	ModeEnum next;

	boolean weAreBlue = true;
	boolean shootingLeft = true;
	boolean paused = false;

	Commander commander;
	
	
	
	private AbstractController controller = null;
	private int visionPort;
	
	public static void main(String[] args)
	{
		
		if (args.length < 3)
		{
			System.out.println("Args required: <proxy host> <proxy port> <vision port>");
		}
		
		String proxyHost = args[0];
		int proxyPort = Integer.parseInt(args[1]);
		int visionPort = Integer.parseInt(args[2]);
		
		Commander commander = new Commander();
		commander.connect(proxyHost,proxyPort);

		// if not connected, quit
		if (!commander.isConnected())
		{
			System.out.println("Cannot connect to proxy");
			System.exit(0);
		}
		
		ControlGUI cg = new ControlGUI(commander, visionPort);
		cg.createGui();
		
	}
	
	public ControlGUI(Commander commander, int visionPort)
	{
		this.commander = commander;
		this.visionPort = visionPort;
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
		pauseButton = new JButton("Pause/Resume");
		pauseButton.setActionCommand("pause");

		stopButton.addActionListener(this);
		startButton.addActionListener(this);
		pauseButton.addActionListener(this);
		
		toppanel.add(startButton);
		toppanel.add(stopButton);
		toppanel.add(pauseButton);

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
		
		
		
		
		JRadioButton left = new JRadioButton("Shooting left");
		left.setActionCommand("left");
		left.setSelected(true);

		JRadioButton right = new JRadioButton("Shooting right");
		right.setActionCommand("right");

		ButtonGroup goalGroup = new ButtonGroup();
		goalGroup.add(left);
		goalGroup.add(right);

		left.addActionListener(this);
		right.addActionListener(this);

		JPanel goalRadioPanel = new JPanel(new GridLayout(0,1));
		goalRadioPanel.add(left);
		goalRadioPanel.add(right);
		centerpanel.add(goalRadioPanel);
		
		
		

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
				reflectMode = (AbstractMode)ctor.newInstance(commander);

			} 
			catch (Exception e1) 
			{
				e1.printStackTrace();
				System.out.println("Unable to load class");
				return;
			}
			
			if (controller != null)
			{
				
				controller.controllerInterruptQuit();
				while (controller.isAlive())
				{
					try
					{
						controller.join();
					}
					catch (InterruptedException ie) {}
				}
				
			}
			
			// create world
			
						
			int color = ((weAreBlue) ? World.ROBOT_BLUE : World.ROBOT_YELLOW);
			double[] goal = ((shootingLeft) ? World.GOAL_LEFT : World.GOAL_RIGHT);
		
			World world = new World(color, goal);
			world.listenForVision(visionPort);			

			// wait until world is giving real states
			while(world.getWorldState() == null)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException ie)
				{
			
				}
			}
			
			controller = new GUIController(commander,world,reflectMode);
			controller.start();
			
		}
		
		if (e.getActionCommand().equals("yellow"))
		{
			weAreBlue = false;
		}
		if (e.getActionCommand().equals("blue"))
		{
			weAreBlue = true;
		}
		if (e.getActionCommand().equals("left"))
		{
			shootingLeft = true;
		}
		if (e.getActionCommand().equals("right"))
		{
			shootingLeft = false;
		}
		/*
		if (e.getActionCommand().equals("pause"))
		{
			if(!paused){
				pauseButton.setText("Paused");
				paused = !paused;
				commander.togglePause();
				commander.pause();
			} else {
				pauseButton.setText("Running");
				paused = !paused;
				commander.togglePause();

			}
		}
		*/
		if (e.getActionCommand().equals("halt"))
		{
			
			if (controller != null)
			{
				
				controller.controllerInterruptQuit();
				while (controller.isAlive())
				{
					try
					{
						controller.join();
					}
					catch (InterruptedException ie) {ie.printStackTrace();}
				}
				
			}
			
			commander.setSpeed(1,1);
			commander.setSpeed(0,0);
		}

		if (e.getActionCommand().equals(exitButton.getActionCommand()))
		{
			System.exit(0);
		}
	}

	public void valueChanged(ListSelectionEvent e) 
	{
		if(e.getValueIsAdjusting() == false){
			if(pList.getSelectedValue() != null){
				next = (ModeEnum) pList.getSelectedValue();	
			}

		}
	}

}
