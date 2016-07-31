package com.superchat.retrofit.api;

import com.superchat.retrofit.response.model.CommonResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by MotoBeans on 12/16/2015.
 */
public interface VoizdInterface {

    String PREFIX_URL = "/voizd/rest/api";

    @FormUrlEncoded
    @POST(PREFIX_URL + "/user/uid/guest/")
    Call<CommonResponse> getGuest(@FieldMap Map<String, String> options);
}