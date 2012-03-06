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
	
	public OffensiveMode(Commander commander)
	{
		super(commander);
	}
	
	public double getViability(World world)
	{
		
		return 1;
		
	}
	
	public void reset(World world)
	{
		
		WorldState state = world.getWorldState();
		
		idx = 0;
		
		AbstractPlan p = new ShootPlan(commander,world);
		plan = p.plan();
		
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void update(World world)
	{
		
		if (complete())
			return;
		
		if (plan.length == 0)
		{
			reset(world);
		}
		
		// if we're complete
		if (idx >= plan.length)
		{
			
			/*if (wheelSpeed < 40)
			{
				wheelSpeed += 3;
				commander.setSpeed(wheelSpeed,wheelSpeed);
			}*/
			
			WorldState state = world.getWorldState();
			
			if (ballPos.length == 0)
			{
				ballPos = new double[2];
				ballPos[0] = world.getWorldState().getBallX();
				ballPos[1] = world.getWorldState().getBallY();

				drivemode = new DriveMode(commander, 0, 40, 40);
			}
			
			double distPastBall = ShootPlan.euclDistance(state.getRobotX(world.getColor()),state.getRobotY(world.getColor()), ballPos[0], ballPos[1]);
			double distToGoal = ShootPlan.euclDistance(state.getRobotX(world.getColor()),state.getRobotY(world.getColor()), World.LEFT_GOAL_CENTER[0], World.LEFT_GOAL_CENTER[1]);
			
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
	
}
