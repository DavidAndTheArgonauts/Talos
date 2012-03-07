package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class PlanFollowerMode extends AbstractMode
{
	
	private int idx = 0;
	private AbstractMode[] plan;
	
	public PlanFollowerMode(Commander commander, AbstractMode[] plan)
	{
		super(commander);
		
		this.plan = plan;
	}
	
	private boolean complete = false;
	
	public boolean complete()
	{
		return complete;
	}
	
	public void reset(World world)
	{
		
		idx = 0;
		
		// reset each item in plan
		for (int i = 0; i < plan.length; i++)
		{
			plan[i].reset(world);
		}
		
	}
	
	public void update(World world)
	{
		
		if (complete)
		{
			commander.stop();
			commander.waitForQueueToEmpty();
			return;
		}
		
		if (idx >= plan.length)
		{
			complete = true;
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
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		if (!complete())
		{
			
			plan[idx].handleInterrupt(world,interrupt);
			
		}
		
	}
	
}
