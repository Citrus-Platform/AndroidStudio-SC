package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.sdk.ChatService;
import com.chatsdk.org.jivesoftware.smack.XMPPConnection;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.CustomAppComponents.Activity.CustomAppCompatActivityViewImpl;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.model.ErrorModel;
import com.superchat.model.GroupChatMetaInfo;
import com.superchat.model.GroupChatServerModel;
import com.superchat.model.GroupChatXmppCaptionData;
import com.superchat.utils.AppUtil;
import com.superchat.utils.CompressImage;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.ProfilePicUploader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.UtilSetFont;
import com.superchat.widgets.MyriadSemiboldTextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public class CreateGroupScreen extends CustomAppCompatActivityViewImpl implements OnClickListener {
    //	ArrayList<String> inviters;
    private static final String TAG = "CreateGroupScreen";
    private String groupName;
    private String groupUUID;
    private String groupDiscription;
    ImageView cameraImageView;
    EditText groupNameView;
    EditText groupDiscriptionView;
    TextView nextButton;
    TextView cancelButton;
    Dialog picChooserDialog;
    ProfilePicUploader picUploader;
    ImageView groupIconView;
    Switch switchBroadcast;
    boolean isForGroupUpdate;
    boolean isForBroadCastUpdate;
    String groupFileId;
    private ChatService messageService;
    private XMPPConnection xmppConnection;
    private boolean isBroadcast;
    private boolean isChannel;
    private MyriadSemiboldTextView title;
    private RadioGroup radioGroup;
    private RadioButton radioGroupType;

    private ImageView ivGroupTypeOpen;
    private ImageView ivGroupTypeClosed;
    private LinearLayout checkBoxesGroupTypes;

    private ServiceConnection mMessageConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            messageService = ((ChatService.MyBinder) binder).getService();
            Log.d("Service", "Connected");
            xmppConnection = messageService.getconnection();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            xmppConnection = null;
            messageService = null;
        }
    };
    private boolean onForeground;
    String mode = Constants.KEY_GROUP_NORMAL;;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.create_group_screen);

        UtilSetFont.setFontMainScreen(this);

        ((ImageView) findViewById(R.id.id_back)).setOnClickListener(this);
        groupNameView = (EditText) findViewById(R.id.id_group_name);
        groupDiscriptionView = (EditText) findViewById(R.id.id_status_message);
        groupIconView = (ImageView) findViewById(R.id.id_group_icon);
        nextButton = (TextView) findViewById(R.id.id_next);
        title = (MyriadSemiboldTextView) findViewById(R.id.id_group_info_title);
        cancelButton = (TextView) findViewById(R.id.id_cancel);
        ivGroupTypeOpen = (ImageView) findViewById(R.id.ivGroupTypeOpen);
        ivGroupTypeClosed = (ImageView) findViewById(R.id.ivGroupTypeClosed);
        checkBoxesGroupTypes = (LinearLayout) findViewById(R.id.checkBoxesGroupTypes);

        switchBroadcast = (Switch) findViewById(R.id.switchBroadcast);
        switchBroadcast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mode = Constants.KEY_GROUP_BROADCAST;
                } else {
                    mode = Constants.KEY_GROUP_NORMAL;
                }
            }
        });

        LinearLayout subTitleOpenGroupView = (LinearLayout) findViewById(R.id.id_create_open_group_subtitle);
        LinearLayout subTitleCloseGroupView = (LinearLayout) findViewById(R.id.id_create_closed_group_subtitle);
        LinearLayout group_type = (LinearLayout) findViewById(R.id.id_group_type);
        TextView open_close = (TextView) findViewById(R.id.id_open_close_txt);
        TextView tvCreateGroupSelectionText = (TextView) findViewById(R.id.tvCreateGroupSelectionText);
        LinearLayout llEditGroupTypeInfo = (LinearLayout) findViewById(R.id.llEditGroupTypeInfo);

        ImageView xmppStatusView = (ImageView) findViewById(R.id.id_xmpp_status);
        cameraImageView = (ImageView) findViewById(R.id.id_group_camera_icon);
        cameraImageView.setOnClickListener(this);
        if (ChatService.xmppConectionStatus) {
            xmppStatusView.setImageResource(R.drawable.blue_dot);
        } else {
            xmppStatusView.setImageResource(R.drawable.red_dot);
        }
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        cancelButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        groupIconView.setOnClickListener(this);
        picChooserDialog = new Dialog(this);
        picChooserDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        picChooserDialog.setContentView(R.layout.pic_chooser_dialog);
        picChooserDialog.findViewById(R.id.id_remove).setVisibility(View.GONE);
        picChooserDialog.findViewById(R.id.id_camera).setOnClickListener(this);
        picChooserDialog.findViewById(R.id.id_gallery).setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isForGroupUpdate = extras.getBoolean(Constants.IS_GROUP_INFO_UPDATE, false);
            isBroadcast = extras.getBoolean(Constants.BROADCAST, false);
            isChannel = extras.getBoolean(Constants.CHANNEL_CREATION, false);
            if (isForGroupUpdate)
                nextButton.setText("Save");
            if (isBroadcast) {
//				groupNameView.setHint(getString(R.string.broadcast_hint));
//				if(isForGroupUpdate)
//					isForBroadCastUpdate = true;
////				groupIconView.setVisibility(View.GONE);
////				groupDiscriptionView.setVisibility(View.GONE);
////				addBroadCastMemberButton.setVisibility(View.VISIBLE);
//				groupName = extras.getString(Constants.GROUP_NAME, "");
//				groupUUID = extras.getString(Constants.GROUP_UUID, "");
//				groupFileId= extras.getString(Constants.GROUP_FILE_ID, "");
//				if(groupName !=null && groupName.trim().length() > 0)
//					groupDiscription = extras.getString(Constants.GROUP_DISCRIPTION, "Welcome to SuperChat group "+groupName+".");
//				if(isForGroupUpdate)
//					title.setText(getString(R.string.edit_broadcast));
//				else
//					title.setText(getString(R.string.create_broadcast));
//				
//				if(groupName != null && groupName.trim().length() > 0)
//					groupNameView.setText(groupName);
//				else
//					groupNameView.setHint(getString(R.string.broadcast_list_name));
//				if(groupDiscription != null && groupDiscription.trim().length() > 0)
//					groupDiscriptionView.setText(groupDiscription);
//				if(groupFileId != null && groupFileId.trim().length() > 0)
//					setProfilePic(groupIconView);
//				else
//					groupIconView.setImageResource(R.drawable.announce);
            } else {
                groupName = extras.getString(Constants.GROUP_NAME, "");
                groupUUID = extras.getString(Constants.GROUP_UUID, "");
                groupFileId = extras.getString(Constants.GROUP_FILE_ID, "");
                if (isChannel || SharedPrefManager.getInstance().isPublicGroup(groupUUID)) {
//					groupNameView.setHint(getString(R.string.channel_name));
                    groupDiscription = extras.getString(Constants.GROUP_DISCRIPTION, "Welcome to SuperChat Channel " + groupName + ".");
//					if(groupUUID != null && groupUUID.trim().length() > 0 && SharedPrefManager.getInstance().isGroupChat(groupUUID)){
//						title.setText(getString(R.string.edit_channel_title));
//					}else{
//						title.setText(getString(R.string.create_channel));
//						
//						}
                } else {
                    groupDiscription = extras.getString(Constants.GROUP_DISCRIPTION, "Welcome to SuperChat group " + groupName + ".");
                    if (groupUUID != null && groupUUID.trim().length() > 0 && SharedPrefManager.getInstance().isGroupChat(groupUUID))
                        title.setText(getString(R.string.edit_group_title));
                    else
                        title.setText(getString(R.string.create_group));
                }
                if (groupName != null && groupName.trim().length() > 0)
                    groupNameView.setText(groupName);
                if (groupUUID != null && groupUUID.trim().length() > 0 && SharedPrefManager.getInstance().isGroupChat(groupUUID)) {
                    title.setText(getString(R.string.edit_group_title));

                    tvCreateGroupSelectionText.setVisibility(View.GONE);
                    llEditGroupTypeInfo.setVisibility(View.VISIBLE);

                    radioGroup.setVisibility(View.GONE);
                    checkBoxesGroupTypes.setVisibility(View.GONE);
                    if (SharedPrefManager.getInstance().isPublicGroup(groupUUID)) {
                        open_close.setText(getString(R.string.open_gp));
                        ivGroupTypeOpen.setVisibility(View.VISIBLE);
                    } else {
                        open_close.setText(getString(R.string.close_gp));
                        ivGroupTypeClosed.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    title.setText(getString(R.string.create_group));
                }
//				else if(isChannel){
//					groupNameView.setHint(getString(R.string.channel_name));
//				}
                if (isChannel) {
                    group_type.setVisibility(View.VISIBLE);
                }

//                else if (isForGroupUpdate) {
//                    if (SharedPrefManager.getInstance().isPublicGroup(groupUUID)) {
//                        subTitleOpenGroupView.setVisibility(View.VISIBLE);
//                    } else {
//                        subTitleCloseGroupView.setVisibility(View.VISIBLE);
//                    }
//                }
//				if(isChannel)
//					subTitleOpenGroupView.setVisibility(View.VISIBLE);
//				else
//					subTitleCloseGroupView.setVisibility(View.VISIBLE);

                if (groupName != null && groupName.trim().length() > 0)
                    groupDiscriptionView.setText(groupDiscription);
                else {
//					groupDiscriptionView.setHint(getString(R.string.description));
                }
                if (groupFileId != null && groupFileId.trim().length() > 0)
                    setProfilePic(groupIconView);
//                else
//                    groupIconView.setImageResource(R.drawable.icongroupimage);
            }
        }
    }

    protected void onResume() {
        super.onResume();
        onForeground = true;
        bindService(new Intent(this, ChatService.class), mMessageConnection, Context.BIND_AUTO_CREATE);

        boolean isBroadCastMode = SharedPrefManager.getInstance().isGroupInBroadcastMode(groupUUID);
        switchBroadcast.setChecked(isBroadCastMode);
    }

    protected void onPause() {
        try {
            unbindService(mMessageConnection);
        } catch (Exception e) {
            // Just ignore that
            Log.d("MessageHistoryScreen", "Unable to un bind");
        }
        super.onPause();
    }

    private void setProfilePic(ImageView view) {
        String groupPicId = SharedPrefManager.getInstance().getUserFileId(groupUUID); // 1_1_7_G_I_I3_e1zihzwn02
        if (groupPicId != null && !groupPicId.equals("")) {
            String profilePicUrl = groupPicId;//AppConstants.media_get_url+
            if (!profilePicUrl.contains(".jpg"))
                profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+

            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            view.setTag(filename);
            File file1 = new File(filename);
            if (file1.exists()) {
                view.setImageURI(Uri.parse(filename));
                view.setBackgroundDrawable(null);
            }
        }
    }

    public void uploadPicture(final String imgPath) {
        String packetID = null;
        if (imgPath != null && imgPath.length() > 0) {
            try {
                String thumbImg = null;
//				if (messageService != null){
//					chatAdapter.setChatService(messageService);
//					thumbImg = MyBase64.encode(getByteArrayOfThumbnail(imgPath));
//					packetID = messageService.sendMediaMessage(senderName, "", imgPath,thumbImg,XMPPMessageType.atMeXmppMessageTypeGroupImage);
                picUploader = new ProfilePicUploader(this, null, true, notifyPhotoUploadHandler);
                picUploader.execute(imgPath, packetID, "", XMPPMessageType.atMeXmppMessageTypeGroupImage.name(), groupUUID);
//				}
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("" + ex.toString());
            }
        }
    }

    private final Handler notifyPhotoUploadHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
//	    	if(isForground)
            showDialog(getString(R.string.failed), getString(R.string.photo_upload_failed));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == HomeScreen.RESULT_OK)
                switch (requestCode) {
                    case AppUtil.POSITION_CAMERA_PICTURE:
                    case AppUtil.POSITION_GALLRY_PICTURE:
                        if (data != null && data.getData() != null) {
                            Uri uri = data.getData();
                            AppUtil.capturedPath1 = AppUtil.getPath(uri, this);
                        }
                        CompressImage compressImage = new CompressImage(this);
                        AppUtil.capturedPath1 = compressImage.compressImage(AppUtil.capturedPath1);
                        performCrop(AppUtil.PIC_CROP);
                        break;
                    case AppUtil.PIC_CROP:
                        String filePath = Environment.getExternalStorageDirectory()
                                + "/" + Constants.contentTemp + "/" + AppUtil.TEMP_PHOTO_FILE;

                        AppUtil.capturedPath1 = filePath;
                        Bitmap selectedImage = BitmapFactory.decodeFile(AppUtil.capturedPath1);
                        groupIconView.setImageBitmap(selectedImage);
                        groupIconView.setBackgroundDrawable(null);
                        groupIconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//					groupIconView.setImageURI(Uri.parse(AppUtil.capturedPath1));
                        uploadPicture(AppUtil.capturedPath1);
                        break;
                }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private void performCrop(byte resultCode) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            File file = new File(AppUtil.capturedPath1);
            Uri outputFileUri = Uri.fromFile(file);
//			System.out.println("----outputFileUri:" + outputFileUri);
            cropIntent.setDataAndType(outputFileUri, "image/*");
            cropIntent.putExtra("outputX", 600);
            cropIntent.putExtra("outputY", 600);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
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

    public void onCancelClick(View view) {
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_group_icon:
                String tmpImgId1 = null;
                if (picUploader != null && picUploader.getServerFileId() != null)
                    tmpImgId1 = picUploader.getServerFileId();
                if (picChooserDialog != null && !picChooserDialog.isShowing() && tmpImgId1 == null)
                    picChooserDialog.show();
                else if (AppUtil.capturedPath1 != null) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    File file = new File(AppUtil.capturedPath1);
                    Uri outputFileUri = Uri.fromFile(file);
                    intent.setDataAndType(outputFileUri, "image/*");
                    startActivity(intent);
                }

                break;
            case R.id.id_back:
            case R.id.id_cancel:
                finish();
                break;
//		case R.id.id_add_broadcast_member:
            case R.id.id_next:
                groupName = groupNameView.getText().toString().trim();
//			if(isBroadcast && groupName.equals("")){
//				groupName = getString(R.string.broadcast_list) + "##$^##" + System.currentTimeMillis();
//			}
                groupDiscription = groupDiscriptionView.getText().toString();
                if (groupName != null && !groupName.equals("")) {
                    if (isForGroupUpdate) {
                        String tmpImgId = null;
                        if (picUploader != null && picUploader.getServerFileId() != null)
                            tmpImgId = picUploader.getServerFileId();
                        if (!isBroadcast)
                            new GroupAndBroadcastTaskOnServer(groupUUID, groupName, null, groupDiscription, Constants.GROUP_USER_CHAT_INVITE).execute(tmpImgId);
                        else
                            new GroupAndBroadcastTaskOnServer(groupUUID, groupName, null, groupDiscription, Constants.BROADCAST_LIST_UPDATE).execute(tmpImgId);
//					 if(messageService!=null)
//						 messageService.updateGroupDisplayName(groupUUID,groupName);
//					SharedPrefManager.getInstance().saveUserStatusMessage(groupUUID, groupDiscription);
//					SharedPrefManager.getInstance().saveGroupDisplayName(groupUUID, groupName);
//					int state = SharedPrefManager.getInstance().getServerGroupState(groupUUID);
//					if(state!=GroupCreateTaskOnServer.SERVER_GROUP_NOT_CREATED && state!=GroupCreateTaskOnServer.SERVER_GROUP_CREATION_FAILED){
//						SharedPrefManager.getInstance().saveServerGroupState(groupUUID, GroupCreateTaskOnServer.SERVER_GROUP_NOT_UPDATED);
//						new GroupCreateTaskOnServer(groupUUID, groupName, null).execute(GroupCreateTaskOnServer.UPDATE_GROUP_REQUEST);
//					}
//					setResult(RESULT_OK, new Intent(this,GroupProfileScreen.class));
//					finish();
//					
                    } else {
                        //Select Domain, get selected radio button from radioGroup
                        String privacy_type = "public";
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioGroupType = (RadioButton) findViewById(selectedId);
                        if (R.id.radio_open == radioGroupType.getId())
                            privacy_type = "public";
                        else if (R.id.radio_closed == radioGroupType.getId())
                            privacy_type = "private";

                        Intent intent = new Intent(CreateGroupScreen.this, EsiaChatContactsScreen.class);
                        intent.putExtra(Constants.CHAT_TYPE, Constants.GROUP_USER_CHAT_CREATE);
                        intent.putExtra(Constants.GROUP_NAME, groupName);
                        intent.putExtra(Constants.GROUP_TYPE, privacy_type);
                        intent.putExtra(Constants.GROUP_DISCRIPTION, groupDiscription);
                        intent.putExtra(Constants.GROUP_MODE, mode);
                        if (isBroadcast) {
                            intent.putExtra(Constants.BROADCAST, true);
                        } else
                            intent.putExtra(Constants.CHANNEL_CREATION, isChannel);

                        if (picUploader != null && picUploader.getServerFileId() != null)
                            intent.putExtra(Constants.GROUP_FILE_ID, picUploader.getServerFileId());

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    if (isBroadcast)
                        showDialog(getString(R.string.enter_broadcast_name), getString(R.string.ok));
                    else if (isChannel)
                        showDialog(getString(R.string.enter_channel_name), getString(R.string.ok));
                    else
                        showDialog(getString(R.string.group_name_shouldnt_null), getString(R.string.ok));
                }
                break;
            case R.id.id_camera:
                AppUtil.clearAppData();
                AppUtil.openCamera(this, AppUtil.capturedPath1,
                        AppUtil.POSITION_CAMERA_PICTURE);
                picChooserDialog.cancel();
                break;
            case R.id.id_gallery:
                AppUtil.clearAppData();
                AppUtil.openImageGallery(this,
                        AppUtil.POSITION_GALLRY_PICTURE);
                picChooserDialog.cancel();
                break;
            case R.id.id_group_camera_icon:
                if (picChooserDialog != null && !picChooserDialog.isShowing())
                    picChooserDialog.show();
                break;
        }
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

    private class GroupAndBroadcastTaskOnServer extends AsyncTask<String, String, String> {
        String groupUUID;
        String displayName;
        List<String> usersList;
        ProgressDialog progressDialog = null;
        String groupDiscription;
        int requestType = -1;
        String[] urlss = null;

        public GroupAndBroadcastTaskOnServer(String groupUUID, String displayName, List<String> usersList, String groupDiscription, int type) {
            this.groupUUID = groupUUID;
            this.displayName = displayName;
            this.usersList = usersList;
            this.groupDiscription = groupDiscription;
            this.requestType = type;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(CreateGroupScreen.this, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
            GroupChatServerModel model = new GroupChatServerModel();

            model.setUserName(iPrefManager.getUserName());
            urlss = urls;

            model.setDisplayName(displayName);
            if (usersList != null && !usersList.isEmpty() && requestType != Constants.GROUP_USER_CHAT_CREATE)
                model.setMemberUserSet(usersList);
            if (groupDiscription != null && !groupDiscription.equals(""))
                model.setDescription(groupDiscription);
            String urlInfo = "update";
            switch (requestType) {
                case Constants.GROUP_USER_CHAT_CREATE:
                    model.setDomainName(iPrefManager.getUserDomain());
                    model.setDescription(groupDiscription);
                    model.setMode(mode);
                    if (urls != null && urls[0] != null && !urls[0].equals(""))
                        model.setFileId(urls[0]);
                    // this will be unique id

//				model.setGroupName(displayName.replace(' ', '_') + "_" + iPrefManager.getUserId() + "_" +System.currentTimeMillis());
                    urlInfo = "/tiger/rest/group/create";
                    break;
                case Constants.GROUP_USER_CHAT_INVITE:
                    model.setGroupName(groupUUID);
                    model.setMode(mode);
                    if (urls != null && urls[0] != null && !urls[0].equals(""))
                        model.setFileId(urls[0]);
                    urlInfo = "/tiger/rest/group/update";
                    break;
                case Constants.BROADCAST_LIST_CRATE:
                    model.setDomainName(iPrefManager.getUserDomain());
                    if (urls != null && urls[0] != null && !urls[0].equals(""))
                        model.setFileId(urls[0]);
//				model.setBroadcastGroupName(groupUUID);
//				model.setBroadcastGroupName(displayName.replace(' ', '_') + "_" + iPrefManager.getUserId() + "_" +System.currentTimeMillis());
                    model.setBroadcastGroupName(displayName + "_" + iPrefManager.getUserId() + "_" + System.currentTimeMillis());
                    model.setDescription(groupDiscription);
                    urlInfo = "/tiger/rest/bcgroup/create";
                    break;
                case Constants.BROADCAST_LIST_UPDATE:
                    model.setBroadcastGroupName(groupUUID);
                    if (urls != null && urls[0] != null && !urls[0].equals(""))
                        model.setFileId(urls[0]);
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
                            //Log.d(TAG, "serverUpdateCreateGroupInfo response: "+line);
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
                GroupChatXmppCaptionData data = new GroupChatXmppCaptionData();

                data.setDisplayName(displayName);

                if (groupDiscription != null)
                    data.setDescription(groupDiscription);
                if (urlss != null && urlss[0] != null && !urlss[0].equals(""))
                    data.setFileId(urlss[0]);


                // Save Meta info of group
                {
                    if (mode != null) {
                        GroupChatMetaInfo groupChatMetaInfo = new GroupChatMetaInfo();
                        groupChatMetaInfo.setBroadCastActive(mode);
                        SharedPrefManager.getInstance().setSubGroupMetaData(groupUUID, groupChatMetaInfo);

                        GroupChatMetaInfo.GroupPermissions permissionMode;
                        if (groupChatMetaInfo.isBroadCastActive()) {
                            permissionMode = GroupChatMetaInfo.GroupPermissions.SCGroupPermissionAllowedAdmins;
                        } else {
                            permissionMode = GroupChatMetaInfo.GroupPermissions.SCGroupPermissionAllowedAll;
                        }

                        data.setGroupPermissionType("" + permissionMode.ordinal());
/*

                            if(permissionMode != null) {
                                messageService.updateGroupType(groupUUID, displayName, permissionMode);
                            }
*/
                    }
                }

                if (messageService != null) {
                    String json = new Gson().toJson(data).toString();
                    messageService.updateGroupDisplayName(groupUUID, displayName, json);
                    json = null;
                }

                if (groupDiscription != null)
                    SharedPrefManager.getInstance().saveUserStatusMessage(groupUUID, groupDiscription);
                if (urlss != null && urlss[0] != null && !urlss[0].equals(""))
                    SharedPrefManager.getInstance().saveUserFileId(groupUUID, urlss[0]);
                if (requestType == Constants.BROADCAST_LIST_UPDATE)
                    SharedPrefManager.getInstance().saveBroadCastDisplayName(groupUUID, displayName);
                else
                    SharedPrefManager.getInstance().saveGroupDisplayName(groupUUID, displayName);
                if (requestType == Constants.BROADCAST_LIST_UPDATE) {
                    messageService.saveInfoMessage(displayName, groupUUID, "You updated broadcast info.", UUID.randomUUID().toString());
                }

                setResult(RESULT_OK, new Intent(CreateGroupScreen.this, GroupProfileScreen.class));
                finish();
            }

            super.onPostExecute(response);
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
}
