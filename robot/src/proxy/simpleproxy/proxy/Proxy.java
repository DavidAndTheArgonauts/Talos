package simpleproxy.proxy;

import java.util.*;
import java.util.concurrent.*;

import java.io.*;
import java.net.*;
import java.lang.*;

import lejos.pc.comm.*;

public class Proxy
{
	
	// constants
	public static final int PACKET_SIZE = 4;
	public static final String ROBOT_ADDR = "00:16:53:07:75:31";
	public static final String ROBOT_NAME = "group3";
	public static final int PC_PORT = 9899;
	public static final int RETRY_WAIT = 2000;
	
	// queues - thread safe
	private Queue<byte[]> toRobot = new ConcurrentLinkedQueue<byte[]>();
	private Queue<byte[]> fromRobot = new ConcurrentLinkedQueue<byte[]>();
	
	public void start()
	{
		
		// start connection managers
		ConnectionManager pc = new ConnectionManager(fromRobot,toRobot,false);
		ConnectionManager robot = new ConnectionManager(toRobot,fromRobot,true);
		
		System.out.println("Ready for connections");
		
		new Thread(pc).start();
		new Thread(robot).start();
		
	}
	
}
