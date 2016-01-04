import java.util.ArrayList;
import java.util.List;

import lejos.robotics.SampleProvider;

/**
 * An abstract control class for sensors. This class contains our implementations for getting a sample and an averaged sample.
 * 
 * @author Bob de Ruiter, Jochem Baaij, Sanna Dinh, Olaf Maltha, Jelle Hilbrands
 *
 */

public abstract class SensorControl {
	protected float[] sample;
	protected List<Float> sampleList = new ArrayList<Float>();
	protected SampleProvider distance;

	public float getSample(int i) {
		distance.fetchSample(sample, 0);
		return sample[i];
	}
	public float getAvgSample(int sampleSize) {
		sampleList.clear();
		for(int i = 0; i < sampleSize; i++) {
			distance.fetchSample(sample, 0);
			sampleList.add(sample[0]);
		}
		float sum = 0;
		for (float d : sampleList) 
			if (!Float.isInfinite(d)) sum += d; 
			else sum += 2;
		return sum / (float)sampleList.size();
	}
}
