import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	//Given Constants and Variables
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 200;
	private final NXTRegulatedMotor leftMotor = Motor.B, rightMotor = Motor.C, usMotor = Motor.A;
	private int distance;
	private int currentLeftSpeed;
	
	//My constants and Variables
	/*the difference ACTUAL_DISTANCE and BandCenter +- Bandwith*/
	private int error = 0;
	/*counts gaps*/
	private int filterControl = 0;
	/*if too many gaps, starts turning left*/
	private int FILTER_OUT = 20;
	/*motor speed constants*/
	private int LEFT_SPEED = 100;
	private int RIGHT_SPEED = 300;
	private final int US_SPEED = 275;
	/*angle the motor will rotate too / from*/
	private final int ANGLE = 30;
	/*direction the US Sensor will rotate in*/
	private boolean dir = false;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		/*sets speed of US Motor*/
		//usMotor.setSpeed(US_SPEED);
		leftMotor.forward();
		rightMotor.forward();
		/*starts rotating the sensor*/
		//rotateSensor();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
		if(Math.abs(this.usMotor.getTachoCount()) >= ANGLE){
			//rotateSensor();
		}
		
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
		this.error = (bandCenter - this.distance);				//difference between ideal distance and real distance
		//if FAR away:
		if (error < -bandwith){
			//increments how many times it has been far away
			filterControl++;
			//robot has been FAR for too long, it turns left
			if (filterControl > FILTER_OUT){
				turnLeft(Math.abs(error));
			}
		}
		//if CLOSE:
		else if (error > bandwith){
			filterControl = 0;
			turnRight(Math.abs(error));
		}
		//if IDEAL distance:
		else {
			filterControl = 0;
			straight();
		}
	}
	/*
	 * Sets the robot to go straight at speed MOTOR_STRAIGHT
	 * Called if robot is traveling between BANDCENTER +- BANDWITH
	 */
	private void straight() {
		leftMotor.setSpeed(motorStraight);		
		rightMotor.setSpeed(motorStraight); 
		
		leftMotor.forward();
		rightMotor.forward();
	}
	/* 
	 * Increases the speed of the leftMotor and reduces the speed of the rightMotor
	 * Method is called if the robot is traveling less than BANDCENTER - BANDWITH
	 * 
	 * @param error : the absolute value of (BandCenter - this.distance)
	 * 
	 * The speed of the left motor is: 		200 rad/s
	 * The speed of the right motor is:		-200 rad/s
	 */
	private void turnRight(int abs) {
		// TODO Auto-generated method stub
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		
		leftMotor.forward();
		rightMotor.backward();
	}
	/* 
	 * Increases the speed of the rightMotor and reduces speed of the Left Motor
	 * Method is called if robot is traveling further than BANDCENTER + BANDWITH
	 * 
	 * @param error : the absolute value of (BandCenter - this.distance)
	 * 
	 * The speed of the left motor is LEFT_SPEED
	 * The speed of the right motor is RIGHT_SPEED
	 */
	private void turnLeft(int abs) {
		// TODO Auto-generated method stub
		leftMotor.setSpeed(LEFT_SPEED);
		rightMotor.setSpeed(RIGHT_SPEED);

		leftMotor.forward();
		rightMotor.forward();
	}
	/*
	 * Rotates the Sensor
	 * 
	 * Rotates to 0 if at ANGLE deg (backward)
	 * Rotates to ANGLE if at 0 deg (forwards) 
	 */
	private void rotateSensor(){
		usMotor.resetTachoCount();
		if(dir){
			usMotor.rotateTo(-ANGLE, true);
		}
		else usMotor.rotateTo(ANGLE, true);
		dir = !dir;
	}
	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
