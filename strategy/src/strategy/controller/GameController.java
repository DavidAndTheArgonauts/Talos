package strategy.controller;

import comms.robot.*;
import strategy.world.*;
import strategy.mode.*;

import java.io.*;

public class GameController extends Thread
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
		World w = new World(color);
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
		Thread gcThread = new GameController(w,c);
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
		
		while (!Thread.interrupted())
		{
			
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
				
				currentMode = newMode;
				
			}
			
			// update
			currentMode.update(world);
			
			// block until new world update
			while (lastUpdate == world.getWorldState().getCreatedMillis())
			{
				
				// we could block forever if we have been given the kill command
				if (Thread.interrupted())
				{
					return;
				}
				
				Thread.yield();
				
			}
			
			lastUpdate = world.getWorldState().getCreatedMillis();
			
		}
		
	}
	
}