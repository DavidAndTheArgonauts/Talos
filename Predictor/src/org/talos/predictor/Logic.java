package org.talos.predictor;

import java.util.ArrayList;

public class Logic {
	
	private static final int LOOK_BACK = 15;
	private static final long PREDICT = 400;
	
	public static World getPrediction(ArrayList<World> history) throws Exception {
	
		if (history.size() - LOOK_BACK < 0)
		{
			throw new Exception("Trying to look too far back in time");
		}
	
		World cworld = history.get(history.size()-1);
		World lworld = history.get(history.size()-LOOK_BACK);
		long dtime = cworld.getTime() - lworld.getTime();
		// Very simple x and y prediction... just take a difference of coordinate between last suitable time and current time,
		// divide it by time to get coordinate progression per millisecond (speed), then add the distance it would travel in PREDICT
		// time to the current coordinate and this will give you approximate position...
		float dbx = cworld.blue.getX() - lworld.blue.getX();
		dbx = dbx + (dbx/dtime)*PREDICT;
		float dby = cworld.blue.getY() - lworld.blue.getY();
		dby = dby + (dby/dtime)*PREDICT;
		float dyx = cworld.yellow.getX() - lworld.yellow.getX();
		dyx = dyx + (dyx/dtime)*PREDICT;
		float dyy = cworld.yellow.getY() - lworld.yellow.getY();
		dyy = dyy + (dyy/dtime)*PREDICT;
		float drx = cworld.red.getX() - lworld.red.getX();
		drx = drx + (drx/dtime)*PREDICT;
		float dry = cworld.red.getY() - lworld.red.getY();
		dry = dry + (dry/dtime)*PREDICT;
		// Now this gets more complicated, first we look 1 frame back to see which direction is it's turning (because by looking further you can miss the direction
		// Then we check angle rotation speed by looking LOOK_BACK frames before and calculate, where it should be
		World sworld = history.get(history.size()-2);
		double sbangle = Math.atan2(sworld.blue.getDY(), sworld.blue.getDX());
		double syangle = Math.atan2(sworld.yellow.getDY(), sworld.yellow.getDX());
		double cbangle = Math.atan2(cworld.blue.getDY(), cworld.blue.getDX());
		double cyangle = Math.atan2(cworld.yellow.getDY(), cworld.yellow.getDX());
		double lbangle = Math.atan2(lworld.blue.getDY(), lworld.blue.getDX());
		double lyangle = Math.atan2(lworld.yellow.getDY(), lworld.yellow.getDX());
		double dbangle = cbangle - lbangle;
		double dyangle = cyangle - lyangle;
		if(cbangle > sbangle && sbangle < 0) {
			dbangle = -(Math.PI*2 - dbangle);
		} else if(cbangle < sbangle && sbangle > 0) {
			dbangle = Math.PI*2 + dbangle;
		} else if(cbangle > sbangle) {
			if(dbangle < 0) {
				dbangle = Math.PI*2 + dbangle;
			}
		} else {
			if(dbangle > 0) {
				dbangle = -(Math.PI*2 - dbangle);
			}
		}
		if(cyangle > syangle && syangle < 0) {
			dyangle = -(Math.PI*2 - dbangle);
		} else if(cyangle < syangle && syangle > 0) {
			dyangle = Math.PI*2 + dyangle;
		} else if(cyangle > syangle) {
			if(dyangle < 0) {
				dyangle = Math.PI*2 + dyangle;
			}
		} else {
			if(dyangle > 0) {
				dyangle = -(Math.PI*2 - dyangle);
			}
		}
		// Predicted angles
		double pbangle = cbangle + (dbangle/dtime)*PREDICT;
		double pyangle = cyangle + (dyangle/dtime)*PREDICT;
		if(pbangle > Math.PI)
			pbangle = pbangle - Math.PI*2;
		else if(pbangle < -Math.PI)
			pbangle = pbangle + Math.PI*2;
		if(pyangle > Math.PI)
			pyangle = pyangle - Math.PI*2;
		else if(pyangle < -Math.PI)
			pyangle = pyangle + Math.PI*2;
		// Convert angles to coordinates
		double btan = Math.tan(pbangle);
		double ytan = Math.tan(pyangle);
		float bdx, bdy, ydx, ydy;
		if(pbangle < -Math.PI/2 || pbangle > Math.PI/2) {
			bdx = -1;
			bdy = (float)-btan;
		} else {
			bdx = 1;
			bdy = (float)btan;
		}
		if(pyangle < -Math.PI/2 || pyangle > Math.PI/2) {
			ydx = -1;
			ydy = (float)-ytan;
		} else {
			ydx = 1;
			ydy = (float)ytan;
		}			
		return new World(new DirectionalObject(dbx, dby, bdx, bdy, 1), new DirectionalObject(dyx, dyy, ydx, ydy, 1), new SimpleObject(drx, dry, 1));
	}
}
