package com.superchat.retrofit.api;

import com.superchat.model.BulletinGetMessageDataModel;
import com.superchat.model.BulletinMessageDataModel;
import com.superchat.model.LoginModel;
import com.superchat.model.LoginResponseModel;
import com.superchat.model.MarkSGActive;
import com.superchat.model.RegistrationForm;
import com.superchat.model.RegistrationFormResponse;
import com.superchat.model.SGroupListObject;
import com.superchat.model.UserProfileModel;
import com.superchat.model.multiplesg.MultipleSGObject;
import com.superchat.retrofit.request.model.ConferenceCalloutFromServerRequest;
import com.superchat.retrofit.request.model.ConferenceCalloutRequest;
import com.superchat.retrofit.request.model.UserAdminRequest;
import com.superchat.retrofit.response.model.ConferenceCalloutFromServerResponse;
import com.superchat.retrofit.response.model.ConferenceCalloutResponse;
import com.superchat.retrofit.response.model.ConferenceInfoResponse;
import com.superchat.retrofit.response.model.ResponseOpenDomains;
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

    @POST(PREFIX_URL + "user/removesubadmin/")
    Call<UserAdminResponse> removeAdmin(@Body UserAdminRequest requestObject);

    @GET(PREFIX_URL + "user/profile/get")
    Call<UserProfileModel> getUserProfile(@Query("userName") String userName);

    @GET(PREFIX_URL + "user/bulletin/get")
    Call<BulletinGetMessageDataModel> getBulletinMessages(@Query("limit") String minId);

    @GET
    Call<BulletinGetMessageDataModel> getMoreMessages(@Url String url);

    @POST(PREFIX_URL + "user/bulletin/post")
    Call<BulletinMessageDataModel> postBulletinMessage(@Body BulletinMessageDataModel.MessageData requestObject);

    @GET(PREFIX_URL + "user/markactive")
    Call<MarkSGActive> markSGActive(@Query("domainName") String domainName);

    @POST(PREFIX_URL + "user/activatedomain/")
    Call<RegistrationFormResponse> activateBulkSGs(@Body RegistrationForm requestObject);

    @POST(PREFIX_URL + "user/login/")
    Call<LoginResponseModel> validateAutoConflict(@Body LoginModel requestObject);

    @GET(PREFIX_URL + "user/domainprofiledata")
    Call<MultipleSGObject> getSGListForMobile(@Query("mobileNumber") String mobileNumber);

    @POST("/v1/callouts")
    Call<ConferenceCalloutResponse> callOut(@Body ConferenceCalloutRequest requestObject);

    @POST(PREFIX_URL + "sinch/groupcall")
    Call<ConferenceCalloutFromServerResponse> callOutFromServer(@Body ConferenceCalloutFromServerRequest requestObject);

    @GET("/conferences/id/")
    Call<ConferenceInfoResponse> getConferenceInfo(@Query("") String myConference);

    @GET(PREFIX_URL + "admin/opendomains")
    Call<ResponseOpenDomains> getOpenHubs(@Query("domainName") String domainName,
                                          @Query("mobileNumber") String mobileNumber);

    @GET
    Call<ResponseOpenDomains> getOpenHubsMore(@Url String url);

}