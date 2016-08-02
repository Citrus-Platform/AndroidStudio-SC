package com.superchat.retrofit.response.model;

/**
 * Created by MotoBeans on 7/26/2016.
 */
public class UserAdminResponse extends CommonResponse{
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
