package comms.robot.connection;

import java.io.*;
import java.util.*;

class InputStreamRunnable implements Runnable
{
	
	private InputStream is;
	private Object lock;
	private ArrayList<RobotCallback> subscribers;
	private int packetSize;
	
	public InputStreamRunnable(InputStream is, Object lock, ArrayList<RobotCallback> subscribers, int packetSize)
	{
		
		this.is = is;
		this.lock = lock;
		this.subscribers = subscribers;
		this.packetSize = packetSize;
		
	}
	
	public void run()
	{
		
		try
		{
			while(!Thread.interrupted())
			{
			
				// read data
				byte[] byteBuffer = new byte[packetSize];
				
				if (is.available() < packetSize)
				{
					continue;
				}
				
				is.read(byteBuffer);
				
				// update subscribers
				updateSubscribers(new Message(byteArrayToInt(byteBuffer)));
			
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void updateSubscribers(Message response)
	{
		
		synchronized (lock)
		{
			
			for (int i = 0; i < subscribers.size(); i++)
			{
				subscribers.get(i).robotCallback(response);
			}
			
		}
		
	}
	
	public static int byteArrayToInt(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}
	
}
