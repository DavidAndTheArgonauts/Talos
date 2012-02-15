package connection;

import java.lang.*;
import java.io.*;
import java.util.*;

/*
	Code that sends messages from NXT to PC End
*/
class OutputStreamRunnable implements Runnable
{
	
	private OutputStream os;
	private Queue queue;
	private Object queueLock;
	
	public OutputStreamRunnable(OutputStream os, Queue queue, Object queueLock)
	{
		
		this.os = os;
		this.queue = queue;
		this.queueLock = queueLock;
		
	}
	
	public void run()
	{
			
		try
		{
			while (!Thread.interrupted())
			{
				
				// Try and send all messages in queue
				Message msg;
				while ((msg = getNextMessage()) != null)
				{
					
					// Get message
					byte[] byteBuffer = intToByteArray(msg.getCommand());
					
					// Try and send
					os.write(byteBuffer);
					os.flush();
					
				}
				// Allow other threads to run
				Thread.yield(); 
				
			}
			
		}
		catch (Exception e)
		{
				// Handle exception
				// FURTHER WORK: Logging exceptions
		}
		
	}

	
	
	private Message getNextMessage()
	{
		synchronized (queueLock)
		{
			if (queue.empty())
			{
				return null;
			}
			
			try
			{
				return (Message)queue.pop();
			}
			// This should never happen
			catch (EmptyQueueException e)
			{
				return null;
			}
		}
	}
	// Convert integer information to a byte array for the queue
	private byte[] intToByteArray(int i)
	{
		return new byte[]{ (byte)(i >>> 24), (byte)(i >> 16 & 0xff), (byte)(i >> 8 & 0xff), (byte)(i & 0xff) };
	}
	
}
