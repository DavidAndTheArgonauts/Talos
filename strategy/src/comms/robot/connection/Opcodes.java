package comms.robot.connection;

/**
 * Stores the list of opcodes available and their meaning
*/
public abstract class Opcodes
{
	
	// robot opcodes
	/**
	 * Sets the robots wheel speeds
	*/
	public static final int SET_SPEED = 0x01;
	/**
	 * Operates the kicker 
	*/
	public static final int KICK = 0x02;
	
	/**
	 * Closes the connection to the robot 
	*/
	public static final int CLOSE = 0x7E;
	/**
	 * Quits the software on the robot 
	*/
	public static final int QUIT = 0x7F;
	
	// return opcodes
	/**
	 * Whenever a command has been processed on the robot, this is returned
	*/
	public static final int COMMAND_COMPLETE = 0x01;
	/**
	 * When the touch sensors are changed this is returned
	*/
	public static final int SENSOR_TOUCHED = 0x02;
	/**
	 * When the wheels are moving, feedback about actual speeds is returned
	*/
	public static final int WHEEL_FEEDBACK = 0x03;
	
}
