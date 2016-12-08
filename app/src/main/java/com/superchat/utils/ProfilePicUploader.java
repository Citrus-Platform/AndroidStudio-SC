package com.superchat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.fileupload.util.Streams;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.FormBodyPart;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.superchat.model.GroupCreateTaskOnServer;
import com.superchat.utils.AndroidMultiPartEntity.ProgressListener;


public class ProfilePicUploader extends AsyncTask<String, Integer, String>{
	
	private final String TAG = "ProfilePicUploader";
	ChatService messageService;
	boolean isLoading;
	ProgressDialog dialog = null;
	Context context;
	private String serverFileId = null;
	public final static String GROUP_CHAT_PICUTRE = "101"; 
	Handler handler = null;
	public String getServerFileId() {
		return serverFileId;
	}
	
	public ProfilePicUploader(Context context, ChatService messageService, boolean isLoading){
		this.messageService = messageService;
		this.isLoading = isLoading;
		this.context = context;
		serverFileId = null;
	}
	public ProfilePicUploader(Context context, ChatService messageService,boolean isLoading, Handler handler){
		this.messageService = messageService;
		this.isLoading = isLoading;
		this.context = context;
		serverFileId = null;
		this.handler = handler;
	}
	@Override
	protected void onPreExecute() {			
		if(isLoading){
			dialog = ProgressDialog.show(context, "","Loading. Please wait...", true);
					}
		super.onPreExecute();
	}
//	 @Override
	  protected void onProgressUpdate(Integer... progress) {
//	   // Making progress bar visible
//		  rightImgProgressBar.setVisibility(View.VISIBLE);
//
//	   // updating progress bar value
//		  rightImgProgressBar.setProgress(progress[0]);
//
//	   // updating percentage value
//		  rightImgProgressPercent.setText(String.valueOf(progress[0]) + "%");
	  }
	@Override
	protected String doInBackground(String... urls) {
		//url[1] - packetID
		//http://54.164.75.109:8080/rtMediaServer/get/1_1_5_E_I_I3_dyj5a3elg1.jpg
		//			String url = Constants.PIC_SEP + "http://54.164.75.109:8080/rtMediaServer/get/";
		//			String url = "http://78.129.179.96:8080/rtMediaServer/get/";
		String url = Constants.media_get_url;
		try{
//			String fileId = Utilities.createMediaID(urls[0], Constants.ID_FOR_UPDATE_PROFILE);
			int retry = 0;
			while(true)
			{
				String postUrl = Constants.media_post_url;
				String filePath = urls[0];
				String userName = urls[4];
				String fileId = null;
				 try {
//					 Log.d(TAG,"Pic uploaded info3: "+filePath+" , "+urls[1]);
//					 DBWrapper.getInstance().updateMediaLoadingStarted(urls[1]);
					  fileId = postMedia(postUrl, filePath);
					  if(fileId!=null && !fileId.equals("")){
					  final JSONObject jsonObject = new JSONObject(fileId);
						final String status = jsonObject.getString("status");
						if (status.equals("error")) {
							retry++;
							synchronized (this) {
								wait(2000);
							}
						} else if (status.trim().equalsIgnoreCase("success")) {
							retry++;
							fileId =  jsonObject.getString("fileId");
//							if (messageService != null)
							 if(urls[3].equals(GROUP_CHAT_PICUTRE)){
									serverFileId = fileId;
									picCopy(filePath,fileId);
									return fileId;
								}else if(urls[3].equals("SG_FILE_ID")){
									serverFileId = fileId;
									picCopy(filePath,fileId);
									SharedPrefManager.getInstance().saveSGFileId(SharedPrefManager.getInstance().getUserDomain(), fileId);
									if(handler != null && (serverFileId!=null || !serverFileId.equals("")))
										handler.sendEmptyMessage(0);
									return fileId;
								}else{
								XMPPMessageType mediaType = XMPPMessageType.fromString(urls[3]);
								Log.d(TAG,"Pic uploaded info: "+mediaType+" , "+fileId);
								if(mediaType == XMPPMessageType.atMeXmppMessageTypeGroupImage){
									serverFileId = fileId;
									picCopy(filePath,fileId);
									Log.d(TAG,"Pic uploaded info1: "+urls[4]+" , "+(url + fileId + ".jpg"));
//									ChatDBWrapper.getInstance().updateMessageMediaURL(urls[1], url + fileId + ".jpg");
									if(userName!=null && !userName.equals(""))
										SharedPrefManager.getInstance().saveUserFileId(userName, fileId);
//									new GroupCreateTaskOnServer(urls[4], null, null).execute(GroupCreateTaskOnServer.UPDATE_GROUP_REQUEST); 
									
									if(messageService!=null)
										messageService.sendMediaURL(urls[4], "", urls[1],null,null, url + fileId + ".jpg", urls[2], mediaType);
//									Log.d(TAG,"-----------"+(url + fileId + ".jpg"));
							}else{
								serverFileId = fileId;
								SharedPrefManager.getInstance().saveUserFileId(userName, fileId);
								picCopy(filePath,fileId);
							}
							return fileId;
							}
						}
					  }else{
						  retry++;
						  wait(5000);
						  }
					  } catch (Exception e) {
						  e.printStackTrace();
						}
				 if(retry>2)
					 return null;
				
			}
			
			//			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	 private void picCopy(String path,String fileId){
    	 int count;
		  String filename = Environment.getExternalStorageDirectory().getPath()+ File.separator + "SuperChat/"+fileId+".jpg";
		  try {
//			  if(params[0] != null && params[0].length() > 0)
//				  filename += params[0].substring(params[0].lastIndexOf('/'));
//			  Log.d(TAG,"Pic uploaded info2: "+filename+" , "+params[0]);
//		      URL url = new URL(params[0]);
//		      URLConnection conection = url.openConnection();
//		      conection.connect();
		      // getting file length
//		  int lenghtOfFile = conection.getContentLength();
		  // input stream to read file - with 8k buffer
			File file = new File(path);
		  InputStream input = new FileInputStream(file);
		  // Output stream to write file
		  OutputStream output = new FileOutputStream(filename);
		  Environment.getExternalStorageDirectory().getPath();
		  byte data[] = new byte[4096]; 
		  long total = 0;
		  while ((count = input.read(data)) != -1) {
		      total += count;
		      // publishing the progress....
		  // After this onProgressUpdate will be called
//		  publishProgress((int)total, (int) lenghtOfFile);
		  // writing data to file
		      output.write(data, 0, count);
		  }
		  // flushing output
		  output.flush();
		  // closing streams
		      output.close();
		      input.close();
		     
		  } catch (Exception e) {
		          Log.e("Error: ", e.getMessage());
		  }
    }
	public String postMedia(String url, String fileToUpload) throws IOException {
		String responseData = null;
		try{
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpPost httppost = new HttpPost(url);
//		  httppost.setHeader("RT-APP-KEY", "'15769260:AAAABl26u1EIeTIz'");
		  
		  FileBody data = new FileBody(new File(fileToUpload));
//		  StringBody comment = new StringBody("Filename: " + fileToUpload);
//		  Log.d(TAG,"postMedia body string "+comment.getFilename()+" , "+comment.getCharset());
//		  MultipartEntity reqEntity = new MultipartEntity();
		 final int  totalSize = (int)data.getContentLength();
		  AndroidMultiPartEntity reqEntity = new AndroidMultiPartEntity(
			      new ProgressListener() {

			       @Override
			       public void transferred(long num) {
			    	  Log.d(TAG,"[[[[[[[[ - "+((int) ((num / (float) totalSize) * 100)));
			        publishProgress(((int) ((num / (float) totalSize) * 100)));
			       }
			      });
		  
		  FormBodyPart dataBodyPart = new FormBodyPart("data", data);
//		  FormBodyPart commentBodyPart = new FormBodyPart("comment", comment);
		  reqEntity.addPart(dataBodyPart);
//		  reqEntity.addPart(commentBodyPart);
		  httppost.setEntity(reqEntity);
		  HttpResponse response = httpclient.execute(httppost);
		  Log.d(TAG,"Status Line: " + response.getStatusLine());
		  HttpEntity resEntity = response.getEntity();
		  responseData = Streams.asString(resEntity.getContent());
		  Log.d(TAG,"ResponseData: " + responseData);
		}catch(Exception e){
			e.printStackTrace();
		}
		  return responseData;
		 }
	@Override
	protected void onPostExecute(String response) {

		super.onPostExecute(response);
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		if(handler != null && (serverFileId==null || serverFileId.equals("")))
			handler.sendEmptyMessage(0);
	}

}
