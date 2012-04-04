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
	
	private int leftMotorPower = 0, rightMotorPower = 0;
	private int leftVeloTarget = 0, rightVeloTarget = 0;
	private int leftVeloCommand = 0, rightVeloCommand= 0;
	private double leftSpeed = 0, rightSpeed = 0;
	private long nanoTime = 0;
	private long lastPrint = 0;

	private int leftNow, rightNow, leftPrev = -1, rightPrev = -1;
	
	
	/**
	* @param(connection): passes the connection from Connection class to receive and send messages
	*/
	public Driver(Connection conn)
	{
		this.connection = conn;
		connection.subscribe(this);

		// Thread to handle wheel feedback
		response = new Thread(new Runnable(){

		
		
			public void run(){
				// TachoCount is the number of degrees wheel has rotated
				Motor.B.resetTachoCount();
				Motor.C.resetTachoCount();
				Motor.B.regulateSpeed(false);
				Motor.C.regulateSpeed(false);
				
				//leftVeloTarget = 300;
				//rightVeloTarget = -300;
				

	
				while(!Thread.interrupted()){
					
					// Wait ~100ms
					try{
						Thread.sleep(5);
					}
					catch (Exception e)
					{
					}
					
					leftNow = Motor.B.getTachoCount();
					rightNow = Motor.C.getTachoCount();
					
					double timeDiff = ( System.nanoTime() - nanoTime ) / 1000000000f;

					
					if (leftPrev != -1 ) leftSpeed = (leftNow - leftPrev) / timeDiff;
					if (rightPrev != -1 ) rightSpeed = (rightNow - rightPrev) / timeDiff;


					leftVeloTarget = leftVeloCommand;
					rightVeloTarget = rightVeloCommand;

						//&& rightMotorPower > -95 || ( rightMotorPower < -95 && leftMotorPower > 95 )



					if (leftSpeed < leftVeloTarget ) leftMotorPower += 1;
					if (leftSpeed > leftVeloTarget ) leftMotorPower -= 1;
					
					if (rightSpeed < rightVeloTarget ) rightMotorPower += 1;
					if (rightSpeed > rightVeloTarget ) rightMotorPower -= 1;

					if (leftMotorPower > 100) leftMotorPower = 100;
					if (leftMotorPower < -100) leftMotorPower = -100;
					
					if (rightMotorPower > 100) rightMotorPower = 100;
					if (rightMotorPower < -100) rightMotorPower = -100;
					
					
					
					
					
					
					
					if (leftVeloCommand == 0) {
						Motor.B.stop();
						leftMotorPower = 0;
					}
					else {
						Motor.B.setPower(leftMotorPower);
						Motor.B.forward();
					}

					if (rightVeloCommand == 0) {
						Motor.C.stop();
						rightMotorPower = 0;
					}
					else {
						Motor.C.setPower(rightMotorPower);
						Motor.C.forward();
					}
					
									




					if ( ( System.nanoTime() - lastPrint ) / 1000000000f > 1 ) { 
						System.out.println( leftMotorPower + " " + rightMotorPower + " " + (int) leftSpeed + " " + (int) rightSpeed);
						lastPrint = System.nanoTime();
					}
					

					leftPrev = leftNow;
					rightPrev = rightNow;
	

					nanoTime = System.nanoTime();



					


					

					
										
				


/*
					if (System.currentTimeMillis() > lastSend + 100)
					{
						// Send message
						if (lSpeed != 0 || rSpeed != 0)
						{
							connection.queueMessage(new Message(Opcodes.WHEEL_FEEDBACK, -1 * (lTC - leftTachoCount), (rTC - rightTachoCount)));
						}
						
						lastSend = System.currentTimeMillis();
						
						leftTachoCount = lTC;
						rightTachoCount = rTC;
						
					}
*/
					
					
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

	
			leftVeloCommand = (int)(args[0]*12);
			rightVeloCommand = (int)(args[1]*12);



			




		/*	
			
			synchronized (lock)
			{
				
				// Get resolutions per second
				int leftSpeed = (int)(args[0]*0.01*900);
				int rightSpeed = (int)(args[1]*0.01*900);
				
				Motor.B.setSpeed(leftSpeed);
				Motor.C.setSpeed(rightSpeed);
				
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
			
*/
		}

	}

}
