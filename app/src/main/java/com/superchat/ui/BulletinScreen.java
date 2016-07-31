package com.superchat.ui;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.chat.sdk.ChatCountListener;
import com.chat.sdk.ChatService;
import com.chat.sdk.ConnectionStatusListener;
import com.chat.sdk.ProfileUpdateListener;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.BulletinScreenAdapter;
import com.superchat.widgets.RoundedImageView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BulletinScreen extends ListFragment  implements ChatCountListener,ConnectionStatusListener, ProfileUpdateListener, OnClickListener{//,TypingListener {
	public static final String TAG = "BulletinScreen";
	Cursor cursor;
	BulletinScreenAdapter adapter;
//	XmppChatClient chatClient;
	LinearLayout noneMessageView;
	private boolean onForeground;
	EditText searchBoxView;
	ImageView clearSearch;
	ImageView searchIcon;
	private RelativeLayout searchViewLayout;
	private RelativeLayout headerBar;
	ImageView xmppStatusView;
	ProgressBar progressBarView;
	private ChatService service;
	private XMPPConnection connection;
	private ListView recentList = null;
	FragmentActivity fragmentactivity;
	String bulletinDomainName;
	ImageView superGroupIcon;
	TextView superGroupName;
	public static final byte BULLETIN_ADMIN = 1;
	public static final byte BULLETIN_MEMBER = 2;
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((ChatService.MyBinder) binder).getService();
			Log.d("Service", "Connected");
			if (service != null) {
				connection = service.getconnection();
				service.setChatListener(BulletinScreen.this);
				service.setProfileUpdateListener(BulletinScreen.this);
//				service.setTypingListener(ChatHome.this);
				service.clearAllNotifications();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			connection = null;
			service = null;
		}
	};
	public void setProfileListener(){
		if(service != null)
			service.setProfileUpdateListener(BulletinScreen.this);
	}
	public View onCreateView(LayoutInflater layoutinflater,
			ViewGroup viewgroup, Bundle bundle) {
		
		bulletinDomainName = SharedPrefManager.getInstance().getUserDomain() + "-all";
		View view = layoutinflater.inflate(R.layout.chat_home, null);
		noneMessageView = (LinearLayout)view.findViewById(R.id.center_layout);
		searchBoxView = (EditText) view.findViewById(R.id.id_search_field);
		searchIcon = (ImageView)view.findViewById(R.id.id_search_icon);
		clearSearch = (ImageView)view.findViewById(R.id.id_back_arrow);
		searchViewLayout = (RelativeLayout)view.findViewById(R.id.id_search_layout);
		xmppStatusView = (ImageView)view.findViewById(R.id.id_xmpp_status);
		progressBarView = (ProgressBar)view.findViewById(R.id.id_loading);
		progressBarView.setVisibility(ProgressBar.VISIBLE);
		superGroupIcon = (ImageView)view.findViewById(R.id.id_sg_icon);
		superGroupName = (TextView)view.findViewById(R.id.id_sg_name_label);
		superGroupIcon.setOnClickListener(this);
		superGroupName.setOnClickListener(this);
		((ImageView)view.findViewById(R.id.id_compose_icon)).setVisibility(View.GONE);
		((TextView) view.findViewById(R.id.id_all_tab1)).setText(getString(R.string.bulletin_board));
		if(ChatService.xmppConectionStatus){
			xmppStatusView.setImageResource(R.drawable.blue_dot);
		}else{
			xmppStatusView.setImageResource(R.drawable.red_dot);
			}
//		headerBar = (RelativeLayout)view.findViewById(R.id.id_header);
//		headerBar.setBackgroundColor(R.color.header_color);
		searchBoxView.setVisibility(EditText.GONE);
		clearSearch.setVisibility(ImageView.GONE);
		
		((Button)view.findViewById(R.id.id_settings)).setVisibility(View.GONE);
		
		searchIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBoxView.setVisibility(View.VISIBLE);
				clearSearch.setVisibility(View.VISIBLE);
				searchIcon.setVisibility(View.GONE);
				superGroupIcon.setVisibility(View.GONE);
				superGroupName.setVisibility(View.GONE);
				
				searchBoxView.requestFocus();
				 InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                 imm.showSoftInput(searchBoxView, InputMethodManager.SHOW_IMPLICIT);
				clearSearch.setVisibility(ImageView.VISIBLE);
			}
		});
		clearSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				searchBoxView.setVisibility(View.GONE);
				searchIcon.setVisibility(View.VISIBLE);
				superGroupIcon.setVisibility(View.VISIBLE);
				superGroupName.setVisibility(View.VISIBLE);
				clearSearch.setVisibility(View.GONE);
				searchBoxView.setText("");		
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);		
			}
		});

//		superGroupName.setText(SharedPrefManager.getInstance().getUserDomain());
//		setSGProfilePic(superGroupIcon, SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID"));
		return view;
	}
	private String getThumbPath(String groupPicId)
	{
		if(groupPicId == null)
		 groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
		if(groupPicId!=null){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto+profilePicUrl;
			File contentFile = new File(filename);
			if(contentFile!=null && contentFile.exists()){
				return filename;
			}
			
		}
		return null;
	}
	private boolean setSGProfilePic(ImageView picView, String groupPicId){
		String img_path = getThumbPath(groupPicId);
		picView.setImageResource(R.drawable.about_icon);
			if(groupPicId == null || groupPicId.equals("clear") ||  groupPicId.contains("logofileid"))
				return false;
			if(img_path != null){
			File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				picView.setImageURI(Uri.parse(img_path));
				setThumb((ImageView) picView,img_path,groupPicId);
				return true;
			}else{
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader((RoundedImageView)picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
	             else
	            	 new BitmapDownloader((RoundedImageView)picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			}
		}else{
			if (Build.VERSION.SDK_INT >= 11)
				new BitmapDownloader((RoundedImageView)picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
             else
            	 new BitmapDownloader((RoundedImageView)picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			
		}
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
	private String getImagePath(String groupPicId){
		if(groupPicId == null)
		 groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
		if(groupPicId!=null){
			String profilePicUrl = groupPicId+".jpg";
			File file = Environment.getExternalStorageDirectory();
			return new StringBuffer(file.getPath()).append(File.separator).append("SuperChat/").append(profilePicUrl).toString();
		}
		return null;
	}
	 ProgressDialog dialog;
	public void onActivityCreated(Bundle bundle) {
		onForeground = true;
//		if(service!=null)
//			service.setTypingListener(this);
//		 chatClient = new XmppChatClient();
//		 SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//			chatClient.setCustomChatNotification(R.layout.message_notifier, R.id.chat_person_name, R.id.chat_message, R.id.chat_notification_bubble_text, R.drawable.chatgreen);
//			XmppChatClient.initChatService(EsiaChatApplication.context, iPrefManager.getUserName(), iPrefManager.getUserPassword(), Constants.CHAT_SERVER_URL);
		
		
		
//		ChatDBWrapper wraper = ChatDBWrapper.getInstance(EsiaChatApplication.context);
//		 cursor = wraper.getRecentChatList(null);
//		 recentList = getListView();
//		 if(cursor!=null && cursor.getCount() > 0){
//			 	noneMessageView.setVisibility(TextView.GONE);
//				String as[] = { DatabaseConstants.FROM_USER_FIELD};
//				int ai[] = new int[1];
//				ai[0] = R.id.chat_person_name;
//				adapter = new ChatHomeAdapter(fragmentactivity,R.layout.chat_history_item, cursor, as, ai, 0,ChatHome.this);
//				getListView().setAdapter(adapter);
//
//	    		if(ChatService.xmppConectionStatus){
//					xmppStatusView.setImageResource(R.drawable.blue_dot);
//				}else{
//					xmppStatusView.setImageResource(R.drawable.red_dot);
//					}	
//		 }else
//			noneMessageView.setVisibility(View.VISIBLE);
		
//		new Timer().schedule(new TimerTask() {
//			
//			@Override
//			public void run() {
//				notifyActivityCreatedHandler.sendEmptyMessage(0);
//			}
//		}, 10);
//		notifyActivityCreatedHandler.sendEmptyMessage(0);
		 recentList = getListView();
		 if(Build.VERSION.SDK_INT >= 11)
			 new YourAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		    else
		    	new YourAsyncTask().execute();
//			chatClient.startClient(this);
		super.onActivityCreated(bundle);
	}
	private class YourAsyncTask extends AsyncTask<String, Void, String> {
		
		protected void onPreExecute() {
//			dialog = ProgressDialog.show(getActivity(), "","Loading. Please wait...", true);
			progressBarView.setVisibility(ProgressBar.VISIBLE);
			 super.onPreExecute();
	     }
	     protected String doInBackground(String... args) {
	    	 ChatDBWrapper wraper = ChatDBWrapper.getInstance(SuperChatApplication.context);
	    	 if(SharedPrefManager.getInstance().isDomainAdmin()){
	    		 cursor = wraper.getBulletinList(BULLETIN_ADMIN);
	    		 if(cursor!=null && cursor.getCount() == 0){
	    			 saveMessage(bulletinDomainName, bulletinDomainName, getString(R.string.bulleting_welcome1) + SharedPrefManager.getInstance().getUserDomain() + "'s" + getString(R.string.bulleting_welcome2));
	    			 cursor = wraper.getBulletinList(BULLETIN_ADMIN);
	    		 }
	    	 }
	    	 else{
	    		 cursor = wraper.getBulletinList();
	    		 if(cursor!=null && cursor.getCount() == 0){
	    			 saveMessage(bulletinDomainName, bulletinDomainName, getString(R.string.bulleting_welcome1) + SharedPrefManager.getInstance().getUserDomain() + "'s" + getString(R.string.bulleting_welcome2));
	    			 cursor = wraper.getBulletinList(BULLETIN_MEMBER);
	    		 }
	    	 }
			 if(cursor!=null && cursor.getCount() > 0){
					String as[] = { DatabaseConstants.FROM_USER_FIELD};
					int ai[] = new int[1];
					ai[0] = R.id.chat_person_name;
					adapter = new BulletinScreenAdapter(fragmentactivity,R.layout.bulletin_broadcast_list_item, cursor, as, ai, 0, BulletinScreen.this);
//					getListView().setAdapter(adapter);
			 }
	        return null;
	     }

	     protected void onPostExecute(String result) {
	    	 if(cursor!=null && cursor.getCount() > 0){
				 	noneMessageView.setVisibility(TextView.GONE);
//					String as[] = { DatabaseConstants.FROM_USER_FIELD};
//					int ai[] = new int[1];
//					ai[0] = R.id.chat_person_name;
//					adapter = new ChatHomeAdapter(fragmentactivity,R.layout.chat_history_item, cursor, as, ai, 0,ChatHome.this);
				 	if(adapter!=null){
				 		adapter.loadDialog();
				 		if(recentList!=null)
				 			recentList.setAdapter(adapter);
					}
			 }
//	    	 else
//				noneMessageView.setVisibility(View.VISIBLE);
	    	 
	    	 if(ChatService.xmppConectionStatus){
					xmppStatusView.setImageResource(R.drawable.blue_dot);
				}else{
					xmppStatusView.setImageResource(R.drawable.red_dot);
					}
	    	 progressBarView.setVisibility(ProgressBar.GONE);
				super.onPostExecute(result);
	     }
	 }
	public void saveMessage(String displayName, String from, String msg) {
		try {
			ChatDBWrapper dbwrapper = ChatDBWrapper.getInstance();
			ContentValues contentvalues = new ContentValues();
			String myName = SharedPrefManager.getInstance().getUserName();
			contentvalues.put(DatabaseConstants.FROM_USER_FIELD, myName);
			contentvalues.put(DatabaseConstants.TO_USER_FIELD, from);
			contentvalues.put(DatabaseConstants.UNREAD_COUNT_FIELD, new Integer(1));
			contentvalues.put(DatabaseConstants.FROM_GROUP_USER_FIELD, "");
			contentvalues.put(DatabaseConstants.SEEN_FIELD, SeenState.sent.ordinal());
			contentvalues.put(DatabaseConstants.MESSAGE_TYPE, 3);

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
	private final Handler notifyActivityCreatedHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
			ChatDBWrapper wraper = ChatDBWrapper.getInstance(SuperChatApplication.context);
			if(SharedPrefManager.getInstance().isDomainAdmin())
				cursor = wraper.getBulletinList(BULLETIN_ADMIN);
			else
				cursor = wraper.getBulletinList(BULLETIN_MEMBER);
			 recentList = getListView();
			 
			 if(cursor!=null && cursor.getCount() > 0){
				 	noneMessageView.setVisibility(TextView.GONE);
					String as[] = { DatabaseConstants.FROM_USER_FIELD};
					int ai[] = new int[1];
					ai[0] = R.id.chat_person_name;
					adapter = new BulletinScreenAdapter(fragmentactivity,R.layout.chat_history_item, cursor, as, ai, 0, BulletinScreen.this);
					if(adapter!=null)
				 		adapter.loadDialog();
					getListView().setAdapter(adapter);

		    		if(ChatService.xmppConectionStatus){
						xmppStatusView.setImageResource(R.drawable.blue_dot);
					}else{
						xmppStatusView.setImageResource(R.drawable.red_dot);
						}
			 }
//			 else
//				noneMessageView.setVisibility(View.VISIBLE);
			 if (dialog != null)
					dialog.dismiss();
				dialog = null;
	    }};
	public void showHistoryChat(Cursor cursor) {
		if(cursor!=null && cursor.getCount()>0)
			noneMessageView.setVisibility(View.GONE);
//		else
//			noneMessageView.setVisibility(View.VISIBLE);
		
		String as[] = { DatabaseConstants.FROM_USER_FIELD};
		int ai[] = new int[1];
		ai[0] = R.id.chat_person_name;
		adapter = new BulletinScreenAdapter(fragmentactivity,R.layout.chat_history_item, cursor, as, ai, 0,this);
		if(adapter!=null)
	 		adapter.loadDialog();
		getListView().setAdapter(adapter);
		recentList = getListView();
		onNotifiUI();
//		updateCursor(null, null);
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		fragmentactivity = getActivity();
//		if(chatClient!=null)
//			chatClient.startClient(this);
		
	}
	public void onResume() {
		// ChatClient.getInstance().clearAllNotifications();
		getActivity().bindService(new Intent(getActivity(), ChatService.class), mConnection,Context.BIND_AUTO_CREATE);
		if (!onForeground) {
			onForeground = true;
			onNotifiUI();
		}
//		getShareInfo();
		onForeground = true;
		ChatService.setConnectionStatusListener(this);
//		if(service!=null)
//			service.setTypingListener(this);
		setProfileListener();
		if(superGroupName != null)
			superGroupName.setText(SharedPrefManager.getInstance().getUserDomain());
		if(superGroupIcon != null)
			setSGProfilePic(superGroupIcon, SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID"));
		super.onResume();
	}
	public void getShareInfo(){
		// Get intent, action and MIME type
		    Intent intent = getActivity().getIntent();
		    String action = intent.getAction();
		    String type = intent.getType();
		    
		
		    if (intent!=null && type != null && action != null && Intent.ACTION_SEND.equals(action)) {
//		    if (type.startsWith("image/")) {
//		    		calledForShare = true;
//		    		shareUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
//		            handleSendImage(intent); // Handle single image being sent
//		            intent.setAction(null);
//		        }
		    }
		   
	}
private void onNotifiUI(){
	new Timer().schedule(new TimerTask() {
		
		@Override
		public void run() {
			notifyChatRecieve("","");
		}
	}, 50);
}

	public void onPause() {
		onForeground = false;
		// ChatClient.getInstance().sendOffLineMessages();
		try {
			getActivity().unbindService(mConnection);
		} catch (Exception e) {
			// Just ignore that
			Log.d("MessageHistoryScreen", "Unable to un bind");
		}
		super.onPause();
	}
	@Override
	public void onDetach() {
		
//		if(chatClient!=null)
//		chatClient.stopClient();
		super.onDetach();
	}

//	@Override
//	public void notifyChatRecieve(String sender, String message) {
//				if (onForeground && context!=null){
//					((HomeScreen)context).runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							Cursor cursor1 = ChatDBWrapper.getInstance().getRecentChatList(null);
//							if(adapter==null)
//								showHistoryChat(cursor1);
//							else if(cursor1!=null){
//								adapter.swapCursor(cursor1);
//								adapter.notifyDataSetChanged();
//								if(cursor1.getCount()>0)
//									noneMessageView.setVisibility(View.GONE);
//								else
//									noneMessageView.setVisibility(View.VISIBLE);
//							}else
//								noneMessageView.setVisibility(View.VISIBLE);
//						}
//					});
//					}else{
//						int messageCount = SharedPrefManager.getInstance().getChatCounter();
////			                    ShortcutBadger.setBadge(getApplicationContext(), badgeCount);
//			                ShortcutBadger.with(EsiaChatApplication.context).count(messageCount);
//					}
//	}
	@Override
	public void notifyConnectionChange() {
		if(onForeground){
			notifyConnectionChangeHandler.sendEmptyMessage(0);
//			((HomeScreen)context).runOnUiThread(new Runnable() {
//				
//				@Override
//				public void run() {
//					if(ChatService.xmppConectionStatus){
//						xmppStatusView.setImageResource(R.drawable.blue_dot);
//					}else{
//						xmppStatusView.setImageResource(R.drawable.red_dot);
//						}
//				}
//				});
		}
		
		
	}
	private final Handler notifyConnectionChangeHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
	    	if(ChatService.xmppConectionStatus){
				xmppStatusView.setImageResource(R.drawable.blue_dot);
			}else{
				xmppStatusView.setImageResource(R.drawable.red_dot);
				}

	    }
	};
	@Override
	public void notifyChatRecieve(String sender, String message) {
		if (onForeground && recentList!=null)
			notifyChatRecieveHandler.sendEmptyMessage(0);
		try{
			((HomeScreen)getActivity()).notificationUI();
		}catch(Exception e){}
	}
	@Override
	public void notifyChatHome(String sender, String message) {
		// TODO Auto-generated method stub
		if (onForeground && recentList!=null)
			notifyChatRecieveHandler.sendEmptyMessage(0);
		try{
			((HomeScreen)getActivity()).notificationUI();
		}catch(Exception e){}
//				((HomeScreen)context).runOnUiThread(new Runnable() {
//	
//						@Override
//						public void run() {
//							try{
//								if(ChatService.xmppConectionStatus){
//									xmppStatusView.setImageResource(R.drawable.blue_dot);
//								}else{
//									xmppStatusView.setImageResource(R.drawable.red_dot);
//									}
//									Cursor cursor1 = ChatDBWrapper.getInstance().getRecentChatList(null);
//									
//									if (cursor1 == null || cursor1.getCount() <= 0){
//										noneMessageView.setVisibility(TextView.VISIBLE);
//				//						startChatButton.setVisibility(View.GONE);
//									}else{
//										noneMessageView.setVisibility(TextView.GONE);
//										if(adapter==null)
//											showHistoryChat(cursor1);
//										else{
//											adapter.swapCursor(cursor1);
//											recentList.setAdapter(adapter);
//											adapter.notifyDataSetChanged();
//										}	
//									}
//								
//							}catch(Exception e){
//								e.printStackTrace();
//							}
//						}
//				});
	
	
}
	private final Handler notifyChatRecieveHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
	    	try{
	    		if(ChatService.xmppConectionStatus){
					xmppStatusView.setImageResource(R.drawable.blue_dot);
				}else{
					xmppStatusView.setImageResource(R.drawable.red_dot);
					}
					Cursor cursor1 = null;
					if(SharedPrefManager.getInstance().isDomainAdmin())
						cursor1 = ChatDBWrapper.getInstance().getBulletinList(BULLETIN_ADMIN);
					else
						cursor1 = ChatDBWrapper.getInstance().getBulletinList(BULLETIN_MEMBER);
					
					if (cursor1 == null || cursor1.getCount() <= 0){
//						noneMessageView.setVisibility(TextView.VISIBLE);
//						startChatButton.setVisibility(View.GONE);
					}else{
						noneMessageView.setVisibility(TextView.GONE);
						if(adapter==null)
							showHistoryChat(cursor1);
						else{
							adapter.swapCursor(cursor1);
							if(adapter!=null)
						 		adapter.loadDialog();
//							recentList.setAdapter(adapter);
							adapter.notifyDataSetChanged();
						}	
					}
//					if(SharedPrefManager.getInstance().getChatCounter()>0){
//						((HomeScreen)getActivity()).totalCountView.setVisibility(View.VISIBLE);
//						((HomeScreen)getActivity()).totalCountView.setText(String.valueOf(SharedPrefManager.getInstance().getChatCounter()));
//					}else
//						((HomeScreen)getActivity()).totalCountView.setVisibility(View.GONE);
			}catch(Exception e){
				e.printStackTrace();
			}
	    }
	};
	
	private final Handler refreshHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
	    	if(onForeground && adapter!=null){
				Cursor cursor1 = null;
				if(SharedPrefManager.getInstance().isDomainAdmin())
					cursor1 = ChatDBWrapper.getInstance().getBulletinList(BULLETIN_ADMIN);
				else
					cursor1 = ChatDBWrapper.getInstance().getBulletinList(BULLETIN_MEMBER);
				if(cursor1!=null){
					adapter.swapCursor(cursor1);
					adapter.notifyDataSetChanged();
				}
			}
	    }};
	 
//	@Override
//	public void notifyTypingRecieve(String userName) {
//		if(onForeground && adapter!=null)
//			notifyChatRecieveHandler.sendEmptyMessage(0);
//	}
//	@Override
//	public void notifyRecordStatusRecieve(String userName) {
//		if(onForeground && adapter!=null)
//			notifyChatRecieveHandler.sendEmptyMessage(0);
//	}
//	@Override
//	public void notifyListeningStatusRecieve(String userName) {
//		if(onForeground && adapter!=null)
//			notifyChatRecieveHandler.sendEmptyMessage(0);
//		
//	}
//	@Override
//	public void refreshOnlineGroupUser() {
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public void refreshSubjectOfGroup() {
//		// TODO Auto-generated method stub
//		
//	}
	@Override
	public void notifyProfileUpdate(final String userName) {
		// TODO Auto-generated method stub
//		if (onForeground && recentList != null)
//			getActivity().runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				updateRow(userName);
//			}
//			});
			
		}
	private void updateRow(String userName, String userDisplayName){
		int row_count = recentList.getChildCount();
		for(int i = 0; i < row_count; i++){
			View view = recentList.getChildAt(i);
			RoundedImageView imgv = (RoundedImageView) view.findViewById(R.id.contact_icon);
			ImageView def_imgv = (ImageView) view.findViewById(R.id.contact_icon_default);
			if(def_imgv!=null && def_imgv.getTag()!=null && ((String)def_imgv.getTag()).equalsIgnoreCase(userName)){
				//Update the row.
				if(adapter != null)
					adapter.setProfilePic(imgv, def_imgv, "", userName);
				if(userDisplayName != null)
					((TextView) view.findViewById(R.id.id_chat_person)).setText(userDisplayName);
			}else{
				if(adapter != null)
					adapter.notifyDataSetChanged();
			}
		}
	}
	@Override
	public void notifyProfileUpdate(String userName, String status) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void notifyProfileUpdate(final String userName, final String status, final String userDisplayName) {
		// TODO Auto-generated method stub
		if (onForeground && recentList != null)
			getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				updateRow(userName, userDisplayName);
			}
			});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.id_sg_icon:
		case R.id.id_sg_name_label:
			Intent intent = new Intent(getActivity(), SuperGroupProfileActivity.class);
			startActivity(intent);
			break;
		}
	}
}


