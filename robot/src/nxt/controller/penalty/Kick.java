package controller.penalty;

import lejos.nxt.*;

import java.util.*;

public class Kick extends Thread
{
	
	private int leftSpeed = 100;
	private int rightSpeed = -100;
	private int turnTime = 800;
	private int random;
	private int turnDirection;

	public Kick()
	{
		Random r = new Random();
		random = r.nextInt(2);
		
		if (random % 2 == 1)
		{
			// Turn right
			turnDirection = 1;
		}
		else
		{
			// Turn left
			turnDirection = -1;
		}
	}
	
	public void run()
	{
			// This never allows straight shooting, always at one of the corners of the goals
			//if (random != 0)
			//{
				Motor.B.setSpeed(leftSpeed*turnDirection);
				Motor.C.setSpeed(rightSpeed*turnDirection);
			
				// Check if motors are meant to go forwards or backwards
				if (leftSpeed*turnDirection < 0){
					Motor.B.forward();
				} else {
					Motor.B.backward();
				}

				if (rightSpeed*turnDirection < 0){
					Motor.C.forward();
				} else {
					Motor.C.backward();
				}
				try
				{
					Thread.sleep(turnTime);
				}
				catch (InterruptedException e)
				{
				}
			//}

			Motor.B.stop();
			Motor.C.stop();

			
			// Kick
			Motor.A.setSpeed(900);
			Motor.A.rotate(1, true);
			try {
				Thread.sleep(300);
			} 
			catch (InterruptedException e) 
			{
			}
			// Reset kicker to original position
			Motor.A.setSpeed(50);
			Motor.A.rotate(-1, true);
			try {
				Thread.sleep(300);
			} 
			catch (InterruptedException e) 
			{
			}
			Motor.A.stop();
			
	}

}

