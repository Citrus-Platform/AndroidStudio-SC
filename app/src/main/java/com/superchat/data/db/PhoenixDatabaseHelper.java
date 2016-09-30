package com.superchat.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhoenixDatabaseHelper extends SQLiteOpenHelper {

	public PhoenixDatabaseHelper(Context context) {
		super(context, "superchat.db", null, 2);
	}

	public void onCreate(SQLiteDatabase sqlitedatabase) {

//		sqlitedatabase.execSQL(DatabaseConstants.TABLE_MESSAGE_INFO);
		sqlitedatabase.execSQL(DatabaseConstants.TABLE_CONTACT_NAMES);
		sqlitedatabase.execSQL(DatabaseConstants.TABLE_CONTACT_NUMBERS);
		sqlitedatabase.execSQL(DatabaseConstants.TABLE_ALL_CONTACT_NUMBERS);
		sqlitedatabase.execSQL(DatabaseConstants.TABLE_CONTACT_EMAILS);
	}

	public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
	}
}
