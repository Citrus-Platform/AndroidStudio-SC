package com.superchat.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.superchat.utils.AndroidMultiPartEntity.ProgressListener;

import org.apache.commons.fileupload.util.Streams;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.FormBodyPart;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;


public class FileUploaderDownloader extends AsyncTask<String, Integer, String>{
	
	private final String TAG = "FileUploader";
	boolean isLoading;
	ProgressDialog dialog = null;
	Context context;
	boolean isUpload;
	private String serverFileId = null;
	public final static String GROUP_CHAT_PICUTRE = "101"; 
	Handler handler = null;
	public FileDownloadResponseHandler fileDownloadResponseHandler;
	public String getServerFileId() {
		return serverFileId;
	}
	
	public FileUploaderDownloader(Context context, FileDownloadResponseHandler download_handler, boolean isLoading, boolean isUpload, Handler handler){
		this.isLoading = isLoading;
		this.context = context;
		serverFileId = null;
		this.isUpload = isUpload;
		this.handler = handler;
		fileDownloadResponseHandler = download_handler;
	}
	@Override
	protected void onPreExecute() {			
		if(isLoading){
			if(isUpload)
				dialog = ProgressDialog.show(context, "","Uploading backup data. Please wait..", true);
			else
				dialog = ProgressDialog.show(context, "","Getting backup data. Please wait..", true);
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
		try{
			int retry = 0;
			int count;
			if(isUpload){
				while(true)
				{
					String postUrl = Constants.media_post_url;
					String filePath = urls[0];
					String fileId = null;
					 try {
						  fileId = postMedia(postUrl, filePath);
						  if(fileId!=null && !fileId.equals("")){
						  final JSONObject jsonObject = new JSONObject(fileId);
							final String status = jsonObject.getString("status");
							if (status.equals("error")) {
								retry++;
									wait(5000);
							} else if (status.trim().equalsIgnoreCase("success")) {
								retry++;
								fileId = jsonObject.getString("fileId");
								serverFileId = fileId;
								return fileId;
							}
						  }else{
							  retry++;
							  wait(5000);
							  }
						  } catch (Exception e) {
							  e.printStackTrace();
							}
					 if(retry > 2)
						return null;
				}
			}else{
				 try {
					  String filePath = urls[0];
					  String fileId = urls[1];
	                  String getUrl = Constants.media_get_url + fileId + ".zip";
	                  URL url = new URL(getUrl);
	                  URLConnection conection = url.openConnection();
	                  conection.connect();
	                  // getting file length
	                  int lenghtOfFile = conection.getContentLength();
	                  System.out.println("[Lenth of file - ]"+lenghtOfFile/1024 + " KB");
	                  // input stream to read file - with 8k buffer
	                  InputStream input = new BufferedInputStream(url.openStream(), 8192);
	                  // Output stream to write file
	                  OutputStream output = new FileOutputStream(filePath);
	                  byte data[] = new byte[4096]; 
	                  long total = 0;
	                  while ((count = input.read(data)) != -1) {
	                      total += count;
	                      // publishing the progress....
	                      // After this onProgressUpdate will be called
//	                      publishProgress(""+(int)((total*100)/lenghtOfFile));
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
	              return null;
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public String postMedia(String url, String fileToUpload) throws IOException {
		String responseData = null;
		try{
		  HttpClient httpclient = new DefaultHttpClient();
		  HttpPost httppost = new HttpPost(url);
		  FileBody data = new FileBody(new File(fileToUpload));
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
		  reqEntity.addPart(new FormBodyPart("ext", new StringBody("zip")));
		  reqEntity.addPart(dataBodyPart);
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
		System.out.println("[Response = ] - "+response);
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		if(isUpload){
			if(serverFileId != null)
				SharedPrefManager.getInstance().saveBackupFileId(serverFileId);
			if(handler != null)
				handler.sendEmptyMessage(0);
		}else{
			if(fileDownloadResponseHandler != null)
				fileDownloadResponseHandler.onFileDownloadResposne(null, 0, null);
		}
	}

}
