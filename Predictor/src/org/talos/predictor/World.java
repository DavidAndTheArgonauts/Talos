package org.talos.predictor;

public class World {
	
	DirectionalObject yellow, blue;
	SimpleObject red;
	long time;
	
	public World(DirectionalObject nblue, DirectionalObject nyellow, SimpleObject nred) {
		blue = nblue;
		yellow = nyellow;
		red = nred;
		time = System.currentTimeMillis();
	}
	
	public DirectionalObject getBlue() {
		return blue;
	}
	
	public DirectionalObject getYellow() {
		return yellow;
	}
	
	public SimpleObject getRed() {
		return red;
	}
	
	public long getTime() {
		return time;
	}
	
	public Object[] getObject() {
		Object obj[] = new Object[] { blue.getX(),   blue.getY(),   blue.getDX(),   blue.getDY(),
									  yellow.getX(), yellow.getY(), yellow.getDX(), yellow.getDY(),
									  red.getX(),    red.getY()};
		return obj;
	}
	
}
