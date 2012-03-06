package comms.robot.interrupt;

import comms.robot.*;

public class TouchInterrupt extends AbstractInterrupt
{
	
	public TouchInterrupt(int id, double value)
	{
		super(id, value);
	}
	
	public boolean hasTriggered(Commander commander)
	{
		
		switch ((int)value)
		{
			case InterruptManager.MODE_LEFT:
				return commander.isLeftSensorPushed();
			case InterruptManager.MODE_RIGHT:
				return commander.isRightSensorPushed();
			case InterruptManager.MODE_BOTH:
				return commander.isLeftSensorPushed() && commander.isRightSensorPushed();
			case InterruptManager.MODE_EITHER:
				return commander.isLeftSensorPushed() || commander.isRightSensorPushed();
			default:
				return false;
		}
		
	}
	
}
