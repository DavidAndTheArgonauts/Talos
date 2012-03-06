package comms.robot.interrupt;

import comms.robot.*;

public abstract class AbstractInterrupt
{
	
	protected double value;
	private int id;
	
	public AbstractInterrupt(int id, double value)
	{
		
		this.id = id;
		this.value = value;
		
	}
	
	public int getId()
	{
		return id;
	}
	
	public abstract boolean hasTriggered(Commander commander);
	
}
