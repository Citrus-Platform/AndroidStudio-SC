package com.superchat.ui;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.superchat.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.SuperChatApplication;
import com.superchat.model.ProfileUpdateModel;
import com.superchat.model.RegMatchCodeModel;
import com.superchat.model.UserProfileModel;
import com.superchat.utils.AppUtil;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.ProfilePicDownloader;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.MyriadSemiboldTextView;
import com.superchat.widgets.RoundedImageView;

public class ContactViewScreen extends FragmentActivity implements OnClickListener{
	public static final String TAG = "ProfileScreen";
	TextView displayNameView;
	TextView saveContactView;
	EditText currentStatusView;
	EditText designationView;
	EditText departmentView;
	Button nextButtonView;
	SharedPrefManager iSharedPrefManager;
	boolean isForground;
	ArrayList<String> contactList;
	ArrayList<String> emailList;
	ArrayList<String> contactTypeList;
	ArrayList<String> emailTypeList;
	String displayName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.contact_view_screen);
		LinearLayout mainLayout = (LinearLayout)findViewById(R.id.id_contact_details_rlayout);
		displayNameView = (TextView)findViewById(R.id.id_display_name_field);
		saveContactView = (TextView)findViewById(R.id.id_save_contact);
		((TextView)findViewById(R.id.id_back_title)).setOnClickListener(this);
		((ImageView)findViewById(R.id.id_back_arrow)).setOnClickListener(this);
		iSharedPrefManager = SharedPrefManager.getInstance();
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			displayName = bundle.getString(Constants.CONTACT_NAME,"NoName");
			contactTypeList = bundle.getStringArrayList(Constants.CONTACT_TYPE_NUMBERS);
			emailTypeList = bundle.getStringArrayList(Constants.CONTACT_TYPE_EMAILS);
			contactList = bundle.getStringArrayList(Constants.CONTACT_NUMBERS);
			emailList = bundle.getStringArrayList(Constants.CONTACT_EMAILS);
		}
		saveContactView.setOnClickListener(this);
		displayNameView.setText(displayName);
//		nextButtonView.setOnClickListener(this);
//		boolean isPic = setProfilePic(userName);
//		if(!isPic){
//			String picId = SharedPrefManager.getInstance().getUserFileId(userName);
//			if(picId!=null && !picId.equals(""))
//			(new ProfilePicDownloader()).download(Constants.media_get_url+picId+".jpg",((RoundedImageView)findViewById(R.id.id_profile_pic)),null);
//		}
		
		if(contactList!=null){
			addSingleTextView(mainLayout,"PHONE");
			addTextView(mainLayout,contactList);
		}if(emailList!=null){
			addSingleTextView(mainLayout,"EMAIL");
			addTextView(mainLayout,emailList);
			}
	}
	private void addSingleTextView(LinearLayout mainLayout, String tmpText){
			MyriadSemiboldTextView textView = new MyriadSemiboldTextView(this);
			textView.setTag(tmpText);
			textView.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			textView.setGravity(Gravity.CENTER_HORIZONTAL);
			textView.setText(tmpText);
			textView.setBackgroundResource(R.color.gray);
			textView.setTextColor(Color.WHITE);
			textView.setTextSize(19);
			textView.setBottom(2);
			mainLayout.addView(textView);
			View view = new View(this);
			view.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1)));
			view.setBackgroundColor(Color.BLACK);
			mainLayout.addView(view);
	}
	private void addTextView(LinearLayout mainLayout, ArrayList<String> list){
		for(String text:list){
			String tmpText = text;
			MyriadSemiboldTextView textView = new MyriadSemiboldTextView(this);
			textView.setTag(text);
			textView.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)));
			textView.setText(tmpText);
//			textView.setBackgroundResource(R.drawable.round_rect);
			textView.setTextColor(Color.GRAY);
			textView.setTextSize(19);
			textView.setBottom(2);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			params.setMargins(20,20,20,20);
			textView.setLayoutParams(params);
//			textView.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//				}
//			});
			mainLayout.addView(textView);
			View view = new View(this);
			view.setLayoutParams((new   ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1)));
			view.setBackgroundColor(Color.GRAY);
			
			mainLayout.addView(view);
		}
	}
	InputFilter filter = new InputFilter() {
	    @Override
	    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
	        for (int i = start; i < end; i++) {
	            int type = Character.getType(source.charAt(i));
	            //System.out.println("Type : " + type);
	            if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
	                return "";
	            }
	        }
	        return null;
	    }
	};
	private String getImagePath(String groupPicId)
	{
		if(groupPicId == null)
		 groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName()); // 1_1_7_G_I_I3_e1zihzwn02
		if(groupPicId!=null){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
//			if(groupPicId != null && groupPicId.length() > 0 && groupPicId.lastIndexOf('/')!=-1)
//				profilePicUrl += groupPicId.substring(groupPicId.lastIndexOf('/'));
			
			return new StringBuffer(file.getPath()).append(File.separator).append("SuperChat/").append(profilePicUrl).toString();
		}
		return null;
	}
	private boolean setProfilePic(String userName){
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); 
		String img_path = getImagePath(groupPicId);
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		ImageView picView = (ImageView) findViewById(R.id.id_profile_pic);
		if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
			picView.setImageResource(R.drawable.female_default);
		else
			picView.setImageResource(R.drawable.male_default);
		if (bitmap != null) {
			picView.setImageBitmap(bitmap);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			picView.setTag(filename);
		}else if(img_path != null){
			File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				((ImageView) findViewById(R.id.id_profile_pic)).setImageURI(Uri.parse(img_path));
				setThumb((ImageView) picView,img_path,groupPicId);
				return true;
				}
		}
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
	public void onProfileImagePicClick(View view){
//		String file_path = AppUtil.capturedPath1;
//		if(file_path == null || (file_path != null && file_path.trim().length() == 0))
//			file_path = getImagePath(null);
//		if(file_path != null)
//		{
//			Intent intent = new Intent();
//			intent.setAction(Intent.ACTION_VIEW);
//			if(file_path.startsWith("http://"))
//				intent.setDataAndType(Uri.parse(file_path), "image/*");
//			else
//				intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
//			startActivity(intent);
//		}
//		else if(picChooserDialog!=null && !picChooserDialog.isShowing())
//			picChooserDialog.show();
		
	}
	public void onProfilePicClick(View view){
//		if(picChooserDialog!=null && !picChooserDialog.isShowing())
//			picChooserDialog.show();
		
	}
	
	public void onClick(View view){
		switch(view.getId()){
		case R.id.id_back_title:
		case R.id.id_back_arrow:
			finish();
		break;
		case R.id.id_save_contact:
//			Intent intent = new Intent(Intent.ACTION_INSERT,  ContactsContract.Contacts.CONTENT_URI);
//			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//			// Just two examples of information you can send to pre-fill out data for the
//			// user.  See android.provider.ContactsContract.Intents.Insert for the complete
//			// list.
//			intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName);
//			if(contactList != null && !contactList.isEmpty()){
//				for(String tmpPhone:contactList)
//					intent.putExtra(ContactsContract.Intents.Insert.PHONE, tmpPhone);
//			}
//			if(emailList != null && !emailList.isEmpty()){
//				for(String tmpEmail:emailList)
//					intent.putExtra(ContactsContract.Intents.Insert.EMAIL, tmpEmail);
//				}
//			startActivity(intent);
			
			Intent intent = new Intent(Intent.ACTION_INSERT);
			intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
			ArrayList<ContentValues> data = new ArrayList<ContentValues>();

			//Filling data with phone numbers
			if(contactList != null && !contactList.isEmpty())
				for(String tmpPhone:contactList){
			    ContentValues row = new ContentValues();
			    row.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			    row.put(Phone.NUMBER, tmpPhone);
//			    int type = Phone.TYPE_WORK;
//			    if(contactTypeList != null && !contactTypeList.isEmpty() && contactList.indexOf(tmpPhone)!=-1){
//			    	type = Integer.parseInt(contactTypeList.get(contactList.indexOf(tmpPhone)));
//			    }
			    row.put(Phone.TYPE, Phone.TYPE_MOBILE);
			    data.add(row);
			}
			//Filling data with phone numbers
			if(emailList != null && !emailList.isEmpty())
				for(String tmpEmail:emailList){
			    ContentValues row = new ContentValues();
			    row.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			    row.put(Email.ADDRESS, tmpEmail);
			    row.put(Phone.TYPE, Phone.TYPE_HOME);
			    data.add(row);
			}
			intent.putExtra(ContactsContract.Intents.Insert.NAME, displayName);
			intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
			startActivity(intent);
			break;
		case R.id.id_next_btn:
			if(isProfileValid()){
				
				new UpdateProfileTaskOnServer().execute();
			}
			
			break;
		}
	}
	private boolean isProfileValid() {
		
		if( displayNameView.getText().toString().trim().equals("")){
			showDialog(getString(R.string.display_name),getString(R.string.please_enter_name));
			return false;
		}
		
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (resultCode == HomeScreen.RESULT_OK)
				switch (requestCode) {
				case AppUtil.POSITION_CAMERA_PICTURE:
				case AppUtil.POSITION_GALLRY_PICTURE:
					if (data != null && data.getData() != null) 
					{
						Uri uri = data.getData();
						AppUtil.capturedPath1 = AppUtil.getPath(uri, this);
					}
					CompressImage compressImage = new CompressImage(this);
					AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
					performCrop(AppUtil.PIC_CROP);
					break;
				case AppUtil.PIC_CROP:
					String filePath= Environment.getExternalStorageDirectory()
							+"/"+Constants.contentTemp+"/"+AppUtil.TEMP_PHOTO_FILE;

					AppUtil.capturedPath1 = filePath ;
					 Bitmap selectedImage =  BitmapFactory.decodeFile(AppUtil.capturedPath1);
					 ((ImageView) findViewById(R.id.id_profile_pic)).setImageBitmap(selectedImage);
						((ImageView) findViewById(R.id.id_profile_pic)).setScaleType(ImageView.ScaleType.CENTER_CROP);
//					((ImageView) findViewById(R.id.id_profile_pic)).setImageURI(Uri.parse(filePath));
//					((ImageView) findViewById(R.id.id_profile_pic)).setScaleType(ImageView.ScaleType.CENTER_CROP);
					sendProfilePictureMessage(AppUtil.capturedPath1);
					break;
				}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}
	private void performCrop(byte resultCode) {
		try {
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			File file = new File(AppUtil.capturedPath1);
			Uri outputFileUri = Uri.fromFile(file);
//			System.out.println("----outputFileUri:" + outputFileUri);
			cropIntent.setDataAndType(outputFileUri, "image/*");
			cropIntent.putExtra("outputX", 300);
			cropIntent.putExtra("outputY", 300);
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("scale", true);
			try {
				cropIntent.putExtra("return-data", false);
				cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getTempUri());
				cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());	
				startActivityForResult(cropIntent, resultCode);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		} catch (ActivityNotFoundException anfe) {
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	public void sendProfilePictureMessage(final String imgPath) {
		String packetID = null;
		if(imgPath != null && imgPath.length() > 0)
		{
			try
			{
				String thumbImg = null;
//				if (messageService != null){
//					chatAdapter.setChatService(messageService);
//					thumbImg = MyBase64.encode(getByteArrayOfThumbnail(imgPath));
//					packetID = messageService.sendMediaMessage(senderName, "", imgPath,thumbImg,XMPPMessageType.atMeXmppMessageTypeGroupImage);
				new ProfilePicUploader(this, null,true,notifyPhotoUploadHandler).execute(imgPath, packetID,"",XMPPMessageType.atMeXmppMessageTypeImage.name(), iSharedPrefManager.getUserName());
//				}
			}catch(Exception ex)
			{
				showDialog(getString(R.string.failed),getString(R.string.photo_upload_failed));
//				System.out.println(""+ex.toString());
			}
		}
	}
	private final Handler notifyPhotoUploadHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
	    	if(isForground)
	    		showDialog(getString(R.string.failed),getString(R.string.photo_upload_failed));
	    }
	};
	public void onResume(){
		super.onResume();
		isForground = true;
	}
	public void onPause(){
		super.onPause();
		isForground = false;
	}
	private class UpdateProfileTaskOnServer extends AsyncTask<String, String, String> {
		ProgressDialog dialog = null;
		@Override
		protected void onPreExecute() {	
			dialog = ProgressDialog.show(ContactViewScreen.this, "","Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {
			
			final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
			ProfileUpdateModel model = new ProfileUpdateModel();
			

				model.setUserId(String.valueOf(iPrefManager.getUserId()));
//				model.setName(displayNameView.getText().toString());
				String statusText = currentStatusView.getText().toString().trim();
				if(statusText==null || statusText.equals("")){
					statusText = getString(R.string.status_hint);
				}
				model.setCurrentStatus(statusText);
				String fileId = iPrefManager.getUserFileId(iPrefManager.getUserName());
				if(fileId!=null && !fileId.equals("")){
					if(fileId.contains("."))
						model.setImageFileId(fileId.substring(0, fileId.indexOf(".")));
					else
						model.setImageFileId(fileId);
					}
			    String JSONstring = new Gson().toJson(model);
			    
			    DefaultHttpClient client1 = new DefaultHttpClient();
			    
				Log.d(TAG, "updateProfileTaskOnServer request:"+JSONstring);
				
				 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/jakarta/rest/user/profileupdate");
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
				 HttpResponse response = null;
		         try {
					httpPost.setEntity(new StringEntity(JSONstring));
					 try {
						 response = client1.execute(httpPost);
						 final int statusCode=response.getStatusLine().getStatusCode();
						 if (statusCode == HttpStatus.SC_OK){
							 HttpEntity entity = response.getEntity();
							 String result = EntityUtils.toString(entity);
		//					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
//							    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
//					            String line = "";
//					            while ((line = rd.readLine()) != null) {
					            	Log.d(TAG, "updateProfileTaskOnServer response: "+result);
//					            }
					            Gson gson = new GsonBuilder().create();
								final RegMatchCodeModel objResponseModel = gson.fromJson(result,RegMatchCodeModel.class);

								if (objResponseModel!=null && objResponseModel.iStatus != null){
									if(objResponseModel.iStatus.equalsIgnoreCase("success")) {
//										iSharedPrefManager.saveDisplayName(model.getName());
										iSharedPrefManager.saveUserStatusMessage(iSharedPrefManager.getUserName(),model.getCurrentStatus());
										iSharedPrefManager.saveUserFileId(iSharedPrefManager.getUserName(), model.imageFileId);
										iSharedPrefManager.setProfileAdded(iSharedPrefManager.getUserName(),true);
							            Intent intent = new Intent(ContactViewScreen.this, HomeScreen.class);
										startActivity(intent);
										finish();
									}else{
										if(objResponseModel.iMessage!=null)
										showDialog(null,objResponseModel.iMessage);
										else
											showDialog(null,getString(R.string.lbl_server_not_responding));
										}
									}
							}
					} catch (ClientProtocolException e) {
						Log.d(TAG, "updateProfileTaskOnServer during HttpPost execution ClientProtocolException:"+e.toString());
					} catch (IOException e) {
						Log.d(TAG, "updateProfileTaskOnServer during HttpPost execution ClientProtocolException:"+e.toString());
					}
					 
				} catch (UnsupportedEncodingException e1) {
					Log.d(TAG, "updateProfileTaskOnServer during HttpPost execution UnsupportedEncodingException:"+e1.toString());
				}catch(Exception e){
					Log.d(TAG, "updateProfileTaskOnServer during HttpPost execution Exception:"+e.toString());
				}
			
			
		    
			
			return null;
		}

		@Override
		protected void onPostExecute(String response) {

			super.onPostExecute(response);
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
		}
	}
	private void getServerUserProfile(String userId){
		final Context context = this;

		AsyncHttpClient client = new AsyncHttpClient();
		client = SuperChatApplication.addHeaderInfo(client,true);
		client.get(Constants.SERVER_URL+"/tiger/rest/user/profile/get?userName="+userId,
				null, new AsyncHttpResponseHandler() {
			ProgressDialog dialog = null;

			@Override
			public void onStart() {
				dialog = ProgressDialog.show(ContactViewScreen.this, "","Loading. Please wait...", true);
				Log.d(TAG, "AsyncHttpClient onStart: ");
			}

			@Override
			public void onSuccess(int arg0, String arg1) {
				Log.d(TAG, "AsyncHttpClient onSuccess: "
						+ arg1);

				Gson gson = new GsonBuilder().create();
				UserProfileModel objUserModel = gson.fromJson(arg1, UserProfileModel.class);
				if (arg1 == null || arg1.contains("error")){ 
					if (dialog != null) {
						dialog.dismiss();
						dialog = null;
					}
					return;
					}
//				SharedPrefManager.getInstance().saveUserName(objUserModel.iUserBaseApi.iName);
//				SharedPrefManager.getInstance().saveUserPhone(objUserModel.iUserBaseApi.iMobileNumber);
//				SharedPrefManager.getInstance().saveUserEmail(objUserModel.iUserBaseApi.iEmail);

				String mobileNumber = iSharedPrefManager.getUserPhone();
				String userName = iSharedPrefManager.getUserName();

				iSharedPrefManager.saveDisplayName(objUserModel.iName);
				iSharedPrefManager.saveUserStatusMessage(userName,objUserModel.currentStatus);
				displayNameView.setText(iSharedPrefManager.getDisplayName());
				currentStatusView.setText(iSharedPrefManager.getUserStatusMessage(userName));
				if(objUserModel.imageFileId!=null && !objUserModel.imageFileId.equals("")){
					SharedPrefManager.getInstance().saveUserFileId(SharedPrefManager.getInstance().getUserName(),objUserModel.imageFileId);
					boolean isPic = setProfilePic(SharedPrefManager.getInstance().getUserName());
					if(!isPic)
					(new ProfilePicDownloader()).download(Constants.media_get_url+objUserModel.imageFileId+".jpg",((RoundedImageView)findViewById(R.id.id_profile_pic)),null);
				}
//				if(mobileNumber != null && !mobileNumber.equals("")){
//					String tmpNumber = "+"+mobileNumber.replace("-", "");
//					if(tmpNumber.contains("++"))
//						tmpNumber = tmpNumber.replace("++", "+");
//					topHeaderNumText.setText(tmpNumber);
//					userMobText.setText(tmpNumber);
//				}
//				if(userName != null && !userName.equals(""))
//					userNameText.setText(userName);
//				topHeaderBalanceText.setText("Balance: Rp.00.000");

				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				super.onSuccess(arg0, arg1);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				Log.d(TAG, "AsyncHttpClient onFailure: "+ arg1);
				if (dialog != null) {
					dialog.dismiss();
					dialog = null;
				}
				//						showDialog("Please try again later.");
				super.onFailure(arg0, arg1);
			}
		});
	}
	public void showDialog(String title, String s) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		if(title!=null)
			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
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
}
