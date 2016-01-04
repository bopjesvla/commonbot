import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;

/**
 * The control class for the client side of the communication protocol.
 * 
 * @author Bob de Ruiter, Jochem Baaij, Sanna Dinh, Olaf Maltha, Jelle Hilbrands
 *
 */
public class ClientControl {
	public DataInputStream dataIn;
	private DataOutputStream dataOut;

	/**
	 * A constructor which sets up the client side for the communication. 
	 */
	public ClientControl() {
		BrickInfo[] found;
		int i = 0;
		do {
			found = BrickFinder.find("a");
		}
		while (found.length == 0);
			
		Socket bekerbot;
		try {
			bekerbot = new Socket(found[0].getIPAddress(), 1994);
			dataIn = new DataInputStream(bekerbot.getInputStream());
			dataOut = new DataOutputStream(bekerbot.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * A function for reading an Integer from the server
	 * @return The read integer
	 */
	public int readInt() {
		try {
			if(dataIn.available() >= 4) {
				try {
					return dataIn.readInt();
				} 
				catch (IOException e) {
					return -1;
				} catch (Exception e) {
					e.printStackTrace();
					return -2;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * A function for writing an Integer to the server
	 * @param w The write Integer
	 */
	public void writeInt(int w) {
		try {
			dataOut.writeInt(w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Waits for an Integer to be read and only continues if said Integer was actually read.
	 * @return The read Integer
	 */
	public int waitForInt() {
		int r = -1;
		do {
			r = readInt();
		} while (r < 0);
		return r;
	}
}
