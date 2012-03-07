package strategy.plan;

import java.util.*;

import comms.robot.*;
import strategy.mode.*;
import strategy.world.*;
import strategy.plan.astar.*;

public class AStarPlan extends AbstractPlan
{
	
	// number of cells in each direction
	private static final int CELLS_X = 52;
	private static final int CELLS_Y = 28;
	
	private double targetX, targetY;
	
	// create our lists
	private ArrayList<Cell> 	openList = new ArrayList<Cell>(),
						closedList = new ArrayList<Cell>();
	
	private boolean nearestCell;
	
	public AStarPlan(Commander commander, World world, double targetX, double targetY, boolean nearestCell)
	{
		
		super(commander,world);
		this.targetX = targetX;
		this.targetY = targetY;
		this.nearestCell = nearestCell;
		
	}	
	
	public AbstractMode[] plan()
	{
		
		
		WorldState state = world.getWorldState();
		
		Cell ourCell = getCell(state.getRobotX(world.getColor()),
							state.getRobotY(world.getColor())),
			ballCell = getCell(state.getBallX(),state.getBallY()),
			enemyCell = getCell(state.getEnemyX(world.getColor()),
							state.getEnemyY(world.getColor())),
			goalCell = getCell(targetX,targetY);
		
		int[] ourPos = ourCell.getCell(), goalPos = goalCell.getCell();
		System.out.println("Starting A-Star plan from current position (" + ourPos[0] + "," + ourPos[1] + ") to (" + goalPos[0] + "," + goalPos[1] + ")");
		
		
		ourCell.setG(0);
		
		openList.add(ourCell);
		
		if (calculateG(goalCell,ourCell) == -1 && !nearestCell)
		{
			System.out.println("Goal cell unobtainable");
			return new AbstractMode[0];
		}
		
		Cell cheapestCell;
		while((nearestCell || !cellInList(goalCell,closedList)) && !openList.isEmpty())
		{
			
			// get cheapest cell
			cheapestCell = getCheapestCell(openList);
			
			// remove from open
			removeCellFromList(cheapestCell,openList);
			
			// add to closed
			insertCell(cheapestCell,closedList);
			
			// add all surrounding cells to open list
			insertAdjcentCells(cheapestCell,openList);
			
		}
		
		// if we didn't find the goal cell select the nearest
		if (nearestCell && !cellInList(goalCell,closedList))
		{
			
			double euclid = -1, thisEuclid;
			Cell cell = null;
			int[] gCoord = goalCell.getCell(), coord;
			for (int i = 0; i < closedList.size(); i++)
			{
				
				coord = closedList.get(i).getCell();
				thisEuclid = euclDistance(coord[0],coord[1],gCoord[0],gCoord[1]);
				
				if (cell == null || thisEuclid < euclid)
				{
					cell = closedList.get(i);
					euclid = thisEuclid;
				}
				
			}
			
			goalCell = cell;
			
		}
		else if (!nearestCell && !cellInList(goalCell,closedList))
		{
			System.out.println("Open list empty");
			return new AbstractMode[0];
		}
		
		// build list of way points
		Cell cell = getCellFromList(goalCell,closedList), c;
		int direction = 0, newDirection;
		ArrayList<Cell> waypointCells = new ArrayList<Cell>();
		while(!cellInList(ourCell,waypointCells))
		{
			
			c = cell.getParent();
			
			if (cellEquals(ourCell,c))
			{
				waypointCells.add(c);
				break;
			}
			
			newDirection = getDirection(cell,c);
			
			System.out.println("Direction = " + newDirection);
			
			if (newDirection == direction)
			{
				cell = c;
				continue;
			}
			
			direction = newDirection;
			waypointCells.add(cell);
			cell = c;
			
		}
		
		Cell[] wca = new Cell[waypointCells.size()];
		int i = 0;
		for (int j = waypointCells.size()-1; j >= 0; j--)
		{
			
			wca[i] = waypointCells.get(j);
			i++;
			
		}
		
		for (i = 0; i < wca.length; i++)
		{
			
			int[] test = wca[i].getCell();
			System.out.println("Next waypoint: (" + test[0] + "," + test[1] + ")");
			
		}
		
		// convert list to array of waypoint modes
		AbstractMode[] waypoints = new AbstractMode[waypointCells.size()];
		
		int[] coords;
		double cellWidth = World.WORLD_WIDTH / CELLS_X,
				cellHeight = World.WORLD_HEIGHT / CELLS_Y;
		for (int k = 0; k < wca.length; k++)
		{
			
			coords = wca[k].getCell();
			
			waypoints[k] = new WaypointMode(commander,(coords[0] * cellWidth) + (cellWidth / 2), (coords[1] * cellHeight) + (cellHeight / 2));
			
		}
		
		return waypoints;
		
	}
	
	private int getDirection(Cell a, Cell b)
	{
		
		if (a == null || b == null)
		{
			return -1;
		}
		
		int[] ac = a.getCell(),
			bc = b.getCell();
		
		if (ac[0] == bc[0]-1 && ac[1] == bc[1] - 1)
		{
			return 1;
		}
		else if (ac[0] == bc[0] && ac[1] == bc[1] - 1)
		{
			return 2;
		}
		else if (ac[0] == bc[0] + 1 && ac[1] == bc[1] - 1)
		{
			return 3;
		}
		else if (ac[0] == bc[0] - 1 && ac[1] == bc[1])
		{
			return 4;
		}
		else if (ac[0] == bc[0] + 1 && ac[1] == bc[1])
		{
			return 5;
		}
		else if (ac[0] == bc[0] - 1 && ac[1] == bc[1] + 1)
		{
			return 6;
		}
		else if (ac[0] == bc[0] && ac[1] == bc[1] + 1)
		{
			return 7;
		}
		else if (ac[0] == bc[0] + 1 && ac[1] == bc[1] + 1)
		{
			return 8;
		}
		
		System.out.println("ac = " + ac[0] + "," + ac[1] + "; bc = " + bc[0] + "," + bc[1] + ";");
		
		return -1;
		
	}
	
	public Cell getCell(double x, double y)
	{
		
		double cellWidth = World.WORLD_WIDTH / CELLS_X,
				cellHeight = World.WORLD_HEIGHT / CELLS_Y;
		
		int cellX = (int)Math.floor(x / cellWidth);
		int cellY = (int)Math.floor(y / cellHeight);
		
		System.out.println("getCell => (" + x + "," + y + ") => (" + cellX + "," + cellY + ")");
		
		return new Cell(cellX, cellY, calculateH(cellX,cellY));
		
	}
	
	private void removeCellFromList(Cell cell, List<Cell> list)
	{
		
		Cell existingCell = getCellFromList(cell,list);
		
		if (existingCell == null)
		{
			return;
		}
		
		list.remove(existingCell);
		
	}
	
	// manhatten method
	private double calculateH(int x, int y)
	{
		
		return (Math.abs(targetX - x) + Math.abs(targetY - y));
		
	}
	
	private double calculateG(int x, int y, Cell parent)
	{
		
		// bounds
		if (x < 2 || y < 2 || x > CELLS_X-2 || y > CELLS_Y-2)
		{
			System.out.println("(" + x + "," + y + ") out of bounds");
			return -1;
		}
		
		WorldState state = world.getWorldState();
		
		double cellWidth = World.WORLD_WIDTH / CELLS_X,
				cellHeight = World.WORLD_HEIGHT / CELLS_Y;
		
		double worldX = (x * cellWidth) + (cellWidth / 2),
			worldY = (y * cellHeight) + (cellHeight / 2);
		
		// robot & ball
		if ((state.getBallVisible()) && (Math.abs(worldX - state.getBallX()) < 10 && Math.abs(worldY - state.getBallY()) < 10))
		{
			System.out.println("(" + worldX + "," + worldY + ") is on ball (ball at [" + state.getBallX() + "," + state.getBallY() + "]");
			return -1;
		}
		
		double robotX = state.getEnemyX(world.getColor()),
			robotY = state.getEnemyY(world.getColor());
		
		if ((state.getEnemyVisible(world.getColor())) && (Math.abs(worldX - robotX) < 20 && Math.abs(worldY - robotY) < 20))
		{
			System.out.println("(" + x + "," + y + ") is enemy robot");
			return -1;
		}
		
		int[] pCoords = parent.getCell();
		
		if (x != pCoords[0] && y != pCoords[1])
		{
			return parent.getG() + 14;
		}
		
		return parent.getG() + 10;
		
	}
	
	private double calculateG(Cell cell, Cell parent)
	{
		
		int[] coord = cell.getCell();
		return calculateG(coord[0],coord[1],parent);	
		
	}
	
	private boolean cellInList(int x, int y, List<Cell> list)
	{
		
		Cell c;
		int[] coords;
		for (int i = 0; i < list.size(); i++)
		{
			c = list.get(i);
			coords = c.getCell();
			if (coords[0] == x && coords[1] == y)
			{
				return true;
			}
		}
		return false;
		
	}
	
	private boolean cellInList(Cell c, List<Cell> list)
	{
		
		int[] coord = c.getCell();
		return cellInList(coord[0], coord[1], list);
		
	}
	
	private Cell getCheapestCell(List<Cell> list)
	{
		
		double cost = -1;
		Cell c = null, d;
		for (int i = 0; i < list.size(); i++)
		{
			
			if (cost == -1)
			{
				c = list.get(i);
				cost = c.getF();
			}
			
			d = list.get(i);
			if (d.getF() < cost)
			{
				c = d;
				cost = c.getF();
			}
			
		}
		return c;
		
	}
	
	private Cell getCellFromList(Cell cell, List<Cell> list)
	{
		
		Cell c;
		int[] coord,
			cellCoord = cell.getCell();
		for (int i = 0; i < list.size(); i++)
		{
			
			c = list.get(i);
			coord = c.getCell();
			if (cellEquals(c,cell))
			{
				return c;
			}
			
		}
		
		return null;
		
	}
	
	private void insertAdjcentCells(Cell cell, List<Cell> list)
	{
		
		Cell c;
		int[] coord = cell.getCell();
		for (int i = coord[0] - 1; i <= coord[0]+1; i++)
		{
			for (int j = coord[1] - 1; j <= coord[1]+1; j++)
			{
				
				// if we're this cell
				if (i == coord[0] && j == coord[1])
				{
					continue;
				}
				
				double g = calculateG(i,j,cell),
						h = calculateH(i,j);
				c = new Cell(i,j,cell,g,h);
				
				if (cellInList(c,closedList))
				{
					continue;
				}
				
				insertCell(c,openList);
				
			}
		}
		
	}
	
	public boolean insertCell(Cell cell, List<Cell> list)
	{
		
		if (cell.getG() == -1)
		{
			return false;
		}
		
		Cell pcell = cell.getParent();
		int[] coord = cell.getCell();
		
		int[] pcoord = new int[2];
		if (pcell != null)
			pcoord = cell.getParent().getCell();
		
		if (cellInList(cell,list))
		{
			Cell existingCell = getCellFromList(cell,list);
			
			if (cell.getG() < existingCell.getG())
			{
				existingCell.setParent(cell.getParent());
				existingCell.setG(cell.getG());
				if (pcell != null)
					System.out.println("Updating cell (" + coord[0] + "," + coord[1] + ") to parent (" + pcoord[0] + "," + pcoord[1] + ")");
			}
			
			return true;
		}
		
		if (pcell != null)
			System.out.println("Inserting cell (" + coord[0] + "," + coord[1] + ") [g = " + cell.getG() + "; h = " + cell.getH() + ";] parent = (" + pcoord[0] + "," + pcoord[1] + ")");
		
		list.add(cell);
		return true;
		
	}
	
	public boolean cellEquals(Cell a, Cell b)
	{
		
		if (a == null || b == null)
		{
			return false;
		}
		
		int[] ac = a.getCell(),
			bc = b.getCell();
		
		if (ac[0] == bc[0] && ac[1] == bc[1])
		{
			return true;
		}
		return false;
		
	}
	
	public static double euclDistance(double Ax, double Ay, double Bx, double By){
		double x = Math.pow(Ax-Bx, 2);
		double y = Math.pow(Ay-By, 2);
		return (Math.sqrt(x+y));
	}
	
}
