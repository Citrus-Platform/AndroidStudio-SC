package com.superchat.ui;

import com.superchat.R;
import com.superchat.utils.UtilSetFont;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

public class SmsRegistrationScreen extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sms_registration_screen);

        UtilSetFont.setFontMainScreen(this);
    }

    public void onRegisterButtonClick(View view) {

    }

    public void onLaterButtonClick(View view) {
        Intent intent = new Intent(this, LoginChangeScreen.class);
        startActivity(intent);
        finish();
    }
}
