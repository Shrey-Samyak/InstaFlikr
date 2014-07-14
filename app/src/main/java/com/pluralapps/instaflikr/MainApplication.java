package com.pluralapps.instaflikr;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.pluralapps.instaflikr.constants.AppConstants;
import com.startapp.android.publish.StartAppSDK;

import android.graphics.Bitmap;
import android.app.Application;

import java.util.HashMap;


public class MainApplication extends Application {
	
	public static DisplayImageOptions options = 
			new DisplayImageOptions.Builder().displayer(new FadeInBitmapDisplayer(300)).bitmapConfig(Bitmap.Config.RGB_565).
			imageScaleType(ImageScaleType.EXACTLY).cacheOnDisk(true).showImageOnLoading(R.drawable.gridrowbackground).build();

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();


    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }


    synchronized Tracker getTracker(TrackerName name) {
        if(!mTrackers.containsKey(name)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker tracker = analytics.newTracker(R.xml.global_tracker);

            mTrackers.put(name, tracker);
        }

        return mTrackers.get(name);
    }

	
	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext()).threadPoolSize(1).
                diskCacheExtraOptions(480, 320, null).build();
		ImageLoader.getInstance().init(config);
		StartAppSDK.init(this, AppConstants.DEVELOPER_ID, AppConstants.APP_ID);
	}
}
