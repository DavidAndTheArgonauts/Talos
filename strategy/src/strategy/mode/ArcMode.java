package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class ArcMode extends AbstractMode
{
	
	private static final int MODE_DRIVE = 0;
	private static final int MODE_ARC = 1;
	private static final int MODE_COMPLETE = 2;
	
	private double forwardDistance, arcAngle, speed;
	private int interruptDrive = -1, interruptArc = -1;
	
	private int wheelSpeed = 0;
	private int mode = 0;
	
	public ArcMode(Commander commander, double forwardDistance, double arcAngle, int speed)
	{
		super(commander);
		
		this.forwardDistance = forwardDistance;
		this.arcAngle = arcAngle;
		this.speed = speed;
	}
	
	public double getViability(World world)
	{
		return -1;
	}
	
	public boolean complete()
	{
		return mode == MODE_COMPLETE;
	}
	
	public void reset(World world)
	{
		
		if (!commander.interruptManagerAvailable())
		{
			System.out.println("Interrupt manager unavailable [reset]");
			return;
		}
		
		// reset all interrupts
		interruptDrive = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,forwardDistance);
		mode = MODE_DRIVE;
		
	}
	
	public void update(World world)
	{
		
		if (!commander.interruptManagerAvailable())
		{
			System.out.println("Interrupt manager unavailable [update]");
			return;
		}
		
		if (interruptDrive == -1)
		{
			reset(world);
		}
		
		// check mode, if we're arcing then check acceleration
		if (wheelSpeed < speed)
			wheelSpeed += 1;
		
		switch (mode)
		{
			case MODE_COMPLETE:
			case MODE_DRIVE:
				commander.setSpeed(wheelSpeed,wheelSpeed);
				break;
			case MODE_ARC:
				if (arcAngle < 0)
				{
					commander.setSpeed((int)Math.round(wheelSpeed * 1.25), (int)Math.round(wheelSpeed * 0.75));
				}
				else
				{
					commander.setSpeed((int)Math.round(wheelSpeed * 0.75), (int)Math.round(wheelSpeed * 1.25));
				}
				break;
		}
		
	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		if (interrupt == interruptDrive)
		{
			System.out.println("Switching to arc mode");
			mode = MODE_ARC;
			interruptArc = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,arcAngle);
		}
		else if (interrupt == interruptArc)
		{
			System.out.println("Switching to complete");
			mode = MODE_COMPLETE;
		}
		
	}
	
}
