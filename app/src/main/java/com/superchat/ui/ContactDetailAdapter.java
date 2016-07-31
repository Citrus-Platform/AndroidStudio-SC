package com.superchat.ui;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.superchat.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.ContactUpDatedModel;
import com.superchat.model.ContactUploadModel;
import com.superchat.model.ContactUpDatedModel.UserDetail;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
public class ContactDetailAdapter {//extends CursorAdapter {
//	static String contactName;
//	static Context context;
//	private static final String TAG = "ContactDetailAdapter";
//	private static final class ContactDetailViewHolder {
//
//		ImageView contactStatusIcon;
//		ImageView contactIcon;
//		TextView contactTypeView;
//		TextView contactNumberView;
//		TextView contactStatusMessageView;
//		TextView contactStatusTitleView;
//		TextView contactAddedTimeView;
//		
//		String contactType;
//		String contactNumber;
//		String userName;
//		String contactStatusMessage;
//		String contactAddedTime;
//		String contactId;
//		boolean isContactAdded;
//		
//		private ContactDetailViewHolder() {
//		}
//
//		public OnClickListener onMessageClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (isContactAdded) {
//					if(v.getId() == R.id.id_contact_icon){
//						String file_path = (String) v.getTag();
//						if(file_path != null)
//						{
//							Intent intent = new Intent();
//							intent.setAction(Intent.ACTION_VIEW);
//							if(file_path.startsWith("http://"))
//								intent.setDataAndType(Uri.parse(file_path), "image/*");
//							else
//								intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
//							((ContactDetails) context).startActivity(intent);
//						}
//						return;
//					}
//					if(HomeScreen.calledForShare){
//						String type = "Voice";
//						switch(HomeScreen.sharingType){
//						case HomeScreen.VIDEO_SHARING:
//							type = "video message";
//							break;
//						case HomeScreen.VOICE_SHARING:
//							type = "Voice message";
//							break;
//						case HomeScreen.IMAGE_SHARING:
//							type = "picture message";
//							break;
//						case HomeScreen.PDF_SHARING:
//							type = "PDF file";
//							break;
//						}
//						showDialog(contactName,"You are sharing "+type+".");
//						return;
//					}
//					 Intent intent = new Intent(SuperChatApplication.context,ChatListScreen.class);
//					 intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,
//					 contactName);
//					 intent.putExtra(DatabaseConstants.USER_NAME_FIELD,userName);
//					 ((ContactDetails)context).startActivity(intent);
//				}else{
//					//do invite here
//					
//					List<String> list = new ArrayList<String>();
//					list.add(contactNumber);
//					serverUpdateContactsInfo(null,list,contactName);
//					
////					try {
////						String text = EsiaChatApplication.context.getResources().getString(R.string.invite_text);
////						Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
////						shareIntent.setType("text/plain");
////						shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);    
////						((ContactDetails) context).startActivity(Intent.createChooser(shareIntent, EsiaChatApplication.context.getString(R.string.invite)));
////					} catch (Exception e) {
////						//				        Toast.makeText(KainatInviteSelection.this, "Facebook not Installed", Toast.LENGTH_SHORT).show();
////					}  
//				}
//			}
//		};
//		public void showDialog(String title, String s) {
//			final Dialog bteldialog = new Dialog(context);
//			bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			bteldialog.setCanceledOnTouchOutside(false);
//			bteldialog.setContentView(R.layout.custom_dialog_two_button);
//			if(title!=null){
//				if(title!=null && title.contains("#786#"))
//					title = title.substring(0, title.indexOf("#786#"));
//				((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
//				}
//			((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
//			((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
//				
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					bteldialog.cancel();
//					 Intent intent = new Intent(SuperChatApplication.context,ChatListScreen.class);
//					 intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,
//					 contactName);
//					 intent.putExtra(DatabaseConstants.USER_NAME_FIELD,userName);
//					 ((ContactDetails)context).startActivity(intent);
//					return false;
//				}
//			});
//			((TextView)bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {
//				
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					bteldialog.cancel();
//					HomeScreen.calledForShare = false;
//					return false;
//				}
//			});
//			bteldialog.show();
//		}
//	}
//
//	public ContactDetailAdapter(Context context, Cursor cursor, boolean flag, String contactName) {
//		super(context, cursor, false);
//		this.contactName = contactName;
//		this.context =context;
//	}
//
//	public void bindView(View view, Context context1, Cursor cursor) {
//		ContactDetailViewHolder contactHolder = (ContactDetailViewHolder)view.getTag();
//		int phone_type = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD));
//		contactHolder.contactNumber = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NUMBERS_FIELD));
//		String displayNumber = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DISPLAY_NUMBERS_FIELD));
//		contactHolder.contactNumberView.setText(displayNumber);
//		contactHolder.userName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
//		int contactStatus = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.VOPIUM_FIELD));
//		if(contactStatus == 0)
//			contactHolder.isContactAdded = false;
//		else
//			contactHolder.isContactAdded = true;
//		switch(phone_type){
//		case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//			contactHolder.contactType = "MOBILE";
//			break;
//		case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//			contactHolder.contactType = "HOME";
//			break;
//		case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//			contactHolder.contactType = "WORK";
//			break;
//		case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
//			contactHolder.contactType = "OTHER";
//			break;
//		}
//		
//		
//		contactHolder.contactTypeView.setText(contactHolder.contactType);
//		if(contactHolder.isContactAdded){
//			contactHolder.contactStatusMessageView.setVisibility(TextView.VISIBLE);
//			contactHolder.contactStatusTitleView.setVisibility(TextView.VISIBLE);
//			contactHolder.contactAddedTimeView.setVisibility(TextView.VISIBLE);
//			contactHolder.contactStatusIcon.setBackgroundResource(R.drawable.added_to_talk);
//			contactHolder.contactStatusMessageView.setText(SharedPrefManager.getInstance().getUserStatusMessage(contactHolder.userName));
//			setProfilePic(contactHolder.contactIcon,contactHolder.userName);
//		}else{
//			contactHolder.contactStatusMessageView.setVisibility(TextView.GONE);
//			contactHolder.contactStatusTitleView.setVisibility(TextView.GONE);
//			contactHolder.contactAddedTimeView.setVisibility(TextView.GONE);
//			contactHolder.contactStatusIcon.setBackgroundResource(R.drawable.add_to_esia_talk);
//		}
//	}
//
//	public long getItemId(int i) {
//		return (long) i;
//	}
//
//	public boolean hasStableIds() {
//		return false;
//	}
//
//	public boolean isEmpty() {
//		return getCount() == 0;
//	}
//
//	public View newView(Context context1, Cursor cursor, ViewGroup viewgroup) {
//		LayoutInflater layoutinflater = (LayoutInflater)context1.getSystemService("layout_inflater");
//		ContactDetailViewHolder contactHolder = new ContactDetailViewHolder();
//		 View view = layoutinflater.inflate(R.layout.contact_details_item, viewgroup, false);
//		    contactHolder.contactNumberView = (TextView)view.findViewById(R.id.id_contact_number);
//	        contactHolder.contactStatusMessageView = (TextView)view.findViewById(R.id.id_status_message);
//	        contactHolder.contactAddedTimeView = (TextView)view.findViewById(R.id.id_status_time);
//	        contactHolder.contactTypeView = (TextView)view.findViewById(R.id.id_contact_type);
//	        contactHolder.contactStatusTitleView = (TextView)view.findViewById(R.id.id_status_title);
//	        contactHolder.contactStatusIcon = (ImageView)view.findViewById(R.id.id_esia_contact_icon);
//	        contactHolder.contactIcon = (ImageView)view.findViewById(R.id.id_contact_icon);
//	        view.setOnClickListener(contactHolder.onMessageClickListener);
//	        contactHolder.contactIcon.setOnClickListener(contactHolder.onMessageClickListener);
//	        view.setTag(contactHolder);
//	        return view;
//	}
//	private void setProfilePic(ImageView view, String userName){
//		String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02
//		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
//		if (bitmap != null) {
//			view.setImageBitmap(bitmap);
//			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
//			File file = Environment.getExternalStorageDirectory();
//			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
//			view.setTag(filename);
//		}else if(groupPicId!=null && !groupPicId.equals("")){
//			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
//			File file = Environment.getExternalStorageDirectory();
//			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
//			view.setTag(filename);
//			File file1 = new File(filename);
//			if(file1.exists()){
//				CompressImage compressImage = new CompressImage(context);
////				filename = compressImage.compressImage(filename);
////				view.setImageURI(Uri.parse(filename));
//				setThumb(view,filename,groupPicId);
//				view.setBackgroundDrawable(null);
//
//				}
//		}else
//			view.setImageResource(R.drawable.avatar);
//	}
//	public static Bitmap rotateImage(String path, Bitmap bm) {
//		int orientation = 1;
//	try {
//		ExifInterface exifJpeg = new ExifInterface(path);
//		  orientation = exifJpeg.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//////			orientation = Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
//		} catch (IOException e) {
//			e.printStackTrace();
//	}
//	if (orientation != ExifInterface.ORIENTATION_NORMAL)
//	{
//		int width = bm.getWidth();
//		int height = bm.getHeight();
//		Matrix matrix = new Matrix();
//		if (orientation == ExifInterface.ORIENTATION_ROTATE_90) 
//		{
//			matrix.postRotate(90);
//		} 
//		else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//			matrix.postRotate(180);
//		} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//			matrix.postRotate(270);
//		}
//		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
//		}
//			
//		return bm;
//	}
//	private void setThumb(ImageView imageViewl,String path, String groupPicId){
//		BitmapFactory.Options bfo = new BitmapFactory.Options();
//	    bfo.inSampleSize = 2;
//	    Bitmap bm = null;
//	    try{
//		    bm = BitmapFactory.decodeFile(path, bfo);
//		    bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
//		    bm = rotateImage(path, bm);
//		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
//	    }catch(Exception ex){
//	    	
//	    }
//	    if(bm!=null){
//	    	imageViewl.setImageBitmap(bm);
//	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
//	    }else{
//	    	try{
//	    		imageViewl.setImageURI(Uri.parse(path));
//	    	}catch(Exception e){
//	    		
//	    	}
//	    }
//	}
//	private static void serverUpdateContactsInfo(final ContactsScreen obj,List<String> numbers, final String displayName){
//		final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//
////		List<String> emails =DBWrapper.getInstance().getAllEmails();
////		List<String> numbers = DBWrapper.getInstance().getAllNumbers();
//
//		ContactUploadModel model = new ContactUploadModel(
//				iPrefManager.getUserId(), null, numbers);
//		Log.d(TAG, "serverUpdateContactsInfo request:"+model.toString());
//		AsyncHttpClient client = new AsyncHttpClient();
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//		nameValuePairs.add(new BasicNameValuePair("form", new Gson()
//		.toJson(model)));
//		for (NameValuePair pair : nameValuePairs) {
//			Log.d(TAG, "serverUpdateContactsInfo login NameValuePair: " + pair.getName() + ":"
//					+ pair.getValue());
//		}
//		HttpEntity entity = null;
//		try {
//			entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		client.post(SuperChatApplication.context, Constants.SERVER_URL
//				+ "/jakarta/rest/contact/upload", entity, null,
//				new AsyncHttpResponseHandler() {
//			ProgressDialog dialog = null;
//			@Override
//			public void onStart() {
//				dialog = ProgressDialog.show(context, "","Loading. Please wait...", true);
//				Log.d(TAG, "AsyncHttpClient onStart: ");
//			}
//
//			@Override
//			public void onSuccess(int arg0, String arg1) {
//				Log.d(TAG, "serverUpdateContactsInfo onSuccess: "+ arg1);
//				Gson gson = new GsonBuilder().create();
//				if (arg1==null || arg1.contains("error")){
////					contactSyncState = CONTACT_SYNC_FAILED;
//					Log.d(TAG,
//							"serverUpdateContactsInfo onSuccess error comming : "
//									+ arg1);
//					if (dialog != null) {
//						dialog.dismiss();
//						dialog = null;
//					}
//					
//					callInvite();
//					return;
//				}
//
//				ContactUpDatedModel updatedModel = gson.fromJson(arg1,
//						ContactUpDatedModel.class);
//				if (updatedModel != null) {
//					UserDetail userDetail = null;
//					Log.d(TAG,
//							"serverUpdateContactsInfo onSuccess : Contact synced successful. ");
//					if(updatedModel.mobileNumberUserBaseMap != null)
//					for (String st : updatedModel.mobileNumberUserBaseMap
//							.keySet()) {
//						 userDetail = updatedModel.mobileNumberUserBaseMap
//								.get(st);
//						//						Log.d(TAG, "contacts sync info with sip address: " + userDetail.iSipAddress);
//						ContentValues contentvalues = new ContentValues();
////						contentvalues.put(
////								DatabaseConstants.USER_SIP_ADDRESS,
////								userDetail.iSipAddress);
//						contentvalues.put(
//								DatabaseConstants.USER_NAME_FIELD,
//								userDetail.userName);
//						contentvalues.put(
//								DatabaseConstants.VOPIUM_FIELD,
//								Integer.valueOf(1));
//						contentvalues
//						.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,
//								userDetail.mobileNumber);
//						
//						DBWrapper.getInstance().updateAtMeDirectStatus(contentvalues,DatabaseConstants.CONTACT_NUMBERS_FIELD);
//						DBWrapper.getInstance().updateAtMeContactDetails(contentvalues,userDetail.mobileNumber);
//						DBWrapper.getInstance().updateUserNameInContacts(userDetail.userName,userDetail.mobileNumber);
//						if(userDetail!=null && userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
//							if(iPrefManager.getUserFileId(userDetail.userName) == null || !iPrefManager.getUserFileId(userDetail.userName).equals(userDetail.imageFileId))
//								new BitmapDownloader().execute(userDetail.imageFileId);
//							}
//						if(iPrefManager!=null && userDetail!=null && userDetail.userName!=null){
//							iPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
//							iPrefManager.saveUserStatusMessage(userDetail.userName,  userDetail.currentStatus);
//						}
//					}
//					if(updatedModel.mobileNumberUserBaseMap == null || updatedModel.mobileNumberUserBaseMap.isEmpty())
//						callInvite();
//					else if(userDetail!=null){
//						Intent intent = new Intent(SuperChatApplication.context,ChatListScreen.class);
//						intent.putExtra(DatabaseConstants.USER_NAME_FIELD, userDetail.userName);
//						intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,displayName);
//						intent.putExtra("is_vopium_user", true);
//						((ContactDetails) context).startActivity(intent);
//						
//					}
////					for (String st : updatedModel.emailUserBaseMap
////							.keySet()) {
////						UserDetail userDetail = updatedModel.emailUserBaseMap
////								.get(st);
////						// Log.d(TAG,
////						// "contacts sync info with sip address: " +
////						// userDetail.iSipAddress);
////						ContentValues contentvalues = new ContentValues();
////						contentvalues.put(
////								DatabaseConstants.USER_SIP_ADDRESS,
////								userDetail.iSipAddress);
////						contentvalues.put(
////								DatabaseConstants.USER_NAME_FIELD,
////								userDetail.userName);
////						contentvalues.put(
////								DatabaseConstants.VOPIUM_FIELD,
////								Integer.valueOf(1));
////						contentvalues.put(
////								DatabaseConstants.PHONE_EMAILS_FIELD,
////								userDetail.email);
////						DBWrapper.getInstance().updateAtMeDirectStatus(contentvalues,DatabaseConstants.PHONE_EMAILS_FIELD);
////						DBWrapper.getInstance().updateAtMeContactDetails(contentvalues,userDetail.email);
////					}
////					if(obj!=null)
////						obj.notifyUpdate();
////					contactSyncState = CONTACT_SYNC_SUCESSED;
//				}
//				else
//					callInvite();
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				Log.d(TAG,
//						"AsyncHttpClient onSuccess : Contact synced successful. ");
//				super.onSuccess(arg0, arg1);
//			}
//
//			@Override
//			public void onFailure(Throwable arg0, String arg1) {
////				contactSyncState = CONTACT_SYNC_FAILED;
//				Log.d(TAG,
//						"serverUpdateContactsInfo onFailure: Contact sync has failed"
//								+ arg1);
//				if (dialog != null) {
//					dialog.dismiss();
//					dialog = null;
//				}
//				super.onFailure(arg0, arg1);
//				
//				callInvite();  
//			}
//		});
//	}
//	private static void callInvite(){
//		//do invite here
//		try {
//			String text = SuperChatApplication.context.getResources().getString(R.string.invite_text);
//			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//			shareIntent.setType("text/plain");
//			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);    
//			((ContactDetails) context).startActivity(Intent.createChooser(shareIntent, SuperChatApplication.context.getString(R.string.invite)));
//		} catch (Exception e) {
//			//				        Toast.makeText(KainatInviteSelection.this, "Facebook not Installed", Toast.LENGTH_SHORT).show();
//		}  
//	}
}
