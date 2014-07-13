package com.pluralapps.instaflikr.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;


public class FileUtils {

	private static FileUtils INSTANCE = null;
	
	
	private FileUtils() {
	}
	
	
	
	public static FileUtils getInstance() {
		if(INSTANCE == null)
			INSTANCE = new FileUtils();
		
		return INSTANCE;
	}
	
	
	public boolean saveImage(Bitmap bm, File path, String photoURL) {
		if(bm != null) {
			if(!path.exists())
				path.mkdirs();
			File filePath = new File(path, photoURL);
			
			
			FileOutputStream outStream;
			try {
				outStream = new FileOutputStream(filePath);
				bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
				return true;
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}
}
