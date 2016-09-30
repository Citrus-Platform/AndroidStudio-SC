package com.superchat.helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.superchat.model.ListSuperGroupNotificationInfo;
import com.superchat.model.SuperGroupNotificationInfo;
import com.superchat.model.SuperGroupNotificationInfo_Meta;
import com.superchat.pref.SharedPreferencesBean;
import com.superchat.pref.SharedPreferencesCustom;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MotoBeans on 9/26/2016.
 */

public class SuperGroupNotificationsInfoHandler {

    private Activity currActivity = null;
    private Context currContext = null;
    private String superGroupName = null;

    public SuperGroupNotificationsInfoHandler(@NonNull final Activity currActivity,
                                              @NonNull final String superGroupName) {
        this.superGroupName = superGroupName;
        this.currActivity = currActivity;
        if (this.superGroupName == null || this.currActivity == null) {
            throw new NullPointerException();
        } else {
            checkGroupValidityOrInsert();
        }
    }

    public SuperGroupNotificationsInfoHandler(@NonNull final Context currContext,
                                              @NonNull final String superGroupName) {
        this.superGroupName = superGroupName;
        this.currContext = currContext;
        if (this.superGroupName == null || this.currActivity == null) {
            throw new NullPointerException();
        } else {
            checkGroupValidityOrInsert();
        }
    }

    private SharedPreferencesCustom getSPNotificationInstance() {
        /**
         * Shared Preferences
         */
        SharedPreferencesCustom objSPSG =
                new SharedPreferencesCustom((currActivity != null ? currActivity : currContext), SharedPreferencesBean.KEY_NOTIFICATION_INFO);

        return objSPSG;
    }

    private ListSuperGroupNotificationInfo getSPNotificationData() {
        ListSuperGroupNotificationInfo objSGNotiInfo = null;
        try {

            SharedPreferencesCustom objSPSG = getSPNotificationInstance();
            String SGNotificationInfoJson = objSPSG.getString(SharedPreferencesBean.KEY_NOTIFICATION_INFO);

            objSGNotiInfo = new Gson().fromJson(SGNotificationInfoJson, ListSuperGroupNotificationInfo.class);

        } catch (Exception e) {

        }
        return objSGNotiInfo != null ? objSGNotiInfo : null;
    }

    private boolean isSGPresent() {
        boolean isSGPresent = false;
        try {
            superGroupName = (superGroupName != null) ? superGroupName.trim() : superGroupName;

            final ListSuperGroupNotificationInfo objSGNotiInfo = getSPNotificationData();

            if ((objSGNotiInfo != null) && (objSGNotiInfo.getHmSuperGroupsInfo() != null)) {

                Map<String, SuperGroupNotificationInfo> hmSuperGroupsInfo = objSGNotiInfo.getHmSuperGroupsInfo();
                if (hmSuperGroupsInfo.containsKey(superGroupName)) {
                    isSGPresent = true;
                }
            }
        } catch (Exception e) {

        }
        return isSGPresent;
    }

    private SuperGroupNotificationInfo getSGInfo() {
        SuperGroupNotificationInfo SGNotiInfo = null;
        try {
            superGroupName = (superGroupName != null) ? superGroupName.trim() : superGroupName;

            final ListSuperGroupNotificationInfo objSGNotiInfo = getSPNotificationData();

            if ((objSGNotiInfo != null) && (objSGNotiInfo.getHmSuperGroupsInfo() != null)) {

                Map<String, SuperGroupNotificationInfo> hmSuperGroupsInfo = objSGNotiInfo.getHmSuperGroupsInfo();
                if (hmSuperGroupsInfo.containsKey(superGroupName)) {
                    SGNotiInfo = hmSuperGroupsInfo.get(superGroupName);
                }
            }
        } catch (Exception e) {

        }
        return SGNotiInfo != null ? SGNotiInfo : null;
    }

    private SuperGroupNotificationInfo_Meta getMetaInfoOfGroup() {
        SuperGroupNotificationInfo_Meta metaInfo = null;
        SuperGroupNotificationInfo SGNotiInfo = getSGInfo();
        if (SGNotiInfo != null) {
            metaInfo = SGNotiInfo.getMetaInfo();
        }
        return metaInfo;
    }

    private int countCheckSGInfo = 0;
    private boolean saveSGInfo(final SuperGroupNotificationInfo sgInfo) {
        boolean isSGDataSaved = false;
        try {
            superGroupName = (superGroupName != null) ? superGroupName.trim() : superGroupName;

            final ListSuperGroupNotificationInfo objSGNotiInfo = getSPNotificationData();

            if ((objSGNotiInfo != null)) {
                if(objSGNotiInfo.getHmSuperGroupsInfo() == null){
                    objSGNotiInfo.setHmSuperGroupsInfo(new HashMap<String, SuperGroupNotificationInfo>());
                }

                Map<String, SuperGroupNotificationInfo> hmSuperGroupsInfo = objSGNotiInfo.getHmSuperGroupsInfo();
                hmSuperGroupsInfo.put(superGroupName, sgInfo);

                isSGDataSaved = saveInSharedPreference(hmSuperGroupsInfo);
            } else {
                saveInSharedPreference(new HashMap<String, SuperGroupNotificationInfo>());
                if(countCheckSGInfo <= 2) {
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

    private boolean saveSGInfoMeta(final SuperGroupNotificationInfo_Meta sgInfoMeta) {
        boolean isSGDataSaved = false;
        try {
            superGroupName = (superGroupName != null) ? superGroupName.trim() : superGroupName;

            SuperGroupNotificationInfo SGNotiInfo = getSGInfo();
            SGNotiInfo.setMetaInfo(sgInfoMeta);

            isSGDataSaved = saveSGInfo(SGNotiInfo);

        } catch (Exception e) {

        }
        return isSGDataSaved;
    }

    private boolean saveInSharedPreference(final Map<String, SuperGroupNotificationInfo> hmSuperGroupsInfo) {
        boolean isSGDataSaved = false;
        try {
            SharedPreferencesCustom objSPSG = getSPNotificationInstance();

            ListSuperGroupNotificationInfo objInfo = new ListSuperGroupNotificationInfo();
            objInfo.setHmSuperGroupsInfo(hmSuperGroupsInfo);

            String objStr = objectToString(objInfo);
            if (objStr != null) {
                objSPSG.putString(SharedPreferencesBean.KEY_NOTIFICATION_INFO, objStr);
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

    private void checkGroupValidityOrInsert() {

        SuperGroupNotificationInfo SGNotiInfo = getSGInfo();
        SuperGroupNotificationInfo_Meta metaInfo = getMetaInfoOfGroup();
        if (SGNotiInfo != null) {
            if (metaInfo != null) {
                // Group is present and populated
            } else {
                SuperGroupNotificationInfo_Meta tempMetaInfo = new SuperGroupNotificationInfo_Meta();
                SGNotiInfo.setMetaInfo(tempMetaInfo);

                saveSGInfo(SGNotiInfo);
            }
        } else {
            SGNotiInfo = new SuperGroupNotificationInfo(superGroupName);
            SuperGroupNotificationInfo_Meta tempMetaInfo = new SuperGroupNotificationInfo_Meta();
            SGNotiInfo.setMetaInfo(tempMetaInfo);

            saveSGInfo(SGNotiInfo);
        }

    }

    public boolean incrementCounter() {
        boolean isSuccessfull = false;

        SuperGroupNotificationInfo_Meta metaInfo = getMetaInfoOfGroup();
        metaInfo.incrementNotificationCount();

        isSuccessfull = saveSGInfoMeta(metaInfo);

        return isSuccessfull;
    }

    public boolean incrementCounter(final int count) {
        boolean isSuccessfull = false;

        SuperGroupNotificationInfo_Meta metaInfo = getMetaInfoOfGroup();
        metaInfo.incrementNotificationCount(count);

        isSuccessfull = saveSGInfoMeta(metaInfo);

        return isSuccessfull;
    }


    public boolean decrementCounter() {
        boolean isSuccessfull = false;

        SuperGroupNotificationInfo_Meta metaInfo = getMetaInfoOfGroup();
        metaInfo.decrementNotificationCount();

        isSuccessfull = saveSGInfoMeta(metaInfo);

        return isSuccessfull;
    }

    public boolean decrementCounter(final int count) {
        boolean isSuccessfull = false;

        SuperGroupNotificationInfo_Meta metaInfo = getMetaInfoOfGroup();
        metaInfo.decrementNotificationCount(count);

        isSuccessfull = saveSGInfoMeta(metaInfo);

        return isSuccessfull;
    }

    public boolean updateCounter(final int count) {
        boolean isSuccessfull = false;

        SuperGroupNotificationInfo_Meta metaInfo = getMetaInfoOfGroup();
        metaInfo.updateNotificationCount(count);

        isSuccessfull = saveSGInfoMeta(metaInfo);

        return isSuccessfull;
    }

    public boolean resetCounter() {
        boolean isSuccessfull = false;

        SuperGroupNotificationInfo_Meta metaInfo = getMetaInfoOfGroup();
        metaInfo.resetNotificationCount();

        isSuccessfull = saveSGInfoMeta(metaInfo);

        return isSuccessfull;
    }

    public int getCount(){
        SuperGroupNotificationInfo_Meta metaInfo = getMetaInfoOfGroup();
        return metaInfo != null ? metaInfo.getNotificationsCount() : 0;
    }

/*
    public void printCurrentNotificationInfo(){
        SuperGroupNotificationInfo sgTemp = getSGInfo();
        sgTemp.toString();
    }


    public void printAllNoti(){
        ListSuperGroupNotificationInfo objSGNotiInfo = getSPNotificationData();
        objSGNotiInfo.toString();
    }


    public int getNotificationSPDataCount(){
        ListSuperGroupNotificationInfo objSGNotiInfo = getSPNotificationData();
        if(objSGNotiInfo != null) {
            return objSGNotiInfo.getHmSuperGroupsInfo() != null ? objSGNotiInfo.getHmSuperGroupsInfo().size() : 0;
        } else {
            return 1;
        }
    }*/
}
