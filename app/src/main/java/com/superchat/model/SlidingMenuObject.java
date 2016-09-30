package com.superchat.model;

/**
 * Created by citrus on 9/19/2016.
 */
public class SlidingMenuObject {

    private String domainName;
    private String domainId;
    private String domainCount;
    private String domainNotify;

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public void setDomainCount(String domainCount) {
        this.domainCount = domainCount;
    }

    public void setDomainNotify(String domainNotify) {
        this.domainNotify = domainNotify;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getDomainCount() {
        return domainCount;
    }

    public String getDomainNotify() {
        return domainNotify;
    }
}
