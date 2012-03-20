package comms.vision;

import strategy.world.*;

import com.illposed.osc.*;

public class VisionReceiver
{

	private OSCPortIn receiver;
	private OSCListener lBlueX;
	private OSCListener lBlueY;
	private OSCListener lBlueDX;
	private OSCListener lBlueDY;
	private OSCListener lBlueV;

	private OSCListener lRedX;
	private OSCListener lRedY;
	private OSCListener lRedV;

	private OSCListener lYellowX;
	private OSCListener lYellowY;
	private OSCListener lYellowDX;
	private OSCListener lYellowDY;
	private OSCListener lYellowV;

	private int msg = 0;
	
	/**
	 * Listens for vision data and updates world when available
	 * @param port The port to listen on
	 * @param world A world which should be updated with vision data
	*/
	public VisionReceiver(int port, final World world) {
		
		try {
			receiver = new OSCPortIn(port);
		} catch(java.net.SocketException e){
			System.out.println("Error: Socket Exception. Did the code finish running or did you force it to exit?");
		}
		lBlueX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setBlueX((Float) message.getArguments()[0]);
			}
		};
		lBlueY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setBlueY((Float) message.getArguments()[0]);
			}
		};
		lBlueDX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setBlueDX((Float) message.getArguments()[0]);
			}
		};
		lBlueDY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setBlueDY((Float) message.getArguments()[0]);
			}
		};
		lBlueV = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setBlueVisible((Integer) message.getArguments()[0]);
			}
		};
		lYellowX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setYellowX((Float) message.getArguments()[0]);
			}
		};
		lYellowY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setYellowY((Float) message.getArguments()[0]);
			}
		};
		lYellowDX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setYellowDX((Float) message.getArguments()[0]);
			}
		};
		lYellowDY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setYellowDY((Float) message.getArguments()[0]);
			}
		};
		lYellowV = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setYellowVisible((Integer) message.getArguments()[0]);
			}
		};
		lRedX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setBallX((Float) message.getArguments()[0]);
			}
		};
		lRedY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setBallY((Float) message.getArguments()[0]);
			}
		};
		lRedV = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				world.getPartialState().setBallVisible((Integer) message.getArguments()[0]);
			}
		};
		receiver.addListener("/table/blue/posx", lBlueX);
		receiver.addListener("/table/blue/posy", lBlueY);
		receiver.addListener("/table/blue/dirx", lBlueDX);
		receiver.addListener("/table/blue/diry", lBlueDY);
		receiver.addListener("/table/blue/visible", lBlueV);
		receiver.addListener("/table/red/posx", lRedX);
		receiver.addListener("/table/red/posy", lRedY);
		receiver.addListener("/table/red/visible", lRedV);
		receiver.addListener("/table/yellow/posx", lYellowX);
		receiver.addListener("/table/yellow/posy", lYellowY);
		receiver.addListener("/table/yellow/dirx", lYellowDX);
		receiver.addListener("/table/yellow/diry", lYellowDY);
		receiver.addListener("/table/yellow/visible", lYellowV);
		receiver.startListening();
	}

	/**
	 * Stops listening for vision data
	*/
	public void close() {
		receiver.stopListening();
		
		receiver.close();
	}
	
}
