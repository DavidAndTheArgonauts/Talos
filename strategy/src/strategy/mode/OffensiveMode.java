package strategy.mode;

import strategy.world.*;
import strategy.plan.*;

import comms.robot.*;

public class OffensiveMode extends AbstractMode
{
	
	private static final double FINAL_DISTANCE = 30;
	
	private AbstractMode[] plan = new AbstractMode[0];
	private int idx = 0;
	
	private double[] ballPos = new double[0];
	
	private boolean complete = false;
	
	private int wheelSpeed = 0;

	private DriveMode drivemode = null;
	
	private int ballInterrupt = -1, bumpInterrupt = -1, stopInterrupt = -1;
	private boolean reversing = false;
	
	public OffensiveMode(Commander commander)
	{
		super(commander);
	}
	
	public double getViability(World world)
	{
		
		return 0;
		
	}
	
	public void reset(World world)
	{
		
		WorldState state = world.getWorldState();
		
		idx = 0;
		
		AbstractPlan p = new ShootPlan(commander,world);
		plan = p.plan();
		
		// set interrupt for touch sensor
		//commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_TOUCH,InterruptManager.MODE_EITHER);
		
		bumpInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_TOUCH,InterruptManager.MODE_EITHER);
		
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void update(World world)
	{
		
		if (complete() || reversing)
			return;
		
		if (plan.length == 0)
		{
			reset(world);
		}
		
		// if we're complete
		if (idx >= plan.length)
		{
			
			/*
			if (ballInterrupt == -1)
			{
				ballInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,25);
				commander.setSpeed(90,90);
			}
			return;
			*/
			
			/*if (wheelSpeed < 40)
			{
				wheelSpeed += 3;
				commander.setSpeed(wheelSpeed,wheelSpeed);
			}*/
			
			
			WorldState state = world.getWorldState();
			
			double[] goalCoords = world.getGoalCoords();
			
			if (ballPos.length == 0)
			{
				ballPos = new double[2];
				ballPos[0] = world.getWorldState().getBallX();
				ballPos[1] = world.getWorldState().getBallY();

				drivemode = new DriveMode(commander, goalCoords[0], goalCoords[1], 80);
			}
			
			
			
			double distPastBall = ShootPlan.euclDistance(state.getRobotX(world.getColor()),state.getRobotY(world.getColor()), ballPos[0], ballPos[1]);
			double distToGoal = ShootPlan.euclDistance(state.getRobotX(world.getColor()),state.getRobotY(world.getColor()), goalCoords[0], goalCoords[1]);
			
			if (distPastBall > FINAL_DISTANCE || distToGoal < 30)
			{
				
				commander.kick();
				commander.waitForQueueToEmpty();
				commander.stop();
				complete = true;
				return;

			}
			
			drivemode.update(world);
			return;
			
			
			
		}
		
		if (plan[idx].complete())
		{
			idx++;
			update(world);
			return;
		}
		
		plan[idx].update(world);
		
	}
	
	public void handleInterrupt(World world,int interrupt)
	{
		
		System.out.println(" << INTERRUPT RECEIVED - ID = " + interrupt + " >> ");
		
		if (interrupt == ballInterrupt)
		{
			
			commander.kick();
			commander.waitForQueueToEmpty();
			commander.stop();
			
		}
		else if (interrupt == bumpInterrupt)
		{
			
			reversing = true;
			commander.setSpeed(-40,-40);
			stopInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,-40);
			
		}
		else if (interrupt == stopInterrupt)
		{
			
			commander.stop();
			commander.waitForQueueToEmpty();
			bumpInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_TOUCH,InterruptManager.MODE_EITHER);
			reversing = false;
			
		}
		else
		{
			
			// pass interrupt to next level
			if (idx < plan.length)
			{
				plan[idx].handleInterrupt(world,interrupt);
			}
			
		}
		
		//System.out.println(" << INTERRUPT TRIGGERED >> ");
		//commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_TOUCH,InterruptManager.MODE_EITHER);
		
	}
	
	
}
