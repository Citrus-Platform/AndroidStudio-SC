package com.superchat.model;

import com.google.gson.annotations.SerializedName;

public class GroupResopnseModel {

	@SerializedName(value = "groupName")
	public String groupName;
	@SerializedName(value = "status")
	public String status;
	@SerializedName(value = "message")
	public String message;
	@SerializedName(value = "broadcastGroupName")
	public String broadcastGroupName;
	
	public GroupResopnseModel() {
		super();

	}
}