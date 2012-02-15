package comms.robot.connection;

import java.io.*;
import java.util.*;

class OutputStreamRunnable implements Runnable
{
	
	private OutputStream os;
	private Queue<Message> queue;
	private Object lock;
	
	public OutputStreamRunnable(OutputStream os, Object lock, Queue<Message> queue)
	{
		
		this.os = os;
		this.lock = lock;
		this.queue = queue;
		
	}
	
	public void run()
	{
		
		try
		{
			
			while(!Thread.interrupted())
			{
				
				byte[] byteBuffer;
				
				synchronized (lock)
				{
					
					if (queue.isEmpty())
					{
						continue;
					}
					
					byteBuffer = intToByteArray(queue.remove().getCommand());
					
				}
				
				os.write(byteBuffer);
				os.flush();
				
				synchronized (lock)
				{
					lock.wait();
				}
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private byte[] intToByteArray(int i)
	{
		return new byte[]{ (byte)(i >>> 24), (byte)(i >> 16 & 0xff), (byte)(i >> 8 & 0xff), (byte)(i & 0xff) };
	}
	
}
