package com.chat.sdk.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChatDatabaseHelper extends SQLiteOpenHelper {

	public ChatDatabaseHelper(Context context) {
		super(context, "subersdkdb.db", null, 1);
	}

	public void onCreate(SQLiteDatabase sqlitedatabase) {

		sqlitedatabase.execSQL(ChatDBConstants.TABLE_MESSAGE_INFO);
		sqlitedatabase.execSQL(ChatDBConstants.TABLE_STATUS_INFO);
	}

	public void onUpgrade(SQLiteDatabase sqlitedatabase, int i, int j) {
	}
}
