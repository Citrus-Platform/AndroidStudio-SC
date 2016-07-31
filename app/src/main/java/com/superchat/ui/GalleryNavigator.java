package com.superchat.ui;


import com.superchat.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GalleryNavigator extends View {
    private static int SPACING = 10;
    private static int RADIUS = 13;
    private int mSize = 20;
    private int mloaded = 0;
     
    private int mPosition = 0;
    private static final Paint mOnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;
    private static final Paint mOffPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;
    private static final Paint mToBeloadedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);;

    public GalleryNavigator(Context context) {
        super(context);
        mOnPaint.setColor(getResources().getColor(R.color.nav_sel));
        mOffPaint.setColor(Color.WHITE);
        mToBeloadedPaint.setColor(getResources().getColor(R.color.nav));
    }

    public GalleryNavigator(Context c, int size) {
        this(c);
        mSize = size;
    }

    public GalleryNavigator(Context context, AttributeSet attrs) {
        super(context, attrs);
        //mOnPaint.setColor(0xCC508043);
        mOnPaint.setColor(getResources().getColor(R.color.nav_sel));
        mOffPaint.setColor(Color.WHITE);
        mToBeloadedPaint.setColor(getResources().getColor(R.color.nav));
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	if(mSize > 1)
        for (int i = 0; i < mSize; ++i) {
            if (i == mPosition) {
                canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOnPaint);
            } else {
            	if(i < mloaded)
            		canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mOffPaint);
            	else
            		canvas.drawCircle(i * (2 * RADIUS + SPACING) + RADIUS, RADIUS, RADIUS, mToBeloadedPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize * (2 * RADIUS + SPACING) - SPACING, 2 * RADIUS);
    }
    
    public int getCurrentIndex(){
    	return mPosition ;
    }

//    @Override
//	public boolean isInEditMode() {
//		return false;
//	}

	public void setPosition(int id) {
        mPosition = id;
    }
	public void setLoadedSize(int mloadedL){
		mloaded = mloadedL;
	}
	public int getLoadedSize(){
		return mloaded;
	}
    public void setSize(int size) {
        mSize = size;
        mloaded = size ;
        if(mSize > 15)
        	SPACING = 1;
        else
        	SPACING = 16;
        	
    }
    public int getSize(){
    	return mSize ;
    }

    public void setPaints(int onColor, int offColor) {
        mOnPaint.setColor(onColor);
        mOffPaint.setColor(offColor);
    }

    public void setBlack() {
        setPaints(0xE6000000, 0x66000000);
    }

}