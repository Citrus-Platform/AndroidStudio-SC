package com.superchat.retrofit.api;

import android.app.Activity;
import android.content.Context;

import com.superchat.helper.UtilExceptionHandler;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by nuhbye on 07/03/16.
 */
public abstract class VoizdRetrofitCallback<S> implements Callback {
    UtilExceptionHandler objExceptionHandler = UtilExceptionHandler.getInstance();
    Activity activity;
    Context context;

    public VoizdRetrofitCallback(Activity activity) {
        this.activity = activity;
    }
    
    public VoizdRetrofitCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onResponse(Call call, Response response) {
        common();
        onResponseVoidzResponse(call, response);

        Object obj = response.body();
        if (obj != null) {
            S objectResponse = (S) obj;
            onResponseVoidzObject(call, objectResponse);
        }
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        common();
        //onFailureVoidz(call, t);
    }

    /**
     * Invoked for a received HTTP response.
     * <p>
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call {@link Response#isSuccess()} to determine if the response indicates success.
     */
    protected abstract void onResponseVoidzResponse(Call call, Response response);

    /**
     * Invoked for a received HTTP response.
     * <p>
     * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
     * Call {@link Response#isSuccess()} to determine if the response indicates success.
     */
    protected abstract void onResponseVoidzObject(Call call, S response);

    /**
     * Invoked when a network exception occurred talking to the server or when an unexpected
     * exception occurred creating the request or processing the response.
     */
    //protected customAbstract void onFailureVoidz(Call call, Throwable t);

    /**
     * Invoked everyTime
     */
    protected abstract void common();
}
