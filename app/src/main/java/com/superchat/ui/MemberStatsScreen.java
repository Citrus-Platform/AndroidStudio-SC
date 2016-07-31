package com.superchat.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.ErrorModel;
import com.superchat.model.LoginResponseModel;
import com.superchat.model.LoginResponseModel.UserResponseDetail;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.MyriadSemiboldTextView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MemberStatsScreen  extends Activity implements OnClickListener{
	public final static String TAG = "MemberStatsScreen"; 
	MemberStatsAdapter statsAdapter;
	ListView statsList;
	int screenType;
	MyriadSemiboldTextView titleView;
	EditText addGroupUserBoxView;
	ArrayList<MemberStatsAdapter.UserInfo> list = null;
	RelativeLayout searchLayout;
	 protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.member_stats_screen);
		titleView = (MyriadSemiboldTextView)findViewById(R.id.id_contact_detail_title);
		addGroupUserBoxView = (EditText) findViewById(R.id.id_search_add);
		statsList = (ListView) findViewById(R.id.id_list_view);
		searchLayout = (RelativeLayout) findViewById(R.id.id_search_layout);
//		ArrayList<MemberStatsAdapter.UserInfo> list = new ArrayList<MemberStatsAdapter.UserInfo>();
		list = new ArrayList<MemberStatsAdapter.UserInfo>();
		MemberStatsAdapter.UserInfo info = new MemberStatsAdapter.UserInfo(getString(R.string.invited_members),getString(R.string.invited_members), null);
		list.add(info);
		info = new MemberStatsAdapter.UserInfo(getString(R.string.joined_member),getString(R.string.joined_member), null);
		list.add(info);
		info = new MemberStatsAdapter.UserInfo(getString(R.string.pending_invitation),getString(R.string.pending_invitation),  null);
		list.add(info);
		screenType = Constants.MEMBER_STATS;
		statsAdapter = new MemberStatsAdapter(this,R.layout.member_stats_item,list,screenType);
		statsAdapter.setClickListener(this);
		statsList.setAdapter(statsAdapter);
		addGroupUserBoxView.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable editable) {
				String search_string = addGroupUserBoxView.getText().toString().trim();
				if(search_string != null && search_string.length() > 0){
					ArrayList<MemberStatsAdapter.UserInfo> temp_list = query(search_string);
					statsAdapter = new MemberStatsAdapter(MemberStatsScreen.this, R.layout.member_stats_item, temp_list, screenType);
					if(temp_list != null){
						statsAdapter.setClickListener(MemberStatsScreen.this);
						statsList.setAdapter(null);
						statsList.setAdapter(statsAdapter);
					}else{
						statsAdapter.setClickListener(MemberStatsScreen.this);
						statsList.setAdapter(null);
					}
				}else{
					statsAdapter = new MemberStatsAdapter(MemberStatsScreen.this, R.layout.member_stats_item, list, screenType);
					statsAdapter.setClickListener(MemberStatsScreen.this);
					statsList.setAdapter(null);
					statsList.setAdapter(statsAdapter);
				}
			}

			public void beforeTextChanged(CharSequence charsequence, int i,
					int j, int k) {
			}

			public void onTextChanged(CharSequence charsequence, int i, int j,
					int k) {
			}

		});
		if (addGroupUserBoxView != null) {
			
	    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    	imm.hideSoftInputFromWindow(addGroupUserBoxView.getWindowToken(), 0);
    	}
    	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	 
	 public ArrayList<MemberStatsAdapter.UserInfo> query(String queryStr) {
		 ArrayList<MemberStatsAdapter.UserInfo> return_list = new ArrayList<MemberStatsAdapter.UserInfo>();
	    for (MemberStatsAdapter.UserInfo entry : list) {
	    	String num = "+"+entry.number.replace("-", "");
	        if (entry.displayName.toLowerCase().contains(queryStr.toLowerCase()) ||
	        		num.contains(queryStr.toLowerCase()))
	        	return_list.add(entry);
	    }
	    if (return_list.isEmpty())
	        return null;
	    else
	        return return_list;
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
//		
		switch(screenType){
		case Constants.MEMBER_STATS:
			if(R.id.id_member_stats_item == view.getId()){
//				if(SharedPrefManager.getInstance().isOpenDomain())
//					return;
				MemberStatsAdapter.ViewHolder holder = (MemberStatsAdapter.ViewHolder)view.getTag();
				if(holder!=null){
					String clickedValue = holder.displayNameView.getTag().toString();
					if(clickedValue!=null && !clickedValue.equals("")){
						if(clickedValue.equals(getString(R.string.joined_member)))
							new StatsTaskOnServer().execute("joined");
						else
							if(clickedValue.equals(getString(R.string.pending_invitation)))
								new StatsTaskOnServer().execute("unjoined");
					}
					Log.d(TAG, "clicked value : "+clickedValue);
				}
//				
//				new StatsTaskOnServer().execute("unjoined");
//				ArrayList<String> list = new ArrayList<String>();
//				list.add("ABCDE");
//				list.add("BBBBB");
//				list.add("BBBBB");
//				list.add("BBBBB");
//				
//				list.add(getString(R.string.pending_invitation));
//				statsAdapter = new MemberStatsAdapter(this,R.layout.member_stats_item,list,screenType);
//				statsList.setAdapter(null);
//				statsList.setAdapter(statsAdapter);
			}
			break;
			
		}
	}
	public void onBackClick(View view){
		if(addGroupUserBoxView != null){
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    	imm.hideSoftInputFromWindow(addGroupUserBoxView.getWindowToken(), 0);
		}
		switch(screenType){
		case Constants.MEMBER_STATS:
			finish();
			break;
			default:
				screenType = Constants.MEMBER_STATS;
				titleView.setText(getString(R.string.member_stats));
				if(searchLayout != null)
					searchLayout.setVisibility(View.GONE);
				ArrayList<MemberStatsAdapter.UserInfo> list = new ArrayList<MemberStatsAdapter.UserInfo>();
				MemberStatsAdapter.UserInfo info = new MemberStatsAdapter.UserInfo(getString(R.string.invited_members),getString(R.string.invited_members), null);
				list.add(info);
				info = new MemberStatsAdapter.UserInfo(getString(R.string.joined_member),getString(R.string.joined_member), null);
				list.add(info);
				info = new MemberStatsAdapter.UserInfo(getString(R.string.pending_invitation),getString(R.string.pending_invitation), null);
				list.add(info);
				statsAdapter = new MemberStatsAdapter(this,R.layout.member_stats_item,list,screenType);
				statsAdapter.setClickListener(this);
				statsList.setAdapter(null);
				statsList.setAdapter(statsAdapter);
		}
		
	}
	public void onBackPressed() {
		
		onBackClick(null);

	}
	private class StatsTaskOnServer extends AsyncTask<String, String, String> {
		ProgressDialog progressDialog = null;
		String urlType;
		public StatsTaskOnServer(){
			
			
		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(MemberStatsScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			urlType = urls[0];
			DefaultHttpClient client1 = new DefaultHttpClient();
			String line = "";
			String responseMsg = "";
//			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			String urlInfo = "";
//			http://52.88.175.48/tiger/rest/user/directory?domainName=government&type=joined			
				urlInfo = "/tiger/rest/user/directory?domainName="+iPrefManager.getUserDomain()+"&type="+urls[0]+"&pg=1&limit=1000";
			
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ urlInfo);
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			HttpResponse response = null;
			try {
//				httpPost.setEntity(new StringEntity(JSONstring));
				try {
					response = client1.execute(httpPost);
					final int statusCode=response.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK){
						HttpEntity entity = response.getEntity();
						//					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
						BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
						
						while ((line = rd.readLine()) != null) {
							responseMsg = responseMsg+line;
							Log.d(TAG, "serverUpdateCreateGroupInfo response: "+line);
						}
//						showDialog(line,"Ok");
					}
					//else
//						showDialog("Network error in add participant.","Ok");
				} catch (ClientProtocolException e) {
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				}
				
			} catch(Exception e){
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
			}
			return responseMsg;
		}
		
		@Override
		protected void onPostExecute(String response) {
			
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if (response!=null && response.contains("error")){
				Gson gson = new GsonBuilder().create();
				ErrorModel errorModel = null;
				try{
					errorModel = gson.fromJson(response,ErrorModel.class);
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
			}else{
				if (response!=null && !response.equals("") && response.contains("success")){
//					ArrayList<MemberStatsAdapter.UserInfo> list = new ArrayList<MemberStatsAdapter.UserInfo>();
					if(addGroupUserBoxView != null)
						addGroupUserBoxView.setText("");
					list = new ArrayList<MemberStatsAdapter.UserInfo>();
					
					Gson gson = new GsonBuilder().create();
					LoginResponseModel loginObj = gson.fromJson(response,LoginResponseModel.class);
					if(loginObj!=null){
						if(loginObj.directoryUserSet!=null){
							for (UserResponseDetail userDetail : loginObj.directoryUserSet) {
								String number = userDetail.mobileNumber;
//								if(number.contains("-"))
//									number = "+" + number.replace("-", "");
								MemberStatsAdapter.UserInfo info = new MemberStatsAdapter.UserInfo(userDetail.userName, userDetail.name, number);
								list.add(info);
							}
							if(urlType!=null && urlType.equals("joined")){
								if(!loginObj.directoryUserSet.isEmpty())
									titleView.setText(getString(R.string.joined_member));
								SharedPrefManager.getInstance().saveDomainJoinedCount(""+loginObj.directoryUserSet.size());
							}else if(urlType!=null && urlType.equals("unjoined")){
								if(!loginObj.directoryUserSet.isEmpty())
									titleView.setText(getString(R.string.pending_invitation));
								SharedPrefManager.getInstance().saveDomainUnjoinedCount(""+loginObj.directoryUserSet.size());
							}
					}
					if(loginObj.directoryUserSet!=null && !loginObj.directoryUserSet.isEmpty()){
						screenType = -1;
						if(urlType!=null && urlType.equals("unjoined"))
							statsAdapter = new MemberStatsAdapter(MemberStatsScreen.this,R.layout.member_stats_item,list,-11);
						else
							statsAdapter = new MemberStatsAdapter(MemberStatsScreen.this,R.layout.member_stats_item,list,-1);
						statsList.setAdapter(null);
						statsList.setAdapter(statsAdapter);
						if(searchLayout != null)
							searchLayout.setVisibility(View.VISIBLE);
					}else{
						showDialog("No member found.");
					}
					}
				}
				super.onPostExecute(response);
			}
		}
	}
	public void showDialog(String s) {
		final Dialog bteldialog = new Dialog(MemberStatsScreen.this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				return false;
			}
		});
		bteldialog.show();
	}
}
