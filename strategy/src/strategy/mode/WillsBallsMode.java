package strategy.mode;

import comms.robot.*;

import strategy.world.*;
import strategy.tools.*;

public class WillsBallsMode extends AbstractMode
{
	
	private static final int SPIN_SPEED = 15;
	private static final int DRIVE_SPEED = 40;
	
	private static final int MODE_SPIN = 0;
	private static final int MODE_DRIVE = 1;
	private static final int MODE_COMPLETE = 2;
	
	private int mode = MODE_SPIN;
	
	private int interruptSpin = -1, interruptDrive = -1;
	
	private double targetX, targetY;
	
	public WillsBallsMode(Commander commander, double targetX, double targetY)
	{
		super(commander);
		this.targetX = targetX;
		this.targetY = targetY;
	}
	
	public boolean complete()
	{
		return mode == MODE_COMPLETE;
	}
	
	public void reset(World world)
	{
		
		interruptSpin = -1;
		interruptDrive = -1;
		mode = MODE_SPIN;
		
	}
	
	public void updateTarget(double targetX, double targetY)
	{
		
		System.out.println("Updating target = (" + targetX + "," + targetY + ")");
		
		this.targetX = targetX;
		this.targetY = targetY;
		
		commander.stop();
		
		interruptSpin = -1;
		interruptDrive = -1;
		mode = MODE_SPIN;
		
	}
	
	public double getTargetX()
	{
		return targetX;
	}
	
	public double getTargetY()
	{
		return targetY;
	}
	
	public void update(World world)
	{
		
		WorldState state = world.getWorldState();
		
		double robotX = state.getRobotX(world.getColor()),
			robotY = state.getRobotY(world.getColor());
		
		switch (mode)
		{
			
			case MODE_SPIN:
				
				
				if (interruptSpin != -1)
				{
					return;
				}
				
				
				// calculate angle to turn
				double robotDX = state.getRobotDX(world.getColor()),
					robotDY = state.getRobotDY(world.getColor());
				
				double angleToTurn = Math.toDegrees(Vector.angleVectors( new Vector(robotDX, robotDY), new Vector(robotX, robotY, targetX, targetY) ));
				
				interruptSpin = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,angleToTurn);
				
				if (angleToTurn > 0)
				{
					commander.setSpeed(-SPIN_SPEED,SPIN_SPEED);
				}
				else
				{
					commander.setSpeed(SPIN_SPEED,-SPIN_SPEED);
				}

				System.out.println("Entering mode spin (angle = " + angleToTurn + ")");
				
				break;
			case MODE_DRIVE:
				
				if (interruptDrive != -1)
				{
					return;
				}
				
				// distance to drive
				
				double distanceToDrive = new Vector(robotX, robotY, targetX, targetY).size();
				
				interruptDrive = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE, distanceToDrive);
				
				commander.setSpeed(DRIVE_SPEED,DRIVE_SPEED);
				
				break;
			case MODE_COMPLETE:
				commander.stop();
				break;
			
		}
		
	}
	
	
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		if (interrupt == interruptSpin)
		{
			commander.stop();
			mode = MODE_DRIVE;
		}
		else if (interrupt == interruptDrive)
		{
			mode = MODE_COMPLETE;
		}
		
	}
	
	
}
