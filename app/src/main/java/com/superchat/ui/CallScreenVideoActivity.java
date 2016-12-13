package com.superchat.ui;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;
import com.sinch.android.rtc.video.VideoScalingType;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.utils.SharedPrefManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;
import static android.media.MediaRecorder.VideoSource.CAMERA;
import static com.superchat.R.id.remoteUser;

public class CallScreenVideoActivity extends Activity implements OnClickListener{

    static final String TAG = CallScreenVideoActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private long mCallStart = 0;
    private boolean mAddedListener = false;
    private boolean mVideoViewsAdded = false;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;
    private TextView tvVideoCallingText;

    ImageView muteButton;
    ImageView speakerButton;
    boolean isMute = false;
    boolean isSpeaker = false;

    SharedPrefManager iChatPref;
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
                    call.addCallListener(new CallScreenVideoActivity.SinchCallListener());
                    Map<String, String> header = call.getHeaders();
                    if(header != null && !header.get("fromUserName").equals(iChatPref.getUserName())) {
                        String myName = header.get("displayName");
                        if(myName != null){
                            //setProfilePic(header.get("fromUserName"));
                        }else {
                            myName = iChatPref.getUserServerName(header.get("fromUserName"));

                            if (myName != null && myName.equals(header.get("userName")))
                                myName = iChatPref.getUserServerName(header.get("userName"));
                            if (myName != null && myName.equals(header.get("userName")))
                                myName = "New User";
                            if (myName != null && myName.contains("_"))
                                myName = "+" + myName.substring(0, myName.indexOf("_"));
                            //setProfilePic(he12ader.get("userName"));
                        }
                        mCallerName.setText(myName);
                        mCallState.setText(call.getState().toString());
                    }else {
                        String myName = null;
                        if(header != null && header.get("fromUserName").equals(iChatPref.getUserName())) {
                            myName = iChatPref.getUserServerName(header.get("userName"));
                            //setProfilePic(header.get("userName"));m
                        }else {
                            myName = iChatPref.getUserServerName(call.getRemoteUserId());
                            if (myName != null && myName.equals(call.getRemoteUserId()))
                                myName = iChatPref.getUserServerName(call.getRemoteUserId());
                            if (myName != null && myName.equals(call.getRemoteUserId()))
                                myName = "New User";
                            if (myName != null && myName.contains("_"))
                                myName = "+" + myName.substring(0, myName.indexOf("_"));
                            //setProfilePic(call.getRemoteUserId());
                        }
                        mCallerName.setText(myName);
                        mCallState.setText(call.getState().toString());
                    }
                    if(SharedPrefManager.getInstance().getCurrentSGDisplayName() != null && SharedPrefManager.getInstance().getCurrentSGDisplayName().trim().length() > 0)
                        mCallerName.setText(SharedPrefManager.getInstance().getCurrentSGDisplayName());
                    else
                        mCallerName.setText(SharedPrefManager.getInstance().getUserDomain());
                    audioController = mSinchServiceInterface.getAudioController();
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
            CallScreenVideoActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    VideoController vc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen_video);

        iChatPref = SharedPrefManager.getInstance();

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(remoteUser);
        mCallState = (TextView) findViewById(R.id.callState);
        tvVideoCallingText = (TextView) findViewById(R.id.callState);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);

        isMute = false;
        isSpeaker = false;
        audioManager=(AudioManager)getSystemService(AUDIO_SERVICE);
        muteButton = (ImageView)findViewById(R.id.mute_button);
        speakerButton = (ImageView)findViewById(R.id.speaker_button);

        Button switchCamera = (Button) findViewById(R.id.switchCamera);
        switchCamera.setOnClickListener(this);

        muteButton.setOnClickListener(this);
        speakerButton.setOnClickListener(this);
        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }

        forceSpeaker();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
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
        mDurationTask = new CallScreenVideoActivity.UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        bindService(new Intent(this, SinchService.class), mCallConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        mDurationTask.cancel();
        mTimer.cancel();
        removeVideoViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        updateUI();
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void updateUI() {
        if (mSinchServiceInterface == null) {
            return; // early
        }

        Call call = mSinchServiceInterface.getCall(mCallId);
        if (call != null) {
            mCallerName.setText(call.getRemoteUserId());
            mCallState.setText(call.getState().toString());
            if (call.getState() == CallState.ESTABLISHED) {
                addVideoViews();
            }
        }
    }

    private void endCall() {
        if(mAudioPlayer!=null)
            mAudioPlayer.stopProgressTone();
        if(mSinchServiceInterface!=null){
            Call call = mSinchServiceInterface.getCall(mCallId);
            if (call != null) {
                call.hangup();
            }
        }
        /*if(!HomeScreen.isLaunched){
            Intent intent = new Intent(this, HomeScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }*/
        finish();
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        if (mCallStart > 0) {
            mCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        }
    }

    private void addVideoViews() {
        if (mVideoViewsAdded || mSinchServiceInterface == null) {
            return; //early
        }

        vc = mSinchServiceInterface.getVideoController();
        if (vc != null) {
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.addView(vc.getLocalView());
            localView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    vc.toggleCaptureDevicePosition();
                }
            });

            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            //vc.setResizeBehaviour(VideoScalingType.ASPECT_FILL);
            view.addView(vc.getRemoteView());
            mVideoViewsAdded = true;
        }
    }

    private void removeVideoViews() {
        if (mSinchServiceInterface == null) {
            return; // early
        }

        vc = mSinchServiceInterface.getVideoController();
        if (vc != null) {
            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.removeView(vc.getRemoteView());

            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

            if(audioManager != null) {
                audioManager.setMode(AudioManager.USE_DEFAULT_STREAM_TYPE);
            }
            String endMsg = "Call ended: " + call.getDetails().toString();
            //Toast.makeText(CallScreenVideoActivity.this, endMsg, Toast.LENGTH_LONG).show();

            endCall();
        }


        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = mSinchServiceInterface.getAudioController();
            audioController.enableSpeaker();

            if(audioManager != null){
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                //audioManager.setSpeakerphoneOn(true);
            }

            mCallStart = System.currentTimeMillis();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());

            if(mCallerName != null){
                mCallerName.setVisibility(View.GONE);
            }
            if(mCallState != null){
                mCallState.setVisibility(View.GONE);
            }
            if(tvVideoCallingText != null){
                tvVideoCallingText.setVisibility(View.GONE);
            }
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

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }
    }


    @Override
    public void onClick(View view){
        try{
            switch(view.getId()){
                case R.id.switchCamera:{
                    if(vc != null){
                        vc.toggleCaptureDevicePosition();
                        //vc.setCaptureDevicePosition(CAMERA_FACING_BACK);
                    }
                    break;
                }
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
                        toggleSpeaker();
                    }
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void forceSpeaker(){
        if(audioController!=null) {
            audioController.enableSpeaker();
            speakerButton.setImageResource(R.drawable.speaker_selected);
            isSpeaker = !isSpeaker;
        }
    }

    private void toggleSpeaker(){
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

}
