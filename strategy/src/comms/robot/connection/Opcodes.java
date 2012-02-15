package comms.robot.connection;

public abstract class Opcodes
{
	
	// robot opcodes
	public static final int SET_SPEED = 0x01;
	public static final int KICK = 0x02;
	public static final int REQUEST_SENSOR_DATA = 0x03;
	
	public static final int CLOSE = 0x7E;
	public static final int QUIT = 0x7F;
	
	// return opcodes
	public static final int COMMAND_COMPLETE = 0x01;
	public static final int SENSOR_DATA = 0x02;
	
}
