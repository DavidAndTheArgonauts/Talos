package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import gui.*;

import java.awt.*;

import java.awt.geom.Point2D;
import java.util.*;

public class ZGameMode extends AbstractMode implements GUIDrawer
{
	
	public static final double ROBOT_RADIUS = 5;
	public static final double ENEMY_RADIUS = 25;
	public static final double BALL_RADIUS = 12;
	public static final double DESTINATION_RADIUS = 2;
	
	private static final int MAX_MOTOR_SPEED = 0;
	
	private double[][] lines = new double[0][4];
	
	private int driveLeft = 0, driveRight = 0;
	private double gotoX, gotoY; 
	private double turntoX, turntoY;
	private String mode = "goto";
	private World world;
	private WorldState state;
	private Point2D target;
	private double speedDelta = 0;
	private boolean turning = false;
	private long lastTime = -1;
	
	
	public ZGameMode(Commander commander)
	{	
		super(commander);
		
		GUI.subscribe(this);
	}

	
	public boolean complete()
	{
		return false;
	}


	public double[] estimateBallSpeed ( ArrayList<Integer> values ) {

		double counter = 0;
		double dx = 0, dy = 0;
		double[] dir = new double[2];

		for ( int v : values ) {
			
			WorldState ago = world.getPastByTime(v);

			if ( ago != null ) {
				double pastBallX = ago.getBallX();
				double pastBallY = ago.getBallY();
				double timeAgo = (System.currentTimeMillis() - ago.getTime()) / 1000f;
				dx += (state.getBallX() - pastBallX) / timeAgo;
				dy += (state.getBallY() - pastBallY) / timeAgo;
				counter++;
			}

		}

		dir[0] = dx / counter;
		dir[1] = dy / counter;

		return dir;

	}

	public void paint(Graphics g, int ratio )
	{
		
		/*final double targetSize = 2;
		
		g.setColor(new Color(158,119,0));
		g.fillOval(
			(int)((target.getX() - targetSize*0.5) * cellWidth),
			(int)((target.getY() - targetSize*0.5) * cellHeight), 
			(int)(targetSize * cellWidth), 
			(int)(targetSize * cellHeight) );
		
		for (int i = 0; i < lines.length; i++)
		{
			
			g.drawLine((int)(lines[i][0] * cellWidth), (int)(lines[i][1] * cellHeight), (int)(lines[i][2] * cellWidth), (int)(lines[i][3] * cellHeight));
			
		}*/
		
		ArrayList<Integer> estTimes = new ArrayList<Integer>();
		estTimes.add(100);
		estTimes.add(200);		
		estTimes.add(300);
		estTimes.add(500);

		for ( int v : estTimes ) {
			WorldState ago = world.getPastByTime(v);
			if ( ago != null ) {
				GUI.drawCircle( g, ago.getBallX()*ratio, ago.getBallY()*ratio, 1*ratio, Color.WHITE, true );
			}
		}

		double[] estBallSpeed =  estimateBallSpeed ( estTimes );
		GUI.drawDirection( g, state.getBallX()*ratio, state.getBallY()*ratio, 1*ratio, Color.WHITE, estBallSpeed[0], estBallSpeed[1] );

		double delay = 0.4;

		double estimatedBallX = state.getBallX() + estBallSpeed[0] * delay;
		double estimatedBallY = state.getBallY() + estBallSpeed[1] * delay;

		GUI.drawCircle( g, estimatedBallX*ratio, estimatedBallY*ratio, 1.5*ratio, Color.WHITE, true );
		


	}
	
	public void reset(World world){}
	
	public void update(World world)
	{
		this.world = world;
		state = world.getWorldState();
		updateStrategy( world );
			
						
		if ( mode == "stop" )
		{
			commander.stop();
		}
			
				
		if ( mode == "turnto" )
		{
			turning = true;
			
			double targetAngle = Math.toDegrees(Math.atan2(turntoX,turntoY));
		
			double dirX = state.getRobotDX(world.getColor());
			double dirY = state.getRobotDY(world.getColor());
		
			double dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));
		
			double dirRoboTarget = targetAngle - dirAngle;
		
			System.out.println("Direction (" + dirX + "," + dirY + ")");
			System.out.println("dirAngle: " + dirAngle);
			System.out.println("targetAngle: " + targetAngle);
		
			if ( dirRoboTarget > 180 ) {
				dirRoboTarget -= 360;
			}

			else if ( dirRoboTarget < -180 ) {
				dirRoboTarget += 360;
			}

			System.out.println("dirRoboTarget: " + dirRoboTarget);

			double leftMotor = 0, rightMotor = 0, speedDelta = 0;
			    if ( dirRoboTarget > 0 ) {
				  leftMotor = -1;
				  rightMotor = 1;
			    }

			    else  {
				  leftMotor = 1;
				  rightMotor = -1;
			    }
			    
	
				
				if ( Math.abs( dirRoboTarget ) > 120 ) speedDelta = 0.5;
				else if ( Math.abs( dirRoboTarget ) > 60 ) speedDelta = 0.4;
				else if ( Math.abs( dirRoboTarget ) > 30 ) speedDelta = 0.2;
				else {
					speedDelta = 0;
					turning = false;
				}
 
			driveLeft = (int) Math.round( leftMotor * MAX_MOTOR_SPEED * speedDelta );
			driveRight = (int) Math.round( rightMotor * MAX_MOTOR_SPEED * speedDelta );
			commander.setSpeed( driveLeft, driveRight );
		
			System.out.println("Speed (" + driveLeft + "," + driveRight + ")\n");


		}
		
		if ( mode == "goto" )
		{
			double dirX = state.getRobotDX(world.getColor());
			double dirY = state.getRobotDY(world.getColor());
			double robotX = state.getRobotX(world.getColor());
			double robotY = state.getRobotY(world.getColor());
			double dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));

			double dirTargetX = gotoX - robotX;
			double dirTargetY = gotoY - robotY;
			double dirTargetNorm = vecSize(dirTargetX, dirTargetY);
			double dirTargetAngle = Math.toDegrees( Math.atan2(dirTargetX, dirTargetY) );
		
			double dirRoboTarget = dirTargetAngle - dirAngle;
		
			System.out.println("Direction (" + dirX + "," + dirY + ")");
			System.out.println("dirAngle: " + dirAngle);
			System.out.println("targetAngle: " + dirTargetAngle);
		
			if ( dirRoboTarget > 180 ) {
				dirRoboTarget -= 360;
			}

			else if ( dirRoboTarget < -180 ) {
				dirRoboTarget += 360;
			}

			System.out.println("dirRoboTarget: " + dirRoboTarget);

			double leftMotor = 0, rightMotor = 0;

			 if ( dirRoboTarget < 0 ) {
				leftMotor = 1;
				rightMotor = 1 - Math.abs(dirRoboTarget) / 90.;
			  }	

			  else {
				rightMotor = 1;
				leftMotor = 1 - Math.abs(dirRoboTarget) / 90.;
			  };


			driveLeft = (int) Math.round( leftMotor * MAX_MOTOR_SPEED * speedDelta );
			driveRight = (int) Math.round( rightMotor * MAX_MOTOR_SPEED * speedDelta );
			commander.setSpeed( driveLeft, driveRight );
		
			System.out.println("Speed (" + driveLeft + "," + driveRight + ")\n");
		
		}
		
		if (lastTime != -1) {
		  System.out.println( "fps: " + 1000000000f / (System.nanoTime()-lastTime) );
		}
		lastTime = System.nanoTime();
		
	}
	
	public void updateStrategy( World world ) 
	{
		
		if ( !state.getBallVisible() || !state.getRobotVisible(world.getColor()) )
		{
			mode = "stop";
			return;
		}
		
		double robotX = state.getRobotX(world.getColor());
		double robotY = state.getRobotY(world.getColor());

		double ballX = state.getBallX();
		double ballY = state.getBallY();
		
		double enemyX = state.getEnemyX(world.getColor());
		double enemyY = state.getEnemyY(world.getColor());





		double[] gCoords = world.getGoalCoords();
	
		target = calculateDestination(ballX, ballY ,gCoords[0],gCoords[1],10);
		
		if (target.getY() < 10)
		{
			target.setLocation(target.getX(),10);
		}
		else if (target.getY() > 70)
		{
			target.setLocation(target.getX(),70);
		}
		if (target.getX() < 10)
		{
			target.setLocation(10,target.getY());
		}
		else if (target.getX() > 120)
		{
			target.setLocation(120,target.getY());
		}
		
		double targetX = target.getX();
		double targetY = target.getY();
		
		boolean enemyIntersect = lineCircleIntersect(robotX, robotY, targetX, targetY, enemyX, enemyY, ENEMY_RADIUS);
		boolean ballIntersect = lineCircleIntersect(robotX, robotY, targetX, targetY, ballX, ballY, BALL_RADIUS);
		
		Point2D ballWP = findWP(robotX, robotY, targetX, targetY, ballX, ballY, BALL_RADIUS);
		Point2D enemyWP = findWP(robotX, robotY, targetX, targetY, enemyX, enemyY, ENEMY_RADIUS);


		
		
		
		if ( vecSize(ballX - robotX, ballY - robotY) < 10 ) {
		
			System.out.println("ball kicking");
		
			turntoX = gCoords[0] - robotX;
			turntoY = gCoords[1] - robotY;
		
			double targetAngle = Math.toDegrees(Math.atan2(turntoX,turntoY));
		
			double dirX = state.getRobotDX(world.getColor());
			double dirY = state.getRobotDY(world.getColor());
		
			double dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));
		
			double dirRoboTarget = targetAngle - dirAngle;
				
			if ( dirRoboTarget > 180 ) {
				dirRoboTarget -= 360;
			}

			else if ( dirRoboTarget < -180 ) {
				dirRoboTarget += 360;
			}
			
			
			if ( Math.abs( dirRoboTarget ) < 45 ) {
				commander.kick();
				System.out.println("kick1");
			}
			
			
		
				
			
		}
		
		
		else if ( vecSize(targetX - robotX, targetY - robotY) < 10 ) {

			System.out.println("Close to target");
			
			mode = "turnto";
			turntoX = gCoords[0] - robotX;
			turntoY = gCoords[1] - robotY;
			
			if ( !turning ) {
				mode = "goto";
				System.out.println("non-turning");
				
				gotoX = ( ballX + gCoords[0] ) / 2f;
				gotoY = ( ballY + gCoords[1] ) / 2f;
				speedDelta = 1;
				
				commander.kick();
				System.out.println("kick2");
			}
			
			
			
		}
		
		else if ( (!enemyIntersect && !ballIntersect)  )
		{
			System.out.println("Going straight to target");
			
			lines = new double[1][4];
			lines[0][0] = robotX;
			lines[0][1] = robotY;
			lines[0][2] = targetX;
			lines[0][3] = targetY;
			
			gotoX = targetX;
			gotoY = targetY;
			
			mode = "goto";
			
			double dirTargetNorm = vecSize(gotoX - robotX, gotoY - robotY);
			
			if ( dirTargetNorm > 20 ) speedDelta = 1;
			else speedDelta = dirTargetNorm / 20f;
			

			if ( dirTargetNorm < DESTINATION_RADIUS ) {
				mode = "stop";
				System.out.println("target found");
						
			}
			
		}
		else if (!enemyIntersect && ballIntersect)
		{
			
			System.out.println("Avoiding ball");
			
			Point2D waypoint = ballWP;
			
			lines = new double[2][4];
			
			lines[0][0] = robotX;
			lines[0][1] = robotY;
			lines[0][2] = waypoint.getX();
			lines[0][3] = waypoint.getY();
			
			lines[1][0] = targetX;
			lines[1][1] = targetY;
			lines[1][2] = waypoint.getX();
			lines[1][3] = waypoint.getY();
			
			mode = "goto";
			
			gotoX = waypoint.getX();
			gotoY = waypoint.getY();
			
			speedDelta = 1;
			
			double dirTargetNorm = vecSize(gotoX - robotX, gotoY - robotY);
			
			if (dirTargetNorm < DESTINATION_RADIUS)
			{
				
				gotoX = targetX;
				gotoY = targetY;
				
				dirTargetNorm = vecSize(gotoX - robotX, gotoY - robotY);
				
				if ( dirTargetNorm > 20 ) speedDelta = 1;
				else speedDelta = dirTargetNorm / 20f;
				
				
				if ( dirTargetNorm < DESTINATION_RADIUS ) {
					mode = "stop";
					System.out.println("target found");
				}
				
			}
			
		}
		else if (enemyIntersect && !ballIntersect)
		{
			
			System.out.println("Avoiding enemy");
			
			Point2D waypoint = enemyWP;
			
			lines = new double[2][4];
			
			lines[0][0] = robotX;
			lines[0][1] = robotY;
			lines[0][2] = waypoint.getX();
			lines[0][3] = waypoint.getY();
			
			lines[1][0] = targetX;
			lines[1][1] = targetY;
			lines[1][2] = waypoint.getX();
			lines[1][3] = waypoint.getY();
			
			mode = "goto";
			
			gotoX = waypoint.getX();
			gotoY = waypoint.getY();
			
			speedDelta = 1;
			
			double dirTargetNorm = vecSize(gotoX - robotX, gotoY - robotY);
			
			if (dirTargetNorm < DESTINATION_RADIUS)
			{
				
				gotoX = targetX;
				gotoY = targetY;
				
				dirTargetNorm = vecSize(gotoX - robotX, gotoY - robotY);
				
				if ( dirTargetNorm > 20 ) speedDelta = 1;
				else speedDelta = dirTargetNorm / 20f;
				
				
				if ( dirTargetNorm < DESTINATION_RADIUS ) {
					mode = "stop";
					System.out.println("target found");
				}
				
			}
			
		}
		else if (ballIntersect && enemyIntersect)
		{
			
			System.out.println("Avoiding enemy and ball");
			
			Point2D firstWP = null, secondWP = null;
			
			if (vecSize(ballWP.getX() - robotX, ballWP.getY() - robotY) < vecSize(enemyWP.getX() - robotX, enemyWP.getY() - robotY))
			{
				
				firstWP = ballWP;
				secondWP = findWP(firstWP.getX(), firstWP.getY(), targetX, targetY, enemyX, enemyY, ENEMY_RADIUS);
				
			}
			else
			{
				
				firstWP = enemyWP;
				secondWP = findWP(firstWP.getX(), firstWP.getY(), targetX, targetY, ballX, ballY, BALL_RADIUS);
				
			}
			
			lines = new double[3][4];
			
			lines[0][0] = robotX;
			lines[0][1] = robotY;
			lines[0][2] = firstWP.getX();
			lines[0][3] = firstWP.getY();
			
			lines[1][0] = firstWP.getX();
			lines[1][1] = firstWP.getY();
			lines[1][2] = secondWP.getX();
			lines[1][3] = secondWP.getY();
			
			lines[2][0] = targetX;
			lines[2][1] = targetY;
			lines[2][2] = secondWP.getX();
			lines[2][3] = secondWP.getY();
			
			mode = "goto";
			
			gotoX = firstWP.getX();
			gotoY = firstWP.getY();
			
			speedDelta = 1;
			
			double dirTargetNorm = vecSize(gotoX - robotX, gotoY - robotY);
			
			if (dirTargetNorm < DESTINATION_RADIUS)
			{
				
				gotoX = secondWP.getX();
				gotoY = secondWP.getY();
				
				speedDelta = 1;
				
				dirTargetNorm = vecSize(gotoX - robotX, gotoY - robotY);
				if ( dirTargetNorm < DESTINATION_RADIUS ) {
					
					gotoX = targetX;
					gotoY = targetY;
					
					dirTargetNorm = vecSize(gotoX - robotX, gotoY - robotY);
					
					if ( dirTargetNorm > 20 ) speedDelta = 1;
					else speedDelta = dirTargetNorm / 20f;
				
					if ( dirTargetNorm < DESTINATION_RADIUS ) {
					
						mode = "stop";
						System.out.println("target found");
					}
				}
				
			}
			
		}
		
		
		
		
		
		
	}
	
	
	
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}
	
	public static double vecSize(double x, double y)
	{
		return Math.sqrt((x*x) + (y*y));
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
	
	public static Point2D closestPoint(double lx1, double ly1, double lx2, double ly2, double targetX, double targetY) {

		final double xDelta = lx2 - lx1;
		final double yDelta = ly2 - ly1;

		if ((xDelta == 0) && (yDelta == 0)) {
			throw new IllegalArgumentException("p1 and p2 cannot be the same point");
		}

		final double u = ((targetX - lx1) * xDelta + (targetY - ly1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

		final Point2D closestPoint;
		if (u < 0) {
			closestPoint = new Point2D.Double(lx1,ly1);
		} else if (u > 1) {
			closestPoint = new Point2D.Double(lx2, ly2);
		} else {
			closestPoint = new Point2D.Double(lx1 + u * xDelta, ly1 + u * yDelta);
		}

		return closestPoint;
	}
	
	public static double distPointLine(double lx1, double ly1, double lx2, double ly2, double targetX, double targetY) {
		
		Point2D point = closestPoint( lx1, ly1, lx2, ly2, targetX, targetY);
		return point.distance(new Point2D.Double(targetX, targetY));
		
	}

	public static boolean lineCircleIntersect(double lx1, double ly1, double lx2, double ly2, double centerX, double centerY, double radius) {
		
		if (distPointLine(lx1, ly1, lx2, ly2, centerX, centerY) <= radius)
			return true;
		else
			return false;
		
	}
	
	public static Point2D findWP(double rx, double ry, double tx, double ty, double avoidx, double avoidy, double avoidRadius)
	{
		
		Point2D avoidP = closestPoint(rx, ry, tx, ty, avoidx, avoidy);
		
		double dirPX = avoidP.getX() - avoidx;
		double dirPY = avoidP.getY() - avoidy;
		
		double dirPNorm = vecSize(dirPX, dirPY);
		
		dirPX /= dirPNorm;
		dirPY /= dirPNorm;
		
		double retx = avoidx + dirPX * avoidRadius;
		double rety = avoidy + dirPY * avoidRadius;
		
		return new Point2D.Double(retx,rety);
		
	}
	
	
}
