package strategy.controller;

import java.math.*;
import comms.robot.*;
import strategy.world.*;

public class ZPlanner extends AbstractPlanner {

	double prevX, prevY, endX, endY, dirX, dirY, dirNorm, ballX, ballY, dirBallX, dirBallY, robotX, robotY, robotDir, dirBallNorm,dirBallAngle, dirRoboBall, rightMotor, leftMotor, speedDelta, dirAngle, motorFullSpeed;
 	int driveLeft, driveRight;

	public ZPlanner() {

	}
	
	protected void plan() {
		Commander command= StrategyManager.getCommand();
		World world = StrategyManager.getWorld();

		while (world.getHistorySize() == 0) {
			try {
				Thread.sleep(100);
				System.out.println("Waiting for vision");
			} catch (Exception e) {

			}
		}
		
		prevX = world.getWorldState().getRobotX(true);
		prevY = world.getWorldState().getRobotY(true);
		
		while (this.getLoop()){
			robotX = world.getWorldState().getRobotX(true);
			robotY = world.getWorldState().getRobotY(true);
			robotDir = world.getWorldState().getRobotDir(true);
			
			//Direction from movement
			dirX = robotX- prevX; 
			dirY = robotY - prevY;
			dirNorm = vecSize(dirX, dirY);
			dirAngle = Math.toDegrees(Math.atan2(dirX,dirY));
			
			//If change of movement > 1 use direction from movement instead of vision
			if (dirNorm > 0.5 ) {
				robotDir = dirAngle;
			}
			
			ballX = world.getWorldState().getBallX();
			ballY = world.getWorldState().getBallY();
			
			dirBallX = ballX - robotX;
			dirBallY = ballY - robotY;
			dirBallNorm = vecSize(dirBallX, dirBallY);
			dirBallAngle = Math.toDegrees(Math.atan2(dirBallX,dirBallY));
			
			dirRoboBall = dirBallAngle - robotDir;
			
			if ( dirRoboBall > 180 ) {
				dirRoboBall -= 360;
			}else if ( dirRoboBall < -180 ) {
				dirRoboBall += 360;
			}

			System.out.println("dirRoboBall: " + dirRoboBall);
			
			// if < -30 or > 30 then spin
			if ( dirRoboBall > 60 ) {
				leftMotor = -1;
				rightMotor = 1;
				speedDelta = 1;
			} else if ( dirRoboBall < -60 ) {
				leftMotor = 1;
				rightMotor = -1;
				speedDelta = 1;
			}else {
				// set motor speeds based on the direction between robot and ball
				if ( dirRoboBall < 0 ) {
					leftMotor = 1;
					rightMotor = 1 - Math.abs(dirRoboBall) / 90.;
				} else {
					rightMotor = 1;
					leftMotor = 1 - Math.abs(dirRoboBall) / 90.;
				};

				// normalize motor speed based on the distance between the robot and the ball
				if (dirBallNorm > 50) {
					speedDelta = 1;
				} else if (dirBallNorm > 20) {
					speedDelta = dirBallNorm / 50f;
				} else {
					speedDelta = 0;
					System.out.println("kick!!!");
					command.kick();
					command.waitForQueueToEmpty();
				}

			}
			
			motorFullSpeed = 50;

			driveLeft = (int) Math.round( leftMotor * motorFullSpeed * speedDelta );
			driveRight = (int) Math.round( rightMotor * motorFullSpeed * speedDelta );

			//System.out.println("driveLeft: " + driveLeft);
			//System.out.println("driveRight: " + driveRight);

			command.setSpeed( driveLeft, driveRight );
			command.waitForQueueToEmpty();
		

			// run at 10 FPS, for the movement from robot function to work
			try{Thread.sleep(100);}
			catch(Exception e){}
			
			prevX = robotX;
			prevY = robotY;
		}
		
		
		
		
		//command.subscribe()
	}

	public static double vecSize(double x, double y) {
		return Math.sqrt((Math.pow( x, 2 ) + Math.pow( y,2 )));
	}
}
