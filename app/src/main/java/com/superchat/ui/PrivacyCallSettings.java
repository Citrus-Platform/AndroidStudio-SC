package com.superchat.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.android.gms.maps.internal.IProjectionDelegate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.AddMemberModel;
import com.superchat.model.AddMemberResponseModel;
import com.superchat.model.ErrorModel;
import com.superchat.model.PrivacyStatusModel;
import com.superchat.ui.GroupProfileScreen.ChannelLeaveJoinOnInfo;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PrivacyCallSettings extends Activity implements OnClickListener {
	public static final String TAG = "PrivacyCallSettings";
	LinearLayout allCallAllowLayout;
	LinearLayout noCallAllowLayout;
	ImageView noCallAllowCheckBox;
	ImageView allCallAllowCheckBox;
	TextView noCallInfoView;
	TextView backView;
	SharedPrefManager pref;
	private byte CALL_PREV_PRIVACY_TYPE = 0;
	private byte CALL_PRIVACY_TYPE = 0;
	private final byte ALL_CALLS_ALLOWED = 0;
	private final byte NO_CALLS_ALLOWED = 1;
	// XmppChatClient chatClient;
    private ChatService messageService;
	  private ServiceConnection mMessageConnection = new ServiceConnection() {
	        public void onServiceConnected(ComponentName className, IBinder binder) {
	            messageService = ((ChatService.MyBinder) binder).getService();
	            Log.d("Service", "Connected");
	        }

	        public void onServiceDisconnected(ComponentName className) {
	            messageService = null;
	        }
	    };
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.call_block_settings);
		pref = SharedPrefManager.getInstance();
		allCallAllowLayout = (LinearLayout)findViewById(R.id.id_all_call_allow_layout);
		noCallAllowLayout = (LinearLayout)findViewById(R.id.id_no_call_allow_layout);
		allCallAllowCheckBox = (ImageView)findViewById(R.id.id_all_call_allow_check);
		noCallAllowCheckBox = (ImageView)findViewById(R.id.id_no_call_allow_check);
		noCallInfoView = (TextView)findViewById(R.id.id_no_one_call_info);
		backView = (TextView)findViewById(R.id.id_back);
		backView.setOnClickListener(this);
		allCallAllowLayout.setOnClickListener(this);
		noCallAllowLayout.setOnClickListener(this);
		if(pref.isDNC(pref.getUserName()))
			CALL_PRIVACY_TYPE = NO_CALLS_ALLOWED;
		updateClick();
		CALL_PREV_PRIVACY_TYPE = CALL_PRIVACY_TYPE;
	}
	public void onResume(){
		super.onResume();
		bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);
	}
	public void onPause(){
		super.onPause();
		 try {
	            unbindService(mMessageConnection);
	        } catch (Exception e) {
	            // Just ignore that
	            Log.d("PrivacyCallSettings", "Unable to un bind");
	        }
	}
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.id_all_call_allow_layout:
			CALL_PRIVACY_TYPE = ALL_CALLS_ALLOWED;
			break;
		case R.id.id_no_call_allow_layout:
			CALL_PRIVACY_TYPE = NO_CALLS_ALLOWED;
			break;
		case R.id.id_back:
			updateServerInfo();
		break;
		}
		updateClick();
	}
	public void onBackPressed(){
//		super.onBackPressed();
		updateServerInfo();
	}
	private void updateServerInfo(){
		
				if(CALL_PREV_PRIVACY_TYPE != CALL_PRIVACY_TYPE){
					 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
					  NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
						boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
						if(isConnected){
							if(Build.VERSION.SDK_INT >= 11)
								new PrivacyCallServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							else
								new PrivacyCallServerTask().execute();
						}else{
							Toast.makeText(this, getString(R.string.error_network_connection), Toast.LENGTH_SHORT).show();
							finish();
						}
				}else 
					finish();
			
	}
private void updateClick(){
	switch(CALL_PRIVACY_TYPE){
	case ALL_CALLS_ALLOWED:
		noCallAllowCheckBox.setBackgroundResource(R.drawable.radio1);
		allCallAllowCheckBox.setBackgroundResource(R.drawable.radio);
		noCallInfoView.setVisibility(View.GONE);
		break;
	case NO_CALLS_ALLOWED:
		allCallAllowCheckBox.setBackgroundResource(R.drawable.radio1);
		noCallAllowCheckBox.setBackgroundResource(R.drawable.radio);
		noCallInfoView.setVisibility(View.VISIBLE);
		break;	
	}
}
private class PrivacyCallServerTask extends AsyncTask<String, String, String> {
	PrivacyStatusModel privacyStatusModel;
	
	ProgressDialog progressDialog = null;
	public PrivacyCallServerTask(){
		privacyStatusModel = new PrivacyStatusModel();
		PrivacyStatusModel.PrivacyTypes privacyTypes = new PrivacyStatusModel.PrivacyTypes();
		privacyTypes.dnc = CALL_PRIVACY_TYPE;
		privacyStatusModel.setPrivacyTypes(privacyTypes);
	}
	@Override
	protected void onPreExecute() {
//		progressDialog = ProgressDialog.show(PrivacyCallSettings.this, "", "Please wait...", true);
		progressDialog = new ProgressDialog(PrivacyCallSettings.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(String... params) {
		
		String JSONstring = new Gson().toJson(privacyStatusModel);		    
		DefaultHttpClient client1 = new DefaultHttpClient();
		Log.d(TAG, "PrivacyCallServerTask request:"+JSONstring); 
		HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/user/profile/update");
		 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
		 HttpResponse response = null;
		try {
			httpPost.setEntity(new StringEntity(JSONstring));
			try {
				response = client1.execute(httpPost);
				final int statusCode=response.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK){
					HttpEntity entity = response.getEntity();
					BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
					String line = "";
					String str = "";
					while ((line = rd.readLine()) != null) {
						
						str+=line;
					}
					Log.d(TAG, "PrivacyCallServerTask Result : "+str);
					if(str!=null &&!str.equals("")){
						str = str.trim();
						return str;
					}
				}
			} catch (ClientProtocolException e) {
				Log.d(TAG, "PrivacyCallServerTask during HttpPost execution ClientProtocolException:"+e.toString());
			} catch (IOException e) {
				Log.d(TAG, "PrivacyCallServerTask during HttpPost execution ClientProtocolException:"+e.toString());
			}
			
		} catch (UnsupportedEncodingException e1) {
			Log.d(TAG, "PrivacyCallServerTask during HttpPost execution UnsupportedEncodingException:"+e1.toString());
		}catch(Exception e){
			Log.d(TAG, "PrivacyCallServerTask during HttpPost execution Exception:"+e.toString());
		}
		
		
		return null;
	}
	@Override
	protected void onPostExecute(String str) {
		if (progressDialog != null) {
			if(progressDialog.isShowing())
				progressDialog.dismiss();
			progressDialog = null;
		}
		if (str!=null && str.contains("error")){
			Gson gson = new GsonBuilder().create();
			ErrorModel errorModel = null;
			try{
				errorModel = gson.fromJson(str,ErrorModel.class);
			}catch(Exception e){}
			if (errorModel != null) {
				if (errorModel.citrusErrors != null
						&& !errorModel.citrusErrors.isEmpty()) {
					ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
					if(citrusError!=null)
						showDialog(citrusError.message);
					else
						showDialog("Please try again later.");
				} else if (errorModel.message != null)
					showDialog(errorModel.message);
			} else
				showDialog("Please try again later.");
		}else if (str!=null && str.contains("success")){
//			{"status":"success","message":"Profile updated successfully"}
			Gson gson = new GsonBuilder().create();
			ErrorModel errorModel = null;
			try{
				errorModel = gson.fromJson(str,ErrorModel.class);
				if(errorModel.status.equals("success")){
					if(errorModel.message==null)
						errorModel.message = "Privacy updated successfully.";
					Toast.makeText(PrivacyCallSettings.this, errorModel.message, Toast.LENGTH_SHORT).show();
					if(CALL_PRIVACY_TYPE == NO_CALLS_ALLOWED)
						pref.saveStatusDNC(pref.getUserName(), true);
					else if(CALL_PRIVACY_TYPE == ALL_CALLS_ALLOWED)
						pref.saveStatusDNC(pref.getUserName(), false);
					sendPrivacyInfoToAll(privacyStatusModel);
					finish();
				}
			}catch(Exception e){}
			if (errorModel != null) {
			}
		}
		super.onPostExecute(str);
	}
	
	
}
private void sendPrivacyInfoToAll(PrivacyStatusModel privacyStatusModel){
	JSONObject finalJSONbject = new JSONObject();
	try {
		finalJSONbject.put("userName", pref.getUserName());
		if(privacyStatusModel!=null && privacyStatusModel.getPrivacyTypes()!=null){
			JSONObject privacyJsonObj = new JSONObject();
			privacyJsonObj.put("DNC", privacyStatusModel.getPrivacyTypes().dnc);
			finalJSONbject.put("privacyStatusMap", privacyJsonObj);
		}
		
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	if(messageService != null){
		String json = finalJSONbject.toString();
		Log.i(TAG, "Final JSON :  " + json);
//		json = json.replace("\"", "&quot;");
		messageService.sendSpecialMessageToAllDomainMembers(pref.getUserDomain() + "-system", json, XMPPMessageType.atMeXmppMessageTypeUserProfileUpdate);
		json = null;
	}
}
public void showDialog(String s) {
	final Dialog bteldialog = new Dialog(this);
	bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	bteldialog.setCanceledOnTouchOutside(false);
	bteldialog.setContentView(R.layout.custom_dialog);
	((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
	((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			bteldialog.cancel();
			finish();
			return false;
		}
	});
	bteldialog.show();
}
}
