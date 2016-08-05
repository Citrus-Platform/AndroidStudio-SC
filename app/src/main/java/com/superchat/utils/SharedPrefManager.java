package com.superchat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.superchat.SuperChatApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SharedPrefManager {

	private SharedPreferences pref;
	// editor for shared refrence
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
	
	public static final long SNOOZE_20_MINUTES_IN_MILLIS = (20*60*1000);
	public static final long SNOOZE_1_HOUR_IN_MILLIS = (60*60*1000);
	public static final long SNOOZE_2_HOURS_IN_MILLIS = (2*60*60*1000);
	public static final long SNOOZE_4_HOURS_IN_MILLIS = (4*60*60*1000);
	public static final long SNOOZE_8_HOURS_IN_MILLIS = (8*60*60*1000);
	public static final long SNOOZE_24_HOURS_IN_MILLIS = (24*60*60*1000);
	
	private final String PREF_NAME = "SuperChatPref";

	private final String FIRST_TIME_APP = "mode"; // For Frash User
	private final String USER_ID = "user_id";
	private final String USER_DOMAIN = "user_domain";
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
	private final String AUTH_STATUS = "status";
	private final String USER_PHONE = "mobile_number";
	private final String DOMAIN_TYPE = "domain_type";
	private final String USER_EMAIL = "email_id";
	private final String USER_FILE_ID = "user_file_id";
	private final String SHARED_ID_FILE_ID = "shared_id_file_id";
	private final String SG_FILE_ID = "sg_file_id";
	private final String SHARED_ID_DATA = "shared_id_data";
	private final String LAST_ONLINE = "last_online";
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
	private static SharedPrefManager sharedPrefManager;

	private SharedPrefManager(Context context) {
		this.mContext = context;
		pref = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public static SharedPrefManager getInstance() {
		if (sharedPrefManager == null && SuperChatApplication.context!=null) {
			sharedPrefManager = new SharedPrefManager(SuperChatApplication.context);
		}
		return sharedPrefManager;
	}
	public void setProfileAdded(String userName,boolean flag){
		editor.putBoolean(PROFILE_ADDED+userName, flag);
		editor.commit();
	}
	public void saveStatusDNM(String userName,boolean flag){
		editor.putBoolean(DNM_ACTIVATION+userName, flag);
		editor.commit();
	}
	public void saveStatusDNC(String userName,boolean flag){
		editor.putBoolean(DNC_ACTIVATION+userName, flag);
		editor.commit();
	}
	public void setMute(String userNameOrGroupName,boolean flag){
		editor.putBoolean(MUTE_GROUP_OR_USER+userNameOrGroupName, flag);
		editor.commit();
	}
	public void setSnoozeIndex(int optionIndex){
		editor.putInt(SNOOZE_INDEX, optionIndex);
		editor.commit();
	}
	public void setSnoozeStartTime(long snoozeStartTime){
		editor.putLong(SNOOZE_START_TIME, snoozeStartTime);
		editor.commit();
	}
	public void setOTPVerified(boolean flag){
		editor.putBoolean(OTP_VERIFIED, flag);
		editor.commit();
	}
	public void setAdminReg(boolean flag){
		editor.putBoolean(ADMIN_REG, flag);
		editor.commit();
	}
	public void setOTPVerifiedTime(long time){
		editor.putLong(OTP_VERIFIED_TIME, time);
		editor.commit();
	}
	public void setSGListData(String data){
		editor.putString(SG_LIST_DATA, data);
		editor.commit();
	}
	public void setBlockStatus(String userName,boolean flag){
		editor.putBoolean(BLOCKED_STATUS+userName, flag);
		editor.commit();
	}
	public void setSharedIDDeactivated(String shared_id, boolean flag){
		editor.putBoolean(SHARED_ID_DEACTIVATED+shared_id, flag);
		editor.commit();
	}
	public void setMobileVerified(String mobileNumber,boolean flag){
		editor.putBoolean(MOBILE_VARIFIED+mobileNumber, flag);
		editor.commit();
	}
	public void setSharedIDContact(String contact, boolean flag){
		editor.putBoolean(SHARED_ID_CONTACT + contact, flag);
		editor.commit();
	}
	public void setAsDomainAdmin(boolean flag){
		editor.putBoolean(DOMAIN_ADMIN, flag);
		editor.commit();
	}
	public void setUpdateCheck(boolean flag){
		editor.putBoolean(APP_UPDATE, flag);
		editor.commit();
	}
	public void setDomainAsPublic(boolean flag){
		editor.putBoolean(PUBLIC_DOMAIN_ADMIN, flag);
		editor.commit();
	}
	public void setDomainType(String type){
		editor.putString(DOMAIN_TYPE, type);
		editor.commit();
	}
	public void setMobileRegistered(String mobileNumber,boolean flag){
		editor.putBoolean(MOBILE_REGISTERED+mobileNumber, flag);
		editor.commit();
	}
	public boolean isUpdateCheckNeeded(){
		return pref.getBoolean(APP_UPDATE, true);
	}
	public boolean isMute(String userNameOrGroupName){
		return pref.getBoolean(MUTE_GROUP_OR_USER+userNameOrGroupName, false);
	}
	public boolean isDNC(String userName){
		return pref.getBoolean(DNC_ACTIVATION+userName, false);
	}
	public boolean isDNM(String userName){
		return pref.getBoolean(DNM_ACTIVATION+userName, false);
	}
	public int getSnoozeIndex(){
		return pref.getInt(SNOOZE_INDEX, SNOOZE_OFF);
	}
	public long getSnoozeStartTime(){
		if(getSnoozeIndex()<=0)
			return 0;
		return pref.getLong(SNOOZE_START_TIME, System.currentTimeMillis());
	}
	public long getSnoozeExpiryTime(){
		if(getSnoozeIndex()<=SNOOZE_OFF)
			return 0;
		switch(getSnoozeIndex()){
		case SNOOZE_20_MINUTES:
			return getSnoozeStartTime()+SNOOZE_20_MINUTES_IN_MILLIS;
		case SNOOZE_1_HOUR:
			return getSnoozeStartTime()+SNOOZE_1_HOUR_IN_MILLIS;
		case SNOOZE_2_HOURS:
			return getSnoozeStartTime()+SNOOZE_2_HOURS_IN_MILLIS;
		case SNOOZE_4_HOURS:
			return getSnoozeStartTime()+SNOOZE_4_HOURS_IN_MILLIS;
		case SNOOZE_8_HOURS:
			return getSnoozeStartTime()+SNOOZE_8_HOURS_IN_MILLIS;
		case SNOOZE_24_HOURS:
			return getSnoozeStartTime()+SNOOZE_24_HOURS_IN_MILLIS;
		}
		return 0;
	}
	public boolean isSnoozeExpired(){
		if(getSnoozeIndex()<=SNOOZE_OFF)
			return true;
		if(System.currentTimeMillis()>getSnoozeExpiryTime()){
			setSnoozeIndex(SNOOZE_OFF);
			return true;
		}
		return false;
	}
	public boolean isOTPVerified(){
		return pref.getBoolean(OTP_VERIFIED, false);
	}
	public String getDomainType(){
		return pref.getString(DOMAIN_TYPE, "company");
	}
	public boolean isAdminReg(){
		return pref.getBoolean(ADMIN_REG, false);
	}
	public long getOTPVerifiedTime(){
		return pref.getLong(OTP_VERIFIED_TIME, 0);
	}
	public String getSgListData(){
		return pref.getString(SG_LIST_DATA, null);
	}
	public boolean isBlocked(String userName){
		return pref.getBoolean(BLOCKED_STATUS+userName, false);
	}
	public boolean isSharedIDDeactivated(String shared_id){
		return pref.getBoolean(SHARED_ID_DEACTIVATED+shared_id, false);
	}
	public boolean isOpenDomain(){
		return pref.getBoolean(PUBLIC_DOMAIN_ADMIN, false);
	}

	public boolean isDomainAdmin(){
		return pref.getBoolean(DOMAIN_ADMIN, false);
	}
	public boolean isDomainSubAdmin(){
		return pref.getBoolean(DOMAIN_SUB_ADMIN, false);
	}
	public void setAsDomainSubAdmin(boolean flag){
		editor.putBoolean(DOMAIN_SUB_ADMIN, flag);
		editor.commit();
	}

	public boolean isContactSynched(){
		return pref.getBoolean(CONTACT_SYNCHED, false);
	}
	public void setContactSynched(boolean bool){
		editor.putBoolean(CONTACT_SYNCHED, bool);
		editor.commit();
	}
	public boolean isGroupsLoaded(){
		return pref.getBoolean(GROUP_LOADED, false);
	}
	public void setGroupsLoaded(boolean bool){
		editor.putBoolean(GROUP_LOADED, bool);
		editor.commit();
	}
	public boolean isSharedIDContact(String contact){
		return pref.getBoolean(SHARED_ID_CONTACT + contact, false);
	}
	public boolean isProfileAdded(String userName){
		return pref.getBoolean(PROFILE_ADDED+userName, false);
	}
	public boolean isMobileVerified(String mobileNumber){
		return pref.getBoolean(MOBILE_VARIFIED+mobileNumber, false);
	}
	public boolean isMobileRegistered(String mobileNumber){
		return pref.getBoolean(MOBILE_REGISTERED+mobileNumber, false);
	}
	public void setContactModified(boolean status){
		editor.putBoolean("contact_modified", status);
		editor.commit();
	}
	public boolean isFirstTime(){
		boolean value = pref.getBoolean(FIRST_TIME, false);
		return value;
	}
public boolean isMyExistence(){
	boolean value = pref.getBoolean(ME_ACTIVATED, true);
	return value;
}
public void saveMyExistence(boolean isFirstTime){
	editor.putBoolean(ME_ACTIVATED, isFirstTime);
	editor.commit();
}
public boolean isUserExistence(String userName){
	boolean value = pref.getBoolean(ME_ACTIVATED+userName, true);
	return value;
}
public void saveUserExistence(String userName , boolean isFirstTime){
	editor.putBoolean(ME_ACTIVATED+userName, isFirstTime);
	editor.commit();
}
public boolean isUserInvited(String userName){
	boolean value = pref.getBoolean(USER_INVITED+userName, true);
	return value;
}
public void saveUserInvited(String userName , boolean isFirstTime){
	editor.putBoolean(USER_INVITED+userName, isFirstTime);
	editor.commit();
}
public void setFirstTime(boolean isFirstTime){
	editor.putBoolean(FIRST_TIME, isFirstTime);
	editor.commit();
}
public boolean isContactModified(){
	boolean value = pref.getBoolean("contact_modified", true);
	return value;
}
	public void setAppMode(String message) {
		editor.putString(FIRST_TIME_APP, message);
		editor.commit();
	}

	public String getAppMode() {
		String value = pref.getString(FIRST_TIME_APP, null);
		return value;
	}
	public String getRecentDomains(){
		String value = pref.getString(ALL_RECENT_DOMAINS, "");
		return value;
	}
	public void saveRecentDomains(String domain) {
		String values = getRecentDomains();
		if(values == null || values.equals(""))
			values = domain;
		else{
			if(values.contains(domain))
				return;
			values += (","+domain);
			}
		editor.putString(ALL_RECENT_DOMAINS, values);
		editor.commit();
	}
	public String getRecentUsers(){
		String value = pref.getString(ALL_RECENT_USERS, "");
		return value;
	}
	public void saveRecentUsers(String userNumber) {
		String values = getRecentUsers();
		if(values == null || values.equals(""))
			values = userNumber;
		else{
			if(values.contains(userNumber))
				return;
			values += (","+userNumber);
			}
		editor.putString(ALL_RECENT_USERS, values);
		editor.commit();
	}
	public void saveDomainJoinedCount(String id) {
		editor.putString(DOMAIN_JOINED_COUNTS, id);
		editor.commit();
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
		editor.putInt(GROUP_SERVER_STATE+groupId, id);
		editor.commit();
	}
	public int getServerGroupState(String groupId) {
		int value = pref.getInt(GROUP_SERVER_STATE+groupId, GroupCreateTaskOnServer.SERVER_GROUP_UPDATE_NOTALLOWED);
		return value;
	}
	public int getBlockOrder() {
		int value = pref.getInt(BLOCK_ORDER, 1);
		return value;
	}
	public Set<String>  getBlockList() {
		Set<String> value = pref.getStringSet(BLOCK_LIST, null);
		return value;
	}
	public void saveBlockedUser(String user) {
		Set<String> existedList = getBlockList();
		if(existedList == null)
			existedList = new HashSet<String>(); 
		existedList.add(user);
		editor.putStringSet(BLOCK_LIST, existedList);
		editor.commit();
	}
	public void removeBlockedUser(String user) {
		Set<String> existedList = getBlockList();
		if(existedList == null)
			return; 
		existedList.remove(user);
		editor.putStringSet(BLOCK_LIST, existedList);
		editor.commit();
	}
	public void saveBlockOrder(int newOrder) {
		if(newOrder==-1)
			editor.putInt(BLOCK_ORDER, getBlockOrder()+1);
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
	public void saveLastOnline(long time) { // new1
		editor.putLong(LAST_ONLINE, time);
		editor.commit();
	}
	public long getLastOnline() { // new1
		long value = pref.getLong(LAST_ONLINE, 0);
		return value;
	}
	public void saveUserFileId(String userName,String fileId) {
		if(fileId != null && !fileId.equals("")){
			editor.putString(USER_FILE_ID+userName, fileId);
			editor.commit();
		}
	}
	public void saveSharedIDFileId(String sharedid, String fileId) {
		if(fileId != null && !fileId.equals("")){
			editor.putString(SHARED_ID_FILE_ID+sharedid, fileId);
			editor.commit();
		}
	}
	public String getUserFileId(String userName) {
		String value = pref.getString(USER_FILE_ID+userName, null);
		return value;
	}
	public String getSharedIDFileId(String sharedid) {
		String value = pref.getString(SHARED_ID_FILE_ID+sharedid, null);
		return value;
	}
	public void saveSGFileId(String userName,String fileId) {
		editor.putString(SG_FILE_ID+userName, fileId);
		editor.commit();
	}
	public void saveSharedIDData(String data) {
		editor.putString(SHARED_ID_DATA, data);
		editor.commit();
	}
	public String getSGFileId(String userName) {
		String value = pref.getString(SG_FILE_ID+userName, null);
		return value;
	}
	public String getSharedIDData() {
		String value = pref.getString(SHARED_ID_DATA, null);
		return value;
	}
	public void saveGroupOwnerName(String groupname, String owner) {
		editor.putString(CHANNEL_OWNER+groupname, owner);
		editor.commit();
	}
	public String getGroupOwnerName(String groupname) {
		String value = pref.getString(CHANNEL_OWNER+groupname, null);
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
		editor.putBoolean(groupName+"_"+groupPerson+"_"+infoType, isSet);
		editor.commit();
	}
	public void saveGroupInfo(String groupName, byte infoType, boolean isSet) {
//		String myName = getUserNameId();
		editor.putBoolean(groupName+"_"+infoType, isSet);
		editor.commit();
	}
	public boolean getUserGroupInfo(String groupName, String groupPerson, byte infoType) {
		boolean value = pref.getBoolean(groupName+"_"+groupPerson+"_"+infoType, false);
		return value;
	}
	public void saveGroupTypeAsPublic(String groupName, boolean isSet) {
//		String myName = getUserNameId();
		editor.putBoolean(groupName+"_"+PUBLIC_GROUP, isSet);
		editor.commit();
	}
	public void saveUseBroadCastInfo(String broadCastName, String broadCastPerson, byte infoType, boolean isSet) {
//		String myName = getUserNameId();
		editor.putBoolean(broadCastName+"_"+broadCastPerson+"_"+infoType, isSet);
		editor.commit();
	}
	public boolean isPublicGroup(String groupName){
		return pref.getBoolean(groupName+"_"+PUBLIC_GROUP, false);
	}
	public boolean isAdmin(String groupName, String groupPerson){
		return pref.getBoolean(groupName+"_"+groupPerson+"_"+GROUP_ADMIN_INFO, false);
	}
	public boolean isBroadCastAdmin(String broadCastName, String broadCastPerson){
		return pref.getBoolean(broadCastName+"_"+broadCastPerson+"_"+BROADCAST_ADMIN_INFO, false);
	}
	public boolean isOwner(String groupName, String groupPerson){
		return pref.getBoolean(groupName+"_"+groupPerson+"_"+GROUP_OWNER_INFO, false);
	}
	public boolean isBroadCastOwner(String broadCastName, String broadCastPerson){
		return pref.getBoolean(broadCastName+"_"+broadCastPerson+"_"+BROADCAST_OWNER_INFO, false);
	}
	public boolean isGroupMemberActive(String groupName, String groupPerson){
		return pref.getBoolean(groupName+"_"+groupPerson+"_"+GROUP_ACTIVE_INFO, true);
	}
	public boolean isGroupActive(String groupName){
		return pref.getBoolean(groupName+"_"+GROUP_ACTIVE_INFO, false);
	}
	public boolean isBroadCastActive(String broadCastName, String broadCastPerson){
		return pref.getBoolean(broadCastName+"_"+broadCastPerson+"_"+BROADCAST_ACTIVE_INFO, false);
	}
	public boolean saveUsersOfGroup(String groupName,String groupPerson) {
		String prevName = getUsersOfGroup(groupName);
		if(isGroupUserPresent(prevName,groupPerson))
			return false;
		if (prevName.equals(""))
			editor.putString(GROUP_USERS+groupName, groupPerson);
		else
			editor.putString(GROUP_USERS+groupName, prevName + "%#%" + groupPerson);
		editor.commit();
		return true;
	}
	
	public boolean saveUsersOfBroadCast(String broadCastName,String broadCastPerson) {
		String prevName = getUsersOfBroadCast(broadCastName);
		if(isBroadCastUserPresent(prevName,broadCastPerson))
			return false;
		if (prevName.equals(""))
			editor.putString(BROADCAST_USERS+broadCastName, broadCastPerson);
		else
			editor.putString(BROADCAST_USERS+broadCastName, prevName + "%#%" + broadCastPerson);
		editor.commit();
		return true;
	}
	
	private boolean isGroupUserPresent(String allUsers,String groupPerson){
		for(String person:allUsers.split("%#%")){
			if(person.equals(groupPerson))
				return true;
		}
		return false;
	}
	
	private boolean isBroadCastUserPresent(String allUsers,String broadCastPerson){
		for(String person:allUsers.split("%#%")){
			if(person.equals(broadCastPerson))
				return true;
		}
		return false;
	}
	
	public void saveGroupName(String groupName,String displayName) {
		String prevName = getGroupName();
		if(prevName.contains(groupName))
			return;
		saveGroupDisplayName(groupName, displayName);
		if (prevName.equals(""))
			editor.putString(GROUP_NAME, groupName);
		else
			editor.putString(GROUP_NAME, prevName + "%#%" + groupName);
		editor.commit();
	}
	public void saveBroadCastName(String broadCastName,String displayName) {
		String prevName = getBroadCastName();
		if(prevName.contains(broadCastName))
			return;
		saveBroadCastDisplayName(broadCastName, displayName);
		if (prevName.equals(""))
			editor.putString(BROADCAST_NAME, broadCastName);
		else
			editor.putString(BROADCAST_NAME, prevName + "%#%" + broadCastName);
		editor.commit();
	}
	public void removeUsersFromGroup(String groupName,String groupPerson) {
		String result = "";
		String prevName = getUsersOfGroup(groupName);
		if (!prevName.equals("")){
			if(isGroupUserPresent(prevName,groupPerson)){
				if(prevName.contains("%#%")){
					ArrayList<String> list = new ArrayList<String>(Arrays.asList(prevName.split("%#%")));
					for(String item:list){
						if(!item.equals(groupPerson))
							result+=(item+"%#%");
					}
					if(result.endsWith("%#%"))
						result = result.substring(0, result.lastIndexOf("%#%"));
				}else{
					result = prevName.replace(groupPerson, "");
				}
			editor.putString(GROUP_USERS+groupName, result);
			editor.commit();
		}
		}
	}
	public void removeUsersFromBroadCast(String broadCastName,String broadCastPerson) {
		String result = "";
		String prevName = getUsersOfBroadCast(broadCastName);
		if (!prevName.equals("")){
			if(isBroadCastUserPresent(prevName,broadCastPerson)){
				if(prevName.contains("%#%")){
					ArrayList<String> list = new ArrayList<String>(Arrays.asList(prevName.split("%#%")));
					for(String item:list){
						if(!item.equals(broadCastPerson))
							result+=(item+"%#%");
					}
					if(result.endsWith("%#%"))
						result = result.substring(0, result.lastIndexOf("%#%"));
				}else{
					result = prevName.replace(broadCastPerson, "");
				}
			editor.putString(BROADCAST_USERS+broadCastName, result);
			editor.commit();
		}
		}
	}
	
	public void removeGroupName(String groupName) {
		String prevName = getGroupName();
		removeGroupDisplayName(groupName);
		if (!prevName.equals("")){
			if(prevName.contains(groupName)){
				if(prevName.contains(groupName+"%#%")){
					prevName = prevName.replace(groupName+"%#%", "");
				}else if(prevName.contains("%#%"+groupName)){
					prevName = prevName.replace("%#%"+groupName, "");
				}else
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
		if (!prevName.equals("")){
			if(prevName.contains(broadCastName)){
				if(prevName.contains(broadCastName+"%#%")){
					prevName = prevName.replace(broadCastName+"%#%", "");
				}else if(prevName.contains("%#%"+broadCastName)){
					prevName = prevName.replace("%#%"+broadCastName, "");
				}else
					prevName = prevName.replace(broadCastName, "");
			editor.putString(BROADCAST_NAME, prevName);
			editor.commit();
		}
		}
	}
	public boolean isGroupChat(String groupName) {
		boolean ret = false;
		if(groupName==null)
			return false;
		String groups = getGroupName();
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
		if(broadCastName==null)
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
		String value = pref.getString(GROUP_USERS+groupName, "");
		return value;
	}
	public String getUsersOfBroadCast(String broadCastName) {
		String value = pref.getString(BROADCAST_USERS+broadCastName, "");
		return value;
	}
	public String getGroupName() {
		String value = pref.getString(GROUP_NAME, "");
		return value;
	}
	public String getBroadCastName() {
		String value = pref.getString(BROADCAST_NAME, "");
		return value;
	}
	public String[] getGroupNamesArray() {
		String array[] = new String[1];
		String groups = getGroupName();
		if (groups != null) {
			if (!groups.equals("") && groups.contains("%#%")) {
				array = groups.split("%#%");
				
			} else{
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
				
			} else{
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
			} else{
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
			} else{
				list.add(groups);
			}
		}
		return list;
	}
	public String getDisplayName() {
		String value = pref.getString(USER_DISPLAY_NAME, null);
		return value;
	}

	public String getUserStatusMessage(String userName) {
		String value = pref.getString(USER_STATUS_MESSAGE+userName, "");
		return value;
	}
	public String getGroupMemberCount(String groupname) {
		String value = pref.getString(GROUP_MEM_COUNT+groupname , "");
		return value;
	}
	public void saveGroupMemberCount(String groupname, String count) {
		editor.putString(GROUP_MEM_COUNT+groupname, count);
		editor.commit();
	}
	public String getUserDesignation(String userName) {
		String value = pref.getString(USER_DESIGNATION+userName, "");
		return value;
	}
	public String getUserDepartment(String userName) {
		String value = pref.getString(USER_DEPARTMENT+userName, "");
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

	public void saveUserEmail(String phone) {
		editor.putString(USER_EMAIL, phone);
		editor.commit();
	}

	public String getUserEmail() {
		String value = pref.getString(USER_EMAIL, null);
		return value;
	}

	public void saveDisplayName(String name) {
		editor.putString(USER_DISPLAY_NAME, name);
		editor.commit();
	}
	public String getUserServerName(String userName) {
//		String value = userName;
		String value = "Superchatter";
		try{
		value = pref.getString(USER_DISPLAY_NAME+userName, userName);
		}catch(Exception e){}
		return value;
	}
	public String getUserServerDisplayName(String userName) {
		String value = null;
		try{
			value = pref.getString(USER_DISPLAY_NAME+userName, "");
		}catch(Exception e){
			
		}
		return value;
	}
	public String getUserServerNameIfExists(String userName) {
		String value = null;
		try{
			value = pref.getString(USER_DISPLAY_NAME+userName, userName);
		}catch(Exception e){}
		return value;
	}
	public void saveUserServerName(String userName , String name) {
		editor.putString(USER_DISPLAY_NAME+userName, name);
		editor.commit();
	}
	public void saveUserStatusMessage(String userName , String status) {
		if(status!=null && status.contains("ESIA"))
			status = status.replace("ESIA", "Super");
		editor.putString(USER_STATUS_MESSAGE+userName, status);
		editor.commit();
	}
	public void saveUserDesignation(String userName , String designation) {
		editor.putString(USER_DESIGNATION+userName, designation);
		editor.commit();
	}
	public void saveUserDepartment(String userName , String department) {
		editor.putString(USER_DEPARTMENT+userName, department);
		editor.commit();
	}
	public void saveUserName(String name) {
		editor.putString(USER_NAME_ID, name);
		editor.commit();
	}
	public void saveUserDomain(String domain) {
		editor.putString(USER_DOMAIN, domain);
		editor.commit();
	}

	public String getUserDomain() {
		String value = pref.getString(USER_DOMAIN, "");
		return value;
	}
	public void saveUserGender(String userName, String gender) {
		editor.putString(USER_GENDER+userName, gender);
		editor.commit();
	}
	
	public String getUserGender(String userName) {
		String value = pref.getString(USER_GENDER+userName, "male");
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
	public String getGroupDisplayName(String room) {
		String value = room;
		try{
		value = pref.getString(GROUP_DISPLAY_NAME+room, room);
		}catch(Exception e){}
		return value;
	}
	
	public String getBroadCastDisplayName(String room) {
		String value = room;
		try{
		value = pref.getString(BROADCAST_DISPLAY_NAME+room, room);
		}catch(Exception e){}
		return value;
	}
	public String getSharedIDDisplayName(String room) {
		String value = null;
		try{
			value = pref.getString(SHARED_ID_DISPLAY_NAME+room, null);
		}catch(Exception e){}
		return value;
	}
	public void saveGroupDisplayName(String room,String displayName) {
		editor.putString(GROUP_DISPLAY_NAME+room, displayName);
		editor.commit();
	}
	public void saveBroadCastDisplayName(String room,String displayName) {
		editor.putString(BROADCAST_DISPLAY_NAME+room, displayName);
		editor.commit();
	}
	public void saveSharedIDDisplayName(String room, String displayName) {
		editor.putString(SHARED_ID_DISPLAY_NAME+room, displayName);
		editor.commit();
	}
	public void removeGroupDisplayName(String room) {
		editor.remove(GROUP_DISPLAY_NAME+room);
		editor.commit();
	}
	public void removeBroadCastDisplayName(String room) {
		editor.remove(BROADCAST_DISPLAY_NAME+room);
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

	public void saveChatCounter(int counter) {
		editor.putInt(CHAT_COUNTER, counter);
		editor.commit();
	}
	public void saveBulletinChatCounter(int counter) {
		editor.putInt(BULLETIN_CHAT_COUNTER, counter);
		editor.commit();
	}
	public void saveNewContactsCounter(int counter) {
		editor.putInt(CONTACT_COUNTER, counter);
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
		Log.i("SharedPrefManager", "Get : key : "+(group + LISTENING)+", value : "+user);
		editor.putString(group + LISTENING, user);
		editor.commit();
	}

	public boolean getUserListeningStatus(String person) {
		boolean value = false;
		try{
		 value = pref.getBoolean(person + LISTENING, false);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return value;
	}
	public String getUserListeningStatusForGroup(String group) {
		String user = "";
		try{
			user = pref.getString(group + LISTENING, "");
			Log.i("SharedPrefManager", "Get : key : "+(group + LISTENING)+", value : "+user);
		}catch(Exception ex){
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
		try{
			value = pref.getBoolean(person + RECORDING, false);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return value;
	}
	public void saveUserRecordingStatusForGroup(String group, String user) {
		editor.putString(group + RECORDING, user);
		Log.i("SharedPrefManager", "Set : key : "+(group + RECORDING)+", value : "+user);
		editor.commit();
	}
	public String getUserRecordingStatusForGroup(String group) {
		String user = "";
		try{
			user = pref.getString(group + RECORDING, "");
			Log.i("SharedPrefManager", "Get : key : "+(group + RECORDING)+", value : "+user);
		}catch(Exception ex){
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
		try{
		  value = pref.getBoolean(person + ISTYPING, false);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return value;
	}
	public void saveUserTypingStatusForGroup(String group, String user) {
		Log.i("SharedPrefManager", "Set : key : "+(group + TYPING)+", value : "+user);
		editor.putString(group + TYPING, user);
		editor.commit();
	}

	public String getUserTypingStatusForGroup(String group) {
		String user = "";
		try{
			user = pref.getString(group + TYPING, "");
			Log.i("SharedPrefManager", "Get : value for key : "+(group + TYPING)+", value : "+user);
		}catch(Exception ex){
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

	public int getChatCounter() {
		int value = pref.getInt(CHAT_COUNTER, 0);
		return value;
	}
	public int getBulletinChatCounter() {
		int value = pref.getInt(BULLETIN_CHAT_COUNTER, 0);
		return value;
	}
	public int getNewContactsCounter() {
		int value = pref.getInt(CONTACT_COUNTER, 0);
		return value;
	}
	public int getChatCountOfUser(String person) {
		int value = pref.getInt(person, 0);
		return value;
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
