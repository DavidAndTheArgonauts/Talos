package strategy.controller;

import gui.*;
import comms.robot.*;
import strategy.world.*;
import strategy.mode.*;
import java.lang.reflect.Constructor;

import java.io.*;

public class MilestoneController extends AbstractController
{
	
	private static final int CONTROL_TIMEOUT = 1000;
	
	private Commander commander;
	private World world;
	
	private static GUI gui;
	
	AbstractMode currentMode = null;
	
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args)
	{
		
		// check args
		if (args.length < 4)
		{
			System.out.println("Args required: <proxy host> <proxy port> <vision port> <colour: yellow or blue> <mode>");
			System.exit(0);
		}
		
		// create variables for args
		String proxyHost = args[0];
		int proxyPort = Integer.parseInt(args[1]);
		int visionPort = Integer.parseInt(args[2]);
		
		int color = -1;
		if (args[3].equals("yellow"))
		{
			color = World.ROBOT_YELLOW;
		}
		else if (args[3].equals("blue"))
		{
			color = World.ROBOT_BLUE;
		}
		else
		{
			System.out.println("Enter a valid color (yellow or blue)");
		}
		
		
		
		// create commander and connect
		Commander c = new Commander();
		c.connect(proxyHost,proxyPort);
		
		// if not connected, quit
		if (!c.isConnected())
		{
			System.out.println("Cannot connect to proxy");
			System.exit(0);
		}
		
		System.out.println("Connected to proxy");
		AbstractMode reflectMode = null;
		Class cls;
		
		try {
			//reflectmode = Class.forName
			//reflectMode = Constructor.forName(args[4]).newInstance(c);
			cls = Class.forName("strategy.mode." + args[4]);
			Constructor[] ctors = cls.getDeclaredConstructors();
			Constructor ctor = null;
			for (int i = 0; i < ctors.length; i++) {
				ctor = ctors[i];
				if (ctor.getGenericParameterTypes().length == 1)
				break;
			}
			
			ctor = cls.getDeclaredConstructor(Commander.class);
			
			ctor.setAccessible(true);
			//reflectMode = new OffensiveMode(c);
 	    	reflectMode = (AbstractMode)ctor.newInstance(c);
	
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("HELLLO");
		}
		
		// create world and listen for vision
		double[] goal = new double[2];
		
		if (args[5].equals("right"))
		{
			goal = World.GOAL_RIGHT;
		}
		else if (args[5].equals("left"))
		{
			goal = World.GOAL_LEFT;
		}
		else
		{
			System.out.println("Enter a valid goal (left or right)");
		}
		
		World w = new World(color,goal);
		w.listenForVision(visionPort);
		
		// wait until world is giving real states
		while(w.getWorldState() == null)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
			
			}
		}
		
		System.out.println("World data found!");
		
		
		gui = new GUI(w.getWorldState(),color,goal);
		
		
		// create controller thread and begin
		AbstractController gcThread = new MilestoneController(w,c, reflectMode);
		gcThread.start();
		
		System.out.println("Registering controller");
		if (!c.registerController(gcThread))
		{
			System.out.println("Unable to register controller");
		}
		
		System.out.println("Press <enter> to quit");
		
		// wait for enter to be pressed
		try 
		{
			reader.readLine();
		} 
		catch (Exception e)
		{
			System.out.println("Read line exception, quitting");
		}
		
		// interrupt and wait for thread to die
		gcThread.interrupt();
		while (gcThread.isAlive())
		{
			try
			{
				gcThread.join();
			}
			catch (InterruptedException e)
			{
			
			}
		}
		
		// stop robot and disconnect
		c.stop();
		c.waitForQueueToEmpty();
		
		// close world connection
		w.close();
		
		c.disconnect();
		
	}
	
	public MilestoneController(World world, Commander commander, AbstractMode mode)
	{
		
		this.world = world;
		this.commander = commander;
		this.currentMode = mode;
		
	}
	
	public void run()
	{
			
		long lastChange = -1, lastUpdate = -1;
		
		currentMode.reset(world);
		
		while (true)
		{
			
			if (currentMode != null && controllerInterrupted())
			{
				
				System.out.println("Controller found interrupt");
				
				if (isQuitInterrupt())
				{
					return;
				}
				
				int interrupt = getControllerInterrupt();
				 
				currentMode.handleInterrupt(world,interrupt);
				 
			}
			else if (currentMode == null && controllerInterrupted())
			{
				
				System.out.println("Controller found interrupt");
				
				if (isQuitInterrupt())
				{
					return;
				}
				
				// clear interrupt
				int interrupt = getControllerInterrupt();
				
			}
			
			// update
			currentMode.update(world);
			
			// draw
			gui.setWorldState(world.getWorldState());
			
			// block until new world update
			while (lastUpdate == world.getWorldState().getCreatedMillis())
			{
				
				// we could block forever if we have been given the kill command
				if (controllerInterrupted())
				{
					 
					 System.out.println("Controller found interrupt");
					 
					 if (isQuitInterrupt())
					 {
					 	return;
					 }
					 
					 int interrupt = getControllerInterrupt();
					 
					 currentMode.handleInterrupt(world,interrupt);
					 
				}
				
			}
			
			lastUpdate = world.getWorldState().getCreatedMillis();
			
		}
		
	}
	
}
