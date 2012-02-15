package strategy.world;

import comms.vision.*;

public class World
{
	
	private Robot blueRobot, yellowRobot;
	private Ball ball;
	
	private VisionReceiver vision;
	
	public World()
	{
		
		blueRobot = new Robot();
		yellowRobot = new Robot();
		
		ball = new Ball();
		
	}
	
	public void listenForVision(int port)
	{
		
		vision = new VisionReceiver(port, this);
		
	}
	
	public Robot getBlueRobot()
	{
		return blueRobot;
	}
	
	public Robot getYellowRobot()
	{
		return yellowRobot;
	}
	
	public Ball getBall()
	{
		return ball;
	}
	
}
