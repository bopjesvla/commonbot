

import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

public class RGBControl extends ColorControl {
	private int color;
	
	public RGBControl(Port port, int color) {
		super(port);
		
		this.color = color;
		distance = eyes.getMode("RGB");
		sample = new float[distance.sampleSize()];
	}
	public float getSample() {
		distance.fetchSample(sample, 0);
		float value = sample[color] / (sample[0] + sample[1] + sample[2] + 0.2f);
		if (!(value < 1))
			value = 0;
		return value;
	}
}

// ccball
//
// colorid: 6/7
// rgb: 0.19/0.32
// red: 0.06/0.08
// 
// ccfront
// 
// colorid: niks/iets/oog -1/7/13