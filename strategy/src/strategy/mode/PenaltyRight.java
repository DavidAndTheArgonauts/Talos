package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import gui.*;

import java.util.*;

public class PenaltyRight extends AbstractMode
{

	
	
	private double targetDriveSpeed = 0;
	private long lastTime = -1;
	private World world;
	private WorldState state;
	private double[] ball = new double[2];
	private long startTime = -1;
	private boolean kicked = false;
	
	public PenaltyRight(Commander commander)
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

		double dirX = state.getRobotDX(world.getColor());
        double dirY = state.getRobotDY(world.getColor());
        double dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));
		
		double dirAngleMod;
		

		if (ControlGUI.shootingLeft) dirAngleMod = 345 - dirAngle; 
		else dirAngleMod = 165 - dirAngle;
	
		if ( dirAngleMod > 180 ) {
                dirAngleMod -= 360;
        }
        else if ( dirAngleMod < -180 ) {
			dirAngleMod += 360;
        }
		


		System.out.printf("dirAngleMod %.2f\n", dirAngleMod);


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

		commander.setSpeed( turnDriveLeft, -turnDriveRight );
		commander.waitForQueueToEmpty();

		
		if (startTime == -1) startTime = System.currentTimeMillis();

		if ( System.currentTimeMillis() - startTime > 5000 && !kicked ) {
			commander.kick();
			kicked = true;
		}
		
		


	   

		if (lastTime != -1) {
			System.out.printf("fps %.2f\n", 1000000000f / (System.nanoTime()-lastTime));
		}
		lastTime = System.nanoTime();
		
	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}

	public static double vecSize(double x, double y)
	{
		return Math.sqrt((x*x) + (y*y));
	}

}
