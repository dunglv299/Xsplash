package com.teusoft.facesplash.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.teusoft.facesplash.R;
import com.teusoft.facesplash.util.Utils;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		Typeface tf = Utils.getTypeface(this);
		((TextView) findViewById(R.id.about_tv)).setTypeface(tf, Typeface.BOLD);
	}

	public void onBack(View v) {
		finish();
	}
}
