package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class InterruptTestMode extends AbstractMode
{
	
	private int interruptId = -1;
	private boolean complete = false;
	
	public InterruptTestMode(Commander commander)
	{	
		super(commander);
		
		

	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void reset(World world){}
	
	public void update(World world)
	{
		
		if (complete)
			return;

		if (interruptId == -1)
			interruptId = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,180);
		
		System.out.println("Turning..");
		
		commander.setSpeed(-10,10);
		
	}
	
	
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		if (interrupt == interruptId)
		{
			complete = true;
			commander.stop();
		}
		
	}
	
	
	
}
