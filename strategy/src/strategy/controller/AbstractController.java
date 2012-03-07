package strategy.controller;

import comms.robot.*;

public abstract class AbstractController extends Thread
{
	
	protected int interruptCode = InterruptManager.INTERRUPT_UNSET, 
				interruptId = -1;
	
	public boolean controllerInterrupt(int code, int id)
	{
		
		synchronized(this)
		{
			if (controllerInterrupted())
			{
				return false;
			}
		
			interruptCode = code;
			interruptId = id;
			
			System.out.println("Storing controller interrupt (code = " + code + ", id = " + id + ")");
			
			interrupt();
			
			return true;
		}
		
	}
	
	public boolean controllerInterrupted()
	{
		
		synchronized (this)
		{
			if (interruptCode == InterruptManager.INTERRUPT_UNSET)
			{
				return false;
			}
			
			System.out.println("Abstract controller found interrupt");
			
			return true;
		}
		
	}
	
	protected int getControllerInterrupt()
	{
		
		synchronized (this)
		{
			int interrupt = interruptId;
			interruptCode = InterruptManager.INTERRUPT_UNSET;
			interruptId = -1;
			return interrupt;
		}
		
	}
	
	public boolean isQuitInterrupt()
	{
		synchronized (this)
		{
			
			return interruptCode == InterruptManager.INTERRUPT_QUIT;
			
		}
	}
	
}
