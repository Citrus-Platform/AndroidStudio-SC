package com.superchat.ui;


import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
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
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sinch.android.rtc.SinchError;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.interfaces.interfaceInstances;
import com.superchat.model.BulletinGetMessageDataModel;
import com.superchat.model.ContactUpDatedModel;
import com.superchat.model.ContactUploadModel;
import com.superchat.model.ErrorModel;
import com.superchat.model.LoginModel;
import com.superchat.model.LoginResponseModel;
import com.superchat.model.LoginResponseModel.BroadcastGroupDetail;
import com.superchat.model.LoginResponseModel.GroupDetail;
import com.superchat.model.LoginResponseModel.UserResponseDetail;
import com.superchat.model.MarkSGActive;
import com.superchat.model.RegistrationFormResponse;
import com.superchat.retrofit.api.RetrofitRetrofitCallback;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.FileDownloadResponseHandler;
import com.superchat.utils.FileUploaderDownloader;
import com.superchat.utils.NetWork;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.MyriadRegularTextView;
import com.superchat.widgets.RoundedImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import retrofit2.Call;
import retrofit2.Response;

//import com.viewpagerindicator.TabPageIndicator;
//import com.viewpagerindicator.TitlePageIndicator;

public class HomeScreen extends AppCompatActivity implements ServiceConnection, SinchService.StartFailedListener, OnClickListener, OnMenuItemClickListener, interfaceInstances, FileDownloadResponseHandler ,FragmentDrawer.FragmentDrawerListener {

	private FragmentDrawer drawerFragment;
	public static RegistrationFormResponse multipleSGDAta;

	private static final String TAG = "HomeScreen";
	CustomViewPager mViewPager = null;
	private HomePagerAdapter mAdapter;
	private FragmentTransaction fragmentTransaction;
	ContactsScreen contactsFragment;
	PublicGroupScreen publicGroupFragment;
	ChatHome chatFragment;
	BulletinScreen bulletinFragment;
//	MoreScreen moreFragment;
//	XmppChatClient chatClient;
	static boolean isforeGround = false;
	public MyriadRegularTextView totalCountView;
	public MyriadRegularTextView unseenContactView;
	public MyriadRegularTextView totalBulletinView;
	ImageView publicGroupTab;
	ImageView chatMenuLayout;
	ImageView contactMenuLayout;
	ImageView bulletinMenuLayout;
	private SharedPrefManager iPrefManager = null;
	public static boolean calledForShare;
	public static String shareUri;
	public static int sharingType = 0;
	public static final int IMAGE_SHARING = 1;
	public static final int VOICE_SHARING = 2;
	public static final int VIDEO_SHARING = 3;
	public static final int PDF_SHARING = 4;
	public static final int DOC_SHARING = 5;
	public static final int XLS_SHARING = 6;
	public static final int PPT_SHARING = 7;
	static boolean isContactRefreshed;
	public static boolean isLaunched = false;
//	ImageView syncView;
	public static File cacheDirImage;
	public static String cacheDir = "";
	public static Map<String, String> cameraSettingMap = new HashMap<String,String>();
	private SinchService.SinchServiceInterface mSinchServiceInterface;
	Timer syncTimer = new Timer();
	ObjectAnimator syncAnimation;
	boolean isLoginProcessing;
	boolean isContactSync;
	boolean noLoadingNeeded = false;
	public static boolean firstTimeAdmin = false;

	public static boolean refreshContactList;

	boolean isRWA = false;

	public Set<GroupDetail> directoryGroupSet = null;
	public Set<BroadcastGroupDetail> directoryBroadcastGroupSet;

	public static HashMap<String, String> textDataRetain = new HashMap<String, String>();
	public static ArrayList<LoginResponseModel.GroupDetail> groupsData = new ArrayList<LoginResponseModel.GroupDetail>();
	public static ArrayList<LoginResponseModel.BroadcastGroupDetail> sharedIDData = new ArrayList<LoginResponseModel.BroadcastGroupDetail>();

	public static List<String> getAdminSetForSharedID(String shared_id){
		List<String> admin = new ArrayList<String>();
		try{
			for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				if(groups.broadcastGroupName.equalsIgnoreCase(shared_id))
					admin = groups.adminUserSet;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return admin;
	}
	public static void updateAdminSetForSharedID(String shared_id, List<String> members){
		try{
			String user = null;
			String member_for_add = null;
			boolean dont_add = false;
			for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				if(groups.broadcastGroupName.equalsIgnoreCase(shared_id)){
					groups.adminUserSet.addAll(members);
					for(int i = 0; i < members.size(); i++){
						member_for_add = members.get(i);
						for(int j = 0; j < groups.adminUserSet.size(); j++){
							user = groups.adminUserSet.get(j);
							if(user.contains(member_for_add))
								dont_add = true;
						}
						if(!dont_add)
							groups.adminUserSet.add(member_for_add);
					}
					HomeScreen.sharedIDData.remove(groups);
					HomeScreen.sharedIDData.add(groups);
					return;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void removeSharedID(String shared_id){
		try{
			for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				if(groups.broadcastGroupName.equalsIgnoreCase(shared_id)){
					HomeScreen.sharedIDData.remove(groups);
					return;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static String getSharedIDOwnerName(String shared_id){
		String owner = null;
		try{
			for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				if(groups.broadcastGroupName.equalsIgnoreCase(shared_id)){
					owner = groups.userName;
					return owner;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return owner;
	}
	public static String getSharedIDOwnerDisplayName(String shared_id){
		String owner = null;
		try{
			for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				if(groups.broadcastGroupName.equalsIgnoreCase(shared_id)){
					owner = groups.userDisplayName;
					return owner;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return owner;
	}
	public static void removeAdminFromSharedID(String shared_id, String members){
		try{
			String user = null;
			for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				if(groups.broadcastGroupName.equalsIgnoreCase(shared_id)){
					for(int i = 0; i < groups.adminUserSet.size(); i++){
						user = groups.adminUserSet.get(i);
						if(user.contains(members))
							groups.adminUserSet.remove(i);
					}
					HomeScreen.sharedIDData.remove(groups);
					HomeScreen.sharedIDData.add(groups);
					return;
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static boolean isAdminFromSharedID(String shared_id, String members){
		try{
			String user = null;
			for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				if(groups.broadcastGroupName.equalsIgnoreCase(shared_id)){
					for(int i = 0; i < groups.adminUserSet.size(); i++){
						user = groups.adminUserSet.get(i);
						if(user.contains(members)){
							return true;
						}
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	public static boolean isSharedIDOwner(String shared_id, String user){
		try{
			for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				if(groups.broadcastGroupName.equalsIgnoreCase(shared_id)){
					if(groups.userName.equals(user)){
						return true;
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	private void syncProcessStart(boolean start){
		if(syncAnimation==null)
			return;
		if(start){
			if(!syncAnimation.isRunning() && isLoginProcessing)
				syncAnimation.start();
		}else{
			if(syncAnimation.isRunning()){
				syncAnimation.cancel();
				syncAnimation = null;
				}
		}
	}
	private void chkSyncProcess(){
		if(syncTimer==null){
		syncTimer = new Timer();
		syncTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if(syncAnimation!=null)
				{
					if(!syncAnimation.isRunning()){
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								syncAnimation.start();
							}
						});

					}
//			}else if(syncAnimation!=null && syncAnimation.isRunning()){
//				runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
////						RefrshList();
//						syncAnimation.cancel();
//					}
//				});
//
//					cancel();
					}else
						cancel();

			}
		}, 500,2000);
		}
	}

	public void OnSyncClick(View view){
		if(syncAnimation==null){
		 	syncAnimation = ObjectAnimator.ofFloat(view, "rotation", 360);
			syncAnimation.setDuration(1000);
//			syncAnimation.setRepeatMode(ValueAnimator.REVERSE);
			syncAnimation.setRepeatCount(Animation.INFINITE);
		}
//			syncAnimation.cancel();

		if(SharedPrefManager.getInstance().isOpenDomain()){
			isContactSync = true;
			isLoginProcessing = true;
			syncProcessStart(true);
			isLoginProcessing = false;
			if(Build.VERSION.SDK_INT >= 11)
				new ContactMatchingLoadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new ContactMatchingLoadingTask().execute();
			return;
		}
//		if(AtMeApplication.contactSyncState == AtMeApplication.CONTACT_SYNC_IDLE || AtMeApplication.contactSyncState == AtMeApplication.CONTACT_SYNC_SUCESSED){
//			AtMeApplication.contactSyncState = AtMeApplication.CONTACT_SYNC_IDLE;
//			AtMeApplication.syncContactsWithServer(this);
//			chkSyncProcess();
//			if(syncAnimation!=null){

//				syncAnimation.start();
//				contactSyncMessage.setVisibility(View.VISIBLE);
//			}
//
//		}
		isContactSync = true;
		syncProcessStart(true);
//		if(!isLoginProcessing){
			isLoginProcessing = true;
			if(Build.VERSION.SDK_INT >= 11)
				new DomainsUserTaskOnServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new DomainsUserTaskOnServer().execute();
//			if(Build.VERSION.SDK_INT >= 11)
//				new SignInTaskOnServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//			else
//				new SignInTaskOnServer().execute();
//		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Check if user is not logged
		if(isServiceRunning("com.chat.sdk.ChatService")){
			System.out.println("[SERVICE RUNNING - SO STOPPING]");
			stopService(new Intent(this, ChatService.class));
		}
    	if(SharedPrefManager.getInstance().getUserName() == null){
    		startActivity(new Intent(this, RegistrationOptions.class));
    		finish();
    		return;
    	}
		if(SharedPrefManager.getInstance().getBackupSchedule() == -1)
			SharedPrefManager.getInstance().setBackupSchedule(2);
		String action = getIntent().getAction();
		if((action != null && Intent.ACTION_SEND.equals(action)) || SharedPrefManager.getInstance().isContactSynched())
			noLoadingNeeded = true;
		setContentView(R.layout.home_screen);
		Toolbar mToolbar;
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		drawerFragment = (FragmentDrawer)
				getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
		drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
		drawerFragment.setDrawerListener(this);

		totalCountView = (MyriadRegularTextView)findViewById(R.id.id_total_unseens);
		unseenContactView = (MyriadRegularTextView)findViewById(R.id.id_contact_unseens);
		totalBulletinView = (MyriadRegularTextView)findViewById(R.id.id_bulletin_unseens);

		contactMenuLayout = (ImageView)findViewById(R.id.id_contact);
		contactMenuLayout.setOnClickListener(this);
		chatMenuLayout = (ImageView)findViewById(R.id.id_chat);
		chatMenuLayout.setOnClickListener(this);
		bulletinMenuLayout = (ImageView)findViewById(R.id.id_more);
		bulletinMenuLayout.setOnClickListener(this);
		publicGroupTab = (ImageView)findViewById(R.id.id_channel);
		publicGroupTab.setOnClickListener(this);
		loadFragments();
		iPrefManager = SharedPrefManager.getInstance();
		if(Build.VERSION.SDK_INT >= 11)
			new SignInTaskOnServer(null).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			new SignInTaskOnServer(null).execute();

		//UserID, UserName, Display Name
//		if(SharedPrefManager.getInstance().getUserId() > 0)
//			Crashlytics.setUserIdentifier(""+SharedPrefManager.getInstance().getUserId());
//		if(SharedPrefManager.getInstance().getDisplayName() != null)
//			Crashlytics.setUserName(SharedPrefManager.getInstance().getDisplayName());
//		if(SharedPrefManager.getInstance().getUserEmail() != null)
//			Crashlytics.setUserEmail(SharedPrefManager.getInstance().getUserEmail());
//		//Set Mobile Model
//		if(ClientProperty.CLIENT_PARAMS != null)
//			Crashlytics.setString("mob_prop", "UN##"+SettingData.sSelf.getUserName()+"::"+ClientProperty.CLIENT_PARAMS);

//		Tracker t = ((SuperChatApplication) getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Home Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());

        mAdapter = new HomePagerAdapter(getSupportFragmentManager(),getApplicationContext());
        mViewPager = (CustomViewPager) findViewById(R.id.view_pager);
		mViewPager.setPagingEnabled(true);
		if(getIntent().getExtras() != null) {
			firstTimeAdmin = getIntent().getExtras().getBoolean("ADMIN_FIRST_TIME");
		}
//		mViewPager.setOffscreenPageLimit(4);
		mViewPager.setAdapter(mAdapter);
		if(iPrefManager.isFirstTime() && iPrefManager.getAppMode().equals("VirginMode")){
//			if(firstTimeAdmin){
//				mViewPager.setCurrentItem(2);
//				chatMenuLayout.setSelected(false);
//				publicGroupTab.setSelected(false);
//				contactMenuLayout.setSelected(true);
//				bulletinMenuLayout.setSelected(false);
//			}else
			{
				mViewPager.setCurrentItem(1);
				chatMenuLayout.setSelected(false);
				publicGroupTab.setSelected(true);
				contactMenuLayout.setSelected(false);
				bulletinMenuLayout.setSelected(false);
			}
		}else{
			mViewPager.setCurrentItem(0);
			chatMenuLayout.setSelected(true);
			publicGroupTab.setSelected(false);
			contactMenuLayout.setSelected(false);
			bulletinMenuLayout.setSelected(false);

		}
		startService(new Intent(SuperChatApplication.context, ChatService.class));

//		syncView.setVisibility(View.GONE);
//		TabPageIndicator titleIndicator = (TabPageIndicator)findViewById(R.id.titles);
//		 titleIndicator.setViewPager(mViewPager);
		 OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int position,
						float possitionOffset, int positionOffsetPixels) {
				}

				@Override
				public void onPageSelected(int position) {
					// actions
					switch(position) {
					case 0:
							contactMenuLayout.setSelected(false);
							bulletinMenuLayout.setSelected(false);
							publicGroupTab.setSelected(false);
//							syncView.setVisibility(View.GONE);

							chatMenuLayout.setSelected(true);
							break;
					case 2:
							chatMenuLayout.setSelected(false);
							bulletinMenuLayout.setSelected(false);
							publicGroupTab.setSelected(false);
//							syncView.setVisibility(View.INVISIBLE);

							if(contactsFragment!=null && contactsFragment.adapter!=null && isContactRefreshed){
								contactsFragment.showAllContacts();
								contactsFragment.setPorfileListener();
//								contactsFragment.adapter.notifyDataSetChanged();
								isContactRefreshed = false;
							}
							contactMenuLayout.setSelected(true);
							int contactsCount = DBWrapper.getInstance().getAllNumbersCount();
							iPrefManager.saveNewContactsCounter(contactsCount);
							notificationHandler.sendEmptyMessage(0);
						break;
					case 1:
						chatMenuLayout.setSelected(false);
						contactMenuLayout.setSelected(false);
						bulletinMenuLayout.setSelected(false);
//						syncView.setVisibility(View.GONE);

						publicGroupTab.setSelected(true);
						publicGroupFragment.refreshList();
						publicGroupFragment.restScreen();
						break;
					case 3:
							contactMenuLayout.setSelected(false);
							publicGroupTab.setSelected(false);
							chatMenuLayout.setSelected(false);
//							syncView.setVisibility(View.GONE);

							bulletinMenuLayout.setSelected(true);
						break;
					}
					publicGroupFragment.setPageNumber(position);
				}

				@Override
				public void onPageScrollStateChanged(int state) {
					// actions
				}
			};
		 mViewPager.addOnPageChangeListener(mPageChangeListener);
//		 titleIndicator.setOnPageChangeListener(mPageChangeListener);
		 startService(new Intent(SuperChatApplication.context, SinchService.class));
	}
	private boolean isVerifiedUser(String mobileNumber) {
		if (mobileNumber == null)
			return false;
		boolean isVerified = SharedPrefManager.getInstance().isMobileVerified(mobileNumber);
		return isVerified;
	}
	private ChatService messageService;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			messageService = ((ChatService.MyBinder) binder).getService();
			Log.d("Service", "Connected");
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			messageService = null;
		}};
	private String getCurrentAppVersion(){
		String version = null;
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			if(version!=null)
				version = version.trim();
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	public void notificationUI(){
		if(isforeGround){
			notificationHandler.sendEmptyMessage(0);
		}
	}
	Handler notificationHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(iPrefManager.getChatCounter()>0){
				totalCountView.setVisibility(View.VISIBLE);
				totalCountView.setText(String.valueOf(iPrefManager.getChatCounter()));
			}else
				totalCountView.setVisibility(View.GONE);

			if(iPrefManager.getBulletinChatCounter() > 0){
				totalBulletinView.setVisibility(View.VISIBLE);
				totalBulletinView.setText(String.valueOf(iPrefManager.getBulletinChatCounter()));
			}else
				totalBulletinView.setVisibility(View.GONE);

			int contactsCount = DBWrapper.getInstance().getAllNumbersCount();
			if((contactsCount-iPrefManager.getNewContactsCounter())>0){
				unseenContactView.setVisibility(View.VISIBLE);
				unseenContactView.setText(String.valueOf(contactsCount-iPrefManager.getNewContactsCounter()));
			}else
				unseenContactView.setVisibility(View.GONE);
			if(publicGroupFragment!=null)
				publicGroupFragment.refreshList();
			}
		};
	ProgressDialog progressDialog = null;
	Handler dialogHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;
				}
				break;
			case 1:
				progressDialog = ProgressDialog.show(HomeScreen.this, "",
						"Loading. Please wait...", true);
				break;
			}
		};
	};
	Handler mainTask = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			if(bundle !=null){
				String taskMsg = bundle.getString("TaskMessage", null);
				if(taskMsg!=null){
//					new BitmapDownloader().execute(taskMsg);
//					if (Build.VERSION.SDK_INT >= 11)
//						new BitmapDownloader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,taskMsg, BitmapDownloader.THUMB_REQUEST);
//		             else
//		            	 new BitmapDownloader().execute(taskMsg, BitmapDownloader.THUMB_REQUEST);
				}
			}
		}
	};

	@Override
	public void onFileDownloadResposne(View view, int type, byte[] data) {

	}

	@Override
	public void onFileDownloadResposne(View view, int[] type, String[] file_urls, String[] file_paths) {

	}
	public void updateUserData(String user){
		try {
			SharedPrefManager prefManager = SharedPrefManager.getInstance();
			long userID = prefManager.getSGUserID(user);
			//update shared preference
			System.out.println("[UserID - ] "+userID);
			System.out.println("[Pass - ] "+prefManager.getSGPassword(user));
			prefManager.saveUserId(userID);
			prefManager.saveUserName(user);
			prefManager.saveUserPassword(prefManager.getSGPassword(user));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void updateUserSGData(String sg_name){
		try {
			SharedPrefManager prefManager = SharedPrefManager.getInstance();
//			String sg_name = user.substring(user.indexOf("_") + 1);
			prefManager.saveUserDomain(sg_name);
			prefManager.saveCurrentSGDisplayName(sg_name);
			cleanDataAndSwitchSG(sg_name);
			//Set contact and group synchs
			prefManager.setContactSynched(false);
			prefManager.setGroupsLoaded(false);
			isContactSync = false;
			if(chatFragment != null)
				chatFragment.refreshList();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}


	public class SignInTaskOnServer extends AsyncTask<String, String, String> {
		LoginModel loginForm;
		ProgressDialog progressDialog = null;
		SharedPrefManager sharedPrefManager;
		String sg_name = null;
		public SignInTaskOnServer(String sg_name){
			 sharedPrefManager = SharedPrefManager.getInstance();
			 loginForm = new LoginModel();
			this.sg_name = sg_name;
			if(sg_name != null) {
				loginForm.setUserName(sharedPrefManager.getUserName());
				loginForm.setPassword(sharedPrefManager.getUserPassword());
				//update shared preference
				updateUserSGData(sg_name);
			}else{
				loginForm.setUserName(sharedPrefManager.getUserName());
				loginForm.setPassword(sharedPrefManager.getUserPassword());
			}
			if(sharedPrefManager.getDeviceToken() != null)
				loginForm.setToken(sharedPrefManager.getDeviceToken());
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
			loginForm.setClientVersion(clientVersion);
		}
		@Override
		protected void onPreExecute() {
			if(!noLoadingNeeded) {
				progressDialog = ProgressDialog.show(HomeScreen.this, "", "Loading. Please wait...", true);
				noLoadingNeeded = false;
			}
				if(isLoginProcessing)
					syncProcessStart(true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String JSONstring = new Gson().toJson(loginForm);
		    DefaultHttpClient client1 = new DefaultHttpClient();

			System.out.println("HomeScreen :: SignInTaskOnServer : URL : "+(Constants.SERVER_URL+ "/tiger/rest/user/login"));
			System.out.println("HomeScreen :: SignInTaskOnServer : serverUpdateCreateGroupInfo request: "+JSONstring);

			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/user/login");
			 HttpResponse response = null;
			 String str = "";
	         try {
	        	 httpPost = SuperChatApplication.addHeaderInfo(httpPost,false);

				httpPost.setEntity(new StringEntity(JSONstring));
				 try {
					 response = client1.execute(httpPost);
					 final int statusCode=response.getStatusLine().getStatusCode();
					 if (statusCode == HttpStatus.SC_OK){
						 HttpEntity entity = response.getEntity();
						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
						    String line = "";
				            while ((line = rd.readLine()) != null) {
				            	str+=line;
				            }
				            if(str!=null &&!str.equals("")){
								System.out.println("HomeScreen :: SignInTaskOnServer : response : "+str);
				            	Gson gson = new GsonBuilder().create();
				            	LoginResponseModel loginObj = gson.fromJson(str, LoginResponseModel.class);
								if (loginObj != null && loginObj.status!=null && loginObj.status.equals("success")) {
									if(isSwitchSG){
										//Important lines, this will remove all the groups of previous SG from Shared - Otherwise that will be joined.
										System.out.println("(Removing all groups from Shared Pref.)");
										sharedPrefManager.removeAllGroups();
									}
									if(loginObj.getDomainType()!=null && !loginObj.getDomainType().equals(""))
										sharedPrefManager.setDomainType(loginObj.getDomainType());
									if(loginObj.domainPrivacyType!=null && loginObj.domainPrivacyType.equals("open"))
										sharedPrefManager.setDomainAsPublic(true);
									else
										sharedPrefManager.setDomainAsPublic(false);
									if(loginObj.type!=null && loginObj.type.equals("domainAdmin")){
										sharedPrefManager.setAsDomainAdmin(true);
										if(loginObj.joinedUserCount!=null && !loginObj.joinedUserCount.equals(""))
										sharedPrefManager.saveDomainJoinedCount(loginObj.joinedUserCount);
										if(loginObj.unJoinedUserCount!=null && !loginObj.unJoinedUserCount.equals(""))
											sharedPrefManager.saveDomainUnjoinedCount(loginObj.unJoinedUserCount);
									}else if(loginObj.type!=null && loginObj.type.equals("domainSubAdmin")){
										sharedPrefManager.setAsDomainSubAdmin(true);
										if(loginObj.joinedUserCount!=null && !loginObj.joinedUserCount.equals(""))
											sharedPrefManager.saveDomainJoinedCount(loginObj.joinedUserCount);
										if(loginObj.unJoinedUserCount!=null && !loginObj.unJoinedUserCount.equals(""))
											sharedPrefManager.saveDomainUnjoinedCount(loginObj.unJoinedUserCount);
									} else
										sharedPrefManager.setAsDomainAdmin(false);

									if(loginObj.directoryUserSet != null){
										System.out.println("HomeScreen :: SignInTaskOnServer : Writing in TABLE_NAME_CONTACT_NUMBERS.");
										for (UserResponseDetail userDetail : loginObj.directoryUserSet) {
											String number = DBWrapper.getInstance().getContactNumber(userDetail.userName);
											if(number!=null && !number.equals(""))
												continue;
	//										UserResponseDetail userDetail = loginObj.directoryUserSet.get(st);
										ContentValues contentvalues = new ContentValues();
										contentvalues.put(DatabaseConstants.USER_NAME_FIELD,userDetail.userName);
										contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(1));
										contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,userDetail.mobileNumber);
										int id = userDetail.userName.hashCode();
										if (id < -1)
											id = -(id);
										contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
										contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
										contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, userDetail.name);
										contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,Integer.valueOf(0));

										contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
										contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
										contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
										contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, userDetail.mobileNumber);
										DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);

										if(userDetail.currentStatus!=null)
											sharedPrefManager.saveUserStatusMessage(userDetail.userName, userDetail.currentStatus);
										if(userDetail.department!=null)
											sharedPrefManager.saveUserDepartment(userDetail.userName, userDetail.department);
										if(userDetail.designation!=null)
											sharedPrefManager.saveUserDesignation(userDetail.userName, userDetail.designation);
										if(userDetail.gender!=null){
											sharedPrefManager.saveUserGender(userDetail.userName, userDetail.gender);
//											Log.i(TAG, "userName : "+userDetail.userName+", gender : "+userDetail.gender);
										}
										if(userDetail.imageFileId!=null){
											sharedPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
											if(userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
												Message msg = new Message();
												Bundle data = new Bundle();
												data.putString("TaskMessage", userDetail.imageFileId);
												msg.setData(data);
												mainTask.sendMessage(msg);
	//											new BitmapDownloader().execute(userDetail.imageFileId);
											}
										}
									}
								}
									directoryGroupSet = loginObj.directoryGroupSet;
									if(directoryGroupSet != null) {
										System.out.println("HomeScreen :: SignInTaskOnServer : Writing Groups..");
									}
									for (GroupDetail groupDetail : loginObj.directoryGroupSet) {
//										Log.d(TAG, "counter check  Login response : "+groupDetail.type+""+groupDetail.displayName+" , "+groupDetail.numberOfMembers);
//										writeLogsToFile(groupDetail.groupName+" - "+groupDetail.displayName);
										sharedPrefManager.saveGroupInfo(groupDetail.groupName,SharedPrefManager.GROUP_ACTIVE_INFO,true);
										sharedPrefManager.saveGroupName(groupDetail.groupName, groupDetail.displayName);
										sharedPrefManager.saveGroupDisplayName(groupDetail.groupName, groupDetail.displayName);
										if(groupDetail.type!=null && groupDetail.type.equalsIgnoreCase("public"))
											sharedPrefManager.saveGroupTypeAsPublic(groupDetail.groupName,true);
										if(groupDetail.activatedUserSet!=null)
//										for (String activatedUser : groupDetail.activatedUserSet) {
//											boolean isNewAdded =  sharedPrefManager.saveUsersOfGroup(groupDetail.groupName, activatedUser);
//											if(isNewAdded)
//												sharedPrefManager.saveUserGroupInfo(groupDetail.groupName,activatedUser,SharedPrefManager.GROUP_ACTIVE_INFO,true);
//										}
										if(groupDetail.userName!=null && !groupDetail.userName.equals("")){
											sharedPrefManager.saveUserGroupInfo(groupDetail.groupName, groupDetail.userName, SharedPrefManager.GROUP_OWNER_INFO, true);
											sharedPrefManager.saveUserGroupInfo(groupDetail.groupName,groupDetail.userName,SharedPrefManager.GROUP_ACTIVE_INFO,true);
											sharedPrefManager.saveUserGroupInfo(groupDetail.groupName,sharedPrefManager.getUserName(),SharedPrefManager.GROUP_ACTIVE_INFO,true);
											sharedPrefManager.saveUserGroupInfo(groupDetail.groupName,groupDetail.userName,SharedPrefManager.PUBLIC_CHANNEL,true);
											sharedPrefManager.saveGroupOwnerName(groupDetail.groupName, groupDetail.userName);
										}
										if(groupDetail.description!=null)
											sharedPrefManager.saveUserStatusMessage(groupDetail.groupName, groupDetail.description);
										if(groupDetail.numberOfMembers!=null)
											sharedPrefManager.saveGroupMemberCount(groupDetail.groupName, groupDetail.numberOfMembers);
//										boolean isFirstChat = ChatDBWrapper.getInstance(SuperChatApplication.context).isFirstChat(groupDetail.groupName);
										//Update Time if Single Message
										ChatDBWrapper.getInstance(SuperChatApplication.context).updateTimeIfSingleMessageOnly(groupDetail.groupName);
//											saveMessage(groupDetail.displayName, groupDetail.groupName,"Group created by "+groupDetail.userDisplayName);//saveMessage(groupDetail.displayName, groupDetail.groupName,"You are welcome.");
										String oldFileId = sharedPrefManager.getUserFileId(groupDetail.fileId);
										sharedPrefManager.saveUserFileId(groupDetail.groupName, groupDetail.fileId);
									}
									directoryBroadcastGroupSet = loginObj.directoryBroadcastGroupSet;
									if(directoryBroadcastGroupSet != null) {
										System.out.println("HomeScreen :: SignInTaskOnServer : Writing Broadcast..");
									}
									for (BroadcastGroupDetail broadcastGroupDetail : loginObj.directoryBroadcastGroupSet) {
										sharedPrefManager.saveBroadCastName(broadcastGroupDetail.broadcastGroupName, broadcastGroupDetail.displayName);
										sharedPrefManager.saveBroadCastDisplayName(broadcastGroupDetail.broadcastGroupName, broadcastGroupDetail.displayName);
										if(broadcastGroupDetail.activatedUserSet!=null)
										for (String activatedUser : broadcastGroupDetail.activatedUserSet) {
											boolean isNewAdded =  sharedPrefManager.saveUsersOfBroadCast(broadcastGroupDetail.broadcastGroupName, activatedUser);
											if(isNewAdded)
												sharedPrefManager.saveUseBroadCastInfo(broadcastGroupDetail.broadcastGroupName,activatedUser,SharedPrefManager.BROADCAST_ACTIVE_INFO,true);
										}
										if(broadcastGroupDetail.userName!=null && !broadcastGroupDetail.userName.equals("")){
											sharedPrefManager.saveUseBroadCastInfo(broadcastGroupDetail.broadcastGroupName, broadcastGroupDetail.userName, SharedPrefManager.BROADCAST_OWNER_INFO, true);
											sharedPrefManager.saveUseBroadCastInfo(broadcastGroupDetail.broadcastGroupName,broadcastGroupDetail.userName,SharedPrefManager.BROADCAST_ACTIVE_INFO,true);
										}
										if(broadcastGroupDetail.description!=null)
											sharedPrefManager.saveUserStatusMessage(broadcastGroupDetail.broadcastGroupName, broadcastGroupDetail.description);
//										boolean isFirstChat = ChatDBWrapper.getInstance(SuperChatApplication.context).isFirstChat(broadcastGroupDetail.broadcastGroupName);
//										if(isFirstChat)
//											saveMessage(broadcastGroupDetail.displayName, broadcastGroupDetail.broadcastGroupName,"Broadcast created by "+broadcastGroupDetail.userDisplayName);//saveMessage(broadcastGroupDetail.displayName, broadcastGroupDetail.broadcastGroupName,"You are welcome.");
//										ChatDBWrapper.getInstance(SuperChatApplication.context).updateTimeIfSingleMessageOnly(broadcastGroupDetail.broadcastGroupName);
										String oldFileId = sharedPrefManager.getUserFileId(broadcastGroupDetail.fileId);
										if(broadcastGroupDetail.fileId!=null && !broadcastGroupDetail.fileId.equals("") && (oldFileId == null || !oldFileId.equals(broadcastGroupDetail.fileId)))
										{
											Message msg = new Message();
											Bundle data = new Bundle();
											data.putString("TaskMessage",broadcastGroupDetail.fileId);
											msg.setData(data);
											mainTask.sendMessage(msg);
										}
										sharedPrefManager.saveUserFileId(broadcastGroupDetail.broadcastGroupName, broadcastGroupDetail.fileId);
									}
									if(loginObj.loggedInDirectoryUser!=null){
										if(loginObj.loggedInDirectoryUser.name!=null)
											sharedPrefManager.saveDisplayName(loginObj.loggedInDirectoryUser.name);
										if(loginObj.loggedInDirectoryUser.currentStatus!=null)
											sharedPrefManager.saveUserStatusMessage(loginObj.loggedInDirectoryUser.userName, loginObj.loggedInDirectoryUser.currentStatus);
										if(loginObj.loggedInDirectoryUser.department!=null)
											sharedPrefManager.saveUserDepartment(loginObj.loggedInDirectoryUser.userName, loginObj.loggedInDirectoryUser.department);
										if(loginObj.loggedInDirectoryUser.designation!=null)
											sharedPrefManager.saveUserDesignation(loginObj.loggedInDirectoryUser.userName, loginObj.loggedInDirectoryUser.designation);
										if(loginObj.loggedInDirectoryUser.imageFileId!=null){
											sharedPrefManager.saveUserFileId(loginObj.loggedInDirectoryUser.userName, loginObj.loggedInDirectoryUser.imageFileId);
											if(loginObj.loggedInDirectoryUser.imageFileId!=null && !loginObj.loggedInDirectoryUser.imageFileId.equals("")){
//													new BitmapDownloader().execute(loginObj.loggedInDirectoryUser.imageFileId);
												Message msg = new Message();
												Bundle data = new Bundle();
												data.putString("TaskMessage",loginObj.loggedInDirectoryUser.imageFileId);
												msg.setData(data);
												mainTask.sendMessage(msg);
												}
										}
									}
					            }
				            }
					 }
				} catch (ClientProtocolException e) {
					Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				}

			} catch (UnsupportedEncodingException e1) {
				Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:"+e1.toString());
			}catch(Exception e){
				Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
				e.printStackTrace();
			}
			return str;
		}
		@Override
		protected void onPostExecute(String str) {
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			boolean new_user = false;
			JSONObject finalJSONbject = new JSONObject();
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
							//below code should be only, in case of brand new user - "First time SC user"
							iPrefManager.setAppMode("SecondMode");
//							iPrefManager.saveUserPhone(regObj.iMobileNumber);
	//						iPrefManager.saveUserPassword(regObj.getPassword());
							iPrefManager.saveUserLogedOut(false);
							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
							showDialog(citrusError.message, null);
						}else if(citrusError!=null && citrusError.code.equals("20020") ){
							showDialog(citrusError.message, citrusError.code);
						}else if(citrusError!=null && citrusError.code.equals("20031") ){
							showDialog(citrusError.message, citrusError.code);
//							DBWrapper.getInstance().updateSGCredentials(regObj.getDomainName(), regObj.getUsername(), regObj.getPassword(), regObj.getUserId(), regObj.isActivateSuccess());
						}else
							showDialog(citrusError.message, null);
					} else if (errorModel.message != null){
						}
				} else
					showDialog("Please try again later.", null);
			}else{
//				if (str!=null && str.contains("success"))
				if(isSwitchSG && messageService != null) {
					messageService.chatLogout();
					messageService.chatLogin();
				}
				if(iPrefManager.isContactSynched() && iPrefManager.isGroupsLoaded()){
					//Get all the shared ID's - This call is for everyone
					String shared_id_data = sharedPrefManager.getSharedIDData();
					if(shared_id_data != null && shared_id_data.length() > 0)
						parseSharedIDData(shared_id_data);
					if(Build.VERSION.SDK_INT >= 11)
						new GetSharedIDListFromServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					else
						new GetSharedIDListFromServer().execute();
					return;
				}
				if(mViewPager!=null && mAdapter!=null && isforeGround){
					mViewPager.setAdapter(mAdapter);
					if(iPrefManager.isFirstTime() && iPrefManager.getAppMode().equals("VirginMode")){
//						if(isContactSync || firstTimeAdmin){
						if(isContactSync){
							mViewPager.setCurrentItem(2);
//							firstTimeAdmin = false;
						}else
							mViewPager.setCurrentItem(1);
						//Update information to Other domain members for the update.
						//Create JSON here for group update info.
						String number = sharedPrefManager.getUserPhone();
						if(number.contains("-"))
							number = number.substring(number.indexOf('-') + 1);
						try {
							finalJSONbject.put("userName", sharedPrefManager.getUserName());
							finalJSONbject.put("displayname", sharedPrefManager.getDisplayName());
							finalJSONbject.put("contryCode", "+"+sharedPrefManager.getUserCountryCode());
							finalJSONbject.put("mobilenumber", number);
							if(sharedPrefManager.getUserFileId(sharedPrefManager.getUserName()) != null)
								finalJSONbject.put("fileId", sharedPrefManager.getUserFileId(sharedPrefManager.getUserName()));
							if(sharedPrefManager.getUserStatusMessage(sharedPrefManager.getUserName()) != null
									&& sharedPrefManager.getUserStatusMessage(sharedPrefManager.getUserName()).trim().length() > 0)
								finalJSONbject.put("statusMessage", sharedPrefManager.getUserStatusMessage(sharedPrefManager.getUserName()));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						new_user = true;
					}else{
						if(isContactSync){
							mViewPager.setCurrentItem(2);
						}else
							mViewPager.setCurrentItem(0);
					}

					if(!isContactSync && !iPrefManager.isGroupsLoaded()){
						if(Build.VERSION.SDK_INT >= 11)
							new OpenGroupTaskOnServer(!iPrefManager.isFirstTime()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						else
							new OpenGroupTaskOnServer(!iPrefManager.isFirstTime()).execute();
					}
					if(!iPrefManager.isContactSynched()){
						if(sharedPrefManager.isOpenDomain()){
							if(Build.VERSION.SDK_INT >= 11)
								new ContactMatchingLoadingTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							else
								new ContactMatchingLoadingTask().execute();


						}else{
							if(Build.VERSION.SDK_INT >= 11)
								new DomainsUserTaskOnServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							else
								new DomainsUserTaskOnServer().execute();
						}
						//call Once
						getBulletinMessages();
					}
				}
			}
			if(isSwitchSG){
				isSwitchSG = false;
				if(chatFragment != null)
					chatFragment.notifyChatRecieve("", "");
//				loadFragments();
//				chatFragment.refreshList();
			}
			//Get all the shared ID's - This call is for everyone
			String shared_id_data = sharedPrefManager.getSharedIDData();
			if(shared_id_data != null && shared_id_data.length() > 0)
				parseSharedIDData(shared_id_data);
			if(Build.VERSION.SDK_INT >= 11)
				new GetSharedIDListFromServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new GetSharedIDListFromServer().execute();
			if(sharedPrefManager.isBackupCheckedForSG(sharedPrefManager.getUserDomain())) {
				addNewGroupsAndBroadcastsToDB();
			}else {
				if (Build.VERSION.SDK_INT >= 11)
					new CheckDataBackup().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				else
					new CheckDataBackup().execute();
			}

			if(new_user && messageService != null){
				String json = finalJSONbject.toString();
				Log.i(TAG, "Final JSON :  " + json);
//				json = json.replace("\"", "&quot;");
				try {
					System.out.println("[Waiting 3 secs...]");
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				messageService.sendSpecialMessageToAllDomainMembers(sharedPrefManager.getUserDomain() + "-system", json, XMPPMessageType.atMeXmppMessageTypeUserRegistered);
				json = null;
				new_user = false;
			}
			syncProcessStart(false);
			isLoginProcessing = false;
			super.onPostExecute(str);
		}
	}
	class ContactMatchingLoadingTask  extends AsyncTask<String,String,String>{
		ProgressDialog progressDialog;
		List<String> numbers = new ArrayList<String>();
		@Override
		protected void onPreExecute() {
			if(!firstTimeAdmin)
				progressDialog = ProgressDialog.show(HomeScreen.this, "", "Contact loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
			String THUMBNAIL_URI = ContactsContract.Contacts.PHOTO_THUMBNAIL_URI;
			Uri PHONECONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
			Cursor cursor = HomeScreen.this.getContentResolver().query(PHONECONTENT_URI, null,
					null, null, null);
			if (cursor == null) {
				return null;
			}
			numbers = DBWrapper.getInstance().getAllNumbers();
			List<String> numbers = new ArrayList<String>();
			while (cursor.moveToNext()) {
				String displayNumber = cursor.getString(cursor.getColumnIndex(NUMBER));
				String displayName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
				String thumbUri = cursor.getString(cursor.getColumnIndex(THUMBNAIL_URI));
				if(displayNumber!=null && displayNumber.contains(" "))
					displayNumber = displayNumber.replace(" ", "");
				String number = formatNumber(displayNumber);
				if(numbers.contains(number))
					continue;
//				AppContact appContact = new AppContact();
//				appContact.setId(thumbUri);
//				appContact.setName(displayName);
//				appContact.setNumber(number);
//				appContact.setDisplayNumber(displayNumber);
//				allContacts.put(displayNumber,appContact);
				numbers.add(number);
			}
			serverUpdateContactsInfo(numbers);
			return null;
		}
		@Override
		protected void onPostExecute(String str) {

			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			if(iPrefManager.isFirstTime() || firstTimeAdmin || isContactSync){
				if(isContactSync)
					syncProcessStart(false);
				iPrefManager.setFirstTime(false);
				isContactSync =false;
				firstTimeAdmin = false;
				iPrefManager.setContactSynched(true);
				if(contactsFragment!=null)
					contactsFragment.showAllContacts();
				int contactsCount = DBWrapper.getInstance().getAllNumbersCount();
				if(iPrefManager.getNewContactsCounter()>=0 && (contactsCount-iPrefManager.getNewContactsCounter())>0 && iPrefManager.getChatCounter()>0){
					unseenContactView.setVisibility(View.VISIBLE);
					unseenContactView.setText(String.valueOf(iPrefManager.getChatCounter()));
				}else
					unseenContactView.setVisibility(View.GONE);
			}
//			if(SharedPrefManager.getInstance().isDomainAdmin()){
//				if(Build.VERSION.SDK_INT >= 11)
//					new DomainsUserTaskOnServer().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//				else
//					new DomainsUserTaskOnServer().execute();
//			}
		}
	}
	public class DomainsUserTaskOnServer extends AsyncTask<String, String, String> {
		LoginModel loginForm;
		ProgressDialog progressDialog = null;
		SharedPrefManager sharedPrefManager;
		public DomainsUserTaskOnServer(){
			sharedPrefManager = SharedPrefManager.getInstance();
			loginForm = new LoginModel();
			loginForm.setUserName(sharedPrefManager.getUserName());
			loginForm.setPassword(sharedPrefManager.getUserPassword());
			if(sharedPrefManager.getDeviceToken() != null)
				loginForm.setToken(sharedPrefManager.getDeviceToken());
		}
		@Override
		protected void onPreExecute() {
//			if(iPrefManager.isFirstTime()|| isContactSync)
//				progressDialog = ProgressDialog.show(HomeScreen.this, "", "Contact refreshing. Please wait...", true);
			if(isContactSync)
				syncProcessStart(true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		    DefaultHttpClient client1 = new DefaultHttpClient();
			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/user/directory?domainName="+sharedPrefManager.getUserDomain()+"&pg=1&limit=1000");
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
			 HttpResponse response = null;

	         try {
				 try {
					 response = client1.execute(httpPost);
					 final int statusCode=response.getStatusLine().getStatusCode();
					 if (statusCode == HttpStatus.SC_OK){ //new1
						 HttpEntity entity = response.getEntity();
						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
						    String line = "";
				            String str = "";
				            while ((line = rd.readLine()) != null) {

				            	str+=line;
				            }
				            if(str!=null &&!str.equals("")){
				            	Gson gson = new GsonBuilder().create();
				            	LoginResponseModel loginObj = gson.fromJson(str,LoginResponseModel.class);
								if (loginObj != null) {
									DBWrapper wrapper = DBWrapper.getInstance();
									if(loginObj.directoryUserSet!=null){
									for (UserResponseDetail userDetail : loginObj.directoryUserSet) {
										if(userDetail.userState!=null){
											if(userDetail.userState.equals("inactive"))
												sharedPrefManager.saveUserExistence(userDetail.userName,false);
				                        	 else if(userDetail.userState.equals("active"))
				                        		 sharedPrefManager.saveUserExistence(userDetail.userName,true);
										}
										String number = wrapper.getContactNumber(userDetail.userName);
										if(number != null && !number.equals("")){
											if(isRWA)
												wrapper.updateUserDetails(number, userDetail);
											else
												wrapper.updateAtMeContactStatus(number);
											firstTimeAdmin = true;
											continue;
											}

							        //Alter Table for the values.
//									wrapper.alterTable(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, new String[]{DatabaseConstants.FLAT_NUMBER, DatabaseConstants.BUILDING_NUMBER,
//											DatabaseConstants.ADDRESS, DatabaseConstants.RESIDENCE_TYPE});
									ContentValues contentvalues = new ContentValues();
									contentvalues.put(DatabaseConstants.USER_NAME_FIELD,userDetail.userName);
									contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(1));
									contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,userDetail.mobileNumber);
									int id = userDetail.userName.hashCode();
									if (id < -1)
										id = -(id);
									contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
									contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
									contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, userDetail.name);
                                        if(userDetail.type != null && userDetail.type.equalsIgnoreCase("domainSubAdmin"))
                                            sharedPrefManager.setUserSGSubAdmin(userDetail.userName, true);
									contentvalues.put(DatabaseConstants.CONTACT_TYPE_FIELD, userDetail.type);
									contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,Integer.valueOf(0));
									contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
									contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
									contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
									//Add Address Details
									contentvalues.put(DatabaseConstants.FLAT_NUMBER, userDetail.flatNumber);
									contentvalues.put(DatabaseConstants.BUILDING_NUMBER, userDetail.buildingNumber);
									contentvalues.put(DatabaseConstants.ADDRESS, userDetail.address);
									contentvalues.put(DatabaseConstants.RESIDENCE_TYPE, userDetail.residenceType);

									contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, userDetail.mobileNumber);
									if(!userDetail.userName.equalsIgnoreCase(sharedPrefManager.getUserName()))
										wrapper.insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
									if(userDetail.userName.equalsIgnoreCase(sharedPrefManager.getUserName()))
										sharedPrefManager.saveDisplayName(userDetail.name);
									sharedPrefManager.saveUserServerName(userDetail.userName, userDetail.name);
//									UserResponseDetail.PrivacyStatusMap privacyStatusMap= new UserResponseDetail.PrivacyStatusMap();
//									userDetail.setPrivacyStatusMap(privacyStatusMap);
									UserResponseDetail.PrivacyStatusMap privacyStatusMap = userDetail.getPrivacyStatusMap();
									if(privacyStatusMap!=null){
										if(privacyStatusMap.dnc == 1)
											sharedPrefManager.saveStatusDNC(userDetail.userName, true);
										else
											sharedPrefManager.saveStatusDNC(userDetail.userName, false);
										if(privacyStatusMap.dnm == 1)
											sharedPrefManager.saveStatusDNM(userDetail.userName, true);
										else
											sharedPrefManager.saveStatusDNM(userDetail.userName, false);
									}
									if(userDetail.currentStatus!=null)
										sharedPrefManager.saveUserStatusMessage(userDetail.userName, userDetail.currentStatus);
									if(userDetail.department!=null)
										sharedPrefManager.saveUserDepartment(userDetail.userName, userDetail.department);
									if(userDetail.designation!=null)
										sharedPrefManager.saveUserDesignation(userDetail.userName, userDetail.designation);
									if(userDetail.gender!=null){
										sharedPrefManager.saveUserGender(userDetail.userName, userDetail.gender);
//										Log.i(TAG, "userName : "+userDetail.userName+", gender : "+userDetail.gender);
									}
									if(userDetail.imageFileId!=null){
										sharedPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
										if(userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
											Message msg = new Message();
											Bundle data = new Bundle();
											data.putString("TaskMessage",userDetail.imageFileId);
											msg.setData(data);
											mainTask.sendMessage(msg);
										}
									}
								}
							}
					     }
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
			if(iPrefManager.isFirstTime()){
				startService(new Intent(SuperChatApplication.context, ChatService.class));
			}
			iPrefManager.setFirstTime(false);
			syncProcessStart(false);
			isLoginProcessing = false;
			notificationHandler.sendEmptyMessage(0);
			if (str!=null && str.contains("error")){
				Gson gson = new GsonBuilder().create();
				ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
				if (errorModel != null) {
					if (errorModel.citrusErrors != null
							&& !errorModel.citrusErrors.isEmpty()) {
						ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
						if(citrusError!=null && citrusError.code.equals("20019")){
							SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//							iPrefManager.saveUserDomain(domainNameView.getText().toString());
							iPrefManager.saveUserId(errorModel.userId);
							//below code should be only, in case of brand new user - "First time SC user"
							iPrefManager.setAppMode("SecondMode");
//							iPrefManager.saveUserPhone(regObj.iMobileNumber);
	//						iPrefManager.saveUserPassword(regObj.getPassword());
							iPrefManager.saveUserLogedOut(false);
							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
							showDialog(citrusError.message, null);
						}else if(citrusError!=null && citrusError.code.equals("20020") ){
							showDialog(citrusError.message, citrusError.code);
						}else
							showDialog(citrusError.message, null);
					} else if (errorModel.message != null)
						showDialog(errorModel.message, null);
				} else
					showDialog("Please try again later.", null);
			}else{
				iPrefManager.setContactSynched(true);
				if(mViewPager != null && mAdapter != null && isforeGround){
					if(firstTimeAdmin){
						firstTimeAdmin = false;
						contactsFragment.showAllContacts();
					}
//					mAdapter.notifyDataSetChanged();
//					mViewPager.setAdapter(mAdapter);
//					mViewPager.setCurrentItem(0);
					isContactRefreshed = true;
					isContactSync = false;
					if(EsiaChatContactsScreen.invitationPending){
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(EsiaChatContactsScreen.inviteService!=null){
							StringBuffer members = new StringBuffer();
							for(String addedUser : EsiaChatContactsScreen.inviteUsersList){
								if(members.toString().length() > 0)
									members.append(',');
								members.append(addedUser);
							}
							//Create Json here for group update info.
							 JSONObject finalJSONbject = new JSONObject();
							 try {
								finalJSONbject.put("displayName", EsiaChatContactsScreen.inviteDisplayName);
//								if(groupDiscription != null)
//									finalJSONbject.put("description", groupDiscription);
								if(iPrefManager.getUserFileId(EsiaChatContactsScreen.inviteRoomName) != null)
									finalJSONbject.put("fileId", iPrefManager.getUserFileId(EsiaChatContactsScreen.inviteRoomName));
								if(members.toString().length() > 0)
								{
									finalJSONbject.put("Members", members.toString());
								}
							 }catch (JSONException e) {
									// TODO Auto-generated catch block
//									e.printStackTrace();
								}
							 String json = finalJSONbject.toString();
//							 json = json.replace("\"", "&quot;");
							for(String addedUser : EsiaChatContactsScreen.inviteUsersList)
								EsiaChatContactsScreen.inviteService.inviteUserInRoom(EsiaChatContactsScreen.inviteRoomName, EsiaChatContactsScreen.inviteDisplayName, "", addedUser, json);
							json = null;
						}
						EsiaChatContactsScreen.invitationPending = false;
						EsiaChatContactsScreen.inviteRoomName = null;
						EsiaChatContactsScreen.inviteDisplayName = null;
						EsiaChatContactsScreen.inviteUsersList = null;
						EsiaChatContactsScreen.inviteService = null;
					}
				}
				int contactsCount = DBWrapper.getInstance().getAllNumbersCount();
				if(iPrefManager.getNewContactsCounter()>=0 && (contactsCount-iPrefManager.getNewContactsCounter())>0){
					if(iPrefManager.getChatCounter()>0){
						unseenContactView.setVisibility(View.VISIBLE);
						unseenContactView.setText(String.valueOf(iPrefManager.getChatCounter()));
					}else
						unseenContactView.setVisibility(View.GONE);
				}else
					unseenContactView.setVisibility(View.GONE);
			}
			super.onPostExecute(str);
		}
	}
	public void showDialogWithPositive(String s) {
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
	public void showDialog(String s, final String error_code) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(error_code != null && error_code.equals("20020") ){
					SharedPrefManager prefManager = SharedPrefManager.getInstance();
					String mobileNumber = prefManager.getUserPhone();
					if(mobileNumber!=null && !mobileNumber.equals("")){
						prefManager.clearSharedPref();
						ChatDBWrapper.getInstance().clearMessageDB();
						DBWrapper.getInstance().clearAllDB();
					}
					try{
						Intent intent1 = new Intent(HomeScreen.this, ChatService.class);
						if(intent1!=null)
							stopService(intent1);
					}catch(Exception ex){
						ex.printStackTrace();
					}
	                Intent intent = new Intent(HomeScreen.this, RegistrationOptions.class);
	                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
	                intent.putExtra("CONFLICT_LOGOUT", true);
	        		startActivity(intent);
				}
				bteldialog.cancel();
				return false;
			}
		});
		bteldialog.show();
	}
	public void showExitDialog(String s) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setText("Exit");
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				iPrefManager.clearSharedPref();
				ChatDBWrapper.getInstance().clearMessageDB();
				DBWrapper.getInstance().clearAllDB();
				Intent intent = new Intent(HomeScreen.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return false;
			}
		});
		bteldialog.show();
	}
	public void onExistingSignInClick(View view) {
	}

	private void loadFragments() {
		FragmentManager fragmentmanager = getSupportFragmentManager();

		if (fragmentmanager.findFragmentByTag("ContactsFragment") == null) {
			contactsFragment = new ContactsScreen();
		} else {
			contactsFragment = (ContactsScreen) getSupportFragmentManager()
					.findFragmentByTag("ContactsFragment");
		}
		if (fragmentmanager.findFragmentByTag("ChatFragment") == null) {
			chatFragment = new ChatHome();
		} else {
			chatFragment = (ChatHome) getSupportFragmentManager()
					.findFragmentByTag("ChatFragment");
		}
		if (fragmentmanager.findFragmentByTag("BulletinFragment") == null) {
			bulletinFragment = new BulletinScreen();
		} else {
			bulletinFragment = (BulletinScreen) getSupportFragmentManager().findFragmentByTag("BulletinFragment");
		}
		if (fragmentmanager.findFragmentByTag("PublicGroupScreen") == null) {
			publicGroupFragment = new PublicGroupScreen();
		} else {
			publicGroupFragment = (PublicGroupScreen) getSupportFragmentManager()
					.findFragmentByTag("PublicGroupScreen");
		}
	}
	private void replaceFragment(Fragment fragment, int i) {
		fragment.setRetainInstance(true);
		if (!fragment.isAdded()) {
			fragmentTransaction.replace(i, fragment);
//			if (fragment instanceof RecentDetailFragment) {
//				fragmentTransaction.addToBackStack("RecentDetail");
//			}
			fragmentTransaction.commitAllowingStateLoss();
		}
	}

	public void onStartChatClick(View view){
		if (!contactMenuLayout.isSelected()) {
			mViewPager.setCurrentItem(1);
		}
	}
	public void onNewChatClick(View view){
		if(calledForShare)
			return;
		Intent intent = new Intent(this, EsiaChatContactsScreen.class);
		intent.putStringArrayListExtra(Constants.GROUP_USERS, null);
		intent.putExtra(Constants.CHAT_TYPE, Constants.NARMAL_CHAT);
		startActivity(intent);
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		boolean frompush = false;
		Bundle extras = intent.getExtras();
		if (extras != null) {
			frompush = extras.getBoolean("FROM_NOTIFICATION");
			if(frompush) {
				String user = iPrefManager.getUserPhone();
				if(user != null && user.contains("-"))
					user = user.replace("-", "");
				switchSG(user + "_" + extras.getString("DOMAIN_NAME"));
				return;
			}
		}
	}
	public void onResume(){
		super.onResume();
		isforeGround = true;
		isLaunched = true;
		isRWA = SharedPrefManager.getInstance().getDomainType().equals("rwa");
//		System.out.println("[ON-RESUME CALLED]");

//		System.out.println("[ON-RESUME 1]");
		startService(new Intent(SuperChatApplication.context, SinchService.class));
//		System.out.println("[ON-RESUME 2]");

		bindService(new Intent(this, ChatService.class), mConnection,Context.BIND_AUTO_CREATE);
		getShareInfo();
		syncProcessStart(true);
		if(!iPrefManager.isMyExistence()){
			showExitDialog("You have been removed.");
		}
		notificationUI();
		if(iPrefManager.isUpdateCheckNeeded()){
			if(Build.VERSION.SDK_INT >= 11)
				new GetVersionCode().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new GetVersionCode().execute();
		}
		checkForBackUpAndUploadBackup();
	}
	boolean backUpFound;
		class CheckDataBackup extends AsyncTask<String, String, String> {

		public CheckDataBackup(){
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
		    	String url = Constants.SERVER_URL + "/tiger/rest/user/profile/getbackup";
		    	Log.i(TAG, "CheckAvailability :: doInBackground : URL - "+url);
		        HttpPost httppost = new HttpPost(url);
		        httppost = SuperChatApplication.addHeaderInfo(httppost,true);
		        HttpClient httpclient = new DefaultHttpClient();
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
		    String fileid = null;
		    String lastdate = null;
		    if(data != null){
		    	System.out.println("Response======>"+data);
		    	try {
					JSONObject jsonobj = new JSONObject(data);
					if (jsonobj != null && jsonobj.getString("status") != null
							&& jsonobj.getString("status").equalsIgnoreCase("success")){
						if(jsonobj.has("backupFileId"))
							fileid = jsonobj.getString("backupFileId");
						if(jsonobj.has("backupDate"))
							lastdate = jsonobj.getString("backupDate");
						//this time is gmt 00 time.
						long time = convertTomilliseconds(lastdate);
						if(time  > 0)
							SharedPrefManager.getInstance().setLastBackUpTime(time);
						Date date = new Date(time);
						SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US);
//						System.out.println("[lastdate - ] "+lastdate);
						 Intent intent = new Intent(HomeScreen.this, ChatBackupRestoreScreen.class);
						 if(fileid != null)
						    intent.putExtra(Constants.BACKUP_FILEID, fileid);
						 if(lastdate != null)
						   	intent.putExtra(Constants.LAST_BACKUP_DATE, dateformat.format(date));
//					    startActivity(intent);
						backUpFound = true;
						startActivityForResult(intent, 111);
					}else
						addNewGroupsAndBroadcastsToDB();
			} catch (JSONException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   }
		   return data;
		}
		@Override
		protected void onPostExecute(String data) {
			if(data != null){
				iPrefManager.setBackupCheckedForSG(iPrefManager.getUserDomain(), true);
			}
		}
	}


	@Override
	public void onBackPressed() {
	    int count = getFragmentManager().getBackStackEntryCount();
	    if (count == 0) {
//	        super.onBackPressed();
	        moveTaskToBack(true);
	        //additional code
	    } else {
	        getFragmentManager().popBackStack();
	    }

	}
	public void getShareInfo(){
		// Get intent, action and MIME type

		    Intent intent = getIntent();
		    String action = intent.getAction();
		    String type = intent.getType();

		    if (intent!=null && type != null && action != null && Intent.ACTION_SEND.equals(action)) {
			    if (type.startsWith("image/"))
			    	sharingType = IMAGE_SHARING;
			    else if (type.startsWith("application/pdf"))
			    	sharingType = PDF_SHARING;
			    else if (type.startsWith("application/msword") || type.startsWith("application/doc")|| type.endsWith("document"))
				sharingType = DOC_SHARING;
				else if (type.startsWith("application/vnd.ms-excel") || type.startsWith("application/xls") || type.endsWith(".sheet"))
			    	sharingType = XLS_SHARING;
			    else if (type.startsWith("audio/"))
			    	sharingType = VOICE_SHARING;
			    else if (type.startsWith("video/"))
			    	sharingType = VIDEO_SHARING;
				else if (type.startsWith("application/ppt") || type.equalsIgnoreCase("application/*") || type.contains(".presentation") || type.endsWith("powerpoint"))
					sharingType = PPT_SHARING;
			    else
			    	sharingType = 0;

				if(sharingType!=0){
				    	Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
			    		if (imageUri != null) {
							if(imageUri.toString().contains("com.google.android.apps"))
							{
//								intent.setAction(null);
//								Toast.makeText(this, "Link sharing is not supported for now. Will update with the new version.", Toast.LENGTH_LONG).show();
//								return;
								shareUri = getRealPathFromURIForFile(this, imageUri);
							}else if (imageUri.toString().startsWith("content://")){
								if (imageUri.toString().startsWith("content://gmail"))
									shareUri = getRealPathFromURIForFile(this, imageUri);
								else
									shareUri = getRealPathFromURI(this, imageUri);
			    			}else if (imageUri.toString().startsWith("file:///storage/")){
			    				shareUri = imageUri.getPath();
			    			}else
			    				shareUri = imageUri.toString();
			    		}else{
							Toast.makeText(this, "This file sharing is not supported", Toast.LENGTH_LONG).show();
						}
			    		if(shareUri != null){
				    		intent.setAction(null);
				    		calledForShare = true;
			    		}else
			    			return;
		    		}else{
					intent.setAction(null);
					Toast.makeText(this, "This file share not supported", Toast.LENGTH_LONG).show();
				}
		    }

//		    if (uri.toString().startsWith("content://gmail")) { // special handling for gmail
//		        Bitmap b;
//		        InputStream stream = activity.getContentResolver().openInputStream(uri);
//		        b = BitmapFactory.decodeStream(stream);
//		    }


//		    /Get your uri
//		    Uri mAndroidUri = Uri.parse("content://gmail-ls/messages/my_gmail_id_@_gmail.com/65/attachments/0.1/SIMPLE/false");
//		    ImageView iv = new ImageView(context);
//
//		    try{
//		        //converts android uri to java uri then from that to file
//		        //after converting to file you should be able to manipulate it in anyway you like.
//		        File mFile = new File(new URI(mAndroidUri.toString()));
//
//		        Bitmap bmp = BitmapFactory.decodeStream(mFile);
//		        if (null != bmp)
//		            iv.setImageBitmap(bmp);
//		        else
//		            System.out.println("The Bitmap is NULL");
//
//		        }catch(Exception e){}
//		    }

	}
	public String getRealPathFromURI(Context context, Uri contentUri) {
		  Cursor cursor = null;
		  try {
		    String[] proj = { MediaStore.Images.Media.DATA };
		    cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
		    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		    cursor.moveToFirst();
		    return cursor.getString(column_index);
		  } finally {
		    if (cursor != null) {
		      cursor.close();
		    }
		  }
		}
	public String getRealPathFromURIForFile(Context context, Uri fileAttachUri) {
		 String full_path = null;
		try {
			String mimeType = getContentResolver().getType(fileAttachUri);
            Log.i(TAG, "mimeType : " + mimeType);
            if(mimeType != null && mimeType.lastIndexOf("/") != -1)
            	mimeType = "." + mimeType.substring(mimeType.lastIndexOf("/") + 1);
            Cursor returnCursor = getContentResolver().query(fileAttachUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            Log.i(TAG, "sizeIndex : " + sizeIndex);
            returnCursor.moveToFirst();
            String path = returnCursor.getString(nameIndex);
//            path = path.replace(" ", "_");
//            if(!path.endsWith(mimeType))
//            	path = path + mimeType;
            full_path = writeFileAndGetFullPath(getContentResolver().openInputStream(fileAttachUri),
                    new File(Environment.getExternalStorageDirectory() + "//" + path));
		}catch(Exception ex){

		}
		return full_path;
	}
	private String writeFileAndGetFullPath(InputStream in, File file) {
        String full_path = file.getAbsolutePath();
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return full_path;
    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK)
			switch (requestCode) {
				case 111:
					backUpFound = false;
					addNewGroupsAndBroadcastsToDB();
					break;
			}
		}
	private void addNewGroupsAndBroadcastsToDB(){
		try{
			if(directoryGroupSet !=null && !directoryGroupSet.isEmpty()) {
				for (GroupDetail groupDetail : directoryGroupSet) {
					boolean isFirstChat = ChatDBWrapper.getInstance(SuperChatApplication.context).isFirstChat(groupDetail.groupName);
					if (isFirstChat)
						saveMessage(groupDetail.displayName, groupDetail.groupName, "Group created by " + groupDetail.userDisplayName);
				}
				directoryGroupSet = null;
			}

			if(directoryBroadcastGroupSet !=null && !directoryBroadcastGroupSet.isEmpty()) {
				for (BroadcastGroupDetail broadcastGroupDetail : directoryBroadcastGroupSet) {
					boolean isFirstChat = ChatDBWrapper.getInstance(SuperChatApplication.context).isFirstChat(broadcastGroupDetail.broadcastGroupName);
					if (isFirstChat)
						saveMessage(broadcastGroupDetail.displayName, broadcastGroupDetail.broadcastGroupName, "Broadcast created by " + broadcastGroupDetail.userDisplayName);
				}
				directoryBroadcastGroupSet = null;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	protected void onPause() {
//		chatClient.stopClient();
		isforeGround = false;
		super.onPause();
//		try {
//			unbindService(this);
//		} catch (Exception e) {
//			// Just ignore that
//			Log.d("MessageHistoryScreen", "Unable to un bind");
//		}
//		if(syncAnimation!=null && syncAnimation.isRunning()){
//			syncAnimation.cancel();
////			if(syncTimer!=null){
////				syncTimer.cancel();
////				syncTimer = null;
////			}
//		}
		syncProcessStart(false);
	}
protected void onDestroy(){
	super.onDestroy();
	calledForShare = false;
	isLaunched = false;
	try {
		unbindService(mConnection);
	} catch (Exception e) {
		// Just ignore that
		Log.d("MessageHistoryScreen", "Unable to un bind");
	}
}
public void onComposeClick(View view){
	if (!contactMenuLayout.isSelected()) {
		contactsFragment.setPorfileListener();
		mViewPager.setCurrentItem(2);
	}
}
	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.id_more:
			if(calledForShare && !SharedPrefManager.getInstance().isDomainAdminORSubAdmin()){
				Toast.makeText(this, "Only owner can post messages in Bulletin!", Toast.LENGTH_LONG).show();
				return;
			}
//			if (!moreMenuLayout.isSelected()) {
//				moreMenuLayout.setSelected(true);
//				contactMenuLayout.setSelected(false);
//				chatMenuLayout.setSelected(false);
//			fragmentTransaction = getSupportFragmentManager().beginTransaction();
//			replaceFragment(moreFragment, R.id.main_frame);
//			}
			mViewPager.setCurrentItem(3);
			break;
		case R.id.id_channel:
			if (!publicGroupTab.isSelected()) {
				mViewPager.setCurrentItem(1);
			}
			break;
		case R.id.id_chat:
			if (!chatMenuLayout.isSelected()) {
//				chatMenuLayout.setSelected(true);
//				contactMenuLayout.setSelected(false);
//				moreMenuLayout.setSelected(false);
//				fragmentTransaction = getSupportFragmentManager().beginTransaction();
//				replaceFragment(chatFragment, R.id.main_frame);
				mViewPager.setCurrentItem(0);
			}
			break;
		case R.id.id_contact:
			if (!contactMenuLayout.isSelected()) {
//				contactMenuLayout.setSelected(true);
//				chatMenuLayout.setSelected(false);
//				moreMenuLayout.setSelected(false);
//				fragmentTransaction = getSupportFragmentManager().beginTransaction();
//				replaceFragment(contactsFragment, R.id.main_frame);
				mViewPager.setCurrentItem(2);
				contactsFragment.setPorfileListener();
			}
			break;

		}


	}
	public void saveMessage(String displayName, String from, String msg) {
		try {
			ChatDBWrapper chatDBWrapper = ChatDBWrapper.getInstance();
			ContentValues contentvalues = new ContentValues();
			String myName = SharedPrefManager.getInstance().getUserName();
			contentvalues.put(DatabaseConstants.FROM_USER_FIELD, from);
//			contentvalues.put(DatabaseConstants.TO_USER_FIELD, myName);
			if(SharedPrefManager.getInstance().isBroadCast(from))
			    contentvalues.put(DatabaseConstants.TO_USER_FIELD, from);
			else
			    contentvalues.put(DatabaseConstants.TO_USER_FIELD, myName);
			contentvalues.put(DatabaseConstants.UNREAD_COUNT_FIELD, new Integer(1));
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
				if(name!=null && name.equals(from))
					name = displayName+"#786#"+from;
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
				contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, XMPPMessageType.atMeXmppMessageTypeSpecialMessage.ordinal());
			} else {
				contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "0");
			}
//			AtMeApplication.dayValue = date;
			contentvalues.put(DatabaseConstants.LAST_UPDATE_FIELD, 0);

            if(SharedPrefManager.getInstance().isBroadCast(from)) {
                if(name.indexOf("#786#") != -1) {
                    SharedPrefManager.getInstance().saveBroadcastFirstTimeName(from, name.substring(0, name.indexOf("#786#")));
                    contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, name.substring(0, name.indexOf("#786#")));
                }
                else {
                    SharedPrefManager.getInstance().saveBroadcastFirstTimeName(from, name);
                    contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, name);
                }
            }else
			    contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, name);
			//Save USerID and SG in DB
			contentvalues.put(DatabaseConstants.USER_ID, SharedPrefManager.getInstance().getUserId());
			contentvalues.put(DatabaseConstants.USER_SG, SharedPrefManager.getInstance().getUserDomain());
			System.out.println("HomeScreen::saveMessage: - "+contentvalues.toString());
			chatDBWrapper.insertInDB(DatabaseConstants.TABLE_NAME_MESSAGE_INFO,contentvalues);
		} catch (Exception e) {

		}
	}
	 class HomePagerAdapter extends FragmentPagerAdapter{
		private Context mContext;
		FragmentManager fm;
//		 private String[] titles = new String[]{"CHAT","CONTACT","MORE"};
		public HomePagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			this.fm = fm;
			this.mContext = context;
		}

		@Override
		public int getCount() {
			return 4;
		}
		@Override
		public int getItemPosition(Object object) {
		    return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position) {
			switch(position) {
			case 0:
				return new ChatHome();
			case 1:
				return  publicGroupFragment;//new PublicGroupScreen();
			case 2:
				return (contactsFragment = new ContactsScreen());

			case 3:
				return  new BulletinScreen();
			}

			return null;
		}
//		@Override
//	    public CharSequence getPageTitle(int position) {
//	        return titles[position];
//	    }
	}
//------------ Changes for call ---------------
	 @Override
	    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
	        if (SinchService.class.getName().equals(componentName.getClassName())) {
	            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
	            onServiceConnected();

	          //Register the user for call
	    		if (!getSinchServiceInterface().isStarted()) {
	                getSinchServiceInterface().startClient(SharedPrefManager.getInstance().getUserName());
	            }
	        }
	    }

	    @Override
	    public void onServiceDisconnected(ComponentName componentName) {
	        if (SinchService.class.getName().equals(componentName.getClassName())) {
	            mSinchServiceInterface = null;
	            onServiceDisconnected();
	        }
	    }

	    protected void onServiceConnected() {
	        // for subclasses
	    }

	    protected void onServiceDisconnected() {
	        // for subclasses
	    }

	    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
	        return mSinchServiceInterface;
	    }
		@Override
		public void onStartFailed(SinchError error) {
			// TODO Auto-generated method stub
			Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
		}
		@Override
		public void onStarted() {
			// TODO Auto-generated method stub

		}
		private class OpenGroupTaskOnServer extends AsyncTask<String, String, String> {
			LoginModel loginForm;
			ProgressDialog progressDialog = null;
			SharedPrefManager sharedPrefManager;
			boolean isLoading;
			public OpenGroupTaskOnServer(boolean isLoading){
				sharedPrefManager = SharedPrefManager.getInstance();
				loginForm = new LoginModel();
				loginForm.setUserName(sharedPrefManager.getUserName());
				loginForm.setPassword(sharedPrefManager.getUserPassword());
				if(sharedPrefManager.getDeviceToken() != null)
					loginForm.setToken(sharedPrefManager.getDeviceToken());
				this.isLoading = isLoading;
			}
			@Override
			protected void onPreExecute() {
//				if(isLoading)
//					progressDialog = ProgressDialog.show(HomeScreen.this, "", "Fetching data. Please wait...", true);
				super.onPreExecute();
			}
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
//				String JSONstring = new Gson().toJson(loginForm);
				String searchText = "";
				if(params!=null && params.length>0){
					searchText = "text="+params[0]+"&";
				}
			    DefaultHttpClient client1 = new DefaultHttpClient();

//				Log.d("HomeScreen", "serverUpdateCreateGroupInfo request:"+JSONstring);  p5domain
				 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/group/search?"+searchText+"limit=1000");
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
				 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
				 HttpResponse response = null;

		         try {
//					httpPost.setEntity(new StringEntity(JSONstring));
					 try {
						 response = client1.execute(httpPost);
						 final int statusCode=response.getStatusLine().getStatusCode();
						 if (statusCode == HttpStatus.SC_OK){ //new1
							 HttpEntity entity = response.getEntity();
//							    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
							    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
							    String line = "";
					            String str = "";
					            while ((line = rd.readLine()) != null) {

					            	str+=line;
					            }
					            if(str!=null &&!str.equals("")){
					            	Gson gson = new GsonBuilder().create();
					            	LoginResponseModel loginObj = gson.fromJson(str,LoginResponseModel.class);
									if (loginObj != null) {
										if(loginObj.directoryGroupSet!=null){
											groupsData.clear();
											for (GroupDetail groupDetail : loginObj.directoryGroupSet) {
												if(!groupDetail.memberType.equals("USER"))
													SharedPrefManager.getInstance().saveUserGroupInfo(groupDetail.groupName, SharedPrefManager.getInstance().getUserName(), SharedPrefManager.PUBLIC_CHANNEL, true);
												groupsData.add(groupDetail);
											}
										}
						            }



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
//								iPrefManager.saveUserDomain(domainNameView.getText().toString());
								iPrefManager.saveUserId(errorModel.userId);
								//below code should be only, in case of brand new user - "First time SC user"
								iPrefManager.setAppMode("SecondMode");
//								iPrefManager.saveUserPhone(regObj.iMobileNumber);
		//						iPrefManager.saveUserPassword(regObj.getPassword());
								iPrefManager.saveUserLogedOut(false);
								iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
								showDialog(citrusError.message, null);
							}else if(citrusError!=null && citrusError.code.equals("20020") ){
								showDialog(citrusError.message, citrusError.code);
							}else
								showDialog(citrusError.message, null);
						} else if (errorModel.message != null)
							showDialog(errorModel.message, null);
					} else
						showDialog("Please try again later.", null);
				}else{
//					if(mViewPager.getCurrentItem() == 1){
					iPrefManager.setGroupsLoaded(true);
					if(PublicGroupScreen.isAllChannelTab)
						publicGroupFragment.showAllContacts(1);
					else
						publicGroupFragment.showAllContacts(0);
//					}
				}
				super.onPostExecute(str);
			}
		}
//-------------------------------------------------------
		public static class GetSharedIDListFromServer extends AsyncTask<String, String, String> {
			LoginModel loginForm;
			ProgressDialog progressDialog = null;
			SharedPrefManager sharedPrefManager;
			public GetSharedIDListFromServer(){
				sharedPrefManager = SharedPrefManager.getInstance();
				loginForm = new LoginModel();
				loginForm.setUserName(sharedPrefManager.getUserName());
				loginForm.setPassword(sharedPrefManager.getUserPassword());
				if(sharedPrefManager.getDeviceToken() != null)
					loginForm.setToken(sharedPrefManager.getDeviceToken());
			}
			@Override
			protected void onPreExecute() {
//				if(isLoading)
//					progressDialog = ProgressDialog.show(HomeScreen.this, "", "Fetching data. Please wait...", true);
				super.onPreExecute();
			}
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
//				String shared_id_name = "";
//				if(params != null && params.length > 0){
////					shared_id_name = "text="+params[0]+"&";
//					shared_id_name = params[0];
//				}
			    DefaultHttpClient client1 = new DefaultHttpClient();
			    //http://52.88.175.48/tiger/rest/sharedid/getall?domainName=p5domain
				 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/sharedid/getall?domainName="+sharedPrefManager.getUserDomain());
				 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
				 HttpResponse response = null;

		         try {
					 try {
						 response = client1.execute(httpPost);
						 final int statusCode=response.getStatusLine().getStatusCode();
						 if (statusCode == HttpStatus.SC_OK){ //new1
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
				if(str != null && str.length() > 0){
					try {
						JSONObject jsonobj = new JSONObject(str);
						if (jsonobj != null && jsonobj.getString("status") != null
								&& jsonobj.getString("status").equalsIgnoreCase("success")){
							if(str != null && !str.equals("")){
								parseSharedIDData(str);
							}
						}//else Do Nothing
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//else Do Nothing
			super.onPostExecute(str);
			}
		}
//-------------------------------------------------------
		public static void parseSharedIDData(String str){
			try{
				SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance();
				Gson gson = new GsonBuilder().create();
            	LoginResponseModel loginObj = gson.fromJson(str, LoginResponseModel.class);
            	if (loginObj != null) {
					if(loginObj.directoryBroadcastGroupSet != null && loginObj.directoryBroadcastGroupSet.size() > 0){
						sharedIDData.clear();
						System.out.println("HomeScreen :: SignInTaskOnServer : Writing Official ID Data..");
						for (BroadcastGroupDetail shared_id_detail : loginObj.directoryBroadcastGroupSet) {
							if(sharedPrefManager.getSharedIDDisplayName(shared_id_detail.broadcastGroupName) == null){
								System.out.println("Shared ID :: "+shared_id_detail.displayName+" : "+shared_id_detail.broadcastGroupName);
								sharedPrefManager.saveSharedIDDisplayName(shared_id_detail.broadcastGroupName, shared_id_detail.displayName);
								sharedPrefManager.setSharedIDContact(shared_id_detail.broadcastGroupName, true);
								if(shared_id_detail.fileId != null)
									sharedPrefManager.saveSharedIDFileId(shared_id_detail.broadcastGroupName, shared_id_detail.fileId);
								//Update Shared Id'd to add on top of contact list
								ContentValues contentvalues = new ContentValues();
								//Shared ID name is saved in username field
								contentvalues.put(DatabaseConstants.USER_NAME_FIELD, shared_id_detail.broadcastGroupName);
								contentvalues.put(DatabaseConstants.VOPIUM_FIELD, Integer.valueOf(1));
								contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD, shared_id_detail.broadcastGroupMemberId);
								int id = shared_id_detail.userName.hashCode();
								if (id < -1)
									id = -(id);
								contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD, Integer.valueOf(id));
								contentvalues.put(DatabaseConstants.RAW_CONTACT_ID, Integer.valueOf(id));
								//Shared ID Display name is saved in Display name field
								contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, Constants.SHARED_ID_START_STRING + shared_id_detail.displayName);
								contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD, Integer.valueOf(0));
								contentvalues.put(DatabaseConstants.DATA_ID_FIELD, Integer.valueOf("5"));
								contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
								contentvalues.put(DatabaseConstants.STATE_FIELD, Integer.valueOf(0));
								contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, "9999999999");
								DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
							}
							sharedIDData.add(shared_id_detail);
						}
					}
            	}
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
//-------------------------------------------------------
		public static int messageCounter = 0;
		public static final int VALUE_TO_EMAIL = 2;
		public void writeLogsToFile(String message) {
			String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SuperChat/logs.txt";
			File file = new File(fileName);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file,
						true));
				Date d = new Date();
				writer.write(""+ d.toString()+"->");
				writer.write(message);
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally
			{
				messageCounter++;
				if(messageCounter == VALUE_TO_EMAIL)
				{
					BufferedReader br = null;
					String sCurrentLine;
					StringBuffer buffer = new StringBuffer();
					try {
						br = new BufferedReader(new FileReader(fileName));
						while ((sCurrentLine = br.readLine()) != null) {
							buffer.append(sCurrentLine);
							buffer.append("\n");
						}

					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							if (br != null)
								br.close();
							//Send email
							messageCounter = 0;

						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}

		public void sendEmail(String aSubject, String aMessage) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_EMAIL, new String[] { "mahesh@citrusplatform.com"});
			i.putExtra(Intent.EXTRA_SUBJECT, aSubject);
			i.putExtra(Intent.EXTRA_TEXT, aMessage);
			try {
				startActivity(Intent.createChooser(i,
						"Crash - Please send email to us for better experience"));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(HomeScreen.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
			}
		}
		private void serverUpdateContactsInfo(List<String> numbers){
			SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			ContactUploadModel model = new ContactUploadModel(iPrefManager.getUserId(),null, numbers);
			  String JSONstring = new Gson().toJson(model);
			    DefaultHttpClient client1 = new DefaultHttpClient();

//				Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);

				 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/contact/match");
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
				 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
				 HttpResponse response = null;
		         try {
					httpPost.setEntity(new StringEntity(JSONstring));
					 try {
						 response = client1.execute(httpPost);
						 final int statusCode=response.getStatusLine().getStatusCode();
						 if (statusCode == HttpStatus.SC_OK){ //new1
							 HttpEntity entity = response.getEntity();
		//					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
							    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
					            String line = "";
					            String str = "";
					            while ((line = rd.readLine()) != null) {

					            	str+=line;
					            }
					            if(str!=null &&!str.equals("")){
					            	str = str.trim();
//					            	Log.d(TAG, "serverUpdateContact sync response: "+str);
					            	Gson gson = new GsonBuilder().create();
									if (str==null || str.contains("error")){
//										contactSyncState = CONTACT_SYNC_FAILED;
//										Log.d(TAG,"serverUpdateContactsInfo onSuccess error comming : "	+ str);
										return;
									}

									ContactUpDatedModel updatedModel = gson.fromJson(str,ContactUpDatedModel.class);
									if (updatedModel != null) {
										DBWrapper wrapper = DBWrapper.getInstance();
//										Log.d(TAG,
//												"serverUpdateContactsInfo onSuccess : Contact synced successful. ");


//											if(iPrefManager.isDomainAdmin()){
//												String number1 = DBWrapper.getInstance().getContactNumber("create_group");
//												ContentValues contentvalues = new ContentValues();
//												if(number1==null){
//													contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"create_group");
//													contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
//													contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,"00000");
//													contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, SuperChatApplication.context.getResources().getString(R.string.create_group));
//													contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
//													int id = "create_group".hashCode();
//													if (id < -1)
//														id = -(id);
//													contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
//													contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
//													DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
//												}
//												String number1 = DBWrapper.getInstance().getContactNumber("create_broadcast");
//												if(number1==null){
//													contentvalues = new ContentValues();
//													contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"create_broadcast");
//													contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
//													contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,"11111");
//													contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, SuperChatApplication.context.getResources().getString(R.string.create_broadcast_list));
//													contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
//													int id = "create_broadcast".hashCode();
//													if (id < -1)
//														id = -(id);
//													contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
//													contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
//													DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
//												}
//												number1 = DBWrapper.getInstance().getContactNumber("new_domain_member");
//												if(number1==null){
//													contentvalues = new ContentValues();
//													contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"new_domain_member");
//													contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
//													contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,"22222");
//													contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, SuperChatApplication.context.getResources().getString(R.string.invite_member));
//													contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
//													int id = "new_domain_member".hashCode();
//													if (id < -1)
//														id = -(id);
//													contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
//													contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
//													DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
//												}
//												number1 = DBWrapper.getInstance().getContactNumber("remove_domain_member");
//												if(number1==null){
//													contentvalues = new ContentValues();
//													contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"remove_domain_member");
//													contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
//													contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,"333333");
//													contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, SuperChatApplication.context.getResources().getString(R.string.manage_members));
//													contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
//													int id = "remove_domain_member".hashCode();
//													if (id < -1)
//														id = -(id);
//													contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
//													contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
//													DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
//												}
//												number1 = DBWrapper.getInstance().getContactNumber("view_member_stats");
//												if(number1==null){
//													contentvalues = new ContentValues();
//													contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"view_member_stats");
//													contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
//													contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,"444444");
//													contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, SuperChatApplication.context.getResources().getString(R.string.view_member_stats));
//													contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
//													int id = "view_member_stats".hashCode();
//													if (id < -1)
//														id = -(id);
//													contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
//													contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
//													DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
//												}
//												number1 = DBWrapper.getInstance().getContactNumber("create_channel");
//												if(number1==null){
//													contentvalues = new ContentValues();
//													contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"create_channel");
//													contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
//													contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,"555555");
//													contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, SuperChatApplication.context.getResources().getString(R.string.create_channel));
//													contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
//													int id = "create_channel".hashCode();
//													if (id < -1)
//														id = -(id);
//													contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
//													contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
//													DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
//												}
//											}else if(SharedPrefManager.getInstance().isOpenDomain()){
//												String number1 = DBWrapper.getInstance().getContactNumber("new_domain_member");
//												if(number1==null){
//													ContentValues contentvalues = new ContentValues();
//													contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"new_domain_member");
//													contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
//													contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,"22222");
//													contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
//													contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, getString(R.string.invite_member));
//													contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
//													int id = "new_domain_member".hashCode();
//													if (id < -1)
//														id = -(id);
//													contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
//													contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
//													DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
//												}
//											}
											for (String st : updatedModel.mobileNumberUserBaseMap.keySet()) {
												ContactUpDatedModel.UserDetail userDetail = updatedModel.mobileNumberUserBaseMap.get(st);
//											}
//										for (UserResponseDetail userDetail : loginObj.directoryUserSet) {
											if(userDetail.userState!=null){
												if(userDetail.userState.equals("inactive"))
													iPrefManager.saveUserExistence(userDetail.userName,false);
					                        	 else if(userDetail.userState.equals("active"))
					                        		 iPrefManager.saveUserExistence(userDetail.userName,true);
											}
											String number = wrapper.getContactNumber(userDetail.userName);
											if(number!=null && !number.equals("")){
												wrapper.updateAtMeContactStatus(number);
												firstTimeAdmin = true;
												continue;
											}
//											UserResponseDetail userDetail = loginObj.directoryUserSet.get(st);
										//Alter Modified Tables here
//										wrapper.alterTable(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, new String[]{DatabaseConstants.FLAT_NUMBER, DatabaseConstants.BUILDING_NUMBER,
//												DatabaseConstants.ADDRESS, DatabaseConstants.RESIDENCE_TYPE});
										ContentValues contentvalues = new ContentValues();
										contentvalues.put(DatabaseConstants.USER_NAME_FIELD,userDetail.userName);
										contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(1));
										contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,userDetail.mobileNumber);
										int id = userDetail.userName.hashCode();
										if (id < -1)
											id = -(id);
										contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
										contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
										contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, (userDetail.name != null ? userDetail.name : ""));
										contentvalues.put(DatabaseConstants.CONTACT_TYPE_FIELD, userDetail.type);
										contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,Integer.valueOf(0));
										contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
										contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
										contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));

										//Add Address Details
										contentvalues.put(DatabaseConstants.FLAT_NUMBER, userDetail.flatNumber);
										contentvalues.put(DatabaseConstants.BUILDING_NUMBER, userDetail.buildingNumber);
										contentvalues.put(DatabaseConstants.ADDRESS, userDetail.address);
										contentvalues.put(DatabaseConstants.RESIDENCE_TYPE, userDetail.residenceType);

										contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, userDetail.mobileNumber);
										if(!userDetail.userName.equalsIgnoreCase(iPrefManager.getUserName()))
											wrapper.insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues);
//										else
											if(userDetail.userName.equalsIgnoreCase(iPrefManager.getUserName()))
												iPrefManager.saveDisplayName((userDetail.name != null ? userDetail.name : ""));

											iPrefManager.saveUserServerName(userDetail.userName, (userDetail.name != null ? userDetail.name : ""));
										if(userDetail.currentStatus!=null)
											iPrefManager.saveUserStatusMessage(userDetail.userName, userDetail.currentStatus);
										if(userDetail.department!=null)
											iPrefManager.saveUserDepartment(userDetail.userName, userDetail.department);
										if(userDetail.designation!=null)
											iPrefManager.saveUserDesignation(userDetail.userName, userDetail.designation);
										if(userDetail.gender!=null){
											iPrefManager.saveUserGender(userDetail.userName, userDetail.gender);
//											Log.i(TAG, "userName : "+userDetail.userName+", gender : "+userDetail.gender);
										}
										ContactUpDatedModel.UserDetail.PrivacyStatusMap privacyStatusMap = userDetail.getPrivacyStatusMap();
										if(privacyStatusMap!=null){
											if(privacyStatusMap.dnc == 1)
												iPrefManager.saveStatusDNC(userDetail.userName, true);
											else
												iPrefManager.saveStatusDNC(userDetail.userName, false);
											if(privacyStatusMap.dnm == 1)
												iPrefManager.saveStatusDNM(userDetail.userName, true);
											else
												iPrefManager.saveStatusDNM(userDetail.userName, false);
										}
										if(userDetail.imageFileId!=null){
											iPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
											if(userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
//												if (Build.VERSION.SDK_INT >= 11)
//													new BitmapDownloader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,userDetail.imageFileId);
//									             else
//									            	 new BitmapDownloader().execute(userDetail.imageFileId);
												Message msg = new Message();
												Bundle data = new Bundle();
												data.putString("TaskMessage",userDetail.imageFileId);
												msg.setData(data);
												mainTask.sendMessage(msg);
											}
										}
									}

//										for (String st : updatedModel.mobileNumberUserBaseMap
//												.keySet()) {
//											ContactUpDatedModel.UserDetail userDetail = updatedModel.mobileNumberUserBaseMap.get(st);
//											//						Log.d(TAG, "contacts sync info with sip address: " + userDetail.iSipAddress);
//											ContentValues contentvalues = new ContentValues();
////											contentvalues.put(
////													DatabaseConstants.USER_SIP_ADDRESS,
////													userDetail.iSipAddress);
//											contentvalues.put(
//													DatabaseConstants.USER_NAME_FIELD,
//													userDetail.userName);
//											contentvalues.put(
//													DatabaseConstants.VOPIUM_FIELD,
//													Integer.valueOf(1));
//											contentvalues
//											.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,
//													userDetail.mobileNumber);
//
//											DBWrapper.getInstance().updateAtMeDirectStatus(contentvalues,DatabaseConstants.CONTACT_NUMBERS_FIELD);
//											DBWrapper.getInstance().updateAtMeContactDetails(contentvalues,userDetail.mobileNumber);
//											DBWrapper.getInstance().updateUserNameInContacts(userDetail.userName,userDetail.mobileNumber);
//											if(userDetail!=null && userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
//												if(iPrefManager.getUserFileId(userDetail.userName) == null || !iPrefManager.getUserFileId(userDetail.userName).equals(userDetail.imageFileId))
//													new BitmapDownloader().execute(userDetail.imageFileId);
//												}
//											if(iPrefManager!=null && userDetail!=null && userDetail.userName!=null){
//												iPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
//												iPrefManager.saveUserStatusMessage(userDetail.userName,  userDetail.currentStatus);
//												if(userDetail.name!=null)
//													iPrefManager.saveUserServerName(userDetail.userName,userDetail.name);
//												DBWrapper.getInstance(context).getChatName(userDetail.userName);
//											}
//										}
//										contactSyncState = CONTACT_SYNC_SUCESSED;
									}
//									else
//										contactSyncState = CONTACT_SYNC_FAILED;
					            }

				            }
//						 else
//				            	contactSyncState = CONTACT_SYNC_FAILED;
					} catch (ClientProtocolException e) {
//						contactSyncState = CONTACT_SYNC_FAILED;
					} catch (IOException e) {
//						contactSyncState = CONTACT_SYNC_FAILED;
					}catch(Exception e){
//						contactSyncState = CONTACT_SYNC_FAILED;
					}

				} catch (UnsupportedEncodingException e1) {
//					contactSyncState = CONTACT_SYNC_FAILED;
				}catch(Exception e){
//					contactSyncState = CONTACT_SYNC_FAILED;
				}

		}
		public static String formatNumber(String str){
			try{
				if(str==null)
					return null;

				boolean isCountryCheckingNeeded = false;
				if(str.startsWith("00"))
				isCountryCheckingNeeded = true;
				if(str.length()>1)
					while(str.startsWith("0")){
						if(str.length()>1)
							str = str.substring(1);
						else break;
					}


			boolean isPlus = str.contains("+")?true:false;
			if(isPlus)
				isCountryCheckingNeeded = true;

			str = str.replace(" ","");
			str = str.replace("+","");
			str = str.replace("-","");
			str = str.replace("(","");
			str = str.replace(")","");

			if(str.length()<8)
				return str;

			String replacingCode = null;
			boolean isNumberModified = false;
			if(isCountryCheckingNeeded){
				for(int i = 5;i>=1;i--){
					replacingCode = str.substring(0, i);
				if(SuperChatApplication.countrySet.contains(replacingCode)){
					str = replacingCode+"-"+str.replaceFirst(replacingCode, "");
					isNumberModified = true;
					break;
				}}
			}
			if(!isNumberModified)
			{
				String code = Constants.countryCode.replace("+", "");
				if(str.startsWith(code))
					str = code+"-"+str.replaceFirst(code, "");
				else
					str = code+"-"+str;
			}

			}catch(Exception e){
				e.printStackTrace();
			}
			return str;
		}
		private class GetVersionCode extends AsyncTask<String, String, String> {
			public final Pattern CURRENT_VERSION_REGEX = Pattern.compile(".*?softwareVersion.*?[>](.*?)[<][/]div.*?");
		    @Override
		    protected String doInBackground(String...voids) {
				String version = null;
				try{
				version = getData("https://play.google.com/store/apps/details?id=com.superchat");
				if(version!=null && !version.equals(""))
				version = getCurrentVersion(version);
				}catch(Exception e){
					e.printStackTrace();
				}
		         return version;
		    }
		    public  String getCurrentVersion(String data) {

	            data = data.replaceAll("\\s+", " ");

	            Matcher m = CURRENT_VERSION_REGEX.matcher(data);

	            if (m.matches()) {

	                  return m.group(1).trim();

	            }

	            return null;

	      }



	      public String getData(String urlPath) {
	    	  try{
	    	  URL url = new URL(urlPath);

	    	  HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
//	    	   urlConnection.setSSLSocketFactory(getSocketFactory());
	    	  urlConnection.connect();
	    	   java.io.InputStream input = urlConnection.getInputStream();

	    	  int count;

//	            java.io.InputStream input = conection.getInputStream();
	            // getting file length
				  int lenghtOfFile = urlConnection.getContentLength();
				  // input stream to read file - with 8k buffer
//				  java.io.InputStream input = new BufferedInputStream(urlConnection.openStream());
				  // Output stream to write file
				  byte data[] = new byte[4096];
				  long total = 0;
				  StringBuilder sb = new StringBuilder();
				  while ((count = input.read(data)) != -1) {



//	            while (input.read(b) > 0) {

	                  sb.append(new String(data, Charset.forName("utf-8")));

	            }

	            input.close();

	            return sb.toString();
	    	  }catch(Throwable e){
	    		  e.printStackTrace();
	    		  Log.d(TAG, "Error throwing in version fetching from play store."+e.toString());
	    	  }
	    	  return null;
	      }
		    @Override
		    protected void onPostExecute(String onlineVersion) {
		        super.onPostExecute(onlineVersion);
		        String currentVersion = getCurrentAppVersion();
		        if (onlineVersion != null && !onlineVersion.isEmpty()) {
		            if (!currentVersion.equals(onlineVersion) && versionCompare(currentVersion,onlineVersion)>0) {
		            	Toast.makeText(HomeScreen.this, "Updates available.", Toast.LENGTH_LONG).show();
		            	showUpdateDialog("Update Available!","Do you want to update \"SuperChat\"?");
		            	iPrefManager.setUpdateCheck(false);
		            }else
		            	iPrefManager.setUpdateCheck(true);
		        }else
		        	iPrefManager.setUpdateCheck(true);//Toast.makeText(HomeScreen.this, "Current version " + currentVersion + "playstore version " + onlineVersion, Toast.LENGTH_LONG).show();

//		        Log.d("update", "Current version " + currentVersion + "playstore version " + onlineVersion);
		    }
		}
		 private int  versionCompare(String currentVersion, String onlineVersion) {
			 try{
			 	if(currentVersion==null || onlineVersion == null)
			 		return 0;
		        String[] currentVersionData = currentVersion.split("[.]");

		        String[] onlineVersionData = onlineVersion.split("[.]");



		        Integer[] currentVersionInt = new Integer[3];

		        Integer[] onlineVersionInt = new Integer[3];



		        for (int i = 0; i < 3; i++) {

		              currentVersionInt[i] = Integer.parseInt(currentVersionData[i].trim());

		        }

		        for (int i = 0; i < 3; i++) {

		              onlineVersionInt[i] = Integer.parseInt(onlineVersionData[i].trim());

		        }



		        for (int i = 0; i < 3; i++) {
		              if (currentVersionInt[i] < onlineVersionInt[i]) {
		                    return 1;
		              } else if (currentVersionInt[i] > onlineVersionInt[i]) {
		                    return -1;
		              }
		        }
			 }catch(Exception e){}
		        return 0;
		  }
		public void showUpdateDialog(final String title, final String s) {
			final Dialog bteldialog = new Dialog(this);
			bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			bteldialog.setCanceledOnTouchOutside(true);
			bteldialog.setContentView(R.layout.custom_dialog_two_button);
			if(title!=null){
				((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
				}
			((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
			((TextView)bteldialog.findViewById(R.id.id_send)).setText("Update Now");
			((TextView)bteldialog.findViewById(R.id.id_cancel)).setText("Later");
			((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					bteldialog.cancel();
//					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.superchat")));
					try {

						Intent intent = new Intent(Intent.ACTION_VIEW);
			            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			            intent.setData(Uri.parse("market://details?id=com.superchat"));
			            startActivity(intent);

			        } catch (Exception anfe) {
			        	try{
				        	Intent intent = new Intent(Intent.ACTION_VIEW);
				            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				            intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.superchat"));
				            startActivity(intent);
			        	}catch(Exception e){}
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

		public void showPopup(View v){
			 PopupMenu popup = new PopupMenu(this, v);
			 popup.setOnMenuItemClickListener(this);
			 if (publicGroupTab.isSelected())// && iPrefManager.isDomainAdmin())
				 popup.getMenu().add(0,0,0,getResources().getString(R.string.create_group));

			 if(contactMenuLayout.isSelected()){
				 if(iPrefManager.isDomainAdmin()){
					 popup.getMenu().add(0,2,0,getResources().getString(R.string.create_broadcast_list));
					 popup.getMenu().add(0,3,0,getResources().getString(R.string.invite_member));
				 } else if(iPrefManager.isDomainSubAdmin()){
					 popup.getMenu().add(0,2,0,getResources().getString(R.string.create_broadcast_list));
					 popup.getMenu().add(0,3,0,getResources().getString(R.string.invite_member));
				 }else if(SharedPrefManager.getInstance().isOpenDomain()){
//			 		popup.getMenu().add(0,3,0,getResources().getString(R.string.invite_member));
			 	}
			 }
			 popup.getMenu().add(0,1,0,getResources().getString(R.string.settings));
			 popup.show();
		}
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO Auto-generated method stub
			  switch (item.getItemId()) {
		        case 0: // Create A group
		        	Intent intent = new Intent(HomeScreen.this, CreateGroupScreen.class);
					intent.putExtra(Constants.CHANNEL_CREATION, true);
					startActivity(intent);
					return true;
		        case 1: // Settings
		        	intent = new Intent(HomeScreen.this, MoreScreen.class);
					startActivity(intent);
					return true;
		        case 2: // create_broadcast_list
		        	intent = new Intent(SuperChatApplication.context, CreateBroadCastScreen.class);
					intent.putExtra(Constants.BROADCAST, true);
					startActivity(intent);
		        	return true;
		        case 3: // invite_member
		        	intent = new Intent(SuperChatApplication.context, BulkInvitationScreen.class);
					startActivity(intent);
		        	return true;
			  }
			return false;
		}
//============================
		 public boolean isServiceRunning(String serviceClassName){
		        final ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		        final List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

		        for (RunningServiceInfo runningServiceInfo : services) {
//		        	System.out.println("ClassName : "+runningServiceInfo.service.getClassName());
		            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
		                return true;
		            }
		        }
		        return false;
		     }
	//-----------------------------------------------
	private void getBulletinMessages() {
		try {
			retrofit2.Call call = null;
			final SharedPrefManager pref = SharedPrefManager.getInstance();
			call = objApi.getApi(this).getMessages("" + 10);
			call.enqueue(new RetrofitRetrofitCallback<BulletinGetMessageDataModel>(this) {
				@Override
				protected void onResponseVoidzResponse(retrofit2.Call call, Response response) {
//					System.out.println("[Here.....]");
				}

				@Override
				protected void onResponseVoidzObject(retrofit2.Call call, BulletinGetMessageDataModel response) {
					if (response != null && response.getStatus() != null && response.getStatus().equalsIgnoreCase("success")) {
						Set<BulletinGetMessageDataModel.MessageData> messages = response.getBulletinMessageAPIList();
						String next_url = null;
						String json_body = null;
						String media_url = null;
						int type = 0;
						String caption = null;
						String extension = null;
						next_url = response.getNextUrl();
						if (!messages.isEmpty()) {
							LinkedList<BulletinGetMessageDataModel.MessageData> list = new LinkedList<BulletinGetMessageDataModel.MessageData>(messages);
							Iterator<BulletinGetMessageDataModel.MessageData> itr = list.descendingIterator();
							System.out.println("HomeScreen :: SignInTaskOnServer : Writing Bulletin Messages..");
							while(itr.hasNext()) {
								BulletinGetMessageDataModel.MessageData message = itr.next();
								json_body = message.getJsonBody();
								ContentValues contentvalues = new ContentValues();
								contentvalues.put(ChatDBConstants.FROM_USER_FIELD, message.getSender());
								contentvalues.put(ChatDBConstants.TO_USER_FIELD, pref.getUserDomain() + "-all");
								contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, message.getSenderName() + "#786#" + message.getSender());
								contentvalues.put(ChatDBConstants.MESSAGE_TYPE, 3);//3 - For all Bulletin Messages
								contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, pref.getUserDomain() + "-all");
								contentvalues.put(ChatDBConstants.SEEN_FIELD, "1");
								contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, (message.getText() != null) ? message.getText() : "");
								contentvalues.put(ChatDBConstants.MESSAGE_ID, message.getPacketId());
								contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD, UUID.randomUUID().toString());
//								System.out.println("[Creaton Date ] "+message.getCreatedDate());


								Calendar calender = Calendar.getInstance();
								calender.setTimeInMillis(convertTomilliseconds(message.getCreatedDate()));
								int old_msg_date = calender.get(Calendar.DATE);
								int new_msg_date = 0;

								String oppName = message.getSender();
								long millis = ChatDBWrapper.getInstance().firstMessageInDB(oppName);
								if(millis > 0){
									calender.setTimeInMillis(millis);
									new_msg_date = calender.get(Calendar.DATE);
								}

								System.out.println("new_msg_date = "+new_msg_date+", old_msg_date = "+old_msg_date);
								if (new_msg_date == 0 || old_msg_date < new_msg_date) {
									contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "1");
								} else {
									contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "0");
								}

								contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, convertTomilliseconds(message.getCreatedDate()));
								if(message.getType() != null){
									try {
										type = Integer.parseInt(message.getType());
									}catch(NumberFormatException nex){
										nex.printStackTrace();
										type = 0;
									}
								}
								contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getType());
								contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD, new Integer(1));
								media_url = message.getFileId();
								if(media_url != null && media_url.length() > 0)
									media_url = Constants.LIVE_DOMAIN + "/rtMediaServer/get/" + media_url;
								if (json_body != null && json_body.trim().length() > 0){
									System.out.println("json_body = " + json_body);
									JSONObject jsonobj = null;
									try {
										jsonobj = new JSONObject(json_body);
										if(jsonobj.has("caption") && jsonobj.getString("caption").toString().trim().length() > 0)
											caption = jsonobj.getString("caption").toString();
//                                        if((type == XMPPMessageType.atMeXmppMessageTypeImage.ordinal()
//                                                || type == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()
//                                                || type == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) && caption != null)
										contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, caption);

										if(jsonobj.has("fileName") && jsonobj.getString("fileName").toString().trim().length() > 0)
											contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, jsonobj.getString("fileName").toString());
										if(jsonobj.has("ext") && jsonobj.getString("ext").toString().trim().length() > 0) {
											extension = jsonobj.getString("ext").toString().trim();
											if(extension != null && extension.equals("caf"))
												media_url = media_url + ".amr";
											else
												media_url = media_url + "." + extension;
										}

										if(jsonobj.has("location") && jsonobj.getString("location").toString().trim().length() > 0)
											contentvalues.put(ChatDBConstants.MESSAGE_TYPE_LOCATION, jsonobj.getString("location").toString());
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
								contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, media_url);
								//Save USerID and SG in DB
								contentvalues.put(ChatDBConstants.USER_ID, SharedPrefManager.getInstance().getUserId());
								contentvalues.put(ChatDBConstants.USER_SG, SharedPrefManager.getInstance().getUserDomain());
								ChatDBWrapper.getInstance().insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO, contentvalues);
								media_url = caption = null;
								json_body = null;
								type = 0;
							}
						}
						if(next_url != null) {
							//Save this url is shared preferences for next hit
							pref.saveBulletinNextURL(next_url);
							next_url = null;
						}else{
							pref.saveBulletinNextURL("0");
						}

					} else {
//						String errorMessage = response.getMessage() != null ? response.getMessage() : "Please try later";
//						showDialog(errorMessage);
					}
				}

				@Override
				protected void common() {
//                    progressDialog.cancel();
				}

				@Override
				public void onFailure(retrofit2.Call call, Throwable t) {
					super.onFailure(call, t);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//--------------------------------
	public long convertTomilliseconds(Date date){
		long time_millis = 0;
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
		Date currentLocalTime = calendar.getTime();
//            System.out.println("GMT offset is "+currentLocalTime.getTime());
		DateFormat datef = new SimpleDateFormat("Z");
		String localTime = datef.format(currentLocalTime);
		System.out.println("GMT offset is "+localTime);

		time_millis = date.getTime();
		return time_millis;
	}

	public long convertTomilliseconds(String date)
	{
		long timeInMilliseconds = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US);
		try
		{
			Date mDate = sdf.parse(date);
			timeInMilliseconds = mDate.getTime();
//			System.out.println("Before : Date in millis => " + timeInMilliseconds);
//
//			Date resultdate = new Date(timeInMilliseconds);
//			System.out.println("Before : Date => "+sdf.format(resultdate));
//
//			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
//			Date currentLocalTime = calendar.getTime();
//
//			System.out.println("After GTM : Date in millis => " + (timeInMilliseconds + currentLocalTime.getTime()));
//
//			resultdate = new Date(timeInMilliseconds + currentLocalTime.getTime());
//			System.out.println("After GMT : Date => "+sdf.format(resultdate));

//			return timeInMilliseconds + convertCurrentTimeintoMillis();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return timeInMilliseconds + convertCurrentTimeintoMillis();
	}

	public long convertCurrentTimeintoMillis(){
		//+05:30
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
		String   timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
		String time = timeZone.substring(0, 3) + ":"+ timeZone.substring(3, 5);
		if(time == null)
			return 0;
		long millis = 0;
		String hr = null;
		String min = null;
		String[] values = null;

		if(time.startsWith("-")){
			time = time.substring(1);
			values = time.split(":");
			millis = -1 * ((Integer.parseInt(values[0]) * 60 * 60 * 1000) + (Integer.parseInt(values[1]) * 60 * 1000));

		}else{
			time = time.substring(1);
			values = time.split(":");
			millis = (Integer.parseInt(values[0]) * 60 * 60 * 1000) + (Integer.parseInt(values[1]) * 60 * 1000);
		}
//		System.out.println("+05:30 Millis = "+millis);
		return millis;
	}
	//------------------------------------------Back Up Code in Background------------------------
	String zip_file_path;
	String backedUpFileID = null;
	private void checkForBackUpAndUploadBackup(){
		boolean isWifi = SharedPrefManager.getInstance().isWifiBackup();
		//Check if wifi is true, then only wifi will be used other wise both.
		int backupOn = SharedPrefManager.getInstance().getBackupSchedule();
		System.out.println("<<Backup type  >> "+backupOn);
		if(!NetWork.isConnected(this)|| backupOn < 2 || backUpFound)
			return;
		System.out.println("[NetWork.NetWorkTypes - ] "+NetWork.getNetwork(this).ordinal());
		if(isWifi && NetWork.getNetwork(this).ordinal() != NetWork.NetWorkTypes.WIFI.ordinal())
			return;
		if(!iPrefManager.isGroupsLoaded())
			return;
		//Check Last Backup Date
		long time = SharedPrefManager.getInstance().getLastBackUpTime();
		long current = System.currentTimeMillis();
		long millis_in_day = 24 * 60 * 60 * 1000;
//		long millis_in_day = 60 * 1000;

		if(backupOn == 2)//Dialy
			millis_in_day = millis_in_day * 1;
		if(backupOn == 3)//Weekly
			millis_in_day = millis_in_day * 7;
		else if(backupOn == 4)//Monthly
			millis_in_day = millis_in_day * 30;

		//Check if Last backup time is >24 Hours then backup.
		if((current - time) > millis_in_day){
			System.out.println("<<More than backup time, so backing up......>>");
			//Do backup in background
			try {
				zip_file_path = ChatDBWrapper.getInstance().getAllMessagesForBackup();
				//Check if file exists then upload zip file to server
				File file = new File(zip_file_path);
				FileBody data = new FileBody(new File(zip_file_path));
				if (file.exists() && (int) data.getContentLength() > 0)
					new FileUploaderDownloader(this, this, false, true, notifyFileUploadHandler).execute(zip_file_path);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		else{
			System.out.println("<<Less than backup time, so no back up!!>>");
		}

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
				String url = Constants.SERVER_URL + "/tiger/rest/user/profile/update";
				Log.i(TAG, "UploadDataBackup :: doInBackground : URL - "+url);
				JSONObject jsonobj = new JSONObject();
				jsonobj.put("backupFileId", backedUpFileID);
				HttpPost httppost = new HttpPost(url);
				httppost = SuperChatApplication.addHeaderInfo(httppost,true);
				HttpClient httpclient = new DefaultHttpClient();
				httppost.setEntity(new StringEntity(jsonobj.toString()));
				HttpResponse response = httpclient.execute(httppost);
				int status = response.getStatusLine().getStatusCode();
				if (status == 200) {
					HttpEntity entity = response.getEntity();
					data = EntityUtils.toString(entity);
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
//				Toast.makeText(HomeScreen.this, "Data backed up successfully in background!", Toast.LENGTH_SHORT).show();
				//Update Time in Shared Preferences
				SharedPrefManager.getInstance().setLastBackUpTime(System.currentTimeMillis());
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
	public void onDrawerItemSelected(View view, int position) {
		displayView(position);
	}

	private void displayView(int position) {
		Fragment fragment = null;
		String title = getString(R.string.app_name);
		switch (position) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			default:
				break;
		}


	}
//------------------------- Clear Data to switch for another SG ------------------------------------------
	boolean isSwitchSG;
	public void switchSG(String sg){
		String sg_name = sg.substring(sg.indexOf("_") + 1);
		String current_username = DBWrapper.getInstance().getSGUserName(sg_name);
		drawerFragment.fragmentClose();
		updateUserData(sg);
		System.out.println("<< mobileNumber :: Switch :: "+SharedPrefManager.getInstance().getUserPhone());
		SharedPrefManager.getInstance().setProfileAdded(current_username, true);
		progressDialog = ProgressDialog.show(HomeScreen.this, "", "Loading. Please wait...", true);
		isSwitchSG = true;
		activateSG(sg_name);
	}
	public void cleanDataAndSwitchSG(String sg_name){
		try{
			//Clear All Shared Preferences Data
//			SharedPrefManager.getInstance().clearSharedPref();
			//Clear All Messages - Message Info Table
//			ChatDBWrapper.getInstance().clearMessageDB();
			//Clear All Contacts - Contacts Table
			DBWrapper.getInstance().clearAllDB();
		}catch(Exception ex){
			ex.toString();
		}
	}
//------------------------- Activate SG --------------------
    private void activateSG(final String sgname){
        try{
            Call call = objApi.getApi(this).markSGActive(sgname);
            call.enqueue(new RetrofitRetrofitCallback<MarkSGActive>(this) {
                @Override
                protected void onResponseVoidzResponse(Call call, Response response) {
                    System.out.println("Retrofit : onResponseVoidzResponse 1 - "+response.toString());

                }

                @Override
                protected void onResponseVoidzObject(Call call, MarkSGActive response) {
					if(progressDialog != null){
						progressDialog.dismiss();
						progressDialog = null;
					}
                    System.out.println("Retrofit : onResponseVoidzObject 2 - "+response.toString());
					if(Build.VERSION.SDK_INT >= 11)
						new SignInTaskOnServer(sgname).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					else
						new SignInTaskOnServer(sgname).execute();

                }

                @Override
                protected void common() {
					System.out.println("Retrofit : onResponseVoidzObject 3 - ");

                }
            });
        } catch(Exception e){
            objExceptione.printStackTrace(e);

        }
    }
//-------------------------------------------------------------------------
	public void updateSlidingDrawer(String text , String fileId) {
		drawerFragment.currentSGName.setText("" + text);
		setProfilePic(drawerFragment.displayPictureCurrent , fileId);
	}
//-------------------------------------------------------------------------
	private boolean setProfilePic(ImageView picView, String groupPicId) {
	//		System.out.println("groupPicId : "+groupPicId);
		String img_path = getThumbPath(groupPicId);
		picView.setImageResource(R.drawable.about_icon);
		if (groupPicId == null || (groupPicId != null && groupPicId.equals("")) || groupPicId.equals("clear") || groupPicId.contains("logofileid"))
			return false;
		if (img_path != null) {
			File file1 = new File(img_path);
	//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if (file1.exists()) {
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	//				picView.setImageURI(Uri.parse(img_path));
				setThumb((ImageView) picView, img_path, groupPicId);
				return true;
			} else {
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader((RoundedImageView) picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
				else
					new BitmapDownloader((RoundedImageView) picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			}
		} else {
			if (Build.VERSION.SDK_INT >= 11)
				new BitmapDownloader((RoundedImageView) picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
			else
				new BitmapDownloader((RoundedImageView) picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);

		}
		return false;
	}
	private String getThumbPath(String groupPicId) {
		if (groupPicId == null)
			groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
		if (groupPicId != null) {
			String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath() + File.separator + Constants.contentProfilePhoto + profilePicUrl;
			File contentFile = new File(filename);
			if (contentFile != null && contentFile.exists()) {
				return filename;
			}

		}
		return null;
	}
	private void setThumb(ImageView imageViewl, String path, String groupPicId) {
		BitmapFactory.Options bfo = new BitmapFactory.Options();
		bfo.inSampleSize = 2;
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeFile(path, bfo);
//		    bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
			bm = rotateImage(path, bm);
//		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
		} catch (Exception ex) {

		}
		if (bm != null) {
			imageViewl.setImageBitmap(bm);
//	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
		} else {
			try {
				imageViewl.setImageURI(Uri.parse(path));
			} catch (Exception e) {

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
		if (orientation != ExifInterface.ORIENTATION_NORMAL) {
			int width = bm.getWidth();
			int height = bm.getHeight();
			Matrix matrix = new Matrix();
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(270);
			}
			return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		}

		return bm;
	}

}
