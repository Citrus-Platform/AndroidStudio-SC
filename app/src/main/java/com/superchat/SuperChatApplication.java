package com.superchat;


//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.multidex.MultiDexApplication;
import android.support.v4.util.LruCache;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.ContactUpDatedModel;
import com.superchat.model.ContactUpDatedModel.UserDetail;
import com.superchat.model.ContactUploadModel;
import com.superchat.ui.ContactsScreen;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.Constants;
import com.superchat.utils.Countries;
import com.superchat.utils.Countries.Country;
import com.superchat.utils.HttpHeaderUtils;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import com.crashlytics.android.Crashlytics;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;

public class SuperChatApplication extends MultiDexApplication {
	 protected static final String TAG = SuperChatApplication.class.getSimpleName();
	public static Context context = null;
	 public static LruCache mMemoryCache;
//	 public static int dayValue = -1;
	 public static final byte CONTACT_SYNC_IDLE = 0;
	 public static final byte CONTACT_SYNC_START = 1;
	 public static final byte CONTACT_SYNC_SUCESSED = 2;
	 public static final byte CONTACT_SYNC_FAILED = 3;
	 public static final byte CONTACT_SYNC_WAIT = 4;
	 public static byte contactSyncState = 0;
	  SharedPrefManager iPrefManager;
	  public static HashSet<String> countrySet = new HashSet<String>();
	  public static float density = 1;
	  public static Point displaySize = new Point();
	  public static DisplayMetrics displayMetrics = new DisplayMetrics();
	  public static List<String> blockUserList = new ArrayList<String>();
//	private static final String PROPERTY_ID = "UA-67304935-1";
//    public static int GENERAL_TRACKER = 0;
//    public enum TrackerName {
//    	        APP_TRACKER, GLOBAL_TRACKER, ECOMMERCE_TRACKER,
//    	    }
//    public HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
//    public SuperChatApplication() {
//        super();
//    }
//    public synchronized Tracker getTracker(TrackerName appTracker) {
//        if (!mTrackers.containsKey(appTracker)) {
//            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//            Tracker t = (appTracker == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID) : (appTracker == TrackerName.GLOBAL_TRACKER) 
//            		? analytics.newTracker(R.xml.global_tracker) : analytics.newTracker(R.xml.ecommerce_tracker);
//            mTrackers.put(appTracker, t);
//        }
//        return (Tracker) mTrackers.get(appTracker);
//    }

	public Context getContextApplication(){
		if(context == null){
			context = getApplicationContext();
		}
		return context;
	}

	private static SuperChatApplication mInstance;
	public static synchronized SuperChatApplication getInstance() {
		return mInstance;
	}
	
	public void onCreate() {
		super.onCreate();
//		Fabric.with(this, new Crashlytics());
		mInstance = this;
		context = getApplicationContext();
		 density = context.getResources().getDisplayMetrics().density;
		 checkDisplaySize();
		iPrefManager = SharedPrefManager.getInstance();
//		iPrefManager.saveLastOnline(-1); //new1
		for(Country country :Country.values()){
			countrySet.add(country.getCode()+"");
		}
		try{
			
			Constants.countryCode = "+91";
			TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			String countryCode = tm.getSimCountryIso();
//			if(countryCode!=null && countryCode.equalsIgnoreCase("in"))
//				Constants.countryCode = "+91";
//			if(Countries.getCodeValue("sa")!=null)
			if(countryCode!=null)
				Constants.countryCode = "+"+Countries.getCodeValue(countryCode).getCode();
		}catch(Exception e){}
		
        final int id = android.os.Process.myPid();
        Log.d(TAG, "onCreate getUidForName myPid: "+ id);
        Log.d(TAG, "onCreate getUidForName : "+ getAppNameByPID(context, id));
        
		getContentResolver().registerContentObserver(ContactsContract.Data.CONTENT_URI, true, new ContentObserver(null){
			long time2 = 0;
			   @Override
			         public void onChange(boolean selfChange) {
				   		super.onChange(selfChange);
				   		long time1 = System.currentTimeMillis();
				   		long thrsold = 5000;
				   		if(time1>(time2+thrsold)){
				   		 time2 = System.currentTimeMillis();
						   		Log.d(TAG, "onChange RunningAppProcessInfo getUidForName onChange : "+ getAppNameByPID(context, id));
					            if (getAppNameByPID(context, id).equals("com.superchat")){
//						            iPrefManager.setContactModified(true);
//						            Log.d(TAG, "onChange contact modifided status at application :"+selfChange);
//						        	if(iPrefManager.isContactModified())
//						    		{
//						        		iPrefManager.setContactModified(false);
//						    			updateContactsAsync();
//						    			contactSyncState = CONTACT_SYNC_WAIT;
//						    			
//						    		}
					        	}
					   }
			        }
			   
			   });
		mMemoryCache = new LruCache((int)(Runtime.getRuntime().maxMemory() / 1024L) / 8) {


            protected int sizeOf(Object obj, Object obj1)
            {
                return sizeOf((String)obj, (Bitmap)obj1);
            }

            protected int sizeOf(String s, Bitmap bitmap)
            {
                return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
            }
        };
	}
	 public static int dp(float value) {
	        if (value == 0) {
	            return 0;
	        }
	        return (int)Math.ceil(density * value);
	    }
	 public static void checkDisplaySize() {
	        try {
	            Configuration configuration = context.getResources().getConfiguration();
	            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	            if (manager != null) {
	                Display display = manager.getDefaultDisplay();
	                if (display != null) {
	                    display.getMetrics(displayMetrics);
	                    if (android.os.Build.VERSION.SDK_INT < 13) {
	                        displaySize.set(display.getWidth(), display.getHeight());
	                    } else {
	                        display.getSize(displaySize);
	                    }
//	                    FileLog.e("tmessages", "display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
	                }
	            }
	        } catch (Exception e) {
//	            FileLog.e("tmessages", e);
	        }
	 }
	public static String getAppNameByPID(Context context, int pid){
	    ActivityManager manager 
	               = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

	    for(RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()){
	        if(processInfo.pid == pid){
	            return processInfo.processName;
	        }
	    }
	    return "";
	}
	static int counter = 0;
//	
//	public static void updateContactsAsync() {
//		(new Thread() {
//
//			public void run() {
//				List<String> numbersList = new ArrayList<String>();
//				boolean isNewNumber = false;
//				contactSyncState = CONTACT_SYNC_WAIT;
//				try{
//				counter = 0;
//				Uri CONTENT_URI = ContactsContract.Data.CONTENT_URI;
//				String _ID = ContactsContract.Contacts._ID;
//				String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
//				String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
//				Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//				String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
//				String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
//				Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
//				String DATA = ContactsContract.CommonDataKinds.Email.DATA;
//				
//				Log.i(TAG, "copyContactsAsync Contacts Copy Started");
//				long l = System.currentTimeMillis();
//				DBWrapper dbwrapper = DBWrapper.getInstance();
//				
//				
//			    
////				String query = "SELECT "+DatabaseConstants.RAW_CONTACT_ID+" FROM "+DatabaseConstants.TABLE_CONTACT_NAMES+" a, "+ContactsContract.Contacts.+" WHERE ";
////				dbwrapper.clearAllDB();
//				int contactId = 0x80000000;
//				String[] as1 = (new String[] { ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE });
//				Cursor cursor = SuperChatApplication.context.getContentResolver().query(CONTENT_URI, null,
//						"mimetype =?", as1, Phone_CONTACT_ID);
//				
//				if (cursor == null) {
//					return;
//				}
////				dbwrapper.beginTransaction();
//				String compositeNumber = "";
//				String contactName = "";
//				boolean isDuplicate = false;
//				String tmpRowId = "";
//				String previousRawId = "";
//				boolean isModified = false;
//				int totalContacts = 0;
//				while (cursor.moveToNext()) {
//					counter++;
//					try{
//						String displayedNumber = cursor.getString(cursor
//								.getColumnIndex(ContactsContract.Data.DATA1));
//					String formattedNum = formatNumber(displayedNumber);
//					int tmpContactId = cursor.getInt(cursor
//							.getColumnIndex(Phone_CONTACT_ID));
//					int j2 = cursor.getInt(cursor.getColumnIndex(_ID));
//					int k2 = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.DATA2));//cursor.getColumnIndex("data2"));
//					int phoneNumberType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
//					String rawId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
//					int version = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.VERSION));
//					if(tmpRowId!=null && rawId!=null && !tmpRowId.equals(rawId)){
//						 isNewNumber = false;
//						 isModified = dbwrapper.isContactModified(rawId,version);
//						 if(isModified){
//							 isNewNumber = true;
//							 tmpRowId = rawId;
//							 dbwrapper.deleteRow(DatabaseConstants.TABLE_NAME_CONTACT_NAMES, rawId);
//							 dbwrapper.deleteRow(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, rawId);
//						 }
//					 }
//						 if(!isModified){
//							 if(dbwrapper.isContactAvailable(rawId))
//								 continue;
//							 else
//								 isNewNumber = true;
//						 }else{
////							 if(!dbwrapper.isContactAvailable(tmpRowId))
////								 continue; 
//						 }
//					if (contactId != tmpContactId) {
//						
////						Log.d(TAG,
////								"compositeNumber last : "+compositeNumber);
//						if(contactId != 0x80000000){
//							if(totalContacts==0 && previousRawId!=null && !previousRawId.equals("")){
//								try{
//									dbwrapper.deleteRow(DatabaseConstants.TABLE_NAME_CONTACT_NAMES, previousRawId);
//								}catch(Exception e){
//									
//								}
//							}
//							try{
////								
//								if(compositeNumber!=null && !compositeNumber.equals("") && compositeNumber.endsWith(","))
//									compositeNumber = compositeNumber.substring(0, compositeNumber.length()-1);
//								}catch(Exception e){}
////								
////								if( isDuplicate && (compositeNumber == null || !compositeNumber.contains(","))){
////									try{
//////										Log.d(TAG, "copyContactsAsync compositeNumber deleteRows start :: "+compositeNumber);
////									boolean deleteRows = dbwrapper.deleteSingleNumberRow(DatabaseConstants.TABLE_CONTACT_NUMBERS,contactId);
//////									Log.d(TAG, "compositeNumber deleteRows end:: "+deleteRows);
////									}catch(Exception e){}
////								}else
//								if(compositeNumber!=null && !compositeNumber.equals("")){
////									if(compositeNumber.contains("9826466151"))
////										Log.d(TAG, "copyContactsAsync compositeNumber 9826466151 start :: "+isDuplicate+" , "+compositeNumber);
//									dbwrapper.updateCompositeContacts(String.valueOf(contactId),compositeNumber);
//									compositeNumber = "";
//									}
//							}
//						isDuplicate = false;
//						totalContacts = 0;
//						contactId = tmpContactId;
//						previousRawId = rawId;
//						String s1 = cursor.getString(cursor
//								.getColumnIndex(DISPLAY_NAME));
//						contactName = s1;
//						
////						String _id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
//						
////						String version = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.VERSION));
////						String timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
//						Log.d(TAG, "state details : "+contactId+" , "+rawId+" , "+contactName);//+version+" , "+timeStamp);
//						
//						ContentValues contentvalues = new ContentValues();
//						contentvalues.put(
//								DatabaseConstants.NAME_CONTACT_ID_FIELD,
//								Integer.valueOf(tmpContactId));
//						contentvalues.put(
//								DatabaseConstants.RAW_CONTACT_ID,
//								Integer.valueOf(rawId));
//						contentvalues.put(
//								DatabaseConstants.CONTACT_VERSION,
//								Integer.valueOf(version));
//						contentvalues.put(
//								DatabaseConstants.CONTACT_NAMES_FIELD, s1);
//						contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"");
//							contentvalues.put(DatabaseConstants.VOPIUM_FIELD,
//									Integer.valueOf(0));
//						contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,
//								Integer.valueOf(0));
////						contentvalues.put(DatabaseConstants.USER_SIP_ADDRESS,
////								"");
//						dbwrapper.insertInDB(
//								DatabaseConstants.TABLE_NAME_CONTACT_NAMES,
//								contentvalues);
////						compositeNumber = fatchEmailContactIds(contactId, dbwrapper,contactName);
////						if(compositeNumber==null)
//						compositeNumber = "";
//					}
//					
//					if(dbwrapper.isNumberExists(formattedNum)){
//						isDuplicate = true;
//						continue;
//					}
//					if(displayedNumber!=null && !displayedNumber.equals("") && !compositeNumber.contains(displayedNumber))
//						compositeNumber+=(displayedNumber+",");
//					ContentValues contentvalues1 = new ContentValues();
//					contentvalues1.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,
//							Integer.valueOf(tmpContactId));
//					contentvalues1.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,
//							formattedNum);
//					contentvalues1.put(DatabaseConstants.DISPLAY_NUMBERS_FIELD,displayedNumber);
//					if(isNewNumber)
//						numbersList.add(formattedNum);
//					contentvalues1.put(DatabaseConstants.RAW_CONTACT_ID, Integer.valueOf(rawId));
////					Log.d(TAG,
////							"compositeNumber : "+compositeNumber);
//					contentvalues1.put(
//							com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, formattedNum);
//					contentvalues1.put(
//							DatabaseConstants.CONTACT_NAMES_FIELD, contactName);
//					contentvalues1.put(DatabaseConstants.DATA_ID_FIELD,
//							Integer.valueOf(k2));
//					contentvalues1.put(DatabaseConstants.VOPIUM_FIELD,
//							Integer.valueOf(0));
//					contentvalues1.put(DatabaseConstants.USER_NAME_FIELD, "");
//					contentvalues1.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, phoneNumberType);
//					// contentvalues1.put(DatabaseConstants.DATA_ID,
//					// Integer.valueOf(j2));
//					contentvalues1.put(DatabaseConstants.STATE_FIELD,
//							Integer.valueOf(0));
//					 dbwrapper.insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues1);
//					totalContacts++;
////					Log.i(TAG, "commiting the batch");
//					
//					}catch(Exception e){
//						isDuplicate = true;
//						Log.d(TAG, "copyContactsAsync compositeNumber Exception :: "+compositeNumber);
//					}
//				}
//				try{
//				if(contactId != 0x80000000){
//					if(compositeNumber!=null && !compositeNumber.equals("") && compositeNumber.endsWith(","))
//						compositeNumber = compositeNumber.substring(0, compositeNumber.length()-1);
////					if( isDuplicate && (compositeNumber == null || !compositeNumber.contains(","))){
////						try{
//////							Log.d(TAG, "copyContactsAsync compositeNumber deleteRows start :: "+compositeNumber);
////						boolean deleteRows = dbwrapper.deleteSingleNumberRow(DatabaseConstants.TABLE_CONTACT_NUMBERS,contactId);
//////						Log.d(TAG, "copyContactsAsync compositeNumber deleteRows end:: "+deleteRows);
////						}catch(Exception e){}
////					}else
//					if(compositeNumber!=null && !compositeNumber.equals("")) {
//						dbwrapper.updateCompositeContacts(String.valueOf(contactId),compositeNumber);
//					}
//				}
//				}catch(Exception e){}
////				dbwrapper.setTransaction();
////				dbwrapper.endTransaction();
////				dbwrapper.beginTransaction();
//				cursor.close();
////				if (dbwrapper.isInTransaction()) {
////					dbwrapper.setTransaction();
////				}
////				dbwrapper.endTransaction();
//				
//				if(numbersList!=null && !numbersList.isEmpty()){
//					contactSyncState = CONTACT_SYNC_IDLE;
//					syncContactsWithServer(null,numbersList);
//				}
////				DBWrapper.getInstance().updateAtMeDirectStatus("91-8103002248");
////				DBWrapper.getInstance().getAllNumbers();
////				DBWrapper.getInstance().getAllEmails();
//			}catch(Exception e){
//				Log.d(TAG, "Cursor not created during copy contact for sync "+e.toString());
//			}
//				}
//		}).start();
//
//	}
	
	public static void copyContactsAsync() {
		(new Thread() {

			public void run() {
				HashSet<String> uniqueNumbers = new HashSet<String>();
				contactSyncState = CONTACT_SYNC_WAIT;
				try{
				counter = 0;
				Uri CONTENT_URI = ContactsContract.Data.CONTENT_URI;
				String _ID = ContactsContract.Contacts._ID;
				String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
				String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
				Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
				String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
				String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
				Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
				String DATA = ContactsContract.CommonDataKinds.Email.DATA;

				Log.i(TAG, "copyContactsAsync Contacts Copy Started");
				long l = System.currentTimeMillis();
				DBWrapper dbwrapper = DBWrapper.getInstance();
				dbwrapper.clearAllDB();
				int contactId = 0x80000000;
				String[] as1 = (new String[] { ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE });
				Cursor cursor = SuperChatApplication.context.getContentResolver().query(CONTENT_URI, null,
						"mimetype =?", as1, Phone_CONTACT_ID);
				if (cursor == null) {
					return;
				}
//				dbwrapper.beginTransaction();
				int totalContacts = 0;
				String compositeNumber = "";
				String contactName = "";
				boolean isDuplicate = false;
				String previousRawId = "";
				while (cursor.moveToNext()) {
					counter++;
					try{
						String displayedNumber = cursor.getString(cursor
								.getColumnIndex(ContactsContract.Data.DATA1));
					String s = formatNumber(displayedNumber);
					int tmpContactId = cursor.getInt(cursor
							.getColumnIndex(Phone_CONTACT_ID));
					int j2 = cursor.getInt(cursor.getColumnIndex(_ID));
					int k2 = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.DATA2));//cursor.getColumnIndex("data2"));
					int phoneNumberType = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE));
					String _raw_id = "";
					_raw_id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
					Log.d(TAG, "state details names only: "+s);//+version+" , "+timeStamp);
					if (contactId != tmpContactId) {
//						Log.d(TAG,
//								"compositeNumber last : "+compositeNumber);
						if(contactId != 0x80000000){
							if(totalContacts==0 && previousRawId!=null && !previousRawId.equals("")){
								try{
									dbwrapper.deleteRow(DatabaseConstants.TABLE_NAME_CONTACT_NAMES, previousRawId);
								}catch(Exception e){
									
								}
							}
							try{
//								
								if( compositeNumber!=null && !compositeNumber.equals("") && compositeNumber.endsWith(","))
									compositeNumber = compositeNumber.substring(0, compositeNumber.length()-1);
								}catch(Exception e){}
								
//								if( isDuplicate && (compositeNumber == null || !compositeNumber.contains(","))){
//									try{
////										Log.d(TAG, "copyContactsAsync compositeNumber deleteRows start :: "+compositeNumber);
//									boolean deleteRows = dbwrapper.deleteSingleNumberRow(DatabaseConstants.TABLE_CONTACT_NUMBERS,contactId);
////									Log.d(TAG, "compositeNumber deleteRows end:: "+deleteRows);
//									}catch(Exception e){}
//								} else
							
								if(compositeNumber != null && !compositeNumber.equals(""))
								{
//									if(compositeNumber.contains("9826466151"))
//										Log.d(TAG, "copyContactsAsync compositeNumber 9826466151 start :: "+isDuplicate+" , "+compositeNumber);
//									dbwrapper.updateCompositeContacts(String.valueOf(contactId),compositeNumber); // central
									
									compositeNumber = "";
								}
							}
						isDuplicate = false;
						previousRawId = _raw_id;
						contactId = tmpContactId;
						String s1 = cursor.getString(cursor
								.getColumnIndex(DISPLAY_NAME));
						contactName = s1;
//						if(contactName.contains("Neha Citrus")){
							Log.d(TAG, "state details names only: "+contactName+", "+previousRawId);//+version+" , "+timeStamp);
//						}
						String _id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
//						_raw_id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
						String version = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts.VERSION));
//						String timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
//						Log.d(TAG, "state details : "+contactId+" , "+_id+" , "+_raw_id+" , "+contactName);//+version+" , "+timeStamp);
						
						ContentValues contentvalues = new ContentValues();
						contentvalues.put(
								DatabaseConstants.NAME_CONTACT_ID_FIELD,
								Integer.valueOf(tmpContactId));
						contentvalues.put(
								DatabaseConstants.RAW_CONTACT_ID,
								Integer.valueOf(_raw_id));
						contentvalues.put(
								DatabaseConstants.CONTACT_VERSION,
								Integer.valueOf(version));
						contentvalues.put(
								DatabaseConstants.CONTACT_NAMES_FIELD, s1);
						contentvalues.put(DatabaseConstants.USER_NAME_FIELD,"");
							contentvalues.put(DatabaseConstants.VOPIUM_FIELD,
									Integer.valueOf(0));
						contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,
								Integer.valueOf(0));
//						contentvalues.put(DatabaseConstants.USER_SIP_ADDRESS,
//								"");
						
//						dbwrapper.insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NAMES,contentvalues);  // central
						
						compositeNumber = "";
						totalContacts = 0;
					}
					
					if(dbwrapper.isNumberExists(s)){
						isDuplicate = true;
						continue;
					}
					if(displayedNumber!=null && !displayedNumber.equals("") && !compositeNumber.contains(displayedNumber))
						compositeNumber+=(displayedNumber+",");
					ContentValues contentvalues1 = new ContentValues();
					contentvalues1.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,
							Integer.valueOf(tmpContactId));
					contentvalues1.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,
							s);
					contentvalues1.put(DatabaseConstants.DISPLAY_NUMBERS_FIELD,
							displayedNumber);
					contentvalues1.put(DatabaseConstants.RAW_CONTACT_ID, Integer.valueOf(_raw_id));
//					Log.d(TAG,
//							"compositeNumber : "+compositeNumber);
					contentvalues1.put(
							com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, s);
					contentvalues1.put(
							DatabaseConstants.CONTACT_NAMES_FIELD, contactName);
					contentvalues1.put(DatabaseConstants.DATA_ID_FIELD,
							Integer.valueOf(k2));
					contentvalues1.put(DatabaseConstants.VOPIUM_FIELD,
							Integer.valueOf(0));
					contentvalues1.put(DatabaseConstants.USER_NAME_FIELD, "");
					contentvalues1.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, phoneNumberType);
					// contentvalues1.put(DatabaseConstants.DATA_ID,
					// Integer.valueOf(j2));
					contentvalues1.put(DatabaseConstants.STATE_FIELD,
							Integer.valueOf(0));
					
//					 dbwrapper.insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues1); // central
					uniqueNumbers.add(s);
					 totalContacts++;
//					Log.i(TAG, "commiting the batch");
					
					}catch(Exception e){
						isDuplicate = true;
						Log.d(TAG, "copyContactsAsync compositeNumber Exception :: "+compositeNumber);
					}
				}
				try{
					if(contactId != 0x80000000){
						if(compositeNumber!=null && !compositeNumber.equals("") && compositeNumber.endsWith(","))
							compositeNumber = compositeNumber.substring(0, compositeNumber.length()-1);
//						if( isDuplicate && (compositeNumber == null || !compositeNumber.contains(","))){
//							try{
//	//							Log.d(TAG, "copyContactsAsync compositeNumber deleteRows start :: "+compositeNumber);
//							boolean deleteRows = dbwrapper.deleteSingleNumberRow(DatabaseConstants.TABLE_CONTACT_NUMBERS,contactId);
//	//						Log.d(TAG, "copyContactsAsync compositeNumber deleteRows end:: "+deleteRows);
//							}catch(Exception e){}
//						}else
						if(compositeNumber!=null && !compositeNumber.equals("") )
						{
//							dbwrapper.updateCompositeContacts(String.valueOf(contactId),compositeNumber); // central
						}
					}
				}catch(Exception e){}
//				dbwrapper.setTransaction();
//				dbwrapper.endTransaction();
//				dbwrapper.beginTransaction();
				cursor.close();
//				if (dbwrapper.isInTransaction()) {
//					dbwrapper.setTransaction();
//				}
//				dbwrapper.endTransaction();
				contactSyncState = CONTACT_SYNC_IDLE;
//				uniqueNumbers
//				List<String> numbers = DBWrapper.getInstance().getAllNumbers();
				List<String> numbers = new ArrayList<String>(uniqueNumbers);
				syncContactsWithServer(null,numbers);
//				DBWrapper.getInstance().updateAtMeDirectStatus("91-8103002248");
//				DBWrapper.getInstance().getAllNumbers();
//				DBWrapper.getInstance().getAllEmails();
			}catch(Exception e){
				Log.d(TAG, "Cursor not created during copy contact for sync "+e.toString());
			}
				}
		}).start();

	}
	private static void serverUpdateContactsInfo(final ContactsScreen obj,List<String> numbers){
		SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
		ContactUploadModel model = new ContactUploadModel(iPrefManager.getUserId(), null, numbers);
		  String JSONstring = new Gson().toJson(model);		    
		    DefaultHttpClient client1 = new DefaultHttpClient();
		   
			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
			
			 HttpPost httpPost = new HttpPost(Constants.SERVER_URL+ "/tiger/rest/user/login");
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
				            String str = "";
				            while ((line = rd.readLine()) != null) {
				            	
				            	str+=line;
				            }
				            if(str!=null &&!str.equals("")){
				            	str = str.trim();
//				            	Log.alltime(TAG, "serverUpdateContact sync response: "+str);
				            	Gson gson = new GsonBuilder().create();
								if (str==null || str.contains("error")){
									contactSyncState = CONTACT_SYNC_FAILED;
									Log.d(TAG,"serverUpdateContactsInfo onSuccess error comming : "+ str);
									return;
								}

								ContactUpDatedModel updatedModel = gson.fromJson(str,ContactUpDatedModel.class);
								if (updatedModel != null) {
									Log.d(TAG,
											"serverUpdateContactsInfo onSuccess : Contact synced successful. ");

									for (String st : updatedModel.mobileNumberUserBaseMap
											.keySet()) {
										UserDetail userDetail = updatedModel.mobileNumberUserBaseMap
												.get(st);
										//						Log.d(TAG, "contacts sync info with sip address: " + userDetail.iSipAddress);
										ContentValues contentvalues = new ContentValues();
//										contentvalues.put(
//												DatabaseConstants.USER_SIP_ADDRESS,
//												userDetail.iSipAddress);
										
//										contentvalues.put(DatabaseConstants.USER_NAME_FIELD,userDetail.userName);
//										contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(1));
//										contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,userDetail.mobileNumber);		
										
//										DBWrapper.getInstance().updateAtMeDirectStatus(contentvalues,DatabaseConstants.CONTACT_NUMBERS_FIELD);
//										DBWrapper.getInstance().updateAtMeContactDetails(contentvalues,userDetail.mobileNumber);
//										DBWrapper.getInstance().updateUserNameInContacts(userDetail.userName,userDetail.mobileNumber);
										
										contentvalues.put(DatabaseConstants.USER_NAME_FIELD,userDetail.userName);
										contentvalues.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(1));
										contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,userDetail.mobileNumber);	
										int id = userDetail.userName.hashCode();
										if (id < -1)
											id = -(id);
										contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf(id));
										contentvalues.put(DatabaseConstants.RAW_CONTACT_ID,Integer.valueOf(id));
										contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, userDetail.mobileNumber);//userDetail.name);
										contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,Integer.valueOf(0));
										
										contentvalues.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf("5"));
										contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
										contentvalues.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
										contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, userDetail.mobileNumber);

										//Save USerID and SG in DB
										contentvalues.put(DatabaseConstants.USER_ID, iPrefManager.getUserId());
										contentvalues.put(DatabaseConstants.USER_SG, iPrefManager.getUserDomain());
										
										DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues); // central
										 
										if(userDetail!=null && userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
											if(iPrefManager.getUserFileId(userDetail.userName) == null || !iPrefManager.getUserFileId(userDetail.userName).equals(userDetail.imageFileId))
												new BitmapDownloader().execute(userDetail.imageFileId);
											}
										if(iPrefManager!=null && userDetail!=null && userDetail.userName!=null){
											iPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
											iPrefManager.saveUserStatusMessage(userDetail.userName,  userDetail.currentStatus);
											if(userDetail.name!=null)
												iPrefManager.saveUserServerName(userDetail.userName,userDetail.name);
											DBWrapper.getInstance(context).getChatName(userDetail.userName);
										}
									}
									contactSyncState = CONTACT_SYNC_SUCESSED;
								}else
									contactSyncState = CONTACT_SYNC_FAILED;
				            }
				           
			            }else
			            	contactSyncState = CONTACT_SYNC_FAILED;
				} catch (ClientProtocolException e) {
					contactSyncState = CONTACT_SYNC_FAILED;
				} catch (IOException e) {
					contactSyncState = CONTACT_SYNC_FAILED;
				}catch(Exception e){
					contactSyncState = CONTACT_SYNC_FAILED;
				}
				 
			} catch (UnsupportedEncodingException e1) {
				contactSyncState = CONTACT_SYNC_FAILED;
			}catch(Exception e){
				contactSyncState = CONTACT_SYNC_FAILED;
			}
		
	}
	public static void syncContactsWithServer(final ContactsScreen obj,final List<String> numbers){
		if (contactSyncState == CONTACT_SYNC_IDLE) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					switch(contactSyncState) {
					case CONTACT_SYNC_IDLE:
					case CONTACT_SYNC_FAILED:
						if(isNetworkConnected()){
							contactSyncState = CONTACT_SYNC_START;
							serverUpdateContactsInfo(obj,numbers);
						}
						break;
					case CONTACT_SYNC_SUCESSED:
						cancel();
						break;
					}

				}
			}, 1000,4000);
		}
	}
	public static boolean isNetworkConnected(){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null&& activeNetwork.isConnected();
		if (!isConnected)
			return false;
		return true;
	}
//	private static void serverUpdateContactsInfoOld(final ContactsScreen obj,List<String> numbers){
//		final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//
////		List<String> emails =DBWrapper.getInstance().getAllEmails();
////		List<String> numbers = DBWrapper.getInstance().getAllNumbers();
//
//		ContactUploadModel model = new ContactUploadModel(
//				iPrefManager.getUserId(), null, numbers);
////		Log.d(TAG, "serverUpdateContactsInfo request:"+model.toString());
//		AsyncHttpClient client = new AsyncHttpClient();
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//		nameValuePairs.add(new BasicNameValuePair("form", new Gson()
//		.toJson(model)));
////		for (NameValuePair pair : nameValuePairs) {
////			Log.d(TAG, "serverUpdateContactsInfo login NameValuePair: " + pair.getName() + ":"
////					+ pair.getValue());
////		}
//		HttpEntity entity = null;
//		try {
//			entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		client.post(SuperChatApplication.context, Constants.SERVER_URL
//				+ "/jakarta/rest/contact/upload", entity, null,
//				new AsyncHttpResponseHandler() {
//
//			@Override
//			public void onStart() {
//
//				Log.d(TAG, "AsyncHttpClient onStart: ");
//			}
//
//			@Override
//			public void onSuccess(int arg0, String arg1) {
//				Log.d(TAG, "serverUpdateContactsInfo onSuccess: "+ arg1);
//				Gson gson = new GsonBuilder().create();
//				if (arg1==null || arg1.contains("error")){
//					contactSyncState = CONTACT_SYNC_FAILED;
//					Log.d(TAG,
//							"serverUpdateContactsInfo onSuccess error comming : "
//									+ arg1);
//					return;
//				}
//
//				ContactUpDatedModel updatedModel = gson.fromJson(arg1,
//						ContactUpDatedModel.class);
//				if (updatedModel != null) {
//					Log.d(TAG,
//							"serverUpdateContactsInfo onSuccess : Contact synced successful. ");
//
//					for (String st : updatedModel.mobileNumberUserBaseMap
//							.keySet()) {
//						UserDetail userDetail = updatedModel.mobileNumberUserBaseMap
//								.get(st);
//						//						Log.d(TAG, "contacts sync info with sip address: " + userDetail.iSipAddress);
//						ContentValues contentvalues = new ContentValues();
////						contentvalues.put(
////								DatabaseConstants.USER_SIP_ADDRESS,
////								userDetail.iSipAddress);
//						contentvalues.put(
//								DatabaseConstants.USER_NAME_FIELD,
//								userDetail.userName);
//						contentvalues.put(
//								DatabaseConstants.VOPIUM_FIELD,
//								Integer.valueOf(1));
//						contentvalues
//						.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,
//								userDetail.mobileNumber);
//						
//						DBWrapper.getInstance().updateAtMeDirectStatus(contentvalues,DatabaseConstants.CONTACT_NUMBERS_FIELD);
//						DBWrapper.getInstance().updateAtMeContactDetails(contentvalues,userDetail.mobileNumber);
//						DBWrapper.getInstance().updateUserNameInContacts(userDetail.userName,userDetail.mobileNumber);
//						if(userDetail!=null && userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
//							if(iPrefManager.getUserFileId(userDetail.userName) == null || !iPrefManager.getUserFileId(userDetail.userName).equals(userDetail.imageFileId))
//								new BitmapDownloader().execute(userDetail.imageFileId);
//							}
//						if(iPrefManager!=null && userDetail!=null && userDetail.userName!=null){
//							iPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
//							iPrefManager.saveUserStatusMessage(userDetail.userName,  userDetail.currentStatus);
//							if(userDetail.name!=null)
//								iPrefManager.saveUserServerName(userDetail.userName,userDetail.name);
//							DBWrapper.getInstance(context).getChatName(userDetail.userName);
//						}
//					}
////					for (String st : updatedModel.emailUserBaseMap
////							.keySet()) {
////						UserDetail userDetail = updatedModel.emailUserBaseMap
////								.get(st);
////						// Log.d(TAG,
////						// "contacts sync info with sip address: " +
////						// userDetail.iSipAddress);
////						ContentValues contentvalues = new ContentValues();
////						contentvalues.put(
////								DatabaseConstants.USER_SIP_ADDRESS,
////								userDetail.iSipAddress);
////						contentvalues.put(
////								DatabaseConstants.USER_NAME_FIELD,
////								userDetail.userName);
////						contentvalues.put(
////								DatabaseConstants.VOPIUM_FIELD,
////								Integer.valueOf(1));
////						contentvalues.put(
////								DatabaseConstants.PHONE_EMAILS_FIELD,
////								userDetail.email);
////						DBWrapper.getInstance().updateAtMeDirectStatus(contentvalues,DatabaseConstants.PHONE_EMAILS_FIELD);
////						DBWrapper.getInstance().updateAtMeContactDetails(contentvalues,userDetail.email);
////					}
////					if(obj!=null)
////						obj.notifyUpdate();
//					contactSyncState = CONTACT_SYNC_SUCESSED;
//				}else
//					contactSyncState = CONTACT_SYNC_FAILED;
//				Log.d(TAG,
//						"AsyncHttpClient onSuccess : Contact synced successful. ");
//				super.onSuccess(arg0, arg1);
//			}
//
//			@Override
//			public void onFailure(Throwable arg0, String arg1) {
//				contactSyncState = CONTACT_SYNC_FAILED;
//				Log.d(TAG,
//						"serverUpdateContactsInfo onFailure: Contact sync has failed"
//								+ arg1);
//
//				super.onFailure(arg0, arg1);
//			}
//		});
//	}
	public static String formatNumber(String str){
		try{
			if(str==null)
				return null;
			boolean isCountryCheckingNeeded = false;
			if(str.startsWith("00"))
			isCountryCheckingNeeded = true;
			if(str.length()>1)
				while(str.startsWith("0")){					
					if(str.length()>1)
						str = str.substring(1);
					else break;
				}
			
			
		boolean isPlus = str.contains("+")?true:false;
		if(isPlus)
			isCountryCheckingNeeded = true;
		
		str = str.replace(" ","");
		str = str.replace("+","");
		str = str.replace("-","");
		str = str.replace("(","");
		str = str.replace(")","");
		
		if(str.length()<8)
			return str;
		
		String replacingCode = null;
		boolean isNumberModified = false;
		if(isCountryCheckingNeeded){
			for(int i = 5;i>=1;i--){
				replacingCode = str.substring(0, i);
			if(countrySet.contains(replacingCode)){
				str = replacingCode+"-"+str.replaceFirst(replacingCode, "");
				isNumberModified = true;
				break;
			}}
		}
		if(!isNumberModified)
		{
			String code = Constants.countryCode.replace("+", "");
			if(str.startsWith(code))
				str = code+"-"+str.replaceFirst(code, "");
			else
				str = code+"-"+str;
		}
		
//		if(str.length()<=11){
//			str = Constants.countryCode.replace("+", "")+"-"+str;
//		}else{
//			StringBuffer buffer = new StringBuffer(str);
//			
//			buffer = buffer.insert(str.length()-10, '-');
////			if(buffer.charAt(0) == '0'){
////				while(buffer.charAt(0) == '0' && buffer.length()>0)
////					buffer.deleteCharAt(0);
////				buffer.insert(0, AppConstants.countryCode.replace("+", ""));
////			}
//			str = buffer.toString();
//		}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	public static void addBitmapToMemoryCache(String s, Bitmap bitmap)
    {
        if (s!=null && bitmap!=null && getBitmapFromMemCache(s) == null)
        {
            mMemoryCache.put(s, bitmap);
        }
    }
	 public static Bitmap getBitmapFromMemCache(String s)
	    {
	        if (mMemoryCache != null && s!=null && !s.equals(""))
	        {
	            return (Bitmap)mMemoryCache.get(s);
	        }
		 return null;
	    }
	 public static void removeBitmapFromMemCache(String s)
	    {
	        if (mMemoryCache != null && s!=null && !s.equals(""))
	        {
	            mMemoryCache.remove(s);
	        }
	    }
	 public static HttpPost addHeaderInfo(HttpPost httpPost , boolean withPassword){
		 String imei = getDeviceId();
    	 if(imei!=null)
    		 httpPost.setHeader("ucid",imei);
    	 try {
				String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
				if(version!=null && version.contains("."))
					version = version.replace(".", "_");
				if(version==null)
					version = "";
				String clientVersion = "superchat_android_"+version;
				 if(clientVersion!=null)
        		 httpPost.setHeader("cversion",clientVersion);
				 if(withPassword){
					 String auData = "'"+SharedPrefManager.getInstance().getUserId()+":"+HttpHeaderUtils.encriptPass(SharedPrefManager.getInstance().getUserPassword())+"'";
					Log.d(TAG, "auData - "+auData);
					httpPost.setHeader("audata",auData);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return httpPost;
	 }
	 public static AsyncHttpClient addHeaderInfo(AsyncHttpClient httpPost , boolean withPassword){
		 String imei = getDeviceId();
    	 if(imei!=null)
    		 httpPost.addHeader("ucid",imei);
    	 try {
				String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
				if(version!=null && version.contains("."))
					version = version.replace(".", "_");
				if(version==null)
					version = "";
				String clientVersion = "superchat_android_"+version;
				 if(clientVersion!=null)
        		 httpPost.addHeader("cversion",clientVersion);
				 if(withPassword){
					 String auData = "'"+SharedPrefManager.getInstance().getUserId()+
							 ":"+HttpHeaderUtils.encriptPass(SharedPrefManager.getInstance().getUserPassword())+"'";
					Log.d(TAG, "auData - "+auData);
					httpPost.addHeader("audata",auData);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return httpPost;
	 }
	 public static String getDeviceId()
	    {
	        String s = "000000000000000";
	        TelephonyManager telephonymanager = getTelephonyManager();
	        if (telephonymanager.getDeviceId() != null)
	        {
	            s = telephonymanager.getDeviceId();
	        }
	        return s;
	    }

	    public static String getNetworkOperator()
	    {
	        TelephonyManager telephonymanager = getTelephonyManager();
	        if (telephonymanager.getNetworkOperator() != null && telephonymanager.getNetworkOperator().length() >= 3)
	        {
	            return telephonymanager.getNetworkOperator();
	        } else
	        {
	            return "00000";
	        }
	    }
	    public static TelephonyManager getTelephonyManager()
	    {
	        return (TelephonyManager)context.getSystemService("phone");
	    }
	}