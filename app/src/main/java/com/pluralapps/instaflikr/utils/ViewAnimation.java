package com.pluralapps.instaflikr.utils;

import android.view.View;
import android.view.animation.Animation;


public class ViewAnimation {

	private static ViewAnimation INSTANCE = null;
	
	
	private ViewAnimation() {
	}
	
	
	public static ViewAnimation getInstance() {
		if(INSTANCE == null)
			INSTANCE = new ViewAnimation();
		
		return INSTANCE;
	}
	
	
	
	public void changeVisibility(View layout, Animation visibleAnimation, Animation invisibleAnimation) {
		int visible = layout.getVisibility();
		if(visible == View.VISIBLE) {
			layout.startAnimation(invisibleAnimation);
			layout.setVisibility(View.INVISIBLE);
		} else {
			layout.startAnimation(visibleAnimation);
			layout.setVisibility(View.VISIBLE);
		}
	}
	
	
	
	public void playAnimation(View view, Animation animation, int visible) {
		view.setVisibility(visible);
		view.startAnimation(animation);
	}
	
	
	public void changeViewVisibility(View view, int visible) {
		view.setVisibility(visible);
	}
}
