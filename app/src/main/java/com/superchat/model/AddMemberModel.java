package com.superchat.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import android.os.Parcel;
import android.os.Parcelable;

public class AddMemberModel implements Serializable {
public static final String TAG = "AddMemberModel";
	@SerializedName(value = "domainName")
	public String domainName;
	@SerializedName(value = "inviteUserDataList")
	public List<MemberDetail> directoryUserSet;

	public AddMemberModel(List<MemberDetail> directoryUserSet) {
		if (directoryUserSet != null)
			this.directoryUserSet = Collections.synchronizedList(directoryUserSet);
	}

	

	public static class MemberDetail implements Serializable{

		@SerializedName("designation")
		public String designation = null;

		@SerializedName("empId")
		public String empId = null;

		@SerializedName("name")
		public String name = null;
		@SerializedName("mobileNumber")
		public String mobileNumber = null;

		@SerializedName("department")
		public String department = null;

		@SerializedName("email")
		public String email = null;
		
//		@SerializedName("gender")
//		public String gender = null;
		
		@SerializedName("comment")
		public String comment = null;

		@SerializedName("groupCsv")
		public String groupCsv = null;

		@SerializedName("broadcastCsv")
		public String broadcastCsv;

	}

}