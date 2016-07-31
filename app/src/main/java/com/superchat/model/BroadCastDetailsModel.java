package com.superchat.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class BroadCastDetailsModel {
	@SerializedName(value = "broadcastGroupMemberId")
	public long broadcastGroupMemberId;
	
	@SerializedName(value = "broadcastGroupName")
	public String broadcastGroupName = null;
	
	@SerializedName(value = "displayName")
	public String displayName = null;
	
	@SerializedName(value = "fileId")
	public String fileId;
	
	@SerializedName(value = "userName")
	public String userName = null;
	
	@SerializedName(value = "state")
	public String state = null;
	
	@SerializedName(value = "description")
	public String description = null;
	
	@SerializedName(value = "memberUserSet")
	public List<String> memberUserSet = null;
	
	@SerializedName(value = "activatedUserSet")
	public List<String> activatedUserSet = null;
	
	@SerializedName(value = "adminUserSet")
	public List<String> adminUserSet = null;
}
