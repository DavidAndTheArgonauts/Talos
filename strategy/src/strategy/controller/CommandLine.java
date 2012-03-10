package strategy.controller;

import java.io.*;

import comms.robot.*;

/**
 * Entrypoint to give direct access to robot through command line style interface 
*/
public class CommandLine
{
	
	/**
	 * The port to connect to the proxy on
	*/
	private static final int PORT = 9899;
	
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args)
	{
		
		final Commander c = new Commander();
		
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
		
		while (true)
		{
			
			String cmd = readLine();
			
			if (cmd.equals("stop")) c.stop();
			else if (cmd.equals("kick")) c.kick();
			else if (cmd.equals("drive fast")) c.drive(Commander.SPEED_FAST);
			else if (cmd.equals("drive medium")) c.drive(Commander.SPEED_MEDIUM);
			else if (cmd.equals("drive slow")) c.drive(Commander.SPEED_SLOW); 
			else if (cmd.equals("spin left")) c.spinLeft(Commander.SPEED_MEDIUM);
			else if (cmd.equals("spin right")) c.spinRight(Commander.SPEED_MEDIUM);
			else if (cmd.equals("turn left")) c.turnLeft(Commander.SPEED_MEDIUM);
			else if (cmd.equals("turn right")) c.turnRight(Commander.SPEED_MEDIUM);
			else if (cmd.equals("penalty kick")) c.penaltyKick();
			else if (cmd.equals("penalty defend")) c.penaltyDefend();
			else if (cmd.equals("quit")) 
			{
				c.waitForQueueToEmpty();
				break;
			}
			
		}
		
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
