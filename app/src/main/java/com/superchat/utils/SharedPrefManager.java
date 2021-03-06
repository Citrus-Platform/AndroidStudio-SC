package com.superchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.superchat.SuperChatApplication;
import com.superchat.model.GroupChatMetaInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SharedPrefManager {

    private SharedPreferences pref;
    // editor for shared preference
    Editor editor;
    Context mContext;
    int PRIVATE_MODE = 0;
    public static final byte GROUP_ADMIN_INFO = 1;
    public static final byte GROUP_ACTIVE_INFO = 2;
    public static final byte GROUP_OWNER_INFO = 3;
    public static final byte PUBLIC_GROUP = 4;
    public static final byte PUBLIC_CHANNEL = 5;
    public static final byte BROADCAST_ADMIN_INFO = 1;
    public static final byte BROADCAST_ACTIVE_INFO = 2;
    public static final byte BROADCAST_OWNER_INFO = 3;

    public static final byte SNOOZE_OFF = 0;
    public static final byte SNOOZE_20_MINUTES = 1;
    public static final byte SNOOZE_1_HOUR = 2;
    public static final byte SNOOZE_2_HOURS = 3;
    public static final byte SNOOZE_4_HOURS = 4;
    public static final byte SNOOZE_8_HOURS = 5;
    public static final byte SNOOZE_24_HOURS = 6;

    public static final long SNOOZE_20_MINUTES_IN_MILLIS = (20 * 60 * 1000);
    public static final long SNOOZE_1_HOUR_IN_MILLIS = (60 * 60 * 1000);
    public static final long SNOOZE_2_HOURS_IN_MILLIS = (2 * 60 * 60 * 1000);
    public static final long SNOOZE_4_HOURS_IN_MILLIS = (4 * 60 * 60 * 1000);
    public static final long SNOOZE_8_HOURS_IN_MILLIS = (8 * 60 * 60 * 1000);
    public static final long SNOOZE_24_HOURS_IN_MILLIS = (24 * 60 * 60 * 1000);

    private final String PREF_NAME = "SuperChatPref";

    private final String FIRST_TIME_APP = "mode"; // For Frash User
    private final String USER_ID = "user_id";
    private final String USER_DOMAIN = "user_domain";
    private final String USER_Current_STATUS = "user_current_status";
    private final String CURRENT_MEMBER_TYPE = "current_member_type";
    private final String OWNED_DOMAIN = "owned_domain";
    private final String USER_DOMAIN_DISPLAY_NAME = "use_domain_display_name";
    private final String USER_GENDER = "user_gender";
    private final String USER_ORG_NAME = "org_name";
    private final String SIP_SERVER = "sip_address";
    private final String GROUP_SERVER_STATE = "group_server_state";
    private final String USER_SIP_PASSWORD = "user_sip_assword";
    private final String USER_PASSWORD = "user_password";
    private final String COUNTRY_CODE = "country_code";
    private final String GROUP_USERS = "Gp_Users";
    private final String BROADCAST_USERS = "Bds_Users";
    private final String GROUP_NAME = "Group_Name";
    private final String BROADCAST_NAME = "Broadcast_Name";
    private final String USER_NAME_ID = "user_name_id";
    private final String USER_DISPLAY_NAME = "name";
    private final String USER_STATUS_MESSAGE = "user_status_message";
    private final String USER_DESIGNATION = "user_designation";
    private final String USER_DEPARTMENT = "user_department";
    private final String BULLETIN_FILE_ID = "bulletin_file_id";
    private final String AUTH_STATUS = "status";
    private final String USER_PHONE = "mobile_number";
    private final String DOMAIN_TYPE = "domain_type";
    private final String USER_EMAIL = "email_id";
    private final String USER_FILE_ID = "user_file_id";
    private final String SHARED_ID_FILE_ID = "shared_id_file_id";
    private final String SG_FILE_ID = "sg_file_id";
    private final String SHARED_ID_DATA = "shared_id_data";
    private final String LAST_ONLINE = "last_online";
    private final String ACTIVATION_TIME = "activation_time";
    private final String USER_VARIFIED = "varified";
    private final String GROUP_DISPLAY_NAME = "group_display_name_";
    private final String BROADCAST_DISPLAY_NAME = "broadcast_display_name_";
    private final String SHARED_ID_DISPLAY_NAME = "shared_id_display_name_";
    private final String SHARED_ID_DEACTIVATED = "shared_id_deactivated";
    private final String MOBILE_VARIFIED = "mobile_varified";
    private final String MOBILE_REGISTERED = "mobile_registered";
    private final String PROFILE_ADDED = "profile_added";
    private final String USER_EPR_COMPLETE = "epr_complete";
    private final String USER_LOGED_OUT = "logout";
    private final String CHAT_COUNTER = "chat_counter";
    private final String BULLETIN_CHAT_COUNTER = "bulletin_chat_counter";
    private final String CONTACT_COUNTER = "contact_counter";
    private final String DNM_ACTIVATION = "dnm_activation";
    private final String DNC_ACTIVATION = "dnc_activation";
    private final String ISTYPING = "_istyping";
    private final String TYPING = "_typing";
    private final String MUTE_GROUP_OR_USER = "mute_group_or_user";
    private final String SNOOZE_INDEX = "snooze_index";
    private final String SNOOZE_START_TIME = "snooze_start_time";
    private final String BLOCKED_STATUS = "blocked_status";
    private final String BLOCK_ORDER = "block_order";
    private final String BLOCK_LIST = "block_list";
    private final String RECORDING = "_recording";
    private final String LISTENING = "_listening";
    private final String FIRST_TIME = "first_time";
    private final String ME_ACTIVATED = "me_activated";
    private final String USER_INVITED = "user_invited";
    private final String DEVICE_TOKEN = "device_token";
    private final String ALL_RECENT_USERS = "all_recent_users";
    private final String ALL_RECENT_DOMAINS = "all_recent_domains";
    private final String DOMAIN_ADMIN = "domain_admin";
    private final String DOMAIN_SUB_ADMIN = "domain_sub_admin";
    private final String SHARED_ID_CONTACT = "shared_id_contact";
    private final String PUBLIC_DOMAIN_ADMIN = "public_domain_admin";
    private final String DOMAIN_JOINED_COUNTS = "domain_joined_count";
    private final String DOMAIN_UNJOINED_COUNTS = "domain_unjoined_count";
    private final String GROUP_MEM_COUNT = "group_member_count";
    private final String CHANNEL_OWNER = "channel_owner";
    private final String APP_UPDATE = "app_update";
    private final String OTP_VERIFIED = "otp_verified";
    private final String ADMIN_REG = "admin_reg";
    private final String OTP_VERIFIED_TIME = "otp_verified_time";
    private final String SG_LIST_DATA = "sg_list_data";
    private final String CONTACT_SYNCHED = "contact_synched";
    private final String GROUP_LOADED = "group_loaded";
    private final String USER_SEL_NAV_INDEX = "sel_nav_index";

    private final String BROADCAST_FIRST_TIME_NAME = "broadcast_first_time_name";
    private final String BACKUP_FILE_ID = "backup_file_id";
    private final String BACKUP_OVER_WIFI = "backup_over_wifi";
    private final String BACKUP_SCHEDULE = "backup_schedule";
    private final String LAST_BACKUP_TIME = "last_backup_time";

    private final String BULLETIN_NEXT_URL = "bulletin_next_url";
    private final String SG_PASSWORD = "sg_password";
    private final String SG_USER_ID = "sg_userid";
    private final String SG_BACKUP_CHECKED = "sg_backup_checked";
    private final String SG_BULLETIN_LOADED = "sg_bulletin_loaded";


    private final String SYSTEM_MSG_FOR_SG = "system_msg_for_sg";


    private final String DATA_LOADED_FOR_SG = "data_loaded_for_sg";


    private final String GROUPS_FOR_SG = "groups_for_sg";


    private static SharedPrefManager sharedPrefManager;

    private SharedPrefManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SharedPrefManager getInstance() {
        if (sharedPrefManager == null && SuperChatApplication.context != null) {
            sharedPrefManager = new SharedPrefManager(SuperChatApplication.context);
        }
        return sharedPrefManager;
    }


    public void saveBroadcastFirstTimeName(String broadcast, String name) {
        if (name != null && !name.equals("")) {
            System.out.println("[Name = ]" + name);
            editor.putString(BROADCAST_FIRST_TIME_NAME + broadcast, name);
            editor.commit();
        }
    }

    public String getBroadcastFirstTimeName(String broadcast) {
        String value = pref.getString(BROADCAST_FIRST_TIME_NAME + broadcast, null);
        return value;
    }

    public void setProfileAdded(String userName, boolean flag) {
        editor.putBoolean(PROFILE_ADDED + userName, flag);
        editor.commit();
    }

    public void saveStatusDNM(String userName, boolean flag) {
        editor.putBoolean(DNM_ACTIVATION + userName, flag);
        editor.commit();
    }

    public void saveStatusDNC(String userName, boolean flag) {
        editor.putBoolean(DNC_ACTIVATION + userName, flag);
        editor.commit();
    }

    public void setMute(String userNameOrGroupName, boolean flag) {
        editor.putBoolean(MUTE_GROUP_OR_USER + userNameOrGroupName, flag);
        editor.commit();
    }

    public void setSnoozeIndex(String sg, int optionIndex) {
        editor.putInt(SNOOZE_INDEX + sg, optionIndex);
        editor.commit();
    }

    public void setSnoozeStartTime(String sg, long snoozeStartTime) {
        editor.putLong(SNOOZE_START_TIME + sg, snoozeStartTime);
        editor.commit();
    }

    public void setOTPVerified(boolean flag) {
        editor.putBoolean(OTP_VERIFIED, flag);
        editor.commit();
    }

    public void setAdminReg(boolean flag) {
        editor.putBoolean(ADMIN_REG, flag);
        editor.commit();
    }

    public void setOTPVerifiedTime(long time) {
        editor.putLong(OTP_VERIFIED_TIME, time);
        editor.commit();
    }

    public void setSGListData(String data) {
        editor.putString(SG_LIST_DATA, data);
        editor.commit();
    }

    public void setBlockStatus(String userName, boolean flag) {
        editor.putBoolean(BLOCKED_STATUS + userName, flag);
        editor.commit();
    }

    public void setSharedIDDeactivated(String shared_id, boolean flag) {
        editor.putBoolean(SHARED_ID_DEACTIVATED + shared_id, flag);
        editor.commit();
    }

    public void setMobileVerified(String mobileNumber, boolean flag) {
        System.out.println("<< mobileNumber : " + mobileNumber + ", verified = " + flag);
        editor.putBoolean(MOBILE_VARIFIED + mobileNumber, flag);
        editor.commit();
    }

    public void setSharedIDContact(String contact, boolean flag) {
        editor.putBoolean(SHARED_ID_CONTACT + contact, flag);
        editor.commit();
    }

    public void setAsDomainAdmin(String sg, boolean flag) {
        editor.putBoolean(DOMAIN_ADMIN + sg, flag);
        editor.commit();
    }

    public void setUpdateCheck(boolean flag) {
        editor.putBoolean(APP_UPDATE, flag);
        editor.commit();
    }

    public void setDomainAsPublic(String sg, boolean flag) {
        editor.putBoolean(PUBLIC_DOMAIN_ADMIN + sg, flag);
        editor.commit();
    }

    public void setDomainType(String type) {
        editor.putString(DOMAIN_TYPE, type);
        editor.commit();
    }

    public void setMobileRegistered(String mobileNumber, boolean flag) {
        editor.putBoolean(MOBILE_REGISTERED + mobileNumber, flag);
        editor.commit();
    }

    public boolean isUserSGSubAdmin(String user) {
        return pref.getBoolean(DOMAIN_SUB_ADMIN + user, false);
    }

    public void setUserSGSubAdmin(String user, boolean flag) {
        editor.putBoolean(DOMAIN_SUB_ADMIN + user, flag);
        editor.commit();
    }


    public boolean isUserGroupAdmin(String groupName) {
        String user = getUserName();
        String sg = getUserDomain();
        String key = sg + groupName + user;
        return pref.getBoolean(key, false);
    }

    public void setUserGroupAdmin(String groupName, String user, boolean flag) {
        String sg = getUserDomain();
        String key = sg + groupName + user;

        editor.putBoolean(key, flag);
        editor.commit();
    }

    public void setSubGroupMetaData(String groupName, GroupChatMetaInfo groupChatMetaInfo) {
        if (groupChatMetaInfo != null) {
            String sg = getUserDomain();
            String key = sg + groupName;

            String value = new Gson().toJson(groupChatMetaInfo);
            editor.putString(key, value);
            editor.commit();
        }
    }

    public GroupChatMetaInfo getSubGroupMetaData(String groupName) {
        GroupChatMetaInfo groupChatMetaInfo = null;
        if (groupName != null) {
            String sg = getUserDomain();
            String key = sg + groupName;

            String value = pref.getString(key, "");

            groupChatMetaInfo = new Gson().fromJson(value, GroupChatMetaInfo.class);
        }
        return groupChatMetaInfo != null ? groupChatMetaInfo : new GroupChatMetaInfo();
    }

    public boolean isGroupInBroadcastMode(String groupName){
        GroupChatMetaInfo groupChatMetaInfo = getSubGroupMetaData(groupName);
        return groupChatMetaInfo.isBroadCastActive();
    }

    public boolean isBackupCheckedForSG(String sg) {
        return pref.getBoolean(SG_BACKUP_CHECKED + sg, false);
    }

    public void setBackupCheckedForSG(String sg, boolean flag) {
        editor.putBoolean(SG_BACKUP_CHECKED + sg, flag);
        editor.commit();
    }

    public boolean isBulletinLoaded(String sg) {
        return pref.getBoolean(SG_BULLETIN_LOADED + sg, false);
    }

    public void setBulletinLoaded(String sg, boolean flag) {
        editor.putBoolean(SG_BULLETIN_LOADED + sg, flag);
        editor.commit();
    }

    public boolean isDataLoadedForSG(String sg) {
        return pref.getBoolean(DATA_LOADED_FOR_SG + sg, false);
    }

    public void setDataLoadedForSG(String sg, boolean flag) {
        System.out.println("Data Loaded for " + sg + " = " + flag);
        editor.putBoolean(DATA_LOADED_FOR_SG + sg, flag);
        editor.commit();
    }

    public boolean isUpdateCheckNeeded() {
        return pref.getBoolean(APP_UPDATE, true);
    }

    public boolean isMute(String userNameOrGroupName) {
        return pref.getBoolean(MUTE_GROUP_OR_USER + userNameOrGroupName, false);
    }

    public boolean isDNC(String userName) {
        return pref.getBoolean(DNC_ACTIVATION + userName, false);
    }

    public boolean isDNM(String userName) {
        return pref.getBoolean(DNM_ACTIVATION + userName, false);
    }

    public int getSnoozeIndex(String sg) {
        return pref.getInt(SNOOZE_INDEX + sg, SNOOZE_OFF);
    }

    public long getSnoozeStartTime(String sg) {
        if (getSnoozeIndex(sg) <= 0)
            return 0;
        return pref.getLong(SNOOZE_START_TIME + sg, System.currentTimeMillis());
    }

    public long getSnoozeExpiryTime(String sg) {
        if (getSnoozeIndex(sg) <= SNOOZE_OFF)
            return 0;
        switch (getSnoozeIndex(sg)) {
            case SNOOZE_20_MINUTES:
                return getSnoozeStartTime(sg) + SNOOZE_20_MINUTES_IN_MILLIS;
            case SNOOZE_1_HOUR:
                return getSnoozeStartTime(sg) + SNOOZE_1_HOUR_IN_MILLIS;
            case SNOOZE_2_HOURS:
                return getSnoozeStartTime(sg) + SNOOZE_2_HOURS_IN_MILLIS;
            case SNOOZE_4_HOURS:
                return getSnoozeStartTime(sg) + SNOOZE_4_HOURS_IN_MILLIS;
            case SNOOZE_8_HOURS:
                return getSnoozeStartTime(sg) + SNOOZE_8_HOURS_IN_MILLIS;
            case SNOOZE_24_HOURS:
                return getSnoozeStartTime(sg) + SNOOZE_24_HOURS_IN_MILLIS;
        }
        return 0;
    }

    public boolean isSnoozeExpired(String sg) {
        if (getSnoozeIndex(sg) <= SNOOZE_OFF)
            return true;
        if (System.currentTimeMillis() > getSnoozeExpiryTime(sg)) {
            setSnoozeIndex(sg, SNOOZE_OFF);
            return true;
        }
        return false;
    }

    public boolean isOTPVerified() {
        return pref.getBoolean(OTP_VERIFIED, false);
    }

    public String getDomainType() {
        return pref.getString(DOMAIN_TYPE, "company");
    }

    public boolean isAdminReg() {
        return pref.getBoolean(ADMIN_REG, false);
    }

    public long getOTPVerifiedTime() {
        return pref.getLong(OTP_VERIFIED_TIME, 0);
    }

    public String getSgListData() {
        return pref.getString(SG_LIST_DATA, null);
    }

    public boolean isBlocked(String userName) {
        return pref.getBoolean(BLOCKED_STATUS + userName, false);
    }

    public boolean isSharedIDDeactivated(String shared_id) {
        return pref.getBoolean(SHARED_ID_DEACTIVATED + shared_id, false);
    }

    public boolean isOpenDomain() {
        String sg = getUserDomain();
        return pref.getBoolean(PUBLIC_DOMAIN_ADMIN + sg, false);
    }

    public boolean isDomainAdmin(String sg) {
        return pref.getBoolean(DOMAIN_ADMIN + sg, false);
    }

    public boolean isDomainSubAdmin(String sg) {
        return pref.getBoolean(DOMAIN_SUB_ADMIN + sg, false);
    }

    public boolean isDomainAdminORSubAdmin() {
        return isDomainAdmin(getUserDomain()) || isDomainSubAdmin(getUserDomain());
    }

    public void setAsDomainSubAdmin(String sg, boolean flag) {
        editor.putBoolean(DOMAIN_SUB_ADMIN + sg, flag);
        editor.commit();
    }

    public boolean isContactSynched(String sg_name) {
        return pref.getBoolean(CONTACT_SYNCHED + sg_name, false);
    }

    public void setContactSynched(String sg_name, boolean bool) {
        editor.putBoolean(CONTACT_SYNCHED + sg_name, bool);
        editor.commit();
    }

    public boolean isGroupsLoaded() {
        return pref.getBoolean(GROUP_LOADED, false);
    }

    public void setGroupsLoaded(boolean bool) {
        editor.putBoolean(GROUP_LOADED, bool);
        editor.commit();
    }

    public boolean isSharedIDContact(String contact) {
        return pref.getBoolean(SHARED_ID_CONTACT + contact, false);
    }

    public boolean isProfileAdded(String userName) {
        return pref.getBoolean(PROFILE_ADDED + userName, false);
    }

    public boolean isMobileVerified(String mobileNumber) {
        return pref.getBoolean(MOBILE_VARIFIED + mobileNumber, false);
    }

    public boolean isMobileRegistered(String mobileNumber) {
        return pref.getBoolean(MOBILE_REGISTERED + mobileNumber, false);
    }

    public void setContactModified(boolean status) {
        editor.putBoolean("contact_modified", status);
        editor.commit();
    }

    public boolean isFirstTime() {
        boolean value = pref.getBoolean(FIRST_TIME, false);
        return value;
    }

    public boolean isMyExistence() {
        boolean value = pref.getBoolean(ME_ACTIVATED, true);
        return value;
    }

    public void saveMyExistence(boolean isFirstTime) {
        editor.putBoolean(ME_ACTIVATED, isFirstTime);
        editor.commit();
    }

    public boolean isUserExistence(String userName) {
        boolean value = pref.getBoolean(ME_ACTIVATED + userName, true);
        return value;
    }

    public boolean isWifiBackup() {
        boolean value = pref.getBoolean(BACKUP_OVER_WIFI, false);
        return value;
    }

    public void setWifiBackup(boolean value) {
        editor.putBoolean(BACKUP_OVER_WIFI, value);
        editor.commit();
    }

    public int getBackupSchedule() {
        int value = pref.getInt(BACKUP_SCHEDULE, -1);
        return value;
    }

    public void setBackupSchedule(int newOrder) {
        editor.putInt(BACKUP_SCHEDULE, newOrder);
        editor.commit();
    }

    public void saveUserExistence(String userName, boolean isFirstTime) {
        editor.putBoolean(ME_ACTIVATED + userName, isFirstTime);
        editor.commit();
    }
    public long getLastBackUpTime() {
        String sg = getUserDomain();
        long value = pref.getLong(LAST_BACKUP_TIME + sg, 0);
        return value;
    }
    public void setLastBackUpTime(long time) {
        String sg = getUserDomain();
        editor.putLong(LAST_BACKUP_TIME + sg, time);
        editor.commit();
    }

    public String getBulletin_File_Id(String sg) {
        String value = pref.getString(BULLETIN_FILE_ID + sg, null);
        return value;
    }

    public void saveBulletin_File_Id(String sg, String fileId) {
        editor.putString(BULLETIN_FILE_ID + sg, fileId);
        editor.commit();
    }

    public boolean isUserInvited(String userName) {
        boolean value = pref.getBoolean(USER_INVITED + userName, true);
        return value;
    }

    public void saveUserInvited(String userName, boolean isFirstTime) {
        editor.putBoolean(USER_INVITED + userName, isFirstTime);
        editor.commit();
    }

    public void setFirstTime(boolean isFirstTime) {
        editor.putBoolean(FIRST_TIME, isFirstTime);
        editor.commit();
    }

    public boolean isContactModified() {
        boolean value = pref.getBoolean("contact_modified", true);
        return value;
    }

    public void setAppMode(String message) {
        String sg = getUserDomain();
        if (sg != null)
            editor.putString(FIRST_TIME_APP + sg, message);
        else
            editor.putString(FIRST_TIME_APP, message);
        editor.commit();
    }

    public String getAppMode() {
        String sg = getUserDomain();
        String value = "";
        if (sg != null)
            value = pref.getString(FIRST_TIME_APP + sg, "");
        else
            value = pref.getString(FIRST_TIME_APP, "");
        return value;
    }

    public String getRecentDomains() {
        String value = pref.getString(ALL_RECENT_DOMAINS, "");
        return value;
    }

    public void saveRecentDomains(String domain) {
        String values = getRecentDomains();
        if (values == null || values.equals(""))
            values = domain;
        else {
            if (values.contains(domain))
                return;
            values += ("," + domain);
        }
        editor.putString(ALL_RECENT_DOMAINS, values);
        editor.commit();
    }

    public String getRecentUsers() {
        String value = pref.getString(ALL_RECENT_USERS, "");
        return value;
    }

    public void saveRecentUsers(String userNumber) {
        String values = getRecentUsers();
        if (values == null || values.equals(""))
            values = userNumber;
        else {
            if (values.contains(userNumber))
                return;
            values += ("," + userNumber);
        }
        editor.putString(ALL_RECENT_USERS, values);
        editor.commit();
    }

    public void saveDomainJoinedCount(String id) {
        editor.putString(DOMAIN_JOINED_COUNTS, id);
        editor.commit();
    }

    public void saveSystemMessageForSG(String sg, String message) {
        editor.putString(SYSTEM_MSG_FOR_SG + sg, message);
        editor.commit();
    }

    public String getSystemMessageForSG(String sg) {
        String value = pref.getString(SYSTEM_MSG_FOR_SG + sg, null);
        return value;
    }

    public void saveDomainUnjoinedCount(String id) {
        editor.putString(DOMAIN_UNJOINED_COUNTS, id);
        editor.commit();
    }

    public String getDomainJoinedCount() {
        String value = pref.getString(DOMAIN_JOINED_COUNTS, "0");
        return value;
    }

    public String getDomainUnjoinedCount() {
        String value = pref.getString(DOMAIN_UNJOINED_COUNTS, "0");
        return value;
    }

    public void saveSipServerAddress(String id) {
        editor.putString(SIP_SERVER, id);
        editor.commit();
    }

    public void saveServerGroupState(String groupId, int id) {
        editor.putInt(GROUP_SERVER_STATE + groupId, id);
        editor.commit();
    }

    public int getServerGroupState(String groupId) {
        int value = pref.getInt(GROUP_SERVER_STATE + groupId, GroupCreateTaskOnServer.SERVER_GROUP_UPDATE_NOTALLOWED);
        return value;
    }

    public int getBlockOrder() {
        int value = pref.getInt(BLOCK_ORDER, 1);
        return value;
    }

    public Set<String> getBlockList() {
        Set<String> value = pref.getStringSet(BLOCK_LIST, null);
        return value;
    }

    public void saveBlockedUser(String user) {
        Set<String> existedList = getBlockList();
        if (existedList == null)
            existedList = new HashSet<String>();
        existedList.add(user);
        editor.putStringSet(BLOCK_LIST, existedList);
        editor.commit();
    }

    public void removeBlockedUser(String user) {
        Set<String> existedList = getBlockList();
        if (existedList == null)
            return;
        existedList.remove(user);
        editor.putStringSet(BLOCK_LIST, existedList);
        editor.commit();
    }

    public void saveBlockOrder(int newOrder) {
        if (newOrder == -1)
            editor.putInt(BLOCK_ORDER, getBlockOrder() + 1);
        else
            editor.putInt(BLOCK_ORDER, newOrder);
        editor.commit();
    }

    public String getSipServerAddress() {
        String value = pref.getString(SIP_SERVER, "");
        return value;
    }

    public String getAuthStatus() {
        String value = pref.getString(AUTH_STATUS, null);
        return value;
    }


    public void saveAuthStatus(String name) {
        editor.putString(AUTH_STATUS, name);
        editor.commit();
    }

    public void saveActivationTime(long time) {
        if (time > 0) {
            editor.putLong(ACTIVATION_TIME, time);
            editor.commit();
        }
    }

    public long getActivationTime() {
        long value = pref.getLong(ACTIVATION_TIME, 0);
        return value;
    }
    public void saveLastOnline(String domain, long time) { // new1
        if (domain != null && !domain.equals("")) {
            editor.putLong(LAST_ONLINE + domain, time);
            editor.commit();
        }
    }

    public long getLastOnline(String domain) { // new1
        long value = pref.getLong(LAST_ONLINE + domain, 0);
        return value;
    }

    public void saveUserFileId(String userName, String fileId) {
        if (fileId != null && !fileId.equals("")) {
            editor.putString(USER_FILE_ID + userName, fileId);
            editor.commit();
        }
    }

    public void saveBackupFileId(String fileId) {
        if (fileId != null && !fileId.equals("")) {
            editor.putString(BACKUP_FILE_ID + getUserName(), fileId);
            editor.commit();
        }
    }

    public String getBackupFileId() {
        String value = pref.getString(BACKUP_FILE_ID + getUserName(), null);
        return value;
    }

    public void saveBulletinNextURL(String sg, String url) {
        if (url != null && !url.equals("")) {
            editor.putString(BULLETIN_NEXT_URL + sg, url);
            editor.commit();
        }
    }

    public String getBulletinNextURL(String sg) {
        String value = pref.getString(BULLETIN_NEXT_URL + sg, null);
        return value;
    }

    public void saveSharedIDFileId(String sharedid, String fileId) {
        if (fileId != null && !fileId.equals("")) {
            editor.putString(SHARED_ID_FILE_ID + sharedid, fileId);
            editor.commit();
        }
    }

    public String getUserFileId(String userName) {
        String value = pref.getString(USER_FILE_ID + userName, null);
        return value;
    }

    public String getSharedIDFileId(String sharedid) {
        String value = pref.getString(SHARED_ID_FILE_ID + sharedid, null);
        return value;
    }

    public void saveSGFileId(String sg, String fileId) {
        editor.putString(SG_FILE_ID + sg, fileId);
        editor.commit();
    }

    public void saveSharedIDData(String data) {
        editor.putString(SHARED_ID_DATA, data);
        editor.commit();
    }

    public String getSGFileId(String sg) {
        String value = pref.getString(SG_FILE_ID + sg, null);
        return value;
    }

    public String getSharedIDData() {
        String value = pref.getString(SHARED_ID_DATA, null);
        return value;
    }

    public void saveGroupOwnerName(String groupname, String owner) {
        editor.putString(CHANNEL_OWNER + groupname, owner);
        editor.commit();
    }

    public String getGroupOwnerName(String groupname) {
        String value = pref.getString(CHANNEL_OWNER + groupname, null);
        return value;
    }

    public String getUserPassword() {
        String value = pref.getString(USER_PASSWORD, "");
        return value;
    }

    public void saveUserPassword(String pass) {
        editor.putString(USER_PASSWORD, pass);
        editor.commit();
    }

    public String getUserCountryCode() {
        String value = pref.getString(COUNTRY_CODE, "");
        return value;
    }

    public void setUserCountryCode(String code) {
        editor.putString(COUNTRY_CODE, code);
        editor.commit();
    }

    public boolean getPollReplyStatus(String poll_id) {
        boolean value = pref.getBoolean(poll_id, false);
        return value;
    }

    public void setPollReplyStatus(String poll_id, boolean replied) {
        editor.putBoolean(poll_id, replied);
        editor.commit();
    }

    public void saveUserGroupInfo(String groupName, String groupPerson, byte infoType, boolean isSet) {
//		String myName = getUserNameId();
        editor.putBoolean(groupName + "_" + groupPerson + "_" + infoType, isSet);
        editor.commit();
    }

    public void saveGroupInfo(String groupName, byte infoType, boolean isSet) {
//		String myName = getUserNameId();
        editor.putBoolean(groupName + "_" + infoType, isSet);
        editor.commit();
    }

    public boolean getUserGroupInfo(String groupName, String groupPerson, byte infoType) {
        boolean value = pref.getBoolean(groupName + "_" + groupPerson + "_" + infoType, false);
        return value;
    }

    public void saveGroupTypeAsPublic(String groupName, boolean isSet) {
//		String myName = getUserNameId();
        editor.putBoolean(groupName + "_" + PUBLIC_GROUP, isSet);
        editor.commit();
    }

    public void saveUseBroadCastInfo(String broadCastName, String broadCastPerson, byte infoType, boolean isSet) {
//		String myName = getUserNameId();
        editor.putBoolean(broadCastName + "_" + broadCastPerson + "_" + infoType, isSet);
        editor.commit();
    }

    public boolean isPublicGroup(String groupName) {
        return pref.getBoolean(groupName + "_" + PUBLIC_GROUP, false);
    }

    public boolean isAdmin(String groupName, String groupPerson) {
        return pref.getBoolean(groupName + "_" + groupPerson + "_" + GROUP_ADMIN_INFO, false);
    }

    public boolean isBroadCastAdmin(String broadCastName, String broadCastPerson) {
        return pref.getBoolean(broadCastName + "_" + broadCastPerson + "_" + BROADCAST_ADMIN_INFO, false);
    }

    public boolean isOwner(String groupName, String groupPerson) {
        return pref.getBoolean(groupName + "_" + groupPerson + "_" + GROUP_OWNER_INFO, false);
    }

    public boolean isBroadCastOwner(String broadCastName, String broadCastPerson) {
        return pref.getBoolean(broadCastName + "_" + broadCastPerson + "_" + BROADCAST_OWNER_INFO, false);
    }

    public boolean isGroupMemberActive(String groupName, String groupPerson) {
        return pref.getBoolean(groupName + "_" + groupPerson + "_" + GROUP_ACTIVE_INFO, true);
    }

    public boolean isGroupActive(String groupName) {
        return pref.getBoolean(groupName + "_" + GROUP_ACTIVE_INFO, false);
    }

    public boolean isBroadCastActive(String broadCastName, String broadCastPerson) {
        return pref.getBoolean(broadCastName + "_" + broadCastPerson + "_" + BROADCAST_ACTIVE_INFO, false);
    }

    public boolean saveUsersOfGroup(String groupName, String groupPerson) {
        String prevName = getUsersOfGroup(groupName);
        if (isGroupUserPresent(prevName, groupPerson))
            return false;
        if (prevName.equals(""))
            editor.putString(GROUP_USERS + groupName, groupPerson);
        else
            editor.putString(GROUP_USERS + groupName, prevName + "%#%" + groupPerson);
        editor.commit();
        return true;
    }

    public boolean saveUsersOfBroadCast(String broadCastName, String broadCastPerson) {
        String prevName = getUsersOfBroadCast(broadCastName);
        if (isBroadCastUserPresent(prevName, broadCastPerson))
            return false;
        if (prevName.equals(""))
            editor.putString(BROADCAST_USERS + broadCastName, broadCastPerson);
        else
            editor.putString(BROADCAST_USERS + broadCastName, prevName + "%#%" + broadCastPerson);
        editor.commit();
        return true;
    }

    private boolean isGroupUserPresent(String allUsers, String groupPerson) {
        for (String person : allUsers.split("%#%")) {
            if (person.equals(groupPerson))
                return true;
        }
        return false;
    }

    private boolean isBroadCastUserPresent(String allUsers, String broadCastPerson) {
        for (String person : allUsers.split("%#%")) {
            if (person.equals(broadCastPerson))
                return true;
        }
        return false;
    }

    public void saveGroupName(String groupName, String displayName) {
        String sg = getUserDomain();
        String prevName = getGroupName(sg);
        if (prevName.contains(groupName))
            return;
        saveGroupDisplayName(groupName, displayName);
        if (prevName.equals(""))
            editor.putString(GROUP_NAME + sg, groupName);
        else
            editor.putString(GROUP_NAME + sg, prevName + "%#%" + groupName);
        editor.commit();
    }

    public void removeAllGroups(String sg) {
        editor.putString(GROUP_NAME + sg, null);
        editor.commit();
    }

    public void saveBroadCastName(String broadCastName, String displayName) {
        String prevName = getBroadCastName();
        if (prevName.contains(broadCastName))
            return;
        saveBroadCastDisplayName(broadCastName, displayName);
        if (prevName.equals(""))
            editor.putString(BROADCAST_NAME, broadCastName);
        else
            editor.putString(BROADCAST_NAME, prevName + "%#%" + broadCastName);
        editor.commit();
    }

    public void removeUsersFromGroup(String groupName, String groupPerson) {
        String result = "";
        String prevName = getUsersOfGroup(groupName);
        if (!prevName.equals("")) {
            if (isGroupUserPresent(prevName, groupPerson)) {
                if (prevName.contains("%#%")) {
                    ArrayList<String> list = new ArrayList<String>(Arrays.asList(prevName.split("%#%")));
                    for (String item : list) {
                        if (!item.equals(groupPerson))
                            result += (item + "%#%");
                    }
                    if (result.endsWith("%#%"))
                        result = result.substring(0, result.lastIndexOf("%#%"));
                } else {
                    result = prevName.replace(groupPerson, "");
                }
                editor.putString(GROUP_USERS + groupName, result);
                editor.commit();
            }
        }
    }

    public void removeUsersFromBroadCast(String broadCastName, String broadCastPerson) {
        String result = "";
        String prevName = getUsersOfBroadCast(broadCastName);
        if (!prevName.equals("")) {
            if (isBroadCastUserPresent(prevName, broadCastPerson)) {
                if (prevName.contains("%#%")) {
                    ArrayList<String> list = new ArrayList<String>(Arrays.asList(prevName.split("%#%")));
                    for (String item : list) {
                        if (!item.equals(broadCastPerson))
                            result += (item + "%#%");
                    }
                    if (result.endsWith("%#%"))
                        result = result.substring(0, result.lastIndexOf("%#%"));
                } else {
                    result = prevName.replace(broadCastPerson, "");
                }
                editor.putString(BROADCAST_USERS + broadCastName, result);
                editor.commit();
            }
        }
    }

    public void removeGroupName(String groupName) {
        String prevName = getGroupName(getUserDomain());
        removeGroupDisplayName(groupName);
        if (!prevName.equals("")) {
            if (prevName.contains(groupName)) {
                if (prevName.contains(groupName + "%#%")) {
                    prevName = prevName.replace(groupName + "%#%", "");
                } else if (prevName.contains("%#%" + groupName)) {
                    prevName = prevName.replace("%#%" + groupName, "");
                } else
                    prevName = prevName.replace(groupName, "");
                editor.putString(GROUP_NAME, prevName);
//			editor.putString(GROUP_USERS+groupName, "");
                editor.commit();
            }
        }
    }

    public void removeBroadCastName(String broadCastName) {
        String prevName = getBroadCastName();
        removeBroadCastDisplayName(broadCastName);
        if (!prevName.equals("")) {
            if (prevName.contains(broadCastName)) {
                if (prevName.contains(broadCastName + "%#%")) {
                    prevName = prevName.replace(broadCastName + "%#%", "");
                } else if (prevName.contains("%#%" + broadCastName)) {
                    prevName = prevName.replace("%#%" + broadCastName, "");
                } else
                    prevName = prevName.replace(broadCastName, "");
                editor.putString(BROADCAST_NAME, prevName);
                editor.commit();
            }
        }
    }

    public boolean isGroupChat(String groupName) {
        boolean ret = false;
        if (groupName == null)
            return false;
        String groups = getGroupName(getUserDomain());
        if (groups != null) {
            if (!groups.equals("") && groups.contains("%#%")) {
                for (String name : groups.split("%#%")) {
                    if (name.equals(groupName)) {
                        return true;
                    }
                }
            } else if (groupName.equals(groups)) {
                ret = true;
            }
        }
        return ret;
    }

    public boolean isBroadCast(String broadCastName) {
        boolean ret = false;
        if (broadCastName == null)
            return false;
        String groups = getBroadCastName();
        if (groups != null) {
            if (!groups.equals("") && groups.contains("%#%")) {
                for (String name : groups.split("%#%")) {
                    if (name.equals(broadCastName)) {
                        return true;
                    }
                }
            } else if (broadCastName.equals(groups)) {
                ret = true;
            }
        }
        return ret;
    }

    //	public String getUserSipPassword() {
//		String value = pref.getString(USER_SIP_PASSWORD, "");
//		return value;
//	}
//
//	public void saveUserSipPassword(String pass) {
//		editor.putString(USER_SIP_PASSWORD, pass);
//		editor.commit();
//	}
    public String getUsersOfGroup(String groupName) {
        String value = pref.getString(GROUP_USERS + groupName, "");
        return value;
    }

    public String getUsersOfBroadCast(String broadCastName) {
        String value = pref.getString(BROADCAST_USERS + broadCastName, "");
        return value;
    }

    public String getGroupName(String sg) {
        String value = pref.getString(GROUP_NAME + sg, "");
        return value;
    }

    public String getBroadCastName() {
        String value = pref.getString(BROADCAST_NAME, "");
        return value;
    }

    public String[] getGroupNamesArray() {
        String array[] = new String[1];
        String groups = getGroupName(getUserDomain());
        if (groups != null) {
            if (!groups.equals("") && groups.contains("%#%")) {
                array = groups.split("%#%");

            } else {
                array[0] = groups;
            }
        }
        return array;
    }

    public String[] getBroadCastNamesArray() {
        String array[] = new String[1];
        String groups = getBroadCastName();
        if (groups != null) {
            if (!groups.equals("") && groups.contains("%#%")) {
                array = groups.split("%#%");

            } else {
                array[0] = groups;
            }
        }
        return array;
    }

    public ArrayList<String> getGroupUsersList(String groupName) {
        ArrayList<String> list = new ArrayList<String>();
        String groups = getUsersOfGroup(groupName);
        if (groups != null && !groups.equals("")) {
            if (groups.contains("%#%")) {
                list = new ArrayList<String>(Arrays.asList(groups.split("%#%")));
            } else {
                list.add(groups);
            }
        }
        return list;
    }

    public ArrayList<String> getBroadCastUsersList(String broadCastName) {
        ArrayList<String> list = new ArrayList<String>();
        String groups = getUsersOfBroadCast(broadCastName);
        if (groups != null && !groups.equals("")) {
            if (groups.contains("%#%")) {
                list = new ArrayList<String>(Arrays.asList(groups.split("%#%")));
            } else {
                list.add(groups);
            }
        }
        return list;
    }

    public String getDisplayName() {
        String sg = getUserDomain();
        String value = pref.getString(USER_DISPLAY_NAME + sg, null);
        return value;
    }

    public String getUserStatusMessage(String userName) {
        String value = pref.getString(USER_STATUS_MESSAGE + userName, "");
        return value;
    }

    public String getGroupMemberCount(String groupname) {
        String value = pref.getString(GROUP_MEM_COUNT + groupname, "");
        return value;
    }

    public void saveGroupMemberCount(String groupname, String count) {
        editor.putString(GROUP_MEM_COUNT + groupname, count);
        editor.commit();
    }

    public void saveGroupsForSG(String sg, String group_data) {
        editor.putString(GROUPS_FOR_SG + sg, group_data);
        editor.commit();
    }

    public String getGroupsForSG(String sg) {
        String value = pref.getString(GROUPS_FOR_SG + sg, null);
        return value;
    }

    public String getUserDesignation(String userName) {
        String value = pref.getString(USER_DESIGNATION + userName, "");
        return value;
    }

    public String getUserDepartment(String userName) {
        String value = pref.getString(USER_DEPARTMENT + userName, "");
        return value;
    }

    public String getUserName() {
        String value = pref.getString(USER_NAME_ID, null);
        return value;
    }

    public void saveUserPhone(String phone) {
        editor.putString(USER_PHONE, phone);
        editor.commit();
    }

    public String getUserPhone() {
        String value = pref.getString(USER_PHONE, null);
        return value;
    }

    public void saveSelectedIndexNav(int index) {
        editor.putInt(USER_SEL_NAV_INDEX, index);
        editor.commit();
    }

    public int getSelectedIndexNav() {
        int value = pref.getInt(USER_SEL_NAV_INDEX, 0);
        return value;
    }

    public void saveUserEmail(String phone) {
        String sg = getUserDomain();
        if(sg != null)
            editor.putString(USER_EMAIL + sg, phone);
        editor.commit();
    }

    public String getUserEmail() {
        String sg = getUserDomain();
        String value = null;
        if(sg != null)
            value = pref.getString(USER_EMAIL + sg, null);
        return value;
    }

    public void saveDisplayName(String name) {
        String sg = getUserDomain();
        editor.putString(USER_DISPLAY_NAME + sg, name);
        editor.commit();
    }

    public String getUserServerName(String userName) {
//		String value = userName;
        String value = "Superchatter";
        try {
            value = pref.getString(USER_DISPLAY_NAME + userName, userName);
        } catch (Exception e) {
        }
        return value;
    }

    public String getUserServerDisplayName(String userName) {
        String value = null;
        try {
            value = pref.getString(USER_DISPLAY_NAME + userName, "");
        } catch (Exception e) {

        }
        return value;
    }

    public String getUserServerNameIfExists(String userName) {
        String value = null;
        try {
            value = pref.getString(USER_DISPLAY_NAME + userName, userName);
        } catch (Exception e) {
        }
        return value;
    }

    public void saveUserServerName(String userName, String name) {
        editor.putString(USER_DISPLAY_NAME + userName, name);
        editor.commit();
    }

    public void saveUserStatusMessage(String userName, String status) {
        if (status != null && status.contains("ESIA"))
            status = status.replace("ESIA", "Super");
        editor.putString(USER_STATUS_MESSAGE + userName, status);
        editor.commit();
    }

    public void saveUserDesignation(String userName, String designation) {
        editor.putString(USER_DESIGNATION + userName, designation);
        editor.commit();
    }

    public void saveUserDepartment(String userName, String department) {
        editor.putString(USER_DEPARTMENT + userName, department);
        editor.commit();
    }

    public void saveUserName(String name) {
        System.out.println("<< saveUserName >> : "+name);
        editor.putString(USER_NAME_ID, name);
        editor.commit();
    }


    public void saveOwnerArray(String value) {
        editor.putString(USER_Current_STATUS, value);
        editor.commit();
    }

    public String getOwnerArray() {
        String value = pref.getString(USER_Current_STATUS , null);
        return value;
    }

    public void saveUserDomain(String domain) {
		System.out.println("<< Domain >> : "+domain);
        editor.putString(USER_DOMAIN, domain);
        editor.commit();
    }

    public String getUserDomain() {
        String value = pref.getString(USER_DOMAIN, "");
        return value;
    }

    public void saveOwnedDomain(String domain) {
        editor.putString(OWNED_DOMAIN, domain);
        editor.commit();
    }

    public String getOwnedDomain() {
        String value = pref.getString(OWNED_DOMAIN, null);
        return value;
    }

    public void saveCurrentSGDisplayName(String domain) {
        if(domain != null && domain.length() > 0) {
            editor.putString(USER_DOMAIN_DISPLAY_NAME, domain);
            editor.commit();
        }
    }

    public String getCurrentSGDisplayName() {
        String value = pref.getString(USER_DOMAIN_DISPLAY_NAME, "");
        return value;
    }

    public void saveUserGender(String userName, String gender) {
        editor.putString(USER_GENDER + userName, gender);
        editor.commit();
    }

    public String getUserGender(String userName) {
        String value = pref.getString(USER_GENDER + userName, "male");
        return value;
    }

    public void saveUserOrgName(String org_name) {
        editor.putString(USER_ORG_NAME, org_name);
        editor.commit();
    }

    public String getUserOrgName() {
        String value = pref.getString(USER_ORG_NAME, "");
        return value;
    }

    public void saveUserId(long id) {
        editor.putLong(USER_ID, id);
        editor.commit();
    }

    public long getUserId() {
        long value = pref.getLong(USER_ID, 0);
        return value;
    }

    public void saveCurrentDomainType(String type) {
        editor.putString(CURRENT_MEMBER_TYPE, type);
        editor.commit();
    }

    public String getCurrentDomainType() {
        String value = pref.getString(CURRENT_MEMBER_TYPE, null);
        return value;
    }


    public String getGroupDisplayName(String room) {
        String value = room;
        try {
            value = pref.getString(GROUP_DISPLAY_NAME + room, room);
        } catch (Exception e) {
        }
        return value;
    }

    public String getBroadCastDisplayName(String room) {
        String value = room;
        try {
            value = pref.getString(BROADCAST_DISPLAY_NAME + room, room);
        } catch (Exception e) {
        }
        return value;
    }

    public String getSharedIDDisplayName(String room) {
        String value = null;
        try {
            value = pref.getString(SHARED_ID_DISPLAY_NAME + room, null);
        } catch (Exception e) {
        }
        return value;
    }

    public void saveGroupDisplayName(String room, String displayName) {
        editor.putString(GROUP_DISPLAY_NAME + room, displayName);
        editor.commit();
    }

    public void saveBroadCastDisplayName(String room, String displayName) {
        editor.putString(BROADCAST_DISPLAY_NAME + room, displayName);
        editor.commit();
    }

    public String getSGPassword(String sg) {
        sg = sg.toLowerCase();
        String value = null;
        try {
            value = pref.getString(SG_PASSWORD + sg, null);
        } catch (Exception e) {
        }
        return value;
    }

    public void saveSGPassword(String sg, String pass) {
        if(sg != null) {
            sg = sg.toLowerCase();
            editor.putString(SG_PASSWORD + sg, pass);
            editor.commit();
        }
    }

    public long getSGUserID(String sg) {
        sg = sg.toLowerCase();
        long value = 0;
        try {
            value = pref.getLong(SG_USER_ID + sg, 0);
        } catch (Exception e) {
        }
        return value;
    }

    public void saveSGUserID(String sg, long userid) {
        if(sg != null) {
            sg = sg.toLowerCase();
            editor.putLong(SG_USER_ID + sg, userid);
            editor.commit();
        }
    }

    public void saveSharedIDDisplayName(String room, String displayName) {
        editor.putString(SHARED_ID_DISPLAY_NAME + room, displayName);
        editor.commit();
    }

    public void removeGroupDisplayName(String room) {
        editor.remove(GROUP_DISPLAY_NAME + room);
        editor.commit();
    }

    public void removeBroadCastDisplayName(String room) {
        editor.remove(BROADCAST_DISPLAY_NAME + room);
        editor.commit();
    }

    public void saveUserVarified(boolean isVarified) {
        editor.putBoolean(USER_VARIFIED, isVarified);
        editor.commit();
    }

    public void saveUserLogedOut(boolean isLogOut) {
        editor.putBoolean(USER_LOGED_OUT, isLogOut);
        editor.commit();
    }

    public void saveChatCounter(String sg, int counter) {
        editor.putInt(CHAT_COUNTER + sg, counter);
        editor.commit();
    }

    public void saveBulletinChatCounter(String sg, int counter) {
        editor.putInt(BULLETIN_CHAT_COUNTER + sg, counter);
        editor.commit();
    }

    public void saveNewContactsCounter(String sg, int counter) {
        editor.putInt(CONTACT_COUNTER + sg, counter);
        editor.commit();
    }

    public void saveChatCountOfUser(String person, int counter) {
        editor.putInt(person, counter);
        editor.commit();
    }

    public void saveUserListeningStatus(String person, boolean status) {
        editor.putBoolean(person + LISTENING, status);
        editor.commit();
    }

    public void saveUserListeningStatusForGroup(String group, String user) {
        Log.i("SharedPrefManager", "Get : key : " + (group + LISTENING) + ", value : " + user);
        editor.putString(group + LISTENING, user);
        editor.commit();
    }

    public boolean getUserListeningStatus(String person) {
        boolean value = false;
        try {
            value = pref.getBoolean(person + LISTENING, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public String getUserListeningStatusForGroup(String group) {
        String user = "";
        try {
            user = pref.getString(group + LISTENING, "");
            Log.i("SharedPrefManager", "Get : key : " + (group + LISTENING) + ", value : " + user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public void saveUserRecordingStatus(String person, boolean status) {
        editor.putBoolean(person + RECORDING, status);
        editor.commit();
    }

    public boolean getUserRecordingStatus(String person) {
        boolean value = false;
        try {
            value = pref.getBoolean(person + RECORDING, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public void saveUserRecordingStatusForGroup(String group, String user) {
        editor.putString(group + RECORDING, user);
        Log.i("SharedPrefManager", "Set : key : " + (group + RECORDING) + ", value : " + user);
        editor.commit();
    }

    public String getUserRecordingStatusForGroup(String group) {
        String user = "";
        try {
            user = pref.getString(group + RECORDING, "");
            Log.i("SharedPrefManager", "Get : key : " + (group + RECORDING) + ", value : " + user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public void saveUserTypingStatus(String person, boolean status) {
        editor.putBoolean(person + ISTYPING, status);
        editor.commit();
    }

    public boolean getUserTypingStatus(String person) {
        boolean value = false;
        try {
            value = pref.getBoolean(person + ISTYPING, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public void saveUserTypingStatusForGroup(String group, String user) {
        Log.i("SharedPrefManager", "Set : key : " + (group + TYPING) + ", value : " + user);
        editor.putString(group + TYPING, user);
        editor.commit();
    }

    public String getUserTypingStatusForGroup(String group) {
        String user = "";
        try {
            user = pref.getString(group + TYPING, "");
            Log.i("SharedPrefManager", "Get : value for key : " + (group + TYPING) + ", value : " + user);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return user;
    }

    public boolean getUserVarified() {
        boolean value = pref.getBoolean(USER_VARIFIED, false);
        return value;
    }

    public void saveUserEPR(boolean isEpr) {
        editor.putBoolean(USER_EPR_COMPLETE, isEpr);
        editor.commit();
    }

    public boolean getUserEPRCompleted() {
        boolean value = pref.getBoolean(USER_EPR_COMPLETE, false);
        return value;
    }

    public void saveDeviceToken(String token) {
        editor.putString(DEVICE_TOKEN, token);
        editor.commit();
    }

    public String getDeviceToken() {
        String value = pref.getString(DEVICE_TOKEN, null);
        return value;
    }

    public boolean getUserLogedOut() {
        boolean value = pref.getBoolean(USER_LOGED_OUT, true);
        return value;
    }

    public int getChatCounter(String sg) {
        int value = pref.getInt(CHAT_COUNTER + sg, 0);
        return value;
    }

    public int getBulletinChatCounter(String sg) {
        int value = pref.getInt(BULLETIN_CHAT_COUNTER + sg, 0);
        return value;
    }

    public int getNewContactsCounter(String sg) {
        int value = pref.getInt(CONTACT_COUNTER + sg, 0);
        return value;
    }

    public int getChatCountOfUser(String person) {
        try {
            int value = pref.getInt(person, 0);
            return value;
        } catch(Exception e){
            return 0;
        }
    }

    public void clearSharedPref() {
        editor.clear();
        editor.commit();
    }

	/*
     * public void UserDataLogout(){ editor.remove(FIRST_TIME_APP);
	 * editor.remove(USER_ID); editor.remove(SIP_SERVER);
	 * editor.remove(USER_SIP_PASSWORD); editor.remove(USER_PASSWORD);
	 * editor.remove(USER_NAME_ID); editor.remove(USER_NAME);
	 * editor.remove(AUTH_STATUS); editor.remove(USER_PHONE);
	 * editor.remove(USER_EMAIL); editor.remove(USER_VARIFIED);
	 * editor.remove(USER_EPR_COMPLETE); editor.remove(USER_LOGED_OUT);
	 * editor.commit(); }
	 */
}
