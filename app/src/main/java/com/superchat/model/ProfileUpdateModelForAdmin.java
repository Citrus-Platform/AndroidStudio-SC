/**
 * 
 */
package com.superchat.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author maheshsonker
 * Date - 21-01-2016
 */
public class ProfileUpdateModelForAdmin {

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
	public String userId= "";
	@SerializedName(value = "location") 
	public String location= "";
	@SerializedName(value = "email") 
	public String email= "";
	@SerializedName(value = "gender") 
	public String gender= "";
	@SerializedName(value = "flatNumber") 
	private String flatNumber;
	@SerializedName(value = "buildingNumber") 
	private String buildingNumber;
	@SerializedName(value = "residenceType") 
	private String residenceType;
	@SerializedName(value = "address") 
	private String address;
	
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
	
	
	
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public ProfileUpdateModelForAdmin() {
        super();
    }
	
}
