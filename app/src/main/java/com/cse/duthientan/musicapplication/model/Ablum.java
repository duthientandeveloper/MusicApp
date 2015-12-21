package com.cse.duthientan.musicapplication.model;

/**
 * Created by Du Thien Tan on 11/1/2015.
 */
public class Ablum {
    public String getmName() {
        return mName;
    }

    public String getmLink() {
        return mLink;
    }

    public String getmImage() {
        return mImage;
    }

    private String mName ;
    private String mLink;
    private String mImage;

    public Ablum(String name, String link, String image){
        mImage= image;
        mLink = link;
        mName = name;
    }
}
