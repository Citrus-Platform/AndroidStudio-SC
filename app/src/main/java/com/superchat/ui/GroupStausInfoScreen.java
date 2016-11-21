package com.superchat.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.emojicon.EmojiconTextView;
import com.superchat.model.BroadCastDetailsModel;
import com.superchat.model.GroupChatServerModel;
import com.superchat.model.GroupDetailsModel;
import com.superchat.utils.Constants;
import com.superchat.utils.GroupCreateTaskOnServer;
import com.superchat.utils.Log;
import com.superchat.utils.MyBase64;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.MyriadSemiboldTextView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GroupStausInfoScreen extends Activity implements OnClickListener{
	public final static String TAG = "GroupStausInfoScreen"; 
	private RelativeLayout ownerLayout;
	private boolean isGroupAdmin;
	private boolean isGroupOwner;
	private String groupUUID;
	private String selectedMessageId;
	private String displayName = "";
	private String statusMessage = "";
	private String selectedMemberUserName;
	private String selectedUserdisplayName;
	private List<String> usersList;
	private TextView displayNameView;
	private TextView title;
	private TextView backTitle;
	private Calendar calander;
	Calendar currentCalender;
	private EmojiconTextView statusView;
	private TextView ownerView;
	private TextView adminView; 
	private TextView allMediaView;
	private TextView groupNotificationView;
	private TextView leaveGroupChatView;
	private TextView editGroupView;
	private TextView muteGroupView;
	private ImageView groupIconView;
	private SharedPrefManager iChatPref;
	private Dialog memberOptionDialog; 
	private HashMap<String, String> hashMap;
	private HashMap<String, MessageInfo> infoList;
	boolean isProfileModified;
	private ChatService service;
	private ChatService messageService;
	boolean invitationEnable;
	ArrayList<String> inviters = null;
	LinearLayout mainLayout;
	private boolean isBroadCast;
	private boolean isUser;
	ProgressBar progressBarView;
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
							messageService.inviteUserInRoom(groupUUID,displayName, description, inviter, null);
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
		setContentView(R.layout.group_status_screen);
		calander = Calendar.getInstance(TimeZone.getDefault());
		currentCalender = Calendar.getInstance(TimeZone.getDefault());
		calander.setTimeInMillis(System.currentTimeMillis());
		currentCalender.setTimeInMillis(System.currentTimeMillis());
		progressBarView = (ProgressBar)findViewById(R.id.id_loading);
		isProfileModified = false;
		title = (TextView)findViewById(R.id.id_group_info_title);
		backTitle = (TextView)findViewById(R.id.id_back_title);
		displayNameView = (TextView)findViewById(R.id.id_group_name);
		statusView = (EmojiconTextView)findViewById(R.id.id_status_message); 
		groupIconView = (ImageView) findViewById(R.id.id_group_icon);
		editGroupView = (TextView)findViewById(R.id.id_edit_group); 
		
		
		
		editGroupView.setOnClickListener(this);
		groupIconView.setOnClickListener(this);
		ownerView = (TextView)findViewById(R.id.id_owner); 
		adminView = (TextView)findViewById(R.id.id_admin); 
		leaveGroupChatView = (TextView)findViewById(R.id.id_leave_group_chat);
		ownerLayout = (RelativeLayout)findViewById(R.id.id_owner_layout); 
		
		iChatPref = SharedPrefManager.getInstance();
		leaveGroupChatView.setOnClickListener(this);

		allMediaView = (TextView) findViewById(R.id.id_view_all_media);
		allMediaView.setOnClickListener(this);
		groupNotificationView = (TextView) findViewById(R.id.id_group_notification);
		groupNotificationView.setOnClickListener(this);
		muteGroupView = (TextView) findViewById(R.id.id_mute_group_chat);
		muteGroupView.setOnClickListener(this);
		Bundle tmpBundle = getIntent().getExtras();
		if(tmpBundle!=null){
			isBroadCast = tmpBundle.getBoolean(Constants.BROADCAST, false);
			groupUUID = tmpBundle.get(Constants.CHAT_USER_NAME).toString();
			selectedMessageId = tmpBundle.getString(Constants.SELECTED_MESSAGE_ID,null);
			displayName = tmpBundle.get(Constants.CHAT_NAME).toString();
			isGroupAdmin = iChatPref.isAdmin(groupUUID, iChatPref.getUserName());
			isGroupOwner = iChatPref.isOwner(groupUUID, iChatPref.getUserName());
			usersList = getIntent().getExtras().getStringArrayList(Constants.GROUP_USERS);
			
			if(groupUUID!=null)
				statusMessage = iChatPref.getUserStatusMessage(groupUUID);
			mainLayout = (LinearLayout)findViewById(R.id.id_group_members);

//			hashMap = (HashMap<String, String>)getIntent().getSerializableExtra(Constants.USER_MAP);
			hashMap = new HashMap<String, String>();
           
			if(isBroadCast){
				
//				title.setText(getString(R.string.broadcast_info));
				backTitle.setText(getString(R.string.broadcast_chat));
				ownerLayout.setVisibility(View.GONE);
			}else{
				if(!iChatPref.isGroupChat(groupUUID))
					isUser = true;
			}

		}
//		if(displayName.contains("##$^##"))
//			displayNameView.setText(displayName.substring(0, displayName.indexOf("##$^##")));
//    	else
//    		displayNameView.setText(displayName);
		statusView.setText(statusMessage);
//		setPic(groupIconView);
		memberOptionDialog = new Dialog(this);
		memberOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		memberOptionDialog.setContentView(R.layout.group_members_options_dialog);
		Window window = memberOptionDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.BOTTOM;
		wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);
		editGroupView.setVisibility(TextView.GONE);
//		if(!isGroupAdmin){
//			memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.GONE);
//			memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.INVISIBLE);
//		}else{
//			editGroupView.setVisibility(TextView.VISIBLE);
//			memberOptionDialog.findViewById(R.id.id_remove_user).setOnClickListener(this);
//		}
//		if(!isGroupOwner){
//			memberOptionDialog.findViewById(R.id.id_add_remove_admin).setVisibility(View.GONE);
//			memberOptionDialog.findViewById(R.id.id_add_remove_admin_seprator).setVisibility(View.INVISIBLE);
//		}else {
//			editGroupView.setVisibility(TextView.VISIBLE);
//			memberOptionDialog.findViewById(R.id.id_add_remove_admin).setOnClickListener(this);
//		}


//		memberOptionDialog.findViewById(R.id.id_message_user).setOnClickListener(this);
//		memberOptionDialog.findViewById(R.id.id_info).setOnClickListener(this);
//		memberOptionDialog.findViewById(R.id.id_cancel).setOnClickListener(this);
		
//		 if(Build.VERSION.SDK_INT >= 11)
//			 new YourAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		    else
//		    	new YourAsyncTask().execute();
		if(!isUser)
			getServerGroupProfile(groupUUID);
		else{
			backTitle.setText("Chat");
			createP2PInfo();			
		}
	}
	private void createP2PInfo(){
		int messageType = Message.XMPPMessageType.atMeXmppMessageTypeNormal.ordinal();
		if(selectedMessageId!=null){
			String messageTypeValue = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageType(selectedMessageId);
			if(messageTypeValue!=null && !messageTypeValue.equals("")){
				try{
					messageType = Integer.parseInt(messageTypeValue);
				}catch(NumberFormatException e){
				}
			}
			displayNameView.setVisibility(View.GONE);
			if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeImage.ordinal()||messageType == Message.XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				String thumb = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);
				
				if(thumb != null && !thumb.equals("")){
//					Bitmap bitmap = createThumbFromByteArray(thumb);
					android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(thumb);
					if(bitmap!=null){
						groupIconView.setImageBitmap(bitmap);
						groupIconView.setBackgroundDrawable(null);
					}
				}
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypePdf.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.pdf);
				String mediaName = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);				
				if(mediaName!=null && !mediaName.equals("") && mediaName.contains("/")){
					mediaName = mediaName.substring(mediaName.lastIndexOf("/")+1);
					displayNameView.setVisibility(View.VISIBLE);
					displayNameView.setText(mediaName);
				}
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.docs);
				String mediaName = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);
				
				if(mediaName!=null && !mediaName.equals("") && mediaName.contains("/")){
					mediaName = mediaName.substring(mediaName.lastIndexOf("/")+1);
					displayNameView.setVisibility(View.VISIBLE);
					displayNameView.setText(mediaName);
				}
				
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.xls);
				String mediaName = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);
				
				if(mediaName!=null && !mediaName.equals("") && mediaName.contains("/")){
					mediaName = mediaName.substring(mediaName.lastIndexOf("/")+1);
					displayNameView.setVisibility(View.VISIBLE);
					displayNameView.setText(mediaName);
				}
				
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypePPT.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.ppt);
				String mediaName = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);
				
				if(mediaName!=null && !mediaName.equals("") && mediaName.contains("/")){
					mediaName = mediaName.substring(mediaName.lastIndexOf("/")+1);
					displayNameView.setVisibility(View.VISIBLE);
					displayNameView.setText(mediaName);
				}
				
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeContact.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.share_contact);
				String captionTag = ChatDBWrapper.getInstance(SuperChatApplication.context).getCaptionTag(selectedMessageId);
				
				if(captionTag!=null && !captionTag.equals("") ){
					if(captionTag.contains("&quot;"))
						captionTag = captionTag.replace("&quot;", "\"");
					try {
						//Show Values from JSON
						JSONObject jsonobj = new JSONObject(captionTag);
						String dislay_name = "Unknown";
						if(jsonobj.has("firstName") && jsonobj.getString("firstName").toString().trim().length() > 0)
							dislay_name = jsonobj.getString("firstName");
						if(jsonobj.has("lastName") && jsonobj.getString("lastName").toString().trim().length() > 0)
							dislay_name = dislay_name + " " + jsonobj.getString("lastName");
						displayNameView.setVisibility(View.VISIBLE);
						displayNameView.setText(dislay_name);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.GONE);
//				groupIconView.setBackgroundResource(R.drawable.announce);
				((RelativeLayout)findViewById(R.id.audio_control_layout)).setVisibility(View.VISIBLE);
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeLocation.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.gmap);
			}else{
				groupIconView.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				String sentMessage = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessage(selectedMessageId);
				if(sentMessage!=null)
					statusView.setText(sentMessage);
			}
			
			String readTime = ChatDBWrapper.getInstance(SuperChatApplication.context).getMessageReadTime(selectedMessageId);
			String deliverTime = ChatDBWrapper.getInstance(SuperChatApplication.context).getMessageDeliverTime(selectedMessageId,isUser);
			if(deliverTime!=null && !deliverTime.equals(""))
			 calander.setTimeInMillis(Long.parseLong(deliverTime));
				SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
//				if((currentCalender.get(Calendar.DAY_OF_YEAR)-calander.get(Calendar.DAY_OF_YEAR))<7)
				int days = daysBetween(currentCalender, calander);
				
				String msgTime = format.format( calander.getTime());
				if(days == 1){
					msgTime = "Yesterday";
				}else if(days>1 && days<7){
					format = new SimpleDateFormat("EEE,hh:mm aa");
					msgTime = format.format( calander.getTime());
				}else if(days>=7){
					format = new SimpleDateFormat("dd/MM/yy hh:mm aa");
					msgTime = format.format( calander.getTime());
				}else
					msgTime = "Today, "+msgTime;
				addSingleBoldTextView(mainLayout, "Delivered");
				if(deliverTime!=null && !deliverTime.equals(""))
					addSingleTextView(mainLayout, ""+msgTime);
			
			calander.setTimeInMillis(Long.parseLong(readTime));
			format = new SimpleDateFormat("hh:mm aa");
//				if((currentCalender.get(Calendar.DAY_OF_YEAR)-calander.get(Calendar.DAY_OF_YEAR))<7)
			 days = daysBetween(currentCalender, calander);
			
			 msgTime = format.format( calander.getTime());
			if(days == 1){
				msgTime = "Yesterday";
			}else if(days>1 && days<7){
				format = new SimpleDateFormat("EEE,hh:mm aa");
				msgTime = format.format( calander.getTime());
			}else if(days>=7){
				format = new SimpleDateFormat("dd/MM/yy hh:mm aa");
				msgTime = format.format( calander.getTime());
			}else
				msgTime = "Today, "+msgTime;
			if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeImage.ordinal())
				addSingleBoldTextView(mainLayout, "Seen");
			else
				addSingleBoldTextView(mainLayout, "Read");
			if(readTime!=null && !readTime.equals("") && !readTime.equals("0")){
				addSingleTextView(mainLayout, ""+msgTime);
				View view = new View(this);
				view.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1)));
				view.setBackgroundColor(Color.GRAY);
				mainLayout.addView(view);
			}
		}
		
	}
	private void addSingleBoldTextView(LinearLayout mainLayout, String tmpText){
		View view = new View(this);
		view.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1)));
		view.setBackgroundColor(Color.GRAY);
		mainLayout.addView(view);
		MyriadSemiboldTextView textView = new MyriadSemiboldTextView(this);
		textView.setTag(tmpText);
		textView.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
//		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setText(tmpText);
//		textView.setBackgroundResource(R.color.gray);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(16);
		textView.setBottom(2);
		textView.setPadding(10, 10, 0, 0);
		mainLayout.addView(textView);
		
}
	private void addSingleTextView(LinearLayout mainLayout, String tmpText){
		TextView textView = new TextView(this);
		textView.setTag(tmpText);
		textView.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
//		textView.setGravity(Gravity.CENTER_HORIZONTAL);
		textView.setText(tmpText);
//		textView.setBackgroundResource(R.color.gray);
		textView.setTextColor(Color.GRAY);
		textView.setTextSize(13);
		textView.setPadding(10, 10, 0, 10);
		textView.setBottom(2);
		mainLayout.addView(textView);
		
}
private class YourAsyncTask extends AsyncTask<String, Void, String> {
	ProgressDialog dialog;
	List<String> displayList = new ArrayList<String>();
		protected void onPreExecute() {
			
			dialog = ProgressDialog.show(GroupStausInfoScreen.this, "","Loading. Please wait...", true);

//			progressBarView.setVisibility(ProgressBar.VISIBLE);
			 super.onPreExecute();
	     }
	     protected String doInBackground(String... args) {
	    	 displayList = convertNames(usersList);		
	    	 if(selectedMessageId!=null && !selectedMessageId.equals("")){
	    		 Cursor cursor = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupOrBroadCastUsersStatus(selectedMessageId);
	    		 if(cursor!=null){
	    			 infoList = new HashMap<String, MessageInfo>();
	    			 try{
	    				 if (cursor != null && cursor.moveToFirst()) {
	    						do {
	    							String messageUserName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD));
	    							String messageStatus = cursor.getString(cursor.getColumnIndex(ChatDBConstants.SEEN_FIELD));
	    							String messageDeliveryTime = cursor.getString(cursor.getColumnIndex(ChatDBConstants.DELIVER_TIME_FIELD));
	    							String messageSeenTime = cursor.getString(cursor.getColumnIndex(ChatDBConstants.SEEN_TIME_FIELD));
	    							if(messageUserName!=null && !messageUserName.equals("")){
	    								MessageInfo messageInfo = new MessageInfo();
	    								messageInfo.setMessageDeliveryTime(messageDeliveryTime);
	    								messageInfo.setMessageSeenTime(messageSeenTime);
	    								messageInfo.setMessageUserName(messageUserName);
	    								messageInfo.setMessageStatus(messageStatus);
	    								if(infoList!=null)
	    									infoList.put(messageUserName, messageInfo);
	    							}
//	    							Log.d("ChatDBWrapper",messageId+" getGroupOrBroadCastUsersStatus count: " + cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID)));
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
class MessageInfo{
	String messageUserName;
	String messageRecipentName;
	String messageDeliveryTime;
	String messageSeenTime;
	String messageStatus;
	
	MessageInfo(){
		
	}
	public String getMessageStatus() {
		return messageStatus;
	}
	public void setMessageStatus(String messageStatus) {
		this.messageStatus = messageStatus;
	}
	public String getMessageUserName() {
		return messageUserName;
	}
	public void setMessageUserName(String messageUserName) {
		this.messageUserName = messageUserName;
	}
	public String getMessageRecipentName() {
		return messageRecipentName;
	}
	public void setMessageRecipentName(String messageRecipentName) {
		this.messageRecipentName = messageRecipentName;
	}
	public String getMessageDeliveryTime() {
		return messageDeliveryTime;
	}
	public void setMessageDeliveryTime(String messageDeliveryTime) {
		this.messageDeliveryTime = messageDeliveryTime;
	}
	public String getMessageSeenTime() {
		return messageSeenTime;
	}
	public void setMessageSeenTime(String messageSeenTime) {
		this.messageSeenTime = messageSeenTime;
	}
	
	 
}
private void getServerGroupProfile(String groupName){
	final Context context = this;
	
	AsyncHttpClient client = new AsyncHttpClient();
	String path = "";
	try {
		if(isBroadCast)
			path = Constants.SERVER_URL+"/tiger/rest/bcgroup/detail?groupName="+URLEncoder.encode(groupName, "utf-8");
		else
			path = Constants.SERVER_URL+"/tiger/rest/group/detail?groupName="+URLEncoder.encode(groupName, "utf-8");;
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	client = SuperChatApplication.addHeaderInfo(client,true);
	client.get(path, null, new AsyncHttpResponseHandler() {
		ProgressDialog dialog = null;

		@Override
		public void onStart() {
			dialog = ProgressDialog.show(GroupStausInfoScreen.this, "","Loading. Please wait...", true);
			Log.d(TAG, "AsyncHttpClient onStart: ");
		}

		@Override
		public void onSuccess(int arg0, String arg1) {
			Log.d(TAG, "AsyncHttpClient onSuccess: "
					+ arg1);

			Gson gson = new GsonBuilder().create();
			if(isBroadCast){
				BroadCastDetailsModel objUserModel = gson.fromJson(arg1, BroadCastDetailsModel.class);
				if (arg1 == null || arg1.contains("error") || objUserModel==null){
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					return;
				}
						
			if(objUserModel!=null){
				if(objUserModel.memberUserSet!=null)
					usersList = objUserModel.memberUserSet;
				
//				if(objUserModel.adminUserSet!=null)
//				usersList = objUserModel.adminUserSet;
				if(objUserModel.adminUserSet!=null){
					for(String str:objUserModel.adminUserSet)
						usersList.add(str);
				}
				if(objUserModel.userName!=null && !objUserModel.userName.equals(""))
					usersList.add(objUserModel.userName);
				if(usersList!=null){
				 if(Build.VERSION.SDK_INT >= 11)
					 new YourAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				    else
				    	new YourAsyncTask().execute();
				}
			}
			}else{
				GroupDetailsModel objUserModel = gson.fromJson(arg1, GroupDetailsModel.class);
				if (arg1 == null || arg1.contains("error") || objUserModel==null){
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					return;
				}
						
				if(objUserModel!=null){
					usersList = objUserModel.memberUserSet;
					if(objUserModel.userName!=null && !objUserModel.userName.equals(""))
						usersList.add(objUserModel.userName);
					if(selectedMessageId!=null && !selectedMessageId.equals(""))
						ChatDBWrapper.getInstance(SuperChatApplication.context).updateTotalUserCount(selectedMessageId, usersList.size());
					if(usersList!=null){
					 if(Build.VERSION.SDK_INT >= 11)
						 new YourAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					    else
					    	new YourAsyncTask().execute();
					}
				}
			}
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			super.onSuccess(arg0, arg1);
		}

		@Override
		public void onFailure(Throwable arg0, String arg1) {
			Log.d(TAG, "AsyncHttpClient onFailure: "+ arg1);
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			//						showDialog("Please try again later.");
			super.onFailure(arg0, arg1);
		}
	});
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
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((ChatService.MyBinder) binder).getService();
			Log.d("Service","Connected");
//			connection=service.getconnection();
		}

		public void onServiceDisconnected(ComponentName className) {
//			connection=null;
			service = null;
		}
	};
	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(this, ChatService.class), mConnection, Context.BIND_AUTO_CREATE);

	}
	protected void onPause() {
		unbindService(mConnection);
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
		int messageType = Message.XMPPMessageType.atMeXmppMessageTypeNormal.ordinal();
		if(selectedMessageId!=null){
			String messageTypeValue = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageType(selectedMessageId);
			if(messageTypeValue!=null && !messageTypeValue.equals("")){
				try{
					messageType = Integer.parseInt(messageTypeValue);
				}catch(NumberFormatException e){					
				}
			}
			displayNameView.setVisibility(View.GONE);
			if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeImage.ordinal()||messageType == Message.XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				String thumb = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);
				
				if(thumb != null && !thumb.equals("")){
//					Bitmap bitmap = createThumbFromByteArray(thumb);
					android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(thumb);
					if(bitmap!=null){
						groupIconView.setImageBitmap(bitmap);
						groupIconView.setBackgroundDrawable(null);
					}
				}
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypePdf.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.pdf);
				String mediaName = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);				
				if(mediaName!=null && !mediaName.equals("") && mediaName.contains("/")){
					mediaName = mediaName.substring(mediaName.lastIndexOf("/")+1);
					displayNameView.setVisibility(View.VISIBLE);
					displayNameView.setText(mediaName);
				}
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.docs);
				String mediaName = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);
				
				if(mediaName!=null && !mediaName.equals("") && mediaName.contains("/")){
					mediaName = mediaName.substring(mediaName.lastIndexOf("/")+1);
					displayNameView.setVisibility(View.VISIBLE);
					displayNameView.setText(mediaName);
				}
				
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.xls);
				String mediaName = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);
				
				if(mediaName!=null && !mediaName.equals("") && mediaName.contains("/")){
					mediaName = mediaName.substring(mediaName.lastIndexOf("/")+1);
					displayNameView.setVisibility(View.VISIBLE);
					displayNameView.setText(mediaName);
				}
				
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypePPT.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.ppt);
				String mediaName = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessageThumb(selectedMessageId);
				
				if(mediaName!=null && !mediaName.equals("") && mediaName.contains("/")){
					mediaName = mediaName.substring(mediaName.lastIndexOf("/")+1);
					displayNameView.setVisibility(View.VISIBLE);
					displayNameView.setText(mediaName);
				}
				
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeContact.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.share_contact);
				String captionTag = ChatDBWrapper.getInstance(SuperChatApplication.context).getCaptionTag(selectedMessageId);
				
				if(captionTag!=null && !captionTag.equals("") ){
					if(captionTag.contains("&quot;"))
						captionTag = captionTag.replace("&quot;", "\"");
					try {
						//Show Values from JSON
						JSONObject jsonobj = new JSONObject(captionTag);
						String dislay_name = "Unknown";
						if(jsonobj.has("firstName") && jsonobj.getString("firstName").toString().trim().length() > 0)
							dislay_name = jsonobj.getString("firstName");
						if(jsonobj.has("lastName") && jsonobj.getString("lastName").toString().trim().length() > 0)
							dislay_name = dislay_name + " " + jsonobj.getString("lastName");
						displayNameView.setVisibility(View.VISIBLE);
						displayNameView.setText(dislay_name);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.GONE);
//				groupIconView.setBackgroundResource(R.drawable.announce);
				((RelativeLayout)findViewById(R.id.audio_control_layout)).setVisibility(View.VISIBLE);
			}else if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeLocation.ordinal()){
				statusView.setVisibility(View.GONE);
				groupIconView.setVisibility(View.VISIBLE);
				groupIconView.setBackgroundResource(R.drawable.gmap);
			}else{
				groupIconView.setVisibility(View.GONE);
				statusView.setVisibility(View.VISIBLE);
				String sentMessage = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupMessage(selectedMessageId);
				if(sentMessage!=null)
					statusView.setText(sentMessage);
			}
		}
		MyriadSemiboldTextView textView = new MyriadSemiboldTextView(this);
		LinearLayout.LayoutParams params = new   LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		params.setMargins(0, 0, 0, 0);
		textView.setLayoutParams(params);
		if(messageType == Message.XMPPMessageType.atMeXmppMessageTypeImage.ordinal() || messageType == Message.XMPPMessageType.atMeXmppMessageTypeAudio.ordinal() || messageType == Message.XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
			textView.setText("Seen by");
		else
			textView.setText("Read by");
//		textView.setBackgroundResource(R.drawable.round_rect);
		textView.setBackgroundColor(Color.GRAY);
		textView.setTextColor(Color.WHITE);
		textView.setTextSize(14);
		textView.setPadding(10, 0, 0, 0);
		textView.setBottom(2);
		mainLayout.addView(textView);
		boolean noReadMember = true;
		MyriadSemiboldTextView statusTextView = null;
		String statusText = null;
		for(String text:usersList){
			String tmpText = text;
				tmpText = hashMap.get(text);
				if(tmpText == null){
					if(text!=null && text.contains("_"))
						tmpText = "+"+text.substring(0, text.indexOf("_"));
					}
			textView = new MyriadSemiboldTextView(this);
			 statusTextView = new MyriadSemiboldTextView(this);
			textView.setTag(text);
			params.setMargins(0, 10, 0, 0);
			textView.setPadding(10, 0, 0, 0);
			textView.setLayoutParams(params);
			statusTextView.setLayoutParams(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
					//(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			 statusText = null;
			if(infoList!=null){
				MessageInfo info = infoList.get(text);
				if(info!=null){
					
					String messageStatus = info.getMessageStatus();
					if(messageStatus!=null){
						if(messageStatus.equals(Message.SeenState.recieved.ordinal()+"")){
							
//							statusText = "";
//							String messageDeliveryTime = info.getMessageDeliveryTime();
//							if(messageDeliveryTime!=null && !messageDeliveryTime.equals("")){
//								long time = Long.parseLong(messageDeliveryTime);
//								calander.setTimeInMillis(time);
////								calander.set(Calendar.DATE, 1);
//								SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
//								String msgTime = format.format(calander.getTime());
//	
////								final int date = calander.get(Calendar.DAY_OF_MONTH);
////								final int month = calander.get(Calendar.MONTH);
////								final int year = calander.get(Calendar.YEAR);
//								int days = daysBetween(currentCalender, calander);
//								
//								if(days == 1){
//									msgTime = "Yesterday";
//								}else if(days>1 && days<7){
//									format = new SimpleDateFormat("EEE,hh:mm aa");
//									msgTime = format.format( calander.getTime());
//								}else if(days>=7){
//									format = new SimpleDateFormat("dd/MM/yy");
//									msgTime = format.format( calander.getTime());
//								}
//								statusText = statusText+" "+msgTime;
//							}
							continue;
						}else if(messageStatus.equals(Message.SeenState.seen.ordinal()+"")){
							noReadMember = false;
							statusText = "";
							String messageSeenTime = info.getMessageSeenTime();
							if(messageSeenTime!=null && !messageSeenTime.equals("")){
								long time = Long.parseLong(messageSeenTime);
								calander.setTimeInMillis(time);
//								calander.set(Calendar.DATE, 1);
//								calander.set(Calendar.MONTH, Calendar.MARCH);
								SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
								String msgTime = format.format(calander.getTime());
	
//								final int date = calander.get(Calendar.DAY_OF_MONTH);
//								final int month = calander.get(Calendar.MONTH);
//								final int year = calander.get(Calendar.YEAR);
								int days = daysBetween(currentCalender, calander);
								
								if(days == 1){
									msgTime = "Yesterday "+msgTime;
								}else if(days>1 && days<7){
									format = new SimpleDateFormat("EEE,hh:mm aa");
									msgTime = format.format( calander.getTime());
								}else if(days>=7){
									format = new SimpleDateFormat("dd/MM/yy");
									msgTime = format.format( calander.getTime());
								}
								statusText = statusText+" "+msgTime;
							}
						}else{
							statusText = "sent";
							continue;
							}
					}
					
				}else if(selectedMessageId!=null){
					statusText = "sent";
					continue;
				}
			}
			
				
			textView.setText(tmpText);
//			textView.setBackgroundResource(R.drawable.round_rect);
			textView.setBackgroundColor(Color.WHITE);
			textView.setTextColor(Color.DKGRAY);
			textView.setTextSize(14);
			textView.setBottom(2);
			if(statusText!=null){
				statusTextView.setText(statusText);
				statusTextView.setBackgroundColor(Color.WHITE);
				statusTextView.setTextColor(Color.GRAY);
				statusTextView.setPadding(10, 0, 0, 0);
				statusTextView.setTextSize(10);
			}
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
			mainLayout.addView(textView);
			if(statusText!=null)
				mainLayout.addView(statusTextView);
		}
		if(noReadMember){
			
			statusText = "none";
			if(statusText!=null){
				 statusTextView = new MyriadSemiboldTextView(this);
				 statusTextView.setLayoutParams(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				statusTextView.setText(statusText);
				statusTextView.setBackgroundColor(Color.WHITE);
				statusTextView.setTextColor(Color.GRAY);
				statusTextView.setTextSize(10);
				statusTextView.setPadding(10, 0, 0, 0);
			}
			mainLayout.addView(statusTextView);
		}
		noReadMember = true;
		textView = new MyriadSemiboldTextView(this);
		params.setMargins(0, 20, 0, 10);
		textView.setLayoutParams(params);
		textView.setPadding(10, 0, 0, 0);
		textView.setText("Delivered to");
//		textView.setBackgroundResource(R.drawable.round_rect);
		textView.setBackgroundColor(Color.GRAY);
		textView.setTextColor(Color.WHITE);
		mainLayout.addView(textView);
		
		for(String text:usersList){
			String tmpText = text;
				tmpText = hashMap.get(text);
				if(tmpText == null){
					if(text!=null && text.contains("_"))
						tmpText = "+"+text.substring(0, text.indexOf("_"));
				}
			textView = new MyriadSemiboldTextView(this);
			statusTextView = new MyriadSemiboldTextView(this);
			textView.setTag(text);
			textView.setPadding(10, 0, 0, 0);
			params.setMargins(0, 10, 0, 0);
			textView.setLayoutParams(params);
			statusTextView.setLayoutParams(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					//(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			 statusText = null;
			if(infoList!=null){
				MessageInfo info = infoList.get(text);
				if(info!=null){
					
					String messageStatus = info.getMessageStatus();
					if(messageStatus!=null){
						if(messageStatus.equals(Message.SeenState.recieved.ordinal()+"")){
							statusText = "";
							String messageDeliveryTime = info.getMessageDeliveryTime();
							if(messageDeliveryTime!=null && !messageDeliveryTime.equals("")){
								long time = Long.parseLong(messageDeliveryTime);
								calander.setTimeInMillis(time);
//								calander.set(Calendar.DATE, 1);
								SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
								String msgTime = format.format(calander.getTime());
	
//								final int date = calander.get(Calendar.DAY_OF_MONTH);
//								final int month = calander.get(Calendar.MONTH);
//								final int year = calander.get(Calendar.YEAR);
								int days = daysBetween(currentCalender, calander);
								
								if(days == 1){
									msgTime = "Yesterday "+msgTime;
								}else if(days>1 && days<7){
									format = new SimpleDateFormat("EEE,hh:mm aa");
									msgTime = format.format( calander.getTime());
								}else if(days>=7){
									format = new SimpleDateFormat("dd/MM/yy");
									msgTime = format.format( calander.getTime());
								}
								statusText = statusText+" "+msgTime;
							}
							
						}else if(messageStatus.equals(Message.SeenState.seen.ordinal()+"")){
//							statusText = "";
//							String messageSeenTime = info.getMessageSeenTime();
//							if(messageSeenTime!=null && !messageSeenTime.equals("")){
//								long time = Long.parseLong(messageSeenTime);
//								calander.setTimeInMillis(time);
////								calander.set(Calendar.DATE, 1);
////								calander.set(Calendar.MONTH, Calendar.MARCH);
//								SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
//								String msgTime = format.format(calander.getTime());
//	
////								final int date = calander.get(Calendar.DAY_OF_MONTH);
////								final int month = calander.get(Calendar.MONTH);
////								final int year = calander.get(Calendar.YEAR);
//								int days = daysBetween(currentCalender, calander);
//								
//								if(days == 1){
//									msgTime = "Yesterday "+msgTime;
//								}else if(days>1 && days<7){
//									format = new SimpleDateFormat("EEE,hh:mm aa");
//									msgTime = format.format( calander.getTime());
//								}else if(days>=7){
//									format = new SimpleDateFormat("dd/MM/yy");
//									msgTime = format.format( calander.getTime());
//								}
//								statusText = statusText+" "+msgTime;
//							}
							continue;
						}else{
							statusText = "sent";
							continue;
							}
					}
					
				}else if(selectedMessageId!=null){
					statusText = "sent";
					continue;
				}
			}
			textView.setText(tmpText);
//			textView.setBackgroundResource(R.drawable.round_rect);
			textView.setBackgroundColor(Color.WHITE);
			textView.setTextColor(Color.DKGRAY);
			textView.setPadding(10, 0, 0, 0);
			textView.setTextSize(14);
			if(statusText!=null){
				statusTextView.setText(statusText);
	//			statusTextView.setBackgroundResource(R.drawable.round_rect);
				statusTextView.setBackgroundColor(Color.WHITE);
				statusTextView.setTextColor(Color.GRAY);
				statusTextView.setPadding(10, 0, 0, 0);
				statusTextView.setTextSize(10);
			}
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
			mainLayout.addView(textView);
			if(statusText!=null)
				mainLayout.addView(statusTextView);
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
					statusView.setText(statusMessage);
					setPic(groupIconView);
					isProfileModified = true;
					break;
				}
				
		}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_edit_group:
			Intent intent = new Intent(this, CreateGroupScreen.class);
			intent.putExtra(Constants.IS_GROUP_INFO_UPDATE, true);		
			intent.putExtra(Constants.GROUP_NAME, displayName);		
			intent.putExtra(Constants.GROUP_UUID, groupUUID);
			intent.putExtra(Constants.GROUP_DISCRIPTION, statusMessage);
			 String groupPicId = iChatPref.getUserFileId(groupUUID);
			 if(groupPicId!=null)
				 intent.putExtra(Constants.GROUP_FILE_ID, groupPicId);
			 
			startActivityForResult(intent, 100);
			break;
		case R.id.id_group_icon:
			String file_path = (String) v.getTag();
			if(file_path != null)
			{
				 intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				if(file_path.startsWith("http://"))
					intent.setDataAndType(Uri.parse(file_path), "image/*");
				else
					intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
				GroupStausInfoScreen.this.startActivity(intent);
			}
			break;
		case R.id.id_leave_group_chat:
			if(service!=null){
				service.removeGroupPerson(groupUUID, SharedPrefManager.getInstance().getUserName());
				usersList.remove(SharedPrefManager.getInstance().getUserName());
				iChatPref.saveServerGroupState(groupUUID, GroupCreateTaskOnServer.SERVER_GROUP_NOT_UPDATED);//new GroupUpdateTaskOnServer().execute();
				iChatPref.removeUsersFromGroup(groupUUID, SharedPrefManager.getInstance().getUserName());
				finish();
			}
			break;
		case R.id.id_info:
			 intent = new Intent(this, ProfileScreen.class);
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
		case R.id.id_view_all_media:
			Toast.makeText(GroupStausInfoScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
			break;
		case R.id.id_group_notification:
			Toast.makeText(GroupStausInfoScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
			break;
		case R.id.id_mute_group_chat:
			Toast.makeText(GroupStausInfoScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
			break;
		}
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
}
