import java.util.concurrent.Callable;

import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.geometry.Point;
import lejos.robotics.navigation.Pose;

public class AfvuurbotPoseControl {
	private RegulatedMotor c, d;
	private GyroControl g;
	USControl u;
	int maxspeed;
	int ltacho, rtacho;
	float gcoffset = 0;
	Pose pose = new Pose();
	int orientation = 0;
	ServerControl server;
	float gyro;
	
	// MapControl map = new MapControl(this);
	
	public AfvuurbotPoseControl(RegulatedMotor c, RegulatedMotor d) {
		this.c = c;
		this.d = d;
		this.u = new USControl(SensorPort.S4);
		this.g = g;
		
		server = new ServerControl();
		
		maxspeed = Math.min((int) c.getMaxSpeed(), (int) d.getMaxSpeed());
		
		// c.setSpeed(maxspeed);
		// d.setSpeed(maxspeed);
		
		ltacho = c.getTachoCount();
		rtacho = d.getTachoCount();
		
		cycle();
	}
	
	public void cycle() {
		rotateToRandom();
		int r = sendRandomRadius();
		System.out.println(r);
		float distance = 0;
		int correctSamples = 0;
		do {
			distance = u.getSample(0);
			System.out.println(distance);
			if (distance > 0.30 * r && distance < 1.20 * r)
				correctSamples++;
			else
				correctSamples = 0;
		} while (correctSamples < 30);
		server.writeInt(5);
		shoot(distance);
	}
	
	public void rotateToRandom() {
		int rotation = (int)(Math.random() * 360);
		d.rotateTo(rotation);
	}
	
	public int sendRandomRadius() {
		int radius = (int)(Math.random() * 0) + 1;
		server.writeInt(radius);
		return radius;
	}
	
	public void shoot(float distance) {
		c.setSpeed(maxspeed*80/100);
		for (int i = 0; i < 10; i++) {
			int r = (int) (4000.0 * distance * i) + 4000;
			System.out.println(r);
			
			c.rotateTo(r);
			c.setSpeed(maxspeed*80/100);
			c.rotateTo(0);
		}
	}
	
	public float getGyro() {
		float gyr = (-g.getAvgSample() - gcoffset);
		
		return normAngle(gyr);
	}
	public void move(int lv, int rv) {
		getPose();
		
		c.rotate(lv*-100000, true);
		d.rotate(rv*-100000, true);
		
		c.setSpeed(lv*maxspeed/100);
		d.setSpeed(rv*maxspeed/100);
	}
	public void stop() {
		c.setSpeed(1);
		d.setSpeed(1);
	}
	private Pose getPose() {
		int ls = -c.getTachoCount() - ltacho;
		int rs = -d.getTachoCount() - ltacho;
		
		float newgyro = normAngle(getGyro());
		float dif = normAngle(newgyro - gyro);
		
		pose.arcUpdate((ls+rs)/2, dif); // (ls+rs)/2, g.getLastSample() - gyro
		pose.setHeading(getGyro());
		//System.out.println(dif);
		
		ltacho = -c.getTachoCount();
		rtacho = -d.getTachoCount();
		gyro = newgyro;
		
		//System.out.println(pose);
		return pose;
	}
	public void turnToRandomAngle() {
		double rndangle=Math.random()*360;
		System.out.println(rndangle);
		turnTo(rndangle);
	}
	public void turnTo(double e) {
		d.rotateTo((int)(-15*180*e/360));
		//stop();
	}
	public void detectJunctions(SensorControl scl, SensorControl scr,  float sThreshold, boolean clockwise, int v) {// doe het
		Pose startpose = getPose();
		
		int found = 0;
		
		if (clockwise)
			move(v, -v);
		else
			move(-v, v);
		
		while (Math.abs(startpose.getHeading() - getGyro()) > 10 && found > 0) {
			if (scl.getSample(0) - scr.getSample(0) < sThreshold)
				getPose().getHeading();
			
			
		}
		
		stop();
	}
	public void followStraightLineTwoSensors(SensorControl scl, SensorControl scr, float sThreshold, Callable<Boolean> func) throws Exception {
		int t = 0;
		char dir = 'l';
		Pose lastPose = getPose();
		
		move(-10, 25);
		
		float rightoffset = -2;
		float leftoffset = -2;
		
		while (true) {
			float lsample = scl.getSample(0);
			float rsample = scr.getSample(0);
			float dif = lsample - rsample;
			float avg = (lsample + rsample)/2;
			
			float angledif = getGyro() - orientation;
			
			if (dif < -sThreshold && !(dir == 'l')) {
				if (normAngle(angledif) < -10) { // ziet afslag
					System.out.println("lbocht");
					//Point p = map.onJunction(getPose().getLocation());
					Point p = new Point(0,1);
					pose.setLocation(p);
					System.out.println(p);
					
					
					move(-15, 25);
					dir = 'l';
				}
				else {
					leftoffset = normAngle(angledif);
					gcoffset = (float)0.9*gcoffset + (float)0.05*(rightoffset+leftoffset);
					move(-10, 25);
					dir = 'l';
				}
			}
			else if (dif > sThreshold && !(dir == 'r')) {
				if (normAngle(angledif) > 10) { // ziet afslag
					System.out.println("rbocht");
					//System.out.println(angledif);
					//turnTo(0, 30);
					move(25, -15);
					dir = 'r';
				}
				else
				{
					rightoffset = normAngle(angledif);
					gcoffset = (float)0.9*gcoffset + (float)0.05*(rightoffset+leftoffset);
					move(25, -10);
					dir = 'r';
				}
			}
			else if (avg > sThreshold && !(dir == 'f')) {
				//move(15, 15);
				//dir = 'f';
			}
			else {
				//detectJunctions();
			}
			/*if (!func.call())
				return;*/
			
			/*if (lsample < sThreshold && rsample > sThreshold && !(dir == 'l')) {
				move(20, 0);
				dir = 'l';
				t = 0;
			}
			else if (rsample < sThreshold && lsample > sThreshold && !(dir == 'r')) {
				move(0, 20);
				dir = 'r';
				t = 0;
			}
			else if (((rsample >= sThreshold && lsample >= sThreshold) || (rsample < sThreshold && lsample < sThreshold)) && !(dir == 'f')) {
				move(20, 20);
				dir = 'f';
				t = 0;
			}
			System.out.println( dir );
			System.out.println( dif );*/
			t += 1;
		}
		
		//move(0, 0);
		//return goleft;
	}
	public float getCardinal(float angle) {
		char directions[] = {'N', 'E', 'S', 'W'};
		
		angle = (float)((int)((angle + 45) / 90)*90);
		
		return normAngle(angle);
	}
	public float normAngle(float angle) {
		angle %= 360;
		if (angle > 180)
			angle -= 360;
		else if (angle <= -180)
			angle += 360;
		
		return angle;
	}
}
