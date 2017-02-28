package com.superchat.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.superchat.CustomAppComponents.Activity.CustomAppCompatActivityViewImpl;
import com.superchat.CustomAppComponents.EventAudioCall;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Motobeans on 2/23/2017.
 */

public class MyAudioCallService extends Service {

    public static final int notify = 1000;  //interval between two services(Here Service run every 5 seconds)
    int count = 0;  //number of times service is display
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling

    private long establishedTime;
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {

        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task

        establishedTime = System.currentTimeMillis();
    }

    public void serviceStopped(){
        try {
            CustomAppCompatActivityViewImpl.callEnded();

            eventAudioCall.setCallEnded(true);
            eventAudioCall.setCallText("");
            fireEvent();

            mTimer.cancel();    //For Cancel Timer
            stopSelf();
        } catch(Exception e){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serviceStopped();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        serviceStopped();
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    long currentTime = System.currentTimeMillis();
                    // display toast
                    if(establishedTime > 0) {
                        String formatterTimeSpan = formatTimespan(currentTime - establishedTime);

                        eventAudioCall.setCallStarted(true);
                        eventAudioCall.setCallText(formatterTimeSpan);
                        fireEvent();
                    }
                }
            });
        }
    }

    EventAudioCall eventAudioCall = new EventAudioCall();

    private void fireEvent(){
        EventBus.getDefault().post(eventAudioCall);
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return "Call Ongoing (" +  String.format(Locale.US, "%02d:%02d", minutes, seconds) + ")";
    }

}
