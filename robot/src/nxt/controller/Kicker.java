package controller;

import lejos.nxt.*;

import connection.*;

import java.lang.*;

/*
	Code that operates the kicker motor
*/
public class Kicker implements ConnectionInterface
{
	
	private Connection connection;
	
	private Thread kicker = null;
	private Object lock = new Object();
	
	public Kicker(Connection connection)
	{
		
		this.connection = connection;
		connection.subscribe(this);
		
	}
	
	// Acts upon receiving a command
	public synchronized void receiveCommand(Message msg)
	{
		
		// Checks thread status
		if (kicker != null && kicker.isAlive())
			return;
		
		// Checks message broadcasted is for this class
		if (msg.getOpcode() == Opcodes.KICK)
		{
			
			kicker = new Thread() {
				
				public void run()
				{
					
					Motor.A.setSpeed(900);
					Motor.A.rotate(1, true);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					// Reset kicker to original position
					Motor.A.setSpeed(50);
					Motor.A.rotate(-1, true);
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					Motor.A.stop();
					
				}
				
			};
			
			kicker.start();
			
		}
		
	}
	
}
