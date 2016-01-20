import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;
/**
 * 
 * @author Bob de Ruiter, Jochem Baaij, Sanna Dinh, Olaf Maltha, Jelle Hilbrands
 *
 * The class for controlling the "Bekerbot". Cycle is the main function here.
 */

public class BekerbotPoseControl {
	private RegulatedMotor l, r, m;
	private USControl u;
	private ClientControl client;
	int maxspeed;
	DifferentialPilot dp;
	SensorControl ccfront;
	SensorControl ccball;

	/**
	 * The Constructor class. This constructor sets the motors, sensors and server.
	 * Then proceeds to call cycle().
	 * 
	 * @param l Lego EV3 RegulatedMotor left wheel
	 * @param r Lego EV3 RegulatedMotor right wheel
	 * @param m Lego Ev3 RegulatedMotor arm
	 */
	@SuppressWarnings("deprecation")
	public BekerbotPoseControl(RegulatedMotor l, RegulatedMotor r, RegulatedMotor m) {
		this.l = l;
		this.r = r;
		this.m = m;
		this.u = new USControl(SensorPort.S4);
		
		System.out.println("uscontrol gevonden");
		
		client = new ClientControl();
		
		System.out.println("server gestart");

		maxspeed = Math.min((int) l.getMaxSpeed(), (int) r.getMaxSpeed());
		
		ccfront = new ColorControl(SensorPort.S3);
		ccball = new GreyControl(SensorPort.S1);

		dp = new DifferentialPilot(5.6f, 13.75f, l, r);
		dp.setLinearSpeed(7);
		cycle();
	}
	
	/**
	 * The function which runs through one loop of our robotics task.
	 * The bekerbot traverses a circle with received radius and waits until a message.
	 * After a message from the afvuurbot the bekerbot waits for the afvuurbot to shoot the ball
	 * into the cup and then proceeds to return the received ball to the afvuurbot.
	 */
	public void cycle() {
		int radius = client.waitForInt();
		
		client.waitForInt(); // start
		
		followCircleUntil(radius, false);
		float sample;
		
		do {
			sample = ccball.getAvgSample(20);
		} while (sample < 0.07);
		
		followCircleUntil(radius, true);
		rotateToA();
		client.writeInt('e'); // emptying bucket
		emptyBucket();
		terminateSignal();
	}

	public void sweepToLeftEye() {
		boolean goingLeft = true;
		dp.arcForward(-55);
		int msg = -1;
		while (msg != 'l') { // l = look around for red eye
			msg = client.readInt();
			if (msg == 'c') { // change sweeping direction
				goingLeft = !goingLeft;
				if (goingLeft) {
					dp.arcBackward(-55);
				}
				else {
					dp.arcBackward(55);
				}
			}
		}
		goingLeft = !goingLeft;
		dp.arc(goingLeft ? -10 : 10, -120, true);
		
		while (ccfront.getSample() != 13) {
			if (!dp.isMoving()) {
				goingLeft = !goingLeft;
				dp.arc(goingLeft ? -10 : 10, -120, true);
			}
		}
		dp.stop();
	}

	/**
	 * Gets the distance read from the UltraSonicSensor
	 * @return the normalized angle from an averaged sample.
	 */
	public float getDistance() {
		return normAngle(u.getAvgSample(2));
	}

	/**
	 * Rotates the motors so that the robot moves.
	 * @param lv
	 * @param rv
	 */
	public void move(int lv, int rv) {
		l.rotate(lv * -100000, true);
		r.rotate(rv * -100000, true);

		l.setSpeed(lv * maxspeed / 100);
		r.setSpeed(rv * maxspeed / 100);
	}

	/**
	 * Stops the motors form rotating
	 */
	public void stop() {
		l.setSpeed(1);
		r.setSpeed(1);
	}

	/**
	 * Rotates the motor hooked up to the arm. This arm then moves the bucket and empties it.
	 */
	public void emptyBucket() {
		m.setSpeed(maxspeed/10);
		m.rotateTo(-110);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.rotateTo(0);
	}
	
	public void rotateToA() {
		dp.arc(9.5, 40);
		
		// if colorID === 13 then emptybucket, rotate 180 degre
	}
	
	/**
	 * Rides a circle with radius r until a message is received from the afvuurbot.
	 * @param radius The radius of the circle to be followed.
	 */
	public void followCircleUntil(int radius, boolean circleToBot) {
		int arc = 55;
		boolean goingLeft = false;
		int msg;
		float colorSample = 0;
		dp.arcForward(-arc);
		do { // stop signal
			float sample = u.getAvgSample(5);
			
			if (circleToBot)
				colorSample = ccfront.getAvgSample(2);
			
			if (!(sample < 2)) { // handle infinity values
				sample = 2;
			}
			
			int newArc;
			
			if (circleToBot ?( sample < 0.9 && colorSample==-1) : sample<1.1) {
				int factor = circleToBot && (sample > 0.192) ? 90 : 120;
				newArc = -(int)(sample * factor);
			}
			else
				newArc = Math.abs(arc);
			
			if (Math.abs(newArc - arc) > 20) { // only update arc after a big change in distance
				arc = newArc;
				dp.arcForward(arc*radius);
			}
			
			System.out.println("Sample: " + colorSample);
			msg = client.readInt(); //READ
			// System.out.println(msg);
		} while (circleToBot ? colorSample != 13: msg != 'x');
		dp.stop();
	}
	
	public void followCircleUntilRedEye(int radius) {
		int arc = 55;
		boolean goingLeft = false;
		int readInt = 0; 
		dp.arcForward(-arc);
		while (ccfront.getSample() != 13) {
			float sample = u.getAvgSample(5);
			//System.out.println(sample);
			if (sample < 0.9*(float)radius && !goingLeft) {
				if (sample < 0.2)
					arc = (int)(sample * 100);
				else if (sample < 0.6)
					arc = (int)(sample * 95);
				
				dp.arcForward(-arc*radius);
				System.out.println("left " + 0.9*(float)radius);
				goingLeft=true;
			} else if (!(sample < 0.9*(float)radius) && goingLeft) {
				dp.arcForward(arc*radius);
				System.out.println("right " + 0.9*(float)radius);
				goingLeft=false;
			}
			System.out.println("S");
			readInt = client.readInt(); //READ
			System.out.println(readInt);
		}
		dp.stop();
	}
	
	private void terminateSignal() {
		while (client.readInt() != 42) {
		}
		client.writeInt(37);
	}

	/**
	 * 
	 * @param angle
	 * @return
	 */
	public float normAngle(float angle) {
		angle %= 360;
		if (angle > 180)
			angle -= 360;
		else if (angle <= -180)
			angle += 360;
		return angle;
	}
}
