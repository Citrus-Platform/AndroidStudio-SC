package com.superchat.service;


import com.chat.sdk.ChatService;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.ui.SinchService;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class XmppStateReceiver  extends BroadcastReceiver  {

	private String TAG = "XmppStateReceiver";
	boolean isConnectedCalled;
	@Override
	public void onReceive(Context context, Intent intent) {
		 if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
			 ConnectivityManager cm =
				        (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
				boolean isConnectingOrConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
				SharedPrefManager pref = SharedPrefManager.getInstance();
				if(pref!=null){
					String mobileNumber = pref.getUserPhone();
					if(mobileNumber == null || mobileNumber.equals("")){
						System.out.println("mobileNumber == null, so returning......");
						return;
					}
				}
				ChatService.xmppConectionStatus = false;
				if(ChatService.connectionStatusListener!=null)
					ChatService.connectionStatusListener.notifyConnectionChange();
				if(isConnected && pref.isProfileAdded(pref.getUserDomain())){
					    isConnectedCalled = true;
						context.startService(new Intent(SuperChatApplication.context, ChatService.class));
						context.startService(new Intent(SuperChatApplication.context, SinchService.class));
				}else{
//					if(!isConnectedCalled && !isConnectingOrConnected){
//						SharedPrefManager.getInstance().saveLastOnline(SharedPrefManager.getInstance().getUserDomain(), System.currentTimeMillis());
//					}
//					ChatService.xmppConectionStatus = false;
//					if(ChatService.connectionStatusListener!=null)
//						ChatService.connectionStatusListener.notifyConnectionChange();
				}
				System.out.println("XmppStateReceiver connectivity changed."+isConnectingOrConnected+" , "+isConnectingOrConnected);
		 }
		
	}
	  private boolean isChatServiceRunning() {
  	    ActivityManager manager = (ActivityManager)SuperChatApplication.context.getSystemService(Context.ACTIVITY_SERVICE);
  	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
  	        if (ChatService.class.getName().equals(service.service.getClassName())) {
  	            return true;
  	        }
  	    }
  	    return false;
  	}

}

