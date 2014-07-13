package com.pluralapps.instaflikr;

import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.pluralapps.instaflikr.dialogs.PhotoFiltersDialog.OnFilterSelected;
import com.pluralapps.instaflikr.R;


public class PhotoFullscreenActivity extends SherlockFragmentActivity implements OnFilterSelected {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.photofullscreenlayout);
		
		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
	}

	
	@Override
	public void onFilterSelected(int style, Object... obj) {
		PhotoFullscreenFragment frag = (PhotoFullscreenFragment) getSupportFragmentManager().findFragmentById(R.id.photoFullScreenFragment);
		if(frag != null)
			frag.updatePhotoFilter(style, obj);
	}
}
