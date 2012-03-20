package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import gui.*;

import java.awt.*;

import java.awt.geom.Point2D;
import java.util.*;

public class OdoMode extends AbstractMode implements GUIDrawer
{
	
	private static final double DELAY = 0.65;
	
	private World lastWorld;
	private WorldState lastState;
	
	public OdoMode(Commander commander)
	{	
		super(commander);
		
		GUI.subscribe(this);
	}

	
	public boolean complete()
	{
		return false;
	}


	public double[] estimateBallSpeed() 
	{
		
		World world = lastWorld;
		
		int[] times = {
			100,
			200,
			300,
			500
		};

		double counter = 0;
		double dx = 0, dy = 0;
		double[] dir = new double[3];

		for ( int v : times ) {
			
			WorldState ago = world.getPastByTime(v);

			if ( ago != null ) {
				double pastBallX = ago.getBallX();
				double pastBallY = ago.getBallY();
				double timeAgo = (System.currentTimeMillis() - ago.getTime()) / 1000f;
				dx += (lastState.getBallX() - pastBallX) / timeAgo;
				dy += (lastState.getBallY() - pastBallY) / timeAgo;
				counter++;
			}

		}

		if (counter == 0) return null;

		dir[0] = dx / counter;
		dir[1] = dy / counter;
		dir[2] = vecSize(  dx / counter, dy / counter );

		return dir;

	}

	public double[] estimateRobotSpeed() 
	{
    		
    		World world = lastWorld;
    		
		int[] times = {
			100,
			200,
			300,
			500
		};

		double counter = 0;
		double dx = 0, dy = 0;
		double[] dir = new double[3];

		for ( int v : times ) {
			
			WorldState ago = world.getPastByTime(v);

			if ( ago != null ) {
				double pastRobotX = ago.getRobotX(world.getColor());
				double pastRobotY = ago.getRobotY(world.getColor());
				double timeAgo = (System.currentTimeMillis() - ago.getTime()) / 1000f;
				dx += (lastState.getRobotX(world.getColor()) - pastRobotX) / timeAgo;
				dy += (lastState.getRobotY(world.getColor()) - pastRobotY) / timeAgo;
				counter++;
            	}

		}

		if (counter == 0) return null;

		dir[0] = dx / counter;
		dir[1] = dy / counter;
		dir[2] = vecSize(  dx / counter, dy / counter );

		return dir;

	}

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
		double Lx = distFromEdge;
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
		
		lastWorld = world;
		lastState = world.getWorldState();
		
		//calculateInterceptWaypoint();
		
	}
	
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		
		
	}
	
	public static double vecSize(double x, double y)
	{
		return Math.sqrt((x*x) + (y*y));
	}
	
	
	
	
}
