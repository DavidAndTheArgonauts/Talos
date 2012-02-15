package comms.robot.connection;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Manages a connection to the robot
*/
public class Connection implements RobotCallback
{
	
	// constants
	/**
	 * The size of opcodes to send and receive (in bytes)
	*/
	private final int PACKET_SIZE = 4; // sizeof(int)
	
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
	
	/**
	 * Connect to the proxy
	 * @param host The host the proxy is running on
	 * @param port The port the proxy is listening on
	 * @return Whether the connection is established
	*/
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
	
	/**
	 * Disconnect from the proxy
	*/
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
	
	/**
	 * Queue a command
	 * @return Whether the command was successfully queued
	*/
	public boolean queueCommand(Message cmd)
	{
		synchronized (queueLock)
		{
			return queue.offer(cmd);
		}
	}
	
	/**
	 * Checks the command queue empty state
	 * @param wait If wait is true, this method blocks until the queue is empty
	 * @return Whether the command queue is empty
	*/
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
	
	/**
	 * Empty the command queue
	*/
	public void clearQueue()
	{
		synchronized (queueLock)
		{
			queue.clear();
		}
	}
	
	/**
	 * Use the robot callback mechanism to receive robot data
	 * @param rc A class implementing RobotCallback
	 * @see comms.robot.connection.RobotCallback
	*/
	public void subscribe(RobotCallback rc)
	{
		synchronized (subscriberLock)
		{
			subscribers.add(rc);
		}
	}
	
	/**
	 * Use the robot callback mechanism to stop receiving robot data
	 * @param rc A class implementing RobotCallback which has already subscribed
	 * @see comms.robot.connection.RobotCallback
	*/
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
