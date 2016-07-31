package com.superchat.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.ErrorModel;
import com.superchat.model.LoginResponseModel;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.MyriadRegularTextView;
import com.superchat.widgets.RoundedImageView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SharedIDAdapter  extends ArrayAdapter<LoginResponseModel.BroadcastGroupDetail>{
	public final static String TAG = "SharedIDAdapter"; 
	Context context;
	SharedIDScreen sharedIDScreen;
	int layout;
	private boolean isEditableContact = false;
	boolean isJoinedMember;
	int check;
	int screenType;
	private static HashMap<String, Boolean> checkedTagMap = new HashMap<String, Boolean>();
	private static HashMap<String, Boolean> ownerCheckedMap = new HashMap<String, Boolean>();
	ArrayList<String> selectedUserList = new ArrayList<String>();
	public int totalChecked;
	ArrayList<LoginResponseModel.BroadcastGroupDetail> data;
	
	public SharedIDAdapter(Context context, int layoutResourceId){
		super(context, layoutResourceId);
	}
	public SharedIDAdapter(SharedIDScreen sharedIDScreen, Context context1, int layout, ArrayList<LoginResponseModel.BroadcastGroupDetail> data,int screenType)
	{
		super(context1,layout,data);
		context = context1;
		this.sharedIDScreen = sharedIDScreen;
		this.data = data;
		this.screenType =screenType;
		this.layout = layout;
		totalChecked = 0;
		checkedTagMap.clear();
		mDrawableBuilder = TextDrawable.builder().beginConfig().toUpperCase().endConfig().round();
	}
	public HashMap<String, Boolean> getSelectedItems() {
		return checkedTagMap;
	}
	public void setSelectedItems(ArrayList<String> users){
		selectedUserList = users;
		for(String user:users){
			checkedTagMap.put(user, true);
		}
	}
	public ArrayList<String> getAllUsers(){
		ArrayList<String> list = new ArrayList<String>();
		String owner = getOwner();
		
		for(LoginResponseModel.BroadcastGroupDetail item: data){
			String admin = checkedTagMap.containsKey(item.userName)==true ? checkedTagMap.get(item.userName).toString():null;
			if((owner==null || !owner.equals(item.userName)) && (admin == null || !admin.equals(item.userName)))
				list.add(item.userName);
			Log.d(TAG, "Groups info members : "+item.userName);
			}
		return list;
	}
	public ArrayList<String> getSelectedMembers(){
		
		ArrayList<String> list = new ArrayList<String>();
		String owner = getOwner();
		if(checkedTagMap!=null && !checkedTagMap.isEmpty())
			for(String item: checkedTagMap.keySet()){
				if(checkedTagMap.get(item)){
					if(owner==null || !owner.equals(item))
					list.add(item);
					Log.d(TAG, "Groups info admins : "+item);
				}
			}
		return list;
	}
	public String getOwner(){
		if(ownerCheckedMap!=null && !ownerCheckedMap.isEmpty())
			for(String item: ownerCheckedMap.keySet()){
				if(ownerCheckedMap.get(item)){
					Log.d(TAG, "Groups info owner : "+item);
					return item;
					}
			}
		return null;
	}
	public void setItems(String user,boolean flg){
			checkedTagMap.put(user, flg);
	}
	public void removeSelectedItems(){
		checkedTagMap.clear();
	}
	public void setEditableContact(boolean isEdit) {
		this.isEditableContact = isEdit;
	}
	public boolean isEditable(){
		return this.isEditableContact;
	}
	public int getCheckedCounts(){
		return totalChecked;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder viewholder = null;
		SharedPrefManager sharedPref = SharedPrefManager.getInstance();
		if(row == null)
		{
			row = LayoutInflater.from(context).inflate(layout, parent, false);
			viewholder = new ViewHolder();
			viewholder.image = (ImageView)row.findViewById(R.id.contact_icon);
			viewholder.imageDefault = (ImageView)row.findViewById(R.id.contact_icon_default);
			viewholder.memberCountsView = (TextView)row.findViewById(R.id.id_member_counts);
			viewholder.userStatusView = (TextView)row.findViewById(R.id.id_contact_status);
			viewholder.displayNameView = (TextView)row.findViewById(R.id.id_contact_name);
			viewholder.displayNameView = (TextView)row.findViewById(R.id.id_contact_name);
			viewholder.crossView = (ImageView)row.findViewById(R.id.id_cross);
			viewholder.leaveJoinView = (RelativeLayout)row.findViewById(R.id.id_leave_join);
			viewholder.iCheckBox = (CheckBox) row.findViewById(R.id.contact_sel_box);
			viewholder.iOwnerChooseBox = (CheckBox) row.findViewById(R.id.id_owner_choose);
			viewholder.unseenCountView = (MyriadRegularTextView) row.findViewById(R.id.id_unseen_count);
			viewholder.image.setOnClickListener(viewholder.onCheckeClickListener);
			viewholder.imageDefault.setOnClickListener(viewholder.onCheckeClickListener);
			viewholder.leaveJoinView.setOnClickListener(viewholder.onCheckeClickListener);
			row.setOnClickListener(viewholder.onCheckeClickListener);
			row.setTag(viewholder);
		}
		else
		{
			viewholder = (ViewHolder)row.getTag();
		}
//		ViewHolder viewholder = (ViewHolder)view.getTag();
		viewholder.map = (LoginResponseModel.BroadcastGroupDetail)getItem(position);
		if(viewholder.map!=null){
			viewholder.userNames = viewholder.map.broadcastGroupName;
//			viewholder.membersCount = viewholder.map.numberOfMembers;
			viewholder.owner = viewholder.map.userDisplayName;
			viewholder.description = viewholder.map.description;
			viewholder.map.displayName = sharedPref.getSharedIDDisplayName(viewholder.map.broadcastGroupName);
			if(sharedPref.getSharedIDFileId(viewholder.map.broadcastGroupName) != null)
				viewholder.map.fileId = sharedPref.getSharedIDFileId(viewholder.map.broadcastGroupName);
//			viewholder.memberType = viewholder.map.memberType;
		}
//		String s = cursor.getString(cursor.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
//		viewholder.displayName =viewholder.map.displayName;
		viewholder.displayName = sharedPref.getSharedIDDisplayName(viewholder.map.broadcastGroupName);
		//		viewholder.voipumValue = cursor.getString(cursor.getColumnIndex(DatabaseConstants.VOPIUM_FIELD));
//		String s2 = cursor.getString(cursor.getColumnIndex(DatabaseConstants.IS_FAVOURITE_FIELD));
//		String compositeNumber = cursor.getString( cursor.getColumnIndex(DatabaseConstants.CONTACT_COMPOSITE_FIELD));
		viewholder.displayNameView.setText(viewholder.displayName);
//		if(screenType == Constants.GROUP_USER_CHAT_CREATE){
//			viewholder.leaveJoinView.setVisibility(View.VISIBLE);
//		}else if(screenType == Constants.GROUP_USERS_ROLE_SELECTION){
//			viewholder.leaveJoinView.setVisibility(View.VISIBLE);
//		}
//		if(viewholder.map.memberType!=null){
//			 int messageCount = SharedPrefManager.getInstance().getChatCountOfUser(viewholder.map.groupName);
//			if(viewholder.map.memberType.equals("OWNER")){
//				if(messageCount>0){
//					viewholder.unseenCountView.setVisibility(View.VISIBLE);
//					viewholder.unseenCountView.setText(String.valueOf(messageCount));
//				}else
//					viewholder.unseenCountView.setVisibility(View.GONE);
//				viewholder.leaveJoinView.setVisibility(View.GONE);
//			}else if(viewholder.map.memberType.equals("ADMIN")){
//				if(messageCount>0){
//					viewholder.unseenCountView.setVisibility(View.VISIBLE);
//					viewholder.unseenCountView.setText(String.valueOf(messageCount));
//				}else
//					viewholder.unseenCountView.setVisibility(View.GONE);
//				viewholder.leaveJoinView.setVisibility(View.GONE);
//				isJoinedMember = true;
////				viewholder.leaveJoinView.setImageResource(R.drawable.leave_btn);
//			} else if(viewholder.map.memberType.equals("MEMBER")){
//				if(messageCount>0){
//					viewholder.unseenCountView.setVisibility(View.VISIBLE);
//					viewholder.unseenCountView.setText(String.valueOf(messageCount));
//				}else
//					viewholder.unseenCountView.setVisibility(View.GONE);
//				viewholder.leaveJoinView.setVisibility(View.GONE);
////				viewholder.leaveJoinView.setImageResource(R.drawable.leave_btn);
//				isJoinedMember = true;
//			} else if(viewholder.map.memberType.equals("USER")){
//				viewholder.unseenCountView.setVisibility(View.GONE);
////				viewholder.leaveJoinView.setImageResource(R.drawable.join_btn);
//			}
//		}
//		if(!viewholder.map.memberType.equals("USER")){
//			viewholder.memberCountsView.setText("Members ("+SharedPrefManager.getInstance().getGroupMemberCount(viewholder.map.groupName)+")");
////			SharedPrefManager.getInstance().saveGroupMemberCount(viewholder.map.groupName, viewholder.map.numberOfMembers);
//		}else if(viewholder.map!=null && viewholder.map.numberOfMembers!=null &&  !viewholder.map.numberOfMembers.equals("")){
//			viewholder.memberCountsView.setText("Members ("+viewholder.map.numberOfMembers+")");
//			SharedPrefManager.getInstance().saveGroupMemberCount(viewholder.map.groupName, viewholder.map.numberOfMembers);
//			}
//		OWNER/ADMIN/MEMBER/USER
		setProfilePic(viewholder.image, viewholder.imageDefault, viewholder.displayName, viewholder.userNames,viewholder.map.fileId);
		if(viewholder.map!=null && viewholder.map.description!=null)
			viewholder.userStatusView.setText(viewholder.map.description);
		return row;
	}
	
	public class ViewHolder
	{
		LoginResponseModel.BroadcastGroupDetail map;
		String id;
		TextView displayNameView;
		TextView memberCountsView;
		TextView userStatusView;
		MyriadRegularTextView unseenCountView;
		RelativeLayout leaveJoinView;
		ImageView crossView;
		String userStatus;
//		String membersCount;
		String owner;
		String description;
		String userNames;
//		String memberType;
		String displayName;
		ImageView imageDefault;
		ImageView image;
		CheckBox iCheckBox;
		CheckBox iOwnerChooseBox;
		CheckBox iOwnerPreviousChooseBox;
		ViewHolder(){

		}
		private OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
			}};
			private OnClickListener onCheckeClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
						Intent intent = new Intent(SuperChatApplication.context, CreateSharedIDActivity.class);
						intent.putExtra("EDIT_MODE", true);
						intent.putExtra(Constants.GROUP_UUID, map.broadcastGroupName);
						intent.putExtra(Constants.GROUP_NAME, map.displayName);
						intent.putExtra(Constants.GROUP_FILE_ID, map.fileId);
//						if(description != null)
//							intent.putExtra(PublicGroupInfoScreen.CHANNEL_DESCRIPTION,description);
						((SharedIDScreen)context).startActivity(intent);
				}};
	}
	
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
	private void setProfilePic(ImageView view, ImageView view_default, String displayName, String userName,String fileId){
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02
		if(groupPicId == null){
			((RoundedImageView) view).setImageResource(R.drawable.small_helpdesk);
			groupPicId = fileId;
//			return;
		}
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		((RoundedImageView) view).setImageResource(R.drawable.small_helpdesk);
		if (bitmap != null) {
			view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			RoundedImageView img = (RoundedImageView) view;
			img.setImageBitmap(bitmap);
		}else if(groupPicId!=null && !groupPicId.equals("")){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto +profilePicUrl;
			File file1 = new File(filename);
			if(file1.exists()){
//				CompressImage compressImage = new CompressImage(context);
//				filename = compressImage.compressImage(filename);
//				view.setImageURI(Uri.parse(filename));
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.small_helpdesk);
				setThumb(view, filename,groupPicId);
//				view.setBackgroundDrawable(null);

			}else{
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.small_helpdesk);
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader((RoundedImageView)view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
	             else
	            	 new BitmapDownloader((RoundedImageView)view).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			}
		}else if(fileId!=null && !fileId.equals("")){
			String profilePicUrl = fileId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			//			if(groupPicId != null && groupPicId.length() > 0 && groupPicId.lastIndexOf('/')!=-1)
			//				profilePicUrl += groupPicId.substring(groupPicId.lastIndexOf('/'));

			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto +profilePicUrl;
			File file1 = new File(filename);
			//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				if(view_default != null)
					view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.small_helpdesk);
				setThumb(view, filename,fileId);
				view.setTag(filename);
			}else{
				//Downloading the file
				if(view_default != null)
					view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.small_helpdesk);
//				(new ProfilePicDownloader()).download(Constants.media_get_url+fileId+".jpg", (RoundedImageView)view, null);
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader((RoundedImageView)view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
	             else
	            	 new BitmapDownloader((RoundedImageView)view).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			}
		}else{
//			if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
//				view.setImageResource(R.drawable.female_default);
//			else
//				view.setImageResource(R.drawable.male_default);
			view_default.setVisibility(View.VISIBLE);
			view.setVisibility(View.INVISIBLE);
			view_default.setImageResource(R.drawable.small_helpdesk);
//			try{
//				String name_alpha = String.valueOf(displayName.charAt(0));
//				if(displayName.contains(" ") && displayName.indexOf(' ') < (displayName.length() - 1))
//					name_alpha +=  displayName.substring(displayName.indexOf(' ') + 1).charAt(0);
//				TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(displayName));
//				view.setVisibility(View.INVISIBLE);
//				view_default.setVisibility(View.VISIBLE);
//				view_default.setImageDrawable(drawable);
//				view_default.setBackgroundColor(Color.TRANSPARENT);
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
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
		    bm = BitmapFactory.decodeFile(path);//, bfo);
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
	private class MemberDeactivationTaskOnServer extends AsyncTask<String, String, String> {
		String objectUserName;
		ProgressDialog progressDialog = null;
		public MemberDeactivationTaskOnServer(String objectUserName){
			this.objectUserName = objectUserName;
			
			
		}
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
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
//			http://52.88.175.48/tiger/rest/user/deactivate?byUser=919717098492_p5domain&toUser=919910968484_p5domain (Both GET and POST are supported)
			
				urlInfo = "/tiger/rest/user/deactivate?byUser="+iPrefManager.getUserName()+"&toUser="+objectUserName;
			
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ urlInfo);
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
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
				if (response!=null && !response.equals("") && response.contains("success")){
					if(((EsiaChatContactsScreen)context).service!=null)
						((EsiaChatContactsScreen)context).service.sendInfoMessage(objectUserName,"test",Message.XMPPMessageType.atMeXmppMessageTypeDeactivateUser);
					 DBWrapper.getInstance().deleteContact(objectUserName);
					 Intent intent = new Intent(context, HomeScreen.class);
//						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_CLEAR_TASK
								| Intent.FLAG_ACTIVITY_NEW_TASK);
//						((EsiaChatContactsScreen)context).startActivity(intent);
						HomeScreen.refreshContactList = true;
						((EsiaChatContactsScreen)context).finish();
				}
				super.onPostExecute(response);
			}
		}
	}
	public void showDialog(String s) {
		final Dialog bteldialog = new Dialog(context);
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
//	public void showDialog(final String title, final String s,final LoginResponseModel.GroupDetail map,final boolean isJoinning) {
//		final Dialog bteldialog = new Dialog(context);
//		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//		bteldialog.setCanceledOnTouchOutside(true);
//		bteldialog.setContentView(R.layout.custom_dialog_two_button);
//		if(title!=null){
//			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
//			}
//		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
//		
//		if(isJoinning){
//			((TextView)bteldialog.findViewById(R.id.id_send)).setText("Join");
//		}else
//			((TextView)bteldialog.findViewById(R.id.id_send)).setText("Leave");
//		((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				bteldialog.cancel();
//				if(isJoinning){
//					if(Build.VERSION.SDK_INT >= 11)
//						new ChannelLeaveJoinTaskOnServer(map).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,map.memberType,map.groupName);
//					else
//						new ChannelLeaveJoinTaskOnServer(map).execute(map.memberType,map.groupName);
//				}else{
//					if(Build.VERSION.SDK_INT >= 11)
//						new ChannelLeaveJoinTaskOnServer(map).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,map.memberType,map.groupName);
//					else
//						new ChannelLeaveJoinTaskOnServer(map).execute(map.memberType,map.groupName);
//				}
//				return false;
//			}
//		});
//((TextView)bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				bteldialog.cancel();
//				return false;
//			}
//		});
//		bteldialog.show();
//	}
//	public class ChannelLeaveJoinTaskOnServer extends AsyncTask<String, String, String> {
//		LoginModel loginForm;
//		ProgressDialog progressDialog = null;
//		SharedPrefManager sharedPrefManager;
//		boolean isJoinning;
//		LoginResponseModel.GroupDetail map;
//		public ChannelLeaveJoinTaskOnServer(LoginResponseModel.GroupDetail map){
//			sharedPrefManager = SharedPrefManager.getInstance();
//			loginForm = new LoginModel();
//			loginForm.setUserName(sharedPrefManager.getUserName());
//			loginForm.setPassword(sharedPrefManager.getUserPassword());
//			loginForm.setToken(sharedPrefManager.getDeviceToken());
//			this.map = map;
//		}
//		@Override
//		protected void onPreExecute() {
//				progressDialog = ProgressDialog.show((HomeScreen)context, "", "Request processing. Please wait...", true);
//			super.onPreExecute();
//		}
//		@Override
//		protected String doInBackground(String... params) {
//			// TODO Auto-generated method stub
////			String JSONstring = new Gson().toJson(loginForm);
//			String url = "";
//			if(params!=null && params.length>0){
//				String query = params[1];
//				try {
//					 query = URLEncoder.encode(query, "utf-8");
//				} catch (UnsupportedEncodingException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if(params[0].equals("USER")){
//					url = "join?groupName="+query;
//					isJoinning = true;
//				}else{
//					url = "leave?groupName="+query;
//					isJoinning = false;
//				}
//			}
//		    DefaultHttpClient client1 = new DefaultHttpClient();
//		    
////		    http://52.88.175.48/tiger/rest/group/leave?groupName=qa_p5domain
////		    http://52.88.175.48/tiger/rest/group/join?groupName=qa_p5domain
//		    
//			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/group/"+url);
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
//				            	return str;
//								
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
//				if (str!=null && str.contains("\"status\":\"success\"")){
//					Gson gson = new GsonBuilder().create();
//					ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
//					sharedIDScreen.updateDataWithUILocally(map,isJoinning);
//					if(errorModel!=null)
//						Toast.makeText(context, errorModel.message, Toast.LENGTH_SHORT).show();//showDialog(errorModel.message);
//					}
//			}
//			super.onPostExecute(str);
//		}
//	}
	
}
