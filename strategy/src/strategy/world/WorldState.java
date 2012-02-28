package strategy.world;

import java.util.*;

public class WorldState {
	
	//Ball Data
	private double ballX;
	private double ballY;
	private int ballVisible;

	//Blue Robot Data
	private double blueX;
	private double blueY;
	private double blueDX;
	private double blueDY;
	private int blueVisible;

	//Yellow Robot Data
	private double yellowX;
	private double yellowY;
	private double yellowDX;
	private double yellowDY;
	private int yellowVisible;

	//Setters

	//Ball
	public void setBallX(double newx) {
		ballX = newx;

	}
	
	public void setBallY(double newy) {
		ballY = newy;
	}

	public void setBallVisible(int vis) {
		ballVisible = vis;
	}
	
	//Blue Robot
	public void setBlueX(double newx) {
		blueX = newx;
	}
	
	public void setBlueY(double newy) {
		blueY = newy;
	}

	public void setBlueDX(double newdx) {
		blueDY = newdx;
	}
	
	public void setBlueDY(double newdy) {
		blueDY = newdy;
	}
	
	public void setBlueVisible(int vis) {
		blueVisible = vis;
	}

	//Yellow Robot
	public void setYellowX(double newx) {
		yellowX = newx;
	}
	
	public void setYellowY(double newy) {
		yellowY = newy;
	}

	public void setYellowDX(double newdx) {
		yellowDY = newdx;
	}
	
	public void setYellowDY(double newdy) {
		yellowDY = newdy;
	}
	
	public void setYellowVisible(int vis) {
		yellowVisible = vis;
	}

	//Getters

	//Ball
	public double getBallX() {
		return ballX;
	}
	
	public double getBallY() {
		return ballY;
	}

	public int getBallVisible() {
		return ballVisible;
	}
	
	//Blue Robot
	public double getBlueX() {
		return blueX;
	}
	
	public double getBlueY() {
		return blueY;
	}

	public double getBlueDX() {
		return blueDX;
	}
	
	public double getBlueDY() {
		return blueDY;
	}
	
	public int getBlueVisible() {
		return blueVisible;
	}

	public double getBlueDir() {
		return Math.toDegrees(Math.atan2(blueDX,blueDY));
	}

	//Yellow Robot
	public double getYellowX() {
		return yellowX;
	}
	
	public double getYellowY() {
		return yellowY;
	}

	public double getYellowDX() {
		return yellowDX;
	}
	
	public double getYellowDY() {
		return yellowDY;
	}
	
	public int getYellowVisible() {
		return yellowVisible;
	}

	public double getYellowDir() {
		return Math.toDegrees(Math.atan2(yellowDX,yellowDY));
	}

	
}
