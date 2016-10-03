package com.superchat.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maheshsonker on 03/10/16.
 */

public class MarkSGActive {
    @SerializedName(value = "status")
    public String status;
    @SerializedName(value = "message")
    public String message;
    @SerializedName(value = "citrusErrors")
    public List<ErrorModel.CitrusError> citrusErrors;
    public MarkSGActive() {
        super();
        this.citrusErrors = new ArrayList<ErrorModel.CitrusError>();

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
