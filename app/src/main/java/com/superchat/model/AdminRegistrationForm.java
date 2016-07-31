package com.superchat.model;

import com.google.gson.annotations.SerializedName;

public class AdminRegistrationForm {

	private static final String TO_STRING_TEMPLATE = "iMobileNumber: %s, type: %s, imei: %s, imsi: %s, clientVersion: %s";

//	private String mobileNumber;

	private String type;
	private String imei;
	private String imsi;
	private String clientVersion;

//	@SerializedName("password")
//	public String password = null;

	@SerializedName("status")
	public String iStatus = null;

	@SerializedName("userId")
	public long iUserId;

	@SerializedName("mobileNumber")
	public String iMobileNumber = null;
	
	@SerializedName("domainName")
	public String domainName = null;
	
	@SerializedName("email")
	public String email = null;
	
	@SerializedName("adminName")
	public String adminName = null;
	
	@SerializedName("password")
	public String password = null;
	
	@SerializedName("orgName")
	public String orgName = null;
	
	@SerializedName("orgUrl")
	public String orgUrl = null;
	
	@SerializedName("logoFileId")
	public String logoFileId = null;
	
	@SerializedName("displayName")
	public String displayName = null;
	
	@SerializedName("description")
	public String description = null;
	
	@SerializedName("privacyType")
	public String privacyType = null;
	
	@SerializedName("domainType")
	private String domainType = null;
	
	public String getDomainType() {
		return domainType;
	}

	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}

	public String getPrivacyType() {
		return privacyType;
	}

	public void setPrivacyType(String privacy_type) {
		this.privacyType = privacy_type;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@SerializedName("countryCode")
	public String countryCode = null;
	
	@SerializedName("token")
	public String token = null;
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public AdminRegistrationForm() {

	}

	public AdminRegistrationForm(String mobileNumber, String type, String imei,
			String imsi, String clientVersion) {
		this.iMobileNumber = mobileNumber;
		this.type = type;
		this.imei = imei;
		setToken(imei);
		this.imsi = imsi;
		this.clientVersion = clientVersion;
	}

	public String toString() {
		return String.format(TO_STRING_TEMPLATE, iMobileNumber, type, imei, imsi, clientVersion);
	}

//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}

	public String getMobileNumber() {
		return iMobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.iMobileNumber = mobileNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	
	public String getAdminEmail() {
		return email;
	}
	public void setAdminEmail(String admin_email) {
		this.email = admin_email;
	}
	
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String admin_name) {
		this.adminName = admin_name;
	}
	public String getAdminOrgName() {
		return orgName;
	}
	public void setAdminOrgName(String admin_or_name) {
		this.orgName = admin_or_name;
	}
	public String getAdminOrgURL() {
		return orgUrl;
	}
	public void setAdminOrgURL(String org_url) {
		this.orgUrl = org_url;
	}
	public String getAdminPassword() {
		return password;
	}
	public void setAdminPassword(String password) {
		this.password = password;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getSGFileID() {
		return logoFileId;
	}
	public void setSGFileID(String logoFileId) {
		this.logoFileId = logoFileId;
	}

}