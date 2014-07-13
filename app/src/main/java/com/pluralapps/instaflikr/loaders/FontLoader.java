package com.pluralapps.instaflikr.loaders;

import android.content.Context;
import android.graphics.Typeface;

public class FontLoader {
	
	public static final int ROBOTO_LIGHT = 0;
	public static final int ROBOTO_THIN = 1;
	public static final int ROBOTO_BOLD = 2;
	private static String[] fontsPath = { "fonts/Roboto-Light.ttf", "fonts/Roboto-Thin.ttf", "fonts/Roboto-Bold.ttf" };
	private static Typeface[] fonts = new Typeface[3];
	private static boolean fontsLoaded = false;
	
	
	
	public static Typeface getTypeFace(Context context, int fontId) {
		if(!fontsLoaded)
			loadFonts(context);
		
		return fonts[fontId];
	}	
	
	
	private static void loadFonts(Context c) {
		for(int i = 0; i < 3; i++)
			fonts[i] = Typeface.createFromAsset(c.getAssets(), fontsPath[i]);
		
		fontsLoaded = true;
	}
}
