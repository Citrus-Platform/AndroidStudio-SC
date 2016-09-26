package com.superchat.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MotoBeans on 9/26/2016.
 */

public class ListSuperGroupNotificationInfo implements Serializable {
    private Map<String, SuperGroupNotificationInfo> hmSuperGroupsInfo = new HashMap<>();

    public Map<String, SuperGroupNotificationInfo> getHmSuperGroupsInfo() {
        return hmSuperGroupsInfo;
    }

    public void setHmSuperGroupsInfo(Map<String, SuperGroupNotificationInfo> hmSuperGroupsInfo) {
        this.hmSuperGroupsInfo = hmSuperGroupsInfo;
    }

    @Override
    public String toString() {
        return "ListSuperGroupNotificationInfo{" +
                "hmSuperGroupsInfo=" + hmSuperGroupsInfo.toString() +
                '}';
    }
}
