package com.superchat.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AddMemberResponseModel {

	@SerializedName(value = "status")
	public String status;
	@SerializedName(value = "message")
	public String message;
	@SerializedName(value = "accountCreated")
	public List<String> accountCreated;
	@SerializedName(value = "accountAlreadyExists")
	public List<String> accountAlreadyExists;
	@SerializedName(value = "accountFailed")
	public List<String> accountFailed;

	public AddMemberResponseModel() {
	}

}