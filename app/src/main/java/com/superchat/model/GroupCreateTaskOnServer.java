package com.superchat.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

public class GroupCreateTaskOnServer extends AsyncTask<String, String, String> {
	public static final String TAG = "GroupCreateTaskOnServer";
	String senderName;
	String chatWindowName;
	ArrayList<String> usersList;
	public final static String CREATE_GROUP_REQUEST = "create_group_request";
	public final static String UPDATE_GROUP_REQUEST = "update_group_request";
	public GroupCreateTaskOnServer(String senderName, String chatWindowName, ArrayList<String> usersList){
		this.senderName = senderName;
		this.chatWindowName = chatWindowName;
		this.usersList = usersList;
	}
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(String... urls) {
		final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
		GroupChatServerModel model = new GroupChatServerModel();
		if(urls[0].equals(CREATE_GROUP_REQUEST)){
			model.setUserName(iPrefManager.getUserName());
			if(senderName!=null)
				model.setGroupName(senderName);
			if(chatWindowName!=null)
				model.setDisplayName(chatWindowName);
			if(iPrefManager.getUserFileId(senderName)!=null)
				model.setFileId(iPrefManager.getUserFileId(senderName));//model.setFileId(URLEncoder.encode(iPrefManager.getGroupPicId(senderName)));
			if(usersList!=null)
				model.setMemberUserSet(usersList);
			ArrayList<String> adminUserSet = new ArrayList<String>();
			adminUserSet.add(SharedPrefManager.getInstance().getUserName());
			model.setAdminUserSet(adminUserSet);
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
					 if (statusCode != HttpStatus.SC_OK){
						 HttpEntity entity = response.getEntity();
	//					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
				            String line = "";
				            while ((line = rd.readLine()) != null) {
				            	Log.d(TAG, "serverUpdateCreateGroupInfo response: "+line);
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
		}else if(urls[0].equals(UPDATE_GROUP_REQUEST)){

			model.setUserName(iPrefManager.getUserName());
			if(senderName!=null)
				model.setGroupName(senderName);
			if(chatWindowName!=null)
				model.setDisplayName(chatWindowName);
			if(iPrefManager.getUserFileId(senderName)!=null)
				model.setFileId(iPrefManager.getUserFileId(senderName));//model.setFileId(URLEncoder.encode(iPrefManager.getGroupPicId(senderName)));//
			if(usersList!=null)
				model.setMemberUserSet(usersList);
			List<String> adminUserSet = new ArrayList<String>();
			model.setAdminUserSet(adminUserSet);
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
					 if (statusCode != HttpStatus.SC_OK){
						 HttpEntity entity = response.getEntity();
	//					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
						    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
				            String line = "";
				            while ((line = rd.readLine()) != null) {
				            	Log.d(TAG, "serverUpdateCreateGroupInfo response: "+line);
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
		
		}
	    
		
		return null;
	}

	@Override
	protected void onPostExecute(String response) {

		super.onPostExecute(response);
	}
}
