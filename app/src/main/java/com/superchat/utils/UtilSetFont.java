package com.superchat.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.superchat.SuperChatApplication;

/**
 * Android Bitmap Object to .bmp image (Windows BMP v3 24bit) file util class
 * 
 * ref : http://en.wikipedia.org/wiki/BMP_file_format
 * 
 * @author ultrakain ( ultrasonic@gmail.com )
 * @since 2012-09-27
 *
 */
public class UtilSetFont {

	private static final SuperChatApplication.FONT_TYPE mainFontType = SuperChatApplication.FONT_TYPE.ARIAL_REGULAR;
	public static void setFontMainScreen(final Activity activity){
		try {
			final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
					.findViewById(android.R.id.content)).getChildAt(0);
			SuperChatApplication.getInstance().settingFont(mainFontType, viewGroup);
		} catch (Exception e){

		}
	}
	public static void setFontMainScreen(final View... view){
		try {
			SuperChatApplication.getInstance().settingFont(mainFontType, view);
		} catch (Exception e){

		}
	}
}
