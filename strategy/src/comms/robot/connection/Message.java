package comms.robot.connection;

/**
 * Stores a packet to send to the robot 
*/
public class Message
{
	
	private final int cmd;
	
	/**
	 * Create a message with only an opcode
	*/
	public Message(int cmd)
	{
		this.cmd = cmd;
	}
	
	/**
	 * Create a message with an opcode and one argument
	*/
	public Message(int cmd, int arg1)
	{
		
		this.cmd = cmd | (arg1 << 8); // needs testing
		
	}
	
	/**
	 * Create a message with an opcode and two arguments
	*/
	public Message(int cmd, int arg1, int arg2)
	{
		
		int command = cmd
		| ((int) Math.abs(arg1) << 8)
		| ((int) Math.abs(arg2) << 20);

		// set the sign bit for arg1
		if (arg1 < 0) {
			command = command | (1 << 19);
		}
		
		// set the sign bit for arg2
		if (arg2 < 0) {
			command = command | (1 << 31);
		}
		
		this.cmd = command;
		
	}
	
	/**
	 * Get the command to be sent
	 * @return Returns the raw command
	*/
	public int getCommand()
	{
		return cmd;
	}
	
	/**
	 * Filters out the opcode part
	 * @return Returns the opcode of the message
	*/
	public int getOpcode()
	{
		
		// returns the opcode of the msg
		return ((cmd << 24) >> 24);
		
	}
	
	/**
	 * Gets the arguments stored in a message
	 * @param count The number of arguments expected
	 * @return Returns an integer array of lenth count, of the arguments
	*/
	public int[] getArguments(int count)
	{
		
		int[] retval;
		
		switch(count)
		{
		
		case 1:
			retval = new int[1];
			
			retval[0] = cmd >> 8; // needs testing
			
			break;
			
		case 2:
			retval = new int[2];
			
			// get values
			retval[0] = ((cmd << 13) >> 21);
			retval[1] = ((cmd << 1) >> 21);
			
			// handle signs
			if ((cmd << 12) >> 31 == -1)
				retval[0] *= -1;
			
			if (cmd >> 31 == -1)
				retval[1] *= -1;
			
			break;
		
		default:
			retval = new int[0];
			break;
		
		}
		
		return retval;
		
	}
	
}
