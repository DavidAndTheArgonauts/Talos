package controller;

import lejos.nxt.*;

import connection.*;

import java.lang.*;

/*
	Code that operates the kicker motor
*/
public class Kicker implements ConnectionInterface
{
	
	private static long THREAD_TIMEOUT = 1000;	
		
	private Connection connection;
	
	private Thread kicker = null;
	private Object lock = new Object();
	
	private long lastStart = -1;
	
	public Kicker(Connection connection)
	{
		
		this.connection = connection;
		connection.subscribe(this);
		
	}
	
	// Acts upon receiving a command
	public synchronized void receiveCommand(Message msg)
	{
		
		// Checks message broadcasted is for this class
		if (msg.getOpcode() == Opcodes.KICK)
		{
			
			// Checks thread status
			if ((lastStart != -1 && System.currentTimeMillis() - lastStart < THREAD_TIMEOUT) && (kicker != null && kicker.isAlive()))
				return;
			
			kicker = new Thread() {
				
				public void run()
				{
					
//					Reset kicker to original position
					Motor.A.setSpeed(900);
					Motor.A.backward();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
					Motor.A.stop();
					Motor.A.setSpeed(900);
					Motor.A.forward();
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
					}
					// Reset kicker to original position
					Motor.A.setSpeed(50);
					Motor.A.backward();
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
					Motor.A.stop();
					
				}
				
			};
			
			kicker.start();
			lastStart = System.currentTimeMillis();
			
		}
		
	}
	
}
