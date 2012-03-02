package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public abstract class AbstractMode
{
	
	protected Commander commander;
	
	public AbstractMode(Commander commander)
	{
		this.commander = commander;
	}
	
	public double getViability(World world)
	{
		return -1;
	}
	
	public abstract boolean complete();
	public abstract void reset(World world);
	public abstract void update(World world);
	
}
