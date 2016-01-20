import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;
/**author
 * 
 * @author Bob de Ruiter, Jochem Baaij, Sanna Dinh, Olaf Maltha, Jelle Hilbrands
 * 
 * The class for controlling the "Afvuurbot". Cycle is the main function here.
 */
public class AfvuurbotPoseControl {
	private RegulatedMotor c, d;
	private USControl u;
	private int maxspeed;
	private ServerControl server;
	
	/**
	 * The constructor for the afvuurbot. 
	 * It takes a RegulatedMotor c and d. Sets the UltraSonic sensor to SensorPort 4.
	 * Calls cycle() when done.
	 * @param c: Lego EV3 RegulatedMotor
	 * @param d: Lego EV3 RegulatedMotor
	 */
	public AfvuurbotPoseControl(RegulatedMotor c, RegulatedMotor d) {
		this.c = c;
		this.d = d;
		this.u = new USControl(SensorPort.S4);
		maxspeed = Math.min((int) c.getMaxSpeed(), (int) d.getMaxSpeed());
//		for (int i = 6500; i < 8500; i += 500) {
//			System.out.println("SHOOTING PERCENTAGE: " + i);
//			for (int j = 0; j < 10; j++) {
//				System.out.println("distance: " + u.getAvgSample(200));
//				shoot(80, i);
//			}
//		}
		
		server = new ServerControl();
		cycle();
	}
	
	/**
	 * The function which runs through one loop of our robotics task.
	 * Rotates the afvuurbot to a random rotation, waits for the "bekerbot" to arrive
	 * at that location and then shoots to that location. 
	 */
	public void cycle() {
		float sample;
		
		int r = sendRandomRadius(); //Always 1, non-dynamic solution
		
//		do {
//			rotateToRandom();
//			sample = u.getAvgSample(20);
//		} while (sample < 1.0);
		
		server.writeInt('s'); // start
		int sleepTime = 500;
		do {
			sleep(sleepTime);
			sample = u.getAvgSample(sleepTime/5);
			System.out.println("distance: " + sample);
		} while (!(sample > 0.25 && sample < 0.65));
		
		server.writeInt('x'); // stop bekerbot
		sleepTime*=3;
		sleep(sleepTime);
		rotateToBekerbot();
		sample = u.getAvgSample(sleepTime/5);
		
		
		
		int shootingSpeed = getShootingSpeedFromDistance(sample);
		System.out.println("final distance: " + sample);
		System.out.println("SS: "+ shootingSpeed);
		shoot(75, shootingSpeed);
		monitorSweeping();
		terminateSignal();
		resetRotation();
	}
	
	public void monitorSweeping() {
		boolean seen = true;
		while (server.readInt() != 'e') {
//			if (seen && !(u.getAvgSample(20) < 0.60)) {
//				seen = false;
//				server.writeInt('c'); // change direction
//			}
//			else if (u.getAvgSample(20) < 0.30) {
//				seen = true;
//				server.writeInt('l'); // change direction, change sweep
//			}
//			else if (u.getAvgSample(20) < 0.60) {
//				seen = true;
//			}
		}
	}
	
	public void sleep(int t) {
		try {
			Thread.sleep(t);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void rotateToBekerbot() {
		d.rotate(310);
	}
	
	public int getShootingSpeedFromDistance(float distance) {
		return Math.min((int)(6200.0 + ((distance - .17) * 2700.0)),8200) ;
	}
	
	public void terminateSignal() {
		server.writeInt(42);
		while(server.readInt() != 37) {
		}
		System.out.println("terminated");
	}
	
	/**
	 * Rotates the robot to a random rotation. Accounts for the gears used.
	 */
	public void rotateToRandom() {
		int rotation = (int)((Math.random() * 360) - 180);
		d.rotateTo(rotation*10); //gear ratio = 10
	}
	
	/**
	 * Writes the random rotation radius to the server for the bekerbot to read.
	 * @return The radius used
	 */
	public int sendRandomRadius() {
		int radius = (int)(Math.random() * 0) + 1; //Always produces 1 
		server.writeInt(radius);
		return radius;
	}
	
	/**
	 * Shoots the payload by rotating the motors to one end and charging the rubber bands.
	 * Then unwinds the rubber bands to be able to shoot again.
	 * @param speed The speed at which the motors turn.
	 * @param distPercentage A percentage of the maximal distance the robot can fire.
	 */
	public void shoot(int speed, int distPercentage) {
		c.setSpeed(maxspeed*speed/100);
		c.rotateTo(distPercentage);
		c.setSpeed(maxspeed*speed/100);
		c.rotateTo(0);
	}
	
	public void resetRotation() {
		d.rotateTo(0);
	}
	
}
