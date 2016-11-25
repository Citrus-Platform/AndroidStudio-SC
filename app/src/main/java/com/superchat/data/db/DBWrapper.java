package com.superchat.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.chat.sdk.db.ChatDBWrapper;
import com.chatsdk.org.jivesoftware.smack.packet.Message;
import com.google.gson.Gson;
import com.superchat.SuperChatApplication;
import com.superchat.model.LoginResponseModel.UserResponseDetail;
import com.superchat.model.multiplesg.InvitedDomainNameSet;
import com.superchat.model.multiplesg.JoinedDomainNameSet;
import com.superchat.model.multiplesg.OwnerDomainName;
import com.superchat.utils.Constants;
import com.superchat.utils.Log;
import com.superchat.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Referenced classes of package com.vopium.data.db:
//            PhoenixDatabaseHelper

public class DBWrapper {
	public static final String TAG = "DBWrapper";
	public static long COPY_THRESHOLD = 60000L;
	public static int TRANSACTION_THRESHOLD = 100;
	private static DBWrapper dbWrapper = null;
	private PhoenixDatabaseHelper dbHelper;

	private DBWrapper() {
		dbHelper = null;
		dbHelper = new PhoenixDatabaseHelper(SuperChatApplication.context);
	}

	public static DBWrapper getInstance() {
		if (dbWrapper == null) {
			dbWrapper = new DBWrapper();
		}
		return dbWrapper;
	}

	public static DBWrapper getInstance(Context context) {
		if (SuperChatApplication.context == null) {
			SuperChatApplication.context = context;
		}
		if (dbWrapper == null) {
			dbWrapper = new DBWrapper();
		}
		return dbWrapper;

	}

	public void beginTransaction() {
		dbHelper.getWritableDatabase().beginTransaction();
	}
	public void setTransactionSuccessful() {
		dbHelper.getWritableDatabase().setTransactionSuccessful();
	}
	public void clearAllDB() {
		if(isTableExists(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS)) {
			SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
			beginTransaction();
			int i = sqlitedatabase.delete(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, null, null);
			Log.i(TAG, (new StringBuilder()).append("Deleted from Data::").append(i).toString());
			setTransaction();
			endTransaction();
		}
		if(isTableExists(DatabaseConstants.TABLE_NAME_MULTIPLE_SG)) {
			SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
			beginTransaction();
			int i = sqlitedatabase.delete(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, null, null);
			Log.i(TAG, (new StringBuilder()).append("Deleted from Data::").append(i).toString());
			setTransaction();
			endTransaction();
		}
	}

	public void deleteTable(String table) {
		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
		beginTransaction();
		int i = sqlitedatabase.delete(table, null, null);
		Log.i(TAG, (new StringBuilder()).append("Deleted from Data::").append(i).toString());
		setTransaction();
		endTransaction();
	}

	public void dropTable(String table) {
		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
		beginTransaction();
		sqlitedatabase.execSQL("drop table if exists "+table);
		int i = sqlitedatabase.delete(table, null, null);
		Log.i(TAG, (new StringBuilder()).append("Deleted from Data::").append(i).toString());
		setTransaction();
		endTransaction();
	}

	public boolean isTableExists(String tableName)
	{
	    if (tableName == null || dbHelper.getWritableDatabase() == null || !dbHelper.getWritableDatabase().isOpen())
	    {
	        return false;
	    }
	    Cursor cursor = dbHelper.getWritableDatabase().rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[] {"table", tableName});
	    if (!cursor.moveToFirst())
	    {
	        return false;
	    }
	    int count = cursor.getInt(0);
	    cursor.close();
	    return count > 0;
	}

	public void clearMessageDB() {
		try{
			if(isTableExists(DatabaseConstants.TABLE_NAME_MESSAGE_INFO)) {
				SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
				int i = sqlitedatabase.delete(DatabaseConstants.TABLE_NAME_MESSAGE_INFO, null, null);
				Log.i(TAG, (new StringBuilder()).append("Deleted from Data::").append(i).toString());
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
		Cursor cursor = dbHelper.getWritableDatabase().query(true,
				DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
				new String[] { DatabaseConstants.CONTACT_NUMBERS_FIELD }, null,
				null, null, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Log.d("DBWrapper",
							"DbNumbers: "
									+ cursor.getString(cursor
											.getColumnIndex(DatabaseConstants.CONTACT_NUMBERS_FIELD)));
					list.add(cursor.getString(cursor
							.getColumnIndex(DatabaseConstants.CONTACT_NUMBERS_FIELD)));
				}
			}
		} catch (Exception e) {
			Log.d("DBWrapper",
					"Exception in getAllNumbers method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return list;
	}
	public int getAllNumbersCount() {
		int totalContacts = 0;
		String sg = SharedPrefManager.getInstance().getUserDomain();
		Cursor cursor = dbHelper.getWritableDatabase().query(true,
				DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
				new String[] { DatabaseConstants.CONTACT_NUMBERS_FIELD }, DatabaseConstants.VOPIUM_FIELD+"=?" + " AND " + DatabaseConstants.USER_SG + "=?",
				new String[] { "1", sg }, null, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				totalContacts = cursor.getCount();
			}
		} catch (Exception e) {
			Log.d("DBWrapper",
					"Exception in getAllNumbersCount method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return totalContacts;
	}

	public List<String> getAllEmails() {
		List<String> list = new ArrayList<String>();
		Cursor cursor = dbHelper.getWritableDatabase().query(true,
				DatabaseConstants.TABLE_NAME_CONTACT_EMAILS,
				new String[] { DatabaseConstants.PHONE_EMAILS_FIELD }, null,
				null, null, null, null, null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					Log.d("DBWrapper",
							"DbEmails: "
									+ cursor.getString(cursor
											.getColumnIndex(DatabaseConstants.PHONE_EMAILS_FIELD)));
					list.add(cursor.getString(cursor
							.getColumnIndex(DatabaseConstants.PHONE_EMAILS_FIELD)));
				}
			}
		} catch (Exception e) {
			Log.d("DBWrapper",
					"Exception in getAllEmails method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return list;
	}

	public String getContactName(String userName) {
		String contactPerson = "Unknown-Person";
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_CONTACT_NAMES,
				new String[] { DatabaseConstants.CONTACT_NAMES_FIELD },
				DatabaseConstants.USER_NAME_FIELD + "='" + userName + "'",
				null, null);
		if (cursor != null) {
			while (cursor.moveToNext())
				contactPerson = cursor.getString(cursor
						.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD));// Log.d(TAG,
			// TAG+"::"+cursor.getString(cursor.getColumnIndex("name"))+" + "+cursor.getString(cursor.getColumnIndex("DatabaseConstants.NAME_CONTACT_ID_FIELD")));
		}
		if (cursor != null)
			cursor.close();
		return contactPerson;
	}

	public String getContactNumber(String userName) {
		String contactNumber = null;
		Cursor cursor = null;
		String sg = SharedPrefManager.getInstance().getUserDomain();
		try{
			 cursor = DBWrapper.getInstance().query(
					DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
					new String[] { DatabaseConstants.CONTACT_NUMBERS_FIELD },
					DatabaseConstants.USER_NAME_FIELD + "='" + userName + "' AND "+DatabaseConstants.USER_SG + "='" + sg + "'",
					null, null);
			if (cursor != null) {
				Log.d(TAG, "Total contact numbers with respect of " + userName
						+ ": " + cursor.getCount());
				if(cursor.moveToNext())
					contactNumber = cursor
					.getString(cursor
							.getColumnIndex(DatabaseConstants.CONTACT_NUMBERS_FIELD));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if (cursor != null)
				cursor.close();
		}
		
		
		return contactNumber;
	}
	
	public String getContactID(String userName) {
		String contactId = "";
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
				new String[] { DatabaseConstants.NAME_CONTACT_ID_FIELD },
				DatabaseConstants.USER_NAME_FIELD + "='" + userName + "'",
				null, null);
		try{
			if (cursor != null) {
				Log.d(TAG, "getContactID numbers with respect of " + userName + ": " + cursor.getCount());
				if(cursor.moveToNext())
					contactId = cursor
					.getString(cursor
							.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
			}
		}catch(Exception e){}
		if (cursor != null)
			cursor.close();
		try{
			if(contactId!=null && !contactId.equals("")){
				cursor = DBWrapper.getInstance().query(
						DatabaseConstants.NAME_CONTACT_ID_FIELD,
						new String[] { DatabaseConstants.USER_NAME_FIELD },
						DatabaseConstants.NAME_CONTACT_ID_FIELD + "='" + contactId + "'",
						null, null);
				contactId = "";
				if (cursor != null) {
					Log.d(TAG, "getContactID numbers with respect of " + contactId + ": " + cursor.getCount());
					
					if(cursor.moveToNext())
						contactId = cursor
						.getString(cursor
								.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
				}
			}
		}catch(Exception e){}
		if (cursor != null)
			cursor.close();
		return contactId;
	}
	public String getContactEmail(String userName) {
		String contactNumber = "";
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_CONTACT_EMAILS,
				new String[] { DatabaseConstants.PHONE_EMAILS_FIELD },
				DatabaseConstants.USER_NAME_FIELD + "='" + userName + "'",
				null, null);
		if (cursor != null) {
			Log.d(TAG, "Total contact numbers with respect of " + userName
					+ ": " + cursor.getCount());
			if(cursor.moveToNext())
				contactNumber = cursor
				.getString(cursor
						.getColumnIndex(DatabaseConstants.PHONE_EMAILS_FIELD));
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
	contentvalues1.put(DatabaseConstants.NAME_CONTACT_ID_FIELD,Integer.valueOf((int)System.currentTimeMillis()));
	contentvalues1.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,mobileNumber);
	contentvalues1.put(DatabaseConstants.CONTACT_COMPOSITE_FIELD, mobileNumber);
	contentvalues1.put(DatabaseConstants.CONTACT_NAMES_FIELD, contactName);
	contentvalues1.put(DatabaseConstants.DATA_ID_FIELD,Integer.valueOf(0));
	contentvalues1.put(DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(1));
	contentvalues1.put(DatabaseConstants.USER_NAME_FIELD, userName);
	contentvalues1.put(DatabaseConstants.STATE_FIELD,Integer.valueOf(0));
	insertInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,contentvalues1);
	}catch(Exception e){}
}
public HashMap<String,String> getUsersDisplayNameList(List<String> list){
	HashMap<String,String> namesList = new HashMap<String,String>();
	String sg = SharedPrefManager.getInstance().getUserDomain();
	if(list.isEmpty())
		return namesList;
//		ArrayList<String> namesList = new ArrayList<String>();
	
		Cursor cursor = null;
//		list.remove(SharedPrefManager.getInstance().getUserName());
			String tags = convertStringArrayToString(list);

			String sql = "SELECT "+DatabaseConstants.CONTACT_NAMES_FIELD+","+DatabaseConstants.USER_NAME_FIELD +" FROM "
					+ DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
					+ DatabaseConstants.USER_NAME_FIELD + " IN " + tags + " AND " + DatabaseConstants.USER_SG + "='"+sg+"'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			try {
				if (cursor != null) {
					while (cursor.moveToNext()){
						String tmpUserName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
						if(tmpUserName!=null && !tmpUserName.equals("")){
							
								namesList.put(tmpUserName,cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD)));
							}
					}
				}
			} catch (Exception e) {
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
			namesList.put(SharedPrefManager.getInstance().getUserName(),SharedPrefManager.getInstance().getDisplayName());
	return namesList;
}
public List<ContentValues> getUsersInfoFromUserNameList(List<String> list){
	List<ContentValues> namesList = new ArrayList<ContentValues>();
	if(list.isEmpty())
		return namesList;
//		ArrayList<String> namesList = new ArrayList<String>();
	
	Cursor cursor = null;
	String tags = convertStringArrayToString(list);
	
	String sql = "SELECT "+DatabaseConstants.CONTACT_NAMES_FIELD+","+DatabaseConstants.CONTACT_NUMBERS_FIELD +" FROM "
			+ DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
			+ DatabaseConstants.USER_NAME_FIELD + " IN " + tags;
	cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
	
	try {
		if (cursor != null) {
			while (cursor.moveToNext()){
				String tmpUserName = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NUMBERS_FIELD));
				ContentValues value = new ContentValues();
				if(tmpUserName!=null && !tmpUserName.equals("")){
					value.put(DatabaseConstants.CONTACT_NUMBERS_FIELD, tmpUserName);
					value.put(DatabaseConstants.CONTACT_NAMES_FIELD, tmpUserName);
					namesList.add(value);
				}
//					namesList.put(tmpUserName,cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD)));
			}
		}
	} catch (Exception e) {
	} finally {
		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
	}
	
	return namesList;
}
private String convertStringArrayToString1(List<String> strList) {
//	String[] strs = strList.toArray(new String[strList.size()]);
//	Gson gson = new Gson();
	String str = strList.toString().replace(" ", "");//gson.toJson(strs);
	str = str.replace('[', '(');
	str = str.replace(']', ')');
	return str;
}

	public String getChatName(String userName) {
		 if (userName!=null && !userName.contains("#786#")){
				try{
				String tmpNumber = userName;//formatNumber(userName.replaceFirst("m", "+"));
				if(tmpNumber.contains("_") )
					tmpNumber= formatNumber(tmpNumber.substring(0,tmpNumber.indexOf("_")));
				int contactId = getContactIDFromData(
						DatabaseConstants.CONTACT_NUMBERS_FIELD + " = ?",
						new String[] { tmpNumber });
				if(contactId!=-1){
					ContentValues contentvalues = new ContentValues();
		//			contentvalues.put(
		//					DatabaseConstants.USER_SIP_ADDRESS,
		//					userDetail.iSipAddress);
					contentvalues.put(
							DatabaseConstants.USER_NAME_FIELD,userName);
					contentvalues.put(
							DatabaseConstants.VOPIUM_FIELD,Integer.valueOf(1));
					
					contentvalues.put(DatabaseConstants.CONTACT_NUMBERS_FIELD,tmpNumber);
					updateAtMeDirectStatus(contentvalues,DatabaseConstants.CONTACT_NUMBERS_FIELD);
					updateAtMeContactDetails(contentvalues,tmpNumber);
					updateUserNameInContacts(userName,tmpNumber);
				}
				}catch(Exception e){
					
				}
			}
		String contactPerson = userName;
		String sg = SharedPrefManager.getInstance().getUserDomain();
		String sql1 = "SELECT "+DatabaseConstants.CONTACT_NAMES_FIELD+" FROM "+ DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS+" WHERE "+DatabaseConstants.USER_NAME_FIELD + "='" + userName + "' AND " + DatabaseConstants.USER_SG + "='"+sg+"'";// UNION"+
//				" SELECT "+DatabaseConstants.CONTACT_NAMES_FIELD+" FROM "+ DatabaseConstants.TABLE_NAME_CONTACT_EMAILS+" WHERE "+DatabaseConstants.USER_NAME_FIELD + "='" + userName + "'";
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql1, null);
		//		Cursor cursor = DBWrapper.getInstance().query(
//				DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
//				new String[] { DatabaseConstants.CONTACT_NAMES_FIELD },
//				DatabaseConstants.USER_NAME_FIELD + "='" + userName + "'",
//				null, null);
		try {
			if (cursor != null) {
				while (cursor.moveToNext()){
					contactPerson = cursor.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD))+"#786#"+userName;// Log.d(TAG,
				// TAG+"::"+cursor.getString(cursor.getColumnIndex("name"))+" + "+cursor.getString(cursor.getColumnIndex("DatabaseConstants.NAME_CONTACT_ID_FIELD")));
					break;
				}
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		if(contactPerson!=null && contactPerson.startsWith("#786#"))
			contactPerson = userName;
		
		if (userName.equals(contactPerson)) {
			String sql = "SELECT "+DatabaseConstants.CONTACT_NAMES_FIELD+" FROM "+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO
					+ " WHERE ("
					+ DatabaseConstants.CONTACT_NAMES_FIELD + "!='" + userName
					+ "' AND ("+DatabaseConstants.FROM_USER_FIELD + "='" + userName+ "' OR "+DatabaseConstants.TO_USER_FIELD + "='" + userName+"'))";
			cursor = ChatDBWrapper.getInstance(SuperChatApplication.context).executeRawQuery(sql);
			try {
				if (cursor != null) {
					if(cursor.moveToLast()){
						contactPerson = cursor
						.getString(cursor.getColumnIndex(DatabaseConstants.CONTACT_NAMES_FIELD))+"#786#"+userName;// Log.d(TAG,
					// TAG+"::"+cursor.getString(cursor.getColumnIndex("name"))+" + "+cursor.getString(cursor.getColumnIndex("DatabaseConstants.NAME_CONTACT_ID_FIELD")));
						}
				}
			} catch (Exception e) {
			} finally {
				if (cursor != null) {
					cursor.close();
					cursor = null;
				}
			}
		}
		if(SharedPrefManager.getInstance().isBroadCast(userName)){
			contactPerson = SharedPrefManager.getInstance().getBroadCastDisplayName(userName);
		}
		if(SharedPrefManager.getInstance().isGroupChat(userName)){
			contactPerson = SharedPrefManager.getInstance().getGroupDisplayName(userName)+"#786#"+userName;
		}
		if (!userName.equals(contactPerson)) {
			updateContactName(contactPerson, userName);
		}
		return contactPerson;
	}
	public static String formatNumber(String str){
		try{
			if(str==null)
				return null;
		
		String replacingCode = null;
		if(str.length()>5)
			for(int i = 5;i>=1;i--){
				replacingCode = str.substring(0, i);
			if(SuperChatApplication.countrySet.contains(replacingCode)){
				str = replacingCode+"-"+str.replaceFirst(replacingCode, "");
				break;
			}}
		
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	public void updateContactName(String contactName, String userName) {
//		Cursor cursor = null;
		try {
//			String sql = "UPDATE " + DatabaseConstants.TABLE_NAME_MESSAGE_INFO
//					+ " SET " + DatabaseConstants.CONTACT_NAMES_FIELD + "='"
//					+ contactName + "' WHERE "
//					+ DatabaseConstants.CONTACT_NAMES_FIELD + "='" + userName
//					+ "'";
			ChatDBWrapper.getInstance(SuperChatApplication.context).updateContactName(contactName, userName);
//			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//			if (cursor != null) {
//				Log.d("DBWrapper",
//						"updateUserNameInContacts count " + cursor.getCount());
//
//			}
		} catch (Exception e) {
			Log.e("DBWrapper", "Exception in updateUserNameInContacts method "
					+ e.toString());
		}
//		finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
	}
	public void updateUserDisplayName(String contactName, String userName) {
          ContentValues values = new ContentValues();
		String sg = SharedPrefManager.getInstance().getUserDomain();
          //update in Contact DB
          values.put(DatabaseConstants.CONTACT_NAMES_FIELD, contactName);
          // updating row
          int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, values, DatabaseConstants.USER_NAME_FIELD + " = ? AND " + DatabaseConstants.USER_SG + "='"+sg+"'",
        		  new String[] { userName });
          if(row > 0)
        	  Log.d("DBWrapper", "updateUserNameInContacts count " + row);
          
          ChatDBWrapper.getInstance(SuperChatApplication.context).updateUserDisplayName(contactName, userName);
	}
	
	public String getDisplayName(String userName) {
		String contactPerson = "User-Name";
		String sg = SharedPrefManager.getInstance().getUserDomain();
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
				new String[] { DatabaseConstants.CONTACT_NAMES_FIELD },
				DatabaseConstants.USER_NAME_FIELD + "='" + userName+"' AND " + DatabaseConstants.USER_SG + "='"+sg+"'", null,
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
			String sql = "UPDATE " + DatabaseConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + DatabaseConstants.MESSAGE_MEDIA_URL_FIELD + "='"+ messageDataPath +"' , "+ DatabaseConstants.MEDIA_STATUS + "='"+ DatabaseConstants.MEDIA_LOADED + "' WHERE "
					+ DatabaseConstants.MESSAGE_ID + "='" + messageID
					+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("DBWrapper",
						"updateMessageData count " + cursor.getCount());
			}
		} catch (Exception e) {
			Log.e("DBWrapper", "Exception in updateMessageData method "
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
			String sql = "UPDATE " + DatabaseConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + DatabaseConstants.MEDIA_STATUS + "='"+ DatabaseConstants.MEDIA_LOADING + "' WHERE "
					+ DatabaseConstants.MESSAGE_ID + "='" + messageID
					+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("DBWrapper",
						"updateMessageData count " + cursor.getCount());
			}
		} catch (Exception e) {
			Log.e("DBWrapper", "Exception in updateMessageData method "
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
			String sql = "UPDATE " + DatabaseConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + DatabaseConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD + "='"+ messageDataPath +"' , "+ DatabaseConstants.MEDIA_STATUS + "='"+ DatabaseConstants.MEDIA_READY_TO_LOAD + "' WHERE "
					+ DatabaseConstants.MESSAGE_ID + "='" + messageID
					+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("DBWrapper",
						"updateMessageData count " + cursor.getCount());
			}
		} catch (Exception e) {
			Log.e("DBWrapper", "Exception in updateMessageData method "
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
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_CONTACT_NAMES,
				new String[] { DatabaseConstants.USER_NAME_FIELD },
				DatabaseConstants.NAME_CONTACT_ID_FIELD + "=" + userId, null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				contactPerson = cursor.getString(cursor
						.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
		}
		if (cursor != null)
			cursor.close();
		return contactPerson;
	}

	public int getContactIDFromData(String s, String as[]) {
		int i = -1;
		Cursor cursor = dbHelper.getWritableDatabase().query(
				DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
				new String[] { DatabaseConstants.NAME_CONTACT_ID_FIELD }, s,
				as, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			i = cursor.getInt(cursor
					.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
		}
		cursor.close();
		return i;
	}

	public int getContactIDFromEmailData(String s, String as[]) {
		int i = -1;
		Cursor cursor = dbHelper.getWritableDatabase().query(
				DatabaseConstants.TABLE_NAME_CONTACT_EMAILS,
				new String[] { DatabaseConstants.NAME_CONTACT_ID_FIELD }, s,
				as, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			i = cursor.getInt(cursor
					.getColumnIndex(DatabaseConstants.NAME_CONTACT_ID_FIELD));
		}
		cursor.close();
		return i;
	}

	public void updateAtMeContactStatus(String contactNumber) {
		DBWrapper dbwrapper = DBWrapper.getInstance();
		dbwrapper.beginTransaction();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(DatabaseConstants.VOPIUM_FIELD, 1);
		dbwrapper.updateInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
				contentvalues,
				DatabaseConstants.CONTACT_NUMBERS_FIELD + " = ?",
				new String[] { contactNumber });
		dbwrapper.setTransaction();
		dbwrapper.endTransaction();
	}
	public void updateUserAccess(String username) {
		DBWrapper dbwrapper = DBWrapper.getInstance();
		dbwrapper.beginTransaction();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(DatabaseConstants.VOPIUM_FIELD, 1);
		dbwrapper.updateInDB(DatabaseConstants.TABLE_ALL_CONTACT_NUMBERS,
				contentvalues,
				DatabaseConstants.USER_NAME_FIELD + " = ?",
				new String[] { username });
		dbwrapper.setTransaction();
		dbwrapper.endTransaction();
	}
	public void updateUserDetails(String contactNumber, UserResponseDetail userDetail) {
		DBWrapper dbwrapper = DBWrapper.getInstance();
		dbwrapper.beginTransaction();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(DatabaseConstants.VOPIUM_FIELD, 1);
		//Add Address Details
		if(userDetail.flatNumber != null)
			contentvalues.put(DatabaseConstants.FLAT_NUMBER, userDetail.flatNumber);
		if(userDetail.buildingNumber != null)
			contentvalues.put(DatabaseConstants.BUILDING_NUMBER, userDetail.buildingNumber);
		if(userDetail.address != null)
			contentvalues.put(DatabaseConstants.ADDRESS, userDetail.address);
		if(userDetail.residenceType != null)
			contentvalues.put(DatabaseConstants.RESIDENCE_TYPE, userDetail.residenceType);
		dbwrapper.updateInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
				contentvalues,
				DatabaseConstants.CONTACT_NUMBERS_FIELD + " = ?",
				new String[] { contactNumber });
		dbwrapper.setTransaction();
		dbwrapper.endTransaction();
	}

//	public Cursor getRecentChatList() {
//		String sql = "SELECT " + "DISTINCT("
//				+ DatabaseConstants.CONTACT_NAMES_FIELD + "), "
//				+ DatabaseConstants._ID + ", "
//				+ DatabaseConstants.FROM_USER_FIELD + ", "
//				+ DatabaseConstants.TO_USER_FIELD + ", "
//				+ DatabaseConstants.MESSAGEINFO_FIELD + ","
//				+ DatabaseConstants.MESSAGE_TYPE_FIELD + ","
//				+ DatabaseConstants.UNREAD_COUNT_FIELD + ","
//				+ DatabaseConstants.LAST_UPDATE_FIELD + ", MAX("
//				+ DatabaseConstants.LAST_UPDATE_FIELD + ") FROM "
//				+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO + " GROUP BY "
//				+ DatabaseConstants.CONTACT_NAMES_FIELD + " ORDER BY "
//				+ DatabaseConstants.LAST_UPDATE_FIELD + " DESC";
//		Log.d("DBWrapper", "getRecentChatList query: " + sql);
//		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//		return cursor;
//	}
	public Cursor getRecentChatList() {
		String sql = "SELECT " + "DISTINCT("
				+ DatabaseConstants.CONTACT_NAMES_FIELD + "), "
				+ DatabaseConstants._ID + ", "
				+ DatabaseConstants.FROM_USER_FIELD + ", "
				+ DatabaseConstants.TO_USER_FIELD + ", "
				+ DatabaseConstants.MESSAGEINFO_FIELD + ","
				+ DatabaseConstants.MESSAGE_TYPE_FIELD + ","
				+ DatabaseConstants.MESSAGE_MEDIA_LENGTH + ","
				+ DatabaseConstants.UNREAD_COUNT_FIELD + ","
				+ DatabaseConstants.LAST_UPDATE_FIELD + ", MAX("
				+ DatabaseConstants.LAST_UPDATE_FIELD + ") FROM "
				+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO + " GROUP BY "
				+ DatabaseConstants.CONTACT_NAMES_FIELD + " ORDER BY "
				+ DatabaseConstants.LAST_UPDATE_FIELD + " DESC";
		Log.d("DBWrapper", "getRecentChatList query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		return cursor;
	}
	public void deleteRecentUserChat(String userName) {

		dbHelper.getWritableDatabase().delete(
				DatabaseConstants.TABLE_NAME_MESSAGE_INFO,
				DatabaseConstants.CONTACT_NAMES_FIELD + "='" + userName + "'",
				null);
	}

	public boolean deleteSelectedChatIteams(List<String> msgArray) {
		boolean isDeleted = false;
		Cursor cursor = null;
		// dbHelper.getWritableDatabase().delete(DatabaseConstants.TABLE_NAME_MESSAGE_INFO,
		// DatabaseConstants.MESSAGE_ID + "='" + tagId+"'", null);
		try {
			String tags = convertStringArrayToString(msgArray);

			String sql = "DELETE FROM "
					+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
					+ DatabaseConstants.MESSAGE_ID + " IN " + tags;
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst()) {
				isDeleted = true;
			}
		} catch (Exception e) {
			Log.d("DBWrapper", "Exception in deleteSelectedChatIteams method "
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
		Log.d("DBWrapper", "updateUserNameInContacts " + userName + " "
				+ contactNumber);
		Cursor cursor = null;
		try {
			String sql = "UPDATE "
					+ DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " SET "
					+ DatabaseConstants.USER_NAME_FIELD + "='" + userName
					+ "' WHERE " + DatabaseConstants.CONTACT_NUMBERS_FIELD
					+ "='" + contactNumber + "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("DBWrapper",
						"updateUserNameInContacts count " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("DBWrapper", "Exception in updateUserNameInContacts method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateCompositeContacts(String fieldId, String compositeNumbers) {
		Log.d("DBWrapper", "updateCompositeContacts " + fieldId + " "
				+ compositeNumbers);
		Cursor cursor = null;
		try {
			String sql = "UPDATE "
					+ DatabaseConstants.TABLE_NAME_CONTACT_NAMES + " SET "
					+ DatabaseConstants.CONTACT_COMPOSITE_FIELD + "='" + compositeNumbers
					+ "' WHERE " + DatabaseConstants.NAME_CONTACT_ID_FIELD
					+ "='" + fieldId + "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("DBWrapper",
						"updateUserNameInContacts count " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("DBWrapper", "Exception in updateUserNameInContacts method "
					+ e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
	public void updateSeenStatus(String userName, String idArrays,
			Message.SeenState state) {
		Log.d("DBWrapper", "updateSeenStatus idArrays " + idArrays);
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + DatabaseConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + DatabaseConstants.SEEN_FIELD + "='"
					+ state.ordinal() + "' WHERE "
					+ DatabaseConstants.MESSAGE_ID + " IN " + idArrays
					+ " AND " + DatabaseConstants.SEEN_FIELD + " != '"
					+ Message.SeenState.seen + "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("DBWrapper",
						"updateSeenStatus idArrays " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("DBWrapper",
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
		Log.d("DBWrapper", "updateSeenStatus idArrays " + idArrays);
		Cursor cursor = null;
		try {
			String sql = "UPDATE " + DatabaseConstants.TABLE_NAME_MESSAGE_INFO
					+ " SET " + DatabaseConstants.SEEN_FIELD + "='"
					+ state.ordinal() + "' WHERE "
					+ DatabaseConstants.FOREIGN_MESSAGE_ID_FIELD + " IN "
					+ idArrays + " AND " + DatabaseConstants.SEEN_FIELD + "!='"
					+ Message.SeenState.seen.ordinal() + "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null) {
				Log.d("DBWrapper",
						"updateSeenStatus idArrays " + cursor.getCount());

			}
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
	}
//public Cursor getAtmeContacts(ArrayList<String> previousUsers){
//	Cursor cursor = null;
//	String tags = "";
//	String sql = "";
//	try {
//		if(previousUsers!=null && !previousUsers.isEmpty()){
//	       tags = convertStringArrayToString(previousUsers);
//	 sql = "SELECT * " + " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
//			+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+ DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
//					+ DatabaseConstants.CONTACT_NAMES_FIELD
//					+ " COLLATE NOCASE";
//		}else
//			 sql = "SELECT * " + " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
//				+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
//						+ DatabaseConstants.CONTACT_NAMES_FIELD
//						+ " COLLATE NOCASE";
//	cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//	} catch (Exception e) {
//		Log.e("DBWrapper",
//				"Exception in updateSeenStatus method " + e.toString());
//	}
//	return cursor;
//}
	public Cursor getModifyContacts(ArrayList<String> previousUsers){
		Cursor cursor = null;
		String tags = "";
		String sql = "";
		try {
			String colmsOfContactNumbers = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.CONTACT_NUMBERS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			String colmsOfContactEmails = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.PHONE_EMAILS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			if(previousUsers!=null && !previousUsers.isEmpty()){
		       tags = convertStringArrayToString(previousUsers);
		 sql = "SELECT "+colmsOfContactNumbers + " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
				+ " WHERE (" +DatabaseConstants.VOPIUM_FIELD + "=1 OR "+DatabaseConstants.VOPIUM_FIELD + "=2) AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " + DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+
				 "SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
				+ " WHERE (" +DatabaseConstants.VOPIUM_FIELD + "=1 OR "+DatabaseConstants.VOPIUM_FIELD + "=2) AND "+ DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.VOPIUM_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}else{
				 sql = "SELECT " +colmsOfContactNumbers+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
					+ " WHERE (" +DatabaseConstants.VOPIUM_FIELD + "=1 OR "+DatabaseConstants.VOPIUM_FIELD + "=2) AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " +DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+"SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
					+ " WHERE (" +DatabaseConstants.VOPIUM_FIELD + "=1 OR "+DatabaseConstants.VOPIUM_FIELD + "=2) AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.VOPIUM_FIELD + " DESC, "
							+ DatabaseConstants.CONTACT_NAMES_FIELD
							+ " COLLATE NOCASE";
				 }
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		}
		return cursor;
	}

	public Cursor getEsiaContacts(ArrayList<String> previousUsers){
		Cursor cursor = null;
		String tags = "";
		String sql = "";
		try {
			String colmsOfContactNumbers = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.CONTACT_NUMBERS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD+", "+DatabaseConstants.CONTACT_TYPE_FIELD;
			String colmsOfContactEmails = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.PHONE_EMAILS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			if(previousUsers!=null && !previousUsers.isEmpty()){
				tags = convertStringArrayToString(previousUsers);
				sql = "SELECT "+colmsOfContactNumbers + " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+ DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+
						"SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+ DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}else{
				sql = "SELECT " +colmsOfContactNumbers+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+"SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		}
		return cursor;
	}

	/**
	 * Munish Code
	 * @param previousUsers
	 * @return
     */
	public Cursor getEsiaContactsNew(ArrayList<String> previousUsers){
		Cursor cursor = null;
		String tags = "";
		String sql = "";
		try {
			String colmsOfContactNumbers = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.CONTACT_NUMBERS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD+", "+DatabaseConstants.CONTACT_TYPE_FIELD;
			if(previousUsers!=null && !previousUsers.isEmpty()){
				tags = convertStringArrayToString(previousUsers);
				sql = "SELECT "+colmsOfContactNumbers + " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+ DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}else{
				sql = "SELECT " +colmsOfContactNumbers+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		}
		return cursor;
	}

	public ArrayList<String> getAllFilteredUsersForSG(ArrayList<String> previousUsers){
		Cursor cursor = null;
		String tags = "";
		String sql = "";
		if(previousUsers == null)
			previousUsers = new ArrayList<String>();
		String sg = SharedPrefManager.getInstance().getUserDomain();
		try {
			String colmsOfContactNumbers = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.CONTACT_NUMBERS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			String colmsOfContactEmails = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.PHONE_EMAILS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			if(previousUsers!=null && !previousUsers.isEmpty()){
		       tags = convertStringArrayToString(previousUsers);
		 sql = "SELECT "+colmsOfContactNumbers + " FROM " + DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS
				+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " + DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+
				 "SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
				+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+ DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}else{
				 sql = "SELECT " +colmsOfContactNumbers+ " FROM " + DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS
					+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " +DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+"SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
					+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
							+ DatabaseConstants.CONTACT_NAMES_FIELD
							+ " COLLATE NOCASE";
				 }
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		previousUsers.clear();
		if (cursor != null && cursor.moveToFirst()) {
			do {
				String user = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
				if(user!=null && SharedPrefManager.getInstance().isUserExistence(user))
					previousUsers.add(user);
			} while (cursor.moveToNext());
		}
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		}
		return previousUsers;
	}
	public Cursor getCursorForFilteredUsersForSG(ArrayList<String> previousUsers){
		Cursor cursor = null;
		String tags = "";
		String sql = "";
		if(previousUsers == null)
			previousUsers = new ArrayList<String>();
		try {
			String colmsOfContactNumbers = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.CONTACT_NUMBERS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD+", "+DatabaseConstants.CONTACT_TYPE_FIELD;
			String colmsOfContactEmails = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.PHONE_EMAILS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			if(previousUsers!=null && !previousUsers.isEmpty()){
				tags = convertStringArrayToString(previousUsers);
				sql = "SELECT "+colmsOfContactNumbers + " FROM " + DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " + DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+
						"SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+ DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}else{
				sql = "SELECT " +colmsOfContactNumbers+ " FROM " + DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " +DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+"SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		}
		return cursor;
	}

	/**
	 * Munish Code
	 * @param previousUsers
	 * @return
     */
	public Cursor getCursorForFilteredUsersForSGNew(ArrayList<String> previousUsers){
		Cursor cursor = null;
		String tags = "";
		String sql = "";
		if(previousUsers == null)
			previousUsers = new ArrayList<String>();
		try {
			String colmsOfContactNumbers = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.CONTACT_NUMBERS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD+", "+DatabaseConstants.CONTACT_TYPE_FIELD;
			if(previousUsers!=null && !previousUsers.isEmpty()){
				tags = convertStringArrayToString(previousUsers);
				sql = "SELECT "+colmsOfContactNumbers + " FROM " + DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " + DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}else{
				sql = "SELECT " +colmsOfContactNumbers+ " FROM " + DatabaseConstants.TABLE_NAME_ALL_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " +DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		}
		return cursor;
	}

	public ArrayList<String> getAllUsers(ArrayList<String> previousUsers){
		Cursor cursor = null;
		String tags = "";
		String sql = "";
		String sg = SharedPrefManager.getInstance().getUserDomain();
		if(previousUsers == null)
			previousUsers = new ArrayList<String>();
		try {
			String colmsOfContactNumbers = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.CONTACT_NUMBERS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
//			String colmsOfContactEmails = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.PHONE_EMAILS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			if(previousUsers!=null && !previousUsers.isEmpty()){
				tags = convertStringArrayToString(previousUsers);
				sql = "SELECT "+colmsOfContactNumbers + " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.USER_SG + "='" + sg + "' AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " + DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()
//						+"' UNION "+ "SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
//						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+ DatabaseConstants.USER_NAME_FIELD + " NOT IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()
						+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}else{
				sql = "SELECT " +colmsOfContactNumbers+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.USER_SG + "='" + sg + "' AND " + DatabaseConstants.CONTACT_COMPOSITE_FIELD + "!='9999999999'" + " AND " +DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()
//						+"' UNION "+"SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
//						+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()
						+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			previousUsers.clear();
			if (cursor != null && cursor.moveToFirst()) {
				do {
					String user = cursor.getString(cursor.getColumnIndex(DatabaseConstants.USER_NAME_FIELD));
					if(user!=null && SharedPrefManager.getInstance().isUserExistence(user))
						previousUsers.add(user);
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		}
		return previousUsers;
	}
	public Cursor getEsiaSelectedContacts(ArrayList<String> previousUsers){
		Cursor cursor = null;
		String tags = "";
		String sql = "";
		String sg = SharedPrefManager.getInstance().getUserDomain();
		try {
			String colmsOfContactNumbers = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.CONTACT_NUMBERS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			String colmsOfContactEmails = DatabaseConstants._ID+", "+DatabaseConstants.NAME_CONTACT_ID_FIELD+", "+DatabaseConstants.PHONE_EMAILS_FIELD+", "+DatabaseConstants.CONTACT_NAMES_FIELD+", "+DatabaseConstants.CONTACT_COMPOSITE_FIELD+", "+DatabaseConstants.VOPIUM_FIELD+", "+DatabaseConstants.USER_NAME_FIELD+", "+DatabaseConstants.IS_FAVOURITE_FIELD;
			if(previousUsers!=null && !previousUsers.isEmpty()){
		       tags = convertStringArrayToString(previousUsers);
		 sql = "SELECT "+colmsOfContactNumbers + " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
				+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND " + DatabaseConstants.USER_SG + "='" + sg + "' " + DatabaseConstants.USER_NAME_FIELD + " IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+
				 "SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
				+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+ DatabaseConstants.USER_NAME_FIELD + " IN " + tags+" AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
						+ DatabaseConstants.CONTACT_NAMES_FIELD
						+ " COLLATE NOCASE";
			}
//			else{
//				 sql = "SELECT " +colmsOfContactNumbers+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS
//					+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' UNION "+"SELECT " +colmsOfContactEmails+ " FROM " + DatabaseConstants.TABLE_NAME_CONTACT_EMAILS
//					+ " WHERE " +DatabaseConstants.VOPIUM_FIELD + "=1 AND "+DatabaseConstants.USER_NAME_FIELD+"!='"+SharedPrefManager.getInstance().getUserName()+"' ORDER BY "+DatabaseConstants.IS_FAVOURITE_FIELD + " DESC, "
//							+ DatabaseConstants.CONTACT_NAMES_FIELD
//							+ " COLLATE NOCASE";
//				 }
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in updateSeenStatus method " + e.toString());
		}
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
			String sql = "SELECT " + DatabaseConstants.MESSAGEINFO_FIELD
					+ " FROM " + DatabaseConstants.TABLE_NAME_MESSAGE_INFO
					+ " WHERE " + DatabaseConstants.MESSAGE_ID + " IN " + tags;
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);

			if (cursor != null && cursor.moveToFirst()) {
				do {
					sb.append(cursor.getString(cursor
							.getColumnIndex(DatabaseConstants.MESSAGEINFO_FIELD))
							+ " ");
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.d("DBWrapper", "Exception in deleteSelectedChatIteams method "
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
			String sql = "SELECT " + DatabaseConstants.FOREIGN_MESSAGE_ID_FIELD
					+ " FROM " + DatabaseConstants.TABLE_NAME_MESSAGE_INFO
					+ " WHERE (" + DatabaseConstants.SEEN_FIELD + " = '"
					+ Message.SeenState.recieved.ordinal() + "' OR "
					+ DatabaseConstants.SEEN_FIELD + " = '"
					+ Message.SeenState.sent.ordinal() + "') AND "
					+ DatabaseConstants.FROM_USER_FIELD + " = '" + userName
					+ "'";
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null)
				Log.d("DBWrapper",
						"Total row selected in getRecievedMessages: "
								+ cursor.getCount());
			if (cursor != null && cursor.moveToFirst()) {
				do {
					Log.d("DBWrapper",
							"id row selected: "
									+ cursor.getString(cursor
											.getColumnIndex(DatabaseConstants.FOREIGN_MESSAGE_ID_FIELD)));

					list.add(cursor.getString(cursor
							.getColumnIndex(DatabaseConstants.FOREIGN_MESSAGE_ID_FIELD)));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in getRecievedMessages method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	public boolean isFirstChat(String person) {
		boolean ret = false;
		String sql = "SELECT * FROM "
				+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
				+ DatabaseConstants.TO_USER_FIELD + " = '" + person + "' OR "
				+ DatabaseConstants.FROM_USER_FIELD + " = '" + person + "'";
		Cursor cursor = null;
		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor == null || cursor.getCount() <= 0)
				ret = true;
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in getRecievedMessages method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return ret;
	}
	public boolean isContactAvailable(String rawId){
		if(rawId == null)
			return false;
		boolean ret = false;
		String sql = "SELECT "+DatabaseConstants.CONTACT_VERSION+" FROM "
				+ DatabaseConstants.TABLE_NAME_CONTACT_NAMES + " WHERE "
				+ DatabaseConstants.RAW_CONTACT_ID + " = '" + rawId + "'";
		Log.d("DBWrapper", "isContactModified method query: "+sql);
		Cursor cursor = null;
		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()){
					ret = true;
				}
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in isRawIdAvailabled method " + e.toString());
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return ret;
	}
public boolean isContactModified(String rawId, int version){
	if(rawId == null)
		return false;
	boolean ret = false;
	String sql = "SELECT "+DatabaseConstants.CONTACT_VERSION+" FROM "
			+ DatabaseConstants.TABLE_NAME_CONTACT_NAMES + " WHERE "
			+ DatabaseConstants.RAW_CONTACT_ID + " = '" + rawId + "'";
	Log.d("DBWrapper", "isContactModified method query: "+sql);
	Cursor cursor = null;
	try {
		cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()){
			
			if(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.CONTACT_VERSION)) != version)
				ret = true;
			}
	} catch (Exception e) {
		Log.e("DBWrapper",
				"Exception in isRawIdAvailabled method " + e.toString());
		e.printStackTrace();
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
		
		String sql = "SELECT "+DatabaseConstants.LAST_UPDATE_FIELD+", MAX("+DatabaseConstants.LAST_UPDATE_FIELD+") FROM "
				+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
						+ DatabaseConstants.TO_USER_FIELD + " = '" + person + "' OR "
						+ DatabaseConstants.FROM_USER_FIELD + " = '" + person + "'";;
		Cursor cursor = null;
		try {
			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
			if (cursor != null)
//			Log.d("DBWrapper","lastMessageInDB "+cursor.getCount());
			if (cursor != null && cursor.moveToFirst()){
				ret = cursor.getLong(cursor.getColumnIndex(DatabaseConstants.LAST_UPDATE_FIELD));
				}
			Log.d("DBWrapper","lastMessageInDB in ret "+ret);
		} catch (Exception e) {
			Log.e("DBWrapper",
					"Exception in getRecievedMessages method " + e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}

		return ret;
	}

//	public ArrayList<HashMap<String, String>> getUndeliveredMessages(
//			String meUser) {
//		Cursor cursor = null;
//
//		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
//		try {
//			String sql = "SELECT " + DatabaseConstants.MESSAGE_ID + ", "
//					+ DatabaseConstants.TO_USER_FIELD + ", "
//					+ DatabaseConstants.MESSAGEINFO_FIELD + " FROM "
//					+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
//					+ DatabaseConstants.SEEN_FIELD + " = '"
//					+ Message.SeenState.wait.ordinal() + "' AND "
//					+ DatabaseConstants.FROM_USER_FIELD + " = '" + meUser + "'";
//			cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
//			if (cursor != null)
//				Log.d("DBWrapper",
//						"Total row selected in getRecievedMessages: "
//								+ cursor.getCount());
//			if (cursor != null && cursor.moveToFirst()) {
//				do {
//					HashMap<String, String> hashMap = new HashMap<String, String>();
//					hashMap.put(
//							DatabaseConstants.MESSAGE_ID,
//							cursor.getString(cursor
//									.getColumnIndex(DatabaseConstants.MESSAGE_ID)));
//					hashMap.put(
//							DatabaseConstants.TO_USER_FIELD,
//							cursor.getString(cursor
//									.getColumnIndex(DatabaseConstants.TO_USER_FIELD)));
//					hashMap.put(
//							DatabaseConstants.MESSAGEINFO_FIELD,
//							cursor.getString(cursor
//									.getColumnIndex(DatabaseConstants.MESSAGEINFO_FIELD)));
//					list.add(hashMap);
//				} while (cursor.moveToNext());
//			}
//		} catch (Exception e) {
//			Log.e("DBWrapper",
//					"Exception in getRecievedMessages method " + e.toString());
//		} finally {
//			if (cursor != null) {
//				cursor.close();
//				cursor = null;
//			}
//		}
//		return list;
//	}
	public boolean isNumberExists(String phoneNumber) {
		boolean isNumberExists = true;
		String sql = "SELECT * FROM "
				+ DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
				+ DatabaseConstants.CONTACT_NUMBERS_FIELD + "='" + phoneNumber+"'";
		
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if(cursor == null || cursor.getCount()==0)
			isNumberExists = false;
		Log.d("DBWrapper", "isNumberExists query: " + isNumberExists+" , "+sql);
		return isNumberExists;
	}
	public boolean isContactExists(String user_name) {
		boolean username = true;
		String sql = "SELECT * FROM "
				+ DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
				+ DatabaseConstants.USER_NAME_FIELD + "='" + user_name+"'";

		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if(cursor == null || cursor.getCount()==0)
			username = false;
		Log.d("DBWrapper", "isContactExists query: user_name = " +user_name + "=" + username+" , "+sql);
		return username;
	}
	public boolean deleteDuplicateRow(int contactRawId) {
		String sql = "DELETE FROM "
				+ DatabaseConstants.TABLE_NAME_CONTACT_NAMES + " WHERE "
				+ DatabaseConstants.RAW_CONTACT_ID + "='" + contactRawId+"'";
		Log.d("DBWrapper", "deleteDuplicateRow query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if(cursor == null || cursor.getCount()==0)
		return false;
		
		return true;
	}
	public boolean deleteDuplicateRow1(int contactId) {
		String sql = "DELETE FROM "
				+ DatabaseConstants.TABLE_NAME_CONTACT_NAMES + " WHERE "
				+ DatabaseConstants.NAME_CONTACT_ID_FIELD + "='" + contactId+"'";
		Log.d("DBWrapper", "deleteDuplicateRow query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if(cursor == null || cursor.getCount()==0)
		return false;
		
		return true;
	}
	public boolean deleteContact(String userName) {
		String sql = "DELETE FROM "
				+ DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
				+ DatabaseConstants.USER_NAME_FIELD + "='" + userName+"'";
		Log.d("DBWrapper", "deleteDuplicateRow query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if(cursor == null || cursor.getCount()==0)
		return false;
		
		return true;
	}
	public boolean deleteRow(String tableName, String contactRowId) {
		String sql = "DELETE FROM "
				+ tableName + " WHERE "
				+ DatabaseConstants.RAW_CONTACT_ID + "='" + contactRowId+"'";
		Log.d("DBWrapper", "deleteRow by contactRowId query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if(cursor == null || cursor.getCount()==0)
		return false;
		
		return true;
	}
	public boolean deleteSingleNumberRow(String tableName, int contactId) {
		String sql = "DELETE FROM "
				+ tableName + " WHERE "
				+ DatabaseConstants.CONTACT_ID_FIELD + "='" + contactId+"'";
		Log.d("DBWrapper", "deleteRow by contactRowId query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		if(cursor == null || cursor.getCount()==0)
		return false;
		
		return true;
	}
	public Cursor getUserChatList(String userName) {
		String sql = "SELECT " + DatabaseConstants._ID + ", "
				+ DatabaseConstants.CONTACT_NAMES_FIELD + ", "
				+ DatabaseConstants.SEEN_FIELD + ", "
				+ DatabaseConstants.MESSAGE_ID + ", "
				+ DatabaseConstants.IS_DATE_CHANGED_FIELD + ", "
				+ DatabaseConstants.FROM_GROUP_USER_FIELD + ", "
				+ DatabaseConstants.FROM_USER_FIELD + ", "
				+ DatabaseConstants.TO_USER_FIELD + ", "
				+ DatabaseConstants.MESSAGEINFO_FIELD + ","
				+ DatabaseConstants.MESSAGE_MEDIA_LOCAL_PATH_FIELD + ","
				+ DatabaseConstants.MESSAGE_THUMB_FIELD + ","
				+ DatabaseConstants.MESSAGE_TYPE_FIELD + ","
				+ DatabaseConstants.MESSAGE_MEDIA_URL_FIELD + ","
				+ DatabaseConstants.MESSAGE_MEDIA_LENGTH + ","
				+ DatabaseConstants.UNREAD_COUNT_FIELD + ","
				+ DatabaseConstants.LAST_UPDATE_FIELD + " FROM "
				+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
				+ DatabaseConstants.FROM_USER_FIELD + "='" + userName + "' OR "
				+ DatabaseConstants.TO_USER_FIELD + "='" + userName
				+ "' ORDER BY " + DatabaseConstants.LAST_UPDATE_FIELD;
		Log.d("DBWrapper", "getRecentChatList query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		return cursor;
	}
	public ArrayList<String> getUsersOfGroup(String groupName){
		ArrayList<String> list = new ArrayList<String>();
		String sql = "SELECT DISTINCT(" + DatabaseConstants.FROM_GROUP_USER_FIELD +") FROM "
				+ DatabaseConstants.TABLE_NAME_MESSAGE_INFO + " WHERE "
				+ DatabaseConstants.FROM_USER_FIELD + "='" + groupName + "' OR "
				+ DatabaseConstants.TO_USER_FIELD + "='" + groupName+"'";
		Log.d("DBWrapper", "getRecentChatList query: " + sql);
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery(sql, null);
		try{
			if (cursor != null && cursor.moveToFirst()) {
				do {
					String user = cursor.getString(cursor.getColumnIndex(DatabaseConstants.FROM_GROUP_USER_FIELD));
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
		String contactNumber = String.valueOf(contentvalues.get(type));
		Log.d("DBWrapper", "updateAtMeDirect sip address: " + contactNumber);
		if (contactNumber != null) {
			int contactId = -1;
			if (contactNumber.contains("@")) {
				contactId = getContactIDFromEmailData(
						DatabaseConstants.PHONE_EMAILS_FIELD + " = ?",
						new String[] { contactNumber });

			} else {
				contactId = getContactIDFromData(
						DatabaseConstants.CONTACT_NUMBERS_FIELD + " = ?",
						new String[] { contactNumber });
			}
			if (contactId == -1)
				return;
			DBWrapper dbwrapper = DBWrapper.getInstance();
			dbwrapper.beginTransaction();
			contentvalues.remove(type);
			dbwrapper.updateInDB(DatabaseConstants.TABLE_NAME_CONTACT_NAMES,
					contentvalues, DatabaseConstants.NAME_CONTACT_ID_FIELD
					+ " = ?",
					new String[] { String.valueOf(contactId) });
			dbwrapper.setTransaction();
			dbwrapper.endTransaction();
		}
	}
	public void updateAtMeContactDetails(ContentValues contentvalues, String contactNumber) {
		Log.d("DBWrapper", "updateAtMeContactDetails address: " + contactNumber);
		if (contactNumber != null) {
			DBWrapper dbwrapper = DBWrapper.getInstance();
			dbwrapper.beginTransaction();
			if (contactNumber.contains("@")){
				dbwrapper.updateInDB(DatabaseConstants.TABLE_NAME_CONTACT_EMAILS,
						contentvalues, DatabaseConstants.PHONE_EMAILS_FIELD
						+ " = ?",
						new String[] {contactNumber});
			}else{
				dbwrapper.updateInDB(DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS,
						contentvalues, DatabaseConstants.CONTACT_NUMBERS_FIELD
						+ " = ?",
						new String[] {contactNumber});
			}
			dbwrapper.setTransaction();
			dbwrapper.endTransaction();
		}
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
		SQLiteDatabase sqlitedatabase = dbHelper.getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put(DatabaseConstants.VOPIUM_FIELD, Integer.valueOf(0));
		contentvalues.put(DatabaseConstants.IS_FAVOURITE_FIELD,
				Integer.valueOf(0));
		int i = sqlitedatabase.update(
				DatabaseConstants.TABLE_NAME_CONTACT_NAMES, contentvalues,
				null, null);
		Log.i(TAG,
				(new StringBuilder())
				.append("Initialized contact_names rows: ").append(i)
				.toString());
		ContentValues contentvalues1 = new ContentValues();
		// contentvalues1.put("data11", Integer.valueOf(0));
		contentvalues1.put(DatabaseConstants.STATE_FIELD, Integer.valueOf(0));
		int j = sqlitedatabase.update(
				DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, contentvalues1,
				null, null);
		Log.i(TAG,
				(new StringBuilder())
				.append("Initialized contact_numbers rows: ").append(j)
				.toString());
	}

	public long insertInDB(String s, ContentValues contentvalues) {
		return dbHelper.getWritableDatabase().insert(s, null, contentvalues);
	}
	public long insertOrUpdateDB(String s, ContentValues contentvalues) {
//		if(isNew)
//			return dbHelper.getWritableDatabase().insert(s, null, contentvalues);
//		else
			return dbHelper.getWritableDatabase().insert(s, null, contentvalues);
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
//----------------------------------------------------------------------------

//	public Cursor getContactsCursor(String sg){
//		Cursor cursor = null;
//		try{
//			cursor = dbHelper.getWritableDatabase().rawQuery("SELECT * FROM " +DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
//                + DatabaseConstants.VOPIUM_FIELD + "!=2 AND "+ DatabaseConstants.USER_SG + "='"+ sg + "'", null);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		return cursor;
//	}

	public Cursor getContactsCursor(String sg){
		Cursor cursor = null;
		try{
			String query = "SELECT " + "DISTINCT("
					+ DatabaseConstants.USER_NAME_FIELD + "), "
					+ DatabaseConstants._ID + ", "
					+ DatabaseConstants.CONTACT_NUMBERS_FIELD + ", "
					+ DatabaseConstants.DISPLAY_NUMBERS_FIELD + ", "
					+ DatabaseConstants.RAW_CONTACT_ID + ", "
					+ DatabaseConstants.CONTACT_NAMES_FIELD + ", "
					+ DatabaseConstants.CONTACT_TYPE_FIELD + ", "
					+ DatabaseConstants.CONTACT_COMPOSITE_FIELD + ", "
					+ DatabaseConstants.VOPIUM_FIELD + ", "
					+ DatabaseConstants.DATA_ID_FIELD + ", "
					+ DatabaseConstants.NAME_CONTACT_ID_FIELD + ", "
					+ DatabaseConstants.PHONE_NUMBER_TYPE_FIELD + ", "
					+ DatabaseConstants.IS_FAVOURITE_FIELD + ", "
					+ DatabaseConstants.FLAT_NUMBER + ", "
					+ DatabaseConstants.BUILDING_NUMBER + ", "
					+ DatabaseConstants.ADDRESS + ", "
					+ DatabaseConstants.RESIDENCE_TYPE + ", "
					+ DatabaseConstants.USER_ID + ", "
					+ DatabaseConstants.USER_SG + ", "
					+ DatabaseConstants.STATE_FIELD

					+ " FROM " +DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
					+ DatabaseConstants.VOPIUM_FIELD + "!=2 AND "+ DatabaseConstants.USER_SG + "='"+ sg + "' AND "
					+ DatabaseConstants.CONTACT_NAMES_FIELD + " LIKE '%"+ Constants.SHARED_ID_START_STRING + "%' ORDER BY "
					+ DatabaseConstants.CONTACT_NAMES_FIELD +" COLLATE NOCASE" +" ASC";

			cursor = dbHelper.getWritableDatabase().rawQuery(query, null);

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return cursor;
	}

	public Cursor getContactsCursorWithoutShared(String sg){
		Cursor cursor = null;
		try{
			String query = "SELECT " + "DISTINCT("
					+ DatabaseConstants.USER_NAME_FIELD + "), "
					+ DatabaseConstants._ID + ", "
					+ DatabaseConstants.CONTACT_NUMBERS_FIELD + ", "
					+ DatabaseConstants.DISPLAY_NUMBERS_FIELD + ", "
					+ DatabaseConstants.RAW_CONTACT_ID + ", "
					+ DatabaseConstants.CONTACT_NAMES_FIELD + ", "
					+ DatabaseConstants.CONTACT_TYPE_FIELD + ", "
					+ DatabaseConstants.CONTACT_COMPOSITE_FIELD + ", "
					+ DatabaseConstants.VOPIUM_FIELD + ", "
					+ DatabaseConstants.DATA_ID_FIELD + ", "
					+ DatabaseConstants.NAME_CONTACT_ID_FIELD + ", "
					+ DatabaseConstants.PHONE_NUMBER_TYPE_FIELD + ", "
					+ DatabaseConstants.IS_FAVOURITE_FIELD + ", "
					+ DatabaseConstants.FLAT_NUMBER + ", "
					+ DatabaseConstants.BUILDING_NUMBER + ", "
					+ DatabaseConstants.ADDRESS + ", "
					+ DatabaseConstants.RESIDENCE_TYPE + ", "
					+ DatabaseConstants.USER_ID + ", "
					+ DatabaseConstants.USER_SG + ", "
					+ DatabaseConstants.STATE_FIELD

					+ " FROM " +DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS + " WHERE "
					+ DatabaseConstants.VOPIUM_FIELD + "!=2 AND "+ DatabaseConstants.USER_SG + "='"+ sg + "' AND "
					+ DatabaseConstants.CONTACT_NAMES_FIELD + " NOT LIKE '%"+ Constants.SHARED_ID_START_STRING + "%' ORDER BY "
					+ DatabaseConstants.CONTACT_NAMES_FIELD +" COLLATE NOCASE" +" ASC";

			cursor = dbHelper.getWritableDatabase().rawQuery(query, null);

		}catch(Exception ex){
			ex.printStackTrace();
		}
		return cursor;
	}

	/**
	 * Inserts an entry in DB for Owned SG/Domain
	 * @param sg_data
     */
	public void updateOwnedSGData(OwnerDomainName sg_data){
		try{
			SharedPrefManager prefManager = SharedPrefManager.getInstance();
			//Update Shared Preferences for owned Domain
			prefManager.saveOwnedDomain(sg_data.getDomainName());
			//Save Last Online time
			prefManager.saveLastOnline(sg_data.getDomainName(), System.currentTimeMillis());

			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_NAME, sg_data.getDomainName());
			//contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDomainDisplayName());
			contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDisplayName());
			contentvalues.put(DatabaseConstants.DOMAIN_ADMIN_NAME, sg_data.getAdminName());
			contentvalues.put(DatabaseConstants.DOMAIN_ORG_NAME, sg_data.getOrgName());
			contentvalues.put(DatabaseConstants.DOMAIN_PRIVACY_TYPE, sg_data.getPrivacyType());

			contentvalues.put(DatabaseConstants.DOMAIN_TYPE, sg_data.getDomainType());
			contentvalues.put(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT, Integer.valueOf(sg_data.getUnreadCounter()));
			contentvalues.put(DatabaseConstants.DOMAIN_CREATED_DATE, sg_data.getCreatedDate());
			contentvalues.put(DatabaseConstants.DOMAIN_TYPE_VALUE, Integer.valueOf(1));
			contentvalues.put(DatabaseConstants.DOMAIN_ORG_URL, sg_data.getOrgUrl());
			contentvalues.put(DatabaseConstants.DOMAIN_LOGO_FILE_ID, sg_data.getLogoFileId());
			contentvalues.put(DatabaseConstants.DOMAIN_MUTE_INFO, Integer.valueOf(0));
			long row = DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues);
			if(row > 0)
				Log.i("DBWrapper", "updateOwnedSGData count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Updates below values for the sg_name in Database
	 * @param sg_name
	 * @param sg_username
	 * @param sg_password
	 * @param user_id
     * @param active
     */
	public void updateSGCredentials(String sg_name, String sg_username, String sg_password, long user_id, boolean active){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_USER_NAME, sg_username);
			contentvalues.put(DatabaseConstants.DOMAIN_USER_PASSWORD, sg_password);
			contentvalues.put(DatabaseConstants.DOMAIN_ACTIVATE_STATUS, active);
			contentvalues.put(DatabaseConstants.DOMAIN_USER_ID, user_id);
//			System.out.println("DBWrapper :: updateSGCredentials : "+contentvalues.toString());
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.i("DBWrapper", "updateSGCredentials count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Updates the SG type in database
	 * types are => 1 - Owned, 2 - Joined, 3 - Invited
	 * @param sg_name
	 * @param type
     */
	public void updateSGTypeValue(String sg_name, int type){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_TYPE_VALUE, Integer.valueOf(type));
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.e("DBWrapper", "updateSGInvitationJoined count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Updates Profile picture for the SG in Database
	 * @param sg_name
	 * @param fileID
     */
	public void updateSGLogoFileID(String sg_name, String fileID){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_LOGO_FILE_ID, fileID);
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.e("DBWrapper", "updateSGLogoFileID count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * Updates Display Name for the SG in Database
	 * @param sg_name
	 * @param displayname
     */
	public void updateSGDisplayName(String sg_name, String displayname){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, displayname);
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.e("DBWrapper", "updateSGLogoFileID count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Updates SG mute information in Database
	 * @param sg_name
	 * @param value
     */
	public void updateSGMuteInfo(String sg_name, int value){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_MUTE_INFO, Integer.valueOf(value));
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.i("DBWrapper", "updateSGMuteInfo count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Get Mute information for any SG
	 * @param sg
	 * @return 1 or 0 for Mute and unmute
     */
	public int getSGMuteInfo(String sg) {
		int mute = 0;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_MUTE_INFO },
				DatabaseConstants.DOMAIN_NAME + "='" + sg+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				mute = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_MUTE_INFO));
		}
		if (cursor != null)
			cursor.close();
		return mute;
	}

	/**
	 * Updates whether SG is active or not in Database
	 * @param sg_name
	 * @param value
     */
	public void updateSGActiveStatus(String sg_name, boolean value){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_ACTIVATE_STATUS, value);
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.e("DBWrapper", "updateSGActiveStatus count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Updates whether contacts for that SG is loaded or not
	 * @param sg_name
	 * @param value
     */
	public void updateSGContactsLoaded(String sg_name, String value){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_CONTACTS_LOADED, value);
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.e("DBWrapper", "updateSGContactsLoaded count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Checks if contacts are loaded for that SG or not
	 * @param sg
	 * @return
     */
	public boolean isSGContactsLoaded(String sg) {
		String loaded = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_CONTACTS_LOADED },
				DatabaseConstants.DOMAIN_NAME + "='" + sg+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				loaded = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_CONTACTS_LOADED));
		}
		if (cursor != null)
			cursor.close();
		if(loaded != null && loaded.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	/**
	 * Updates whether Groups and Broadcast has been loaded for that SG or not
	 * @param sg_name
	 * @param value
     */
	public void updateSGGroupsBroadcastLoaded(String sg_name, String value){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_GROUPS_LOADED, value);
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.e("DBWrapper", "updateSGGroupsBroadcastLoaded count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Checks if Groups and Broadcasts has been loaded or not
	 * @param sg
	 * @return
     */
	public boolean isSGGroupsBroadcastLoaded(String sg) {
		String loaded = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_GROUPS_LOADED},
				DatabaseConstants.DOMAIN_NAME + "='" + sg+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				loaded = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_GROUPS_LOADED));
		}
		if (cursor != null)
			cursor.close();
		if(loaded != null && loaded.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	/**
	 * Update the information that Official Id is loaded for that SG or not
	 * @param sg_name
	 * @param value
     */
	public void updateSGSharedIDLoaded(String sg_name, String value){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_OFFICIAL_ID_LOADED, value);
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.e("DBWrapper", "updateSGSharedIDLoaded count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Checks if Official ID has been loaded or not
	 * @param sg
	 * @return
     */
	public boolean isSGSharedIDLoaded(String sg) {
		String loaded = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_OFFICIAL_ID_LOADED},
				DatabaseConstants.DOMAIN_NAME + "='" + sg+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				loaded = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_OFFICIAL_ID_LOADED));
		}
		if (cursor != null)
			cursor.close();
		if(loaded != null && loaded.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	/**
	 * Updates whether Bulletin has been loaded for that SG or not
	 * @param sg_name
	 * @param value
     */
	public void updateSGBulletinLoaded(String sg_name, String value){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_BULLETIN_LOADED, value);
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.e("DBWrapper", "updateBulletinLoaded count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Checks if Bulletin has been loaded or not
	 * @param sg
	 * @return
     */
	public boolean isSGBulletinLoaded(String sg) {
		String loaded = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_BULLETIN_LOADED},
				DatabaseConstants.DOMAIN_NAME + "='" + sg+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				loaded = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_BULLETIN_LOADED));
		}
		if (cursor != null)
			cursor.close();
		if(loaded != null && loaded.equalsIgnoreCase("true"))
			return true;
		return false;
	}

	/**
	 * Checks if SG is active or not
	 * @param sg
	 * @return
     */
	public boolean isSGActive(String sg) {
		String active = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_ACTIVATE_STATUS },
				DatabaseConstants.DOMAIN_NAME + "='" + sg+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				active = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_ACTIVATE_STATUS));
		}
		if (cursor != null)
			cursor.close();
		if(active != null && (active.equalsIgnoreCase("true") || active.equalsIgnoreCase("1")))
			return true;
		return false;
	}
	/**
	 * Checks if SG Owner or not
	 * @param sg
	 * @return
     */
	public boolean isSGOwner(String sg) {
		int owner = 0;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_TYPE_VALUE },
				DatabaseConstants.DOMAIN_NAME + "='" + sg+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				owner = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_TYPE_VALUE));
		}
		if (cursor != null)
			cursor.close();
		if(owner == 1)
			return true;
		return false;
	}

	/**
	 * Updates Joined SG's in DB
	 * @param list
     */
	public void updateJoinedSGData(ArrayList<JoinedDomainNameSet> list){

		SharedPrefManager prefManager = SharedPrefManager.getInstance();

		try{
			for (JoinedDomainNameSet sg_data : list) {
				ContentValues contentvalues = new ContentValues();
				contentvalues.put(DatabaseConstants.DOMAIN_NAME, sg_data.getDomainName());
				//contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDomainDisplayName());
				contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDisplayName());
				contentvalues.put(DatabaseConstants.DOMAIN_ADMIN_NAME, sg_data.getAdminName());
				contentvalues.put(DatabaseConstants.DOMAIN_ORG_NAME, sg_data.getOrgName());
				contentvalues.put(DatabaseConstants.DOMAIN_PRIVACY_TYPE, sg_data.getPrivacyType());
				contentvalues.put(DatabaseConstants.DOMAIN_TYPE, sg_data.getDomainType());
				contentvalues.put(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT, Integer.valueOf(sg_data.getUnreadCounter()));
				contentvalues.put(DatabaseConstants.DOMAIN_CREATED_DATE, sg_data.getCreatedDate());
				contentvalues.put(DatabaseConstants.DOMAIN_TYPE_VALUE, Integer.valueOf(2));
				contentvalues.put(DatabaseConstants.DOMAIN_ORG_URL, sg_data.getOrgUrl());
				contentvalues.put(DatabaseConstants.DOMAIN_LOGO_FILE_ID, sg_data.getLogoFileId());
				contentvalues.put(DatabaseConstants.DOMAIN_MUTE_INFO, Integer.valueOf(0));
				//Save Last Online time
				prefManager.saveLastOnline(sg_data.getDomainName(), System.currentTimeMillis());
				long row = DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues);
				if(row > 0)
					Log.i("DBWrapper", "updateJoinedSGData count " + row);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void addNewJoinedSGData(JoinedDomainNameSet sg_data){

		SharedPrefManager prefManager = SharedPrefManager.getInstance();

		try{
				ContentValues contentvalues = new ContentValues();
				contentvalues.put(DatabaseConstants.DOMAIN_NAME, sg_data.getDomainName());
				//contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDomainDisplayName());
				contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDisplayName());
				contentvalues.put(DatabaseConstants.DOMAIN_ADMIN_NAME, sg_data.getAdminName());
				contentvalues.put(DatabaseConstants.DOMAIN_ORG_NAME, sg_data.getOrgName());
				contentvalues.put(DatabaseConstants.DOMAIN_PRIVACY_TYPE, sg_data.getPrivacyType());
				contentvalues.put(DatabaseConstants.DOMAIN_TYPE, sg_data.getDomainType());
				contentvalues.put(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT, Integer.valueOf(sg_data.getUnreadCounter()));
				contentvalues.put(DatabaseConstants.DOMAIN_CREATED_DATE, sg_data.getCreatedDate());
				contentvalues.put(DatabaseConstants.DOMAIN_TYPE_VALUE, Integer.valueOf(2));
				contentvalues.put(DatabaseConstants.DOMAIN_ORG_URL, sg_data.getOrgUrl());
				contentvalues.put(DatabaseConstants.DOMAIN_LOGO_FILE_ID, sg_data.getLogoFileId());
				contentvalues.put(DatabaseConstants.DOMAIN_MUTE_INFO, Integer.valueOf(0));
				//Save Last Online time
				prefManager.saveLastOnline(sg_data.getDomainName(), System.currentTimeMillis());
				long row = DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues);
				if(row > 0)
					Log.i("DBWrapper", "updateJoinedSGData count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Updates Invited SG's in DB
	 * @param list
     */
	public void updateInvitedSGData(ArrayList<InvitedDomainNameSet> list){
		try{
			for (InvitedDomainNameSet sg_data : list) {
				ContentValues contentvalues = new ContentValues();
				contentvalues.put(DatabaseConstants.DOMAIN_NAME, sg_data.getDomainName());
				//contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDomainDisplayName());
				contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDisplayName());
				contentvalues.put(DatabaseConstants.DOMAIN_ADMIN_NAME, sg_data.getAdminName());
				contentvalues.put(DatabaseConstants.DOMAIN_ORG_NAME, sg_data.getOrgName());
				contentvalues.put(DatabaseConstants.DOMAIN_PRIVACY_TYPE, sg_data.getPrivacyType());
				contentvalues.put(DatabaseConstants.DOMAIN_TYPE, sg_data.getDomainType());
				contentvalues.put(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT, Integer.valueOf(sg_data.getUnreadCounter()));
				contentvalues.put(DatabaseConstants.DOMAIN_CREATED_DATE, sg_data.getCreatedDate());
				contentvalues.put(DatabaseConstants.DOMAIN_TYPE_VALUE, Integer.valueOf(3));
				contentvalues.put(DatabaseConstants.DOMAIN_ORG_URL, sg_data.getOrgUrl());
				contentvalues.put(DatabaseConstants.DOMAIN_LOGO_FILE_ID, sg_data.getLogoFileId());
				contentvalues.put(DatabaseConstants.DOMAIN_MUTE_INFO, Integer.valueOf(0));
				long row = DBWrapper.getInstance().insertInDB(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues);
				if(row > 0)
					Log.i("DBWrapper", "updateInvitedSGData count " + row);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * Add and Update Invited SG's in DB
	 * @param list
	 */
	public void addAndUpdateInvitedSGData(ArrayList<InvitedDomainNameSet> list){
		try{
			for (InvitedDomainNameSet sg_data : list) {
				ContentValues contentvalues = new ContentValues();
				//Check if domain name is already added then skip.
				int type = getSGUserTypeValue(sg_data.getDomainName());
				if(type == -1) {
					contentvalues.put(DatabaseConstants.DOMAIN_NAME, sg_data.getDomainName());
					//contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDomainDisplayName());
					contentvalues.put(DatabaseConstants.DOMAIN_DISPLAY_NAME, sg_data.getDisplayName());
					contentvalues.put(DatabaseConstants.DOMAIN_ADMIN_NAME, sg_data.getAdminName());
					contentvalues.put(DatabaseConstants.DOMAIN_ORG_NAME, sg_data.getOrgName());
					contentvalues.put(DatabaseConstants.DOMAIN_PRIVACY_TYPE, sg_data.getPrivacyType());
					contentvalues.put(DatabaseConstants.DOMAIN_TYPE, sg_data.getDomainType());
					contentvalues.put(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT, Integer.valueOf(sg_data.getUnreadCounter()));
					contentvalues.put(DatabaseConstants.DOMAIN_CREATED_DATE, sg_data.getCreatedDate());
					contentvalues.put(DatabaseConstants.DOMAIN_TYPE_VALUE, Integer.valueOf(3));
					contentvalues.put(DatabaseConstants.DOMAIN_ORG_URL, sg_data.getOrgUrl());
					contentvalues.put(DatabaseConstants.DOMAIN_LOGO_FILE_ID, sg_data.getLogoFileId());
					contentvalues.put(DatabaseConstants.DOMAIN_MUTE_INFO, Integer.valueOf(0));
					long row = insertInDB(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues);
					if (row > 0)
						Log.i("DBWrapper", "updateInvitedSGData count " + row);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Returns SG Name
	 * @param username
	 * @return
     */
	public String getSGName(String username) {
		String sg_name = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_NAME },
				DatabaseConstants.DOMAIN_USER_NAME + "='" + username+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				sg_name = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_NAME));
		}
		if (cursor != null)
			cursor.close();
		return sg_name;
	}
	/**
	 * Returns new Message count for that SG
	 * @param sg
	 * @return
     */
	public int getNewMessageCountForSG(String sg) {
		int msg_count = 0;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT },
				DatabaseConstants.DOMAIN_NAME + "='" + sg+"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				msg_count = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT));
		}
		if (cursor != null)
			cursor.close();
		return msg_count;
	}

	/**
	 * Updates New Message Count for this SG
	 * @param sg_name
	 * @param value
	 */
	public void updateSGNewMessageCount(String sg_name, int value){
		try{
			ContentValues contentvalues = new ContentValues();
			contentvalues.put(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT, Integer.valueOf(value));
			int row = dbHelper.getWritableDatabase().update(DatabaseConstants.TABLE_NAME_MULTIPLE_SG, contentvalues, DatabaseConstants.DOMAIN_NAME + " = ?",
					new String[] { sg_name });
			if(row > 0)
				Log.i("DBWrapper", "updateSGNewMessageCount count " + row);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Returns username for that SG
 	 * @param sg
	 * @return
     */
	public String getSGUserName(String sg) {
		String sg_username = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_USER_NAME },
				DatabaseConstants.DOMAIN_NAME + "='" + sg +"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				sg_username = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_USER_NAME));
		}
		if (cursor != null)
			cursor.close();
		return sg_username;
	}

	/**
	 * Returns SG profile pic ID
	 * @param sg
	 * @return
     */
	public String getSGLogoFileID(String sg) {
		String sg_username = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_LOGO_FILE_ID },
				DatabaseConstants.DOMAIN_NAME + "='" + sg +"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				sg_username = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_LOGO_FILE_ID));
		}
		if (cursor != null)
			cursor.close();
		return sg_username;
	}

	/**
	 * Returns Display name for SG
	 * @param sg
	 * @return
     */
	public String getSGDisplayName(String sg) {
		String sg_username = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_DISPLAY_NAME },
				DatabaseConstants.DOMAIN_NAME + "='" + sg +"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				sg_username = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_DISPLAY_NAME));
		}
		if (cursor != null)
			cursor.close();
		return sg_username;
	}

	/**
	 * Returns userid for SG
	 * @param sg
	 * @return
     */
	public long getSGUserID(String sg) {
		long user_id = 0;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_USER_ID },
				DatabaseConstants.DOMAIN_NAME + "='" + sg +"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				user_id = cursor.getLong(cursor.getColumnIndex(DatabaseConstants.DOMAIN_USER_ID));
		}
		if (cursor != null)
			cursor.close();
		return user_id;
	}

	/**
	 * Returns type for the SG 1 - Owner, 2 - Member, 3 - Invited
	 * @param sg
	 * @return
     */
	public int getSGUserTypeValue(String sg) {
		int type = -1;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_TYPE_VALUE },
				DatabaseConstants.DOMAIN_NAME + "='" + sg +"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				type = cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_TYPE_VALUE));
		}
		if (cursor != null)
			cursor.close();
		return type;
	}

	/**
	 * Return password for SG
	 * @param sg
	 * @return
     */
	public String getSGPassword(String sg) {
		String user_password = null;
		Cursor cursor = DBWrapper.getInstance().query(
				DatabaseConstants.TABLE_NAME_MULTIPLE_SG,
				new String[] { DatabaseConstants.DOMAIN_USER_PASSWORD },
				DatabaseConstants.DOMAIN_NAME + "='" + sg +"'", null,
				null);
		if (cursor != null) {
			while (cursor.moveToNext())
				user_password = cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_USER_PASSWORD));
		}
		if (cursor != null)
			cursor.close();
		return user_password;
	}

	/**
	 * Returns list of Joined SG's
	 * @returnh
     */
	public ArrayList<JoinedDomainNameSet> getListOfJoinedSGs()
	{
		ArrayList<JoinedDomainNameSet> joined = null;
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery("SELECT * FROM " +DatabaseConstants.TABLE_NAME_MULTIPLE_SG + " WHERE "
				+ DatabaseConstants.DOMAIN_TYPE_VALUE + "=2", null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				joined = new ArrayList<JoinedDomainNameSet>();
				while (cursor.moveToNext()){
					JoinedDomainNameSet sgdata = new JoinedDomainNameSet();
					sgdata.setDomainName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_NAME)));
					sgdata.setDomainDisplayName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_DISPLAY_NAME)));
					sgdata.setAdminName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_ADMIN_NAME)));
					sgdata.setOrgName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_ORG_NAME)));
					sgdata.setPrivacyType(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_PRIVACY_TYPE)));
					sgdata.setUnreadCounter(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT)));
					sgdata.setCreatedDate(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_CREATED_DATE)));
					sgdata.setLogoFileId(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_LOGO_FILE_ID)));
					sgdata.setDomainMuteInfo(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_MUTE_INFO)));
					sgdata.setDomainType(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_TYPE)));
					joined.add(sgdata);
				}
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return joined;
	}

	/**
	 * Returns list of Invited SG's
	 * @return
     */
	public ArrayList<InvitedDomainNameSet> getListOfInvitedSGs()
	{
		ArrayList<InvitedDomainNameSet> invited = null;
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery("SELECT * FROM "+DatabaseConstants.TABLE_NAME_MULTIPLE_SG +" WHERE "
				+ DatabaseConstants.DOMAIN_TYPE_VALUE + "=3", null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				invited = new ArrayList<InvitedDomainNameSet>();
				while (cursor.moveToNext()){
					InvitedDomainNameSet sgdata = new InvitedDomainNameSet();
					sgdata.setDomainName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_NAME)));
					sgdata.setDomainDisplayName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_DISPLAY_NAME)));
					sgdata.setAdminName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_ADMIN_NAME)));
					sgdata.setOrgName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_ORG_NAME)));
					sgdata.setPrivacyType(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_PRIVACY_TYPE)));
					sgdata.setUnreadCounter(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT)));
					sgdata.setCreatedDate(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_CREATED_DATE)));
					sgdata.setLogoFileId(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_LOGO_FILE_ID)));
					sgdata.setDomainMuteInfo(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_MUTE_INFO)));
					invited.add(sgdata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return invited;
	}

	/**
	 * Returns OwnerDomainName Data model object
	 * @return
     */
	public OwnerDomainName getOwnedSG()
	{
		OwnerDomainName sgdata = null;
		Cursor cursor = dbHelper.getWritableDatabase().rawQuery("SELECT * FROM "+DatabaseConstants.TABLE_NAME_MULTIPLE_SG+ " WHERE "
				+ DatabaseConstants.DOMAIN_TYPE_VALUE + "=1", null);
		try {
			if (cursor != null && cursor.getCount() > 0) {
				sgdata = new OwnerDomainName();
				while (cursor.moveToNext()){
					sgdata.setDomainName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_NAME)));
					sgdata.setDomainDisplayName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_DISPLAY_NAME)));
					sgdata.setAdminName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_ADMIN_NAME)));
					sgdata.setOrgName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_ORG_NAME)));
					sgdata.setPrivacyType(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_PRIVACY_TYPE)));
					sgdata.setUnreadCounter(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_UNREAD_MSG_COUNT)));
					sgdata.setCreatedDate(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_CREATED_DATE)));
					sgdata.setLogoFileId(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_LOGO_FILE_ID)));
					sgdata.setDomainMuteInfo(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.DOMAIN_MUTE_INFO)));
					sgdata.setDomainType(cursor.getString(cursor.getColumnIndex(DatabaseConstants.DOMAIN_TYPE)));
				}
			}
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return sgdata;
	}

}
