package com.superchat.model.multiplesg;

/**
 * Created by maheshsonker on 03/10/16.
 */

public class InvitedDomainNameSet
{
    private String domainName;
    private String description;
    private int domainMuteInfo;
    private String domainDisplayName;
    private String adminName;
    private String orgName;
    private String orgUrl;
    private String privacyType;
    private String displayName;
    private String logoFileId;
    private String domainType;
    private int unreadCounter;
    private String createdDate;

    public int getDomainMuteInfo() {
        return domainMuteInfo;
    }

    public void setDomainMuteInfo(int domainMuteInfo) {
        this.domainMuteInfo = domainMuteInfo;
    }

    public String getDomainName() { return this.domainName; }

    public void setDomainName(String domainName) { this.domainName = domainName; }

    public String getDomainDisplayName() {
        return domainDisplayName;
    }

    public void setDomainDisplayName(String domainDisplayName) {
        this.domainDisplayName = domainDisplayName;
    }

    public String getAdminName() { return this.adminName; }

    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getOrgName() { return this.orgName; }

    public void setOrgName(String orgName) { this.orgName = orgName; }

    public String getOrgUrl() { return this.orgUrl; }

    public void setOrgUrl(String orgUrl) { this.orgUrl = orgUrl; }

    public String getPrivacyType() { return this.privacyType; }

    public void setPrivacyType(String privacyType) { this.privacyType = privacyType; }

    public String getDisplayName() { return this.displayName; }

    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getLogoFileId() { return this.logoFileId; }

    public void setLogoFileId(String logoFileId) { this.logoFileId = logoFileId; }

    public String getDomainType() { return this.domainType; }

    public void setDomainType(String domainType) { this.domainType = domainType; }

    public int getUnreadCounter() { return this.unreadCounter; }

    public void setUnreadCounter(int unreadCounter) { this.unreadCounter = unreadCounter; }

    public String getCreatedDate() { return this.createdDate; }

    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
