package com.teusoft.facesplash.customcontrol;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teusoft.facesplash.R;

public class InfoAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<ItemAbout> listItems;

	public InfoAdapter(Context context, ArrayList<ItemAbout> listItems) {
		this.context = context;
		this.listItems = listItems;
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.info_single_item, null);
		}

		ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);

		imgIcon.setImageResource(listItems.get(position).getIcon());
		txtTitle.setText(listItems.get(position).getTitle());

		return convertView;
	}
}
