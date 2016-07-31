package com.superchat.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.LoginResponseModel;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class SharedIDScreen extends Activity implements OnClickListener{//, OnMenuItemClickListener{
	public static final String TAG = "PublicGroupScreen";
	Cursor cursor;
//	RelativeLayout myChannelTabLayout;
//	RelativeLayout allChannelTabLayout;
//	LinearLayout myChannelTabLayout;
//	LinearLayout allChannelTabLayout;
	RelativeLayout createGroup;
	ImageView searchIcon;
//	TextView myChannelLabel, allChannelLabel;
//	View viewMyChannelLabel, viewAllChannelLabel;
	private static SharedIDAdapter adapter;
//	private EditText searchBoxView;
	private ImageView clearSearch;
	private boolean onForeground;
	ImageView xmppStatusView;
	private ChatService service;
	private XMPPConnection connection;
	private TabHost myTabHost;
//	public static boolean isAllChannelTab = false;
	public static boolean isFirstTime = false;
	Timer timer = null;
	TimerTask task = null;
	boolean isSearchOn = false;
	long fireTimer;
//	ImageView superGroupIcon;
//	TextView superGroupName;
	ListView listView = null;
//	public static ArrayList<LoginResponseModel.GroupDetail> HomeScreen.groupsData = new ArrayList<LoginResponseModel.GroupDetail>();
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			service = ((ChatService.MyBinder) binder).getService();
			Log.d("Service", "Connected");
			if (service != null) {
				connection = service.getconnection();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			connection = null;
			service = null;
		}
	};
	public void setPorfileListener(){
	}
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shared_id_screen);
//		View view = layoutinflater.inflate(R.layout.shared_id_screen, null);
//		searchBoxView = (EditText) view.findViewById(R.id.id_search_user);
//		myChannelTabLayout = (LinearLayout) view.findViewById(R.id.id_my_channel);
//		allChannelTabLayout = (LinearLayout) view.findViewById(R.id.id_all_channels);
//		superGroupIcon = (ImageView)findViewById(R.id.id_sg_icon);
//		superGroupName = (TextView)findViewById(R.id.id_sg_name_label);
		listView = (ListView)findViewById(R.id.list);
		
//		viewMyChannelLabel = (View)view.findViewById(R.id.view_contacts);
//		viewAllChannelLabel = (View)view.findViewById(R.id.view_otherapps);
//		myChannelLabel = (TextView)view.findViewById(R.id.text_contacts);
//		allChannelLabel = (TextView)view.findViewById(R.id.text_otherapps);
//		myChannelTabLayout.setOnClickListener(this);
//		allChannelTabLayout.setOnClickListener(this);
		
//		superGroupIcon.setOnClickListener(this);
//		superGroupName.setOnClickListener(this);
//		if(SharedPrefManager.getInstance().getAppMode().equals("VirginMode") && !HomeScreen.firstTimeAdmin)
//			isAllChannelTab = true;
//		if(!isAllChannelTab){
//			myChannelTabLayout.setBackgroundColor(0xffcde1f5);//Color.WHITE);
//			allChannelTabLayout.setBackgroundColor(Color.GRAY);
//		}else{
//			myChannelTabLayout.setBackgroundColor(Color.GRAY);
//			allChannelTabLayout.setBackgroundColor(0xffcde1f5);//Color.WHITE);
//		}
		
		createGroup = (RelativeLayout) findViewById(R.id.id_create_gp_layout);
		if(SharedPrefManager.getInstance().isDomainAdmin())
			createGroup.setVisibility(View.VISIBLE);
		else
			createGroup.setVisibility(View.GONE);
//		searchBoxView.setVisibility(View.GONE);
		((TextView)findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		((ImageView)findViewById(R.id.id_info)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showPopupDialog();
			}
		});
		createGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SuperChatApplication.context, CreateSharedIDActivity.class);
//				intent.putExtra(Constants.CHANNEL_CREATION, true);
				startActivity(intent);				
			}
		});
//		searchIcon = (ImageView)findViewById(R.id.id_search_icon);
//		searchIcon.setVisibility(View.VISIBLE);
//		searchIcon.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				searchBoxView.setText("");		
//				createGroup.setVisibility(View.GONE);
////				searchBoxView.setVisibility(View.VISIBLE);
//				clearSearch.setVisibility(View.VISIBLE);
//			}
//		});
//		clearSearch = (ImageView)findViewById(R.id.id_search_cross);
//		clearSearch.setVisibility(View.GONE);
//		clearSearch.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
////				if(searchBoxView.getText().toString().trim().length() > 0){
////					searchBoxView.setText("");	
////					 Timer timer=new Timer();
////			         long DELAY = 800; // milliseconds
////					 timer.cancel();
////			            timer = new Timer();
////			            if(isSearchOn)
////			            timer.schedule(
////			                new TimerTask() {
////			                    @Override
////			                    public void run() {
////			                    	Message msg = new Message();
////									Bundle data = new Bundle();
////									data.putString("SearchText", "");
////									msg.setData(data);
////									handler.sendMessage(msg); 
////			                    }
////			                }, 
////			                DELAY
////			            );
////				}else
//				{
////					searchBoxView.setVisibility(View.GONE);
//					clearSearch.setVisibility(View.GONE);
//					if(SharedPrefManager.getInstance().isDomainAdmin())
//						createGroup.setVisibility(View.VISIBLE);
//					else
//						createGroup.setVisibility(View.GONE);
//					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//				    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//				}
//			}
//		});
//		showAllSharedIDs();
//		view.findViewById(R.id.id_add_icon).setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				 Intent intent = new Intent(Intent.ACTION_INSERT,  ContactsContract.Contacts.CONTENT_URI);
//				 intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//				startActivity(intent);
//			}
//		});
//		superGroupName.setText(SharedPrefManager.getInstance().getUserDomain());
//		setSGProfilePic(superGroupIcon, SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID"));
//		return view;
	}
	
	private void showPopupDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.shared_is_popup));
		builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        // Do do my action here
		        dialog.dismiss();
		      //Need to send 
		        Intent intent = new Intent(SuperChatApplication.context, CreateSharedIDActivity.class);
				startActivity(intent);	
		    }

		});
		builder.setNegativeButton(getString(R.string.no_not_now), new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        // I do not need any action here you might
		        dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	private boolean setSGProfilePic(ImageView picView, String groupPicId){
		if(groupPicId == null)
			return false;
		String img_path = getImagePath(groupPicId);
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		picView.setImageResource(R.drawable.about_icon);
			if(groupPicId == null)
				return false;
		if (bitmap != null) {
			picView.setImageBitmap(bitmap);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			picView.setTag(filename);
			return true;
		}else if(img_path != null){
			File file1 = new File(img_path);
			if(file1.exists()){
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				setThumb((ImageView) picView,img_path,groupPicId);
				return true;
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
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			if(bundle !=null){
				String searchText = bundle.getString("SearchText", null);
				if(searchText != null){
//					getPublicSearch(searchText.length(),searchText);
				}
			}
			
		}
	};
//	public void getPublicSearch(int length,String searchText){
//			if(Build.VERSION.SDK_INT >= 11)
//				new GetSharedIDList(onForeground).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,searchText);
//			else
//				new GetSharedIDList(onForeground).execute(searchText);
//			fireTimer = 0;
//	}
	@Override
	public void onClick(View v) {
		if(v!=null)
		switch(v.getId()){
		case R.id.id_sg_icon:
		case R.id.id_sg_name_label:
			Intent intent = new Intent(this, SuperGroupProfileActivity.class);
			startActivity(intent);
			break;
		}

//		if(Build.VERSION.SDK_INT >= 11)
//			new GetSharedIDList(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		else
//			new GetSharedIDList(true).execute();
	}
	
	public void onResume(){
		super.onResume();
		bindService(new Intent(this, ChatService.class), mConnection,Context.BIND_AUTO_CREATE);
		onForeground = true;
//		refreshList();
		if(HomeScreen.firstTimeAdmin && SharedPrefManager.getInstance().isDomainAdmin()){
			if(!isFirstTimeDialogShowing)
				showDialog(getResources().getString(R.string.first_time_gp_creation_alert));
		}
//		if(superGroupName != null)
//			superGroupName.setText(SharedPrefManager.getInstance().getUserDomain());
//		if(superGroupIcon != null)
//			setSGProfilePic(superGroupIcon, SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID"));
		showAllSharedIDs();
	}
	public void onPause(){
		super.onPause();
		onForeground = false;
		 try {
			 unbindService(mConnection);
	        } catch (Exception e) {
	            // Just ignore that
	            Log.d("MessageHistoryScreen", "Unable to un bind");
	        }
	}
	private static int screenNumber;
	public void setPageNumber(int screenNumber){
		this.screenNumber = screenNumber;
	}
	public void onActivityCreated(Bundle bundle) {
		Intent intent = getIntent();
//		if(isAllChannelTab)
//			showAllContacts(1);
//		else
//			showAllContacts(0);
		
//		if(Build.VERSION.SDK_INT >= 11)
//			new OpenGroupTaskOnServer(isFirstTime).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		else
//			new OpenGroupTaskOnServer(isFirstTime).execute();
		isFirstTime = false;
		isSearchOn = false;
		
//		searchBoxView.addTextChangedListener(new TextWatcher() {
//			private Timer timer=new Timer();
//	        private final long DELAY = 800; // milliseconds
//			public void afterTextChanged(Editable editable) {
//				final String s1 = (new StringBuilder()).append(searchBoxView.getText().toString()).toString().trim();
//				final int i = s1.length();
//				if(i > 0)
//					isSearchOn = true;
////				if(!isAllChannelTab)
//				{
//					searchMyChannels(0,s1);
//				}
////				else{
////				  timer.cancel();
////		            timer = new Timer();
////		            if(isSearchOn)
////		            timer.schedule(
////		                new TimerTask() {
////		                    @Override
////		                    public void run() {
////		                    	Message msg = new Message();
////								Bundle data = new Bundle();
////								data.putString("SearchText", s1);
////								msg.setData(data);
////								handler.sendMessage(msg); 
////		                    }
////		                }, 
////		                DELAY
////		            );
////	            }
//				
//			}
//			public void beforeTextChanged(CharSequence charsequence, int i,
//					int j, int k) {
//			}
//
//			public void onTextChanged(CharSequence charsequence, int i, int j,
//					int k) {
//			}
//
//		});
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult in PublicGroupScreen are called.");
			if (resultCode == Activity.RESULT_OK)
				switch (requestCode) {
				case 105:
					
					break;
			}
	}
	public void showAllSharedIDs() {
		Activity fragmentactivity = this;
		ArrayList<LoginResponseModel.BroadcastGroupDetail> list = new ArrayList<LoginResponseModel.BroadcastGroupDetail>();
		list.addAll(HomeScreen.sharedIDData);
		Collections.sort(list);
		try{
			adapter = new SharedIDAdapter(this, fragmentactivity, R.layout.shared_id_item, list,Constants.GROUP_USER_CHAT_CREATE);
			if(listView != null && adapter != null)
				listView.setAdapter(adapter);
		}catch(Exception e){}
	}
	public static void refreshList(){
		try{
			if(adapter!=null){
				adapter.notifyDataSetChanged();
				adapter.notifyDataSetInvalidated();
			}
			
		} catch(Exception e){}
	}
//	public void restScreen(){
//		if(!isAllChannelTab){
//			if(viewAllChannelLabel != null){
//				viewAllChannelLabel.setVisibility(View.GONE);
//				viewMyChannelLabel.setVisibility(View.VISIBLE);
//			}
//			if(allChannelLabel != null)
//				allChannelLabel.setTextColor(getResources().getColor(R.color.darkest_gray));
//			if(myChannelLabel != null)
//				myChannelLabel.setTextColor(getResources().getColor(R.color.color_lite_blue));
//		}else{
//			if(viewMyChannelLabel != null){
//				viewMyChannelLabel.setVisibility(View.GONE);
//				viewAllChannelLabel.setVisibility(View.VISIBLE);
//			}
//			if(myChannelLabel != null)
//				myChannelLabel.setTextColor(getResources().getColor(R.color.darkest_gray));
//			if(allChannelLabel != null)
//				allChannelLabel.setTextColor(getResources().getColor(R.color.color_lite_blue));
//		}
//		if(isAllChannelTab)
//			showAllContacts(1);
//		else
//			showAllContacts(0);
//	}
//	public void searchMyChannels(int type,String searchTxt) {
//		FragmentActivity fragmentactivity = SharedIDScreen.this;
//		String as[] = { DatabaseConstants.CONTACT_NAMES_FIELD};
//		int ai[] = new int[1];
//		ai[0] = R.id.id_contact_name;
//		ArrayList<LoginResponseModel.GroupDetail> list = new ArrayList<LoginResponseModel.GroupDetail>();
//		for(LoginResponseModel.GroupDetail groups : HomeScreen.groupsData){
////			LoginResponseModel.GroupDetail info = new LoginResponseModel.GroupDetail();
//			if(type == 0 && !groups.memberType.equals("USER") && groups.displayName.toLowerCase().contains(searchTxt.toLowerCase()))
//				list.add(groups);
//			else if(type == 1)
//				list.add(groups);
//		}
//		try{
//		adapter = new SharedIDAdapter(this, fragmentactivity, R.layout.public_group_items, list,Constants.GROUP_USER_CHAT_CREATE);
//		ListView listView = getListView();
//		if(onForeground && listView!=null && adapter != null)
//			listView.setAdapter(adapter);
//		}catch(Exception e){}
////		updateCursor(null, null);
//	}
	public void updateDataWithUILocally(LoginResponseModel.BroadcastGroupDetail tmpGroup, boolean isJoinning) {
		try{
		if(service!=null && isJoinning && tmpGroup!=null){
			service.sendGroupPresence(tmpGroup.broadcastGroupName, 0);
		}
		}catch(Exception e){}
		if(isJoinning){
			SharedPrefManager.getInstance().saveGroupTypeAsPublic(tmpGroup.broadcastGroupName,true);
			SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.broadcastGroupName, tmpGroup.userName, SharedPrefManager.GROUP_OWNER_INFO, true);
			SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.broadcastGroupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.GROUP_ACTIVE_INFO,true);
			SharedPrefManager.getInstance().saveGroupInfo(tmpGroup.broadcastGroupName,SharedPrefManager.GROUP_ACTIVE_INFO,true);
			SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.broadcastGroupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.PUBLIC_CHANNEL,true);
			SharedPrefManager.getInstance().saveGroupOwnerName(tmpGroup.broadcastGroupName, tmpGroup.userName);
		}
		updateDataLocally(tmpGroup, isJoinning);
		Activity fragmentactivity = this;
		ArrayList<LoginResponseModel.BroadcastGroupDetail> list = new ArrayList<LoginResponseModel.BroadcastGroupDetail>();
		for(LoginResponseModel.BroadcastGroupDetail groups : HomeScreen.sharedIDData){
				list.add(groups);
		}
//		Collections.sort(list);
		try{
			adapter = new SharedIDAdapter(this, fragmentactivity, R.layout.public_group_items, list,Constants.GROUP_USER_CHAT_CREATE);
//			ListView listView = getListView();
//			if(onForeground && listView!=null && adapter != null)
//				listView.setAdapter(adapter);
			
			}catch(Exception e){}
//		updateCursor(null, null);
	}
	public static void updateDataLocally(LoginResponseModel.BroadcastGroupDetail broadcastGroupName, boolean isJoinning){
		LoginResponseModel.BroadcastGroupDetail tmpGroup = null;
		for(LoginResponseModel.BroadcastGroupDetail shared_id : HomeScreen.sharedIDData){
			if(shared_id.broadcastGroupName.equals(broadcastGroupName)){
				tmpGroup = shared_id;
				break;
				}
		}
		if(tmpGroup==null)
			return;
		if(HomeScreen.sharedIDData.contains(tmpGroup)){
			HomeScreen.sharedIDData.remove(tmpGroup);
			if(!isJoinning){
//				try{
//					if(tmpGroup.numberOfMembers!=null && !tmpGroup.numberOfMembers.equals("")){
//						int number = Integer.parseInt(tmpGroup.numberOfMembers);
//						number--;
//						tmpGroup.numberOfMembers = String.valueOf(number);
//					}
//				}catch(NumberFormatException nfx){
//					
//				}
//				tmpGroup.memberType = "USER";
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.broadcastGroupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.GROUP_ACTIVE_INFO,false);
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.broadcastGroupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.PUBLIC_CHANNEL,false);
			}else{
//				try{
//					if(tmpGroup.numberOfMembers!=null && !tmpGroup.numberOfMembers.equals("")){
//						int number = Integer.parseInt(tmpGroup.numberOfMembers);
//						number++;
//						tmpGroup.numberOfMembers = String.valueOf(number);
//					}
//				}catch(NumberFormatException nfx){
//					
//				}
//				tmpGroup.memberType = "MEMBER";
				SharedPrefManager.getInstance().saveGroupName(tmpGroup.broadcastGroupName, tmpGroup.displayName);
				SharedPrefManager.getInstance().saveGroupDisplayName(tmpGroup.broadcastGroupName, tmpGroup.displayName);
				SharedPrefManager.getInstance().saveGroupTypeAsPublic(tmpGroup.broadcastGroupName,true);
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.broadcastGroupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.GROUP_ACTIVE_INFO,true);
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.broadcastGroupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.PUBLIC_CHANNEL,true);
				}
			HomeScreen.sharedIDData.add(tmpGroup);
			try{
				if(adapter!=null){
					adapter.clear();
					for(LoginResponseModel.BroadcastGroupDetail group : HomeScreen.sharedIDData){
					    adapter.add(group);
					} 
					adapter.notifyDataSetChanged();
				}
			}catch(Exception e){}
		}
	}
	 
	public static void updateDataLocally(LoginResponseModel.GroupDetail tmpGroup,boolean isJoinning){
		if(HomeScreen.groupsData.contains(tmpGroup)){
				HomeScreen.groupsData.remove(tmpGroup);
			if(!isJoinning){
				try{
					if(tmpGroup.numberOfMembers!=null && !tmpGroup.numberOfMembers.equals("")){
						int number = Integer.parseInt(tmpGroup.numberOfMembers);
						number--;
						tmpGroup.numberOfMembers = String.valueOf(number);
						SharedPrefManager.getInstance().saveGroupMemberCount(tmpGroup.groupName, tmpGroup.numberOfMembers);
					}
				}catch(NumberFormatException nfx){
					
				}
				tmpGroup.memberType = "USER";
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.GROUP_ACTIVE_INFO,false);
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.PUBLIC_CHANNEL,false);
			}else{
				try{
					if(tmpGroup.numberOfMembers!=null && !tmpGroup.numberOfMembers.equals("")){
						int number = Integer.parseInt(tmpGroup.numberOfMembers);
						number++;
						tmpGroup.numberOfMembers = String.valueOf(number);
					}
				}catch(NumberFormatException nfx){
					
				}
				tmpGroup.memberType = "MEMBER";
				SharedPrefManager.getInstance().saveGroupMemberCount(tmpGroup.groupName, tmpGroup.numberOfMembers);
				SharedPrefManager.getInstance().saveGroupName(tmpGroup.groupName, tmpGroup.displayName);
				SharedPrefManager.getInstance().saveGroupDisplayName(tmpGroup.groupName, tmpGroup.displayName);
				SharedPrefManager.getInstance().saveGroupTypeAsPublic(tmpGroup.groupName,true);
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.GROUP_ACTIVE_INFO,true);
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.PUBLIC_CHANNEL,true);
			}
			HomeScreen.groupsData.add(tmpGroup);
		}
	}

//	private class GetSharedIDList extends AsyncTask<String, String, String> {
//		LoginModel loginForm;
//		ProgressDialog progressDialog = null;
//		SharedPrefManager sharedPrefManager;
//		boolean isLoading;
//		public GetSharedIDList(boolean isLoading){
//			sharedPrefManager = SharedPrefManager.getInstance();
//			loginForm = new LoginModel();
//			loginForm.setUserName(sharedPrefManager.getUserName());
//			loginForm.setPassword(sharedPrefManager.getUserPassword());
//			loginForm.setToken(sharedPrefManager.getDeviceToken());
//			this.isLoading = isLoading;
//		}
//		@Override
//		protected void onPreExecute() {
//			if(isLoading && (screenNumber==1|| screenNumber==-1))
//				progressDialog = ProgressDialog.show(SharedIDScreen.this, "", "Fetching data. Please wait...", true);
//			super.onPreExecute();
//		}
//		@Override
//		protected String doInBackground(String... params) {
//			// TODO Auto-generated method stub
////			String JSONstring = new Gson().toJson(loginForm);
//			String searchText = "";
//			if(params!=null && params.length>0){
//				String query = params[0];
//				try {
//					 query = URLEncoder.encode(query, "utf-8");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				searchText = "text="+query+"&";
//			}
//		    DefaultHttpClient client1 = new DefaultHttpClient();
//		    
////			Log.d("HomeScreen", "serverUpdateCreateGroupInfo request:"+JSONstring);  p5domain
//			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/group/search?"+searchText+"limit=1000");
////	         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
//			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
//			 HttpResponse response = null;
//			 
//	         try {
////				httpPost.setEntity(new StringEntity(JSONstring));
//				 try {
//					 response = client1.execute(httpPost);
//					 final int statusCode=response.getStatusLine().getStatusCode();
//					 if (statusCode == HttpStatus.SC_OK){ //new1
//						 HttpEntity entity = response.getEntity();
////						    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
//						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
//						    String line = "";
//				            String str = "";
//				            while ((line = rd.readLine()) != null) {
//				            	
//				            	str+=line;
//				            }
//				            if(str!=null &&!str.equals("")){
//				            	Gson gson = new GsonBuilder().create();
//				            	LoginResponseModel loginObj = gson.fromJson(str,LoginResponseModel.class);
//								if (loginObj != null) {
//									if(loginObj.directoryGroupSet!=null){
//										HomeScreen.groupsData.clear();
//										for (GroupDetail groupDetail : loginObj.directoryGroupSet) {
//											if(!groupDetail.memberType.equals("USER"))
//												SharedPrefManager.getInstance().saveUserGroupInfo(groupDetail.groupName, SharedPrefManager.getInstance().getUserName(), SharedPrefManager.PUBLIC_CHANNEL, true);
//											SharedPrefManager.getInstance().saveGroupMemberCount(groupDetail.groupName, groupDetail.numberOfMembers);
//											HomeScreen.groupsData.add(groupDetail);
//											Log.d(TAG, "counter check Discover response : "+ groupDetail.type+""+groupDetail.displayName+" , "+groupDetail.numberOfMembers);
//										}
//									}
//					            }
//									
//									
//				            
//				            }
//					 }
//				} catch (ClientProtocolException e) {
//					Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
//				} catch (IOException e) {
//					Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
//				}
//				 
//			} catch(Exception e){
//				Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
//				e.printStackTrace();
//			}
//		
//		
//			return null;
//		}
//		@Override
//		protected void onPostExecute(String str) {
//			if (progressDialog != null) {
//				progressDialog.dismiss();
//				progressDialog = null;
//			}
//			if (str!=null && str.contains("error")){
//				Gson gson = new GsonBuilder().create();
//				ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
//				if (errorModel != null) {
//					if (errorModel.citrusErrors != null
//							&& !errorModel.citrusErrors.isEmpty()) {
//						ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
//						if(citrusError!=null && citrusError.code.equals("20019") ){
//							SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
////							iPrefManager.saveUserDomain(domainNameView.getText().toString());
//							iPrefManager.saveUserId(errorModel.userId);
//							iPrefManager.setAppMode("VirginMode");
////							iPrefManager.saveUserPhone(regObj.iMobileNumber);
//	//						iPrefManager.saveUserPassword(regObj.getPassword());
//							iPrefManager.saveUserLogedOut(false);
//							iPrefManager.setMobileRegistered(iPrefManager.getUserPhone(), true);
//							showDialog(citrusError.message);
//						}else
//							showDialog(citrusError.message);
//					} else if (errorModel.message != null)
//						showDialog(errorModel.message);
//				} else
//					showDialog("Please try again later.");
//			}else{
////				if(isAllChannelTab)
////					showAllContacts(1);
////				else
//					showAllContacts();
//			}
//			super.onPostExecute(str);
//		}
//	}
//	public void showPopup(View v){
//		 PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
//		 popup.setOnMenuItemClickListener(this);
//		 popup.getMenu().add(0,0,0,getResources().getString(R.string.create_group));
//		 popup.getMenu().add(0,1,0,getResources().getString(R.string.settings));
//		 popup.show();
//	}
	boolean isFirstTimeDialogShowing;
	public void showDialog(String s, boolean custom) {
		final Dialog bteldialog = new Dialog(this);
		isFirstTimeDialogShowing = true;
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog_gray);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				HomeScreen.firstTimeAdmin = false;
				return false;
			}
		});
		bteldialog.show();
	}
	public void showDialog(String s) {
		final Dialog bteldialog = new Dialog(this);
		isFirstTimeDialogShowing = true;
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				HomeScreen.firstTimeAdmin = false;
				return false;
			}
		});
		bteldialog.show();
	}

}
