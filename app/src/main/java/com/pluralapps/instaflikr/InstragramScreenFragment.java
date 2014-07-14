package com.pluralapps.instaflikr;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pluralapps.instaflikr.api.InstagramAPI;
import com.pluralapps.instaflikr.dialogs.InstagramVideoDialog;
import com.pluralapps.instaflikr.interfaces.InitialSetup;
import com.pluralapps.instaflikr.loaders.FontLoader;
import com.pluralapps.instaflikr.net.MediaDownloader;
import com.pluralapps.instaflikr.net.NetworkClient;
import com.pluralapps.instaflikr.types.MediaType;
import com.pluralapps.instaflikr.utils.StringUtils;
import com.pluralapps.instaflikr.utils.ViewAnimation;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;

import java.io.IOException;



public class InstragramScreenFragment extends SherlockFragment implements InitialSetup {
	
	private EditText searchURL;
	private Button instagramDownloadButton, instagramVideoPreviewButton;
	private MediaDownloader mediaDownloader;
	protected TextView loadingText;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v;
		
		v = inflater.inflate(R.layout.instagramfragment, container, false);
		if(v != null)
			return v;
		
		return null;
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);		
		performInitialSetup(view);
		/**
		 * Este codigo e chamado quando o utilizador prime o "button"
		 * "download video"
		 */
		instagramDownloadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Extrair o texto da EditText
				String textURL = searchURL.getText().toString().trim();
				
				//Correr o codigo seguinte so se o conteudo da EditText nao estiver vazio
				if(!textURL.isEmpty()) {
					//Verificar se a string faz match com o regex
					if(StringUtils.getInstance().regexMatch(StringUtils.INSTAGRAM_REGEX, textURL)) {
						//Correto, podemos fazer um GET request para a pagina
						requestInstagramVideo(textURL);
					} else {
						//Mostrar uma mensagem de erro
						StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.invalidRegexInstagram));
					}
				}
			}
		});
		
		
		
		//Este codigo e executado quando o utilizador carrega no "button" "Video Preview"
		instagramVideoPreviewButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String textURL = searchURL.getText().toString().trim();
				//Informar o utilizador que o video esta a carregar
				ViewAnimation.getInstance().changeViewVisibility(loadingText, View.VISIBLE);
				if(StringUtils.getInstance().regexMatch(StringUtils.INSTAGRAM_REGEX, textURL)) {
					NetworkClient.get(textURL, null, new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                String response = IOUtils.toString(responseBody, "UTF-8");
                                String realURL = InstagramAPI.getInstance().parseInstagramVideoHTML(response);
                                if(realURL != null) {
                                    //Mostrar o texto
                                    launchVideoPreviewDialog(realURL);
                                } else {
                                    ViewAnimation.getInstance().changeViewVisibility(loadingText, View.INVISIBLE);
                                    StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.htmlParsedIsNULL));
                                }
                            } catch(IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							ViewAnimation.getInstance().changeViewVisibility(loadingText, View.INVISIBLE);
							StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.instagramErrorResponse));
						}
					});
				} else {
					ViewAnimation.getInstance().changeViewVisibility(loadingText, View.INVISIBLE);
					StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.invalidRegexInstagram));
				}
			}
		});
	}
	
	
	
	
	/**
	 * Esta funcao faz um GET request a pagina do Instagram e de seguida 
	 * extrai os dados necessarios que contem o endereco real do video
	 */
	public void requestInstagramVideo(String URL) {
		NetworkClient.get(URL, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = IOUtils.toString(responseBody, "UTF-8");

                    if(response.length() > 0) {
                        //Extrair o URL verdadeiro do video
                        String realURL = InstagramAPI.getInstance().parseInstagramVideoHTML(response);
                        if(realURL != null) {
                            //Fixe, foi possivel extrair o URL real do video
                            downloadVideoInstagram(realURL);
                        } else
                            StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.htmlParsedIsNULL));
                    } else
                        StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.instagramEmptyResponse));
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.instagramErrorResponse));
			}
		});
	}
	
	
	
	/**
	 * Esta funcao faz simplesmente o download de um video do instagram
	 * com o URL passado como argumento da funcao
	 */
	public void downloadVideoInstagram(String URL) {
		mediaDownloader.downloadFile(URL, "Instagram Video", MediaType.VIDEO);
	}
	
	
	
	/**
	 * Esta funcao inicia o Dialog que mostra o video preview de um video do Instagram
	 * E necessario passar o URL do video em questao
	 */
	public void launchVideoPreviewDialog(String URL) {
		try {
            FragmentManager fm = getFragmentManager();
            InstagramVideoDialog instagramVideoDialog = InstagramVideoDialog.newInstance(URL);
            instagramVideoDialog.show(fm, "dialogInstagramVideoPreview");
		} catch(NullPointerException e) {
			//Esta excepcao pode ocorrer quando o utilizar fecha o dialog antes de ele estar aberto
			e.printStackTrace();
		}
	}
	


	@Override
	public void performInitialSetup(View view) {
		mediaDownloader = new MediaDownloader(getSherlockActivity());
		
		searchURL = (EditText) view.findViewById(R.id.instagramVideourl);
		instagramDownloadButton = (Button) view.findViewById(R.id.instagramDownloadButton);
		instagramVideoPreviewButton = (Button) view.findViewById(R.id.instagramPreviewVideo);
		loadingText = (TextView) view.findViewById(R.id.videoLoadingText);
		instagramDownloadButton.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		instagramVideoPreviewButton.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		searchURL.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));
		loadingText.setTypeface(FontLoader.getTypeFace(getSherlockActivity(), FontLoader.ROBOTO_BOLD));


		//Esconder o texto de loading inicialmente
		ViewAnimation.getInstance().changeViewVisibility(loadingText, View.INVISIBLE);
	}
}
