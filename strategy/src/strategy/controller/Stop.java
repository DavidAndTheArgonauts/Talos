package strategy.controller;

import java.io.*;

import comms.robot.*;

/**
 * Entrypoint to stop robot's wheels
*/
public class Stop
{
	
	/**
	 * The port to connect to the proxy on
	*/
	private static final int PORT = 9899;
	
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	
	
	public static void main(String[] args)
	{
		
		Commander c = new Commander();
		
		do
		{
			
			String host = readLine("Enter hostname (localhost): ");
			
			if (host.equals(""))
				host = "localhost";
			
			c.connect(host,PORT);
			
		}
		while (!c.isConnected());
		
		print("Connected!");
		
		c.stop();
		
		c.waitForQueueToEmpty();
		
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
