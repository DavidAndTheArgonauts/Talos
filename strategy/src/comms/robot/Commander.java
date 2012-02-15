package comms.robot;

import comms.robot.connection.*;

/**
 * Provides full control of the robot and sensor values
*/
public class Commander implements RobotCallback
{
	
	/* constants */
	
	/**
	 * Fast speed mode
	*/
	public static final int SPEED_FAST = 1;
	/**
	 * Medium speed mode
	*/
	public static final int SPEED_MEDIUM = 2;
	/**
	 * Slow speed mode
	*/
	public static final int SPEED_SLOW = 3;
	
	/**
	 * Fast speed value
	*/
	private static final int FAST = 90;
	/**
	 * Medium speed value
	*/
	private static final int MEDIUM = 55;
	/**
	 * Slow speed value
	*/
	private static final int SLOW = 15;
	
	private int lWheelSpeed = 0, rWheelSpeed = 0, lWheelSetSpeed = 0, rWheelSetSpeed = 0;
	private boolean lSensorTouched = false, rSensorTouched = false;
	private int lTouchCount = 0, rTouchCount = 0;
	
	private Connection connection = new Connection();
	
	private boolean connected = false;
	private String host;
	private int port;
	
	public Commander()
	{
		
	}
	
	/**
	 * Create a commander and connect
	 * @param host The host computer the proxy is running on
	 * @param port The port to use
	*/
	public Commander(String host, int port)
	{
		
		connect(host,port);
		
	}
	
	/**
	 * Connect to a different host and port
	 * @param host The host computer the proxy is running on
	 * @param port The port to use
	*/
	public void connect(String host,int port)
	{
		
		connected = connection.connect(host,port);
		
		this.host = host;
		this.port = port;
		
		if (connected)
		{
			connection.subscribe(this);
		}
		
	}
	
	/**
	 * If connected, disconnect
	*/
	public void disconnect()
	{
		if (connected)
		{
			connection.disconnect();
			System.out.println("Disconnected");
		}
	}
	
	/**
	 * @return Returns connection state
	*/
	public boolean isConnected()
	{
		return connected;
	}
	
	/**
	 * Drives in a straight(ish) line
	 * @param mode The speed mode to use
	*/
	public void drive(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(Commander.FAST,Commander.FAST);
				break;
			case Commander.SPEED_MEDIUM:
				setSpeed(Commander.MEDIUM,Commander.MEDIUM);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(Commander.SLOW,Commander.SLOW);
				break;
		}
		
	}
	
	/**
	 * Stops the robot
	*/
	public void stop()
	{
		
		setSpeed(0,0);
		
	}
	
	/**
	 * Operates the kicker
	*/
	public void kick()
	{
		
		clearQueue();
		connection.queueCommand(new Message(Opcodes.KICK));
		
	}
	
	/**
	 * Turns to the left
	 * @param mode The speed mode to use
	*/
	public void turnLeft(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(0,Commander.FAST);
				break;
			case Commander.SPEED_MEDIUM:
				setSpeed(0,Commander.MEDIUM);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(0,Commander.SLOW);
				break;
		}
	
	}
	
	/**
	 * Turns to the right
	 * @param mode The speed mode to use
	*/
	public void turnRight(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(Commander.FAST,0);
				break;
			case Commander.SPEED_MEDIUM:
				setSpeed(Commander.MEDIUM,0);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(Commander.SLOW,0);
				break;
		}
		
	}
	
	/**
	 * Turns to the left on the spot
	 * @param mode The speed mode to use
	*/
	public void spinLeft(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(-Commander.FAST,Commander.FAST);
				break;
			case Commander.SPEED_MEDIUM:
				setSpeed(-Commander.MEDIUM,Commander.MEDIUM);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(-Commander.SLOW,Commander.SLOW);
				break;
		}
	
	}
	
	/**
	 * Turns to the right on the spot
	 * @param mode The speed mode to use
	*/
	public void spinRight(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(Commander.FAST,-Commander.FAST);
				break;
			case Commander.SPEED_MEDIUM:
				setSpeed(Commander.MEDIUM,-Commander.MEDIUM);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(Commander.SLOW,-Commander.SLOW);
				break;
		}
	
	}
	
	/**
	 * Set the speed of the wheels individually
	 * @param left The left wheel speed (percentage)
	 * @param right The right wheel speed (percentage)
	*/
	public void setSpeed(int left, int right)
	{
		
		// if we are sending the same wheel speed command
		if (left == lWheelSetSpeed && right == rWheelSetSpeed)
		{
			return;
		}
		
		lWheelSetSpeed = left;
		rWheelSetSpeed = right;
		
		clearQueue();
		connection.queueCommand(new Message(Opcodes.SET_SPEED,left,right));
		
	}
	
	
	private void clearQueue()
	{
		
		connection.clearQueue();
		
	}
	
	/**
	 * Use the robot callback mechanism to receive robot data
	 * @param rc A class implementing RobotCallback
	 * @see comms.robot.connection.RobotCallback
	*/
	public void subscribe(RobotCallback rc)
	{
		
		connection.subscribe(rc);
		
	}
	
	/**
	 * Use the robot callback mechanism to stop receiving robot data
	 * @param rc A class implementing RobotCallback which has already subscribed
	 * @see comms.robot.connection.RobotCallback
	*/
	public void unsubscribe(RobotCallback rc)
	{
		
		connection.unsubscribe(rc);
		
	}
	
	/**
	 * @return Whether the left touch sensor is pushed
	*/
	public boolean isLeftSensorPushed()
	{
		return lSensorTouched;
	}
	
	/**
	 * @return Whether the right touch sensor is pushed
	*/
	public boolean isRightSensorPushed()
	{
		return rSensorTouched;
	}
	
	/**
	 * @return The actual left wheel speed
	*/
	public int getLeftWheelSpeed()
	{
		return lWheelSpeed;
	}
	
	/**
	 * @return The actual right wheel speed
	*/
	public int getRightWheelSpeed()
	{
		return rWheelSpeed;
	}
	
	/**
	 * @return The number of times the left sensor has been touched
	*/
	public int getLeftTouchCount()
	{
		return lTouchCount;
	}
	
	/**
	 * @return The number of times the right sensor has been touched
	*/
	public int getRightTouchCount()
	{
		return rTouchCount;
	}
	
	/**
	 * Blocks until the queue is empty
	*/
	public void waitForQueueToEmpty()
	{
		connection.isQueueEmpty(true);
	}
	
	public void robotCallback(Message response)
	{
		
		switch (response.getOpcode())
		{
			case Opcodes.WHEEL_FEEDBACK:
				
				int[] speeds = response.getArguments(2);
				
				lWheelSpeed = speeds[0];
				rWheelSpeed = speeds[1];
				
				break;
				
			case Opcodes.SENSOR_TOUCHED:
			
				int[] states = response.getArguments(2);
				
				if (states[0] == 1)
				{
					lSensorTouched = true;
					lTouchCount++;
				}
				else
				{
					lSensorTouched = false;
				}
				
				if (states[1] == 1)
				{
					rSensorTouched = true;
					rTouchCount++;
				}
				else
				{
					rSensorTouched = false;
				}
				
				break;
		}
		
	}
	
}
