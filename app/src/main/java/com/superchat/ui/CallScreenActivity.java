package com.superchat.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chat.sdk.db.ChatDBWrapper;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.superchat.CustomAppComponents.Activity.CustomAppCompatActivityViewImpl;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.retrofit.api.RetrofitRetrofitCallback;
import com.superchat.retrofit.response.model.ConferenceInfoResponse;
import com.superchat.service.MyAudioCallService;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Response;

import static com.superchat.R.id.sg_name;
import static com.superchat.interfaces.interfaceInstances.objApi;

public class CallScreenActivity extends AppCompatActivity implements OnClickListener{

    public static void start(Context context, final String callId) {
        Intent starter = new Intent(context, CallScreenActivity.class);
        starter.putExtra(SinchService.CALL_ID, callId);
        context.startActivity(starter);
    }

    static final String TAG = CallScreenActivity.class.getSimpleName();
    private final Context context = this;

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private boolean isReceived;
    private boolean isGroupCall;
    private long mCallStart = 0;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;
    private TextView sgName;
    private LinearLayout groupLayout;
    SharedPrefManager iChatPref;
    ChatDBWrapper chatDBWrapper;
    ImageView muteButton;
    ImageView speakerButton;
    ImageView chat_button;
    boolean isMute = false;
    boolean isSpeaker = false;
    private SinchService.SinchServiceInterface mSinchServiceInterface;
    AudioController audioController;
    AudioManager audioManager;

    private ServiceConnection mCallConnection = new ServiceConnection() {
        // ------------ Changes for call ---------------
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            if (mSinchServiceInterface != null) {
                Call call = mSinchServiceInterface.getCall(mCallId);
                if (call != null) {
                    call.addCallListener(new SinchCallListener());
                    Map<String, String> header = call.getHeaders();
                    String domainDisplayName = null;
                    String domainName = null;
                    if(header != null && header.size() > 0 && header.get("fromUserName") != null && !header.get("fromUserName").equals(iChatPref.getUserName())) {
                        domainDisplayName = header.get("domainDisplayName");
                        domainName = header.get("domainName");
                        String myName = header.get("displayName");
                        if(myName != null){
                            setProfilePic(header.get("fromUserName"));
                        }else {
                            myName = iChatPref.getUserServerName(header.get("fromUserName"));

                            if (myName != null && myName.equals(header.get("userName")))
                                myName = iChatPref.getUserServerName(header.get("userName"));
                            if (myName != null && myName.equals(header.get("userName")))
                                myName = "New User";
                            if (myName != null && myName.contains("_"))
                                myName = "+" + myName.substring(0, myName.indexOf("_"));
                            isGroupCall = false;
                            setProfilePic(header.get("userName"));
                        }
                        mCallerName.setText(myName);
                        mCallState.setText(call.getState().toString());
                    }else {
                        String myName = null;
                        if(header != null && header.size() > 0 && header.get("fromUserName").equals(iChatPref.getUserName())) {
                            myName = iChatPref.getUserServerName(header.get("userName"));
                            setProfilePic(header.get("userName"));
                        }else {
                            if(iChatPref.isGroupChat(call.getRemoteUserId()) || isGroupCall) {
                                String group = call.getRemoteUserId();
                                myName = iChatPref.getGroupDisplayName(call.getRemoteUserId());
                                groupLayout.setVisibility(View.VISIBLE);
                                setProfilePic(iChatPref.getUserFileId(group));
                            }else{
                                myName = iChatPref.getUserServerName(call.getRemoteUserId());
                                if (myName != null && myName.equals(call.getRemoteUserId()))
                                    myName = iChatPref.getUserServerName(call.getRemoteUserId());
                                if (myName != null && myName.equals(call.getRemoteUserId()))
                                    myName = "New User";
                                if (myName != null && myName.contains("_"))
                                    myName = "+" + myName.substring(0, myName.indexOf("_"));
                                setProfilePic(call.getRemoteUserId());
                            }
                        }
                        if(myName != null && myName.equals(call.getRemoteUserId())) {
                            mCallerName.setText("Group Call");
                            ((ImageView) findViewById(R.id.id_profile_pic)).setImageResource(R.drawable.group_call_def);
                        }
                        else
                            mCallerName.setText(myName);
                        if(call.getState() != null && call.getState().toString().equalsIgnoreCase("ended"))
                            mCallState.setVisibility(View.GONE);
                        else
                            mCallState.setText(call.getState().toString());
                    }

                    Log.e("Calling : ", "domainDisplayName : "+ domainDisplayName + "\ndomainName : "+domainName);
                    if(domainDisplayName != null){
                        sgName.setText(domainDisplayName);
                    } else if(domainName != null){
                        sgName.setText(domainName);
                    } else {
                        if (SharedPrefManager.getInstance().getCurrentSGDisplayName() != null && SharedPrefManager.getInstance().getCurrentSGDisplayName().trim().length() > 0)
                            sgName.setText(SharedPrefManager.getInstance().getCurrentSGDisplayName());
                        else
                            sgName.setText(SharedPrefManager.getInstance().getUserDomain());
                    }

                    if(mSinchServiceInterface != null && isReceived && (iChatPref.isGroupChat(call.getRemoteUserId()) || isGroupCall))
                        mSinchServiceInterface.callGroup(call.getRemoteUserId(), null);
/*

					 if(SharedPrefManager.getInstance().getCurrentSGDisplayName() != null && SharedPrefManager.getInstance().getCurrentSGDisplayName().trim().length() > 0)
						 sgName.setText(SharedPrefManager.getInstance().getCurrentSGDisplayName());
					 else
						 sgName.setText(SharedPrefManager.getInstance().getUserDomain());
*/

                    audioController = mSinchServiceInterface.getAudioController();
                    startService(new Intent(context, MyAudioCallService.class)); //start service which is MyAudioCallService.java
                } else {
                    Log.e(TAG, "Started with invalid callId, aborting.");
                    finish();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // if
            // (SinchService.class.getName().equals(componentName.getClassName()))
            {
                mSinchServiceInterface = null;
                CustomAppCompatActivityViewImpl.callEnded();

                stopService(new Intent(context, MyAudioCallService.class)); //start service which is MyAudioCallService.java

                onServiceDisconnected();
            }
        }

        protected void onServiceDisconnected() {
            // for subclasses
        }

    };

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        UtilSetFont.setFontMainScreen(this);

//		KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//		final KeyguardManager.KeyguardLock kl = km .newKeyguardLock("MyKeyguardLock");
//		kl.disableKeyguard();

//		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
//		                                 | PowerManager.ACQUIRE_CAUSES_WAKEUP
//		                                 | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
//		wakeLock.acquire();

        iChatPref = SharedPrefManager.getInstance();
        chatDBWrapper = ChatDBWrapper.getInstance(getApplicationContext());
        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        groupLayout = (LinearLayout) findViewById(R.id.id_group_call);
        sgName = (TextView) findViewById(sg_name);
        mCallState = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);
        isMute = false;
        isSpeaker = false;
        audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
        muteButton = (ImageView)findViewById(R.id.mute_button);
        speakerButton = (ImageView)findViewById(R.id.speaker_button);
        chat_button = (ImageView)findViewById(R.id.chat_button);

        muteButton.setOnClickListener(this);
        speakerButton.setOnClickListener(this);
        chat_button.setOnClickListener(this);

        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        mCallStart = System.currentTimeMillis();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        if(mCallId == null){
            mCallId = CustomAppCompatActivityViewImpl.getCallID();
        } else {
            CustomAppCompatActivityViewImpl.callStarted(mCallId);
        }
        isGroupCall = getIntent().getBooleanExtra(SinchService.GROUP_CALL, false);
        isReceived = getIntent().getBooleanExtra(SinchService.GROUP_CALL_RECEIVED, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
        try {
            unbindService(mCallConnection);
        } catch (Exception e) {
            // Just ignore that
            Log.d("MessageHistoryScreen", "Unable to un bind");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        bindService(new Intent(this, SinchService.class), mCallConnection,Context.BIND_AUTO_CREATE);
    }

    /*
    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }*/

    private void endCall() {
        CustomAppCompatActivityViewImpl.callEnded();

        stopService(new Intent(this, MyAudioCallService.class)); //start service which is MyAudioCallService.java

        if(mAudioPlayer!=null)
            mAudioPlayer.stopProgressTone();
        if(mSinchServiceInterface!=null){
            Call call = mSinchServiceInterface.getCall(mCallId);
            if (call != null) {
                call.hangup();
            }
            if(isGroupCall) {
                mSinchServiceInterface.stopClient();
                mSinchServiceInterface.startClient(iChatPref.getUserName());
            }
        }
//		if(!HomeScreen.isLaunched){
//			Intent intent = new Intent(this, HomeScreen.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//		}


        finish();
    }
    //boolean isFirstActivity(){
//	ActivityManager mngr = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
//
//	List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
//for(ActivityManager.RunningTaskInfo info : taskList){
//	Log.d("CallScreenActivity", "StacksActivity: "+info.topActivity.getClassName());
//}
//	if(taskList.get(0).numActivities >= 1 &&
//	   taskList.get(0).topActivity.getClassName().equals(HomeScreen.class.getName())) {
//	    return false;
//	}
//	return true;
//}

    private String formatTimespan(long timespan) {
        //long totalSeconds = timespan / 1000;
        long minutes = timespan / 60;
        long seconds = timespan % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        if (mCallStart > 0) {
            if(mSinchServiceInterface != null) {
                Call call = mSinchServiceInterface.getCall(mCallId);
                if(call != null){
                    long startedTime = call.getDetails().getStartedTime();
                    long establishedTime = call.getDetails().getEstablishedTime();
                    long currentTime = System.currentTimeMillis();

                    currentTime = currentTime / 1000;

                    Log.e(TAG, "startedTime : "+ startedTime +
                            " -> establishedTime : " + establishedTime +
                            " -> currentTime : " + currentTime);

                    if(startedTime > 0) {
                        String formatterTimeSpan = formatTimespan(currentTime - startedTime);
                        mCallDuration.setText(formatterTimeSpan);
                    }
                }
            } else {
                //mCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
            }
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            if(audioManager != null){
                audioManager.setMode(AudioManager.USE_DEFAULT_STREAM_TYPE);
                //audioManager.setSpeakerphoneOn(false);
            }
            String endMsg = "Call ended: " + call.getDetails().toString();
//			Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.MODE_IN_CALL);
            if(audioManager != null){
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                //audioManager.setSpeakerphoneOn(true);
            }

            mCallStart = System.currentTimeMillis();

            startService(new Intent(context, MyAudioCallService.class)); //start service which is MyAudioCallService.java
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }
    }
    public void onClick(View view){
        try{
            switch(view.getId()){
                case R.id.mute_button:
                    if(audioController!=null){
                        if(!isMute){
                            audioController.mute();
                            muteButton.setImageResource(R.drawable.mute_selected);
                        }else{
                            audioController.unmute();
                            muteButton.setImageResource(R.drawable.mute);
                        }
                        isMute = !isMute;
                    }
                    break;
                case R.id.speaker_button:
                    if(audioController!=null){
                        if(isSpeaker){
                            audioController.disableSpeaker();
                            speakerButton.setImageResource(R.drawable.speaker);
                            //audioManager.setSpeakerphoneOn(false);
                        }else{
                            audioController.enableSpeaker();
                            speakerButton.setImageResource(R.drawable.speaker_selected);
                            //audioManager.setSpeakerphoneOn(true);
                        }
                        isSpeaker = !isSpeaker;
                    }
                    break;
                case R.id.chat_button:{
                    HomeScreen.start(this);
                    finish();
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


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

        String img_path = null;
        Bitmap bitmap = null;
        if(groupPicId != null) {
            img_path = getImagePath(groupPicId);
            bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
        }
        ImageView picView = (ImageView) findViewById(R.id.id_profile_pic);
        if(isGroupCall){
            picView.setImageResource(R.drawable.group_call_def);
        }else {
            if (SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
                picView.setImageResource(R.drawable.female_default);
            else
                picView.setImageResource(R.drawable.male_default);
        }
        if (bitmap != null) {
            picView.setImageBitmap(bitmap);
            String profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
            picView.setTag(filename);
            return true;
        }else if(img_path != null){
            File file1 = new File(img_path);
//			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
            if(file1.exists()){
                picView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//				((ImageView) findViewById(R.id.id_profile_pic)).setImageURI(Uri.parse(img_path));
                setThumb((ImageView) picView,img_path,groupPicId);
                return true;
            }
        }else{

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
    //----------------------------------------------------------------
    private void getConfereneceInfo(final String id) {
        try {
            retrofit2.Call call = objApi.getApi(this).getConferenceInfo(id);
            call.enqueue(new RetrofitRetrofitCallback<ConferenceInfoResponse>(this) {
                @Override
                protected void onResponseVoidzResponse(retrofit2.Call call, Response response) {

                }

                @Override
                protected void onResponseVoidzObject(retrofit2.Call call, ConferenceInfoResponse response) {
                    System.out.println("Retrofit : onResponseVoidzObject 2 - " + response.toString());

                }

                @Override
                protected void common() {

                }

                @Override
                public void onFailure(retrofit2.Call call, Throwable t) {
                    super.onFailure(call, t);
                }
            });
        } catch (Exception e) {

        }
    }
}