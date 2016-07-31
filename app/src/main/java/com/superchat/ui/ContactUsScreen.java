package com.superchat.ui;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.chat.sdk.db.ChatDBWrapper;
import com.superchat.R;

public class ContactUsScreen extends Activity{


	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_us_screen);


		(findViewById(R.id.help_back)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		findViewById(R.id.layout01).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(getApplicationContext(), "Email is pending", Toast.LENGTH_SHORT).show();
				String selChat = "SuperChat Email";

				if (selChat != null && selChat.length() > 0 && !selChat.equals("")) {

					final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
					intent.putExtra(Intent.EXTRA_TEXT, selChat.trim());
					final PackageManager pm = getPackageManager();
					final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
					ResolveInfo best = null;
					for (final ResolveInfo info : matches)
						if (info.activityInfo.packageName.endsWith(".gm") ||
								info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
					if (best != null)
						intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
					startActivity(intent);
				}
			}
		});
	}
}
