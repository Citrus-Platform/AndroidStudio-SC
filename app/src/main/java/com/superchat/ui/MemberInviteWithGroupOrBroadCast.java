package com.superchat.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.AddMemberModel;
import com.superchat.model.AddMemberResponseModel;
import com.superchat.model.ErrorModel;
import com.superchat.model.GroupChatServerModel;
import com.superchat.utils.Constants;
import com.superchat.utils.GroupCreateTaskOnServer;
import com.superchat.utils.Log;
import com.superchat.utils.MyBase64;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.MyriadSemiboldTextView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MemberInviteWithGroupOrBroadCast extends Activity implements OnClickListener{
	public final static String TAG = "MemberInviteWithGroupOrBroadCast"; 
	private String groupUUID;
	private String selectedMessageId;
	private String displayName = "";
	private String statusMessage = "";
	private String selectedMemberUserName;
	private String selectedUserdisplayName;
	private List<String> usersList;
	private TextView backTitle;
	private Calendar calander;
	Calendar currentCalender;
	private TextView addNewView;
	private SharedPrefManager iChatPref;
	private Dialog memberOptionDialog; 
	private TextView groupTab;
	CheckBox adminCheckBox;
	private TextView broadcastTab;
	boolean isNewGroupAdding = false;
	AutoCompleteTextView newGroupOrBroadName;
	private HashMap<String, String> hashMap;
	private HashMap<String, String> newGroupsList = new HashMap<String, String>();
	private HashMap<String, String> newBroadcastsList = new HashMap<String, String>();
	boolean isProfileModified;
	private ChatService service;
	private ChatService messageService;
	boolean invitationEnable;
	ArrayList<String> inviters = null;
	LinearLayout mainLayout;
	private boolean isBroadCast;
	ProgressBar progressBarView;
	SharedPrefManager prefManager;
	AddMemberModel requestModel;
	private ServiceConnection mMessageConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			messageService = ((ChatService.MyBinder) binder).getService();
			Log.d("Service", "Connected");
			if (messageService != null && invitationEnable && inviters!=null) {
				invitationEnable = false;
				try {
					
					String displayName = SharedPrefManager.getInstance().getGroupDisplayName(groupUUID);
					String description = iChatPref.getUsersOfGroup(groupUUID);
					String infoList = "";
					for (String inviter : inviters) {
						if (inviter != null && !inviter.equals("")){
							messageService.inviteUserInRoom(groupUUID,displayName, description, inviter, null, false);
							infoList +=  inviter+",";
						}
					}
					if(!infoList.equals("")){
						if(usersList!=null && !usersList.isEmpty()){
							for(String element:usersList)
								infoList +=  element+",";
						}
						if(infoList.endsWith(","))
							infoList = infoList.substring(0, infoList.length()-1);
						boolean isSuccessed = messageService.sendInfoMessage(groupUUID,infoList,Message.XMPPMessageType.atMeXmppMessageTypeMemberList);
						if(isSuccessed){
							handler.sendEmptyMessage(200);
							int state = iChatPref.getServerGroupState(groupUUID);
							if(state!=GroupCreateTaskOnServer.SERVER_GROUP_NOT_CREATED && state!=GroupCreateTaskOnServer.SERVER_GROUP_CREATION_FAILED){
								iChatPref.saveServerGroupState(groupUUID, GroupCreateTaskOnServer.SERVER_GROUP_NOT_UPDATED);
							 new GroupCreateTaskOnServer(groupUUID, displayName,usersList).execute(GroupCreateTaskOnServer.UPDATE_GROUP_REQUEST); 
							}
							try {
								unbindService(mMessageConnection);
							} catch (Exception e) {
								// Just ignore that
								Log.d("MessageHistoryScreen", "Unable to un bind");
							}
						}
					}
					getIntent().getExtras().putString("INVITATION_LIST", "");
				} catch (Exception e) {
					Log.d(TAG, "users are not able to joined.");
				}
				
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			messageService = null;
		}
	};
	Handler handler = new Handler(){
		@Override
	public void handleMessage(android.os.Message msg) {
		// TODO Auto-generated method stub
		super.handleMessage(msg);
		switch(msg.what){
		case 200:
			
			if(inviters!=null && !inviters.isEmpty()){
				ArrayList<String> tmp = new ArrayList<String>();
				for(String inviter:inviters){
					String name = DBWrapper.getInstance(SuperChatApplication.context).getChatName(inviter);
					if(name!=null && name.contains("#786#"))
			        	name = name.substring(0, name.indexOf("#786#"));
					tmp.add(name);
					usersList.add(inviter);
				}
				addTextView(mainLayout, tmp);
			}
			break;
		}
	}};
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.member_invite_in_grupnbrad);

		UtilSetFont.setFontMainScreen(this);

		prefManager = SharedPrefManager.getInstance();
		calander = Calendar.getInstance(TimeZone.getDefault());
		currentCalender = Calendar.getInstance(TimeZone.getDefault());
		currentCalender.setTimeInMillis(System.currentTimeMillis());
		progressBarView = (ProgressBar)findViewById(R.id.id_loading);
		isProfileModified = false;
		backTitle = (TextView)findViewById(R.id.id_back_title);
		addNewView = (TextView)findViewById(R.id.id_add_new);
		((TextView)findViewById(R.id.id_done)).setOnClickListener(this);
		
		
		
		addNewView.setOnClickListener(this);
		
		iChatPref = SharedPrefManager.getInstance();

		Bundle tmpBundle = getIntent().getExtras();
		if(tmpBundle!=null){
			 requestModel = (AddMemberModel)tmpBundle.getSerializable(AddMemberModel.TAG);
			if(requestModel!=null)
				Log.d(TAG, "Person info is "+requestModel.domainName+" , "+requestModel.directoryUserSet.get(0).mobileNumber);
			else
				Log.d(TAG, "Person info is null.");
			isBroadCast = tmpBundle.getBoolean(Constants.BROADCAST, false);
			selectedMessageId = tmpBundle.getString(Constants.SELECTED_MESSAGE_ID,null);
			usersList = getIntent().getExtras().getStringArrayList(Constants.GROUP_USERS);
			
			if(groupUUID!=null)
				statusMessage = iChatPref.getUserStatusMessage(groupUUID);
			mainLayout = (LinearLayout)findViewById(R.id.id_group_members);

//			hashMap = (HashMap<String, String>)getIntent().getSerializableExtra(Constants.USER_MAP);
			hashMap = new HashMap<String, String>();
           
			if(isBroadCast){
				
//				title.setText(getString(R.string.broadcast_info));
				backTitle.setText(getString(R.string.broadcast_chat));
			}

		}
//		if(displayName.contains("##$^##"))
//			displayNameView.setText(displayName.substring(0, displayName.indexOf("##$^##")));
//    	else
//    		displayNameView.setText(displayName);
		memberOptionDialog = new Dialog(this);
		memberOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		memberOptionDialog.setContentView(R.layout.add_group_broadcast_dialog);
//		Window window = memberOptionDialog.getWindow();
//		WindowManager.LayoutParams wlp = window.getAttributes();
//
//		wlp.gravity = Gravity.BOTTOM;
//		wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//		window.setAttributes(wlp);

		
		adminCheckBox = (CheckBox)memberOptionDialog.findViewById(R.id.id_admin_check);
		groupTab = (TextView)memberOptionDialog.findViewById(R.id.id_new_group);
		groupTab.setOnClickListener(this);
		broadcastTab = (TextView)memberOptionDialog.findViewById(R.id.id_new_broad);
		broadcastTab.setOnClickListener(this);
		newGroupOrBroadName = (AutoCompleteTextView)memberOptionDialog.findViewById(R.id.id_name);
		memberOptionDialog.findViewById(R.id.id_add).setOnClickListener(this);
		memberOptionDialog.findViewById(R.id.id_cancel).setOnClickListener(this);
		
//		newGroupsList.put("M4Mast", "a:M4Mast");
//		newGroupsList.put("MyGod", "MyGod");
//		
//		newBroadcastsList.put("Gautum_broad", "Gautum_broad");
//		newBroadcastsList.put("NewsBroad", "NewsBroad");
		
		addTextView(mainLayout, null);
	}



	private List<String> convertNames(List<String> arrayList){
		ArrayList<String> displayList = new ArrayList<String>();
		hashMap = DBWrapper.getInstance().getUsersDisplayNameList(arrayList);
		for(String tmp:arrayList){
			String value = tmp;//DBWrapper.getInstance().getChatName(tmp);
			if(iChatPref.getUserName().equals(tmp))
				value = "You";
			if(value!=null && value.contains("#786#")){
//				String tUserName = value.substring(value.indexOf("#786#")+"#786#".length());
				value = value.substring(0, value.indexOf("#786#"));
//				hashMap.put(tUserName, value);
			}
			displayList.add(value);
		}
		Collections.sort(displayList,String.CASE_INSENSITIVE_ORDER);
		return displayList;
	}
	@Override
	protected void onResume() {
		super.onResume();
//		bindService(new Intent(this, ChatService.class), mConnection, Context.BIND_AUTO_CREATE);

	}
	protected void onPause() {
//		unbindService(mConnection);
		super.onPause();
	}
	public static int daysBetween(Calendar startDate, Calendar endDate) {  
	    return Math.abs(startDate.get(Calendar.DAY_OF_MONTH)-endDate.get(Calendar.DAY_OF_MONTH));  
	} 
	private Bitmap createThumbFromByteArray(String baseData) {
		Bitmap bmp = null;
		byte[] data = MyBase64.decode(baseData);
		if (data != null)
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		return bmp;
	}
	private void addTextView(LinearLayout mainLayout, List<String> list){
			
		// existing Groups  *********************************************************************
		
		MyriadSemiboldTextView textView = new MyriadSemiboldTextView(this);
		LinearLayout.LayoutParams params = new   LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 0, 0, 0);
		textView.setLayoutParams(params);
			textView.setText("Groups");
//		textView.setBackgroundResource(R.drawable.round_rect);
		textView.setBackgroundColor(Color.DKGRAY);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(19);
		textView.setPadding(10, 5, 0, 5);
		textView.setBottom(2);
//		mainLayout.addView(textView);

		CheckBox checkBoxView = null;
//		THREE PLACES COMMENTED 
//		for(String text:prefManager.getGroupNamesArray()){
//			String tmpText = prefManager.getGroupDisplayName(text);
//			if(tmpText==null || tmpText.trim().equals(""))
//				continue;
////			textView = new MyriadSemiboldTextView(this);
//			 checkBoxView = new CheckBox(this);
//			 GroupTypeInfo groupTypeInfo = new GroupTypeInfo();
//			 groupTypeInfo.setGroupDisplayName(tmpText);
//			 groupTypeInfo.setgroupRequestName(tmpText);
//			 groupTypeInfo.setAsGroup(true);
//			 checkBoxView.setTag(groupTypeInfo);
//			 if(list!=null && list.contains(tmpText)){
//				 checkBoxView.setChecked(true); 
//			 }
////			params.setMargins(0, 2, 0, 0);
////			textView.setPadding(10, 2, 0, 2);
//			checkBoxView.setPadding(10, 2, 0, 2);
//			checkBoxView.setBackgroundColor(Color.WHITE);
////			textView.setLayoutParams(params);
//			checkBoxView.setLayoutParams(params);
//			checkBoxView.setText(tmpText);
////			checkBoxView.setLayoutParams(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
//					//(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
////					String messageStatus = text;
////					statusText = messageStatus;
//					
//				
//			
//			
//				
////			textView.setText(tmpText);
//////			textView.setBackgroundResource(R.drawable.round_rect);
////			textView.setBackgroundColor(Color.WHITE);
////			textView.setTextColor(Color.DKGRAY);
////			textView.setTextSize(17);
////			textView.setBottom(2);
////			if(statusText!=null){
////				statusTextView.setText(statusText);
////				statusTextView.setBackgroundColor(Color.WHITE);
////				statusTextView.setTextColor(Color.GRAY);
////				statusTextView.setPadding(10, 0, 0, 0);
////				statusTextView.setTextSize(10);
////			}
//			textView.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if (memberOptionDialog != null && !memberOptionDialog.isShowing()) {
//						selectedMemberUserName = ((TextView)v).getTag().toString();
//						selectedUserdisplayName =  ((TextView)v).getText().toString();
//						if(!selectedMemberUserName.equals(iChatPref.getUserName()))
//							memberOptionDialog.show();
//					}
//				}
//			});
//			mainLayout.addView(checkBoxView);
//		}
		
		// new Groups  *********************************************************************
		
		
		textView = new MyriadSemiboldTextView(this);
		textView.setLayoutParams(params);
		textView.setText("New Groups");
//	textView.setBackgroundResource(R.drawable.round_rect);
	textView.setBackgroundColor(Color.DKGRAY);
	textView.setTextColor(Color.WHITE);
	textView.setTextSize(19);
	textView.setPadding(0, 5, 0, 5);
	textView.setBottom(2);
//	mainLayout.addView(textView);
	LinearLayout horizontalLayout = new LinearLayout(this);
	horizontalLayout.setBackgroundColor(Color.DKGRAY);
	LinearLayout.LayoutParams horizontalLayoutParams = new   LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	horizontalLayoutParams.setMargins(10, 0, 0, 0);
	horizontalLayout.setLayoutParams(horizontalLayoutParams);
	textView.setLayoutParams(horizontalLayoutParams);
	horizontalLayout.addView(textView);
	
	ImageView addView = new ImageView(this);
	addView.setPadding(10, 5, 0, 5);
	addView.setBackgroundResource(R.drawable.media_attach_icon);
	addView.setLayoutParams(horizontalLayoutParams);
	horizontalLayout.setPadding(10, 5, 0, 5);
	horizontalLayout.addView(addView);
	mainLayout.addView(horizontalLayout,params);
	addView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (memberOptionDialog != null && !memberOptionDialog.isShowing()) {
				isNewGroupAdding = true;
				//groupTab.setBackgroundResource(R.drawable.button_background_green_midle_round);
				//broadcastTab.setBackgroundResource(R.drawable.button_background_white_midle_round);
				groupTab.setVisibility(View.VISIBLE);
				broadcastTab.setVisibility(View.INVISIBLE);
				newGroupOrBroadName.setHint(getString(R.string.type_group_name));
				newGroupOrBroadName.setText("");
				adminCheckBox.setChecked(false);
				memberOptionDialog.show();
				
				
		}
		}
	});
	for(String text:newGroupsList.keySet()){
		String tmpText = text;
		textView = new MyriadSemiboldTextView(this);
		checkBoxView = new CheckBox(this);
		checkBoxView.setTag(tmpText);
		GroupTypeInfo groupTypeInfo = new GroupTypeInfo();
		 groupTypeInfo.setGroupDisplayName(tmpText);
		 groupTypeInfo.setgroupRequestName(tmpText);
		 groupTypeInfo.setAsGroup(true);
		 checkBoxView.setTag(groupTypeInfo);
		 if(list!=null && list.contains(tmpText)){
			 checkBoxView.setChecked(true); 
		 }
		textView.setPadding(10, 2, 0, 0);
		params.setMargins(0, 2, 0, 0);
		checkBoxView.setPadding(10, 2, 0, 0);
		textView.setLayoutParams(params);
		checkBoxView.setLayoutParams(params);
		
		checkBoxView.setText(tmpText);
		//(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
		 
		textView.setText(tmpText);
		checkBoxView.setText(tmpText);
		checkBoxView.setBackgroundColor(Color.WHITE);
//		textView.setBackgroundResource(R.drawable.round_rect);
		textView.setBackgroundColor(Color.WHITE);
		textView.setTextColor(Color.DKGRAY);
		textView.setPadding(10, 0, 0, 0);
		textView.setTextSize(19);
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (memberOptionDialog != null && !memberOptionDialog.isShowing()) {
					selectedMemberUserName = ((TextView)v).getTag().toString();
					selectedUserdisplayName =  ((TextView)v).getText().toString();
					if(!selectedMemberUserName.equals(iChatPref.getUserName()))
						memberOptionDialog.show();
				}
			}
		});
		mainLayout.addView(checkBoxView);
	}
	
	// existing broadcasts  *********************************************************************
	
//		textView = new MyriadSemiboldTextView(this);
//		params.setMargins(0, 20, 0, 10);
//		textView.setLayoutParams(params);
//		textView.setTextSize(19);
//		textView.setPadding(10, 5, 0, 5);
//		textView.setText("Broadcast Lists");
////		textView.setBackgroundResource(R.drawable.round_rect);
//		textView.setBackgroundColor(Color.DKGRAY);
//		textView.setTextColor(Color.WHITE);
//		mainLayout.addView(textView);
//		for(String text:prefManager.getBroadCastNamesArray()){
//			String tmpText = prefManager.getBroadCastDisplayName(text);
//			if(tmpText==null || tmpText.trim().equals(""))
//				continue;
//			textView = new MyriadSemiboldTextView(this);
//			checkBoxView = new CheckBox(this);
//			GroupTypeInfo groupTypeInfo = new GroupTypeInfo();
//			 groupTypeInfo.setGroupDisplayName(tmpText);
//			 groupTypeInfo.setgroupRequestName(tmpText);
//			 groupTypeInfo.setAsGroup(false);
//			 checkBoxView.setTag(groupTypeInfo);
//			 if(list!=null && list.contains(tmpText)){
//				 checkBoxView.setChecked(true); 
//			 }
//			textView.setTag(text);
//			textView.setPadding(10, 2, 0, 0);
//			params.setMargins(0, 2, 0, 0);
//			checkBoxView.setPadding(10, 2, 0, 0);
//			textView.setLayoutParams(params);
//			checkBoxView.setLayoutParams(params);
//			
//			checkBoxView.setText(tmpText);
//			//(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
//			 
//			textView.setText(tmpText);
//			checkBoxView.setText(tmpText);
//			checkBoxView.setBackgroundColor(Color.WHITE);
////			textView.setBackgroundResource(R.drawable.round_rect);
//			textView.setBackgroundColor(Color.WHITE);
//			textView.setTextColor(Color.DKGRAY);
//			textView.setPadding(10, 0, 0, 0);
//			textView.setTextSize(19);
//			textView.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					if (memberOptionDialog != null && !memberOptionDialog.isShowing()) {
//						
//						selectedMemberUserName = ((TextView)v).getTag().toString();
//						selectedUserdisplayName =  ((TextView)v).getText().toString();
//						if(!selectedMemberUserName.equals(iChatPref.getUserName()))
//							memberOptionDialog.show();
//					}
//				}
//			});
//			mainLayout.addView(checkBoxView);
//		}
		
		// new added broadcasts  *********************************************************************
		
		textView = new MyriadSemiboldTextView(this);
		params.setMargins(0, 20, 0, 10);
		textView.setLayoutParams(params);
		textView.setTextSize(19);
		textView.setPadding(0, 5, 0, 5);
		textView.setText("New Broadcast Lists");
//		textView.setBackgroundResource(R.drawable.round_rect);
		textView.setBackgroundColor(Color.DKGRAY);
		textView.setTextColor(Color.WHITE);
//		mainLayout.addView(textView);
		horizontalLayout = new LinearLayout(this);
		horizontalLayout.setBackgroundColor(Color.DKGRAY);
//		horizontalLayout
		horizontalLayout.setLayoutParams(horizontalLayoutParams);
		textView.setLayoutParams(horizontalLayoutParams);
		horizontalLayout.addView(textView);
		
		 addView = new ImageView(this);
		 addView.setPadding(10, 5, 0, 5);
		addView.setBackgroundResource(R.drawable.media_attach_icon);
		addView.setLayoutParams(horizontalLayoutParams);
		horizontalLayout.setPadding(10, 5, 0, 5);
		horizontalLayout.addView(addView);
		mainLayout.addView(horizontalLayout,params);
		addView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (memberOptionDialog != null && !memberOptionDialog.isShowing()) {
					isNewGroupAdding = false;
					newGroupOrBroadName.setText("");
					adminCheckBox.setChecked(false);
					//groupTab.setBackgroundResource(R.drawable.button_background_white_midle_round);
					//broadcastTab.setBackgroundResource(R.drawable.button_background_green_midle_round);
					groupTab.setVisibility(View.INVISIBLE);
					broadcastTab.setVisibility(View.VISIBLE);
					newGroupOrBroadName.setHint(getString(R.string.type_broadcast_name));
					memberOptionDialog.show();
					
			}
			}
		});
		for(String text:newBroadcastsList.keySet()){
			String tmpText = text;
			textView = new MyriadSemiboldTextView(this);
			checkBoxView = new CheckBox(this);
			checkBoxView.setTag(tmpText);
			GroupTypeInfo groupTypeInfo = new GroupTypeInfo();
			 groupTypeInfo.setGroupDisplayName(tmpText);
			 groupTypeInfo.setgroupRequestName(tmpText);
			 groupTypeInfo.setAsGroup(false);
			 checkBoxView.setTag(groupTypeInfo);
			 if(list!=null && list.contains(tmpText)){
				 checkBoxView.setChecked(true); 
			 }
			textView.setPadding(10, 2, 0, 0);
			params.setMargins(0, 2, 0, 0);
			checkBoxView.setPadding(10, 2, 0, 0);
			textView.setLayoutParams(params);
			checkBoxView.setLayoutParams(params);
			
			checkBoxView.setText(tmpText);
			//(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			textView.setText(tmpText);
			checkBoxView.setText(tmpText);
			checkBoxView.setBackgroundColor(Color.WHITE);
//			textView.setBackgroundResource(R.drawable.round_rect);
			textView.setBackgroundColor(Color.WHITE);
			textView.setTextColor(Color.DKGRAY);
			textView.setPadding(10, 0, 0, 0);
			textView.setTextSize(19);
			textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (memberOptionDialog != null && !memberOptionDialog.isShowing()) {
						isNewGroupAdding = false;
						selectedMemberUserName = ((TextView)v).getTag().toString();
						selectedUserdisplayName =  ((TextView)v).getText().toString();
						if(!selectedMemberUserName.equals(iChatPref.getUserName()))
							memberOptionDialog.show();
					}
				}
			});
			mainLayout.addView(checkBoxView);
		}
		
		
		
	}
	private void setPic(ImageView view){
		String groupPicId = iChatPref.getUserFileId(groupUUID); // 1_1_7_G_I_I3_e1zihzwn02
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			view.setTag(filename);
		}else if(groupPicId!=null){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			//			if(groupPicId != null && groupPicId.length() > 0 && groupPicId.lastIndexOf('/')!=-1)
			//				profilePicUrl += groupPicId.substring(groupPicId.lastIndexOf('/'));

			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			File file1 = new File(filename);
			//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				setThumb(view, filename,groupPicId);
				view.setTag(filename);
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
	public void onBackPressed(){
		super.onBackPressed();
		setResult(RESULT_OK, new Intent(this,ChatListScreen.class));
		finish();
	}
	public void onBackClick(View view) {
		setResult(RESULT_OK, new Intent(this,ChatListScreen.class));
		finish();
	}
	private class GroupUpdateTaskOnServer extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {		
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			GroupChatServerModel model = new GroupChatServerModel();

				model.setUserName(iPrefManager.getUserName());
				model.setGroupName(groupUUID);
				model.setDisplayName(displayName);
				model.setMemberUserSet(usersList);
//				List<String> adminUserSet = new ArrayList<String>();
//				model.setAdminUserSet(adminUserSet);
			    String JSONstring = new Gson().toJson(model);
			    
			    DefaultHttpClient client1 = new DefaultHttpClient();
			    
				Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
				
				 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/jakarta/rest/group/update");
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
				 HttpResponse response = null;
		         try {
					httpPost.setEntity(new StringEntity(JSONstring));
					 try {
						 response = client1.execute(httpPost);
						 final int statusCode=response.getStatusLine().getStatusCode();
						 if (statusCode != HttpStatus.SC_OK){
							 HttpEntity entity = response.getEntity();
		//					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
							    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
					            String line = "";
					            while ((line = rd.readLine()) != null) {
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
			
			
		    
			
			return null;
		}

		@Override
		protected void onPostExecute(String response) {

			super.onPostExecute(response);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
			if (resultCode == RESULT_OK)
				switch (requestCode) {
				case 100:
					statusMessage = iChatPref.getUserStatusMessage(groupUUID);
					displayName = iChatPref.getGroupDisplayName(groupUUID);
//					if(displayName.contains("##$^##"))
//						displayNameView.setText(displayName.substring(0, displayName.indexOf("##$^##")));
//			    	else
//			    		displayNameView.setText(displayName);
					isProfileModified = true;
					break;
				}
				
		}
	private List<String> getSelectedItems(){
		List<String> selectedItems = new ArrayList<String>(); 
		for(int index = 0; index<mainLayout.getChildCount();index++){
			View childView = mainLayout.getChildAt(index);
			if(childView instanceof CheckBox){
				CheckBox checkBox = (CheckBox)childView;
				if(checkBox!=null && checkBox.isChecked()){
					Object obj = checkBox.getTag();
					if(obj!=null && obj instanceof GroupTypeInfo){
						GroupTypeInfo groupTypeInfo= (GroupTypeInfo)obj;
						if(groupTypeInfo!=null){
							selectedItems.add(groupTypeInfo.getGroupReguestName());
						}
						
					}
				}
				
			}
		}
		return selectedItems;
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_done:
			Log.d(TAG, "ChildCounts of view : "+ mainLayout.getChildCount());
			String groups = null;
			String broadcasts = null;
			for(int index = 0; index<mainLayout.getChildCount();index++){
				View childView = mainLayout.getChildAt(index);
				if(childView instanceof CheckBox){
					CheckBox checkBox = (CheckBox)childView;
					if(checkBox!=null && checkBox.isChecked()){
						Object obj = checkBox.getTag();
						if(obj!=null && obj instanceof GroupTypeInfo){
							GroupTypeInfo groupTypeInfo= (GroupTypeInfo)obj;
							if(groupTypeInfo!=null){
								if(groupTypeInfo.isAsGroup()){
									if(groups == null){
										groups = groupTypeInfo.getGroupReguestName()+",";
									}else
										groups = groups + groupTypeInfo.getGroupReguestName()+",";
									Log.d(TAG, "ChildCounts of view group : "+ groupTypeInfo.getGroupDisplayName()+" , "+groupTypeInfo.getGroupReguestName());
								}else{
									if(broadcasts == null){
										broadcasts = groupTypeInfo.getGroupReguestName()+",";
									}else
										broadcasts = broadcasts + groupTypeInfo.getGroupReguestName()+",";
									Log.d(TAG, "ChildCounts of view broadcast : "+ groupTypeInfo.getGroupDisplayName()+" , "+groupTypeInfo.getGroupReguestName());
								}
							}
							
						}
					}
					
				}
			}
//			requestModel.directoryUserSet.get(0).broadcastCsv
			if(groups!=null && !groups.equals(""))
				requestModel.directoryUserSet.get(0).groupCsv = groups;
			
			if(broadcasts!=null && !broadcasts.equals(""))
				requestModel.directoryUserSet.get(0).broadcastCsv = broadcasts;
			
			if(Build.VERSION.SDK_INT >= 11)
				new InviteMemberServerTask(requestModel, null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new InviteMemberServerTask(requestModel, null).execute();
			break;
//		case R.id.id_add_new:
//			if (memberOptionDialog != null && !memberOptionDialog.isShowing()) {
//					isNewGroupAdding = true;
//					//groupTab.setBackgroundResource(R.drawable.button_background_green_midle_round);
//					//broadcastTab.setBackgroundResource(R.drawable.button_background_white_midle_round);
//					newGroupOrBroadName.setHint(getString(R.string.type_group_name));
//					newGroupOrBroadName.setText("");
//					adminCheckBox.setChecked(false);
//					memberOptionDialog.show();
//					
//			}
//			break;
//		case R.id.id_new_group:
//			if(!isNewGroupAdding){
//				newGroupOrBroadName.setText("");
//				adminCheckBox.setChecked(false);
//				//groupTab.setBackgroundResource(R.drawable.button_background_green_midle_round);
//				//broadcastTab.setBackgroundResource(R.drawable.button_background_white_midle_round);
//				newGroupOrBroadName.setHint(getString(R.string.type_group_name));
//			}
//			isNewGroupAdding = true;
//			break;
		case R.id.id_new_broad:
			if(isNewGroupAdding){
				newGroupOrBroadName.setText("");
				adminCheckBox.setChecked(false);
				//groupTab.setBackgroundResource(R.drawable.button_background_white_midle_round);
				//broadcastTab.setBackgroundResource(R.drawable.button_background_green_midle_round);
				newGroupOrBroadName.setHint(getString(R.string.type_broadcast_name));
			}
			isNewGroupAdding = false;
			break;
		case R.id.id_add:
			String groupOrBrodName = newGroupOrBroadName.getText().toString();
			if(groupOrBrodName == null || groupOrBrodName.trim().equals("")){
				if(isNewGroupAdding)
					showDialog("Please enter the group name.");
				else
					showDialog("Please enter the broadcast name.");
				return;
			}
			if(adminCheckBox.isChecked())
				groupOrBrodName = "a:"+groupOrBrodName;
			if(isNewGroupAdding)
				newGroupsList.put(groupOrBrodName, groupOrBrodName);
			else
				newBroadcastsList.put(groupOrBrodName, groupOrBrodName);
			
			if(memberOptionDialog!=null)
				memberOptionDialog.cancel();
			List<String> selectedItems =  getSelectedItems();
			selectedItems.add(groupOrBrodName);
			mainLayout.removeAllViews();
			addTextView(mainLayout, selectedItems);
			break;
		case R.id.id_info:
			Intent intent = new Intent(this, ProfileScreen.class);
			 Bundle bundle = new Bundle();
			 bundle.putString(Constants.CHAT_USER_NAME, selectedMemberUserName);
			 bundle.putString(Constants.CHAT_NAME, selectedUserdisplayName);
			 
//			 intent.putExtra(Constants.CHAT_USER_NAME, selectedMemberUserName);
//			 intent.putExtra(Constants.CHAT_NAME, selectedUserdisplayName);
			 intent.putExtras(bundle);
			 startActivity(intent);
			 
			 if(memberOptionDialog!=null)
					memberOptionDialog.cancel();
				
			break;
		case R.id.id_message_user:
			if(selectedUserdisplayName == null || selectedUserdisplayName.equals(""))
				selectedUserdisplayName = selectedMemberUserName;
			 intent = new Intent(this,ChatListScreen.class);
			intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,selectedUserdisplayName);
			intent.putExtra(DatabaseConstants.USER_NAME_FIELD,selectedMemberUserName);
			startActivity(intent);
			finish();
			 if(memberOptionDialog!=null)
					memberOptionDialog.cancel();
				
			break;
//		case R.id.id_add_remove_admin:
//			break;
		case R.id.id_remove_user:
//			if(service!=null){
//				service.removeGroupPerson(groupUUID, selectedMemberUserName);
//				finish();
//			}else{
//
//			}
			if(service!=null){
				service.removeGroupPerson(groupUUID, selectedMemberUserName);
				usersList.remove(selectedMemberUserName);
				iChatPref.saveServerGroupState(groupUUID, GroupCreateTaskOnServer.SERVER_GROUP_NOT_UPDATED);//new GroupUpdateTaskOnServer().execute();
				iChatPref.removeUsersFromGroup(groupUUID, selectedMemberUserName);
//				finish();
				ArrayList<String> tmp = new ArrayList<String>();
				String name = selectedMemberUserName;
				if(name!=null && name.contains("#786#"))
		        	name = name.substring(0, name.indexOf("#786#"));
				tmp.add(name);
				usersList.remove(name);
				mainLayout.removeView(mainLayout.findViewWithTag(name));
			if(memberOptionDialog!=null)
				memberOptionDialog.cancel();
			}
			break;
		case R.id.id_cancel:
			if(memberOptionDialog!=null)
				memberOptionDialog.cancel();
			break;
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
	public void saveMessage(String displayName, String from, String msg) {
		try {
			ChatDBWrapper chatDBWrapper = ChatDBWrapper.getInstance();
			ContentValues contentvalues = new ContentValues();
			String myName = SharedPrefManager.getInstance().getUserName();
			contentvalues.put(DatabaseConstants.FROM_USER_FIELD, from);
			contentvalues.put(DatabaseConstants.TO_USER_FIELD, myName);
			contentvalues.put(DatabaseConstants.UNREAD_COUNT_FIELD,
					new Integer(1));
			contentvalues.put(DatabaseConstants.FROM_GROUP_USER_FIELD, "");
			contentvalues.put(DatabaseConstants.SEEN_FIELD, com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState.sent.ordinal());

			contentvalues.put(DatabaseConstants.MESSAGEINFO_FIELD, msg);
			// String name =
			// cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));

			String name = "";
			String oppName = "";
			{
				oppName = from;
				name = chatDBWrapper.getChatName(from);
				contentvalues.put(DatabaseConstants.MESSAGE_ID, UUID
						.randomUUID().toString());
				contentvalues.put(DatabaseConstants.FOREIGN_MESSAGE_ID_FIELD,
						UUID.randomUUID().toString());
			}

			long currentTime = System.currentTimeMillis();
			Calendar calender = Calendar.getInstance();
			calender.setTimeInMillis(currentTime);
			int date = calender.get(Calendar.DATE);
			int oldDate = date;
			long milis = ChatDBWrapper.getInstance().lastMessageInDB(oppName);
			if(milis!=-1){
				calender.setTimeInMillis(milis);
				oldDate = calender.get(Calendar.DATE);
			}
			if ((oldDate != date)
					|| ChatDBWrapper.getInstance().isFirstChat(oppName)) {
				contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "1");
			} else {
				contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "0");
			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(DatabaseConstants.LAST_UPDATE_FIELD, currentTime);

			contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, name);
			chatDBWrapper.insertInDB(DatabaseConstants.TABLE_NAME_MESSAGE_INFO,contentvalues);
		} catch (Exception e) {

		}
	}
	private class YourAsyncTask extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;
		List<String> displayList = new ArrayList<String>();
			protected void onPreExecute() {
				
				dialog = ProgressDialog.show(MemberInviteWithGroupOrBroadCast.this, "","Loading. Please wait...", true);

//				progressBarView.setVisibility(ProgressBar.VISIBLE);
				 super.onPreExecute();
		     }
		     protected String doInBackground(String... args) {
		    	 displayList = convertNames(usersList);		
		    	 if(selectedMessageId!=null && !selectedMessageId.equals("")){
		    		 Cursor cursor = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupOrBroadCastUsersStatus(selectedMessageId);
		    		 if(cursor!=null){
		    			 try{
		    				 if (cursor != null && cursor.moveToFirst()) {
		    						do {
		    							String groupDisplayName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD));
		    							String messageStatus = cursor.getString(cursor.getColumnIndex(ChatDBConstants.SEEN_FIELD));
		    							String messageDeliveryTime = cursor.getString(cursor.getColumnIndex(ChatDBConstants.DELIVER_TIME_FIELD));
		    							String messageSeenTime = cursor.getString(cursor.getColumnIndex(ChatDBConstants.SEEN_TIME_FIELD));
//		    							Log.d("ChatDBWrapper",messageId+" getGroupOrBroadCastUsersStatus count: " + cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID)));
		    						} while (cursor.moveToNext());
		    					}
		    			 }catch(Exception e){}
		    			 finally {
						 cursor.close();
		    			 cursor = null;
		    			 }
		    		 }
		    	}
		        return null;
		     }

		     protected void onPostExecute(String result) {
		    	 addTextView(mainLayout, displayList);
		    	 progressBarView.setVisibility(ProgressBar.GONE);
		    	 dialog.cancel();
					super.onPostExecute(result);
		     }
		 }
	class GroupTypeInfo{
		String groupDisplayName;
		String groupReguestName;
		boolean isGroup = false;
		public boolean isAsGroup() {
			return isGroup;
		}
		public void setAsGroup(boolean isGroup) {
			this.isGroup = isGroup;
		}
		GroupTypeInfo(){
			
		}
		public String getGroupDisplayName() {
			return groupDisplayName;
		}
		public void setGroupDisplayName(String groupDisplayName) {
			this.groupDisplayName = groupDisplayName;
		}
		public String getGroupReguestName() {
			return groupReguestName;
		}
		public void setgroupRequestName(String groupReguestName) {
			this.groupReguestName = groupReguestName;
		}
		
		 
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
//				 Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName() );
					Intent intent = new Intent(MemberInviteWithGroupOrBroadCast.this, HomeScreen.class);
//					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_CLEAR_TASK
							| Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				finish();
				return false;
			}
		});
		bteldialog.show();
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
		View view1;
		public InviteMemberServerTask(AddMemberModel requestForm,final View view1){
			this.requestForm = requestForm;
			this.view1 = view1;
		}
		@Override
		protected void onPreExecute() {		
			progressDialog = ProgressDialog.show(MemberInviteWithGroupOrBroadCast.this, "", "Loading. Please wait...", true);
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
			}
			super.onPostExecute(str);
		}
		
		
	}
}
