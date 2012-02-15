package simpleproxy.proxy;

import java.util.*;
import java.io.*;
import java.lang.*;

class OutgoingPacketManager implements Runnable
{
	
	private Queue<byte[]> queue;
	private OutputStream os;
	private int packetSize;
	
	public OutgoingPacketManager(Queue<byte[]> queue, OutputStream os)
	{
		this.queue = queue;
		this.os = os;
		this.packetSize = packetSize;
	}
	
	public void run()
	{
		//System.out.println(" OUT >> OPEN");
		try
		{
			while (!Thread.interrupted())
			{
				
				byte[] byteBuffer = queue.poll();
				
				if (byteBuffer == null)
				{
					continue;
				}
				
				os.write(byteBuffer);
				os.flush();
				
				//System.out.println("SENT >> " + byteBuffer);
				
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			//System.out.println(" OUT >> CLOSED");
			try
			{
				os.close();
			}
			catch (Exception e) {}
		}
		
	}
	
}
