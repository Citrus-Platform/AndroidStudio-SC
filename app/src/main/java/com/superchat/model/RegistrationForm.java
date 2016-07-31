package com.superchat.model;

import com.google.gson.annotations.SerializedName;

public class RegistrationForm {

	private static final String TO_STRING_TEMPLATE = "mobileNumber: %s, type: %s, imei: %s, imsi: %s, clientVersion: %s";

//	private String mobileNumber;

	private String type;
	private String imei;
	private String imsi;
	private String clientVersion;

	@SerializedName("password")
	public String password = null;

	@SerializedName("status")
	public String iStatus = null;

	@SerializedName("userId")
	public long iUserId;
	
	@SerializedName("mobileNumber")
	public String iMobileNumber = null;

	@SerializedName("pendingProfile")
	public boolean pendingProfile = false;
	
	@SerializedName("domainName")
	public String domainName = null;
	
	public boolean isPendingProfile() {
		return pendingProfile;
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

	public RegistrationForm() {

	}

	public RegistrationForm(String mobileNumber, String type, String imei,
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

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
}