package gui;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;

import java.util.*;

import strategy.world.*;

import strategy.mode.*;

public class GUI extends JPanel
{
	
	private static final int DIRECTION_LENGTH = 5;
	
	private static final int WIDTH = 130;
	private static final int HEIGHT = 80;
	
	private JFrame jf;
	private WorldState state;
	
	private static ArrayList<GUIDrawer> painters = new ArrayList<GUIDrawer>();
	
	private int color;
	private double[] goal;
	
	public GUI(WorldState state, int color, double[] goalCoords)
	{
		
		this.color = color;
		this.goal = goalCoords;
		
		jf = new JFrame("Strategy Output");
		// add this to the frame
		jf.getContentPane().add( this );

		// set default close operation
		jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		// get the inset size of the JFrame
		jf.setSize( 5*WIDTH + jf.getInsets().left + jf.getInsets().right , 5*HEIGHT + jf.getInsets().top + jf.getInsets().bottom );

		// set the size of the panel
		setSize( 5*WIDTH , 5*HEIGHT );
		
		jf.setVisible(true);
		
		setWorldState(state);
		
	}
	
	public static void subscribe(GUIDrawer guid)
	{
		painters.add(guid);
	}
	
	public static void unsubscribe(GUIDrawer guid)
	{
		painters.remove(guid);
	}
	
	public void setWorldState(WorldState state)
	{
		
		this.state = state;
		
		repaint();
		
	}
	
	// override paint component
	public void paintComponent( Graphics g ) {

		// call the super method
		super.paintComponent( g );
		
		// get size of panel
		double width = getBounds().getSize().getWidth();
		double height = getBounds().getSize().getHeight();

		// calculate width and height of each cell
		int cellWidth = (int)( width/WIDTH );
		int cellHeight = (int)( height/HEIGHT );
		
		int drawWidth = cellWidth * WIDTH;
		int drawHeight = cellHeight * HEIGHT;
		
		g.setColor(new Color(0,102,0));
		g.fillRect(0,0,drawWidth,drawHeight);
		
		// draw state
		if (state == null)
			return;
		
		double robotSize = 0;
		if (color == World.ROBOT_BLUE)
		{
			robotSize = 2*ZGameMode.ROBOT_RADIUS;
		}
		else
		{
			robotSize = 2*ZGameMode.ENEMY_RADIUS;
		}
		
		
		double blueX = state.getBlueX();
		double blueY = state.getBlueY();
		
		double blueXTL = (blueX - robotSize * 0.5) * cellWidth;
		double blueYTL = (blueY - robotSize * 0.5) * cellHeight;
		
		double blueDX = state.getBlueDX();
		double blueDY = state.getBlueDY();
		
		if (state.getBlueVisible())
		{
			g.setColor(Color.BLUE);
			g.fillOval((int)(blueXTL), (int)(blueYTL), (int)(robotSize * cellWidth), (int)(robotSize * cellHeight));
			g.setColor(Color.YELLOW);
			g.drawLine((int)(blueX * cellWidth), (int)(blueY * cellHeight), (int)(blueX * cellWidth + robotSize * blueDX * cellWidth), (int)(blueY * cellHeight + robotSize * blueDY * cellHeight));
		}
		
		
		if (color == World.ROBOT_YELLOW)
		{
			robotSize = 2*ZGameMode.ROBOT_RADIUS;
		}
		else
		{
			robotSize = 2*ZGameMode.ENEMY_RADIUS;
		}
		
		double yellowX = state.getYellowX();
		double yellowY = state.getYellowY();
		
		double yellowXTL = (yellowX - robotSize * 0.5) * cellWidth;
		double yellowYTL = (yellowY - robotSize * 0.5) * cellHeight;
		
		double yellowDX = state.getYellowDX();
		double yellowDY = state.getYellowDY();
		
		if (state.getYellowVisible())
		{
			g.setColor(Color.YELLOW);
			g.fillOval((int)(yellowXTL), (int)(yellowYTL), (int)(robotSize * cellWidth), (int)(robotSize * cellHeight));
			g.setColor(Color.BLUE);
			g.drawLine((int)(yellowX * cellWidth), (int)(yellowY * cellHeight), (int)(yellowX * cellWidth + robotSize * yellowDX * cellWidth), (int)(yellowY * cellHeight + robotSize * yellowDY * cellHeight));
		}
		
		
		double ballX = state.getBallX();
		double ballY = state.getBallY();
		
		double ballXTL = (ballX - ZGameMode.BALL_RADIUS) * cellWidth;
		double ballYTL = (ballY - ZGameMode.BALL_RADIUS) * cellHeight;
		
		if (state.getBallVisible())
		{
			g.setColor(Color.RED);
			g.fillOval((int)(ballXTL), (int)(ballYTL), (int)(ZGameMode.BALL_RADIUS * 2 * cellWidth), (int)(ZGameMode.BALL_RADIUS * 2 * cellHeight));
		}
		
		// draw from all painters
		for (int i = 0; i < painters.size(); i++)
		{
			painters.get(i).paint(g, cellWidth, cellHeight);
		}
		
	}
	
}

