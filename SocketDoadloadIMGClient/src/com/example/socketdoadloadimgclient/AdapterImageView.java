package com.example.socketdoadloadimgclient;

import java.util.List;

import Data.ItemsInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterImageView extends ArrayAdapter<ItemsInfo> {

	private Context context;
	private List<ItemsInfo> listItems;

	public AdapterImageView(Context context, List<ItemsInfo> objects) {
		super(context, R.layout.list, objects);
		this.context = context;
		this.listItems = objects;
		// Log.i("In adapter:  ", "" + objects.toString());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = inflater.inflate(R.layout.list, null, false);
			TextView tvTitle = (TextView) rowView.findViewById(R.id.textView1);

			tvTitle.setText(listItems.get(position).title);

		}
		if (listItems.get(position).bitmap != null) {
			ImageView ivImage = (ImageView) rowView
					.findViewById(R.id.imageView1);
			Bitmap ima = listItems.get(position).bitmap;
			ivImage.setImageBitmap(ima);
		}
		if (listItems.get(position).title != null) {
			TextView tvTitle = (TextView) rowView.findViewById(R.id.textView1);
			tvTitle.setText(listItems.get(position).title);
		}

		return rowView;

	}

}