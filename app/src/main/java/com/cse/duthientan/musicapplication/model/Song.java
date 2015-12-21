package com.cse.duthientan.musicapplication.model;

/**
 * Created by Du Thien Tan on 10/13/2015.
 */
public class Song {

    private String name;
    private String path;

    public Song(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

}
