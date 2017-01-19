package com.superchat.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.ui.ChatListAdapter.ViewHolder;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.RTMediaPlayer;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.utils.VoiceMediaHandler;
import com.superchat.widgets.MyriadSemiboldTextView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class UsersProfileScreen extends Activity implements OnClickListener,VoiceMediaHandler{
	private String userName;
	private String displayName = "";
	private String statusMessage = "";
	private TextView backTitleView;
	private TextView clearChatView;
	private TextView blockUserView;
	private TextView emailChatView;
//	private TextView allMediaView;
	private TextView blockContactView;
	private ImageView addContactIv;
	private TextView userBlockView;
	private TextView userNumberView;
	private TextView viewProfileView;
	private LinearLayout mediaScrollLayout;
	private LinearLayout docsScrollLayout;
	private TextView mediaView;
	private TextView docsView;
	private TextView mediaCountView;
	private TextView docsCountView;
	SharedPrefManager prefManager;
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
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.users_info_screen);

		UtilSetFont.setFontMainScreen(this);

		prefManager = SharedPrefManager.getInstance();
		mediaScrollLayout = (LinearLayout)findViewById(R.id.id_media_scroll_view); 
		docsScrollLayout = (LinearLayout)findViewById(R.id.id_docs_scroll_view); 
		
		mediaCountView = (TextView)findViewById(R.id.id_media_count); 
		docsCountView = (TextView)findViewById(R.id.id_docs_count); 
		
		viewProfileView = (TextView)findViewById(R.id.id_view_profile); 
		mediaView = (TextView)findViewById(R.id.id_view_all_media); 
		docsView = (TextView)findViewById(R.id.id_view_all_docs); 
		backTitleView = (TextView)findViewById(R.id.id_back_title); 
		clearChatView = (TextView)findViewById(R.id.id_clear_chat); 
		blockUserView = (TextView)findViewById(R.id.id_block_user); 
		
		blockUserView.setOnClickListener(this);
		clearChatView.setOnClickListener(this);
		emailChatView = (TextView) findViewById(R.id.id_email_chat);
		emailChatView.setOnClickListener(this);

//		allMediaView = (TextView) findViewById(R.id.id_view_all_media);
//		allMediaView.setOnClickListener(this);
		emailChatView = (TextView) findViewById(R.id.id_add_member);
		emailChatView.setOnClickListener(this);

		userBlockView = (TextView) findViewById(R.id.id_user_block);
		userNumberView = (TextView) findViewById(R.id.id_user_number);
		addContactIv = (ImageView)findViewById(R.id.add_contact_plus);
		addContactIv.setOnClickListener(this);
		viewProfileView.setOnClickListener(this);
		mediaView.setOnClickListener(this);
		docsView.setOnClickListener(this);
		Bundle tmpBundle = getIntent().getExtras();
		if(tmpBundle!=null){
			userName = tmpBundle.get(Constants.CHAT_USER_NAME).toString();
			displayName = tmpBundle.get(Constants.CHAT_NAME).toString();
			if(userName!=null){
				statusMessage = SharedPrefManager.getInstance().getUserStatusMessage(userName);
				if(statusMessage == null || statusMessage.equals("")){
					statusMessage = "";
					userNumberView.setVisibility(View.GONE);
				}else if(!statusMessage.trim().equals("")){
					statusMessage = " Status: \""+statusMessage.trim()+"\"";
					
					
				}
				if(prefManager.isBlocked(userName))
					blockUserView.setText(getString(R.string.unblock));
				else
					blockUserView.setText(getString(R.string.block));
//				String degignation = SharedPrefManager.getInstance().getUserDesignation(userName);
//				if(degignation!=null && !degignation.equals(""))
//					statusMessage += " \n \n Designation: \""+degignation.trim()+"\"";
//				
//				String department = SharedPrefManager.getInstance().getUserDepartment(userName);
//				if(department!=null && !department.equals(""))
//					statusMessage += " \n \n Department: \""+department.trim()+"\"";
				
//					String phoneNumber = DBWrapper.getInstance(SuperChatApplication.context).getContactNumber(userName);
//					if(phoneNumber == null || phoneNumber.equals("")){
//						if(userName!=null && userName.contains("_"))
//							userNumberView.setText("+"+userName.substring(0, userName.indexOf("_"))+statusMessage);//userName.replaceFirst("m", "+").replace("-", "")+statusMessage);
//					}else {
//						if(phoneNumber.contains("-"))
//							userNumberView.setText("+"+phoneNumber.replace("-", "")+statusMessage);
//						else
//							userNumberView.setText("+"+phoneNumber+statusMessage);
//					}
				userNumberView.setText(statusMessage);
				}

		}
		if(displayName != null){
			if(displayName.length()>10)
				backTitleView.setText(displayName.substring(0, 10)+ "..");
			else
				backTitleView.setText(displayName);
			
			userBlockView.setText(displayName);
			Pattern p = Pattern.compile("([0-9])");
			Matcher m = p.matcher(displayName);

			if(m.find()){
				addContactIv.setVisibility(View.VISIBLE);
			}else{
				addContactIv.setVisibility(View.GONE);
			}
		}
		
//		Tracker t = ((SuperChatApplication) SuperChatApplication.context).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("User Profile Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());
		 setAllMedia();
		 setAllDocs();
	}
	 public void onResume() {
	        super.onResume();
	        bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);
    }
	 protected void onPause() {
		 try {
	            unbindService(mMessageConnection);
	        } catch (Exception e) {
	        }
	        super.onPause();
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
			myVoicePlayer.setMediaHandler(UsersProfileScreen.this);
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
		ArrayList<ContentValues> allDocs = ChatDBWrapper.getInstance().getAllPersonDocs(userName);
		docsCountView.setText(""+allDocs.size());
		for(ContentValues values : allDocs){
			addDocsView(docsScrollLayout, values.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD),values.getAsString(ChatDBConstants.MEDIA_CAPTION_TAG),values.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD));
		}
	}
	private void setAllMedia(){
		ArrayList<ContentValues> allMedia = ChatDBWrapper.getInstance().getAllPersonMedia(userName);
		mediaCountView.setText(""+allMedia.size());
		for(ContentValues values : allMedia){
			if(values.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD) == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
				addVideoView(mediaScrollLayout, values.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
			else if(values.getAsInteger(ChatDBConstants.MESSAGE_TYPE_FIELD) == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
				addAudioView(mediaScrollLayout, values.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
			else
				addImageView(mediaScrollLayout, values.getAsString(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
		}
	}
	private void addVideoView(LinearLayout mainLayout,String path){
		View view = new View(this);
		RelativeLayout rlLayout = new RelativeLayout(this);
		rlLayout.setBackgroundColor(Color.DKGRAY);
		RelativeLayout.LayoutParams rlLayoutParams = new   RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		rlLayoutParams.setMargins(0, 0, 0, 0);
		rlLayout.setLayoutParams(rlLayoutParams);
		
		view.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1)));
		view.setBackgroundColor(Color.GRAY);
		mainLayout.addView(view);
		ImageView imageView = new ImageView(this);
		imageView.setTag(path);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100, 75);
		params.setMargins(0, 0, 0, 0);
//		imageView.setLayoutParams((new   ViewGroup.LayoutParams(150,130)));//ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
		imageView.setLayoutParams(LayoutHelper.createLinear(100, 75, 2, 0, 2, 0));
		imageView.setBackgroundResource(R.color.red);
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
		setPicForCache(imageView,path);
		
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
		view.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 1)));
		view.setBackgroundColor(Color.GRAY);
		mainLayout.addView(view);
		ImageView imageView = new ImageView(this);
		imageView.setTag(path);
//		imageView.setLayoutParams((new   ViewGroup.LayoutParams(150,130)));//ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
		imageView.setLayoutParams(LayoutHelper.createLinear(100, 75, 2, 0, 2, 0));
		imageView.setBackgroundResource(R.color.gray);
		imageView.setBottom(2);
//		imageView.setPadding(2, 2, 0, 0);
		mainLayout.addView(imageView);
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
	public void onBackClick(View view) {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_block_user:
			if(!prefManager.isBlocked(userName))
				showBlockUnblockConfirmDialog(getString(R.string.confirmation),getString(R.string.block_confirmation));
        	else{
				showBlockUnblockConfirmDialog(getString(R.string.confirmation),getString(R.string.unblock_confirmation));
//			 if (Build.VERSION.SDK_INT >= 11)
//                 new BlockUnBlockInInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//             else
//            	 new BlockUnBlockInInfoTask().execute();
			 }
			break;
		case R.id.id_clear_chat:
//			SharedPrefManager prefObj = SharedPrefManager.getInstance();
//			prefObj.saveChatCountOfUser(userName, 0);
//			ChatDBWrapper.getInstance().deleteRecentUserChatByUserName(userName);
//			finish();
			showDialog("","Clear all messages.");
			break;
		case R.id.id_email_chat:

			String selChat = "";
			ArrayList<String> textList = ChatDBWrapper.getInstance().getChatHistory(userName);
			for(String msg:textList)
				selChat = selChat + msg + "\n";

			int listSize = textList.size();
			if (selChat != null && selChat.length() > 0 && !selChat.equals("")) {

				final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
				intent.putExtra(Intent.EXTRA_TEXT, selChat.trim());
				final PackageManager pm = getPackageManager();
				final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
				ResolveInfo best = null;
				for (final ResolveInfo info : matches)
					if (info.activityInfo.packageName.endsWith(".gm") ||
							info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
				if (best != null)
					intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
				startActivity(intent);
			}
			break;
		case R.id.id_add_member:
			Toast.makeText(UsersProfileScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
			break;
		case R.id.add_contact_plus:
			Intent intent = new Intent(Intent.ACTION_INSERT,  ContactsContract.Contacts.CONTENT_URI);
			intent.putExtra(ContactsContract.Intents.Insert.PHONE, displayName.trim());
			startActivity(intent);
			break;
		case R.id.id_view_profile:
			 intent = new Intent(this, ProfileScreen.class);
			 Bundle bundle = new Bundle();
			 bundle.putString(Constants.CHAT_USER_NAME, userName);
			 bundle.putString(Constants.CHAT_NAME, displayName);
			 intent.putExtras(bundle);
			 startActivity(intent);
			break;
		case R.id.id_view_all_media:
			Intent galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	        startActivity(galleryIntent);
			
//			String folderPath = Environment.getExternalStorageDirectory()+"/SuperChat";
//
//			intent = new Intent();  
//			intent.setAction(Intent.ACTION_GET_CONTENT);
//			Uri myUri = Uri.parse(folderPath);
//			intent.setDataAndType(myUri , "*/*");   
//			startActivity(intent);
			
			break;
		case R.id.id_view_all_docs:
//			galleryIntent = new Intent(Intent.ACTION_VIEW, android.provider.MediaStore.Images.Media.getContentUri("SuperChat"));
//	        startActivity(galleryIntent);
			break;
		}

	}
	private void showBlockUnblockConfirmDialog(final String title, final String s) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(true);
		bteldialog.setContentView(R.layout.custom_dialog_two_button);
		if(title!=null){
			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
			}
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_send)).setText(getString(R.string.yes));
		((TextView)bteldialog.findViewById(R.id.id_cancel)).setText(getString(R.string.no));
		((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				if (Build.VERSION.SDK_INT >= 11)
	                new BlockUnBlockInInfoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	            else
	           	 new BlockUnBlockInInfoTask().execute();
				 
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
	private class BlockUnBlockInInfoTask extends AsyncTask<String, Void, String> {
	    ProgressDialog dialog;
	boolean isStatusChanged = false;
	    BlockUnBlockInInfoTask() {
	    }

	    protected void onPreExecute() {

	        dialog = ProgressDialog.show(UsersProfileScreen.this, "", "Please wait...", true);

	        // progressBarView.setVisibility(ProgressBar.VISIBLE);
	        super.onPreExecute();
	    }

	    protected String doInBackground(String... args) {
	    	boolean isStatusChanged = false;
	    	if(prefManager.isBlocked(userName)){
	    		isStatusChanged = messageService.blockUnblockUser(userName,true);
	    		if(isStatusChanged)
	    			prefManager.setBlockStatus(userName, false);
	    	}else{
	    		isStatusChanged = messageService.blockUnblockUser(userName,false);
	    		if(isStatusChanged)
	    			prefManager.setBlockStatus(userName, true);
	    	}
	    	this.isStatusChanged = isStatusChanged;
	        return null;
	    }

	    protected void onPostExecute(String str) {
	    	super.onPostExecute(str);
	    	if(dialog!=null){
	    	 dialog.cancel();
	    	 dialog = null;
	    	} 
	    	if(isStatusChanged){
	    		if(prefManager.isBlocked(userName)){
	    			blockUserView.setText(getString(R.string.unblock));
	    			Toast.makeText(UsersProfileScreen.this, getString(R.string.block_successful), Toast.LENGTH_SHORT).show();
	    		}else{
	    			blockUserView.setText(getString(R.string.block));
	    			Toast.makeText(UsersProfileScreen.this, getString(R.string.unblock_successful), Toast.LENGTH_SHORT).show();
	    		}
	    	}else{
	    		Toast.makeText(UsersProfileScreen.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
	    	}
		}
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
				SharedPrefManager prefObj = SharedPrefManager.getInstance();
				prefObj.saveChatCountOfUser(userName, 0);
				ChatDBWrapper.getInstance().deleteRecentUserChatByUserName(userName);
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
	public void onDureationchanged(long total, long current, SeekBar currentSeekBar) {
		if(seekBar!=null)
		seekBar.setProgress((int)current);
	}
}
