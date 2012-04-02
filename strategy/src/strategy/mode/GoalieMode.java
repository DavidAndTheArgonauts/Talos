package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import gui.*;

public class GoalieMode extends AbstractMode
{
	
	private double targetDriveSpeed = 0;
	private int prevMotorSpeed = 0;
	private long lastTime = -1;
	
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
	
		WorldState state = world.getWorldState();
		
		int 	lUS = commander.getLeftUltrasonic(),
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
		
		double totalUS = 107;
			
		//if ( lUS != 255 && rUS != 255 ) totalUS = lUS + rUS;
		//else totalUS = 106;
		
		double halfRobot = ( World.WORLD_HEIGHT - totalUS) / 2f;
		
		double pos;

		// position using average
		/*if ( tUS == 255 ) pos = World.WORLD_HEIGHT - bUS - halfRobot;
		else if ( bUS == 255 ) pos = tUS + halfRobot;
		else pos = ( (tUS + halfRobot) + (World.WORLD_HEIGHT - bUS - halfRobot) ) / 2f;*/
		
		// position using closer
		if ( tUS < bUS ) pos = tUS + halfRobot;
		else pos = World.WORLD_HEIGHT - bUS - halfRobot;
		
		System.out.println( "top: " + tUS + " bottom: " + bUS );
		System.out.println( "pos: " + pos );
		
		
		
		double targetPos = GUI.getClickY();
		//double targetPos = state.getBallY();
		
		double driveSpeed;
		
		System.out.println( "targetPos: " + targetPos );
		
		if ( Math.abs ( targetPos - pos ) > 50 )
			driveSpeed = 1f;
		else if ( Math.abs ( targetPos - pos ) > 5 ) 
			driveSpeed = Math.abs ( targetPos - pos ) / 50f;
		else 
			driveSpeed = 0;
		
		int driveSign;
		if ( ( targetPos - pos ) > 0 ) 
			driveSign = 1;
		else 
			driveSign = -1;
		
		double MAXSPEED = 50;
		
		targetDriveSpeed = driveSpeed * driveSign * MAXSPEED;


		double dirX = state.getRobotDX(world.getColor());
        double dirY = state.getRobotDY(world.getColor());
        double dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));

		double dirAngleMod = 180 - dirAngle;
	
		if ( dirAngleMod > 180 ) {
                dirAngleMod -= 360;
        }

        else if ( dirAngleMod < -180 ) {
			dirAngleMod += 360;
        }

		double rearWheelSlowDown = dirAngleMod / 50f;
		if (rearWheelSlowDown > 1) rearWheelSlowDown = 1;
		if (rearWheelSlowDown < -1) rearWheelSlowDown = -1;

		System.out.printf("dirAngleMod %.2f\n", dirAngleMod);
		System.out.printf("rearWheelSlowDown %.2f\n", rearWheelSlowDown);
		










		
		int motorSpeed = 0;
        int acceleration = 20;
		int deceleration = 20;

		if ( targetDriveSpeed > prevMotorSpeed + acceleration ) {
		    motorSpeed = (int) (prevMotorSpeed + acceleration);
		}
		else if ( targetDriveSpeed < prevMotorSpeed - deceleration ) {
		    motorSpeed = (int) (prevMotorSpeed - deceleration);
		}
		else {
		    motorSpeed = (int) (targetDriveSpeed);
		}


		int rearMotorSpeed = (int) ( motorSpeed * (1 - rearWheelSlowDown) );
		
		System.out.println("Ultrasonic values: [" + lUS + "," + rUS + "]");
		System.out.println( "targetDriveSpeed: " + Math.round(targetDriveSpeed) + " motorSpeed: " + motorSpeed + " rearMotorSpeed: " + rearMotorSpeed + "\n" );

		if ( ControlGUI.paused ) {
		    commander.setSpeed( 0, 0);
		    prevMotorSpeed = 0;
		}

		else {
			commander.setSpeed( motorSpeed, -rearMotorSpeed );
			prevMotorSpeed = motorSpeed;
	    }

		if (lastTime != -1) {
		  System.out.println( "fps: " + 1000000000f / (System.nanoTime()-lastTime) );
		}
		lastTime = System.nanoTime();
		
	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}
	
}
