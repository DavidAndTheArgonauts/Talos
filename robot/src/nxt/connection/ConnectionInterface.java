package connection;

public interface ConnectionInterface
{
	// Classes with this interface must receive a command
	public void receiveCommand(Message msg);
	
}
