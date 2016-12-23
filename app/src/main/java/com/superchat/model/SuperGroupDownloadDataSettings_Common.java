package com.superchat.model;

/**
 * Created by Motobeans on 12/22/2016.
 */

public class SuperGroupDownloadDataSettings_Common implements SuperGroupDownloadDataSettings_connector{

    private boolean isPhotoAllowed = true;
    private boolean isAudioAllowed = true;
    private boolean isVideoAllowed = true;

    public boolean isPhotoAllowed() {
        return isPhotoAllowed;
    }

    public void setPhotoAllowed(boolean photoAllowed) {
        isPhotoAllowed = photoAllowed;
    }

    public boolean isAudioAllowed() {
        return isAudioAllowed;
    }

    public void setAudioAllowed(boolean audioAllowed) {
        isAudioAllowed = audioAllowed;
    }

    public boolean isVideoAllowed() {
        return isVideoAllowed;
    }

    public void setVideoAllowed(boolean videoAllowed) {
        isVideoAllowed = videoAllowed;
    }
}
