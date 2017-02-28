package com.superchat.CustomAppComponents.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.superchat.CustomAppComponents.Interface.CustomAppActivity;

/**
 * Created by Motobeans on 10/26/2016.
 */

public class CustomAppActivityImpl extends AppCompatActivity implements CustomAppActivity {
    private Activity activity = this;
    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initCustomAppActivityImpl();
    }

    public void initCustomAppActivityImpl(){
        activity = this;
        context = this;
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
