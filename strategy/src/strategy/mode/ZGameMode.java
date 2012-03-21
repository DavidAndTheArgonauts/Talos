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
    
	
	
	private static final int MAX_MOTOR_SPEED = 70;
	
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
	private ArrayList<Integer> estTimes = new ArrayList<Integer>();
	private double[] estBallSpeed, estRobotSpeed;
	private double estimatedBallX, estimatedBallY;
	private long lastKick = -1;
	private int kickcounts = 0;
    private double[] ball = new double[2];
    private double[] robot = new double[2];
  


	public void paint(Graphics g, int ratio )
	{

        estRobotSpeed = estimateRobotSpeed();
        robot = estimateRobotPos(0.4);
                
        ball[0] = state.getBallX();
        ball[1] = state.getBallY();

    
        estBallSpeed =  estimateBallSpeed();
        
           
        if (estBallSpeed == null || estRobotSpeed == null) return;
        
		GUI.drawCircle(     g, 
                            estimateBallPos( 0.4 )[0]*ratio, 
                            estimateBallPos( 0.4 )[1]*ratio, 
                            1.5*ratio, 
                            Color.WHITE, 
                            true );
        
        System.out.println( "ballSpeed: " + estBallSpeed[2] );
        System.out.println( "robotSpeed: " + estRobotSpeed[2] );
        System.out.println( "mode: " + mode );

        double speed = 30;
        double[] expPos = estimateBallPos(0.4);
        expPos = limitVectorToTable(ball, expPos);
        
        double dist = distanceRobotObject( expPos );
        double robotDistTime = dist/speed;

        expPos = estimateBallPos(0.4 + robotDistTime);
        expPos = limitVectorToTable(ball, expPos);
        
        System.out.println( "robotDistTime: " + robotDistTime );

        mode = "skip";
        
        if ( estBallSpeed[2] > 2 )
        {
            gotoX = expPos[0];
            gotoY = expPos[1];
            System.out.println( "estimating using ball" );
        } 

        else
        {
            double[] expFromEnemy = limitVectorToTableDir(  state.getEnemyX(world.getColor()), 
                                                            state.getEnemyY(world.getColor()),
                                                            state.getEnemyDX(world.getColor()),
                                                            state.getEnemyDY(world.getColor()) );
            gotoX = expFromEnemy[0];
            gotoY = expFromEnemy[1];
            System.out.println( "using enemy robot's direction" );
        }


        gotoX = GUI.getClickX();
        gotoY = GUI.getClickY();


        if ( Math.abs( angleRobotObj(gotoX, gotoY) ) > 60 ) {
            mode = "turnto";
            turntoX = gotoX - robot[0];
            turntoY = gotoY - robot[1];
        }
        














    
        g.setColor( Color.ORANGE );
        g.drawLine( (int) state.getEnemyX(world.getColor())*ratio, 
                    (int) state.getEnemyY(world.getColor())*ratio, 
                    (int) (state.getEnemyX(world.getColor())*ratio + state.getEnemyDX(world.getColor())*ratio*1000) , 
                    (int) (state.getEnemyY(world.getColor())*ratio + state.getEnemyDY(world.getColor())*ratio*1000) );

       
        
        g.setColor( Color.RED );
        g.drawLine( (int) robot[0]*ratio, 
                    (int) robot[1]*ratio, 
                    (int) gotoX*ratio, 
                    (int) gotoY*ratio );

        GUI.drawCircle( g, 
                        gotoX*ratio, 
                        gotoY*ratio, 
                        1.5*ratio, 
                        Color.BLUE, 
                        true );



        
        if ( mode == "stop" ) {
            targetDriveLeft = 0;
            targetDriveRight = 0;
        }

        int driveLeft = 0, driveRight = 0;
        
        int acceleration = 5;
        int deceleration = 10;

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

        if ( ControlGUI.paused ) {
            commander.setSpeed( 0, 0);
            prevDriveLeft = 0;
            prevDriveRight = 0;
        }

        else {
            commander.setSpeed( driveLeft, driveRight );
            prevDriveLeft = driveLeft;
            prevDriveRight = driveRight;
            System.out.println( "driveLeft: " + driveLeft + " driveRight: " + driveRight );
        }
        
        
        
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
			int MAX_TURNING = 30;
			
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
            

            
            if ( Math.abs( dirRoboTarget ) > 120 ) speedDelta = 0.4;
            else if ( Math.abs( dirRoboTarget ) > 60 ) speedDelta = 0.3;
            else if ( Math.abs( dirRoboTarget ) > 30 ) speedDelta = 0.2;
            else {
                speedDelta = 0;
            }
 
			targetDriveLeft = (int) Math.round( leftMotor * MAX_TURNING * speedDelta );
			targetDriveRight = (int) Math.round( rightMotor * MAX_TURNING * speedDelta );
			
		}
		
		if ( mode == "goto" )
		{
            double dirX = state.getRobotDX(world.getColor());
            double dirY = state.getRobotDY(world.getColor());
            double dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));

			double dirTargetX = gotoX - robot[0];
			double dirTargetY = gotoY - robot[1];
			double dirTargetNorm = vecSize(dirTargetX, dirTargetY);
			double dirTargetAngle = Math.toDegrees( Math.atan2(dirTargetX, dirTargetY) );
		
			double dirRoboTarget = dirTargetAngle - dirAngle;
		
			//System.out.println("Direction (" + dirX + "," + dirY + ")");
			//System.out.println("dirAngle: " + dirAngle);
			//System.out.println("targetAngle: " + dirTargetAngle);
		
			if ( dirRoboTarget > 180 ) {
				dirRoboTarget -= 360;
			}

			else if ( dirRoboTarget < -180 ) {
				dirRoboTarget += 360;
			}

			//System.out.println("dirRoboTarget: " + dirRoboTarget);

			double leftMotor = 0, rightMotor = 0;


            double correctionFactor = Math.abs(dirRoboTarget) / 180f  /*/ (dirTargetNorm/50f)*/;

            





            if ( dirRoboTarget < 0 ) {
                leftMotor = 1;
                rightMotor = 1 - correctionFactor;
            }

            else {
                rightMotor = 1;
                leftMotor = 1 - correctionFactor;
            }
        /*
            if (  estRobotSpeed[2] < 5 ) {
                rightMotor = 1;
                leftMotor = 1;
            }*/

            if ( dirTargetNorm > 30 ) speedDelta = 1;
            else if ( dirTargetNorm > 20 ) speedDelta = dirTargetNorm / 30f;
            else speedDelta = 0;


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
		
       /* double robot[0] = robot[0];
		double robot[1] = robot[1];

		double ball[0] = ball[0];
		double ball[1] = ball[1];
		
		double enemyX = state.getEnemyX(world.getColor());
		double enemyY = state.getEnemyY(world.getColor());


        */

		/*if ( estimatedBallX < robot[0] ) 
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
        //times.add(500);

        double counter = 0;
        double dx = 0, dy = 0;
        double[] dir = new double[3];

        for ( int v : times ) {
            
            WorldState ago = world.getPastByTime(v);

            if ( ago != null ) {
                double pastBallX = ago.getBallX();
                double pastBallY = ago.getBallY();
                double timeAgo = (System.currentTimeMillis() - ago.getTime()) / 1000f;
                dx += (ball[0] - pastBallX) / timeAgo;
                dy += (ball[1] - pastBallY) / timeAgo;
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
        //times.add(500);
       
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
        pos[0] = ball[0] + estBallSpeed[0] * time;
        pos[1] = ball[1] + estBallSpeed[1] * time;
        return pos;
    }

    public double[] estimateRobotPos ( double time ) {
        double[] pos = new double[2];
        pos[0] = state.getRobotX(world.getColor()) + estRobotSpeed[0] * time;
        pos[1] = state.getRobotY(world.getColor()) + estRobotSpeed[1] * time;
        return pos;
    }

    public double distanceRobotObject ( double[] obj ) {
        double distance = vecSize(  robot[0] - obj[0], robot[1] - obj[1] );
        return distance;
    }

    public double[] limitVectorToTable( double[] start, double[] end ) {
        return limitVectorToTable( start[0], start[1], end[0], end[1] );
    }

    public double[] limitVectorToTable( double x, double y, double targetX, double targetY ) {

        double distFromEdge = 10;
        double[] result = new double[2];

        double dx = targetX - x;
        double dy = targetY - y;
        
        double Lx = distFromEdge;
        double Ty = distFromEdge;
        double Rx = World.WORLD_WIDTH - distFromEdge;
        double By = World.WORLD_HEIGHT - distFromEdge;

        double Bx = dx*(By-y)/dy + x;
        double Tx = dx*(Ty-y)/dy + x;
        double Ry = dy*(Rx-x)/dx + y;
        double Ly = dy*(Lx-x)/dx + y;

        if ( targetX > 0 && targetX < World.WORLD_WIDTH && targetY > 0 && targetY < World.WORLD_HEIGHT ) {
            result[0] = targetX;
            result[1] = targetY;
            return result;
        }

        if ( Tx < Rx && Tx > Lx && ( ( dx > 0 ) == ( (Tx - x) > 0 ) ) ) {
            result[0] = Tx;
            result[1] = Ty;
            System.out.println("Top");
        }
        if ( Bx < Rx && Bx > Lx && ( ( dx > 0 ) == ( (Bx - x) > 0 ) ) ) {
            result[0] = Bx;
            result[1] = By;
            System.out.println("Bottom");
        }
        if ( Ly > Ty && Ly < By && ( ( dy > 0 ) == ( (Ly - y) > 0 ) ) ) {
            result[0] = Lx;
            result[1] = Ly;
            System.out.println("Left");
        }
        if ( Ry > Ty && Ry < By && ( ( dy > 0 ) == ( (Ry - y) > 0 ) ) ) {
            result[0] = Rx;
            result[1] = Ry;
            System.out.println("Right");
        }

        return result;

    }

    public double[] limitVectorToTableDir( double x, double y, double dx, double dy ) {

        double distFromEdge = 10;
        double[] result = new double[2];

        double Lx = distFromEdge;
        double Ty = distFromEdge;
        double Rx = World.WORLD_WIDTH - distFromEdge;
        double By = World.WORLD_HEIGHT - distFromEdge;

        double Bx = dx*(By-y)/dy + x;
        double Tx = dx*(Ty-y)/dy + x;
        double Ry = dy*(Rx-x)/dx + y;
        double Ly = dy*(Lx-x)/dx + y;


        if ( Tx < Rx && Tx > Lx && ( ( dx > 0 ) == ( (Tx - x) > 0 ) ) ) {
            result[0] = Tx;
            result[1] = Ty;
        }
        if ( Bx < Rx && Bx > Lx && ( ( dx > 0 ) == ( (Bx - x) > 0 ) ) ) {
            result[0] = Bx;
            result[1] = By;
        }
        if ( Ly > Ty && Ly < By && ( ( dy > 0 ) == ( (Ly - y) > 0 ) ) ) {
            result[0] = Lx;
            result[1] = Ly;
        }
        if ( Ry > Ty && Ry < By && ( ( dy > 0 ) == ( (Ry - y) > 0 ) ) ) {
            result[0] = Rx;
            result[1] = Ry;
        }

        return result;

    }

    public double angleRobotObj ( double objX, double objY ) {

            double dirX = state.getRobotDX(world.getColor());
            double dirY = state.getRobotDY(world.getColor());
            double dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));

            double dirTargetX = objX - robot[0];
            double dirTargetY = objY - robot[1];
            double dirTargetNorm = vecSize(dirTargetX, dirTargetY);
            double dirTargetAngle = Math.toDegrees( Math.atan2(dirTargetX, dirTargetY) );

            double dirRoboTarget = dirTargetAngle - dirAngle;

            if ( dirRoboTarget > 180 ) {
                dirRoboTarget -= 360;
            }

            else if ( dirRoboTarget < -180 ) {
                dirRoboTarget += 360;
            }

            return dirRoboTarget;
    }

}
