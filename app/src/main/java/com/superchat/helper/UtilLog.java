package com.superchat.helper;

import android.util.Log;

public class UtilLog {

    public UtilLog() {

    }

    public void Log_d(final String TAG, String Message, String MODE) {
        if (UtilGlobal.isValidMode(MODE))
            Log.d(TAG, Message);
    }

    public void Log_e(final String TAG, String Message, String MODE) {
        if (UtilGlobal.isValidMode(MODE))
            Log.e(TAG, Message);
    }

    public void Log_i(final String TAG, String Message, String MODE) {
        if (UtilGlobal.isValidMode(MODE))
            Log.i(TAG, Message);
    }

    public void Log_v(final String TAG, String Message, String MODE) {
        if (UtilGlobal.isValidMode(MODE))
            Log.v(TAG, Message);
    }

    private static UtilLog uniqInstance;

    public static synchronized UtilLog getInstance() {
        if (uniqInstance == null) {
            uniqInstance = new UtilLog();
        }
        return uniqInstance;
    }
}
