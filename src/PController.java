import lejos.nxt.*;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int MOTOR_STRAIGHT = 200, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.B, rightMotor = Motor.C, usMotor = Motor.A;	
	private int distance;
	private int filterControl;
	
	//my constants and variables
	private final int MIN_SPEED = 50;
	private final int MAX_SPEED = 350;
	private final int SCALING_FACTOR = 10;
	private final int ANGLE = 90;
	private int error = 0;
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
		rotateSensor();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
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
	 * Rotates to 0 if at 90 deg (backward)
	 * Rotates to 90 if at 0 deg (forwards) 
	 */
	private void rotateSensor() {
		usMotor.resetTachoCount();
		if(backward){
			usMotor.rotateTo(-ANGLE, true);
		}
		else usMotor.rotateTo(ANGLE, true);
		backward = !backward;
	}
	/*Increases the speed of the rightMotor and reduces speed of the Left Motor
	 * 
	 * 
	 */
	public void turnLeft(int error){
		leftMotor.setSpeed(Math.max((MOTOR_STRAIGHT - SCALING_FACTOR * error) , MIN_SPEED));
		rightMotor.setSpeed(Math.min((MOTOR_STRAIGHT + SCALING_FACTOR * error), MAX_SPEED));

		leftMotor.forward();
		rightMotor.forward();
	}
	/*Increases the speed of the leftMotor and reduces the speed of the rightMotor
	 * 
	 * 
	 */
	public void turnRight(int error){	
		leftMotor.setSpeed(MOTOR_STRAIGHT + error / SCALING_FACTOR);
		rightMotor.setSpeed(MOTOR_STRAIGHT - error / SCALING_FACTOR);
		
		leftMotor.forward();
		rightMotor.backward();
	}
	/*
	 * sets the robot to go straight
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
