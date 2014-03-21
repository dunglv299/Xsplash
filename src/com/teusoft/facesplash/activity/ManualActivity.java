package com.teusoft.facesplash.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.teusoft.facesplash.R;
import com.teusoft.facesplash.customcontrol.StepFragmentAdapter;
import com.teusoft.facesplash.util.Utils;
import com.viewpagerindicator.CirclePageIndicator;

public class ManualActivity extends FragmentActivity {
	StepFragmentAdapter mAdapter;
	ViewPager mPager;
	CirclePageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual);
		((TextView) findViewById(R.id.howtouse_tv)).setTypeface(
				Utils.getTypeface(this), Typeface.BOLD);
		mAdapter = new StepFragmentAdapter(getSupportFragmentManager());
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

		final float density = getResources().getDisplayMetrics().density;
		mIndicator.setRadius(4 * density);
		// mIndicator.setStrokeColor(getResources().getColor(R.color.gray));
		// mIndicator.setStrokeWidth(20 * density);
	}

	public void onBack(View v) {
		finish();
	}
}
