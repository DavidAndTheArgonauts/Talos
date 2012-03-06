package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class TurnMode extends AbstractMode
{
	
	public static final double TURN_TOLERENCE = 5;

	private double targetX, targetY;
	private boolean complete = false;
	
	public TurnMode(Commander commander, double targetX, double targetY)
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
		
		double diff = angleDiff(world);
		
		if (Math.abs(diff) < TURN_TOLERENCE)
		{
			commander.stop();
			complete = true;
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
