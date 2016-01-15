import lejos.hardware.port.Port;


public class GreenRedControl extends ColorControl {

	public GreenRedControl(Port port) {
		super(port);
		
		distance = eyes.getMode("RGB");
		sample = new float[distance.sampleSize()];
	}
	@Override
	public float getSample() {
		distance.fetchSample(sample, 0);
		float value = sample[1] / (sample[0] + 0.1f);
		if (!(sample[1] < 1))
			value = 0;
		return value;
	}
}
