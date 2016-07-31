package com.superchat.ui;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	private Context mContext;
	public ViewPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return 3;
	}
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
//		switch (position) {
//		case 0:
//			LoginFragment sFragment = new LoginFragment();
////			sFragment.SetContext(mContext);
//			return sFragment;			
//		case 1:
//			SignUpFragment rFragment = new SignUpFragment();
////			rFragment.SetContext(mContext);
//			return rFragment;
//		default:
			return null;
//		}
	}	 
}
