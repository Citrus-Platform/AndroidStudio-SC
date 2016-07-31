package com.superchat.model;

import com.google.gson.annotations.SerializedName;

public class RegMatchCodeModel {

	@SerializedName("status")
	public String  iStatus = null;
	
	@SerializedName("message")
	public String  iMessage = null;
	
	@SerializedName("username")
	public String  username = null;
	
	@SerializedName("password")
	public String  password = null;
}
