package org.talos.predictor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.illposed.osc.*;

public class Predictor {

	private static final int HISTORY_SIZE = 100;
	private static final int PORT_IN = 5500;
	private static final int PORT_OUT = 5501;
	
	private ArrayList<World> history = new ArrayList<World>();
	
	private OSCPortIn receiver;
	private OSCPortOut sender;
	
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
	
	private float tbx_;
	private float tby_;
	private float tbdx_;
	private float tbdy_;
	
	private float tyx_;
	private float tyy_;
	private float tydx_;
	private float tydy_;
	
	private float trx_;
	private float try_;

	public Predictor() {
		try {
			receiver = new OSCPortIn(PORT_IN);
			sender = new OSCPortOut(InetAddress.getLocalHost(), PORT_OUT);
		} catch(java.net.SocketException e){
			System.out.println("Error: Socket Exception. Did the code finish running or did you force it to exit?");
		} catch (UnknownHostException e) {
			System.out.println("Error: Unknown Host Exception. Is localhost loopback available?");
		}
		lBlueX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("BlueX", (Float) message.getArguments()[0]);
			}
		};
		lBlueY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("BlueY", (Float) message.getArguments()[0]);
			}
		};
		lBlueDX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("BlueDX", (Float) message.getArguments()[0]);
			}
		};
		lBlueDY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("BlueDY", (Float) message.getArguments()[0]);
			}
		};
		lBlueV = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				// TODO Not being used in system at the moment
			}
		};
		lYellowX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("YellowX", (Float) message.getArguments()[0]);
			}
		};
		lYellowY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("YellowY", (Float) message.getArguments()[0]);
			}
		};
		lYellowDX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("YellowDX", (Float) message.getArguments()[0]);
			}
		};
		lYellowDY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("YellowDY", (Float) message.getArguments()[0]);
			}
		};
		lYellowV = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				// TODO Not being used in system at the moment
			}
		};
		lRedX = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("RedX", (Float) message.getArguments()[0]);
			}
		};
		lRedY = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				accept("RedY", (Float) message.getArguments()[0]);
			}
		};
		lRedV = new OSCListener() {
			public void acceptMessage(java.util.Date time, OSCMessage message) {
				// TODO Not being used in system at the moment
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
	 * Accepts incoming message from vision
	 * @param name Name of the incoming value
	 * @param value Value, extracted from the message
	 */
	public void accept(String name, float value) {
		if(name.equals("BlueX")) {
			tbx_ = value;
		}
		if(name.equals("BlueY")) {
			tby_ = value;
		}
		if(name.equals("BlueDX")) {
			tbdx_ = value;
		}
		if(name.equals("BlueDY")) {
			tbdy_ = value;
		}
		if(name.equals("YellowX")) {
			tyx_ = value;
		}
		if(name.equals("YellowY")) {
			tyy_ = value;
		}
		if(name.equals("YellowDX")) {
			tydx_ = value;
		}
		if(name.equals("YellowDY")) {
			tydy_ = value;
		}
		if(name.equals("RedX")) {
			trx_ = value;
		}
		if(name.equals("RedY")) {
			try_ = value;
			history.add(new World(new DirectionalObject(tbx_, tby_, tbdx_, tbdy_, 1),
								  new DirectionalObject(tyx_, tyy_, tydx_, tydy_, 1),
								  new SimpleObject(trx_, try_, 1)));
			if(history.size() > HISTORY_SIZE)
				history.remove(0);
			return;
		}
	}
	
	/**
	 * Closes all communications
	 */
	public void close() {
		receiver.stopListening();
		receiver.close();
		sender.close();
	}
	
	public ArrayList<World> getHistory() {
		return history;
	}
	
	public static void main(String[] args) {
		Predictor p = new Predictor();
		while(true) {
			try
			{
				p.send(Logic.getPrediction(p.getHistory()));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void send(World state) {
		//OSCMessage msg = new OSCMessage("/table", state.getObject());
		
		Object[] values = state.getObject();
		
		OSCBundle msg = new OSCBundle();
		
		msg.addPacket(getMsg("/table/blue/posx",values[0]));
		msg.addPacket(getMsg("/table/blue/posy",values[1]));
		msg.addPacket(getMsg("/table/blue/dirx",values[2]));
		msg.addPacket(getMsg("/table/blue/diry",values[3]));
		msg.addPacket(getMsg("/table/blue/visible",1));
		msg.addPacket(getMsg("/table/yellow/posx",values[4]));
		msg.addPacket(getMsg("/table/yellow/posy",values[5]));
		msg.addPacket(getMsg("/table/yellow/dirx",values[6]));
		msg.addPacket(getMsg("/table/yellow/diry",values[7]));
		msg.addPacket(getMsg("/table/yellow/visible",1));
		msg.addPacket(getMsg("/table/red/posx",values[8]));
		msg.addPacket(getMsg("/table/red/posy",values[9]));
		msg.addPacket(getMsg("/table/red/visible",1));
		
		try{
			sender.send(msg);
		} catch (Exception e) {
            System.out.println("Error: Couldn't send a message");
		}
	}
	
	private OSCMessage getMsg(String addr, Object value)
	{
		
		OSCMessage msg = new OSCMessage(addr);
		msg.addArgument(value);
		return msg;
		
	}
	
}
