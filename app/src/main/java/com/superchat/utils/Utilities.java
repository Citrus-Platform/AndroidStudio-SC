package com.superchat.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.superchat.ui.HomeScreen;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;

public final class Utilities {

	private static final String TAG = Utilities.class.getSimpleName();
	public static String sMacAddress;
	public static String sPhoneNumber;
	private static String sIMEINumber;
	public static String sPhoneModel;
	public static String appVersion;
	public static String timeZone;
	public static String operatorName;
	public static String countryName;
	public static String countryCode;
	public static String sPhoneLanguage = "1";
	public static String sOSVersion;// = "iPod touch";
	public static String sRegId = null;
	public static String chatpath = "/rockeTalk/datastorech/";
	public static String draftPostPath = "/rockeTalk/.draftpostdata/";
	public static String sendPostPath = "/rockeTalk/.sendpostdata/";
	public static String homeTab = "homeTab";
//	String pattern = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+"; //Old Email pattern
	public static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
	public static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^\\d{1,5}[-]\\d{6,12}$");
	public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,29}$");
	public static final Pattern NAME_PATTERN = Pattern.compile("^\\s*([A-Za-z ]{3,})(\\s*([A-Za-z ]+?))?\\s*$");
	public static final Pattern PASSWORD_PATTERN = Pattern.compile("^.*(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9])(?=\\S+$).{8,}$");


	public static final int bytesToInt(byte[] b, int offset, int byteLen) {
		int x, y = 0;
		int mask = (byteLen - 1) * 8;
		for (int i = offset; i < offset + byteLen; i++) {
			x = signedToUnsigned((int) b[i]);
			y |= ((x & 0x000000ff) << mask);
			mask -= 8;
		}
		return y;
	}

	private static final int signedToUnsigned(int a) {
		if (a < 0)
			return a + 256;
		return a;
	}

	public static final int getColor(int r, int g, int b) {
		return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
	}

	// public static String convertTime(String aTimeToConvert) {
	// StringBuffer convertedTime = new StringBuffer();
	// try {
	// Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	// long currGMT = cal.getTime().getTime();
	//
	// int temp = Integer.parseInt(aTimeToConvert.substring(0, 4));
	// cal.set(Calendar.YEAR, temp);
	// temp = Integer.parseInt(aTimeToConvert.substring(5, 7));
	// cal.set(Calendar.MONTH, temp - 1);
	// temp = Integer.parseInt(aTimeToConvert.substring(8, 10));
	// cal.set(Calendar.DAY_OF_MONTH, temp);
	// temp = Integer.parseInt(aTimeToConvert.substring(11, 13));
	// cal.set(Calendar.HOUR_OF_DAY, temp);
	// temp = Integer.parseInt(aTimeToConvert.substring(14, 16));
	// cal.set(Calendar.MINUTE, temp);
	// temp = Integer.parseInt(aTimeToConvert.substring(17, 19));
	// cal.set(Calendar.SECOND, temp);
	// long recGMT = cal.getTime().getTime();
	// long offset = currGMT - recGMT;
	// cal = Calendar.getInstance();
	// long currLocal = System.currentTimeMillis() - offset;
	// cal.setTime(new Date(currLocal));
	// if (cal.get(Calendar.DAY_OF_MONTH) < 10)
	// convertedTime.append('0');
	// convertedTime.append(cal.get(Calendar.DAY_OF_MONTH));
	// convertedTime.append('/');
	// if ((cal.get(Calendar.MONTH) + 1) < 10)
	// convertedTime.append('0');
	// convertedTime.append(cal.get(Calendar.MONTH) + 1);
	// convertedTime.append('/');
	// convertedTime.append(cal.get(Calendar.YEAR));
	// convertedTime.append('\n');
	// if (cal.get(Calendar.HOUR) >= 0 && cal.get(Calendar.HOUR) < 10)
	// convertedTime.append('0');
	// if (cal.get(Calendar.AM_PM) == 1) {
	// convertedTime.append(12 + cal.get(Calendar.HOUR));
	// } else
	// convertedTime.append(cal.get(Calendar.HOUR));
	// convertedTime.append(':');
	// if (cal.get(Calendar.MINUTE) >= 0 && cal.get(Calendar.MINUTE) < 10)
	// convertedTime.append('0');
	// convertedTime.append(cal.get(Calendar.MINUTE));
	// convertedTime.append(':');
	// if (cal.get(Calendar.SECOND) < 10)
	// convertedTime.append('0');
	// convertedTime.append(cal.get(Calendar.SECOND));
	// if (cal.get(Calendar.AM_PM) == 1)
	// convertedTime.append("PM");
	// else
	// convertedTime.append("AM");
	// } catch (Exception ex) {
	// convertedTime.delete(0, convertedTime.length());
	// convertedTime.append(aTimeToConvert);
	// }
	// return convertedTime.toString();
	// }

	public static String convertTime(String aTimeToConvert) {
		SimpleDateFormat serverFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		serverFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date serverDate = null;
		try {
			serverDate = serverFormat.parse(aTimeToConvert);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		SimpleDateFormat localFormat = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss");
		localFormat.setTimeZone(TimeZone.getDefault());
		if (serverDate != null) {
			return localFormat.format(serverDate);
		}
		return aTimeToConvert;
	}

	private static String getWifiMacAddress(Context context) {
		if (null != sMacAddress)
			return sMacAddress;
		try {
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			if (null != wifiManager) {
				WifiInfo info = wifiManager.getConnectionInfo();
				if (null != info) {
					sMacAddress = info.getMacAddress();
					if (null != sMacAddress) {
						sMacAddress = sMacAddress.toUpperCase();
					} else {
						sMacAddress = "";
					}
				}
			}
		} catch (Exception _ex) {
		}
		return sMacAddress;
	}

	public static boolean isNetworkAvailable(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netinfo = cm.getActiveNetworkInfo();
			if (null != netinfo) {
				State state = netinfo.getState();
				if (State.CONNECTED == state
						|| State.CONNECTING == state
						|| State.SUSPENDED == state) {
					return true;
				}
			}
		} catch (Exception _ex) {
		}
		return false;
	}

	/**
	 * This method gets the phone IMEI number.
	 * 
	 * @param context
	 *            Context to access the {@link TelephonyManager} manager.
	 * @return Phone IMEI number if found other wise "".
	 */
	public static String getPhoneIMEINumber(Context context) {
		if (null != sIMEINumber)
			return sIMEINumber;
		try {
			TelephonyManager telphonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			sIMEINumber = telphonyManager.getDeviceId();
			// System.out.println("-------------sIMEINumber---------"+sIMEINumber);
			// if(sIMEINumber == null || sIMEINumber.equalsIgnoreCase("null")){
			// return sIMEINumber = telphonyManager.getDeviceId();
			// }
		} catch (Exception _ex) {
		}
		return sIMEINumber;
	}

	public static String getPhoneIMEINumberMethod(Context context) {
		if (null != sIMEINumber)
			return sIMEINumber;
		try {
			TelephonyManager telphonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			sIMEINumber = telphonyManager.getDeviceId();
			// System.out.println("-------------sIMEINumber---------"+sIMEINumber);
			// if(sIMEINumber == null || sIMEINumber.equalsIgnoreCase("null")){
			// return sIMEINumber = telphonyManager.getDeviceId();
			// }

			if (sIMEINumber == null) {
				sIMEINumber = "123456789";
			}
		} catch (Exception _ex) {
		}
		return sIMEINumber;
	}

	// private static String getBluetoothId(){
	// if (null != sIMEINumber)
	// return sIMEINumber;
	// BluetoothAdapter btAdapt= null;
	// String address=null;
	// btAdapt = BluetoothAdapter.getDefaultAdapter();
	// if(btAdapt!=null){
	// address= btAdapt.getAddress();
	// }else{
	// address=null;
	// }
	// return address;
	// }

	private static String getWifiMacId(Context context) {
		if (null != sIMEINumber)
			return sIMEINumber;
		String address = null;
		WifiManager wimanager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wimanager != null)
			address = wimanager.getConnectionInfo().getMacAddress();
		else
			address = null;

		return address;
	}

	private static String generateUniqueCode() {
		if (null != sIMEINumber)
			return sIMEINumber;

		String code = "1234567890";

		try {
			code = "" + Math.abs((new Random().nextLong()));

			return code;
		} catch (NumberFormatException nEx) {
			return code;
		}
	}

	public static String getPhoneIMEINumber() {
		// if (sRegId != null)
		// return sIMEINumber + ";" + sRegId;//+";"+token ;
		return sIMEINumber;// +";"+token ;
	}

	public static String getPhoneIMEINumberWithPushReg() {
		// System.out.println("sRegId ::::::::::::::::::::::::::: "+sRegId);
		// System.out.println("sIMEINumber ::::::::::::::::::::::::::: "+sIMEINumber);
		if (sRegId != null)
			return sIMEINumber + ";" + sRegId;// +";"+token ;
		return sIMEINumber;// +";"+token ;
	}

	public static String getGoogleToken() {
			return sRegId;
	}

	public static byte[] loadResToByteArray(int resId, Context ctx) {
		byte[] s = null;
		try {
			InputStream is = ctx.getResources().openRawResource(resId);
			s = new byte[is.available()];
			is.read(s);
			is.close();
		} catch (Exception e) {
		}
		return s;
	}

	public static InputStream LoadInputStreamFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();

			return is;
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("Exc=" + e);
			return null;
		}
	}

	public static byte[] inputStreamToByteArray(InputStream inputStream) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[100];

		try {
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		try {
			buffer.flush();
		} catch (IOException e) {

			e.printStackTrace();
		}

		return buffer.toByteArray();
	}

	public static String replaceAll(String target, String from, String to) {

		int start = target.indexOf(from);
		if (start == -1)
			return target;
		int lf = from.length();
		char[] targetChars = target.toCharArray();
		StringBuffer buffer = new StringBuffer();
		int copyFrom = 0;
		while (start != -1) {
			buffer.append(targetChars, copyFrom, start - copyFrom);
			buffer.append(to);
			copyFrom = start + lf;
			start = target.indexOf(from, copyFrom);
		}
		buffer.append(targetChars, copyFrom, targetChars.length - copyFrom);
		return buffer.toString();
	}

	public static void closeSoftKeyBoard(View view, Activity context) {
		try {
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private static final int MAX_DATA_CHUNK = 1024 * 100;

	public static String saveDataIntoFile(byte iRequestData[], String fileExten) {
		String absPath = "";

		int iTotalBytesDone = 0;
		try {
			int dataLength = iRequestData.length;
			File file = new File(HomeScreen.cacheDir, "/"
					+ System.currentTimeMillis() + "_rtin" + fileExten);
			absPath = file.getAbsolutePath();
			if (!file.exists()) {
				file.createNewFile();
				if (file.canWrite()) {
					FileOutputStream lOutputStream = new FileOutputStream(file);
					while (dataLength > 0) {
						if (dataLength >= MAX_DATA_CHUNK) {
							if (null != lOutputStream) {
								lOutputStream.write(iRequestData,
										iTotalBytesDone, MAX_DATA_CHUNK);
							}
							iTotalBytesDone += MAX_DATA_CHUNK;
							dataLength -= MAX_DATA_CHUNK;
						} else {
							if (null != lOutputStream) {
								lOutputStream.write(iRequestData,
										iTotalBytesDone, dataLength);
							}
							iTotalBytesDone += dataLength;
							dataLength = 0;
						}
					}
					lOutputStream.flush();
					lOutputStream.close();
					// out.close();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return absPath;
	}

	public static String getCurrentdate() {
		Calendar cal = Calendar.getInstance();
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sdf.format(d);
		// return convertlongToDate(cal.getTime().getTime());
	}

	public static String convertlongToDate(long timestamp) {
		StringBuilder time = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
			time.append('0');
		time.append(calendar.get(Calendar.DAY_OF_MONTH));
		time.append('/');
		if ((calendar.get(Calendar.MONTH) + 1) < 10)
			time.append('0');
		time.append(calendar.get(Calendar.MONTH) + 1);
		time.append('/');
		time.append(calendar.get(Calendar.YEAR));
		time.append('\n');
		if (calendar.get(Calendar.HOUR) >= 0
				&& calendar.get(Calendar.HOUR) < 10)
			time.append('0');
		if (calendar.get(Calendar.AM_PM) == 1) {
			time.append(12 + calendar.get(Calendar.HOUR));
		} else
			time.append(calendar.get(Calendar.HOUR));
		time.append(':');
		if (calendar.get(Calendar.MINUTE) < 10)
			time.append('0');
		time.append(calendar.get(Calendar.MINUTE));
		time.append(':');
		if (calendar.get(Calendar.SECOND) < 10)
			time.append('0');
		time.append(calendar.get(Calendar.SECOND));
		if (calendar.get(Calendar.AM_PM) == 1)
			time.append("PM");
		else
			time.append("AM");

		return time.toString();
	}


	public static long gettimemillis(String datetime)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long convertedtime = TimeZone.getDefault().getRawOffset() + 18000000l;
		if(datetime.indexOf('/') != -1)
		{
			sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");//24/01/2015 00:38:52//2015-01-30T01:54:42-05:00
			convertedtime = 0;
		}
		Date date = null;
		//2015-01-06T17:45:15-05:00, 2015-01-23 18:37:30
		if(datetime == null)
			return convertedtime;
		if(datetime.length() > 19)
		{
		if(datetime.lastIndexOf('-') != -1)
			datetime = datetime.substring(0, datetime.lastIndexOf('-'));
		else if(datetime.lastIndexOf('+') != -1)
			datetime = datetime.substring(0, datetime.lastIndexOf('+'));
		if(datetime.indexOf('T') != -1)
			datetime = datetime.replace('T', ' ');
		}
		
		try {
			date = sdf.parse(datetime);
			System.out.println(date.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(date != null)
			convertedtime += date.getTime();
		return convertedtime;
	}



	public static long CompareTime(long value) {

		Long xx = (System.currentTimeMillis() - value) / 1000;
		/*
		 * java.util.Date past = new Date(value);//sdf.parse(date);
		 * java.util.Date now = new java.util.Date(); //
		 * System.out.println(past.toString()+"-----------"+now.toString()); //
		 * String s = sdf.format(now); // now = sdf.parse(s); long agosecond =
		 * Math.abs(TimeUnit.MILLISECONDS.toSeconds(now .getTime() -
		 * past.getTime()));
		 * 
		 * int seconds = (int) (agosecond % 60);
		 */
		// System.out.println("xx===="+xx);
		return xx;
	}


	public static boolean canWeWriteInSDCard() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;

		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		if (mExternalStorageAvailable && mExternalStorageWriteable) {

			return true;
		} else {
			return false;
		}
	}


	public static String[] getChatList() {

		// Vector<String> chatter = new Vector<String>();
		if (canWeWriteInSDCard()) {
			try {

				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
				boolean exists = (new File(path)).exists();
				if (exists) {
					File file = new File(path);//
					if (file.isDirectory()) {
						return file.list();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	public static String[] getFileList(String path) {

		// Vector<String> chatter = new Vector<String>();
		if (canWeWriteInSDCard()) {
			try {

				// path = Environment.getExternalStorageDirectory()
				// .getAbsolutePath() + chatpath;// "/RockeTalk/Chat/";
				boolean exists = (new File(path)).exists();
				if (exists) {
					File file = new File(path);//
					if (file.isDirectory()) {
						return file.list();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	public static String[] getAllfile(String path) {

		// Vector<String> chatter = new Vector<String>();
		if (canWeWriteInSDCard()) {
			try {

				// String path =
				// Environment.getExternalStorageDirectory().getAbsolutePath() +
				// "/RockeTalk/Chat/";

				boolean exists = (new File(path)).exists();
				if (exists) {
					File file = new File(path);//
					if (file.isDirectory()) {
						return file.list();
					}

				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return null;
	}

	public static boolean moveFileToSdCard(String sourceFilepath) {

		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel in = null;
		FileChannel out = null;

		try {
			// File root = Environment.getExternalStorageDirectory();
			// File file = new
			// File(root.getAbsolutePath()+"/DCIM/Camera/img.jpg");

			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/"
					+ System.currentTimeMillis()
					+ "_RT.jpg";// "/RockeTalk/Chat/";
			File backupFile = new File(path);
			backupFile.createNewFile();
			File sourceFile = new File(sourceFilepath);
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(backupFile);
			byte fData[] = new byte[fis.available()];
			fis.read(fData);
			fos.write(fData);
			// in = fis.getChannel();
			// out = fos.getChannel();
			//
			// long size = in.size();
			// in.transferTo(0, size, out);
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Throwable ignore) {
			}

			try {
				if (fos != null)
					fos.close();
			} catch (Throwable ignore) {
			}

			try {
				if (in != null && in.isOpen())
					in.close();
			} catch (Throwable ignore) {
			}

			try {
				if (out != null && out.isOpen())
					out.close();
			} catch (Throwable ignore) {

			}
		}

	}

	public static InputStream getFileInputStream(String path) {
		try {

			InputStream finput = new FileInputStream(path);
			return finput;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * public static int getFileInputStream(String path) { int size = 0 ; try {
	 * InputStream finput = new FileInputStream(path); size= finput.available()
	 * ; finput.close(); } catch (FileNotFoundException e) {
	 * e.printStackTrace(); return null ; } return size ; }
	 */
	public static String[] getShort(String fname[]) {
		if (fname != null)
			for (int x = 0; x < fname.length; x++) {
				for (int y = 0; y < fname.length - 1; y++) {
					if (Long.parseLong(fname[y]) > Long.parseLong(fname[y + 1])) {
						String temp = fname[y + 1];
						fname[y + 1] = fname[y];
						fname[y] = temp;

					}
				}
			}
		return fname;
	}


	private static final String TAG_DECLINE = "decline";
	private static final String TAG_COMMONFRIEND = "commonfriends";
	private static final String TAG_MEDIAPOST = "mediaposts";
	private static final String TAG_IGNORE = "ignore";
	private static final String TAG_REPORT = "report";
	private static final String TAG_COMMONCOMMUNITIES = "commoncommunities";
	private static final String TAG_ACCEPT = "accept";
	private static final String TAG_LOCTION = "location";
	private static final String TAG_BIRTHDAY = "birthday";

	// ArrayList<HashMap<String, String>> contactList = new
	// ArrayList<HashMap<String, String>>();
	public static HashMap<String, String> mapData = new HashMap<String, String>();

	public static void jsonParserEngine(String parseString) {

		try {

			JSONObject myjson = new JSONObject(parseString);
			// HashMap<String, String> mapData=new HashMap<String, String>();
			// JSONArray nameArray = myjson.names();
			// JSONArray valArray = myjson.toJSONArray(nameArray);

			// for(int i=0;i<valArray.length();i++)
			// {

			// String p = nameArray.getString(i) + "," + valArray.getString(i);
			String decline = myjson.getString(TAG_DECLINE);
			String commonfriend = myjson.getString(TAG_COMMONFRIEND);
			String mediapost = myjson.getString(TAG_MEDIAPOST);
			String ignore = myjson.getString(TAG_IGNORE);
			String report = myjson.getString(TAG_REPORT);
			String communities = myjson.getString(TAG_COMMONCOMMUNITIES);
			String accept = myjson.getString(TAG_ACCEPT);
			String loctaion = myjson.getString(TAG_LOCTION);
			String birthday = myjson.getString(TAG_BIRTHDAY);

			mapData.put(TAG_DECLINE, decline);
			mapData.put(TAG_COMMONFRIEND, commonfriend);
			mapData.put(TAG_MEDIAPOST, mediapost);
			mapData.put(TAG_IGNORE, ignore);
			mapData.put(TAG_REPORT, report);
			mapData.put(TAG_COMMONCOMMUNITIES, communities);
			mapData.put(TAG_ACCEPT, accept);
			mapData.put(TAG_LOCTION, loctaion);
			mapData.put(TAG_BIRTHDAY, birthday);
			// contactList.add(mapData);
			// }

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static String decline() {
		return mapData.get(TAG_DECLINE);
	}

	public static String commonFriend() {
		return mapData.get(TAG_COMMONFRIEND);
	}

	public static String mediaPost() {
		return mapData.get(TAG_MEDIAPOST);
	}

	public static String ignore() {
		return mapData.get(TAG_IGNORE);

	}

	public static String report() {
		return mapData.get(TAG_REPORT);
	}

	public static String communities() {
		return mapData.get(TAG_COMMONCOMMUNITIES);
	}

	public static String accept() {
		return mapData.get(TAG_ACCEPT);
	}

	public static String location() {
		return mapData.get(TAG_LOCTION);
	}

	public static String birthday() {
		return mapData.get(TAG_BIRTHDAY);
	}


	public static Object deserializeObject(byte[] b) {
		try {
			ObjectInputStream in = new ObjectInputStream(
					new ByteArrayInputStream(b));
			Object object = in.readObject();
			in.close();

			return object;
		} catch (ClassNotFoundException cnfe) {
			Log.e("deserializeObject", "class not found error", cnfe);

			return null;
		} catch (IOException ioe) {
			Log.e("deserializeObject", "io error", ioe);
			ioe.printStackTrace();
			return null;
		}
	}




	public static byte[] serializeObject(Object o) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			ObjectOutput out = new ObjectOutputStream(bos);
			out.writeObject(o);
			out.close();

			byte[] buf = bos.toByteArray();
			bos.close();
			return buf;
		} catch (IOException ioe) {
			Log.e("serializeObject", "error", ioe);
			ioe.printStackTrace();
			return null;
		}
	}

	public void animation() {

	}

	public static void startAnimition(Context context, View view, int animition) {
		try {
			Animation animation = AnimationUtils.loadAnimation(context,
					animition);
			// animation.setAnimationListener(listener)
			view.startAnimation(animation);
		} catch (Exception e) {
			// TODO: handle exception
		}
		//
	}

	public static float getDip(int dip, Context context) {
		Resources r = context.getResources();
		float pix = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				r.getDisplayMetrics());
		return pix;
	}

	public static float convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return px;
	}

	/**
	 * This method converts device specific pixels to device independent pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A float value to represent db equivalent to px value
	 */
	public static float convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / (metrics.densityDpi / 160f);
		return dp;

	}




	public static String[][] imageIDs2 = {
//		{ ":01", "" + R.drawable.emo_1 },
//			{ ":02", "" + R.drawable.emo_2, },
//			{ ":03", "" + R.drawable.emo_3, },
//			{ ":04", "" + R.drawable.emo_4, },
//			{ ":05", "" + R.drawable.emo_5, },
//			{ ":06", "" + R.drawable.emo_6, },
//			{ ":07", "" + R.drawable.emo_7, },
//			{ ":08", "" + R.drawable.emo_8, },
//			{ ":09", "" + R.drawable.emo_9, },
//			{ ":010", "" + R.drawable.emo_10, },
//			{ ":011", "" + R.drawable.emo_11, },
//			{ ":012", "" + R.drawable.emo_12, },
//			{ ":013", "" + R.drawable.emo_13, },
//			{ ":014", "" + R.drawable.emo_14, }

	};
	public static String[][] imageIDs3 = { 
//		{ ":rt01", "" + R.drawable.emo1 },
//			{ ":rt02", "" + R.drawable.emo2, },
//			{ ":rt03", "" + R.drawable.emo3, },
//			{ ":rt04", "" + R.drawable.emo4, },
//			{ ":rt05", "" + R.drawable.emo5, },
//			{ ":rt06", "" + R.drawable.emo6, },
//			{ ":rt07", "" + R.drawable.emo7, },
//			{ ":rt08", "" + R.drawable.emo8, },
//			{ ":rt09", "" + R.drawable.emo9, },
//			{ ":rt010", "" + R.drawable.emo10, },
//			{ ":rt011", "" + R.drawable.emo11, },
//			{ ":rt012", "" + R.drawable.emo12, },
//			{ ":rt013", "" + R.drawable.emo13, },
//			{ ":rt014", "" + R.drawable.emo14, }

	};

	public static String[][] imageIDs1 = {};

	public static final HashMap<String, Integer> mEmoticons = new HashMap<String, Integer>();
	static {}


	public static void trimCache(Context context) {
		try {
			File dir = context.getCacheDir();
			if (dir != null && dir.isDirectory()) {
				deleteDir(dir);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		if (dir.getAbsolutePath().indexOf(".th") != -1)
			return false;
		// System.out.println("-----------delete file---"+dir.getAbsolutePath());
		return dir.delete();
	}

	public static byte[] readBytes(InputStream inputStream) throws IOException {
		// this dynamically extends to take the bytes you read
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		// this is storage overwritten on each iteration with bytes
		int bufferSize = 1024;
		byte[] buffer = new byte[bufferSize];

		// we need to know how may bytes were read to write them to the
		// byteBuffer
		int len = 0;
		// System.out.println("-----byteBuffer is availabel : "+inputStream.available());
		if(inputStream != null)
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
			// System.out.println("-----byteBuffer size : "+byteBuffer.size());
		}
		// inputStream.close() ;
		// and then we can return your byte array.
		return byteBuffer.toByteArray();
	}

	public static byte[] readBytes(InputStream inputStream, int till)
			throws IOException {
		// this dynamically extends to take the bytes you read
		ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

		// this is storage overwritten on each iteration with bytes
		int bufferSize = till;
		byte[] buffer = new byte[bufferSize];

		// we need to know how may bytes were read to write them to the
		// byteBuffer
		int len = 0;
		while ((len = inputStream.read(buffer)) != -1) {
			byteBuffer.write(buffer, 0, len);
			break;
		}
		inputStream.close();
		// and then we can return your byte array.
		return byteBuffer.toByteArray();
	}

	public static String getPicPath() {
		// File file = new File(Environment.getExternalStorageDirectory(),
		// getRandomNumber() + ".jpg");

		File path = new File(Environment.getExternalStorageDirectory()
				+ "/DCIM");
		if (path.exists()) {
			File test1 = new File(path, "Camera/");
			if (test1.exists()) {
				path = test1;
			} else {
				File test2 = new File(path, "100ANDRO/");
				if (test2.exists()) {
					path = test2;
				} else {
					File test3 = new File(path, "100MEDIA/");
					if (!test3.exists()) {
						test3.mkdirs();
					}
					path = test3;
				}
			}
		} else {
			path = new File(path, "Camera/");
			path.mkdirs();
		}
		// System.out.println("------camera path----------"+path.getAbsolutePath());
		return path.getAbsolutePath();
	}

	public static boolean renameFile(String oriFile, String destFile) {
		File sdcard = Environment.getExternalStorageDirectory();
		File from = new File(oriFile);
		File to = new File(destFile);

		return from.renameTo(to);
	}

	public static void obs(FileObserver fo, String path) {
		// System.out.println("-----FileObserver "+ path);
		fo = new FileObserver(path.toString()) {

			@Override
			public void onEvent(int event, String path) {
				// System.out.println("-----event "+ event);
				/*
				 * Log.d("operator", "out side if" + Phototaken +
				 * externalStorageState .equals(Environment.MEDIA_MOUNTED)); if
				 * (Phototaken == 0 && event == 8){ String st = timeStamp();
				 * Log.d("operator", "in event " + Phototaken);
				 * Log.d("operator", "lat: " + MainService.lat + " " + "lng: " +
				 * MainService.lng + " " + "location: " + MainService.addre +
				 * " " + "time: " + st); ptd.insert(st,
				 * String.valueOf(MainService.lat),
				 * String.valueOf(MainService.lng), MainService.addre); }
				 */

			}
		};
		fo.startWatching();
	}

	public static String getVideoLastVideoFile(Activity ctx) {
		final String[] columns = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_TAKEN };
		final String orderBy = MediaStore.Video.Media._ID;
		Cursor imagecursor = ctx.managedQuery(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int count = imagecursor.getCount();
		// System.out.println("-------------count------------"+count);
		long cTime = System.currentTimeMillis();
		long DATE_TAKEN = 0;
		String s = "";
		for (int i = 0; i < count; i++) {
			imagecursor.moveToPosition(i);
			// long dataColumnIndex =
			// imagecursor.getLong(MediaStore.Images.Media.DATE_TAKEN);
			// long dataColumnIndex =
			// imagecursor.getLong(imagecursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN));
			// if(dataColumnIndex > DATE_TAKEN)
			// DATE_TAKEN = dataColumnIndex ;
			// if((cTime - dataColumnIndex)>5000)
			{
				// System.out.println("--------dataColumnIndex-----------"+dataColumnIndex);
				int ind = imagecursor
						.getColumnIndex(MediaStore.Video.Media.DATA);
				s = imagecursor.getString(ind);
				// System.out.println("---------------path-----------"+s);
			}
		}
		return s;
	}

	public static Hashtable<String, String> getVideoPath(Activity ctx) {
		final String[] columns = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_TAKEN };
		final String orderBy = MediaStore.Video.Media._ID;
		Cursor imagecursor = ctx.managedQuery(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int count = imagecursor.getCount();
		int image_column_index = imagecursor
				.getColumnIndex(MediaStore.Images.Media._ID);
		Hashtable<String, String> sr = new Hashtable<String, String>();
		for (int i = 0; i < count; i++) {
			imagecursor.moveToPosition(i);
			int id = imagecursor.getInt(image_column_index);
			int dataColumnIndex = imagecursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			sr.put(id + "", imagecursor.getString(dataColumnIndex));
		}
		return sr;
	}

	public static Hashtable<String, String> getImagePath(Activity ctx) {
		final String[] columns = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media._ID, MediaStore.Video.Media.DATE_TAKEN };
		final String orderBy = MediaStore.Video.Media._ID;
		Cursor imagecursor = ctx.managedQuery(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, columns, null,
				null, orderBy);
		int count = imagecursor.getCount();
		int image_column_index = imagecursor
				.getColumnIndex(MediaStore.Images.Media._ID);
		Hashtable<String, String> sr = new Hashtable<String, String>();
		for (int i = 0; i < count; i++) {
			imagecursor.moveToPosition(i);
			int id = imagecursor.getInt(image_column_index);
			int dataColumnIndex = imagecursor
					.getColumnIndex(MediaStore.Images.Media.DATA);
			sr.put(id + "", imagecursor.getString(dataColumnIndex));
		}
		return sr;
	}





	public static String replace(String _text, String _searchStr,
			String _replacementStr) {
		StringBuffer sb = new StringBuffer();
		int searchStringPos = _text.indexOf(_searchStr);
		int startPos = 0;
		int searchStringLength = _searchStr.length();
		while (searchStringPos != -1) {
			sb.append(_text.substring(startPos, searchStringPos)).append(
					_replacementStr);
			startPos = searchStringPos + searchStringLength;
			searchStringPos = _text.indexOf(_searchStr, startPos);
		}
		sb.append(_text.substring(startPos, _text.length()));
		return sb.toString();
	}

	public static void setRateTime(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong("ratetime", System.currentTimeMillis());
		editor.commit();
	}

	public static long getRateTime(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getLong("ratetime", 0);
	}

	public static void setLoginCount(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		int loginCount = prefs.getInt("loginCount", 0);
		editor.putInt("loginCount", ++loginCount);
		editor.commit();
	}

	public static int getLoginCount(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getInt("loginCount", 0);
	}

	public static void setSafeLogout(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("safelogin", false);
		editor.putBoolean("safelogout", true);
		editor.commit();
	}

	public static boolean isSafeLogout(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getBoolean("safelogout", false);
	}

	public static void setSafeLogin(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("safelogin", true);
		editor.putBoolean("safelogout", false);
		editor.commit();
	}

	public static boolean isSafeLoin(Context mContext) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getBoolean("safelogin", false);
	}

	public static String getConterType(byte[] data) {
		// System.out.println("-------------data.length----" + data.length);
		byte type[] = new byte[100];
		String contentType = null;
		System.arraycopy(data, 0, type, 0, 100);
		String cT = new String(type);
		cT = cT.toLowerCase();
		if (cT.indexOf("png") != -1)
			contentType = "image/png";
		else if (cT.indexOf("3gp") != -1)
			contentType = "video/3gp";
		else if (cT.indexOf("mp4") != -1)
			contentType = "video/mp4";
		else if (cT.indexOf("JFIF") != -1)
			contentType = "image/jpeg ";
		// System.out.println("-------------content type----" + contentType);
		return null;
	}

	public static String converMiliSecond(long timeMillis) {
		long time = timeMillis / 1000;
		String seconds = Integer.toString((int) (time % 60));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		String hours = Integer.toString((int) (time / 3600));
		// System.out.println("time====="+time);
		// System.out.println("seconds====="+seconds);
		// System.out.println("minutes====="+minutes);
		// System.out.println("hours====="+hours);

		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2) {
				seconds = "0" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}
		return minutes + ":" + seconds;
	}

	public static String converMiliSecondForAudioStatus(long timeMillis) {
		long time = timeMillis / 1000;//88000
		String seconds = (Integer.toString(60 - (int) (time % 60)));
		String minutes = Integer.toString((int) ((time % 3600) / 60));
		String hours = Integer.toString((int) (time / 3600));
		// System.out.println("time====="+time);
		// System.out.println("seconds====="+seconds);
		// System.out.println("minutes====="+minutes);
		// System.out.println("hours====="+hours);

		for (int i = 0; i < 2; i++) {
			if (seconds.length() < 2) {
				seconds = "" + seconds;
			}
			if (minutes.length() < 2) {
				minutes = "0" + minutes;
			}
			if (hours.length() < 2) {
				hours = "0" + hours;
			}
		}
		return seconds;
	}

	public static byte[] getFileData(String filePath) {
		try {
			System.out.println("getFileData:filePath : "+filePath);
			FileInputStream fin = new FileInputStream(filePath);
			byte[] data = Utilities.readBytes(fin);// new byte[fin.available()];
			fin.read(data, 0, data.length);
			return data;
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		return null;
	}

	public static String[] split(StringBuffer sb, String splitter) {
		String[] strs = new String[sb.length()];
		int splitterLength = splitter.length();
		int initialIndex = 0;
		int indexOfSplitter = indexOf(sb, splitter, initialIndex);
		int count = 0;
		if (-1 == indexOfSplitter)
			return new String[] { sb.toString() };
		while (-1 != indexOfSplitter) {
			char[] chars = new char[indexOfSplitter - initialIndex];
			sb.getChars(initialIndex, indexOfSplitter, chars, 0);
			initialIndex = indexOfSplitter + splitterLength;
			indexOfSplitter = indexOf(sb, splitter, indexOfSplitter + 1);
			strs[count] = new String(chars);
			count++;
		}
		// get the remaining chars.
		if (initialIndex + splitterLength <= sb.length()) {
			char[] chars = new char[sb.length() - initialIndex];
			sb.getChars(initialIndex, sb.length(), chars, 0);
			strs[count] = new String(chars);
			count++;
		}
		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = strs[i];
		}
		return result;
	}

	public static int indexOf(StringBuffer sb, String str, int start) {
		int index = -1;
		if ((start >= sb.length() || start < -1) || str.length() <= 0)
			return index;
		char[] tofind = str.toCharArray();
		outer: for (; start < sb.length(); start++) {
			char c = sb.charAt(start);
			if (c == tofind[0]) {
				if (1 == tofind.length)
					return start;
				inner: for (int i = 1; i < tofind.length; i++) { // start on the
																	// 2nd
																	// character
					char find = tofind[i];
					int currentSourceIndex = start + i;
					if (currentSourceIndex < sb.length()) {
						char source = sb.charAt(start + i);
						if (find == source) {
							if (i == tofind.length - 1) {
								return start;
							}
							continue inner;
						} else {
							start++;
							continue outer;
						}
					} else {
						return -1;
					}

				}
			}
		}
		return index;
	}

	public static void setInt(Context mContext, String key, int value) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getInt(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getInt(key, 0);
	}

	public static void setBoolean(Context mContext, String key, boolean value) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBoolean(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getBoolean(key, false);
	}

	public static void setString(Context mContext, String key, String value) {
		// System.out.println("----------------- key : " + key);
		// System.out.println("----------------- value : " + value);
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getString(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getString(key, null);
	}

	public static void setLong(Context mContext, String key, long value) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static long getLong(Context mContext, String key) {
		SharedPreferences prefs = mContext.getSharedPreferences("setting", 0);
		SharedPreferences.Editor editor = prefs.edit();
		return prefs.getLong(key, 0);
	}

	public static boolean isSupportThisVideo(String path) {
		String extension = MimeTypeMap.getFileExtensionFromUrl(path);
		if (extension == null || extension.trim().length() <= 0) {
			try {
				extension = path.substring(path.lastIndexOf(".") + 1,
						path.length());
			} catch (Exception e) {
				extension = "3gp";
			}
		}
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				extension);
		if (mimeType == null)
			return false;
		else
			return true;

	}
	/**
	 * Return the size of a directory in bytes
	 */
	private static long dirSize(File dir) {

		if (dir.exists()) {
			long result = 0;
			File[] fileList = dir.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				// Recursive call if it's a directory
				if (fileList[i].isDirectory()) {
					result += dirSize(fileList[i]);
				} else {
					// Sum the file size in bytes
					result += fileList[i].length();
				}
			}
			return result; // return the file size
		}
		return 0;
	}

	public static String makeLink(String tempString) {

		return tempString;
	}
	public static String readStream(InputStream in) {
		BufferedReader reader = null;
		String res = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				res += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
	}

	public static boolean isComposeServiceRunning(Context activity) {
		ActivityManager manager = (ActivityManager) activity
				.getSystemService(activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.kainat.app.android.engine.ComposeService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * These constants aren't yet available in my API level (7), but I need to
	 * handle these cases if they come up, on newer versions
	 */
	public static final int NETWORK_TYPE_EHRPD = 14; // Level 11
	public static final int NETWORK_TYPE_EVDO_B = 12; // Level 9
	public static final int NETWORK_TYPE_HSPAP = 15; // Level 13
	public static final int NETWORK_TYPE_IDEN = 11; // Level 8
	public static final int NETWORK_TYPE_LTE = 13; // Level 11


	public static String isConnectedFast(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();

		if ((info != null && info.isConnected())) {
			return isConnectionFast(info.getType(), info.getSubtype());
		} else
			return "No NetWork Access";

	}

	public static String isConnectionFast(int type, int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			System.out.println("CONNECTED VIA WIFI");
			return "CONNECTED VIA WIFI";
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
			case TelephonyManager.NETWORK_TYPE_1xRTT:
				return "NETWORK TYPE 1xRTT"; // ~ 50-100 kbps
			case TelephonyManager.NETWORK_TYPE_CDMA:
				return "NETWORK TYPE CDMA (3G) Speed: 2 Mbps"; // ~ 14-64 kbps
			case TelephonyManager.NETWORK_TYPE_EDGE:

				return "NETWORK TYPE EDGE (2.75G) Speed: 100-120 Kbps"; // ~
																		// 50-100
																		// kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_0:
				return "NETWORK TYPE EVDO_0"; // ~ 400-1000 kbps
			case TelephonyManager.NETWORK_TYPE_EVDO_A:
				return "NETWORK TYPE EVDO_A"; // ~ 600-1400 kbps
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return "NETWORK TYPE GPRS (2.5G) Speed: 40-50 Kbps"; // ~ 100
																		// kbps
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				return "NETWORK TYPE HSDPA (4G) Speed: 2-14 Mbps"; // ~ 2-14
																	// Mbps
			case TelephonyManager.NETWORK_TYPE_HSPA:
				return "NETWORK TYPE HSPA (4G) Speed: 0.7-1.7 Mbps"; // ~
																		// 700-1700
																		// kbps
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				return "NETWORK TYPE HSUPA (3G) Speed: 1-23 Mbps"; // ~ 1-23
																	// Mbps
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return "NETWORK TYPE UMTS (3G) Speed: 0.4-7 Mbps"; // ~ 400-7000
																	// kbps
				// NOT AVAILABLE YET IN API LEVEL 7
			case NETWORK_TYPE_EHRPD:
				return "NETWORK TYPE EHRPD"; // ~ 1-2 Mbps
			case NETWORK_TYPE_EVDO_B:
				return "NETWORK_TYPE_EVDO_B"; // ~ 5 Mbps
			case NETWORK_TYPE_HSPAP:
				return "NETWORK TYPE HSPA+ (4G) Speed: 10-20 Mbps"; // ~ 10-20
																	// Mbps
			case NETWORK_TYPE_IDEN:
				return "NETWORK TYPE IDEN"; // ~25 kbps
			case NETWORK_TYPE_LTE:
				return "NETWORK TYPE LTE (4G) Speed: 10+ Mbps"; // ~ 10+ Mbps
				// Unknown
			case TelephonyManager.NETWORK_TYPE_UNKNOWN:
				return "NETWORK TYPE UNKNOWN";
			default:
				return "";
			}
		} else {
			return "";
		}
	}

	static Matrix matrix = new Matrix();
	private static int containerWidth;
	private static int containerHeight;
	private static Bitmap imgBitmap = null;
	public static final int DEFAULT_SCALE_FIT_INSIDE = 0;
	public static final int DEFAULT_SCALE_ORIGINAL = 1;
	//

	private static int defaultScale;


	public static String getDate(DatePicker datePicker) {
		Calendar dateOfKYC = new GregorianCalendar(datePicker.getYear(),
				datePicker.getMonth(), datePicker.getDayOfMonth());

		return dateOfKYC.get(Calendar.DAY_OF_MONTH) + "/"
				+ dateOfKYC.get(Calendar.MONTH) + "/"
				+ dateOfKYC.get(Calendar.YEAR);
	}

	public static String[] split(String str, String sep) {
		Vector v = new Vector();
		int offset = str.indexOf(sep);
		int sepLen = sep.length();
		while (offset != -1) {
			v.addElement(str.substring(0, offset).trim());
			if (str.length() > (offset + sepLen))
				str = str.substring(offset + sepLen);
			else {
				str = null;
				break;
			}
			offset = str.indexOf(sep);
		}
		if (str != null && str.length() > 0)
			v.addElement(str.trim());
		String[] array = new String[v.size()];
		v.copyInto(array);
		return array;
	}
	public static void writeFile(byte[] data, File f, int id) {

		FileOutputStream out = null;
		if (f != null && !f.exists() && data != null) {
			try {

				f.createNewFile();
				out = new FileOutputStream(f);
				out.write(data);

			} catch (Exception e) {
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
		}

	}
	public static boolean shouldCompressImage(long size){
		if((size/1024) >60)
			return true ;
		else
			return false;  	
	}
	public static boolean shouldCompressImage(long size, int mbkb){
		if((size/1024) >mbkb)
			return true ;
		else
			return false;  	
	}
	
	public static Bitmap getCompressImage(File f){
		Bitmap myBitmap = null ;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		if((f.length()/1024)>1500)
			options.inSampleSize = 8;
		else if((f.length()/1024)>1200)
			options.inSampleSize = 6;
		else if((f.length()/1024)>600)
			options.inSampleSize = 4;
		else
			options.inSampleSize = 3;
//		System.out.println("--------------f.getPath():"+f.getPath());
//		System.out.println("--------------f.getPath():"+f.getPath());
//		System.out.println("--------------f.getAbsolutePath():"+f.getAbsolutePath());
		myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),options);
		return myBitmap ;
	}



	static Notification mNotification;
	public static NotificationManager mNotificationManager;
	static boolean toggle;
	static int HELLO_ID = 1;



	public static boolean checkName(String name) {
		if (name == null)
			return false;
		Matcher m = NAME_PATTERN.matcher(name);
		return m.matches();
	}
	public static boolean checkMobileNUmber(String number) {
		if (number == null)
			return false;
		Matcher m = MOBILE_NUMBER_PATTERN.matcher(number);
		return m.matches();
	}
	public static boolean canSendSMS (Context context)
	{
		PackageManager pm = context.getPackageManager();
		return (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) || pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA));
	}
	public static boolean hasSIMSupport(Context context)
	{
		TelephonyManager telephonyManager1 = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager1.getPhoneType()==TelephonyManager.PHONE_TYPE_NONE)
		{
		//coming here if Tablet 
        	return false;
		}
		else{
		//coming here if phone
			return true;
		}
	}
	public static String getScreenResolution(Context context)
	{
	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics metrics = new DisplayMetrics();
	    display.getMetrics(metrics);
	    int width = metrics.widthPixels;
	    int height = metrics.heightPixels;

	    return "{" + width + "," + height + "}";
	}
	public static String getDensity(Context context)
	{
		int density= context.getResources().getDisplayMetrics().densityDpi;
		String densityValue = "MDPI";
	   switch(density)
	  {
		  case DisplayMetrics.DENSITY_LOW:
		     densityValue = "LDPI";
		      break;
		  case DisplayMetrics.DENSITY_MEDIUM:
		       densityValue = "MDPI";
		      break;
		  case DisplayMetrics.DENSITY_HIGH:
		      densityValue = "HDPI";
		      break;
		  case DisplayMetrics.DENSITY_XHIGH:
		       densityValue = "XHDPI";
		      break;
		  case DisplayMetrics.DENSITY_XXHIGH:
			  densityValue = "XXHDPI";
			  break;
	  }
	   return densityValue;
	}
	//---------------- Validation methods ---------------
		public static boolean validateEmail(String emailstring) {
			if (emailstring == null)
				return false;
			Matcher m = EMAIL_PATTERN.matcher(emailstring);
			return m.matches();
		}
		public static boolean validateName(String name) {
			if (name == null)
				return false;
			Matcher m = NAME_PATTERN.matcher(name);
			return m.matches();
		}
		public static boolean validateUserName(String name) {
			if (name == null)
				return false;
			Matcher m = USERNAME_PATTERN.matcher(name);
			return m.matches();
		}
		public static boolean validateMobileNumber(String number) {
			if (number == null)
				return false;
			Matcher m = MOBILE_NUMBER_PATTERN.matcher(number);
			return m.matches();
		}
		
		public static boolean validatePassword(String password) {
			if (password == null)
				return false;
			Matcher m = PASSWORD_PATTERN.matcher(password);
			return m.matches();
		}
		
		
		public static String getVideoFileExtensionFromFileID(String file_id){
			if(file_id.contains("V1")){
				return ".mp4";
			}
			if(file_id.contains("V2")){
				return ".3gp";
			}
			if(file_id.contains("V3")){
				return ".flv";
			}
			if(file_id.contains("V4")){
				return ".pcm";
			}
			if(file_id.contains("V5")){
				return ".3gpp";
			}
			if(file_id.contains("V6")){
				return ".3gpp2";
			}
			if(file_id.contains("V7")){
				return ".avi";
			}
			if(file_id.contains("V8")){
				return ".mpeg";
			}
			if(file_id.contains("V9")){
				return ".qt";
			}
			if(file_id.contains("V10")){
				return ".qt";
			}
			return null;
		}
		
		public static String getAndroidVersion() {
		    String release = Build.VERSION.RELEASE;
		    int sdkVersion = Build.VERSION.SDK_INT;
		    sOSVersion = sdkVersion + " (" + release +")";
		    return sOSVersion;
		}
		public static String getPhoneModel() {
			if (null != sPhoneModel)
				return sPhoneModel;
			sPhoneModel = Build.MODEL;
			return sPhoneModel;
		}
		public static String getAppVersion(Context context) {
			String version = null;
			try {
				version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(version!=null && version.contains("."))
				version = version.replace(".", "_");
			if(version==null)
				version = "";
			appVersion = "superchat_android_"+version;
			return appVersion;
		}
		public static String getTimeZone() {
			try{
				TimeZone tz = TimeZone.getDefault();
				timeZone = tz.getDisplayName(false, TimeZone.SHORT);
				timeZone = tz.getDisplayName(false, TimeZone.SHORT)+"\nTimezone ID :: " +tz.getID();
			}catch(Exception ex){
				
			}
			return timeZone;
		}
		public static String getOperatorName(Context context) {
			try{
				TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
				operatorName = telephonyManager.getNetworkOperatorName();
//				operatorName = telephonyManager.getSimOperatorName();
			}catch(Exception ex){
				
			}
			return operatorName;
		}
		public static String getCountryName(Context context) {
			try{
				String cc = getCountryCode(context);
				Locale loc = new Locale("",cc);
				countryName = loc.getDisplayCountry();
			}catch(Exception ex){
				
			}
			return countryName;
		}
		public static String getPhoneLanguage() {
			try{
				sPhoneLanguage = Locale.getDefault().getLanguage();
			}catch(Exception ex){
				
			}
			return sPhoneLanguage;
		}
		public static String getCountryCode(Context context)
		{
			try{
				countryCode = context.getResources().getConfiguration().locale.getCountry();
				if(countryCode == null || (countryCode != null && countryCode.trim().length() == 0))
				{
					TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
					countryCode = tm.getNetworkCountryIso();
				}
			}catch(Exception ex){
				
			}
			return countryCode;
		}
		public static String getSupportParameters(Context context){
			StringBuffer buffer = new StringBuffer("App version : ").append(getAppVersion(context));
			buffer.append("\n").append("Platform : Android");
			if(getAndroidVersion() != null)
				buffer.append("\n").append("Android OS Version : "+getAndroidVersion());
			if(getPhoneModel() != null)
				buffer.append("\n").append("Device Model : "+getPhoneModel());
//			if(getCountryCode(context) != null)
//				buffer.append("\n").append("Country Code : "+getCountryCode(context));
//			if(getCountryName(context) != null)
//				buffer.append("\n").append("Country Name : "+getCountryName(context));
			if(getTimeZone() != null)
				buffer.append("\n").append("Time Zone : "+getTimeZone());
			if(getPhoneLanguage() != null)
				buffer.append("\n").append("Language : "+getPhoneLanguage());
			return buffer.toString();
		}
}