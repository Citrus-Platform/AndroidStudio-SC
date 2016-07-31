package com.superchat.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by vis on 08-05-2015.
 */
public class SuperCharPref {

    private static final SuperCharPref instance = new SuperCharPref();
    private static final String PREF_NAME = "SKEDULE_PREF";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";


    public interface PreferenceKey {

        String GCM_REG_ID = "registration_id";
        String checkCategoryDrawer = "checkCategoryDrawer";
        String listView_header = "header";
        String SAVED_APP_VERSION = "appVersion";
        String GOOGLE_APP_PROJECT_ID = "878328875689";
        String PHONENUMBER = "phoneNumber";
        String favorite_teams = "favorite_teams";
        String favorite_athletes = "favorite_athletes";
        String PROFILE_PIC_PATH = "PROFILE_PIC_PATH";
        String USER_ID = "USER_ID";
        String REQUEST_GetHomeDetails = "REQUEST_GetHomeDetails";
        String REQUEST_getFeedPosts = "REQUEST_getFeedPosts";
        String facebookAccessToken = "facebookAccessToken";
        String UserConnectedWithFB = "UserConnectedWithFB";
        String profileResponseSaved = "profileResponseSaved";
        String profileResponse = "profileResponse";
        String isAppOpenedIn15Days = "1";
        String isDirectLogin = "isDirectLogin";
        String countforShowVersionUpdates = "countforShowVersionUpdates";
        String IS_LOGGEDIN = "LOGGEDIN";
        String LAT = "lat";
        String LNG = "lng";
        String USER_FB_ID = "FB_ID";
        String NAME = "FB_NAME";
        String LoginDetails = "LoginDetails";
        String requestArrayList = "requestArrayList";
        String backersArrayListString = "backersArrayListString";
        String IS_VERIFIED = "IS_VERIFIED";
        String IS_SHOWCASE_CATEGORY_VIEWED = "IS_SHOWCASE_CATEGORY_VIEWED";
        String EMAIL_ID = "EMAIL_ID";
        String Dashboard_Response = "Dashboard_Response";
        String Dashboard_Response_new = "Dashboard_Response";
        String isFirstTime = "isFirstTime";
        String GET_ALL_BOOKING_RESPONSE = "GET_ALL_BOOKING_RESPONSE";
        String GET_ALL_RequestList_RESPONSE = "GET_ALL_RequestList_RESPONSE";
        String RequestDetail_RESPONSE = "RequestDetail_RESPONSE";
        String RequestRefresh_RESPONSE = "RequestRefresh_RESPONSE";
        String RequestLoad_RESPONSE = "RequestLoad_RESPONSE";
        String uplodedImgUrl = "uplodedImgUrl";
        String NOTIFICATION = "NOTIFICATION";
        String REFERAL_CODE = "REFERAL_CODE";
        String REFERAL_Link = "REFERAL_Link";
        String REFERAL_MESSAGE = "REFERAL_MESSAGE";
        String USER_LOCATION = "USER_LOCATION";
    }

    private SuperCharPref() {

    }

    public static SuperCharPref getInstance() {
        return instance;
    }

    public void resetPreference(Context context) {
        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * Save the string into the shared preference.
     *
     * @param context Context object.
     * @param key     Key to save.
     * @param value   String value associated with the key.
     */
    public void saveString(Context context, String key, String value) {
        try {
            SharedPreferences info = context.getApplicationContext()
                    .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = info.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SkedulePref", ":save:exit");
          //  System.exit(0);
        }
    }

    /**
     * Get the string value of key from shared preference.
     *
     * @param key      Key whose value need to be searched.
     * @param defValue Default value to return in case no such key exist.
     * @return Value associated with the key.
     */
    public String getString(Context context, String key, String defValue) {
        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return info.getString(key, defValue);
    }

    /**
     * Save the boolean into the shared preference.
     *
     * @param context Context object.
     * @param key     Key to save.
     * @param value   String value associated with the key.
     */
    public void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * Get the boolean value of key from shared preference.
     *
     * @param key      Key whose value need to be searched.
     * @param defValue Default value to return in case no such key exist.
     * @return Value associated with the key.
     */
    public boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return info.getBoolean(key, defValue);
    }

    /**
     * Save the Integer into the shared preference.
     *
     * @param context Context object.
     * @param key     Key to save.
     * @param value   Integer value associated with the key.
     */
    public void saveInt(Context context, String key, int value) {
        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Get the Integer value of key from shared preference.
     *
     * @param key      Key whose value need to be searched.
     * @param defValue Default value to return in case no such key exist.
     * @return Value associated with the key.
     */
    public int getInt(Context context, String key, int defValue) {
        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return info.getInt(key, defValue);
    }

    /**
     * Save the Integer into the shared preference.
     *
     * @param context Context object.
     * @param key     Key to save.
     * @param value   Integer value associated with the key.
     */
    public void saveDouble(Context context, String key, Double value) {
        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    /**
     * Get the Integer value of key from shared preference.
     *
     * @param key      Key whose value need to be searched.
     * @param defValue Default value to return in case no such key exist.
     * @return Value associated with the key.
     */
    public double getDouble(Context context, String key, double defValue) {

        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return Double.longBitsToDouble(info.getLong(key, Double.doubleToLongBits(defValue)));

    }

}