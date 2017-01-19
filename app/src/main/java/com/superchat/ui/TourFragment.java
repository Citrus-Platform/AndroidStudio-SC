package com.superchat.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superchat.R;
import com.superchat.utils.UtilSetFont;

public class TourFragment extends Fragment{
	int mCurrentPage;
	int imageRes ;
	String mlang;
	ImageView imageView;
	TextView textView_header,textView_desc;
	LinearLayout main_linearlayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public static TourFragment newInstance(int imageRes, String lang) {
		TourFragment ret = new TourFragment();
		ret.MyConstructor_TourFragment(imageRes, lang);
		return ret;
	}

	public void MyConstructor_TourFragment(int imageRes, String lang){
		this.imageRes = imageRes ;
		mlang		  = lang;
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_tour_new, container,false);

		UtilSetFont.setFontMainScreen(v);

		imageView 	= (ImageView) v.findViewById(R.id.id_image);
		LoginFragmentTwoCheck(imageRes, mlang);
		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void LoginFragmentTwoCheck(int imageRes, String lang){
		if (imageRes == 0) {
			setImage(R.drawable.login_img);
		} else if (imageRes == 1) {
			setImage(R.drawable.login_img1);
		} else if (imageRes == 2) {
			setImage(R.drawable.login_img2);
		}
	}

	private void setImage(final int drawableId){
		imageView.setImageResource(drawableId);
	}
}
