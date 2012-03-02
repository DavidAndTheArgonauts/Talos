package strategy.mode;

import strategy.world.*;
import strategy.plan.*;

import comms.robot.*;

public class OffensiveMode extends AbstractMode
{
	
	private AbstractMode[] plan = new AbstractMode[0];
	private int idx = 0;
	
	public OffensiveMode(Commander commander)
	{
		super(commander);
	}
	
	public double getViability(World world)
	{
		
		return 1;
		
	}
	
	public void reset(World world)
	{
		
		idx = 0;
		ShootPlan sp = new ShootPlan(world,commander);
		plan = sp.plan();
		
	}
	
	public boolean complete()
	{
		return false;
	}
	
	public void update(World world)
	{
		
		if (plan.length == 0)
		{
			reset(world);
		}
		
		// if we're complete
		if (idx >= plan.length)
		{
			commander.stop();
			commander.kick();
			return;
		}
		
		if (plan[idx].complete())
		{
			idx++;
			update(world);
			return;
		}
		
		plan[idx].update(world);
		
	}
	
}
