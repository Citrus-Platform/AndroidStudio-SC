package com.superchat.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
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

import com.superchat.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class UtilTemp {
	private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	public static String getDateTime(){
		Calendar cal = Calendar.getInstance();
		String dateTime = sdf.format(cal.getTime());
		return dateTime;
	}

	public static String getCallRelatedLogs(final AudioManager audioManager){
		String log = "";
		try{
			if(audioManager != null) {
				log = log + "\t\nUser -> "+SharedPrefManager.getInstance().getUserName();
				log = log + "\t\nAudio Logs -> \n\n";
				log = log + "\t\nisMicrophoneMute : "+audioManager.isMicrophoneMute();
				log = log + "\t\nisMusicActive : "+audioManager.isMusicActive();
				log = log + "\t\nisSpeakerphoneOn : "+audioManager.isSpeakerphoneOn();
				log = log + "\t\nisBluetoothScoAvailableOffCall : "+audioManager.isBluetoothScoAvailableOffCall();
				log = log + "\t\nisBluetoothA2dpOn : "+audioManager.isBluetoothA2dpOn();
				log = log + "\t\nisBluetoothScoOn : "+audioManager.isBluetoothScoOn();
				log = log + "\t\nisVolumeFixed : "+audioManager.isVolumeFixed();
			}
		} catch(Exception e){

		}
		return log;
	}

	public static  void sendLogs(final Context context, final AudioManager audioManager){
		Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
		mailIntent.setType("text/plain");
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Phone Log at : "+ UtilTemp.getDateTime());
		mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@citrusplatform.com",
				"munish@citrusplatform.com", "tariq@citrusplatform.com"});
		mailIntent.putExtra(Intent.EXTRA_TEXT, getCallRelatedLogs(audioManager));
		final PackageManager pm = context.getPackageManager();
		final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
		ResolveInfo best = null;
		for (final ResolveInfo info : matches)
			if (info.activityInfo.packageName.endsWith(".gm") ||
					info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
		if (best != null)
			mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
		context.startActivity(mailIntent);
	}

}
