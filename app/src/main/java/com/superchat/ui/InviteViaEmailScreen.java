package com.superchat.ui;

import java.util.List;

import com.superchat.R;
import com.superchat.utils.SharedPrefManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class InviteViaEmailScreen extends Activity implements OnClickListener{

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.invite_via_email_screen);
		((TextView)findViewById(R.id.id_back_txt)).setOnClickListener(this);
		((ImageView)findViewById(R.id.id_back)).setOnClickListener(this);
//		((TextView)findViewById(R.id.id_email_us)).setMovementMethod(LinkMovementMethod.getInstance());
//		<a href="mailto:bulkupload@citrusplatform.com">Email us</a> and let us know how we can help.
		TextView t3 = (TextView) findViewById(R.id.id_email_us);
		
		t3.setLinksClickable(true);
//        t3.setText(
//            Html.fromHtml(getString(R.string.email_us_and)));
//        t3.setMovementMethod(LinkMovementMethod.getInstance());
        t3.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
        t3.setOnClickListener(this);
	}
public void onClick(View view){
	switch(view.getId()){
	case R.id.id_email_us:
		final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
		mailIntent.setType("text/plain");
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, "SuperNova");
		mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"bulkupload@citrusplatform.com"});
		String msgData = "\n\nOwner: "+SharedPrefManager.getInstance().getDisplayName()+"\n Mobile: +"+SharedPrefManager.getInstance().getUserPhone().replace("-", "");
		mailIntent.putExtra(Intent.EXTRA_TEXT, msgData);
		final PackageManager pm = getPackageManager();
		final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
		ResolveInfo best = null;
		for (final ResolveInfo info : matches)
			if (info.activityInfo.packageName.endsWith(".gm") ||
					info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
		if (best != null)
			mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		startActivity(mailIntent);
		break;
	case R.id.id_back_txt:
	case R.id.id_back:
		finish();
		break;
	}
}
}
