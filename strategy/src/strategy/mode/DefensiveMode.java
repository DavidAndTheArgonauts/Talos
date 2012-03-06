package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class DefensiveMode extends AbstractMode
{
	
	private int touchInterrupt = -1, distanceInterrupt = -1;
	
	private int turnInterrupt = -1;
	
	private boolean complete = false;
	
	public DefensiveMode(Commander commander)
	{
		super(commander);
	}
	
	public double getViability(World world)
	{
		
		return 1;
		
	}
	
	public void reset(World world)
	{
		
		/*
		distanceInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,50);
		
		touchInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_TOUCH,InterruptManager.MODE_EITHER);
		
		System.out.println("Interrupts registered (distance = " + distanceInterrupt + ", touch = " + touchInterrupt + ")");
		
		commander.setSpeed(20,20);
		*/
		turnInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,30);
		
		commander.setSpeed(5,10);
		
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void update(World world)
	{
		
		if (complete) return;
		
		if (turnInterrupt == -1)
		{
			reset(world);
		}
		
	}
	
	public void handleInterrupt(World world,int interrupt)
	{
		
		/*
		System.out.println("Interrupt handled (id = " + interrupt + ")");
		
		if (interrupt == distanceInterrupt)
		{
			commander.stop();
			complete = true;
		}
		else if (interrupt == touchInterrupt)
		{
			commander.setSpeed(-20,-20);
			distanceInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,-20);
		}
		*/
		
		System.out.println("Interrupt handled (id = " + interrupt + ")");
		
		if (interrupt == turnInterrupt)
		{
			commander.stop();
			complete = true;
		}
		
	}
	
}
