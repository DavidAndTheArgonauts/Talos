package comms.robot.interrupt;

import comms.robot.*;

public class FacingInterrupt extends AbstractInterrupt
{
	
	private long lWheelRev = -1, rWheelRev = -1;
	
	public FacingInterrupt(int id, double value)
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
		
		// wheel diff
		long lWheelDiff = commander.getLeftRevolution() - lWheelRev,
			rWheelDiff = commander.getRightRevolution() - rWheelRev;
		
		long wheelUpdateTime = commander.getWheelUpdateTime();
		
		if (wheelUpdateTime > 0)
		{
			int timeDiff = (int)(System.currentTimeMillis() - commander.getWheelUpdateTime());
			
			lWheelDiff += commander.getLeftSpeed() * timeDiff;
			rWheelDiff += commander.getRightSpeed() * timeDiff;
		}
		
		double turnDiff = ((double)rWheelDiff / 360.0) * Math.PI * Commander.WHEEL_DIAMETER - ((double)lWheelDiff / 360.0) * Math.PI * Commander.WHEEL_DIAMETER;
		
		double angleTurned = Math.toDegrees(turnDiff / Commander.WHEEL_SEPARATION)/2.0;
		
		System.out.println("angle turned = " + angleTurned + "(lWheelDiff = " + lWheelDiff + ",rWheelDiff = " + rWheelDiff + ")");
		
		if (value > 0 && angleTurned > value)
		{
			return true;
		}
		else if (value < 0 && angleTurned < value)
		{
			return true;
		}
		
		return false;
		
	}
	
}
