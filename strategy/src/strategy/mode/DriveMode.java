package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class DriveMode extends AbstractMode
{
	
	public static final double DESTINATION_TOLERENCE = 2;

	private double targetX, targetY;
	private boolean complete = false;
	
	public DriveMode(Commander commander, double targetX, double targetY)
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
	
	}
	
	public void update(World world)
	{
	
		WorldState state = world.getWorldState();
		
		double dx = state.getRobotX(world.getColor()) - targetX;
		double dy = state.getRobotY(world.getColor()) - targetY;
		
		double dist = Math.sqrt((dx * dx) + (dy * dy));
		
		/* set speed modifier for minor corrections */
		double diff = angleDiff(world);
		
		int n = (int)Math.round(diff / 30.0);
		
		double speedDiff = 0;
		if (Math.abs(diff) > TurnMode.TURN_TOLERENCE)
		{
			
			if (diff < 0)
			{
				speedDiff = -(n/10.0);
			}
			else
			{
				speedDiff = n/10.0;
			}
			
		}

		System.out.println("Speeddiff = " + speedDiff);
		
		//System.out.println("Dist = " + dist);
		
		if (dist < DESTINATION_TOLERENCE)
		{
			commander.stop();
			complete = true;
			return;
		}
		
		int speed = (int)dist;
		if (speed > 20) speed = 20;
		if (speed < 10) speed = 1;
		
		int lSpeed = (int)Math.ceil((-speedDiff) * speed) + speed;
		int rSpeed = (int)Math.ceil(speedDiff * speed) + speed;
		
		System.out.println("(" + state.getRobotX(world.getColor()) + "," + state.getRobotY(world.getColor()) +") -> (" + targetX + "," + targetY +") at (" + lSpeed + "," + rSpeed + ")");
		
		commander.setSpeed(lSpeed, rSpeed);
			

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

}
