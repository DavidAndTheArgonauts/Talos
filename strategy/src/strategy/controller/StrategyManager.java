package strategy.controller;

import java.io.*;
import comms.robot.*;
import comms.vision.*;
import strategy.world.*;

import java.util.EnumMap;
import java.awt.geom.*;
import java.util.*;

import java.math.*;


public class StrategyManager {

	//Singleton. Constructor should not do anything. 
	private StrategyManager() {}
	
	//Singleton holder
	private static class Holder {
		public static final StrategyManager instance = new StrategyManager();
	}
	
	//Get the instance of the StrategyManager Singleton
	public static StrategyManager getInstance() {
		return Holder.instance;
	}

	//For future. If we get to the point where we use multiple swapple strategies, we'll use this block of code
	private static Map<PlannerEnum, AbstractPlanner> planners = new EnumMap<PlannerEnum, AbstractPlanner>(PlannerEnum.class);
	private static PlannerEnum current ; 
	static {
		planners.put(PlannerEnum.REACTIVE, new ReactivePlanner());
		planners.put(PlannerEnum.DEDUCTIVE, new DeductivePlanner());
		current = PlannerEnum.REACTIVE;
	}
	
	//For use of waypoints. Double precision
	public static ArrayList<Point2D.Double> wayPoints = new ArrayList<Point2d.Double>();
	
	//Single Commander for all planners. 
	private static Commander commander = new Commander("localhost", 9899);
	private static VisionReceiver vision; 
	private static World world;
	public static void main(){
		
		//Gui gui = new Gui();
		
		world = new World();
		vision = new VisionReceiver(5500, world);
		Values.setBlue(false); // set to false if using yellow... later on it should be GUI coded.
		
		//For future. Ignore for now.
		//planners.get(current).execute();
		
		//Example planner start. Only one planner should be running at anytime. 
		ZPlanner examplePlanner = new ZPlanner();
		examplePlanner.execute();

		vision.close()
	}

	//Planners cna use this to pass control to other strategies. 
	//They should halt their own running before calling this. 
	
	public static void switchStrategy(PlannerEnum e){
		planners.get(e).execute();
	}
	
	public static Commander getCommand(){
		return commander;
	}

	public static World getWorld(){
		return world;
	}

}
