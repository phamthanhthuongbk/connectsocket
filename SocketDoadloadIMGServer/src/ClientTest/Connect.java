package ClientTest;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import Data.Pack;
import Data.Request;
public class Connect {
	private Socket clientSocket;
	DataInputStream inputStream;
	OutputStream outputStream;

	public Connect() {
		try {

			clientSocket = new Socket("192.168.42.164", 5000);

			if (!clientSocket.isConnected()) {
				System.out.println("Socket Connection Not established");
			} else {
				System.out.println("Socket Connection established : "
						+ clientSocket.getInetAddress());
			}

			System.out.println("Thiet lap thong so cho Client");
			outputStream = clientSocket.getOutputStream();
			inputStream =new DataInputStream(clientSocket.getInputStream());
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendRequest()
	{
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Pack pack = new Pack(0, Request.GET_FILE, null);
		System.out.println("data : "+pack);
		try {
			outputStream.write(pack.toByteArray(), 0, pack.sizeData + 9);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void receve()
	{
		while(true){
			byte _byte;
			try {
				_byte = inputStream.readByte();
				System.out.println("byte: "+_byte);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

}
