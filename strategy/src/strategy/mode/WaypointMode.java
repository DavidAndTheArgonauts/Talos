package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import strategy.plan.*;

public class WaypointMode extends AbstractMode
{
	
	private static final double TURN_TOLERENCE = 5;
	private static final double DRIVE_TOLERENCE = 7;
	
	private static final int MODE_TURN = 0;
	private static final int MODE_DRIVE = 1;
	private static final int MODE_COMPLETE = 2;
	
	private double targetX, targetY;
	
	private int mode = MODE_TURN;
	private boolean complete = false;
	private int speed = 0, maxSpeed = 20;
	
	public WaypointMode(Commander commander, double targetX, double targetY)
	{
		super(commander);
		
		this.targetX = targetX;
		this.targetY = targetY;
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void reset(World world)
	{
		
		mode = MODE_TURN;
			
	}
	
	public void update(World world)
	{
		
		if (complete)
		{
			return;
		}
		
		WorldState state = world.getWorldState();
		
		double dist = AStarPlan.euclDistance(state.getRobotX(world.getColor()), state.getRobotY(world.getColor()), targetX, targetY);
		
		if (dist < DRIVE_TOLERENCE)
		{
			commander.stop();
			commander.waitForQueueToEmpty();
			
			complete = true;
			mode = MODE_COMPLETE;
			
			return;
		}
		
		double angle = angleDiff(world);
		
		switch (mode)
		{
			case MODE_TURN:
				
				
				if (Math.abs(angle) < TURN_TOLERENCE)
				{
					commander.stop();
					commander.waitForQueueToEmpty();
					mode = MODE_DRIVE;
					
					System.out.println("Switching to drive mode");
					
					update(world);
					
					return;
				}
				
				int turnSpeed = 15;
				
				if (Math.abs(angle) < TURN_TOLERENCE * 3)
				{
					turnSpeed = 5;
				}
				//int turnSpeed = (int)Math.round(angle / maxSpeed) * 10 + 5;
				
				
				if (angle > 0)
				{
					turnSpeed *= -1;
				}
				
				System.out.println("Angle = " + angle + ", turn speed = " + turnSpeed);
				
				commander.setSpeed(turnSpeed,-turnSpeed);
				
				break;
			case MODE_DRIVE:
				
				double speedMod = 0;
				
				if (Math.abs(angle) > TURN_TOLERENCE)
				{
					
					if (Math.abs(angle) > TURN_TOLERENCE * 10)
					{
						
						commander.stop();
						commander.waitForQueueToEmpty();
						mode = MODE_TURN;
						
						System.out.println("Switching back to turn mode");
						
						update(world);
						
						return;
						
					}
					
					if (angle < 0)
					{
						speedMod = 0.1;
					}
					else
					{
						speedMod = -0.1;
					}
					
				}
				
				//double driveSpeed = (int)Math.round(dist / maxSpeed) * 10 + 5;
				double driveSpeed = 20;
				
				int lDriveSpeed = (int)Math.round(driveSpeed + speedMod * driveSpeed);
				int rDriveSpeed = (int)Math.round(driveSpeed - speedMod * driveSpeed);
				
				System.out.println("Dist to go = " + dist + " at speed = (" + lDriveSpeed + "," + rDriveSpeed + ")");
				
				commander.setSpeed(lDriveSpeed,rDriveSpeed);
				
				break;
			case MODE_COMPLETE:
				break;
		}
		
	}
	
	private double angleToPoint(World world)
	{

		WorldState state = world.getWorldState();

		return Math.toDegrees(Math.atan2(state.getRobotX(world.getColor()) - targetX, state.getRobotY(world.getColor()) - targetY));

	}

	private double robotAngle(World world)
	{

		WorldState state = world.getWorldState();

		return Math.toDegrees(Math.atan2(state.getRobotDX(world.getColor()),state.getRobotDY(world.getColor())));

	}

	private double angleDiff(World world)
	{

		double diff = angleToPoint(world) - robotAngle(world);

		diff -= 180;

		if (diff < -180) diff += 360;
		if (diff > 180) diff -= 360;

		return diff;

	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}
	
}
