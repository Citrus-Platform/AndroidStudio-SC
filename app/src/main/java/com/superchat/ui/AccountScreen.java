package com.superchat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.superchat.R;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;

public class AccountScreen extends Activity {

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account_screen);

	}

	public void onBackClick(View view) {
		finish();
	}
}
