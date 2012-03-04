package strategy.controller;

import java.math.*;

/**
* Abstract Planner superclass. Any methods common to all planners should be implemented here rather than in the individual classes
* Child classes must implement plan() , which does the actual executions of robot control.
*/

public abstract class AbstractPlanner {
	//Looping variable. Is an instance variable so that other planners can stop each other from running.
	private boolean loopback = true;

	/**
	*Call to start the planner
	*/
	public void execute() {
		this.loopback = true;
		plan();
	}
	
	/**
	*Call to halt the planner
	*/
	public void halt() {
		this.loopback = false;
		StrategyManager.getCommand().stop();
		StrategyManager.getCommand().waitForQueueToEmpty();
	}
	
	/*
	public double angleBetween2Lines(double prevX, double prevY, double currentX, double currentY) {
		double angle1 = Math.atan2(currentY - Ball.getY(),
		                           currentX - Ball.getX());
		double angle2 = Math.atan2(currentY - prevY,
		                           currentX - prevX);
		return angle1-angle2;
	}
	*/
	
	/**
	* Used to check if the current planner should continue running. 
	*/
	public boolean getLoop(){
		return loopback;
	}
	
	/**
	* Should be implemented by the child classes. 
	*/
	protected abstract void plan();
}

