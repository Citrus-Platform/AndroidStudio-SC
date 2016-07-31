package com.superchat.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.ErrorModel;
import com.superchat.model.LoginModel;
import com.superchat.model.LoginResponseModel;
import com.superchat.model.LoginResponseModel.GroupDetail;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
//import android.app.ListFragment;
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
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

public class PublicGroupScreen extends ListFragment implements OnClickListener{//, OnMenuItemClickListener{
	public static final String TAG = "PublicGroupScreen";
	Cursor cursor;
//	RelativeLayout myChannelTabLayout;
//	RelativeLayout allChannelTabLayout;
	LinearLayout myChannelTabLayout;
	LinearLayout allChannelTabLayout;
	RelativeLayout createGroup;
	ImageView searchIcon;
	EditText searchEditText;
	TextView myChannelLabel, allChannelLabel;
	ImageView createGroupIcon;
	View viewMyChannelLabel, viewAllChannelLabel;
	private static PublicGroupAdapter adapter;
	private EditText searchBoxView;
	private ImageView clearSearch;
	private boolean onForeground;
	ImageView xmppStatusView;
	private static ChatService service;
	private XMPPConnection connection;
	private TabHost myTabHost;
	public static boolean isAllChannelTab = false;
	public static boolean isFirstTime = false;
	Timer timer = null;
	TimerTask task = null;
	boolean isSearchOn = false;
	long fireTimer;
	ImageView superGroupIcon;
	TextView superGroupName;
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
	public View onCreateView(LayoutInflater layoutinflater,
			ViewGroup viewgroup, Bundle bundle) {
		View view = layoutinflater.inflate(R.layout.public_group_layout, null);
		searchBoxView = (EditText) view.findViewById(R.id.id_search_user);
		myChannelTabLayout = (LinearLayout) view.findViewById(R.id.id_my_channel);
		allChannelTabLayout = (LinearLayout) view.findViewById(R.id.id_all_channels);
		superGroupIcon = (ImageView)view.findViewById(R.id.id_sg_icon);
		superGroupName = (TextView)view.findViewById(R.id.id_sg_name_label);
		
		viewMyChannelLabel = (View)view.findViewById(R.id.view_contacts);
		viewAllChannelLabel = (View)view.findViewById(R.id.view_otherapps);
		myChannelLabel = (TextView)view.findViewById(R.id.text_contacts);
		allChannelLabel = (TextView)view.findViewById(R.id.text_otherapps);
		createGroupIcon = (ImageView)view.findViewById(R.id.id_create_group_icon);
		myChannelTabLayout.setOnClickListener(this);
		allChannelTabLayout.setOnClickListener(this);
		superGroupIcon.setOnClickListener(this);
		superGroupName.setOnClickListener(this);
		if(SharedPrefManager.getInstance().getAppMode() != null && 
				SharedPrefManager.getInstance().getAppMode().equals("VirginMode") && !HomeScreen.firstTimeAdmin)
			isAllChannelTab = true;
		
		if(SharedPrefManager.getInstance().isGroupsLoaded()){
			allChannelTabLayout.performClick();
		}
//		if(!isAllChannelTab){
//			myChannelTabLayout.setBackgroundColor(0xffcde1f5);//Color.WHITE);
//			allChannelTabLayout.setBackgroundColor(Color.GRAY);
//		}else{
//			myChannelTabLayout.setBackgroundColor(Color.GRAY);
//			allChannelTabLayout.setBackgroundColor(0xffcde1f5);//Color.WHITE);
//		}
		
		createGroup = (RelativeLayout) view.findViewById(R.id.id_create_gp_layout);
//		if(SharedPrefManager.getInstance().isDomainAdmin())
			createGroupIcon.setVisibility(View.VISIBLE);
//		else
//			createGroupIcon.setVisibility(View.GONE);
		searchBoxView.setVisibility(View.GONE);
		createGroupIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SuperChatApplication.context, CreateGroupScreen.class);
				intent.putExtra(Constants.CHANNEL_CREATION, true);
				startActivity(intent);				
			}
		});
		searchIcon = (ImageView)view.findViewById(R.id.id_search_icon);
		
		searchEditText = (EditText)view.findViewById(R.id.id_search_field);
		
		searchIcon.setVisibility(View.VISIBLE);
		searchIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				searchBoxView.setText("");		
//				createGroup.setVisibility(View.GONE);
				searchEditText.setVisibility(View.VISIBLE);
				clearSearch.setVisibility(View.VISIBLE);
				searchIcon.setVisibility(View.GONE);
				superGroupIcon.setVisibility(View.GONE);
				superGroupName.setVisibility(View.GONE);
				 InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                 imm.showSoftInput(searchBoxView, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		clearSearch = (ImageView)view.findViewById(R.id.id_back_arrow);
		clearSearch.setVisibility(View.GONE);
		clearSearch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(searchBoxView.getText().toString().trim().length() > 0){
					searchBoxView.setText("");	
					 Timer timer=new Timer();
			         long DELAY = 800; // milliseconds
					 timer.cancel();
			            timer = new Timer();
			            if(isSearchOn)
			            timer.schedule(
			                new TimerTask() {
			                    @Override
			                    public void run() {
			                    	Message msg = new Message();
									Bundle data = new Bundle();
									data.putString("SearchText", "");
									msg.setData(data);
									handler.sendMessage(msg); 
			                    }
			                }, 
			                DELAY
			            );
				}else{
					searchEditText.setText("");
					searchEditText.setVisibility(View.GONE);
					searchIcon.setVisibility(View.VISIBLE);
					superGroupIcon.setVisibility(View.VISIBLE);
					superGroupName.setVisibility(View.VISIBLE);
					
					searchBoxView.setVisibility(View.GONE);
					clearSearch.setVisibility(View.GONE);
//					if(SharedPrefManager.getInstance().isDomainAdmin())
//						createGroup.setVisibility(View.VISIBLE);
//					else
//						createGroup.setVisibility(View.GONE);
					InputMethodManager imm = (InputMethodManager) getActivity().getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
				    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
		});
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
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			if(bundle !=null){
				String searchText = bundle.getString("SearchText", null);
				if(searchText!=null){
					getPublicSearch(searchText.length(),searchText);
				}
			}
			
		}
	};
	public void getPublicSearch(int length,String searchText){
//		if (length >= 1) {
			if(Build.VERSION.SDK_INT >= 11)
				new OpenGroupTaskOnServer(onForeground).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,searchText);
			else
				new OpenGroupTaskOnServer(onForeground).execute(searchText);
			fireTimer = 0;
//		}
	}
	@Override
	public void onClick(View v) {
		if(v!=null)
		switch(v.getId()){
		case R.id.id_sg_icon:
		case R.id.id_sg_name_label:
			Intent intent = new Intent(getActivity(), SuperGroupProfileActivity.class);
			startActivity(intent);
			break;
		case R.id.id_my_channel:
			isAllChannelTab = false;
			break;
		case R.id.id_all_channels:
			isAllChannelTab = true;
			break;
		}
		
		if(!isAllChannelTab){
//			myChannelTabLayout.setBackgroundColor(0xffcde1f5);//Color.WHITE);
//			allChannelTabLayout.setBackgroundColor(Color.GRAY);
			viewAllChannelLabel.setVisibility(View.GONE);
			viewMyChannelLabel.setVisibility(View.VISIBLE);
			allChannelLabel.setTextColor(getResources().getColor(R.color.darkest_gray));
			myChannelLabel.setTextColor(getResources().getColor(R.color.color_lite_blue));
		}else{
//			myChannelTabLayout.setBackgroundColor(Color.GRAY);
//			allChannelTabLayout.setBackgroundColor(0xffcde1f5);//Color.WHITE);
			viewMyChannelLabel.setVisibility(View.GONE);
			viewAllChannelLabel.setVisibility(View.VISIBLE);
			myChannelLabel.setTextColor(getResources().getColor(R.color.darkest_gray));
			allChannelLabel.setTextColor(getResources().getColor(R.color.color_lite_blue));
		}
		if(isAllChannelTab)
			showAllContacts(1);
		else
			showAllContacts(0);
		if(isAllChannelTab){
		if(Build.VERSION.SDK_INT >= 11)
			new OpenGroupTaskOnServer(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			new OpenGroupTaskOnServer(true).execute();
		}
	}
	
	public void onResume(){
		super.onResume();
		getActivity().bindService(new Intent(getActivity(), ChatService.class), mConnection,Context.BIND_AUTO_CREATE);
		onForeground = true;
		refreshList();
//		if(HomeScreen.refreshContactList){
//			updateCursor(null, null);
//			HomeScreen.refreshContactList = false;
//		}
		if(HomeScreen.firstTimeAdmin && SharedPrefManager.getInstance().isDomainAdmin()){
			isAllChannelTab = false;
			if(!isFirstTimeDialogShowing)
				showDialog(getResources().getString(R.string.first_time_gp_creation_alert));
		}
		if(superGroupName != null)
			superGroupName.setText(SharedPrefManager.getInstance().getUserDomain());
		if(superGroupIcon != null)
			setSGProfilePic(superGroupIcon, SharedPrefManager.getInstance().getSGFileId("SG_FILE_ID"));
	}
	public void onPause(){
		super.onPause();
		onForeground = false;
		 try {
			 getActivity().unbindService(mConnection);
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
		Intent intent = getActivity().getIntent();
		if(isAllChannelTab)
			showAllContacts(1);
		else
			showAllContacts(0);
		
//		if(Build.VERSION.SDK_INT >= 11)
//			new OpenGroupTaskOnServer(isFirstTime).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		else
//			new OpenGroupTaskOnServer(isFirstTime).execute();
		isFirstTime = false;
		isSearchOn = false;
		
		searchEditText.addTextChangedListener(new TextWatcher() {
			private Timer timer=new Timer();
	        private final long DELAY = 800; // milliseconds
			public void afterTextChanged(Editable editable) {
				final String s1 = (new StringBuilder()).append(searchEditText.getText().toString()).toString().trim();
				final int i = s1.length();
				if(i > 0)
					isSearchOn = true;
				if(!isAllChannelTab){
					searchMyChannels(0,s1);
				}else{
				  timer.cancel();
		            timer = new Timer();
		            if(isSearchOn)
		            timer.schedule(
		                new TimerTask() {
		                    @Override
		                    public void run() {
		                    	Message msg = new Message();
								Bundle data = new Bundle();
								data.putString("SearchText", s1);
								msg.setData(data);
								handler.sendMessage(msg); 
		                    }
		                }, 
		                DELAY
		            );
	            }
				
			}
			public void beforeTextChanged(CharSequence charsequence, int i,
					int j, int k) {
			}

			public void onTextChanged(CharSequence charsequence, int i, int j,
					int k) {
			}

		});
		
		super.onActivityCreated(bundle);
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
	public void showAllContacts(int type) {
		FragmentActivity fragmentactivity = getActivity();
		String as[] = { DatabaseConstants.CONTACT_NAMES_FIELD};
		int ai[] = new int[1];
		ai[0] = R.id.id_contact_name;
		ArrayList<LoginResponseModel.GroupDetail> list = new ArrayList<LoginResponseModel.GroupDetail>();
		for(LoginResponseModel.GroupDetail groups : HomeScreen.groupsData){
//			LoginResponseModel.GroupDetail info = new LoginResponseModel.GroupDetail();
			if(type == 0 && !groups.memberType.equals("USER")){
				list.add(groups);
			}else if(type == 1)
				list.add(groups);
		}
		if(type == 0)
			for (String group : SharedPrefManager.getInstance().getGroupNamesArray()) {
				if(group==null || group.equals(""))
					continue;
				if(SharedPrefManager.getInstance().isPublicGroup(group) || !SharedPrefManager.getInstance().isGroupMemberActive(group, SharedPrefManager.getInstance().getUserName()))
					continue;
				LoginResponseModel.GroupDetail groups = new LoginResponseModel.GroupDetail();
				groups.groupName = group;
				groups.memberType = "MEMBER";
				groups.displayName = SharedPrefManager.getInstance().getGroupDisplayName(group);
				groups.description = SharedPrefManager.getInstance().getUserStatusMessage(group);
				groups.numberOfMembers = SharedPrefManager.getInstance().getGroupMemberCount(group);
				if(groups.numberOfMembers!=null && groups.numberOfMembers.equals(""))
					groups.numberOfMembers = "1";
				if(list != null && !isGroupAddedInList(list, groups.groupName))
					list.add(groups);
			}
		Collections.sort(list);
		ListView listView = null;
		try{
			adapter = new PublicGroupAdapter(this, fragmentactivity,
					R.layout.public_group_items, list,Constants.GROUP_USER_CHAT_CREATE);
			listView = getListView();
			if(listView!=null && adapter!=null)
				listView.setAdapter(adapter);
		}catch(Exception e){}
		
//		updateCursor(null, null);
	}
	private boolean isGroupAddedInList(ArrayList<LoginResponseModel.GroupDetail> list, String group){
		try{
			for (LoginResponseModel.GroupDetail added_group : list){
				if(added_group.groupName.equals(group))
					return true;
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return false;
	}
	public static void refreshList(){
		try{
			if(adapter!=null){
				adapter.notifyDataSetChanged();
				adapter.notifyDataSetInvalidated();
			}
			
		} catch(Exception e){}
	}
	public void restScreen(){
		if(!isAllChannelTab){
			if(viewAllChannelLabel != null){
				viewAllChannelLabel.setVisibility(View.GONE);
				viewMyChannelLabel.setVisibility(View.VISIBLE);
			}
			if(allChannelLabel != null)
				allChannelLabel.setTextColor(getResources().getColor(R.color.darkest_gray));
			if(myChannelLabel != null)
				myChannelLabel.setTextColor(getResources().getColor(R.color.color_lite_blue));
		}else{
			if(viewMyChannelLabel != null){
				viewMyChannelLabel.setVisibility(View.GONE);
				viewAllChannelLabel.setVisibility(View.VISIBLE);
			}
			if(myChannelLabel != null)
				myChannelLabel.setTextColor(getResources().getColor(R.color.darkest_gray));
			if(allChannelLabel != null)
				allChannelLabel.setTextColor(getResources().getColor(R.color.color_lite_blue));
		}
		if(isAllChannelTab)
			showAllContacts(1);
		else
			showAllContacts(0);
	}
	public void searchMyChannels(int type,String searchTxt) {
		FragmentActivity fragmentactivity = getActivity();
		String as[] = { DatabaseConstants.CONTACT_NAMES_FIELD};
		int ai[] = new int[1];
		ai[0] = R.id.id_contact_name;
		ArrayList<LoginResponseModel.GroupDetail> list = new ArrayList<LoginResponseModel.GroupDetail>();
		for(LoginResponseModel.GroupDetail groups : HomeScreen.groupsData){
//			LoginResponseModel.GroupDetail info = new LoginResponseModel.GroupDetail();
			if(type == 0 && !groups.memberType.equals("USER") && groups.displayName.toLowerCase().contains(searchTxt.toLowerCase()))
				list.add(groups);
			else if(type == 1)
				list.add(groups);
		}
		try{
		adapter = new PublicGroupAdapter(this, fragmentactivity,
				R.layout.public_group_items, list,Constants.GROUP_USER_CHAT_CREATE);
		ListView listView = getListView();
		if(onForeground && listView!=null && adapter != null)
			listView.setAdapter(adapter);
		}catch(Exception e){}
//		updateCursor(null, null);
	}
	public void updateDataWithUILocally(LoginResponseModel.GroupDetail tmpGroup,boolean isJoinning) {
		try{
		if(service!=null && isJoinning && tmpGroup!=null){
			service.sendGroupPresence(tmpGroup.groupName,0);
		}
		}catch(Exception e){}
		if(isJoinning){
			SharedPrefManager.getInstance().saveGroupTypeAsPublic(tmpGroup.groupName,true);
			SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName, tmpGroup.userName, SharedPrefManager.GROUP_OWNER_INFO, true);
			SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.GROUP_ACTIVE_INFO,true);
			SharedPrefManager.getInstance().saveGroupInfo(tmpGroup.groupName,SharedPrefManager.GROUP_ACTIVE_INFO,true);
			SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.PUBLIC_CHANNEL,true);
			SharedPrefManager.getInstance().saveGroupOwnerName(tmpGroup.groupName, tmpGroup.userName);
		}
		updateDataLocally(tmpGroup,isJoinning);
		FragmentActivity fragmentactivity = getActivity();
		ArrayList<LoginResponseModel.GroupDetail> list = new ArrayList<LoginResponseModel.GroupDetail>();
		for(LoginResponseModel.GroupDetail groups : HomeScreen.groupsData){
			
//			LoginResponseModel.GroupDetail info = new LoginResponseModel.GroupDetail();
			if(!isAllChannelTab && !groups.memberType.equals("USER"))
				list.add(groups);
			else if(isAllChannelTab) //  && groups.memberType.equals("USER")
				list.add(groups);
		}
		Collections.sort(list);
		try{
			adapter = new PublicGroupAdapter(this, fragmentactivity,
					R.layout.public_group_items, list,Constants.GROUP_USER_CHAT_CREATE);
			ListView listView = getListView();
			if(onForeground && listView!=null && adapter != null)
				listView.setAdapter(adapter);
			
			}catch(Exception e){}
//		updateCursor(null, null);
	}
	public static void updateDataLocally(String groupName,boolean isJoinning){
		LoginResponseModel.GroupDetail tmpGroup = null;
		for(LoginResponseModel.GroupDetail group : HomeScreen.groupsData){
			if(group.groupName.equals(groupName)){
				tmpGroup = group;
				break;
				}
		}
		if(tmpGroup==null)
			return;
		try{
			if(service != null && isJoinning && tmpGroup != null){
				service.sendGroupPresence(tmpGroup.groupName,0);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(HomeScreen.groupsData.contains(tmpGroup)){
			HomeScreen.groupsData.remove(tmpGroup);
			if(!isJoinning){
				try{
					if(tmpGroup.numberOfMembers!=null && !tmpGroup.numberOfMembers.equals("")){
						int number = Integer.parseInt(tmpGroup.numberOfMembers);
						number--;
						tmpGroup.numberOfMembers = String.valueOf(number);
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
				SharedPrefManager.getInstance().saveGroupName(tmpGroup.groupName, tmpGroup.displayName);
				SharedPrefManager.getInstance().saveGroupDisplayName(tmpGroup.groupName, tmpGroup.displayName);
				SharedPrefManager.getInstance().saveGroupTypeAsPublic(tmpGroup.groupName,true);
				if(isJoinning){
					SharedPrefManager.getInstance().saveGroupInfo(tmpGroup.groupName,SharedPrefManager.GROUP_ACTIVE_INFO,true);
				}
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.GROUP_ACTIVE_INFO,true);
				SharedPrefManager.getInstance().saveUserGroupInfo(tmpGroup.groupName,SharedPrefManager.getInstance().getUserName(),SharedPrefManager.PUBLIC_CHANNEL,true);
				}
			HomeScreen.groupsData.add(tmpGroup);
			
//			FragmentActivity fragmentactivity = getActivity();
//			ArrayList<LoginResponseModel.GroupDetail> list = new ArrayList<LoginResponseModel.GroupDetail>();
//			for(LoginResponseModel.GroupDetail groups : HomeScreen.groupsData){
//				
////				LoginResponseModel.GroupDetail info = new LoginResponseModel.GroupDetail();
//				if(!isAllChannelTab && !groups.memberType.equals("USER"))
//					list.add(groups);
//				else if(isAllChannelTab)
//					list.add(groups);
//			}
//			
//			adapter = new PublicGroupAdapter(this,fragmentactivity,
//					R.layout.public_group_items, list,Constants.GROUP_USER_CHAT_CREATE);
//			getListView().setAdapter(adapter);
			try{
			if(adapter!=null){
			adapter.clear();
			for(LoginResponseModel.GroupDetail group : HomeScreen.groupsData){
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
	private void updateCursor(String s, String as[]) {
//		Log.i(TAG, "Updating cursor");
////		cursor = DBWrapper.getInstance().query(DatabaseConstants.TABLE_NAME_CONTACT_NAMES, null, s, as,
////				DatabaseConstants.VOPIUM_FIELD+" DESC, "+DatabaseConstants.CONTACT_NAMES_FIELD +" COLLATE NOCASE");
//		if(s==null){
//			s = DatabaseConstants.VOPIUM_FIELD + "!=?";
//			as = (new String[] { "2" });
//		}
//		cursor = DBWrapper.getInstance().query(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, null, s, as,
//				DatabaseConstants.VOPIUM_FIELD+" ASC, "+DatabaseConstants.CONTACT_NAMES_FIELD +" COLLATE NOCASE");
//		if (cursor != null && adapter != null)
//		{
////			adapter.changeCursor(cursor);
//			adapter.notifyDataSetChanged();
//		}
	}
//	@Override
//	public void notifyConnectionChange() {
//		if(onForeground){
//			((HomeScreen)getActivity()).runOnUiThread(new Runnable() {
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
//		}
//		
//	}
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
			loginForm.setToken(sharedPrefManager.getDeviceToken());
			this.isLoading = isLoading;
		}
		@Override
		protected void onPreExecute() {
			if(isLoading && (screenNumber==1|| screenNumber==-1))
				progressDialog = ProgressDialog.show(getActivity(), "", "Fetching data. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
//			String JSONstring = new Gson().toJson(loginForm);
			String searchText = "";
			if(params!=null && params.length>0){
				String query = params[0];
				try {
					 query = URLEncoder.encode(query, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				searchText = "text="+query+"&";
			}
		    DefaultHttpClient client1 = new DefaultHttpClient();
		    
//			Log.d("HomeScreen", "serverUpdateCreateGroupInfo request:"+JSONstring);  p5domain
			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/group/search?"+searchText+"limit=1000");
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
				            	Gson gson = new GsonBuilder().create();
				            	LoginResponseModel loginObj = gson.fromJson(str,LoginResponseModel.class);
								if (loginObj != null) {
									if(loginObj.directoryGroupSet!=null){
										HomeScreen.groupsData.clear();
										for (GroupDetail groupDetail : loginObj.directoryGroupSet) {
											if(!groupDetail.memberType.equals("USER"))
												SharedPrefManager.getInstance().saveUserGroupInfo(groupDetail.groupName, SharedPrefManager.getInstance().getUserName(), SharedPrefManager.PUBLIC_CHANNEL, true);
											SharedPrefManager.getInstance().saveGroupMemberCount(groupDetail.groupName, groupDetail.numberOfMembers);
											HomeScreen.groupsData.add(groupDetail);
											Log.d(TAG, "counter check Discover response : "+ groupDetail.type+""+groupDetail.displayName+" , "+groupDetail.numberOfMembers);
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
				if(isAllChannelTab)
					showAllContacts(1);
				else
					showAllContacts(0);
			}
			super.onPostExecute(str);
		}
	}
//	public void showPopup(View v){
//		 PopupMenu popup = new PopupMenu(getActivity().getApplicationContext(), v);
//		 popup.setOnMenuItemClickListener(this);
//		 popup.getMenu().add(0,0,0,getResources().getString(R.string.create_group));
//		 popup.getMenu().add(0,1,0,getResources().getString(R.string.settings));
//		 popup.show();
//	}
	boolean isFirstTimeDialogShowing;
	public void showDialog(String s, boolean custom) {
		final Dialog bteldialog = new Dialog(getActivity());
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
		final Dialog bteldialog = new Dialog(getActivity());
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
//	@Override
//	public boolean onMenuItemClick(MenuItem item) {
//		// TODO Auto-generated method stub
//		  switch (item.getItemId()) {
//	        case 0: // Create A group
//	        	Intent intent = new Intent(SuperChatApplication.context, CreateGroupScreen.class);
//				intent.putExtra(Constants.CHANNEL_CREATION, false);
//				startActivity(intent);	
//				return true;
//	        case 1: // Settings
//	        	intent = new Intent(getActivity(), MoreScreen.class);
//				startActivity(intent);		
//				return true;
//		  }
//		return false;
//	}
}
