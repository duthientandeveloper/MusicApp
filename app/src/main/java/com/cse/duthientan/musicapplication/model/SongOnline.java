package com.cse.duthientan.musicapplication.model;

/**
 * Created by Du Thien Tan on 10/27/2015.
 */
public class SongOnline {
    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    private String name;
    private String link;

    public SongOnline(String name, String link){
        this.name= name;
        this.link = link;
    }

}
