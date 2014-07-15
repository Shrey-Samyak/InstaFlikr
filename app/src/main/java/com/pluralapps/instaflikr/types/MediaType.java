package com.pluralapps.instaflikr.types;


public enum MediaType {

    VIDEO("video"),
    IMAGE("image"),
    VIDEO_IMAGE("video_image");
    private final String asString;


    public String asString() {
        return asString;
    }


    private MediaType(String asString) {
        this.asString = asString;
    }
}
