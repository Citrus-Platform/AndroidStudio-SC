package com.superchat.model;

import java.util.ArrayList;

/**
 * Created by citrus on 9/27/2016.
 */
public class RegistrationFormResponse {

    private String status;
    private String message;
    private ArrayList<DomainSetObject> activateDomainDataSet;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setActivateDomainDataSet(ArrayList<DomainSetObject> activateDomainDataSet) {
        this.activateDomainDataSet = activateDomainDataSet;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<DomainSetObject> getActivateDomainDataSet() {
        return activateDomainDataSet;
    }
}
