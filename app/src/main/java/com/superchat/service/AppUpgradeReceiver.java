package com.superchat.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.superchat.utils.Constants;

import java.io.IOException;

/**
 * Created by Mahesh on 15/03/17.
 */

public class AppUpgradeReceiver extends BroadcastReceiver {

    Context context;
    String TAG = AppUpgradeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Uri packageName = intent.getData();
        this.context = context;
        if(packageName.toString().equals("package:" + context.getPackageName())){
            //Application was upgraded

        }
    }

    GoogleCloudMessaging gcm;
    private void registerInBackgroundLocal()
    {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    if(Constants.isBuildLive)
                        Constants.regid = gcm.register(Constants.GOOGLE_PROJECT_ID_PROD);
                    else
                        Constants.regid = gcm.register(Constants.GOOGLE_PROJECT_ID_DEV);
                    msg = "Device registered, registration ID=" + Constants.regid;
                    System.out.println("registerInBackgroundLocal :: regid----> "+Constants.regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // Persist the regID - no need to register again.
                storeRegistrationId(context, Constants.regid);
            }
        }.execute(null, null, null);
    }
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(AppUpgradeReceiver.class.getSimpleName(), Context.MODE_PRIVATE);
    }
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "storeRegistrationId :: Saving regId ==> "+regId+", on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PROPERTY_REG_ID, regId);
        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}