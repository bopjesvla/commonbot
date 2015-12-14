import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerControl {
	public DataInputStream dataIn;
	private DataOutputStream dataOut;
	
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
			return;
		}
	}

}
