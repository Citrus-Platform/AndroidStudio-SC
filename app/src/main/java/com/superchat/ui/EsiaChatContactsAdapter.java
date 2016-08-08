package com.superchat.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.daasuu.bl.BubbleLayout;
import com.daasuu.bl.BubblePopupHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.R;
import com.superchat.SuperChatApplication;
import com.superchat.data.beans.PhotoToLoad;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.helper.UtilGlobal;
import com.superchat.interfaces.interfaceInstances;
import com.superchat.model.ErrorModel;
import com.superchat.retrofit.api.RetrofitRetrofitCallback;
import com.superchat.retrofit.request.model.UserAdminRequest;
import com.superchat.retrofit.response.model.UserAdminResponse;
import com.superchat.task.ImageLoaderWorker;
import com.superchat.utils.ColorGenerator;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.ProfilePicDownloader;
import com.superchat.utils.SharedPrefManager;
import com.superchat.widgets.RoundedImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

//import com.superchat.widgets.DontPressWithParentLayout;

public class EsiaChatContactsAdapter extends SimpleCursorAdapter implements interfaceInstances {
    public final static String TAG = "EsiaChatContactsAdapter";


    private static final String KEY_SUPER_ADMIN = "domainAdmin";
    private static final String KEY_SUPER_SUB_ADMIN = "domainSubAdmin";

    SharedPrefManager prefManager = SharedPrefManager.getInstance();

    Activity activity;
    Context context;
    int layout;
    public ExecutorService executorService;
    private boolean isEditableContact = false;
    int check;
    int screenType;
    private static HashMap<String, Boolean> checkedTagMap = new HashMap<String, Boolean>();
    ArrayList<String> selectedUserList = new ArrayList<String>();
    private HashMap<String, String> hmAdmins = new HashMap<>();

    public int totalChecked;

    /**
     * Bubble Layout to show when user will click on 3 menu dots
     * created by : Munish Thakur
     **/
    BubbleLayout bubbleLayout;
    PopupWindow popupWindow;

    public EsiaChatContactsAdapter(Activity activity, Context context1, int i, Cursor cursor, String as[], int ai[], int j, int screenType) {
        super(context1, i, cursor, as, ai, j);
        this.activity = activity;
        context = context1;
        this.screenType = screenType;
        layout = i;
        executorService = Executors.newFixedThreadPool(5);
        totalChecked = 0;
        checkedTagMap.clear();

        mDrawableBuilder = TextDrawable.builder()
                .beginConfig().toUpperCase()
                .endConfig()
                .round();
    }

    public HashMap<String, Boolean> getSelectedItems() {
        return checkedTagMap;
    }

    public void setSelectedItems(ArrayList<String> users) {
        selectedUserList = users;
        for (String user : users) {
            checkedTagMap.put(user, true);
        }
    }

    public void setItems(String user, boolean flg) {
        checkedTagMap.put(user, flg);
    }

    public void removeSelectedItems() {
        checkedTagMap.clear();
    }

    public void setEditableContact(boolean isEdit) {
        this.isEditableContact = isEdit;
    }

    public boolean isEditable() {
        return this.isEditableContact;
    }

    public int getCheckedCounts() {
        return totalChecked;
    }

    private void displayImage(ImageView imageview, String s, boolean flag) {
        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(s);
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

    public void bindView(View view, Context context1, Cursor cursor) {
        super.bindView(view, context1, cursor);
        final ViewHolder viewholder = (ViewHolder) view.getTag();

        viewholder.userNames = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
        String s = cursor.getString(cursor.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
        viewholder.contentType = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_TYPE_FIELD));
        viewholder.displayName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));
        viewholder.voipumValue = cursor.getString(cursor.getColumnIndex(DatabaseConstants.VOPIUM_FIELD));
        String s2 = cursor.getString(cursor.getColumnIndex(DatabaseConstants.IS_FAVOURITE_FIELD));
        String status = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_COMPOSITE_FIELD));
        if(status != null)
            System.out.println("[status - ]"+status);
        //viewholder.activationDate = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_ACTIVATION_DATE));
        viewholder.displayNameView.setText(viewholder.displayName);

        // Necessary to call because it populates HashMap which contains
        populateHMIfSuperSubAdmin(viewholder.userNames, viewholder.contentType);

        if (screenType == Constants.MEMBER_DELETE) {
            viewholder.iCheckBox.setVisibility(View.GONE);
            viewholder.ivOverFlowMenuMembers.setVisibility(View.VISIBLE);
            /*{
                viewholder.removeMemberView.setVisibility(View.VISIBLE);
                if (!SharedPrefManager.getInstance().isUserExistence(viewholder.userNames))
                    viewholder.removeMemberView.setImageResource(R.drawable.iconactivatemember);
                else
                    viewholder.removeMemberView.setImageResource(R.drawable.icondeactivatemember);
            }*/
        } else if (isEditableContact) {
            if (EsiaChatContactsScreen.SCREEN_TYPE == Constants.GROUP_USER_SELECTED_LIST) {
                viewholder.crossView.setVisibility(ImageView.VISIBLE);
                viewholder.iCheckBox.setVisibility(View.GONE);
            } else {
                viewholder.crossView.setVisibility(ImageView.GONE);
                viewholder.iCheckBox.setVisibility(View.VISIBLE);
                //			if(viewholder.iCheckBox.isChecked())
                //				checkedTagMap.put(viewholder.userNames,true);
                boolean isChecked = false;
                Object obj = checkedTagMap.get(viewholder.userNames);
                if (obj != null) {
                    isChecked = checkedTagMap.get(viewholder.userNames);
                } else {
                    checkedTagMap.put(viewholder.userNames, isChecked);
                }

                viewholder.iCheckBox.setChecked(isChecked);
                viewholder.iCheckBox.setTag(viewholder.userNames);
            }
            //viewholder.removeMemberView.setVisibility(View.GONE);
        } else {
            //viewholder.removeMemberView.setVisibility(View.GONE);
            viewholder.iCheckBox.setVisibility(View.GONE);
        }
        viewholder.imageDefault.setTag(viewholder.userNames);
        setProfilePic(viewholder.image, viewholder.imageDefault, viewholder.displayName, viewholder.userNames);

        boolean isSuperAdmin = prefManager.isDomainAdminORSubAdmin();
        boolean isCurrentUserAdmin = isSuperAdmin(viewholder.userNames, viewholder.contentType);
        boolean isCurrentUserSubAdmin = isSuperSubAdmin(viewholder.userNames, viewholder.contentType);

        if(isCurrentUserAdmin) {
            viewholder.userStatusView.setText("Owner");
        } else if(isCurrentUserSubAdmin) {
            viewholder.userStatusView.setText("Admin");
        } else {
            viewholder.userStatusView.setText("");
        }

        final boolean isCurrentUserSuperOrSubAdmin = (isCurrentUserAdmin || isCurrentUserSubAdmin);
        final boolean isUserInvited = prefManager.isUserInvited(viewholder.userNames);
        if((isCurrentUserSuperOrSubAdmin && isUserInvited) && !(isSuperAdmin)){
            viewholder.ivOverFlowMenuMembers.setVisibility(View.INVISIBLE);
        } else {
            viewholder.ivOverFlowMenuMembers.setVisibility(View.VISIBLE);
        }

//        viewholder.image.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProfileUserScreen(viewholder.userNames, viewholder.displayName);
//            }
//        });
//        viewholder.imageDefault.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showProfileUserScreen(viewholder.userNames, viewholder.displayName);
//            }
//        });
        view.setOnClickListener(viewholder.onCheckeClickListener);
//        view.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if(isCurrentUserSuperOrSubAdmin){
////                    showProfileUserScreen(viewholder.userNames, viewholder.displayName);
////                } else {
////                    showEditUserScreen(viewholder.userNames, viewholder.displayName);
////                }
//
//            }
//        });
    }

    public View newView(Context context1, Cursor cursor, ViewGroup viewgroup) {
        View view = LayoutInflater.from(context).inflate(layout, null);
        final ViewHolder viewholder = new ViewHolder();
        viewholder.ivOverFlowMenuMembers = (ImageView) view.findViewById(R.id.ivOverFlowMenuMembers);
        viewholder.image = (ImageView) view.findViewById(R.id.contact_icon);
        viewholder.imageDefault = (ImageView) view.findViewById(R.id.contact_icon_default);
        viewholder.userStatusView = (TextView) view.findViewById(R.id.id_contact_status);
        viewholder.displayNameView = (TextView) view.findViewById(R.id.id_contact_name);
        viewholder.crossView = (ImageView) view.findViewById(R.id.id_cross);
        //viewholder.removeMemberView = (ImageView) view.findViewById(R.id.id_remove_member);
        viewholder.iCheckBox = (CheckBox) view.findViewById(R.id.contact_sel_box);
        if (screenType == Constants.MEMBER_DELETE) {
            ((CheckBox) view.findViewById(R.id.id_owner_choose)).setVisibility(CheckBox.GONE);
        }else{
            viewholder.displayNameView.setOnClickListener(viewholder.onCheckeClickListener);
        }

        //		if (isEditableContact){
        //viewholder.removeMemberView.setOnClickListener(viewholder.onCheckeClickListener);
        viewholder.crossView.setOnClickListener(viewholder.onCheckeClickListener);
        viewholder.image.setOnClickListener(viewholder.onCheckeClickListener);

        viewholder.crossView.setOnClickListener(viewholder.onCheckeClickListener);
        viewholder.iCheckBox.setOnClickListener(viewholder.onCheckeClickListener);
        //view.setOnClickListener(viewholder.onCheckeClickListener);
        viewholder.ivOverFlowMenuMembers.setOnClickListener(viewholder.onCheckeClickListener);
        //		}
        viewholder.id = cursor.getString(cursor.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
        view.setTag(viewholder);

        return view;
    }

    private void populateHMIfSuperSubAdmin(final String userName, final String contentType){
        if (contentType != null && contentType.trim().equalsIgnoreCase(KEY_SUPER_SUB_ADMIN)) {
            hmAdmins.put(userName, null);
        }
    }

    private boolean isSuperSubAdmin(final String userName, final String contentType) {
        boolean isSuperAdmin = false;
        if((hmAdmins != null) && hmAdmins.containsKey(userName)){
            isSuperAdmin = true;
        }

        return isSuperAdmin;
    }


    private boolean isSuperAdmin(final String userName, final String contentType) {
        boolean isSuperAdmin = false;
        if((contentType != null) && contentType.equalsIgnoreCase(KEY_SUPER_ADMIN)){
            isSuperAdmin = true;
        }

        return isSuperAdmin;
    }


    private void makeRemoveSuperAdmin(String userNames, String displayName, String contentType){
        if (isSuperSubAdmin(userNames, contentType))
            showDialogSuperAdmin("Remove Super Admin",
                    "Are you sure you want to remove user from Super Admin?",
                    userNames, contentType);
        else{
            AddRemoveSuperAdmin(userNames, contentType);
        }
    }


    private void activateDeactivateUser(String userNames, String displayName){
        if (SharedPrefManager.getInstance().isUserExistence(userNames))
            showDialog("Deactivate Member", "This member may be a Group Admin of one or more groups. You should appoint another Group Admin for those groups before removing this member", userNames);
        else
            showDialog("Activate Member", "Do you want to activate " + displayName + ".", userNames);
    }

    private void showProfileUserScreen(String userNames, String displayName){
        Intent intent1 = new Intent(SuperChatApplication.context, ProfileScreen.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CHAT_USER_NAME, userNames);
        bundle.putString(Constants.CHAT_NAME, displayName);
        intent1.putExtras(bundle);
        ((EsiaChatContactsScreen) context).startActivity(intent1);
    }


    private void showEditUserScreen(String userNames, String displayName){
        Intent intent1 = new Intent(SuperChatApplication.context, ProfileScreen.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CHAT_USER_NAME, userNames);
        bundle.putString(Constants.CHAT_NAME, displayName);
        bundle.putBoolean("MANAGE_MEMBER_BY_ADMIN", true);
        intent1.putExtras(bundle);
        ((EsiaChatContactsScreen) context).startActivity(intent1);
        /*if (screenType == Constants.MEMBER_DELETE)
            ((EsiaChatContactsScreen) context).finish();*/
    }

    private void showChatScreen(String userNames, String displayName){
        Intent intent = new Intent(SuperChatApplication.context, ChatListScreen.class);
        intent.putExtra(DatabaseConstants.CONTACT_NAMES_FIELD, displayName);
        intent.putExtra(DatabaseConstants.USER_NAME_FIELD, userNames);
        intent.putExtra("is_vopium_user", true);
        ((EsiaChatContactsScreen) context).startActivity(intent);
    }

    public class ViewHolder {
        String id;
        TextView displayNameView;
        TextView userStatusView;
        //ImageView removeMemberView;
        ImageView crossView;
        ImageView ivOverFlowMenuMembers;
        String userStatus;
        String userNames;
        //String activationDate;
        String contentType;
        String displayName;
        String voipumValue;
        ImageView imageDefault;
        ImageView image;
        CheckBox iCheckBox;

        ViewHolder() {

        }

        private OnClickListener onCheckeClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isIdInSwitchFound = false;
                int id = v.getId();
                switch (id) {
                    case R.id.contact_icon:
                    case R.id.ivOverFlowMenuMembers: {
                        isIdInSwitchFound = true;
                        MenuPopup(v, userNames, displayName, contentType);
                        return;
                    }
                }

                if(!isIdInSwitchFound) {
                    if (screenType == Constants.MEMBER_DELETE) {
//						if(voipumValue!=null && voipumValue.equals("2")){
//							
//						}else 
                        if (v.getId() == R.id.id_remove_member) {
//							showDialog("Remove Member","Do you want to remove "+displayName+".",userNames);
                            activateDeactivateUser(userNames, displayName);
                        } else {
//                            showEditUserScreen(userNames, displayName);
                            isIdInSwitchFound = true;
                            MenuPopup(v, userNames, displayName, contentType);
                        }
                    } else if (isEditableContact) {
                        if (v.getId() == R.id.id_cross && selectedUserList != null) {
                            checkedTagMap.put(userNames, false);
                            selectedUserList.remove(userNames);
                            Cursor cursor1 = DBWrapper.getInstance().getEsiaSelectedContacts(selectedUserList);
                            if (selectedUserList.isEmpty()) {
                                swapCursor(null);
                                notifyDataSetChanged();
                            } else {
                                swapCursor(cursor1);
                                notifyDataSetChanged();
                            }
                            return;
                        }
                        if (iCheckBox != null) {
                            if (v.getId() != R.id.contact_sel_box) {
                                iCheckBox.setChecked(!checkedTagMap.get(userNames));
                            }
                            checkedTagMap.put(userNames, !checkedTagMap.get(userNames));
                            if (checkedTagMap.get(userNames))
                                totalChecked++;
                            else
                                totalChecked--;
                            ((EsiaChatContactsScreen) context).itemCountView.setText(totalChecked + " " + context.getString(R.string.selected));
                            ((EsiaChatContactsScreen) context).allSelectCheckBox.setChecked(false);
                        }
                    } else {
                        showChatScreen(userNames, displayName);
                    }
                }
            }
        };
    }

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;

    public void setProfilePic(ImageView view, ImageView view_default, String displayName, String userName) {
        if (userName.equals("view_member_stats")) {
            view_default.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
            view.setImageResource(R.drawable.iconinvite);
            return;
        }
        String groupPicId = SharedPrefManager.getInstance().getUserFileId(userName); // 1_1_7_G_I_I3_e1zihzwn02
        android.graphics.Bitmap bitmap = SuperChatApplication.getBitmapFromMemCache(groupPicId);
        if (bitmap != null) {
            view_default.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
            RoundedImageView img = (RoundedImageView) view;
            img.setImageBitmap(bitmap);
        } else if (groupPicId != null && !groupPicId.equals("") && !groupPicId.equals("clear")) {
            String profilePicUrl = groupPicId + ".jpg";//AppConstants.media_get_url+
            File file = Environment.getExternalStorageDirectory();
            String filename = file.getPath() + File.separator + "SuperChat/" + profilePicUrl;
            File file1 = new File(filename);
            if (file1.exists()) {
//				CompressImage compressImage = new CompressImage(context);
//				filename = compressImage.compressImage(filename);
//				view.setImageURI(Uri.parse(filename));
                view_default.setVisibility(View.INVISIBLE);
                view.setVisibility(View.VISIBLE);
                setThumb(view, filename, groupPicId);
//				view.setBackgroundDrawable(null);

            } else {
                {
                    view_default.setVisibility(View.INVISIBLE);
                    view.setVisibility(View.VISIBLE);
                    //			view.setImageResource(R.drawable.group_icon);
                    (new ProfilePicDownloader()).download(Constants.media_get_url + groupPicId + ".jpg", (RoundedImageView) view, null);
                }
            }
        } else {
//			if(SharedPrefManager.getInstance().getUserGender(userName).equalsIgnoreCase("female"))
//				view.setImageResource(R.drawable.female_default);
//			else
//				view.setImageResource(R.drawable.male_default);
            try {
                String name_alpha = String.valueOf(displayName.charAt(0));
                if (displayName.contains(" ") && displayName.indexOf(' ') < (displayName.length() - 1))
                    name_alpha += displayName.substring(displayName.indexOf(' ') + 1).charAt(0);
                TextDrawable drawable = mDrawableBuilder.build(name_alpha, mColorGenerator.getColor(displayName));
                view.setVisibility(View.INVISIBLE);
                view_default.setVisibility(View.VISIBLE);
                view_default.setImageDrawable(drawable);
                view_default.setBackgroundColor(Color.TRANSPARENT);
            } catch (Exception ex) {
                ex.printStackTrace();
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

    private void setThumb(ImageView imageViewl, String path, String groupPicId) {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inSampleSize = 2;
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeFile(path, bfo);
            bm = ThumbnailUtils.extractThumbnail(bm, 200, 200);
            bm = rotateImage(path, bm);
            bm = Bitmap.createScaledBitmap(bm, 200, 200, true);
        } catch (Exception ex) {

        }
        if (bm != null) {
            imageViewl.setImageBitmap(bm);
            SuperChatApplication.addBitmapToMemoryCache(groupPicId, bm);
        } else {
            try {
                imageViewl.setImageURI(Uri.parse(path));
            } catch (Exception e) {

            }
        }
    }

    private class MemberDeactivationTaskOnServer extends AsyncTask<String, String, String> {
        String objectUserName;
        ProgressDialog progressDialog = null;

        public MemberDeactivationTaskOnServer(String objectUserName) {
            this.objectUserName = objectUserName;


        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            final SharedPrefManager iPrefManager = SharedPrefManager.getInstance();
//			GroupChatServerModel model = new GroupChatServerModel();

//			model.setUserName(iPrefManager.getUserName());
//			if(isBroadCast)
//				model.setBroadcastGroupName(groupUUID);
//			else
//				model.setGroupName(groupUUID);
//			String JSONstring = new Gson().toJson(model);

            DefaultHttpClient client1 = new DefaultHttpClient();
            String line = "";
            String responseMsg = "";
//			Log.d(TAG, "serverUpdateCreateGroupInfo request:"+JSONstring);
            String urlInfo = "";
//			http://52.88.175.48/tiger/rest/user/deactivate?byUser=919717098492_p5domain&toUser=919910968484_p5domain (Both GET and POST are supported)
            if (SharedPrefManager.getInstance().isUserExistence(objectUserName))
                urlInfo = "/tiger/rest/user/deactivate?byUser=" + iPrefManager.getUserName() + "&toUser=" + objectUserName;
            else
                urlInfo = "/tiger/rest/user/activate?byUser=" + iPrefManager.getUserName() + "&toUser=" + objectUserName;
            HttpPost httpPost = new HttpPost(Constants.SERVER_URL + urlInfo);
            httpPost = SuperChatApplication.addHeaderInfo(httpPost, true);
//		         httpPost.setEntity(new UrlEncodedFormEntity(JSONstring));
            HttpResponse response = null;
            try {
//				httpPost.setEntity(new StringEntity(JSONstring));
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
                if (response != null && !response.equals("") && response.contains("success")) {
                    if (SharedPrefManager.getInstance().isUserExistence(objectUserName)) {
                        if (((EsiaChatContactsScreen) context).service != null)
                            ((EsiaChatContactsScreen) context).service.sendSpecialMessageToAllDomainMembers(SharedPrefManager.getInstance().getUserDomain() + "-system", objectUserName, XMPPMessageType.atMeXmppMessageTypeDeactivateUser);
                        //((EsiaChatContactsScreen)context).service.sendInfoMessage(objectUserName,"test",Message.XMPPMessageType.atMeXmppMessageTypeDeactivateUser);
                        //					 DBWrapper.getInstance().deleteContact(objectUserName);
                        SharedPrefManager.getInstance().saveUserExistence(objectUserName, false);
                    } else {
                        if (((EsiaChatContactsScreen) context).service != null)
                            ((EsiaChatContactsScreen) context).service.sendSpecialMessageToAllDomainMembers(SharedPrefManager.getInstance().getUserDomain() + "-system", objectUserName, XMPPMessageType.atMeXmppMessageTypeActivateUser);
                        SharedPrefManager.getInstance().saveUserExistence(objectUserName, true);
                    }
                    Intent intent = new Intent(context, HomeScreen.class);
//						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
//						((EsiaChatContactsScreen)context).startActivity(intent);
                    HomeScreen.refreshContactList = true;
                    //((EsiaChatContactsScreen) context).finish();
                }
                super.onPostExecute(response);
            }
        }
    }

    public void showDialog(String s) {
        final Dialog bteldialog = new Dialog(context);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(false);
        bteldialog.setContentView(R.layout.custom_dialog);
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        ((TextView) bteldialog.findViewById(R.id.id_ok)).setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                return false;
            }
        });
        bteldialog.show();
    }

    public void showDialog(final String title, final String s, final String userNames) {
        final Dialog bteldialog = new Dialog(context);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.custom_dialog_two_button);
        if (title != null) {
            ((TextView) bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
        }
        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);
        if (SharedPrefManager.getInstance().isUserExistence(userNames))
            ((TextView) bteldialog.findViewById(R.id.id_send)).setText("Deactivate");
        else
            ((TextView) bteldialog.findViewById(R.id.id_send)).setText("Activate");
        ((TextView) bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();
                new MemberDeactivationTaskOnServer(userNames).execute();
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


    private void AddRemoveSuperAdmin(final String userNames, final String contentType){
        try{
            final ProgressDialog progressDialog = ProgressDialog.show(context, "", "Loading. Please wait...", true);

            UserAdminRequest objUserAdmin = new UserAdminRequest();
            objUserAdmin.setUserName(userNames);

            Call call = null;

            final boolean isSuperAdmin = isSuperSubAdmin(userNames, contentType);
            if(isSuperAdmin) {
                call = objApi.getApi(context).removeAdmin(objUserAdmin);
            }
            else {
                call = objApi.getApi(context).makeAdmin(objUserAdmin);
            }

            progressDialog.show();
            call.enqueue(new RetrofitRetrofitCallback<UserAdminResponse>(context) {
                @Override
                protected void onResponseVoidzResponse(Call call, Response response) {
                }

                @Override
                protected void onResponseVoidzObject(Call call, UserAdminResponse response) {
                    if(response != null && response.getStatus() != null && response.getStatus().equalsIgnoreCase("success")){

                        if(isSuperAdmin){
                            hmAdmins.remove(userNames);
//                            DBWrapper.getInstance().updateUserAccess(userNames, "");
                            try {
                                EsiaChatContactsScreen parentActivity = ((EsiaChatContactsScreen) activity);
                                if (parentActivity != null && parentActivity != null) {
                                    parentActivity.addRemoveAdmin(userNames, false);
                                }
                            } catch(Exception e){

                            }
                        } else {
                            hmAdmins.put(userNames, null);
//                            DBWrapper.getInstance().updateUserAccess(userNames, "Admin");
                            try {
                                EsiaChatContactsScreen parentActivity = ((EsiaChatContactsScreen) activity);
                                if (parentActivity != null && parentActivity != null) {
                                    parentActivity.addRemoveAdmin(userNames, true);
                                }
                            } catch(Exception e){

                            }
                            objToast.makeToast(context, "Member has been promoted as SuperAdmin", UtilGlobal.MODE_RELEASE);
                        }
                    } else {
                        String errorMessage = response.getMessage() != null ? response.getMessage() : "Please try later";
                        showDialog(errorMessage);
                    }
                }

                @Override
                protected void common() {
                    progressDialog.cancel();
                    isPopshown = false;
                }
            });

        } catch(Exception e){

        }
    }

    public void showDialogSuperAdmin(final String title, final String s, final String userNames, final String contentType) {
        final Dialog bteldialog = new Dialog(context);
        bteldialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bteldialog.setCanceledOnTouchOutside(true);
        bteldialog.setContentView(R.layout.custom_dialog_two_button);
        if (title != null) {
            ((TextView) bteldialog.findViewById(R.id.id_dialog_title)).setText(title);
        }

        ((TextView) bteldialog.findViewById(R.id.id_dialog_message)).setText(s);

        final boolean isSuperSubAdmin = isSuperSubAdmin(userNames, contentType);
        if (isSuperSubAdmin)
            ((TextView) bteldialog.findViewById(R.id.id_send)).setText("Remove Admin");
        else
            ((TextView) bteldialog.findViewById(R.id.id_send)).setText("Make Admin");

        ((TextView) bteldialog.findViewById(R.id.id_send)).setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bteldialog.cancel();

                AddRemoveSuperAdmin(userNames, contentType);
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

    /**
     * This method will open up flow window for a particular users with all applicable options
     */

    public boolean cancelPopupMenu(){
        if(popupWindow != null && popupWindow.isShowing()){
            popupWindow.dismiss();
            isPopshown = false;
            return true;
        }
        return false;
    }

    boolean isPopshown;
    public void MenuPopup(View v, final String userNames, final String displayName, final String contentType) {
        if(popupWindow != null && isPopshown){
            popupWindow.dismiss();
            isPopshown = false;
            return;
        }
        bubbleLayout = (BubbleLayout) LayoutInflater.from(context).inflate(R.layout.inflate_members_menu_popup, null);
        popupWindow = BubblePopupHelper.create(context, bubbleLayout);

        if (bubbleLayout != null && popupWindow != null) {

                Cursor cursor = getCursor();

                int[] location = new int[2];
                v.getLocationInWindow(location);
//                popupWindow.showAtLocation(v, Gravity.CENTER_VERTICAL, location[0], v.getHeight() + location[1]);
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 50);
                popupWindow.setOutsideTouchable(false);

                LinearLayout llMemberAction_MakeSuperAdmin = (LinearLayout) bubbleLayout.findViewById(R.id.llMemberAction_MakeSuperAdmin);
                LinearLayout llMemberAction_RemoveSuperAdmin = (LinearLayout) bubbleLayout.findViewById(R.id.llMemberAction_RemoveSuperAdmin);
                LinearLayout llMemberAction_ReactivateUser = (LinearLayout) bubbleLayout.findViewById(R.id.llMemberAction_ReactivateUser);
                LinearLayout llMemberAction_DeactivateUser = (LinearLayout) bubbleLayout.findViewById(R.id.llMemberAction_DeactivateUser);
                LinearLayout llMemberAction_ViewProfile = (LinearLayout) bubbleLayout.findViewById(R.id.llMemberAction_ViewProfile);
                LinearLayout llMemberAction_EditProfile = (LinearLayout) bubbleLayout.findViewById(R.id.llMemberAction_EditProfile);
                LinearLayout llMemberAction_Message = (LinearLayout) bubbleLayout.findViewById(R.id.llMemberAction_Message);

                llMemberAction_MakeSuperAdmin.setOnClickListener(new MenuClickListerenr(userNames, displayName, contentType));
                llMemberAction_RemoveSuperAdmin.setOnClickListener(new MenuClickListerenr(userNames, displayName, contentType));
                llMemberAction_ReactivateUser.setOnClickListener(new MenuClickListerenr(userNames, displayName, contentType));
                llMemberAction_DeactivateUser.setOnClickListener(new MenuClickListerenr(userNames, displayName, contentType));
                llMemberAction_ViewProfile.setOnClickListener(new MenuClickListerenr(userNames, displayName, contentType));
                llMemberAction_EditProfile.setOnClickListener(new MenuClickListerenr(userNames, displayName, contentType));
                llMemberAction_Message.setOnClickListener(new MenuClickListerenr(userNames, displayName, contentType));

                // Check Cursor Values and show / Hide view accordingly
                {
                    boolean isUserInvited = prefManager.isUserInvited(userNames);
                    boolean isDomainAdmin = prefManager.isDomainAdmin();
                    boolean isDomainSubAdmin = prefManager.isDomainSubAdmin();

                    boolean isCurrentUserAdmin = isSuperAdmin(userNames, contentType);
                    boolean isCurrentUserSubAdmin = isSuperSubAdmin(userNames, contentType);

                    {
                        if (isCurrentUserSubAdmin) {
                            llMemberAction_MakeSuperAdmin.setVisibility(View.GONE);
                            llMemberAction_RemoveSuperAdmin.setVisibility(View.VISIBLE);
                        } else {
                            llMemberAction_MakeSuperAdmin.setVisibility(View.VISIBLE);
                            llMemberAction_RemoveSuperAdmin.setVisibility(View.GONE);
                        }

                        if (screenType == Constants.MEMBER_DELETE) {
                            if (!SharedPrefManager.getInstance().isUserExistence(userNames)) {
                                llMemberAction_ReactivateUser.setVisibility(View.VISIBLE);
                                llMemberAction_DeactivateUser.setVisibility(View.GONE);
                            } else {
                                llMemberAction_ReactivateUser.setVisibility(View.GONE);
                                llMemberAction_DeactivateUser.setVisibility(View.VISIBLE);
                            }
                        }
                    }


                    if(isDomainAdmin){

                        if(isUserInvited){
                            llMemberAction_ViewProfile.setVisibility(View.GONE);
                            llMemberAction_Message.setVisibility(View.GONE);
                        }
                    } else if(isDomainSubAdmin){

                        llMemberAction_MakeSuperAdmin.setVisibility(View.GONE);
                        llMemberAction_RemoveSuperAdmin.setVisibility(View.GONE);

                        boolean isCurrentUserSuperOrSubAdmin = (isCurrentUserAdmin || isCurrentUserSubAdmin);

                        if(isCurrentUserSuperOrSubAdmin){
                            llMemberAction_ReactivateUser.setVisibility(View.GONE);
                            llMemberAction_DeactivateUser.setVisibility(View.GONE);
                            llMemberAction_EditProfile.setVisibility(View.GONE);
                        } else {

                        }

                        if(isUserInvited){
                            llMemberAction_ViewProfile.setVisibility(View.GONE);
                            llMemberAction_Message.setVisibility(View.GONE);
                        }
                    } else {

                    }
                }
            }
        isPopshown = true;
    }

    class MenuClickListerenr implements OnClickListener {

        String displayName;
        String userNames;
        String contentType;

        public MenuClickListerenr(String userNames, String displayName, String contentType) {
            this.userNames = userNames;
            this.displayName = displayName;
            this.contentType = contentType;
        }

        @Override
        public void onClick(View v) {

            if(popupWindow != null && popupWindow.isShowing()){
                popupWindow.dismiss();
            }
            int id = v.getId();
            switch (id) {
                case R.id.llMemberAction_MakeSuperAdmin:
                case R.id.llMemberAction_RemoveSuperAdmin: {
                    makeRemoveSuperAdmin(userNames, displayName, contentType);
                    break;
                }
                case R.id.llMemberAction_ReactivateUser:
                case R.id.llMemberAction_DeactivateUser: {
                    activateDeactivateUser(userNames, displayName);
                    break;
                }
                case R.id.llMemberAction_ViewProfile: {
                    showProfileUserScreen(userNames, displayName);
                    break;
                }
                case R.id.llMemberAction_EditProfile: {
                    showEditUserScreen(userNames, displayName);
                    break;
                }
                case R.id.llMemberAction_Message: {
                    showChatScreen(userNames, displayName);
                    break;
                }
            }
        }
    }

}
