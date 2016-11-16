package com.superchat.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Motobeans on 11/10/2016.
 */

public class YourActivity extends AppCompatActivity {

    private View mTouchOutsideView;

    private OnTouchOutsideViewListener mOnTouchOutsideViewListener;

    /**
     * Sets a listener that is being notified when the user has tapped outside a given view. To remove the listener,
     * call {@link #()}.
     * <p/>
     * This is useful in scenarios where a view is in edit mode and when the user taps outside the edit mode shall be
     * stopped.
     *
     * @param view
     * @param onTouchOutsideViewListener
     */
    public void setOnTouchOutsideViewListener(View view, OnTouchOutsideViewListener onTouchOutsideViewListener) {
        mTouchOutsideView = view;
        mOnTouchOutsideViewListener = onTouchOutsideViewListener;
    }

    public OnTouchOutsideViewListener getOnTouchOutsideViewListener() {
        return mOnTouchOutsideViewListener;
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // Notify touch outside listener if user tapped outside a given view
            if (mOnTouchOutsideViewListener != null && mTouchOutsideView != null
                    && mTouchOutsideView.getVisibility() == View.VISIBLE) {
                Rect viewRect = new Rect();
                mTouchOutsideView.getGlobalVisibleRect(viewRect);
                if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    mOnTouchOutsideViewListener.onTouchOutside(mTouchOutsideView, ev);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Interface definition for a callback to be invoked when a touch event has occurred outside a formerly specified
     * view. See {@link #setOnTouchOutsideViewListener(View, OnTouchOutsideViewListener).}
     */
    public interface OnTouchOutsideViewListener {

        /**
         * Called when a touch event has occurred outside a given view.
         *
         * @param view  The view that has not been touched.
         * @param event The MotionEvent object containing full information about the event.
         */
        public void onTouchOutside(View view, MotionEvent event);
    }
}
