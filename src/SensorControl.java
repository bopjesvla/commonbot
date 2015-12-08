

import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public abstract class SensorControl {
	protected float[] sample;
	private int sampleSize;
	protected SampleProvider distance;
	
	public SensorControl(Port port) {
		
	}
	public float getSample(int i) {
		distance.fetchSample(sample, 0);
		return sample[i];
	}
	public float getAvgSample() {
		distance.fetchSample(sample, 0);
		float sum = 0;
		for (float d : sample) if (!Float.isInfinite(d)) sum += d; else sum += 1;
		
		return sum / (float)sample.length;
	}
}
