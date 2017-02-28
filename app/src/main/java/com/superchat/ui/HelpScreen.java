package com.superchat.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.superchat.CustomAppComponents.Activity.CustomAppCompatActivityViewImpl;
import com.superchat.R;
import com.superchat.utils.UtilSetFont;

public class HelpScreen extends CustomAppCompatActivityViewImpl {


	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help_screen);

		UtilSetFont.setFontMainScreen(this);

		(findViewById(R.id.help_back)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		findViewById(R.id.layout02).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HelpScreen.this, ContactUsScreen.class);
				startActivity(intent);
			}
		});
	}
}
