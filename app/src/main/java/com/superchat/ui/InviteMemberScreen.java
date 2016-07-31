package com.superchat.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.AddMemberModel;
import com.superchat.model.AddMemberResponseModel;
import com.superchat.model.ErrorModel;
import com.superchat.utils.Constants;
import com.superchat.utils.Countries;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.MyriadRegularTextView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class InviteMemberScreen extends FragmentActivity implements
		OnItemSelectedListener,OnClickListener {
	private String TAG = "InviteMemberScreen";
	private final int PICK_CONTACT = 121;
	
	AutoCompleteTextView mobileNumberView = null;
	TextView domainNameView = null;
	TextView countryNameView = null;
	ToggleButton toggle;
	TextView fethContact;
	AutoCompleteTextView memberEmail = null;
	AutoCompleteTextView memberName = null;
	AutoCompleteTextView memberDeparment = null;
	AutoCompleteTextView memberEmployeeId = null;
	AutoCompleteTextView memberDesignation = null;
	Button skipButtonView;
	Spinner genderSpinner = null;
	TextView countryCodeView = null;
	String formatedNumber = "";
	String gender = "donotdisclose";
	private android.content.DialogInterface.OnClickListener helpDialogListner;
	int indexCountryCode;
	Spinner spinner;
	String countryName;
	SharedPrefManager sharedPrefManager;
	ArrayAdapter<String> historyNumberAdapter;
//	ArrayAdapter<String> historyDomainAdapter;
	boolean isAdmin;
	public static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	public static final Pattern MOBILE_NUMBER_PATTERN_WITH_CC = Pattern.compile("^\\d{1,5}[-]\\d{6,12}$");
	public static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^\\d{8,16}$");
	public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,29}$");
	public static final Pattern NAME_PATTERN = Pattern.compile("^\\s*([A-Za-z. ]{3,})(\\s*([A-Za-z ]+?))?\\s*$");
	public static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-z0-9_.]*$");
	public static final Pattern MEMBER_DEPARTMENT_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-z0-9 _.@&-]*$");
	boolean inviteFromReg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		Bundle bundle = getIntent().getExtras();
		String reg_type = "ADMIN";
//		if(bundle.get(Constants.REG_TYPE) != null)
//			reg_type = bundle.get(Constants.REG_TYPE).toString();
		if(reg_type != null && reg_type.equals("ADMIN"))
			isAdmin = true;
		if(isAdmin)
			setContentView(R.layout.invite_member_screen);
		 toggle =(ToggleButton)findViewById(R.id.toggel_btn_pushnotification);
		 toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			        if (isChecked) {
			        	gender = "female";
			        } else {
			        	gender = "male";
			        }
			    }
			});
		countryNameView = (TextView) findViewById(R.id.id_country_name);
		domainNameView = (TextView) findViewById(R.id.id_domain_name);
		fethContact = (TextView) findViewById(R.id.id_fetch_contact);
		fethContact.setOnClickListener(this);
		mobileNumberView = (AutoCompleteTextView) findViewById(R.id.mobile_number);
		countryCodeView = (MyriadRegularTextView) findViewById(R.id.country_code);
		if(isAdmin){
			memberEmail = (AutoCompleteTextView) findViewById(R.id.id_email);
			memberName = (AutoCompleteTextView) findViewById(R.id.id_invite_name);
			memberDeparment = (AutoCompleteTextView) findViewById(R.id.id_department);
			memberEmployeeId = (AutoCompleteTextView) findViewById(R.id.id_empid);
			memberDesignation = (AutoCompleteTextView) findViewById(R.id.id_designation);
			skipButtonView = (Button)findViewById(R.id.id_next_btn);
			if(bundle != null && bundle.getBoolean(Constants.REG_TYPE)){
				inviteFromReg = true;
				skipButtonView.setVisibility(View.VISIBLE);
			}else{
				skipButtonView.setText(getString(R.string.done));
				skipButtonView.setVisibility(View.VISIBLE);
			}
			skipButtonView.setOnClickListener(this);
			genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
			// Spinner click listener
			genderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					// On selecting a spinner item
					gender = parent.getItemAtPosition(position).toString();
			        Log.i(TAG, "onItemSelected : Gender option : "+gender);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			
//			mobileNumberView.setText("9811030022");
//			memberDeparment.setText("android");
//			memberName.setText("mahehsh");
//			memberEmail.setText("masmas@gmail.com");
			memberEmployeeId.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE) {
						// Do whatever you want here
//						onNextButtonClicked(v);
						onDoneButtonClicked(v);
						return true;
					}
					return false;
				}
			});
		}
		spinner = (Spinner) findViewById(R.id.id_city_drop_down);
		spinner.setAdapter(new ArrayAdapter<Countries.Country>(this, R.layout.country_testview_screen, Countries.Country.values()));
		sharedPrefManager = SharedPrefManager.getInstance();
		domainNameView.setText(sharedPrefManager.getUserDomain());//getRecentDomains().split(",")[0]);
		sharedPrefManager.setFirstTime(true);
//		String[] str = sharedPrefManager.getRecentUsers().split(",");
//         historyNumberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, str);
//        mobileNumberView.setAdapter(historyNumberAdapter);
        
        sharedPrefManager.saveRecentDomains("p5domain");
//        str = sharedPrefManager.getRecentDomains().split(",");
//        historyDomainAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, str);
//        domainNameView.setAdapter(historyDomainAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View arg1,
					int arg2, long arg3) {
				indexCountryCode = arg2;
//				try {
//					((TextView) parentView.getChildAt(0)).setBackgroundColor(getResources().getColor(R.color.list_background));
//				} catch (Exception e) {
//
//				}

				String selected = ""
						+ ((Countries.Country) spinner.getSelectedItem()).getCode();

				countryName =  ((Countries.Country) spinner.getSelectedItem()).getStationName() ;
				countryCodeView.setText("+" + selected);
				countryNameView.setText(countryName);
				//				AppUtil.showTost(VerificationActivity.this, countryName) ;
				// Countries countries = new Countries() ;
				// countries
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		//Write code to detect Country Code, and show the selection be default :)
				try
				{
					String cc = "";
//					cc = getResources().getConfiguration().locale.getCountry();
//					if(cc == null || (cc != null && cc.trim().length() == 0))
					{
						TelephonyManager tm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
						cc = tm.getNetworkCountryIso();
						if(cc != null && cc.trim().length() != 0)
						{
							int ind = Countries.getCountryName(cc).getIndex();
							spinner.setSelection(ind > 0 ? ind : 0);
						}
					}
					if(cc != null && cc.trim().length() != 0)
					{
						int index = Countries.getCountryName(cc).getIndex();
						spinner.setSelection(index > 0 ? index : 0);
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				// AppUtil.showTost(VerificationActivity.this, ""+indexCountryCode) ;
		
		try{
			if (bundle != null) {
				String mobileNumber = bundle.get(Constants.MOBILE_NUMBER_TXT).toString();
				String countryCode = bundle.getString(Constants.COUNTRY_CODE_TXT);
				if (mobileNumber != null)
					mobileNumberView.setText(mobileNumber);
				if (countryCode != null)
					countryCodeView.setText(countryCode);
			}
			countryCodeView.setText(Constants.countryCode);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		helpDialogListner = new android.content.DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialoginterface, int i) {
				dialoginterface.dismiss();
			}

		};
		countryCodeView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				spinner.performClick();
				
			}
		});
countryNameView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				spinner.performClick();
				
			}
		});
//		Tracker t = ((SuperChatApplication) getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Login Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());
	}

//	public void onBackPressed(){
//		finish();
//	}
	
	public void onDoneButtonClicked(View view) {
		if (validateInputForRegistration()) {
//			sharedPrefManager.saveUserOrgName("");
//			sharedPrefManager.saveDisplayName("");
			AddMemberModel requestForm = null;
			String input = mobileNumberView.getText().toString();
//			if(input!=null && !sharedPrefManager.getRecentUsers().contains(input))
//				historyNumberAdapter.add(input);
			sharedPrefManager.saveRecentUsers(input);
			
			 input = domainNameView.getText().toString();
//			if(input!=null && !sharedPrefManager.getRecentDomains().contains(input))
//				historyDomainAdapter.add(input);
			sharedPrefManager.saveRecentDomains(input);
			
			formatedNumber = countryCodeView.getText().toString().replace("+", "")+"-"+mobileNumberView.getText();
			String imei = SuperChatApplication.getDeviceId();
			String imsi = SuperChatApplication.getNetworkOperator();
			String version = "";
			try {
				version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
				if(version!=null && version.contains("."))
					version = version.replace(".", "_");
				if(version==null)
					version = "";
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String clientVersion = "Android_"+version;
			List<AddMemberModel.MemberDetail> list = new ArrayList<AddMemberModel.MemberDetail>();
			AddMemberModel.MemberDetail memberDetail = new AddMemberModel.MemberDetail();
			memberDetail.name = memberName.getText().toString();
			if(gender != null && !gender.equals("")){
				if(gender.equalsIgnoreCase("Do not want to disclose") || gender.equals("Select Gender"))
					gender = "donotdisclose";
			}
//			memberDetail.gender = gender;
			memberDetail.mobileNumber = formatedNumber = countryCodeView.getText().toString().replace("+", "")+"-"+mobileNumberView.getText();//mobileNumberView.getText().toString();
			memberDetail.department = memberDeparment.getText().toString();
			String designation = memberDesignation.getText().toString();
			if(designation!=null && !designation.equals(""))
				memberDetail.designation = designation;
			memberDetail.email = memberEmail.getText().toString();
			String empId = memberEmployeeId.getText().toString();
			if(empId!=null && !empId.equals(""))
					memberDetail.empId = empId;
			list.add(memberDetail);
			requestForm = new AddMemberModel(list);//formatedNumber, "normal",imei, imsi, clientVersion);
			requestForm.domainName = domainNameView.getText().toString();
			if(Build.VERSION.SDK_INT >= 11)
				new InviteMemberServerTask(requestForm, view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new InviteMemberServerTask(requestForm, view).execute();
		}
	}
	public void onNextButtonClicked(View view){
		if (validateInputForRegistration()) {
			Intent intent = new Intent(InviteMemberScreen.this,MemberInviteWithGroupOrBroadCast.class);
			AddMemberModel requestForm = null;
			List<AddMemberModel.MemberDetail> list = new ArrayList<AddMemberModel.MemberDetail>();
			AddMemberModel.MemberDetail memberDetail = new AddMemberModel.MemberDetail();
			memberDetail.name = memberName.getText().toString();
			memberDetail.mobileNumber = formatedNumber = countryCodeView.getText().toString().replace("+", "")+"-"+mobileNumberView.getText();//mobileNumberView.getText().toString();
			memberDetail.department = memberDeparment.getText().toString();
			String designation = memberDesignation.getText().toString();
			if(designation!=null && !designation.equals(""))
				memberDetail.designation = designation;
			memberDetail.email = memberEmail.getText().toString();
			String empId = memberEmployeeId.getText().toString();
			if(empId!=null && !empId.equals(""))
					memberDetail.empId = empId;
			list.add(memberDetail);
			requestForm = new AddMemberModel(list);//formatedNumber, "normal",imei, imsi, clientVersion);
			requestForm.domainName = domainNameView.getText().toString();
			Bundle bundle = new Bundle();
			bundle.putSerializable(AddMemberModel.TAG, requestForm);
			if(requestForm!=null)
				intent.putExtras(bundle);
			startActivity(intent);
		}
	}
	private boolean validateInputForRegistration() {
		if(memberName.getText().toString().trim().length() < 3){
			showDialog(getString(R.string.please_enter_name));
			return false;
		}
		if(!checkName(memberName.getText().toString())){
			showDialog(getString(R.string.please_enter_valid_name));
			return false;
		}
		if(!checkUserDomainName(domainNameView.getText().toString())){
			showDialog(getString(R.string.domain_validation_alert));
			return false;
		}
		if(countryCodeView.getText().toString().equals("")){
			showDialog(getString(R.string.country_validation));
			return false;
		}
		if(!checkMobileNumber(mobileNumberView.getText().toString())){
			showDialog(getString(R.string.mobile_validation_length));
			return false;
		}
//		if(isAdmin){
//			if(!checkEmail(memberEmail.getText().toString())){
//				showDialog(getString(R.string.please_enter_email));
//				return false;
//			}
			
//			if(!checkUserOrgName(memberDeparment.getText().toString())){
//				showDialog(getString(R.string.please_enter_org_name));
//				return false;
//			}
//		}
		return true;
	}
	
//	Handler dialogHandler = new Handler(){
//		public void handleMessage(android.os.Message msg) {
//			switch(msg.what){
//			case 0:
//				if (progressDialog != null) {
//					progressDialog.dismiss();
//					progressDialog = null;
//				}
//				break;
//			case 1:
//				progressDialog = ProgressDialog.show(InviteMemberScreen.this, "",
//						"Loading. Please wait...", true);
//				break;
//			}
//		};
//	};
	public class SignupTaskOnServer extends AsyncTask<String, String, String> {
		AddMemberModel requestForm;
		ProgressDialog progressDialog = null;
		View view1;
		public SignupTaskOnServer(AddMemberModel requestForm,final View view1){
			this.requestForm = requestForm;
			this.view1 = view1;
		}
		@Override
		protected void onPreExecute() {		
			progressDialog = ProgressDialog.show(InviteMemberScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String JSONstring = new Gson().toJson(requestForm);		    
		    DefaultHttpClient client1 = new DefaultHttpClient();
		    
			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			
			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/admin/inviteuser");
//	         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
			 HttpResponse response = null;
			 
	         try {
				httpPost.setEntity(new StringEntity(JSONstring));
				 try {
					 response = client1.execute(httpPost);
					 final int statusCode=response.getStatusLine().getStatusCode();
					 if (statusCode == HttpStatus.SC_OK){ //new1
						 HttpEntity entity = response.getEntity();
//						    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
						    String line = "";
				            String str = "";
				            while ((line = rd.readLine()) != null) {
				            	
				            	str+=line;
				            }
				            if(str!=null &&!str.equals("")){
						            	str = str.trim();
						            	Log.d(TAG, "InviteMemberScreen sync response: "+str);
						            	Gson gson = new GsonBuilder().create();
										if (str==null || str.contains("error")){
//											ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
//											if (errorModel != null) {
//												if (errorModel.citrusErrors != null
//														&& !errorModel.citrusErrors.isEmpty()) {
//													ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
//													showDialog(citrusError.message);
//												} else if (errorModel.message != null)
//													showDialog(errorModel.message);
//											} else
//												showDialog("Please try again later.");
											return str;
										}
//										AddMemberModel regObj = gson.fromJson(str,
//												AddMemberModel.class);
//										if (regObj != null) {
//			
//											SharedPrefManager iPrefManager = SharedPrefManager
//													.getInstance();
//			
//											if (iPrefManager != null
//													&& iPrefManager.getUserId() != 0) {
//												if (iPrefManager.getUserId() != regObj.iUserId) {
//													try {
//														DBWrapper.getInstance().clearMessageDB();
//														iPrefManager.clearSharedPref();
//													} catch (Exception e) {
//													}
//												}
//											}
//											Log.d(TAG, "Esia chat registration password mobileNumber: " + regObj.getPassword()+" , "+regObj.iMobileNumber);
//											iPrefManager.saveUserDomain(domainNameView.getText().toString());
////											iPrefManager.saveUserName("m"+regObj.iMobileNumber.replace("-", ""));
////											iPrefManager.saveSipServerAddress(iPrefManager.getDisplayName()+"@"+Constants.CHAT_SERVER_URL+":5060");
//											iPrefManager.saveAuthStatus(regObj.iStatus);
//											iPrefManager.saveDeviceToken(regObj.token);
//											iPrefManager.saveUserId(regObj.iUserId);
//											iPrefManager.setAppMode("VirginMode");
//											iPrefManager.saveUserPhone(regObj.iMobileNumber);
////											iPrefManager.saveUserPassword(regObj.getPassword());
//											iPrefManager.saveUserLogedOut(false);
//											iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
//										}
//										Intent intent = new Intent(InviteMemberScreen.this, MobileVerificationScreen.class);
//										intent.putExtra(Constants.MOBILE_NUMBER_TXT,formatedNumber);
//										intent.putExtra(Constants.COUNTRY_CODE_TXT,
//												countryCodeView.getText());
//										if(isAdmin)
//											intent.putExtra(Constants.REG_TYPE, "ADMIN");
//										else
//											intent.putExtra(Constants.REG_TYPE, "USER");
//										startActivity(intent);
//										finish();
					            }
				            
				            }
				} catch (ClientProtocolException e) {
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				}
				 
			} catch (UnsupportedEncodingException e1) {
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:"+e1.toString());
			}catch(Exception e){
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
			}
		
		
			return null;
		}
		@Override
		protected void onPostExecute(String str) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
//			if (str!=null && str.contains("error")){
//				Gson gson = new GsonBuilder().create();
//				ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
//				if (errorModel != null) {
//					if (errorModel.citrusErrors != null
//							&& !errorModel.citrusErrors.isEmpty()) {
//						ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
//						if(citrusError!=null && citrusError.code.equals("20019") ){
//							SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//							iPrefManager.saveUserDomain(domainNameView.getText().toString());
//							iPrefManager.saveUserId(errorModel.userId);
//							iPrefManager.setAppMode("VirginMode");
////							iPrefManager.saveUserPhone(regObj.iMobileNumber);
//	//						iPrefManager.saveUserPassword(regObj.getPassword());
//							iPrefManager.saveUserLogedOut(false);
//							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
//							showDialogWithPositive(citrusError.message);
//						}else
//							showDialog(citrusError.message);
//					} else if (errorModel.message != null)
//						showDialog(errorModel.message);
//				} else
//					showDialog("Please try again later.");
//			}
			super.onPostExecute(str);
		}
		
		
	}
	Handler dialogHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int type = msg.what;
			switch(type){
			case 1:
				showDialog("User invitation sent",true);
				break;
			case 2:
				showDialog("User already added.",false);
				break;
			case 3:
				showDialog("User already added in this domain.",false);
				break;
			}
		}
	};
	private class InviteMemberServerTask extends AsyncTask<String, String, String> {
		AddMemberModel requestForm;
		ProgressDialog progressDialog = null;
		public InviteMemberServerTask(AddMemberModel requestForm,final View view1){
			this.requestForm = requestForm;
		}
		@Override
		protected void onPreExecute() {		
			progressDialog = ProgressDialog.show(InviteMemberScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String JSONstring = new Gson().toJson(requestForm);		    
			DefaultHttpClient client1 = new DefaultHttpClient();
			Log.d(TAG, "InviteMemberServerTask request:"+JSONstring);
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/admin/inviteuser");
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
						Log.d(TAG, "invite Result : "+str);
						if(str!=null &&!str.equals("")){
							str = str.trim();
							Gson gson = new GsonBuilder().create();
							if (str==null || str.contains("error")){
								return str;
							}
							AddMemberResponseModel regObj = gson.fromJson(str, AddMemberResponseModel.class);
							if (regObj != null) {
								if(regObj.accountCreated!=null && !regObj.accountCreated.isEmpty()){
									dialogHandler.sendEmptyMessage(1);//showDialog("User invitation sent",true);
								}else if(regObj.accountAlreadyExists!=null && !regObj.accountAlreadyExists.isEmpty()){
									dialogHandler.sendEmptyMessage(2);//showDialog("User already added.",false);
								}else if(regObj.accountFailed!=null && !regObj.accountFailed.isEmpty()){
									dialogHandler.sendEmptyMessage(3);//showDialog("User already added in this domain.",false);
								}								
							}
							return str;
//								SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//								if (iPrefManager != null
//										&& iPrefManager.getUserId() != 0) {
//									if (iPrefManager.getUserId() != regObj.iUserId) {
//										try {
//											DBWrapper.getInstance().clearMessageDB();
//											iPrefManager.clearSharedPref();
//										} catch (Exception e) {
//										}
//									}
//								}
//								Log.d(TAG, "Esia chat registration password mobileNumber: " + regObj.getPassword()+" , "+regObj.iMobileNumber);
//								iPrefManager.saveUserDomain(domainNameView.getText().toString());
//								iPrefManager.saveUserEmail(memberEmail.getText().toString());
//								iPrefManager.saveDisplayName(memberName.getText().toString());
//								iPrefManager.saveUserOrgName(memberDeparment.getText().toString());
//								iPrefManager.saveAuthStatus(regObj.iStatus);
//								iPrefManager.saveDeviceToken(regObj.token);
//								iPrefManager.saveUserId(regObj.iUserId);
//								iPrefManager.setAppMode("VirginMode");
//								iPrefManager.saveUserPhone(regObj.iMobileNumber);
//								iPrefManager.saveUserLogedOut(false);
//								iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
//							}
//							Intent intent = new Intent(InviteMemberScreen.this, MobileVerificationScreen.class);
//							intent.putExtra(Constants.MOBILE_NUMBER_TXT,formatedNumber);
//							intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCodeView.getText());
//							if(isAdmin)
//								intent.putExtra(Constants.REG_TYPE, "ADMIN");
//							else
//								intent.putExtra(Constants.REG_TYPE, "USER");
//							startActivity(intent);
//							finish();
						}
					}
				} catch (ClientProtocolException e) {
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				}
				
			} catch (UnsupportedEncodingException e1) {
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:"+e1.toString());
			}catch(Exception e){
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
			}
			
			
			return null;
		}
		@Override
		protected void onPostExecute(String str) {
			if (progressDialog != null) {
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
//						if(citrusError!=null && citrusError.code.equals("20019") ){
//							SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//							iPrefManager.saveUserDomain(domainNameView.getText().toString());
//							iPrefManager.saveUserId(errorModel.userId);
//							iPrefManager.setAppMode("VirginMode");
////							iPrefManager.saveUserPhone(regObj.iMobileNumber);
//							//						iPrefManager.saveUserPassword(regObj.getPassword());
//							iPrefManager.saveUserLogedOut(false);
//							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
//							showDialogWithPositive(citrusError.message);
//						}else 
						if(citrusError!=null)
							showDialog(citrusError.message);
						else
							showDialog("Please try again later.");
					} else if (errorModel.message != null)
						showDialog(errorModel.message);
				} else
					showDialog("Please try again later.");
			}else if (str!=null && str.contains("success")){
				if(memberEmail != null)
					memberEmail.setText("");
				if(memberName != null)
					memberName.setText("");
				if(memberDeparment != null)
					memberDeparment.setText("");
				if(memberEmployeeId != null)
					memberEmployeeId.setText("");
				if(memberDesignation != null)
					memberDesignation.setText("");
				if(mobileNumberView != null)
					mobileNumberView.setText("");
				genderSpinner.setSelection(0);
			}
			super.onPostExecute(str);
		}
		
		
	}
	
	public void showDialog1(String s) {
		Dialog bteldialog = new Dialog(this);
		MyriadRegularTextView myriadregulartextview = new MyriadRegularTextView(
				this);
		myriadregulartextview.setText(s);
		myriadregulartextview.setGravity(17);
		myriadregulartextview.setPadding(15, 15, 15, 15);
		bteldialog.setContentView(myriadregulartextview);
		bteldialog.setCancelable(true);
		myriadregulartextview.setTextColor(Color.parseColor("#ff4f4f4f"));
//		bteldialog.setSingleButton(getString(R.string.close), helpDialogListner);
		bteldialog.show();
	}
	public void showDialogWithPositive(String s) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				
//				iPrefManager.saveUserName("m"+regObj.iMobileNumber.replace("-", ""));
//				iPrefManager.saveSipServerAddress(iPrefManager.getDisplayName()+"@"+Constants.CHAT_SERVER_URL+":5060");
			Intent intent = new Intent(InviteMemberScreen.this, MobileVerificationScreen.class);
			intent.putExtra(Constants.MOBILE_NUMBER_TXT,formatedNumber);
			intent.putExtra(Constants.COUNTRY_CODE_TXT,
					countryCodeView.getText());
			if(isAdmin)
				intent.putExtra(Constants.REG_TYPE, "ADMIN");
			else
				intent.putExtra(Constants.REG_TYPE, "USER");
			startActivity(intent);
			finish();
				return false;
			}
		});
		bteldialog.show();
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
				return false;
			}
		});
		bteldialog.show();
	}
	public void showDialog(String s,final boolean needExit) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				if(needExit){
//					Intent intent = new Intent(InviteMemberScreen.this, HomeScreen.class);
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//							| Intent.FLAG_ACTIVITY_CLEAR_TASK
//							| Intent.FLAG_ACTIVITY_NEW_TASK);
//					startActivity(intent);
//					finish();
					}
				return false;
			}
		});
		bteldialog.show();
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String code = parent.getItemAtPosition(position).toString();
		countryCodeView.setText(code.substring(0, code.indexOf(' ')));
		// Toast.makeText(parent.getContext(),
		// "On Item Select : \n" +
		// parent.getItemAtPosition(position).toString(),
		// Toast.LENGTH_LONG).show();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}
//---------------- Validation methods ---------------
	public static boolean checkEmail(String emailstring) {
		if (emailstring == null)
			return false;
		Matcher m = EMAIL_PATTERN.matcher(emailstring);
		return m.matches();
	}
	public static boolean checkName(String name) {
		if (name == null)
			return false;
		Matcher m = NAME_PATTERN.matcher(name);
		return m.matches();
	}
	public static boolean checkUserName(String name) {
		if (name == null)
			return false;
		Matcher m = USERNAME_PATTERN.matcher(name);
		return m.matches();
	}
	public static boolean checkUserDomainName(String name) {
		if (name == null)
			return false;
		Matcher m = DOMAIN_NAME_PATTERN.matcher(name);
		return m.matches();
	}
	public static boolean checkUserOrgName(String name) {
		if (name == null)
			return false;
		Matcher m = MEMBER_DEPARTMENT_PATTERN.matcher(name);
		return m.matches();
	}
	public static boolean checkMobileNumber(String number) {
		if (number == null)
			return false;
		Matcher m = MOBILE_NUMBER_PATTERN.matcher(number);
		return m.matches();
	}
	public void onBackClick(View view){
		if(!inviteFromReg)
			finish();
	}
	@Override
	public void onBackPressed() {
		if(inviteFromReg)
			return;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_next_btn:
			if(inviteFromReg){
				Intent intent = new Intent(InviteMemberScreen.this, HomeScreen.class);
				intent.putExtra("ADMIN_FIRST_TIME", true);
				startActivity(intent);
				finish();
			}else
				onDoneButtonClicked(v);
			break;
		case R.id.id_fetch_contact:
			Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			 intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
			startActivityForResult(intent, PICK_CONTACT);
			break;
		}		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
	        if(requestCode == PICK_CONTACT){
	        	 Uri contactData = data.getData();
	                String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
	                        ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.PHOTO_THUMBNAIL_URI};
	                Cursor c = getContentResolver().query(contactData, projection, null, null, null);
	                c.moveToFirst();
	                int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
	                int phoneNumberIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
	                String name = c.getString(nameIdx);
	                String phoneNumber = c.getString(phoneNumberIdx);
	                if (name == null) {
	                    name = "";
	                }
	                mobileNumberView.setText(formatNumber(phoneNumber));
	                c.close();

	                // Now you have the phone number

	            
	        
	    }
	}
	}
	public String formatNumber(String str){
		try{
			if(str==null)
				return null;
			boolean isCountryCheckingNeeded = false;
			if(str.startsWith("00"))
			isCountryCheckingNeeded = true;
			if(str.length()>1)
				while(str.startsWith("0")){					
					if(str.length()>1)
						str = str.substring(1);
					else break;
				}
			
			
		boolean isPlus = str.contains("+")?true:false;
		if(isPlus)
			isCountryCheckingNeeded = true;
		
		str = str.replace(" ","");
		str = str.replace("+","");
		str = str.replace("-","");
		str = str.replace("(","");
		str = str.replace(")","");
		
		if(str.length()<8)
			return str;
		
		String replacingCode = null;
		boolean isNumberModified = false;
		if(isCountryCheckingNeeded){
			for(int i = 5;i>=1;i--){
				replacingCode = str.substring(0, i);
				if(SuperChatApplication.countrySet!=null && SuperChatApplication.countrySet.contains(replacingCode)){
					countryCodeView.setText("+"+replacingCode);
					countryNameView.setText(Countries.getCountryName1(replacingCode).name());
					str = str.replaceFirst(replacingCode, "");
	//				str = replacingCode+"-"+str.replaceFirst(replacingCode, "");
					isNumberModified = true;
					break;
				}
			}
		}
		if(!isNumberModified)
		{
			String code = Constants.countryCode.replace("+", "");
			if(str.startsWith(code))
				str = str.replaceFirst(code, "");
			else
				str = str;
//			if(str.startsWith(code))
//				str = code+"-"+str.replaceFirst(code, "");
//			else
//				str = code+"-"+str;
			countryCodeView.setText("+"+code);
			countryNameView.setText(Countries.getCountryName1(code).name());
		}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
}
