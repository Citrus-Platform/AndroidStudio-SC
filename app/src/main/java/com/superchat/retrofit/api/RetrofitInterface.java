package com.superchat.retrofit.api;

import com.superchat.model.BulletinGetMessageDataModel;
import com.superchat.model.BulletinMessageDataModel;
import com.superchat.model.UserProfileModel;
import com.superchat.retrofit.request.model.UserAdminRequest;
import com.superchat.retrofit.response.model.UserAdminResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by MotoBeans on 12/16/2015.
 */
public interface RetrofitInterface {

    String PREFIX_URL = "/tiger/rest/";

    @POST(PREFIX_URL + "user/makesubadmin/")
    Call<UserAdminResponse> makeAdmin(@Body UserAdminRequest requestObject);

    @POST(PREFIX_URL + "user/removesubadmin /")
    Call<UserAdminResponse> removeAdmin(@Body UserAdminRequest requestObject);

    @GET(PREFIX_URL + "user/profile/get")
    Call<UserProfileModel> getUserProfile(@Query("userName") String userName);

    @GET(PREFIX_URL + "user/bulletin/get")
    Call<BulletinGetMessageDataModel> getMessages(@Query("limit") String minId);

    @GET
    Call<BulletinGetMessageDataModel> getMoreMessages(@Url String url);

    @POST(PREFIX_URL + "user/bulletin/post")
    Call<BulletinMessageDataModel> postBulletinMessage(@Body BulletinMessageDataModel.MessageData requestObject);

}