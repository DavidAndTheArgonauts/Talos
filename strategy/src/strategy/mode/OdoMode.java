package strategy.mode;

import strategy.world.*;
import comms.robot.*;

import gui.*;

import strategy.tools.*;

public class OdoMode extends AbstractMode 
{
	
	private static final double EDGE_TOLERENCE = 10;
	
	private WillsBallsMode mode = null;
	
	public OdoMode(Commander commander)
	{	
		
		super(commander);
		
	}
	
	public boolean complete()
	{
		return false;
	}
	
	public void reset(World world){}
	
	public void update(World world)
	{
		
		getInterceptMode(world);
			
		if (mode == null)
		{
			return;
		}
		
		mode.update(world);
		
	}
	
	
	public void handleInterrupt(World world, int interrupt)
	{
		
		if (mode != null)
		{
			mode.handleInterrupt(world, interrupt);
		}
		
	}
	
	public void getInterceptMode(World world)
	{
		
		WorldState state = world.getWorldState();
		
		double Lx = EDGE_TOLERENCE;
		double Ty = EDGE_TOLERENCE;
		double Rx = World.WORLD_WIDTH - EDGE_TOLERENCE;
		double By = World.WORLD_HEIGHT - EDGE_TOLERENCE;
		
		ObjectTracking.PredictedPosition ballPos = ObjectTracking.getBallPosition(world);
		
		//if (ballPos == null || ballPos.getSpeed() < 10)
		if (ballPos == null)
			return;
		
		//Vector ballVel = ballPos.getVelocity();
		
		//double dx = ballVel.getX();
		//double dy = ballVel.getY();
		
		double dx = state.getEnemyDX(world.getColor());
		double dy = state.getEnemyDY(world.getColor());
		
		double x = state.getEnemyX(world.getColor());
		double y = state.getEnemyY(world.getColor());
		
		double Bx = ( +dx*(By-y) + dy*x )/dy;
		double Tx = ( -dx*(y+Ty) + dy*x )/dy;
		double Ry = ( +dy*(Rx-x) + dx*y )/dx;
		double Ly = ( -dy*(x+Lx) + dx*y )/dx;
		
		double targetX, targetY;
		
		if ( Tx < Rx && Tx > Lx && ( ( dx > 0 ) == ( (Tx - x) > 0 ) ) ) 
		{
		
			targetX = Tx;
			targetY = Ty;
			
		}
		else if ( Bx < Rx && Bx > Lx && ( ( dx > 0 ) == ( (Bx - x) > 0 ) ) ) 
		{
			targetX = Bx;
			targetY = By;
			
		}
		else if ( Ly > Ty && Ly < By && ( ( dy > 0 ) == ( (Ly - y) > 0 ) ) ) 
		{
			targetX = Lx;
			targetY = Ly;
			
		}
		else if ( Ry > Ty && Ry < By && ( ( dy > 0 ) == ( (Ry - y) > 0 ) ) ) 
		{
			targetX = Rx;
			targetY = Ry;
			
		}
		else
		{
			return;
		}
		
		if (mode == null)
			mode = new WillsBallsMode(commander, targetX, targetY);
		else
		{
			if ((new Vector(targetX, targetY, mode.getTargetX(), mode.getTargetY()).size()) > 30)
				mode.updateTarget(targetX,targetY);
		}
		
	}
	
}
