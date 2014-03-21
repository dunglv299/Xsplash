package com.teusoft.facesplash.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.teusoft.facesplash.R;
import com.teusoft.facesplash.customcontrol.InfoAdapter;
import com.teusoft.facesplash.customcontrol.ItemAbout;
import com.teusoft.facesplash.util.Utils;

public class InfoActivity extends Activity {
	private TypedArray menuIcons;
	private String[] menuTitles;
	private ArrayList<ItemAbout> listItems;
	private InfoAdapter adapter;
	private ListView listView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		Typeface tf = Utils.getTypeface(this);
		// free version
		((TextView) findViewById(R.id.facewank_tv)).setTypeface(tf,
				Typeface.BOLD);
		listView1 = (ListView) findViewById(R.id.listView1);
		menuIcons = getResources().obtainTypedArray(R.array.list_icons);
		menuTitles = getResources().getStringArray(R.array.list_title);
		// adding nav drawer items to array
		listItems = new ArrayList<ItemAbout>();
		listItems.add(new ItemAbout(menuTitles[0], menuIcons.getResourceId(0,
				-1)));
		listItems.add(new ItemAbout(menuTitles[1], menuIcons.getResourceId(1,
				-1)));
		listItems.add(new ItemAbout(menuTitles[2], menuIcons.getResourceId(2,
				-1)));
		adapter = new InfoAdapter(this, listItems);
		listView1.setAdapter(adapter);

		// Recycle the typed array
		menuIcons.recycle();
		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> myAdapter, View myView,
					int position, long mylng) {
				Intent i;
				switch (position) {
				case 0:
					i = new Intent(InfoActivity.this, AboutActivity.class);
					startActivity(i);
					break;
				case 1:
					i = new Intent(InfoActivity.this, ManualActivity.class);
					startActivity(i);
					break;

				case 2:
					i = new Intent(InfoActivity.this, UpgradeActivity.class);
					startActivity(i);
					break;

				default:
					break;
				}
			}
		});
	}

	public void onBack(View v) {
		finish();
	}
}
