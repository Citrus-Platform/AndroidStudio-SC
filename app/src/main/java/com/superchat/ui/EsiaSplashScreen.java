package com.superchat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.superchat.R;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import static com.superchat.utils.Constants.PROPERTY_APP_VERSION;
import static com.superchat.utils.Constants.PROPERTY_REG_ID;

public class
EsiaSplashScreen extends Activity {
	Timer timer;
	SharedPrefManager iPrefManager;
	public static final String EXTRA_MESSAGE = "message";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Handler handler = new Handler();
    GoogleCloudMessaging gcm;
    static final String TAG = EsiaSplashScreen.class.getSimpleName();
    AtomicInteger msgId = new AtomicInteger();
    Context context;
//    String regid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_esia);

        UtilSetFont.setFontMainScreen(this);

        context = this;
		iPrefManager = SharedPrefManager.getInstance();
		String mobileNumber = iPrefManager.getUserPhone();
        if(iPrefManager.getBackupSchedule() == -1) {
            iPrefManager.setBackupSchedule(2);
            SharedPrefManager.getInstance().setLastBackUpTime(System.currentTimeMillis());
        }
		if(iPrefManager.isOTPVerified() && !iPrefManager.isAdminReg() && iPrefManager.getSgListData() != null){
			Bundle bundle = new Bundle();
			String data = iPrefManager.getSgListData();
			JSONObject json;
			String number = iPrefManager.getUserPhone();
//			 if(number.indexOf('-') != -1)
//				 number =  number.substring(number.indexOf('-') + 1);
			ArrayList<String> ownerDomainNameSet = new ArrayList<String>();
			ArrayList<String> invitedDomainNameSet = new ArrayList<String>();
			ArrayList<String> joinedDomainNameSet = new ArrayList<String>();
			try {
				json = new JSONObject(data);
				//Get owner List
				if(json != null && json.has("ownerDomainName")){
					ownerDomainNameSet.add(json.getString("ownerDomainName"));
				}
				//invitedDomainNameSet
				JSONArray array = json.getJSONArray("invitedDomainNameSet");
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

            //SupergroupListingScreenNew.start(EsiaSplashScreen.this, number, invitedDomainNameSet, false);
            Intent intent = new Intent(EsiaSplashScreen.this, SupergroupListingScreen.class);
			bundle.putString(Constants.MOBILE_NUMBER_TXT, number);
			if(ownerDomainNameSet != null && ownerDomainNameSet.size() > 0)
				bundle.putStringArrayList("OWNERDOMAINNAMESET", ownerDomainNameSet);
			if(invitedDomainNameSet != null && invitedDomainNameSet.size() > 0)
				bundle.putStringArrayList("INVITEDDOMAINSET", invitedDomainNameSet);
			if(joinedDomainNameSet != null && joinedDomainNameSet.size() > 0)
				bundle.putStringArrayList("JOINEDDOMAINSET", joinedDomainNameSet);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		}else if (isVerifiedUser(mobileNumber)){
			if (!isProfileExist(iPrefManager.getUserName())) {
				Intent intent = new Intent(EsiaSplashScreen.this, ProfileScreen.class);
				Bundle bundle = new Bundle();
				bundle.putString(Constants.CHAT_USER_NAME, iPrefManager.getUserName());
				bundle.putString(Constants.CHAT_NAME, "");
				bundle.putBoolean("PROFILE_EDIT_REG_FLOW", true);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}else{
				Intent intent = new Intent(EsiaSplashScreen.this, HomeScreen.class);
				startActivity(intent);
				finish();
			}
		}
		if (iPrefManager.isContactModified()) {
//			iPrefManager.setContactModified(false);
//			SuperChatApplication.contactSyncState = SuperChatApplication.CONTACT_SYNC_WAIT;
//			SuperChatApplication.copyContactsAsync();
		}else{
//			EsiaChatApplication.contactSyncState = EsiaChatApplication.CONTACT_SYNC_IDLE;
//			EsiaChatApplication.syncContactsWithServer(null);
		}
//		Tracker t = ((SuperChatApplication) getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Splash Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());
		
		if (checkPlayServices())
        {
            gcm = GoogleCloudMessaging.getInstance(this);
            Constants.regid = getRegistrationId(context);
//            Utilities.sRegId = regid;
            Log.i(TAG, "onCreate :: getRegistrationId saved ===> " + Constants.regid);
//            testPushMessage("");

            if (Constants.regid.isEmpty()){
                registerInBackgroundLocal();
            }
            registerInBackgroundLocal();
        }
        else
        {
            Log.i(TAG, "onCreate :: No valid Google Play Services APK found.");
        }
//		Mobiruck mobiruck = new Mobiruck();
//		mobiruck.triggerConversion(this);
	}
	//============================================
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "checkPlayServices :: This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackgroundLocal()
    {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    if(Constants.isBuildLive)
                    	Constants.regid = gcm.register(Constants.GOOGLE_PROJECT_ID_PROD);
                    else
                    	Constants.regid = gcm.register(Constants.GOOGLE_PROJECT_ID_DEV);
                    msg = "Device registered, registration ID=" + Constants.regid;
                    Log.i(TAG, "registerInBackgroundLocal :: regid----> "+Constants.regid);

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // Persist the regID - no need to register again.
                storeRegistrationId(context, Constants.regid);
            }
        }.execute(null, null, null);
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(EsiaSplashScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "getRegistrationId :: Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "getRegistrationId :: App version changed.");
            return "";
        }
        return registrationId;
    }
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "storeRegistrationId :: Saving regId ==> "+regId+", on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
//        Utilities.sRegId = regId;//BusinessProxy.sSelf.getPushRegId(DBEngine.PUSH_TABLE);
//        testPushMessage("");
    }

	public void onResume() {
		super.onResume();
		String mobileNumber = iPrefManager.getUserPhone();
		if (!isVerifiedUser(mobileNumber)){			
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
	//				String tmpNumber = "9910040529";
	//				int id = 5;
	//				String pass = "MVTGrLj9";
					
	//				String tmpNumber = "8103002248";
	//				int id = 7;
	//				String pass = "j6d9FkLa";
	//				
	//				iPrefManager.saveUserPhone("91-"+tmpNumber);
	//				iPrefManager.saveUserId(id);
	//				iPrefManager.setMobileRegistered("91-"+tmpNumber, true);
	//				iPrefManager.setMobileVerified("91-"+tmpNumber, true);
	//				iPrefManager.setProfileAdded("91-"+tmpNumber, true);
	//				iPrefManager.saveUserPassword(pass); //j6d9FkLa : 91-8103002248 : 7  //MVTGrLj9 : 91-9910040529 : 5   //Bzz4g76x  : 91-9599822779
	//				iPrefManager.saveSipServerAddress("m91"+tmpNumber+"@52.74.197.243:5060");
	//				iPrefManager.saveUserName("m91"+tmpNumber);
					
					String mobileNumber = iPrefManager.getUserPhone();
					String userPass = iPrefManager.getUserPassword();
					Log.d("SUPER_SPLASH", "userPass: "+userPass);
	//				if(mobileNumber!=null)
	//					iPrefManager.setProfileAdded(userName, true);
					
					if (isExistingUser(mobileNumber)) {
						Intent intent = null;
						if (isVerifiedUser(mobileNumber)) {
							if (!isProfileExist(iPrefManager.getUserName())) {
								intent = new Intent(EsiaSplashScreen.this, ProfileScreen.class);
								Bundle bundle = new Bundle();
								bundle.putString(Constants.CHAT_USER_NAME, iPrefManager.getUserName());
								bundle.putString(Constants.CHAT_NAME, "");
								bundle.putBoolean("PROFILE_EDIT_REG_FLOW", true);
								intent.putExtras(bundle);
								startActivity(intent);
								finish();
							} else
								intent = new Intent(EsiaSplashScreen.this, HomeScreen.class);
						} else {
							 intent = new Intent(EsiaSplashScreen.this, MobileVerificationScreen.class);
							 intent.putExtra(Constants.MOBILE_NUMBER_TXT,mobileNumber);
							 intent.putExtra(Constants.COUNTRY_CODE_TXT,Constants.countryCode);
						}
						startActivity(intent);
						finish();
						
					}else{
//						startActivity(new Intent(EsiaSplashScreen.this, TermAndConditionScreen.class));
						startActivity(new Intent(EsiaSplashScreen.this, RegistrationOptions.class));
//						startActivity(new Intent(EsiaSplashScreen.this, TourActivity.class));
						finish();
					}
				}
			}, 2000);
		}
	}
	private boolean isExistingUser(String mobileNumber) {
		if (mobileNumber == null)
			return false;
		boolean isRegisterd = iPrefManager.isMobileRegistered(mobileNumber);
		return isRegisterd;
	}

	private boolean isVerifiedUser(String mobileNumber) {
        System.out.println("<< mobileNumber :: "+mobileNumber);
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
	public void onPause() {
		super.onPause();
		timer.cancel();
		timer = null;
	}
//======================================================================================
	public static void testPushMessage(String regID)
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                String apiKey = Constants.GOOGLE_PUSH_API_KEY_DEV;
                if(Constants.isBuildLive)
                    apiKey = Constants.GOOGLE_PUSH_API_KEY_LIVE;
                try{
                    // 1. URL
                    URL url = new URL("https://android.googleapis.com/gcm/send");
                    // 2. Open connection
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 3. Specify POST method
                    conn.setRequestMethod("POST");
                    // 4. Set the headers
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Authorization", "key="+apiKey);
                    conn.setDoOutput(true);
                    // 5. Add JSON data into POST request body
                    //`5.1 Use Jackson object mapper to convert Contnet object into JSON
//            ObjectMapper mapper = new ObjectMapper();
                    // 5.2 Get connection output stream
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    String message = "Testing Push Message 😋";
                    byte[] data = message.getBytes("utf-8");
                    String encodedMessage = new String(data, "utf-8");
                    String jsonMessage = "{ \"data\": {\"title\": \"My Push\",\"message\": \"" + encodedMessage + "\"},\"registration_ids\": " +
                            "[\"APA91bF8zrIFJoyRt18YMxSoJU_DCg1d8zpjVqoUs_RwSKM8Ek9dKqfl1iDqk3M8UZyMCWKoBaWyww2r8Y67Gce25rsNVC4LsIPQYm9Muez3-XepTqEJy7I\"]}";
                    //APA91bH0EPv2bqfQxrnvdv6SPfFqPtQATZBlhq_hieYkksUCI6s6kqFRZ5P1b65fd31KmzREUqbSC2kQrwTu9WqlzemG7m6qO_ADm5-SmJFpg5fh8GXty7wTU-Q2h5KvyjumjX4hCyQAsOXT8l0VHhb_-DlC5L1OILbFa4qUj5dpupkoBQ86Sco
                    // 5.3 Copy Content "JSON" into
//            mapper.writeValue(wr, content);
//            // 5.4 Send the request
//            wr.flush();
//            // 5.5 close
//            wr.close();
                    wr.write(jsonMessage.getBytes());
                    wr.flush();
                    wr.close();
                    // 6. Get the response
                    int responseCode = conn.getResponseCode();
                    Log.i(TAG, "post :: \nSending 'POST' request to URL : " + url);
                    Log.i(TAG, "post :: \nData Sent : " + jsonMessage);
                    Log.i(TAG, "post :: Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    // 7. Print result
//            System.out.println(response.toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
