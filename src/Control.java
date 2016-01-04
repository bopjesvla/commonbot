import java.util.ArrayList;

import lejos.hardware.LocalBTDevice;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.robotics.navigation.Pose;

public class Control {
	ArrayList<Pose> junctions = new ArrayList<Pose>(); 
	
	public Control() {

		LocalBTDevice n = new LocalBTDevice();
		System.out.println(n.getFriendlyName());
		if (n.getFriendlyName().equals("b")) {
			BekerbotPoseControl bpc = new BekerbotPoseControl(new EV3LargeRegulatedMotor(MotorPort.A), new EV3LargeRegulatedMotor(MotorPort.B), new EV3LargeRegulatedMotor(MotorPort.D));
		} else {
			AfvuurbotPoseControl apc = new AfvuurbotPoseControl(new EV3LargeRegulatedMotor(MotorPort.C), new EV3LargeRegulatedMotor(MotorPort.D));
		}
	}
}
