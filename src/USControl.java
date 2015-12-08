

import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class USControl extends SensorControl {
	public USControl(Port port) {
		super(port);
		SensorModes eyes = new EV3UltrasonicSensor(port);
		distance = eyes.getMode("Distance");
		sample = new float[distance.sampleSize()];
	}
}
