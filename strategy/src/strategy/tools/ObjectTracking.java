package strategy.tools;

	/*
	 * This file contains all the code for tracking objects
	 * over multiple frames, including prediction for 
	 * positions at given times
	*/

import strategy.world.*;

public class ObjectTracking
{
	
	private static final int[] TIMESTEPS = {
		100,
		200,
		300,
		500
	};
	
	/* class to store predicted positions */
	public class PredictedPosition
	{
		
		public double x, y, speed;
		
		public PredictedPosition(double x, double y, double speed)
		{
			this.x = x;
			this.y = y;
			this.speed = speed;
		}
		
	}
	
	private World world;
	
	public ObjectTracking(World world)
	{
		
		updateWorld(world);
		
	}
	
	public void updateWorld(World world)
	{
		this.world = world;
	}

	private PredictedPosition getRobotPosition()
	{
    		
    		WorldState lastState = world.getWorldState();
    		
		double counter = 0;
		double dx = 0, dy = 0;
		
		for ( int time : TIMESTEPS ) {
			
			WorldState ago = world.getPastByTime(time);

			if ( ago != null ) 
			{
				double pastRobotX = ago.getRobotX(world.getColor());
				double pastRobotY = ago.getRobotY(world.getColor());
				double timeAgo = (System.currentTimeMillis() - ago.getTime()) / 1000f;
				dx += (lastState.getRobotX(world.getColor()) - pastRobotX) / timeAgo;
				dy += (lastState.getRobotY(world.getColor()) - pastRobotY) / timeAgo;
				counter++;
            	}

		}

		if (counter == 0) return null;
		
		return new PredictedPosition(dx / counter, dy / counter, Vector.size(new Vector(dx / counter, dy / counter)));

	}
	
	private PredictedPosition getBallPosition()
	{
    		
    		WorldState lastState = world.getWorldState();
    		
		double counter = 0;
		double dx = 0, dy = 0;
		
		for ( int time : TIMESTEPS ) {
			
			WorldState ago = world.getPastByTime(time);

			if ( ago != null ) 
			{
				double pastBallX = ago.getBallX();
				double pastBallY = ago.getBallY();
				double timeAgo = (System.currentTimeMillis() - ago.getTime()) / 1000f;
				dx += (lastState.getBallX() - pastBallX) / timeAgo;
				dy += (lastState.getBallY() - pastBallY) / timeAgo;
				counter++;
            	}

		}

		if (counter == 0) return null;
		
		return new PredictedPosition(dx / counter, dy / counter,  Vector.size(new Vector(dx / counter, dy / counter)));

	}
	
	private PredictedPosition getEnemyPosition()
	{
    		
    		WorldState lastState = world.getWorldState();
    		
		double counter = 0;
		double dx = 0, dy = 0;
		
		for ( int time : TIMESTEPS ) {
			
			WorldState ago = world.getPastByTime(time);

			if ( ago != null ) 
			{
				double pastRobotX = ago.getEnemyX(world.getColor());
				double pastRobotY = ago.getEnemyY(world.getColor());
				double timeAgo = (System.currentTimeMillis() - ago.getTime()) / 1000f;
				dx += (lastState.getEnemyX(world.getColor()) - pastRobotX) / timeAgo;
				dy += (lastState.getEnemyY(world.getColor()) - pastRobotY) / timeAgo;
				counter++;
            	}

		}

		if (counter == 0) return null;
		
		return new PredictedPosition(dx / counter, dy / counter,  Vector.size(new Vector(dx / counter, dy / counter)));

	}
	
	
	public static double vecSize(double x, double y)
	{
		return Math.sqrt((x*x) + (y*y));
	}
	
}
