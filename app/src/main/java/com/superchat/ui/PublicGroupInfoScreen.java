package com.superchat.ui;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.model.ErrorModel;
import com.superchat.model.LoginModel;
import com.superchat.model.LoginResponseModel;
import com.superchat.ui.PublicGroupAdapter.ChannelLeaveJoinTaskOnServer;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.ProfilePicDownloader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PublicGroupInfoScreen extends Activity implements OnClickListener{
	TextView membersCountView;
	TextView channelOwnerView;
	TextView channelDescriptionView;
	TextView channelTitleView;
	TextView backView;
	ImageView joinLeaveBtnView;
	ImageView publicImageView;
	public static final String CHANNEL_TITLE = "channel_title";
	public static final String MEMBERS_COUNT_ID = "members_count_id";
	public static final String CHANNEL_OWNER = "channel_owner";
	public static final String CHANNEL_DESCRIPTION = "channel_description";
	public static final String CHANNEL_MEMBER_TYPE = "channel_member_type";
	public static final String CHANNEL_NAME = "channel_name";
	public static final String CHANNEL_PIC_ID = "channel_ic_id";
	String membersCount;
	String ownerName ="";
	String channelDescription ="";
	String channelTitle ="";
	String channelName ="";
	String memberType ="USER";
	String channelPicId = null;
	ArrayList<String> usersList = new ArrayList<String>();
    ArrayList<String> usersDisplayList = new ArrayList<String>();
    HashMap<String, String> nameMap = new HashMap<String, String>();
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.public_group_info_screen);
		membersCountView = (TextView)findViewById(R.id.id_member_counts);
		channelOwnerView = (TextView)findViewById(R.id.id_channel_owner);
		channelDescriptionView = (TextView)findViewById(R.id.id_channel_description);
		channelTitleView = (TextView)findViewById(R.id.header_txt);
		backView = (TextView)findViewById(R.id.help_back);
		joinLeaveBtnView = (ImageView)findViewById(R.id.id_join_leave_btn);
		publicImageView = (ImageView)findViewById(R.id.id_public_image);
		publicImageView.setOnClickListener(this);
		Bundle extras = getIntent().getExtras();
		if(extras!=null) {
			channelTitle = extras.getString(CHANNEL_TITLE, "SuperChat");
			membersCount = extras.getString(MEMBERS_COUNT_ID, "0");
			ownerName = extras.getString(CHANNEL_OWNER, "");
			channelDescription = extras.getString(CHANNEL_DESCRIPTION, "");
			memberType = extras.getString(CHANNEL_MEMBER_TYPE, "USER");
			channelName = extras.getString(CHANNEL_NAME, "");
			channelPicId = extras.getString(CHANNEL_PIC_ID, null);
			
		}
		if(memberType!=null && memberType.equals("USER")){
			joinLeaveBtnView.setImageResource(R.drawable.join_btn);
		} else if(memberType!=null && !memberType.equals("OWNER")){
			joinLeaveBtnView.setImageResource(R.drawable.leave_btn);
		}else
			joinLeaveBtnView.setVisibility(View.GONE);
		backView.setOnClickListener(this);
		joinLeaveBtnView.setOnClickListener(this);
		channelTitleView.setText(channelTitle);
		membersCountView.setText("Members ("+membersCount+")");
		channelOwnerView.setText(ownerName);
		channelDescriptionView.setText(channelDescription);
		setPic(publicImageView, channelName);
	}
	private void setPic(ImageView view, String id){
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(id); // 1_1_7_G_I_I3_e1zihzwn02
		android.graphics.Bitmap bitmap = null;//SuperChatApplication.getBitmapFromMemCache(groupPicId);
		if (bitmap != null) {
			view.setVisibility(View.VISIBLE);
			view.setImageBitmap(bitmap);
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			view.setTag(filename);
		}else if(channelPicId!=null && channelPicId.trim().length() > 0){
			String profilePicUrl = channelPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
			//			if(groupPicId != null && groupPicId.length() > 0 && groupPicId.lastIndexOf('/')!=-1)
			//				profilePicUrl += groupPicId.substring(groupPicId.lastIndexOf('/'));

			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			File file1 = new File(filename);
			//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
			if(file1.exists()){
//				view.setImageURI(Uri.parse(filename));
				view.setVisibility(View.VISIBLE);
//				setThumb(view, filename,groupPicId);
				try{
					view.setImageURI(Uri.parse(filename));
		    	}catch(Exception e){
		    		
		    	}
				view.setTag(filename);
			}else{
				//Downloading the file
				view.setVisibility(View.VISIBLE);
//				view.setImageResource(R.drawable.group_icon);
				if(view instanceof RoundedImageView)
					(new ProfilePicDownloader()).download(Constants.media_get_url+channelPicId+".jpg", (RoundedImageView)view, null);
			}
		}
	}
	private void setThumb(ImageView imageViewl,String path,String groupPicId){
		BitmapFactory.Options bfo = new BitmapFactory.Options();
	    bfo.inSampleSize = 1;
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
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.id_public_image:
			String file_path = (String) v.getTag();
			if(file_path != null)
			{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				if(file_path.startsWith("http://"))
					intent.setDataAndType(Uri.parse(file_path), "image/*");
				else
					intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
				startActivity(intent);
			}
			break;
			case R.id.id_join_leave_btn:
				if(memberType.equals("USER")){  // JOIN
//					showDialog("Join "+channelTitle,"Do you want to join "+channelTitle+".",true);
					if(Build.VERSION.SDK_INT >= 11)
						new ChannelLeaveJoinOnInfo(true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,channelName);
					else
						new ChannelLeaveJoinOnInfo(true).execute(channelName);
				} else if(!memberType.equals("OWNER")){  // Leave
//					showDialog("Leave "+channelTitle,"Do you want to leave "+channelTitle+".",false);
					if(Build.VERSION.SDK_INT >= 11)
						new ChannelLeaveJoinOnInfo(false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,channelName);
					else
						new ChannelLeaveJoinOnInfo(false).execute(channelName);
				}
				break;
			case R.id.help_back:
				finish();
				break;
		}
	}
	public void showDialog(final String title, final String s,final boolean isJoinning) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(true);
		bteldialog.setContentView(R.layout.custom_dialog_two_button);
		if(title!=null){
			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
			}
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		
		if(isJoinning){
			((TextView)bteldialog.findViewById(R.id.id_send)).setText("Join");
		}else
			((TextView)bteldialog.findViewById(R.id.id_send)).setText("Leave");
		((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				if(isJoinning){
					if(Build.VERSION.SDK_INT >= 11)
						new ChannelLeaveJoinOnInfo(isJoinning).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,channelName);
					else
						new ChannelLeaveJoinOnInfo(isJoinning).execute(channelName);
				}else{
					if(Build.VERSION.SDK_INT >= 11)
						new ChannelLeaveJoinOnInfo(isJoinning).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,channelName);
					else
						new ChannelLeaveJoinOnInfo(isJoinning).execute(channelName);
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
	public class ChannelLeaveJoinOnInfo extends AsyncTask<String, String, String> {
		LoginModel loginForm;
		ProgressDialog progressDialog = null;
		SharedPrefManager sharedPrefManager;
		boolean isJoinning;
		public ChannelLeaveJoinOnInfo(boolean isJoinning){
			sharedPrefManager = SharedPrefManager.getInstance();
			loginForm = new LoginModel();
			loginForm.setUserName(sharedPrefManager.getUserName());
			loginForm.setPassword(sharedPrefManager.getUserPassword());
			loginForm.setToken(sharedPrefManager.getDeviceToken());
			this.isJoinning = isJoinning;
		}
		@Override
		protected void onPreExecute() {
				progressDialog = ProgressDialog.show(PublicGroupInfoScreen.this, "", "Request processing. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
//			String JSONstring = new Gson().toJson(loginForm);
			String url = "";
			if(params!=null && params.length>0){
				String query = params[0];
				try {
					 query = URLEncoder.encode(query, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(isJoinning){
					url = "/join?groupName="+query;
				}else{
					url = "/leave?groupName="+query;
				}
			}
		    DefaultHttpClient client1 = new DefaultHttpClient();
		    
//		    http://52.88.175.48/tiger/rest/group/leave?groupName=qa_p5domain
//		    http://52.88.175.48/tiger/rest/group/join?groupName=qa_p5domain
		    
			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/group/"+url);
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
				            	return str;
								
									
									
				            
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
				if (str!=null && str.contains("\"status\":\"success\"")){
					Gson gson = new GsonBuilder().create();
					ErrorModel errorModel = gson.fromJson(str,ErrorModel.class);
					PublicGroupScreen.updateDataLocally(channelName,isJoinning);
					if(isJoinning){
						memberType = "MEMBER";
						joinLeaveBtnView.setImageResource(R.drawable.leave_btn);
						int members = 0;
						try{
							members = Integer.parseInt(membersCount);
							members++;						
						}catch(NumberFormatException e){}
						membersCountView.setText("Members ("+members+")");
						//Finish and switch to next page
						sharedPrefManager = SharedPrefManager.getInstance();
						usersList = sharedPrefManager.getGroupUsersList(channelName);
						Intent intent = new Intent(PublicGroupInfoScreen.this, GroupProfileScreen.class);
				            intent.putStringArrayListExtra(Constants.GROUP_USERS, usersList);
				            intent.putExtra(Constants.USER_MAP, nameMap);
				            intent.putExtra(Constants.CHAT_USER_NAME, channelName);
				            intent.putExtra(Constants.OPEN_CHANNEL, true);
				            intent.putExtra(Constants.CHAT_NAME, channelTitle);
				            startActivityForResult(intent, 300);
				            finish();
					}else{
						memberType = "USER";
						joinLeaveBtnView.setImageResource(R.drawable.join_btn);
						int members = 0;
						try{
							
								members = Integer.parseInt(membersCount);
								if(members>0){
									members--;
								}
						}catch(NumberFormatException e){}
						membersCountView.setText("Members ("+members+")");
					}
//					if(errorModel!=null)
//						showDialog(errorModel.message);
					}
			}
			super.onPostExecute(str);
		}
	}
	private List<String> convertNames(List<String> arrayList){
		ArrayList<String> displayList = new ArrayList<String>();
//		hashMap = DBWrapper.getInstance().getUsersDisplayNameList(arrayList);
		for(String tmp:arrayList){
			String value = tmp;//DBWrapper.getInstance().getChatName(tmp);
			if(SharedPrefManager.getInstance().getUserName().equals(tmp))
				value = "You";
			if(value!=null && value.contains("#786#")){
//				String tUserName = value.substring(value.indexOf("#786#")+"#786#".length());
				value = value.substring(0, value.indexOf("#786#"));
//				hashMap.put(tUserName, value);
			}
			displayList.add(value);
		}
		Collections.sort(displayList, String.CASE_INSENSITIVE_ORDER);
		return displayList;
	}
	public void showDialog(String s) {
		final Dialog bteldialog = new Dialog(PublicGroupInfoScreen.this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(false);
		bteldialog.setContentView(R.layout.custom_dialog);
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				bteldialog.cancel();
//				finish();
				return false;
			}
		});
		bteldialog.show();
	}
}
