package com.superchat.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.ErrorModel;
import com.superchat.model.RegGenrateCodeModel;
import com.superchat.model.RegMatchCodeModel;
import com.superchat.model.SGroupListObject;
import com.superchat.model.SlidingMenuData;
import com.superchat.model.multiplesg.InvitedDomainNameSet;
import com.superchat.model.multiplesg.JoinedDomainNameSet;
import com.superchat.model.multiplesg.MultipleSGObject;
import com.superchat.model.multiplesg.OwnerDomainName;
import com.superchat.utils.Constants;
import com.superchat.utils.DataEncryption;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.MyriadRegularEditText;
import com.superchat.widgets.MyriadRegularTextView;
import com.superchat.widgets.MyriadSemiboldTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MobileVerificationScreen extends FragmentActivity implements OnClickListener{
	private static final String TAG = "MobileVerificationScreen";
	SharedPrefManager iPrefManager = null;
	String mobileNumber = null;
	String countryCode = null;
	String domainName = null;
	TextView mobileNumberView = null;
	private TextView numEditText01, numEditText02, numEditText03, numEditText04;
	private TextView invalidPinView;
	private TextView notReceivedPinView;
	private TextView callRecieveView;
	private TextView orTextView;
	private TextView resendView;
//	private CheckBox privacyCheckbox;
	private MyriadRegularEditText inputEditText;
	private MyriadRegularTextView editTextView;
	private MyriadRegularTextView timeCounterView;
	private TextWatcher watcherEditText01;
	private KeyListener iKeyLisener = null;
	InputMethodManager imManager = null;
	boolean isSelfVerifing;
	boolean isAutoServerCode;
	boolean isAppDestroy;
	String encryptedMessage;
	DataEncryption dataEncryption = null;
	final String MY_KEY = "The quick brown fox jumps over the lazy dog";
	final short BINARY_DATA_PORT = 6734;
	SmsReceiver smsReceiver;
	boolean isSelfVerify;
	boolean regAsAdmin;
	boolean tempVerify;
	String displayName;
	ArrayList<String> ownerDomainDisplayNameSet = new ArrayList<String>();
	ArrayList<String> invitedDomainDisplayNameSet = new ArrayList<String>();
	ArrayList<String> joinedDomainDisplayNameSet = new ArrayList<String>();

	ArrayList<String> ownerDomainNameSet = new ArrayList<String>();
	ArrayList<String> invitedDomainNameSet = new ArrayList<String>();
	ArrayList<String> joinedDomainNameSet = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mobile_code_confirm);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		Bundle bundle = getIntent().getExtras();
		String reg_type = null;
		if(bundle.get(Constants.REG_TYPE) != null)
			reg_type = bundle.get(Constants.REG_TYPE).toString();
		if(reg_type != null && reg_type.equals("ADMIN"))
			regAsAdmin = true;
		tempVerify = bundle.getBoolean("TEMP_VERIFY");
		iPrefManager = SharedPrefManager.getInstance();
		imManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mobileNumber = getIntent().getExtras().get(Constants.MOBILE_NUMBER_TXT).toString();
		countryCode = getIntent().getExtras().getString(Constants.COUNTRY_CODE_TXT);
		domainName = getIntent().getExtras().getString(Constants.DOMAIN_NAME);
		displayName = getIntent().getExtras().getString(Constants.NAME);
		mobileNumberView = (MyriadRegularTextView) findViewById(R.id.id_mobile_number);
		mobileNumberView.setText("+"+mobileNumber.replace(" ", "-"));
		
//		privacyCheckbox = (CheckBox) findViewById(R.id.privacy_check);
//		privacyCheckbox.setMovementMethod(LinkMovementMethod.getInstance());
		
		invalidPinView = (MyriadRegularTextView) findViewById(R.id.id_invalid_pin);
		isAppDestroy = false;
		notReceivedPinView = (MyriadRegularTextView) findViewById(R.id.id_didnt_receive_pin);
		resendView = (MyriadRegularTextView) findViewById(R.id.id_resend);
		callRecieveView = (MyriadRegularTextView) findViewById(R.id.id_receive_call);
		orTextView = (MyriadRegularTextView) findViewById(R.id.id_or);
		
		numEditText01 = (MyriadSemiboldTextView) findViewById(R.id.m_number_etxt01);
		numEditText02 = (MyriadSemiboldTextView) findViewById(R.id.m_number_etxt02);
		numEditText03 = (MyriadSemiboldTextView) findViewById(R.id.m_number_etxt03);
		numEditText04 = (MyriadSemiboldTextView) findViewById(R.id.m_number_etxt04);
		inputEditText = (MyriadRegularEditText) findViewById(R.id.m_number_field);
		editTextView = (MyriadRegularTextView) findViewById(R.id.id_edit_number);
		timeCounterView = (MyriadRegularTextView) findViewById(R.id.id_timer_clock); 
		editTextView.setOnClickListener(this);
		resendView.setOnClickListener(this);
		callRecieveView.setOnClickListener(this);
//		privacyCheckbox.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//                if(privacyCheckbox.isChecked()) {
//                	((Button) findViewById(R.id.next_btn)).setVisibility(View.VISIBLE);
//                }else
//                	((Button) findViewById(R.id.next_btn)).setVisibility(View.INVISIBLE);
//			}
//		});
		iKeyLisener = inputEditText.getKeyListener();
		inputEditText.setKeyListener(null);
		inputEditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		watcherEditText01 = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String numValue = inputEditText.getText().toString().trim();
				switch (numValue.length()) {
				case 1:
					numEditText01.setText("" + numValue.charAt(0));
					numEditText02.setText("");
					numEditText03.setText("");
					numEditText04.setText("");

					break;
				case 2:
					numEditText01.setText("" + numValue.charAt(0));
					numEditText02.setText("" + numValue.charAt(1));
					numEditText03.setText("");
					numEditText04.setText("");
					break;
				case 3:
					numEditText01.setText("" + numValue.charAt(0));
					numEditText02.setText("" + numValue.charAt(1));
					numEditText03.setText("" + numValue.charAt(2));
					numEditText04.setText("");
					break;
				case 4:
					numEditText01.setText("" + numValue.charAt(0));
					numEditText02.setText("" + numValue.charAt(1));
					numEditText03.setText("" + numValue.charAt(2));
					numEditText04.setText("" + numValue.charAt(3));
					imManager.hideSoftInputFromWindow(inputEditText.getWindowToken(), 0);
					((Button)findViewById(R.id.confirm_button)).setBackgroundResource(R.drawable.round_rect_blue);
					onConfirmClick(findViewById(R.id.confirm_button));
					if(clockTimer != null){
						clockTimer.cancel();
						clockTimer = null;
						clockTimerTask = null;
						timeCounterView.setText("00:00");
					}
					break;

				default:
					((Button)findViewById(R.id.confirm_button)).setBackgroundResource(R.drawable.round_rect_gray);
					numEditText01.setText("");
					numEditText02.setText("");
					numEditText03.setText("");
					numEditText04.setText("");
					break;
				}

			}
			//
		};
		inputEditText.addTextChangedListener(watcherEditText01);

//		// +919910040529
//		mobileNumber = "9910040529";
		// boolean verified = isSelfVerified(countryCode + mobileNumber,"7948");
		// Log.d(TAG, "isSelfVerified: "+verified);
		calander = Calendar.getInstance(TimeZone.getDefault());
//		isSelfVerifing = true;
		isAutoServerCode = true;
		if(!isSelfVerifing)
			generateCodeRequest(iPrefManager.getUserId(), true);
		else{
			callRecieveView.setVisibility(TextView.VISIBLE);
		}
		
	}
	Timer clockTimer;
	TimerTask clockTimerTask;
	private Calendar calander;
	private final int  WAITING_TIME_MINUTE = 0;// 2 min
	private final int  WAITING_TIME_SECOND = 30;
	private long startClockTime;
	private long currentClockTime;
private void timerClockStart(){
	if(clockTimer==null)
		clockTimer = new Timer();
	if(clockTimerTask==null)
		clockTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				
				currentClockTime-=1000;
				myHandler.sendEmptyMessage(1);
//				Log.d(TAG, "Clock in Handler: "+startClockTime+" , "+currentClockTime);
				if((startClockTime-currentClockTime)>=((WAITING_TIME_MINUTE*60*1000)+(WAITING_TIME_SECOND*1000))){
					myHandler.sendEmptyMessage(2);
					cancel();
					clockTimer = null;
					clockTimerTask = null;
					if(inputEditText != null && isForeGround){
						runOnUiThread(new Runnable() {
							public void run() {
								inputEditText.setFocusable(true);
							}
						});
						
						}
					isSelfVerify = false;
					}
					
			}
		};
		calander.setTimeInMillis(System.currentTimeMillis());
		calander.set(Calendar.MINUTE, WAITING_TIME_MINUTE);
        calander.set(Calendar.SECOND , WAITING_TIME_SECOND);
        startClockTime = calander.getTimeInMillis();
        currentClockTime = startClockTime;
        clockTimer.schedule(clockTimerTask, 1000, 1000);
}
private final Handler myHandler = new Handler() {
    public void handleMessage(Message msg) {
    	int type = msg.what;
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        calander.setTimeInMillis(currentClockTime);
		String msgTime = format.format(calander.getTime());
//		Log.d(TAG, "Clock in Handler: "+WAITING_TIME);
        timeCounterView.setText(msgTime);
        if(type == 2){
        	resendView.setVisibility(View.VISIBLE);
        	notReceivedPinView.setVisibility(View.VISIBLE);
        	orTextView.setVisibility(View.VISIBLE);
        	callRecieveView.setVisibility(View.VISIBLE);
        }
    }
};
	public void OnKeyBoardOpen(View view) {
		inputEditText.setKeyListener(iKeyLisener);
		inputEditText.requestFocus();
		imManager.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT);
	}

	
	public void onConfirmClick(View view) {
		String code = inputEditText.getText().toString();
		if(!code.equals("") && code.length()==4){
			notReceivedPinView.setVisibility(TextView.GONE);
			callRecieveView.setVisibility(TextView.GONE);
			resendView.setVisibility(TextView.GONE);
			orTextView.setVisibility(TextView.GONE);
			verifyCode(iPrefManager.getUserId(), inputEditText.getText().toString());
		}	else{
//			showDialog(getString(R.string.pin_not_entered));
		}	
	}

	boolean smsReadAndProcessed;
	public void autoReadMessage(final String mobileNumber, final long time,final boolean isByMe) {
        String recievedCode = "";
        // "+919910040529","7948"

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                Uri uriSms = Uri.parse("content://sms/inbox");
//                Cursor cursor = getContentResolver().query(uriSms,
//                        new String[] { "_id", "address", "date", "body" },
//                        "address=?", new String[] { mobileNumber }, null);
                Cursor cursor =null;
                try{
                		cursor = getContentResolver().query(uriSms,
                        new String[] { "_id", "address", "date", "body" },
                        "date>"+time, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();

                    do {
                        if(isAppDestroy){
                            cancel();
                            return;
                        }
                        String address = cursor.getString(1);
                        String body = cursor.getString(3);
                        long date = Long.parseLong(cursor.getString(2));

                        Log.d(TAG, "mobileNumber date - " + address + " , "+body+" , " +" , "+isForeGround+" , *"+(date > time));
                        
                        if ( !isAppDestroy&& address != null && body != null
                                && ((address.contains(mobileNumber) && isByMe)||(!isByMe )) && date > time) {
                             String codeStr1 = body;
                            if(codeStr1!=null && codeStr1.contains(" "))
                            	codeStr1 = body.substring(0,body.indexOf(" "));
                            final String codeStr = codeStr1;
                            Log.d(TAG, "body - " + body + " , "+!codeStr.equals(Constants.SELF_VARIFICATION_MSG));
                            if(isByMe && codeStr != null && !codeStr.equals(Constants.SELF_VARIFICATION_MSG)){
                                
                            }else if((isByMe ||(!isByMe && body != null && body.contains("is the verification code for SuperChat")))){// && !smsReadAndProcessed){
                              
//                                	if(isByMe){
//                                		verifyCode(iPrefManager.getUserId(), codeStr);
//                                	}else  
	                            runOnUiThread(new Runnable() {
	                                @Override
	                                public void run() {
	                                	inputEditText.setFocusable(true);
	        							isSelfVerify = false;
	                                    inputEditText.setText(codeStr);
	                                    smsReadAndProcessed = true;
	                                    numEditText01.setText(""
	                                            + codeStr.charAt(0));
	                                    numEditText02.setText(""
	                                            + codeStr.charAt(1));
	                                    numEditText03.setText(""
	                                            + codeStr.charAt(2));
	                                    numEditText04.setText(""
	                                            + codeStr.charAt(3));
	                                }
	                            });
                                
                            cancel();
                            break;
                            }
                        }
                        if (!isAppDestroy && (date - time) > ((WAITING_TIME_MINUTE*60*1000) + (WAITING_TIME_SECOND*1000))) {
                            if(isForeGround)
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    notReceivedPinView
                                            .setVisibility(TextView.VISIBLE);
                                    callRecieveView
                                            .setVisibility(TextView.VISIBLE);
                                    resendView.setVisibility(TextView.VISIBLE);
                                    orTextView.setVisibility(TextView.VISIBLE);
                                }
                            });

                            cancel();
                            break;
                        }

                    } while (cursor.moveToNext());
                }}catch(Exception e){
                	 cancel();
                }
                finally{
                	if(cursor!=null)
                		cursor.close();
                	cursor = null;
                }
            }
        }, 4000, 2000);

    }
	
	boolean isForeGround;
	public void onResume(){
		super.onResume();
		isForeGround = true;
	
	}
	public void onPause(){
		super.onPause();
		isForeGround = false;
	
	}
public void onDestroy(){
	super.onDestroy();
	isAppDestroy = true;
}
	public boolean isSelfVerified(String mobileNumber, String code) {
		boolean verifiedFlag = false;
		// "+919910040529","7948"

		Uri uriSms = Uri.parse("content://sms/inbox");
		Cursor cursor = getContentResolver().query(uriSms,
				new String[] { "_id", "address", "date", "body" },
				"address=? and body=?", new String[] { mobileNumber, code },
				null);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();

			do {
				String address = cursor.getString(1);
				String body = cursor.getString(3);

//				System.out.println("mobileNumber - " + address);
//				System.out.println("body - " + body);
				if (address != null && body != null
						&& address.equals(mobileNumber) && body.equals(code)) {
					verifiedFlag = true;
					break;
				}

			} while (cursor.moveToNext());
			cursor.close();
		}
		return verifiedFlag;

	}
	
	private void verifyCode(long id, String digit) {
		String codeVerifyUrl = null;
		Log.d(TAG, digit+ " self code match "+Constants.SELF_VARIFICATION_MSG);
		if(isSelfVerifing && !digit.equals(Constants.SELF_VARIFICATION_MSG)){
//			showDialog("Self verified code are not matched");
			isSelfVerifing = false;
			verifyCode(id,digit);
			return;
		}
		String version = "";
		String imei = SuperChatApplication.getDeviceId();
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
		if(isSelfVerifing){
			codeVerifyUrl = Constants.SERVER_URL + "/tiger/rest/user/domainprofiledata?mobileNumber="+mobileNumber;
//			codeVerifyUrl = Constants.SERVER_URL + "/tiger/rest/user/mobileverification/verify?userId="+ id+"&clientVersion="+clientVersion+"&imei="+imei;
		}
		else 
			codeVerifyUrl = Constants.SERVER_URL+ "/tiger/rest/user/mobileverification/matchcode?userId="+ id + "&code=" + digit+"&clientVersion="+clientVersion+"&imei="+imei;
		final Context context = this;

		AsyncHttpClient client = new AsyncHttpClient();
		 client = SuperChatApplication.addHeaderInfo(client,false);
		client.get(codeVerifyUrl, null, new AsyncHttpResponseHandler() {
			ProgressDialog dialog = null;

			@Override
			public void onStart() {
				if(!isAppDestroy){
					runOnUiThread(new Runnable() {
						public void run() {
							dialog = ProgressDialog.show(MobileVerificationScreen.this, "",
									"Loading. Please wait...", true);
						}
					});
				
				}
				Log.d(TAG, "verifyCode method onStart: ");
			}

			@Override
			public void onSuccess(int arg0, String arg1) {
				System.out.println("verifyCode :: onSuccess : onSuccess Data : " + arg1);
				if(arg1 == null){
					showDialog(getString(R.string.network_not_responding));
					return;
				}
				Gson gson = new GsonBuilder().create();
				final RegMatchCodeModel objUserModel = gson.fromJson(arg1, RegMatchCodeModel.class);
				if (objUserModel.iStatus != null
						&& objUserModel.iStatus.equalsIgnoreCase("success")) {
					if(!tempVerify){
						iPrefManager.saveUserVarified(true);
						iPrefManager.setMobileVerified(iPrefManager.getUserPhone(), true);
						iPrefManager.saveUserName(objUserModel.username);
						iPrefManager.saveUserPassword(objUserModel.password);
					}
					iPrefManager.setOTPVerified(true);
					iPrefManager.saveUserPhone(mobileNumber);
//					if(displayName != null)
//						iPrefManager.saveDisplayName(displayName);
					if(regAsAdmin)
						iPrefManager.setAdminReg(true);
					iPrefManager.setOTPVerifiedTime(System.currentTimeMillis());
					if(arg1 != null && arg1.length() > 0) {
						iPrefManager.setSGListData(arg1);
						//Update in DB also
						MultipleSGObject gsonObject = new Gson().fromJson(arg1, MultipleSGObject.class);

						ArrayList<OwnerDomainName> ownerDomainNameSet = gsonObject.getOwnerDomainNameSet();
						if((ownerDomainNameSet != null && ownerDomainNameSet.size() > 0)) {
							DBWrapper.getInstance().updateOwnedSGDataArray(ownerDomainNameSet);
						}
						ArrayList<JoinedDomainNameSet> joinedSG = new ArrayList<>();
						joinedSG = gsonObject.getJoinedDomainNameSet();
						if(joinedSG != null && joinedSG.size() > 0)
							DBWrapper.getInstance().updateJoinedSGData(joinedSG);
						ArrayList<InvitedDomainNameSet> invitedSG = new ArrayList<>();
						invitedSG = gsonObject.getInvitedDomainNameSet();
						if(invitedSG != null && invitedSG.size() > 0)
							DBWrapper.getInstance().updateInvitedSGData(invitedSG);
					}
					if(!isAppDestroy){
						Bundle bundle = new Bundle();
						JSONObject json;
						try {
							json = new JSONObject(arg1);
							//Get owner List
							/*if(json != null && json.has("ownerDomainName")){
								ownerDomainNameSet.add(json.getString("ownerDomainName"));
							}*/
							//invitedDomainNameSet
							JSONArray array = json.getJSONArray("ownerDomainNameSet");
							ownerDomainNameSet = new ArrayList<String>();
							for(int i = 0; i < array.length(); i++){
								ownerDomainNameSet.add(array.getString(i));
							}
							//invitedDomainNameSet
							array = json.getJSONArray("invitedDomainNameSet");
							invitedDomainNameSet = new ArrayList<String>();
							for(int i = 0; i < array.length(); i++){
								invitedDomainNameSet.add(array.getString(i));
							}
							//joinedDomainNameSet
							array = json.getJSONArray("joinedDomainNameSet");
							joinedDomainNameSet = new ArrayList<String>();
							for(int i = 0; i < array.length(); i++){
								joinedDomainNameSet.add(array.getString(i));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(regAsAdmin)
						{
							/*//Check if he has already owned a Supergroup then prompt him
							boolean isMultipleSuperGroupSupported = true;
							if(ownerDomainNameSet != null && ownerDomainNameSet.size() > 0 && (!isMultipleSuperGroupSupported)){
								regAsAdmin = false;
								Intent intent = new Intent(MobileVerificationScreen.this, SupergroupListingScreen.class);
								bundle.putString(Constants.MOBILE_NUMBER_TXT, mobileNumber);
								if(ownerDomainNameSet != null && ownerDomainNameSet.size() > 0)
									bundle.putStringArrayList("OWNERDOMAINNAMESET", ownerDomainNameSet);
								if(invitedDomainNameSet != null && invitedDomainNameSet.size() > 0)
									bundle.putStringArrayList("INVITEDDOMAINSET", invitedDomainNameSet);
								if(joinedDomainNameSet != null && joinedDomainNameSet.size() > 0)
									bundle.putStringArrayList("JOINEDDOMAINSET", joinedDomainNameSet);
								bundle.putBoolean("SHOW_OWNED_ALERT", true);
								intent.putExtras(bundle);
								startActivity(intent);
								finish();
							}else{*/
								 Intent intent = new Intent(MobileVerificationScreen.this, ProfileScreen.class);
								 bundle.putBoolean(Constants.REG_TYPE, true);
								 bundle.putBoolean("PROFILE_EDIT_REG_FLOW", true);
								 //SPECIAL DATA FOR BACK
								 bundle.putBoolean("PROFILE_FIRST", true);
								 if(mobileNumber.indexOf('-') != -1)
									 intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber.substring(mobileNumber.indexOf('-') + 1));
								 else
									 intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
								 intent.putExtra(Constants.REG_TYPE, "ADMIN");
								 intent.putExtra("REGISTER_SG", true);
								 intent.putExtras(bundle);
								 startActivity(intent);
								 finish();
							//}
						}else{
							Intent intent = new Intent(MobileVerificationScreen.this, SupergroupListingScreen.class);
							bundle.putString(Constants.MOBILE_NUMBER_TXT, mobileNumber);
							if(ownerDomainNameSet != null && ownerDomainNameSet.size() > 0)
								bundle.putStringArrayList("OWNERDOMAINNAMESET", ownerDomainNameSet);
							if(invitedDomainNameSet != null && invitedDomainNameSet.size() > 0)
								bundle.putStringArrayList("INVITEDDOMAINSET", invitedDomainNameSet);
							if(joinedDomainNameSet != null && joinedDomainNameSet.size() > 0)
								bundle.putStringArrayList("JOINEDDOMAINSET", joinedDomainNameSet);
							intent.putExtras(bundle);
							startActivity(intent);
							finish();
						}
					}
				} 
				else if (objUserModel.iStatus != null
						&& objUserModel.iStatus.equalsIgnoreCase("error") && !isAppDestroy) {
					notReceivedPinView.setVisibility(TextView.VISIBLE);
					callRecieveView.setVisibility(TextView.VISIBLE);
					resendView.setVisibility(TextView.VISIBLE);
					orTextView.setVisibility(TextView.VISIBLE);
//					invalidPinView.setVisibility(TextView.VISIBLE);
					inputEditText.setText("");
					numEditText01.setText("");
					numEditText02.setText("");
					numEditText03.setText("");
					numEditText04.setText("");
					
					//Show error here
					ErrorModel errorModel = gson.fromJson(arg1,ErrorModel.class);
					if (errorModel != null) {
						if (errorModel.citrusErrors != null
								&& !errorModel.citrusErrors.isEmpty()) {
							ErrorModel.CitrusError kdiError = errorModel.citrusErrors.get(0);
							showDialog(kdiError.message);
						} else if (errorModel.message != null)
							showDialog(errorModel.message);
					} else
						showDialog(objUserModel.iMessage);
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					return;
				} 
				else if(!isAppDestroy){
					runOnUiThread(new Runnable() {
						public void run() {
							notReceivedPinView.setVisibility(TextView.VISIBLE);
							callRecieveView.setVisibility(TextView.VISIBLE);
							resendView.setVisibility(TextView.VISIBLE);
							orTextView.setVisibility(TextView.VISIBLE);
//							invalidPinView.setVisibility(TextView.VISIBLE);
							inputEditText.setText("");
							numEditText01.setText("");
							numEditText02.setText("");
							numEditText03.setText("");
							numEditText04.setText("");
							showDialog(objUserModel.iMessage);
						}
					});

				}
				try{
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				try{
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
			}catch(Exception e){}
			}
		});
				}catch(Exception e){}
		super.onSuccess(arg0, arg1);
	}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				Log.i(TAG, "onFailure Data : " + arg1);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if (dialog != null) {
							dialog.dismiss();
							dialog = null;
						}
					}
				});
				inputEditText.setText("");
				numEditText01.setText("");
				numEditText02.setText("");
				numEditText03.setText("");
				numEditText04.setText("");
				showDialog(getString(R.string.network_not_responding));
				super.onFailure(arg0, arg1);
			}
		});
	}

	private void generateCodeRequest(long id, final boolean isShowLoading) {
		if(clockTimer != null)
			clockTimer.cancel();
		clockTimer = null;
		clockTimerTask = null;
		timerClockStart();
		AsyncHttpClient client = new AsyncHttpClient();
		 client = SuperChatApplication.addHeaderInfo(client,false);
		 String url = Constants.SERVER_URL
					+ "/tiger/rest/user/mobileverification/generatecode?userId="
					+ id;
		 Log.i(TAG, "generateCodeRequest : url : "+url);
		client.get(url, null, new AsyncHttpResponseHandler() {
			ProgressDialog dialog = null;

			@Override
			public void onStart() {
				if (isShowLoading) {
					dialog = ProgressDialog.show(MobileVerificationScreen.this,
							"", "Loading. Please wait...", true);
					Log.i(TAG, "genrateCodeRequest method onStart: ");

				}
			}

			@Override
			public void onSuccess(int arg0, String arg1) {
				Log.i(TAG, "genrateCodeRequest method onSuccess: " + arg1);

				Gson gson = new GsonBuilder().create();
				RegGenrateCodeModel objUserModel = gson.fromJson(arg1, RegGenrateCodeModel.class);

				Log.d(TAG, "genrateCodeRequest method iStatus: "+ objUserModel.iStatus);
				Log.d(TAG, "genrateCodeRequest method iMessage: "+ objUserModel.iMessage);
			
				// if(isShowLoading){
				// showDialog(objUserModel.iMessage);
				// }
				if (dialog != null && isShowLoading) {
					dialog.dismiss();
					dialog = null;
				}
				if(objUserModel.iStatus!=null && objUserModel.iStatus.equals("success")){
					autoReadMessage("BW-SMSTXT", System.currentTimeMillis(), false);// countryCode
					// +
					// mobileNumber
				}else{
					notReceivedPinView.setVisibility(TextView.VISIBLE);
					callRecieveView.setVisibility(TextView.VISIBLE);
					resendView.setVisibility(TextView.VISIBLE);
					orTextView.setVisibility(TextView.VISIBLE);
					if(clockTimer!=null)
						clockTimer.cancel();
						clockTimer = null;
					clockTimerTask = null;
					if(!isAutoServerCode) {
						timeCounterView.setText("00:00");
						if (arg1 == null || arg1.contains("error")) {
							ErrorModel errorModel = gson.fromJson(arg1,ErrorModel.class);
							if (errorModel != null) {
								if (errorModel.citrusErrors != null
										&& !errorModel.citrusErrors.isEmpty()) {
									ErrorModel.CitrusError kdiError = errorModel.citrusErrors.get(0);
									showDialog(kdiError.message);
								} else if (errorModel.message != null)
									showDialog(errorModel.message);
							} else
								showDialog("Error generating code on SMS.");
							if (dialog != null) {
								dialog.dismiss();
								dialog = null;
							}
							return;
						}						
					}else if(objUserModel.iStatus != null && objUserModel.iStatus.equals("error")){
						if(objUserModel.iMessage != null)
							showDialog(objUserModel.iMessage);
						else
							showDialog("Error generating code on SMS.");
					}else{
							isAutoServerCode = false;
							timeCounterView.setText("00:00");
//							timerClockStart();
					}
					
				}
				super.onSuccess(arg0, arg1);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {

				Log.i(TAG, "genrateCodeRequest method onFailure: " + arg1);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						notReceivedPinView.setVisibility(TextView.VISIBLE);
						callRecieveView.setVisibility(TextView.VISIBLE);
						resendView.setVisibility(TextView.VISIBLE);
						orTextView.setVisibility(TextView.VISIBLE);
					}
				});
				if (dialog != null && isShowLoading) {
					dialog.dismiss();
					dialog = null;
				}
				if (isShowLoading) {
					showDialog(getString(R.string.lbl_server_not_responding));
				}
				super.onFailure(arg0, arg1);
			}
		});
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
				invalidPinView.setVisibility(TextView.GONE);
				bteldialog.cancel();
				return false;
			}
		});
		bteldialog.show();
	}
	
	@Override
	public void onBackPressed(){
		clockTimer = null;
		clockTimerTask = null;
		Intent intent = new Intent(this, MainActivity.class);
		if(mobileNumber != null && mobileNumber.trim().length() > 0)
			mobileNumber = mobileNumber.substring(mobileNumber.indexOf('-') + 1);
		intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
		intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCode);
		intent.putExtra(Constants.DOMAIN_NAME, domainName);
		if(regAsAdmin)
			intent.putExtra(Constants.REG_TYPE, "ADMIN");
		else
			intent.putExtra(Constants.REG_TYPE, "USER");
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_edit_number:
			clockTimer = null;
			clockTimerTask = null;
			Intent intent = new Intent(this, MainActivity.class);
			if(mobileNumber != null && mobileNumber.trim().length() > 0)
				mobileNumber = mobileNumber.substring(mobileNumber.indexOf('-') + 1);
			intent.putExtra(Constants.MOBILE_NUMBER_TXT, mobileNumber);
			intent.putExtra(Constants.COUNTRY_CODE_TXT, countryCode);
			intent.putExtra(Constants.DOMAIN_NAME, domainName);
			if(tempVerify)
				intent.putExtra("TEMP_VERIFY", true);
			if(regAsAdmin)
				intent.putExtra(Constants.REG_TYPE, "ADMIN");
			else
				intent.putExtra(Constants.REG_TYPE, "USER");
			startActivity(intent);
			finish();
			break;
		case R.id.id_resend:
			resendView.setVisibility(View.GONE);
        	notReceivedPinView.setVisibility(View.GONE);
        	orTextView.setVisibility(View.GONE);
        	callRecieveView.setVisibility(View.GONE);
        	isSelfVerifing = true;
        	smsReadAndProcessed = false;
        	Constants.SELF_VARIFICATION_MSG = "93048305";
        	inputEditText.setFocusable(true);
        	generateCodeRequest(iPrefManager.getUserId(), true);
		break;		
		case R.id.id_receive_call: // send sms for self verification
			try{
                
				isSelfVerify = true;
				resendView.setVisibility(View.GONE);
	        	notReceivedPinView.setVisibility(View.GONE);
	        	orTextView.setVisibility(View.GONE);
	        	callRecieveView.setVisibility(View.GONE);
				SmsManager smsManager = SmsManager.getDefault();
				String mobileMatched = mobileNumber;
				if(mobileNumber.contains("-"))
					mobileMatched = mobileNumber.substring(mobileNumber.indexOf('-')+1);
				Constants.SELF_VARIFICATION_NUM = "+"+mobileNumber.replace("-", "");
				Random random = new Random();
				int code = random.nextInt(9999);
				if(code < 1000)
					code = code + 1000;
				Constants.SELF_VARIFICATION_MSG = ""+code;
				Log.d(TAG, "On this number "+Constants.SELF_VARIFICATION_NUM+ " self generated code is "+Constants.SELF_VARIFICATION_MSG);
				if(Constants.SELF_VARIFICATION_NUM  != null && !Constants.SELF_VARIFICATION_NUM.equals("")){// && Constants.SELF_VARIFICATION_NUM.length() >=10){
					
					smsManager.sendTextMessage(Constants.SELF_VARIFICATION_NUM, null, Constants.SELF_VARIFICATION_MSG, null, null);
					
					// Get the default instance of SmsManager
					// Send a text based SMS
					// Register a broadcast receiver

					if(Constants.SELF_VARIFICATION_MSG != null){
//						if(Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
//							if(!SMSWriteService.isWriteEnabled(getApplicationContext())) {
//								SMSWriteService.setWriteEnabled(getApplicationContext(), true);
//							}
//							smsManager.sendTextMessage(Constants.SELF_VARIFICATION_NUM, null, Constants.SELF_VARIFICATION_MSG, null, null);
//						}
//						else{
//							smsManager.sendDataMessage(Constants.SELF_VARIFICATION_NUM, null, BINARY_DATA_PORT, Constants.SELF_VARIFICATION_MSG.getBytes(), null, null);
//							registerSMSReceiver();
//						}
						autoReadMessage(mobileMatched, System.currentTimeMillis(),true);
						smsReadAndProcessed = false;
						isSelfVerifing = true;
						clockTimer = null;
						clockTimerTask = null;
						timerClockStart();
						showDialog(getString(R.string.verification_code_sent)+" "+Constants.SELF_VARIFICATION_NUM);
						//Disable text field to enter the number
						inputEditText.setFocusable(false);
						isSelfVerify = true;
						
					}
					else
						showDialog("Generated code is null");
				}else{
					
					resendView.setVisibility(View.VISIBLE);
		        	notReceivedPinView.setVisibility(View.VISIBLE);
		        	orTextView.setVisibility(View.VISIBLE);
		        	callRecieveView.setVisibility(View.VISIBLE);
		        	showDialog(getString(R.string.self_verify_phone_number_msg));
				}
				
			}catch(Exception ex){
//				showDialog(getString(R.string.signup_mobile_help));
				ex.printStackTrace();
				resendView.setVisibility(View.VISIBLE);
	        	notReceivedPinView.setVisibility(View.VISIBLE);
	        	orTextView.setVisibility(View.VISIBLE);
	        	callRecieveView.setVisibility(View.VISIBLE);
				showDialog(getString(R.string.self_verify_phone_number_msg));
			}
			break;
		}
	}
//--------------------------------------- SMS Broadcast Receiver  -----------------------------------------------------------------
	private void registerSMSReceiver(){
		// Register a broadcast receiver
		smsReceiver = new SmsReceiver();
		IntentFilter intentFilter = new IntentFilter("android.intent.action.DATA_SMS_RECEIVED");
		intentFilter.setPriority(10);
		intentFilter.addDataScheme("sms");
		intentFilter.addDataAuthority("*", "6734");
		registerReceiver(smsReceiver, intentFilter);
	}
	private void unRegisterSMSReceiver(){
		// Register a broadcast receiver
		if(smsReceiver != null){
			unregisterReceiver(smsReceiver);
			smsReceiver = null;
		}
	}
	class SmsReceiver extends BroadcastReceiver {
	    private String TAGS = SmsReceiver.class.getSimpleName();
	 
	    public SmsReceiver() {
	    }
	 
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        try {
				// Get the data (SMS data) bound to intent
				Bundle bundle = intent.getExtras();
				unRegisterSMSReceiver();
 
				SmsMessage[] msgs = null;
 
				String str = "";
 
				if (bundle != null){
				    // Retrieve the Binary SMS data
				    Object[] pdus = (Object[]) bundle.get("pdus");
				    msgs = new SmsMessage[pdus.length];
 
				    // For every SMS message received (although multipart is not supported with binary)
				    for (int i=0; i<msgs.length; i++) {
				        byte[] data = null;
 
				        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
 
				        str += "Binary SMS from " + msgs[i].getOriginatingAddress() + " :";
//	 
				        str += "\nBINARY MESSAGE: ";
 
				        // Return the User Data section minus the
				        // User Data Header (UDH) (if there is any UDH at all)
				        data = msgs[i].getUserData();
 
				        // Generally you can do away with this for loop
				        // You'll just need the next for loop
				        for (int index=0; index < data.length; index++) {
				            str += Byte.toString(data[index]);
				        }
 
				        str += "\nTEXT MESSAGE (FROM BINARY):";
 
				        for (int index=0; index < data.length; index++) {
				            str += Character.toString((char) data[index]);
				        }
 
//	                str += "\n";
				    }
				    // Dump the entire message
//	             Toast.makeText(context, str, Toast.LENGTH_LONG).show();
				    Log.d(TAGS, "Message Received : "+str);
				    
				    //Got the SMS value -
				    if(str != null && str.contains("(FROM BINARY):")){
				    	str = str.substring(str.lastIndexOf(':') + 1);
				    final String codeStr = str;
				    if(isForeGround && Constants.SELF_VARIFICATION_MSG.equalsIgnoreCase(codeStr) && !smsReadAndProcessed){
				        runOnUiThread(new Runnable() {
				            @Override
				            public void run() {
				                inputEditText.setText(codeStr);
				                smsReadAndProcessed = true;
				                numEditText01.setText(""+ codeStr.charAt(0));
				                numEditText02.setText(""+ codeStr.charAt(1));
				                numEditText03.setText("" + codeStr.charAt(2));
				                numEditText04.setText(""+ codeStr.charAt(3));
				            }
				        });
				    }
				}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
