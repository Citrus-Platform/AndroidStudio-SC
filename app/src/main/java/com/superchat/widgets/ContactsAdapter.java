



package com.superchat.widgets;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.beans.PhotoToLoad;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.ContactUpDatedModel;
import com.superchat.model.ContactUpDatedModel.UserDetail;
import com.superchat.model.ContactUploadModel;
import com.superchat.task.ImageLoaderWorker;
import com.superchat.ui.BulkInvitationScreen;
import com.superchat.ui.ChatListScreen;
import com.superchat.ui.ContactsScreen;
import com.superchat.ui.CreateBroadCastScreen;
import com.superchat.ui.CreateGroupScreen;
import com.superchat.ui.CreateSharedIDActivity;
import com.superchat.ui.EsiaChatContactsScreen;
import com.superchat.ui.HomeScreen;
import com.superchat.ui.MemberStatsScreen;
import com.superchat.ui.ProfileScreen;
import com.superchat.ui.TextDrawable;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Referenced classes of package com.vopium.widget:
//            DontPressWithParentLayout

public class ContactsAdapter extends SimpleCursorAdapter
{
	public class ViewHolder
	{

		public ImageView favouriteImage;
		public ImageView contactImage;
		public ImageView contactImageDefault;
		public ImageView adminOptionImages;
		public String id;
		TextView name;
		public TextView contact_status;
		public TextView iconText;
		public TextView contactAddedTime;
		private CheckBox iCheckBox;
		String userNames="";
		String nameText="";
		String contactNumber="";
		String voipumValue="";
		String compositeNumber="";
		String userType = "";
		String flatNumber = null;
		String buildingNumber = null;
		public ImageView contactAddedOrNot;
		public TextView user_type;
		private OnClickListener onCheckeClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
			
				if(v.getId() == R.id.contact_icon || v.getId() == R.id.contact_icon_default){
					if(SharedPrefManager.getInstance().isSharedIDContact(userNames)){
						if(SharedPrefManager.getInstance().isDomainAdminORSubAdmin() ||
							HomeScreen.isAdminFromSharedID(userNames, SharedPrefManager.getInstance().getUserName())){
							Intent intent = new Intent(SuperChatApplication.context, CreateSharedIDActivity.class);
							intent.putExtra("EDIT_MODE", true);
							intent.putExtra(Constants.GROUP_UUID, userNames);
							intent.putExtra(Constants.GROUP_NAME, SharedPrefManager.getInstance().getSharedIDDisplayName(userNames));
							intent.putExtra(Constants.GROUP_FILE_ID, SharedPrefManager.getInstance().getSharedIDFileId(userNames));
							((HomeScreen) context).startActivity(intent);
						}else return;
					}else{
						Intent intent1 = new Intent(SuperChatApplication.context, ProfileScreen.class);
						 Bundle bundle = new Bundle();
						 bundle.putString(Constants.CHAT_USER_NAME, userNames);
						 bundle.putString(Constants.CHAT_NAME, nameText);
						 bundle.putBoolean("VIEW_ONLY", true);
						 intent1.putExtras(bundle);
						 ((HomeScreen)context).startActivity(intent1);
					}
					return;
				}
				
				Log.d("compositeNumber", "compositeNumber:- "+compositeNumber);
				if(compositeNumber== null || compositeNumber.split(",").length>1 ){
//					Intent intent = new Intent(SuperChatApplication.context, ContactDetails.class);
//					intent.putExtra(DatabaseConstants.NAME_CONTACT_ID_FIELD, id);
//					intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, nameText);
//					intent.putExtra("is_vopium_user", true);
//					((HomeScreen)context).startActivity(intent);
				}else if(voipumValue != null && voipumValue.equals("1")){
					if(HomeScreen.calledForShare){
						String type = "Voice";
						switch(HomeScreen.sharingType){
						case HomeScreen.VIDEO_SHARING:
							type = "video message";
							break;
						case HomeScreen.VOICE_SHARING:
							type = "Voice message";
							break;
						case HomeScreen.IMAGE_SHARING:
							type = "picture message";
							break;
						case HomeScreen.PDF_SHARING:
							type = "PDF file";
							break;
						}
						showDialog(nameText,"You are sharing "+type+".");
						return;
					}
					if(!SharedPrefManager.getInstance().isUserExistence(userNames)){
				     	  showDialog(SharedPrefManager.getInstance().getUserServerName(userNames)+" has been deactivated.");
				     	  return;
					}
//					if(SharedPrefManager.getInstance().isSharedIDContact(userNames) 
//							&& (SharedPrefManager.getInstance().isDomainAdmin() || HomeScreen.isAdminFromSharedID(userNames, SharedPrefManager.getInstance().getUserName()))){
//						Intent intent = new Intent(SuperChatApplication.context, CreateSharedIDActivity.class);
//						intent.putExtra("EDIT_MODE", true);
//						intent.putExtra(Constants.GROUP_UUID, userNames);
//						intent.putExtra(Constants.GROUP_NAME, SharedPrefManager.getInstance().getSharedIDDisplayName(userNames));
//						intent.putExtra(Constants.GROUP_FILE_ID, SharedPrefManager.getInstance().getSharedIDFileId(userNames));
//						((HomeScreen) context).startActivity(intent);
//						return;
//					}else
					{
						Intent intent = new Intent(SuperChatApplication.context,ChatListScreen.class);
						intent.putExtra(DatabaseConstants.USER_NAME_FIELD, userNames);
						intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,nameText);
						intent.putExtra("is_vopium_user", true);
						((HomeScreen) context).startActivity(intent);
					}
				}
				else{
					if(userNames.equals("view_member_stats")){
						Intent intent = new Intent(SuperChatApplication.context, MemberStatsScreen.class);
						((HomeScreen) context).startActivity(intent);
					}else if(userNames.equals("remove_domain_member")){
						Intent intent = new Intent(SuperChatApplication.context,EsiaChatContactsScreen.class);
						intent.putExtra(Constants.CHAT_TYPE, Constants.MEMBER_DELETE);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						((HomeScreen) context).startActivity(intent);
						((HomeScreen) context).startActivityForResult(intent, 105);
					}else if(userNames.equals("new_domain_member")){
//						Intent intent = new Intent(SuperChatApplication.context, InviteMemberScreen.class);
//						((HomeScreen) context).startActivity(intent);
						Intent intent = new Intent(SuperChatApplication.context, BulkInvitationScreen.class);
						((HomeScreen) context).startActivity(intent);
					}else if(userNames.equals("create_channel")){
						Intent intent = new Intent(SuperChatApplication.context, CreateGroupScreen.class);
						intent.putExtra(Constants.CHANNEL_CREATION, true);
						((HomeScreen) context).startActivity(intent);
					}else if(userNames.equals("create_group")){
//						Intent intent = new Intent(SuperChatApplication.context,EsiaChatContactsScreen.class);
//						intent.putExtra(Constants.CHAT_TYPE, Constants.GROUP_USER_CHAT_CREATE);
//						intent.putExtra(Constants.IS_GROUP_INVITATION, true);
//						intent.putExtra(Constants.GROUP_NAME, groupUUID);
//						
//						intent.putStringArrayListExtra(Constants.GROUP_USERS, new ArrayList<String>(usersList));
////						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						((HomeScreen) context).startActivity(intent);
						
						Intent intent = new Intent(SuperChatApplication.context, CreateGroupScreen.class);
						intent.putExtra(Constants.CHANNEL_CREATION, false);
						((HomeScreen) context).startActivity(intent);
					}else if(userNames.equals("create_broadcast")){
//						Toast.makeText(SuperChatApplication.context, "Under development.", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(SuperChatApplication.context, CreateBroadCastScreen.class);
						intent.putExtra(Constants.BROADCAST, true);
						((HomeScreen) context).startActivity(intent);
					}else{
						List<String> list = new ArrayList<String>();
						String formatedNumber = formatNumber(compositeNumber);
						list.add(formatedNumber);
						serverUpdateContactsInfo(null,list,nameText);
					}
//					//do invite here
//					try {
//						String text = EsiaChatApplication.context.getResources().getString(R.string.invite_text);
//						Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//						shareIntent.setType("text/plain");
//						shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);    
//						((HomeScreen) context).startActivity(Intent.createChooser(shareIntent, EsiaChatApplication.context.getString(R.string.invite)));
//					} catch (Exception e) {
//						//				        Toast.makeText(KainatInviteSelection.this, "Facebook not Installed", Toast.LENGTH_SHORT).show();
//					}  
				}
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
					Intent intent = new Intent(SuperChatApplication.context,ChatListScreen.class);
					intent.putExtra(DatabaseConstants.USER_NAME_FIELD, userNames);
					intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,nameText);
					intent.putExtra("is_vopium_user", true);
					((HomeScreen) context).startActivity(intent);
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
		public void showDialog(String s) {
	        final Dialog bteldialog = new Dialog((HomeScreen)context);
	        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        bteldialog.setCanceledOnTouchOutside(false);
	        bteldialog.setContentView(R.layout.custom_dialog);
	        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
	        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
	                bteldialog.cancel();
	                return false;
	            }
	        });
	        bteldialog.show();
	    }
		public ViewHolder(){
		}
	}

	 
	int check;
	private static final String TAG = "ContactsAdapter";
	private static HashMap<String, Boolean> checkedTagMap = new HashMap<String, Boolean>();
	private static Context context;
	public ExecutorService executorService;
	Handler handler;
	private boolean isEditableContact = false;
	int layout;
	public int listenerState;
	ViewGroup myParent;
	private Uri uri;
	boolean isRWA;

	public ContactsAdapter(Context context1, int i, Cursor cursor, String as[], int ai[], int j)
	{
		super(context1, i, cursor, as, ai, j);
		executorService = Executors.newFixedThreadPool(2);
		check = 0;
		context = context1;
		executorService = Executors.newFixedThreadPool(5);
		layout = i;
		handler = new Handler();
		mDrawableBuilder = TextDrawable.builder()
                .beginConfig().toUpperCase()
            .endConfig()
            .round();
		isRWA = SharedPrefManager.getInstance().getDomainType().equals("rwa");
		
//		 mDrawableBuilder = TextDrawable.builder().round();
	}
	public HashMap<String, Boolean> getSelectedItems() {
		return checkedTagMap;
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
	private void displayImage(ImageView imageview, String s, boolean flag){
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(s);
		if (bitmap != null)
		{
			imageview.setImageBitmap(bitmap);
		} else
		{
			imageview.setImageResource(R.drawable.avatar);
		}
		if (!flag && bitmap == null)
		{
			PhotoToLoad phototoload = new PhotoToLoad(imageview, s);
			executorService.execute(new ImageLoaderWorker(phototoload));
		}
	}

	public void bindView(View view, Context context1, Cursor cursor){
		ViewHolder viewholder = (ViewHolder)view.getTag();
		viewholder.userNames = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
		String s = cursor.getString(cursor.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
		viewholder.nameText =cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));		
		viewholder.userType =cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_TYPE_FIELD));		
		viewholder.voipumValue = cursor.getString(cursor.getColumnIndex(DatabaseConstants.VOPIUM_FIELD));
		if(SharedPrefManager.getInstance().getDomainType().equals("rwa")){
			viewholder.flatNumber = cursor.getString(cursor.getColumnIndex(DatabaseConstants.FLAT_NUMBER));
			viewholder.buildingNumber = cursor.getString(cursor.getColumnIndex(DatabaseConstants.BUILDING_NUMBER));
		}
		String s2 = cursor.getString(cursor.getColumnIndex(DatabaseConstants.IS_FAVOURITE_FIELD));
		String phone_type = cursor.getString(cursor.getColumnIndex(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD));
		boolean isSharedIDContact = false;
//		if(phone_type != null && phone_type.equals("2"))
//			continue;
		viewholder.compositeNumber = cursor.getString( cursor.getColumnIndex(DatabaseConstants.CONTACT_NUMBERS_FIELD));
//		Log.d(TAG, "Contacts - "+viewholder.nameText);
		viewholder.id = s;
		if(viewholder.nameText != null && viewholder.nameText.equals("Create Channel"))
			viewholder.nameText = SuperChatApplication.context.getResources().getString(R.string.create_channel);
//		displayImage(viewholder.contactImage, s, true);
		if(viewholder.nameText != null && viewholder.nameText.equals(""))
			viewholder.nameText = "SuperChatter";
		if(viewholder.nameText.startsWith(Constants.SHARED_ID_START_STRING)){
//			viewholder.nameText = viewholder.nameText.substring(viewholder.nameText.indexOf(Constants.SHARED_ID_START_STRING) + Constants.SHARED_ID_START_STRING.length());
			viewholder.nameText = SharedPrefManager.getInstance().getSharedIDDisplayName(viewholder.userNames);
			isSharedIDContact = true;
		}
		viewholder.name.setText(viewholder.nameText);
//		viewholder.contactAddedOrNot.setBackgroundResource(R.drawable.chat_icon_green);
		if(viewholder.userType != null && !viewholder.voipumValue.equals("0") && !isSharedIDContact){
			viewholder.user_type.setVisibility(View.VISIBLE);
				if(viewholder.userType.equalsIgnoreCase("domainAdmin"))
					viewholder.user_type.setText(SuperChatApplication.context.getResources().getString(R.string.admin));
				else if(!viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.create_broadcast_list)) 
						&& !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.create_group))
						&& !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.invite_member)) 
						&& !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.manage_members))
						&& !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.create_channel))){
					viewholder.user_type.setText(SuperChatApplication.context.getResources().getString(R.string.member));
				}
		}else if(!viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.create_broadcast_list)) 
				&& !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.create_group))
				&& !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.invite_member)) 
				&& !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.manage_members))
				&& !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.view_member_stats))
			    && !viewholder.nameText.contains(SuperChatApplication.context.getResources().getString(R.string.create_channel))
			    && !isSharedIDContact){
			viewholder.user_type.setText(SuperChatApplication.context.getResources().getString(R.string.member));
//			viewholder.user_type.setVisibility(View.INVISIBLE);
		}else
			viewholder.user_type.setVisibility(View.GONE);
//				viewholder.contactAddedTime.setVisibility(TextView.GONE);
//					if(viewholder.compositeNumber!=null){
//						viewholder.contact_status.setText("+"+viewholder.compositeNumber.replace("-", ""));
//				}else
//					viewholder.contact_status.setText("");
			if(isSharedIDContact)
				viewholder.contact_status.setText("("+SuperChatApplication.context.getResources().getString(R.string.official)+")");
			else{
				if(isRWA){
					if(viewholder.flatNumber != null && viewholder.flatNumber.trim().length() > 0 
							&& viewholder.buildingNumber != null && viewholder.buildingNumber.trim().length() > 0)
					viewholder.contact_status.setText(SuperChatApplication.context.getResources().getString(R.string.flat_num_txt) + " " +
							viewholder.flatNumber+", "+SuperChatApplication.context.getResources().getString(R.string.building_num_txt)+" "+viewholder.buildingNumber);
					else if(viewholder.flatNumber != null && viewholder.flatNumber.trim().length() > 0 )
						viewholder.contact_status.setText(SuperChatApplication.context.getResources().getString(R.string.flat_num_txt) + " " +viewholder.flatNumber);
					else if(viewholder.buildingNumber != null && viewholder.buildingNumber.trim().length() > 0)
						viewholder.contact_status.setText(SuperChatApplication.context.getResources().getString(R.string.building_num_txt)+" "+viewholder.buildingNumber);
					else
						viewholder.contact_status.setText("");
				}else
					viewholder.contact_status.setText(SharedPrefManager.getInstance().getUserStatusMessage(viewholder.userNames));
			}
//			id_admin_option
			
			viewholder.contactImageDefault.setTag(viewholder.userNames);
			if(viewholder.voipumValue.equals("0")){
				viewholder.contactImage.setVisibility(View.INVISIBLE);
				viewholder.adminOptionImages.setVisibility(View.VISIBLE);
				setProfilePic(viewholder.adminOptionImages, viewholder.contactImageDefault, viewholder.userNames,viewholder.nameText,viewholder.iconText, isSharedIDContact);
			}else{
				viewholder.contactImage.setVisibility(View.VISIBLE);
				viewholder.adminOptionImages.setVisibility(View.INVISIBLE);
				setProfilePic(viewholder.contactImage, viewholder.contactImageDefault, viewholder.userNames,viewholder.nameText,viewholder.iconText, isSharedIDContact);
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
		
//		if(str.length()<=11){
//			str = Constants.countryCode.replace("+", "")+"-"+str;
//		}else{
//			StringBuffer buffer = new StringBuffer(str);
//			
//			buffer = buffer.insert(str.length()-10, '-');
////			if(buffer.charAt(0) == '0'){
////				while(buffer.charAt(0) == '0' && buffer.length()>0)
////					buffer.deleteCharAt(0);
////				buffer.insert(0, AppConstants.countryCode.replace("+", ""));
////			}
//			str = buffer.toString();
//		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	public View newView(Context context1, Cursor cursor, ViewGroup viewgroup)
	{
		myParent = viewgroup;
		View view = LayoutInflater.from(context).inflate(layout, null);
		ViewHolder viewholder = new ViewHolder();

//		viewholder.iconText = (TextView)view.findViewById(R.id.id_icon_text);
		viewholder.contact_status = (TextView)view.findViewById(R.id.id_contact_status);
		viewholder.contactAddedTime = (TextView)view.findViewById(R.id.id_status_time);
		viewholder.name = (TextView)view.findViewById(R.id.id_contact_name);
		viewholder.contactImage = (ImageView)view.findViewById(R.id.contact_icon);
		viewholder.contactImageDefault = (ImageView)view.findViewById(R.id.contact_icon_default);
		viewholder.adminOptionImages = (ImageView)view.findViewById(R.id.id_admin_option);
		viewholder.contactAddedOrNot = (ImageView)view.findViewById(R.id.id_esia_contact);
		viewholder.user_type = (TextView)view.findViewById(R.id.user_type);
		
//		if (isEditableContact){
			viewholder.contactImage.setOnClickListener(viewholder.onCheckeClickListener);
			viewholder.contactImageDefault.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.name.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.iCheckBox.setOnClickListener(viewholder.onCheckeClickListener);
			view.setOnClickListener(viewholder.onCheckeClickListener);
//		}
		String s = cursor.getString(cursor.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
		viewholder.id = s;
		view.setTag(viewholder);
//		displayImage(viewholder.contactImage, s, false);
//		viewholder.userNames = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
//		setProfilePic(viewholder.contactImage,viewholder.userNames);
		return view;
	}
	// declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
	public void setProfilePic(ImageView view, ImageView view_default,  String userName,String displayName,TextView iconText, boolean sharedID){
		String groupPicId = null;
		if(sharedID) {
			groupPicId = SharedPrefManager.getInstance().getSharedIDFileId(userName);
            if(groupPicId != null && groupPicId.startsWith("http://") && groupPicId.lastIndexOf('/') != -1 && groupPicId.lastIndexOf('.') != -1)
                groupPicId = groupPicId.substring(groupPicId.lastIndexOf('/') + 1, groupPicId.lastIndexOf('.'));
		}
		else
			groupPicId = SharedPrefManager.getInstance().getUserFileId(userName);
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		if (bitmap != null) {
			view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			view.setImageBitmap(bitmap);
			view.setBackgroundColor(Color.TRANSPARENT);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto +profilePicUrl;
			view.setTag(filename);
//			iconText.setVisibility(View.INVISIBLE);
		}else if(groupPicId!=null && !groupPicId.equals("")&& !groupPicId.equals("clear")){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator +  Constants.contentProfilePhoto +profilePicUrl;
			view.setTag(filename);
			File file1 = new File(filename);
			if(file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				setThumb(view,filename,groupPicId);
//				view.setBackgroundDrawable(null);
				}else{
				//Downloading the file
//				view_default.setVisibility(View.INVISIBLE);
//				view.setVisibility(View.VISIBLE);
//				view.setImageResource(R.drawable.group_icon);
//				(new ProfilePicDownloader()).download(Constants.media_get_url+groupPicId+".jpg", (RoundedImageView)view, null);
				try{
					if(sharedID){
						view.setImageResource(R.drawable.small_helpdesk);
					}else{
						String name_alpha = "";
						if(displayName != null && displayName.trim().length() > 0)
							name_alpha = String.valueOf(displayName.charAt(0));
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
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader((RoundedImageView)view,view_default).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId, BitmapDownloader.THUMB_REQUEST);
	             else
	            	 new BitmapDownloader((RoundedImageView)view,view_default).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			}
//			iconText.setVisibility(View.INVISIBLE);
		}else{
//			iconText.setVisibility(View.VISIBLE);
			if(userName.equals("view_member_stats")){
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.iconmemberstats);
			}else if(userName.equals("remove_domain_member")){
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.iconmanagemember);
			}else if(userName.equals("new_domain_member")){
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.iconinvite);
			}else if(userName.equals("create_channel")){
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.iconcreategroup);
			}else if(userName.equals("create_group")){
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.iconcreategroup);
			}else if(userName.equals("create_broadcast")){
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				view.setImageResource(R.drawable.iconbroadcast);
			}else{
				try{
					if(sharedID){
						view_default.setImageResource(R.drawable.small_helpdesk);
						view.setImageResource(R.drawable.small_helpdesk);
					}else{
						String name_alpha = "";
						if(displayName != null && displayName.trim().length() > 0)
							name_alpha = String.valueOf(displayName.charAt(0));
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
//				if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female")){
////				view.setImageResource(R.drawable.female_default);
//				TextDrawable drawable = mDrawableBuilder.build(String.valueOf(displayName.charAt(0)), mColorGenerator.getColor(displayName));
//				view.setImageDrawable(drawable);
//				view.setBackgroundColor(Color.TRANSPARENT);
//			}
//			else{
////				view.setImageResource(R.drawable.male_default);
//				TextDrawable drawable = mDrawableBuilder.build(String.valueOf(displayName.charAt(0)) + String.valueOf(displayName.charAt(1)), mColorGenerator.getColor(displayName));
//				view.setImageDrawable(drawable);
//				view.setBackgroundColor(Color.TRANSPARENT);
//			}
							

//			if(displayName!=null){
//				displayName = displayName.trim();
//				String iconTxt = displayName.charAt(0)+"";
//if(displayName.contains(" "))
//	iconTxt = iconTxt+displayName.charAt(displayName.indexOf(" ")+1);
//				iconText.setText(iconTxt);
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
	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
	    } else{
	    	try{
	    		imageViewl.setImageURI(Uri.parse(path));
	    	}catch(Exception e){
	    		
	    	}
	    }
	}
	public void setDirectCallDisabled(boolean flag)
	{
	}
	private static void serverUpdateContactsInfo(final ContactsScreen obj,List<String> numbers, final String displayName){
		final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();

//		List<String> emails =DBWrapper.getInstance().getAllEmails();
//		List<String> numbers = DBWrapper.getInstance().getAllNumbers();

		ContactUploadModel model = new ContactUploadModel(
				iPrefManager.getUserId(), null, numbers);
//		Log.d(TAG, "serverUpdateContactsInfo request:"+model.toString());
		AsyncHttpClient client = new AsyncHttpClient();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("form", new Gson()
		.toJson(model)));
		for (NameValuePair pair : nameValuePairs) {
			Log.d(TAG, "serverUpdateContactsInfo login NameValuePair: " + pair.getName() + ":"
					+ pair.getValue());
		}
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		client.post(SuperChatApplication.context, Constants.SERVER_URL
				+ "/jakarta/rest/contact/upload", entity, null,
				new AsyncHttpResponseHandler() {
			ProgressDialog dialog = null;
			@Override
			public void onStart() {
				dialog = ProgressDialog.show(context, "","Loading. Please wait...", true);
				Log.d(TAG, "AsyncHttpClient onStart: ");
			}

			@Override
			public void onSuccess(int arg0, String arg1) {
				Log.d(TAG, "serverUpdateContactsInfo onSuccess: "+ arg1);
				Gson gson = new GsonBuilder().create();
				if (arg1==null || arg1.contains("error")){
//					contactSyncState = CONTACT_SYNC_FAILED;
					Log.d(TAG,
							"serverUpdateContactsInfo onSuccess error comming : "
									+ arg1);
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					
					callInvite();
					return;
				}

				ContactUpDatedModel updatedModel = gson.fromJson(arg1,
						ContactUpDatedModel.class);
				if (updatedModel != null) {
					UserDetail userDetail = null;
					Log.d(TAG,
							"serverUpdateContactsInfo onSuccess : Contact synced successful. ");
					if(updatedModel.mobileNumberUserBaseMap != null)
					for (String st : updatedModel.mobileNumberUserBaseMap
							.keySet()) {
						 userDetail = updatedModel.mobileNumberUserBaseMap
								.get(st);
						//						Log.d(TAG, "contacts sync info with sip address: " + userDetail.iSipAddress);
						ContentValues contentvalues = new ContentValues();
//						contentvalues.put(
//								DatabaseConstants.USER_SIP_ADDRESS,
//								userDetail.iSipAddress);
						contentvalues.put(
								DatabaseConstants.USER_NAME_FIELD,
								userDetail.userName);
						contentvalues.put(
								DatabaseConstants.VOPIUM_FIELD,
								Integer.valueOf(1));
						contentvalues
						.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,
								userDetail.mobileNumber);
						
						DBWrapper.getInstance().updateAtMeDirectStatus(contentvalues,DatabaseConstants.CONTACT_NUMBERS_FIELD);
						DBWrapper.getInstance().updateAtMeContactDetails(contentvalues,userDetail.mobileNumber);
						DBWrapper.getInstance().updateUserNameInContacts(userDetail.userName,userDetail.mobileNumber);
						if(userDetail!=null && userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
							if(iPrefManager.getUserFileId(userDetail.userName) == null || !iPrefManager.getUserFileId(userDetail.userName).equals(userDetail.imageFileId))
								new BitmapDownloader().execute(userDetail.imageFileId);
							}
						if(iPrefManager!=null && userDetail!=null && userDetail.userName!=null){
							iPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
							iPrefManager.saveUserStatusMessage(userDetail.userName,  userDetail.currentStatus);
						}
					}
					if(updatedModel.mobileNumberUserBaseMap == null || updatedModel.mobileNumberUserBaseMap.isEmpty())
						callInvite();
					else if(userDetail!=null){
						Intent intent = new Intent(SuperChatApplication.context,ChatListScreen.class);
						intent.putExtra(DatabaseConstants.USER_NAME_FIELD, userDetail.userName);
						intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,displayName);
						intent.putExtra("is_vopium_user", true);
						((HomeScreen) context).startActivity(intent);
					}
//					for (String st : updatedModel.emailUserBaseMap
//							.keySet()) {
//						UserDetail userDetail = updatedModel.emailUserBaseMap
//								.get(st);
//						// Log.d(TAG,
//						// "contacts sync info with sip address: " +
//						// userDetail.iSipAddress);
//						ContentValues contentvalues = new ContentValues();
//						contentvalues.put(
//								DatabaseConstants.USER_SIP_ADDRESS,
//								userDetail.iSipAddress);
//						contentvalues.put(
//								DatabaseConstants.USER_NAME_FIELD,
//								userDetail.userName);
//						contentvalues.put(
//								DatabaseConstants.VOPIUM_FIELD,
//								Integer.valueOf(1));
//						contentvalues.put(
//								DatabaseConstants.PHONE_EMAILS_FIELD,
//								userDetail.email);
//						DBWrapper.getInstance().updateAtMeDirectStatus(contentvalues,DatabaseConstants.PHONE_EMAILS_FIELD);
//						DBWrapper.getInstance().updateAtMeContactDetails(contentvalues,userDetail.email);
//					}
//					if(obj!=null)
//						obj.notifyUpdate();
//					contactSyncState = CONTACT_SYNC_SUCESSED;
				}
				else
					callInvite();
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				Log.d(TAG,
						"AsyncHttpClient onSuccess : Contact synced successful. ");
				super.onSuccess(arg0, arg1);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
//				contactSyncState = CONTACT_SYNC_FAILED;
				Log.d(TAG,
						"serverUpdateContactsInfo onFailure: Contact sync has failed"
								+ arg1);
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				super.onFailure(arg0, arg1);
				
				callInvite();  
			}
		});
	}
	private static void callInvite(){
		//do invite here
		try {
			String text = SuperChatApplication.context.getResources().getString(R.string.invite_text);
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);    
			((HomeScreen) context).startActivity(Intent.createChooser(shareIntent, SuperChatApplication.context.getString(R.string.invite)));
		} catch (Exception e) {
			//				        Toast.makeText(KainatInviteSelection.this, "Facebook not Installed", Toast.LENGTH_SHORT).show();
		}  
	}
}