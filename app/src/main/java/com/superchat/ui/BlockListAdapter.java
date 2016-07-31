package com.superchat.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BlockListAdapter  extends ArrayAdapter<BlockListAdapter.UserInfo>{
	public final static String TAG = "BlockListAdapter"; 
	Context context;
	int layout;
	int check;
	public int totalChecked;
	ArrayList<UserInfo> data;
	SharedPrefManager prefManager;
	public static class UserInfo implements Comparable{
		public String userName="";
		public UserInfo(String userName,String displayName){
			this.displayName = displayName;
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
		@Override
		public int compareTo(Object another) {
				String tmpName = ((UserInfo)another).getDisplayName();
			return this.displayName.compareToIgnoreCase(tmpName);
		}
 }
	public BlockListAdapter(Context context, int layoutResourceId){
		super(context, layoutResourceId);
		this.context = context;
	}
	public BlockListAdapter(Context context1, int layout, ArrayList<UserInfo> data)
	{
		super(context1,layout,data);
		context = context1;
		this.data = data;
		this.layout = layout;
		totalChecked = 0;
		mDrawableBuilder = TextDrawable.builder().beginConfig().toUpperCase().endConfig().round();
		prefManager = SharedPrefManager.getInstance();
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
			viewholder.displayNameView = (TextView)row.findViewById(R.id.id_contact_name);
			viewholder.displayNameView = (TextView)row.findViewById(R.id.id_contact_name);
			viewholder.blockBtnView = (TextView)row.findViewById(R.id.id_unblock_btn);
			 
			viewholder.blockBtnView.setOnClickListener(viewholder.onCheckeClickListener);
			row.setTag(viewholder);
		}
		else
		{
			viewholder = (ViewHolder)row.getTag();
		}
//		ViewHolder viewholder = (ViewHolder)view.getTag();
		UserInfo map = (UserInfo)getItem(position);
		viewholder.map = map;
		viewholder.userNames = map.getUserName();//cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
//		String s = cursor.getString(cursor.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
		viewholder.displayName =map.getDisplayName();//cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));
		//		viewholder.voipumValue = cursor.getString(cursor.getColumnIndex(DatabaseConstants.VOPIUM_FIELD));
//		String s2 = cursor.getString(cursor.getColumnIndex(DatabaseConstants.IS_FAVOURITE_FIELD));
//		String compositeNumber = cursor.getString( cursor.getColumnIndex(DatabaseConstants.CONTACT_COMPOSITE_FIELD));
		if(viewholder.userNames.equals(viewholder.displayName))
			viewholder.displayName = SharedPrefManager.getInstance().getUserServerName(viewholder.displayName);
		if(viewholder.userNames.equals(viewholder.displayName))
			viewholder.displayName = "New User";
		viewholder.displayNameView.setText(viewholder.displayName);
		
		setProfilePic(viewholder.image, viewholder.imageDefault, viewholder.displayName, viewholder.userNames);
		return row;
	}
	
	public class ViewHolder
	{
		String id;
		TextView displayNameView;
		TextView blockBtnView;
		String userNames;
		String displayName;
		ImageView imageDefault;
		ImageView image;
		UserInfo map;
		ViewHolder(){

		}
			private OnClickListener onCheckeClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					 showBlockUnblockConfirmDialog(context.getString(R.string.confirmation),context.getString(R.string.unblock_confirmation),userNames,map);
//					 if (Build.VERSION.SDK_INT >= 11)
//			                new BlockUnBlockTask(map).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,userNames);
//			            else
//			           	 new BlockUnBlockTask(map).execute(userNames);
				}};
				private void showBlockUnblockConfirmDialog(final String title, final String s,final String user, final UserInfo map) {
					final Dialog bteldialog = new Dialog(context);
					bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					bteldialog.setCanceledOnTouchOutside(true);
					bteldialog.setContentView(R.layout.custom_dialog_two_button);
					if(title!=null){
						((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
						}
					((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
					((TextView)bteldialog.findViewById(R.id.id_send)).setText(context.getString(R.string.yes));
					((TextView)bteldialog.findViewById(R.id.id_cancel)).setText(context.getString(R.string.no));
					((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
						
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							bteldialog.cancel();
							if (Build.VERSION.SDK_INT >= 11)
				                new BlockUnBlockTask(map).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,user);
				            else
				           	 new BlockUnBlockTask(map).execute(user);
							 
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
	private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
	private void setProfilePic(ImageView view, ImageView view_default, String displayName, String userName){
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); 
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
	private class BlockUnBlockTask extends AsyncTask<String, Void, String> {
	    ProgressDialog dialog;
	boolean isStatusChanged = false;
	String userName;
	UserInfo item;
	    BlockUnBlockTask(UserInfo item) {
	    	this.item = item;
	    }

	    protected void onPreExecute() {

	        dialog = ProgressDialog.show(context, "", "Please wait...", true);

	        // progressBarView.setVisibility(ProgressBar.VISIBLE);
	        super.onPreExecute();
	    }

	    protected String doInBackground(String... args) {
	    	boolean isStatusChanged = false;
	    	userName = args[0];
	    	if(((BlockListScreen)context).messageService==null)
	    		return null;
	    	if(prefManager.isBlocked(userName)){
	    		isStatusChanged = ((BlockListScreen)context).messageService.blockUnblockUser(userName,true);
	    		if(isStatusChanged)
	    			prefManager.setBlockStatus(userName, false);
	    	}else{
	    		isStatusChanged = ((BlockListScreen)context).messageService.blockUnblockUser(userName,false);
	    		if(isStatusChanged)
	    			prefManager.setBlockStatus(userName, true);
	    	}
	    	this.isStatusChanged = isStatusChanged;
	        return null;
	    }

	    protected void onPostExecute(String str) {
	    	super.onPostExecute(str);
	    	if(dialog!=null){
	    	 dialog.cancel();
	    	 dialog = null;
	    	} 
	    	if(isStatusChanged){
	    		if(prefManager.isBlocked(userName)){
	    			Toast.makeText(context, context.getString(R.string.block_successful), Toast.LENGTH_SHORT).show();
	    		}else{
	    			Toast.makeText(context, context.getString(R.string.unblock_successful), Toast.LENGTH_SHORT).show();
	    		}
	    		//restartActivity((BlockListScreen)context);
	    		remove(item);
	    		notifyDataSetChanged();
	    		if(getCount() == 0)
	    			((BlockListScreen)context).finish();
	    	}else{
	    		Toast.makeText(context, "Please try after some time.", Toast.LENGTH_SHORT).show();
	    	}
		}
	    }
	private void restartActivity(Activity activity) {
		if (Build.VERSION.SDK_INT >= 11) {
			activity.recreate();
		} else {
			activity.finish();
			activity.startActivity(activity.getIntent());
		}
	}
}
