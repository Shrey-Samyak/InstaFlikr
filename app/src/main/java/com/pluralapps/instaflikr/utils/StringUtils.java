package com.pluralapps.instaflikr.utils;

import android.content.Context;
import android.widget.Toast;

import com.pluralapps.instaflikr.constants.AppConstants;

public class StringUtils {
	
	private static StringUtils INSTANCE = null;
	

	private StringUtils() {
	}
	
	
	public static StringUtils getInstance() {
		if(INSTANCE == null)
			INSTANCE = new StringUtils();
		
		return INSTANCE;
	}
	
	
	public boolean isEmail(String text) {
		return text.matches(AppConstants.EMAIL_REGEX);
	}
	
	
	
	public void showToast(Context c, String message) {
		Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
		return;
	}
}
