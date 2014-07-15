package com.pluralapps.instaflikr.parcelabletypes;


import android.os.Parcel;
import android.os.Parcelable;

import com.pluralapps.instaflikr.types.MediaType;

public class InstagramObject implements Parcelable {

    private String videoURL, imageURL;
    private MediaType mediaType;


    public InstagramObject(String videoURL, String imageURL) {
        this.videoURL = videoURL;
        this.imageURL = imageURL;

        //Atribuir automaticamente um tipo de media a este objeto
        if(videoURL != null) {
            if(imageURL != null)
                mediaType = MediaType.VIDEO_IMAGE;
            else
                mediaType = MediaType.VIDEO;
        } else
            if(imageURL != null)
                mediaType = MediaType.IMAGE;
    }



    public String getVideoURL() {
        return videoURL;
    }


    public String getImageURL() {
        return imageURL;
    }


    public MediaType getMediaType() {
        return mediaType;
    }


    @Override
    public int describeContents() {
        return 0;
    }


    @SuppressWarnings("unchecked")
    protected InstagramObject(Parcel in) {
        videoURL = in.readString();
        imageURL = in.readString();
        mediaType = (MediaType) in.readSerializable();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageURL);
        dest.writeString(videoURL);
        dest.writeSerializable(mediaType);
    }



    public static final Creator<InstagramObject> CREATOR = new Creator<InstagramObject>() {

        @Override
        public InstagramObject createFromParcel(Parcel source) {
            return new InstagramObject(source);
        }

        @Override
        public InstagramObject[] newArray(int size) {
            return new InstagramObject[size];
        }
    };
}
