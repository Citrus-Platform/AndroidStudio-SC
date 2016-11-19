package com.superchat.ui;

import com.sinch.android.rtc.calling.Call;
import com.superchat.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceCallActivity extends Activity {

    private Button mCallButton;
    private EditText mCallName;
    private SinchService.SinchServiceInterface mSinchServiceInterface;
	private ServiceConnection mCallConnection = new ServiceConnection() {
		// ------------ Changes for call ---------------
		@Override
		public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
			mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
			if (mSinchServiceInterface != null) {
				 TextView userName = (TextView) findViewById(R.id.loggedInName);
		        userName.setText(mSinchServiceInterface.getUserName());
		        mCallButton.setEnabled(true);
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

		// protected void onServiceConnected() {
		// //Register the user for call
		// if (mSinchServiceInterface!=null &&
		// !mSinchServiceInterface.isStarted()) {
		// mSinchServiceInterface.startClient(SharedPrefManager.getInstance().getUserName());
		// }
		// }

		protected void onServiceDisconnected() {
			// for subclasses
		}

	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mCallName = (EditText) findViewById(R.id.callName);
        mCallButton = (Button) findViewById(R.id.callButton);
        mCallButton.setEnabled(false);
        mCallButton.setOnClickListener(buttonClickListener);

        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(buttonClickListener);
    }

//    @Override
//    protected void onServiceConnected() {
//        TextView userName = (TextView) findViewById(R.id.loggedInName);
//        userName.setText(getSinchServiceInterface().getUserName());
//        mCallButton.setEnabled(true);
//    }
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
    }
    @Override
    public void onDestroy() {
//        if (getSinchServiceInterface() != null) {
//            getSinchServiceInterface().stopClient();
//        }
        super.onDestroy();
    }

    private void stopButtonClicked() {
//        if (getSinchServiceInterface() != null) {
//            getSinchServiceInterface().stopClient();
//        }
        finish();
    }

    private void callButtonClicked() {
        String userName = mCallName.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }
        if(mSinchServiceInterface!=null){
	        Call call = mSinchServiceInterface.callUserWithHeader(userName, HomeScreen.createHeaderForCalling(userName));
	        String callId = call.getCallId();
	
	        Intent callScreen = new Intent(this, CallScreenActivity.class);
	        callScreen.putExtra(SinchService.CALL_ID, callId);
	        startActivity(callScreen);
        }
    }

    private OnClickListener buttonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.callButton:
                    callButtonClicked();
                    break;

                case R.id.stopButton:
                    stopButtonClicked();
                    break;

            }
        }
    };
}
