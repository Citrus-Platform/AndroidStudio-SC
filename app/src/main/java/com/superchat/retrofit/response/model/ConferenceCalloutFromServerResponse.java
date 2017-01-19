package com.superchat.retrofit.response.model;

/**
 * Created by Mahesh on 16/01/17.
 */

public class ConferenceCalloutFromServerResponse {
    String status;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String message;
}
