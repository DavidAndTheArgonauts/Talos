package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import gui.*;

import strategy.tools.*;

public class OdoMode extends AbstractMode //implements GUIDrawer
{
	
	private static final double EDGE_TOLERENCE = 10;
	
	public OdoMode(Commander commander)
	{	
		super(commander);
		
		//GUI.subscribe(this);
		
		ObjectTracking.PredictedPosition pp = new ObjectTracking.PredictedPosition(null,null,0);
		
	}
	
	public boolean complete()
	{
		return false;
	}
	
	/*
	public void paint(Graphics g, int ratio)
	{

		double[] estBallSpeed = estimateBallSpeed();
		double[] estRobotSpeed = estimateRobotSpeed();

		if (estBallSpeed == null || estRobotSpeed == null) return;

		GUI.drawDirection( g, lastState.getBallX()*ratio, lastState.getBallY()*ratio, 1*ratio, Color.WHITE, estBallSpeed[0], estBallSpeed[1] );

		double estimatedBallX = lastState.getBallX() + estBallSpeed[0] * DELAY;
		double estimatedBallY = lastState.getBallY() + estBallSpeed[1] * DELAY;

		GUI.drawCircle( g, estimatedBallX*ratio, estimatedBallY*ratio, 1.5*ratio, Color.WHITE, true );
        
	}
	
	/*
	public void findInterceptWP()
	{
		
		double Lx = EDGE_TOLERENCE;
		double Ty = EDGE_TOLERENCE;
		double Rx = World.WORLD_WIDTH - EDGE_TOLERENCE;
		double By = World.WORLD_HEIGHT - EDGE_TOLERENCE;

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
		}

		if ( estBallSpeed[2] > 2 ) mode = "goto";
		else mode = "stop";

		System.out.println( "ballSpeed: " + estBallSpeed[2] );
		System.out.println( "robotSpeed: " + estRobotSpeed[2] );
		System.out.println( "mode: " + mode );

		GUI.drawDirection( g, state.getRobotX(world.getColor())*ratio, state.getRobotY(world.getColor())*ratio, 1*ratio, Color.RED, estRobotSpeed[0], estRobotSpeed[1] );


		gotoX = x;
		gotoY = y;

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
	*/
	
	public void reset(World world){}
	
	public void update(World world)
	{
		
		
		
	}
	
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}
	
}
