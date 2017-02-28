package com.superchat.CustomAppComponents.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.superchat.CustomAppComponents.EventAudioCall;
import com.superchat.CustomAppComponents.Interface.CustomAppActivityView;
import com.superchat.R;
import com.superchat.ui.CallScreenActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static android.R.attr.data;

public class CustomAppCompatActivityViewImpl extends CustomAppActivityImpl implements CustomAppActivityView {

    private static String SINCH_CALL_ID = "";

    private LinearLayout llInflatorContainer;
    private LinearLayout llCallGoingOn;
    private int layoutView;
    private TextView tvCallGoingOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setContentView(int view) {
        layoutView = view;

        initializeView();
    }

    public void initializeView() {
        super.setContentView(R.layout.custom_actionbaractivity_with_back);
        llInflatorContainer = (LinearLayout) findViewById(R.id.llInflatorContainer);
        llCallGoingOn = (LinearLayout) findViewById(R.id.llCallGoingOn);
        tvCallGoingOn = (TextView) findViewById(R.id.tvCallGoingOn);

        View inflatingInfo = getLayoutInflater().inflate(layoutView, llInflatorContainer, false);
        llInflatorContainer.addView(inflatingInfo);

        handleOnGoingCallUI();
    }

    private void handleOnGoingCallUI(){
        try{
            if(isCallOngoing()){
                llCallGoingOn.setVisibility(View.VISIBLE);
            } else {
                llCallGoingOn.setVisibility(View.GONE);
            }

            llCallGoingOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(SINCH_CALL_ID != null && SINCH_CALL_ID.trim().length() > 0) {
                        CallScreenActivity.start(getActivity(), SINCH_CALL_ID);
                    } else {
                        toggleCallInfo(false);
                    }
                }
            });
        } catch(Exception e){

        }
    }

    private void toggleCallInfo(boolean isCallLayoutShow){
        try{
            if(isCallOngoing() && isCallLayoutShow){
                llCallGoingOn.setVisibility(View.VISIBLE);
            } else {
                llCallGoingOn.setVisibility(View.GONE);
            }
        } catch(Exception e){

        }
    }

    private void setCallingText(final String callText){
        try{
            tvCallGoingOn.setText(callText);
        } catch(Exception e){

        }
    }

    public static boolean isCallOngoing() {
        if (SINCH_CALL_ID != null && SINCH_CALL_ID.trim().length() > 0) {
            return true;
        }
        return false;
    }

    public static String getCallID() {
        return SINCH_CALL_ID;
    }

    public static void callStarted(final String callID) {
        SINCH_CALL_ID = callID;
    }

    public static void callEnded() {
        SINCH_CALL_ID = null;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe
    public void getCallInformation(EventAudioCall eventAudioCall){
        System.out.print("");
        if(eventAudioCall != null){
            eventAudioCall.isCallIncoming();
            boolean isCallStarted = eventAudioCall.isCallStarted();
            boolean isCallEnded = eventAudioCall.isCallEnded();
            boolean isCallIncoming = eventAudioCall.isCallIncoming();
            String callText = eventAudioCall.getCallText();

            if(isCallStarted || isCallIncoming){
                toggleCallInfo(true);
                setCallingText(callText);
            }
            if(isCallEnded){
                toggleCallInfo(false);
                callEnded();
            }
        }
    }

}
