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
	
	private static final int WIDTH = (int) World.WORLD_WIDTH;
	private static final int HEIGHT = (int) World.WORLD_HEIGHT;
	
	private JFrame jf;
	private WorldState state;
	
	private static ArrayList<GUIDrawer> painters = new ArrayList<GUIDrawer>();
	
	private int color;
	private double[] goal;
	private double robotRad = 8;
	private double ballRad = 2;
	
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

	public static void drawCircle( Graphics g, double x, double y, double radius, Color c, boolean filled ) {
		g.setColor( c );
		double tlx = x - radius;
		double tly = y - radius;
		if (filled) {
			g.fillOval( (int) tlx, (int) tly, (int) (2*radius), (int) (2*radius) );
		} 
		else {
			g.drawOval( (int) tlx, (int) tly, (int) (2*radius), (int) (2*radius) );
		}
	}

	public static void drawDirection( Graphics g, double x, double y, double radius, Color c, double dx, double dy ) {
		g.setColor( c );
		g.drawLine( (int) x, (int) y, (int) (x + dx*radius), (int) (y + dy*radius) );
	}
	
	// override paint component
	public void paintComponent( Graphics g ) {

		// call the super method
		super.paintComponent( g );
		
		// get size of panel
		double width = getBounds().getSize().getWidth();
		double height = getBounds().getSize().getHeight();

		// calculate width and height of each cell
		int ratio = (int)( Math.min( width/WIDTH, height/HEIGHT ) );
		
		int drawWidth = ratio * WIDTH;
		int drawHeight = ratio * HEIGHT;
		
		g.setColor(new Color(0,102,0));
		g.fillRect(0,0,drawWidth,drawHeight);
		
		// draw state
		if (state == null)
			return;
			
		double blueDX = state.getBlueDX();
		double blueDY = state.getBlueDY();
		
		if ( state.getBlueVisible() )
		{
			double x =  state.getBlueX();
			double y =  state.getBlueY();
			double dx = state.getBlueDX();
			double dy = state.getBlueDY();
			drawCircle( g, x*ratio, y*ratio, robotRad*ratio, Color.BLUE , true );
			drawDirection( g, x*ratio, y*ratio, robotRad*ratio, Color.YELLOW , dx, dy );
		}
	
		if ( state.getYellowVisible() )
		{
			double x =  state.getYellowX();
			double y =  state.getYellowY();
			double dx = state.getYellowDX();
			double dy = state.getYellowDY();
			drawCircle( g, x*ratio, y*ratio, robotRad*ratio, Color.YELLOW, true );
			drawDirection( g, x*ratio, y*ratio, robotRad*ratio, Color.BLUE, dx, dy );
		}
		
		if ( state.getBallVisible() )
		{
			double x =  state.getBallX();
			double y =  state.getBallY();
			drawCircle( g, x*ratio, y*ratio, ballRad*ratio, Color.RED, true );
		}

		// draw from all painters
		for (int i = 0; i < painters.size(); i++)
		{
			painters.get(i).paint(g, ratio );
		}
		
	}
	
}

