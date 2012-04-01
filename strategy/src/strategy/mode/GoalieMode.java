package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class GoalieMode extends AbstractMode
{
	
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
		
		int 	lUS = commander.getLeftUltrasonic(),
			rUS = commander.getRightUltrasonic();
		
		
		
		System.out.println(lUS + rUS);
		
		
		
		
		
		
		
		
		
		
		
		
		
		int driveLeft = 0, driveRight = 0;
        
		int acceleration = 10;
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
			
		System.out.println("Ultrasonic values: [" + lUS + "," + rUS + "]");
		System.out.println( "driveLeft: " + driveLeft + " driveRight: " + driveRight + "\n" );

		if ( ControlGUI.paused ) {
		    //commander.setSpeed( 0, 0);
		    prevDriveLeft = 0;
		    prevDriveRight = 0;
		}

		else {
		    //commander.setSpeed( driveLeft, driveRight );
		    prevDriveLeft = driveLeft;
		    prevDriveRight = driveRight;
	       }
		
	}
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}
	
}
