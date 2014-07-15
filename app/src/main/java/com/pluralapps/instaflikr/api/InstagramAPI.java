package com.pluralapps.instaflikr.api;

import com.pluralapps.instaflikr.constants.AppConstants;
import com.pluralapps.instaflikr.parcelabletypes.InstagramObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


public class InstagramAPI {
	
	private static InstagramAPI INSTANCE = null;
	
	
	private InstagramAPI() {
	}
	
	
	public static InstagramAPI getInstance() {
		if(INSTANCE == null)
			INSTANCE = new InstagramAPI();
		
		return INSTANCE;
	}


    /**
     * Esta funcao faz parse do codigo HTML e tenta extrair todos os dados que forem possiveis
     * de maneira a construir um objeto do tipo InstagramObject com o maior numero de atributos
     * possiveis
     */
    public InstagramObject parseInstagramVideoHTML(String htmlCode) {
        Document doc = Jsoup.parse(htmlCode);
        Elements elems = doc.getElementsByTag(AppConstants.META_TAG);
        String videoURL = null, imageURL = null, tmp;
        boolean foundVideoURL = false, foundImageURL = false;

        if(elems.size() > 0) {
            for(Element e : elems) {
                if(e.hasAttr(AppConstants.PROPERTY_TAG)) {
                    String ppr = e.attr(AppConstants.PROPERTY_TAG);
                    //Verificar se a o valor da tag property e o que procuramos
                    if(ppr.equals(AppConstants.IMAGE_KEY)) {
                        //Extrair o valor do atributo "content"
                        tmp = e.attr(AppConstants.CONTENT_TAG);
                        if(!tmp.isEmpty()) {
                            imageURL = tmp;
                            foundImageURL = true;
                        }
                    } else if(ppr.equals(AppConstants.VIDEO_KEY)) {
                        tmp = e.attr(AppConstants.CONTENT_TAG);
                        if(!tmp.isEmpty()) {
                            videoURL = tmp;
                            foundVideoURL = true;
                        }
                    }
                }

                //Se ja encontramos os dois atributos entao nao e preciso continuar
                if(foundImageURL && foundVideoURL)
                    break;
            }
            return new InstagramObject(videoURL, imageURL);
        }
        return null;
    }
}
