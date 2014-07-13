package com.pluralapps.instaflikr.adapters;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.pluralapps.instaflikr.MainApplication;
import com.pluralapps.instaflikr.Photo;
import com.pluralapps.instaflikr.SquareImageView;
import com.pluralapps.instaflikr.loaders.FontLoader;
import com.pluralapps.instaflikr.types.PhotoQuality;
import com.pluralapps.instaflikr.utils.ViewAnimation;
import com.pluralapps.instaflikr.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



public class PhotoListAdapter extends ArrayAdapter<Photo> {
	
	private Context c;
	private ArrayList<Photo> photoList;
	
	
	public PhotoListAdapter(Context c, ArrayList<Photo> photoList) {
		super(c, R.layout.photosgridrow, photoList);
		this.c = c;
		this.photoList = photoList;
	}
	

	@Override
	public int getCount() {
		return photoList.size();
	}

	
	@Override
	public Photo getItem(int position) {
		if(photoList != null) 
			return photoList.get(position);
		
		return null;
	}

	
	@Override
	public long getItemId(int position) {
		if(photoList != null)
			return position;
		
		return 0;
	}
	
	
	public void setPhotos(ArrayList<Photo> photoList) {
		this.photoList = photoList;
	}
	

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View photoView = convertView;
		Holder holder;
		
		if(photoView == null) {
			LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			photoView = inflater.inflate(R.layout.photosgridrow, null);
			
			holder = new Holder();
			holder.photoTitle = (TextView) photoView.findViewById(R.id.flickrImageOverlay);
			holder.rowImage = (SquareImageView) photoView.findViewById(R.id.photoImage);
			
			photoView.setTag(holder);
		} else
			holder = (Holder) photoView.getTag();
		
		
		Photo photo = photoList.get(position);
		String title = photo.getTitle();
		//Alterar o tipo de letra 
		holder.photoTitle.setTypeface(FontLoader.getTypeFace(c, FontLoader.ROBOTO_BOLD));
		ImageLoader.getInstance().displayImage(photo.getDownloadURL(PhotoQuality.LARGE_SQUARE), holder.rowImage, MainApplication.options);

		
		if(title.length() > 15)
			title = title.substring(0, 14) + "...";
		if(title.isEmpty())
			ViewAnimation.getInstance().changeViewVisibility(holder.photoTitle, View.GONE);
		else
			holder.photoTitle.setText(title);
		
		return photoView;
	}
	
	
	static class Holder {
		TextView photoTitle;
		SquareImageView rowImage;
	}
}
