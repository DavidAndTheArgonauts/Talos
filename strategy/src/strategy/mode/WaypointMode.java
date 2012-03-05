package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class WaypointMode extends AbstractMode
{
	
	private static final int MODE_TURN = 0;
	private static final int MODE_DRIVE = 1;
	private static final int MODE_COMPLETE = 2;
	
	
	private double targetX, targetY;
	private long lastUpdate = -1;
	private int mode = 0;
	private double lastDirX = 0, lastDirY = 0;

	private TurnMode turner = null;
	private DriveMode driver = null;
	
	public WaypointMode(Commander commander, double targetX, double targetY)
	{
		super(commander);
		
		System.out.println("Target coord: (" + targetX + "," + targetY + ")");
		
		this.targetX = targetX;
		this.targetY = targetY;
	}
	
	public void reset(World world)
	{
			
		turner = null;
		driver = null;
		mode = MODE_TURN;
		
	}
	
	public void update(World world)
	{
		
		if (mode == MODE_COMPLETE)
		{
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
		if (Math.abs(x - targetX) < DriveMode.DESTINATION_TOLERENCE && Math.abs(y - targetY) < DriveMode.DESTINATION_TOLERENCE)
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
		
		if (turner == null)
		{
			turner = new TurnMode(commander,targetX,targetY);
		}
		
		if (turner.complete())
		{
			mode = MODE_DRIVE;
			modeDrive(world);
			return;
		}

		turner.update(world);
		
	}
	
	private void modeDrive(World world)
	{
		
		if (driver == null)
		{
			driver = new DriveMode(commander,targetX,targetY);
		}
	
		if (driver.complete())
		{
			commander.stop();
			mode = MODE_COMPLETE;
			return;
		}

		driver.update(world);

	}
	
}
