package com.example.socketdoadloadimgclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.DataFormatException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import CompressData.CompressData;
import Connect.ListenRequest;
import Connect.SendPackageData;
import Data.ItemsInfo;
import Data.Pack;
import Data.Request;
import TEA.TEA;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class ProcessPack extends AsyncTask<ArrayList<ItemsInfo>, Void, Void> {

	Queue<Pack> queue;
	SendPackageData sendPackageData;
	ListenRequest listenRequest;
	private boolean isRun = true;

	FileOutputStream fstream;
	int dataZize;

	ByteArrayOutputStream data;
	DataOutputStream outData;

	MainActivity root;
	ArrayList<ItemsInfo> arrItemps;
	

	public ProcessPack(SendPackageData sendPackageData, MainActivity root) {
		queue = new LinkedList<Pack>();
		this.sendPackageData = sendPackageData;
		this.root = root;
			
	}

	public void addToQueue(Pack pack) {
		synchronized (queue) {
			queue.add(pack);
			queue.notify();
		}
	}

	public void stopRun() {
		isRun = false;
	}

	public void addSendPackageData(SendPackageData sendPackageData) {
		this.sendPackageData = sendPackageData;
	}

	@Override
	protected Void doInBackground(ArrayList<ItemsInfo>... params) {
		arrItemps = params[0];
		while (isRun) {
			try {
				synchronized (queue) {
					while (queue.isEmpty())
						queue.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (queue.isEmpty() == false) {
				Pack pack = queue.poll();
				processPack(pack);
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		root._adapter.notifyDataSetChanged();
	}

	private void processPack(Pack pack) {
		byte request = pack.request;
		switch (request) {
		case Request.NEW_FILE:
			Log.i("Switch", "NEW_FILE");
			createNewData(pack);
			break;
		case Request.NEW_IMG:
			Log.i("Switch", "NEW_IMG");
			createNewIMG(pack);
			break;
		case Request.FILE:
			receveData(pack);
			break;
		case Request.IMG:
			receiveFile(pack);
			break;
		case Request.CONNECT_ERROR:
			listenRequest.stopRun();
			sendPackageData.stopRun();
			stopRun();
			System.out.println("Ket noi bi loi");
			break;
		}
	}

	private void createNewData(Pack pack) {
		try {
			
//			byte teaData[] = tea.decrypt(pack.arrByte);
//			ByteArrayInputStream a = new ByteArrayInputStream(teaData);
//			DataInputStream b = new DataInputStream(a);
//			dataZize = b.readInt();
			
			
			ByteArrayInputStream a = new ByteArrayInputStream(pack.arrByte);
			DataInputStream b = new DataInputStream(a);
			dataZize = b.readInt();

			data = new ByteArrayOutputStream();
			outData = new DataOutputStream(data);
		} catch (IOException e) {
			Log.i("Process", "Loi nhan data");
		}
	}

	private void receveData(Pack pack) {
		try {
			dataZize = dataZize - pack.sizeData;
			outData.write(pack.arrByte, 0, pack.sizeData);
			

//			byte teaData[] = tea.decrypt(pack.arrByte);
//			dataZize = dataZize - teaData.length;
//			outData.write(teaData, 0, teaData.length);
			
			
			
			if (dataZize == 0) {
				Log.i("Process", "Nhan data thanh cong");
				JSONArray arrJson = new JSONArray(data.toString());
				// Log.i("Process: json",arrJson.toString());
				processJsonData(arrJson);
			}
		} catch (IOException e) {
			Log.i("Process", "Loi ghi file");
		} catch (JSONException e) {
			Log.i("Process", "Parse json err");
			e.printStackTrace();
		}
	}

	private void receveDataGP(Pack pack) {
		try {
			
//			byte teaData[] = tea.decrypt(pack.arrByte);
//			pack.arrByte = teaData;
//			pack.sizeData = teaData.length;
//			outData.write(teaData, 0, teaData.length);
			
			byte[] compressData = null;
			try {
				compressData = CompressData.decompress(pack.arrByte);
//				pack.sizeData = compressData.length;
			} catch (IOException e) {
				System.out.println("Nen file bi loi");
			} catch (DataFormatException e) {
				System.out.println("Nen file bi loi");
				e.printStackTrace();
			}

			
			dataZize = dataZize - pack.sizeData;
			outData.write(compressData, 0, compressData.length);
			// outData.write(pack.arrByte, 0, pack.sizeData);
			if (dataZize == 0) {
				Log.i("Process", "Nhan data thanh cong");
				JSONArray arrJson = new JSONArray(data.toString());
				// Log.i("Process: json",arrJson.toString());
				processJsonData(arrJson);
			}
		} catch (IOException e) {
			Log.i("Process", "Loi ghi file");
		} catch (JSONException e) {
			Log.i("Process", "Parse json err");
			e.printStackTrace();
		}
	}

	private void processJsonData(JSONArray arrJson) {
		for (int i = 0; i < arrJson.length(); i++) {
			try {
				JSONObject temp = (JSONObject) arrJson.get(i);
				int id = temp.getInt("id");
				Log.i("Process ID", "" + id);
				ItemsInfo itemp = new ItemsInfo();
				itemp.setItemsInfo(temp.getInt("id"), temp.getString("title"),
						temp.getString("author"));
				arrItemps.add(itemp);

				// gui du lieu yeu cau package
				Pack pack = new Pack(id, Request.GET_IMG, null);
				sendPackageData.addDataToQueue(pack);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		publishProgress();
	}

	private void createNewIMG(Pack pack) {

		String fileName = "" + pack.id + "c_jpg.jpg";
		Log.i("Create IMG name", fileName);
		try {

//			byte teaData[] = tea.decrypt(pack.arrByte);
//			ByteArrayInputStream a = new ByteArrayInputStream(teaData);
//			DataInputStream b = new DataInputStream(a);
//			dataZize = b.readInt();

			ByteArrayInputStream a = new ByteArrayInputStream(pack.arrByte);
			DataInputStream b = new DataInputStream(a);
			dataZize = b.readInt();

			Log.i("Create IMG size: ", "" + dataZize);

			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + fileName);
			file.createNewFile();
			if (file.exists()) {
				fstream = new FileOutputStream(file);
				Log.i("FILE", "file created: " + file);
			} else {
				file.delete();
				file = new File(Environment.getExternalStorageDirectory()
						+ File.separator + fileName);
				file.createNewFile();
				fstream = new FileOutputStream(file);
				Log.i("FILE", "file rep created: " + file);
			}
		} catch (IOException e) {
			Log.i("Create New IMG", "eroor");
			// e.printStackTrace();
		}
	}

	public void receiveFile(Pack pack) {
		try {

			
			dataZize = dataZize - pack.sizeData;
			Log.i("Process receve data datasize: ", "" + dataZize);
			fstream.write(pack.arrByte, 0, pack.sizeData);
			if (dataZize == 0) {
				Log.i("Process", "Ghi file thanh cong");
				fstream.close();
				fstream = null;

				String fileName = "" + pack.id + "c_jpg.jpg";
				File file = new File(Environment.getExternalStorageDirectory()
						+ File.separator + fileName);
				FileInputStream fstreamInput = new FileInputStream(file);
				arrItemps.get(pack.id).bitmap = BitmapFactory
						.decodeStream(fstreamInput);
				Log.i("ReceverIMG Bitmap",
						"" + arrItemps.get(pack.id).bitmap.getWidth());
				publishProgress();
			}
		} catch (IOException e) {
			Log.i("Process", "Loi ghi file");
		}
	}

	public void receiveFileGP(Pack pack) {
		try {
//
//			byte teaData[] = tea.decrypt(pack.arrByte);
//			pack.arrByte = teaData;
//			pack.sizeData = teaData.length;
//			outData.write(teaData, 0, teaData.length);
			
			dataZize = dataZize - pack.sizeData;
			Log.i("Process receve data datasize: ", "" + dataZize);

			byte[] compressData = null;
			try {
				compressData = CompressData.decompress(pack.arrByte);
			} catch (IOException e) {
				System.out.println("Nen file bi loi");
			} catch (DataFormatException e) {
				System.out.println("Nen file bi loi");
				e.printStackTrace();
			}
			fstream.write(compressData, 0, compressData.length);
			
//			fstream.write(pack.arrByte, 0, pack.sizeData);
			if (dataZize == 0) {
				Log.i("Process", "Ghi file thanh cong");
				fstream.close();
				fstream = null;

				String fileName = "" + pack.id + "c_jpg.jpg";
				File file = new File(Environment.getExternalStorageDirectory()
						+ File.separator + fileName);
				FileInputStream fstreamInput = new FileInputStream(file);
				arrItemps.get(pack.id).bitmap = BitmapFactory
						.decodeStream(fstreamInput);
				Log.i("ReceverIMG Bitmap",
						"" + arrItemps.get(pack.id).bitmap.getWidth());
				publishProgress();

			}
		} catch (IOException e) {
			Log.i("Process", "Loi ghi file");
		}
	}

}
