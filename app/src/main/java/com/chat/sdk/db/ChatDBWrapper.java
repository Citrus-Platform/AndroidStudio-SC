package com.chat.sdk.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.chatsdk.org.jivesoftware.smack.packet.Message.SeenState;
import com.chatsdk.org.jivesoftware.smack.packet.Message.XMPPMessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.superchat.data.db.DatabaseConstants;
import com.superchat.model.MessageDataModel;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;
import com.superchat.utils.Utilities;
import com.superchat.utils.ZipManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatDBWrapper {
	public static final String TAG = "ChatDBWrapper";
	public static long COPY_THRESHOLD = 60000L;
	public static int TRANSACTION_THRESHOLD = 100;
	private static ChatDBWrapper dbWrapper = null;
	private ChatDatabaseHelper dbHelper;
	static Context context;
	private ChatDBWrapper() {
		dbHelper = null;
		dbHelper = new ChatDatabaseHelper(context);
	}

	public static ChatDBWrapper getInstance() {
		 synchronized(ChatDBWrapper.class){
		if (dbWrapper == null) {
			dbWrapper = new ChatDBWrapper();
		}
		return dbWrapper;
		 }
	}

	public static ChatDBWrapper getInstance(Context context) {
		if (ChatDBWrapper.context == null) {
			ChatDBWrapper.context = context;
		}
		 synchronized(ChatDBWrapper.class){
			if (dbWrapper == null) {
				dbWrapper = new ChatDBWrapper();
			}
			return dbWrapper;
		}
	}
	
	public void alterTable(String table_name, String[] column_name){
		try{
			//Check if this new column exists, then add new one
			SQLiteDatabase database = dbHelper.getWritableDatabase();
	    	 Cursor cursor = database.rawQuery("SELECT * FROM "+table_name, null); // grab cursor for all data
	    	 for(int i = 0; i < column_name.length; i++){
		    	 int index = cursor.getColumnIndex(column_name[i]);  // see if the column is there
		    	 if (index < 0) { 
		    	     // missing_column not there - add it
		    		 if(column_name.length == 1)
		    			 database.execSQL("ALTER TABLE "+table_name+" ADD COLUMN "+column_name[i]+" integer default 0 NOT NULL;");
		    		 else
		    			 database.execSQL("ALTER TABLE "+table_name+" ADD COLUMN "+column_name[i]+" TEXT;");
		    	 }
	    	 }
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}

	public void beginTransaction() {
		dbHelper.getWritableDatabase().beginTransaction();
	}
	public void setTransactionSuccessful() {
		dbHelper.getWritableDatabase().setTransactionSuccessful();
	}
	public void clearAllDB() {
//		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
//		beginTransaction();
//		int i = sqlitedatabase.delete(
//				ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS, null, null);
//		Log.i(TAG, (new StringBuilder()).append("Deleted from Data::")
//				.append(i).toString());
//		int j = sqlitedatabase.delete(
//				ChatDBConstants.TABLE_NAME_CONTACT_NAMES, null, null);
//		Log.i(TAG,
//				(new StringBuilder()).append("Deleted from Names::").append(j)
//				.toString());
//		int k = sqlitedatabase.delete(
//				ChatDBConstants.TABLE_NAME_CONTACT_EMAILS, null, null);
//		Log.i(TAG,
//				(new StringBuilder()).append("Deleted from Names::").append(k)
//				.toString());
//		setTransaction();
//		endTransaction();
		
		
	}
	public boolean isDuplicateMessage(String person,String forigenId) {
		boolean ret = false;
		String sql = "SELECT * FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
				+ ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD + " = '" + forigenId + "' AND "
				+ ChatDBConstants.FROM_USER_FIELD + " = '" + person + "'";
		Cursor cursor = null;
		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null && cursor.getCount() > 0)
				ret = true;
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in getRecievedMessages method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return ret;
	}
	public void clearMessageDB() {
		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
		int i = sqlitedatabase.delete(
				ChatDBConstants.TABLE_NAME_MESSAGE_INFO, null, null);
		Log.i(TAG, (new StringBuilder()).append("Deleted from Data::")
				.append(i).toString());
	}

	public long deleteInDB(String table, ArrayList arraylist, String s1) {
		Exception exception;
		int k = 0;
		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
		long l = 0L;
		int i = arraylist.size();
		sqlitedatabase.beginTransaction();
		int j = 0;
		String as[];
		ContentValues contentvalues;
		long l1;
		try {
			as = new String[1];
			while (true) {
				if (k >= i) {
					break;
				}
				contentvalues = (ContentValues) arraylist.get(k);
				l1 = contentvalues.getAsLong(s1).longValue();
				as[0] = (new StringBuilder()).append(l1).append("").toString();
				contentvalues.remove(s1);
				l = sqlitedatabase.delete(table,
						(new StringBuilder()).append(s1).append("=?")
						.toString(), as);
				j++;
				if (j < TRANSACTION_THRESHOLD) {
					break;
				}
				sqlitedatabase.setTransactionSuccessful();
				sqlitedatabase.endTransaction();
				sqlitedatabase.beginTransaction();
				k++;
			}
		} catch (Exception exception1) {
			sqlitedatabase.endTransaction();
			return l;
		} finally {
			sqlitedatabase.endTransaction();
		}

		return l;
	}

	public int deleteRows(String s, String s1, String as[]) {
		return dbHelper.getWritableDatabase().delete(s, s1, as);
	}

	public void endTransaction() {
		dbHelper.getWritableDatabase().endTransaction();
	}

	public Cursor executeRawQuery(String s) {
		return dbHelper.getWritableDatabase().rawQuery(s, null);
	}

	public List<String> getAllNumbers() {
		List<String> list = new ArrayList<String>();
//		Cursor cursor = dbHelper.getWritableDatabase().query(true,
//				ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS,
//				new String[] { ChatDBConstants.CONTACT_NUMBERS_FIELD }, null,
//				null, null, null, null, null);
//		try {
//			if (cursor != null && cursor.getCount() > 0) {
//				while (cursor.moveToNext()) {
//					Log.d("ChatDBWrapper",
//							"DbNumbers: "
//									+ cursor.getString(cursor
//											.getColumnIndex(ChatDBConstants.CONTACT_NUMBERS_FIELD)));
//					list.add(cursor.getString(cursor
//							.getColumnIndex(ChatDBConstants.CONTACT_NUMBERS_FIELD)));
//				}
//			}
//		} catch (Exception e) {
//			Log.d("ChatDBWrapper",
//					"Exception in getAllNumbers method " + e.toString());
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}

		return list;
	}

	public List<String> getAllEmails() {
		List<String> list = new ArrayList<String>();
//		Cursor cursor = dbHelper.getWritableDatabase().query(true,
//				ChatDBConstants.TABLE_NAME_CONTACT_EMAILS,
//				new String[] { ChatDBConstants.PHONE_EMAILS_FIELD }, null,
//				null, null, null, null, null);
//		try {
//			if (cursor != null && cursor.getCount() > 0) {
//				while (cursor.moveToNext()) {
//					Log.d("ChatDBWrapper",
//							"DbEmails: "
//									+ cursor.getString(cursor
//											.getColumnIndex(ChatDBConstants.PHONE_EMAILS_FIELD)));
//					list.add(cursor.getString(cursor
//							.getColumnIndex(ChatDBConstants.PHONE_EMAILS_FIELD)));
//				}
//			}
//		} catch (Exception e) {
//			Log.d("ChatDBWrapper",
//					"Exception in getAllEmails method " + e.toString());
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}

		return list;
	}

	public String getContactName(String userName) {
		String contactPerson = userName;
//		Cursor cursor = ChatDBWrapper.getInstance().query(
//				ChatDBConstants.TABLE_NAME_CONTACT_NAMES,
//				new String[] { ChatDBConstants.CONTACT_NAMES_FIELD },
//				ChatDBConstants.USER_NAME_FIELD + "='" + userName + "'",
//				null, null);
//		if (cursor != null) {
//			while (cursor.moveToNext())
//				contactPerson = cursor.getString(cursor
//						.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));// Log.d(TAG,
//			// TAG+"::"+cursor.getString(cursor.getColumnIndex("name"))+" + "+cursor.getString(cursor.getColumnIndex("ChatDBConstants.NAME_CONTACT_ID_FIELD")));
//		}
//		if (cursor != null)
//			cursor.close();
		return contactPerson;
	}

	public String getContactNumber(String userName) {
		String contactNumber = "";
//		Cursor cursor = ChatDBWrapper.getInstance().query(
//				ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS,
//				new String[] { ChatDBConstants.CONTACT_NUMBERS_FIELD },
//				ChatDBConstants.USER_NAME_FIELD + "='" + userName + "'",
//				null, null);
//		if (cursor != null) {
//			Log.d(TAG, "Total contact numbers with respect of " + userName
//					+ ": " + cursor.getCount());
//			if(cursor.moveToNext())
//				contactNumber = cursor
//				.getString(cursor
//						.getColumnIndex(ChatDBConstants.CONTACT_NUMBERS_FIELD));
//		}
//		if (cursor != null)
//			cursor.close();
		return contactNumber;
	}
	
	public String getContactID(String userName) {
		String contactId = "";
//		Cursor cursor = ChatDBWrapper.getInstance().query(
//				ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS,
//				new String[] { ChatDBConstants.NAME_CONTACT_ID_FIELD },
//				ChatDBConstants.USER_NAME_FIELD + "='" + userName + "'",
//				null, null);
//		try{
//			if (cursor != null) {
//				Log.d(TAG, "getContactID numbers with respect of " + userName + ": " + cursor.getCount());
//				if(cursor.moveToNext())
//					contactId = cursor
//					.getString(cursor
//							.getColumnIndex(ChatDBConstants.NAME_CONTACT_ID_FIELD));
//			}
//		}catch(Exception e){}
//		if (cursor != null)
//			cursor.close();
//		try{
//			if(contactId!=null && !contactId.equals("")){
//				cursor = ChatDBWrapper.getInstance().query(
//						ChatDBConstants.NAME_CONTACT_ID_FIELD,
//						new String[] { ChatDBConstants.USER_NAME_FIELD },
//						ChatDBConstants.NAME_CONTACT_ID_FIELD + "='" + contactId + "'",
//						null, null);
//				contactId = "";
//				if (cursor != null) {
//					Log.d(TAG, "getContactID numbers with respect of " + contactId + ": " + cursor.getCount());
//					
//					if(cursor.moveToNext())
//						contactId = cursor
//						.getString(cursor
//								.getColumnIndex(ChatDBConstants.NAME_CONTACT_ID_FIELD));
//				}
//			}
//		}catch(Exception e){}
//		if (cursor != null)
//			cursor.close();
		return contactId;
	}
	public String getContactEmail(String userName) {
		String contactNumber = "";
		Cursor cursor = null;
//		Cursor cursor = ChatDBWrapper.getInstance().query(
//				ChatDBConstants.TABLE_NAME_CONTACT_EMAILS,
//				new String[] { ChatDBConstants.PHONE_EMAILS_FIELD },
//				ChatDBConstants.USER_NAME_FIELD + "='" + userName + "'",
//				null, null);
		if (cursor != null) {
			Log.d(TAG, "Total contact numbers with respect of " + userName
					+ ": " + cursor.getCount());
			if(cursor.moveToNext())
				contactNumber = cursor
				.getString(cursor
						.getColumnIndex(ChatDBConstants.PHONE_EMAILS_FIELD));
		}
		if (cursor != null)
			cursor.close();
		return contactNumber;
	}
public void saveNewNumber(String userName,String contactName, String mobileNumber){
	try{
		if(isNumberExists(mobileNumber)){
			return;
		}
	if(mobileNumber==null)
		mobileNumber = "";
	ContentValues contentvalues1 = new ContentValues();
	contentvalues1.put(ChatDBConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf((int)System.currentTimeMillis()));
	contentvalues1.put(ChatDBConstants.CONTACT_NUMBERS_FIELD,mobileNumber);
	contentvalues1.put(ChatDBConstants.CONTACT_COMPOSITE_FIELD, mobileNumber);
	contentvalues1.put(ChatDBConstants.CONTACT_NAMES_FIELD, contactName);
	contentvalues1.put(ChatDBConstants.DATA_ID_FIELD,Integer.valueOf(0));
	contentvalues1.put(ChatDBConstants.VOPIUM_FIELD,Integer.valueOf(1));
	contentvalues1.put(ChatDBConstants.USER_NAME_FIELD, userName);
	contentvalues1.put(ChatDBConstants.STATE_FIELD,Integer.valueOf(0));
//	insertInDB(ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues1);
	}catch(Exception e){}
}
	public String getChatName(String userName) {
		String contactPerson = userName;
//		String sql1 = "SELECT "+ChatDBConstants.CONTACT_NAMES_FIELD+" FROM "+ ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS+" WHERE "+ChatDBConstants.USER_NAME_FIELD + "='" + userName + "' UNION"+
//				" SELECT "+ChatDBConstants.CONTACT_NAMES_FIELD+" FROM "+ ChatDBConstants.TABLE_NAME_CONTACT_EMAILS+" WHERE "+ChatDBConstants.USER_NAME_FIELD + "='" + userName + "'";
//		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql1, null);
//		//		Cursor cursor = ChatDBWrapper.getInstance().query(
////				ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS,
////				new String[] { ChatDBConstants.CONTACT_NAMES_FIELD },
////				ChatDBConstants.USER_NAME_FIELD + "='" + userName + "'",
////				null, null);
//		try {
//			if (cursor != null) {
//
//				while (cursor.moveToNext()){
//					contactPerson = cursor
//					.getString(cursor
//							.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD))+"#786#"+userName;// Log.d(TAG,
//				// TAG+"::"+cursor.getString(cursor.getColumnIndex("name"))+" + "+cursor.getString(cursor.getColumnIndex("ChatDBConstants.NAME_CONTACT_ID_FIELD")));
//					break;
//				}
//			}
//		} catch (Exception e) {
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
//		if (userName.equals(contactPerson)) {
//			String sql = "SELECT "+ChatDBConstants.CONTACT_NAMES_FIELD+" FROM "+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO
//					+ " WHERE ("
//					+ ChatDBConstants.CONTACT_NAMES_FIELD + "!='" + userName
//					+ "' AND ("+ChatDBConstants.FROM_USER_FIELD + "='" + userName+ "' OR "+ChatDBConstants.TO_USER_FIELD + "='" + userName+"'))";
//			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//			try {
//				if (cursor != null) {
//
//					if(cursor.moveToLast()){
//						contactPerson = cursor
//						.getString(cursor
//								.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD))+"#786#"+userName;// Log.d(TAG,
//					// TAG+"::"+cursor.getString(cursor.getColumnIndex("name"))+" + "+cursor.getString(cursor.getColumnIndex("ChatDBConstants.NAME_CONTACT_ID_FIELD")));
//						}
//				}
//			} catch (Exception e) {
//			} finally {
//				if (cursor != null) {
//					cursor.close();
//					cursor = null;
//				}
//			}
//		}
//		if (!userName.equals(contactPerson)) {
//			updateContactName(contactPerson, userName);
//		}
		return contactPerson;
	}

	public void updateContactName(String contactName, String userName) {
		Cursor cursor = null;
		try {
//			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
//					+ " SET " + ChatDBConstants.CONTACT_NAMES_FIELD + "='"
//					+ contactName + "' WHERE "
//					+ ChatDBConstants.CONTACT_NAMES_FIELD + "='" + userName
//					+ "'";
//			if(SharedPrefManager.getInstance().isGroupChat(userName))
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
			+ " SET " + ChatDBConstants.CONTACT_NAMES_FIELD + "='"
			+ contactName + "' WHERE "
			+ ChatDBConstants.CONTACT_NAMES_FIELD + " like '%" + userName
			+ "%'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateUserNameInContacts count " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper", "Exception in updateUserNameInContacts method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateMediaLength(String key , String length) {
		Cursor cursor = null;
		try {
//			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
//					+ " SET " + ChatDBConstants.CONTACT_NAMES_FIELD + "='"
//					+ contactName + "' WHERE "
//					+ ChatDBConstants.CONTACT_NAMES_FIELD + "='" + userName
//					+ "'";
//			if(SharedPrefManager.getInstance().isGroupChat(userName))
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
			+ " SET " + ChatDBConstants.MESSAGE_MEDIA_LENGTH + "='"
			+ length + "' WHERE "
			+ ChatDBConstants.MESSAGE_ID + " = '" + key
			+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateUserNameInContacts count " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper", "Exception in updateUserNameInContacts method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateUserDisplayName(String contactName, String userName) {
		
//		String temp = getDisplayName(userName);
         ContentValues chat_values = new ContentValues();
//         Name#786#user_name
//         Mahesh#786#maheshsonkar_mydomain
         String name_to_store = contactName + "#786#" + userName;
         chat_values.put(ChatDBConstants.CONTACT_NAMES_FIELD, name_to_store);
          int row = dbHelper.getWritableDatabase().update(ChatDBConstants.TABLE_NAME_MESSAGE_INFO, chat_values, ChatDBConstants.FROM_USER_FIELD + " = ?",
                 new String[] { userName });
          if(row > 0)
         	 Log.d("DBWrapper", "updateUserNameInContacts count " + row);
         
	}
	
	public String getDisplayName(String userName) {
		String contactPerson = "User-Name";
		Cursor cursor = ChatDBWrapper.getInstance().query(
				ChatDBConstants.TABLE_NAME_MESSAGE_INFO,
				new String[] { DatabaseConstants.CONTACT_NAMES_FIELD },
				DatabaseConstants.FROM_USER_FIELD + "='" + userName+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				contactPerson = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));
		}
		if (cursor != null)
			cursor.close();
		return contactPerson;
	}

	public void updateMessageMediaURL(String messageID, String messageDataPath) {
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.MESSAGE_MEDIA_URL_FIELD + "='"+ messageDataPath +"' , "+ ChatDBConstants.MEDIA_STATUS + "='"+ ChatDBConstants.MEDIA_LOADED + "' WHERE "
					+ ChatDBConstants.MESSAGE_ID + "='" + messageID
					+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateMessageData count " + cursor.getCount());
			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper", "Exception in updateMessageData method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateMediaLoadingStarted(String messageID) {
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.MEDIA_STATUS + "='"+ ChatDBConstants.MEDIA_LOADING + "' WHERE "
					+ ChatDBConstants.MESSAGE_ID + "='" + messageID
					+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateMessageData count " + cursor.getCount());
			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper", "Exception in updateMessageData method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateMessageMediaLocalPath(String messageID, String messageDataPath) {
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD + "='"+ messageDataPath +"' , "+ ChatDBConstants.MEDIA_STATUS + "='"+ ChatDBConstants.MEDIA_READY_TO_LOAD + "' WHERE "
					+ ChatDBConstants.MESSAGE_ID + "='" + messageID
					+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateMessageData count " + cursor.getCount());
			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper", "Exception in updateMessageData method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateFileDocSize(String fileUrl, String fileSize) {
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.MESSAGE_MEDIA_LENGTH + "='"+ fileSize+ "' WHERE "
					+ ChatDBConstants.MESSAGE_MEDIA_URL_FIELD + "='" + fileUrl+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateFileDocSize count " + cursor.getCount());
			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper", "Exception in updateFileDocSize method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	
	public String getUserName(long userId) {
		String contactPerson = "User-Name";
//		Cursor cursor = ChatDBWrapper.getInstance().query(
//				ChatDBConstants.TABLE_NAME_CONTACT_NAMES,
//				new String[] { ChatDBConstants.USER_NAME_FIELD },
//				ChatDBConstants.NAME_CONTACT_ID_FIELD + "=" + userId, null,
//				null);
//		if (cursor != null) {
//			while (cursor.moveToNext())
//				contactPerson = cursor.getString(cursor
//						.getColumnIndex(ChatDBConstants.USER_NAME_FIELD));
//		}
//		if (cursor != null)
//			cursor.close();
		return contactPerson;
	}

	public int getContactIDFromData(String s, String as[]) {
		int i = -1;
//		Cursor cursor = dbHelper.getWritableDatabase().query(
//				ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS,
//				new String[] { ChatDBConstants.NAME_CONTACT_ID_FIELD }, s,
//				as, null, null, null);
//		if (cursor != null && cursor.moveToFirst()) {
//			i = cursor.getInt(cursor
//					.getColumnIndex(ChatDBConstants.NAME_CONTACT_ID_FIELD));
//		}
//		cursor.close();
		return i;
	}

	public int getContactIDFromEmailData(String s, String as[]) {
		int i = -1;
//		Cursor cursor = dbHelper.getWritableDatabase().query(
//				ChatDBConstants.TABLE_NAME_CONTACT_EMAILS,
//				new String[] { ChatDBConstants.NAME_CONTACT_ID_FIELD }, s,
//				as, null, null, null);
//		if (cursor != null && cursor.moveToFirst()) {
//			i = cursor.getInt(cursor
//					.getColumnIndex(ChatDBConstants.NAME_CONTACT_ID_FIELD));
//		}
//		cursor.close();
		return i;
	}

	public void updateAtMeContactStatus(String contactNumber) {
//		ChatDBWrapper dbwrapper = ChatDBWrapper.getInstance();
//		dbwrapper.beginTransaction();
//		ContentValues contentvalues = new ContentValues();
//		dbwrapper.updateInDB(ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS,
//				contentvalues,
//				ChatDBConstants.CONTACT_NUMBERS_FIELD + " = ?",
//				new String[] { contactNumber });
//		dbwrapper.setTransaction();
//		dbwrapper.endTransaction();
	}

//	public Cursor getRecentChatList() {
//		String sql = "SELECT " + "DISTINCT("
//				+ ChatDBConstants.CONTACT_NAMES_FIELD + "), "
//				+ ChatDBConstants._ID + ", "
//				+ ChatDBConstants.FROM_USER_FIELD + ", "
//				+ ChatDBConstants.TO_USER_FIELD + ", "
//				+ ChatDBConstants.MESSAGEINFO_FIELD + ","
//				+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
//				+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
//				+ ChatDBConstants.LAST_UPDATE_FIELD + ", MAX("
//				+ ChatDBConstants.LAST_UPDATE_FIELD + ") FROM "
//				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " GROUP BY "
//				+ ChatDBConstants.CONTACT_NAMES_FIELD + " ORDER BY "
//				+ ChatDBConstants.LAST_UPDATE_FIELD + " DESC";
//		Log.d("ChatDBWrapper", "getRecentChatList query: " + sql);
//		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//		return cursor;
//	}
	public Cursor getGroupOrBroadCastUsersStatus(String messageId){
		String sql = "SELECT*FROM "
				+ ChatDBConstants.TABLE_NAME_STATUS_INFO + " WHERE " + ChatDBConstants.MESSAGE_ID + " = '" + messageId +"'";
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				Log.d("ChatDBWrapper",messageId+" getGroupOrBroadCastUsersStatus count: " + cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID)));
			} while (cursor.moveToNext());
		}
		return cursor;
	}
		
	public Cursor getRecentChatList(String searchKey) {
		String sql = "";
		if(searchKey==null || searchKey.equals(""))
		sql = "SELECT " + "DISTINCT("
				+ ChatDBConstants.CONTACT_NAMES_FIELD + "), "
				+ ChatDBConstants._ID + ", "
				+ ChatDBConstants.FROM_USER_FIELD + ", "
				+ ChatDBConstants.TO_USER_FIELD + ", "
				+ ChatDBConstants.MESSAGEINFO_FIELD + ","
				+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
				+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
				+ ChatDBConstants.MEDIA_CAPTION_TAG + ","
				+ ChatDBConstants.MESSAGE_MEDIA_LENGTH + ","
				+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
				+ ChatDBConstants.LAST_UPDATE_FIELD + ", MAX("
				+ ChatDBConstants.LAST_UPDATE_FIELD + ") FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO +" WHERE "+ChatDBConstants.MESSAGE_TYPE + "!=" + 3 + " GROUP BY "
				+ ChatDBConstants.CONTACT_NAMES_FIELD + " ORDER BY "
				+ ChatDBConstants.LAST_UPDATE_FIELD + " DESC";
		else
			sql = "SELECT " + "DISTINCT("
					+ ChatDBConstants.CONTACT_NAMES_FIELD + "), "
					+ ChatDBConstants._ID + ", "
					+ ChatDBConstants.FROM_USER_FIELD + ", "
					+ ChatDBConstants.TO_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGEINFO_FIELD + ","
					+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
					+ ChatDBConstants.MEDIA_CAPTION_TAG + ","
					+ ChatDBConstants.MESSAGE_MEDIA_LENGTH + ","
					+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
					+ ChatDBConstants.LAST_UPDATE_FIELD + ", MAX("
					+ ChatDBConstants.LAST_UPDATE_FIELD + ") FROM "
					+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+" WHERE "+ChatDBConstants.CONTACT_NAMES_FIELD + " like '"+searchKey
					+"' GROUP BY "
					+ ChatDBConstants.CONTACT_NAMES_FIELD + " ORDER BY "
					+ ChatDBConstants.LAST_UPDATE_FIELD + " DESC";
		Log.d("ChatDBWrapper", "getRecentChatList query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		return cursor;
	}
	public Cursor getBulletinList(byte type) {
		String sql = "";
		String bulletin_name = SharedPrefManager.getInstance().getUserDomain() + "-all";
		if(type == 1){
			sql = "SELECT " + "DISTINCT("
					+ ChatDBConstants.CONTACT_NAMES_FIELD + "), "
					+ ChatDBConstants._ID + ", "
					+ ChatDBConstants.FROM_USER_FIELD + ", "
					+ ChatDBConstants.TO_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGEINFO_FIELD + ","
					+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
					+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
					+ ChatDBConstants.MEDIA_CAPTION_TAG + ","
					+ ChatDBConstants.MESSAGE_MEDIA_LENGTH + ","
					+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
					+ ChatDBConstants.LAST_UPDATE_FIELD + ", MAX("
					+ ChatDBConstants.LAST_UPDATE_FIELD + ") FROM "
					+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO +" WHERE "+ChatDBConstants.MESSAGE_TYPE + "=" + 3 + " GROUP BY "
					+ ChatDBConstants.MESSAGE_TYPE + " ORDER BY "
					+ ChatDBConstants.LAST_UPDATE_FIELD + " DESC";//
		}
		else
			sql = "SELECT " + "DISTINCT("
					+ ChatDBConstants.CONTACT_NAMES_FIELD + "), "
					+ ChatDBConstants._ID + ", "
					+ ChatDBConstants.FROM_USER_FIELD + ", "
					+ ChatDBConstants.TO_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGEINFO_FIELD + ","
					+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
					+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
					+ ChatDBConstants.MEDIA_CAPTION_TAG + ","
					+ ChatDBConstants.MESSAGE_MEDIA_LENGTH + ","
					+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
					+ ChatDBConstants.LAST_UPDATE_FIELD + ", MAX("
					+ ChatDBConstants.LAST_UPDATE_FIELD + ") FROM "
					+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO +" WHERE "+ChatDBConstants.MESSAGE_TYPE + "=" + 3 + " AND "
					+ ChatDBConstants.TO_USER_FIELD + " != '"+ bulletin_name+ "' GROUP BY "
					+ ChatDBConstants.CONTACT_NAMES_FIELD + " ORDER BY "
					+ ChatDBConstants.LAST_UPDATE_FIELD + " DESC";
		
		Log.d("ChatDBWrapper", "getBulletinList query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		return cursor;
	}
	
	public Cursor getBulletinList() {
		String sql = "";
		String bulletin_name = SharedPrefManager.getInstance().getUserDomain() + "-all";
		sql = "SELECT " + "DISTINCT("
				+ ChatDBConstants.CONTACT_NAMES_FIELD + "), "
				+ ChatDBConstants._ID + ", "
				+ ChatDBConstants.FROM_USER_FIELD + ", "
				+ ChatDBConstants.TO_USER_FIELD + ", "
				+ ChatDBConstants.MESSAGEINFO_FIELD + ","
				+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
				+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
				+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
				+ ChatDBConstants.MEDIA_CAPTION_TAG + ","
				+ ChatDBConstants.MESSAGE_MEDIA_LENGTH + ","
				+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
				+ ChatDBConstants.LAST_UPDATE_FIELD + ", MAX("
				+ ChatDBConstants.LAST_UPDATE_FIELD + ") FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO +" WHERE "+ChatDBConstants.MESSAGE_TYPE + "=" + 3 + " AND "
				+ ChatDBConstants.TO_USER_FIELD + " == '"+ bulletin_name+ "' GROUP BY "
				+ ChatDBConstants.CONTACT_NAMES_FIELD + " ORDER BY "
				+ ChatDBConstants.LAST_UPDATE_FIELD + " DESC";
		
		Log.d("ChatDBWrapper", "getBulletinList query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		return cursor;
	}
	
public boolean isBroadCastMessage(String idArrays){
	String sql = "SELECT "+ChatDBConstants.TO_USER_FIELD +" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE " + ChatDBConstants.MESSAGE_ID + " IN " + idArrays;
	Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
	boolean isBroadCast = false;
	try{
		if (cursor != null && cursor.moveToFirst()) {
			//do {
				String tmpUserName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.TO_USER_FIELD));
				if(tmpUserName!=null && !tmpUserName.equals("")){
					if(SharedPrefManager.getInstance().isBroadCast(tmpUserName)){
						isBroadCast = true;
					}
				}
			// } while (cursor.moveToNext());
		}
	}catch(Exception e){}
		finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
	}
		return isBroadCast;
	}
	public void deleteRecentUserChat(String userName) {
		dbHelper.getWritableDatabase().delete(
				ChatDBConstants.TABLE_NAME_MESSAGE_INFO,
				ChatDBConstants.CONTACT_NAMES_FIELD + "='" + userName + "'",
				null);
	}
	public void deleteRecentUserChatByUserName(String userName) {

		dbHelper.getWritableDatabase().delete(
				ChatDBConstants.TABLE_NAME_MESSAGE_INFO,
				ChatDBConstants.FROM_USER_FIELD + "='" + userName + "' OR "+ChatDBConstants.TO_USER_FIELD + "='" + userName + "'",
				null);
	}

	public boolean deleteSelectedChatIteams(List<String> msgArray) {
		boolean isDeleted = false;
		Cursor cursor = null;
		// dbHelper.getWritableDatabase().delete(ChatDBConstants.TABLE_NAME_MESSAGE_INFO,
		// ChatDBConstants.MESSAGE_ID + "='" + tagId+"'", null);
		try {
			String tags = convertStringArrayToString(msgArray);

			String sql = "DELETE FROM "
					+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
					+ ChatDBConstants.MESSAGE_ID + " IN " + tags;
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst()) {
				isDeleted = true;
			}
		} catch (Exception e) {
			Log.d("ChatDBWrapper", "Exception in deleteSelectedChatIteams method "
					+ e.toString());
			isDeleted = false;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return isDeleted;
	}

	public void updateUserNameInContacts(String userName, String contactNumber) {
//		Log.d("ChatDBWrapper", "updateUserNameInContacts " + userName + " "
//				+ contactNumber);
//		Cursor cursor = null;
//		try {
//			String sql = "UPDATE "
//					+ ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS + " SET "
//					+ ChatDBConstants.USER_NAME_FIELD + "='" + userName
//					+ "' WHERE " + ChatDBConstants.CONTACT_NUMBERS_FIELD
//					+ "='" + contactNumber + "'";
//			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//			if (cursor != null) {
//				Log.d("ChatDBWrapper",
//						"updateUserNameInContacts count " + cursor.getCount());
//
//			}
//		} catch (Exception e) {
//			Log.e("ChatDBWrapper", "Exception in updateUserNameInContacts method "
//					+ e.toString());
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
	}
	public void updateCompositeContacts(String fieldId, String compositeNumbers) {
//		Log.d("ChatDBWrapper", "updateCompositeContacts " + fieldId + " "
//				+ compositeNumbers);
//		Cursor cursor = null;
//		try {
//			String sql = "UPDATE "
//					+ ChatDBConstants.TABLE_NAME_CONTACT_NAMES + " SET "
//					+ ChatDBConstants.CONTACT_COMPOSITE_FIELD + "='" + compositeNumbers
//					+ "' WHERE " + ChatDBConstants.NAME_CONTACT_ID_FIELD
//					+ "='" + fieldId + "'";
//			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//			if (cursor != null) {
//				Log.d("ChatDBWrapper",
//						"updateUserNameInContacts count " + cursor.getCount());
//
//			}
//		} catch (Exception e) {
//			Log.e("ChatDBWrapper", "Exception in updateUserNameInContacts method "
//					+ e.toString());
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
	}
	public void updateP2PReadTime(String idArrays, long time) {
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.READ_TIME_FIELD + "='"
					+ time + "' WHERE "+ ChatDBConstants.MESSAGE_ID + " IN " + idArrays;
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateP2PReadTime row counts " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper", "Exception in updateP2PReadTime method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateSeenStatus(String userName, String idArrays,
			Message.SeenState state) {
		Log.d("ChatDBWrapper", "updateSeenStatus idArrays " + idArrays);
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.SEEN_FIELD + "='"
					+ state.ordinal() + "' WHERE "
					+ ChatDBConstants.MESSAGE_ID + " IN " + idArrays
					+ " AND " + ChatDBConstants.SEEN_FIELD + " != '"
					+ Message.SeenState.seen + "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateSeenStatus idArrays " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateGroupOrBroadCastSeenStatus(String userName, String idArrays,
			Message.SeenState state, long statusTime) {
		Log.d("ChatDBWrapper", "updateGroupOrBroadCastSeenStatus idArrays " + idArrays);
		Cursor cursor = null;
		try {
			String sql = null;
			if(state == Message.SeenState.recieved){
				sql = "UPDATE " + ChatDBConstants.TABLE_NAME_STATUS_INFO
				+ " SET " + ChatDBConstants.SEEN_FIELD + "='"+ state.ordinal()+"'," 
				+ ChatDBConstants.DELIVER_TIME_FIELD + "='"+ statusTime + "' WHERE "
				+ ChatDBConstants.MESSAGE_ID + " IN " + idArrays
				+ " AND " + ChatDBConstants.SEEN_FIELD + " != '"+ Message.SeenState.seen + "'"
				+ " AND " + ChatDBConstants.FROM_USER_FIELD + " != '"+ userName + "'";
			}else
				sql = "UPDATE " + ChatDBConstants.TABLE_NAME_STATUS_INFO
				+ " SET " + ChatDBConstants.SEEN_FIELD + "='"+ state.ordinal() +"',"
				+ ChatDBConstants.SEEN_TIME_FIELD + "='"+ statusTime + "' WHERE "
				+ ChatDBConstants.MESSAGE_ID + " IN " + idArrays
				+ " AND " + ChatDBConstants.SEEN_FIELD + " != '"+ Message.SeenState.seen + "'"
				+ " AND " + ChatDBConstants.FROM_USER_FIELD + " != '"+ userName + "'";
			
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateSeenStatus idArrays " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateFrndsSeenStatus(String userName, String idArrays,
			Message.SeenState state) {
		Log.d("ChatDBWrapper", "updateSeenStatus idArrays " + idArrays);
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.SEEN_FIELD + "='"
					+ state.ordinal() + "' WHERE "
					+ ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD + " IN "
					+ idArrays + " AND " + ChatDBConstants.SEEN_FIELD + "!='"
					+ Message.SeenState.seen.ordinal() + "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateSeenStatus idArrays " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateUserReadCount(String messageId, int readCount) {
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.READ_USER_COUNT_FIELD + "='"
					+ readCount + "' WHERE "
					+ ChatDBConstants.MESSAGE_ID + "='"+messageId+"'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateSeenStatus idArrays " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateTotalUserCount(String messageId, int totalCount) {
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.TOTAL_USER_COUNT_FIELD + "='"
					+ totalCount + "' WHERE "
					+ ChatDBConstants.MESSAGE_ID + "='"+messageId+"'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateSeenStatus idArrays " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateTotalUserCountByGroupName(String groupUUID, int totalCount) {
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + ChatDBConstants.TOTAL_USER_COUNT_FIELD + "='"
					+ totalCount + "' WHERE "
					+ ChatDBConstants.FROM_USER_FIELD + "='"+groupUUID+"'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("ChatDBWrapper",
						"updateSeenStatus idArrays " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public int getTotalGroupUsersCount(String messageId){
		String sql = "SELECT "+ChatDBConstants.TOTAL_USER_COUNT_FIELD +" FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE " + ChatDBConstants.MESSAGE_ID + "='"+messageId+"'";
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		int tmpUserName = 0;
		try{
			if (cursor != null && cursor.moveToFirst()) {
					 tmpUserName = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.TOTAL_USER_COUNT_FIELD));
			}
		}catch(Exception e){}
			finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
		}
			return tmpUserName;
	}
	public int getTotalMessageReadCount(String messageId){
		String sql = "SELECT "+ChatDBConstants.READ_USER_COUNT_FIELD +" FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE " + ChatDBConstants.MESSAGE_ID + "='"+messageId+"'";
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		int tmpUserName = 0;
		try{
			if (cursor != null && cursor.moveToFirst()) {
					 tmpUserName = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.READ_USER_COUNT_FIELD));
			}
		}catch(Exception e){}
			finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
		}
			return tmpUserName;
	}
	public String getUsersDisplayName(String userName){
		String sql = "SELECT "+ChatDBConstants.CONTACT_NAMES_FIELD +" FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE " + ChatDBConstants.FROM_USER_FIELD + "='"+userName+"'";
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		String tmpUserName = userName;
		try{
			if (cursor != null && cursor.moveToFirst()) {
					 tmpUserName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));
					 if(tmpUserName!=null && tmpUserName.contains("#786#"))
						 tmpUserName = tmpUserName.substring(0, tmpUserName.indexOf("#786#"));
			}
		}catch(Exception e){}
			finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
		}
			return tmpUserName;
	}
	public String getUserDisplayName(String userName){
		String sql = "SELECT "+ChatDBConstants.CONTACT_NAMES_FIELD +" FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE " + ChatDBConstants.FROM_USER_FIELD + "='"+userName+"' OR "+ ChatDBConstants.TO_USER_FIELD + "='"+userName+"'";
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		String tmpUserName = userName;
		try{
			if (cursor != null && cursor.moveToFirst()) {
					 tmpUserName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));
					 if(tmpUserName!=null && tmpUserName.contains("#786#"))
						 tmpUserName = tmpUserName.substring(0, tmpUserName.indexOf("#786#"));
			}
		}catch(Exception e){}
			finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
		}
			return tmpUserName;
	}
//public Cursor getAtmeContacts(ArrayList<String> previousUsers){
//	Cursor cursor = null;
//	String tags = "";
//	String sql = "";
//	try {
//		if(previousUsers!=null && !previousUsers.isEmpty()){
//	       tags = convertStringArrayToString(previousUsers);
//	 sql = "SELECT * " + " FROM " + ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS
//			+ " WHERE " +ChatDBConstants.VOPIUM_FIELD + "=1 AND "+ ChatDBConstants.USER_NAME_FIELD + " NOT IN " + tags+" ORDER BY "+ChatDBConstants.IS_FAVOURITE_FIELD + " DESC, "
//					+ ChatDBConstants.CONTACT_NAMES_FIELD
//					+ " COLLATE NOCASE";
//		}else
//			 sql = "SELECT * " + " FROM " + ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS
//				+ " WHERE " +ChatDBConstants.VOPIUM_FIELD + "=1 ORDER BY "+ChatDBConstants.IS_FAVOURITE_FIELD + " DESC, "
//						+ ChatDBConstants.CONTACT_NAMES_FIELD
//						+ " COLLATE NOCASE";
//	cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//	} catch (Exception e) {
//		Log.e("ChatDBWrapper",
//				"Exception in updateSeenStatus method " + e.toString());
//	}
//	return cursor;
//}
	
	public Cursor getAtmeContacts(ArrayList<String> previousUsers){
		Cursor cursor = null;
//		String tags = "";
//		String sql = "";
//		try {
//			String colmsOfContactNumbers = ChatDBConstants._ID+", "+ChatDBConstants.NAME_CONTACT_ID_FIELD+", "+ChatDBConstants.CONTACT_NUMBERS_FIELD+", "+ChatDBConstants.CONTACT_NAMES_FIELD+", "+ChatDBConstants.CONTACT_COMPOSITE_FIELD+", "+ChatDBConstants.VOPIUM_FIELD+", "+ChatDBConstants.USER_NAME_FIELD+", "+ChatDBConstants.IS_FAVOURITE_FIELD;
//			String colmsOfContactEmails = ChatDBConstants._ID+", "+ChatDBConstants.NAME_CONTACT_ID_FIELD+", "+ChatDBConstants.PHONE_EMAILS_FIELD+", "+ChatDBConstants.CONTACT_NAMES_FIELD+", "+ChatDBConstants.CONTACT_COMPOSITE_FIELD+", "+ChatDBConstants.VOPIUM_FIELD+", "+ChatDBConstants.USER_NAME_FIELD+", "+ChatDBConstants.IS_FAVOURITE_FIELD;
//			if(previousUsers!=null && !previousUsers.isEmpty()){
//		       tags = convertStringArrayToString(previousUsers);
//		 sql = "SELECT "+colmsOfContactNumbers + " FROM " + ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS
//				+ " WHERE " +ChatDBConstants.VOPIUM_FIELD + "=1 AND "+ ChatDBConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+ChatDBConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserNameId()+"' UNION "+
//				 "SELECT " +colmsOfContactEmails+ " FROM " + ChatDBConstants.TABLE_NAME_CONTACT_EMAILS
//				+ " WHERE " +ChatDBConstants.VOPIUM_FIELD + "=1 AND "+ ChatDBConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+ChatDBConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserNameId()+"' ORDER BY "+ChatDBConstants.IS_FAVOURITE_FIELD + " DESC, "
//						+ ChatDBConstants.CONTACT_NAMES_FIELD
//						+ " COLLATE NOCASE";
//			}else{
//				 sql = "SELECT " +colmsOfContactNumbers+ " FROM " + ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS
//					+ " WHERE " +ChatDBConstants.VOPIUM_FIELD + "=1 AND "+ChatDBConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserNameId()+"' UNION "+"SELECT " +colmsOfContactEmails+ " FROM " + ChatDBConstants.TABLE_NAME_CONTACT_EMAILS
//					+ " WHERE " +ChatDBConstants.VOPIUM_FIELD + "=1 AND "+ChatDBConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserNameId()+"' ORDER BY "+ChatDBConstants.IS_FAVOURITE_FIELD + " DESC, "
//							+ ChatDBConstants.CONTACT_NAMES_FIELD
//							+ " COLLATE NOCASE";
//				 }
//		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//		} catch (Exception e) {
//			Log.e("ChatDBWrapper",
//					"Exception in updateSeenStatus method " + e.toString());
//		}
		return cursor;
	}
	private String convertStringArrayToString(List<String> strList) {
		String[] strs = strList.toArray(new String[strList.size()]);
		Gson gson = new Gson();
		String str = gson.toJson(strs);
		str = str.replace('[', '(');
		str = str.replace(']', ')');
		return str;
	}

	public String getSelectedChatIteams(List<String> msgArray) {
		StringBuilder sb = new StringBuilder();
		Cursor cursor = null;
		String tags = convertStringArrayToString(msgArray);
		try {
			String sql = "SELECT " + ChatDBConstants.MESSAGEINFO_FIELD
					+ " FROM " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " WHERE " + ChatDBConstants.MESSAGE_ID + " IN " + tags+" AND "+ChatDBConstants.MESSAGE_TYPE_FIELD +"= '"+XMPPMessageType.atMeXmppMessageTypeNormal.ordinal()+"'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst()) {
				do {
					sb.append(cursor.getString(cursor
							.getColumnIndex(ChatDBConstants.MESSAGEINFO_FIELD))
							+ " \n");
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.d("ChatDBWrapper", "Exception in deleteSelectedChatIteams method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return sb.toString().trim();
	}

	public ArrayList<String> getRecievedMessages(String userName) {
		Cursor cursor = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			String sql = "SELECT " + ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD
					+ " FROM " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO
					+ " WHERE (" + ChatDBConstants.SEEN_FIELD + " = '"
					+ Message.SeenState.recieved.ordinal() + "' OR "
					+ ChatDBConstants.SEEN_FIELD + " = '"
					+ Message.SeenState.sent.ordinal() + "') AND "
					+ ChatDBConstants.FROM_USER_FIELD + " = '" + userName
					+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null)
				Log.d("ChatDBWrapper",
						"Total row selected in getRecievedMessages: "
								+ cursor.getCount());
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Log.d("ChatDBWrapper",
							"id row selected: "
									+ cursor.getString(cursor
											.getColumnIndex(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD)));

					list.add(cursor.getString(cursor
							.getColumnIndex(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD)));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in getRecievedMessages method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
public String getGroupMessageSenderName(String foreignMessageId){
	String senderGroupPersonUserName = null;
	String sql = "SELECT "+ChatDBConstants.FROM_GROUP_USER_FIELD+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
			+ ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD + " = '" + foreignMessageId + "'";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null)
//		Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
		if (cursor != null && cursor.moveToFirst()){
				String tmpValue = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD));
				if(tmpValue!=null && tmpValue.contains("#786#")){
					tmpValue = tmpValue.substring(tmpValue.indexOf("#786#")+"#786#".length());
					if(tmpValue.contains("#786#"))
						tmpValue = tmpValue.replaceAll("#786#", "");
				 senderGroupPersonUserName = tmpValue;
				}else if(tmpValue!=null && !tmpValue.equals("")){
					senderGroupPersonUserName = tmpValue;
				}
			}
	} catch (Exception e) {
		Log.e("ChatDBWrapper",
				"Exception in getRecievedMessages method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return senderGroupPersonUserName;
}
public String getGroupMessage(String messageId){
	String sentGroupMessage = null;
	String sql = "SELECT "+ChatDBConstants.MESSAGEINFO_FIELD+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
			+ ChatDBConstants.MESSAGE_ID + " = '" + messageId + "'";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null)
//		Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
		if (cursor != null && cursor.moveToFirst()){
				String tmpValue = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGEINFO_FIELD));
				sentGroupMessage = tmpValue;
			}
	} catch (Exception e) {
		Log.e("ChatDBWrapper",
				"Exception in getRecievedMessages method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return sentGroupMessage;
}
public String getGroupMediaTag(String messageId){
	String sentGroupMessage = null;
	String sql = "SELECT "+ChatDBConstants.MEDIA_CAPTION_TAG+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
			+ ChatDBConstants.MESSAGE_ID + " = '" + messageId + "'";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null)
//		Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
		if (cursor != null && cursor.moveToFirst()){
				String tmpValue = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MEDIA_CAPTION_TAG));
				sentGroupMessage = tmpValue;
			}
	} catch (Exception e) {
		Log.e("ChatDBWrapper",
				"Exception in getRecievedMessages method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return sentGroupMessage;
}
public String getGroupMessageType(String messageId){
	String sentGroupMessage = null;
	String sql = "SELECT "+ChatDBConstants.MESSAGE_TYPE_FIELD+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
			+ ChatDBConstants.MESSAGE_ID + " = '" + messageId + "'";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null)
//		Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
		if (cursor != null && cursor.moveToFirst()){
				String tmpValue = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD));
				sentGroupMessage = tmpValue;
			}
	} catch (Exception e) {
		Log.e("ChatDBWrapper",
				"Exception in getRecievedMessages method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return sentGroupMessage;
}
public String getGroupMessageThumb(String messageId){
	String sentGroupMessage = null;
	String sql = "SELECT "+ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
			+ ChatDBConstants.MESSAGE_ID + " = '" + messageId + "'";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null)
//		Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
		if (cursor != null && cursor.moveToFirst()){
				String tmpValue = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
				sentGroupMessage = tmpValue;
			}
	} catch (Exception e) {
		Log.e("ChatDBWrapper",
				"Exception in getRecievedMessages method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return sentGroupMessage;
}
public ArrayList<ContentValues> getAllPersonDocs(String userName){
	ArrayList<ContentValues> mediaList = new ArrayList<ContentValues>();
	String sql = "SELECT "+ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD+","+ChatDBConstants.MESSAGE_TYPE_FIELD+","+ChatDBConstants.MEDIA_CAPTION_TAG+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE ("
			+ ChatDBConstants.FROM_USER_FIELD + " = '" + userName + "' OR "+ ChatDBConstants.TO_USER_FIELD + " = '" + userName + "') AND ("+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypeDoc.ordinal()+"' OR "+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypeXLS.ordinal()+"' OR "+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypePdf.ordinal()+"' OR "+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypePPT.ordinal()+"')"+ " ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD+" DESC";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
	if (cursor != null && cursor.moveToFirst()){
		do{
		String mediaPath = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
		if(mediaPath!=null && !mediaPath.equals("")){
			ContentValues values = new ContentValues();
			values.put(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD, mediaPath);
			int msgType =  cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD));
			values.put(ChatDBConstants.MESSAGE_TYPE_FIELD, msgType);
			String fileName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MEDIA_CAPTION_TAG));
			values.put(ChatDBConstants.MEDIA_CAPTION_TAG, fileName);
			
			mediaList.add(values);
		}
		}while(cursor.moveToNext());
	}
	} catch (Exception e) {
		Log.e("ChatDBWrapper","Exception in getAllPersonMedia method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return mediaList;
}
public ArrayList<ContentValues> getAllPersonMedia(String userName){
	ArrayList<ContentValues> mediaList = new ArrayList<ContentValues>();
//	String sql = "SELECT "+ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD+","+ChatDBConstants.MESSAGE_TYPE_FIELD+" FROM "
//			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE ("
//			+ ChatDBConstants.FROM_USER_FIELD + " = '" + userName + "' OR "+ ChatDBConstants.TO_USER_FIELD + " = '" + userName + "') AND ("+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypeImage.ordinal()+"' OR "+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()+"' OR "+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypeAudio.ordinal()+"')";
	String sql = "SELECT "+ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD+","+ChatDBConstants.MESSAGE_TYPE_FIELD+","+ChatDBConstants.MESSAGE_THUMB_FIELD+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE ("
			+ ChatDBConstants.FROM_USER_FIELD + " = '" + userName + "' OR "+ ChatDBConstants.TO_USER_FIELD + " = '" + userName + "') AND ("+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypeImage.ordinal()+"' OR "+ChatDBConstants.MESSAGE_TYPE_FIELD+"='"+XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()+"')"+ " ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD+" DESC";
	
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
	if (cursor != null && cursor.moveToFirst()){
		do{
		String mediaPath = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
		if(mediaPath!=null && !mediaPath.equals("")){
			ContentValues values = new ContentValues();
			values.put(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD, mediaPath);
			int msgType =  cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD));
			values.put(ChatDBConstants.MESSAGE_TYPE_FIELD, msgType);
			String thumb = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_THUMB_FIELD));
			if(thumb!=null)
			values.put(ChatDBConstants.MESSAGE_THUMB_FIELD, thumb);
			mediaList.add(values);
		}
		}while(cursor.moveToNext());
	}
	} catch (Exception e) {
		Log.e("ChatDBWrapper","Exception in getAllPersonMedia method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return mediaList;
}
public String getCaptionTag(String messageId){
	String sentGroupMessage = null;
	String sql = "SELECT "+ChatDBConstants.MEDIA_CAPTION_TAG+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
			+ ChatDBConstants.MESSAGE_ID + " = '" + messageId + "'";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null)
//		Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
		if (cursor != null && cursor.moveToFirst()){
				String tmpValue = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MEDIA_CAPTION_TAG));
				sentGroupMessage = tmpValue;
			}
	} catch (Exception e) {
		Log.e("ChatDBWrapper",
				"Exception in getRecievedMessages method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return sentGroupMessage;
}
public String getMessageReadTime(String messageId){
	String sentGroupMessage = null;
	String sql = "SELECT "+ChatDBConstants.READ_TIME_FIELD+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
			+ ChatDBConstants.MESSAGE_ID + " = '" + messageId + "'";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null)
//		Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
		if (cursor != null && cursor.moveToFirst()){
				String tmpValue = cursor.getString(cursor.getColumnIndex(ChatDBConstants.READ_TIME_FIELD));
				sentGroupMessage = tmpValue;
			}
	} catch (Exception e) {
		Log.e("ChatDBWrapper",
				"Exception in getRecievedMessages method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return sentGroupMessage;
}
public String getMessageDeliverTime(String messageId,boolean isP2p){
	String sentGroupMessage = null;
	String sql = "SELECT "+ChatDBConstants.LAST_UPDATE_FIELD+","+ChatDBConstants.SEEN_FIELD+" FROM "
			+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
			+ ChatDBConstants.MESSAGE_ID + " = '" + messageId + "'";
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null)
//		Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
		if (cursor != null && cursor.moveToFirst()){
			int statusType = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.SEEN_FIELD));
			if(!isP2p|| (statusType!=SeenState.pic_wait.ordinal() && statusType!=SeenState.wait.ordinal() && statusType!=SeenState.sent.ordinal())){
				String tmpValue = cursor.getString(cursor.getColumnIndex(ChatDBConstants.LAST_UPDATE_FIELD));
				sentGroupMessage = tmpValue;
			}
		}
	} catch (Exception e) {
		Log.e("ChatDBWrapper",
				"Exception in getRecievedMessages method " + e.toString());
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	return sentGroupMessage;
}
	public boolean isFirstChat(String person) {
		boolean ret = false;
		String sql = "SELECT * FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
				+ ChatDBConstants.TO_USER_FIELD + " = '" + person + "' OR "
				+ ChatDBConstants.FROM_USER_FIELD + " = '" + person + "'";
		Cursor cursor = null;
		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor == null || cursor.getCount() <= 0)
				ret = true;
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in getRecievedMessages method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return ret;
	}
	public long lastMessageInDB(String person) {
		long ret = -1;
		
		String sql = "SELECT "+ChatDBConstants.LAST_UPDATE_FIELD+", MAX("+ChatDBConstants.LAST_UPDATE_FIELD+") FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
						+ ChatDBConstants.TO_USER_FIELD + " = '" + person + "' OR "
						+ ChatDBConstants.FROM_USER_FIELD + " = '" + person + "'";;
		Cursor cursor = null;
		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null)
//			Log.d("ChatDBWrapper","lastMessageInDB "+cursor.getCount());
			if (cursor != null && cursor.moveToFirst()){
				ret = cursor.getLong(cursor.getColumnIndex(ChatDBConstants.LAST_UPDATE_FIELD));
				}
			Log.d("ChatDBWrapper","lastMessageInDB in ret "+ret);
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in getRecievedMessages method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return ret;
	}

	public ArrayList<HashMap<String, String>> getUndeliveredMessages(
			String meUser) {
		Cursor cursor = null;

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		try {
			String sql = "SELECT " + ChatDBConstants.MESSAGE_ID + ", "
					+ ChatDBConstants.TO_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGE_MEDIA_URL_FIELD + ", "
					+ ChatDBConstants.MESSAGE_MEDIA_LENGTH + ", "
					+ ChatDBConstants.MESSAGE_THUMB_FIELD + ", "
					+ ChatDBConstants.MESSAGE_TYPE_FIELD + ", "
					+ ChatDBConstants.MEDIA_CAPTION_TAG + ", "
					+ ChatDBConstants.LAST_UPDATE_FIELD + ", "
					+ ChatDBConstants.MESSAGEINFO_FIELD + " FROM "
					+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
					+ ChatDBConstants.SEEN_FIELD + " = '"
					+ Message.SeenState.wait.ordinal() + "' AND "
					+ ChatDBConstants.FROM_USER_FIELD + " = '" + meUser + "'"+ " ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD;
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null)
				Log.d("ChatDBWrapper",
						"Total row selected in getRecievedMessages: "
								+ cursor.getCount());
			if (cursor != null && cursor.moveToFirst()) {
				do {
					HashMap<String, String> hashMap = new HashMap<String, String>();
					hashMap.put(
							ChatDBConstants.MESSAGE_ID,
							cursor.getString(cursor
									.getColumnIndex(ChatDBConstants.MESSAGE_ID)));
					hashMap.put(
							ChatDBConstants.TO_USER_FIELD,
							cursor.getString(cursor
									.getColumnIndex(ChatDBConstants.TO_USER_FIELD)));
					hashMap.put(
							ChatDBConstants.MESSAGEINFO_FIELD,
							cursor.getString(cursor
									.getColumnIndex(ChatDBConstants.MESSAGEINFO_FIELD)));
					int msgType =  cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD));
					
					if(msgType == XMPPMessageType.atMeXmppMessageTypeDoc.ordinal() || msgType == XMPPMessageType.atMeXmppMessageTypePdf.ordinal() 
							|| msgType == XMPPMessageType.atMeXmppMessageTypeXLS.ordinal() || msgType == XMPPMessageType.atMeXmppMessageTypePPT.ordinal() 
							|| msgType == XMPPMessageType.atMeXmppMessageTypeAudio.ordinal() || msgType == XMPPMessageType.atMeXmppMessageTypeImage.ordinal() 
							||msgType == XMPPMessageType.atMeXmppMessageTypeVideo.ordinal()){
						if(msgType != XMPPMessageType.atMeXmppMessageTypeDoc.ordinal() && msgType != XMPPMessageType.atMeXmppMessageTypePdf.ordinal() 
								&& msgType != XMPPMessageType.atMeXmppMessageTypeXLS.ordinal() && msgType != XMPPMessageType.atMeXmppMessageTypePPT.ordinal()
								&& msgType != XMPPMessageType.atMeXmppMessageTypeAudio.ordinal())
							hashMap.put(ChatDBConstants.MESSAGE_THUMB_FIELD,cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_THUMB_FIELD)));
						hashMap.put(ChatDBConstants.MESSAGE_TYPE_FIELD,String.valueOf(msgType));
						hashMap.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD,cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD)));
					}
					list.add(hashMap);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("ChatDBWrapper",
					"Exception in getRecievedMessages method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}
	public boolean isNumberExists(String phoneNumber) {
		boolean isNumberExists = true;
//		String sql = "SELECT * FROM "
//				+ ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
//				+ ChatDBConstants.CONTACT_NUMBERS_FIELD + "='" + phoneNumber+"'";
//		
//		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//		if(cursor == null || cursor.getCount()==0)
//			isNumberExists = false;
//		Log.d("ChatDBWrapper", "isNumberExists query: " + isNumberExists+" , "+sql);
		return isNumberExists;
	}
	public boolean deleteDuplicateRow(int contactId) {
//		String sql = "DELETE FROM "
//				+ ChatDBConstants.TABLE_NAME_CONTACT_NAMES + " WHERE "
//				+ ChatDBConstants.NAME_CONTACT_ID_FIELD + "='" + contactId+"'";
//		Log.d("ChatDBWrapper", "deleteDuplicateRow query: " + sql);
//		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//		if(cursor == null || cursor.getCount()==0)
//		return false;
//		
		return true;
	}
	/**
	 * This method provides the Cursor. It provides all the messages received and sent by you to specific user.And, this messages sorted by time.
	 * You can fetch following fields from this cursor object 
	 * ChatDBConstants._ID, ChatDBConstants.CONTACT_NAMES_FIELD,ChatDBConstants.SEEN_FIELD,ChatDBConstants.MESSAGE_ID,
	 * ChatDBConstants.IS_DATE_CHANGED_FIELD, ChatDBConstants.FROM_GROUP_USER_FIELD, ChatDBConstants.FROM_USER_FIELD,
	 * ChatDBConstants.TO_USER_FIELD, ChatDBConstants.MESSAGEINFO_FIELD, ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD,
	 * ChatDBConstants.MESSAGE_THUMB_FIELD, ChatDBConstants.MESSAGE_TYPE_FIELD, ChatDBConstants.LAST_UPDATE_FIELD,
	 * @param userName userName is the user name string of chat user.
	 * @return Cursor cursor object fetching whole message conversation with specific user.
	 * 
	 */
	public Cursor getUserChatList(String userName, byte type) {
		String sql = null;
		if(type == 1)
			sql = "SELECT " + ChatDBConstants._ID + ", "
					+ ChatDBConstants.CONTACT_NAMES_FIELD + ", "
					+ ChatDBConstants.SEEN_FIELD + ", "
					+ ChatDBConstants.MESSAGE_ID + ", "
					+ ChatDBConstants.IS_DATE_CHANGED_FIELD + ", "
					+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
					+ ChatDBConstants.FROM_USER_FIELD + ", "
					+ ChatDBConstants.TO_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGEINFO_FIELD + ","
					+ ChatDBConstants.MEDIA_CAPTION_TAG + ","
					+ ChatDBConstants.READ_USER_COUNT_FIELD + ","
					+ ChatDBConstants.TOTAL_USER_COUNT_FIELD + ","
					+ ChatDBConstants.MESSAGE_TYPE_LOCATION + ","
					+ ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD + ","
					+ ChatDBConstants.MESSAGE_THUMB_FIELD + ","
					+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
					+ ChatDBConstants.MESSAGE_MEDIA_LENGTH + ","
					+ ChatDBConstants.MESSAGE_MEDIA_URL_FIELD + ","
					+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
					+ ChatDBConstants.LAST_UPDATE_FIELD + " FROM "
					+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE ("
					+ ChatDBConstants.FROM_USER_FIELD + "='" + userName + "' OR "
					+ ChatDBConstants.TO_USER_FIELD + "='" + userName + "') AND "
					+ ChatDBConstants.MESSAGE_TYPE + "!=" + 3
					+ " ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD;
		else
			sql = "SELECT " + ChatDBConstants._ID + ", "
					+ ChatDBConstants.CONTACT_NAMES_FIELD + ", "
					+ ChatDBConstants.SEEN_FIELD + ", "
					+ ChatDBConstants.MESSAGE_ID + ", "
					+ ChatDBConstants.IS_DATE_CHANGED_FIELD + ", "
					+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
					+ ChatDBConstants.FROM_USER_FIELD + ", "
					+ ChatDBConstants.TO_USER_FIELD + ", "
					+ ChatDBConstants.MESSAGEINFO_FIELD + ","
					+ ChatDBConstants.MEDIA_CAPTION_TAG + ","
					+ ChatDBConstants.READ_USER_COUNT_FIELD + ","
					+ ChatDBConstants.TOTAL_USER_COUNT_FIELD + ","
					+ ChatDBConstants.MESSAGE_TYPE_LOCATION + ","
					+ ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD + ","
					+ ChatDBConstants.MESSAGE_THUMB_FIELD + ","
					+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
					+ ChatDBConstants.MESSAGE_MEDIA_LENGTH + ","
					+ ChatDBConstants.MESSAGE_MEDIA_URL_FIELD + ","
					+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
					+ ChatDBConstants.LAST_UPDATE_FIELD + " FROM "
					+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
//					+ ChatDBConstants.FROM_USER_FIELD + "='" + userName + "' OR "
//					+ ChatDBConstants.TO_USER_FIELD + "='" + userName + "' AND "
					+ ChatDBConstants.MESSAGE_TYPE + "=" + 3
					+ " ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD;
		Log.d("ChatDBWrapper", "getRecentChatList query: " + sql);
		Cursor cursor = null;
		try{
			if(dbHelper!=null){
				cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
				Log.i("ChatDBWrapper", "record count - "+cursor.getCount());
			}
			else
				Log.d("ChatDBWrapper", "dbHelper is null.");
		}catch(Exception e){
			
		}
		return cursor;
	}
//	viewholder.key = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID));
//	viewholder.groupMsgSenderName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD));
//	viewholder.captionTagMsg = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MEDIA_CAPTION_TAG));
//	viewholder.locationMsg = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_LOCATION));
//	
//	viewholder.seenState = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.SEEN_FIELD));
//	viewholder.messageType = cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD));
//	viewholder.userName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD));
//	viewholder.receiverName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.TO_USER_FIELD));
//
//	viewholder.message = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGEINFO_FIELD));
//	viewholder.mediaUrl = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD));
//	viewholder.audioLength = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LENGTH));
//	String url = viewholder.mediaUrl;
//	viewholder.mediaLocalPath = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD));
//	viewholder.mediaThumb = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_THUMB_FIELD));
//	viewholder.time = cursor.getLong(cursor.getColumnIndex(ChatDBConstants.LAST_UPDATE_FIELD));
//	viewholder.isDateShow = "1".equals(cursor.getString(cursor.getColumnIndex(ChatDBConstants.IS_DATE_CHANGED_FIELD)));
//	viewholder.totalGroupUsers=cursor.getInt(cursor.getColumnIndex(ChatDBConstants.TOTAL_USER_COUNT_FIELD));
//	viewholder.totalGroupReadUsers=cursor.getInt(cursor.getColumnIndex(ChatDBConstants.READ_USER_COUNT_FIELD));
	public String getAllMessagesForBackup() {
		String query = "SELECT * FROM " + ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD;
		Log.d("ChatDBWrapper", "getAllMessagesForBackup query: " + query);
		Cursor cursor = null;
		String compressed_file_path = null;
        SharedPrefManager pref = SharedPrefManager.getInstance();
		try{
			String backup_dir = "SCBackup";
			String zip_file = "SCBackup.zip";
			createDirIfNotExists(Environment.getExternalStorageDirectory().getAbsolutePath(), backup_dir);
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() +"/" + backup_dir;
//			String filename_message = path + "/" + "message.txt";
//			String filename_status = path + "/" + "status.txt";
			String filename_message = "message.txt";
			String filename_status = "status.txt";
            String from = null;
            String to = null;
            String sent_time = null;
			if(dbHelper!=null){
				//Read data from message table
				cursor = dbHelper.getWritableDatabase().rawQuery(query, null);
				Log.i("ChatDBWrapper", "record count - "+cursor.getCount());
				if (cursor != null && cursor.moveToFirst()){
					 JSONObject backup = new JSONObject();
		             JSONArray message_array = new JSONArray();
		             JSONObject message = null;
		             int i = 1;
					do{
//						1.	recipientCount
//						2.	fromUserName
//						3.	fromGroupUserName
//						4.	messageID
//						5.	textMessage
//						6.	toUserName
//						7.	audioMessageLength
//						8.	caption
//						9.	mediaURL
//						10. mediaLocalPath
//						11. thumbData
//						12. lastUpdateField
//						13. readUserCount
//						14. dataChanged
//						15. locationMessage
//						16. broadcastMessageID
//						17. foreignMessageID
//						18. contactName
//						19. unreadCount
//						20. seen
//						21. mediaStatus
//						22.	readTime
//						23.	messageType
//						24. messageTypeID
				//========================= FOR IOS ====		
//						1.	chatType
//						2.	userID
//						3.	date
//						4.	dateTime
//						5.	status
//						6.	deliveryTime

						 message = new JSONObject();
                         from = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD));
                         to = cursor.getString(cursor.getColumnIndex(ChatDBConstants.TO_USER_FIELD));
						 message.put("fromUserName", from);
			             message.put("fromGroupUserName", cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD)));
			             message.put("messageID", cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID)));
			             message.put("toUserName", to);
			             message.put("textMessage", cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGEINFO_FIELD)));
			             message.put("caption", cursor.getString(cursor.getColumnIndex(ChatDBConstants.MEDIA_CAPTION_TAG)));
			             message.put("broadcastMessageID", cursor.getString(cursor.getColumnIndex(ChatDBConstants.BROADCAST_MESSAGE_ID)));
			             message.put("foreignMessageID", cursor.getString(cursor.getColumnIndex(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD)));
			             message.put("contactName", cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD)));
			             message.put("locationMessage", cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_LOCATION)));
			             
			             message.put("unreadCount", cursor.getInt(cursor.getColumnIndex(ChatDBConstants.UNREAD_COUNT_FIELD)));
			             message.put("seen", cursor.getInt(cursor.getColumnIndex(ChatDBConstants.SEEN_FIELD)));
			             message.put("mediaStatus", cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MEDIA_STATUS)));
			             message.put("messageType", cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE)));
			             message.put("messageTypeID", cursor.getInt(cursor.getColumnIndex(ChatDBConstants.MESSAGE_TYPE_FIELD)));
			             message.put("dataChanged", cursor.getInt(cursor.getColumnIndex(ChatDBConstants.IS_DATE_CHANGED_FIELD)));
			             message.put("recipientCount", cursor.getInt(cursor.getColumnIndex(ChatDBConstants.TOTAL_USER_COUNT_FIELD)));
			             message.put("readUserCount", cursor.getInt(cursor.getColumnIndex(ChatDBConstants.READ_USER_COUNT_FIELD)));
			             
			             message.put("mediaURL", cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD)));
			             message.put("audioMessageLength", cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LENGTH)));
			             message.put("mediaLocalPath", cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD)));

                        //Do not add thumb data, thumbData wil be fetched from server
//			             message.put("thumbData", cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_THUMB_FIELD)));


                        sent_time = convertToDate(cursor.getLong(cursor.getColumnIndex(ChatDBConstants.LAST_UPDATE_FIELD)));
                        message.put("readTime", cursor.getLong(cursor.getColumnIndex(ChatDBConstants.READ_TIME_FIELD)));
                        message.put("lastUpdateField", cursor.getLong(cursor.getColumnIndex(ChatDBConstants.LAST_UPDATE_FIELD)));

						//Some additional values for IOS
                        if(sent_time != null)
                            message.put("dateTime", sent_time);
                         if(pref.isGroupChat(from) || pref.isGroupChat(to)) {
                             message.put("chatType", 2);
                             if(pref.isGroupChat(from))
                                message.put("roomID", from);
                             else if(pref.isGroupChat(to))
                                 message.put("roomID", to);
                         }
                        else if(pref.isBroadCast(from) || pref.isBroadCast(to)) {
                             message.put("chatType", 4);
                             if(pref.isBroadCast(from))
                                 message.put("roomID", from);
                             else if(pref.isBroadCast(to))
                                 message.put("roomID", to);
                         }

                        from = to = sent_time = null;


//			             message.put(ChatDBConstants.MESSAGE_ID, cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID)));
//			             message.put(ChatDBConstants.FROM_GROUP_USER_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD)));
//			             message.put(ChatDBConstants.FROM_USER_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD)));
//			             message.put(ChatDBConstants.TO_USER_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.TO_USER_FIELD)));
//			             message.put(ChatDBConstants.MESSAGEINFO_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGEINFO_FIELD)));
//			             message.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD)));
//			             message.put(ChatDBConstants.MESSAGE_MEDIA_LENGTH, cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LENGTH)));
//			             message.put(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD)));
//			             message.put(ChatDBConstants.MESSAGE_THUMB_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_THUMB_FIELD)));
//			             message.put(ChatDBConstants.LAST_UPDATE_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.LAST_UPDATE_FIELD)));
//			             message.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.IS_DATE_CHANGED_FIELD)));
//			             message.put(ChatDBConstants.TOTAL_USER_COUNT_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.TOTAL_USER_COUNT_FIELD)));
//			             message.put(ChatDBConstants.READ_USER_COUNT_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.READ_USER_COUNT_FIELD)));
			             message_array.put(message);
			             System.out.println("==> "+(i++) + ":: "+message.toString());
					} while (cursor.moveToNext());
					if(message_array.length() > 0)
						backup.put("messages", message_array);
					
					//Write message table date to file
					File f = new File(filename_message);
					byte[] data = backup.toString().getBytes("utf-8");
					if(data.length > 0)
					{
						f = new File(path, filename_message);
						Utilities.writeFile(data, f, 1);
					}
				}
				
				//Read data from status table
				query = "SELECT*FROM " + ChatDBConstants.TABLE_NAME_STATUS_INFO;
				cursor = dbHelper.getWritableDatabase().rawQuery(query, null);
				Log.i("ChatDBWrapper", "record count - "+cursor.getCount());
				if (cursor != null && cursor.moveToFirst()){
					 JSONObject backup = new JSONObject();
		             JSONArray status_array = new JSONArray();
		             JSONObject message = null;
					do{
						 message = new JSONObject();
			             message.put(ChatDBConstants.MESSAGE_ID, cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGE_ID)));
			             message.put(ChatDBConstants.SEEN_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.SEEN_FIELD)));
			             message.put(ChatDBConstants.DELIVER_TIME_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.DELIVER_TIME_FIELD)));
			             message.put(ChatDBConstants.SEEN_TIME_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.SEEN_TIME_FIELD)));
			             message.put(ChatDBConstants.FROM_USER_FIELD, cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD)));
			             status_array.put(message);
					} while (cursor.moveToNext());
					if(status_array.length() > 0)
						backup.put("messageStatus", status_array);
					
					//Write status table date to file
					File f = new File(filename_status);
					byte[] data = backup.toString().getBytes("utf-8");
					if(data.length > 0)
					{
						f = new File(path, filename_status);
						Utilities.writeFile(data, f, 1);
					}
				}
				//Create Zip for message and status files
				compressed_file_path = path + "/" + zip_file;
				String[] files = new String[]{path + "/" + filename_message};
				if(new File(path + "/" + filename_status).exists())
					files = new String[]{path + "/" + filename_message, path + "/" + filename_status};
				ZipManager compress = new ZipManager(files, compressed_file_path);
				compress.zip();
//				System.out.println("path : "+path);
			}
			else
				Log.d("ChatDBWrapper", "dbHelper is null.");
		}catch(Exception e){
			
		}
		return compressed_file_path;
	}
    public String convertToDate(long millis){
        String date_converted = null;
        if(millis == 0)
            return null;
        try {
            Date date = new Date(millis);
//        DateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
            date_converted = formatter.format(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return date_converted;
    }
	public boolean createDirIfNotExists(String path, String folder_name) {
	    boolean ret = true;
	    File file = new File(path, folder_name);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("TravellerLog :: ", "Problem creating Image folder");
	            ret = false;
	        }
	    }
	    return ret;
	}
//======================================================
	public int insertBackUpInDB(JSONArray data) {
		int success = 0;
		try{
			 Gson gson = new GsonBuilder().create();
//			 Iterator full_data = data.iterator();
			 for (int i = 0; i < data.length(); i++){
//		        JSONObject message = (JSONObject) full_data.next();
		        JSONObject message = data.getJSONObject(i);
		        System.out.println("==> "+(i+1)+":: "+message.toString());
				MessageDataModel message_data = gson.fromJson(message.toString(), MessageDataModel.class);
				
				ContentValues contentvalues = new ContentValues();
				if(message_data.fromUserName != null)
					contentvalues.put(ChatDBConstants.FROM_USER_FIELD, message_data.fromUserName);
				if(message_data.toUserName != null)
					contentvalues.put(ChatDBConstants.TO_USER_FIELD, message_data.toUserName);
				if(message_data.fromGroupUserName != null)
					contentvalues.put(ChatDBConstants.FROM_GROUP_USER_FIELD, message_data.fromGroupUserName);
				contentvalues.put(ChatDBConstants.MESSAGE_TYPE, message_data.messageType);
				if(message_data.caption != null)
					contentvalues.put(ChatDBConstants.MEDIA_CAPTION_TAG, message_data.caption);
				if(message_data.locationMessage != null)
					contentvalues.put(ChatDBConstants.MESSAGE_TYPE_LOCATION, message_data.locationMessage);
				contentvalues.put(ChatDBConstants.MESSAGE_TYPE_FIELD, message_data.messageTypeID);
				contentvalues.put(ChatDBConstants.UNREAD_COUNT_FIELD, message_data.unreadCount);
				contentvalues.put(ChatDBConstants.SEEN_FIELD, message_data.seen);
				if(message_data.mediaURL != null)
					contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_URL_FIELD, message_data.mediaURL);
				if(message_data.thumbData != null)
					contentvalues.put(ChatDBConstants.MESSAGE_THUMB_FIELD, message_data.thumbData);
				if(message_data.audioMessageLength != null)
					contentvalues.put(ChatDBConstants.MESSAGE_MEDIA_LENGTH, message_data.audioMessageLength);
				if(message_data.textMessage != null)
					contentvalues.put(ChatDBConstants.MESSAGEINFO_FIELD, message_data.textMessage);
				if(message_data.messageID != null)
					contentvalues.put(ChatDBConstants.MESSAGE_ID, message_data.messageID);
				if(message_data.foreignMessageID != null)
				contentvalues.put(ChatDBConstants.FOREIGN_MESSAGE_ID_FIELD, message_data.foreignMessageID);
				contentvalues.put(ChatDBConstants.IS_DATE_CHANGED_FIELD, message_data.dataChanged);
				contentvalues.put(ChatDBConstants.LAST_UPDATE_FIELD, message_data.lastUpdateField);
				if(message_data.contactName != null)
					contentvalues.put(ChatDBConstants.CONTACT_NAMES_FIELD, message_data.contactName);
				//insert in DB
				insertInDB(ChatDBConstants.TABLE_NAME_MESSAGE_INFO,contentvalues);
		     }
			 success = 1;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return success;
	}
//====================================================================
	public Cursor getUserBroadCastChatList(String broadCastName) {
//		select * from Customers where customerId in (select max(customerId) as customerId from Customers group by country);
		String sql = "SELECT * FROM "+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO +" WHERE "+ChatDBConstants.FROM_USER_FIELD+"='"+broadCastName+"' AND "+ ChatDBConstants._ID + " IN (SELECT MAX("
				+ChatDBConstants._ID+") AS "+ChatDBConstants._ID+" FROM "+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO+" group by "+ ChatDBConstants.BROADCAST_MESSAGE_ID
				+") "+" ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD;
//				+ChatDBConstants.CONTACT_NAMES_FIELD + ", "
//				+ ChatDBConstants.SEEN_FIELD + ", "
//				+ ChatDBConstants.MESSAGE_ID + ", "
//				+ ChatDBConstants.IS_DATE_CHANGED_FIELD + ", "
//				+ ChatDBConstants.FROM_GROUP_USER_FIELD + ", "
//				+ ChatDBConstants.FROM_USER_FIELD + ", "
//				+ ChatDBConstants.TO_USER_FIELD + ", "
//				+ ChatDBConstants.MESSAGEINFO_FIELD + ","
//				+ ChatDBConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD + ","
//				+ ChatDBConstants.MESSAGE_THUMB_FIELD + ","
//				+ ChatDBConstants.MESSAGE_TYPE_FIELD + ","
//				+ ChatDBConstants.MESSAGE_MEDIA_URL_FIELD + ","
//				+ ChatDBConstants.UNREAD_COUNT_FIELD + ","
//				+ ChatDBConstants.LAST_UPDATE_FIELD + " FROM "
//				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
//				+ ChatDBConstants.FROM_USER_FIELD + "='" + userName + "' OR "
//				+ ChatDBConstants.TO_USER_FIELD + "='" + userName
//				+ "' ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD;
		Log.d("ChatDBWrapper", "getRecentChatList query: " + sql);
		Cursor cursor = null;
		if(dbHelper!=null)
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		else
			Log.d("ChatDBWrapper", "dbHelper is null.");
		return cursor;
	}
	public ArrayList<String> getChatHistory(String userName){
		ArrayList<String> list = new ArrayList<String>();
		String sql = "SELECT " + ChatDBConstants.MESSAGEINFO_FIELD +","+ChatDBConstants.CONTACT_NAMES_FIELD+","+ChatDBConstants.FROM_GROUP_USER_FIELD+","+ChatDBConstants.FROM_USER_FIELD+" FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
				+ ChatDBConstants.FROM_USER_FIELD + "='" + userName + "' OR "
				+ ChatDBConstants.TO_USER_FIELD + "='" + userName
				+ "' ORDER BY " + ChatDBConstants.LAST_UPDATE_FIELD;
		Log.d("ChatDBWrapper", "getRecentChatList query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
        SharedPrefManager pref = SharedPrefManager.getInstance();
		try{
			if (cursor != null && cursor.moveToFirst()) {
				do {
					String name = "";
					
					if(pref.isGroupChat(userName)){
						name = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD));
						if(name!=null && name.contains("#786#")){
				        	name = name.replace("#786#m", "@+");
				        	
				        	
					 }
						if(name==null || name.equals("")){
//			        		name = SharedPrefManager.getInstance().getDisplayName()+"@"+SharedPrefManager.getInstance().getUserName().replaceFirst("m","+");
			        		name = pref.getDisplayName();
			        	}
					}else{
						name = cursor.getString(cursor.getColumnIndex(ChatDBConstants.CONTACT_NAMES_FIELD));
						 String fromName = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_USER_FIELD));
						 String tmpUserName = fromName;
//						 String tmpUserName = SharedPrefManager.getInstance().getUserServerName(fromName);
						 boolean isMe = pref.getUserName().equals(fromName)?true:false;
//						 if(fromName!=null)
//							 fromName = fromName.replaceFirst("m", "+");
						 if(isMe){
//							 name = SharedPrefManager.getInstance().getDisplayName()+"@"+fromName;
							 name = pref.getDisplayName();
						 }
						 if(name!=null && name.contains("#786#")){
//					        	name = name.replace("#786#m", "@+");	
					        	name = name.substring(0, name.indexOf("#786#"));	
						 }
						
						 if(name == null){
							 
								 name = fromName;
							 }
//						 if(name.equals(tmpUserName)){
//							 name = name.replaceFirst("m", "+");
//						 }
					}
					if(name!=null && name.contains("#786#")){
			        	name = name.substring(0, name.indexOf("#786#"));	
					}
					String user = cursor.getString(cursor.getColumnIndex(ChatDBConstants.MESSAGEINFO_FIELD));
					if(user != null && !user.equals("")){
						if(name!=null && !name.equals(""))
							user = name+": "+user;
						list.add(user);
						}
				} while (cursor.moveToNext());
			}
		}
		catch(Exception e){}
		finally{
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}
	public ArrayList<String> getUsersOfGroup(String groupName){
		ArrayList<String> list = new ArrayList<String>();
		String sql = "SELECT DISTINCT(" + ChatDBConstants.FROM_GROUP_USER_FIELD +") FROM "
				+ ChatDBConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
				+ ChatDBConstants.FROM_USER_FIELD + "='" + groupName + "' OR "
				+ ChatDBConstants.TO_USER_FIELD + "='" + groupName+"'";
		Log.d("ChatDBWrapper", "getRecentChatList query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		try{
			if (cursor != null && cursor.moveToFirst()) {
				do {
					String user = cursor.getString(cursor.getColumnIndex(ChatDBConstants.FROM_GROUP_USER_FIELD));
					if(user != null && !user.equals(""))
						list.add(user);
				} while (cursor.moveToNext());
			}
		}
		catch(Exception e){}
		finally{
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}
	public void updateAtMeDirectStatus(ContentValues contentvalues, String type) {
//		String contactNumber = String.valueOf(contentvalues.get(type));
//		Log.d("ChatDBWrapper", "updateAtMeDirect sip address: " + contactNumber);
//		if (contactNumber != null) {
//			int contactId = -1;
//			if (contactNumber.contains("@")) {
//				contactId = getContactIDFromEmailData(
//						ChatDBConstants.PHONE_EMAILS_FIELD + " = ?",
//						new String[] { contactNumber });
//
//			} else {
//				contactId = getContactIDFromData(
//						ChatDBConstants.CONTACT_NUMBERS_FIELD + " = ?",
//						new String[] { contactNumber });
//			}
//			if (contactId == -1)
//				return;
//			ChatDBWrapper dbwrapper = ChatDBWrapper.getInstance();
//			dbwrapper.beginTransaction();
//			contentvalues.remove(type);
//			dbwrapper.updateInDB(ChatDBConstants.TABLE_NAME_CONTACT_NAMES,
//					contentvalues, ChatDBConstants.NAME_CONTACT_ID_FIELD
//					+ " = ?",
//					new String[] { String.valueOf(contactId) });
//			dbwrapper.setTransaction();
//			dbwrapper.endTransaction();
//		}
	}
	public void updateAtMeContactDetails(ContentValues contentvalues, String contactNumber) {
//		Log.d("ChatDBWrapper", "updateAtMeContactDetails address: " + contactNumber);
//		if (contactNumber != null) {
//			ChatDBWrapper dbwrapper = ChatDBWrapper.getInstance();
//			dbwrapper.beginTransaction();
//			contentvalues.remove(ChatDBConstants.USER_SIP_ADDRESS);
//			if (contactNumber.contains("@")){
//				dbwrapper.updateInDB(ChatDBConstants.TABLE_NAME_CONTACT_EMAILS,
//						contentvalues, ChatDBConstants.PHONE_EMAILS_FIELD
//						+ " = ?",
//						new String[] {contactNumber});
//			}else{
//				dbwrapper.updateInDB(ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS,
//						contentvalues, ChatDBConstants.CONTACT_NUMBERS_FIELD
//						+ " = ?",
//						new String[] {contactNumber});
//			}
//			dbwrapper.setTransaction();
//			dbwrapper.endTransaction();
//		}
	}
	public int getContactId(String contactNumber) {

		return 1;
	}

	public int getRowsCount(String s) {
		Cursor cursor = dbHelper.getWritableDatabase().query(s, null, null,
				null, null, null, null);
		int i = cursor.getCount();
		cursor.close();
		return i;
	}

	public String getValues(String s, String s1, String s2) {
		Cursor cursor = dbHelper.getWritableDatabase().query(s,
				new String[] { s1 }, null, null, null, null, null);
		StringBuilder stringbuilder = new StringBuilder();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				String s3 = cursor.getString(cursor.getColumnIndex(s1));
				stringbuilder.append((new StringBuilder()).append("'")
						.append(s3).append("'").toString());
				if (!cursor.isLast()) {
					stringbuilder.append(s2);
				}
			} while (cursor.moveToNext());
		}
		if (cursor != null) {
			cursor.close();
		}
		return stringbuilder.toString();
	}

	public void initializeDB() {
//		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
//		ContentValues contentvalues = new ContentValues();
//		contentvalues.put(ChatDBConstants.VOPIUM_FIELD, Integer.valueOf(0));
//		contentvalues.put(ChatDBConstants.IS_FAVOURITE_FIELD,
//				Integer.valueOf(0));
//		int i = sqlitedatabase.update(
//				ChatDBConstants.TABLE_NAME_CONTACT_NAMES, contentvalues,
//				null, null);
//		Log.i(TAG,
//				(new StringBuilder())
//				.append("Initialized contact_names rows: ").append(i)
//				.toString());
//		ContentValues contentvalues1 = new ContentValues();
//		// contentvalues1.put("data11", Integer.valueOf(0));
//		contentvalues1.put(ChatDBConstants.STATE_FIELD, Integer.valueOf(0));
//		int j = sqlitedatabase.update(
//				ChatDBConstants.TABLE_NAME_CONTACT_NUMBERS, contentvalues1,
//				null, null);
//		Log.i(TAG,
//				(new StringBuilder())
//				.append("Initialized contact_numbers rows: ").append(j)
//				.toString());
	}

	public long insertInDB(String s, ContentValues contentvalues) {
		return dbHelper.getWritableDatabase().insert(s, null, contentvalues);
	}

	public long insertInDB(String s, ArrayList arraylist) {
		SQLiteDatabase sqlitedatabase;

		int j;
		int k;
		sqlitedatabase = dbHelper.getWritableDatabase();
		long l = 0L;
		int size = arraylist.size();
		sqlitedatabase.beginTransaction();
		j = 0;
		k = 0;
		while (true) {
			if (k >= size) {
				break;
			}
			try {
				l = sqlitedatabase.insert(s, null,
						(ContentValues) arraylist.get(k));
				j++;
				if (j < TRANSACTION_THRESHOLD) {
					break;
				}
				sqlitedatabase.setTransactionSuccessful();
				sqlitedatabase.endTransaction();
				sqlitedatabase.beginTransaction();

			} catch (Exception exception1) {
				sqlitedatabase.endTransaction();
				return l;
			} finally {
				sqlitedatabase.endTransaction();
			}
			k++;
		}
		return l;

	}

	public boolean isInTransaction() {
		return dbHelper.getWritableDatabase().inTransaction();
	}

	public Cursor query(String s, String as[], String s1, String as1[],
			String s2) {
		return dbHelper.getWritableDatabase().query(s, as, s1, as1, null, null,
				s2);
	}

	public void setTransaction() {
		dbHelper.getWritableDatabase().setTransactionSuccessful();
	}

	public int updateInDB(String s, ContentValues contentvalues, String s1,
			String as[]) {
		return dbHelper.getWritableDatabase().update(s, contentvalues, s1, as);
	}

	public long updateInDB(String s, ArrayList arraylist, String s1) {
		int k = 0;
		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
		long l = 0L;
		int size = arraylist.size();
		sqlitedatabase.beginTransaction();
		int j = 0;
		String as[];
		ContentValues contentvalues;
		long l1;
		try {
			as = new String[1];
			while (true) {
				if (k >= size) {
					break;
				}
				contentvalues = (ContentValues) arraylist.get(k);
				l1 = contentvalues.getAsLong(s1).longValue();
				as[0] = (new StringBuilder()).append(l1).append("").toString();
				contentvalues.remove(s1);
				l = sqlitedatabase.update(s, contentvalues,
						(new StringBuilder()).append(s1).append("=?")
						.toString(), as);
				j++;
				if (j < TRANSACTION_THRESHOLD) {
					break;
				}
				sqlitedatabase.setTransactionSuccessful();
				sqlitedatabase.endTransaction();
				sqlitedatabase.beginTransaction();
				k++;
			}
		} catch (Exception exception1) {
			sqlitedatabase.endTransaction();
			return l;
		} finally {
			sqlitedatabase.endTransaction();
		}

		return l;
	}
}