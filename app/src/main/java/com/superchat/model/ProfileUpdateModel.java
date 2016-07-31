package com.superchat.model;

import com.google.gson.annotations.SerializedName;

public class ProfileUpdateModel {

	
	@SerializedName(value = "name") 
	public String name;
	@SerializedName(value = "userName") 
	public String userName;
	@SerializedName(value = "designation") 
	public String designation;
	@SerializedName(value = "department") 
	public String department;
	@SerializedName(value = "empId") 
	public String empId;
	@SerializedName(value = "userId") 
	public String userId;
	@SerializedName(value = "imageFileId") 
	public String imageFileId;
	@SerializedName(value = "currentStatus") 
	public String currentStatus;
	@SerializedName(value = "aboutMe") 
	public String aboutMe;
	@SerializedName(value = "location") 
	public String location;
	@SerializedName(value = "address") 
	public String address;
	@SerializedName(value = "dob") 
	public String dob;
	@SerializedName(value = "gender") 
	public String gender;
	//Address details
	@SerializedName("flatNumber")
	public String flatNumber = null;
	@SerializedName("buildingNumber")
	public String buildingNumber = null;
	@SerializedName("residenceType")
	public String residenceType = null;
	
	public String getFlatNumber() {
		return flatNumber;
	}
	public void setFlatNumber(String flatNumber) {
		this.flatNumber = flatNumber;
	}
	public String getBuildingNumber() {
		return buildingNumber;
	}
	public void setBuildingNumber(String buildingNumber) {
		this.buildingNumber = buildingNumber;
	}
	public String getResidenceType() {
		return residenceType;
	}
	public void setResidenceType(String residenceType) {
		this.residenceType = residenceType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getDesignation() {
		return designation;
	}
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getImageFileId() {
		return imageFileId;
	}

	public void setImageFileId(String imageFileId) {
		this.imageFileId = imageFileId;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public String getAbout() {
		return aboutMe;
	}
	
	public void setAbout(String aboutMe) {
		this.aboutMe = aboutMe;
	}
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getDOB() {
		return dob;
	}
	
	public void setDOB(String dob) {
		this.dob = dob;
	}

	
	
	public ProfileUpdateModel() {
        super();
    }

	
	}
