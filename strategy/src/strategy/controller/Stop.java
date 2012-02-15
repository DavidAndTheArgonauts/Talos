package strategy.controller;

import java.io.*;

import comms.robot.*;

public class Stop
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
		
		c.stop();
		
		System.exit(0);
		
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
