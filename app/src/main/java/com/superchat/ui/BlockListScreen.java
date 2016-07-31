package com.superchat.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.chat.sdk.ChatService;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BlockListScreen  extends Activity {
	// XmppChatClient chatClient;
    public ChatService messageService;
    BlockListAdapter adapter;
     SharedPrefManager prefManager;
     private ListView blockListView = null;
    private ServiceConnection mMessageConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            messageService = ((ChatService.MyBinder) binder).getService();
            if (Build.VERSION.SDK_INT >= 11)
                new BlockedListTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,prefManager.getUserName());
            else
           	 	new BlockedListTask().execute(prefManager.getUserName());
            Log.d("Service", "Connected");
        }

        public void onServiceDisconnected(ComponentName className) {
            messageService = null;
        }
    };
    protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.block_list_screen);
		prefManager = SharedPrefManager.getInstance();
		blockListView = (ListView) findViewById(R.id.id_block_list);
		((TextView) findViewById(R.id.id_back)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		 
	}
    public void onResume() {
        super.onResume();
        bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);
      
    }
 protected void onPause() {
	 try {
            unbindService(mMessageConnection);
        } catch (Exception e) {
        }
        super.onPause();
 }
 public void showBlockUnblockConfirmDialog(final String title, final String s, final String userName) {
		final Dialog bteldialog = new Dialog(this);
		bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		bteldialog.setCanceledOnTouchOutside(true);
		bteldialog.setContentView(R.layout.custom_dialog_two_button);
		if(title!=null){
			((TextView)bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
			}
		((TextView)bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
		((TextView)bteldialog.findViewById(R.id.id_send)).setText("Ok");
		((TextView)bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				bteldialog.cancel();
				if (Build.VERSION.SDK_INT >= 11)
	                new BlockUnBlockTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	            else
	           	 new BlockUnBlockTask().execute();
				 
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
 private class BlockedListTask extends AsyncTask<String, Void, String> {
	    ProgressDialog dialog;
	    List<String> blockList = new ArrayList<String>();
	    String userName;
		BlockedListTask() {
	    }

	    protected void onPreExecute() {

	        dialog = ProgressDialog.show(BlockListScreen.this, "", "Please wait...", true);

	        // progressBarView.setVisibility(ProgressBar.VISIBLE);
	        super.onPreExecute();
	    }

	    protected String doInBackground(String... args) {
	    	boolean isStatusChanged = false;
	    	userName = args[0];
//	    	if(messageService==null)
//	    		return null;
	    	Set<String> blockSet = prefManager.getBlockList();
	    	if(blockSet!=null){
	    		blockList = new ArrayList<String>();
	    		for(String tmpUser : blockSet){
	    			blockList.add(tmpUser);
	    		}
	    		}
//	    		blockList = SuperChatApplication.blockUserList;//messageService.getBlockedUserList(userName);
	        return null;
	    }

	    protected void onPostExecute(String str) {
	    	super.onPostExecute(str);
	    	if(dialog!=null){
	    	 dialog.cancel();
	    	 dialog = null;
	    	} 
	    	if(blockList!=null && !blockList.isEmpty()){
	    		ArrayList<BlockListAdapter.UserInfo> list = new ArrayList<BlockListAdapter.UserInfo>();
	    		for(String tmpUser: blockList){
	    			BlockListAdapter.UserInfo info = new BlockListAdapter.UserInfo(tmpUser, prefManager.getUserServerName(tmpUser));
	    			list.add(info);
	    		}
	    		adapter = new BlockListAdapter(BlockListScreen.this,R.layout.block_list_item,list);
	    		blockListView.setAdapter(adapter);
	    	}else{
	    		Toast.makeText(BlockListScreen.this, "No list found.", Toast.LENGTH_SHORT).show();
	    		finish();
	    	}
		}
	    }
	private class BlockUnBlockTask extends AsyncTask<String, Void, String> {
	    ProgressDialog dialog;
	boolean isStatusChanged = false;
	String userName;
	    BlockUnBlockTask() {
	    }

	    protected void onPreExecute() {

	        dialog = ProgressDialog.show(BlockListScreen.this, "", "Please wait...", true);

	        // progressBarView.setVisibility(ProgressBar.VISIBLE);
	        super.onPreExecute();
	    }

	    protected String doInBackground(String... args) {
	    	boolean isStatusChanged = false;
	    	userName = args[0];
	    	if(messageService==null)
	    		return null;
	    	if(prefManager.isBlocked(userName)){
	    		isStatusChanged = messageService.blockUnblockUser(userName,true);
	    		if(isStatusChanged)
	    			prefManager.setBlockStatus(userName, false);
	    	}else{
	    		isStatusChanged = messageService.blockUnblockUser(userName,false);
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
	    			Toast.makeText(BlockListScreen.this, getString(R.string.block_successful), Toast.LENGTH_SHORT).show();
	    		}else{
	    			Toast.makeText(BlockListScreen.this, getString(R.string.unblock_successful), Toast.LENGTH_SHORT).show();
	    		}
	    	}else{
	    		Toast.makeText(BlockListScreen.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
	    	}
		}
	    }
}
