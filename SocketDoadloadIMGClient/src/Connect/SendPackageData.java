package Connect;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import Data.Pack;

public class SendPackageData extends Thread {
	Queue<Pack> queue;
	OutputStream outputStream;
	private boolean isRun = true;

	public SendPackageData(OutputStream outputStream) {
		this.outputStream = outputStream;
		queue = new LinkedList<Pack>();
	}

	public void addDataToQueue(Pack pack) {
		synchronized (queue) {
			queue.add(pack);
			queue.notify();
		}
	}

	public void stopRun()
	{
		isRun = false;
	}

	@Override
	public void run() {
		while (isRun) {
			try {
			synchronized (queue){
				while(queue.isEmpty())
						queue.wait();
					} 
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (queue.isEmpty() == false) {
				Pack pack = queue.poll();
				try {
					byte pacbyte[] = pack.toByteArray();
					outputStream.write(pacbyte, 0, pacbyte.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
