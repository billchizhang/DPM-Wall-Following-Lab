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
	private boolean dir = false;
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
		this.error = (bandCenter - this.distance);				//difference between ideal distance and real distance
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
		//it wasn't working without telling the motor to go forward everytime
	}
	private void rotateSensor() {
		// TODO Auto-generated method stub
		usMotor.resetTachoCount();
		if(dir){
			usMotor.rotateTo(-ANGLE, true);
		}
		else usMotor.rotateTo(ANGLE, true);
		dir = !dir;
	}

	public void turnLeft(int error){
/*		rightSpeed = Math.max(MOTOR_STRAIGHT + error / SCALING_FACTOR, MIN_SPEED);
		leftSpeed = MOTOR_STRAIGHT;
*/	
		leftMotor.setSpeed(Math.max((MOTOR_STRAIGHT - SCALING_FACTOR * error) , MIN_SPEED));
		rightMotor.setSpeed(Math.min((MOTOR_STRAIGHT + SCALING_FACTOR * error), MAX_SPEED));

		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void turnRight(int error){
/*		rightSpeed = Math.min(MOTOR_STRAIGHT - error * SCALING_FACTOR, MAX_SPEED);
		leftSpeed = Math.max(MOTOR_STRAIGHT + error * SCALING_FACTOR,MIN_SPEED);
*/	
		leftMotor.setSpeed(MOTOR_STRAIGHT + error / SCALING_FACTOR);
		rightMotor.setSpeed(MOTOR_STRAIGHT - error / SCALING_FACTOR);
		
		leftMotor.forward();
		rightMotor.backward();
	}
	
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
