package com.superchat.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by MotoBeans on 9/26/2016.
 */

public class ListSuperGroupSettingDataInfo implements Serializable {
    private Map<String, SuperGroupDownloadDataSettings> hmSuperGroupsInfo = new HashMap<>();

    public Map<String, SuperGroupDownloadDataSettings> getHmSuperGroupsInfo() {
        return hmSuperGroupsInfo;
    }

    public void setHmSuperGroupsInfo(Map<String, SuperGroupDownloadDataSettings> hmSuperGroupsInfo) {
        this.hmSuperGroupsInfo = hmSuperGroupsInfo;
    }

    @Override
    public String toString() {
        return "ListSuperGroupDownloadDataSettings{" +
                "hmSuperGroupsInfo=" + hmSuperGroupsInfo.toString() +
                '}';
    }
}
