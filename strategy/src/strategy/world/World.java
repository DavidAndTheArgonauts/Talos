package strategy.world;

import comms.vision.*;
import java.util.*;

/*
* Contains the world states
*/
public class World
{
	
	
	private ArrayList<WorldState> worldStates; 
	private VisionReceiver vision;
	private WorldState partialState;
	private boolean blueRobot = true;
	private int updateCount = 0;

	

	public World() {
		worldStates = new ArrayList<WorldState>();
		partialState = new WorldState();
		
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

	public void setBlue(boolean areWeBlue){
		blueRobot = areWeBlue;
	}

	/**
	* Returns Partial state of world. Should only be used by vision Receiver!!
	*/
	public WorldState getPartialState(){
		if (updateCount == 13) {
			worldStates.add(partialState);
			partialState = new WorldState();
			updateCount= 0;
		}

		return partialState;
	}
	
	/**
	* Returns latest complete state of world
	*/
	public WorldState getWorldState() {
		return worldStates.get(worldStates.size()-1);
	}
	
	/**Returns the state x states ago.
	* Eg, if there are 500 stored states, and 
	*/
	public WorldState getPastState(int x) { 
		int temp = worldStates.size() - x; 
		if (temp < 0) { 
			return worldStates.get(0);
		} else {
			return worldStates.get(temp);
		}
		
	}
	
	public int get historySize() {
		return worldStates.size();
	}
	
	
	


}
