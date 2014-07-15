package com.pluralapps.instaflikr.parcelabletypes;


import java.util.HashMap;

import com.pluralapps.instaflikr.types.PhotoQuality;

import android.os.Parcel;
import android.os.Parcelable;



/**
 * Cada objeto desta classe representara uma fotografia do Flickr
 * Cada fotografia tem um ID, um utilizador que e o owner(dono), um codigo
 * "secret", um servidor onde esta alojado(representado por um codigo), uma 
 * farm e um titulo
 * A resposta tera mais parametros no entanto estes sao suficientes
 * Com estes parametros podemos tambem construir um URL que nos permitira fazer download
 * da fotografia em questao.
 * 
 * 
 * @author Andre Pinheiro
 */
public class Photo implements Parcelable {

	private int farm;
	private String ID;
	private String ownerID;
	private String secret;
	private String server;
	private String title;
	private HashMap<String, String> qualities;
	
	
	public Photo(String ID, String ownerID, String secret, String server, String title, int farm) {
		this.ID = ID;
		this.ownerID = ownerID;
		this.secret = secret;
		this.server = server;
		this.title = title;
		this.farm = farm;
		this.qualities = new HashMap<String, String>();
	}
	
	
	public int getFarm() {
		return farm;
	}
	
	
	public String getID() {
		return ID;
	}
	
	
	public String getOwnerID() {
		return ownerID;
	}
	
	
	public String getSecret() {
		return secret;
	}
	
	
	public String getServer() {
		return server;
	}
	
	
	public String getTitle() {
		return title;
	}
	
	
	public HashMap<String, String> getQualities() {
		return qualities;
	}
	
	
	public void setQualities(HashMap<String, String> qualities) {
		this.qualities = qualities;
	}
	
	
	public String getDownloadURL(PhotoQuality p) {
		String URL = "https://farm";
		URL += String.valueOf(getFarm());
		URL += ".staticflickr.com/";
		URL += getServer() + "/";
		URL += getID() + "_";
		URL += getSecret() + p.asString() + ".jpg";

		return URL;
	}
	
	
	@SuppressWarnings("unchecked")
	protected Photo(Parcel in) {
		ID = in.readString();
		ownerID = in.readString();
		secret = in.readString();
		server = in.readString();
		title = in.readString();
		farm = in.readInt();
		qualities = in.readHashMap(String.class.getClassLoader());
	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ID);
		dest.writeString(ownerID);
		dest.writeString(secret);
		dest.writeString(server);
		dest.writeString(title);
		dest.writeInt(farm);
		dest.writeMap(qualities);
	}
	
	
	
	public static final Creator<Photo> CREATOR = new Creator<Photo>() {
		
		@Override
		public Photo[] newArray(int size) {
			return new Photo[size];
		}
		
		
		@Override
		public Photo createFromParcel(Parcel source) {
			return new Photo(source);
		}
	};

}
