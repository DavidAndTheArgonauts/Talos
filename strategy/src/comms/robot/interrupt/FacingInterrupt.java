package comms.robot.interrupt;

import comms.robot.*;

public class FacingInterrupt extends AbstractInterrupt
{
	
	private long lWheelRev = -1, rWheelRev = -1;
	private boolean VERBOSE = false;
	
	public FacingInterrupt(int id, double value)
	{
		super(id, value);
	}
	
	public boolean hasTriggered(Commander commander)
	{
		// Initial setup
		if (lWheelRev == -1)
		{
			lWheelRev = commander.getLeftRevolution();
		}
		if (rWheelRev == -1)
		{
			rWheelRev = commander.getRightRevolution();
		}
		
		// Wheel Difference
		long lWheelDiff = commander.getLeftRevolution() - lWheelRev,
			rWheelDiff = commander.getRightRevolution() - rWheelRev;
		if (VERBOSE)
			System.out.println("lWheelDiff: " + lWheelDiff + ", rWheelDiff " + rWheelDiff);

		long wheelUpdateTime = commander.getWheelUpdateTime();
		
		if (wheelUpdateTime > 0)
		{
			int timeDiff = (int)(System.currentTimeMillis() - commander.getWheelUpdateTime());
			
			lWheelDiff += commander.getLeftSpeed() * timeDiff;
			rWheelDiff += commander.getRightSpeed() * timeDiff;
			if (VERBOSE)
			{
				System.out.println("POST timeDiff: lWheelDiff: " + lWheelDiff + ", rWheelDiff " + rWheelDiff + " timeDiff: " + timeDiff);
				System.out.println("commander.getLeftSpeed(): " + commander.getLeftSpeed() + " commander.getRightSpeed() " + commander.getRightSpeed());
			}
		}
		
		double turnDiff = ((double)rWheelDiff / 360.0) * Math.PI * Commander.WHEEL_DIAMETER - ((double)lWheelDiff / 360.0) * Math.PI * Commander.WHEEL_DIAMETER;
		
		if (VERBOSE)
			System.out.println("turnDiff: " + turnDiff);

		double angleTurned = Math.toDegrees(turnDiff / Commander.WHEEL_SEPARATION);
		
		if (VERBOSE1)
			System.out.println("angle turned = " + angleTurned + " [value = " + value + "] (lWheelDiff = " + lWheelDiff + ",rWheelDiff = " + rWheelDiff + ")");
		
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
