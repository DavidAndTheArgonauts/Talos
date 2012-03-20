package comms.robot;

import java.util.*;

import comms.robot.interrupt.*;
import strategy.controller.*;

public class InterruptManager extends Thread
{
	
	public static final int INTERRUPT_UNSET = 0;
	public static final int INTERRUPT_DISTANCE = 1;
	public static final int INTERRUPT_FACING = 2;
	public static final int INTERRUPT_TOUCH = 3;
	
	public static final int INTERRUPT_QUIT = 98;
	public static final int INTERRUPT_INTERRUPTED = 99;
	
	/* touch modes */
	public static final int MODE_BOTH = 0;
	public static final int MODE_LEFT = 1;
	public static final int MODE_RIGHT = 3;
	public static final int MODE_EITHER = 4;
	
	private ArrayList<AbstractInterrupt> interrupts = new ArrayList<AbstractInterrupt>();
	private Map<Integer,AbstractInterrupt> triggeredInterrupts = new TreeMap<Integer,AbstractInterrupt>();
	
	private Commander commander;
	private AbstractController controller;
	
	private int id = 0;
	
	public InterruptManager(Commander commander, AbstractController controller)
	{
		
		this.commander = commander;
		this.controller = controller;
		
	}
	
	public int registerInterrupt(int code, double value)
	{
		
		synchronized (interrupts)
		{
			
			AbstractInterrupt interrupt = null;
			
			switch(code)
			{
				case INTERRUPT_DISTANCE:
					interrupt = new DistanceInterrupt(id++,value);
					break;
				case INTERRUPT_FACING:
					interrupt = new FacingInterrupt(id++,value);
					break;
				case INTERRUPT_TOUCH:
					interrupt = new TouchInterrupt(id++,value);
					break;
				default:
					return -1;
			}
			
			if (interrupt == null)
			{
				return -1;
			}
			
			System.out.println("Registered interrupt: " + interrupt.getId());
			
			interrupts.add(interrupt);
			
			return interrupt.getId();
			
		}
		
	}
	
	public void clearInterrupts()
	{
		
		synchronized (interrupts)
		{
			interrupts.clear();
		}
		
		synchronized (triggeredInterrupts)
		{
			triggeredInterrupts.clear();
		}
		
	}
	
	public void run()
	{
		
		// keep checking all the interrupts
		
		while (!Thread.interrupted())
		{
			
			synchronized (interrupts)
			{
				
				for (int i = 0; i < interrupts.size(); i++)
				{
					
					if (interrupts.get(i).hasTriggered(commander))
					{
						
						// move interrupt to triggered map
						AbstractInterrupt interrupt = interrupts.get(i);
						interrupts.remove(i);
						
						synchronized (triggeredInterrupts)
						{
							triggeredInterrupts.put(interrupt.getId(), interrupt);
						}
						
						System.out.println("Interrupt manager - interrupt triggered (id = " + interrupt.getId() + ")");
						
						// interrupt controller
						while (!controller.controllerInterrupt(INTERRUPT_INTERRUPTED,interrupt.getId()))
						{
							System.out.println("Retrying to send interrupt...");
							
							try
							{
								Thread.sleep(100);
							}
							catch (InterruptedException e)
							{
								return;
							}
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	public AbstractInterrupt getInterrupt(int id)
	{
		
		synchronized (triggeredInterrupts)
		{
			return triggeredInterrupts.get(id);
		}
		
	}
	
}
