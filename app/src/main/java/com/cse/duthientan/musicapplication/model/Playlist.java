package com.cse.duthientan.musicapplication.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/13/2015.
 */
public class Playlist {
    private int mId;

    public List<Song> getmListSong() {
        return mListSong;
    }

    private List<Song> mListSong = new ArrayList<>();
    private String mNamePlaylist;
    private String mPathImage;

    public String getmDecriptionPlaylist() {
        return mDecriptionPlaylist;
    }

    public String getmPathImage() {
        return mPathImage;
    }

    public int getmId() {
        return mId;
    }

    private String mDecriptionPlaylist;

    public String getmNamePlaylist() {
        return mNamePlaylist;
    }

    public Playlist(int id, String name,String path, String decrip,List<Song> list){
        this.mId = id;
        this.mNamePlaylist = name;
        this.mPathImage = path;
        this.mDecriptionPlaylist = decrip;
        this.mListSong = list;
    }


}
