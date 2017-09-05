package Connect;

import java.io.InputStream;

import utils.Log;

import com.example.socketdoadloadimgclient.ProcessPack;

import Data.Pack;

public class ListenRequest extends Thread {
	SendPackageData sendPackageData;
	InputStream inputStream;
	ProcessPack processPack;
	private boolean isRun = true;
	
	public ListenRequest(InputStream inputStream2, ProcessPack processPack)
	{
		this.inputStream = inputStream2;
		this.processPack = processPack;
	}
	
	public void addSendData(SendPackageData sendPackageData)
	{
		this.sendPackageData = sendPackageData;
	}	
	
	public void stopRun()
	{
		isRun = false;
	}
	
	@Override
	public void run() {
		while(isRun)
		{
			Pack pack = new Pack(inputStream);
			processPack.addToQueue(pack);
			Log.p("xxxxxxxxxxxxxxxx");
		}
	}
}
