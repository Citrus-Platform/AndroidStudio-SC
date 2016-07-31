package com.superchat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.superchat.model.GroupChatServerModel;

public class GroupCreateTaskOnServer extends AsyncTask<String, String, String> {
	public static final String TAG = "GroupCreateTaskOnServer";
	String groupUUID;
	String chatWindowName;
	List<String> usersList;
	SharedPrefManager iPrefManager;
	public final static String CREATE_GROUP_REQUEST = "create_group_request";
	public final static String UPDATE_GROUP_REQUEST = "update_group_request";
	
	public final static int SERVER_GROUP_UPDATE_NOTALLOWED = 0;
	public final static int SERVER_GROUP_NOT_CREATED = 1;
	public final static int SERVER_GROUP_CREATED = 2;
	public final static int SERVER_GROUP_CREATION_FAILED = 3;
	public final static int SERVER_GROUP_NOT_UPDATED = 4;
	public final static int SERVER_GROUP_UPDATED = 5;
	public final static int SERVER_GROUP_UPDATION_FAILED = 6;
	
	
	public GroupCreateTaskOnServer(String groupUUID, String chatWindowName, List<String> usersList){
		this.groupUUID = groupUUID;
		this.chatWindowName = chatWindowName;
		if(usersList!=null)
			this.usersList = Collections.synchronizedList(usersList);
		iPrefManager = SharedPrefManager.getInstance();
	}
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(String... urls) {
		
		GroupChatServerModel model = new GroupChatServerModel();
		if(urls[0].equals(CREATE_GROUP_REQUEST)){
			int state = iPrefManager.getServerGroupState(groupUUID);
			if(state==SERVER_GROUP_NOT_CREATED || state==SERVER_GROUP_CREATION_FAILED){
			model.setUserName(iPrefManager.getUserName());
			if(groupUUID!=null)
				model.setGroupName(groupUUID);
			if(chatWindowName!=null)
				model.setDisplayName(chatWindowName);
			if(iPrefManager.getUserFileId(groupUUID)!=null)
				model.setFileId(iPrefManager.getUserFileId(groupUUID));//model.setFileId(URLEncoder.encode(iPrefManager.getGroupPicId(senderName)));
			if(iPrefManager.getUserStatusMessage(groupUUID)!=null)
				model.setDescription(iPrefManager.getUserStatusMessage(groupUUID));
			if(usersList!=null)
				model.setMemberUserSet(usersList);
			ArrayList<String> adminUserSet = new ArrayList<String>();
			adminUserSet.add(SharedPrefManager.getInstance().getUserName());
		    String JSONstring = new Gson().toJson(model);		    
		    DefaultHttpClient client1 = new DefaultHttpClient();
		    
			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			
			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/jakarta/rest/group/create");
//	         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			 HttpResponse response = null;
	         try {
				httpPost.setEntity(new StringEntity(JSONstring));
				 try {
					 response = client1.execute(httpPost);
					 final int statusCode=response.getStatusLine().getStatusCode();
					 if (statusCode == HttpStatus.SC_OK){ //new1
						 HttpEntity entity = response.getEntity();
	//					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
				            String line = "";
				            while ((line = rd.readLine()) != null) {
				            	Log.d(TAG, "serverUpdateCreateGroupInfo response: "+line);
				            	iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_CREATED);
				            }
			            }else
			            	iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_CREATION_FAILED);
				} catch (ClientProtocolException e) {
					iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_CREATION_FAILED);
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_CREATION_FAILED);
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				}
				 
			} catch (UnsupportedEncodingException e1) {
				iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_CREATION_FAILED);
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:"+e1.toString());
			}catch(Exception e){
				iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_CREATION_FAILED);
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
			}
		}
		}else if(urls[0].equals(UPDATE_GROUP_REQUEST)){
			int state = iPrefManager.getServerGroupState(groupUUID);
			if(state==SERVER_GROUP_NOT_UPDATED || state==SERVER_GROUP_UPDATION_FAILED){
			model.setUserName(iPrefManager.getUserName());
			if(groupUUID!=null)
				model.setGroupName(groupUUID);
			if(chatWindowName!=null)
				model.setDisplayName(chatWindowName);
			if(iPrefManager.getUserFileId(groupUUID)!=null)
				model.setFileId(iPrefManager.getUserFileId(groupUUID));//model.setFileId(URLEncoder.encode(iPrefManager.getGroupPicId(senderName)));//
			if(iPrefManager.getUserStatusMessage(groupUUID)!=null)
				model.setDescription(iPrefManager.getUserStatusMessage(groupUUID));
			if(usersList!=null)
				model.setMemberUserSet(usersList);
			
			
			List<String> adminUserSet = new ArrayList<String>();
			if(usersList!=null)
				for(String member:usersList){
					if(iPrefManager.isAdmin(groupUUID, member))
						model.setAdminUserSet(adminUserSet);
				}
		    String JSONstring = new Gson().toJson(model);
		    
		    DefaultHttpClient client1 = new DefaultHttpClient();
		    
			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			
			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/jakarta/rest/group/update");
//	         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
			 HttpResponse response = null;
	         try {
				httpPost.setEntity(new StringEntity(JSONstring));
				 try {
					 response = client1.execute(httpPost);
					 final int statusCode=response.getStatusLine().getStatusCode();
					 if (statusCode == HttpStatus.SC_OK){ //new1
						 HttpEntity entity = response.getEntity();
	//					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
				            String line = "";
				            while ((line = rd.readLine()) != null) {
				            	Log.d(TAG, "serverUpdateCreateGroupInfo response: "+line);
				            }
				            iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_UPDATED);
			            }else
			            	iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_UPDATION_FAILED);
				} catch (ClientProtocolException e) {
					iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_UPDATION_FAILED);
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				} catch (IOException e) {
					iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_UPDATION_FAILED);
					Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:"+e.toString());
				}
				 
			} catch (UnsupportedEncodingException e1) {
				iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_UPDATION_FAILED);
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:"+e1.toString());
			}catch(Exception e){
				iPrefManager.saveServerGroupState(groupUUID, SERVER_GROUP_UPDATION_FAILED);
				Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:"+e.toString());
			}
		}
		}
	    
		
		return null;
	}

	@Override
	protected void onPostExecute(String response) {

		super.onPostExecute(response);
	}
}
