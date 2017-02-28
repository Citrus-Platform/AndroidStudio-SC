package com.superchat.ui;

import com.superchat.CustomAppComponents.Activity.CustomAppCompatActivityViewImpl;
import com.superchat.R;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

public class TermAndConditionScreen extends CustomAppCompatActivityViewImpl {
	private SharedPrefManager iPrefManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
//		iPrefManager = SharedPrefManager.getInstance();
//		String mobileNumber = iPrefManager.getUserPhone();
//		String userName = iPrefManager.getUserName();
//		if (isExistingUser(mobileNumber)) {
//			Intent intent = null;
//			if (isVerifiedUser(mobileNumber)) {
//				if (!isProfileExist(userName)) {
//					intent = new Intent(this, ProfileScreen.class);
//				} else
//					intent = new Intent(this, HomeScreen.class);
//			} else {
//				 intent = new Intent(this, MobileVerificationScreen.class);
//				 intent.putExtra(Constants.MOBILE_NUMBER_TXT,mobileNumber);
//				 intent.putExtra(Constants.COUNTRY_CODE_TXT,Constants.countryCode);
//			}
//			startActivity(intent);
//			finish();
//			return;
//		}
		
		setContentView(R.layout.terms_and_conditions);

		UtilSetFont.setFontMainScreen(this);

		TextView titleView = (TextView)findViewById(R.id.id_title);
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null && bundle.getString("TITLE")!=null)
			titleView.setText(bundle.getString("TITLE"));
		WebView wv;
		wv = (WebView) findViewById(R.id.id_web_view);
		wv.loadUrl("file:///android_asset/terms.html");
		((ImageView) findViewById(R.id.id_back_arrow)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
//		Tracker t = ((SuperChatApplication) getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Terms & Conditions Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());
	}

	private boolean isExistingUser(String mobileNumber) {
		if (mobileNumber == null)
			return false;
		boolean isRegisterd = iPrefManager.isMobileRegistered(mobileNumber);
		return isRegisterd;
	}

	private boolean isVerifiedUser(String mobileNumber) {
		if (mobileNumber == null)
			return false;
		boolean isVerified = iPrefManager.isMobileVerified(mobileNumber);
		return isVerified;
	}

	private boolean isProfileExist(String userName) {
		if (userName == null)
			return false;
		boolean isProfile = iPrefManager.isProfileAdded(userName);
		return isProfile;
	}

	@Override
	public void onBackPressed(){
		finish();
	    
	}
	public void onConfirmClick(View view) {
//		Intent intent = new Intent(this, RegistrationOptions.class);
		Intent intent = new Intent(this, RegistrationOptions.class);
		startActivity(intent);
		finish();
	}
}
