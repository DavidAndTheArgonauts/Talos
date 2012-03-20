package strategy.controller;

import gui.*;
import comms.robot.*;
import strategy.world.*;
import strategy.mode.*;
import java.lang.reflect.Constructor;

import java.io.*;

public class GUIController extends AbstractController
{
	
	private Commander commander;
	private World world;
	
	private GUI gui;
	
	private AbstractMode currentMode = null;

	public GUIController(Commander commander, World world, AbstractMode mode)
	{
		
		gui = new GUI(world.getWorldState(),world.getColor(),world.getGoalCoords());
		
		System.out.println("Registering controller");
		if (!commander.registerController(this))
		{
			System.out.println("Unable to register controller");
		}
		
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
					System.out.println("Found quit interrupt");
					cleanup();
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
					System.out.println("Found quit interrupt");
					cleanup();
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
					 	System.out.println("Found quit interrupt");
					 	cleanup();
					 	return;
					 }
					 
					 int interrupt = getControllerInterrupt();
					 
					 currentMode.handleInterrupt(world,interrupt);
					 
				}
				
			}
			
			lastUpdate = world.getWorldState().getCreatedMillis();
			
		}
		
	}
	
	private void cleanup()
	{
		
		System.out.println("<< CLEANING UP >>");
		
		gui.close();
		gui = null;
		
		world.close();
		
		
		commander.unregisterController();
		
	}
	
}
