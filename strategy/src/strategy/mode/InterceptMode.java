package strategy.mode;

import strategy.world.*;
import comms.robot.*;
import strategy.tools.*;
/*
public class InterceptMode extends AbstractMode
{
	
	
	private enum State {Turning, Driving, Intercepting, Dribbling, Quitting, Nothing, Arcing, Begin};
	
	private State currentState;
	private Vector destination;
	
	private int facingInterrupt = -1;
	private int distanceInterrupt = -1;
	private int quitInterrupt = -1;
	
	private World world;
	
	private ObjectTracking tracker;
	
	private ObjectTracking.PredictedPosition robot;
	
	
	private int interruptId = -1;
	private boolean complete = false;
	
	
	public InterceptMode(Commander commander, double targetX, double targetY)
	{
		super(commander);
		currentState = State.Begin;
		updateTarget(new Vector(targetX, targetY));
		tracker = new ObjectTracking();
		
		
	}
	
	public InterceptMode(Commander commander) {
		super(commander);
		currentState = State.Begin;
		tracker = new ObjectTracking();
		updateTarget(new Vector(60,40));
	}
	
	public void updateTarget(Vector pos) {
		destination = pos;
		currentState = State.Begin;
		
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void reset(World world){
		currentState = State.Begin;
		tracker = new ObjectTracking();
		tracker.updateWorld(world);
		
	}
	
	public void update(World world)
	{
		
		this.world = world;
		tracker.updateWorld(world);
		
		
		switch(currentState) {
		
			case Begin:
				// Decide where to start
				// probably turning since we are stationary

				currentState = State.Turning;
				break;
		
		
		
			case Turning:
					// if we want to turn
					// use Tools to calc angle
					// register interrupt
					// facingInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,60);
					// spin left
					double ang = 0;
					WorldState worldstate = world.getWorldState();
					ang = 8 * Math.toDegrees(  Vector.angleVectors(new Vector(worldstate.getRobotX(world.getColor()), worldstate.getRobotY(world.getColor())), destination));
					
					System.out.println("ANG: " + ang);

					//if(ang > 0) {
							commander.setSpeed(50, -50);
					///	} else {
							//commander.setSpeed(-50, 50);
						///}
						
						System.out.println(" << REGISTERING TURN INTERRUPT >> ");
						
						facingInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,-90);
						currentState = State.Nothing;
					
				break;
				
			case Arcing:
					// calculate angle
					// calculate distance
					// work out optimal arc?
					//robot = tracker.getRobotPosition();
					//double ang = Vector.angleVectors(robot.getPosition(), destination);
					//double resultant = new Vector(robot.getPosition().getX(), robot.getPosition().getY(), destination.getX(), destination.getY());
					
					
				
				break;
				
				
			case Driving:
					// if we want to drive forward
					// use Tools to calc distance
					// register interrupt
					robot = tracker.getRobotPosition();
					double resultant = (new Vector(robot.getPosition().getX(), robot.getPosition().getY(), destination.getX(), destination.getY())).size();
					distanceInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,resultant);
					commander.setSpeed(70,70);
					distanceInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,resultant);
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
				
				System.out.println("LOL");
				break;
				
			case Quitting:
				commander.stop();
				complete = true;
				break;

		}
		

		if (complete)
			return;
			
		
		
		/*
		if (interruptId == -1)
			interruptId = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,-360);

		if(facingInterrupt == -1) {
			interruptId = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_FACING,-360);
		}
		
		if(distanceInterrupt == -1) {
			distanceInterrupt = commander.getInterruptManager().registerInterrupt(InterruptManager.INTERRUPT_DISTANCE,40);
		}
		
		
	}
	
	
	
	public void handleInterrupt(World world, int interrupt)
	{
		
			if(facingInterrupt==interrupt) {
				currentState = State.Arcing;
				System.out.println(" << HANDLING TURN INTERRUPT >> ");
				commander.stop();
			} else if(distanceInterrupt==interrupt) {
				currentState = State.Arcing;
				commander.stop();
			} else if(quitInterrupt==interrupt) {
				currentState = State.Quitting;
			} else {}
		
		
	}
	
		
	
	
	
}*/
