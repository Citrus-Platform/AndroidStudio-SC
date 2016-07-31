package com.superchat.data.db;

import com.chat.sdk.db.ChatDBConstants;

public class DatabaseConstants {
public static final byte MEDIA_NONE = 0;
public static final byte MEDIA_READY_TO_LOAD = 1;
public static final byte MEDIA_LOADING = 2;
public static final byte MEDIA_LOADED = 3;
public static final byte MEDIA_FAILED = 4;
	// messaging constants
	public static final String _ID = "_id";
	public static final String TABLE_NAME_MESSAGE_INFO = "message_info_table";
	public static final String MESSAGING_PERSON_FIELD = "messaging_persons";
	public static final String MESSAGING_NUMBER_FIELD = "messaging_number";
	public static final String DIALED_NUMBER_FIELD = "dialed_number";
	public static final String UNREAD_COUNT_FIELD = "unread_count";
	public static final String SEEN_FIELD = "seen";
	public static final String LAST_UPDATE_FIELD = "last_update";
	public static final String MESSAGEINFO_FIELD = "type_messageinfo";
	public static final String PICTURE_MESSAGE_PATH = "picture_message_path";
	public static final String CONTACT_ID_FIELD = "contact_id";
	public static final String CONTACT_VERSION = "contact_version";
	// contact name constants
	public static final String TABLE_NAME_CONTACT_NAMES = "contact_names_table";
	public static final String NAME_CONTACT_ID_FIELD = "name_contact_id";
	public static final String USER_NAME_FIELD = "user_name";
	public static final String CONTACT_NAMES_FIELD = "contact_names";
	public static final String CONTACT_TYPE_FIELD = "contact_type";
	public static final String CONTACT_COMPOSITE_FIELD = "contact_composite_numbers";
	public static final String VOPIUM_FIELD = "vopium";
	public static final String IS_FAVOURITE_FIELD = "is_favourite";
	public static final String FLAT_NUMBER = "flat_number";
	public static final String BUILDING_NUMBER = "building_number";
	public static final String ADDRESS = "address";
	public static final String RESIDENCE_TYPE = "residence_type";
//	public static final String USER_SIP_ADDRESS = "user_sip_address";

	public static final String POLL_ID = "poll_id";
	public static final String POLL_ADMIN = "poll_admin";
	public static final String POLL_MEMBER_USERNAME = "poll_member_username";
	public static final String POLL_MEMBER_NAME = "poll_member_name";
	public static final String POLL_START_DATE_TIME = "start_time";
	public static final String POLL_END_DATE_TIME = "end_time";
	public static final String POLL_STATE = "poll_state";
	public static final String POLL_TITLE = "poll_title";
	public static final String POLL_TYPE = "poll_type";
	public static final String POLL_TXT_MESSAGE = "poll_txt_message";


	//Poll related Data -

	public static final String TABLE_CONTACT_NAMES = "CREATE TABLE IF NOT EXISTS "
			+ DatabaseConstants.TABLE_NAME_CONTACT_NAMES
			+ "("
			+ DatabaseConstants._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DatabaseConstants.NAME_CONTACT_ID_FIELD
			+" INTEGER NOT NULL,"
			+ DatabaseConstants.RAW_CONTACT_ID
			+ " LONG NOT NULL,"
			+ DatabaseConstants.CONTACT_NAMES_FIELD
			+ " TEXT NOT NULL, "
			+ DatabaseConstants.CONTACT_COMPOSITE_FIELD
			+ " TEXT, "
			+ DatabaseConstants.USER_NAME_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.VOPIUM_FIELD
			+ " BOOLEAN,"
			+ DatabaseConstants.CONTACT_VERSION
			+ " INTEGER,"
			+ DatabaseConstants.IS_FAVOURITE_FIELD + " INTEGER NOT NULL);";

	// contact number constants
	public static final String TABLE_NAME_CONTACT_NUMBERS = "contact_numbers_table";
	public static final String TABLE_NAME_ALL_CONTACT_NUMBERS = "all_contact_numbers_table";
	public static final String CONTACT_NUMBERS_FIELD = "contact_numbers";
	public static final String DISPLAY_NUMBERS_FIELD = "display_numbers";
	public static final String DATA_ID_FIELD = "data_id";
	public static final String RAW_CONTACT_ID = "raw_contact_id";
	public static final String DATA_CONTACT_ID_FIELD = "data_contact_id";
	public static final String STATE_FIELD = "state";
	public static final String PHONE_NUMBER_TYPE_FIELD = "phone_number_type";

	public static final String TABLE_ALL_CONTACT_NUMBERS = "CREATE TABLE IF NOT EXISTS "
			+ DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS
			+ "("
			+ DatabaseConstants._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DatabaseConstants.CONTACT_NUMBERS_FIELD
			+ " LONG UNIQUE NOT NULL,"
			+ DatabaseConstants.DISPLAY_NUMBERS_FIELD
			+ " TEXT,"
			+ DatabaseConstants.RAW_CONTACT_ID
			+ " LONG NOT NULL,"
			+ DatabaseConstants.CONTACT_NAMES_FIELD
			+ " TEXT,"
			+ DatabaseConstants.CONTACT_TYPE_FIELD
			+ " TEXT,"
			+ DatabaseConstants.CONTACT_COMPOSITE_FIELD
			+ " TEXT, "
			+ DatabaseConstants.USER_NAME_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.VOPIUM_FIELD
			+ " BOOLEAN,"
			+ DatabaseConstants.DATA_ID_FIELD
			+ " LONG NOT NULL,"
			+ DatabaseConstants.NAME_CONTACT_ID_FIELD
			+ " INTEGER NOT NULL,"
			+ DatabaseConstants.PHONE_NUMBER_TYPE_FIELD
			+ " INTEGER NOT NULL,"
			+ DatabaseConstants.IS_FAVOURITE_FIELD + " INTEGER,"
			+ DatabaseConstants.STATE_FIELD + " INTEGER NOT NULL);";
	public static final String TABLE_CONTACT_NUMBERS = "CREATE TABLE IF NOT EXISTS "
			+ DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
			+ "("
			+ DatabaseConstants._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DatabaseConstants.CONTACT_NUMBERS_FIELD
			+ " LONG UNIQUE NOT NULL,"
			+ DatabaseConstants.DISPLAY_NUMBERS_FIELD
			+ " TEXT,"
			+ DatabaseConstants.RAW_CONTACT_ID
			+ " LONG NOT NULL,"
			+ DatabaseConstants.CONTACT_NAMES_FIELD
			+ " TEXT,"
			+ DatabaseConstants.CONTACT_TYPE_FIELD
			+ " TEXT,"
			+ DatabaseConstants.CONTACT_COMPOSITE_FIELD
			+ " TEXT, "
			+ DatabaseConstants.USER_NAME_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.VOPIUM_FIELD
			+ " BOOLEAN,"
			+ DatabaseConstants.DATA_ID_FIELD
			+ " LONG NOT NULL,"
			+ DatabaseConstants.NAME_CONTACT_ID_FIELD
			+ " INTEGER NOT NULL,"
			+ DatabaseConstants.PHONE_NUMBER_TYPE_FIELD
			+ " INTEGER NOT NULL,"
			+ DatabaseConstants.IS_FAVOURITE_FIELD + " INTEGER,"
			+ DatabaseConstants.FLAT_NUMBER + " TEXT, "
			+ DatabaseConstants.BUILDING_NUMBER + " TEXT, "
			+ DatabaseConstants.ADDRESS + " TEXT, "
			+ DatabaseConstants.RESIDENCE_TYPE + " TEXT, "
			+ DatabaseConstants.STATE_FIELD + " INTEGER NOT NULL);";

	// contact email constants
	public static final String TABLE_NAME_CONTACT_EMAILS = "contact_emails_table";
	public static final String PHONE_EMAILS_FIELD = "phone_emails";

	public static final String TABLE_CONTACT_EMAILS = "CREATE TABLE IF NOT EXISTS "
			+ DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
			+ "("
			+ DatabaseConstants._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DatabaseConstants.NAME_CONTACT_ID_FIELD
			+ " INTEGER UNIQUE NOT NULL,"
			+ DatabaseConstants.PHONE_EMAILS_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.CONTACT_NAMES_FIELD
			+ " TEXT,"
			+ DatabaseConstants.CONTACT_COMPOSITE_FIELD
			+ " TEXT, "
			+ DatabaseConstants.VOPIUM_FIELD
			+ " BOOLEAN,"
			+ DatabaseConstants.USER_NAME_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.IS_FAVOURITE_FIELD + " INTEGER,"
			+ DatabaseConstants.DATA_ID_FIELD + " LONG NOT NULL);";

	// user chat messages
	public static final String MESSAGE_ID = "message_id";
	public static final String FROM_USER_FIELD = "from_user";
	public static final String FROM_GROUP_USER_FIELD = "from_group_user";
	public static final String TO_USER_FIELD = "to_user";
	public static final String IS_DATE_CHANGED_FIELD = "is_date_changed";
	public static final String FOREIGN_MESSAGE_ID_FIELD = "foreign_message_id";
	public static final String MESSAGE_TYPE_FIELD = "message_type_id";
	public static final String MESSAGE_TYPE = "message_type";
	public static final String MESSAGE_THUMB_FIELD = "message_thumb_image";
	public static final String MESSAGE_MEDIA_URL_FIELD = "message_media_url";
	public static final String MESSAGE_MEDIA_LENGTH = "message_media_length";
	public static final String MESSAGE_MEDIA_LOCAL_PATH_FIELD = "message_media_local";
	public static final String MEDIA_STATUS = "media_status";
	public static final String TABLE_MESSAGE_INFO = "CREATE TABLE IF NOT EXISTS "
			+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO
			+ "("
			+ DatabaseConstants._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DatabaseConstants.MESSAGE_ID
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.FOREIGN_MESSAGE_ID_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.CONTACT_NAMES_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.FROM_GROUP_USER_FIELD
			+ " TEXT,"
			+ DatabaseConstants.FROM_USER_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.TO_USER_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.UNREAD_COUNT_FIELD
			+ " INTEGER NOT NULL,"
			+ DatabaseConstants.SEEN_FIELD
			+ " INTEGER NOT NULL,"
			+ DatabaseConstants.MEDIA_STATUS
			+ " INTEGER  DEFAULT 0, "
			+ DatabaseConstants.LAST_UPDATE_FIELD
			+ " LONG NOT NULL,"
			+ DatabaseConstants.IS_DATE_CHANGED_FIELD
			+ " BOOLEAN,"
			+ DatabaseConstants.MESSAGEINFO_FIELD
			+ " TEXT NOT NULL,"
			+ DatabaseConstants.MESSAGE_THUMB_FIELD
			+ " TEXT,"
			+ DatabaseConstants.MESSAGE_MEDIA_URL_FIELD
			+ " TEXT,"
			+ DatabaseConstants.MESSAGE_MEDIA_LENGTH
			+ " TEXT,"
			+ DatabaseConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD
			+ " TEXT,"
			+ ChatDBConstants.MESSAGE_TYPE
			+ " INTEGER  DEFAULT 0, "
			+ DatabaseConstants.MESSAGE_TYPE_FIELD + " INTEGER DEFAULT 0);";
	
	
}
