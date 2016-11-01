package com.superchat.widgets;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.sdk.ChatCountListener;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.beans.PhotoToLoad;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.task.ImageLoaderWorker;
import com.superchat.ui.ChatListScreen;
import com.superchat.ui.CreateSharedIDActivity;
import com.superchat.ui.GroupProfileScreen;
import com.superchat.ui.HomeScreen;
import com.superchat.ui.ProfileScreen;
import com.superchat.ui.TextDrawable;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// Referenced classes of package com.vopium.widget:
//            DontPressWithParentLayout

public class ChatHomeAdapter extends SimpleCursorAdapter implements OnClickListener{
	public class ViewHolder {

		public ImageView personImage;
		public ImageView personImageDefault;
		public String id;
		TextView chatPerson;
		public TextView lastMessage;
		public ImageView lastMessageIcon;
		public ImageView muteIcon;
		public TextView id_last_msg_sender;
		public TextView lastMessageTime;
		private CheckBox iCheckBox;
		public TextView unseenMessages;
		public TextView moreTab;
		public TextView deleteTab;
		String userName = "";
		String nameText = "";
		String contactNumber = "";
		String voipumValue = "";
		String displayName="";
		boolean isBroadCast;
		String fileId;
		private OnLongClickListener onLongClickListener = new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// unseenMessages.setVisibility(TextView.INVISIBLE);
				// lastMessageTime.setVisibility(TextView.INVISIBLE);
				// lastMessage.setVisibility(TextView.INVISIBLE);
//				Log.d("ChatHomeAdapter", "onLongClick name: "+userName);
				if(!iChatPref.isGroupChat(userName) && !iChatPref.isBroadCast(userName)){
					moreTab.setVisibility(TextView.VISIBLE);
					deleteTab.setVisibility(TextView.VISIBLE);
				}
				return false;
			}
		};
		public void showDialog(String title, String s) {
			final Dialog bteldialog = new Dialog((HomeScreen)context);
			bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			bteldialog.setCanceledOnTouchOutside(false);
			bteldialog.setContentView(R.layout.custom_dialog_two_button);
			if(title!=null){
				if(title!=null && title.contains("#786#"))
					title = title.substring(0, title.indexOf("#786#"));
				((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
				}
			((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
			((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					bteldialog.cancel();
					Intent intent = new Intent(SuperChatApplication.context,
							ChatListScreen.class);
					intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,nameText);
					intent.putExtra(DatabaseConstants.USER_NAME_FIELD,userName);
					intent.putExtra("is_vopium_user", true);
					((HomeScreen) context).startActivity(intent);
					moreTab.setVisibility(TextView.GONE);
					deleteTab.setVisibility(TextView.GONE);
					return false;
				}
			});
			((TextView)bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					bteldialog.cancel();
					HomeScreen.calledForShare = false;
					return false;
				}
			});
			bteldialog.show();
		}
		private OnClickListener onCheckeClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(HomeScreen.calledForShare){
					String type = "";
					switch(HomeScreen.sharingType){
					case HomeScreen.VIDEO_SHARING:
						type = "video";
						break;
					case HomeScreen.VOICE_SHARING:
						type = "Voice";
						break;
					case HomeScreen.IMAGE_SHARING:
						type = "picture";
						break;
					case HomeScreen.PDF_SHARING:
						type = "PDF";
						break;
					case HomeScreen.DOC_SHARING:
						type = "MS Word";
						break;
					case HomeScreen.PPT_SHARING:
						type = "MS PPT";
						break;
					case HomeScreen.XLS_SHARING:
						type = "MS XLS";
						break;
					}
					showDialog(displayName,"You are sharing "+type+".");
				}
				else{
					switch (v.getId()) {
					case R.id.contact_icon_default:
					case R.id.contact_icon:
//						String file_path = (String) v.getTag();
//						if(file_path != null && !file_path.contains("clear"))
//						{
//							Intent intent = new Intent();
//							intent.setAction(Intent.ACTION_VIEW);
//							if(file_path.startsWith("http://"))
//								intent.setDataAndType(Uri.parse(file_path), "image/*");
//							else
//								intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
//							((HomeScreen) context).startActivity(intent);
//						}
						if(!iChatPref.isGroupChat(userName) && !iChatPref.isBroadCast(userName)){
							if(iChatPref.isSharedIDContact(userName)){
								if(iChatPref.isDomainAdminORSubAdmin() || HomeScreen.isAdminFromSharedID(userName, SharedPrefManager.getInstance().getUserName())){
									Intent intent = new Intent(SuperChatApplication.context, CreateSharedIDActivity.class);
									intent.putExtra("EDIT_MODE", true);
									intent.putExtra(Constants.GROUP_UUID, userName);
									intent.putExtra(Constants.GROUP_NAME, iChatPref.getSharedIDDisplayName(userName));
									intent.putExtra(Constants.GROUP_FILE_ID, iChatPref.getSharedIDFileId(userName));
									((HomeScreen) context).startActivity(intent);
								}else
									return;
							}else{
								 Intent intent1 = new Intent(SuperChatApplication.context, ProfileScreen.class);
								 Bundle bundle = new Bundle();
								 bundle.putString(Constants.CHAT_USER_NAME, userName);
								 bundle.putString(Constants.CHAT_NAME, displayName);
								 intent1.putExtras(bundle);
								 ((HomeScreen)context).startActivity(intent1);
							}
						 }else{
							 if(fileId!=null && !fileId.equals(""))
									SuperChatApplication.removeBitmapFromMemCache(fileId);
							 String file_path = (String) v.getTag();
							 if(iChatPref.isGroupChat(file_path)) {
								 Intent intent = new Intent(SuperChatApplication.context,
											ChatListScreen.class);
									intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,nameText);
									intent.putExtra(DatabaseConstants.USER_NAME_FIELD,userName);
									intent.putExtra("is_vopium_user", true);
									((HomeScreen) context).startActivity(intent);
									moreTab.setVisibility(TextView.GONE);
									deleteTab.setVisibility(TextView.GONE);
							 }else if(iChatPref.isBroadCast(file_path)) {
								 Intent intent = new Intent(SuperChatApplication.context,
											ChatListScreen.class);
									intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,nameText);
									intent.putExtra(DatabaseConstants.USER_NAME_FIELD,userName);
									intent.putExtra("is_vopium_user", true);
									((HomeScreen) context).startActivity(intent);
									moreTab.setVisibility(TextView.GONE);
									deleteTab.setVisibility(TextView.GONE);
							 }else{
							
								if(file_path != null && !file_path.equals("") && !file_path.contains("clear"))
								{
//									Intent intent = new Intent();
//									intent.setAction(Intent.ACTION_VIEW);
//									if(file_path.startsWith("http://"))
//										intent.setDataAndType(Uri.parse(file_path), "image/*");
//									else
//										intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
//									((HomeScreen) context).startActivity(intent);
									
									if(fileId!=null && !fileId.equals("")){
//										SuperChatApplication.removeBitmapFromMemCache(fileId);
									if (Build.VERSION.SDK_INT >= 11)
										new BitmapDownloader(context,(ImageView)v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,fileId,BitmapDownloader.PIC_VIEW_REQUEST);
						             else
						            	 new BitmapDownloader(context,(ImageView)v).execute(fileId,BitmapDownloader.PIC_VIEW_REQUEST);
									}
								}
							 }
						 }
						break;
					case R.id.id_more_tab:
						if (moreOptionDialog != null
								&& !moreOptionDialog.isShowing()) {
							optionUser = userName;
							userDisplayName = displayName;
							isGroup = iChatPref.isGroupChat(userName);
							if (isGroup) {
								usersList.clear();
								usersList = iChatPref.getGroupUsersList(userName);
								Collections.sort(usersList);
								usersDisplayList = convertNames(usersList);
								updateGroupUsersList(usersDisplayList);
								}
							if(!isGroup){
								moreOptionDialog.findViewById(R.id.id_group_info).setVisibility(View.GONE);
							}else{
								moreOptionDialog.findViewById(R.id.id_group_info).setVisibility(View.VISIBLE);
								moreOptionDialog.findViewById(R.id.id_group_info).setOnClickListener(ChatHomeAdapter.this);
							}
							moreOptionDialog.findViewById(R.id.id_email_chat).setTag(userName);
							moreOptionDialog.show();
						}
						break;
					case R.id.id_delete_tab:
						SharedPrefManager prefObj = SharedPrefManager.getInstance();
						prefObj.saveChatCountOfUser(userName, 0);
						ChatDBWrapper.getInstance().deleteRecentUserChat(displayName);
			
						Cursor cursor1 = ChatDBWrapper.getInstance().getRecentChatList(null);
							swapCursor(cursor1);
			//				setAdapter(adapter);
						notifyDataSetChanged();
						moreTab.setVisibility(TextView.GONE);
						deleteTab.setVisibility(TextView.GONE);
						if(connectListener!=null)
							connectListener.notifyChatRecieve(null,null);
						break;
					default:
						Intent intent = new Intent(SuperChatApplication.context,
								ChatListScreen.class);
						intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,nameText);
						intent.putExtra(DatabaseConstants.USER_NAME_FIELD,userName);
						intent.putExtra("is_vopium_user", true);
						((HomeScreen) context).startActivity(intent);
						moreTab.setVisibility(TextView.GONE);
						deleteTab.setVisibility(TextView.GONE);
						
					}
				}
			}
		};

		public ViewHolder() {
		}
	}

//	public void switchToUserChat(String display, String user){
//		Intent intent = new Intent(SuperChatApplication.context,
//				ChatListScreen.class);
//		intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, display);
//		intent.putExtra(DatabaseConstants.USER_NAME_FIELD, user);
//		intent.putExtra("is_vopium_user", true);
//		((HomeScreen) context).startActivity(intent);
//		moreTab.setVisibility(TextView.GONE);
//		deleteTab.setVisibility(TextView.GONE);
//	}

	int check;
	private static HashMap<String, Boolean> checkedTagMap = new HashMap<String, Boolean>();
	ChatCountListener connectListener;
	private Context context;
	ArrayList<String> usersList = new ArrayList<String>();
	ArrayList<String> usersDisplayList = new ArrayList<String>();
	HashMap<String, String> nameMap = new HashMap<String, String>();
	public ExecutorService executorService;
	SharedPrefManager iChatPref;
//	Handler handler;
	private boolean isEditableContact = false;
	int layout;
	public int listenerState;
	ViewGroup myParent;
	private Uri uri;
	Dialog moreOptionDialog;
	  Calendar calander;
	  Calendar currentCalender;
	  String userMe;
	  String optionUser = "";
	  String userDisplayName = "";
	  boolean isGroup;
	public ChatHomeAdapter(Context context1, int i, Cursor cursor, String as[],
			int ai[], int j,ChatCountListener connectListener) {
		super(context1, i, cursor, as, ai, j);
		this.connectListener = connectListener;
		executorService = Executors.newFixedThreadPool(2);
		check = 0;
		context = context1;
		executorService = Executors.newFixedThreadPool(5);
		layout = i;
//		handler = new Handler();
		iChatPref = SharedPrefManager.getInstance();
		calander = Calendar.getInstance(TimeZone.getDefault());
		currentCalender = Calendar.getInstance(TimeZone.getDefault());
		currentCalender.setTimeInMillis(System.currentTimeMillis());
		userMe = com.superchat.utils.SharedPrefManager.getInstance().getUserName();
		mDrawableBuilder = TextDrawable.builder()
                .beginConfig().toUpperCase()
            .endConfig()
            .round();
	}
public void loadDialog(){
	moreOptionDialog = new Dialog(context);
	moreOptionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	moreOptionDialog.setContentView(R.layout.more_options_dialog);
	iChatPref = SharedPrefManager.getInstance();
	moreOptionDialog.findViewById(R.id.id_clear_chat).setOnClickListener(this);
	moreOptionDialog.findViewById(R.id.id_group_info).setOnClickListener(this);
	moreOptionDialog.findViewById(R.id.id_email_chat).setOnClickListener(this);
	moreOptionDialog.findViewById(R.id.id_cancel).setOnClickListener(this);
	
	Window window = moreOptionDialog.getWindow();
	WindowManager.LayoutParams wlp = window.getAttributes();

	wlp.gravity = Gravity.BOTTOM;
	wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
	window.setAttributes(wlp);
}
	public HashMap<String, Boolean> getSelectedItems() {
		return checkedTagMap;
	}

	public void removeSelectedItems() {
		checkedTagMap.clear();
	}

	public void setEditableContact(boolean isEdit) {
		this.isEditableContact = isEdit;
	}

	public boolean isEditable() {
		return this.isEditableContact;
	}

	
	public static int daysBetween(Calendar startDate, Calendar endDate) {  
	    return Math.abs(startDate.get(Calendar.DAY_OF_MONTH)-endDate.get(Calendar.DAY_OF_MONTH));  
	} 
	public void bindView(View view, Context context1, Cursor cursor) {
        ViewHolder viewholder = (ViewHolder)view.getTag();
        String fromName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.FROM_USER_FIELD));
        String toUserName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TO_USER_FIELD));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));
        String caption = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MEDIA_CAPTION_TAG));
        String msg = cursor.getString(cursor.getColumnIndex(DatabaseConstants.MESSAGEINFO_FIELD));

        String displayName = null;
        boolean isSharedID = false;
//        Log.i("ChatHomeAdapter", "caption : "+caption);
        String groupMsgSenderName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD));

//		System.out.println("CONTACT_NAMES_FIELD -> "+name);
//		System.out.println("TO -> " + toUserName);
//		System.out.println("FROM -> " + fromName);
//		System.out.println("groupMsgSenderName -> " + groupMsgSenderName);

        viewholder.isBroadCast = SharedPrefManager.getInstance().isBroadCast(toUserName);
        Log.d("ChatHomeAdapter", "name in bind view of ChatHomeAdapter class : "+name);
        viewholder.displayName = name+"";
        if(name!=null && name.contains("#786#"))
        	name = name.substring(0, name.indexOf("#786#"));
        if(name.startsWith(Constants.SHARED_ID_START_STRING)){
        	name = name.substring(name.indexOf(Constants.SHARED_ID_START_STRING) + Constants.SHARED_ID_START_STRING.length());
        	if(groupMsgSenderName != null && groupMsgSenderName.contains("<") && groupMsgSenderName.contains(">"))
        		fromName = groupMsgSenderName.substring(groupMsgSenderName.indexOf('<') + 1, groupMsgSenderName.indexOf('>'));
        	isSharedID = true;
        	viewholder.isBroadCast = false;
        }else if(groupMsgSenderName != null && groupMsgSenderName.contains("<") && groupMsgSenderName.contains(">")){
        	if(!fromName.equals(SharedPrefManager.getInstance().getUserName()))
        		name = name + "@" + groupMsgSenderName.substring(0, groupMsgSenderName.indexOf('<'));
        	fromName = groupMsgSenderName.substring(groupMsgSenderName.indexOf('<') + 1, groupMsgSenderName.indexOf('>'));
        	isSharedID = true;
        	viewholder.isBroadCast = false;
        }else if(iChatPref.isSharedIDContact(fromName))
        	isSharedID = true;
        else
        	isSharedID = false;
        viewholder.userName = fromName;
        if (viewholder.userName.equals(userMe) && !viewholder.isBroadCast)//if (viewholder.userName.equals(userMe) && !viewholder.isBroadCast)
			viewholder.userName = toUserName;
        if(viewholder.isBroadCast){
        	viewholder.userName = toUserName;
        }
        
        if(SharedPrefManager.getInstance().isMute(viewholder.userName))
        	viewholder.muteIcon.setVisibility(View.VISIBLE);
        else
        	viewholder.muteIcon.setVisibility(View.GONE);

        String msg_icon = null;
		String msgTime = null;
		String audio_len = null;
		long time = cursor.getLong(cursor.getColumnIndex(DatabaseConstants.LAST_UPDATE_FIELD));
		int messageType = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.MESSAGE_TYPE_FIELD));
		if(time != 0) {
			calander.setTimeInMillis(time);
			SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
			int days = daysBetween(currentCalender, calander);
			msgTime = format.format(calander.getTime());
			if (days == 1) {
				msgTime = "Yesterday";
			} else if (days > 1 && days < 7) {
				format = new SimpleDateFormat("EEE,hh:mm aa");
				msgTime = format.format(calander.getTime());
			} else if (days >= 7) {
				format = new SimpleDateFormat("dd/MM/yy");
				msgTime = format.format(calander.getTime());
			}
		}

        int messageCount = iChatPref.getChatCountOfUser(viewholder.userName);
        if(isSharedID)
        	name = iChatPref.getSharedIDDisplayName(viewholder.userName);
        else
        	name = iChatPref.getGroupDisplayName(name);
        if(viewholder.isBroadCast)
        	viewholder.nameText = iChatPref.getBroadCastDisplayName(toUserName);
        else if(isSharedID){
        	if(iChatPref.isSharedIDContact(name))
        		viewholder.nameText = iChatPref.getUserServerName(groupMsgSenderName) + "@"+ iChatPref.getSharedIDDisplayName(name);
			else if(iChatPref.isSharedIDContact(toUserName)){
				viewholder.nameText = name;
			}
        	else {
				if(groupMsgSenderName.contains("#786#"))
					viewholder.nameText = groupMsgSenderName.substring(0, groupMsgSenderName.indexOf("#786#")) + "@" + name;
				else viewholder.nameText = iChatPref.getUserServerName(groupMsgSenderName) + "@" + name;
			}
        }
        else{
        	if(toUserName != null && toUserName.equals(name)){
        		viewholder.nameText = name = iChatPref.getUserServerName(name);
        	}
        	else
        		viewholder.nameText = name;
        }
         String usersMessagesCount = String.valueOf(messageCount);
		if(time != 0) {
			viewholder.lastMessageTime.setVisibility(View.VISIBLE);
			viewholder.lastMessageTime.setText(msgTime);
		}else
			viewholder.lastMessageTime.setVisibility(View.GONE);
         if(messageCount > 0){
        	 viewholder.unseenMessages.setVisibility(TextView.VISIBLE);
	         viewholder.unseenMessages.setText(usersMessagesCount);
         }else{
        	 viewholder.unseenMessages.setVisibility(TextView.GONE);
         }
         if(iChatPref.isGroupChat(fromName)){
        	 String tmpName = SharedPrefManager.getInstance().getGroupDisplayName(fromName);
        	 if(tmpName.contains("##$^##"))
         		viewholder.chatPerson.setText(tmpName.substring(0, tmpName.indexOf("##$^##")));
         	else
         		viewholder.chatPerson.setText(tmpName);
         }else if(viewholder.isBroadCast){
        	 String tmpName = SharedPrefManager.getInstance().getBroadCastDisplayName(toUserName);
        	 if(tmpName.contains("##$^##"))
         		viewholder.chatPerson.setText(tmpName.substring(0, tmpName.indexOf("##$^##")));
         	else
         		viewholder.chatPerson.setText(tmpName);
     	}else if(isSharedID){
     		if(viewholder.nameText != null && viewholder.nameText.contains("<") && viewholder.nameText.contains(">")){
     			viewholder.nameText = viewholder.nameText.substring(0, groupMsgSenderName.indexOf('<'));
     			viewholder.chatPerson.setText(viewholder.nameText);
       	 }else
     		viewholder.chatPerson.setText(viewholder.nameText);
     	}else if (name!=null)
        {
        	String tmpName = name.trim();
        	if(tmpName.equals(fromName)|| tmpName.equals(toUserName)){
        		if(tmpName.contains("_"))
        			tmpName = tmpName.substring(0, tmpName.indexOf("_"));
        		}
        	if(tmpName.contains("##$^##"))
        		viewholder.chatPerson.setText(tmpName.substring(0, tmpName.indexOf("##$^##")));
        	else
        		viewholder.chatPerson.setText(tmpName);
        }
        if (msg!=null)
        {
        	if(iChatPref.isGroupChat(viewholder.userName)){
        		 if(groupMsgSenderName!=null && groupMsgSenderName.contains("#786#")){
        			 groupMsgSenderName = groupMsgSenderName.substring(0, groupMsgSenderName.indexOf("#786#"))+": ";
//        			 msg =  groupMsgSenderName+": "+msg;
        			 viewholder.id_last_msg_sender.setVisibility(View.VISIBLE);
        			 viewholder.id_last_msg_sender.setText(groupMsgSenderName);
        		}else{
//        			if(msg!=null && !msg.contains("created by") && !msg.contains("You have removed")  
//        					&& !msg.contains("You have added") && !msg.contains("have added \"You\"")){
        			if(msg!=null && !msg.contains("created by") && !msg.contains("have removed") && !msg.contains("updated group")  
        					&& !msg.contains("have added")){
        				groupMsgSenderName ="You: ";
        				viewholder.id_last_msg_sender.setVisibility(View.VISIBLE);
        				viewholder.id_last_msg_sender.setText(groupMsgSenderName);
        			}else
        				 viewholder.id_last_msg_sender.setVisibility(View.GONE);
    			}
        	}
        	else
        		 viewholder.id_last_msg_sender.setVisibility(View.GONE);
        	
//        	if(messageType == Constants.MESSAGE_PICTURE)//if(msg.startsWith(AppConstants.PIC_SEP))
//        		viewholder.lastMessage.setText("Picture Message");
//        	else
        	//🎤📷⛰�??📹📌📊📑📰📦🎫
        	switch(messageType){
        	case Constants.MESSAGE_TEXT:
        		Log.i("ChatHomeAdapter", "MESSAGE_TEXT");
        		msg_icon = null;
        		break;
        	case Constants.MESSAGE_PICTURE:
        		Log.i("ChatHomeAdapter", "MESSAGE_PICTURE");
        		msg_icon = "📷";
        		if(caption != null)
        			msg = caption;
        		else
        			msg = "Image";
        		break;
        	case Constants.MESSAGE_AUDIO:
        		Log.i("ChatHomeAdapter", "MESSAGE_AUDIO");
        		byte minutes = 0;
        		byte seconds = 0;
        		try{
        			audio_len = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LENGTH));
        		}catch(Exception ex){
        			ex.printStackTrace();
        		}
        		msg_icon = "🎤";
        		try{
	        		int total = Integer.parseInt(audio_len);
	        		minutes = (byte) (total/60);
	        		seconds = (byte) (total%60);
        		}catch(NumberFormatException nex){
        			nex.printStackTrace();
        		}
        		if(minutes == 0 && seconds == 0)
        			msg = "Voice";
        		else
        			msg = "Voice " + minutes + ":" + ((seconds < 10) ? ("0"+seconds) : seconds);
//        		msg = "Voice " + ((minutes < 10) ? ("0"+minutes) : minutes) + ":" + ((seconds < 10) ? ("0"+seconds) : seconds);
        		break;
        	case Constants.MESSAGE_VIDEO:
        		Log.i("ChatHomeAdapter", "MESSAGE_VIDEO");
        		msg_icon = "📹";
        		if(caption != null)
        			msg = caption;
        		else
        			msg = "Video";
        		break;
        	case Constants.MESSAGE_LOCATION:
        		Log.i("ChatHomeAdapter", "MESSAGE_LOCATION");
        		msg_icon = "📌";
        		msg = "Location";
        		break;
        	case Constants.MESSAGE_PDF:
        		Log.i("ChatHomeAdapter", "MESSAGE_PDF");
        		msg_icon = "🅿";
        		if(caption != null)
        			msg = caption;
        		break;
        	case Constants.MESSAGE_DOC:
        		Log.i("ChatHomeAdapter", "MESSAGE_DOC");
        		msg_icon = "📑";
        		if(caption != null)
        			msg = caption;
        		break;
        	case Constants.MESSAGE_PPT:
        		Log.i("ChatHomeAdapter", "MESSAGE_PPT");
        		msg_icon = "📰";
        		if(caption != null)
        			msg = caption;
        		break;
        	case Constants.MESSAGE_XLS:
        		Log.i("ChatHomeAdapter", "MESSAGE_XLS");
        		msg_icon = "📊";
        		if(caption != null)
        			msg = caption;
        		break;
        	case Constants.MESSAGE_CONTACT:
        		Log.i("ChatHomeAdapter", "MESSAGE_CONTACT");
        		msg_icon = "🎫";
        		msg = "Contact";
        		break;
        	}
        	if(msg_icon == null)
        		viewholder.lastMessageIcon.setVisibility(View.GONE);
        	else{
        		viewholder.lastMessageIcon.setVisibility(View.VISIBLE);
//        		viewholder.lastMessageIcon.setText(msg_icon);
        		switch(messageType){
            	case Constants.MESSAGE_PICTURE:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_camera);
        		break;
            	case Constants.MESSAGE_AUDIO:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_audio);
            		break;
            	case Constants.MESSAGE_VIDEO:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_video);
            		break;
            	case Constants.MESSAGE_LOCATION:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_share_location);
            		break;
            	case Constants.MESSAGE_PDF:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_pdf);
            		break;
            	case Constants.MESSAGE_DOC:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_document);
            		break;
            	case Constants.MESSAGE_PPT:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_ppt);
            		break;
            	case Constants.MESSAGE_XLS:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_excel);
            		break;
            	case Constants.MESSAGE_CONTACT:
            		viewholder.lastMessageIcon.setImageResource(R.drawable.emo_contact_share);
            		break;
        		}
        	}
        	
//           	Log.i("ChatHome", "toUserName : "+toUserName+", fromName : "+fromName);
           	
        		if(iChatPref.isGroupChat(viewholder.userName)){
        			if(iChatPref.isGroupMemberActive(viewholder.userName, iChatPref.getUserName())){
        				String temp = null;
        			if(!SharedPrefManager.getInstance().getUserTypingStatusForGroup(viewholder.userName).equals("")){
            			displayName = SharedPrefManager.getInstance().getUserTypingStatusForGroup(viewholder.userName);
            			temp = DBWrapper.getInstance().getChatName(displayName);
            			if(temp.equalsIgnoreCase(displayName))
            				displayName = "New User";
            			else
            				displayName = temp;
            			 if(displayName != null && displayName.contains("#786#"))
            				 displayName = displayName.substring(0, displayName.indexOf("#786#"));
            			msg = displayName + " is typing...";
            			viewholder.lastMessageIcon.setVisibility(View.GONE);
            			 viewholder.id_last_msg_sender.setVisibility(View.GONE);
            		}else if(!SharedPrefManager.getInstance().getUserRecordingStatusForGroup(viewholder.userName).equals("")){
            			displayName = SharedPrefManager.getInstance().getUserRecordingStatusForGroup(viewholder.userName);
            			temp = DBWrapper.getInstance().getChatName(displayName);
            			if(temp.equalsIgnoreCase(displayName))
            				displayName = "New User";
            			else
            				displayName = temp;
            			if(displayName != null && displayName.contains("#786#"))
            				 displayName = displayName.substring(0, displayName.indexOf("#786#"));
            			 msg = displayName + " is recording...";
            			 viewholder.lastMessageIcon.setVisibility(View.GONE);
            			 viewholder.id_last_msg_sender.setVisibility(View.GONE);
            		}else  if(!SharedPrefManager.getInstance().getUserListeningStatusForGroup(viewholder.userName).equals("")){
                 		displayName = SharedPrefManager.getInstance().getUserListeningStatusForGroup(viewholder.userName);
                 		temp = DBWrapper.getInstance().getChatName(displayName);
            			if(temp.equalsIgnoreCase(displayName))
            				displayName = "New User";
            			else
            				displayName = temp;
             			if(displayName != null && displayName.contains("#786#"))
             				 displayName = displayName.substring(0, displayName.indexOf("#786#"));
             			msg = displayName + " is listening...";
             			viewholder.lastMessageIcon.setVisibility(View.GONE);
             			 viewholder.id_last_msg_sender.setVisibility(View.GONE);
             		}
//            		else
//             			msg = groupMsgSenderName+":"+msg;
        			}
        		}else {
        			if(toUserName.equals(SharedPrefManager.getInstance().getUserName())){
        				if(SharedPrefManager.getInstance().getUserTypingStatus(fromName)){
                    		msg = "Typing...";
                    		viewholder.lastMessageIcon.setVisibility(View.GONE);
                    		 viewholder.id_last_msg_sender.setVisibility(View.GONE);
                		}else if(SharedPrefManager.getInstance().getUserRecordingStatus(fromName)){
                			msg = "Recording...";
                			viewholder.lastMessageIcon.setVisibility(View.GONE);
                			 viewholder.id_last_msg_sender.setVisibility(View.GONE);
                		} else if(SharedPrefManager.getInstance().getUserListeningStatus(fromName)){
                    		msg = "Listening...";
                    		viewholder.lastMessageIcon.setVisibility(View.GONE);
                    		 viewholder.id_last_msg_sender.setVisibility(View.GONE);
                		}
        			}else{
        				if(SharedPrefManager.getInstance().getUserTypingStatus(toUserName)){
                    		msg = "Typing...";
                    		viewholder.lastMessageIcon.setVisibility(View.GONE);
                    		 viewholder.id_last_msg_sender.setVisibility(View.GONE);
                		} else if(SharedPrefManager.getInstance().getUserRecordingStatus(toUserName)){
                    		msg = "Recording...";
                    		viewholder.lastMessageIcon.setVisibility(View.GONE);
                    		 viewholder.id_last_msg_sender.setVisibility(View.GONE);
                		} else if(SharedPrefManager.getInstance().getUserListeningStatus(toUserName)){
                    		msg = "Listening...";
                    		viewholder.lastMessageIcon.setVisibility(View.GONE);
                    		 viewholder.id_last_msg_sender.setVisibility(View.GONE);
                		}
        			}
        		}
			if(msg!=null && msg.contains("You are welcome")) {
				if(!viewholder.isBroadCast)
					viewholder.lastMessage.setText("Group created by");
				else
					viewholder.lastMessage.setText("Broadcast list created by");
			}else
        		viewholder.lastMessage.setText(msg);
        }
        view.setTag(viewholder);

		viewholder.moreTab.setVisibility(TextView.GONE);
		viewholder.deleteTab.setVisibility(TextView.GONE);
		if(iChatPref.isGroupChat(toUserName))
			viewholder.personImageDefault.setTag(toUserName);
		else
			viewholder.personImageDefault.setTag(fromName);
//		System.out.println("[[[ Image User - "+fromName);
		if (fromName.equals(userMe)){
			if(isSharedID)
				setProfilePic(viewholder.personImage, viewholder.personImageDefault, viewholder.nameText, toUserName, (byte)1);
			else
				setProfilePic(viewholder.personImage, viewholder.personImageDefault, viewholder.nameText, toUserName, (byte)0);
//			viewholder.fileId = SharedPrefManager.getInstance().getUserFileId(toUserName);
		}else{
			if(isSharedID){
				if(groupMsgSenderName != null && groupMsgSenderName.contains("<") && groupMsgSenderName.contains(">")){
					fromName = groupMsgSenderName.substring(groupMsgSenderName.indexOf('<') + 1, groupMsgSenderName.indexOf('>') );
		        }
				setProfilePic(viewholder.personImage, viewholder.personImageDefault, viewholder.nameText, fromName, (byte)1);
			}
			else
				setProfilePic(viewholder.personImage, viewholder.personImageDefault, viewholder.nameText, fromName, (byte)0);
//			viewholder.fileId = SharedPrefManager.getInstance().getUserFileId(fromName);
		}
	}

	public View newView(Context context1, Cursor cursor, ViewGroup viewgroup) {
		myParent = viewgroup;
		View view = LayoutInflater.from(context).inflate(layout, null);
		ViewHolder viewholder = new ViewHolder();
		viewholder.lastMessage = (TextView) view.findViewById(R.id.id_last_message);
		viewholder.muteIcon = (ImageView) view.findViewById(R.id.id_mute_icon);
		viewholder.lastMessageIcon = (ImageView) view.findViewById(R.id.id_last_message_icon);
		viewholder.id_last_msg_sender = (TextView) view.findViewById(R.id.id_last_msg_sender);
		viewholder.chatPerson = (TextView) view.findViewById(R.id.id_chat_person);
		viewholder.lastMessageTime = (TextView) view.findViewById(R.id.id_message_time);
		viewholder.unseenMessages = (TextView) view.findViewById(R.id.id_unseen_count);
		viewholder.moreTab = (TextView) view.findViewById(R.id.id_more_tab);
		viewholder.deleteTab = (TextView) view.findViewById(R.id.id_delete_tab);

		viewholder.personImage = (ImageView) view.findViewById(R.id.contact_icon);
		viewholder.personImageDefault = (ImageView) view.findViewById(R.id.contact_icon_default);

		// if (isEditableContact){
		// viewholder.personImage.setOnClickListener(viewholder.onCheckeClickListener);
		// viewholder.name.setOnClickListener(viewholder.onCheckeClickListener);
		// viewholder.iCheckBox.setOnClickListener(viewholder.onCheckeClickListener);
		view.setOnClickListener(viewholder.onCheckeClickListener);
		viewholder.moreTab.setOnClickListener(viewholder.onCheckeClickListener);
		viewholder.personImage.setOnClickListener(viewholder.onCheckeClickListener);
		viewholder.personImageDefault.setOnClickListener(viewholder.onCheckeClickListener);
		viewholder.deleteTab
				.setOnClickListener(viewholder.onCheckeClickListener);
		view.setOnLongClickListener(viewholder.onLongClickListener);
		// }

		viewholder.moreTab.setVisibility(TextView.GONE);
		viewholder.deleteTab.setVisibility(TextView.GONE);

		String s = cursor.getString(cursor
				.getColumnIndex(DatabaseConstants.FROM_USER_FIELD));
		viewholder.id = s;
		view.setTag(viewholder);
//		displayImage(viewholder.personImage, s, false);
//		 String fromName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.FROM_USER_FIELD));
//	        String toUserName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.TO_USER_FIELD));
//		if (fromName.equals(userMe))
//			setProfilePic(viewholder.personImage, toUserName);
//		else
//			setProfilePic(viewholder.personImage, fromName);
		return view;
	}
	private void displayImage(ImageView imageview, String s, boolean flag) {
		android.graphics.Bitmap bitmap = SuperChatApplication
				.getBitmapFromMemCache(s);
		if (bitmap != null) {
			imageview.setImageBitmap(bitmap);
		} else {
			imageview.setImageResource(R.drawable.avatar);
		}
		if (!flag && bitmap == null) {
			PhotoToLoad phototoload = new PhotoToLoad(imageview, s);
			executorService.execute(new ImageLoaderWorker(phototoload));
		}
	}
	 private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
	    private TextDrawable.IBuilder mDrawableBuilder;

	HashMap processing = new HashMap();
	public void addProcessing(String file_id){
		processing.put(file_id, "1");
	}
	public void removeProcessing(String file_id){
		processing.remove(file_id);
	}
	public boolean inProcessing(String file_id){
		return processing.containsKey(file_id);
	}
	public void updateUI(){
//		try{
//			((ChatHome)context).runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					System.out.println("[updateUI ]");
//					notifyDataSetChanged();
//				}
//			});
//			notifyDataSetChanged();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
		notifyDataSetChanged();
	}
	public void setProfilePic(ImageView view, ImageView view_default, String displayName, String userName, byte type){
		String groupPicId = null;
		if(SharedPrefManager.getInstance().isSharedIDContact(userName))
			type = 1;
		if(type == 1)
			groupPicId = SharedPrefManager.getInstance().getSharedIDFileId(userName);
		else
			groupPicId = SharedPrefManager.getInstance().getUserFileId(userName);
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		if (bitmap != null) {
			view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			view.setImageBitmap(bitmap);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto+profilePicUrl;
			view.setTag(filename);
		}else if(groupPicId!=null && !groupPicId.equals("") && !groupPicId.equals("clear")){
			String profilePicUrl = null;
			if(groupPicId.endsWith(".jpg"))
				profilePicUrl = groupPicId;
			else
				profilePicUrl = groupPicId+".jpg";
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto+profilePicUrl;
			view.setTag(filename);
			File file1 = new File(filename);
			if(file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				Log.i("", "setProfilePic : filename : "+filename);
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				if(iChatPref.isGroupChat(userName))
					view.setImageResource(R.drawable.chat_person);
				else  if(iChatPref.isBroadCast(userName))
					view.setImageResource(R.drawable.announce);
				else{
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
				}
					
				setThumb(view, view_default,filename, groupPicId);
//				view.setBackgroundDrawable(null);

				}else{
					//Downloading the file
					
					if(iChatPref.isGroupChat(userName)){
						view_default.setVisibility(View.INVISIBLE);
						view.setVisibility(View.VISIBLE);
						view.setImageResource(R.drawable.chat_person);
						if (Build.VERSION.SDK_INT >= 11)
							new BitmapDownloader((RoundedImageView)view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
			             else
			            	 new BitmapDownloader((RoundedImageView)view).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
					}else if(iChatPref.isBroadCast(userName) && type != 1){
						view_default.setVisibility(View.INVISIBLE);
						view.setVisibility(View.VISIBLE);
						view.setImageResource(R.drawable.announce);
						if (Build.VERSION.SDK_INT >= 11)
							new BitmapDownloader((RoundedImageView)view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
			             else
			            	 new BitmapDownloader((RoundedImageView)view).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
					}else{
						try{
							if(displayName != null && displayName.trim().length() > 0){
								String name_alpha = String.valueOf(displayName.charAt(0));
								if (displayName.contains(" ") && displayName.indexOf(' ') < (displayName.length() - 1))
									name_alpha += displayName.substring(displayName.indexOf(' ') + 1).charAt(0);
								TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(displayName));
								view.setVisibility(View.INVISIBLE);
								view_default.setVisibility(View.VISIBLE);
								view_default.setImageDrawable(drawable);
								view_default.setBackgroundColor(Color.TRANSPARENT);
							}
						}catch(Exception ex){
							ex.printStackTrace();
						}
//						if(!processing.containsKey(groupPicId))
						{
							if (Build.VERSION.SDK_INT >= 11)
								new BitmapDownloader(this, (RoundedImageView) view, view_default).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
							else
								new BitmapDownloader(this, (RoundedImageView) view, view_default).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
						}
					}
						 
//					(new ProfilePicDownloader()).download(Constants.media_get_url+groupPicId+".jpg", (RoundedImageView)view, null);
					
				}
		}else if(iChatPref.isBroadCast(userName)){
			view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			view.setImageResource(R.drawable.announce);
			view.setTag(userName);
		}
		else if(iChatPref.isGroupChat(userName)){
			view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			view.setImageResource(R.drawable.chat_person);
			view.setTag(userName);
		}
		else{
//			if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
//				view.setImageResource(R.drawable.female_default);
//			else
//				view.setImageResource(R.drawable.male_default);
			try{
				if(type ==1){
					view_default.setImageResource(R.drawable.small_helpdesk);
					view.setImageResource(R.drawable.small_helpdesk);
				}else{
					String name_alpha = String.valueOf(displayName.charAt(0));
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
	private void setThumb(ImageView imageViewl,ImageView defaultImageView,String path,String groupPicId){
		BitmapFactory.Options bfo = new BitmapFactory.Options();
	    bfo.inSampleSize = 2;
	    Bitmap bm = null;
	    try{
		    bm = BitmapFactory.decodeFile(path);//, bfo);
//		    bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
		    bm = rotateImage(path, bm);
//		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
	    }catch(Exception ex){
	    	
	    }
	    if(bm!=null){
	    	imageViewl.setVisibility(View.VISIBLE);
	    	defaultImageView.setVisibility(View.INVISIBLE);
	    	imageViewl.setImageBitmap(bm);
//	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
	    } else{
	    	try{
	    		imageViewl.setVisibility(View.VISIBLE);
	    		defaultImageView.setVisibility(View.INVISIBLE);
	    		imageViewl.setImageURI(Uri.parse(path));
	    	}catch(Exception e){
	    		imageViewl.setVisibility(View.INVISIBLE);
	    		defaultImageView.setVisibility(View.VISIBLE);
	    	}
	    }
	}
	public void setDirectCallDisabled(boolean flag) {
	}
	private ArrayList<String> convertNames(ArrayList<String> arrayList){
		nameMap.clear();
		ArrayList<String> displayList = new ArrayList<String>();
		for(String tmp:arrayList){
			String value = DBWrapper.getInstance().getChatName(tmp);
			if(iChatPref.getUserName().equals(tmp))
				value = "You";
			 if(value!=null && value.contains("#786#"))
				 value = value.substring(0, value.indexOf("#786#"));
			 if(value!=null && value.equals(tmp))
				 value =  value.replace("m", "+");
			displayList.add(value);
			nameMap.put(tmp,value);
		}
		Collections.sort(displayList,String.CASE_INSENSITIVE_ORDER);
		return displayList;
	}
	public void updateGroupUsersList(ArrayList<String> displayList) {
		if (iChatPref.isGroupChat(optionUser)) {
			//ChatDBWrapper.getInstance().getUsersOfGroup(senderName);
			String list = displayList.toString();
			if (list == null) {
				list = "";
			}
			if (list.contains("[")) {
				list = list.replace("[", "");
				list = list.replace("]", "");
			}
			
		}
	}
	public void saveMessage(String displayName, String from, String msg) {
		try {
			ChatDBWrapper dbwrapper = ChatDBWrapper.getInstance();
			ContentValues contentvalues = new ContentValues();
			String myName = SharedPrefManager.getInstance().getUserName();
			contentvalues.put(DatabaseConstants.FROM_USER_FIELD, from);
			contentvalues.put(DatabaseConstants.TO_USER_FIELD, myName);
			contentvalues.put(DatabaseConstants.UNREAD_COUNT_FIELD,
					new Integer(1));
			contentvalues.put(DatabaseConstants.FROM_GROUP_USER_FIELD, "");
			contentvalues.put(DatabaseConstants.SEEN_FIELD,
					SeenState.sent.ordinal());

			contentvalues.put(DatabaseConstants.MESSAGEINFO_FIELD, msg);
			// String name =
			// cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));

			String name = "";
			String oppName = "";
			{
				oppName = from;
				name = dbwrapper.getChatName(from);
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
			long milis = DBWrapper.getInstance().lastMessageInDB(oppName);
			if(milis!=-1){
				calender.setTimeInMillis(milis);
				oldDate = calender.get(Calendar.DATE);
			}
			if ((oldDate != date)
					|| DBWrapper.getInstance().isFirstChat(oppName)) {
				contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "1");
			} else {
				contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "0");
			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(DatabaseConstants.LAST_UPDATE_FIELD, currentTime);

			contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, name);
			dbwrapper.insertInDB(DatabaseConstants.TABLE_NAME_MESSAGE_INFO,
					contentvalues);
		} catch (Exception e) {

		}
	}
	@Override
	public void onClick(View v) {
		if(moreOptionDialog!=null)
			moreOptionDialog.cancel();
		switch(v.getId()){
		case R.id.id_clear_chat:
			SharedPrefManager prefObj = SharedPrefManager.getInstance();
			prefObj.saveChatCountOfUser(optionUser, 0);
			ChatDBWrapper.getInstance().deleteRecentUserChat(userDisplayName);
			if(isGroup)
				saveMessage(userDisplayName, optionUser,"All conversations are cleared.");
			Cursor cursor1 = ChatDBWrapper.getInstance().getRecentChatList(null);
				swapCursor(cursor1);
//				setAdapter(adapter);
			notifyDataSetChanged();
			break;
		case R.id.id_group_info:
			Intent intent = new Intent(context, GroupProfileScreen.class);
			intent.putStringArrayListExtra(Constants.GROUP_USERS, usersList);
			intent.putExtra(Constants.USER_MAP, nameMap);
			intent.putExtra(Constants.CHAT_USER_NAME, optionUser);
			intent.putExtra(Constants.CHAT_NAME, iChatPref.getGroupDisplayName(optionUser));
			((HomeScreen)context).startActivity(intent);
			break;
		case R.id.id_email_chat:
			String selChat = "";
			ArrayList<String> textList = ChatDBWrapper.getInstance().getChatHistory((String)v.getTag());
			for(String msg:textList)
				selChat = selChat + msg + "\n";
			
			int listSize = textList.size();
			if (selChat != null && selChat.length() > 0 && !selChat.equals("")) {

				final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
				mailIntent.setType("text/plain");
				mailIntent.putExtra(Intent.EXTRA_SUBJECT, ((HomeScreen)context).getString(R.string.app_name));
				mailIntent.putExtra(Intent.EXTRA_TEXT, selChat.trim());
		        final PackageManager pm = ((HomeScreen)context).getPackageManager();
		        final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
		        ResolveInfo best = null;
		        for (final ResolveInfo info : matches)
		           if (info.activityInfo.packageName.endsWith(".gm") ||
		        info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
		        if (best != null)
		        	mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		        ((HomeScreen)context).startActivity(mailIntent);
			}
			break;
		case R.id.id_cancel:
			
		
		}
		
		
	}
}
