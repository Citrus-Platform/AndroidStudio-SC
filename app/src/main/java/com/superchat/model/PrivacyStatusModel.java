package com.superchat.model;


import com.google.gson.annotations.SerializedName;


public class PrivacyStatusModel {
public static final String TAG = "PrivacyStatusModel";
	@SerializedName(value = "privacyStatusMap")
	private PrivacyTypes privacyTypes;

	public PrivacyTypes getPrivacyTypes() {
		return privacyTypes;
	}

	public void setPrivacyTypes(PrivacyTypes privacyTypes) {
		this.privacyTypes = privacyTypes;
	}

	public PrivacyStatusModel() {
		super();
	}

	public static class PrivacyTypes {

		@SerializedName("DNC")
		public int dnc;

		@SerializedName("DNM")
		public int dnm;
		
//		@SerializedName("DND")
//		public int dnd;

	}

}