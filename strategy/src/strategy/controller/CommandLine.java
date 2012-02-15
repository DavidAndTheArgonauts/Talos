package strategy.controller;

import java.io.*;

import comms.robot.*;

public class CommandLine
{
	
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
			else if (cmd.equals("quit")) System.exit(0);
			
		}
		
	}
	
	public static void print(String s)
	{
		System.out.println(s);
	}
	
	public static String readLine()
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
	
	public static String readLine(String cmd)
	{
		print(cmd);
		return readLine();
	}
	
}
