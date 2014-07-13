package com.pluralapps.instaflikr.dialogs;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.pluralapps.instaflikr.interfaces.InitialSetup;
import cn.Ragnarok.BitmapFilter;
import com.pluralapps.instaflikr.R;



public class PhotoFiltersDialog extends SherlockDialogFragment implements InitialSetup {

	private ImageView filterPixelateView, filterAverageBlur, filterGaussianBlur, filterSoftGlow;
	private Bitmap bitmapToManipulate, newBitmap;
	private OnFilterSelected filterSelected;
	
	
	public PhotoFiltersDialog(Bitmap bitmapToManipulate) {
		this.bitmapToManipulate = bitmapToManipulate;
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
			filterSelected = (OnFilterSelected) activity;
		} catch(ClassCastException e) {
			throw new ClassCastException(activity.toString() + " has to implement OnFilterSelected interface!");
		}
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v;

		v = inflater.inflate(R.layout.photofiltercircular, container, false);
		if(v != null)
			return v;
		
		return null;
	}
	
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		performInitialSetup(view);
		new SetImageViewFilter().execute();
		
		
		/**
		 * O codigo seguinte e executado quando carregamos em cada ImageView
		 * Cada um tem um filtro diferente
		 */
		filterPixelateView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					filterSelected.onFilterSelected(BitmapFilter.PIXELATE_STYLE, 8);
				} catch(OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		filterAverageBlur.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					filterSelected.onFilterSelected(BitmapFilter.AVERAGE_BLUR_STYLE, 5);
				} catch(OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		filterGaussianBlur.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					filterSelected.onFilterSelected(BitmapFilter.GAUSSIAN_BLUR_STYLE, 1.2);
				} catch(OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
		});
		
		
		
		filterSoftGlow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					filterSelected.onFilterSelected(BitmapFilter.SOFT_GLOW_STYLE, 0.6);
				} catch(OutOfMemoryError e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	/**
	 * As imagens sao colocadas nas respetivas ImageView numa thread separada
	 * Cada imagem tera um estilo diferente
	 */
	private class SetImageViewFilter extends AsyncTask<Void, Void, Bitmap[]> {
		
		
		private void recycleBitmap() {
			if(newBitmap != null)
				newBitmap = null;
		}
		
		
		@Override
		protected Bitmap[] doInBackground(Void... params) {
			Bitmap bitmaps[] = new Bitmap[4];
			if(bitmapToManipulate != null) {
				newBitmap = BitmapFilter.changeStyle(bitmapToManipulate, BitmapFilter.PIXELATE_STYLE, 8);
				if(newBitmap != null)
					bitmaps[0] = Bitmap.createBitmap(newBitmap);
				recycleBitmap();
				newBitmap = BitmapFilter.changeStyle(bitmapToManipulate, BitmapFilter.AVERAGE_BLUR_STYLE, 5);
				if(newBitmap != null)
					bitmaps[1] = Bitmap.createBitmap(newBitmap);
				recycleBitmap();
				newBitmap = BitmapFilter.changeStyle(bitmapToManipulate, BitmapFilter.GAUSSIAN_BLUR_STYLE, 1.2);
				if(newBitmap != null)
					bitmaps[2] = Bitmap.createBitmap(newBitmap);
				recycleBitmap();
				newBitmap = BitmapFilter.changeStyle(bitmapToManipulate, BitmapFilter.SOFT_GLOW_STYLE, 0.6);
				if(newBitmap != null)
					bitmaps[3] = Bitmap.createBitmap(newBitmap);
				recycleBitmap();
			}
			
			return bitmaps;
		}
		
		
		@Override
		public void onPostExecute(Bitmap[] response) {
			if(response[0] != null)
				filterPixelateView.setImageBitmap(response[0]);
			if(response[1] != null)
				filterAverageBlur.setImageBitmap(response[1]);
			if(response[2] != null)
				filterGaussianBlur.setImageBitmap(response[2]);
			if(response[3] != null)
				filterSoftGlow.setImageBitmap(response[3]);
		}
	}
	
	
	
	/**
	 * Esta interface e utilizada para passar os dados do DialogFragment 
	 * para o SherlockFragment
	 */
	public interface OnFilterSelected {
		public void onFilterSelected(int style, Object... obj);
	}



	@Override
	public void performInitialSetup(View view) {
		filterPixelateView = (ImageView) view.findViewById(R.id.photoFilterPixelate);
		filterAverageBlur = (ImageView) view.findViewById(R.id.photoFilterAverageBlur);
		filterGaussianBlur = (ImageView) view.findViewById(R.id.photoFilterGaussianBlur);
		filterSoftGlow = (ImageView) view.findViewById(R.id.photoFilterSoftGlow);
		//Colocar o fundo transparente
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
	}
}
