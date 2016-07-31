package com.superchat.ui;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.ErrorModel;
import com.superchat.model.GroupChatServerModel;
import com.superchat.ui.MainActivity.CheckAvailability;
import com.superchat.utils.AppUtil;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.ProfilePicDownloader;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.Utilities;
import com.superchat.widgets.MyriadSemiboldTextView;
import com.superchat.widgets.RoundedImageView;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class CreateSharedIDActivity extends Activity implements OnClickListener{
//	ArrayList<String> inviters;
	private static final String TAG = "CreateGroupScreen";
	private String sharedIDName;
	private String sharedID;
	private String sharedIDFileID;
	private String sharedIDDescription;
	ImageView cameraImageView;
	EditText groupNameView;
	EditText groupDiscriptionView;
	TextView nextButton;
	TextView cancelButton;
	Dialog picChooserDialog;
	ProfilePicUploader picUploader;
	RelativeLayout createSharedID;
	LinearLayout deleteSharedID;
	ImageView groupIconView;
	boolean isForGroupUpdate;
	boolean isForBroadCastUpdate;
	String groupFileId;
	private ChatService messageService;
	private XMPPConnection xmppConnection;
//	private boolean isBroadcast;
//	private boolean isChannel;
	private MyriadSemiboldTextView title;
	private RadioGroup radioGroup;
	private RadioButton radioGroupType;
	boolean isEditMode;
	LinearLayout mainLayout;
	private TextDrawable.IBuilder mDrawableBuilder;
	List<String> groupAdmins;
	TextView memberCountView;
	boolean isAdminUser;
	boolean isSharedIDDeactivated;
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
	private ServiceConnection mMessageConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			messageService = ((ChatService.MyBinder) binder).getService();
			Log.d("Service", "Connected");
			xmppConnection = messageService.getconnection();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			xmppConnection = null;
			messageService = null;
		}};
	private boolean onForeground;
	String domainNameBefore = null;
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_shared_id);
		((ImageView)findViewById(R.id.id_back)).setOnClickListener(this);
		groupNameView =(EditText)findViewById(R.id.id_group_name);
		createSharedID =(RelativeLayout)findViewById(R.id.id_create_shared_id_layout);
		deleteSharedID =(LinearLayout)findViewById(R.id.id_delete);
		groupDiscriptionView=(EditText)findViewById(R.id.id_status_message);
		groupIconView = (ImageView) findViewById(R.id.id_group_icon);
		nextButton=(TextView)findViewById(R.id.id_next);
		title = (MyriadSemiboldTextView)findViewById(R.id.id_group_info_title);
		cancelButton=(TextView)findViewById(R.id.id_cancel);
		mainLayout = (LinearLayout)findViewById(R.id.id_group_members);
		cameraImageView =(ImageView)findViewById(R.id.id_group_camera_icon);
		cameraImageView.setOnClickListener(this);
		radioGroup = (RadioGroup) findViewById(R.id.radio_group);
		memberCountView = (TextView)findViewById(R.id.id_participants_count);
		createSharedID.setOnClickListener(this);
		deleteSharedID.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		groupIconView.setOnClickListener(this);
		picChooserDialog = new Dialog(this);
		picChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picChooserDialog.setContentView(R.layout.pic_chooser_dialog);
		picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
		picChooserDialog.findViewById(R.id.id_camera).setOnClickListener(this);
		picChooserDialog.findViewById(R.id.id_gallery).setOnClickListener(this);
		Bundle extras = getIntent().getExtras();
		((TextView)findViewById(R.id.id_what_is_this)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopupDialog();
			}
		});
		mDrawableBuilder = TextDrawable.builder()
                .beginConfig().toUpperCase().endConfig().round();
		deleteSharedID.setVisibility(View.GONE);
		if(extras != null){
			isForGroupUpdate = extras.getBoolean(Constants.IS_GROUP_INFO_UPDATE, false);
			isEditMode = extras.getBoolean("EDIT_MODE", false);
			sharedID = extras.getString(Constants.GROUP_UUID, "");
			sharedIDName = extras.getString(Constants.GROUP_NAME, "");
			groupFileId = extras.getString(Constants.GROUP_FILE_ID, "");
			if(!SharedPrefManager.getInstance().isDomainAdmin() && HomeScreen.isAdminFromSharedID(sharedID, SharedPrefManager.getInstance().getUserName()))
				isAdminUser = true;
			isSharedIDDeactivated = SharedPrefManager.getInstance().isSharedIDDeactivated(sharedID);
			if(isEditMode){
				if(isAdminUser || isSharedIDDeactivated){
					groupNameView.setEnabled(false);
					cameraImageView.setVisibility(View.GONE);
					((TextView)findViewById(R.id.id_add_photo)).setVisibility(View.GONE);
					createSharedID.setVisibility(View.GONE);
					deleteSharedID.setVisibility(View.GONE);
					groupIconView.setOnClickListener(null);
					((TextView)findViewById(R.id.id_next)).setVisibility(View.GONE);
				}else{
					deleteSharedID.setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.id_next)).setVisibility(View.VISIBLE);
				}
				if(groupFileId != null)
					((TextView)findViewById(R.id.id_add_photo)).setVisibility(View.GONE);
				((ImageView)findViewById(R.id.id_info)).setVisibility(View.GONE);
				((TextView)findViewById(R.id.id_next)).setOnClickListener(this);
//				if(SharedPrefManager.getInstance().isDomainAdmin())
//					((ImageView)findViewById(R.id.id_more_option1)).setVisibility(View.VISIBLE);
			}else
				deleteSharedID.setVisibility(View.GONE);
			if(sharedIDName != null && sharedIDName.trim().length() > 0){
				groupNameView.setText(sharedIDName);
				groupNameView.setSelection(sharedIDName.length());
			}
			if(sharedIDDescription != null && sharedIDDescription.trim().length() > 0)
				groupDiscriptionView.setText(sharedIDDescription);
			if(groupFileId != null && groupFileId.trim().length() > 0)
				setProfilePic(groupIconView, groupFileId);
			else
				groupIconView.setImageResource(R.drawable.helpdesk);
//			groupAdmins = HomeScreen.getAdminSetForSharedID(sharedID);
//			if(isEditMode && groupAdmins != null && groupAdmins.size() > 0){
//				mainLayout.setVisibility(View.VISIBLE);
//				addMembersToView(mainLayout, groupAdmins);
//			}
		}
//		if(!isEditMode)
//			groupNameView.addTextChangedListener(new TextWatcher() {
//				
//				@Override
//				public void onTextChanged(CharSequence s, int start, int before, int count) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//					// TODO Auto-generated method stub
//					domainNameBefore = s.toString();
//				}
//				
//				@Override
//				public void afterTextChanged(Editable s) {
//					// TODO Auto-generated method stub
//					if(groupNameView.getText().toString().trim().length() >= 3){
//						if(!Utilities.checkName(displayName))
//					}else{
//						if(checkAvailability != null)
//							checkAvailability.setVisibility(View.GONE);
//						if(text_view != null)
//							text_view.setVisibility(View.GONE);
//					}
//				}
//			});
	}
	private void addMembersToView(LinearLayout mainLayout, List<String> list){
		SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
		String[] data = null;
		if(list.size() > 0){
			if(memberCountView != null)
				memberCountView.setText(""+list.size() + "/10");
		}
		for(String text:list){
			String tmpText = text;
			if(text.indexOf(':') != -1){
				data = text.split(":");
				if(data != null && data.length >= 2){
					text = data[0];
					tmpText = data[1];
				}
			}
			final String user_name = text;
				
			if(tmpText == null){
				if(text != null && text.equals(iPrefManager.getUserName()))
					tmpText = iPrefManager.getDisplayName();
				else
					tmpText = iPrefManager.getUserServerName(text);
				if(tmpText == null || (tmpText != null && (tmpText.equals(text) || tmpText.equalsIgnoreCase("null"))))
					tmpText = "Superchatter";
			}else if(tmpText != null && tmpText.equalsIgnoreCase("null"))
				tmpText = "Superchatter";
			else{//tmpText not null
				//Check if name is not saved in shared pref , then save
				if(tmpText.equals(text)){
					if(text.equals(iPrefManager.getUserName()))
						tmpText = iPrefManager.getDisplayName();
					else
						tmpText = iPrefManager.getUserServerName(text);
				}
			}
				
			MyriadSemiboldTextView textView = new MyriadSemiboldTextView(this);
			MyriadSemiboldTextView statusTextView = new MyriadSemiboldTextView(this);
			RelativeLayout relativeLayout = (RelativeLayout)getLayoutInflater().inflate(R.layout.shared_id_cell_info, null);
			ImageView profile = (ImageView) relativeLayout.findViewById(R.id.contact_icon);
			ImageView profile_default = (ImageView) relativeLayout.findViewById(R.id.contact_icon_default);
//			textView.setTag(text);
			relativeLayout.setTag(text);
			textView.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			statusTextView.setLayoutParams(LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 0, 0, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT));

			
			((TextView)relativeLayout.findViewById(R.id.id_contact_name)).setText(tmpText);
			if(text.equals(iPrefManager.getUserName())){
//				((RelativeLayout)relativeLayout.findViewById(R.id.id_right_rlayout)).setVisibility(View.GONE);
				((TextView)relativeLayout.findViewById(R.id.user_type)).setText("You");
			}
			else if(isAdminUser || isSharedIDDeactivated){
				((RelativeLayout)relativeLayout.findViewById(R.id.id_right_rlayout)).setVisibility(View.GONE);
			}else
				((RelativeLayout)relativeLayout.findViewById(R.id.id_right_rlayout)).setVisibility(View.VISIBLE);
			textView.setTextColor(Color.DKGRAY);
			textView.setTextSize(18);
			textView.setTextColor(getResources().getColor(R.color.darkest_gray));
			textView.setBottom(2);
			
			if(text.indexOf(":") != -1)
				text = text.substring(0, text.indexOf(":"));
			profile_default.setTag(text);
			setPic(profile, profile_default, tmpText, text);
			relativeLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(!user_name.endsWith(SharedPrefManager.getInstance().getUserName()) && !isAdminUser && !isSharedIDDeactivated)
						showConfirmationDialog(user_name);
				}
			});
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			params.setMargins(0,10,0,10);
			textView.setLayoutParams(params);
			relativeLayout.setTag(text);
			mainLayout.addView(relativeLayout);
			View v = new View(this);
			v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2));
			v.setBackgroundColor(Color.parseColor("#B3B3B3"));
			v.setTag(text+"line");
			mainLayout.addView(v);
		}
	}
	private void showPopupDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.shared_is_popup));
		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        // Do do my action here
		        dialog.dismiss();
		      //Need to send 

		    }

		});
//		builder.setNegativeButton(getString(R.string.no_not_now), new DialogInterface.OnClickListener() {
//
//		    @Override
//		    public void onClick(DialogInterface dialog, int which) {
//		        // I do not need any action here you might
//		        dialog.dismiss();
//		    }
//		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	private void showConfirmationDialog(final String user){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String name  = SharedPrefManager.getInstance().getUserServerName(user);
		builder.setMessage(getString(R.string.sharedid_remove_admin) + " " + name + "?");
		builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Do do my action here
				dialog.dismiss();
				//Need to send 
				ArrayList<String> removeList = new ArrayList<String>();
				removeList.add(user);
				new GroupMemberRemoveTaskOnServer(sharedID, SharedPrefManager.getInstance().getSharedIDDisplayName(sharedID), removeList).execute();
			}
			
		});
		builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// I do not need any action here you might
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	private void showDeactivateDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		final String name  = SharedPrefManager.getInstance().getUserServerName(user);
		builder.setMessage(getString(R.string.sharedid_deactivate));
		builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Do do my action here
				dialog.dismiss();
				new SharedIDDeactivationTaskOnServer(sharedID).execute();
			}
		});
		builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// I do not need any action here you might
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	protected void onResume() {
		super.onResume();
		onForeground = true;
		bindService(new Intent(this, ChatService.class), mMessageConnection,Context.BIND_AUTO_CREATE);
		//Update
		groupAdmins = HomeScreen.getAdminSetForSharedID(sharedID);
		//Add your self
		if(SharedPrefManager.getInstance().isDomainAdmin()){
			if(groupAdmins != null && !groupAdmins.contains(SharedPrefManager.getInstance().getUserName()))
				groupAdmins.add(0, SharedPrefManager.getInstance().getUserName());
		}else if(isAdminUser && groupAdmins != null && !groupAdmins.contains(HomeScreen.getSharedIDOwnerName(sharedID)))
			groupAdmins.add(0, HomeScreen.getSharedIDOwnerName(sharedID));
		String owner_name = HomeScreen.getSharedIDOwnerName(sharedID);
		if(SharedPrefManager.getInstance().getUserServerName(owner_name) != null 
				&& SharedPrefManager.getInstance().getUserServerName(owner_name).equals(owner_name))
			SharedPrefManager.getInstance().saveUserServerName(owner_name, HomeScreen.getSharedIDOwnerDisplayName(sharedID));
//		else if(isAdminUser){
//			if(groupAdmins != null && !groupAdmins.contains(SharedPrefManager.getInstance().getUserName()))
//				groupAdmins.add(0, SharedPrefManager.getInstance().getUserName());
//		}
		if(isEditMode && groupAdmins != null && groupAdmins.size() > 0){
			mainLayout.setVisibility(View.VISIBLE);
			mainLayout.removeAllViews();
			addMembersToView(mainLayout, groupAdmins);
		}
		if(isSharedIDDeactivated)
        	Toast.makeText(this, "This Official ID is deactivated!", Toast.LENGTH_SHORT).show();
	}
	protected void onPause() {
		try {
			unbindService(mMessageConnection);
		} catch (Exception e) {
			// Just ignore that
			Log.d("MessageHistoryScreen", "Unable to un bind");
		}
		super.onPause();
	}
	private void setProfilePic(ImageView view, String groupFileId){
//		String groupPicId = SharedPrefManager.getInstance().getUserFileId(sharedID); // 1_1_7_G_I_I3_e1zihzwn02
		String groupPicId = groupFileId;
		if(groupPicId!=null && !groupPicId.equals("") && !groupPicId.equals("clear")){
			String profilePicUrl = groupPicId;//AppConstants.media_get_url+
			if(!profilePicUrl.contains(".jpg"))
				profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+

			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			view.setTag(filename);
			File file1 = new File(filename);
			if(file1.exists()){
				view.setImageURI(Uri.parse(filename));
				view.setBackgroundDrawable(null);
			}
		} 
	}
	public void uploadPicture(final String imgPath) {
		String packetID = null;
		if(imgPath != null && imgPath.length() > 0)
		{
			try
			{
				String thumbImg = null;
//				if (messageService != null){
//					chatAdapter.setChatService(messageService);
//					thumbImg = MyBase64.encode(getByteArrayOfThumbnail(imgPath));
//					packetID = messageService.sendMediaMessage(senderName, "", imgPath,thumbImg,XMPPMessageType.atMeXmppMessageTypeGroupImage);
				picUploader = new ProfilePicUploader(this, null,true,notifyPhotoUploadHandler);
				picUploader.execute(imgPath, packetID,"",XMPPMessageType.atMeXmppMessageTypeGroupImage.name(),sharedID);
//				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
				System.out.println(""+ex.toString());
			}
		}
	}
	private final Handler notifyPhotoUploadHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
//	    	if(isForground)
	    		showDialog(getString(R.string.failed),getString(R.string.photo_upload_failed));
	    }
	};
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == HomeScreen.RESULT_OK){
				switch (requestCode) {
				case AppUtil.POSITION_CAMERA_PICTURE:
				case AppUtil.POSITION_GALLRY_PICTURE:
					if (data != null && data.getData() != null) 
					{
						Uri uri = data.getData();
						AppUtil.capturedPath1 = AppUtil.getPath(uri, this);
					}
					CompressImage compressImage = new CompressImage(this);
					AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
					performCrop(AppUtil.PIC_CROP);
					break;
				case AppUtil.PIC_CROP:
					String filePath= Environment.getExternalStorageDirectory()
							+"/"+Constants.contentTemp+"/"+AppUtil.TEMP_PHOTO_FILE;

					AppUtil.capturedPath1 = filePath ;
					 Bitmap selectedImage =  BitmapFactory.decodeFile(AppUtil.capturedPath1);
					 groupIconView.setImageBitmap(selectedImage);
					 groupIconView.setBackgroundDrawable(null);
					 groupIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//					groupIconView.setImageURI(Uri.parse(AppUtil.capturedPath1));
					uploadPicture(AppUtil.capturedPath1);
					break;
				}
			}else if (resultCode == 1001){
				finish();
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
//			System.out.println("----outputFileUri:" + outputFileUri);
			cropIntent.setDataAndType(outputFileUri, "image/*");
			cropIntent.putExtra("outputX", 600);
			cropIntent.putExtra("outputY", 600);
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
	public void onCancelClick(View view) {
				finish();
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_delete:
			showDeactivateDialog();
			break;
		case R.id.id_create_shared_id_layout:
			sharedIDName = groupNameView.getText().toString().trim();
//			sharedIDDescription = groupDiscriptionView.getText().toString();
			if(sharedIDName!=null && !sharedIDName.equals("")){
				Intent intent = new Intent(CreateSharedIDActivity.this, EsiaChatContactsScreen.class);
				intent.putExtra(Constants.CHAT_TYPE, Constants.GROUP_USER_CHAT_CREATE);
//				intent.putExtra(Constants.GROUP_UUID, sharedID);
				intent.putExtra(Constants.GROUP_DISCRIPTION, "");
				if(isEditMode){
					intent.putExtra(Constants.GROUP_NAME, sharedID);
					intent.putExtra(Constants.SHAREDID_UPDATE, true);
					groupAdmins = HomeScreen.getAdminSetForSharedID(sharedID);
					ArrayList<String> tmpList = new ArrayList<String>();
					for(String tmp: groupAdmins){
						if(tmp != null && !tmp.equals("")){
							if(tmp.indexOf(':') != -1)
								tmpList.add(tmp.split(":")[0]);
							else
								tmpList.add(tmp);
						}
					}
					intent.putStringArrayListExtra(Constants.GROUP_USERS, new ArrayList<String>(tmpList));
				}
				else{
					intent.putExtra(Constants.GROUP_NAME, sharedIDName);
					intent.putExtra(Constants.SHARED_ID, true);
				}
				if(picUploader!=null && picUploader.getServerFileId() != null)
					intent.putExtra(Constants.GROUP_FILE_ID, picUploader.getServerFileId());
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
				startActivityForResult(intent, 1001);
			}
			break;
		case R.id.id_group_icon:
			String tmpImgId1 = null;
			 if(picUploader!=null && picUploader.getServerFileId()!=null)
				 tmpImgId1 = picUploader.getServerFileId();
			if(picChooserDialog!=null && !picChooserDialog.isShowing() && tmpImgId1==null)
				picChooserDialog.show();
			else if(AppUtil.capturedPath1!=null){
				
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				File file = new File(AppUtil.capturedPath1);
				Uri outputFileUri = Uri.fromFile(file);
				intent.setDataAndType(outputFileUri, "image/*");
				startActivity(intent);
			}
				
			break;
		case R.id.id_back:
		case R.id.id_cancel:
			finish();
			break;
//		case R.id.id_add_broadcast_member:
		case R.id.id_next:
			sharedIDName = groupNameView.getText().toString().trim();
			if(sharedIDName == null || (sharedIDName != null && sharedIDName.trim().equals(""))){
				Toast.makeText(CreateSharedIDActivity.this, "Please enter title for Official ID!", Toast.LENGTH_SHORT).show();
				return;
			}
			if(groupDiscriptionView != null)
				sharedIDDescription = groupDiscriptionView.getText().toString();
			else
				sharedIDDescription = "";
			 if(sharedIDName != null && !sharedIDName.equals("")){
				String tmpImgId = null;
				if(picUploader!=null && picUploader.getServerFileId()!=null)
					tmpImgId = picUploader.getServerFileId();
				new GroupAndBroadcastTaskOnServer(sharedID, sharedIDName, null,sharedIDDescription,Constants.BROADCAST_LIST_UPDATE).execute(tmpImgId);
			}else{
				showDialog(getString(R.string.group_name_shouldnt_null),getString(R.string.ok));
			}
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
		case R.id.id_group_camera_icon:
			if(picChooserDialog!=null && !picChooserDialog.isShowing())
				picChooserDialog.show();
			break;
		}
	}
	public void showDialog(String s,String btnTxt) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		TextView btn = ((TextView)bteldialog.findViewById(R.id.id_ok));
		btn.setText(btnTxt);
		btn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				return false;
			}
		});
		bteldialog.show();
	}
	private class GroupAndBroadcastTaskOnServer extends AsyncTask<String, String, String> {
		String sharedID;
		String displayName;
		List<String> usersList;
		ProgressDialog progressDialog = null;
		String sharedIDDescription;
		int requestType = -1;
		String[] urlss = null;
		public GroupAndBroadcastTaskOnServer(String sharedID,String displayName,List<String> usersList, String sharedIDDescription,int type){
			this.sharedID =sharedID;
			this.displayName = displayName;
			this.usersList = usersList;
			this.sharedIDDescription =sharedIDDescription;
			this.requestType = type;
		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(CreateSharedIDActivity.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			GroupChatServerModel model = new GroupChatServerModel();

			model.setUserName(iPrefManager.getUserName());
			urlss = urls;
			
			model.setDisplayName(displayName);
			if(usersList!=null && !usersList.isEmpty() && requestType != Constants.GROUP_USER_CHAT_CREATE)
				model.setMemberUserSet(usersList);
			if(sharedIDDescription!=null && !sharedIDDescription.equals(""))
				model.setDescription(sharedIDDescription);
			String urlInfo = "update";
			switch(requestType){
			case Constants.GROUP_USER_CHAT_CREATE:
				model.setDomainName(iPrefManager.getUserDomain());
				model.setDescription(sharedIDDescription);
				if(urls!=null && urls[0]!=null && !urls[0].equals(""))
					model.setFileId(urls[0]);
				// this will be unique id
				
//				model.setGroupName(displayName.replace(' ', '_') + "_" + iPrefManager.getUserId() + "_" +System.currentTimeMillis());
				urlInfo = "/tiger/rest/group/create";
				break;
			case Constants.GROUP_USER_CHAT_INVITE:
				model.setGroupName(sharedID);
				if(urls!=null && urls[0]!=null && !urls[0].equals(""))
					model.setFileId(urls[0]);
				urlInfo = "/tiger/rest/group/update";
				break;
			case Constants.BROADCAST_LIST_CRATE:
				model.setDomainName(iPrefManager.getUserDomain());
				if(urls!=null && urls[0]!=null && !urls[0].equals(""))
					model.setFileId(urls[0]);
//				model.setBroadcastGroupName(sharedID);
//				model.setBroadcastGroupName(displayName.replace(' ', '_') + "_" + iPrefManager.getUserId() + "_" +System.currentTimeMillis());
				model.setBroadcastGroupName(displayName + "_" + iPrefManager.getUserId() + "_" +System.currentTimeMillis());
				model.setDescription(sharedIDDescription);
				urlInfo = "/tiger/rest/bcgroup/create";
				break;
			case Constants.BROADCAST_LIST_UPDATE:
				model.setBroadcastGroupName(sharedID);
				if(urls!=null && urls[0]!=null && !urls[0].equals("")){
					model.setFileId(urls[0]);
					sharedIDFileID = urls[0];
				}
				urlInfo = "/tiger/rest/bcgroup/update";
				break;
			}
//			if(isForCreateGroup){
//				model.setDomainName(iPrefManager.getUserDomain());
//				model.setDescription(sharedIDDescription);
//				if(urls!=null && urls[0]!=null && !urls[0].equals(""))
//					model.setFileId(urls[0]);
//			}
//				List<String> adminUserSet = new ArrayList<String>();
//				model.setAdminUserSet(adminUserSet);
			String JSONstring = new Gson().toJson(model);

			DefaultHttpClient client1 = new DefaultHttpClient();
			String line = "";
			String responseMsg = "";
			Log.d(TAG, "serverUpdateCreateGroupInfo request: "+JSONstring);
			
//			if(isForCreateGroup)
//				urlInfo = "create";
			
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ urlInfo);
			httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			HttpResponse response = null;
			try {
				httpPost.setEntity(new StringEntity(JSONstring));
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

			} catch (UnsupportedEncodingException e1) {
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:"+e1.toString());
			}catch(Exception e){
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
			}else if(requestType == Constants.BROADCAST_LIST_UPDATE){
				JSONObject jsonobj;
				SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
				try {
					jsonobj = new JSONObject(response);
					if(sharedIDName != null)
						SharedPrefManager.getInstance().saveSharedIDDisplayName(sharedID, sharedIDName);
					if(sharedIDFileID != null)
						SharedPrefManager.getInstance().saveSharedIDFileId(sharedID, sharedIDFileID);
					JSONObject finalJSONbject = new JSONObject();
					String number = iPrefManager.getUserPhone();
					if(number.contains("-"))
						number = number.substring(number.indexOf('-') + 1);
					try {
						//For User Info
						finalJSONbject.put("userName", iPrefManager.getUserName());
						finalJSONbject.put("displayname", iPrefManager.getDisplayName());
						if(iPrefManager.getUserFileId(iPrefManager.getUserName()) != null)
							finalJSONbject.put("fileId", iPrefManager.getUserFileId(iPrefManager.getUserName()));
						//For Shared ID Info
						finalJSONbject.put("sharedIDName", sharedID);
						finalJSONbject.put("sharedIDDisplayName", sharedIDName);
						if(sharedIDFileID != null)
							finalJSONbject.put("sharedIDFileID", sharedIDFileID);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String json = finalJSONbject.toString();
					Log.i(TAG, "Final JSON :  " + json);
//					json = json.replace("\"", "&quot;");
					if(messageService != null)
						messageService.sendSpecialMessageToAllDomainMembers(iPrefManager.getUserDomain() + "-system", json, XMPPMessageType.atMeXmppMessageTypeSharedIDUpdated);
					json = null;
					
					if (jsonobj != null && jsonobj.getString("status") != null && jsonobj.getString("status").equalsIgnoreCase("success")){
						 Toast.makeText(CreateSharedIDActivity.this, jsonobj.getString("message"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HomeScreen.refreshContactList = true;
				finish();
			}else{
				//Create Json here for group update info.
				 JSONObject finalJSONbject = new JSONObject();
				 try {
					finalJSONbject.put("displayName", displayName);
					if(sharedIDDescription != null)
						finalJSONbject.put("description", sharedIDDescription);
					if(urlss!=null && urlss[0]!=null && !urlss[0].equals(""))
						finalJSONbject.put("fileId", urlss[0]);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 
				 if(messageService!=null){
					 String json = finalJSONbject.toString();
//					 json = json.replace("\"", "&quot;");
					messageService.updateGroupDisplayName(sharedID,displayName, json);
					json = null;
				 }
				 if(sharedIDDescription != null)
					 SharedPrefManager.getInstance().saveUserStatusMessage(sharedID, sharedIDDescription);
				 if(urlss!=null && urlss[0]!=null && !urlss[0].equals(""))
					 SharedPrefManager.getInstance().saveUserFileId(sharedID, urlss[0]);
				 if(requestType == Constants.BROADCAST_LIST_UPDATE)
					 SharedPrefManager.getInstance().saveBroadCastDisplayName(sharedID, displayName);
				 else
					 SharedPrefManager.getInstance().saveGroupDisplayName(sharedID, displayName);
				 if(requestType == Constants.BROADCAST_LIST_UPDATE){
					 messageService.saveInfoMessage(displayName, sharedID, "You updated broadcast info.",UUID.randomUUID().toString());
				 }
				setResult(RESULT_OK, new Intent(CreateSharedIDActivity.this, GroupProfileScreen.class));
				finish();
			}
			super.onPostExecute(response);
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
				return false;
			}
		});
		bteldialog.show();
	}
//----------------------------------------------------------
	private void setPic(ImageView view, ImageView view_default, String displayName, String id){
		Log.i(TAG, "User ID : "+id);
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(id); 
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		if (bitmap != null) {
			if(view_default != null)
				view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			view.setImageBitmap(bitmap);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto+profilePicUrl;
			view.setTag(filename);
		}else if(groupPicId!=null && groupPicId.trim().length() > 0 && !groupPicId.equals("clear")){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			//			if(groupPicId != null && groupPicId.length() > 0 && groupPicId.lastIndexOf('/')!=-1)
			//				profilePicUrl += groupPicId.substring(groupPicId.lastIndexOf('/'));

			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			File file1 = new File(filename);
			//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				if(view_default != null)
					view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				setThumb(view, filename,groupPicId);
				view.setTag(filename);
			}else{
				//Downloading the file
//				view_default.setVisibility(View.INVISIBLE);
//				view.setVisibility(View.VISIBLE);
////				view.setImageResource(R.drawable.group_icon);
//				(new ProfilePicDownloader()).download(Constants.media_get_url+groupPicId+".jpg", (RoundedImageView)view, null);
				
				try{
					String name_alpha = String.valueOf(displayName.charAt(0));
					if(displayName.contains(" ") && displayName.indexOf(' ') < (displayName.length() - 1))
						name_alpha +=  displayName.substring(displayName.indexOf(' ') + 1).charAt(0);
					TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(displayName));
					view.setVisibility(View.INVISIBLE);
					view_default.setVisibility(View.VISIBLE);
					view_default.setImageDrawable(drawable);
					view_default.setBackgroundColor(Color.TRANSPARENT);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader((RoundedImageView)view,view_default).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
	             else
	            	 new BitmapDownloader((RoundedImageView)view,view_default).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			}
		}else{
			try{
				if(displayName != null && !displayName.equals("")){
					String name_alpha = String.valueOf(displayName.charAt(0)).toUpperCase();
					if(displayName.contains(" ") && displayName.indexOf(' ') < (displayName.length() - 1))
						name_alpha +=  displayName.substring(displayName.indexOf(' ') + 1).charAt(0);
					TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(displayName));
					view.setVisibility(View.INVISIBLE);
					view_default.setVisibility(View.VISIBLE);
					view_default.setImageDrawable(drawable);
					view_default.setBackgroundColor(Color.TRANSPARENT);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	private void setPic(ImageView view, String id){
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(id); // 1_1_7_G_I_I3_e1zihzwn02
//		android.graphics.Bitmap bitmap = null;//SuperChatApplication.getBitmapFromMemCache(groupPicId);
//		if (bitmap != null) {
//			view.setVisibility(View.VISIBLE);
//			view.setImageBitmap(bitmap);
//			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
//			File file = Environment.getExternalStorageDirectory();
//			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
//			view.setTag(filename);
//		}else 
			if(groupPicId!=null && groupPicId.trim().length() > 0){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			//			if(groupPicId != null && groupPicId.length() > 0 && groupPicId.lastIndexOf('/')!=-1)
			//				profilePicUrl += groupPicId.substring(groupPicId.lastIndexOf('/'));

			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			File file1 = new File(filename);
			//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				view.setVisibility(View.VISIBLE);
//				setThumb(view, filename,groupPicId);
				try{
					view.setImageURI(Uri.parse(filename));
		    	}catch(Exception e){
		    		
		    	}
				view.setTag(filename);
			}else{
				//Downloading the file
				view.setVisibility(View.VISIBLE);
//				view.setImageResource(R.drawable.group_icon);
//				(new ProfilePicDownloader()).download(Constants.media_get_url+groupPicId+".jpg", (RoundedImageView)view, null);
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader(this,view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId);
	             else
	            	 new BitmapDownloader(this,view).execute(groupPicId);
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
	private void setThumb(ImageView imageViewl,String path,String groupPicId){
		BitmapFactory.Options bfo = new BitmapFactory.Options();
	    bfo.inSampleSize = 2;
	    Bitmap bm = null;
	    try{
		    bm = BitmapFactory.decodeFile(path, bfo);
		    bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
		    bm = rotateImage(path, bm);
		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
	    }catch(Exception ex){
	    	
	    }
	    if(bm!=null){
	    	imageViewl.setImageBitmap(bm);
	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
	    } else{
	    	try{
	    		imageViewl.setImageURI(Uri.parse(path));
	    	}catch(Exception e){
	    		
	    	}
	    }
	}
//============================================================================
	private class SharedIDDeactivationTaskOnServer extends AsyncTask<String, String, String> {
		String groupUUID;
		ProgressDialog progressDialog = null;
		public SharedIDDeactivationTaskOnServer(String groupUUID){
			this.groupUUID = groupUUID;
		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(CreateSharedIDActivity.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			
			DefaultHttpClient client1 = new DefaultHttpClient();
			String line = "";
			String responseMsg = "";
			String urlInfo = "";
			String query = groupUUID;
			try {
				 query = URLEncoder.encode(query, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			urlInfo = "/tiger/rest/bcgroup/deactivate?groupName="+query;
			
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ urlInfo);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
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
							Log.d(TAG, "SharedIDDeactivationTaskOnServer response: "+line);
						}
					}
				} catch (ClientProtocolException e) {
					Log.d(TAG, "SharedIDDeactivationTaskOnServer during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					Log.d(TAG, "SharedIDDeactivationTaskOnServer during HttpPost execution ClientProtocolException:"+e.toString());
				}
			} catch(Exception e){
				Log.d(TAG, "SharedIDDeactivationTaskOnServer during HttpPost execution Exception:"+e.toString());
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
				if (response!=null && !response.equals("")){
					if(messageService != null){
						SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//						SharedPrefManager.getInstance().saveGroupInfo(groupUUID, SharedPrefManager.GROUP_ACTIVE_INFO, false);
						//Update information to Other domain members for the update.
						//Create JSON here for group update info.
						JSONObject finalJSONbject = new JSONObject();
						String number = iPrefManager.getUserPhone();
						if(number.contains("-"))
							number = number.substring(number.indexOf('-') + 1);
						try {
							//For User Info
							finalJSONbject.put("userName", iPrefManager.getUserName());
							finalJSONbject.put("displayname", iPrefManager.getDisplayName());
							if(iPrefManager.getUserFileId(iPrefManager.getUserName()) != null)
								finalJSONbject.put("fileId", iPrefManager.getUserFileId(iPrefManager.getUserName()));
							//For Shared ID Info
							finalJSONbject.put("sharedIDName", sharedID);
							finalJSONbject.put("sharedIDDisplayName", iPrefManager.getSharedIDDisplayName(sharedID));
							finalJSONbject.put("deactivated", true);
							if(iPrefManager.getSharedIDFileId(sharedID) != null)
								finalJSONbject.put("sharedIDFileID", iPrefManager.getSharedIDFileId(sharedID));
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						String json = finalJSONbject.toString();
						Log.i(TAG, "Final JSON :  " + json);
//						json = json.replace("\"", "&quot;");
						if(messageService != null)
							messageService.sendSpecialMessageToAllDomainMembers(iPrefManager.getUserDomain() + "-system", json, XMPPMessageType.atMeXmppMessageTypeSharedIDDeleted);
						json = null;
						iPrefManager.setSharedIDDeactivated(sharedID, true);
						HomeScreen.removeSharedID(sharedID);
						DBWrapper.getInstance().deleteContact(sharedID);
					}
				}
				HomeScreen.refreshContactList = true;
				finish();
				super.onPostExecute(response);
			}
		}
	}
//============================================================================
	private class GroupMemberRemoveTaskOnServer extends AsyncTask<String, String, String> {
		String groupUUID;
		String displayName;
		List<String> userList;
		ProgressDialog progressDialog = null;
		public GroupMemberRemoveTaskOnServer(String groupUUID,String displayName,List<String> usersList){
			this.groupUUID =groupUUID;
			this.displayName = displayName;
			this.userList = usersList;


		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(CreateSharedIDActivity.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			GroupChatServerModel model = new GroupChatServerModel();
			model.setUserName(iPrefManager.getUserName());
			model.setBroadcastGroupName(groupUUID);
			model.setDisplayName(displayName);
			model.setRemoveUserSet(userList);
			String JSONstring = new Gson().toJson(model);
			DefaultHttpClient client1 = new DefaultHttpClient();
			String line = "";
			String responseMsg = "";
			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			String urlInfo = "/tiger/rest/bcgroup/update";
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ urlInfo);
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
			HttpResponse response = null;
			try {
				httpPost.setEntity(new StringEntity(JSONstring));
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
				if (response != null){
					String user = userList.get(0);
					HomeScreen.removeAdminFromSharedID(groupUUID, user);
					mainLayout.removeAllViews();
					addMembersToView(mainLayout, HomeScreen.getAdminSetForSharedID(groupUUID));

					SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//					SharedPrefManager.getInstance().saveGroupInfo(groupUUID, SharedPrefManager.GROUP_ACTIVE_INFO, false);
					//Update information to Other domain members for the update.
					//Create JSON here for group update info.
					JSONObject finalJSONbject = new JSONObject();
					String number = iPrefManager.getUserPhone();
					if(number.contains("-"))
						number = number.substring(number.indexOf('-') + 1);
					try {
						//For User Info
						finalJSONbject.put("userName", iPrefManager.getUserName());
						finalJSONbject.put("displayname", iPrefManager.getDisplayName());
						if(iPrefManager.getUserFileId(iPrefManager.getUserName()) != null)
							finalJSONbject.put("fileId", iPrefManager.getUserFileId(iPrefManager.getUserName()));
						//For Shared ID Info
						finalJSONbject.put("sharedIDName", sharedID);
						finalJSONbject.put("sharedIDDisplayName", iPrefManager.getSharedIDDisplayName(sharedID));
						finalJSONbject.put("adminuser", user);
						if(iPrefManager.getSharedIDFileId(sharedID) != null)
							finalJSONbject.put("sharedIDFileID", iPrefManager.getSharedIDFileId(sharedID));
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String json = finalJSONbject.toString();
					Log.i(TAG, "Final JSON :  " + json);
//					json = json.replace("\"", "&quot;");
					if(messageService != null)
						messageService.sendSpecialMessageToAllDomainMembers(iPrefManager.getUserDomain() + "-system", json, XMPPMessageType.atMeXmppMessageTypeSharedIDAdminRemoved);
					json = null;
				
				}
			super.onPostExecute(response);
		}
	}
}
//===============================================
//	public void showPopup(View v){
//		SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//		 PopupMenu popup = new PopupMenu(this, v);
//		 popup.setOnMenuItemClickListener(this);
//		 if (iPrefManager.isDomainAdmin())
//			 popup.getMenu().add(0,0,0,getResources().getString(R.string.delete_channel));
//		 popup.show();
//	}
//	@Override
//	public boolean onMenuItemClick(MenuItem item) {
//		// TODO Auto-generated method stub
//		switch (item.getItemId()) {
//		case 0:
//			showDeactivateDialog();
//			return true;
//	  }
//	return false;
//	}
}
