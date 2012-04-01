package controller;

import lejos.nxt.*;

import connection.*;

import java.lang.*;

/*
	Code that operates the driver motor
*/
public class Driver implements ConnectionInterface
{

	// Parameter variables for wheel speeds [left, right]
	public static int[] args;
 
	private final Connection connection;

	private Object lock = new Object();

	private Thread response;
	
	private int lSpeed = 0, rSpeed = 0;
	
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
			args = msg.getArguments(2);

			// Get resolutions per second
			int leftSpeed = (int)(Driver.args[0]*0.01*900);
			int rightSpeed = (int)(Driver.args[1]*0.01*900);
			Motor.B.setSpeed(leftSpeed);
			Motor.C.setSpeed(rightSpeed);
			
			lSpeed = leftSpeed;
			rSpeed = rightSpeed;
			
			// Check if motors are meant to go forwards or backwards
			if (leftSpeed < 0){
				Motor.B.forward();
			} else {
				Motor.B.backward();
			}

			if (rightSpeed < 0){
				Motor.C.forward();
			} else {
				Motor.C.backward();
			}

		}

	}

}
