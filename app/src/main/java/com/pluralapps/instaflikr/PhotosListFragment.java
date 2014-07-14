package com.pluralapps.instaflikr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pluralapps.instaflikr.adapters.PhotoListAdapter;
import com.pluralapps.instaflikr.api.FlickrAPI;
import com.pluralapps.instaflikr.constants.AppConstants;
import com.pluralapps.instaflikr.dialogs.DownloadPhotoDialog;
import com.pluralapps.instaflikr.interfaces.InitialSetup;
import com.pluralapps.instaflikr.loaders.FontLoader;
import com.pluralapps.instaflikr.net.NetworkClient;
import com.pluralapps.instaflikr.utils.StringUtils;
import com.pluralapps.instaflikr.utils.ViewAnimation;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;


public class PhotosListFragment extends SherlockFragment implements InitialSetup {
	
	private GridView photosGrid;
	private PhotoListAdapter photoAdapter;
	private ImageView moveGridToTop;
	private EditText flickrSearch;
	private ArrayList<Photo> photos;
	private TextView loadingImageDetails;
	

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		Window win = getSherlockActivity().getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		winParams.flags |= bits;
		win.setAttributes(winParams);
		winParams = win.getAttributes();
		bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
		winParams.flags |= bits;
		win.setAttributes(winParams);
	}
	

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v;
		v = inflater.inflate(R.layout.photosgridfragment, container, false);
		if(v != null)
			return v;
		
		return null;
	}
	
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		performInitialSetup(view);
		
		//Funcao chamada quando um o utilizador carrega num item do grid
		photosGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {					
				//Mover a foto para o topo da pagina
				photosGrid.smoothScrollToPositionFromTop(position, 0, 1000);
				photosGrid.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						Photo p = photos.get(position);
						getPhotoSizes(p);
					}
				}, 1000);
			}
		});
		
		
		moveGridToTop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				photosGrid.smoothScrollToPositionFromTop(0, 0);
			}
		});
		
		
		
		//Funcao chamada quando um utilizador faz scroll da gridview que contem as fotos
		photosGrid.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				final GridView lw = photosGrid;
				
				if(scrollState == 0)
					ViewAnimation.getInstance().changeViewVisibility(moveGridToTop, View.INVISIBLE);
				
				if(view.getId() == lw.getId())
					ViewAnimation.getInstance().changeViewVisibility(moveGridToTop, View.VISIBLE);
			}
			
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		
		
		//Funcao chamada quando o utilizador quer pesquisar algo no Flickr
		flickrSearch.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String text = flickrSearch.getText().toString().trim();
					if(!text.isEmpty()) {
						//Vamos verificar se o texto e um email ou nao
						if(StringUtils.getInstance().isEmail(text)) {
							/**
							 * O texto na EditText e um email.
							 * Agora temos que retirar o NSID do Flickr
							 */
							updatePhotosUser(text);
						} else {
							String URL = FlickrAPI.getInstance().getSearchPhotosURL(text);
							updatePhotosGrid(URL, true);
						}
						return true;
					}
					return false;
				}
				return false;
			}
		});
	}
	
	
	
	/**
	 * Esta funcao mostra o SherlockDialogFragment de download de uma foto.
	 * Neste dialog o utilizador tambem pode optar por ver a foto em fullscreen
	 */
	public void showDownloadDialog(Photo photo) {
		try {
            //So vamos iniciar o SherlockDialog se o objecto do tipo Photo nao for null
            if(photo != null) {
                FragmentManager fm = getFragmentManager();
                DownloadPhotoDialog downloadPhotoDialog = DownloadPhotoDialog.newInstance(photo);
                downloadPhotoDialog.show(fm, "donwloadPhotoDialog");
            }
		} catch(NullPointerException e) {
			//Esta excepcao pode ocorrer quando o utilizador fecha o programa antes de o dialog abrir
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Esta funcao faz update da GridView de fotografias com fotografias
	 * do utilizador com o email passo no parametro "email".
	 */
	public void updatePhotosUser(String email) {
		NetworkClient.get(FlickrAPI.getInstance().getSearchUserURL(email), null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = IOUtils.toString(responseBody, "UTF-8");
                    //Remover as imagens que estao em cache
                    ImageLoader.getInstance().clearDiskCache();
                    String NSID = FlickrAPI.getInstance().extractNSID(response);
                    if(NSID == null) {
                        //Nao foi possivel extrair o NSID de um utilizador, informar o utilizador
                        StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.NSIDError));
                    } else {
                        /**
                         * Foi possivel extrair o NSID do utilizador
                         * Agora temos que retirar todas as fotografias publicas desse utilizador
                         */
                        String URL = FlickrAPI.getInstance().getSearchByNSIDURL(NSID);
                        updatePhotosGrid(URL, true);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            //Informar o utilizador que nao foi possivel extrair os dados da lista
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.couldNotUpdateList));
			}
		});
	}
	
	
	
	/**
	 * Esta funcao e chamada quando um utilizador faz uma pesquisa
	 * e carrega no botao "enter".
	 */
	public void updatePhotosGrid(String URL, final boolean startNull) {
		NetworkClient.get(URL, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = IOUtils.toString(responseBody, "UTF-8");
                    //Remover as imagens que estao em cache
                    ImageLoader.getInstance().clearDiskCache();
                    ArrayList<Photo> tmpPhotos = FlickrAPI.getInstance().getPhotos(response);
                    if(tmpPhotos.isEmpty())
                        StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.noResultsFlickr));
                    else {
                        photos = tmpPhotos;
                        if(startNull)
                            photosGrid.setAdapter(null);
                        photoAdapter.setPhotos(photos);
                        photosGrid.setAdapter(photoAdapter);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            //Informar o utilizador que nao foi possivel extrair os dados da lista
			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.couldNotUpdateList));
			}
		});
	}
	
	
	
	/**
	 * Esta funcao extrai todos os tamanhos disponiveis de uma foto
	 * De seguida atribui automaticamente estes tamanhos a uma determinada foto
	 */
	public void getPhotoSizes(final Photo p) {
		String URL = FlickrAPI.getInstance().getPhotoInfoURL(p.getID());
		//Informar o utilizador que o programa esta a carregar os detalhes da imagem
		ViewAnimation.getInstance().changeViewVisibility(loadingImageDetails, View.VISIBLE);
		
		NetworkClient.get(URL, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = IOUtils.toString(responseBody, "UTF-8");
                    //Esconder a TextView de loading
                    ViewAnimation.getInstance().changeViewVisibility(loadingImageDetails, View.INVISIBLE);
                    HashMap<String, String> map = FlickrAPI.getInstance().getPhotoSizes(response);
                    //Nao foi possivel extrair as qualidades da fotografia
                    if(map == null || map.isEmpty())
                        StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.couldNotExtractPhotos));
                    else {
                        p.setQualities(map);
                        showDownloadDialog(p);
                    }
                }  catch(IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				ViewAnimation.getInstance().changeViewVisibility(loadingImageDetails, View.INVISIBLE);
				StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.couldNotExtractPhotos));
			}
		});
	}



	@Override
	public void performInitialSetup(View view) {
		photos = new ArrayList<Photo>();
		photoAdapter = new PhotoListAdapter(getSherlockActivity(), photos);
		photosGrid = (GridView) view.findViewById(R.id.photosGrid);
		flickrSearch = (EditText) view.findViewById(R.id.flickrSearchImages);
		moveGridToTop = (ImageView) view.findViewById(R.id.flickrMoveListToTop);
		loadingImageDetails = (TextView) view.findViewById(R.id.loadingImageDetails);
		moveGridToTop.setVisibility(View.INVISIBLE);
		loadingImageDetails.setVisibility(View.INVISIBLE);
		flickrSearch.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		loadingImageDetails.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_LIGHT));
		
		updatePhotosGrid(FlickrAPI.getInstance().getRecentPhotosURL(), false);
	}
}
