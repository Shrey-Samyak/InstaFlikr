package com.pluralapps.instaflikr.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pluralapps.instaflikr.Photo;
import com.pluralapps.instaflikr.constants.AppConstants;

public class FlickrAPI {

	private static String BASE_URL = "https://api.flickr.com/services/rest/?method=";
	private static String DATA_TYPE = "json";
	//Utilizado para manter o padrao de desenho singleton
	private static FlickrAPI INSTANCE = null;
	
	
	private FlickrAPI() {
	}
	
	
	public static FlickrAPI getInstance() {
		if(INSTANCE == null)
			INSTANCE = new FlickrAPI();
		
		return INSTANCE;
	}
	
	
	
	/**
	 * Esta funcao constroi o URL responsavel por retirar as ultimas fotos
	 * publicas carregadas para o Flickr
	 * 
	 * Os dados sao retornados em JSON.
	 */
	public String getRecentPhotosURL() {
		return BASE_URL + "flickr.photos.getRecent&api_key=" + AppConstants.APP_KEY + "&format=" + DATA_TYPE + "&nojsoncallback=1";
	}
	
	
	
	/**
	 * Esta funcao constroi o URL responsavel por retirar as fotos de uma 
	 * determinada String de pesquisa
	 * 
	 * Os dados sao retornados em JSON
	 */
	public String getSearchPhotosURL(String text) {
		return BASE_URL + "flickr.photos.search&api_key=" + AppConstants.APP_KEY + "&text=" + text + "&format=" + DATA_TYPE + "&nojsoncallback=1";
	}
	
	
	
	/**
	 * Esta funcao constroi o URL responsavel por retirar os dados de um determinado endereco de 
	 * email. 
	 */
	public String getSearchUserURL(String email) {
		return BASE_URL + "flickr.people.findByEmail&api_key=" + AppConstants.APP_KEY + "&find_email=" + email + "&format=" + DATA_TYPE + "&nojsoncallback=1";
	}
	
	
	
	/**
	 * Esta funcao constroi o URL responsavel por extrair as fotos de um determinado user com um dado
	 * NSID
	 */
	public String getSearchByNSIDURL(String NSID) {
		return BASE_URL + "flickr.photos.search&api_key=" + AppConstants.APP_KEY + "&user_id=" + NSID + "&format=" + DATA_TYPE + "&nojsoncallback=1";
	}
	
	
	
	/**
	 * Esta funcao constroi o URL responsavel por extrair todos os tamanhos disponiveis para
	 * uma determinada fotografia
	 */
	public String getPhotoInfoURL(String photoID) {
		return BASE_URL + "flickr.photos.getSizes&api_key=" + AppConstants.APP_KEY + "&photo_id=" + 
				photoID + "&format=" + DATA_TYPE + "&nojsoncallback=1"; 
	}
	
	
	
	/**
	 * Esta funcao retorna uma lista das fotografias mais recentes que
	 * foram carregadas para o Flickr.
	 */
	public ArrayList<Photo> getPhotos(String jsonCode) {
		ArrayList<Photo> photosArray = new ArrayList<Photo>();
		try {
			JSONObject mainObj = new JSONObject(jsonCode);
			JSONObject photosObj = mainObj.getJSONObject(AppConstants.PHOTOS_JSON);
			JSONArray jsonPhotos = photosObj.getJSONArray(AppConstants.PHOTO_JSON);
			
			//Iterar sobre todos os objetos e extrair os dados de cada um para o ArrayList photosArray
			for(int i = 0; i < jsonPhotos.length(); i++) {
				JSONObject tmpPhoto = jsonPhotos.getJSONObject(i);
				Photo photo = getPhotoFromJSON(tmpPhoto);
				
				if(photo != null) 
					photosArray.add(photo);
			}
			
		} catch (JSONException e) {
			//Nao deve acontecer. Ocorreu um erro ao fazer parse da String
			e.printStackTrace();
		}
		
		return photosArray;
	}
	
	
	
	/**
	 * Esta funcao extrai uma fotografia do codigo JSON
	 * E um helper da funcao getRecentPhotos(...)
	 */
	public Photo getPhotoFromJSON(JSONObject object) {
		int farm;
		String ID;
		String ownerID;
		String secret;
		String server;
		String title;
		
		try {
			ID = object.getString(AppConstants.PHOTO_ID);
			ownerID = object.getString(AppConstants.PHOTO_OWNER);
			secret = object.getString(AppConstants.PHOTO_SECRET);
			server = object.getString(AppConstants.PHOTO_SERVER);
			farm = object.getInt(AppConstants.PHOTO_FARM);
			title = object.getString(AppConstants.PHOTO_TITLE);
			
			Photo photo = new Photo(ID, ownerID, secret, server, title, farm);
			return photo;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * Funcao que extrai o NSID de um utilizador atraves do codigo JSON.
	 * Se nao for possivel extrair entao retorna null
	 */
	public String extractNSID(String jsonCode) {
		String stat = null;
		
		try {
			JSONObject codeObj = new JSONObject(jsonCode);
			
			stat = codeObj.getString(AppConstants.STAT);
			//Se esta conta nao existir entao retornar null
			if(!stat.equals(AppConstants.STAT_OK))
				return null;
			
			//Neste ponto sabemos que a conta de utilizador existe, agora temos que extrair o email
			JSONObject userObj = codeObj.getJSONObject(AppConstants.USER);
			//Extrair o NSID do utilizador aqui
			return userObj.getString(AppConstants.NSID);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * Esta funcao extrai todas as qualidades disponiveis de uma foto
	 * O valor e retornado na forma de um HashMap
	 */
	public HashMap<String, String> getPhotoSizes(String response) {
		String stat = null;
		HashMap<String, String> sizes = new HashMap<String, String>();
		
		try {
			JSONObject obj = new JSONObject(response);
			stat = obj.getString(AppConstants.STAT);
			if(!stat.equals(AppConstants.STAT_OK))
				return null;
			
			JSONObject sizesObj = obj.getJSONObject(AppConstants.SIZES);
			JSONArray sizeArray = sizesObj.getJSONArray(AppConstants.SIZE);
			
			for(int i = 0; i < sizeArray.length(); i++) {
				JSONObject o = sizeArray.getJSONObject(i);
				String label = o.getString(AppConstants.LABEL);
				String source = o.getString(AppConstants.SOURCE);
				sizes.put(label, source);
			}
			
			return sizes;
		} catch(JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
}
