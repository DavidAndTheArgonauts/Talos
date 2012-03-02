package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class DefensiveMode extends AbstractMode
{
	
	public DefensiveMode(Commander commander)
	{
		super(commander);
	}
	
	public double getViability(World world)
	{
		
		return 0;
		
	}
	
	public void reset(World world)
	{
		
		
		
	}
	
	public boolean complete()
	{
		return false;
	}
	
	public void update(World world)
	{
		
		commander.setSpeed(20,20);
		
	}
	
}
