package com.superchat.retrofit.response.model;

import java.io.Serializable;

/**
 * Created by MotoBeans on 7/26/2016.
 */
public class CommonResponse implements Serializable {
    private String status;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
