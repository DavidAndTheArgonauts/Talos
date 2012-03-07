package strategy.plan;

import java.awt.geom.Point2D;

import comms.robot.*;
import strategy.mode.*;
import strategy.world.*;

public class StaticShootPlan extends AbstractPlan
{
	
	public StaticShootPlan(Commander commander, World world)
	{
		super(commander,world);
	}
	
	public AbstractMode[] plan()
	{
		
		WorldState state = world.getWorldState();
		
		// calculate target point
		double[] gCoords = world.getGoalCoords();
		
		Point2D aStarEnd = calculateDestination(state.getBallX(),state.getBallY(),gCoords[0],gCoords[1],15);
		
		if (aStarEnd.getY() < 10)
		{
			aStarEnd.setLocation(aStarEnd.getX(),10);
		}
		else if (aStarEnd.getY() > 70)
		{
			aStarEnd.setLocation(aStarEnd.getX(),70);
		}
		
		AStarPlan planner = new AStarPlan(commander, world, aStarEnd.getX(), aStarEnd.getY(), false);
		AbstractMode[] plan = planner.plan();
		
		if (plan.length == 0)
		{
			System.out.println("Unable to find plan");
			System.exit(0);
		}
		
		// add way point very close to ball, then drive to goal
		WaypointMode wp = new WaypointMode(commander, state.getBallX(), state.getBallY());
		
		AbstractMode[] tempPlan = new AbstractMode[plan.length+1];
		for (int i = 0; i < plan.length; i++)
		{
			tempPlan[i] = plan[i];
		}
		tempPlan[tempPlan.length-1] = wp;
		
		return tempPlan;
		
	}
	
	public Point2D calculateDestination(double ballX, double ballY, double goalX, double goalY, double distance )
	{

		double destinationX;
		double destinationY;

		if(goalY == ballY)
		{
			
			destinationX = ballX + distance;
			destinationY = ballY;

		}
		else
		{
		
			//calculate the vector from goal to ball
			double vectorx = (ballX-goalX);
			double vectory = (ballY-goalY);

			//normalising to unit vector
			double i = Math.sqrt((vectorx*vectorx) + (vectory*vectory));

			double unitVectorx = vectorx/i;
			double unitVectory = vectory/i;

			//calculate destination by adding multiples of the uWnit vector
			// Handling out of bounds


			destinationX = (ballX + (distance*unitVectorx));
			destinationY = (ballY + (distance*unitVectory));

		}

		//System.out.println("X: " + destinationX + " Y: " + destinationY);
		Point2D destination = new Point2D.Double(destinationX, destinationY);
		return destination;

	}
	
}
