package strategy.world;

public class Robot
{
	
	private double x;
	private double y;
	private double dx;
	private double dy;
	private int visible;
	
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getDir() {
		return Math.toDegrees(Math.atan2(dx, dy));
	}

	public int getVisible() {
		return visible;
	}

	public void setX(double new_x) {
		x = new_x;
	}

	public void setY(double new_y) {
		y = new_y;
	}

	public void setDX(double new_dir) {
		dx = new_dir;
	}

	public void setDY(double new_dir) {
		dy = new_dir;
	}

	public void setVisible(int vis) {
		visible = vis;
	}
	
}
