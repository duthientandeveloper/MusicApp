package com.cse.duthientan.musicapplication.controller;

import android.content.Context;

import com.cse.duthientan.musicapplication.backend.Zingmp3API;
import com.cse.duthientan.musicapplication.model.AblumHot;
import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.model.SongOnline;

import java.util.List;

/**
 * Created by Du Thien Tan on 10/27/2015.
 */
public class Zingmp3Controller {

    private static final String URL = "";
    private Context mContext;
    private Zingmp3API api;

    public interface OnControllerErrorListener{
        void onError(String error);
    }

    public interface OnControllerAblumListener extends OnControllerErrorListener{
        void onSuccess(AblumHot ablumHot);
    }

    public interface OnControllerDataListener extends OnControllerErrorListener{
        void onSuccess(List<Song> list);
    }

    public interface OnControllerSongListener extends OnControllerErrorListener{
        void onSuccess(List<Song> list);
    }

    public Zingmp3Controller(Context context) {
        this.mContext = context;
        api =  new Zingmp3API(mContext);
    }

    public void ablum(final OnControllerAblumListener onControllerAblumListener){
        api.album(new Zingmp3API.OnBackendAlbumListener() {
            @Override
            public void onCompleted(AblumHot ablumHot) {
                onControllerAblumListener.onSuccess(ablumHot);
            }

            @Override
            public void onError(String error) {
                onControllerAblumListener.onError(error);
            }
        });
    }

    public void data (String URL, final OnControllerDataListener onControllerDataListener){
        api.Data(URL, new Zingmp3API.OnBackendDataListener() {
            @Override
            public void onCompleted(List<Song> list) {
                onControllerDataListener.onSuccess(list);
            }

            @Override
            public void onError(String error) {
                onControllerDataListener.onError(error);
            }
        });
    }


    public void song(final OnControllerSongListener onControllerSongListener){
        api.song(new Zingmp3API.OnBackendSongListener() {
            @Override
            public void onCompleted(List<Song> list) {
                onControllerSongListener.onSuccess(list);
            }

            @Override
            public void onError(String error) {
                onControllerSongListener.onError(error);
            }
        });
    }
}
