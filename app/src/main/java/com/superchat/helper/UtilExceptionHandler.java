package com.superchat.helper;

public class UtilExceptionHandler {

    private final boolean isShowError = true;

    public void printStackTrace(Exception e) {
        if (isShowError)
            e.printStackTrace();
    }

    public void printStackTrace(Throwable t) {
        if (isShowError)
            t.printStackTrace();
    }

    public static void handleRetrofitErrorStackTrace(Exception exception){
        if(UtilGlobal.isValidMode(UtilGlobal.MODE_DEVELOPMENT)) {
            exception.printStackTrace();
        }
    }

    private static UtilExceptionHandler uniqInstance;

    public static synchronized UtilExceptionHandler getInstance() {
        if (uniqInstance == null) {
            uniqInstance = new UtilExceptionHandler();
        }
        return uniqInstance;
    }
}
