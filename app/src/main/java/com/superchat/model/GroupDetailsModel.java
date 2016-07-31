package com.superchat.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GroupDetailsModel {

	public GroupDetailsModel() {
		super();
	}

	@SerializedName(value = "groupId")
	public long groupId;
	@SerializedName(value = "fileId")
	public String fileId;
	@SerializedName(value = "groupName")
	public String groupName = null;

	@SerializedName(value = "displayName")
	public String displayName = null;
	
	@SerializedName(value = "ownerDisplayName")
	public String ownerDisplayName = null;

	@SerializedName(value = "userName")
	public String userName = null;

	@SerializedName(value = "description")
	public String description = null;
	
	@SerializedName(value = "type")
	public String type = null;

	@SerializedName(value = "memberUserSet")
	public List<String> memberUserSet = null;

	@SerializedName(value = "activatedUserSet")
	public List<String> activatedUserSet = null;

	@SerializedName(value = "adminUserSet")
	public List<String> adminUserSet = null;

}
