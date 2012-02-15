package simpleproxy.proxy;

import java.util.*;
import java.io.*;
import java.lang.*;

class IncomingPacketManager implements Runnable
{
	
	private Queue<byte[]> queue;
	private InputStream is;
	
	public IncomingPacketManager(Queue<byte[]> queue, InputStream is)
	{
		this.queue = queue;
		this.is = is;
	}
	
	public void run()
	{
		
		//System.out.println("  IN >> OPEN");
		
		try
		{
			while (!Thread.interrupted())
			{
			
				byte[] byteBuffer = new byte[Proxy.PACKET_SIZE];
				
				/*
				if (is.available() < Proxy.PACKET_SIZE)
				{
					continue;
				}
				*/
				
				if (is.read(byteBuffer) == -1)
					break;
				
				queue.offer(byteBuffer);
				
				//System.out.println("RECV >> " + byteBuffer);
				
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			//System.out.println("  IN >> CLOSED");
			try
			{
				is.close();
			}
			catch (Exception e) {}
		}
		
	}
	
}
