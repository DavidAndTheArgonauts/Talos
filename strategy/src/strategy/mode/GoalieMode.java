package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import gui.*;

import java.util.*;

public class GoalieMode extends AbstractMode
{

	private double MAXSPEED = 30;
	
	private double targetDriveSpeed = 0;
	private int prevMotorSpeed = 0;
	private long lastTime = -1;
	private World world;
	private WorldState state;
	private double[] estBallSpeed;
	private double[] ball = new double[2];
	private long lastKick = -1;

	
	public GoalieMode(Commander commander)
	{
		super(commander);
		
		commander.enableUltrasonic();
	}
	
	public boolean complete()
	{
		return false;
	}
	
	public void reset(World world)
	{
		
	}
	
	public void update(World world)
	{
	
		this.world = world;
		state = world.getWorldState();
	
		WorldState state = world.getWorldState();

		ball[0] = state.getBallX();
        ball[1] = state.getBallY();

		estBallSpeed =  estimateBallSpeed();

		if (estBallSpeed == null ) return;

		

		double dirX = state.getRobotDX(world.getColor());
        double dirY = state.getRobotDY(world.getColor());
        double dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));
		
		double dirAngleMod;
		
		if (ControlGUI.shootingLeft) dirAngleMod = - dirAngle; 
		else dirAngleMod = 180 - dirAngle;
	
		if ( dirAngleMod > 180 ) {
                dirAngleMod -= 360;
        }
        else if ( dirAngleMod < -180 ) {
			dirAngleMod += 360;
        }
		System.out.printf("dirAngleMod %.2f\n", dirAngleMod);


		
		int lUS = commander.getLeftUltrasonic(),
			rUS = commander.getRightUltrasonic();
		
		int tUS, bUS;
		
		
		if (ControlGUI.shootingLeft) {
			tUS = rUS;
			bUS = lUS;
		}
		else
		{
			tUS = lUS;
			bUS = rUS;			
		}
		
		
		
		double pos;

		// position using closer
		double totalUS = 107;
		double halfRobot = ( World.WORLD_HEIGHT - totalUS) / 2f;

		if ( tUS < bUS ) pos = tUS + halfRobot;
		else pos = World.WORLD_HEIGHT - bUS - halfRobot;

		if ( lUS != 255 && rUS != 255 ) {
			totalUS = lUS + rUS;
			halfRobot = ( World.WORLD_HEIGHT - totalUS) / 2f;

			pos = ( (tUS + halfRobot) + (World.WORLD_HEIGHT - bUS - halfRobot) ) / 2f;
			//System.out.println("position using both");
		}

		if ( Math.abs(dirAngleMod) > 20 ) 
			pos = state.getRobotY(world.getColor());
		
		System.out.println( "top: " + tUS + " bottom: " + bUS );
		System.out.println( "pos: " + pos );
		
		
		
		//double targetPos = GUI.getClickY();
		//double targetPos = state.getBallY();
		double targetPos = estimateBallPos(0.5)[1];

		if ( targetPos < 10 ) targetPos = 10;
		if ( targetPos >  World.WORLD_HEIGHT - 10 ) targetPos =  World.WORLD_HEIGHT - 10;

		if ( !state.getBallVisible() ) targetPos =  World.WORLD_HEIGHT / 2f;
		
		double driveSpeed;
		
		System.out.println( "targetPos: " + targetPos );
		
		if ( Math.abs ( targetPos - pos ) > 20 )
			driveSpeed = 1f;
		else if ( Math.abs ( targetPos - pos ) > 0 ) 
			driveSpeed = Math.abs ( targetPos - pos ) / 20f;
		else 
			driveSpeed = 0;



		




		// turning correction

		int MAX_TURNING = 30;
		
		double turnLeftMotor = 0, turnRightMotor = 0, turnSpeedDelta = 0;

            if ( dirAngleMod > 0 ) {
                turnLeftMotor = -1;
                turnRightMotor = 1;
            }

            else  {
                turnLeftMotor = 1;
                turnRightMotor = -1;
            }
            

            
            if ( Math.abs( dirAngleMod ) > 120 ) turnSpeedDelta = 0.3;
            else if ( Math.abs( dirAngleMod ) > 60 ) turnSpeedDelta = 0.3;
            else if ( Math.abs( dirAngleMod ) > 30 ) turnSpeedDelta = 0.2;
            else {
                turnSpeedDelta = Math.abs( dirAngleMod ) / 200;
            }
 
			int turnDriveLeft = (int) Math.round( turnLeftMotor * MAX_TURNING * turnSpeedDelta );
			int turnDriveRight = (int) Math.round( turnRightMotor * MAX_TURNING * turnSpeedDelta  );



		// turning correction end
		



		double rearWheelSlowDown;

		int driveSign;
		if (	( !ControlGUI.shootingLeft && ( targetPos - pos ) > 0 ) || 
				( ControlGUI.shootingLeft && ( targetPos - pos ) < 0 )   )
		{ 
			// driving right
			if (rUS < 10) driveSpeed = 0;
			driveSign = 1;
			//rearWheelSlowDown = 0.1 -( dirAngleMod / 100f );
            //rearWheelSlowDown = 0;
            rearWheelSlowDown = -( dirAngleMod / 200f );
		}
		else {
			// driving left
			if (lUS < 10) driveSpeed = 0;
			driveSign = -1;
			//rearWheelSlowDown = 0.1 + 0.1 * Math.pow( (prevMotorSpeed / 100f), 2 ) + ( dirAngleMod / 100f );
            rearWheelSlowDown = ( dirAngleMod / 200f );
            //rearWheelSlowDown = 0;
		}

		if (rearWheelSlowDown > 1) rearWheelSlowDown = 1;
		if (rearWheelSlowDown < -1) rearWheelSlowDown = -1;

		System.out.printf("rearWheelSlowDown %.2f\n", rearWheelSlowDown);



		if ( 	Math.abs ( estimateBallPos(0.5)[0] - state.getRobotX(world.getColor()) ) < 10
				&& ( System.currentTimeMillis() - lastKick ) / 1000f > 0.5
				&& !ControlGUI.paused )
        {
            commander.kick();
			commander.waitForQueueToEmpty();
            lastKick = System.currentTimeMillis();
            //System.out.println("kicking!" );
        }












		targetDriveSpeed = driveSpeed * driveSign * MAXSPEED;
		
		int motorSpeed = 0;
        int acceleration = 10;
		int deceleration = 10;

		if ( targetDriveSpeed > prevMotorSpeed + acceleration ) {
		    motorSpeed = (int) (prevMotorSpeed + acceleration);
		}
		else if ( targetDriveSpeed < prevMotorSpeed - deceleration ) {
		    motorSpeed = (int) (prevMotorSpeed - deceleration);
		}
		else {
		    motorSpeed = (int) (targetDriveSpeed);
		}


		int rearMotorSpeed = (int) ( motorSpeed * (1 - rearWheelSlowDown) + turnDriveRight );
		int frontMotorSpeed = (int) ( motorSpeed + turnDriveLeft );
		
		System.out.println("Ultrasonic values: [" + lUS + "," + rUS + "]");
		System.out.println( "targetDriveSpeed: " + Math.round(targetDriveSpeed) + " motorSpeed: " + motorSpeed + " rearMotorSpeed: " + rearMotorSpeed + "\n" );

		if ( ControlGUI.paused ) {
		    commander.setSpeed( 0, 0);
			commander.stop();
			commander.waitForQueueToEmpty();
		    prevMotorSpeed = 0;
			System.out.println("PAUSED");
		}

		else {
			commander.setSpeed( frontMotorSpeed, -rearMotorSpeed );
			commander.waitForQueueToEmpty();
			prevMotorSpeed = motorSpeed;
	    }

		if (lastTime != -1) {
			System.out.printf("fps %.2f\n", 1000000000f / (System.nanoTime()-lastTime));
		}
		lastTime = System.nanoTime();
		
	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}

	public double[] estimateBallPos ( double time ) {
        double[] pos = new double[2];
        pos[0] = ball[0] + estBallSpeed[0] * time;
        pos[1] = ball[1] + estBallSpeed[1] * time;
        return pos;
	
	}

	public static double vecSize(double x, double y)
	{
		return Math.sqrt((x*x) + (y*y));
	}

	public double[] estimateBallSpeed ( ) {

        ArrayList<Integer> times = new ArrayList<Integer>();
        
        times.add(100);
        //times.add(200);
        //times.add(300);
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


}
