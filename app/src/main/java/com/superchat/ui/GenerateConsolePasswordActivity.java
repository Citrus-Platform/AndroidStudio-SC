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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.ErrorModel;
import com.superchat.utils.Constants;
import com.superchat.utils.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class GenerateConsolePasswordActivity extends Activity{

	EditText password;
	EditText confirmPassword;
	Button submit;
	String pass;
	String confPass;
	private static final String TAG = "GenerateConsolePasswordActivity";
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.generate_password);
		password = (EditText)findViewById(R.id.id_pass);
		confirmPassword = (EditText)findViewById(R.id.id_conf_pass);
		submit = (Button)findViewById(R.id.id_submit);
		
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				generateRequest();
			}
		});
		
		((TextView)findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		confirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					// Do whatever you want here
					generateRequest();
					return true;
				}
				return false;
			}
		});
	}
	private void generateRequest(){
		pass = password.getText().toString();
		confPass = confirmPassword.getText().toString();
		if(!Utilities.validatePassword(pass)){
			Toast.makeText(GenerateConsolePasswordActivity.this, getString(R.string.pass_validation_alert), Toast.LENGTH_SHORT).show();
			return;
		}
		if(!Utilities.validatePassword(confPass)){
			Toast.makeText(GenerateConsolePasswordActivity.this, getString(R.string.pass_validation_alert), Toast.LENGTH_SHORT).show();
			return;
		}
		if(!pass.equals(confPass)){
			Toast.makeText(GenerateConsolePasswordActivity.this, "Password mismatch!", Toast.LENGTH_SHORT).show();
			return;
		}
		JSONObject obj = new JSONObject();
		try {
			obj.put("password", pass);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new GeneratePasswordTask(obj, null).execute();
	}
//=========================================================
	public class GeneratePasswordTask extends AsyncTask<String, String, String> {
		JSONObject requestJSON;
		ProgressDialog progressDialog = null;
		View view1;
		public GeneratePasswordTask(JSONObject requestJSON, final View view1){
			this.requestJSON = requestJSON;
			this.view1 = view1;
		}
		@Override
		protected void onPreExecute() {		
			progressDialog = ProgressDialog.show(GenerateConsolePasswordActivity.this, "", "Generating. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
//			String JSONstring = new Gson().toJson(registrationForm);		    
			DefaultHttpClient client1 = new DefaultHttpClient();
			String url = Constants.SERVER_URL+ "/tiger/rest/user/genconsolepwd";
			HttpPost httpPost = new HttpPost(url);
			Log.i(TAG, "SignupTaskForAdmin :: doInBackground:  url:"+url);
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
			HttpResponse response = null;
			try {
				httpPost.setEntity(new StringEntity(requestJSON.toString()));
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
						return str;
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
				ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
				if (errorModel != null) {
					if (errorModel.citrusErrors != null
							&& !errorModel.citrusErrors.isEmpty()) {
						ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
						Toast.makeText(GenerateConsolePasswordActivity.this, citrusError.message, Toast.LENGTH_SHORT).show();
					} else if (errorModel.message != null)
					Toast.makeText(GenerateConsolePasswordActivity.this, errorModel.message, Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(GenerateConsolePasswordActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
			}else if(str != null ){
				JSONObject json;
				try {
					json = new JSONObject(str);
					if(str !=null && str.contains("message"))
						Toast.makeText(GenerateConsolePasswordActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			}else
				Toast.makeText(GenerateConsolePasswordActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
			super.onPostExecute(str);
		}

	}
}

