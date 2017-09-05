package Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkEngine {
	private Socket socket;
	private InputStream in;
	private OutputStream out;

	private void connect() {
		try {
			socket = new Socket("", 1234);
			in = socket.getInputStream();
			out = socket.getOutputStream();

			startRequestThread();
			startReceiveThread();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void startRequestThread() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
			}
		}).start();
	}

	private void startReceiveThread() {

	}
}
