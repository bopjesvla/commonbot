import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;

public class ColorControl extends SensorControl {
	protected EV3ColorSensor eyes;
	
	public ColorControl(Port port) {
		
		eyes = new EV3ColorSensor(port);
		distance = eyes.getMode("ColorID");
		sample = new float[distance.sampleSize()];
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