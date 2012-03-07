package strategy.controller;

import comms.robot.*;
import strategy.world.*;
import strategy.mode.*;

import java.io.*;

public class GameController extends AbstractController
{
	
	private static final int CONTROL_TIMEOUT = 1000;
	
	private Commander commander;
	private World world;
	
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args)
	{
		
		// check args
		if (args.length < 4)
		{
			System.out.println("Args required: <proxy host> <proxy port> <vision port> <colour: yellow or blue>");
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
		
		// create world and listen for vision
		double[] goal = new double[2];
		
		if (args[4].equals("right"))
		{
			goal = World.GOAL_RIGHT;
		}
		else if (args[4].equals("left"))
		{
			goal = World.GOAL_LEFT;
		}
		else
		{
			System.out.println("Enter a valid goal (left or right)");
		}
		
		World w = new World(color, goal);
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
		
		// create controller thread and begin
		GameController gcThread = new GameController(w,c);
		
		// register the controller for interrupts
		c.registerController(gcThread);
		
		gcThread.start();
		
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
		while (!gcThread.controllerInterrupt(InterruptManager.INTERRUPT_QUIT,-1))
		{
			Thread.yield();
		}
		
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
	
	public GameController(World world, Commander commander)
	{
		
		this.world = world;
		this.commander = commander;
		
	}
	
	public void run()
	{
		
		// instansiate all modes available
		AbstractMode[] availableModes = {
			new DefensiveMode(commander),
			new OffensiveMode(commander)
		};
		AbstractMode currentMode = null;
		
		long lastChange = -1, lastUpdate = -1;
		
		/* coding here */
		while (true)
		{
		
			if (currentMode != null && controllerInterrupted())
			{
				
				if (isQuitInterrupt())
				{
					return;
				}
				
				int interrupt = getControllerInterrupt();
				 
				currentMode.handleInterrupt(world,interrupt);
				 
			}
			else if (controllerInterrupted())
			{
				
				
				
				if (isQuitInterrupt())
				{
					return;
				}
				
				int interrupt = getControllerInterrupt();
				
			}
			
			if (System.currentTimeMillis() - lastChange > CONTROL_TIMEOUT)
			{
				
				// possibility to change mode
				double maxViability = -1;
				AbstractMode newMode = null;
				for (int i = 0; i < availableModes.length; i++)
				{
					
					double viability = availableModes[i].getViability(world);
					
					if (viability > maxViability)
					{
						newMode = availableModes[i];
						maxViability = viability;
					}
					
				}
				
				// call reset when changing mode
				if (currentMode != null && newMode != null && !currentMode.equals(newMode))
				{
					commander.getInterruptManager().clearInterrupts();
					newMode.reset(world);
				}
				
				currentMode = newMode;
				
			}
			
			// update
			currentMode.update(world);
			
			// block until new world update
			while (lastUpdate == world.getWorldState().getCreatedMillis())
			{
				
				// we could block forever if we have been given the kill command
				if (controllerInterrupted())
				{
					 
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
