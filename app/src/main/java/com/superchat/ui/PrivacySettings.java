package com.superchat.ui;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.ErrorModel;
import com.superchat.model.PrivacyStatusModel;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Set;

import static android.R.attr.mode;
import static com.superchat.R.id.switchBroadcast;

public class PrivacySettings extends Activity implements OnClickListener {

    public static void start(Context context) {
        Intent starter = new Intent(context, PrivacySettings.class);
        context.startActivity(starter);
    }
    public static final String TAG = "PrivacyChatSettings";
    SharedPrefManager pref;

    TextView backView;
    TextView tvContactCount;
    LinearLayout llBlockContacts;
    LinearLayout llSwitchChatSettings;
    LinearLayout llSwitchCallSettings;

    Switch switchCallSetting;
    Switch switchChatSettings;

    private byte CALL_PREV_PRIVACY_TYPE = 0;
    private byte CALL_PRIVACY_TYPE = 0;
    private final byte ALL_CALLS_ALLOWED = 0;
    private final byte NO_CALLS_ALLOWED = 1;

    private byte CHAT_PREV_PRIVACY_TYPE = 0;
    private byte CHAT_PRIVACY_TYPE = 0;
    private final byte ALL_MESSAGES_ALLOWED = 0;
    private final byte NO_MESSAGES_ALLOWED = 1;

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
        setContentView(R.layout.privacy_settings);

        UtilSetFont.setFontMainScreen(this);

        pref = SharedPrefManager.getInstance();

        tvContactCount = (TextView) findViewById(R.id.tvContactCount);
        llBlockContacts = (LinearLayout) findViewById(R.id.llBlockContacts);
        llSwitchChatSettings = (LinearLayout) findViewById(R.id.llSwitchChatSettings);
        llSwitchCallSettings = (LinearLayout) findViewById(R.id.llSwitchCallSettings);

        switchCallSetting = (Switch) findViewById(R.id.switchCallSetting);
        switchChatSettings = (Switch) findViewById(R.id.switchChatSettings);

        llSwitchChatSettings.setOnClickListener(this);
        llSwitchCallSettings.setOnClickListener(this);

        backView = (TextView)findViewById(R.id.id_back);
        backView.setOnClickListener(this);

        if (pref.isDNM(pref.getUserName())) {
            CHAT_PRIVACY_TYPE = NO_MESSAGES_ALLOWED;
            switchChatSettings.setChecked(true);
        }
        CHAT_PREV_PRIVACY_TYPE = CHAT_PRIVACY_TYPE;

        switchChatSettings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CHAT_PRIVACY_TYPE = NO_MESSAGES_ALLOWED;
                } else {
                    CHAT_PRIVACY_TYPE = ALL_MESSAGES_ALLOWED;
                }
            }
        });

        if(pref.isDNC(pref.getUserName())) {
            CALL_PRIVACY_TYPE = NO_CALLS_ALLOWED;
            switchCallSetting.setChecked(true);
        }
        CALL_PREV_PRIVACY_TYPE = CALL_PRIVACY_TYPE;

        switchCallSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    CALL_PRIVACY_TYPE = NO_CALLS_ALLOWED;
                } else {
                    CALL_PRIVACY_TYPE = ALL_CALLS_ALLOWED;
                }
            }
        });

        Set<String> blockSet = pref.getBlockList();
        int blockSetSize = (blockSet != null ? blockSet.size() : 0);
        String size = (blockSetSize > 0 ? blockSetSize + " Contacts" : "No Contact");
        tvContactCount.setText(size);
        if(blockSetSize > 0){
            llBlockContacts.setOnClickListener(this);
        }
    }

    public void onResume() {
        super.onResume();
        bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);
    }

    public void onPause() {
        super.onPause();
        try {
            unbindService(mMessageConnection);
        } catch (Exception e) {
            // Just ignore that
            Log.d("PrivacyChatSettings", "Unable to un bind");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llBlockContacts:{
                BlockListScreen.start(this);
                break;
            }
            case R.id.llSwitchChatSettings:{
                switchChatSettings.toggle();
                break;
            }
            case R.id.llSwitchCallSettings:{
                switchCallSetting.toggle();
                break;
            }
            case R.id.id_back:
                updateServerInfo();
                break;
        }
    }

    private void updateServerInfo() {

        if ((CHAT_PREV_PRIVACY_TYPE != CHAT_PRIVACY_TYPE) || (CALL_PREV_PRIVACY_TYPE != CALL_PRIVACY_TYPE)) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
            if (isConnected) {
                if (Build.VERSION.SDK_INT >= 11)
                    new PrivacyChatServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    new PrivacyChatServerTask().execute();
            } else {
                Toast.makeText(this, getString(R.string.error_network_connection), Toast.LENGTH_SHORT).show();
                finish();
            }
        } else
            finish();

    }

    public void onBackPressed() {
//	super.onBackPressed();
        updateServerInfo();
    }

    private class PrivacyChatServerTask extends AsyncTask<String, String, String> {
        PrivacyStatusModel privacyStatusModel;

        ProgressDialog progressDialog = null;

        public PrivacyChatServerTask() {
            privacyStatusModel = new PrivacyStatusModel();
            PrivacyStatusModel.PrivacyTypes privacyTypes = new PrivacyStatusModel.PrivacyTypes();
            privacyTypes.dnm = CHAT_PRIVACY_TYPE;
            privacyTypes.dnc = CALL_PRIVACY_TYPE;
            privacyStatusModel.setPrivacyTypes(privacyTypes);
        }

        @Override
        protected void onPreExecute() {
//		progressDialog = ProgressDialog.show(PrivacyChatSettings.this, "", "Please wait...", true);
            progressDialog = new ProgressDialog(PrivacySettings.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String JSONstring = new Gson().toJson(privacyStatusModel);
            DefaultHttpClient client1 = new DefaultHttpClient();
            Log.d(TAG, "PrivacyChatServerTask request:" + JSONstring);
            HttpPost httpPost = new HttpPost(Constants.SERVER_URL + "/tiger/rest/user/profile/update");
            httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
            HttpResponse response = null;
            try {
                httpPost.setEntity(new StringEntity(JSONstring));
                try {
                    response = client1.execute(httpPost);
                    final int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                        String line = "";
                        String str = "";
                        while ((line = rd.readLine()) != null) {

                            str += line;
                        }
                        Log.d(TAG, "PrivacyChatServerTask Result : " + str);
                        if (str != null && !str.equals("")) {
                            str = str.trim();
                            return str;
                        }
                    }
                } catch (ClientProtocolException e) {
                    Log.d(TAG, "PrivacyChatServerTask during HttpPost execution ClientProtocolException:" + e.toString());
                } catch (IOException e) {
                    Log.d(TAG, "PrivacyChatServerTask during HttpPost execution ClientProtocolException:" + e.toString());
                }

            } catch (UnsupportedEncodingException e1) {
                Log.d(TAG, "PrivacyChatServerTask during HttpPost execution UnsupportedEncodingException:" + e1.toString());
            } catch (Exception e) {
                Log.d(TAG, "PrivacyChatServerTask during HttpPost execution Exception:" + e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            if (progressDialog != null) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                progressDialog = null;
            }
            if (str != null && str.contains("error")) {
                Gson gson = new GsonBuilder().create();
                ErrorModel errorModel = null;
                try {
                    errorModel = gson.fromJson(str, ErrorModel.class);
                } catch (Exception e) {
                }
                if (errorModel != null) {
                    if (errorModel.citrusErrors != null
                            && !errorModel.citrusErrors.isEmpty()) {
                        ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
                        if (citrusError != null)
                            showDialog(citrusError.message);
                        else
                            showDialog("Please try again later.");
                    } else if (errorModel.message != null)
                        showDialog(errorModel.message);
                } else
                    showDialog("Please try again later.");
            } else if (str != null && str.contains("success")) {
//			{"status":"success","message":"Profile updated successfully"}
                Gson gson = new GsonBuilder().create();
                ErrorModel errorModel = null;
                try {
                    errorModel = gson.fromJson(str, ErrorModel.class);
                    if (errorModel.status.equals("success")) {
                        if (errorModel.message == null)
                            errorModel.message = "Privacy updated successfully.";
                        Toast.makeText(PrivacySettings.this, errorModel.message, Toast.LENGTH_SHORT).show();
                        if (CHAT_PRIVACY_TYPE == NO_MESSAGES_ALLOWED)
                            pref.saveStatusDNM(pref.getUserName(), true);
                        else if (CHAT_PRIVACY_TYPE == ALL_MESSAGES_ALLOWED)
                            pref.saveStatusDNM(pref.getUserName(), false);
                        if(CALL_PRIVACY_TYPE == NO_CALLS_ALLOWED)
                            pref.saveStatusDNC(pref.getUserName(), true);
                        else if(CALL_PRIVACY_TYPE == ALL_CALLS_ALLOWED)
                            pref.saveStatusDNC(pref.getUserName(), false);

                        sendPrivacyInfoToAll(privacyStatusModel);
                        finish();
                    }
                } catch (Exception e) {
                }
                if (errorModel != null) {
                }
            }
            super.onPostExecute(str);
        }


    }

    private void sendPrivacyInfoToAll(PrivacyStatusModel privacyStatusModel) {
        JSONObject finalJSONbject = new JSONObject();
        try {
            finalJSONbject.put("userName", pref.getUserName());
            if (privacyStatusModel != null && privacyStatusModel.getPrivacyTypes() != null) {
                JSONObject privacyJsonObj = new JSONObject();
                privacyJsonObj.put("DNC", privacyStatusModel.getPrivacyTypes().dnc);
                privacyJsonObj.put("DNM", privacyStatusModel.getPrivacyTypes().dnm);
                finalJSONbject.put("privacyStatusMap", privacyJsonObj);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (messageService != null) {
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
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

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
