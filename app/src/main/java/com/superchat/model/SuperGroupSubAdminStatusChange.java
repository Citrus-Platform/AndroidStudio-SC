package com.superchat.model;

import java.io.Serializable;

/**
 * Created by MotoBeans on 9/26/2016.
 */

public class SuperGroupSubAdminStatusChange implements Serializable {
    private boolean isSGSubAdmin = false;

    public boolean isSGSubAdmin() {
        return isSGSubAdmin;
    }

    public void setSGSubAdmin(boolean SGSubAdmin) {
        isSGSubAdmin = SGSubAdmin;
    }
}
