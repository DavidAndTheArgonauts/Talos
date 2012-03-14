

import connection.*;
import controller.*;
import controller.penalty.*;
import sensor.*;

import lejos.nxt.*;

public class NXT implements ConnectionInterface
{
	
	private static boolean terminateConnection = false;
	
	public static void main(String[] args)
	{
		
		LCD.clear();
		LCD.drawString("Starting...",0,0);
		
		// create a new connection
		Connection connection = new Connection();
				
		// subscribe (for the quit command)
		connection.subscribe(new NXT());
		
		// create controllers
		Driver driver = new Driver(connection);
		Kicker kicker = new Kicker(connection);
		Penalty penalty = new Penalty(connection);
		
		
		// create sensors
		Touch sensor = new Touch(connection);
		
		while (!NXT.terminateConnection)
		{
			
			LCD.clear();
			LCD.drawString("Ready...",0,0);
			
			// wait for connection
			connection.connect();
			
		}
		
	}
	
	public void receiveCommand(Message msg)
	{
		
		if (msg.getOpcode() == Opcodes.QUIT)
		{
			
			// handle quit message
			NXT.terminateConnection = true;
			
		}
		
	}
	
}
