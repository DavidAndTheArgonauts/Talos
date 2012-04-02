package sensor;

import lejos.nxt.*;

import connection.*;

import java.lang.*;

/*
	Code that operates the kicker motor
*/
public class Ultrasonic implements ConnectionInterface
{
	
	private Connection connection;
	
	private Thread ultrasonic = null;
	
	private final UltrasonicSensor u3sensor = new UltrasonicSensor(SensorPort.S1);
	private final UltrasonicSensor u4sensor = new UltrasonicSensor(SensorPort.S2);
	
	public Ultrasonic(Connection connection)
	{
		
		this.connection = connection;
		connection.subscribe(this);
		
	}
	
	// Acts upon receiving a command
	public synchronized void receiveCommand(Message msg)
	{
		
		// Checks message broadcasted is for this class
		if (msg.getOpcode() == Opcodes.ULTRASONIC)
		{
			
			int args[] = msg.getArguments(1);
			
			if (ultrasonic != null && ultrasonic.isAlive())
			{
				
				ultrasonic.interrupt();
				while (ultrasonic.isAlive())
				{
					
					try
					{
						ultrasonic.join();
					}
					catch (Exception e)
					{
						
					}
					
				}
				
			}
			
			if (args[0] == 1)
			{
				
				
				
				ultrasonic = new Thread() {
				
					public void run()
					{
						
						if (u3sensor.continuous() == 0)
						{
							System.out.println("u3sensor Switched to continuous");
						}

						if (u4sensor.continuous() == 0)
						{
							System.out.println("u4sensor Switched to continuous");
						}
						
						
						u3sensor.setContinuousInterval((byte)50);
						u4sensor.setContinuousInterval((byte)50);

						// send data back repeatidly
						while (!Thread.interrupted())
						{
							
							int numu3 = u3sensor.getDistance();
							int numu4 = u4sensor.getDistance();
							
							connection.queueMessage(new Message(Opcodes.ULTRASONIC_DATA, numu3,numu4));
							
							try
							{
								Thread.sleep(50);
							}
							catch (InterruptedException e)
							{
								break;
							}
						}
					
					}
				
				};
			
				ultrasonic.start();
			}
			
		}
		
	}
	
}
