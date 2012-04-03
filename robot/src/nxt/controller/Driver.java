package controller;

import lejos.nxt.*;

import connection.*;

import java.lang.*;

/*
	Code that operates the driver motor
*/
public class Driver implements ConnectionInterface
{

	
	private final Connection connection;

	private final Object lock = new Object();

	private Thread response;
	
	public static int lSpeed = 0, rSpeed = 0;
	public static long lTachoSet = 0, rTachoSet = 0;
	public static long setTime = 0;
	
	/**
	* @param(connection): passes the connection from Connection class to receive and send messages
	*/
	public Driver(Connection conn)
	{
		this.connection = conn;
		connection.subscribe(this);

		// Thread to handle wheel feedback
		response = new Thread(new Runnable(){

			// Keeps track of current time
			//private long timer;
			private int leftTachoCount = 0, rightTachoCount = 0;
			private int lSpeedDelta = 0, rSpeedDelta = 0;
			
			public void run(){
				// TachoCount is the number of degrees wheel has rotated
				Motor.B.resetTachoCount();
				Motor.C.resetTachoCount();
				//timer = System.currentTimeMillis();
				while(!Thread.interrupted()){
					// Wait ~100ms
					try{
						Thread.sleep(10);
					}
					catch (Exception e)
					{
					}
					// Update counters
					int lTC = Motor.B.getTachoCount();
					int rTC = Motor.C.getTachoCount();

					// Send message
					if (lSpeed != 0 || rSpeed != 0)
					{
						connection.queueMessage(new Message(Opcodes.WHEEL_FEEDBACK, -1 * (lTC - leftTachoCount), -1 * (rTC - rightTachoCount)));
					}
					
					long lTachoSet, rTachoSet, setTime;
					int lSpeed, rSpeed;
					synchronized (lock)
					{
						lTachoSet = Driver.lTachoSet;
						rTachoSet = Driver.rTachoSet;
						setTime = Driver.setTime;
						lSpeed = Driver.lSpeed;
						rSpeed = Driver.rSpeed;
					
					
						if (lSpeed != 0 || rSpeed != 0)
						{
					
							//System.out.println("==");
					
							long now = System.currentTimeMillis();
							double diff = (now - setTime) / 1000d;
							
							if (diff > 0.05)
							{
								
								//System.out.println(diff);
								
								int maxLSpeed = (int)Math.min(lSpeed, Battery.getVoltage() * 95);
								int maxRSpeed = (int)Math.max(rSpeed, -Battery.getVoltage() * 95); // change here
						
								long lExpectedCount = (long)(maxLSpeed * diff + lTachoSet);
								long rExpectedCount = (long)(maxRSpeed * diff + rTachoSet);
						
						
								//System.out.println("--[" + lExpectedCount);
								//System.out.println("++[" + lTC);
								
								
								lSpeedDelta = (int)((lExpectedCount - lTC))*10;
								rSpeedDelta = (int)((rExpectedCount - rTC))*10;
						
								System.out.println("[" + lSpeedDelta + "," + rSpeedDelta + "]");
				
				
								int lSetSpeed = lSpeed + lSpeedDelta;
								int rSetSpeed = rSpeed + rSpeedDelta;
								
								System.out.println("L[" + lSetSpeed + "]");
								System.out.println("R[" + rSetSpeed + "]");
								
								Motor.B.setSpeed(lSetSpeed);
								Motor.C.setSpeed(rSetSpeed);
							
						
								
								// Check if motors are meant to go forwards or backwards
								if (lSetSpeed > 0){
									Motor.B.forward();
								} else {
									Motor.B.backward();
								}
						
								if (rSetSpeed > 0){
									Motor.C.forward();
								} else {
									Motor.C.backward();
								}
								
							}
						
						}
						else
						{
						
							Motor.B.stop();
							Motor.C.stop();
						
						}
					
					}
					
					leftTachoCount = lTC;
					rightTachoCount = rTC;
					
				}
			}
		});
		// Start thread
		response.start();
	}

	// Acts upon receiving a command
	public void receiveCommand(Message msg)
	{

		// Checks message broadcasted is for this class
		if (msg.getOpcode() == Opcodes.SET_SPEED)
		{
			// Get wheel percentages
			int[] args = msg.getArguments(2);

			
			
			synchronized (lock)
			{
				
				// Get resolutions per second
				int leftSpeed = (int)(args[0]*0.01*900);
				int rightSpeed = (int)(args[1]*0.01*900);
				Motor.B.setSpeed(leftSpeed);
				Motor.C.setSpeed(rightSpeed);
				
				lTachoSet = Motor.B.getTachoCount();
				rTachoSet = Motor.C.getTachoCount();
				setTime = System.currentTimeMillis();
				lSpeed = leftSpeed;
				rSpeed = rightSpeed;
				
				
				// Check if motors are meant to go forwards or backwards
				if (leftSpeed > 0){
					Motor.B.forward();
				} else {
					Motor.B.backward();
				}

				if (rightSpeed > 0){
					Motor.C.forward();
				} else {
					Motor.C.backward();
				}
				
			}
			

		}

	}

}
