package com.superchat.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import com.superchat.data.beans.PhotoToLoad;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.ErrorModel;
import com.superchat.task.ImageLoaderWorker;
import com.superchat.ui.EsiaChatContactsAdapter.ViewHolder;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupRoleCreationAdapter  extends ArrayAdapter<GroupRoleCreationAdapter.UserInfo>{
	public final static String TAG = "EsiaChatContactsAdapter"; 
	Context context;
	int layout;
	private boolean isEditableContact = false;
	int check;
	int screenType;
	private static HashMap<String, Boolean> checkedTagMap = new HashMap<String, Boolean>();
	private static HashMap<String, Boolean> ownerCheckedMap = new HashMap<String, Boolean>();
	ArrayList<String> selectedUserList = new ArrayList<String>();
	public int totalChecked;
	public int maxCount;
	public int cuerrentMemberCount;
	ArrayList<UserInfo> data;
	boolean isOwnerSelectionAllowed = true;
	public static class UserInfo implements Comparable{
		public String userName="";
		public UserInfo(String userName,String displayName, String displayNumber){
			this.displayName = displayName;
			this.displayNumber = displayNumber;
			this.userName = userName;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String displayName="";
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public String displayNumber="";
		public String getDisplayNumber() {
			return displayNumber;
		}
		public void setDisplayNumber(String displayNumber) {
			this.displayNumber = displayNumber;
		}
		@Override
		public int compareTo(Object another) {
				String tmpName = ((UserInfo)another).getDisplayName();
			return this.displayName.compareToIgnoreCase(tmpName);
		}
 }
	public GroupRoleCreationAdapter(Context context, int layoutResourceId){
		super(context, layoutResourceId);
	}
	public GroupRoleCreationAdapter(Context context1, int layout, ArrayList<UserInfo> data,int screenType)
	{
		super(context1,layout,data);
		context = context1;
		this.data = data;
		this.screenType =screenType;
		this.layout = layout;
		totalChecked = 0;
		checkedTagMap.clear();
		ownerCheckedMap.clear();
		if(screenType != Constants.GROUP_USERS_ROLE_SELECTION)
			checkedTagMap.put(SharedPrefManager.getInstance().getUserName(), true);
		else{
			ownerCheckedMap.put(SharedPrefManager.getInstance().getUserName(), true);
			checkedTagMap.put(SharedPrefManager.getInstance().getUserName(), true);
			}
		
		isOwnerSelectionAllowed = SharedPrefManager.getInstance().isDomainAdmin();
		
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
		
		for(UserInfo item: data){
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
	public void setMaxCount(int count){
		maxCount = count;
		totalChecked = 1;
	}
	public void setMemberCount(int count){
		cuerrentMemberCount = count;
		totalChecked = 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder viewholder = null;
		if(row == null)
		{
			row = LayoutInflater.from(context).inflate(layout, parent, false);
			viewholder = new ViewHolder();
			viewholder.image = (ImageView)row.findViewById(R.id.contact_icon);
			viewholder.imageDefault = (ImageView)row.findViewById(R.id.contact_icon_default);
			viewholder.userStatusView = (TextView)row.findViewById(R.id.id_contact_status);
			viewholder.displayNameView = (TextView)row.findViewById(R.id.id_contact_name);
			viewholder.crossView = (ImageView)row.findViewById(R.id.id_cross);
			viewholder.removeMemberView = (ImageView)row.findViewById(R.id.id_remove_member);
			viewholder.iCheckBox = (CheckBox) row.findViewById(R.id.contact_sel_box);
			viewholder.iOwnerChooseBox = (CheckBox) row.findViewById(R.id.id_owner_choose);
			
			//		if (isEditableContact){
//			viewholder.removeMemberView.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.crossView.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.image.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.displayNameView.setOnClickListener(viewholder.onCheckeClickListener);
//			viewholder.crossView.setOnClickListener(viewholder.onCheckeClickListener);
			viewholder.iCheckBox.setOnClickListener(viewholder.onCheckeClickListener);
			if(isOwnerSelectionAllowed)
				viewholder.iOwnerChooseBox.setOnClickListener(viewholder.onCheckeClickListener);
			else{
				viewholder.iOwnerChooseBox.setVisibility(View.GONE);
			}
//			if(screenType == Constants.GROUP_USER_CHAT_CREATE)
				row.setOnClickListener(viewholder.onCheckeClickListener);
			//		}
//			viewholder.id = cursor.getString(cursor.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
			row.setTag(viewholder);
		}
		else
		{
			viewholder = (ViewHolder)row.getTag();
		}
//		ViewHolder viewholder = (ViewHolder)view.getTag();
		UserInfo map = (UserInfo)getItem(position);
		viewholder.userNames = map.getUserName();//cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
//		String s = cursor.getString(cursor.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
		viewholder.displayName =map.getDisplayName();//cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));
		//		viewholder.voipumValue = cursor.getString(cursor.getColumnIndex(DatabaseConstants.VOPIUM_FIELD));
//		String s2 = cursor.getString(cursor.getColumnIndex(DatabaseConstants.IS_FAVOURITE_FIELD));
//		String compositeNumber = cursor.getString( cursor.getColumnIndex(DatabaseConstants.CONTACT_COMPOSITE_FIELD));
		if(viewholder.userNames.equals(viewholder.displayName))
			viewholder.displayName = SharedPrefManager.getInstance().getUserServerName(viewholder.displayName);
		if(viewholder.displayName == null || (viewholder.displayName != null && viewholder.displayName.equals("")) 
				|| viewholder.userNames.equals(viewholder.displayName))
			viewholder.displayName = "Superchatter";
		viewholder.displayNameView.setText(viewholder.displayName);
		if(screenType == Constants.GROUP_USER_CHAT_CREATE){
			viewholder.iCheckBox.setVisibility(View.VISIBLE);
			viewholder.iOwnerChooseBox.setVisibility(View.GONE);
			viewholder.removeMemberView.setVisibility(View.GONE);
			boolean isChecked = false;
			Object obj = checkedTagMap.get(viewholder.userNames);
			if (obj != null) {
				isChecked = checkedTagMap.get(viewholder.userNames);
			}else{
				checkedTagMap.put(viewholder.userNames,isChecked);
			}
			viewholder.iCheckBox.setChecked(isChecked);
			viewholder.iCheckBox.setTag(viewholder.userNames);
		}else if(screenType == Constants.GROUP_USERS_ROLE_SELECTION){
			viewholder.iCheckBox.setVisibility(View.VISIBLE);
			if(isOwnerSelectionAllowed)
				viewholder.iOwnerChooseBox.setVisibility(View.VISIBLE);
			viewholder.removeMemberView.setVisibility(View.GONE);
			boolean isChecked = false;
			Object obj = checkedTagMap.get(viewholder.userNames);
			if (obj != null) {
				isChecked = checkedTagMap.get(viewholder.userNames);
			}else{
				checkedTagMap.put(viewholder.userNames,isChecked);
			}
			viewholder.iCheckBox.setChecked(isChecked);
			
			 isChecked = false;
			Object obj1 = ownerCheckedMap.get(viewholder.userNames);
			if (obj1 != null) {
				isChecked = ownerCheckedMap.get(viewholder.userNames);
			}else{
				ownerCheckedMap.put(viewholder.userNames,isChecked);
			}
			viewholder.iOwnerChooseBox.setChecked(isChecked);
			
			viewholder.iCheckBox.setTag(viewholder.userNames);
			viewholder.iOwnerChooseBox.setTag(viewholder.userNames);
//			viewholder.iOwnerChooseBox.setChecked(true);
		}
		setProfilePic(viewholder.image, viewholder.imageDefault, viewholder.displayName, viewholder.userNames);
		if(SharedPrefManager.getInstance().isUserInvited(viewholder.userNames) && viewholder.displayNumber != null)
			viewholder.userStatusView.setText(viewholder.displayNumber);
		else
			viewholder.userStatusView.setText(SharedPrefManager.getInstance().getUserStatusMessage(viewholder.userNames));
		return row;
	}
	
	public class ViewHolder
	{
		String id;
		TextView displayNameView;
		TextView userStatusView;
		ImageView removeMemberView;
		ImageView crossView;
		String userStatus;
		String userNames;
		String displayName;
		String displayNumber;
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
						if(iCheckBox != null){
//							if(maxCount > 0 && totalChecked >= maxCount){
//								Toast.makeText(context, "Maximum "+maxCount+ " are allowed!", Toast.LENGTH_SHORT).show();
//								return;
//							}
							boolean checked = false;
							if(v.getId() == R.id.id_owner_choose){
								for(String key:ownerCheckedMap.keySet()){
									ownerCheckedMap.put(key, false);
								}
								iOwnerChooseBox.setChecked(true);
								ownerCheckedMap.put(userNames, true);
								notifyDataSetChanged();
								return;
							}
							if(v.getId() != R.id.contact_sel_box){
								checked = checkedTagMap.get(userNames);
								if(checked){
									if(SharedPrefManager.getInstance().isDomainAdmin() && userNames.equals(SharedPrefManager.getInstance().getUserName())){
										Toast.makeText(context, "You need to be part of this Official ID!", Toast.LENGTH_SHORT).show();
										return;
									}
									else
										iCheckBox.setChecked(!checked);
								}
								else{
									if(maxCount > 0 && (totalChecked + cuerrentMemberCount) == maxCount){
										if(cuerrentMemberCount > 0 )
											Toast.makeText(context, "Maximum "+maxCount+ " are allowed, You have already added "+cuerrentMemberCount+ " members!", Toast.LENGTH_SHORT).show();
										else
											Toast.makeText(context, "Maximum "+maxCount+ " are allowed!", Toast.LENGTH_SHORT).show();
										return;
									}
									iCheckBox.setChecked(!checked);
								}
							}else{
								checked = checkedTagMap.get(userNames);
								if(checked){
									if(SharedPrefManager.getInstance().isDomainAdmin() && userNames.equals(SharedPrefManager.getInstance().getUserName())){
										Toast.makeText(context, "You need to be part of this Official ID!", Toast.LENGTH_SHORT).show();
										iCheckBox.setChecked(true);
										return;
									}
									iCheckBox.setChecked(!checked);
								}else{
									if(maxCount > 0 && (totalChecked + cuerrentMemberCount) == maxCount){
										if(cuerrentMemberCount > 0 )
											Toast.makeText(context, "Maximum "+maxCount+ " are allowed, You have already added "+cuerrentMemberCount+ " members!", Toast.LENGTH_SHORT).show();
										else
											Toast.makeText(context, "Maximum "+maxCount+ " are allowed!", Toast.LENGTH_SHORT).show();
										iCheckBox.setChecked(false);
										return;
									}
								}
							}
							checkedTagMap.put(userNames, !checkedTagMap.get(userNames));
							if(checkedTagMap.get(userNames))
								totalChecked++;
							else
								totalChecked--;
							((EsiaChatContactsScreen)context).itemCountView.setText(totalChecked+" "+context.getString(R.string.selected));
							((EsiaChatContactsScreen)context).allSelectCheckBox.setChecked(false);
						}
				}};
	}
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
	private void setProfilePic(ImageView view, ImageView view_default, String displayName, String userName){
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		if (bitmap != null) {
			view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			RoundedImageView img = (RoundedImageView) view;
			img.setImageBitmap(bitmap);
		}else if(groupPicId!=null && !groupPicId.equals("")){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			File file1 = new File(filename);
			if(file1.exists()){
//				CompressImage compressImage = new CompressImage(context);
//				filename = compressImage.compressImage(filename);
//				view.setImageURI(Uri.parse(filename));
				view_default.setVisibility(View.INVISIBLE);
				view.setVisibility(View.VISIBLE);
				setThumb(view, filename,groupPicId);
//				view.setBackgroundDrawable(null);

			}
		}else{
//			if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
//				view.setImageResource(R.drawable.female_default);
//			else
//				view.setImageResource(R.drawable.male_default);
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
	public void showDialog(final String title, final String s, final String userNames) {
		final Dialog bteldialog = new Dialog(context);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(true);
		bteldialog.setContentView(R.layout.custom_dialog_two_button);
		if(title!=null){
			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
			}
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_send)).setText("Deactivate");
		((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				new MemberDeactivationTaskOnServer(userNames).execute();
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
}
