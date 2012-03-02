package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class WaypointMode extends AbstractMode
{
	
	private static final int MODE_TURN = 0;
	private static final int MODE_DRIVE = 1;
	private static final int MODE_COMPLETE = 2;
	
	private static final double DESTINATION_TOLERENCE = 30;
	private static final double TURN_TOLERENCE = 5;
	
	private double targetX, targetY;
	private long lastUpdate = -1;
	private int mode = 0;
	private double lastDirX = 0, lastDirY = 0;
	
	public WaypointMode(Commander commander, double targetX, double targetY)
	{
		super(commander);
		
		this.targetX = targetX;
		this.targetY = targetY;
	}
	
	public double getViability(World world)
	{
		return -1;
	}
	
	public void update(World world)
	{
		
		WorldState prev = world.getPastState(1);
		// if we haven't been called continuously
		// reset our mode to turning
		if (prev.getCreatedMillis() != lastUpdate)
		{
			mode = MODE_TURN;
		}
		
		if (mode == MODE_COMPLETE)
		{
			commander.stop();
			return;
		}
		
		// calculate angle between robot and point
		WorldState state = world.getWorldState();
		
		lastUpdate = state.getCreatedMillis();
		
		double x = state.getRobotX(world.getColor());
		double y = state.getRobotY(world.getColor());
		
		// catch the case that we have reached our destination (since angle isn't important)
		if (Math.abs(x - targetX) < DESTINATION_TOLERENCE && Math.abs(y - targetY) < DESTINATION_TOLERENCE)
		{
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
		if (mode == MODE_COMPLETE)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private void modeTurn(World world)
	{
		
		
		WorldState state = world.getWorldState();
		
		double angleToPoint = Math.toDegrees(Math.atan2(state.getRobotX(world.getColor()) - state.getBallX(), state.getRobotY(world.getColor()) - state.getBallY()));
		double robotAngle = Math.toDegrees(Math.atan2(state.getRobotDX(world.getColor()),state.getRobotDY(world.getColor())));
		
		System.out.println("angleToPoint = " + angleToPoint + "; robotAngle = " + robotAngle + " (" + state.getRobotDX(world.getColor()) + "," + state.getRobotDY(world.getColor()) + ");");
		
		double diff = angleToPoint - robotAngle;
		
		diff -= 180;
		
		if (diff < -180) diff += 360;
		if (diff > 180) diff -= 360;
		
		System.out.println("Diff = " + diff);
		
		if (Math.abs(diff) < TURN_TOLERENCE)
		{
			commander.stop();
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
		
		double dx = state.getRobotX(world.getColor()) - state.getBallY();
		double dy = state.getRobotY(world.getColor()) - state.getBallY();
		
		double dist = Math.sqrt((dx * dx) + (dy * dy));
		
		System.out.println("Dist = " + dist);
		
		if (dist < DESTINATION_TOLERENCE)
		{
			System.out.println("Complete");
			mode = MODE_COMPLETE;
			return;
		}
		
		int speed = (int)dist;
		if (speed > 20) speed = 20;
		if (speed == 10) speed = 1;
		
		commander.setSpeed(speed,speed);
		
	}
	
}
