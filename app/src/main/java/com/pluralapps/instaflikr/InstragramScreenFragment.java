package com.pluralapps.instaflikr;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pluralapps.instaflikr.api.InstagramAPI;
import com.pluralapps.instaflikr.constants.AppConstants;
import com.pluralapps.instaflikr.dialogs.InstagramVideoDialog;
import com.pluralapps.instaflikr.interfaces.InitialSetup;
import com.pluralapps.instaflikr.loaders.FontLoader;
import com.pluralapps.instaflikr.net.MediaDownloader;
import com.pluralapps.instaflikr.net.NetworkClient;
import com.pluralapps.instaflikr.parcelabletypes.InstagramObject;
import com.pluralapps.instaflikr.types.MediaType;
import com.pluralapps.instaflikr.utils.StringUtils;
import com.pluralapps.instaflikr.utils.ViewAnimation;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.w3c.dom.Text;

import java.io.IOException;



public class InstragramScreenFragment extends SherlockFragment implements InitialSetup {
	
	private EditText searchURL;
	private Button instagramSaveVideoButton, instagramVideoPreviewButton, instagramSaveImageButton;
	private MediaDownloader mediaDownloader;
    private RelativeLayout actionsLayout;
	protected TextView loadingText, loadingVideoDetailsText, instagramText;
    private Context context;

    //Este objeto esta presente em todo o ciclo de vida do fragmento
    private InstagramObject instagramObject;

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v;
		
		v = inflater.inflate(R.layout.instagram_fragment, container, false);
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
         * de download do video
         */
        instagramSaveVideoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String URL = instagramObject.getVideoURL();
                if(URL != null) {
                    //Fazer o download do ficheiro de video
                    mediaDownloader.downloadFile(URL, "Instagram video: ", MediaType.VIDEO);
                } else {
                    //O URL ainda nao esta definido
                    StringUtils.getInstance().showToast(context, getString(R.string.instagramVideoNotDefined));
                }
            }
        });



        /**
         * Este codigo e chamado quando o utilizador prime o "button"
         * de download da imagem do video do Instagram
         */
        instagramSaveImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String URL = instagramObject.getImageURL();
                if(URL != null) {
                    //Fazer o download do ficheiro JPG/PNG
                    mediaDownloader.downloadFile(URL, "Instagram image: ", MediaType.IMAGE);
                } else {
                    //O URL ainda nao esta definido
                    StringUtils.getInstance().showToast(context, getString(R.string.instagramVideoNotDefined));
                }
            }
        });


        /**
         * Este codigo e executado quando o utilizador pressione o "button"
         * de pre-visualizacao de video
         */
        instagramVideoPreviewButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String URL = instagramObject.getVideoURL();
                if(URL != null) {
                    //Informar o utilizador que o video esta a carregar
                    ViewAnimation.getInstance().changeViewVisibility(loadingText, View.VISIBLE);
                    launchVideoPreviewDialog(URL);
                } else {
                    //O URL ainda nao esta definido
                    StringUtils.getInstance().showToast(context, getString(R.string.instagramVideoNotDefined));
                }
            }
        });
		


        //Codigo chamado quando o utilizador carrega em enter ao introduzir um URL do instagram
        searchURL.setOnKeyListener(new View.OnKeyListener() {


            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //Extrair o texto da EditText
                    String textURL = searchURL.getText().toString().trim();
                    if(!textURL.isEmpty()) {
                        //Verificar se corresponde ao regex do Instagram
                        if(textURL.matches(AppConstants.INSTAGRAM_REGEX)) {
                            //Sucesso, podemos fazer um GET request a pagina do video
                            requestInstagramVideo(textURL);
                        } else {
                            //Informar o utilizador que o endereco e invalido
                            StringUtils.getInstance().showToast(getSherlockActivity(), getString(R.string.invalidRegexInstagram));
                        }
                    }
                }

                return false;
            }
        });
	}

	
	
	/**
	 * Esta funcao faz um GET request a pagina do Instagram e de seguida 
	 * extrai os dados necessarios que contem o endereco real do video
     * e da imagem do video
	 */
	public void requestInstagramVideo(String URL) {
        //Informar o utilizador que os detalhes do video estao a carregar
        ViewAnimation.getInstance().changeViewVisibility(loadingVideoDetailsText, View.VISIBLE);

		NetworkClient.get(URL, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //Retirar o texto de loading dos detalhes do video
                ViewAnimation.getInstance().changeViewVisibility(loadingVideoDetailsText, View.INVISIBLE);

                try {
                    String response = IOUtils.toString(responseBody, "UTF-8");

                    if(response.length() > 0) {
                        InstagramObject tmpObject = InstagramAPI.getInstance().parseInstagramVideoHTML(response);
                        if(tmpObject != null) {
                            //Atualizar o objeto do tipo InstagramObject
                            instagramObject = tmpObject;
                            //Nesta altura temos que alterar a visibilidade de algumas Views
                            changeActionViewVisibility(tmpObject);
                        } else
                            StringUtils.getInstance().showToast(context, getString(R.string.htmlParsedIsNULL));
                    } else
                        StringUtils.getInstance().showToast(context, getString(R.string.instagramEmptyResponse));
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Retirar o texto de loading dos detalhes do video
                ViewAnimation.getInstance().changeViewVisibility(loadingVideoDetailsText, View.INVISIBLE);
				StringUtils.getInstance().showToast(context, getString(R.string.instagramErrorResponse));
			}
		});
	}


    /**
     * Esta funcao altera a visibilidade das Views(Button) consoante as informacoes
     * que estiverem disponiveis no objeto InstagramObject
     */
    public void changeActionViewVisibility(InstagramObject object) {
        MediaType mediaType = object.getMediaType();
        //Mostrar a layout das acoes
        if(mediaType != null) {
            if(mediaType.equals(MediaType.VIDEO_IMAGE)) {
                //Mostrar os tres "Button"
                ViewAnimation.getInstance().changeViewVisibility(instagramSaveImageButton, View.VISIBLE);
                ViewAnimation.getInstance().changeViewVisibility(instagramSaveVideoButton, View.VISIBLE);
                ViewAnimation.getInstance().changeViewVisibility(instagramVideoPreviewButton, View.VISIBLE);
            } else if(mediaType.equals(MediaType.IMAGE)) {
                //Mostrar so o "Button" de gravar imagem
                ViewAnimation.getInstance().changeViewVisibility(instagramSaveImageButton, View.VISIBLE);
                ViewAnimation.getInstance().changeViewVisibility(instagramSaveVideoButton, View.INVISIBLE);
                ViewAnimation.getInstance().changeViewVisibility(instagramVideoPreviewButton, View.INVISIBLE);
            } else if(mediaType.equals(MediaType.VIDEO)) {
                //Mostrar so os dois "Button" de gravar o video
                ViewAnimation.getInstance().changeViewVisibility(instagramSaveImageButton, View.INVISIBLE);
                ViewAnimation.getInstance().changeViewVisibility(instagramSaveVideoButton, View.VISIBLE);
                ViewAnimation.getInstance().changeViewVisibility(instagramVideoPreviewButton, View.VISIBLE);
            }
        }
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
        context = getSherlockActivity();


        instagramObject = new InstagramObject(null, null);
		mediaDownloader = new MediaDownloader(context);


        actionsLayout = (RelativeLayout) view.findViewById(R.id.instagramActionsLayout);
		searchURL = (EditText) view.findViewById(R.id.instagramVideourl);
        instagramSaveVideoButton = (Button) view.findViewById(R.id.saveVideoButton);
		instagramVideoPreviewButton = (Button) view.findViewById(R.id.videoPreviewButton);
        instagramSaveImageButton = (Button) view.findViewById(R.id.saveImageButton);
		loadingText = (TextView) view.findViewById(R.id.videoLoadingText);
        loadingVideoDetailsText = (TextView) view.findViewById(R.id.loadingVideoDetails);
        instagramText = (TextView) view.findViewById(R.id.instagramText);
        instagramSaveVideoButton.setTypeface(FontLoader.getTypeFace(context, FontLoader.ROBOTO_LIGHT));
		instagramVideoPreviewButton.setTypeface(FontLoader.getTypeFace(context, FontLoader.ROBOTO_LIGHT));
        instagramSaveImageButton.setTypeface(FontLoader.getTypeFace(context, FontLoader.ROBOTO_LIGHT));
		searchURL.setTypeface(FontLoader.getTypeFace(context, FontLoader.ROBOTO_BOLD));
		loadingText.setTypeface(FontLoader.getTypeFace(context, FontLoader.ROBOTO_BOLD));
        loadingVideoDetailsText.setTypeface(FontLoader.getTypeFace(context, FontLoader.ROBOTO_BOLD));
        instagramText.setTypeface(FontLoader.getTypeFace(context, FontLoader.INFERNOS_SPICY));


		ViewAnimation.getInstance().changeViewVisibility(loadingText, View.INVISIBLE);
        ViewAnimation.getInstance().changeViewVisibility(loadingVideoDetailsText, View.INVISIBLE);
	}
}
