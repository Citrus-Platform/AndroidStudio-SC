package com.superchat.widgets;

import com.superchat.interfaces.OnSwipeListener;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyGestureListener  extends SimpleOnGestureListener {

	   private static final int MIN_DISTANCE = 30;
	   private static final String TAG = "MyGestureListener";
	   private RelativeLayout backLayout;
	   private LinearLayout frontLayout;
	   private Animation inFromRight,outToRight,outToLeft,inFromLeft;
	   private OnSwipeListener iSwipeListner = null;

	   public MyGestureListener(Context ctx,View convertView, OnSwipeListener mSwipeListner) {
		   this.iSwipeListner = mSwipeListner;
	   }

	   @Override
	   public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	         float velocityY) {
	      float diffX = e2.getX() - e1.getX();
	      float diffY = e2.getY() - e1.getY();
	      if (Math.abs(diffX) > Math.abs(diffY)) {
	         if (Math.abs(diffX) > MIN_DISTANCE) {
	            if(diffX<0){
	            	iSwipeListner.swipeLeft();
	               Log.v(TAG, "Swipe Right to Left");
	            }else{
	            	iSwipeListner.swipeRight();
	               Log.v(TAG, "Swipe Left to Right");
	            }
	         }
	      }

	      return true;
	   }
	   
	}