package strategy.mode;

import strategy.plan.*;
import strategy.world.*;
import comms.robot.*;

public class StaticShootMode extends AbstractMode
{
	
	private PlanFollowerMode follower = null;
	private boolean complete = false;
	private int interruptTouch = -1, interruptReverse = -1;
	
	public StaticShootMode(Commander commander)
	{
		super(commander);
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void reset(World world)
	{
		
		// plan to get to ball
		WorldState state = world.getWorldState();
		
		// create planner
		AbstractPlan planner = new StaticShootPlan(commander, world);
		
		// make plan
		AbstractMode[] plan = planner.plan();
		
		// make follower follow it
		follower = new PlanFollowerMode(commander, plan);
		follower.reset(world);
		
		// register bump interrupt
		interruptTouch = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_TOUCH,InterruptManager.MODE_EITHER);
		
	}
	
	private int speed = 0;
	
	public void update(World world)
	{
		
		if (complete || interruptReverse != -1)
		{
			return;
		}
		
		if (follower == null)
		{
			reset(world);
		}
		
		if (follower.complete())
		{
			
			if (speed < 90) speed += 10;
			
			WorldState state = world.getWorldState();
			double[] gCoord = world.getGoalCoords();
			
			double dist = AStarPlan.euclDistance(state.getRobotX(world.getColor()), state.getRobotY(world.getColor()), gCoord[0], gCoord[1]);
			
			double angle = angleDiff(world, gCoord[0], gCoord[1]);
			
			double speedModifier = (angle / 5.0);
			
			commander.setSpeed((int)Math.ceil(speed - speedModifier),(int)Math.ceil(speed + speedModifier));
			
			if (speed < 90 && dist > 40)
			{
				
				return;
				
			}
			
			commander.kick();
			commander.waitForQueueToEmpty();
			commander.stop();
			complete = true;
			return;
		}
		
		follower.update(world);
		
	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		if (interrupt == interruptTouch)
		{
			
			System.out.println(" << RECEIVED TOUCH INTERRUPT >> ");
			
			interruptReverse = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,-40);
			
			commander.setSpeed(-20,-20);
			commander.waitForQueueToEmpty();
			return;
			
		}
		else if (interrupt == interruptReverse)
		{
			
			interruptTouch = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_TOUCH,InterruptManager.MODE_EITHER);
			
			commander.stop();
			commander.waitForQueueToEmpty();
			interruptReverse = -1;
			return;
			
		}
		
		if (follower != null)
		{
			
			follower.handleInterrupt(world,interrupt);			
		}
		
	}
	
	private double angleToPoint(World world, double targetX, double targetY)
	{

		WorldState state = world.getWorldState();

		return Math.toDegrees(Math.atan2(state.getRobotX(world.getColor()) - targetX, state.getRobotY(world.getColor()) - targetY));

	}

	private double robotAngle(World world)
	{

		WorldState state = world.getWorldState();

		return Math.toDegrees(Math.atan2(state.getRobotDX(world.getColor()),state.getRobotDY(world.getColor())));

	}

	private double angleDiff(World world, double targetX, double targetY)
	{

		double diff = angleToPoint(world, targetX, targetY) - robotAngle(world);

		diff -= 180;

		if (diff < -180) diff += 360;
		if (diff > 180) diff -= 360;

		return diff;

	}
	
}
