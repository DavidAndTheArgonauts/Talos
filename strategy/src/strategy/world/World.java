package strategy.world;

import comms.vision.*;
import java.util.*;

/*
* Contains the world states
*/
public class World
{
	
	public static final int ROBOT_YELLOW = 0;
	public static final int ROBOT_BLUE = 1;
	
	public static final double[] GOAL_LEFT = { 0, 40 };
	public static final double[] GOAL_RIGHT = { 130, 40 };
	
	public static final double WORLD_HEIGHT = 80, WORLD_WIDTH = 130;
	
	private ArrayList<WorldState> worldStates; 
	private VisionReceiver vision;
	private WorldState partialState;
	private boolean blueRobot = true;
	private int updateCount = 0;
	private int color;
	private double[] goal;

	public World(int color, double[] goal) {
		worldStates = new ArrayList<WorldState>();
		partialState = new WorldState();
		this.color = color;
		this.goal = goal;
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
	
	public int getColor()
	{
		return color;
	}
	
	public double[] getGoalCoords()
	{
		return goal;
	}
	
	/**
	* Returns Partial state of world. Should only be used by vision Receiver!!
	*/
	public WorldState getPartialState(){
		synchronized (worldStates)
		{
			if (updateCount == 14) {
				partialState.setTime();
				worldStates.add(partialState);
				partialState = new WorldState();
				updateCount= 0;
			}
		
			updateCount++;
			
			return partialState;
			
		}
		
	}
	
	/**
	* Returns latest complete state of world
	*/
	public WorldState getWorldState() {
		
		synchronized (worldStates)
		{		

			if (worldStates.size() == 0)
				return null;
		
			return worldStates.get(worldStates.size()-1);
		
		}
	}
	
	/**Returns the state x states ago.
	* Eg, if there are 500 stored states, and 
	*/
	public WorldState getPastState(int x) { 
		
		synchronized (worldStates)
		{
		
			int temp = worldStates.size() - x; 
			if (temp < 0) { 
				return worldStates.get(0);
			} else {
				return worldStates.get(temp);
			}
		
		}
		
	}
	
	public int getHistorySize() {
		
		synchronized (worldStates)
		{
			return worldStates.size();
		}
		
	}
	
	public void close()
	{
		vision.close();
	}


}
