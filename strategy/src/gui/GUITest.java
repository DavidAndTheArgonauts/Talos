package gui;

import strategy.world.*;

public class GUITest
{
	
	public static void main(String[] args)
	{
		
		double[] goal = {0,40};
		
		World w = new World(World.ROBOT_YELLOW,World.GOAL_LEFT);
		
		WorldState state = w.getPartialState();
		state.setBallX(65);
		state.setBallY(40);
		
		state.setBlueX(30);
		state.setBlueY(40);
		state.setBlueDX(1);
		state.setBlueDY(0);
		
		state.setYellowX(100);
		state.setYellowY(40);
		state.setYellowDX(0);
		state.setYellowDY(1);
		
		state.setBlueVisible(1);
		state.setYellowVisible(1);
		state.setBallVisible(1);
		
		for (int i = 0; i < 15; i++)
		{
			w.getPartialState();
		}
		
		GUI gui = new GUI(w.getWorldState(),0,goal);
		
		while(true) {}
		
	}
	
}
