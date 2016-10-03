package com.superchat.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chat.sdk.db.ChatDBConstants;

public class PhoenixDatabaseHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 2;

	public PhoenixDatabaseHelper(Context context) {
		super(context, "superchat.db", null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqlitedatabase) {

//		sqlitedatabase.execSQL(DatabaseConstants.TABLE_MESSAGE_INFO);
		sqlitedatabase.execSQL(DatabaseConstants.TABLE_CONTACT_NAMES);
		sqlitedatabase.execSQL(DatabaseConstants.TABLE_CONTACT_NUMBERS);
		sqlitedatabase.execSQL(DatabaseConstants.TABLE_ALL_CONTACT_NUMBERS);
		sqlitedatabase.execSQL(DatabaseConstants.TABLE_CONTACT_EMAILS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		switch(oldVersion) {
//			case 1:
////				db.execSQL(DATABASE_CREATE_color);
//				// we want both updates, so no break statement here...
//			case 2:
//				db.execSQL(DatabaseConstants.TABLE_MESSAGE_INFO);
//		}
		// If you need to add a column
		if (newVersion > oldVersion) {
//			db.execSQL("ALTER TABLE foo ADD COLUMN new_column INTEGER DEFAULT 0");
			db.execSQL("ALTER TABLE " + DatabaseConstants.TABLE_MESSAGE_INFO + " ADD COLUMN " + ChatDBConstants.USER_ID + " long;");
			db.execSQL("ALTER TABLE " + DatabaseConstants.TABLE_MESSAGE_INFO + " ADD COLUMN " + ChatDBConstants.USER_SG + " TEXT;");
		}
	}
}
