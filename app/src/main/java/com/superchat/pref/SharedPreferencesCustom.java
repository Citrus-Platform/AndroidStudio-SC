package com.superchat.pref;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesCustom {

	private Context context;
	private String KEY_PREFERENCE;

	public SharedPreferencesCustom(final Activity activity, final String KEY_PREFERENCE){
		this.KEY_PREFERENCE = KEY_PREFERENCE;
		this.context = activity;
	}

	public SharedPreferencesCustom(final Context context, final String KEY_PREFERENCE){
		this.KEY_PREFERENCE = KEY_PREFERENCE;
		this.context = context;
	}

	private SharedPreferences getSharedPreferences(){
		SharedPreferences SP = context.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE);

		return SP;
	}

	private String getFromSharedPreferences(final String KEY){
		String VALUE;
		try{
			SharedPreferences SP = getSharedPreferences();
			VALUE = SP.getString(KEY, null);
		} catch(Exception e){
			VALUE = null;
		}
		
		return VALUE;
	}
	
	private void putInSharedPreferences(final String KEY, final String VALUE){
		try{
			SharedPreferences SP = context.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE);

			Editor SP_editor = SP.edit();
			SP_editor.putString(KEY, VALUE);
			SP_editor.commit();
		} catch(Exception e){

		}
	}

	public void putString(final String KEY, final String VALUE){
		putInSharedPreferences(KEY, VALUE);
	}
	
	public String getString(final String KEY){
		return getFromSharedPreferences(KEY);
	}
	
	public void clearSharedPreferences(){
		try{
			SharedPreferences SP = context.getSharedPreferences(KEY_PREFERENCE, Context.MODE_PRIVATE);
			SP.edit().clear();
			
			Editor SP_editor = SP.edit();
			SP_editor.clear();
			SP_editor.commit();
		} catch(Exception e){

		}
	}
}
