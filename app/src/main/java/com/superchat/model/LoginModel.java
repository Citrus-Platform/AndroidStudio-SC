package com.superchat.model;



import java.util.Map;

import com.google.gson.annotations.SerializedName;

public class LoginModel {

	public String userName;
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String password;
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String token;
	
//	@SerializedName(value = "userId") 
//	public String userId= "";
	@SerializedName(value = "status") 
	public String status;
	@SerializedName(value = "message") 
	public String message;
	@SerializedName(value = "directoryUserSet")
	public Map<String, UserDetail> directoryUserSet;
	
	@SerializedName(value = "clientVersion") 
		private String clientVersion;
	public String getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public LoginModel() {
        super();
       
    }
	
	public static class UserDetail {

		@SerializedName(value = "userName")
		public String userName = null;
		
//		@SerializedName("state")
//		public String iState = null;
//
//		@SerializedName("userId")
//		public long iUserId;

//		@SerializedName("presenceStatus")
//		public String presenceStatus = null;
		
//		@SerializedName("type")
//		public String type = null;
		
		@SerializedName("name")
		public String name = null;
		@SerializedName("mobileNumber")
		public String mobileNumber = null;
		
//		@SerializedName("email")
//		public String email = null;
		
//		@SerializedName("profileUrl")
//		public String profileUrl = null;
//		@SerializedName("imageFileId")
//		public String imageFileId = null;
//		@SerializedName("currentStatus")
//		public String currentStatus = null;
		
	}
	}