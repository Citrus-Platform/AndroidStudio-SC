package com.superchat.interfaces;


import com.superchat.helper.UtilExceptionHandler;
import com.superchat.helper.UtilGlobal;
import com.superchat.helper.UtilLog;
import com.superchat.helper.UtilToastMessage;
import com.superchat.retrofit.api.ApiClient;

/**
 * Created by MotoBeans on 9/28/2015.
 */
public interface interfaceInstances {
    public static final ApiClient objApi = ApiClient.getInstance();
    public static final UtilExceptionHandler objExceptione = UtilExceptionHandler.getInstance();
    public static final UtilLog objLog = UtilLog.getInstance();
    public static final UtilGlobal objGlobal = new UtilGlobal();
    public static final UtilToastMessage objToast = UtilToastMessage.getInstance();
}
