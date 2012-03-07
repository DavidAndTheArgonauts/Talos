package strategy.plan;

import strategy.mode.*;
import strategy.world.*;

import comms.robot.*;

import java.awt.*;

public class ShootPlan extends AbstractPlan
{

	public ShootPlan(Commander commander, World world)
	{
		super(commander,world);
	}
	
	public AbstractMode[] plan()
	{

		WorldState state = world.getWorldState();

		System.out.println("Ball coord: (" + (int)state.getBallX() + "," + (int)state.getBallY() + ")");
		System.out.println("Enemy coord: (" + (int)state.getEnemyX(world.getColor()) + "," + (int)state.getEnemyY(world.getColor()) + ")");

		double[] goalCoords = world.getGoalCoords();
		
		Point p = calculateDestination(state.getBallX(),state.getBallY(),goalCoords[0],goalCoords[1],16);
		
		boolean finalArc = false;
		
		if (p.getY() < 10)
		{
			p.setLocation(p.getX(), 10);
			finalArc = true;
		}
		else if (p.getY() > 70)
		{	
			p.setLocation(p.getX(), 70);
			finalArc = true;
		}
		
		AStarPlan astarplan = new AStarPlan(commander,world,p.getX(),p.getY(), true);
		AbstractMode[] aplan = astarplan.plan();
		
		AbstractMode[] planb = new AbstractMode[0];
		
		AbstractMode ballFace = new TurnMode(commander, state.getBallX(),state.getBallY());
		
		planb = new AbstractMode[aplan.length+1];
		for (int i = 0; i < aplan.length; i++)
		{
			planb[i] = aplan[i];
		}
		planb[planb.length-1] = ballFace;
		
		if (finalArc)
		{
			
			System.out.println("ARC PLANNED");
			
			double[] gCoords = world.getGoalCoords();
			
			double ballY = gCoords[1],
					robotY = state.getBallY(),
					ballX = gCoords[0],
					robotX = state.getBallX();
			
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

			System.out.println("Angle DEGREES: " + Math.toDegrees(angle));
			
			if (Math.toDegrees(angle) > 180)
				angle = (Math.PI * 2) - angle;
			
			AbstractMode arcmode = new ArcMode(commander, 40, Math.toDegrees(angle) * 0.75, 50);
			
			AbstractMode[] planc = new AbstractMode[planb.length+1];
			for (int i = 0; i < aplan.length; i++)
			{
				planc[i] = planb[i];
			}
			planc[planc.length-1] = arcmode;
			
			planb = planc;
			
		}
		
		return planb;

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

			/*if(destinationX>120){
				while (destinationX>120){
					destinationX-=5;
					System.out.println("David");
				}	
			} else {
				while (destinationX<10){
					destinationX+=5;
					System.out.println("Ewing");
				}
			}
			if (destinationY>70){
				while (destinationY>70){
					destinationY-=5;
					System.out.println("Rankin");
				}
			} else {
				while (destinationY<10){
					destinationY+=5;
					System.out.println("Dickface");
				}
			}*/
			
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

		int distance_threshold = 27;

		if(distance<distance_threshold){
			// Enemy robot is close from robot - ball vector	
			if (enemyY>40){
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
			} else if (enemyY<=40){
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

		System.out.println("Angle DEGREES: " + Math.toDegrees(angle));

		double addX = Math.sin(angle)*distance;
		double addY = Math.cos(angle)*distance;

		System.out.println("old distance: " + distance);

		// Super hacky fix to get round out of bound coordinates
		if (((enemyX + addX)>130)||(enemyX + addX)<0){
			System.out.println("X axis out of bounds");
			addX = addX*-1;
			addY = addY*-1;
			/*
			addX = Math.sin(angle)*-distance;
			addY = Math.cos(angle)*-distance;
			*/
		}
		if (((enemyY + addY)>80)||(enemyY + addY)<0){
			System.out.println("Y axis out of bounds");
			addX = addX*-1;
			addY = addY*-1;
			/*
			addX = Math.sin(angle)*-distance;
			addY = Math.cos(angle)*-distance;
			*/
		}

		System.out.println("new distance: " + distance);

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
