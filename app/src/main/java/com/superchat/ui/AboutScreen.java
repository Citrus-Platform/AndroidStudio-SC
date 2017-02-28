package com.superchat.ui;


import com.superchat.CustomAppComponents.Activity.CustomAppCompatActivityViewImpl;
import com.superchat.R;
import com.superchat.utils.SharedPrefManager;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutScreen extends CustomAppCompatActivityViewImpl {

TextView versionNumberView;
RelativeLayout termsView;
RelativeLayout policyView;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_screen);
		versionNumberView = (TextView)findViewById(R.id.current_no_txt);
		termsView = (RelativeLayout)findViewById(R.id.id_terms_service);
		policyView = (RelativeLayout)findViewById(R.id.id_privacy_policy);
		termsView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutScreen.this, TermAndConditionScreen.class);
				intent.putExtra("TITLE", getString(R.string.term_of_service));
				startActivity(intent);
			}
		});
		policyView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutScreen.this, TermAndConditionScreen.class);
				intent.putExtra("TITLE", getString(R.string.privacy_policy));
				startActivity(intent);
			}
		});
		
		String version = null;
		try {
			version = getString(R.string.current_version_lbl)+" "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(version!=null && !version.equals(""))
			versionNumberView.setText(version);

		
		//SharedPrefManager.getInstance()
//		((TextView)findViewById(R.id.domain_name)).setText(SharedPrefManager.getInstance().getUserDomain());
		
		(findViewById(R.id.about_back)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
//		(findViewById(R.id.support)).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
//				mailIntent.setType("text/plain");
//				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
//				mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@citrusplatform.com" });
//				final PackageManager pm = getPackageManager();
//				final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
//				ResolveInfo best = null;
//				for (final ResolveInfo info : matches)
//					if (info.activityInfo.packageName.endsWith(".gm") ||
//							info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
//				if (best != null)
//					mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
//				startActivity(mailIntent);
//			}
//		});
	}
}
