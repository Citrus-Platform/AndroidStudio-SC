package com.superchat.model;

/**
 * Created by citrus on 9/27/2016.
 */
public class DomainSetObject {

    private String domainName;
    private int userId;
    private String username;
    private String password;
    private boolean activateSuccess;

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActivateSuccess(boolean activateSuccess) {
        this.activateSuccess = activateSuccess;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getUsername() {
        return username;
    }

    public int getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public boolean isActivateSuccess() {
        return activateSuccess;
    }
}
