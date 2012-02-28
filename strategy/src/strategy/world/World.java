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

	public WorldState getPartialState(){
		if (updateCount == 13) {
			worldStates.add(partialState);
			partialState = new WorldState();
			updateCount= 0;
		}

		return partialState;
	}
	

	


}
