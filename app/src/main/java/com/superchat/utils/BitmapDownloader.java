package com.superchat.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.superchat.SuperChatApplication;
import com.superchat.widgets.ChatHomeAdapter;
import com.superchat.widgets.ContactsAdapter;
import com.superchat.widgets.RoundedImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class BitmapDownloader extends AsyncTask<String, Integer, String>{
    private static final String TAG = "BitmapDownloader";
	private String fileId;
	private boolean isThumbRequest = false;
    private boolean isPicViewRequest = false;
    private boolean isProfilePicViewRequest = false;
    private WeakReference<RoundedImageView> imageViewReference = null;
    private WeakReference<ImageView> defaultViewReference = null;
    private WeakReference<ImageView> profileViewReference = null;
    private WeakReference<RoundedImageView> profileRoundViewReference = null;
    public static final String THUMB_REQUEST = "thumb_request"; 
    public static final String PIC_VIEW_REQUEST = "pic_view_request"; 
    public static final String PROFILE_PIC_REQUEST = "profile_view_request"; 
    ProgressDialog progressDialog = null;
    Context context;
	Adapter adapter = null;
    public BitmapDownloader(Context context,ImageView profileImageView) {
    	this.context = context;
    	profileViewReference = new WeakReference<ImageView>(profileImageView);
    }
    public BitmapDownloader(Context context,RoundedImageView profileImageView) {
    	this.context = context;
    	profileRoundViewReference = new WeakReference<RoundedImageView>(profileImageView);
    }
    public BitmapDownloader(RoundedImageView roundedImageView) {
    	imageViewReference = new WeakReference<RoundedImageView>(roundedImageView);
    }
    public BitmapDownloader(RoundedImageView roundedImageView,ImageView defaultView) {
    	imageViewReference = new WeakReference<RoundedImageView>(roundedImageView);
    	defaultViewReference = new WeakReference<ImageView>(defaultView);
    }
    public BitmapDownloader(Adapter adapter, RoundedImageView roundedImageView, ImageView defaultView) {
		if(adapter instanceof ChatHomeAdapter)
			this.adapter = (ChatHomeAdapter)adapter;
		else if(adapter instanceof ContactsAdapter)
			this.adapter = (ContactsAdapter)adapter;
    	imageViewReference = new WeakReference<RoundedImageView>(roundedImageView);
    	defaultViewReference = new WeakReference<ImageView>(defaultView);
    }
 public BitmapDownloader() {
    	
    }
//  /*  Before starting background thread. Show Progress Bar Dialog */
//  @Override
  protected void onPreExecute() {
          super.onPreExecute();
          if(profileViewReference!=null)
        	  progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
  }
    
    /**
     * Actual download method.
     */
    @Override
    protected String doInBackground(String... params) {
    	fileId = params[0];
//		System.out.println("[doInBackground] - fileId : "+fileId);
		if(adapter != null && adapter instanceof ChatHomeAdapter && ((ChatHomeAdapter)adapter).inProcessing(fileId))
			return null;
//        return downloadBitmap(url);
        if(params != null && params.length > 1){
        	if(params[1].equals(THUMB_REQUEST))
        		isThumbRequest = true;
        	else if(params[1].equals(PIC_VIEW_REQUEST))
        		isPicViewRequest = true;
        	else if(params[1].equals(PROFILE_PIC_REQUEST))
        		isProfilePicViewRequest = true;
        }
        if(fileId!=null && fileId.equals("clear"))
        	return null;
		  int count;
//		  String esiDirectory = Environment.getExternalStorageDirectory().getPath()+ File.separator + "SuperChat";
		  
			
		  String filename = null;
		  File file = null;
		  if(isThumbRequest){
			  filename = Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+fileId+".jpg";
			  file = new File(Environment.getExternalStorageDirectory().getPath(), File.separator +Constants.contentProfilePhoto);
		  } else{
			  filename = Environment.getExternalStorageDirectory().getPath()+ File.separator + "SuperChat/"+fileId+".jpg";
			  file = new File(Environment.getExternalStorageDirectory().getPath(), File.separator + "SuperChat/");
		  }
		 
		    if (!file.exists()) {
		        file.mkdirs();
		    }
		    if(filename!=null){
	    		  File file1 = new File(filename);
	    		  if(file1.exists())
	    			  return filename;
	          }
		if(adapter != null && adapter instanceof ChatHomeAdapter)
			((ChatHomeAdapter)adapter).addProcessing(fileId);
//		  File file1 = new File(filename);
////		  if(!file1.exists())
//				file1.mkdir();
		  try {
			  ConnectivityManager cm = (ConnectivityManager) SuperChatApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE);
			  NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
				if(!isConnected){
					filename = Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+fileId+".jpg";
					 File file1 = new File(filename);
		    		  if(file1.exists())
		    			  return filename;
					return null;
				}
//			  if(params[0] != null && params[0].length() > 0)
//				  filename += params[0].substring(params[0].lastIndexOf('/'));
			  Log.d(TAG,"Pic uploaded info2: "+filename+" , "+params[0]);
		      URL url = null;
		      int pixels = (int)convertDpToPixel(75);
		      if(!isThumbRequest)
	    		  url =  new URL(Constants.media_get_url+params[0]+".jpg");
		      else
		    	  url =  new URL(Constants.media_convertget_url+params[0]+".jpg?height="+pixels+"&width="+pixels);
		      URLConnection conection = url.openConnection();
		      conection.connect();
		      // getting file length
		  int lenghtOfFile = conection.getContentLength();
		  // input stream to read file - with 8k buffer
		  InputStream input = new BufferedInputStream(url.openStream());
		  // Output stream to write file
			  System.out.println("[ filename - ] "+filename);
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
		          if(filename!=null){
		    		  File file1 = new File(filename);
		    		  file1.delete();
		    		  SuperChatApplication.removeBitmapFromMemCache(fileId);
		          }
    	  }catch(Throwable t){
    		  if(filename!=null){
	    		  File file1 = new File(filename);
	    		  file1.delete();
	    		  SuperChatApplication.removeBitmapFromMemCache(fileId);
	          }
    	  }
		  
		
		  return filename;
		}
    
    /**
     * Once the image is downloaded, associates it to the imageView
     */
    @Override
    protected void onPostExecute(String filePath) {
    	if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
        if (isCancelled()) {
        	if(filePath!=null){
	    		  File file1 = new File(filePath);
	    		  file1.delete();
	    		  SuperChatApplication.removeBitmapFromMemCache(fileId);
	          }
        	filePath = null;
        	return;
        }
        if(filePath == null)
        	return;
        if(isProfilePicViewRequest){
        	Bitmap bm = null;
    	    try{
    		    bm = BitmapFactory.decodeFile(filePath);
    		    bm = rotateImage(filePath, bm);
    	    }catch(Exception ex){
    	    	
    	    }
    	    if(bm!=null && profileRoundViewReference!=null){
    	    	RoundedImageView defaultView = profileRoundViewReference.get();
    	    	if(defaultView!=null)
    	    		defaultView.setImageBitmap(bm);
//    	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
    	    }
        }else if(isPicViewRequest){
        	Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			 File file1 = new File(filePath);
   		  	if(file1!=null && file1.exists())
   		  		intent.setDataAndType(Uri.parse("file://" + filePath), "image/*");
   		  	else{
   		  		file1 = new File(Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+fileId+".jpg");
	   		  	if(file1!=null && file1.exists())
	   		  		intent.setDataAndType(Uri.parse("file://" + file1.getPath()), "image/*");
	   		  	else{
	   		  		Toast.makeText(context, "Picture not found", Toast.LENGTH_SHORT).show();
	   		  		return;
	   		  	}
   		  	}
			context.startActivity(intent);
        }else if (imageViewReference != null) {
        	RoundedImageView imageView = imageViewReference.get();
			ImageView defaultView = null;
        	if(imageView != null && filePath != null)
        	{
        		if(defaultViewReference!=null){
					defaultView = defaultViewReference.get();
        			if(defaultView!=null)
        				defaultView.setVisibility(View.INVISIBLE);
        		}
				if (imageView.getTag() != null && imageView.getTag().toString().contains(fileId)){
//					System.out.println("[doInBackground] - filimageView.getTag() : "+imageView.getTag().toString());
					imageView.setVisibility(View.VISIBLE);
					if(adapter != null && adapter instanceof ChatHomeAdapter)
						((ChatHomeAdapter)adapter).removeProcessing(fileId);
					setThumb(imageView, filePath, fileId);
				}else{
					if(adapter instanceof ChatHomeAdapter)
						((ChatHomeAdapter)adapter).updateUI();
					else if(adapter instanceof ContactsAdapter)
						((ContactsAdapter)adapter).updateUI();
					if(imageView.getTag() != null) {
						System.out.println("[[[[[ELSE CASE imageView.getTag() - ]]]]]] " + imageView.getTag());
						System.out.println("[[[[[ELSE CASE fileId - ]]]]]] " + fileId);
					}
//					if(defaultViewReference!=null){
//						defaultView = defaultViewReference.get();
//						if(defaultView!=null) {
//							System.out.println("[[[[[ELSE CASE - 2]]]]]]");
//							defaultView.setVisibility(View.INVISIBLE);
//							imageView.setVisibility(View.VISIBLE);
//							imageView.setImageResource(R.drawable.profile_about_me);
//						}
//					}else
					{
						System.out.println("[[[[[ELSE CASE - 3]]]]]]");
						if(defaultView != null) {
							System.out.println("[[[[[ELSE CASE - 4]]]]]]");
							imageView.setVisibility(View.VISIBLE);
							defaultView.setVisibility(View.VISIBLE);
						}else {
							System.out.println("[[[[[ELSE CASE - 5]]]]]]");
							imageView.setVisibility(View.VISIBLE);
//							if (imageView.getTag() != null){
//								if(adapter instanceof ChatHomeAdapter)
//									((ChatHomeAdapter)adapter).updateUI();
//							}
//								setThumb(imageView, filePath, fileId);
//							imageView.setImageResource(R.drawable.profile_about_me);
						}
					}
				}
        	}
        }else if(profileViewReference!=null){
    			ImageView defaultView = profileViewReference.get();
    			if(defaultView!=null){
					if (defaultView.getTag() != null && defaultView.getTag().toString().contains(fileId)) {
						defaultView.setVisibility(View.VISIBLE);
						setThumbWithoutCashing(defaultView, filePath, fileId);
					}
//    				setThumb(defaultView, filePath,fileId);
//    				updateProfilePic(fileId);
				}
        } 
    }
    private static float convertPixelsToDp(float px){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(dp);
    }
    private static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return Math.round(px);
    }
    private void updateProfilePic(String picId){
//    	 SuperChatApplication.removeBitmapFromMemCache(fileId);
//    	String filename = Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+picId+".jpg";
//    	File file1 = new File(filename);
//		 boolean isDeleted =  file1.delete();
//		  filename = Environment.getExternalStorageDirectory().getPath()+ File.separator + "SuperChat/"+fileId+".jpg";
//		  file1 = new File(filename);
//		  boolean isMoved =   file1.renameTo(new File(Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+picId+".jpg"));
//		 Log.d(TAG, "file deleted and moved status : "+isDeleted+" , "+isMoved);
//		  Bitmap bm = null;
//		    try{
//			    bm = BitmapFactory.decodeFile(file1.getPath(), null);
//			    bm = rotateImage(file1.getPath(), bm);
//			    SuperChatApplication.addBitmapToMemoryCache(picId,bm);
//		    }catch(Exception ex){
//		    }
    }
    private void setThumb(RoundedImageView imageViewl,String path,String groupPicId){
		BitmapFactory.Options bfo = new BitmapFactory.Options();
	    bfo.inSampleSize = 2;
	    Bitmap bm = null;
	    try{
		    bm = BitmapFactory.decodeFile(path);
		    bm = rotateImage(path, bm);
	    }catch(Exception ex){
	    	
	    }
	    if(bm!=null){
	    	imageViewl.setImageBitmap(bm);
//	    	SuperChatApplication.addBitmapToMemoryCache(groupPicId,bm);
	    } else{
	    	try{
	    		imageViewl.setImageURI(Uri.parse(path));
	    	}catch(Exception e){
	    		
	    	}
	    }
//	    if (Build.VERSION.SDK_INT >= 11)
//			new ImageLoadTask(path,groupPicId,imageViewl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//         else
//        	 new ImageLoadTask(path,groupPicId, imageViewl).execute();
	}
    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

	    private String filePath;
	    private String fileId;
	    private ImageView imageView;

	    private ImageLoadTask(String filePath,String fileId, ImageView imageView) {
	    	this.fileId = fileId;
	        this.filePath = filePath;
	        this.imageView = imageView;
	    }
	    private ImageLoadTask(String filePath,String fileId, RoundedImageView imageView) {
	    	this.fileId = fileId;
	        this.filePath = filePath;
	        this.imageView = imageView;
	    }
	    @Override
	    protected Bitmap doInBackground(Void... params) {
//	    	FileInputStream  input = null;
//	        try {
//	        	File file = new File(filePath);
//	        	if(file==null || !file.exists())
//	        		return null;
//	        	 input = new FileInputStream(file.getAbsolutePath());
//	        	if(input!=null){
//	        		Bitmap myBitmap = BitmapFactory.decodeStream(input);
//	        	    return myBitmap;
//	        	}
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	        finally{
//	        	if(input!=null)
//	        	try {
//					input.close();
//					input = null;
//				} catch (Exception e) {
//				}
//	        }
	    	 Bitmap bm = null;
	    	  try{
	    		    SuperChatApplication.removeBitmapFromMemCache(fileId);
	    	    	String filename = Environment.getExternalStorageDirectory().getPath()+ File.separator +Constants.contentProfilePhoto+fileId+".jpg";
	    	    	File file1 = new File(filename);
				    bm = BitmapFactory.decodeFile(file1.getPath(), null);
				    bm = rotateImage(file1.getPath(), bm);
				    if(bm!=null)
				    	SuperChatApplication.addBitmapToMemoryCache(fileId,bm);
			    }catch(Exception ex){
			    }
	        return bm;
	    }

	    @Override
	    protected void onPostExecute(Bitmap result) {
	        super.onPostExecute(result);
	        if(result!=null){
		        imageView.setImageBitmap(result);
//		        if(fileId!=null){
//		             SuperChatApplication.addBitmapToMemoryCache(fileId,result);
//	            }
	        }
	    }

	}
    private void setThumbWithoutCashing(ImageView imageViewl,String path,String groupPicId){
//		BitmapFactory.Options bfo = new BitmapFactory.Options();
//	    bfo.inSampleSize = 2;
	    Bitmap bm = null;
	    try{
		    bm = BitmapFactory.decodeFile(path, null);
		    bm = rotateImage(path, bm);
	    }catch(Exception ex){
	    	
	    }
	    if(bm!=null){
	    	imageViewl.setImageBitmap(bm);
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
		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		}
			
		return bm;
	}
}
