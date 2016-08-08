package com.superchat.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.superchat.R;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.Utilities;

import java.util.List;

public class RegistrationOptions extends FragmentActivity implements OnClickListener {

	private SharedPrefManager iPrefManager = null;
	Handler handler = new Handler();
	GalleryNavigator navi;
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		iPrefManager = SharedPrefManager.getInstance();
		String mobileNumber = iPrefManager.getUserPhone();
		String userName = iPrefManager.getUserName();
		if (isExistingUser(mobileNumber)) {
			Intent intent = null;
			if (isVerifiedUser(mobileNumber)) {
				if (!isProfileExist(userName)) {
					intent = new Intent(this, ProfileScreen.class);
				} else
					intent = new Intent(this, HomeScreen.class);
			}
			else {
				 intent = new Intent(this, MobileVerificationScreen.class);
				 intent.putExtra(Constants.MOBILE_NUMBER_TXT,mobileNumber);
				 intent.putExtra(Constants.COUNTRY_CODE_TXT,Constants.countryCode);
			}
			startActivity(intent);
			finish();
			return;
		}
		
		overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out); 
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tour_activity);
		final ViewPager pager = (ViewPager) findViewById(R.id.pager);

		FragmentManager fm = getSupportFragmentManager();
		navi = (GalleryNavigator) findViewById(R.id.count);
		navi.setSize(2);
		navi.setPosition(0);
		navi.setLoadedSize(0);
		navi.invalidate();
		final LoginFragmentAdopter pagerAdapter = new LoginFragmentAdopter(fm);
		//pagerAdapter.setFragments(getApplicationContext());
		pager.setAdapter(pagerAdapter);

//		findViewById(R.id.done).setOnClickListener(this);
		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {


				navi.setPosition(position);
				navi.invalidate();

//				if(position == 2)
//					findViewById(R.id.done).setVisibility(View.GONE);
//				else
//					findViewById(R.id.done).setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		((Button)findViewById(R.id.as_user_join_button)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegistrationOptions.this, MainActivity.class);
				intent.putExtra(Constants.REG_TYPE, "USER");
				intent.putExtra("TEMP_VERIFY", true);
				startActivity(intent);
				finish();
			}
		});
		((Button)findViewById(R.id.as_sg_join_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegistrationOptions.this, MainActivity.class);
				intent.putExtra(Constants.REG_TYPE, "ADMIN");
				intent.putExtra("TEMP_VERIFY", true);
				startActivity(intent);
				finish();
				
//				Intent intent = new Intent(RegistrationOptions.this, MainActivity.class);
//				intent.putExtra(Constants.MOBILE_NUMBER_TXT, "91-9910040529");
//				intent.putExtra(Constants.COUNTRY_CODE_TXT, "+91");
//					intent.putExtra("REGISTER_SG", true);
//					intent.putExtra(Constants.REG_TYPE, "ADMIN");
//				startActivity(intent);
//				finish();
			}
		});
		((ImageView)findViewById(R.id.id_support)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopupDialog();
			}
		});
		((TextView)findViewById(R.id.id_support_txt)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopupDialog();
			}
		});
	
//		Tracker t = ((SuperChatApplication) getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Register Options Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());
		boolean isMultipleLoggedIn = getIntent().getBooleanExtra("CONFLICT_LOGOUT", false);
		if(isMultipleLoggedIn){
			Toast.makeText(this, getString(R.string.reactivate_alert), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void showPopupDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.support_txt));
		builder.setPositiveButton(getString(R.string.contact_us_lbl), new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        // Do do my action here
		        dialog.dismiss();
		      //Need to send 
//				App version
//				Android or iOS version
//				Device model info
//				Country with city/state location
//				Time zone
				Intent mailIntent = new Intent(Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support");
				mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "support@citrusplatform.com" });
				mailIntent.putExtra(Intent.EXTRA_TEXT, Utilities.getSupportParameters(RegistrationOptions.this));
				final PackageManager pm = getPackageManager();
				final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
				ResolveInfo best = null;
				for (final ResolveInfo info : matches)
					if (info.activityInfo.packageName.endsWith(".gm") ||
							info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
				if (best != null)
					mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
				startActivity(mailIntent);
		    }

		});
		builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        // I do not need any action here you might
		        dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_back_title:
			finish();
			break;
		}
	}
//===========================================================
	class LoginFragmentAdopter extends FragmentPagerAdapter {

		final int PAGE_COUNT = 2;
		TourFragment fragA;
		TourFragment fragB;
		public LoginFragmentAdopter(FragmentManager fm) {
			super(fm);
			setFragments(getApplicationContext());
		}

		public void setFragments(Context c){
			fragA = new TourFragment(0,"en");
			fragB = new TourFragment(1,"en");
		
		}
		@Override
		public Fragment getItem(int position) {
			
			  Fragment frag = null;
		        if(position == 0){
		            frag = fragA;
		        }
		        else if(position == 1){
		            frag = fragB;
		        }
		        return frag;
		}
		@Override
		public int getCount() {
			return PAGE_COUNT;
		}
		@Override
		public CharSequence getPageTitle(int position) {
			return "Page #" + (position + 1);
		}
	}
}
