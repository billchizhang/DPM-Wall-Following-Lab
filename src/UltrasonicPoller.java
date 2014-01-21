import lejos.nxt.UltrasonicSensor;

/*
 * Group 21 - ECSE 211
 * Satyajit Kanetkar 	-- 260504913
 * Sean Wolfe			-- 260584644
 * 
 * @requirement: Sensor must be positioned at a 45 degree angle
 * @requirement: Robot must be on the right of the wall
 * 
 * No changes
 */

public class UltrasonicPoller extends Thread{
	private UltrasonicSensor us;
	private UltrasonicController cont;
	
	public UltrasonicPoller(UltrasonicSensor us, UltrasonicController cont) {
		this.us = us;
		this.cont = cont;
	}
	
	public void run() {
		while (true) {
			//process collected data
			cont.processUSData(us.getDistance());
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}

}
