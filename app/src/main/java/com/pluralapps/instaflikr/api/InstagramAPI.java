package com.pluralapps.instaflikr.api;

import com.pluralapps.instaflikr.constants.AppConstants;

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
	 * Esta funcao faz parse do codigo HTML e tenta extrair os dados
	 * do video em questao
	 */
	public String parseInstagramVideoHTML(String htmlCode) {
		Document doc = Jsoup.parse(htmlCode);
		Elements elems = doc.getElementsByTag("meta");
		if(elems.size() > 0) {
			for(Element e : elems) {
				if(e.hasAttr(AppConstants.PROPERTY_TAG)) {
					String ppr = e.attr(AppConstants.PROPERTY_TAG);
					//Verificar se o valor da tag property e o que procuramos
					if(ppr.equals(AppConstants.VIDEO_KEY)) {
						//Extrair o valor do atributo "content" que contem o URL de download do video
						String videoURL = e.attr(AppConstants.CONTENT_TAG);
						if(!videoURL.isEmpty())
							return videoURL;
						
						return null;
					}
				}
			}
		}
		return null;
	}
}
