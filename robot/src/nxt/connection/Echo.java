package connection;

public class Echo implements ConnectionInterface
{
	
	private Connection connection;
	
	public Echo(Connection connection)
	{
		
		this.connection = connection;
		connection.subscribe(this);
	
	}
	
	public void receiveCommand(Message msg)
	{
		
		connection.queueMessage(msg);
		
	}
	
}
