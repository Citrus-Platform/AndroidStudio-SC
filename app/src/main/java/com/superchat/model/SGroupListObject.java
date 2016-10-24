package com.superchat.model;

/**
 * Created by citrus on 9/21/2016.
 */
public class SGroupListObject {

    private String domainName;
    private String adminName;
    private String orgName;
    private String orgUrl;
    private String privacyType;
    private String logoFileId;
    private String domainType;
    private String createdDate;
    private String domainCount;
    private String domainNotify;
    private String domainDisplayName;

    public void setDomainDisplayName(String domainDisplayName) {
        this.domainDisplayName = domainDisplayName;
    }

    public String getDomainDisplayName() {
        return domainDisplayName;
    }

    public void setDomainCount(String domainCount) {
        this.domainCount = domainCount;
    }

    public void setDomainNotify(String domainNotify) {
        this.domainNotify = domainNotify;
    }

    public String getDomainCount() {
        return domainCount;
    }

    public String getDomainNotify() {
        return domainNotify;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public void setPrivacyType(String privacyType) {
        this.privacyType = privacyType;
    }

    public void setLogoFileId(String logoFileId) {
        this.logoFileId = logoFileId;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public String getPrivacyType() {
        return privacyType;
    }

    public String getLogoFileId() {
        return logoFileId;
    }

    public String getDomainType() {
        return domainType;
    }

    public String getCreatedDate() {
        return createdDate;
    }
}
