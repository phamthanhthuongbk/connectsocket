package com.example.socketdoadloadimgclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


import Connect.ListenRequest;
import Connect.SendPackageData;
import Connect.SocketClient;
import Data.ItemsInfo;
import Data.Pack;
import Data.Request;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	public ArrayAdapter<?> _adapter;
	ArrayList<ItemsInfo> listItems;
	ListView listView;
	
	int height;
	int width;

	Socket clientSocket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;
		
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.list);

		listItems = new ArrayList<ItemsInfo>();
		_adapter = new AdapterImageView(MainActivity.this,listItems);
		listView.setAdapter(_adapter);
		
		System.out.println("Connect Server");
		SocketClient connect = new SocketClient();
		connect.execute();
		
		try {
			clientSocket = connect.get();
			InputStream inputStream = clientSocket.getInputStream();
			
			OutputStream outputStream = clientSocket.getOutputStream();
			
			SendPackageData sendPackageData = new SendPackageData(outputStream);
			ProcessPack processPack = new ProcessPack(sendPackageData, this);
			ListenRequest listenRequest = new ListenRequest(inputStream, processPack);
			//process bao cho listen biet luc ket noi bi ngat dot ngot nen phai add tham so listen vao
			processPack.listenRequest = listenRequest; 
			
			listenRequest.start();
			processPack.execute(listItems);
			sendPackageData.start();
			
			Pack pack = new Pack(0, Request.GET_FILE, null);
			sendPackageData.addDataToQueue(pack);
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("errrrrrrrrrrrrrrrrrrrrrrrr");
			e.printStackTrace();
		} 
		
		listView.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) { 
		    	Log.i("thuong", ""+position);
		    	 	
		    	
		    	initiatePopupWindow(listItems.get(position));
		    	
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	private PopupWindow pwindo;

	private void initiatePopupWindow(ItemsInfo item) { 
	try { 
	// We need to get the instance of the LayoutInflater 
		LayoutInflater inflater = (LayoutInflater) MainActivity.this 
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		View layout = inflater.inflate(R.layout.popup,(ViewGroup)
	
				
		findViewById(R.id.popup_element)); 
		pwindo = new PopupWindow(layout, width/2, height, true); 
		pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
	
		ImageView imgview = (ImageView) layout.findViewById(R.id.ppimg);
		imgview.setImageBitmap(item.bitmap);
		
		TextView textview = (TextView) layout.findViewById(R.id.text);
		textview.setText(item.author);
		
		Log.i("Athor", item.author);
		
		Bitmap bitmap = item.bitmap;
		if(bitmap == null)
			Log.i("Bitmap ", "Loi");
		else
		{
			Log.i("Bitmap W:", " "+ bitmap.getWidth());
			Log.i("Bitmap H:", " "+ bitmap.getHeight());
		}
		
		
		Button btnClosePopup = (Button) layout.findViewById(R.id.dismiss); 
		btnClosePopup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pwindo.dismiss();		
			}
		});
		
		} catch (Exception e) { 
		e.printStackTrace(); 
		}

	}
}
