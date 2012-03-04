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
	
	//Special

	private boolean weAreBlue;
	
	public WorldState(boolean areWeBlue){
		weAreBlue = areWeBlue;
	}

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
	
	// Robots
	// All methods have parameter getUs
	// Input true to return data for our robot. 

	public double getRobotX(boolean getUs) {
		if (getUs){
			if (weAreBlue){
				 return blueX;
			} else {
				return yellowX;
			}
		} else {
			if (!weAreBlue){
				 return blueX;
			} else {
				return yellowX;
			}
		}

	}
	
	public double getRobotY(boolean getUs) {
		if (getUs){
			if (weAreBlue){
				 return blueY;
			} else {
				return yellowY;
			}
		} else {
			if (!weAreBlue){
				 return blueY;
			} else {
				return yellowY;
			}
		}
	}

	public double getRobotDX(boolean getUs) {
		if (getUs){
			if (weAreBlue){
				 return blueDX;
			} else {
				return yellowDX;
			}
		} else {
			if (!weAreBlue){
				 return blueDX;
			} else {
				return yellowDX;
			}
		}
	}
	
	public double getRobotDY(boolean getUs) {
		if (getUs){
			if (weAreBlue){
				 return blueDY;
			} else {
				return yellowDY;
			}
		} else {
			if (!weAreBlue){
				 return blueDY;
			} else {
				return yellowDY;
			}
		}
	}
	
	public int getRobtVisible(boolean getUs) {
		if (getUs){
			if (weAreBlue){
				 return blueVisible;
			} else {
				return yellowVisible;
			}
		} else {
			if (!weAreBlue){
				 return blueVisible;
			} else {
				return yellowVisible;
			}
		}
	}

	public double getRobotDir(boolean getUs) {
		if (getUs){
			if (weAreBlue){
				 return Math.toDegrees(Math.atan2(blueDX,blueDY));
			} else {
				return Math.toDegrees(Math.atan2(yellowDX,yellowDY));
			}
		} else {
			if (!weAreBlue){
				 return Math.toDegrees(Math.atan2(blueDX,blueDY));
			} else {
				return Math.toDegrees(Math.atan2(yellowDX,yellowDY));
			}
		}

	}


	
}
