package com.pluralapps.instaflikr.dialogs;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.pluralapps.instaflikr.constants.AppConstants;
import com.pluralapps.instaflikr.interfaces.InitialSetup;
import com.pluralapps.instaflikr.R;



public class InstagramVideoDialog extends SherlockDialogFragment implements InitialSetup {

	private String videoURL;
	private Context c;
	private VideoView instagramVideo;
	private OnVideoLoaded videoLoaded;


    public static InstagramVideoDialog newInstance(String videoURL) {
        InstagramVideoDialog instagramVideoDialog = new InstagramVideoDialog();
        Bundle args = new Bundle();
        args.putString(AppConstants.VIDEO_PREVIEW_URL_KEY, videoURL);
        instagramVideoDialog.setArguments(args);

        return instagramVideoDialog;
    }
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog);
	}
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			videoLoaded = (OnVideoLoaded) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " has to implement OnFilterSelected interface!");
		}
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v;
		
		v = inflater.inflate(R.layout.instagramvideolayout, container, false);
		if(v != null)
			return v;
		
		return null;
	}
	

	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		performInitialSetup(view);
		
		playVideo();
	}
	
	
	//Faz o setup necessario e iniciar o video automaticamente
	public void playVideo() {
		MediaController mediaController = new MediaController(c);
		mediaController.setAnchorView(instagramVideo);
		//Fazer parse do URL do video
		Uri uri = Uri.parse(videoURL);
		instagramVideo.setMediaController(mediaController);
		instagramVideo.setVideoURI(uri);
		
		instagramVideo.requestFocus();
		instagramVideo.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				videoLoaded.videoLoaded();
				instagramVideo.start();
			}
		});
		
		
		
		instagramVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				//Se ocorrer um erro temos que retirar o texto de loading do fragmento
				videoLoaded.videoLoaded();
				return true;
			}
		});
	}
	
	
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		//Se o utilizador fazer dismiss do dialog entao temos que retirar o texto de loading do fragmento
		videoLoaded.videoLoaded();
	}


	@Override
	public void performInitialSetup(View view) {
		instagramVideo = (VideoView) view.findViewById(R.id.instagramVideoPreview);


        Bundle b = getArguments();
        videoURL = b.getString(AppConstants.VIDEO_PREVIEW_URL_KEY);
        //c representa o Contexto
        c = getSherlockActivity();


        //Fundo transparente
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
	}
	
	
	public interface OnVideoLoaded {
		public void videoLoaded();
	}
}
