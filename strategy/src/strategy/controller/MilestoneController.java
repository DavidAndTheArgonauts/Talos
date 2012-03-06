package strategy.controller;

import comms.robot.*;
import strategy.world.*;
import strategy.mode.*;
import java.lang.reflect.Constructor;

import java.io.*;

public class MilestoneController extends Thread
{
	
	private static final int CONTROL_TIMEOUT = 1000;
	
	private Commander commander;
	private World world;
	
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
		
		try {
			//reflectmode = Class.forName
			//reflectMode = Constructor.forName(args[4]).newInstance(c);
			
			Constructor[] ctors = AbstractMode.class.getDeclaredConstructors();
			Constructor ctor = null;
			for (int i = 0; i < ctors.length; i++) {
				ctor = ctors[i];
				if (ctor.getGenericParameterTypes().length == 1)
				break;
			}
			
			ctor.setAccessible(true);
 	    	reflectMode = (AbstractMode)ctor.newInstance(c);
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		Thread gcThread = new MilestoneController(w,c, reflectMode);
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
		while (!Thread.interrupted())
		{

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
