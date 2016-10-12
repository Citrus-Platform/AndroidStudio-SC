package com.superchat.ui;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.superchat.utils.SharedPrefManager;

public class SinchService extends Service {

//    private static final String APP_KEY = "enter-application-key";
//    private static final String APP_SECRET = "enter-application-secret";
//    private static final String ENVIRONMENT = "sandbox.sinch.com";

//    private static final String APP_KEY = "6d6fb10a-5195-4639-bb24-d678f228eed3";
//    private static final String APP_SECRET = "VWq5RrOaaEqkyYukzCCahw==";
//    private static final String ENVIRONMENT = "sandbox.sinch.com";

	private static final String APP_KEY = "bceec279-151d-483c-933f-1eed6c0c6cb6";
    private static final String APP_SECRET = "Gd9DlpvF7ki/CZV/yZAwvg==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";
    public static final String CALL_ID = "CALL_ID";
    static final String TAG = SinchService.class.getSimpleName();

    private SinchServiceInterface mSinchServiceInterface = new SinchServiceInterface();
    private static SinchClient mSinchClient;
    private String mUserId;

    private StartFailedListener mListener;

    public final static String KEY_INTENT_SINCH_SERVICE = "KilledSinchServices";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mSinchClient != null && mSinchClient.isStarted()) {
            mSinchClient.terminate();
        }
        sendBroadcast(new Intent(KEY_INTENT_SINCH_SERVICE));
        super.onDestroy();
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "started");
        try {
            if (mSinchServiceInterface != null && !mSinchServiceInterface.isStarted() && SharedPrefManager.getInstance().getUserName() != null && !SharedPrefManager.getInstance().getUserName().equals("")) {
                mSinchServiceInterface.startClient(SharedPrefManager.getInstance().getUserName());
            }
        } catch(Exception e){

        }
        return START_STICKY;
	}
    private void start(String userName) {
        Log.d(TAG, "SinchClient start 1");
        if (mSinchClient == null) {
            Log.d(TAG, "SinchClient start 2");
            mUserId = userName;
            mSinchClient = Sinch.getSinchClientBuilder().context(getApplicationContext()).userId(userName)
                    .applicationKey(APP_KEY)
                    .applicationSecret(APP_SECRET)
                    .environmentHost(ENVIRONMENT).build();
            Log.d(TAG, "SinchClient start 3");
        }

        mSinchClient.setSupportCalling(true);
        mSinchClient.startListeningOnActiveConnection();
        mSinchClient.addSinchClientListener(new MySinchClientListener());
        mSinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        mSinchClient.start();

        Log.d(TAG, "SinchClient start 4");
    }

    private void stop() {
        if (mSinchClient != null) {
            mSinchClient.terminate();
            mSinchClient = null;
        }
    }

    private boolean isStarted() {
        return (mSinchClient != null && mSinchClient.isStarted());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSinchServiceInterface;
    }

    public class SinchServiceInterface extends Binder {

        public Call callPhoneNumber(String phoneNumber) {
            return mSinchClient.getCallClient().callPhoneNumber(phoneNumber);
        }

        public Call callUser(String userId) {
            return mSinchClient.getCallClient().callUser(userId);
        }

        public String getUserName() {
            return mUserId;
        }
        public AudioController getAudioController(){
        	AudioController audioController = null;
        	if(mSinchClient!=null)
        		audioController = mSinchClient.getAudioController();
        	return audioController;
        }
        public boolean isStarted() {
            return SinchService.this.isStarted();
        }

        public void startClient(String userName) {
            start(userName);
        }

        public void stopClient() {
            stop();
        }

        public void setStartListener(StartFailedListener listener) {
            mListener = listener;
        }

        public Call getCall(String callId) {
            return mSinchClient.getCallClient().getCall(callId);
        }
    }

    public interface StartFailedListener {
        void onStartFailed(SinchError error);

        void onStarted();
    }

    private class MySinchClientListener implements SinchClientListener {

        @Override
        public void onClientFailed(SinchClient client, SinchError error) {
            Log.d(TAG, "SinchClient failed 1");
            if (mListener != null) {
                mListener.onStartFailed(error);
            }
            if(mSinchClient != null)
                mSinchClient.terminate();
            mSinchClient = null;
            Log.d(TAG, "SinchClient failed 2");

        }

        @Override
        public void onClientStarted(SinchClient client) {
            Log.d(TAG, "SinchClient started");
            if (mListener != null) {
                mListener.onStarted();
            }
        }

        @Override
        public void onClientStopped(SinchClient client) {
            Log.d(TAG, "SinchClient stopped");
        }

        @Override
        public void onLogMessage(int level, String area, String message) {
            switch (level) {
                case Log.DEBUG:
                    Log.d(area, message);
                    break;
                case Log.ERROR:
                    Log.e(area, message);
                    break;
                case Log.INFO:
                    Log.i(area, message);
                    break;
                case Log.VERBOSE:
                    Log.v(area, message);
                    break;
                case Log.WARN:
                    Log.w(area, message);
                    break;
            }
        }

        @Override
        public void onRegistrationCredentialsRequired(SinchClient client,
                ClientRegistration clientRegistration) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call call) {
            Log.d(TAG, "Incoming call");
//            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
//    		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
//    		        "com.superchat.onCall");
//    		wakeLock.acquire();
            if(call!=null && call.getRemoteUserId()!=null && SharedPrefManager.getInstance().isBlocked(call.getRemoteUserId()) ){
//            	 Call callTmp = mSinchServiceInterface.getCall(mCallId);
     	        if (call != null) {
     	        	call.hangup();
     	        }
        		return;
    		}

            Intent intent = new Intent(SinchService.this, IncomingCallScreenActivity.class);
            intent.putExtra(CALL_ID, call.getCallId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SinchService.this.startActivity(intent);
        }
    }

}