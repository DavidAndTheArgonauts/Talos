package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class OffensiveMode extends AbstractMode
{
	
	private WaypointMode center;
	
	public OffensiveMode(Commander commander)
	{
		super(commander);
		
		center = new WaypointMode(commander,65d,40d);
	}
	
	public double getViability(World world)
	{
		
		return 1;
		
	}
	
	public void update(World world)
	{
		
		if (!center.complete())
			center.update(world);
		
	}
	
}
