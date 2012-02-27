package strategy.controller;


import java.math.*;


import comms.robot.*;
import strategy.world.*;

public class DribblePlanner extends AbstractPlanner {

	private static double angle, prevRobotX, prevRobotY, robotX, robotY;
	private static int currentState;
	private MovementController movement = StrategyManager.getMController();

	public DribblePlanner() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void plan() {
		double dribbleStartX = -1, dribbleStartY = -1;
		
		System.out.println("Starting Dribble Planner");
		currentState = 1000;
		prevRobotX = Robot.getX();
		prevRobotY = Robot.getY();
		movement.drive(30,30);
		try{
			Thread.sleep(1000);
		}
		catch(Exception e){
		}
		movement.stop();

		System.out.println("Entering main loop!");
		
		planloop:
		while(this.getLoop()){
			if ((prevRobotX == Robot.getX()) && (prevRobotY == Robot.getY()))
				{
					try
					{
						Thread.sleep(5);
					}
					catch (Exception e) {}
					continue;
				}

				
				if (Robot.getX() < 15 || Robot.getX() > 225 || Robot.getY() < 15 || Robot.getY() > 110)
				{
					System.out.println("("+ Robot.getX() + "," + Robot.getY() + ")");
					System.out.println("Going to collide");
					movement.drive(-30,-30);
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException ie) {}
					movement.drive(10,10);
				}
				
				
				// Get current robot position
				robotX = Robot.getX();
				robotY = Robot.getY();
				// Get angle between robot vector and robot-ball vector
				double angle1 = Math.toDegrees(this.angleBetween2Lines(prevRobotX, prevRobotY, robotX, robotY));
				if (angle1 < 90 && angle1 > 0){
					angle = angle1;
				}
				if (angle1 > 90){
					angle = 180 - angle1;
				}  
				if (angle1 < 0 && angle1 > -90) {
					angle = angle1;
				}
				if (angle1 < -90){
					angle = -180 - angle1;
				}
				
				System.out.println("Angle == " + angle);
				// Update previous robot position
				  if((-10<angle) && (angle<10))
				{
					if (currentState != 0){
						movement.drive(50,50);
						currentState = 0;
						System.out.println("Drive forward!");		
					}
				} else if (angle>10){
					if (currentState != 1){
						movement.drive(15,40);
						currentState = 1;
						System.out.println("Drive right!");	
					}
				} else if (angle<-10){
					if (currentState != -1){
						movement.drive(40,15);
						currentState = -1;
						System.out.println("Drive left!");	
					}
				} 

				double euclidDistance = Math.sqrt((Math.pow(Robot.getX() - Ball.getX(), 2) + Math.pow(Robot.getY() - Ball.getY(), 2)));
				if (euclidDistance < 25) {
					
					if (dribbleStartX == -1 || dribbleStartY == -1)
					{
						dribbleStartX = Robot.getX();
						dribbleStartY = Robot.getY();
					}
					
					double dribbleDistance = Math.sqrt((Math.pow(Robot.getX() - dribbleStartX, 2) + Math.pow(Robot.getY() - dribbleStartY, 2)));
					
					movement.drive(50,50);
					
					if (dribbleDistance > 50)
					{
						System.out.println("Dribble complete");
						
						movement.kick();
						movement.stop();
						movement.waitForRobot();
						
						this.halt();
						break planloop;
					}
					
				}
				else
				{
					dribbleStartX = -1;
					dribbleStartY = -1;
				}

				prevRobotX = robotX;
				prevRobotY = robotY;
				System.out.println(currentState);
				System.out.println(euclidDistance);
				
		}

	}
}
