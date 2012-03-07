package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class DefensiveMode extends AbstractMode
{
	
	ArcMode mode = null;
	
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
		
		mode = new ArcMode(commander, 35, 90, 80);
		mode.reset(world);
		
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void update(World world)
	{
		
		if (mode == null)
		{
			reset(world);
		}
		
		if (!mode.complete())
			mode.update(world);
		else
			commander.stop();
	}
	
	public void handleInterrupt(World world,int interrupt)
	{
		
		if (mode != null)
		{
			mode.handleInterrupt(world, interrupt);
		}
		
	}
	
}
