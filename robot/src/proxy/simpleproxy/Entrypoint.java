package simpleproxy;

/*

	Open connection to robot (with ability to keep trying to connect if connection fails)
	Accept connections from other sources
	Forward data to robot

*/

import simpleproxy.proxy.Proxy;

public class Entrypoint
{
	
	public static void main(String[] args)
	{
		
		Proxy proxy = new Proxy();
		proxy.start();
		
	}
	
}
