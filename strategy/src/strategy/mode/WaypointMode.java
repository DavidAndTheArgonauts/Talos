package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class WaypointMode extends AbstractMode
{
	
	private static final int MODE_TURN = 0;
	private static final int MODE_DRIVE = 1;
	private static final int MODE_COMPLETE = 2;
	
	private static final double DESTINATION_TOLERENCE = 2;
	private static final double TURN_TOLERENCE = 10;
	
	private double targetX, targetY;
	private long lastUpdate = -1;
	private int mode = 0;
	private double lastDirX = 0, lastDirY = 0;
	
	public WaypointMode(Commander commander, double targetX, double targetY)
	{
		super(commander);
		
		System.out.println("Target coord: (" + targetX + "," + targetY + ")");
		
		this.targetX = targetX;
		this.targetY = targetY;
	}
	
	public void reset(World world)
	{
		
		mode = MODE_TURN;
		
	}
	
	public void update(World world)
	{
		
		if (mode == MODE_COMPLETE)
		{
			System.out.println("Completed...");
			commander.stop();
			return;
		}
		
		// calculate angle between robot and point
		WorldState state = world.getWorldState();
		
		//targetX = state.getBallX();
		//targetY = state.getBallY();
		
		lastUpdate = state.getCreatedMillis();
		
		double x = state.getRobotX(world.getColor());
		double y = state.getRobotY(world.getColor());
		
		// catch the case that we have reached our destination (since angle isn't important)
		if (Math.abs(x - targetX) < DESTINATION_TOLERENCE && Math.abs(y - targetY) < DESTINATION_TOLERENCE)
		{
			System.out.println("Complete");
			mode = MODE_COMPLETE;
			commander.stop();
			return;
		}
		
		// switch mode
		switch (mode)
		{
			case MODE_TURN:
				modeTurn(world);
				break;
			case MODE_DRIVE:
				modeDrive(world);
				break;
		}
		
	}
	
	public boolean complete()
	{
		return mode == MODE_COMPLETE;
	}
	
	private void modeTurn(World world)
	{
		
		
		WorldState state = world.getWorldState();
		
		double diff = angleDiff(world);
		
		if (Math.abs(diff) < TURN_TOLERENCE)
		{
			System.out.println("Switching to drive");
			//commander.stop();
			mode = MODE_DRIVE;
			modeDrive(world);
			return;
		}
		
		//System.out.println("RobotAngle = " + robotAngle + " Robot Direction = (" + state.getRobotDX(world.getColor()) + "," + state.getRobotDY(world.getColor())+ ")");
		
		int directionModifier = 1;
		if (diff < 0)
		{
			directionModifier = -1;
		}
		
		int speed = (int)(((Math.abs(diff) / 180.0) * 15.0) + 1);
		System.out.println("Speed = " + speed);
		
		commander.setSpeed(directionModifier * -1 * speed, directionModifier * speed);
		
	}
	
	private void modeDrive(World world)
	{
		
		WorldState state = world.getWorldState();
		
		double dx = state.getRobotX(world.getColor()) - targetX;
		double dy = state.getRobotY(world.getColor()) - targetY;
		
		double dist = Math.sqrt((dx * dx) + (dy * dy));
		
		/* set speed modifier for minor corrections */
		double diff = angleDiff(world);
		
		if (diff > 60)
		{
			mode = MODE_TURN;
			modeTurn(world);
			return;
		}
		
		double speedDiff = 0;
		if (Math.abs(diff) > TURN_TOLERENCE)
		{
			
			if (diff < 0)
			{
				speedDiff = -0.1;
			}
			else
			{
				speedDiff = 0.1;
			}
			
		}
				
		//System.out.println("Dist = " + dist);
		
		if (dist < DESTINATION_TOLERENCE)
		{
			System.out.println("Complete");
			mode = MODE_COMPLETE;
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
