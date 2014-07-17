package com.pluralapps.instaflikr.constants;

public class AppConstants {
	//public static final String SECRET_KEY = "dc979942fd33a119";
	public static final String APP_KEY = "d55c19eb95f25bda6e1f2bc8308e310f";
	public static final String DEVELOPER_ID = "106133045";
	public static final String APP_ID = "207111580";
	
	
	/**
	 * Constantes do codigo JSON do Flickr
	 */
	public static final String PHOTO_JSON = "photo";
	public static final String PHOTOS_JSON = "photos";
	public static final String PHOTO_ID = "id";
	public static final String PHOTO_OWNER = "owner";
	public static final String PHOTO_SECRET = "secret";
	public static final String PHOTO_SERVER = "server";
	public static final String PHOTO_FARM = "farm";
	public static final String PHOTO_TITLE = "title";
	public static final String NSID = "nsid";
	public static final String STAT = "stat";
	public static final String STAT_OK = "ok";
	public static final String USER = "user";
	public static final String SIZES = "sizes";
	public static final String SIZE = "size";
	public static final String LABEL = "label";
	public static final String SOURCE = "source";
	public static final String SMALL_320 = "Small 320";
	public static final String ORIGINAL = "Original";
	public static final String MEDIUM = "Medium";
	public static final String LARGE = "Large";



    /**
     * Constantes do codigo HTML do Instagram
     */
    public static final String META_TAG = "meta";
    public static final String PROPERTY_TAG = "property";
    public static final String VIDEO_KEY = "og:video";
    public static final String IMAGE_KEY = "og:image";
    public static final String CONTENT_TAG = "content";



    /**
     * Constantes utilizadas para passar dados entre os fragmentos
     */
    public static final String VIDEO_PREVIEW_URL_KEY = "url_key";
    public static final String PHOTO_OBJECT_KEY = "photo_key";
    public static final String BITMAP_TO_MANIPULATE_KEY = "bitmapToManipulate";



    /**
     * Constantes relacionadas com expressoes regulares
     */
    public static final String EMAIL_REGEX = "[A-Za-z_.]+@\\b([a-zA-Z]+.*?).[A-Za-z]+";
    public static final String INSTAGRAM_REGEX = "https?:\\/\\/instagram.com\\/p\\/([a-zA-Z0-9_-]{1,})\\/?";
}
