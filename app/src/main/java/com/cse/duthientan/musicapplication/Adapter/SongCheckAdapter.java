package com.cse.duthientan.musicapplication.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cse.duthientan.musicapplication.model.Song;
import com.cse.duthientan.musicapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/13/2015.
 */
public class SongCheckAdapter extends BaseAdapter {
    List<Song> songs = new ArrayList<>();
    Boolean[] mArrSongChecked ;
    public SongCheckAdapter(List<Song> songs){
        this.songs = songs;
        this.mArrSongChecked = new Boolean[songs.size()];
        Arrays.fill(mArrSongChecked,false);
    }

    public SongCheckAdapter(List<Song> allSong, List<Song> checkedSong){
        this.songs = allSong;
        this.mArrSongChecked = check(allSong,checkedSong);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return songs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();
        view=View.inflate(context, R.layout.item_song_check,null);

        TextView name =(TextView) view.findViewById(R.id.name_song);
        CheckBox checkBox = (CheckBox)view.findViewById(R.id.check);
        checkBox.setChecked(mArrSongChecked[i]);
        Song song = songs.get(i);
        name.setText(song.getName());
        if(mArrSongChecked[i])
            checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    mArrSongChecked[i] = true;
                else
                    mArrSongChecked[i] = false;
            }
        });

        return view;
    }

    public List<Song> getListSongChecked(){
        List<Song> result =  new ArrayList<>();
        for(int i=0;i<mArrSongChecked.length;i++){
            if(mArrSongChecked[i]) {
                System.out.println(songs.get(i).getName());
                result.add(songs.get(i));
            }
        }
        return result;
    }

    private Boolean[] check (List<Song> all, List<Song> check){
        Boolean[] booleans =  new Boolean[all.size()];
        Arrays.fill(booleans,false);
        for(Song s :check){

            for(int i =0;i<all.size();i++){
                if(s.getPath().equals(all.get(i).getPath()) ){
                    booleans[i] = true;
                }
            }
        }
        return booleans;
    }

}
