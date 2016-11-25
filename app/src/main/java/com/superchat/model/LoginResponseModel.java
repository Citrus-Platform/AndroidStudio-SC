package com.superchat.model;



import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.annotations.SerializedName;
import com.superchat.ui.GroupRoleCreationAdapter.UserInfo;

public class LoginResponseModel {

	@SerializedName(value = "directoryUserSet")
	public Set<UserResponseDetail> directoryUserSet;
	
	@SerializedName(value = "directoryGroupSet")
	public Set<GroupDetail> directoryGroupSet;
	
	@SerializedName(value = "directoryBroadcastGroupSet")
	public Set<BroadcastGroupDetail> directoryBroadcastGroupSet;
	@SerializedName(value = "joinedUserCount") 
	public String joinedUserCount;
	@SerializedName(value = "unJoinedUserCount") 
	public String unJoinedUserCount;
	@SerializedName(value = "domainType") 
	private String domainType;
	public String getDomainType() {
		return domainType;
	}
	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}
	@SerializedName(value = "domainPrivacyType") 
	public String domainPrivacyType;
	@SerializedName(value = "status") 
	public String status;
	@SerializedName(value = "message") 
	public String message;
	@SerializedName(value = "type")
	public String type;
	@SerializedName(value = "loggedInDirectoryUser")
	public LoggedInDirectoryUser loggedInDirectoryUser;
	
	public LoginResponseModel() {
        super();
    }
	
	public static class LoggedInDirectoryUser {

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
		@SerializedName("currentStatus")
		public String currentStatus = null;
		@SerializedName("imageFileId")
		public String imageFileId = null;
		//Address details
		@SerializedName("flatNumber")
		public String flatNumber = null;
		@SerializedName("buildingNumber")
		public String buildingNumber = null;
		@SerializedName("address")
		public String address = null;
		@SerializedName("residenceType")
		public String residenceType = null;
				
		
	}
	public static class GroupDetail  implements Comparable{

		@SerializedName(value = "groupId")
		public long groupId;
		
		@SerializedName(value = "fileId")
		public String fileId;
		
		@SerializedName(value = "type")
		public String type;
		
		@SerializedName(value = "groupName")
		public String groupName = null;
		
		@SerializedName(value = "displayName")
		public String displayName = null;
		
		@SerializedName(value = "memberType")
		public String memberType = null;
		
		@SerializedName(value = "numberOfMembers")
		public String numberOfMembers = null;
		
		@SerializedName(value = "userDisplayName")
		public String userDisplayName = null;

		@SerializedName(value = "userName")
		public String userName = null;

		@SerializedName(value = "description")
		public String description = null;

		@SerializedName(value = "mode")
		public String mode = null;
		
		@SerializedName(value = "memberUserSet")
		public List<String> memberUserSet = null;
		
		@SerializedName(value = "activatedUserSet")
		public List<String> activatedUserSet = null;
		@Override
		public int compareTo(Object another) {
				String tmpName = ((GroupDetail)another).displayName;
			return this.displayName.compareToIgnoreCase(tmpName);
		}
	}
	public static class BroadcastGroupDetail  implements Comparable{

		@SerializedName(value = "broadcastGroupMemberId")
		public long broadcastGroupMemberId;
		@SerializedName(value = "fileId")
		public String fileId;
		@SerializedName(value = "broadcastGroupName")
		public String broadcastGroupName = null;
		
		@SerializedName(value = "displayName")
		public String displayName = null;
		
		@SerializedName(value = "userName")
		public String userName = null;

		@SerializedName(value = "userDisplayName")
		public String userDisplayName = null;

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
		@Override
		public int compareTo(Object another) {
				String tmpName = ((BroadcastGroupDetail)another).displayName;
			return this.displayName.compareToIgnoreCase(tmpName);
		}
	}
	public static class UserResponseDetail {

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
		@SerializedName("activationDate")
		public String activationDate = null;
		@SerializedName("imageFileId")
		public String imageFileId = null;
		@SerializedName("userState")
		public String userState = null;
		@SerializedName(value = "privacyStatusMap")
		private PrivacyStatusMap privacyStatusMap;
		//Address details
		@SerializedName("flatNumber")
		public String flatNumber = null;
		@SerializedName("buildingNumber")
		public String buildingNumber = null;
		@SerializedName("address")
		public String address = null;
		@SerializedName("residenceType")
		public String residenceType = null;
		
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