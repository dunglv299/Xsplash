package com.teusoft.facesplash.customcontrol;

import com.teusoft.facesplash.fragment.StepFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class StepFragmentAdapter extends FragmentPagerAdapter {

	public StepFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		return StepFragment.newInstance(position);
	}

	@Override
	public int getCount() {
		return 4;
	}

}
