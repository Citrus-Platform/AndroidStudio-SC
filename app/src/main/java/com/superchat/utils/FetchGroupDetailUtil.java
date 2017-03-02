package com.superchat.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.superchat.SuperChatApplication;
import com.superchat.model.BroadCastDetailsModel;
import com.superchat.model.GroupChatMetaInfo;
import com.superchat.model.GroupDetailsModel;
import com.superchat.model.LoginResponseModel;
import com.superchat.ui.HomeScreen;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.superchat.utils.Constants.KEY_GROUP_BROADCAST;
import static com.superchat.utils.Constants.KEY_GROUP_NORMAL;

/**
 * Created by Motobeans on 2/3/2017.
 */

public class FetchGroupDetailUtil {

    private static final String TAG = "FetchGroupDetailUtil";
    public static void getServerGroupProfile(final String groupUUID, final boolean isBroadCast) {

        final SharedPrefManager iChatPref = SharedPrefManager.getInstance();

        AsyncHttpClient client = new AsyncHttpClient();
        String path = "";
        String query = groupUUID;
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (isBroadCast)
            path = Constants.SERVER_URL + "/tiger/rest/bcgroup/detail?groupName=" + query + "&nameNeeded=true";
        else
            path = Constants.SERVER_URL + "/tiger/rest/group/detail?groupName=" + query + "&nameNeeded=true";
        client = SuperChatApplication.addHeaderInfo(client, true);
        client.get(path,
                null, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        Log.d(TAG, "AsyncHttpClient onStart: ");
                    }

                    @Override
                    public void onSuccess(int arg0, String arg1) {
                        Log.d(TAG, "AsyncHttpClient onSuccess: "
                                + arg1);
                        if (arg1 == null)
                            return;

                        Gson gson = new GsonBuilder().create();
                        if (isBroadCast) {
                            BroadCastDetailsModel objUserModel = gson.fromJson(arg1, BroadCastDetailsModel.class);
                            if (arg1 == null || arg1.contains("error") || objUserModel == null) {
                                return;
                            }

                            if (objUserModel != null) {
                                if (objUserModel.displayName != null && !objUserModel.displayName.equals("")) {
                                    iChatPref.saveBroadCastDisplayName(groupUUID, objUserModel.displayName);
                                }
                                if (objUserModel.description != null && !objUserModel.description.equals("")) {
                                    iChatPref.saveUserStatusMessage(groupUUID, objUserModel.description);
                                }
                                if (objUserModel.fileId != null && !objUserModel.fileId.equals("")) {
                                    iChatPref.saveUserFileId(groupUUID, objUserModel.fileId);
                                }
                            }
                        } else if (arg1 != null) {
                            GroupDetailsModel objUserModel = gson.fromJson(arg1, GroupDetailsModel.class);
                            LoginResponseModel.GroupDetail groupDetail = gson.fromJson(arg1, LoginResponseModel.GroupDetail.class);
                            if(groupDetail != null && HomeScreen.groupsData != null) {
                                groupDetail.memberType = "MEMBER";
                                HomeScreen.groupsData.add(groupDetail);
                            }
                            if (arg1 == null || arg1.contains("error") || objUserModel == null) {
                                return;
                            }

                            if (objUserModel != null) {
                                try{
                                    String groupName = objUserModel.groupName;
                                    String groupModeType = objUserModel.mode;
                                    String groupMode = "";
                                    if (groupModeType != null && groupModeType.equalsIgnoreCase("broadcast")){
                                        groupMode = KEY_GROUP_BROADCAST;
                                    } else {
                                        groupMode = KEY_GROUP_NORMAL;
                                    }


                                    // Save Meta info of group
                                    {
                                        GroupChatMetaInfo groupChatMetaInfo = new GroupChatMetaInfo();
                                        groupChatMetaInfo.setBroadCastActive(groupMode);
                                        SharedPrefManager.getInstance().setSubGroupMetaData(groupUUID, groupChatMetaInfo);
                                    }
                                } catch(Exception e){

                                }
                                if (objUserModel.displayName != null && !objUserModel.displayName.equals("")) {
                                    iChatPref.saveGroupDisplayName(groupUUID, objUserModel.displayName);
                                }
                                if (objUserModel.type != null && objUserModel.type.equals("public")) {
                                    iChatPref.saveGroupTypeAsPublic(groupUUID, true);
                                }
                                if (objUserModel.description != null && !objUserModel.description.equals("")) {
                                    iChatPref.saveUserStatusMessage(groupUUID, objUserModel.description);
                                }
                                if (objUserModel.fileId != null && !objUserModel.fileId.equals("")) {
                                    iChatPref.saveUserFileId(groupUUID, objUserModel.fileId);
                                }
                                if (objUserModel.ownerDisplayName != null && !objUserModel.ownerDisplayName.equals("")) {
                                    iChatPref.saveUserServerName(objUserModel.userName, objUserModel.ownerDisplayName);
                                }

                                boolean isMemberAddAllowed = false;
                                if (objUserModel.adminUserSet != null) {
                                    for (String item : objUserModel.adminUserSet) {
                                        for (String str : objUserModel.activatedUserSet) {
                                            if (item.contains(SharedPrefManager.getInstance().getUserName())) {
                                                isMemberAddAllowed = true;
                                                iChatPref.saveUserGroupInfo(groupUUID, SharedPrefManager.getInstance().getUserName(), SharedPrefManager.GROUP_ADMIN_INFO, true);
                                            }
                                        }
                                    }
                                }
                                if (!isMemberAddAllowed)
                                    iChatPref.saveUserGroupInfo(groupUUID, SharedPrefManager.getInstance().getUserName(), SharedPrefManager.GROUP_ADMIN_INFO, false);

                            }
                        }
                        super.onSuccess(arg0, arg1);
                    }

                    @Override
                    public void onFailure(Throwable arg0, String arg1) {
                        super.onFailure(arg0, arg1);
                        Log.d(TAG, "AsyncHttpClient onFailure: " + arg1);
                    }
                });
    }

}
