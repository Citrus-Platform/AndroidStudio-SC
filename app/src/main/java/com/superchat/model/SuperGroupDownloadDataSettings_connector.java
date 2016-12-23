package com.superchat.model;

/**
 * Created by Motobeans on 12/22/2016.
 */

public interface SuperGroupDownloadDataSettings_connector {

    public boolean isPhotoAllowed();
    public void setPhotoAllowed(boolean photoAllowed);
    public boolean isAudioAllowed();
    public void setAudioAllowed(boolean audioAllowed);
    public boolean isVideoAllowed();
    public void setVideoAllowed(boolean videoAllowed);
}