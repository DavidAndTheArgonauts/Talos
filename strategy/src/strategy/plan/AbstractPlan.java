package strategy.plan;

import comms.robot.*;
import strategy.mode.*;
import strategy.world.*;

public abstract class AbstractPlan
{
	
	protected World world;
	protected Commander commander;
	
	public AbstractPlan(Commander commander, World world)
	{
		this.commander = commander;
		this.world = world;
	}
	
	public abstract AbstractMode[] plan();
	
}
