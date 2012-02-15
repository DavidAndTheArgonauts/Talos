package comms.robot;

import comms.robot.connection.*;

public class Commander
{
	
	/* constants */
	public static final int SPEED_FAST = 1;
	public static final int SPEED_MEDIAN = 2;
	public static final int SPEED_SLOW = 3;
	
	private static final int FAST = 90;
	private static final int MEDIAN = 55;
	private static final int SLOW = 15;
	
	private Connection connection = new Connection();
	
	private boolean connected = false;
	private String host;
	private int port;
	
	public Commander()
	{
		
	}
	
	public Commander(String host, int port)
	{
		
		connect(host,port);
		
	}
	
	public void reconnect()
	{
		
		connected = connection.connect(host,port);
		
	}
	
	public void connect(String host,int port)
	{
		
		connected = connection.connect(host,port);
		
		this.host = host;
		this.port = port;
		
	}
	
	public boolean isConnected()
	{
		return connected;
	}
	
	public void drive(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(Commander.FAST,Commander.FAST);
				break;
			case Commander.SPEED_MEDIAN:
				setSpeed(Commander.MEDIAN,Commander.MEDIAN);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(Commander.SLOW,Commander.SLOW);
				break;
		}
		
	}
	
	public void stop()
	{
		
		clearQueue();
		connection.queueCommand(new Message(Opcodes.SET_SPEED,0,0));
		
	}
	
	public void kick()
	{
		
		clearQueue();
		connection.queueCommand(new Message(Opcodes.KICK));
		
	}
	
	public void turnLeft(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(0,Commander.FAST);
				break;
			case Commander.SPEED_MEDIAN:
				setSpeed(0,Commander.MEDIAN);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(0,Commander.SLOW);
				break;
		}
	
	}
	
	public void turnRight(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(Commander.FAST,0);
				break;
			case Commander.SPEED_MEDIAN:
				setSpeed(Commander.MEDIAN,0);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(Commander.SLOW,0);
				break;
		}
		
	}
	
	public void spinLeft(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(-Commander.FAST,Commander.FAST);
				break;
			case Commander.SPEED_MEDIAN:
				setSpeed(-Commander.MEDIAN,Commander.MEDIAN);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(-Commander.SLOW,Commander.SLOW);
				break;
		}
	
	}
	
	public void spinRight(int mode)
	{
		
		switch (mode)
		{
			case Commander.SPEED_FAST:
				setSpeed(Commander.FAST,-Commander.FAST);
				break;
			case Commander.SPEED_MEDIAN:
				setSpeed(Commander.MEDIAN,-Commander.MEDIAN);
				break;
			case Commander.SPEED_SLOW:
				setSpeed(Commander.SLOW,-Commander.SLOW);
				break;
		}
	
	}
	
	public void setSpeed(int left, int right)
	{
		
		clearQueue();
		connection.queueCommand(new Message(Opcodes.SET_SPEED,left,right));
		
	}
	
	private void clearQueue()
	{
		
		connection.clearQueue();
		
	}
	
	public void subscribe(RobotCallback rc)
	{
		
		connection.subscribe(rc);
		
	}
	
	public void unsubscribe(RobotCallback rc)
	{
		
		connection.unsubscribe(rc);
		
	}
	
}
