/*
 * Group 21 - ECSE 211
 * Satyajit Kanetkar 	-- 260504913
 * Sean Wolfe			-- 260584644
 * 
 * @requirement: Sensor must be positioned at a 45 degree angle
 * @requirement: Robot must be on the right of the wall
 * 
 * no changes
 */

public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
	
}
