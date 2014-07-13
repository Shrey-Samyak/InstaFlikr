package com.pluralapps.instaflikr.types;



public enum PhotoQuality {

    LARGE_SQUARE("_q");
    private final String asString;


    private PhotoQuality(String asString) {
        this.asString = asString;
    }


    public String asString() {
        return asString;
    }
}
