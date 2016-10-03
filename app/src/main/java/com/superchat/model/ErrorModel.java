package com.superchat.model;



import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ErrorModel {

	@SerializedName(value = "userId") 
	public long userId;
	@SerializedName(value = "status") 
	public String status;
	@SerializedName(value = "message") 
	public String message;
	@SerializedName(value = "citrusErrors") 
	public List<CitrusError> citrusErrors;
	public ErrorModel() {
        super();
        this.citrusErrors = new ArrayList<CitrusError>();
       
    }
	public static class CitrusError{
		
		@SerializedName(value = "code") 
		public String code;
		@SerializedName(value = "message") 
		public String message;


		public CitrusError() {
		}
	}
}