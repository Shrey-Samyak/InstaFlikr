package com.pluralapps.instaflikr.dialogs;


import java.util.HashMap;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.pluralapps.instaflikr.parcelabletypes.Photo;
import com.pluralapps.instaflikr.PhotoFullscreenActivity;
import com.pluralapps.instaflikr.constants.AppConstants;
import com.pluralapps.instaflikr.interfaces.InitialSetup;
import com.pluralapps.instaflikr.loaders.FontLoader;
import com.pluralapps.instaflikr.net.MediaDownloader;
import com.pluralapps.instaflikr.types.MediaType;
import com.pluralapps.instaflikr.R;


public class DownloadPhotoDialog extends SherlockDialogFragment implements InitialSetup {
	
	
	private Button downloadButton, viewBiggerPhotoButton;
	private Button downloadSmallButton, downloadLargeButton, downloadMediumButton, downloadOriginalButton;
	private Photo photo;
	private MediaDownloader mediaDownloader;
	private Animation fadeOutAnimation, fadeInAnimation, fadeOutAnimationAndExit;
	private RelativeLayout downloadButtons;
	private HashMap<String, String> qualities;



    public static DownloadPhotoDialog newInstance(Photo p) {
        DownloadPhotoDialog f = new DownloadPhotoDialog();
        Bundle args = new Bundle();
        args.putParcelable(AppConstants.PHOTO_OBJECT_KEY, p);
        f.setArguments(args);

        return f;
    }


    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v;
		
		v = inflater.inflate(R.layout.flickr_photo_details_dialog, container, false);
		if(v != null)
			return v;
		
		return null;
	}
	
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		performInitialSetup(view);
		
		
		downloadButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//Mostrar a layout que contem os "buttons" que permitem ao utilizador selecionar a qualidade da fotografia
				int visible = downloadButtons.getVisibility();
				if(visible == View.GONE) {
					downloadButtons.setVisibility(View.VISIBLE);
					downloadButtons.startAnimation(fadeInAnimation);
				} else {
					downloadButtons.setVisibility(View.GONE);
					downloadButtons.startAnimation(fadeOutAnimation);
				}
			}
		});
		
		
		
		/**
		 * Este codigo e executado quando carregamos no "button" para ver a foto maior
		 * Temos que passar o objeto "photo" para a nova Activity
		 */
		viewBiggerPhotoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				launchPhotoActivity();
			}
		});
		
		
		
		fadeOutAnimationAndExit.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if(isAdded())
					dismiss();
			}
		});
		
		
		
		/**
		 * As linhas seguintes descrevem como o programa se deve comportar quando 
		 * o utilizador carrega em qualquer um dos "button" de selecao de qualidade
		 */
		downloadSmallButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isAdded())
					mediaDownloader.downloadFile(qualities.get(AppConstants.SMALL_320), photo.getTitle(), MediaType.IMAGE);
			}
		});
		
		
		
		downloadMediumButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isAdded())
					mediaDownloader.downloadFile(qualities.get(AppConstants.MEDIUM), photo.getTitle(), MediaType.IMAGE);
			}
		});
		
		
		
		downloadLargeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isAdded())
					mediaDownloader.downloadFile(qualities.get(AppConstants.LARGE), photo.getTitle(), MediaType.IMAGE);
			}
		});
		
		
		
		downloadOriginalButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isAdded())
					mediaDownloader.downloadFile(qualities.get(AppConstants.ORIGINAL), photo.getTitle(), MediaType.IMAGE);
			}
		});
	}
	
	
	
	public void launchPhotoActivity() {
		Intent i = new Intent(getSherlockActivity(), PhotoFullscreenActivity.class);
		i.putExtra("photo", photo);
		startActivity(i);
		if(isAdded())
			dismiss();
	}
	
	
	
	@Override
	public void performInitialSetup(View view) {
		downloadButtons = (RelativeLayout) view.findViewById(R.id.downloadFlickrQualities);
		downloadSmallButton = (Button) view.findViewById(R.id.downloadPhotoSmall);
		downloadLargeButton = (Button) view.findViewById(R.id.downloadPhotoLarge);
		downloadMediumButton = (Button) view.findViewById(R.id.downloadPhotoMedium);
		downloadButton = (Button) view.findViewById(R.id.downloadPhotoButton);
		viewBiggerPhotoButton = (Button) view.findViewById(R.id.viewPhotoBiggerButton);
		downloadOriginalButton = (Button) view.findViewById(R.id.downloadPhotoOriginal);
		fadeOutAnimation = AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.fadeout);
		fadeOutAnimationAndExit = AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.fadeout);
		fadeInAnimation = AnimationUtils.loadAnimation(getSherlockActivity(), R.anim.fadein);
		
		
		//Inicialmente vamos esconder a layout que contem os "buttons" de qualidade
		downloadButtons.setVisibility(View.GONE);
		downloadButton.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		viewBiggerPhotoButton.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		downloadOriginalButton.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		downloadSmallButton.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		downloadLargeButton.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		downloadMediumButton.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));


        //Iniciar as principais variaveis nesta parte do codigo
        mediaDownloader = new MediaDownloader(getActivity());
        Bundle b = getArguments();
        photo = b.getParcelable(AppConstants.PHOTO_OBJECT_KEY);
        qualities = photo.getQualities();
		
		
		if(qualities.get(AppConstants.LARGE) == null)
			downloadLargeButton.setVisibility(View.GONE);
		if(qualities.get(AppConstants.MEDIUM) == null)
			downloadMediumButton.setVisibility(View.GONE);
		if(qualities.get(AppConstants.SMALL_320) == null)
			downloadSmallButton.setVisibility(View.GONE);
		if(qualities.get(AppConstants.ORIGINAL) == null)
			downloadOriginalButton.setVisibility(View.GONE);
	}
}
