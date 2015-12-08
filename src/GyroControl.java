

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.SensorModes;

public class GyroControl extends SensorControl {
	public GyroControl(Port port) {
		super(port);
		
		EV3GyroSensor eyes = new EV3GyroSensor(port);
		distance = eyes.getAngleMode();
		sample = new float[distance.sampleSize()];
	}
}