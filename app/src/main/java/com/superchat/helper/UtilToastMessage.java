package com.superchat.helper;

import android.app.Activity;
import android.widget.Toast;

public class UtilToastMessage {

	public void makeToast(final Activity activity, String Message, String MODE){
		if(UtilGlobal.isValidMode(MODE))
			Toast.makeText(activity, Message, Toast.LENGTH_LONG).show();

	}
	
	private static UtilToastMessage uniqInstance;
	
	public static synchronized UtilToastMessage getInstance() {
		if (uniqInstance == null) {
			uniqInstance = new UtilToastMessage();
		}
		return uniqInstance;
	}
}
