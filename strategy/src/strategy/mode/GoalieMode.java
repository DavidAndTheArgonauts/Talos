package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import gui.*;

public class GoalieMode extends AbstractMode
{
	
	private int targetDriveLeft = 0, targetDriveRight = 0;
	private int prevDriveLeft = 0, prevDriveRight = 0;
	
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
		
		
		
		//double targetPos = GUI.getClickY();
		double targetPos = state.getBallY();
		
		double driveSpeed;
		
		System.out.println( "targetPos: " + targetPos );
		
		if ( Math.abs ( targetPos - pos ) > 50 ) 
		{
			driveSpeed = 1f;
		}
		else if ( Math.abs ( targetPos - pos ) > 5 ) 
		{
			driveSpeed = Math.abs ( targetPos - pos ) / 50f;
		}
		else {
			driveSpeed = 0;
		}
		
		
		int driveSign;
		
		if ( ( targetPos - pos ) > 0 ) 
		{
			driveSign = 1;			
		}
		else
		{
			driveSign = -1;	
		}
		
		double MAXSPEED = 100;
		
		targetDriveLeft = (int) (driveSpeed * driveSign * MAXSPEED);
		targetDriveRight = (int) (-1 * driveSpeed * driveSign * MAXSPEED);
		
		
		
		
		
		int driveLeft = 0, driveRight = 0;
        
		int acceleration = 20;
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
			
		System.out.println("Ultrasonic values: [" + lUS + "," + rUS + "]");
		System.out.println( "driveLeft: " + driveLeft + " driveRight: " + driveRight + "\n" );

		if ( ControlGUI.paused ) {
		    commander.setSpeed( 0, 0);
		    prevDriveLeft = 0;
		    prevDriveRight = 0;
		}

		else {
		    commander.setSpeed( driveLeft, driveRight );
		    prevDriveLeft = driveLeft;
		    prevDriveRight = driveRight;
	       }
		
	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}
	
}
