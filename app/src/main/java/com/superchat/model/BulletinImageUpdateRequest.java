package com.superchat.model;

/**
 * Created by lalitjoshi on 09/11/16.
 */

public class BulletinImageUpdateRequest {

    private String domainName;
    private String orgName;
    private String description;
    private String orgUrl;
    private String logoFileId;
    private String bulletinFileId;


    public BulletinImageUpdateRequest(String domainName, String orgName, String description,
                                      String orgUrl, String logoFileId, String bulletinFileId) {

        this.domainName = domainName;
        this.orgName = orgName;
        this.description = description;
        this.orgUrl = orgUrl;
        this.logoFileId = logoFileId;
        this.bulletinFileId = bulletinFileId;

    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrgUrl() {
        return orgUrl;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getLogoFileId() {
        return logoFileId;
    }

    public void setLogoFileId(String logoFileId) {
        this.logoFileId = logoFileId;
    }

    public String getBulletinFileId() {
        return bulletinFileId;
    }

    public void setBulletinFileId(String bulletinFileId) {
        this.bulletinFileId = bulletinFileId;
    }
}
