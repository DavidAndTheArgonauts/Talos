package connection;

public class Message
{
	
	private final int cmd;
	
	public Message(int cmd)
	{
		this.cmd = cmd;
	}
	
	// save message with 1 argument
	public Message(int cmd, int arg1)
	{
		
		this.cmd = cmd | (arg1 << 8); // needs testing
		
	}
	
	// save message with 2 arguments
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
	
	public int getCommand()
	{
		return cmd;
	}
	
	public int getOpcode()
	{
		
		// returns the opcode of the msg
		return ((cmd << 24) >> 24);
		
	}
	
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
