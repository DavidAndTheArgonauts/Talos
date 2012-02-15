package simpleproxy.proxy;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;

import lejos.pc.comm.*;

class ConnectionManager implements Runnable
{
	
	private boolean bluetoothConn;
	
	private Queue<byte[]> in, out;
	
	private InputStream is;
	private OutputStream os;
	
	private Thread inputThread, outputThread;
	
	private NXTComm nc;
	private NXTInfo ni;
	private Socket s;
	
	public ConnectionManager(Queue<byte[]> in, Queue<byte[]> out, boolean bluetoothConn)
	{
		
		this.in = in;
		this.out = out;
		this.bluetoothConn = bluetoothConn;
		
	}
	
	public void run()
	{
		
		while(true)
		{
		
			try
			{
				// wait for new connection
				if (bluetoothConn)
				{
					
					nc = waitForRobot();
						
					is = nc.getInputStream();
					os = nc.getOutputStream();
			
				}
				else
				{
			
					s = waitForPC();
			
					is = s.getInputStream();
					os = s.getOutputStream();
			
				}
			}
			catch (Exception e)
			{
				System.err.println("Couldn't get streams");
				continue;
			}
			
			// restart threads
			inputThread = new Thread(new IncomingPacketManager(in,is));
			inputThread.start();
			
			outputThread = new Thread(new OutgoingPacketManager(out,os));
			outputThread.start();
			
			while (inputThread.isAlive() && outputThread.isAlive())
			{
				try
				{
					Thread.sleep(200);
				}
				catch (Exception e) {}
			}
			
			closeThread(inputThread);
			closeThread(outputThread);
			
			try
			{
				if (bluetoothConn)
				{
					nc.close();
				}
				else
				{
					s.close();
				}
			}
			catch (Exception e) 
			{
				System.err.println("Couldn't close connection");
			}
			
		}
		
	}
	
	private void closeThread(Thread t)
	{
		
		if (!t.isAlive())
			return;
		
		try
		{
			
			t.interrupt();
			t.join();
			
		}
		catch (Exception e)
		{
		
			e.printStackTrace();
			
		}
		
	}
	
	private NXTComm waitForRobot()
	{
		
		try
		{
			
			ni = new NXTInfo(NXTCommFactory.BLUETOOTH, Proxy.ROBOT_NAME, Proxy.ROBOT_ADDR);
			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			boolean connected = false;
			
			System.out.println("Waiting for robot [" + Proxy.ROBOT_NAME + ":" + Proxy.ROBOT_ADDR + "]...");
			
			while(!connected)
			{
				
				try
				{
					
					connected = nxtComm.open(ni);
					
					Thread.sleep(Proxy.RETRY_WAIT);
					
				}
				catch (Exception e) { }
				
			}
			
			System.out.println("Robot connected!");
			
			return nxtComm;
			
		}
		catch(NXTCommException e)
		{
			
			System.err.println("Unable to start NXT connection");
			e.printStackTrace();
			System.exit(1);
			
		}
		
		return null;
		
	}
	
	private Socket waitForPC()
	{
		
		while (true)
		{
			
			try
			{
			
				ServerSocket serverSocket = new ServerSocket(Proxy.PC_PORT);
		
				System.out.println("Waiting for PC [0.0.0.0:" + Proxy.PC_PORT+  "]...");
		
				Socket clientSocket = serverSocket.accept();
			
				System.out.println("PC connected!");
				
				serverSocket.close();	
				
				return clientSocket;
				
			}
			catch (Exception e)
			{
				System.err.println("Retrying...");
				e.printStackTrace();
			}
			
			try
			{
				Thread.sleep(Proxy.RETRY_WAIT);
			}
			catch (Exception e) {}
			
		}
		
	}
	
}
