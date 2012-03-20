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
    
	
	
	private static final int MAX_MOTOR_SPEED = 100;
	
	private double[][] lines = new double[0][4];
	
	private int targetDriveLeft = 0, targetDriveRight = 0;
    private int prevDriveLeft = 0, prevDriveRight = 0;
	private double gotoX, gotoY; 
	private double turntoX, turntoY;
	private String mode = "goto";
	private World world;
	private WorldState state;
	private Point2D target;
	private double speedDelta = 0;
	private boolean turning = false;
	private long lastTime = -1;
	private double delay = 0.4;
	private ArrayList<Integer> estTimes = new ArrayList<Integer>();
	private double[] estBallSpeed, estRobotSpeed;
	private double estimatedBallX, estimatedBallY;
	private long lastKick = -1;
	private int kickcounts = 0;
    private double distFromEdge = 10;




	public ZGameMode(Commander commander)
	{	
		super(commander);
		
		GUI.subscribe(this);
	}

	
	public boolean complete()
	{
		return false;
	}


	public double[] estimateBallSpeed ( ) {

        ArrayList<Integer> times = new ArrayList<Integer>();
        
        times.add(100);
        times.add(200);
        times.add(300);
        times.add(500);

		double counter = 0;
		double dx = 0, dy = 0;
		double[] dir = new double[3];

		for ( int v : times ) {
			
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

        if (counter == 0) return null;

		dir[0] = dx / counter;
		dir[1] = dy / counter;
        dir[2] = vecSize(  dx / counter, dy / counter );

		return dir;

	}

    public double[] estimateRobotSpeed () {
    
        ArrayList<Integer> times = new ArrayList<Integer>();
        
        times.add(100);
        times.add(200);
        times.add(300);
        times.add(500);
       
        double counter = 0;
        double dx = 0, dy = 0;
        double[] dir = new double[3];

        for ( int v : times ) {
            
            WorldState ago = world.getPastByTime(v);

            if ( ago != null ) {
                double pastRobotX = ago.getRobotX(world.getColor());
                double pastRobotY = ago.getRobotY(world.getColor());
                double timeAgo = (System.currentTimeMillis() - ago.getTime()) / 1000f;
                dx += (state.getRobotX(world.getColor()) - pastRobotX) / timeAgo;
                dy += (state.getRobotY(world.getColor()) - pastRobotY) / timeAgo;
                counter++;
            }

        }

        
         
        if (counter == 0) return null;

        dir[0] = dx / counter;
        dir[1] = dy / counter;
        dir[2] = vecSize(  dx / counter, dy / counter );
        
        
        return dir;

    }

    public double[] estimateBallPos ( double time ) {
        double[] pos = new double[2];
        pos[0] = state.getBallX() + estBallSpeed[0] * time;
        pos[1] = state.getBallY() + estBallSpeed[1] * time;
        return pos;
    }

    public double[] estimateRobotPos ( double time ) {
        double[] pos = new double[2];
        pos[0] = state.getRobotX(world.getColor()) + estRobotSpeed[0] * time;
        pos[1] = state.getRobotY(world.getColor()) + estRobotSpeed[1] * time;
        return pos;
    }



        

	public void paint(Graphics g, int ratio )
	{
		


/*		for ( int v : estTimes ) {
			WorldState ago = world.getPastByTime(v);
			if ( ago != null ) {
				GUI.drawCircle( g, ago.getBallX()*ratio, ago.getBallY()*ratio, 1*ratio, Color.WHITE, true );
			}
		}*/

		estBallSpeed =  estimateBallSpeed();
        estRobotSpeed = estimateRobotSpeed();
           
        if (estBallSpeed == null || estRobotSpeed == null) return;
        
		GUI.drawDirection( g, state.getBallX()*ratio, state.getBallY()*ratio, 1*ratio, Color.WHITE, estBallSpeed[0], estBallSpeed[1] );

		GUI.drawCircle( g, estimateBallPos(delay)[0]*ratio, estimateBallPos(delay)[1]*ratio, 1.5*ratio, Color.WHITE, true );

        /*double Lx = distFromEdge;
        double Ty = distFromEdge;
        double Rx = World.WORLD_WIDTH - distFromEdge;
        double By = World.WORLD_HEIGHT - distFromEdge;

        double dx = estBallSpeed[0];
        double dy = estBallSpeed[1];
        
        double x = state.getBallX();
        double y = state.getBallY();

        double Bx = ( +dx*(By-y) + dy*x )/dy;
        double Tx = ( -dx*(y+Ty) + dy*x )/dy;
        double Ry = ( +dy*(Rx-x) + dx*y )/dx;
        double Ly = ( -dy*(x+Lx) + dx*y )/dx;

        String side = "";

        if ( Tx < Rx && Tx > Lx && ( ( dx > 0 ) == ( (Tx - x) > 0 ) ) ) {
            GUI.drawCircle( g, Tx*ratio, Ty*ratio, 1.5*ratio, Color.ORANGE, true );
            gotoX = Tx;
            gotoY = Ty;
        }
        if ( Bx < Rx && Bx > Lx && ( ( dx > 0 ) == ( (Bx - x) > 0 ) ) ) {
            GUI.drawCircle( g, Bx*ratio, By*ratio, 1.5*ratio, Color.ORANGE, true );
            gotoX = Bx;
            gotoY = By;
        }
        if ( Ly > Ty && Ly < By && ( ( dy > 0 ) == ( (Ly - y) > 0 ) ) ) {
            GUI.drawCircle( g, Lx*ratio, Ly*ratio, 1.5*ratio, Color.ORANGE, true );
            gotoX = Lx;
            gotoY = Ly;
        }
        if ( Ry > Ty && Ry < By && ( ( dy > 0 ) == ( (Ry - y) > 0 ) ) ) {
            GUI.drawCircle( g, Rx*ratio, Ry*ratio, 1.5*ratio, Color.ORANGE, true );
            gotoX = Rx;
            gotoY = Ry;
        }*/

        if ( estBallSpeed[2] > 1 ) {
            mode = "goto";
        }
    
        else mode = "stop";
        
        System.out.println( "ballSpeed: " + estBallSpeed[2] );
        System.out.println( "robotSpeed: " + estRobotSpeed[2] );
        System.out.println( "mode: " + mode );

        GUI.drawDirection( g, state.getRobotX(world.getColor())*ratio, state.getRobotY(world.getColor())*ratio, 1*ratio, Color.RED, estRobotSpeed[0], estRobotSpeed[1] );


        gotoX = estimateBallPos(delay)[0];
        gotoY = estimateBallPos(delay)[1];
        
        g.setColor( Color.RED );
        g.drawLine( (int) state.getRobotX(world.getColor())*ratio, 
                    (int) state.getRobotY(world.getColor())*ratio, 
                    (int) gotoX*ratio, 
                    (int) gotoY*ratio );



        
        if ( mode == "stop" ) {
            targetDriveLeft = 0;
            targetDriveRight = 0;
        }

        int driveLeft = 0, driveRight = 0;
        
        int acceleration = 10;
        int deceleration = 20;

        if ( targetDriveLeft > prevDriveLeft + acceleration ) {
            driveLeft = prevDriveLeft + acceleration;
        }
        else if ( targetDriveLeft < prevDriveLeft - deceleration ) {
            driveLeft = prevDriveLeft - deceleration;
        }
        else {
            driveLeft = targetDriveLeft;
        }
    
        if ( targetDriveRight > prevDriveRight + acceleration ) {
            driveRight = prevDriveRight + acceleration;
        }
        else if ( targetDriveRight < prevDriveRight - deceleration ) {
            driveRight = prevDriveRight - deceleration;
        }
        else {
            driveRight = targetDriveRight;
        }


      



            
        commander.setSpeed( driveLeft, driveRight );
        prevDriveLeft = driveLeft;
        prevDriveRight = driveRight;
        System.out.println( "driveLeft: " + driveLeft + " driveRight: " + driveRight );
        
	}

	
	public void reset(World world){}
	
	public void update(World world)
	{
		this.world = world;
		state = world.getWorldState();
		//updateStrategy( world );

        if (estBallSpeed == null || estRobotSpeed == null) return;
			
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
 
			targetDriveLeft = (int) Math.round( leftMotor * MAX_MOTOR_SPEED * speedDelta );
			targetDriveRight = (int) Math.round( rightMotor * MAX_MOTOR_SPEED * speedDelta );
			commander.setSpeed( targetDriveLeft, targetDriveRight );
		
			System.out.println("Speed (" + targetDriveLeft + "," + targetDriveRight + ")\n");


		}
		
		if ( mode == "goto" )
		{
            double dirX = estRobotSpeed[0];
            double dirY = estRobotSpeed[1];
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
                rightMotor = 1 - Math.abs(dirRoboTarget) / 300.;
            }

            else {
                rightMotor = 1;
                leftMotor = 1 - Math.abs(dirRoboTarget) / 300.;
            }
        
            if (  estRobotSpeed[2] < 5 ) {
                rightMotor = 1;
                leftMotor = 1;
            }

            if ( dirTargetNorm > 10 ) speedDelta = 1;
            else speedDelta = dirTargetNorm / 10f;


			targetDriveLeft = (int) Math.round( leftMotor * MAX_MOTOR_SPEED * speedDelta );
			targetDriveRight = (int) Math.round( rightMotor * MAX_MOTOR_SPEED * speedDelta );
		
		}
		
		if (lastTime != -1) {
		  //System.out.println( "fps: " + 1000000000f / (System.nanoTime()-lastTime) );
		}
		lastTime = System.nanoTime();
		
	}
	
	public void updateStrategy( World world ) 
	{
		
       /* double robotX = state.getRobotX(world.getColor());
		double robotY = state.getRobotY(world.getColor());

		double ballX = state.getBallX();
		double ballY = state.getBallY();
		
		double enemyX = state.getEnemyX(world.getColor());
		double enemyY = state.getEnemyY(world.getColor());


        */

		/*if ( estimatedBallX < robotX ) 
		{
			if ( ( ( System.currentTimeMillis() - lastKick ) / 1000f ) > 2 ) 
			{
				//commander.kick();
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
		}*/
		
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
