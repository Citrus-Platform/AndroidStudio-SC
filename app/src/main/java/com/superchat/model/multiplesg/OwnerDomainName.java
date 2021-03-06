package com.superchat.model.multiplesg;

/**
 * Created by maheshsonker on 03/10/16.
 */

public class OwnerDomainName
{
    public int getDomainMuteInfo() {
        return domainMuteInfo;
    }

    public void setDomainMuteInfo(int domainMuteInfo) {
        this.domainMuteInfo = domainMuteInfo;
    }

    private int domainMuteInfo;
    private String domainName;

    public String getDomainName() { return this.domainName; }

    public String getDomainDisplayName() {
        return domainDisplayName;
    }

    public void setDomainDisplayName(String domainDisplayName) {
        this.domainDisplayName = domainDisplayName;
    }

    private String domainDisplayName;

    private String domainCount;
    private String domainNotify;

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

    public void setDomainName(String domainName) { this.domainName = domainName; }

    private String adminName;

    public String getAdminName() { return this.adminName; }

    public void setAdminName(String adminName) { this.adminName = adminName; }

    private String orgName;

    public String getOrgName() { return this.orgName; }

    public void setOrgName(String orgName) { this.orgName = orgName; }

    private String logoFileId;

    public String getLogoFileId() { return this.logoFileId; }

    public void setLogoFileId(String orgName) { this.logoFileId = logoFileId; }

    private String orgUrl;

    public String getOrgUrl() { return this.orgUrl; }

    public void setOrgUrl(String orgUrl) { this.orgUrl = orgUrl; }

    private String privacyType;

    public String getPrivacyType() { return this.privacyType; }

    public void setPrivacyType(String privacyType) { this.privacyType = privacyType; }

    private String displayName;

    public String getDisplayName() { return this.displayName; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }

    private String domainType;

    public String getDomainType() { return this.domainType; }

    public void setDomainType(String domainType) { this.domainType = domainType; }

    private int unreadCounter;

    public int getUnreadCounter() { return this.unreadCounter; }

    public void setUnreadCounter(int unreadCounter) { this.unreadCounter = unreadCounter; }

    private String createdDate;

    public String getCreatedDate() { return this.createdDate; }

    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}