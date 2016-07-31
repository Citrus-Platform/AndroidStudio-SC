package com.superchat.ui;

import com.superchat.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

public class LoginChangeScreen extends FragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_change_screen);
	}

	public void onExistingSignInClick(View view) {
		Intent intent = new Intent(this, HomeScreen.class);
		startActivity(intent);
		finish();
	}

	public void onNewSignInClick(View view) {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
