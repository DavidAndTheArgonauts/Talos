package strategy.world;

/**
 * Stores the robots position, direction and visibility
*/
public class Robot
{
	
	private double x;
	private double y;
	private double dx;
	private double dy;
	private int visible;
	
	/**
	 * @return The robot's x position in cm 
	*/
	public double getX() {
		return x;
	}
	
	/**
	 * @return The robot's y position in cm 
	*/
	public double getY() {
		return y;
	}
	
	/**
	 * @return The robot's direction in degrees
	*/
	public double getDir() {
		return Math.toDegrees(Math.atan2(dx, dy));
	}

	/**
	 * @return Whether the vision can see the robot (1 or 0)
	*/
	public int getVisible() {
		return visible;
	}
	
	/**
	 * Sets the robot's x position
	 * @param new_x The new x position
	*/
	public void setX(double new_x) {
		x = new_x;
	}
	
	/**
	 * Sets the robot's y position
	 * @param new_y The new y position
	*/
	public void setY(double new_y) {
		y = new_y;
	}
	
	/**
	 * Sets the robot's x direction component
	 * @param new_dir The x direction component
	*/
	public void setDX(double new_dir) {
		dx = new_dir;
	}

	/**
	 * Sets the robot's y direction component
	 * @param new_dir The y direction component
	*/
	public void setDY(double new_dir) {
		dy = new_dir;
	}

	/**
	 * Sets the robots visibility
	 * @param vis Visibility (1 or 0)
	*/
	public void setVisible(int vis) {
		visible = vis;
	}
	
}
