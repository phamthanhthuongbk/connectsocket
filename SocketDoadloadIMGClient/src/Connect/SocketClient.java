package Connect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

public class SocketClient extends AsyncTask<Void, Void, Socket> {

	@Override
	protected void onPostExecute(Socket result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	@Override
	protected Socket doInBackground(Void... params) {

		Socket clientSocket = ServeConnect();
		return clientSocket;
	}
	
	public Socket ServeConnect() {
		Socket clientSocket = null;
		try {
			Log.i("Main", "Dang ket noi den server");
			System.out.println("Dang ket noi den server");
			clientSocket = new Socket("192.168.42.164", 5000);

			if (!clientSocket.isConnected()) {
				Log.i("Connect stt", "Socket Connection Not established");
			} else {
				Log.i("Connect stt", "Socket Connection established : "
						+ clientSocket.getInetAddress());
			}
			// arrItemps = new ArrayList<ItemsInfo>();

			Log.i("Main Activity","Thiet lap thong so cho Client");

		} catch (UnknownHostException e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (IOException e) {

			e.printStackTrace();
		}
		return clientSocket;
	}

}
