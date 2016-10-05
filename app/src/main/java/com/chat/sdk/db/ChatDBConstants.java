package com.chat.sdk.db;

public class ChatDBConstants {
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
	public static final String READ_TIME_FIELD = "deliver_time_field";
	public static final String MESSAGEINFO_FIELD = "type_messageinfo";
	public static final String PICTURE_MESSAGE_PATH = "picture_message_path";
	public static final String CONTACT_ID_FIELD = "contact_id";

	// contact name constants
	public static final String NAME_CONTACT_ID_FIELD = "name_contact_id";
	public static final String USER_NAME_FIELD = "user_name";
	public static final String CONTACT_NAMES_FIELD = "contact_names";
	public static final String CONTACT_COMPOSITE_FIELD = "contact_composite_numbers";
	public static final String VOPIUM_FIELD = "vopium";
	public static final String IS_FAVOURITE_FIELD = "is_favourite";
	public static final String USER_SIP_ADDRESS = "user_sip_address";

	

	// contact number constants
	public static final String CONTACT_NUMBERS_FIELD = "contact_numbers";
	public static final String DATA_ID_FIELD = "data_id";
	public static final String DATA_CONTACT_ID_FIELD = "data_contact_id";
	public static final String STATE_FIELD = "state";

	
	public static final String PHONE_EMAILS_FIELD = "phone_emails";

	public static final String MESSAGE_ID = "message_id";
	public static final String BROADCAST_MESSAGE_ID = "broadcast_message_id";
	public static final String FROM_USER_FIELD = "from_user";
	public static final String FROM_GROUP_USER_FIELD = "from_group_user";
	public static final String TO_USER_FIELD = "to_user";
	public static final String IS_DATE_CHANGED_FIELD = "is_date_changed";
	public static final String FOREIGN_MESSAGE_ID_FIELD = "foreign_message_id";
	/**
	 * You can put following enum values for this field.
	 * Message.SeenState.sent,
	 * Message.SeenState.recieved,
	 * Message.SeenState.seen,
	 * Message.SeenState.wait
	 */
	//New Added Fields
	public static final String USER_ID = "user_id";
	public static final String USER_SG = "user_sg";
	//Old Fields
	public static final String MESSAGE_TYPE = "message_type";
	public static final String MESSAGE_TYPE_FIELD = "message_type_id";
	public static final String MESSAGE_THUMB_FIELD = "message_thumb_image";
	public static final String MESSAGE_MEDIA_URL_FIELD = "message_media_url";
	public static final String MESSAGE_MEDIA_LENGTH = "message_media_length";
	public static final String MESSAGE_MEDIA_LOCAL_PATH_FIELD = "message_media_local";
	public static final String MEDIA_CAPTION_TAG = "media_caption_tag";
	public static final String MESSAGE_TYPE_LOCATION = "message_type_location";
	public static final String MEDIA_STATUS = "media_status";
	public static final String READ_USER_COUNT_FIELD = "read_user_count";
	public static final String TOTAL_USER_COUNT_FIELD = "total_user_count";
	public static final String TABLE_MESSAGE_INFO = "CREATE TABLE IF NOT EXISTS "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO
			+ "("
			+ ChatDBConstants._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ ChatDBConstants.MESSAGE_ID
			+ " TEXT NOT NULL,"
			+ ChatDBConstants.MEDIA_CAPTION_TAG
			+ " TEXT,"
			+ ChatDBConstants.MESSAGE_TYPE_LOCATION
			+ " TEXT,"
			+ ChatDBConstants.BROADCAST_MESSAGE_ID
			+ " TEXT,"
			+ ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD
			+ " TEXT NOT NULL,"
			+ ChatDBConstants.CONTACT_NAMES_FIELD
			+ " TEXT NOT NULL,"
			+ ChatDBConstants.FROM_GROUP_USER_FIELD
			+ " TEXT,"
			+ ChatDBConstants.FROM_USER_FIELD
			+ " TEXT NOT NULL,"
			+ ChatDBConstants.TO_USER_FIELD
			+ " TEXT NOT NULL,"
			+ ChatDBConstants.UNREAD_COUNT_FIELD
			+ " INTEGER NOT NULL,"
			+ ChatDBConstants.READ_USER_COUNT_FIELD
			+ " INTEGER DEFAULT 0,"
			+ ChatDBConstants.TOTAL_USER_COUNT_FIELD
			+ " INTEGER DEFAULT 0,"
			+ ChatDBConstants.SEEN_FIELD
			+ " INTEGER NOT NULL,"
			+ ChatDBConstants.MEDIA_STATUS
			+ " INTEGER  DEFAULT 0, "
			+ ChatDBConstants.READ_TIME_FIELD
			+ " LONG DEFAULT 0, "
			+ ChatDBConstants.LAST_UPDATE_FIELD
			+ " LONG NOT NULL,"
			+ ChatDBConstants.IS_DATE_CHANGED_FIELD
			+ " BOOLEAN,"
			+ ChatDBConstants.MESSAGEINFO_FIELD
			+ " TEXT NOT NULL,"
			+ ChatDBConstants.MESSAGE_THUMB_FIELD
			+ " TEXT,"
			+ ChatDBConstants.MESSAGE_MEDIA_URL_FIELD
			+ " TEXT,"
			+ ChatDBConstants.MESSAGE_MEDIA_LENGTH
			+ " TEXT,"
			+ ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD
			+ " TEXT,"
			+ ChatDBConstants.MESSAGE_TYPE
			+ " INTEGER  DEFAULT 0, "
			+ ChatDBConstants.USER_ID
			+ " LONG, "
			+ ChatDBConstants.USER_SG
			+ " TEXT,"
			+ ChatDBConstants.MESSAGE_TYPE_FIELD + " INTEGER DEFAULT 0);";
	
	// user message status info
		public static final String TABLE_NAME_STATUS_INFO = "status_info_table";
		public static final String DELIVER_TIME_FIELD = "deliver_time";
		public static final String SEEN_TIME_FIELD = "seen_time";
		public static final String GROUP_UUID_FIELD = "group_uuid";
		
		public static final String TABLE_STATUS_INFO = "CREATE TABLE IF NOT EXISTS "
				+ ChatDBConstants.TABLE_NAME_STATUS_INFO+ "("
				+ ChatDBConstants._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ChatDBConstants.MESSAGE_ID+ " TEXT NOT NULL,"
//				+ ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD+ " TEXT NOT NULL,"
				+ChatDBConstants.SEEN_FIELD+ " INTEGER NOT NULL,"
				+ ChatDBConstants.DELIVER_TIME_FIELD+ " LONG,"
				+ ChatDBConstants.SEEN_TIME_FIELD+ " LONG,"
				+ ChatDBConstants.USER_ID
				+ " LONG, "
				+ ChatDBConstants.USER_SG
				+ " TEXT,"
				+ ChatDBConstants.FROM_USER_FIELD+ " TEXT NOT NULL);";
//				+ ChatDBConstants.GROUP_UUID_FIELD+ " TEXT);";
}
