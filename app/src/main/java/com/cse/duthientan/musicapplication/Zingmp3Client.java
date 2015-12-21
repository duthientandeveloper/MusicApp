package com.cse.duthientan.musicapplication;

import android.content.Context;

import com.cse.duthientan.musicapplication.controller.Zingmp3Controller;
import com.cse.duthientan.musicapplication.model.AblumHot;
import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.model.SongOnline;

import java.util.List;

/**
 * Created by Du Thien Tan on 10/27/2015.
 */
public class Zingmp3Client {
    private Context mContext;
    private Zingmp3Controller controller;

    public interface OnErrorListener{
        void onError(String error);
    }

    public interface OnAblumListerner extends OnErrorListener{
        void onSuccess(AblumHot ablumHot);
    }

    public interface OnDataListener extends OnErrorListener{
        void onSuccess(List<Song> list);
    }
    public interface OnSongListener extends OnErrorListener{
        void onSuccess(List<Song> list);
    }

    public Zingmp3Client(Context context){
        mContext = context;
        controller = new Zingmp3Controller(context);
    }

    public void ablum( final OnAblumListerner onAblumListerner){
        controller.ablum(new Zingmp3Controller.OnControllerAblumListener() {
            @Override
            public void onSuccess(AblumHot ablumHot) {
                onAblumListerner.onSuccess(ablumHot);
            }

            @Override
            public void onError(String error) {
                onAblumListerner.onError(error);
            }
        });
    }

    public void data(String URL, final OnDataListener onDataListener){
        controller.data(URL, new Zingmp3Controller.OnControllerDataListener() {
            @Override
            public void onSuccess(List<Song> list) {
                onDataListener.onSuccess(list);
            }

            @Override
            public void onError(String error) {
                onDataListener.onError(error);
            }
        });
    }

    public void song(final OnSongListener onSongListener){
        controller.song(new Zingmp3Controller.OnControllerSongListener() {
            @Override
            public void onSuccess(List<Song> list) {
                onSongListener.onSuccess(list);
            }

            @Override
            public void onError(String error) {
                onSongListener.onError(error);
            }
        });
    }
}
