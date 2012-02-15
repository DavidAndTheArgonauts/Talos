package sensor;

import lejos.nxt.*;
import connection.*;

/*
	Code that handles responses from the touch sensor (only sensor ports 1 & 2)
*/
public class Touch implements Runnable
{
	
	private Connection connection;
	private Thread sensor = null;

	public Touch(Connection connection)
	{
		this.connection = connection;
		sensor = new Thread(this);
		sensor.start();
	}

	public void run() {

		TouchSensor touchA = new TouchSensor(SensorPort.S1);
		TouchSensor touchB = new TouchSensor(SensorPort.S2);
		
		// Stores state of sensor (pressed, not pressed)
		boolean aPressed = touchA.isPressed();
		boolean bPressed = touchB.isPressed();
		
		// Variables to be passed along with the message (Message does not take boolean arguments)
		int aPressedInt;
		int bPressedInt;

		while (true) {
			try {
				// If new state is different to save state, send sensor feedback
				if (touchA.isPressed() != aPressed || touchB.isPressed() != bPressed) {
					aPressed = touchA.isPressed();
					bPressed = touchB.isPressed();
					if(aPressed){
						aPressedInt = 1;
					} else {
						aPressedInt = 0;
					}
					if(bPressed){
						bPressedInt = 1;
					} else {
						bPressedInt = 0;
					}
					connection.queueMessage(new Message(Opcodes.SENSOR_TOUCHED,aPressedInt,bPressedInt));
					Thread.sleep(100);
				}
				Thread.yield();

			} catch (Exception ex) {}

		}
	}
	
}
