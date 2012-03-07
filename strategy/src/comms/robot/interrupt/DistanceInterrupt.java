package comms.robot.interrupt;

import comms.robot.*;

public class DistanceInterrupt extends AbstractInterrupt
{
	
	private long lWheelRev = -1, rWheelRev = -1;
	
	public DistanceInterrupt(int id, double value)
	{
		super(id, value);
	}
	
	public boolean hasTriggered(Commander commander)
	{
		
		if (lWheelRev == -1)
		{
			lWheelRev = commander.getLeftRevolution();
		}
		if (rWheelRev == -1)
		{
			rWheelRev = commander.getRightRevolution();
		}
		
		long lWheelDiff = commander.getLeftRevolution() - lWheelRev,
			rWheelDiff = commander.getRightRevolution() - rWheelRev;
		
		long wheelUpdateTime = commander.getWheelUpdateTime();
		
		if (wheelUpdateTime > 0)
		{
			int timeDiff = (int)(System.currentTimeMillis() - commander.getWheelUpdateTime());
			
			lWheelDiff += commander.getLeftSpeed() * timeDiff;
			rWheelDiff += commander.getRightSpeed() * timeDiff;
		}
		
		double maxDistCovered = 0;
		if (value > 0)
		{
			maxDistCovered = Math.max(((double)lWheelDiff / 360.0) * Math.PI * Commander.WHEEL_DIAMETER, ((double)rWheelDiff / 360.0) * Math.PI * Commander.WHEEL_DIAMETER);
		}
		else
		{
			maxDistCovered = Math.min(((double)lWheelDiff / 360.0) * Math.PI * Commander.WHEEL_DIAMETER, ((double)rWheelDiff / 360.0) * Math.PI * Commander.WHEEL_DIAMETER);
		}
		
		if ((value > 0 && maxDistCovered >= value) || (value < 0 && maxDistCovered <= value))
		{
			System.out.println("maxDistCovered = " + maxDistCovered + " (l = " + lWheelDiff + ", r  = " + rWheelDiff + ")");
			return true;
		}
		
		return false;
	}
	
}
