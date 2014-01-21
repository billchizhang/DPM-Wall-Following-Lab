import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;

/*
 * Group 21 - ECSE 211
 * Satyajit Kanetkar 	-- 260504913
 * Sean Wolfe			-- 260584644
 * 
 * @requirement: Sensor must be positioned at a 45 degree angle
 * @requirement: Robot must be on the right of the wall
 * 
 * Prints speed of motors
 */

public class Printer extends Thread {
	
	private UltrasonicController cont;
	private final int option;
	
	public Printer(int option, UltrasonicController cont) {
		this.cont = cont;
		this.option = option;
	}
	
	public void run() {
		while (true) {
			LCD.clear();
			LCD.drawString("Controller Type is... ", 0, 0);
			if (this.option == Button.ID_LEFT)
				LCD.drawString("BangBang", 0, 1);
			else if (this.option == Button.ID_RIGHT)
				LCD.drawString("P type", 0, 1);
			LCD.drawString("US Distance: " + cont.readUSDistance(), 0, 2 );
			
			//prints diriving motor speed 
			LCD.drawInt(Motor.B.getSpeed(), 0, 3);
			LCD.drawInt(Motor.C.getSpeed(), 4, 3);
			
			LCD.drawInt(Motor.A.getTachoCount(), 0, 4);
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	public static void printMainMenu() {
		LCD.clear();
		LCD.drawString("left = bangbang",  0, 0);
		LCD.drawString("right = p type", 0, 1);
	}
}
