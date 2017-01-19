package com.superchat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.superchat.R;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.MyriadRegularTextView;

public class WelcomeScreen extends Activity implements OnClickListener {

	SharedPrefManager iPrefManager = null;
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_screen);

		UtilSetFont.setFontMainScreen(this);

		iPrefManager = SharedPrefManager.getInstance();
		String domain = iPrefManager.getUserDomain();
		final String name = iPrefManager.getDisplayName();
		String org = iPrefManager.getUserOrgName();
		if(domain != null && domain.trim().length() > 0)
			((MyriadRegularTextView)findViewById(R.id.id_domain_name)).setText("Your Domain : "+domain);
		if(name != null && name.trim().length() > 0)
			((MyriadRegularTextView)findViewById(R.id.id_inviters_name)).setText("Your Name : "+name);
		if(org != null && org.trim().length() > 0)
			((MyriadRegularTextView)findViewById(R.id.user_org_name)).setText("Your Org : "+org);
		
		((Button)findViewById(R.id.done_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(name.trim().length() > 0){
					Intent intent = new Intent(WelcomeScreen.this, HomeScreen.class);
					startActivity(intent);
//					intent = new Intent(WelcomeScreen.this, InviteMemberScreen.class);
//					startActivity(intent);
					intent = new Intent(WelcomeScreen.this, BulkInvitationScreen.class);
					startActivity(intent);
				}
				else
				{
					Intent intent = new Intent(WelcomeScreen.this, HomeScreen.class);
					startActivity(intent);
				}
				finish();
			}
		});
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_back_title:
			finish();
			break;
		}
	}
}
