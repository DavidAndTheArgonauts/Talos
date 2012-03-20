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
	private double delay = 0.65;
	private ArrayList<Integer> estTimes = new ArrayList<Integer>();
	private double[] estBallSpeed;
	private double estimatedBallX, estimatedBallY;
	private long lastKick = -1;
	private int kickcounts = 0;


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

		estBallSpeed =  estimateBallSpeed ( estTimes );
		GUI.drawDirection( g, state.getBallX()*ratio, state.getBallY()*ratio, 1*ratio, Color.WHITE, estBallSpeed[0], estBallSpeed[1] );

		estimatedBallX = state.getBallX() + estBallSpeed[0] * delay;
		estimatedBallY = state.getBallY() + estBallSpeed[1] * delay;

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
			//commander.stop();
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
		  //System.out.println( "fps: " + 1000000000f / (System.nanoTime()-lastTime) );
		}
		lastTime = System.nanoTime();
		
	}
	
	public void updateStrategy( World world ) 
	{
		
		mode = "skip";
		
		double robotX = state.getRobotX(world.getColor());
		double robotY = state.getRobotY(world.getColor());

		double ballX = state.getBallX();
		double ballY = state.getBallY();
		
		double enemyX = state.getEnemyX(world.getColor());
		double enemyY = state.getEnemyY(world.getColor());

		if ( estimatedBallX < robotX ) 
		{
			if ( ( ( System.currentTimeMillis() - lastKick ) / 1000f ) > 2 ) 
			{
				commander.kick();
				kickcounts++;
				lastKick = System.currentTimeMillis();
				System.out.println("kicking!" + kickcounts );
			}
			else {
			}
					
		}
			
		else
		{
			//commander.setSpeed( 0, 0 );
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
