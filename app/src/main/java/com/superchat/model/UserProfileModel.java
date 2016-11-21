package com.superchat.model;

import com.google.gson.annotations.SerializedName;
import com.superchat.model.ContactUpDatedModel.UserDetail.PrivacyStatusMap;

public class UserProfileModel {

//	@SerializedName("userbaseAPI")
//	public UserBaseAPI iUserBaseApi;
//
//	public static class UserBaseAPI {


		@SerializedName("clientVersion")
		public String clientVersion;


		@SerializedName("userId")
		public String iUserId;

		@SerializedName("empId")
		public String empId;
		
		@SerializedName("domainName")
		public String iDomainName = null;
		
		@SerializedName("userName")
		public String iUserName = null;

		@SerializedName("presenceStatus")
		public String iPresenceStatus = null;

		@SerializedName("type")
		public String iType = null;

		@SerializedName("name")
		public String iName = null;
		
		@SerializedName("currentStatus")
		public String currentStatus = null;
		
		@SerializedName("countryCode")
		public String iCountryCode = null;
		
		@SerializedName("mobileNumber")
		public String iMobileNumber = null;

		@SerializedName("email")
		public String iEmail = null;

		@SerializedName("profileUrl")
		public String iProfileUrl = null;

		@SerializedName("imageFileId")
		public String imageFileId = null;
		
		@SerializedName("userState")
		public String userState = null;
		
		@SerializedName("designation")
		public String designation = null;
		
		@SerializedName("department")
		public String department = null;
		
		@SerializedName("gender")
		public String gender = null;
		
		@SerializedName("status")
		public String iStatus = null;

		@SerializedName("message")
		public String iMessage = null;
		
		@SerializedName("aboutMe")
		public String aboutMe = null;
		@SerializedName("dob")
		public String dob = null;
		
		@SerializedName("flatNumber")
		public String flatNumber = null;
		
		@SerializedName("buildingNumber")
		public String buildingNumber = null;
		
		@SerializedName("residenceType")
		public String residenceType = null;
		
		@SerializedName("address")
		public String address = null;
		
		@SerializedName("location")
		public String location = null;
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
//	}

//	@SerializedName("status")
//	public String iStatus = null;
//
//	@SerializedName("message")
//	public String iMessage = null;

}
