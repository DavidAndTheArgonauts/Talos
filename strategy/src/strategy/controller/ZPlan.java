package strategy.controller;

import strategy.world.*;
import comms.robot.*;
import java.io.*;

public class ZPlan
{
	
	/**
	 * The port to connect to the proxy on
	*/
	private static final int PORT = 9899;
	
	private Thread touchThread;
	private final Commander c;
	private World w;
	private int color;
	
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args)
	{
		
		ZPlan z = new ZPlan(World.ROBOT_YELLOW);
		
		z.run();
		
	}
	
	public ZPlan(int color)
	{
		
		this.color = color;
		
		c = new Commander();
		w = new World(color);
		
		do
		{
			
			String host = readLine("Enter hostname (localhost): ");
			
			if (host.equals(""))
				host = "localhost";
			
			c.connect(host,PORT);
			
		}
		while (!c.isConnected());
		
		print("Connected!");
		
		Thread touchThread = new Thread()
		{
			
			public void run()
			{
				while (!Thread.interrupted())
				{
					if (c.isLeftSensorPushed() || c.isRightSensorPushed())
					{
						c.stop();
					}
					
					Thread.yield();
				}
			}
			
		};
		touchThread.start();
		
		w.listenForVision(5500);
		
		// wait for vision
		print("Waiting for vision");
		while (w.getWorldState() == null)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException ie)
			{
			
			}
		}
		print("Vision connected!");
		
	}
	
	public void run()
	{
		
		double prevX, prevY, endX, endY, dirX, dirY, dirNorm, ballX, ballY, dirBallX, dirBallY, robotX, robotY, robotDir, dirBallNorm,dirBallAngle, dirRoboBall, rightMotor, leftMotor, speedDelta, dirAngle, motorFullSpeed, spinSpeed, prevLeft, prevRight,driveLeft, driveRight, motorDelta;


		prevX = getX();
		prevY = getY();
		prevLeft = 0;
		prevRight = 0;

		while (true) {

		    robotX = getX();
		    robotY = getY();
		    robotDir = getDir();

		    // direction from movement
		    dirX = robotX - prevX;
		    dirY = robotY - prevY;
		    dirNorm = vecSize(dirX, dirY);
		    dirAngle = Math.toDegrees( Math.atan2(dirX, dirY) );

		    // if change of movement > 1, use direction from movement instead of from vision
		    if ( dirNorm > 1 ) {
			  robotDir = dirAngle;
		    }
		    

		    ballX = w.getWorldState().getBallX();
		    ballY = w.getWorldState().getBallY();

		    dirBallX = ballX - robotX;
		    dirBallY = ballY - robotY;
		    dirBallNorm = vecSize(dirBallX, dirBallY);
		    dirBallAngle = Math.toDegrees( Math.atan2(dirBallX, dirBallY) );
		    
		    dirRoboBall = dirBallAngle - robotDir;

		    if ( dirRoboBall > 180 ) {
			  dirRoboBall -= 360;
		    }

		    else if ( dirRoboBall < -180 ) {
			  dirRoboBall += 360;
		    }

		    System.out.println("dirRoboBall: " + dirRoboBall);
			
			spinSpeed = 0.05;
		
			
		    // if < -60 or > 60 then spin
		    if ( dirRoboBall > 60 ) {
			  leftMotor = -1;
			  rightMotor = 1;
			  speedDelta = spinSpeed;
		    }

		    else if ( dirRoboBall < -60 ) {
			  leftMotor = 1;
			  rightMotor = -1;
			  speedDelta = spinSpeed;
		    }

		    else {

			  // set motor speeds based on the direction between robot and ball
			  if ( dirRoboBall < 0 ) {
				leftMotor = 1;
				rightMotor = 1 - Math.abs(dirRoboBall) / 60.;
			  }	

			  else {
				rightMotor = 1;
				leftMotor = 1 - Math.abs(dirRoboBall) / 60.;
			  };


			  // normalize motor speed based on the distance between the robot and the ball
			  if (dirBallNorm > 50) {
				speedDelta = 1;
			  } else if (dirBallNorm > 20) {
				speedDelta = dirBallNorm / 50f;
			  } else {
				speedDelta = 0;
				System.out.println("kick!!!");
				c.kick();
				c.waitForQueueToEmpty();
			  }

		    }
		    
		    motorFullSpeed = 100;
		    
		    motorDelta = 1;
		    
		    
		    driveLeft = leftMotor * motorFullSpeed * speedDelta;
		    driveRight = rightMotor * motorFullSpeed * speedDelta;
		    
	
		    
			if ( (driveLeft - prevLeft) > motorDelta ) {
		    	driveLeft = prevLeft + motorDelta;
		    }
		    
		    if ( (driveRight - prevRight) > motorDelta ) {
		    	driveRight = prevRight + motorDelta;
		    }
		    
		    if ( (driveLeft - prevLeft) < -motorDelta ) {
		    	driveLeft = prevLeft - motorDelta;
		    }
		    
		    if ( (driveRight - prevRight) < -motorDelta ) {
		    	driveRight = prevRight - motorDelta;
		    }
		    
		    
		    prevLeft = driveLeft;
		    prevRight = driveRight;
		    
		    	     System.out.println("driveLeft: " + driveLeft);
		      System.out.println("driveRight: " + driveRight);
		    
		    
		    c.setSpeed( (int) Math.round( driveLeft ), (int) Math.round( driveRight ) );
		    c.waitForQueueToEmpty();

		    // run at 10 FPS, for the movement from robot function to work
		    /*try{Thread.sleep(100);}
		    catch(Exception e){}*/
		    
		    prevX = robotX;
		    prevY = robotY;
		}
		
	}
	
	public double getX()
	{
		
		return w.getWorldState().getRobotX(color);
		
	}
	
	public double getY()
	{
		
		return w.getWorldState().getRobotY(color);
		
	}
	
	public double getDir()
	{
		
		return w.getWorldState().getRobotDir(color);
		
	}
	
	public double vecSize( double x, double y ) {
		return Math.sqrt( ( Math.pow( x, 2 ) + Math.pow( y, 2 ) ) );
	}
	
	public void close()
	{
		
		
		while (touchThread.isAlive())
		{
			try
			{
				touchThread.interrupt();
				touchThread.join();
			}
			catch (InterruptedException ie)
			{
				
			}
		}
		
		c.disconnect();
		
		
	}
	
	private static void print(String s)
	{
		System.out.println(s);
	}
	
	private static String readLine()
	{
		try 
		{
			return reader.readLine();
		} 
		catch (Exception e)
		{
			return "";
		}
	}
	
	private static String readLine(String cmd)
	{
		print(cmd);
		return readLine();
	}
	
}
