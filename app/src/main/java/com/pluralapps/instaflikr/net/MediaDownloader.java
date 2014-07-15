package com.pluralapps.instaflikr.net;


import com.pluralapps.instaflikr.types.MediaType;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;


public class MediaDownloader {

	private DownloadManager dm = null;
	
	
	public MediaDownloader(Context c) {
		dm = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	
	
	public void downloadFile(String URL, String title, MediaType mediaType) {
		Uri uri = Uri.parse(URL);
		String notificationTitle = "";
		String fileName = URL.substring(URL.lastIndexOf('/') + 1);
		
		if(mediaType == MediaType.IMAGE)
			notificationTitle = "Downloading image: " + title;
		else if(mediaType == MediaType.VIDEO)
			notificationTitle = "Downloading video: " + title;
		
		
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
		dm.enqueue(new DownloadManager.Request(uri)
		.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
		.setAllowedOverRoaming(false)
		.setTitle(notificationTitle)
		.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName));
	}
}
