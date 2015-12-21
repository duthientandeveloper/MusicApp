package com.cse.duthientan.musicapplication.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/13/2015.
 */
public class SongAdapter extends BaseAdapter {
    List<Song> mSongs = new ArrayList<>();

    public SongAdapter(List<Song> mSongs){
       this.mSongs = mSongs;
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public Object getItem(int i) {
        return mSongs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Context context = viewGroup.getContext();
        view=View.inflate(context, R.layout.item_song,null);

        TextView name_song =(TextView) view.findViewById(R.id.name_song);

        name_song.setText(mSongs.get(i).getName());
        return view;
    }
}
