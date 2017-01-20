package com.superchat.model;

import java.io.Serializable;

/**
 * Created by MotoBeans on 9/26/2016.
 */

public class SuperGroupNotificationInfo_Meta implements Serializable {
    private int notificationsCount;

    public int getNotificationsCount() {
        return notificationsCount;
    }

    public void setNotificationsCount(int notificationsCount) {
        this.notificationsCount = notificationsCount;
    }

    public void incrementNotificationCount(){
        this.notificationsCount = this.notificationsCount + 1;
    }

    public void decrementNotificationCount(){
        this.notificationsCount = this.notificationsCount - 1;
    }

    public void incrementNotificationCount(final int count){
        this.notificationsCount = this.notificationsCount + count;
    }

    public void decrementNotificationCount(final int count){
        this.notificationsCount = this.notificationsCount - count;
    }

    public void updateNotificationCount(final int count){
        this.notificationsCount = count;
    }

    public void resetNotificationCount(){
        this.notificationsCount = 0;
    }


    @Override
    public String toString() {
        return "HubNotificationInfo_Meta{" +
                "notificationsCount=" + notificationsCount +
                '}';
    }
}
