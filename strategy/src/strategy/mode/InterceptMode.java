package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class InterceptMode extends AbstractMode
{
	
	
	private enum State {Turning, Driving, Intercepting, Dribbling, Quitting, Nothing, Resetting, Arcing};
	
	private State currentState;
	private double[] destCoords;
	
	private int facingInterrupt = -1;
	private int distanceInterrupt = -1;
	private int quitInterrupt = -1;
	
	
	private int interruptId = -1;
	private boolean complete = false;
	
	
	public InterceptMode(Commander commander, double targetX, double targetY)
	{
		super(commander);
		currentState = State.Turning;
		updateTarget(targetX, targetY);
		
	}
	
	public void updateTarget(double targetX, double targetY) {
		destCoords[0] = targetX;
		destCoords[1] = targetY;
		
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void reset(World world){
		currentState = State.Resetting;
		
	}
	
	public void update(World world)
	{
		
		switch(currentState) {
		
			case Resetting:
				// work out if we need to turn or drive
				// etc
				
				
				break;
		
			case Turning:
					// if we want to turn
					// use Tools to calc angle
					// register interrupt
					// facingInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,60);
					// spin left
					int ang = 0;
					if(ang > 0) {
						commander.setSpeed(-50, 50);
					} else {
						commander.setSpeed(50, -50);
					}
					currentState = State.Nothing;
				break;
				
			case Arcing:
				
				break;
				
				
			case Driving:
					// if we want to drive forward
					// use Tools to calc distance
					// register interrupt
					double distToGo = 0;
					distanceInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,distToGo);
					commander.setSpeed(70,70);
					// drive forward
					currentState = State.Nothing;
				break;
				
				
			case Intercepting:
					// We are currently getting in the way of the ball
				
				currentState = State.Nothing;
				break;
				
				
			case Dribbling:
				
				currentState = State.Nothing;
				break;
				
			case Nothing:
				
				
				break;
				
			case Quitting:
				commander.stop();
				complete = true;
				break;
		
		
		}
		
		
		
		if (complete)
			return;
			
		if(quitInterrupt == -1) {
			quitInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,40);
		}
		
		/*
		if (interruptId == -1)
			interruptId = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,-360);

		if(facingInterrupt == -1) {
			interruptId = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,-360);
		}
		
		if(distanceInterrupt == -1) {
			distanceInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,40);
		}
		*/
		
	}
	
	
	
	public void handleInterrupt(World world, int interrupt)
	{
		
			if(facingInterrupt==interrupt) {
				currentState = State.Driving;
				commander.stop();
			} else if(distanceInterrupt==interrupt) {
				currentState = State.Intercepting;
				commander.stop();
			} else if(quitInterrupt==interrupt) {
				currentState = State.Quitting;
			} else {}
		
		
	}
	
		
	
	
	
}
