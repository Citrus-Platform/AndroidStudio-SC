/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.superchat.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.superchat.*;
import com.superchat.ui.ChatListAdapter;

/**
 * This helper class download images from the Internet and binds those with the provided ImageView.
 *
 * <p>It requires the INTERNET permission, which should be added to your application's manifest
 * file.</p>
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class ImageDownloader {
    private static final String LOG_TAG = "ImageDownloader";

    public enum Mode { NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT }
    private Mode mode = Mode.CORRECT;
    ChatListAdapter adaptor;
    
    /**
     * Download the specified image from the Internet and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param url The URL of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
//    public void download(String url, ImageView imageView) {
//        resetPurgeTimer();
//        Bitmap bitmap = getBitmapFromCache(url);
//
//        if (bitmap == null) {
//            forceDownload(url, imageView);
//        } else {
//            cancelPotentialDownload(url, imageView);
//            imageView.setImageBitmap(bitmap);
//        }
//    }
    private HashMap<String, String> processing = new HashMap<String, String>();
    public void download(String url, ImageView imageView, ProgressBar pb, Object[] callbackParams) {
    	resetPurgeTimer();
    	String localPath = getBitmapFromCache(url);
    	
    	if (localPath == null) {
    		processing.put(url, "true");
    		adaptor = (ChatListAdapter)callbackParams[0];
    		forceDownload(url, imageView, pb, callbackParams);
    	} else {
    		cancelPotentialDownload(url, imageView, pb, callbackParams);
    		imageView.setImageURI(Uri.parse(localPath));
    		if(pb != null)
    			pb.setVisibility(View.GONE);
    	}
    }
    public String getProcessingForURL(String url)
    {
    	return ((String)processing.get(url));
    }
    public void clearMap()
    {
    	 processing.clear();
    }

    /*
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
       private void forceDownload(String url, ImageView view) {
          forceDownload(url, view, null);
       }
     */

    /**
     * Same as download but the image is always downloaded and the cache is not used.
     * Kept private at the moment as its interest is not clear.
     */
    private void forceDownload(String url, ImageView imageView, ProgressBar pb, Object[] params) {
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (url == null) {
//            imageView.setImageDrawable(null);
        	if(imageView!=null)
        		imageView.setVisibility(View.GONE);
            return;
        }

        if (cancelPotentialDownload(url, imageView, pb, params)) {
            switch (mode) {
//                case NO_ASYNC_TASK:
//                    Bitmap bitmap = downloadBitmap(url);
//                    addBitmapToCache(url, bitmap);
//                    imageView.setImageBitmap(bitmap);
//                    break;
//
//                case NO_DOWNLOADED_DRAWABLE:
//                    imageView.setMinimumHeight(156);
//                    BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, pb);
//                    task.execute(url);
//                    break;

                case CORRECT:
                	BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, pb, params);
                    DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                    if(imageView!=null)
                    imageView.setImageDrawable(downloadedDrawable);
                    if(pb != null)
                    {
                    	pb.setVisibility(View.VISIBLE);
                    	pb.setProgress(0);
                    }
                    if(imageView!=null)
                    imageView.setMinimumHeight(156);
                    task.execute(url);
                    break;
            }
        }
    }
    
//    private void forceDownloadWithProgress(String url, ImageView imageView, ProgressBar pb) {
//    	// State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
//    	if (url == null) {
//    		imageView.setImageDrawable(null);
//    		return;
//    	}
//    	
//    	if (cancelPotentialDownload(url, imageView)) {
//    		switch (mode) {
//    		case NO_ASYNC_TASK:
//    			Bitmap bitmap = downloadBitmap(url);
//    			addBitmapToCache(url, bitmap);
//    			imageView.setImageBitmap(bitmap);
//    			break;
//    			
//    		case NO_DOWNLOADED_DRAWABLE:
//    			imageView.setMinimumHeight(156);
//    			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
//    			task.execute(url);
//    			break;
//    			
//    		case CORRECT:
//    			task = new BitmapDownloaderTask(imageView);
//    			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
//    			imageView.setImageDrawable(downloadedDrawable);
//    			imageView.setMinimumHeight(156);
//    			task.execute(url);
//    			break;
//    		}
//    	}
//    }

    /**
     * Returns true if the current download has been canceled or if there was no download in
     * progress on this image view.
     * Returns false if the download in progress deals with the same url. The download is not
     * stopped in that case.
     */
    private boolean cancelPotentialDownload(String url, ImageView imageView, ProgressBar pb, Object[] params) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
     */
    private BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

//    Bitmap downloadBitmap(String url) {
//        final int IO_BUFFER_SIZE = 4 * 1024;
//
//        // AndroidHttpClient is not allowed to be used from the main thread
//        final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient() :
//            AndroidHttpClient.newInstance("Android");
//        final HttpGet getRequest = new HttpGet(url);
//
//        try {
//            HttpResponse response = client.execute(getRequest);
//            final int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != HttpStatus.SC_OK) {
//                Log.w("ImageDownloader", "Error " + statusCode +
//                        " while retrieving bitmap from " + url);
//                return null;
//            }
//
//            final HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream inputStream = null;
//                try {
//                    inputStream = entity.getContent();
//                    // return BitmapFactory.decodeStream(inputStream);
//                    // Bug on slow connections, fixed in future release.
//                    return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
//                } finally {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    entity.consumeContent();
//                }
//            }
//        } catch (IOException e) {
//            getRequest.abort();
//            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
//        } catch (IllegalStateException e) {
//            getRequest.abort();
//            Log.w(LOG_TAG, "Incorrect URL: " + url);
//        } catch (Exception e) {
//            getRequest.abort();
//            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
//        } finally {
//            if ((client instanceof AndroidHttpClient)) {
//                ((AndroidHttpClient) client).close();
//            }
//        }
//        return null;
//    }

    /*
     * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
     */
     class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    /**
     * The actual AsyncTask that will asynchronously download the image.
     */
    class BitmapDownloaderTask extends AsyncTask<String, Integer, String> {
        private String url;
        Object[] objParams;
        private WeakReference<ImageView> imageViewReference = null;
        private final WeakReference<ProgressBar> progressbar;
        private final WeakReference<Object[]> callbakParams;

        public BitmapDownloaderTask(ImageView imageView, ProgressBar pb, Object[] params) {
        	if(imageView!=null)
        		imageViewReference = new WeakReference<ImageView>(imageView);
            progressbar = new WeakReference<ProgressBar>(pb);
            callbakParams = new WeakReference<Object[]>(params);
        }

//      /*  Before starting background thread. Show Progress Bar Dialog */
//      @Override
      protected void onPreExecute() {
              super.onPreExecute();
            if(imageViewReference != null)
            	imageViewReference.get().setBackgroundResource(R.drawable.def_bt_img);
            if(progressbar != null)
          	{
          		ProgressBar pBar = progressbar.get();
          		pBar.setProgress(0);
          		pBar.setVisibility(View.VISIBLE);
          	}
            if(callbakParams != null)
            {
            	objParams = callbakParams.get();
            	TextView tv = (TextView)objParams[3];
            	tv.setVisibility(View.VISIBLE);
            	tv.setText("initiating..");
            }
      }
        
        /**
         * Actual download method.
         */
        @Override
        protected String doInBackground(String... params) {
            url = params[0];
//            return downloadBitmap(url);
            
			  int count;
			  String filename = Environment.getExternalStorageDirectory().getPath()+ File.separator + "Atme";
			  try {
				  if(params[0] != null && params[0].length() > 0)
					  filename += params[0].substring(params[0].lastIndexOf('/'));
			      URL url = new URL(params[0]);
			      URLConnection conection = url.openConnection();
			      conection.connect();
			      // getting file length
			  int lenghtOfFile = conection.getContentLength();
			  // input stream to read file - with 8k buffer
			  InputStream input = new BufferedInputStream(url.openStream());
			  // Output stream to write file
			  OutputStream output = new FileOutputStream(filename);
			  Environment.getExternalStorageDirectory().getPath();
			  byte data[] = new byte[4096]; 
			  long total = 0;
			  while ((count = input.read(data)) != -1) {
			      total += count;
			      // publishing the progress....
			  // After this onProgressUpdate will be called
			  publishProgress((int)total, (int) lenghtOfFile);
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
			  return filename;
			}
        
        @Override
        protected void onProgressUpdate(Integer... progress) {
//          // setting progress percentage
        	if(progressbar != null)
        	{
        		ProgressBar pBar = progressbar.get();
        		TextView tv = (TextView)objParams[3]; 
        		if(pBar != null)
        		{
        			tv.setText("["+progress[0]/1024 + "KB of " +progress[1]/1024 + "KB]");
//        			tv.setText(Integer.parseInt(""+progress[0]) + "%");
        			pBar.setProgress(Integer.parseInt(""+(int)((progress[0]*100)/progress[1])));
//        			pBar.setSecondaryProgress(Integer.parseInt(""+progress[0]) + 5);
        		}
        	}
        }

        /**
         * Once the image is downloaded, associates it to the imageView
         */
        @Override
        protected void onPostExecute(String filePath) {
            if (isCancelled()) {
            	filePath = null;
            }

//            addBitmapToCache(url, filePath);
            

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
//                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
//                if ((this == bitmapDownloaderTask) || (mode != Mode.CORRECT)) 
                {
                	if(imageView != null && filePath != null)
                	{
                		imageView.setImageURI(Uri.parse(filePath));
                		imageView.setBackgroundDrawable(null);
                	}
                    if(progressbar != null)
                    {
                    	ProgressBar pBar = progressbar.get();
                    	if(pBar != null)
                    		pBar.setVisibility(View.GONE);
                    	TextView tv = (TextView)objParams[3]; 
                    	tv.setVisibility(View.GONE);
                    }
//                    addBitmapToCache(url, filePath);
//                    processing.remove(url);
                    System.out.println("<<   view updated - >> "+url);
//                    if(params != null && params.length == 3)
                    if(adaptor != null && objParams != null)
                    {
                    	adaptor.updateDataWithCursor(filePath, (String)objParams[1], imageView);
                    }
                }
            }else{
            	 if(adaptor != null && objParams != null)
                 {
                 	adaptor.updateDataWithCursor(filePath, (String)objParams[1], null);
                 }
            }
        }
    }
//--------------- Mahesh - My Code ---------
    /* Background Async Task to download file */
//    class DownloadFileFromURL extends AsyncTask<String, String, String> {
//                    /*  Before starting background thread. Show Progress Bar Dialog */
//                    @SuppressWarnings("deprecation")
//                    @Override
//                    protected void onPreExecute() {
//                            super.onPreExecute();
////                            showDialog(CUSTOM_PROGRESS_DIALOG);
//                    }
//                    /* Downloading file in background thread */
//                    @Override
//                    protected String doInBackground(String... f_url) {
//                            int count;
//                    try {
//                        URL url = new URL(f_url[0]);
//                        URLConnection conection = url.openConnection();
//                        conection.connect();
//                        // getting file length
//                        int lenghtOfFile = conection.getContentLength();
//                        // input stream to read file - with 8k buffer
//                        InputStream input = new BufferedInputStream(url.openStream(), 8192);
//                        // Output stream to write file
//                        OutputStream output = new FileOutputStream("/sdcard/filedownload.jpg");
//                        byte data[] = new byte[1024]; 
//                        long total = 0;
//                        while ((count = input.read(data)) != -1) {
//                            total += count;
//                            // publishing the progress....
//                            // After this onProgressUpdate will be called
//                            publishProgress(""+(int)((total*100)/lenghtOfFile));
//                            // writing data to file
//                            output.write(data, 0, count);
//                        }
//                        // flushing output
//                        output.flush();
//                        // closing streams
//                        output.close();
//                        input.close();
//                       
//                    } catch (Exception e) {
//                            Log.e("Error: ", e.getMessage());
//                    }
//                    return null;
//                    }
//                    /* Updating progress bar */
//                    protected void onProgressUpdate(String... value) {
//                            // setting progress percentage
//                            pDialog.setProgress(Integer.parseInt(value[0]));
//                            pDialog.setSecondaryProgress(Integer.parseInt(value[0]) + 5);
//         }
//                    /*  After completing background task. Dismiss the progress dialog */
//                    @SuppressWarnings("deprecation")
//                    @Override
//                    protected void onPostExecute(String file_url) {
//                            // dismiss the dialog after the file was downloaded
//                            dismissDialog(CUSTOM_PROGRESS_DIALOG);
//                            // Displaying downloaded image into image view
//                            // Reading image path from sdcard
//                            String imagePath = Environment.getExternalStorageDirectory().toString() + "/filedownload.jpg";
//                            // setting downloaded into image view
//                            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
//                    }
//            }
    
//-----------------------------------------

    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     *
     * <p>Contains a reference to the actual download task, so that a download task can be stopped
     * if a new binding is required, and makes sure that only the last started download process can
     * bind its result, independently of the download finish order.</p>
     */
     class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
//            super(Color.GRAY);
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        clearCache();
    }

    
    /*
     * Cache-related fields and methods.
     * 
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */
    
    private static final int HARD_CACHE_CAPACITY = 50;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, String> sHardBitmapCache = new LinkedHashMap<String, String>(HARD_CACHE_CAPACITY / 2, 0.75f, true) 
        {
        @Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, String> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                // Entries push-out of hard reference cache are transferred to soft reference cache
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<String>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };

    // Soft cache for bitmaps kicked out of hard cache
    private final ConcurrentHashMap<String, SoftReference<String>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<String>>(HARD_CACHE_CAPACITY / 2);

    private final Handler purgeHandler = new Handler();

    private final Runnable purger = new Runnable() {
        public void run() {
            clearCache();
        }
    };

    /**
     * Adds this bitmap to the cache.
     * @param bitmap The newly downloaded bitmap.
     */
//    private void addBitmapToCache(String url, Bitmap bitmap) {
//        if (bitmap != null) {
//            synchronized (sHardBitmapCache) {
//                sHardBitmapCache.put(url, bitmap);
//            }
//        }
//    }
    private void addBitmapToCache(String url, String localPath) {
    	if (localPath != null) {
    		synchronized (sHardBitmapCache) {
    			sHardBitmapCache.put(url, localPath);
    			System.out.println("[url] - "+url+ ", [localPath] - "+localPath);
    		}
    	}
    }

    /**
     * @param url The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private String getBitmapFromCache(String url) {
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final String localPath = sHardBitmapCache.get(url);
            if (localPath != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, localPath);
                return localPath;
            }
        }

        // Then try the soft reference cache
        SoftReference<String> bitmapReference = sSoftBitmapCache.get(url);
        if (bitmapReference != null) {
            final String bitmap = bitmapReference.get();
            if (bitmap != null) {
                // Bitmap found in soft cache
                return bitmap;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }
 
    /**
     * Clears the image cache used internally to improve performance. Note that for memory
     * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
     */
    public void clearCache() {
//        sHardBitmapCache.clear();
//        sSoftBitmapCache.clear();
    }

    /**
     * Allow a new delay before the automatic cache clear is done.
     */
    private void resetPurgeTimer() {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }
}
