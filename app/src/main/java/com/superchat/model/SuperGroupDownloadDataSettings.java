package com.superchat.model;

/**
 * Created by Motobeans on 12/22/2016.
 */

public class SuperGroupDownloadDataSettings {
    private String groupName;
    private boolean isSavedInGallery = true;
    private SuperGroupDownloadDataSettings_Common modileSettings;
    private SuperGroupDownloadDataSettings_Common wifiSettings;

    public SuperGroupDownloadDataSettings(final String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isSavedInGallery() {
        return isSavedInGallery;
    }

    public void setSavedInGallery(boolean savedInGallery) {
        isSavedInGallery = savedInGallery;
    }

    public SuperGroupDownloadDataSettings_Common getModileSettings() {
        return modileSettings;
    }

    public void setModileSettings(SuperGroupDownloadDataSettings_Common modileSettings) {
        this.modileSettings = modileSettings;
    }

    public SuperGroupDownloadDataSettings_Common getWifiSettings() {
        return wifiSettings;
    }

    public void setWifiSettings(SuperGroupDownloadDataSettings_Common wifiSettings) {
        this.wifiSettings = wifiSettings;
    }
}
