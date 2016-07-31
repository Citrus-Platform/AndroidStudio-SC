package com.superchat.model;

import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.superchat.model.LoginResponseModel.UserResponseDetail.PrivacyStatusMap;

public class ContactUpDatedModel {

	@SerializedName(value = "emailUserBaseMap")
	public Map<String, UserDetail> emailUserBaseMap;

	@SerializedName(value = "mobileNumberUserBaseMap")
	public Map<String, UserDetail> mobileNumberUserBaseMap;

	ContactUpDatedModel() {
		super();
	}

	public static class UserDetail {

		@SerializedName(value = "userName")
		public String userName = null;
		@SerializedName("name")
		public String name = null;
		@SerializedName("mobileNumber")
		public String mobileNumber = null;
		@SerializedName("empId")
		public String empId = null;
		@SerializedName("gender")
		public String gender = null;
		@SerializedName("designation")
		public String designation = null;
		@SerializedName("department")
		public String department = null;
		@SerializedName("type")
		public String type = null;
		@SerializedName("currentStatus")
		public String currentStatus = null;
		@SerializedName("imageFileId")
		public String imageFileId = null;
		@SerializedName("userState")
		public String userState = null;
		//Address details
		@SerializedName("flatNumber")
		public String flatNumber = null;
		@SerializedName("buildingNumber")
		public String buildingNumber = null;
		@SerializedName("address")
		public String address = null;
		@SerializedName("residenceType")
		public String residenceType = null;
				
		@SerializedName(value = "privacyStatusMap")
		private PrivacyStatusMap privacyStatusMap;
		public PrivacyStatusMap getPrivacyStatusMap() {
			return privacyStatusMap;
		}
		public void setPrivacyStatusMap(PrivacyStatusMap privacyStatusMap) {
			this.privacyStatusMap = privacyStatusMap;
		}
		public static class PrivacyStatusMap {

			@SerializedName("DNC")
			public int dnc;

			@SerializedName("DNM")
			public int dnm;
			
//			@SerializedName("DND")
//			public int dnd;

		}
		
	}
}