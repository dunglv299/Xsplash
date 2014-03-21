package com.teusoft.facesplash.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.teusoft.facesplash.R;

public class StepFragment extends Fragment {
	ImageView mImageView;
	private int position;
	private int imageStep[] = { R.drawable.step1, R.drawable.step2,
			R.drawable.step3, R.drawable.step4 };

	public static StepFragment newInstance(int position) {
		StepFragment stepFragment = new StepFragment();
		stepFragment.position = position;
		return stepFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_step, container, false);
		mImageView = (ImageView) v.findViewById(R.id.mImageView);
		mImageView.setImageDrawable(getResources().getDrawable(
				imageStep[position]));
		return v;
	}
}
