import java.io.IOException;
import java.util.concurrent.Callable;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;

public class BekerbotPoseControl {
	private RegulatedMotor l, r, m;
	private GyroControl g;
	private USControl u;
	private ServerControl s;
	int maxspeed;
	int ltacho, rtacho;
	float gcoffset = 0;
	
	Pose pose = new Pose();
	int orientation = 0;
	float gyro;
	float circle = 0;
	DifferentialPilot dp;
	SensorControl ccfront;
	SensorControl ccball;

	public BekerbotPoseControl(RegulatedMotor l, RegulatedMotor r, RegulatedMotor m, GyroControl g) {
		this.l = l;
		this.r = r;
		this.m = m;
		this.g = g;
		this.u = new USControl(SensorPort.S4);
		
		System.out.println("uscontrol gevonden");
		
		this.s = new ServerControl();
		
		System.out.println("server gestart");

		maxspeed = Math.min((int) l.getMaxSpeed(), (int) r.getMaxSpeed());

		ltacho = l.getTachoCount();
		rtacho = r.getTachoCount();
		
		ccfront = new ColorControl(SensorPort.S3);
		ccball = new RedControl(SensorPort.S1);

		gyro = getGyro();
		dp = new DifferentialPilot(5.6f, 13.75f, l, r);
		int i = 0;
		while (i++<3)
			this.followCircleUntil(270+i*270,1);
		
		System.out.println("cirkel gevolgd");
		
		// this.emptyBucket();
	}

	public float getGyro() {
		float gyr = g.getAvgSample() - gcoffset;

		return normAngle(gyr);
	}
	
	public float getDistance() {
		return normAngle(u.getAvgSample());
	}

	public void move(int lv, int rv) {
		getPose();

		l.rotate(lv * -100000, true);
		r.rotate(rv * -100000, true);

		l.setSpeed(lv * maxspeed / 100);
		r.setSpeed(rv * maxspeed / 100);
	}

	public void stop() {
		l.setSpeed(1);
		r.setSpeed(1);
	}

	private Pose getPose() {
		int ls = -l.getTachoCount() - ltacho;
		int rs = -r.getTachoCount() - ltacho;

		float newgyro = normAngle(getGyro());
		float dif = normAngle(newgyro - gyro);

		pose.arcUpdate((ls + rs) / 2, dif); // (ls+rs)/2, g.getLastSample() -
											// gyro
		pose.setHeading(getGyro());
		// System.out.println(dif);

		ltacho = -l.getTachoCount();
		rtacho = -r.getTachoCount();
		gyro = newgyro;

		// System.out.println(pose);
		return pose;
	}
	
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
	
	public void goToA() {
		dp.rotate(90);
		
		
		
		// if colorID === 13 then emptybucket, rotate 180 degre
	}
	
	public void followCircleUntil(float toAngle, int r) {
		toAngle = normAngle(toAngle);
		boolean goingLeft = false;
		float v = l.getMaxSpeed()/2;

		dp.arcForward(-55);
		float j;
		
		while ((j = Math.abs(normAngle(toAngle - getGyro()))) > 5 && s.readInt() != 5) {
			float sample = u.getSample(0);
			System.out.println(sample);
			if (sample < 0.9*(float)r/10 && !goingLeft) {
				dp.arcForward(-55*r);
				goingLeft=true;
			} else if (!(sample < 0.9*(float)r/10) && goingLeft) {
				dp.arcForward(55*r);
				goingLeft=false;
			}
		}
		
//		while (Math.abs(normAngle(toAngle - getGyro())) > 2) {
//			float sample = cc.getSample(0);
//			System.out.println(sample);
//			if (sample < 0.01 && !goingLeft) {
//				dp.arcForward(-5);
//				goingLeft=true;
//			} else if (sample >= 0.01 && goingLeft) {
//				dp.arcForward(35);
//				goingLeft=false;
//			}
//		}
		dp.stop();
		
//		while (Math.abs(normAngle(toAngle - getGyro())) > 2) {
//			float dif = normAngle(toAngle - getGyro());
//			// if (dif < 0) turnAround();
//			if (goingLeft > 0)
//				move(v * 3 * (int) dif / 180 + (int) (0.5 * v), -v * 3
//						* (int) dif / 180 - (int) (0.5 * v));
//			else
//				move(v * 3 * (int) dif / 180 - (int) (0.5 * v), -v * 3
//						* (int) dif / 180 + (int) (0.5 * v));

		
	}

	public void detectJunctions(SensorControl scl, SensorControl scr,
			float sThreshold, boolean clockwise, int v) {// doe het
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

	public void followStraightLineTwoSensors(SensorControl scl,
			SensorControl scr, float sThreshold, Callable<Boolean> func)
			throws Exception {
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
			float avg = (lsample + rsample) / 2;

			float angledif = getGyro() - orientation;

			if (dif < -sThreshold && !(dir == 'l')) {
				if (normAngle(angledif) < -10) { // ziet afslag
					System.out.println("lbocht");
//					Point p = map.onJunction(getPose().getLocation());
//					pose.setLocation(p);
//					System.out.println(p);

					move(-15, 25);
					dir = 'l';
				} else {
					leftoffset = normAngle(angledif);
					gcoffset = (float) 0.9 * gcoffset + (float) 0.05
							* (rightoffset + leftoffset);
					move(-10, 25);
					dir = 'l';
				}
			} else if (dif > sThreshold && !(dir == 'r')) {
				if (normAngle(angledif) > 10) { // ziet afslag
					System.out.println("rbocht");
					// System.out.println(angledif);
					// turnTo(0, 30);
					move(25, -15);
					dir = 'r';
				} else {
					rightoffset = normAngle(angledif);
					gcoffset = (float) 0.9 * gcoffset + (float) 0.05
							* (rightoffset + leftoffset);
					move(25, -10);
					dir = 'r';
				}
			} else if (avg > sThreshold && !(dir == 'f')) {
				// move(15, 15);
				// dir = 'f';
			} else {
				// detectJunctions();
			}
			/*
			 * if (!func.call()) return;
			 */

			/*
			 * if (lsample < sThreshold && rsample > sThreshold && !(dir ==
			 * 'l')) { move(20, 0); dir = 'l'; t = 0; } else if (rsample <
			 * sThreshold && lsample > sThreshold && !(dir == 'r')) { move(0,
			 * 20); dir = 'r'; t = 0; } else if (((rsample >= sThreshold &&
			 * lsample >= sThreshold) || (rsample < sThreshold && lsample <
			 * sThreshold)) && !(dir == 'f')) { move(20, 20); dir = 'f'; t = 0;
			 * } System.out.println( dir ); System.out.println( dif );
			 */
			t += 1;
		}

		// move(0, 0);
		// return goleft;
	}

	public float getCardinal(float angle) {
		char directions[] = { 'N', 'E', 'S', 'W' };

		angle = (float) ((int) ((angle + 45) / 90) * 90);

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
