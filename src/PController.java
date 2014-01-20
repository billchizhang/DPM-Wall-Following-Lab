import lejos.nxt.*;

public class PController implements UltrasonicController {
	//Given Constants and Variables
	private final int bandCenter, bandwith;
	private final int MOTOR_STRAIGHT = 200, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.B, rightMotor = Motor.C, usMotor = Motor.A;	
	private int distance;
	private int filterControl;
	
	//my constants and variables
	/* minimum speed the robot will travel at */
	private final int MIN_SPEED = 50;
	/* max speed the robot will travel at */
	private final int MAX_SPEED = 350;
	/*factor the error is multiplied by to calculate the speed*/
	private final int SCALING_FACTOR = 10;
	/*angle the US Sensor will rotate to / from*/
	private final int ANGLE = 30;
	/*the difference ACTUAL_DISTANCE and BandCenter +- Bandwith*/
	private int error = 0;
	/*the direction the US Sensor rotates in*/
	private boolean backward = false;
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(MOTOR_STRAIGHT);
		rightMotor.setSpeed(MOTOR_STRAIGHT);
		leftMotor.forward();
		rightMotor.forward();
		usMotor.forward();
		/*starts rotating the sensor*/
		rotateSensor();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		//rotates the sensor back if has passed angle
		if(Math.abs(this.usMotor.getTachoCount()) >= ANGLE){
			rotateSensor();
		}
		this.distance = distance;
		//difference between ideal distance and real distance
		this.error = (bandCenter - this.distance);
		//if FAR away:
		if (error < -bandwith){
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
	 * Rotates the Sensor
	 * 
	 * Rotates to 0 if at ANGLE deg (backward)
	 * Rotates to ANGLE if at 0 deg (forwards) 
	 */
	private void rotateSensor() {
		//sets starting angle to 0
		usMotor.resetTachoCount();
		if(backward){
			usMotor.rotateTo(-ANGLE, true);
		}
		else usMotor.rotateTo(ANGLE, true);
		//changes the direction it will travel in next
		backward = !backward;
	}
	/* 
	 * Increases the speed of the rightMotor and reduces speed of the Left Motor
	 * Method is called if robot is traveling further than BANDCENTER + BANDWITH
	 * 
	 * @param error : the absolute value of (BandCenter - this.distance)
	 * 
	 * The speed of the left motor is 200 - 10 * |this.distance - BANDCENTER| down to 50 rad/s
	 * The speed of the right motor is 200 + 10 * |this.distance - BANDCENTER| up to 350 rad/s
	 */
	public void turnLeft(int error){
		leftMotor.setSpeed(Math.max((MOTOR_STRAIGHT - SCALING_FACTOR * error) , MIN_SPEED));
		rightMotor.setSpeed(Math.min((MOTOR_STRAIGHT + SCALING_FACTOR * error), MAX_SPEED));

		leftMotor.forward();
		rightMotor.forward();
	}
	/* 
	 * Increases the speed of the leftMotor and reduces the speed of the rightMotor
	 * Method is called if the robot is traveling less than BANDCENTER - BANDWITH
	 * 
	 * @param error : the absolute value of (BandCenter - this.distance)
	 * 
	 * The speed of the left motor is: 	200 + (|this.distance - BANDCENTER| / 10)
	 * The speed of the right motor is:	-1 * [ 200 - (|this.distance - BANDCENTER| / 10)]
	 */
	public void turnRight(int error){	
		leftMotor.setSpeed(MOTOR_STRAIGHT + error / SCALING_FACTOR);
		rightMotor.setSpeed(MOTOR_STRAIGHT - error / SCALING_FACTOR);
		
		leftMotor.forward();
		rightMotor.backward();
	}
	/*
	 * Sets the robot to go straight at speed MOTOR_STRAIGHT
	 * Called if robot is traveling between BANDCENTER +- BANDWITH
	 */
	public void straight(){
	
		leftMotor.setSpeed(MOTOR_STRAIGHT);		
		rightMotor.setSpeed(MOTOR_STRAIGHT); 
		
		leftMotor.forward();
		rightMotor.forward();
	}
	
	@Override
	public int readUSDistance() {
		return this.distance;
	}
}
