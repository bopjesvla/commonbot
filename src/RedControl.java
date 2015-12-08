

import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

public class RedControl extends ColorControl {
	public RedControl(Port port) {
		super(port);
		
		distance = eyes.getMode("Red");
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