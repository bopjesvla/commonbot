import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;


public class ClientControl {
	public DataInputStream dataIn;
	private DataOutputStream dataOut;

	public ClientControl() {
		BrickInfo[] found;
		int i = 0;
		do {
			found = BrickFinder.find("b");
		}
		while (found.length == 0);
			
		Socket bekerbot;
		try {
			bekerbot = new Socket(found[0].getIPAddress(), 1994);
			dataIn = new DataInputStream(bekerbot.getInputStream());
			dataOut = new DataOutputStream(bekerbot.getOutputStream());
			
			String hallo = "hallo!";
			
			while (true) {
				dataOut.writeInt(6);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int readInt() {
		try {
			return dataIn.readInt();
		} catch (IOException e) {
			return -1;
		}
	}
	public void writeInt(int w) {
		try {
			dataOut.writeInt(w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
