package com.superchat.data.beans;

import android.widget.ImageView;

public class PhotoToLoad {

	public String id;
	public ImageView iv;

	public PhotoToLoad(ImageView imageview, String s) {
		id = s;
		iv = imageview;
	}
}
