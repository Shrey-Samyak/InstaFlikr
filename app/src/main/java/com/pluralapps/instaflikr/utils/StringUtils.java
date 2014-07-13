package com.pluralapps.instaflikr.utils;

import android.content.Context;
import android.widget.Toast;

public class StringUtils {
	
	private static StringUtils INSTANCE = null;
	private final String EMAIL_REGEX = "[A-Za-z_.]+@\\b([a-zA-Z]+.*?).[A-Za-z]+";
	public static String INSTAGRAM_REGEX = "https?:\\/\\/instagram.com\\/p\\/([a-zA-Z0-9]{1,})\\/?";
	

	private StringUtils() {
	}
	
	
	public static StringUtils getInstance() {
		if(INSTANCE == null)
			INSTANCE = new StringUtils();
		
		return INSTANCE;
	}
	
	
	public boolean isEmail(String text) {
		return text.matches(EMAIL_REGEX);
	}
	
	
	
	public void showToast(Context c, String message) {
		Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
		return;
	}
	
	
	
	public boolean regexMatch(String pattern, String testString) {
		return testString.matches(pattern);
	}
}
