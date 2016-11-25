package com.superchat.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;
public class GroupChatServerModel {
	@SerializedName(value = "userName") 
	public String userName;
	@SerializedName(value = "groupName") 
	public String groupName;
	@SerializedName(value = "broadcastGroupName") 
	public String broadcastGroupName;	
	@SerializedName(value = "type")
	private String type;
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBroadcastGroupName() {
		return broadcastGroupName;
	}

	public void setBroadcastGroupName(String broadcastGroupName) {
		this.broadcastGroupName = broadcastGroupName;
	}

	@SerializedName(value = "displayName") 
	public String displayName;
	@SerializedName(value = "description") 
	public String description;
	@SerializedName(value = "fileId") 
	public String fileId;
	@SerializedName(value = "domainName")
	public String domainName;
	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	@SerializedName(value = "adminUserSet") 
	private List<String> adminUserSet;
	
	@SerializedName(value = "memberUserSet") 
	private List<String> memberUserSet;

	public List<String> getRemoveUserSet() {
		return removeUserSet;
	}

	public void setRemoveUserSet(List<String> removeUserSet) {
		this.removeUserSet = removeUserSet;
	}

	@SerializedName(value = "removeUserSet")
	private List<String> removeUserSet;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public List<String> getAdminUserSet() {
		return adminUserSet;
	}

	public void setAdminUserSet(List<String> adminUserSet) {
		this.adminUserSet = adminUserSet;
	}

	public List<String> getMemberUserSet() {
		return memberUserSet;
	}

	public void setMemberUserSet(List<String> memberUserSet) {
		this.memberUserSet = memberUserSet;
	}
	
	public GroupChatServerModel() {
        super();
    }


	// Munish
	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}