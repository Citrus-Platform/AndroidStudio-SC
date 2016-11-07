package com.superchat.utils;

import android.graphics.Bitmap.CompressFormat;

import java.util.regex.Pattern;

public class Constants {
	public static final String BACKUP_FILEID = "backup_fileid";
	public static final String LAST_BACKUP_DATE = "last_backup_date";
	public static final String PROFILE_UPDATE = "profile_update";
	public static final String MOBILE_NUMBER_TXT = "mobile_number";
	public static final String COUNTRY_CODE_TXT = "country_code";
	public static final String DOMAIN_NAME = "domain_name";
	public static final String EMAIL = "email";
	public static final String NAME = "name";
	public static final String ORGNAME = "org_name";
	public static final String REG_TYPE = "reg_type";
	public static final String contentTemp = "SuperChat/temp";
	public static final String imageTempPath = "SuperChat/streem/tmp/SuperChat";
	private static final String LOCAL_IP = "52.74.195.75";

	public static final String KEY_SCREEN_USER_COMING_FROM = "reg_type";
	public static final String SG_CREATE_AFTER_LOGIN = "sg_create_after_login";
	public static final String VAL_SCREEN_USER_COMING_FROM_NEW_USER_AFTER_LOGIN = "newUserAfterLogin";
	public static final String VAL_SCREEN_USER_COMING_FROM_NEW_USER_BEFORE_LOGIN = "newUserBeforeLogin";

//Ireland
//private static final String PRODUCTION_IP = "52.208.65.253";
//public static final String LIVE_DOMAIN = "52.208.65.253";

//==============================================================

// Dev Sever
//private static final String PRODUCTION_IP = "52.24.75.58";
//public static final String 	LIVE_DOMAIN = "http://52.24.75.58";

//==============================================================

// Live Server
//private static final String PRODUCTION_IP = "52.88.175.48";
private static final String PRODUCTION_IP = "superchat3c.com";
public static final String 	LIVE_DOMAIN = "http://superchat3c.com";


private static final String TMP_IP = "52.74.197.243";

public static final String CHAT_SERVER_URL = PRODUCTION_IP; // LOCAL_IP;

public static final String CHAT_DOMAIN = CHAT_SERVER_URL;//"ip-172-31-31-148";

public static final String CHAT_SERVER_PORT = "5222";
public static String countryCode = "+91";

public static  String SELF_VARIFICATION_MSG = "SuperChat Self verification code is 7496";
public static final Pattern NAME_PATTERN = Pattern.compile("^\\s*([A-Za-z ]{3,})(\\s*([A-Za-z ]+?))?\\s*$");
public static final Pattern EMAIL_PATTERN = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
public static final Pattern MOBILE_NUMBER_PATTERN = Pattern.compile("^\\d{1,5}[-]\\d{6,12}$");
public static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,29}$");

//public static String media_post_url = "http://"+TMP_IP+":8080/tejas/feeds/api/fileupload";
//public static String media_get_url = "http://"+TMP_IP+":8080/rtMediaServer/get/";

public static String media_post_url = "http://"+CHAT_SERVER_URL+"/tejas/feeds/api/fileupload";
public static String media_get_url = "http://"+CHAT_SERVER_URL+"/rtMediaServer/get/";
public static String media_convertget_url = "http://"+CHAT_SERVER_URL+"/rtMediaServer/convertget/";

public static String SELF_VARIFICATION_NUM = null;

public static final String SERVER_URL = "http://"+CHAT_SERVER_URL;//+":8080";
//------------------- Constants for image-------------
public static final String defaultVideoPath = "/sdcard/f1.3gp"; 
public static final String imagePath = "SuperChat/streem/image/SuperChat";
public static final String imagePathPost = "SuperChat/streem/imagepost/SuperChat";
public static final String contentVideo = "SuperChat/streem/video/SuperChat";
public static final String contentVoice = "SuperChat/streem/voice/SuperChat";
public static final String contentPost = "SuperChat/post";
	public static final String contentProfilePhoto = "SuperChat/.profile/";
	public static final String sentProfilePhoto = "SuperChat/.Sent/";

public static final String INVITATION_LIST = "INVITATION_LIST";
public static final String BROADCAST = "BROADCAST";
public static final String SHARED_ID_NAME = "SHARED_ID_NAME";
public static final String SHARED_ID = "SHARED_ID";
public static final String SHAREDID_UPDATE = "SHARED_ID_UPDATE";
public static final String OPEN_CHANNEL = "OPEN_CHANNEL";
public static final String CHANNEL_CREATION = "channel_creation";
public static final String GROUP_NAME = "GROUP_NAME";
public static final String GROUP_DISCRIPTION = "GROUP_DISCRIPTION";
public static final String GROUP_TYPE = "GROUP_TYPE";
public static final String GROUP_UUID = "group_uuid";
public static final String GROUP_FILE_ID = "group_file_id";
public static final String IS_GROUP_CREATION = "is_group_creation";
public static final String IS_GROUP_INFO_UPDATE = "is_group_info_update";
public static final String IS_GROUP_INVITATION = "is_group_invitation";
public static final String CHAT_TYPE = "CHAT_TYPE";
public static final String CHAT_USER_NAME = "chat_user_name";
public static final String CHAT_NAME = "chat_name";
public static final String GROUP_USERS = "group_users";
public static final String CONTACT_NAME = "contact_name";
public static final String CONTACT_NUMBERS = "Contact_numbers";
public static final String CONTACT_EMAILS = "Contact_emails";
public static final String CONTACT_TYPE_NUMBERS = "Contact_type_numbers";
public static final String CONTACT_TYPE_EMAILS = "Contact_type_emails";
public static final String USER_MAP = "user_map";
public static final String SELECTED_MESSAGE_ID = "selected_message_id";
public static final byte NARMAL_CHAT = 0;
public static final byte GROUP_USER_CHAT_CREATE = 1;
public static final byte MULTI_USER_CHAT_CREATE = 2;
public static final byte MULTI_USER_CHAT_INVITE = 3;
public static final byte GROUP_USER_CHAT_INVITE = 4;
public static final byte GROUP_USER_SELECTED_LIST = 5;
public static final byte MULTI_USER_SELECTED_LIST = 6;
public static final byte BROADCAST_LIST_CRATE = 7;
public static final byte BROADCAST_LIST_UPDATE = 8;
public static final byte MEMBER_DELETE = 9;
public static final byte GROUP_USERS_ROLE_SELECTION = 10;
public static final byte MEMBER_STATS = 11;
public static final byte SHARED_ID_CREATE = 12;
public static final byte SHARED_ID_UPDATE = 13;
public static final int CHUNK_LENGTH = 1024;
public static final int IN_SAMPLE_SIZE = 1;//4
public static final int COMPRESS = 80;//100
public static final CompressFormat COMPRESS_TYPE = CompressFormat.PNG;//100
public static final String ID_FOR_UPDATE_PROFILE = "UPDATE_PROFILE";

public static final String PIC_SEP = "[PIC]::";
public static final String VOICE_SEP = "[VOICE]::";
public static final String VIDEO_SEP = "[VIDEO]::";

public static final int MAX_AUDIO_RECORD_TIME = 300;
public static final int MAX_AUDIO_RECORD_TIME_REST = 300;
public static final float dimamount = 0.4f;
final public static byte UI_STATE_INIT = 1;
final public static byte UI_STATE_IDLE = 2;
final public static byte UI_STATE_RECORDING = 3;
public static final byte MESSAGE_TEXT = 0;
public static final byte MESSAGE_PICTURE = 7;
public static final byte MESSAGE_AUDIO = 8;
public static final byte MESSAGE_VIDEO = 9;
public static final byte MESSAGE_LOCATION = 10;
public static final byte MESSAGE_PDF = 11;
public static final byte MESSAGE_DOC = 12;
public static final byte MESSAGE_PPT = 13;
public static final byte MESSAGE_XLS = 14;
public static final byte MESSAGE_CONTACT = 16;
public static final int FILE_DIMEN = 160;

public static final String SHARED_ID_START_STRING = "00000aone##";

//=========================== For Push Notifications ==========
public static boolean isBuildLive = true;
//LIVE
//public static final String GOOGLE_PROJECT_ID_PROD = "370540499760";
public static final String GOOGLE_PROJECT_ID_PROD =   "807027113590";
public static final String GOOGLE_PUSH_API_KEY_LIVE = "AIzaSyDMfJcsLiYeAm8BrecztSkDKI5YJrokNUE";

//DEV
public static final String GOOGLE_PROJECT_ID_DEV = "807027113590";//change this for dev, this is copied
public static final String GOOGLE_PUSH_API_KEY_DEV = "AIzaSyDMfJcsLiYeAm8BrecztSkDKI5YJrokNUE";

public static String regid;

}
