package com.superchat.ui;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.util.Base64;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatService;
import com.chat.sdk.ProfileUpdateListener;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.BroadCastDetailsModel;
import com.superchat.model.ErrorModel;
import com.superchat.model.GroupChatServerModel;
import com.superchat.model.GroupDetailsModel;
import com.superchat.model.LoginModel;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.GroupCreateTaskOnServer;
import com.superchat.utils.Log;
import com.superchat.utils.ProfilePicDownloader;
import com.superchat.utils.RTMediaPlayer;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.VoiceMediaHandler;
import com.superchat.widgets.MyriadSemiboldTextView;
import com.superchat.widgets.RoundedImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class GroupProfileScreen extends Activity implements OnClickListener, ProfileUpdateListener,VoiceMediaHandler,OnMenuItemClickListener{
	public final static String TAG = "GroupProfileScreen"; 
	private RelativeLayout adminLayout;
	private RelativeLayout ownerLayout;
	private LinearLayout addGroupParticipantLayout;
	private boolean isGroupAdmin;
	private boolean isGroupOwner;
	private String groupUUID;
	private String selectedMessageId;
	private String displayName = "";
	private String statusMessage = "";
	private String selectedMemberUserName;
	private String selectedUserdisplayName;
	private List<String> usersList;
	private List<String> addMemberFilterList;
	private TextView displayNameView;
	private TextView ownerName;
	private String ownerDislayName;
	private TextView title;
	private TextView backTitle;
	private TextView deleteGroupView;
	boolean isDeleteItem;
	private Calendar calander;
	Calendar currentCalender;
	private MyriadSemiboldTextView deleteBroadcastList;
	private TextView statusView;
	private TextView addMemberView;
	private TextView ownerView;
	private TextView adminView; 
	private TextView clearGroupChatView;
	private TextView activePollView;
	private TextView emailChatView;
	private TextView allMediaView;
	private TextView groupNotificationView;
	private TextView leaveGroupChatView;
	private ImageView editGroupView;
	private TextView muteGroupView;
	private ImageView groupIconView;
	private SharedPrefManager iChatPref;
	private Dialog memberOptionDialog; 
	private HashMap<String, String> hashMap;
//	private HashMap<String, MessageInfo> infoList;
	boolean isProfileModified;
	private ChatService service;
	private XMPPConnection connection;
	private ChatService messageService;
	boolean invitationEnable;
	ArrayList<String> inviters = null;
	LinearLayout mainLayout;
	private boolean isBroadCast;
	private boolean isOpenChannel;
	private boolean isMemberAddAllowed;
	ProgressBar progressBarView;
	public String groupOwnerName;
	public String fileId;
	public List<String> adminUserSet;
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    private TextView mediaCountView;
	private TextView docsCountView;
	private LinearLayout mediaScrollLayout;
	private LinearLayout docsScrollLayout;
	LinearLayout statusLayout;
	TextView leaveBtnView;
	
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
		setContentView(R.layout.group_info_screen);
		mediaScrollLayout = (LinearLayout)findViewById(R.id.id_media_scroll_view); 
		docsScrollLayout = (LinearLayout)findViewById(R.id.id_docs_scroll_view); 
		mediaCountView = (TextView)findViewById(R.id.id_media_count); 
		docsCountView = (TextView)findViewById(R.id.id_docs_count); 
		statusLayout = (LinearLayout)findViewById(R.id.linearlayout_status);
		calander = Calendar.getInstance(TimeZone.getDefault());
		currentCalender = Calendar.getInstance(TimeZone.getDefault());
		currentCalender.setTimeInMillis(System.currentTimeMillis());
		progressBarView = (ProgressBar)findViewById(R.id.id_loading);
		isProfileModified = false;
		title = (TextView)findViewById(R.id.id_group_info_title);
		backTitle = (TextView)findViewById(R.id.id_back_title);
		deleteGroupView = (TextView)findViewById(R.id.id_delete_group);
		deleteGroupView.setOnClickListener(this);
		displayNameView = (TextView)findViewById(R.id.id_group_name);
		ownerName = (TextView)findViewById(R.id.id_created_by);
		statusView = (TextView)findViewById(R.id.id_status_message); 
		groupIconView = (ImageView) findViewById(R.id.id_group_icon);
		editGroupView = (ImageView)findViewById(R.id.id_edit_group); 
		
		leaveBtnView = (TextView)findViewById(R.id.id_leave_btn);
		leaveBtnView.setOnClickListener(this);
		
		
		mDrawableBuilder = TextDrawable.builder()
                .beginConfig().toUpperCase()
            .endConfig()
            .round();
		
		
		deleteBroadcastList = (MyriadSemiboldTextView)findViewById(R.id.id_delete_broadcast_list); 
		editGroupView.setOnClickListener(this);
		groupIconView.setOnClickListener(this);
		addMemberView = (TextView)findViewById(R.id.id_add_member);
		ownerView = (TextView)findViewById(R.id.id_owner); 
		adminView = (TextView)findViewById(R.id.id_admin); 
		clearGroupChatView = (TextView)findViewById(R.id.id_clear_group_chat);
		activePollView = (TextView)findViewById(R.id.id_active_poll_view);
		leaveGroupChatView = (TextView)findViewById(R.id.id_leave_group_chat);
		adminLayout = (RelativeLayout)findViewById(R.id.id_admin_layout);
		addGroupParticipantLayout = (LinearLayout)findViewById(R.id.id_add_participants);
		TextView addParticipantsTextView = (TextView)findViewById(R.id.id_add_participants_txt);
		
		addGroupParticipantLayout.setVisibility(View.GONE);
		adminLayout.setVisibility(View.GONE);
		ownerLayout = (RelativeLayout)findViewById(R.id.id_owner_layout); 
		
		iChatPref = SharedPrefManager.getInstance();
		addGroupParticipantLayout.setOnClickListener(this);
		addMemberView.setOnClickListener(this);
		leaveGroupChatView.setOnClickListener(this);
		emailChatView = (TextView) findViewById(R.id.id_email_group_chat);
		emailChatView.setOnClickListener(this);

		allMediaView = (TextView) findViewById(R.id.id_view_all_media);
		allMediaView.setOnClickListener(this);
		deleteBroadcastList.setOnClickListener(this);
		groupNotificationView = (TextView) findViewById(R.id.id_group_notification);
		groupNotificationView.setOnClickListener(this);
		muteGroupView = (TextView) findViewById(R.id.id_mute_group_chat);
		muteGroupView.setOnClickListener(this);
		clearGroupChatView.setOnClickListener(this);
		activePollView.setOnClickListener(this);
		Bundle tmpBundle = getIntent().getExtras();
		if(!iChatPref.isDomainAdminORSubAdmin()){
			deleteGroupView.setVisibility(View.GONE);
			isDeleteItem = false;
			}
		if(tmpBundle!=null){
			isBroadCast = tmpBundle.getBoolean(Constants.BROADCAST, false);
			isOpenChannel = tmpBundle.getBoolean(Constants.OPEN_CHANNEL, false);
			if(!iChatPref.isDomainAdminORSubAdmin() || isBroadCast){
				deleteGroupView.setVisibility(View.GONE);
				isDeleteItem = true;
			}
			groupUUID = tmpBundle.get(Constants.CHAT_USER_NAME).toString();
			selectedMessageId = tmpBundle.getString(Constants.SELECTED_MESSAGE_ID,null);
			displayName = tmpBundle.get(Constants.CHAT_NAME).toString();
			isGroupAdmin = iChatPref.isAdmin(groupUUID, iChatPref.getUserName());
			isGroupOwner = iChatPref.isOwner(groupUUID, iChatPref.getUserName());
			usersList = getIntent().getExtras().getStringArrayList(Constants.GROUP_USERS);
			if(iChatPref.isPublicGroup(groupUUID)){
				deleteGroupView.setText(getString(R.string.delete_channel));
				isDeleteItem = true;
				}
			if(groupUUID!=null){
				statusMessage = iChatPref.getUserStatusMessage(groupUUID);
				
				}
//			if(!isGroupAdmin)
//				addMemberView.setVisibility(TextView.GONE);
//			else
//				adminLayout.setVisibility(View.VISIBLE);
			mainLayout = (LinearLayout)findViewById(R.id.id_group_members);
			String group_owner = iChatPref.getGroupOwnerName(groupUUID);

//			hashMap = (HashMap<String, String>)getIntent().getSerializableExtra(Constants.USER_MAP);
			hashMap = new HashMap<String, String>();
           
			if(isBroadCast){
				addParticipantsTextView.setText(getString(R.string.add_group_participants));
//				title.setText(getString(R.string.broadcast_info));
				title.setText(displayName);
				backTitle.setText(getString(R.string.broadcast_chat));
				emailChatView.setVisibility(View.GONE);
				clearGroupChatView.setVisibility(View.GONE);
				ownerLayout.setVisibility(View.GONE);
				adminLayout.setVisibility(View.GONE);
				deleteBroadcastList.setVisibility(View.GONE);
				activePollView.setVisibility(View.GONE);
				addGroupParticipantLayout.setVisibility(View.VISIBLE);
				
			}else if(isOpenChannel){
				if(group_owner != null && 
						!group_owner.equals(SharedPrefManager.getInstance().getUserName()) && 
						!group_owner.equalsIgnoreCase("You"))
					leaveBtnView.setVisibility(View.VISIBLE);
				else
					leaveBtnView.setVisibility(View.GONE);
//				title.setText(getString(R.string.channel_info));
				title.setText(displayName);
			}else
				title.setText(displayName);

		}
		if(displayName.contains("##$^##"))
			displayNameView.setText(displayName.substring(0, displayName.indexOf("##$^##")));
    	else
    		displayNameView.setText(displayName);
		if(groupOwnerName != null && ownerName!=null)
			ownerName.setText(getString(R.string.created_by) + " " +ownerDislayName);
		statusView.setText(statusMessage);
		if(statusMessage!=null && !statusMessage.trim().equals(""))
			statusLayout.setVisibility(View.VISIBLE);
		else
			statusLayout.setVisibility(View.GONE);
		
//		setPic(groupIconView, null, null, groupUUID);
		setPic(groupIconView,  groupUUID);
		memberOptionDialog = new Dialog(this);
		memberOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		memberOptionDialog.setContentView(R.layout.group_members_options_dialog);
		Window window = memberOptionDialog.getWindow();
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);
		editGroupView.setVisibility(TextView.GONE);
		isMemberAddAllowed = false;
		if(!isGroupAdmin){
			memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.GONE);
			memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.GONE);
		}else{
			isMemberAddAllowed = true;
			editGroupView.setVisibility(TextView.VISIBLE);
			memberOptionDialog.findViewById(R.id.id_remove_user).setOnClickListener(this);
		}
		if(!isGroupOwner){
			memberOptionDialog.findViewById(R.id.id_add_admin).setVisibility(View.GONE);
			memberOptionDialog.findViewById(R.id.id_add_admin_seprator).setVisibility(View.GONE);
		}else {
			isMemberAddAllowed = true;
			editGroupView.setVisibility(TextView.VISIBLE);
			memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.VISIBLE);
			memberOptionDialog.findViewById(R.id.id_add_admin).setOnClickListener(this);
		}

		if(isMemberAddAllowed || isBroadCast){
			editGroupView.setVisibility(TextView.VISIBLE);
			memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.VISIBLE);
			addGroupParticipantLayout.setVisibility(View.VISIBLE);
		}
		memberOptionDialog.findViewById(R.id.id_message_user).setOnClickListener(this);
		memberOptionDialog.findViewById(R.id.id_info).setOnClickListener(this);
		memberOptionDialog.findViewById(R.id.id_cancel).setOnClickListener(this);
//		 if(Build.VERSION.SDK_INT >= 11)
//			 new YourAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		    else
//		    	new YourAsyncTask().execute();
		if(groupUUID!=null && iChatPref.isGroupChat(groupUUID) && !iChatPref.isGroupMemberActive(groupUUID, iChatPref.getUserName())){
			editGroupView.setVisibility(View.GONE);
		}
		getServerGroupProfile(groupUUID);
//		if(!isBroadCast){
		 setAllMedia();
		 setAllDocs();
//		 }
	}
	public void showPopup(View v){
		 PopupMenu popup = new PopupMenu(this, v);
		 popup.setOnMenuItemClickListener(this);
		 //Commented for this release
		 popup.getMenu().add(0,0,0,getResources().getString(R.string.email_chat));
		 popup.getMenu().add(0,1,0,getResources().getString(R.string.clear_chat));
		 if(addGroupParticipantLayout.getVisibility() == View.VISIBLE)
			 popup.getMenu().add(0,5,0,getResources().getString(R.string.add_member));
		if(!isBroadCast){
			 if(iChatPref.isOwner(groupUUID, iChatPref.getUserName()))// if(!iChatPref.isDomainAdmin() || iChatPref.isPublicGroup(groupUUID) || isDeleteItem)
				 popup.getMenu().add(0,2,0,getResources().getString(R.string.delete_channel));
			 
			 if(leaveBtnView.getVisibility() == View.VISIBLE)
				 popup.getMenu().add(0,3,0,getResources().getString(R.string.leave_group));		
			 if(iChatPref.isMute(groupUUID))
				 popup.getMenu().add(0,4,0,getResources().getString(R.string.unmute));
			 else
				 popup.getMenu().add(0,4,0,getResources().getString(R.string.mute));
		}
		 popup.show();
	}

	public boolean onMenuItemClick(MenuItem item) {
	    switch (item.getItemId()) {
	        case 0: // email
	        	String selChat = "";
				ArrayList<String> textList = ChatDBWrapper.getInstance().getChatHistory(groupUUID);
				for(String msg:textList)
					selChat = selChat + msg + "\n";

				int listSize = textList.size();
				if (selChat != null && selChat.length() > 0 && !selChat.equals("")) {

					final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
					mailIntent.setType("text/plain");
					mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
					mailIntent.putExtra(Intent.EXTRA_TEXT, selChat.trim());
					final PackageManager pm = getPackageManager();
					final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
					ResolveInfo best = null;
					for (final ResolveInfo info : matches)
						if (info.activityInfo.packageName.endsWith(".gm") ||
								info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
					if (best != null)
						mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
					startActivity(mailIntent);
				}
	        	return true;
	        case 1: // clear
	        	showDialog(displayName,"Do you want to clear all messages of this chat?");
	        	return true;
	        case 2: // delete
	        	new GroupDeactivationTaskOnServer(groupUUID).execute();
	        return true;
	        case 3: // leave
	        	if(!isGroupOwner){
//					showDialog("Leave "+displayName,"Do you want to leave "+displayName+".", false);
					if(Build.VERSION.SDK_INT >= 11)
						new ChannelLeaveJoinOnInfo(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupUUID);
					else
						new ChannelLeaveJoinOnInfo(false).execute(groupUUID);
				}
	        	return true;
	        case 4:
	        		iChatPref.setMute(groupUUID, !iChatPref.isMute(groupUUID));
	        		if(iChatPref.isMute(groupUUID))
	        			Toast.makeText(this, "Group Mute!", Toast.LENGTH_SHORT).show();
	        		else
	        			Toast.makeText(this, "Group Unmute!", Toast.LENGTH_SHORT).show();
	        	return true;
	        case 5: // Add Member
	        	Intent intent = new Intent(this,EsiaChatContactsScreen.class);
				intent.putExtra(Constants.CHAT_TYPE, Constants.GROUP_USER_CHAT_CREATE);
				intent.putExtra(Constants.IS_GROUP_INVITATION, true);
				intent.putExtra(Constants.GROUP_NAME, groupUUID);
				intent.putExtra(Constants.GROUP_DISCRIPTION, statusMessage);
				if(isBroadCast)
					intent.putExtra(Constants.BROADCAST, true);
				ArrayList<String> tmpList = new ArrayList<String>();
				for(String tmp: usersList){
					if(tmp!=null && !tmp.equals(""))
					tmpList.add(tmp.split(":")[0]);
				}
				intent.putStringArrayListExtra(Constants.GROUP_USERS, new ArrayList<String>(tmpList));
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//				finish();
				startActivityForResult(intent, 200);
	        default:
	            return false;
	        	}
	    }
private class YourAsyncTask extends AsyncTask<String, Void, String> {
	ProgressDialog dialog;
	List<String> displayList = new ArrayList<String>();
		protected void onPreExecute() {
			
			dialog = ProgressDialog.show(GroupProfileScreen.this, "","Loading. Please wait...", true);

//			progressBarView.setVisibility(ProgressBar.VISIBLE);
			 super.onPreExecute();
	     }
	     protected String doInBackground(String... args) {
	    	 displayList = convertNames(usersList);		
	    	 if(selectedMessageId!=null && !selectedMessageId.equals("")){
//	    		 Cursor cursor = ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupOrBroadCastUsersStatus(selectedMessageId);
//	    		 if(cursor!=null){
//	    			 infoList = new HashMap<String, MessageInfo>();
//	    			 try{
//	    				 if (cursor != null && cursor.moveToFirst()) {
//	    						do {
//	    							String messageUserName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD));
//	    							String messageStatus = cursor.getString(cursor.getColumnIndex(ChatDBConstants.SEEN_FIELD));
//	    							String messageDeliveryTime = cursor.getString(cursor.getColumnIndex(ChatDBConstants.DELIVER_TIME_FIELD));
//	    							String messageSeenTime = cursor.getString(cursor.getColumnIndex(ChatDBConstants.SEEN_TIME_FIELD));
//	    							if(messageUserName!=null && !messageUserName.equals("")){
//	    								MessageInfo messageInfo = new MessageInfo();
//	    								messageInfo.setMessageDeliveryTime(messageDeliveryTime);
//	    								messageInfo.setMessageSeenTime(messageSeenTime);
//	    								messageInfo.setMessageUserName(messageUserName);
//	    								messageInfo.setMessageStatus(messageStatus);
//	    								if(infoList!=null)
//	    									infoList.put(messageUserName, messageInfo);
//	    							}
////	    							Log.d("ChatDBWrapper",messageId+" getGroupOrBroadCastUsersStatus count: " + cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID)));
//	    						} while (cursor.moveToNext());
//	    					}
//	    			 }catch(Exception e){}
//	    			 finally {
//					 cursor.close();
//	    			 cursor = null;
//	    			 }
//	    		 }
	    	}
	        return null;
	     }

	     protected void onPostExecute(String result) {
	    	 addTextView(mainLayout, displayList);
	    	 progressBarView.setVisibility(ProgressBar.GONE);
			 if(isMemberAddAllowed || isBroadCast){
				 isGroupAdmin = iChatPref.isAdmin(groupUUID, iChatPref.getUserName());
				 if(isMemberAddAllowed && (isGroupOwner || isGroupAdmin)){
					 editGroupView.setVisibility(TextView.VISIBLE);
					 addGroupParticipantLayout.setVisibility(View.VISIBLE);
					 editGroupView.setOnClickListener(GroupProfileScreen.this);
				 }else if(isBroadCast){
					 editGroupView.setVisibility(TextView.VISIBLE);
					 addGroupParticipantLayout.setVisibility(View.VISIBLE);
					 editGroupView.setOnClickListener(GroupProfileScreen.this);
				 }
				 memberOptionDialog.findViewById(R.id.id_remove_user).setOnClickListener(GroupProfileScreen.this);
				 if(groupUUID!=null && iChatPref.isGroupChat(groupUUID) && !iChatPref.isGroupMemberActive(groupUUID, iChatPref.getUserName())){
						editGroupView.setVisibility(View.GONE);
					}
				 if(groupOwnerName!=null && groupOwnerName.equals(iChatPref.getUserName()) && !isBroadCast){
						deleteGroupView.setVisibility(View.VISIBLE);
						isDeleteItem = true;
					}else
						isDeleteItem = false;
			 }else if(iChatPref.isDomainAdminORSubAdmin() && !iChatPref.isAdmin(groupUUID, iChatPref.getUserName())){
				 editGroupView.setVisibility(TextView.GONE);
				 addGroupParticipantLayout.setVisibility(View.GONE);
			 }
	    	 dialog.cancel();
				super.onPostExecute(result);
	     }
	 }
//class MessageInfo{
//	String messageUserName;
//	String messageRecipentName;
//	String messageDeliveryTime;
//	String messageSeenTime;
//	String messageStatus;
//	
//	MessageInfo(){
//		
//	}
//	public String getMessageStatus() {
//		return messageStatus;
//	}
//	public void setMessageStatus(String messageStatus) {
//		this.messageStatus = messageStatus;
//	}
//	public String getMessageUserName() {
//		return messageUserName;
//	}
//	public void setMessageUserName(String messageUserName) {
//		this.messageUserName = messageUserName;
//	}
//	public String getMessageRecipentName() {
//		return messageRecipentName;
//	}
//	public void setMessageRecipentName(String messageRecipentName) {
//		this.messageRecipentName = messageRecipentName;
//	}
//	public String getMessageDeliveryTime() {
//		return messageDeliveryTime;
//	}
//	public void setMessageDeliveryTime(String messageDeliveryTime) {
//		this.messageDeliveryTime = messageDeliveryTime;
//	}
//	public String getMessageSeenTime() {
//		return messageSeenTime;
//	}
//	public void setMessageSeenTime(String messageSeenTime) {
//		this.messageSeenTime = messageSeenTime;
//	}
//	
//	 
//}
private boolean isUserContains(List<String> tmpList, String tmpMember){
	if(tmpList!=null && !tmpList.isEmpty())
		for(String tmp: tmpList){
			if(tmp!=null && tmp.contains(tmpMember))
				return true;
		}
	return false;
}
private void getServerGroupProfile(String groupName){
	final Context context = this;
	
	AsyncHttpClient client = new AsyncHttpClient();
	String path = "";
	String query = groupName;
	try {
		 query = URLEncoder.encode(query, "utf-8");
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	if(isBroadCast)
		path = Constants.SERVER_URL+"/tiger/rest/bcgroup/detail?groupName="+query+"&nameNeeded=true";
	else
		path = Constants.SERVER_URL+"/tiger/rest/group/detail?groupName="+query+"&nameNeeded=true";
	 client = SuperChatApplication.addHeaderInfo(client,true);
	client.get(path,
			null, new AsyncHttpResponseHandler() {
		ProgressDialog dialog = null;

		@Override
		public void onStart() {
			dialog = ProgressDialog.show(GroupProfileScreen.this, "","Loading. Please wait...", true);
			Log.d(TAG, "AsyncHttpClient onStart: ");
		}

		@Override
		public void onSuccess(int arg0, String arg1) {
			Log.d(TAG, "AsyncHttpClient onSuccess: "
					+ arg1);
			if(arg1 == null)
				return;

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
				if(objUserModel.displayName!=null && !objUserModel.displayName.equals("")){
					iChatPref.saveBroadCastDisplayName(groupUUID, objUserModel.displayName);
				}
				if(objUserModel.description!=null && !objUserModel.description.equals("")){
					iChatPref.saveUserStatusMessage(groupUUID, objUserModel.description);
				}
				if(objUserModel.fileId!=null && !objUserModel.fileId.equals("")){
					iChatPref.saveUserFileId(groupUUID, objUserModel.fileId);
				}
				addMemberFilterList = new ArrayList<String>();
//				if(objUserModel.activatedUserSet!=null){
//					usersList = objUserModel.activatedUserSet;
//					addMemberFilterList.addAll(usersList);
//				}
				
//				if(objUserModel.adminUserSet!=null)
//				usersList = objUserModel.adminUserSet;

//				if(objUserModel.adminUserSet!=null){
//					for(String str:objUserModel.adminUserSet)
//						usersList.add(str);
//				}
				if(objUserModel.memberUserSet != null){
					for(String item: objUserModel.memberUserSet){
						for(String str:objUserModel.activatedUserSet){
							if(item.contains(str) && !item.equals(groupOwnerName))
								usersList.add(item);
						}
					}
				}
				if(objUserModel.adminUserSet != null){
					for(String item: objUserModel.adminUserSet){
						for(String str:objUserModel.activatedUserSet){
							if(item.contains(str) && !item.equals(groupOwnerName)){
								usersList.add(item);								
								}
						}
					}
				}

//				if(objUserModel.userName!=null && !objUserModel.userName.equals(""))
//					usersList.add(objUserModel.userName);
				groupOwnerName = objUserModel.userName;
				ownerDislayName = SharedPrefManager.getInstance().getUserServerDisplayName(groupOwnerName);
				fileId = objUserModel.fileId;
				adminUserSet = objUserModel.adminUserSet;
//				addMemberFilterList.addAll(usersList);
				if(objUserModel.memberUserSet!=null && !objUserModel.memberUserSet.isEmpty())
					addMemberFilterList.addAll(objUserModel.memberUserSet);
				if(objUserModel.adminUserSet!=null && !objUserModel.adminUserSet.isEmpty())
					addMemberFilterList.addAll(objUserModel.adminUserSet);
				if(objUserModel.activatedUserSet!=null && !objUserModel.activatedUserSet.isEmpty())
					addMemberFilterList.addAll(objUserModel.activatedUserSet);
				isMemberAddAllowed = true;
				iChatPref.saveGroupMemberCount(groupUUID, String.valueOf(usersList.size()));
				if(usersList != null){
				 if(Build.VERSION.SDK_INT >= 11)
					 new YourAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				    else
				    	new YourAsyncTask().execute();
				}
			}
			}else if(arg1 != null){
				GroupDetailsModel objUserModel = gson.fromJson(arg1, GroupDetailsModel.class);
				if (arg1 == null || arg1.contains("error") || objUserModel==null){
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					return;
				}
						
			if(objUserModel!=null){
				if(objUserModel.displayName!=null && !objUserModel.displayName.equals("")){
					iChatPref.saveGroupDisplayName(groupUUID, objUserModel.displayName);
				}
				if(objUserModel.type!=null && objUserModel.type.equals("public")){
					iChatPref.saveGroupTypeAsPublic(groupUUID, true);
				}
				if(objUserModel.description!=null && !objUserModel.description.equals("")){
					iChatPref.saveUserStatusMessage(groupUUID, objUserModel.description);
				}
				if(objUserModel.fileId!=null && !objUserModel.fileId.equals("")){
					iChatPref.saveUserFileId(groupUUID, objUserModel.fileId);
				}
				if(usersList==null || !usersList.isEmpty()){
					usersList = new ArrayList<String>();
				}
//				usersList = objUserModel.activatedUserSet;
//				if(objUserModel.userName!=null && !objUserModel.userName.equals(""))
//					usersList.add(objUserModel.userName);

				groupOwnerName = objUserModel.userName;
				if(objUserModel.ownerDisplayName!=null && !objUserModel.ownerDisplayName.equals("")){
					iChatPref.saveUserServerName(objUserModel.userName, objUserModel.ownerDisplayName);
				}
				fileId = objUserModel.fileId;
				adminUserSet = objUserModel.adminUserSet;
				usersList.add(groupOwnerName);
//				usersList.addAll(adminUserSet);
				addMemberFilterList = new ArrayList<String>();
				
//				if(objUserModel.activatedUserSet!=null){
//					usersList = objUserModel.activatedUserSet;
//					addMemberFilterList.addAll(usersList);
//				}
				
				if(objUserModel.memberUserSet != null){
					for(String item: objUserModel.memberUserSet){
						for(String str:objUserModel.activatedUserSet){
							if(item.contains(str) && !item.equals(groupOwnerName))
								usersList.add(item);
						}
					}
				}
				isMemberAddAllowed = false;
				if(objUserModel.adminUserSet != null){
					for(String item: objUserModel.adminUserSet){
						for(String str:objUserModel.activatedUserSet){
							if(item.contains(str) && !item.equals(groupOwnerName))
								usersList.add(item);
							if(item.contains(SharedPrefManager.getInstance().getUserName())){
								isMemberAddAllowed = true;
								iChatPref.saveUserGroupInfo(groupUUID, SharedPrefManager.getInstance().getUserName(), SharedPrefManager.GROUP_ADMIN_INFO, true);
							}
						}
					}
				}
				if(!isMemberAddAllowed)
					iChatPref.saveUserGroupInfo(groupUUID, SharedPrefManager.getInstance().getUserName(), SharedPrefManager.GROUP_ADMIN_INFO, false);

//				addMemberFilterList.addAll(usersList);
				if(objUserModel.memberUserSet!=null && !objUserModel.memberUserSet.isEmpty())
					addMemberFilterList.addAll(objUserModel.memberUserSet);
				if(objUserModel.adminUserSet!=null && !objUserModel.adminUserSet.isEmpty())
					addMemberFilterList.addAll(objUserModel.adminUserSet);
				if(objUserModel.activatedUserSet!=null && !objUserModel.activatedUserSet.isEmpty())
					addMemberFilterList.addAll(objUserModel.activatedUserSet);
				
				if(objUserModel.adminUserSet!=null){
					if(groupOwnerName.contains(SharedPrefManager.getInstance().getUserName()) || isUserContains(objUserModel.adminUserSet,SharedPrefManager.getInstance().getUserName()))
					isMemberAddAllowed = true;
				}
				if(groupOwnerName!=null && !groupOwnerName.equals("")){
					iChatPref.saveUserGroupInfo(groupUUID, groupOwnerName, SharedPrefManager.GROUP_OWNER_INFO, true);
//					iChatPref.saveUserGroupInfo(groupUUID,groupOwnerName,SharedPrefManager.GROUP_ACTIVE_INFO,true);
				}
				iChatPref.saveGroupMemberCount(groupUUID, String.valueOf(usersList.size()));
				ChatDBWrapper.getInstance(SuperChatApplication.context).updateTotalUserCountByGroupName(groupUUID, usersList.size());	
				
				if(!iChatPref.getUserName().equals(groupOwnerName) && iChatPref.isPublicGroup(groupUUID)){
					if(objUserModel.displayName != null && 
							!objUserModel.displayName.equals(iChatPref.getUserName()) && 
							!objUserModel.displayName.equalsIgnoreCase("You"))
						leaveBtnView.setVisibility(View.VISIBLE);
				}
				
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
			super.onFailure(arg0, arg1);
			Log.d(TAG, "AsyncHttpClient onFailure: "+ arg1);
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			//						showDialog("Please try again later.");
			Toast.makeText(GroupProfileScreen.this, getString(R.string.lbl_server_not_responding), Toast.LENGTH_LONG).show();
			finish();
		}
	});
}
	private List<String> convertNames(List<String> arrayList){
		ArrayList<String> displayList = new ArrayList<String>();
		hashMap = DBWrapper.getInstance().getUsersDisplayNameList(arrayList);
		for(String tmp:arrayList){
			String value = tmp;//"919324268448_profile:mahesh sonker:1_1_7_G_I_I3_ebygzgauu8"
			String[] data = null;//0 => username, DisplayName, FileID
			if(tmp.indexOf(':') != -1){
				data = tmp.split(":");
				if(data != null && data.length >= 2){
					value = data[0];
					if(iChatPref.getUserName().equals(data[0])){
						value = "You";
						hashMap.put(data[0], value);
						}
					if(value!=null && value.contains("#786#")){
						value = value.substring(0, value.indexOf("#786#"));
					}
					if(value.equals("You"))
						displayList.add(value);
					else
						displayList.add(data[1]);
					//Store Name and File ID in preference
					if(iChatPref.getUserServerName(data[0]) != null && iChatPref.getUserServerName(data[0]).equals(data[0])){
						iChatPref.saveUserServerName(data[0], data[1]);
						if(data.length == 3 && !data[2].equalsIgnoreCase("null"))
							iChatPref.saveUserFileId(data[0], data[2]);
					}
//					//Save number in DB
//					String number = data[0].substring(0, data[0].indexOf('_'));
//					updateContactcInDB(data[0], data[1], number);
				}
			}else{
				if(iChatPref.getUserName().equals(tmp)){
					value = "You";
					hashMap.put(tmp, value);
					}
				if(value!=null && value.contains("#786#")){
	//				String tUserName = value.substring(value.indexOf("#786#")+"#786#".length());
					value = value.substring(0, value.indexOf("#786#"));
	//				hashMap.put(tUserName, value);
				}
				displayList.add(value);
			}
		}
		Collections.sort(displayList, String.CASE_INSENSITIVE_ORDER);
		return displayList;
	}
	private void updateContactcInDB(String username, String dislay_name, String mobile_number){
		try{
			String number = DBWrapper.getInstance().getContactNumber(username);
			if(number!=null && !number.equals(""))
				return;
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(DatabaseConstants.USER_NAME_FIELD,username);
		contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(2));
		contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,mobile_number);	
		int id = username.hashCode();
		if (id < -1)
			id = -(id);
		contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
		contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
		contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, dislay_name);
		contentvalues.put(DatabaseConstants.CONTACT_TYPE_FIELD, "");
		contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,Integer.valueOf(0));
		contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
		contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
		contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
		contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, mobile_number);
		if(!username.equalsIgnoreCase(SharedPrefManager.getInstance().getUserName()))
			DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);

		}catch(Exception ex){
			
		}
	}
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((ChatService.MyBinder) binder).getService();
			Log.d("Service","Connected");
//			connection=service.getconnection();
			connection=service.getconnection();
			if (service != null) {
				service.setProfileUpdateListener(GroupProfileScreen.this);
            }
		}

		public void onServiceDisconnected(ComponentName className) {
//			connection=null;
//			if (service != null) {
//				service.setProfileUpdateListener(null);
//            }
			service = null;
		}
	};
	@Override
	protected void onResume() {
		super.onResume();
		try{
			bindService(new Intent(this, ChatService.class), mConnection, Context.BIND_AUTO_CREATE);
		}catch(Exception e){}
		if(SharedPrefManager.getInstance().isBroadCast(groupUUID) && SharedPrefManager.getInstance().getBroadCastDisplayName(groupUUID) != null)
			title.setText(SharedPrefManager.getInstance().getBroadCastDisplayName(groupUUID));
	}
	protected void onPause() {
		try{
		unbindService(mConnection);
		}catch(Exception e){}
		super.onPause();
	}
	public static int daysBetween(Calendar startDate, Calendar endDate) {  
	    return Math.abs(startDate.get(Calendar.DAY_OF_MONTH)-endDate.get(Calendar.DAY_OF_MONTH));  
	}
	private void addTextView(LinearLayout mainLayout, List<String> list){
		boolean isAnyAdmin = false;
		boolean isAnyOwner = false;
		String[] data = null;
		if(usersList.size() > 0){
			((TextView)findViewById(R.id.id_participants_lbl)).setVisibility(View.VISIBLE);
			if(((TextView)findViewById(R.id.id_participants_count)) != null)
				((TextView)findViewById(R.id.id_participants_count)).setText(""+iChatPref.getGroupMemberCount(groupUUID));
		}
		for(String text:usersList){
			String tmpText = text;
			isAnyOwner = isAnyAdmin = false;
				tmpText = hashMap.get(text);
				if(tmpText == null){
					if(text.indexOf(':') != -1){
						data = text.split(":");
						if(data != null && data.length >= 2)
							tmpText = data[1];
					}
				}
				if(tmpText == null){
					tmpText = SharedPrefManager.getInstance().getUserServerName(text);
					if(tmpText == null || (tmpText != null && (tmpText.equals(text) || tmpText.equalsIgnoreCase("null"))))
						tmpText = "Superchatter";
				}else if(tmpText != null && tmpText.equalsIgnoreCase("null"))
					tmpText = "Superchatter";
				else{//tmpText not null
					//Check if name is not saved in shared pref , then save
					if(iChatPref.getUserServerName(text).equals(text))
						iChatPref.saveUserServerName(text, tmpText);
				}
				((TextView)findViewById(R.id.id_is_owner)).setVisibility(View.GONE);
				((TextView)findViewById(R.id.id_is_admin)).setVisibility(View.GONE);
				if(iChatPref.isOwner(groupUUID, text)){
					isAnyOwner = true;
					ownerDislayName = tmpText;
					iChatPref.saveGroupOwnerName(groupUUID,ownerDislayName);
				}else if(adminUserSet != null && isUserContains(adminUserSet,text)){
					isAnyAdmin = true;
					
				}
			MyriadSemiboldTextView textView = new MyriadSemiboldTextView(this);
			MyriadSemiboldTextView statusTextView = new MyriadSemiboldTextView(this);
			RelativeLayout relativeLayout = (RelativeLayout)getLayoutInflater().inflate(R.layout.info_cell, null);
			ImageView profile = (ImageView) relativeLayout.findViewById(R.id.contact_icon);
			ImageView profile_default = (ImageView) relativeLayout.findViewById(R.id.contact_icon_default);
//			textView.setTag(text);
			relativeLayout.setTag(text);
			textView.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			statusTextView.setLayoutParams(LayoutHelper.createRelative(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_VERTICAL, 0, 0, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT));
					//(new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			String statusText = null;

			if(isAnyOwner){
//				textvalue += " (Owner)";
				if(isBroadCast)
					((TextView)relativeLayout.findViewById(R.id.user_type)).setText(getString(R.string.bcst_owner));
				else if(isOpenChannel)
					((TextView)relativeLayout.findViewById(R.id.user_type)).setText(getString(R.string.channel_owner));
				else
					((TextView)relativeLayout.findViewById(R.id.user_type)).setText(getString(R.string.grp_owner));
			}
			else if(isAnyAdmin){
				if(isBroadCast)
					((TextView)relativeLayout.findViewById(R.id.user_type)).setText(getString(R.string.bcst_admin));
				else  if(isOpenChannel)
					((TextView)relativeLayout.findViewById(R.id.user_type)).setText(getString(R.string.channel_admin));
				else
					((TextView)relativeLayout.findViewById(R.id.user_type)).setText(getString(R.string.grp_admin));
			}else{
				((TextView)relativeLayout.findViewById(R.id.user_type)).setVisibility(View.GONE);
			}
			((TextView)relativeLayout.findViewById(R.id.id_contact_status)).setText(iChatPref.getUserStatusMessage(text));
			((TextView)relativeLayout.findViewById(R.id.id_contact_name)).setText(tmpText);
			textView.setTextColor(Color.DKGRAY);
			textView.setTextSize(18);
			textView.setTextColor(getResources().getColor(R.color.darkest_gray));
			textView.setBottom(2);
			
			//Set profile pics
			
			if(text.indexOf(":") != -1)
				text = text.substring(0, text.indexOf(":"));
			profile_default.setTag(text);
			setPic(profile, profile_default, tmpText, text);
			
			relativeLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (memberOptionDialog != null && !memberOptionDialog.isShowing()) {
						selectedMemberUserName = ((RelativeLayout)v).getTag().toString();
						selectedUserdisplayName =  ((TextView)v.findViewById(R.id.id_contact_name)).getText().toString();
						if(selectedMemberUserName.equals(iChatPref.getUserName()))
							return;
						//If Logged In user is owner - 
						if(groupOwnerName != null && groupOwnerName.equals(iChatPref.getUserName())){
							//Check if selected user is Admin
							memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.VISIBLE);
							memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.VISIBLE);
							if(adminUserSet != null && isUserContains(adminUserSet,selectedMemberUserName)){//Remove as admin
								memberOptionDialog.findViewById(R.id.id_add_admin).setVisibility(View.GONE);
								memberOptionDialog.findViewById(R.id.id_add_admin_seprator).setVisibility(View.GONE);
								memberOptionDialog.findViewById(R.id.id_remove_admin).setVisibility(View.VISIBLE);
								memberOptionDialog.findViewById(R.id.id_remove_admin_seprator).setVisibility(View.VISIBLE);
								memberOptionDialog.findViewById(R.id.id_remove_admin).setOnClickListener(GroupProfileScreen.this);
							}else{//Make Admin
								memberOptionDialog.findViewById(R.id.id_add_admin).setVisibility(View.VISIBLE);
								memberOptionDialog.findViewById(R.id.id_add_admin_seprator).setVisibility(View.VISIBLE);
								memberOptionDialog.findViewById(R.id.id_add_admin).setOnClickListener(GroupProfileScreen.this);
								memberOptionDialog.findViewById(R.id.id_remove_admin).setVisibility(View.GONE);
								memberOptionDialog.findViewById(R.id.id_remove_admin_seprator).setVisibility(View.GONE);
							}
							
						}else if(adminUserSet != null && isUserContains(adminUserSet,iChatPref.getUserName())){
							if(!isUserContains(adminUserSet,selectedMemberUserName) && !groupOwnerName.equals(selectedMemberUserName)){
								memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.VISIBLE);
								memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.VISIBLE);
//								memberOptionDialog.findViewById(R.id.id_add_admin).setVisibility(View.VISIBLE);
//								memberOptionDialog.findViewById(R.id.id_add_admin_seprator).setVisibility(View.VISIBLE);
//								memberOptionDialog.findViewById(R.id.id_add_admin).setOnClickListener(GroupProfileScreen.this);
								memberOptionDialog.findViewById(R.id.id_remove_admin).setVisibility(View.GONE);
								memberOptionDialog.findViewById(R.id.id_remove_admin_seprator).setVisibility(View.GONE);
							}else{
								memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.GONE);
								memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.GONE);
							}
						}else{
							memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.GONE);
							memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.GONE);
						}
//						if(!selectedMemberUserName.equals(iChatPref.getUserName())){
//							if(groupOwnerName!=null && !groupOwnerName.equals(selectedMemberUserName)
//									&& (adminUserSet == null  ||  !adminUserSet.contains(selectedMemberUserName))){
//								memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.VISIBLE);
//								memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.VISIBLE);
//								memberOptionDialog.findViewById(R.id.id_add_remove_admin).setVisibility(View.VISIBLE);
//								memberOptionDialog.findViewById(R.id.id_add_remove_admin_seprator).setVisibility(View.VISIBLE);
//							}else{
//								memberOptionDialog.findViewById(R.id.id_remove_user_seprator).setVisibility(View.GONE);
//								memberOptionDialog.findViewById(R.id.id_remove_user).setVisibility(View.GONE);
//							}
//						}
						if(ChatService.xmppConectionStatus)
							memberOptionDialog.show();
						else 
							showDialog("Not able to connect with server. Please try after sometime.");
					}
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
			
//			if(statusText!=null)
//				mainLayout.addView(statusTextView);
		}
		if(ownerDislayName != null && ownerName!=null){
			ownerName.setText(getString(R.string.created_by) + " " + ownerDislayName);
		}
//		if(!isAnyAdmin){
//			adminLayout.setVisibility(TextView.GONE);
//		}
	}
	private void setPic(ImageView view, ImageView view_default, String displayName, String id){
		Log.i(TAG, "User ID : "+id);
		String groupPicId = iChatPref.getUserFileId(id); // 1_1_7_G_I_I3_e1zihzwn02
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		if (bitmap != null) {
			if(view_default != null)
				view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			view.setImageBitmap(bitmap);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			view.setTag(filename);
		}else if(groupPicId!=null && groupPicId.trim().length() > 0  && !groupPicId.equals("clear")){
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
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
//				view.setImageResource(R.drawable.group_icon);
				(new ProfilePicDownloader()).download(Constants.media_get_url+groupPicId+".jpg", (RoundedImageView)view, null);
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
			if(groupPicId!=null && groupPicId.trim().length() > 0 && !groupPicId.equals("clear")){
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
	public void onBackPressed(){
		if(isPollResultPage){
			isPollResultPage = false;
			poll.dismiss();
			return;
		}
		super.onBackPressed();
		setResult(RESULT_OK, new Intent(this,ChatListScreen.class));
		finish();
	}
	public void onBackClick(View view) {
		setResult(RESULT_OK, new Intent(this,ChatListScreen.class));
		finish();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		
			if (resultCode == RESULT_OK)
				switch (requestCode) {
				case 100:
					if(isBroadCast){
						statusMessage = iChatPref.getUserStatusMessage(groupUUID);
						displayName = iChatPref.getBroadCastDisplayName(groupUUID);
					}else{
						statusMessage = iChatPref.getUserStatusMessage(groupUUID);
						displayName = iChatPref.getGroupDisplayName(groupUUID);
					}
					if(displayName.contains("##$^##"))
						displayNameView.setText(displayName.substring(0, displayName.indexOf("##$^##")));
			    	else
			    		displayNameView.setText(displayName);
					//ownerName
					if(groupOwnerName != null)
						ownerName.setText(getString(R.string.created_by) + " " + ownerDislayName);
					statusView.setText(statusMessage);
					if(statusMessage!=null && !statusMessage.trim().equals(""))
						statusLayout.setVisibility(View.VISIBLE);
					else
						statusLayout.setVisibility(View.GONE);
					setPic(groupIconView, null, null, groupUUID);
					isProfileModified = true;
					break;
					case 200:
						restartActivity(this);
				}
				
		}
	public void restartActivity(Activity activity) {
		if (Build.VERSION.SDK_INT >= 11) {
			activity.recreate();
		} else {
			activity.finish();
			activity.startActivity(getIntent());
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_leave_btn:
			if(!isGroupOwner){
//				showDialog("Leave "+displayName,"Do you want to leave "+displayName+".", false);
				if(Build.VERSION.SDK_INT >= 11)
					new ChannelLeaveJoinOnInfo(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupUUID);
				else
					new ChannelLeaveJoinOnInfo(false).execute(groupUUID);
			}
		break;

		case R.id.id_edit_group:
			if(!ChatService.xmppConectionStatus){
				showDialog("Not able to connect with server. Please try after sometime.");
				break;
			}
			 if(isBroadCast){
				 Intent intent = new Intent(this, CreateBroadCastScreen.class);
					intent.putExtra(Constants.IS_GROUP_INFO_UPDATE, true);		
					intent.putExtra(Constants.GROUP_NAME, displayName);		
					intent.putExtra(Constants.GROUP_UUID, groupUUID);
					intent.putExtra(Constants.GROUP_DISCRIPTION, statusMessage);
					 String groupPicId = iChatPref.getUserFileId(groupUUID);
					 if(groupPicId!=null)
						 intent.putExtra(Constants.GROUP_FILE_ID, groupPicId);
						 intent.putExtra(Constants.BROADCAST, true);
					 
					startActivityForResult(intent, 100);
			 }else{
				 	Intent intent = new Intent(this, CreateGroupScreen.class);
					intent.putExtra(Constants.IS_GROUP_INFO_UPDATE, true);		
					intent.putExtra(Constants.GROUP_NAME, displayName);		
					intent.putExtra(Constants.GROUP_UUID, groupUUID);
					intent.putExtra(Constants.GROUP_DISCRIPTION, statusMessage);
					 String groupPicId = iChatPref.getUserFileId(groupUUID);
					 if(groupPicId!=null)
						 intent.putExtra(Constants.GROUP_FILE_ID, groupPicId);
						 intent.putExtra(Constants.BROADCAST, false);
					 
					startActivityForResult(intent, 100);
			 }
			
//			finish();
			break;
		case R.id.id_group_icon:
			String file_path = (String) v.getTag();
			if(file_path != null)
			{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				if(file_path.startsWith("http://"))
					intent.setDataAndType(Uri.parse(file_path), "image/*");
				else
					intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
				GroupProfileScreen.this.startActivity(intent);
			}
			break;
		case R.id.id_delete_group:
			new GroupDeactivationTaskOnServer(groupUUID).execute();
			break;
		case R.id.id_delete_broadcast_list:
			//@TODO
			//Implement delete broadcast list code here
			Log.i(TAG, "onClick : deleteBroadcastList button clicked");
			break;
		case R.id.id_clear_group_chat:
			showDialog(displayName,"Clear all messages in '"+displayName+"'.");
//			ChatDBWrapper.getInstance().deleteRecentUserChatByUserName(groupUUID);
//			saveMessage(displayName, groupUUID,"All conversations are cleared.");
//			finish();
			break;
		case R.id.id_active_poll_view:
			HashMap<String, String> map = getPollForGroup(groupUUID);
				if(map != null){
					if(ChatListScreen.openedPollID == null)
						ChatListScreen.openedPollID = getActivePollID();
					String data = (String) map.get(ChatListScreen.openedPollID);
				if(data != null) {
					showPoll(data, true);
				}
			}else
				Toast.makeText(GroupProfileScreen.this, getString(R.string.no_active_polls), Toast.LENGTH_SHORT).show();
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
		case R.id.id_add_admin://This is basically add
			if(memberOptionDialog!=null){
				memberOptionDialog.cancel();
				ArrayList<String> removeList = new ArrayList<String>();
				removeList.add(selectedMemberUserName);
				if(isBroadCast)
					new GroupAddAsAdminTaskOnServer(groupUUID, SharedPrefManager.getInstance().getBroadCastDisplayName(groupUUID), removeList).execute();
				else
					new GroupAddAsAdminTaskOnServer(groupUUID, SharedPrefManager.getInstance().getGroupDisplayName(groupUUID), removeList).execute();
			}
			break;
		case R.id.id_remove_admin:
			if(memberOptionDialog!=null){
				memberOptionDialog.cancel();
				ArrayList<String> removeList = new ArrayList<String>();
				removeList.add(selectedMemberUserName);
				if(isBroadCast)
					new GroupRemoveAsAdminTaskOnServer(groupUUID, SharedPrefManager.getInstance().getBroadCastDisplayName(groupUUID), removeList).execute();
				else
					new GroupRemoveAsAdminTaskOnServer(groupUUID, SharedPrefManager.getInstance().getGroupDisplayName(groupUUID), removeList).execute();
			}
			break;
		case R.id.id_remove_user:
			if(memberOptionDialog!=null){
				memberOptionDialog.cancel();
				ArrayList<String> removeList = new ArrayList<String>();
				removeList.add(selectedMemberUserName);
				if(isBroadCast)
					new GroupMemberRemoveTaskOnServer(groupUUID, SharedPrefManager.getInstance().getBroadCastDisplayName(groupUUID), removeList).execute();
				else
					new GroupMemberRemoveTaskOnServer(groupUUID, SharedPrefManager.getInstance().getGroupDisplayName(groupUUID), removeList).execute();
			}
//			if(service!=null){
//				service.removeGroupPerson(groupUUID, selectedMemberUserName);
//				usersList.remove(selectedMemberUserName);
//				iChatPref.saveServerGroupState(groupUUID, GroupCreateTaskOnServer.SERVER_GROUP_NOT_UPDATED);//new GroupUpdateTaskOnServer().execute();
//				iChatPref.removeUsersFromGroup(groupUUID, selectedMemberUserName);
////				finish();
//				ArrayList<String> tmp = new ArrayList<String>();
//				String name = selectedMemberUserName;
//				if(name!=null && name.contains("#786#"))
//		        	name = name.substring(0, name.indexOf("#786#"));
//				tmp.add(name);
//				usersList.remove(name);
//				mainLayout.removeView(mainLayout.findViewWithTag(name));
//			if(memberOptionDialog!=null)
//				memberOptionDialog.cancel();
//			}
			break;
			case R.id.id_cancel:
			if(memberOptionDialog!=null)
				memberOptionDialog.cancel();
			break;
		case R.id.id_add_participants: // R.id.id_add_member:
			intent = new Intent(this,EsiaChatContactsScreen.class);
			intent.putExtra(Constants.CHAT_TYPE, Constants.GROUP_USER_CHAT_CREATE);
			intent.putExtra(Constants.IS_GROUP_INVITATION, true);
			intent.putExtra(Constants.GROUP_NAME, groupUUID);
			intent.putExtra(Constants.GROUP_DISCRIPTION, statusMessage);
			if(isBroadCast)
				intent.putExtra(Constants.BROADCAST, true);
			ArrayList<String> tmpList = new ArrayList<String>();
//			for(String tmp: usersList){
//				if(tmp!=null && !tmp.equals(""))
//				tmpList.add(tmp.split(":")[0]);
//			}
			if(addMemberFilterList != null) {
				for (String tmp : addMemberFilterList) {
					if (tmp != null && !tmp.equals(""))
						tmpList.add(tmp.split(":")[0]);
				}
			}
			intent.putStringArrayListExtra(Constants.GROUP_USERS, new ArrayList<String>(tmpList));
			startActivityForResult(intent, 200);
			break;
		case R.id.id_email_group_chat:
			String selChat = "";
			ArrayList<String> textList = ChatDBWrapper.getInstance().getChatHistory(groupUUID);
			for(String msg:textList)
				selChat = selChat + msg + "\n";

			int listSize = textList.size();
			if (selChat != null && selChat.length() > 0 && !selChat.equals("")) {

				final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
				mailIntent.putExtra(Intent.EXTRA_TEXT, selChat.trim());
				final PackageManager pm = getPackageManager();
				final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
				ResolveInfo best = null;
				for (final ResolveInfo info : matches)
					if (info.activityInfo.packageName.endsWith(".gm") ||
							info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
				if (best != null)
					mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
				startActivity(mailIntent);
			}
			break;
		case R.id.id_view_all_media:
			Toast.makeText(GroupProfileScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
			break;
		case R.id.id_group_notification:
			Toast.makeText(GroupProfileScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
			break;
		case R.id.id_mute_group_chat:
			Toast.makeText(GroupProfileScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
			break;
		}
	}

	boolean isPollResultPage;
	private HashMap<String, String> getPollForGroup(String grp_name)
	{
		//save in shared prefs
		SharedPreferences prefs = getSharedPreferences("poll_data", MODE_PRIVATE);
		Gson gson = new Gson();

		//get from shared prefs
		String storedHashMapString = prefs.getString(grp_name, null);
		java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
		if(storedHashMapString != null) {
			HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
			return testHashMap2;
		}
		return null;
	}

	 public String getActivePollID(){
	    	HashMap<String, String> polls = getPollForGroup(groupUUID);
	        String poll_id = null;
	        if(polls != null && !polls.isEmpty()){
	            Iterator<Map.Entry<String, String>> iterator = polls.entrySet().iterator();
	            while (iterator.hasNext()) {
	                Map.Entry<String, String> pollEntry = iterator.next();
	                poll_id = pollEntry.getKey();
	                String poll_status = getPollStatus(poll_id);
	                if(poll_status != null && poll_status.equals("active")) {
	                    break;
	                }
	            }
	        }
	        return poll_id;
	    }
	 private String getPollStatus(String poll_id)
	    {
	        //save in shared prefs
	        String status = "inactive";
	        SharedPreferences prefs = getSharedPreferences("all_polls", MODE_PRIVATE);
	        Gson gson = new Gson();

	        //get from shared prefs
	        String storedHashMapString = prefs.getString("all_polls", null);
	        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
	        if(storedHashMapString != null) {
	            HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
	            status = testHashMap2.get(poll_id);
	            return status;
	        }
	        return null;
	    }
	Dialog poll = null;
	public void showPoll(final String message, final boolean is_view){
        try {
            if(message != null){

                isPollResultPage = true;

//				poll = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen)\;

                poll = new Dialog(this);
                poll.requestWindowFeature(Window.FEATURE_NO_TITLE);

//				poll.setCanceledOnTouchOutside(false);
                poll.setContentView(R.layout.poll_view);

                Window window = poll.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.CENTER;
                wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                poll.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // dialog dismiss without button press
                        isPollResultPage = false;
                    }
                });

//                final LinearLayout poll_result_view2 = (LinearLayout) poll.findViewById(R.id.poll_result_view2);
                final TextView title = (TextView) poll.findViewById(R.id.poll_title_txt);
                final TextView poll_text = (TextView) poll.findViewById(R.id.poll_text_message);
                final TextView poll_expires = (TextView) poll.findViewById(R.id.poll_expires);

                //Poll options
                final TextView option_one_result = (TextView) poll.findViewById(R.id.option_one_result);
                final TextView option_two_result = (TextView) poll.findViewById(R.id.option_two_result);
                final TextView option_three_result = (TextView) poll.findViewById(R.id.option_three_result);
                final TextView option_four_result = (TextView) poll.findViewById(R.id.option_four_result);
                final TextView option_five_result = (TextView) poll.findViewById(R.id.option_five_result);



                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject jsonobj = new JSONObject(message);
                            JSONArray poll_options = null;
                            String poll_unique_id = null;
                            String[] poll_id = null;
                            String[] poll_value = null;
                            int[] poll_option_count = null;
                            int total_replies = 0;
//							if(jsonobj.has("PollID") && jsonobj.getString("PollID").toString().trim().length() > 0) {
//								openedPollID = jsonobj.getString("PollID").toString();
//							}
                            if(jsonobj.has("PollTitle") && jsonobj.getString("PollTitle").toString().trim().length() > 0) {
                                title.setText("Active poll - " + jsonobj.getString("PollTitle"));
                            }
                            if(jsonobj.has("PollEndDate") && jsonobj.getString("PollEndDate").toString().trim().length() > 0) {
                                Date d = new Date(new Date().getTime());
                                SimpleDateFormat data_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String poll_end_time = jsonobj.getString("PollEndDate").toString();
                                String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                                long ss = 0;
                                long ee = 0;
                                try {
                                    ss = data_formatter.parse(current).getTime();
                                    ee = data_formatter.parse(poll_end_time).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if((ee - ss) < 0){
                                    //Poll Expired
                                    poll_expires.setText("Poll expired on - " + jsonobj.getString("PollEndDate"));
                                }else
                                poll_expires.setText("Poll expires on - " + jsonobj.getString("PollEndDate"));
                            }
                            if(jsonobj.has("Pollmessage") && jsonobj.getString("Pollmessage").toString().trim().length() > 0) {
                                poll_text.setText(jsonobj.getString("Pollmessage"));
                            }
                            if(jsonobj.has("PollOption"))
                                poll_options = jsonobj.getJSONArray("PollOption");
                            if(poll_options.length() > 0){
                                poll_id = new String[poll_options.length()];
                                poll_value = new String[poll_options.length()];
                                poll_option_count = new int[poll_options.length()];
                                for(int i = 0; i < poll_options.length(); i++){
                                    JSONObject obj = (JSONObject) poll_options.get(i);
                                    if(obj.has("OptionId")){
                                        poll_id[i] = obj.getString("OptionId");
                                    }
                                    if(obj.has("OptionText")){
                                        poll_value[i] = obj.getString("OptionText");
                                    }
                                    if(obj.has("PollOptionCount")){
                                        poll_option_count[i] = Integer.parseInt(obj.getString("PollOptionCount"));
                                        total_replies += poll_option_count[i];
                                    }
                                }
                            }
                            int total_poll_count = 0;
                            if(total_replies == 0)
                            	total_replies = 1;
                            for(int j = 0; j < poll_option_count.length; j++)
                                total_poll_count += poll_option_count[j];
                            final String[] poll_id_array = poll_id;
                            if (poll_value != null && poll_value.length == 2) {
                                option_one_result.setText(poll_value[0] + " - " + (poll_option_count[0] * 100/total_replies) + "%");
                                option_two_result.setText(poll_value[1] + " - " + (poll_option_count[1] * 100/total_replies) + "%");
                            } else if (poll_value != null && poll_value.length == 3) {
//                                poll_result_view2.setVisibility(View.VISIBLE);
                                option_one_result.setText(poll_value[0] + " - " + (poll_option_count[0] * 100/total_replies) + "%");
                                option_two_result.setText(poll_value[1] + " - " + (poll_option_count[1] * 100/total_replies) + "%");
                                option_three_result.setVisibility(View.VISIBLE);
                                option_three_result.setText(poll_value[2] + " - " + (poll_option_count[2] * 100/total_replies) + "%");
                            } else if (poll_value != null && poll_value.length == 4) {
//                                poll_result_view2.setVisibility(View.VISIBLE);
                                option_one_result.setText(poll_value[0] + " - " + (poll_option_count[0] * 100/total_replies) + "%");
                                option_two_result.setText(poll_value[1] + " - " + (poll_option_count[1] * 100/total_replies) + "%");
                                option_three_result.setVisibility(View.VISIBLE);
                                option_three_result.setText(poll_value[2] + " - " + (poll_option_count[2] * 100/total_replies) + "%");
                                option_four_result.setVisibility(View.VISIBLE);
                                option_four_result.setText(poll_value[3] + " - " + (poll_option_count[3] * 100/total_replies) + "%");
                            }else if (poll_value != null && poll_value.length == 5) {
//                              poll_result_view2.setVisibility(View.VISIBLE);
                              option_one_result.setText(poll_value[0] + " - " + (poll_option_count[0] * 100/total_replies) + "%");
                              option_two_result.setText(poll_value[1] + " - " + (poll_option_count[1] * 100/total_replies) + "%");
                              option_three_result.setVisibility(View.VISIBLE);
                              option_three_result.setText(poll_value[2] + " - " + (poll_option_count[2] * 100/total_replies) + "%");
                              option_four_result.setVisibility(View.VISIBLE);
                              option_four_result.setText(poll_value[3] + " - " + (poll_option_count[3] * 100/total_replies) + "%");
                              option_five_result.setVisibility(View.VISIBLE);
                              option_five_result.setText(poll_value[4] + " - " + (poll_option_count[4] * 100/total_replies) + "%");
                          }
                        }
                        catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        }catch(Exception  ex){
            ex.printStackTrace();
        }
        poll.show();
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
			progressDialog = ProgressDialog.show(GroupProfileScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			GroupChatServerModel model = new GroupChatServerModel();

			model.setUserName(iPrefManager.getUserName());
			if(isBroadCast)
				model.setBroadcastGroupName(groupUUID);
			else
				model.setGroupName(groupUUID);
			model.setDisplayName(displayName);
			model.setRemoveUserSet(userList);
//				List<String> adminUserSet = new ArrayList<String>();
//				model.setAdminUserSet(adminUserSet);
			String JSONstring = new Gson().toJson(model);

			DefaultHttpClient client1 = new DefaultHttpClient();
			String line = "";
			String responseMsg = "";
			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			String urlInfo = "";
			if(!isBroadCast)
				urlInfo = "/tiger/rest/group/update";
			else
				urlInfo = "/tiger/rest/bcgroup/update";
			
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ urlInfo);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
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
			}else{
				if (response!=null){
					if(service!=null){
						 if(isBroadCast){
							 iChatPref.removeUsersFromBroadCast(groupUUID, selectedMemberUserName);
								//				finish();
							ArrayList<String> tmp = new ArrayList<String>();
							String name = selectedMemberUserName;
							if(name!=null && name.contains("#786#"))
					        	name = name.substring(0, name.indexOf("#786#"));
							tmp.add(name);
							usersList.remove(selectedMemberUserName);
							mainLayout.removeView(mainLayout.findViewWithTag(name));
							mainLayout.removeView(mainLayout.findViewWithTag(name+"line"));
							selectedMemberUserName = SharedPrefManager.getInstance().getUserServerName(selectedMemberUserName);
							if(selectedMemberUserName != null && selectedMemberUserName.equalsIgnoreCase("null"))
								selectedMemberUserName = "Superchatter";
							saveInfoMessage(SharedPrefManager.getInstance().getBroadCastDisplayName(groupUUID), groupUUID, "You removed \""+selectedMemberUserName+"\".",UUID.randomUUID().toString());
						 }else{
							 service.removeGroupPerson(groupUUID, selectedMemberUserName);
//							 selectedMemberUserName = SharedPrefManager.getInstance().getUserServerName(selectedMemberUserName);
//							if(selectedMemberUserName != null && selectedMemberUserName.equalsIgnoreCase("null"))
//								selectedMemberUserName = "Superchatter";
//							 saveInfoMessage(SharedPrefManager.getInstance().getBroadCastDisplayName(groupUUID), groupUUID, "You removed \""+selectedMemberUserName+"\".",UUID.randomUUID().toString());
//								if(usersList!=null && usersList.contains(selectedMemberUserName))
//									usersList.remove(selectedMemberUserName);
								iChatPref.removeUsersFromGroup(groupUUID, selectedMemberUserName);
								String storedCount = iChatPref.getGroupMemberCount(groupUUID);
								if(storedCount!=null && !storedCount.equals("")){
									int membersSize =  Integer.parseInt(storedCount);
									if(membersSize>0)
										membersSize--;
									iChatPref.saveGroupMemberCount(groupUUID, String.valueOf(membersSize));
									((TextView)findViewById(R.id.id_participants_count)).setText(membersSize+"");
								}
				//				finish();
								ArrayList<String> tmp = new ArrayList<String>();
								String name = selectedMemberUserName;
								if(name!=null && name.contains("#786#"))
						        	name = name.substring(0, name.indexOf("#786#"));
								tmp.add(name);
								usersList.remove(selectedMemberUserName);
								mainLayout.removeView(mainLayout.findViewWithTag(name));
								mainLayout.removeView(mainLayout.findViewWithTag(name+"line"));
								PublicGroupScreen.refreshList();
						 }
						
//					if(memberOptionDialog!=null)
//						memberOptionDialog.cancel();
					}
//					
				}
//				finish();
			super.onPostExecute(response);
		}
	}
	}
	public void saveInfoMessage(String displayName, String from, String msg, String msgId) {
		if(iChatPref.isGroupChat(from) && !iChatPref.isGroupMemberActive(from, iChatPref.getUserName())){
			return;
		}
		try {
//			ChatDBWrapper chatDBWrapper = chatDBWrapper;
			ContentValues contentvalues = new ContentValues();
			String myName = SharedPrefManager.getInstance().getUserName();
			contentvalues.put(ChatDBConstants.FROM_USER_FIELD, from);
			contentvalues.put(ChatDBConstants.TO_USER_FIELD, myName);
			contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD,
					new Integer(1));
			contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, "");
			contentvalues.put(ChatDBConstants.SEEN_FIELD,
					SeenState.sent.ordinal());
//			 if(msg!=null && msg.contains("#786#")){
//				 msg = msg.replace("#786#"+from,"");
//				 msg = msg.replace("#786#"+myName,"");
//				}
			contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, msg);
			// String name =
			// cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));

			String name = "";
			String oppName = "";
			{
				oppName = from;
				name = DBWrapper.getInstance().getChatName(from);
				if(name!=null && name.equals(from))
					name = displayName+"#786#"+from;
				contentvalues.put(ChatDBConstants.MESSAGE_ID, UUID
						.randomUUID().toString());
				if(msgId == null)
					msgId = UUID.randomUUID().toString();
				contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD,msgId);
//						UUID.randomUUID().toString());
			}

			long currentTime = System.currentTimeMillis();
			Calendar calender = Calendar.getInstance();
			calender.setTimeInMillis(currentTime);
			int date = calender.get(Calendar.DATE);
//			int oldDate = date;
//			long milis = chatDBWrapper.lastMessageInDB(oppName);
//			if(milis!=-1){
//				calender.setTimeInMillis(milis);
//				oldDate = calender.get(Calendar.DATE);
//			}
//			if ((oldDate != date)
//					|| chatDBWrapper.isFirstChat(oppName)) {
//				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "1");
//			} else {
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "0");
			contentvalues.put(ChatDBConstants.MESSAGE_TYPE, XMPPMessageType.atMeXmppMessageTypeSpecialMessage.ordinal());
//			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, currentTime);

			contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, name);
			ChatDBWrapper.getInstance().insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO,
					contentvalues);
//			if (chatListener != null)
//				chatListener.notifyChatRecieve(from,msg);
		} catch (Exception e) {

		}
	}
	private class GroupAddAsAdminTaskOnServer extends AsyncTask<String, String, String> {
		String groupUUID;
		String displayName;
		List<String> userList;
		ProgressDialog progressDialog = null;
		public GroupAddAsAdminTaskOnServer(String groupUUID,String displayName,List<String> usersList){
			this.groupUUID =groupUUID;
			this.displayName = displayName;
			this.userList = usersList;
			
			
		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(GroupProfileScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			GroupChatServerModel model = new GroupChatServerModel();
			
			model.setUserName(iPrefManager.getUserName());
			if(isBroadCast)
				model.setBroadcastGroupName(groupUUID);
			else
				model.setGroupName(groupUUID);
			model.setDisplayName(displayName);
			model.setAdminUserSet(userList);
//				List<String> adminUserSet = new ArrayList<String>();
//				model.setAdminUserSet(adminUserSet);
			String JSONstring = new Gson().toJson(model);
			
			DefaultHttpClient client1 = new DefaultHttpClient();
			String line = "";
			String responseMsg = "";
			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			String urlInfo = "";
			if(!isBroadCast)
				urlInfo = "/tiger/rest/group/update";
			else
				urlInfo = "/tiger/rest/bcgroup/update";
			
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ urlInfo);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
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
			}else{
				if (response!=null){
					if(service!=null ){
						String name = SharedPrefManager.getInstance().getDisplayName();
						String fileid = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
						if(isBroadCast)
							service.sendGroupTaskMessage(name, fileid, iChatPref.getGroupOwnerName(groupUUID), selectedMemberUserName,groupUUID,displayName,statusMessage,fileId,String.valueOf(usersList.size()), XMPPMessageType.atMeXmppMessageTypeNewCreateBroadCast);
//						service.removeGroupPerson(groupUUID, selectedMemberUserName);
//						usersList.remove(selectedMemberUserName);
//						iChatPref.removeUsersFromGroup(groupUUID, selectedMemberUserName);
						//				finish();
//						ArrayList<String> tmp = new ArrayList<String>();
//						String name = selectedMemberUserName;
//						if(name!=null && name.contains("#786#"))
//				        	name = name.substring(0, name.indexOf("#786#"));
//						tmp.add(name);
//						usersList.remove(name);
//						mainLayout.removeView(mainLayout.findViewWithTag(name));
//					if(memberOptionDialog!=null)
//						memberOptionDialog.cancel();
					}
				}
				finish();
				super.onPostExecute(response);
			}
		}
	}
	private class GroupDeactivationTaskOnServer extends AsyncTask<String, String, String> {
		String groupUUID;
		ProgressDialog progressDialog = null;
		public GroupDeactivationTaskOnServer(String groupUUID){
			this.groupUUID =groupUUID;
			
			
		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(GroupProfileScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//			GroupChatServerModel model = new GroupChatServerModel();
			
//			model.setUserName(iPrefManager.getUserName());
//			if(isBroadCast)
//				model.setBroadcastGroupName(groupUUID);
//			else
//				model.setGroupName(groupUUID);
//			String JSONstring = new Gson().toJson(model);
			
			DefaultHttpClient client1 = new DefaultHttpClient();
			String line = "";
			String responseMsg = "";
//			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			String urlInfo = "";
//			API URL: http://52.88.175.48/tiger/rest/group/deactivate?groupName=p5grpapi_p5domain&userName=919717098492_p5domain
			String query = groupUUID;
			try {
				 query = URLEncoder.encode(query, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!isBroadCast)
				urlInfo = "/tiger/rest/group/deactivate?groupName="+query+"&userName="+iPrefManager.getUserName();
			else
				urlInfo = "/tiger/rest/bcgroup/deactivate?";
			
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
				
			} catch(Exception e){
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
				if (response!=null && !response.equals("")){
					if(service!=null && !isBroadCast){
						SharedPrefManager.getInstance().saveGroupInfo(groupUUID, SharedPrefManager.GROUP_ACTIVE_INFO, false);
						service.removeGroupPerson(groupUUID, SharedPrefManager.getInstance().getUserName());
					}
				}
				finish();
				super.onPostExecute(response);
			}
		}
	}
	private class GroupRemoveAsAdminTaskOnServer extends AsyncTask<String, String, String> {
		String groupUUID;
		String displayName;
		List<String> userList;
		ProgressDialog progressDialog = null;
		public GroupRemoveAsAdminTaskOnServer(String groupUUID,String displayName,List<String> usersList){
			this.groupUUID =groupUUID;
			this.displayName = displayName;
			this.userList = usersList;
			
			
		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(GroupProfileScreen.this, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			GroupChatServerModel model = new GroupChatServerModel();
			
			model.setUserName(iPrefManager.getUserName());
			if(isBroadCast)
				model.setBroadcastGroupName(groupUUID);
			else
				model.setGroupName(groupUUID);
			model.setDisplayName(displayName);
			model.setMemberUserSet(userList);
//				List<String> adminUserSet = new ArrayList<String>();
//				model.setAdminUserSet(adminUserSet);
			String JSONstring = new Gson().toJson(model);
			
			DefaultHttpClient client1 = new DefaultHttpClient();
			String line = "";
			String responseMsg = "";
			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			String urlInfo = "";
			if(!isBroadCast)
				urlInfo = "/tiger/rest/group/update";
			else
				urlInfo = "/tiger/rest/bcgroup/update";
			
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ urlInfo);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
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
			}else{
				if (response!=null){
					if(service!=null){
						String name = SharedPrefManager.getInstance().getDisplayName();
						String fileid = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
						if(isBroadCast)
							service.sendGroupTaskMessage(name, fileid, iChatPref.getGroupOwnerName(groupUUID), selectedMemberUserName,groupUUID,null,null,null, String.valueOf(usersList.size()), XMPPMessageType.atMeXmppMessageTypeRemoveBroadCast);
//						service.removeGroupPerson(groupUUID, selectedMemberUserName);
//						usersList.remove(selectedMemberUserName);
//						iChatPref.removeUsersFromGroup(groupUUID, selectedMemberUserName);
						//				finish();
//						ArrayList<String> tmp = new ArrayList<String>();
//						String name = selectedMemberUserName;
//						if(name!=null && name.contains("#786#"))
//				        	name = name.substring(0, name.indexOf("#786#"));
//						tmp.add(name);
//						usersList.remove(name);
//						mainLayout.removeView(mainLayout.findViewWithTag(name));
//					if(memberOptionDialog!=null)
//						memberOptionDialog.cancel();
					}
				}
				finish();
				super.onPostExecute(response);
			}
		}
	}
	public void showDialog(String s) {
		final Dialog bteldialog = new Dialog(GroupProfileScreen.this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				return false;
			}
		});
		bteldialog.show();
	}
	public void showDialog(final String title, final String s) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(true);
		bteldialog.setContentView(R.layout.custom_dialog_two_button);
		if(title!=null){
			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
			}
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_send)).setText("Ok");
		((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				ChatDBWrapper.getInstance().deleteRecentUserChatByUserName(groupUUID);
//				saveMessage(title, groupUUID,s);
				saveInfoMessage(title, groupUUID, getString(R.string.msgs_cleared), UUID.randomUUID().toString());
				finish();
				return false;
			}
		});
		((TextView)bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				return false;
			}
		});
		bteldialog.show();
	}
	
	private void updateRow(String userName, String status, String userDisplayName){
		int row_count = mainLayout.getChildCount();
		String tag = null;
		for(int i = 0; i < row_count; i++){
			View view = mainLayout.getChildAt(i);
			if(view instanceof RelativeLayout){
				tag = (String)view.getTag();
				if(tag != null && tag.equalsIgnoreCase(userName)){
					//Update the row.
//					setPic((RoundedImageView) mainLayout.findViewById(R.id.contact_icon), (ImageView) mainLayout.findViewById(R.id.contact_icon_default), iChatPref.getUserServerNameIfExists(userName), userName);
					if(userDisplayName != null)
						((TextView)view.findViewById(R.id.id_contact_name)).setText(userDisplayName);
					if(status != null)
						((TextView)view.findViewById(R.id.id_contact_status)).setText(status);
				}
				else if(tag != null){
//					setPic((RoundedImageView) mainLayout.findViewById(R.id.contact_icon), (ImageView) mainLayout.findViewById(R.id.contact_icon_default), iChatPref.getUserServerNameIfExists(tag), tag);
				}
			}
		}
	}
	@Override
	public void notifyProfileUpdate(final String userName, final String status) {
		// TODO Auto-generated method stub
//		if (usersList != null)
//			runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				updateRow(userName, status);
//			}
//			});
	}
	@Override
	public void notifyProfileUpdate(String userName) {
		// TODO Auto-generated method stub
	}
	@Override
	public void notifyProfileUpdate(final String userName, final String status, final String userDisplayName) {
		// TODO Auto-generated method stub
		if (usersList != null)
			runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				updateRow(userName, status, userDisplayName);
			}
			});
	}
	private OnClickListener onImageClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + arg0.getTag()), "image/*");
			startActivity(intent);
		}
		
	};
	private OnClickListener onVideoClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(arg0.getTag().toString()), "video/*");
			startActivity(intent);
		}
		
	};
	private OnClickListener onAudioClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if(myVoicePlayer!=null)
				return;
//			Intent intent = new Intent();
//			intent.setAction(Intent.ACTION_VIEW);
//			intent.setDataAndType(Uri.parse(arg0.getTag().toString()), "audio/*");
//			startActivity(intent);
			 myVoicePlayer = new RTMediaPlayer();
//			myVoicePlayer._startPlay(arg0.getTag().toString(), null, null);
			showVoiceDialog("Audio Player","");
			myVoicePlayer.setProgressBar(seekBar);
			myVoicePlayer.setMediaHandler(GroupProfileScreen.this);
			myVoicePlayer._startPlay(arg0.getTag().toString(), null, null);
		}
		
	};
	RTMediaPlayer myVoicePlayer;
	SeekBar seekBar;
	Dialog voiceDialog;
	public void showVoiceDialog(final String title, final String s) {
		voiceDialog = new Dialog(this);
		voiceDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		voiceDialog.setCanceledOnTouchOutside(false);
		voiceDialog.setContentView(R.layout.voice_dialog);
		if(title!=null){
			((TextView)voiceDialog.findViewById(R.id.id_dialog_title)).setText(title);
		}
		
		seekBar = (SeekBar)voiceDialog.findViewById(R.id.seekBar1);
//		myVoicePlayer.setProgressBar(seekBar);
		((TextView)voiceDialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)voiceDialog.findViewById(R.id.id_ok)).setText("Stop");
((TextView)voiceDialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				voiceDialog.cancel();
				if(myVoicePlayer!=null){
					myVoicePlayer.reset();
					myVoicePlayer.clear();
				}
				myVoicePlayer = null;
				return false;
			}
		});
	voiceDialog.show();
	}
	private OnClickListener onDocsClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			String mediaLocalPath = arg0.getTag().toString();
			if (mediaLocalPath.contains(".pdf")) {
				intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/pdf");
			}else if (mediaLocalPath.contains(".doc")) {
			    intent.addCategory("android.intent.category.DEFAULT");
			    intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
			    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/msword");
			    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}else if (mediaLocalPath.contains(".xls")) {
			    intent.addCategory("android.intent.category.DEFAULT");
			    intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
			    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/vnd.ms-excel");
			    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}else if (mediaLocalPath.contains(".ppt")) {
			    intent.addCategory("android.intent.category.DEFAULT");
			    intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
			    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/vnd.ms-powerpoint");
			    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			}
			startActivity(intent);
		}
		
	};
	private void setAllDocs(){
		ArrayList<ContentValues> allDocs = ChatDBWrapper.getInstance().getAllPersonDocs(groupUUID);
		docsCountView.setText(""+allDocs.size());
		for(ContentValues values : allDocs){
			addDocsView(docsScrollLayout, values.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD),values.getAsString(ChatDBConstants.MEDIA_CAPTION_TAG),values.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD));
		}
	}
	private void setAllMedia(){
		ArrayList<ContentValues> allMedia = ChatDBWrapper.getInstance().getAllPersonMedia(groupUUID);
		mediaCountView.setText(""+allMedia.size());
		for(ContentValues values : allMedia){
			if(values.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD) == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
				addVideoView(mediaScrollLayout, values.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD),values.getAsString(ChatDBConstants.MESSAGE_THUMB_FIELD));
			else if(values.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD) == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
				addAudioView(mediaScrollLayout, values.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
			else
				addImageView(mediaScrollLayout, values.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
		}
	}
	private void addVideoView(LinearLayout mainLayout,String path,String thumb){
//		View view = new View(this);
		RelativeLayout rlLayout = new RelativeLayout(this);
		rlLayout.setBackgroundColor(Color.DKGRAY);
		RelativeLayout.LayoutParams rlLayoutParams = new   RelativeLayout.LayoutParams(150, 150);
		rlLayoutParams.setMargins(3, 2, 3, 2);
		rlLayout.setLayoutParams(rlLayoutParams);
		
//		view.setLayoutParams((new   ViewGroup.LayoutParams(2, 150)));
//		view.setBackgroundColor(Color.GRAY);
//		mainLayout.addView(view);
		
//		LinearLayout linearLayout= new LinearLayout(this);
//        linearLayout.setLayoutParams(new LayoutParams(150, 150));
		
		ImageView imageView = new ImageView(this);
		imageView.setTag(path);
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 75);
//		params.setMargins(0, 0, 0, 0);
//		imageView.setLayoutParams((new   ViewGroup.LayoutParams(150,130)));//ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		imageView.setBackgroundResource(R.color.red);
		imageView.setBottom(2);
//		imageView.setPadding(2, 2, 0, 0);
		rlLayout.addView(imageView);
		ImageView playView = new ImageView(this);
		RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    labelLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		playView.setBackgroundResource(R.drawable.play_video_button);
		rlLayout.addView(playView,labelLayoutParams);
		mainLayout.addView(rlLayout);
		imageView.setOnClickListener(onVideoClickListener);
		setVideoPicForCache(imageView,path);
//		String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
//		Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
//		view.setImageBitmap(tmpBitMap);
		
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(path);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}else{
			Bitmap tmpBitMap = createVideoThumbFromByteArray(thumb);
			imageView.setImageBitmap(tmpBitMap);
	    	SuperChatApplication.addBitmapToMemoryCache(path,tmpBitMap);
		}
		}
	private void setVideoPicForCache(ImageView view, String cacheIdPath){
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(cacheIdPath);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
		}else {
			File file1 = new File(cacheIdPath);
			//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1!=null && file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				setThumbForCache(view, cacheIdPath);
			}else{
				
			}
		}
	}
	private Bitmap createVideoThumbFromByteArray(String baseData) {
		if(baseData == null)
			return null;
		Bitmap bmp = null;
		byte[] data = Base64.decode(baseData, Base64.DEFAULT);
		if (data != null)
			bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		return bmp;
	}
	private void addAudioView(LinearLayout mainLayout,String path){
		View view = new View(this);
		view.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1)));
		view.setBackgroundColor(Color.GRAY);
		mainLayout.addView(view);
		ImageView imageView = new ImageView(this);
		imageView.setTag(path);
//		imageView.setLayoutParams((new   ViewGroup.LayoutParams(150,130)));//ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
		imageView.setLayoutParams(LayoutHelper.createLinear(100, 75, 2, 0, 2, 0));
		imageView.setBackgroundResource(R.color.orange);
		imageView.setBottom(2);
		
//		imageView.setPadding(2, 2, 0, 0);
	   
		mainLayout.addView(imageView);
		imageView.setOnClickListener(onAudioClickListener);
		imageView.setImageResource(R.drawable.addplay);		
}
	private void addDocsView(LinearLayout mainLayout,String path, String name, int fileType){
		View view = new View(this);
		LinearLayout rlLayout = new LinearLayout(this);
//		rlLayout.setBackgroundColor(Color.DKGRAY);
		LinearLayout.LayoutParams rlLayoutParams = new   LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		rlLayout.setOrientation(LinearLayout.VERTICAL);
		rlLayout.setGravity(Gravity.CENTER);
		rlLayoutParams.setMargins(0, 0, 0, 0);
		rlLayout.setLayoutParams(rlLayoutParams);
		view.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1)));
		view.setBackgroundColor(Color.GRAY);
		mainLayout.addView(view);
		ImageView imageView = new ImageView(this);
		imageView.setTag(path);
//		imageView.setLayoutParams((new   ViewGroup.LayoutParams(150,130)));//ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
		imageView.setLayoutParams(LayoutHelper.createLinear(50, 60, 0, 0, 0, 0));
//		imageView.setBackgroundResource(R.color.white);
//		imageView.setBottom(2);
//		imageView.setPadding(2, 2, 0, 0);
		TextView titleView = new TextView(this);
		LinearLayout.LayoutParams labelLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    int index = path.lastIndexOf("/");
        titleView.setText(path.substring(index+1,index+9));
//        titleView.setEllipsize(TruncateAt.MIDDLE);
        titleView.setTextSize(8);
	    rlLayout.addView(imageView);
	    rlLayout.addView(titleView,labelLayoutParams);
	    mainLayout.addView(rlLayout);
//		mainLayout.addView(imageView);
		imageView.setOnClickListener(onDocsClickListener);
		if(fileType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
			imageView.setImageResource(R.drawable.docs);	
		else if(fileType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
			imageView.setImageResource(R.drawable.pdf);	
		else if(fileType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
			imageView.setImageResource(R.drawable.ppt);	
		else if(fileType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
			imageView.setImageResource(R.drawable.xls);	
//		name
}
	private void addImageView(LinearLayout mainLayout,String path){
		View view = new View(this);
		view.setLayoutParams((new   ViewGroup.LayoutParams(2, 150)));
		view.setBackgroundColor(Color.GRAY);
//		mainLayout.addView(view);
		
		LinearLayout linearLayout= new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 150);
        layoutParams.setMargins(3, 2, 3, 2);
        linearLayout.setLayoutParams(layoutParams);
        
		ImageView imageView = new ImageView(this);
		imageView.setTag(path);
		imageView.setScaleType(ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		imageView.setBackgroundResource(R.drawable.round_rect_profile_bg);
		imageView.setBottom(2);
		//adding view to layout
        linearLayout.addView(imageView);
      //adding view to Main View
		mainLayout.addView(linearLayout);
		imageView.setOnClickListener(onImageClickListener);
		setPicForCache(imageView,path);
		
}
	private void setPicForCache(ImageView view, String cacheIdPath){
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(cacheIdPath);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
		}else {
			File file1 = new File(cacheIdPath);
			//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1!=null && file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				setThumbForCache(view, cacheIdPath);
			}else{
				
			}
		}
	}
	private void setThumbForCache(ImageView imageViewl,String path){
		BitmapFactory.Options bfo = new BitmapFactory.Options();
	    bfo.inSampleSize = 2;
	    Bitmap bm = null;
	    Bitmap bm1 = null;
	    try{
		    bm = BitmapFactory.decodeFile(path, bfo);
		    if(bm.getWidth()>300)
		    	bm = ThumbnailUtils.extractThumbnail(bm, 200, 210);
		    bm = rotateImageForCache(path, bm);
		    bm1 = Bitmap.createScaledBitmap(bm, 100, 75, true);
	    }catch(Exception ex){
	    
	    }
	    if(bm!=null){
	    	imageViewl.setImageBitmap(bm1);
	    	SuperChatApplication.addBitmapToMemoryCache(path,bm);
	    	ChatListAdapter.cacheKeys.add(path);
	    } else{
//	    	try{
//	    		imageViewl.setImageURI(Uri.parse(path));
//	    	}catch(Exception e){
//	    		
//	    	}
	    }
	}
	public static Bitmap rotateImageForCache(String path, Bitmap bm) {
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
	@Override
	public void voiceRecordingStarted(String recordingPath) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void voiceRecordingCompleted(String recordedVoicePath) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void voicePlayStarted() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void voicePlayCompleted(View view) {
		if(voiceDialog!=null)
			voiceDialog.cancel();
			if(myVoicePlayer!=null){
				myVoicePlayer.reset();
				myVoicePlayer.clear();
				myVoicePlayer = null;
			}
	}
	@Override
	public void onError(int i) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDureationchanged(long total, long current , SeekBar currentSeekBar) {
		if(seekBar!=null)
			seekBar.setProgress((int)current);
	}
//---------------------------------------------------------------------------------------
	public void showDialog(final String title, final String s, final boolean isJoinning) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(true);
		bteldialog.setContentView(R.layout.custom_dialog_two_button);
		if(title!=null){
			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
			}
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		
		if(isJoinning){
			((TextView)bteldialog.findViewById(R.id.id_send)).setText("Join");
		}else
			((TextView)bteldialog.findViewById(R.id.id_send)).setText("Leave");
		((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				if(isJoinning){
					if(Build.VERSION.SDK_INT >= 11)
						new ChannelLeaveJoinOnInfo(isJoinning).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupUUID);
					else
						new ChannelLeaveJoinOnInfo(isJoinning).execute(groupUUID);
				}else{
					if(Build.VERSION.SDK_INT >= 11)
						new ChannelLeaveJoinOnInfo(isJoinning).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupUUID);
					else
						new ChannelLeaveJoinOnInfo(isJoinning).execute(groupUUID);
				}
				return false;
			}
		});
		((TextView)bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				return false;
			}
		});
		bteldialog.show();
	}
	//-----------------------------------------------------------------------------
	public class ChannelLeaveJoinOnInfo extends AsyncTask<String, String, String> {
		LoginModel loginForm;
		ProgressDialog progressDialog = null;
		SharedPrefManager sharedPrefManager;
		boolean isJoinning;
		public ChannelLeaveJoinOnInfo(boolean isJoinning){
			sharedPrefManager = SharedPrefManager.getInstance();
			loginForm = new LoginModel();
			loginForm.setUserName(sharedPrefManager.getUserName());
			loginForm.setPassword(sharedPrefManager.getUserPassword());
			loginForm.setToken(sharedPrefManager.getDeviceToken());
			this.isJoinning = isJoinning;
		}
		@Override
		protected void onPreExecute() {
				progressDialog = ProgressDialog.show(GroupProfileScreen.this, "", "Request processing. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
//			String JSONstring = new Gson().toJson(loginForm);
			String url = "";
			try {
				 if(params!=null && params.length>0){
						if(isJoinning){
							url = "/join?groupName="+URLEncoder.encode(params[0], "utf-8");
						}else{
							url = "/leave?groupName="+URLEncoder.encode(params[0], "utf-8");
						}
					}
				 
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    DefaultHttpClient client1 = new DefaultHttpClient();
		    
//		    http://52.88.175.48/tiger/rest/group/leave?groupName=qa_p5domain
//		    http://52.88.175.48/tiger/rest/group/join?groupName=qa_p5domain
		    
			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/group/"+url);
//	         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
			 HttpResponse response = null;
			 
	         try {
//				httpPost.setEntity(new StringEntity(JSONstring));
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
				            	return str;
								
									
									
				            
				            }
					 }
				} catch (ClientProtocolException e) {
					Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				}
				 
			} catch(Exception e){
				Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
				e.printStackTrace();
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
						if(citrusError!=null && citrusError.code.equals("20019") ){
							SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//							iPrefManager.saveUserDomain(domainNameView.getText().toString());
							iPrefManager.saveUserId(errorModel.userId);
							iPrefManager.setAppMode("VirginMode");
//							iPrefManager.saveUserPhone(regObj.iMobileNumber);
	//						iPrefManager.saveUserPassword(regObj.getPassword());
							iPrefManager.saveUserLogedOut(false);
							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
							showDialog(citrusError.message);
						}else
							showDialog(citrusError.message);
					} else if (errorModel.message != null)
						showDialog(errorModel.message);
				} else
					showDialog("Please try again later.");
			}else{
				if (str!=null && str.contains("\"status\":\"success\"")){
					Gson gson = new GsonBuilder().create();
					String disp_name = iChatPref.getGroupOwnerName(groupUUID);//DBWrapper.getInstance(getApplicationContext()).getChatName(groupOwnerName);
					if(disp_name != null && disp_name.contains("#"))
						disp_name = disp_name.substring(0, disp_name.indexOf('#'));
//					if(disp_name==null || disp_name.equals(groupOwnerName))
//						String	disp_name = iChatPref.getGroupOwnerName(groupUUID);
//					ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
					PublicGroupScreen.updateDataLocally(groupUUID, false);
					SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
					iPrefManager.saveUserGroupInfo(groupUUID, iPrefManager.getUserName(), SharedPrefManager.PUBLIC_CHANNEL, false);
					Intent intent = new Intent(SuperChatApplication.context,PublicGroupInfoScreen.class);
					intent.putExtra(PublicGroupInfoScreen.CHANNEL_TITLE, displayName);
					
					String count = SharedPrefManager.getInstance().getGroupMemberCount(groupUUID);
					if(isJoinning){
						if(count!=null && !count.equals("")){
							int membersSize = Integer.parseInt(count)+1;
							SharedPrefManager.getInstance().saveGroupMemberCount(groupUUID, String.valueOf(membersSize));
						}
					}else{
						if(count!=null && !count.equals("")){
							int membersSize = Integer.parseInt(count)-1;
							if(membersSize>0)
								SharedPrefManager.getInstance().saveGroupMemberCount(groupUUID, String.valueOf(membersSize));
						}
					}
					count = SharedPrefManager.getInstance().getGroupMemberCount(groupUUID);
					((TextView)findViewById(R.id.id_participants_count)).setText(count+"");
					if(count != null)
						intent.putExtra(PublicGroupInfoScreen.MEMBERS_COUNT_ID, count);
					intent.putExtra(PublicGroupInfoScreen.CHANNEL_OWNER, disp_name);
					intent.putExtra(PublicGroupInfoScreen.CHANNEL_DESCRIPTION, statusMessage);
					if(isGroupOwner)
						intent.putExtra(PublicGroupInfoScreen.CHANNEL_MEMBER_TYPE, "OWNER");
					else
						intent.putExtra(PublicGroupInfoScreen.CHANNEL_MEMBER_TYPE, "USER");
					intent.putExtra(PublicGroupInfoScreen.CHANNEL_NAME, groupUUID);
					intent.putExtra(PublicGroupInfoScreen.CHANNEL_PIC_ID, iChatPref.getUserFileId(groupUUID));
					startActivity(intent);
					finish();
				}
			}
			super.onPostExecute(str);
		}
	}

}
