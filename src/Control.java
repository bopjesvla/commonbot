import java.util.ArrayList;

import lejos.hardware.LocalBTDevice;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.robotics.navigation.Pose;

public class Control {
	private boolean goalReached = false;
//	private HeadControl hc;
//	private ColorControl ccl;
//	private ColorControl ccr;
	private GyroControl gc;
	
	private int orientation = 0;
	
	ArrayList<Pose> junctions = new ArrayList<Pose>(); 
	
	public Control() {
        
//        hc = new HeadControl(SensorPort.S2);
//        ccl = new ColorControl(SensorPort.S3);
//        ccr = new ColorControl(SensorPort.S4);
        
		LocalBTDevice n = new LocalBTDevice();
		System.out.println(n.getFriendlyName());
		if (n.getFriendlyName().equals("b")) {
			gc = new GyroControl(SensorPort.S2);
			BekerbotPoseControl bpc = new BekerbotPoseControl(new EV3LargeRegulatedMotor(MotorPort.A), new EV3LargeRegulatedMotor(MotorPort.B), new EV3LargeRegulatedMotor(MotorPort.D), gc);
		} else {
			AfvuurbotPoseControl apc = new AfvuurbotPoseControl(new EV3LargeRegulatedMotor(MotorPort.C), new EV3LargeRegulatedMotor(MotorPort.D));
		}
        
        //color.rotateTo(0);
        
//        mc.followStraightLineTwoSensors(ccl, ccr, (float)0.30, new Callable<Boolean>() {
//        	public Boolean call() {
//        		return true;
//        	}
//        });
        
        
        // draaitest
        
        /*for (int j=0; j < 10; j++) {
        	mc.move(50, -50);
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	mc.move(-50, 50);
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	System.out.println(gc.getLastSample());
        }*/
        //
        // mc.turnTo(120);
        /*mc.turnTo(80, 100, new Callable<Boolean>() {
        	public Boolean call() {
        		return true;
        	}
        });*/
        
        while(true)
        {
        	System.out.println(-gc.getAvgSample());
        	Thread.yield();
        }
        
        /*head.close();
        left.close();
        right.close();*/
	}
//	public boolean followStraightLine(SensorControl sc, float sThreshold) {
//		int t = 0;
//		boolean goleft = false;
//		
//		mc.move(100, 100);
//		
//		while (t < 10000) {
//			if(sc.getAvgSample() < sThreshold && goleft) {
//				mc.move(20, 100);
//				goleft = false;
//				t = 0;
//			}
//			else if (sc.getAvgSample() > sThreshold && !goleft) {
//				mc.move(100, 20);
//				goleft = true;
//				t = 0;
//			}
//			
//			//System.out.println( sc.getAvgSample() );
//			t += 1;
//		}
//		
//		mc.move(0, 0);
//		return goleft;
//	}
	public int getCardinal(int angle) {
		char directions[] = {'N', 'E', 'S', 'W'};
		int g = angle + 45;
		
		while (g < 0)
			g += 360;
		
		return (int)(g%360)/90;
	}
	public int getAngleFromCardinal(int card) {
		
		while (card < 0)
			card += 4;
		
		return (int)(card%4)*90;
	}
	public void detectJunctions() {
		
	}
}
