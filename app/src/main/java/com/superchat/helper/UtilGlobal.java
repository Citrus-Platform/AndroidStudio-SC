package com.superchat.helper;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.superchat.interfaces.interfaceInstances;

/**
 * Created by Akshay on 6/18/2015.
 * Modified by: Sumit
 */
public class UtilGlobal implements interfaceInstances {

    private static boolean isDEVELOPMENT = true;
    private static boolean isTESTING = true;
    private static boolean isRELEASE = true;

    public static String MODE_DEVELOPMENT = "Development";
    public static String MODE_TESTING = "Testing";
    public static String MODE_RELEASE = "Release";

    public static boolean isValidMode(final String MODE){
        boolean isValidMode = false;
        if(MODE != null){
            if((MODE.trim().equalsIgnoreCase(MODE_DEVELOPMENT.trim())) && isDEVELOPMENT){
                isValidMode = true;
            } else if((MODE.trim().equalsIgnoreCase(MODE_TESTING.trim())) && isTESTING){
                isValidMode = true;
            } else if((MODE.trim().equalsIgnoreCase(MODE_RELEASE.trim())) && isRELEASE){
                isValidMode = true;
            }
        }

        return isValidMode;
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    /**
     * Other Constants and Utlity Methods
     */

    public static final String KEY_ERROR_MESSAGE = "Oopss! \nWe are facing some error at this moment. Please try again later";

    public static final String KEY_APP_VERSION_CODE = "versionCode";
    public static final String KEY_APP_VERSION_NAME = "versionName";

    public static final String MALE = "male";
    public static final String FEMALE = "female";

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static final String PACKAGE_NAME = "com.mazkara.user";
    public static final String KEY_NONE = " == None == ";

    public static final String replace_otp_msg_pattern = "&&&&&";

    public static final String ERROR_OUTLET_LISTING_NO_RESULT = "No result to display.";
    public static final String ERROR_OUTLET_LISTING_FAVOURITE_NO_RESULT = "No result to display.";
    public static final String ERROR_ALL_REVIEWS_NO_RESULT = "No result to display.";
    public static final String ERROR_ALL_REVIEWS_USER_NO_RESULT = "No result to display.";
    public static final String ERROR_OUTLET_LISTING_NO_LOCATION = "No Location found\nTap here to search again";
    public static final String ERROR_USER_TIPS_NO_RESULT = "You don't have any tips yet.\n" +
            "Leave a tip for your fellow beauties";

    public static boolean isSdCardPresent() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);

    }

    public static String getExternalDirectoryFolder() {
        return Environment.getExternalStorageDirectory()
                + "/Hello1/";
    }


    public static boolean isServiceRunning(final Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
