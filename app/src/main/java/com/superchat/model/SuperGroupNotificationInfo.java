package com.superchat.model;

import java.io.Serializable;

/**
 * Created by MotoBeans on 9/26/2016.
 */

public class SuperGroupNotificationInfo implements Serializable {
    private String groupName;
    private SuperGroupNotificationInfo_Meta metaInfo;

    public SuperGroupNotificationInfo(final String groupName){
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public SuperGroupNotificationInfo_Meta getMetaInfo() {
        return metaInfo;
    }

    public void setMetaInfo(SuperGroupNotificationInfo_Meta metaInfo) {
        this.metaInfo = metaInfo;
    }

    @Override
    public String toString() {
        return "HubNotificationInfo{" +
                "groupName='" + groupName + '\'' +
                ", metaInfo=" + metaInfo.toString() +
                '}';
    }
}
