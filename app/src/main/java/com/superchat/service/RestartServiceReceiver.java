package com.superchat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.superchat.SuperChatApplication;
import com.superchat.ui.SinchService;

/**
 * Created by MotoBeans on 9/21/2016.
 */

public class RestartServiceReceiver extends BroadcastReceiver
{

    private static final String TAG = "RestartServiceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e(TAG, "onReceive after killed..");
        context.startService(new Intent(SuperChatApplication.context, SinchService.class));
    }

}
