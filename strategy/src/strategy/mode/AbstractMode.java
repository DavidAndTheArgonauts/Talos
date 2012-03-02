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
	
	public abstract double getViability(World world);
	
	public abstract void update(World world);
	
}
