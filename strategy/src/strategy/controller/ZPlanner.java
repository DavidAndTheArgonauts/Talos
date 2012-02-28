package strategy.controller;

import java.math.*;
import comms.robot.*;
import strategy.world.*;

public class ZPlanner extends AbstractPlanner {

	double prevX, prevY, endX, endY, dirX, dirY, dirNorm, ballX, ballY, dirBallX, dirBallY, robotX, robotY, robotDir, dirBallNorm,dirBallAngle, dirRoboBall, rightMotor, leftMotor, speedDelta, dirAngle;
int driveLeft, driveRight;

	public ZPlanner() {
	
	}
	
	protected void plan() {
		/*
		prevX = Robot.getX();
		prevY = Robot.getY();
		while(this.getLoop()){
			robotX = Robot.getX();
			robotY = Robot.getY();
			robotDir = Robot.getDir();
			
			// direction from movement
			dirX = robotX - prevX;
			dirY = robotY - prevY;
			dirNorm = vecSize(dirX, dirY);
			dirAngle = Math.toDegrees( Math.atan2(dirX, dirY) );

			System.out.println("dirNorm: " + dirNorm);
			// if change of movement > 1, use direction from movement instead of from vision
			if ( dirNorm > 1 ) {
				robotDir = dirAngle;
			}


			ballX = Ball.getX();
			ballY = Ball.getY();

			dirBallX = ballX - robotX;
			dirBallY = ballY - robotY;
			dirBallNorm = vecSize(dirBallX, dirBallY);
			dirBallAngle = Math.toDegrees( Math.atan2(dirBallX, dirBallY) );

			dirRoboBall = dirBallAngle - robotDir;

			if ( dirRoboBall > 180 ) {
				dirRoboBall = 360 - dirRoboBall;
			}

			System.out.println("dirRoboBall: " + dirRoboBall);

			// set motor speeds based on the direction between robot and ball
			if ( dirRoboBall < 0 ) {
				leftMotor = 1;
				rightMotor = 1 - Math.abs(dirRoboBall) / 90.;
			}	

			else {
				rightMotor = 1;
				leftMotor = 1 - Math.abs(dirRoboBall) / 90.;
			}

			System.out.println("leftMotor: " + leftMotor);
			System.out.println("rightMotor: " + rightMotor);

			System.out.println("dirBallNorm: " + dirBallNorm);

			// normalize motor speed based on the distance between the robot and the ball
			if (dirBallNorm > 50) {
				speedDelta = 1;
			} else if (dirBallNorm > 20) {
				speedDelta = dirBallNorm / 50f;
			} else {
				speedDelta = 0;
			}

			System.out.println("speedDelta: " + speedDelta);

			driveLeft = (int) Math.round( leftMotor * 100f * speedDelta );
			driveRight = (int) Math.round( rightMotor * 100f * speedDelta );

			System.out.println("driveLeft: " + driveLeft);
			System.out.println("driveRight: " + driveRight);

			movement.drive( driveLeft, driveRight );
			movement.waitForRobot();


			// run at 10 FPS, for the movement from robot function to work
			try{Thread.sleep(100);}
				catch(Exception e){}

			prevX = robotX;
			prevY = robotY;
		}*/
	}
}
