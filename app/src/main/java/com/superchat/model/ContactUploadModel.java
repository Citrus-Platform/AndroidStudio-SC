package com.superchat.model;



import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ContactUploadModel {
	private static final String TO_STRING_TEMPLATE = "userId: %s, emailList: %s, mobileNumberList: %s";

	private long userId;
	private List<String> emailList;
	private List<String> mobileNumberList;

	public String toString() {
		return String.format(TO_STRING_TEMPLATE, userId, emailList, mobileNumberList);
	}
	
	public ContactUploadModel(long userId, List<String> emailList, List<String> mobileNumberList) {
		this.userId = userId;
		this.emailList = emailList;
		this.mobileNumberList = mobileNumberList;
	}

	public ContactUploadModel() {
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public List<String> getEmailList() {
		return emailList;
	}

	public void setEmailList(List<String> emailList) {
		this.emailList = emailList;
	}

	public List<String> getMobileNumberList() {
		return mobileNumberList;
	}

	public void setMobileNumberList(List<String> mobileNumberList) {
		this.mobileNumberList = mobileNumberList;
	}
	}