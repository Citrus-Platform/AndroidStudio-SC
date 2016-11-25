package com.superchat.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatCountListener;
import com.chat.sdk.ChatService;
import com.chat.sdk.ConnectionStatusListener;
import com.chat.sdk.ProfileUpdateListener;
import com.chat.sdk.TypingListener;
import com.chat.sdk.db.ChatDBConstants;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.PrivacyList;
import com.chatsdk.org.jivesoftware.smack.PrivacyListManager;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.chatsdk.org.jivesoftware.smack.packet.PrivacyItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sinch.android.rtc.calling.Call;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.date.DatePickerDialog;
import com.superchat.emojicon.EmojiconEditText;
import com.superchat.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import com.superchat.emojicon.EmojiconsPopup;
import com.superchat.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import com.superchat.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import com.superchat.emojicon.emoji.Emojicon;
import com.superchat.interfaces.OnChatEditInterFace;
import com.superchat.interfaces.interfaceInstances;
import com.superchat.model.BulletinGetMessageDataModel;
import com.superchat.model.GroupChatMetaInfo;
import com.superchat.model.GroupChatServerModel;
import com.superchat.model.SuperGroupSubAdminStatusChange;
import com.superchat.model.UserProfileModel;
import com.superchat.retrofit.api.RetrofitRetrofitCallback;
import com.superchat.time.RadialPickerLayout;
import com.superchat.time.TimePickerDialog;
import com.superchat.utils.AndroidBmpUtil;
import com.superchat.utils.AppUtil;
import com.superchat.utils.BitmapDownloader;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.MediaEngine;
import com.superchat.utils.MyBase64;
import com.superchat.utils.ProfilePicDownloader;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.RTMediaPlayer;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.VoiceMedia;
import com.superchat.utils.VoiceMediaHandler;
import com.superchat.widgets.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Response;

import static android.R.id.toggle;
import static com.superchat.R.id.count;
import static com.superchat.R.id.create_doodle;
import static com.superchat.R.id.id_attach_media;

//import com.superchat.utils.SharedPrefManager;
public class ChatListScreen extends FragmentActivity implements MultiChoiceModeListener, VoiceMediaHandler, TypingListener, ChatCountListener, ProfileUpdateListener,
        OnClickListener, OnChatEditInterFace, ConnectionStatusListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, OnMenuItemClickListener, interfaceInstances {
    public final static String TAG = "ChatListScreen";
    public static final String CONTACT_ID = "contact_id";
    public final static String CREATE_GROUP_REQUEST = "create_group_request";
    public final static String UPDATE_GROUP_REQUEST = "update_group_request";
    private final byte NO_TAGGING = 0;
    private final byte IMAGE_TAGGING = 1;
    private final byte VIDEO_TAGGING = 2;
    private final byte AUDIO_TAGGING = 3;
    final int PICK_CONTACT = 119;
    final int PICK_LOCATION = 129;
    final int REQUEST_PLACE_PICKER = 1;
    private static final byte POSITION_PICTURE_RT_CANVAS = 5;
    private String MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?zoom=14&size=300x250&markers=size:mid|color:red|$lat,$lon&sensor=true";

    private ImageView playSenderView;
    private SeekBar playSenderSeekBar;
    private LinearLayout playLinearlayout;
    private TextView playSenderMaxTimeText;

    // XmppChatClient chatClient;
    private ChatService messageService;
    ProfilePicUploader picUploader;
    Dialog attachOptionsDialog;
    Dialog cameraOptionsDialog;
    Dialog fileConfirmationDialog;
    Dialog attachAudioTrackDialog;
    Dialog captionDialog;
    Dialog captionDialogNew;
    Dialog voiceRecorderDialog;
    private VoiceMedia mVoiceMedia;
    SharedPrefManager iChatPref;
    EmojiconEditText typingEditText;
    ImageView editTextLine;
    TextView timeCounterView;
    TextView recordInfoView;
    private ImageView micView;
    private RelativeLayout recordPanel;
    private RelativeLayout slideText;
    private TextView fileConfirmMessageView;
    private TextView closeView;
    private ImageView attachEmoView;
    private ImageView rightImageView;
    private ImageView crossImageView;
    private ImageView attachMediaView;
    TextView recordTipView;
    ProgressWheel wheelProgress;
    RoundedImageView userIcon;
    ImageView userIconDefault;
    private TextView windowNameView;
    private String contactID;
    private String contactNameTxt;
    private String userName;
    private String chatWindowName;
    private Cursor cursor;
    Uri fileAttachUri;
    private static HashMap<String, Boolean> selectedTagMap = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> checkSenderTagMap = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> checkTextMap = new HashMap<String, Boolean>();
    private boolean isLastIndex = false;
    private ListView chatList = null;
    private ChatListAdapter chatAdapter;
    public static String currentUser = "";
    public static boolean onForeground;
    private boolean isFreshLaunch = false;
    ArrayList<String> usersList = new ArrayList<String>();
    ArrayList<String> usersDisplayList = new ArrayList<String>();
    HashMap<String, String> nameMap = new HashMap<String, String>();
    private ImageView callOption;
    private ImageView chatOptions;
    private ImageView createPoll;

    private float distCanMove = SuperChatApplication.dp(160);
    private boolean isTouchEditbox = false;
    private InputMethodManager inputManager;
    LinearLayout mainLayout;
    private EmojiconsPopup popUp; // emojicon-popUp
    private ImageView smilyButton;
    boolean isFromGroupCreation;
    boolean invitationEnable;
    private String mVoicePath, mVideoPath;
    Timer clockTimer;
    TimerTask clockTimerTask;
    Calendar calander;
    long currentClockTime;
    boolean isPollOptionShown;
    private boolean isInvalidAudio = true;
    private boolean isRecordingStarted = false;
    private LinearLayout mainHeaderLayout;
    private RelativeLayout editHeaderLayout;
    private TextView okEditTextView;
    public ImageView chatCopyIv, chatDeleteIv;
    public ImageView chatInfoIv;
    private int buttonLayoutHeight = 0;
    private ImageView canncelView, sendView;
    private TextView tapToReordView;
    TextView typingText;
    private LinearLayout audioRecordLayout;
    private XMPPConnection xmppConnection;
    ImageView xmppStatusView;
    LinearLayout networkConnection;
    //    boolean isVideoTagged = true;
//    boolean isPictureTagged = true;
    byte taggingType = NO_TAGGING;
    String mediaUrl;

    private Button loadPrevious;
    private ProgressBar messageLoading;

    public SinchService.SinchServiceInterface mSinchServiceInterface;
    MediaPlayer mPlayer = null;
//    RelativeLayout bottomPanel;
//    LinearLayout pollMainLayout;
//    RadioGroup pollRadioGroup;
//    LinearLayout poll_result_view1;
//    TextView option_one_result;
//    TextView option_two_result;
//    TextView option_three_result;
//    TextView option_four_result;
//    TextView option_five_result;
//    TextView pollTitleText;
//    TextView pollTextMessage;
//    TextView pollExpires;
//    RadioButton option1;
//    RadioButton option2;
//    RadioButton option3;
//    RadioButton option4;
//    RadioButton option5;


    static String openedPollID = null;
    String openedGroupName = null;
    boolean isPollResultPage;
    boolean isPollActive = true;
    boolean isBulletinBroadcast;
    boolean isSharedIDMessage;
    boolean isSharedIDAdmin;
    boolean isSharedIDDeactivated;
    public static final byte CHAT_LIST_NORMAL = 1;
    public static final byte CHAT_LIST_BULLETIN = 2;
    private ServiceConnection mCallConnection = new ServiceConnection() {
        // ------------ Changes for call ---------------
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // if
            // (SinchService.class.getName().equals(componentName.getClassName()))
            {
                mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
                onServiceConnected();

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            // if
            // (SinchService.class.getName().equals(componentName.getClassName()))
            {
                mSinchServiceInterface = null;
                onServiceDisconnected();
            }
        }

        protected void onServiceConnected() {
            // Register the user for call
            if (mSinchServiceInterface != null && !mSinchServiceInterface.isStarted()) {
                mSinchServiceInterface.startClient(SharedPrefManager.getInstance().getUserName());
            }
        }

        protected void onServiceDisconnected() {
            // for subclasses
        }

        protected SinchService.SinchServiceInterface getSinchServiceInterface() {
            return mSinchServiceInterface;
        }
    };
    private ServiceConnection mMessageConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            messageService = ((ChatService.MyBinder) binder).getService();
            Log.d("Service", "Connected");
            xmppConnection = messageService.getconnection();
            messageService.setChatListener(ChatListScreen.this);
            messageService.setChatCountListener(ChatListScreen.this);
            messageService.setTypingListener(ChatListScreen.this);
            messageService.setProfileUpdateListener(ChatListScreen.this);
            sendSeenStatus();
            if (messageService != null) {
                messageService.setChatVisibilty(onForeground);
                messageService.clearAllNotifications();
                if (chatAdapter != null)
                    chatAdapter.setChatService(messageService);
                messageService.sendOffLineMessages1();
                sendSharingData();
            }
//            if (messageService != null && invitationEnable && !isFromGroupCreation) {
//                invitationEnable = false;
//                try {
//                    ArrayList<String> inviters = getIntent().getStringArrayListExtra(Constants.INVITATION_LIST);
//                    String displayName = SharedPrefManager.getInstance().getGroupDisplayName(userName);
//                    String description = iChatPref.getUsersOfGroup(userName);
//                    String infoList = "";
//                    for (String inviter : inviters) {
//                        if (inviter != null && !inviter.equals("")) {
//
//                            messageService.inviteUserInRoom(userName, displayName, description, inviter);
//                            infoList += inviter + ",";
//                        }
//                    }
//                    if (!infoList.equals("")) {
//                        if (usersList != null && !usersList.isEmpty()) {
//                            for (String element : usersList)
//                                infoList += element + ",";
//                        }
//                        if (infoList.endsWith(","))
//                            infoList = infoList.substring(0, infoList.length() - 1);
//                        boolean isSuccessed = messageService.sendInfoMessage(userName, infoList,
//                                Message.XMPPMessageType.atMeXmppMessageTypeMemberList);
//                        if (isSuccessed) {
//                            int state = iChatPref.getServerGroupState(userName);
//                            if (state != GroupCreateTaskOnServer.SERVER_GROUP_NOT_CREATED
//                                    && state != GroupCreateTaskOnServer.SERVER_GROUP_CREATION_FAILED) {
//                                iChatPref.saveServerGroupState(userName,
//                                        GroupCreateTaskOnServer.SERVER_GROUP_NOT_UPDATED);
//                                new GroupCreateTaskOnServer(userName, chatWindowName, usersList)
//                                        .execute(GroupCreateTaskOnServer.UPDATE_GROUP_REQUEST);
//                            }
//                        }
//                    }
//                    getIntent().getExtras().putString("INVITATION_LIST", "");
//                } catch (Exception e) {
//                    Log.d(TAG, "users are not able to joined.");
//                }
//
//            }
        }

        public void onServiceDisconnected(ComponentName className) {
            xmppConnection = null;
            messageService = null;
        }
    };

//    Dialog shortProfileDialog = null;
//    boolean shortProfile;
//    public void showShortProfile(String user_name){
//    	try{
//    		shortProfileDialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//    		shortProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
////			poll.setCanceledOnTouchOutside(false);
//    		shortProfileDialog.setContentView(R.layout.user_profile_short);
//
//            Window window = shortProfileDialog.getWindow();
//            WindowManager.LayoutParams wlp = window.getAttributes();
//
//            wlp.gravity = Gravity.CENTER;
//            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            window.setAttributes(wlp);
//
//            shortProfileDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    // dialog dismiss without button press
//                    shortProfileDialog.dismiss();
//                }
//            });
//            
//            final ImageView message = (ImageView) shortProfileDialog.findViewById(R.id.id_send_message);
//            final ImageView connect = (ImageView) shortProfileDialog.findViewById(R.id.id_connect);
//            final ImageView block = (ImageView) shortProfileDialog.findViewById(R.id.id_block);
//            final ImageView cancel = (ImageView) shortProfileDialog.findViewById(R.id.id_cancel);
//            final TextView name = (TextView) shortProfileDialog.findViewById(R.id.id_name);
//            String display_name = iChatPref.getUserServerName(user_name);
//            if(display_name != null && display_name.trim().length() > 0)
//            	name.setText(display_name);
//            cancel.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					shortProfileDialog.dismiss();
//				}
//			});
//            block.setOnClickListener(new OnClickListener() {
//            	
//            	@Override
//            	public void onClick(View v) {
//            		// TODO Auto-generated method stub
//            		shortProfileDialog.dismiss();
//            		// Create a privacy manager for the current connection._
//            	    PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(ChatService.connection);
//            	    // Retrieve server privacy lists_
//            	    try {
//            	    	blockUnblockUser(userName, true);
//            	    	PrivacyList[] lists = privacyManager.getPrivacyLists();
//            	    	if(lists != null && lists.length > 0)
//            	    		Log.i(TAG, "Privacy list Size : "+lists.length);
////            	    	getBlockedUserList(userName);
//					} catch (XMPPException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//            	}
//            });
//            
////            shortProfileDialog.show();
//            shortProfile = true;
//    	}catch(Exception ex){
//    		
//    	}
//    }

    // Here function for block user on xmpp

    public boolean blockUnblockUser(String userName, boolean bool) {

        String jid = userName + "@" + Constants.CHAT_SERVER_URL;
        String listName = "newList";

        // Create the list of PrivacyItem that will allow or
        // deny some privacy aspect

        //ArrayList privacyItems = new ArrayList();

        List<PrivacyItem> privacyItems = new Vector<PrivacyItem>();

        PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid.toString(), bool, 1);
        item.setValue(jid);
        item.setFilterIQ(bool);
        item.setFilterMessage(bool);
//    item.setFilterPresenceIn(false);
//    item.setFilterPresenceOut(false);

        privacyItems.add(item);
        // Get the privacy manager for the current connection.
        // Create the new list.
        PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(ChatService.connection);
        try {
            privacyManager.updatePrivacyList(listName, privacyItems);
            privacyManager.setActiveListName(listName);

            return true;
        } catch (Exception e) {
            Log.e("PRIVACY_ERROR: ", " " + e.toString());
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getBlockedUserList(String userId) {

        List<String> privacyList = new ArrayList<String>();
        try {
            PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(xmppConnection);
            if (privacyManager == null) {
                return privacyList;
            }
            String ser = "@" + messageService;
            PrivacyList plist = null;
            try {
                plist = privacyManager.getPrivacyList("public");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (plist != null) {// No blacklisted or is not listed, direct getPrivacyList error
                List<PrivacyItem> items = plist.getItems();
                for (PrivacyItem item : items) {
                    String from = item.getValue().substring(0, item.getValue().indexOf(ser));
                    if (userId.equals(from)) {
                        item.isAllow();
                    }
                    // privacyList.add(from);
                }
            } else {
                return privacyList;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return privacyList;
    }

    public void showPoll(final String message, final boolean is_view) {
        try {
            if (message != null && message.length() > 10) {
                String poll_id = null;
                JSONObject obj = new JSONObject(message);
                if (obj.has("PollID") && obj.getString("PollID").toString().trim().length() > 0)
                    poll_id = obj.getString("PollID").toString();
                if (is_view || SharedPrefManager.getInstance().getPollReplyStatus(poll_id)) {
                    showUpdatedPollresults(poll_id);
                    isPollResultPage = true;
                    return;
                }

                poll = new Dialog(this);
                poll.requestWindowFeature(Window.FEATURE_NO_TITLE);

//				poll.setCanceledOnTouchOutside(false);
                poll.setContentView(R.layout.poll_display);

                Window window = poll.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.CENTER;
                wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                poll.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // dialog dismiss without button press
                        isPollResultPage = false;
                    }
                });


//              final LinearLayout pollMainLayout = (LinearLayout) findViewById(R.id.poll_main_layout);
                final RadioGroup pollRadioGroup = (RadioGroup) poll.findViewById(R.id.poll_options_group);
                final RadioButton option1 = (RadioButton) poll.findViewById(R.id.option_one);
                final RadioButton option2 = (RadioButton) poll.findViewById(R.id.option_two);
                final RadioButton option3 = (RadioButton) poll.findViewById(R.id.option_three);
                final RadioButton option4 = (RadioButton) poll.findViewById(R.id.option_four);
                final RadioButton option5 = (RadioButton) poll.findViewById(R.id.option_five);
                final TextView pollTitleText = (TextView) poll.findViewById(R.id.poll_title_txt);
                final TextView pollTextMessage = (TextView) poll.findViewById(R.id.poll_text_message);
                final TextView pollExpires = (TextView) poll.findViewById(R.id.poll_expires);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
//                            pollMainLayout.setVisibility(View.VISIBLE);
                            JSONObject jsonobj = new JSONObject(message);
                            JSONArray poll_options = null;
                            String poll_unique_id = null;
                            String[] poll_id = null;
                            String[] poll_value = null;
                            int[] poll_option_count = null;
                            boolean expired = false;
                            if (jsonobj.has("PollID") && jsonobj.getString("PollID").toString().trim().length() > 0) {
                                openedPollID = jsonobj.getString("PollID").toString();
                                //Check poll id in the shared pref,if replied the open show view only.
                                if (SharedPrefManager.getInstance().getPollReplyStatus(openedPollID)) {
//                                	pollMainLayout.setVisibility(View.GONE);
                                    poll.dismiss();
//                                	showPollView(message);
                                    showUpdatedPollresults(openedPollID);
                                    isPollResultPage = true;
                                    return;
                                }
                            }
                            if (jsonobj.has("PollTitle") && jsonobj.getString("PollTitle").toString().trim().length() > 0) {
                                pollTitleText.setText("Active poll - " + jsonobj.getString("PollTitle"));
                            }
                            if (jsonobj.has("PollEndDate") && jsonobj.getString("PollEndDate").toString().trim().length() > 0) {
                                Date d = new Date(new Date().getTime());
                                SimpleDateFormat data_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String poll_end_time = jsonobj.getString("PollEndDate").toString();
                                String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                                long ss = 0;
                                long ee = 0;
                                String ex_date = jsonobj.getString("PollEndDate");
                                if (ex_date != null && ex_date.endsWith(" +0000"))
                                    ex_date = ex_date.substring(0, ex_date.indexOf(" +0000"));
                                try {
                                    ss = data_formatter.parse(current).getTime();
                                    ee = data_formatter.parse(poll_end_time).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if ((ee - ss) < 0) {
                                    //Poll Expired
                                    pollExpires.setText("Poll expired on - " + ex_date);
                                    expired = true;
                                } else {
                                    pollExpires.setText("Poll expires on - " + ex_date);
                                }
                            }
                            if (jsonobj.has("Pollmessage") && jsonobj.getString("Pollmessage").toString().trim().length() > 0) {
                                pollTextMessage.setText(jsonobj.getString("Pollmessage"));
                            }
                            if (jsonobj.has("PollOption"))
                                poll_options = jsonobj.getJSONArray("PollOption");
                            if (poll_options.length() > 0) {
                                poll_id = new String[poll_options.length()];
                                poll_value = new String[poll_options.length()];
                                poll_option_count = new int[poll_options.length()];
                                for (int i = 0; i < poll_options.length(); i++) {
                                    JSONObject obj = (JSONObject) poll_options.get(i);
                                    if (obj.has("OptionId")) {
                                        poll_id[i] = obj.getString("OptionId");
                                    }
                                    if (obj.has("OptionText")) {
                                        poll_value[i] = obj.getString("OptionText");
                                    }
                                    if (obj.has("PollOptionCount")) {
                                        poll_option_count[i] = Integer.parseInt(obj.getString("PollOptionCount"));
                                    }
                                }
                            }
                            if (is_view || expired) {
                                if (expired) {
                                    poll.dismiss();
                                    showUpdatedPollresults(openedPollID);
                                    isPollResultPage = true;
                                    return;
                                }
                            } else {
                                isPollResultPage = false;
                                pollRadioGroup.setVisibility(View.VISIBLE);
                                final String[] poll_id_array = poll_id;
                                if (poll_value != null && poll_value.length == 2) {
                                    option1.setText(poll_value[0]);
                                    option2.setText(poll_value[1]);
                                } else if (poll_value != null && poll_value.length == 3) {
//                                    pollMoreOptioLayout.setVisibility(View.VISIBLE);
//                                    pollMoreOptios0.setVisibility(View.VISIBLE);
                                    option1.setText(poll_value[0]);
                                    option2.setText(poll_value[1]);
                                    option3.setVisibility(View.VISIBLE);
                                    option3.setText(poll_value[2]);
//                                    option4.setVisibility(View.GONE);
                                } else if (poll_value != null && poll_value.length == 4) {
//                                    pollMoreOptioLayout.setVisibility(View.VISIBLE);
//                                    pollMoreOptios0.setVisibility(View.VISIBLE);
                                    option1.setText(poll_value[0]);
                                    option2.setText(poll_value[1]);
                                    option3.setVisibility(View.VISIBLE);
                                    option3.setText(poll_value[2]);
                                    option4.setVisibility(View.VISIBLE);
                                    option4.setText(poll_value[3]);
                                } else if (poll_value != null && poll_value.length == 5) {
//                                  pollMoreOptioLayout.setVisibility(View.VISIBLE);
//                                  pollMoreOptios0.setVisibility(View.VISIBLE);
                                    option1.setText(poll_value[0]);
                                    option2.setText(poll_value[1]);
                                    option3.setVisibility(View.VISIBLE);
                                    option3.setText(poll_value[2]);
                                    option4.setVisibility(View.VISIBLE);
                                    option4.setText(poll_value[3]);
                                    option5.setVisibility(View.VISIBLE);
                                    option5.setText(poll_value[4]);
                                }
                                option1.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
//                                        pollMainLayout.setVisibility(View.GONE);
                                        poll.dismiss();
                                        sendPollReply(message, poll_id_array[0]);
                                    }
                                });
                                option2.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
//                                        pollMainLayout.setVisibility(View.GONE);
                                        poll.dismiss();
                                        sendPollReply(message, poll_id_array[1]);
                                    }
                                });
                                option3.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
//                                        pollMainLayout.setVisibility(View.GONE);
                                        poll.dismiss();
                                        sendPollReply(message, poll_id_array[2]);
                                    }
                                });
                                option4.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
//                                        pollMainLayout.setVisibility(View.GONE);
                                        poll.dismiss();
                                        sendPollReply(message, poll_id_array[3]);
                                    }
                                });
                                option5.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
//                                		pollMainLayout.setVisibility(View.GONE);
                                        poll.dismiss();
                                        sendPollReply(message, poll_id_array[4]);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            } else
                return;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (!isPollResultPage && poll != null)
            poll.show();
    }

    Dialog poll = null;

    public void showPollView(final String message) {
        try {

            if (message != null) {

                isPollResultPage = true;

//				poll = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen)\;

                poll = new Dialog(this);
                poll.requestWindowFeature(Window.FEATURE_NO_TITLE);

//				poll.setCanceledOnTouchOutside(false);
                poll.setContentView(R.layout.poll_view);

                Window window = poll.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.CENTER;
                wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                poll.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // dialog dismiss without button press
                        isPollResultPage = false;
                    }
                });

//                final LinearLayout poll_result_view2 = (LinearLayout) poll.findViewById(R.id.poll_result_view2);
                final TextView title = (TextView) poll.findViewById(R.id.poll_title_txt);
                final TextView poll_text = (TextView) poll.findViewById(R.id.poll_text_message);
                final TextView poll_expires = (TextView) poll.findViewById(R.id.poll_expires);

                //Poll options
                final TextView option_one_result = (TextView) poll.findViewById(R.id.option_one_result);
                final TextView option_two_result = (TextView) poll.findViewById(R.id.option_two_result);
                final TextView option_three_result = (TextView) poll.findViewById(R.id.option_three_result);
                final TextView option_four_result = (TextView) poll.findViewById(R.id.option_four_result);
                final TextView option_five_result = (TextView) poll.findViewById(R.id.option_five_result);


                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject jsonobj = new JSONObject(message);
                            JSONArray poll_options = null;
                            String poll_unique_id = null;
                            String[] poll_id = null;
                            String[] poll_value = null;
                            int[] poll_option_count = null;
                            int total_replies = 0;
//							if(jsonobj.has("PollID") && jsonobj.getString("PollID").toString().trim().length() > 0) {
//								openedPollID = jsonobj.getString("PollID").toString();
//							}
                            if (jsonobj.has("PollTitle") && jsonobj.getString("PollTitle").toString().trim().length() > 0) {
                                title.setText("Active poll - " + jsonobj.getString("PollTitle"));
                            }
                            if (jsonobj.has("PollEndDate") && jsonobj.getString("PollEndDate").toString().trim().length() > 0) {
                                Date d = new Date(new Date().getTime());
                                SimpleDateFormat data_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String poll_end_time = jsonobj.getString("PollEndDate").toString();
                                String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
                                long ss = 0;
                                long ee = 0;
                                String ex_date = jsonobj.getString("PollEndDate");
                                if (ex_date != null && ex_date.endsWith(" +0000"))
                                    ex_date = ex_date.substring(0, ex_date.indexOf(" +0000"));
                                try {
                                    ss = data_formatter.parse(current).getTime();
                                    ee = data_formatter.parse(poll_end_time).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if ((ee - ss) < 0) {
                                    //Poll Expired
                                    poll_expires.setText("Poll expired on - " + ex_date);
                                } else
                                    poll_expires.setText("Poll expires on - " + ex_date);
                            }
                            if (jsonobj.has("Pollmessage") && jsonobj.getString("Pollmessage").toString().trim().length() > 0) {
                                poll_text.setText(jsonobj.getString("Pollmessage"));
                            }
                            if (jsonobj.has("PollOption"))
                                poll_options = jsonobj.getJSONArray("PollOption");
                            if (poll_options.length() > 0) {
                                poll_id = new String[poll_options.length()];
                                poll_value = new String[poll_options.length()];
                                poll_option_count = new int[poll_options.length()];
                                for (int i = 0; i < poll_options.length(); i++) {
                                    JSONObject obj = (JSONObject) poll_options.get(i);
                                    if (obj.has("OptionId")) {
                                        poll_id[i] = obj.getString("OptionId");
                                    }
                                    if (obj.has("OptionText")) {
                                        poll_value[i] = obj.getString("OptionText");
                                    }
                                    if (obj.has("PollOptionCount")) {
                                        poll_option_count[i] = Integer.parseInt(obj.getString("PollOptionCount"));
                                        total_replies += poll_option_count[i];
                                    }
                                }
                            }
                            int total_poll_count = 0;
                            if (total_replies == 0)
                                total_replies = 1;
                            for (int j = 0; j < poll_option_count.length; j++)
                                total_poll_count += poll_option_count[j];
                            final String[] poll_id_array = poll_id;
                            if (poll_value != null && poll_value.length == 2) {
                                option_one_result.setText(poll_value[0] + " - " + (poll_option_count[0] * 100 / total_replies) + "%");
                                option_two_result.setText(poll_value[1] + " - " + (poll_option_count[1] * 100 / total_replies) + "%");
                            } else if (poll_value != null && poll_value.length == 3) {
//                                poll_result_view2.setVisibility(View.VISIBLE);
                                option_one_result.setText(poll_value[0] + " - " + (poll_option_count[0] * 100 / total_replies) + "%");
                                option_two_result.setText(poll_value[1] + " - " + (poll_option_count[1] * 100 / total_replies) + "%");
                                option_three_result.setVisibility(View.VISIBLE);
                                option_three_result.setText(poll_value[2] + " - " + (poll_option_count[2] * 100 / total_replies) + "%");
                            } else if (poll_value != null && poll_value.length == 4) {
//                                poll_result_view2.setVisibility(View.VISIBLE);
                                option_one_result.setText(poll_value[0] + " - " + (poll_option_count[0] * 100 / total_replies) + "%");
                                option_two_result.setText(poll_value[1] + " - " + (poll_option_count[1] * 100 / total_replies) + "%");
                                option_three_result.setVisibility(View.VISIBLE);
                                option_three_result.setText(poll_value[2] + " - " + (poll_option_count[2] * 100 / total_replies) + "%");
                                option_four_result.setVisibility(View.VISIBLE);
                                option_four_result.setText(poll_value[3] + " - " + (poll_option_count[3] * 100 / total_replies) + "%");
                            } else if (poll_value != null && poll_value.length == 5) {
//                              poll_result_view2.setVisibility(View.VISIBLE);
                                option_one_result.setText(poll_value[0] + " - " + (poll_option_count[0] * 100 / total_replies) + "%");
                                option_two_result.setText(poll_value[1] + " - " + (poll_option_count[1] * 100 / total_replies) + "%");
                                option_three_result.setVisibility(View.VISIBLE);
                                option_three_result.setText(poll_value[2] + " - " + (poll_option_count[2] * 100 / total_replies) + "%");
                                option_four_result.setVisibility(View.VISIBLE);
                                option_four_result.setText(poll_value[3] + " - " + (poll_option_count[3] * 100 / total_replies) + "%");
                                option_five_result.setVisibility(View.VISIBLE);
                                option_five_result.setText(poll_value[4] + " - " + (poll_option_count[4] * 100 / total_replies) + "%");
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        poll.show();
    }


    private void savePoll(String grp_name, String poll_id, String json_data) {

        //create test hashmap
        HashMap<String, String> pollHashMap = null;
        pollHashMap = getPollForGroup(grp_name);
        if (pollHashMap == null)
            pollHashMap = new HashMap<String, String>();

        if (pollHashMap.containsKey(poll_id)) {
            pollHashMap.remove(poll_id);
            pollHashMap.put(poll_id, json_data);
        } else
            pollHashMap.put(poll_id, json_data);

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(pollHashMap);

        //save in shared prefs
        SharedPreferences prefs = getSharedPreferences("poll_data", MODE_PRIVATE);
        prefs.edit().putString(grp_name, hashMapString).apply();

//        //get from shared prefs
//        String storedHashMapString = prefs.getString("hashString", "oopsDintWork");
//        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
//        HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);

//        //use values
//        String toastString = testHashMap2.get("key1") + " | " + testHashMap2.get("key2");
//        Toast.makeText(this, toastString, Toast.LENGTH_LONG).show();
    }

    private void savePollIDs(String poll_id, String active_inactive) {
        //create test hashmap
        HashMap<String, String> pollHashMap = null;
        pollHashMap = getPollForGroup("all_polls");
        if (pollHashMap == null)
            pollHashMap = new HashMap<String, String>();

        pollHashMap.put(poll_id, active_inactive);

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(pollHashMap);

        //save in shared prefs
        SharedPreferences prefs = getSharedPreferences("all_polls", MODE_PRIVATE);
        prefs.edit().putString("all_polls", hashMapString).apply();
    }

    //    private HashMap getPollStatusMapForAllPolls(String poll_id)
//    {
//        //save in shared prefs
//        SharedPreferences prefs = getSharedPreferences("all_polls", MODE_PRIVATE);
//        Gson gson = new Gson();
//
//        //get from shared prefs
//        String storedHashMapString = prefs.getString("all_polls", null);
//        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
//        if(storedHashMapString != null) {
//            HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
//            return testHashMap2;
//        }
//        return null;
//    }
    private String getPollStatus(String poll_id) {
        //save in shared prefs
        String status = "inactive";
        SharedPreferences prefs = getSharedPreferences("all_polls", MODE_PRIVATE);
        Gson gson = new Gson();

        //get from shared prefs
        String storedHashMapString = prefs.getString("all_polls", null);
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        if (storedHashMapString != null) {
            HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
            status = testHashMap2.get(poll_id);
            return status;
        }
        return null;
    }

    private HashMap<String, String> getPollForGroup(String grp_name) {
        //save in shared prefs
        SharedPreferences prefs = getSharedPreferences("poll_data", MODE_PRIVATE);
        Gson gson = new Gson();

        //get from shared prefs
        String storedHashMapString = prefs.getString(grp_name, null);
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        if (storedHashMapString != null) {
            HashMap<String, String> testHashMap2 = gson.fromJson(storedHashMapString, type);
            return testHashMap2;
        }
        return null;
    }


    private LinearLayout mRevealView;
    private boolean menuOpen = false;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.chat_list_screen);
        mPlayer = MediaPlayer.create(this, R.raw.off);
        isFreshLaunch = true;
        initVoiceRecorderWheelDialog();
        initAudioAttachTrackDialog();
        initCameraOptionDialog();
        initAttachFileConfirmDialog();
        calander = Calendar.getInstance();
        mainHeaderLayout = (LinearLayout) findViewById(R.id.id_header_view);
        editHeaderLayout = (RelativeLayout) findViewById(R.id.edit_chat_header);
        okEditTextView = (TextView) findViewById(R.id.id_ok_title);
        loadPrevious = (Button) findViewById(R.id.load_older_messages);
        messageLoading = (ProgressBar) findViewById(R.id.message_loading);
        //Load poll views

        mDrawableBuilder = TextDrawable.builder()
                .beginConfig().toUpperCase()
                .endConfig()
                .round();

        okEditTextView.setOnClickListener(this);
        chatCopyIv = (ImageView) findViewById(R.id.id_copy_iv);
        chatInfoIv = (ImageView) findViewById(R.id.id_info_iv);
        chatDeleteIv = (ImageView) findViewById(R.id.id_delete_iv);
        chatInfoIv.setVisibility(View.GONE);
        chatInfoIv.setOnClickListener(this);
        chatCopyIv.setOnClickListener(this);
        chatDeleteIv.setOnClickListener(this);
        windowNameView = (TextView) findViewById(R.id.id_chat_name);
        typingText = (TextView) findViewById(R.id.id_chat_status);
        mainLayout = (LinearLayout) findViewById(R.id.id_chat_home);
        typingEditText = (EmojiconEditText) findViewById(R.id.id_chat_field);
        editTextLine = (ImageView) findViewById(R.id.line_iv);
        userIcon = (RoundedImageView) findViewById(R.id.id_chat_icon);
        userIconDefault = (ImageView) findViewById(R.id.id_chat_icon_default);
        xmppStatusView = (ImageView) findViewById(R.id.id_xmpp_status);
        networkConnection = (LinearLayout) findViewById(R.id.connecting_layout);
        micView = (ImageView) findViewById(R.id.id_mic);
        attachEmoView = (ImageView) findViewById(R.id.id_attach_emoticon);
        attachMediaView = (ImageView) findViewById(R.id.id_attach_media);
        chatOptions = (ImageView) findViewById(R.id.chat_options);
        callOption = (ImageView) findViewById(R.id.call_option);
        createPoll = (ImageView) findViewById(R.id.create_poll);

        if (!isPollActive)
            createPoll.setVisibility(View.GONE);
        callOption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onCallClicked();
            }
        });

        createPoll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
//                onPollClicked("");
                //Get poll data
                if (isPollResultPage) {
//                    pollMainLayout.setVisibility(View.GONE);
                    if (poll != null)
                        poll.dismiss();
                    isPollResultPage = false;
                } else {
                    showUpdatedPollresults(openedPollID);
                }
            }
        });
        setAudioRecorderUIPoint();
        if (ChatService.xmppConectionStatus) {
            networkConnection.setVisibility(View.GONE);
            xmppStatusView.setImageResource(R.drawable.blue_dot);
        } else {
            networkConnection.setVisibility(View.VISIBLE);
            xmppStatusView.setImageResource(R.drawable.red_dot);
        }
//        userIcon.setOnClickListener(this);
//        bottomPanel =  ((RelativeLayout)findViewById(R.id.bottom_write_bar1));
        final ImageView sendButton = (ImageView) findViewById(R.id.id_send_chat);
        sendButton.setOnClickListener(this);
        sendButton.setVisibility(TextView.GONE);
        micView.setVisibility(TextView.VISIBLE);
        attachEmoView.setVisibility(ImageView.VISIBLE);
        attachMediaView.setVisibility(ImageView.VISIBLE);
        typingEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable editable) {
                String msgTxt = typingEditText.getText().toString();
                if (msgTxt != null && msgTxt.length() > 0) {
                    sendButton.setVisibility(TextView.VISIBLE);
                    micView.setVisibility(TextView.GONE);
                } else {
                    sendButton.setVisibility(TextView.GONE);
                    micView.setVisibility(TextView.VISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
            }

            public void onTextChanged(CharSequence charsequence, int i, int j, int k) {
            }

        });

        iChatPref = SharedPrefManager.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userName = extras.getString(DatabaseConstants.USER_NAME_FIELD);
            isBulletinBroadcast = extras.getBoolean("FROM_BULLETIN_NOTIFICATION");
            isSharedIDMessage = iChatPref.isSharedIDContact(userName);
            if (!iChatPref.isDomainAdminORSubAdmin() && HomeScreen.isAdminFromSharedID(userName, iChatPref.getUserName()))
                isSharedIDAdmin = true;
            isSharedIDDeactivated = iChatPref.isSharedIDDeactivated(userName);
            if (userName != null && userName.equalsIgnoreCase(iChatPref.getUserDomain() + "-all"))
                isBulletinBroadcast = true;
            else if (isBulletinBroadcast && iChatPref.isDomainAdminORSubAdmin()) {
                userName = iChatPref.getUserDomain() + "-all";
            }
            contactNameTxt = getIntent().getExtras().getString(DatabaseConstants.CONTACT_NAMES_FIELD);
            if (isBulletinBroadcast) {
                windowNameView.setText(iChatPref.getCurrentSGDisplayName());
            } else if (isSharedIDMessage) {
                windowNameView.setText(iChatPref.getSharedIDDisplayName(userName));
            } else if (contactNameTxt.contains("##$^##"))
                windowNameView.setText(contactNameTxt.substring(0, contactNameTxt.indexOf("##$^##")));
            else
                windowNameView.setText(contactNameTxt);

            isFromGroupCreation = extras.getBoolean(Constants.IS_GROUP_CREATION, false);
            if (isFromGroupCreation) {
                getIntent().putStringArrayListExtra(Constants.INVITATION_LIST, new ArrayList<String>());

            }
            chatWindowName = iChatPref.getGroupDisplayName(userName);
            if (iChatPref.isBroadCast(userName)) {
                callOption.setVisibility(View.GONE);
                invitationEnable = true;
                // usersList.clear();
                chatWindowName = iChatPref.getBroadCastDisplayName(userName);
                if (chatWindowName != null && chatWindowName.indexOf('_') != -1) {
                    chatWindowName = chatWindowName.substring(0, chatWindowName.indexOf('_'));
                    windowNameView.setText(chatWindowName);
                } else
                    windowNameView.setText(chatWindowName);
                usersList = iChatPref.getBroadCastUsersList(userName);
                onForeground = true;
                // Collections.sort(usersList);
                usersDisplayList = usersList;// convertNames(usersList);
                Log.d(TAG, usersList + " group persons displayed of group " + userName);
                // updateGroupUsersList(usersDisplayList);
                typingText.setVisibility(TextView.VISIBLE);
                typingText.setText(iChatPref.getUserStatusMessage(userName));

            } else if (iChatPref.isGroupChat(userName) || isSharedIDMessage) {
                openedGroupName = userName;
                callOption.setVisibility(View.GONE);
                if (isPollActive)
                    createPoll.setVisibility(View.VISIBLE);
                invitationEnable = true;
                usersList.clear();
                usersList = iChatPref.getGroupUsersList(userName);
                // usersList.add(iChatPref.getUserName());
                // Collections.sort(usersList);
                // usersDisplayList = convertNames(usersList);
                // updateGroupUsersList(usersDisplayList);
                onForeground = true;
                Collections.sort(usersList);
                usersDisplayList = usersList;// convertNames(usersList);
                Log.d(TAG, usersList + " group persons displayed of group " + userName);
                // updateGroupUsersList(usersDisplayList);
                // refreshOnlineGroupUser();
                if (isSharedIDMessage) {
                    windowNameView.setText(iChatPref.getSharedIDDisplayName(userName));
                } else if (chatWindowName != null && chatWindowName.trim().length() > 0)
                    windowNameView.setText(chatWindowName);
                typingText.setText(iChatPref.getUserStatusMessage(userName));
                typingText.setVisibility(View.GONE);
            } else if (windowNameView.getText().toString().equalsIgnoreCase(userName)) {
                if (userName.contains("_"))
                    windowNameView.setText("+" + userName.substring(0, userName.indexOf("_")));
                else
                    windowNameView.setText(userName);
                typingText.setVisibility(View.GONE);
            }
            // setProfilePic(userIcon);
        }
        if (isBulletinBroadcast) {
            chatOptions.setVisibility(View.GONE);
            callOption.setVisibility(View.GONE);
            iChatPref.saveBulletinChatCounter(iChatPref.getUserDomain(), 0);
//            final String next_url = iChatPref.getBulletinNextURL();
//            if(next_url != null && !next_url.equals("0"))
//                loadPrevious.setVisibility(View.VISIBLE);
//            loadPrevious.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    //Check for bulletin next url;
//                    getBulletinMessages(iChatPref.getBulletinNextURL());
//                }
//            });
        }
        if (isSharedIDMessage) {
//        	chatOptions.setVisibility(View.GONE);
            callOption.setVisibility(View.GONE);
        }
        if (iChatPref.isGroupChat(userName))
            userIcon.setImageResource(R.drawable.group_white_icon);
        else if (isBulletinBroadcast)
            userIcon.setImageResource(R.drawable.broadcast_white_icon);
        else {
//            if (SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
//                userIcon.setImageResource(R.drawable.female_default);
//            else
//                userIcon.setImageResource(R.drawable.male_default);

            setProfilePic(userIcon, userIconDefault, contactNameTxt);
        }
        Log.d(TAG, "userName isGroupChat: " + iChatPref.isGroupChat(userName));
        Cursor cursor1 = null;
        if (isBulletinBroadcast)
            cursor1 = ChatDBWrapper.getInstance(getApplicationContext()).getUserChatList(userName, CHAT_LIST_BULLETIN);
        else
            cursor1 = ChatDBWrapper.getInstance(getApplicationContext()).getUserChatList(userName, CHAT_LIST_NORMAL);
        if (cursor1 == null) {
            if (isBulletinBroadcast)
                cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_BULLETIN);
            else
                cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_NORMAL);
        }
        String as[] = {DatabaseConstants.FROM_USER_FIELD};
        int ai[] = new int[1];
        ai[0] = R.id.chat_person_name;
        if (isBulletinBroadcast)
            chatAdapter = new ChatListAdapter(ChatListScreen.this, R.layout.bulletin_item_list_row, cursor1, as, ai, 0, userName, this);
        else
            chatAdapter = new ChatListAdapter(ChatListScreen.this, R.layout.chatlist_item, cursor1, as, ai, 0, userName, this);
        if (chatAdapter != null) {
            chatAdapter.setRefreshListener(this);
            if (isBulletinBroadcast)
                chatAdapter.chatListBulletin(true);
            chatAdapter.setGroupOrMultiUserChat(iChatPref.isGroupChat(userName));
            chatList = (ListView) findViewById(R.id.chat_list);
            chatList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            chatList.setMultiChoiceModeListener(this);
            chatList.setVerticalScrollBarEnabled(false);
            if (isBulletinBroadcast) {
                LayoutInflater inflater = getLayoutInflater();
                final ViewGroup header = (ViewGroup) inflater.inflate(R.layout.message_header, chatList, false);
                chatList.addHeaderView(header, null, false);
                final String next_url = iChatPref.getBulletinNextURL(iChatPref.getUserDomain());
                if (next_url != null && !next_url.equals("0")) {
                    header.setVisibility(View.VISIBLE);
                    header.findViewById(R.id.load_older_messages).setVisibility(View.VISIBLE);
                    header.findViewById(R.id.load_older_messages).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //do something
                            getBulletinMessages(iChatPref.getBulletinNextURL(iChatPref.getUserDomain()), header);
                        }
                    });
                }
            }
            chatList.setAdapter(chatAdapter);
            if (chatAdapter != null && chatAdapter.getCount() > 0)
                chatList.setSelection(chatAdapter.getCount() - 1);
        }
        smilyButton = (ImageView) findViewById(R.id.id_attach_emoticon);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        popUp = new EmojiconsPopup(mainLayout, ChatListScreen.this);
        popUp.setSizeForSoftKeyboard();
        popUp.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                typingEditText.append(emojicon.getEmoji());
            }
        });
        typingEditText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                smilyButton.setImageResource(R.drawable.smiley_icon);
                if (popUp.isShowing()) {
                    popUp.dismiss();
                }

            }
        });
        typingEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charsequence, int i, int j, int k) {
            }

            public void onTextChanged(CharSequence charsequence, int i, int j, int k) {
                if (ChatService.xmppConectionStatus)
                    typingSendHandler.sendEmptyMessage(0);
                // recordStatusSendHandler.sendEmptyMessage(0);
                // listeningStatusSendHandler.sendEmptyMessage(0);
                // if (messageService != null)
                // messageService.sendTypingStatus(userName);
            }
        });

        popUp.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {
            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                typingEditText.dispatchKeyEvent(event);
            }
        });

        popUp.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {
            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
            }

            @Override
            public void onKeyboardClose() {
                if (popUp.isShowing()) {
                    popUp.dismiss();
                    smilyButton.setImageResource(R.drawable.smiley_icon);
                }
            }
        });


        attachOptionsDialog = new Dialog(this);
        attachOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        attachOptionsDialog.setContentView(R.layout.attach_options_dialog);
        Window window = attachOptionsDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        ImageView recordView = (ImageView) attachOptionsDialog.findViewById(R.id.id_audio_record);
//        ImageView videoRecordView = (ImageView) attachOptionsDialog.findViewById(R.id.id_video_record);
        ImageView attachPicView = (ImageView) attachOptionsDialog.findViewById(R.id.id_attach_pic);
        ImageView capturePicView = (ImageView) attachOptionsDialog.findViewById(R.id.id_camera);
        RelativeLayout pollView = (RelativeLayout) attachOptionsDialog.findViewById(R.id.relativeLayout3);
        ImageView pollImageView = (ImageView) attachOptionsDialog.findViewById(R.id.id_create_poll);
        TextView pollTextView = (TextView) attachOptionsDialog.findViewById(R.id.create_poll_lbl);

        if (iChatPref.isGroupChat(userName)/* && iChatPref.isOwner(userName, iChatPref.getUserName())*/) {
            pollImageView.setVisibility(View.VISIBLE);
//        	pollTextView.setVisibility(View.VISIBLE);
            isPollOptionShown = true;
        } else {
            pollImageView.setVisibility(View.INVISIBLE);
//    		pollTextView.setVisibility(View.INVISIBLE);
            isPollOptionShown = false;
        }
        // ImageView locationView =
        // (ImageView)attachOptionsDialog.findViewById(R.id.location_iv);
        ImageView addContactView = (ImageView) attachOptionsDialog.findViewById(R.id.id_attach_file);
        capturePicView.setOnClickListener(this);
        recordView.setOnClickListener(this);
//        videoRecordView.setOnClickListener(this);
        attachPicView.setOnClickListener(this);
        // locationView.setOnClickListener(this);
        addContactView.setOnClickListener(this);
        pollImageView.setOnClickListener(this);
        ((ImageView) attachOptionsDialog.findViewById(R.id.create_doodle)).setOnClickListener(this);

        ((ImageView) attachOptionsDialog.findViewById(R.id.share_location)).setOnClickListener(this);
        ((ImageView) attachOptionsDialog.findViewById(R.id.share_contact)).setOnClickListener(this);


        // ------------Added for voice recording -------------
        MediaEngine.initMediaEngineInstance(this.getBaseContext());
        // media_play_layout = (LinearLayout)
        // findViewById(R.id.media_play_layout);`
        mVoiceMedia = new VoiceMedia(ChatListScreen.this, ChatListScreen.this);
        chatList.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, final int scrollState) {
                // TODO Auto-generated method stub

                int threshold = 1;
                int count = chatList.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (chatList.getLastVisiblePosition() >= count - threshold) {
                        isLastIndex = true;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        int state = iChatPref.getServerGroupState(userName);
        boolean canGetGroupProfile = true;
//        if (state == GroupCreateTaskOnServer.SERVER_GROUP_NOT_CREATED
//                || state == GroupCreateTaskOnServer.SERVER_GROUP_CREATION_FAILED) {
//            canGetGroupProfile = false;
//            new GroupCreateTaskOnServer(userName, chatWindowName, usersList)
//                    .execute(GroupCreateTaskOnServer.CREATE_GROUP_REQUEST);
//        }
//        if (state == GroupCreateTaskOnServer.SERVER_GROUP_NOT_UPDATED
//                || state == GroupCreateTaskOnServer.SERVER_GROUP_UPDATION_FAILED) {
//            canGetGroupProfile = false;
//            new GroupCreateTaskOnServer(userName, chatWindowName, usersList)
//                    .execute(GroupCreateTaskOnServer.UPDATE_GROUP_REQUEST);
//        }
        if (isBulletinBroadcast)
            userIcon.setImageResource(R.drawable.broadcast_white_icon);
        else
            setProfilePic(userIcon, userIconDefault, contactNameTxt);

//        if (!isFromGroupCreation && iChatPref.isGroupChat(userName)) {
//            if (canGetGroupProfile)
//                getServerGroupProfile(userName);
//        } else if (!iChatPref.isGroupChat(userName)&&!iChatPref.isBroadCast(userName))
//            getServerUserProfile(userName);


        // else

        // Tracker t = ((SuperChatApplication)
        // SuperChatApplication.context).getTracker(TrackerName.APP_TRACKER);
        // t.setScreenName("Chat Screen");
        // t.send(new HitBuilders.AppViewBuilder().build());
        captionDialog = getScreenDialog();
//        captionDialogNew = createCaptionDialogNew();
        if (chatAdapter != null)
            chatAdapter.removeSelectedItems();


        //Check if group is deactivated
        setUIChat(false);

        if (!iChatPref.isUserExistence(userName)) {
            callOption.setVisibility(View.GONE);
        }
        if (ChatService.xmppConectionStatus) {
            networkConnection.setVisibility(View.GONE);
//       		bottomPanel.setVisibility(View.VISIBLE);
            xmppStatusView.setImageResource(R.drawable.blue_dot);
        } else {
            networkConnection.setVisibility(View.VISIBLE);
//       		bottomPanel.setVisibility(View.GONE);
            xmppStatusView.setImageResource(R.drawable.red_dot);
        }
        if (isBulletinBroadcast) {
            if (isBulletinBroadcastForAdmin()) {
                ((RelativeLayout) findViewById(R.id.bottom_write_bar1)).setVisibility(View.VISIBLE);
                attachMediaView.setVisibility(View.VISIBLE);
            } else {
                ((RelativeLayout) findViewById(R.id.bottom_write_bar1)).setVisibility(View.GONE);
                attachMediaView.setVisibility(View.GONE);
            }
        }
//       if((isSharedIDMessage && iChatPref.isDomainAdmin()) || isSharedIDDeactivated || isSharedIDAdmin){
//    	   ((RelativeLayout)findViewById(R.id.bottom_write_bar1)).setVisibility(View.GONE);
//    	   callOption.setVisibility(View.GONE);
//       }
        if (typingText != null && iChatPref != null) {

            String myMsg = iChatPref.getUserStatusMessage(userName);
            if (myMsg != null && !myMsg.equals("") && !iChatPref.isGroupChat(userName)) {

                typingText.setVisibility(TextView.GONE);
                typingText.setText(iChatPref.getUserStatusMessage(userName));
            } else {
                typingText.setVisibility(TextView.GONE);
            }
        }
        if (iChatPref.isBlocked(userName) || isBulletinBroadcast || isSharedIDMessage) {
            callOption.setVisibility(View.GONE);
        } else if (!iChatPref.isGroupChat(userName) && !iChatPref.isBroadCast(userName))
            callOption.setVisibility(View.VISIBLE);
        if (iChatPref.isGroupChat(userName) && !isSharedIDMessage && isPollActive) {
            if (isPollActive())
                createPoll.setVisibility(View.VISIBLE);
            else
                createPoll.setVisibility(View.GONE);
        } else
            createPoll.setVisibility(View.GONE);


        /**
         * Whatsapp like menu Config
         */

        mRevealView = (LinearLayout) findViewById(R.id.the_menu);
        mRevealView.setVisibility(View.INVISIBLE);

        try {
            ImageView recordViewNew = (ImageView) findViewById(R.id.id_audio_record);
            ImageView attachPicViewNew = (ImageView) findViewById(R.id.id_attach_pic);
            ImageView capturePicViewNew = (ImageView) findViewById(R.id.id_camera);
            ImageView pollImageViewNew = (ImageView) findViewById(R.id.id_create_poll);
            ImageView addContactViewNew = (ImageView) findViewById(R.id.id_attach_file);
            ImageView create_doodleNew = (ImageView) findViewById(create_doodle);
            ImageView share_locationNew = (ImageView) findViewById(R.id.share_location);
            ImageView share_contactNew = (ImageView) findViewById(R.id.share_contact);

            capturePicViewNew.setOnClickListener(this);
            recordViewNew.setOnClickListener(this);
            attachPicViewNew.setOnClickListener(this);
            addContactViewNew.setOnClickListener(this);
            pollImageViewNew.setOnClickListener(this);
            create_doodleNew.setOnClickListener(this);
            share_locationNew.setOnClickListener(this);
            share_contactNew.setOnClickListener(this);

            if (iChatPref.isGroupChat(userName)) {
                pollImageViewNew.setVisibility(View.VISIBLE);
                isPollOptionShown = true;
            } else {
                pollImageViewNew.setVisibility(View.INVISIBLE);
                isPollOptionShown = false;
            }
        } catch (Exception e) {

        }

        attachMediaView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!menuOpen) {
                    revealMenu();
                } else {
                    hideMenu();
                }
            }
        });
    }

    boolean flinging = false;

    private boolean isPollActive() {
        HashMap polls = getPollForGroup(openedGroupName);
        String poll_id = null;
        String poll_data = null;
        String poll_end_time = null;
        if (polls != null && !polls.isEmpty()) {
            Date d = new Date(new Date().getTime());
            SimpleDateFormat data_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            Iterator<Map.Entry<String, String>> iterator = polls.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pollEntry = iterator.next();
                System.out.println(pollEntry.getKey() + " :: " + pollEntry.getValue());
                //You can remove elements while iterating.
                //            iterator.remove();
                poll_id = pollEntry.getKey();
                poll_data = pollEntry.getValue();
                JSONObject jsonobj = null;
                try {
                    jsonobj = new JSONObject(poll_data);
                    if (jsonobj.has("PollEndDate") && jsonobj.getString("PollEndDate").toString().trim().length() > 0) {
                        poll_end_time = jsonobj.getString("PollEndDate").toString();
                        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a").format(d);
                        //Check for the end time, and expires then mark as inactive.

                        long ss = 0;
                        long ee = 0;
                        try {
                            ss = data_formatter.parse(current).getTime();
                            ee = data_formatter.parse(poll_end_time).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if ((ee - ss) < 0) {
                            //Poll Expired
//                            savePollIDs(poll_id, "inactive");
                        } else
                            return true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean isBulletinBroadcastForAdmin() {
        if (iChatPref.isDomainAdminORSubAdmin() && isBulletinBroadcast)
            return true;
        return false;
    }

    private void initAudioAttachTrackDialog() {
        attachAudioTrackDialog = new Dialog(this);
        attachAudioTrackDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        attachAudioTrackDialog.setContentView(R.layout.attach_audio_track_dialog);
        attachAudioTrackDialog.setCanceledOnTouchOutside(true);
        attachAudioTrackDialog.setCancelable(true);
        ((TextView) attachAudioTrackDialog.findViewById(R.id.id_select_audio_track)).setOnClickListener(this);
        ((TextView) attachAudioTrackDialog.findViewById(R.id.id_use_recorder)).setOnClickListener(this);
        ((TextView) attachAudioTrackDialog.findViewById(R.id.id_cancel_track_dialog)).setOnClickListener(this);
        Window window = attachAudioTrackDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
    }

    private void initCameraOptionDialog() {
        if (cameraOptionsDialog == null)
            cameraOptionsDialog = new Dialog(this);
        cameraOptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cameraOptionsDialog.setContentView(R.layout.camera_option_dialog);
        cameraOptionsDialog.setCancelable(true);
        cameraOptionsDialog.setCanceledOnTouchOutside(true);
        TextView takePicView = (TextView) cameraOptionsDialog.findViewById(R.id.id_take_picture_item);
        TextView recordVideoView = (TextView) cameraOptionsDialog.findViewById(R.id.id_video_record_item);
        takePicView.setOnClickListener(this);
        recordVideoView.setOnClickListener(this);
    }

    private void initAttachFileConfirmDialog() {
        if (fileConfirmationDialog == null)
            fileConfirmationDialog = new Dialog(this);
        fileConfirmationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        fileConfirmationDialog.setContentView(R.layout.attach_doc_confirmation);
        fileConfirmationDialog.setCancelable(false);
        fileConfirmationDialog.setCanceledOnTouchOutside(false);

        fileConfirmMessageView = (TextView) fileConfirmationDialog.findViewById(R.id.id_msg_file_confirm);
        TextView cancelFileView = (TextView) fileConfirmationDialog.findViewById(R.id.id_cancel_file);
        TextView sendFileView = (TextView) fileConfirmationDialog.findViewById(R.id.id_send_file);
        cancelFileView.setOnClickListener(this);
        sendFileView.setOnClickListener(this);
    }

    private void initVoiceRecorderWheelDialog() {
        if (voiceRecorderDialog == null)
            voiceRecorderDialog = new Dialog(this);
        voiceRecorderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        voiceRecorderDialog.setContentView(R.layout.voice_touch_record);
        voiceRecorderDialog.setCanceledOnTouchOutside(false);
        recordInfoView = (TextView) voiceRecorderDialog.findViewById(R.id.id_recorder_info);
        timeCounterView = (TextView) voiceRecorderDialog.findViewById(R.id.id_timer_clock);
        recordTipView = (TextView) voiceRecorderDialog.findViewById(R.id.id_text_recorder);
        recordTipView.setVisibility(TextView.GONE);
        rightImageView = ((ImageView) voiceRecorderDialog.findViewById(R.id.id_send_audio));
        rightImageView.setVisibility(ImageView.GONE);
        rightImageView.setOnClickListener(this);
        crossImageView = ((ImageView) voiceRecorderDialog.findViewById(R.id.id_cancel_audio));
        crossImageView.setVisibility(ImageView.GONE);
        crossImageView.setOnClickListener(this);

        wheelProgress = (ProgressWheel) voiceRecorderDialog.findViewById(R.id.circular_progress);//findFragmentById(position)//getView().findViewById(R.id.circular_progress);
        wheelProgress.setOnClickListener(this);
        wheelProgress.setWheelType(ProgressWheel.IDEAL_WHEEL);
        wheelProgress.setProgress(0);
        setRecordingViews(ProgressWheel.IDEAL_WHEEL);
        Window window = voiceRecorderDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        wheelProgress.refreshWheel(false);
    }

    public void showPopup(View v) {

        if (menuOpen) {
            hideMenu();
        }

        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.getMenu().add(0, 0, 0, getResources().getString(R.string.clear_chat));
        popup.getMenu().add(0, 1, 0, getResources().getString(R.string.email_chat));
//	 if(iChatPref.isGroupChat(userName)){
//		 if(iChatPref.isMute(userName))
//			 popup.getMenu().add(0,2,0,getResources().getString(R.string.unmute));
//		 else
//			 popup.getMenu().add(0,2,0,getResources().getString(R.string.mute));
//	 }else 
        if (!iChatPref.isBroadCast(userName)) {
            if (iChatPref.isMute(userName))
                popup.getMenu().add(0, 2, 0, getResources().getString(R.string.unmute));
            else
                popup.getMenu().add(0, 2, 0, getResources().getString(R.string.mute));
            if (!isSharedIDMessage && !iChatPref.isGroupChat(userName)) {
                if (iChatPref.isBlocked(userName))
                    popup.getMenu().add(0, 3, 0, getResources().getString(R.string.unblock));
                else
                    popup.getMenu().add(0, 3, 0, getResources().getString(R.string.block));
            }
        }
//	 if(!iChatPref.isGroupChat(userName) && !iChatPref.isBroadCast(userName))
//		 popup.getMenu().add(getResources().getString(R.string.delete_channel));
        popup.show();

    }

    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case 0: // clear
                if (!iChatPref.isGroupChat(userName) && !iChatPref.isBroadCast(userName))
                    showDialog("", "Do you want to clear all messages of this chat?");
                else
                    showDialog(chatWindowName, "Do you want to clear all messages of this chat?");// in '"+chatWindowName+"'.");
                return true;
            case 1: // email
                String selChat = "";
                ArrayList<String> textList = ChatDBWrapper.getInstance().getChatHistory(userName);
                for (String msg : textList)
                    selChat = selChat + msg + "\n";

                int listSize = textList.size();
                if (selChat != null && selChat.length() > 0 && !selChat.equals("")) {

                    final Intent mailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    mailIntent.setType("text/plain");
                    mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    mailIntent.putExtra(Intent.EXTRA_TEXT, selChat.trim());
                    final PackageManager pm = getPackageManager();
                    final List<ResolveInfo> matches = pm.queryIntentActivities(mailIntent, 0);
                    ResolveInfo best = null;
                    for (final ResolveInfo info : matches)
                        if (info.activityInfo.packageName.endsWith(".gm") ||
                                info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
                    if (best != null)
                        mailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                    startActivity(mailIntent);
                }
                return true;
            case 2:
                iChatPref.setMute(userName, !iChatPref.isMute(userName));
                if (iChatPref.isGroupChat(userName)) {
                    if (iChatPref.isMute(userName))
                        Toast.makeText(this, "Group Mute!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "Group Unmute!", Toast.LENGTH_SHORT).show();
                } else {
                    if (iChatPref.isMute(userName))
                        Toast.makeText(this, "Mute!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(this, "Unmute!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case 3: // isBlock Unbolck
                if (!iChatPref.isBlocked(userName))
                    showBlockUnblockConfirmDialog(getString(R.string.confirmation), getString(R.string.block_confirmation));
                else {
                    showBlockUnblockConfirmDialog(getString(R.string.confirmation), getString(R.string.unblock_confirmation));
//        	 if (Build.VERSION.SDK_INT >= 11)
//                 new BlockUnBlockTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//             else
//            	 new BlockUnBlockTask().execute();
                }
                return true;
            default:
                return false;
        }
    }

    private void showBlockUnblockConfirmDialog(final String title, final String s) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.custom_dialog_two_button);
        if (title != null) {
            ((TextView) bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
        }
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_send)).setText(getString(R.string.yes));
        ((TextView) bteldialog.findViewById(R.id.id_cancel)).setText(getString(R.string.no));
        ((TextView) bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                if (Build.VERSION.SDK_INT >= 11)
                    new BlockUnBlockTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    new BlockUnBlockTask().execute();

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

    public void blockTask() {
        if (Build.VERSION.SDK_INT >= 11)
            new BlockUnBlockTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            new BlockUnBlockTask().execute();
    }

    private class BlockUnBlockTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        boolean isStatusChanged = false;

        BlockUnBlockTask() {
        }

        protected void onPreExecute() {

            dialog = ProgressDialog.show(ChatListScreen.this, "", "Please wait...", true);

            // progressBarView.setVisibility(ProgressBar.VISIBLE);
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            boolean isStatusChanged = false;
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
                    Toast.makeText(ChatListScreen.this, getString(R.string.block_successful), Toast.LENGTH_SHORT).show();
                    callOption.setVisibility(View.GONE);
                } else {
                    Toast.makeText(ChatListScreen.this, getString(R.string.unblock_successful), Toast.LENGTH_SHORT).show();
                    callOption.setVisibility(View.VISIBLE);
                }

            } else {
                Toast.makeText(ChatListScreen.this, "Please try after some time.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Camera Option");
        menu.add(0, 1, 0, "Take picture");
        menu.add(0, 2, 0, "Recording video");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Toast.makeText(getApplicationContext(), "calling code", Toast.LENGTH_LONG).show();
        } else if (item.getItemId() == 2) {
            Toast.makeText(getApplicationContext(), "sending sms code", Toast.LENGTH_LONG).show();
        } else {
            return false;
        }
        return true;
    }

    public boolean showUpdatedPollresults(String poll_id) {
        boolean active_polls = true;
        HashMap<String, String> map = getPollForGroup(openedGroupName);
        if (poll_id == null)//Get, If any active poll is there
            openedPollID = getActivePollID();
        else
            openedPollID = poll_id;
        if (map != null) {
            String data = (String) map.get(openedPollID);
            String PollMessageType = null;
            if (data != null) {
                try {
                    JSONObject jsonobj = new JSONObject(data);
                    if (jsonobj.has("PollMessageType") && jsonobj.getString("PollMessageType").toString().trim().length() > 0) {
                        PollMessageType = jsonobj.getString("PollMessageType").toString();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (PollMessageType.equals("1"))
                    showPoll(data, false);
                else
                    showPollView(data);
                isPollResultPage = true;
            } else {
                if (poll != null && poll.isShowing())
                    poll.dismiss();
                Toast.makeText(ChatListScreen.this, "Oops! no active polls.", Toast.LENGTH_SHORT).show();
                active_polls = false;
            }
        } else {
            if (poll != null && poll.isShowing())
                poll.dismiss();
            Toast.makeText(ChatListScreen.this, "Oops! no active polls.", Toast.LENGTH_SHORT).show();
            active_polls = false;
        }
        return active_polls;
    }

    public boolean checkForActivePolls(String poll_id) {
        boolean active_polls = false;
        HashMap<String, String> map = getPollForGroup(openedGroupName);
        if (poll_id == null)//Get, If any active poll is there
            openedPollID = getActivePollID();
        else
            openedPollID = poll_id;
        if (map != null) {
            String data = (String) map.get(openedPollID);

            if (data != null) {
                active_polls = true;
            } else {
                active_polls = false;
            }
        } else {
            active_polls = false;
        }
        return active_polls;
    }

    public String getAndUpdatedPollresultsToSendToAllMembers(String poll_id) {
        HashMap<String, String> map = getPollForGroup(openedGroupName);
        String latest_poll_data = null;
        if (poll_id == null)
            openedPollID = getActivePollID();
        else
            openedPollID = poll_id;
        if (map != null)
            latest_poll_data = (String) map.get(openedPollID);
        return latest_poll_data;
    }

    ImageView mediaPreview;
    ImageView thumbPreview;
    ImageView mediaPreviewNew;
    LinearLayout captionTopOptions;
    EditText captionField;
    EditText captionFieldNew;
    String captionText = "";
    ImageButton videoPlayButton;
    ImageView cropImg, rotateImg;

    private Dialog getScreenDialog() {
//        Dialog screenDialog = new Dialog(this);
        Dialog screenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        screenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        screenDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        screenDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        screenDialog.setContentView(R.layout.caption_dialog);
        captionField = (EditText) screenDialog.findViewById(R.id.id_caption_field);
        videoPlayButton = (ImageButton) screenDialog.findViewById(R.id.video_play_btn);
        captionField.setHorizontallyScrolling(false);
        captionField.setMaxLines(5);
        videoPlayButton.setOnClickListener(this);
        captionField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                boolean handled = false;

                // Some phones disregard the IME setting option in the xml,
                // instead
                // they send IME_ACTION_UNSPECIFIED so we need to catch that
                if (EditorInfo.IME_ACTION_DONE == actionId || EditorInfo.IME_ACTION_UNSPECIFIED == actionId) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    handled = true;
                }

                return handled;
            }
        });
        LinearLayout llMainLayoutEmoji = (LinearLayout) screenDialog.findViewById(R.id.llMainLayoutEmoji);
        TextView cancelBtn = (TextView) screenDialog.findViewById(R.id.id_cancel_btn);
        TextView sendBtn = (TextView) screenDialog.findViewById(R.id.id_send_btn);
        ImageView backImg = (ImageView) screenDialog.findViewById(R.id.id_back_arrow);
        cropImg = (ImageView) screenDialog.findViewById(R.id.id_crop_image);
        rotateImg = (ImageView) screenDialog.findViewById(R.id.id_rotate_image);

        captionTopOptions = (LinearLayout) screenDialog.findViewById(R.id.id_top_options);
        mediaPreview = (ImageView) screenDialog.findViewById(R.id.id_media_preview);
        thumbPreview = (ImageView) screenDialog.findViewById(R.id.id_media_thumb_view);
        captionText = "";
        cancelBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        mediaPreview.setOnClickListener(this);
//        backImg.setOnClickListener(this);
        cropImg.setOnClickListener(this);
        rotateImg.setOnClickListener(this);
        Window window = screenDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;// BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        // Munish Code
        {
            ImageView id_attach_emoticon_caption = (ImageView) screenDialog.findViewById(R.id.id_attach_emoticon_caption);

            final EmojiconsPopup popUpcaption = new EmojiconsPopup(llMainLayoutEmoji, this);
            popUpcaption.setSizeForSoftKeyboard();
            popUpcaption.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {
                @Override
                public void onEmojiconClicked(Emojicon emojicon) {
                    captionField.append(emojicon.getEmoji());
                }
            });
            captionField.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (popUpcaption.isShowing()) {
                        popUpcaption.dismiss();
                    }

                }
            });

            popUpcaption.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {
                @Override
                public void onEmojiconBackspaceClicked(View v) {
                    KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                    captionField.dispatchKeyEvent(event);
                }
            });

            popUpcaption.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {
                @Override
                public void onKeyboardOpen(int keyBoardHeight) {
                }

                @Override
                public void onKeyboardClose() {
                    if (popUpcaption.isShowing()) {
                        popUpcaption.dismiss();
                    }
                }
            });

            id_attach_emoticon_caption.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!popUpcaption.isShowing()) {
                        captionField.requestFocus();
                        popUpcaption.showAtBottomPending();
                        // the keyboard is visible
                        inputManager.showSoftInput(captionField, InputMethodManager.SHOW_IMPLICIT);
                    } else if (popUpcaption.isShowing()) {
                        popUpcaption.dismiss(); // hide popUp with emojicons
                    }
                }
            });
        }
        return screenDialog;
    }

    private int getAudioFileDuration(String file_path) {
        int duration = 0;
        try {
            MediaPlayer mp = MediaPlayer.create(this, Uri.parse(file_path));
            duration = mp.getDuration();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return duration;
    }

    private Dialog createCaptionDialogNew() {
//      Dialog screenDialog = new Dialog(this);
        Dialog screenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        screenDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        screenDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        screenDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        screenDialog.setContentView(R.layout.caption_dialog_new);
        screenDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (isPlaying)
                    stopVoicePlay();
            }
        });
        captionFieldNew = (EditText) screenDialog.findViewById(R.id.id_caption_field);
        playSenderView = (ImageView) screenDialog.findViewById(R.id.media_play);
        playSenderSeekBar = (SeekBar) screenDialog.findViewById(R.id.mediavoicePlayingDialog_progressbar);
        playSenderMaxTimeText = (TextView) screenDialog.findViewById(R.id.audio_counter_max);

        myVoicePlayer = new RTMediaPlayer();
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                listeningStatusSendHandler.sendEmptyMessage(0);

            }
        };
        totalAudioLength = getAudioFileDuration(mediaUrl) / 1000;
        if (totalAudioLength > 0) {
            final byte min = (byte) (totalAudioLength / 60);
            final byte sec = (byte) (totalAudioLength % 60);
            globalSeekBarValue = 0;
            globalSeekBarMaxValue = 0;
            if (min < 9)
                playSenderMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
            else
                playSenderMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
        }

//      videoPlayButton = (ImageButton) screenDialog.findViewById(R.id.video_play_btn);
//    videoPlayButton.setOnClickListener(this);
        playSenderView.setOnClickListener(this);
        captionFieldNew.setHorizontallyScrolling(false);
        captionFieldNew.setMaxLines(5);
        captionFieldNew.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                boolean handled = false;

                // Some phones disregard the IME setting option in the xml,
                // instead
                // they send IME_ACTION_UNSPECIFIED so we need to catch that
                if (EditorInfo.IME_ACTION_DONE == actionId || EditorInfo.IME_ACTION_UNSPECIFIED == actionId) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    handled = true;
                }

                return handled;
            }
        });
        TextView cancelBtn = (TextView) screenDialog.findViewById(R.id.id_cancel_btn);
        TextView sendBtn = (TextView) screenDialog.findViewById(R.id.id_send_btn);
//      ImageView backImg = (ImageView) screenDialog.findViewById(R.id.id_back_arrow);
//      ImageView cropImg = (ImageView) screenDialog.findViewById(R.id.id_crop_image);
//      ImageView rotateImg = (ImageView) screenDialog.findViewById(R.id.id_rotate_image);

//      captionTopOptions = (LinearLayout) screenDialog.findViewById(R.id.id_top_options);
        mediaPreviewNew = (ImageView) screenDialog.findViewById(R.id.id_media_preview);
        captionText = "";
        cancelBtn.setOnClickListener(this);
        sendBtn.setOnClickListener(this);
        mediaPreviewNew.setOnClickListener(this);
//      backImg.setOnClickListener(this);
//      cropImg.setOnClickListener(this);
//      rotateImg.setOnClickListener(this);
        Window window = screenDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;// BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        return screenDialog;
    }

    RelativeLayout bottomLayout;

    private void setAudioRecorderUIPoint() {
        recordPanel = (RelativeLayout) findViewById(R.id.id_record_panel);// new
        // FrameLayout(this);
        recordPanel.setVisibility(RelativeLayout.GONE);
        recordPanel.setBackgroundColor(0xff0a9cd5);

        // bottomLayout = (RelativeLayout)findViewById(R.id.bottom_write_bar1);
        // FrameLayout.LayoutParams params =
        // LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,
        // LayoutHelper.WRAP_CONTENT, Gravity.BOTTOM);
        //// (params).addRule(RelativeLayout.LEFT_OF, R.id.id_mic);
        // bottomLayout.addView(recordPanel, params);
        recordPanel.setGravity(Gravity.CENTER);
        slideText = new RelativeLayout(this);
        closeView = (TextView) recordPanel.findViewById(R.id.id_record_close);
        closeView.setVisibility(TextView.GONE);
//        slideText.setOrientation(LinearLayout.HORIZONTAL);
//        recordPanel.addView(slideText, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
//                Gravity.CENTER_VERTICAL, 0, 0, 0, 0, RelativeLayout.ALIGN_PARENT_RIGHT));

//        ImageView imageView = new ImageView(this);
//        imageView.setImageResource(R.drawable.back_arrow_green);
//        slideText.addView(imageView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
//                Gravity.CENTER_VERTICAL, 0, 1, 0, 0));

//        TextView textView = new TextView(this);
//        textView.setText("Slide To Cancel");
//        textView.setTextColor(0xff999999);
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
//        slideText.addView(textView, LayoutHelper.createLinear(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT,
//                Gravity.CENTER_VERTICAL, 0, 0, 0, 0));


        micView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (iChatPref.isBlocked(userName)) {
                    showDialog(getString(R.string.block_alert));
                    return false;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    try {
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(20);
                        if (chatAdapter != null && chatAdapter.myVoicePlayer != null) {
                            try {
                                chatAdapter.myVoicePlayer.reset();
                                chatAdapter.myVoicePlayer.clear();
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
//                    if (view != null) {
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//                    }
                    typingEditText.setVisibility(View.INVISIBLE);
                    editTextLine.setVisibility(View.INVISIBLE);
                    attachEmoView.setVisibility(ImageView.INVISIBLE);
                    attachMediaView.setVisibility(ImageView.INVISIBLE);
                    closeView.setVisibility(TextView.GONE);
                    // if (parentFragment != null) {
                    // String action;
                    // TLRPC.Chat currentChat;
                    // if ((int) dialog_id < 0) {
                    // currentChat =
                    // MessagesController.getInstance().getChat(-(int)
                    // dialog_id);
                    // if (currentChat != null && currentChat.participants_count
                    // > MessagesController.getInstance().groupBigSize) {
                    // action = "bigchat_upload_audio";
                    // } else {
                    // action = "chat_upload_audio";
                    // }
                    // } else {
                    // action = "pm_upload_audio";
                    // }
                    // if (!MessagesController.isFeatureEnabled(action,
                    // parentFragment)) {
                    // return false;
                    // }
                    // }
                    // startedDraggingX = -1;
                    // MediaController.getInstance().startRecording(dialog_id,
                    // replyingMessageObject);
                    recordingAudio = true;
                    isInvalidAudio = false;
                    updateAudioRecordIntefrace();
                    micView.getParent().requestDisallowInterceptTouchEvent(true);
                    if (mVoiceMedia != null && mVoiceMedia.getMediaState() == Constants.UI_STATE_IDLE) {
                        // tapToReordView.setVisibility(View.GONE);
                        // audioRecordLayout.setVisibility(View.VISIBLE);
                        if (recordTimeText != null) {
                            // if(!isRecordingStarted){
                            recordTimeText.setText("00:00");
                            timerClockStart();
                        }
                        mVoiceMedia.startRecording(getString(R.string.done), getString(R.string.cancel), null,
                                Constants.MAX_AUDIO_RECORD_TIME_REST);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                        || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    // attachEmoView.setVisibility(ImageView.VISIBLE);
                    // attachMediaView.setVisibility(ImageView.VISIBLE);
                    // typingEditText.setVisibility(View.VISIBLE);

                    // startedDraggingX = -1;
                    // MediaController.getInstance().stopRecording(true);
                    if (recordTimeText != null && recordTimeText.isShown()) {
                        int second = calander.get(Calendar.SECOND);
                        if (second < 2) {
                            isInvalidAudio = true;
                        } else {
                            isInvalidAudio = false;
                        }
                        recordingAudio = false;
                        updateAudioRecordIntefrace();
                        clockTimer.cancel();
                        clockTimer = null;
                        clockTimerTask = null;
                        mVoiceMedia.stopRec();

                        recordTimeText.setVisibility(View.INVISIBLE);
                    }
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {// &&
                    // recordingAudio)
                    // {
                    float x = motionEvent.getX();
//                    closeView.setVisibility(TextView.VISIBLE);
                    Log.d(TAG, "ACTION_MOVE values : " + x + " , " + distCanMove);
                    if (x < -distCanMove) {

                        // MediaController.getInstance().stopRecording(false);
                        isInvalidAudio = true;
                        if (clockTimer != null) {
                            clockTimer.cancel();
                            clockTimer = null;
                            clockTimerTask = null;
                            mVoiceMedia.stopRec();
                            recordingAudio = false;
                            updateAudioRecordIntefrace();
                        }
                        if (recordTimeText != null)
                            recordTimeText.setVisibility(View.INVISIBLE);

                    }
                    //
                    x = x + micView.getX();
                    // RelativeLayout.LayoutParams params =
                    // (RelativeLayout.LayoutParams)
                    // slideText.getLayoutParams();
                    // if (startedDraggingX != -1) {
                    // float dist = (x - startedDraggingX);
                    // params.leftMargin = AndroidUtilities.dp(30) + (int) dist;
                    // slideText.setLayoutParams(params);
                    // float alpha = 1.0f + dist / distCanMove;
                    // if (alpha > 1) {
                    // alpha = 1;
                    // } else if (alpha < 0) {
                    // alpha = 0;
                    // }
                    // ViewProxy.setAlpha(slideText, alpha);
                    // }
                    // if (x <= ViewProxy.getX(slideText) + slideText.getWidth()
                    // + AndroidUtilities.dp(30)) {
                    // if (startedDraggingX == -1) {
                    // startedDraggingX = x;
                    // distCanMove = (recordPanel.getMeasuredWidth() -
                    // slideText.getMeasuredWidth() - AndroidUtilities.dp(48)) /
                    // 2.0f;
                    // if (distCanMove <= 0) {
                    // distCanMove = AndroidUtilities.dp(80);
                    // } else if (distCanMove > AndroidUtilities.dp(80)) {
                    // distCanMove = AndroidUtilities.dp(80);
                    // }
                    // }
                    // }
                    // if (params.leftMargin > AndroidUtilities.dp(30)) {
                    // params.leftMargin = AndroidUtilities.dp(30);
                    // slideText.setLayoutParams(params);
                    // ViewProxy.setAlpha(slideText, 1);
                    // startedDraggingX = -1;
                    // }
                }
                view.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    boolean recordingAudio;
    int audioInterfaceState = 0;
    WakeLock mWakeLock;
    private TextView recordTimeText;
    String lastTimeString;
    ObjectAnimator runningAnimationAudio;

    private void updateAudioRecordIntefrace() {
        if (recordingAudio) {
            if (audioInterfaceState == 1) {
                return;
            }
            audioInterfaceState = 1;
            try {
                if (mWakeLock == null) {
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                            "audio record lock");
                    mWakeLock.acquire();
                }
            } catch (Exception e) {
                Log.d("tmessages", "");
            }

            recordPanel.setVisibility(View.VISIBLE);
            recordTimeText = (TextView) recordPanel.findViewById(R.id.id_record_timer);//new TextView(this);
            recordTimeText.setVisibility(TextView.VISIBLE);
//            recordTimeText.setText("00:00");
//            recordTimeText.setTextColor(0xffff0000);
//            recordTimeText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
//            recordTimeText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//            recordPanel.addView(recordTimeText, LayoutHelper.createRelative(LayoutHelper.WRAP_CONTENT,
//                    LayoutHelper.WRAP_CONTENT, 5, 0, 0, 5, RelativeLayout.CENTER_VERTICAL));
            lastTimeString = null;

//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) slideText.getLayoutParams();
//            params.leftMargin = SuperChatApplication.dp(30);
//            slideText.setLayoutParams(params);
//            // ViewProxy.setAlpha(slideText, 1);
//            slideText.setAlpha(1);
            // ViewProxy.setX(recordPanel, AndroidUtilities.displaySize.x);
            recordPanel.setX(SuperChatApplication.displaySize.x);
            if (runningAnimationAudio != null) {
                runningAnimationAudio.cancel();
            }
            runningAnimationAudio = ObjectAnimator.ofFloat(recordPanel, "translationX", 0).setDuration(300);
            runningAnimationAudio.addListener(new AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (runningAnimationAudio != null && runningAnimationAudio.equals(animator)) {
                        // ViewProxy.setX(recordPanel, 0);
                        recordPanel.setX(0);
                    }
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    // TODO Auto-generated method stub

                }
            });
            runningAnimationAudio.setInterpolator(new AccelerateDecelerateInterpolator());
            runningAnimationAudio.start();
        } else {
            if (mWakeLock != null) {
                try {
                    mWakeLock.release();
                    mWakeLock = null;
                } catch (Exception e) {
                }
            }
            // AndroidUtilities.unlockOrientation(parentActivity);
            if (audioInterfaceState == 0) {
                return;
            }
            audioInterfaceState = 0;

            if (runningAnimationAudio != null) {
                runningAnimationAudio.cancel();
            }
            recordPanel.setVisibility(View.GONE);
            attachEmoView.setVisibility(ImageView.VISIBLE);
            attachMediaView.setVisibility(ImageView.VISIBLE);
            typingEditText.setVisibility(View.VISIBLE);
            editTextLine.setVisibility(View.VISIBLE);
//            runningAnimationAudio = ObjectAnimator
//                    .ofFloat(recordPanel, "translationX", SuperChatApplication.displaySize.x).setDuration(300);
//            runningAnimationAudio.addListener(new AnimatorListener() {
//
//                public void onAnimationEnd(Animator animator) {
//                    if (runningAnimationAudio != null && runningAnimationAudio.equals(animator)) {
////                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) slideText.getLayoutParams();
////                        params.leftMargin = SuperChatApplication.dp(30);
////                        slideText.setLayoutParams(params);
////                        // ViewProxy.setAlpha(slideText, 1);
////                        slideText.setAlpha(1);
//                        recordPanel.setVisibility(View.GONE);
//                    }
//                    attachEmoView.setVisibility(ImageView.VISIBLE);
//                    attachMediaView.setVisibility(ImageView.VISIBLE);
//                    typingEditText.setVisibility(View.VISIBLE);
//                }
//
//                @Override
//                public void onAnimationStart(Animator animation) {
//                    // TODO Auto-generated method stub
//
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//                    // TODO Auto-generated method stub
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//                    // TODO Auto-generated method stub
//
//                }
//            });
//            runningAnimationAudio.setInterpolator(new AccelerateDecelerateInterpolator());
//            runningAnimationAudio.start();
        }
    }

    private final Handler typingSendHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (messageService != null)
                messageService.sendTypingStatus(userName);
        }
    };
    private final Handler recordStatusSendHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (messageService != null)
                messageService.sendRecordingStatus(userName);
        }
    };
    public final Handler listeningStatusSendHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (messageService != null)
                messageService.sendListeningStatus(userName);
        }
    };

    public static Bitmap rotateImage(String path, Bitmap bm) {
        int orientation = 1;
        try {
            ExifInterface exifJpeg = new ExifInterface(path);
            orientation = exifJpeg.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            //// orientation =
            //// Integer.parseInt(exifJpeg.getAttribute(ExifInterface.TAG_ORIENTATION));
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

    public void onAttachEmoticonClickOld(View view) {
        if (!isTouchEditbox) { // For first time to set requestFocus
            typingEditText.requestFocus();
            if (!popUp.isShowing()) {
                popUp.showAtBottomPending();
                // the keyboard is visible
                inputManager.showSoftInput(typingEditText, InputMethodManager.SHOW_IMPLICIT);
                smilyButton.setImageResource(R.drawable.smiley_off);
            }
            isTouchEditbox = true;
        } else {
            if (!popUp.isShowing()) {
                popUp.showAtBottomPending();
                // the keyboard is visible
                inputManager.showSoftInput(typingEditText, InputMethodManager.SHOW_IMPLICIT);
                smilyButton.setImageResource(R.drawable.smiley_off);
            } else if (popUp.isShowing()) {
                smilyButton.setImageResource(R.drawable.smiley_icon);
                popUp.dismiss(); // hide popUp with emojicons
            }
        }
    }

    public void onAttachEmoticonClick(View view) {
        {
            if (!popUp.isShowing()) {
                typingEditText.requestFocus();
                popUp.showAtBottomPending();
                // the keyboard is visible
                inputManager.showSoftInput(typingEditText, InputMethodManager.SHOW_IMPLICIT);
                smilyButton.setImageResource(R.drawable.smiley_off);
            } else if (popUp.isShowing()) {
                smilyButton.setImageResource(R.drawable.smiley_icon);
                popUp.dismiss(); // hide popUp with emojicons
            }
        }
    }

    private void timerClockStart() {
        if (clockTimer == null)
            clockTimer = new Timer();
        if (clockTimerTask == null)
            clockTimerTask = new TimerTask() {

                @Override
                public void run() {

                    currentClockTime += 1000;
                    myHandler.sendEmptyMessage(1);
                    recordStatusSendHandler.sendEmptyMessage(0);
                    Log.d(TAG, "Clock in Handler: " + currentClockTime);
                    // if((startClockTime-currentClockTime)>=((WAITING_TIME_MINUTE*60*1000)+(WAITING_TIME_SECOND*1000))){
                    // myHandler.sendEmptyMessage(2);
                    // cancel();
                    // clockTimer = null;
                    // clockTimerTask = null;
                    // }

                }
            };
        calander.setTimeInMillis(System.currentTimeMillis());
        calander.set(Calendar.MINUTE, 0);
        calander.set(Calendar.SECOND, 0);
        currentClockTime = calander.getTimeInMillis();
        clockTimer.schedule(clockTimerTask, 1000, 1000);
    }

    Timer wheelTimer;

    private void startWheel() {
        if (wheelTimer != null) {
            wheelTimer.cancel();
        }
        wheelTimer = new Timer();
        wheelTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (wheelProgress.getWheelType() != ProgressWheel.PAUSE_WHEEL) {
                    currentClockTime += 833;
                    if (wheelProgress.getProgress() < 360)
                        wheelHandler.sendEmptyMessage(0);
                    else {
                        stopPlayingHandler.sendEmptyMessage(0);
                        cancel();
                    }
                }
            }
        }, 1000, 833);
        calander.setTimeInMillis(System.currentTimeMillis());
        calander.set(Calendar.MINUTE, 0);
        calander.set(Calendar.SECOND, 0);
        currentClockTime = calander.getTimeInMillis();
//             clockTimer.schedule(clockTimerTask, 1000, 1000);
    }

    private final Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int type = msg.what;
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            calander.setTimeInMillis(currentClockTime);
            String msgTime = format.format(calander.getTime());

            Log.d(TAG, "Clock in Handler " + msgTime);
            if (recordTimeText != null)
                recordTimeText.setText(msgTime);

        }
    };
    Handler wheelHandler = new Handler() {

        @Override
        public void handleMessage(android.os.Message msg) {

            wheelProgress.incrementProgress();
            int progress = wheelProgress.getProgress();
//				if(progress>=0){
//					byte min = (byte) (progress/60);
//					byte sec = (byte) (progress%60);
//					timeCounterView.setText(min+":"+sec);
//				}
            SimpleDateFormat format = new SimpleDateFormat("mm:ss");
            calander.setTimeInMillis(currentClockTime);
            String msgTime = format.format(calander.getTime());

            Log.d(TAG, "Clock in Handler " + msgTime);
            if (timeCounterView != null)
                timeCounterView.setText(msgTime);
        }
    };
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    private void setProfilePic(ImageView view, ImageView view_default, String displayName) {
        String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName);
        int type = 0;
        if (SharedPrefManager.getInstance().isSharedIDContact(userName))
            type = 1;
        if (type == 1)
            groupPicId = SharedPrefManager.getInstance().getSharedIDFileId(userName);
        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
        if (bitmap != null) {
            view_default.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            view.setImageBitmap(bitmap);
            String profilePicUrl = groupPicId + ".jpg";// AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + Constants.contentProfilePhoto + profilePicUrl;
            view.setTag(filename);
        } else if (groupPicId != null && !groupPicId.equals("") && !groupPicId.equals("clear")) {
            String profilePicUrl = groupPicId;// AppConstants.media_get_url+
            if (!profilePicUrl.contains(".jpg"))
                profilePicUrl = groupPicId + ".jpg";// AppConstants.media_get_url+

            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + Constants.contentProfilePhoto + profilePicUrl;
            view.setTag(filename);
            File file1 = new File(filename);
            if (file1.exists()) {

                // view.setImageURI(Uri.parse(filename));
                view_default.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                setThumb(view, filename, groupPicId);
                view.setBackgroundDrawable(null);
            } else {
                //Downloading the file
                view_default.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
//				(new ProfilePicDownloader()).download(Constants.media_get_url+groupPicId+".jpg", (RoundedImageView)view, null);
                if (Build.VERSION.SDK_INT >= 11)
                    new BitmapDownloader((RoundedImageView) view).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, groupPicId, BitmapDownloader.THUMB_REQUEST);
                else
                    new BitmapDownloader((RoundedImageView) view).execute(groupPicId, BitmapDownloader.THUMB_REQUEST);
            }
        } else if (iChatPref.isBroadCast(userName)) {
            view_default.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.broadcast_white_icon);
        } else if (iChatPref.isGroupChat(userName)) {
            view_default.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.group_white_icon);
        } else if (type == 1) {
            view_default.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.small_helpdesk);
        } else {
            if (displayName != null) {
                try {
                    String name_alpha = String.valueOf(displayName.charAt(0));
                    if (displayName.contains(" ") && displayName.indexOf(' ') < (displayName.length() - 1))
                        name_alpha += displayName.substring(displayName.indexOf(' ') + 1).charAt(0);
                    TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(displayName));
                    view.setVisibility(View.GONE);
                    view_default.setVisibility(View.VISIBLE);
                    view_default.setImageDrawable(drawable);
                    view_default.setBackgroundColor(Color.TRANSPARENT);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

//            if (SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
//                view.setImageResource(R.drawable.female_default);
//            else
//                view.setImageResource(R.drawable.male_default); //
        }
    }

    private void setThumb(ImageView imageViewl, String path, String id) {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeFile(path, bfo);
//            bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
            bm = rotateImage(path, bm);
//            bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
        } catch (Exception ex) {

        }
        if (bm != null) {
            imageViewl.setImageBitmap(bm);
//            SuperChatApplication.addBitmapToMemoryCache(id, bm);
        } else {
            try {
                imageViewl.setImageURI(Uri.parse(path));
            } catch (Exception e) {

            }
        }
    }

    public void uploadPicture(final String imgPath) {
        String packetID = null;
        if (imgPath != null && imgPath.length() > 0) {
            try {
                String thumbImg = null;
                // if (messageService != null){
                // chatAdapter.setChatService(messageService);
                // thumbImg = MyBase64.encode(getByteArrayOfThumbnail(imgPath));
                // packetID = messageService.sendMediaMessage(senderName, "",
                // imgPath,thumbImg,XMPPMessageType.atMeXmppMessageTypeGroupImage);
                picUploader = new ProfilePicUploader(this, null, true);
                if (Build.VERSION.SDK_INT >= 11)
                    picUploader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imgPath, packetID, "",
                            ProfilePicUploader.GROUP_CHAT_PICUTRE, "");
                else
                    picUploader.execute(imgPath, packetID, "", ProfilePicUploader.GROUP_CHAT_PICUTRE, "");
                // }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("1" + ex.toString());
            }
        }
    }

//    public void updateGroupUsersList(ArrayList<String> displayList) {
//        if (iChatPref.isGroupChat(userName) || iChatPref.isBroadCast(userName)) {
//            // ChatDBWrapper.getInstance().getUsersOfGroup(senderName);
//            String list = displayList.toString();
//            if (list == null) {
//                list = "";
//            }
//            if (list.contains("[")) {
//                list = list.replace("[", "");
//                list = list.replace("]", "");
//            }
//            typingText.setText(list);
//        }
//    }

    boolean isFromOnResume = false;

    public void onResume() {
        super.onResume();
        onForeground = true;
        currentUser = (userName + "");
        bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);
        ChatService.setConnectionStatusListener(this);
        if (!isFreshLaunch) {
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    isFromOnResume = true;
                    notifyChatRecieve1(userName, null);
                    // refreshOnlineGroupUser();
                    cancel();
                }
            }, 100);

        }
        //Check if any text was retained
        if (HomeScreen.textDataRetain.get(userName) != null) {
            if (typingEditText != null)
                typingEditText.setText(HomeScreen.textDataRetain.remove(userName));
        }
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                SharedPrefManager prefObj = SharedPrefManager.getInstance();
                int userUnreadMsgCount = prefObj.getChatCountOfUser(userName);
                if (userUnreadMsgCount > 0) {
                    int totalChatCount = prefObj.getChatCounter(prefObj.getUserDomain()) - userUnreadMsgCount;
                    if (totalChatCount < 0)
                        totalChatCount = 0;
                    prefObj.saveChatCounter(prefObj.getUserDomain(), totalChatCount);
                    prefObj.saveChatCountOfUser(userName, 0);
                    int messageCount = prefObj.getChatCounter(prefObj.getUserDomain());
                    // ShortcutBadger.setBadge(getApplicationContext(),
                    // badgeCount);
                    ShortcutBadger.with(SuperChatApplication.context).count(messageCount);
                } else
                    ShortcutBadger.with(SuperChatApplication.context).count(0);
                cancel();
            }
        }, 100);

        isFreshLaunch = false;
        bindService(new Intent(this, SinchService.class), mCallConnection, Context.BIND_AUTO_CREATE);

        //Fetch all the polls for this group and check which one is expired
        //Also update in the poll table for active and inactive status w.r.t current time
        HashMap polls = getPollForGroup(openedGroupName);
        String poll_id = null;
        String poll_data = null;
        String poll_end_time = null;
        if (polls != null && !polls.isEmpty()) {
            Date d = new Date(new Date().getTime());
            SimpleDateFormat data_formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            Iterator<Map.Entry<String, String>> iterator = polls.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pollEntry = iterator.next();
                System.out.println(pollEntry.getKey() + " :: " + pollEntry.getValue());
                //You can remove elements while iterating.
                //            iterator.remove();
                poll_id = pollEntry.getKey();
                poll_data = pollEntry.getValue();
                JSONObject jsonobj = null;
                try {
                    jsonobj = new JSONObject(poll_data);
                    if (jsonobj.has("PollEndDate") && jsonobj.getString("PollEndDate").toString().trim().length() > 0) {
                        poll_end_time = jsonobj.getString("PollEndDate").toString();
                        String current = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(d);
                        //Check for the end time, and expires then mark as inactive.

                        long ss = 0;
                        long ee = 0;
                        try {
                            ss = data_formatter.parse(current).getTime();
                            ee = data_formatter.parse(poll_end_time).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if ((ee - ss) < 0) {
                            //Poll Expired
                            savePollIDs(poll_id, "inactive");
                        } else
                            createPoll.setVisibility(View.VISIBLE);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!iChatPref.isMyExistence()) {
            showExitDialog("You have been removed.");
        }
        //remove poll icon if active polls are not there.
//        if(!checkForActivePolls(openedPollID) && createPoll != null)
//        	createPoll.setVisibility(View.GONE);
        if (isSharedIDMessage) {
            callOption.setVisibility(View.GONE);
        }
        if (isSharedIDDeactivated)
            Toast.makeText(this, "This Official ID is deactivated!", Toast.LENGTH_SHORT).show();

        setBroadCastScreenMode(null);
    }

    public static int prevIndex = 0;

    protected void onPause() {
        onForeground = false;
        isFromOnResume = false;

        if (clockTimer != null) {
            isInvalidAudio = true;
            clockTimer.cancel();
            clockTimer = null;
            clockTimerTask = null;
            mVoiceMedia.stopRec();
            recordingAudio = false;
            updateAudioRecordIntefrace();
        }
        if (recordTimeText != null)
            recordTimeText.setVisibility(View.INVISIBLE);

        if (chatList != null)
            prevIndex = chatList.getFirstVisiblePosition();
        currentUser = "";
        // SharedPrefManager prefObj = SharedPrefManager.getInstance();
        // prefObj.saveChatCountOfUser(userName, 0);
        if (messageService != null)
            messageService.setChatVisibilty(onForeground);
        SharedPrefManager prefObj = SharedPrefManager.getInstance();
        int userUnreadMsgCount = prefObj.getChatCountOfUser(userName);
        if (userUnreadMsgCount > 0) {
            int totalChatCount = prefObj.getChatCounter(prefObj.getUserDomain()) - userUnreadMsgCount;
            if (totalChatCount < 0)
                totalChatCount = 0;
            prefObj.saveChatCounter(prefObj.getUserDomain(), totalChatCount);
            prefObj.saveChatCountOfUser(userName, 0);
            int messageCount = prefObj.getChatCounter(prefObj.getUserDomain());
            // ShortcutBadger.setBadge(getApplicationContext(), badgeCount);
            ShortcutBadger.with(SuperChatApplication.context).count(messageCount);
        } else
            ShortcutBadger.with(SuperChatApplication.context).count(0);
        //Do not stop voice in background.
//        chatAdapter.myVoicePlayer.reset();
//        chatAdapter.myVoicePlayer.clear();
        mVoiceMedia.autoStop();

        // ChatClient.getInstance().sendOffLineMessages();
//        try {
//            unbindService(mMessageConnection);
//            unbindService(mCallConnection);
//        } catch (Exception e) {
//            // Just ignore that
//            Log.d("MessageHistoryScreen", "Unable to un bind");
//        }
        super.onPause();
    }

    protected void onDestroy() {
        usersList.clear();
        if (!ChatListAdapter.cacheKeys.isEmpty())
            for (String key : ChatListAdapter.cacheKeys) {
                SuperChatApplication.removeBitmapFromMemCache(key);
            }
        // chatAdapter.clearMap();
        try {
            unbindService(mMessageConnection);
            unbindService(mCallConnection);
        } catch (Exception e) {
            // Just ignore that
            Log.d("MessageHistoryScreen", "Unable to un bind");
        }
        //Reset Bulleting message count, if any
        if (isBulletinBroadcast)
            iChatPref.saveBulletinChatCounter(iChatPref.getUserDomain(), 0);
        try {
            if (mPlayer != null)
                mPlayer.stop();
        } catch (Exception e) {
        }
        super.onDestroy();

    }

    public void onBackClick(View view) {
        finish();
        onBackPressed();
    }

    public void onCallClicked() {
        if (!iChatPref.isGroupChat(userName)) {
            if (iChatPref.isDNC(userName) && !iChatPref.isDomainAdminORSubAdmin()) {
                Toast.makeText(this, getString(R.string.dnc_alert), Toast.LENGTH_SHORT).show();
                return;
            }
            if (mSinchServiceInterface != null) {
                try {
                    Call call = mSinchServiceInterface.callUserWithHeader(userName, HomeScreen.createHeaderForCalling(userName));
                    String callId = call.getCallId();

                    Intent callScreen = new Intent(this, CallScreenActivity.class);
                    callScreen.putExtra(SinchService.CALL_ID, callId);
                    startActivity(callScreen);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // //919910040523_p5domain
            // String number = null;
            // if(userName.indexOf('_') != -1)
            // number = "+"+userName.substring(0, userName.indexOf('_'));
            // else
            // number = "+"+userName;
            // final String uri = "tel:" + number;
            // new AlertDialog.Builder(this).setMessage("Do you want to call
            // "+number+"?")
            // .setPositiveButton(R.string.yes,
            // new DialogInterface.OnClickListener() {
            //
            // public void onClick(DialogInterface dialog,
            // int whichButton) {
            // //Start Native calle
            //// Intent call = new Intent(Intent.ACTION_CALL);
            //// call.setData(Uri.parse(uri));
            //// startActivity(call);
            //
            // //Start SIP call
            // Call call = getSinchServiceInterface().callUser(userName);
            // String callId = call.getCallId();
            //
            // Intent callScreen = new Intent(ChatListScreen.this,
            // CallScreenActivity.class);
            // callScreen.putExtra(SinchService.CALL_ID, callId);
            // startActivity(callScreen);
            //
            // }
            // }).setNegativeButton(R.string.cancel, null).show();

        }
    }

    public void showDialog(String s) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    private static final String TIME_PATTERN = "HH:mm";
    DateFormat dateFormat = null;
    SimpleDateFormat timeFormat = null;
    TextView lblStartDate = null;
    TextView lblStartTime = null;
    TextView lblEndDate = null;
    TextView lblEndTime = null;
    boolean startDateAndTimeClicked;
    Calendar pollStartCalender = Calendar.getInstance();
    Calendar pollEndCalender = Calendar.getInstance();


    public String getActivePollID() {
        HashMap<String, String> polls = getPollForGroup(openedGroupName);
        String poll_id = null;
        if (polls != null && !polls.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = polls.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pollEntry = iterator.next();
                poll_id = pollEntry.getKey();
                String poll_status = getPollStatus(poll_id);
                if (poll_status != null && poll_status.equals("active")) {
                    break;
                }
            }
        }
        return poll_id;
    }

    public void onPollClicked(String s) {
        HashMap<String, String> polls = getPollForGroup(openedGroupName);
        String poll_id = null;
        String poll_data = null;
        String poll_end_time = null;
        boolean is_poll_active = false;
        if (polls != null && !polls.isEmpty()) {
            Date d = new Date(new Date().getTime());
            Iterator<Map.Entry<String, String>> iterator = polls.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pollEntry = iterator.next();
                System.out.println(pollEntry.getKey() + " :: " + pollEntry.getValue());
                //You can remove elements while iterating.
                //            iterator.remove();
                poll_id = pollEntry.getKey();
                poll_data = pollEntry.getValue();
                String poll_status = getPollStatus(poll_id);
                if (poll_status != null && poll_status.equals("active")) {
                    is_poll_active = true;
                    break;
                }
            }
        }
        if (is_poll_active) {
            Toast.makeText(this, getString(R.string.one_poll_alert), Toast.LENGTH_SHORT).show();
            return;
        }
//        final Dialog poll = new Dialog(this);
        final Dialog poll = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        poll.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        poll.getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT);
        poll.setCanceledOnTouchOutside(false);
        poll.setContentView(R.layout.poll_layout);
        final EmojiconEditText poll_tx_box = ((EmojiconEditText) poll.findViewById(R.id.poll_text_message));
        final EmojiconEditText poll_title_box = ((EmojiconEditText) poll.findViewById(R.id.poll_title));
        //Set Dates
        pollStartCalender = Calendar.getInstance();
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        lblStartDate = (TextView) poll.findViewById(R.id.lbl_start_date);
        lblStartTime = (TextView) poll.findViewById(R.id.lbl_start_time);
        lblStartDate.setText(dateFormat.format(pollStartCalender.getTime()));
        lblStartTime.setText(timeFormat.format(pollStartCalender.getTime()));
        ((Button) poll.findViewById(R.id.btn_set_date1)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startDateAndTimeClicked = true;
                DatePickerDialog.newInstance(ChatListScreen.this, pollStartCalender.get(Calendar.YEAR), pollStartCalender.get(Calendar.MONTH), pollStartCalender.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
            }
        });
        ((Button) poll.findViewById(R.id.btn_set_time1)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startDateAndTimeClicked = true;
                TimePickerDialog.newInstance(ChatListScreen.this, pollStartCalender.get(Calendar.HOUR_OF_DAY), pollStartCalender.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
            }
        });

        lblEndDate = (TextView) poll.findViewById(R.id.lbl_end_date);
        lblEndTime = (TextView) poll.findViewById(R.id.lbl_end_time);
        lblEndDate.setText(dateFormat.format(pollEndCalender.getTime()));
        lblEndTime.setText(timeFormat.format(pollEndCalender.getTime()));
        ((Button) poll.findViewById(R.id.btn_set_date2)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startDateAndTimeClicked = false;
                DatePickerDialog.newInstance(ChatListScreen.this, pollEndCalender.get(Calendar.YEAR), pollEndCalender.get(Calendar.MONTH), pollEndCalender.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
            }
        });
        ((Button) poll.findViewById(R.id.btn_set_time2)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startDateAndTimeClicked = false;
                TimePickerDialog.newInstance(ChatListScreen.this, pollEndCalender.get(Calendar.HOUR_OF_DAY), pollEndCalender.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");

            }
        });

        final EditText option1 = ((EditText) poll.findViewById(R.id.option_one_box));
        final EditText option2 = ((EditText) poll.findViewById(R.id.option_two_box));
        final EditText option3 = ((EditText) poll.findViewById(R.id.option_three_box));
        final EditText option4 = ((EditText) poll.findViewById(R.id.option_four_box));
        final EditText option5 = ((EditText) poll.findViewById(R.id.option_five_box));
        ((Button) poll.findViewById(R.id.btn_cancel)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                poll.dismiss();
            }
        });

        ((Button) poll.findViewById(R.id.btn_submit)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Submit poll to users
                String poll_title_txt = null;
                String poll_txt = null;
                String option1_txt = null;
                String option2_txt = null;
                String option3_txt = null;
                String option4_txt = null;
                String option5_txt = null;
                poll_txt = poll_tx_box.getText().toString();
                poll_title_txt = poll_title_box.getText().toString();
                if (poll_txt.trim().length() < 3) {
                    Toast.makeText(ChatListScreen.this, "Please enter poll message!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (poll_title_txt.trim().length() < 3) {
                    Toast.makeText(ChatListScreen.this, "Please enter poll title!", Toast.LENGTH_SHORT).show();
                    return;
                }

                byte options = 0;
                option1_txt = option1.getText().toString().trim();
                option2_txt = option2.getText().toString().trim();
                option3_txt = option3.getText().toString().trim();
                option4_txt = option4.getText().toString().trim();
                option5_txt = option5.getText().toString().trim();

                if (option1_txt.length() > 0)
                    options++;
                if (option2_txt.length() > 0)
                    options++;
                if (option3_txt.length() > 0)
                    options++;
                if (option4_txt.length() > 0)
                    options++;
                if (option5_txt.length() > 0)
                    options++;

                if (options < 2) {
                    Toast.makeText(ChatListScreen.this, "Please enter at least two options for the poll!", Toast.LENGTH_SHORT).show();
                    return;
                }


                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");//

                //Poll Start date
                String startDate = df.format(pollStartCalender.getTime());
                String PollCreationDate = startDate;

                //Poll End date
                String endDate = df.format(pollEndCalender.getTime());
                String PollEndDate = endDate;


                Date d = new Date(new Date().getTime());
                SimpleDateFormat data_formatter = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                String current = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(d);

                long current_date = 0;
                long entered_date = 0;
                try {
                    current_date = data_formatter.parse(current).getTime();
                    entered_date = data_formatter.parse(endDate).getTime();
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if ((entered_date - current_date) < 0) {
                    Toast.makeText(ChatListScreen.this, "Please enter future data and time!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String PollID = "" + System.currentTimeMillis();//Create uniqueID for poll
                openedPollID = PollID;
                int PollMessageType = 1;
                String PollOwnerName = SharedPrefManager.getInstance().getDisplayName();
                String PollUserName = SharedPrefManager.getInstance().getUserName();
                String PollState = "active";
                String PollTitle = poll_title_txt;
                String PollType = "textPoll";
                String Pollmessage = poll_txt;
                //Poll options depending upon the number of options filled among 5

                JSONObject finalJSONbject = new JSONObject();

                JSONArray options_array = new JSONArray();
                JSONObject options_element = null;


                try {
                    finalJSONbject.put("PollCreationDate", PollCreationDate);
                    finalJSONbject.put("PollEndDate", PollEndDate);
                    finalJSONbject.put("PollID", PollID);
                    finalJSONbject.put("PollMessageType", PollMessageType);
                    finalJSONbject.put("PollOwnerName", PollOwnerName);
                    finalJSONbject.put("PollUserName", PollUserName);
                    finalJSONbject.put("PollState", PollState);
                    finalJSONbject.put("PollTitle", PollTitle);
                    finalJSONbject.put("PollType", PollType);
                    finalJSONbject.put("Pollmessage", Pollmessage);

                    if (option1_txt != null && option1_txt.trim().length() > 0) {
                        options_element = new JSONObject();
                        options_element.put("OptionId", "1");
                        options_element.put("OptionText", option1_txt.trim());
                        options_element.put("PollOptionCount", "0");
                        options_array.put(options_element);
                    }
                    if (option2_txt != null && option2_txt.trim().length() > 0) {
                        options_element = new JSONObject();
                        options_element.put("OptionId", "2");
                        options_element.put("OptionText", option2_txt.trim());
                        options_element.put("PollOptionCount", "0");
                        options_array.put(options_element);
                    }
                    if (option3_txt != null && option3_txt.trim().length() > 0) {
                        options_element = new JSONObject();
                        options_element.put("OptionId", "3");
                        options_element.put("OptionText", option3_txt.trim());
                        options_element.put("PollOptionCount", "0");
                        options_array.put(options_element);
                    }
                    if (option4_txt != null && option4_txt.trim().length() > 0) {
                        options_element = new JSONObject();
                        options_element.put("OptionId", "4");
                        options_element.put("OptionText", option4_txt.trim());
                        options_element.put("PollOptionCount", "0");
                        options_array.put(options_element);
                    }
                    if (option5_txt != null && option5_txt.trim().length() > 0) {
                        options_element = new JSONObject();
                        options_element.put("OptionId", "5");
                        options_element.put("OptionText", option5_txt.trim());
                        options_element.put("PollOptionCount", "0");
                        options_array.put(options_element);
                    }
                    finalJSONbject.put("PollOption", options_array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                System.out.println("Final JSON for Poll : " + finalJSONbject.toString());
                if (finalJSONbject != null) {
                    poll.dismiss();
                    sendPoll(finalJSONbject.toString(), XMPPMessageType.atMeXmppMessageTypePoll, 1);
                    savePollIDs(PollID, "active");
                    Toast.makeText(ChatListScreen.this, "Poll published successfully in group!", Toast.LENGTH_SHORT).show();
                    if (createPoll != null)
                        createPoll.setVisibility(View.VISIBLE);
                }
            }
        });
        poll.show();
    }

    public static String convertTime(String aTimeToConvert) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        serverFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date serverDate = null;
        try {
            serverDate = serverFormat.parse(aTimeToConvert);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat localFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        localFormat.setTimeZone(TimeZone.getDefault());
        if (serverDate != null) {
            return localFormat.format(serverDate);
        }
        return aTimeToConvert;
    }

    public void sendPollReply(String poll_json, String selected_option) {

        //Get latest poll data
        String poll_with_updated_data = getAndUpdatedPollresultsToSendToAllMembers(openedPollID);
        if (poll_with_updated_data != null && poll_with_updated_data.trim().length() > 10)
            poll_json = poll_with_updated_data;

        //Poll creator date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDate = df.format(c.getTime());
        String PollCreationDate = startDate;


        //Poll End Date - For 8 hours
        Date d = new Date(new Date().getTime() + 28800000);
        String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);

        String PollEndDate = endDate;

        //1 - New poll, 2 - Poll reply
        int PollMessageType = 2;
        String PollMemberName = SharedPrefManager.getInstance().getDisplayName();
        String PollMemberUserName = SharedPrefManager.getInstance().getUserName();
        String PollState = "active";
        String PollTitle = "";
        String PollType = "textPoll";
        String PollID = null;
        String Pollmessage = "";

        JSONObject jsonobj = null;
        JSONArray poll_options = null;
        String[] poll_id = null;
        String[] poll_value = null;
        int[] poll_option_count = null;
        try {
            jsonobj = new JSONObject(poll_json);
            if (jsonobj.has("PollID") && jsonobj.getString("PollID").toString().trim().length() > 0) {
                PollID = jsonobj.getString("PollID").toString();
            }
            if (jsonobj.has("PollTitle") && jsonobj.getString("PollTitle").toString().trim().length() > 0) {
                PollTitle = jsonobj.getString("PollTitle");
            }
            if (jsonobj.has("Pollmessage") && jsonobj.getString("Pollmessage").toString().trim().length() > 0) {
                Pollmessage = jsonobj.getString("Pollmessage");
            }
            if (jsonobj.has("PollOption"))
                poll_options = jsonobj.getJSONArray("PollOption");
            if (poll_options.length() > 0) {
                poll_id = new String[poll_options.length()];
                poll_value = new String[poll_options.length()];
                poll_option_count = new int[poll_options.length()];
                for (int i = 0; i < poll_options.length(); i++) {
                    JSONObject obj = (JSONObject) poll_options.get(i);
                    if (obj.has("OptionId")) {
                        poll_id[i] = obj.getString("OptionId");
                    }
                    if (obj.has("OptionText")) {
                        poll_value[i] = obj.getString("OptionText");
                    }
                    if (obj.has("PollOptionCount")) {
                        poll_option_count[i] = Integer.parseInt(obj.getString("PollOptionCount"));
                        if (obj.getString("OptionId").equals(selected_option))
                            poll_option_count[i] = poll_option_count[i] + 1;
                    }
                }
                //Update options
                poll_options = new JSONArray();
                for (int i = 0; i < poll_id.length; i++) {
                    JSONObject options_element = new JSONObject();
                    options_element.put("OptionId", poll_id[i]);
                    options_element.put("OptionText", poll_value[i]);
                    options_element.put("PollOptionCount", poll_option_count[i]);
                    poll_options.put(options_element);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Poll options depending upon the number of options filled among 5
        JSONObject finalJSONbject = null;


        JSONArray options_array = new JSONArray();
        JSONObject options_element = null;


        try {
            finalJSONbject = new JSONObject(poll_json);
//            finalJSONbject.put("PollCreationDate", PollCreationDate);
//            finalJSONbject.put("PollEndDate", PollEndDate);
//            finalJSONbject.put("PollID", PollID);
            finalJSONbject.put("PollMessageType", PollMessageType);
            finalJSONbject.put("PollOwnerName", PollMemberName);
            finalJSONbject.put("PollUserName", PollMemberUserName);
//            finalJSONbject.put("PollState", PollState);
//            finalJSONbject.put("PollTitle", PollTitle);
//            finalJSONbject.put("PollType", PollType);
//            finalJSONbject.put("Pollmessage", Pollmessage);
            finalJSONbject.remove("PollOption");
            finalJSONbject.put("PollOption", poll_options);

            //Add Selected Option
            if (selected_option != null) {
                options_element = new JSONObject();
                options_element.put("OptionId", selected_option);
                options_array.put(options_element);
            }
            finalJSONbject.put("PollReplyOption", options_array);

            if (finalJSONbject != null) {
                //Update Poll Options to show updated results.
                sendPoll(finalJSONbject.toString(), XMPPMessageType.atMeXmppMessageTypePoll, PollMessageType);
//                showPoll(finalJSONbject.toString(), true);
                SharedPrefManager.getInstance().setPollReplyStatus(PollID, true);
//                showPollView(finalJSONbject.toString());
                savePollIDs(PollID, "active");
                isPollResultPage = true;
                showUpdatedPollresults(PollID);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void stopVoicePlay() {
        myVoicePlayer.reset();
        myVoicePlayer.clear();
    }

    public void onBackPressed() {

        if (menuOpen) {
            hideMenu();
            return;
        }

        if (isPollResultPage) {
//            pollMainLayout.setVisibility(View.GONE);
            if (poll != null)
                poll.dismiss();
            isPollResultPage = false;
            return;
        }
        //Text message Retain
        if (typingEditText != null && typingEditText.getText().toString().trim().length() > 0) {
            HomeScreen.textDataRetain.put(userName, typingEditText.getText().toString());
        }
        if (chatAdapter != null)
            chatAdapter.stopPlaying();
        if (recordingAudio && mVoiceMedia != null)
            mVoiceMedia.stopRec();
        if (isPlaying) {
            stopVoicePlay();
        }
        if (isBulletinBroadcast)
            iChatPref.saveBulletinChatCounter(iChatPref.getUserDomain(), 0);
        if (chatAdapter != null && chatAdapter.isEditableChat()) {
            chatAdapter.removeSelectedItems();
            onClick(okEditTextView);
            return;

        } else {
            Intent intent = new Intent(this, HomeScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        if (!ChatService.xmppConectionStatus && messageService != null)
            messageService.chatLogin();
    }

    public void onProfileClick(View view) {
//    	if(true){
//	    	showShortProfile(userName);
//	    	return;
//    	}
        if (isBulletinBroadcast)
            return;
//    	System.out.println("iChatPref.isGroupChat(userName) - "+iChatPref.isGroupChat(userName));
//    	System.out.println("iChatPref.isGroupMemberActive(userName, iChatPref.getUserName() - "+iChatPref.isGroupMemberActive(userName, iChatPref.getUserName()));
//    	System.out.println("iChatPref.isGroupActive(userName) - "+iChatPref.isGroupActive(userName));
        if (iChatPref.isGroupChat(userName) && !iChatPref.isGroupActive(userName)) {
            showDialog("Group has been deactivated.");
            return;
        }
//        if(iChatPref.isGroupChat(userName) && 
//        		(!iChatPref.isGroupMemberActive(userName, iChatPref.getUserName()) ||  !iChatPref.isGroupActive(userName))){
//        	showDialog("Group has been deactivated.");
//        	return;
//        }
//        if(iChatPref.isGroupChat(userName) && !iChatPref.isGroupActive(userName) && iChatPref.isPublicGroup(userName)){
//		 showDialog("Group has been deactivated.");
//       	 return;
//       }
        Intent intent = null;
        if (isSharedIDMessage) {
            if (iChatPref.isDomainAdminORSubAdmin() || isSharedIDAdmin) {
                intent = new Intent(this, CreateSharedIDActivity.class);
                intent.putExtra("EDIT_MODE", true);
                intent.putExtra(Constants.GROUP_UUID, userName);
                intent.putExtra(Constants.GROUP_NAME, iChatPref.getSharedIDDisplayName(userName));
                intent.putExtra(Constants.GROUP_FILE_ID, iChatPref.getSharedIDFileId(userName));
                startActivity(intent);
            }
            return;
        }
        if (iChatPref.isGroupChat(userName) || iChatPref.isBroadCast(userName)) {
            //check if user has joined the channel, Here username has groupname value
            boolean joined = SharedPrefManager.getInstance().getUserGroupInfo(userName, iChatPref.getUserName(), SharedPrefManager.GROUP_ACTIVE_INFO);
            if (joined || !iChatPref.isPublicGroup(userName)) {
                intent = new Intent(this, GroupProfileScreen.class);
                intent.putStringArrayListExtra(Constants.GROUP_USERS, usersList);
                intent.putExtra(Constants.USER_MAP, nameMap);
                if (iChatPref.isBroadCast(userName))
                    intent.putExtra(Constants.BROADCAST, true);
                else if (iChatPref.isPublicGroup(userName))
                    intent.putExtra(Constants.OPEN_CHANNEL, true);
                intent.putExtra(Constants.CHAT_USER_NAME, userName);
                intent.putExtra(Constants.CHAT_NAME, windowNameView.getText());
                startActivityForResult(intent, 300);
            } else {
                boolean owner = iChatPref.isOwner(userName, iChatPref.getUserName());
                String count = iChatPref.getGroupMemberCount(userName);
                String group_owner = iChatPref.getGroupOwnerName(userName);
                String disp_name = null;
                try {
                    disp_name = DBWrapper.getInstance(getApplicationContext()).getChatName(group_owner);
                } catch (Exception ex) {
                    disp_name = DBWrapper.getInstance(getApplicationContext()).getContactName(group_owner);
                }
                if (disp_name != null && disp_name.equals(group_owner))
                    disp_name = iChatPref.getUserServerName(group_owner);
                if (disp_name != null && disp_name.contains("#"))
                    disp_name = disp_name.substring(0, disp_name.indexOf('#'));

                intent = new Intent(this, PublicGroupInfoScreen.class);
                intent.putExtra(PublicGroupInfoScreen.CHANNEL_TITLE, windowNameView.getText());
                intent.putExtra(PublicGroupInfoScreen.MEMBERS_COUNT_ID, count);
                intent.putExtra(PublicGroupInfoScreen.CHANNEL_OWNER, disp_name);
                intent.putExtra(PublicGroupInfoScreen.CHANNEL_DESCRIPTION, iChatPref.getUserStatusMessage(userName));
                if (owner)
                    intent.putExtra(PublicGroupInfoScreen.CHANNEL_MEMBER_TYPE, "OWNER");
                else
                    intent.putExtra(PublicGroupInfoScreen.CHANNEL_MEMBER_TYPE, "USER");
                intent.putExtra(PublicGroupInfoScreen.CHANNEL_NAME, userName);
                intent.putExtra(PublicGroupInfoScreen.CHANNEL_PIC_ID, iChatPref.getUserFileId(userName));
                startActivity(intent);
            }
        } else {
            intent = new Intent(this, UsersProfileScreen.class);
            intent.putExtra(Constants.CHAT_USER_NAME, userName);
            intent.putExtra(Constants.CHAT_NAME, windowNameView.getText());
            startActivity(intent);
        }
    }

    public void onAttachMediaClick(View view) {
        if (attachOptionsDialog != null && !attachOptionsDialog.isShowing()) {
            attachOptionsDialog.show();
        }

    }

    private ArrayList<String> convertNames(ArrayList<String> arrayList) {
        nameMap.clear();
        ArrayList<String> displayList = new ArrayList<String>();
        for (String tmp : arrayList) {
            String value = DBWrapper.getInstance().getChatName(tmp);
            if (iChatPref.getUserName().equals(tmp))
                value = "You";
            if (value != null && value.contains("#786#"))
                value = value.substring(0, value.indexOf("#786#"));
            if (value != null && value.equals(tmp))
                value = value.replace("m", "+");
            displayList.add(value);
            nameMap.put(tmp, value);
        }
        Collections.sort(displayList, String.CASE_INSENSITIVE_ORDER);
        return displayList;
    }

    // @Override
    // public void chatClientConnected(ChatService service) {
    //
    //
    // messageService = service;
    // if(messageService!=null){
    // messageService.setChatVisibilty(true);
    // messageService.setChatPerson(currentUser);
    // }
    // Log.d("Service", "Connected");
    // // xmppConnection = messageService.getconnection();
    // // messageService.setChatListener(ChatListScreen.this);
    // // messageService.setTypingListener(ChatListScreen.this);
    // sendSeenStatus();
    // if (messageService != null)
    // messageService.clearAllNotifications();
    // if (messageService != null && invitationEnable && !isFromGroupCreation) {
    // invitationEnable = false;
    // try {
    // ArrayList<String> inviters =
    // getIntent().getStringArrayListExtra(Constants.INVITATION_LIST);
    // String displayName = iChatPref.getGroupDisplayName(userName);
    // String description = iChatPref.getUsersOfGroup(userName);
    // String infoList = "";
    // for (String inviter : inviters) {
    // if (inviter != null && !inviter.equals("")){
    //
    // messageService.inviteUserInRoom(userName,
    // displayName, description, inviter);
    // infoList += inviter+",";
    // }
    // }
    // if(!infoList.equals("")){
    // if(usersList!=null && !usersList.isEmpty()){
    // for(String element:usersList)
    // infoList += element+",";
    // }
    // if(infoList.endsWith(","))
    // infoList = infoList.substring(0, infoList.length()-1);
    // boolean isSuccessed =
    // messageService.sendInfoMessage(userName,infoList,Message.XMPPMessageType.atMeXmppMessageTypeMemberList);
    // if(isSuccessed){
    // new GroupCreateTaskOnServer(userName, chatWindowName,
    // usersList).execute(GroupCreateTaskOnServer.UPDATE_GROUP_REQUEST);
    // }
    // }
    // getIntent().getExtras().putString(Constants.INVITATION_LIST, "");
    // } catch (Exception e) {
    // Log.d(TAG, "users are not able to joined.");
    // }
    // }
    //
    //
    // }
    //
    // @Override
    // public void chatClientDisconnected() {
    // if(messageService!=null){
    // messageService.setChatVisibilty(false);
    // messageService.setChatPerson(currentUser);
    // }
    // messageService = null;
    // }
    @Override
    public void notifyRecordStatusRecieve(final String user) {
        if (iChatPref.isGroupChat(userName) && !iChatPref.isGroupMemberActive(userName, iChatPref.getUserName())) {
            return;
        }
        if (onForeground) {
            // recordingStatusNotifierHandler.sendEmptyMessage(0);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (SharedPrefManager.getInstance().getUserRecordingStatus(userName)
                            || !SharedPrefManager.getInstance().getUserRecordingStatusForGroup(userName).equals("")) {
                        if (user.equalsIgnoreCase(userName)) {
                            if (!userName.equals(iChatPref.getUserName())) {
                                typingText.setVisibility(TextView.VISIBLE);
                                typingText.setText("Recording...");
                            }
                        } else {
                            // Show xyz is typing -
                            // mahesh_test_group_readmark@919878427137_readmark
                            String temp = user;
                            if (user.indexOf('@') != -1)
                                temp = user.substring(user.indexOf('@') + 1);
                            String name = DBWrapper.getInstance().getChatName(temp);
                            if (name.equalsIgnoreCase(temp))
                                name = "New user";
                            if (name != null && name.indexOf('#') != -1)
                                name = name.substring(0, name.indexOf('#'));
                            if (temp.equals(SharedPrefManager.getInstance().getUserName())) {
                                typingText.setVisibility(TextView.GONE);
                                typingText.setText("");

                            } else if (name != null && name.length() > 0) {
                                typingText.setVisibility(TextView.VISIBLE);
                                typingText.setText(name + " is recording...");
                            }
                        }
                    } else {
                        typingText.setVisibility(TextView.GONE);
                        typingText.setText("");
                    }

                }
            });
        }
    }

    @Override
    public void notifyListeningStatusRecieve(final String user) {
        if (iChatPref.isGroupChat(userName) && !iChatPref.isGroupMemberActive(userName, iChatPref.getUserName())) {
            return;
        }
        if (onForeground) {
            // listeningStatusNotifierHandler.sendEmptyMessage(0);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (SharedPrefManager.getInstance().getUserListeningStatus(userName)
                            || !SharedPrefManager.getInstance().getUserListeningStatusForGroup(userName).equals("")) {
                        if (user.equalsIgnoreCase(userName)) {
                            if (!userName.equals(iChatPref.getUserName())) {
                                typingText.setVisibility(TextView.VISIBLE);
                                typingText.setText("Listening...");
                            }
                        } else {
                            // Show xyz is typing -
                            // mahesh_test_group_readmark@919878427137_readmark
                            String temp = user;
                            if (user.indexOf('@') != -1)
                                temp = user.substring(user.indexOf('@') + 1);
                            String name = DBWrapper.getInstance().getChatName(temp);
                            if (name.equalsIgnoreCase(temp))
                                name = "New user";
                            if (name != null && name.indexOf('#') != -1)
                                name = name.substring(0, name.indexOf('#'));
                            if (temp.equals(SharedPrefManager.getInstance().getUserName())) {
                                typingText.setVisibility(TextView.GONE);
                                typingText.setText("");

                            } else if (name != null && name.length() > 0) {
                                typingText.setVisibility(TextView.VISIBLE);
                                typingText.setText(name + " is listening...");
                            }
                        }
                    } else {
                        typingText.setVisibility(TextView.GONE);
                        typingText.setText("");
                    }
                }
            });
        }
    }

    @Override
    public void notifyTypingRecieve(final String user) {
        if (iChatPref.isGroupChat(userName) && !iChatPref.isGroupMemberActive(userName, iChatPref.getUserName())) {
            return;
        }
        if (onForeground) {
            // typingNotifierHandler.sendEmptyMessage(0);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (SharedPrefManager.getInstance().getUserTypingStatus(userName)
                            || !SharedPrefManager.getInstance().getUserTypingStatusForGroup(userName).equals("")) {
                        if (user.equalsIgnoreCase(userName)) {
                            if (!userName.equals(iChatPref.getUserName())) {
                                typingText.setVisibility(TextView.VISIBLE);
                                typingText.setText("Typing...");
                            }
                        } else {
                            // Show xyz is typing -
                            // mahesh_test_group_readmark@919878427137_readmark
                            String temp = user;
                            if (user.indexOf('@') != -1)
                                temp = user.substring(user.indexOf('@') + 1);
                            String name = DBWrapper.getInstance().getChatName(temp);

                            if (name.equalsIgnoreCase(temp))
                                name = "New user";
                            if (name != null && name.indexOf('#') != -1)
                                name = name.substring(0, name.indexOf('#'));
                            if (temp.equals(SharedPrefManager.getInstance().getUserName())) {
                                typingText.setVisibility(TextView.GONE);
                                typingText.setText("");

                            } else if (name != null && name.length() > 0) {
                                typingText.setVisibility(TextView.VISIBLE);
                                typingText.setText(name + " is typing...");
                            }
                        }
                    } else {
                        typingText.setVisibility(TextView.GONE);
                        typingText.setText("");
                    }

                }
            });
        }
    }

//    private final Handler typingNotifierHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            Log.i(TAG, "typingNotifierHandler: userName : " + userName);
//            if (SharedPrefManager.getInstance().getUserTypingStatus(userName))
//                typingText.setText("Typing...");
//            else
//                typingText.setText("");
//
//        }
//    };
//    private final Handler recordingStatusNotifierHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            if (SharedPrefManager.getInstance().getUserRecordingStatus(userName))
//                typingText.setText("Recording...");
//            else
//                typingText.setText("");
//
//        }
//    };
//    private final Handler listeningStatusNotifierHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            if (SharedPrefManager.getInstance().getUserListeningStatus(userName))
//                typingText.setText("Listening...");
//            else
//                typingText.setText("");
//
//        }
//    };

    @Override
    public void refreshOnlineGroupUser() {
        if (onForeground) {
            refreshOnlineGroupUserHandler.sendEmptyMessage(0);
            // runOnUiThread(new Runnable() {
            //
            // @Override
            // public void run() {
            // usersList.clear();
            // usersList = iChatPref.getGroupUsersList(userName);
            // Collections.sort(usersList);
            // usersDisplayList = convertNames(usersList);
            // Log.d(TAG,usersList+" group persons displayed of group "+
            // userName);
            // updateGroupUsersList(usersDisplayList);
            // }
            // });
        }

    }

    private final Handler refreshOnlineGroupUserHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (isFromOnResume) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                isFromOnResume = false;
            }
            usersList.clear();
            if (iChatPref.isBroadCast(userName))
                usersList = iChatPref.getBroadCastUsersList(userName);
            else
                usersList = iChatPref.getGroupUsersList(userName);
            Collections.sort(usersList);
//            usersDisplayList = convertNames(usersList);
//            Log.d(TAG, usersList + " group persons displayed of group " + userName);
//            updateGroupUsersList(usersDisplayList);

        }
    };

    @Override
    public void refreshSubjectOfGroup() {
        if (onForeground) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    if (iChatPref != null && iChatPref.isGroupChat(userName)) {
                        String displayName = iChatPref.getGroupDisplayName(userName);
                        windowNameView.setText(displayName);
                        getServerGroupProfile(userName);
                    }
                    if (iChatPref != null && iChatPref.isBroadCast(userName)) {
                        String displayName = iChatPref.getBroadCastDisplayName(userName);
                        windowNameView.setText(displayName);
                    }
                }
            });
        }

    }

    public void sendVoiceMessage(final String voicePath) {
        String packetID = null;
        if (voicePath != null && voicePath.length() > 0) {
            try {
                String thumbData = null;
                if (messageService != null) {
                    chatAdapter.setChatService(messageService);
                    // thumbData =
                    // MyBase64.encode(getByteArrayOfThumbnail(voicePath));
                    packetID = messageService.sendMediaMessage(userName, "", captionText, voicePath, thumbData,
                            XMPPMessageType.atMeXmppMessageTypeAudio);
                    ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaLocalPath(packetID,
                            voicePath);
                    refreshAdpter();// chatAdapter.notifyDataSetChanged();
                    chatAdapter.notifyDataSetChanged();
                    captionText = "";
//                  chatList.setSelection(chatAdapter.getCount() - 1);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatList.setSelection(chatAdapter.getCount() - 1);
                                }
                            });
                        }
                    }, 1000);
                    // new MediaFileUpload().execute(voicePath,
                    // packetID,thumbData,XMPPMessageType.atMeXmppMessageTypeAudio.name());
                }
            } catch (Exception ex) {
                System.out.println("3" + ex.toString());
            }
        }
    }

    public void sendFile(final String pdfPath, XMPPMessageType file_type) {
        String packetID = null;
        if (pdfPath != null && pdfPath.length() > 0) {
            try {
                String thumbData = null;
                if (messageService != null) {
                    chatAdapter.setChatService(messageService);
                    // thumbData =
                    // MyBase64.encode(getByteArrayOfThumbnail(voicePath));
                    packetID = messageService.sendMediaMessage(userName, "", null, pdfPath, thumbData, file_type);
                    ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaLocalPath(packetID,
                            pdfPath);
                    refreshAdpter();// 
                    chatAdapter.notifyDataSetChanged();
//                    chatList.setSelection(chatAdapter.getCount() - 1);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatList.setSelection(chatAdapter.getCount() - 1);
                                }
                            });
                        }
                    }, 1000);
                    // new MediaFileUpload().execute(voicePath,
                    // packetID,thumbData,XMPPMessageType.atMeXmppMessageTypeAudio.name());
                }
            } catch (Exception ex) {
                System.out.println("4" + ex.toString());
            }
        }
    }

    public void sendLocation(String location, String caption, XMPPMessageType file_type) {
        String packetID = null;
        try {
            String thumbData = null;
            if (messageService != null) {
                chatAdapter.setChatService(messageService);
                // if (iChatPref.isBroadCast(userName)){
                // for(String person: usersList)
                // messageService.sendContactAndLocation(person, caption,
                // location, null , file_type, true);
                // }
                messageService.sendContactAndLocation(userName, caption, location, null, file_type, false);
                refreshAdpter();
                chatAdapter.notifyDataSetChanged();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatList.setSelection(chatAdapter.getCount() - 1);
                            }
                        });
                    }
                }, 1000);

            }
        } catch (Exception ex) {
            System.out.println("" + ex.toString());
        }
    }

    public void sendContact(String contact, XMPPMessageType file_type) {
        String packetID = null;
        try {
//            contact = contact.replace("\"", "&quot;");
            if (messageService != null) {
                chatAdapter.setChatService(messageService);
                // if (iChatPref.isBroadCast(userName)){
                // for(String person: usersList)
                // messageService.sendContactAndLocation(person, "", contact,
                // null , file_type, true);
                // }
                messageService.sendContactAndLocation(userName, "", contact, null, file_type, false);
                refreshAdpter();
                chatAdapter.notifyDataSetChanged();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatList.setSelection(chatAdapter.getCount() - 1);
                            }
                        });
                    }
                }, 1000);
            }
        } catch (Exception ex) {
            System.out.println("" + ex.toString());
        }
    }

    public void sendPoll(String poll_data, XMPPMessageType file_type, int poll_type) {
        String packetID = null;
        try {
            //Save value in Shared pref
            //Group_name, Poll_ID, Poll_JSON_DATA
            savePoll(openedGroupName, openedPollID, poll_data);
//            poll_data = poll_data.replace("\"", "&quot;");
            if (messageService != null) {
                chatAdapter.setChatService(messageService);
                // if (iChatPref.isBroadCast(userName)){
                // for(String person: usersList)
                // messageService.sendContactAndLocation(person, "", contact,
                // null , file_type, true);
                // }
                messageService.sendPoll(userName, "", poll_data, file_type, poll_type);

                if (poll_type == 1) {
                    refreshAdpter();
                    chatAdapter.notifyDataSetChanged();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatList.setSelection(chatAdapter.getCount() - 1);
                                }
                            });
                        }
                    }, 1000);
                }

            }
        } catch (Exception ex) {
            System.out.println("" + ex.toString());
        }
    }

    public void sendVideoMessage(final String videoPath) {
        String packetID = null;
        if (videoPath != null && videoPath.length() > 0) {
            try {
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
                if (messageService != null) {
                    // bitmap = ThumbnailUtils.extractThumbnail(bitmap, 64, 64);

                    chatAdapter.setChatService(messageService);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    packetID = messageService.sendMediaMessage(userName, "", captionText, videoPath, encoded,
                            XMPPMessageType.atMeXmppMessageTypeVideo);

                    ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaLocalPath(packetID,
                            videoPath);
                    refreshAdpter();// chatAdapter.notifyDataSetChanged();
                    chatAdapter.notifyDataSetChanged();
                    chatList.setSelection(chatAdapter.getCount() - 1);
                    // new MediaFileUpload().execute(videoPath, packetID,
                    // encoded,
                    // XMPPMessageType.atMeXmppMessageTypeVideo.name());
                }
            } catch (Exception ex) {
                System.out.println("5" + ex.toString());
            }
        }
    }

    public void sendPictureMessage(final String imgPath) {
        String packetID = null;
        if (imgPath != null && imgPath.length() > 0) {
            try {
                String thumbImg = null;
                if (messageService != null) {
                    chatAdapter.setChatService(messageService);
                    thumbImg = MyBase64.encode(getByteArrayOfThumbnail(imgPath));
                    packetID = messageService.sendMediaMessage(userName, "", captionText, imgPath, thumbImg,
                            XMPPMessageType.atMeXmppMessageTypeImage);
                    ChatDBWrapper.getInstance(SuperChatApplication.context).updateMessageMediaLocalPath(packetID,
                            imgPath);
                    refreshAdpter();//
                    chatAdapter.notifyDataSetChanged();
                    chatList.setSelection(chatAdapter.getCount() - 1);
                    // new MediaFileUpload().execute(imgPath,
                    // packetID,thumbImg,XMPPMessageType.atMeXmppMessageTypeImage.name());
                }
            } catch (Exception ex) {
                System.out.println("6" + ex.toString());
            }
        }
    }

    public void refreshAdpter() {
        // TODO Auto-generated method stub
        if (onForeground) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Cursor cursor1 = null;
                    if (isBulletinBroadcast)
                        cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_BULLETIN);
                    else
                        cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_NORMAL);
                    // chatAdapter.swapCursor(cursor1);
                    // chatAdapter.notifyDataSetChanged();
                    chatAdapter.changeCursor(cursor1);
                }
            });
        }
    }

    @Override
    public void notifyChatHome(String sender, String message) {

    }

    @Override
    public void notifyChatRecieve(String sender, String message) {
        // TODO Auto-generated method stub
        if (onForeground) {
            android.os.Message androidMsg = new android.os.Message();
            Bundle msgBundle = new Bundle();
            msgBundle.putString("MSG_SENDER", sender);
            msgBundle.putString("MSG_DATA", message);
            androidMsg.setData(msgBundle);
            notifyChatRecieveHandler.sendMessage(androidMsg);
//            notifyChatRecieveHandler.sendEmptyMessage(0);

            // runOnUiThread(new Runnable() {
            //
            // @Override
            // public void run() {
            // if(ChatService.xmppConectionStatus){
            // xmppStatusView.setImageResource(R.drawable.blue_dot);
            // }else{
            // xmppStatusView.setImageResource(R.drawable.red_dot);
            // }
            // Cursor cursor1 = ChatDBWrapper.getInstance().getUserChatList(
            // userName);
            // chatAdapter.swapCursor(cursor1);
            // chatList.setAdapter(chatAdapter);
            //
            // chatAdapter.notifyDataSetChanged();
            // chatList.setSelection(chatAdapter.getCount() - 1);
            // chatList.setVerticalScrollBarEnabled(false);
            // // updateGroupUsersList();
            // }
            // });
            sendSeenStatus();
        }
    }

    public void notifyChatRecieve1(String sender, String message) {
        // TODO Auto-generated method stub
        if (onForeground) {
            notifyChatRecieveHandler1.sendEmptyMessage(0);
            // runOnUiThread(new Runnable() {
            //
            // @Override
            // public void run() {
            // if(ChatService.xmppConectionStatus){
            // xmppStatusView.setImageResource(R.drawable.blue_dot);
            // }else{
            // xmppStatusView.setImageResource(R.drawable.red_dot);
            // }
            // Cursor cursor1 = ChatDBWrapper.getInstance().getUserChatList(
            // userName);
            // chatAdapter.swapCursor(cursor1);
            // chatList.setAdapter(chatAdapter);
            //
            // chatAdapter.notifyDataSetChanged();
            // chatList.setSelection(chatAdapter.getCount() - 1);
            // chatList.setVerticalScrollBarEnabled(false);
            // // updateGroupUsersList();
            // }
            // });
            sendSeenStatus();
        }
    }

    private final Handler notifyChatRecieveHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            boolean isScrollAllowed = true;
            if (msg != null) {
                Bundle msgBundle = msg.getData();
                if (msgBundle != null) {
                    String msgSender = msgBundle.getString("MSG_SENDER");
                    String msgData = msgBundle.getString("MSG_DATA");
                    if (msgSender != null && !msgSender.equals(iChatPref.getUserName())) {
                        isScrollAllowed = false;
//        				if(msgData!=null && ((msgSender.equals(userName)&&iChatPref.isGroupChat(userName))||iChatPref.isGroupChat(userName)))
                        if (msgData != null && currentUser.equals(msgSender))
                            try {
                                mPlayer.start();
                            } catch (Exception e) {
                            }
                    }
                    if (msgSender == null)
                        isScrollAllowed = false;

                }
            }
            if (!chatList.canScrollVertically(ListView.SCROLL_AXIS_VERTICAL))
                isScrollAllowed = true;

//            if (ChatService.xmppConectionStatus) {
//            	networkConnection.setVisibility(View.GONE);
//                xmppStatusView.setImageResource(R.drawable.blue_dot);
//            } else {
//            	networkConnection.setVisibility(View.VISIBLE);
//                xmppStatusView.setImageResource(R.drawable.red_dot);
//            }
            Cursor cursor1 = null;
            if (isBulletinBroadcast)
                cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_BULLETIN);
            else
                cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_NORMAL);
            chatAdapter.swapCursor(cursor1);
//            if(isScrollAllowed)
//            	chatList.setAdapter(chatAdapter);

            chatAdapter.notifyDataSetChanged();
            if (isScrollAllowed) {
                // if(prevIndex>0)
                // chatList.setSelection(prevIndex);
                // else
                chatList.setSelection(chatAdapter.getCount() - 1);
            }
            prevIndex = 0;
            chatList.setVerticalScrollBarEnabled(false);
            // updateGroupUsersList();
            if (!iChatPref.isMyExistence()) {
                showExitDialog("You have been removed.");
            }
        }
    };

    public void showExitDialog(String s) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setText("Exit");
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                iChatPref.clearSharedPref();
                ChatDBWrapper.getInstance().clearMessageDB();
                DBWrapper.getInstance().clearAllDB();
                Intent intent = new Intent(ChatListScreen.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return false;
            }
        });
        bteldialog.show();
    }

    private final Handler notifyChatRecieveHandler1 = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (ChatService.xmppConectionStatus) {
                networkConnection.setVisibility(View.GONE);
                xmppStatusView.setImageResource(R.drawable.blue_dot);
            } else {
                networkConnection.setVisibility(View.VISIBLE);
                xmppStatusView.setImageResource(R.drawable.red_dot);
            }
            if (iChatPref.isBlocked(userName) || isBulletinBroadcast || isSharedIDMessage) {
                callOption.setVisibility(View.GONE);
            } else if (!iChatPref.isGroupChat(userName) && !iChatPref.isBroadCast(userName))
                callOption.setVisibility(View.VISIBLE);
            Cursor cursor1 = null;
            if (isBulletinBroadcast)
                cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_BULLETIN);
            else
                cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_NORMAL);
            chatAdapter.swapCursor(cursor1);
            // chatList.setAdapter(chatAdapter);
            if (iChatPref.isGroupChat(userName))
                typingText.setVisibility(View.GONE);
            else
                typingText.setVisibility(TextView.GONE);
            typingText.setText(iChatPref.getUserStatusMessage(userName));
            chatAdapter.notifyDataSetChanged();
            // if(prevIndex>0)
            // chatList.setSelection(prevIndex);
            // else
            // chatList.setSelection(chatAdapter.getCount() - 1);
            prevIndex = 0;
            chatList.setVerticalScrollBarEnabled(false);
            // updateGroupUsersList();

        }
    };

    private void sendSeenStatus() {
        // currentUser = "";
        new Thread() {
            @Override
            public void run() {
                if (userName != null && !userName.equals("") && !iChatPref.isBroadCast(userName)) {
                    ArrayList<String> list = ChatDBWrapper.getInstance().getRecievedMessages(userName);
                    if (list != null && !list.isEmpty() && messageService != null) {
                        if (iChatPref.isGroupChat(userName)) {
                            for (String foreignId : list) {
                                if (foreignId != null) {
                                    String senderGroupPersonUserName = ChatDBWrapper.getInstance()
                                            .getGroupMessageSenderName(foreignId);
                                    if (senderGroupPersonUserName != null && !senderGroupPersonUserName.equals("")) {
                                        ArrayList<String> tmpList = new ArrayList<String>();
                                        tmpList.add(foreignId);
                                        messageService.sendGroupOrBroadcastAck(senderGroupPersonUserName, tmpList, Message.SeenState.seen, userName);
                                    }
                                }
                            }
                        } else
                            messageService.sendAck(userName, list, Message.SeenState.seen);

                    } else
                        Log.d(TAG, "sendSeenStatus items not found in the DB.");
                }
            }
        }.start();

    }

    @Override
    public void voiceRecordingStarted(String recordingPath) {
        // TODO Auto-generated method stub

    }

    @Override
    public void voiceRecordingCompleted(String recordedVoicePath) {
        if (!isInvalidAudio) {
            mVoicePath = recordedVoicePath;
            sendVoiceMessage(mVoicePath);
        } else {
            // Toast.makeText(ChatListScreen.this,
            // getString(R.string.invalid_audio_message),
            // Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void voicePlayStarted() {
        // TODO Auto-generated method stub

    }

    @Override
    public void voicePlayCompleted(View view) {
        // TODO Auto-generated method stub
        if (wheelTimer != null) {
            wheelTimer.cancel();
            stopPlayingHandler.sendEmptyMessage(0);
        }
        final byte min = (byte) (totalAudioLength / 60);
        final byte sec = (byte) (totalAudioLength % 60);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                globalSeekBarValue = 0;
                globalSeekBarMaxValue = 0;
                if (playSenderSeekBar != null)
                    playSenderSeekBar.setProgress(0);
                if (playSenderMaxTimeText != null) {
                    if (min < 9)
                        playSenderMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                    else
                        playSenderMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                }
                if (playSenderView != null)
                    playSenderView.setBackgroundResource(R.drawable.audio_play);
                isPlaying = false;
                if (handler != null)
                    handler.sendEmptyMessage(0);
            }
        });
    }

    @Override
    public void onError(int i) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDureationchanged(long total, final long current, final SeekBar currentSeekBar) {
        // TODO Auto-generated method stub
        globalSeekBarMaxValue = (int) total;
        if (isPlaying)
            globalSeekBarValue = (int) current;
        currentAudioPlayCounter = current;
        int tot = totalAudioLength * 1000;
        long ttt = tot - currentAudioPlayCounter;
        if ((currentAudioPlayCounter + 100) >= tot)
            ttt = tot;
        ttt = ttt / 1000;
        final byte min = (byte) (ttt / 60);
        final byte sec = (byte) (ttt % 60);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                currentSeekBar.setProgress((int) current);
                if (min < 9)
                    playSenderMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                else
                    playSenderMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
            }
        });
        handler.sendEmptyMessage(0);
    }

    // private class GroupCreateTaskOnServer extends AsyncTask<String, String,
    // String> {
    //
    // @Override
    // protected void onPreExecute() {
    // super.onPreExecute();
    // }
    // @Override
    // protected String doInBackground(String... urls) {
    // final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
    // GroupChatServerModel model = new GroupChatServerModel();
    // if(urls[0].equals(CREATE_GROUP_REQUEST)){
    // model.setUserName(iPrefManager.getUserName());
    // model.setGroupName(userName);
    // model.setDisplayName(chatWindowName);
    // model.setMemberUserSet(usersList);
    // ArrayList<String> adminUserSet = new ArrayList<String>();
    // adminUserSet.add(SharedPrefManager.getInstance().getUserName());
    // model.setAdminUserSet(adminUserSet);
    // String JSONstring = new Gson().toJson(model);
    //
    // DefaultHttpClient client1 = new DefaultHttpClient();
    //
    // Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
    //
    // HttpPost httpPost = new HttpPost(Constants.SERVER_URL+
    // "/jakarta/rest/group/create");
    // // httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
    // HttpResponse response = null;
    // try {
    // httpPost.setEntity(new StringEntity(JSONstring));
    // try {
    // response = client1.execute(httpPost);
    // final int statusCode=response.getStatusLine().getStatusCode();
    // if (statusCode != HttpStatus.SC_OK){
    // HttpEntity entity = response.getEntity();
    // // System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
    // BufferedReader rd = new BufferedReader(new
    // InputStreamReader(entity.getContent()));
    // String line = "";
    // while ((line = rd.readLine()) != null) {
    // Log.d(TAG, "serverUpdateCreateGroupInfo response: "+line);
    // }
    // }
    // } catch (ClientProtocolException e) {
    // Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution
    // ClientProtocolException:"+e.toString());
    // } catch (IOException e) {
    // Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution
    // ClientProtocolException:"+e.toString());
    // }
    //
    // } catch (UnsupportedEncodingException e1) {
    // Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution
    // UnsupportedEncodingException:"+e1.toString());
    // }catch(Exception e){
    // Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution
    // Exception:"+e.toString());
    // }
    // }else if(urls[0].equals(UPDATE_GROUP_REQUEST)){
    //
    // model.setUserName(iPrefManager.getUserName());
    // model.setGroupName(userName);
    // model.setDisplayName(chatWindowName);
    // model.setMemberUserSet(usersList);
    // List<String> adminUserSet = new ArrayList<String>();
    // model.setAdminUserSet(adminUserSet);
    // String JSONstring = new Gson().toJson(model);
    //
    // DefaultHttpClient client1 = new DefaultHttpClient();
    //
    // Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
    //
    // HttpPost httpPost = new HttpPost(Constants.SERVER_URL+
    // "/jakarta/rest/group/update");
    // // httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
    // HttpResponse response = null;
    // try {
    // httpPost.setEntity(new StringEntity(JSONstring));
    // try {
    // response = client1.execute(httpPost);
    // final int statusCode=response.getStatusLine().getStatusCode();
    // if (statusCode != HttpStatus.SC_OK){
    // HttpEntity entity = response.getEntity();
    // // System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
    // BufferedReader rd = new BufferedReader(new
    // InputStreamReader(entity.getContent()));
    // String line = "";
    // while ((line = rd.readLine()) != null) {
    // Log.d(TAG, "serverUpdateCreateGroupInfo response: "+line);
    // }
    // }
    // } catch (ClientProtocolException e) {
    // Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution
    // ClientProtocolException:"+e.toString());
    // } catch (IOException e) {
    // Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution
    // ClientProtocolException:"+e.toString());
    // }
    //
    // } catch (UnsupportedEncodingException e1) {
    // Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution
    // UnsupportedEncodingException:"+e1.toString());
    // }catch(Exception e){
    // Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution
    // Exception:"+e.toString());
    // }
    //
    // }
    //
    //
    // return null;
    // }
    //
    // @Override
    // protected void onPostExecute(String response) {
    //
    // super.onPostExecute(response);
    // }
    // }
    private byte[] getByteArrayOfThumbnail(String imagePath) {
        // final int THUMBNAIL_HEIGHT = 48;
        // final int THUMBNAIL_WIDTH = 66;
        final int THUMBSIZE = 64;
        Bitmap imageBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), THUMBSIZE, THUMBSIZE);

        // Bitmap imageBitmap = BitmapFactory.decodeByteArray(mImageData, 0,
        // mImageData.length);
        Float width = new Float(imageBitmap.getWidth());
        Float height = new Float(imageBitmap.getHeight());
        Float ratio = width / height;
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (THUMBSIZE * ratio), THUMBSIZE, false);

        // int padding = (THUMBSIZE - imageBitmap.getWidth())/2;
        // imageView.setPadding(padding, 0, padding, 0);
        // imageView.setImageBitmap(imageBitmap);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        return byteArray;
    }

    public void sendProfilePictureMessage(final String imgPath) {
        String packetID = null;
        if (imgPath != null && imgPath.length() > 0) {
            try {
                String thumbImg = null;
                if (messageService != null) {
                    chatAdapter.setChatService(messageService);
                    thumbImg = MyBase64.encode(getByteArrayOfThumbnail(imgPath));
                    // packetID = messageService.sendMediaMessage(userName, "",
                    // imgPath,thumbImg,XMPPMessageType.atMeXmppMessageTypeGroupImage);
                    new ProfilePicUploader(this, messageService, true).execute(imgPath, packetID, "",
                            XMPPMessageType.atMeXmppMessageTypeGroupImage.name(), userName);
                }
            } catch (Exception ex) {
                System.out.println("7" + ex.toString());
            }
        }
    }

    public String getVCard(Intent data) {
        JSONObject finalJSONbject = new JSONObject();
        // Vector<String> number_type = new Vector<String>();
        // Vector<String> number = new Vector<String>();
        // Vector<String> email_type = new Vector<String>();
        // Vector<String> email = new Vector<String>();

        String contact_name = "";
        String first_name = "";
        String last_name = "";
        JSONArray number_array = new JSONArray();
        JSONObject number_array_element = null;

        JSONArray email_array = new JSONArray();
        JSONObject email_array_element = null;

        // Get the URI that points to the selected contact
        Uri contactUri = data.getData();
        // We only need the NUMBER column, because there will be only one row in
        // the result
        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID};

        String[] segments = contactUri.toString().split("/");
        String id = segments[segments.length - 1];

        // Perform the query on the contact to get the NUMBER column
        // We don't need a selection or sort order (there's only one result for
        // the given URI)
        // CAUTION: The query() method should be called from a separate thread
        // to avoid blocking
        // your app's UI thread. (For simplicity of the sample, this code
        // doesn't do that.)
        // Consider using CursorLoader to perform the query.
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null,
                null, null);
        cursor.moveToFirst();
        int j = 1;
        while (!cursor.isAfterLast()) {
            int cid = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            String contactid = cursor.getString(cid);

            if (contactid.equals(id)) {
                // Retrieve the phone number type
                int column_type = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                String type = null;
                if (column_type > -1)
                    type = cursor.getString(column_type);
                else
                    type = "Phone " + j++;

                // Retrieve the phone number from the NUMBER column
                int column_number = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column_number);

                // Retrieve the contact name from the DISPLAY_NAME column
                int column_name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                contact_name = cursor.getString(column_name);

                number_array_element = new JSONObject();
                try {
                    number_array_element.put("type", type);
                    number_array_element.put("number", number);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                number_array.put(number_array_element);

                // Toast.makeText(this, "I added the Contact: \n" + name + " " +
                // phone_number, Toast.LENGTH_SHORT).show();
            }
            cursor.moveToNext();
        }
        cursor.close();

        // email
        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, null, null,
                null);
        cursor.moveToFirst();
        int i = 1;
        while (!cursor.isAfterLast()) {
            int cid = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
            String contactid = cursor.getString(cid);

            if (contactid.equals(id)) {
                // Retrieve the contact name from the DISPLAY_NAME column
                int column_name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME_PRIMARY);
                if (contact_name.length() == 0)
                    contact_name = cursor.getString(column_name);

                // Retrieve the email type
                int column_type = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE);
                String type = null;
                if (column_type > -1)
                    type = cursor.getString(column_type);
                else {
                    column_type = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL);
                    if (column_type > -1)
                        type = cursor.getString(column_type);
                    else
                        type = "Email " + i++;
                }

                // Retrieve the contact name from the email column
                int column_email = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                String email = cursor.getString(column_email);

                email_array_element = new JSONObject();
                try {
                    email_array_element.put("type", type);
                    email_array_element.put("email", email);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                email_array.put(email_array_element);

                // Toast.makeText(this, "I added the Contact: email : "+email,
                // Toast.LENGTH_SHORT).show();
            }
            cursor.moveToNext();
        }
        cursor.close();
        // Add to final array
        try {
            if (contact_name.lastIndexOf(' ') != -1) {
                first_name = contact_name.substring(0, contact_name.lastIndexOf(' '));
                last_name = contact_name.substring(contact_name.lastIndexOf(' ') + 1);
            } else {
                first_name = contact_name;
                last_name = "";
            }
            finalJSONbject.put("firstName", first_name);
            finalJSONbject.put("lastName", last_name);
            finalJSONbject.put("phoneNumber", number_array);
            finalJSONbject.put("EmailIDs", email_array);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i(TAG, "getVCard :: JSON Object : " + finalJSONbject.toString());
        return finalJSONbject.toString();
    }

    // public void makJsonObject(JSONObject finalobject, String type[],
    // String phone_number[], int numberof_students)
    // throws JSONException {
    // JSONObject array_element = null;
    // JSONArray jsonArray = new JSONArray();
    // for (int i = 0; i < numberof_students; i++) {
    // array_element = new JSONObject();
    // try {
    // array_element.put("type", type[i]);
    // array_element.put("number", phone_number[i]);
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // jsonArray.put(array_element);
    // }
    // finalobject.put("phoneNumber", jsonArray);
    // }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 300) {
                if (iChatPref.isBroadCast(userName)) {
                    chatWindowName = iChatPref.getBroadCastDisplayName(userName);
                    if (chatWindowName.contains("##$^##"))
                        windowNameView.setText(chatWindowName.substring(0, chatWindowName.indexOf("##$^##")));
                    else
                        windowNameView.setText(chatWindowName);
                    notifyChatRecieveHandler.sendEmptyMessage(0);
                } else if (iChatPref.isGroupChat(userName)) {
                    chatWindowName = iChatPref.getGroupDisplayName(userName);
                    if (chatWindowName.contains("##$^##"))
                        windowNameView.setText(chatWindowName.substring(0, chatWindowName.indexOf("##$^##")));
                    else
                        windowNameView.setText(chatWindowName);
                    notifyChatRecieveHandler.sendEmptyMessage(0);
                } else {
                    chatWindowName = ChatDBWrapper.getInstance(getApplicationContext()).getUserDisplayName(userName);
                    if (chatWindowName.equals(userName))
                        chatWindowName = iChatPref.getUserServerName(userName);
                    windowNameView.setText(chatWindowName);
                }
                setProfilePic(userIcon, userIconDefault, contactNameTxt);
            }
            if (resultCode == RESULT_OK)
                switch (requestCode) {
                    case PICK_CONTACT:
                        String contact_json = getVCard(data);
                        if (contact_json != null) {
                            sendContact(contact_json, XMPPMessageType.atMeXmppMessageTypeContact);
                        } else {
                            // Show Error to user
                            Toast.makeText(this, "This contact has some error, Please try another one.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case REQUEST_PLACE_PICKER:
                        // The user has selected a place. Extract the name and
                        // address.
//                        StringBuffer full_address = new StringBuffer();
//                        ;
//                        final Place place = PlacePicker.getPlace(data, this);
//                        final CharSequence name = place.getName();//(28.6304491,// 77.0795350)
//                        if (name != null)
//                            full_address.append(name);
//                        final LatLng latLong = place.getLatLng();
//                        final CharSequence address = place.getAddress();
//                        if (address != null) {
//                            full_address.append("\n");
//                            full_address.append(address);
//                        }
//                        if (latLong != null && latLong.toString().length() > 0) {
//                            String location = latLong.toString();
//                            if (location.indexOf('(') != -1 && location.indexOf(')')
//                                    != -1)
//                                location = location.substring(location.indexOf('(') + 1,
//                                        location.indexOf(')'));
//                            sendLocation(location, full_address.toString(),
//                                    XMPPMessageType.atMeXmppMessageTypeLocation);
//                        }
                        break;
                    // case PICK_LOCATION:
                    // String location = data.getStringExtra("GET_LOCATION");
                    // sendLocation(location,
                    // XMPPMessageType.atMeXmppMessageTypeLocation);
                    // break;
                    case AppUtil.FILE_PDF_PICKER:
                        if (fileConfirmationDialog != null && !fileConfirmationDialog.isShowing()) {
                            if (data == null) {
                                return;
                            }
                            fileAttachUri = data.getData();
                            if (fileAttachUri != null) {
                                String fileName = "";
                                String tmpUri = fileAttachUri.toString();
                                if (tmpUri != null && (tmpUri.startsWith("content://com.google.android.apps"))) {// ||
                                    // tmpUri.startsWith("content://com.android.providers.downloads.documents"))){
                                    String mimeType = getContentResolver().getType(fileAttachUri);
                                    Log.i(TAG, "mimeType : " + mimeType);
                                    Cursor returnCursor = getContentResolver().query(fileAttachUri, null, null, null, null);
                                    int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                                    int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                                    Log.i(TAG, "sizeIndex : " + sizeIndex);
                                    returnCursor.moveToFirst();
                                    fileName = returnCursor.getString(nameIndex);
                                    Log.i(TAG, "returnCursor.getString(nameIndex) : " + fileName);

                                } else
                                    fileName = AppUtil.getPath(this, fileAttachUri);
                                if (fileName.contains("/")) {
                                    String[] fileSplit = fileName.split("/");
                                    fileName = fileSplit[fileSplit.length - 1];
                                }
                                fileConfirmMessageView.setText("Send" + " \"" + fileName + "\" to " + windowNameView.getText() + "?");
                                fileConfirmationDialog.show();

                            }
                        }
//                        if (data != null && data.getData() != null) {
//                            Uri uri = data.getData();
//                            if(uri!=null){
//                            String tmpUri = uri.toString();
//                            if (tmpUri != null && (tmpUri.startsWith("content://com.google.android.apps"))) {// ||
//                                // tmpUri.startsWith("content://com.android.providers.downloads.documents"))){
//                                String mimeType = getContentResolver().getType(uri);
//                                Log.i(TAG, "mimeType : " + mimeType);
//                                Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
//                                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                                Log.i(TAG, "sizeIndex : " + sizeIndex);
//                                returnCursor.moveToFirst();
//                                String path = returnCursor.getString(nameIndex);
//                                Log.i(TAG, "returnCursor.getString(nameIndex) : " + path);
//                                if (Build.VERSION.SDK_INT >= 11)
//                                    new FileFetchTask(uri, path).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                                else
//                                    new FileFetchTask(uri, path).execute();
//                                 return;
//                            }
//                            AppUtil.capturedPath1 = AppUtil.getPath(this, uri);
//                            if (AppUtil.capturedPath1 != null && (AppUtil.capturedPath1.toLowerCase().endsWith(".docx")
//                                    || AppUtil.capturedPath1.toLowerCase().endsWith(".doc")))
//                                sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeDoc);
//                            else if (AppUtil.capturedPath1 != null && (AppUtil.capturedPath1.toLowerCase().endsWith(".xls")
//                                    || AppUtil.capturedPath1.toLowerCase().endsWith(".xlsx")))
//                                sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeXLS);
//                            else if (AppUtil.capturedPath1 != null && (AppUtil.capturedPath1.toLowerCase().endsWith(".ppt")
//                                    || AppUtil.capturedPath1.toLowerCase().endsWith(".pptx")))
//                                sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypePPT);
//                            else if (AppUtil.capturedPath1 != null
//                                    && (AppUtil.capturedPath1.toLowerCase().endsWith(".pdf")))
//                                sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypePdf);
//                            else if (AppUtil.capturedPath1 != null
//                            		&& (AppUtil.capturedPath1.toLowerCase().endsWith(".jpg") || AppUtil.capturedPath1.toLowerCase().endsWith(".jpeg") || AppUtil.capturedPath1.toLowerCase().endsWith(".png"))){
//                            	CompressImage compressImage = new CompressImage(this);
//                                AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
//                                mediaUrl = AppUtil.capturedPath1;
//                                // sendPictureMessage(AppUtil.capturedPath1);
//                                if (captionDialog != null && !captionDialog.isShowing()) {
//                                    isVideoTagged = false;
//                                    isPictureTagged = true;
//                                    captionDialog.show();
//                                    Bitmap imageBitmap = BitmapFactory.decodeFile(mediaUrl);
//                                    mediaPreview.setImageBitmap(imageBitmap);
//
//                                }
////                            	sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeImage);
//                            }else if (AppUtil.capturedPath1 != null
//                            		&& (AppUtil.capturedPath1.toLowerCase().endsWith(".mp4") || AppUtil.capturedPath1.toLowerCase().endsWith(".3gp")
//                            		|| AppUtil.capturedPath1.toLowerCase().endsWith(".mov")
//                            		|| AppUtil.capturedPath1.toLowerCase().endsWith(".3gpp"))){
//                            	 mediaUrl = AppUtil.capturedPath1;//AppUtil.getPath(uri, this);
//                                 if (captionDialog != null && !captionDialog.isShowing()) {
//                                     isPictureTagged = false;
//                                     isVideoTagged = true;
//
//                                     captionDialog.show();
//                                     Bitmap imageBitmap = ThumbnailUtils.createVideoThumbnail(mediaUrl,
//                                             MediaStore.Video.Thumbnails.MINI_KIND);
//                                     mediaPreview.setImageBitmap(imageBitmap);
////                                     sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeVideo);
//                                 }
//                            }
//                            	
//                            else {
//                                Toast.makeText(this, "This content is not supported.", Toast.LENGTH_SHORT).show();
//                            }
//                        }else
//                        	 Toast.makeText(this, "This content is not supported.", Toast.LENGTH_SHORT).show();
//                        }
                        break;
                    case AppUtil.POSITION_CAMERA_VIDEO:
                    case AppUtil.POSITION_GALLRY_VIDEO:
                        if (data != null && data.getData() != null) {
                            Uri fileUri = (Uri) data.getData();
                            if (fileUri != null) {
                                mediaUrl = AppUtil.getPath(fileUri, this);
                                if (captionDialog != null && !captionDialog.isShowing()) {
                                    taggingType = VIDEO_TAGGING;
                                    if (captionTopOptions != null)
                                        captionTopOptions.setVisibility(View.INVISIBLE);
                                    captionDialog.show();
                                    Bitmap imageBitmap = ThumbnailUtils.createVideoThumbnail(mediaUrl,
                                            MediaStore.Video.Thumbnails.MINI_KIND);
                                    mediaPreview.setImageBitmap(imageBitmap);
                                    thumbPreview.setImageBitmap(imageBitmap);
                                }
                                if (captionTopOptions != null)
                                    captionTopOptions.setVisibility(View.INVISIBLE);
                                if (videoPlayButton != null)
                                    videoPlayButton.setVisibility(View.VISIBLE);
                                cropImg.setVisibility(View.GONE);
                                rotateImg.setVisibility(View.GONE);
                                // sendVideoMessage(mediaUrl);
                            }
                        }
                        break;
                    case AppUtil.FILE_AUDIO_TRACK:
                        if (data != null && data.getData() != null) {
                            Uri uri = data.getData();
                            if (uri != null) {
                                String tmpUri = uri.toString();
                                mediaUrl = AppUtil.getPath(uri, this);
                                Log.d(TAG, "FILE_AUDIO_TRACK - " + tmpUri + ", " + mediaUrl);
                                if (mediaUrl != null) {
//                            	 sendVoiceMessage(mediaUrl);
                                    if (captionDialogNew != null && !captionDialogNew.isShowing()) {
                                        taggingType = AUDIO_TAGGING;
                                        if (playSenderMaxTimeText != null) {
                                            totalAudioLength = getAudioFileDuration(mediaUrl) / 1000;
                                            if (totalAudioLength > 0) {
                                                final byte min = (byte) (totalAudioLength / 60);
                                                final byte sec = (byte) (totalAudioLength % 60);
                                                globalSeekBarValue = 0;
                                                globalSeekBarMaxValue = 0;
                                                if (min < 9)
                                                    playSenderMaxTimeText.setText("0" + min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                                                else
                                                    playSenderMaxTimeText.setText(min + ":" + ((sec < 10) ? ("0" + sec) : sec));
                                            }
                                        }
                                        captionDialogNew.show();
                                    } else {
                                        captionDialogNew = createCaptionDialogNew();
                                        taggingType = AUDIO_TAGGING;
                                        captionDialogNew.show();
                                    }
                                } else
                                    Toast.makeText(this, "Media type not supported.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case AppUtil.PIC_CROP:
                        String filePath = Environment.getExternalStorageDirectory() + "/" + Constants.contentTemp + "/"
                                + AppUtil.TEMP_PHOTO_FILE;

                        AppUtil.capturedPath1 = filePath;
                        CompressImage compressImage = new CompressImage(this);
                        AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
                        mediaUrl = AppUtil.capturedPath1;
                        mediaPreview.setImageURI(Uri.parse(AppUtil.capturedPath1));

//                        userIcon.setImageURI(Uri.parse(AppUtil.capturedPath1));
//                        sendProfilePictureMessage(AppUtil.capturedPath1);
                        break;
                    case AppUtil.POSITION_CAMERA_PICTURE:
                    case AppUtil.POSITION_GALLRY_PICTURE:
                    case 5:
                        if (data != null && data.getData() != null) {
                            Uri uri = data.getData();
                            AppUtil.capturedPath1 = AppUtil.getPath(uri, this);
                        }
                        if (AppUtil.capturedPath1 != null) {
                            if (AppUtil.capturedPath1.toLowerCase().endsWith(".jpg")
                                    || AppUtil.capturedPath1.toLowerCase().endsWith(".jpeg")
                                    || AppUtil.capturedPath1.toLowerCase().endsWith(".png")) {
                                compressImage = new CompressImage(this);
                                if (requestCode == AppUtil.POSITION_CAMERA_PICTURE || requestCode == 5)
                                    compressImage.saveImage(true);
                                AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
                                mediaUrl = AppUtil.capturedPath1;
                                // sendPictureMessage(AppUtil.capturedPath1);
                                if (captionDialog != null && !captionDialog.isShowing()) {
                                    taggingType = IMAGE_TAGGING;
                                    if (captionTopOptions != null)
                                        captionTopOptions.setVisibility(View.VISIBLE);
                                    if (videoPlayButton != null)
                                        videoPlayButton.setVisibility(View.GONE);
                                    cropImg.setVisibility(View.VISIBLE);
                                    rotateImg.setVisibility(View.VISIBLE);
                                    captionDialog.show();
                                    Bitmap imageBitmap = BitmapFactory.decodeFile(mediaUrl);
                                    if (requestCode == AppUtil.POSITION_CAMERA_PICTURE)
                                        imageBitmap = AppUtil.rotateImage(AppUtil.capturedPath1, imageBitmap);
                                    mediaPreview.setImageBitmap(imageBitmap);
                                    thumbPreview.setImageBitmap(imageBitmap);

                                }
                            } else if (AppUtil.capturedPath1.toLowerCase().endsWith(".mp4") || AppUtil.capturedPath1.toLowerCase().endsWith(".3gp")) {
                                // sendVideoMessage(AppUtil.capturedPath1);
                                mediaUrl = AppUtil.capturedPath1;
                                if (captionDialog != null && !captionDialog.isShowing()) {
                                    taggingType = VIDEO_TAGGING;
                                    if (captionTopOptions != null)
                                        captionTopOptions.setVisibility(View.INVISIBLE);
                                    if (videoPlayButton != null)
                                        videoPlayButton.setVisibility(View.VISIBLE);
                                    cropImg.setVisibility(View.GONE);
                                    rotateImg.setVisibility(View.GONE);
                                    captionDialog.show();
                                    Bitmap imageBitmap = ThumbnailUtils.createVideoThumbnail(mediaUrl,
                                            MediaStore.Video.Thumbnails.MINI_KIND);
                                    mediaPreview.setImageBitmap(imageBitmap);
                                    thumbPreview.setImageBitmap(imageBitmap);
                                }
                            } else {
                                Toast.makeText(this, "Media are not supported.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Media are not supported.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        if (startDateAndTimeClicked) {
            pollStartCalender.set(year, monthOfYear, dayOfMonth);
            lblStartDate.setText(dateFormat.format(pollStartCalender.getTime()));
            lblStartTime.setText(timeFormat.format(pollStartCalender.getTime()));

        } else {
            pollEndCalender.set(year, monthOfYear, dayOfMonth);
            lblEndDate.setText(dateFormat.format(pollEndCalender.getTime()));
            lblEndTime.setText(timeFormat.format(pollEndCalender.getTime()));
        }

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        if (startDateAndTimeClicked) {
            pollStartCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
            pollStartCalender.set(Calendar.MINUTE, minute);
            lblStartDate.setText(dateFormat.format(pollStartCalender.getTime()));
            lblStartTime.setText(timeFormat.format(pollStartCalender.getTime()));

        } else {
            pollEndCalender.set(Calendar.HOUR_OF_DAY, hourOfDay);
            pollEndCalender.set(Calendar.MINUTE, minute);
            lblEndDate.setText(dateFormat.format(pollEndCalender.getTime()));
            lblEndTime.setText(timeFormat.format(pollEndCalender.getTime()));

//            System.out.println("Time diff => "+(pollEndCalender.getTime().getTime() - pollStartCalender.getTime().getTime())/1000+" seconds");
        }

    }

    private class FileFetchTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        Uri uri;
        String path;

        FileFetchTask(Uri uri, String path) {
            this.uri = uri;
            this.path = path;
        }

        protected void onPreExecute() {

            dialog = ProgressDialog.show(ChatListScreen.this, "", "File fetching. Please wait...", true);

            // progressBarView.setVisibility(ProgressBar.VISIBLE);
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            // String path = args[0];
            String full_path = null;
            try {
                full_path = writeFileAndGetFullPath(getContentResolver().openInputStream(uri),
                        new File(Environment.getExternalStorageDirectory() + "//" + path));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

            return full_path;
        }

        protected void onPostExecute(String full_path) {
            // if(AppUtil.capturedPath1 != null &&
            // (AppUtil.capturedPath1.toLowerCase().endsWith(".doc")
            // || AppUtil.capturedPath1.toLowerCase().endsWith(".docx")))
            if (full_path != null
                    && (full_path.toLowerCase().endsWith(".doc") || full_path.toLowerCase().endsWith(".docx")))
                sendFile(AppUtil.capturedPath1 = full_path, XMPPMessageType.atMeXmppMessageTypeDoc);
            else if (full_path != null && (full_path.toLowerCase().endsWith(".pdf")))
                sendFile(AppUtil.capturedPath1 = full_path, XMPPMessageType.atMeXmppMessageTypePdf);
            else if (full_path != null
                    && (full_path.toLowerCase().endsWith(".xls") || full_path.toLowerCase().endsWith(".xlsx")))
                sendFile(AppUtil.capturedPath1 = full_path, XMPPMessageType.atMeXmppMessageTypeXLS);
            else if (full_path != null
                    && (full_path.toLowerCase().endsWith(".ppt") || full_path.toLowerCase().endsWith(".pptx")))
                sendFile(AppUtil.capturedPath1 = full_path, XMPPMessageType.atMeXmppMessageTypePPT);
            else if (AppUtil.capturedPath1 != null
                    && (AppUtil.capturedPath1.toLowerCase().endsWith(".jpg") || AppUtil.capturedPath1.toLowerCase().endsWith(".jpeg") || AppUtil.capturedPath1.toLowerCase().endsWith(".png"))) {
                CompressImage compressImage = new CompressImage(ChatListScreen.this);
                AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
                mediaUrl = AppUtil.capturedPath1;
                // sendPictureMessage(AppUtil.capturedPath1);
                if (captionDialog != null && !captionDialog.isShowing()) {
                    taggingType = IMAGE_TAGGING;
                    captionDialog.show();
                    Bitmap imageBitmap = BitmapFactory.decodeFile(mediaUrl);
                    mediaPreview.setImageBitmap(imageBitmap);
                    thumbPreview.setImageBitmap(imageBitmap);

                }
//            	sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeImage);
            } else if (AppUtil.capturedPath1 != null
                    && (AppUtil.capturedPath1.toLowerCase().endsWith(".mp4") || AppUtil.capturedPath1.toLowerCase().endsWith(".3gp")
                    || AppUtil.capturedPath1.toLowerCase().endsWith(".mov")
                    || AppUtil.capturedPath1.toLowerCase().endsWith(".3gpp"))) {
                mediaUrl = AppUtil.capturedPath1;
                if (captionDialog != null && !captionDialog.isShowing()) {
                    taggingType = VIDEO_TAGGING;

                    captionDialog.show();
                    Bitmap imageBitmap = ThumbnailUtils.createVideoThumbnail(mediaUrl,
                            MediaStore.Video.Thumbnails.MINI_KIND);
                    mediaPreview.setImageBitmap(imageBitmap);
                    thumbPreview.setImageBitmap(imageBitmap);
//                     sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeVideo);
                }
            }
            dialog.cancel();
            super.onPostExecute(full_path);
        }
    }

    private String writeFileAndGetFullPath(InputStream in, File file) {
        String full_path = file.getAbsolutePath();
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return full_path;
    }

    public void sendSharingData() {
        if (HomeScreen.calledForShare) {
            String uriPath = HomeScreen.shareUri;
            if (uriPath != null) {
                switch (HomeScreen.sharingType) {
                    case HomeScreen.VIDEO_SHARING:
                        sendVideoMessage(uriPath);
                        break;
                    case HomeScreen.VOICE_SHARING:
                        break;
                    case HomeScreen.IMAGE_SHARING:
                        CompressImage compressImage = new CompressImage(this);
                        uriPath = compressImage.compressImage(uriPath);
                        sendPictureMessage(uriPath);
                        break;
                    case HomeScreen.DOC_SHARING:
                        if (!HomeScreen.calledForShare)
                            AppUtil.capturedPath1 = AppUtil.getPath(this, Uri.parse(uriPath));
                        else
                            AppUtil.capturedPath1 = uriPath;
                        sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeDoc);
                        break;
                    case HomeScreen.PDF_SHARING:
                        if (!HomeScreen.calledForShare)
                            AppUtil.capturedPath1 = AppUtil.getPath(this, Uri.parse(uriPath));
                        else
                            AppUtil.capturedPath1 = uriPath;
                        sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypePdf);
                        break;
                    case HomeScreen.XLS_SHARING:
                        if (!HomeScreen.calledForShare)
                            AppUtil.capturedPath1 = AppUtil.getPath(this, Uri.parse(uriPath));
                        else
                            AppUtil.capturedPath1 = uriPath;
                        sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeXLS);
                        break;
                    case HomeScreen.PPT_SHARING:
                        if (!HomeScreen.calledForShare)
                            AppUtil.capturedPath1 = AppUtil.getPath(this, Uri.parse(uriPath));
                        else
                            AppUtil.capturedPath1 = uriPath;
                        sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypePPT);
                        break;
                }

            }
            HomeScreen.calledForShare = false;
        }

    }

    private void performCrop(byte resultCode) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            File file = new File(AppUtil.capturedPath1);
            Uri outputFileUri = Uri.fromFile(file);
            // System.out.println("----outputFileUri:" + outputFileUri);
            cropIntent.setDataAndType(outputFileUri, "image/*");
//            cropIntent.putExtra("outputX", 300);
//            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("aspectX", 3);
            cropIntent.putExtra("aspectY", 2);
            cropIntent.putExtra("scale", true);
            try {
                cropIntent.putExtra("return-data", false);
                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, AppUtil.getTempUri());
                cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                startActivityForResult(cropIntent, resultCode);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void getServerGroupProfile(String groupId) {
        final Context context = this;
        Log.d("AsyncHttpClient", "AsyncHttpClient onStart: groupId : " + groupId);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Constants.SERVER_URL + "/tiger/rest/group/detail?groupName=" + groupId + "&nameNeeded=true", null,
                new AsyncHttpResponseHandler() {
                    ProgressDialog dialog = null;

                    @Override
                    public void onStart() {
                        // dialog = ProgressDialog.show(ChatListScreen.this, "",
                        // "Loading. Please wait...", true);
                        Log.d("AsyncHttpClient", "AsyncHttpClient onStart: ");
                    }

                    @Override
                    public void onSuccess(int arg0, String arg1) {
                        Log.d("AsyncHttpClient", "AsyncHttpClient onSuccess: " + arg1);

                        Gson gson = new GsonBuilder().create();
                        GroupChatServerModel objUserModel = gson.fromJson(arg1, GroupChatServerModel.class);
                        if (objUserModel != null) {

                            Log.d("AsyncHttpClient", "AsyncHttpClient onSuccess: " + objUserModel.getFileId());
                            String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02
                            String description = objUserModel.getDescription();
                            if (description != null) {
                                SharedPrefManager.getInstance().saveUserStatusMessage(userName, description);
                            }
                            String displayName = objUserModel.getDisplayName();
                            if (displayName != null) {
                                SharedPrefManager.getInstance().saveGroupDisplayName(userName, displayName);
                                chatWindowName = displayName;
                                windowNameView.setText(displayName);
                            }

                            if (groupPicId == null || !groupPicId.equals(objUserModel.getFileId())) {
                                String newFileId = objUserModel.getFileId();
                                if (newFileId != null && newFileId.contains(".jpg")) {
                                    newFileId = newFileId.substring(0, newFileId.indexOf("."));
                                    SharedPrefManager.getInstance().saveUserFileId(objUserModel.getGroupName(), newFileId);
                                    // groupPicId =
                                    // SharedPrefManager.getInstance().getUserFileId(objUserModel.getGroupName());
                                    (new ProfilePicDownloader()).download(Constants.media_get_url + newFileId + ".jpg",
                                            userIcon, null);
                                }
                            } else {
                                setProfilePic(userIcon, userIconDefault, contactNameTxt);
                            }
                            iChatPref.saveUserGroupInfo(userName, objUserModel.getUserName(),
                                    SharedPrefManager.GROUP_OWNER_INFO, true);

                            List<String> adminUserSet = objUserModel.getAdminUserSet();
                            for (String admin : adminUserSet) {
                                iChatPref.saveUserGroupInfo(userName, admin, SharedPrefManager.GROUP_ADMIN_INFO, true);
                                iChatPref.saveUsersOfGroup(userName, admin);
                                iChatPref.saveUserGroupInfo(userName, admin, SharedPrefManager.GROUP_ACTIVE_INFO, true);
                            }
                            List<String> memberUserSet = objUserModel.getMemberUserSet();
                            for (String member : memberUserSet) {
                                iChatPref.saveUsersOfGroup(userName, member);
                                iChatPref.saveUserGroupInfo(userName, member, SharedPrefManager.GROUP_ACTIVE_INFO,
                                        true);
                            }
                            iChatPref.saveUsersOfGroup(userName, iChatPref.getUserName());
                            iChatPref.saveUsersOfGroup(userName, objUserModel.getUserName());

                            iChatPref.saveUserGroupInfo(userName, iChatPref.getUserName(),
                                    SharedPrefManager.GROUP_ACTIVE_INFO, true);
                            iChatPref.saveUserGroupInfo(userName, objUserModel.getUserName(),
                                    SharedPrefManager.GROUP_ACTIVE_INFO, true);
                        }

                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        super.onSuccess(arg0, arg1);
                    }

                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        Log.d("AsyncHttpClient", "AsyncHttpClient onFailure: " + arg1);
                        if (dialog != null) {
                            dialog.dismiss();
                            dialog = null;
                        }
                        // showDialog("Please try again later.");
                        super.onFailure(arg0, arg1);
                    }
                });
    }

    private void getServerUserProfile(String userId) {
        final Context context = this;

        AsyncHttpClient client = new AsyncHttpClient();
        client = SuperChatApplication.addHeaderInfo(client, true);
        client.get(Constants.SERVER_URL + "/tiger/rest/user/profile/get?userName=" + userId, null,
                new AsyncHttpResponseHandler() {
                    ProgressDialog dialog = null;

                    @Override
                    public void onStart() {
                        // dialog = ProgressDialog.show(ChatListScreen.this,
                        // "","Loading. Please wait...", true);
                        // Log.d(TAG, "AsyncHttpClient onStart: ");
                    }

                    @Override
                    public void onSuccess(int arg0, String arg1) {
                        Log.d(TAG, "AsyncHttpClient onSuccess: " + arg1);

                        Gson gson = new GsonBuilder().create();
                        UserProfileModel objUserModel = gson.fromJson(arg1, UserProfileModel.class);
                        if (arg1 == null || arg1.contains("error")) {
                            if (dialog != null) {
                                dialog.dismiss();
                                dialog = null;
                            }
                            return;
                        }
                        if (objUserModel != null) {
                            String userPicId = SharedPrefManager.getInstance().getUserFileId(userName);
                            // 1_1_7_G_I_I3_e1zihzwn02
                            String status = objUserModel.currentStatus;
                            if (status != null) {
                                SharedPrefManager.getInstance().saveUserStatusMessage(userName, status);
                            }
                            if (objUserModel.iName != null && !objUserModel.iName.equals("")) {
                                iChatPref.saveUserServerName(userName, objUserModel.iName);
                                if (!iChatPref.isGroupChat(userName) && !iChatPref.isBroadCast(userName))
                                    addNewContactEntry(objUserModel.iName, userName, objUserModel.iMobileNumber);
                            }
                            if (objUserModel.imageFileId != null && (userPicId == null || !userPicId.equals(objUserModel.imageFileId))) {
                                String newFileId = objUserModel.imageFileId;
                                if (newFileId.contains(".jpg"))
                                    newFileId = newFileId.substring(0,
                                            newFileId.indexOf("."));
                                iChatPref.saveUserFileId(userName, newFileId);
                                // groupPicId = SharedPrefManager.getInstance().getUserFileId(objUserModel.getGroupName());
//	                         (new ProfilePicDownloader()).download(Constants.media_get_url+newFileId+".jpg",userIcon,null);
                                if (Build.VERSION.SDK_INT >= 11)
                                    new BitmapDownloader(userIcon).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, newFileId, BitmapDownloader.THUMB_REQUEST);
                                else
                                    new BitmapDownloader(userIcon).execute(newFileId, BitmapDownloader.THUMB_REQUEST);
                            } else {
                                setProfilePic(userIcon, userIconDefault, contactNameTxt);
                            }
                            String userState = objUserModel.userState;
                            if (userState != null) {
                                if (userState.equals("inactive"))
                                    iChatPref.saveUserExistence(userName, false);
                                else if (userState.equals("active"))
                                    iChatPref.saveUserExistence(userName, true);
                            }
                        }
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
                        // showDialog("Please try again later.");
                        super.onFailure(arg0, arg1);
                    }
                });
    }

    private void addNewContactEntry(String displayName, String tmpUserName, String tmpMobile) {
        String number = DBWrapper.getInstance().getContactNumber(tmpUserName);
        if (number != null && !number.equals(""))
            return;
        ContentValues contentvalues = new ContentValues();
        contentvalues.put(DatabaseConstants.USER_NAME_FIELD, tmpUserName);
        contentvalues.put(DatabaseConstants.VOPIUM_FIELD, Integer.valueOf(1));
        contentvalues.put(DatabaseConstants.DATA_ID_FIELD, Integer.valueOf("5"));
        contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD, tmpMobile);
        contentvalues.put(DatabaseConstants.STATE_FIELD, Integer.valueOf(0));
        contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, displayName);
        contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
        int id = tmpUserName.hashCode();
        if (id < -1)
            id = -(id);
        contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD, Integer.valueOf(id));
        contentvalues.put(DatabaseConstants.RAW_CONTACT_ID, Integer.valueOf(id));
        //Save USerID and SG in DB
        contentvalues.put(DatabaseConstants.USER_ID, iChatPref.getUserId());
        contentvalues.put(DatabaseConstants.USER_SG, iChatPref.getUserDomain());

        DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, contentvalues);
    }

    boolean isPlaying = false;
    public RTMediaPlayer myVoicePlayer;
    int globalSeekBarValue = 0;
    int globalSeekBarMaxValue = 0;
    long currentAudioPlayCounter = 0;
    int totalAudioLength;
    static Handler handler;

    @Override
    public void onClick(View v) {
        if (iChatPref.isDNM(userName) && !iChatPref.isDomainAdminORSubAdmin() && v.getId() != R.id.id_cancel_btn) {
            Toast.makeText(this, getString(R.string.dnm_alert), Toast.LENGTH_SHORT).show();
            return;
        }
        if (iChatPref.isBlocked(userName) && v.getId() != R.id.id_cancel_btn) {
            showDialog(getString(R.string.block_alert));
            return;
        }
        if (iChatPref.isGroupChat(userName) && !iChatPref.isGroupMemberActive(userName, iChatPref.getUserName())) {
            ((RelativeLayout) findViewById(R.id.bottom_write_bar1)).setVisibility(View.GONE);
            attachMediaView.setVisibility(View.GONE);
            chatList.setFastScrollEnabled(true);
            chatAdapter.setEditableChat(false);
            chatAdapter.notifyDataSetChanged();
            editHeaderLayout.setVisibility(View.GONE);
            mainHeaderLayout.setVisibility(View.VISIBLE);
            chatList.setScrollY(0);
            isLastIndex = false;
            return;
        }
        if (!iChatPref.isUserExistence(userName)) {
            ((RelativeLayout) findViewById(R.id.bottom_write_bar1)).setVisibility(View.GONE);
            attachMediaView.setVisibility(View.GONE);
            callOption.setBackgroundResource(R.drawable.call_icon);
            callOption.setVisibility(View.GONE);
            showDialog(iChatPref.getUserServerName(userName) + " has been removed.");
            return;
        }
        switch (v.getId()) {
            case R.id.media_play:
                if (isPlaying) {
                    stopVoicePlay();
                    globalSeekBarValue = 0;
                    globalSeekBarMaxValue = 0;
                    playSenderView.setBackgroundResource(R.drawable.audio_play);
                    isPlaying = false;
                    playSenderSeekBar.setProgress(0);
                    handler.sendEmptyMessage(0);
                } else {

                    myVoicePlayer.setMediaHandler(this);
                    myVoicePlayer.setProgressBar(playSenderSeekBar);
                    playSenderView.setBackgroundResource(R.drawable.audio_stop);
                    myVoicePlayer._startPlay(mediaUrl, playSenderView, handler);
//					totalAudioLength = myVoicePlayer.getDuration() / 1000;
                    globalSeekBarValue = 0;
                    isPlaying = true;
                }
                break;
            case R.id.video_play_btn:
                Intent video_play = new Intent();
                video_play.setAction(Intent.ACTION_VIEW);
                if (mediaUrl.contains(".mp4") || mediaUrl.endsWith(".3gp")) {
                    video_play.setDataAndType(Uri.parse(mediaUrl), "video/*");
                }
                startActivity(video_play);
                break;
            case R.id.id_send_btn:

                switch (taggingType) {
                    case IMAGE_TAGGING:
                        captionText = captionField.getText().toString();
                        sendPictureMessage(mediaUrl);
                        if (captionDialog != null && captionDialog.isShowing()) {
                            captionDialog.cancel();
                            AppUtil.capturedPath1 = null;
                            taggingType = NO_TAGGING;
                            captionField.setText("");
                        }
                        break;
                    case VIDEO_TAGGING:
                        captionText = captionField.getText().toString();
                        sendVideoMessage(mediaUrl);
                        if (captionDialog != null && captionDialog.isShowing()) {
                            captionDialog.cancel();
                            AppUtil.capturedPath1 = null;
                            taggingType = NO_TAGGING;
                            captionField.setText("");
                        }
                        break;
                    case AUDIO_TAGGING:
                        captionText = captionFieldNew.getText().toString();
                        sendVoiceMessage(mediaUrl);
                        if (captionDialogNew != null && captionDialogNew.isShowing()) {
                            captionDialogNew.cancel();
                            AppUtil.capturedPath1 = null;
                            taggingType = NO_TAGGING;
                            captionFieldNew.setText("");
                        }
                        break;
                }
                break;
            case R.id.id_cancel_btn:
            case R.id.id_back_arrow:
                if (captionDialog != null && captionDialog.isShowing()) {
                    captionDialog.cancel();
                    taggingType = NO_TAGGING;
                    if (captionField != null)
                        captionField.setText("");
                }
                if (captionDialogNew != null && captionDialogNew.isShowing()) {
                    captionDialogNew.cancel();
                    taggingType = NO_TAGGING;
                    if (captionField != null)
                        captionField.setText("");
                }
                if (isPlaying)
                    myVoicePlayer.reset();
                break;
            case R.id.id_crop_image:
                if (captionDialog != null && captionDialog.isShowing()) {
                    performCrop(AppUtil.PIC_CROP);
                }
                break;
            case R.id.id_rotate_image:
                if (captionDialog != null && captionDialog.isShowing()) {
                    String photopath = AppUtil.capturedPath1;
                    Bitmap bmp = BitmapFactory.decodeFile(photopath);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                    try {
                        new AndroidBmpUtil().save(bmp, AppUtil.capturedPath1);
//                    	 FileOutputStream fOut;
//                         fOut = new FileOutputStream(AppUtil.capturedPath1);
//                         bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//                         fOut.flush();
//                         fOut.close();

                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    mediaPreview.setImageBitmap(bmp);
                    thumbPreview.setImageBitmap(bmp);
                }
                break;
            case R.id.id_send_chat:
                if (!ChatService.xmppConectionStatus) {
                    Toast.makeText(getApplicationContext(), "Connecting..", Toast.LENGTH_SHORT).show();
                    return;
                }
                String newMessage = typingEditText.getText().toString().trim();
                if (newMessage.length() > 0) {
                    typingEditText.setText("");
                    if (messageService != null) {
                        // if (iChatPref.isBroadCast(userName)){
                        // for(String person: usersList)
                        // messageService.sendBroadCastMessage(person, newMessage);
                        // }
                        // messageService.sendMessage(userName, newMessage);
                        if (isBulletinBroadcastForAdmin()) {
                            messageService.sendBroadCastMessageToAll(userName, newMessage);
                        } else if (iChatPref.isSharedIDContact(userName)) {
                            //Send Shared ID message
                            messageService.sendSharedIDMessageToAllAdmins(userName, newMessage);
                        } else if (iChatPref.isBroadCast(userName))
                            messageService.sendBroadCastMessage(userName, newMessage);
                        else
                            messageService.sendMessage(userName, newMessage);
                    }
                    chatAdapter.notifyDataSetChanged();
                    chatList.setSelection(chatAdapter.getCount() - 1);
                }
                break;
            case R.id.id_ok_title:
                chatList.setFastScrollEnabled(true);
                chatAdapter.setEditableChat(false);
                chatAdapter.notifyDataSetChanged();
                editHeaderLayout.setVisibility(View.GONE);
                mainHeaderLayout.setVisibility(View.VISIBLE);
                chatList.setScrollY(0);
                isLastIndex = false;
                break;
            case R.id.id_copy_iv:
                ArrayList<String> listArray = getSelectedChat();
                String copiedText = ChatDBWrapper.getInstance().getSelectedChatIteams(listArray).trim();
                if (copiedText != null && copiedText.length() > 0 && !copiedText.equals("")) {

                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(
                                CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", copiedText);
                        clipboard.setPrimaryClip(clip);
                    } else {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
                                CLIPBOARD_SERVICE);
                        clipboard.setText(copiedText);
                    }
                    Toast.makeText(getApplicationContext(), "Chat copied", Toast.LENGTH_SHORT).show();
                }
                chatAdapter.removeSelectedItems();
                chatList.setSelection(chatAdapter.getCount() - 1);
                chatAdapter.notifyDataSetChanged();
                for (int i = 0; i < chatList.getCount(); i++)
                    chatList.setItemChecked(i, false);
                break;
            case R.id.id_delete_iv:

                ArrayList<String> listArray01 = getSelectedChat();
                boolean isDelete = ChatDBWrapper.getInstance().deleteSelectedChatIteams(listArray01);
                if (isDelete) {
                    Toast.makeText(getApplicationContext(), "Chat deleted", Toast.LENGTH_SHORT).show();
                }

                Cursor cursor1 = null;
                if (isBulletinBroadcast)
                    cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_BULLETIN);
                else
                    cursor1 = ChatDBWrapper.getInstance().getUserChatList(userName, CHAT_LIST_NORMAL);
                for (int i = 0; i < chatList.getCount(); i++)
                    chatList.setItemChecked(i, false);
                chatAdapter.swapCursor(cursor1);
                chatList.setAdapter(chatAdapter);
                chatAdapter.removeSelectedItems();
                chatList.setSelection(chatAdapter.getCount() - 1);
                chatAdapter.notifyDataSetChanged();

                break;
            case R.id.id_chat_icon:
            case R.id.id_info_iv:


                String selectedMessageId = null;//chatInfoIv.getTag().toString();

                for (String key : selectedTagMap.keySet()) {
                    if (selectedTagMap.get(key)) {
                        selectedMessageId = key;
                        break;
                    }
                }
                if (selectedMessageId != null && !selectedMessageId.equals("")) {
                    if (chatAdapter != null && chatAdapter.isEditableChat()) {
                        chatAdapter.removeSelectedItems();
                        onClick(okEditTextView);
                    }
                    // ChatDBWrapper.getInstance(SuperChatApplication.context).getGroupOrBroadCastUsersStatus(selectedMessageId);
                    Intent intent = new Intent(this, GroupStausInfoScreen.class);
                    intent.putStringArrayListExtra(Constants.GROUP_USERS, usersList);
                    intent.putExtra(Constants.USER_MAP, nameMap);
                    intent.putExtra(Constants.SELECTED_MESSAGE_ID, selectedMessageId);
                    if (iChatPref.isBroadCast(userName))
                        intent.putExtra(Constants.BROADCAST, true);
                    intent.putExtra(Constants.CHAT_USER_NAME, userName);
                    intent.putExtra(Constants.CHAT_NAME, windowNameView.getText());
                    startActivityForResult(intent, 300);
                    for (int i = 0; i < chatList.getCount(); i++)
                        chatList.setItemChecked(i, false);
                }
                break;
//            case R.id.id_chat_icon:
//                String file_path = (String) v.getTag();
//                if (file_path != null) {
////                    Intent intent = new Intent();
////                    intent.setAction(Intent.ACTION_VIEW);
////                    if (file_path.startsWith("http://"))
////                        intent.setDataAndType(Uri.parse(file_path), "image/*");
////                    else
////                        intent.setDataAndType(Uri.parse("file://" + file_path), "image/*");
////                    startActivity(intent);
//                	 String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName);
//                    if (Build.VERSION.SDK_INT >= 11)
//						new BitmapDownloader(this,(ImageView)v).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,groupPicId,BitmapDownloader.PIC_VIEW_REQUEST);
//		             else
//		            	 new BitmapDownloader(this,(ImageView)v).execute(groupPicId,BitmapDownloader.PIC_VIEW_REQUEST);
//                }
//                break;
            // case R.id.id_audio_record:
            // if (attachOptionsDialog != null && attachOptionsDialog.isShowing()) {
            // attachOptionsDialog.cancel();
            //
            // // if(!voiceRecorderDialog.isShowing())
            // tapToReordView.setVisibility(View.VISIBLE);
            // audioRecordLayout.setVisibility(View.INVISIBLE);
            // voiceRecorderDialog.show();
            // }

            // break;

            case R.id.id_audio_record:
                if (attachOptionsDialog != null && attachOptionsDialog.isShowing()) {
                    attachOptionsDialog.cancel();
                }
//                AppUtil.openVideo(this, AppUtil.POSITION_CAMERA_VIDEO);

                if (attachAudioTrackDialog != null && !attachAudioTrackDialog.isShowing()) {
                    attachAudioTrackDialog.show();

                    if (chatAdapter != null && chatAdapter.myVoicePlayer != null) {
                        try {
                            chatAdapter.myVoicePlayer.reset();
                            chatAdapter.myVoicePlayer.clear();
                        } catch (Exception e) {
                        }
                    }
                }
                hideMenu();
                break;
            case R.id.id_attach_pic:
                if (attachOptionsDialog != null && attachOptionsDialog.isShowing()) {
                    attachOptionsDialog.cancel();
                }
                AppUtil.openGallery(this, AppUtil.POSITION_GALLRY_PICTURE);
                hideMenu();

                break;
            case R.id.id_camera:
                if (attachOptionsDialog != null && attachOptionsDialog.isShowing()) {
                    attachOptionsDialog.cancel();
                }
                if (cameraOptionsDialog != null && !cameraOptionsDialog.isShowing()) {
                    cameraOptionsDialog.show();
                }
                hideMenu();

                break;
            case R.id.id_video_record_item:
//            case R.id.id_video_record:
                if (cameraOptionsDialog != null && cameraOptionsDialog.isShowing()) {
                    cameraOptionsDialog.cancel();
                }
                AppUtil.openVideo(this, AppUtil.POSITION_CAMERA_VIDEO);
                break;
            case R.id.id_take_picture_item:
                if (cameraOptionsDialog != null && cameraOptionsDialog.isShowing()) {
                    cameraOptionsDialog.cancel();
                }
                AppUtil.openCamera(this, AppUtil.capturedPath1, AppUtil.POSITION_CAMERA_PICTURE);
                break;
            // case R.id.location_iv:
            // Toast.makeText(ChatListScreen.this,
            // getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
            // break;
            case R.id.id_attach_file:
                // Toast.makeText(ChatListScreen.this,
                // getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
                if (attachOptionsDialog != null && attachOptionsDialog.isShowing()) {
                    attachOptionsDialog.cancel();
                }
                AppUtil.openPdf(this, AppUtil.FILE_PDF_PICKER);
                hideMenu();
                break;
            case R.id.id_create_poll:
                if (!isPollOptionShown)
                    break;
                if (attachOptionsDialog != null && attachOptionsDialog.isShowing()) {
                    attachOptionsDialog.cancel();
                }
//                Toast.makeText(ChatListScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
                onPollClicked("");
                hideMenu();
                break;
            case create_doodle:
                if (attachOptionsDialog != null && attachOptionsDialog.isShowing()) {
                    attachOptionsDialog.cancel();
                }
                Intent intent = new Intent(this, RTCanvas.class);
                startActivityForResult(intent, POSITION_PICTURE_RT_CANVAS);
                hideMenu();
                break;
            case R.id.share_contact:
                if (attachOptionsDialog != null && attachOptionsDialog.isShowing()) {
                    attachOptionsDialog.cancel();
                }
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
                hideMenu();
                break;
            case R.id.share_location:
                if (attachOptionsDialog != null &&
                        attachOptionsDialog.isShowing()) {
                    attachOptionsDialog.cancel();
                }
//                Toast.makeText(ChatListScreen.this, getString(R.string.coming_soon_lbl), Toast.LENGTH_SHORT).show();
//                try {
//                    PlacePicker.IntentBuilder builder = new
//                            PlacePicker.IntentBuilder();
//                    startActivityForResult(builder.build(this),
//                            REQUEST_PLACE_PICKER);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
                onCurrentLocationClicked(v);
                hideMenu();
                break;
            case R.id.id_cancel_track_dialog:
                if (attachAudioTrackDialog != null && attachAudioTrackDialog.isShowing()) {
                    attachAudioTrackDialog.cancel();
                }
                break;
            case R.id.id_select_audio_track:
                if (attachAudioTrackDialog != null && attachAudioTrackDialog.isShowing()) {
                    attachAudioTrackDialog.cancel();
                }
                try {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, AppUtil.FILE_AUDIO_TRACK);
                } catch (Exception e) {
//        			   intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//        			   intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
//        			     startActivityForResult(intent, AppUtil.FILE_AUDIO_TRACK);
                    Toast.makeText(this, "Feature not supported.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.id_use_recorder:
                if (attachAudioTrackDialog != null && attachAudioTrackDialog.isShowing()) {
                    attachAudioTrackDialog.cancel();
                }
                if (voiceRecorderDialog != null && !voiceRecorderDialog.isShowing()) {
                    wheelProgress.refreshWheel(false);//initVoiceRecorderWheelDialog();
                    wheelProgress.setProgress(0);
                    wheelProgress.setWheelType(ProgressWheel.IDEAL_WHEEL);
                    setRecordingViews(wheelProgress.getWheelType());
                    if (wheelTimer != null) {
                        wheelTimer.cancel();
                    }
                    voiceRecorderDialog.show();
                }
//            	intent = new Intent(this,WheelDialogActivity.class);
//            	startActivity(intent);
                break;
            case R.id.circular_progress:
                if (wheelProgress.getWheelType() == ProgressWheel.IDEAL_WHEEL) {
                    wheelProgress.setWheelType(ProgressWheel.RECORD_WHEEL);
                    wheelProgress.setProgress(0);
                    startWheel();
                    if (mVoiceMedia != null && mVoiceMedia.getMediaState() == Constants.UI_STATE_IDLE) {
                        mVoiceMedia.startRecording(getString(R.string.done), getString(R.string.cancel), null, Constants.MAX_AUDIO_RECORD_TIME_REST);
                    }
                } else if (wheelProgress.getWheelType() == ProgressWheel.RECORD_WHEEL) {
                    if (wheelTimer != null) {
                        wheelTimer.cancel();
                        if (mVoiceMedia != null)
                            mVoiceMedia.stopRec();
                    }
                    wheelProgress.setWheelType(ProgressWheel.RECORDED_WHEEL);

                } else if (wheelProgress.getWheelType() == ProgressWheel.RECORDED_WHEEL || wheelProgress.getWheelType() == ProgressWheel.PAUSE_WHEEL) {
                    if (wheelProgress.getWheelType() != ProgressWheel.PAUSE_WHEEL) {
                        if (wheelProgress.getProgress() < 4) {
                            wheelProgress.setWheelType(ProgressWheel.IDEAL_WHEEL);
                            setRecordingViews(wheelProgress.getWheelType());
                            wheelProgress.setProgress(0);
                            wheelProgress.refreshWheel(true);
                            Toast.makeText(this, "Minimum 3 seconds voice required.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        wheelProgress.setProgress(0);
                        startWheel();

                    }
                    wheelProgress.setWheelType(ProgressWheel.PLAY_WHEEL);
                    if (mVoiceMedia != null)
                        mVoiceMedia.startPlaying();
                    isPlaying = true;

                } else if (wheelProgress.getWheelType() == ProgressWheel.PLAY_WHEEL) {
                    wheelProgress.setWheelType(ProgressWheel.PAUSE_WHEEL);
                    wheelProgress.setProgress(0);
                    if (mVoiceMedia != null)
                        mVoiceMedia.pause();
                    isPlaying = false;
                }
                setRecordingViews(wheelProgress.getWheelType());
                wheelProgress.refreshWheel(true);
                break;
            case R.id.id_cancel_audio:
                if (wheelProgress != null) {
                    if (mVoiceMedia != null && (wheelProgress.getWheelType() == ProgressWheel.PLAY_WHEEL || wheelProgress.getWheelType() == ProgressWheel.PAUSE_WHEEL))
                        mVoiceMedia.stopVoicePlaying();

                    wheelProgress.setWheelType(ProgressWheel.IDEAL_WHEEL);
                    setRecordingViews(wheelProgress.getWheelType());
                }
                if (voiceRecorderDialog != null && voiceRecorderDialog.isShowing()) {
                    voiceRecorderDialog.cancel();
                }
                if (mVoiceMedia != null) {
                    mVoiceMedia.stopRec();
                    mVoiceMedia.stop();
                }
//                audioRecordLayout.setVisibility(View.INVISIBLE);
//                isInvalidAudio = true;
//                recordTipView.setVisibility(View.INVISIBLE);
//                timeCounterView.setText("00:00");
//                clockTimer.cancel();
//                clockTimer = null;
//                clockTimerTask = null;
//                mVoiceMedia.stopRec();
//                voiceRecorderDialog.cancel();
//                isRecordingStarted = false;
                break;
            case R.id.id_send_audio:
                if (isPlaying) {
                    wheelProgress.setWheelType(ProgressWheel.PAUSE_WHEEL);
                    wheelProgress.setProgress(0);
                    if (mVoiceMedia != null)
                        mVoiceMedia.pause();
                    isPlaying = false;
                } else if (wheelProgress != null) {
                    if (mVoiceMedia != null && (wheelProgress.getWheelType() == ProgressWheel.PLAY_WHEEL || wheelProgress.getWheelType() == ProgressWheel.PAUSE_WHEEL))
                        mVoiceMedia.stopVoicePlaying();
                    wheelProgress.setWheelType(ProgressWheel.IDEAL_WHEEL);
                    setRecordingViews(wheelProgress.getWheelType());
                    if (wheelProgress.getProgress() < 4) {
                        wheelProgress.setProgress(0);
                        wheelProgress.refreshWheel(true);
                        Toast.makeText(this, "Minimum 3 seconds voice required.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }


                if (voiceRecorderDialog != null && voiceRecorderDialog.isShowing()) {
                    voiceRecorderDialog.cancel();
                }
                if (mVoiceMedia != null && mVoiceMedia.getVoicePath() != null) {
//            		sendVoiceMessage(mVoiceMedia.getVoicePath());
                    if (captionDialogNew != null && !captionDialogNew.isShowing()) {
                        mediaUrl = mVoiceMedia.getVoicePath();
                        taggingType = AUDIO_TAGGING;
                        captionDialogNew.show();
                    } else {
                        mediaUrl = mVoiceMedia.getVoicePath();
                        taggingType = AUDIO_TAGGING;
                        captionDialogNew = createCaptionDialogNew();
                        captionDialogNew.show();
                    }
                }
//                audioRecordLayout.setVisibility(View.INVISIBLE);
//                int second = calander.get(Calendar.SECOND);
//                if (second < 2) {
//                    isInvalidAudio = true;
//                } else {
//                    isInvalidAudio = false;
//                }
//                Log.d(TAG, "Clock in Handler int = " + second);
//                recordTipView.setVisibility(View.INVISIBLE);
//                timeCounterView.setText("00:00");
//                clockTimer.cancel();
//                clockTimer = null;
//                clockTimerTask = null;
//                mVoiceMedia.stopRec();
//                voiceRecorderDialog.dismiss();
//                isRecordingStarted = false;
                break;
//            case R.id.id_touch_recorder:
//                if (mVoiceMedia != null && mVoiceMedia.getMediaState() == Constants.UI_STATE_IDLE) {
//                    tapToReordView.setVisibility(View.GONE);
//                    audioRecordLayout.setVisibility(View.VISIBLE);
//                    if (!isRecordingStarted) {
//                        recordTipView.setVisibility(View.GONE);
//                        isRecordingStarted = true;
//                        timeCounterView.setText("00:00");
//                        timerClockStart();
//                    }
//                    mVoiceMedia.startRecording(getString(R.string.done), getString(R.string.cancel), null,
//                            Constants.MAX_AUDIO_RECORD_TIME_REST);
//                }
//                break;
            case R.id.id_cancel_file:
                if (fileConfirmationDialog != null && fileConfirmationDialog.isShowing()) {
                    fileConfirmationDialog.cancel();
                }
                AppUtil.openPdf(this, AppUtil.FILE_PDF_PICKER);
                break;
            case R.id.id_send_file:
                if (fileConfirmationDialog != null && fileConfirmationDialog.isShowing()) {
                    fileConfirmationDialog.cancel();
                }

                if (fileAttachUri != null) {
                    String tmpUri = fileAttachUri.toString();
                    if (tmpUri != null && (tmpUri.startsWith("content://com.google.android.apps"))) {// ||
                        // tmpUri.startsWith("content://com.android.providers.downloads.documents"))){
                        String mimeType = getContentResolver().getType(fileAttachUri);
                        Log.i(TAG, "mimeType : " + mimeType);
                        Cursor returnCursor = getContentResolver().query(fileAttachUri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        Log.i(TAG, "sizeIndex : " + sizeIndex);
                        returnCursor.moveToFirst();
                        String path = returnCursor.getString(nameIndex);
                        Log.i(TAG, "returnCursor.getString(nameIndex) : " + path);
                        if (Build.VERSION.SDK_INT >= 11)
                            new FileFetchTask(fileAttachUri, path).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        else
                            new FileFetchTask(fileAttachUri, path).execute();
                        return;
                    }
                    AppUtil.capturedPath1 = AppUtil.getPath(this, fileAttachUri);
                    if (AppUtil.capturedPath1 != null && (AppUtil.capturedPath1.toLowerCase().endsWith(".docx")
                            || AppUtil.capturedPath1.toLowerCase().endsWith(".doc")))
                        sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeDoc);
                    else if (AppUtil.capturedPath1 != null && (AppUtil.capturedPath1.toLowerCase().endsWith(".xls")
                            || AppUtil.capturedPath1.toLowerCase().endsWith(".xlsx")))
                        sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeXLS);
                    else if (AppUtil.capturedPath1 != null && (AppUtil.capturedPath1.toLowerCase().endsWith(".ppt")
                            || AppUtil.capturedPath1.toLowerCase().endsWith(".pptx")))
                        sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypePPT);
                    else if (AppUtil.capturedPath1 != null
                            && (AppUtil.capturedPath1.toLowerCase().endsWith(".pdf")))
                        sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypePdf);
                    else if (AppUtil.capturedPath1 != null
                            && (AppUtil.capturedPath1.toLowerCase().endsWith(".jpg") || AppUtil.capturedPath1.toLowerCase().endsWith(".jpeg") || AppUtil.capturedPath1.toLowerCase().endsWith(".png"))) {
                        CompressImage compressImage = new CompressImage(this);
                        AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
                        mediaUrl = AppUtil.capturedPath1;
                        // sendPictureMessage(AppUtil.capturedPath1);
                        if (captionDialog != null && !captionDialog.isShowing()) {
                            taggingType = IMAGE_TAGGING;
                            captionDialog.show();
                            Bitmap imageBitmap = BitmapFactory.decodeFile(mediaUrl);
                            mediaPreview.setImageBitmap(imageBitmap);
                            thumbPreview.setImageBitmap(imageBitmap);

                        }
//              	sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeImage);
                    } else if (AppUtil.capturedPath1 != null
                            && (AppUtil.capturedPath1.toLowerCase().endsWith(".mp4") || AppUtil.capturedPath1.toLowerCase().endsWith(".3gp")
                            || AppUtil.capturedPath1.toLowerCase().endsWith(".mov")
                            || AppUtil.capturedPath1.toLowerCase().endsWith(".3gpp"))) {
                        mediaUrl = AppUtil.capturedPath1;//AppUtil.getPath(uri, this);
                        if (captionDialog != null && !captionDialog.isShowing()) {
                            taggingType = VIDEO_TAGGING;
                            captionDialog.show();
                            Bitmap imageBitmap = ThumbnailUtils.createVideoThumbnail(mediaUrl,
                                    MediaStore.Video.Thumbnails.MINI_KIND);
                            mediaPreview.setImageBitmap(imageBitmap);
                            thumbPreview.setImageBitmap(imageBitmap);
//                       sendFile(AppUtil.capturedPath1, XMPPMessageType.atMeXmppMessageTypeVideo);
                        }
                    } else {
                        Toast.makeText(this, "This content is not supported.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "This content is not supported.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private final Handler stopPlayingHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            wheelProgress.setWheelType(ProgressWheel.RECORDED_WHEEL);
            wheelProgress.refreshWheel(true);
            setRecordingViews(ProgressWheel.RECORDED_WHEEL);
        }
    };

    private void setRecordingViews(byte type) {
        switch (type) {
            case ProgressWheel.IDEAL_WHEEL:
                recordInfoView.setText(getString(R.string.tap_and_record_upto_five));
                recordTipView.setText(getString(R.string.tap_to_start));
                timeCounterView.setText("00:00");
                timeCounterView.setTextColor(R.color.gray_dark);
                recordTipView.setVisibility(TextView.VISIBLE);
                rightImageView.setVisibility(ImageView.GONE);
                crossImageView.setVisibility(ImageView.GONE);
                break;
            case ProgressWheel.RECORD_WHEEL:
                timeCounterView.setTextColor(0xfff67f7f);
                recordInfoView.setText(getString(R.string.tap_and_record_upto_five));
                recordTipView.setText(getString(R.string.tap_to_stop));
                recordTipView.setVisibility(TextView.VISIBLE);
                rightImageView.setVisibility(ImageView.GONE);
                crossImageView.setVisibility(ImageView.GONE);
                break;
            case ProgressWheel.RECORDED_WHEEL:
//		timeCounterView.setText("00:00");
            case ProgressWheel.PLAY_WHEEL:
                timeCounterView.setTextColor(0xfff67f7f);
                recordInfoView.setText(getString(R.string.review_and_send));
                recordTipView.setVisibility(TextView.GONE);
                rightImageView.setVisibility(ImageView.VISIBLE);
                crossImageView.setVisibility(ImageView.VISIBLE);
                rightImageView.setOnClickListener(this);
                crossImageView.setOnClickListener(this);
                break;
            case ProgressWheel.PAUSE_WHEEL:
                timeCounterView.setTextColor(0xfff67f7f);
                recordInfoView.setText(getString(R.string.review_and_send));
                recordTipView.setVisibility(TextView.GONE);
                rightImageView.setVisibility(ImageView.VISIBLE);
                crossImageView.setVisibility(ImageView.VISIBLE);
                rightImageView.setOnClickListener(this);
                crossImageView.setOnClickListener(this);
                break;

        }

    }

    public void onCurrentLocationClicked(View view) {
        //Get Current location and pre-fill.
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Location not enabled");
            dialog.setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
            return;
        }

        //Show Location selection Dialog.
        showLocationDialog();
    }

    public void showLocationDialog() {
        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingsDialog.setContentView(getLayoutInflater().inflate(R.layout.location_imageview, null));
        ImageView location_img = ((ImageView) settingsDialog.findViewById(R.id.image_location_view));
        ProgressBar loadingBar = ((ProgressBar) settingsDialog.findViewById(R.id.image_loading_progress));
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        double latitude = 0;
        double longitude = 0;
        if (lm != null) {
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (location == null) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        final double lat = latitude;
        final double lon = longitude;
        final String full_address = getCompleteAddressString(lat, lon);
        if (full_address != null) {
            ((TextView) settingsDialog.findViewById(R.id.address_view)).setText(full_address);
        }

        String mapurl = MAP_URL.replace("$lat", "" + lat);
        mapurl = mapurl.replace("$lon", "" + lon);
        new ImageLoadTask(mapurl, location_img, loadingBar).execute();
//			android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(mapurl);
//			if (bitmap != null) {
//				location_img.setImageBitmap(bitmap);
//			}else{
//				new ImageLoadTask(mapurl, location_img, loadingBar).execute();
//			}
        ((Button) settingsDialog.findViewById(R.id.button_cancel)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                settingsDialog.dismiss();
            }
        });
        ((Button) settingsDialog.findViewById(R.id.button_send)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                settingsDialog.dismiss();
                if (full_address != null)
                    sendLocation(lat + "," + lon, full_address, XMPPMessageType.atMeXmppMessageTypeLocation);
            }
        });
        settingsDialog.show();
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;
        private ProgressBar loadingBar;

        public ImageLoadTask(String url, ImageView imageView, ProgressBar loadingBar) {
            this.url = url;
            this.imageView = imageView;
            this.loadingBar = loadingBar;
        }

        @Override
        protected void onPreExecute() {
            if (loadingBar != null)
                loadingBar.setVisibility(View.VISIBLE);
        }

        ;

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
            if (loadingBar != null)
                loadingBar.setVisibility(View.GONE);
            imageView.setImageBitmap(result);
            if (url != null) {
                if (result != null)
                    SuperChatApplication.addBitmapToMemoryCache(url, result);
//	            processingMap.put(url, null);
            }
        }

    }

    public String getAddress(final double latitude, final double longitude) {
        String my_address = "";
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
        }
        // Handle case where no address was found.
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            my_address = TextUtils.join(System.getProperty("line.separator"), addressFragments);
        }
        return my_address;
    }

    @Override
    public void onChatEditEnable(String copyVisible) {
        // TODO Auto-generated method stub
        if (isBulletinBroadcast && chatAdapter != null)// && chatAdapter.getCount() == 1)
            return;
        chatList.setFastScrollEnabled(false);
        mainHeaderLayout.setVisibility(View.GONE);
        editHeaderLayout.setVisibility(View.VISIBLE);
        if (copyVisible != null && copyVisible.equals("Y")) {
            chatCopyIv.setVisibility(View.VISIBLE);
        } else {
            chatCopyIv.setVisibility(View.GONE);
        }

        ViewTreeObserver observer = editHeaderLayout.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                editHeaderLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                buttonLayoutHeight = editHeaderLayout.getHeight();
            }
        });

        chatAdapter.setEditableChat(true);
        if (willMyListScroll()) {
            // Do something
            // Log.d(TAG, "LIST WILL SCROLL");
            chatList.setScrollY(buttonLayoutHeight);
        } else {
            // Log.d(TAG, "LIST not SCROLL");
        }
        chatList.setSelection(chatAdapter.getCount() - 1);
        chatAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> getSelectedChat() {
        chatList.setFastScrollEnabled(true);
        chatAdapter.setEditableChat(false);
        chatAdapter.notifyDataSetChanged();
        editHeaderLayout.setVisibility(View.GONE);
        mainHeaderLayout.setVisibility(View.VISIBLE);

        HashMap<String, Boolean> hm = selectedTagMap;//chatAdapter.getSelectedItems();
        ArrayList<String> list = new ArrayList<String>();
        for (String key : hm.keySet()) {
            if (hm.get(key) != null && (boolean) hm.get(key))
                list.add(key);
        }
        chatList.setScrollY(0);
        isLastIndex = false;
        return list;
    }

    private boolean willMyListScroll() {
        if (isLastIndex) {
            return true;
        }
        if (chatList.getLastVisiblePosition() + 1 == chatList.getCount()) {
            return false;
        }
        return true;
    }

    @Override
    public void notifyConnectionChange() {
        if (onForeground) {
            notifyConnectionChangeHandler.sendEmptyMessage(0);
//            System.out.print("==========XMPP Connected======");
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (ChatService.xmppConectionStatus) {
                        if (networkConnection != null)
                            networkConnection.setVisibility(View.GONE);
                        if (xmppStatusView != null)
                            xmppStatusView.setImageResource(R.drawable.blue_dot);
//	            	 if(bottomPanel != null)
//	            		 bottomPanel.setVisibility(View.VISIBLE);
                    } else {
                        if (networkConnection != null)
                            networkConnection.setVisibility(View.VISIBLE);
                        if (xmppStatusView != null)
                            xmppStatusView.setImageResource(R.drawable.red_dot);
//	            	 if(bottomPanel != null)
//	            		 bottomPanel.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    private final Handler notifyConnectionChangeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (ChatService.xmppConectionStatus) {
                networkConnection.setVisibility(View.GONE);
                xmppStatusView.setImageResource(R.drawable.blue_dot);
            } else {
                networkConnection.setVisibility(View.VISIBLE);
                xmppStatusView.setImageResource(R.drawable.red_dot);
            }

        }
    };
    // @Override
    // public void onMapReady(GoogleMap map) {
    // LatLng sydney = new LatLng(-33.867, 151.206);
    //
    // map.setMyLocationEnabled(true);
    // map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
    //
    // map.addMarker(new MarkerOptions()
    // .title("Sydney")
    // .snippet("The most populous city in Australia.")
    // .position(sydney));
    // }

    @Override
    public void notifyProfileUpdate(String userName) {
        // TODO Auto-generated method stub
        if (this.userName.equalsIgnoreCase(userName) && onForeground) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    setProfilePic(userIcon, userIconDefault, contactNameTxt);
                }
            });
        }
    }


    @Override
    public void notifyProfileUpdate(String userName, String status) {
        // TODO Auto-generated method stub

    }


    @Override
    public void notifyProfileUpdate(final String userName, final String status, final String userDisplayName) {
        // TODO Auto-generated method stub
        if (this.userName.equalsIgnoreCase(userName) && onForeground) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (userDisplayName != null && windowNameView != null)
                        windowNameView.setText(userDisplayName);
                    setProfilePic(userIcon, userIconDefault, userDisplayName);
                }
            });
        }
    }

    public void showDialog(final String title, final String s) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.custom_dialog_two_button);
        if (title != null) {
            ((TextView) bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
        }
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_send)).setText("Ok");
        ((TextView) bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();

                if (!iChatPref.isGroupChat(userName) && !iChatPref.isBroadCast(userName)) {
                    SharedPrefManager prefObj = SharedPrefManager.getInstance();
                    prefObj.saveChatCountOfUser(userName, 0);
                    ChatDBWrapper.getInstance().deleteRecentUserChatByUserName(userName);
                } else {
                    ChatDBWrapper.getInstance().deleteRecentUserChatByUserName(userName);
//					 saveMessage(title, userName,s);
                    saveInfoMessage(title, userName, getString(R.string.msgs_cleared), UUID.randomUUID().toString());
                }
                if (chatAdapter != null)
                    notifyChatRecieveHandler1.sendEmptyMessage(0);
//				finish();
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

    public void saveMessage(String displayName, String from, String msg) {
        try {
            ChatDBWrapper chatDBWrapper = ChatDBWrapper.getInstance();
            ContentValues contentvalues = new ContentValues();
            String myName = SharedPrefManager.getInstance().getUserName();
            contentvalues.put(DatabaseConstants.FROM_USER_FIELD, from);
            contentvalues.put(DatabaseConstants.TO_USER_FIELD, myName);
            contentvalues.put(DatabaseConstants.UNREAD_COUNT_FIELD,
                    new Integer(1));
            contentvalues.put(DatabaseConstants.FROM_GROUP_USER_FIELD, "");
            contentvalues.put(DatabaseConstants.SEEN_FIELD, com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState.sent.ordinal());

            contentvalues.put(DatabaseConstants.MESSAGEINFO_FIELD, msg);
            // String name =
            // cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));

            String name = "";
            String oppName = "";
            {
                oppName = from;
                name = chatDBWrapper.getChatName(from);
                contentvalues.put(DatabaseConstants.MESSAGE_ID, UUID
                        .randomUUID().toString());
                contentvalues.put(DatabaseConstants.FOREIGN_MESSAGE_ID_FIELD,
                        UUID.randomUUID().toString());
            }

            long currentTime = System.currentTimeMillis();
            Calendar calender = Calendar.getInstance();
            calender.setTimeInMillis(currentTime);
            int date = calender.get(Calendar.DATE);
            int oldDate = date;
            long milis = ChatDBWrapper.getInstance().lastMessageInDB(oppName);
            if (milis != -1) {
                calender.setTimeInMillis(milis);
                oldDate = calender.get(Calendar.DATE);
            }
            if ((oldDate != date)
                    || ChatDBWrapper.getInstance().isFirstChat(oppName)) {
                contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "1");
            } else {
                contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "0");
            }
//			AtMeApplication.dayValue = date;
            contentvalues.put(DatabaseConstants.LAST_UPDATE_FIELD, currentTime);

            contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, name);
            //Save USerID and SG in DB
            contentvalues.put(DatabaseConstants.USER_ID, SharedPrefManager.getInstance().getUserId());
            contentvalues.put(DatabaseConstants.USER_SG, SharedPrefManager.getInstance().getUserDomain());
            chatDBWrapper.insertInDB(DatabaseConstants.TABLE_NAME_MESSAGE_INFO, contentvalues);
        } catch (Exception e) {

        }
    }

    public void saveInfoMessage(String displayName, String from, String msg, String msgId) {
        if (iChatPref.isGroupChat(from) && !iChatPref.isGroupMemberActive(from, iChatPref.getUserName())) {
            return;
        }
        try {
//			ChatDBWrapper chatDBWrapper = chatDBWrapper;
            ContentValues contentvalues = new ContentValues();
            String myName = SharedPrefManager.getInstance().getUserName();
            contentvalues.put(ChatDBConstants.FROM_USER_FIELD, from);
            contentvalues.put(ChatDBConstants.TO_USER_FIELD, myName);
            contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD,
                    new Integer(1));
            contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, "");
            contentvalues.put(ChatDBConstants.SEEN_FIELD,
                    SeenState.sent.ordinal());
//			 if(msg!=null && msg.contains("#786#")){
//				 msg = msg.replace("#786#"+from,"");
//				 msg = msg.replace("#786#"+myName,"");
//				}
            contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, msg);
            // String name =
            // cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));

            String name = "";
            String oppName = "";
            {
                oppName = from;
                name = DBWrapper.getInstance().getChatName(from);
                if (name != null && name.equals(from))
                    name = displayName + "#786#" + from;
                contentvalues.put(ChatDBConstants.MESSAGE_ID, UUID
                        .randomUUID().toString());
                if (msgId == null)
                    msgId = UUID.randomUUID().toString();
                contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD, msgId);
//						UUID.randomUUID().toString());
            }

            long currentTime = System.currentTimeMillis();
            Calendar calender = Calendar.getInstance();
            calender.setTimeInMillis(currentTime);
            int date = calender.get(Calendar.DATE);
//			int oldDate = date;
//			long milis = chatDBWrapper.lastMessageInDB(oppName);
//			if(milis!=-1){
//				calender.setTimeInMillis(milis);
//				oldDate = calender.get(Calendar.DATE);
//			}
//			if ((oldDate != date)
//					|| chatDBWrapper.isFirstChat(oppName)) {
//				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "1");
//			} else {
            contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, "0");
//			}
//			AtMeApplication.dayValue = date;
            contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, currentTime);

            contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, name);
            //Save USerID and SG in DB
            contentvalues.put(DatabaseConstants.USER_ID, SharedPrefManager.getInstance().getUserId());
            contentvalues.put(DatabaseConstants.USER_SG, SharedPrefManager.getInstance().getUserDomain());
            ChatDBWrapper.getInstance().insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO, contentvalues);
//			if (chatListener != null)
//				chatListener.notifyChatRecieve(from,msg);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
        // TODO Auto-generated method stub
//		SparseBooleanArray selected = chatAdapter.getSelectedItems();
//		chatAdapter.getSelectedItems();
        if (arg1.getItemId() == R.id._exit) {
            arg0.finish();
        }
        return false;
    }

    LinearLayout rlLayout;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//		View view = mode.getCustomView();
        View customNav = LayoutInflater.from(ChatListScreen.this).inflate(R.layout.action_mode_layout, null);

        if (customNav != null) {
            mode.setCustomView(customNav);
        }
        selectedTagMap.clear();
        checkSenderTagMap.clear();
        checkTextMap.clear();
        mainHeaderLayout.setVisibility(View.GONE);
        if (chatAdapter != null)
            chatAdapter.setEditableChat(true);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mainHeaderLayout.setVisibility(View.VISIBLE);
        if (chatAdapter != null)
            chatAdapter.setEditableChat(false);
        checkSenderTagMap.clear();
        checkTextMap.clear();
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        return false;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        final int checkedCount = chatList.getCheckedItemCount();
//        Cursor cursor = (Cursor) chatAdapter.getItem(position);
        Cursor cursor = null;
        if (isBulletinBroadcast)
            cursor = chatAdapter.getCursor();
        else
            cursor = (Cursor) chatAdapter.getItem(position);
        if (cursor.isClosed())
            cursor = (Cursor) chatAdapter.getItem(position);
        boolean isOwnSentMessage = false;
        boolean isAllTextMessages = true;
        if (cursor != null) {
            String key = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID));
            String messageSenderUserName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD));
            int messageType = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD));
            if (messageType == XMPPMessageType.atMeXmppMessageTypeNormal.ordinal() && checked) {
                String groupMsgSenderName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD));
                boolean isDateShow = "1".equals(cursor.getString(cursor.getColumnIndex(ChatDBConstants.IS_DATE_CHANGED_FIELD)));
                if (!isDateShow && groupMsgSenderName != null && groupMsgSenderName.equals("") && !messageSenderUserName.equals(iChatPref.getUserName()) && (iChatPref.isGroupChat(userName) || iChatPref.isBroadCast(userName))) {
                    chatList.setItemChecked(position, false);
                }
            }
            if (key != null) {
                selectedTagMap.put(key, checked);
                if (messageSenderUserName != null && messageSenderUserName.equals(iChatPref.getUserName()))
                    checkSenderTagMap.put(key, true);
                else
                    checkSenderTagMap.put(key, false);
            }
            if (checkedCount == 1) {
                String selectedMessageId = null;
                for (String tKey : selectedTagMap.keySet()) {
                    if (selectedTagMap.get(tKey)) {
                        selectedMessageId = tKey;
                        break;
                    }
                }
                if (selectedMessageId != null && checkSenderTagMap.get(selectedMessageId))
                    isOwnSentMessage = true;
            }

            if (messageType == XMPPMessageType.atMeXmppMessageTypeNormal.ordinal())
                checkTextMap.put(key, true);
            else
                checkTextMap.put(key, !checked);

            for (String tKey : checkTextMap.keySet()) {
                if (!checkTextMap.get(tKey)) {
                    isAllTextMessages = false;
                    break;
                }
            }
        }
        mode.setTitle(checkedCount + " Selected");
        View view = mode.getCustomView();
        if (view != null) {
            TextView titleView = (TextView) ((view).findViewById(R.id.id_selector_count));
            ImageView infoView = (ImageView) ((view).findViewById(R.id.id_info_iv));
            ImageView deleteView = (ImageView) ((view).findViewById(R.id.id_delete_iv));
            ImageView copyView = (ImageView) ((view).findViewById(R.id.id_copy_iv));
            if (titleView != null)
                titleView.setText(checkedCount + " Selected");
            infoView.setOnClickListener(this);
            deleteView.setOnClickListener(this);
            copyView.setOnClickListener(this);
            if (isAllTextMessages)
                copyView.setVisibility(View.VISIBLE);
            else
                copyView.setVisibility(View.GONE);

            if (checkedCount == 1 && !isBulletinBroadcast && !iChatPref.isBroadCast(userName) & isOwnSentMessage)
                infoView.setVisibility(View.VISIBLE);
            else
                infoView.setVisibility(View.GONE);
        }

/*
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        System.out.println(stackTraceElements.toString());
        System.out.println(stackTraceElements.toString());*/
    }

    //-------------------------------------------------------------------
    private void getBulletinMessages(final String url, final View header) {
        try {
//            final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
//            progressDialog.show();
            retrofit2.Call call = null;
            if (url != null && url.trim().length() > 0) {
                System.out.println("ChatListScreen: [Hitting next url]");
                call = objApi.getApi(this).getMoreMessages(url);
            } else {
                System.out.println("ChatListScreen: [Hitting first time]");
                call = objApi.getApi(this).getBulletinMessages("" + 10);
            }
            (header.findViewById(R.id.load_older_messages)).setVisibility(View.GONE);
            (header.findViewById(R.id.message_loading)).setVisibility(View.VISIBLE);
            call.enqueue(new RetrofitRetrofitCallback<BulletinGetMessageDataModel>(this) {
                @Override
                protected void onResponseVoidzResponse(retrofit2.Call call, Response response) {
                    System.out.println("ChatListScreen: [Here.....]");
                }

                @Override
                protected void onResponseVoidzObject(retrofit2.Call call, BulletinGetMessageDataModel response) {
                    if (response != null && response.getStatus() != null && response.getStatus().equalsIgnoreCase("success")) {
                        Set<BulletinGetMessageDataModel.MessageData> messages = response.getBulletinMessageAPIList();
                        String next_url = null;
                        String json_body = null;
                        String media_url = null;
                        int type = 0;
                        String caption = null;
                        next_url = response.getNextUrl();
                        if (!messages.isEmpty()) {
                            LinkedList<BulletinGetMessageDataModel.MessageData> list = new LinkedList<BulletinGetMessageDataModel.MessageData>(messages);
                            Iterator<BulletinGetMessageDataModel.MessageData> itr = list.descendingIterator();
                            while (itr.hasNext()) {
                                BulletinGetMessageDataModel.MessageData message = itr.next();
                                json_body = message.getJsonBody();
                                ContentValues contentvalues = new ContentValues();
                                contentvalues.put(ChatDBConstants.FROM_USER_FIELD, message.getSender());
                                contentvalues.put(ChatDBConstants.TO_USER_FIELD, iChatPref.getUserDomain() + "-all");
                                contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, message.getSenderName() + "#786#" + message.getSender());
                                contentvalues.put(ChatDBConstants.MESSAGE_TYPE, 3);//3 - For all Bulletin Messages
                                contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, iChatPref.getUserDomain() + "-all");
                                contentvalues.put(ChatDBConstants.SEEN_FIELD, "1");
                                contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, (message.getText() != null) ? message.getText() : "");
                                contentvalues.put(ChatDBConstants.MESSAGE_ID, message.getPacketId());
                                contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD, UUID.randomUUID().toString());
//                                System.out.println("ChatListScreen: [Creaton Date ] " + message.getCreatedDate());

                                Calendar calender = Calendar.getInstance();
                                calender.setTimeInMillis(convertTomilliseconds(message.getCreatedDate()));
                                int old_msg_date = calender.get(Calendar.DATE);

                                String oppName = message.getSender();
                                long millis = ChatDBWrapper.getInstance().firstMessageInDB(oppName);
                                if (millis != -1) {
                                    calender.setTimeInMillis(millis);
                                }
                                int new_msg_date = calender.get(Calendar.DATE);

                                if (old_msg_date < new_msg_date) {
                                    contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "1");
                                } else {
                                    contentvalues.put(DatabaseConstants.IS_DATE_CHANGED_FIELD, "0");
                                }

                                contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, convertTomilliseconds(message.getCreatedDate()));
                                if (message.getType() != null) {
                                    try {
                                        type = Integer.parseInt(message.getType());
                                    } catch (NumberFormatException nex) {
                                        nex.printStackTrace();
                                        type = 0;
                                    }
                                }
                                contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message.getType());
                                contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD, new Integer(1));
                                media_url = message.getFileId();
                                if (media_url != null && media_url.length() > 0)
                                    media_url = Constants.LIVE_DOMAIN + "/rtMediaServer/get/" + media_url;
                                if (json_body != null && json_body.trim().length() > 0) {
//                                    System.out.println("json_body = " + json_body);
                                    JSONObject jsonobj = null;
                                    try {
                                        jsonobj = new JSONObject(json_body);
                                        if (jsonobj.has("caption") && jsonobj.getString("caption").toString().trim().length() > 0)
                                            caption = jsonobj.getString("caption").toString();
//                                        if((type == XMPPMessageType.atMeXmppMessageTypeImage.ordinal()
//                                                || type == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()
//                                                || type == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()) && caption != null)
                                        contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, caption);

                                        if (jsonobj.has("fileName") && jsonobj.getString("fileName").toString().trim().length() > 0)
                                            contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, jsonobj.getString("fileName").toString());
                                        if (jsonobj.has("ext") && jsonobj.getString("ext").toString().trim().length() > 0)
                                            media_url = media_url + "." + jsonobj.getString("ext").toString().trim();

                                        if (jsonobj.has("location") && jsonobj.getString("location").toString().trim().length() > 0)
                                            contentvalues.put(ChatDBConstants.MESSAGE_TYPE_LOCATION, jsonobj.getString("location").toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, media_url);
                                //Save USerID and SG in DB
                                contentvalues.put(ChatDBConstants.USER_ID, iChatPref.getUserId());
                                contentvalues.put(ChatDBConstants.USER_SG, iChatPref.getUserDomain());
                                ChatDBWrapper.getInstance().insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO, contentvalues);
                                media_url = caption = null;
                                json_body = null;
                                type = 0;
                            }
                            if (chatAdapter != null) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        chatAdapter.updateList();
                                    }
                                });
                            }
                        }
                        if (next_url != null) {
                            //Save this url is shared preferences for next hit
                            iChatPref.saveBulletinNextURL(iChatPref.getUserDomain(), next_url);
                            next_url = null;
                            (header.findViewById(R.id.load_older_messages)).setVisibility(View.VISIBLE);
                            (header.findViewById(R.id.message_loading)).setVisibility(View.GONE);
                        } else {
                            iChatPref.saveBulletinNextURL(iChatPref.getUserDomain(), "0");
                            (header.findViewById(R.id.load_older_messages)).setVisibility(View.GONE);
                            (header.findViewById(R.id.message_loading)).setVisibility(View.GONE);
                            header.setVisibility(View.GONE);
                        }

                    } else {
                        String errorMessage = response.getMessage() != null ? response.getMessage() : "Please try later";
                        showDialog(errorMessage);
                    }
                }

                @Override
                protected void common() {
//                    progressDialog.cancel();
                }

                @Override
                public void onFailure(retrofit2.Call call, Throwable t) {
                    super.onFailure(call, t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--------------------------------
    public long convertTomilliseconds(Date date) {
        long time_millis = 0;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"),
                Locale.getDefault());
        Date currentLocalTime = calendar.getTime();
//            System.out.println("GMT offset is "+currentLocalTime.getTime());
        DateFormat datef = new SimpleDateFormat("Z");
        String localTime = datef.format(currentLocalTime);
        System.out.println("GMT offset is " + localTime);

        time_millis = date.getTime();
        return time_millis;
    }

    public long convertTomilliseconds(String date) {
        long timeInMilliseconds = 0;
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.US);
        try {
            Date mDate = sdf.parse(date);
            timeInMilliseconds = mDate.getTime();
//			System.out.println("Before : Date in millis => " + timeInMilliseconds);
//
//			Date resultdate = new Date(timeInMilliseconds);
//			System.out.println("Before : Date => "+sdf.format(resultdate));
//
//			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
//			Date currentLocalTime = calendar.getTime();
//
//			System.out.println("After GTM : Date in millis => " + (timeInMilliseconds + currentLocalTime.getTime()));
//
//			resultdate = new Date(timeInMilliseconds + currentLocalTime.getTime());
//			System.out.println("After GMT : Date => "+sdf.format(resultdate));

//			return timeInMilliseconds + convertCurrentTimeintoMillis();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return timeInMilliseconds + convertCurrentTimeintoMillis();
    }

    public long convertCurrentTimeintoMillis() {
        //+05:30
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        String timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
        String time = timeZone.substring(0, 3) + ":" + timeZone.substring(3, 5);
        if (time == null)
            return 0;
        long millis = 0;
        String hr = null;
        String min = null;
        String[] values = null;

        if (time.startsWith("-")) {
            time = time.substring(1);
            values = time.split(":");
            millis = -1 * ((Integer.parseInt(values[0]) * 60 * 60 * 1000) + (Integer.parseInt(values[1]) * 60 * 1000));

        } else {
            time = time.substring(1);
            values = time.split(":");
            millis = (Integer.parseInt(values[0]) * 60 * 60 * 1000) + (Integer.parseInt(values[1]) * 60 * 1000);
        }
//        System.out.println("+05:30 Millis = " + millis);
        return millis;
    }


    /**
     * Munish Code - Animation Menu like Whatsapp
     */

    public void revealMenu() {
        try {
            menuOpen = true;
            if (mRevealView != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    revealMenuBelow21();
                } else {
                    revealMenuAbove21();
                }
            }
        } catch (Exception e) {

        }
    }

    public void hideMenu() {
        try {
            menuOpen = false;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                hideMenuBelow21();
            } else {
                hideMenuAbove21();
            }
        } catch (Exception e) {

        }
    }

    private final int animationTimeInMilliseconds = 400;
    SupportAnimator animatorBelow21;
    SupportAnimator animatorBelow21_reverse;
    Animator animatorAbove21;

    @TargetApi(21)
    public void revealMenuAbove21() {

        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        int cy = mRevealView.getTop();
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        animatorAbove21 =
                android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        animatorAbove21.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorAbove21.setDuration(animationTimeInMilliseconds);

        mRevealView.setVisibility(View.VISIBLE);
        animatorAbove21.start();
    }

    public void revealMenuBelow21() {

        int cx = (mRevealView.getLeft() + mRevealView.getRight());
        int cy = mRevealView.getTop();
        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        animatorBelow21 =
                ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        animatorBelow21.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorBelow21.setDuration(animationTimeInMilliseconds);

        mRevealView.setVisibility(View.VISIBLE);
        animatorBelow21.start();

    }

    @TargetApi(21)
    public void hideMenuAbove21() {
        if (animatorAbove21 != null && !animatorAbove21.isRunning()) {
            int reverse_startradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
            int reverse_endradius = 0;

            int cx = (mRevealView.getLeft() + mRevealView.getRight());
            int cy = mRevealView.getTop();


            animatorAbove21 =
                    android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, reverse_startradius, reverse_endradius);

            animatorAbove21.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animator) {
                    System.out.print("onAnimationStart");
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    System.out.print("onAnimationEnd");
                    mRevealView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorAbove21.start();
        }
    }

    public void hideMenuBelow21() {
        if (animatorBelow21 != null && !animatorBelow21.isRunning()) {
            animatorBelow21_reverse = animatorBelow21.reverse();
            animatorBelow21_reverse.addListener(new SupportAnimator.AnimatorListener() {

                @Override
                public void onAnimationStart() {
                    System.out.print("onAnimationStart");
                }

                @Override
                public void onAnimationEnd() {
                    System.out.print("onAnimationEnd");
                    mRevealView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel() {
                    System.out.print("onAnimationCancel");
                }

                @Override
                public void onAnimationRepeat() {
                    System.out.print("onAnimationRepeat");
                }
            });
            animatorBelow21_reverse.start();
        }
    }

    //-------------------------------------------------------
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    // Called in Android UI's main thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(String message) {
        showDialogWithPositive(message);
//        showAlertDialog(message);
    }

    Dialog systemMessageDialog;

    public void showDialogWithPositive(String s) {
        if (systemMessageDialog != null)
            systemMessageDialog.dismiss();
        systemMessageDialog = new Dialog(ChatListScreen.this);
        try {
            systemMessageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            systemMessageDialog.setCanceledOnTouchOutside(false);
//            dailog.setContentView(R.layout.custom_dialog);
            systemMessageDialog.setContentView(R.layout.system_message_dialog);
            ((TextView) systemMessageDialog.findViewById(R.id.id_dialog_message)).setText(s);
            ((TextView) systemMessageDialog.findViewById(R.id.id_ok)).setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    systemMessageDialog.cancel();
                    return false;
                }
            });
            systemMessageDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // Munish Code

    private void setUIChat(boolean isBroadcastScreenMode) {
        try {
            if(isBroadcastScreenMode){
                ((RelativeLayout) findViewById(R.id.bottom_write_bar1)).setVisibility(View.GONE);
                attachMediaView.setVisibility(View.GONE);
                return;
            } else {
                ((RelativeLayout) findViewById(R.id.bottom_write_bar1)).setVisibility(View.VISIBLE);
                attachMediaView.setVisibility(View.VISIBLE);
            }

            if ((iChatPref.isGroupChat(userName) && !iChatPref.isGroupMemberActive(userName, iChatPref.getUserName()))) {
                ((RelativeLayout) findViewById(R.id.bottom_write_bar1)).setVisibility(View.GONE);
                attachMediaView.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }

    }

    private void setUIBroascastMode(boolean isBroadcastScreenMode) {
        try {
            if (isBroadcastScreenMode) {
                chatOptions.setVisibility(View.GONE);
            } else {
                chatOptions.setVisibility(View.VISIBLE);
            }

            setUIChat(isBroadcastScreenMode);
        } catch (Exception e) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setBroadCastScreenMode(SuperGroupSubAdminStatusChange superGroupSubAdminStatusChange) {

        String groupName = userName;

        GroupChatMetaInfo groupChatMetaInfo = iChatPref.getSubGroupMetaData(groupName);
        if(groupChatMetaInfo != null && groupChatMetaInfo.isBroadCastActive()) {
            if (iChatPref.isGroupChat(userName) && !isBulletinBroadcast) {
                if (iChatPref.isDomainAdminORSubAdmin() || isSharedIDAdmin) {
                    Toast.makeText(this, "You are NOW admin or Subadmin", Toast.LENGTH_SHORT).show();
                    setUIBroascastMode(false);
                } else {
                    Toast.makeText(this, "You are NOT authorized to edit", Toast.LENGTH_SHORT).show();
                    setUIBroascastMode(true);
                }
            } else {
                Toast.makeText(this, "This is not group :-P", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
