package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatConnectListener;
import com.chat.sdk.ChatService;
import com.chat.sdk.ConnectionStatusListener;
import com.chat.sdk.ProfileUpdateListener;
import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.AddMemberModel;
import com.superchat.model.AddMemberResponseModel;
import com.superchat.model.ErrorModel;
import com.superchat.model.GroupChatServerModel;
import com.superchat.model.GroupResopnseModel;
import com.superchat.model.LoginResponseModel;
import com.superchat.model.LoginResponseModel.UserResponseDetail;
import com.superchat.ui.GroupRoleCreationAdapter.UserInfo;
import com.superchat.utils.Constants;
import com.superchat.utils.GroupCreateTaskOnServer;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EsiaChatContactsScreen extends Activity implements OnClickListener,
        ChatConnectListener, ConnectionStatusListener, ProfileUpdateListener {
    private static final String TAG = "EsiaChatContactsScreen";
    public static byte SCREEN_TYPE = 0;
    EsiaChatContactsAdapter adapter;
    LinearLayout multiOptionTitle;
    ArrayList<String> previousUsersList = new ArrayList<String>();
    Cursor cursor;
    ListView contactList;
    TextView createGroupView;
    TextView sendBroadCastView;
    public TextView itemCountView;
    public TextView allSelectTextView;
    CheckBox allSelectCheckBox;
    ImageView clearSearch;
    ImageView backArrowView;
    ImageView plusView;
    EditText searchBoxView;
    TextView titleView;
    //	TextView backTitleView;
    TextView doneView;
    EditText addGroupUserBoxView;
    ArrayList<String> inviters;
    ArrayList<String> allUsers = null;
    ArrayList<GroupRoleCreationAdapter.UserInfo> allUsersWithRole = null;
    String groupName;
    String groupDiscription;
    String groupType;
    boolean isChannel;
    boolean isLoading = false;
    boolean onForeground = false;
    boolean isGroupInvitation = false;
    boolean isBroadcast = false;
    boolean isSharedIDCreate = false;
    boolean isSharedIDUpdate = false;
    String groupFileId;
    public ChatService service;
    private XMPPConnection connection;

    //For Invite member
    public static boolean invitationPending;
    public static String inviteRoomName = null;
    public static String inviteDisplayName = null;
    public static List<String> inviteUsersList = null;
    public static ChatService inviteService = null;

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            service = ((ChatService.MyBinder) binder).getService();
            Log.d("Service", "Connected");
            connection = service.getconnection();
            if (service != null) {
                service.setChatVisibilty(onForeground);
                service.setProfileUpdateListener(EsiaChatContactsScreen.this);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            connection = null;
            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.esia_chat_contact_screen);

        createGroupView = (TextView) findViewById(R.id.id_create_group);
        sendBroadCastView = (TextView) findViewById(R.id.id_broadcast_message);
        itemCountView = (TextView) findViewById(R.id.id_items_selected);
        allSelectTextView = (TextView) findViewById(R.id.id_all_selected);
        allSelectCheckBox = (CheckBox) findViewById(R.id.id_all_sel_box);
        searchBoxView = (EditText) findViewById(R.id.id_search_user);
        clearSearch = (ImageView) findViewById(R.id.id_search_cross);
        plusView = (ImageView) findViewById(R.id.id_plus);
        backArrowView = (ImageView) findViewById(R.id.id_back_arrow);
        titleView = (TextView) findViewById(R.id.id_contact_detail_title);
//		backTitleView = (TextView) findViewById(R.id.id_back_title);
        doneView = (TextView) findViewById(R.id.id_done);
//		backTitleView.setText(getString(R.string.small_chat));
        contactList = (ListView) findViewById(R.id.id_list_view);
        addGroupUserBoxView = (EditText) findViewById(R.id.id_search_add);
        multiOptionTitle = (LinearLayout) findViewById(R.id.id_multiselect_option);
        multiOptionTitle.setVisibility(View.GONE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            SCREEN_TYPE = extras.getByte(Constants.CHAT_TYPE, Constants.NARMAL_CHAT);
            groupFileId = extras.getString(Constants.GROUP_FILE_ID, null);
            previousUsersList = extras.getStringArrayList(Constants.GROUP_USERS);
            if (previousUsersList == null)
                previousUsersList = new ArrayList<String>();
            previousUsersList.add(SharedPrefManager.getInstance().getUserName());
            groupName = extras.getString(Constants.GROUP_NAME, "");
            groupType = extras.getString(Constants.GROUP_TYPE, "");
            isBroadcast = extras.getBoolean(Constants.BROADCAST, false);
            isSharedIDCreate = extras.getBoolean(Constants.SHARED_ID, false);
            isSharedIDUpdate = extras.getBoolean(Constants.SHAREDID_UPDATE, false);
            if (isBroadcast)
                groupDiscription = extras.getString(Constants.GROUP_DISCRIPTION, "Welcome to SuperChat broadcast " + groupName + ".");
            else if (isSharedIDCreate || isSharedIDUpdate) {
                allSelectCheckBox.setVisibility(View.GONE);
            } else {
                isChannel = extras.getBoolean(Constants.CHANNEL_CREATION, false);
                groupDiscription = extras.getString(Constants.GROUP_DISCRIPTION, "Welcome to SuperChat group " + groupName + ".");
            }
            isGroupInvitation = extras.getBoolean(Constants.IS_GROUP_INVITATION, false);

        } else
            Log.d(TAG, "Bundle is null in Oncreate of EsiaChatContactsScreen");
        if (((isGroupInvitation || SCREEN_TYPE == Constants.GROUP_USER_CHAT_CREATE) && !SharedPrefManager.getInstance().isDomainAdmin())) {
            isSharedIDUpdate = true;
            initOrCreateAdapter(SCREEN_TYPE);
            isSharedIDUpdate = false;
        } else if (isGroupInvitation || isChannel || isBroadcast || SCREEN_TYPE == Constants.MEMBER_DELETE) {
            if (SuperChatApplication.isNetworkConnected()) {
                if (Build.VERSION.SDK_INT >= 11)
                    new GetAllSGUserList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    new GetAllSGUserList().execute();
            } else {
                finish();
                Toast.makeText(EsiaChatContactsScreen.this, getString(R.string.check_net_connection), Toast.LENGTH_SHORT).show();
            }
        } else
            initOrCreateAdapter(SCREEN_TYPE);
        setScreen(SCREEN_TYPE);
        if (SCREEN_TYPE == Constants.MEMBER_DELETE)
            titleView.setText(getString(R.string.manage_members));
        else if (isBroadcast) {
            titleView.setText(getString(R.string.add_member));//titleView.setText(getString(R.string.broadcast));
        } else if (isChannel || SharedPrefManager.getInstance().isPublicGroup(groupName)) {
//			if(SharedPrefManager.getInstance().isPublicGroup(groupName))
//				titleView.setText(getString(R.string.edit_channel_title));
//			else
//				titleView.setText(getString(R.string.create_channel));
            titleView.setText(getString(R.string.add_member));
        } else if (isSharedIDCreate) {
            titleView.setText(groupName);
//			titleView.setText(getString(R.string.shared_id) + ": " + groupName);
        } else if (isSharedIDUpdate) {
            titleView.setText(SharedPrefManager.getInstance().getSharedIDDisplayName(groupName));
//			titleView.setText(getString(R.string.shared_id) + ": " + SharedPrefManager.getInstance().getSharedIDDisplayName(groupName));
        } else {
//			if(SharedPrefManager.getInstance().isGroupChat(groupName))
//				titleView.setText(getString(R.string.edit_group_title));
//			else
//				titleView.setText(getString(R.string.create_group));
            titleView.setText(getString(R.string.add_member));
        }
        allSelectCheckBox.setOnClickListener(this);
        allSelectTextView.setOnClickListener(this);
//		createGroupView.setOnClickListener(this);
        sendBroadCastView.setOnClickListener(this);
        clearSearch.setOnClickListener(this);
        doneView.setOnClickListener(this);
        plusView.setOnClickListener(this);
        searchBoxView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable editable) {
                String s1 = (new StringBuilder()).append(searchBoxView.getText().toString()).toString();
                if (SCREEN_TYPE == Constants.GROUP_USERS_ROLE_SELECTION) { // && !isGroupInvitation
                    if (allUsersWithRole == null)
                        return;
                    boolean isAllShown = false;
                    ArrayList<UserInfo> listClone = new ArrayList<UserInfo>();
                    if (s1 != null && !s1.trim().equals("")) {
                        for (GroupRoleCreationAdapter.UserInfo string : allUsersWithRole) {
                            String tmpSearch = SharedPrefManager.getInstance().getUserServerDisplayName(string.userName).toLowerCase();
                            if (tmpSearch.contains(s1.toLowerCase())) {
                                listClone.add(string);
                            }
                        }
                    } else {
                        listClone.addAll(allUsersWithRole);
                        isAllShown = true;
                    }
                    groupRoleCreationAdapter.clear();
//					ArrayList<UserInfo> list = new ArrayList<UserInfo>();
                    GroupRoleCreationAdapter.UserInfo info1 = new GroupRoleCreationAdapter.UserInfo(SharedPrefManager.getInstance().getUserName(), "You", null);
                    if (isAllShown && !isGroupInvitation)
                        groupRoleCreationAdapter.add(info1);
                    for (GroupRoleCreationAdapter.UserInfo member : listClone) {
                        if (SharedPrefManager.getInstance().getUserName().equals(member.userName))
                            continue;
                        GroupRoleCreationAdapter.UserInfo info = new GroupRoleCreationAdapter.UserInfo(member.userName, SharedPrefManager.getInstance().getUserServerName(member.userName), null);
//						list.add(info);
                        groupRoleCreationAdapter.add(info);
                    }

//					groupRoleCreationAdapter = new GroupRoleCreationAdapter(EsiaChatContactsScreen.this,R.layout.esiachat_contact_item,list,Constants.GROUP_USER_CHAT_CREATE);
//					groupRoleCreationAdapter.setEditableContact(true);
                    groupRoleCreationAdapter.notifyDataSetChanged();
//					contactList.setAdapter(groupRoleCreationAdapter);
                } else if (SCREEN_TYPE == Constants.GROUP_USER_CHAT_CREATE) { // && !isGroupInvitation
                    if (allUsers == null)
                        return;
                    boolean isAllShown = false;
                    ArrayList<String> listClone = new ArrayList<String>();
                    if (s1 != null && !s1.trim().equals("")) {
                        for (String string : allUsers) {
                            String tmpSearch = SharedPrefManager.getInstance().getUserServerDisplayName(string).toLowerCase();
                            if (tmpSearch.contains(s1.toLowerCase())) {
                                listClone.add(string);
                            }
                        }
                    } else {
                        listClone.addAll(allUsers);
                        isAllShown = true;
                    }
                    groupRoleCreationAdapter.clear();
                    ArrayList<GroupRoleCreationAdapter.UserInfo> list = new ArrayList<GroupRoleCreationAdapter.UserInfo>();
                    GroupRoleCreationAdapter.UserInfo info1 = new GroupRoleCreationAdapter.UserInfo(SharedPrefManager.getInstance().getUserName(), "You", null);
                    if (isAllShown && !isGroupInvitation)
                        groupRoleCreationAdapter.add(info1);
                    for (String member : listClone) {
                        GroupRoleCreationAdapter.UserInfo info = new GroupRoleCreationAdapter.UserInfo(member, SharedPrefManager.getInstance().getUserServerName(member), null);
//					list.add(info);
                        groupRoleCreationAdapter.add(info);
                    }

//				groupRoleCreationAdapter = new GroupRoleCreationAdapter(EsiaChatContactsScreen.this,R.layout.esiachat_contact_item,list,Constants.GROUP_USER_CHAT_CREATE);
                    groupRoleCreationAdapter.setEditableContact(true);
                    groupRoleCreationAdapter.notifyDataSetChanged();
//				contactList.setAdapter(groupRoleCreationAdapter);
                } else if (SCREEN_TYPE == Constants.MEMBER_DELETE) {
                    s1 = (new StringBuilder()).append("%").append(searchBoxView.getText().toString().trim()).append("%").toString();
                    int i = s1.length();
                    String as[] = null;
                    String s2 = null;
                    if (i >= 1) {
                        s2 = "(" + DatabaseConstants.VOPIUM_FIELD + "=? OR " + DatabaseConstants.VOPIUM_FIELD + "=? )" + "AND "
                                + DatabaseConstants.CONTACT_NAMES_FIELD + " like ?";
                        as = new String[3];
                        as[0] = "1";
                        as[1] = "2";
                        as[2] = s1;
//					s2 = DatabaseConstants.VOPIUM_FIELD + "!=?";
//					as = (new String[] { "2" });
                    }
                    updateAtmeCursor(s2, as);
                } else {
                    int i = s1.length();
                    String as[] = null;
                    String s2 = null;
                    if (i >= 1) {
                        s2 = DatabaseConstants.VOPIUM_FIELD + "=? AND "
                                + DatabaseConstants.CONTACT_NAMES_FIELD + " like ?";
                        as = new String[2];
                        as[0] = "1";
                        as[1] = s1;
                    }
                    updateAtmeCursor(s2, as);
                }
            }

            public void beforeTextChanged(CharSequence charsequence, int i,
                                          int j, int k) {
            }

            public void onTextChanged(CharSequence charsequence, int i, int j,
                                      int k) {
            }

        });
        addGroupUserBoxView.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable editable) {
                String s1 = (new StringBuilder()).append("%")
                        .append(addGroupUserBoxView.getText().toString()).append("%")
                        .toString();
                int i = s1.length();
                String as[] = null;
                String s2 = null;
                if (i >= 1) {
                    s2 = DatabaseConstants.VOPIUM_FIELD + "=? AND "
                            + DatabaseConstants.CONTACT_NAMES_FIELD + " like ?";
                    as = new String[2];
                    as[0] = "1";
                    as[1] = s1;
                }
                updateAtmeCursor(s2, as);
            }

            public void beforeTextChanged(CharSequence charsequence, int i,
                                          int j, int k) {
            }

            public void onTextChanged(CharSequence charsequence, int i, int j,
                                      int k) {
            }

        });
        if (searchBoxView != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchBoxView.getWindowToken(), 0);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//    	Tracker t = ((SuperChatApplication) getApplicationContext()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Contacts Screen");
//        t.send(new HitBuilders.AppViewBuilder().build());
    }

    GroupRoleCreationAdapter groupRoleCreationAdapter;

    private void initOrCreateAdapter(byte screenType) {
        String as[] = {DatabaseConstants.CONTACT_NAMES_FIELD};
        int ai[] = new int[1];
        ai[0] = R.id.id_contact_name;
        boolean isFresh = false;
        if (screenType == Constants.GROUP_USER_CHAT_CREATE) { // && !isGroupInvitation
            //DBWrapper.getInstance().getAllUsers(null);
            if (allUsers != null)
                allUsers.clear();
//			if(isGroupInvitation || isChannel){
////				allUsers = DBWrapper.getInstance().getAllUsers(previousUsersList);
//				allUsers = DBWrapper.getInstance().getAllFilteredUsersForSG(previousUsersList);
//			}
//			else
//				allUsers = DBWrapper.getInstance().getAllUsers(null);
            if (isChannel || isBroadcast) {
                if (SharedPrefManager.getInstance().isDomainAdmin())
                    allUsers = DBWrapper.getInstance().getAllFilteredUsersForSG(previousUsersList);
                if ((allUsers == null || allUsers.isEmpty()))
                    allUsers = DBWrapper.getInstance().getAllUsers(previousUsersList);
            } else if (isSharedIDCreate) {
                allUsers = DBWrapper.getInstance().getAllUsers(null);
            } else if (isSharedIDUpdate) {
                if (previousUsersList.contains(SharedPrefManager.getInstance().getUserName()))
                    previousUsersList.remove(SharedPrefManager.getInstance().getUserName());
//				if(SharedPrefManager.getInstance().isOpenDomain())
//					allUsers = DBWrapper.getInstance().getAllUsers(previousUsersList);
//				else
                allUsers = DBWrapper.getInstance().getAllUsers(previousUsersList);
            } else
                allUsers = DBWrapper.getInstance().getAllFilteredUsersForSG(previousUsersList);
            ArrayList<GroupRoleCreationAdapter.UserInfo> list = new ArrayList<GroupRoleCreationAdapter.UserInfo>();
            if (!isSharedIDUpdate) {
                GroupRoleCreationAdapter.UserInfo info1 = new GroupRoleCreationAdapter.UserInfo(SharedPrefManager.getInstance().getUserName(), "You", null);
                if (!isGroupInvitation)
                    list.add(info1);
            }
            for (String member : allUsers) {
                GroupRoleCreationAdapter.UserInfo info =
                        new GroupRoleCreationAdapter.UserInfo(member, SharedPrefManager.getInstance().getUserServerName(member), "99109");
                list.add(info);
            }
            groupRoleCreationAdapter = new GroupRoleCreationAdapter(this, R.layout.esiachat_contact_item, list, Constants.GROUP_USER_CHAT_CREATE);
            groupRoleCreationAdapter.setEditableContact(true);
            if (isSharedIDCreate || isSharedIDUpdate)
                groupRoleCreationAdapter.setMaxCount(10);
            List<String> admins = HomeScreen.getAdminSetForSharedID(groupName);
            if (isSharedIDUpdate)
                groupRoleCreationAdapter.setMemberCount(admins.size());
            contactList.setAdapter(null);
            contactList.setAdapter(groupRoleCreationAdapter);
        } else if (adapter == null) {
            isFresh = true;
            if (screenType == Constants.MEMBER_DELETE) {
//				cursor = DBWrapper.getInstance().getModifyContacts(previousUsersList);
                cursor = DBWrapper.getInstance().getCursorForFilteredUsersForSGNew(previousUsersList);
            } else
                cursor = DBWrapper.getInstance().getEsiaContactsNew(previousUsersList);

            adapter = new EsiaChatContactsAdapter(EsiaChatContactsScreen.this, R.layout.esiachat_contact_item, cursor, as, ai, 0, screenType);
            if (screenType == Constants.GROUP_USER_CHAT_CREATE) {
                titleView.setText(getString(R.string.manage_members));
                adapter.setEditableContact(true);
            }
            adapter.changeCursor(cursor);
            contactList.setAdapter(adapter);
        }
        switch (screenType) {
            case Constants.GROUP_USER_CHAT_INVITE:

                break;
            case Constants.GROUP_USER_SELECTED_LIST:
                ArrayList<String> selectedUsers = getSelectedUsers(screenType);
                if (selectedUsers == null || selectedUsers.isEmpty())
                    contactList.setAdapter(null);
                else {
                    cursor = DBWrapper.getInstance().getEsiaSelectedContacts(selectedUsers);
                    adapter.setSelectedItems(selectedUsers);
                    adapter.setEditableContact(true);
                    adapter.swapCursor(cursor);
                    adapter.notifyDataSetChanged();
                }
                break;
            case Constants.GROUP_USER_CHAT_CREATE:
                if (!isFresh) {
//				cursor = DBWrapper.getInstance().getEsiaContacts(previousUsersList);
//				adapter.setEditableContact(true);
//				contactList.setAdapter(adapter);
//				adapter.swapCursor(cursor);

//				adapter.notifyDataSetChanged();
                } else {
//				if(isGroupInvitation){
//					cursor = DBWrapper.getInstance().getEsiaContacts(previousUsersList);
//					adapter.setEditableContact(true);
//				}
                }
                break;
        }

    }

    private void setScreen(byte screenType) {
        SCREEN_TYPE = screenType;
        switch (screenType) {

            case Constants.GROUP_USER_CHAT_CREATE:
                titleView.setVisibility(TextView.VISIBLE);
                createGroupView.setVisibility(TextView.GONE); // createGroupView.setVisibility(TextView.INVISIBLE);
                sendBroadCastView.setVisibility(TextView.GONE);
                itemCountView.setVisibility(TextView.GONE);
                if (!isSharedIDCreate && !isSharedIDUpdate) {
                    allSelectTextView.setVisibility(TextView.VISIBLE);
                    allSelectCheckBox.setVisibility(TextView.VISIBLE);
                }
                backArrowView.setVisibility(ImageView.VISIBLE);
                searchBoxView.setVisibility(TextView.VISIBLE);
//				clearSearch.setVisibility(TextView.VISIBLE);
                doneView.setVisibility(TextView.VISIBLE);
//				backTitleView.setVisibility(ImageView.VISIBLE);
//				backTitleView.setText(getString(R.string.back));
//				if(isBroadcast)
//					titleView.setText(getString(R.string.broadcast));
//				else
//					titleView.setText(getString(R.string.create_group));
                titleView.setText(getString(R.string.add_member));//
                doneView.setText(getString(R.string.done));
                plusView.setVisibility(ImageView.GONE);
                addGroupUserBoxView.setVisibility(EditText.GONE);
                if (isGroupInvitation) {
                    if (isBroadcast) {
//						backTitleView.setText(getString(R.string.back));
                        titleView.setText(getString(R.string.add_member));//	titleView.setText(getString(R.string.invite_in_group));
                    } else {
//						backTitleView.setText(getString(R.string.back));
                        titleView.setText(getString(R.string.add_member));//titleView.setText(getString(R.string.add_participants));
                    }
                }
                break;
            case Constants.GROUP_USER_CHAT_INVITE:
                titleView.setVisibility(TextView.INVISIBLE);
                createGroupView.setVisibility(TextView.INVISIBLE);
                sendBroadCastView.setVisibility(TextView.GONE);
                itemCountView.setVisibility(TextView.VISIBLE);
                allSelectTextView.setVisibility(TextView.VISIBLE); // VISIBLE
                allSelectCheckBox.setVisibility(TextView.VISIBLE); // VISIBLE
                doneView.setVisibility(TextView.VISIBLE);
                addGroupUserBoxView.setVisibility(EditText.GONE);
                doneView.setText(getString(R.string.done));
                if (isGroupInvitation) {
//					backTitleView.setText(getString(R.string.back));
                    titleView.setText(getString(R.string.add_member));//titleView.setText(getString(R.string.invite_in_group));
                }
                break;
            case Constants.GROUP_USER_SELECTED_LIST:
                plusView.setVisibility(ImageView.VISIBLE);
                addGroupUserBoxView.setVisibility(EditText.VISIBLE);
                titleView.setVisibility(TextView.VISIBLE);
                createGroupView.setVisibility(TextView.GONE);
                sendBroadCastView.setVisibility(TextView.GONE);
                itemCountView.setVisibility(TextView.GONE);
                allSelectTextView.setVisibility(TextView.GONE);
                allSelectCheckBox.setVisibility(CheckBox.GONE);
                backArrowView.setVisibility(ImageView.GONE);
                searchBoxView.setVisibility(TextView.INVISIBLE);
                clearSearch.setVisibility(TextView.INVISIBLE);
                doneView.setVisibility(TextView.VISIBLE);
                backArrowView.setVisibility(ImageView.GONE);
//				backTitleView.setVisibility(ImageView.VISIBLE);
//				backTitleView.setText(getString(R.string.cancel));
//				if(isBroadcast)
//					titleView.setText(getString(R.string.broadcast));
//				else
//					titleView.setText(getString(R.string.create_group));
                titleView.setText(getString(R.string.add_member));//
                if (!isGroupInvitation)
                    doneView.setText(getString(R.string.create));
                else
                    doneView.setText(getString(R.string.done));
                if (isGroupInvitation) {
//					backTitleView.setText(getString(R.string.back));
                    titleView.setText(getString(R.string.add_member));//titleView.setText(getString(R.string.invite_in_group));
                }
                break;
            case Constants.MULTI_USER_CHAT_CREATE:
                titleView.setVisibility(TextView.INVISIBLE);
                createGroupView.setVisibility(TextView.INVISIBLE);
                sendBroadCastView.setVisibility(TextView.GONE);
                itemCountView.setVisibility(TextView.VISIBLE);
                allSelectTextView.setVisibility(TextView.GONE); // VISIBLE
                allSelectCheckBox.setVisibility(CheckBox.GONE); // VISIBLE
                doneView.setVisibility(TextView.VISIBLE);
                break;
            case Constants.MULTI_USER_CHAT_INVITE:
                titleView.setVisibility(TextView.INVISIBLE);
                createGroupView.setVisibility(TextView.INVISIBLE);
                sendBroadCastView.setVisibility(TextView.GONE);
                itemCountView.setVisibility(TextView.VISIBLE);
                allSelectTextView.setVisibility(TextView.GONE); // VISIBLE
                allSelectCheckBox.setVisibility(CheckBox.GONE); // VISIBLE
                doneView.setVisibility(TextView.VISIBLE);
                addGroupUserBoxView.setVisibility(EditText.GONE);
                break;
            case Constants.MULTI_USER_SELECTED_LIST:
                titleView.setVisibility(TextView.VISIBLE);
                createGroupView.setVisibility(TextView.GONE);
                sendBroadCastView.setVisibility(TextView.GONE);
                itemCountView.setVisibility(TextView.GONE);
                allSelectTextView.setVisibility(TextView.GONE);
                allSelectCheckBox.setVisibility(CheckBox.GONE);
                backArrowView.setVisibility(ImageView.GONE);
                searchBoxView.setVisibility(TextView.INVISIBLE);
                clearSearch.setVisibility(TextView.INVISIBLE);
                doneView.setVisibility(TextView.VISIBLE);
//				backTitleView.setVisibility(ImageView.VISIBLE);
//				backTitleView.setText(getString(R.string.back));
                titleView.setText(getString(R.string.add_broadcast_list));
                doneView.setText(getString(R.string.done));
                break;
            case Constants.MEMBER_DELETE:
            default:
                doneView.setVisibility(TextView.GONE);
                addGroupUserBoxView.setVisibility(EditText.GONE);
                searchBoxView.setVisibility(TextView.VISIBLE);
//					clearSearch.setVisibility(TextView.VISIBLE);
                createGroupView.setVisibility(TextView.GONE); // INVISIBLE
                sendBroadCastView.setVisibility(TextView.GONE); // VISIBLE
                itemCountView.setVisibility(TextView.GONE); // INVISIBLE
                allSelectTextView.setVisibility(TextView.GONE); // INVISIBLE
                allSelectCheckBox.setVisibility(CheckBox.GONE); // INVISIBLE
                plusView.setVisibility(ImageView.INVISIBLE);
                if (isGroupInvitation) {
//						backTitleView.setText(getString(R.string.back));
                    titleView.setText(getString(R.string.add_member));//titleView.setText(getString(R.string.invite_in_group));
                }
                if (screenType == Constants.MEMBER_DELETE)
                    titleView.setText(getString(R.string.manage_members));

                break;
        }
    }

    private void updateAtmeCursor(String s, String as[]) {
        Log.i(TAG, "Updating cursor");
        if (isGroupInvitation)
            cursor = DBWrapper.getInstance().query(
                    DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS, null, s, as,
                    DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, " + DatabaseConstants.CONTACT_NAMES_FIELD
                            + " COLLATE NOCASE");
        else if (SCREEN_TYPE == Constants.MEMBER_DELETE)
            cursor = DBWrapper.getInstance().query(
                    DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS, null, s, as,
                    DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, " + DatabaseConstants.CONTACT_NAMES_FIELD
                            + " COLLATE NOCASE");
        else
            cursor = DBWrapper.getInstance().query(
                    DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, null, s, as,
                    DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, " + DatabaseConstants.CONTACT_NAMES_FIELD
                            + " COLLATE NOCASE");
        if (cursor != null && adapter != null) {
//			contactSyncMessage.setVisibility(View.GONE);
            adapter.changeCursor(cursor);
        }
    }

    public void onResume() {
        onForeground = true;
        bindService(new Intent(this, ChatService.class), mConnection, Context.BIND_AUTO_CREATE);
        ChatService.setConnectionStatusListener(this);

        super.onResume();
    }

    public void onPause() {
        onForeground = false;
        try {
            unbindService(mConnection);
        } catch (Exception e) {
        }
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();

    }

    public void onBackPressed() {

        onBackClick(null);

    }

    private ArrayList<String> getSelectedUsers(int screenType) {
        ArrayList<String> list = new ArrayList<String>();
        if (screenType == Constants.GROUP_USER_CHAT_CREATE) { // && !isGroupInvitation
            HashMap<String, Boolean> hm = groupRoleCreationAdapter.getSelectedItems();
            for (String key : hm.keySet()) {
                if (hm.get(key) != null && (boolean) hm.get(key)) {
                    list.add(key);
                }
            }
        } else if (screenType == Constants.GROUP_USERS_ROLE_SELECTION && !isGroupInvitation) {
            HashMap<String, Boolean> hm = groupRoleCreationAdapter.getSelectedItems();
            for (String key : hm.keySet()) {
                if (hm.get(key) != null && (boolean) hm.get(key)) {
                    list.add(key);
                }
            }
        } else {
            HashMap<String, Boolean> hm = adapter.getSelectedItems();
            for (String key : hm.keySet()) {
                if (hm.get(key) != null && (boolean) hm.get(key)) {
                    list.add(key);
                }
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
//		ArrayList<String> allUsers = null;
        switch (v.getId()) {
            case R.id.id_all_selected:
                allSelectCheckBox.setChecked(!allSelectCheckBox.isChecked());

                if (isGroupInvitation) {
                    if (SharedPrefManager.getInstance().isDomainAdmin())
                        allUsers = DBWrapper.getInstance().getAllFilteredUsersForSG(null);
                    if ((allUsers == null || allUsers.isEmpty()))
                        allUsers = DBWrapper.getInstance().getAllUsers(previousUsersList);
                } else
                    allUsers = DBWrapper.getInstance().getAllUsers(null);
                if (!isGroupInvitation)
                    allUsers.add(SharedPrefManager.getInstance().getUserName());
                for (String tUser : allUsers) {
                    groupRoleCreationAdapter.setItems(tUser, allSelectCheckBox.isChecked());
                }
                if (allSelectCheckBox.isChecked()) {
                    groupRoleCreationAdapter.totalChecked = allUsers.size();
                } else {
                    groupRoleCreationAdapter.totalChecked = 0;
                }
//			itemCountView.setText(adapter.totalChecked+" "+getString(R.string.selected));
                groupRoleCreationAdapter.notifyDataSetChanged();
            case R.id.id_all_sel_box:
//			ArrayList<String> 
//			if(isGroupInvitation || isChannel || isBroadcast){
//				if(SharedPrefManager.getInstance().isDomainAdmin())
//				allUsers = DBWrapper.getInstance().getAllFilteredUsersForSG(null);
//				if((allUsers==null || allUsers.isEmpty()))
//					allUsers = DBWrapper.getInstance().getAllUsers(previousUsersList);	
//			}else
//				allUsers = DBWrapper.getInstance().getAllUsers(null);
                if (!isGroupInvitation)
                    allUsers.add(SharedPrefManager.getInstance().getUserName());
                for (String tUser : allUsers) {
                    groupRoleCreationAdapter.setItems(tUser, allSelectCheckBox.isChecked());
                }
                if (allSelectCheckBox.isChecked()) {
                    groupRoleCreationAdapter.totalChecked = allUsers.size();
                } else {
                    groupRoleCreationAdapter.totalChecked = 0;
                }
//			itemCountView.setText(adapter.totalChecked+" "+getString(R.string.selected));
                groupRoleCreationAdapter.notifyDataSetChanged();
                break;

            case R.id.id_create_group:
//			if (adapter != null && createGroupView.isShown()) {
//				createGroupView.setVisibility(TextView.INVISIBLE);
//				//sendBroadCastView.setVisibility(TextView.GONE);
//				titleView.setVisibility(TextView.INVISIBLE);
//				itemCountView.setVisibility(TextView.VISIBLE);
//				doneView.setVisibility(TextView.VISIBLE);
//				backTitleView.setText(getString(R.string.create_group));
//				adapter.setEditableContact(true);
//				adapter.notifyDataSetChanged();
//			}
//			Intent intent = new Intent(this, CreateGroupScreen.class);
//			intent.putExtra(Constants.INVITATION_LIST, users);
//			startActivity(intent);
                break;
            case R.id.id_broadcast_message:
//			if (adapter != null && sendBroadCastView.isShown()) {
//				titleView.setVisibility(TextView.INVISIBLE);
//				createGroupView.setVisibility(TextView.INVISIBLE);
//				sendBroadCastView.setVisibility(TextView.GONE);
//				itemCountView.setVisibility(TextView.VISIBLE);
//				doneView.setVisibility(TextView.VISIBLE);
//				backTitleView.setText(getString(R.string.broadcast_message));
//				adapter.setEditableContact(true);
//				adapter.notifyDataSetChanged();
//				isBroadcast = true;
//			}			
                Intent intent = new Intent(this, CreateBroadCastScreen.class); // 21march
                intent.putExtra(Constants.BROADCAST, true);
                startActivity(intent);
                break;
            case R.id.id_plus:
//			ArrayList<String> users = getSelectedUsers();
//			Cursor cursor1 = DBWrapper.getInstance().getEsiaSelectedContacts(users);
                setScreen(Constants.GROUP_USER_CHAT_CREATE);

                initOrCreateAdapter(SCREEN_TYPE);
                break;
            case R.id.id_search_cross:
                searchBoxView.setText("");
                break;
            case R.id.id_done:
                if (SCREEN_TYPE == Constants.GROUP_USERS_ROLE_SELECTION) {

                    String owner = groupRoleCreationAdapter.getOwner();
                    if (owner != null && !owner.equals(""))
                        new GroupAndBroadcastTaskOnServer(groupName, groupName, groupType, groupRoleCreationAdapter.getAllUsers(), groupRoleCreationAdapter.getSelectedMembers(),
                                owner, groupDiscription, Constants.GROUP_USER_CHAT_CREATE).execute(groupFileId);
                    else
                        showDialog("Please select owner of this group.", getString(R.string.ok));
                    break;
                }
                inviters = getSelectedUsers(SCREEN_TYPE);
                if (isSharedIDUpdate && inviters.size() == 1 && inviters.contains(SharedPrefManager.getInstance().getUserName())) {
                    inviters.remove(SharedPrefManager.getInstance().getUserName());
                }
                if (inviters != null && !inviters.isEmpty()) {
//					if(isBroadcast){
//						isBroadcast = false;
//						intent = new Intent(this,CreateBroadCastScreen.class);
//						startActivity(intent);
//					}else
                    switch (SCREEN_TYPE) {

                        case Constants.GROUP_USER_SELECTED_LIST:

                            if (service != null) {
                                if (isGroupInvitation) {
////								intent = new Intent(this,ChatListScreen.class);
////								intent.putExtra(Constants.IS_GROUP_CREATION, false);
////								intent.putStringArrayListExtra(Constants.INVITATION_LIST, inviters);
////								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
////								intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,SharedPrefManager.getInstance().getGroupDisplayName(groupName));
////								intent.putExtra(DatabaseConstants.USER_NAME_FIELD,groupName);
////								startActivity(intent);
//								intent = new Intent(this,GroupProfileScreen.class);
//								intent.putExtra(Constants.IS_GROUP_CREATION, false);
//								intent.putStringArrayListExtra(Constants.INVITATION_LIST, inviters);
////								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
//								intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD,SharedPrefManager.getInstance().getGroupDisplayName(groupName));
//								intent.putExtra(DatabaseConstants.USER_NAME_FIELD,groupName);
////								startActivity(intent);
//								setResult(RESULT_OK, intent);
//									 new GroupAndBroadcastTaskOnServer(groupName, SharedPrefManager.getInstance().getGroupDisplayName(groupName), inviters,false).execute();
                                } else if (isValide(groupName) && !isLoading) {
                                    if (isBroadcast) {
                                        String broadCastUUID = UUID.randomUUID().toString();
                                        SharedPrefManager.getInstance().saveBroadCastName(broadCastUUID, groupName);
                                        for (String inviter : inviters)
                                            SharedPrefManager.getInstance().saveUsersOfBroadCast(broadCastUUID, inviter);
                                        saveMessage(groupName, broadCastUUID, "Created broadcast list");
                                        Log.d(TAG, "Broadcast list creation called.");
                                        intent = new Intent(EsiaChatContactsScreen.this, ChatListScreen.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                        intent.putExtra(Constants.IS_GROUP_CREATION, true);
                                        intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, groupName);
                                        intent.putExtra(DatabaseConstants.USER_NAME_FIELD, broadCastUUID);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        isLoading = true;
                                        AsyncCreateTask task = new AsyncCreateTask();
                                        task.execute(new String[]{groupName, groupDiscription});
                                        Log.d(TAG, "Group name creation are calling.");
                                    }
                                } else {
                                    showDialog(getString(R.string.create_group_validation_message), getString(R.string.ok));
                                    isLoading = false;
                                }

                            } else {
                                isLoading = false;
                                showDialog(getString(R.string.error_network_connection), getString(R.string.ok));
                            }

                            break;
//					case Constants.GROUP_USERS_ROLE_SELECTION:
//						multiOptionTitle.setVisibility(View.GONE);
//						new GroupAndBroadcastTaskOnServer(groupName, groupName, groupRoleCreationAdapter.getAllUsers(),groupRoleCreationAdapter.getSelectedMembers(),groupRoleCreationAdapter.getOwner(),groupDiscription,Constants.GROUP_USER_CHAT_CREATE).execute(groupFileId);
//						break;
                        case Constants.GROUP_USER_CHAT_CREATE:
                            if (isBroadcast) {
                                if (isGroupInvitation)
                                    new GroupAndBroadcastTaskOnServer(groupName, SharedPrefManager.getInstance().getBroadCastDisplayName(groupName), inviters, groupDiscription, Constants.BROADCAST_LIST_UPDATE).execute();
                                else
                                    new GroupAndBroadcastTaskOnServer(groupName, SharedPrefManager.getInstance().getBroadCastDisplayName(groupName), inviters, groupDiscription, Constants.BROADCAST_LIST_CRATE).execute(groupFileId);
                                break;
                            }
                            if (isSharedIDCreate) {
                                new GroupAndBroadcastTaskOnServer(groupName, groupName, inviters, groupDiscription, Constants.SHARED_ID_CREATE).execute(groupFileId);
                                break;
                            } else if (isSharedIDUpdate) {
                                new GroupAndBroadcastTaskOnServer(groupName, SharedPrefManager.getInstance().getSharedIDDisplayName(groupName), inviters, groupDiscription, Constants.BROADCAST_LIST_UPDATE).execute(groupFileId);
                                break;
                            } else if (isGroupInvitation) {
                                new GroupAndBroadcastTaskOnServer(groupName, SharedPrefManager.getInstance().getGroupDisplayName(groupName), inviters, groupDiscription, Constants.GROUP_USER_CHAT_INVITE).execute();
                                break;
                            }

                            allUsersWithRole = new ArrayList<GroupRoleCreationAdapter.UserInfo>();
                            ArrayList<GroupRoleCreationAdapter.UserInfo> allTempUsersWithRole = new ArrayList<GroupRoleCreationAdapter.UserInfo>();
                            if (inviters.contains(SharedPrefManager.getInstance().getUserName())) {
                                GroupRoleCreationAdapter.UserInfo info = new GroupRoleCreationAdapter.UserInfo(SharedPrefManager.getInstance().getUserName(), "You", null);
                                allUsersWithRole.add(info);
                                allTempUsersWithRole.add(info);
                            }
                            for (String member : inviters) {
                                String dispName = SharedPrefManager.getInstance().getUserServerName(member);
                                if (SharedPrefManager.getInstance().getUserName().equals(member))
                                    continue;
//								dispName = "You";
                                GroupRoleCreationAdapter.UserInfo info = new GroupRoleCreationAdapter.UserInfo(member, dispName, null);
                                allUsersWithRole.add(info);
                                allTempUsersWithRole.add(info);
                            }
                            allSelectTextView.setVisibility(TextView.GONE);
                            allSelectCheckBox.setVisibility(CheckBox.GONE);
                            SCREEN_TYPE = Constants.GROUP_USERS_ROLE_SELECTION;
                            multiOptionTitle.setVisibility(View.VISIBLE);
                            if (!SharedPrefManager.getInstance().isDomainAdmin())
                                ((TextView) findViewById(R.id.id_owner_title)).setVisibility(View.GONE);
                            titleView.setText(getString(R.string.select_admin_owner));
                            groupRoleCreationAdapter = new GroupRoleCreationAdapter(this, R.layout.esiachat_contact_item, allTempUsersWithRole, Constants.GROUP_USERS_ROLE_SELECTION);
                            contactList.setAdapter(null);
                            contactList.setAdapter(groupRoleCreationAdapter);
                    }
                } else {
//					new GroupAndBroadcastTaskOnServer(groupName, groupName, inviters,groupDiscription,Constants.GROUP_USER_CHAT_CREATE).execute(groupFileId);
                    showDialog(getString(R.string.none_selected), getString(R.string.ok));
                }
                break;

        }
    }

    private String getUserInfo(List<ContentValues> vlaues, String username, int type) {
        String value = null;
        for (ContentValues members : vlaues) {
            if (type == 1) {
            } else if (type == 2) {

            }
        }
        return value;
    }

    public void showDialog(String s, String btnTxt) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        TextView btn = ((TextView) bteldialog.findViewById(R.id.id_ok));
        btn.setText(btnTxt);
        btn.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    public void showBackDialog(final String title, final String s) {
        final Dialog bteldialog = new Dialog(this);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.custom_dialog_two_button);
        if (title != null) {
            ((TextView) bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
        }
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_send)).setText("Leave page");
//		((TextView)bteldialog.findViewById(R.id.id_cancel)).setText("Later");
        ((TextView) bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                multiOptionTitle.setVisibility(View.GONE);
                setScreen(Constants.GROUP_USER_CHAT_CREATE);
                initOrCreateAdapter(SCREEN_TYPE);
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

    public void onBackClick(View view) {
//			if(isGroupInvitation)
//			{
//				finish();
//				return;
//			}
        switch (SCREEN_TYPE) {
            case Constants.GROUP_USERS_ROLE_SELECTION:
                multiOptionTitle.setVisibility(View.GONE);
                setScreen(Constants.GROUP_USER_CHAT_CREATE);
                initOrCreateAdapter(SCREEN_TYPE);
//				showBackDialog("SuperChat","Group not created. If you leave this page, all changes will be lost.");
                break;
//			case Constants.GROUP_USER_SELECTED_LIST:
//				finish();
//				break;
//				default:
//					if (adapter != null) {
//						if (adapter.isEditable()) {
//							createGroupView.setVisibility(TextView.INVISIBLE);
//							sendBroadCastView.setVisibility(TextView.GONE); // VISIBLE
//							titleView.setVisibility(TextView.VISIBLE);
//							itemCountView.setVisibility(TextView.INVISIBLE);
//							doneView.setVisibility(TextView.GONE);
//							backTitleView.setText(getString(R.string.small_chat));
//							itemCountView.setText("0 "+getString(R.string.selected));
//							adapter.setEditableContact(false);
//							adapter.removeSelectedItems();
//							adapter.notifyDataSetChanged();
//						} else
//							finish();
//
//					} else {
//						finish();
//					}
            default:
                finish();
                return;
        }

    }

    private class AsyncCreateTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(EsiaChatContactsScreen.this, "",
                    "Loading. Please wait...", true);
        }

        @Override
        protected String doInBackground(String... urls) {
            inviters.add(SharedPrefManager.getInstance().getUserName());
            if (service == null)
                return null;
            String groupId = service.createMultiUserChat(urls[0], inviters, urls[1]);
            return groupId;
        }

        @Override
        protected void onPostExecute(String groupId) {
            Log.d(TAG, "AsyncCreateTask: " + groupId);

            if (groupId != null && !groupId.equals("")) {
                if (groupFileId != null && groupId != null && !groupFileId.equals("") && !groupId.equals(""))
                    SharedPrefManager.getInstance().saveUserFileId(groupId, groupFileId);
                SharedPrefManager.getInstance().saveServerGroupState(groupId, GroupCreateTaskOnServer.SERVER_GROUP_NOT_CREATED);
                SharedPrefManager.getInstance().saveUserStatusMessage(groupId, groupDiscription);
                Intent intent = new Intent(EsiaChatContactsScreen.this, ChatListScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                intent.putExtra(Constants.IS_GROUP_CREATION, true);
                intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, groupName);
                intent.putExtra(DatabaseConstants.USER_NAME_FIELD, groupId);
                startActivity(intent);
                Log.d(TAG, "Group name creation are called.");
                finish();
            } else {
                showDialog(getString(R.string.lbl_please_try_later), getString(R.string.ok));
            }

            if (dialog != null)
                dialog.dismiss();
            dialog = null;
            isLoading = false;
        }
    }

    private boolean isValide(String value) {
        if (value != null && !value.equals("") && value.length() >= 1) {
            return true;
        }
        return false;
    }

    @Override
    public void chatClientConnected(ChatService service) {
        // TODO Auto-generated method stub

    }

    @Override
    public void chatClientDisconnected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyTypingRecieve(String user) {
        // TODO Auto-generated method stub

    }

    @Override
    public void refreshOnlineGroupUser() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refreshSubjectOfGroup() {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyChatRecieve(String sender, String message) {

    }

    public void saveMessage(String displayName, String from, String msg) {
        try {
            ChatDBWrapper chatDBWrapper = ChatDBWrapper.getInstance();
            ContentValues contentvalues = new ContentValues();
            String myName = SharedPrefManager.getInstance().getUserName();
            contentvalues.put(DatabaseConstants.FROM_USER_FIELD, from);
            contentvalues.put(DatabaseConstants.TO_USER_FIELD, "");
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
//				AtMeApplication.dayValue = date;
            contentvalues.put(DatabaseConstants.LAST_UPDATE_FIELD, currentTime);

            contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, name);
            chatDBWrapper.insertInDB(DatabaseConstants.TABLE_NAME_MESSAGE_INFO, contentvalues);
        } catch (Exception e) {

        }
    }

    private class InviteMemberServerTask extends AsyncTask<String, String, String> {
        AddMemberModel requestForm;
        ProgressDialog progressDialog = null;
        View view1;

        public InviteMemberServerTask(AddMemberModel requestForm, final View view1) {
            this.requestForm = requestForm;
            this.view1 = view1;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(EsiaChatContactsScreen.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String JSONstring = new Gson().toJson(requestForm);
            DefaultHttpClient client1 = new DefaultHttpClient();
            Log.d(TAG, "InviteMemberServerTask request:" + JSONstring);
            HttpPost httpPost = new HttpPost(Constants.SERVER_URL + "/tiger/rest/admin/inviteuser");
            httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
            HttpResponse response = null;
            try {
                httpPost.setEntity(new StringEntity(JSONstring));
                try {
                    response = client1.execute(httpPost);
                    final int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                        String line = "";
                        String str = "";
                        while ((line = rd.readLine()) != null) {

                            str += line;
                        }
                        Log.d(TAG, "invite Result : " + str);
                        if (str != null && !str.equals("")) {
                            str = str.trim();
                            Gson gson = new GsonBuilder().create();
                            if (str == null || str.contains("error")) {
                                return str;
                            }
                            AddMemberResponseModel regObj = gson.fromJson(str, AddMemberResponseModel.class);
                            if (regObj != null) {
                                if (regObj.accountCreated != null && !regObj.accountCreated.isEmpty()) {
//										dialogHandler.sendEmptyMessage(1);//showDialog("User invitation sent",true);
                                } else if (regObj.accountAlreadyExists != null && !regObj.accountAlreadyExists.isEmpty()) {
//										dialogHandler.sendEmptyMessage(2);//showDialog("User already added.",false);
                                } else if (regObj.accountFailed != null && !regObj.accountFailed.isEmpty()) {
//										dialogHandler.sendEmptyMessage(3);//showDialog("User already added in this domain.",false);
                                }
                            }
                        }
                    }
                } catch (ClientProtocolException e) {
                    Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:" + e.toString());
                } catch (IOException e) {
                    Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:" + e.toString());
                }

            } catch (UnsupportedEncodingException e1) {
                Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:" + e1.toString());
            } catch (Exception e) {
                Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:" + e.toString());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (str != null && str.contains("error")) {
                Gson gson = new GsonBuilder().create();
                ErrorModel errorModel = null;
                try {
                    errorModel = gson.fromJson(str, ErrorModel.class);
                } catch (Exception e) {
                }
                if (errorModel != null) {
                    if (errorModel.citrusErrors != null
                            && !errorModel.citrusErrors.isEmpty()) {
                        ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
                        if (citrusError != null)
                            showDialog(citrusError.message);
                        else
                            showDialog("Please try again later.");
                    } else if (errorModel.message != null)
                        showDialog(errorModel.message);
                } else
                    showDialog("Please try again later.");
            }
            super.onPostExecute(str);
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

    private class GroupAndBroadcastTaskOnServer extends AsyncTask<String, String, String> {
        String groupUUID;
        String displayName;
        String groupType;
        List<String> usersList;
        List<String> groupAdmins;
        String groupOwner;
        ProgressDialog progressDialog = null;
        String groupDiscription;
        int requestType = -1;
        String fileId;
        String[] urlss = null;

        public GroupAndBroadcastTaskOnServer(String groupUUID, String displayName, String groupType, List<String> usersList, List<String> groupAdmins,
                                             String groupOwner, String groupDiscription, int type) {
            this.groupUUID = groupUUID;
            this.displayName = displayName;
            this.groupType = groupType;
            this.usersList = usersList;
            this.groupAdmins = groupAdmins;
            this.groupOwner = groupOwner;
            this.groupDiscription = groupDiscription;
            this.requestType = type;
        }

        public GroupAndBroadcastTaskOnServer(String groupUUID, String displayName, List<String> usersList, String groupDiscription, int type) {
            this.groupUUID = groupUUID;
            this.displayName = displayName;
            this.usersList = usersList;
            this.groupDiscription = groupDiscription;
            this.requestType = type;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(EsiaChatContactsScreen.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
            GroupChatServerModel model = new GroupChatServerModel();
            urlss = urls;
            model.setDisplayName(displayName);
            if (!usersList.isEmpty()) {
                if (requestType == Constants.SHARED_ID_CREATE || (isSharedIDUpdate && requestType == Constants.BROADCAST_LIST_UPDATE)) {
                    if (isSharedIDUpdate && usersList.contains(SharedPrefManager.getInstance().getUserName())) {
                        usersList.remove(SharedPrefManager.getInstance().getUserName());
                        model.setAdminUserSet(usersList);
                    } else
                        model.setAdminUserSet(usersList);

                } else
                    model.setMemberUserSet(usersList);
            }
            String urlInfo = "update";
            switch (requestType) {
                case Constants.SHARED_ID_CREATE:
//{"userName":"919717098492_p5domain","broadcastGroupName":"shrd3","domainName":"p5domain","displayName":"shared id 3","description":"my shared id 3"}				
                    if (groupOwner != null && !groupOwner.equals("")) {
                        model.setUserName(groupOwner);
                    } else
                        model.setUserName(iPrefManager.getUserName());
                    model.setDomainName(iPrefManager.getUserDomain());
                    if (groupDiscription != null)
                        model.setDescription(groupDiscription);
                    else
                        model.setDescription("");
                    if (urls != null && urls[0] != null && !urls[0].equals("")) {
                        fileId = urls[0];
                        model.setFileId(urls[0]);
                    }
                    // this will be unique id
                    String uniqueName = displayName.trim().toLowerCase();
                    uniqueName = uniqueName.replaceAll("[^A-Za-z0-9 ]", "");
                    if (uniqueName.contains("-"))
                        uniqueName = uniqueName.replace('-', '_');
                    if (uniqueName.contains(" "))
                        uniqueName = uniqueName.replace(' ', '_');
                    groupUUID = uniqueName + "_" + iPrefManager.getUserId() + "_" + (new Random()).nextInt(99999);
                    model.setBroadcastGroupName(groupUUID);
                    urlInfo = "/tiger/rest/sharedid/create";
                    break;
                case Constants.GROUP_USER_CHAT_CREATE:
                    model.setType(groupType);
                    if (groupOwner != null && !groupOwner.equals("")) {
                        model.setUserName(groupOwner);
                    } else
                        model.setUserName(iPrefManager.getUserName());
                    if (groupAdmins != null && !groupAdmins.isEmpty())
                        model.setAdminUserSet(groupAdmins);
                    model.setDomainName(iPrefManager.getUserDomain());
                    model.setDescription(groupDiscription);
                    if (urls != null && urls[0] != null && !urls[0].equals("")) {
                        fileId = urls[0];
                        model.setFileId(urls[0]);
                    }
                    // this will be unique id
                    uniqueName = displayName.trim().toLowerCase();
                    uniqueName = uniqueName.replaceAll("[^A-Za-z0-9 ]", "");
                    if (uniqueName.contains("-"))
                        uniqueName = uniqueName.replace('-', '_');
                    if (uniqueName.contains(" "))
                        uniqueName = uniqueName.replace(' ', '_');
                    model.setGroupName(uniqueName + "_" + iPrefManager.getUserId() + "_" + System.currentTimeMillis());
//				model.setGroupName(UUID.randomUUID().toString());
                    urlInfo = "/tiger/rest/group/create";
                    break;
                case Constants.GROUP_USER_CHAT_INVITE:
                    model.setUserName(iPrefManager.getUserName());
                    model.setGroupName(groupUUID);
                    urlInfo = "/tiger/rest/group/update";
                    break;
                case Constants.BROADCAST_LIST_CRATE:
                    model.setUserName(iPrefManager.getUserName());
                    model.setDomainName(iPrefManager.getUserDomain());
                    if (urls != null && urls[0] != null && !urls[0].equals("")) {
                        fileId = urls[0];
                        model.setFileId(urls[0]);
                    }
//				model.setBroadcastGroupName(groupUUID);
                    uniqueName = displayName.trim().toLowerCase();
                    uniqueName = uniqueName.replaceAll("[^A-Za-z0-9 ]", "");
                    if (uniqueName.contains("-"))
                        uniqueName = uniqueName.replace('-', '_');
                    if (uniqueName.contains(" "))
                        uniqueName = uniqueName.replace(' ', '_');
                    model.setBroadcastGroupName(uniqueName + "_" + iPrefManager.getUserId() + "_" + System.currentTimeMillis());
//				model.setBroadcastGroupName(UUID.randomUUID().toString());//displayName + "_" + iPrefManager.getUserId() + "_" +System.currentTimeMillis());
                    model.setDescription(groupDiscription);
                    urlInfo = "/tiger/rest/bcgroup/create";
                    break;
                case Constants.BROADCAST_LIST_UPDATE:
                    if (isSharedIDUpdate) {

                    }
                    model.setUserName(iPrefManager.getUserName());
                    model.setBroadcastGroupName(groupUUID);
                    urlInfo = "/tiger/rest/bcgroup/update";
                    break;
            }
//			if(isForCreateGroup){
//				model.setDomainName(iPrefManager.getUserDomain());
//				model.setDescription(groupDiscription);
//				if(urls!=null && urls[0]!=null && !urls[0].equals(""))
//					model.setFileId(urls[0]);
//			}
//				List<String> adminUserSet = new ArrayList<String>();
//				model.setAdminUserSet(adminUserSet);
            String JSONstring = new Gson().toJson(model);

            DefaultHttpClient client1 = new DefaultHttpClient();
            String line = "";
            String responseMsg = "";
            Log.d(TAG, "serverUpdateCreateGroupInfo request: " + JSONstring);

//			if(isForCreateGroup)
//				urlInfo = "create";

            HttpPost httpPost = new HttpPost(Constants.SERVER_URL + urlInfo);
            httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
            HttpResponse response = null;
            try {
                httpPost.setEntity(new StringEntity(JSONstring));
                try {
                    response = client1.execute(httpPost);
                    final int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        HttpEntity entity = response.getEntity();
                        //					    System.out.println("SERVER RESPONSE STRING: " + entity.getContent());
                        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));

                        while ((line = rd.readLine()) != null) {
                            responseMsg = responseMsg + line;
                            Log.d(TAG, "serverUpdateCreateGroupInfo response: " + line);
                        }
//						showDialog(line,"Ok");
                    }
                    //else
//						showDialog("Network error in add participant.","Ok");
                } catch (ClientProtocolException e) {
                    Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:" + e.toString());
                } catch (IOException e) {
                    Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:" + e.toString());
                }

            } catch (UnsupportedEncodingException e1) {
                Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution UnsupportedEncodingException:" + e1.toString());
            } catch (Exception e) {
                Log.d(TAG, "serverUpdateCreateGroupInfo during HttpPost execution Exception:" + e.toString());
            }
            return responseMsg;
        }

        @Override
        protected void onPostExecute(String response) {

            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (response != null && response.contains("error")) {
                Gson gson = new GsonBuilder().create();
                ErrorModel errorModel = null;
                try {
                    errorModel = gson.fromJson(response, ErrorModel.class);
                } catch (Exception e) {
                }
                if (errorModel != null) {
                    if (errorModel.citrusErrors != null
                            && !errorModel.citrusErrors.isEmpty()) {
                        ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
                        if (citrusError != null)
                            showDialog(citrusError.message);
                        else
                            showDialog("Please try again later.");
                    } else if (errorModel.message != null)
                        showDialog(errorModel.message);
                } else
                    showDialog("Please try again later.");
            } else {
                if (requestType == Constants.GROUP_USER_CHAT_CREATE || requestType == Constants.BROADCAST_LIST_CRATE) {
                    if (service != null) {
                        if (requestType == Constants.GROUP_USER_CHAT_CREATE) {
                            Gson gson = new GsonBuilder().create();

                            GroupResopnseModel grsModel = gson.fromJson(response, GroupResopnseModel.class);
                            if (grsModel != null) {
                                groupUUID = grsModel.groupName;
                            }
//							SharedPrefManager.getInstance().saveGroupDisplayName(groupUUID, displayName);
                            String userDisplayName = SharedPrefManager.getInstance().getDisplayName();
                            String userFileID = SharedPrefManager.getInstance().getUserFileId(SharedPrefManager.getInstance().getUserName());
                            //THis is fall back code to recover the add member at time of invite
                            if (groupOwner != null && groupOwner.equals(SharedPrefManager.getInstance().getUserName())) {
                                SharedPrefManager.getInstance().saveGroupInfo(groupUUID, SharedPrefManager.GROUP_ACTIVE_INFO, true);
                                SharedPrefManager.getInstance().saveUserGroupInfo(groupUUID, groupOwner, SharedPrefManager.GROUP_OWNER_INFO, true);
                                SharedPrefManager.getInstance().saveUserGroupInfo(groupUUID, groupOwner, SharedPrefManager.GROUP_ACTIVE_INFO, true);
                                if (service != null) {
                                    String membersCount = "50";
                                    if (usersList != null && !usersList.isEmpty())
                                        membersCount = String.valueOf(usersList.size());
                                    service.sendGroupOwnerTaskMessage(userDisplayName, userFileID, groupOwner, groupUUID, displayName, groupDiscription, fileId, membersCount, XMPPMessageType.atMeXmppMessageTypeNewCreateGroup);
                                    if (usersList != null && !usersList.isEmpty())
                                        for (String member : usersList)
                                            service.sendGroupTaskMessage(userDisplayName, userFileID, groupOwner, member, groupUUID, displayName, groupDiscription, fileId, String.valueOf(usersList.size()), XMPPMessageType.atMeXmppMessageTypeNewCreateGroup);
                                    if (groupAdmins != null && !groupAdmins.isEmpty())
                                        for (String member : groupAdmins)
                                            service.sendGroupTaskMessage(userDisplayName, userFileID, groupOwner, member, groupUUID, displayName, groupDiscription, fileId, String.valueOf(usersList.size()), XMPPMessageType.atMeXmppMessageTypeNewCreateGroup);
                                }
                            } else {
                                if (service != null) {
                                    String membersCount = "50";
                                    if (usersList != null && !usersList.isEmpty())
                                        membersCount = String.valueOf(usersList.size());
                                    service.sendGroupOwnerTaskMessage(userDisplayName, userFileID, groupOwner, groupUUID, displayName, groupDiscription, fileId, membersCount, XMPPMessageType.atMeXmppMessageTypeNewCreateGroup);
                                    if (usersList != null && !usersList.isEmpty())
                                        for (String member : usersList)
                                            service.sendGroupTaskMessage(userDisplayName, userFileID, groupOwner, member, groupUUID, displayName, groupDiscription, fileId, String.valueOf(usersList.size()), XMPPMessageType.atMeXmppMessageTypeNewCreateGroup);
                                    if (groupAdmins != null && !groupAdmins.isEmpty())
                                        for (String member : groupAdmins)
                                            service.sendGroupTaskMessage(userDisplayName, userFileID, groupOwner, member, groupUUID, displayName, groupDiscription, fileId, String.valueOf(usersList.size()), XMPPMessageType.atMeXmppMessageTypeNewCreateGroup);
                                }
                            }
                        } else {
                            Gson gson = new GsonBuilder().create();
                            GroupResopnseModel grsModel = gson.fromJson(response, GroupResopnseModel.class);
                            if (grsModel != null) {
                                groupUUID = grsModel.broadcastGroupName;
                            }
                            final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                            iPrefManager.saveBroadCastName(groupUUID, displayName);
                            iPrefManager.saveBroadCastDisplayName(groupUUID, displayName);
                            for (String addedUser : usersList)
                                service.inviteUserInRoom(groupUUID, displayName, groupDiscription, addedUser, null);
                        }
                    }
                    Intent intent = new Intent(EsiaChatContactsScreen.this, HomeScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else if (requestType == Constants.SHARED_ID_CREATE) {
                    //Update shared ID data.
                    SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                    LoginResponseModel.BroadcastGroupDetail sharedid = new LoginResponseModel.BroadcastGroupDetail();
                    JSONObject jsonobj;
                    try {
                        jsonobj = new JSONObject(response);
                        sharedid.broadcastGroupMemberId = 0;
                        if (jsonobj.has("broadcastGroupName") && jsonobj.getString("broadcastGroupName").toString().trim().length() > 0) {
                            sharedid.broadcastGroupName = jsonobj.getString("broadcastGroupName").toString();
                        } else
                            sharedid.broadcastGroupName = groupUUID + "_" + iPrefManager.getUserDomain();
                        sharedid.displayName = displayName;
                        if (groupDiscription != null)
                            sharedid.description = groupDiscription;
                        else
                            sharedid.description = "";
                        sharedid.userName = iPrefManager.getUserName();
                        sharedid.userDisplayName = iPrefManager.getDisplayName();
                        if (fileId != null)
                            sharedid.fileId = fileId;
                        if (usersList != null)//Here usersList contains admin set
                            sharedid.adminUserSet = usersList;
                        if (HomeScreen.sharedIDData != null)
                            HomeScreen.sharedIDData.add(sharedid);
                        //Save Data in preferences
                        if (iPrefManager.getSharedIDDisplayName(sharedid.broadcastGroupName) == null) {
                            iPrefManager.saveSharedIDDisplayName(sharedid.broadcastGroupName, sharedid.displayName);
                            iPrefManager.setSharedIDContact(sharedid.broadcastGroupName, true);
                            if (sharedid.fileId != null)
                                iPrefManager.saveSharedIDFileId(sharedid.broadcastGroupName, sharedid.fileId);
                        }
                        //Update Shared Id'd to add on top of contact list
                        ContentValues contentvalues = new ContentValues();
                        //Shared ID name is saved in username field
                        contentvalues.put(DatabaseConstants.USER_NAME_FIELD, sharedid.broadcastGroupName);
                        contentvalues.put(DatabaseConstants.VOPIUM_FIELD, Integer.valueOf(1));
                        if (sharedid.broadcastGroupName != null)
                            contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD, sharedid.broadcastGroupName);
                        else
                            contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD, (new Random()).nextInt());
                        int id = sharedid.broadcastGroupName.hashCode();
                        if (id < -1)
                            id = -(id);
                        contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD, Integer.valueOf(id));
                        contentvalues.put(DatabaseConstants.RAW_CONTACT_ID, Integer.valueOf(id));
                        //Shared ID Display name is saved in Display name field
                        contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, Constants.SHARED_ID_START_STRING + sharedid.broadcastGroupName);
                        contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD, Integer.valueOf(0));
                        contentvalues.put(DatabaseConstants.DATA_ID_FIELD, Integer.valueOf("5"));
                        contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
                        contentvalues.put(DatabaseConstants.STATE_FIELD, Integer.valueOf(0));
                        contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, "9999999999");
                        DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, contentvalues);
                        HomeScreen.refreshContactList = true;
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    //Update information to Other domain members for the update.
                    //Create JSON here for group update info.
                    JSONObject finalJSONbject = new JSONObject();
                    String number = iPrefManager.getUserPhone();
                    if (number.contains("-"))
                        number = number.substring(number.indexOf('-') + 1);
                    try {
                        //For User Info
                        finalJSONbject.put("userName", iPrefManager.getUserName());
                        finalJSONbject.put("displayname", iPrefManager.getDisplayName());
                        if (iPrefManager.getUserFileId(iPrefManager.getUserName()) != null)
                            finalJSONbject.put("fileId", iPrefManager.getUserFileId(iPrefManager.getUserName()));
                        //For Shared ID Info
                        finalJSONbject.put("sharedIDName", sharedid.broadcastGroupName);
                        finalJSONbject.put("sharedIDDisplayName", sharedid.displayName);
                        if (sharedid.fileId != null)
                            finalJSONbject.put("sharedIDFileID", sharedid.fileId);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String json = finalJSONbject.toString();
                    Log.i(TAG, "Final JSON :  " + json);
//						json = json.replace("\"", "&quot;");
                    if (service != null)
                        service.sendSpecialMessageToAllDomainMembers(iPrefManager.getUserDomain() + "-system", json, XMPPMessageType.atMeXmppMessageTypeSharedIDCreated);
                    json = null;
                    setResult(1001, null);
                    finish();
//						Intent intent = new Intent(EsiaChatContactsScreen.this, SharedIDScreen.class);
//						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//								| Intent.FLAG_ACTIVITY_CLEAR_TASK
//								| Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(intent);
                } else if (isSharedIDUpdate && requestType == Constants.BROADCAST_LIST_UPDATE) {
                    if (isSharedIDUpdate && usersList.contains(SharedPrefManager.getInstance().getUserName())) {
                        usersList.remove(SharedPrefManager.getInstance().getUserName());
                    }
                    HomeScreen.updateAdminSetForSharedID(groupUUID, usersList);
                    //Update information to Other domain members for the update.
                    //Create JSON here for group update info.
                    //Update shared ID data.
                    SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
                    LoginResponseModel.BroadcastGroupDetail sharedid = new LoginResponseModel.BroadcastGroupDetail();
                    sharedid.broadcastGroupMemberId = 0;
                    sharedid.broadcastGroupName = groupUUID;
                    sharedid.displayName = displayName;
                    if (groupDiscription != null)
                        sharedid.description = groupDiscription;
                    else
                        sharedid.description = "";
                    sharedid.userName = iPrefManager.getUserName();
                    sharedid.userDisplayName = iPrefManager.getDisplayName();
                    if (fileId != null)
                        sharedid.fileId = fileId;
                    if (usersList != null)//Here usersList contains admin set
                        sharedid.adminUserSet = usersList;
                    JSONObject finalJSONbject = new JSONObject();
                    String number = iPrefManager.getUserPhone();
                    if (number.contains("-"))
                        number = number.substring(number.indexOf('-') + 1);
                    try {
                        //For User Info
                        finalJSONbject.put("userName", iPrefManager.getUserName());
                        finalJSONbject.put("displayname", iPrefManager.getDisplayName());
                        if (iPrefManager.getUserFileId(iPrefManager.getUserName()) != null)
                            finalJSONbject.put("fileId", iPrefManager.getUserFileId(iPrefManager.getUserName()));
                        //For Shared ID Info
                        finalJSONbject.put("sharedIDName", sharedid.broadcastGroupName);
                        finalJSONbject.put("sharedIDDisplayName", sharedid.displayName);
                        if (sharedid.fileId != null)
                            finalJSONbject.put("sharedIDFileID", sharedid.fileId);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    String json = finalJSONbject.toString();
                    Log.i(TAG, "Final JSON :  " + json);
//						json = json.replace("\"", "&quot;");
                    if (service != null)
                        service.sendSpecialMessageToAllDomainMembers(iPrefManager.getUserDomain() + "-system", json, XMPPMessageType.atMeXmppMessageTypeSharedIDUpdated);
                    json = null;
                    Toast.makeText(EsiaChatContactsScreen.this, "Shared ID update successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    if (service != null) {
                        StringBuffer members = new StringBuffer();
                        for (String addedUser : usersList) {
                            if (members.toString().length() > 0)
                                members.append(',');
                            members.append(addedUser);
                        }
                        //Create Json here for group update info.
                        JSONObject finalJSONbject = new JSONObject();
                        try {
                            finalJSONbject.put("displayName", displayName);
                            if (groupDiscription != null)
                                finalJSONbject.put("description", groupDiscription);
                            if (urlss != null && urlss.length > 0 && urlss[0] != null && !urlss[0].equals(""))
                                finalJSONbject.put("fileId", urlss[0]);
                            if (members.toString().length() > 0) {
                                finalJSONbject.put("Members", members.toString());
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        String json = finalJSONbject.toString();
//						 json = json.replace("\"", "&quot;");
                        for (String addedUser : usersList) {
                            if (!SharedPrefManager.getInstance().isUserInvited(addedUser) || !SharedPrefManager.getInstance().isDomainAdmin())
                                service.inviteUserInRoom(groupUUID, displayName, "", addedUser, json);
                        }
                        json = null;
                    }
                }
//				finish();
                Intent intent = new Intent(EsiaChatContactsScreen.this, GroupProfileScreen.class);
                setResult(RESULT_OK, intent);
                finish();
            }
            super.onPostExecute(response);
        }
    }

    @Override
    public void notifyConnectionChange() {
        // TODO Auto-generated method stub

    }

    private void updateRow(String userName, String status, String userDisplayName) {
        int row_count = contactList.getChildCount();
        for (int i = 0; i < row_count; i++) {
            View view = contactList.getChildAt(i);
            ImageView imgv = (ImageView) view.findViewById(R.id.contact_icon);
            ImageView def_imgv = (ImageView) view.findViewById(R.id.contact_icon_default);
            if (((String) def_imgv.getTag()).equalsIgnoreCase(userName)) {
                //Update the row.
                if (adapter != null)
                    adapter.setProfilePic(imgv, def_imgv, "", userName);
                if (userDisplayName != null)
                    ((TextView) view.findViewById(R.id.id_contact_name)).setText(userDisplayName);
                if (status != null)
                    ((TextView) view.findViewById(R.id.id_contact_status)).setText(status);
            }
        }
    }

    @Override
    public void notifyProfileUpdate(final String userName, final String status) {
        // TODO Auto-generated method stub
//		if (onForeground && contactList != null)
//			runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				updateRow(userName, status);
//			}
//			});
    }

    @Override
    public void notifyProfileUpdate(String userName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void notifyProfileUpdate(final String userName, final String status, final String userDisplayName) {
        // TODO Auto-generated method stub
        if (onForeground && contactList != null)
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    updateRow(userName, status, userDisplayName);
                }
            });
    }

    //=======================================================================================================================
    public class GetAllSGUserList extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog = null;
        SharedPrefManager sharedPrefManager;
        String domain_name = null;

        public GetAllSGUserList() {
            sharedPrefManager = SharedPrefManager.getInstance();
            domain_name = sharedPrefManager.getUserDomain();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(EsiaChatContactsScreen.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            DefaultHttpClient client1 = new DefaultHttpClient();

            String URL = Constants.SERVER_URL + "/tiger/rest/user/directory?domainName=" + domain_name + "&type=all" + "&pg=1&limit=1000";
            HttpPost httpPost = new HttpPost(URL);
//				 HttpParams httpParameters = new BasicHttpParams();
//				// Set the timeout in milliseconds until a connection is established.
//				// The default value is zero, that means the timeout is not used. 
//				int timeoutConnection = 30000;
//				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//				// Set the default socket timeout (SO_TIMEOUT) 
//				// in milliseconds which is the timeout for waiting for data.
//				int timeoutSocket = 40000;
//				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//				client1.setParams((org.apache.http.params.HttpParams) httpParameters);
            httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
            HttpResponse response = null;

            try {
                try {
                    response = client1.execute(httpPost);
                    final int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) { //new1
                        HttpEntity entity = response.getEntity();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                        String line = "";
                        String str = "";
                        while ((line = rd.readLine()) != null) {

                            str += line;
                        }
                        System.out.println("Full Data => " + str);
                        if (str != null && !str.equals("")) {
                            Gson gson = new GsonBuilder().create();
                            LoginResponseModel loginObj = gson.fromJson(str, LoginResponseModel.class);
                            if (loginObj != null) {
                                if (loginObj.directoryUserSet != null) {
                                    if (DBWrapper.getInstance().isTableExists(DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS))
                                        DBWrapper.getInstance().deleteTable(DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS);
                                    for (UserResponseDetail userDetail : loginObj.directoryUserSet) {
                                        if (userDetail.activationDate != null && !userDetail.activationDate.equals("")) {
                                            sharedPrefManager.saveUserInvited(userDetail.userName, false);
                                        } else
                                            sharedPrefManager.saveUserInvited(userDetail.userName, true);
//											String number = DBWrapper.getInstance().getContactNumber(userDetail.userName);
//											if(number!=null && !number.equals("")){
//												DBWrapper.getInstance().updateAtMeContactStatus(number);
//												continue;
//												}	
                                        ContentValues contentvalues = new ContentValues();
                                        contentvalues.put(DatabaseConstants.USER_NAME_FIELD, userDetail.userName);
                                        contentvalues.put(DatabaseConstants.VOPIUM_FIELD, Integer.valueOf(1));
                                        contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD, userDetail.mobileNumber);
                                        int id = userDetail.userName.hashCode();
                                        if (id < -1)
                                            id = -(id);
                                        contentvalues.put(DatabaseConstants.NAME_CONTACT_ID_FIELD, Integer.valueOf(id));
                                        contentvalues.put(DatabaseConstants.RAW_CONTACT_ID, Integer.valueOf(id));
                                        if (userDetail.name == null || (userDetail.name != null && (userDetail.name.equals("") || userDetail.name.equalsIgnoreCase("user"))))
                                            contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, "Superchatter");
                                        else
                                            contentvalues.put(DatabaseConstants.CONTACT_NAMES_FIELD, userDetail.name);

                                        contentvalues.put(DatabaseConstants.CONTACT_TYPE_FIELD, userDetail.type);

                                        String activationDate = userDetail.activationDate == null ? "" : userDetail.activationDate;

                                        //contentvalues.put(DatabaseConstants.USER_ACTIVATION_DATE, activationDate);
                                        contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD, Integer.valueOf(0));
                                        contentvalues.put(DatabaseConstants.DATA_ID_FIELD, Integer.valueOf("5"));
                                        contentvalues.put(DatabaseConstants.PHONE_NUMBER_TYPE_FIELD, "1");
                                        contentvalues.put(DatabaseConstants.STATE_FIELD, Integer.valueOf(0));
                                        contentvalues.put(com.superchat.data.db.DatabaseConstants.CONTACT_COMPOSITE_FIELD, userDetail.mobileNumber);
                                        if (!userDetail.userName.equalsIgnoreCase(sharedPrefManager.getUserName())) {
                                            long value = DBWrapper.getInstance()
                                                    .insertInDB(DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS,
                                                            contentvalues);
                                            System.out.println("Inserted with : " + value);
                                        }
                                        if (userDetail.userName.equalsIgnoreCase(sharedPrefManager.getUserName())) {
                                            if (userDetail.name == null || (userDetail.name != null && (userDetail.name.equals("") || userDetail.name.equalsIgnoreCase("user"))))
                                                sharedPrefManager.saveDisplayName("Superchatter");
                                            else
                                                sharedPrefManager.saveDisplayName(userDetail.name);
                                        }
                                        if (userDetail.name == null || (userDetail.name != null && (userDetail.name.equals("") || userDetail.name.equalsIgnoreCase("user"))))
                                            sharedPrefManager.saveUserServerName(userDetail.userName, "Superchatter");
                                        else
                                            sharedPrefManager.saveUserServerName(userDetail.userName, userDetail.name);
//										if(userDetail.currentStatus!=null)
//											sharedPrefManager.saveUserStatusMessage(userDetail.userName, userDetail.currentStatus);
//										if(userDetail.department!=null)
//											sharedPrefManager.saveUserDepartment(userDetail.userName, userDetail.department);
//										if(userDetail.designation!=null)
//											sharedPrefManager.saveUserDesignation(userDetail.userName, userDetail.designation);
//										if(userDetail.gender!=null){
//											sharedPrefManager.saveUserGender(userDetail.userName, userDetail.gender);
//											Log.i(TAG, "userName : "+userDetail.userName+", gender : "+userDetail.gender);
//										}
                                        if (userDetail.imageFileId != null) {
                                            sharedPrefManager.saveUserFileId(userDetail.userName, userDetail.imageFileId);
//											if(userDetail.imageFileId!=null && !userDetail.imageFileId.equals("")){
//												Message msg = new Message();
//												Bundle data = new Bundle();
//												data.putString("TaskMessage",userDetail.imageFileId);
//												msg.setData(data);
//												mainTask.sendMessage(msg);
//											}
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (ClientProtocolException e) {
                    Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:" + e.toString());
                } catch (IOException e) {
                    Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution ClientProtocolException:" + e.toString());
                }
            } catch (Exception e) {
                Log.d("HomeScreen", "serverUpdateCreateGroupInfo during HttpPost execution Exception:" + e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
            if (str != null && str.contains("error")) {
                Gson gson = new GsonBuilder().create();
                ErrorModel errorModel = gson.fromJson(str, ErrorModel.class);
                if (errorModel != null) {
                    if (errorModel.citrusErrors != null
                            && !errorModel.citrusErrors.isEmpty()) {
                        ErrorModel.CitrusError citrusError = errorModel.citrusErrors.get(0);
                        if (citrusError != null && citrusError.code.equals("20019")) {
                            showDialog(citrusError.message);
                        } else
                            showDialog(citrusError.message);
                    } else if (errorModel.message != null)
                        showDialog(errorModel.message);
                } else
                    showDialog("Please try again later.");
            } else {
                //Success case show adapter here.
                initOrCreateAdapter(SCREEN_TYPE);
            }
            super.onPostExecute(str);
        }
    }
    //==========================================================
}
