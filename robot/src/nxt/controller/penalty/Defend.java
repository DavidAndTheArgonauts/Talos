package controller.penalty;

import lejos.nxt.*;

import java.util.*;

public class Defend extends Thread
{
	
	private int wheelSpeed = 900;
	private int driveTime = 700;
	private int random;
	private int direction;

	public Defend()
	{
		Random r = new Random();
		random = r.nextInt(4);

		// Choose a direction (forwards or backwards) to drive
		if (random % 2 == 1)
		{
			direction = 1;
		}
		else
		{
			direction = -1;
		}
	}
	
	public void run()
	{	

		// This chooses a side to drive into, never sits still 
		Motor.B.setSpeed(wheelSpeed*direction);
		Motor.C.setSpeed(wheelSpeed*direction);
	
		// Check if motors are meant to go forwards or backwards
		if (wheelSpeed*direction < 0){
			Motor.B.forward();
			Motor.C.forward();
		} else {
			Motor.B.backward();
			Motor.C.backward();
		}

		try
		{
			Thread.sleep(driveTime);
		}
		catch (InterruptedException e)
		{
		}

		Motor.B.stop();
		Motor.C.stop();
		
}

}

