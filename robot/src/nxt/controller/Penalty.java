package controller;

import lejos.nxt.*;

import connection.*;

import controller.penalty.*;

import java.lang.*;

/*
	Code that operates the penalty strategy
*/
public class Penalty implements ConnectionInterface
{

	private final Connection connection;

	private Thread penalty = null;
	
	/**
	* @param(connection): passes the connection from Connection class to receive and send messages
	*/
	public Penalty(Connection conn)
	{
		this.connection = conn;
		connection.subscribe(this);
	}

	// Acts upon receiving a command
	public synchronized void receiveCommand(Message msg)
	{
		
		stopThread();

		// Checks message broadcasted is for this class
		switch (msg.getOpcode())
		{
		case Opcodes.PENALTY_KICK:
			penalty = new Kick();
			penalty.start();
			break;
		case Opcodes.PENALTY_DEFENSE:
			penalty = new Defend();
			penalty.start();
			break;
		}
		

	}

	// Checks thread status
	public void stopThread()
	{
		if (penalty == null || !penalty.isAlive())
		{
			return;
		}
		
		
		penalty.interrupt();

		while(penalty.isAlive())
		{
			try
			{
				penalty.join();
			}
			catch (Exception e)
			{
			}
		}
		
	}	
		

}
