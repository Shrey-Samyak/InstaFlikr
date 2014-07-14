package com.pluralapps.instaflikr;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pluralapps.instaflikr.constants.AppConstants;
import com.startapp.android.publish.StartAppSDK;

import android.graphics.Bitmap;
import android.app.Application;


public class MainApplication extends Application {
	
	public static DisplayImageOptions options = 
			new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(300)).bitmapConfig(Bitmap.Config.RGB_565).
			imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).showImageOnLoading(R.drawable.gridrowbackground).build();
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPoolSize(1).
                diskCacheExtraOptions(480, 320, null).build();
		ImageLoader.getInstance().init(config);
		StartAppSDK.init(this, AppConstants.DEVELOPER_ID, AppConstants.APP_ID);
	}
}
