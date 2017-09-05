package Socket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import CompressData.CompressData;
import Data.Pack;
import Data.Request;

public class ProcessPack extends Thread {

	Queue<Pack> queue;
	SendPackageData sendPackageData;
	ListenRequest listenRequest;
	private boolean isRun = true;


	public ProcessPack(SendPackageData sendPackageData) {
		queue = new LinkedList<Pack>();
		this.sendPackageData = sendPackageData;
		
	}

	public void addToQueue(Pack pack) {
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
				processPack(pack);
			}
		}
	}

	public void processPack(Pack pack) {
		byte request = pack.request;
		switch (request) {
		case Request.GET_FILE:
			processGetFile(pack);
			break;
		case Request.GET_IMG:
			processIMG(pack);
			break;
		case Request.CONNECT_ERROR:
			listenRequest.stopRun();
			sendPackageData.stopRun();
			stopRun();
			System.out.println("Ket noi bi loi");
			break;
		}
	}

	public void processGetFile(Pack pack) {
		String file = "Data/" + "file.txt";
		byte data[] = getDataFromFile(file);
		
		
		//gui goi package chua size cua file
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(a);
		try {
			out.writeInt(data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Pack packSizeData = new Pack(0, Request.NEW_FILE, a.toByteArray());
		sendPackageData.addDataToQueue(packSizeData);
	
		sendData(0, data, Request.FILE);
	}	
	
	public void processGetFileGP(Pack pack) {
		String file = "Data/" + "file.txt";
		byte data[] = getDataFromFile(file);
		
		
		byte[] compressData = null;
		try {
			compressData = CompressData.compress(data);
		} catch (IOException e) {
			System.out.println("Nen file bi loi");
		}
		
		
		//gui goi package chua size cua file
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(a);
		try {
			out.writeInt(compressData.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Pack packSizeData = new Pack(0, Request.NEW_FILE, a.toByteArray());
//		Pack packSizeData = new Pack(0, Request.NEW_FILE, tea.encrypt(a.toByteArray()));
		
		sendPackageData.addDataToQueue(packSizeData);
		
		ByteArrayOutputStream arrbyte = new ByteArrayOutputStream();
		arrbyte.write(compressData, 0, compressData.length);
		Pack pack2 = new Pack(0, Request.FILE, arrbyte.toByteArray());
//		Pack pack2 = new Pack(0, Request.FILE, tea.encrypt(arrbyte.toByteArray()));
		sendPackageData.addDataToQueue(pack2);
		
	}
	
	public void processIMGGP(Pack pack) {
		String file = "Image/" + pack.id + "_jpg.jpg";
		byte data[] = getDataFromFile(file);
		

		byte[] compressData = null;
		try {
			compressData = CompressData.compress(data);
		} catch (IOException e) {
			System.out.println("Nen file bi loi");
		}
		
		//gui goi package chua size cua file
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(a);
		try {
			out.writeInt(compressData.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Pack packSizeData = new Pack(pack.id, Request.NEW_IMG, a.toByteArray());
//		Pack packSizeData = new Pack(0, Request.NEW_IMG, tea.encrypt(a.toByteArray()));
		sendPackageData.addDataToQueue(packSizeData);

		ByteArrayOutputStream arrbyte = new ByteArrayOutputStream();
		arrbyte.write(compressData, 0, compressData.length);
		Pack pack2 = new Pack(pack.id, Request.IMG, arrbyte.toByteArray());
//		Pack pack2 = new Pack(0, Request.IMG, tea.encrypt(arrbyte.toByteArray()));
		sendPackageData.addDataToQueue(pack2);
		
	}
	
	
	public void processIMG(Pack pack) {
		String file = "Image/" + pack.id + "_jpg.jpg";
		byte data[] = getDataFromFile(file);
		
		//gui goi package chua size cua file
		ByteArrayOutputStream a = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(a);
		try {
			out.writeInt(data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Pack packSizeData = new Pack(pack.id, Request.NEW_IMG, a.toByteArray());
		sendPackageData.addDataToQueue(packSizeData);
		
		sendData(pack.id, data, Request.IMG);
	}

	
	public void sendData(int id, byte data[], byte request) {
		int leng = data.length;
		int offset = 0;
		
		if (leng > 50000) {
			while (leng > 1024) {
				ByteArrayOutputStream arrbyte = new ByteArrayOutputStream();
				arrbyte.write(data, offset, 50000);

				offset += 50000;
				leng -= 50000;

				Pack pack = new Pack(id, request, arrbyte.toByteArray());
				sendPackageData.addDataToQueue(pack);
			}
		}
		
		ByteArrayOutputStream arrbyte = new ByteArrayOutputStream();
		arrbyte.write(data, offset, leng);
		Pack pack = new Pack(id, request, arrbyte.toByteArray());
		sendPackageData.addDataToQueue(pack);	
	}

	public byte[] getDataFromFile(String fileName) {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream(fileName);
			int numberRead = 0;
			while (numberRead != -1) {
				byte[] arrbyte = new byte[1000];
				numberRead = fstream.read(arrbyte, 0, 1000);
//				System.out.println("ben gui:  " + arrbyte);

				data.write(arrbyte, 0, numberRead);
				if (numberRead < 1000)
					break;
			}
			System.out.println("numberRead:  " + numberRead);
			fstream.close();
			System.out.println("Dong file " + fileName);
		} catch (FileNotFoundException e) {
			System.out.println("Process: Loi mo file");
//			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Process: Loi doc file");
//			e.printStackTrace();
		}
		return data.toByteArray();
	}

}
