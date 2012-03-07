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
	
}
