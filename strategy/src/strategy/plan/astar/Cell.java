package strategy.plan.astar;

public class Cell
{
	
	private int x, y;
	private Cell parent = null;
	private double h = 0, g = -1;
	
	public Cell(int x, int y, Cell parent, double g, double h)
	{
		
		this.x = x;
		this.y = y;
		this.parent = parent;
		this.g = g;
		this.h = h;
		
	}
	
	public Cell(int x, int y, double h)
	{
		
		this.x = x;
		this.y = y;
		this.h = h;
		
	}
	
	public int[] getCell()
	{
		
		int[] retval = {
			x,
			y
		};
		
		return retval;
		
	}
	
	public Cell getParent()
	{
		return parent;
	}
	
	public double getG()
	{
		return g;
	}
	
	public double getH()
	{
		return h;
	}
	
	public double getF()
	{
		return getH() + getG();
	}
	
	public void setParent(Cell parent)
	{
		this.parent = parent;
	}
	
	public void setG(double g)
	{
		this.g = g;
	}
	
}
