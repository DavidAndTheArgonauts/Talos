package connection;

import lejos.nxt.LCD;
import lejos.nxt.comm.*;

import java.io.*;
import java.util.*;
import java.lang.*;

/*
	Code that runs on NXT Brick
	Manages the connection to PC end
*/
public class Connection
{
	
	// Constants
	public final int PACKET_SIZE = 4;
	
	// Queue
	private Object queueLock = new Object();
	private Queue queue = new Queue();
	
	// Subscribers
	private Object subscriberLock = new Object();
	private ArrayList<ConnectionInterface> subscribers = new ArrayList<ConnectionInterface>();
	
	// Threads
	private Thread outgoingStream;
	
	// Streams
	private InputStream is;
	
	// Wait for connection
	public void connect()
	{
		
		// Empty queue of commands stored on the brick previously
		clearQueue();
		
		NXTConnection connection = null;
		
		try
		{
			// Wait for connection
			connection = Bluetooth.waitForConnection();
			
			// Start send command thread
			outgoingStream = new Thread(new OutputStreamRunnable(connection.openOutputStream(),queue,queueLock));
			outgoingStream.start();
			
			LCD.drawString("Connected",0,0);
			
			is = connection.openInputStream();
			
			Message msg = null;
			
			do
			{
				
				// Read commands
				byte[] byteBuffer = new byte[PACKET_SIZE];
				
				// Disconnect opcode
				if (is.read(byteBuffer) == -1)
					break;
				
				int response = byteArrayToInt(byteBuffer);
				msg = new Message(response);
				
				// Reply that we have the command
				queueMessage(new Message(Opcodes.COMMAND_COMPLETE));
				
				// Send message to subscribers
				distributeMessage(msg);
				
			} while (msg == null || msg.getOpcode() != Opcodes.CLOSE);
			
		}
		catch (Exception e)
		{
			// Show exception message
			LCD.drawString("EXCEPTION",0,6);
		}
		finally
		{
			// Cleanup of streams
			
			// If we managed to connect
			if (connection != null)
			{
			
				// Send clean up message to controllers
				Message msg = new Message(Opcodes.CLOSE);
				distributeMessage(msg);
				
				try
				{
	
					// Close input stream
					is.close();
		
					// Close connection
					connection.close();
				
				}
				catch (Exception e)
				{
					// Handle exception
					// FURTHER WORK: Logging exceptions
				}
			
			}
			
			try
			{
				
				// Wait for outgoingStream thread to terminate
				if (outgoingStream.isAlive())
				{
					outgoingStream.interrupt();
					outgoingStream.join();
				}
				
			}
			catch (Exception e) {}
			
		}
		
	}
	
	// Sends a message to all subscribers
	private void distributeMessage(Message m)
	{
		
		synchronized (subscriberLock)
		{
			
			for (int i = 0; i < subscribers.size(); i++)
			{
				subscribers.get(i).receiveCommand(m);
			}
			
		}	
		
	}
	
	// Converts bytes in an array to an integer (byte commands converted to opcode)
	private int byteArrayToInt(byte[] b) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i] & 0x000000FF) << shift;
		}
		return value;
	}
	
	private void clearQueue()
	{
		synchronized (queueLock)
		{
			queue.clear();
		}
	}	
	
	// Queue message to PC END
	public void queueMessage(Message msg)
	{
		synchronized (queueLock)
		{
			queue.push(msg);
		}
	}
	
	// Subscribe a class for pc commands
	public void subscribe(ConnectionInterface ci)
	{
		synchronized(subscriberLock)
		{
			subscribers.add(ci);
		}
	}
	
	// Unsubscribe a class for pc commands
	public void unsubscribe(ConnectionInterface ci)
	{
		synchronized(subscriberLock)
		{
			subscribers.remove(ci);
		}
	}
	
}
