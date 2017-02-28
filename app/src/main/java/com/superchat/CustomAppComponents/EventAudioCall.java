package com.superchat.CustomAppComponents;

/**
 * Created by Motobeans on 2/21/2017.
 */

public class EventAudioCall {
    private boolean isCallStarted;
    private boolean isCallEnded;
    private boolean isCallIncoming;
    private String callText;

    public boolean isCallStarted() {
        return isCallStarted;
    }

    public void setCallStarted(boolean callStarted) {
        isCallStarted = callStarted;
    }

    public boolean isCallEnded() {
        return isCallEnded;
    }

    public void setCallEnded(boolean callEnded) {
        isCallEnded = callEnded;
    }

    public boolean isCallIncoming() {
        return isCallIncoming;
    }

    public void setCallIncoming(boolean callIncoming) {
        isCallIncoming = callIncoming;
    }

    public String getCallText() {
        return callText;
    }

    public void setCallText(String callText) {
        this.callText = callText;
    }
}
