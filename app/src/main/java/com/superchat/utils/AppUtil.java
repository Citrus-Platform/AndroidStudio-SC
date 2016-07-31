package com.superchat.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.superchat.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;


public class AppUtil {

	public static final byte POSITION_GALLRY_PICTURE = 123;
	public static final byte POSITION_CAMERA_PICTURE = 2;
	public static final byte POSITION_CAMERA_PICTURE_LEFT = 3;
	public static final byte POSITION_CAMERA_PICTURE_RIGHT = 4;
	public static final byte POSITION_GALLRY_PICTURE_LEFT = 5;
	public static final byte POSITION_GALLRY_PICTURE_RIGHT = 6;
	public static final byte POSITION_GALLRY_PROFILE_PICTURE = 7;
	public static final byte POSITION_CAMERA_PROFILE_PICTURE = 8;
	public static final byte PIC_CROP = 13;
	public static final byte PIC_CROP_LEFT = 8;
	public static final byte PIC_CROP_RIGHT = 9;
	public static final byte POSITION_GALLRY_VIDEO = 10;
	public static final byte POSITION_CAMERA_VIDEO = 11;
	public static final byte FILE_PDF_PICKER = 14;
	public static final byte FILE_AUDIO_TRACK = 15;
	
	public static final byte ADD_COLOR = 10;

	public static final String cost = "$%s";//(%s$ per day)";

	public static final int RATE_CONST = 20;

	public static final int RADIUS = 4000;
	public static final double lat = 28.6100;
	public static final double lon = 77.2300;

	public static String capturedPath1, capturedPath2;



	public static void openCamera(Object context, String cameraImagePathL, byte resultCode) {
		try {
			File file = new File(Environment.getExternalStorageDirectory(),
					getRandomNumber() + ".jpg");
			// if(capturedPath1==null)
			capturedPath1 = file.getPath();
			// if(capturedPath2==null)
			// capturedPath2 = file.getPath();
			Uri outputFileUri = Uri.fromFile(file);
			Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
			i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			((Activity) context).startActivityForResult(i, resultCode);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Bitmap rotateImage(String path, Bitmap bm) {
			int orientation = 1;
		try {
			ExifInterface exifJpeg = new ExifInterface(path);
			  orientation = exifJpeg.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//				orientation = Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
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
			return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
			}
				
			return bm;
		}
	public static void openVideo(Object context,
			byte resultCode) {
		try {
			 Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			 intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); 
			 ((Activity) context).startActivityForResult(intent, POSITION_CAMERA_VIDEO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void openGallery(Object context, byte resultCode) {
		try {
			Intent i = new Intent(Intent.ACTION_PICK);
			i.setType("image/* video/*");
			((Activity) context).startActivityForResult(i, resultCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void openImageGallery(Object context, byte resultCode) {
		try {
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			((Activity) context).startActivityForResult(i, resultCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void openPdf(Object context, byte resultCode) {
		 final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		 Intent intent = null;
		 if(isKitKat){
				 intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
			 intent.setType("*/*"); 
//			 String[] mimetypes = {"image/*", "video/*"};
//			 String[] mimetypes = {"application/pdf", "application/msword","application/x-excel",
//					 "application/vnd.ms-excel","application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
//					 "application/vnd.openxmlformats-officedocument.presentationml.presentation",
//					 "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//			 		"application/vnd.openxmlformats-officedocument.wordprocessingml.template"};
//			 intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
//			 intent.setType("file/*");
//			 intent.setType("application/*");
		 }else{
			 intent = new Intent(Intent.ACTION_GET_CONTENT);
//			 intent.setType("application/*");
			 intent.setType("*/*"); 
		 }
//		if(resultCode == FILE_PDF_PICKER){
////			intent.setType("*/msword */pdf ");  
////			intent.setType("application/*");
////			intent.setType("application/pdf|application/msword");
////			intent.setType("application/x-excel");
//		}else{
//			intent.setType("*/*"); 
//		}
	    intent.addCategory(Intent.CATEGORY_OPENABLE);

	    try {
	    	((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
	                resultCode);
	    } catch (android.content.ActivityNotFoundException ex) {
	        // Potentially direct the user to the Market with a Dialog
//	        Toast.makeText(this, "Please install a File Manager.", 
//	                Toast.LENGTH_SHORT).show();
	    }
	}
	public static void openFilesToShare(Object context, byte resultCode) {
		PackageManager pm = ((Activity) context).getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage("com.google.android.apps.docs");
		intent.setType("*/*");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Intent.EXTRA_SUBJECT, "attach a file test");
		intent.addCategory(Intent.ACTION_ATTACH_DATA);
		try {
			((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
					resultCode);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
//	        Toast.makeText(this, "Please install a File Manager.", 
//	                Toast.LENGTH_SHORT).show();
		}
	}
	public static void openVideoGallery(Object context, byte resultCode) {
		try {
			Intent i = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
			((Activity) context).startActivityForResult(i, resultCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static long getRandomNumber() {
		return System.currentTimeMillis();// rand.nextInt();
	}

	public static String getPath(Uri uri, Context context) {

		try {
			String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = ((Activity) context).managedQuery(uri, projection,
					null, null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} catch (Exception e) {
			return uri.getPath();
		}
	}

	public static void setImage(ImageView imageView, Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
	}
	
	public static void showTost(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		//toast_screen.xml
//		LayoutInflater inflater = LayoutInflater.from(context);;
//		View retView = inflater.inflate(R.layout.toast_screen, null,
//				false);
//		((TextView)retView.findViewById(R.id.msg)).setText(msg);
//		toast.setView(retView);
		toast.show();
	}
	

	public static void clearAppData() {
		
		capturedPath1 = null;
		capturedPath2 = null;
		// if(templates!=null)
		// templates.clear();
		removeTempFile();
	}

	public static void showAlert(final Activity activity,String title,String msg,final int action) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				activity);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
				.setMessage(msg)
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								// MainActivity.this.finish();
								if(action==1){
									activity.finish();
//									Intent intent = new Intent(activity,
//											MenuActivity.class);
//									activity.startActivity(intent);
								}
							}
						});
//				.setNegativeButton("No", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//						// if this button is clicked, just close
//						// the dialog box and do nothing
//						dialog.cancel();
//					}
//				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        // Log exception
	        return null;
	    }
	}
	public static boolean checkInternetConnection(final Activity context) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET

		if (conMgr.getActiveNetworkInfo() != null

		&& conMgr.getActiveNetworkInfo().isAvailable()

		&& conMgr.getActiveNetworkInfo().isConnected()) {
			// System.out.println("================network avilable==========================");
			return true;

		} else {
			// System.out.println("================network not avilable==========================");
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					showTost(context, context.getString(R.string.check_net_connection)) ;
				}
			});
			return false;

		}
	}
	public static final String TEMP_PHOTO_FILE = "temp.jpg";
	public static File getTempFile() {

	    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

	    	  File file = new File(Environment.getExternalStorageDirectory().getPath(), Constants.contentTemp);//"MyFolder/Images");
	  	    if (!file.exists()) {
	  	        file.mkdirs();
	  	    }
	  	    
	         file = new File(Environment.getExternalStorageDirectory(),Constants.contentTemp+"/"+TEMP_PHOTO_FILE);
	        try {
	            file.createNewFile();
	        } catch (IOException e) {}

	        return file;
	    } else {

	        return null;
	    }
	}
	public static boolean removeTempFile() {

	    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

//	    	  File file = new File(Environment.getExternalStorageDirectory().getPath(), Constants.contentTemp);//"MyFolder/Images");
//	  	    if (!file.exists()) {
//	  	        file.mkdirs();
//	  	    }
	    	
	        try {
	        	File file = new File(Environment.getExternalStorageDirectory(),Constants.contentTemp+"/"+TEMP_PHOTO_FILE);
	        	if(file!=null && file.exists())
	        		file.delete();
	        } catch (Exception e) {}

	        return true;
	    } else {

	        return false;
	    }
	}
	public static Uri getTempUri() {
	    return Uri.fromFile(getTempFile());
	}	
	
	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @author paulburke
	 */
	public static String getPath(final Context context, final Uri uri) {

	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }

	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };

	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {

	        // Return the remote address
	        if (isGooglePhotosUri(uri))
	            return uri.getLastPathSegment();

	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
}
