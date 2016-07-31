package com.superchat.utils;

import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public final class ThumbnailImage {
		public static  Map<String, ThumbnailImage> cache = new HashMap<String, ThumbnailImage>();
		public static  Vector<Bitmap> cacheToRecycle = new Vector<Bitmap>();
		public String mName;
		public int mIndex;
		public byte mOnOffLine;
		public Bitmap mBitmap;
		public String mStatus;
		public String mContent;
		public String mTime;
		public String mUserName;
		public long time;
		public ThumbnailImage(String name, int index) {
			this.mName = name;
			this.mIndex = index;
			
		}
	}
