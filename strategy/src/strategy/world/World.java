package strategy.world;

import comms.vision.*;

/**
 * Contains the objects in the world 
*/
public class World
{
	
	private Robot blueRobot, yellowRobot;
	private Ball ball;
	
	private VisionReceiver vision;
	
	/**
	 * Creates a world with 2 robots and a ball
	*/
	public World()
	{
		
		blueRobot = new Robot();
		yellowRobot = new Robot();
		
		ball = new Ball();
		
	}
	
	/**
	 * The world will use a vision receiver to update itself
	 * @param port The port to listen on
	 * @see comms.vision.VisionReceiver
	*/
	public void listenForVision(int port)
	{
		
		vision = new VisionReceiver(port, this);
		
	}
	
	/**
	 * Gets the blue robot object
	 * @return The blue robot
	*/
	public Robot getBlueRobot()
	{
		return blueRobot;
	}
	
	/**
	 * Gets the yellow robot object
	 * @return The yellow robot
	*/
	public Robot getYellowRobot()
	{
		return yellowRobot;
	}
	
	/**
	 * Gets the ball object
	 * @return The ball
	*/
	public Ball getBall()
	{
		return ball;
	}
	
}
