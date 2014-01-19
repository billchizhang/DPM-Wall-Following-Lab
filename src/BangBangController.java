import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 200;
	private final NXTRegulatedMotor leftMotor = Motor.B, rightMotor = Motor.C, usMotor = Motor.A;
	private int distance;
	private int currentLeftSpeed;
	
	//mine
	private final int DELTA = 100;
	private int error = 0;
	private int filterControl = 0;
	private int FILTER_OUT = 20;
	private int LEFT_SPEED = 100;
	private int RIGHT_SPEED = 300;
	private final int ANGLE = 90;
	private final int US_SPEED = 275;
	private boolean dir = false;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		usMotor.setSpeed(US_SPEED);
		leftMotor.forward();
		rightMotor.forward();
		rotateSensor();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
		if(Math.abs(this.usMotor.getTachoCount()) >= ANGLE){
			rotateSensor();
		}
		
		this.distance = distance;
		// TODO: process a movement based on the us distance passed in (BANG-BANG style)
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
	}

	private void straight() {
		// TODO Auto-generated method stub
		leftMotor.setSpeed(motorStraight);				//sets to full speed			
		rightMotor.setSpeed(motorStraight); 
		
		leftMotor.forward();
		rightMotor.forward();
	}

	private void turnRight(int abs) {
		// TODO Auto-generated method stub
		leftMotor.setSpeed(motorStraight);				//starts turning a lot
		rightMotor.setSpeed(motorStraight);
		
		leftMotor.forward();
		rightMotor.backward();
	}

	private void turnLeft(int abs) {
		// TODO Auto-generated method stub
		leftMotor.setSpeed(LEFT_SPEED);
		rightMotor.setSpeed(RIGHT_SPEED);

		leftMotor.forward();
		rightMotor.forward();
	}
	
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
