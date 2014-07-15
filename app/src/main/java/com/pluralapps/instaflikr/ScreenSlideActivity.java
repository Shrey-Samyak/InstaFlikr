package com.pluralapps.instaflikr;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.pluralapps.instaflikr.dialogs.InstagramVideoDialog.OnVideoLoaded;
import com.pluralapps.instaflikr.transformers.ZoomOutPageTransformer;
import com.pluralapps.instaflikr.utils.ViewAnimation;


public class ScreenSlideActivity extends SherlockFragmentActivity implements OnVideoLoaded {
	
	private static final int NUM_PAGES = 2;
	private ViewPager viewPager;
	private ScreenSlideAdapter screenAdapter;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_slidepager);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		screenAdapter = new ScreenSlideAdapter(getSupportFragmentManager());
		viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		viewPager.setAdapter(screenAdapter);
	}
	
	
	
	
	private class ScreenSlideAdapter extends FragmentStatePagerAdapter {
		SparseArray<SherlockFragment> frags = new SparseArray<SherlockFragment>();
		
		public ScreenSlideAdapter(FragmentManager fm) {
			super(fm);
		}

		
		@Override
		public SherlockFragment getItem(int position) {
			switch(position) {
			case 0:
				return new PhotosListFragment();
			
			case 1:
				return new InstragramScreenFragment();
				
			default:
				return new PhotosListFragment();
			}
		}

		
		@Override
		public int getCount() {
			return NUM_PAGES;
		}
		
		
		@Override
	    public Object instantiateItem(ViewGroup container, int position) {
			SherlockFragment fragment = (SherlockFragment) super.instantiateItem(container, position);
			frags.put(position, fragment);
			return fragment;
	    }
		
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			frags.remove(position);
			super.destroyItem(container, position, object);
		}
		
		
		public SherlockFragment getFragment(int position) {
			return frags.get(position);
		}
	}




	@Override
	public void videoLoaded() {
		InstragramScreenFragment frag = (InstragramScreenFragment) screenAdapter.getFragment(1);
		if(frag != null)
			ViewAnimation.getInstance().changeViewVisibility(frag.loadingText, View.INVISIBLE);
	}
}
