package com.pluralapps.instaflikr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.Ragnarok.BitmapFilter;

import com.actionbarsherlock.app.SherlockFragment;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pluralapps.instaflikr.constants.AppConstants;
import com.pluralapps.instaflikr.dialogs.PhotoFiltersDialog;
import com.pluralapps.instaflikr.interfaces.InitialSetup;
import com.pluralapps.instaflikr.loaders.FontLoader;
import com.pluralapps.instaflikr.utils.FileUtils;
import com.pluralapps.instaflikr.utils.StringUtils;
import com.pluralapps.instaflikr.utils.ViewAnimation;
import com.pluralapps.instaflikr.R;


public class PhotoFullscreenFragment extends SherlockFragment implements
		InitialSetup {

	private Photo photo;
	private ImageView fullScreenPhoto;
	private TextView saveFullscreen, showFilters, loadingFullscreenPhoto, fullscreenPhotoTitle;
	private Animation fadeOutAnimation, fadeInAnimation;
	private RelativeLayout filtersLayout;
	private Bitmap originalImage;
	private String photoURL;
	private boolean imageLoaded, mediaPlayerPrepared, alreadySeenShowCaseView;
	private MediaPlayer mediaPlayer;
	private Target viewTarget;
	private SharedPreferences preferences;
	private HashMap<String, String> qualities;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v;
		v = inflater
				.inflate(R.layout.photofullscreenfragment, container, false);
		if (v != null)
			return v;

		return null;
	}

	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		performInitialSetup(view);

		//Carregar a "photo" na ImageView
		if (photo != null) {
			//Colocar o titulo da foto da respetiva TextView
			fullscreenPhotoTitle.setText(photo.getTitle());
			
			//Selecionar uma das qualidades
			photoURL = qualities.get(AppConstants.MEDIUM);
			if (photoURL == null)
				photoURL = qualities.get(AppConstants.LARGE);
			if (photoURL == null)
				photoURL = qualities.get(AppConstants.ORIGINAL);

			
			//Verificar se foi possivel extrair os dados da "photo"
			ImageLoader.getInstance().displayImage(photoURL, fullScreenPhoto,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
							//Mostrar a TextView de loading
							ViewAnimation.getInstance().playAnimation(
									loadingFullscreenPhoto, fadeInAnimation,
									View.VISIBLE);
						}

						
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							//Esconder a TextView de loading e mostrar a layout dos comandos
							ViewAnimation.getInstance().playAnimation(loadingFullscreenPhoto, fadeOutAnimation, View.INVISIBLE);
							ViewAnimation.getInstance().playAnimation(filtersLayout, fadeInAnimation, View.VISIBLE);

							//Guardar imediatamente uma referencia para imagem original
							originalImage = ((BitmapDrawable) fullScreenPhoto.getDrawable()).getBitmap();
							updateShowCasePreference();
						}

						
						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
						}

						
						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason reason) {
							//Esconder a TextView de loading
							ViewAnimation.getInstance().playAnimation(loadingFullscreenPhoto, fadeOutAnimation, View.INVISIBLE);
							//Informar o utilizador que aconteceu algo de errado enquanto a imagem carregava
							StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.loadingImageFailed));
						}
					});
		} else {
			//Informar o utilizador que nao foi possivel carregar a fotografia e fechar
			StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.loadingImageFailed));
		}

		
		saveFullscreen.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//Guarda a imagem que esta atualmente na ImageView no cartao SD
				new SaveFileAsync().execute();
			}
		});

		
		//Este codigo e chamado quando o utilizador carrega na TextView "Filters"
		showFilters.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				launchFiltersDialog();
			}
		});

		
		/**
		 * Quando o utilizador carrega na ImageView o programa deve
		 * esconder/mostrar a TextView de download dependendo se atualmente esta
		 * a mostrar ou nao
		 */
		fullScreenPhoto.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ViewAnimation.getInstance().changeVisibility(filtersLayout,
						fadeInAnimation, fadeOutAnimation);
			}
		});

		
		//Codigo chamado quando o objeto MediaPlayer esta preparado
		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				mediaPlayerPrepared = true;
			}
		});

		
		//Codigo chamado quando ocorre um erro no MediaPlayer
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mp.reset();
				setupMediaPlayer();
				mediaPlayer.prepareAsync();
				return true;
			}
		});

		
		/**
		 * Codigo chamado quando fazemos um "long click" na imagem A imagem e
		 * reiniciada para o estado original
		 */
		fullScreenPhoto.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (originalImage != null)
					fullScreenPhoto.setImageBitmap(originalImage);
				return true;
			}
		});
	}

	
	
	/**
	 * Esta funcao extrai o Bitmap que esta na ImageView e passa como parametro
	 * para o fragmento que abre logo de seguida
	 */
	public void launchFiltersDialog() {
		try {
			Bitmap bm = ((BitmapDrawable) fullScreenPhoto.getDrawable()).getBitmap();
			if (bm != null) {
				try {
					FragmentManager fm = getFragmentManager();
					PhotoFiltersDialog filterDialog = new PhotoFiltersDialog(bm);
					filterDialog.show(fm, "filterDialog");
				} catch (NullPointerException e) {
					// Ocorre quando o utilizador fecha o programa antes de o
					// dialog abrir
					e.printStackTrace();
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	
	
	//Faz update da foto que esta atualmente na Imageview(com um novo efeito)
	public void updatePhotoFilter(int style, Object... obj) {
		Bitmap newBm = null;
		Bitmap bm = originalImage;
		if (bm != null) {
			newBm = BitmapFilter.changeStyle(bm, style, obj);
			if (newBm != null)
				fullScreenPhoto.setImageBitmap(Bitmap.createBitmap(newBm));
		}
	}

	
	
	/**
	 * Esta classe estende uma AsyncTask para que esta operacao seja realizada
	 * numa thread separada Testa varios erros que possam ocorrer e se tal for o
	 * caso avisa o utilizador do que se esta a passar atualmente
	 */
	public class SaveFileAsync extends AsyncTask<Void, Void, Void> {

		private boolean success = false;
		private int current_status = 0;

		
		@Override
		protected Void doInBackground(Void... params) {
			//So podemos gravar se a imagem ja tiver sido carregada
			if (imageLoaded) {
				try {
					Bitmap bm = ((BitmapDrawable) fullScreenPhoto.getDrawable()).getBitmap();
					File f = new File(Environment.getExternalStorageDirectory()
							+ File.separator + "Download" + File.separator);
					boolean couldSaveImage = FileUtils.getInstance().saveImage(bm, f, FilenameUtils.getName(photoURL));
					if (!couldSaveImage)
						current_status = 1;
					else
						success = true;
				} catch (NullPointerException e) {
					e.printStackTrace();
					current_status = 2;
				}
			} else
				current_status = 3;
			return null;
		}

		
		
		@Override
		public void onPostExecute(Void response) {
			if(isAdded()) {
				if (success) {
					if (mediaPlayerPrepared) {
						//Mostrar uma mensagem de sucesso
						StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.imageSavedSuccess));
						mediaPlayer.start();
						ViewAnimation.getInstance().changeVisibility(filtersLayout,
								fadeInAnimation, fadeOutAnimation);
					}
				} else {
					if (current_status == 1 || current_status == 2)
						StringUtils.getInstance().showToast(
								getSherlockActivity().getBaseContext(),
								getString(R.string.errorWhileSavingImage));
					else if (current_status == 3)
						StringUtils.getInstance().showToast(
								getSherlockActivity().getBaseContext(),
								getString(R.string.waitImageLoading));
				}
			}
		}
	}

	
	
	public void setupMediaPlayer() {
		AssetFileDescriptor afd = getSherlockActivity().getResources().openRawResourceFd(R.raw.successful);
		try {
			mediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	
	
	@Override
	public void performInitialSetup(View view) {
		imageLoaded = mediaPlayerPrepared = false;
		mediaPlayer = new MediaPlayer();
		setupMediaPlayer();
		mediaPlayer.prepareAsync();
		filtersLayout = (RelativeLayout) view.findViewById(R.id.filtersLayout);
		fullScreenPhoto = (ImageView) view.findViewById(R.id.originalImageFlickr);
		saveFullscreen = (TextView) view.findViewById(R.id.downloadFullscreenPhoto);
		showFilters = (TextView) view.findViewById(R.id.showFilters);
		loadingFullscreenPhoto = (TextView) view.findViewById(R.id.loadingFulLscreenPhoto);
		fullscreenPhotoTitle = (TextView) view.findViewById(R.id.fullscreenPhotoTitle);
		saveFullscreen.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_LIGHT));
		showFilters.setTypeface(FontLoader.getTypeFace(getSherlockActivity(),FontLoader.ROBOTO_LIGHT));
		loadingFullscreenPhoto.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		viewTarget = new ViewTarget(view.findViewById(R.id.originalImageFlickr));
		preferences = getSherlockActivity().getSharedPreferences(getString(R.string.preferencesFile), Context.MODE_PRIVATE);
		

		//Ler das preferencias e verificar se a ShowCaseView ja foi mostrada
		alreadySeenShowCaseView = preferences.getBoolean(getString(R.string.showCaseKey), false);

		//Carrega as animacoes
		fadeOutAnimation = AnimationUtils.loadAnimation(getSherlockActivity(),R.anim.fadeout);
		fadeInAnimation = AnimationUtils.loadAnimation(getSherlockActivity(),R.anim.fadein);

		//Esconder a TextView de loading inicialmente
		ViewAnimation.getInstance().changeViewVisibility(loadingFullscreenPhoto, View.GONE);
		//Esconder a RelativeLayout dos comandos
		ViewAnimation.getInstance().changeViewVisibility(filtersLayout, View.GONE);

		//Extrair a "photo" passada pelo Intent
		Intent i = getSherlockActivity().getIntent();
		photo = (Photo) i.getParcelableExtra("photo");
		qualities = photo.getQualities();
		
		//Se o titulo estiver vazio entao esocndemos a TextView
		if(photo != null)
			if(photo.getTitle().isEmpty())
				ViewAnimation.getInstance().changeViewVisibility(fullscreenPhotoTitle, View.INVISIBLE);
	}

	
	
	public void updateShowCasePreference() {
		imageLoaded = true;
		if (!alreadySeenShowCaseView) {
			//Mostrar a ShowCaseView se ainda nao foi visto
			new ShowcaseView.Builder(getSherlockActivity(), true)
					.setTarget(viewTarget)
					.setContentTitle(getString(R.string.showCaseViewTitle))
					.setContentText(getString(R.string.showCaseViewDesc))
					.build();

			//Atualizar as preferencias
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(getString(R.string.showCaseKey), true);
			editor.commit();
		}
	}

	
	//Funcao chamada quando o dialog e fechado
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Eliminar os recursos alocados pelo MediaPlayer
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
}