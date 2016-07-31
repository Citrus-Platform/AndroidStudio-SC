package com.superchat.ui;

import com.superchat.R;
import com.superchat.data.db.DBWrapper;
import com.superchat.data.db.DatabaseConstants;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class ContactDetails extends ListActivity {
//	public final static String TAG = "ContactDetails";
//
//	public static class ContactInvite {
//
//		static int count = 0;
//		boolean checked;
//		String number;
//
//		public boolean isChecked() {
//			return checked;
//		}
//
//		public ContactInvite(String s, boolean flag) {
//			number = s;
//			checked = flag;
//			count = 1 + count;
//		}
//	}
//
//	public static final String CONTACT_ID = "contact_id";
//	private ContactDetailAdapter contactDetailAdapter;
//
//	private TextView contactName;
//	private boolean isEsiaChatUser;
//	private String contactID;
//	private String contactNameTxt = "";
//	private Cursor cursor;
//
//	protected void onCreate(Bundle bundle) {
//		super.onCreate(bundle);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.contact_detail);
//		contactName = (TextView) findViewById(R.id.id_contact_name);
//		if (getIntent().getExtras() != null) {
//			isEsiaChatUser = getIntent().getExtras().getBoolean("is_vopium_user");
//			contactID = getIntent().getExtras().getString(DatabaseConstants.NAME_CONTACT_ID_FIELD);
//			contactNameTxt = getIntent().getExtras().getString(DatabaseConstants.CONTACT_NAMES_FIELD);
//			contactName.setText(contactNameTxt);
//		}
//		String where = DatabaseConstants.NAME_CONTACT_ID_FIELD + "=?";
//		String[] params = (new String[] { contactID });
//		cursor = DBWrapper.getInstance(getApplicationContext()).query(
//				DatabaseConstants.TABLE_NAME_CONTACT_NUMBERS, null, where,
//				params, null);// "data11 DESC");
//		contactDetailAdapter = new ContactDetailAdapter(this, cursor, false,contactNameTxt);
//		 setListAdapter(contactDetailAdapter);
//	}
//	public void onBackClick(View view){
//		finish();
//	}
	
}
