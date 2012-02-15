package strategy.world;

/**
 * Stores the balls position and visibility
*/
public class Ball
{
	
	private double x;
	private double y;
	private int visible;
	
	/**
	 * @return The ball's x position
	*/
	public double getX() {
		return x;
	}

	/**
	 * @return The ball's y position
	*/
	public double getY() {
		return y;
	}

	/**
	 * @return The ball's visibility
	*/
	public int getVisible() {
		return visible;
	}

	/**
	 * Sets the ball's x position
	 * @param new_x The new x position
	*/
	public void setX(double new_x) {
		x = new_x;
	}

	/**
	 * Sets the ball's y position
	 * @param new_x The new y position
	*/
	public void setY(double new_y) {
		y = new_y;
	}

	/**
	 * Sets the balls visibility
	 * @param vis Visibility (1 or 0)
	*/
	public void setVisible(int vis) {
		visible = vis;
	}
	
}
