package strategy.mode;

import strategy.world.*;
import comms.robot.*;

public class InterruptTestMode extends AbstractMode
{
	
	private int interruptId = -1;
	private boolean complete = false;
	private long lastTime = -1;
	private int leftMotorSpeed = 0;
	private int rightMotorSpeed = 0;
	private long previousCount = 0;
	
	public InterruptTestMode(Commander commander)
	{	
		super(commander);
	}
	
	public boolean complete()
	{
		return complete;
	}
	
	public void reset(World world){}
	
	public void update(World world)
	{

		double lSpeedNow, rSpeedNow;

		lSpeedNow = commander.getLeftSpeed();
		rSpeedNow = commander.getRightSpeed();

		commander.setSpeed(1,1);

		System.out.println("lSpeedNow: " + lSpeedNow + " rSpeedNow: " + rSpeedNow);

		System.out.println("getLeftRevolution: " + commander.getLeftRevolution() + " getRightRevolution: " +  commander.getRightRevolution());


		double targetSpeed = 0.5;

		if ( lSpeedNow < targetSpeed ) leftMotorSpeed += 5;
		else if ( lSpeedNow > targetSpeed ) leftMotorSpeed -= 5;

		if (leftMotorSpeed > 100) leftMotorSpeed = 100;
		if (leftMotorSpeed < -100) leftMotorSpeed = -100;

		if (previousCount != 0) System.out.println("countdiff: " + ( commander.getLeftRevolution() - previousCount) );
		previousCount = commander.getLeftRevolution();



		System.out.println("leftMotorSpeed: " + leftMotorSpeed + " rightMotorSpeed: " + rightMotorSpeed);
		commander.setSpeed(leftMotorSpeed,rightMotorSpeed);

	

		if (lastTime != -1) {
			System.out.printf("fps %.2f\n", 1000000000f / (System.nanoTime()-lastTime));
		}
		lastTime = System.nanoTime();

	}
	
	
	
	public void handleInterrupt(World world, int interrupt)
	{		
	}
	
	
	
}
