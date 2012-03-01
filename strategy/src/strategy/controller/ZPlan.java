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
	private static final int VISION_PORT = 5500;

	private static final int YELLOW = 0;
	private static final int BLUE = 1;

	private Thread touchThread;
	private final Commander c;
	private World w = new World();
	private int color;
	private static boolean sensorActivate = false;

	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args)
	{

		ZPlan z = new ZPlan(BLUE);
		z.run();

	}

	public ZPlan(int color)
	{
		this.color = color;
		c = new Commander();
		do
		{

			String host = readLine("Enter hostname (localhost): ");

			if (host.equals(""))
				host = "localhost";
			c.connect(host,PORT);

		}
		while (!c.isConnected());
		
		print("Connected!");
		
		/***
		 *  Thread to listen to sensor when they are activated.
		 */
		Thread touchThread = new Thread()
		{

			public void run()
			{
				while (!Thread.interrupted())
				{
					if (c.isLeftSensorPushed() || c.isRightSensorPushed())
					{
						sensorActivate = true;
					} else {
						sensorActivate = false;
					}
					Thread.yield();
				}
			}

		};
		touchThread.start();

		w.listenForVision(VISION_PORT);

		// Wait for vision
		print("Waiting for vision");
		while (w.getWorldState() == null)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException ie)
			{
				print("Error: Waiting for vision.");
				System.exit(1);
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

			// If either sensors are touched, robot will stop for ~500ms
			if (sensorActivate){

				print("SENSOR ACTIVATE");
				c.stop();
				try {
					Thread.sleep(500);
				} 
				catch (Exception e)
				{
					print("Error: Sensor activated has produced an error.");
				}
				sensorActivate = false;

			} else {

				// Get robot position
				robotX = getX();
				robotY = getY();
				robotDir = getDir();

				// Direction from movement
				dirX = robotX - prevX;
				dirY = robotY - prevY;
				dirNorm = vecSize(dirX, dirY);
				dirAngle = Math.toDegrees( Math.atan2(dirX, dirY) );

				// If change of movement > 1, use direction from movement instead of vision data
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


				// If < -60 or > 60 then spin
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

					// Set motor speeds based on the direction between robot and ball
					if ( dirRoboBall < 0 ) {
						leftMotor = 1;
						rightMotor = 1 - Math.abs(dirRoboBall) / 60.;
					}	

					else {
						rightMotor = 1;
						leftMotor = 1 - Math.abs(dirRoboBall) / 60.;
					};


					// Normalize motor speed based on the distance between the robot and the ball
					if (dirBallNorm > 50) {
						speedDelta = 1;
					} else if (dirBallNorm > 20) {
						speedDelta = dirBallNorm / 50f;
					} else {
						speedDelta = 0;
						System.out.println("Kicker Activated");
						c.kick();
						c.waitForQueueToEmpty();
					}

				}

				motorFullSpeed = 100;

				motorDelta = 1;


				driveLeft = leftMotor * motorFullSpeed * speedDelta;
				driveRight = rightMotor * motorFullSpeed * speedDelta;

				/*
				if ( (driveLeft - prevLeft) > motorDelta ) {
					driveLeft = prevLeft + motorDelta;
				} else if ( (driveRight - prevRight) > motorDelta ) {
					driveRight = prevRight + motorDelta;
				} else if ( (driveLeft - prevLeft) < -motorDelta ) {
					driveLeft = prevLeft - motorDelta;
				} else if ( (driveRight - prevRight) < -motorDelta ) {
					driveRight = prevRight - motorDelta;
				}
				*/

				prevLeft = driveLeft;
				prevRight = driveRight;

				System.out.println("driveLeft: " + driveLeft);
				System.out.println("driveRight: " + driveRight);


				c.setSpeed( (int) Math.round( driveLeft ), (int) Math.round( driveRight ) );
				c.waitForQueueToEmpty();

				// run at 10 FPS, for the movement from robot function to work
				/*
				try{
					Thread.sleep(100);
				}
				catch (Exception e)
				{}
				 */

				prevX = robotX;
				prevY = robotY;
			}
		}

	}

	/***
	 * Get Talo's X coordinate.
	 * @return Talo's X coordinate.
	 */
	public double getX()
	{
		if (color == YELLOW)
		{
			return w.getWorldState().getYellowX();
		}
		else
		{
			return w.getWorldState().getBlueX();
		}
	}

	/***
	 * Get Talo's Y coordinate.
	 * @return Talo's Y coordinate.
	 */
	public double getY()
	{
		if (color == YELLOW)
		{
			return w.getWorldState().getYellowY();
		}
		else
		{
			return w.getWorldState().getBlueY();
		}
	}

	/***
	 * Get Talo's direction.
	 * @return Talo's direction.
	 */
	public double getDir()
	{
		if (color == YELLOW)
		{
			return w.getWorldState().getYellowDir();
		}
		else
		{
			return w.getWorldState().getBlueDir();
		}
	}

	/***
	 * Get vector magnitude.
	 * @param x X line vector.
	 * @param y Y line vector.
	 * @return vector magnitude.
	 */
	public double vecSize( double x, double y ) {
		return Math.sqrt( ( Math.pow( x, 2 ) + Math.pow( y, 2 ) ) );
	}

	/***
	 * Terminates sensor thread and disconnects the connection.
	 */
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

	/***
	 * Will's lazy printing string code.
	 * @param s String to print.
	 */
	private static void print(String s)
	{
		System.out.println(s);
	}
	
	/***
	 * Reads in a line from keyboard.
	 * @return String input from keyboard.
	 */
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

	/***
	 *  Prints string from command line.
	 * @param cmd String to print.
	 * @return Resets reader buffer.
	 */
	private static String readLine(String cmd)
	{
		print(cmd);
		return readLine();
	}

}
