package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class GoalieMode extends AbstractMode
{
	
	public GoalieMode(Commander commander)
	{
		super(commander);
		
		commander.enableUltrasonic();
	}
	
	public boolean complete()
	{
		return false;
	}
	
	public void reset(World world)
	{
		
	}
	
	public void update(World world)
	{
		
		int lUS = commander.getLeftUltrasonic(),
			rUS = commander.getRightUltrasonic();
			
		System.out.println("Ultrasonic values: [" + lUS + "," + rUS + "]");
		
	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}
	
}
