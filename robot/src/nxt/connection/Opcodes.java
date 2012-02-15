package connection;

public abstract class Opcodes
{
	
	// Incoming opcodes (PC end to NXT)
	public static final int SET_SPEED = 0x01;
	public static final int KICK = 0x02;
	public static final int REQUEST_SENSOR_DATA = 0x03;
	
	public static final int CLOSE = 0x7E;
	public static final int QUIT = 0x7F;
	
	// Outgoing opcodes (NXT end to PC end)
	public static final int COMMAND_COMPLETE = 0x01;
	public static final int SENSOR_TOUCHED = 0x02;
	public static final int WHEEL_FEEDBACK = 0x03;

	
}
