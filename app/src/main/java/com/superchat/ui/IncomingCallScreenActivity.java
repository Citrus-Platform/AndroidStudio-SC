package com.superchat.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chat.sdk.db.ChatDBWrapper;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IncomingCallScreenActivity extends Activity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;
    SharedPrefManager iChatPref;
    ChatDBWrapper chatDBWrapper;
    private SinchService.SinchServiceInterface mSinchServiceInterface;
    AudioController audioController;
    // Screen wake lock for incoming call
    private WakeLock wakeLock;
    private PowerManager powerManager;
    private ServiceConnection mCallConnection = new ServiceConnection() {
		//------------ Changes for call ---------------
		 @Override
		    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

				 mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
				 if(mSinchServiceInterface!=null){
					 Call call = mSinchServiceInterface.getCall(mCallId);
			        if (call != null) {

			            call.addCallListener(new SinchCallListener());
			            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
			            String myName =  iChatPref.getUserServerName(call.getRemoteUserId());
						if(myName != null && myName.equalsIgnoreCase(call.getRemoteUserId()))
							myName = chatDBWrapper.getUsersDisplayName(call.getRemoteUserId());
			            if(myName != null && myName.equals(call.getRemoteUserId())) {
							myName = SharedPrefManager.getInstance().getUserServerName(myName);
						}
						if(myName != null && myName.equals(call.getRemoteUserId())) {
							myName = "New User";
						}
						if(myName!=null && myName.contains("_"))
							myName = "+"+myName.substring(0, myName.indexOf("_"));
			            remoteUser.setText(myName);
			            setProfilePic(call.getRemoteUserId());
			            audioController = mSinchServiceInterface.getAudioController();
			        } else {
			            Log.e(TAG, "Started with invalid callId, aborting");
			            finish();
			        }
		        }
		    }

		    @Override
		    public void onServiceDisconnected(ComponentName componentName) {
//		        if (SinchService.class.getName().equals(componentName.getClassName())) 
		        {
		            mSinchServiceInterface = null;
		            onServiceDisconnected();
		        }
		    }

//		    protected void onServiceConnected() {
//		    	//Register the user for call
//	    		if (mSinchServiceInterface!=null && !mSinchServiceInterface.isStarted()) {
//	    			mSinchServiceInterface.startClient(SharedPrefManager.getInstance().getUserName());
//	            } 
//		    }

		    protected void onServiceDisconnected() {
		        // for subclasses
		    }

	};
	KeyguardLock keyguardLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming);
        keyguardLock =((KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE)).newKeyguardLock(KEYGUARD_SERVICE);
        powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");

        keyguardLock.disableKeyguard();
        wakeLock.acquire();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
		
        iChatPref = SharedPrefManager.getInstance();
        chatDBWrapper = ChatDBWrapper.getInstance(getApplicationContext());
        Button answer = (Button) findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        Button decline = (Button) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);
        mAudioPlayer = new AudioPlayer(this);
        if(SharedPrefManager.getInstance().isSnoozeExpired(SharedPrefManager.getInstance().getUserDomain())){
        	mAudioPlayer.playRingtone();
    	}
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
//        Log.d(TAG, "mCallId in calling time: "+mCallId);
//        getIntent().putExtra(SinchService.CALL_ID, "");
//        getIntent().setAction(null);
    }
    private String getImagePath(String groupPicId)
	{
		if(groupPicId == null) {
//			groupPicId = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
			return null;
		}
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
			return true;
		}else if(img_path != null){
			File file1 = new File(img_path);
			if(file1.exists()){
				picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				setThumb((ImageView) picView,img_path,groupPicId);
				return true;
			}else{
				if (Build.VERSION.SDK_INT >= 11)
					new BitmapDownloader((RoundedImageView) picView, picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
				else
					new BitmapDownloader((RoundedImageView) picView, picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
			}
		}else{
			if (Build.VERSION.SDK_INT >= 11)
				new BitmapDownloader((RoundedImageView) picView, picView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
			else
				new BitmapDownloader((RoundedImageView) picView, picView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
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
//    @Override
//    protected void onServiceConnected() {
//        Call call = getSinchServiceInterface().getCall(mCallId);
//        if (call != null) {
//            call.addCallListener(new SinchCallListener());
//            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
//            remoteUser.setText(call.getRemoteUserId());
//        } else {
//            Log.e(TAG, "Started with invalid callId, aborting");
//            finish();
//        }
//    }

    private void answerClicked() {
    	if(mAudioPlayer!=null)
    		mAudioPlayer.stopRingtone();
        if(mSinchServiceInterface!=null){
        Call call = mSinchServiceInterface.getCall(mCallId);
        if (call != null) {
            call.answer();
            HomeScreen.isLaunched = false;
            Intent intent = new Intent(this, CallScreenActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            startActivity(intent);
            finish();
        } else {
        	if(!HomeScreen.isLaunched){
    			Intent intent = new Intent(this, HomeScreen.class);
    			 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    		}
			finish();
        }
        }else {
        	if(!HomeScreen.isLaunched){
    			Intent intent = new Intent(this, HomeScreen.class);
    			 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    		}
			finish();
        }
    }
//    boolean isFirstActivity(){
//    	ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
//
//    	List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
//
//    	if(taskList.get(0).numActivities >= 1 &&
//    	   taskList.get(0).topActivity.getClassName().equals(HomeScreen.class.getName())) {
//    	    return false;
//    	}
//    	return true;
//    }
    private void declineClicked() {
    	if(mAudioPlayer!=null)
    		mAudioPlayer.stopRingtone();
        if(mSinchServiceInterface!=null){
	        Call call = mSinchServiceInterface.getCall(mCallId);
	        if (call != null) {
	            call.hangup();
	        }
        }
        if(!HomeScreen.isLaunched){
			Intent intent = new Intent(this, HomeScreen.class);
			 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
		finish();
    }
    public void onResume() {
		super.onResume();
		bindService(new Intent(this, SinchService.class), mCallConnection,Context.BIND_AUTO_CREATE);
    }
    protected void onPause() {
    	super.onPause();
    			try {
    				unbindService(mCallConnection);
    			} catch (Exception e) {
    				// Just ignore that
    				Log.d("MessageHistoryScreen", "Unable to un bind");
    			}
    			if(keyguardLock!=null)
    				keyguardLock.reenableKeyguard();
    			if (wakeLock != null && wakeLock.isHeld()) {
    	            wakeLock.release();
    	        }
    }
    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            if(!HomeScreen.isLaunched){
    			Intent intent = new Intent(IncomingCallScreenActivity.this, HomeScreen.class);
    			 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			startActivity(intent);
    		}
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };
}
