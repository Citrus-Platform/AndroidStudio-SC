package com.superchat.ui;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.db.ChatDBWrapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.ErrorModel;
import com.superchat.utils.Constants;
import com.superchat.utils.FileDownloadResponseHandler;
import com.superchat.utils.FileUploaderDownloader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.ZipManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;


public class ChatBackupScreen extends Activity implements OnClickListener, OnCheckedChangeListener, FileDownloadResponseHandler{

    LinearLayout backupSettings;
    LinearLayout backupNetworkSettings;
    RadioGroup radioGroup;
    Dialog radioSelectionDialog;
    TextView backupSettingTxt;
    TextView lastBackUpDate;
    TextView backupNetworkSettingTxt;
    Button backup;
    String zip_file_path;
    private static final byte BACK_UP_SETTING = 1;
    private static final byte BACK_UP_NETWORK_SETTING = 2;
    byte selectionType = BACK_UP_SETTING;
	private static final String TAG = "ChatBackupScreen";
	String backedUpFileID = null;
	boolean onlyWifi = false;
	int backupOn;
	String[] wifiOptions = new String[]{"WiFi", "WiFi or Cellular"};
	String[] backupOptions = new String[]{"Never", "Only when I tap \"Back up\"", "Daily", "Weekly", "Monthly"};
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.chat_backup);
//		password = (EditText)findViewById(R.id.id_pass);
//		confirmPassword = (EditText)findViewById(R.id.id_conf_pass);
//		submit = (Button)findViewById(R.id.id_submit);
		
		backupSettings = (LinearLayout)findViewById(R.id.id_backup_to_server);
		backupNetworkSettings = (LinearLayout)findViewById(R.id.id_backup_network_server);

		backupSettingTxt = (TextView)findViewById(R.id.id_backup_setting);
		backupNetworkSettingTxt = (TextView)findViewById(R.id.id_backup_network_setting);
		lastBackUpDate = (TextView)findViewById(R.id.id_last_backup_date);
		
		backup = (Button)findViewById(R.id.id_backup);
		
		backup.setOnClickListener(this);
		backupSettings.setOnClickListener(this);
		backupNetworkSettings.setOnClickListener(this);

		if(SharedPrefManager.getInstance().isWifiBackup())
			backupNetworkSettingTxt.setText(wifiOptions[0]);
		else
			backupNetworkSettingTxt.setText(wifiOptions[1]);
		if(SharedPrefManager.getInstance().getBackupSchedule() > -1)
			backupSettingTxt.setText(backupOptions[SharedPrefManager.getInstance().getBackupSchedule()]);
		else if(SharedPrefManager.getInstance().getBackupSchedule() == -1)
			backupSettingTxt.setText(backupOptions[2]);
		(findViewById(R.id.back_id)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	public void onResume() {
		super.onResume();
		long time = SharedPrefManager.getInstance().getLastBackUpTime();
		if(time > 0 ) {
			Date date = new Date(time);
			SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US);
			lastBackUpDate.setText(dateformat.format(date));
		}else
			lastBackUpDate.setVisibility(View.GONE);
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
			progressDialog = ProgressDialog.show(ChatBackupScreen.this, "", "Generating. Please wait...", true);
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
						Toast.makeText(ChatBackupScreen.this, citrusError.message, Toast.LENGTH_SHORT).show();
					} else if (errorModel.message != null)
					Toast.makeText(ChatBackupScreen.this, errorModel.message, Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(ChatBackupScreen.this, "Please try again later.", Toast.LENGTH_SHORT).show();
			}else if(str != null ){
				JSONObject json;
				try {
					json = new JSONObject(str);
					if(str !=null && str.contains("message"))
						Toast.makeText(ChatBackupScreen.this, json.getString("message"), Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			}else
				Toast.makeText(ChatBackupScreen.this, "Please try again later.", Toast.LENGTH_SHORT).show();
			super.onPostExecute(str);
		}

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.id_backup:
			zip_file_path = ChatDBWrapper.getInstance().getAllMessagesForBackup();
			//Check if file exists then upload zip file to server 
     	   File file = new File(zip_file_path);
     	   FileBody data = new FileBody(new File(zip_file_path));
    	   if (file.exists() && (int)data.getContentLength() > 0) 
    		   new FileUploaderDownloader(this, this, true, true, notifyFileUploadHandler).execute(zip_file_path);
			break;
		case R.id.id_cancel:
			if(radioSelectionDialog != null)
				radioSelectionDialog.dismiss();
			break;
			case R.id.id_done:
				if(radioSelectionDialog != null)
					radioSelectionDialog.dismiss();
				if(selectionType == BACK_UP_SETTING) {
					SharedPrefManager.getInstance().setBackupSchedule(backupOn);
					backupSettingTxt.setText(backupOptions[backupOn]);
				}else{
					if(onlyWifi) {
						SharedPrefManager.getInstance().setWifiBackup(true);
						backupNetworkSettingTxt.setText(wifiOptions[0]);
					}
					else {
						SharedPrefManager.getInstance().setWifiBackup(false);
						backupNetworkSettingTxt.setText(wifiOptions[1]);
					}
				}
				break;

		case R.id.id_backup_to_server:
			// custom dialog

			backupOn = SharedPrefManager.getInstance().getBackupSchedule();
			if(backupOn == -1)
				backupOn = 2;
			radioSelectionDialog = new Dialog(this);
			radioSelectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			radioSelectionDialog.setContentView(R.layout.radiobutton_dialog);
			RadioButton never = (RadioButton)radioSelectionDialog.findViewById(R.id.id_never);
			RadioButton when_tap = (RadioButton)radioSelectionDialog.findViewById(R.id.id_when_tap);
			RadioButton daily = (RadioButton)radioSelectionDialog.findViewById(R.id.id_daily);
			RadioButton weekly = (RadioButton)radioSelectionDialog.findViewById(R.id.id_weekly);
			RadioButton monthly = (RadioButton)radioSelectionDialog.findViewById(R.id.id_monthly);

			((TextView) radioSelectionDialog.findViewById(R.id.id_cancel)).setOnClickListener(this);
			((TextView) radioSelectionDialog.findViewById(R.id.id_done)).setOnClickListener(this);
			selectionType = BACK_UP_SETTING;
			switch(backupOn){
				case 0:
					never.setChecked(true);
					break;
				case 1:
					when_tap.setChecked(true);
					break;
				case 2:
					daily.setChecked(true);
					break;
				case 3:
					weekly.setChecked(true);
					break;
				case 4:
					monthly.setChecked(true);
					break;
			}
			never.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("===never=====");
					backupOn = 0;
//					SharedPrefManager.getInstance().setBackupSchedule(0);
				}
			});
			when_tap.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("===when_tap=====");
					backupOn = 1;
//					SharedPrefManager.getInstance().setBackupSchedule(1);
				}
			});
			daily.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("===daily=====");
					backupOn = 2;
//					SharedPrefManager.getInstance().setBackupSchedule(2);
				}
			});
			weekly.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("===weekly=====");
					backupOn = 3;
//					SharedPrefManager.getInstance().setBackupSchedule(3);
				}
			});
			monthly.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("===monthly=====");
					backupOn = 4;
//					SharedPrefManager.getInstance().setBackupSchedule(4);
				}
			});
			radioSelectionDialog.show();
			break;
		case R.id.id_backup_network_server:
			onlyWifi = SharedPrefManager.getInstance().isWifiBackup();
			radioSelectionDialog = new Dialog(this);
			radioSelectionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			radioSelectionDialog.setContentView(R.layout.backup_over);
			RadioButton wifi = (RadioButton)radioSelectionDialog.findViewById(R.id.id_wifi);
			RadioButton both = (RadioButton)radioSelectionDialog.findViewById(R.id.id_both);
			((TextView) radioSelectionDialog.findViewById(R.id.id_title)).setText("Back up over");
	        ((TextView) radioSelectionDialog.findViewById(R.id.id_cancel)).setOnClickListener(this);
	        ((TextView) radioSelectionDialog.findViewById(R.id.id_done)).setOnClickListener(this);
			if(onlyWifi)
				wifi.setChecked(true);
			else
				both.setChecked(true);
			wifi.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("===wifi=====");
					onlyWifi = true;
//					SharedPrefManager.getInstance().setBackupSchedule(0);
				}
			});
			both.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("===wifi & network=====");
					onlyWifi = false;
//					SharedPrefManager.getInstance().setBackupSchedule(1);
				}
			});
	        selectionType = BACK_UP_NETWORK_SETTING;
	        radioSelectionDialog.show();
			break;
		}
	}
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
//		int childCount = group.getChildCount();
//        for (int x = 0; x < childCount; x++) {
//           RadioButton btn = (RadioButton) group.getChildAt(x);
//           if (btn.getId() == checkedId) {
//        	   System.out.println("selected RadioButton->"+btn.getText().toString());
//        	   if(selectionType == BACK_UP_SETTING) {
//				   backupSettingTxt.setText(btn.getText().toString());
//				   //{"Never", "Only when I tap \"Back up\"", "Daily", "Weekly", "Monthly"};
//				   if(btn.getText().toString().equalsIgnoreCase(backupOptions[0]))
//				  	 backupOn = 0;
//				   else if(btn.getText().toString().equalsIgnoreCase(backupOptions[1]))
//					   backupOn = 1;
//				   else if(btn.getText().toString().equalsIgnoreCase(backupOptions[2]))
//					   backupOn = 2;
//				   else if(btn.getText().toString().equalsIgnoreCase(backupOptions[3]))
//					   backupOn = 3;
//				   else if(btn.getText().toString().equalsIgnoreCase(backupOptions[4]))
//					   backupOn = 4;
//			   }
//        	   else if(selectionType == BACK_UP_NETWORK_SETTING) {
//				   backupNetworkSettingTxt.setText(btn.getText().toString());
//				   if(btn.getText().toString().equalsIgnoreCase(wifiOptions[0]))
//					   onlyWifi = true;
//				   else
//					   onlyWifi = false;
//			   }
////        	   radioSelectionDialog.dismiss();
//           }
//        }
	}
	private final Handler notifyFileUploadHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			backedUpFileID = SharedPrefManager.getInstance().getBackupFileId();
			System.out.println("[backedUpFileID ] - "+backedUpFileID);
			//Upload this backup file id to server
			if(backedUpFileID != null){
				if(Build.VERSION.SDK_INT >= 11)
					new UploadDataBackup().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				else
					new UploadDataBackup().execute();
			}
			
			//Download backed up file
//			if(backedUpFileID != null){
//				String backup_dir = "SCBackup";
//				String zip_file = "SCBackup2.zip";
//				createDirIfNotExists(Environment.getExternalStorageDirectory().getAbsolutePath(), backup_dir);
//				String full_path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + backup_dir + "/" + zip_file;
//				new FileUploaderDownloader(ChatBackupScreen.this, ChatBackupScreen.this, true, false, notifyFileUploadHandler).execute(full_path, backedUpFileID);
//			}
		}
	};
	
	class UploadDataBackup extends AsyncTask<String, String, String> {
		
		public UploadDataBackup(){
		}
		@Override
		protected void onPreExecute() {
		    super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			 String data = null;
		    try {
//		    	String url = Constants.SERVER_URL + "/tiger/rest/admin/domain/check?domainName="+URLEncoder.encode(domain_name, "UTF-8");
				//String url = Constants.SERVER_URL+ "/tiger/rest/user/genconsolepwd";
//		    	String url = "http://superchat3c.com/tiger/rest/user/profile/update";
		    	String url = Constants.SERVER_URL + "/tiger/rest/user/profile/update";
		    	Log.i(TAG, "UploadDataBackup :: doInBackground : URL - "+url);
		    	JSONObject jsonobj = new JSONObject();
		    	jsonobj.put("backupFileId", backedUpFileID);
		        HttpPost httppost = new HttpPost(url);
		        httppost = SuperChatApplication.addHeaderInfo(httppost,true);
		        HttpClient httpclient = new DefaultHttpClient();
		        httppost.setEntity(new StringEntity(jsonobj.toString()));
		        HttpResponse response = httpclient.execute(httppost);
		        // StatusLine stat = response.getStatusLine();
		        int status = response.getStatusLine().getStatusCode();
		        if (status == 200) {
		            HttpEntity entity = response.getEntity();
		            data = EntityUtils.toString(entity);
//		            return data;
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch (Exception e) {

		        e.printStackTrace();
		    }
		    return data;
		}
		@Override
		protected void onPostExecute(String data) {
			if(data != null){
				//Delete directory and files
				System.out.println("==== backup response===>"+data);
				if(zip_file_path != null){
					String dir_path = zip_file_path.substring(0, zip_file_path.lastIndexOf('/'));
					deleteDirectoryWithContets(new File(dir_path));
				}
				//Update Time in Shared Preferences
				SharedPrefManager.getInstance().setLastBackUpTime(System.currentTimeMillis());
				Toast.makeText(ChatBackupScreen.this, "Data backed up successfully.", Toast.LENGTH_SHORT).show();
			    finish();
			}
		}
	}
	public static void deleteDirectoryWithContets(File dir)
		{
		    if ( dir.isDirectory() )
		    {
		        String [] children = dir.list();
		        for ( int i = 0 ; i < children.length ; i ++ )
		        {
		         File child =    new File( dir , children[i] );
		         if(child.isDirectory()){
		        	 deleteDirectoryWithContets( child );
		             child.delete();
		         }else{
		             child.delete();
	
		         }
		        }
		        dir.delete();
		    }
		}
	public boolean createDirIfNotExists(String path, String folder_name) {
	    boolean ret = true;
	    File file = new File(path, folder_name);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("TravellerLog :: ", "Problem creating Image folder");
	            ret = false;
	        }
	    }
	    return ret;
	}
	@Override
	public void onFileDownloadResposne(View view, int type, byte[] data) {
		// TODO Auto-generated method stub
		String backup_dir = "SCBackup";
		String zip_file = "SCBackup.scb";
		createDirIfNotExists(Environment.getExternalStorageDirectory().getAbsolutePath(), backup_dir);
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + backup_dir +"/";
		String full_path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + backup_dir + "/" + zip_file;
		ZipManager zipManager = new ZipManager(null, null);
		Vector<String> files = zipManager.unzip(full_path, path);
		String os_type = null;
		if(files != null){
//			Gson gson = new GsonBuilder().create();
			for(int i = 0; i < files.size(); i++){
				String filedata = getStringFromFile(path + files.elementAt(i));
//				System.out.println(filedata);
				JSONObject jsonobj;
				try {
					jsonobj = new JSONObject(filedata);
					if(jsonobj.has("osType"))
						os_type = (String) jsonobj.get("osType");
					JSONArray messages = (JSONArray) jsonobj.get("messages");
//					MessageDataModel message_data = gson.fromJson(filedata, MessageDataModel.class);
					int success = ChatDBWrapper.getInstance().insertBackUpInDB(messages, os_type);
					if(success == 1)
						System.out.println("------DATA SUCCESSFULLY RESTORED IN DB-----");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	@Override
	public void onFileDownloadResposne(View view, int[] type, String[] file_urls, String[] file_paths) {
		// TODO Auto-generated method stub
		
	}
	
	public static String getStringFromFile (String filename) {
		BufferedReader br = null;
		StringBuffer file_date = new StringBuffer();
		try {
		        try {
		            br = new BufferedReader(new FileReader(filename));
		        } catch (FileNotFoundException e1) {
		            e1.printStackTrace();
		        }
		        String line = "";
		        while ((line = br.readLine()) != null) {
		         //Do something here 
		        	file_date.append(line);
		        }
			}catch(Exception ex){
		}
		return file_date.toString();
	}
}

