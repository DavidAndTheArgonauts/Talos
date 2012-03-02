package strategy.plan;

import strategy.mode.*;
import strategy.world.*;

import comms.robot.*;

import java.awt.*;

public class ShootPlan extends AbstractPlan
{
	
	private World world;
	private Commander commander;
	
	public ShootPlan(World world, Commander commander)
	{
		this.world = world;
		this.commander = commander;
	}
	
	public AbstractMode[] plan()
	{
		
		WorldState state = world.getWorldState();
		
		System.out.println("Enemy coord: (" + state.getEnemyX(world.getColor()) + "," + state.getEnemyY(world.getColor()) + ")");
		
		double[] output = calculateWaypoint(state.getRobotX(world.getColor()), 
								state.getRobotY(world.getColor()), 
								state.getBallX(), 
								state.getBallY(),
								state.getEnemyX(world.getColor()),
								state.getEnemyY(world.getColor()));
		
		Point p = calculateDestination(output[0], output[1], state.getBallX(), state.getBallY(), output[2]);
		
		AbstractMode avoidancePoint = new WaypointMode(commander, p.getX(), p.getY());
		
		System.out.println("First waypoint: (" + p.getX() + "," + p.getY() + ")");
		
		/* hard coded goal */
		p = calculateDestination(state.getBallX(), state.getBallY(), 130, 40, 5);
		
		AbstractMode ballPoint = new WaypointMode(commander, p.getX(), p.getY());
		
		System.out.println("Final waypoint: (" + p.getX() + "," + p.getY() + ")");
		
		AbstractMode[] plan = {
			avoidancePoint,
			ballPoint
		};
		
		return plan;
		
	}
	
	
	
	public Point calculateDestination(double ballX, double ballY, double goalX, double goalY, double distance ){
		
		double destinationX;
		double destinationY;
		
		if(goalY == ballY){
			destinationX = ballX + distance;
			destinationY = ballY;
			
		}else{
			//calculate the vector from goal to ball
			double vectorx = (ballX-goalX);
			double vectory = (ballY-goalY);
			
			//normalising to unit vector
			double i = Math.sqrt((vectorx*vectorx) + (vectory*vectory));
			
			double unitVectorx = vectorx/i;
			double unitVectory = vectory/i;
			
			//calculate destination by adding multiples of the unit vector
			destinationX = (ballX + (distance*unitVectorx));
			destinationY = (ballY + (distance*unitVectory));

			
		}
		
		//System.out.println("X: " + destinationX + " Y: " + destinationY);
		Point destination = new Point((int)Math.round(destinationX), (int)Math.round(destinationY));
		return destination;
		
	}
	
	public double[] calculateWaypoint(double robotX, double robotY, double ballX, double ballY, double enemyX, double enemyY){
		
		 double gradient = (robotY - ballY)/(robotX-ballX);
		 
		 double holder = Math.abs(((ballX - robotX)*(robotY - enemyY) - (robotX - enemyX)*(ballY-robotY)));
		 
		 /* add some normalising thing stuff shit */
		 double distance = holder/Math.sqrt((ballX-robotX)*(ballX-robotX) + (ballY - robotY)*(ballY - robotY));
		 
		 //return distance;
		 
		 //calculating the point of intersection
		 double uHolder = ((enemyX - robotX)*(ballX-robotX)) + ((enemyY - robotY)*(ballY-robotY));
		 double u = uHolder/(((ballX-robotX)+(ballY - robotY)) * ((ballX-robotX)+(ballY - robotY)));
		 
		  double intersectionX = (robotX + u*(ballX - robotX));
		  double intersectionY = (robotY + u*(ballY - robotY));
		  
		  double[] output = new double[3];
		  output[0] = intersectionX;
		  output[1] = intersectionY;
		  
		  if(distance<20){
			  output[2] = (20 -distance);
		  }else{
		  	output[2] = 0;
		  }
		  
		  return output;
		  
	}
		
}
