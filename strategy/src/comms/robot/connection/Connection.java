package comms.robot.connection;

import java.io.*;
import java.net.*;
import java.util.*;

public class Connection implements RobotCallback
{
	
	// constants
	private final int PACKET_SIZE = 4; // sizeof(int)
	private final int ROBOT_COMMAND_COMPLETE = 0x01;
	
	// connection details
	private Socket clientSocket = null;
	private InputStream is;
	private OutputStream os;
	private boolean connected = false;
	
	private Thread incomingThread = null, outgoingThread = null;
	
	// queue for sending commands
	private Queue<Message> queue = new LinkedList<Message>();
	private Object queueLock = new Object();
	
	// subscribers for receiving responses
	private ArrayList<RobotCallback> subscribers = new ArrayList<RobotCallback>();
	private Object subscriberLock = new Object();
	
	public boolean connect(String host, int port)
	{
		
		// if we're already connected don't try again
		if (connected && clientSocket != null && clientSocket.isConnected())
		{
			// make sure the streams are up to date
			return getStreams();
		}
		
		// first connect
		try
		{
			clientSocket = new Socket(host,port);
		}
		catch (Exception e)
		{
			System.err.println("Could not connected to proxy ["+host+":"+port+"], is proxy running?");
			return false;
		}
		
		// then get streams
		if (!getStreams())
		{
			return false;
		}
		
		// spawn threads to handle queue
		spawnThreads();
		
		subscribe(this);
		
		// update connected variable and return
		connected = true;
		return true;
		
	}
	
	protected void spawnThreads()
	{
		
		// spawn new threads
		incomingThread = new Thread(new InputStreamRunnable(is, subscriberLock, subscribers, PACKET_SIZE));
		outgoingThread = new Thread(new OutputStreamRunnable(os, queueLock, queue));
		
		// start threads
		incomingThread.start();
		outgoingThread.start();
		
	}
	
	private boolean getStreams()
	{
		try
		{
			is = clientSocket.getInputStream();
			os = clientSocket.getOutputStream();
		}
		catch (IOException e)
		{
			System.err.println("Unable to get streams");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void disconnect()
	{
		
		if (!connected)
		{
			System.err.println("Not connected");
			return;
		}
	
		try
		{
			
			incomingThread.interrupt();
			outgoingThread.interrupt();
			
			outgoingThread.join();
			incomingThread.join();
			
			is.close();
			os.close();
			
			clientSocket.close();
			
		}
		catch(Exception e)
		{
			System.err.println("Unable to close connection");
			e.printStackTrace();
		}
	}
	
	public boolean queueCommand(Message cmd)
	{
		synchronized (queueLock)
		{
			return queue.offer(cmd);
		}
	}
	
	public boolean isQueueEmpty(boolean wait)
	{
		
		if (wait)
		{
		
			boolean isEmpty = false;
			
			synchronized (queueLock)
			{
				isEmpty = queue.isEmpty();
			}
			
			if (isEmpty)
			{
				return true;
			}
			else
			{
				
				while (!isEmpty)
				{
					
					try
					{
						Thread.sleep(1);
					}
					catch (InterruptedException ie) {}
					
					synchronized (queueLock)
					{
						isEmpty = queue.isEmpty();
					}
					
				}
				
				return true;
				
			}
			
			
		}
		else
		{
			
			synchronized (queueLock)
			{
				return queue.isEmpty();
			}
			
		}
		
	}
	
	public void clearQueue()
	{
		synchronized (queueLock)
		{
			queue.clear();
		}
	}
	
	public void subscribe(RobotCallback rc)
	{
		synchronized (subscriberLock)
		{
			subscribers.add(rc);
		}
	}
	
	public void unsubscribe(RobotCallback rc)
	{
		synchronized (subscriberLock)
		{
			subscribers.remove(rc);
		}
	}
	
	public void robotCallback(Message response)
	{
		if (response.getOpcode() == Opcodes.COMMAND_COMPLETE)
		{
			synchronized (queueLock)
			{
				queueLock.notify();
			}
		}
		
	}
	
}
