package com.superchat.retrofit.api;

import com.superchat.retrofit.request.model.UserAdminRequest;
import com.superchat.retrofit.response.model.UserAdminResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by MotoBeans on 12/16/2015.
 */
public interface RetrofitInterface {

    String PREFIX_URL = "/tiger/rest/";

    @POST(PREFIX_URL + "/user/makeadmin/")
    Call<UserAdminResponse> makeAdmin(@Body UserAdminRequest requestObject);

    @POST(PREFIX_URL + "/user/removeadmin/")
    Call<UserAdminResponse> removeAdmin(@Body UserAdminRequest requestObject);

}