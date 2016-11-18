package com.superchat.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatCountListener;
import com.chat.sdk.ChatService;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sinch.android.rtc.calling.Call;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.beans.PhotoToLoad;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.emojicon.EmojiconTextView;
import com.superchat.interfaces.OnChatEditInterFace;
import com.superchat.model.UserProfileModel;
import com.superchat.task.ImageLoaderWorker;
import com.superchat.utils.AndroidMultiPartEntity;
import com.superchat.utils.AndroidMultiPartEntity.ProgressListener;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.ImageDownloader.Mode;
import com.superchat.utils.Log;
import com.superchat.utils.MediaEngine;
import com.superchat.utils.MyBase64;
import com.superchat.utils.RTMediaPlayer;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.Utilities;
import com.superchat.utils.VoiceMediaHandler;
import com.superchat.widgets.RoundedImageView;

import org.apache.commons.fileupload.util.Streams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.entity.mime.FormBodyPart;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;

import static com.chatsdk.org.xbill.DNS.Type.A;

//import com.superchat.utils.ImageDownloader;
public class ChatListAdapter extends SimpleCursorAdapter {
    public class ViewHolder implements VoiceMediaHandler {
        //		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 150);
        private String MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?zoom=13&size=150x150&markers=size:mid|color:red|$lat,$lon&sensor=true";
        //		private final MediaDownloader imageDownloader = new MediaDownloader();
        private RelativeLayout.LayoutParams params;
        private LinearLayout receiverLayout;
        private LinearLayout senderLayout;
        private RelativeLayout rightFileLayout;
        private RelativeLayout leftFileLayout;
        private RelativeLayout leftRightCompositeView;
        private RelativeLayout voiceRecieverInnerLayout;
        private RelativeLayout voiceSenderInnerLayout;
        private LinearLayout voiceSenderLayout;
        private LinearLayout leftAudioBtnLayout;
        private LinearLayout rightAudioBtnLayout;
        private LinearLayout sDateLayout;
        private ImageView playSenderView;
        private ImageView playRecieverView;
        private SeekBar playSenderSeekBar;
        private SeekBar playRecieverSeekBar;
        private RelativeLayout dateLayout;
        //		private TextView playSenderTimeText;
        private TextView playSenderMaxTimeText;
        private TextView playRecieverTimeText;
        private TextView playRecieverMaxTimeText;
        private EmojiconTextView senderMsgText;
        private TextView senderTime;
        private TextView receiverPersonName;
        private EmojiconTextView receiverMsgText;
        private TextView receiverTime;
        private EmojiconTextView dateText;
        private TextView messageStatusView;
        private TextView rightFileTypeView;
        private TextView leftFileTypeView;
        private CheckBox senderCheckBox;
        private CheckBox receiverCheckBox;
        private ImageView sendImgView;
        private EmojiconTextView sendTagView;
        private EmojiconTextView recieveTagView;
        private EmojiconTextView recieveAudioTagView;
        private EmojiconTextView senderAudioTagView;
        private ImageView receiveImgView;
        private ImageView sVideoPlayImageView;
        private ImageView rVideoPlayImageView;
        private ImageView unsentAlertView;
        private View parentView;
        boolean isTimeLineMessage;
        //Contact Layout - Receiver Side
        private LinearLayout contactLayoutReceiver;
        private RoundedImageView contactIconReceiver;
        private TextView contactNameReceiver;
        private RelativeLayout rDateLayout;
        public String contactData;
        public String locationData;

        //Poll Layout - Receiver Side
        private LinearLayout pollLayoutReceiver;
        private TextView pollTtitleReceiver;
        private TextView pollMessageReceiver;
        public String pollMessageReceived;

        //Location Layout - Receiver Side
        private RelativeLayout locationLayoutReceiver;
        private TextView locationNameReceiver;
        private TextView locationNameAddressReceiver;
        private ImageView mapviewReceiver;
        GoogleMap mapReceiver;


        private ProgressBar progressbar;
        private ProgressBar rightImgProgressBar;
        private ProgressBar leftImgProgressIndeterminate;
        private ProgressBar rightImgProgressIndeterminate;
        private ProgressBar voiceDownloadingBar;
        private ProgressBar voiceDownloadIndeterminateBar;
        private TextView progressPercent;
        private TextView voiceLoadingPercent;
        private TextView rightImgProgressPercent;
        RelativeLayout leftPersonPicLayout;
        private RoundedImageView leftPersonPic;
        private ImageView leftPersonDefaultPic;
        //Contact Layout
        private LinearLayout contactLayout;
        private RoundedImageView contactIcon;
        private TextView contactName;
        public String contactDataSent;

        //Poll Layout
        private LinearLayout pollLayout;
        private TextView pollTitle;
        public TextView pollMessage;
        public String pollDataSent;

        //Location Layout - Receiver Side
        private RelativeLayout locationLayout;
        private TextView locationName;
        private TextView locationNameAddress;
        private ImageView mapviewSender;
        GoogleMap mapSender;

        //Reply Shared ID
        RelativeLayout replySharedID;


        public String receiverName = null;
        public String key;
        public String message;
        public String mediaUrl;
        public String audioLength;
        public String mediaLocalPath;
        public String mediaThumb;
        public long time;
        public boolean isDateShow;
        public String groupMsgSenderName;
        public int seenState;
        public int messageType;
        public int messageTypeID;
        public String userName;
        public String captionTagMsg;
        public String locationMsg;
        int totalGroupUsers = 0;
        int totalGroupReadUsers = 0;
        public XMPPMessageType fileType;

        public boolean listItemSelection() {
            if (!isEditableChat)
                return false;

            boolean isWorking = false;
            ListView listView1 = (ListView) parentView.getParent();
            System.out.println("listView1 : " + listView1.getCount());
            if (listView1 != null) {
                final int position = listView1.getPositionForView(parentView);
                listView1.setItemChecked(position, !listView1.isItemChecked(position));

                isWorking = true;
            }
            return isWorking;
        }

        private OnLongClickListener onLongPressListener = new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

//				listView.setItemChecked(g, true);
//				if(!isTimeLineMessage)
                setEditableChat(true);
                listItemSelection();
                Log.d(TAG, "long press key : " + key);
//				if(v.getTag()!=null && !iChatPref.isBlocked(chatName)){
//					checkedTagMap.put(key, true);
//					if(checkedTagMap.get(key) && totalItemChecked() == 1 && !receiverName.equals(SharedPrefManager.getInstance().getUserName())&&(groupMsgSenderName== null || groupMsgSenderName.equals("") || groupMsgSenderName.contains(SharedPrefManager.getInstance().getUserName()))){
//						if(!isBulletinBroadcast && !isBroadCastChat){
//							((ChatListScreen) context).chatInfoIv.setVisibility(View.VISIBLE);
//							((ChatListScreen) context).chatInfoIv.setTag(key);
//						}
//						}
//					iEditListener.onChatEditEnable((String) v.getTag());
//					}
                return false;
            }
        };
        private OnClickListener onCheckeClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (listItemSelection())
                    return;
                String key = (String) v.getTag();
                if (checkedTagMap.get(key) == null) {
                    checkedTagMap.put(key, false);
                }
                checkedTagMap.put(key, !checkedTagMap.get(key));
//				if((isBroadCastChat || iChatPref.isGroupChat(chatName)) && checkedTagMap.get(key) && totalItemChecked() == 1 && (groupMsgSenderName== null || groupMsgSenderName.equals("") || groupMsgSenderName.contains(SharedPrefManager.getInstance().getUserName()))){
                if (checkedTagMap.get(key) && totalItemChecked() == 1 && !receiverName.equals(SharedPrefManager.getInstance().getUserName()) && (groupMsgSenderName == null || groupMsgSenderName.equals("") || groupMsgSenderName.contains(SharedPrefManager.getInstance().getUserName()))) {
                    if (!isBulletinBroadcast && !isBroadCastChat) {
                        ((ChatListScreen) context).chatInfoIv.setTag(key);
                        ((ChatListScreen) context).chatInfoIv.setVisibility(View.VISIBLE);


                    }
                } else {
                    ((ChatListScreen) context).chatInfoIv.setVisibility(View.GONE);
//					if((isBroadCastChat || iChatPref.isGroupChat(chatName)) && totalItemChecked() == 1 && (groupMsgSenderName== null || groupMsgSenderName.equals("") || groupMsgSenderName.contains(SharedPrefManager.getInstance().getUserName()))){
                    if (totalItemChecked() == 1 && !receiverName.equals(SharedPrefManager.getInstance().getUserName()) && (groupMsgSenderName == null || groupMsgSenderName.equals("") || groupMsgSenderName.contains(SharedPrefManager.getInstance().getUserName()))) {
                        ((ChatListScreen) context).chatInfoIv.setTag(singleCheckedKey());
                        ((ChatListScreen) context).chatInfoIv.setVisibility(View.VISIBLE);
                    }
                }
                if (checkedTagMap.get(key) && messageType == XMPPMessageType.atMeXmppMessageTypeNormal.ordinal())
                    ((ChatListScreen) context).chatCopyIv.setVisibility(View.VISIBLE);
//				iEditListener.onChatEditEnable("Y");
            }
        };
        private OnClickListener onSenderBubbleClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (listItemSelection())
                    return;
                senderCheckBox.performClick();
            }
        };
        private OnClickListener onReceiverBubbleClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listItemSelection())
                    return;
                // TODO Auto-generated method stub
//				if(isSharedIDMessage){
//					String user = (String) v.getTag();
//					Intent intent = new Intent(SuperChatApplication.context,  ChatListScreen.class);
//					intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, SharedPrefManager.getInstance().getUserServerName(user));
//					intent.putExtra(DatabaseConstants.USER_NAME_FIELD, user);
//					((ChatListScreen) context).startActivity(intent);onc
//
//				}else
                receiverCheckBox.performClick();
            }
        };
        private OnClickListener onVoiceClickListener = new OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (listItemSelection())
                    return;
                switch (v.getId()) {
                    case R.id.send_mediavoicePlayingDialog_progressbar:
                    case R.id.mediavoicePlayingDialog_progressbar:
                        break;
                    case R.id.send_media_play:
                    case R.id.media_play:
                    case R.id.left_audio_btn_layout:
                    case R.id.right_audio_btn_layout:

                        try {
                            if (mediaLocalPath == null || mediaLocalPath.equals(""))
                                return;
                            Log.d("ChatListAdapter", "TESTER_ onVoiceClickListener called. " + mediaLocalPath);
                            boolean isPlaying = playerBundle.getBoolean(key);
                            if (isPlaying) {
//                                System.out.println("Playing - key - "+key);
//							playerBundle.putBoolean(mediaLocalPath, false);
                                playerBundle.putBoolean(key, false);
                                myVoicePlayer.reset();
                                myVoicePlayer.clear();
                                globalSeekBarValue = 0;
                                globalSeekBarMaxValue = 0;
                                audioPlayerKey = null;
                            } else {
                                View previousView = myVoicePlayer.getCurrentView();
                                if (previousView != null) {
//                                    System.out.println("111111");
                                    myVoicePlayer.reset();
                                    myVoicePlayer.clear();
                                    globalSeekBarValue = 0;
                                    globalSeekBarMaxValue = 0;
                                    audioPlayerKey = null;
                                    if (previousView.getTag() != null)
                                        playerBundle.putBoolean(previousView.getTag().toString(), false);
                                }
                                playerBundle.putBoolean(key, true);
                                Thread.sleep(500);
                                if (userName.equals(myUserName)) {
//                                    System.out.println("mememememe");
                                    audioPlayerKey = key;
                                    myVoicePlayer.setMediaHandler(ViewHolder.this);
                                    myVoicePlayer.setProgressBar(playSenderSeekBar);
                                    rightAudioBtnLayout.setBackgroundResource(R.drawable.round_rect_gray);
                                    playSenderView.setBackgroundResource(R.drawable.addpause);
                                    playingVoicePath = mediaLocalPath;
                                    myVoicePlayer._startPlay(mediaLocalPath, playSenderView, handler);
                                    globalSeekBarValue = 0;
                                    if ((audioLength == null || audioLength.equals("") || audioLength.equals("0")) && myVoicePlayer.getDuration() != 0) {
                                        updateMediaLengthInDb(key, String.valueOf(myVoicePlayer.getDuration() / 1000));
                                    }
                                } else {
//                                    System.out.println("other");
                                    audioPlayerKey = key;
                                    myVoicePlayer.setMediaHandler(ViewHolder.this);
                                    myVoicePlayer.setProgressBar(playRecieverSeekBar);
                                    leftAudioBtnLayout.setBackgroundResource(R.drawable.round_rect_gray);
                                    playRecieverView.setBackgroundResource(R.drawable.addpause);
                                    playingVoicePath = mediaLocalPath;
                                    myVoicePlayer._startPlay(mediaLocalPath, playRecieverView, handler);
                                    globalSeekBarValue = 0;
                                    if ((audioLength == null || audioLength.equals("") || audioLength.equals("0")) && myVoicePlayer.getDuration() != 0) {
                                        updateMediaLengthInDb(key, String.valueOf(myVoicePlayer.getDuration() / 1000));
                                    }
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    // case R.id.media_stop_play:
                    // // media_play_layout.setVisibility(View.GONE);
                    // // voiceIsPlaying = false;
                    // RTMediaPlayer.reset();
                    // RTMediaPlayer.clear();
                    // break;
                }
            }
        };

        private OnClickListener onLocationClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (listItemSelection())
                    return;
                try {
                    if (locationData != null) {
                        String[] loc = locationData.split(",");
                        double lat = Double.parseDouble(loc[0]);
                        double lon = Double.parseDouble(loc[1]);
                        String head = "My Location";
                        head = ((ChatListScreen) context).getAddress(lat, lon);
                        String geoUriString = "geo:" + lat + "," + lon + "?q=(" + head + ")@" + lat + "," + lon;
                        Log.i(TAG, "onClick :: geoUriString : " + geoUriString);
                        Uri geoUri = Uri.parse(geoUriString);
                        Intent mapCall = new Intent(Intent.ACTION_VIEW, geoUri);
                        ((ChatListScreen) context).startActivity(mapCall);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        private OnClickListener onContactClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listItemSelection())
                    return;
                try {
                    if (contactData != null) {
                        try {
                            //Show Values from JSON
                            JSONObject jsonobj = new JSONObject(contactData);
                            JSONArray phoneNumber = null;
                            String dislay_name = "Unknown";
                            String[] number_type = null;
                            String[] number_value = null;
                            ArrayList<String> contactList = new ArrayList<String>();
                            ArrayList<String> emailList = new ArrayList<String>();
                            ArrayList<String> contactTypeList = new ArrayList<String>();
                            ArrayList<String> emailTypeList = new ArrayList<String>();
                            String[] email_type = null;
                            String[] email_value = null;
                            if (jsonobj.has("firstName") && jsonobj.getString("firstName").toString().trim().length() > 0)
                                dislay_name = jsonobj.getString("firstName");
                            if (jsonobj.has("lastName") && jsonobj.getString("lastName").toString().trim().length() > 0)
                                dislay_name = dislay_name + " " + jsonobj.getString("lastName");

                            if (jsonobj.has("phoneNumber"))
                                phoneNumber = jsonobj.getJSONArray("phoneNumber");
                            if (phoneNumber.length() > 0) {
                                number_type = new String[phoneNumber.length()];
                                number_value = new String[phoneNumber.length()];
                                for (int i = 0; i < phoneNumber.length(); i++) {
                                    JSONObject obj = (JSONObject) phoneNumber.get(i);
                                    if (obj.has("type")) {
                                        String tmpType = obj.getString("type");
                                        number_type[i] = tmpType;
                                        if (tmpType != null && !tmpType.equals(""))
                                            contactTypeList.add(tmpType);
                                    }

                                    if (obj.has("number")) {
                                        String tmpNumber = obj.getString("number").replace(" ", "");
                                        number_value[i] = tmpNumber;
                                        if (tmpNumber != null && !tmpNumber.equals("") && !isDuplicatePhone(tmpNumber, contactList))
                                            contactList.add(tmpNumber);
                                    }
                                }
                            }
                            //Get email
                            if (jsonobj.has("EmailIDs"))
                                phoneNumber = jsonobj.getJSONArray("EmailIDs");
                            if (phoneNumber.length() > 0) {
                                email_type = new String[phoneNumber.length()];
                                email_value = new String[phoneNumber.length()];
                                for (int i = 0; i < phoneNumber.length(); i++) {
                                    JSONObject obj = (JSONObject) phoneNumber.get(i);
                                    if (obj.has("type")) {
                                        String tmpType = obj.getString("type");
                                        email_type[i] = tmpType;
                                        if (tmpType != null && !tmpType.equals(""))
                                            emailTypeList.add(tmpType);
                                    }
                                    if (obj.has("email")) {
                                        String tmpEmail = obj.getString("email");
                                        email_value[i] = tmpEmail;
                                        if (tmpEmail != null && !tmpEmail.equals(""))
                                            emailList.add(tmpEmail);
                                    }
                                }
                            }

                            Intent intent = new Intent(SuperChatApplication.context, ContactViewScreen.class);
                            intent.putExtra(Constants.CONTACT_NAME, dislay_name);
                            intent.putExtra(Constants.CONTACT_TYPE_NUMBERS, contactTypeList);
                            intent.putExtra(Constants.CONTACT_TYPE_EMAILS, emailTypeList);
                            intent.putExtra(Constants.CONTACT_NUMBERS, contactList);
                            intent.putExtra(Constants.CONTACT_EMAILS, emailList);
                            ((ChatListScreen) context).startActivity(intent);

//							Intent intent = new Intent(Intent.ACTION_INSERT,  ContactsContract.Contacts.CONTENT_URI);
//							intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
//							// Just two examples of information you can send to pre-fill out data for the
//							// user.  See android.provider.ContactsContract.Intents.Insert for the complete
//							// list.
//							intent.putExtra(ContactsContract.Intents.Insert.NAME, dislay_name);
//							if(number_value != null && number_value[0] != null)
//								intent.putExtra(ContactsContract.Intents.Insert.PHONE, number_value[0]);
//							if(email_value != null && email_value[0] != null)
//								intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email_value[0]);
//							((ChatListScreen) context).startActivity(intent);

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        private OnClickListener onPollClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listItemSelection())
                    return;
                String message = (String) v.getTag();
                if (message != null && message.startsWith("sender="))
                    ((ChatListScreen) context).showPoll(message.substring(message.indexOf("sender=") + 7), false);
                else
                    ((ChatListScreen) context).showPoll(message, false);
            }
        };

        private boolean isDuplicatePhone(String currentPhone, ArrayList<String> phoneList) {
            String forMatchNumber = currentPhone;//.replace(" ", "");
            if (currentPhone != null && currentPhone.length() > 10) {
                forMatchNumber = currentPhone.substring(currentPhone.length() - 10);
            }
            for (String tmpPhone : phoneList) {
//				tmpPhone = tmpPhone.replace(" ", "");
                if (tmpPhone.contains(forMatchNumber))
                    return true;
            }
            return false;
        }

        private OnClickListener onImageClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listItemSelection())
                    return;
                try {
                    if (v.getId() == R.id.id_left_pic || v.getId() == R.id.id_left_default_pic) {
//						if(isSharedIDMessage){
//							Intent intent = new Intent(SuperChatApplication.context,  ChatListScreen.class);
//							intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, SharedPrefManager.getInstance().getUserServerName(groupMsgSenderName));
//							intent.putExtra(DatabaseConstants.USER_NAME_FIELD, groupMsgSenderName);
//							((ChatListScreen) context).startActivity(intent);
//							return;
//
//						}else
                        {
                            String personId = groupMsgSenderName;
                            if (!isSharedIDMessage)
                                personId = groupMsgSenderName.substring(groupMsgSenderName.indexOf("#786#") + "#786#".length());
                            getServerUserProfile(personId);//showShortProfile(personId);
                            return;
                        }
                    }
                    if (mediaLocalPath != null && !mediaLocalPath.equals("")) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        // intent.setDataAndType(Uri.parse(mediaLocalPath),
                        // "image/jpeg");
                        // /storage/emulated/0/Atme/streem/tmp/Atme/1421374623085

                        if (mediaLocalPath.contains(".mp4") || mediaLocalPath.endsWith(".3gp")) {
                            intent.setDataAndType(Uri.parse(mediaLocalPath), "video/*");
//							System.out.println("[[modle]] "+Utilities.getPhoneModel());
                            if (Utilities.getPhoneModel().contains("ASUS"))
                                intent.setClassName("com.UCMobile.intl", "com.UCMobile.main.UCMobile.alias.video");
                        } else if (mediaLocalPath.contains(".pdf")) {
                            intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/pdf");
                        } else if (mediaLocalPath.contains(".doc")) {
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/msword");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else if (mediaLocalPath.contains(".xls")) {
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/vnd.ms-excel");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else if (mediaLocalPath.contains(".ppt")) {
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/ppt");
                            intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/vnd.ms-powerpoint");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        } else if (mediaLocalPath.contains(".mp4")) {
                            intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "video/*");
                        } else if (mediaLocalPath != null && !mediaLocalPath.equals(""))
                            intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "image/*");
//						if (mediaLocalPath != null
//								&& mediaLocalPath.startsWith("http:")) {
//							if (mediaLocalPath.contains(".mp4")) {
//								intent.setDataAndType(Uri.parse(mediaLocalPath), "video/*");
//							} else {
//								intent.setDataAndType(Uri.parse(mediaLocalPath), "image/jpeg");
//							}
//						} else {
////							System.out.println("mediaLocalPath == >"+mediaLocalPath);
//							if (mediaLocalPath.contains(".pdf")) {
//								intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/pdf");
//							}else if (mediaLocalPath.contains(".doc")) {
//							    intent.addCategory("android.intent.category.DEFAULT");
//							    intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
//							    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/msword");
//							    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//							}else if (mediaLocalPath.contains(".xls")) {
//							    intent.addCategory("android.intent.category.DEFAULT");
//							    intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
//							    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/vnd.ms-excel");
//							    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//							}else if (mediaLocalPath.contains(".ppt")) {
//							    intent.addCategory("android.intent.category.DEFAULT");
//							    intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
////							    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/ppt");
//							    intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "application/vnd.ms-powerpoint");
//							    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//							}else if (mediaLocalPath.contains(".mp4")) {
//								intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "video/*");
//							} else
//								intent.setDataAndType(Uri.parse("file://" + mediaLocalPath), "image/*");
//						}
                        ((ChatListScreen) context).startActivity(intent);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(mContext, "Viewer not available.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        public ViewHolder() {
        }

        @Override
        public void voiceRecordingStarted(String recordingPath) {
            Log.d("ChatListAdapter", "TESTER_ voiceRecordingStarted called. " + mediaLocalPath);

        }

        @Override
        public void voiceRecordingCompleted(String recordedVoicePath) {
            Log.d("ChatListAdapter", "TESTER_ voiceRecordingCompleted called. " + mediaLocalPath);

        }

        @Override
        public void voicePlayStarted() {
            Log.d("ChatListAdapter", "TESTER_ voicePlayStarted called. " + mediaLocalPath);

        }

        @Override
        public void voicePlayCompleted(View view) {
            availableVoice = true;
            voiceIsPlaying = false;
            Log.d("ChatListAdapter", "TESTER_ voicePlayCompleted called. " + mediaLocalPath);
            if (playingVoicePath != null)
                playerBundle.putBoolean(key, false);
            globalSeekBarValue = 0;
            globalSeekBarMaxValue = 0;
            audioPlayerKey = null;
            progressUpdateHandler.sendEmptyMessage(0);
            if (userName.equals(myUserName)) {
                playSenderSeekBar.setProgress(0);
                ((ChatListScreen) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        rightAudioBtnLayout.setBackgroundResource(R.drawable.round_rect_blue);
                        playSenderView.setBackgroundResource(R.drawable.addplay);
                    }
                });

            } else {
                playRecieverSeekBar.setProgress(0);
                ((ChatListScreen) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        leftAudioBtnLayout.setBackgroundResource(R.drawable.round_rect_blue);
                        playRecieverView.setBackgroundResource(R.drawable.addplay);
                    }
                });

            }
            if (view.getTag() != null) {
                try {
                    android.os.Message msg = new android.os.Message();
                    Bundle data = new Bundle();
                    int length = Integer.parseInt(view.getTag().toString());
                    data.putLong("total", length);
                    data.putLong("spent", length);
                    data.putString("currentuser", userName);
                    msg.setData(data);
                    voiceTimerHandler.sendMessage(msg);
                } catch (Exception e) {
                }
            }
            //			((ChatListScreen) context).refreshAdpter();

        }

        @Override
        public void onError(int i) {
            Log.d("ChatListAdapter", "TESTER_ onError called. " + mediaLocalPath);

        }

        @Override
        public void onDureationchanged(long total, long current, SeekBar currentSeekBar) {
//			android.util.Log.d("ChatListAdapter", "onDureationchanged current. " + globalSeekBarValue);
            globalSeekBarMaxValue = (int) total;
            if (playingVoicePath != null && mediaLocalPath != null && !playingVoicePath.equals("") && playingVoicePath.equals(mediaLocalPath)) {
                globalSeekBarValue = (int) current;
//					if (userName.equals(myUserName)) {
//						currentSeekBar.setProgress((int)current);
//
//					}else{
//						currentSeekBar.setProgress((int)current);
//
//					}
                currentAudioPlayCounter = current;
//				android.os.Message msg = new android.os.Message();
//				Bundle data = new Bundle();
//				data.putLong("total", total);
//				data.putLong("spent", current);
//				data.putString("currentuser", userName);
//				msg.setData(data);
//				voiceTimerHandler.sendMessage(msg);
                progressUpdateHandler.sendEmptyMessage(0);
            }

        }

        Handler voiceTimerHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // android.os.Message msg = handler.obtainMessage();
                Bundle bundle = msg.getData();
//				Log.d("ChatListAdapter",
//						"Timing by message " + bundle.getString("Timing"));
//				timeSpent = bundle.getString("Timing");
                if (bundle != null) {
                    int total = (int) bundle.getLong("total");
                    int current = (int) bundle.getLong("spent");
                    String tUsers = bundle.getString("currentuser");
                    if (tUsers.equals(myUserName)) {
                        long ttt = total - current;
                        if ((current + 100) >= total)
                            ttt = total;
                        ttt = ttt / 1000;
                        byte min = (byte) (ttt / 60);
                        byte sec = (byte) (ttt % 60);
                        if (min < 9)
                            playSenderMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                        else
                            playSenderMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                    } else {
                        long ttt = total - current;
                        if ((current + 100) >= total)
                            ttt = total;
                        ttt = ttt / 1000;
                        byte min = (byte) (ttt / 60);
                        byte sec = (byte) (ttt % 60);
                        if (min < 9)
                            playRecieverMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                        else
                            playRecieverMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                    }
                }
            }
        };
        private static final String LOG_TAG = "ImageDownloader";

        //		    public enum Mode { NO_ASYNC_TASK, NO_DOWNLOADED_DRAWABLE, CORRECT }
        private Mode mode = Mode.CORRECT;
        ChatListAdapter adaptor;

        /**
         * Download the specified image from the Internet and binds it to the provided ImageView. The
         * binding is immediate if the image is found in the cache and will be done asynchronously
         * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
         *
         * @param url       The URL of the image to download.
         * @param imageView The ImageView to bind the downloaded image to.
         */
//		    public void download(String url, ImageView imageView) {
//		        resetPurgeTimer();
//		        Bitmap bitmap = getBitmapFromCache(url);
        //
//		        if (bitmap == null) {
//		            forceDownload(url, imageView);
//		        } else {
//		            cancelPotentialDownload(url, imageView);
//		            imageView.setImageBitmap(bitmap);
//		        }
//		    }
//		    public void uploadMedia(String url,int msgType, ProgressBar pb){//XMPPMessageType type){//String mediaLocalPath, String key, String mediaThumb, XMPPMessageType type){
//		    	Log.d(TAG, "file uploading repeating: "+url+" , "+processing.get(url)+" , "+key);
//		    	MediaFileUpload mediaFileUpload = new MediaFileUpload(key,url,pb);
//		    	if(Build.VERSION.SDK_INT >= 11){
//		    		fileUploadMap.put(key,mediaFileUpload);
//		    		mediaFileUpload.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, key,mediaThumb, msgType+"",captionTagMsg);
//		    	}  else{
//			    	fileUploadMap.put(key,mediaFileUpload);
//			    	mediaFileUpload.execute(url, key,mediaThumb, msgType+"",captionTagMsg);
//		    	}
//		    }
        public void uploadMedia(String url, int msgType) {
            if (processing.containsKey(url))
                return;
            System.out.println("file uploading repeating: " + url + " , " + processing.get(url) + " , " + key);
            if (Build.VERSION.SDK_INT >= 11)
                new MediaFileUpload(key, url).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url, key, mediaThumb, msgType + "", captionTagMsg);
            else
                new MediaFileUpload(key, url).execute(url, key, mediaThumb, msgType + "", captionTagMsg);
            processing.put(url, "0");
        }

        public void download(String url, int msgType, ImageView imageView, ProgressBar pb, Object[] callbackParams) {
            processing.put(url, "1");
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, pb, callbackParams, msgType);
            if (Build.VERSION.SDK_INT >= 11)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
            else
                task.execute(url);
//		    	resetPurgeTimer();
//		    	String localPath = getBitmapFromCache(url);
//
//		    	if (localPath == null) {
//		    		processing.put(url, "0");
//		    		adaptor = (ChatListAdapter)callbackParams[0];
//		    		forceDownload(url, imageView, pb, callbackParams);
//		    	} else {
//		    		cancelPotentialDownload(url, imageView, pb, callbackParams);
////		    		imageView.setImageURI(Uri.parse(localPath));
//		    		if(pb != null)
//		    			pb.setVisibility(View.GONE);
//		    	}
        }

        public String getProcessingForURL(String url) {
            if (url == null)
                return url;
            return ((String) processing.get(url));
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
//		            imageView.setImageDrawable(null);
                if (imageView != null)
                    imageView.setVisibility(View.GONE);
                return;
            }

            if (cancelPotentialDownload(url, imageView, pb, params)) {
                switch (mode) {
//		                case NO_ASYNC_TASK:
//		                    Bitmap bitmap = downloadBitmap(url);
//		                    addBitmapToCache(url, bitmap);
//		                    imageView.setImageBitmap(bitmap);
//		                    break;
                    //
//		                case NO_DOWNLOADED_DRAWABLE:
//		                    imageView.setMinimumHeight(156);
//		                    BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, pb);
//		                    task.execute(url);
//		                    break;

                    case CORRECT:
                        BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, pb, params, 0);
                        DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                        if (imageView != null)
                            imageView.setImageDrawable(downloadedDrawable);
                        if (pb != null) {
                            pb.setVisibility(View.VISIBLE);
                            pb.setProgress(0);
                        }
                        if (imageView != null)
                            imageView.setMinimumHeight(156);
                        task.execute(url);
                        break;
                }
            }
        }

//		    private void forceDownloadWithProgress(String url, ImageView imageView, ProgressBar pb) {
//		    	// State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
//		    	if (url == null) {
//		    		imageView.setImageDrawable(null);
//		    		return;
//		    	}
//
//		    	if (cancelPotentialDownload(url, imageView)) {
//		    		switch (mode) {
//		    		case NO_ASYNC_TASK:
//		    			Bitmap bitmap = downloadBitmap(url);
//		    			addBitmapToCache(url, bitmap);
//		    			imageView.setImageBitmap(bitmap);
//		    			break;
//
//		    		case NO_DOWNLOADED_DRAWABLE:
//		    			imageView.setMinimumHeight(156);
//		    			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
//		    			task.execute(url);
//		    			break;
//
//		    		case CORRECT:
//		    			task = new BitmapDownloaderTask(imageView);
//		    			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
//		    			imageView.setImageDrawable(downloadedDrawable);
//		    			imageView.setMinimumHeight(156);
//		    			task.execute(url);
//		    			break;
//		    		}
//		    	}
//		    }

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
                    DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                    return downloadedDrawable.getBitmapDownloaderTask();
                }
            }
            return null;
        }

//		    Bitmap downloadBitmap(String url) {
//		        final int IO_BUFFER_SIZE = 4 * 1024;
        //
//		        // AndroidHttpClient is not allowed to be used from the main thread
//		        final HttpClient client = (mode == Mode.NO_ASYNC_TASK) ? new DefaultHttpClient() :
//		            AndroidHttpClient.newInstance("Android");
//		        final HttpGet getRequest = new HttpGet(url);
        //
//		        try {
//		            HttpResponse response = client.execute(getRequest);
//		            final int statusCode = response.getStatusLine().getStatusCode();
//		            if (statusCode != HttpStatus.SC_OK) {
//		                Log.w("ImageDownloader", "Error " + statusCode +
//		                        " while retrieving bitmap from " + url);
//		                return null;
//		            }
        //
//		            final HttpEntity entity = response.getEntity();
//		            if (entity != null) {
//		                InputStream inputStream = null;
//		                try {
//		                    inputStream = entity.getContent();
//		                    // return BitmapFactory.decodeStream(inputStream);
//		                    // Bug on slow connections, fixed in future release.
//		                    return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
//		                } finally {
//		                    if (inputStream != null) {
//		                        inputStream.close();
//		                    }
//		                    entity.consumeContent();
//		                }
//		            }
//		        } catch (IOException e) {
//		            getRequest.abort();
//		            Log.w(LOG_TAG, "I/O error while retrieving bitmap from " + url, e);
//		        } catch (IllegalStateException e) {
//		            getRequest.abort();
//		            Log.w(LOG_TAG, "Incorrect URL: " + url);
//		        } catch (Exception e) {
//		            getRequest.abort();
//		            Log.w(LOG_TAG, "Error while retrieving bitmap from " + url, e);
//		        } finally {
//		            if ((client instanceof AndroidHttpClient)) {
//		                ((AndroidHttpClient) client).close();
//		            }
//		        }
//		        return null;
//		    }

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
            int msgType;
//		        private WeakReference<ImageView> imageViewReference = null;
//		        private final WeakReference<ProgressBar> progressbar;
//		        private final WeakReference<Object[]> callbakParams;

            public BitmapDownloaderTask(ImageView imageView, ProgressBar pb, Object[] params, int msgType) {
//		        	if(imageView!=null)
//		        		imageViewReference = new WeakReference<ImageView>(imageView);
//		            progressbar = new WeakReference<ProgressBar>(pb);
//		            callbakParams = new WeakReference<Object[]>(params);
                this.msgType = msgType;
                if (params != null && params.length > 0)
                    url = params[params.length - 1].toString();
                objParams = params;
            }

            //		      /*  Before starting background thread. Show Progress Bar Dialog */
//		      @Override
            protected void onPreExecute() {
                super.onPreExecute();
//		            if(imageViewReference != null)
//		            	imageViewReference.get().setBackgroundResource(R.drawable.def_bt_img);
//		            if(progressbar != null)
//		          	{
//		          		ProgressBar pBar = progressbar.get();
//		          		pBar.setProgress(0);
//		          		pBar.setVisibility(View.VISIBLE);
//		          	}
//		            if(callbakParams != null)
//		            {
//		            	objParams = callbakParams.get();
//		            	TextView tv = (TextView)objParams[3];
//		            	tv.setVisibility(View.VISIBLE);
//		            	tv.setText("initiating..");
//		            }
            }

            /**
             * Actual download method.
             */
            @Override
            protected String doInBackground(String... params) {

//		            return downloadBitmap(url);

                int count;
                String filename = Environment.getExternalStorageDirectory().getPath() + File.separator + "SuperChat";
                File file = new File(filename);
                if (!file.exists()) {
                    file.mkdirs();
                }
                try {
                    if (params[0] != null && params[0].length() > 0)
                        filename += params[0].substring(params[0].lastIndexOf('/'));
                    URL url = new URL(params[0]);
                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();
                    // input stream to read file - with 8k buffer
                    BufferedInputStream input = new BufferedInputStream(url.openStream());
//					  BitmapFactory.decodeStream(input,null,options);
//					  input.reset();
                    // Output stream to write file
                    if (msgType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal() || msgType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()
                            || msgType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal() || msgType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
                        saveFileSizeInChatDB(params[0], lenghtOfFile);
                    OutputStream output = new FileOutputStream(filename);
                    Environment.getExternalStorageDirectory().getPath();
                    byte data[] = new byte[4096];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress((int) total, (int) lenghtOfFile);
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
                    if (url != null) {
                        objParams = null;
                        if (filename != null) {
                            File file1 = new File(filename);
                            file1.delete();
                        }
                        processing.remove(url);
                    }
                    return null;
                }
                return filename;
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
//		          // setting progress percentage
//		        	if(progressbar != null)
                {
//		        		ProgressBar pBar = progressbar.get();
                    TextView tv = null;//(TextView)objParams[3];
//		        		if(pBar != null)
                    {
                        if (tv != null)
                            tv.setText("[" + progress[0] / 1024 + "KB of " + progress[1] / 1024 + "KB]");
//		        			tv.setText(Integer.parseInt(""+progress[0]) + "%");
//		        			pBar.setProgress(Integer.parseInt(""+(int)((progress[0]*100)/progress[1])));
//		        			pBar.setSecondaryProgress(Integer.parseInt(""+progress[0]) + 5);
                    }
                }
                if (url != null)
                    processing.put(url, "" + Integer.parseInt("" + (int) ((progress[0] * 100) / progress[1])));
            }

            /**
             * Once the image is downloaded, associates it to the imageView
             */
            @Override
            protected void onPostExecute(String filePath) {
                if (isCancelled()) {
                    filePath = null;
                }
                if (filePath != null)
                    ((ChatListScreen) context).sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
                if (objParams != null && filePath != null) {
                    updateDataWithCursor(filePath, (String) objParams[1], null);
                }
                if (url != null)
                    processing.remove(url);
            }
        }
        //--------------- Mahesh - My Code ---------
            /* Background Async Task to download file */
//		    class DownloadFileFromURL extends AsyncTask<String, String, String> {
//		                    /*  Before starting background thread. Show Progress Bar Dialog */
//		                    @SuppressWarnings("deprecation")
//		                    @Override
//		                    protected void onPreExecute() {
//		                            super.onPreExecute();
////		                            showDialog(CUSTOM_PROGRESS_DIALOG);
//		                    }
//		                    /* Downloading file in background thread */
//		                    @Override
//		                    protected String doInBackground(String... f_url) {
//		                            int count;
//		                    try {
//		                        URL url = new URL(f_url[0]);
//		                        URLConnection conection = url.openConnection();
//		                        conection.connect();
//		                        // getting file length
//		                        int lenghtOfFile = conection.getContentLength();
//		                        // input stream to read file - with 8k buffer
//		                        InputStream input = new BufferedInputStream(url.openStream(), 8192);
//		                        // Output stream to write file
//		                        OutputStream output = new FileOutputStream("/sdcard/filedownload.jpg");
//		                        byte data[] = new byte[1024];
//		                        long total = 0;
//		                        while ((count = input.read(data)) != -1) {
//		                            total += count;
//		                            // publishing the progress....
//		                            // After this onProgressUpdate will be called
//		                            publishProgress(""+(int)((total*100)/lenghtOfFile));
//		                            // writing data to file
//		                            output.write(data, 0, count);
//		                        }
//		                        // flushing output
//		                        output.flush();
//		                        // closing streams
//		                        output.close();
//		                        input.close();
//
//		                    } catch (Exception e) {
//		                            Log.e("Error: ", e.getMessage());
//		                    }
//		                    return null;
//		                    }
//		                    /* Updating progress bar */
//		                    protected void onProgressUpdate(String... value) {
//		                            // setting progress percentage
//		                            pDialog.setProgress(Integer.parseInt(value[0]));
//		                            pDialog.setSecondaryProgress(Integer.parseInt(value[0]) + 5);
//		         }
//		                    /*  After completing background task. Dismiss the progress dialog */
//		                    @SuppressWarnings("deprecation")
//		                    @Override
//		                    protected void onPostExecute(String file_url) {
//		                            // dismiss the dialog after the file was downloaded
//		                            dismissDialog(CUSTOM_PROGRESS_DIALOG);
//		                            // Displaying downloaded image into image view
//		                            // Reading image path from sdcard
//		                            String imagePath = Environment.getExternalStorageDirectory().toString() + "/filedownload.jpg";
//		                            // setting downloaded into image view
//		                            my_image.setImageDrawable(Drawable.createFromPath(imagePath));
//		                    }
//		            }

        //-----------------------------------------

        /**
         * A fake Drawable that will be attached to the imageView while the download is in progress.
         * <p/>
         * <p>Contains a reference to the actual download task, so that a download task can be stopped
         * if a new binding is required, and makes sure that only the last started download process can
         * bind its result, independently of the download finish order.</p>
         */
        class DownloadedDrawable extends ColorDrawable {
            private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

            public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
//		            super(Color.GRAY);
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
        private final HashMap<String, String> sHardBitmapCache = new LinkedHashMap<String, String>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
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
         *
         * @param bitmap The newly downloaded bitmap.
         */
//		    private void addBitmapToCache(String url, Bitmap bitmap) {
//		        if (bitmap != null) {
//		            synchronized (sHardBitmapCache) {
//		                sHardBitmapCache.put(url, bitmap);
//		            }
//		        }
//		    }
        private void addBitmapToCache(String url, String localPath) {
            if (localPath != null) {
                synchronized (sHardBitmapCache) {
                    sHardBitmapCache.put(url, localPath);
//		    			System.out.println("[url] - "+url+ ", [localPath] - "+localPath);
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
//		        sHardBitmapCache.clear();
//		        sSoftBitmapCache.clear();
        }

        /**
         * Allow a new delay before the automatic cache clear is done.
         */
        private void resetPurgeTimer() {
            purgeHandler.removeCallbacks(purger);
            purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
        }

        private void updateResult(String tmpUri, int progress) {
            if (progress <= 99) {
                rightImgProgressBar.setVisibility(View.VISIBLE);
                rightImgProgressIndeterminate.setVisibility(View.GONE);
                rightImgProgressPercent.setVisibility(View.VISIBLE);
                rightImgProgressBar.setProgress(progress);
                if (tmpUri != null)
                    processing.put(tmpUri, "" + progress);
                //
                //								   // updating percentage value
                rightImgProgressPercent.setText(String.valueOf(progress) + "%");
            } else {
                rightImgProgressIndeterminate.setVisibility(View.VISIBLE);/// tmp chk
                rightImgProgressBar.setVisibility(View.INVISIBLE);
                rightImgProgressPercent.setVisibility(TextView.INVISIBLE);
            }
        }

        private void updateResultOld(String tmpUri, int progress) {
            ProgressBar tmProgressBar = mediaUploadProgressBar.get(tmpUri);
            if (tmProgressBar != null) {
                if (progress <= 99) {

                    tmProgressBar.setVisibility(View.VISIBLE);
                    rightImgProgressPercent.setVisibility(View.VISIBLE);
                    tmProgressBar.setProgress(progress);
                    if (tmpUri != null)
                        processing.put(tmpUri, "" + progress);
                    //
                    //								   // updating percentage value
                    rightImgProgressPercent.setText(String.valueOf(progress) + "%");
                } else {
                    tmProgressBar.setVisibility(View.INVISIBLE);
                    rightImgProgressPercent.setVisibility(TextView.INVISIBLE);
                }
            }
        }

        public class MediaFileUpload extends AsyncTask<String, Integer, String> {
            String localUri = null;
            public WeakReference<ProgressBar> progressbar;

            MediaFileUpload(String key, String url) {
                localUri = url;
                ChatDBWrapper.getInstance().updateMediaLoadingStarted(key);
            }

            //		    	MediaFileUpload(String key,String url,ProgressBar pb){
//		    		localUri = url;
//		    		ChatDBWrapper.getInstance().updateMediaLoadingStarted(key);
//		    		progressbar = new WeakReference<ProgressBar>(pb);
//		    	}
            @Override
            protected void onPreExecute() {
                //				rTDialog = new RTDialog(CompleteProfileActivity.this, null,
                //						getString(R.string.updating));
                //
                //			rTDialog.show();
                Cursor cursor1 = null;
                if (isBulletinBroadcast)
                    cursor1 = ChatDBWrapper.getInstance().getUserChatList(chatName, CHAT_LIST_BULLETIN);
                else
                    cursor1 = ChatDBWrapper.getInstance().getUserChatList(chatName, CHAT_LIST_NORMAL);
                swapCursor(cursor1);
                notifyDataSetChanged();
                super.onPreExecute();
            }

            //				 @Override
            protected void onProgressUpdate(Integer... progress) {
//					  if(localUri!=null)
//						  processing.put(localUri, ""+progress[0]);
                if (localUri != null && progress[1] != 0)
                    processing.put(localUri, "" + Integer.parseInt("" + (int) ((progress[0] * 100) / progress[1])));
//					  if(viewUpdateListener!=null)
//						  viewUpdateListener.

//						  updateListener(localUri,progress[0]);

//					  notifyDataSetChanged();
//					  if(progress[0]<=99){
//						  rightImgProgressBar.setVisibility(View.VISIBLE);
////						  rightImgProgressPercent.setVisibility(View.VISIBLE);
//						  rightImgProgressBar.setProgress(progress[0]);
//						  if(localUri!=null)
//							  processing.put(localUri, ""+progress[0]);
//						  //
//						  //								   // updating percentage value
////						  rightImgProgressPercent.setText(String.valueOf(progress[0]) + "%");
//					  }else{
//						  rightImgProgressBar.setVisibility(View.INVISIBLE);
////						  rightImgProgressPercent.setVisibility(TextView.INVISIBLE);
//					  }

//					  Log.alltime(TAG, "onProgressUpdate count: "+progress[0]);
                super.onProgressUpdate(progress);
            }

            @Override
            protected String doInBackground(String... urls) {
                //url[1] - packetID
                //http://54.164.75.109:8080/rtMediaServer/get/1_1_5_E_I_I3_dyj5a3elg1.jpg
                //			String url = Constants.PIC_SEP + "http://54.164.75.109:8080/rtMediaServer/get/";
                //			String url = "http://78.129.179.96:8080/rtMediaServer/get/";
                String url = Constants.media_get_url;
                final byte PACKET_ID_INDEX = 1;
                final byte THUMB_INDEX = 2;
                final byte MESSAGE_TYPE_INDEX = 3;
                final byte CAPTION_INDEX = 4;
                try {
//						String fileId = Utilities.createMediaID(urls[0], Constants.ID_FOR_UPDATE_PROFILE);
                    int retry = 0;
                    while (true) {
                        String postUrl = Constants.media_post_url;
                        String filePath = urls[0];
                        String fileId = null;
                        try {
//								 ChatDBWrapper.getInstance().updateMediaLoadingStarted(urls[1]);
                            fileId = postMedia(postUrl, filePath);
                            String actualFileName = null;
                            if (fileId != null && !fileId.equals("")) {
                                final JSONObject jsonObject = new JSONObject(fileId);
                                final String status = jsonObject.getString("status");
                                if (status.equals("error")) {
                                    retry++;
                                    wait(5000);
                                } else if (status.trim().equalsIgnoreCase("success")) {
                                    retry++;
                                    fileId = jsonObject.getString("fileId");
                                    if (messageService != null) {
//											XMPPMessageType mediaType = XMPPMessageType.fromString(urls[3]);
                                        int msgType = Integer.parseInt(urls[MESSAGE_TYPE_INDEX]);
                                        Log.d(TAG, "mediaType: " + msgType);
                                        if (msgType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()) {
                                            String newFileType = ".doc";
                                            if (filePath != null && filePath.toLowerCase().endsWith("docx"))
                                                newFileType = ".docx";
                                            if (filePath != null && filePath.contains("/")) {
                                                actualFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                                            }
//												 if (iChatPref.isBroadCast(chatName)){
//													 ArrayList<String> usersList = iChatPref.getBroadCastUsersList(chatName);
//													 for(String person: usersList){
////														 messageService.sendMediaMessage(person, "", url + fileId + newFileType, urls[2], XMPPMessageType.atMeXmppMessageTypeDoc);
//														 messageService.sendMediaURL(person, "", urls[1],urls[4], url + fileId + newFileType, urls[2], XMPPMessageType.atMeXmppMessageTypeDoc);
//													 }
//
//												 }else
                                            messageService.sendMediaURL(chatName, "", urls[PACKET_ID_INDEX], urls[CAPTION_INDEX], actualFileName, url + fileId + newFileType, urls[THUMB_INDEX], XMPPMessageType.atMeXmppMessageTypeDoc);
                                            ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaURL(urls[PACKET_ID_INDEX], url + fileId + newFileType);
                                            Log.d(TAG, "-----------" + (url + fileId + newFileType));
                                        } else if (msgType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()) {
                                            if (filePath != null && filePath.contains("/")) {
                                                actualFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                                            }
//												 if (iChatPref.isBroadCast(chatName)){
//													 ArrayList<String> usersList = iChatPref.getBroadCastUsersList(chatName);
//													 for(String person: usersList){
////														 messageService.sendMediaMessage(person, "", url + fileId + ".pdf", urls[2], XMPPMessageType.atMeXmppMessageTypePdf);
//														 messageService.sendMediaURL(person, "", urls[1],urls[4], url + fileId + ".pdf", urls[2], XMPPMessageType.atMeXmppMessageTypePdf);
//													 }
//												 }else
                                            messageService.sendMediaURL(chatName, "", urls[PACKET_ID_INDEX], urls[CAPTION_INDEX], actualFileName, url + fileId + ".pdf", urls[THUMB_INDEX], XMPPMessageType.atMeXmppMessageTypePdf);
                                            ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaURL(urls[PACKET_ID_INDEX], url + fileId + ".pdf");
                                            Log.d(TAG, "-----------" + (url + fileId + ".pdf"));
                                        } else if (msgType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()) {
                                            String newFileType = ".xls";
                                            if (filePath != null && filePath.toLowerCase().endsWith("xlsx"))
                                                newFileType = ".xlsx";
                                            if (filePath != null && filePath.contains("/")) {
                                                actualFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                                            }
//												 if (iChatPref.isBroadCast(chatName)){
//													 ArrayList<String> usersList = iChatPref.getBroadCastUsersList(chatName);
//													 for(String person: usersList){
//														 messageService.sendMediaURL(person, "", urls[1],urls[4], url + fileId + newFileType, urls[2], XMPPMessageType.atMeXmppMessageTypeXLS);
//													 }
//												 }else
                                            messageService.sendMediaURL(chatName, "", urls[PACKET_ID_INDEX], urls[CAPTION_INDEX], actualFileName, url + fileId + newFileType, urls[THUMB_INDEX], XMPPMessageType.atMeXmppMessageTypeXLS);
                                            ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaURL(urls[PACKET_ID_INDEX], url + fileId + newFileType);
                                            Log.d(TAG, "-----------" + (url + fileId + newFileType));
                                        } else if (msgType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                                            String newFileType = ".ppt";
                                            if (filePath != null && filePath.toLowerCase().endsWith("pptx"))
                                                newFileType = ".pptx";
                                            if (filePath != null && filePath.contains("/")) {
                                                actualFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                                            }
//												 if (iChatPref.isBroadCast(chatName)){
//													 ArrayList<String> usersList = iChatPref.getBroadCastUsersList(chatName);
//													 for(String person: usersList){
//														 messageService.sendMediaURL(person, "", urls[1],urls[4], url + fileId + newFileType, urls[2], XMPPMessageType.atMeXmppMessageTypePPT);
//													 }
//												 }else
                                            messageService.sendMediaURL(chatName, "", urls[PACKET_ID_INDEX], urls[CAPTION_INDEX], actualFileName, url + fileId + newFileType, urls[THUMB_INDEX], XMPPMessageType.atMeXmppMessageTypePPT);
                                            ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaURL(urls[PACKET_ID_INDEX], url + fileId + newFileType);
                                            Log.d(TAG, "-----------" + (url + fileId + newFileType));
                                        } else if (msgType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
//												 if (iChatPref.isBroadCast(chatName)){
//													 ArrayList<String> usersList = iChatPref.getBroadCastUsersList(chatName);
//													 for(String person: usersList){
////														 messageService.sendMediaMessage(person, "", url + fileId + MediaEngine.MEDIA_TYPE, urls[2], XMPPMessageType.atMeXmppMessageTypeAudio);
//														 messageService.sendMediaURL(person, "", urls[1],urls[4], url + fileId + MediaEngine.MEDIA_TYPE, urls[2], XMPPMessageType.atMeXmppMessageTypeAudio);
//													 }
//												 }else
                                            messageService.sendMediaURL(chatName, "", urls[PACKET_ID_INDEX], urls[CAPTION_INDEX], filePath, url + fileId + MediaEngine.MEDIA_TYPE, urls[THUMB_INDEX], XMPPMessageType.atMeXmppMessageTypeAudio);
                                            ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaURL(urls[PACKET_ID_INDEX], url + fileId + MediaEngine.MEDIA_TYPE);
                                            Log.d(TAG, "-----------" + (url + fileId + MediaEngine.MEDIA_TYPE));
                                        } else if (msgType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()) {
//											 if (iChatPref.isBroadCast(chatName)){
//												 ArrayList<String> usersList = iChatPref.getBroadCastUsersList(chatName);
//												 for(String person: usersList){
////													 messageService.sendMediaMessage(person, "", url + fileId + ".mp4",urls[2],XMPPMessageType.atMeXmppMessageTypeImage);
//													 messageService.sendMediaURL(person, "", urls[1],urls[4], url + fileId + ".mp4", urls[2], XMPPMessageType.atMeXmppMessageTypeVideo);
//												 }
//											 }else
                                            //Check here for file ID and put desired extension -
                                            String ext = Utilities.getVideoFileExtensionFromFileID(fileId);
                                            if (ext == null)
                                                ext = ".mp4";
                                            messageService.sendMediaURL(chatName, "", urls[PACKET_ID_INDEX], urls[CAPTION_INDEX], null, url + fileId + ext, urls[THUMB_INDEX], XMPPMessageType.atMeXmppMessageTypeVideo);
                                            ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaURL(urls[PACKET_ID_INDEX], url + fileId + ext);
                                            //Deliberately putting 3gp as default recording is 3gp
//												messageService.sendMediaURL(chatName, "", urls[PACKET_ID_INDEX],urls[CAPTION_INDEX], null, url + fileId + ".3gp", urls[THUMB_INDEX], XMPPMessageType.atMeXmppMessageTypeVideo);
//												ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaURL(urls[PACKET_ID_INDEX], url + fileId + ".3gp");
//												Log.d(TAG,"-----------"+(url + fileId + ".m3gp"));
                                        } else {
                                            String newFileType = ".jpg";
                                            if (filePath != null && filePath.toLowerCase().endsWith("png"))
                                                newFileType = ".png";
                                            else if (filePath != null && filePath.toLowerCase().endsWith("jpeg"))
                                                newFileType = ".png";
//											 if (iChatPref.isBroadCast(chatName)){
//													 ArrayList<String> usersList = iChatPref.getBroadCastUsersList(chatName);
//													 for(String person: usersList){
////														 messageService.sendMediaMessage(person, "", url + fileId + ".jpg",urls[2],XMPPMessageType.atMeXmppMessageTypeImage);
//														 messageService.sendMediaURL(person, "", urls[1],urls[4], url + fileId + ".jpg", urls[2], XMPPMessageType.atMeXmppMessageTypeImage);
//													 }
//												 }else
                                            messageService.sendMediaURL(chatName, "", urls[PACKET_ID_INDEX], urls[CAPTION_INDEX], null, url + fileId + newFileType, urls[2], XMPPMessageType.atMeXmppMessageTypeImage);
                                            ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaURL(urls[PACKET_ID_INDEX], url + fileId + newFileType);
                                            Log.d(TAG, "-----------" + (url + fileId + newFileType));
                                        }
                                        if (filePath != null && !filePath.equals("")) {
                                            android.os.Message refreshMsg = new android.os.Message();
                                            Bundle refreshBundle = new Bundle();
                                            refreshBundle.putString("file_uri", filePath);
                                            refreshMsg.setData(refreshBundle);
                                            galaryRefresh.sendMessage(refreshMsg);
                                        }
                                        return fileId;
                                    }
                                }
                            } else {
                                retry++;
                                wait(5000);
                            }
                        } catch (Exception e) {
//									  e.printStackTrace();
                        }
                        if (retry > 2)
                            return null;

                    }

                    //			}

                } catch (final Exception e) {

                }
                return null;
            }

            public String postMedia(String url, String fileToUpload) throws IOException {
                String responseData = null;
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
//					  httppost.setHeader("RT-APP-KEY", "'15769260:AAAABl26u1EIeTIz'");

                    FileBody data = new FileBody(new File(fileToUpload));
//					  StringBody comment = new StringBody("Filename: " + fileToUpload);
//					  Log.d(TAG,"postMedia body string "+comment.getFilename()+" , "+comment.getCharset());
//					  MultipartEntity reqEntity = new MultipartEntity();
                    final int totalSize = (int) data.getContentLength();
                    AndroidMultiPartEntity reqEntity = new AndroidMultiPartEntity(
                            new ProgressListener() {

                                @Override
                                public void transferred(long num) {
//						    	  Log.d(TAG,"[[[[[[[[ - "+((int) ((num / (float) totalSize) * 100)));
//						        publishProgress(((int) ((num / (float) totalSize) * 100)));
                                    publishProgress((int) num, (int) totalSize);
                                }
                            });

                    FormBodyPart dataBodyPart = new FormBodyPart("data", data);
                    if (fileToUpload.toLowerCase().endsWith(".docx")) {
                        FormBodyPart extBodyPart = new FormBodyPart("ext", new StringBody("docx"));
                        reqEntity.addPart(extBodyPart);
                        saveFileSizeInChatDB(fileToUpload, totalSize);
                    } else if (fileToUpload.toLowerCase().endsWith(".doc")) {
                        FormBodyPart extBodyPart = new FormBodyPart("ext", new StringBody("doc"));
                        reqEntity.addPart(extBodyPart);
                        saveFileSizeInChatDB(fileToUpload, totalSize);
                    } else if (fileToUpload.toLowerCase().endsWith(".pdf")) {
                        FormBodyPart extBodyPart = new FormBodyPart("ext", new StringBody("pdf"));
                        reqEntity.addPart(extBodyPart);
                        saveFileSizeInChatDB(fileToUpload, totalSize);
                    } else if (fileToUpload.toLowerCase().endsWith(".xls")) {
                        FormBodyPart extBodyPart = new FormBodyPart("ext", new StringBody("xls"));
                        reqEntity.addPart(extBodyPart);
                        saveFileSizeInChatDB(fileToUpload, totalSize);
                    } else if (fileToUpload.toLowerCase().endsWith(".xlsx")) {
                        FormBodyPart extBodyPart = new FormBodyPart("ext", new StringBody("xlsx"));
                        reqEntity.addPart(extBodyPart);
                        saveFileSizeInChatDB(fileToUpload, totalSize);
                    } else if (fileToUpload.toLowerCase().endsWith(".ppt")) {
                        FormBodyPart extBodyPart = new FormBodyPart("ext", new StringBody("ppt"));
                        reqEntity.addPart(extBodyPart);
                        saveFileSizeInChatDB(fileToUpload, totalSize);
                    } else if (fileToUpload.toLowerCase().endsWith(".pptx")) {
                        FormBodyPart extBodyPart = new FormBodyPart("ext", new StringBody("pptx"));
                        reqEntity.addPart(extBodyPart);
                        saveFileSizeInChatDB(fileToUpload, totalSize);
                    }
//					  FormBodyPart commentBodyPart = new FormBodyPart("comment", comment);
                    reqEntity.addPart(dataBodyPart);
//					  reqEntity.addPart(commentBodyPart);
                    httppost.setEntity(reqEntity);
                    HttpResponse response = httpclient.execute(httppost);
                    Log.d(TAG, "Status Line: " + response.getStatusLine());
                    HttpEntity resEntity = response.getEntity();
                    responseData = Streams.asString(resEntity.getContent());
                    Log.d(TAG, "ResponseData: " + responseData);
//                    if(responseData != null && responseData.contains("success") && (fileToUpload.endsWith(".jpg") || fileToUpload.endsWith(".jpeg"))){
//                        File file = new File(fileToUpload);
//                        boolean deleted = file.delete();
//                        if(deleted)
//                            System.out.println("[File deleted after sending]");
//                    }
                } catch (Exception e) {
//						e.printStackTrace();
                    processing.remove(url);
                }
                return responseData;
            }

            @Override
            protected void onPostExecute(String response) {
                if (localUri != null) {
                    processing.remove(localUri);
                }
                Cursor cursor1 = null;
                if (isBulletinBroadcast) {
                    cursor1 = ChatDBWrapper.getInstance().getUserChatList(chatName, CHAT_LIST_BULLETIN);
                } else {
                    cursor1 = ChatDBWrapper.getInstance().getUserChatList(chatName, CHAT_LIST_NORMAL);
                }
                swapCursor(cursor1);
                notifyDataSetChanged();
                super.onPostExecute(response);
            }
        }

    }

    Handler galaryRefresh = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // android.os.Message msg = handler.obtainMessage();
            Bundle bundle = msg.getData();
//			Log.d("ChatListAdapter",
//					"Timing by message " + bundle.getString("Timing"));
            String uriPath = bundle.getString("file_uri");
            ((ChatListScreen) context).sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(uriPath))));

        }
    };

    private void updateMediaLengthInDb(String key, String length) {
        try {
            ChatDBWrapper.getInstance().updateMediaLength(key, length);
            Cursor cursor1 = null;
            if (isBulletinBroadcast)
                cursor1 = ChatDBWrapper.getInstance().getUserChatList(chatName, CHAT_LIST_BULLETIN);
            else
                cursor1 = ChatDBWrapper.getInstance().getUserChatList(chatName, CHAT_LIST_NORMAL);
            swapCursor(cursor1);
            notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    private void saveFileSizeInChatDB(String fileUrl, int fileSize) {
        Log.d(TAG, "saveFileSizeInChatDB : " + fileUrl + " , " + fileSize);
        String sizeInKB = "0KB";
        if (fileSize > 0) {

            float tmpSize = fileSize;
            if (fileSize < 1024) {
                sizeInKB = fileSize + " BYTES";
            } else {
                tmpSize = tmpSize / 1024;
                if (tmpSize >= 1024) {
                    tmpSize = tmpSize / 1024;
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    double dd2dec = new Double(df.format(tmpSize)).doubleValue();
                    sizeInKB = dd2dec + " MB";
                } else if (tmpSize > 0) {
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);
                    double dd2dec = new Double(df.format(tmpSize)).doubleValue();
                    sizeInKB = dd2dec + " KB";
                } else
                    sizeInKB = fileSize + " BYTES";
            }
        }
        ChatDBWrapper.getInstance().updateFileDocSize(fileUrl, sizeInKB);
    }

    public void stopPlaying() {
        if (myVoicePlayer != null) {
            myVoicePlayer.reset();
            myVoicePlayer.clear();
        }
    }

    public void refreshUI() {
        swapCursor(null);
    }

    public void clearMap() {
//	    	 processing.clear();
//	    	 progressTimer = null;
    }

    public static String TAG = "ChatListAdapter";
    private ChatService messageService;
    private LayoutInflater mInflater;
    public RTMediaPlayer myVoicePlayer;
    int globalSeekBarValue = 0;
    int globalSeekBarMaxValue = 0;
    String audioPlayerKey = null;
    public String playingVoicePath;
    private static Context context;
    public ExecutorService executorService;
    static Handler handler;
    long currentAudioPlayCounter = 0;
    int layout;
    static Timer progressTimer;
    static ProgressTask progressTask;
    public int listenerState;
    private Calendar calander;
    ChatCountListener refreshListener;
    boolean isBulletinBroadcast;
    private final String[] MONTH_ARRAY = new String[]{"JANUARY", "FEBRUARY",
            "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER",
            "OCTOBER", "NOVEMBER", "DECEMBER"};
    String myUserName;
    public static Bundle playerBundle = new Bundle();
    private static HashMap<String, Boolean> checkedTagMap = new HashMap<String, Boolean>();
    public static HashSet<String> cacheKeys = new HashSet<String>();
    //    private static HashMap<String, MediaFileUpload> fileUploadMap = new HashMap<String, MediaFileUpload>();
    private boolean isEditableChat = false;
    private boolean isGroupChat = false;
    private boolean isSharedIDMessage = false;
    private boolean isSharedIDAdmin = false;
    private boolean isBroadCastChat = false;
    ChatListAdapter chatAdaptor;
    String timeSpent;
    String chatName;
    HashMap<String, String> colors = new HashMap<String, String>();
    public static HashMap<String, String> processing = new HashMap<String, String>();
    public static HashMap<String, String> processingMap = new HashMap<String, String>();
    static HashMap<String, ProgressBar> mediaUploadProgressBar = new HashMap<String, ProgressBar>();
    private OnChatEditInterFace iEditListener = null;
    public static final byte CHAT_LIST_NORMAL = 1;
    public static final byte CHAT_LIST_BULLETIN = 2;
    SharedPrefManager iChatPref;
    String[] colorsArray = {"#E01313", "#4C44BE", "#095C17", "#9A1C03", "#B76609", "#088682", "#236A4D", "#0B5687", "#4D535F", "#4D0613", "#778400", "#94860C", "#323022"};
    static int colorIndex;

    Cursor cursor = null;

    public ChatListAdapter(Context context1, int i, Cursor cursor, String as[],
                           int ai[], int j, String chatName, OnChatEditInterFace mEditListener) {
        super(context1, i, cursor, as, ai, j);
        progressTimer = null;
        progressTask = null;
        this.chatName = chatName;
        this.cursor = cursor;
        this.iEditListener = mEditListener;
        iChatPref = SharedPrefManager.getInstance();
        myUserName = iChatPref.getUserName();
        isBroadCastChat = iChatPref.isBroadCast(chatName);
        isGroupChat = iChatPref.isGroupChat(chatName);
        isSharedIDMessage = iChatPref.isSharedIDContact(chatName);
        if (!SharedPrefManager.getInstance().isDomainAdminORSubAdmin() && HomeScreen.isAdminFromSharedID(chatName, SharedPrefManager.getInstance().getUserName()))
            isSharedIDAdmin = true;

        colors = new HashMap<String, String>();
        colorIndex = 0;
//		if(ichatPref.isGroupChat(chatName)){
//			int index = 0;
//			for(String user :ichatPref.getGroupUsersList(chatName)){
//				colors.put(user, colorsArray[index]);
//				index++;
//				if(index>colorsArray.length-1)
//					index = 0;
//			}
//			}
        executorService = Executors.newFixedThreadPool(2);
        context = context1;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        executorService = Executors.newFixedThreadPool(5);
        layout = i;

        calander = Calendar.getInstance(TimeZone.getDefault());
        chatAdaptor = this;

        playerBundle.clear();
        myVoicePlayer = new RTMediaPlayer();
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // android.os.Message msg = handler.obtainMessage();
                Bundle bundle = msg.getData();
//				Log.d("ChatListAdapter",
//						"Timing by message " + bundle.getString("Timing"));
//				timeSpent = bundle.getString("Timing");
                ((ChatListScreen) context).listeningStatusSendHandler.sendEmptyMessage(0);

            }
        };
        mDrawableBuilder = TextDrawable.builder()
                .beginConfig().toUpperCase()
                .endConfig()
                .round();
        // handler.post(new Runnable() {
        //
        // @Override
        // public void run() {
        // android.os.Message msg = handler.obtainMessage();
        // Bundle bundle = msg.getData();
        // Log.d("ChatListAdapter",
        // "Timing by message "+bundle.getString("Timing"));
        // ((ChatListScreen)context).refreshAdpter();
        // }
        // });
    }

    public Cursor getCursor() {
        return this.cursor;
    }

    public void setChatService(ChatService messageService) {
        this.messageService = messageService;
    }

    public void setRefreshListener(ChatCountListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    public void chatListBulletin(boolean bool) {
        this.isBulletinBroadcast = bool;
    }

    public void setEditableChat(boolean isEdit) {
        this.isEditableChat = isEdit;
    }

    public boolean isEditableChat() {
        return isEditableChat;
    }

    public void setGroupOrMultiUserChat(boolean isMultiple) {
        this.isGroupChat = isMultiple;
    }

    public HashMap<String, Boolean> getSelectedItems() {
        return checkedTagMap;
    }

    public void removeSelectedItems() {
        if (checkedTagMap != null)
            checkedTagMap.clear();
    }

    public int totalItemChecked() {
        int totalItems = 0;
        if (checkedTagMap != null && !checkedTagMap.isEmpty())
            for (String itemKey : checkedTagMap.keySet()) {
                if (checkedTagMap.get(itemKey))
                    totalItems++;
            }
        return totalItems;
    }

    public String singleCheckedKey() {
        String totalItems = "";
        if (checkedTagMap != null && !checkedTagMap.isEmpty())
            for (String itemKey : checkedTagMap.keySet()) {
                if (checkedTagMap.get(itemKey))
                    return itemKey;
            }
        return totalItems;
    }

    private int convertPixelToDip(int paddingPixel) {
        float density = context.getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        return paddingDp;
    }

    private void displayImage(ImageView imageview, String s, boolean flag) {
        android.graphics.Bitmap bitmap = SuperChatApplication
                .getBitmapFromMemCache(s);
        if (bitmap != null) {
            imageview.setImageBitmap(bitmap);
        } else {
            imageview.setImageResource(R.drawable.avatar);
        }
        if (!flag && bitmap == null) {
            PhotoToLoad phototoload = new PhotoToLoad(imageview, s);
            executorService.execute(new ImageLoaderWorker(phototoload));
        }
    }

    Handler progressUpdateHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            notifyDataSetChanged();
//			Bundle msgBundle = msg.getData();
//			String urlKey;
//			if(msgBundle!=null){
//				urlKey = msgBundle.getString("URL_KEY");
//				int progressValue = msgBundle.getInt("PROGRESS_VALUE");
//				if(urlKey!=null && !urlKey.equals("")){
//					ProgressBar taskProgressBar = mediaUploadProgressBar.get(progressValue);
//					if(taskProgressBar!=null && progressValue>0 && progressValue<98)
//						taskProgressBar.setProgress(progressValue);
//					notifyDataSetChanged();
//				}
//			}
            super.handleMessage(msg);
        }
    };

    class ProgressTask extends TimerTask {
        String urlKey;

        //	    ProgressBar taskProgressBar;
        public ProgressTask(String urlKey) {
            this.urlKey = urlKey;
//	        taskProgressBar = mediaUploadProgressBar.get(urlKey);
        }

        @Override
        public void run() {

//	    	if(urlKey!=null){
            String progress = null;
            int progressValue = 0;
            for (String key : processing.keySet()) {
                if (key == null)
                    continue;
                progress = processing.get(key);
                if (progress != null && !progress.equals(""))
                    try {
                        progressValue = Integer.parseInt(progress);
                    } catch (NumberFormatException ex) {

                    }
                if (progressValue > 0 && progressValue < 100)
                    break;
            }
            if (progressValue <= 0 || progressValue > 99) {
                if (progressValue > 99)
                    progressUpdateHandler.sendEmptyMessage(0);
                cancel();
                progressTask = null;
                progressTimer = null;
                return;
            }

//		    	android.os.Message androidMsg = new android.os.Message();
//		    	Bundle data = new Bundle();
//		    	data.putString("URL_KEY", urlKey);
//		    	data.putInt("PROGRESS_VALUE", progressValue);
//		    	androidMsg.setData(data);
//		    	progressUpdateHandler.sendMessage(androidMsg);
            progressUpdateHandler.sendEmptyMessage(0);
//	    	}else
//	    		cancel();
        }
    }

    //class ProgressTask extends TimerTask  {
//    String urlKey;
//    ProgressBar taskProgressBar;
//    public ProgressTask(String urlKey) {
//        this.urlKey = urlKey;
//        taskProgressBar = mediaUploadProgressBar.get(urlKey);
//    }
//
//    @Override
//    public void run() {
//
//    	if(urlKey!=null && taskProgressBar!=null){
//    		String progress = ((String)processing.get(urlKey));
//    		int progressValue = 0;
//			if(progress!=null && !progress.equals(""))
//				try{
//					progressValue = Integer.parseInt(progress);
//				}catch(NumberFormatException ex){
//
//				}
//			if(progressValue<=0 || progressValue>=98){
//				cancel();
//				return;
//			}
//
//	    	android.os.Message androidMsg = new android.os.Message();
//	    	Bundle data = new Bundle();
//	    	data.putString("URL_KEY", urlKey);
//	    	data.putInt("PROGRESS_VALUE", progressValue);
//	    	androidMsg.setData(data);
//	    	progressUpdateHandler.sendMessage(androidMsg);
//    	}else
//    		cancel();
//    }
//}

    public void showDate(long yourmilliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
        Date resultdate = new Date(yourmilliseconds);
        System.out.println("Date => " + sdf.format(resultdate));
    }

    @Override
    public void bindView(View view, Context context1, Cursor cursor) {
        final ViewHolder viewholder = (ViewHolder) view.getTag();
        viewholder.parentView = view;
        viewholder.key = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID));
        viewholder.groupMsgSenderName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD));
        if (viewholder.groupMsgSenderName == null)
            viewholder.groupMsgSenderName = "";
        viewholder.captionTagMsg = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MEDIA_CAPTION_TAG));
        viewholder.locationMsg = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_LOCATION));

        viewholder.seenState = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.SEEN_FIELD));
        viewholder.messageType = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD));
        viewholder.userName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD));
        viewholder.receiverName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.TO_USER_FIELD));

        viewholder.message = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGEINFO_FIELD));
        viewholder.mediaUrl = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD));
        viewholder.audioLength = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LENGTH));
        String url = viewholder.mediaUrl;
        viewholder.mediaLocalPath = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
        viewholder.mediaThumb = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_THUMB_FIELD));
        viewholder.time = cursor.getLong(cursor.getColumnIndex(ChatDBConstants.LAST_UPDATE_FIELD));
        viewholder.isDateShow = "1".equals(cursor.getString(cursor.getColumnIndex(ChatDBConstants.IS_DATE_CHANGED_FIELD)));
        viewholder.totalGroupUsers = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.TOTAL_USER_COUNT_FIELD));
        viewholder.totalGroupReadUsers = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.READ_USER_COUNT_FIELD));
        viewholder.isTimeLineMessage = false;

        if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeSpecialMessage.ordinal())// i.e. 31
            viewholder.isDateShow = true;


//        System.out.println("[CONTACT_NAMES_FIELD] == "+cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD)));
//        System.out.println("[TO] == " + viewholder.receiverName);
//        System.out.println("[FROM] == " + viewholder.userName);
//        System.out.println("[groupMsgSenderName] == " + viewholder.groupMsgSenderName);

        if (viewholder.totalGroupUsers == 0) {
            String memebers = SharedPrefManager.getInstance().getGroupMemberCount(chatName);
            if (memebers != null && !memebers.equals("")) {
                try {
                    viewholder.totalGroupUsers = Integer.parseInt(memebers);
                } catch (Exception e) {

                }
            }
        }
        viewholder.playRecieverTimeText.setVisibility(View.GONE);
//		viewholder.playSenderTimeText.setVisibility(View.GONE);
        viewholder.playRecieverMaxTimeText.setVisibility(View.GONE);
        viewholder.playSenderMaxTimeText.setVisibility(View.GONE);
        viewholder.senderAudioTagView.setVisibility(TextView.GONE);
        if (viewholder.mediaLocalPath != null && !viewholder.mediaLocalPath.equals("")) {
            url = viewholder.mediaLocalPath;
        }
        calander.setTimeInMillis(viewholder.time);
//        System.out.println("[Time ] "+viewholder.time);
//        showDate(viewholder.time);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
        String msgTime = format.format(calander.getTime());

        final int date = calander.get(Calendar.DAY_OF_MONTH);
        final int month = calander.get(Calendar.MONTH);
        final int year = calander.get(Calendar.YEAR);
//        boolean bulletin_welcome = false;

		/* For checking it is voice note or other message. */
        if (viewholder.messageType != XMPPMessageType.atMeXmppMessageTypePdf.ordinal()
                && viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()
                && viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()) {
            viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
            viewholder.voiceSenderLayout.setVisibility(View.GONE);
        }

        if (viewholder.userName.equals(myUserName)) {
            if (!iChatPref.isDomainAdminORSubAdmin()) {
                if (viewholder.message.startsWith("Welcome to ") && viewholder.message.endsWith("bulletin board ")) {
//					viewholder.dateLayout.setVisibility(View.VISIBLE);
//					viewholder.dateText.setText(viewholder.message);
//                    bulletin_welcome = true;
                }
            }
        }

		/* Its for displaying middle text message for date change */
        if (viewholder.isDateShow && viewholder.time != 0) {
            String tmp = "";
            if (viewholder.message != null && (viewholder.message.contains("added")
                    || viewholder.message.contains("group created.")
                    || viewholder.message.contains("created by ")
                    || viewholder.message.endsWith("bulletin board ")))
                tmp = " \n " + viewholder.message;
            viewholder.leftRightCompositeView.setVisibility(View.VISIBLE);
            viewholder.dateLayout.setVisibility(View.VISIBLE);
            viewholder.leftPersonPic.setVisibility(View.GONE);
            viewholder.receiverLayout.setVisibility(View.GONE);
            viewholder.dateLayout.setVisibility(View.VISIBLE);
            viewholder.leftPersonPic.setVisibility(View.GONE);
            viewholder.senderLayout.setVisibility(View.GONE);
            viewholder.dateText.setText(date + " " + MONTH_ARRAY[month] + " " + year + tmp);
            viewholder.isTimeLineMessage = true;
        } else {
            if (isBroadCastChat && (!viewholder.userName.equals(myUserName) || viewholder.message.contains("updated broad"))) {
                viewholder.leftRightCompositeView.setVisibility(View.GONE);
                viewholder.receiverMsgText.setVisibility(TextView.GONE);
                viewholder.recieveTagView.setVisibility(TextView.GONE);
                viewholder.receiverLayout.setVisibility(View.GONE);
                viewholder.dateLayout.setVisibility(View.VISIBLE);
                viewholder.leftPersonPic.setVisibility(View.GONE);
                viewholder.senderLayout.setVisibility(View.GONE);
                viewholder.dateText.setText(viewholder.message);
                viewholder.message = "";
            } else if (viewholder.groupMsgSenderName.equals("") && !viewholder.userName.equals(myUserName) && isGroupChat) {
                viewholder.leftRightCompositeView.setVisibility(View.GONE);
                viewholder.dateLayout.setVisibility(View.VISIBLE);
                viewholder.leftPersonPic.setVisibility(View.GONE);
                viewholder.dateText.setText(viewholder.message);
                viewholder.message = "";
                viewholder.isTimeLineMessage = true;
            } else {
                viewholder.leftRightCompositeView.setVisibility(View.VISIBLE);
                viewholder.dateLayout.setVisibility(View.GONE);
            }

        }
        int progressValue = 0;

        // My sent messages
        if (viewholder.userName.equals(myUserName)) {
            viewholder.leftFileLayout.setVisibility(View.GONE);
            viewholder.leftFileTypeView.setVisibility(View.GONE);
            viewholder.leftPersonPicLayout.setVisibility(View.GONE);
            String progress = viewholder.getProcessingForURL(viewholder.mediaLocalPath);
            if (progress != null && !progress.equals("")) {
                try {
                    progressValue = Integer.parseInt(progress);
                } catch (NumberFormatException ex) {

                }
            }
            if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()
                    || viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()
                    || viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()
                    || viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                viewholder.rightFileLayout.setVisibility(View.VISIBLE);
                viewholder.rightFileTypeView.setVisibility(View.VISIBLE);
                String actualFileName = "unknown file";
                if (viewholder.mediaLocalPath != null && !viewholder.mediaLocalPath.equals("")) {
                    actualFileName = viewholder.mediaLocalPath.substring(viewholder.mediaLocalPath.lastIndexOf("/") + 1);
                }
                viewholder.sDateLayout.setVisibility(View.GONE);
                viewholder.contactLayout.setVisibility(View.GONE);
                viewholder.pollLayout.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
                viewholder.sendTagView.setVisibility(TextView.GONE);
                viewholder.rightImgProgressBar.setVisibility(View.GONE);
                viewholder.rightImgProgressIndeterminate.setVisibility(View.GONE);
//				viewholder.sendTagView.setText(actualFileName);
                if (viewholder.captionTagMsg != null)
                    ((TextView) viewholder.rightFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                else
                    ((TextView) viewholder.rightFileLayout.findViewById(R.id.id_file_name)).setText(actualFileName);
                if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal())
                    viewholder.rightFileTypeView.setText(R.string.pdf);
                else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal())
                    viewholder.rightFileTypeView.setText(R.string.doc);
                else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal())
                    viewholder.rightFileTypeView.setText(R.string.xls);
                else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal())
                    viewholder.rightFileTypeView.setText(R.string.ppt);
                if (viewholder.audioLength != null)
                    ((TextView) viewholder.rightFileLayout.findViewById(R.id.id_file_size)).setText(viewholder.audioLength);
                else
                    ((TextView) viewholder.rightFileLayout.findViewById(R.id.id_file_size)).setText("");
                if (viewholder.mediaUrl != null && !viewholder.mediaUrl.startsWith("http")) {
                    if (progressValue > 1 && progressValue < 100) {
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.VISIBLE);
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.VISIBLE);
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader)).setProgress(progressValue);
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.file_loading_percent)).setText(String.valueOf(progress) + "%");
                    } else {
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.VISIBLE);
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.INVISIBLE);
                    }
                } else {
                    ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.GONE);
                    ((TextView) viewholder.rightFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                    ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                }
            } else if (viewholder.captionTagMsg == null || viewholder.captionTagMsg.equals("")) {
                viewholder.rightFileLayout.setVisibility(View.GONE);
                viewholder.rightFileTypeView.setVisibility(View.GONE);
                viewholder.sendTagView.setVisibility(TextView.GONE);
                viewholder.senderAudioTagView.setVisibility(TextView.GONE);
            } else {
                viewholder.rightFileLayout.setVisibility(View.GONE);
                viewholder.rightFileTypeView.setVisibility(View.GONE);
                if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal()) {
                    viewholder.sendTagView.setVisibility(View.GONE);
                    viewholder.senderMsgText.setVisibility(View.GONE);
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                    viewholder.contactLayout.setVisibility(View.VISIBLE);
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal()) {
                    viewholder.sendTagView.setVisibility(View.GONE);
                    viewholder.senderMsgText.setVisibility(View.GONE);
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                    viewholder.pollLayout.setVisibility(View.VISIBLE);
                } else {
                    if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                        viewholder.contactLayout.setVisibility(View.GONE);
                        viewholder.pollLayout.setVisibility(View.GONE);
                        viewholder.senderMsgText.setVisibility(View.GONE);
                        viewholder.sendTagView.setVisibility(View.GONE);
                        if (viewholder.captionTagMsg != null) {
                            viewholder.senderAudioTagView.setVisibility(TextView.VISIBLE);
                            viewholder.senderAudioTagView.setText(viewholder.captionTagMsg);
                        } else
                            viewholder.senderAudioTagView.setVisibility(TextView.GONE);
                    } else if (viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeContact.ordinal()
                            && viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeLocation.ordinal()) {
                        viewholder.contactLayout.setVisibility(View.GONE);
                        viewholder.pollLayout.setVisibility(View.GONE);
                        viewholder.senderMsgText.setVisibility(View.GONE);
                        viewholder.sendTagView.setVisibility(TextView.VISIBLE);
                        viewholder.sendTagView.setText(viewholder.captionTagMsg);
                    }
                }
            }
            //Add here code for adding downlaod to the sender media.
            if (viewholder.mediaLocalPath == null && url != null && url.startsWith("http://")) {
                if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()) {
                    ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setBackgroundResource(R.drawable.pdf);
                    if (viewholder.captionTagMsg != null)
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                    if (viewholder.getProcessingForURL(url) == null) {
                        Log.d("ChatListAdapter", "<<   started for - " + url);
                        Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, url};
                        viewholder.download(url, viewholder.messageType, viewholder.sendImgView, viewholder.progressbar, params);
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()) {
                    ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setBackgroundResource(R.drawable.docs);
                    if (viewholder.captionTagMsg != null)
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                    if (viewholder.getProcessingForURL(url) == null) {
                        Log.d("ChatListAdapter", "<<   started for - " + url);
                        Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, url};
                        viewholder.download(url, viewholder.messageType, viewholder.sendImgView, viewholder.progressbar, params);
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()) {
                    ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setBackgroundResource(R.drawable.xls);
                    if (viewholder.captionTagMsg != null)
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                    if (viewholder.getProcessingForURL(url) == null) {
                        Log.d("ChatListAdapter", "<<   started for - " + url);
                        Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, url};
                        viewholder.download(url, viewholder.messageType, viewholder.sendImgView, viewholder.progressbar, params);
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                    ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setBackgroundResource(R.drawable.ppt);
                    if (viewholder.captionTagMsg != null)
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                    if (viewholder.getProcessingForURL(url) == null) {
                        Log.d("ChatListAdapter", "<<   started for - " + url);
                        Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, url};
                        viewholder.download(url, viewholder.messageType, viewholder.sendImgView, viewholder.progressbar, params);
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                    ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setVisibility(View.GONE);
                    viewholder.sendImgView.setVisibility(View.GONE);
                    if (viewholder.getProcessingForURL(url) == null) {
                        Log.d("ChatListAdapter", "media url found for download " + url);
                        Object[] params = new Object[]{this,
                                viewholder.key, cursor,
                                viewholder.voiceLoadingPercent, url};
                        viewholder.download(url, viewholder.messageType,
                                viewholder.playSenderView,
                                viewholder.voiceDownloadingBar, params);
//                    ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar_indeterminate)).setVisibility(View.VISIBLE);
                    }
                    ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar)).setVisibility(View.GONE);
//                    if (viewholder.getProcessingForURL(viewholder.mediaLocalPath) == null) {
//                        processing.put(viewholder.mediaLocalPath, "0");
//                        viewholder.uploadMedia(viewholder.mediaLocalPath, viewholder.messageType);//XMPPMessageType.atMeXmppMessageTypeImage);//viewholder.mediaLocalPath, viewholder.key,viewholder.mediaThumb,XMPPMessageType.atMeXmppMessageTypeImage);
//                    }

                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()) {
                    viewholder.voiceSenderLayout.setVisibility(View.GONE);
                    if (viewholder.getProcessingForURL(url) == null) {
                        Object[] params = new Object[]{this,
                                viewholder.key, cursor,
                                viewholder.progressPercent, url};
                        viewholder.download(url, viewholder.messageType,
                                viewholder.sendImgView,
                                viewholder.progressbar, params);
                    }
                    android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(url);
                    if (bitmap != null) {
                        viewholder.sendImgView.setImageBitmap(bitmap);
                    } else {
                        Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
                        viewholder.sendImgView.setImageBitmap(tmpBitMap);
                        SuperChatApplication.addBitmapToMemoryCache(url, tmpBitMap);
                    }
                    if (bitmap != null) {
                        viewholder.sendImgView.setImageBitmap(bitmap);
                    } else {
                        Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
                        viewholder.sendImgView.setImageBitmap(tmpBitMap);
                        SuperChatApplication.addBitmapToMemoryCache(url, tmpBitMap);
                    }
                    viewholder.rightImgProgressIndeterminate.setVisibility(View.VISIBLE);
                }
                //Show progress loading and % loading
                if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()
                        || viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()
                        || viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()
                        || viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                    if (progressValue > 1 && progressValue < 100) {
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.VISIBLE);
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.VISIBLE);
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader)).setProgress(progressValue);
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.file_loading_percent)).setText(String.valueOf(progress) + "%");
                    } else {
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.VISIBLE);
                        ((TextView) viewholder.rightFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.rightFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.INVISIBLE);
                    }
                }
            }


//			view.setPadding(convertPixelToDip(25), convertPixelToDip(0),
//					convertPixelToDip(5), 0);

            viewholder.receiverLayout.setVisibility(View.GONE);
//			if (isEditableChat) {
//				viewholder.receiverCheckBox.setVisibility(View.GONE);
//				viewholder.senderCheckBox.setVisibility(View.VISIBLE);
//				boolean isChecked = false;
//				Object obj = checkedTagMap.get(viewholder.key);
//				if (obj != null) {
//					isChecked = checkedTagMap.get(viewholder.key);
//				}
//				viewholder.senderCheckBox.setChecked(isChecked);
//				viewholder.senderCheckBox.setTag(viewholder.key);
//			} else
            {
                viewholder.senderCheckBox.setVisibility(View.GONE);
            }
            viewholder.senderLayout.setVisibility(View.VISIBLE);
            viewholder.senderLayout.setTag("N");
            viewholder.senderLayout.setOnLongClickListener(viewholder.onLongPressListener);
            viewholder.contactLayout.setVisibility(View.GONE);
            viewholder.pollLayout.setVisibility(View.GONE);
            viewholder.contactLayoutReceiver.setVisibility(View.GONE);
            viewholder.pollLayoutReceiver.setVisibility(View.GONE);
            viewholder.locationLayout.setVisibility(View.GONE);
            viewholder.locationLayoutReceiver.setVisibility(View.GONE);
            viewholder.recieveTagView.setVisibility(View.GONE);
            viewholder.senderMsgText.setVisibility(View.GONE);
            viewholder.sVideoPlayImageView.setVisibility(View.GONE);
            viewholder.receiverMsgText.setVisibility(View.GONE);
            viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);


            if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                viewholder.voiceSenderLayout.setVisibility(View.VISIBLE);
                viewholder.sDateLayout.setVisibility(View.VISIBLE);
                viewholder.sendImgView.setBackgroundResource(0);
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
                viewholder.rightImgProgressBar.setVisibility(View.GONE);
                viewholder.rightImgProgressIndeterminate.setVisibility(View.GONE);
                viewholder.playSenderSeekBar.setTag(viewholder.key);
//					android.util.Log.d("ChatListAdapter", "onBind current. key: " + audioPlayerKey);
                if (audioPlayerKey != null && audioPlayerKey.equals(viewholder.key)) {
                    viewholder.playSenderSeekBar.setProgress(globalSeekBarValue);
                    viewholder.playSenderSeekBar.setMax(globalSeekBarMaxValue);
//					android.util.Log.d("ChatListAdapter", "onBind current. " + globalSeekBarMaxValue+" , "+ globalSeekBarValue);
                } else {
                    viewholder.playSenderSeekBar.setProgress(0);
                    viewholder.playSenderSeekBar.setMax(0);
                }

                //				if (playerBundle.getBoolean(viewholder.mediaUrl)){
                //					viewholder.playSenderView.setBackgroundResource(R.drawable.addpause);
                //					viewholder.playSenderSeekBar.setProgress(RTMediaPlayer.progress);
                //					}
                if (viewholder.mediaUrl == null || viewholder.mediaUrl.equals("") || !viewholder.mediaUrl.startsWith("http")) {

                    if (progressValue > 1 && progressValue < 100) {
                        ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar)).setVisibility(View.VISIBLE);
                        ((TextView) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_percent)).setVisibility(View.VISIBLE);
                        ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar_indeterminate)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar)).setProgress(progressValue);
                        ((TextView) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_percent)).setText(String.valueOf(progressValue) + "%");
                    } else {
                        ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar)).setVisibility(View.GONE);
                        ((TextView) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_percent)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar_indeterminate)).setVisibility(View.VISIBLE);
                    }
                    if (viewholder.getProcessingForURL(viewholder.mediaLocalPath) == null) {
//                        processing.put(viewholder.mediaLocalPath, "0");
                        viewholder.uploadMedia(viewholder.mediaLocalPath, viewholder.messageType);//XMPPMessageType.atMeXmppMessageTypeImage);//viewholder.mediaLocalPath, viewholder.key,viewholder.mediaThumb,XMPPMessageType.atMeXmppMessageTypeImage);
                    }
                } else if (viewholder.mediaLocalPath != null) {
                    ((TextView) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_percent)).setVisibility(View.GONE);
                    ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar_indeterminate)).setVisibility(View.GONE);
                    ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar)).setVisibility(View.GONE);
                } else if (viewholder.getProcessingForURL(url) == null)
                    ((ProgressBar) viewholder.voiceSenderLayout.findViewById(R.id.audio_upload_bar_indeterminate)).setVisibility(View.VISIBLE);
//				viewholder.playSenderView.setTag(url);
                viewholder.playSenderView.setTag(viewholder.key);
                viewholder.playSenderView.setVisibility(View.VISIBLE);
                viewholder.sVideoPlayImageView.setVisibility(View.GONE);
                viewholder.rVideoPlayImageView.setVisibility(View.GONE);
                viewholder.sDateLayout.setBackgroundResource(0);
//				viewholder.senderTime.setTextColor(context1.getResources().getColor(R.color.greytext_on_white_light));
                viewholder.voiceSenderLayout.setTag("N");
                viewholder.voiceSenderLayout.setOnLongClickListener(viewholder.onLongPressListener);
                //Show total time
                if (viewholder.audioLength != null) {
                    viewholder.playSenderMaxTimeText.setVisibility(View.VISIBLE);

                    if (!playerBundle.getBoolean(viewholder.key)) {
                        viewholder.rightAudioBtnLayout.setBackgroundResource(R.drawable.round_rect_blue);
                        viewholder.playSenderView.setBackgroundResource(R.drawable.addplay);
                        int tot = Integer.parseInt(viewholder.audioLength);
                        byte min = (byte) (tot / 60);
                        byte sec = (byte) (tot % 60);
                        if (min < 9)
                            viewholder.playSenderMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                        else
                            viewholder.playSenderMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                    } else {

                        viewholder.rightAudioBtnLayout.setBackgroundResource(R.drawable.round_rect_gray);
                        viewholder.playSenderView.setBackgroundResource(R.drawable.addpause);
                        int tot = Integer.parseInt(viewholder.audioLength) * 1000;
                        long ttt = tot - currentAudioPlayCounter;
                        if ((currentAudioPlayCounter + 100) >= tot)
                            ttt = tot;
                        ttt = ttt / 1000;
                        byte min = (byte) (ttt / 60);
                        byte sec = (byte) (ttt % 60);

                        if (min < 9)
                            viewholder.playSenderMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                        else
                            viewholder.playSenderMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                    }

                }
            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()) {

                viewholder.senderMsgText.setVisibility(View.GONE);
                viewholder.sendImgView.setVisibility(View.VISIBLE);
                viewholder.voiceSenderLayout.setVisibility(View.GONE);
                viewholder.sendImgView.setTag(url);
                if (viewholder.mediaThumb != null) {
                    android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(url);
                    if (bitmap != null) {
                        viewholder.sendImgView.setImageBitmap(bitmap);
                    } else {
                        Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
                        viewholder.sendImgView.setImageBitmap(tmpBitMap);
                        SuperChatApplication.addBitmapToMemoryCache(url, tmpBitMap);
//					viewholder.sendImgView.setImageBitmap(createVideoThumbFromByteArray(viewholder.mediaThumb));// ThumbnailUtils.createVideoThumbnail(
                        // mediaUrl,
                        // MediaStore.Video.Thumbnails.MINI_KIND
                        // ));
                    }
                    if (viewholder.mediaUrl == null || viewholder.mediaUrl.equals("") || !viewholder.mediaUrl.startsWith("http")) {

                        viewholder.sVideoPlayImageView.setVisibility(View.INVISIBLE);
                        if (progressValue > 1 && progressValue < 100) {
                            viewholder.rightImgProgressBar.setVisibility(View.VISIBLE);
                            viewholder.rightImgProgressPercent.setVisibility(View.VISIBLE);
                            viewholder.rightImgProgressIndeterminate.setVisibility(View.GONE);
                            viewholder.rightImgProgressBar.setProgress(progressValue);
                            viewholder.rightImgProgressPercent.setText(String.valueOf(progress) + "%");
                        } else {
                            viewholder.rightImgProgressPercent.setVisibility(View.GONE);
                            viewholder.rightImgProgressBar.setVisibility(View.GONE);
                            viewholder.rightImgProgressIndeterminate.setVisibility(View.VISIBLE);
                        }
                        if (viewholder.getProcessingForURL(viewholder.mediaLocalPath) == null) {
//                            processing.put(viewholder.mediaLocalPath, "0");
                            viewholder.uploadMedia(viewholder.mediaLocalPath, viewholder.messageType);//XMPPMessageType.atMeXmppMessageTypeImage);//viewholder.mediaLocalPath, viewholder.key,viewholder.mediaThumb,XMPPMessageType.atMeXmppMessageTypeImage);
                        }
                    } else if (viewholder.mediaLocalPath != null) {
                        viewholder.rightImgProgressPercent.setVisibility(View.GONE);
                        viewholder.rightImgProgressIndeterminate.setVisibility(View.GONE);
                        viewholder.rightImgProgressBar.setVisibility(View.GONE);
                        viewholder.sVideoPlayImageView.setVisibility(View.VISIBLE);
                    }

                    viewholder.rVideoPlayImageView.setVisibility(View.GONE);
                    viewholder.sDateLayout.setBackgroundResource(R.drawable.chat_time_gradient);
                    viewholder.sVideoPlayImageView.setTag("N");
                    viewholder.sVideoPlayImageView.setOnLongClickListener(viewholder.onLongPressListener);
                    viewholder.voiceSenderLayout.setVisibility(View.GONE);
                } else {
                    if (viewholder.mediaLocalPath != null)
                        viewholder.sendImgView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(viewholder.mediaLocalPath, MediaStore.Video.Thumbnails.MINI_KIND));
                    viewholder.sVideoPlayImageView.setVisibility(View.VISIBLE);
                }

            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()) {
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
                ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setImageResource(R.drawable.pdf);
                if (viewholder.mediaUrl == null || viewholder.mediaUrl.equals("") || !viewholder.mediaUrl.startsWith("http"))
                    if (viewholder.mediaLocalPath != null && viewholder.getProcessingForURL(viewholder.mediaLocalPath) == null) {
//                        processing.put(viewholder.mediaLocalPath, "0");
                        viewholder.uploadMedia(viewholder.mediaLocalPath, viewholder.messageType);
                    }
                viewholder.sendImgView.setOnLongClickListener(viewholder.onLongPressListener);

            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()) {
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
                ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setImageResource(R.drawable.docs);
                if (viewholder.mediaUrl == null || viewholder.mediaUrl.equals("") || !viewholder.mediaUrl.startsWith("http")) {
                    if (viewholder.getProcessingForURL(viewholder.mediaLocalPath) == null)
//                        System.out.println("[Calling here======>]");
//                        processing.put(viewholder.mediaLocalPath, "0");
                        viewholder.uploadMedia(viewholder.mediaLocalPath, viewholder.messageType);
                }
                viewholder.sendImgView.setOnLongClickListener(viewholder.onLongPressListener);
            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()) {
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
//				viewholder.sendImgView.setLayoutParams(viewholder.layoutParams);
//				viewholder.sendImgView.setImageResource(R.drawable.xls);
                ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setImageResource(R.drawable.xls);
                if (viewholder.mediaUrl == null || viewholder.mediaUrl.equals("") || !viewholder.mediaUrl.startsWith("http"))
                    if (viewholder.mediaLocalPath != null && viewholder.getProcessingForURL(viewholder.mediaLocalPath) == null) {
//                        processing.put(viewholder.mediaLocalPath, "0");
                        viewholder.uploadMedia(viewholder.mediaLocalPath, viewholder.messageType);
                    }
                viewholder.sendImgView.setOnLongClickListener(viewholder.onLongPressListener);
            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
//				viewholder.sendImgView.setLayoutParams(viewholder.layoutParams);
//				viewholder.sendImgView.setImageResource(R.drawable.ppt);
                ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setImageResource(R.drawable.ppt);
                if (viewholder.mediaUrl == null || viewholder.mediaUrl.equals("") || !viewholder.mediaUrl.startsWith("http"))
                    if (viewholder.mediaLocalPath != null && viewholder.getProcessingForURL(viewholder.mediaLocalPath) == null) {
//                        processing.put(viewholder.mediaLocalPath, "0");
                        viewholder.uploadMedia(viewholder.mediaLocalPath, viewholder.messageType);
                    }
                viewholder.sendImgView.setOnLongClickListener(viewholder.onLongPressListener);
            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal()) {
                viewholder.rightFileLayout.setVisibility(View.GONE);
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
                viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                viewholder.contactLayout.setVisibility(View.VISIBLE);
//				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewholder.sDateLayout.getLayoutParams();
//				params.addRule(RelativeLayout.RIGHT_OF, R.id.contact_layout);
//				params.addRule(RelativeLayout.BELOW, R.id.contact_layout);
//				viewholder.sDateLayout.setLayoutParams(params);
                viewholder.contactData = viewholder.captionTagMsg;
                if (viewholder.contactData.contains("&quot;"))
                    viewholder.contactData = viewholder.contactData.replace("&quot;", "\"");
                viewholder.contactLayout.setOnClickListener(viewholder.onContactClickListener);
                viewholder.contactLayout.setOnLongClickListener(viewholder.onLongPressListener);
                try {
                    //Show Values from JSON
                    JSONObject jsonobj = new JSONObject(viewholder.contactData);
                    String dislay_name = "Unknown";
                    if (jsonobj.has("firstName") && jsonobj.getString("firstName").toString().trim().length() > 0)
                        dislay_name = jsonobj.getString("firstName");
                    if (jsonobj.has("lastName") && jsonobj.getString("lastName").toString().trim().length() > 0)
                        dislay_name = dislay_name + " " + jsonobj.getString("lastName");
                    viewholder.contactName.setText(dislay_name);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal()) {
                viewholder.rightFileLayout.setVisibility(View.GONE);
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
                viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                viewholder.pollLayout.setVisibility(View.VISIBLE);
                viewholder.pollDataSent = viewholder.captionTagMsg;
                if (viewholder.pollDataSent.contains("&quot;"))
                    viewholder.pollDataSent = viewholder.pollDataSent.replace("&quot;", "\"");

                viewholder.senderLayout.setTag("sender=" + viewholder.pollDataSent);
                viewholder.senderLayout.setOnClickListener(viewholder.onPollClickListener);
                viewholder.senderLayout.setOnLongClickListener(viewholder.onLongPressListener);
                viewholder.pollLayout.setTag("sender=" + viewholder.pollDataSent);
                viewholder.pollLayout.setOnClickListener(viewholder.onPollClickListener);
                viewholder.pollLayout.setOnLongClickListener(viewholder.onLongPressListener);
                try {
                    //Show Values from JSON
                    JSONObject jsonobj = new JSONObject(viewholder.pollDataSent);
                    if (jsonobj.has("PollTitle") && jsonobj.getString("PollTitle").toString().trim().length() > 0) {
                        viewholder.pollTitle.setText("Poll - " + jsonobj.getString("PollTitle"));
                    }
                    if (jsonobj.has("Pollmessage") && jsonobj.getString("Pollmessage").toString().trim().length() > 0) {
                        viewholder.pollMessage.setText(jsonobj.getString("Pollmessage"));
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal()) {
                viewholder.locationData = viewholder.locationMsg;
                viewholder.rightFileLayout.setVisibility(View.GONE);
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.contactLayout.setVisibility(View.GONE);
                viewholder.pollLayout.setVisibility(View.GONE);
                viewholder.sendTagView.setVisibility(View.GONE);
                viewholder.locationLayout.setVisibility(View.VISIBLE);
//				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewholder.sDateLayout.getLayoutParams();
//				params.addRule(RelativeLayout.RIGHT_OF, R.id.location_layout);
//				params.addRule(RelativeLayout.BELOW, R.id.location_layout);
//				viewholder.sDateLayout.setLayoutParams(params);
                viewholder.locationLayout.setOnClickListener(viewholder.onLocationClickListener);
                viewholder.locationLayout.setTag("Y");
                viewholder.locationLayout.setOnLongClickListener(viewholder.onLongPressListener);
                //Display Map here.
                if (viewholder.locationData != null && !viewholder.locationData.equals("")) {
                    String[] loc = viewholder.locationData.split(",");
                    double lat = Double.parseDouble(loc[0]);
                    double lon = Double.parseDouble(loc[1]);
//					String head = "My Location";
//					head =  ((ChatListScreen) context).getAddress(lat, lon);
//					viewholder.contactNameReceiver.setText(head);
                    if (viewholder.captionTagMsg != null && viewholder.captionTagMsg.length() > 0) {
                        if (viewholder.captionTagMsg.indexOf("\n") != -1) {
                            viewholder.locationName.setText(viewholder.captionTagMsg.substring(0, viewholder.captionTagMsg.indexOf("\n")));
                            viewholder.locationNameAddress.setText(viewholder.captionTagMsg.substring(viewholder.captionTagMsg.indexOf("\n") + 1));
                        } else
                            viewholder.locationName.setText(viewholder.captionTagMsg);
                    }

                    String mapurl = viewholder.MAP_URL.replace("$lat", "" + lat);
                    mapurl = mapurl.replace("$lon", "" + lon);
                    android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(mapurl);
                    if (bitmap != null) {
                        viewholder.mapviewSender.setImageBitmap(bitmap);
                    } else {
                        if (processingMap.get(mapurl) == null) {
                            processingMap.put(mapurl, mapurl);
                            new ImageLoadTask(mapurl, viewholder.mapviewSender).execute();
                        }
                    }

                    // Gets to GoogleMap from the MapView and does initialization stuff
//					viewholder.mapSender = viewholder.mapviewSender.getMap();
//					viewholder.mapSender.getUiSettings().setMyLocationButtonEnabled(false);
//					// Needs to call MapsInitializer before doing any CameraUpdateFactory calls
//					viewholder.mapSender.setMyLocationEnabled(true);
//					try {
//						MapsInitializer.initialize(context);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					// Updates the location and zoom of the MapView
//					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10);
//					viewholder.mapSender.animateCamera(cameraUpdate);

//					viewholder.mapSender.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(13.0810,80.2740), 15.5f), 4000, null);
//					viewholder.mapviewSender.getMapAsync((ChatListScreen)context);
                }

            } else if (url != null) {// this is only image case
                viewholder.contactLayout.setVisibility(View.GONE);
                viewholder.pollLayout.setVisibility(View.GONE);
                viewholder.senderMsgText.setVisibility(View.GONE);
                viewholder.sendImgView.setVisibility(View.VISIBLE);
                viewholder.sendImgView.setTag("N");
                viewholder.sendImgView.setOnLongClickListener(viewholder.onLongPressListener);
                viewholder.sendImgView.setTag(url);
//				viewholder.sendImgView.setImageURI(Uri.parse(url));
                setPicForCache(viewholder.sendImgView, url);
                viewholder.sVideoPlayImageView.setVisibility(View.GONE);
                viewholder.rVideoPlayImageView.setVisibility(View.GONE);
                viewholder.sDateLayout.setBackgroundResource(R.drawable.chat_time_gradient);
                if (viewholder.mediaUrl == null || viewholder.mediaUrl.equals("") || !viewholder.mediaUrl.startsWith("http")) {

                    if (progressValue > 1 && progressValue < 100) {
                        viewholder.rightImgProgressBar.setVisibility(View.VISIBLE);
                        viewholder.rightImgProgressPercent.setVisibility(View.VISIBLE);
                        viewholder.rightImgProgressIndeterminate.setVisibility(View.GONE);
                        viewholder.rightImgProgressBar.setProgress(progressValue);
                        viewholder.rightImgProgressPercent.setText(String.valueOf(progress) + "%");
                    } else {
                        viewholder.rightImgProgressBar.setVisibility(View.GONE);
                        viewholder.rightImgProgressPercent.setVisibility(View.GONE);
                        viewholder.rightImgProgressIndeterminate.setVisibility(View.VISIBLE);
                    }
                    if (viewholder.getProcessingForURL(viewholder.mediaLocalPath) == null) {
//						processing.put(viewholder.mediaLocalPath, "0");
                        viewholder.uploadMedia(viewholder.mediaLocalPath, viewholder.messageType);//XMPPMessageType.atMeXmppMessageTypeImage);//viewholder.mediaLocalPath, viewholder.key,viewholder.mediaThumb,XMPPMessageType.atMeXmppMessageTypeImage);
                    }
                } else if (viewholder.mediaUrl.startsWith("http") && viewholder.mediaLocalPath == null) {
                    if (viewholder.getProcessingForURL(viewholder.mediaUrl) == null && viewholder.mediaLocalPath == null) {
                        if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()) {
                            ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setBackgroundResource(R.drawable.pdf);
                            Log.d("ChatListAdapter", "<<   started for - " + viewholder.mediaUrl);
                            Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, viewholder.mediaUrl};
                            viewholder.download(viewholder.mediaUrl, viewholder.messageType, viewholder.sendImgView, viewholder.progressbar, params);
                        } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()) {
                            ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setBackgroundResource(R.drawable.docs);
                            Log.d("ChatListAdapter", "<<   started for - " + viewholder.mediaUrl);
                            Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, viewholder.mediaUrl};
                            viewholder.download(viewholder.mediaUrl, viewholder.messageType, viewholder.sendImgView, viewholder.progressbar, params);
                        } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()) {
                            ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setBackgroundResource(R.drawable.xls);
                            Log.d("ChatListAdapter", "<<   started for - " + viewholder.mediaUrl);
                            Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, viewholder.mediaUrl};
                            viewholder.download(url, viewholder.messageType, viewholder.sendImgView, viewholder.progressbar, params);
                        } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                            ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setBackgroundResource(R.drawable.ppt);
                            Log.d("ChatListAdapter", "<<   started for - " + viewholder.mediaUrl);
                            Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, viewholder.mediaUrl};
                            viewholder.download(viewholder.mediaUrl, viewholder.messageType, viewholder.sendImgView, viewholder.progressbar, params);
                        } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                            ((ImageView) viewholder.rightFileLayout.findViewById(R.id.id_file_image)).setVisibility(View.GONE);
                            Log.d("ChatListAdapter", "media url found for download " + viewholder.mediaUrl);
                            Object[] params = new Object[]{this,
                                    viewholder.key, cursor,
                                    viewholder.voiceLoadingPercent, viewholder.mediaUrl};
                            viewholder.download(viewholder.mediaUrl, viewholder.messageType,
                                    viewholder.playSenderView,
                                    viewholder.voiceDownloadingBar, params);
                        } else {//video and image
                            Object[] params = new Object[]{this, viewholder.key, cursor,
                                    viewholder.rightImgProgressPercent, viewholder.mediaUrl};
                            viewholder.download(viewholder.mediaUrl, viewholder.messageType,
                                    viewholder.sendImgView,
                                    viewholder.rightImgProgressBar, params);

                            android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(viewholder.mediaUrl);
                            if (bitmap != null) {
                                viewholder.sendImgView.setImageBitmap(bitmap);
                            } else if (viewholder.mediaThumb != null) {
                                Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
                                viewholder.sendImgView.setImageBitmap(tmpBitMap);
                                SuperChatApplication.addBitmapToMemoryCache(url, tmpBitMap);
                            } else {
                                viewholder.sendImgView.setImageResource(R.drawable.default_bg_img);
//                                String img_url = viewholder.mediaUrl.replace("get", "convertget") + "?height=50&width=50";
//                                viewholder.sendImgView.setImageURI(Uri.parse(img_url));
//                                img_url = null;
                            }
                        }
                    }
                    if (progressValue > 1 && progressValue < 100) {
                        viewholder.rightImgProgressBar.setVisibility(View.VISIBLE);
                        viewholder.rightImgProgressPercent.setVisibility(View.VISIBLE);
                        viewholder.rightImgProgressIndeterminate.setVisibility(View.GONE);
                        viewholder.rightImgProgressBar.setProgress(progressValue);
                        viewholder.rightImgProgressPercent.setText(String.valueOf(progress) + "%");
                    } else {
                        viewholder.rightImgProgressBar.setVisibility(View.GONE);
                        viewholder.rightImgProgressPercent.setVisibility(View.GONE);
                        viewholder.rightImgProgressIndeterminate.setVisibility(View.VISIBLE);
                    }
                } else {
                    viewholder.rightImgProgressIndeterminate.setVisibility(View.GONE);
                    viewholder.rightImgProgressBar.setVisibility(View.GONE);
                    viewholder.rightImgProgressBar.setVisibility(View.GONE);
                    viewholder.rightImgProgressPercent.setVisibility(View.GONE);
                }
            } else {
                viewholder.sendImgView.setBackgroundResource(0);
                viewholder.sendImgView.setVisibility(View.GONE);
                viewholder.rightFileLayout.setVisibility(View.GONE);
                viewholder.sDateLayout.setVisibility(View.VISIBLE);
                viewholder.senderMsgText.setVisibility(View.VISIBLE);
                viewholder.rightImgProgressBar.setVisibility(View.GONE);
                viewholder.rightImgProgressPercent.setVisibility(View.GONE);
                if (viewholder.rightImgProgressIndeterminate != null)
                    viewholder.rightImgProgressIndeterminate.setVisibility(View.GONE);
                viewholder.senderMsgText.setText(viewholder.message);
                viewholder.senderMsgText.setTag("Y");
                viewholder.senderLayout.setTag("Y");
                viewholder.senderMsgText.setOnLongClickListener(viewholder.onLongPressListener);
                viewholder.senderLayout.setOnLongClickListener(viewholder.onLongPressListener);

                viewholder.sVideoPlayImageView.setVisibility(View.GONE);
                viewholder.rVideoPlayImageView.setVisibility(View.GONE);
//				viewholder.senderTime.setTextColor(context1.getResources().getColor(R.color.greytext_on_white_light));
                viewholder.sDateLayout.setBackgroundResource(0);
            }
            viewholder.senderTime.setTextColor(context1.getResources().getColor(R.color.color_lite_gray));
            viewholder.senderTime.setText(msgTime);
            viewholder.messageStatusView.setVisibility(View.VISIBLE);
            viewholder.unsentAlertView.setVisibility(View.GONE);
            if (isGroupChat) {
                if (viewholder.totalGroupUsers != 0 && viewholder.totalGroupReadUsers != 0
                        && (viewholder.totalGroupUsers - 1) <= viewholder.totalGroupReadUsers) {
//					viewholder.messageStatusView.setText(R.string.read);
                    if (!isBulletinBroadcast) {
                        viewholder.messageStatusView.setBackgroundResource(R.drawable.read_tick);
                        viewholder.senderTime.setTextColor(context1.getResources().getColor(R.color.color_lite_blue));
                    }
                    viewholder.senderTime.setText(msgTime);
                } else if (viewholder.seenState == Message.SeenState.wait.ordinal()
                        || viewholder.seenState == Message.SeenState.pic_wait.ordinal()) {
//					viewholder.messageStatusView.setText(R.string.wait);
                    viewholder.messageStatusView.setBackgroundResource(R.drawable.time_clock);
                } else {
//					viewholder.messageStatusView.setText(R.string.sent);
                    viewholder.messageStatusView.setBackgroundResource(R.drawable.sent_tick);
                }
            } else if (viewholder.seenState == Message.SeenState.seen.ordinal()) {
                if (!isBroadCastChat && !isBulletinBroadcast) {
//					viewholder.messageStatusView.setText(R.string.read);
                    viewholder.messageStatusView.setBackgroundResource(R.drawable.read_tick);
                    viewholder.senderTime.setTextColor(context1.getResources().getColor(R.color.color_lite_blue));
                    viewholder.senderTime.setText(msgTime);
                } else {
//					viewholder.messageStatusView.setText(R.string.sent);
                    viewholder.messageStatusView.setBackgroundResource(R.drawable.sent_tick);
                }
            } else if (viewholder.seenState == Message.SeenState.recieved.ordinal()) {
                if (!isBroadCastChat && !isBulletinBroadcast) {
//					viewholder.messageStatusView.setText(R.string.recieved);
                    viewholder.messageStatusView.setBackgroundResource(R.drawable.delivered_tick);
                } else {
//					viewholder.messageStatusView.setText(R.string.sent);
                    viewholder.messageStatusView.setBackgroundResource(R.drawable.sent_tick);
                }
            } else if (viewholder.seenState == Message.SeenState.sent.ordinal()) {
//				if(isGroupChat)
//					viewholder.messageStatusView.setText(R.string.sent);
                viewholder.messageStatusView.setBackgroundResource(R.drawable.sent_tick);
//				else
//					viewholder.messageStatusView.setText(R.string.wait);

            } else {
//				viewholder.messageStatusView.setVisibility(View.INVISIBLE);
//				if(isGroupChat){
//					viewholder.messageStatusView.setText(R.string.wait);
                viewholder.messageStatusView.setBackgroundResource(R.drawable.time_clock);
                viewholder.unsentAlertView.setVisibility(View.GONE);
//				}	else{
//					viewholder.messageStatusView.setText(R.string.not_sent);
//					viewholder.unsentAlertView.setVisibility(View.VISIBLE);
//				}
            }

//			if(progressValue>0 &&progressValue<100 && viewholder.seenState == Message.SeenState.pic_wait.ordinal()){
//				viewholder.rightImgProgressBar.setVisibility(ProgressBar.VISIBLE);
//				viewholder.rightImgProgressBar.setProgress(progressValue);
//			}else
//				viewholder.rightImgProgressBar.setVisibility(ProgressBar.GONE);
            viewholder.leftPersonPic.setVisibility(View.GONE);
            viewholder.leftPersonDefaultPic.setVisibility(View.GONE);

        } else if (!isBroadCastChat) {
            String progress = viewholder.getProcessingForURL(url);
            if (progress != null && !progress.equals("")) {
                try {
                    progressValue = Integer.parseInt(progress);
                } catch (NumberFormatException ex) {

                }
//				
            }
            viewholder.leftPersonPicLayout.setVisibility(View.GONE);
            viewholder.rightFileLayout.setVisibility(View.GONE);
            viewholder.rightFileTypeView.setVisibility(View.GONE);
            if (viewholder.captionTagMsg == null || viewholder.captionTagMsg.equals("")) {

                viewholder.recieveTagView.setVisibility(TextView.GONE);
            } else {
                if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal()) {
                    viewholder.receiverMsgText.setVisibility(TextView.GONE);
                    viewholder.recieveTagView.setVisibility(TextView.GONE);
                    viewholder.contactLayoutReceiver.setVisibility(TextView.VISIBLE);
                    viewholder.contactLayoutReceiver.setOnClickListener(viewholder.onContactClickListener);
                    viewholder.contactLayoutReceiver.setOnLongClickListener(viewholder.onLongPressListener);
//					RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewholder.rDateLayout.getLayoutParams();
//					params.addRule(RelativeLayout.RIGHT_OF, R.id.contact_layout_r);
//					params.addRule(RelativeLayout.BELOW, R.id.contact_layout_r);
//					viewholder.rDateLayout.setLayoutParams(params);
                    try {
                        //Show Values from JSON
                        viewholder.contactData = viewholder.captionTagMsg;
                        JSONObject jsonobj = new JSONObject(viewholder.captionTagMsg);
                        String dislay_name = "Unknown";
                        if (jsonobj.has("firstName") && jsonobj.getString("firstName").toString().trim().length() > 0)
                            dislay_name = jsonobj.getString("firstName");
                        if (jsonobj.has("lastName") && jsonobj.getString("lastName").toString().trim().length() > 0)
                            dislay_name = dislay_name + " " + jsonobj.getString("lastName");
                        viewholder.contactNameReceiver.setText(dislay_name);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal()) {
                    viewholder.receiverMsgText.setVisibility(TextView.GONE);
                    viewholder.recieveTagView.setVisibility(TextView.GONE);
                    viewholder.pollLayoutReceiver.setVisibility(TextView.VISIBLE);

                    viewholder.receiverLayout.setTag(viewholder.captionTagMsg);
                    viewholder.receiverLayout.setOnClickListener(viewholder.onPollClickListener);
                    viewholder.pollLayoutReceiver.setTag(viewholder.captionTagMsg);
                    viewholder.pollLayoutReceiver.setOnClickListener(viewholder.onPollClickListener);
                    viewholder.receiverLayout.setOnLongClickListener(viewholder.onLongPressListener);
                    viewholder.pollLayoutReceiver.setOnLongClickListener(viewholder.onLongPressListener);
                    try {
                        //Show Values from JSON
                        viewholder.pollMessageReceived = viewholder.captionTagMsg;
                        JSONObject jsonobj = new JSONObject(viewholder.pollMessageReceived);
                        String dislay_name = "Unknown";
                        if (jsonobj.has("PollTitle") && jsonobj.getString("PollTitle").toString().trim().length() > 0)
                            viewholder.pollTtitleReceiver.setText("Poll - " + jsonobj.getString("PollTitle"));
                        if (jsonobj.has("Pollmessage") && jsonobj.getString("Pollmessage").toString().trim().length() > 0)
                            viewholder.pollMessageReceiver.setText(jsonobj.getString("Pollmessage"));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                        viewholder.contactLayout.setVisibility(TextView.GONE);
                        viewholder.pollLayout.setVisibility(TextView.GONE);
                        viewholder.recieveTagView.setVisibility(TextView.GONE);
                        if (viewholder.captionTagMsg != null) {
                            viewholder.recieveAudioTagView.setVisibility(TextView.VISIBLE);
                            viewholder.recieveAudioTagView.setText(viewholder.captionTagMsg);
                        } else {
                            viewholder.recieveAudioTagView.setVisibility(TextView.GONE);
                        }
                    } else if (viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeLocation.ordinal()) {
                        viewholder.contactLayout.setVisibility(TextView.GONE);
                        viewholder.pollLayout.setVisibility(TextView.GONE);
                        viewholder.recieveTagView.setVisibility(TextView.VISIBLE);
                        viewholder.recieveTagView.setText(viewholder.captionTagMsg);
                    }
                }

            }
            // My received messages.
            //Check for location
            if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal()) {
                if (viewholder.locationMsg != null && !viewholder.locationMsg.equals("")) {
                    viewholder.locationData = viewholder.locationMsg;
                    viewholder.receiverMsgText.setVisibility(View.GONE);
//					viewholder.mapviewSender.setVisibility(View.GONE);
                    viewholder.contactLayout.setVisibility(View.GONE);
                    viewholder.pollLayout.setVisibility(TextView.GONE);
                    viewholder.recieveTagView.setVisibility(View.GONE);
                    viewholder.locationLayoutReceiver.setVisibility(View.VISIBLE);
                    viewholder.locationLayoutReceiver.setOnClickListener(viewholder.onLocationClickListener);
                    viewholder.locationLayoutReceiver.setOnLongClickListener(viewholder.onLongPressListener);
                    if (viewholder.captionTagMsg != null && viewholder.captionTagMsg.length() > 0) {
                        if (viewholder.captionTagMsg.indexOf("\n") != -1) {
                            viewholder.locationNameReceiver.setText(viewholder.captionTagMsg.substring(0, viewholder.captionTagMsg.indexOf("\n")));
                            viewholder.locationNameAddressReceiver.setText(viewholder.captionTagMsg.substring(viewholder.captionTagMsg.indexOf("\n") + 1));
                        } else
                            viewholder.locationNameReceiver.setText(viewholder.captionTagMsg);
                    }
                    //Show map here
                    String[] loc = viewholder.locationData.split(",");
                    double lat = Double.parseDouble(loc[0]);
                    double lon = Double.parseDouble(loc[1]);


                    String mapurl = viewholder.MAP_URL.replace("$lat", "" + lat);
                    mapurl = mapurl.replace("$lon", "" + lon);
                    android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(mapurl);
                    if (bitmap != null) {
                        viewholder.mapviewReceiver.setImageBitmap(bitmap);
                    } else {
                        if (processingMap.get(mapurl) == null) {
                            processingMap.put(mapurl, mapurl);
                            new ImageLoadTask(mapurl, viewholder.mapviewReceiver).execute();
                        }
                    }
//					String head = "Location";
//					head =  ((ChatListScreen) context).getAddress(lat, lon);
//					viewholder.contactNameReceiver.setText(head);

                    // Gets to GoogleMap from the MapView and does initialization stuff
//					viewholder.mapReceiver = viewholder.mapviewReceiver.getMap();
//					viewholder.mapReceiver.getUiSettings().setMyLocationButtonEnabled(false);
//					viewholder.mapReceiver.setMyLocationEnabled(true);
//					// Needs to call MapsInitializer before doing any CameraUpdateFactory calls
//					try {
//						MapsInitializer.initialize(context);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					// Updates the location and zoom of the MapView
//					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 10);
//					viewholder.mapReceiver.animateCamera(cameraUpdate);

//					viewholder.mapviewReceiver.invalidate();
//			        viewholder.mapviewReceiver.getMapAsync((ChatListScreen)context);
                }
            }

            viewholder.senderLayout.setVisibility(View.GONE);

//			view.setPadding(convertPixelToDip(5), convertPixelToDip(0),
//					convertPixelToDip(25), 0);

//			if (isEditableChat) {
//				if (viewholder.groupMsgSenderName.equals("")
//						&& !viewholder.userName.equals(myUserName)
//						&& isGroupChat) {
//					viewholder.senderCheckBox.setVisibility(View.GONE);
//					viewholder.receiverCheckBox.setVisibility(View.GONE);
//				} else {
//					viewholder.senderCheckBox.setVisibility(View.GONE);
//					viewholder.receiverCheckBox.setVisibility(View.VISIBLE);
//					boolean isChecked = false;
//					Object obj = checkedTagMap.get(viewholder.key);
//					if (obj != null) {
//						isChecked = checkedTagMap.get(viewholder.key);
//					}
//					viewholder.receiverCheckBox.setChecked(isChecked);
//					viewholder.receiverCheckBox.setTag(viewholder.key);
//				}
//			} else
            {
                viewholder.receiverCheckBox.setVisibility(View.GONE);
            }

            if (isGroupChat || isSharedIDMessage || isBulletinBroadcast) {
                viewholder.receiverPersonName.setVisibility(View.VISIBLE);
                String name = viewholder.groupMsgSenderName;
                if (isSharedIDMessage && (iChatPref.isDomainAdminORSubAdmin()) || isSharedIDAdmin) {
                    name = iChatPref.getUserServerName(viewholder.groupMsgSenderName);
                    viewholder.replySharedID.setVisibility(View.VISIBLE);
                }
                if (name != null && name.equalsIgnoreCase("New User"))
                    name = iChatPref.getUserServerName(viewholder.groupMsgSenderName);
//				if(name!=null && name.length()>2 && name.startsWith("m") && Integer.name.substring(1).)
//				int colorValue = (name+viewholder.userName).hashCode();
//
//				if(colorValue<=0 || colorValue>14540236)
//					colorValue  = 14540236;
                String colorValue = colors.get(name);
                if (colorValue == null) {
                    colors.put(name, colorsArray[colorIndex]);
                    colorValue = colorsArray[colorIndex];
                    colorIndex++;
                    if (colorIndex > colorsArray.length - 1)
                        colorIndex = 0;
                }
                String personId = "";
                viewholder.receiverPersonName.setTextColor(Color.parseColor(colorValue));//colorValue.intValue());
                if (name != null && name.contains("#786#")) {
                    personId = viewholder.groupMsgSenderName.substring(viewholder.groupMsgSenderName.indexOf("#786#") + "#786#".length());
                    name = name.substring(0, name.indexOf("#786#"));
                } else if (isSharedIDMessage) {
                    personId = viewholder.groupMsgSenderName;
                    name = iChatPref.getUserServerName(personId);
                }
                if (name != null && name.contains("_"))
                    name = "+" + name.substring(0, name.indexOf("_"));
                if (name != null && name.startsWith("+"))
                    name = "New User";
                viewholder.receiverPersonName.setText(name); // Set the name
                if (name == null || name.equals("")) {
                    viewholder.leftPersonPicLayout.setVisibility(View.GONE);
                    viewholder.leftPersonDefaultPic.setVisibility(View.GONE);
                    viewholder.leftPersonPic.setVisibility(View.GONE);
                } else {
                    viewholder.leftPersonPicLayout.setVisibility(View.VISIBLE);
                    setProfilePic(viewholder.leftPersonPic, viewholder.leftPersonDefaultPic, personId, name);
                }
                // of
                // message
                // sender
                // name in
                // the group
                // messaging
                if (viewholder.groupMsgSenderName.equals("")) {
                    viewholder.receiverLayout.setVisibility(View.GONE);
                } else
                    viewholder.receiverLayout.setVisibility(View.VISIBLE);
            } else if (isBulletinBroadcast/* && iChatPref.isDomainAdminORSubAdmin()*/) {
                viewholder.receiverPersonName.setVisibility(View.VISIBLE);
                String name = iChatPref.getUserServerName(viewholder.userName);
                if (name != null && name.equalsIgnoreCase("New User"))
                    name = iChatPref.getUserServerName(viewholder.userName);

                if (name.equals(iChatPref.getUserName()))
                    name = "";

                String colorValue = colors.get(name);
                if (colorValue == null) {
                    colors.put(name, colorsArray[colorIndex]);
                    colorValue = colorsArray[colorIndex];
                    colorIndex++;
                    if (colorIndex > colorsArray.length - 1)
                        colorIndex = 0;
                }
                String personId = viewholder.userName;
                viewholder.receiverPersonName.setTextColor(Color.parseColor(colorValue));
                viewholder.receiverPersonName.setText(name);
//                    if(name == null || name.equals("")){
//                        viewholder.leftPersonPicLayout.setVisibility(View.GONE);
//                        viewholder.leftPersonDefaultPic.setVisibility(View.GONE);
//                        viewholder.leftPersonPic.setVisibility(View.GONE);
//                    }
                viewholder.receiverLayout.setVisibility(View.VISIBLE);
            } else {
                viewholder.leftPersonPic.setVisibility(View.GONE);
                viewholder.receiverPersonName.setVisibility(View.GONE);
                viewholder.receiverLayout.setVisibility(View.VISIBLE);
            }

            //Received media
            if (url != null && url.trim().length() > 0) {
                viewholder.contactLayoutReceiver.setVisibility(View.GONE);
                viewholder.pollLayoutReceiver.setVisibility(TextView.GONE);
                viewholder.locationLayoutReceiver.setVisibility(View.GONE);
                viewholder.receiverMsgText.setVisibility(View.GONE);
                if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()) {
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                    viewholder.leftFileLayout.setVisibility(View.VISIBLE);
                    viewholder.leftFileTypeView.setVisibility(View.VISIBLE);
                    viewholder.recieveTagView.setVisibility(TextView.GONE);
                    viewholder.receiveImgView.setVisibility(View.GONE);
                    ((ImageView) viewholder.leftFileLayout.findViewById(R.id.id_file_image)).setImageResource(R.drawable.pdf);
                    ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                    viewholder.leftFileTypeView.setText(R.string.pdf);
                    if (viewholder.audioLength != null)
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_size)).setText(viewholder.audioLength);
                    else
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_size)).setText("");
                    if (url != null && url.startsWith("http://")) {
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.VISIBLE);
                    } else {
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.GONE);
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()) {
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                    viewholder.leftFileLayout.setVisibility(View.VISIBLE);
                    viewholder.leftFileTypeView.setVisibility(View.VISIBLE);
                    viewholder.recieveTagView.setVisibility(TextView.GONE);
                    viewholder.receiveImgView.setVisibility(View.GONE);
                    ((ImageView) viewholder.leftFileLayout.findViewById(R.id.id_file_image)).setImageResource(R.drawable.docs);
                    ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                    viewholder.leftFileTypeView.setText(R.string.doc);
                    if (viewholder.audioLength != null)
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_size)).setText(viewholder.audioLength);
                    else
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_size)).setText("");
                    if (url != null && url.startsWith("http://"))
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.VISIBLE);
                    else {
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.GONE);
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()) {
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                    viewholder.leftFileLayout.setVisibility(View.VISIBLE);
                    viewholder.leftFileTypeView.setVisibility(View.VISIBLE);
                    viewholder.recieveTagView.setVisibility(TextView.GONE);
                    viewholder.receiveImgView.setVisibility(View.GONE);
//					viewholder.receiveImgView.setLayoutParams(viewholder.layoutParams);
//					viewholder.receiveImgView.setImageResource(R.drawable.xls);
                    ((ImageView) viewholder.leftFileLayout.findViewById(R.id.id_file_image)).setImageResource(R.drawable.xls);
                    ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                    viewholder.leftFileTypeView.setText(R.string.xls);
                    if (viewholder.audioLength != null)
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_size)).setText(viewholder.audioLength);
                    else
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_size)).setText("");
                    if (url != null && url.startsWith("http://"))
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.VISIBLE);
                    else {
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.GONE);
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                    viewholder.leftFileLayout.setVisibility(View.VISIBLE);
                    viewholder.leftFileTypeView.setVisibility(View.VISIBLE);
                    viewholder.recieveTagView.setVisibility(TextView.GONE);
                    viewholder.receiveImgView.setVisibility(View.GONE);
//					viewholder.receiveImgView.setLayoutParams(viewholder.layoutParams);
//					viewholder.receiveImgView.setImageResource(R.drawable.ppt);
                    ((ImageView) viewholder.leftFileLayout.findViewById(R.id.id_file_image)).setImageResource(R.drawable.ppt);
                    ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_name)).setText(viewholder.captionTagMsg);
                    viewholder.leftFileTypeView.setText(R.string.ppt);
                    if (viewholder.audioLength != null)
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_size)).setText(viewholder.audioLength);
                    else
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.id_file_size)).setText("");
                    if (url != null && url.startsWith("http://"))
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.VISIBLE);
                    else {
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.GONE);
                        ((TextView) viewholder.leftFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                        ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                    }
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                    viewholder.leftFileLayout.setVisibility(View.GONE);
                    viewholder.recieveTagView.setVisibility(TextView.GONE);
                    viewholder.leftFileTypeView.setVisibility(View.GONE);
                    viewholder.contactLayoutReceiver.setVisibility(View.GONE);
                    viewholder.pollLayoutReceiver.setVisibility(TextView.GONE);
                    viewholder.locationLayoutReceiver.setVisibility(View.GONE);
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.VISIBLE);
                    viewholder.voiceRecieverInnerLayout.setTag("N");
                    viewholder.voiceRecieverInnerLayout.setOnLongClickListener(viewholder.onLongPressListener);
                    viewholder.receiveImgView.setVisibility(View.GONE);
                    viewholder.playRecieverView.setTag(url);

                    //					if (playerBundle.getBoolean(viewholder.mediaUrl)){
                    //						viewholder.playRecieverView.setBackgroundResource(R.drawable.addpause);
                    //						viewholder.playRecieverSeekBar.setProgress(RTMediaPlayer.progress);
                    //					}
//					viewholder.receiverTime.setTextColor(context1.getResources().getColor(R.color.text_color_on_white_light));
                    viewholder.receiverTime.setBackgroundResource(0);
                    viewholder.receiverTime.setPadding(0, 0, 0, 0);
                    if (viewholder.captionTagMsg == null) {
                        viewholder.recieveAudioTagView.setVisibility(TextView.GONE);
                    }

                    //Show total time
                    if (viewholder.audioLength != null && !viewholder.audioLength.equals("") && !viewholder.audioLength.equals("0")) {
                        viewholder.playRecieverMaxTimeText.setVisibility(View.VISIBLE);
                        if (!playerBundle.getBoolean(viewholder.key)) {

                            int tot = Integer.parseInt(viewholder.audioLength);
                            byte min = (byte) (tot / 60);
                            byte sec = (byte) (tot % 60);
                            if (min < 9)
                                viewholder.playRecieverMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                            else
                                viewholder.playRecieverMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                            viewholder.leftAudioBtnLayout.setBackgroundResource(R.drawable.round_rect_blue);
                            viewholder.playRecieverView.setBackgroundResource(R.drawable.addplay);
                        } else {
                            viewholder.leftAudioBtnLayout.setBackgroundResource(R.drawable.round_rect_gray);
                            viewholder.playRecieverView.setBackgroundResource(R.drawable.addpause);
                            int tot = Integer.parseInt(viewholder.audioLength) * 1000;
                            long ttt = tot - currentAudioPlayCounter;
                            if ((currentAudioPlayCounter + 100) >= tot)
                                ttt = tot;
                            ttt = ttt / 1000;
                            byte min = (byte) (ttt / 60);
                            byte sec = (byte) (ttt % 60);

                            if (min < 9)
                                viewholder.playRecieverMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                            else
                                viewholder.playRecieverMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                        }
                    } else
                        viewholder.playRecieverMaxTimeText.setVisibility(View.GONE);


                } else {
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                    viewholder.leftFileLayout.setVisibility(View.GONE);
                    viewholder.leftFileTypeView.setVisibility(View.GONE);
                    viewholder.locationLayoutReceiver.setVisibility(View.GONE);
                    viewholder.contactLayoutReceiver.setVisibility(View.GONE);
                    viewholder.pollLayoutReceiver.setVisibility(TextView.GONE);
                    viewholder.receiveImgView.setVisibility(View.VISIBLE);
                    viewholder.receiveImgView.setTag("N");
                    viewholder.receiveImgView.setOnLongClickListener(viewholder.onLongPressListener);
                    viewholder.receiveImgView.setTag(url);
                    if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal() && url != null) {
//						setPicForCache(viewholder.receiveImgView, url);


//						android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(url);
//						if (bitmap != null) {
//							viewholder.receiveImgView.setImageBitmap(bitmap);
//						}else{
//							Bitmap tmpBitMap = ThumbnailUtils.createVideoThumbnail(url,MediaStore.Video.Thumbnails.MINI_KIND);
//							viewholder.receiveImgView.setImageBitmap(tmpBitMap);
//					    	SuperChatApplication.addBitmapToMemoryCache(url,tmpBitMap);
//						}


//						viewholder.receiveImgView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(url,MediaStore.Video.Thumbnails.MINI_KIND));
                        if (!url.startsWith("http://")) {
                            android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(url);
                            if (bitmap != null) {
                                viewholder.receiveImgView.setImageBitmap(bitmap);
                            } else if (viewholder.mediaThumb != null) {
                                Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
                                viewholder.receiveImgView.setImageBitmap(tmpBitMap);
                                SuperChatApplication.addBitmapToMemoryCache(url, tmpBitMap);
                            } else if (viewholder.mediaLocalPath != null) {
                                viewholder.receiveImgView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MINI_KIND));
                            }
                        }
                        viewholder.sVideoPlayImageView.setVisibility(View.GONE);
                        viewholder.rVideoPlayImageView.setVisibility(View.VISIBLE);
//						viewholder.receiverTime.setTextColor(context1.getResources().getColor(R.color.white));
//						viewholder.receiverTime.setBackgroundResource(R.drawable.chat_time_gradient);
//						viewholder.receiverTime.setPadding(10, 5, 10, 5);
                    }
                }

                if (url != null && url.startsWith("http://")) {

                    Log.d("ChatListAdapter", "media url found " + url);
                    viewholder.sVideoPlayImageView.setVisibility(View.GONE);
                    viewholder.rVideoPlayImageView.setVisibility(View.GONE);
                    if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()) {
                        viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                        viewholder.receiveImgView.setVisibility(View.GONE);
                        viewholder.leftFileTypeView.setVisibility(View.VISIBLE);
//						viewholder.receiveImgView.setLayoutParams(viewholder.layoutParams);
                        viewholder.receiveImgView.setBackgroundResource(R.drawable.pdf);
                        if (viewholder.getProcessingForURL(url) == null && viewholder.mediaLocalPath == null) {
                            Log.d("ChatListAdapter", "<<   started for - " + url);
                            Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, url};
                            viewholder.download(url, viewholder.messageType, viewholder.receiveImgView, viewholder.progressbar, params);
                        }
                    } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()) {
                        viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                        viewholder.receiveImgView.setVisibility(View.GONE);
//						viewholder.receiveImgView.setLayoutParams(viewholder.layoutParams);
                        viewholder.receiveImgView.setBackgroundResource(R.drawable.docs);
                        if (viewholder.getProcessingForURL(url) == null && viewholder.mediaLocalPath == null) {
                            Log.d("ChatListAdapter", "<<   started for - " + url);
                            Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, url};
                            viewholder.download(url, viewholder.messageType, viewholder.receiveImgView, viewholder.progressbar, params);
                        }
                    } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()) {
                        viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                        viewholder.receiveImgView.setVisibility(View.GONE);
//						viewholder.receiveImgView.setLayoutParams(viewholder.layoutParams);
                        viewholder.receiveImgView.setBackgroundResource(R.drawable.xls);
                        if (viewholder.getProcessingForURL(url) == null && viewholder.mediaLocalPath == null) {
                            Log.d("ChatListAdapter", "<<   started for - " + url);
                            Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, url};
                            viewholder.download(url, viewholder.messageType, viewholder.receiveImgView, viewholder.progressbar, params);
                        }
                    } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                        viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                        viewholder.receiveImgView.setVisibility(View.GONE);
//						viewholder.receiveImgView.setLayoutParams(viewholder.layoutParams);
                        viewholder.receiveImgView.setBackgroundResource(R.drawable.ppt);
                        if (viewholder.getProcessingForURL(url) == null && viewholder.mediaLocalPath == null) {
                            Log.d("ChatListAdapter", "<<   started for - " + url);
                            Object[] params = new Object[]{this, viewholder.key, cursor, viewholder.progressPercent, url};
                            viewholder.download(url, viewholder.messageType, viewholder.receiveImgView, viewholder.progressbar, params);
                        }
                    } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                        viewholder.contactLayoutReceiver.setVisibility(View.GONE);
                        viewholder.pollLayoutReceiver.setVisibility(TextView.GONE);
                        viewholder.receiveImgView.setBackgroundResource(0);
                        viewholder.playRecieverView.setVisibility(View.INVISIBLE);
                        viewholder.receiveImgView.setVisibility(View.GONE);
                        if (viewholder.getProcessingForURL(url) == null && viewholder.mediaLocalPath == null) {
                            Log.d("ChatListAdapter", "media url found for download " + url);
                            viewholder.playRecieverSeekBar.setVisibility(View.INVISIBLE);
                            viewholder.progressbar.setVisibility(View.GONE);
                            viewholder.progressPercent.setVisibility(View.GONE);
                            viewholder.leftImgProgressIndeterminate.setVisibility(View.GONE);
                            Object[] params = new Object[]{this,
                                    viewholder.key, cursor,
                                    viewholder.voiceLoadingPercent, url};
                            viewholder.download(url, viewholder.messageType,
                                    viewholder.playRecieverView,
                                    viewholder.voiceDownloadingBar, params);
                            viewholder.progressPercent.setVisibility(View.GONE);
                        }
                        viewholder.playRecieverView.setVisibility(View.INVISIBLE);
                        viewholder.playRecieverSeekBar.setVisibility(View.INVISIBLE);
                        viewholder.voiceLoadingPercent.setVisibility(View.GONE);
                        viewholder.voiceDownloadingBar.setVisibility(View.INVISIBLE);
                    } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()) {
                        if (viewholder.getProcessingForURL(url) == null && viewholder.mediaLocalPath == null) {
                            Object[] params = new Object[]{this,
                                    viewholder.key, cursor,
                                    viewholder.progressPercent, url};
                            viewholder.download(url, viewholder.messageType,
                                    viewholder.receiveImgView,
                                    viewholder.progressbar, params);
                        }
                        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(url);
                        if (bitmap != null) {
                            viewholder.receiveImgView.setImageBitmap(bitmap);
                        } else {
                            Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
                            viewholder.receiveImgView.setImageBitmap(tmpBitMap);
                            SuperChatApplication.addBitmapToMemoryCache(url, tmpBitMap);
                        }
                        if (bitmap != null) {
                            viewholder.sendImgView.setImageBitmap(bitmap);
                        } else {
                            Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
                            viewholder.sendImgView.setImageBitmap(tmpBitMap);
                            SuperChatApplication.addBitmapToMemoryCache(url, tmpBitMap);
                        }
                        viewholder.voiceLoadingPercent.setVisibility(View.GONE);
                        viewholder.voiceDownloadingBar.setVisibility(View.INVISIBLE);
                        viewholder.progressPercent.setVisibility(View.VISIBLE);
                        viewholder.progressbar.setVisibility(View.VISIBLE);
                        viewholder.leftImgProgressIndeterminate.setVisibility(View.GONE);
                        viewholder.voiceRecieverInnerLayout.setVisibility(View.INVISIBLE);
                    } else {
                        if (viewholder.getProcessingForURL(url) == null && viewholder.mediaLocalPath == null) {
                            Object[] params = new Object[]{this, viewholder.key, cursor,
                                    viewholder.progressPercent, url};
                            viewholder.download(url, viewholder.messageType,
                                    viewholder.receiveImgView,
                                    viewholder.progressbar, params);

                        }
                        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(url);
                        if (bitmap != null) {
                            viewholder.receiveImgView.setImageBitmap(bitmap);
                        } else {
                            if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()) {
                                viewholder.receiveImgView.setImageResource(R.drawable.xls);
                            } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal()) {
                                viewholder.receiveImgView.setImageResource(R.drawable.ppt);
                            } else {
                                if (viewholder.mediaThumb != null) {
                                    Bitmap tmpBitMap = createVideoThumbFromByteArray(viewholder.mediaThumb);
                                    viewholder.receiveImgView.setImageBitmap(tmpBitMap);
                                    SuperChatApplication.addBitmapToMemoryCache(url, tmpBitMap);
                                } else {
                                    viewholder.receiveImgView.setImageResource(R.drawable.default_bg_img);
//                                    String img_url = viewholder.mediaUrl.replace("get", "convertget") + "?height=50&width=50";
//                                    viewholder.receiveImgView.setImageURI(Uri.parse(img_url));
//                                    img_url = null;
                                }
                            }
                        }
                        viewholder.progressPercent.setVisibility(View.VISIBLE);
                        viewholder.progressbar.setVisibility(View.VISIBLE);
                    }
                    if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                        viewholder.receiverMsgText.setVisibility(View.GONE);
                        viewholder.progressbar.setVisibility(ProgressBar.GONE);
                        viewholder.progressPercent.setVisibility(ProgressBar.GONE);
                        viewholder.leftImgProgressIndeterminate.setVisibility(View.GONE);
                        if (progressValue <= 100) {
                            if (progressValue > 1 && progressValue < 100) {
                                viewholder.voiceDownloadIndeterminateBar.setVisibility(ProgressBar.GONE);
                                viewholder.voiceDownloadingBar.setVisibility(ProgressBar.VISIBLE);
                                viewholder.voiceLoadingPercent.setVisibility(View.VISIBLE);
                                viewholder.voiceDownloadingBar.setProgress(progressValue);
                                viewholder.voiceLoadingPercent.setText(String.valueOf(progressValue) + "%");
                            } else {
                                viewholder.voiceDownloadIndeterminateBar.setVisibility(ProgressBar.VISIBLE);
                                viewholder.voiceDownloadingBar.setVisibility(ProgressBar.GONE);
                                viewholder.voiceLoadingPercent.setVisibility(View.GONE);
                            }
                        } else {
                            viewholder.voiceDownloadIndeterminateBar.setVisibility(ProgressBar.GONE);
                            viewholder.voiceLoadingPercent.setVisibility(View.GONE);
                            viewholder.voiceDownloadingBar.setVisibility(ProgressBar.GONE);
                        }
                    } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal() || viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal()) {
                        viewholder.voiceDownloadingBar.setVisibility(ProgressBar.GONE);
                        viewholder.voiceDownloadIndeterminateBar.setVisibility(ProgressBar.GONE);
                        viewholder.voiceLoadingPercent.setVisibility(View.GONE);
                        viewholder.voiceDownloadingBar.setVisibility(ProgressBar.GONE);
                        if (progressValue <= 100) {
                            if (progressValue > 1 && progressValue < 100) {
                                viewholder.leftImgProgressIndeterminate.setVisibility(View.GONE);
                                viewholder.progressbar.setVisibility(ProgressBar.VISIBLE);
                                viewholder.progressPercent.setVisibility(ProgressBar.VISIBLE);
                                viewholder.progressPercent.setText(String.valueOf(progress) + "%");
                                viewholder.progressbar.setProgress(progressValue);
                            } else {
                                viewholder.leftImgProgressIndeterminate.setVisibility(View.VISIBLE);
                                viewholder.progressbar.setVisibility(ProgressBar.INVISIBLE);
                                viewholder.progressPercent.setVisibility(ProgressBar.GONE);
                            }
                        } else {
                            viewholder.progressbar.setVisibility(ProgressBar.GONE);
                            viewholder.progressPercent.setVisibility(ProgressBar.GONE);
                            viewholder.leftImgProgressIndeterminate.setVisibility(View.GONE);
                        }
                    } else {
                        viewholder.progressbar.setVisibility(ProgressBar.GONE);
                        viewholder.progressPercent.setVisibility(ProgressBar.GONE);
                        viewholder.leftImgProgressIndeterminate.setVisibility(View.GONE);
                        viewholder.voiceDownloadingBar.setVisibility(ProgressBar.GONE);
                        viewholder.voiceDownloadIndeterminateBar.setVisibility(ProgressBar.GONE);
                        viewholder.voiceLoadingPercent.setVisibility(View.GONE);
                        viewholder.voiceDownloadingBar.setVisibility(ProgressBar.GONE);

                        if (progressValue > 1 && progressValue < 100) {
                            ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                            ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.VISIBLE);
                            ((TextView) viewholder.leftFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.VISIBLE);
                            ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setProgress(progressValue);
                            ((TextView) viewholder.leftFileLayout.findViewById(R.id.file_loading_percent)).setText(String.valueOf(progress) + "%");
                        } else {
                            ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.VISIBLE);
                            ((TextView) viewholder.leftFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                            ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader)).setVisibility(View.GONE);
                    ((TextView) viewholder.leftFileLayout.findViewById(R.id.file_loading_percent)).setVisibility(View.GONE);
                    ((ProgressBar) viewholder.leftFileLayout.findViewById(R.id.id_file_loader_indeterminate)).setVisibility(View.GONE);
                    viewholder.progressbar.setVisibility(ProgressBar.GONE);
                    viewholder.voiceDownloadIndeterminateBar.setVisibility(ProgressBar.GONE);
                    viewholder.progressPercent.setVisibility(ProgressBar.GONE);
                    viewholder.leftImgProgressIndeterminate.setVisibility(View.GONE);
                    viewholder.voiceDownloadingBar.setVisibility(View.GONE);
                    if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal()) {
                        viewholder.voiceDownloadingBar.setVisibility(View.INVISIBLE);
                        viewholder.voiceLoadingPercent.setVisibility(View.GONE);
                        viewholder.sVideoPlayImageView.setVisibility(View.GONE);
                        viewholder.rVideoPlayImageView.setVisibility(View.GONE);
//						viewholder.receiverTime.setTextColor(context1.getResources().getColor(R.color.white));
//						viewholder.receiverTime.setBackgroundResource(R.drawable.chat_time_gradient);
//						viewholder.receiverTime.setPadding(10, 5, 10, 5);
                    }
                    if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) {
                        viewholder.contactLayoutReceiver.setVisibility(View.GONE);
                        viewholder.pollLayoutReceiver.setVisibility(TextView.GONE);
                        viewholder.locationLayoutReceiver.setVisibility(View.GONE);
                        viewholder.receiveImgView.setBackgroundResource(0);
                        viewholder.receiveImgView.setVisibility(View.GONE);
                        viewholder.voiceRecieverInnerLayout.setVisibility(View.VISIBLE);
                        viewholder.voiceRecieverInnerLayout.setTag("N");
                        viewholder.voiceRecieverInnerLayout.setOnLongClickListener(viewholder.onLongPressListener);
                        viewholder.playRecieverView.setVisibility(View.VISIBLE);
                        viewholder.playRecieverSeekBar.setVisibility(View.VISIBLE);
                        viewholder.playRecieverSeekBar.setTag(viewholder.key);
//						android.util.Log.d("ChatListAdapter", "onBind current. key: " + audioPlayerKey);
                        if (audioPlayerKey != null && audioPlayerKey.equals(viewholder.key)) {
                            viewholder.playRecieverSeekBar.setProgress(globalSeekBarValue);
                            viewholder.playRecieverSeekBar.setMax(globalSeekBarMaxValue);
//							android.util.Log.d("ChatListAdapter", "onBind current. " + globalSeekBarMaxValue+" , "+ globalSeekBarValue);
                        } else {
                            viewholder.playRecieverSeekBar.setProgress(0);
                            viewholder.playRecieverSeekBar.setMax(0);
                        }
                        viewholder.voiceLoadingPercent.setVisibility(View.INVISIBLE);
                        viewholder.voiceDownloadingBar.setVisibility(View.INVISIBLE);
                        viewholder.rVideoPlayImageView.setVisibility(View.INVISIBLE);
                    }
                    if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()) {
                        viewholder.playRecieverView.setVisibility(View.INVISIBLE);
                        viewholder.playRecieverSeekBar.setVisibility(View.INVISIBLE);
                        viewholder.voiceLoadingPercent.setVisibility(View.INVISIBLE);
                        viewholder.voiceDownloadingBar.setVisibility(View.INVISIBLE);
                        viewholder.rVideoPlayImageView.setVisibility(View.VISIBLE);
                        viewholder.rVideoPlayImageView.setTag("N");
                        viewholder.rVideoPlayImageView.setOnLongClickListener(viewholder.onLongPressListener);
                    }
                    if (viewholder.messageType != XMPPMessageType.atMeXmppMessageTypePdf.ordinal() && viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeAudio.ordinal() && viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()) {
                        viewholder.receiveImgView.setBackgroundResource(0);
//						Log.d(TAG, "bindView exception in url: "+url);
//						CompressImage compressImage = new CompressImage(context);
//						String tmpUrl = compressImage.compressImage(url);
//						setThumb(viewholder.receiveImgView, url,viewholder.mediaThumb);
                        setPicForCache(viewholder.receiveImgView, url);
//						viewholder.receiveImgView.setImageURI(Uri.parse(url));
                        viewholder.voiceDownloadingBar.setVisibility(View.INVISIBLE);
                        viewholder.voiceLoadingPercent.setVisibility(View.INVISIBLE);
                        viewholder.sVideoPlayImageView.setVisibility(View.GONE);
                        viewholder.rVideoPlayImageView.setVisibility(View.GONE);
//						viewholder.receiverTime.setTextColor(context1.getResources().getColor(R.color.white));
//						viewholder.receiverTime.setBackgroundResource(R.drawable.chat_time_gradient);
//						viewholder.receiverTime.setPadding(10, 5, 10, 5);
                    }
                }

            } else {
                viewholder.receiveImgView.setBackgroundResource(0);
                viewholder.progressPercent.setVisibility(View.GONE);
                viewholder.voiceLoadingPercent.setVisibility(View.GONE);
                viewholder.voiceDownloadingBar.setVisibility(View.INVISIBLE);
                viewholder.progressbar.setVisibility(View.GONE);
                viewholder.leftImgProgressIndeterminate.setVisibility(ProgressBar.GONE);
                viewholder.receiveImgView.setVisibility(View.GONE);
                viewholder.receiverMsgText.setVisibility(View.GONE);
                if (viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeContact.ordinal()
                        && viewholder.messageType != XMPPMessageType.atMeXmppMessageTypeLocation.ordinal()
                        && viewholder.messageType != XMPPMessageType.atMeXmppMessageTypePoll.ordinal()) {
                    viewholder.leftFileLayout.setVisibility(View.GONE);
                    viewholder.leftFileTypeView.setVisibility(View.GONE);
                    viewholder.locationLayout.setVisibility(View.GONE);
                    viewholder.contactLayout.setVisibility(View.GONE);
                    viewholder.pollLayout.setVisibility(TextView.GONE);
                    viewholder.locationLayoutReceiver.setVisibility(View.GONE);
                    viewholder.contactLayoutReceiver.setVisibility(View.GONE);
                    viewholder.pollLayoutReceiver.setVisibility(TextView.GONE);
                    viewholder.receiverMsgText.setVisibility(View.VISIBLE);
                    viewholder.receiverMsgText.setText(viewholder.message);
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeContact.ordinal()) {
                    viewholder.leftFileLayout.setVisibility(View.GONE);
                    viewholder.leftFileTypeView.setVisibility(View.GONE);
                    viewholder.locationLayoutReceiver.setVisibility(View.GONE);
                    viewholder.pollLayoutReceiver.setVisibility(View.GONE);
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypePoll.ordinal()) {
                    viewholder.leftFileLayout.setVisibility(View.GONE);
                    viewholder.leftFileTypeView.setVisibility(View.GONE);
                    viewholder.contactLayoutReceiver.setVisibility(View.GONE);
                    viewholder.locationLayoutReceiver.setVisibility(View.GONE);
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                } else if (viewholder.messageType == XMPPMessageType.atMeXmppMessageTypeLocation.ordinal()) {
                    viewholder.leftFileLayout.setVisibility(View.GONE);
                    viewholder.leftFileTypeView.setVisibility(View.GONE);
                    viewholder.contactLayoutReceiver.setVisibility(View.GONE);
                    viewholder.pollLayoutReceiver.setVisibility(TextView.GONE);
                    viewholder.voiceRecieverInnerLayout.setVisibility(View.GONE);
                }

                viewholder.sVideoPlayImageView.setVisibility(View.GONE);
                viewholder.rVideoPlayImageView.setVisibility(View.GONE);
//				viewholder.receiverTime.setTextColor(context1.getResources().getColor(R.color.text_color_on_white_light));
//				viewholder.receiverTime.setBackgroundResource(0);
//				viewholder.receiverTime.setPadding(0, 0, 0, 0);
                viewholder.receiverLayout.setTag("Y");
                viewholder.receiverLayout.setOnLongClickListener(viewholder.onLongPressListener);
            }
            viewholder.receiverTime.setText(msgTime);
            if (isSharedIDMessage && (iChatPref.isDomainAdminORSubAdmin()) || isSharedIDAdmin) {
//				viewholder.receiverLayout.setTag(viewholder.groupMsgSenderName);
//				viewholder.replySharedID.setTag(viewholder.groupMsgSenderName);
                viewholder.replySharedID.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(SuperChatApplication.context, ChatListScreen.class);
                        intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, SharedPrefManager.getInstance().getUserServerName(viewholder.groupMsgSenderName));
                        intent.putExtra(DatabaseConstants.USER_NAME_FIELD, viewholder.groupMsgSenderName);
                        ((ChatListScreen) context).startActivity(intent);
                        return;
                    }
                });
            }

        }
        startShowingProgress();
        view.setTag(viewholder);
    }

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    public void setProfilePic(RoundedImageView view, ImageView defaultView, String userName, String displayName) {
        String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02
        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
        if (bitmap != null) {
            view.setVisibility(View.VISIBLE);
            view.setImageBitmap(bitmap);
            view.setBackgroundColor(Color.TRANSPARENT);
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            view.setTag(filename);
            defaultView.setVisibility(View.GONE);
        } else if (groupPicId != null && !groupPicId.equals("") && !groupPicId.equals("clear")) {
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            view.setTag(filename);
            File file1 = new File(filename);
            if (file1.exists()) {
//				view.setImageURI(Uri.parse(filename));
                defaultView.setVisibility(View.INVISIBLE);
                view.setVisibility(View.VISIBLE);
                setThumb(view, filename, groupPicId);
//				view.setBackgroundDrawable(null);
            } else {
                //Downloading the file
                defaultView.setVisibility(View.INVISIBLE);
                view.setVisibility(View.VISIBLE);
//				view.setImageResource(R.drawable.group_icon);
//				(new ProfilePicDownloader()).download(Constants.media_get_url+groupPicId+".jpg", (RoundedImageView)view, null);
                if (Build.VERSION.SDK_INT >= 11)
                    new BitmapDownloader((RoundedImageView) view, defaultView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
                else
                    new BitmapDownloader((RoundedImageView) view, defaultView).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
            }
//			iconText.setVisibility(View.INVISIBLE);
        } else {
            try {
                if (displayName != null && displayName.length() > 0) {
                    String name_alpha = String.valueOf(displayName.charAt(0));
                    if (displayName.contains(" ") && displayName.indexOf(' ') < (displayName.length() - 1))
                        name_alpha += displayName.substring(displayName.indexOf(' ') + 1).charAt(0);
                    TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(displayName));
                    view.setVisibility(View.GONE);
                    defaultView.setVisibility(View.VISIBLE);
                    defaultView.setImageDrawable(drawable);
                    defaultView.setBackgroundColor(Color.TRANSPARENT);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                defaultView.setVisibility(View.GONE);
                view.setVisibility(View.GONE);
            }
        }

    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            if (url != null) {
                if (result != null)
                    SuperChatApplication.addBitmapToMemoryCache(url, result);
                processingMap.put(url, null);
            }
        }

    }

    private void startShowingProgress() {
        if (progressTimer == null) {
            (progressTimer = new Timer()).schedule((progressTask = new ProgressTask(null)), 1000, 500);
        }
    }

    private void setPicForCache(ImageView view, String cacheIdPath) {
        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(cacheIdPath);
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        } else {
            File file1 = new File(cacheIdPath);
            //			Log.d(TAG, "PicAvailibilty: "+ Uri.parse(filename)+" , "+filename+" , "+file1.exists());
            if (file1 != null && file1.exists()) {
//				view.setImageURI(Uri.parse(filename));
                setThumbForCache(view, cacheIdPath);
            } else {

            }
        }
    }

    public static Bitmap rotateImageForCache(String path, Bitmap bm) {
        int orientation = 1;
        try {
            ExifInterface exifJpeg = new ExifInterface(path);
            orientation = exifJpeg.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

////			orientation = Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (orientation != ExifInterface.ORIENTATION_NORMAL) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        }

        return bm;
    }

    private void setThumbForCache(ImageView imageViewl, String path) {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 1;
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeFile(path, bfo);
            if (bm.getWidth() > 400)
                bm = ThumbnailUtils.extractThumbnail(bm, 400, 400);
            bm = rotateImageForCache(path, bm);
//		    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
        } catch (Exception ex) {

        } catch (OutOfMemoryError oom) {

        }
        if (bm != null) {
            imageViewl.setImageBitmap(bm);
            SuperChatApplication.addBitmapToMemoryCache(path, bm);
            cacheKeys.add(path);
        } else {
//	    	try{
//	    		imageViewl.setImageURI(Uri.parse(path));
//	    	}catch(Exception e){
//
//	    	}
        }
    }

    private void setThumb(ImageView imageViewl, String path, String thumb) {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 1;
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeFile(path, bfo);
            if (bm.getWidth() > 400)
                bm = ThumbnailUtils.extractThumbnail(bm, 400, 400);
            bm = rotateImage(path, bm);
//	    bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
        } catch (Exception ex) {

        }
        if (bm != null) {
            imageViewl.setImageBitmap(bm);
            SuperChatApplication.addBitmapToMemoryCache(thumb, bm);
        } else {
            try {
                bm = createThumbFromByteArray(thumb);
                SuperChatApplication.addBitmapToMemoryCache(thumb, bm);
                imageViewl.setImageBitmap(bm);
            } catch (Exception e) {

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
        if (orientation != ExifInterface.ORIENTATION_NORMAL) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            Matrix matrix = new Matrix();
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                matrix.postRotate(90);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                matrix.postRotate(180);
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                matrix.postRotate(270);
            }
            return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        }

        return bm;
    }

    public void updateDataWithCursor(Object[] params)// localpath, key, cursor
    {
        // update the cursor with the given local path (http url is in db, now
        // image has downloaded to local so update local in DB)
        if (params != null && params.length == 3) {
            String localImagePath = (String) params[0];
            String messageID = (String) params[1];

            // update data here
            // ChatDBWrapper.getInstance().updateMessageMediaLocalPath(messageID,
            // Constants.PIC_SEP + localImagePath);
            ChatDBWrapper.getInstance().updateMessageMediaLocalPath(messageID,
                    localImagePath);
        }
    }

    // public void updateDataWithCursor(String url, String localImagePath,
    // ImageView imgView)
    // {
    // //update the cursor with the given local path (http url is in db, now
    // image has downloaded to local so update local in DB)
    // if(url != null && localImagePath != null)
    // {
    // //update data here
    // imgView.setTag(localImagePath);
    // ChatDBWrapper.getInstance().updateMessageData(url, Constants.PIC_SEP +
    // localImagePath);
    // }
    // }
    public void updateDataWithCursor(String localImagePath, String messageID,
                                     ImageView imgView) {
        // update the cursor with the given local path (http url is in db, now
        // image has downloaded to local so update local in DB)
        if (localImagePath != null && messageID != null) {
            // update data here
            // imgView.setTag(localImagePath);
            // ChatDBWrapper.getInstance().updateMessageMediaLocalPath(messageID,
            // Constants.PIC_SEP + localImagePath);
            ChatDBWrapper.getInstance().updateMessageMediaLocalPath(messageID,
                    localImagePath);
            if (refreshListener != null)
                refreshListener.notifyChatRecieve(null, null);
            // if(chatAdaptor != null)
            // chatAdaptor.getCursor().requery();
            // if(imageDownloader != null)
            // imageDownloader.clearMap();
        }
        Log.d("ChatListAdapter", "<<   updated msgid -" + messageID
                + ", with path-" + (localImagePath));
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return super.getCount();
    }

//	private GoogleMap initializeMap(int id) {
//		GoogleMap googleMap = null;
//        if (googleMap == null) {
//            googleMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(id)).getMap();
//            // check if map is created successfully or not
//            if (googleMap == null) {
//                Toast.makeText((ChatListScreen)context, "Sorry! unable to create maps", Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//        return googleMap;
//    }

    public View newView(Context context1, Cursor cursor, ViewGroup viewgroup) {

        final View view = LayoutInflater.from(context).inflate(layout, null); // mInflater.inflate(layout, viewgroup, false);//
        final ViewHolder viewholder = new ViewHolder();
        viewholder.messageType = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD));
        viewholder.leftRightCompositeView = (RelativeLayout) view.findViewById(R.id.left_right_composite);
        viewholder.senderLayout = (LinearLayout) view.findViewById(R.id.right_block_linear_layout);
        viewholder.rightFileLayout = (RelativeLayout) view.findViewById(R.id.id_right_file_layout);
        viewholder.leftFileLayout = (RelativeLayout) view.findViewById(R.id.id_left_file_layout);
        viewholder.rightFileLayout.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.leftFileLayout.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.rightFileTypeView = (TextView) view.findViewById(R.id.id_right_file_type);
        viewholder.leftFileTypeView = (TextView) view.findViewById(R.id.id_left_file_type);
        viewholder.leftFileLayout.setOnClickListener(viewholder.onImageClickListener);
        viewholder.rightFileLayout.setOnClickListener(viewholder.onImageClickListener);
        viewholder.receiverLayout = (LinearLayout) view.findViewById(R.id.left_block_layout);
        viewholder.leftPersonPicLayout = (RelativeLayout) view.findViewById(R.id.id_left_pic_layout);
        viewholder.leftPersonPic = (RoundedImageView) view.findViewById(R.id.id_left_pic);
        viewholder.leftPersonDefaultPic = (ImageView) view.findViewById(R.id.id_left_default_pic);
        viewholder.voiceRecieverInnerLayout = (RelativeLayout) view.findViewById(R.id.left_audio_control_layout);
        viewholder.voiceSenderInnerLayout = (RelativeLayout) view.findViewById(R.id.right_audio_control_layout);
        viewholder.leftAudioBtnLayout = (LinearLayout) view.findViewById(R.id.left_audio_btn_layout);

        viewholder.rightAudioBtnLayout = (LinearLayout) view.findViewById(R.id.right_audio_btn_layout);
        viewholder.voiceSenderLayout = (LinearLayout) view.findViewById(R.id.right_audio_layout);
        viewholder.playSenderView = (ImageView) view.findViewById(R.id.send_media_play);
        viewholder.playSenderSeekBar = (SeekBar) view.findViewById(R.id.send_mediavoicePlayingDialog_progressbar);
//		viewholder.playSenderTimeText = (TextView) view.findViewById(R.id.send_audio_counter_time);
        viewholder.playSenderMaxTimeText = (TextView) view.findViewById(R.id.send_audio_counter_max);

        viewholder.playRecieverView = (ImageView) view.findViewById(R.id.media_play);
        viewholder.playRecieverSeekBar = (SeekBar) view.findViewById(R.id.mediavoicePlayingDialog_progressbar);
        viewholder.playRecieverTimeText = (TextView) view.findViewById(R.id.audio_counter_time);
        viewholder.playRecieverMaxTimeText = (TextView) view.findViewById(R.id.audio_counter_max);

        viewholder.sendImgView = (ImageView) view.findViewById(R.id.right_image_view);
        viewholder.sendTagView = (EmojiconTextView) view.findViewById(R.id.right_image_tag);
        viewholder.recieveTagView = (EmojiconTextView) view.findViewById(R.id.left_image_tag);
        viewholder.recieveAudioTagView = (EmojiconTextView) view.findViewById(R.id.audio_tag);

        viewholder.senderAudioTagView = (EmojiconTextView) view.findViewById(R.id.audio_tag_sender);
        viewholder.senderMsgText = (EmojiconTextView) view.findViewById(R.id.right_block_text);
        viewholder.senderMsgText.setEmojiconSizeInDip(40);
        viewholder.senderTime = (TextView) view.findViewById(R.id.right_block_time);
        viewholder.messageStatusView = (TextView) view.findViewById(R.id.imageview_tick);
        viewholder.sVideoPlayImageView = (ImageView) view.findViewById(R.id.sender_video_play);
        viewholder.sDateLayout = (LinearLayout) view.findViewById(R.id.id_right_audio_and_text_layout);
        viewholder.rDateLayout = (RelativeLayout) view.findViewById(R.id.receive_date_layout);
        //Sender side - Contact
        viewholder.contactLayout = (LinearLayout) view.findViewById(R.id.contact_layout);
        viewholder.contactIcon = (RoundedImageView) view.findViewById(R.id.contact_icon);
        viewholder.contactName = (TextView) view.findViewById(R.id.contact_name);

        //Sender side - Poll
        viewholder.pollLayout = (LinearLayout) view.findViewById(R.id.poll_layout);
        viewholder.pollTitle = (TextView) view.findViewById(R.id.poll_title);
        viewholder.pollMessage = (TextView) view.findViewById(R.id.poll_message);

        //Sender side - Location
        viewholder.locationLayout = (RelativeLayout) view.findViewById(R.id.location_layout);
        viewholder.mapviewSender = (ImageView) view.findViewById(R.id.mapview_sender);
        viewholder.locationLayout.setOnLongClickListener(viewholder.onLongPressListener);

//		viewholder.mapSender = ((SupportMapFragment) ((ChatListScreen)context).getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMap();

        viewholder.locationName = (TextView) view.findViewById(R.id.location_name);
        viewholder.locationNameAddress = (TextView) view.findViewById(R.id.location_address);


        //Receiver side - Contact
        viewholder.contactLayoutReceiver = (LinearLayout) view.findViewById(R.id.contact_layout_r);
        viewholder.contactIconReceiver = (RoundedImageView) view.findViewById(R.id.contact_icon_r);
        viewholder.contactNameReceiver = (TextView) view.findViewById(R.id.contact_name_r);

        //Receiver side - Contact
        viewholder.pollLayoutReceiver = (LinearLayout) view.findViewById(R.id.poll_layout_r);
        viewholder.pollTtitleReceiver = (TextView) view.findViewById(R.id.poll_title_r);
        viewholder.pollMessageReceiver = (TextView) view.findViewById(R.id.poll_message_r);

        //Receiver side - Location
        viewholder.locationLayoutReceiver = (RelativeLayout) view.findViewById(R.id.location_layout_r);
        viewholder.locationLayoutReceiver.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.mapviewReceiver = (ImageView) view.findViewById(R.id.mapview_receiver);
        viewholder.locationNameReceiver = (TextView) view.findViewById(R.id.location_name_r);
        viewholder.locationNameAddressReceiver = (TextView) view.findViewById(R.id.location_address_r);

        //Receiver side - Reply Shared ID
        viewholder.replySharedID = (RelativeLayout) view.findViewById(R.id.receive_reply_layout);

        viewholder.receiverPersonName = (TextView) view.findViewById(R.id.reciever_name_text);

        viewholder.receiveImgView = (ImageView) view.findViewById(R.id.left_image_view);
        viewholder.progressbar = (ProgressBar) view.findViewById(R.id.progress_image_loader);
        viewholder.leftImgProgressIndeterminate = (ProgressBar) view.findViewById(R.id.progress_image_indeterminate);
        viewholder.rightImgProgressBar = (ProgressBar) view.findViewById(R.id.right_progress_image_loader);
        viewholder.rightImgProgressIndeterminate = (ProgressBar) view.findViewById(R.id.right_progress_image_indeterminate);
        viewholder.voiceDownloadingBar = (ProgressBar) viewholder.receiverLayout.findViewById(R.id.progress_voice_loader);
        viewholder.voiceDownloadIndeterminateBar = (ProgressBar) viewholder.receiverLayout.findViewById(R.id.progress_voice_indeterminate);

        viewholder.voiceLoadingPercent = (TextView) view.findViewById(R.id.progress_voice_loading_percent);
        viewholder.progressPercent = (TextView) view.findViewById(R.id.loading_percent);
        viewholder.rightImgProgressPercent = (TextView) view.findViewById(R.id.right_loading_percent);

        viewholder.receiverMsgText = (EmojiconTextView) view.findViewById(R.id.left_block_text);
        viewholder.receiverMsgText.setEmojiconSizeInDip(40);
        viewholder.receiverTime = (TextView) view.findViewById(R.id.left_block_time);
        viewholder.rVideoPlayImageView = (ImageView) view.findViewById(R.id.receiver_video_play);
        viewholder.unsentAlertView = (ImageView) view.findViewById(R.id.unsent_alert);

        viewholder.dateLayout = (RelativeLayout) view.findViewById(R.id.midle_block_layout);
//		viewholder.dateLayout.getBackground().setAlpha(98);
        viewholder.dateText = (EmojiconTextView) view.findViewById(R.id.midle_block_text);
        viewholder.dateText.setEmojiconSizeInDip(40);
        viewholder.senderCheckBox = (CheckBox) view.findViewById(R.id.sender_sel_box);
        viewholder.receiverCheckBox = (CheckBox) view.findViewById(R.id.receiver_sel_box);

        viewholder.senderCheckBox.setOnClickListener(viewholder.onCheckeClickListener);
        viewholder.senderLayout.setOnClickListener(viewholder.onSenderBubbleClickListener);
        viewholder.receiverCheckBox.setOnClickListener(viewholder.onCheckeClickListener);
        viewholder.receiverLayout.setOnClickListener(viewholder.onReceiverBubbleClickListener);
        viewholder.sVideoPlayImageView.setOnClickListener(viewholder.onImageClickListener);
        viewholder.rVideoPlayImageView.setOnClickListener(viewholder.onImageClickListener);
        viewholder.sendImgView.setOnClickListener(viewholder.onImageClickListener);
        viewholder.receiveImgView.setOnClickListener(viewholder.onImageClickListener);
        viewholder.playSenderView.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.playRecieverView.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.leftAudioBtnLayout.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.rightAudioBtnLayout.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.voiceDownloadingBar.setVisibility(View.INVISIBLE);
        viewholder.playRecieverView.setVisibility(View.INVISIBLE);
        viewholder.playRecieverSeekBar.setVisibility(View.INVISIBLE);
        viewholder.leftPersonPic.setOnClickListener(viewholder.onImageClickListener);
        viewholder.leftPersonDefaultPic.setOnClickListener(viewholder.onImageClickListener);

        /////////////////for handling message selection purpose only ////////////////////
        viewholder.senderMsgText.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.receiverMsgText.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.leftAudioBtnLayout.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.rightAudioBtnLayout.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.playSenderSeekBar.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.voiceRecieverInnerLayout.setOnClickListener(viewholder.onVoiceClickListener);
        viewholder.voiceSenderInnerLayout.setOnClickListener(viewholder.onVoiceClickListener);

        viewholder.playSenderSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        viewholder.playRecieverSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
//        viewholder.playSenderSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                viewholder.listItemSelection();
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//        });
//        viewholder.playRecieverSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                viewholder.listItemSelection();
//            }
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//        });
        viewholder.playRecieverSeekBar.setOnClickListener(viewholder.onVoiceClickListener);

        viewholder.sVideoPlayImageView.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.rVideoPlayImageView.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.playSenderView.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.playSenderSeekBar.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.playRecieverSeekBar.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.playRecieverView.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.leftAudioBtnLayout.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.rightAudioBtnLayout.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.senderMsgText.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.receiverMsgText.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.sendImgView.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.voiceSenderInnerLayout.setOnLongClickListener(viewholder.onLongPressListener);
        viewholder.voiceRecieverInnerLayout.setOnLongClickListener(viewholder.onLongPressListener);
/////////////////for handling message selection purpose only ////////////////////
//		view.setOnLongClickListener(viewholder.onLongPressListener);
        view.setTag(viewholder);
        return view;
    }

    LinearLayout media_play_layout;
    public boolean availableVoice = false;
    SeekBar baradd;
    TextView total_autio_time, played_autio_time;
    public boolean isAudio = false;
    boolean voiceIsPlaying;

    private void playAvailableVoice() {
        // availableVoice = true;
        // // mVoiceMedia.startPlaying(mVoicePath, null);
        // media_play_layout.setVisibility(View.VISIBLE);
        // baradd = (SeekBar) media_play_layout
        // .findViewById(R.id.mediavoicePlayingDialog_progressbar);
        // if (((ImageView) media_play_layout.findViewById(R.id.media_play)) !=
        // null)
        // ((ImageView)
        // media_play_layout.findViewById(R.id.media_play)).setOnClickListener(playerClickEvent);
        //
        // total_autio_time = ((TextView) media_play_layout
        // .findViewById(R.id.audio_counter_max));
        // played_autio_time = ((TextView) media_play_layout
        // .findViewById(R.id.audio_counter_time));
        // total_autio_time.setText("00:00)");
        // played_autio_time.setText("(00:00 of ");
        // // openPlayScreen(Downloader.getInstance().getPlayUrl(mVoicePath),
        // false, media_play_layout);
    }

    private void playAvailableVoice(final int postion, final String pathValue) {
        // try {
        //
        // new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // // TODO Auto-generated method stub
        // availableVoice = true;
        // // AssetFileDescriptor descriptor =
        // // getAssets().openFd(animationSound.get(postion));
        //
        // // m.setDataSource(descriptor.getFileDescriptor(),
        // // descriptor.getStartOffset(), descriptor.getLength());
        // // String path="assets/"+animationSound.get(postion);
        // // mVoicePath
        // // =path;//descriptor.getFileDescriptor().toString();
        //
        // //
        // System.out.println("mVoicePath=="+mVoicePath+"descriptor=="+descriptor);
        // // descriptor.close();
        // String path = Environment.getExternalStorageDirectory()
        // .getAbsolutePath() + gifpath;
        // File file = null;
        // if (pathValue == null)
        // file = new File(path + animationSound.get(postion));
        // else
        // file = new File(path + pathValue);
        //
        // // System.out.println("postion====="+postion
        // // +"pathValue==="+pathValue
        // // +"file====="+file.getAbsolutePath());
        // // ReadWriteUtill.readBytesFromFile(file);
        // // InputStream input =
        // // getAssets().open(animationSound.get(postion));
        //
        // try {
        // mVoiceMedia.startPlayingWithByte(ReadWriteUtill
        // .readBytesFromFile(file));
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // // mVoiceMedia.startNewPlaying(mVoicePath, null,false);
        // }
        // }).start();
        //
        // } catch (Exception e) {
        // // System.out.println("onerroe=======3");
        // e.printStackTrace();
        // if (fullDialog != null && fullDialog.isShowing()) {
        // fullDialog.dismiss();
        // }
        //
        // }

        // System.out.println("playing.......");
    }

    // private View.OnClickListener playerClickEvent = new OnClickListener() {
    //
    // @Override
    // public void onClick(final View v) {
    // switch (v.getId()) {
    // case R.id.media_play:
    // ImageView imageView1 = null;
    // try {
    //
    // imageView1 = (ImageView) media_play_layout
    // .findViewById(R.id.media_play);
    // if (((String) v.getTag()).equals("PLAY")) {
    // new Thread(new Runnable() {
    //
    // @Override
    // public void run() {
    // try {
    //
    // // baradd.setTag(new Stats(object)) ;
    // TextView tv = (TextView) media_play_layout
    // .findViewById(R.id.streemStatus);
    // tv.setTextColor(((ChatListScreen)context).getResources().getColor(R.color.sub_heading));
    // if (RTMediaPlayer.getUrl() != null)
    // RTMediaPlayer.start();
    // else {
    // try {
    // //
    // RTMediaPlayer._startPlay(v.getTag().toString(), v);
    //
    // } catch (Exception e) {
    // // e.print
    // }
    // }
    // } catch (Exception e) {
    // }
    // }
    // }).start();
    // imageView1.setBackgroundResource(R.drawable.addpause);
    // imageView1.setTag("PAUSE");
    //
    // } else if (((String) v.getTag()).equals("PAUSE")) {
    // // imageView1.setBackgroundResource(R.drawable.addplay);
    // // imageView1.setTag("PLAY");
    // // RTMediaPlayer.pause();
    // // imageView1 = (ImageView)
    // media_play_layout.findViewById(R.id.media_pause_play);
    // // imageView1.setVisibility(View.INVISIBLE);
    // }
    // } catch (Exception e) {
    // }
    // break;
    // case R.id.media_stop_play:
    // media_play_layout.setVisibility(View.GONE);
    // voiceIsPlaying = false;
    // RTMediaPlayer.reset();
    // RTMediaPlayer.clear();
    // break;
    // }
    // }
    // };

    public void resetProgress() {
        // ProgressBar progressBar = (ProgressBar)
        // findViewById(R.id.progressbar);
        baradd.setVisibility(View.VISIBLE);
        baradd.setProgress(0);
        baradd.invalidate();
    }

    private Bitmap getThumbnailImage(String imagePath) {
        final int THUMBSIZE = 64;
        Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(imagePath), THUMBSIZE, THUMBSIZE);
        return thumbImage;
    }

    private byte[] getByteArrayOfThumbnail(String imagePath) {
        // final int THUMBNAIL_HEIGHT = 48;
        // final int THUMBNAIL_WIDTH = 66;
        final int THUMBSIZE = 64;
        Bitmap imageBitmap = ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(imagePath), THUMBSIZE, THUMBSIZE);

        // Bitmap imageBitmap = BitmapFactory.decodeByteArray(mImageData, 0,
        // mImageData.length);
        Float width = new Float(imageBitmap.getWidth());
        Float height = new Float(imageBitmap.getHeight());
        Float ratio = width / height;
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap,
                (int) (THUMBSIZE * ratio), THUMBSIZE, false);

        // int padding = (THUMBSIZE - imageBitmap.getWidth())/2;
        // imageView.setPadding(padding, 0, padding, 0);
        // imageView.setImageBitmap(imageBitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return byteArray;
    }

    private Bitmap createThumbFromByteArray(String baseData) {
        Bitmap bmp = null;
        byte[] data = MyBase64.decode(baseData);
        if (data != null)
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bmp;
    }

    private Bitmap createVideoThumbFromByteArray(String baseData) {
        if (baseData == null)
            return null;
        Bitmap bmp = null;
        byte[] data = Base64.decode(baseData, Base64.DEFAULT);
        if (data != null)
            bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bmp;
    }

    Dialog shortProfileDialog = null;
    boolean shortProfile;

    public void showRWAProfile(final UserProfileModel objUserModel) {
        try {
            shortProfileDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            shortProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//			poll.setCanceledOnTouchOutside(false);
            shortProfileDialog.setContentView(R.layout.user_rwa_profile);

            Window window = shortProfileDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
//            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            shortProfileDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // dialog dismiss without button press
                    shortProfileDialog.dismiss();
                }
            });

            final ImageView callView = (ImageView) shortProfileDialog.findViewById(R.id.id_rwa_call_btn);
            final RoundedImageView photoView = (RoundedImageView) shortProfileDialog.findViewById(R.id.id_profile_pic);
            final ImageView msgBtnView = (ImageView) shortProfileDialog.findViewById(R.id.id_rwa_msg_btn);
            final ImageView cancel = (ImageView) shortProfileDialog.findViewById(R.id.id_cancel);
            final TextView name = (TextView) shortProfileDialog.findViewById(R.id.id_name);
            final ImageView fullProfileBtn = (ImageView) shortProfileDialog.findViewById(R.id.id_full_profile_view);
            final TextView addressView1 = (TextView) shortProfileDialog.findViewById(R.id.id_address1);
            final TextView addressView2 = (TextView) shortProfileDialog.findViewById(R.id.id_address2);
            String display_name = objUserModel.iName;
            if (display_name != null && display_name.trim().length() > 0)
                name.setText(display_name);
            String address1 = null;
            if (objUserModel.flatNumber != null && !objUserModel.flatNumber.equals(""))
                address1 = objUserModel.flatNumber + ", ";
            if (objUserModel.buildingNumber != null && !objUserModel.buildingNumber.equals(""))
                address1 = address1 + objUserModel.buildingNumber + ",";
            if (address1 == null)
                addressView1.setVisibility(View.GONE);
            else {
                addressView1.setVisibility(View.VISIBLE);
                addressView1.setText(address1);
            }

            String address2 = null;
            if (objUserModel.address != null && !objUserModel.address.equals(""))
                address2 = objUserModel.address;
            if (address2 == null)
                addressView2.setVisibility(View.GONE);
            else {
                addressView2.setVisibility(View.VISIBLE);
                addressView2.setText(address2);
            }
//            if(iChatPref.isBlocked(objUserModel.iUserName)){
//            	messageView.setBackgroundResource(R.drawable.message_gray);
//            	blockView.setBackgroundResource(R.drawable.unblock_button);
//        	}else{
//        		messageView.setBackgroundResource(R.drawable.message_blue);
//        		blockView.setBackgroundResource(R.drawable.block_button);
//        	}
            if (objUserModel.imageFileId != null) {
                android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(objUserModel.imageFileId);
                if (bitmap != null) {
                    photoView.setImageBitmap(bitmap);
                    photoView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= 11)
                                new BitmapDownloader(context, (ImageView) v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objUserModel.imageFileId, BitmapDownloader.PIC_VIEW_REQUEST);
                            else
                                new BitmapDownloader(context, (ImageView) v).execute(objUserModel.imageFileId, BitmapDownloader.PIC_VIEW_REQUEST);
                        }
                    });
                }
            }
            fullProfileBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//					shortProfileDialog.dismiss();
                    Intent intent = new Intent(context, ProfileScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.CHAT_USER_NAME, objUserModel.iUserName);
                    bundle.putString(Constants.CHAT_NAME, objUserModel.iName);
                    intent.putExtras(bundle);
                    ((ChatListScreen) context).startActivity(intent);
                }
            });
            cancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    shortProfileDialog.dismiss();
                }
            });
            callView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (iChatPref.isDNC(objUserModel.iUserName) && !iChatPref.isDomainAdminORSubAdmin()) {
                        Toast.makeText(context, context.getResources().getString(R.string.dnc_alert), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if ((((ChatListScreen) context).mSinchServiceInterface) != null) {
                        try {
//                            Call call = ((ChatListScreen) context).mSinchServiceInterface.callUserWithHeader(objUserModel.iUserName, HomeScreen.createHeaderForCalling(objUserModel.iUserName));
                            Call call = ((ChatListScreen) context).mSinchServiceInterface.callUser(objUserModel.iUserName);
                            String callId = call.getCallId();

                            Intent callScreen = new Intent(context, CallScreenActivity.class);
                            callScreen.putExtra(SinchService.CALL_ID, callId);
                            ((ChatListScreen) context).startActivity(callScreen);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            msgBtnView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (iChatPref.isBlocked(objUserModel.iUserName)) {
                        Toast.makeText(context, "User blocked!", Toast.LENGTH_SHORT).show();
                    } else {
                        shortProfileDialog.dismiss();
                        ((ChatListScreen) context).finish();
                        Intent intent = new Intent(context, ChatListScreen.class);
                        intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, objUserModel.iName);
                        intent.putExtra(DatabaseConstants.USER_NAME_FIELD, objUserModel.iUserName);
                        ((ChatListScreen) context).startActivity(intent);
                    }
                }
            });

            shortProfileDialog.show();
            shortProfile = true;
        } catch (Exception ex) {

        }
    }

    public void showShortProfile(final UserProfileModel objUserModel) {
        try {
            shortProfileDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            shortProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

//			poll.setCanceledOnTouchOutside(false);
            shortProfileDialog.setContentView(R.layout.user_profile_short);

            Window window = shortProfileDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
//            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            shortProfileDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // dialog dismiss without button press
                    shortProfileDialog.dismiss();
                }
            });

            final ImageView messageView = (ImageView) shortProfileDialog.findViewById(R.id.id_send_message);
            final RoundedImageView photoView = (RoundedImageView) shortProfileDialog.findViewById(R.id.id_profile_pic);
            final ImageView blockView = (ImageView) shortProfileDialog.findViewById(R.id.id_block);
            final ImageView cancel = (ImageView) shortProfileDialog.findViewById(R.id.id_cancel);
            final TextView name = (TextView) shortProfileDialog.findViewById(R.id.id_name);
            final TextView birthdayView = (TextView) shortProfileDialog.findViewById(R.id.id_birthday);
            final TextView addressView = (TextView) shortProfileDialog.findViewById(R.id.id_address);
            String display_name = objUserModel.iName;
            if (display_name != null && display_name.trim().length() > 0)
                name.setText(display_name);
            if (objUserModel.dob != null && objUserModel.dob.contains("-"))
                birthdayView.setText(objUserModel.dob.substring(0, objUserModel.dob.lastIndexOf("-")));
            if (objUserModel.address != null)
                addressView.setText(objUserModel.address);
            if (iChatPref.isBlocked(objUserModel.iUserName)) {
                messageView.setBackgroundResource(R.drawable.message_gray);
                blockView.setBackgroundResource(R.drawable.unblock_button);
            } else {
                messageView.setBackgroundResource(R.drawable.message_blue);
                blockView.setBackgroundResource(R.drawable.block_button);
            }
            if (objUserModel.imageFileId != null) {
                android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(objUserModel.imageFileId);
                if (bitmap != null) {
                    photoView.setImageBitmap(bitmap);
                    photoView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= 11)
                                new BitmapDownloader(context, (ImageView) v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objUserModel.imageFileId, BitmapDownloader.PIC_VIEW_REQUEST);
                            else
                                new BitmapDownloader(context, (ImageView) v).execute(objUserModel.imageFileId, BitmapDownloader.PIC_VIEW_REQUEST);
                        }
                    });
                }
            }
            cancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    shortProfileDialog.dismiss();
                }
            });
            messageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (iChatPref.isBlocked(objUserModel.iUserName)) {
                        Toast.makeText(context, "User blocked!", Toast.LENGTH_SHORT).show();
                    } else {
                        shortProfileDialog.dismiss();
                        ((ChatListScreen) context).finish();
                        Intent intent = new Intent(context, ChatListScreen.class);
                        intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, objUserModel.iName);
                        intent.putExtra(DatabaseConstants.USER_NAME_FIELD, objUserModel.iUserName);
                        ((ChatListScreen) context).startActivity(intent);
                    }
                }
            });
            blockView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
//
                    if (!iChatPref.isBlocked(objUserModel.iUserName))
                        showBlockUnblockConfirmDialog(context.getString(R.string.confirmation), context.getString(R.string.block_confirmation), objUserModel);
                    else {
                        showBlockUnblockConfirmDialog(context.getString(R.string.confirmation), context.getString(R.string.unblock_confirmation), objUserModel);
//                		if (Build.VERSION.SDK_INT >= 11)
//                	         new BlockUnBlockOnProfileTask(objUserModel).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,objUserModel.iUserName);
//                	     else
//                	    	 new BlockUnBlockOnProfileTask(objUserModel).execute(objUserModel.iUserName);
                    }
                }
            });

            shortProfileDialog.show();
            shortProfile = true;
        } catch (Exception ex) {

        }
    }

    private void showBlockUnblockConfirmDialog(final String title, final String s, final UserProfileModel objUserModel) {
        final Dialog bteldialog = new Dialog(context);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.custom_dialog_two_button);
        if (title != null) {
            ((TextView) bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
        }
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_send)).setText(context.getString(R.string.yes));
        ((TextView) bteldialog.findViewById(R.id.id_cancel)).setText(context.getString(R.string.no));
        ((TextView) bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                if (Build.VERSION.SDK_INT >= 11)
                    new BlockUnBlockOnProfileTask(objUserModel).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objUserModel.iUserName);
                else
                    new BlockUnBlockOnProfileTask(objUserModel).execute(objUserModel.iUserName);

                return false;
            }
        });
        ((TextView) bteldialog.findViewById(R.id.id_cancel)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    private void getServerUserProfile(final String userName) {

        AsyncHttpClient client = new AsyncHttpClient();
        client = SuperChatApplication.addHeaderInfo(client, true);
        client.get(Constants.SERVER_URL + "/tiger/rest/user/profile/get?userName=" + userName,
                null, new AsyncHttpResponseHandler() {
                    ProgressDialog dialog = null;

                    @Override
                    public void onStart() {
                        ((ChatListScreen) context).runOnUiThread(new Runnable() {
                            public void run() {
                                dialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
                            }
                        });

                        Log.d(TAG, "AsyncHttpClient onStart: ");
                    }

                    @Override
                    public void onSuccess(int arg0, String arg1) {
                        Log.d(TAG, "AsyncHttpClient onSuccess: "
                                + arg1);

                        Gson gson = new GsonBuilder().create();
                        UserProfileModel objUserModel = gson.fromJson(arg1, UserProfileModel.class);
                        if (arg1 == null || arg1.contains("error") || objUserModel == null) {
//					runOnUiThread(new Runnable() {
//						public void run() {
//							if (dialog != null) {
//								dialog.dismiss();
//								dialog = null;
//							}
//						}
//					});

                            return;
                        }
//				objUserModel.iName
//				objUserModel.dob    convertDOBInFormat
//				objUserModel.address
//				public String iType = null;
//				objUserModel.imageFileId
//				setProfilePic(userName);
                        if (SharedPrefManager.getInstance().getDomainType().equals("rwa"))
                            showRWAProfile(objUserModel);
                        else
                            showShortProfile(objUserModel);//userName);
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        super.onSuccess(arg0, arg1);
                    }

                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        Log.d(TAG, "AsyncHttpClient onFailure: " + arg1);
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        //						showDialog("Please try again later.");
                        super.onFailure(arg0, arg1);
                    }
                });
    }

    private class BlockUnBlockOnProfileTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        boolean isStatusChanged = false;
        String userName;
        UserProfileModel objUserModel;

        BlockUnBlockOnProfileTask(UserProfileModel objUserModel) {
            this.objUserModel = objUserModel;
        }

        protected void onPreExecute() {

            dialog = ProgressDialog.show(context, "", "Please wait...", true);

            // progressBarView.setVisibility(ProgressBar.VISIBLE);
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            boolean isStatusChanged = false;
            userName = args[0];
            if (iChatPref.isBlocked(userName)) {
                isStatusChanged = messageService.blockUnblockUser(userName, true);
                if (isStatusChanged)
                    iChatPref.setBlockStatus(userName, false);
            } else {
                isStatusChanged = messageService.blockUnblockUser(userName, false);
                if (isStatusChanged)
                    iChatPref.setBlockStatus(userName, true);
            }
            this.isStatusChanged = isStatusChanged;
            return null;
        }

        protected void onPostExecute(String str) {
            super.onPostExecute(str);
            if (dialog != null) {
                dialog.cancel();
                dialog = null;
            }
            if (isStatusChanged) {
                if (iChatPref.isBlocked(userName)) {
                    Toast.makeText(context, context.getString(R.string.block_successful), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.unblock_successful), Toast.LENGTH_SHORT).show();
                }
                shortProfileDialog.dismiss();
                if (iChatPref.getDomainType().equals("rwa"))
                    showRWAProfile(objUserModel);
                else
                    showShortProfile(objUserModel);
            } else {
                Toast.makeText(context, "Please try after some time.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //--------------------------------
    public void updateList() {
        if (isBulletinBroadcast) {
            System.out.println("[updateList called..]");
            swapCursor(ChatDBWrapper.getInstance().getUserChatList(chatName, CHAT_LIST_BULLETIN));
            notifyDataSetChanged();
        }
    }
}
