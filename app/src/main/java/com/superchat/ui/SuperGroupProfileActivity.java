package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.AdminRegistrationForm;
import com.superchat.model.ErrorModel;
import com.superchat.utils.AppUtil;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuperGroupProfileActivity extends Activity implements OnClickListener{
	TextView superGroupName;
	TextView superGroupDisplayName;
	boolean isClosed;
	TextView createdOn;
	TextView ownerName;
	TextView adminCount;
	TextView memberCount;
	TextView sgDescription;
	LinearLayout sgDescriptionLayout;
	TextView sgType;
	Button sgEditButton;
	ImageView superGroupIconView;
	ImageView superGroupIconViewMain;
	ImageView superGroupPicEdit;
	Dialog picChooserDialog;
	String fileID;
	TextView aboutLabelView;
	boolean isEditModeOn;
	
	//Edit Mode
	TextView sgRealNameBox;
	TextView sgDisplayNameBox;
	EditText orgNameBox;
	EditText orgURLBox;
	EditText sgDescriptionBox;
	private RadioGroup radioGroup;
	private RadioButton radioGroupType;
	String privacyType = "closed";
	int selectedId = 0;

	String sg_display_name = null;
	//Values from profile
	String sg_real_name = null;
 	String org_name = null;
 	String org_URL = null;
 	String org_desc = null;
 	
 	Dialog editProfile;
 	
 	final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9][a-zA-z0-9_.]{1,50}$");

	private ChatService messageService;
	// private XMPPConnection xmppConnection;
	private ServiceConnection mMessageConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			messageService = ((ChatService.MyBinder) binder).getService();
			com.superchat.utils.Log.d("Service", "Connected");
			// xmppConnection = messageService.getconnection();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// xmppConnection = null;
			messageService = null;
		}
	};
	
private static final String TAG = "SuperGroupProfileActivity";
	@Override
	protected void onCreate(final Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.supergoup_profile);

		aboutLabelView = (TextView)findViewById(R.id.id_about_label);
		superGroupDisplayName = (TextView)findViewById(R.id.id_sg_display_name);
		superGroupName = (TextView)findViewById(R.id.id_sg_name);
		sgType = (TextView)findViewById(R.id.id_sg_type);
		sgDescription = (TextView)findViewById(R.id.id_sg_description);
		sgEditButton = (Button)findViewById(R.id.id_edit_btn);
		superGroupIconView = (ImageView) findViewById(R.id.id_profile_pic);

		SuperChatApplication.getInstance().settingFont(SuperChatApplication.FONT_TYPE.LATO_BOLD, superGroupDisplayName);

		superGroupIconViewMain = superGroupIconView;
		superGroupIconView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				fileID = SharedPrefManager.getInstance().getSGFileId(SharedPrefManager.getInstance().getUserDomain());
				String file_path = getImagePath(fileID);
				 if(fileID!=null && !fileID.equals(""))
						SuperChatApplication.removeBitmapFromMemCache(fileID);
				if(file_path != null && !file_path.equals("") && !file_path.contains("clear"))
				{
					if(fileID!=null && !fileID.equals("")){
					if (Build.VERSION.SDK_INT >= 11)
						new BitmapDownloader(SuperGroupProfileActivity.this,(ImageView)v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,fileID,BitmapDownloader.PIC_VIEW_REQUEST);
		             else
		            	 new BitmapDownloader(SuperGroupProfileActivity.this,(ImageView)v).execute(fileID,BitmapDownloader.PIC_VIEW_REQUEST);
					}
				}
			}
		});
		if(SharedPrefManager.getInstance().isDomainAdmin(SharedPrefManager.getInstance().getUserDomain())){
			sgEditButton.setVisibility(View.VISIBLE);
			sgEditButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showProfileEditDialog();
				}
			});
		}
		createdOn = (TextView)findViewById(R.id.id_created_on);
		ownerName = (TextView)findViewById(R.id.id_owner_name);
		adminCount = (TextView)findViewById(R.id.id_admin_count);
		memberCount = (TextView)findViewById(R.id.id_member_count);
		sgDescriptionLayout = (LinearLayout)findViewById(R.id.id_sg_description_layout);
		
		picChooserDialog = new Dialog(this);
		picChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picChooserDialog.setContentView(R.layout.pic_chooser_dialog);
		picChooserDialog.findViewById(R.id.id_camera).setOnClickListener(this);
		picChooserDialog.findViewById(R.id.id_gallery).setOnClickListener(this);
		picChooserDialog.findViewById(R.id.id_remove).setOnClickListener(this);
		
		(findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isEditModeOn){
			        backToProfileView();
				}else
					finish();
			}
		});
		superGroupDisplayName.setText(SharedPrefManager.getInstance().getCurrentSGDisplayName());
		String file_id = SharedPrefManager.getInstance().getSGFileId(SharedPrefManager.getInstance().getUserDomain());
		if(file_id != null && file_id.trim().length() > 0){
			setProfilePic(superGroupIconView, file_id);
		}
		new GetSuperGroupProfile(SharedPrefManager.getInstance().getUserDomain()).execute();
	}

	public void onResume() {
		super.onResume();
		bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);
	}

	public void onPause() {
		super.onPause();
		unbindService(mMessageConnection);
	}
	
	@Override
	public void onBackPressed(){
		if(isEditModeOn){
	        backToProfileView();
		}else
			finish();
	}
	public void backToProfileView(){
		if(editProfile != null){
			editProfile.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			editProfile.dismiss();
		}
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		isEditModeOn = false;
//		if(sg_real_name != null)
//			superGroupName.setText(sg_real_name);
		if(superGroupName != null)
			superGroupName.setText(sg_display_name);
		if(org_desc != null && !org_desc.equals("")){
			aboutLabelView.setVisibility(View.VISIBLE);
			sgDescriptionLayout.setVisibility(View.VISIBLE);
			sgDescription.setText(org_desc);
		}else{
			org_desc = null;
			aboutLabelView.setVisibility(View.GONE);
			sgDescriptionLayout.setVisibility(View.GONE);
		}
		final String file_id = SharedPrefManager.getInstance().getSGFileId(SharedPrefManager.getInstance().getUserDomain());
		if(file_id != null && file_id.trim().length() > 0){
//			superGroupIconView = superGroupIconViewMain;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					setProfilePic(superGroupIconViewMain, file_id);
				}
			});
		}
//		new GetSuperGroupProfile(SharedPrefManager.getInstance().getUserDomain()).execute();
	}
//================================================
	private void showProfileEditDialog(){
		try{
			// TODO Auto-generated method stub
			isEditModeOn = true;
			editProfile = new Dialog(this,android.R.style.Theme_Black_NoTitleBar);
			editProfile.setCanceledOnTouchOutside(false);
			editProfile.setContentView(R.layout.supergroup_profile_edit);//id_sg_type

			if(privacy_type != null && privacy_type.equalsIgnoreCase("closed")){
				//((TextView)editProfile.findViewById(R.id.id_sg_type)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, 0, 0);
				((TextView)editProfile.findViewById(R.id.id_sg_type)).setText(getString(R.string.private_hub));
			}
			else{
				//((TextView)editProfile.findViewById(R.id.id_sg_type)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_open, 0, 0, 0);
				((TextView)editProfile.findViewById(R.id.id_sg_type)).setText(getString(R.string.public_hub));
			}

			sgDisplayNameBox = (EditText)editProfile.findViewById(R.id.id_sg_display_name);
			sgRealNameBox = (TextView)editProfile.findViewById(R.id.id_sg_real_name_box);
			sgDescriptionBox = (EditText)editProfile.findViewById(R.id.id_desc_message);
			orgNameBox = (EditText)editProfile.findViewById(R.id.id_org_name);
			orgURLBox = (EditText)editProfile.findViewById(R.id.id_org_url);
			editProfile.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			superGroupIconView = (ImageView) editProfile.findViewById(R.id.id_profile_pic);
			radioGroup = (RadioGroup) editProfile.findViewById(R.id.radio_group);
			((Button)editProfile.findViewById(R.id.id_done_btn)).setOnClickListener(SuperGroupProfileActivity.this);
			((Button)editProfile.findViewById(R.id.id_done_btn_bottom)).setOnClickListener(SuperGroupProfileActivity.this);
			superGroupPicEdit = (ImageView) editProfile.findViewById(R.id.id_edit_pic);
			superGroupPicEdit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(picChooserDialog!=null && !picChooserDialog.isShowing()){
//						String fileName = fileID;
//						if(fileName==null ||fileName.equals("") || fileName.equals("clear"))
//							picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
//						else
//							picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.VISIBLE);
						picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
						picChooserDialog.show();
					}
				}
			});
			String file_id = SharedPrefManager.getInstance().getSGFileId(SharedPrefManager.getInstance().getUserDomain());
    		if(file_id != null && file_id.trim().length() > 0){
    			setProfilePic(superGroupIconView, file_id);
    		}

//			if(superGroupDisplayName != null)
//				superGroupDisplayName.addTextChangedListener(new TextWatcher() {
//
//					@Override
//					public void onTextChanged(CharSequence s, int start, int before, int count) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//						// TODO Auto-generated method stub
////						domainNameBefore = s.toString();
//					}
//
//					@Override
//					public void afterTextChanged(Editable s) {
//						// TODO Auto-generated method stub
////						if(validateInputForRegistration(false)){
////							if(nextButton != null)
////								nextButton.setVisibility(View.VISIBLE);
////							else
////								nextButton.setVisibility(View.GONE);
////						}
//						//Check here for available supergroup name
//						String address = superGroupDisplayName.getText().toString();
//						if(address.contains(" "))
//							address = address.replace(" ", "");
//						if(checkName(address))
//							superGroupDisplayName.setText(address);
//						else
//							superGroupDisplayName.setText(superGroupDisplayName);
//					}
//				});

			if(sg_display_name != null) {
				sgDisplayNameBox.setText(sg_display_name);
			} else if(sg_real_name != null){
				sgDisplayNameBox.setText(sg_real_name);
			}
			if(sg_real_name != null)
				sgRealNameBox.setText(sg_real_name);
			if(org_URL != null)
				orgURLBox.setText(org_URL);
			if(org_name != null)
				orgNameBox.setText(org_name);
			if(org_desc != null)
				sgDescriptionBox.setText(org_desc);
			(editProfile.findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isEditModeOn){
				        backToProfileView();
					}else
						finish();
				}
			});
			editProfile.show();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
//================================================
	public void onProfilePicClick(View view){
		
		if(picChooserDialog!=null && !picChooserDialog.isShowing()){
			String fileName = fileID;
			if(fileName==null ||fileName.equals("") || fileName.equals("clear"))
				picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
			else
				picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.VISIBLE);
			picChooserDialog.show();
		}
	}
//================================================
	String privacy_type = null;
	class GetSuperGroupProfile extends AsyncTask<String, String, String> {
		
		String domain_name;
		ProgressDialog progressDialog = null;
		public GetSuperGroupProfile(final String domain_name){
			this.domain_name = domain_name;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(SuperGroupProfileActivity.this, "", "Loading. Please wait...", true);
		    super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
		    try {
		    	String url = Constants.SERVER_URL + "/tiger/rest/admin/domain/profile?domainName="+URLEncoder.encode(domain_name, "UTF-8");
		    	Log.i(TAG, "GetSuperGroupProfile :: doInBackground : URL - "+url);
		        HttpGet httppost = new HttpGet(url);
		        HttpClient httpclient = new DefaultHttpClient();
		        HttpResponse response = httpclient.execute(httppost);
		        // StatusLine stat = response.getStatusLine();
		        int status = response.getStatusLine().getStatusCode();
		        if (status == 200) {
		            HttpEntity entity = response.getEntity();
		            String data = EntityUtils.toString(entity);
		            return data;
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch (Exception e) {

		        e.printStackTrace();
		    }
		    return null;
		}
		protected void onPostExecute(String data) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if(data != null){
				Log.i(TAG, "GetSuperGroupProfile :: onPostExecute : response data - "+data);
				 if(data.contains("success")){
					 	
		                String inviter = null;
		                String file_id = null;
		                String created_date = null;
		                String member_count = null;
		                try {
		    				JSONObject json = new JSONObject(data);
							if(json != null && json.has("displayName")){
								sg_display_name = json.getString("displayName");
								superGroupDisplayName.setText(sg_display_name);
//								superGroupName.setText("Permanent Name : "+sg_display_name);
							}
							else
								sg_display_name = null;

		    				if(json != null && json.has("domainName")){
								sg_real_name = json.getString("domainName");
//		    					superGroupDisplayName.setText(sg_real_name);
		    					superGroupName.setText("Permanent Name : "+sg_real_name);
		    				}
		    				else
								sg_real_name = null;

		    				if(json != null && json.has("adminName"))
		    					inviter = json.getString("adminName");
		    				else
		    					inviter = null;
		    				if(json != null && json.has("description")){
		    					org_desc = json.getString("description");
		    					if(org_desc != null && !org_desc.equals("")){
		    					aboutLabelView.setVisibility(View.VISIBLE);
		    					sgDescriptionLayout.setVisibility(View.VISIBLE);
		    					sgDescription.setText(org_desc);
		    					}else{
		    						org_desc = null;
			    					aboutLabelView.setVisibility(View.GONE);
			    					sgDescriptionLayout.setVisibility(View.GONE);
		    					}
		    				}
		    				else{
		    					org_desc = null;
		    					aboutLabelView.setVisibility(View.GONE);
		    					sgDescriptionLayout.setVisibility(View.GONE);
		    				}
		    				if(json != null && json.has("orgUrl"))
		    					org_URL = json.getString("orgUrl");
		    				else
		    					org_URL = null;
		    				ownerName.setVisibility(View.VISIBLE);
		    				ownerName.setText(inviter);
		    				adminCount.setText("Super Admins (1)");
		    				if(json != null && json.has("logoFileId"))
		    					file_id = json.getString("logoFileId");
		    				else
		    					file_id = null;
		    				if(file_id != null){
		    					fileID = file_id;
		    					//Save file ID here
		    					SharedPrefManager.getInstance().saveSGFileId(SharedPrefManager.getInstance().getUserDomain(), fileID);
		    					setProfilePic(superGroupIconView, fileID);
		    				}
		    				if(json != null && json.has("orgName"))
		    					org_name = json.getString("orgName");
		    				else
		    					org_name = null;
		    				if(json != null && json.has("numberOfMembers")){
		    					member_count = json.getString("numberOfMembers");
		    					memberCount.setVisibility(View.VISIBLE);
		    					memberCount.setText(""+member_count);
		    				}
		    				else
		    					member_count = null;
		    				if(json != null && json.has("createdDate")){
		    					created_date = json.getString("createdDate");
		    					if(created_date.indexOf(' ') != created_date.lastIndexOf(' ' ))
		    						created_date = created_date.substring(0, created_date.lastIndexOf(' ' ));
		    					if(created_date.indexOf(' ') != created_date.lastIndexOf(' ' ))
		    						created_date = created_date.substring(0, created_date.lastIndexOf(' ' ));
		    					createdOn.setVisibility(View.VISIBLE);
		    					createdOn.setText(created_date);
		    				}
		    				else
		    					created_date = null;
		    				if(json != null && json.has("privacyType"))
		    					privacy_type = json.getString("privacyType");
		    				else
		    					privacy_type = "open";
		    			} catch (JSONException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			}
		                sgType.setVisibility(View.VISIBLE);
		                if(privacy_type != null && privacy_type.equalsIgnoreCase("closed")){
//		                	showDialog("This SuperGroup only accepts members by invitation");
		                	isClosed = true;
		                	//sgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, 0, 0);
		                	sgType.setText(getString(R.string.private_hub));
		                }
		                else{
		                	isClosed = false;
		                	//sgType.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_open, 0, 0, 0);
		                	sgType.setText(getString(R.string.public_hub));
		                }
	            }else if(data.contains("error")){
	            	Gson gson = new GsonBuilder().create();
					ErrorModel errorModel = gson.fromJson(data, ErrorModel.class);
					if (errorModel != null) {
						if (errorModel.citrusErrors != null && !errorModel.citrusErrors.isEmpty()) {
							ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
							if(citrusError!=null && citrusError.code.equals("20021") ){
								showDialog(citrusError.message);
							}else
								showDialog(citrusError.message);
						} else if (errorModel.message != null)
							showDialog(errorModel.message);
					} else
						showDialog("Please try again later.");
	            }
			}else
				showDialog("Please try again later.");
		}
	}
//==============================================
	private String getImagePath(String groupPicId){
		if(groupPicId == null)
		 groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
		if(groupPicId!=null){
			String profilePicUrl = groupPicId+".jpg";
			File file = Environment.getExternalStorageDirectory();
			return new StringBuffer(file.getPath()).append(File.separator).append("SuperChat/").append(profilePicUrl).toString();
//			return Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+groupPicId+".jpg";
		}
		return null;
	}
	private boolean setProfilePic(ImageView picView, String groupPicId){

		groupPicId = DBWrapper.getInstance().getSGLogoFileID(SharedPrefManager.getInstance().getUserDomain());

		SharedPrefManager pref = SharedPrefManager.getInstance();
		if (groupPicId == null) {
			String domainName = pref.getUserDomain();
			groupPicId = pref.getSGFileId(domainName);
		}

		if(groupPicId == null || groupPicId != null && (groupPicId.equals("")||groupPicId.equals("clear") || groupPicId.contains("logofileid")))
			return false;
		String img_path = getImagePath(groupPicId);
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		picView.setImageResource(R.drawable.about_icon);
		if (bitmap != null) {
			picView.setImageBitmap(bitmap);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			picView.setTag(filename);
			return true;
		}else if(img_path != null){
			File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				picView.setImageURI(Uri.parse(img_path));
				setThumb((ImageView) picView,img_path,groupPicId);
				return true;
			}else{
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader(this, (RoundedImageView)picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
	             else
	            	 new BitmapDownloader(this, (RoundedImageView)picView).execute(groupPicId, BitmapDownloader.PROFILE_PIC_REQUEST);
			}
		}else{
		}
		if(groupPicId!=null && groupPicId.equals("clear"))
			return true;	
		return false;	
	}
	private void setThumb(ImageView imageViewl,String path, String groupPicId){
		BitmapFactory.Options bfo = new BitmapFactory.Options();
	    bfo.inSampleSize = 2;
	    Bitmap bm = null;
	    try{
		    bm = BitmapFactory.decodeFile(path, bfo);
//		    bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
		    bm = rotateImage(path, bm);
//		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
	    }catch(Exception ex){
	    	
	    }
	    if(bm!=null){
	    	imageViewl.setImageBitmap(bm);
//	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
	    } else{
	    	try{
	    		imageViewl.setImageURI(Uri.parse(path));
	    	}catch(Exception e){
	    		
	    	}
	    }
	}
	public static Bitmap rotateImage(String path, Bitmap bm) {
		int orientation = 1;
	try {
		ExifInterface exifJpeg = new ExifInterface(path);
		  orientation = exifJpeg.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

////			orientation = Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
		} catch (IOException e) {
			e.printStackTrace();
	}
	if (orientation != ExifInterface.ORIENTATION_NORMAL)
	{
		int width = bm.getWidth();
		int height = bm.getHeight();
		Matrix matrix = new Matrix();
		if (orientation == ExifInterface.ORIENTATION_ROTATE_90) 
		{
			matrix.postRotate(90);
		} 
		else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
			matrix.postRotate(180);
		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
			matrix.postRotate(270);
		}
		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		}
			
		return bm;
	}
//==============================================
	public void uploadProfilePicture(final String imgPath) {
		String packetID = null;
		if(imgPath != null && imgPath.length() > 0)
		{
			try
			{
				new ProfilePicUploader(this, null, true, notifyPhotoUploadHandler).execute(imgPath, packetID,"",
						"SG_FILE_ID", SharedPrefManager.getInstance().getUserName());
			}catch(Exception ex)
			{
				showDialog(getString(R.string.failed),getString(R.string.photo_upload_failed));
			}
		}
	}
	private final Handler notifyPhotoUploadHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
	    	String file_id = SharedPrefManager.getInstance().getSGFileId(SharedPrefManager.getInstance().getUserDomain());
			System.out.println("file_id ===> "+file_id);
//    		if(file_id != null && file_id.trim().length() > 0){
//    			sendUpdateSGRequest(file_id);
//    		}
	    }
	};
	private void sendUpdateSGRequest(String file_id){
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
		AdminRegistrationForm registrationForm = new AdminRegistrationForm("", "normal",imei, imsi, clientVersion);
		registrationForm.setDomainName(SharedPrefManager.getInstance().getUserDomain());
		registrationForm.setSGFileID(file_id);
//		if(Build.VERSION.SDK_INT >= 11)
//			new UpdateSuperGroupTask(registrationForm, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		else
//			new UpdateSuperGroupTask(registrationForm, null).execute();
	}
//==============================================
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == HomeScreen.RESULT_OK)
				switch (requestCode) {
				case AppUtil.POSITION_CAMERA_PICTURE:
				case AppUtil.POSITION_GALLRY_PICTURE:
					if (data != null && data.getData() != null) 
					{
						Uri uri = data.getData();
						AppUtil.capturedPath1 = AppUtil.getPath(uri, this);
					}
//					CompressImage compressImage = new CompressImage(this);
//					AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
					performCrop(AppUtil.PIC_CROP);
					break;
				case AppUtil.PIC_CROP:
					String filePath= Environment.getExternalStorageDirectory()
							+"/"+Constants.contentTemp+"/"+AppUtil.TEMP_PHOTO_FILE;
					AppUtil.capturedPath1 = filePath ;
					 Bitmap selectedImage =  BitmapFactory.decodeFile(AppUtil.capturedPath1);
					 superGroupIconView.setImageBitmap(selectedImage);
					 superGroupIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
					 uploadProfilePicture(AppUtil.capturedPath1);
					break;
				}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}
	private void performCrop(byte resultCode) {
		try {
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			File file = new File(AppUtil.capturedPath1);
			Uri outputFileUri = Uri.fromFile(file);
			cropIntent.setDataAndType(outputFileUri, "image/*");
			cropIntent.putExtra("outputX", 300);
			cropIntent.putExtra("outputY", 300);
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("scale", true);
			try {
				cropIntent.putExtra("return-data", false);
				cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getTempUri());
				cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());	
				startActivityForResult(cropIntent, resultCode);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		} catch (ActivityNotFoundException anfe) {
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}
//==============================================
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
	public void showDialog(String title, String s) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		if(title!=null)
			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.id_done_btn:
		case R.id.id_done_btn_bottom:
			if(sgDisplayNameBox != null && sgDisplayNameBox.getText().toString().trim().length() < 3){
				showDialog(getString(R.string.domain_display_name_validation_alert));
				return;
			}
			if(sgDisplayNameBox != null && sgDisplayNameBox.getText().toString().trim().length() > 2 && !Character.isLetter(sgDisplayNameBox.getText().toString().trim().charAt(0))){
				showDialog(getString(R.string.domain_display_name_validation_alert));
				return;
			}
			if(!checkUserDomainName(sgRealNameBox.getText().toString())){
				showDialog(getString(R.string.domain_validation_alert));
				return;
			}
			String fileid = SharedPrefManager.getInstance().getSGFileId(SharedPrefManager.getInstance().getUserDomain());
			JSONObject finalJSONbject = new JSONObject();
			try{
				if(sgDisplayNameBox != null && sgDisplayNameBox.getText().toString().trim().length() > 0) {
					sg_display_name = sgDisplayNameBox.getText().toString().trim();
					finalJSONbject.put("displayName", sg_display_name);
				}
				if(sgRealNameBox != null && sgRealNameBox.getText().toString().trim().length() > 0)
					finalJSONbject.put("domainName", sgRealNameBox.getText().toString().trim());
				if(sgDescriptionBox != null)// && sgDescriptionBox.getText().toString().trim().length() > 0)
					finalJSONbject.put("description", sgDescriptionBox.getText().toString().trim());
				if(orgNameBox != null)// && orgNameBox.getText().toString().trim().length() > 0)
					finalJSONbject.put("orgName", orgNameBox.getText().toString().trim());
				if(orgURLBox != null)// && orgURLBox.getText().toString().trim().length() > 0)
					finalJSONbject.put("orgUrl", orgURLBox.getText().toString().trim());
				if(fileid != null && fileid.length() > 0)
					finalJSONbject.put("logoFileId", fileid);
			}catch(JSONException ex){
				
			}
			if(Build.VERSION.SDK_INT >= 11)
				new UpdateSuperGroupTask(finalJSONbject, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new UpdateSuperGroupTask(finalJSONbject, null).execute();
			break;
		case R.id.id_camera:
			AppUtil.clearAppData();
			AppUtil.openCamera(this, AppUtil.capturedPath1,
					AppUtil.POSITION_CAMERA_PICTURE);
			picChooserDialog.cancel();
			break;
		case R.id.id_gallery:
			AppUtil.clearAppData();
			AppUtil.openImageGallery(this,
					AppUtil.POSITION_GALLRY_PICTURE);
			picChooserDialog.cancel();
			break;
		case R.id.id_remove:
			picChooserDialog.cancel();
			String currentFileId = fileID;
			if(currentFileId!=null && !currentFileId.equals("clear") && !currentFileId.equals("")){
//				iSharedPrefManager.saveUserFileId(iSharedPrefManager.getUserName(), "clear");
				((ImageView) findViewById(R.id.id_profile_pic)).setImageResource(R.drawable.logo_small);
//				new UpdateProfileTaskOnServer().execute();
			}else{
				showDialog(getString(R.string.app_name),getString(R.string.no_image_delete));
			}
			break;
		}
	}
//================================================================================
	public class UpdateSuperGroupTask extends AsyncTask<String, String, String> {
		JSONObject requestJSON;
		ProgressDialog progressDialog = null;
		View view1;
		public UpdateSuperGroupTask(JSONObject requestJSON,final View view1){
			this.requestJSON = requestJSON;
			this.view1 = view1;
		}
		@Override
		protected void onPreExecute() {		
			progressDialog = ProgressDialog.show(SuperGroupProfileActivity.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
//			String JSONstring = new Gson().toJson(registrationForm);		    
			DefaultHttpClient client1 = new DefaultHttpClient();
			String url = Constants.SERVER_URL+ "/tiger/rest/admin/domain/update";
			HttpPost httpPost = new HttpPost(url);
			Log.i(TAG, "SignupTaskForAdmin :: doInBackground:  url:"+url);
			System.out.println("Request :: "+requestJSON.toString());
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
//						if(str!=null &&!str.equals("")){
//							str = str.trim();
//							Gson gson = new GsonBuilder().create();
//							if (str==null || str.contains("error")){
//								return str;
//							}
//						}
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
			System.out.println("Onpost str :: "+str);
			if (str!=null && str.contains("error")){
				Gson gson = new GsonBuilder().create();
				ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
				if (errorModel != null) {
					if (errorModel.citrusErrors != null
							&& !errorModel.citrusErrors.isEmpty()) {
						ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
						if(citrusError!=null && citrusError.code.equals("20019") ){
							showDialog(citrusError.message);
						}else{
							if(citrusError.message != null && citrusError.message.contains("no data to update")){
								backToProfileView();
							}else
								showDialog(citrusError.message);
						}
					} else if (errorModel.message != null)
						showDialog(errorModel.message);
				} else
					showDialog("Please try again later.");
			}else{
//				sg_display_name = superGroupDisplayName.getText().toString().trim();
				//Save SG display name
				SharedPrefManager prefManager = SharedPrefManager.getInstance();
				prefManager.saveCurrentSGDisplayName(sg_display_name);
				if(superGroupDisplayName != null)
					superGroupDisplayName.setText(sg_display_name);
				sg_real_name = sgRealNameBox.getText().toString().trim();
			 	org_name = orgNameBox.getText().toString().trim();
			 	org_URL = orgURLBox.getText().toString().trim();
			 	org_desc = sgDescriptionBox.getText().toString().trim();
				//Braodcast message to all
				JSONObject finalJSONbject = new JSONObject();
				if (superGroupDisplayName != null)
					try {
						if(superGroupDisplayName != null ) {
							finalJSONbject.put("domainDisplayName", sg_display_name);
							DBWrapper.getInstance().updateSGDisplayName(sg_real_name , sg_display_name);
						}
						if (requestJSON.has("logoFileId")) {
							finalJSONbject.put("domainPicID", requestJSON.get("logoFileId").toString());
							prefManager.saveSGFileId(sg_real_name, requestJSON.get("logoFileId").toString());
							DBWrapper.getInstance().updateSGLogoFileID(sg_real_name , requestJSON.get("logoFileId").toString());
						}

						String json_txt = finalJSONbject.toString();
						System.out.println("Final JSON :  " + json_txt);
						messageService.sendSpecialMessageToAllDomainMembers(
								prefManager.getUserDomain() + "-system", json_txt,
								Message.XMPPMessageType.atMeXmppMessageTypeSGUpdate);
						json_txt = null;
					} catch (JSONException e) {
						e.printStackTrace();
					}

	    		//Show toast
				try {
					JSONObject json = new JSONObject(str);
					if(json != null && json.has("message")){
						Toast.makeText(getApplicationContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				backToProfileView();
			}
			super.onPostExecute(str);
		}
	}
//===============================
	private boolean checkUserDomainName(String name) {
		if (name == null)
			return false;
		Matcher m = DOMAIN_NAME_PATTERN.matcher(name);
		return m.matches();
	}
}
