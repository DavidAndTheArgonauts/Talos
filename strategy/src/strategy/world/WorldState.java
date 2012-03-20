package strategy.world;

import java.util.*;

public class WorldState {
	
	//Ball Data
	private double ballX;
	private double ballY;
	private boolean ballVisible;

	//Blue Robot Data
	private double blueX;
	private double blueY;
	private double blueDX;
	private double blueDY;
	private boolean blueVisible;

	//Yellow Robot Data
	private double yellowX;
	private double yellowY;
	private double yellowDX;
	private double yellowDY;
	private boolean yellowVisible;
	
	//Special
	private long created = -1;

	public long getCreatedMillis()
	{
		return created;
	}

	public long getTime()
	{
		return created;
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
		ballVisible = (vis != 0);
	}
	
	//Blue Robot
	public void setBlueX(double newx) {
		blueX = newx;
	}
	
	public void setBlueY(double newy) {
		blueY = newy;
	}

	public void setBlueDX(double newdx) {
		blueDX = newdx;
	}
	
	public void setBlueDY(double newdy) {
		blueDY = newdy;
	}
	
	public void setBlueVisible(int vis) {
		blueVisible = (vis != 0);
	}

	//Yellow Robot
	public void setYellowX(double newx) {
		yellowX = newx;
	}
	
	public void setYellowY(double newy) {
		yellowY = newy;
	}

	public void setYellowDX(double newdx) {
		yellowDX = newdx;
	}
	
	public void setYellowDY(double newdy) {
		yellowDY = newdy;
	}
	
	public void setYellowVisible(int vis) {
		yellowVisible = (vis != 0);
	}

	//Getters
	
	public double getRobotX(int color)
	{
		if (color == World.ROBOT_BLUE)
		{
			return getBlueX();
		}
		else
		{
			return getYellowX();
		}
	}
	
	public double getRobotDY(int color)
	{
		if (color == World.ROBOT_BLUE)
		{
			return getBlueDY();
		}
		else
		{
			return getYellowDY();
		}
	}
	
	public double getRobotDX(int color)
	{
		if (color == World.ROBOT_BLUE)
		{
			return getBlueDX();
		}
		else
		{
			return getYellowDX();
		}
	}
	
	public double getRobotY(int color)
	{
		if (color == World.ROBOT_BLUE)
		{
			return getBlueY();
		}
		else
		{
			return getYellowY();
		}
	}
	
	public double getRobotDir(int color)
	{
		if (color == World.ROBOT_BLUE)
		{
			return getBlueDir();
		}
		else
		{
			return getYellowDir();
		}
	}
	
	// enemy
	
	public double getEnemyX(int color)
	{
		if (color == World.ROBOT_YELLOW)
		{
			return getBlueX();
		}
		else
		{
			return getYellowX();
		}
	}
	
	public double getEnemyDY(int color)
	{
		if (color == World.ROBOT_YELLOW)
		{
			return getBlueDY();
		}
		else
		{
			return getYellowDY();
		}
	}
	
	public double getEnemyDX(int color)
	{
		if (color == World.ROBOT_YELLOW)
		{
			return getBlueDX();
		}
		else
		{
			return getYellowDX();
		}
	}
	
	public double getEnemyY(int color)
	{
		if (color == World.ROBOT_YELLOW)
		{
			return getBlueY();
		}
		else
		{
			return getYellowY();
		}
	}
	
	public double getEnemyDir(int color)
	{
		if (color == World.ROBOT_YELLOW)
		{
			return getBlueDir();
		}
		else
		{
			return getYellowDir();
		}
	}
	
	public boolean getEnemyVisible(int color)
	{
		if (color == World.ROBOT_YELLOW)
		{
			return getBlueVisible();
		}
		else
		{
			return getYellowVisible();
		}
	}
	
	public boolean getRobotVisible(int color)
	{
		if (color == World.ROBOT_BLUE)
		{
			return getBlueVisible();
		}
		else
		{
			return getYellowVisible();
		}
	}
	
	//Ball
	public double getBallX() {
		return ballX;
	}
	
	public double getBallY() {
		return ballY;
	}

	public boolean getBallVisible() {
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
	
	public boolean getBlueVisible() {
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
	
	public boolean getYellowVisible() {
		return yellowVisible;
	}

	public double getYellowDir() {
		return Math.toDegrees(Math.atan2(yellowDX,yellowDY));
	}
	
	public void setTime()
	{
		if (created == -1)
		{
			created = System.currentTimeMillis();
		}
	}
	
}
