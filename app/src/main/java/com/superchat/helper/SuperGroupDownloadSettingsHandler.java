package com.superchat.helper;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.gson.Gson;
import com.superchat.model.ListSuperGroupSettingDataInfo;
import com.superchat.model.ListSuperGroupSettingDataInfo;
import com.superchat.model.SuperGroupDownloadDataSettings;
import com.superchat.model.SuperGroupDownloadDataSettings_Mobile;
import com.superchat.model.SuperGroupDownloadDataSettings_WIFI;
import com.superchat.model.SuperGroupDownloadDataSettings_connector;
import com.superchat.pref.SharedPreferencesBean;
import com.superchat.pref.SharedPreferencesCustom;
import com.superchat.utils.SharedPrefManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MotoBeans on 9/26/2016.
 */

public class SuperGroupDownloadSettingsHandler {

    private Activity currActivity = null;
    private Context currContext = null;
    private String superGroupName = null;

    public SuperGroupDownloadSettingsHandler(@NonNull final Activity currActivity) {

        this.superGroupName = SharedPrefManager.getInstance().getUserDomain();
        this.currActivity = currActivity;
        if (this.superGroupName == null || this.currActivity == null) {
            throw new NullPointerException();
        } else {
            checkGroupValidityOrInsert();
        }
    }

    public SuperGroupDownloadSettingsHandler(@NonNull final Context currContext) {

        this.superGroupName = SharedPrefManager.getInstance().getUserDomain();
        this.currContext = currContext;
        if (this.superGroupName == null || this.currContext == null) {
            throw new NullPointerException();
        } else {
            checkGroupValidityOrInsert();
        }
    }

    private void checkGroupValidityOrInsert() {

        SuperGroupDownloadDataSettings SGSettingsDataInfo = getSGInfo();
        if (SGSettingsDataInfo == null) {
            SGSettingsDataInfo = new SuperGroupDownloadDataSettings(superGroupName);
            saveSGInfo(SGSettingsDataInfo);
        }
    }

    public SuperGroupDownloadDataSettings getSGInfo() {
        SuperGroupDownloadDataSettings SGSettingsDataInfo = null;
        try {
            superGroupName = (superGroupName != null) ? superGroupName.trim() : superGroupName;

            final ListSuperGroupSettingDataInfo objSGSettingsDataInfo = getSPDownloadSettingsData();

            if ((objSGSettingsDataInfo != null) && (objSGSettingsDataInfo.getHmSuperGroupsInfo() != null)) {

                Map<String, SuperGroupDownloadDataSettings> hmSuperGroupsInfo = objSGSettingsDataInfo.getHmSuperGroupsInfo();
                if (hmSuperGroupsInfo.containsKey(superGroupName)) {
                    SGSettingsDataInfo = hmSuperGroupsInfo.get(superGroupName);
                }
            }
        } catch (Exception e) {

        }
        return SGSettingsDataInfo != null ? SGSettingsDataInfo : null;
    }

    public boolean isSavedInGallery(){
        SuperGroupDownloadDataSettings SGSettingsDataInfo = getSGInfo();
        return SGSettingsDataInfo.isSavedInGallery();
    }

    public void changeMediaGallerySetting(boolean isSaveInGallery){
        SuperGroupDownloadDataSettings SGSettingsDataInfo = getSGInfo();
        SGSettingsDataInfo.setSavedInGallery(isSaveInGallery);

        saveSGInfo(SGSettingsDataInfo);
    }

    public SuperGroupDownloadDataSettings_connector getMobileConnector() {
        return (getSGInfo().getModileSettings() == null ? new SuperGroupDownloadDataSettings_Mobile() : getSGInfo().getModileSettings());
    }

    public SuperGroupDownloadDataSettings_connector getWIFIConnector() {
        return (getSGInfo().getWifiSettings() == null ? new SuperGroupDownloadDataSettings_WIFI() : getSGInfo().getWifiSettings());
    }

    private SharedPreferencesCustom getSPNotificationInstance() {
        /**
         * Shared Preferences
         */
        SharedPreferencesCustom objSPSG =
                new SharedPreferencesCustom((currActivity != null ? currActivity : currContext), SharedPreferencesBean.KEY_DATA_USAGE_SETTINGS);

        return objSPSG;
    }


    private ListSuperGroupSettingDataInfo getSPDownloadSettingsData() {
        ListSuperGroupSettingDataInfo objSGSettingsDataInfo = null;
        try {
            SharedPreferencesCustom objSPSG = getSPNotificationInstance();
            String SGDataSettingInfoJSON = objSPSG.getString(SharedPreferencesBean.KEY_DATA_USAGE_SETTINGS);

            objSGSettingsDataInfo = new Gson().fromJson(SGDataSettingInfoJSON, ListSuperGroupSettingDataInfo.class);

        } catch (Exception e) {

        }
        return objSGSettingsDataInfo != null ? objSGSettingsDataInfo : null;
    }

    private boolean isSGPresent() {
        boolean isSGPresent = false;
        try {
            superGroupName = (superGroupName != null) ? superGroupName.trim() : superGroupName;

            final ListSuperGroupSettingDataInfo objSGSettingsDataInfo = getSPDownloadSettingsData();

            if ((objSGSettingsDataInfo != null) && (objSGSettingsDataInfo.getHmSuperGroupsInfo() != null)) {

                Map<String, SuperGroupDownloadDataSettings> hmSuperGroupsInfo = objSGSettingsDataInfo.getHmSuperGroupsInfo();
                if (hmSuperGroupsInfo.containsKey(superGroupName)) {
                    isSGPresent = true;
                }
            }
        } catch (Exception e) {

        }
        return isSGPresent;
    }

    private int countCheckSGInfo = 0;

    public boolean saveSGInfo(final SuperGroupDownloadDataSettings sgInfo) {
        boolean isSGDataSaved = false;
        try {
            superGroupName = (superGroupName != null) ? superGroupName.trim() : superGroupName;

            final ListSuperGroupSettingDataInfo objSGSettingsDataInfo = getSPDownloadSettingsData();

            if ((objSGSettingsDataInfo != null)) {
                if (objSGSettingsDataInfo.getHmSuperGroupsInfo() == null) {
                    objSGSettingsDataInfo.setHmSuperGroupsInfo(new HashMap<String, SuperGroupDownloadDataSettings>());
                }

                Map<String, SuperGroupDownloadDataSettings> hmSuperGroupsInfo = objSGSettingsDataInfo.getHmSuperGroupsInfo();
                hmSuperGroupsInfo.put(superGroupName, sgInfo);

                isSGDataSaved = saveInSharedPreference(hmSuperGroupsInfo);
            } else {
                saveInSharedPreference(new HashMap<String, SuperGroupDownloadDataSettings>());
                if (countCheckSGInfo <= 2) {
                    countCheckSGInfo++;
                    saveSGInfo(sgInfo);
                } else {
                    countCheckSGInfo = 0;
                }
            }
        } catch (Exception e) {

        }
        return isSGDataSaved;
    }

    private boolean saveInSharedPreference(final Map<String, SuperGroupDownloadDataSettings> hmSuperGroupsInfo) {
        boolean isSGDataSaved = false;
        try {
            SharedPreferencesCustom objSPSG = getSPNotificationInstance();

            ListSuperGroupSettingDataInfo objInfo = new ListSuperGroupSettingDataInfo();
            objInfo.setHmSuperGroupsInfo(hmSuperGroupsInfo);

            String objStr = objectToString(objInfo);
            if (objStr != null) {
                objSPSG.putString(SharedPreferencesBean.KEY_DATA_USAGE_SETTINGS, objStr);
                isSGDataSaved = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSGDataSaved;
    }

    private String objectToString(final Object obj) {
        String objStr = null;
        if (obj != null) {
            objStr = new Gson().toJson(obj);
        }
        return objStr;
    }

    public enum DOWNLOADABLE_CONTENT_TYPE {
        PHOTO, AUDIO, VIDEO;
    }

    public boolean isDownloadable(DOWNLOADABLE_CONTENT_TYPE type) {
        boolean isValidToDownload = false;

        if(type == null){
            return false;
        }
        SuperGroupDownloadDataSettings_connector connector = null;
        switch (chkStatus()) {
            case MOBILE: {
                connector = getMobileConnector();
                break;
            }
            case WIFI: {
                connector = getWIFIConnector();
                break;
            }
        }

        if (connector != null) {
            switch (type) {
                case PHOTO: {
                    isValidToDownload = connector.isPhotoAllowed();
                    break;
                }
                case AUDIO: {
                    isValidToDownload = connector.isAudioAllowed();
                    break;
                }
                case VIDEO: {
                    isValidToDownload = connector.isVideoAllowed();
                    break;
                }
            }
        }
        return isValidToDownload;
    }

    private Context getValidContext() {
        return (currActivity != null ? currActivity : currContext);
    }

    private boolean CheckConnectivity() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getValidContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mConnectivityManager.getActiveNetworkInfo() != null
                && mConnectivityManager.getActiveNetworkInfo().isAvailable()
                && mConnectivityManager.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false; // make it false
        }
    }

    private NetworkConnection chkStatus() {
        NetworkConnection networkConnection = NetworkConnection.NO_NETWORK;
        if (CheckConnectivity()) {
            final ConnectivityManager connMgr = (ConnectivityManager)
                    getValidContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi.isConnectedOrConnecting()) {
                networkConnection = NetworkConnection.WIFI;
            } else if (mobile.isConnectedOrConnecting()) {
                networkConnection = NetworkConnection.MOBILE;
            } else {
                networkConnection = NetworkConnection.NO_NETWORK;
            }
        } else {
            networkConnection = NetworkConnection.NO_NETWORK;
        }

        return networkConnection;
    }

    enum NetworkConnection {
        MOBILE, WIFI, NO_NETWORK;
    }
}
