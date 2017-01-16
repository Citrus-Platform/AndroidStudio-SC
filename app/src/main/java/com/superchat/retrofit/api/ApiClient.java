package com.superchat.retrofit.api;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.superchat.SuperChatApplication;
import com.superchat.ui.SinchService;
import com.superchat.utils.Constants;
import com.superchat.utils.HttpHeaderUtils;
import com.superchat.utils.SharedPrefManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static ApiClient uniqInstance;
//    private final String API_URL_LIVE = "http://" + Constants.LIVE_DOMAIN;
    private final String API_URL_LIVE = Constants.LIVE_DOMAIN;
    private final String API_URL_SINCH = "https://callingapi.sinch.com";
//    private final String API_URL_SINCH = "https://api.sinch.com";

    private RetrofitInterface apiMazkara;
    private int type = 0;

    public static synchronized ApiClient getInstance() {
        if (uniqInstance == null) {
            uniqInstance = new ApiClient();
        }
        return uniqInstance;
    }

    private void ApiClient(@NonNull final Context currContext) {
        try {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // set your desired log level
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor headerInterceptor = new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder();
                    builder.header("Content-Type", "application/json").method(original.method(), original.body());

                    if(type == 0) {
                        String imei = SuperChatApplication.getDeviceId();
                        if (imei != null)
                            builder.header("ucid", imei);
                        try {
                            String version = currContext.getPackageManager().getPackageInfo(currContext.getPackageName(), 0).versionName;
                            if (version != null && version.contains("."))
                                version = version.replace(".", "_");
                            if (version == null)
                                version = "";
                            String clientVersion = "superchat_android_" + version;
                            if (clientVersion != null)
                                builder.header("cversion", clientVersion);

                            boolean withPassword = true;
                            if (withPassword) {
//                            System.out.println("[UserID - ] "+SharedPrefManager.getInstance().getUserId());
//                            System.out.println("[Pass - ] "+SharedPrefManager.getInstance().getUserPassword());
                                String auData = "'" + SharedPrefManager.getInstance().getUserId() + ":" + HttpHeaderUtils.encriptPass(SharedPrefManager.getInstance().getUserPassword()) + "'";
                                builder.header("audata", auData);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }else{
                        //sinch uses basic authentication
//                        String usernamePassword = "application:" + appKey + ":" + appSecret;
//                        String encoded = Base64.encodeToString(usernamePassword.getBytes(), Base64.NO_WRAP);
//                        httpGet.addHeader("Authorization", "Basic " + encoded);

                        ////application call
                        String usernameAndPassword = "application\\" + SinchService.APP_KEY + ":" + SinchService.APP_SECRET;
                        String auth = "basic" + " " + Base64.encodeToString(usernameAndPassword.getBytes(), Base64.NO_WRAP);
                        System.out.println("APIClient:: Authorization = "+auth);
                        builder.header("Authorization", auth);
                    }

                    Request request = builder.build();
                    return chain.proceed(request);
                }
            };

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(headerInterceptor)
                    .addInterceptor(logging)
                    .build();
            //httpClient.networkInterceptors().add(headerInterceptor);

            // <-- this is the important line!
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl((type == 0 ) ? API_URL_LIVE : API_URL_SINCH)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();

            apiMazkara = retrofit.create(RetrofitInterface.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RetrofitInterface getApi(Context currContext) {
        if (uniqInstance == null) {
            getInstance();
        }
        uniqInstance.ApiClient(currContext);

        return apiMazkara;
    }
    public void setRequestType(int type) {
        this.type = type;
    }
}