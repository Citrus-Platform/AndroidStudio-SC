package com.superchat;

import java.io.File;
import java.util.Calendar;
import java.util.TimeZone;

import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.superchat.data.db.DBWrapper;
import com.superchat.ui.ChatListScreen;
import com.superchat.utils.Constants;
import com.superchat.utils.SharedPrefManager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
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
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.RemoteViews;

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
	static String notificationActivity = ".ui.ChatListScreen";

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
        String senderDisplayName = null;
        String message = null;
        String from = "";
        String screen = null;
        String[] data = null;
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
                Log.i(TAG, "GCM - Push Message Received: " + extras.toString());
//                if(!isMyServiceRunning(ChatService.class, SuperChatApplication.context))
                {
                	Log.i(TAG, "isMyServiceRunning : false");
	                if(ChatService.xmppConectionStatus){
	                	Log.i(TAG, "ChatService.xmppConectionStatus: "+ChatService.xmppConectionStatus);
	         		   ChatService.xmppConectionStatus = false;
	         		   stopService(new Intent(SuperChatApplication.context, ChatService.class));
	         	   }
	         	   startService(new Intent(SuperChatApplication.context, ChatService.class));
                }
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
    public void showNotificationForGroupMessage(String senderName, String groupID,
			String displayName, String msg, int type, int mediaType) {
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
			notificationSenderName = DBWrapper.getInstance().getChatName(senderName);
			if(notificationSenderName!=null && notificationSenderName.contains("#786#"))
				notificationSenderName = notificationSenderName.substring(0, notificationSenderName.indexOf("#786#"));
			if(notificationSenderName.equals(senderName)){
//				notificationSenderName = notificationSenderName.replaceFirst("m", "+");
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
				tickerText = "Message from " + notificationSenderName + "@" + grpDisplayName;
		messageNotification.setWhen(System.currentTimeMillis());
		messageNotification.setTicker(tickerText);
		Intent notificationIntent = new Intent();
		Log.d(TAG, "notificationPackage: "+notificationPackage+" , "+notificationActivity);
		notificationIntent.setClassName(notificationPackage, notificationPackage+notificationActivity);
//		Intent notificationIntent = new Intent(context,
//				ChatListScreen.class);
		notificationIntent.putExtra(ChatDBConstants.CONTACT_NAMES_FIELD, grpDisplayName);
		notificationIntent.putExtra(ChatDBConstants.USER_NAME_FIELD, user);
		notificationIntent.putExtra("FROM_NOTIFICATION", true);
		
//		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.broadcasttoall.ordinal())
//			notificationIntent.putExtra("FROM_BULLETIN_NOTIFICATION", true);
		
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent contentIntent = PendingIntent.getActivity(
				SuperChatApplication.context, 0, notificationIntent,
				PendingIntent.FLAG_ONE_SHOT);
//		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal())
//			messageNotification.setContentTitle(notificationSenderName + "@" + SharedPrefManager.getInstance().getSharedIDDisplayName(grpDisplayName));
//		else
		if(grpDisplayName != null && !grpDisplayName.trim().equals(""))
			messageNotification.setContentTitle(notificationSenderName + "@" + grpDisplayName);
		else
			messageNotification.setContentTitle(notificationSenderName);
		messageNotification.setContentText(msg);
		messageNotification.setContentIntent(contentIntent);
		int count = prefManager.getChatCountOfUser(user);
		Notification notification = messageNotification.build();
		if(R.layout.message_notifier!=-1){
			RemoteViews contentView = new RemoteViews(
					SuperChatApplication.context.getPackageName(),
					R.layout.message_notifier);
//			if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.sharedID.ordinal())
//				contentView.setTextViewText(R.id.chat_person_name, notificationSenderName+"@"+SharedPrefManager.getInstance().getSharedIDDisplayName(grpDisplayName));
//			else
				contentView.setTextViewText(R.id.chat_person_name, notificationSenderName+"@"+grpDisplayName);
			Uri uri = getPicUri(user);
			if(uri!=null)
				contentView.setImageViewUri(R.id.imagenotileft, uri);
			else
				contentView.setImageViewResource(R.id.imagenotileft, R.drawable.chat_person);
			if(mediaType == 0)
				contentView.setTextViewText(R.id.chat_message, msg);
			else{
				if(mediaType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Video message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Picture message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Voice message");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Doc file");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Pdf file");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
					contentView.setTextViewText(R.id.chat_message, "XLS file");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
					contentView.setTextViewText(R.id.chat_message, "PPT file");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Shared a location");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Shared contact");
				else if(mediaType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal())
					contentView.setTextViewText(R.id.chat_message, "Poll");
			}
			if (count > 0) {
				contentView.setTextViewText(R.id.chat_notification_bubble_text, String.valueOf(count));
			}
			notification.contentView = contentView;
		}
//		Random random = new Random();
		int id = (senderName + "@" + grpDisplayName).hashCode();
		if (id < -1)
			id = -(id);
//		Log.d(TAG, "showNotificationForMessage1: "+from+" , "+currentUser+" , "+onForeground);
		if(prefManager.isSnoozeExpired() && ((ChatListScreen.onForeground && !ChatListScreen.currentUser
										.equals(groupID)) || !ChatListScreen.onForeground))
		notificationManager.notify(id, notification);

	}
   //========================================
    public void showNotificationForP2PMessage(String from, String displayName,
			String msg, byte messageType, int mediaType) {
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
		Intent notificationIntent = new Intent();
		Log.d(TAG, "notificationPackage: "+notificationPackage+" , "+notificationActivity);
		notificationIntent.setClassName(notificationPackage, notificationPackage+notificationActivity);
		notificationIntent.putExtra(ChatDBConstants.CONTACT_NAMES_FIELD, displayName);
		notificationIntent.putExtra(ChatDBConstants.USER_NAME_FIELD, user);
		notificationIntent.putExtra("FROM_NOTIFICATION", true);
		
//		if(message.getStatusMessageType().ordinal() == Message.StatusMessageType.broadcasttoall.ordinal())
//			notificationIntent.putExtra("FROM_BULLETIN_NOTIFICATION", true);
		
		
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
		PendingIntent contentIntent = PendingIntent.getActivity(
				context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

		messageNotification.setContentTitle(displayName);
			 
			messageNotification.setContentText(msg);
		messageNotification.setContentIntent(contentIntent);
		int count = prefManager.getChatCountOfUser(user);
		Notification notification = messageNotification.build();
		
		if(R.layout.message_notifier!=-1){
			RemoteViews contentView = new RemoteViews(
					context.getPackageName(),
					R.layout.message_notifier);
			contentView.setTextViewText(R.id.chat_person_name, displayName);
			Uri uri = getPicUri(user);
			if(uri!=null)
			contentView.setImageViewUri(R.id.imagenotileft, uri);
//			setProfilePic()
			
				if(mediaType == 0)
					contentView.setTextViewText(R.id.chat_message, msg);
				else{
					if(mediaType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal())
						contentView.setTextViewText(R.id.chat_message, "Picture message");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
						contentView.setTextViewText(R.id.chat_message, "Voice message");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal())
						contentView.setTextViewText(R.id.chat_message, "Video message");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
						contentView.setTextViewText(R.id.chat_message, "Doc message");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
						contentView.setTextViewText(R.id.chat_message, "Pdf message");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
						contentView.setTextViewText(R.id.chat_message, "XLS message");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
						contentView.setTextViewText(R.id.chat_message, "PPT message");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal())
						contentView.setTextViewText(R.id.chat_message, "Shared a location");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal())
						contentView.setTextViewText(R.id.chat_message, "Shared contact");
					else if(mediaType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal())
						contentView.setTextViewText(R.id.chat_message, "Poll");
				}
			if (count > 0) {
				contentView.setTextViewText(R.id.chat_notification_bubble_text, String.valueOf(count));
			}
			notification.contentView = contentView;
		}
		int id = user.hashCode();
		if (id < -1)
			id = -(id);
		Log.d(TAG, "showNotificationForMessage: "+from+" , "+currentUser+" , "+onForeground);
		if(prefManager.isSnoozeExpired() && ((ChatListScreen.onForeground && !ChatListScreen.currentUser
				.equals(from)) || !ChatListScreen.onForeground))
		notificationManager.notify(id, notification);
		previousUser = from;
		isFirstMessage = false;
		startService(new Intent(SuperChatApplication.context, ChatService.class));
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
}

