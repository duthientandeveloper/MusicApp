package com.cse.duthientan.musicapplication.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Du Thien Tan on 10/22/2015.
 */
public class SongManager {
    private ContentResolver mContentResolver;
    private List<Song> listSong;

    public SongManager(ContentResolver mContentResolver) {
        this.mContentResolver = mContentResolver;
        this.listSong = getSong();
    }

    public List<Song> getListSong() {
        return listSong;
    }

    public List<Song> getSong() {
        List<Song> result = new ArrayList<>();
        //
        final Cursor mCursor = mContentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA
                }, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");
        //
        if (mCursor.moveToFirst()) {
            do {
                //
                String full_name = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String name = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                String path = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //
                if (full_name.contains(".mp3") || full_name.contains(".MP3"))
                    result.add(new Song(name, path));

            } while (mCursor.moveToNext());
        }


        File yourDir = new File(Environment.getExternalStorageDirectory(),"musicapp");
        if(yourDir.listFiles()==null) return new ArrayList<>();
        for (File f : yourDir.listFiles()) {
            if (f.isFile()&&f.getName().contains(".mp3"))
            {
                result.add(new Song(f.getName().replace(".mp3",""),f.getPath()));
            }
        }
        return result;
    }
}
