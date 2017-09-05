package Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[] args) {
		ServerSocket serverSocket;

		try {
			serverSocket = new ServerSocket();
			InetSocketAddress in = new InetSocketAddress("192.168.42.164", 5000);
			serverSocket.bind(in);

			if (!serverSocket.isBound()) {
				System.out.println("Sever Socket not Bounded...");
			} else {
				System.out.println("Server Socket bounded to Port : "
						+ serverSocket.getLocalPort());
				InetAddress inet = serverSocket.getInetAddress();
				System.out.println(inet.getHostAddress().toString());
			}
			
			while (true) {
				System.out.println("Dang lang nghe tu client");
				Socket clientSocket = serverSocket.accept();

				if (!clientSocket.isConnected())
					System.out.println("Client Socket not Connected...");
				else {

					System.out.println("Client Socket Connected : "
							+ clientSocket.getInetAddress());
					try {

						OutputStream outputStream = clientSocket.getOutputStream();
						InputStream inputStream = clientSocket.getInputStream();
						
						SendPackageData sendPackageData = new SendPackageData(outputStream);
						ProcessPack processPack = new ProcessPack(sendPackageData);
						ListenRequest listenRequest = new ListenRequest(inputStream, processPack);
						//process bao cho listen biet luc ket noi bi ngat dot ngot nen phai add tham so listen vao
						processPack.listenRequest = listenRequest; 
						
						listenRequest.start();
						processPack.start();
						sendPackageData.start();
						
					} catch (IOException e) {
						System.out.println("Eroooooooooooooooooooooooooooo");
						e.printStackTrace();
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
