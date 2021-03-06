package com.superchat;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;

import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.superchat.data.db.DBWrapper;
import com.superchat.emojicon.EmojiconTextView;
import com.superchat.ui.ChatListScreen;
import com.superchat.ui.HomeScreen;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

import static com.google.android.gms.internal.zzip.runOnUiThread;

/**
 * Created by maheshsonker on 15/05/16.
 */
public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static Bitmap bitmap = null;
	boolean isSameUser = false;
	static String userMe = "";
	String displayUserName = "";
	Calendar calender;
	boolean onForeground;
	String currentUser = "";
	public static Context context;
	ChatDBWrapper chatDBWrapper;
	public static final String MEDIA_TYPE =  ".amr";
	public static boolean xmppConectionStatus = false; 
	private static String notificationAllMessage="";
	static boolean isFirstMessage = true;
	static String previousUser = "";
	private static NotificationManager notificationManager;
	private Builder messageNotification;
	SharedPrefManager prefManager;
	static String notificationPackage = "";
//	static String notificationActivity = ".ui.ChatListScreen";
	static String notificationActivity = ".ui.HomeScreen";
	static String homeScreen = ".ui.HomeScreen";

	private EmojiconTextView notificationTextView;

    public GcmIntentService() {
        super("GcmIntentService");
        prefManager = SharedPrefManager.getInstance();
		context = SuperChatApplication.context;
		chatDBWrapper = ChatDBWrapper.getInstance(context);
		calender = Calendar.getInstance(TimeZone.getDefault());
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		userMe = prefManager.getUserName();
		displayUserName = prefManager.getDisplayName();
		bitmap = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_view);//Bitmap.createBitmap(createColors(), 0, STRIDE, WIDTH, HEIGHT,Bitmap.Config.RGB_565);
		notificationPackage = context.getPackageName();
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        String senderUserName = null;
        String groupName = null;
        String domainName = null;
        String senderDisplayName = null;
        String message = null;
        String from = "";
        String screen = null;
        String[] data = null;
		String currentDomain = prefManager.getUserDomain();
		String user = prefManager.getUserName();
        if (extras != null && !extras.isEmpty()) {
        	if(extras.containsKey("message"))
        		message = extras.getString("message");
        	if(message != null && message.indexOf(":") != -1){
        		data = message.split(":");
        		if(data != null && data.length == 2){
        			senderDisplayName = data[0];
        			message = data[1];
        		}
        	}
        	if(extras.containsKey("username"))
        		senderUserName = extras.getString("username");
        	if(extras.containsKey("domainName"))
				domainName = extras.getString("domainName");
        	if(extras.containsKey("screen"))
        		screen = extras.getString("screen");
        	if(extras.containsKey("groupname"))
        		groupName = extras.getString("groupname");
//            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
//            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " + extras.toString());
//            } else 
            	if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
               
//                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
//                sendNotification("Received: " + extras.toString());
//            		if(screen != null && screen.equalsIgnoreCase("group"))
//            			showNotificationForGroupMessage(senderUserName, groupName, senderDisplayName, message, 0, 0);
//            		else
//            			showNotificationForP2PMessage(senderUserName, senderDisplayName, message, (byte)0, 0);
                System.out.println("GCM Push Message Received: " + extras.toString());

					if(user == null)
						return;

					try {
						//Check For special Message type
						if (senderUserName == null && groupName == null) {
							if (HomeScreen.isforeGround) {
								if(message != null) {
									if(screen != null && screen.equals("invite"))
										EventBus.getDefault().post("[INVITE] : "+message);
									else
										EventBus.getDefault().post(message);
								}
							} else {
								if(message != null) {
									if(screen != null && screen.equals("invite"))
										EventBus.getDefault().post("[INVITE] : "+message);
									else
										EventBus.getDefault().post(message);
									showSystemMessage(message, screen);
									GcmBroadcastReceiver.completeWakefulIntent(intent);
								}
							}
							return;
						}
					} catch(Exception e){

					}

				if(domainName != null && !domainName.equals(currentDomain)){
					if(screen != null && screen.equalsIgnoreCase("group"))
            			showNotificationForGroupMessage(screen, domainName, senderUserName, groupName, senderDisplayName, message, 0, 0, true);
            		else
            			showNotificationForP2PMessage(screen, domainName, senderUserName, senderDisplayName, message, (byte)0, 0, true);

				}else{
					Log.i(TAG, "isMyServiceRunning : false");
					if(!ChatService.xmppConectionStatus){
						Log.i(TAG, "ChatService.xmppConnectionStatus: "+ChatService.xmppConectionStatus);
						ChatService.xmppConectionStatus = false;
						stopService(new Intent(SuperChatApplication.context, ChatService.class));
						startService(new Intent(SuperChatApplication.context, ChatService.class));
					}else{
						Log.i(TAG, "isMyServiceRunning : True");
					}
				}
//                if(!isMyServiceRunning(ChatService.class, SuperChatApplication.context))
//                {
//                	Log.i(TAG, "isMyServiceRunning : false");
//	                if(ChatService.xmppConectionStatus){
//	                	Log.i(TAG, "ChatService.xmppConectionStatus: "+ChatService.xmppConectionStatus);
//	         		   ChatService.xmppConectionStatus = false;
//	         		   stopService(new Intent(SuperChatApplication.context, ChatService.class));
//	         	   }
//	         	   startService(new Intent(SuperChatApplication.context, ChatService.class));
//                }
//                else
//                	Log.i(TAG, "isMyServiceRunning : true");
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
   //============================================================================
//    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
//	    ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
//	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//	        if (serviceClass.getName().equals(service.service.getClassName())) {
//	            Log.i("Service already","running");
//	            return true;
//	        }
//	    }
//	    Log.i("Service not","running");
//	    return false;
//    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ChatListScreen.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        //Start xmppservice
//        startService(new Intent(SuperChatApplication.context, ChatService.class));
//		startService(new Intent(SuperChatApplication.context, SinchService.class));
    }
 //===================================================================================
    public void showNotificationForGroupMessage(final String screen, final String domainName, String senderName, String groupID,
			String displayName, final String msg, int type, int mediaType, boolean forOtherSG) {
		 if(senderName != null && senderName.contains("#786#"))
			 senderName = senderName.substring(0, senderName.indexOf("#786#"));
		CharSequence tickerText = msg;
		String user = groupID;
		prefManager = SharedPrefManager.getInstance();
		String grpDisplayName = prefManager.getGroupDisplayName(user);
		if (messageNotification == null) {
			messageNotification = new NotificationCompat.Builder(SuperChatApplication.context);
			messageNotification.setSmallIcon(R.drawable.chatgreen);
			messageNotification.setAutoCancel(true);
			messageNotification.setLights(Color.RED, 3000, 3000);
		}
		Notification note = messageNotification.build();
		if(prefManager.isMute(groupID))
			note.defaults = 0;
		else
			note.defaults |= Notification.DEFAULT_SOUND;
		note.defaults |= Notification.DEFAULT_LIGHTS;
		// messageNotification.setOnlyAlertOnce(true);
		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if(prefManager.isMute(groupID))
			messageNotification.setSound(null);
		else
			messageNotification.setSound(alarmSound);
		
		String notificationSenderName = senderName;
		if(SuperChatApplication.getInstance().getContextApplication() ==  null)
			DBWrapper.getInstance(SuperChatApplication.context);
			notificationSenderName = DBWrapper.getInstance().getChatName(senderName);
			if(notificationSenderName!=null && notificationSenderName.contains("#786#"))
				notificationSenderName = notificationSenderName.substring(0, notificationSenderName.indexOf("#786#"));
			if(notificationSenderName.equals(senderName)){
				if(notificationSenderName.contains("_"))
					notificationSenderName = "+"+notificationSenderName.substring(0, notificationSenderName.indexOf("_"));
				}
			if(displayName != null)
				notificationSenderName = displayName;
			else
				notificationSenderName = "New user";
//			if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal())
//				tickerText = "Message from " + notificationSenderName + "@" + SharedPrefManager.getInstance().getSharedIDDisplayName(grpDisplayName);
//			else
//				tickerText = "Message from " + notificationSenderName + "@" + grpDisplayName;
		if(displayName != null && displayName.length() > 0)
			tickerText = "Message from " + displayName;
		else
			tickerText = "Message from " + notificationSenderName + "@" + grpDisplayName;
		messageNotification.setWhen(System.currentTimeMillis());
		messageNotification.setTicker(tickerText);
		final Intent notificationIntent = new Intent(context, HomeScreen.class);
		Log.d(TAG, "notificationPackage: "+notificationPackage+" , "+notificationActivity);
//		notificationIntent.setClassName(notificationPackage, "com.superchat.ui.HomeScreen");
//		Intent notificationIntent = new Intent(context,
//				ChatListScreen.class);
		notificationIntent.putExtra(ChatDBConstants.CONTACT_NAMES_FIELD, grpDisplayName);
		notificationIntent.putExtra(ChatDBConstants.USER_NAME_FIELD, user);
		notificationIntent.putExtra("FROM_NOTIFICATION", true);
		notificationIntent.putExtra("SYSTEM_MESSAGE", false);
		notificationIntent.putExtra("DOMAIN_NAME", domainName);
		notificationIntent.putExtra("SCREEN_NAME", screen);

//		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.broadcasttoall.ordinal())
//			notificationIntent.putExtra("FROM_BULLETIN_NOTIFICATION", true);
		
//		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent contentIntent = PendingIntent.getActivity(
				SuperChatApplication.context, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
//		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal())
//			messageNotification.setContentTitle(notificationSenderName + "@" + SharedPrefManager.getInstance().getSharedIDDisplayName(grpDisplayName));
//		else
		if(displayName != null && !displayName.trim().equals(""))
			messageNotification.setContentTitle(displayName);
		else
			messageNotification.setContentTitle(notificationSenderName);

		messageNotification.setContentIntent(contentIntent);
//		int count = prefManager.getChatCountOfUser(user);
//		Notification notification = messageNotification.build();
//		if(R.layout.message_notifier!=-1){
//			RemoteViews contentView = new RemoteViews(
//					SuperChatApplication.context.getPackageName(),
//					R.layout.message_notifier_group);
//
//
////			if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal())
////				contentView.setTextViewText(R.id.chat_person_name, notificationSenderName+"@"+SharedPrefManager.getInstance().getSharedIDDisplayName(grpDisplayName));
////			else
////				contentView.setTextViewText(R.id.chat_person_name, notificationSenderName+"@"+grpDisplayName);
//			if(notificationSenderName != null && notificationSenderName.contains("]")) {
//				contentView.setTextViewText(R.id.chat_sg_name, notificationSenderName.substring(0, notificationSenderName.indexOf("]") + 1));
//				contentView.setTextViewText(R.id.chat_person_name, notificationSenderName.substring(notificationSenderName.indexOf("]") + 1));
//			}
//			else {
//				contentView.setTextViewText(R.id.chat_sg_name, notificationSenderName);
//				contentView.setTextViewText(R.id.chat_person_name, notificationSenderName);
//			}
//			Uri uri = getPicUri(user);
//			if(uri!=null)
//				contentView.setImageViewUri(R.id.imagenotileft, uri);
//			else
//				contentView.setImageViewResource(R.id.imagenotileft, R.drawable.chat_person);
//			if(mediaType == 0)
//				contentView.setTextViewText(R.id.chat_message, msg);
//			else{
//				if(mediaType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "Video message");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "Picture message");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "Voice message");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "Doc file");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "Pdf file");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "XLS file");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "PPT file");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "Shared a location");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "Shared contact");
//				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal())
//					contentView.setTextViewText(R.id.chat_message, "Poll");
//			}
//			if (count > 0) {
//				contentView.setTextViewText(R.id.chat_notification_bubble_text, String.valueOf(count));
//			}
//			notification.contentView = contentView;
//		}
//		Random random = new Random();
		int id = (senderName + "@" + grpDisplayName).hashCode();
		if (id < -1)
			id = -(id);
//		Log.d(TAG, "showNotificationForMessage1: "+from+" , "+currentUser+" , "+onForeground);
		if(prefManager.isSnoozeExpired(prefManager.getUserDomain()) && ((ChatListScreen.onForeground && !ChatListScreen.currentUser
										.equals(groupID)) || !ChatListScreen.onForeground)) {
//			notificationManager.notify(id, notification);
			final int unique_id = id;
			final int count = prefManager.getChatCountOfUser(user);
			final String disp = displayName;
			final String imageID = SharedPrefManager.getInstance().getUserFileId(user);
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					new showNotificationWithImage(notificationIntent, count, domainName, disp, msg, imageID, unique_id, (byte)2).execute();
				}
			});
		}

	}
   //========================================
    public void showNotificationForP2PMessage(final String screen, final String domainName, final String from, String displayName,
			final String msg, byte messageType, int mediaType, boolean forOtherSG) {
		if(screen != null && screen.equals("bulletin") && from != null && from.equals("admin")){
			return;
		}
    	SharedPrefManager sharedPref = SharedPrefManager.getInstance();
		 if(displayName!=null && displayName.contains("#786#"))
			 displayName = displayName.substring(0, displayName.indexOf("#786#"));
		CharSequence tickerText = msg;
		String user = from;
		if(displayName.equals(from)){
			displayName = sharedPref.getUserServerName(from);
			if(displayName.equals(from)){
				if(displayName.contains("_")){
//					displayName = "+"+displayName.substring(0, displayName.indexOf("_"));
					if(displayName != null){
						sharedPref.saveUserServerName(from, displayName);
						String picId = sharedPref.getUserFileId(from);
						if(picId!=null && !picId.equals(""))
							SharedPrefManager.getInstance().saveUserFileId(from, picId);
					}
				}
				}
			}
		if (messageNotification == null) {
			messageNotification = new NotificationCompat.Builder(context);
			messageNotification.setSmallIcon(R.drawable.chatgreen);
			messageNotification.setAutoCancel(true);
			messageNotification.setLights(Color.RED, 3000, 3000);

		}
		Notification note = messageNotification.build();
		if(prefManager.isMute(from))
			note.defaults = 0;
		else
			note.defaults |= Notification.DEFAULT_SOUND;
		note.defaults |= Notification.DEFAULT_LIGHTS;
		// messageNotification.setOnlyAlertOnce(true);
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if(prefManager.isMute(from))
			messageNotification.setSound(null);
		else
			messageNotification.setSound(alarmSound);
		
		tickerText = "Message from " + displayName;
		messageNotification.setWhen(System.currentTimeMillis());
		messageNotification.setTicker(tickerText);
		Log.d(TAG, "notificationPackage: "+notificationPackage+" , "+notificationActivity);

		final Intent notificationIntent = new Intent(context, HomeScreen.class);
		notificationIntent.setClassName(notificationPackage, notificationPackage+notificationActivity);
		notificationIntent.putExtra(ChatDBConstants.CONTACT_NAMES_FIELD, displayName);
		notificationIntent.putExtra(ChatDBConstants.USER_NAME_FIELD, user);
		notificationIntent.putExtra("FROM_NOTIFICATION", true);
		notificationIntent.putExtra("DOMAIN_NAME", domainName);
		notificationIntent.putExtra("SCREEN_NAME", screen);
		notificationIntent.putExtra("SYSTEM_MESSAGE", false);

//		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.broadcasttoall.ordinal())
//			notificationIntent.putExtra("FROM_BULLETIN_NOTIFICATION", true);
		
		
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		messageNotification.setContentTitle(displayName);
		messageNotification.setContentText(msg);
		messageNotification.setContentIntent(contentIntent);
//		int count = prefManager.getChatCountOfUser(user);
		Notification notification = messageNotification.build();
		try {

//			if (R.layout.message_notifier != -1) {
//				RemoteViews contentView = new RemoteViews(
//						SuperChatApplication.context.getPackageName(),
//						R.layout.message_notifier);
//				contentView.setTextViewText(R.id.chat_person_name, displayName);
//				Uri uri = getPicUri(user);
//				if (uri != null)
//					contentView.setImageViewUri(R.id.imagenotileft, uri);
////			setProfilePic()
//
//				if (mediaType == 0)
//					contentView.setTextViewText(R.id.chat_message, msg);
//				else {
//					if (mediaType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "Picture message");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "Voice message");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "Video message");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "Doc message");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "Pdf message");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "XLS message");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "PPT message");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "Shared a location");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "Shared contact");
//					else if (mediaType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal())
//						contentView.setTextViewText(R.id.chat_message, "Poll");
//				}
//				if (count > 0) {
//					contentView.setTextViewText(R.id.chat_notification_bubble_text, String.valueOf(count));
//				}
//				notification.contentView = contentView;
//			}
			int id = user.hashCode();
			if (id < -1)
				id = -(id);
			Log.d(TAG, "showNotificationForMessage: " + from + " , " + currentUser + " , " + onForeground);
			final String imageID = SharedPrefManager.getInstance().getUserFileId(user);
			if (prefManager.isSnoozeExpired(prefManager.getUserDomain()) && ((ChatListScreen.onForeground && !ChatListScreen.currentUser
					.equals(from)) || !ChatListScreen.onForeground)) {
//                notificationManager.notify(id, notification);
				final int unique_id = id;
				final String disp = displayName;
				final int count = prefManager.getChatCountOfUser(user);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        new showNotificationWithImage(notificationIntent, count, domainName, disp, msg, imageID, unique_id, (byte)1).execute();
                    }
                });
            }
			previousUser = from;
			isFirstMessage = false;
		}catch(Exception ex){
			ex.printStackTrace();
		}
//		startService(new Intent(SuperChatApplication.context, ChatService.class));
	}
	//============================================================================================================
	public void showSystemMessage(String msg, String screen) {
		SharedPrefManager sharedPref = SharedPrefManager.getInstance();
		CharSequence tickerText = msg;
		String user = "SuperChat";
		String displayName = user;

		if (messageNotification == null) {
			messageNotification = new NotificationCompat.Builder(context);
			messageNotification.setSmallIcon(R.drawable.chatgreen);
			messageNotification.setAutoCancel(true);
			messageNotification.setLights(Color.RED, 3000, 3000);

		}
		Notification note = messageNotification.build();

		note.defaults |= Notification.DEFAULT_SOUND;
		note.defaults |= Notification.DEFAULT_LIGHTS;
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		messageNotification.setSound(alarmSound);
		tickerText = "Message from " + displayName;
		messageNotification.setWhen(System.currentTimeMillis());
		messageNotification.setTicker(tickerText);
		Intent notificationIntent = new Intent();
		Log.d(TAG, "notificationPackage: "+notificationPackage+" , "+notificationActivity);
		notificationIntent.setClassName(notificationPackage, notificationPackage + homeScreen);
		notificationIntent.putExtra(ChatDBConstants.CONTACT_NAMES_FIELD, displayName);
		notificationIntent.putExtra(ChatDBConstants.USER_NAME_FIELD, user);
		notificationIntent.putExtra("FROM_NOTIFICATION", true);
		notificationIntent.putExtra("SYSTEM_MESSAGE", true);
		notificationIntent.putExtra("SYSTEM_MESSAGE_TEXT", msg);
		notificationIntent.putExtra("SCREEN_NAME", screen);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		messageNotification.setContentTitle(displayName);
		messageNotification.setContentText(msg);
		messageNotification.setContentIntent(contentIntent);
		messageNotification.setPriority(Notification.PRIORITY_MAX);
		int count = prefManager.getChatCountOfUser(user);
		Notification notification = messageNotification.build();
		if(R.layout.message_notifier != -1){
			RemoteViews contentView = new RemoteViews(
					context.getPackageName(),
					R.layout.system_push_layout);
			contentView.setTextViewText(R.id.chat_person_name, displayName);
			Uri uri = getPicUri(user);
			if(uri!=null)
				contentView.setImageViewUri(R.id.imagenotileft, uri);
			contentView.setTextViewText(R.id.chat_message, msg);
			if (count > 0) {
				contentView.setTextViewText(R.id.chat_notification_bubble_text, String.valueOf(count));
			}
			notification.contentView = contentView;
		}
		int id = user.hashCode();
		if (id < -1)
			id = -(id);
		Log.d(TAG, "showSpecialMessage: "+user+" , "+currentUser+" , "+onForeground);
		notificationManager.notify(id, notification);
		previousUser = user;
		isFirstMessage = false;
	}
    //========================================
    private Uri getPicUri(String userName){
		String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02
		if(groupPicId!=null && !groupPicId.equals("")){
			String profilePicUrl = groupPicId;//AppConstants.media_get_url+
			if(!profilePicUrl.contains(".jpg"))
				profilePicUrl = groupPicId+".jpg";//AppConstants.media_get_url+

			File file = Environment.getExternalStorageDirectory();
//			String filename = file.getPath()+ File.separator + "SuperChat/"+profilePicUrl;
			String filename = file.getPath()+ File.separator + Constants.contentProfilePhoto+profilePicUrl;
//			view.setTag(filename);
			File file1 = new File(filename);
			if(file1!=null && file1.exists()){
				return Uri.parse(filename);
			}
//				view.setImageURI(Uri.parse(filename));
//				view.setBackgroundDrawable(null);
//			}
		}
//		else if(SharedPrefManager.getInstance().isGroupChat(userName))
//			view.setImageResource(R.drawable.chat_person);
//		else
//			view.setImageResource(R.drawable.avatar); // 
		return null;
	}
	//---------------------------------------------------
	class showNotificationWithImage extends AsyncTask<String, Void, Bitmap> {

		private Context mContext;
		private Bundle extras;
		private Intent intent;
		private String displayName;
		private String title, message, imgURL, tickerMsg;
		PendingIntent contentIntent = null;
		int unique_id;
		byte type;
		int count;

		public showNotificationWithImage(Intent intent, int count, String tickerMsg, String displayName,
                                         String msg, String imgURL, int unique_id, byte type) {
			super();
			this.mContext = context;
			this.extras = extras;
//			if(extras != null) {
//				if(extras.containsKey("imgURL")) {
//					this.imgURL = extras.getString("imgURL");
//					if(imgURL != null && imgURL.length() > 0)
//						try {
//							imgURL = URLDecoder.decode(imgURL, "UTF-8");
//						} catch (UnsupportedEncodingException e) {
//							e.printStackTrace();
//						}
//				}
//				if(extras.containsKey("text1")) {
//					this.title = extras.getString("text1");
//				}
//				if(extras.containsKey("text2")) {
//					this.message = extras.getString("text2");
//				}
//			}
            this.intent = intent;
            this.displayName = displayName;
            this.tickerMsg = tickerMsg;
            this.title = msg;
            this.message = msg;
            this.imgURL = imgURL;
			this.unique_id = unique_id;
			this.type = type;
			this.count = count;

			if(imgURL != null)
				this.imgURL = Constants.media_convertget_url + imgURL + ".jpg?height=100&width=100";
		}

		@Override
		protected Bitmap doInBackground(String... params) {

			InputStream in;
			if(imgURL != null && imgURL.trim().length() > 0) {
				try {
					URL url = new URL(this.imgURL);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setDoInput(true);
					connection.connect();
					in = connection.getInputStream();
					Bitmap myBitmap = BitmapFactory.decodeStream(in);
					return myBitmap;
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}else
				return null;
		}

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
//			System.out.println("GCM Push Message : unique_id = "+unique_id);
			PendingIntent pendingIntent = PendingIntent.getActivity(mContext, unique_id, intent, PendingIntent.FLAG_ONE_SHOT);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
			Bitmap icon = null;
			switch(type){
				case 1:
					icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.profile_pic);
					break;
				case 2:
					icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.chat_person);
					break;
			}
			if(result != null)
				icon = result;
			Notification notification = mBuilder.setSmallIcon(R.drawable.logo_small).setTicker(tickerMsg)
					.setAutoCancel(true)
					.setContentTitle(displayName)
					.setContentText(message)
					.setNumber(++count)
//					.setStyle(result == null ? new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(displayName) : new NotificationCompat.BigPictureStyle().bigPicture(result).setSummaryText(message))
					.setStyle(new NotificationCompat.BigTextStyle().bigText(message).setBigContentTitle(displayName))
					.setContentIntent(pendingIntent)
					.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
					.setLargeIcon(icon)
					.setPriority(Notification.PRIORITY_MAX)
					.build();

			notification.flags = Notification.FLAG_AUTO_CANCEL;
			NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.notify(unique_id, notification);
		}
	}
}

