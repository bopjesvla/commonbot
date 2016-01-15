import lejos.hardware.port.Port;


public class GreyControl extends RGBControl {

	public GreyControl(Port port) {
		super(port, 0);
		
		distance = eyes.getMode("RGB");
		sample = new float[distance.sampleSize()];
	}
	public float getSample() {
		distance.fetchSample(sample, 0);
		float value = (sample[0] + sample[1] + sample[2]) / 3;
		if (!(value < 1))
			value = 0;
		return value;
	}
}
