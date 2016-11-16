package com.superchat.pref;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;

/**
 * Created by Motobeans on 11/8/2016.
 */

public class SharedPreseferencesUtil {


    public static void setLastUpdateCheckDate(Context currActivity, AppUpdatedCheckBean appUpdatedCheckBean) {
        if (appUpdatedCheckBean != null) {
            SharedPreferencesCustom SPInstigatorsList = new SharedPreferencesCustom(currActivity, SharedPreferencesBean.KEY_APP_UPDATE_CHECKER_INFO);
            String appUpdatedCheckData = new Gson().toJson(
                    appUpdatedCheckBean);
            SPInstigatorsList.putString(SharedPreferencesBean.KEY_APP_UPDATE_CHECKER_INFO, "" + appUpdatedCheckData);
        }
    }

    public static String getLastUpdateCheckDate(final Activity curr_activity) {

        AppUpdatedCheckBean appUpdatedCheckBean = null;
        try {
			/*
			 * Shared Preferences
			 */
            SharedPreferencesCustom obj_sp_login = new SharedPreferencesCustom(curr_activity, SharedPreferencesBean.KEY_APP_UPDATE_CHECKER_INFO);

            String loginJson = obj_sp_login.getString(SharedPreferencesBean.KEY_APP_UPDATE_CHECKER_INFO);

            appUpdatedCheckBean = new Gson().fromJson(loginJson, AppUpdatedCheckBean.class);
        } catch (Exception e) {

        }

        return appUpdatedCheckBean != null ? appUpdatedCheckBean.getLastUpdateCheck() : null;
    }
}
