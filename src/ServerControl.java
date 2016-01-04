import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerControl {
	public DataInputStream dataIn;
	private DataOutputStream dataOut;
	
	/**
	 * 
	 * A constructor which sets up the server side for the communication. 
	 */
	public ServerControl() {
		ServerSocket server;
		try {
			server = new ServerSocket(1994);
			
			Socket remoteClient = server.accept();
			dataIn = new DataInputStream(remoteClient.getInputStream());
			dataOut = new DataOutputStream(remoteClient.getOutputStream());
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
			return;
		}
	}

}
