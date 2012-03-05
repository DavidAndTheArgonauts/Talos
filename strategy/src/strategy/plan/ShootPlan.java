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
		
		double[] goal = World.LEFT_GOAL_CENTER;
		
		WorldState state = world.getWorldState();

		System.out.println("Ball coord: (" + (int)state.getBallX() + "," + (int)state.getBallY() + ")");
		System.out.println("Enemy coord: (" + (int)state.getEnemyX(world.getColor()) + "," + (int)state.getEnemyY(world.getColor()) + ")");

		Point wayPoint;

		// If the ball is nearer the goal than the robot
		if (euclDistance(state.getRobotX(world.getColor()),state.getRobotY(world.getColor()),goal[0],goal[1])>
		euclDistance(state.getBallX(),state.getBallY(),goal[0],goal[1])){
			System.out.println("Ball is nearer the goal than the robot");
			wayPoint = calculateWaypoint(state.getRobotX(world.getColor()), 
					state.getRobotY(world.getColor()), 
					state.getBallX(), 
					state.getBallY(),
					state.getEnemyX(world.getColor()),
					state.getEnemyY(world.getColor()));

		} else {

			// If the robot is nearer the goal than the ball
			System.out.println("Robot is nearer the goal than the ball");
			if (state.getBallY()<40){
				wayPoint = new Point((int)Math.round(state.getBallX()), (int)Math.round(state.getBallY())+20);
			} else {
				wayPoint = new Point((int)Math.round(state.getBallX()), (int)Math.round(state.getBallY())-20);
			}
		}

		Point p = calculateDestination(state.getBallX(),state.getBallY(),goal[0],goal[1],20);
		Point q = calculateDestination(state.getBallX(),state.getBallY(),goal[0],goal[1],10);

		AbstractMode avoidancePoint = new WaypointMode(commander, wayPoint.getX(), wayPoint.getY());
		AbstractMode shootPoint = new WaypointMode(commander,p.getX(), p.getY());
		AbstractMode ballPoint = new WaypointMode(commander,q.getX(), q.getY());
		AbstractMode goalPoint = new WaypointMode(commander, goal[0],goal[1]);



		/*
		AbstractMode avoidancePoint = new WaypointMode(commander, p.getX(), p.getY());

		System.out.println("First waypoint: (" + p.getX() + "," + p.getY() + ")");

		p = calculateDestination(state.getBallX(), state.getBallY(), 130, 40, 5);

		AbstractMode ballPoint = new WaypointMode(commander, p.getX(), p.getY());

		System.out.println("Final waypoint: (" + p.getX() + "," + p.getY() + ")");
		 */

		AbstractMode[] plan = {
				avoidancePoint,
				shootPoint,
				ballPoint
				//goalPoint
		};

		return plan;

	}


	/***
	 * Calculates the waypoint to be in ideal shooting position from ball to goal
	 * @param ballX X coordinate of robot 
	 * @param ballY Y coordinate of robot 
	 * @param goalX X coordinate of goal 
	 * @param goalY Y coordinate of goal 
	 * @param distance the distance from shooting point and ball
	 * @return
	 */
	public Point calculateDestination(double ballX, double ballY, double goalX, double goalY, double distance ){

		double destinationX;
		double destinationY;

		if(goalY == ballY){
			destinationX = ballX + distance;
			destinationY = ballY;

		}else{
			//calculate the vector from goal to balls
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


	/***
	 * Calculates the waypoint to avoid obstacle between robot and ball
	 * @param robotX X coordinate of robot 
	 * @param robotY Y coordinate of robot 
	 * @param ballX X coordinate of ball
	 * @param ballY Y coordinate of ball
	 * @param enemyX X coordinate of enemy
	 * @param enemyY Y coordinate of enemy
	 * @return
	 */
	public  Point calculateWaypoint(double robotX, double robotY, double ballX, double ballY, double enemyX, double enemyY){

		double holder = Math.abs(((ballX - robotX)*(robotY - enemyY) - (robotX - enemyX)*(ballY-robotY)));

		// distance is the distance between enemy robot to robot - ball vector
		double distance = holder/Math.sqrt((ballX-robotX)*(ballX-robotX) + (ballY - robotY)*(ballY - robotY));

		int distance_threshold = 30;

		if(distance<distance_threshold){
			// Enemy robot is close from robot - ball vector	
			if (robotY>40){
				// Robot is in top half
				/*
				if (robotX<20){
					// Goal:(0,40), robot on left
					System.out.println("a");
					distance+=distance_threshold;
				} else {
					// Goal:(0,40), robot on right
					System.out.println("b");
					distance-=distance_threshold;
				} 
				*/
				
				System.out.println("Top half - distance");
				distance-=distance_threshold;
			} else if (robotY<=40){
				// Robot is in bottom half
				/*
				if (robotX>110){
					// Goal:(130,40), robot on right
					System.out.println("c");
					distance+=distance_threshold;
				} else {
					// Goal:(130,40), robot on left
					System.out.println("d");
					distance-=distance_threshold;	
				}
				*/
				System.out.println("Bottom half + distance");
				distance+=distance_threshold;
			}
		} else {
			// Enemy robot is far away from robot - ball vector
			// So return dummy values (robot coordinate)
			System.out.println("Enemy robot is not between robot and ball");
			Point wayPoint = new Point((int)robotX, (int)robotY);
			return wayPoint;
		}

		double gradient = (ballY-robotY)/(ballX-robotX);

		// Gets angle between robot and ball vector, and X axis
		double angle = Math.atan2(robotY - ballY, robotX - ballX);
		angle = Math.abs(angle);

		if (gradient<0){
			if(angle>(Math.PI/2))
				angle = Math.PI-angle;
		} else if (gradient>0){
			if(angle>(Math.PI/2))
				angle = Math.PI+angle;
		}

		System.out.println("Angle RAD: " + angle);
		System.out.println("Angle DEGREES: " + Math.toDegrees(angle));

		double addX = Math.sin(angle)*distance;
		double addY = Math.cos(angle)*distance;

		// Super hacky fix to get round out of bound coordinates
		if (((enemyX + addX)>130)||(enemyX + addX)<0){
			System.out.println("adas");
			addX = addX*-1;
			addY = addY*-1;
		}
		if (((enemyY + addY)>80)||(enemyY + addY)<0){
			System.out.println("adasasdas");
			addX = addX*-1;
			addY = addY*-1;
		}

		Point wayPoint = new Point((int)Math.round(enemyX + addX), (int)Math.round(enemyY + addY));
		return wayPoint;
	}

	/***
	 * Calculates the Euclidean Distance between object A and object B
	 * @param Ax Object A X coordinate
	 * @param Ay Object A Y coordinate
	 * @param Bx Object B X coordinate
	 * @param By Object B Y coordinate
	 * @return
	 */
	public static double euclDistance(double Ax, double Ay, double Bx, double By){
		double x = Math.pow(Ax-Bx, 2);
		double y = Math.pow(Ay-By, 2);
		return (Math.sqrt(x+y));
	}
}
