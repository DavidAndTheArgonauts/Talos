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
		
		private double speed;
		private Vector position;
		private Vector velocity;
		
		public PredictedPosition(Vector positon, Vector velocity, double speed)
		{
			this.position = position;
			this.velocity = velocity;
			this.speed = speed;
		}
		
		public Vector getPosition()
		{
			return position;
		}
		
		public Vector getPosition(double timestep)
		{
			return new Vector(position.getX() + velocity.getX() * timestep, position.getY() + velocity.getY() * timestep);	
		}
		
		public Vector getVelocity()
		{
			return velocity;
		}
		
		public double getSpeed()
		{
			return speed;
		}
		
	}
	
	private World world = null;
	
	public void updateWorld(World world)
	{
		this.world = world;
	}

	private PredictedPosition getRobotPosition()
	{
    		
    		if (world == null) return null;
    		
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
		
		// current positions
		double robotX = lastState.getRobotX(world.getColor());
		double robotY = lastState.getRobotY(world.getColor());
		
		// average speeds
		dx /= counter;
		dy /= counter;
		
		Vector velocity = new Vector(dx, dy);
		
		Vector position = new Vector(robotX, robotY);
		
		return new PredictedPosition(position, velocity, velocity.size());

	}
	
	private PredictedPosition getBallPosition()
	{
    		
    		if (world == null) return null;
    		
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
		
		// current positions
		double ballX = lastState.getBallX();
		double ballY = lastState.getBallY();
		
		// average speeds
		dx /= counter;
		dy /= counter;
		
		Vector velocity = new Vector(dx, dy);
		
		Vector position = new Vector(ballX, ballY);
		
		return new PredictedPosition(position, velocity, velocity.size());
		
	}
	
	private PredictedPosition getEnemyPosition()
	{
    		
    		if (world == null) return null;
    		
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
		
		// current positions
		double robotX = lastState.getEnemyX(world.getColor());
		double robotY = lastState.getEnemyY(world.getColor());
		
		// average speeds
		dx /= counter;
		dy /= counter;
		
		Vector velocity = new Vector(dx, dy);
		
		Vector position = new Vector(robotX, robotY);
		
		return new PredictedPosition(position, velocity, velocity.size());

	}
	
}
