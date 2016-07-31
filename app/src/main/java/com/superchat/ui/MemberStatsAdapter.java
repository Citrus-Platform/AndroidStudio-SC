package com.superchat.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.AddMemberModel;
import com.superchat.model.AddMemberModel.MemberDetail;
import com.superchat.model.AddMemberResponseModel;
import com.superchat.model.ErrorModel;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MemberStatsAdapter extends ArrayAdapter<MemberStatsAdapter.UserInfo>{
	Context context;
	int layout;
	int screenType;
	OnClickListener onClickListener;
	private static final String TAG = "BulkInvitationAdapter";
	public MemberStatsAdapter(Context context, int layoutResourceId){
		super(context, layoutResourceId);
	}
	public MemberStatsAdapter(Context context1, int layout, ArrayList<UserInfo> data,int screenType)
	{
		super(context1,layout,data);
		context = context1;
		this.layout = layout;
		this.screenType = screenType;
		if(mDrawableBuilder == null)
			mDrawableBuilder = TextDrawable.builder().beginConfig().toUpperCase().endConfig().round();
	}
	public static class UserInfo implements Comparable{
		public String userName="";
		public UserInfo(String userName, String displayName, String number){
			this.displayName = displayName;
			this.userName = userName;
			this.number = number;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String number="";
		public String getNumber() {
			return number;
		}
		public void setNumber(String number) {
			this.number = number;
		}
		public String displayName="";
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		@Override
		public int compareTo(Object another) {
				String tmpName = ((UserInfo)another).getDisplayName();
			return this.displayName.compareToIgnoreCase(tmpName);
		}
 }
	public void setClickListener(OnClickListener onClickListener){
		this.onClickListener =onClickListener;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder viewholder = null;
		if(row == null)
		{
			row = LayoutInflater.from(context).inflate(layout, parent, false);
			viewholder = new ViewHolder();
			viewholder.layout = (RelativeLayout)row.findViewById(R.id.id_member_stats_item);
			viewholder.displayNameView = (TextView)row.findViewById(R.id.id_contact_name);
			viewholder.mobileNumber = (TextView)row.findViewById(R.id.id_contact_status);
			viewholder.image = (ImageView)row.findViewById(R.id.contact_icon);
			viewholder.imageDefault = (ImageView)row.findViewById(R.id.contact_icon_default);
//			viewholder.userStatusView = (TextView)row.findViewById(R.id.id_contact_status);
			viewholder.statsCount = (TextView)row.findViewById(R.id.id_stats_count);
			viewholder.statsIcon = (ImageView)row.findViewById(R.id.stats_icon);
			viewholder.reInvite = (TextView)row.findViewById(R.id.id_re_invite_btn);
			
		}else
		{
			viewholder = (ViewHolder)row.getTag();
		}
		
		UserInfo info = (UserInfo)getItem(position);
		final String item = info.displayName;
		final String number = info.number;
		switch(screenType){
		case Constants.MEMBER_STATS:
			row.setOnClickListener(onClickListener);
			viewholder.image.setVisibility(ImageView.INVISIBLE);
			viewholder.imageDefault.setVisibility(ImageView.GONE);
			viewholder.statsIcon.setVisibility(ImageView.VISIBLE);
//			viewholder.userStatusView.setVisibility(TextView.GONE);
			
			viewholder.displayNameView.setText(item);
			viewholder.displayNameView.setTag(item);
			switch(position){
			case 0:
				int total = Integer.parseInt(SharedPrefManager.getInstance().getDomainUnjoinedCount())+Integer.parseInt(SharedPrefManager.getInstance().getDomainJoinedCount());
//				viewholder.statsCount.setText(SharedPrefManager.getInstance().getDomainJoinedCount());
				viewholder.statsCount.setText("("+total+")");
				break;
			case 1:
				viewholder.statsCount.setText("("+SharedPrefManager.getInstance().getDomainJoinedCount()+")");
				break;
			case 2:
				viewholder.statsCount.setText("("+SharedPrefManager.getInstance().getDomainUnjoinedCount()+")");
				break;
			}
			break;
			default:
				viewholder.statsCount.setVisibility(TextView.GONE);
				if(screenType == -11){
					viewholder.reInvite.setVisibility(TextView.VISIBLE);
					viewholder.layout.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							showPopupDialog(v, item, number);
						}
					});
				}
				viewholder.displayNameView.setText(item);
				if(number != null && viewholder.mobileNumber != null){
					viewholder.mobileNumber.setVisibility(View.VISIBLE);
					if(number.contains("-"))
						viewholder.mobileNumber.setText("+" + number.replace("-", ""));
					else
						viewholder.mobileNumber.setText(number);
				}
				
		}
		setProfilePic(viewholder.statsIcon,viewholder.image, viewholder.imageDefault, info.displayName, info.userName);
		row.setTag(viewholder);
		return row;
	}
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
	 private TextDrawable.IBuilder mDrawableBuilder;
	private void setProfilePic(ImageView statsView,ImageView view, ImageView view_default, String displayName, String userName){
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02
		android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
		if (bitmap != null) {
			view_default.setVisibility(View.INVISIBLE);
			view.setVisibility(View.VISIBLE);
			RoundedImageView img = (RoundedImageView) view;
			img.setImageBitmap(bitmap);
		}else if(groupPicId!=null && !groupPicId.equals("")  && !groupPicId.equals("clear")){
			String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
			File file = Environment.getExternalStorageDirectory();
//			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto+profilePicUrl;
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
		}else if(userName.equals(context.getString(R.string.invited_members))){
//			statsView.setImageResource(R.drawable.iconmemberinvitated);
			statsView.setBackgroundResource(R.drawable.members_invited);
		}else if(userName.equals(context.getString(R.string.joined_member))){
//			view_default.setVisibility(View.INVISIBLE);
//			view.setVisibility(View.VISIBLE);
			statsView.setImageResource(R.drawable.members_joined);
		}else if(userName.equals(context.getString(R.string.pending_invitation))){
//			view_default.setVisibility(View.INVISIBLE);
//			view.setVisibility(View.VISIBLE);
			statsView.setImageResource(R.drawable.pending_invitation);
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
	public class ViewHolder
	{
		RelativeLayout layout;
		String id;
		TextView displayNameView;
		TextView mobileNumber;
//		TextView userStatusView;
		TextView statsCount;
		String userStatus;
		String userNames;
		String displayName;
		ImageView imageDefault;
		ImageView image;
		TextView reInvite;
		ImageView  statsIcon;
		ViewHolder(){

		}
	}
	//=============================
	private class BulkInviteServerTask extends AsyncTask<String, String, String> {
		
		AddMemberModel requestForm;
		ProgressDialog progressDialog = null;
		public BulkInviteServerTask(AddMemberModel requestForm,final View view1){
			this.requestForm = requestForm;
		}
		@Override
		protected void onPreExecute() {		
			progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String JSONstring = new Gson().toJson(requestForm);		    
			DefaultHttpClient client1 = new DefaultHttpClient();
			Log.d(TAG, "InviteMemberServerTask request:"+JSONstring);
			HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/admin/reinviteuser");
			 httpPost = SuperChatApplication.addHeaderInfo(httpPost,true);
			 HttpResponse response = null;
			try {
				httpPost.setEntity(new StringEntity(JSONstring));
				try {
					response = client1.execute(httpPost);
					final int statusCode=response.getStatusLine().getStatusCode();
					if (statusCode == HttpStatus.SC_OK){
						HttpEntity entity = response.getEntity();
						BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
						String line = "";
						String str = "";
						while ((line = rd.readLine()) != null) {
							
							str+=line;
						}
						Log.d(TAG, "invite Result : "+str);
						if(str!=null &&!str.equals("")){
							str = str.trim();
							Gson gson = new GsonBuilder().create();
							if (str==null || str.contains("error")){
								return str;
							}
							AddMemberResponseModel regObj = gson.fromJson(str, AddMemberResponseModel.class);
							if (regObj != null) {
								if(regObj.accountCreated!=null && !regObj.accountCreated.isEmpty()){
									dialogHandler.sendEmptyMessage(1);//showDialog("User invitation sent",true);
								}else if(regObj.accountAlreadyExists!=null && !regObj.accountAlreadyExists.isEmpty()){
									dialogHandler.sendEmptyMessage(2);//showDialog("User already added.",false);
								}else if(regObj.accountFailed!=null && !regObj.accountFailed.isEmpty()){
									dialogHandler.sendEmptyMessage(3);//showDialog("User already added in this domain.",false);
								}								
							}
							return str;
						}
					}
				} catch (ClientProtocolException e) {
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				}
				
			} catch (UnsupportedEncodingException e1) {
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:"+e1.toString());
			}catch(Exception e){
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
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
				ErrorModel errorModel = null;
				try{
					errorModel = gson.fromJson(str,ErrorModel.class);
				}catch(Exception e){}
				if (errorModel != null) {
					if (errorModel.citrusErrors != null
							&& !errorModel.citrusErrors.isEmpty()) {
						ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
						if(citrusError!=null){
//							showDialog(citrusError.message);
							Toast.makeText(context, citrusError.message, Toast.LENGTH_SHORT).show();
						}
						else{
//							showDialog("Please try again later.");
							Toast.makeText(context, "Please try again later.", Toast.LENGTH_SHORT).show();
						}
					} else if (errorModel.message != null){
//						showDialog(errorModel.message);
						Toast.makeText(context, errorModel.message, Toast.LENGTH_SHORT).show();
					}
				} else{
//					showDialog("Please try again later.");
					Toast.makeText(context, "Please try again later.", Toast.LENGTH_SHORT).show();
				}
			}else if (str!=null && str.contains("success")){
				Toast.makeText(context, "Invitation sent successfully!", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(str);
		}
	}
	Handler dialogHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int type = msg.what;
			switch(type){
			case 1:
				Toast.makeText(context, "Invite sent", Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(context, "Invite already sent!", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(context, "Invite already sent!", Toast.LENGTH_SHORT).show();
				break;
			}
//			if(adapter!=null){
//				adapter.removeSelectedItems();
//				adapter.notifyDataSetChanged();
//			}
		}
	};
//===============================================
	private void showPopupDialog(final View v, final String name, final String number){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getString(R.string.re_invite_confirmation) + name +"?");
		builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	ArrayList<MemberDetail> members1 = new ArrayList<MemberDetail>();
				MemberDetail memberDetail1 = new MemberDetail();
				memberDetail1.name   = name;
				memberDetail1.mobileNumber = number;
				members1.add(memberDetail1);
				AddMemberModel requestForm1 = new AddMemberModel(members1);
				requestForm1.domainName = SharedPrefManager.getInstance().getUserDomain();
				if(Build.VERSION.SDK_INT >= 11)
					new BulkInviteServerTask(requestForm1, v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				else
					new BulkInviteServerTask(requestForm1, v).execute();
		    }

		});
		builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {

		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        // I do not need any action here you might
		        dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
